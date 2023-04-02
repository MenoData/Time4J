/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2023 Meno Hochschild, <http://www.menodata.de/>
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
import net.time4j.engine.Temporal;
import net.time4j.engine.TimeLine;
import net.time4j.format.expert.ChronoParser;
import net.time4j.format.expert.ParseLog;

import java.text.ParseException;

import static net.time4j.format.Attributes.TRAILING_CHARACTERS;
import static net.time4j.range.IntervalEdge.CLOSED;
import static net.time4j.range.IntervalEdge.OPEN;

/**
 * <p>
 * Interpretiert Intervalle basierend auf Zeitformatierern, die auf die Start-
 * oder Endkomponenten angewandt werden. </p>
 *
 * @author Meno Hochschild
 * @param <T> generic temporal type
 * @param <I> generic interval type
 * @since 2.0
 */
class IntervalParser<T extends Temporal<? super T>, I extends IsoInterval<T, I>>
    implements ChronoParser<I> {

    //~ Instanzvariablen --------------------------------------------------
    private final IntervalFactory<T, I> factory;
    private final ChronoParser<T> startFormat;
    private final ChronoParser<T> endFormat;
    private final BracketPolicy policy;
    private final Character separator;

    //~ Konstruktoren -----------------------------------------------------
    IntervalParser(
        IntervalFactory<T, I> factory,
        ChronoParser<T> startFormat,
        ChronoParser<T> endFormat, // optional
        BracketPolicy policy,
        Character separator // optional
    ) {
        super();

        if (policy == null) {
            throw new NullPointerException("Missing bracket policy.");
        }

        this.factory = factory;
        this.startFormat = startFormat;
        this.endFormat = endFormat;
        this.policy = policy;
        this.separator = separator;

    }

    //~ Methoden ----------------------------------------------------------
    /**
     * <p>
     * Factory method with either solidus or hyphen as separation char. </p>
     *
     * @param <T> generic temporal type
     * @param <I> generic interval type
     * @param factory interval factory
     * @param parser boundary parser
     * @param policy bracket policy
     * @return new interval parser
     * @since 2.0
     */
    static <T extends Temporal<? super T>, I extends IsoInterval<T, I>> IntervalParser<T, I> of(
        IntervalFactory<T, I> factory,
        ChronoParser<T> parser,
        BracketPolicy policy
    ) {

        if (parser == null) {
            throw new NullPointerException("Missing boundary parser.");
        }

        return new IntervalParser<>(factory, parser, parser, policy, null);

    }

    /**
     * <p>
     * General factory method. </p>
     *
     * @param <T> generic temporal type
     * @param <I> generic interval type
     * @param factory interval factory
     * @param startFormat formatter for lower interval boundary
     * @param endFormat formatter for upper interval boundary
     * @param policy bracket policy
     * @param separator separation char between start and end component
     * @return new interval parser
     * @since 3.9/4.6
     */
    static <T extends Temporal<? super T>, I extends IsoInterval<T, I>> IntervalParser<T, I> of(
        IntervalFactory<T, I> factory,
        ChronoParser<T> startFormat,
        ChronoParser<T> endFormat,
        BracketPolicy policy,
        char separator
    ) {

        if (startFormat == null) {
            throw new NullPointerException("Missing start boundary parser.");
        } else if (endFormat == null) {
            throw new NullPointerException("Missing end boundary parser.");
        }

        return new IntervalParser<>(
                factory,
                startFormat,
                endFormat,
                policy,
                Character.valueOf(separator));

    }

    /**
     * <p>
     * Interpretiert den angegebenen Text als chronologisches Intervall. </p>
     *
     * @param text text to be parsed
     * @return parse result
     * @throws IndexOutOfBoundsException if the text is empty
     * @throws ParseException if the text is not parseable
     */
    I parse(String text) throws ParseException {

        ParseLog plog = new ParseLog();
        AttributeQuery attrs = this.startFormat.getAttributes();
        I ret = this.parse(text, plog, attrs);
        int pos = plog.getPosition();
        int len = text.length();

        if ((ret == null) || plog.isError()) {
            throw new ParseException(
                plog.getErrorMessage(),
                plog.getErrorIndex());
        } else if ((pos < len)
                && !attrs.get(TRAILING_CHARACTERS, Boolean.FALSE).booleanValue()) {
            String suffix;

            if (len - pos <= 10) {
                suffix = text.subSequence(pos, len).toString();
            } else {
                suffix = text.subSequence(pos, pos + 10).toString() + "...";
            }

            throw new ParseException("Unparsed trailing characters: " + suffix, pos);
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
            throw new IndexOutOfBoundsException("[" + pos + "]: " + text.toString());
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
        int symbol = 0;

        // starting boundary
        char c = text.charAt(pos);
        boolean leftVisible = ((c == '[') || (c == '('));
        boolean rightVisible = false;

        if (leftVisible) {
            if (this.policy == BracketPolicy.SHOW_NEVER) {
                status.setError(
                    pos,
                    "Illegal start boundary due to bracket policy " + this.policy + ": " + c);
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
            int solidus = -1; // here assuming iso mode hence searching for solidus only
            while (++index < len) {
                if (text.charAt(index) == '/') {
                    solidus = index;
                    break;
                }
            }
            if (solidus == -1) {
                status.setError(
                    pos,
                    "Solidus char separating start and end boundaries expected.");
                return null;
            }
            period = text.subSequence(pos, solidus).toString();
            pos = solidus + 1;
        } else if (
            (c == '-')
            && (pos + 1 < len)
            && this.isExpectedSeparator(text.charAt(pos + 1))
        ) {
            if ((left == CLOSED) && leftVisible) {
                status.setError(pos - 1, "Open boundary expected.");
                return null;
            }
            left = OPEN;
            lower = Boundary.infinitePast();
            symbol = 1;
            pos += 2;
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
            this.checkSeparatorChar(text, status, pos, len);
            if (status.isError()) {
                return null;
            }
            symbol = 2;
            pos++;
        } else {
            lowerLog = new ParseLog(pos);
            t1 = this.startFormat.parse(text, lowerLog, attributes);
            if (t1 == null || lowerLog.isError()) {
                status.setError(pos, lowerLog.getErrorMessage());
                return null;
            }
            lower = Boundary.of(left, t1);
            pos = lowerLog.getPosition();
            this.checkSeparatorChar(text, status, pos, len);
            if (status.isError()) {
                return null;
            }
            pos++;
        }

        // end component after separator char
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
                (c == '-')
                && ((pos + 1 >= len) || (text.charAt(pos + 1) == ')'))
        ) {
            if (symbol == 2) {
                status.setError(pos, "Mixed infinity symbols not allowed.");
                return null;
            }
            right = OPEN;
            upper = Boundary.infiniteFuture();
            symbol = 1;
            pos++;
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
            } else if (symbol == 1) {
                status.setError(pos, "Mixed infinity symbols not allowed.");
                return null;
            }
            right = OPEN;
            upper = Boundary.infiniteFuture();
            symbol = 2;
            pos += 2;
        } else {
            upperLog = new ParseLog(pos);
            if (lowerLog == null) {
                t2 = this.startFormat.parse(text, upperLog, attributes);
            } else if (this.endFormat == null) {
                t2 = this.parseReducedEnd(text, t1, lowerLog, upperLog, attributes);
            } else {
                t2 = this.endFormat.parse(text, upperLog, attributes);
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
                    "Illegal end boundary due to bracket policy " + this.policy + ": " + c);
                } else {
                    rightVisible = true;
                    right = ((c == ']') ? CLOSED : OPEN);
                    if (t2 != null) {
                        upper = Boundary.of(right, t2);
                    }
                    pos++;
                }
            } else if (this.policy == BracketPolicy.SHOW_ALWAYS) {
                status.setError(pos, "Missing end boundary bracket.");
            }
        }

        if (status.isError()) {
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

        if ((symbol == 0) && this.factory.supportsInfinity()) {
            lower = this.resolveInfinity(leftVisible, lower);
            upper = this.resolveInfinity(rightVisible, upper);
        }

        // create and return interval
        try {
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
                        "Standard boundary not allowed due to bracket policy: " + this.policy);
                    return null;
                }
            }

            status.setPosition(pos);
            return interval;
        } catch (IllegalArgumentException iae) {
            status.setError(pos, iae.getMessage());
            return null;
        }

    }

    /**
     * <p>Custom parsing using any kind of interval pattern (possibly with
     * or-logic). </p>
     *
     * @param text text to be parsed
     * @param factory interval factory
     * @param parser parser used for parsing either start or end component
     * @param pattern interval pattern containing the placeholders {0} or {1}
     * @return parsed interval
     * @throws ParseException if parsing fails
     * @since 4.18
     */
    static <T, I extends ChronoInterval<T>> I parsePattern(
        CharSequence text,
        IntervalCreator<T, I> factory,
        ChronoParser<T> parser,
        String pattern
    ) throws ParseException {

        ParseLog plog = new ParseLog();
        String[] components = pattern.split("\\|");

        for (String component : components) {
            plog.reset();
            I interval = IntervalParser.parseComponent(text, factory, parser, component, plog);

            if ((interval != null) && !plog.isError()) {
                return interval;
            }
        }

        if (plog.isError()) {
            throw new ParseException(plog.getErrorMessage(), plog.getErrorIndex());
        } else {
            throw new ParseException("Parsing of interval failed: " + text, plog.getPosition());
        }

    }

    private static <T, I extends ChronoInterval<T>> I parseComponent(
        CharSequence text,
        IntervalCreator<T, I> factory,
        ChronoParser<T> parser,
        String pattern,
        ParseLog plog
    ) {

        int pos = plog.getPosition();
        int len = text.length();
        T start = null;
        T end = null;
        int i = 0;
        int n = pattern.length();
        boolean startWasSet = false;
        boolean endWasSet = false;
        AttributeQuery attrs = parser.getAttributes();

        while (i < n) {
            char c = pattern.charAt(i);

            if ((c == '{') && (i + 2 < n) && (pattern.charAt(i + 2) == '}')) {
                char next = pattern.charAt(i + 1);

                if (next == '0') {
                    if (startWasSet) {
                        plog.setError(pos, "Cannot parse start component more than once.");
                        return null;
                    }
                    plog.setPosition(pos);
                    if ((pos + 1 < len) && (text.charAt(pos) == '-') && (text.charAt(pos + 1) == '\u221E')) {
                        // start = null;
                        pos += 2;
                    } else {
                        start = parser.parse(text, plog, attrs);
                        if ((start == null) || plog.isError()) {
                            return null;
                        } else {
                            pos = plog.getPosition();
                        }
                    }
                    startWasSet = true;
                    i += 3;
                    continue;
                } else if (next == '1') {
                    if (endWasSet) {
                        plog.setError(pos, "Cannot parse end component more than once.");
                        return null;
                    }
                    plog.setPosition(pos);
                    if (
                        (pos + 1 < len) 
                        && (text.charAt(pos) == '+') 
                        && (text.charAt(pos + 1) == '\u221E')
                    ) {
                        // end = null;
                        pos += 2;
                    } else {
                        end = parser.parse(text, plog, attrs);
                        if ((end == null) || plog.isError()) {
                            return null;
                        } else {
                            pos = plog.getPosition();
                        }
                    }
                    endWasSet = true;
                    i += 3;
                    continue;
                }
            } else if (c == '[' || c == ']' || c == '(' || c == ')') {
                plog.setError(pos, "Brackets representing interval boundaries cannot be parsed: " + text);
                return null;
            }

            if (pos >= len) {
                plog.setError(pos, "End of text reached.");
                return null;
            } else if (c != text.charAt(pos)) {
                plog.setError(pos, "Literal mismatched: " + text.toString() + " (expected=" + pattern + ")");
                return null;
            }

            i++;
            pos++;
        }

        if ((pos < len) && !attrs.get(TRAILING_CHARACTERS, Boolean.FALSE).booleanValue()) {
            plog.setError(pos, "Trailing characters found: " + text);
            return null;
        }

        Boundary<T> s = Boundary.infinitePast();
        Boundary<T> e = Boundary.infiniteFuture();

        if (start != null) {
            s = Boundary.ofClosed(start);
        }

        if (end != null) {
            if (factory.isCalendrical()) {
                e = Boundary.ofClosed(end);
            } else {
                e = Boundary.ofOpen(end);
            }
        }

        try {
            return factory.between(s, e);
        } catch (IllegalArgumentException iae) {
            plog.setError(pos, iae.getMessage());
            return null;
        }

    }

    protected T parseReducedEnd(
        CharSequence text,
        T t1,
        ParseLog lowerLog,
        ParseLog upperLog,
        AttributeQuery attrs
    ) {

        return null;

    }

    private Boundary<T> resolveInfinity(
        boolean visible,
        Boundary<T> boundary
    ) {

        if (!boundary.isInfinite() && (!visible || boundary.isOpen())) {
            TimeLine<T> timeLine = this.factory.getTimeLine();
            T test = boundary.getTemporal();
            if (test.equals(timeLine.getMinimum())) {
                return Boundary.infinitePast();
            } else if (test.equals(timeLine.getMaximum())) {
                return Boundary.infiniteFuture();
            }
        }

        return boundary;

    }

    private void checkSeparatorChar(
        CharSequence text,
        ParseLog status,
        int pos,
        int len
    ) {

        if (pos >= len) {
            status.setError(
                pos,
                "Reached end of text, but not found any separation char.");
            return;
        }

        if (!this.isExpectedSeparator(text.charAt(pos))) {
            status.setError(
                pos,
                "Missing or misplaced separation char between start and end boundaries: "
                + ((this.separator == null) ? "/ or -" : this.separator.toString()));
        }

    }

    private boolean isExpectedSeparator(char test) {

        if (this.separator == null) {
            return ((test == '/') || (test == '-'));
        } else {
            return (test == this.separator.charValue());
        }

    }

}
