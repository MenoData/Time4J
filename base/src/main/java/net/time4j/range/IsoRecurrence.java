/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
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

import net.time4j.Duration;
import net.time4j.IsoDateUnit;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;
import net.time4j.ZonalDateTime;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.Iso8601Format;
import net.time4j.tz.ZonalOffset;

import java.text.ParseException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static net.time4j.CalendarUnit.*;
import static net.time4j.ClockUnit.*;

import static java.util.Spliterator.*;


/**
 * <p>Represents a sequence of recurrent finite intervals as defined by ISO-8601. </p>
 *
 * @author  Meno Hochschild
 * @since   3.22/4.18
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Sequenz von endlichen wiederkehrenden Intervallen wie in ISO-8601 definiert. </p>
 *
 * @author  Meno Hochschild
 * @since   3.22/4.18
 */
public class IsoRecurrence<I>
    implements Iterable<I> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int INFINITE = -1;

    private static final int TYPE_START_END = 0;
    private static final int TYPE_START_DURATION = 1;
    private static final int TYPE_DURATION_END = 2;

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
     * <p>Creates a recurrent sequence of date intervals having given duration. </p>
     *
     * @param   count       the count of repeating intervals ({@code >= 0})
     * @param   start       denotes the start of first interval (inclusive)
     * @param   duration    represents the duration of every repeating interval
     * @return  sequence of recurrent closed date intervals
     * @throws  IllegalArgumentException if the count is negative or the duration is not positive
     */
    /*[deutsch]
     * <p>Erzeugt eine Sequenz von wiederkehrenden Datumsintervallen mit der angegebenen Dauer. </p>
     *
     * @param   count       the count of repeating intervals ({@code >= 0})
     * @param   start       denotes the start of first interval (inclusive)
     * @param   duration    represents the duration of every repeating interval
     * @return  sequence of recurrent closed date intervals
     * @throws  IllegalArgumentException if the count is negative or the duration is not positive
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
     * <p>Creates a recurrent backward sequence of date intervals having given duration. </p>
     *
     * @param   count       the count of repeating intervals ({@code >= 0})
     * @param   duration    represents the negative duration of every repeating interval
     * @param   end         denotes the end of first interval (inclusive)
     * @return  sequence of recurrent closed date intervals
     * @throws  IllegalArgumentException if the count is negative or the duration is not positive
     */
    /*[deutsch]
     * <p>Erzeugt eine Sequenz von wiederkehrenden r&uuml;ckw&auml;rts laufenden
     * Datumsintervallen mit der angegebenen Dauer. </p>
     *
     * @param   count       the count of repeating intervals ({@code >= 0})
     * @param   duration    represents the negative duration of every repeating interval
     * @param   end         denotes the end of first interval (inclusive)
     * @return  sequence of recurrent closed date intervals
     * @throws  IllegalArgumentException if the count is negative or the duration is not positive
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
     * <p>Creates a recurrent sequence of date intervals having the duration
     * of first interval in years, months and days. </p>
     *
     * @param   count       the count of repeating intervals ({@code >= 0})
     * @param   start       denotes the start of first interval (inclusive)
     * @param   end         denotes the end of first interval (inclusive)
     * @return  sequence of recurrent closed date intervals
     * @throws  IllegalArgumentException if the count is negative or if start is not before end
     */
    /*[deutsch]
     * <p>Erzeugt eine Sequenz von wiederkehrenden Datumsintervallen mit der Dauer des ersten Intervalls
     * in Jahren, Monaten und Tagen. </p>
     *
     * @param   count       the count of repeating intervals ({@code >= 0})
     * @param   start       denotes the start of first interval (inclusive)
     * @param   end         denotes the end of first interval (inclusive)
     * @return  sequence of recurrent closed date intervals
     * @throws  IllegalArgumentException if the count is negative or if start is not before end
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
            Duration.inYearsMonthsDays().between(start, end.plus(1, DAYS)));

    }

    /**
     * <p>Creates a recurrent sequence of timestamp intervals having given duration. </p>
     *
     * @param   count       the count of repeating intervals ({@code >= 0})
     * @param   start       denotes the start of first interval (inclusive)
     * @param   duration    represents the duration of every repeating interval
     * @return  sequence of recurrent half-open plain timestamp intervals
     * @throws  IllegalArgumentException if the count is negative or the duration is not positive
     */
    /*[deutsch]
     * <p>Erzeugt eine Sequenz von wiederkehrenden Zeit-Intervallen mit der angegebenen Dauer. </p>
     *
     * @param   count       the count of repeating intervals ({@code >= 0})
     * @param   start       denotes the start of first interval (inclusive)
     * @param   duration    represents the duration of every repeating interval
     * @return  sequence of recurrent half-open plain timestamp intervals
     * @throws  IllegalArgumentException if the count is negative or the duration is not positive
     */
    public static IsoRecurrence<TimestampInterval> of(
        int count,
        PlainTimestamp start,
        Duration<?> duration
    ) {

        check(count);

        if (start == null) {
            throw new NullPointerException("Missing start of recurrent interval.");
        }

        return new RecurrentTimestampIntervals(count, TYPE_START_DURATION, start, duration);

    }

    /**
     * <p>Creates a recurrent backward sequence of timestamp intervals having given duration. </p>
     *
     * @param   count       the count of repeating intervals ({@code >= 0})
     * @param   duration    represents the negative duration of every repeating interval
     * @param   end         denotes the end of first interval (exclusive)
     * @return  sequence of recurrent half-open plain timestamp intervals
     * @throws  IllegalArgumentException if the count is negative or the duration is not positive
     */
    /*[deutsch]
     * <p>Erzeugt eine Sequenz von wiederkehrenden r&uuml;ckw&auml;rts laufenden
     * Zeit-Intervallen mit der angegebenen Dauer. </p>
     *
     * @param   count       the count of repeating intervals ({@code >= 0})
     * @param   duration    represents the negative duration of every repeating interval
     * @param   end         denotes the end of first interval (exclusive)
     * @return  sequence of recurrent half-open plain timestamp intervals
     * @throws  IllegalArgumentException if the count is negative or the duration is not positive
     */
    public static IsoRecurrence<TimestampInterval> of(
        int count,
        Duration<?> duration,
        PlainTimestamp end
    ) {

        check(count);

        if (end == null) {
            throw new NullPointerException("Missing end of recurrent interval.");
        }

        return new RecurrentTimestampIntervals(count, TYPE_DURATION_END, end, duration);

    }

    /**
     * <p>Creates a recurrent sequence of timestamp intervals having the duration
     * of first timestamp interval in years, months, days, hours, minutes, seconds and nanoseconds. </p>
     *
     * @param   count       the count of repeating intervals ({@code >= 0})
     * @param   start       denotes the start of first interval (inclusive)
     * @param   end         denotes the end of first interval (exclusive)
     * @return  sequence of recurrent half-open plain timestamp intervals
     * @throws  IllegalArgumentException if the count is negative or if start is not before end
     */
    /*[deutsch]
     * <p>Erzeugt eine Sequenz von wiederkehrenden Zeit-Intervallen mit der Dauer des ersten Intervalls
     * in Jahren, Monaten, Tagen, Stunden, Minuten, Sekunden und Nanosekunden. </p>
     *
     * @param   count       the count of repeating intervals ({@code >= 0})
     * @param   start       denotes the start of first interval (inclusive)
     * @param   end         denotes the end of first interval (exclusive)
     * @return  sequence of recurrent half-open plain timestamp intervals
     * @throws  IllegalArgumentException if the count is negative or if start is not before end
     */
    public static IsoRecurrence<TimestampInterval> of(
        int count,
        PlainTimestamp start,
        PlainTimestamp end
    ) {

        check(count);

        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End is not after start.");
        }

        return new RecurrentTimestampIntervals(
            count,
            TYPE_START_END,
            start,
            Duration.in(YEARS, MONTHS, DAYS, HOURS, MINUTES, SECONDS, NANOS).between(start, end));

    }

    /**
     * <p>Creates a recurrent sequence of moment intervals having given duration. </p>
     *
     * @param   count       the count of repeating intervals ({@code >= 0})
     * @param   start       denotes the start of first interval (inclusive)
     * @param   duration    represents the duration of every repeating interval
     * @param   offset      time zone offset in full minutes
     * @return  sequence of recurrent half-open plain timestamp intervals
     * @throws  IllegalArgumentException if the count is negative or the duration is not positive
     */
    /*[deutsch]
     * <p>Erzeugt eine Sequenz von wiederkehrenden Moment-Intervallen mit der angegebenen Dauer. </p>
     *
     * @param   count       the count of repeating intervals ({@code >= 0})
     * @param   start       denotes the start of first interval (inclusive)
     * @param   duration    represents the duration of every repeating interval
     * @param   offset      time zone offset in full minutes
     * @return  sequence of recurrent half-open plain timestamp intervals
     * @throws  IllegalArgumentException if the count is negative or the duration is not positive
     */
    public static IsoRecurrence<MomentInterval> of(
        int count,
        Moment start,
        Duration<?> duration,
        ZonalOffset offset
    ) {

        check(count);

        return new RecurrentMomentIntervals(
            count, TYPE_START_DURATION, start.toZonalTimestamp(offset), offset, duration);

    }

    /**
     * <p>Creates a recurrent backward sequence of moment intervals having given duration. </p>
     *
     * @param   count       the count of repeating intervals ({@code >= 0})
     * @param   duration    represents the negative duration of every repeating interval
     * @param   end         denotes the end of first interval (exclusive)
     * @param   offset      time zone offset in full minutes
     * @return  sequence of recurrent half-open plain timestamp intervals
     * @throws  IllegalArgumentException if the count is negative or the duration is not positive
     */
    /*[deutsch]
     * <p>Erzeugt eine Sequenz von wiederkehrenden r&uuml;ckw&auml;rts laufenden
     * Moment-Intervallen mit der angegebenen Dauer. </p>
     *
     * @param   count       the count of repeating intervals ({@code >= 0})
     * @param   duration    represents the negative duration of every repeating interval
     * @param   end         denotes the end of first interval (exclusive)
     * @param   offset      time zone offset in full minutes
     * @return  sequence of recurrent half-open plain timestamp intervals
     * @throws  IllegalArgumentException if the count is negative or the duration is not positive
     */
    public static IsoRecurrence<MomentInterval> of(
        int count,
        Duration<?> duration,
        Moment end,
        ZonalOffset offset
    ) {

        check(count);

        return new RecurrentMomentIntervals(
            count, TYPE_DURATION_END, end.toZonalTimestamp(offset), offset, duration);

    }

    /**
     * <p>Creates a recurrent sequence of moment intervals having the duration
     * of first timestamp interval in years, months, days, hours, minutes, seconds and nanoseconds. </p>
     *
     * @param   count       the count of repeating intervals ({@code >= 0})
     * @param   start       denotes the start of first interval (inclusive)
     * @param   end         denotes the end of first interval (exclusive)
     * @param   offset      time zone offset in full minutes
     * @return  sequence of recurrent half-open moment intervals
     * @throws  IllegalArgumentException if the count is negative or if start is not before end
     */
    /*[deutsch]
     * <p>Erzeugt eine Sequenz von wiederkehrenden Moment-Intervallen mit der Dauer des ersten Intervalls
     * in Jahren, Monaten, Tagen, Stunden, Minuten, Sekunden und Nanosekunden. </p>
     *
     * @param   count       the count of repeating intervals ({@code >= 0})
     * @param   start       denotes the start of first interval (inclusive)
     * @param   end         denotes the end of first interval (exclusive)
     * @param   offset      time zone offset in full minutes
     * @return  sequence of recurrent half-open moment intervals
     * @throws  IllegalArgumentException if the count is negative or if start is not before end
     */
    public static IsoRecurrence<MomentInterval> of(
        int count,
        Moment start,
        Moment end,
        ZonalOffset offset
    ) {

        check(count);

        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End is not after start.");
        }

        PlainTimestamp s = start.toZonalTimestamp(offset);
        PlainTimestamp e = end.toZonalTimestamp(offset);

        return new RecurrentMomentIntervals(
            count,
            TYPE_START_END,
            s,
            offset,
            Duration.in(YEARS, MONTHS, DAYS, HOURS, MINUTES, SECONDS, NANOS).between(s, e));

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
     * <strong>Special care must be taken to avoid infinite loops or streams.</strong></p>
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
    /*[deutsch]
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
    /*[deutsch]
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
     * @return  parsed sequence of recurrent closed date intervals
     * @throws  ParseException in any case of inconsistencies
     */
    /*[deutsch]
     * <p>Interpretiert einen Text wie &quot;R5/2016-04-01/2016-04-30&quot; oder &quot;R5/2016-04-01/P1M&quot;
     * als eine Sequenz von wiederkehrenden Datumsintervallen. </p>
     *
     * @param   iso     canonical representation of recurrent date intervals
     * @return  parsed sequence of recurrent closed date intervals
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
            PlainDate start = Iso8601Format.parseDate(parts[1]);
            Duration<? extends IsoDateUnit> duration = Duration.parseCalendarPeriod(parts[2]);
            recurrence = IsoRecurrence.of(count, start, duration);
        } else if (parts[1].charAt(0) == 'P') {
            Duration<? extends IsoDateUnit> duration = Duration.parseCalendarPeriod(parts[1]);
            PlainDate end = Iso8601Format.parseDate(parts[2]);
            recurrence = IsoRecurrence.of(count, duration, end);
        } else {
            DateInterval interval = DateInterval.parseISO(iso.substring(getFirstSlash(iso) + 1));
            PlainDate start = interval.getStart().getTemporal();
            PlainDate end = interval.getEnd().getTemporal();
            recurrence = IsoRecurrence.of(count, start, end);
        }

        if (infinite) {
            recurrence = recurrence.withInfiniteCount();
        }

        return recurrence;

    }

    /**
     * <p>Parses a string like &quot;R5/2016-04-01T10:45/2016-04-30T23:59&quot;
     * to a sequence of recurrent timestamp intervals. </p>
     *
     * <p>Supported ISO-formats for timestamp parts are {@link Iso8601Format#BASIC_DATE_TIME}
     * and {@link Iso8601Format#EXTENDED_DATE_TIME}. A duration component will be parsed
     * by {@link Duration#parsePeriod(String)}. </p>
     *
     * @param   iso     canonical representation of recurrent intervals
     * @return  parsed sequence of recurrent half-open timestamp intervals
     * @throws  ParseException in any case of inconsistencies
     */
    /*[deutsch]
     * <p>Interpretiert einen Text wie &quot;R5/2016-04-01T10:45/2016-04-30T23:59&quot;
     * als eine Sequenz von wiederkehrenden Zeit-Intervallen. </p>
     *
     * <p>Unterst&uuml;tzte ISO-Formate f&uuml;r Zeitstempelkomponenten sind
     * {@link Iso8601Format#BASIC_DATE_TIME} und {@link Iso8601Format#EXTENDED_DATE_TIME}.
     * Eine Dauerkomponente wird mittels {@link Duration#parsePeriod(String)}
     * interpretiert. </p>
     *
     * @param   iso     canonical representation of recurrent intervals
     * @return  parsed sequence of recurrent half-open timestamp intervals
     * @throws  ParseException in any case of inconsistencies
     */
    public static IsoRecurrence<TimestampInterval> parseTimestampIntervals(String iso)
        throws ParseException {

        String[] parts = iso.split("/");
        int count = parseCount(parts);
        boolean infinite = false;

        if (count == INFINITE) {
            count = 0;
            infinite = true;
        }

        IsoRecurrence<TimestampInterval> recurrence;

        if (parts[2].charAt(0) == 'P') {
            boolean extended = isExtendedFormat(parts[1]);
            PlainTimestamp start = timestampFormatter(extended).parse(parts[1]);
            Duration<?> duration = Duration.parsePeriod(parts[2]);
            recurrence = IsoRecurrence.of(count, start, duration);
        } else if (parts[1].charAt(0) == 'P') {
            Duration<?> duration = Duration.parsePeriod(parts[1]);
            boolean extended = isExtendedFormat(parts[2]);
            PlainTimestamp end = timestampFormatter(extended).parse(parts[2]);
            recurrence = IsoRecurrence.of(count, duration, end);
        } else {
            TimestampInterval interval = TimestampInterval.parseISO(iso.substring(getFirstSlash(iso) + 1));
            PlainTimestamp start = interval.getStart().getTemporal();
            PlainTimestamp end = interval.getEnd().getTemporal();
            recurrence = IsoRecurrence.of(count, start, end);
        }

        if (infinite) {
            recurrence = recurrence.withInfiniteCount();
        }

        return recurrence;

    }

    /**
     * <p>Parses a string like &quot;R5/2016-04-01T10:45Z/30T23:59&quot;
     * to a sequence of recurrent moment intervals. </p>
     *
     * <p>Supported ISO-formats for moment parts are {@link Iso8601Format#BASIC_DATE_TIME_OFFSET}
     * and {@link Iso8601Format#EXTENDED_DATE_TIME_OFFSET}. A duration component will be parsed
     * by {@link Duration#parsePeriod(String)}. </p>
     *
     * @param   iso     canonical representation of recurrent intervals
     * @return  parsed sequence of recurrent half-open moment intervals
     * @throws  ParseException in any case of inconsistencies
     */
    /*[deutsch]
     * <p>Interpretiert einen Text wie &quot;R5/2016-04-01T10:45Z/30T23:59&quot;
     * als eine Sequenz von wiederkehrenden Moment-Intervallen. </p>
     *
     * <p>Unterst&uuml;tzte ISO-Formate f&uuml;r Zeitstempelkomponenten sind
     * {@link Iso8601Format#BASIC_DATE_TIME_OFFSET} und {@link Iso8601Format#EXTENDED_DATE_TIME_OFFSET}.
     * Eine Dauerkomponente wird mittels {@link Duration#parsePeriod(String)} interpretiert. </p>
     *
     * @param   iso     canonical representation of recurrent intervals
     * @return  parsed sequence of recurrent half-open moment intervals
     * @throws  ParseException in any case of inconsistencies
     */
    public static IsoRecurrence<MomentInterval> parseMomentIntervals(String iso)
        throws ParseException {

        String[] parts = iso.split("/");
        int count = parseCount(parts);
        boolean infinite = false;

        if (count == INFINITE) {
            count = 0;
            infinite = true;
        }

        IsoRecurrence<MomentInterval> recurrence;

        if (parts[2].charAt(0) == 'P') {
            boolean extended = isExtendedFormat(parts[1]);
            ZonalDateTime zdt = ZonalDateTime.parse(parts[1], momentFormatter(extended));
            Duration<?> duration = Duration.parsePeriod(parts[2]);
            recurrence = IsoRecurrence.of(count, zdt.toMoment(), duration, zdt.getOffset());
        } else if (parts[1].charAt(0) == 'P') {
            Duration<?> duration = Duration.parsePeriod(parts[1]);
            boolean extended = isExtendedFormat(parts[2]);
            ZonalDateTime zdt = ZonalDateTime.parse(parts[2], momentFormatter(extended));
            recurrence = IsoRecurrence.of(count, duration, zdt.toMoment(), zdt.getOffset());
        } else {
            String remainder = iso.substring(getFirstSlash(iso) + 1);
            MomentInterval interval = MomentInterval.parseISO(remainder);
            Moment start = interval.getStart().getTemporal();
            Moment end = interval.getEnd().getTemporal();
            ZonalOffset offset = null;
            int signIndex = -1;
            for (int i = 1, n = remainder.length(); i < n; i++) {
                char c = remainder.charAt(i);
                if (c == 'Z') {
                    offset = ZonalOffset.UTC;
                    break;
                } else if ((c == '-') || (c == '+')) {
                    signIndex = i;
                } else if (c == '/') {
                    remainder = remainder.substring(signIndex, i);
                    if (remainder.charAt(3) != ':') {
                        remainder = remainder.substring(0, 3) + ":" + remainder.substring(3);
                    }
                    offset = ZonalOffset.parse(remainder);
                    break;
                }
            }
            recurrence = IsoRecurrence.of(count, start, end, offset);
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

    /**
     * <p>Yields a representation in extended ISO-format. </p>
     *
     * <p>Examples: </p>
     *
     * <pre>
     *     System.out.println(
     *          IsoRecurrence.of(5, PlainDate.of(2016, 8, 12), Duration.of(3, CalendarUnit.WEEKS));
     *     // R5/2016-08-12/P3W
     *     System.out.println(
     *          IsoRecurrence.of(0, PlainTimestamp.of(2016, 8, 12, 10, 45), PlainTimestamp.of(2016, 8, 12, 12, 0)
     *          .withInfiniteCount());
     *     // R/2016-08-12T10:45/2016-08-12T12:00
     * </pre>
     *
     * @return  String
     */
    /*[deutsch]
     * <p>Liefert eine Darstellung im <i>extended</i> ISO-8601-Format. </p>
     *
     * <p>Beispiele: </p>
     *
     * <pre>
     *     System.out.println(
     *          IsoRecurrence.of(5, PlainDate.of(2016, 8, 12), Duration.of(3, CalendarUnit.WEEKS));
     *     // R5/2016-08-12/P3W
     *     System.out.println(
     *          IsoRecurrence.of(0, PlainTimestamp.of(2016, 8, 12, 10, 45), PlainTimestamp.of(2016, 8, 12, 12, 0)
     *          .withInfiniteCount());
     *     // R/2016-08-12T10:45/2016-08-12T12:00
     * </pre>
     *
     * @return  String
     */
    @Override
    public String toString() {

        throw new AbstractMethodError();

    }

    @Override
    public Iterator<I> iterator() {

        throw new AbstractMethodError();

    }

    /**
     * <p>Obtains an ordered stream of recurrent intervals. </p>
     *
     * @return  Stream
     * @since   4.18
     * @see     Spliterator#DISTINCT
     * @see     Spliterator#IMMUTABLE
     * @see     Spliterator#NONNULL
     * @see     Spliterator#ORDERED
     * @see     Spliterator#SIZED
     * @see     Spliterator#SUBSIZED
     */
    /*[deutsch]
     * <p>Erzeugt einen geordneten {@code Stream} von wiederkehrenden Intervallen. </p>
     *
     * @return  Stream
     * @since   4.18
     * @see     Spliterator#DISTINCT
     * @see     Spliterator#IMMUTABLE
     * @see     Spliterator#NONNULL
     * @see     Spliterator#ORDERED
     * @see     Spliterator#SIZED
     * @see     Spliterator#SUBSIZED
     */
    public Stream<I> intervalStream() {

        long size = (this.isInfinite() ? Long.MAX_VALUE : this.getCount());
        int characteristics = DISTINCT | IMMUTABLE | NONNULL | ORDERED | SIZED | SUBSIZED;
        Spliterator<I> spliterator = Spliterators.spliterator(this.iterator(), size, characteristics);
        return StreamSupport.stream(spliterator, false);

    }

    IsoRecurrence<I> copyWithCount(int count) {

        throw new AbstractMethodError();

    }

    int getType() {

        return this.type;

    }

    private static void check(int count) {

        if (count < 0) {
            throw new IllegalArgumentException("Count of recurrent intervals must be postive or zero: " + count);
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
            if (digit >= 0 && digit <= 9) {
                total = total * 10 + digit;
            } else {
                throw new ParseException("Digit 0-9 is missing.", i);
            }
        }

        return total;

    }

    private static boolean isExtendedFormat(String iso) {

        for (int i = 1, n = iso.length(); i < n; i++) {
            char c = iso.charAt(i);
            if (c == 'T') {
                break;
            } else if (c == '-') {
                return true;
            }
        }

        return false;

    }

    private static int getFirstSlash(String iso) {

        for (int i = 0, n = iso.length(); i < n; i++) {
            if (iso.charAt(i) == '/') {
                return i;
            }
        }

        return -1;

    }

    private static ChronoFormatter<PlainTimestamp> timestampFormatter(boolean extended) {

        return (extended ? Iso8601Format.EXTENDED_DATE_TIME : Iso8601Format.BASIC_DATE_TIME);

    }

    private static ChronoFormatter<Moment> momentFormatter(boolean extended) {

        return (extended ? Iso8601Format.EXTENDED_DATE_TIME_OFFSET : Iso8601Format.BASIC_DATE_TIME_OFFSET);

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
                        s = Boundary.ofClosed(next.plus(1, DAYS));
                        e = Boundary.ofClosed(this.current);
                    } else {
                        next = this.current.plus(RecurrentDateIntervals.this.duration);
                        s = Boundary.ofClosed(this.current);
                        e = Boundary.ofClosed(next.minus(1, DAYS));
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
                    sb.append(this.ref.plus(this.duration).minus(1, DAYS));
                    break;
            }

            return sb.toString();

        }

        @Override
        IsoRecurrence<DateInterval> copyWithCount(int count) {

            return new RecurrentDateIntervals(count, this.getType(), this.ref, this.duration);

        }

    }

    private static class RecurrentTimestampIntervals
        extends IsoRecurrence<TimestampInterval> {

        //~ Statische Felder/Initialisierungen ----------------------------

        //~ Instanzvariablen ----------------------------------------------

        private final PlainTimestamp ref;
        private final Duration<?> duration;

        //~ Konstruktoren -------------------------------------------------

        private RecurrentTimestampIntervals(
            int count,
            int type,
            PlainTimestamp ref,
            Duration<?> duration
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
        public Iterator<TimestampInterval> iterator() {
            return new ReadOnlyIterator<TimestampInterval, RecurrentTimestampIntervals>(this) {
                private PlainTimestamp current = RecurrentTimestampIntervals.this.ref;
                @Override
                protected TimestampInterval nextInterval() {
                    PlainTimestamp next;
                    Boundary<PlainTimestamp> s;
                    Boundary<PlainTimestamp> e;
                    if (RecurrentTimestampIntervals.this.isBackwards()) {
                        next = this.current.minus(RecurrentTimestampIntervals.this.duration);
                        s = Boundary.ofClosed(next);
                        e = Boundary.ofOpen(this.current);
                    } else {
                        next = this.current.plus(RecurrentTimestampIntervals.this.duration);
                        s = Boundary.ofClosed(this.current);
                        e = Boundary.ofOpen(next);
                    }
                    this.current = next;
                    return TimestampIntervalFactory.INSTANCE.between(s, e);
                }
            };
        }

        @Override
        public boolean equals(Object obj) {

            if (super.equals(obj)) {
                RecurrentTimestampIntervals that = (RecurrentTimestampIntervals) obj;
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
                    sb.append(this.ref.plus(this.duration));
                    break;
            }

            return sb.toString();

        }

        @Override
        IsoRecurrence<TimestampInterval> copyWithCount(int count) {

            return new RecurrentTimestampIntervals(count, this.getType(), this.ref, this.duration);

        }

    }

    private static class RecurrentMomentIntervals
        extends IsoRecurrence<MomentInterval> {

        //~ Statische Felder/Initialisierungen ----------------------------

        //~ Instanzvariablen ----------------------------------------------

        private final PlainTimestamp ref;
        private final ZonalOffset offset;
        private final Duration<?> duration;

        //~ Konstruktoren -------------------------------------------------

        private RecurrentMomentIntervals(
            int count,
            int type,
            PlainTimestamp ref,
            ZonalOffset offset,
            Duration<?> duration
        ) {
            super(count, type);

            this.ref = ref;
            this.offset = offset;
            this.duration = duration;

            if (!duration.isPositive()) {
                throw new IllegalArgumentException("Duration must be positive: " + duration);
            } else if ((offset.getIntegralAmount() % 60 != 0) || (offset.getFractionalAmount() != 0)) {
                throw new IllegalArgumentException("Offset with seconds is invalid in ISO-8601: " + offset);
            }

        }

        //~ Methoden ----------------------------------------------------------

        @Override
        public Iterator<MomentInterval> iterator() {
            return new ReadOnlyIterator<MomentInterval, RecurrentMomentIntervals>(this) {
                private PlainTimestamp current = RecurrentMomentIntervals.this.ref;
                private ZonalOffset offset = RecurrentMomentIntervals.this.offset;
                @Override
                protected MomentInterval nextInterval() {
                    PlainTimestamp next;
                    Boundary<Moment> s;
                    Boundary<Moment> e;
                    if (RecurrentMomentIntervals.this.isBackwards()) {
                        next = this.current.minus(RecurrentMomentIntervals.this.duration);
                        s = Boundary.ofClosed(next.at(offset));
                        e = Boundary.ofOpen(this.current.at(offset));
                    } else {
                        next = this.current.plus(RecurrentMomentIntervals.this.duration);
                        s = Boundary.ofClosed(this.current.at(offset));
                        e = Boundary.ofOpen(next.at(offset));
                    }
                    this.current = next;
                    return MomentIntervalFactory.INSTANCE.between(s, e);
                }
            };
        }

        @Override
        public boolean equals(Object obj) {

            if (super.equals(obj)) {
                RecurrentMomentIntervals that = (RecurrentMomentIntervals) obj;
                return (
                    this.ref.equals(that.ref)
                    && this.duration.equals(that.duration)
                    && this.offset.equals(that.offset));
            }

            return false;

        }

        @Override
        public int hashCode() {

            return super.hashCode()
                + 7 * this.ref.hashCode()
                + 31 * this.offset.hashCode()
                + 37 * this.duration.hashCode();

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
                    sb.append(this.getOffsetAsString());
                    sb.append('/');
                    sb.append(this.duration);
                    break;
                case TYPE_DURATION_END:
                    sb.append(this.duration);
                    sb.append('/');
                    sb.append(this.ref);
                    sb.append(this.getOffsetAsString());
                    break;
                case TYPE_START_END:
                    sb.append(this.ref);
                    sb.append(this.getOffsetAsString());
                    sb.append('/');
                    sb.append(this.ref.plus(this.duration));
                    sb.append(this.getOffsetAsString());
                    break;
            }

            return sb.toString();

        }

        @Override
        IsoRecurrence<MomentInterval> copyWithCount(int count) {

            return new RecurrentMomentIntervals(count, this.getType(), this.ref, this.offset, this.duration);

        }

        private String getOffsetAsString() {

            if ((this.offset.getIntegralAmount() == 0) && (this.offset.getFractionalAmount() == 0)) {
                return "Z";
            }

            return this.offset.toString();

        }

    }

}