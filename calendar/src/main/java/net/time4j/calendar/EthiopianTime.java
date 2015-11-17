/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (EthiopianTime.java) is part of project Time4J.
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

package net.time4j.calendar;

import net.time4j.Meridiem;
import net.time4j.PlainTime;
import net.time4j.base.MathUtils;
import net.time4j.base.TimeSource;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoMerger;
import net.time4j.engine.ChronoUnit;
import net.time4j.engine.Chronology;
import net.time4j.engine.DisplayStyle;
import net.time4j.engine.ElementRule;
import net.time4j.engine.FormattableElement;
import net.time4j.engine.StartOfDay;
import net.time4j.engine.Temporal;
import net.time4j.engine.TimeAxis;
import net.time4j.engine.TimePoint;
import net.time4j.engine.UnitRule;
import net.time4j.format.CalendarType;
import net.time4j.format.LocalizedPatternSupport;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;


/**
 * <p>Represents the 12-hour-time in second precision used in Ethiopia
 * starting in the morning at 6 AM as zero point. </p>
 *
 * <p>Mapping table: </p>
 *
 * <table border="1">
 *     <tr>
 *         <th>ISO-8601</th>
 *         <th>Ethiopic</th>
 *     </tr>
 *     <tr>
 *         <td>00:00</td>
 *         <td>6 (night)</td>
 *     </tr>
 *     <tr>
 *         <td>01:00</td>
 *         <td>7 (night)</td>
 *     </tr>
 *     <tr>
 *         <td>02:00</td>
 *         <td>8 (night)</td>
 *     </tr>
 *     <tr>
 *         <td>03:00</td>
 *         <td>9 (night)</td>
 *     </tr>
 *     <tr>
 *         <td>04:00</td>
 *         <td>10 (night)</td>
 *     </tr>
 *     <tr>
 *         <td>05:00</td>
 *         <td>11 (night)</td>
 *     </tr>
 *     <tr>
 *         <td>06:00</td>
 *         <td>12 (day)</td>
 *     </tr>
 *     <tr>
 *         <td>07:00</td>
 *         <td>1 (day)</td>
 *     </tr>
 *     <tr>
 *         <td>08:00</td>
 *         <td>2 (day)</td>
 *     </tr>
 *     <tr>
 *         <td>09:00</td>
 *         <td>3 (day)</td>
 *     </tr>
 *     <tr>
 *         <td>10:00</td>
 *         <td>4 (day)</td>
 *     </tr>
 *     <tr>
 *         <td>11:00</td>
 *         <td>5 (day)</td>
 *     </tr>
 *     <tr>
 *         <td>12:00</td>
 *         <td>6 (day)</td>
 *     </tr>
 *     <tr>
 *         <td>13:00</td>
 *         <td>7 (day)</td>
 *     </tr>
 *     <tr>
 *         <td>14:00</td>
 *         <td>8 (day)</td>
 *     </tr>
 *     <tr>
 *         <td>15:00</td>
 *         <td>9 (day)</td>
 *     </tr>
 *     <tr>
 *         <td>16:00</td>
 *         <td>10 (day)</td>
 *     </tr>
 *     <tr>
 *         <td>17:00</td>
 *         <td>11 (day)</td>
 *     </tr>
 *     <tr>
 *         <td>18:00</td>
 *         <td>12 (night)</td>
 *     </tr>
 *     <tr>
 *         <td>19:00</td>
 *         <td>1 (night)</td>
 *     </tr>
 *     <tr>
 *         <td>20:00</td>
 *         <td>2 (night)</td>
 *     </tr>
 *     <tr>
 *         <td>21:00</td>
 *         <td>3 (night)</td>
 *     </tr>
 *     <tr>
 *         <td>22:00</td>
 *         <td>4 (night)</td>
 *     </tr>
 *     <tr>
 *         <td>23:00</td>
 *         <td>5 (night)</td>
 *     </tr>
 * </table>
 *
 * @author  Meno Hochschild
 * @since   3.11/4.8
 */
