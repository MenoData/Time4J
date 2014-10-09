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

import net.time4j.engine.AttributeKey;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.Calendrical;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.Temporal;
import net.time4j.engine.TimeLine;
import net.time4j.format.Attributes;
import net.time4j.format.ChronoFormatter;
import net.time4j.format.ChronoParser;
import net.time4j.format.ParseLog;

import static net.time4j.range.IntervalEdge.CLOSED;
import static net.time4j.range.IntervalEdge.OPEN;


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
public final class IntervalParser
    <T extends ChronoEntity<T> & Temporal<? super T>>
	implements ChronoParser<ChronoInterval<T>> {

    //~ Instanzvariablen --------------------------------------------------

	private final IntervalFactory<T> factory;
	private final ChronoFormatter<T> startFormat;
	private final ChronoFormatter<T> endFormat;
	private final BracketPolicy policy;

    //~ Konstruktoren -----------------------------------------------------

	private IntervalParser(
		IntervalFactory<T> factory,
		ChronoFormatter<T> startFormat,
		ChronoFormatter<T> endFormat,
		BracketPolicy	   policy
	) {
		super();

		if (policy == null) {
			throw new NullPointerException("Missing bracket policy.");
		}

		this.factory = factory;
		this.startFormat = startFormat;
		this.endFormat = endFormat;
		this.policy = policy;

	}

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates an interval parser. </p>
     *
     * <p>Equivalent to
     * {@code of(format, BracketPolicy.SHOW_WHEN_NON_STANDARD}. </p>
     *
     * @param   <T> generic temporal type
     * @param   format     formatter for start and end component
     * @return  new interval parser
     * @throws  IllegalArgumentException if the format does not refer to
     *          a chronology based on a timeline
     * @since   1.3
     * @see     BracketPolicy#SHOW_WHEN_NON_STANDARD
     */
    /*[deutsch]
     * <p>Erzeugt einen Intervallinterpretierer. </p>
     *
     * <p>&Auml;quivalent zu
     * {@code of(format, BracketPolicy.SHOW_WHEN_NON_STANDARD}. </p>
     *
     * @param   <T> generic temporal type
     * @param   format     formatter for start and end component
     * @return  new interval parser
     * @throws  IllegalArgumentException if the format does not refer to
     *          a chronology based on a timeline
     * @since   1.3
     * @see     BracketPolicy#SHOW_WHEN_NON_STANDARD
     */
	public static
    <T extends ChronoEntity<T> & Temporal<? super T>> IntervalParser<T> of(
		ChronoFormatter<T> format
	) {

		return of(format, BracketPolicy.SHOW_WHEN_NON_STANDARD);

	}

    /**
     * <p>Creates an interval parser. </p>
     *
     * @param   <T> generic temporal type
     * @param   format     formatter for start and end component
     * @param   policy     bracket policy
     * @return  new interval parser
     * @throws  IllegalArgumentException if the format does not refer to
     *          a chronology based on a timeline
     * @since   1.3
     */
    /*[deutsch]
     * <p>Erzeugt einen Intervallinterpretierer. </p>
     *
     * @param   <T> generic temporal type
     * @param   format     formatter for start and end component
     * @param   policy     bracket policy
     * @return  new interval parser
     * @throws  IllegalArgumentException if the format does not refer to
     *          a chronology based on a timeline
     * @since   1.3
     */
	@SuppressWarnings("unchecked")
	public static
    <T extends ChronoEntity<T> & Temporal<? super T>> IntervalParser<T> of(
		ChronoFormatter<T> format,
		BracketPolicy	   policy
	) {

		Object chronology = format.getChronology(); // NPE-check
		TimeLine<T> timeline;

		if (chronology instanceof TimeLine) {
			timeline = (TimeLine<T>) chronology;
		} else {
			throw new IllegalArgumentException(
                "Formatter without a timeline-chronology.");
		}

		IntervalFactory<T> factory = new GenericIntervalFactory<T>(timeline);
		return of(factory, format, format, policy);

	}

    /**
     * <p>Interne Methode, die auch von ISO-Intervallen aufgerufen wird. </p>
     *
     * @param   <T> generic temporal type
     * @param   factory         interval factory
     * @param   startFormat     formatter for lower interval boundary
     * @param   endFormat       formatter for upper interval boundary
     * @param   policy          bracket policy
     * @return  new interval parser
     * @since   1.3
     */
	static
    <T extends ChronoEntity<T> & Temporal<? super T>> IntervalParser<T> of(
		IntervalFactory<T> factory,
		ChronoFormatter<T> startFormat,
		ChronoFormatter<T> endFormat,
		BracketPolicy	   policy
	) {

		return new IntervalParser<T>(factory, startFormat, endFormat, policy);

	}

	@Override
	public ChronoInterval<T> parse(
        CharSequence text,
        ParseLog status,
        final AttributeQuery attributes
    ) {

		// initialization phase
		int start = status.getPosition();
		int len = text.length();
		int pos = start;

		if (pos >= len) {
            throw new IndexOutOfBoundsException(
                "[" + pos + "]: " + text.toString());
        }

        AttributeQuery attrs = attributes;
        boolean allowTrailingChars =
            attributes.get(
                Attributes.TRAILING_CHARACTERS,
                Boolean.FALSE
            ).booleanValue();

        if (!allowTrailingChars) {
            attrs =
                new AttributeQuery() {
                    @Override
                    public boolean contains(AttributeKey<?> key) {
                        if (key.equals(Attributes.TRAILING_CHARACTERS)) {
                            return true;
                        }
                        return attributes.contains(key);
                    }
                    @Override
                    public <A> A get(AttributeKey<A> key) {
                        if (key.equals(Attributes.TRAILING_CHARACTERS)) {
                            return key.type().cast(Boolean.TRUE);
                        }
                        return attributes.get(key);
                    }
                    @Override
                    public <A> A get(AttributeKey<A> key, A defaultValue) {
                        if (key.equals(Attributes.TRAILING_CHARACTERS)) {
                            return key.type().cast(Boolean.TRUE);
                        }
                        return attributes.get(key, defaultValue);
                    }
                };
        }

        Class<T> type = this.startFormat.getChronology().getChronoType();
		IntervalEdge left = CLOSED;
		IntervalEdge right =
			Calendrical.class.isAssignableFrom(type) ? CLOSED : OPEN;
		T t1 = null;
		T t2 = null;
        Boundary<T> lower = null;
        Boundary<T> upper = null;

		// starting boundary
		char c = text.charAt(pos);
		boolean leftVisible = ((c == '[') || (c == '('));

		if (leftVisible) {
			if (this.policy == BracketPolicy.SHOW_NEVER) {
				status.setError(
                    pos,
                    "Illegal start boundary due to bracket policy: " + c);
			} else if (c == '(') {
				left = OPEN;
			}
            pos++;
		} else if (this.policy == BracketPolicy.SHOW_ALWAYS) {
			status.setError(pos, "Missing start boundary bracket.");
		}

		if (status.isError()) {
			return null;
		} else if (pos >= len) {
			status.setError(
                pos,
                "Missing interval start component, end of text reached.");
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
                return solidusError(status, pos);
			}
            String period = text.subSequence(pos, solidus).toString();
			parsed.with(PeriodElement.START_PERIOD, period);
			pos = solidus + 1;
		} else if (
            (c == '-')
            && (pos + 1 < len)
            && (text.charAt(pos + 1) == '\u221E')
        ) {
			if ((left == CLOSED) && leftVisible) {
				status.setError(pos - 1, "Open boundary expected.");
				return null;
			}
			left = OPEN;
			parsed.with(InfiniteElement.START_INFINITE, Boolean.TRUE);
            lower = Boundary.infinitePast();
			pos += 2;
			if ((pos >= len) || (text.charAt(pos) != '/')) {
                return solidusError(status, pos);
			}
			pos++;
		} else {
			ParseLog plog = new ParseLog(pos);
			t1 = this.startFormat.parse(text, plog, attrs);
			if (t1 == null || plog.isError()) {
				status.setError(pos, plog.getErrorMessage());
				return null;
			}
			parsed.with(TemporalElement.start(type), t1);
            lower = Boundary.of(left, t1);
			pos = plog.getPosition();
			if ((pos >= len) || (text.charAt(pos) != '/')) {
                return solidusError(status, pos);
			}
			pos++;
		}

		// end component after solidus
		if (pos >= len) {
			status.setError(
                pos,
                "Missing interval end component, end of text reached.");
			return null;
		}

		c = text.charAt(pos);

		if (c == 'P') {
            int endIndex = len;
            char test = text.charAt(endIndex - 1);
            if ((test == ']') || (test == ')')) {
                endIndex--;
            }
            String period = text.subSequence(pos, endIndex).toString();
			parsed.with(PeriodElement.END_PERIOD, period);
			pos = endIndex;
		} else if (
            (c == '+')
            && (pos + 1 < len)
            && (text.charAt(pos + 1) == '\u221E')
        ) {
            if (
                (pos + 2 < len)
                && (text.charAt(pos + 2) == ']')
                && (this.policy != BracketPolicy.SHOW_NEVER)
            ) {
                status.setError(pos + 2, "Open boundary expected.");
                return null;
            }
			right = OPEN;
			parsed.with(InfiniteElement.END_INFINITE, Boolean.TRUE);
            upper = Boundary.infiniteFuture();
			pos += 2;
		} else {
            // TODO: default-Werte vom Start übernehmen (wenn t1 != null)
			ParseLog plog = new ParseLog(pos);
			t2 = this.endFormat.parse(text, plog, attrs);
			if (t2 == null || plog.isError()) {
				status.setError(pos, plog.getErrorMessage());
				return null;
			}
			parsed.with(TemporalElement.end(type), t2);
            upper = Boundary.of(right, t2);
			pos = plog.getPosition();
		}

		// ending boundary
        if (pos >= len) {
            if (this.policy == BracketPolicy.SHOW_ALWAYS) {
                status.setError(pos, "Missing end boundary bracket.");
            }
        } else {
            c = text.charAt(pos);
            if ((c == ']') || (c == ')')) {
                if (this.policy == BracketPolicy.SHOW_NEVER) {
                    status.setError(
                        pos,
                        "Illegal end boundary due to bracket policy: " + c);
                } else {
                    right = ((c == ']') ? CLOSED : OPEN);
                    pos++;
                }
            } else if (this.policy == BracketPolicy.SHOW_ALWAYS) {
                status.setError(pos, "Missing end boundary bracket.");
            }
        }

        if (status.isError()) {
            return null;
        }

        if (
            (pos < len)
            && !allowTrailingChars
        ) {
            String suffix;

            if (len - pos <= 10) {
                suffix = text.subSequence(pos, len).toString();
            } else {
                suffix = text.subSequence(pos, pos + 10).toString() + "...";
            }

            status.setError(pos, "Unparsed trailing characters: " + suffix);
            return null;
        }

        // special case for p-strings (ISO-support)
        if (lower == null) {
            // TODO: impl
        }

        if (upper == null) {
            // TODO: impl
        }

        // create and return interval
        status.setPosition(pos);
		return this.factory.between(lower, upper);

	}


    private static <R> R solidusError(
        ParseLog status,
        int pos
    ) {

        status.setError(
            pos,
            "Solidus char separating start and end boundaries expected.");
        return null;

    }

}
