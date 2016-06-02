/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (PlainTimestamp.java) is part of project Time4J.
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

import net.time4j.base.GregorianDate;
import net.time4j.base.MathUtils;
import net.time4j.base.TimeSource;
import net.time4j.base.UnixTime;
import net.time4j.base.WallTime;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.ChronoExtension;
import net.time4j.engine.ChronoMerger;
import net.time4j.engine.Chronology;
import net.time4j.engine.DisplayStyle;
import net.time4j.engine.ElementRule;
import net.time4j.engine.EpochDays;
import net.time4j.engine.Normalizer;
import net.time4j.engine.StartOfDay;
import net.time4j.engine.Temporal;
import net.time4j.engine.TimeAxis;
import net.time4j.engine.TimeMetric;
import net.time4j.engine.TimePoint;
import net.time4j.engine.TimeSpan;
import net.time4j.engine.UnitRule;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.CalendarType;
import net.time4j.format.ChronoPattern;
import net.time4j.format.DisplayMode;
import net.time4j.format.Leniency;
import net.time4j.format.LocalizedPatternSupport;
import net.time4j.format.TemporalFormatter;
import net.time4j.scale.TimeScale;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.TransitionStrategy;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static net.time4j.CalendarUnit.*;
import static net.time4j.ClockUnit.HOURS;
import static net.time4j.ClockUnit.*;
import static net.time4j.PlainDate.*;
import static net.time4j.PlainTime.*;


/**
 * <p>Represents a plain composition of calendar date and wall time as defined
 * in ISO-8601, but without any timezone. </p>
 *
 * <p>Following elements which are declared as constants are registered by
 * this class: </p>
 *
 * <ul>
 *  <li>{@link PlainDate#COMPONENT}</li>
 *  <li>{@link PlainDate#YEAR}</li>
 *  <li>{@link PlainDate#YEAR_OF_WEEKDATE}</li>
 *  <li>{@link PlainDate#QUARTER_OF_YEAR}</li>
 *  <li>{@link PlainDate#MONTH_OF_YEAR}</li>
 *  <li>{@link PlainDate#MONTH_AS_NUMBER}</li>
 *  <li>{@link PlainDate#DAY_OF_MONTH}</li>
 *  <li>{@link PlainDate#DAY_OF_QUARTER}</li>
 *  <li>{@link PlainDate#DAY_OF_WEEK}</li>
 *  <li>{@link PlainDate#DAY_OF_YEAR}</li>
 *  <li>{@link PlainDate#WEEKDAY_IN_MONTH}</li>
 *  <li>{@link PlainTime#COMPONENT}</li>
 *  <li>{@link PlainTime#AM_PM_OF_DAY}</li>
 *  <li>{@link PlainTime#CLOCK_HOUR_OF_AMPM}</li>
 *  <li>{@link PlainTime#CLOCK_HOUR_OF_DAY}</li>
 *  <li>{@link PlainTime#DIGITAL_HOUR_OF_AMPM}</li>
 *  <li>{@link PlainTime#DIGITAL_HOUR_OF_DAY}</li>
 *  <li>{@link PlainTime#ISO_HOUR}</li>
 *  <li>{@link PlainTime#MINUTE_OF_HOUR}</li>
 *  <li>{@link PlainTime#MINUTE_OF_DAY}</li>
 *  <li>{@link PlainTime#SECOND_OF_MINUTE}</li>
 *  <li>{@link PlainTime#SECOND_OF_DAY}</li>
 *  <li>{@link PlainTime#MILLI_OF_SECOND}</li>
 *  <li>{@link PlainTime#MICRO_OF_SECOND}</li>
 *  <li>{@link PlainTime#NANO_OF_SECOND}</li>
 *  <li>{@link PlainTime#MILLI_OF_DAY}</li>
 *  <li>{@link PlainTime#MICRO_OF_DAY}</li>
 *  <li>{@link PlainTime#NANO_OF_DAY}</li>
 *  <li>{@link PlainTime#DECIMAL_HOUR}</li>
 *  <li>{@link PlainTime#DECIMAL_MINUTE}</li>
 *  <li>{@link PlainTime#DECIMAL_SECOND}</li>
 *  <li>{@link PlainTime#PRECISION}</li>
 * </ul>
 *
 * <p>Furthermore, all elements of class {@link Weekmodel} are supported. As
 * timestamp units can be used: {@link CalendarUnit} and {@link ClockUnit}. </p>
 *
 * <p>Note: The special time value 24:00 is only supported in the factory
 * methods which normalize the resulting timestamp to midnight of the following
 * day. In element access and manipulations this value is not supported. </p>
 *
 * @author      Meno Hochschild
 */
/*[deutsch]
 * <p>Komposition aus Datum und Uhrzeit nach dem ISO-8601-Standard. </p>
 *
 * <p>Registriert sind folgende als Konstanten deklarierte Elemente: </p>
 *
 * <ul>
 *  <li>{@link PlainDate#COMPONENT}</li>
 *  <li>{@link PlainDate#YEAR}</li>
 *  <li>{@link PlainDate#YEAR_OF_WEEKDATE}</li>
 *  <li>{@link PlainDate#QUARTER_OF_YEAR}</li>
 *  <li>{@link PlainDate#MONTH_OF_YEAR}</li>
 *  <li>{@link PlainDate#MONTH_AS_NUMBER}</li>
 *  <li>{@link PlainDate#DAY_OF_MONTH}</li>
 *  <li>{@link PlainDate#DAY_OF_QUARTER}</li>
 *  <li>{@link PlainDate#DAY_OF_WEEK}</li>
 *  <li>{@link PlainDate#DAY_OF_YEAR}</li>
 *  <li>{@link PlainDate#WEEKDAY_IN_MONTH}</li>
 *  <li>{@link PlainTime#COMPONENT}</li>
 *  <li>{@link PlainTime#AM_PM_OF_DAY}</li>
 *  <li>{@link PlainTime#CLOCK_HOUR_OF_AMPM}</li>
 *  <li>{@link PlainTime#CLOCK_HOUR_OF_DAY}</li>
 *  <li>{@link PlainTime#DIGITAL_HOUR_OF_AMPM}</li>
 *  <li>{@link PlainTime#DIGITAL_HOUR_OF_DAY}</li>
 *  <li>{@link PlainTime#ISO_HOUR}</li>
 *  <li>{@link PlainTime#MINUTE_OF_HOUR}</li>
 *  <li>{@link PlainTime#MINUTE_OF_DAY}</li>
 *  <li>{@link PlainTime#SECOND_OF_MINUTE}</li>
 *  <li>{@link PlainTime#SECOND_OF_DAY}</li>
 *  <li>{@link PlainTime#MILLI_OF_SECOND}</li>
 *  <li>{@link PlainTime#MICRO_OF_SECOND}</li>
 *  <li>{@link PlainTime#NANO_OF_SECOND}</li>
 *  <li>{@link PlainTime#MILLI_OF_DAY}</li>
 *  <li>{@link PlainTime#MICRO_OF_DAY}</li>
 *  <li>{@link PlainTime#NANO_OF_DAY}</li>
 *  <li>{@link PlainTime#DECIMAL_HOUR}</li>
 *  <li>{@link PlainTime#DECIMAL_MINUTE}</li>
 *  <li>{@link PlainTime#DECIMAL_SECOND}</li>
 *  <li>{@link PlainTime#PRECISION}</li>
 * </ul>
 *
 * <p>Dar&uuml;berhinaus sind alle Elemente der Klasse {@link Weekmodel}
 * nutzbar. Als Zeiteinheiten kommen vor allem {@link CalendarUnit} und
 * {@link ClockUnit} in Betracht. </p>
 *
 * <p>Notiz: Unterst&uuml;tzung f&uuml;r den speziellen Zeitwert T24:00 gibt es
 * nur in den Fabrikmethoden, die dann diesen Wert zum n&auml;chsten Tag hin
 * normalisieren, nicht aber in den Elementen. </p>
 *
 * @author      Meno Hochschild
 */
