/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (IntervalParser.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Time4J. If not, see <http://www.gnu.org/licenses/>.
 * -----------------------------------------------------------------------
 */

package net.time4j.range;

import net.time4j.engine.AttributeQuery;
import net.time4j.engine.Calendrical;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.Temporal;
import net.time4j.engine.TimeLine;

import net.time4j.format.ChronoFormatter;
import net.time4j.format.ChronoParser;
import net.time4j.format.ParseLog;

/**
 * <p>Parses intervals based on component formatters which are applied on start
 * and end boundaries. </p>
 *
 * @author  Meno Hochschild
 * @param   <T>  generic temporal type
 * @since   1.3
 */
/*[deutsch]
 * <p>Interpretiert Intervalle basierend auf Zeitformatierern, die auf die
 * Start- oder Endkomponenten angewandt werden. </p>
 *
 * @author  Meno Hochschild
 * @param   <T>  generic temporal type
 * @since   1.3
 */
public final class IntervalParser<T extends ChronoEntity<T> & Temporal<? super T>>
	implements ChronoParser<ChronoInterval<T>> {

	private final IntervalFactory<T> factory;
	private final ChronoFormatter<T> startFormat;
	private final ChronoFormatter<T> endFormat;
	private final BracketPolicy policy;

	private IntervalParser(
		IntervalFactory<T> factory,
		ChronoFormatter<T> startFormat,
		ChronoFormatter<T> endFormat,
		BracketPolicy	   policy
	) {
		super();

		if (factory == null) {
			throw new NullPointerException("Missing interval factory.");
		} else if (startFormat == null) {
			throw new NullPointerException("Missing start component formatter.");
		} else if (endFormat == null) {
			throw new NullPointerException("Missing end component formatter.");
		} else if (policy == null) {
			throw new NullPointerException("Missing bracket policy.");
		}

		this.factory = factory;
		this.startFormat = startFormat;
		this.endFormat = endFormat;
		this.policy = policy;

	}

	/**
	 * @param   format
	 * @return  new interval parser
	 */
	public static <T extends ChronoEntity<T> & Temporal<? super T>> IntervalParser<T> of(
		ChronoFormatter<T> format
	) {

		return of(format, BracketPolicy.SHOW_WHEN_NON_STANDARD);

	}

	/**
	 * @param   format
	 * @param   policy
	 * @return  new interval parser
	 */
	@SuppressWarnings("unchecked")
	public static <T extends ChronoEntity<T> & Temporal<? super T>> IntervalParser<T> of(
		ChronoFormatter<T> format,
		BracketPolicy	   policy
	) {

		Object chronology = format.getChronology();
		TimeLine<T> timeline;

		if (chronology instanceof TimeLine) {
			timeline = (TimeLine<T>) chronology;
		} else {
			throw new IllegalArgumentException("Formatter without a timeline-chronology.");
		}

		IntervalFactory<T> factory = new GenericIntervalFactory<T>(timeline);
		return of(factory, format, format, policy);

	}

	static <T extends ChronoEntity<T> & Temporal<? super T>> IntervalParser<T> of(
		IntervalFactory<T> factory,
		ChronoFormatter<T> startFormat,
		ChronoFormatter<T> endFormat,
		BracketPolicy	   policy
	) {

		return new IntervalParser<T>(factory, startFormat, endFormat, policy);

	}

	@Override
	public ChronoInterval<T> parse(CharSequence text, ParseLog status, AttributeQuery attributes) {

		// initialization phase
		IntervalEdge left = IntervalEdge.CLOSED;
		IntervalEdge right =
			Calendrical.class.isAssignableFrom(this.startFormat.getChronology().getChronoType())
			? IntervalEdge.CLOSED : IntervalEdge.OPEN;
		T t1 = null;
		T t2 = null;

		int start = status.getPosition();
		int len = text.length();
		int pos = start;

		if (pos >= len) {
			status.setError(pos, "End of text reached.");
			return null;
		}

		// starting boundary
		char c = text.charAt(pos);
		boolean leftVisible = ((c == '[') || (c == '('));

		if (leftVisible) {
			if (this.policy == BracketPolicy.SHOW_NEVER) {
				status.setError(pos, "Boundary bracket not allowed: " + c);
			} else if (c == '(') {
				left = IntervalEdge.OPEN;
			}
		} else if (this.policy == BracketPolicy.SHOW_ALWAYS) {
			status.setError(pos, "Missing start boundary bracket.");
		}

		if (status.isError()) {
			return null;
		}

		pos++;

		if (pos >= len) {
			status.setError(pos, "End of text reached.");
			return null;
		}

		// start component and solidus
		ChronoEntity<?> parsed = status.getRawValues();
		c = text.charAt(pos);

		if (c == 'P') {
			int index = pos;
			int solidus = -1;
			while (++index < len) {
				if (text.charAt(index) == '/') {
					solidus = index;
					break;
				}
			}
			if (solidus == -1) {
				status.setError(pos, "Solidus char separating start and end boundaries expected.");
				return null;
			}
			parsed.with(PeriodElement.START_PERIOD, text.subSequence(pos, solidus).toString());
			pos = solidus + 1;
			status.setPosition(pos);
		} else if ((c == '-') && (pos + 1 < len) && (text.charAt(pos + 1) == '\u221E')) {
			if ((left == IntervalEdge.CLOSED) && leftVisible) {
				status.setError(pos, "Open boundary expected.");
				return null;
			}
			left = IntervalEdge.OPEN;
			parsed.with(InfiniteElement.START_INFINITE, Boolean.TRUE);
			pos += 2;
			if ((pos >= len) || (text.charAt(pos) != '/')) {
				status.setError(pos, "Solidus char separating start and end boundaries expected.");
				return null;
			}
			pos++;
			status.setPosition(pos);
		} else {
			ParseLog plog = new ParseLog(pos);
			t1 = this.startFormat.parse(text, plog, attributes);
			if (t1 == null || plog.isError()) {
				status.setError(pos, plog.getErrorMessage());
				return null;
			}
			parsed.with(
				TemporalElement.start(this.startFormat.getChronology().getChronoType()),
				t1
			);
			pos = plog.getPosition();
			if ((pos >= len) || (text.charAt(pos) != '/')) {
				status.setError(pos, "Solidus char separating start and end boundaries expected.");
				return null;
			}
			pos++;
			status.setPosition(pos);
		}

		// end component
		// TODO: impl

		return null;

	}

}
