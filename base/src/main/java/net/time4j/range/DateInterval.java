/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (DateInterval.java) is part of project Time4J.
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
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.Weekcycle;
import net.time4j.Weekday;
import net.time4j.Weekmodel;
import net.time4j.base.GregorianMath;
import net.time4j.base.TimeSource;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.EpochDays;
import net.time4j.engine.TimeSpan;
import net.time4j.format.Attributes;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.ChronoParser;
import net.time4j.format.expert.ChronoPrinter;
import net.time4j.format.expert.Iso8601Format;
import net.time4j.format.expert.IsoDateStyle;
import net.time4j.format.expert.ParseLog;
import net.time4j.format.expert.PatternType;
import net.time4j.format.expert.SignPolicy;
import net.time4j.tz.GapResolver;
import net.time4j.tz.OverlapResolver;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Spliterator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static net.time4j.PlainDate.*;
import static net.time4j.range.IntervalEdge.CLOSED;
import static net.time4j.range.IntervalEdge.OPEN;


/**
 * <p>Defines a date interval. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 */
/*[deutsch]
 * <p>Definiert ein Datumsintervall. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 */
public final class DateInterval
    extends IsoInterval<PlainDate, DateInterval>
    implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * Constant for a date interval from infinite past to infinite future.
     *
     * @since   3.31/4.26
     */
    /*[deutsch]
     * Konstante f&uuml;r ein Datumsintervall, das von der unbegrenzten Vergangenheit
     * bis in die unbegrenzte Zukunft reicht.
     *
     * @since   3.31/4.26
     */
    public static final DateInterval ALWAYS =
        DateIntervalFactory.INSTANCE.between(Boundary.infinitePast(), Boundary.infiniteFuture());

    private static final long serialVersionUID = 8074261825266036014L;

    private static final Comparator<ChronoInterval<PlainDate>> COMPARATOR =
        new IntervalComparator<>(PlainDate.axis());
    private static final ChronoPrinter<PlainDate> REDUCED_DD =
        ChronoFormatter.ofDatePattern("dd", PatternType.CLDR, Locale.ROOT);
    private static final ChronoPrinter<PlainDate> REDUCED_MMDD =
        ChronoFormatter.ofDatePattern("MMdd", PatternType.CLDR, Locale.ROOT);
    private static final ChronoPrinter<PlainDate> REDUCED_MM_DD =
        ChronoFormatter.ofDatePattern("MM-dd", PatternType.CLDR, Locale.ROOT);
    private static final ChronoPrinter<PlainDate> REDUCED_E =
        ChronoFormatter.ofDatePattern("e", PatternType.CLDR, Locale.ROOT);
    private static final ChronoPrinter<PlainDate> REDUCED_W_WWE =
        ChronoFormatter.ofDatePattern("'W'wwe", PatternType.CLDR, Locale.ROOT);
    private static final ChronoPrinter<PlainDate> REDUCED_W_WW_E =
        ChronoFormatter.ofDatePattern("'W'ww-e", PatternType.CLDR, Locale.ROOT);
    private static final ChronoPrinter<PlainDate> REDUCED_DDD =
        ChronoFormatter.ofDatePattern("DDD", PatternType.CLDR, Locale.ROOT);

    private static final ChronoPrinter<Integer> NOOP =
        (formattable, buffer, attributes) -> Collections.emptySet();

    //~ Konstruktoren -----------------------------------------------------

    // package-private
    DateInterval(
        Boundary<PlainDate> start,
        Boundary<PlainDate> end
    ) {
        super(start, end);

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Defines a comparator which sorts intervals first
     * by start boundary and then by length. </p>
     *
     * @return  Comparator
     * @since   2.0
     */
    /*[deutsch]
     * <p>Definiert ein Vergleichsobjekt, das Intervalle zuerst nach dem
     * Start und dann nach der L&auml;nge sortiert. </p>
     *
     * @return  Comparator
     * @since   2.0
     */
    public static Comparator<ChronoInterval<PlainDate>> comparator() {

        return COMPARATOR;

    }

    /**
     * <p>Creates a closed interval between given dates. </p>
     *
     * @param   start   date of lower boundary (inclusive)
     * @param   end     date of upper boundary (inclusive)
     * @return  new date interval
     * @throws  IllegalArgumentException if start is after end
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt ein geschlossenes Intervall zwischen den angegebenen
     * Datumswerten. </p>
     *
     * @param   start   date of lower boundary (inclusive)
     * @param   end     date of upper boundary (inclusive)
     * @return  new date interval
     * @throws  IllegalArgumentException if start is after end
     * @since   2.0
     */
    public static DateInterval between(
        PlainDate start,
        PlainDate end
    ) {

        return new DateInterval(
            Boundary.of(CLOSED, start),
            Boundary.of(CLOSED, end));

    }

    /**
     * <p>Creates a closed interval between given dates. </p>
     *
     * @param   start   date of lower boundary (inclusive)
     * @param   end     date of upper boundary (inclusive)
     * @return  new date interval
     * @throws  IllegalArgumentException if start is after end
     * @see     #between(PlainDate, PlainDate)
     * @since   4.11
     */
    /*[deutsch]
     * <p>Erzeugt ein geschlossenes Intervall zwischen den angegebenen Datumswerten. </p>
     *
     * @param   start   date of lower boundary (inclusive)
     * @param   end     date of upper boundary (inclusive)
     * @return  new date interval
     * @throws  IllegalArgumentException if start is after end
     * @see     #between(PlainDate, PlainDate)
     * @since   4.11
     */
    public static DateInterval between(
        LocalDate start,
        LocalDate end
    ) {

        return DateInterval.between(PlainDate.from(start), PlainDate.from(end));

    }

    /**
     * <p>Creates an infinite interval since given start date. </p>
     *
     * @param   start   date of lower boundary (inclusive)
     * @return  new date interval
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt ein unbegrenztes Intervall ab dem angegebenen
     * Startdatum. </p>
     *
     * @param   start   date of lower boundary (inclusive)
     * @return  new date interval
     * @since   2.0
     */
    public static DateInterval since(PlainDate start) {

        Boundary<PlainDate> future = Boundary.infiniteFuture();
        return new DateInterval(Boundary.of(CLOSED, start), future);

    }

    /**
     * <p>Creates an infinite interval since given start date. </p>
     *
     * @param   start   date of lower boundary (inclusive)
     * @return  new date interval
     * @see     #since(PlainDate)
     * @since   4.11
     */
    /*[deutsch]
     * <p>Erzeugt ein unbegrenztes Intervall ab dem angegebenen Startdatum. </p>
     *
     * @param   start   date of lower boundary (inclusive)
     * @return  new date interval
     * @see     #since(PlainDate)
     * @since   4.11
     */
    public static DateInterval since(LocalDate start) {

        return DateInterval.since(PlainDate.from(start));

    }

    /**
     * <p>Creates an infinite interval until given end date. </p>
     *
     * @param   end     date of upper boundary (inclusive)
     * @return  new date interval
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt ein unbegrenztes Intervall bis zum angegebenen
     * Endedatum. </p>
     *
     * @param   end     date of upper boundary (inclusive)
     * @return  new date interval
     * @since   2.0
     */
    public static DateInterval until(PlainDate end) {

        Boundary<PlainDate> past = Boundary.infinitePast();
        return new DateInterval(past, Boundary.of(CLOSED, end));

    }

    /**
     * <p>Creates an infinite interval until given end date. </p>
     *
     * @param   end     date of upper boundary (inclusive)
     * @return  new date interval
     * @see     #until(PlainDate)
     * @since   4.11
     */
    /*[deutsch]
     * <p>Erzeugt ein unbegrenztes Intervall bis zum angegebenen
     * Endedatum. </p>
     *
     * @param   end     date of upper boundary (inclusive)
     * @return  new date interval
     * @see     #until(PlainDate)
     * @since   4.11
     */
    public static DateInterval until(LocalDate end) {

        return DateInterval.until(PlainDate.from(end));

    }

    /**
     * <p>Creates a closed interval including only given date. </p>
     *
     * @param   date    single contained date
     * @return  new date interval
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt ein geschlossenes Intervall, das nur das angegebene
     * Datum enth&auml;lt. </p>
     *
     * @param   date    single contained date
     * @return  new date interval
     * @since   2.0
     */
    public static DateInterval atomic(PlainDate date) {

        return between(date, date);

    }

    /**
     * <p>Creates a closed interval including only given date. </p>
     *
     * @param   date    single contained date
     * @return  new date interval
     * @see     #atomic(PlainDate)
     * @since   4.11
     */
    /*[deutsch]
     * <p>Erzeugt ein geschlossenes Intervall, das nur das angegebene
     * Datum enth&auml;lt. </p>
     *
     * @param   date    single contained date
     * @return  new date interval
     * @see     #atomic(PlainDate)
     * @since   4.11
     */
    public static DateInterval atomic(LocalDate date) {

        return DateInterval.atomic(PlainDate.from(date));

    }

    /**
     * <p>Creates an empty interval with an anchor date. </p>
     *
     * <p>An empty interval does not contain any date but the anchor can modifiy the behaviour
     * of methods like {@link IsoInterval#meets(IsoInterval)}. The lower interval boundary is closed,
     * and the upper interval boundary is open. </p>
     *
     * @param   anchor      the anchor date
     * @return  new empty date interval with given anchor date
     * @since   4.37
     */
    /*[deutsch]
     * <p>Erzeugt ein leeres Intervall mit einem Ankerdatum. </p>
     *
     * <p>Ein leeres Intervall enth&auml;lt kein Datum, aber der Anker kann das Verhalten von Methoden
     * wie {@link IsoInterval#meets(IsoInterval)} modifizieren. Die untere Intervallgrenze ist geschlossen,
     * aber die obere Intervallgrenze ist offen. </p>
     *
     * @param   anchor      the anchor date
     * @return  new empty date interval with given anchor date
     * @since   4.37
     */
    public static DateInterval emptyWithAnchor(PlainDate anchor) {

        return new DateInterval(
            Boundary.of(CLOSED, anchor),
            Boundary.of(OPEN, anchor));

    }

    /**
     * <p>Creates an empty interval with an anchor date. </p>
     *
     * @param   anchor      the anchor date
     * @return  new empty date interval with given anchor date
     * @see     #emptyWithAnchor(PlainDate)
     * @since   4.37
     */
    /*[deutsch]
     * <p>Erzeugt ein leeres Intervall mit einem Ankerdatum. </p>
     *
     * @param   anchor      the anchor date
     * @return  new empty date interval with given anchor date
     * @see     #emptyWithAnchor(PlainDate)
     * @since   4.37
     */
    public static DateInterval emptyWithAnchor(LocalDate anchor) {

        return DateInterval.emptyWithAnchor(PlainDate.from(anchor));

    }

    /**
     * <p>Obtains the current calendar week based on given clock, time zone and first day of week. </p>
     *
     * <p>A localized first day of week can be obtained by the expression
     * {@code Weekmodel.of(Locale.getDefault()).getFirstDayOfWeek()}. The
     * next week can be found by applying {@code move(1, CalendarUnit.DAYS)}
     * on the result of this method. If the first day of week is Monday then
     * users should consider the alternative {@link CalendarWeek ISO calendar week}. </p>
     *
     * @param   clock       the clock for evaluating the current calendar week
     * @param   tzid        time zone in which the calendar week is valid
     * @param   firstDay    first day of week
     * @return  the current calendar week as {@code DateInterval}
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @see     net.time4j.SystemClock#INSTANCE
     * @see     Timezone#getID()
     * @see     Weekmodel#getFirstDayOfWeek()
     * @see     #move(long, IsoDateUnit)
     * @since   5.4
     */
    /*[deutsch]
     * <p>Liefert die aktuelle Kalenderwoche, die auf den angegebenen Parametern wie Uhr, Zeitzone
     * und Wochenanfang beruht. </p>
     *
     * <p>Ein lokalisierter Wochenanfang kann mittels des Ausdrucks
     * {@code Weekmodel.of(Locale.getDefault()).getFirstDayOfWeek()}
     * ermittelt werden. Die Folgewoche kann mittels {@code move(1, CalendarUnit.DAYS)}
     * angewendet auf das Ergebnis gefunden werden. Wenn der erste Tag der Woche der
     * Montag ist, sollte die Verwendung der Alternative {@link CalendarWeek ISO calendar week}
     * in Betracht gezogen werden. </p>
     *
     * @param   clock       the clock for evaluating the current calendar week
     * @param   tzid        time zone in which the calendar week is valid
     * @param   firstDay    first day of week
     * @return  the current calendar week as {@code DateInterval}
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @see     net.time4j.SystemClock#INSTANCE
     * @see     Timezone#getID()
     * @see     Weekmodel#getFirstDayOfWeek()
     * @see     #move(long, IsoDateUnit)
     * @since   5.4
     */
    public static DateInterval ofCurrentWeek(
        TimeSource clock,
        TZID tzid,
        Weekday firstDay
    ) {

        PlainDate today = Moment.from(clock.currentTime()).toZonalTimestamp(tzid).toDate();
        PlainDate start = today.with(PlainDate.DAY_OF_WEEK.setToPreviousOrSame(firstDay));
        PlainDate end = start.plus(6, CalendarUnit.DAYS);
        return DateInterval.between(start, end);

    }

    /**
     * <p>Converts an arbitrary date interval to an interval of this type. </p>
     *
     * @param   interval    any kind of date interval
     * @return  DateInterval
     * @since   3.28/4.24
     */
    /*[deutsch]
     * <p>Konvertiert ein beliebiges Datumsintervall zu einem Intervall dieses Typs. </p>
     *
     * @param   interval    any kind of date interval
     * @return  DateInterval
     * @since   3.28/4.24
     */
    public static DateInterval from(ChronoInterval<PlainDate> interval) {

        if (interval instanceof DateInterval) {
            return DateInterval.class.cast(interval);
        } else {
            return new DateInterval(interval.getStart(), interval.getEnd());
        }

    }

    /**
     * <p>Yields the start date. </p>
     *
     * @return  start date or {@code null} if infinite
     * @see     Boundary#isInfinite()
     * @since   4.11
     */
    /*[deutsch]
     * <p>Liefert das Startdatum. </p>
     *
     * @return  start date or {@code null} if infinite
     * @see     Boundary#isInfinite()
     * @since   4.11
     */
    public PlainDate getStartAsCalendarDate() {

        return this.getStart().getTemporal();

    }

    /**
     * <p>Yields the start date. </p>
     *
     * @return  start date or {@code null} if infinite
     * @see     Boundary#isInfinite()
     * @since   4.11
     */
    /*[deutsch]
     * <p>Liefert das Startdatum. </p>
     *
     * @return  start date or {@code null} if infinite
     * @see     Boundary#isInfinite()
     * @since   4.11
     */
    public LocalDate getStartAsLocalDate() {

        PlainDate date = this.getStartAsCalendarDate();
        return ((date == null) ? null : date.toTemporalAccessor());

    }

    /**
     * <p>Yields the end date. </p>
     *
     * @return  end date or {@code null} if infinite
     * @see     Boundary#isInfinite()
     * @since   4.11
     */
    /*[deutsch]
     * <p>Liefert das Endedatum. </p>
     *
     * @return  end date or {@code null} if infinite
     * @see     Boundary#isInfinite()
     * @since   4.11
     */
    public PlainDate getEndAsCalendarDate() {

        return this.getEnd().getTemporal();

    }

    /**
     * <p>Yields the end date. </p>
     *
     * @return  end date or {@code null} if infinite
     * @see     Boundary#isInfinite()
     * @since   4.11
     */
    /*[deutsch]
     * <p>Liefert das Endedatum. </p>
     *
     * @return  end date or {@code null} if infinite
     * @see     Boundary#isInfinite()
     * @since   4.11
     */
    public LocalDate getEndAsLocalDate() {

        PlainDate date = this.getEndAsCalendarDate();
        return ((date == null) ? null : date.toTemporalAccessor());

    }

    /**
     * <p>Converts this instance to a timestamp interval with
     * dates from midnight to midnight. </p>
     *
     * <p>The resulting interval is half-open if this interval is finite. </p>
     *
     * @return  timestamp interval (from midnight to midnight)
     * @since   2.0
     */
    /*[deutsch]
     * <p>Wandelt diese Instanz in ein Zeitstempelintervall
     * mit Datumswerten von Mitternacht zu Mitternacht um. </p>
     *
     * <p>Das Ergebnisintervall ist halb-offen, wenn dieses Intervall
     * endlich ist. </p>
     *
     * @return  timestamp interval (from midnight to midnight)
     * @since   2.0
     */
    public TimestampInterval toFullDays() {

        Boundary<PlainTimestamp> b1;
        Boundary<PlainTimestamp> b2;

        if (this.getStart().isInfinite()) {
            b1 = Boundary.infinitePast();
        } else {
            PlainDate d1 = this.getStart().getTemporal();
            PlainTimestamp t1;
            if (this.getStart().isOpen()) {
                t1 = d1.at(PlainTime.midnightAtEndOfDay());
            } else {
                t1 = d1.atStartOfDay();
            }
            b1 = Boundary.of(IntervalEdge.CLOSED, t1);
        }

        if (this.getEnd().isInfinite()) {
            b2 = Boundary.infiniteFuture();
        } else {
            PlainDate d2 = this.getEnd().getTemporal();
            PlainTimestamp t2;
            if (this.getEnd().isOpen()) {
                t2 = d2.atStartOfDay();
            } else {
                t2 = d2.at(PlainTime.midnightAtEndOfDay());
            }
            b2 = Boundary.of(IntervalEdge.OPEN, t2);
        }

        return new TimestampInterval(b1, b2);

    }

    /**
     * <p>Converts this instance to a moment interval with date boundaries mapped
     * to the midnight cycle in given time zone. </p>
     *
     * <p>The resulting interval is half-open if this interval is finite. Note that sometimes
     * the moments of result intervals can deviate from midnight if midnight does not exist
     * due to daylight saving effects. </p>
     *
     * @param   tzid        timezone identifier
     * @return  global timestamp intervall interpreted in given timezone
     * @see     GapResolver#NEXT_VALID_TIME
     * @see     OverlapResolver#EARLIER_OFFSET
     * @since   3.23/4.19
     */
    /*[deutsch]
     * <p>Kombiniert dieses Datumsintervall mit der angegebenen
     * Zeitzone zu einem globalen UTC-Intervall, indem die Momente
     * den Mitternachtszyklus abbilden. </p>
     *
     * <p>Das Ergebnisintervall ist halb-offen, wenn dieses Intervall endlich ist. Hinweis:
     * Manchmal sind die Momentgrenzen von Mitternacht verschieden, n&auml;mlich dann, wenn
     * wegen Sommerzeitumstellungen Mitternacht nicht vorhanden ist. </p>
     *
     * @param   tzid        timezone identifier
     * @return  global timestamp intervall interpreted in given timezone
     * @see     GapResolver#NEXT_VALID_TIME
     * @see     OverlapResolver#EARLIER_OFFSET
     * @since   3.23/4.19
     */
    public MomentInterval inTimezone(TZID tzid) {

        return this.toFullDays().in(
            Timezone.of(tzid).with(GapResolver.NEXT_VALID_TIME.and(OverlapResolver.EARLIER_OFFSET)));

    }

    /**
     * <p>Yields the length of this interval in days. </p>
     *
     * @return  duration in days as long primitive
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   2.0
     * @see     #getDurationInYearsMonthsDays()
     * @see     #getDuration(CalendarUnit[]) getDuration(CalendarUnit...)
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge dieses Intervalls in Tagen. </p>
     *
     * @return  duration in days as long primitive
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   2.0
     * @see     #getDurationInYearsMonthsDays()
     * @see     #getDuration(CalendarUnit[]) getDuration(CalendarUnit...)
     */
    public long getLengthInDays() {

        if (this.isFinite()) {
            long days = CalendarUnit.DAYS.between(
                this.getStart().getTemporal(),
                this.getEnd().getTemporal());
            if (this.getStart().isOpen()) {
                days--;
            }
            if (this.getEnd().isClosed()) {
                days++;
            }
            return days;
        } else {
            throw new UnsupportedOperationException(
                "An infinite interval has no finite duration.");
        }

    }

    /**
     * <p>Yields the length of this interval in years, months and days. </p>
     *
     * @return  duration in years, months and days
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   2.0
     * @see     #getLengthInDays()
     * @see     #getDuration(CalendarUnit[]) getDuration(CalendarUnit...)
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge dieses Intervalls in Jahren, Monaten und
     * Tagen. </p>
     *
     * @return  duration in years, months and days
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   2.0
     * @see     #getLengthInDays()
     * @see     #getDuration(CalendarUnit[]) getDuration(CalendarUnit...)
     */
    public Duration<CalendarUnit> getDurationInYearsMonthsDays() {

        PlainDate date = this.getTemporalOfOpenEnd();
        boolean max = (date == null);

        if (max) { // max reached
            date = this.getEnd().getTemporal();
        }

        Duration<CalendarUnit> result =
            Duration.inYearsMonthsDays().between(
                this.getTemporalOfClosedStart(),
                date);

        if (max) {
            return result.plus(1, CalendarUnit.DAYS);
        }

        return result;

    }

    /**
     * <p>Yields the length of this interval in given calendrical units. </p>
     *
     * @param   units   calendrical units as calculation base
     * @return  duration in given units
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   2.0
     * @see     #getLengthInDays()
     * @see     #getDurationInYearsMonthsDays()
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge dieses Intervalls in den angegebenen
     * kalendarischen Zeiteinheiten. </p>
     *
     * @param   units   calendrical units as calculation base
     * @return  duration in given units
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   2.0
     * @see     #getLengthInDays()
     * @see     #getDurationInYearsMonthsDays()
     */
    public Duration<CalendarUnit> getDuration(CalendarUnit... units) {

        PlainDate date = this.getTemporalOfOpenEnd();
        boolean max = (date == null);

        if (max) { // max reached
            date = this.getEnd().getTemporal();
        }

        Duration<CalendarUnit> result =
            Duration.in(units).between(
                this.getTemporalOfClosedStart(),
                date);

        if (max) {
            return result.plus(1, CalendarUnit.DAYS);
        }

        return result;

    }

    /**
     * <p>Moves this interval along the time axis by given units. </p>
     *
     * @param   amount  amount of units
     * @param   unit    time unit for moving
     * @return  moved copy of this interval
     * @since   3.37/4.32
     */
    /*[deutsch]
     * <p>Versetzt dieses Intervall entlang der Datumsachse um die angegebenen Zeiteinheiten. </p>
     *
     * @param   amount  amount of units
     * @param   unit    time unit for moving
     * @return  moved copy of this interval
     * @since   3.37/4.32
     */
    public DateInterval move(
        long amount,
        IsoDateUnit unit
    ) {

        if (amount == 0) {
            return this;
        }

        Boundary<PlainDate> s;
        Boundary<PlainDate> e;

        if (this.getStart().isInfinite()) {
            s = Boundary.infinitePast();
        } else {
            s =
                Boundary.of(
                    this.getStart().getEdge(),
                    this.getStart().getTemporal().plus(amount, unit));
        }

        if (this.getEnd().isInfinite()) {
            e = Boundary.infiniteFuture();
        } else {
            e =
                Boundary.of(
                    this.getEnd().getEdge(),
                    this.getEnd().getTemporal().plus(amount, unit));
        }

        return new DateInterval(s, e);

    }

    /**
     * <p>Obtains a stream iterating over every calendar date of the canonical form of this interval. </p>
     *
     * @return  daily stream
     * @throws  IllegalStateException if this interval is infinite or if there is no canonical form
     * @see     #toCanonical()
     * @see     #streamDaily(PlainDate, PlainDate)
     * @since   4.18
     */
    /*[deutsch]
     * <p>Erzeugt einen {@code Stream}, der &uuml;ber jedes Kalenderdatum der kanonischen Form
     * dieses Intervalls geht. </p>
     *
     * @return  daily stream
     * @throws  IllegalStateException if this interval is infinite or if there is no canonical form
     * @see     #toCanonical()
     * @see     #streamDaily(PlainDate, PlainDate)
     * @since   4.18
     */
    public Stream<PlainDate> streamDaily() {

        if (this.isEmpty()) {
            return Stream.empty();
        }

        DateInterval interval = this.toCanonical();
        PlainDate start = interval.getStartAsCalendarDate();
        PlainDate end = interval.getEndAsCalendarDate();

        if ((start == null) || (end == null)) {
            throw new IllegalStateException("Streaming is not supported for infinite intervals.");
        }

        return StreamSupport.stream(new DailySpliterator(start, end), false);

    }

    /**
     * <p>Obtains a stream iterating over every calendar date between given interval boundaries. </p>
     *
     * <p>This static method avoids the costs of constructing an instance of {@code DateInterval}. </p>
     *
     * @param   start       start boundary - inclusive
     * @param   end         end boundary - inclusive
     * @throws  IllegalArgumentException if start is after end
     * @return  daily stream
     * @see     #streamDaily()
     * @since   4.18
     */
    /*[deutsch]
     * <p>Erzeugt einen {@code Stream}, der &uuml;ber jedes Kalenderdatum zwischen den angegebenen
     * Intervallgrenzen geht. </p>
     *
     * <p>Diese statische Methode vermeidet die Kosten der Intervallerzeugung. </p>
     *
     * @param   start       start boundary - inclusive
     * @param   end         end boundary - inclusive
     * @throws  IllegalArgumentException if start is after end
     * @return  daily stream
     * @see     #streamDaily()
     * @since   4.18
     */
    public static Stream<PlainDate> streamDaily(
        PlainDate start,
        PlainDate end
    ) {

        long s = start.getDaysSinceEpochUTC();
        long e = end.getDaysSinceEpochUTC();

        if (s > e) {
            throw new IllegalArgumentException("Start after end: " + start + "/" + end);
        }

        return StreamSupport.stream(new DailySpliterator(start, s, e), false);

    }

    /**
     * <p>Obtains a stream iterating over every calendar date which is the result of addition of given duration
     * to start until the end of this interval is reached. </p>
     *
     * @param   duration    duration which has to be added to the start multiple times
     * @throws  IllegalArgumentException if the duration is not positive
     * @throws  IllegalStateException if this interval is infinite or if there is no canonical form
     * @return  stream consisting of distinct dates which are the result of adding the duration to the start
     * @see     #toCanonical()
     * @see     #stream(Duration, PlainDate, PlainDate)
     * @since   4.18
     */
    /*[deutsch]
     * <p>Erzeugt einen {@code Stream}, der jeweils ein Kalenderdatum als Vielfaches der Dauer angewandt auf
     * den Start und bis zum Ende dieses Intervalls geht. </p>
     *
     * @param   duration    duration which has to be added to the start multiple times
     * @throws  IllegalArgumentException if the duration is not positive
     * @throws  IllegalStateException if this interval is infinite or if there is no canonical form
     * @return  stream consisting of distinct dates which are the result of adding the duration to the start
     * @see     #toCanonical()
     * @see     #stream(Duration, PlainDate, PlainDate)
     * @since   4.18
     */
    public Stream<PlainDate> stream(Duration<CalendarUnit> duration) {

        if (this.isEmpty() && duration.isPositive()) {
            return Stream.empty();
        }

        DateInterval interval = this.toCanonical();
        PlainDate start = interval.getStartAsCalendarDate();
        PlainDate end = interval.getEndAsCalendarDate();

        if ((start == null) || (end == null)) {
            throw new IllegalStateException("Streaming is not supported for infinite intervals.");
        }

        return DateInterval.stream(duration, start, end);

    }

    /**
     * <p>Obtains a stream iterating over every calendar date which is the result of addition of given duration
     * to start until the end is reached. </p>
     *
     * <p>This static method avoids the costs of constructing an instance of {@code DateInterval}. </p>
     *
     * @param   duration    duration which has to be added to the start multiple times
     * @param   start       start boundary - inclusive
     * @param   end         end boundary - inclusive
     * @throws  IllegalArgumentException if start is after end or if the duration is not positive
     * @return  stream consisting of distinct dates which are the result of adding the duration to the start
     * @since   4.18
     */
    /*[deutsch]
     * <p>Erzeugt einen {@code Stream}, der jeweils ein Kalenderdatum als Vielfaches der Dauer angewandt auf
     * den Start und bis zum Ende geht. </p>
     *
     * <p>Diese statische Methode vermeidet die Kosten der Intervallerzeugung. </p>
     *
     * @param   duration    duration which has to be added to the start multiple times
     * @param   start       start boundary - inclusive
     * @param   end         end boundary - inclusive
     * @throws  IllegalArgumentException if start is after end or if the duration is not positive
     * @return  stream consisting of distinct dates which are the result of adding the duration to the start
     * @since   4.18
     */
    public static Stream<PlainDate> stream(
        Duration<CalendarUnit> duration,
        PlainDate start,
        PlainDate end
    ) {

        if (!duration.isPositive()) {
            throw new IllegalArgumentException("Duration must be positive: " + duration);
        }

        long months = 0;
        long days = 0;

        for (TimeSpan.Item<CalendarUnit> item : duration.getTotalLength()) {
            long amount = item.getAmount();
            switch (item.getUnit()) {
                case MILLENNIA:
                    months = Math.addExact(months, Math.multiplyExact(1000 * 12, amount));
                    break;
                case CENTURIES:
                    months = Math.addExact(months, Math.multiplyExact(100 * 12, amount));
                    break;
                case DECADES:
                    months = Math.addExact(months, Math.multiplyExact(10 * 12, amount));
                    break;
                case YEARS:
                    months = Math.addExact(months, Math.multiplyExact(12, amount));
                    break;
                case QUARTERS:
                    months = Math.addExact(months, Math.multiplyExact(3, amount));
                    break;
                case MONTHS:
                    months = Math.addExact(months, amount);
                    break;
                case WEEKS:
                    days = Math.addExact(days, Math.multiplyExact(7, amount));
                    break;
                case DAYS:
                    days = Math.addExact(days, amount);
                    break;
                default:
                    throw new UnsupportedOperationException(item.getUnit().name());
            }
        }

        final long eMonths = months;
        final long eDays = days;

        if ((eMonths == 0) && (eDays == 1)) {
            return DateInterval.streamDaily(start, end);
        }

        long s = start.getDaysSinceEpochUTC();
        long e = end.getDaysSinceEpochUTC();

        if (s > e) {
            throw new IllegalArgumentException("Start after end: " + start + "/" + end);
        }

        long n = 1 + ((e - s) / (Math.addExact(Math.multiplyExact(eMonths, 31), eDays))); // first estimate
        PlainDate date;
        long size;

        do {
            size = n;
            long m = Math.multiplyExact(eMonths, n);
            long d = Math.multiplyExact(eDays, n);
            date = start.plus(m, CalendarUnit.MONTHS).plus(d, CalendarUnit.DAYS);
            n++;
        } while (!date.isAfter(end));

        if (size == 1) {
            return Stream.of(start); // short-cut
        }

        return LongStream.range(0, size).mapToObj(
            index -> start.plus(eMonths * index, CalendarUnit.MONTHS).plus(eDays * index, CalendarUnit.DAYS));

    }

    /**
     * <p>Obtains a stream iterating over every calendar date of the canonical form of this interval
     * and applies given exclusion filter. </p>
     *
     * <p>Example of exclusion of Saturday and Sunday: </p>
     *
     * <pre>
     *     DateInterval.between(
     *       PlainDate.of(2017, 2, 1),
     *       PlainDate.of(2017, 2, 8)
     *     ).streamExcluding(Weekday.SATURDAY.or(Weekday.SUNDAY)).forEach(System.out::println);
     * </pre>
     *
     * <p>All objects whose type is a subclass of {@code ChronoCondition} can be also used as parameter, for example
     * localized weekends by the expression {@code Weekmodel.of(locale).weekend()}. </p>
     *
     * @param   exclusion   filter as predicate
     * @return  daily filtered stream
     * @throws  IllegalStateException if this interval is infinite or if there is no canonical form
     * @see     #toCanonical()
     * @see     net.time4j.engine.ChronoCondition
     * @since   4.24
     */
    /*[deutsch]
     * <p>Erzeugt einen {@code Stream}, der &uuml;ber jedes Kalenderdatum der kanonischen Form
     * dieses Intervalls geht und den angegebenen Ausschlu&szlig;filter anwendet. </p>
     *
     * <p>Beispiel des Ausschlusses von Samstag und Sonntag: </p>
     *
     * <pre>
     *     DateInterval.between(
     *       PlainDate.of(2017, 2, 1),
     *       PlainDate.of(2017, 2, 8)
     *     ).streamExcluding(Weekday.SATURDAY.or(Weekday.SUNDAY)).forEach(System.out::println);
     * </pre>
     *
     * <p>Alle Objekte, deren Typ eine Subklasse von {@code ChronoCondition} darstellt, k&ouml;nnen ebenfalls
     * als Parameter verwendet werden, zum Beispiel lokalisierte Wochenenden mit dem Ausdruck
     * {@code Weekmodel.of(locale).weekend()}. </p>
     *
     * @param   exclusion   filter as predicate
     * @return  daily filtered stream
     * @throws  IllegalStateException if this interval is infinite or if there is no canonical form
     * @see     #toCanonical()
     * @see     net.time4j.engine.ChronoCondition
     * @since   4.24
     */
    public Stream<PlainDate> streamExcluding(Predicate<? super PlainDate> exclusion) {

        return this.streamDaily().filter(exclusion.negate());

    }

    /**
     * <p>Obtains a stream iterating over every calendar date which is the result of addition of given duration
     * in week-based units to start until the end of this interval is reached. </p>
     *
     * @param   weekBasedYears      duration component of week-based years
     * @param   isoWeeks            duration component of calendar weeks (from Monday to Sunday)
     * @param   days                duration component of ordinary calendar days
     * @throws  IllegalStateException       if this interval is infinite or if there is no canonical form
     * @throws  IllegalArgumentException    if there is any negative duration component or if there is
     *                                      no positive duration component at all
     * @return  stream consisting of distinct dates which are the result of adding the duration to the start
     * @see     #toCanonical()
     * @see     Weekcycle#YEARS
     * @since   4.18
     */
    /*[deutsch]
     * <p>Erzeugt einen {@code Stream}, der jeweils ein Kalenderdatum als Vielfaches der Dauer in
     * wochenbasierten Zeiteinheiten angewandt auf den Start und bis zum Ende dieses Intervalls geht. </p>
     *
     * @param   weekBasedYears      duration component of week-based years
     * @param   isoWeeks            duration component of calendar weeks (from Monday to Sunday)
     * @param   days                duration component of ordinary calendar days
     * @throws  IllegalStateException       if this interval is infinite or if there is no canonical form
     * @throws  IllegalArgumentException    if there is any negative duration component or if there is
     *                                      no positive duration component at all
     * @return  stream consisting of distinct dates which are the result of adding the duration to the start
     * @see     #toCanonical()
     * @see     Weekcycle#YEARS
     * @since   4.18
     */
    public Stream<PlainDate> streamWeekBased(
        int weekBasedYears,
        int isoWeeks,
        int days
    ) {

        if ((weekBasedYears < 0) || (isoWeeks < 0) || (days < 0)) {
            throw new IllegalArgumentException("Found illegal negative duration component.");
        }

        final long effYears = weekBasedYears;
        final long effDays = 7L * isoWeeks + days;

        if ((weekBasedYears == 0) && (effDays == 0)) {
            throw new IllegalArgumentException("Cannot create stream with empty duration.");
        }

        if (this.isEmpty()) {
            return Stream.empty();
        }

        DateInterval interval = this.toCanonical();
        PlainDate start = interval.getStartAsCalendarDate();
        PlainDate end = interval.getEndAsCalendarDate();

        if ((start == null) || (end == null)) {
            throw new IllegalStateException("Streaming is not supported for infinite intervals.");
        }

        if ((effYears == 0) && (effDays == 1)) {
            return DateInterval.streamDaily(start, end);
        }

        long s = start.getDaysSinceEpochUTC();
        long e = end.getDaysSinceEpochUTC();

        long n = 1 + ((e - s) / (Math.addExact(Math.multiplyExact(effYears, 371), effDays))); // first estimate
        PlainDate date;
        long size;

        do {
            size = n;
            long y = Math.multiplyExact(effYears, n);
            long d = Math.multiplyExact(effDays, n);
            date = start.plus(y, Weekcycle.YEARS).plus(d, CalendarUnit.DAYS);
            n++;
        } while (!date.isAfter(end));

        if (size == 1) {
            return Stream.of(start); // short-cut
        }

        return LongStream.range(0, size).mapToObj(
            index -> start.plus(effYears * index, Weekcycle.YEARS).plus(effDays * index, CalendarUnit.DAYS));

    }

    /**
     * <p>Creates a partitioning stream of timestamp intervals where every day of this interval is partitioned
     * according to given partitioning rule. </p>
     *
     * <p>This method enables the easy construction of daily shop opening times or weekly work time schedules. </p>
     *
     * @param   rule        day partition rule
     * @return  stream of timestamp intervals
     * @throws  IllegalStateException if this interval is infinite or if there is no canonical form
     * @see     #streamPartitioned(DayPartitionRule, TZID)
     * @since   4.18
     */
    /*[deutsch]
     * <p>Erzeugt einen {@code Stream}, der jeden Tag dieses Intervalls entsprechend der angegebenen Regel
     * in einzelne Tagesabschnitte zerlegt. </p>
     *
     * <p>Hiermit k&ouml;nnen t&auml;gliche Laden&ouml;ffnungszeiten oder w&ouml;chentliche Arbeitszeitschemata
     * auf einfache Weise erstellt werden. </p>
     *
     * @param   rule        day partition rule
     * @return  stream of timestamp intervals
     * @throws  IllegalStateException if this interval is infinite or if there is no canonical form
     * @see     #streamPartitioned(DayPartitionRule, TZID)
     * @since   4.18
     */
    public Stream<TimestampInterval> streamPartitioned(DayPartitionRule rule) {

        return this.streamDaily().flatMap(
            date ->
                rule.getPartitions(date).stream().map(
                    partition ->
                        TimestampInterval.between(
                            date.at(partition.getStart().getTemporal()),
                            date.at(partition.getEnd().getTemporal())
                        )
                )
        );

    }

    /**
     * <p>Creates a partitioning stream of moment intervals where every day of this interval is partitioned
     * according to given partitioning rule. </p>
     *
     * <p>This method enables the easy construction of daily shop opening times or weekly work time schedules. </p>
     *
     * @param   rule        day partition rule
     * @param   tzid        timezone identifier
     * @return  stream of moment intervals
     * @throws  IllegalStateException if this interval is infinite or if there is no canonical form
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @see     #streamPartitioned(DayPartitionRule)
     * @see     GapResolver#NEXT_VALID_TIME
     * @see     OverlapResolver#EARLIER_OFFSET
     * @since   4.19
     */
    /*[deutsch]
     * <p>Erzeugt einen {@code Stream}, der jeden Tag dieses Intervalls entsprechend der angegebenen Regel
     * in einzelne Tagesabschnitte zerlegt und als Momentintervalle darstellt. </p>
     *
     * <p>Hiermit k&ouml;nnen t&auml;gliche Laden&ouml;ffnungszeiten oder w&ouml;chentliche Arbeitszeitschemata
     * auf einfache Weise erstellt werden. </p>
     *
     * @param   rule        day partition rule
     * @param   tzid        timezone identifier
     * @return  stream of moment intervals
     * @throws  IllegalStateException if this interval is infinite or if there is no canonical form
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @see     #streamPartitioned(DayPartitionRule)
     * @see     GapResolver#NEXT_VALID_TIME
     * @see     OverlapResolver#EARLIER_OFFSET
     * @since   4.19
     */
    public Stream<MomentInterval> streamPartitioned(
        DayPartitionRule rule,
        TZID tzid
    ) {

        final Timezone tz = Timezone.of(tzid).with(GapResolver.NEXT_VALID_TIME.and(OverlapResolver.EARLIER_OFFSET));

        return this.streamPartitioned(rule)
            .map(interval -> interval.in(tz))
            .filter(interval -> !interval.isEmpty());

    }

    /**
     * <p>Obtains a random date within this interval. </p>
     *
     * @return  random date within this interval
     * @throws  IllegalStateException if this interval is infinite or empty or if there is no canonical form
     * @see     #toCanonical()
     * @since   5.0
     */
    /*[deutsch]
     * <p>Liefert ein Zufallsdatum innerhalb dieses Intervalls. </p>
     *
     * @return  random date within this interval
     * @throws  IllegalStateException if this interval is infinite or empty or if there is no canonical form
     * @see     #toCanonical()
     * @since   5.0
     */
    public PlainDate random() {

        DateInterval interval = this.toCanonical();

        if (interval.isFinite() && !interval.isEmpty()) {
            long randomNum =
                ThreadLocalRandom.current().nextLong(
                    interval.getStartAsCalendarDate().getDaysSinceEpochUTC(),
                    interval.getEndAsCalendarDate().getDaysSinceEpochUTC() + 1);
            return PlainDate.of(randomNum, EpochDays.UTC);
        } else {
            throw new IllegalStateException("Cannot get random date in an empty or infinite interval: " + this);
        }

    }

    /**
     * <p>Prints the canonical form of this interval in given ISO-8601 style. </p>
     *
     * @param   dateStyle       controlling the date format of output
     * @param   infinityStyle   controlling the format of infinite boundaries
     * @return  String
     * @throws  IllegalStateException   if there is no canonical form
     *                                  or given infinity style prevents infinite intervals
     * @see     #toCanonical()
     * @since   4.18
     */
    /*[deutsch]
     * <p>Formatiert die kanonische Form dieses Intervalls im angegebenen ISO-8601-Stil. </p>
     *
     * @param   dateStyle       controlling the date format of output
     * @param   infinityStyle   controlling the format of infinite boundaries
     * @return  String
     * @throws  IllegalStateException   if there is no canonical form
     *                                  or given infinity style prevents infinite intervals
     * @see     #toCanonical()
     * @since   4.18
     */
    public String formatISO(
        IsoDateStyle dateStyle,
        InfinityStyle infinityStyle
    ) {

        DateInterval interval = this.toCanonical();
        StringBuilder buffer = new StringBuilder(21);
        ChronoPrinter<PlainDate> printer = Iso8601Format.ofDate(dateStyle);
        if (interval.getStart().isInfinite()) {
            buffer.append(infinityStyle.displayPast(printer, PlainDate.axis()));
        } else {
            printer.print(interval.getStartAsCalendarDate(), buffer);
        }
        buffer.append('/');
        if (interval.getEnd().isInfinite()) {
            buffer.append(infinityStyle.displayFuture(printer, PlainDate.axis()));
        } else {
            printer.print(interval.getEndAsCalendarDate(), buffer);
        }
        return buffer.toString();

    }

    /**
     * <p>Prints the canonical form of this interval in given reduced ISO-8601 style. </p>
     *
     * <p>The term &quot;reduced&quot; means that higher-order elements like the year can be
     * left out in the end component if it is equal to the value of the start component. Example: </p>
     *
     * <pre>
     *     DateInterval interval =
     *          DateInterval.between(
     *              PlainDate.of(2016, 2, 29),
     *              PlainDate.of(2016, 3, 13));
     *     System.out.println(interval.formatReduced(IsoDateStyle.EXTENDED_CALENDAR_DATE));
     *     // Output: 2016-02-29/03-13
     * </pre>
     *
     * @param   dateStyle       controlling the date format of output
     * @param   infinityStyle   controlling the format of infinite boundaries
     * @return  String
     * @throws  IllegalStateException   if there is no canonical form
     *                                  or given infinity style prevents infinite intervals
     * @see     #toCanonical()
     * @since   4.18
     */
    /*[deutsch]
     * <p>Formatiert die kanonische Form dieses Intervalls im angegebenen reduzierten ISO-8601-Stil. </p>
     *
     * <p>Der Begriff &quot;reduziert&quot; bedeutet, da&szlig; h&ouml;herwertige Elemente wie das Jahr
     * in der Endkomponente weggelassen werden, wenn ihr Wert gleich dem Wert der Startkomponente ist.
     * Beispiel: </p>
     *
     * <pre>
     *     DateInterval interval =
     *          DateInterval.between(
     *              PlainDate.of(2016, 2, 29),
     *              PlainDate.of(2016, 3, 13));
     *     System.out.println(interval.formatReduced(IsoDateStyle.EXTENDED_CALENDAR_DATE));
     *     // Output: 2016-02-29/03-13
     * </pre>
     *
     * @param   dateStyle       controlling the date format of output
     * @param   infinityStyle   controlling the format of infinite boundaries
     * @return  String
     * @throws  IllegalStateException   if there is no canonical form
     *                                  or given infinity style prevents infinite intervals
     * @see     #toCanonical()
     * @since   4.18
     */
    public String formatReduced(
        IsoDateStyle dateStyle,
        InfinityStyle infinityStyle
    ) {

        DateInterval interval = this.toCanonical();
        StringBuilder buffer = new StringBuilder(21);
        ChronoPrinter<PlainDate> printer = Iso8601Format.ofDate(dateStyle);
        PlainDate start = interval.getStartAsCalendarDate();
        if (interval.getStart().isInfinite()) {
            buffer.append(infinityStyle.displayPast(printer, PlainDate.axis()));
        } else {
            printer.print(start, buffer);
        }
        buffer.append('/');
        PlainDate end = interval.getEndAsCalendarDate();
        if (interval.isFinite()) {
            getEndPrinter(dateStyle, start, end).print(end, buffer);
        } else if (interval.getEnd().isInfinite()) {
            buffer.append(infinityStyle.displayFuture(printer, PlainDate.axis()));
        } else {
            printer.print(end, buffer);
        }
        return buffer.toString();

    }

    /**
     * <p>Interpretes given text as interval using a localized interval pattern. </p>
     *
     * <p>If given parser does not contain a reference to a locale then the interval pattern
     * &quot;{0}/{1}&quot; will be used. </p>
     *
     * @param   text        text to be parsed
     * @param   parser      format object for parsing start and end components
     * @return  parsed interval
     * @throws  IndexOutOfBoundsException if given text is empty
     * @throws  ParseException if the text is not parseable
     * @since   3.9/4.6
     * @see     #parse(String, ChronoParser, String)
     * @see     net.time4j.format.FormatPatternProvider#getIntervalPattern(Locale)
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text als Intervall mit Hilfe eines lokalisierten
     * Intervallmusters. </p>
     *
     * <p>Falls der angegebene Formatierer keine Referenz zu einer Sprach- und L&auml;ndereinstellung hat, wird
     * das Intervallmuster &quot;{0}/{1}&quot; verwendet. </p>
     *
     * @param   text        text to be parsed
     * @param   parser      format object for parsing start and end components
     * @return  parsed interval
     * @throws  IndexOutOfBoundsException if given text is empty
     * @throws  ParseException if the text is not parseable
     * @since   3.9/4.6
     * @see     #parse(String, ChronoParser, String)
     * @see     net.time4j.format.FormatPatternProvider#getIntervalPattern(Locale)
     */
    public static DateInterval parse(
        String text,
        ChronoParser<PlainDate> parser
    ) throws ParseException {

        return parse(text, parser, IsoInterval.getIntervalPattern(parser));

    }

    /**
     * <p>Interpretes given text as interval using given interval pattern. </p>
     *
     * <p>Starting with version v4.18, it is also possible to use an or-pattern logic. Example: </p>
     *
     * <pre>
     *     String multiPattern = &quot;{0} - {1}|since {0}|until {1}&quot;;
     *     ChronoParser&lt;PlainDate&gt; parser =
     *          ChronoFormatter.ofDatePattern(&quot;MMMM d / uuuu&quot;, PatternType.CLDR, Locale.US);
     *
     *     DateInterval between =
     *          DateInterval.parse(&quot;July 20 / 2015 - December 31 / 2015&quot;, parser, multiPattern);
     *     System.out.println(between); // [2015-07-20/2015-12-31]
     *
     *     DateInterval since =
     *          DateInterval.parse(&quot;since July 20 / 2015&quot;, parser, multiPattern);
     *     System.out.println(since); // [2015-07-20/+&#x221E;)
     *
     *     DateInterval until =
     *          DateInterval.parse(&quot;until December 31 / 2015&quot;, parser, multiPattern);
     *     System.out.println(until); // (-&#x221E;/2015-12-31]
     * </pre>
     *
     * @param   text                text to be parsed
     * @param   parser              format object for parsing start and end components
     * @param   intervalPattern     interval pattern containing placeholders {0} and {1} (for start and end)
     * @return  parsed interval
     * @throws  IndexOutOfBoundsException if given text is empty
     * @throws  ParseException if the text is not parseable
     * @since   3.9/4.6
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text als Intervall mit Hilfe des angegebenen
     * Intervallmusters. </p>
     *
     * <p>Beginnend mit der Version v4.18 ist es auch m&ouml;glich, eine Oder-Logik im Muster zu verwenden.
     * Beispiel: </p>
     *
     * <pre>
     *     String multiPattern = &quot;{0} - {1}|since {0}|until {1}&quot;;
     *     ChronoParser&lt;PlainDate&gt; parser =
     *          ChronoFormatter.ofDatePattern(&quot;MMMM d / uuuu&quot;, PatternType.CLDR, Locale.US);
     *
     *     DateInterval between =
     *          DateInterval.parse(&quot;July 20 / 2015 - December 31 / 2015&quot;, parser, multiPattern);
     *     System.out.println(between); // [2015-07-20/2015-12-31]
     *
     *     DateInterval since =
     *          DateInterval.parse(&quot;since July 20 / 2015&quot;, parser, multiPattern);
     *     System.out.println(since); // [2015-07-20/+&#x221E;)
     *
     *     DateInterval until =
     *          DateInterval.parse(&quot;until December 31 / 2015&quot;, parser, multiPattern);
     *     System.out.println(until); // (-&#x221E;/2015-12-31]
     * </pre>
     *
     * @param   text                text to be parsed
     * @param   parser              format object for parsing start and end components
     * @param   intervalPattern     interval pattern containing placeholders {0} and {1} (for start and end)
     * @return  parsed interval
     * @throws  IndexOutOfBoundsException if given text is empty
     * @throws  ParseException if the text is not parseable
     * @since   3.9/4.6
     */
    public static DateInterval parse(
        String text,
        ChronoParser<PlainDate> parser,
        String intervalPattern
    ) throws ParseException {

        return IntervalParser.parsePattern(text, DateIntervalFactory.INSTANCE, parser, intervalPattern);

    }

    /**
     * <p>Interpretes given text as interval. </p>
     *
     * <p>This method can also accept a hyphen as alternative to solidus as separator
     * between start and end component unless the start component is a period.
     * Infinity symbols are understood. </p>
     *
     * @param   text        text to be parsed
     * @param   parser      format object for parsing start and end components
     * @param   policy      strategy for parsing interval boundaries
     * @return  result
     * @throws  ParseException if parsing does not work
     * @throws  IndexOutOfBoundsException if the start position is at end of text or even behind
     * @since   4.18
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text als Intervall. </p>
     *
     * <p>Diese Methode kann auch einen Bindestrich als Alternative zum Schr&auml;gstrich als Trennzeichen
     * zwischen Start- und Endkomponente interpretieren, es sei denn, die Startkomponente ist eine Periode.
     * Unendlichkeitssymbole werden verstanden. </p>
     *
     * @param   text        text to be parsed
     * @param   parser      format object for parsing start and end components
     * @param   policy      strategy for parsing interval boundaries
     * @return  result
     * @throws  ParseException if parsing does not work
     * @throws  IndexOutOfBoundsException if the start position is at end of text or even behind
     * @since   4.18
     */
    public static DateInterval parse(
        CharSequence text,
        ChronoParser<PlainDate> parser,
        BracketPolicy policy
    ) throws ParseException {

        ParseLog plog = new ParseLog();
        DateInterval interval =
            IntervalParser.of(
                DateIntervalFactory.INSTANCE,
                parser,
                policy
            ).parse(text, plog, parser.getAttributes());

        if ((interval == null) || plog.isError()) {
            throw new ParseException(plog.getErrorMessage(), plog.getErrorIndex());
        } else if (
            (plog.getPosition() < text.length())
            && !parser.getAttributes().get(Attributes.TRAILING_CHARACTERS, Boolean.FALSE).booleanValue()
        ) {
            throw new ParseException("Trailing characters found: " + text, plog.getPosition());
        } else {
            return interval;
        }

    }

    /**
     * <p>Interpretes given text as interval. </p>
     *
     * <p>This method is mainly intended for parsing technical interval formats similar to ISO-8601
     * which are not localized. Infinity symbols are understood. </p>
     *
     * @param   text        text to be parsed
     * @param   startFormat format object for parsing start component
     * @param   separator   char separating start and end component
     * @param   endFormat   format object for parsing end component
     * @param   policy      strategy for parsing interval boundaries
     * @param   status      parser information (always as new instance)
     * @return  result or {@code null} if parsing does not work
     * @throws  IndexOutOfBoundsException if the start position is at end of text or even behind
     * @since   3.9/4.6
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text als Intervall. </p>
     *
     * <p>Diese Methode ist vor allem f&uuml;r technische nicht-lokalisierte Intervallformate &auml;hnlich
     * wie in ISO-8601 definiert gedacht. Unendlichkeitssymbole werden verstanden. </p>
     *
     * @param   text        text to be parsed
     * @param   startFormat format object for parsing start component
     * @param   separator   char separating start and end component
     * @param   endFormat   format object for parsing end component
     * @param   policy      strategy for parsing interval boundaries
     * @param   status      parser information (always as new instance)
     * @return  result or {@code null} if parsing does not work
     * @throws  IndexOutOfBoundsException if the start position is at end of text or even behind
     * @since   3.9/4.6
     */
    public static DateInterval parse(
        CharSequence text,
        ChronoParser<PlainDate> startFormat,
        char separator,
        ChronoParser<PlainDate> endFormat,
        BracketPolicy policy,
        ParseLog status
    ) {

        return IntervalParser.of(
            DateIntervalFactory.INSTANCE,
            startFormat,
            endFormat,
            policy,
            separator
        ).parse(text, status, startFormat.getAttributes());

    }

    /**
     * <p>Interpretes given ISO-conforming text as interval. </p>
     *
     * <p>All styles are supported, namely calendar dates, ordinal dates
     * and week dates, either in basic or in extended format. Mixed date
     * styles for start and end are not allowed however. Furthermore, one
     * of start or end can also be represented by a period string. If not
     * then the end component may exist in an abbreviated form as
     * documented in ISO-8601-paper leaving out higher-order elements
     * like the calendar year (which will be overtaken from the start
     * component instead). </p>
     *
     * <p>The infinity symbols &quot;-&quot; (past and future),
     * &quot;-&#x221E;&quot; (past), &quot;+&#x221E;&quot; (future),
     * &quot;-999999999-01-01&quot; und &quot;+999999999-12-31&quot;
     * can also be parsed as extension although strictly spoken ISO-8601
     * does not know or specify infinite intervals. Examples for supported
     * formats: </p>
     *
     * <pre>
     *  System.out.println(
     *      DateInterval.parseISO(&quot;2012-01-01/2014-06-20&quot;));
     *  // output: [2012-01-01/2014-06-20]
     *
     *  System.out.println(DateInterval.parseISO(&quot;2012-01-01/08-11&quot;));
     *  // output: [2012-01-01/2012-08-11]
     *
     *  System.out.println(DateInterval.parseISO(&quot;2012-W01-1/W06-4&quot;));
     *  // output: [2012-01-02/2012-02-09]
     *
     *  System.out.println(DateInterval.parseISO(&quot;2012-001/366&quot;));
     *  // output: [2012-01-01/2012-12-31]
     *
     *  System.out.println(DateInterval.parseISO(&quot;2012-001/+&#x221E;&quot;));
     *  // output: [2012-01-01/+&#x221E;)
     * </pre>
     *
     * <p>This method dynamically creates an appropriate interval format for reduced forms.
     * If performance is more important then a static fixed formatter might be considered. </p>
     *
     * @param   text        text to be parsed
     * @return  parsed interval
     * @throws  IndexOutOfBoundsException if given text is empty
     * @throws  ParseException if the text is not parseable
     * @since   2.0
     * @see     BracketPolicy#SHOW_NEVER
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen ISO-konformen Text als Intervall. </p>
     *
     * <p>Alle Stile werden unterst&uuml;tzt, n&auml;mlich Kalendardatum,
     * Ordinaldatum und Wochendatum, sowohl im Basisformat als auch im
     * erweiterten Format. Gemischte Datumsstile von Start und Ende
     * sind jedoch nicht erlaubt. Au&szlig;erdem darf eine der beiden
     * Komponenten Start und Ende als P-String vorliegen. Wenn nicht, dann
     * darf die Endkomponente auch in einer abgek&uuml;rzten Schreibweise
     * angegeben werden, in der weniger pr&auml;zise Elemente wie das
     * Kalenderjahr ausgelassen und von der Startkomponente &uuml;bernommen
     * werden. </p>
     *
     * <p>Die Unendlichkeitssymbole &quot;-&quot; (sowohl Vergangenheit als auch Zukunft),
     * &quot;-&#x221E;&quot; (Vergangenheit), &quot;+&#x221E;&quot; (Zukunft),
     * &quot;-999999999-01-01&quot; und &quot;+999999999-12-31&quot; werden ebenfalls
     * interpretiert, obwohl ISO-8601 keine unendlichen Intervalle kennt. Beispiele
     * f&uuml;r unterst&uuml;tzte Formate: </p>
     *
     * <pre>
     *  System.out.println(
     *      DateInterval.parseISO(&quot;2012-01-01/2014-06-20&quot;));
     *  // Ausgabe: [2012-01-01/2014-06-20]
     *
     *  System.out.println(DateInterval.parseISO(&quot;2012-01-01/08-11&quot;));
     *  // Ausgabe: [2012-01-01/2012-08-11]
     *
     *  System.out.println(DateInterval.parseISO(&quot;2012-W01-1/W06-4&quot;));
     *  // Ausgabe: [2012-01-02/2012-02-09]
     *
     *  System.out.println(DateInterval.parseISO(&quot;2012-001/366&quot;));
     *  // Ausgabe: [2012-01-01/2012-12-31]
     *
     *  System.out.println(DateInterval.parseISO(&quot;2012-001/+&#x221E;&quot;));
     *  // output: [2012-01-01/+&#x221E;)
     * </pre>
     *
     * <p>Intern wird das notwendige Intervallformat f&uuml;r reduzierte Formen dynamisch ermittelt. Ist
     * das Antwortzeitverhalten wichtiger, sollte einem statisch initialisierten konstanten Format der
     * Vorzug gegeben werden. </p>
     *
     * @param   text        text to be parsed
     * @return  parsed interval
     * @throws  IndexOutOfBoundsException if given text is empty
     * @throws  ParseException if the text is not parseable
     * @since   2.0
     * @see     BracketPolicy#SHOW_NEVER
     */
    public static DateInterval parseISO(String text) throws ParseException {

        if (text.isEmpty()) {
            throw new IndexOutOfBoundsException("Empty text.");
        }

        // prescan for format analysis
		int start = 0;
		int n = Math.min(text.length(), 48);
        boolean sameFormat = true;
        int componentLength = 0;

        for (int i = 1; i < n; i++) {
            if (text.charAt(i) == '/') {
                if (i + 1 == n) {
                    throw new ParseException("Missing end component.", n);
                } else if (
                    (text.charAt(0) == 'P')
                    || ((text.charAt(0) == '-') && (i == 1 || text.charAt(1) == '\u221E'))
                ) {
                    start = i + 1;
                    componentLength = n - i - 1;
                } else if (
                    (text.charAt(i + 1) == 'P')
                    || ((text.charAt(i + 1) == '-') && (i + 2 == n))
                    || ((text.charAt(i + 1) == '+') && (i + 2 < n) && (text.charAt(i + 2) == '\u221E'))
                ) {
                    componentLength = i;
                } else {
                    sameFormat = (2 * i + 1 == n);
                    componentLength = i;
                }
                break;
            }
        }

        int literals = 0;
        boolean ordinalStyle = false;
        boolean weekStyle = false;

        for (int i = start + 1; i < n; i++) {
            char c = text.charAt(i);
            if (c == '-') {
                literals++;
            } else if (c == 'W') {
                weekStyle = true;
                break;
            } else if (c == '/') {
                break;
            }
        }

        boolean extended = (literals > 0);
        char c = text.charAt(start);
        componentLength -= 4;

        if ((c == '+') || (c == '-')) {
            componentLength -= 2;
        }

        if (!weekStyle) {
            ordinalStyle = (
                (literals == 1)
                || ((literals == 0) && (componentLength == 3)));
        }

        // start format
        ChronoFormatter<PlainDate> startFormat;

        if (extended) {
            if (ordinalStyle) {
                startFormat = Iso8601Format.EXTENDED_ORDINAL_DATE;
            } else if (weekStyle) {
                startFormat = Iso8601Format.EXTENDED_WEEK_DATE;
            } else {
                startFormat = Iso8601Format.EXTENDED_CALENDAR_DATE;
            }
        } else {
            if (ordinalStyle) {
                startFormat = Iso8601Format.BASIC_ORDINAL_DATE;
            } else if (weekStyle) {
                startFormat = Iso8601Format.BASIC_WEEK_DATE;
            } else {
                startFormat = Iso8601Format.BASIC_CALENDAR_DATE;
            }
        }

        // prepare component parsers
        ChronoFormatter<PlainDate> endFormat = (sameFormat ? startFormat : null); // null means reduced iso format

        // create interval
        Parser parser = new Parser(startFormat, endFormat, extended, weekStyle, ordinalStyle);
        return parser.parse(text);

    }

    static ChronoPrinter<PlainDate> getEndPrinter(
        IsoDateStyle style,
        PlainDate start,
        PlainDate end
    ) {

        ChronoPrinter<PlainDate> endPrinter = Iso8601Format.ofDate(style);
        int year1 = start.getYear();
        int year2 = end.getYear();

        switch (style) {
            case BASIC_CALENDAR_DATE:
                if (year1 == year2) {
                    endPrinter = ((start.getMonth() == end.getMonth()) ? REDUCED_DD : REDUCED_MMDD);
                }
                break;
            case BASIC_ORDINAL_DATE:
                if (year1 == year2) {
                    endPrinter = REDUCED_DDD;
                }
                break;
            case BASIC_WEEK_DATE:
                year1 = start.getInt(PlainDate.YEAR_OF_WEEKDATE);
                year2 = end.getInt(PlainDate.YEAR_OF_WEEKDATE);
                if (year1 == year2) {
                    if (start.getInt(Weekmodel.ISO.weekOfYear()) == end.getInt(Weekmodel.ISO.weekOfYear())) {
                        endPrinter = REDUCED_E;
                    } else {
                        endPrinter = REDUCED_W_WWE;
                    }
                }
                break;
            case EXTENDED_CALENDAR_DATE:
                if (year1 == year2) {
                    endPrinter = ((start.getMonth() == end.getMonth()) ? REDUCED_DD : REDUCED_MM_DD);
                }
                break;
            case EXTENDED_ORDINAL_DATE:
                if (year1 == year2) {
                    endPrinter = REDUCED_DDD;
                }
                break;
            case EXTENDED_WEEK_DATE:
                year1 = start.getInt(PlainDate.YEAR_OF_WEEKDATE);
                year2 = end.getInt(PlainDate.YEAR_OF_WEEKDATE);
                if (year1 == year2) {
                    if (start.getInt(Weekmodel.ISO.weekOfYear()) == end.getInt(Weekmodel.ISO.weekOfYear())) {
                        endPrinter = REDUCED_E;
                    } else {
                        endPrinter = REDUCED_W_WW_E;
                    }
                }
                break;
            default:
                throw new UnsupportedOperationException(style.name());
        }

        return endPrinter;

    }

    @Override
    IntervalFactory<PlainDate, DateInterval> getFactory() {

        return DateIntervalFactory.INSTANCE;

    }

    @Override
    DateInterval getContext() {

        return this;

    }

    /**
     * @serialData  Uses
     *              <a href="../../../serialized-form.html#net.time4j.range.SPX">
     *              a dedicated serialization form</a> as proxy. The first byte
     *              contains the type-ID {@code 32} in the six most significant
     *              bits. The next bytes represent the start and the end
     *              boundary.
     *
     * Schematic algorithm:
     *
     * <pre>
       int header = 32;
       header &lt;&lt;= 2;
       out.writeByte(header);
       writeBoundary(getStart(), out);
       writeBoundary(getEnd(), out);

       private static void writeBoundary(
           Boundary&lt;?&gt; boundary,
           ObjectOutput out
       ) throws IOException {
           if (boundary.equals(Boundary.infinitePast())) {
               out.writeByte(1);
           } else if (boundary.equals(Boundary.infiniteFuture())) {
               out.writeByte(2);
           } else {
               out.writeByte(boundary.isOpen() ? 4 : 0);
               out.writeObject(boundary.getTemporal());
           }
       }
      </pre>
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.DATE_TYPE);

    }

    /**
     * @serialData  Blocks because a serialization proxy is required.
     * @param       in      object input stream
     * @throws      InvalidObjectException (always)
     */
    private void readObject(ObjectInputStream in)
        throws IOException {

        throw new InvalidObjectException("Serialization proxy required.");

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class Parser
        extends IntervalParser<PlainDate, DateInterval> {

        //~ Instanzvariablen ----------------------------------------------

        private final boolean extended;
        private final boolean weekStyle;
        private final boolean ordinalStyle;

        //~ Konstruktoren -------------------------------------------------

        Parser(
            ChronoParser<PlainDate> startFormat,
            ChronoParser<PlainDate> endFormat, // optional
            boolean extended,
            boolean weekStyle,
            boolean ordinalStyle
        ) {
            super(DateIntervalFactory.INSTANCE, startFormat, endFormat, BracketPolicy.SHOW_NEVER, '/');

            this.extended = extended;
            this.weekStyle = weekStyle;
            this.ordinalStyle = ordinalStyle;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        protected PlainDate parseReducedEnd(
            CharSequence text,
            PlainDate start,
            ParseLog lowerLog,
            ParseLog upperLog,
            AttributeQuery attrs
        ) {

            ChronoFormatter<PlainDate> reducedParser =
                this.createEndFormat(
                    PlainDate.axis().preformat(start, attrs),
                    lowerLog.getRawValues());
            return reducedParser.parse(text, upperLog);

        }

        private ChronoFormatter<PlainDate> createEndFormat(
            ChronoDisplay defaultSupplier,
            ChronoEntity<?> rawData
        ) {

            ChronoFormatter.Builder<PlainDate> builder =
                ChronoFormatter.setUp(PlainDate.class, Locale.ROOT);

            ChronoElement<Integer> year = (this.weekStyle ? YEAR_OF_WEEKDATE : YEAR);
            if (this.extended) {
                int p = (this.ordinalStyle ? 3 : 5);
                builder.startSection(Attributes.PROTECTED_CHARACTERS, p);
                builder.addCustomized(
                    year,
                    NOOP,
                    (this.weekStyle ? YearParser.YEAR_OF_WEEKDATE : YearParser.YEAR));
            } else {
                int p = (this.ordinalStyle ? 3 : 4);
                builder.startSection(Attributes.PROTECTED_CHARACTERS, p);
                builder.addInteger(year, 4, 9, SignPolicy.SHOW_WHEN_BIG_NUMBER);
            }
            builder.endSection();

            if (this.weekStyle) {
                builder.startSection(Attributes.PROTECTED_CHARACTERS, 1);
                builder.addCustomized(
                    Weekmodel.ISO.weekOfYear(),
                    NOOP,
                    this.extended
                        ? FixedNumParser.EXTENDED_WEEK_OF_YEAR
                        : FixedNumParser.BASIC_WEEK_OF_YEAR);
                builder.endSection();
                builder.addFixedNumerical(DAY_OF_WEEK, 1);
            } else if (this.ordinalStyle) {
                builder.addFixedInteger(DAY_OF_YEAR, 3);
            } else {
                builder.startSection(Attributes.PROTECTED_CHARACTERS, 2);
                if (this.extended) {
                    builder.addCustomized(
                        MONTH_AS_NUMBER,
                        NOOP,
                        FixedNumParser.CALENDAR_MONTH);
                } else {
                    builder.addFixedInteger(MONTH_AS_NUMBER, 2);
                }
                builder.endSection();
                builder.addFixedInteger(DAY_OF_MONTH, 2);
            }

            for (ChronoElement<?> key : DateIntervalFactory.INSTANCE.stdElements(rawData)) {
                setDefault(builder, key, defaultSupplier);
            }

            return builder.build();

        }

        // wilcard capture
        private static <V> void setDefault(
            ChronoFormatter.Builder<PlainDate> builder,
            ChronoElement<V> element,
            ChronoDisplay defaultSupplier
        ) {

            builder.setDefault(element, defaultSupplier.get(element));

        }

    }

    private static class DailySpliterator
        implements Spliterator<PlainDate> {

        //~ Instanzvariablen ----------------------------------------------

        private long startEpoch; // always inclusive
        private final long endEpoch; // closed range

        private PlainDate current;

        //~ Konstruktoren -------------------------------------------------

        DailySpliterator(
            PlainDate start,
            PlainDate end
        ) {
            this(start, start.getDaysSinceEpochUTC(), end.getDaysSinceEpochUTC());

        }

        private DailySpliterator(
            PlainDate start,
            long startEpoch,
            long endEpoch
        ) {
            super();

            this.startEpoch = startEpoch;
            this.endEpoch = endEpoch;

            this.current = (
                (startEpoch > endEpoch)
                ? null
                : start.with(PlainDate.DAY_OF_WEEK, Weekday.valueOf((int) Math.floorMod(startEpoch + 5, 7) + 1)));

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public boolean tryAdvance(Consumer<? super PlainDate> action) {

            if (this.current == null) {
                return false;
            }

            action.accept(this.current);

            if (this.startEpoch == this.endEpoch) {
                this.current = null;
            } else {
                this.current = this.current.plus(1, CalendarUnit.DAYS);
            }

            this.startEpoch++;
            return true;

        }

        @Override
        public void forEachRemaining(Consumer<? super PlainDate> action) {

            if (this.current == null) {
                return;
            }

            PlainDate date = this.current;

            for (long index = this.startEpoch, n = this.endEpoch; index <= n; index++) {
                action.accept(date);

                if (index < n) {
                    date = date.plus(1, CalendarUnit.DAYS);
                }
            }

            this.current = null;
            this.startEpoch = this.endEpoch + 1;

        }

        @Override
        public Spliterator<PlainDate> trySplit() {

            if (this.current == null) {
                return null; // end of traversal
            }

            long sum = (this.endEpoch - this.startEpoch - 3);

            if (sum < 7) {
                return null; // no split
            }

            long dateEpoch = (sum >>> 1) + this.startEpoch;
            PlainDate date = PlainDate.of(dateEpoch, EpochDays.UTC);
            int year = date.getYear();
            int month = date.getMonth();
            final int dom = date.getDayOfMonth();
            final boolean allowHalfMonths = (this.estimateSize() < 180) && (dom <= 15);

            if (allowHalfMonths) {
                dateEpoch += (15 - dom); // split at midth of month
            } else {
                dateEpoch += (GregorianMath.getLengthOfMonth(year, month) - dom); // split at end of month
            }

            if (dateEpoch > this.endEpoch - 7) {
                return null; // no split
            }

            Spliterator<PlainDate> split = new DailySpliterator(this.current, this.startEpoch, dateEpoch);
            Weekday newWD = this.current.getDayOfWeek().roll((int) (dateEpoch - this.startEpoch + 1));
            this.startEpoch = dateEpoch + 1;

            if (allowHalfMonths) {
                this.current = PlainDate.of(year, month, 16);
            } else {
                month++;
                if (month == 13) {
                    year++;
                    month = 1;
                }
                this.current = PlainDate.of(year, month, 1);
            }

            this.current = this.current.with(PlainDate.DAY_OF_WEEK, newWD); // trigger day-of-week-optimization
            return split;

        }

        @Override
        public long estimateSize() {

            return (this.endEpoch - this.startEpoch + 1);

        }

        @Override
        public int characteristics() {

            return DISTINCT | IMMUTABLE | NONNULL | ORDERED | SORTED | SIZED | SUBSIZED;

        }

        @Override
        public Comparator<? super PlainDate> getComparator() {

            return null;

        }

    }

}