@CalendarType("iso8601")
public final class PlainTimestamp
    extends TimePoint<IsoUnit, PlainTimestamp>
    implements GregorianDate, WallTime, Temporal<PlainTimestamp>, Normalizer<IsoUnit>, LocalizedPatternSupport {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int MRD = 1000000000;

    private static final PlainTimestamp MIN =
        new PlainTimestamp(PlainDate.MIN, PlainTime.MIN);
    private static final PlainTimestamp MAX =
        new PlainTimestamp(PlainDate.MAX, WALL_TIME.getDefaultMaximum());

    private static final Map<Object, ChronoElement<?>> CHILDREN;
    private static final TimeAxis<IsoUnit, PlainTimestamp> ENGINE;
    private static final TimeMetric<IsoUnit, Duration<IsoUnit>> STD_METRIC;

    static {
        Map<Object, ChronoElement<?>> children =
            new HashMap<Object, ChronoElement<?>>();
        children.put(CALENDAR_DATE, WALL_TIME);
        children.put(YEAR, MONTH_AS_NUMBER);
        children.put(YEAR_OF_WEEKDATE, Weekmodel.ISO.weekOfYear());
        children.put(QUARTER_OF_YEAR, DAY_OF_QUARTER);
        children.put(MONTH_OF_YEAR, DAY_OF_MONTH);
        children.put(MONTH_AS_NUMBER, DAY_OF_MONTH);
        children.put(DAY_OF_MONTH, WALL_TIME);
        children.put(DAY_OF_WEEK, WALL_TIME);
        children.put(DAY_OF_YEAR, WALL_TIME);
        children.put(DAY_OF_QUARTER, WALL_TIME);
        children.put(WEEKDAY_IN_MONTH, WALL_TIME);
        children.put(AM_PM_OF_DAY, DIGITAL_HOUR_OF_AMPM);
        children.put(CLOCK_HOUR_OF_AMPM, MINUTE_OF_HOUR);
        children.put(CLOCK_HOUR_OF_DAY, MINUTE_OF_HOUR);
        children.put(DIGITAL_HOUR_OF_AMPM, MINUTE_OF_HOUR);
        children.put(DIGITAL_HOUR_OF_DAY, MINUTE_OF_HOUR);
        children.put(ISO_HOUR, MINUTE_OF_HOUR);
        children.put(MINUTE_OF_HOUR, SECOND_OF_MINUTE);
        children.put(MINUTE_OF_DAY, SECOND_OF_MINUTE);
        children.put(SECOND_OF_MINUTE, NANO_OF_SECOND);
        children.put(SECOND_OF_DAY, NANO_OF_SECOND);
        CHILDREN = Collections.unmodifiableMap(children);

        TimeAxis.Builder<IsoUnit, PlainTimestamp> builder =
            TimeAxis.Builder
                .setUp(
                    IsoUnit.class,
                    PlainTimestamp.class,
                    new Merger(),
                    MIN,
                    MAX)
                .appendElement(
                    CALENDAR_DATE,
                    FieldRule.of(CALENDAR_DATE),
                    DAYS)
                .appendElement(
                    YEAR,
                    FieldRule.of(YEAR),
                    YEARS)
                .appendElement(
                    YEAR_OF_WEEKDATE,
                    FieldRule.of(YEAR_OF_WEEKDATE),
                    Weekcycle.YEARS)
                .appendElement(
                    QUARTER_OF_YEAR,
                    FieldRule.of(QUARTER_OF_YEAR),
                    QUARTERS)
                .appendElement(
                    MONTH_OF_YEAR,
                    FieldRule.of(MONTH_OF_YEAR),
                    MONTHS)
                .appendElement(
                    MONTH_AS_NUMBER,
                    FieldRule.of(MONTH_AS_NUMBER),
                    MONTHS)
                .appendElement(
                    DAY_OF_MONTH,
                    FieldRule.of(DAY_OF_MONTH),
                    DAYS)
                .appendElement(
                    DAY_OF_WEEK,
                    FieldRule.of(DAY_OF_WEEK),
                    DAYS)
                .appendElement(
                    DAY_OF_YEAR,
                    FieldRule.of(DAY_OF_YEAR),
                    DAYS)
                .appendElement(
                    DAY_OF_QUARTER,
                    FieldRule.of(DAY_OF_QUARTER),
                    DAYS)
                .appendElement(
                    WEEKDAY_IN_MONTH,
                    FieldRule.of(WEEKDAY_IN_MONTH),
                    WEEKS)
                .appendElement(
                    WALL_TIME,
                    FieldRule.of(WALL_TIME))
                .appendElement(
                    AM_PM_OF_DAY,
                    FieldRule.of(AM_PM_OF_DAY))
                .appendElement(
                    CLOCK_HOUR_OF_AMPM,
                    FieldRule.of(CLOCK_HOUR_OF_AMPM),
                    HOURS)
                .appendElement(
                    CLOCK_HOUR_OF_DAY,
                    FieldRule.of(CLOCK_HOUR_OF_DAY),
                    HOURS)
                .appendElement(
                    DIGITAL_HOUR_OF_AMPM,
                    FieldRule.of(DIGITAL_HOUR_OF_AMPM),
                    HOURS)
                .appendElement(
                    DIGITAL_HOUR_OF_DAY,
                    FieldRule.of(DIGITAL_HOUR_OF_DAY),
                    HOURS)
                .appendElement(
                    ISO_HOUR,
                    FieldRule.of(ISO_HOUR),
                    HOURS)
                .appendElement(
                    MINUTE_OF_HOUR,
                    FieldRule.of(MINUTE_OF_HOUR),
                    MINUTES)
                .appendElement(
                    MINUTE_OF_DAY,
                    FieldRule.of(MINUTE_OF_DAY),
                    MINUTES)
                .appendElement(
                    SECOND_OF_MINUTE,
                    FieldRule.of(SECOND_OF_MINUTE),
                    SECONDS)
                .appendElement(
                    SECOND_OF_DAY,
                    FieldRule.of(SECOND_OF_DAY),
                    SECONDS)
                .appendElement(
                    MILLI_OF_SECOND,
                    FieldRule.of(MILLI_OF_SECOND),
                    MILLIS)
                .appendElement(
                    MICRO_OF_SECOND,
                    FieldRule.of(MICRO_OF_SECOND),
                    MICROS)
                .appendElement(
                    NANO_OF_SECOND,
                    FieldRule.of(NANO_OF_SECOND),
                    NANOS)
                .appendElement(
                    MILLI_OF_DAY,
                    FieldRule.of(MILLI_OF_DAY),
                    MILLIS)
                .appendElement(
                    MICRO_OF_DAY,
                    FieldRule.of(MICRO_OF_DAY),
                    MICROS)
                .appendElement(
                    NANO_OF_DAY,
                    FieldRule.of(NANO_OF_DAY),
                    NANOS)
                .appendElement(
                    DECIMAL_HOUR,
                    new DecimalRule(DECIMAL_HOUR))
                .appendElement(
                    DECIMAL_MINUTE,
                    new DecimalRule(DECIMAL_MINUTE))
                .appendElement(
                    DECIMAL_SECOND,
                    new DecimalRule(DECIMAL_SECOND))
                .appendElement(
                    PRECISION,
                    FieldRule.of(PRECISION));
        registerCalendarUnits(builder);
        registerClockUnits(builder);
        registerExtensions(builder);
        ENGINE = builder.build();

        IsoUnit[] units =
            {YEARS, MONTHS, DAYS, HOURS, MINUTES, SECONDS, NANOS};
        STD_METRIC = Duration.in(units);
    }

    private static final long serialVersionUID = 7458380065762437714L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final PlainDate date;
    private transient final PlainTime time;

    //~ Konstruktoren -----------------------------------------------------

    private PlainTimestamp(
        PlainDate date,
        PlainTime time
    ) {
        super();

        if (time.getHour() == 24) { // T24 normalisieren
            this.date = date.plus(1, DAYS);
            this.time = PlainTime.MIN;
        } else if (date == null) {
            throw new NullPointerException("Missing date.");
        } else {
            this.date = date;
            this.time = time;
        }

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new local timestamp with calendar date and wall time. </p>
     *
     * <p>The special time value 24:00 will automatically normalized such
     * that the resulting timestamp is on starting midnight of following
     * day. </p>
     *
     * @param   date    calendar date component
     * @param   time    wall time component (24:00 will always be normalized)
     * @return  timestamp as composition of date and time
     * @see     #of(int, int, int, int, int)
     * @see     #of(int, int, int, int, int, int)
     */
    /*[deutsch]
     * <p>Erzeugt eine neue Instanz mit Datum und Uhrzeit. </p>
     *
     * <p>Der Spezialwert T24:00 wird automatisch so normalisiert, da&szlig;
     * der resultierende Zeitstempel auf Mitternacht des Folgetags zeigt. </p>
     *
     * @param   date    calendar date component
     * @param   time    wall time component (24:00 will always be normalized)
     * @return  timestamp as composition of date and time
     * @see     #of(int, int, int, int, int)
     * @see     #of(int, int, int, int, int, int)
     */
    public static PlainTimestamp of(
        PlainDate date,
        PlainTime time
    ) {

        return new PlainTimestamp(date, time);

    }

    /**
     * <p>Creates a new local timestamp in minute precision. </p>
     *
     * <p>The special time value 24:00 will automatically normalized such
     * that the resulting timestamp is on starting midnight of following
     * day. </p>
     *
     * @param   year        proleptic iso year [(-999,999,999)-999,999,999]
     * @param   month       gregorian month in range (1-12)
     * @param   dayOfMonth  day of month in range (1-31)
     * @param   hour        hour in the range {@code 0-23} or {@code 24}
     *                      if minute and second are equal to {@code 0}
     * @param   minute      minute in the range {@code 0-59}
     * @return  timestamp as composition of date and time
     */
    /*[deutsch]
     * <p>Erzeugt einen neuen minutengenauen Zeitstempel. </p>
     *
     * <p>Der Spezialwert T24:00 wird automatisch so normalisiert, da&szlig;
     * der resultierende Zeitstempel auf Mitternacht des Folgetags zeigt. </p>
     *
     * @param   year        proleptic iso year [(-999,999,999)-999,999,999]
     * @param   month       gregorian month in range (1-12)
     * @param   dayOfMonth  day of month in range (1-31)
     * @param   hour        hour in the range {@code 0-23} or {@code 24}
     *                      if minute and second are equal to {@code 0}
     * @param   minute      minute in the range {@code 0-59}
     * @return  timestamp as composition of date and time
     */
    public static PlainTimestamp of(
        int year,
        int month,
        int dayOfMonth,
        int hour,
        int minute
    ) {

        return PlainTimestamp.of(year, month, dayOfMonth, hour, minute, 0);

    }

    /**
     * <p>Creates a new local timestamp in second precision. </p>
     *
     * <p>The special time value 24:00 will automatically normalized such
     * that the resulting timestamp is on starting midnight of following
     * day. </p>
     *
     * @param   year        proleptic iso year [(-999,999,999)-999,999,999]
     * @param   month       gregorian month in range (1-12)
     * @param   dayOfMonth  day of month in range (1-31)
     * @param   hour        hour in the range {@code 0-23} or {@code 24}
     *                      if minute and second are equal to {@code 0}
     * @param   minute      minute in the range {@code 0-59}
     * @param   second      second in the range {@code 0-59}
     * @return  timestamp as composition of date and time
     */
    /*[deutsch]
     * <p>Erzeugt einen neuen sekundengenauen Zeitstempel. </p>
     *
     * <p>Der Spezialwert T24:00 wird automatisch so normalisiert, da&szlig;
     * der resultierende Zeitstempel auf Mitternacht des Folgetags zeigt. </p>
     *
     * @param   year        proleptic iso year [(-999,999,999)-999,999,999]
     * @param   month       gregorian month in range (1-12)
     * @param   dayOfMonth  day of month in range (1-31)
     * @param   hour        hour in the range {@code 0-23} or {@code 24}
     *                      if minute and second are equal to {@code 0}
     * @param   minute      minute in the range {@code 0-59}
     * @param   second      second in the range {@code 0-59}
     * @return  timestamp as composition of date and time
     */
    public static PlainTimestamp of(
        int year,
        int month,
        int dayOfMonth,
        int hour,
        int minute,
        int second
    ) {

        return PlainTimestamp.of(
            PlainDate.of(year, month, dayOfMonth),
            PlainTime.of(hour, minute, second)
        );

    }

    /**
     * <p>Provides the calendar date part. </p>
     *
     * @return  calendar date component
     */
    /*[deutsch]
     * <p>Liefert die Datumskomponente. </p>
     *
     * @return  calendar date component
     */
    public PlainDate getCalendarDate() {

        return this.date;

    }

    /**
     * <p>Provides the wall time part. </p>
     *
     * @return  wall time component
     */
    /*[deutsch]
     * <p>Liefert die Uhrzeitkomponente. </p>
     *
     * @return  wall time component
     */
    public PlainTime getWallTime() {

        return this.time;

    }

    @Override
    public int getYear() {

        return this.date.getYear();

    }

    @Override
    public int getMonth() {

        return this.date.getMonth();

    }

    @Override
    public int getDayOfMonth() {

        return this.date.getDayOfMonth();

    }

    @Override
    public int getHour() {

        return this.time.getHour();

    }

    @Override
    public int getMinute() {

        return this.time.getMinute();

    }

    @Override
    public int getSecond() {

        return this.time.getSecond();

    }

    @Override
    public int getNanosecond() {

        return this.time.getNanosecond();

    }

    /**
     * <p>Adjusts this timestamp by given operator. </p>
     *
     * @param   operator    element-related operator
     * @return  changed copy of this timestamp
     * @see     ChronoEntity#with(net.time4j.engine.ChronoOperator)
     */
    /*[deutsch]
     * <p>Passt diesen Zeitstempel mit Hilfe des angegebenen Operators an. </p>
     *
     * @param   operator    element-related operator
     * @return  changed copy of this timestamp
     * @see     ChronoEntity#with(net.time4j.engine.ChronoOperator)
     */
    public PlainTimestamp with(ElementOperator<?> operator) {

        return this.with(operator.onTimestamp());

    }

    /**
     * <p>Adjusts the calendar part of this timestamp. </p>
     *
     * @param   date    new calendar date component
     * @return  changed copy of this timestamp
     * @see     PlainDate#COMPONENT
     */
    /*[deutsch]
     * <p>Passt die Datumskomponente an. </p>
     *
     * @param   date    new calendar date component
     * @return  changed copy of this timestamp
     * @see     PlainDate#COMPONENT
     */
    public PlainTimestamp with(PlainDate date) {

        return this.with(CALENDAR_DATE, date);

    }

    /**
     * <p>Adjusts the wall time part of this timestamp. </p>
     *
     * @param   time    new wall time component
     * @return  changed copy of this timestamp
     * @see     PlainTime#COMPONENT
     */
    /*[deutsch]
     * <p>Passt die Uhrzeitkomponente an. </p>
     *
     * @param   time    new wall time component
     * @return  changed copy of this timestamp
     * @see     PlainTime#COMPONENT
     */
    public PlainTimestamp with(PlainTime time) {

        return this.with(WALL_TIME, time);

    }

    @Override
    public boolean isBefore(PlainTimestamp timestamp) {

        return (this.compareTo(timestamp) < 0);

    }

    @Override
    public boolean isAfter(PlainTimestamp timestamp) {

        return (this.compareTo(timestamp) > 0);

    }

    @Override
    public boolean isSimultaneous(PlainTimestamp timestamp) {

        return (this.compareTo(timestamp) == 0);

    }

    /**
     * <p>Defines the temporal order of date and time as natural order. </p>
     *
     * <p>The comparison is consistent with {@code equals()}. </p>
     *
     * @see     #isBefore(PlainTimestamp)
     * @see     #isAfter(PlainTimestamp)
     */
    /*[deutsch]
     * <p>Definiert eine nat&uuml;rliche Ordnung, die auf der zeitlichen
     * Position basiert. </p>
     *
     * <p>Der Vergleich ist konsistent mit {@code equals()}. </p>
     *
     * @see     #isBefore(PlainTimestamp)
     * @see     #isAfter(PlainTimestamp)
     */
    @Override
    public int compareTo(PlainTimestamp timestamp) {

        if (this.date.isAfter(timestamp.date)) {
            return 1;
        } else if (this.date.isBefore(timestamp.date)) {
            return -1;
        }

        return this.time.compareTo(timestamp.time);

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof PlainTimestamp) {
            PlainTimestamp that = (PlainTimestamp) obj;
            return (this.date.equals(that.date) && this.time.equals(that.time));
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return 13 * this.date.hashCode() + 37 * this.time.hashCode();

    }

    /**
     * <p>Creates a canonical representation of the form
     * &quot;yyyy-MM-dd'T'HH:mm:ss,fffffffff&quot;. </p>
     *
     * <p>Dependent on the precision (that is last non-zero part of time)
     * the time representation might be shorter. </p>
     *
     * @return  canonical ISO-8601-formatted string
     * @see     PlainTime#toString()
     */
    /*[deutsch]
     * <p>Erzeugt eine kanonische Darstellung im Format
     * &quot;yyyy-MM-dd'T'HH:mm:ss,fffffffff&quot;. </p>
     *
     * <p>Je nach Genauigkeit kann der Uhrzeitanteil auch k&uuml;rzer sein. </p>
     *
     * @return  canonical ISO-8601-formatted string
     * @see     PlainTime#toString()
     */
    @Override
    public String toString() {

        return this.date.toString() + this.time.toString();

    }

    /**
     * <p>Synonym for {@code getCalendarDate()}. </p>
     *
     * @return  calendar date component
     * @see     #getCalendarDate()
     * @since   3.6/4.4
     */
    /*[deutsch]
     * <p>Synonym f&uuml;r {@code getCalendarDate()}. </p>
     *
     * @return  calendar date component
     * @since   3.6/4.4
     */
    public PlainDate toDate() {

        return this.date;

    }

    /**
     * <p>Synonym for {@code getWallTime()}. </p>
     *
     * @return  wall time component
     * @see     #getWallTime()
     * @since   3.6/4.4
     */
    /*[deutsch]
     * <p>Synonym f&uuml;r {@code getWallTime()}. </p>
     *
     * @return  wall time component
     * @see     #getWallTime()
     * @since   3.6/4.4
     */
    public PlainTime toTime() {

        return this.time;

    }

    /**
     * <p>Creates a new formatter which uses the given pattern in the
     * default locale for formatting and parsing plain timestamps. </p>
     *
     * @param   <P> generic pattern type
     * @param   formatPattern   format definition as pattern
     * @param   patternType     pattern dialect
     * @return  format object for formatting {@code PlainTimestamp}-objects
     *          using system locale
     * @throws  IllegalArgumentException if resolving of pattern fails
     * @since   3.0
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Format-Objekt mit Hilfe des angegebenen Musters
     * in der Standard-Sprach- und L&auml;ndereinstellung. </p>
     *
     * @param   <P> generic pattern type
     * @param   formatPattern   format definition as pattern
     * @param   patternType     pattern dialect
     * @return  format object for formatting {@code PlainTimestamp}-objects
     *          using system locale
     * @throws  IllegalArgumentException if resolving of pattern fails
     * @since   3.0
     */
    public static <P extends ChronoPattern<P>> TemporalFormatter<PlainTimestamp> localFormatter(
        String formatPattern,
        P patternType
    ) {

        return FormatSupport.createFormatter(PlainTimestamp.class, formatPattern, patternType, Locale.getDefault());

    }

    /**
     * <p>Creates a new formatter which uses the given pattern and locale
     * for formatting and parsing plain timestamps. </p>
     *
     * @param   <P> generic pattern type
     * @param   formatPattern   format definition as pattern
     * @param   patternType     pattern dialect
     * @param   locale          locale setting
     * @return  format object for formatting {@code PlainTimestamp}-objects using given locale
     * @throws  IllegalArgumentException if resolving of pattern fails
     * @since   3.0
     * @see     #localFormatter(String,ChronoPattern)
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Format-Objekt mit Hilfe des angegebenen Musters
     * in der angegebenen Sprach- und L&auml;ndereinstellung. </p>
     *
     * @param   <P> generic pattern type
     * @param   formatPattern   format definition as pattern
     * @param   patternType     pattern dialect
     * @param   locale          locale setting
     * @return  format object for formatting {@code PlainTimestamp}-objects using given locale
     * @throws  IllegalArgumentException if resolving of pattern fails
     * @since   3.0
     * @see     #localFormatter(String,ChronoPattern)
     */
    public static <P extends ChronoPattern<P>> TemporalFormatter<PlainTimestamp> formatter(
        String formatPattern,
        P patternType,
        Locale locale
    ) {

        return FormatSupport.createFormatter(PlainTimestamp.class, formatPattern, patternType, locale);

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
    public static TimeAxis<IsoUnit, PlainTimestamp> axis() {

        return ENGINE;

    }

    /**
     * <p>Combines this local timestamp with the timezone offset UTC+00:00
     * to a global UTC-moment. </p>
     *
     * @return  global UTC-moment based on this local timestamp interpreted
     *          at offset UTC+00:00
     * @see     #at(ZonalOffset)
     */
    /*[deutsch]
     * <p>Kombiniert diesen lokalen Zeitstempel mit UTC+00:00 zu
     * einem globalen UTC-Moment. </p>
     *
     * @return  global UTC-moment based on this local timestamp interpreted
     *          at offset UTC+00:00
     * @see     #at(ZonalOffset)
     */
    public Moment atUTC() {

        return this.at(ZonalOffset.UTC);

    }

    /**
     * <p>Combines this local timestamp with the given timezone offset
     * to a global UTC-moment. </p>
     *
     * @param   offset  timezone offset
     * @return  global UTC-moment based on this local timestamp interpreted
     *          at given offset
     * @since   1.2
     * @see     #atUTC()
     * @see     #in(Timezone)
     */
    /*[deutsch]
     * <p>Kombiniert diesen lokalen Zeitstempel mit dem angegebenen
     * Zeitzonen-Offset zu einem globalen UTC-Moment. </p>
     *
     * @param   offset  timezone offset
     * @return  global UTC-moment based on this local timestamp interpreted
     *          at given offset
     * @since   1.2
     * @see     #atUTC()
     * @see     #in(Timezone)
     */
    public Moment at(ZonalOffset offset) {

        long localSeconds =
            MathUtils.safeMultiply(
                this.date.getDaysSinceUTC() + 2 * 365,
                86400);
        localSeconds += (this.time.getHour() * 3600);
        localSeconds += (this.time.getMinute() * 60);
        localSeconds += this.time.getSecond();

        int localNanos = this.time.getNanosecond();
        long posixTime = localSeconds - offset.getIntegralAmount();
        int posixNanos = localNanos - offset.getFractionalAmount();

        if (posixNanos < 0) {
            posixNanos += MRD;
            posixTime--;
        } else if (posixNanos >= MRD) {
            posixNanos -= MRD;
            posixTime++;
        }

        return Moment.of(posixTime, posixNanos, TimeScale.POSIX);

    }

    /**
     * <p>Combines this local timestamp with the system timezone to a global
     * UTC-moment. </p>
     *
     * @return  global UTC-moment based on this local timestamp interpreted
     *          in system timezone
     * @since   1.2
     * @see     Timezone#ofSystem()
     * @see     #inTimezone(TZID)
     */
    /*[deutsch]
     * <p>Kombiniert diesen lokalen Zeitstempel mit der System-Zeitzone
     * zu einem UTC-Moment. </p>
     *
     * @return  global UTC-moment based on this local timestamp interpreted
     *          in system timezone
     * @since   1.2
     * @see     Timezone#ofSystem()
     * @see     #inTimezone(TZID)
     */
    public Moment inStdTimezone() {

        return this.in(Timezone.ofSystem());

    }

    /**
     * <p>Combines this local timestamp with given timezone to a global
     * UTC-moment. </p>
     *
     * @param   tzid        timezone id
     * @return  global UTC-moment based on this local timestamp interpreted
     *          in given timezone
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   1.2
     * @see     Timezone#of(TZID)
     * @see     #inStdTimezone()
     */
    /*[deutsch]
     * <p>Kombiniert diesen lokalen Zeitstempel mit der angegebenen Zeitzone
     * zu einem UTC-Moment. </p>
     *
     * @param   tzid        timezone id
     * @return  global UTC-moment based on this local timestamp interpreted
     *          in given timezone
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   1.2
     * @see     Timezone#of(TZID)
     * @see     #inStdTimezone()
     */
    public Moment inTimezone(TZID tzid) {

        return this.in(Timezone.of(tzid));

    }

    /**
     * <p>Combines this local timestamp with given timezone to a global
     * UTC-moment. </p>
     *
     * @param   tz      timezone
     * @return  global UTC-moment based on this local timestamp interpreted
     *          in given timezone
     * @since   1.2
     * @see     Timezone#of(String)
     */
    /*[deutsch]
     * <p>Kombiniert diesen lokalen Zeitstempel mit der angegebenen Zeitzone
     * zu einem UTC-Moment. </p>
     *
     * @param   tz      timezone
     * @return  global UTC-moment based on this local timestamp interpreted
     *          in given timezone
     * @since   1.2
     * @see     Timezone#of(String)
     */
    public Moment in(Timezone tz) {

        if (tz.isFixed()) { // optimization
            return this.at(tz.getOffset(this.date, this.time));
        }

        TransitionStrategy strategy = tz.getStrategy();
        long posixTime = strategy.resolve(this.date, this.time, tz);
        Moment moment =
            Moment.of(posixTime, this.time.getNanosecond(), TimeScale.POSIX);

        if (strategy == Timezone.STRICT_MODE) {
            Moment.checkNegativeLS(posixTime, this);
        }

        return moment;

    }

    /**
     * <p>Equivalent to {@link #inZonalView(Timezone) inZonalView(Timezone.ofSystem()}. </p>
     *
     * @return  ZonalDateTime
     * @since   3.16/4.13
     */
    /*[deutsch]
     * <p>&Auml;quivalent zu {@link #inZonalView(Timezone) inZonalView(Timezone.ofSystem()}. </p>
     *
     * @return  ZonalDateTime
     * @since   3.16/4.13
     */
    public ZonalDateTime inLocalView() {

        return this.inZonalView(Timezone.ofSystem());

    }

    /**
     * <p>Converts this instance to a combination of UTC-moment, given timezone and its zonal timestamp. </p>
     *
     * <p><strong>Attention:</strong> Due to winter/summer-time-changes the resulting zonal timestamp
     * ({@link ZonalDateTime#toTimestamp()}) can deviate from this plain timestamp. </p>
     *
     * @param   tz      timezone
     * @return  ZonalDateTime
     * @since   3.16/4.13
     */
    /*[deutsch]
     * <p>Converts this instance to a combination of UTC-moment, given timezone and its zonal timestamp. </p>
     *
     * <p><strong>Achtung:</strong> Wegen Winter-/Sommerzeitumstellungen kann der resultierende zonale
     * Zeitstempel ({@link ZonalDateTime#toTimestamp()}) von diesem Zeitstempel abweichen. </p>
     *
     * @param   tz      timezone
     * @return  ZonalDateTime
     * @since   3.16/4.13
     */
    public ZonalDateTime inZonalView(Timezone tz) {

        Moment m = this.in(tz);
        return ZonalDateTime.of(m, tz);

    }

    /**
     * <p>Does this local timestamp exist in given timezone? </p>
     *
     * @param   tzid    timezone id (optional)
     * @return  {@code true} if this timestamp is valid in given timezone
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     */
    /*[deutsch]
     * <p>Existiert dieser Zeitstempel in der angegebenen Zeitzone? </p>
     *
     * @param   tzid    timezone id (optional)
     * @return  {@code true} if this timestamp is valid in given timezone
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     */
    public boolean isValid(TZID tzid) {

        if (tzid == null) {
            return false;
        }

        return !Timezone.of(tzid).isInvalid(this.date, this.time);

    }

    /**
     * <p>Normalized given timespan using years, months, days and
     * all clock units. </p>
     *
     * <p>This normalizer can also convert from days to months. Example: </p>
     *
     * <pre>
     *  Duration&lt;CalendarUnit&gt; dur = Duration.of(30, CalendarUnit.DAYS);
     *  Duration&lt;IsoUnit&gt; result =
     *      PlainTimestamp.of(2012, 2, 28, 0, 0).normalize(dur);
     *  System.out.println(result); // output: P1M1D (leap year!)
     * </pre>
     *
     * @param   timespan    to be normalized
     * @return  normalized duration
     * @since   2.0
     */
    /*[deutsch]
     * <p>Normalisiert die angegebene Zeitspanne, indem Jahre, Monate, Tage
     * und alle Uhrzeiteinheiten verwendet werden. </p>
     *
     * <p>Dieser Normalisierer kann auch von Tagen zu Monaten konvertieren.
     * Beispiel: </p>
     *
     * <pre>
     *  Duration&lt;CalendarUnit&gt; dur = Duration.of(30, CalendarUnit.DAYS);
     *  Duration&lt;IsoUnit&gt; result =
     *      PlainTimestamp.of(2012, 2, 28, 0, 0).normalize(dur);
     *  System.out.println(result); // Ausgabe: P1M1D (Schaltjahr!)
     * </pre>
     *
     * @param   timespan    to be normalized
     * @return  normalized duration
     * @since   2.0
     */
    @Override
    public Duration<IsoUnit> normalize(TimeSpan<? extends IsoUnit> timespan) {

        return this.until(this.plus(timespan), STD_METRIC);

    }

    @Override
    protected TimeAxis<IsoUnit, PlainTimestamp> getChronology() {

        return ENGINE;

    }

    @Override
    protected PlainTimestamp getContext() {

        return this;

    }

    /**
     * <p>Erzeugt eine neue Uhrzeit passend zur angegebenen absoluten Zeit. </p>
     *
     * @param   ut      unix time in seconds
     * @param   offset  shift of local timestamp relative to UTC
     * @return  new or cached local timestamp
     */
    static PlainTimestamp from(
        UnixTime ut,
        ZonalOffset offset
    ) {

        long localSeconds = ut.getPosixTime() + offset.getIntegralAmount();
        int localNanos = ut.getNanosecond() + offset.getFractionalAmount();

        if (localNanos < 0) {
            localNanos += MRD;
            localSeconds--;
        } else if (localNanos >= MRD) {
            localNanos -= MRD;
            localSeconds++;
        }

        PlainDate date =
            PlainDate.of(
                MathUtils.floorDivide(localSeconds, 86400),
                EpochDays.UNIX);

        int secondsOfDay = MathUtils.floorModulo(localSeconds, 86400);
        int second = secondsOfDay % 60;
        int minutesOfDay = secondsOfDay / 60;
        int minute = minutesOfDay % 60;
        int hour = minutesOfDay / 60;

        PlainTime time =
            PlainTime.of(
                hour,
                minute,
                second,
                localNanos
            );

        return PlainTimestamp.of(date, time);

    }

    private static void registerCalendarUnits(
        TimeAxis.Builder<IsoUnit, PlainTimestamp> builder
    ) {

        Set<CalendarUnit> monthly = EnumSet.range(MILLENNIA, MONTHS);
        Set<CalendarUnit> daily = EnumSet.range(WEEKS, DAYS);

        for (CalendarUnit unit : CalendarUnit.values()) {
            builder.appendUnit(
                unit,
                new CompositeUnitRule(unit),
                unit.getLength(),
                (unit.compareTo(WEEKS) < 0) ? monthly : daily
            );
        }

    }

    private static void registerClockUnits(
        TimeAxis.Builder<IsoUnit, PlainTimestamp> builder
    ) {

        for (ClockUnit unit : ClockUnit.values()) {
            builder.appendUnit(
                unit,
                new CompositeUnitRule(unit),
                unit.getLength(),
                EnumSet.allOf(ClockUnit.class)
            );
        }

    }

    private static void registerExtensions(
        TimeAxis.Builder<IsoUnit, PlainTimestamp> builder
    ) {

        for (ChronoExtension extension : PlainDate.axis().getExtensions()) {
            builder.appendExtension(extension);
        }
        for (ChronoExtension extension : PlainTime.axis().getExtensions()) {
            builder.appendExtension(extension);
        }

    }

    /**
     * @serialData  Uses <a href="../../serialized-form.html#net.time4j.SPX">
     *              a dedicated serialization form</a> as proxy. The layout
     *              is bit-compressed. The first byte contains within the
     *              four most significant bits the type id {@code 8}. Then
     *              the data bytes for date and time component follow.
     *
     * Schematic algorithm:
     *
     * <pre>
     *  int range;
     *
     *  if (year &gt;= 1850 &amp;&amp; year &lt;= 2100) {
     *      range = 1;
     *  } else if (Math.abs(year) &lt; 10000) {
     *      range = 2;
     *  } else {
     *      range = 3;
     *  }
     *
     *  int header = 8; // type-id
     *  header &lt;&lt;= 4;
     *  header |= month;
     *  out.writeByte(header);
     *
     *  int header2 = range;
     *  header2 &lt;&lt;= 5;
     *  header2 |= dayOfMonth;
     *  out.writeByte(header2);
     *
     *  if (range == 1) {
     *      out.writeByte(year - 1850 - 128);
     *  } else if (range == 2) {
     *      out.writeShort(year);
     *  } else {
     *      out.writeInt(year);
     *  }
     *
     *  if (time.nano == 0) {
     *      if (time.second == 0) {
     *          if (time.minute == 0) {
     *              out.writeByte(~time.hour);
     *          } else {
     *              out.writeByte(time.hour);
     *              out.writeByte(~time.minute);
     *          }
     *      } else {
     *          out.writeByte(time.hour);
     *          out.writeByte(time.minute);
     *          out.writeByte(~time.second);
     *      }
     *  } else {
     *      out.writeByte(time.hour);
     *      out.writeByte(time.minute);
     *      out.writeByte(time.second);
     *      out.writeInt(time.nano);
     *  }
     * </pre>
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

    private static class Merger
        implements ChronoMerger<PlainTimestamp> {

        //~ Methoden ------------------------------------------------------

        @Override
        public String getFormatPattern(
            DisplayStyle style,
            Locale locale
        ) {

            DisplayMode mode = DisplayMode.ofStyle(style.getStyleValue());
            return CalendarText.patternForTimestamp(mode, mode, locale);

        }

        @Override
        public StartOfDay getDefaultStartOfDay() {

            return StartOfDay.MIDNIGHT;

        }

        @Override
        public PlainTimestamp createFrom(
            TimeSource<?> clock,
            final AttributeQuery attributes
        ) {

            Timezone zone;

            if (attributes.contains(Attributes.TIMEZONE_ID)) {
                zone = Timezone.of(attributes.get(Attributes.TIMEZONE_ID));
            } else if (attributes.get(Attributes.LENIENCY, Leniency.SMART).isLax()) {
                zone = Timezone.ofSystem();
            } else {
                return null;
            }

            final UnixTime ut = clock.currentTime();
            return PlainTimestamp.from(ut, zone.getOffset(ut));

        }

        @Override
        @Deprecated
        public PlainTimestamp createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean preparsing
        ) {

            boolean lenient = attributes.get(Attributes.LENIENCY, Leniency.SMART).isLax();
            return this.createFrom(entity, attributes, lenient, preparsing);

        }

        @Override
        public PlainTimestamp createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean lenient,
            boolean preparsing
        ) {

            if (entity instanceof UnixTime) {
                TZID tzid;

                if (attributes.contains(Attributes.TIMEZONE_ID)) {
                    tzid = attributes.get(Attributes.TIMEZONE_ID);
                } else if (lenient) {
                    tzid = ZonalOffset.UTC;
                } else {
                    throw new IllegalArgumentException(
                        "Missing timezone attribute for type conversion.");
                }

                Moment ut = Moment.from(UnixTime.class.cast(entity));
                return ut.toZonalTimestamp(tzid);
            }

            boolean leapsecond = (preparsing && (entity.getInt(SECOND_OF_MINUTE) == 60));

            if (leapsecond) { // temporär, wird später kompensiert
                entity.with(SECOND_OF_MINUTE, 59);
            }

            PlainDate date;
            PlainTime time;

            if (entity.contains(CALENDAR_DATE)) {
                date = entity.get(CALENDAR_DATE);
            } else {
                date = PlainDate.axis().createFrom(entity, attributes, lenient, false);
            }

            if (date == null) {
                return null;
            } else if (entity.contains(WALL_TIME)) {
                time = entity.get(WALL_TIME);
            } else {
                time = PlainTime.axis().createFrom(entity, attributes, lenient, false);
                if ((time == null) && lenient) {
                    time = PlainTime.MIN;
                }
            }

            if (time == null) {
                return null;
            } else {
                if (entity.contains(LongElement.DAY_OVERFLOW)) {
                    date =
                        date.plus(
                            entity.get(LongElement.DAY_OVERFLOW).longValue(),
                            DAYS);
                }

                if (
                    leapsecond
                    && entity.isValid(LeapsecondElement.INSTANCE, Boolean.TRUE)
                ) {
                    entity.with(
                        LeapsecondElement.INSTANCE,
                        Boolean.TRUE);
                }

                return PlainTimestamp.of(date, time);
            }

        }

        @Override
        public ChronoDisplay preformat(
            PlainTimestamp context,
            AttributeQuery attributes
        ) {

            return context;

        }

        @Override
        public Chronology<?> preparser() {

            return null;

        }

    }

    private static class FieldRule<V>
        implements ElementRule<PlainTimestamp, V> {

        //~ Instanzvariablen ----------------------------------------------

        private final ChronoElement<V> element;

        //~ Konstruktoren -------------------------------------------------

        private FieldRule(ChronoElement<V> element) {
            super();

            this.element = element;

        }

        //~ Methoden ------------------------------------------------------

        static <V> FieldRule<V> of(ChronoElement<V> element) {

            return new FieldRule<V>(element);

        }

        @Override
        public V getValue(PlainTimestamp context) {

            if (this.element.isDateElement()) {
                return context.date.get(this.element);
            } else if (this.element.isTimeElement()) {
                return context.time.get(this.element);
            }

            throw new ChronoException(
                "Missing rule for: " + this.element.name());

        }

        @Override
        public V getMinimum(PlainTimestamp context) {

            if (this.element.isDateElement()) {
                return context.date.getMinimum(this.element);
            } else if (this.element.isTimeElement()) {
                return this.element.getDefaultMinimum();
            }

            throw new ChronoException(
                "Missing rule for: " + this.element.name());

        }

        @Override
        public V getMaximum(PlainTimestamp context) {

            if (this.element.isDateElement()) {
                return context.date.getMaximum(this.element);
            } else if (this.element.isTimeElement()) {
                return this.element.getDefaultMaximum();
            }

            throw new ChronoException(
                "Missing rule for: " + this.element.name());

        }

        @Override
        public boolean isValid(
            PlainTimestamp context,
            V value
        ) {

            if (this.element.isDateElement()) {
                return context.date.isValid(this.element, value);
            } else if (this.element.isTimeElement()) {
                if (Number.class.isAssignableFrom(this.element.getType())) {
                    if (value == null) {
                        return false;
                    }
                    long min = this.toNumber(this.element.getDefaultMinimum());
                    long max = this.toNumber(this.element.getDefaultMaximum());
                    long val = this.toNumber(value);
                    return ((min <= val) && (max >= val));
                } else if (
                    this.element.equals(WALL_TIME)
                    && PlainTime.MAX.equals(value)
                ) {
                    return false;
                } else {
                    return context.time.isValid(this.element, value);
                }
            }

            throw new ChronoException(
                "Missing rule for: " + this.element.name());

        }

        @Override
        public PlainTimestamp withValue(
            PlainTimestamp context,
            V value,
            boolean lenient
        ) {

            if (value.equals(this.getValue(context))) {
                return context;
            } else if (lenient) { // nur auf numerischen Elementen definiert
                IsoUnit unit = ENGINE.getBaseUnit(this.element);
                long oldValue = this.toNumber(this.getValue(context));
                long newValue = this.toNumber(value);
                long amount = MathUtils.safeSubtract(newValue, oldValue);
                return context.plus(amount, unit);
            } else if (this.element.isDateElement()) {
                PlainDate date = context.date.with(this.element, value);
                return PlainTimestamp.of(date, context.time);
            } else if (this.element.isTimeElement()) {
                if (Number.class.isAssignableFrom(this.element.getType())) {
                    long min = this.toNumber(this.element.getDefaultMinimum());
                    long max = this.toNumber(this.element.getDefaultMaximum());
                    long val = this.toNumber(value);
                    if ((min > val) || (max < val)) {
                        throw new IllegalArgumentException(
                            "Out of range: " + value);
                    }
                } else if (
                    this.element.equals(WALL_TIME)
                    && value.equals(PlainTime.MAX)
                ) {
                    throw new IllegalArgumentException(
                        "Out of range: " + value);
                }

                PlainTime time = context.time.with(this.element, value);
                return PlainTimestamp.of(context.date, time);
            }

            throw new ChronoException(
                "Missing rule for: " + this.element.name());

        }

        // optional
        @Override
        public ChronoElement<?> getChildAtFloor(PlainTimestamp context) {

            return CHILDREN.get(this.element);

        }

        // optional
        @Override
        public ChronoElement<?> getChildAtCeiling(PlainTimestamp context) {

            return CHILDREN.get(this.element);

        }

        private long toNumber(V value) {

            return Number.class.cast(value).longValue();

        }

    }

    private static class DecimalRule
        extends FieldRule<BigDecimal> {

        //~ Konstruktoren -------------------------------------------------

        DecimalRule(ChronoElement<BigDecimal> element) {
            super(element);

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public boolean isValid(
            PlainTimestamp context,
            BigDecimal value
        ) {

            if (value == null) {
                return false;
            }

            BigDecimal min = super.element.getDefaultMinimum();
            BigDecimal max = super.element.getDefaultMaximum();

            return (
                (min.compareTo(value) <= 0)
                && (value.compareTo(max) <= 0)
            );

        }

        @Override
        public PlainTimestamp withValue(
            PlainTimestamp context,
            BigDecimal value,
            boolean lenient
        ) {

            if (!this.isValid(context, value)) {
                throw new IllegalArgumentException("Out of range: " + value);
            }

            PlainTime time = context.time.with(super.element, value);
            return PlainTimestamp.of(context.date, time);

        }

    }

    private static class CompositeUnitRule
        implements UnitRule<PlainTimestamp> {

        //~ Instanzvariablen ----------------------------------------------

        private final CalendarUnit calendarUnit;
        private final ClockUnit clockUnit;

        //~ Konstruktoren -------------------------------------------------

        CompositeUnitRule(CalendarUnit unit) {
            super();

            this.calendarUnit = unit;
            this.clockUnit = null;

        }

        CompositeUnitRule(ClockUnit unit) {
            super();

            this.calendarUnit = null;
            this.clockUnit = unit;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public PlainTimestamp addTo(
            PlainTimestamp timepoint,
            long amount
        ) {

            PlainDate d;
            PlainTime t;

            if (this.calendarUnit != null) {
                d = timepoint.date.plus(amount, this.calendarUnit);
                t = timepoint.time;
            } else {
                DayCycles cycles = timepoint.time.roll(amount, this.clockUnit);
                d = timepoint.date.plus(cycles.getDayOverflow(), DAYS);
                t = cycles.getWallTime();
            }

            return PlainTimestamp.of(d, t);

        }

        @Override
        public long between(
            PlainTimestamp start,
            PlainTimestamp end
        ) {

            long delta;

            if (this.calendarUnit != null) {
                delta = this.calendarUnit.between(start.date, end.date);

                if (delta != 0) {
                	boolean needsTimeCorrection;

                	if (this.calendarUnit == DAYS) {
                		needsTimeCorrection = true;
                	} else {
                		PlainDate d = start.date.plus(delta, this.calendarUnit);
                		needsTimeCorrection = (d.compareByTime(end.date) == 0);
                	}

                	if (needsTimeCorrection) {
                	    PlainTime t1 = start.time;
                	    PlainTime t2 = end.time;

                	    if ((delta > 0) && t1.isAfter(t2)) {
                	        delta--;
                	    } else if ((delta < 0) && t1.isBefore(t2)) {
                	        delta++;
                	    }
                	}
                }
            } else if (start.date.isAfter(end.date)) {
                delta = -between(end, start);
            } else {
                long days = start.date.until(end.date, DAYS);

                if (days == 0) {
                    return this.clockUnit.between(start.time, end.time);
                } else if (this.clockUnit.compareTo(SECONDS) <= 0) {
                    // HOURS, MINUTES, SECONDS
                    delta =
                        MathUtils.safeAdd(
                            MathUtils.safeMultiply(days, 86400),
                            MathUtils.safeSubtract(
                                end.time.get(SECOND_OF_DAY).longValue(),
                                start.time.get(SECOND_OF_DAY).longValue()
                            )
                        );
                    if (start.time.getNanosecond() > end.time.getNanosecond()) {
                        delta--;
                    }
                } else {
                    // MILLIS, MICROS, NANOS
                    delta =
                        MathUtils.safeAdd(
                            MathUtils.safeMultiply(days, 86400L * MRD),
                            MathUtils.safeSubtract(
                                end.time.get(NANO_OF_DAY).longValue(),
                                start.time.get(NANO_OF_DAY).longValue()
                            )
                        );
                }

                switch (this.clockUnit) {
                    case HOURS:
                        delta = delta / 3600;
                        break;
                    case MINUTES:
                        delta = delta / 60;
                        break;
                    case SECONDS:
                        break;
                    case MILLIS:
                        delta = delta / 1000000;
                        break;
                    case MICROS:
                        delta = delta / 1000;
                        break;
                    case NANOS:
                        break;
                    default:
                        throw new UnsupportedOperationException(
                            this.clockUnit.name());
                }
            }

            return delta;

        }

    }

}