/*[deutsch]
 * <p>Repr&auml;sentiert die 12-Stunden-Uhr in Sekundengenauigkeit, die in &Auml;thiopien verwendet wird
 * und morgens um 6 Uhr startet. </p>
 *
 * <p>Konversionstabelle: </p>
 *
 * <table border="1">
 *     <tr>
 *         <th>ISO-8601</th>
 *         <th>&Auml;thiopisch</th>
 *     </tr>
 *     <tr>
 *         <td>00:00</td>
 *         <td>6 (night)</td>
 *     </tr>
 *     <tr>
 *         <td>01:00</td>
 *         <td>7 (night)</td>
 *     </tr>
 *     <tr>
 *         <td>02:00</td>
 *         <td>8 (night)</td>
 *     </tr>
 *     <tr>
 *         <td>03:00</td>
 *         <td>9 (night)</td>
 *     </tr>
 *     <tr>
 *         <td>04:00</td>
 *         <td>10 (night)</td>
 *     </tr>
 *     <tr>
 *         <td>05:00</td>
 *         <td>11 (night)</td>
 *     </tr>
 *     <tr>
 *         <td>06:00</td>
 *         <td>12 (day)</td>
 *     </tr>
 *     <tr>
 *         <td>07:00</td>
 *         <td>1 (day)</td>
 *     </tr>
 *     <tr>
 *         <td>08:00</td>
 *         <td>2 (day)</td>
 *     </tr>
 *     <tr>
 *         <td>09:00</td>
 *         <td>3 (day)</td>
 *     </tr>
 *     <tr>
 *         <td>10:00</td>
 *         <td>4 (day)</td>
 *     </tr>
 *     <tr>
 *         <td>11:00</td>
 *         <td>5 (day)</td>
 *     </tr>
 *     <tr>
 *         <td>12:00</td>
 *         <td>6 (day)</td>
 *     </tr>
 *     <tr>
 *         <td>13:00</td>
 *         <td>7 (day)</td>
 *     </tr>
 *     <tr>
 *         <td>14:00</td>
 *         <td>8 (day)</td>
 *     </tr>
 *     <tr>
 *         <td>15:00</td>
 *         <td>9 (day)</td>
 *     </tr>
 *     <tr>
 *         <td>16:00</td>
 *         <td>10 (day)</td>
 *     </tr>
 *     <tr>
 *         <td>17:00</td>
 *         <td>11 (day)</td>
 *     </tr>
 *     <tr>
 *         <td>18:00</td>
 *         <td>12 (night)</td>
 *     </tr>
 *     <tr>
 *         <td>19:00</td>
 *         <td>1 (night)</td>
 *     </tr>
 *     <tr>
 *         <td>20:00</td>
 *         <td>2 (night)</td>
 *     </tr>
 *     <tr>
 *         <td>21:00</td>
 *         <td>3 (night)</td>
 *     </tr>
 *     <tr>
 *         <td>22:00</td>
 *         <td>4 (night)</td>
 *     </tr>
 *     <tr>
 *         <td>23:00</td>
 *         <td>5 (night)</td>
 *     </tr>
 * </table>
 *
 * @author  Meno Hochschild
 * @since   3.11/4.8
 */
