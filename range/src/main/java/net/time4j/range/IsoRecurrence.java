/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (IsoRecurrence.java) is part of project Time4J.
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

import net.time4j.CalendarUnit;
import net.time4j.Duration;
import net.time4j.IsoDateUnit;
import net.time4j.PlainDate;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;


/**
 * <p>Represents a sequence of recurrent intervals as defined by ISO-8601. </p>
 *
 * @author  Meno Hochschild
 * @since   3.22/4.18
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Sequenz von wiederkehrenden Intervallen wie in ISO-8601 definiert. </p>
 *
 * @author  Meno Hochschild
 * @since   3.22/4.18
 */
public class IsoRecurrence<I extends IsoInterval<?, ?>>
    implements Iterable<I> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int INFINITE = -1;

    private static final int TYPE_START_END = 0;
    private static final int TYPE_START_DURATION = 1;
    private static final int TYPE_DURATION_END = 2;

    private static final Map<String, ChronoFormatter<PlainDate>> DATE_FORMATTERS;

    static {
        String[] datePatterns = {"YYYY-'W'ww-E", "uuuu-DDD", "uuuu-MM-dd", "YYYY'W'wwE", "uuuuDDD", "uuuuMMdd"};
        Map<String, ChronoFormatter<PlainDate>> formatters = new HashMap<>();
        for (String datePattern : datePatterns) {
            formatters.put(
                datePattern,
                ChronoFormatter.ofDatePattern(datePattern, PatternType.CLDR, Locale.ROOT));
        }
        DATE_FORMATTERS = Collections.unmodifiableMap(formatters);
    }

    //~ Instanzvariablen --------------------------------------------------

    private final int count;
    private final int type;

    //~ Konstruktoren -----------------------------------------------------

    private IsoRecurrence(
        int count,
        int type
    ) {
        super();

        this.count = count;
        this.type = type;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a recurrent sequence of intervals having given duration. </p>
     *
     * @param   count       the count of repeating intervals ({@code >= 0})
     * @param   start       denotes the start of first interval (inclusive)
     * @param   duration    represents the duration of every repeating interval
     * @return  sequence of recurrent date intervals
     * @throws  IllegalArgumentException if the count or the duration are not positive
     */
    /*[deutsch]
     * <p>Erzeugt eine Sequenz von wiederkehrenden Intervallen mit der angegebenen Dauer. </p>
     *
     * @param   count       the count of repeating intervals ({@code >= 0})
     * @param   start       denotes the start of first interval (inclusive)
     * @param   duration    represents the duration of every repeating interval
     * @return  sequence of recurrent date intervals
     * @throws  IllegalArgumentException if the count or the duration are not positive
     */
    public static IsoRecurrence<DateInterval> of(
        int count,
        PlainDate start,
        Duration<? extends IsoDateUnit> duration
    ) {

        check(count);

        if (start == null) {
            throw new NullPointerException("Missing start of recurrent interval.");
        }

        return new RecurrentDateIntervals(count, TYPE_START_DURATION, start, duration);

    }

    /**
     * <p>Creates a recurrent backward sequence of intervals having given duration. </p>
     *
     * @param   count       the count of repeating intervals ({@code >= 0})
     * @param   duration    represents the negative duration of every repeating interval
     * @param   end         denotes the end of first interval (inclusive)
     * @return  sequence of recurrent date intervals
     * @throws  IllegalArgumentException if the count or the duration are not positive
     */
    /*[deutsch]
     * <p>Erzeugt eine Sequenz von wiederkehrenden r&uuml;ckw&auml;rts laufenden
     * Intervallen mit der angegebenen Dauer. </p>
     *
     * @param   count       the count of repeating intervals ({@code >= 0})
     * @param   duration    represents the negative duration of every repeating interval
     * @param   end         denotes the end of first interval (inclusive)
     * @return  sequence of recurrent date intervals
     * @throws  IllegalArgumentException if the count or the duration are not positive
     */
    public static IsoRecurrence<DateInterval> of(
        int count,
        Duration<? extends IsoDateUnit> duration,
        PlainDate end
    ) {

        check(count);

        if (end == null) {
            throw new NullPointerException("Missing end of recurrent interval.");
        }

        return new RecurrentDateIntervals(count, TYPE_DURATION_END, end, duration);

    }

    /**
     * <p>Creates a recurrent sequence of intervals having the duration
     * of first interval in years, months and days. </p>
     *
     * @param   count       the count of repeating intervals ({@code >= 0})
     * @param   start       denotes the start of first interval (inclusive)
     * @param   end         denotes the end of first interval (inclusive)
     * @return  sequence of recurrent date intervals
     * @throws  IllegalArgumentException if the count or the duration are not positive
     */
    /*[deutsch]
     * <p>Erzeugt eine Sequenz von wiederkehrenden Intervallen mit der Dauer des ersten Intervalls
     * in Jahren, Monaten und Tagen. </p>
     *
     * @param   count       the count of repeating intervals ({@code >= 0})
     * @param   start       denotes the start of first interval (inclusive)
     * @param   end         denotes the end of first interval (inclusive)
     * @return  sequence of recurrent date intervals
     * @throws  IllegalArgumentException if the count or the duration are not positive
     */
    public static IsoRecurrence<DateInterval> of(
        int count,
        PlainDate start,
        PlainDate end
    ) {

        check(count);

        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End is not after start.");
        }

        return new RecurrentDateIntervals(
            count,
            TYPE_START_END,
            start,
            Duration.inYearsMonthsDays().between(start, end.plus(1, CalendarUnit.DAYS)));

    }

    /**
     * <p>Obtains the count of recurrent intervals. </p>
     *
     * @return non-negative count of recurrent intervals or {@code -1} if infinite
     */
    /*[deutsch]
     * <p>Ermittelt die Anzahl der wiederkehrenden Intervalle. </p>
     *
     * @return  non-negative count of recurrent intervals or {@code -1} if infinite
     */
    public int getCount() {

        return this.count;

    }

    /**
     * <p>Creates a copy with given modified count. </p>
     *
     * @param   count   non-negative count of recurrent intervals
     * @return  modified copy or this instance if not modified
     * @throws  IllegalArgumentException if the argument is negative
     */
    /*[deutsch]
     * <p>Erzeugt eine Kopie mit der angegebenen neuen Anzahl von wiederkehrenden Intervallen. </p>
     *
     * @param   count   non-negative count of recurrent intervals
     * @return  modified copy or this instance if not modified
     * @throws  IllegalArgumentException if the argument is negative
     */
    public IsoRecurrence<I> withCount(int count) {

        if (count == this.count) {
            return this;
        }

        check(count);
        return this.copyWithCount(count);

    }

    /**
     * <p>Creates a copy with an unlimited count of recurrent intervals. </p>
     *
     * <p>This method mainly exists to satisfy the requirements of ISO-8601. However:
     * <strong>Special care must be taken to avoid infinite loops.</strong></p>
     *
     * @return  modified copy or this instance if not modified
     */
    /*[deutsch]
     * <p>Erzeugt eine Kopie mit einer unbegrenzten Anzahl von wiederkehrenden Intervallen. </p>
     *
     * <p>Diese Methode existiert haupts&auml;chlich, um die Anforderungen von ISO-8601 zu erf&uuml;llen.
     * Aber: <strong>Besondere Vorsicht ist angebracht, um Endlosschleifen zu vermeiden.</strong></p>
     *
     * @return  modified copy or this instance if not modified
     */
    public IsoRecurrence<I> withInfiniteCount() {

        if (this.count == INFINITE) {
            return this;
        }

        return this.copyWithCount(INFINITE);

    }

    @Override
    public Iterator<I> iterator() {

        throw new AbstractMethodError();

    }

    /**
     * <p>Queries if the resulting interval stream describes a backwards running sequence. </p>
     *
     * @return  boolean
     */
    /*[deutsch]
     * <p>Ermittelt, ob die resultierende Intervallsequenz r&uuml;ckl&auml;ufig ist. </p>
     *
     * @return  boolean
     */
    public boolean isBackwards() {

        return (this.type == TYPE_DURATION_END);

    }

    /**
     * <p>Queries if the count of intervals is zero. </p>
     *
     * @return  boolean
     */
    /**
     * <p>Ermittelt, ob die Anzahl der Intervalle null ist. </p>
     *
     * @return  boolean
     */
    public boolean isEmpty() {

        return (this.count == 0);

    }

    /**
     * <p>Queries if the count of intervals is unlimited. </p>
     *
     * @return  boolean
     */
    /**
     * <p>Ermittelt, ob die Anzahl der Intervalle unbegrenzt ist. </p>
     *
     * @return  boolean
     */
    public boolean isInfinite() {

        return (this.count == INFINITE);

    }

    /**
     * <p>Parses a string like &quot;R5/2016-04-01/2016-04-30&quot; or &quot;R5/2016-04-01/P1M&quot;
     * to a sequence of recurrent date intervals. </p>
     *
     * @param   iso     canonical representation of recurrent date intervals
     * @return  parsed sequence of recurrent date intervals
     * @throws  ParseException in any case of inconsistencies
     */
    /*[deutsch]
     * <p>Interpretiert einen Text wie &quot;R5/2016-04-01/2016-04-30&quot; oder &quot;R5/2016-04-01/P1M&quot;
     * als eine Sequenz von wiederkehrenden Datumsintervallen. </p>
     *
     * @param   iso     canonical representation of recurrent date intervals
     * @return  parsed sequence of recurrent date intervals
     * @throws  ParseException in any case of inconsistencies
     */
    public static IsoRecurrence<DateInterval> parseDateIntervals(String iso)
        throws ParseException {

        String[] parts = iso.split("/");
        int count = parseCount(parts);
        boolean infinite = false;

        if (count == INFINITE) {
            count = 0;
            infinite = true;
        }

        IsoRecurrence<DateInterval> recurrence;

        if (parts[2].charAt(0) == 'P') {
            String pattern = getDatePattern(parts[1]);
            PlainDate start = DATE_FORMATTERS.get(pattern).parse(parts[1]);
            Duration<? extends IsoDateUnit> duration = Duration.parseCalendarPeriod(parts[2]);
            recurrence = IsoRecurrence.of(count, start, duration);
        } else if (parts[1].charAt(0) == 'P') {
            Duration<? extends IsoDateUnit> duration = Duration.parseCalendarPeriod(parts[1]);
            String pattern = getDatePattern(parts[2]);
            PlainDate end = DATE_FORMATTERS.get(pattern).parse(parts[2]);
            recurrence = IsoRecurrence.of(count, duration, end);
        } else {
            String pattern = getDatePattern(parts[1]);
            ChronoFormatter<PlainDate> f = DATE_FORMATTERS.get(pattern);
            PlainDate start = f.parse(parts[1]);
            PlainDate end = f.parse(parts[2]);
            recurrence = IsoRecurrence.of(count, start, end);
        }

        if (infinite) {
            recurrence = recurrence.withInfiniteCount();
        }

        return recurrence;

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() == obj.getClass()) {
            IsoRecurrence<?> that = IsoRecurrence.class.cast(obj);
            return ((this.count == that.count) && (this.type == that.type));
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return this.count;

    }

    int getType() {

        return this.type;

    }

    IsoRecurrence<I> copyWithCount(int count) {

        throw new AbstractMethodError();

    }

    private static void check(int count) {

        if (count < 0) {
            throw new IllegalArgumentException("Count of recurrent intervals must not be negative: " + count);
        }

    }

    private static int parseCount(String[] parts)
        throws ParseException {

        if (parts.length != 3) {
            throw new ParseException("Recurrent interval format must contain exactly 3 chars '/'.", 0);
        } else if (parts[0].isEmpty() || parts[0].charAt(0) != 'R') {
            throw new ParseException("Recurrent interval format must start with char 'R'.", 0);
        }

        int total = INFINITE;

        for (int i = 1; i < parts[0].length(); i++) {
            if (i == 1) {
                total = 0;
            }
            int digit = (parts[0].charAt(i) - '0');
            if (digit >= 0 && digit < 9) {
                total = total * 10 + digit;
            } else {
                throw new ParseException("Digit 0-9 is missing.", i);
            }
        }

        return total;

    }

    private static String getDatePattern(String iso) {

        int countOfHyphens = 0;
        boolean weekbased = false;

        for (int i = 1, n = iso.length(); i < n; i++) {
            char c = iso.charAt(i);
            if (c == 'T') {
                break;
            } else if (c == '-') {
                countOfHyphens++;
            } else if (c == 'W') {
                weekbased = true;
            }
        }

        if (countOfHyphens > 0) { // extended format
            if (weekbased) {
                return "YYYY-'W'ww-E";
            } else if (countOfHyphens == 1) {
                return "uuuu-DDD";
            } else {
                return "uuuu-MM-dd";
            }
        } else { // basic format
            if (weekbased) {
                return "YYYY'W'wwE";
            } else if (iso.length() == 7) {
                return "uuuuDDD";
            } else {
                return "uuuuMMdd";
            }
        }

    }

    //~ Innere Klassen ----------------------------------------------------

    private abstract static class ReadOnlyIterator<I, R extends IsoRecurrence<?>>
        implements Iterator<I> {

        //~ Instanzvariablen ----------------------------------------------

        private int index = 0;
        private R recurrence;

        //~ Konstruktoren -------------------------------------------------

        ReadOnlyIterator(R recurrence) {
            super();

            this.recurrence = recurrence;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public final boolean hasNext() {

            int c = this.recurrence.getCount();
            return ((c == INFINITE) || (this.index < c));

        }

        @Override
        public final I next() {

            int c = this.recurrence.getCount();

            if ((c != INFINITE) && (this.index >= c)) {
                throw new NoSuchElementException("After end of interval recurrence.");
            }

            I result = nextInterval();
            this.index++;
            return result;

        }

        @Override
        public final void remove() {

            throw new UnsupportedOperationException();

        }

        protected abstract I nextInterval();

    }

    private static class RecurrentDateIntervals
        extends IsoRecurrence<DateInterval> {

        //~ Statische Felder/Initialisierungen ----------------------------

        //~ Instanzvariablen ----------------------------------------------

        private final PlainDate ref;
        private final Duration<? extends IsoDateUnit> duration;

        //~ Konstruktoren -------------------------------------------------

        private RecurrentDateIntervals(
            int count,
            int type,
            PlainDate ref,
            Duration<? extends IsoDateUnit> duration
        ) {
            super(count, type);

            this.ref = ref;
            this.duration = duration;

            if (!duration.isPositive()) {
                throw new IllegalArgumentException("Duration must be positive: " + duration);
            }

        }

        //~ Methoden ----------------------------------------------------------

        @Override
        public Iterator<DateInterval> iterator() {
            return new ReadOnlyIterator<DateInterval, RecurrentDateIntervals>(this) {
                private PlainDate current = RecurrentDateIntervals.this.ref;
                @Override
                protected DateInterval nextInterval() {
                    PlainDate next;
                    Boundary<PlainDate> s;
                    Boundary<PlainDate> e;
                    if (RecurrentDateIntervals.this.isBackwards()) {
                        next = this.current.minus(RecurrentDateIntervals.this.duration);
                        s = Boundary.ofClosed(next.plus(1, CalendarUnit.DAYS));
                        e = Boundary.ofClosed(this.current);
                    } else {
                        next = this.current.plus(RecurrentDateIntervals.this.duration);
                        s = Boundary.ofClosed(this.current);
                        e = Boundary.ofClosed(next.minus(1, CalendarUnit.DAYS));
                    }
                    this.current = next;
                    return DateIntervalFactory.INSTANCE.between(s, e);
                }
            };
        }

        @Override
        public boolean equals(Object obj) {

            if (super.equals(obj)) {
                RecurrentDateIntervals that = (RecurrentDateIntervals) obj;
                return (this.ref.equals(that.ref) && this.duration.equals(that.duration));
            }

            return false;

        }

        @Override
        public int hashCode() {

            return super.hashCode() + 31 * this.ref.hashCode() + 37 * this.duration.hashCode();

        }

        @Override
        public String toString() {

            StringBuilder sb = new StringBuilder();
            sb.append('R');
            int c = this.getCount();
            if (c != INFINITE) {
                sb.append(this.getCount());
            }
            sb.append('/');

            switch (this.getType()) {
                case TYPE_START_DURATION:
                    sb.append(this.ref);
                    sb.append('/');
                    sb.append(this.duration);
                    break;
                case TYPE_DURATION_END:
                    sb.append(this.duration);
                    sb.append('/');
                    sb.append(this.ref);
                    break;
                case TYPE_START_END:
                    sb.append(this.ref);
                    sb.append('/');
                    sb.append(this.ref.plus(this.duration).minus(1, CalendarUnit.DAYS));
                    break;
            }

            return sb.toString();

        }

        @Override
        IsoRecurrence<DateInterval> copyWithCount(int count) {

            return new RecurrentDateIntervals(count, this.getType(), this.ref, this.duration);

        }

    }

}