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
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.Temporal;
import net.time4j.engine.TimeLine;
import net.time4j.format.Attributes;
import net.time4j.format.ChronoFormatter;
import net.time4j.format.ChronoParser;
import net.time4j.format.ParseLog;

import java.text.ParseException;

import static net.time4j.format.Leniency.SMART;
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
		return new IntervalParser<T>(factory, format, format, policy);

	}

    /**
     * <p>Interne Methode, die von ISO-Intervallen aufgerufen wird. </p>
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
		IsoIntervalFactory<T> factory,
		ChronoFormatter<T> startFormat,
		ChronoFormatter<T> endFormat,
		BracketPolicy	   policy
	) {

		return new IntervalParser<T>(factory, startFormat, endFormat, policy);

	}

    /**
     * <p>Interpretes given text as chronological interval. </p>
     *
     * @param   text        text to be parsed
     * @return  parse result
     * @throws  IndexOutOfBoundsException if the text is empty
     * @throws  ParseException if the text is not parseable
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text als chronologisches Intervall. </p>
     *
     * @param   text        text to be parsed
     * @return  parse result
     * @throws  IndexOutOfBoundsException if the text is empty
     * @throws  ParseException if the text is not parseable
     */
	public ChronoInterval<T> parse(String text) throws ParseException {

        ParseLog plog = new ParseLog();
        ChronoInterval<T> ret = this.parse(text, plog, Attributes.empty());

        if (ret == null) {
            throw new ParseException(
                plog.getErrorMessage(),
                plog.getErrorIndex());
        }

        return ret;

    }

	@Override
	public ChronoInterval<T> parse(
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
            attrs =
                new AttributeWrapper(
                    attributes,
                    Attributes.TRAILING_CHARACTERS);
        }

        Class<T> type = this.startFormat.getChronology().getChronoType();
		IntervalEdge left = CLOSED;
		IntervalEdge right =
			Calendrical.class.isAssignableFrom(type) ? CLOSED : OPEN;
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

		if (
            (c == 'P')
            && (this.factory instanceof IsoIntervalFactory)
        ) {
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

		if (
            (c == 'P')
            && (this.factory instanceof IsoIntervalFactory)
        ) {
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
                    && (this.factory instanceof IsoIntervalFactory)
                    && !attrs.get(Attributes.LENIENCY, SMART).isStrict()
                ) {
                    IsoIntervalFactory<T> iif =
                        (IsoIntervalFactory<T>) this.factory;
                    ChronoFormatter<T> fmt = this.endFormat;
                    ChronoEntity<?> upperRaw = upperLog.getRawValues();
                    for (ChronoElement<?> key : iif.stdElements(upperRaw)) {
                        fmt = withDefault(fmt, t1, key);
                    }
                    ChronoEntity<?> lowerRaw = lowerLog.getRawValues();
                    if (lowerRaw.hasTimezone()) {
                        fmt = fmt.withTimezone(lowerRaw.getTimezone());
                    }
                    upperLog.reset();
                    upperLog.setPosition(pos);
                    attrs =
                        new AttributeWrapper(
                            attrs,
                            Attributes.USE_DEFAULT_WHEN_ERROR);
                    t2 = fmt.parse(text, upperLog, attrs);
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
            IsoIntervalFactory<T> iif = (IsoIntervalFactory<T>) this.factory;

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

    // wildcard capture
    private static
    <T extends ChronoEntity<T>, V> ChronoFormatter<T> withDefault(
        ChronoFormatter<T> fmt,
        T timepoint,
        ChronoElement<V> key
    ) {

        return fmt.withDefault(key, timepoint.get(key));

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class AttributeWrapper
        implements AttributeQuery {

        //~ Instanzvariablen ----------------------------------------------

        private final AttributeQuery attributes;
        private final AttributeKey<Boolean> specialKey;

        //~ Konstruktoren -------------------------------------------------

        AttributeWrapper(
            AttributeQuery attributes,
            AttributeKey<Boolean> specialKey
        ) {
            super();

            this.attributes = attributes;
            this.specialKey = specialKey;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public boolean contains(AttributeKey<?> key) {
            if (key.equals(this.specialKey)) {
                return true;
            }
            return this.attributes.contains(key);
        }

        @Override
        public <A> A get(AttributeKey<A> key) {
            if (key.equals(this.specialKey)) {
                return key.type().cast(Boolean.TRUE);
            }
            return this.attributes.get(key);
        }

        @Override
        public <A> A get(AttributeKey<A> key, A defaultValue) {
            if (key.equals(this.specialKey)) {
                return key.type().cast(Boolean.TRUE);
            }
            return this.attributes.get(key, defaultValue);
        }

    }

}
