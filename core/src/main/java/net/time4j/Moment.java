/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Moment.java) is part of project Time4J.
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

package net.time4j;

import net.time4j.base.GregorianMath;
import net.time4j.base.MathUtils;
import net.time4j.base.TimeSource;
import net.time4j.base.UnixTime;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.BridgeChronology;
import net.time4j.engine.CalendarFamily;
import net.time4j.engine.CalendarVariant;
import net.time4j.engine.Calendrical;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.ChronoMerger;
import net.time4j.engine.ChronoOperator;
import net.time4j.engine.Chronology;
import net.time4j.engine.Converter;
import net.time4j.engine.DisplayStyle;
import net.time4j.engine.ElementRule;
import net.time4j.engine.EpochDays;
import net.time4j.engine.RealTime;
import net.time4j.engine.StartOfDay;
import net.time4j.engine.Temporal;
import net.time4j.engine.TimeAxis;
import net.time4j.engine.TimeLine;
import net.time4j.engine.TimePoint;
import net.time4j.engine.UnitRule;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.CalendarType;
import net.time4j.format.ChronoPattern;
import net.time4j.format.DisplayMode;
import net.time4j.format.Leniency;
import net.time4j.format.TemporalFormatter;
import net.time4j.scale.LeapSecondEvent;
import net.time4j.scale.LeapSeconds;
import net.time4j.scale.TimeScale;
import net.time4j.scale.UniversalTime;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.TransitionStrategy;
import net.time4j.tz.ZonalOffset;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static net.time4j.PlainTime.*;
import static net.time4j.SI.NANOSECONDS;
import static net.time4j.SI.SECONDS;
import static net.time4j.scale.TimeScale.*;


/**
 * <p>Represents an instant/moment on the universal timeline with reference
 * to the timezone UTC (UTC+00:00 / Greenwich-meridian). </p>
 *
 * <p>The JDK-equivalent is traditionally the class {@code java.util.Date}.
 * In contrast to that old class this class stores the elapsed time not just
 * in millisecond but in nanosecond precision on 96-bit base. </p>
 *
 * <p>Following elements which are declared as constants are registered by
 * this class with access in UTC timezone: </p>
 *
 * <ul>
 *  <li>{@link #POSIX_TIME}</li>
 *  <li>{@link #FRACTION}</li>
 *  <li>{@link #PRECISION}</li>
 * </ul>
 *
 * <p>Furthermore, most local elements like {@code PlainTime.ISO_HOUR} etc.
 * registered in class {@code PlainTimestamp} or those defined in
 * {@link Weekmodel} are indirectly supported via the queries in the
 * interface {@link ZonalElement}. A {@code Moment} is also capable of
 * delivering the date- and time-values in a different timezone if the
 * method {@link #toZonalTimestamp(TZID)} is called. If zonal operators
 * are defined by any elements then manipulations of related data are
 * possible in any timezone. </p>
 *
 * <p><strong>Time arithmetic</strong></p>
 *
 * <p>The main time units are defined by {@link SI} (counting possible
 * UTC-leapseconds) and {@link TimeUnit}. Latter unit type can be used
 * if a better interoperability is needed for external APIs which ignore
 * leapseconds. Both kinds of time units can be used in the methods
 * {@code plus(long, unit)}, {@code minus(long, unit)} and
 * {@code until(Moment, unit)}. </p>
 *
 * @author      Meno Hochschild
 */
/*[deutsch]
 * <p>Repr&auml;sentiert einen Zeitpunkt auf der Weltzeitlinie mit Bezug
 * auf die UTC-Zeitzone (UTC+00:00 / Greenwich-Meridian). </p>
 *
 * <p>Im JDK hei&szlig;t das &Auml;quivalent {@code java.util.Date}. Diese
 * Klasse speichert im Gegensatz zum JDK die Epochenzeit nicht in Milli-,
 * sondern in Nanosekunden auf 96-Bit-Basis. </p>
 *
 * <p>Registriert sind folgende als Konstanten deklarierte Elemente mit
 * Zugriff in der UTC-Zeitzone: </p>
 *
 * <ul>
 *  <li>{@link #POSIX_TIME}</li>
 *  <li>{@link #FRACTION}</li>
 *  <li>{@link #PRECISION}</li>
 * </ul>
 *
 * <p>Dar&uuml;berhinaus sind die meisten lokalen Elemente wie
 * {@code PlainTime.ISO_HOUR} usw., die in der Klasse {@code PlainTimestamp}
 * registriert sind oder jene definiert in {@link Weekmodel}, indirekt
 * unterst&uuml;tzt, wenn &uuml;ber die entsprechenden Abfragen im Interface
 * {@link ZonalElement} eine Zeitzonenreferenz angegeben wird. Ein
 * {@code Moment} kann auch die Datums- und Zeitwerte in einer beliebigen
 * Zeitzone ausgeben, wenn die Methode {@link #toZonalTimestamp(TZID)}
 * aufgerufen wird. Wenn Elemente zonale Operatoren definieren, dann sind
 * Manipulationen der zugeh&ouml;rigen Daten in einer beliebigen Zeitzone
 * m&ouml;glich. </p>
 *
 * <p><strong>Zeitarithmetik</strong></p>
 *
 * <p>Als Zeiteinheiten kommen {@link SI} (mit Z&auml;hlung von Schaltsekunden)
 * und {@link TimeUnit} in Betracht. Letztere Einheit kann verwendet werden,
 * wenn eine bessere Interoperabilit&auml;t mit externen APIs notwendig ist,
 * die UTC-Schaltsekunden ignorieren. Beide Arten von Zeiteinheiten werden in
 * den Methoden {@code plus(long, unit)}, {@code minus(long, unit)} und
 * {@code until(Moment, unit)} verwendet. </p>
 *
 * @author      Meno Hochschild
 */
