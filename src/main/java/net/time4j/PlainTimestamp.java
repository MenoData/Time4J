/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (PlainTimestamp.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
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
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.ChronoMerger;
import net.time4j.engine.ElementRule;
import net.time4j.engine.EpochDays;
import net.time4j.engine.Temporal;
import net.time4j.engine.TimeAxis;
import net.time4j.engine.TimePoint;
import net.time4j.engine.UnitRule;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarType;
import net.time4j.format.ChronoFormatter;
import net.time4j.format.Leniency;
import net.time4j.scale.TimeScale;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.TransitionStrategy;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static net.time4j.PlainDate.*;
import static net.time4j.PlainTime.*;


/**
 * <p>Komposition aus Datum und Uhrzeit nach dem ISO-8601-Standard. </p>
 *
 * <p>Registriert sind folgende als Konstanten deklarierte Elemente: </p>
 *
 * <ul>
 *  <li>{@link PlainDate#CALENDAR_DATE}</li>
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
 *  <li>{@link PlainTime#WALL_TIME}</li>
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
 * @concurrency <immutable>
 */
@CalendarType("iso8601")
public final class PlainTimestamp
    extends TimePoint<IsoUnit, PlainTimestamp>
    implements GregorianDate, WallTime, Temporal<PlainTimestamp> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int MRD = 1000000000;

    private static final PlainTimestamp MIN =
        new PlainTimestamp(PlainDate.MIN, PlainTime.MIN);
    private static final PlainTimestamp MAX =
        new PlainTimestamp(PlainDate.MAX, WALL_TIME.getDefaultMaximum());

    private static final Map<Object, ChronoElement<?>> CHILDREN;

    /** Zeitachse eines ISO-Zeitstempels. */
    static final TimeAxis<IsoUnit, PlainTimestamp> ENGINE;

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
                    CalendarUnit.DAYS)
                .appendElement(
                    YEAR,
                    FieldRule.of(YEAR),
                    CalendarUnit.YEARS)
                .appendElement(
                    YEAR_OF_WEEKDATE,
                    FieldRule.of(YEAR_OF_WEEKDATE),
                    YOWElement.YOWUnit.WEEK_BASED_YEARS)
                .appendElement(
                    QUARTER_OF_YEAR,
                    FieldRule.of(QUARTER_OF_YEAR),
                    CalendarUnit.QUARTERS)
                .appendElement(
                    MONTH_OF_YEAR,
                    FieldRule.of(MONTH_OF_YEAR),
                    CalendarUnit.MONTHS)
                .appendElement(
                    MONTH_AS_NUMBER,
                    FieldRule.of(MONTH_AS_NUMBER),
                    CalendarUnit.MONTHS)
                .appendElement(
                    DAY_OF_MONTH,
                    FieldRule.of(DAY_OF_MONTH),
                    CalendarUnit.DAYS)
                .appendElement(
                    DAY_OF_WEEK,
                    FieldRule.of(DAY_OF_WEEK),
                    CalendarUnit.DAYS)
                .appendElement(
                    DAY_OF_YEAR,
                    FieldRule.of(DAY_OF_YEAR),
                    CalendarUnit.DAYS)
                .appendElement(
                    DAY_OF_QUARTER,
                    FieldRule.of(DAY_OF_QUARTER),
                    CalendarUnit.DAYS)
                .appendElement(
                    WEEKDAY_IN_MONTH,
                    FieldRule.of(WEEKDAY_IN_MONTH),
                    CalendarUnit.WEEKS)
                .appendElement(
                    WALL_TIME,
                    FieldRule.of(WALL_TIME))
                .appendElement(
                    AM_PM_OF_DAY,
                    FieldRule.of(AM_PM_OF_DAY))
                .appendElement(
                    CLOCK_HOUR_OF_AMPM,
                    FieldRule.of(CLOCK_HOUR_OF_AMPM),
                    ClockUnit.HOURS)
                .appendElement(
                    CLOCK_HOUR_OF_DAY,
                    FieldRule.of(CLOCK_HOUR_OF_DAY),
                    ClockUnit.HOURS)
                .appendElement(
                    DIGITAL_HOUR_OF_AMPM,
                    FieldRule.of(DIGITAL_HOUR_OF_AMPM),
                    ClockUnit.HOURS)
                .appendElement(
                    DIGITAL_HOUR_OF_DAY,
                    FieldRule.of(DIGITAL_HOUR_OF_DAY),
                    ClockUnit.HOURS)
                .appendElement(
                    ISO_HOUR,
                    FieldRule.of(ISO_HOUR),
                    ClockUnit.HOURS)
                .appendElement(
                    MINUTE_OF_HOUR,
                    FieldRule.of(MINUTE_OF_HOUR),
                    ClockUnit.MINUTES)
                .appendElement(
                    MINUTE_OF_DAY,
                    FieldRule.of(MINUTE_OF_DAY),
                    ClockUnit.MINUTES)
                .appendElement(
                    SECOND_OF_MINUTE,
                    FieldRule.of(SECOND_OF_MINUTE),
                    ClockUnit.SECONDS)
                .appendElement(
                    SECOND_OF_DAY,
                    FieldRule.of(SECOND_OF_DAY),
                    ClockUnit.SECONDS)
                .appendElement(
                    MILLI_OF_SECOND,
                    FieldRule.of(MILLI_OF_SECOND),
                    ClockUnit.MILLIS)
                .appendElement(
                    MICRO_OF_SECOND,
                    FieldRule.of(MICRO_OF_SECOND),
                    ClockUnit.MICROS)
                .appendElement(
                    NANO_OF_SECOND,
                    FieldRule.of(NANO_OF_SECOND),
                    ClockUnit.NANOS)
                .appendElement(
                    MILLI_OF_DAY,
                    FieldRule.of(MILLI_OF_DAY),
                    ClockUnit.MILLIS)
                .appendElement(
                    MICRO_OF_DAY,
                    FieldRule.of(MICRO_OF_DAY),
                    ClockUnit.MICROS)
                .appendElement(
                    NANO_OF_DAY,
                    FieldRule.of(NANO_OF_DAY),
                    ClockUnit.NANOS)
                .appendExtension(new WeekExtension());
        registerCalendarUnits(builder);
        registerClockUnits(builder);
        ENGINE = builder.build();
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
            this.date = date.plus(1, CalendarUnit.DAYS);
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
     * <p>Liefert die Datumskomponente. </p>
     *
     * @return  calendar date component
     */
    public PlainDate getCalendarDate() {

        return this.date;

    }

    /**
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

    /**
     * <p>Passt diesen Zeitstempel mit Hilfe des angegebenen Operators an. </p>
     *
     * @param   operator    element-related operator
     * @return  changed copy of this timestamp
     * @see     ChronoEntity#with(net.time4j.engine.ChronoOperator)
     */
    public PlainTimestamp with(ElementOperator<?> operator) {

        return this.with(operator.onTimestamp());

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

        int delta = this.date.compareTo(timestamp.getCalendarDate());

        if (delta == 0) {
            delta = this.time.compareTo(timestamp.getWallTime());
        }

        return ((delta < 0) ? -1 : ((delta == 0) ? 0 : 1));

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
     * <p>Erstellt ein neues Formatobjekt, das eine Komposition der angegebenen
     * Datums- und Uhrzeitformate darstellt. </p>
     *
     * <p>Die Sprach- und L&auml;ndereinstellung wird vom Datumsformat
     * &uuml;bernommen. </p>
     *
     * @param   dateFormatter   calendar date formatter
     * @param   timeFormatter   walltime formatter
     * @return  composite formatter object for a plain timestamp
     */
    public static ChronoFormatter<PlainTimestamp> formatter(
        ChronoFormatter<PlainDate> dateFormatter,
        ChronoFormatter<PlainTime> timeFormatter
    ) {

        return ChronoFormatter
            .setUp(PlainTimestamp.class, dateFormatter.getLocale())
            .addCustomized(CALENDAR_DATE, dateFormatter)
            .addCustomized(WALL_TIME, timeFormatter)
            .build();

    }

    @Override
    public TimeAxis<IsoUnit, PlainTimestamp> getChronology() {

        return ENGINE;

    }

    /**
     * <p>Kombiniert diesen lokalen Zeitstempel mit dem angegebenen Offset
     * zu einem UTC-Zeitstempel. </p>
     *
     * @param   offset      fixed timezone offset
     * @return  global timestamp  based on this local timestamp interpreted
     *          at given timezone offset
     */
    public Moment atOffset(ZonalOffset offset) {

        return this.inTimezone(Timezone.of(offset));

    }

    /**
     * <p>Kombiniert diesen lokalen Zeitstempel mit der System-Zeitzone
     * zu einem UTC-Zeitstempel. </p>
     *
     * @return  global timestamp based on this local timestamp interpreted
     *          in system timezone
     * @see     Timezone#ofSystem()
     * @see     #inTimezone(TZID)
     * @see     #atOffset(ZonalOffset)
     */
    public Moment inStdTimezone() {

        return this.inTimezone(Timezone.ofSystem());

    }

    /**
     * <p>Kombiniert diesen lokalen Zeitstempel mit der angegebenen Zeitzone
     * zu einem UTC-Zeitstempel. </p>
     *
     * @param   tzid        timezone id
     * @return  global timestamp based on this local timestamp interpreted
     *          in given timezone
     * @see     Timezone#of(TZID)
     * @see     #inStdTimezone()
     * @see     #atOffset(ZonalOffset)
     */
    public Moment inTimezone(TZID tzid) {

        return this.inTimezone(Timezone.of(tzid));

    }

    /**
     * <p>Kombiniert diesen lokalen Zeitstempel mit der angegebenen Zeitzone
     * zu einem UTC-Zeitstempel. </p>
     *
     * @param   tzid        timezone id
     * @param   strategy    conflict resolving strategy
     * @return  global timestamp based on this local timestamp interpreted
     *          in given timezone selecting given transition strategy
     * @see     Timezone#of(TZID)
     * @see     #inStdTimezone()
     * @see     #atOffset(ZonalOffset)
     */
    public Moment inTimezone(
        TZID tzid,
        TransitionStrategy strategy
    ) {

        return Moment.from(
            strategy.resolve(this.date, this.time, Timezone.of(tzid)));

    }

    /**
     * <p>Existiert dieser Zeitstempel in der angegebenen Zeitzone? </p>
     *
     * @param   tzid    timezone id
     * @return  {@code true} if this timestamp is valid in given timezone
     */
    public boolean isValid(TZID tzid) {

        return !Timezone.of(tzid).isInvalid(this.date, this.time);

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

    /**
     * <p>Wandelt diesen lokalen Zeitstempel mit Hilfe der angegebenen Zeitzone
     * in eine UTC-Zeit um. </p>
     *
     * @param   tz          timezone data
     * @return  universal time
     * @throws  ChronoException if given strategy is strict and this timestamp
     *                          falls in a gap on local timeline (DST-change)
     */
    Moment inTimezone(Timezone tz) {

        ZonalOffset offset = tz.getOffset(this, this);
        long localSeconds = (this.date.getDaysSinceUTC() + 2 * 365) * 86400;
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

    private static void registerCalendarUnits(
        TimeAxis.Builder<IsoUnit, PlainTimestamp> builder
    ) {

        Set<CalendarUnit> monthly =
            EnumSet.range(CalendarUnit.MILLENNIA, CalendarUnit.MONTHS);
        Set<CalendarUnit> daily =
            EnumSet.range(CalendarUnit.WEEKS, CalendarUnit.DAYS);

        for (CalendarUnit unit : CalendarUnit.values()) {
            builder.appendUnit(
                unit,
                new CompositeUnitRule(unit),
                unit.getLength(),
                (unit.compareTo(CalendarUnit.WEEKS) < 0) ? monthly : daily
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

    /**
     * @serialData  Uses <a href="../../serialized-form.html#net.time4j.SPX">
     *              a dedicated serialization form</a> as proxy. The layout
     *              is bit-compressed. The first byte contains within the
     *              four most significant bits the type id {@code 5}. Then
     *              the data bytes for date and time component follow.
     *
     * Schematic algorithm:
     *
     * <pre>
     *      out.writeByte(5 << 4);
     *      out.writeObject(timestamp.getCalendarDate());
     *      out.writeObject(timestamp.getWallTime());
     * </pre>
     */
    private Object writeReplace() throws ObjectStreamException {

        return new SPX(this, SPX.TIMESTAMP_TYPE);

    }

    /**
     * @serialData  Blocks because a serialization proxy is required.
     * @throws      InvalidObjectException (always)
     */
    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {

        throw new InvalidObjectException("Serialization proxy required.");

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class Merger
        implements ChronoMerger<PlainTimestamp> {

        //~ Methoden ------------------------------------------------------

        @Override
        public PlainTimestamp createFrom(
            TimeSource<?> clock,
            final AttributeQuery attributes
        ) {

            Timezone zone;

            if (attributes.contains(Attributes.TIMEZONE_ID)) {
                zone = Timezone.of(attributes.get(Attributes.TIMEZONE_ID));
            } else {
                zone = Timezone.ofSystem();
            }

            final UnixTime ut = clock.currentTime();
            return PlainTimestamp.from(ut, zone.getOffset(ut));

        }

        @Override
        public PlainTimestamp createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes
        ) {

            PlainDate date = null;
            PlainTime time = null;

            if (entity.contains(CALENDAR_DATE)) {
                date = entity.get(CALENDAR_DATE);
            } else {
                date = PlainDate.ENGINE.createFrom(entity, attributes);
            }

            if (date == null) {
                return null;
            } else if (entity.contains(WALL_TIME)) {
                time = entity.get(WALL_TIME);
            } else {
                time = PlainTime.ENGINE.createFrom(entity, attributes);

                if (time == null) {
                    Leniency leniency =
                        attributes.get(Attributes.LENIENCY, Leniency.SMART);
                    if (!leniency.isStrict()) {
                        time = PlainTime.MIN;
                    }
                }
            }

            if (time == null) {
                return null;
            } else {
                return PlainTimestamp.of(date, time);
            }

        }

        @Override
        public ChronoEntity<?> preformat(
            PlainTimestamp context,
            AttributeQuery attributes
        ) {

            return context;

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
                DayCycles cycles =
                    timepoint.time.roll(amount, this.clockUnit);
                d =
                    timepoint.date.plus(
                        cycles.getDayOverflow(),
                        CalendarUnit.DAYS);
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
                    PlainTime t1 = start.time;
                    PlainTime t2 = end.time;

                    if ((delta > 0) && t1.isAfter(t2)) {
                        delta--;
                    } else if ((delta < 0) && t1.isBefore(t2)) {
                        delta++;
                    }
                }
            } else if (start.date.isAfter(end.date)) {
                delta = -between(end, start);
            } else {
                long days = start.date.until(end.date, CalendarUnit.DAYS);

                if (days == 0) {
                    return this.clockUnit.between(start.time, end.time);
                } else if (this.clockUnit.compareTo(ClockUnit.SECONDS) <= 0) {
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
