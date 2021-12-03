/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimestampInterval.java) is part of project Time4J.
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
import net.time4j.ClockUnit;
import net.time4j.Duration;
import net.time4j.IsoUnit;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.Weekmodel;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.CalendarDays;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.TimeSpan;
import net.time4j.format.Attributes;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.ChronoParser;
import net.time4j.format.expert.ChronoPrinter;
import net.time4j.format.expert.Iso8601Format;
import net.time4j.format.expert.IsoDateStyle;
import net.time4j.format.expert.IsoDecimalStyle;
import net.time4j.format.expert.ParseLog;
import net.time4j.format.expert.SignPolicy;
import net.time4j.tz.GapResolver;
import net.time4j.tz.OverlapResolver;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static net.time4j.PlainDate.*;
import static net.time4j.range.IntervalEdge.CLOSED;
import static net.time4j.range.IntervalEdge.OPEN;


/**
 * <p>Defines a timestamp interval on local timeline. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 */
/*[deutsch]
 * <p>Definiert ein Zeitstempelintervall auf dem lokalen Zeitstrahl. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 */
public final class TimestampInterval
    extends IsoInterval<PlainTimestamp, TimestampInterval>
    implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * Constant for a timestamp interval from infinite past to infinite future.
     *
     * @since   3.36/4.31
     */
    /*[deutsch]
     * Konstante f&uuml;r ein Zeitstempelintervall, das von der unbegrenzten Vergangenheit
     * bis in die unbegrenzte Zukunft reicht.
     *
     * @since   3.36/4.31
     */
    public static final TimestampInterval ALWAYS =
        TimestampIntervalFactory.INSTANCE.between(Boundary.infinitePast(), Boundary.infiniteFuture());

    private static final long serialVersionUID = -3965530927182499606L;

    private static final Comparator<ChronoInterval<PlainTimestamp>> COMPARATOR =
        new IntervalComparator<>(PlainTimestamp.axis());

    private static final ChronoPrinter<Integer> NOOP =
        (formattable, buffer, attributes) -> Collections.emptySet();

    //~ Konstruktoren -----------------------------------------------------

    // package-private
    TimestampInterval(
        Boundary<PlainTimestamp> start,
        Boundary<PlainTimestamp> end
    ) {
        super(start, end);

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Defines a comparator which sorts intervals first
     * by start boundary and then by length. </p>
     *
     * @return  Comparator
     * @throws  IllegalArgumentException if applied on intervals which have
     *          boundaries with extreme values
     * @since   2.0
     */
    /*[deutsch]
     * <p>Definiert ein Vergleichsobjekt, das Intervalle zuerst nach dem
     * Start und dann nach der L&auml;nge sortiert. </p>
     *
     * @return  Comparator
     * @throws  IllegalArgumentException if applied on intervals which have
     *          boundaries with extreme values
     * @since   2.0
     */
    public static Comparator<ChronoInterval<PlainTimestamp>> comparator() {

        return COMPARATOR;

    }

    /**
     * <p>Creates a finite half-open interval between given time points. </p>
     *
     * @param   start   timestamp of lower boundary (inclusive)
     * @param   end     timestamp of upper boundary (exclusive)
     * @return  new timestamp interval
     * @throws  IllegalArgumentException if start is after end
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt ein begrenztes halb-offenes Intervall zwischen den
     * angegebenen Zeitpunkten. </p>
     *
     * @param   start   timestamp of lower boundary (inclusive)
     * @param   end     timestamp of upper boundary (exclusive)
     * @return  new timestamp interval
     * @throws  IllegalArgumentException if start is after end
     * @since   2.0
     */
    public static TimestampInterval between(
        PlainTimestamp start,
        PlainTimestamp end
    ) {

        return new TimestampInterval(
            Boundary.of(CLOSED, start),
            Boundary.of(OPEN, end));

    }

    /**
     * <p>Creates a finite half-open interval between given time points. </p>
     *
     * @param   start   timestamp of lower boundary (inclusive)
     * @param   end     timestamp of upper boundary (exclusive)
     * @return  new timestamp interval
     * @throws  IllegalArgumentException if start is after end
     * @see     #between(PlainTimestamp, PlainTimestamp)
     * @since   4.11
     */
    /*[deutsch]
     * <p>Erzeugt ein begrenztes halb-offenes Intervall zwischen den
     * angegebenen Zeitpunkten. </p>
     *
     * @param   start   timestamp of lower boundary (inclusive)
     * @param   end     timestamp of upper boundary (exclusive)
     * @return  new timestamp interval
     * @throws  IllegalArgumentException if start is after end
     * @see     #between(PlainTimestamp, PlainTimestamp)
     * @since   4.11
     */
    public static TimestampInterval between(
        LocalDateTime start,
        LocalDateTime end
    ) {

        return TimestampInterval.between(PlainTimestamp.from(start), PlainTimestamp.from(end));

    }

    /**
     * <p>Creates an infinite half-open interval since given start
     * timestamp. </p>
     *
     * @param   start       timestamp of lower boundary (inclusive)
     * @return  new timestamp interval
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt ein unbegrenztes halb-offenes Intervall ab dem angegebenen
     * Startzeitpunkt. </p>
     *
     * @param   start       timestamp of lower boundary (inclusive)
     * @return  new timestamp interval
     * @since   2.0
     */
    public static TimestampInterval since(PlainTimestamp start) {

        Boundary<PlainTimestamp> future = Boundary.infiniteFuture();
        return new TimestampInterval(Boundary.of(CLOSED, start), future);

    }

    /**
     * <p>Creates an infinite half-open interval since given start
     * timestamp. </p>
     *
     * @param   start       timestamp of lower boundary (inclusive)
     * @return  new timestamp interval
     * @see     #since(PlainTimestamp)
     * @since   4.11
     */
    /*[deutsch]
     * <p>Erzeugt ein unbegrenztes halb-offenes Intervall ab dem angegebenen
     * Startzeitpunkt. </p>
     *
     * @param   start       timestamp of lower boundary (inclusive)
     * @return  new timestamp interval
     * @see     #since(PlainTimestamp)
     * @since   4.11
     */
    public static TimestampInterval since(LocalDateTime start) {

        return TimestampInterval.since(PlainTimestamp.from(start));

    }

    /**
     * <p>Creates an infinite open interval until given end timestamp. </p>
     *
     * @param   end     timestamp of upper boundary (exclusive)
     * @return  new timestamp interval
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt ein unbegrenztes offenes Intervall bis zum angegebenen
     * Endzeitpunkt. </p>
     *
     * @param   end     timestamp of upper boundary (exclusive)
     * @return  new timestamp interval
     * @since   2.0
     */
    public static TimestampInterval until(PlainTimestamp end) {

        Boundary<PlainTimestamp> past = Boundary.infinitePast();
        return new TimestampInterval(past, Boundary.of(OPEN, end));

    }

    /**
     * <p>Creates an infinite open interval until given end timestamp. </p>
     *
     * @param   end     timestamp of upper boundary (exclusive)
     * @return  new timestamp interval
     * @see     #until(PlainTimestamp)
     * @since   4.11
     */
    /*[deutsch]
     * <p>Erzeugt ein unbegrenztes offenes Intervall bis zum angegebenen
     * Endzeitpunkt. </p>
     *
     * @param   end     timestamp of upper boundary (exclusive)
     * @return  new timestamp interval
     * @see     #until(PlainTimestamp)
     * @since   4.11
     */
    public static TimestampInterval until(LocalDateTime end) {

        return TimestampInterval.until(PlainTimestamp.from(end));

    }

    /**
     * <p>Converts an arbitrary timestamp interval to an interval of this type. </p>
     *
     * @param   interval    any kind of timestamp interval
     * @return  TimestampInterval
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Konvertiert ein beliebiges Intervall zu einem Intervall dieses Typs. </p>
     *
     * @param   interval    any kind of timestamp interval
     * @return  TimestampInterval
     * @since   3.34/4.29
     */
    public static TimestampInterval from(ChronoInterval<PlainTimestamp> interval) {

        if (interval instanceof TimestampInterval) {
            return TimestampInterval.class.cast(interval);
        } else {
            return new TimestampInterval(interval.getStart(), interval.getEnd());
        }

    }

    /**
     * <p>Yields the start time point. </p>
     *
     * @return  start time point or {@code null} if infinite
     * @see     Boundary#isInfinite()
     * @since   4.11
     */
    /*[deutsch]
     * <p>Liefert den Startzeitpunkt. </p>
     *
     * @return  start time point or {@code null} if infinite
     * @see     Boundary#isInfinite()
     * @since   4.11
     */
    public PlainTimestamp getStartAsTimestamp() {

        return this.getStart().getTemporal();

    }

    /**
     * <p>Yields the start time point. </p>
     *
     * @return  start time point or {@code null} if infinite
     * @see     Boundary#isInfinite()
     * @since   4.11
     */
    /*[deutsch]
     * <p>Liefert den Startzeitpunkt. </p>
     *
     * @return  start time point or {@code null} if infinite
     * @see     Boundary#isInfinite()
     * @since   4.11
     */
    public LocalDateTime getStartAsLocalDateTime() {

        PlainTimestamp tsp = this.getStartAsTimestamp();
        return ((tsp == null) ? null : tsp.toTemporalAccessor());

    }

    /**
     * <p>Yields the end time point. </p>
     *
     * @return  end time point or {@code null} if infinite
     * @see     Boundary#isInfinite()
     * @since   4.11
     */
    /*[deutsch]
     * <p>Liefert den Endzeitpunkt. </p>
     *
     * @return  end time point or {@code null} if infinite
     * @see     Boundary#isInfinite()
     * @since   4.11
     */
    public PlainTimestamp getEndAsTimestamp() {

        return this.getEnd().getTemporal();

    }

    /**
     * <p>Yields the end time point. </p>
     *
     * @return  end time point or {@code null} if infinite
     * @see     Boundary#isInfinite()
     * @since   4.11
     */
    /*[deutsch]
     * <p>Liefert den Endzeitpunkt. </p>
     *
     * @return  end time point or {@code null} if infinite
     * @see     Boundary#isInfinite()
     * @since   4.11
     */
    public LocalDateTime getEndAsLocalDateTime() {

        PlainTimestamp tsp = this.getEndAsTimestamp();
        return ((tsp == null) ? null : tsp.toTemporalAccessor());

    }

    /**
     * <p>Combines this local timestamp interval with the timezone offset
     * UTC+00:00 to a global UTC-interval. </p>
     *
     * @return  global timestamp interval interpreted at offset UTC+00:00
     * @since   2.0
     * @see     #at(ZonalOffset)
     */
    /*[deutsch]
     * <p>Kombiniert dieses lokale Zeitstempelintervall mit UTC+00:00 zu
     * einem globalen UTC-Intervall. </p>
     *
     * @return  global timestamp interval interpreted at offset UTC+00:00
     * @since   2.0
     * @see     #at(ZonalOffset)
     */
    public MomentInterval atUTC() {

        return this.at(ZonalOffset.UTC);

    }

    /**
     * <p>Combines this local timestamp interval with given timezone offset
     * to a global UTC-interval. </p>
     *
     * @param   offset  timezone offset
     * @return  global timestamp interval interpreted at given offset
     * @since   2.0
     * @see     #atUTC()
     * @see     #inTimezone(TZID)
     */
    /*[deutsch]
     * <p>Kombiniert dieses lokale Zeitstempelintervall mit dem angegebenen
     * Zeitzonen-Offset zu einem globalen UTC-Intervall. </p>
     *
     * @param   offset  timezone offset
     * @return  global timestamp interval interpreted at given offset
     * @since   2.0
     * @see     #atUTC()
     * @see     #inTimezone(TZID)
     */
    public MomentInterval at(ZonalOffset offset) {

        Boundary<Moment> b1;
        Boundary<Moment> b2;

        if (this.getStart().isInfinite()) {
            b1 = Boundary.infinitePast();
        } else {
            Moment m1 = this.getStart().getTemporal().at(offset);
            b1 = Boundary.of(this.getStart().getEdge(), m1);
        }

        if (this.getEnd().isInfinite()) {
            b2 = Boundary.infiniteFuture();
        } else {
            Moment m2 = this.getEnd().getTemporal().at(offset);
            b2 = Boundary.of(this.getEnd().getEdge(), m2);
        }

        return new MomentInterval(b1, b2);

    }

    /**
     * <p>Combines this local timestamp interval with the system timezone
     * to a global UTC-interval. </p>
     *
     * @return  global timestamp interval interpreted in system timezone
     * @since   2.0
     * @see     Timezone#ofSystem()
     */
    /*[deutsch]
     * <p>Kombiniert dieses lokale Zeitstempelintervall mit der System-Zeitzone
     * zu einem globalen UTC-Intervall. </p>
     *
     * @return  global timestamp interval interpreted in system timezone
     * @since   2.0
     * @see     Timezone#ofSystem()
     */
    public MomentInterval inStdTimezone() {

        return this.in(SystemTimezoneHolder.get());

    }

    /**
     * <p>Combines this local timestamp interval with given timezone
     * to a global UTC-interval. </p>
     *
     * @param   tzid        timezone id
     * @return  global timestamp interval interpreted in given timezone
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   2.0
     * @see     Timezone#of(TZID)
     * @see     #inStdTimezone()
     * @see     GapResolver#NEXT_VALID_TIME
     * @see     OverlapResolver#EARLIER_OFFSET
     */
    /*[deutsch]
     * <p>Kombiniert dieses lokale Zeitstempelintervall mit der angegebenen
     * Zeitzone zu einem globalen UTC-Intervall. </p>
     *
     * @param   tzid        timezone id
     * @return  global timestamp interval interpreted in given timezone
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   2.0
     * @see     Timezone#of(TZID)
     * @see     #inStdTimezone()
     * @see     GapResolver#NEXT_VALID_TIME
     * @see     OverlapResolver#EARLIER_OFFSET
     */
    public MomentInterval inTimezone(TZID tzid) {

        return this.in(Timezone.of(tzid).with(GapResolver.NEXT_VALID_TIME.and(OverlapResolver.EARLIER_OFFSET)));

    }

    /**
     * <p>Yields the length of this interval in given units. </p>
     *
     * @param   <U> generic unit type
     * @param   units   time units to be used in calculation
     * @return  duration in given units
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge dieses Intervalls in den angegebenen
     * Zeiteinheiten. </p>
     *
     * @param   <U> generic unit type
     * @param   units   time units to be used in calculation
     * @return  duration in given units
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   2.0
     */
    @SafeVarargs
    public final <U extends IsoUnit> Duration<U> getDuration(U... units) {

        PlainTimestamp tsp = this.getTemporalOfOpenEnd();
        boolean max = (tsp == null);

        if (max) { // max reached
            tsp = this.getEnd().getTemporal();
        }

        Duration<U> result =
            Duration.in(units).between(this.getTemporalOfClosedStart(), tsp);

        if (max) {
            for (U unit : units) {
                if (unit.equals(ClockUnit.NANOS)) {
                    return result.plus(1, unit);
                }
            }
        }

        return result;

    }

    /**
     * <p>Yields the length of this interval in given units and applies
     * a timezone offset correction . </p>
     *
     * @param   tz      timezone
     * @param   units   time units to be used in calculation
     * @return  duration in given units including a zonal correction
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge dieses Intervalls in den angegebenen
     * Zeiteinheiten und wendet eine Zeitzonenkorrektur an. </p>
     *
     * @param   tz      timezone
     * @param   units   time units to be used in calculation
     * @return  duration in given units including a zonal correction
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   2.0
     */
    public Duration<IsoUnit> getDuration(
        Timezone tz,
        IsoUnit... units
    ) {

        PlainTimestamp tsp = this.getTemporalOfOpenEnd();
        boolean max = (tsp == null);

        if (max) { // max reached
            tsp = this.getEnd().getTemporal();
        }

        Duration<IsoUnit> result =
            Duration.in(tz, units).between(
                this.getTemporalOfClosedStart(),
                tsp);

        if (max) {
            for (IsoUnit unit : units) {
                if (unit.equals(ClockUnit.NANOS)) {
                    return result.plus(1, unit);
                }
            }
        }

        return result;

    }

    /**
     * <p>Moves this interval along the time axis by given units. </p>
     *
     * @param   amount  amount of units
     * @param   unit    time unit for moving
     * @return  moved copy of this interval
     */
    public TimestampInterval move(
        long amount,
        IsoUnit unit
    ) {

        if (amount == 0) {
            return this;
        }

        Boundary<PlainTimestamp> s;
        Boundary<PlainTimestamp> e;

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

        return new TimestampInterval(s, e);

    }

    /**
     * <p>Obtains a stream iterating over every timestamp which is the result of addition of given duration
     * to start until the end of this interval is reached. </p>
     *
     * <p>The stream size is limited to {@code Integer.MAX_VALUE - 1} else an {@code ArithmeticException}
     * will be thrown. </p>
     *
     * @param   duration    duration which has to be added to the start multiple times
     * @throws  IllegalArgumentException if the duration is not positive
     * @throws  IllegalStateException if this interval is infinite or if there is no canonical form
     * @return  stream consisting of distinct timestamps which are the result of adding the duration to the start
     * @see     #toCanonical()
     * @see     #stream(Duration, PlainTimestamp, PlainTimestamp)
     * @since   4.18
     */
    /*[deutsch]
     * <p>Erzeugt einen {@code Stream}, der jeweils einen Zeitstempel als Vielfaches der Dauer angewandt auf
     * den Start und bis zum Ende dieses Intervalls geht. </p>
     *
     * <p>Die Gr&ouml;&szlig;e des {@code Stream} ist maximal {@code Integer.MAX_VALUE - 1}, ansonsten wird
     * eine {@code ArithmeticException} geworfen. </p>
     *
     * @param   duration    duration which has to be added to the start multiple times
     * @throws  IllegalArgumentException if the duration is not positive
     * @throws  IllegalStateException if this interval is infinite or if there is no canonical form
     * @return  stream consisting of distinct timestamps which are the result of adding the duration to the start
     * @see     #toCanonical()
     * @see     #stream(Duration, PlainTimestamp, PlainTimestamp)
     * @since   4.18
     */
    public Stream<PlainTimestamp> stream(Duration<?> duration) {

        TimestampInterval interval = this.toCanonical();
        PlainTimestamp start = interval.getStartAsTimestamp();
        PlainTimestamp end = interval.getEndAsTimestamp();

        if ((start == null) || (end == null)) {
            throw new IllegalStateException("Streaming is not supported for infinite intervals.");
        }

        return TimestampInterval.stream(duration, start, end);

    }

    /**
     * <p>Obtains a stream iterating over every timestamp which is the result of addition of given duration
     * to start until the end is reached. </p>
     *
     * <p>This static method avoids the costs of constructing an instance of {@code TimestampInterval}.
     * The stream size is limited to {@code Integer.MAX_VALUE - 1} else an {@code ArithmeticException}
     * will be thrown. </p>
     *
     * @param   duration    duration which has to be added to the start multiple times
     * @param   start       start boundary - inclusive
     * @param   end         end boundary - exclusive
     * @throws  IllegalArgumentException if start is after end or if the duration is not positive
     * @return  stream consisting of distinct timestamps which are the result of adding the duration to the start
     * @since   4.18
     */
    /*[deutsch]
     * <p>Erzeugt einen {@code Stream}, der jeweils einen Zeitstempel als Vielfaches der Dauer angewandt auf
     * den Start und bis zum Ende geht. </p>
     *
     * <p>Diese statische Methode vermeidet die Kosten der Intervallerzeugung. Die Gr&ouml;&szlig;e des
     * {@code Stream} ist maximal {@code Integer.MAX_VALUE - 1}, ansonsten wird eine {@code ArithmeticException}
     * geworfen. </p>
     *
     * @param   duration    duration which has to be added to the start multiple times
     * @param   start       start boundary - inclusive
     * @param   end         end boundary - exclusive
     * @throws  IllegalArgumentException if start is after end or if the duration is not positive
     * @return  stream consisting of distinct timestamps which are the result of adding the duration to the start
     * @since   4.18
     */
    public static Stream<PlainTimestamp> stream(
        Duration<?> duration,
        PlainTimestamp start,
        PlainTimestamp end
    ) {

        if (!duration.isPositive()) {
            throw new IllegalArgumentException("Duration must be positive: " + duration);
        }

        int comp = start.compareTo(end);

        if (comp > 0) {
            throw new IllegalArgumentException("Start after end: " + start + "/" + end);
        } else if (comp == 0) {
            return Stream.empty();
        }

        double secs = 0.0;

        for (TimeSpan.Item<? extends IsoUnit> item : duration.getTotalLength()) {
            secs += item.getUnit().getLength() * item.getAmount();
        }

        double est; // first estimate

        if (secs < 1.0) {
            est = (ClockUnit.NANOS.between(start, end) / (secs * 1_000_000_000));
        } else {
            est = (ClockUnit.SECONDS.between(start, end) / secs);
        }

        if (Double.compare(est, Integer.MAX_VALUE) >= 0) {
            throw new ArithmeticException();
        }

        int n = (int) Math.floor(est);
        boolean backwards = false;

        while ((n > 0) && !start.plus(duration.multipliedBy(n)).isBefore(end)) {
            n--;
            backwards = true;
        }

        int size = n + 1;

        if (!backwards) {
            do {
                size = Math.addExact(n, 1);
                n++;
            } while (start.plus(duration.multipliedBy(n)).isBefore(end));
        }

        if (size == 1) {
            return Stream.of(start); // short-cut
        }

        return IntStream.range(0, size).mapToObj(index -> start.plus(duration.multipliedBy(index)));

    }

    /**
     * <p>Creates a partitioning stream of timestamp intervals where every day of this interval is partitioned
     * according to given partitioning rule. </p>
     *
     * <p>If a day of this interval is not fully defined (concerns only start and end) then it will be intersected
     * with given day partition rule. </p>
     *
     * @param   rule        day partition rule
     * @return  stream of timestamp intervals
     * @throws  IllegalStateException if this interval is infinite or if there is no canonical form
     * @since   5.0
     */
    /*[deutsch]
     * <p>Erzeugt einen {@code Stream}, der jeden Tag dieses Intervalls entsprechend der angegebenen Regel
     * in einzelne Tagesabschnitte zerlegt. </p>
     *
     * <p>Falls ein Tag dieses Intervalls nicht von Mitternacht zu Mitternacht reicht (betrifft nur Start und Ende),
     * dann wird f&uuml;r einen solchen Tag eine gemeinsame Schnittmenge mit der angegebenen {@code DayPartitionRule}
     * gebildet. </p>
     *
     * @param   rule        day partition rule
     * @return  stream of timestamp intervals
     * @throws  IllegalStateException if this interval is infinite or if there is no canonical form
     * @since   5.0
     */
    public Stream<TimestampInterval> streamPartitioned(DayPartitionRule rule) {

        TimestampInterval interval = this.toCanonical();
        PlainTimestamp start = interval.getStartAsTimestamp();
        PlainTimestamp end = interval.getEndAsTimestamp();

        if ((start == null) || (end == null)) {
            throw new IllegalStateException("Streaming is not supported for infinite intervals.");
        }

        PlainDate d1 = start.getCalendarDate();
        PlainDate d2 = end.getCalendarDate();
        long days = CalendarUnit.DAYS.between(d1, d2);

        if (days == 0) {
            ClockInterval w = ClockInterval.between(start.getWallTime(), end.getWallTime());
            return getPartitions(d1, w, rule).stream();
        }

        ClockInterval w1 = ClockInterval.between(start.getWallTime(), PlainTime.midnightAtEndOfDay());
        ClockInterval w2 = ClockInterval.between(PlainTime.midnightAtStartOfDay(), end.getWallTime());

        Stream<TimestampInterval> a = getPartitions(d1, w1, rule).stream();
        Stream<TimestampInterval> c = getPartitions(d2, w2, rule).stream();

        if (days > 1) {
            Stream<TimestampInterval> b =
                DateInterval.between(d1.plus(CalendarDays.ONE), d2.minus(CalendarDays.ONE)).streamPartitioned(rule);
            a = Stream.concat(a, b);
        }

        return Stream.concat(a, c);

    }

    /**
     * <p>Obtains a random timestamp within this interval. </p>
     *
     * @return  random timestamp within this interval
     * @throws  IllegalStateException if this interval is infinite or empty or if there is no canonical form
     * @see     #toCanonical()
     * @since   5.0
     */
    /*[deutsch]
     * <p>Liefert einen Zufallszeitstempel innerhalb dieses Intervalls. </p>
     *
     * @return  random timestamp within this interval
     * @throws  IllegalStateException if this interval is infinite or empty or if there is no canonical form
     * @see     #toCanonical()
     * @since   5.0
     */
    public PlainTimestamp random() {

        try {
            // use UTC-offset for internal conversion only
            MomentInterval interval = this.toCanonical().atUTC();
            return interval.random().toZonalTimestamp(ZonalOffset.UTC);
        } catch (IllegalStateException ise){
            throw new IllegalStateException(
                "Cannot get random timestamp in an empty or infinite interval: " + this,
                ise);
        }

    }

    /**
     * <p>Prints the canonical form of this interval in given ISO-8601 style. </p>
     *
     * @param   dateStyle       iso-compatible date style
     * @param   decimalStyle    iso-compatible decimal style
     * @param   precision       controls the precision of output format with constant length
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
     * @param   dateStyle       iso-compatible date style
     * @param   decimalStyle    iso-compatible decimal style
     * @param   precision       controls the precision of output format with constant length
     * @param   infinityStyle   controlling the format of infinite boundaries
     * @return  String
     * @throws  IllegalStateException   if there is no canonical form
     *                                  or given infinity style prevents infinite intervals
     * @see     #toCanonical()
     * @since   4.18
     */
    public String formatISO(
        IsoDateStyle dateStyle,
        IsoDecimalStyle decimalStyle,
        ClockUnit precision,
        InfinityStyle infinityStyle
    ) {

        TimestampInterval interval = this.toCanonical();
        StringBuilder buffer = new StringBuilder(60);
        ChronoPrinter<PlainTimestamp> printer = Iso8601Format.ofTimestamp(dateStyle, decimalStyle, precision);
        if (interval.getStart().isInfinite()) {
            buffer.append(infinityStyle.displayPast(printer, PlainTimestamp.axis()));
        } else {
            printer.print(interval.getStartAsTimestamp(), buffer);
        }
        buffer.append('/');
        if (interval.getEnd().isInfinite()) {
            buffer.append(infinityStyle.displayFuture(printer, PlainTimestamp.axis()));
        } else {
            printer.print(interval.getEndAsTimestamp(), buffer);
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
     *     TimestampInterval interval =
     *          TimestampInterval.between(
     *              PlainTimestamp.of(2016, 2, 29, 10, 45, 53),
     *              PlainTimestamp.of(2016, 2, 29, 16, 30));
     *     System.out.println(
     *          interval.formatReduced(
     *              IsoDateStyle.EXTENDED_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MINUTES, InfinityStyle.SYMBOL));
     *     // Output: 2016-02-29T10:45/T16:30
     * </pre>
     *
     * @param   dateStyle       iso-compatible date style
     * @param   decimalStyle    iso-compatible decimal style
     * @param   precision       controls the precision of output format with constant length
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
     *     TimestampInterval interval =
     *          TimestampInterval.between(
     *              PlainTimestamp.of(2016, 2, 29, 10, 45, 53),
     *              PlainTimestamp.of(2016, 2, 29, 16, 30));
     *     System.out.println(
     *          interval.formatReduced(
     *              IsoDateStyle.EXTENDED_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MINUTES, InfinityStyle.SYMBOL));
     *     // Output: 2016-02-29T10:45/T16:30
     * </pre>
     *
     * @param   dateStyle       iso-compatible date style
     * @param   decimalStyle    iso-compatible decimal style
     * @param   precision       controls the precision of output format with constant length
     * @param   infinityStyle   controlling the format of infinite boundaries
     * @return  String
     * @throws  IllegalStateException   if there is no canonical form
     *                                  or given infinity style prevents infinite intervals
     * @see     #toCanonical()
     * @since   4.18
     */
    public String formatReduced(
        IsoDateStyle dateStyle,
        IsoDecimalStyle decimalStyle,
        ClockUnit precision,
        InfinityStyle infinityStyle
    ) {

        TimestampInterval interval = this.toCanonical();
        PlainTimestamp start = interval.getStartAsTimestamp();
        PlainTimestamp end = interval.getEndAsTimestamp();

        StringBuilder buffer = new StringBuilder(60);

        ChronoPrinter<PlainTime> timePrinter = (
            dateStyle.isExtended()
                ? Iso8601Format.ofExtendedTime(decimalStyle, precision)
                : Iso8601Format.ofBasicTime(decimalStyle, precision));
        ChronoPrinter<PlainTimestamp> printer = null;

        if (interval.getStart().isInfinite()) {
            printer = Iso8601Format.ofTimestamp(dateStyle, decimalStyle, precision);
            buffer.append(infinityStyle.displayPast(printer, PlainTimestamp.axis()));
        } else {
            Iso8601Format.ofDate(dateStyle).print(start.getCalendarDate(), buffer);
            buffer.append('T');
            timePrinter.print(start.getWallTime(), buffer);
        }

        buffer.append('/');

        if (interval.isFinite()) {
            PlainDate d1 = start.getCalendarDate();
            PlainDate d2 = end.getCalendarDate();
            if (!d1.equals(d2)) {
                DateInterval.getEndPrinter(dateStyle, d1, d2).print(d2, buffer);
            }
            buffer.append('T');
            timePrinter.print(end.getWallTime(), buffer);
        } else if (interval.getEnd().isInfinite()) {
            if (printer == null) {
                printer = Iso8601Format.ofTimestamp(dateStyle, decimalStyle, precision);
            }
            buffer.append(infinityStyle.displayFuture(printer, PlainTimestamp.axis()));
        } else {
            if (printer == null) {
                printer = Iso8601Format.ofTimestamp(dateStyle, decimalStyle, precision);
            }
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
    public static TimestampInterval parse(
        String text,
        ChronoParser<PlainTimestamp> parser
    ) throws ParseException {

        return parse(text, parser, IsoInterval.getIntervalPattern(parser));

    }

    /**
     * <p>Interpretes given text as interval using given interval pattern. </p>
     *
     * <p>About usage see also {@link DateInterval#parse(String, ChronoParser, String)}. </p>
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
     * <p>Zur Verwendung siehe auch: {@link DateInterval#parse(String, ChronoParser, String)}. </p>
     *
     * @param   text                text to be parsed
     * @param   parser              format object for parsing start and end components
     * @param   intervalPattern     interval pattern containing placeholders {0} and {1} (for start and end)
     * @return  parsed interval
     * @throws  IndexOutOfBoundsException if given text is empty
     * @throws  ParseException if the text is not parseable
     * @since   3.9/4.6
     */
    public static TimestampInterval parse(
        String text,
        ChronoParser<PlainTimestamp> parser,
        String intervalPattern
    ) throws ParseException {

        return IntervalParser.parsePattern(text, TimestampIntervalFactory.INSTANCE, parser, intervalPattern);

    }

    /**
     * <p>Interpretes given text as interval. </p>
     *
     * <p>This method can also accept a hyphen as alternative to solidus as separator
     * between start and end component unless the start component is a period. Infinity
     * symbols are understood. </p>
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
    public static TimestampInterval parse(
        CharSequence text,
        ChronoParser<PlainTimestamp> parser,
        BracketPolicy policy
    ) throws ParseException {

        ParseLog plog = new ParseLog();
        TimestampInterval interval =
            IntervalParser.of(
                TimestampIntervalFactory.INSTANCE,
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
    public static TimestampInterval parse(
        CharSequence text,
        ChronoParser<PlainTimestamp> startFormat,
        char separator,
        ChronoParser<PlainTimestamp> endFormat,
        BracketPolicy policy,
        ParseLog status
    ) {

        return IntervalParser.of(
            TimestampIntervalFactory.INSTANCE,
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
     * component instead). Infinity symbols are understood as extension
     * although strictly spoken ISO-8601 does not know or specify infinite
     * intervals. Examples for supported formats: </p>
     *
     * <pre>
     *  System.out.println(
     *      TimestampInterval.parseISO(
     *          &quot;2012-01-01T14:15/2014-06-20T16:00&quot;));
     *  // output: [2012-01-01T14:15/2014-06-20T16:00)
     *
     *  System.out.println(
     *      TimestampInterval.parseISO(
     *          &quot;2012-01-01T14:15/08-11T16:00&quot;));
     *  // output: [2012-01-01T14:15/2012-08-11T16:00)
     *
     *  System.out.println(
     *      TimestampInterval.parseISO(
     *          &quot;2012-01-01T14:15/16:00&quot;));
     *  // output: [2012-01-01T14:15/2012-01-01T16:00)
     *
     *  System.out.println(
     *      TimestampInterval.parseISO(
     *          &quot;2012-01-01T14:15/P2DT1H45M&quot;));
     *  // output: [2012-01-01T14:15/2012-01-03T16:00)
     *
     *  System.out.println(
     *      TimestampInterval.parseISO(
     *          &quot;2015-01-01T08:45/-&quot;));
     *  // output: [2015-01-01T08:45:00/+&#x221E;)
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
     * werden. Unendlichkeitssymbole werden verstanden, obwohl ISO-8601 keine
     * unendlichen Intervalle kennt. Beispiele f&uuml;r unterst&uuml;tzte Formate: </p>
     *
     * <pre>
     *  System.out.println(
     *      TimestampInterval.parseISO(
     *          &quot;2012-01-01T14:15/2014-06-20T16:00&quot;));
     *  // Ausgabe: [2012-01-01T14:15/2014-06-20T16:00)
     *
     *  System.out.println(
     *      TimestampInterval.parseISO(
     *          &quot;2012-01-01T14:15/08-11T16:00&quot;));
     *  // Ausgabe: [2012-01-01T14:15/2012-08-11T16:00)
     *
     *  System.out.println(
     *      TimestampInterval.parseISO(
     *          &quot;2012-01-01T14:15/16:00&quot;));
     *  // Ausgabe: [2012-01-01T14:15/2012-01-01T16:00)
     *
     *  System.out.println(
     *      TimestampInterval.parseISO(
     *          &quot;2012-01-01T14:15/P2DT1H45M&quot;));
     *  // Ausgabe: [2012-01-01T14:15/2012-01-03T16:00)
     *
     *  System.out.println(
     *      TimestampInterval.parseISO(
     *          &quot;2015-01-01T08:45/-&quot;));
     *  // Ausgabe: [2015-01-01T08:45:00/+&#x221E;)
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
    public static TimestampInterval parseISO(String text)
        throws ParseException {

        if (text.isEmpty()) {
            throw new IndexOutOfBoundsException("Empty text.");
        }

        // prescan for format analysis
        int start = 0;
        int n = Math.min(text.length(), 107);
        boolean sameFormat = true;
        int firstDate = 1; // loop starts one index position later
        int secondDate = 0;
        int timeLength = 0;
        boolean startsWithHyphen = (text.charAt(0) == '-');

        if ((text.charAt(0) == 'P') || startsWithHyphen) {
            for (int i = 1; i < n; i++) {
                if (text.charAt(i) == '/') {
                    if (i + 1 == n) {
                        throw new ParseException("Missing end component.", n);
                    } else if (startsWithHyphen) {
                        if ((text.charAt(1) == '\u221E') || (i == 1)) {
                            start = i + 1;
                        }
                    } else {
                        start = i + 1;
                    }
                    break;
                }
            }
        }

        int literals = 0;
        int literals2 = 0;
        boolean ordinalStyle = false;
        boolean weekStyle = false;
        boolean weekStyle2 = false;
        boolean secondComponent = false;
        int slash = -1;

        for (int i = start + 1; i < n; i++) {
            char c = text.charAt(i);
            if (secondComponent) {
                if (
                    (c == 'P')
                    || ((c == '-') && (i == n - 1))
                    || ((c == '+') && (i == n - 2) && (text.charAt(i + 1) == '\u221E'))
                ) {
                    secondComponent = false;
                    break;
                } else if ((c == 'T') || (timeLength > 0)) {
                    timeLength++;
                } else {
                    if (c == 'W') {
                        weekStyle2 = true;
                    } else if ((c == '-') && (i > slash + 1)) {
                        literals2++;
                    }
                    secondDate++;
                }
            } else if (c == '/') {
                if (slash == -1) {
                    slash = i;
                    secondComponent = true;
                    timeLength = 0;
                } else {
                    throw new ParseException("Interval with two slashes found: " + text, i);
                }
            } else if ((c == 'T') || (timeLength > 0)) {
                timeLength++;
            } else if (c == '-') {
                firstDate++;
                literals++;
            } else if (c == 'W') {
                firstDate++;
                weekStyle = true;
            } else {
                firstDate++;
            }
        }

        if (secondComponent && (weekStyle != weekStyle2)) {
            throw new ParseException("Mixed date styles not allowed.", n);
        }

        char c = text.charAt(start);
        int componentLength = firstDate - 4;

        if ((c == '+') || (c == '-')) {
            componentLength -= 2;
        }

        if (!weekStyle) {
            ordinalStyle = ((literals == 1) || ((literals == 0) && (componentLength == 3)));
        }

        boolean extended = (literals > 0);
        boolean hasT = true;

        if (secondComponent) {
            if (timeLength == 0) { // no T in end component => no date part
                hasT = false;
                timeLength = secondDate;
                secondDate = 0;
            }
            sameFormat = ((firstDate == secondDate) && (literals == literals2));
        }

        // prepare component parsers
        ChronoFormatter<PlainTimestamp> startFormat = (
            extended ? Iso8601Format.EXTENDED_DATE_TIME : Iso8601Format.BASIC_DATE_TIME);
        ChronoFormatter<PlainTimestamp> endFormat = (sameFormat ? startFormat : null); // null means reduced iso format

        // create interval
        Parser parser = new Parser(startFormat, endFormat, extended, weekStyle, ordinalStyle, timeLength, hasT);
        return parser.parse(text);

    }

    // combines this local timestamp interval with given timezone to a global UTC-interval
    MomentInterval in(Timezone tz) {

        Boundary<Moment> b1;
        Boundary<Moment> b2;

        if (this.getStart().isInfinite()) {
            b1 = Boundary.infinitePast();
        } else {
            Moment m1 = this.getStart().getTemporal().in(tz);
            b1 = Boundary.of(this.getStart().getEdge(), m1);
        }

        if (this.getEnd().isInfinite()) {
            b2 = Boundary.infiniteFuture();
        } else {
            Moment m2 = this.getEnd().getTemporal().in(tz);
            b2 = Boundary.of(this.getEnd().getEdge(), m2);
        }

        return new MomentInterval(b1, b2);

    }

    @Override
    IntervalFactory<PlainTimestamp, TimestampInterval> getFactory() {

        return TimestampIntervalFactory.INSTANCE;

    }

    @Override
    TimestampInterval getContext() {

        return this;

    }

    private static List<TimestampInterval> getPartitions(
        PlainDate date,
        ClockInterval window,
        DayPartitionRule rule
    ) {

        List<TimestampInterval> intervals = new ArrayList<>();
        TimestampInterval outer = window.on(date);

        for (ChronoInterval<PlainTime> partition : rule.getPartitions(date)) {
            TimestampInterval ti =
                TimestampInterval.between(
                    date.at(partition.getStart().getTemporal()),
                    date.at(partition.getEnd().getTemporal()));
            outer.findIntersection(ti).ifPresent(intervals::add);
        }

        return intervals;

    }

    /**
     * @serialData  Uses
     *              <a href="../../../serialized-form.html#net.time4j.range.SPX">
     *              a dedicated serialization form</a> as proxy. The first byte
     *              contains the type-ID {@code 34} in the six most significant
     *              bits. The next bytes represent the start and the end
     *              boundary.
     *
     * Schematic algorithm:
     *
     * <pre>
       int header = 34;
       header &lt;&lt;= 2;
       out.writeByte(header);
       writeBoundary(getStart(), out);
       writeBoundary(getEnd(), out);

       private static void writeBoundary(
           Boundary&lt;?&lt; boundary,
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

        return new SPX(this, SPX.TIMESTAMP_TYPE);

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
        extends IntervalParser<PlainTimestamp, TimestampInterval> {

        //~ Instanzvariablen ----------------------------------------------

        private final boolean extended;
        private final boolean weekStyle;
        private final boolean ordinalStyle;
        private final int protectedArea;
        private final boolean hasT;

        //~ Konstruktoren -------------------------------------------------

        Parser(
            ChronoParser<PlainTimestamp> startFormat,
            ChronoParser<PlainTimestamp> endFormat, // optional
            boolean extended,
            boolean weekStyle,
            boolean ordinalStyle,
            int protectedArea,
            boolean hasT
        ) {
            super(TimestampIntervalFactory.INSTANCE, startFormat, endFormat, BracketPolicy.SHOW_NEVER, '/');

            this.extended = extended;
            this.weekStyle = weekStyle;
            this.ordinalStyle = ordinalStyle;
            this.protectedArea = protectedArea;
            this.hasT = hasT;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        protected PlainTimestamp parseReducedEnd(
            CharSequence text,
            PlainTimestamp start,
            ParseLog lowerLog,
            ParseLog upperLog,
            AttributeQuery attrs
        ) {

            ChronoFormatter<PlainTimestamp> reducedParser =
                this.createEndFormat(
                    PlainTimestamp.axis().preformat(start, attrs),
                    lowerLog.getRawValues());
            return reducedParser.parse(text, upperLog);

        }

        private ChronoFormatter<PlainTimestamp> createEndFormat(
            ChronoDisplay defaultSupplier,
            ChronoEntity<?> rawData
        ) {

            ChronoFormatter.Builder<PlainTimestamp> builder =
                ChronoFormatter.setUp(PlainTimestamp.class, Locale.ROOT);

            ChronoElement<Integer> year = (this.weekStyle ? YEAR_OF_WEEKDATE : YEAR);
            if (this.extended) {
                int p = (this.ordinalStyle ? 3 : 5) + this.protectedArea;
                builder.startSection(Attributes.PROTECTED_CHARACTERS, p);
                builder.addCustomized(
                    year,
                    NOOP,
                    (this.weekStyle ? YearParser.YEAR_OF_WEEKDATE : YearParser.YEAR));
            } else {
                int p = (this.ordinalStyle ? 3 : 4) + this.protectedArea;
                builder.startSection(Attributes.PROTECTED_CHARACTERS, p);
                builder.addInteger(year, 4, 9, SignPolicy.SHOW_WHEN_BIG_NUMBER);
            }
            builder.endSection();

            if (this.weekStyle) {
                builder.startSection(
                    Attributes.PROTECTED_CHARACTERS,
                    1 + this.protectedArea);
                builder.addCustomized(
                    Weekmodel.ISO.weekOfYear(),
                    NOOP,
                    extended
                        ? FixedNumParser.EXTENDED_WEEK_OF_YEAR
                        : FixedNumParser.BASIC_WEEK_OF_YEAR);
                builder.endSection();
                builder.startSection(Attributes.PROTECTED_CHARACTERS, this.protectedArea);
                builder.addFixedNumerical(DAY_OF_WEEK, 1);
                builder.endSection();
            } else if (this.ordinalStyle) {
                builder.startSection(Attributes.PROTECTED_CHARACTERS, this.protectedArea);
                builder.addFixedInteger(DAY_OF_YEAR, 3);
                builder.endSection();
            } else {
                builder.startSection(
                    Attributes.PROTECTED_CHARACTERS,
                    2 + this.protectedArea);
                if (this.extended) {
                    builder.addCustomized(
                        MONTH_AS_NUMBER,
                        NOOP,
                        FixedNumParser.CALENDAR_MONTH);
                } else {
                    builder.addFixedInteger(MONTH_AS_NUMBER, 2);
                }
                builder.endSection();
                builder.startSection(Attributes.PROTECTED_CHARACTERS, this.protectedArea);
                builder.addFixedInteger(DAY_OF_MONTH, 2);
                builder.endSection();
            }

            if (this.hasT) {
                builder.addLiteral('T');
            }

            builder.addCustomized(
                PlainTime.COMPONENT,
                extended
                    ? Iso8601Format.EXTENDED_WALL_TIME
                    : Iso8601Format.BASIC_WALL_TIME);

            for (ChronoElement<?> key : TimestampIntervalFactory.INSTANCE.stdElements(rawData)) {
                setDefault(builder, key, defaultSupplier);
            }

            return builder.build();

        }

        // wilcard capture
        private static <V> void setDefault(
            ChronoFormatter.Builder<PlainTimestamp> builder,
            ChronoElement<V> element,
            ChronoDisplay defaultSupplier
        ) {

            builder.setDefault(element, defaultSupplier.get(element));

        }

    }

    private static class SystemTimezoneHolder {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final Timezone SYS_TZ;

        static {
            if (Boolean.getBoolean("net.time4j.allow.system.tz.override")) {
                SYS_TZ = null;
            } else {
                SYS_TZ = create();
            }
        }

        //~ Methoden ------------------------------------------------------

        static Timezone get() {
            return ((SYS_TZ == null) ? create() : SYS_TZ);
        }

        private static Timezone create() {
            return Timezone.ofSystem().with(GapResolver.NEXT_VALID_TIME.and(OverlapResolver.EARLIER_OFFSET));
        }

    }

}