@CalendarType("iso8601")
public final class Moment
    extends TimePoint<TimeUnit, Moment>
    implements UniversalTime, Temporal<UniversalTime> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long UTC_GPS_DELTA =
        ((1980 - 1972) * 365 + 2 + 5) * 86400 + 9;
    private static final long POSIX_UTC_DELTA =
        2 * 365 * 86400;
    private static final long POSIX_GPS_DELTA =
        POSIX_UTC_DELTA + UTC_GPS_DELTA - 9; // -9 => without leap seconds

    private static final int MIO = 1000000;
    private static final int MRD = 1000000000;
    private static final int POSITIVE_LEAP_MASK = 0x40000000;

    private static final long MIN_LIMIT;
    private static final long MAX_LIMIT;

    static {
        long mjdMin = GregorianMath.toMJD(GregorianMath.MIN_YEAR, 1, 1);
        long mjdMax = GregorianMath.toMJD(GregorianMath.MAX_YEAR, 12, 31);

        MIN_LIMIT =
            EpochDays.UNIX.transform(
                mjdMin,
                EpochDays.MODIFIED_JULIAN_DATE)
            * 86400;
        MAX_LIMIT =
            EpochDays.UNIX.transform(
                mjdMax,
                EpochDays.MODIFIED_JULIAN_DATE)
            * 86400 + 86399;
    }

    private static final Moment MIN = new Moment(MIN_LIMIT, 0, POSIX);
    private static final Moment MAX = new Moment(MAX_LIMIT, MRD - 1, POSIX);

    private static final Moment START_LS_CHECK =
        new Moment(86400 + POSIX_UTC_DELTA, 0, POSIX);
    private static final Set<ChronoElement<?>> HIGH_TIME_ELEMENTS;
    private static final Map<ChronoElement<?>, Integer> LOW_TIME_ELEMENTS;
    private static final Map<TimeUnit, Double> UNIT_LENGTHS;

    static {
        Set<ChronoElement<?>> high = new HashSet<ChronoElement<?>>();
        high.add(ISO_HOUR);
        high.add(DIGITAL_HOUR_OF_DAY);
        high.add(DIGITAL_HOUR_OF_AMPM);
        high.add(CLOCK_HOUR_OF_DAY);
        high.add(CLOCK_HOUR_OF_AMPM);
        high.add(AM_PM_OF_DAY);
        high.add(MINUTE_OF_HOUR);
        high.add(MINUTE_OF_DAY);
        HIGH_TIME_ELEMENTS = Collections.unmodifiableSet(high);

        Map<ChronoElement<?>, Integer> low =
            new HashMap<ChronoElement<?>, Integer>();
        low.put(SECOND_OF_MINUTE, Integer.valueOf(1));
        low.put(SECOND_OF_DAY, Integer.valueOf(1));
        low.put(MILLI_OF_SECOND, Integer.valueOf(1000));
        low.put(MILLI_OF_DAY, Integer.valueOf(1000));
        low.put(MICRO_OF_SECOND, Integer.valueOf(MIO));
        low.put(MICRO_OF_DAY, Integer.valueOf(MIO));
        low.put(NANO_OF_SECOND, Integer.valueOf(MRD));
        low.put(NANO_OF_DAY, Integer.valueOf(MRD));
        LOW_TIME_ELEMENTS = Collections.unmodifiableMap(low);

        Map<TimeUnit, Double> unitLengths =
            new EnumMap<TimeUnit, Double>(TimeUnit.class);
        unitLengths.put(TimeUnit.DAYS, 86400.0);
        unitLengths.put(TimeUnit.HOURS, 3600.0);
        unitLengths.put(TimeUnit.MINUTES, 60.0);
        unitLengths.put(TimeUnit.SECONDS, 1.0);
        unitLengths.put(TimeUnit.MILLISECONDS, 0.001);
        unitLengths.put(TimeUnit.MICROSECONDS, 0.000001);
        unitLengths.put(TimeUnit.NANOSECONDS, 0.000000001);
        UNIT_LENGTHS = Collections.unmodifiableMap(unitLengths);
    }

    private static final TimeAxis<TimeUnit, Moment> ENGINE;

    static {
        TimeAxis.Builder<TimeUnit, Moment> builder =
            TimeAxis.Builder.setUp(
                TimeUnit.class, Moment.class, new Merger(), MIN, MAX);

        for (TimeUnit unit : TimeUnit.values()) {
            builder.appendUnit(
                unit,
                new TimeUnitRule(unit),
                UNIT_LENGTHS.get(unit),
                UNIT_LENGTHS.keySet());
        }

        builder.appendElement(
            LongElement.POSIX_TIME,
            LongElement.POSIX_TIME,
            TimeUnit.SECONDS);
        builder.appendElement(
            IntElement.FRACTION,
            IntElement.FRACTION,
            TimeUnit.NANOSECONDS);
        builder.appendElement(
            PrecisionElement.TIME_PRECISION,
            new PrecisionRule());

        ENGINE = builder.withTimeLine(new GlobalTimeLine()).build();
    }

    /**
     * <p>Start of UNIX-era = [1970-01-01T00:00:00,000000000Z]. </p>
     */
    /*[deutsch]
     * <p>Start der UNIX-&Auml;ra = [1970-01-01T00:00:00,000000000Z]. </p>
     */
    public static final Moment UNIX_EPOCH = new Moment(0, 0, TimeScale.POSIX);

    /**
     * <p>Represents the POSIX-time in seconds since UNIX-epoch. </p>
     *
     * @since   2.0
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die POSIX-Zeit in Sekunden seit der
     * UNIX-Epoche. </p>
     *
     * @since   2.0
     */
    public static final ChronoElement<Long> POSIX_TIME = LongElement.POSIX_TIME;

    /**
     * <p>Represents the nano-fraction of current second. </p>
     *
     * @since   2.0
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den Nanosekundenbruchteil der aktuellen
     * Sekunde. </p>
     *
     * @since   2.0
     */
    public static final ChronoElement<Integer> FRACTION = IntElement.FRACTION;

    /**
     * <p>Represents the precision. </p>
     *
     * @since   3.7/4.5
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die Genauigkeit. </p>
     *
     * @since   3.7/4.5
     */
    public static final ChronoElement<TimeUnit> PRECISION = PrecisionElement.TIME_PRECISION;

    private static final ChronoOperator<Moment> NEXT_LS = new NextLS();
    private static final long serialVersionUID = -3192884724477742274L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final long posixTime;
    private transient final int fraction;

    //~ Konstruktoren -----------------------------------------------------

    private Moment(
        long elapsedTime,
        int nanosecond,
        TimeScale scale
    ) {
        super();

        if (scale == POSIX) {
            this.posixTime = elapsedTime;
            this.fraction = nanosecond;
        } else {
            LeapSeconds ls = LeapSeconds.getInstance();

            if (ls.isEnabled()) {
                long utcTime;

                if (scale == UTC) {
                    utcTime = elapsedTime;
                } else if (scale == TAI) {
                    utcTime = MathUtils.safeSubtract(elapsedTime, 10);

                    if (utcTime < 0) {
                        throw new IllegalArgumentException(
                            "TAI not supported before 1972-01-01: " + elapsedTime);
                    }
                } else if (scale == GPS) {
                    utcTime = MathUtils.safeAdd(elapsedTime, UTC_GPS_DELTA);

                    if (utcTime < UTC_GPS_DELTA) {
                        throw new IllegalArgumentException(
                            "GPS not supported before 1980-01-06: " + elapsedTime);
                    }
                } else {
                    throw new UnsupportedOperationException(
                        "Not yet implemented: " + scale.name());
                }

                long unix = ls.strip(utcTime);
                long diff = (utcTime - ls.enhance(unix));
                this.posixTime = unix;

                if (
                    (diff == 0)
                    || (unix == MAX_LIMIT)
                ) {
                    this.fraction = nanosecond;
                } else if (diff == 1) { // positive Schaltsekunde
                    this.fraction = (nanosecond | POSITIVE_LEAP_MASK);
                } else {
                    throw new IllegalStateException(
                        "Cannot handle leap shift of " + elapsedTime + ".");
                }
            } else {
                throw new IllegalStateException(
                    "Leap seconds are not supported by configuration.");
            }
        }

        checkUnixTime(this.posixTime);
        checkFraction(nanosecond);

    }

    // Deserialisierung
    private Moment(
        int nano,
        long unixTime
    ) {
        super();

        // keine Prüfung des Nano-Anteils und Schaltsekunden-Bits
        checkUnixTime(unixTime);

        this.posixTime = unixTime;
        this.fraction = nano;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Equivalent to {@code Moment.of(elapsedTime, 0, scale)}. </p>
     *
     * @param   elapsedTime     elapsed seconds on given time scale
     * @param   scale           time scale reference
     * @return  new moment instance
     * @throws  IllegalArgumentException if elapsed time is out of range limits
     *          beyond year +/-999,999,999 or out of time scale range
     * @throws  IllegalStateException if time scale is not POSIX but
     *          leap second support is switched off by configuration
     * @see     LeapSeconds#isEnabled()
     */
    /*[deutsch]
     * <p>Entspricht {@code Moment.of(elapsedTime, 0, scale)}. </p>
     *
     * @param   elapsedTime     elapsed seconds on given time scale
     * @param   scale           time scale reference
     * @return  new moment instance
     * @throws  IllegalArgumentException if elapsed time is out of range limits
     *          beyond year +/-999,999,999 or out of time scale range
     * @throws  IllegalStateException if time scale is not POSIX but
     *          leap second support is switched off by configuration
     * @see     LeapSeconds#isEnabled()
     */
    public static Moment of(
        long elapsedTime,
        TimeScale scale
    ) {

        return Moment.of(elapsedTime, 0, scale);

    }

    /**
     * <p>Creates a new UTC-timestamp by given time coordinates on given
     * time scale. </p>
     *
     * <p>The given elapsed time {@code elapsedTime} will be internally
     * transformed into the UTC-epochtime, should another time scale than UTC
     * be given. The time scale TAI will only be supported earliest on UTC
     * start 1972-01-01, the time scale GPS earliest on 1980-01-06. </p>
     *
     * @param   elapsedTime     elapsed seconds on given time scale
     * @param   nanosecond      nanosecond fraction of last second
     * @param   scale           time scale reference
     * @return  new moment instance
     * @throws  IllegalArgumentException if the nanosecond is not in the range
     *          {@code 0 <= nanosecond <= 999,999,999} or if elapsed time is
     *          out of supported range limits beyond year +/-999,999,999 or
     *          out of time scale range
     * @throws  IllegalStateException if time scale is not POSIX but
     *          leap second support is switched off by configuration
     * @see     LeapSeconds#isEnabled()
     */
    /*[deutsch]
     * <p>Konstruiert einen neuen UTC-Zeitstempel mit Hilfe von
     * Zeitkoordinaten auf der angegebenen Zeitskala. </p>
     *
     * <p>Die angegebene verstrichene Zeit {@code elapsedTime} wird intern
     * in die UTC-Epochenzeit umgerechnet, sollte eine andere Zeitskala als
     * UTC angegeben sein. Die Zeitskala TAI wird erst ab der UTC-Epoche
     * 1972-01-01 unterst&uuml;tzt, die Zeitskala GPS erst ab 1980-01-06. </p>
     *
     * @param   elapsedTime     elapsed seconds on given time scale
     * @param   nanosecond      nanosecond fraction of last second
     * @param   scale           time scale reference
     * @return  new moment instance
     * @throws  IllegalArgumentException if the nanosecond is not in the range
     *          {@code 0 <= nanosecond <= 999,999,999} or if elapsed time is
     *          out of supported range limits beyond year +/-999,999,999 or
     *          out of time scale range
     * @throws  IllegalStateException if time scale is not POSIX but
     *          leap second support is switched off by configuration
     * @see     LeapSeconds#isEnabled()
     */
    public static Moment of(
        long elapsedTime,
        int nanosecond,
        TimeScale scale
    ) {

        if (
            (elapsedTime == 0)
            && (nanosecond == 0)
            && (scale == POSIX)
        ) {
            return Moment.UNIX_EPOCH;
        }

        return new Moment(elapsedTime, nanosecond, scale);

    }

    /**
     * <p>Obtains the current time using the system clock. </p>
     *
     * <p>Equivalent alternative for: {@code SystemClock.currentMoment()}. </p>
     *
     * @return  current moment using the system clock
     * @see     SystemClock#currentMoment()
     * @since   3.23/4.19
     */
    /*[deutsch]
     * <p>Ermittelt die aktuelle Systemzeit. </p>
     *
     * <p>Alternative f&uuml;r: {@code SystemClock.currentMoment()}. </p>
     *
     * @return  current moment using the system clock
     * @see     SystemClock#currentMoment()
     * @since   3.23/4.19
     */
    public static Moment nowInSystemTime() {

        return SystemClock.INSTANCE.currentTime();

    }

    /**
     * <p>Common conversion method. </p>
     *
     * @param   ut      UNIX-timestamp
     * @return  corresponding {@code Moment}
     */
    /*[deutsch]
     * <p>Allgemeine Konversionsmethode. </p>
     *
     * @param   ut      UNIX-timestamp
     * @return  corresponding {@code Moment}
     */
    public static Moment from(UnixTime ut) {

        if (ut instanceof Moment) {
            return Moment.class.cast(ut);
        } else if (
            (ut instanceof UniversalTime)
            && LeapSeconds.getInstance().isEnabled()
        ) {
            UniversalTime utc = UniversalTime.class.cast(ut);
            return Moment.of(
                utc.getElapsedTime(UTC),
                utc.getNanosecond(UTC),
                UTC);
        } else {
            return Moment.of(
                ut.getPosixTime(),
                ut.getNanosecond(),
                POSIX);
        }

    }

    @Override
    public long getPosixTime() {

        return this.posixTime;

    }

    @Override
    public long getElapsedTime(TimeScale scale) {

        if (scale == POSIX) {
            return this.posixTime;
        }

        long utc = this.getEpochTime();

        switch (scale) {
            case UTC:
                return utc;
            case TAI:
                if (utc < 0) {
                    throw new IllegalArgumentException(
                        "TAI not supported before 1972-01-01: " + this);
                } else {
                    return utc + 10;
                }
            case GPS:
                if (LeapSeconds.getInstance().strip(utc) < POSIX_GPS_DELTA) {
                    throw new IllegalArgumentException(
                        "GPS not supported before 1980-01-06: " + this);
                } else {
                    long gps =
                        LeapSeconds.getInstance().isEnabled()
                        ? utc
                        : (utc + 9);
                    return gps - UTC_GPS_DELTA;
                }
            default:
                throw new UnsupportedOperationException(
                    "Not yet implemented: " + scale);
        }

    }

    @Override
    public int getNanosecond() {

        return (this.fraction & (~POSITIVE_LEAP_MASK));

    }

    @Override
    public int getNanosecond(TimeScale scale) {

        switch (scale) {
            case POSIX:
            case UTC:
                return this.getNanosecond();
            case TAI:
                if (this.posixTime < POSIX_UTC_DELTA) {
                    throw new IllegalArgumentException(
                        "TAI not supported before 1972-01-01: " + this);
                } else {
                    return this.getNanosecond();
                }
            case GPS:
                long utc = this.getEpochTime();
                if (LeapSeconds.getInstance().strip(utc) < POSIX_GPS_DELTA) {
                    throw new IllegalArgumentException(
                        "GPS not supported before 1980-01-06: " + this);
                } else {
                    return this.getNanosecond();
                }
            default:
                throw new UnsupportedOperationException(
                    "Not yet implemented: " + scale);
        }

    }

    @Override
    public boolean isLeapSecond() {

        return (this.isPositiveLS() && LeapSeconds.getInstance().isEnabled());

    }

    /**
     * <p>Tries to determine the next coming leap second. </p>
     *
     * @return  operator which either gets next leap second or {@code null}
     *          if unknown or disabled
     * @since   2.1
     */
    /*[deutsch]
     * <p>Versucht, die n&auml;chste bevorstehende UTC-Schaltsekunde zu
     * ermitteln. </p>
     *
     * @return  operator which either gets next leap second or {@code null}
     *          if unknown or disabled
     * @since   2.1
     */
    public static ChronoOperator<Moment> nextLeapSecond() {

        return NEXT_LS;

    }

    /**
     * <p>Represents this timestamp as decimal value in given time scale. </p>
     *
     * @param   scale       time scale reference
     * @return  decimal value in given time scale as seconds inclusive fraction
     * @throws  IllegalArgumentException if this instance is out of range
     *          for given time scale
     */
    /*[deutsch]
     * <p>Stellt diese Zeit als Dezimalwert in der angegebenen Zeitskala
     * dar. </p>
     *
     * @param   scale       time scale reference
     * @return  decimal value in given time scale as seconds inclusive fraction
     * @throws  IllegalArgumentException if this instance is out of range
     *          for given time scale
     */
    public BigDecimal transform(TimeScale scale) {

        BigDecimal elapsedTime =
            new BigDecimal(this.getElapsedTime(scale)).setScale(9, RoundingMode.UNNECESSARY);
        BigDecimal nanosecond = new BigDecimal(this.getNanosecond(scale));
        return elapsedTime.add(nanosecond.movePointLeft(9));

    }

    @Override
    public boolean isAfter(UniversalTime temporal) {

        Moment other = Moment.from(temporal);
        return (this.compareTo(other) > 0);

    }

    @Override
    public boolean isBefore(UniversalTime temporal) {

        Moment other = Moment.from(temporal);
        return (this.compareTo(other) < 0);

    }

    @Override
    public boolean isSimultaneous(UniversalTime temporal) {

        Moment other = Moment.from(temporal);
        return (this.compareTo(other) == 0);

    }

    /**
     * <p>Converts this instance to a local timestamp in the system
     * timezone. </p>
     *
     * @return  local timestamp in system timezone (leap seconds will
     *          always be lost)
     * @since   1.2
     * @see     Timezone#ofSystem()
     * @see     #toZonalTimestamp(TZID)
     * @see     #toZonalTimestamp(String)
     */
    /*[deutsch]
     * <p>Wandelt diese Instanz in einen lokalen Zeitstempel um. </p>
     *
     * @return  local timestamp in system timezone (leap seconds will
     *          always be lost)
     * @since   1.2
     * @see     Timezone#ofSystem()
     * @see     #toZonalTimestamp(TZID)
     * @see     #toZonalTimestamp(String)
     */
    public PlainTimestamp toLocalTimestamp() {

        return this.in(Timezone.ofSystem());

    }

    /**
     * <p>Converts this instance to a local timestamp in given timezone. </p>
     *
     * @param   tzid    timezone id
     * @return  local timestamp in given timezone (leap seconds will
     *          always be lost)
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   1.2
     * @see     #toLocalTimestamp()
     */
    /*[deutsch]
     * <p>Wandelt diese Instanz in einen lokalen Zeitstempel um. </p>
     *
     * @param   tzid    timezone id
     * @return  local timestamp in given timezone (leap seconds will
     *          always be lost)
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   1.2
     * @see     #toLocalTimestamp()
     */
    public PlainTimestamp toZonalTimestamp(TZID tzid) {

        return this.in(Timezone.of(tzid));

    }

    /**
     * <p>Converts this instance to a local timestamp in given timezone. </p>
     *
     * @param   tzid    timezone id
     * @return  local timestamp in given timezone (leap seconds will
     *          always be lost)
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   1.2
     * @see     #toZonalTimestamp(TZID)
     * @see     #toLocalTimestamp()
     */
    /*[deutsch]
     * <p>Wandelt diese Instanz in einen lokalen Zeitstempel um. </p>
     *
     * @param   tzid    timezone id
     * @return  local timestamp in given timezone (leap seconds will
     *          always be lost)
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   1.2
     * @see     #toZonalTimestamp(TZID)
     * @see     #toLocalTimestamp()
     */
    public PlainTimestamp toZonalTimestamp(String tzid) {

        return this.in(Timezone.of(tzid));

    }

    /**
     * <p>Converts this instance to a general timestamp in given timezone. </p>
     *
     * @param   <C> generic type of date component
     * @param   chronology      chronology of date component
     * @param   tzid            timezone id
     * @param   startOfDay      start of day
     * @return  general timestamp in given timezone (leap seconds will always be lost)
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   3.8/4.5
     */
    /*[deutsch]
     * <p>Wandelt diese Instanz in einen allgemeinen Zeitstempel um. </p>
     *
     * @param   <C> generic type of date component
     * @param   chronology      chronology of date component
     * @param   tzid            timezone id
     * @param   startOfDay      start of day
     * @return  general timestamp in given timezone (leap seconds will always be lost)
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   3.8/4.5
     */
    public <C extends Calendrical<?, C>> GeneralTimestamp<C> toGeneralTimestamp(
        Chronology<C> chronology,
        TZID tzid,
        StartOfDay startOfDay
    ) {

        PlainTimestamp tsp = this.toZonalTimestamp(tzid);
        PlainTime time = tsp.getWallTime();
        int deviation = startOfDay.getDeviation(tsp.getCalendarDate(), tzid);
        tsp = tsp.minus(deviation, ClockUnit.SECONDS);
        C date = tsp.getCalendarDate().transform(chronology.getChronoType());
        return GeneralTimestamp.of(date, time);

    }

    /**
     * <p>Converts this instance to a general timestamp in given timezone. </p>
     *
     * @param   <C> generic type of date component
     * @param   family          calendar family for date component
     * @param   variant         variant of date component
     * @param   tzid            timezone id
     * @param   startOfDay      start of day
     * @return  general timestamp in given timezone (leap seconds will always be lost)
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @throws  ChronoException if given variant is not recognized
     * @since   3.8/4.5
     */
    /*[deutsch]
     * <p>Wandelt diese Instanz in einen allgemeinen Zeitstempel um. </p>
     *
     * @param   <C> generic type of date component
     * @param   family          calendar family for date component
     * @param   variant         variant of date component
     * @param   tzid            timezone id
     * @param   startOfDay      start of day
     * @return  general timestamp in given timezone (leap seconds will always be lost)
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @throws  ChronoException if given variant is not recognized
     * @since   3.8/4.5
     */
    public <C extends CalendarVariant<C>> GeneralTimestamp<C> toGeneralTimestamp(
        CalendarFamily<C> family,
        String variant,
        TZID tzid,
        StartOfDay startOfDay
    ) {

        PlainTimestamp tsp = this.toZonalTimestamp(tzid);
        PlainTime time = tsp.getWallTime();
        int deviation = startOfDay.getDeviation(tsp.getCalendarDate(), tzid);
        tsp = tsp.minus(deviation, ClockUnit.SECONDS);
        C date = tsp.getCalendarDate().transform(family.getChronoType(), variant);
        return GeneralTimestamp.of(date, time);

    }

    /**
     * <p>Creates a combination of this moment and system timezone. </p>
     *
     * <p>A direct conversion to a local timestamp can be achieved by
     * {@link #toLocalTimestamp()}. </p>
     *
     * @return  moment in system timezone
     * @since   2.0
     * @throws  IllegalArgumentException if this moment is a leapsecond and
     *          shall be combined with a non-full-minute-timezone-offset
     */
    /*[deutsch]
     * <p>Erzeugt eine Kombination dieses Moments und der Systemzeitzone. </p>
     *
     * <p>Eine Direktumwandlung zu einem lokalen Zeitstempel kann mit Hilfe
     * von {@link #toLocalTimestamp()} erreicht werden. </p>
     *
     * @return  moment in system timezone
     * @since   2.0
     * @throws  IllegalArgumentException if this moment is a leapsecond and
     *          shall be combined with a non-full-minute-timezone-offset
     */
    public ZonalDateTime inLocalView() {

        return ZonalDateTime.of(this, Timezone.ofSystem());

    }

    /**
     * <p>Creates a combination of this moment and given timezone. </p>
     *
     * <p>A direct conversion to a zonal timestamp can be achieved by
     * {@link #toZonalTimestamp(TZID)}. </p>
     *
     * @param   tzid    timezone id
     * @return  moment in given timezone
     * @since   2.0
     * @throws  IllegalArgumentException if this moment is a leapsecond and
     *          shall be combined with a non-full-minute-timezone-offset or
     *          if given timezone cannot be loaded
     */
    /*[deutsch]
     * <p>Erzeugt eine Kombination dieses Moments und der angegebenen
     * Zeitzone. </p>
     *
     * <p>Eine Direktumwandlung zu einem zonalen Zeitstempel kann mit Hilfe
     * von {@link #toZonalTimestamp(TZID)} erreicht werden. </p>
     *
     * @param   tzid    timezone id
     * @return  moment in given timezone
     * @since   2.0
     * @throws  IllegalArgumentException if this moment is a leapsecond and
     *          shall be combined with a non-full-minute-timezone-offset or
     *          if given timezone cannot be loaded
     */
    public ZonalDateTime inZonalView(TZID tzid) {

        return ZonalDateTime.of(this, Timezone.of(tzid));

    }

    /**
     * <p>Creates a combination of this moment and given timezone. </p>
     *
     * <p>A direct conversion to a zonal timestamp can be achieved by
     * {@link #toZonalTimestamp(String)}. </p>
     *
     * @param   tzid    timezone id
     * @return  moment in given timezone
     * @since   2.0
     * @throws  IllegalArgumentException if this moment is a leapsecond and
     *          shall be combined with a non-full-minute-timezone-offset or
     *          if given timezone cannot be loaded
     */
    /*[deutsch]
     * <p>Erzeugt eine Kombination dieses Moments und der angegebenen
     * Zeitzone. </p>
     *
     * <p>Eine Direktumwandlung zu einem zonalen Zeitstempel kann mit Hilfe
     * von {@link #toZonalTimestamp(String)} erreicht werden. </p>
     *
     * @param   tzid    timezone id
     * @return  moment in given timezone
     * @since   2.0
     * @throws  IllegalArgumentException if this moment is a leapsecond and
     *          shall be combined with a non-full-minute-timezone-offset or
     *          if given timezone cannot be loaded
     */
    public ZonalDateTime inZonalView(String tzid) {

        return ZonalDateTime.of(this, Timezone.of(tzid));

    }

    /**
     * <p>Adds an amount of given SI-unit to this timestamp
     * on the UTC time scale. </p>
     *
     * @param   amount  amount in units to be added
     * @param   unit    time unit defined in UTC time space
     * @return  changed copy of this instance
     * @throws  UnsupportedOperationException if either this moment or the result are before 1972
     * @throws  ArithmeticException in case of overflow
     */
    /*[deutsch]
     * <p>Addiert einen Betrag in der angegegebenen SI-Zeiteinheit auf die
     * UTC-Zeit dieses Zeitstempels. </p>
     *
     * @param   amount  amount in units to be added
     * @param   unit    time unit defined in UTC time space
     * @return  changed copy of this instance
     * @throws  UnsupportedOperationException if either this moment or the result are before 1972
     * @throws  ArithmeticException in case of overflow
     */
    public Moment plus(
        long amount,
        SI unit
    ) {

        Moment.check1972(this);

        if (amount == 0) {
            return this;
        }

        Moment result;

        try {
            switch (unit) {
                case SECONDS:
                    if (LeapSeconds.getInstance().isEnabled()) {
                        result = new Moment(
                            MathUtils.safeAdd(this.getEpochTime(), amount),
                            this.getNanosecond(),
                            UTC);
                    } else {
                        result = Moment.of(
                            MathUtils.safeAdd(this.posixTime, amount),
                            this.getNanosecond(),
                            POSIX
                        );
                    }
                    break;
                case NANOSECONDS:
                    long sum =
                        MathUtils.safeAdd(this.getNanosecond(), amount);
                    int nano = MathUtils.floorModulo(sum, MRD);
                    long second = MathUtils.floorDivide(sum, MRD);

                    if (LeapSeconds.getInstance().isEnabled()) {
                        result = new Moment(
                            MathUtils.safeAdd(this.getEpochTime(), second),
                            nano,
                            UTC
                        );
                    } else {
                        result = Moment.of(
                            MathUtils.safeAdd(this.posixTime, second),
                            nano,
                            POSIX
                        );
                    }
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        } catch (IllegalArgumentException iae) {
            ArithmeticException ex =
                new ArithmeticException(
                    "Result beyond boundaries of time axis.");
            ex.initCause(iae);
            throw ex;
        }

        if (amount < 0) {
            Moment.check1972(result);
        }

        return result;

    }

    /**
     * <p>Adds given real time to this timestamp on the UTC time scale. </p>
     *
     * @param   realTime    real time defined in UTC time space
     * @return  changed copy of this instance
     * @throws  UnsupportedOperationException if either this moment or the result are before 1972
     * @throws  ArithmeticException in case of overflow
     * @since   3.23/4.19
     */
    /*[deutsch]
     * <p>Addiert die angegebene Realzeit zur UTC-Zeit dieses Zeitstempels. </p>
     *
     * @param   realTime    real time defined in UTC time space
     * @return  changed copy of this instance
     * @throws  UnsupportedOperationException if either this moment or the result are before 1972
     * @throws  ArithmeticException in case of overflow
     * @since   3.23/4.19
     */
    public Moment plus(RealTime<SI> realTime) {

        return this.plus(realTime.getSeconds(), SI.SECONDS).plus(realTime.getFraction(), SI.NANOSECONDS);

    }

    /**
     * <p>Subtracts an amount of given SI-unit from this timestamp
     * on the UTC time scale. </p>
     *
     * @param   amount  amount in SI-units to be subtracted
     * @param   unit    time unit defined in UTC time space
     * @return  changed copy of this instance
     * @throws  UnsupportedOperationException if either this moment or the result are before 1972
     * @throws  ArithmeticException in case of overflow
     */
    /*[deutsch]
     * <p>Subtrahiert einen Betrag in der angegegebenen Zeiteinheit von der
     * UTC-Zeit dieses Zeitstempels. </p>
     *
     * @param   amount  amount in SI-units to be subtracted
     * @param   unit    time unit defined in UTC time space
     * @return  changed copy of this instance
     * @throws  UnsupportedOperationException if either this moment or the result are before 1972
     * @throws  ArithmeticException in case of overflow
     */
    public Moment minus(
        long amount,
        SI unit
    ) {

        return this.plus(MathUtils.safeNegate(amount), unit);

    }

    /**
     * <p>Subtracts given real time from this timestamp on the UTC time scale. </p>
     *
     * @param   realTime    real time defined in UTC time space
     * @return  changed copy of this instance
     * @throws  UnsupportedOperationException if either this moment or the result are before 1972
     * @throws  ArithmeticException in case of overflow
     * @since   3.23/4.19
     */
    /*[deutsch]
     * <p>Subtrahiert die angegegebene Realzeit von der UTC-Zeit dieses Zeitstempels. </p>
     *
     * @param   realTime    real time defined in UTC time space
     * @return  changed copy of this instance
     * @throws  UnsupportedOperationException if either this moment or the result are before 1972
     * @throws  ArithmeticException in case of overflow
     * @since   3.23/4.19
     */
    public Moment minus(RealTime<SI> realTime) {

        return this.minus(realTime.getSeconds(), SI.SECONDS).minus(realTime.getFraction(), SI.NANOSECONDS);

    }

    /**
     * <p>Calculates the time distance between this timestamp and given
     * end timestamp in given SI-unit on the UTC time scale. </p>
     *
     * @param   end     end time point
     * @param   unit    time unit defined in UTC time space
     * @return  count of SI-units between this instance and end time point
     * @throws  UnsupportedOperationException if any moment is before 1972
     */
    /*[deutsch]
     * <p>Bestimmt den zeitlichen Abstand zu einem Endzeitpunkt in der
     * angegebenen Zeiteinheit auf der UTC-Zeitskala. </p>
     *
     * @param   end     end time point
     * @param   unit    time unit defined in UTC time space
     * @return  count of SI-units between this instance and end time point
     * @throws  UnsupportedOperationException wenn ein Zeitpunkt vor 1972 ist
     */
    public long until(
        Moment end,
        SI unit
    ) {

        return unit.between(this, end);

    }

    /**
     * <p>Creates a new formatter which uses the given pattern in the
     * default locale for formatting and parsing UTC-timestamps. </p>
     *
     * <p>Note: The formatter can be adjusted to other locales and timezones however. </p>
     *
     * @param   <P> generic pattern type
     * @param   formatPattern   format definition as pattern
     * @param   patternType     pattern dialect
     * @return  format object for formatting {@code Moment}-objects using system locale and system timezone
     * @throws  IllegalArgumentException if resolving of pattern fails
     * @since   3.0
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Format-Objekt mit Hilfe des angegebenen Musters
     * in der Standard-Sprach- und L&auml;ndereinstellung und in der
     * System-Zeitzone. </p>
     *
     * <p>Das Format-Objekt kann an andere Sprachen oder Zeitzonen angepasst werden. </p>
     *
     * @param   <P> generic pattern type
     * @param   formatPattern   format definition as pattern
     * @param   patternType     pattern dialect
     * @return  format object for formatting {@code Moment}-objects using system locale and system timezone
     * @throws  IllegalArgumentException if resolving of pattern fails
     * @since   3.0
     */
    public static <P extends ChronoPattern<P>> TemporalFormatter<Moment> localFormatter(
        String formatPattern,
        P patternType
    ) {

        return FormatSupport.createFormatter(
            Moment.class, formatPattern, patternType, Locale.getDefault(), Timezone.ofSystem().getID());

    }

    /**
     * <p>Creates a new formatter which uses the given display mode in the
     * default locale for formatting and parsing UTC-timestamps. </p>
     *
     * <p>Note: The formatter can be adjusted to other locales and timezones however. </p>
     *
     * @param   mode        common formatting style for date part and time part
     * @return  format object for formatting {@code Moment}-objects using system locale and system timezone
     * @throws  IllegalStateException if format pattern cannot be retrieved
     * @since   3.0
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Format-Objekt mit Hilfe des angegebenen Stils
     * in der Standard-Sprach- und L&auml;ndereinstellung und in der
     * System-Zeitzone. </p>
     *
     * <p>Das Format-Objekt kann an andere Sprachen oder Zeitzonen angepasst werden. </p>
     *
     * @param   mode        common formatting style for date part and time part
     * @return  format object for formatting {@code Moment}-objects using system locale and system timezone
     * @throws  IllegalStateException if format pattern cannot be retrieved
     * @since   3.0
     */
    public static TemporalFormatter<Moment> localFormatter(DisplayMode mode) {

        return formatter(mode, Locale.getDefault(), Timezone.ofSystem().getID());

    }

    /**
     * <p>Creates a new formatter which uses the given pattern and locale
     * for formatting and parsing moments in given timezone. </p>
     *
     * <p>Note: The given timezone will be used in printing while it serves
     * as replacement value during parsing (if the text is missing a timezone
     * information). The formatter can be adjusted to other locales and
     * timezones however. </p>
     *
     * @param   <P> generic pattern type
     * @param   formatPattern   format definition as pattern
     * @param   patternType     pattern dialect
     * @param   locale          locale setting
     * @param   tzid            timezone id
     * @return  format object for formatting {@code Moment}-objects using given locale and timezone
     * @throws  IllegalArgumentException if resolving of pattern fails
     * @since   3.0
     * @see     #localFormatter(String,ChronoPattern)
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Format-Objekt mit Hilfe des angegebenen Musters in
     * der angegebenen Sprach- und L&auml;ndereinstellung sowie Zeitzone. </p>
     *
     * <p>Hinweis: Das Format-Objekt kann an andere Sprachen oder Zeitzonen
     * angepasst werden. Die angegebene Zeitzone dient beim Parsen als
     * Ersatzwert (wenn im zu interpretierenden Text keine Zeitzoneninformation
     * existiert). </p>
     *
     * @param   <P> generic pattern type
     * @param   formatPattern   format definition as pattern
     * @param   patternType     pattern dialect
     * @param   locale          locale setting
     * @param   tzid            timezone id
     * @return  format object for formatting {@code Moment}-objects using given locale and timezone
     * @throws  IllegalArgumentException if resolving of pattern fails
     * @since   3.0
     * @see     #localFormatter(String,ChronoPattern)
     */
    public static <P extends ChronoPattern<P>> TemporalFormatter<Moment> formatter(
        String formatPattern,
        P patternType,
        Locale locale,
        TZID tzid
    ) {

        return FormatSupport.createFormatter(Moment.class, formatPattern, patternType, locale, tzid);

    }

    /**
     * <p>Creates a new formatter which uses the given display mode and locale
     * for formatting and parsing moments in given timezone. </p>
     *
     * <p>Note: The given timezone will be used in printing while it serves
     * as replacement value during parsing (if the text is missing a timezone
     * information). The formatter can be adjusted to other locales and
     * timezones however. </p>
     *
     * @param   mode        common formatting style for date part and time part
     * @param   locale      locale setting
     * @param   tzid        timezone id
     * @return  format object for formatting {@code Moment}-objects using given locale
     * @throws  IllegalStateException if format pattern cannot be retrieved
     * @since   3.0
     * @see     #localFormatter(DisplayMode)
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Format-Objekt mit Hilfe des angegebenen Stils
     * und in der angegebenen Sprach- und L&auml;ndereinstellung und
     * Zeitzone. </p>
     *
     * <p>Hinweis: Das Format-Objekt kann an andere Sprachen oder Zeitzonen
     * angepasst werden. Die angegebene Zeitzone dient beim Parsen als
     * Ersatzwert (wenn im zu interpretierenden Text keine Zeitzoneninformation
     * existiert). </p>
     *
     * @param   mode        common formatting style for date part and time part
     * @param   locale      locale setting
     * @param   tzid        timezone id
     * @return  format object for formatting {@code Moment}-objects using given locale
     * @throws  IllegalStateException if format pattern cannot be retrieved
     * @since   3.0
     * @see     #localFormatter(DisplayMode)
     */
    public static TemporalFormatter<Moment> formatter(
        DisplayMode mode,
        Locale locale,
        TZID tzid
    ) {

        String formatPattern = CalendarText.patternForMoment(mode, mode, locale);
        return FormatSupport.createFormatter(Moment.class, formatPattern, locale, tzid);

    }

    /**
     * <p>Defines the RFC-1123-format which is for example used in mail
     * headers. </p>
     *
     * <p>Equivalent to the pattern &quot;[EEE, ]d MMM yyyy HH:mm[:ss] XX&quot;
     * where the timezone offset XX is modified such that in case of zero
     * offset the expression &quot;GMT&quot; is preferred. &quot;UT&quot; or &quot;Z&quot;
     * will be accepted as zero offset, too. The text elements will always be interpreted
     * in English and are case-insensitive. If no extra timezone is specified then this
     * formatter will use the timezone UTC as default for printing. </p>
     *
     * <p>Note: In contrast to the RFC-1123-standard this method does not
     * support military timezone abbreviations (A-Y) or north-american
     * timezone names (EST, EDT, CST, CDT, MST, MDT, PST, PDT). </p>
     *
     * @return  formatter object for RFC-1123 (technical internet-timestamp)
     */
    /*[deutsch]
     * <p>Definiert das RFC-1123-Format, das zum Beispiel in Mail-Headers
     * verwendet wird. </p>
     *
     * <p>Entspricht &quot;[EEE, ]d MMM yyyy HH:mm[:ss] XX&quot;, wobei
     * der Zeitzonen-Offset XX so modifiziert ist, da&szlig; im Fall eines
     * Null-Offsets bevorzugt der Ausdruck &quot;GMT&quot; benutzt wird. Als
     * Null-Offset werden auch &quot;UT&quot; oder &quot;Z&quot; akzeptiert.
     * Die Textelemente werden ohne Beachtung der Gro&szlig;- oder
     * Kleinschreibung in Englisch interpretiert. Wird keine extra Zeitzone
     * angegeben, wird dieser Formatierer die UTC-Zeitzone als Vorgabe zum
     * Formatieren verwenden. </p>
     *
     * <p>Zu beachten: Im Gegensatz zum RFC-1123-Standard unterst&uuml;tzt die
     * Methode keine milit&auml;rischen Zeitzonen (A-Y) oder nordamerikanischen
     * Zeitzonennamen (EST, EDT, CST, CDT, MST, MDT, PST, PDT). </p>
     *
     * @return  formatter object for RFC-1123 (technical internet-timestamp)
     */
    public static TemporalFormatter<Moment> formatterRFC1123() {

        return RFC1123.FORMATTER; // lazy initialization

    }

    @Override
    public int compareTo(Moment moment) {

        long u1 = this.getEpochTime();
        long u2 = moment.getEpochTime();

        if (u1 < u2) {
            return -1;
        } else if (u1 > u2) {
            return 1;
        } else {
            int result = this.getNanosecond() - moment.getNanosecond();
            return ((result > 0) ? 1 : ((result < 0) ? -1 : 0));
        }

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof Moment) {
            Moment that = (Moment) obj;

            if (this.posixTime != that.posixTime) {
                return false;
            }

            if (LeapSeconds.getInstance().isEnabled()) {
                return (this.fraction == that.fraction);
            } else {
                return (this.getNanosecond() == that.getNanosecond());
            }
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        long value = (this.posixTime ^ (this.posixTime >>> 32));
        return (19 * ((int) value) + 37 * this.getNanosecond());

    }

    /**
     * <p>Provides a canonical representation in the ISO-format
     * [yyyy-MM-ddTHH:mm:ss,fffffffffZ]. </p>
     *
     * <p>The fraction will only be printed if not zero. Example:
     * The expression {@code Moment.of(1341100824, 210, TimeScale.UTC)}
     * has the representation &quot;2012-06-30T23:59:60,000000210Z&quot;. </p>
     *
     * @return  ISO-8601-formatted string
     */
    /*[deutsch]
     * <p>Erzeugt eine kanonische Darstellung im ISO-Format
     * [yyyy-MM-ddTHH:mm:ss,fffffffffZ]. </p>
     *
     * <p>Der fraktionale Teil wird nur ausgegeben, wenn nicht 0. Beispiel:
     * Der Ausdruck {@code Moment.of(1341100824, 210, TimeScale.UTC)}
     * hat die Darstellung &quot;2012-06-30T23:59:60,000000210Z&quot;. </p>
     *
     * @return  ISO-8601-formatted string
     */
    @Override
    public String toString() {

        // Datum berechnen
        PlainDate date = this.getDateUTC();

        // Uhrzeit berechnen
        int timeOfDay = getTimeOfDay(this);
        int minutes = timeOfDay / 60;
        int hour = minutes / 60;
        int minute = minutes % 60;
        int second = timeOfDay % 60;

        // LS-Korrektur (negative LS => 59!!!, positive LS => 60)
        second += LeapSeconds.getInstance().getShift(this.getEpochTime());

        StringBuilder sb = new StringBuilder(50);

        // Datum formatieren
        sb.append(date);

        // Separator
        sb.append('T');

        // Uhrzeit formatieren
        format(hour, 2, sb);
        sb.append(':');
        format(minute, 2, sb);
        sb.append(':');
        format(second, 2, sb);

        // Fraktionaler Sekundenteil
        int nano = this.getNanosecond();

        if (nano > 0) {
            sb.append(',');
            format(nano, 9, sb);
        }

        // UTC-Symbol anhängen
        sb.append('Z');

        return sb.toString();

    }

    /**
     * <p>Creates a formatted view of this instance taking in account
     * given time scale. </p>
     *
     * <pre>
     *  Moment moment =
     *      PlainDate.of(2012, Month.JUNE, 30)
     *      .at(PlainTime.of(23, 59, 59, 999999999))
     *      .atUTC()
     *      .plus(1, SI.SECONDS); // move to leap second
     *
     *  System.out.println(moment.toString(TimeScale.POSIX));
     *  // Output: POSIX-2012-06-30T23:59:59,999999999Z
     *
     *  System.out.println(moment.toString(TimeScale.UTC));
     *  // Output: UTC-2012-06-30T23:59:60,999999999Z
     *
     *  System.out.println(moment.toString(TimeScale.TAI));
     *  // Output: TAI-2012-07-01T00:00:34,999999999Z
     *
     *  System.out.println(moment.toString(TimeScale.GPS));
     *  // Output: GPS-2012-07-01T00:00:15,999999999Z
     * </pre>
     *
     * @param   scale   time scale to be used for formatting
     * @return  formatted string with date-time fields in timezone UTC
     * @throws  IllegalArgumentException if this instance is out of range
     *          for given time scale
     * @see     #getElapsedTime(TimeScale)
     */
    /*[deutsch]
     * <p>Erzeugt eine formatierte Sicht dieser Instanz unter
     * Ber&uuml;cksichtigung der angegebenen Zeitskala. </p>
     *
     * <pre>
     *  Moment moment =
     *      PlainDate.of(2012, Month.JUNE, 30)
     *      .at(PlainTime.of(23, 59, 59, 999999999))
     *      .atUTC()
     *      .plus(1, SI.SECONDS); // move to leap second
     *
     *  System.out.println(moment.toString(TimeScale.POSIX));
     *  // Ausgabe: POSIX-2012-06-30T23:59:59,999999999Z
     *
     *  System.out.println(moment.toString(TimeScale.UTC));
     *  // Ausgabe: UTC-2012-06-30T23:59:60,999999999Z
     *
     *  System.out.println(moment.toString(TimeScale.TAI));
     *  // Ausgabe: TAI-2012-07-01T00:00:34,999999999Z
     *
     *  System.out.println(moment.toString(TimeScale.GPS));
     *  // Ausgabe: GPS-2012-07-01T00:00:15,999999999Z
     * </pre>
     *
     * @param   scale   time scale to be used for formatting
     * @return  formatted string with date-time fields in timezone UTC
     * @throws  IllegalArgumentException if this instance is out of range
     *          for given time scale
     * @see     #getElapsedTime(TimeScale)
     */
    public String toString(TimeScale scale) {

        StringBuilder sb = new StringBuilder(50);
        sb.append(scale.name());
        sb.append('-');

        switch (scale) {
            case POSIX:
                sb.append(PlainTimestamp.from(this, ZonalOffset.UTC));
                sb.append('Z');
                break;
            case UTC:
                sb.append(this.toString());
                break;
            case TAI:
                Moment tai =
                    new Moment(
                        this.getNanosecond(),
                        MathUtils.safeAdd(
                            this.getElapsedTime(TAI),
                            POSIX_UTC_DELTA)
                        );
                sb.append(PlainTimestamp.from(tai, ZonalOffset.UTC));
                sb.append('Z');
                break;
            case GPS:
                Moment gps =
                    new Moment(
                        this.getNanosecond(),
                        MathUtils.safeAdd(
                            this.getElapsedTime(GPS),
                            POSIX_GPS_DELTA)
                        );
                sb.append(PlainTimestamp.from(gps, ZonalOffset.UTC));
                sb.append('Z');
                break;
            default:
                throw new UnsupportedOperationException(scale.name());
        }

        return sb.toString();

    }

    /**
     * <p>Provides a static access to the associated time axis respective
     * chronology which contains the chronological rules. </p>
     *
     * @return  chronological system as time axis (never {@code null})
     */
    /*[deutsch]
     * <p>Liefert die zugeh&ouml;rige Zeitachse, die alle notwendigen
     * chronologischen Regeln enth&auml;lt. </p>
     *
     * @return  chronological system as time axis (never {@code null})
     */
    public static TimeAxis<TimeUnit, Moment> axis() {

        return ENGINE;

    }

    /**
     * <p>Provides a static access to the associated time axis using the foreign type S. </p>
     *
     * @param   <S> foreign temporal type
     * @param   converter       type converter
     * @return  chronological system for foreign type
     * @see     TemporalType#JAVA_UTIL_DATE
     * @since   3.24/4.20
     */
    /*[deutsch]
     * <p>Liefert die zugeh&ouml;rige Zeitachse angepasst f&uuml;r den Fremdtyp S. </p>
     *
     * @param   <S> foreign temporal type
     * @param   converter       type converter
     * @return  chronological system for foreign type
     * @see     TemporalType#JAVA_UTIL_DATE
     * @since   3.24/4.20
     */
    public static <S> Chronology<S> axis(Converter<S, Moment> converter) {

        return new BridgeChronology<S, Moment>(converter, ENGINE);

    }

    @Override
    protected TimeAxis<TimeUnit, Moment> getChronology() {

        return ENGINE;

    }

    @Override
    protected Moment getContext() {

        return this;

    }

    /**
     * <p>Pr&uuml;ft, ob eine negative Schaltsekunde vorliegt. </p>
     *
     * @param   posixTime   UNIX-time in seconds
     * @param   ts          local timestamp used for error message
     * @throws  ChronoException if a negative leap second is touched
     */
    static void checkNegativeLS(
        long posixTime,
        PlainTimestamp ts
    ) {

        LeapSeconds ls = LeapSeconds.getInstance();

        if (
            ls.supportsNegativeLS()
            && (ls.strip(ls.enhance(posixTime)) > posixTime)
        ) {
            throw new ChronoException(
                "Illegal local timestamp due to "
                + "negative leap second: " + ts);
        }

    }

    /**
     * <p>Pr&uuml;ft, ob der Zeitpunkt vor 1972 liegt. </p>
     *
     * @param   context     Pr&uuml;fzeitpunkt
     * @throws  UnsupportedOperationException wenn der Zeitpunkt vor 1972 ist
     */
    static void check1972(Moment context) {

        if (context.posixTime < POSIX_UTC_DELTA) {
            throw new UnsupportedOperationException(
                "Cannot calculate SI-duration before 1972-01-01.");
        }

    }

    private long getEpochTime() {

        if (LeapSeconds.getInstance().isEnabled()) {
            long time = LeapSeconds.getInstance().enhance(this.posixTime);
            return (this.isPositiveLS() ? time + 1 : time);
        } else {
            return this.posixTime - POSIX_UTC_DELTA;
        }

    }

    // Datum in der UTC-Zeitzone
    private PlainDate getDateUTC() {

        return PlainDate.of(
            MathUtils.floorDivide(this.posixTime, 86400),
            EpochDays.UNIX);

    }

    // Uhrzeit in der UTC-Zeitzone (ohne Schaltsekunde)
    private PlainTime getTimeUTC() {

        int timeOfDay = getTimeOfDay(this);
        int minutes = timeOfDay / 60;
        int hour = minutes / 60;
        int minute = minutes % 60;
        int second = timeOfDay % 60;
        int nano = this.getNanosecond();

        return PlainTime.of(hour, minute, second, nano);

    }

    private boolean isPositiveLS() {

        return ((this.fraction >>> 30) != 0);

    }

    private boolean isNegativeLS() {

        LeapSeconds ls = LeapSeconds.getInstance();

        if (ls.supportsNegativeLS()) {
            long ut = this.posixTime;
            return (ls.strip(ls.enhance(ut)) > ut);
        } else {
            return false;
        }

    }

    private static void checkUnixTime(long unixTime) {

        if (
            (unixTime > MAX_LIMIT)
            || (unixTime < MIN_LIMIT)
        ) {
            throw new IllegalArgumentException(
                "UNIX time (UT1) out of supported range: " + unixTime);
        }

    }

    private static void checkFraction(int nanoFraction) {

        if ((nanoFraction >= MRD) || (nanoFraction < 0)) {
            throw new IllegalArgumentException(
                "Nanosecond out of range: " + nanoFraction);
        }

    }

    private static void format(
        int value,
        int max,
        StringBuilder sb
    )  {

        int n = 1;

        for (int i = 0; i < max - 1; i++) {
            n *= 10;
        }

        while ((value < n) && (n >= 10)) {
            sb.append('0');
            n = n / 10;
        }

        sb.append(String.valueOf(value));

    }

    // Anzahl der POSIX-Sekunden des Tages
    private static int getTimeOfDay(Moment context) {

        return MathUtils.floorModulo(context.posixTime, 86400);

    }

    // Schaltsekundenkorrektur
    private static Moment moveEventuallyToLS(Moment adjusted) {

        PlainDate date = adjusted.getDateUTC();
        PlainTime time = adjusted.getTimeUTC();

        if (
            (LeapSeconds.getInstance().getShift(date) == 1)
            && (time.getHour() == 23)
            && (time.getMinute() == 59)
            && (time.getSecond() == 59)
        ) {
            return adjusted.plus(1, SI.SECONDS);
        } else {
            return adjusted;
        }

    }

    private PlainTimestamp in(Timezone tz) {

        return PlainTimestamp.from(this, tz.getOffset(this));

    }

    private static int getMaxSecondOfMinute(Moment context) {

        int minutes = getTimeOfDay(context) / 60;
        int second = 59;

        if (((minutes / 60) == 23) && ((minutes % 60) == 59)) {
            PlainDate date = context.getDateUTC();
            second += LeapSeconds.getInstance().getShift(date);
        }

        return second;

    }

    /**
     * @serialData  Uses <a href="../../serialized-form.html#net.time4j.SPX">
     *              a dedicated serialization form</a> as proxy. The format
     *              is bit-compressed. Overall until 13 data bytes are used.
     *              The first byte contains in the four most significant bits
     *              the type-ID {@code 4}. The lowest bit is {@code 1} if this
     *              instance is a positive leap second. The bit (2) will be
     *              set if there is a non-zero nanosecond part. After this
     *              header byte eight bytes follow containing the unix time
     *              (as long) and optional four bytes with the fraction part.
     *
     * Schematic algorithm:
     *
     * <pre>
     *  int header = 4;
     *  header &lt;&lt;= 4;
     *
     *  if (isLeapSecond()) {
     *      header |= 1;
     *  }
     *
     *  int fraction = getNanosecond();
     *
     *  if (fraction &gt; 0) {
     *      header |= 2;
     *  }
     *
     *  out.writeByte(header);
     *  out.writeLong(getPosixTime());
     *
     *  if (fraction &gt; 0) {
     *      out.writeInt(fraction);
     *  }
     * </pre>
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

    /**
     * Serialisierungsmethode.
     *
     * @param   out         output stream
     * @throws  IOException
     */
    void writeTimestamp(DataOutput out)
        throws IOException {

        int header = SPX.MOMENT_TYPE;
        header <<= 4;

        if (this.isPositiveLS()) {
            header |= 1;
        }

        int fp = this.getNanosecond();

        if (fp > 0) {
            header |= 2;
        }

        out.writeByte(header);
        out.writeLong(this.posixTime);

        if (fp > 0) {
            out.writeInt(fp);
        }

    }

    /**
     * Deserialisierungsmethode.
     *
     * @param   in          input stream
     * @param   positiveLS  positive leap second indicated?
     * @return  deserialized instance
     * @throws  IOException
     */
    static Moment readTimestamp(
        DataInput in,
        boolean positiveLS,
        boolean hasNanos
    ) throws IOException {

        long unixTime = in.readLong();
        int nano = (hasNanos ? in.readInt() : 0);

        if (unixTime == 0) {
            if (positiveLS) {
                throw new InvalidObjectException(
                    "UTC epoch is no leap second.");
            } else if (nano == 0) {
                return UNIX_EPOCH;
            }
        }

        if (
            (unixTime == MIN_LIMIT)
            && (nano == 0)
        ) {
            if (positiveLS) {
                throw new InvalidObjectException("Minimum is no leap second.");
            }
            return MIN;
        } else if (
            (unixTime == MAX_LIMIT)
            && (nano == MRD - 1)
        ) {
            if (positiveLS) {
                throw new InvalidObjectException("Maximum is no leap second.");
            }
            return MAX;
        } else {
            checkFraction(nano);
        }

        if (positiveLS) {
            LeapSeconds ls = LeapSeconds.getInstance();
            if (
                !ls.isEnabled() // keep LS-state when propagating to next vm
                || ls.isPositiveLS(ls.enhance(unixTime) + 1)
            ) {
                nano |= POSITIVE_LEAP_MASK;
            } else {
                long packed = GregorianMath.toPackedDate(unixTime);
                int month = GregorianMath.readMonth(packed);
                int day = GregorianMath.readDayOfMonth(packed);
                throw new InvalidObjectException(
                    "Not registered as leap second event: "
                    + GregorianMath.readYear(packed)
                    + "-"
                    + ((month < 10) ? "0" : "")
                    + month
                    + ((day < 10) ? "0" : "")
                    + day
                    + " [Please check leap second configurations "
                    + "either of emitter vm or this target vm]"
                );
            }
        }

        return new Moment(nano, unixTime);

    }

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Delegiert Anpassungen von {@code Moment}-Instanzen an einen
     * {@code ChronoOperator<PlainTimestamp>} mit Hilfe einer Zeitzone. </p>
     */
    static final class Operator
        implements ChronoOperator<Moment> {

        //~ Instanzvariablen ----------------------------------------------

        private final ChronoOperator<PlainTimestamp> delegate;
        private final ChronoElement<?> element;
        private final int type;
        private final Timezone tz;

        //~ Konstruktoren -------------------------------------------------

        /**
         * <p>Erzeugt einen Operator, der einen {@link Moment} mit
         * Hilfe der Systemzeitzone anpassen kann. </p>
         *
         * @param   delegate    delegating operator
         * @param   element     element reference
         * @param   type        operator type
         */
        Operator(
            ChronoOperator<PlainTimestamp> delegate,
            ChronoElement<?> element,
            int type
        ) {
            super();

            this.delegate = delegate;
            this.element = element;
            this.type = type;
            this.tz = null;

        }

        /**
         * <p>Erzeugt einen Operator, der einen {@link Moment} mit
         * Hilfe einer Zeitzonenreferenz anpassen kann. </p>
         *
         * @param   delegate    delegating operator
         * @param   element     element reference
         * @param   type        operator type
         * @param   tz          timezone
         */
        Operator(
            ChronoOperator<PlainTimestamp> delegate,
            ChronoElement<?> element,
            int type,
            Timezone tz
        ) {
            super();

            this.delegate = delegate;
            this.element = element;
            this.type = type;
            this.tz = tz;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Moment apply(Moment moment) {

            Timezone timezone = (
                (this.tz == null)
                ? Timezone.ofSystem()
                : this.tz);

            if (
                moment.isLeapSecond()
                && isNonIsoOffset(timezone, moment)
            ) {
                throw new IllegalArgumentException(
                    "Leap second can only be adjusted "
                    + " with timezone-offset in full minutes: "
                    + timezone.getOffset(moment));
            }

            // Spezialfall feingranulare Zeitarithmetik in der UTC-Ära
            if (moment.isAfter(START_LS_CHECK)) {
                if (
                    (this.element == SECOND_OF_MINUTE)
                    && (this.type == ElementOperator.OP_NEW_VALUE)
                    && (this.extractValue() == 60)
                ) {
                    if (moment.isLeapSecond()) {
                        return moment;
                    } else if (isNonIsoOffset(timezone, moment)) {
                        throw new IllegalArgumentException(
                            "Leap second can only be set "
                            + " with timezone-offset in full minutes: "
                            + timezone.getOffset(moment));
                    } else if (getMaxSecondOfMinute(moment) == 60) {
                        return moment.plus(
                            MathUtils.safeSubtract(60, this.extractOld(moment)),
                            SECONDS);
                    } else {
                        throw new IllegalArgumentException(
                            "Leap second invalid in context: " + moment);
                    }
                } else if (
                    LOW_TIME_ELEMENTS.containsKey(this.element)
                    && ((this.type == ElementOperator.OP_DECREMENT)
                        || (this.type == ElementOperator.OP_INCREMENT)
                        || (this.type == ElementOperator.OP_LENIENT))
                ) {
                    int step = LOW_TIME_ELEMENTS.get(this.element).intValue();
                    long amount = 1;

                    if (this.type == ElementOperator.OP_DECREMENT) {
                        amount = -1;
                    } else if (this.type == ElementOperator.OP_LENIENT) {
                        long oldValue = this.extractOld(moment);
                        long newValue = this.extractValue();
                        amount = MathUtils.safeSubtract(newValue, oldValue);
                    }

                    switch (step) {
                        case 1:
                            return moment.plus(amount, SECONDS);
                        case 1000:
                            return moment.plus(
                                MathUtils.safeMultiply(MIO, amount),
                                NANOSECONDS);
                        case MIO:
                            return moment.plus(
                                MathUtils.safeMultiply(1000, amount),
                                NANOSECONDS);
                        case MRD:
                            return moment.plus(amount, NANOSECONDS);
                        default:
                            throw new AssertionError();
                    }
                }
            }

            // lokale Transformation
            PlainTimestamp ts = moment.in(timezone).with(this.delegate);
            Moment result = ts.in(timezone);

            // hier kann niemals die Schaltsekunde erreicht werden
            if (this.type == ElementOperator.OP_FLOOR) {
                return result;
            }

            // Schaltsekundenprüfung, weil lokale Transformation keine LS kennt
            if (result.isNegativeLS()) {
                if (this.tz.getStrategy() == Timezone.STRICT_MODE) {
                    throw new ChronoException(
                        "Illegal local timestamp due to "
                        + "negative leap second: " + ts);
                } else {
                    return result;
                }
            }

            if (
                this.element.isDateElement()
                || HIGH_TIME_ELEMENTS.contains(this.element)
            ) {
                if (
                    moment.isLeapSecond()
                    || (this.type == ElementOperator.OP_CEILING)
                ) {
                    return moveEventuallyToLS(result);
                }
            } else if (this.element == SECOND_OF_MINUTE) {
                if (
                    (this.type == ElementOperator.OP_MAXIMIZE)
                    || (this.type == ElementOperator.OP_CEILING)
                ) {
                    return moveEventuallyToLS(result);
                }
            } else if (
                (this.element == MILLI_OF_SECOND)
                || (this.element == MICRO_OF_SECOND)
                || (this.element == NANO_OF_SECOND)
            ) {
                switch (this.type) {
                    case ElementOperator.OP_NEW_VALUE:
                    case ElementOperator.OP_MINIMIZE:
                    case ElementOperator.OP_MAXIMIZE:
                    case ElementOperator.OP_CEILING:
                        if (moment.isLeapSecond()) {
                            result = result.plus(1, SI.SECONDS);
                        }
                        break;
                    default:
                        // no-op
                }
            }

            return result;

        }

        private long extractOld(Moment context) {

            return Number.class.cast(
                context.getTimeUTC().get(this.element)
            ).longValue();

        }

        private long extractValue() {

            Object obj = ValueOperator.class.cast(this.delegate).getValue();

            if (obj == null) {
                throw new IllegalArgumentException("Missing new element value.");
            }

            return Number.class.cast(obj).longValue();

        }

        private static boolean isNonIsoOffset(
            Timezone timezone,
            Moment context
        ) {

            ZonalOffset offset = timezone.getOffset(context);

            return (
                (offset.getFractionalAmount() != 0)
                || ((offset.getAbsoluteSeconds() % 60) != 0)
            );

        }

    }

    private static class TimeUnitRule
        implements UnitRule<Moment> {

        //~ Instanzvariablen ----------------------------------------------

        private final TimeUnit unit;

        //~ Konstruktoren -------------------------------------------------

        TimeUnitRule(TimeUnit unit) {
            super();

            this.unit = unit;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Moment addTo(
            Moment context,
            long amount
        ) {

            if (this.unit.compareTo(TimeUnit.SECONDS) >= 0) {
                long secs =
                    MathUtils.safeMultiply(amount, this.unit.toSeconds(1));
                return Moment.of(
                    MathUtils.safeAdd(context.getPosixTime(), secs),
                    context.getNanosecond(),
                    POSIX
                );
            } else { // MILLIS, MICROS, NANOS
                long nanos =
                    MathUtils.safeMultiply(amount, this.unit.toNanos(1));
                long sum = MathUtils.safeAdd(context.getNanosecond(), nanos);
                int nano = MathUtils.floorModulo(sum, MRD);
                long second = MathUtils.floorDivide(sum, MRD);

                return Moment.of(
                    MathUtils.safeAdd(context.getPosixTime(), second),
                    nano,
                    POSIX
                );
            }

        }

        @Override
        public long between(
            Moment start,
            Moment end
        ) {

            long delta;

            if (this.unit.compareTo(TimeUnit.SECONDS) >= 0) {
                delta = (end.getPosixTime() - start.getPosixTime());
                if (delta < 0) {
                    if (end.getNanosecond() > start.getNanosecond()) {
                        delta++;
                    }
                } else if (delta > 0) {
                    if (end.getNanosecond() < start.getNanosecond()) {
                        delta--;
                    }
                }
            } else { // MILLIS, MICROS, NANOS
                delta =
                    MathUtils.safeAdd(
                        MathUtils.safeMultiply(
                            MathUtils.safeSubtract(
                                end.getPosixTime(),
                                start.getPosixTime()
                            ),
                            MRD
                        ),
                        end.getNanosecond() - start.getNanosecond()
                     );
            }

            switch (this.unit) {
                case DAYS:
                    delta = delta / 86400;
                    break;
                case HOURS:
                    delta = delta / 3600;
                    break;
                case MINUTES:
                    delta = delta / 60;
                    break;
                case SECONDS:
                    break;
                case MILLISECONDS:
                    delta = delta / MIO;
                    break;
                case MICROSECONDS:
                    delta = delta / 1000;
                    break;
                case NANOSECONDS:
                    break;
                default:
                    throw new UnsupportedOperationException(this.unit.name());
            }

            return delta;

        }

    }

    private static enum LongElement
        implements ChronoElement<Long>, ElementRule<Moment, Long> {

        //~ Statische Felder/Initialisierungen ----------------------------

        POSIX_TIME;

        //~ Methoden ------------------------------------------------------

        @Override
        public Class<Long> getType() {

            return Long.class;

        }

        @Override
        public char getSymbol() {

            return '\u0000';

        }

        @Override
        public int compare(
            ChronoDisplay o1,
            ChronoDisplay o2
        ) {

            return o1.get(this).compareTo(o2.get(this));

        }

        @Override
        public Long getDefaultMinimum() {

            return Long.valueOf(MIN_LIMIT);

        }

        @Override
        public Long getDefaultMaximum() {

            return Long.valueOf(MAX_LIMIT);

        }

        @Override
        public boolean isDateElement() {

            return false;

        }

        @Override
        public boolean isTimeElement() {

            return false;

        }

        @Override
        public boolean isLenient() {

            return false;

        }

        @Override
        public String getDisplayName(Locale language) {

            return this.name();

        }

        @Override
        public Long getValue(Moment context) {

            return Long.valueOf(context.getPosixTime());

        }

        @Override
        public Long getMinimum(Moment context) {

            return Long.valueOf(MIN_LIMIT);

        }

        @Override
        public Long getMaximum(Moment context) {

            return Long.valueOf(MAX_LIMIT);

        }

        @Override
        public boolean isValid(
            Moment context,
            Long value
        ) {

            if (value == null) {
                return false;
            }

            long val = value.longValue();
            return ((val >= MIN_LIMIT) && (val <= MAX_LIMIT));

        }

        @Override
        public Moment withValue(
            Moment context,
            Long value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing elapsed seconds.");
            }

            return Moment.of(
                value.longValue(),
                context.getNanosecond(),
                TimeScale.POSIX);

        }

        @Override
        public ChronoElement<?> getChildAtFloor(Moment context) {

            return IntElement.FRACTION;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(Moment context) {

            return IntElement.FRACTION;

        }

    }

    private static enum IntElement
        implements ChronoElement<Integer>, ElementRule<Moment, Integer> {

        //~ Statische Felder/Initialisierungen ----------------------------

        FRACTION;

        //~ Methoden ------------------------------------------------------

        @Override
        public Class<Integer> getType() {

            return Integer.class;

        }

        @Override
        public char getSymbol() {

            return '\u0000';

        }

        @Override
        public int compare(
            ChronoDisplay o1,
            ChronoDisplay o2
        ) {

            return o1.get(this).compareTo(o2.get(this));

        }

        @Override
        public Integer getDefaultMinimum() {

            return Integer.valueOf(0);

        }

        @Override
        public Integer getDefaultMaximum() {

            return Integer.valueOf(MRD - 1);

        }

        @Override
        public boolean isDateElement() {

            return false;

        }

        @Override
        public boolean isTimeElement() {

            return false;

        }

        @Override
        public boolean isLenient() {

            return false;

        }

        @Override
        public String getDisplayName(Locale language) {

            return this.name();

        }

        @Override
        public Integer getValue(Moment context) {

            return Integer.valueOf(context.getNanosecond());

        }

        @Override
        public Integer getMinimum(Moment context) {

            return this.getDefaultMinimum();

        }

        @Override
        public Integer getMaximum(Moment context) {

            return this.getDefaultMaximum();

        }

        @Override
        public boolean isValid(
            Moment context,
            Integer value
        ) {

            if (value == null) {
                return false;
            }

            int val = value.intValue();
            return ((val >= 0) && (val < MRD));

        }

        @Override
        public Moment withValue(
            Moment context,
            Integer value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing fraction value.");
            }

            if (LeapSeconds.getInstance().isEnabled()) {
                return Moment.of(
                    context.getElapsedTime(TimeScale.UTC),
                    value.intValue(),
                    TimeScale.UTC);
            } else {
                return Moment.of(
                    context.getPosixTime(),
                    value.intValue(),
                    TimeScale.POSIX);
            }

        }

        @Override
        public ChronoElement<?> getChildAtFloor(Moment context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(Moment context) {

            return null;

        }

    }

    private static class Merger
        implements ChronoMerger<Moment> {

        //~ Methoden ------------------------------------------------------

        @Override
        public String getFormatPattern(
            DisplayStyle style,
            Locale locale
        ) {

            DisplayMode mode = DisplayMode.ofStyle(style.getStyleValue());
            return CalendarText.patternForMoment(mode, mode, locale);

        }

        @Override
        public StartOfDay getDefaultStartOfDay() {

            return StartOfDay.MIDNIGHT;

        }

        @Override
        public Moment createFrom(
            TimeSource<?> clock,
            AttributeQuery attributes
        ) {

            return Moment.from(clock.currentTime());

        }

        @Override
        @Deprecated
        public Moment createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean preparsing
        ) {

            boolean lenient = attributes.get(Attributes.LENIENCY, Leniency.SMART).isLax();
            return this.createFrom(entity, attributes, lenient, preparsing);

        }

        @Override
        public Moment createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attrs,
            boolean lenient,
            boolean pp
        ) {

            if (entity instanceof UnixTime) {
                return Moment.from(UnixTime.class.cast(entity));
            } else if (entity.contains(LongElement.POSIX_TIME)) {
                long posixTime = entity.get(LongElement.POSIX_TIME).longValue();
                int fraction = 0;
                if (entity.contains(IntElement.FRACTION)) {
                    fraction = entity.get(IntElement.FRACTION).intValue();
                }
                return Moment.of(posixTime, fraction, POSIX);
            }

            Moment result = null;
            boolean leapsecond = false;

            if (entity.contains(LeapsecondElement.INSTANCE)) {
                leapsecond = true;
                entity.with(SECOND_OF_MINUTE, 60);
            }

            ChronoElement<PlainTimestamp> self = PlainTimestamp.axis().element();
            PlainTimestamp ts;

            if (entity.contains(self)) {
                ts = entity.get(self);
            } else {
                ts = PlainTimestamp.axis().createFrom(entity, attrs, lenient, pp);
            }

            if (ts == null) {
                return null;
            }

            TZID tzid = null;

            if (entity.hasTimezone()) {
                tzid = entity.getTimezone();
            } else if (attrs.contains(Attributes.TIMEZONE_ID)) {
                tzid = attrs.get(Attributes.TIMEZONE_ID); // Ersatzwert
            }

            if (tzid != null) {
                if (attrs.contains(Attributes.TRANSITION_STRATEGY)) {
                    TransitionStrategy strategy =
                        attrs.get(Attributes.TRANSITION_STRATEGY);
                    result = ts.in(Timezone.of(tzid).with(strategy));
                } else {
                    result = ts.inTimezone(tzid);
                }
            }

            if (leapsecond && (result != null)) {
                ZonalOffset offset;

                if (tzid instanceof ZonalOffset) {
                    offset = (ZonalOffset) tzid;
                } else {
                    offset = Timezone.of(tzid).getOffset(result);
                }

                if (
                    (offset.getFractionalAmount() != 0)
                    || ((offset.getAbsoluteSeconds() % 60) != 0)
                ) {
                    throw new IllegalArgumentException(
                        "Leap second is only allowed "
                        + " with timezone-offset in full minutes: "
                        + offset);
                }

                Moment test;

                if (result.getDateUTC().getYear() >= 1972) {
                    test = result.plus(1, SECONDS);
                } else {
                    test =
                        new Moment(
                            result.getNanosecond(),
                            result.getPosixTime() + 1);
                }

                if (lenient) {
                    result = test;
                } else if (LeapSeconds.getInstance().isEnabled()) {
                    if (test.isPositiveLS()) {
                        result = test;
                    } else {
                        throw new IllegalArgumentException(
                            "SECOND_OF_MINUTE parsed as invalid leapsecond before " + test);
                    }
                }
            }

            return result;

        }

        @Override
        public ChronoDisplay preformat(
            Moment context,
            AttributeQuery attributes
        ) {

            if (attributes.contains(Attributes.TIMEZONE_ID)) {
                TZID tzid = attributes.get(Attributes.TIMEZONE_ID);
                return context.inZonalView(tzid);
            }

            throw new IllegalArgumentException("Cannot print moment without timezone.");

        }

        @Override
        public Chronology<?> preparser() {

            return PlainTimestamp.axis();

        }

    }

    private static class GlobalTimeLine
        implements TimeLine<Moment> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Moment stepForward(Moment timepoint) {

            try {
                if (useSI(timepoint)) {
                    return timepoint.plus(1, SI.NANOSECONDS);
                } else {
                    return timepoint.plus(1, TimeUnit.NANOSECONDS);
                }
            } catch (ArithmeticException iae) {
                return null; // out of range
            }

        }

        @Override
        public Moment stepBackwards(Moment timepoint) {

            try {
                if (useSI(timepoint)) {
                    return timepoint.minus(1, SI.NANOSECONDS);
                } else {
                    return timepoint.minus(1, TimeUnit.NANOSECONDS);
                }
            } catch (ArithmeticException iae) {
                return null; // out of range
            }

        }

        @Override
        public int compare(Moment m1, Moment m2) {

            return m1.compareTo(m2);

        }

        private static boolean useSI(Moment timepoint) {

            return (
                (timepoint.posixTime > POSIX_UTC_DELTA)
                && LeapSeconds.getInstance().isEnabled()
            );

        }

    }

    private static class NextLS
        implements ChronoOperator<Moment> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Moment apply(Moment timepoint) {

            LeapSeconds ls = LeapSeconds.getInstance();

            if (ls.isEnabled()) {
                long utc = timepoint.getElapsedTime(TimeScale.UTC);
                LeapSecondEvent event = ls.getNextEvent(utc);

                if (event != null) {
                    PlainTimestamp tsp =
                        PlainDate.from(event.getDate()).atTime(23, 59, 59);
                    return tsp.atUTC().plus(event.getShift(), SECONDS);
                }
            }

            return null;

        }

    }

    private static class PrecisionRule
        implements ElementRule<Moment, TimeUnit> {

        //~ Methoden ------------------------------------------------------

        @Override
        public TimeUnit getValue(Moment context) {

            int f = context.getNanosecond();

            if (f != 0) {
                if ((f % MIO) == 0) {
                    return TimeUnit.MILLISECONDS;
                } else if ((f % 1000) == 0) {
                    return TimeUnit.MICROSECONDS;
                } else {
                    return TimeUnit.NANOSECONDS;
                }
            }

            long secs = context.posixTime;

            if (MathUtils.floorModulo(secs, 86400) == 0) {
                return TimeUnit.DAYS;
            } else if (MathUtils.floorModulo(secs, 3600) == 0) {
                return TimeUnit.HOURS;
            } else if (MathUtils.floorModulo(secs, 60) == 0) {
                return TimeUnit.MINUTES;
            } else {
                return TimeUnit.SECONDS;
            }

        }

        @Override
        public Moment withValue(
            Moment context,
            TimeUnit value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing precision.");
            }

            Moment result;

            switch (value) {
                case DAYS:
                    long secsD = MathUtils.floorDivide(context.posixTime, 86400) * 86400;
                    return Moment.of(secsD, TimeScale.POSIX);
                case HOURS:
                    long secsH = MathUtils.floorDivide(context.posixTime, 3600) * 3600;
                    return Moment.of(secsH, TimeScale.POSIX);
                case MINUTES:
                    long secsM = MathUtils.floorDivide(context.posixTime, 60) * 60;
                    return Moment.of(secsM, TimeScale.POSIX);
                case SECONDS:
                    result = Moment.of(context.posixTime, 0, TimeScale.POSIX);
                    break;
                case MILLISECONDS:
                    int f3 = (context.getNanosecond() / MIO) * MIO;
                    result = Moment.of(context.posixTime, f3, TimeScale.POSIX);
                    break;
                case MICROSECONDS:
                    int f6 = (context.getNanosecond() / 1000) * 1000;
                    result = Moment.of(context.posixTime, f6, TimeScale.POSIX);
                    break;
                case NANOSECONDS:
                    return context;
                default:
                    throw new UnsupportedOperationException(value.name());
            }

            if (context.isLeapSecond() && LeapSeconds.getInstance().isEnabled()) {
                return result.plus(1, SI.SECONDS);
            } else {
                return result;
            }

        }

        @Override
        public boolean isValid(
            Moment context,
            TimeUnit value
        ) {

            return (value != null);

        }

        @Override
        public TimeUnit getMinimum(Moment context) {

            return TimeUnit.DAYS;

        }

        @Override
        public TimeUnit getMaximum(Moment context) {

            return TimeUnit.NANOSECONDS;

        }

        @Override
        public ChronoElement<?> getChildAtFloor(Moment context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(Moment context) {

            return null;

        }

    }

    @SuppressWarnings("unchecked")
    private static class RFC1123 {

        // Typecast is okay because the type Moment is required per specification.
        static final TemporalFormatter<Moment> FORMATTER =
            (TemporalFormatter<Moment>) FormatSupport.getDefaultFormatEngine().createRFC1123();

    }

}
