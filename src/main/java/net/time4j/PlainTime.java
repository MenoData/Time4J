/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (PlainTime.java) is part of project Time4J.
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

import net.time4j.base.MathUtils;
import net.time4j.base.TimeSource;
import net.time4j.base.UnixTime;
import net.time4j.base.WallTime;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoMerger;
import net.time4j.engine.ElementRule;
import net.time4j.engine.FormattableElement;
import net.time4j.engine.Temporal;
import net.time4j.engine.TimeAxis;
import net.time4j.engine.TimePoint;
import net.time4j.engine.UnitRule;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarType;
import net.time4j.format.ChronoFormatter;
import net.time4j.format.ChronoPattern;
import net.time4j.format.DisplayMode;
import net.time4j.format.Leniency;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * <p>Repr&auml;sentiert eine reine Uhrzeit ohne Zeitzonen- oder Datumsteil
 * nach dem ISO-8601-Standard in maximal Nanosekundengenauigkeit. </p>
 *
 * <p>Hinweis: UTC-Schaltsekunden k&ouml;nnen mangels Datumsbezug
 * nicht unterst&uuml;tzt werden. Wird z.B. ein {@code Moment}
 * nach seiner Uhrzeit als {@code PlainTime} statt direkt der UTC-Sekunde
 * gefragt, so wird eine eventuelle Schaltsekunde auf den Wert {@code 59}
 * zur&uuml;ckgesetzt. </p>
 *
 * <p>Diese Klasse unterst&uuml;tzt auch den Spezialwert T24:00 in ihrem
 * Zustandsraum, w&auml;hrend die Klasse {@code PlainTimestamp} den Wert
 * lediglich in der Instanzerzeugung, aber nicht in der Manipulation von
 * Daten akzeptiert. </p>
 *
 * <p>Registriert sind folgende als Konstanten deklarierte Elemente: </p>
 *
 * <ul>
 *  <li>{@link #AM_PM_OF_DAY}</li>
 *  <li>{@link #CLOCK_HOUR_OF_AMPM}</li>
 *  <li>{@link #CLOCK_HOUR_OF_DAY}</li>
 *  <li>{@link #DIGITAL_HOUR_OF_AMPM}</li>
 *  <li>{@link #DIGITAL_HOUR_OF_DAY}</li>
 *  <li>{@link #ISO_HOUR}</li>
 *  <li>{@link #MINUTE_OF_HOUR}</li>
 *  <li>{@link #MINUTE_OF_DAY}</li>
 *  <li>{@link #SECOND_OF_MINUTE}</li>
 *  <li>{@link #SECOND_OF_DAY}</li>
 *  <li>{@link #MILLI_OF_SECOND}</li>
 *  <li>{@link #MICRO_OF_SECOND}</li>
 *  <li>{@link #NANO_OF_SECOND}</li>
 *  <li>{@link #MILLI_OF_DAY}</li>
 *  <li>{@link #MICRO_OF_DAY}</li>
 *  <li>{@link #NANO_OF_DAY}</li>
 *  <li>{@link #PRECISION}</li>
 *  <li>{@link #DECIMAL_HOUR}</li>
 *  <li>{@link #DECIMAL_MINUTE}</li>
 *  <li>{@link #DECIMAL_SECOND}</li>
 * </ul>
 *
 * @author      Meno Hochschild
 * @concurrency <immutable>
 */
@CalendarType("iso8601")
public final class PlainTime
    extends TimePoint<IsoTimeUnit, PlainTime>
    implements WallTime, Temporal<PlainTime> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * System-Property f&uuml;r die Darstellung des Dezimaltrennzeichens.
     */
    static final char ISO_DECIMAL_SEPARATOR = (
        Boolean.getBoolean("net.time4j.format.iso.decimal.dot")
        ? '.'
        : ',' // Empfehlung des ISO-Standards
    );

    private static final int MRD = 1000000000;
    private static final int MIO = 1000000;
    private static final int KILO = 1000;

    private static final BigDecimal DECIMAL_24 = new BigDecimal(24);
    private static final BigDecimal DECIMAL_60 = new BigDecimal(60);
    private static final BigDecimal DECIMAL_3600 = new BigDecimal(3600);
    private static final BigDecimal DECIMAL_MRD = new BigDecimal(MRD);

    private static final BigDecimal DECIMAL_24_0 =
        new BigDecimal("24");
    private static final BigDecimal DECIMAL_59_9 =
        new BigDecimal("59.999999999999999");

    private static final PlainTime[] HOURS = new PlainTime[25];
    private static final long serialVersionUID = 2780881537313863339L;

    static {
        for (int i = 0; i <= 24; i++) {
            HOURS[i] = new PlainTime(i, 0, 0, 0);
        }
    }

    /** Minimalwert. */
    static final PlainTime MIN = HOURS[0];

    /** Maximalwert. */
    static final PlainTime MAX = HOURS[24];

    /**
     * <p>Element mit der Uhrzeit im Wertebereich {@code [T00:00:00,000000000]}
     * bis {@code [T24:00:00,000000000]} (inklusive im Kontext von
     * {@code PlainTime}, sonst exklusive). </p>
     *
     * <p>Dieses Element definiert keine Basiseinheit. </p>
     */
    static final ChronoElement<PlainTime> WALL_TIME = TimeElement.INSTANCE;

    /**
     * <p>Element mit dem Tagesabschnitt relativ zur Mittagszeit (Vormittag
     * oder Nachmittag). </p>
     *
     * <p>Dieses Element behandelt die Zeit T24:00 genauso wie T00:00, macht
     * also keinen Unterschied zwischen Anfang und Ende eines Tages. Im Detail
     * sieht die Stundenzuordnung so aus: </p>
     *
     * <div style="margin-top:5px;">
     * <table border="1">
     * <tr>
     *  <td>AM_PM_OF_DAY</td><td>AM</td><td>AM</td><td>...</td><td>AM</td>
     *  <td>PM</td><td>PM</td><td>...</td><td>PM</td><td>AM</td>
     * </tr>
     * <tr>
     *  <td>ISO-8601-Wert</td><td>T0</td><td>T1</td><td>...</td><td>T11</td>
     *  <td>T12</td><td>T13</td><td>...</td><td>T23</td><td>T24</td>
     * </tr>
     * </table>
     * </div>
     *
     * <p>Anwendungsbeispiel: </p>
     *
     * <pre>
     *  import static net.time4j.PlainTime.AM_PM_OF_DAY;
     *
     *  PlainTime time = PlainTime.of(12, 45, 20);
     *  System.out.println(time.get(AM_PM_OF_DAY));
     *  // Ausgabe: PM
     * </pre>
     *
     * <p>Dieses Element definiert keine Basiseinheit. </p>
     */
    @FormattableElement(format = "a")
    public static final ChronoElement<Meridiem> AM_PM_OF_DAY =
        AmPmElement.AM_PM_OF_DAY;

    /**
     * <p>Element mit der Halbtagsstunde im Bereich {@code 1-12}
     * (Ziffernblattanzeige einer analogen Uhr). </p>
     *
     * <p>Dieses Element behandelt die Zeit T24:00 genauso wie T00:00, macht
     * also keinen Unterschied zwischen Anfang und Ende eines Tages. Das ist
     * eine Einschr&auml;nkung, die die Kompatibilit&auml;t mit CLDR und
     * {@code java.text.SimpleDateFormat} wahrt. Um den vollen Stundenbereich
     * zu unterst&uuml;tzen, sollte m&ouml;glichst {@link #ISO_HOUR} verwendet
     * werden. Im Detail sieht die Stundenzuordnung so aus: </p>
     *
     * <div style="margin-top:5px;">
     * <table border="1">
     * <tr>
     *  <td>CLOCK_HOUR_OF_AMPM</td><td>12</td><td>1</td><td>...</td><td>11</td>
     *  <td>12</td><td>1</td><td>...</td><td>11</td><td>12</td>
     * </tr>
     * <tr>
     *  <td>ISO-8601-Wert</td><td>T0</td><td>T1</td><td>...</td><td>T11</td>
     *  <td>T12</td><td>T13</td><td>...</td><td>T23</td><td>T24</td>
     * </tr>
     * </table>
     * </div>
     */
    @FormattableElement(format = "h")
    public static final
    AdjustableElement<Integer, PlainTime> CLOCK_HOUR_OF_AMPM =
        IntegerTimeElement.createClockElement("CLOCK_HOUR_OF_AMPM", false);

    /**
     * <p>Element mit der Stunde im Bereich {@code 1-24} (analoge Anzeige). </p>
     *
     * <p>Dieses Element behandelt die Zeit T24:00 genauso wie T00:00, macht
     * also keinen Unterschied zwischen Anfang und Ende eines Tages. Das ist
     * eine Einschr&auml;nkung, die die Kompatibilit&auml;t mit CLDR und
     * {@code java.text.SimpleDateFormat} wahrt. Um den vollen Stundenbereich
     * zu unterst&uuml;tzen, sollte m&ouml;glichst {@link #ISO_HOUR} verwendet
     * werden. Im Detail sieht die Stundenzuordnung so aus: </p>
     *
     * <div style="margin-top:5px;">
     * <table border="1">
     * <tr>
     *  <td>CLOCK_HOUR_OF_DAY</td><td>24</td><td>1</td><td>...</td><td>11</td>
     *  <td>12</td><td>13</td><td>...</td><td>23</td><td>24</td>
     * </tr>
     * <tr>
     *  <td>ISO-8601-Wert</td><td>T0</td><td>T1</td><td>...</td><td>T11</td>
     *  <td>T12</td><td>T13</td><td>...</td><td>T23</td><td>T24</td>
     * </tr>
     * </table>
     * </div>
     */
    @FormattableElement(format = "k")
    public static final
    AdjustableElement<Integer, PlainTime> CLOCK_HOUR_OF_DAY =
        IntegerTimeElement.createClockElement("CLOCK_HOUR_OF_DAY", true);

    /**
     * <p>Element mit der digitalen Halbtagsstunde im Bereich {@code 0-11}. </p>
     *
     * <p>Dieses Element behandelt die Zeit T24:00 genauso wie T00:00, macht
     * also keinen Unterschied zwischen Anfang und Ende eines Tages. Das ist
     * eine Einschr&auml;nkung, die die Kompatibilit&auml;t mit CLDR und
     * {@code java.text.SimpleDateFormat} wahrt. Um den vollen Stundenbereich
     * zu unterst&uuml;tzen, sollte m&ouml;glichst {@link #ISO_HOUR} verwendet
     * werden. Im Detail sieht die Stundenzuordnung so aus: </p>
     *
     * <div style="margin-top:5px;">
     * <table border="1">
     * <tr>
     *  <td>DIGITAL_HOUR_OF_AMPM</td><td>0</td><td>1</td><td>...</td><td>11</td>
     *  <td>0</td><td>1</td><td>...</td><td>11</td><td>0</td>
     * </tr>
     * <tr>
     *  <td>ISO-8601-Wert</td><td>T0</td><td>T1</td><td>...</td><td>T11</td>
     *  <td>T12</td><td>T13</td><td>...</td><td>T23</td><td>T24</td>
     * </tr>
     * </table>
     * </div>
     */
    @FormattableElement(format = "K")
    public static final
    ProportionalElement<Integer, PlainTime> DIGITAL_HOUR_OF_AMPM =
        IntegerTimeElement.createTimeElement(
            "DIGITAL_HOUR_OF_AMPM",
            IntegerTimeElement.DIGITAL_HOUR_OF_AMPM,
            0,
            11,
            'K');

    /**
     * <p>Element mit der digitalen Stunde im Bereich {@code 0-23}. </p>
     *
     * <p>Dieses Element behandelt die Zeit T24:00 genauso wie T00:00, macht
     * also keinen Unterschied zwischen Anfang und Ende eines Tages. Das ist
     * eine Einschr&auml;nkung, die die Kompatibilit&auml;t mit CLDR und
     * {@code java.text.SimpleDateFormat} wahrt. Um den vollen Stundenbereich
     * zu unterst&uuml;tzen, sollte m&ouml;glichst {@link #ISO_HOUR} verwendet
     * werden. Im Detail sieht die Stundenzuordnung so aus: </p>
     *
     * <div style="margin-top:5px;">
     * <table border="1">
     * <tr>
     *  <td>DIGITAL_HOUR_OF_DAY</td><td>0</td><td>1</td><td>...</td><td>11</td>
     *  <td>12</td><td>13</td><td>...</td><td>23</td><td>0</td>
     * </tr>
     * <tr>
     *  <td>ISO-8601-Wert</td><td>T0</td><td>T1</td><td>...</td><td>T11</td>
     *  <td>T12</td><td>T13</td><td>...</td><td>T23</td><td>T24</td>
     * </tr>
     * </table>
     * </div>
     */
    @FormattableElement(format = "H")
    public static final
    ProportionalElement<Integer, PlainTime> DIGITAL_HOUR_OF_DAY =
        IntegerTimeElement.createTimeElement(
            "DIGITAL_HOUR_OF_DAY",
            IntegerTimeElement.DIGITAL_HOUR_OF_DAY,
            0,
            23,
            'H');

    /**
     * <p>Element mit der ISO-8601-Stunde im Bereich {@code 0-24}. </p>
     *
     * <p>Im Kontext von {@code PlainTime} mit vollen Stunden ist das Maximum
     * {@code 24} und steht f&uuml;r die Uhrzeit T24:00, ansonsten ist das
     * Maximum in jedem anderen Kontext {@code 23}. </p>
     *
     * @see     #getHour()
     */
    public static final ProportionalElement<Integer, PlainTime> ISO_HOUR =
        IntegerTimeElement.createTimeElement(
            "ISO_HOUR",
            IntegerTimeElement.ISO_HOUR,
            0,
            23,
            '\u0000');

    /**
     * <p>Element mit der Minute im Bereich {@code 0-59}. </p>
     *
     * @see     #getMinute()
     */
    @FormattableElement(format = "m")
    public static final ProportionalElement<Integer, PlainTime> MINUTE_OF_HOUR =
        IntegerTimeElement.createTimeElement(
            "MINUTE_OF_HOUR",
            IntegerTimeElement.MINUTE_OF_HOUR,
            0,
            59,
            'm');

    /**
     * <p>Element mit der Minute des Tages im Bereich {@code 0-1440}. </p>
     *
     * <p>Im Kontext von {@code PlainTime} mit vollen Minuten ist das Maximum
     * {@code 1440} und steht f&uuml;r die Uhrzeit T24:00, ansonsten ist das
     * Maximum in jedem anderen Kontext {@code 1439}. </p>
     */
    public static final ProportionalElement<Integer, PlainTime> MINUTE_OF_DAY =
        IntegerTimeElement.createTimeElement(
            "MINUTE_OF_DAY",
            IntegerTimeElement.MINUTE_OF_DAY,
            0,
            1439,
            '\u0000');

    /**
     * <p>Element mit der Sekunde im Bereich {@code 0-59}. </p>
     *
     * <p>Dieses Element kennt im lokalen Kontext keine UTC-Schaltsekunden und
     * bezieht sich auf eine normale analoge Uhr. Wenn dieses Element im
     * UTC-Kontext ({@link Moment}) verwendet wird, dann ist der Wertebereich
     * stattdessen {@code 0-58/59/60}. </p>
     *
     * @see     #getSecond()
     */
    @FormattableElement(format = "s")
    public static final
    ProportionalElement<Integer, PlainTime> SECOND_OF_MINUTE =
        IntegerTimeElement.createTimeElement(
            "SECOND_OF_MINUTE",
            IntegerTimeElement.SECOND_OF_MINUTE,
            0,
            59,
            's');

    /**
     * <p>Element mit der Sekunde des Tages im Bereich
     * {@code 0-86400}. </p>
     *
     * <p>Im Kontext von {@code PlainTime} mit vollen Sekunden entspricht das
     * Maximum {@code 86400} der Uhrzeit T24:00, in jedem anderen Kontext gilt
     * {@code 86399}. UTC-Schaltsekunden werden nicht mitgez&auml;hlt. </p>
     */
    public static final
    ProportionalElement<Integer, PlainTime> SECOND_OF_DAY =
        IntegerTimeElement.createTimeElement(
            "SECOND_OF_DAY",
            IntegerTimeElement.SECOND_OF_DAY,
            0,
            86399,
            '\u0000');

    /**
     * <p>Element mit der Millisekunde im Bereich {@code 0-999}. </p>
     */
    public static final
    ProportionalElement<Integer, PlainTime> MILLI_OF_SECOND =
        IntegerTimeElement.createTimeElement(
            "MILLI_OF_SECOND",
            IntegerTimeElement.MILLI_OF_SECOND,
            0,
            999,
            '\u0000');

    /**
     * <p>Element mit der Mikrosekunde im Bereich {@code 0-999999}. </p>
     */
    public static final
    ProportionalElement<Integer, PlainTime> MICRO_OF_SECOND =
        IntegerTimeElement.createTimeElement(
            "MICRO_OF_SECOND",
            IntegerTimeElement.MICRO_OF_SECOND,
            0,
            999999,
            '\u0000');

    /**
     * <p>Element mit der Nanosekunde im Bereich {@code 0-999999999}. </p>
     */
    @FormattableElement(format = "S")
    public static final
    ProportionalElement<Integer, PlainTime> NANO_OF_SECOND =
        IntegerTimeElement.createTimeElement(
            "NANO_OF_SECOND",
            IntegerTimeElement.NANO_OF_SECOND,
            0,
            999999999,
            'S');

    /**
     * <p>Element mit der Tageszeit in Millisekunden im
     * Bereich {@code 0-86400000}. </p>
     *
     * <p>Im Kontext von {@code PlainTime} mit vollen Millisekunden ist das
     * Maximum {@code 86400000} (entsprechend der Uhrzeit T24:00), in jedem
     * anderen Kontext ist das Maximum der Wert {@code 86399999}.
     * UTC-Schaltsekunden werden nicht mitgez&auml;hlt. </p>
     */
    @FormattableElement(format = "A")
    public static final
    ProportionalElement<Integer, PlainTime> MILLI_OF_DAY =
        IntegerTimeElement.createTimeElement(
            "MILLI_OF_DAY",
            IntegerTimeElement.MILLI_OF_DAY,
            0,
            86399999,
            'A');

    /**
     * <p>Element mit der Tageszeit in Mikrosekunden im
     * Bereich {@code 0-86400000000}. </p>
     *
     * <p>Im Kontext von {@code PlainTime} mit vollen Mikrosekunden ist das
     * Maximum {@code 86400000000} (entsprechend der Uhrzeit T24:00), in jedem
     * anderen Kontext ist das Maximum der Wert {@code 86399999999}.
     * UTC-Schaltsekunden werden nicht mitgez&auml;hlt. </p>
     */
    public static final
    ProportionalElement<Long, PlainTime> MICRO_OF_DAY =
        LongElement.create("MICRO_OF_DAY", 0L, 86399999999L);

    /**
     * <p>Element mit der Tageszeit in Nanosekunden im
     * Bereich {@code 0-86400000000000}. </p>
     *
     * <p>Im Kontext von {@code PlainTime} ist das Maximum stets
     * {@code 86400000000000} (entsprechend der Uhrzeit T24:00), in jedem
     * anderen Kontext ist das Maximum der Wert {@code 86399999999999}.
     * UTC-Schaltsekunden werden nicht mitgez&auml;hlt. </p>
     *
     * <p>Beispiel: </p>
     *
     * <pre>
     *  import static net.time4j.ClockUnit.HOURS;
     *  import static net.time4j.PlainTime.NANO_OF_DAY;
     *
     *  PlainTime time =
     *      PlainTime.midnightAtStartOfDay().plus(6, HOURS); // T06:00
     *  System.out.println(
     *      time.get(NANO_OF_DAY.ratio())
     *          .multiply(BigDecimal.ofHour(100)).stripTrailingZeros()
     *      + "% of day are over.");
     *  // Ausgabe: 25% of day are over.
     * </pre>
     */
    public static final
    ProportionalElement<Long, PlainTime> NANO_OF_DAY =
        LongElement.create("NANO_OF_DAY", 0L, 86399999999999L);

    /**
     * <p>Dezimal-Stunde im Wertebereich {@code 0.0} inklusive bis
     * {@code 24.0} inklusive. </p>
     *
     * <p>Dieses Element definiert keine Basiseinheit und wird exklusiv
     * nur von der Klasse {@code PlainTime} unterst&uuml;tzt. </p>
     */
    public static final ChronoElement<BigDecimal> DECIMAL_HOUR =
        new DecimalTimeElement("DECIMAL_HOUR", DECIMAL_24_0);

    /**
     * <p>Dezimal-Minute im Wertebereich {@code 0.0} inklusive bis
     * {@code 60.0} exklusive. </p>
     *
     * <p>Dieses Element definiert keine Basiseinheit und wird exklusiv
     * nur von der Klasse {@code PlainTime} unterst&uuml;tzt. </p>
     */
    public static final ChronoElement<BigDecimal> DECIMAL_MINUTE =
        new DecimalTimeElement("DECIMAL_MINUTE", DECIMAL_59_9);

    /**
     * <p>Dezimal-Sekunde im Wertebereich {@code 0.0} inklusive bis
     * {@code 60.0} exklusive. </p>
     *
     * <p>Dieses Element definiert keine Basiseinheit und wird exklusiv
     * nur von der Klasse {@code PlainTime} unterst&uuml;tzt. </p>
     */
    public static final ChronoElement<BigDecimal> DECIMAL_SECOND =
        new DecimalTimeElement("DECIMAL_SECOND", DECIMAL_59_9);

    /**
     * <p>Definiert die Genauigkeit als das kleinste von {@code 0} verschiedene
     * Uhrzeitelement und schneidet bei Bedarf zu genaue Zeitanteile ab. </p>
     *
     * <p>Beim Setzen der Genauigkeit ist zu beachten, da&szlig; eine
     * h&ouml;here Genauigkeit wirkungslos ist. Das Setzen einer kleineren
     * Genauigkeit hingegen schneidet Daten ab. Beispiele: </p>
     *
     * <pre>
     *  // Lesen der Genauigkeit ------------------------------------
     *  PlainTime time = new PlainTime(12, 26, 52, 987654000);
     *  System.out.println(time.get(PRECISION)); // Ausgabe: MICROS
     *
     *  // Setzen der Genauigkeit -----------------------------------
     *  PlainTime time = new PlainTime(12, 26, 52, 987654000);
     *  System.out.println(time.with(PRECISION, ClockUnit.MILLIS));
     *  // Ausgabe: T12:26:52,987
     * </pre>
     *
     * <p>Dieses Element definiert keine Basiseinheit. </p>
     */
    public static final ChronoElement<ClockUnit> PRECISION =
        PrecisionElement.PRECISION;

    // Dient der Serialisierungsunterstützung.
    private static final Map<String, Object> ELEMENTS;

    static {
        Map<String, Object> constants = new HashMap<String, Object>();
        fill(constants, WALL_TIME);
        fill(constants, AM_PM_OF_DAY);
        fill(constants, CLOCK_HOUR_OF_AMPM);
        fill(constants, CLOCK_HOUR_OF_DAY);
        fill(constants, DIGITAL_HOUR_OF_AMPM);
        fill(constants, DIGITAL_HOUR_OF_DAY);
        fill(constants, ISO_HOUR);
        fill(constants, MINUTE_OF_HOUR);
        fill(constants, MINUTE_OF_DAY);
        fill(constants, SECOND_OF_MINUTE);
        fill(constants, SECOND_OF_DAY);
        fill(constants, MILLI_OF_SECOND);
        fill(constants, MICRO_OF_SECOND);
        fill(constants, NANO_OF_SECOND);
        fill(constants, MILLI_OF_DAY);
        fill(constants, MICRO_OF_DAY);
        fill(constants, NANO_OF_DAY);
        fill(constants, DECIMAL_HOUR);
        fill(constants, DECIMAL_MINUTE);
        fill(constants, DECIMAL_SECOND);
        fill(constants, PRECISION);
        ELEMENTS = Collections.unmodifiableMap(constants);
    }

    private static final ElementRule<PlainTime, BigDecimal> H_DECIMAL_RULE =
        new BigDecimalElementRule(DECIMAL_HOUR, DECIMAL_24_0);
    private static final ElementRule<PlainTime, BigDecimal> M_DECIMAL_RULE =
        new BigDecimalElementRule(DECIMAL_MINUTE, DECIMAL_59_9);
    private static final ElementRule<PlainTime, BigDecimal> S_DECIMAL_RULE =
        new BigDecimalElementRule(DECIMAL_SECOND, DECIMAL_59_9);

    private static final TimeAxis<IsoTimeUnit, PlainTime> ENGINE;

    static {
        TimeAxis.Builder<IsoTimeUnit, PlainTime> builder =
            TimeAxis.Builder.setUp(
                IsoTimeUnit.class,
                PlainTime.class,
                new Merger(),
                PlainTime.MIN,
                PlainTime.MAX)
            .appendElement(
                WALL_TIME,
                new TimeRule())
            .appendElement(
                AM_PM_OF_DAY,
                new MeridiemRule())
            .appendElement(
                CLOCK_HOUR_OF_AMPM,
                new IntegerElementRule(CLOCK_HOUR_OF_AMPM, 1, 12),
                ClockUnit.HOURS)
            .appendElement(
                CLOCK_HOUR_OF_DAY,
                new IntegerElementRule(CLOCK_HOUR_OF_DAY, 1, 24),
                ClockUnit.HOURS)
            .appendElement(
                DIGITAL_HOUR_OF_AMPM,
                new IntegerElementRule(DIGITAL_HOUR_OF_AMPM, 0, 11),
                ClockUnit.HOURS)
            .appendElement(
                DIGITAL_HOUR_OF_DAY,
                new IntegerElementRule(DIGITAL_HOUR_OF_DAY, 0, 23),
                ClockUnit.HOURS)
            .appendElement(
                ISO_HOUR,
                new IntegerElementRule(ISO_HOUR, 0, 24),
                ClockUnit.HOURS)
            .appendElement(
                MINUTE_OF_HOUR,
                new IntegerElementRule(MINUTE_OF_HOUR, 0, 59),
                ClockUnit.MINUTES)
            .appendElement(
                MINUTE_OF_DAY,
                new IntegerElementRule(MINUTE_OF_DAY, 0, 1440),
                ClockUnit.MINUTES)
            .appendElement(
                SECOND_OF_MINUTE,
                new IntegerElementRule(SECOND_OF_MINUTE, 0, 59),
                ClockUnit.SECONDS)
            .appendElement(
                SECOND_OF_DAY,
                new IntegerElementRule(SECOND_OF_DAY, 0, 86400),
                ClockUnit.SECONDS)
            .appendElement(
                MILLI_OF_SECOND,
                new IntegerElementRule(MILLI_OF_SECOND, 0, 999),
                ClockUnit.MILLIS)
            .appendElement(
                MICRO_OF_SECOND,
                new IntegerElementRule(MICRO_OF_SECOND, 0, 999999),
                ClockUnit.MICROS)
            .appendElement(
                NANO_OF_SECOND,
                new IntegerElementRule(NANO_OF_SECOND, 0, 999999999),
                ClockUnit.NANOS)
            .appendElement(
                MILLI_OF_DAY,
                new IntegerElementRule(MILLI_OF_DAY, 0, 86400000),
                ClockUnit.MILLIS)
            .appendElement(
                MICRO_OF_DAY,
                new LongElementRule(MICRO_OF_DAY, 0, 86400000000L),
                ClockUnit.MICROS)
            .appendElement(
                NANO_OF_DAY,
                new LongElementRule(NANO_OF_DAY, 0, 86400000000000L),
                ClockUnit.NANOS)
            .appendElement(
                DECIMAL_HOUR,
                H_DECIMAL_RULE)
            .appendElement(
                DECIMAL_MINUTE,
                M_DECIMAL_RULE)
            .appendElement(
                DECIMAL_SECOND,
                S_DECIMAL_RULE)
            .appendElement(
                PRECISION,
                new PrecisionRule());
        registerUnits(builder);
        ENGINE = builder.build();
    }

    //~ Instanzvariablen --------------------------------------------------

    private transient final byte hour;
    private transient final byte minute;
    private transient final byte second;
    private transient final int nano;

    //~ Konstruktoren -----------------------------------------------------

    private PlainTime(
        int hour,
        int minute,
        int second,
        int nanosecond
    ) {
        super();

        checkHour(hour);
        checkMinute(minute);
        checkSecond(second);
        checkNano(nanosecond);

        if (
            (hour == 24)
            && ((minute | second | nanosecond) != 0)
        ) {
            throw new IllegalArgumentException("T24:00:00 exceeded.");
        }

        this.hour = (byte) hour;
        this.minute = (byte) minute;
        this.second = (byte) second;
        this.nano = nanosecond;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public int getHour() {

        return this.hour;

    }

    @Override
    public int getMinute() {

        return this.minute;

    }

    @Override
    public int getSecond() {

        return this.second;

    }

    /**
     * <p>Liefert Mitternacht zu Beginn des Tages. </p>
     *
     * @return  midnight at the start of day T00:00
     * @see     #midnightAtEndOfDay()
     */
    public static PlainTime midnightAtStartOfDay() {

        return PlainTime.MIN;

    }

    /**
     * <p>Liefert Mitternacht zum Ende des Tages. </p>
     *
     * @return  midnight at the end of day T24:00
     * @see     #midnightAtStartOfDay()
     */
    public static PlainTime midnightAtEndOfDay() {

        return PlainTime.MAX;

    }

    /**
     * <p>Erzeugt eine neue Uhrzeit als volle Stunde. </p>
     *
     * @param   hour    iso-hour of day in the range {@code 0-24}
     * @return  cached full hour
     * @throws  IllegalArgumentException if given hour is out of range
     */
    public static PlainTime of(int hour) {

        checkHour(hour);
        return HOURS[hour];

    }

    /**
     * <p>Erzeugt eine neue Uhrzeit mit Stunde und Minute. </p>
     *
     * @param   hour    hour of day in the range {@code 0-23} or
     *                  {@code 24} if the given minute equals to {@code 0}
     * @param   minute  minute in the range {@code 0-59}
     * @return  new or cached wall time
     * @throws  IllegalArgumentException if any argument is out of range
     */
    public static PlainTime of(
        int hour,
        int minute
    ) {

        if (minute == 0) {
            return PlainTime.of(hour);
        }

        return new PlainTime(hour, minute, 0, 0);

    }

    /**
     * <p>Erzeugt eine neue Uhrzeit mit Stunde, Minute und Sekunde. </p>
     *
     * @param   hour        hour in the range {@code 0-23} or {@code 24}
     *                      if the other arguments are equal to {@code 0}
     * @param   minute      minute in the range {@code 0-59}
     * @param   second      second in the range {@code 0-59}
     * @return  new or cached wall time
     * @throws  IllegalArgumentException if any argument is out of range
     */
    public static PlainTime of(
        int hour,
        int minute,
        int second
    ) {

        if ((minute | second) == 0) {
            return PlainTime.of(hour);
        }

        return new PlainTime(hour, minute, second, 0);

    }

    /**
     * <p>Erzeugt eine neue Uhrzeit mit Stunde, Minute, Sekunde und
     * Nanosekunde. </p>
     *
     * @param   hour        hour in the range {@code 0-23} or {@code 24}
     *                      if the other argumenta equal to {@code 0}
     * @param   minute      minute in the range {@code 0-59}
     * @param   second      second in the range {@code 0-59}
     * @param   nanosecond  nanosecond in the range {@code 0-999,999,999}
     * @return  new or cached wall time
     * @throws  IllegalArgumentException if any argument is out of range
     * @see     #of(int)
     * @see     #of(int, int)
     * @see     #of(int, int, int)
     * @see     #NANO_OF_SECOND
     */
    public static PlainTime of(
        int hour,
        int minute,
        int second,
        int nanosecond
    ) {

        if ((minute | second | nanosecond) == 0) {
            return PlainTime.of(hour);
        }

        return new PlainTime(hour, minute, second, nanosecond);

    }

    /**
     * <p>Erzeugt eine neue Uhrzeit auf Basis der angegebenen
     * Dezimalstunde. </p>
     *
     * @param   decimal    decimal hour of day in the range {@code [0.0-24.0]}
     * @return  new or cached wall time
     * @throws  IllegalArgumentException if the argument is out of range
     * @see     #DECIMAL_HOUR
     */
    public static PlainTime of(BigDecimal decimal) {

        return H_DECIMAL_RULE.withValue(null, decimal, false);

    }

    /**
     * <p>Allgemeine Konversionsmethode. </p>
     *
     * @param   time    ISO-time
     * @return  PlainTime
     */
    public static PlainTime from(WallTime time) {

        if (time instanceof PlainTime) {
            return (PlainTime) time;
        } else {
            return PlainTime.of(
                time.getHour(), time.getMinute(), time.getSecond());
        }

    }

    /**
     * <p>Rollt die angegebene Dauer mit Betrag und Einheit zu dieser Uhrzeit
     * auf und z&auml;hlt dabei auch tageweise &Uuml;berl&auml;ufe. </p>
     *
     * @param   amount      amount to be added (maybe negative)
     * @param   unit        time unit
     * @return  result of rolling including possible day overflow
     * @see     #plus(long, Object) plus(long, Unit)
     */
    public DayCycles roll(
        long amount,
        ClockUnit unit
    ) {

        return ClockUnitRule.addToWithOverflow(this, amount, unit);

    }

    /**
     * <p>Erzeugt ein neues Format-Objekt mit Hilfe des angegebenen Musters
     * in der Standard-Sprach- und L&auml;ndereinstellung. </p>
     *
     * <p>Das Format-Objekt kann an andere Sprachen angepasst werden. </p>
     *
     * @param   formatPattern   format definition as pattern
     * @param   patternType     pattern dialect
     * @return  format object for formatting {@code PlainTime}-objects
     *          using system locale
     * @throws  IllegalArgumentException if resolving of pattern fails
     * @see     PatternType
     * @see     ChronoFormatter#with(Locale)
     */
    public static ChronoFormatter<PlainTime> localFormatter(
        String formatPattern,
        ChronoPattern patternType
    ) {

        return ChronoFormatter
            .setUp(PlainTime.class, Locale.getDefault())
            .addPattern(formatPattern, patternType)
            .build();

    }

    /**
     * <p>Erzeugt ein neues Format-Objekt mit Hilfe des angegebenen Stils
     * in der Standard-Sprach- und L&auml;ndereinstellung. </p>
     *
     * <p>Das Format-Objekt kann an andere Sprachen angepasst werden. </p>
     *
     * @param   mode        formatting style
     * @return  format object for formatting {@code PlainTime}-objects
     *          using system locale
     * @throws  IllegalStateException if format pattern cannot be retrieved
     * @see     ChronoFormatter#with(Locale)
     */
    public static ChronoFormatter<PlainTime> localFormatter(DisplayMode mode) {

        int style = PatternType.getFormatStyle(mode);
        DateFormat df = DateFormat.getTimeInstance(style);
        String pattern = PatternType.getFormatPattern(df);

        return ChronoFormatter
            .setUp(PlainTime.class, Locale.getDefault())
            .addPattern(pattern, PatternType.SIMPLE_DATE_FORMAT)
            .build();

    }

    /**
     * <p>Erzeugt ein neues Format-Objekt mit Hilfe des angegebenen Musters
     * in der angegebenen Sprach- und L&auml;ndereinstellung. </p>
     *
     * <p>Das Format-Objekt kann an andere Sprachen angepasst werden. </p>
     *
     * @param   formatPattern   format definition as pattern
     * @param   patternType     pattern dialect
     * @param   locale          locale setting
     * @return  format object for formatting {@code PlainTime}-objects
     *          using given locale
     * @throws  IllegalArgumentException if resolving of pattern fails
     * @see     PatternType
     * @see     #localFormatter(String,ChronoPattern)
     */
    public static ChronoFormatter<PlainTime> formatter(
        String formatPattern,
        ChronoPattern patternType,
        Locale locale
    ) {

        return ChronoFormatter
            .setUp(PlainTime.class, locale)
            .addPattern(formatPattern, patternType)
            .build();

    }

    /**
     * <p>Erzeugt ein neues Format-Objekt mit Hilfe des angegebenen Stils
     * und in der angegebenen Sprach- und L&auml;ndereinstellung. </p>
     *
     * <p>Das Format-Objekt kann an andere Sprachen angepasst werden. </p>
     *
     * @param   mode        formatting style
     * @param   locale      locale setting
     * @return  format object for formatting {@code PlainTime}-objects
     *          using given locale
     * @throws  IllegalStateException if format pattern cannot be retrieved
     * @see     #localFormatter(DisplayMode)
     */
    public static ChronoFormatter<PlainTime> formatter(
        DisplayMode mode,
        Locale locale
    ) {

        int style = PatternType.getFormatStyle(mode);
        DateFormat df = DateFormat.getTimeInstance(style, locale);
        String pattern = PatternType.getFormatPattern(df);

        return ChronoFormatter
            .setUp(PlainTime.class, locale)
            .addPattern(pattern, PatternType.SIMPLE_DATE_FORMAT)
            .build();

    }

    /**
     * <p>Vergleicht alle Zeitzustandsattribute, n&auml;mlich Stunde, Minute,
     * Sekunde und Nanosekunde. </p>
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof PlainTime) {
            PlainTime that = (PlainTime) obj;
            return (
                (this.hour == that.hour)
                && (this.minute == that.minute)
                && (this.second == that.second)
                && (this.nano == that.nano)
            );
        } else {
            return false;
        }

    }

    /**
     * <p>Basiert auf allen Zeitzustandsattributen. </p>
     */
    @Override
    public int hashCode() {

        return (
            this.hour
            + 60 * this.minute
            + 3600 * this.second
            + 37 * this.nano);

    }

    @Override
    public boolean isBefore(PlainTime time) {

        return (this.compareTo(time) < 0);

    }

    @Override
    public boolean isAfter(PlainTime time) {

        return (this.compareTo(time) > 0);

    }

    @Override
    public boolean isSimultaneous(PlainTime time) {

        return (this.compareTo(time) == 0);

    }

    /**
     * <p>Liegt Mitternacht vor (am Anfang oder am Ende eines Tages)? </p>
     *
     * @return  boolean
     */
    public boolean isMidnight() {

        return (this.isFullHour() && ((this.hour % 24) == 0));

    }

    /**
     * <p>Definiert eine nat&uuml;rliche Ordnung, die auf der zeitlichen
     * Position basiert. </p>
     *
     * <p>Der Vergleich ist konsistent mit {@code equals()}. </p>
     *
     * @see     #isBefore(PlainTime)
     * @see     #isAfter(PlainTime)
     */
    @Override
    public int compareTo(PlainTime time) {

        int delta = this.hour - time.hour;

        if (delta == 0) {
            delta = this.minute - time.minute;
            if (delta == 0) {
                delta = this.second - time.second;
                if (delta == 0) {
                    delta = this.nano - time.nano;
                }
            }
        }

        return ((delta < 0) ? -1 : ((delta == 0) ? 0 : 1));

    }

    /**
     * <p>Liefert je nach Genauigkeit einen String in einem der folgenden
     * Formate (CLDR-Syntax): </p>
     *
     * <ul>
     *  <li>'T'HH</li>
     *  <li>'T'HH:mm</li>
     *  <li>'T'HH:mm:ss</li>
     *  <li>'T'HH:mm:ss,SSS</li>
     *  <li>'T'HH:mm:ss,SSSSSS</li>
     *  <li>'T'HH:mm:ss,SSSSSSSSS</li>
     * </ul>
     *
     * <p>Vor dem Sekundenbruchteil erscheint im Standardfall das Komma, es sei
     * denn, die System-Property &quot;net.time4j.format.iso.decimal.dot&quot;
     * wurde auf &quot;true&quot; gesetzt. </p>
     *
     * @return  canonical ISO-8601-formatted string
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(19);

        sb.append('T');
        append2Digits(this.hour, sb);

        if ((this.minute | this.second | this.nano) != 0) {
            sb.append(':');
            append2Digits(this.minute, sb);

            if ((this.second | this.nano) != 0) {
                sb.append(':');
                append2Digits(this.second, sb);

                if (this.nano != 0) {
                    sb.append(ISO_DECIMAL_SEPARATOR);
                    String num = Integer.toString(this.nano);
                    int len;
                    if ((this.nano % MIO) == 0) {
                        len = 3;
                    } else if ((this.nano % KILO) == 0) {
                        len = 6;
                    } else {
                        len = 9;
                    }
                    for (int i = num.length(); i < 9; i++) {
                        sb.append('0');
                    }
                    for (
                        int i = 0, n = Math.min(len, num.length());
                        i < n;
                        i++
                    ) {
                        sb.append(num.charAt(i));
                    }
                }
            }
        }

        return sb.toString();

    }

    /**
     * <p>Liefert die zugeh&ouml;rige Zeitachse, die alle notwendigen
     * chronologischen Regeln enth&auml;lt. </p>
     *
     * @return  chronological system as time axis (never {@code null})
     */
    public static TimeAxis<IsoTimeUnit, PlainTime> axis() {

        return ENGINE;

    }

    @Override
    protected TimeAxis<IsoTimeUnit, PlainTime> getChronology() {

        return ENGINE;

    }

    @Override
    protected PlainTime getContext() {

        return this;

    }

    /**
     * <p>Liefert die Nanosekunde. </p>
     *
     * @return  nanosecond in the range {@code 0 - 999,999,999}
     */
    int getNanosecond() {

        return this.nano;

    }

    /**
     * <p>Erzeugt eine neue Uhrzeit passend zur angegebenen absoluten Zeit. </p>
     *
     * @param   ut      unix time in seconds
     * @param   offset  shift of local time relative to UTC
     * @return  new or cached wall time
     */
    static PlainTime from(
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

        int secondsOfDay = MathUtils.floorModulo(localSeconds, 86400);
        int second = secondsOfDay % 60;
        int minutesOfDay = secondsOfDay / 60;
        int minute = minutesOfDay % 60;
        int hour = minutesOfDay / 60;

        return PlainTime.of(
            hour,
            minute,
            second,
            localNanos
        );

    }

    /**
     * <p>Dient der Serialisierungsunterst&uuml;tzung. </p>
     *
     * @param   elementName     name of element
     * @return  found element or {@code null}
     */
    // optional
    static Object lookupElement(String elementName) {

        return ELEMENTS.get(elementName);

    }

    /**
     * <p>Wird von SQL-TIMESTAMP gebraucht. </p>
     *
     * @param   millisOfDay     milliseconds of day
     * @return  new instance
     */
    static PlainTime createFromMillis(int millisOfDay) {

        return PlainTime.createFromMillis(millisOfDay, 0);

    }

    /**
     * <p>Wird von der {@code ratio()}-Function des angegebenenElements
     * aufgerufen. </p>
     *
     * @param   context     walltime context
     * @param   element     reference time element
     * @return  {@code true} if element maximum is reduced else {@code false}
     */
    boolean hasReducedRange(ChronoElement<?> element) {

        return (
            ((element == MILLI_OF_DAY) && ((this.nano % MIO) != 0))
            || ((element == ISO_HOUR) && !this.isFullHour())
            || ((element == MINUTE_OF_DAY) && !this.isFullMinute())
            || ((element == SECOND_OF_DAY) && (this.nano != 0))
            || ((element == MICRO_OF_DAY) && ((this.nano % KILO) != 0))
        );

    }

    private static void fill(
        Map<String, Object> map,
        ChronoElement<?> element
    ) {

        map.put(element.name(), element);

    }

    private static void append2Digits(
        int element,
        StringBuilder sb
    ) {

        if (element < 10) {
            sb.append('0');
        }

        sb.append(element);

    }

    private static void checkHour(long hour) {

        if (hour < 0 || hour > 24) {
            throw new IllegalArgumentException(
                "HOUR_OF_DAY out of range: " + hour);
        }

    }

    private static void checkMinute(long minute) {

        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException(
                "MINUTE_OF_HOUR out of range: " + minute);
        }

    }

    private static void checkSecond(long second) {

        if (second < 0 || second > 59) {
            throw new IllegalArgumentException(
                "SECOND_OF_MINUTE out of range: " + second);
        }

    }

    private static void checkNano(int nano) {

        if (nano < 0 || nano >= MRD) {
            throw new IllegalArgumentException(
                "NANO_OF_SECOND out of range: " + nano);
        }

    }

    private static PlainTime createFromMillis(
        int millisOfDay,
        int micros
    ) {

        int nanosecond = (millisOfDay % KILO) * MIO + micros;
        int secondsOfDay = millisOfDay / KILO;
        int second = secondsOfDay % 60;
        int minutesOfDay = secondsOfDay / 60;
        int minute = minutesOfDay % 60;
        int hour = minutesOfDay / 60;

        return PlainTime.of(hour, minute, second, nanosecond);

    }

    private static PlainTime createFromMicros(
        long microsOfDay,
        int nanos
    ) {

        int nanosecond = ((int) (microsOfDay % MIO)) * KILO + nanos;
        int secondsOfDay = (int) (microsOfDay / MIO);
        int second = secondsOfDay % 60;
        int minutesOfDay = secondsOfDay / 60;
        int minute = minutesOfDay % 60;
        int hour = minutesOfDay / 60;

        return PlainTime.of(hour, minute, second, nanosecond);

    }

    private static PlainTime createFromNanos(long nanosOfDay) {

        int nanosecond = (int) (nanosOfDay % MRD);
        int secondsOfDay = (int) (nanosOfDay / MRD);
        int second = secondsOfDay % 60;
        int minutesOfDay = secondsOfDay / 60;
        int minute = minutesOfDay % 60;
        int hour = minutesOfDay / 60;

        return PlainTime.of(hour, minute, second, nanosecond);

    }

    private long getNanoOfDay() {

        return (
            this.nano
            + this.second * 1L * MRD
            + this.minute * 60L * MRD
            + this.hour * 3600L * MRD
        );

    }

    private boolean isFullHour() {

        return ((this.minute | this.second | this.nano) == 0);

    }

    private boolean isFullMinute() {

        return ((this.second | this.nano) == 0);

    }

    private static
    void registerUnits(TimeAxis.Builder<IsoTimeUnit, PlainTime> builder) {

        Set<ClockUnit> convertibles = EnumSet.allOf(ClockUnit.class);

        for (ClockUnit unit : ClockUnit.values()) {
            builder.appendUnit(
                unit,
                new ClockUnitRule(unit),
                unit.getLength(),
                convertibles);
        }

    }

    private static long floorMod(
        long value,
        long divisor
    ) {

        long num =
            (value >= 0)
            ? (value / divisor)
            : (((value + 1) / divisor) - 1);
        return (value - divisor * num);

    }

    private static long floorDiv(
        long value,
        long divisor
    ) {

        if (value >= 0) {
            return (value / divisor);
        } else {
            return ((value + 1) / divisor) - 1;
        }

    }

    /**
     * @serialData  Uses <a href="../../serialized-form.html#net.time4j.SPX">
     *              a dedicated serialization form</a> as proxy. The layout
     *              is bit-compressed. The first byte contains within the
     *              four most significant bits the type id {@code 2}. Then
     *              the data bytes for hour, minute, second and nanosecond
     *              follow (in last case int instead of byte). Is the precision
     *              limited to seconds, minutes or hours then the last non-zero
     *              byte will be bit-inverted by the operator (~), and the
     *              following bytes will be left out. The hour byte however
     *              is always written.
     *
     * Schematic algorithm:
     *
     * <pre>
     *      out.writeByte(2 << 4);
     *
     *      if (time.nano == 0) {
     *          if (time.second == 0) {
     *              if (time.minute == 0) {
     *                  out.writeByte(~time.hour);
     *              } else {
     *                  out.writeByte(time.hour);
     *                  out.writeByte(~time.minute);
     *              }
     *          } else {
     *              out.writeByte(time.hour);
     *              out.writeByte(time.minute);
     *              out.writeByte(~time.second);
     *          }
     *      } else {
     *          out.writeByte(time.hour);
     *          out.writeByte(time.minute);
     *          out.writeByte(time.second);
     *          out.writeInt(time.nano);
     *      }
     * </pre>
     */
    private Object writeReplace() throws ObjectStreamException {

        return new SPX(this, SPX.TIME_TYPE);

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

    private static class ClockUnitRule
        implements UnitRule<PlainTime> {

        //~ Instanzvariablen ----------------------------------------------

        private final ClockUnit unit;

        //~ Konstruktoren -------------------------------------------------

        private ClockUnitRule(ClockUnit unit) {
            super();

            this.unit = unit;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public PlainTime addTo(
            PlainTime context,
            long amount
        ) {

            if (amount == 0) {
                return context;
            }

            return doAdd(PlainTime.class, this.unit, context, amount);

        }

        @Override
        public long between(
            PlainTime start,
            PlainTime end
        ) {

            long delta = (end.getNanoOfDay() - start.getNanoOfDay());
            long factor;

            switch (this.unit) {
                case HOURS:
                    factor = MRD * 3600L;
                    break;
                case MINUTES:
                    factor = MRD * 60L;
                    break;
                case SECONDS:
                    factor = MRD;
                    break;
                case MILLIS:
                    factor = MIO;
                    break;
                case MICROS:
                    factor = KILO;
                    break;
                case NANOS:
                    factor = 1;
                    break;
                default:
                    throw new UnsupportedOperationException(this.unit.name());
            }

            return delta / factor;

        }

        private static DayCycles addToWithOverflow(
            PlainTime context,
            long amount,
            ClockUnit unit
        ) {

            if (amount == 0) {
                return new DayCycles(0, context);
            }

            return doAdd(DayCycles.class, unit, context, amount);

        }

        private static <R> R doAdd(
            Class<R> returnType,
            ClockUnit unit,
            PlainTime context,
            long amount
        ) {

            long hours;
            long minutes;
            long seconds;
            long nanos;

            int minute = context.minute;
            int second = context.second;
            int fraction = context.nano;

            switch (unit) {
                case HOURS:
                    hours = MathUtils.safeAdd(context.hour, amount);
                    break;
                case MINUTES:
                    minutes = MathUtils.safeAdd(context.minute, amount);
                    hours =
                        MathUtils.safeAdd(
                            context.hour,
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
                            context.hour,
                            MathUtils.floorDivide(minutes, 60));
                    minute = MathUtils.floorModulo(minutes, 60);
                    second = MathUtils.floorModulo(seconds, 60);
                    break;
                case MILLIS:
                    return doAdd(
                        returnType,
                        ClockUnit.NANOS,
                        context,
                        MathUtils.safeMultiply(amount, MIO));
                case MICROS:
                    return doAdd(
                        returnType,
                        ClockUnit.NANOS,
                        context,
                        MathUtils.safeMultiply(amount, KILO));
                case NANOS:
                    nanos =
                        MathUtils.safeAdd(context.nano, amount);
                    seconds =
                        MathUtils.safeAdd(
                            context.second,
                            MathUtils.floorDivide(nanos, MRD));
                    minutes =
                        MathUtils.safeAdd(
                            context.minute,
                            MathUtils.floorDivide(seconds, 60));
                    hours =
                        MathUtils.safeAdd(
                            context.hour,
                            MathUtils.floorDivide(minutes, 60));
                    minute = MathUtils.floorModulo(minutes, 60);
                    second = MathUtils.floorModulo(seconds, 60);
                    fraction = MathUtils.floorModulo(nanos, MRD);
                    break;
                default:
                    throw new UnsupportedOperationException(unit.name());
            }

            int hour = MathUtils.floorModulo(hours, 24);
            PlainTime ret;

            if ((hour | minute | second | fraction) == 0) {
                ret = ((amount > 0) ? PlainTime.MAX : PlainTime.MIN);
            } else {
                ret = PlainTime.of(hour, minute, second, fraction);
            }

            if (returnType == PlainTime.class) {
                return returnType.cast(ret);
            } else {
                long cycles = MathUtils.floorDivide(hours, 24);
                if (context.hour == 24) {
                    cycles--;
                }
                return returnType.cast(new DayCycles(cycles, ret));
            }

        }

    }

    private static class TimeRule
        implements ElementRule<PlainTime, PlainTime> {

        //~ Methoden ------------------------------------------------------

        @Override
        public PlainTime getValue(PlainTime context) {

            return context;

        }

        @Override
        public PlainTime withValue(
            PlainTime context,
            PlainTime value,
            boolean lenient
        ) {

            if (value == null) {
                throw new NullPointerException("Missing time value.");
            }

            return value;

        }

        @Override
        public boolean isValid(
            PlainTime context,
            PlainTime value
        ) {

            return (value != null);

        }

        @Override
        public PlainTime getMinimum(PlainTime context) {

            return PlainTime.MIN;

        }

        @Override
        public PlainTime getMaximum(PlainTime context) {

            return PlainTime.MAX;

        }

        @Override
        public ChronoElement<?> getChildAtFloor(PlainTime context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(PlainTime context) {

            return null;

        }

    }

    private static class PrecisionRule
        implements ElementRule<PlainTime, ClockUnit> {

        //~ Methoden ------------------------------------------------------

        @Override
        public ClockUnit getValue(PlainTime context) {

            if (context.nano != 0) {
                if ((context.nano % MIO) == 0) {
                    return ClockUnit.MILLIS;
                } else if ((context.nano % KILO) == 0) {
                    return ClockUnit.MICROS;
                } else {
                    return ClockUnit.NANOS;
                }
            } else if (context.second != 0) {
                return ClockUnit.SECONDS;
            } else if (context.minute != 0) {
                return ClockUnit.MINUTES;
            } else {
                return ClockUnit.HOURS;
            }

        }

        @Override
        public PlainTime withValue(
            PlainTime context,
            ClockUnit value,
            boolean lenient
        ) {

            int ordinal = value.ordinal();

            if (ordinal >= this.getValue(context).ordinal()) {
                return context; // Kein Abschneiden notwendig!
            }

            switch (value) {
                case HOURS:
                    return PlainTime.of(context.hour);
                case MINUTES:
                    return PlainTime.of(context.hour, context.minute);
                case SECONDS:
                    return PlainTime.of(
                        context.hour, context.minute, context.second);
                case MILLIS:
                    return PlainTime.of(
                        context.hour,
                        context.minute,
                        context.second,
                        (context.nano / MIO) * MIO);
                case MICROS:
                    return PlainTime.of(
                        context.hour,
                        context.minute,
                        context.second,
                        (context.nano / KILO) * KILO);
                case NANOS:
                    return context; // Programm sollte nie hierher kommen!
                default:
                    throw new UnsupportedOperationException(value.name());
            }

        }

        @Override
        public boolean isValid(
            PlainTime context,
            ClockUnit value
        ) {

            return (value != null);

        }

        @Override
        public ClockUnit getMinimum(PlainTime context) {

            return ClockUnit.HOURS;

        }

        @Override
        public ClockUnit getMaximum(PlainTime context) {

            return ClockUnit.NANOS;

        }

        @Override
        public ChronoElement<?> getChildAtFloor(PlainTime context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(PlainTime context) {

            return null;

        }

    }

    private static class MeridiemRule
        implements ElementRule<PlainTime, Meridiem> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Meridiem getValue(PlainTime context) {

            return Meridiem.ofHour(context.hour);

        }

        @Override
        public PlainTime withValue(
            PlainTime context,
            Meridiem value,
            boolean lenient
        ) {

            int h = ((context.hour == 24) ? 0 : context.hour);

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

            return PlainTime.of(
                h,
                context.minute,
                context.second,
                context.nano
            );

        }

        @Override
        public boolean isValid(
            PlainTime context,
            Meridiem value
        ) {

            return (value != null);

        }

        @Override
        public Meridiem getMinimum(PlainTime context) {

            return Meridiem.AM;

        }

        @Override
        public Meridiem getMaximum(PlainTime context) {

            return Meridiem.PM;

        }

        @Override
        public ChronoElement<?> getChildAtFloor(PlainTime context) {

            return DIGITAL_HOUR_OF_AMPM;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(PlainTime context) {

            return DIGITAL_HOUR_OF_AMPM;

        }

    }

    private static class IntegerElementRule
        implements ElementRule<PlainTime, Integer> {

        //~ Instanzvariablen ----------------------------------------------

        private final ChronoElement<Integer> element;
        private final int index;
        private final int min;
        private final int max;

        //~ Konstruktoren -------------------------------------------------

        IntegerElementRule(
            ChronoElement<Integer> element,
            int min,
            int max
        ) {
            super();

            this.element = element;

            if (element instanceof IntegerTimeElement) {
                this.index = ((IntegerTimeElement) element).getIndex();
            } else {
                this.index = -1;
            }

            this.min = min;
            this.max = max;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Integer getValue(PlainTime context) {

            int ret;

            switch (this.index) {
                case IntegerTimeElement.CLOCK_HOUR_OF_AMPM:
                    ret = (context.hour % 12);
                    if (ret == 0) {
                        ret = 12;
                    }
                    break;
                case IntegerTimeElement.CLOCK_HOUR_OF_DAY:
                    ret = context.hour % 24;
                    if (ret == 0) {
                        ret = 24;
                    }
                    break;
                case IntegerTimeElement.DIGITAL_HOUR_OF_AMPM:
                    ret = (context.hour % 12);
                    break;
                case IntegerTimeElement.DIGITAL_HOUR_OF_DAY:
                    ret = context.hour % 24;
                    break;
                case IntegerTimeElement.ISO_HOUR:
                    ret = context.hour;
                    break;
                case IntegerTimeElement.MINUTE_OF_HOUR:
                    ret = context.minute;
                    break;
                case IntegerTimeElement.MINUTE_OF_DAY:
                    ret = context.hour * 60 + context.minute;
                    break;
                case IntegerTimeElement.SECOND_OF_MINUTE:
                    ret = context.second;
                    break;
                case IntegerTimeElement.SECOND_OF_DAY:
                    ret =
                        context.hour * 3600
                        + context.minute * 60
                        + context.second;
                    break;
                case IntegerTimeElement.MILLI_OF_SECOND:
                    ret = (context.nano / MIO);
                    break;
                case IntegerTimeElement.MICRO_OF_SECOND:
                    ret = (context.nano / KILO);
                    break;
                case IntegerTimeElement.NANO_OF_SECOND:
                    ret = context.nano;
                    break;
                case IntegerTimeElement.MILLI_OF_DAY:
                    ret = (int) (context.getNanoOfDay() / MIO);
                    break;
                default:
                    throw new UnsupportedOperationException(
                        this.element.name());
            }

            return Integer.valueOf(ret);

        }

        @Override
        public PlainTime withValue(
            PlainTime context,
            Integer value,
            boolean lenient
        ) {

            if (value == null) {
                throw new NullPointerException("Missing element value.");
            } else if (lenient) {
                return this.withValueInLenientMode(context, value.intValue());
            } else if (!this.isValid(context, value)) {
                throw new IllegalArgumentException(
                    "Value out of range: " + value);
            }

            int h = context.hour;
            int m = context.minute;
            int s = context.second;
            int f = context.nano;
            int v = value.intValue();

            switch (this.index) {
                case IntegerTimeElement.CLOCK_HOUR_OF_AMPM:
                    v = ((v == 12) ? 0 : v);
                    h = (isAM(context) ? v : (v + 12));
                    break;
                case IntegerTimeElement.CLOCK_HOUR_OF_DAY:
                    h = ((v == 24) ? 0 : v);
                    break;
                case IntegerTimeElement.DIGITAL_HOUR_OF_AMPM:
                    h = (isAM(context) ? v : (v + 12));
                    break;
                case IntegerTimeElement.DIGITAL_HOUR_OF_DAY:
                    h = v;
                    break;
                case IntegerTimeElement.ISO_HOUR:
                    h = v;
                    break;
                case IntegerTimeElement.MINUTE_OF_HOUR:
                    m = v;
                    break;
                case IntegerTimeElement.MINUTE_OF_DAY:
                    h = v / 60;
                    m = v % 60;
                    break;
                case IntegerTimeElement.SECOND_OF_MINUTE:
                    s = v;
                    break;
                case IntegerTimeElement.SECOND_OF_DAY:
                    h = v / 3600;
                    int remainder = v % 3600;
                    m = remainder / 60;
                    s = remainder % 60;
                    break;
                case IntegerTimeElement.MILLI_OF_SECOND:
                    f = v * MIO + (context.nano % MIO);
                    break;
                case IntegerTimeElement.MICRO_OF_SECOND:
                    f = v * KILO + (context.nano % KILO);
                    break;
                case IntegerTimeElement.NANO_OF_SECOND:
                    f = v;
                    break;
                case IntegerTimeElement.MILLI_OF_DAY:
                    return PlainTime.createFromMillis(v, context.nano % MIO);
                default:
                    throw new UnsupportedOperationException(
                        this.element.name());
            }

            return PlainTime.of(h, m, s, f);

        }

        @Override
        public boolean isValid(
            PlainTime context,
            Integer value
        ) {

            if (value == null) {
                return false;
            }

            int v = value.intValue();

            if ((v < this.min) || (v > this.max)) {
                return false;
            }

            if (v == this.max) {
                switch (this.index) {
                    case IntegerTimeElement.ISO_HOUR:
                        return context.isFullHour();
                    case IntegerTimeElement.MINUTE_OF_DAY:
                        return context.isFullMinute();
                    case IntegerTimeElement.SECOND_OF_DAY:
                        return (context.nano == 0);
                    case IntegerTimeElement.MILLI_OF_DAY:
                        return ((context.nano % MIO) == 0);
                    default:
                        // no-op
                }
            }

            if (context.hour == 24) {
                switch (this.index) {
                    case IntegerTimeElement.MINUTE_OF_HOUR:
                    case IntegerTimeElement.SECOND_OF_MINUTE:
                    case IntegerTimeElement.MILLI_OF_SECOND:
                    case IntegerTimeElement.MICRO_OF_SECOND:
                    case IntegerTimeElement.NANO_OF_SECOND:
                        return (v == 0);
                    default:
                        // no-op
                }
            }

            return true;

        }

        @Override
        public Integer getMinimum(PlainTime context) {

            return Integer.valueOf(this.min);

        }

        @Override
        public Integer getMaximum(PlainTime context) {

            if (context.hour == 24) {
                switch (this.index) {
                    case IntegerTimeElement.MINUTE_OF_HOUR:
                    case IntegerTimeElement.SECOND_OF_MINUTE:
                    case IntegerTimeElement.MILLI_OF_SECOND:
                    case IntegerTimeElement.MICRO_OF_SECOND:
                    case IntegerTimeElement.NANO_OF_SECOND:
                        return Integer.valueOf(0);
                    default:
                        // no-op
                }
            }

            if (context.hasReducedRange(this.element)) {
                return Integer.valueOf(this.max - 1);
            }

            return Integer.valueOf(this.max);

        }

        @Override
        public ChronoElement<?> getChildAtFloor(PlainTime context) {

            return this.getChild(context);

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(PlainTime context) {

            return this.getChild(context);

        }

        private ChronoElement<?> getChild(PlainTime context) {

            switch (this.index) {
                case IntegerTimeElement.CLOCK_HOUR_OF_AMPM:
                case IntegerTimeElement.CLOCK_HOUR_OF_DAY:
                case IntegerTimeElement.DIGITAL_HOUR_OF_AMPM:
                case IntegerTimeElement.DIGITAL_HOUR_OF_DAY:
                case IntegerTimeElement.ISO_HOUR:
                    return MINUTE_OF_HOUR;
                case IntegerTimeElement.MINUTE_OF_HOUR:
                case IntegerTimeElement.MINUTE_OF_DAY:
                    return SECOND_OF_MINUTE;
                case IntegerTimeElement.SECOND_OF_MINUTE:
                case IntegerTimeElement.SECOND_OF_DAY:
                    return NANO_OF_SECOND;
                default:
                    return null;
            }

        }

        private PlainTime withValueInLenientMode(
            PlainTime context,
            int value
        ) {

            if (
                (this.element == ISO_HOUR)
                || (this.element == DIGITAL_HOUR_OF_DAY)
                || (this.element == DIGITAL_HOUR_OF_AMPM)
            ) {
                return context.plus(
                    MathUtils.safeSubtract(value, context.get(this.element)),
                    ClockUnit.HOURS);
            } else if (this.element == MINUTE_OF_HOUR) {
                return context.plus(
                    MathUtils.safeSubtract(value, context.minute),
                    ClockUnit.MINUTES);
            } else if (this.element == SECOND_OF_MINUTE) {
                return context.plus(
                    MathUtils.safeSubtract(value, context.second),
                    ClockUnit.SECONDS);
            } else if (this.element == MILLI_OF_SECOND) {
                return context.plus(
                    MathUtils.safeSubtract(
                        value, context.get(MILLI_OF_SECOND)),
                    ClockUnit.MILLIS);
            } else if (this.element == MICRO_OF_SECOND) {
                return context.plus(
                    MathUtils.safeSubtract(
                        value, context.get(MICRO_OF_SECOND)),
                    ClockUnit.MICROS);
            } else if (this.element == NANO_OF_SECOND) {
                return context.plus(
                    MathUtils.safeSubtract(value, context.nano),
                    ClockUnit.NANOS);
            } else if (this.element == MILLI_OF_DAY) {
                int remainder1 = MathUtils.floorModulo(value, 86400 * KILO);
                int remainder2 = context.nano % MIO;
                if ((remainder1 == 0) && (remainder2 == 0)) {
                    return (value > 0) ? PlainTime.MAX : PlainTime.MIN;
                } else {
                    return PlainTime.createFromMillis(remainder1, remainder2);
                }
            } else if (this.element == MINUTE_OF_DAY) {
                int remainder = MathUtils.floorModulo(value, 1440);
                if ((remainder == 0) && context.isFullMinute()) {
                    return (value > 0) ? PlainTime.MAX : PlainTime.MIN;
                } else {
                    return this.withValue(
                        context, Integer.valueOf(remainder), false);
                }
            } else if (this.element == SECOND_OF_DAY) {
                int remainder = MathUtils.floorModulo(value, 86400);
                if ((remainder == 0) && (context.nano == 0)) {
                    return (value > 0) ? PlainTime.MAX : PlainTime.MIN;
                } else {
                    return this.withValue(
                        context, Integer.valueOf(remainder), false);
                }
            } else {
                throw new UnsupportedOperationException(this.element.name());
            }

        }

        private static boolean isAM(PlainTime context) {

            return ((context.hour < 12) || (context.hour == 24));

        }

    }

    private static class LongElementRule
        implements ElementRule<PlainTime, Long> {

        //~ Instanzvariablen ----------------------------------------------

        private final ChronoElement<Long> element;
        private final long min;
        private final long max;

        //~ Konstruktoren -------------------------------------------------

        LongElementRule(
            ChronoElement<Long> element,
            long min,
            long max
        ) {
            super();

            this.element = element;
            this.min = min;
            this.max = max;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Long getValue(PlainTime context) {

            long ret;

            if (this.element == MICRO_OF_DAY) {
                ret = (context.getNanoOfDay() / KILO);
            } else { // NANO_OF_DAY
                ret = context.getNanoOfDay();
            }

            return Long.valueOf(ret);

        }

        @Override
        public PlainTime withValue(
            PlainTime context,
            Long value,
            boolean lenient
        ) {

            if (value == null) {
                throw new NullPointerException("Missing element value.");
            } else if (lenient) {
                return this.withValueInLenientMode(context, value.longValue());
            } else if (!this.isValid(context, value)) {
                throw new IllegalArgumentException(
                    "Value out of range: " + value);
            }

            long v = value.longValue();

            if (this.element == MICRO_OF_DAY) {
                return PlainTime.createFromMicros(v, context.nano % KILO);
            } else { // NANO_OF_DAY
                return PlainTime.createFromNanos(v);
            }

        }

        @Override
        public boolean isValid(
            PlainTime context,
            Long value
        ) {

            if (value == null) {
                return false;
            } else if (
                (this.element == MICRO_OF_DAY)
                && (value.longValue() == this.max)
            ) {
                return ((context.nano % KILO) == 0);
            } else {
                return (
                    (this.min <= value.longValue())
                    && (value.longValue() <= this.max)
                );
            }

        }

        @Override
        public Long getMinimum(PlainTime context) {

            return Long.valueOf(this.min);

        }

        @Override
        public Long getMaximum(PlainTime context) {

            if (
                (this.element == MICRO_OF_DAY)
                && ((context.nano % KILO) != 0)
            ) {
                return Long.valueOf(this.max - 1);
            }

            return Long.valueOf(this.max);

        }

        @Override
        public ChronoElement<?> getChildAtFloor(PlainTime context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(PlainTime context) {

            return null;

        }

        private PlainTime withValueInLenientMode(
            PlainTime context,
            long value
        ) {

            if (this.element == MICRO_OF_DAY) {
                long remainder1 = floorMod(value, 86400L * MIO);
                int remainder2 = context.nano % KILO;
                return
                    ((remainder1 == 0) && (remainder2 == 0) && (value > 0))
                    ? PlainTime.MAX
                    : PlainTime.createFromMicros(remainder1, remainder2);
            } else { // NANO_OF_DAY
                long remainder = floorMod(value, 86400L * MRD);
                return
                    ((remainder == 0) && (value > 0))
                    ? PlainTime.MAX
                    : PlainTime.createFromNanos(remainder);
            }

        }

    }

    private static class BigDecimalElementRule
        implements ElementRule<PlainTime, BigDecimal> {

        //~ Instanzvariablen ----------------------------------------------

        private final ChronoElement<BigDecimal> element;
        private final BigDecimal max;

        //~ Konstruktoren -------------------------------------------------

        BigDecimalElementRule(
            ChronoElement<BigDecimal> element,
            BigDecimal max
        ) {
            super();

            this.element = element;
            this.max = max;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public BigDecimal getValue(PlainTime context) {

            BigDecimal val;

            if (this.element == DECIMAL_HOUR) {
                if (context.equals(PlainTime.MIN)) {
                    return BigDecimal.ZERO;
                } else if (context.hour == 24) {
                    return DECIMAL_24;
                }

                val =
                    BigDecimal.valueOf(context.hour)
                    .add(div(BigDecimal.valueOf(context.minute), DECIMAL_60))
                    .add(div(BigDecimal.valueOf(context.second), DECIMAL_3600))
                    .add(
                        div(
                            BigDecimal.valueOf(context.nano),
                            DECIMAL_3600.multiply(DECIMAL_MRD)));
            } else if (this.element == DECIMAL_MINUTE) {
                if (context.isFullHour()) {
                    return BigDecimal.ZERO;
                }

                val =
                    BigDecimal.valueOf(context.minute)
                    .add(div(BigDecimal.valueOf(context.second), DECIMAL_60))
                    .add(
                        div(
                            BigDecimal.valueOf(context.nano),
                            DECIMAL_60.multiply(DECIMAL_MRD)));
            } else if (this.element == DECIMAL_SECOND) {
                if (context.isFullMinute()) {
                    return BigDecimal.ZERO;
                }

                val =
                    BigDecimal.valueOf(context.second)
                    .add(div(BigDecimal.valueOf(context.nano), DECIMAL_MRD));
            } else {
                throw new UnsupportedOperationException(this.element.name());
            }

            return val.setScale(15, RoundingMode.FLOOR).stripTrailingZeros();

        }

        @Override
        public PlainTime withValue(
            PlainTime context,
            BigDecimal value,
            boolean lenient
        ) {

            BigDecimal bd = value;
            int h, m, s, f;
            long hv;

            if (this.element == DECIMAL_HOUR) {
                BigDecimal intH = bd.setScale(0, RoundingMode.FLOOR);
                BigDecimal fractionalM = bd.subtract(intH).multiply(DECIMAL_60);
                BigDecimal intM = fractionalM.setScale(0, RoundingMode.FLOOR);
                BigDecimal fractionalS =
                    fractionalM.subtract(intM).multiply(DECIMAL_60);
                BigDecimal intS = fractionalS.setScale(0, RoundingMode.FLOOR);
                hv = intH.longValueExact();
                m = intM.intValue();
                s = intS.intValue();
                f = toNano(fractionalS.subtract(intS));
            } else if (this.element == DECIMAL_MINUTE) {
                BigDecimal totalM = bd.setScale(0, RoundingMode.FLOOR);
                BigDecimal fractionalS =
                    bd.subtract(totalM).multiply(DECIMAL_60);
                BigDecimal intS = fractionalS.setScale(0, RoundingMode.FLOOR);
                s = intS.intValue();
                f = toNano(fractionalS.subtract(intS));
                long minutes = totalM.longValueExact();
                hv = context.hour;
                if (lenient) {
                    hv += MathUtils.floorDivide(minutes, 60);
                    m = MathUtils.floorModulo(minutes, 60);
                } else {
                    checkMinute(minutes);
                    m = (int) minutes;
                }
            } else if (this.element == DECIMAL_SECOND) {
                BigDecimal totalS = bd.setScale(0, RoundingMode.FLOOR);
                f = toNano(bd.subtract(totalS));
                long seconds = totalS.longValueExact();
                hv = context.hour;
                m = context.minute;
                if (lenient) {
                    s = MathUtils.floorModulo(seconds, 60);
                    long minutes = m + MathUtils.floorDivide(seconds, 60);
                    hv += MathUtils.floorDivide(minutes, 60);
                    m = MathUtils.floorModulo(minutes, 60);
                } else {
                    checkSecond(seconds);
                    s = (int) seconds;
                }
            } else {
                throw new UnsupportedOperationException(this.element.name());
            }

            if (lenient) {
                h = MathUtils.floorModulo(hv, 24);
                if ((hv > 0) && ((h | m | s | f) == 0)) {
                    return PlainTime.MAX;
                }
            } else if (hv < 0 || hv > 24) {
                throw new IllegalArgumentException(
                    "Value out of range: " + value);
            } else {
                h = (int) hv;
            }

            return PlainTime.of(h, m, s, f);

        }

        @Override
        public boolean isValid(
            PlainTime context,
            BigDecimal value
        ) {

            if (value == null) {
                return false;
            }

            if (context.hour == 24) {
                if (
                    (this.element == DECIMAL_MINUTE)
                    || (this.element == DECIMAL_SECOND)
                ) {
                    return (BigDecimal.ZERO.compareTo(value) == 0);
                }
            }

            return (
                (BigDecimal.ZERO.compareTo(value) <= 0)
                && (this.max.compareTo(value) >= 0)
            );

        }

        @Override
        public BigDecimal getMinimum(PlainTime context) {

            return BigDecimal.ZERO;

        }

        @Override
        public BigDecimal getMaximum(PlainTime context) {

            if (context.hour == 24) {
                if (
                    (this.element == DECIMAL_MINUTE)
                    || (this.element == DECIMAL_SECOND)
                ) {
                    return BigDecimal.ZERO;
                }
            }

            return this.max;

        }

        @Override
        public ChronoElement<?> getChildAtFloor(PlainTime context) {

            return this.getChild();

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(PlainTime context) {

            return this.getChild();

        }

        private ChronoElement<?> getChild() {

            if (this.element == DECIMAL_HOUR) {
                return DECIMAL_MINUTE;
            } else if (this.element == DECIMAL_MINUTE) {
                return DECIMAL_SECOND;
            } else if (this.element == DECIMAL_SECOND) {
                return NANO_OF_SECOND;
            } else {
                throw new UnsupportedOperationException(this.element.name());
            }

        }

        private static BigDecimal div(
            BigDecimal value,
            BigDecimal factor
        ) {

            return value.divide(factor, 16, RoundingMode.FLOOR);

        }

        private static int toNano(BigDecimal fractionOfSecond) {

            // Dezimalwert fast immer etwas zu klein => Aufrunden notwendig
            BigDecimal result =
                fractionOfSecond.movePointRight(9).setScale(
                    0,
                    RoundingMode.HALF_UP);
            return Math.min(MRD - 1, result.intValue());

        }

    }

    private static class Merger
        implements ChronoMerger<PlainTime> {

        //~ Methoden ------------------------------------------------------

        @Override
        public PlainTime createFrom(
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
            return PlainTime.from(ut, zone.getOffset(ut));

        }

        // Löst bevorzugt Elemente auf, die in Format-Patterns vorkommen
        @Override
        public PlainTime createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes
        ) {

            Leniency leniency =
                attributes.get(Attributes.LENIENCY, Leniency.SMART);

            if (entity instanceof UnixTime) {
                Moment ut = Moment.from(UnixTime.class.cast(entity));

                TZID tzid = ZonalOffset.UTC;

                if (attributes.contains(Attributes.TIMEZONE_ID)) {
                    tzid = attributes.get(Attributes.TIMEZONE_ID);
                }

                return ut.inTimezone(tzid).getWallTime();
            }

            // Uhrzeit bereits vorhanden? -------------------------------------
            if (entity.contains(WALL_TIME)) {
                return entity.get(WALL_TIME);
            }

            // Stundenteil ----------------------------------------------------
            if (entity.contains(DECIMAL_HOUR)) {
                return PlainTime.of(entity.get(DECIMAL_HOUR));
            }

            int hour = 0;

            if (entity.contains(ISO_HOUR)) {
                hour = entity.get(ISO_HOUR).intValue();
            } else {
                Integer h = readHour(entity);
                if (h == null) {
                    return readSpecialCases(entity);
                }
                hour = h.intValue();
                if (
                    (hour == 24)
                    && !leniency.isLax()
                ) {
                    throw new IllegalArgumentException(
                        "Time 24:00 not allowed, "
                        + "use lax mode or ISO_HOUR in smart mode instead.");
                }
            }

            // Minutenteil ----------------------------------------------------
            if (entity.contains(DECIMAL_MINUTE)) {
                return M_DECIMAL_RULE.withValue(
                    PlainTime.of(hour),
                    entity.get(DECIMAL_MINUTE),
                    false
                );
            }

            int minute = 0;

            if (entity.contains(MINUTE_OF_HOUR)) {
                minute = entity.get(MINUTE_OF_HOUR).intValue();
            }

            // Sekundenteil ---------------------------------------------------
            if (entity.contains(DECIMAL_SECOND)) {
                return S_DECIMAL_RULE.withValue(
                    PlainTime.of(hour, minute),
                    entity.get(DECIMAL_SECOND),
                    false
                );
            }

            int second = 0;

            if (entity.contains(SECOND_OF_MINUTE)) {
                second = entity.get(SECOND_OF_MINUTE).intValue();

                if (
                    (second == 60) // Spezialfall: UTC-Schaltsekunde
                    && !leniency.isStrict()
                ) {
                    second = 59;
                }
            }

            // Nanoteil -------------------------------------------------------
            int nanosecond = 0;

            if (entity.contains(NANO_OF_SECOND)) {
                nanosecond = entity.get(NANO_OF_SECOND).intValue();
            } else if (entity.contains(MICRO_OF_SECOND)) {
                nanosecond = entity.get(MICRO_OF_SECOND).intValue() * KILO;
            } else if (entity.contains(MILLI_OF_SECOND)) {
                nanosecond = entity.get(MILLI_OF_SECOND).intValue() * MIO;
            }

            // Ergebnis aus Stunde, Minute, Sekunde und Nano ------------------
            if (leniency.isLax()) {
                long total =
                    MathUtils.safeAdd(
                        MathUtils.safeMultiply(
                            MathUtils.safeAdd(
                                MathUtils.safeAdd(
                                    MathUtils.safeMultiply(hour, 3600L),
                                    MathUtils.safeMultiply(minute, 60L)),
                                second
                            ),
                            MRD
                        ),
                        nanosecond
                    );
                long nanoOfDay = floorMod(total, 86400L * MRD);
                long overflow = floorDiv(total, 86400L * MRD);
                if (entity.isValid(LongElement.DAY_OVERFLOW, overflow)) {
                    entity.with(LongElement.DAY_OVERFLOW, overflow);
                }
                return PlainTime.createFromNanos(nanoOfDay);
            } else {
                return PlainTime.of(hour, minute, second, nanosecond);
            }

        }

        private static Integer readHour(ChronoEntity<?> entity) {

            int hour;

            if (entity.contains(DIGITAL_HOUR_OF_DAY)) {
                hour = entity.get(DIGITAL_HOUR_OF_DAY).intValue();
            } else if (entity.contains(CLOCK_HOUR_OF_DAY)) {
                hour = entity.get(CLOCK_HOUR_OF_DAY).intValue();
                if (hour == 24) {
                    hour = 0;
                }
            } else if (entity.contains(AM_PM_OF_DAY)) {
                Meridiem ampm = entity.get(AM_PM_OF_DAY);
                if (entity.contains(DIGITAL_HOUR_OF_AMPM)) {
                    int h = entity.get(DIGITAL_HOUR_OF_AMPM).intValue();
                    hour = ((ampm == Meridiem.AM) ? h : h + 12);
                } else if (
                    entity.contains(CLOCK_HOUR_OF_AMPM)
                ) {
                    int h = entity.get(CLOCK_HOUR_OF_AMPM).intValue();
                    if (h == 12) {
                        h = 0;
                    }
                    hour = ((ampm == Meridiem.AM) ? h : h + 12);
                } else {
                    return null;
                }
            } else {
                return null;
            }

            return Integer.valueOf(hour);

        }

        private static PlainTime readSpecialCases(ChronoEntity<?> entity) {

            // Spezialfall CLDR-Symbol A --------------------------------------
            if (entity.contains(MILLI_OF_DAY)) {
                int micros = 0;
                if (entity.contains(NANO_OF_SECOND)) {
                    micros =
                      entity.get(NANO_OF_SECOND).intValue() % MIO;
                } else if (entity.contains(MICRO_OF_SECOND)) {
                    micros =
                      entity.get(MICRO_OF_SECOND).intValue() % KILO;
                }
                return PlainTime.createFromMillis(
                    entity.get(MILLI_OF_DAY).intValue(),
                    micros
                );
            }

            return null;

        }

        @Override
        public ChronoEntity<?> preformat(
            PlainTime context,
            AttributeQuery attributes
        ) {

            return context;

        }

    }

}
