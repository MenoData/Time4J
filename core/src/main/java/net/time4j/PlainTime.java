/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (PlainTime.java) is part of project Time4J.
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

import net.time4j.base.MathUtils;
import net.time4j.base.ResourceLoader;
import net.time4j.base.TimeSource;
import net.time4j.base.UnixTime;
import net.time4j.base.WallTime;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.BridgeChronology;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoExtension;
import net.time4j.engine.ChronoMerger;
import net.time4j.engine.Chronology;
import net.time4j.engine.Converter;
import net.time4j.engine.DisplayStyle;
import net.time4j.engine.ElementRule;
import net.time4j.engine.FormattableElement;
import net.time4j.engine.StartOfDay;
import net.time4j.engine.Temporal;
import net.time4j.engine.TimeAxis;
import net.time4j.engine.TimePoint;
import net.time4j.engine.UnitRule;
import net.time4j.engine.ValidationElement;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.CalendarType;
import net.time4j.format.ChronoPattern;
import net.time4j.format.DisplayMode;
import net.time4j.format.Leniency;
import net.time4j.format.LocalizedPatternSupport;
import net.time4j.format.TemporalFormatter;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * <p>Represents a plain wall time without any timezone or date component
 * as defined in ISO-8601 up to nanosecond precision. </p>
 *
 * <p>This type also supports the special value 24:00 in its state space.
 * That value means midnight at the end of day and can be both set and
 * queried. </p>
 *
 * <p>Following elements which are declared as constants are registered by
 * this class: </p>
 *
 * <ul>
 *  <li>{@link #COMPONENT}</li>
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
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine reine Uhrzeit ohne Zeitzonen- oder Datumsteil
 * nach dem ISO-8601-Standard in maximal Nanosekundengenauigkeit. </p>
 *
 * <p>Diese Klasse unterst&uuml;tzt auch den Spezialwert T24:00 in ihrem
 * Zustandsraum, w&auml;hrend die Klasse {@code PlainTimestamp} den Wert
 * lediglich in der Instanzerzeugung, aber nicht in der Manipulation von
 * Daten akzeptiert. </p>
 *
 * <p>Registriert sind folgende als Konstanten deklarierte Elemente: </p>
 *
 * <ul>
 *  <li>{@link #COMPONENT}</li>
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
 */
@CalendarType("iso8601")
public final class PlainTime
    extends TimePoint<IsoTimeUnit, PlainTime>
    implements WallTime, Temporal<PlainTime>, LocalizedPatternSupport {

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

    private static final BigDecimal DECIMAL_60 = new BigDecimal(60);
    private static final BigDecimal DECIMAL_3600 = new BigDecimal(3600);
    private static final BigDecimal DECIMAL_MRD = new BigDecimal(MRD);

    private static final BigDecimal DECIMAL_24_0 =
        new BigDecimal("24");
    private static final BigDecimal DECIMAL_23_9 =
        new BigDecimal("23.999999999999999");
    private static final BigDecimal DECIMAL_59_9 =
        new BigDecimal("59.999999999999999");

    private static final PlainTime[] HOURS = new PlainTime[25];
    private static final long serialVersionUID = 2780881537313863339L;

    static {
        for (int i = 0; i <= 24; i++) {
            HOURS[i] = new PlainTime(i, 0, 0, 0, false);
        }
    }

    /** Minimalwert. */
    static final PlainTime MIN = HOURS[0];

    /** Maximalwert. */
    static final PlainTime MAX = HOURS[24];

    /** Uhrzeitkomponente. */
    static final ChronoElement<PlainTime> WALL_TIME = TimeElement.INSTANCE;

    /**
     * <p>Element with the wall time in the value range
     * {@code [T00:00:00,000000000]} until {@code [T24:00:00,000000000]}
     * (inclusive in the context of {@code PlainTime} else exclusive). </p>
     *
     * <p>Example of usage: </p>
     *
     * <pre>
     *  PlainTimestamp tsp =
     *      PlainTimestamp.localFormatter("uuuu-MM-dd", PatternType.CLDR)
     *          .withDefault(
     *              PlainTime.COMPONENT,
     *              PlainTime.midnightAtStartOfDay())
     *          .parse("2014-08-20");
     *  System.out.println(tsp); // output: 2014-08-20T00
     * </pre>
     *
     * <p>Note: This element does not define any base unit. </p>
     *
     * @since   1.2
     */
    /*[deutsch]
     * <p>Element mit der Uhrzeit im Wertebereich {@code [T00:00:00,000000000]}
     * bis {@code [T24:00:00,000000000]} (inklusive im Kontext von
     * {@code PlainTime}, sonst exklusive). </p>
     *
     * <p>Beispiel: </p>
     *
     * <pre>
     *  PlainTimestamp tsp =
     *      PlainTimestamp.localFormatter("uuuu-MM-dd", PatternType.CLDR)
     *          .withDefault(
     *              PlainTime.COMPONENT,
     *              PlainTime.midnightAtStartOfDay())
     *          .parse("2014-08-20");
     *  System.out.println(tsp); // output: 2014-08-20T00
     * </pre>
     *
     * <p>Hinweis: Dieses Element definiert keine Basiseinheit. </p>
     *
     * @since   1.2
     */
    public static final WallTimeElement COMPONENT = TimeElement.INSTANCE;

    /**
     * <p>Element with the half day section relative to noon (ante meridiem
     * or post meridiem). </p>
     *
     * <p>This element handles the value 24:00 in the same way as 00:00, hence
     * does not make any difference between start and end of day. In detail
     * the mapping from hours to meridiem values looks like following: </p>
     *
     * <div style="margin-top:5px;">
     * <table border="1">
     * <caption>Legend</caption>
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
     * <p>Example: </p>
     *
     * <pre>
     *  import static net.time4j.PlainTime.AM_PM_OF_DAY;
     *
     *  PlainTime time = PlainTime.of(12, 45, 20);
     *  System.out.println(time.get(AM_PM_OF_DAY));
     *  // Output: PM
     * </pre>
     *
     * <p>This element does not define a base unit. </p>
     */
    /*[deutsch]
     * <p>Element mit dem Tagesabschnitt relativ zur Mittagszeit (Vormittag
     * oder Nachmittag). </p>
     *
     * <p>Dieses Element behandelt die Zeit T24:00 genauso wie T00:00, macht
     * also keinen Unterschied zwischen Anfang und Ende eines Tages. Im Detail
     * sieht die Stundenzuordnung so aus: </p>
     *
     * <div style="margin-top:5px;">
     * <table border="1">
     * <caption>Legende</caption>
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
    public static final ZonalElement<Meridiem> AM_PM_OF_DAY =
        AmPmElement.AM_PM_OF_DAY;

    /**
     * <p>Element with the hour of half day in the value range {@code 1-12}
     * (dial on an analogue watch). </p>
     *
     * <p>This element handles the value 24:00 in the same way as 00:00, hence
     * does not make any difference between start and end of day. This is a
     * limitation which preserves the compatibility with CLDR and the class
     * {@code java.text.SimpleDateFormat}. In order to support the full
     * hour range users can use the element {@link #ISO_HOUR}. In detail
     * the mapping to ISO-hours looks like following: </p>
     *
     * <div style="margin-top:5px;">
     * <table border="1">
     * <caption>Legend</caption>
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
    /*[deutsch]
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
     * <caption>Legende</caption>
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
     * <p>Element with the hour in the value range {@code 1-24} (analogue
     * display). </p>
     *
     * <p>This element handles the value 24:00 in the same way as 00:00, hence
     * does not make any difference between start and end of day. This is a
     * limitation which preserves the compatibility with CLDR and the class
     * {@code java.text.SimpleDateFormat}. In order to support the full
     * hour range users can use the element {@link #ISO_HOUR}. In detail
     * the mapping to ISO-hours looks like following: </p>
     *
     * <div style="margin-top:5px;">
     * <table border="1">
     * <caption>Legend</caption>
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
    /*[deutsch]
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
     * <caption>Legende</caption>
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
     * <p>Element with the digital hour of half day in the value range
     * {@code 0-11}. </p>
     *
     * <p>This element handles the value 24:00 in the same way as 00:00, hence
     * does not make any difference between start and end of day. This is a
     * limitation which preserves the compatibility with CLDR and the class
     * {@code java.text.SimpleDateFormat}. In order to support the full
     * hour range users can use the element {@link #ISO_HOUR}. In detail
     * the mapping to ISO-hours looks like following: </p>
     *
     * <div style="margin-top:5px;">
     * <table border="1">
     * <caption>Legend</caption>
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
    /*[deutsch]
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
     * <caption>Legende</caption>
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
     * <p>Element with the digital hour in the value range {@code 0-23}. </p>
     *
     * <p>This element handles the value 24:00 in the same way as 00:00, hence
     * does not make any difference between start and end of day. This is a
     * limitation which preserves the compatibility with CLDR and the class
     * {@code java.text.SimpleDateFormat}. In order to support the full
     * hour range users can use the element {@link #ISO_HOUR}. In detail
     * the mapping to ISO-hours looks like following: </p>
     *
     * <div style="margin-top:5px;">
     * <table border="1">
     * <caption>Legend</caption>
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
    /*[deutsch]
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
     * <caption>Legende</caption>
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
     * <p>Element with the ISO-8601-hour of day in the value range
     * {@code 0-24}. </p>
     *
     * <p>Given a context of {@code PlainTime} with full hours, the maximum
     * is {@code 24} and stands for the time 24:00 (midnight at end of day),
     * else the maximum is {@code 23} in every different context. </p>
     *
     * @see     #getHour()
     */
    /*[deutsch]
     * <p>Element mit der ISO-8601-Stunde im Bereich {@code 0-24}. </p>
     *
     * <p>Im Kontext von {@code PlainTime} mit vollen Stunden ist das Maximum
     * {@code 24} und steht f&uuml;r die Uhrzeit T24:00, ansonsten ist das
     * Maximum in jedem anderen Kontext {@code 23}. </p>
     *
     * @see     #getHour()
     */
    @FormattableElement(format = "H")
    public static final ProportionalElement<Integer, PlainTime> ISO_HOUR =
        IntegerTimeElement.createTimeElement(
            "ISO_HOUR",
            IntegerTimeElement.ISO_HOUR,
            0,
            23,
            'H');

    /**
     * <p>Element with the minute of hour in the value range {@code 0-59}. </p>
     *
     * @see     #getMinute()
     */
    /*[deutsch]
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
     * <p>Element with the minute of day in the value range {@code 0-1440}. </p>
     *
     * <p>Given a context of {@code PlainTime} with full minutes, the maximum
     * is {@code 1440} and stands for the time 24:00 (midnight at end of day),
     * else the maximum is {@code 1439} in every different context. </p>
     */
    /*[deutsch]
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
     * <p>Element with the second of minute in the value range
     * {@code 0-59}. </p>
     *
     * <p>This element does not know any leapseconds in a local context and
     * refers to a normal analogue clock. If this element is used in
     * UTC-context ({@link Moment}) however then the value range is
     * {@code 0-58/59/60} instead. </p>
     *
     * @see     #getSecond()
     */
    /*[deutsch]
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
     * <p>Element with the second of day in the value range
     * {@code 0-86400}. </p>
     *
     * <p>Given a context of {@code PlainTime} with full seconds, the maximum
     * is {@code 86400} and stands for the time 24:00 (midnight at end of day),
     * else the maximum is {@code 86399} in every different context. Leapseconds
     * are never counted. </p>
     */
    /*[deutsch]
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
     * <p>Element with the millisecond in the value range {@code 0-999}. </p>
     */
    /*[deutsch]
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
     * <p>Element with the microsecond in the value range {@code 0-999999}. </p>
     */
    /*[deutsch]
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
     * <p>Element with the nanosecond in the value range
     * {@code 0-999999999}. </p>
     */
    /*[deutsch]
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
     * <p>Element with the day time in milliseconds in the value range
     * {@code 0-86400000}. </p>
     *
     * <p>Given a context of {@code PlainTime} with full milliseconds, the
     * maximum is {@code 86400000} and stands for the time 24:00 (midnight at
     * end of day), else the maximum is {@code 86399999} in every different
     * context. Leapseconds are never counted. </p>
     */
    /*[deutsch]
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
     * <p>Element with the day time in microseconds in the value range
     * {@code 0-86400000000}. </p>
     *
     * <p>Given a context of {@code PlainTime} with full microseconds, the
     * maximum is {@code 86400000000} and stands for the time 24:00 (midnight
     * at end of day), else the maximum is {@code 86399999999} in every
     * different context. Leapseconds are never counted. </p>
     */
    /*[deutsch]
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
     * <p>Element with the day time in nanoseconds in the value range
     * {@code 0-86400000000000}. </p>
     *
     * <p>Given any context of {@code PlainTime}, the maximum is always
     * {@code 86400000000000} and stands for the time 24:00 (midnight
     * at end of day), else the maximum is {@code 86399999999999} in every
     * different context. Leapseconds are never counted. </p>
     *
     * <p>Example: </p>
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
     *  // Output: 25% of day are over.
     * </pre>
     */
    /*[deutsch]
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
     * <p>Decimal hour in the value range {@code 0.0} inclusive until
     * {@code 24.0} exclusive (inclusive in {@code PlainTime}). </p>
     *
     * <p>This element does not define any base unit. </p>
     */
    /*[deutsch]
     * <p>Dezimal-Stunde im Wertebereich {@code 0.0} inklusive bis
     * {@code 24.0} exklusive (inklusive in {@code PlainTime}). </p>
     *
     * <p>Dieses Element definiert keine Basiseinheit. </p>
     */
    public static final ZonalElement<BigDecimal> DECIMAL_HOUR =
        new DecimalTimeElement("DECIMAL_HOUR", DECIMAL_23_9);

    /**
     * <p>Decimal minute in the value range {@code 0.0} inclusive until
     * {@code 60.0} exclusive. </p>
     *
     * <p>This element does not define any base unit. </p>
     */
    /*[deutsch]
     * <p>Dezimal-Minute im Wertebereich {@code 0.0} inklusive bis
     * {@code 60.0} exklusive. </p>
     *
     * <p>Dieses Element definiert keine Basiseinheit. </p>
     */
    public static final ZonalElement<BigDecimal> DECIMAL_MINUTE =
        new DecimalTimeElement("DECIMAL_MINUTE", DECIMAL_59_9);

    /**
     * <p>Decimal second in the value range {@code 0.0} inclusive until
     * {@code 60.0} exclusive. </p>
     *
     * <p>This element does not define any base unit. </p>
     */
    /*[deutsch]
     * <p>Dezimal-Sekunde im Wertebereich {@code 0.0} inklusive bis
     * {@code 60.0} exklusive. </p>
     *
     * <p>Dieses Element definiert keine Basiseinheit. </p>
     */
    public static final ZonalElement<BigDecimal> DECIMAL_SECOND =
        new DecimalTimeElement("DECIMAL_SECOND", DECIMAL_59_9);

    /**
     * <p>Defines the precision as the smallest non-zero time element and
     * truncates time parts of higher precision if necessary. </p>
     *
     * <p>Setting higher precisions than available is without any effect.
     * But setting lower precisions can truncate data however. Examples: </p>
     *
     * <pre>
     *  // reading of precision -------------------------------------
     *  PlainTime time = PlainTime.of(12, 26, 52, 987654000);
     *  System.out.println(time.get(PRECISION)); // Output: MICROS
     *
     *  // setting of precision -------------------------------------
     *  PlainTime time = PlainTime.of(12, 26, 52, 987654000);
     *  System.out.println(time.with(PRECISION, ClockUnit.MILLIS));
     *  // Output: T12:26:52,987
     * </pre>
     *
     * <p>This element does not define any base unit. </p>
     */
    /*[deutsch]
     * <p>Definiert die Genauigkeit als das kleinste von {@code 0} verschiedene
     * Uhrzeitelement und schneidet bei Bedarf zu genaue Zeitanteile ab. </p>
     *
     * <p>Beim Setzen der Genauigkeit ist zu beachten, da&szlig; eine
     * h&ouml;here Genauigkeit wirkungslos ist. Das Setzen einer kleineren
     * Genauigkeit hingegen schneidet Daten ab. Beispiele: </p>
     *
     * <pre>
     *  // Lesen der Genauigkeit ------------------------------------
     *  PlainTime time = PlainTime.of(12, 26, 52, 987654000);
     *  System.out.println(time.get(PRECISION)); // Ausgabe: MICROS
     *
     *  // Setzen der Genauigkeit -----------------------------------
     *  PlainTime time = PlainTime.of(12, 26, 52, 987654000);
     *  System.out.println(time.with(PRECISION, ClockUnit.MILLIS));
     *  // Ausgabe: T12:26:52,987
     * </pre>
     *
     * <p>Dieses Element definiert keine Basiseinheit. </p>
     */
    public static final ChronoElement<ClockUnit> PRECISION = PrecisionElement.CLOCK_PRECISION;

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
        registerExtensions(builder);
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
        int nanosecond,
        boolean validating
    ) {
        super();

        if (validating) {
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

    @Override
    public int getNanosecond() {

        return this.nano;

    }

    /**
     * <p>Yields midnight at the start of the day. </p>
     *
     * @return  midnight at the start of day T00:00
     * @see     #midnightAtEndOfDay()
     */
    /*[deutsch]
     * <p>Liefert Mitternacht zu Beginn des Tages. </p>
     *
     * @return  midnight at the start of day T00:00
     * @see     #midnightAtEndOfDay()
     */
    public static PlainTime midnightAtStartOfDay() {

        return PlainTime.MIN;

    }

    /**
     * <p>Yields midnight at the end of the day, that is midnight at
     * the start of the following day. </p>
     *
     * @return  midnight at the end of day T24:00
     * @see     #midnightAtStartOfDay()
     */
    /*[deutsch]
     * <p>Liefert Mitternacht zum Ende des Tages, das ist Mitternacht zum
     * Start des Folgetags. </p>
     *
     * @return  midnight at the end of day T24:00
     * @see     #midnightAtStartOfDay()
     */
    public static PlainTime midnightAtEndOfDay() {

        return PlainTime.MAX;

    }

    /**
     * <p>Creates a wall time as full hour. </p>
     *
     * @param   hour    iso-hour of day in the range {@code 0-24}
     * @return  cached full hour
     * @throws  IllegalArgumentException if given hour is out of range
     */
    /*[deutsch]
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
     * <p>Creates a wall time with hour and minute. </p>
     *
     * @param   hour    hour of day in the range {@code 0-23} or
     *                  {@code 24} if the given minute equals to {@code 0}
     * @param   minute  minute in the range {@code 0-59}
     * @return  new or cached wall time
     * @throws  IllegalArgumentException if any argument is out of range
     */
    /*[deutsch]
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

        return new PlainTime(hour, minute, 0, 0, true);

    }

    /**
     * <p>Creates a wall time with hour, minute and second. </p>
     *
     * @param   hour        hour in the range {@code 0-23} or {@code 24}
     *                      if the other arguments are equal to {@code 0}
     * @param   minute      minute in the range {@code 0-59}
     * @param   second      second in the range {@code 0-59}
     * @return  new or cached wall time
     * @throws  IllegalArgumentException if any argument is out of range
     */
    /*[deutsch]
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

        return new PlainTime(hour, minute, second, 0, true);

    }

    /**
     * <p>Creates a wall time with hour, minute, second and nanosecond. </p>
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
    /*[deutsch]
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

        return PlainTime.of(hour, minute, second, nanosecond, true);

    }

    /**
     * <p>Creates a wall time by given decimal hour. </p>
     *
     * @param   decimal    decimal hour of day in the range {@code [0.0-24.0]}
     * @return  new or cached wall time
     * @throws  IllegalArgumentException if the argument is out of range
     * @see     #DECIMAL_HOUR
     */
    /*[deutsch]
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
     * <p>Obtains the current clock time in system time. </p>
     *
     * <p>Convenient short-cut for: {@code SystemClock.inLocalView().now().toTime()}. </p>
     *
     * @return  current wall time (without zone) in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     ZonalClock#now()
     * @since   3.23/4.19
     */
    /*[deutsch]
     * <p>Ermittelt die aktuelle Uhrzeit in der Systemzeit. </p>
     *
     * <p>Bequeme Abk&uuml;rzung f&uuml;r: {@code SystemClock.inLocalView().now().toTime()}. </p>
     *
     * @return  current wall time (without zone) in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     ZonalClock#now()
     * @since   3.23/4.19
     */
    public static PlainTime nowInSystemTime() {

        return ZonalClock.ofSystem().now().toTime();

    }

    /**
     * <p>Common conversion method. </p>
     *
     * @param   time    ISO-time
     * @return  PlainTime
     */
    /*[deutsch]
     * <p>Allgemeine Konversionsmethode. </p>
     *
     * @param   time    ISO-time
     * @return  PlainTime
     */
    public static PlainTime from(WallTime time) {

        if (time instanceof PlainTime) {
            return (PlainTime) time;
        } else if (time instanceof PlainTimestamp) {
            return ((PlainTimestamp) time).getWallTime();
        } else {
            return PlainTime.of(
                time.getHour(),
                time.getMinute(),
                time.getSecond(),
                time.getNanosecond());
        }

    }

    /**
     * <p>Rolls this time by the given duration (as amount and unit) and
     * also counts possible day overflow. </p>
     *
     * @param   amount      amount to be added (maybe negative)
     * @param   unit        time unit
     * @return  result of rolling including possible day overflow
     * @see     #plus(long, Object) plus(long, IsoTimeUnit)
     */
    /*[deutsch]
     * <p>Rollt die angegebene Dauer mit Betrag und Einheit zu dieser Uhrzeit
     * auf und z&auml;hlt dabei auch tageweise &Uuml;berl&auml;ufe. </p>
     *
     * @param   amount      amount to be added (maybe negative)
     * @param   unit        time unit
     * @return  result of rolling including possible day overflow
     * @see     #plus(long, Object) plus(long, IsoTimeUnit)
     */
    public DayCycles roll(
        long amount,
        ClockUnit unit
    ) {

        return ClockUnitRule.addToWithOverflow(this, amount, unit);

    }

    /**
     * <p>Creates a new formatter which uses the given pattern in the
     * default locale for formatting and parsing plain times. </p>
     *
     * @param   <P> generic pattern type
     * @param   formatPattern   format definition as pattern
     * @param   patternType     pattern dialect
     * @return  format object for formatting {@code PlainTime}-objects
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
     * @return  format object for formatting {@code PlainTime}-objects
     *          using system locale
     * @throws  IllegalArgumentException if resolving of pattern fails
     * @since   3.0
     */
    public static <P extends ChronoPattern<P>> TemporalFormatter<PlainTime> localFormatter(
        String formatPattern,
        P patternType
    ) {

        return FormatSupport.createFormatter(PlainTime.class, formatPattern, patternType, Locale.getDefault());

    }

    /**
     * <p>Creates a new formatter which uses the given display mode in the
     * default locale for formatting and parsing plain times. </p>
     *
     * @param   mode        formatting style
     * @return  format object for formatting {@code PlainTime}-objects
     *          using system locale
     * @throws  IllegalStateException if format pattern cannot be retrieved
     * @since   3.0
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Format-Objekt mit Hilfe des angegebenen Stils
     * in der Standard-Sprach- und L&auml;ndereinstellung. </p>
     *
     * @param   mode        formatting style
     * @return  format object for formatting {@code PlainTime}-objects
     *          using system locale
     * @throws  IllegalStateException if format pattern cannot be retrieved
     * @since   3.0
     */
    public static TemporalFormatter<PlainTime> localFormatter(DisplayMode mode) {

        return formatter(mode, Locale.getDefault());

    }

    /**
     * <p>Creates a new formatter which uses the given pattern and locale
     * for formatting and parsing plain times. </p>
     *
     * @param   <P> generic pattern type
     * @param   formatPattern   format definition as pattern
     * @param   patternType     pattern dialect
     * @param   locale          locale setting
     * @return  format object for formatting {@code PlainTime}-objects
     *          using given locale
     * @throws  IllegalArgumentException if resolving of pattern fails
     * @since   3.0
     * @see     #localFormatter(String, ChronoPattern)
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Format-Objekt mit Hilfe des angegebenen Musters
     * in der angegebenen Sprach- und L&auml;ndereinstellung. </p>
     *
     * @param   <P> generic pattern type
     * @param   formatPattern   format definition as pattern
     * @param   patternType     pattern dialect
     * @param   locale          locale setting
     * @return  format object for formatting {@code PlainTime}-objects
     *          using given locale
     * @throws  IllegalArgumentException if resolving of pattern fails
     * @since   3.0
     * @see     #localFormatter(String, ChronoPattern)
     */
    public static <P extends ChronoPattern<P>> TemporalFormatter<PlainTime> formatter(
        String formatPattern,
        P patternType,
        Locale locale
    ) {

        return FormatSupport.createFormatter(PlainTime.class, formatPattern, patternType, locale);

    }

    /**
     * <p>Creates a new formatter which uses the given display mode and locale
     * for formatting and parsing plain times. </p>
     *
     * @param   mode        formatting style
     * @param   locale      locale setting
     * @return  format object for formatting {@code PlainTime}-objects
     *          using given locale
     * @throws  IllegalStateException if format pattern cannot be retrieved
     * @since   3.0
     * @see     #localFormatter(DisplayMode)
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Format-Objekt mit Hilfe des angegebenen Stils
     * und in der angegebenen Sprach- und L&auml;ndereinstellung. </p>
     *
     * @param   mode        formatting style
     * @param   locale      locale setting
     * @return  format object for formatting {@code PlainTime}-objects
     *          using given locale
     * @throws  IllegalStateException if format pattern cannot be retrieved
     * @since   3.0
     * @see     #localFormatter(DisplayMode)
     */
    public static TemporalFormatter<PlainTime> formatter(
        DisplayMode mode,
        Locale locale
    ) {

        String formatPattern = CalendarText.patternForTime(mode, locale);
        return FormatSupport.createFormatter(PlainTime.class, formatPattern, locale);

    }

    /**
     * <p>Compares the full state, that is hour, minute, second and nanosecond
     * of this instance and given argument. </p>
     */
    /*[deutsch]
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

    /*[deutsch]
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
     * <p>Is this instance at midnight, either at start or at end of day? </p>
     *
     * @return  boolean
     */
    /*[deutsch]
     * <p>Liegt Mitternacht vor (am Anfang oder am Ende eines Tages)? </p>
     *
     * @return  boolean
     */
    public boolean isMidnight() {

        return (this.isFullHour() && ((this.hour % 24) == 0));

    }

    /**
     * <p>Defines a natural order which is solely based on the timeline
     * order. </p>
     *
     * <p>The natural order is consistent with {@code equals()}. </p>
     *
     * @see     #isBefore(PlainTime)
     * @see     #isAfter(PlainTime)
     */
    /*[deutsch]
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
     * <p>Dependent on the precision of this instance, this method yields a
     * canonical representation in one of following formats (CLDR-syntax): </p>
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
     * <p>The fraction part will be preceded by a comma as recommended by ISO
     * unless the system property &quot;net.time4j.format.iso.decimal.dot&quot;
     * was set to &quot;true&quot;. </p>
     *
     * @return  canonical ISO-8601-formatted string
     */
    /*[deutsch]
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
                    printNanos(sb, this.nano);
                }
            }
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
    public static TimeAxis<IsoTimeUnit, PlainTime> axis() {

        return ENGINE;

    }

    /**
     * <p>Provides a static access to the associated time axis using the foreign type S. </p>
     *
     * @param   <S> foreign temporal type
     * @param   converter       type converter
     * @return  chronological system for foreign type
     * @since   3.24/4.20
     */
    /*[deutsch]
     * <p>Liefert die zugeh&ouml;rige Zeitachse angepasst f&uuml;r den Fremdtyp S. </p>
     *
     * @param   <S> foreign temporal type
     * @param   converter       type converter
     * @return  chronological system for foreign type
     * @since   3.24/4.20
     */
    public static <S> Chronology<S> axis(Converter<S, PlainTime> converter) {

        return new BridgeChronology<S, PlainTime>(converter, ENGINE);

    }

    @Override
    protected TimeAxis<IsoTimeUnit, PlainTime> getChronology() {

        return ENGINE;

    }

    @Override
    protected PlainTime getContext() {

        return this;

    }

    // also called by ZonalDateTime
    static void printNanos(
        StringBuilder sb,
        int nano
    ) {

        sb.append(PlainTime.ISO_DECIMAL_SEPARATOR);
        String num = Integer.toString(nano);
        int len;

        if ((nano % MIO) == 0) {
            len = 3;
        } else if ((nano % KILO) == 0) {
            len = 6;
        } else {
            len = 9;
        }

        for (int i = num.length(); i < 9; i++) {
            sb.append('0');
        }

        for (int i = 0, n = len + num.length() - 9; i < n; i++) {
            sb.append(num.charAt(i));
        }

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
     * <p>Wird von der {@code ratio()}-Function des angegebenenElements
     * aufgerufen. </p>
     *
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

    private static PlainTime of(
        int hour,
        int minute,
        int second,
        int nanosecond,
        boolean validating
    ) {

        if ((minute | second | nanosecond) == 0) {
            if (validating) {
                return PlainTime.of(hour);
            } else {
                return HOURS[hour];
            }
        }

        return new PlainTime(hour, minute, second, nanosecond, validating);

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
            + (long) this.second * MRD
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

    private static void registerExtensions(TimeAxis.Builder<IsoTimeUnit, PlainTime> builder) {

        for (ChronoExtension extension : ResourceLoader.getInstance().services(ChronoExtension.class)) {
            if (extension.accept(PlainTime.class)) {
                builder.appendExtension(extension);
            }
        }

        builder.appendExtension(new DayPeriod.Extension());

    }

    private static void registerUnits(TimeAxis.Builder<IsoTimeUnit, PlainTime> builder) {

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
     *      out.writeByte(2 &lt;&lt; 4);
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
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.TIME_TYPE);

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

            if ((amount == 0) && (context.hour < 24)) {
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
            PlainTime time;

            if ((hour | minute | second | fraction) == 0) { // midnight
                time = (
                	((amount > 0) && (returnType == PlainTime.class))
                	? PlainTime.MAX
                	: PlainTime.MIN);
            } else {
                time = PlainTime.of(hour, minute, second, fraction);
            }

            if (returnType == PlainTime.class) {
                return returnType.cast(time);
            } else {
                long cycles = MathUtils.floorDivide(hours, 24);
                return returnType.cast(new DayCycles(cycles, time));
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
                throw new IllegalArgumentException("Missing time value.");
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

            if (value == null) {
                throw new IllegalArgumentException("Missing precision value.");
            }

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
                throw new IllegalArgumentException("Missing am/pm-value.");
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
                throw new IllegalArgumentException("Missing element value.");
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
                throw new IllegalArgumentException("Missing element value.");
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
                    return DECIMAL_24_0;
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
            BigDecimal bd,
            boolean lenient
        ) {

            if (bd == null) {
                throw new IllegalArgumentException("Missing element value.");
            }

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
                    "Value out of range: " + bd);
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

            return null; // never called

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(PlainTime context) {

            return null; // never called

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
        public String getFormatPattern(
            DisplayStyle style,
            Locale locale
        ) {

            DisplayMode mode = DisplayMode.ofStyle(style.getStyleValue());
            return CalendarText.patternForTime(mode, locale);

        }

        @Override
        public PlainTime createFrom(
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
            return PlainTime.from(ut, zone.getOffset(ut));

        }

        @Override
        @Deprecated
        public PlainTime createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean preparsing
        ) {

            boolean lenient = attributes.get(Attributes.LENIENCY, Leniency.SMART).isLax();
            return this.createFrom(entity, attributes, lenient, preparsing);

        }

        // Löst bevorzugt Elemente auf, die in Format-Patterns vorkommen
        @Override
        public PlainTime createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean lenient,
            boolean preparsing
        ) {

            if (entity instanceof UnixTime) {
                return PlainTimestamp.axis().createFrom(entity, attributes, lenient, preparsing).getWallTime();
            } else if (entity.contains(WALL_TIME)) {
                return entity.get(WALL_TIME);
            }

            // Stundenteil ----------------------------------------------------
            if (entity.contains(DECIMAL_HOUR)) {
                return PlainTime.of(entity.get(DECIMAL_HOUR));
            }

            int hour = entity.getInt(ISO_HOUR);

            if (hour == Integer.MIN_VALUE) {
                hour = readHour(entity);
                if (hour == Integer.MIN_VALUE) {
                    return readSpecialCases(entity);
                }
                if ((hour == 24) && !lenient) {
                    flagValidationError(
                        entity,
                        "Time 24:00 not allowed, "
                        + "use lax mode or element ISO_HOUR instead.");
                    return null;
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

            int minute = entity.getInt(MINUTE_OF_HOUR);

            if (minute == Integer.MIN_VALUE) {
                minute = 0; // fallback
            }

            // Sekundenteil ---------------------------------------------------
            if (entity.contains(DECIMAL_SECOND)) {
                return S_DECIMAL_RULE.withValue(
                    PlainTime.of(hour, minute),
                    entity.get(DECIMAL_SECOND),
                    false
                );
            }

            int second = entity.getInt(SECOND_OF_MINUTE);

            if (second == Integer.MIN_VALUE) {
                second = 0; // fallback
            }

            // Nanoteil -------------------------------------------------------
            int nanosecond = entity.getInt(NANO_OF_SECOND);

            if (nanosecond == Integer.MIN_VALUE) {
                nanosecond = entity.getInt(MICRO_OF_SECOND);
                if (nanosecond == Integer.MIN_VALUE) {
                    nanosecond = entity.getInt(MILLI_OF_SECOND);
                    if (nanosecond == Integer.MIN_VALUE) {
                        nanosecond = 0; // fallback
                    } else {
                        nanosecond = MathUtils.safeMultiply(nanosecond, MIO);
                    }
                } else {
                    nanosecond = MathUtils.safeMultiply(nanosecond, KILO);
                }
            }

            // Ergebnis aus Stunde, Minute, Sekunde und Nano ------------------
            if (lenient) {
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
                if (
                    (overflow != 0)
                    && entity.isValid(LongElement.DAY_OVERFLOW, overflow)
                ) {
                    entity.with(LongElement.DAY_OVERFLOW, overflow);
                }
                if ((nanoOfDay == 0) && (overflow > 0)) {
                    return PlainTime.MAX;
                } else {
                    return PlainTime.createFromNanos(nanoOfDay);
                }
            } else if (
                (hour >= 0)
                && (minute >= 0)
                && (second >= 0)
                && (nanosecond >= 0)
                && (
                    ((hour == 24) && (minute | second | nanosecond) == 0))
                    || (
                        (hour < 24)
                        && (minute <= 59)
                        && (second <= 59)
                        && (nanosecond <= MRD))
            ) {
                return PlainTime.of(hour, minute, second, nanosecond, false);
            } else {
                flagValidationError(entity, "Time component out of range.");
                return null;
            }

        }

        private static int readHour(ChronoEntity<?> entity) {

            int hour = entity.getInt(DIGITAL_HOUR_OF_DAY);

            if (hour != Integer.MIN_VALUE) {
                return hour;
            }

            hour = entity.getInt(CLOCK_HOUR_OF_DAY);

            if (hour == 24) {
                return 0;
            } else if (hour != Integer.MIN_VALUE) {
                return hour;
            }

            if (entity.contains(AM_PM_OF_DAY)) {
                Meridiem ampm = entity.get(AM_PM_OF_DAY);
                int h = entity.getInt(CLOCK_HOUR_OF_AMPM);

                if (h != Integer.MIN_VALUE) {
                    if (h == 12) {
                        h = 0;
                    }
                    return ((ampm == Meridiem.AM) ? h : h + 12);
                }

                h = entity.getInt(DIGITAL_HOUR_OF_AMPM);

                if (h != Integer.MIN_VALUE) {
                    return ((ampm == Meridiem.AM) ? h : h + 12);
                }
            }

            return Integer.MIN_VALUE;

        }

        private static PlainTime readSpecialCases(ChronoEntity<?> entity) {

            if (entity.contains(NANO_OF_DAY)) { // Threeten-Symbol N
                long nanoOfDay = entity.get(NANO_OF_DAY).longValue();
                if ((nanoOfDay < 0) || (nanoOfDay > 86400L * MRD)) {
                    flagValidationError(
                        entity,
                        "NANO_OF_DAY out of range: " + nanoOfDay);
                    return null;
                }
                return PlainTime.createFromNanos(nanoOfDay);
            } else if (entity.contains(MICRO_OF_DAY)) {
                int nanos = 0;
                if (entity.contains(NANO_OF_SECOND)) {
                    nanos = entity.get(NANO_OF_SECOND).intValue() % KILO;
                }
                return PlainTime.createFromMicros(
                    entity.get(MICRO_OF_DAY).longValue(),
                    nanos
                );
            } else if (entity.contains(MILLI_OF_DAY)) { // CLDR-Symbol A
                int submillis = 0;
                if (entity.contains(NANO_OF_SECOND)) {
                    int nanoOfSecond = entity.get(NANO_OF_SECOND).intValue();
                    if ((nanoOfSecond < 0) || (nanoOfSecond >= MRD)) {
                        flagValidationError(
                            entity,
                            "NANO_OF_SECOND out of range: " + nanoOfSecond);
                        return null;
                    }
                    submillis = nanoOfSecond % MIO;
                } else if (entity.contains(MICRO_OF_SECOND)) {
                    int microOfSecond = entity.get(MICRO_OF_SECOND).intValue();
                    if ((microOfSecond < 0) || (microOfSecond >= MIO)) {
                        flagValidationError(
                            entity,
                            "MICRO_OF_SECOND out of range: " + microOfSecond);
                        return null;
                    }
                    submillis = microOfSecond % KILO;
                }
                int milliOfDay = entity.get(MILLI_OF_DAY).intValue();
                if ((milliOfDay < 0) || (milliOfDay > 86400 * KILO)) {
                    flagValidationError(
                        entity,
                        "MILLI_OF_DAY out of range: " + milliOfDay);
                    return null;
                }
                return PlainTime.createFromMillis(milliOfDay, submillis);
            } else if (entity.contains(SECOND_OF_DAY)) {
                int nanos = 0;
                if (entity.contains(NANO_OF_SECOND)) {
                    nanos = entity.get(NANO_OF_SECOND).intValue();
                } else if (entity.contains(MICRO_OF_SECOND)) {
                    nanos = entity.get(MICRO_OF_SECOND).intValue() * KILO;
                } else if (entity.contains(MILLI_OF_SECOND)) {
                    nanos = entity.get(MILLI_OF_SECOND).intValue() * MIO;
                }
                return PlainTime.of(0, 0, 0, nanos).with(
                    SECOND_OF_DAY,
                    entity.get(SECOND_OF_DAY));
            } else if (entity.contains(MINUTE_OF_DAY)) {
                int nanos = 0;
                if (entity.contains(NANO_OF_SECOND)) {
                    nanos = entity.get(NANO_OF_SECOND).intValue();
                } else if (entity.contains(MICRO_OF_SECOND)) {
                    nanos = entity.get(MICRO_OF_SECOND).intValue() * KILO;
                } else if (entity.contains(MILLI_OF_SECOND)) {
                    nanos = entity.get(MILLI_OF_SECOND).intValue() * MIO;
                }
                int secs = 0;
                if (entity.contains(SECOND_OF_MINUTE)) {
                    secs = entity.get(SECOND_OF_MINUTE).intValue();
                }
                return PlainTime.of(0, 0, secs, nanos).with(
                    MINUTE_OF_DAY,
                    entity.get(MINUTE_OF_DAY));
            }

            return null;

        }

        private static void flagValidationError(
            ChronoEntity<?> entity,
            String message
        ) {

            if (entity.isValid(ValidationElement.ERROR_MESSAGE, message)) {
                entity.with(ValidationElement.ERROR_MESSAGE, message);
            }

        }

        @Override
        public ChronoDisplay preformat(
            PlainTime context,
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

            return StartOfDay.MIDNIGHT;

        }

    }

}
