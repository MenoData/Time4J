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
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoMerger;
import net.time4j.engine.Temporal;
import net.time4j.format.Attributes;
import net.time4j.format.ChronoFormatter;
import net.time4j.format.ChronoParser;
import net.time4j.format.ParseLog;
import net.time4j.tz.TZID;

import java.text.ParseException;

import static net.time4j.format.Leniency.SMART;
import static net.time4j.range.IntervalEdge.CLOSED;
import static net.time4j.range.IntervalEdge.OPEN;


/**
 * <p>Interpretiert Intervalle basierend auf Zeitformatierern, die auf die
 * Start- oder Endkomponenten angewandt werden. </p>
 *
 * @author  Meno Hochschild
 * @param   <T> generic temporal type
 * @param   <I> generic interval type
 * @since   2.0
 */
final class IntervalParser
    <T extends Temporal<? super T>, I extends IsoInterval<T, I>>
    implements ChronoParser<I> {

    //~ Instanzvariablen --------------------------------------------------

	private final IntervalFactory<T, I> factory;
	private final ChronoParser<T> startFormat;
	private final ChronoParser<T> endFormat;
	private final BracketPolicy policy;
    private final ChronoMerger<T> merger;

    //~ Konstruktoren -----------------------------------------------------

	private IntervalParser(
		IntervalFactory<T, I> factory,
		ChronoParser<T> startFormat,
		ChronoParser<T> endFormat,
		BracketPolicy policy,
        ChronoMerger<T> merger // optional
	) {
		super();

		if (policy == null) {
			throw new NullPointerException("Missing bracket policy.");
        }

        this.factory = factory;
		this.startFormat = startFormat;
		this.endFormat = endFormat;
		this.policy = policy;
        this.merger = merger;

	}

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Fabrikmethode f&uuml;r allgemeine Format-Objekte. </p>
     *
     * @param   <T> generic temporal type
     * @param   <I> generic interval type
     * @param   factory         interval factory
     * @param   parser          boundary parser
     * @param   policy          bracket policy
     * @return  new interval parser
     * @since   2.0
     */
	static <T extends Temporal<? super T>, I extends IsoInterval<T, I>>
    IntervalParser<T, I> of(
		IntervalFactory<T, I> factory,
		ChronoParser<T> parser,
		BracketPolicy policy
	) {

		if (parser == null) {
			throw new NullPointerException("Missing boundary parser.");
        }

		return new IntervalParser<T, I>(factory, parser, parser, policy, null);

	}

    /**
     * <p>Fabrikmethode im ISO-Modus. </p>
     *
     * @param   <T> generic temporal type
     * @param   <I> generic interval type
     * @param   factory         interval factory
     * @param   startFormat     formatter for lower interval boundary
     * @param   endFormat       formatter for upper interval boundary
     * @param   policy          bracket policy
     * @return  new interval parser
     * @since   2.0
     */
	static <T extends Temporal<? super T>, I extends IsoInterval<T, I>>
    IntervalParser<T, I> of(
		IntervalFactory<T, I> factory,
		ChronoParser<T> startFormat,
		ChronoParser<T> endFormat,
		BracketPolicy policy,
        ChronoMerger<T> merger
	) {

		return new IntervalParser<T, I>(
            factory,
            startFormat,
            endFormat,
            policy,
            merger);

	}

    /**
     * <p>Interpretiert den angegebenen Text als chronologisches Intervall. </p>
     *
     * @param   text        text to be parsed
     * @return  parse result
     * @throws  IndexOutOfBoundsException if the text is empty
     * @throws  ParseException if the text is not parseable
     */
	I parse(String text) throws ParseException {

        ParseLog plog = new ParseLog();
        I ret =
            this.parse(
                text,
                plog,
                IsoInterval.extractDefaultAttributes(this.startFormat));

        if (
            (ret == null)
            || plog.isError()
        ) {
            throw new ParseException(
                plog.getErrorMessage(),
                plog.getErrorIndex());
        }

        return ret;

    }

	@Override
	public I parse(
        CharSequence text,
        ParseLog status,
        AttributeQuery attributes
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
            attrs = new Wrapper(attributes);
        }

		IntervalEdge left = CLOSED;
		IntervalEdge right = this.factory.isCalendrical() ? CLOSED : OPEN;
		T t1 = null;
		T t2 = null;
        Boundary<T> lower = null;
        Boundary<T> upper = null;
        int posLower = -1;
        int posUpper = -1;
        ParseLog lowerLog = null;
        ParseLog upperLog = null;
        String period = null;

		// starting boundary
		char c = text.charAt(pos);
		boolean leftVisible = ((c == '[') || (c == '('));
        boolean rightVisible = false;

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
		c = text.charAt(pos);

		if (c == 'P') {
            posLower = pos;
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
            period = text.subSequence(pos, solidus).toString();
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
            lower = Boundary.infinitePast();
			pos += 2;
			if ((pos >= len) || (text.charAt(pos) != '/')) {
                return solidusError(status, pos);
			}
			pos++;
		} else {
			lowerLog = new ParseLog(pos);
			t1 = this.startFormat.parse(text, lowerLog, attrs);
			if (t1 == null || lowerLog.isError()) {
				status.setError(pos, lowerLog.getErrorMessage());
				return null;
			}
            lower = Boundary.of(left, t1);
			pos = lowerLog.getPosition();
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
            if (t1 == null) {
                status.setError(
                    pos,
                    "Cannot process end period without start time.");
                return null;
            }
            posUpper = pos;
            int endIndex = len;
            char test = text.charAt(endIndex - 1);
            if ((test == ']') || (test == ')')) {
                endIndex--;
            }
            period = text.subSequence(pos, endIndex).toString();
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
            upper = Boundary.infiniteFuture();
			pos += 2;
		} else {
			upperLog = new ParseLog(pos);
			t2 = this.endFormat.parse(text, upperLog, attrs);
			if (t2 == null || upperLog.isError()) {
                if (
                    (t1 != null)
                    && (this.merger != null)
                    && !attrs.get(Attributes.LENIENCY, SMART).isStrict()
                ) {
                    IntervalFactory<T, I> iif = this.factory;
                    ChronoParser<T> parser = this.endFormat;
                    ChronoEntity<?> lowerRaw = lowerLog.getRawValues();
                    if (lowerRaw.hasTimezone()) {
                        attrs = new Wrapper(attrs, lowerRaw.getTimezone());
                    }
                    ChronoDisplay cv = this.merger.preformat(t1, attrs);
                    for (ChronoElement<?> key : iif.stdElements(lowerRaw)) {
                        parser = setDefault(parser, cv, key);
                    }
                    upperLog.reset();
                    upperLog.setPosition(pos);
                    t2 = parser.parse(text, upperLog, attrs);
                }
            }
			if (t2 == null || upperLog.isError()) {
				status.setError(pos, upperLog.getErrorMessage());
				return null;
			}
            upper = Boundary.of(right, t2);
			pos = upperLog.getPosition();
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
                    rightVisible = true;
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

        // special case for P-strings (ISO-support)
        if (period != null) {
            IntervalFactory<T, I> iif = this.factory;

            if (lower == null) {
                if (t2 == null) {
                    status.setError(
                        posLower,
                        "Cannot process start period without end time.");
                    return null;
                }
                t1 = iif.minusPeriod(t2, period, upperLog, attributes);
                if (t1 == null) {
                    status.setError(posLower, "Wrong period: " + period);
                    return null;
                }
                lower = Boundary.of(left, t1);
            }

            if (upper == null) {
                t2 = iif.plusPeriod(t1, period, lowerLog, attributes);
                if (t2 == null) {
                    status.setError(posUpper, "Wrong period: " + period);
                    return null;
                }
                upper = Boundary.of(right, t2);
            }
        }

        // create and return interval
        I interval = this.factory.between(lower, upper);

        if (this.policy == BracketPolicy.SHOW_WHEN_NON_STANDARD) {
            boolean visible = this.policy.display(interval);

            if (visible && (!leftVisible || !rightVisible)) {
                int index = (!rightVisible ? pos : start);
                status.setError(index, "Missing boundary.");
                return null;
            } else if (!visible && (leftVisible || rightVisible)) {
                int index = (rightVisible ? pos : start);
                status.setError(
                    index,
                    "Standard boundary not allowed due to sign policy.");
                return null;
            }
        }

        status.setPosition(pos);
        return interval;

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

    @SuppressWarnings("unchecked")
    private static <T, V> ChronoParser<T> setDefault(
        ChronoParser<T> parser,
        ChronoDisplay cv,
        ChronoElement<V> key
    ) {

        ChronoFormatter<?> fmt = ChronoFormatter.class.cast(parser);
        fmt = fmt.withDefault(key, cv.get(key));
        return (ChronoParser<T>) fmt;

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class Wrapper
        implements AttributeQuery {

        //~ Instanzvariablen ----------------------------------------------

        private final AttributeQuery attributes;
        private final AttributeKey<Boolean> specialKey;
        private final TZID tzid;

        //~ Konstruktoren -------------------------------------------------

        Wrapper(AttributeQuery attributes) {
            this(attributes, null);

        }

        Wrapper(
            AttributeQuery attributes,
            TZID tzid
        ) {
            super();

            this.attributes = attributes;
            this.specialKey = Attributes.TRAILING_CHARACTERS;
            this.tzid = tzid;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public boolean contains(AttributeKey<?> key) {
            if (key.equals(this.specialKey)) {
                return true;
            } else if (
                (this.tzid != null)
                && key.equals(Attributes.TIMEZONE_ID)
            ) {
                return true;
            }
            return this.attributes.contains(key);
        }

        @Override
        public <A> A get(AttributeKey<A> key) {
            if (key.equals(this.specialKey)) {
                return key.type().cast(Boolean.TRUE);
            } else if (
                (this.tzid != null)
                && key.equals(Attributes.TIMEZONE_ID)
            ) {
                return key.type().cast(this.tzid);
            }
            return this.attributes.get(key);
        }

        @Override
        public <A> A get(AttributeKey<A> key, A defaultValue) {
            if (key.equals(this.specialKey)) {
                return key.type().cast(Boolean.TRUE);
            } else if (
                (this.tzid != null)
                && key.equals(Attributes.TIMEZONE_ID)
            ) {
                return key.type().cast(this.tzid);
            }
            return this.attributes.get(key, defaultValue);
        }

    }

}