@CalendarType("ethiopic")
public final class EthiopianTime
    extends TimePoint<EthiopianTime.Unit, EthiopianTime>
    implements Temporal<EthiopianTime>, LocalizedPatternSupport {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int ETHIOPIAN_HOUR_INDEX = 0;
    private static final int DIGITAL_HOUR_INDEX = 1;
    private static final int MINUTE_INDEX = 2;
    private static final int SECOND_INDEX = 3;

    /**
     * Behaves like {@link PlainTime#AM_PM_OF_DAY}.
     */
    /*[deutsch]
     * Verh&auml;lt sich wie {@link PlainTime#AM_PM_OF_DAY}.
     */
    @FormattableElement(format = "a")
    public static final ChronoElement<Meridiem> AM_PM_OF_DAY = PlainTime.AM_PM_OF_DAY;

    /**
     * The Ethiopian hour with the range 1-12 which is 6 hours behind the western clock.
     */
    /*[deutsch]
     * Die &auml;thiopische Stunde mit dem Bereich 1-12, die der westlichen Uhr um 6 Stunden nacheilt.
     */
    @FormattableElement(format = "h")
    public static final ChronoElement<Integer> ETHIOPIAN_HOUR = EthiopianHour.ELEMENT;

    /**
     * Behaves like {@link PlainTime#DIGITAL_HOUR_OF_DAY} with the hour range 0-23.
     */
    /*[deutsch]
     * Verh&auml;lt sich wie {@link PlainTime#DIGITAL_HOUR_OF_DAY} mit dem Stundenbereich 0-23.
     */
    @FormattableElement(format = "H")
    public static final ChronoElement<Integer> DIGITAL_HOUR_OF_DAY = PlainTime.DIGITAL_HOUR_OF_DAY;

    /**
     * The minute of hour with the range 0-59.
     */
    /*[deutsch]
     * Die Minute innerhalb einer Stunde mit dem Bereich 0-59.
     */
    @FormattableElement(format = "m")
    public static final ChronoElement<Integer> MINUTE_OF_HOUR = PlainTime.MINUTE_OF_HOUR;

    /**
     * The second of minute with the range 0-59.
     */
    /*[deutsch]
     * Die Sekunde innerhalb einer Minute mit dem Bereich 0-59.
     */
    @FormattableElement(format = "s")
    public static final ChronoElement<Integer> SECOND_OF_MINUTE = PlainTime.SECOND_OF_MINUTE;

    private static final EthiopianTime MIN;
    private static final EthiopianTime MAX;
    private static final TimeAxis<Unit, EthiopianTime> ENGINE;

    static {
        MIN = new EthiopianTime(6, 0, 0);
        MAX = new EthiopianTime(5, 59, 59);

        TimeAxis.Builder<Unit, EthiopianTime> builder =
            TimeAxis.Builder.setUp(
                Unit.class,
                EthiopianTime.class,
                new Merger(),
                EthiopianTime.MIN,
                EthiopianTime.MAX
            ).appendElement(
                AM_PM_OF_DAY,
                new MeridiemRule())
            .appendElement(
                ETHIOPIAN_HOUR,
                new IntegerElementRule(ETHIOPIAN_HOUR_INDEX),
                Unit.HOURS)
            .appendElement(
                DIGITAL_HOUR_OF_DAY,
                new IntegerElementRule(DIGITAL_HOUR_INDEX),
                Unit.HOURS)
            .appendElement(
                MINUTE_OF_HOUR,
                new IntegerElementRule(MINUTE_INDEX),
                Unit.MINUTES)
            .appendElement(
                SECOND_OF_MINUTE,
                new IntegerElementRule(SECOND_INDEX),
                Unit.SECONDS);
        registerUnits(builder);
        ENGINE = builder.build();
    }

    private static final long serialVersionUID = 3576122091324773241L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final int hour24;
    private transient final int minute;
    private transient final int second;

    //~ Konstruktoren -----------------------------------------------------

    private EthiopianTime(
        int hour24,
        int minute,
        int second
    ) {
        super();

        if (hour24 < 0 || hour24 > 23) {
            throw new IllegalArgumentException(
                "HOUR_OF_DAY out of range: " + hour24);
        }

        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException(
                "MINUTE_OF_HOUR out of range: " + minute);
        }

        if (second < 0 || second > 59) {
            throw new IllegalArgumentException(
                "SECOND_OF_MINUTE out of range: " + second);
        }

        this.hour24 = hour24;
        this.minute = minute;
        this.second = second;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Equivalent to {@link #ofDay(int, int, int) ofDay(hour, minute, 0)}. </p>
     *
     * @param   hour    ethiopian hour in range 1-12 during day
     * @param   minute  minute of hour
     * @return  ethiopian time
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>&Auml;quivalent zu {@link #ofDay(int, int, int) ofDay(hour, minute, 0)}. </p>
     *
     * @param   hour    ethiopian hour in range 1-12 during day
     * @param   minute  minute of hour
     * @return  ethiopian time
     * @since   3.11/4.8
     */
    public static EthiopianTime ofDay(
        int hour,
        int minute
    ) {

        return EthiopianTime.of(false, hour, minute, 0);

    }

    /**
     * <p>Creates a new instance for times in the ISO-range (06:00:00-17:59:59). </p>
     *
     * @param   hour    ethiopian hour in range 1-12 during day
     * @param   minute  minute of hour
     * @param   second  second of hour
     * @return  ethiopian time
     * @see     #ofNight(int, int, int)
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Erzeugt eine neue Instanz im ISO-Bereich (06:00:00-17:59:59). </p>
     *
     * @param   hour    ethiopian hour in range 1-12 during day
     * @param   minute  minute of hour
     * @param   second  second of hour
     * @return  ethiopian time
     * @see     #ofNight(int, int, int)
     * @since   3.11/4.8
     */
    public static EthiopianTime ofDay(
        int hour,
        int minute,
        int second
    ) {

        return EthiopianTime.of(false, hour, minute, second);

    }

    /**
     * <p>Equivalent to {@link #ofNight(int, int, int) ofNight(hour, minute, 0)}. </p>
     *
     * @param   hour    ethiopian hour in range 1-12 during night
     * @param   minute  minute of hour
     * @return  ethiopian time
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>&Auml;quivalent zu {@link #ofNight(int, int, int) ofNight(hour, minute, 0)}. </p>
     *
     * @param   hour    ethiopian hour in range 1-12 during night
     * @param   minute  minute of hour
     * @return  ethiopian time
     * @since   3.11/4.8
     */
    public static EthiopianTime ofNight(
        int hour,
        int minute
    ) {

        return EthiopianTime.of(true, hour, minute, 0);

    }

    /**
     * <p>Creates a new instance for times in the ISO-range (18:00:00-05:59:59 around midnight). </p>
     *
     * @param   hour    ethiopian hour in range 1-12 during night
     * @param   minute  minute of hour
     * @param   second  second of hour
     * @return  ethiopian time
     * @see     #ofDay(int, int, int)
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Erzeugt eine neue Instanz im ISO-Bereich (18:00:00-05:59:59 um Mitternacht herum). </p>
     *
     * @param   hour    ethiopian hour in range 1-12 during night
     * @param   minute  minute of hour
     * @param   second  second of hour
     * @return  ethiopian time
     * @see     #ofDay(int, int, int)
     * @since   3.11/4.8
     */
    public static EthiopianTime ofNight(
        int hour,
        int minute,
        int second
    ) {

        return EthiopianTime.of(true, hour, minute, second);

    }

    /**
     * <p>Is this time during day (ISO 06:00:00-17:59:59)? </p>
     *
     * @return  boolean
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Liegt diese Uhrzeit am Tage (ISO 06:00:00-17:59:59)? </p>
     *
     * @return  boolean
     * @since   3.11/4.8
     */
    public boolean isDay() {

        return ((this.hour24 >= 6) && (this.hour24 < 18));

    }

    /**
     * <p>Is this time during night (ISO 18:00:00-05:59:59)? </p>
     *
     * @return  boolean
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Liegt diese Uhrzeit in der Nacht (ISO 18:00:00-05:59:59)? </p>
     *
     * @return  boolean
     * @since   3.11/4.8
     */
    public boolean isNight() {

        return !this.isDay();

    }

    /**
     * <p>Yields the Ethiopian clock hour in range 1-12. </p>
     *
     * @return  hour in range 1-12
     * @see     #isDay()
     * @see     #isNight()
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Liefert die &auml;thiopische Uhrzeit im Bereich 1-12. </p>
     *
     * @return  hour in range 1-12
     * @see     #isDay()
     * @see     #isNight()
     * @since   3.11/4.8
     */
    public int getHour() {

        int h = this.hour24 - 6;

        if (h < 0) {
            h += 12;
        } else if (h >= 12) {
            h -= 12;
        }

        return ((h == 0) ? 12 : h);

    }

    /**
     * <p>Yields the minute of hour. </p>
     *
     * @return  int in range 0-59
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Liefert die Minute innerhalb der aktuellen Stunde. </p>
     *
     * @return  int in range 0-59
     * @since   3.11/4.8
     */
    public int getMinute() {

        return this.minute;

    }

    /**
     * <p>Yields the second of minute. </p>
     *
     * @return  int in range 0-59
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Liefert die Sekunde innerhalb der aktuellen Minute. </p>
     *
     * @return  int in range 0-59
     * @since   3.11/4.8
     */
    public int getSecond() {

        return this.second;

    }

    @Override
    public boolean isAfter(EthiopianTime other) {

        return (this.getTimeOfDay() > other.getTimeOfDay());

    }

    @Override
    public boolean isBefore(EthiopianTime other) {

        return (this.getTimeOfDay() < other.getTimeOfDay());

    }

    @Override
    public boolean isSimultaneous(EthiopianTime other) {

        return (this.getTimeOfDay() == other.getTimeOfDay());

    }

    @Override
    public int compareTo(EthiopianTime other) {

        return (this.getTimeOfDay() - other.getTimeOfDay());

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof EthiopianTime) {
            EthiopianTime that = (EthiopianTime) obj;
            return (this.getTimeOfDay() == that.getTimeOfDay());
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return this.getTimeOfDay();

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("ethiopic-");
        sb.append(this.isDay() ? "day-" : "night-");
        sb.append(this.getHour());
        sb.append(':');
        if (this.minute < 10) {
            sb.append('0');
        }
        sb.append(this.minute);
        sb.append(':');
        if (this.second < 10) {
            sb.append('0');
        }
        sb.append(this.second);
        return sb.toString();

    }

    /**
     * <p>Converts this instance to its ISO-analogy. </p>
     *
     * @return  PlainTime
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Konvertiert diese Instanz zu ihrem ISO-Analogon. </p>
     *
     * @return  PlainTime
     * @since   3.11/4.8
     */
    public PlainTime toISO() {

        return PlainTime.of(this.hour24, this.minute, this.second);

    }

    /**
     * <p>Converts given ISO-time to Ethiopian time. </p>
     *
     * <p>The special time 24:00 will be treated like 00:00. Fractions of second are lost during conversion. </p>
     *
     * @param   time    ISO-time
     * @return  Ethiopian time
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Konvertiert die angegebene ISO-Uhrzeit zu einer &auml;thiopischen Uhrzeit. </p>
     *
     * <p>Die spezielle Uhrzeit 24:00 wird wie 00:00 gehandhabt. Sekundenbruchteile gehen w&auml;hrend der
     * Konversion verloren. </p>
     *
     * @param   time    ISO-time
     * @return  Ethiopian time
     * @since   3.11/4.8
     */
    public static EthiopianTime from(PlainTime time) {

        int h24 = time.getHour();
        return new EthiopianTime((h24 == 24) ? 0 : h24, time.getMinute(), time.getSecond());

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
    public static TimeAxis<EthiopianTime.Unit, EthiopianTime> axis() {

        return ENGINE;

    }

    @Override
    protected TimeAxis<Unit, EthiopianTime> getChronology() {

        return ENGINE;

    }

    @Override
    protected EthiopianTime getContext() {

        return this;

    }

    private static EthiopianTime of(
        boolean night,
        int hour,
        int minute,
        int second
    ) {

        if (hour < 1 || hour > 12) {
            throw new IllegalArgumentException("Hour out of range 1-12: " + hour);
        }

        int h = ((hour == 12) ? 0 : hour);
        int hour24 = h + 6;

        if (night) {
            hour24 += 12;
            if (hour24 >= 24) {
                hour24 -= 24;
            }
        }

        return new EthiopianTime(hour24, minute, second);

    }

    private int getTimeOfDay() {

        return (
            this.second
            + this.minute * 60
            + ((this.hour24 < 6) ? this.hour24 + 24 : this.hour24) * 3600
        );

    }

    private static void registerUnits(TimeAxis.Builder<Unit, EthiopianTime> builder) {

        Set<Unit> convertibles = EnumSet.allOf(Unit.class);

        for (Unit unit : Unit.values()) {
            builder.appendUnit(
                unit,
                new ClockUnitRule(unit),
                unit.getLength(),
                convertibles);
        }

    }

    /**
     * @serialData  Uses <a href="../../serialized-form.html#net.time4j.SPX">
     *              a dedicated serialization form</a> as proxy. The first byte contains
     *              the type-ID {@code 5}. Then the time of day in seconds using western
     *              format is written as integer.
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.ETHIOPIAN_TIME);

    }

    /**
     * @serialData  Blocks because a serialization proxy is required.
     * @param       in      object input stream
     * @throws InvalidObjectException (always)
     */
    private void readObject(ObjectInputStream in)
        throws IOException {

        throw new InvalidObjectException("Serialization proxy required.");

    }

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Defines the time units for the Ethiopian clock time. </p>
     *
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Definiert die Zeiteinheiten f&uuml;r die &auml;thiopische Uhrzeit. </p>
     *
     * @since   3.11/4.8
     */
    public static enum Unit
        implements ChronoUnit {

        //~ Statische Felder/Initialisierungen ----------------------------

        HOURS(3600.0),

        MINUTES(60.0),

        SECONDS(1.0);

        //~ Instanzvariablen ----------------------------------------------

        private transient final double length;

        //~ Konstruktoren -------------------------------------------------

        private Unit(double length) {
            this.length = length;
        }

        //~ Methoden ------------------------------------------------------

        @Override
        public double getLength() {

            return this.length;

        }

        @Override
        public boolean isCalendrical() {

            return false;

        }

        /**
         * <p>Calculates the difference between given Ethiopian times in this unit. </p>
         *
         * @param   start   start time (inclusive)
         * @param   end     end time (exclusive)
         * @return  difference counted in this unit
         * @since   3.11/4.8
         */
        /*[deutsch]
         * <p>Berechnet die Differenz zwischen den angegebenen Zeitparametern in dieser Zeiteinheit. </p>
         *
         * @param   start   start time (inclusive)
         * @param   end     end time (exclusive)
         * @return  difference counted in this unit
         * @since   3.11/4.8
         */
        public int between(
            EthiopianTime start,
            EthiopianTime end
        ) {

            return (int) start.until(end, this); // safe

        }

    }

    private static class ClockUnitRule
        implements UnitRule<EthiopianTime> {

        //~ Instanzvariablen ----------------------------------------------

        private final Unit unit;

        //~ Konstruktoren -------------------------------------------------

        private ClockUnitRule(Unit unit) {
            super();

            this.unit = unit;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public EthiopianTime addTo(
            EthiopianTime context,
            long amount
        ) {

            if (amount == 0) {
                return context;
            }

            long hours;
            long minutes;
            long seconds;

            int minute = context.minute;
            int second = context.second;

            switch (this.unit) {
                case HOURS:
                    hours = MathUtils.safeAdd(context.hour24, amount);
                    break;
                case MINUTES:
                    minutes = MathUtils.safeAdd(context.minute, amount);
                    hours =
                        MathUtils.safeAdd(
                            context.hour24,
                            MathUtils.floorDivide(minutes, 60));
                    minute = MathUtils.floorModulo(minutes, 60);
                    break;
                case SECONDS:
                    seconds = MathUtils.safeAdd(context.second, amount);
                    minutes =
                        MathUtils.safeAdd(
                            context.minute,
                            MathUtils.floorDivide(seconds, 60));
                    hours =
                        MathUtils.safeAdd(
                            context.hour24,
                            MathUtils.floorDivide(minutes, 60));
                    minute = MathUtils.floorModulo(minutes, 60);
                    second = MathUtils.floorModulo(seconds, 60);
                    break;
                default:
                    throw new UnsupportedOperationException(this.unit.name());
            }

            int h24 = MathUtils.floorModulo(hours, 24);
            return new EthiopianTime(h24, minute, second);

        }

        @Override
        public long between(
            EthiopianTime start,
            EthiopianTime end
        ) {

            long delta = (end.getTimeOfDay() - start.getTimeOfDay());
            long factor;

            switch (this.unit) {
                case HOURS:
                    factor = 3600;
                    break;
                case MINUTES:
                    factor = 60;
                    break;
                case SECONDS:
                    factor = 1;
                    break;
                default:
                    throw new UnsupportedOperationException(this.unit.name());
            }

            return delta / factor;

        }

    }

    private static class EthiopianHour
        implements ChronoElement<Integer>, Serializable {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final long serialVersionUID = -2095959121446847268L;
        static final EthiopianHour ELEMENT = new EthiopianHour();

        //~ Konstruktoren -------------------------------------------------

        private EthiopianHour() {
            super();

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public String name() {
            return "ETHIOPIAN_HOUR";
        }

        @Override
        public Class<Integer> getType() {
            return Integer.class;
        }

        @Override
        public char getSymbol() {
            return 'h';
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
            return Integer.valueOf(1);
        }

        @Override
        public Integer getDefaultMaximum() {
            return Integer.valueOf(12);
        }

        @Override
        public boolean isDateElement() {
            return false;
        }

        @Override
        public boolean isTimeElement() {
            return true;
        }

        @Override
        public boolean isLenient() {
            return false;
        }

        private Object readResolve() {
            return ELEMENT;
        }

    }

    private static class MeridiemRule
        implements ElementRule<EthiopianTime, Meridiem> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Meridiem getValue(EthiopianTime context) {

            return ((context.hour24 < 12) ? Meridiem.AM : Meridiem.PM);

        }

        @Override
        public EthiopianTime withValue(
            EthiopianTime context,
            Meridiem value,
            boolean lenient
        ) {

            int h = context.hour24;

            if (value == null) {
                throw new NullPointerException("Missing am/pm-value.");
            } else if (value == Meridiem.AM) {
                if (h >= 12) {
                    h -= 12;
                }
            } else if (value == Meridiem.PM) {
                if (h < 12) {
                    h += 12;
                }
            }

            return new EthiopianTime(h, context.minute, context.second);

        }

        @Override
        public boolean isValid(
            EthiopianTime context,
            Meridiem value
        ) {

            return (value != null);

        }

        @Override
        public Meridiem getMinimum(EthiopianTime context) {

            return Meridiem.AM;

        }

        @Override
        public Meridiem getMaximum(EthiopianTime context) {

            return Meridiem.PM;

        }

        @Override
        public ChronoElement<?> getChildAtFloor(EthiopianTime context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(EthiopianTime context) {

            return null;

        }

    }

    private static class IntegerElementRule
        implements ElementRule<EthiopianTime, Integer> {

        //~ Instanzvariablen ----------------------------------------------

        private final int index;

        //~ Konstruktoren -------------------------------------------------

        IntegerElementRule(int index) {
            super();

            this.index = index;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Integer getValue(EthiopianTime context) {
            switch (this.index) {
                case ETHIOPIAN_HOUR_INDEX:
                    return context.getHour();
                case DIGITAL_HOUR_INDEX:
                    return context.hour24;
                case MINUTE_INDEX:
                    return context.minute;
                case SECOND_INDEX:
                    return context.second;
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }
        }

        @Override
        public Integer getMinimum(EthiopianTime context) {
            switch (this.index) {
                case ETHIOPIAN_HOUR_INDEX:
                    return Integer.valueOf(1);
                case DIGITAL_HOUR_INDEX:
                case MINUTE_INDEX:
                case SECOND_INDEX:
                    return Integer.valueOf(0);
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }
        }

        @Override
        public Integer getMaximum(EthiopianTime context) {
            switch (this.index) {
                case ETHIOPIAN_HOUR_INDEX:
                    return Integer.valueOf(12);
                case DIGITAL_HOUR_INDEX:
                    return Integer.valueOf(23);
                case MINUTE_INDEX:
                case SECOND_INDEX:
                    return Integer.valueOf(59);
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }
        }

        @Override
        public boolean isValid(
            EthiopianTime context,
            Integer value
        ) {
            return (
                (this.getMinimum(context).compareTo(value) <= 0)
                && (this.getMaximum(context).compareTo(value) >= 0)
            );
        }

        @Override
        public EthiopianTime withValue(
            EthiopianTime context,
            Integer value,
            boolean lenient
        ) {
            int v = value.intValue();

            switch (this.index) {
                case ETHIOPIAN_HOUR_INDEX:
                    if (context.isDay()) {
                        return EthiopianTime.ofDay(v, context.minute, context.second);
                    } else {
                        return EthiopianTime.ofNight(v, context.minute, context.second);
                    }
                case DIGITAL_HOUR_INDEX:
                    return new EthiopianTime(v, context.minute, context.second);
                case MINUTE_INDEX:
                    return new EthiopianTime(context.hour24, v, context.second);
                case SECOND_INDEX:
                    return new EthiopianTime(context.hour24, context.minute, v);
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }
        }

        @Override
        public ChronoElement<?> getChildAtFloor(EthiopianTime context) {
            return null;
        }

        @Override
        public ChronoElement<?> getChildAtCeiling(EthiopianTime context) {
            return null;
        }

    }

    private static class Merger
        implements ChronoMerger<EthiopianTime> {

        //~ Methoden ------------------------------------------------------

        @Override
        public String getFormatPattern(
            DisplayStyle style,
            Locale locale
        ) {
            return ((style.getStyleValue() == DateFormat.SHORT) ? "h:mm a" : "h:mm:ss a");
        }

        @Override
        public EthiopianTime createFrom(
            TimeSource<?> clock,
            AttributeQuery attributes
        ) {
            return EthiopianTime.from(PlainTime.axis().createFrom(clock, attributes));
        }

        @Override
        public EthiopianTime createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean preparsing
        ) {
            PlainTime time = PlainTime.axis().createFrom(entity, attributes, false);

            if (time != null) {
                return EthiopianTime.from(time);
            } else if (entity.contains(AM_PM_OF_DAY) && entity.contains(ETHIOPIAN_HOUR)) {
                Meridiem meridiem = entity.get(AM_PM_OF_DAY);
                int hour = entity.get(ETHIOPIAN_HOUR);
                if (hour == 12) {
                    hour = 0;
                }
                hour += 6;
                if (hour >= 12) {
                    hour -= 12;
                }
                if (meridiem == Meridiem.PM) {
                    hour += 12;
                }
                int minute = 0, second = 0;
                if (entity.contains(MINUTE_OF_HOUR)) {
                    minute = entity.get(MINUTE_OF_HOUR);
                }
                if (entity.contains(SECOND_OF_MINUTE)) {
                    second = entity.get(SECOND_OF_MINUTE);
                }
                return new EthiopianTime(hour, minute, second);
            }

            return null;
        }

        @Override
        public ChronoDisplay preformat(
            EthiopianTime context,
            AttributeQuery attributes
        ) {
            return context;
        }

        @Override
        public Chronology<?> preparser() {
            return null;
        }

        @Override
        public StartOfDay getDefaultStartOfDay() {

            return StartOfDay.MORNING;

        }

    }

}
