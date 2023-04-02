/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2023 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (MomentInterval.java) is part of project Time4J.
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

import net.time4j.ClockUnit;
import net.time4j.Duration;
import net.time4j.IsoUnit;
import net.time4j.MachineTime;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.SI;
import net.time4j.Weekmodel;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.TimeSpan;
import net.time4j.format.Attributes;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.ChronoParser;
import net.time4j.format.expert.ChronoPrinter;
import net.time4j.format.expert.ElementPosition;
import net.time4j.format.expert.Iso8601Format;
import net.time4j.format.expert.IsoDateStyle;
import net.time4j.format.expert.IsoDecimalStyle;
import net.time4j.format.expert.ParseLog;
import net.time4j.format.expert.SignPolicy;
import net.time4j.scale.TimeScale;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.time.Instant;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static net.time4j.PlainDate.*;
import static net.time4j.format.Attributes.PROTECTED_CHARACTERS;
import static net.time4j.range.IntervalEdge.CLOSED;
import static net.time4j.range.IntervalEdge.OPEN;


/**
 * <p>Defines a moment interval on global timeline. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 */
/*[deutsch]
 * <p>Definiert ein Momentintervall auf dem globalen Zeitstrahl. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 */
public final class MomentInterval
    extends IsoInterval<Moment, MomentInterval>
    implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * Constant for a moment interval from infinite past to infinite future.
     *
     * @since   3.36/4.31
     */
    /*[deutsch]
     * Konstante f&uuml;r ein Momentintervall, das von der unbegrenzten Vergangenheit
     * bis in die unbegrenzte Zukunft reicht.
     *
     * @since   3.36/4.31
     */
    public static final MomentInterval ALWAYS =
        MomentIntervalFactory.INSTANCE.between(Boundary.infinitePast(), Boundary.infiniteFuture());

    /**
     * Determines the alignment of surrounding intervals.
     *
     * @see     #surrounding(Moment, MachineTime, double)
     */
    /*[deutsch]
     * Legt die Ausrichtung von Umgebungsintervallen fest.
     *
     * @see     #surrounding(Moment, MachineTime, double)
     */
    public static double LEFT_ALIGNED = 1.0;

    /**
     * Determines the alignment of surrounding intervals.
     *
     * @see     #surrounding(Moment, MachineTime, double)
     */
    /*[deutsch]
     * Legt die Ausrichtung von Umgebungsintervallen fest.
     *
     * @see     #surrounding(Moment, MachineTime, double)
     */
    public static double CENTERED = 0.5;

    /**
     * Determines the alignment of surrounding intervals.
     *
     * @see     #surrounding(Moment, MachineTime, double)
     */
    /*[deutsch]
     * Legt die Ausrichtung von Umgebungsintervallen fest.
     *
     * @see     #surrounding(Moment, MachineTime, double)
     */
    public static double RIGHT_ALIGNED = 0.0;

    private static final int MRD = 1_000_000_000;
    private static final long serialVersionUID = -5403584519478162113L;

    private static final Comparator<ChronoInterval<Moment>> COMPARATOR =
        new IntervalComparator<>(Moment.axis());

    private static final ChronoPrinter<Integer> NOOP =
        (formattable, buffer, attributes) -> Collections.emptySet();

    //~ Konstruktoren -----------------------------------------------------

    // package-private
    MomentInterval(
        Boundary<Moment> start,
        Boundary<Moment> end
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
    public static Comparator<ChronoInterval<Moment>> comparator() {

        return COMPARATOR;

    }

    /**
     * <p>Creates a finite half-open interval between given time points. </p>
     *
     * @param   start   moment of lower boundary (inclusive)
     * @param   end     moment of upper boundary (exclusive)
     * @return  new moment interval
     * @throws  IllegalArgumentException if start is after end
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt ein begrenztes halb-offenes Intervall zwischen den
     * angegebenen Zeitpunkten. </p>
     *
     * @param   start   moment of lower boundary (inclusive)
     * @param   end     moment of upper boundary (exclusive)
     * @return  new moment interval
     * @throws  IllegalArgumentException if start is after end
     * @since   2.0
     */
    public static MomentInterval between(
        Moment start,
        Moment end
    ) {

        return new MomentInterval(
            Boundary.of(CLOSED, start),
            Boundary.of(OPEN, end));

    }

    /**
     * <p>Creates a finite half-open interval between given time points. </p>
     *
     * @param   start   moment of lower boundary (inclusive)
     * @param   end     moment of upper boundary (exclusive)
     * @return  new moment interval
     * @throws  IllegalArgumentException if start is after end
     * @see     #between(Moment, Moment)
     * @since   4.11
     */
    /*[deutsch]
     * <p>Erzeugt ein begrenztes halb-offenes Intervall zwischen den angegebenen Zeitpunkten. </p>
     *
     * @param   start   moment of lower boundary (inclusive)
     * @param   end     moment of upper boundary (exclusive)
     * @return  new moment interval
     * @throws  IllegalArgumentException if start is after end
     * @see     #between(Moment, Moment)
     * @since   4.11
     */
    public static MomentInterval between(
        Instant start,
        Instant end
    ) {

        return MomentInterval.between(Moment.from(start), Moment.from(end));

    }

    /**
     * <p>Creates an infinite half-open interval since given start. </p>
     *
     * @param   start       moment of lower boundary (inclusive)
     * @return  new moment interval
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt ein unbegrenztes halb-offenes Intervall ab dem angegebenen
     * Startzeitpunkt. </p>
     *
     * @param   start       moment of lower boundary (inclusive)
     * @return  new moment interval
     * @since   2.0
     */
    public static MomentInterval since(Moment start) {

        Boundary<Moment> future = Boundary.infiniteFuture();
        return new MomentInterval(Boundary.of(CLOSED, start), future);

    }

    /**
     * <p>Creates an infinite half-open interval since given start. </p>
     *
     * @param   start       moment of lower boundary (inclusive)
     * @return  new moment interval
     * @see     #since(Moment)
     * @since   4.11
     */
    /*[deutsch]
     * <p>Erzeugt ein unbegrenztes halb-offenes Intervall ab dem angegebenen Startzeitpunkt. </p>
     *
     * @param   start       moment of lower boundary (inclusive)
     * @return  new moment interval
     * @see     #since(Moment)
     * @since   4.11
     */
    public static MomentInterval since(Instant start) {

        return MomentInterval.since(Moment.from(start));

    }

    /**
     * <p>Creates an infinite open interval until given end. </p>
     *
     * @param   end     moment of upper boundary (exclusive)
     * @return  new moment interval
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt ein unbegrenztes offenes Intervall bis zum angegebenen
     * Endzeitpunkt. </p>
     *
     * @param   end     moment of upper boundary (exclusive)
     * @return  new moment interval
     * @since   2.0
     */
    public static MomentInterval until(Moment end) {

        Boundary<Moment> past = Boundary.infinitePast();
        return new MomentInterval(past, Boundary.of(OPEN, end));

    }

    /**
     * <p>Creates an infinite open interval until given end. </p>
     *
     * @param   end     moment of upper boundary (exclusive)
     * @return  new moment interval
     * @see     #until(Moment)
     * @since   4.11
     */
    /*[deutsch]
     * <p>Erzeugt ein unbegrenztes offenes Intervall bis zum angegebenen Endzeitpunkt. </p>
     *
     * @param   end     moment of upper boundary (exclusive)
     * @return  new moment interval
     * @see     #until(Moment)
     * @since   4.11
     */
    public static MomentInterval until(Instant end) {

        return MomentInterval.until(Moment.from(end));

    }

    /**
     * <p>Creates an interval surrounding given moment. </p>
     *
     * <p><strong>Alignment: </strong></p>
     * <ul>
     *     <li>If the alignment is {@code 0.0} then the new interval will start at given moment.</li>
     *     <li>If the alignment is {@code 0.5} then the new interval will be centered around given moment.</li>
     *     <li>If the alignment is {@code 1.0} then the new interval will end at given moment.</li>
     * </ul>
     *
     * @param   moment      embedded moment at focus of alignment
     * @param   duration    machine time duration
     * @param   alignment   determines how to align the interval around moment (in range {@code 0.0 <= x <= 1.0})
     * @return  new moment interval
     * @throws  IllegalArgumentException if the duration is negative or the alignment is not finite or out of range
     * @throws  UnsupportedOperationException if any given or calculated moment is before 1972 (only for SI-duration)
     * @see     #LEFT_ALIGNED
     * @see     #CENTERED
     * @see     #RIGHT_ALIGNED
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Erzeugt ein Intervall, das den angegebenen Zeitpunkt umgibt. </p>
     *
     * <p><strong>Ausrichtung: </strong></p>
     * <ul>
     *     <li>Wenn die Ausrichtung {@code 0.0} ist, beginnt das neue Interval mit dem angegebenen Moment.</li>
     *     <li>Wenn die Ausrichtung {@code 0.5} ist, ist das neue Interval um den angegebenen Moment zentriert.</li>
     *     <li>Wenn die Ausrichtung {@code 1.0} ist, endet das neue Interval mit dem angegebenen Moment.</li>
     * </ul>
     *
     * @param   moment      embedded moment at focus of alignment
     * @param   duration    machine time duration
     * @param   alignment   determines how to align the interval around moment (in range {@code 0.0 <= x <= 1.0})
     * @return  new moment interval
     * @throws  IllegalArgumentException if the duration is negative or the alignment is not finite or out of range
     * @throws  UnsupportedOperationException if any given or calculated moment is before 1972 (only for SI-duration)
     * @see     #LEFT_ALIGNED
     * @see     #CENTERED
     * @see     #RIGHT_ALIGNED
     * @since   3.34/4.29
     */
    public static MomentInterval surrounding(
        Moment moment,
        MachineTime<?> duration,
        double alignment
    ) {

        if ((Double.compare(alignment, 0.0) < 0) || (Double.compare(alignment, 1.0) > 0)) {
            throw new IllegalArgumentException("Out of range: " + alignment);
        }

        Moment start = subtract(moment, duration.multipliedBy(alignment));
        return MomentInterval.between(start, (alignment == 1.0) ? moment : add(start, duration));

    }

    /**
     * <p>Creates an interval surrounding given instant. </p>
     *
     * <p>Equivalent to {@link #surrounding(Moment, MachineTime, double)
     * surrounding(Moment.from(instant), MachineTime.from(duration), alignment)}. </p>
     *
     * @param   instant     embedded instant at focus of alignment
     * @param   duration    machine time duration
     * @param   alignment   determines how to align the interval around instant (in range {@code 0.0 <= x <= 1.0})
     * @return  new moment interval
     * @throws  IllegalArgumentException if the duration is negative or the alignment is not finite or out of range
     * @see     #LEFT_ALIGNED
     * @see     #CENTERED
     * @see     #RIGHT_ALIGNED
     * @since   4.31
     */
    /*[deutsch]
     * <p>Erzeugt ein Intervall, das den angegebenen Zeitpunkt umgibt. </p>
     *
     * <p>&Auml;quivalent zu {@link #surrounding(Moment, MachineTime, double)
     * surrounding(Moment.from(instant), MachineTime.from(duration), alignment)}. </p>
     *
     * @param   instant     embedded instant at focus of alignment
     * @param   duration    machine time duration
     * @param   alignment   determines how to align the interval around instant (in range {@code 0.0 <= x <= 1.0})
     * @return  new moment interval
     * @throws  IllegalArgumentException if the duration is negative or the alignment is not finite or out of range
     * @see     #LEFT_ALIGNED
     * @see     #CENTERED
     * @see     #RIGHT_ALIGNED
     * @since   4.31
     */
    public static MomentInterval surrounding(
        Instant instant,
        java.time.Duration duration,
        double alignment
    ) {

        return MomentInterval.surrounding(Moment.from(instant), MachineTime.from(duration), alignment);

    }

    /**
     * <p>Converts an arbitrary moment interval to an interval of this type. </p>
     *
     * @param   interval    any kind of moment interval
     * @return  MomentInterval
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Konvertiert ein beliebiges Intervall zu einem Intervall dieses Typs. </p>
     *
     * @param   interval    any kind of moment interval
     * @return  MomentInterval
     * @since   3.34/4.29
     */
    public static MomentInterval from(ChronoInterval<Moment> interval) {

        if (interval instanceof MomentInterval) {
            return MomentInterval.class.cast(interval);
        } else {
            return new MomentInterval(interval.getStart(), interval.getEnd());
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
    public Moment getStartAsMoment() {

        return this.getStart().getTemporal();

    }

    /**
     * <p>Yields the start time point. </p>
     *
     * <p>Note: Leap seconds will be lost here. </p>
     *
     * @return  start time point or {@code null} if infinite
     * @see     Boundary#isInfinite()
     * @since   4.11
     */
    /*[deutsch]
     * <p>Liefert den Startzeitpunkt. </p>
     *
     * <p>Hinweis: Schaltsekunden gehen hier verloren. </p>
     *
     * @return  start time point or {@code null} if infinite
     * @see     Boundary#isInfinite()
     * @since   4.11
     */
    public Instant getStartAsInstant() {

        Moment moment = this.getStartAsMoment();
        return ((moment == null) ? null : moment.toTemporalAccessor());

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
    public Moment getEndAsMoment() {

        return this.getEnd().getTemporal();

    }

    /**
     * <p>Yields the end time point. </p>
     *
     * <p>Note: Leap seconds will be lost here. </p>
     *
     * @return  end time point or {@code null} if infinite
     * @see     Boundary#isInfinite()
     * @since   4.11
     */
    /*[deutsch]
     * <p>Liefert den Endzeitpunkt. </p>
     *
     * <p>Hinweis: Schaltsekunden gehen hier verloren. </p>
     *
     * @return  end time point or {@code null} if infinite
     * @see     Boundary#isInfinite()
     * @since   4.11
     */
    public Instant getEndAsInstant() {

        Moment moment = this.getEndAsMoment();
        return ((moment == null) ? null : moment.toTemporalAccessor());

    }

    /**
     * <p>Converts this instance to a local timestamp interval in the system
     * timezone. </p>
     *
     * @return  local timestamp interval in system timezone (leap seconds will
     *          always be lost)
     * @since   2.0
     * @see     Timezone#ofSystem()
     * @see     #toZonalInterval(TZID)
     * @see     #toZonalInterval(String)
     */
    /*[deutsch]
     * <p>Wandelt diese Instanz in ein lokales Zeitstempelintervall um. </p>
     *
     * @return  local timestamp interval in system timezone (leap seconds will
     *          always be lost)
     * @since   2.0
     * @see     Timezone#ofSystem()
     * @see     #toZonalInterval(TZID)
     * @see     #toZonalInterval(String)
     */
    public TimestampInterval toLocalInterval() {

        Boundary<PlainTimestamp> b1;
        Boundary<PlainTimestamp> b2;

        if (this.getStart().isInfinite()) {
            b1 = Boundary.infinitePast();
        } else {
            PlainTimestamp t1 =
                this.getStart().getTemporal().toLocalTimestamp();
            b1 = Boundary.of(this.getStart().getEdge(), t1);
        }

        if (this.getEnd().isInfinite()) {
            b2 = Boundary.infiniteFuture();
        } else {
            PlainTimestamp t2 = this.getEnd().getTemporal().toLocalTimestamp();
            b2 = Boundary.of(this.getEnd().getEdge(), t2);
        }

        return new TimestampInterval(b1, b2);

    }

    /**
     * <p>Converts this instance to a zonal timestamp interval
     * in given timezone. </p>
     *
     * @param   tzid    timezone id
     * @return  zonal timestamp interval in given timezone (leap seconds will
     *          always be lost)
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   2.0
     * @see     #toLocalInterval()
     */
    /*[deutsch]
     * <p>Wandelt diese Instanz in ein zonales Zeitstempelintervall um. </p>
     *
     * @param   tzid    timezone id
     * @return  zonal timestamp interval in given timezone (leap seconds will
     *          always be lost)
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   2.0
     * @see     #toLocalInterval()
     */
    public TimestampInterval toZonalInterval(TZID tzid) {

        Boundary<PlainTimestamp> b1;
        Boundary<PlainTimestamp> b2;

        if (this.getStart().isInfinite()) {
            b1 = Boundary.infinitePast();
        } else {
            PlainTimestamp t1 =
                this.getStart().getTemporal().toZonalTimestamp(tzid);
            b1 = Boundary.of(this.getStart().getEdge(), t1);
        }

        if (this.getEnd().isInfinite()) {
            b2 = Boundary.infiniteFuture();
        } else {
            PlainTimestamp t2 =
                this.getEnd().getTemporal().toZonalTimestamp(tzid);
            b2 = Boundary.of(this.getEnd().getEdge(), t2);
        }

        return new TimestampInterval(b1, b2);

    }

    /**
     * <p>Converts this instance to a zonal timestamp interval
     * in given timezone. </p>
     *
     * @param   tzid    timezone id
     * @return  zonal timestamp interval in given timezone (leap seconds will
     *          always be lost)
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   2.0
     * @see     #toZonalInterval(TZID)
     * @see     #toLocalInterval()
     */
    /*[deutsch]
     * <p>Wandelt diese Instanz in ein zonales Zeitstempelintervall um. </p>
     *
     * @param   tzid    timezone id
     * @return  zonal timestamp interval in given timezone (leap seconds will
     *          always be lost)
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   2.0
     * @see     #toZonalInterval(TZID)
     * @see     #toLocalInterval()
     */
    public TimestampInterval toZonalInterval(String tzid) {

        Boundary<PlainTimestamp> b1;
        Boundary<PlainTimestamp> b2;

        if (this.getStart().isInfinite()) {
            b1 = Boundary.infinitePast();
        } else {
            PlainTimestamp t1 =
                this.getStart().getTemporal().toZonalTimestamp(tzid);
            b1 = Boundary.of(this.getStart().getEdge(), t1);
        }

        if (this.getEnd().isInfinite()) {
            b2 = Boundary.infiniteFuture();
        } else {
            PlainTimestamp t2 =
                this.getEnd().getTemporal().toZonalTimestamp(tzid);
            b2 = Boundary.of(this.getEnd().getEdge(), t2);
        }

        return new TimestampInterval(b1, b2);

    }

    /**
     * <p>Yields the nominal duration of this interval in given timezone and units. </p>
     *
     * @param   tz          timezone
     * @param   units       time units to be used in calculation
     * @return  nominal duration
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   3.0
     * @see     #getSimpleDuration()
     * @see     #getRealDuration()
     */
    /*[deutsch]
     * <p>Liefert die nominelle Dauer dieses Intervalls in der angegebenen Zeitzone
     * und den angegebenen Zeiteinheiten. </p>
     *
     * @param   tz          timezone
     * @param   units       time units to be used in calculation
     * @return  nominal duration
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   3.0
     * @see     #getSimpleDuration()
     * @see     #getRealDuration()
     */
    public Duration<IsoUnit> getNominalDuration(
        Timezone tz,
        IsoUnit... units
    ) {

        return this.toZonalInterval(tz.getID()).getDuration(tz, units);

    }

    /**
     * <p>Yields the length of this interval on the POSIX-scale. </p>
     *
     * @return  machine time duration on POSIX-scale
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   2.0
     * @see     #getRealDuration()
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge dieses Intervalls auf der POSIX-Skala. </p>
     *
     * @return  machine time duration on POSIX-scale
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   2.0
     * @see     #getRealDuration()
     */
    public MachineTime<TimeUnit> getSimpleDuration() {

        Moment tsp = this.getTemporalOfOpenEnd();
        boolean max = (tsp == null);

        if (max) { // max reached
            tsp = this.getEnd().getTemporal();
        }

        MachineTime<TimeUnit> result =
            MachineTime.ON_POSIX_SCALE.between(
                this.getTemporalOfClosedStart(),
                tsp);

        if (max) {
            return result.plus(1, TimeUnit.NANOSECONDS);
        }

        return result;

    }

    /**
     * <p>Yields the length of this interval on the UTC-scale. </p>
     *
     * @return  machine time duration on UTC-scale
     * @throws  UnsupportedOperationException if start is before year 1972
     *          or if this interval is infinite
     * @since   2.0
     * @see     #getSimpleDuration()
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge dieses Intervalls auf der UTC-Skala. </p>
     *
     * @return  machine time duration on UTC-scale
     * @throws  UnsupportedOperationException if start is before year 1972
     *          or if this interval is infinite
     * @since   2.0
     * @see     #getSimpleDuration()
     */
    public MachineTime<SI> getRealDuration() {

        Moment tsp = this.getTemporalOfOpenEnd();
        boolean max = (tsp == null);

        if (max) { // max reached
            tsp = this.getEnd().getTemporal();
        }

        MachineTime<SI> result =
            MachineTime.ON_UTC_SCALE.between(
                this.getTemporalOfClosedStart(),
                tsp);

        if (max) {
            return result.plus(1, SI.NANOSECONDS);
        }

        return result;

    }

    /**
     * <p>Moves this interval along the POSIX-axis by given time units. </p>
     *
     * @param   amount  amount of units
     * @param   unit    time unit for moving
     * @return  moved copy of this interval
     */
    public MomentInterval move(
        long amount,
        TimeUnit unit
    ) {

        if (amount == 0) {
            return this;
        }

        Boundary<Moment> s;
        Boundary<Moment> e;

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

        return new MomentInterval(s, e);

    }

    /**
     * <p>Moves this interval along the UTC-axis by given SI-units. </p>
     *
     * @param   amount  amount of units
     * @param   unit    time unit for moving
     * @return  moved copy of this interval
     */
    public MomentInterval move(
        long amount,
        SI unit
    ) {

        if (amount == 0) {
            return this;
        }

        Boundary<Moment> s;
        Boundary<Moment> e;

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

        return new MomentInterval(s, e);

    }

    /**
     * <p>Obtains a stream iterating over every moment which is the result of addition of given duration
     * to start until the end of this interval is reached. </p>
     *
     * <p>The stream size is limited to {@code Integer.MAX_VALUE - 1} else an {@code ArithmeticException}
     * will be thrown. </p>
     *
     * @param   duration    duration which has to be added to the start multiple times
     * @throws  IllegalArgumentException if the duration is not positive
     * @throws  IllegalStateException if this interval is infinite or if there is no canonical form
     * @return  stream consisting of distinct moments which are the result of adding the duration to the start
     * @see     #toCanonical()
     * @see     #stream(MachineTime, Moment, Moment)
     * @since   4.35
     */
    /*[deutsch]
     * <p>Erzeugt einen {@code Stream}, der jeweils einen Moment als Vielfaches der Dauer angewandt auf
     * den Start und bis zum Ende dieses Intervalls geht. </p>
     *
     * <p>Die Gr&ouml;&szlig;e des {@code Stream} ist maximal {@code Integer.MAX_VALUE - 1}, ansonsten wird
     * eine {@code ArithmeticException} geworfen. </p>
     *
     * @param   duration    duration which has to be added to the start multiple times
     * @throws  IllegalArgumentException if the duration is not positive
     * @throws  IllegalStateException if this interval is infinite or if there is no canonical form
     * @return  stream consisting of distinct moments which are the result of adding the duration to the start
     * @see     #toCanonical()
     * @see     #stream(MachineTime, Moment, Moment)
     * @since   4.35
     */
    public Stream<Moment> stream(MachineTime<?> duration) {

        MomentInterval interval = this.toCanonical();
        Moment start = interval.getStartAsMoment();
        Moment end = interval.getEndAsMoment();

        if ((start == null) || (end == null)) {
            throw new IllegalStateException("Streaming is not supported for infinite intervals.");
        }

        return MomentInterval.stream(duration, start, end);

    }

    /**
     * <p>Obtains a stream iterating over every moment which is the result of addition of given duration
     * to start until the end is reached. </p>
     *
     * <p>This static method avoids the costs of constructing an instance of {@code MomentInterval}.
     * The stream size is limited to {@code Integer.MAX_VALUE - 1} else an {@code ArithmeticException}
     * will be thrown. </p>
     *
     * @param   duration    duration which has to be added to the start multiple times
     * @param   start       start boundary - inclusive
     * @param   end         end boundary - exclusive
     * @throws  IllegalArgumentException if start is after end or if the duration is not positive
     * @return  stream consisting of distinct moments which are the result of adding the duration to the start
     * @since   4.35
     */
    /*[deutsch]
     * <p>Erzeugt einen {@code Stream}, der jeweils einen Moment als Vielfaches der Dauer angewandt auf
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
     * @return  stream consisting of distinct moments which are the result of adding the duration to the start
     * @since   4.35
     */
    public static Stream<Moment> stream(
        MachineTime<?> duration,
        Moment start,
        Moment end
    ) {

        if (!duration.isPositive()) {
            throw new IllegalArgumentException("Duration must be positive: " + duration);
        }

        int comp = start.compareTo(end);

        if (comp > 0) {
            throw new IllegalArgumentException("Start after end: " + start + "/" + end);
        } else if (comp == 0) {
            return Stream.empty();
        } else if (duration.getScale() == TimeScale.UTC) {
            return streamUTC(cast(duration), start, end);
        } else {
            return streamPOSIX(cast(duration), start, end);
        }

    }

    /**
     * <p>Obtains a random moment within this interval. </p>
     *
     * @return  random moment within this interval
     * @throws  IllegalStateException if this interval is infinite or empty or if there is no canonical form
     * @see     #toCanonical()
     * @since   5.0
     */
    /*[deutsch]
     * <p>Liefert einen Zufallsmoment innerhalb dieses Intervalls. </p>
     *
     * @return  random moment within this interval
     * @throws  IllegalStateException if this interval is infinite or empty or if there is no canonical form
     * @see     #toCanonical()
     * @since   5.0
     */
    public Moment random() {

        MomentInterval interval = this.toCanonical();

        if (interval.isFinite() && !interval.isEmpty()) {
            Moment m1 = interval.getStartAsMoment();
            Moment m2 = interval.getEndAsMoment();
            double factor = MRD;
            double d1 = m1.getPosixTime() + m1.getNanosecond() / factor;
            double d2 = m2.getPosixTime() + m2.getNanosecond() / factor;
            double randomNum = ThreadLocalRandom.current().nextDouble(d1, d2);
            long posix = (long) Math.floor(randomNum);
            int fraction = (int) (MRD * (randomNum - posix));
            Moment random = Moment.of(posix, fraction, TimeScale.POSIX);
            if (random.isBefore(m1)) {
                random = m1;
            } else if (random.isAfterOrEqual(m2)) {
                random = m2.minus(1, TimeUnit.NANOSECONDS);
            }
            return random;
        } else {
            throw new IllegalStateException("Cannot get random moment in an empty or infinite interval: " + this);
        }

    }

    /**
     * <p>Prints the canonical form of this interval in given ISO-8601 style. </p>
     *
     * @param   dateStyle       iso-compatible date style
     * @param   decimalStyle    iso-compatible decimal style
     * @param   precision       controls the precision of output format with constant length
     * @param   offset          timezone offset
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
     * @param   offset          timezone offset
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
        ZonalOffset offset,
        InfinityStyle infinityStyle
    ) {

        MomentInterval interval = this.toCanonical();
        StringBuilder buffer = new StringBuilder(65);
        ChronoPrinter<Moment> printer = Iso8601Format.ofMoment(dateStyle, decimalStyle, precision, offset);
        if (interval.getStart().isInfinite()) {
            buffer.append(infinityStyle.displayPast(printer, Moment.axis()));
        } else {
            printer.print(interval.getStartAsMoment(), buffer);
        }
        buffer.append('/');
        if (interval.getEnd().isInfinite()) {
            buffer.append(infinityStyle.displayFuture(printer, Moment.axis()));
        } else {
            printer.print(interval.getEndAsMoment(), buffer);
        }
        return buffer.toString();

    }

    /**
     * <p>Prints the canonical form of this interval in given reduced ISO-8601 style. </p>
     *
     * <p>The term &quot;reduced&quot; means that higher-order elements like the year can be
     * left out in the end component if it is equal to the value of the start component.
     * And the offset is never printed in the end component of finite intervals. Example: </p>
     *
     * <pre>
     *     MomentInterval interval =
     *          MomentInterval.between(
     *              PlainTimestamp.of(2012, 6, 29, 10, 45),
     *              PlainTimestamp.of(2012, 6, 30, 23, 59, 59).atUTC().plus(1, SI.SECONDS));
     *     System.out.println(
     *          interval.formatReduced(
     *              IsoDateStyle.EXTENDED_CALENDAR_DATE,
     *              IsoDecimalStyle.DOT,
     *              ClockUnit.SECONDS,
     *              ZonalOffset.UTC,
     *              InfinityStyle.SYMBOL));
     *     // Output: 2016-02-29T10:45:00Z/30T23:59:60
     * </pre>
     *
     * @param   dateStyle       iso-compatible date style
     * @param   decimalStyle    iso-compatible decimal style
     * @param   precision       controls the precision of output format with constant length
     * @param   offset          timezone offset
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
     * Au&szlig;erdem wird in der Endkomponente der Offset f&uuml;r begrenzte Intervalle immer weggelassen.
     * Beispiel: </p>
     *
     * <pre>
     *     MomentInterval interval =
     *          MomentInterval.between(
     *              PlainTimestamp.of(2012, 6, 29, 10, 45),
     *              PlainTimestamp.of(2012, 6, 30, 23, 59, 59).atUTC().plus(1, SI.SECONDS));
     *     System.out.println(
     *          interval.formatReduced(
     *              IsoDateStyle.EXTENDED_CALENDAR_DATE,
     *              IsoDecimalStyle.DOT,
     *              ClockUnit.SECONDS,
     *              ZonalOffset.UTC,
     *              InfinityStyle.SYMBOL));
     *     // Output: 2016-02-29T10:45:00Z/30T23:59:60
     * </pre>
     *
     * @param   dateStyle       iso-compatible date style
     * @param   decimalStyle    iso-compatible decimal style
     * @param   precision       controls the precision of output format with constant length
     * @param   offset          timezone offset
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
        ZonalOffset offset,
        InfinityStyle infinityStyle
    ) {

        MomentInterval interval = this.toCanonical();
        Moment start = interval.getStartAsMoment();
        Moment end = interval.getEndAsMoment();

        StringBuilder buffer = new StringBuilder(65);
        ChronoPrinter<Moment> printer = Iso8601Format.ofMoment(dateStyle, decimalStyle, precision, offset);

        if (interval.getStart().isInfinite()) {
            ChronoPrinter<Moment> utc = null;
            if (infinityStyle == InfinityStyle.MIN_MAX) {
                utc = Iso8601Format.ofMoment(dateStyle, decimalStyle, precision, ZonalOffset.UTC);
            }
            buffer.append(infinityStyle.displayPast(utc, Moment.axis()));
        } else {
            printer.print(start, buffer);
        }

        buffer.append('/');

        if (interval.isFinite()) {
            PlainTimestamp tsp2 = end.toZonalTimestamp(offset);
            PlainDate d1 = start.toZonalTimestamp(offset).getCalendarDate();
            PlainDate d2 = tsp2.getCalendarDate();

            if (!d1.equals(d2)) {
                DateInterval.getEndPrinter(dateStyle, d1, d2).print(d2, buffer);
            }

            buffer.append('T');

            ChronoPrinter<PlainTime> timePrinter = (
                dateStyle.isExtended()
                    ? Iso8601Format.ofExtendedTime(decimalStyle, precision)
                    : Iso8601Format.ofBasicTime(decimalStyle, precision));

            Set<ElementPosition> positions = timePrinter.print(tsp2.getWallTime(), buffer);

            if (end.isLeapSecond()) {
                for (ElementPosition position : positions) {
                    if (position.getElement() == PlainTime.SECOND_OF_MINUTE) {
                        buffer.replace(position.getStartIndex(), position.getEndIndex(), "60");
                        break;
                    }
                }
            }
        } else if (interval.getEnd().isInfinite()) {
            ChronoPrinter<Moment> utc = null;
            if (infinityStyle == InfinityStyle.MIN_MAX) {
                utc = Iso8601Format.ofMoment(dateStyle, decimalStyle, precision, ZonalOffset.UTC);
            }
            buffer.append(infinityStyle.displayFuture(utc, Moment.axis()));
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
    public static MomentInterval parse(
        String text,
        ChronoParser<Moment> parser
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
    public static MomentInterval parse(
        String text,
        ChronoParser<Moment> parser,
        String intervalPattern
    ) throws ParseException {

        return IntervalParser.parsePattern(text, MomentIntervalFactory.INSTANCE, parser, intervalPattern);

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
    public static MomentInterval parse(
        CharSequence text,
        ChronoParser<Moment> parser,
        BracketPolicy policy
    ) throws ParseException {

        ParseLog plog = new ParseLog();
        MomentInterval interval =
            IntervalParser.of(
                MomentIntervalFactory.INSTANCE,
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
    public static MomentInterval parse(
        CharSequence text,
        ChronoParser<Moment> startFormat,
        char separator,
        ChronoParser<Moment> endFormat,
        BracketPolicy policy,
        ParseLog status
    ) {

        return IntervalParser.of(
            MomentIntervalFactory.INSTANCE,
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
     * component instead). In latter case, the timezone offset of the
     * end component is optional, too. Infinity symbols are understood
     * as extension although strictly spoken ISO-8601 does not know or
     * specify infinite intervals. Examples for supported formats: </p>
     *
     * <pre>
     *  System.out.println(
     *      MomentInterval.parseISO(
     *          &quot;2012-01-01T14:15Z/2014-06-20T16:00Z&quot;));
     *  // output: [2012-01-01T14:15:00Z/2014-06-20T16:00:00Z)
     *
     *  System.out.println(
     *      MomentInterval.parseISO(
     *          &quot;2012-01-01T14:15Z/08-11T16:00+00:01&quot;));
     *  // output: [2012-01-01T14:15:00Z/2012-08-11T15:59:00Z)
     *
     *  System.out.println(
     *      MomentInterval.parseISO(
     *          &quot;2012-01-01T14:15Z/16:00&quot;));
     *  // output: [2012-01-01T14:15:00Z/2012-01-01T16:00:00Z)
     *
     *  System.out.println(
     *      MomentInterval.parseISO(
     *          &quot;2012-01-01T14:15Z/P2DT1H45M&quot;));
     *  // output: [2012-01-01T14:15:00Z/2012-01-03T16:00:00Z)
     *
     *  System.out.println(
     *      MomentInterval.parseISO(
     *          &quot;2015-01-01T08:45Z/-&quot;));
     *  // output: [2015-01-01T08:45:00Z/+&#x221E;)
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
     * werden. In letzterem Fall ist auch der Offset der Endkomponente
     * optional. Unendlichkeitssymbole werden verstanden, obwohl ISO-8601
     * keine unendlichen Intervalle kennt. Beispiele f&uuml;r unterst&uuml;tzte
     * Formate: </p>
     *
     * <pre>
     *  System.out.println(
     *      MomentInterval.parseISO(
     *          &quot;2012-01-01T14:15Z/2014-06-20T16:00Z&quot;));
     *  // Ausgabe: [2012-01-01T14:15:00Z/2014-06-20T16:00:00Z)
     *
     *  System.out.println(
     *      MomentInterval.parseISO(
     *          &quot;2012-01-01T14:15Z/08-11T16:00+00:01&quot;));
     *  // Ausgabe: [2012-01-01T14:15:00Z/2012-08-11T15:59:00Z)
     *
     *  System.out.println(
     *      MomentInterval.parseISO(
     *          &quot;2012-01-01T14:15Z/16:00&quot;));
     *  // Ausgabe: [2012-01-01T14:15:00Z/2012-01-01T16:00:00Z)
     *
     *  System.out.println(
     *      MomentInterval.parseISO(
     *          &quot;2012-01-01T14:15Z/P2DT1H45M&quot;));
     *  // Ausgabe: [2012-01-01T14:15:00Z/2012-01-03T16:00:00Z)
     *
     *  System.out.println(
     *      MomentInterval.parseISO(
     *          &quot;2015-01-01T08:45Z/-&quot;));
     *  // Ausgabe: [2015-01-01T08:45:00Z/+&#x221E;)
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
    public static MomentInterval parseISO(String text)
        throws ParseException {

        if (text.isEmpty()) {
            throw new IndexOutOfBoundsException("Empty text.");
        }

        // prescan for format analysis
        int start = 0;
        int n = Math.min(text.length(), 117);
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
            sameFormat = ((firstDate == secondDate) && (literals == literals2) && hasSecondOffset(text, n));
        }

        // prepare component parsers
        ChronoFormatter<Moment> startFormat = (
            extended ? Iso8601Format.EXTENDED_DATE_TIME_OFFSET : Iso8601Format.BASIC_DATE_TIME_OFFSET);
        ChronoFormatter<Moment> endFormat = (sameFormat ? startFormat : null); // null means reduced iso format

        // create interval
        Parser parser = new Parser(startFormat, endFormat, extended, weekStyle, ordinalStyle, timeLength, hasT);
        return parser.parse(text);

    }

    @Override
    IntervalFactory<Moment, MomentInterval> getFactory() {

        return MomentIntervalFactory.INSTANCE;

    }

    @Override
    MomentInterval getContext() {

        return this;

    }

    private static boolean hasSecondOffset(
        String text,
        int len
    ) {

        if (text.charAt(len - 1) == 'Z') {
            return true;
        }

        for (int i = len - 1, n = Math.max(0, len - 6); i >= n; i--) {
            char test = text.charAt(i);
            if ((test == 'T') || (test == '/') || (test == '.') || (test == ',')) {
                break;
            } else if ((test == '+') || (test == '-')) {
                return true;
            }
        }

        return false;

    }

    @SuppressWarnings("unchecked")
    private static Moment add(
        Moment moment,
        MachineTime<?> duration
    ) {

        if (duration.getScale() == TimeScale.UTC) {
            MachineTime<SI> mt = (MachineTime<SI>) duration;
            return moment.plus(mt);
        } else {
            MachineTime<TimeUnit> mt = (MachineTime<TimeUnit>) duration;
            return moment.plus(mt);
        }

    }

    @SuppressWarnings("unchecked")
    private static Moment subtract(
        Moment moment,
        MachineTime<?> duration
    ) {

        if (duration.getScale() == TimeScale.UTC) {
            MachineTime<SI> mt = (MachineTime<SI>) duration;
            return moment.minus(mt);
        } else {
            MachineTime<TimeUnit> mt = (MachineTime<TimeUnit>) duration;
            return moment.minus(mt);
        }

    }

    private static Stream<Moment> streamPOSIX(
        MachineTime<TimeUnit> duration,
        Moment start,
        Moment end
    ) {

        double secs = 0.0;

        for (TimeSpan.Item<? extends TimeUnit> item : duration.getTotalLength()) {
            secs += TimeUnit.SECONDS.convert(item.getAmount(), item.getUnit());
        }

        double est; // first estimate

        if (secs < 1.0) {
            est = (start.until(end, TimeUnit.NANOSECONDS) / (secs * 1_000_000_000));
        } else {
            est = (start.until(end, TimeUnit.SECONDS) / secs);
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

    private static Stream<Moment> streamUTC(
        MachineTime<SI> duration,
        Moment start,
        Moment end
    ) {

        double secs = 0.0;

        for (TimeSpan.Item<? extends SI> item : duration.getTotalLength()) {
            secs += item.getUnit().getLength() * item.getAmount();
        }

        double est; // first estimate

        if (secs < 1.0) {
            est = (SI.NANOSECONDS.between(start, end) / (secs * 1_000_000_000));
        } else {
            est = (SI.SECONDS.between(start, end) / secs);
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

    @SuppressWarnings("unchecked")
    private static <T> T cast(Object obj) {

        return (T) obj;

    }

    /**
     * @serialData  Uses
     *              <a href="../../../serialized-form.html#net.time4j.range.SPX">
     *              a dedicated serialization form</a> as proxy. The first byte
     *              contains the type-ID {@code 35} in the six most significant
     *              bits. The next bytes represent the start and the end
     *              boundary.
     *
     * Schematic algorithm:
     *
     * <pre>
       int header = 35;
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

        return new SPX(this, SPX.MOMENT_TYPE);

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
        extends IntervalParser<Moment, MomentInterval> {

        //~ Instanzvariablen ----------------------------------------------

        private final boolean extended;
        private final boolean weekStyle;
        private final boolean ordinalStyle;
        private final int protectedArea;
        private final boolean hasT;

        //~ Konstruktoren -------------------------------------------------

        Parser(
            ChronoParser<Moment> startFormat,
            ChronoParser<Moment> endFormat, // optional
            boolean extended,
            boolean weekStyle,
            boolean ordinalStyle,
            int protectedArea,
            boolean hasT
        ) {
            super(MomentIntervalFactory.INSTANCE, startFormat, endFormat, BracketPolicy.SHOW_NEVER, '/');

            this.extended = extended;
            this.weekStyle = weekStyle;
            this.ordinalStyle = ordinalStyle;
            this.protectedArea = protectedArea;
            this.hasT = hasT;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        protected Moment parseReducedEnd(
            CharSequence text,
            Moment t1,
            ParseLog lowerLog,
            ParseLog upperLog,
            AttributeQuery attrs
        ) {
            
            ChronoEntity<?> rawData = 
                lowerLog.getRawValues();            
            Set<ChronoElement<?>> stdElements =
                MomentIntervalFactory.INSTANCE.stdElements(rawData);
            TZID tzid = 
                rawData.hasTimezone() 
                ? rawData.getTimezone() 
                : attrs.get(Attributes.TIMEZONE_ID, ZonalOffset.UTC);
            Attributes attributes =
                new Attributes.Builder().setTimezone(tzid).build();
            
            ChronoFormatter<Moment> reducedParser =
                this.createEndFormat(
                    Moment.axis().preformat(t1, attributes),
                    stdElements,
                    attributes);
            return reducedParser.parse(text, upperLog);

        }

        private ChronoFormatter<Moment> createEndFormat(
            ChronoDisplay defaultSupplier,
            Set<ChronoElement<?>> stdElements,
            Attributes attributes
        ) {

            ChronoFormatter.Builder<Moment> builder =
                ChronoFormatter.setUp(Moment.class, Locale.ROOT);

            ChronoElement<Integer> year = (this.weekStyle ? YEAR_OF_WEEKDATE : YEAR);
            if (this.extended) {
                int p = (this.ordinalStyle ? 3 : 5) + this.protectedArea;
                builder.startSection(PROTECTED_CHARACTERS, p);
                builder.addCustomized(
                    year,
                    NOOP,
                    (this.weekStyle ? YearParser.YEAR_OF_WEEKDATE : YearParser.YEAR));
            } else {
                int p = (this.ordinalStyle ? 3 : 4) + this.protectedArea;
                builder.startSection(PROTECTED_CHARACTERS, p);
                builder.addInteger(year, 4, 9, SignPolicy.SHOW_WHEN_BIG_NUMBER);
            }
            builder.endSection();

            if (this.weekStyle) {
                builder.startSection(PROTECTED_CHARACTERS, 1 + this.protectedArea);
                builder.addCustomized(
                    Weekmodel.ISO.weekOfYear(),
                    NOOP,
                    this.extended
                        ? FixedNumParser.EXTENDED_WEEK_OF_YEAR
                        : FixedNumParser.BASIC_WEEK_OF_YEAR);
                builder.endSection();
                builder.startSection(PROTECTED_CHARACTERS, this.protectedArea);
                builder.addFixedNumerical(DAY_OF_WEEK, 1);
                builder.endSection();
            } else if (this.ordinalStyle) {
                builder.startSection(PROTECTED_CHARACTERS, this.protectedArea);
                builder.addFixedInteger(DAY_OF_YEAR, 3);
                builder.endSection();
            } else {
                builder.startSection(PROTECTED_CHARACTERS, 2 + this.protectedArea);
                if (this.extended) {
                    builder.addCustomized(
                        MONTH_AS_NUMBER,
                        NOOP,
                        FixedNumParser.CALENDAR_MONTH);
                } else {
                    builder.addFixedInteger(MONTH_AS_NUMBER, 2);
                }
                builder.endSection();
                builder.startSection(PROTECTED_CHARACTERS, this.protectedArea);
                builder.addFixedInteger(DAY_OF_MONTH, 2);
                builder.endSection();
            }

            if (this.hasT) {
                builder.addLiteral('T');
            }

            builder.addCustomized(
                PlainTime.COMPONENT,
                this.extended
                    ? Iso8601Format.EXTENDED_WALL_TIME
                    : Iso8601Format.BASIC_WALL_TIME);

            builder.startOptionalSection();
            builder.addTimezoneOffset(
                FormatStyle.SHORT,
                this.extended,
                Collections.singletonList("Z"));
            builder.endSection();

            for (ChronoElement<?> key : stdElements) {
                setDefault(builder, key, defaultSupplier);
            }

            return builder.build(attributes);

        }

        // wilcard capture
        private static <V> void setDefault(
            ChronoFormatter.Builder<Moment> builder,
            ChronoElement<V> element,
            ChronoDisplay defaultSupplier
        ) {

            builder.setDefault(element, defaultSupplier.get(element));

        }

    }

}
