/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (BadiCalendar.java) is part of project Time4J.
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

package net.time4j.calendar.bahai;

import net.time4j.GeneralTimestamp;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.SystemClock;
import net.time4j.Weekday;
import net.time4j.Weekmodel;
import net.time4j.base.GregorianMath;
import net.time4j.base.MathUtils;
import net.time4j.base.TimeSource;
import net.time4j.calendar.StdCalendarElement;
import net.time4j.calendar.astro.SolarTime;
import net.time4j.calendar.astro.StdSolarCalculator;
import net.time4j.calendar.service.StdEnumDateElement;
import net.time4j.calendar.service.StdIntegerDateElement;
import net.time4j.calendar.service.StdWeekdayElement;
import net.time4j.engine.AttributeKey;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.BasicElement;
import net.time4j.engine.CalendarDays;
import net.time4j.engine.CalendarEra;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.Calendrical;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.ChronoMerger;
import net.time4j.engine.ChronoUnit;
import net.time4j.engine.Chronology;
import net.time4j.engine.ElementRule;
import net.time4j.engine.EpochDays;
import net.time4j.engine.FormattableElement;
import net.time4j.engine.IntElementRule;
import net.time4j.engine.StartOfDay;
import net.time4j.engine.TimeAxis;
import net.time4j.engine.UnitRule;
import net.time4j.engine.ValidationElement;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.CalendarType;
import net.time4j.format.DisplayElement;
import net.time4j.format.Leniency;
import net.time4j.format.OutputContext;
import net.time4j.format.TextAccessor;
import net.time4j.format.TextElement;
import net.time4j.format.TextWidth;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.text.ParsePosition;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


/**
 * <p>Represents the calendar used by the Baha'i community. </p>
 *
 * <p>The calendar composes 19 days to a month, 19 months to a year (plus an intercalary period called Ayyam-i-Ha
 * between the 18th and the 19th month), 19 years to a vahid cycle and finally 19 vahids to a major cycle. Days
 * start at sunset. And a week starts on Friday at sunset. The first year of the calendar begins on 1844-03-21.
 * Years before 2015 start in this implementation always on 21st of March, but then on the day (from sunset
 * to sunset in Teheran) which contains the vernal equinox. The latter change follows a decision made by
 * the <a href="https://en.wikipedia.org/wiki/Universal_House_of_Justice">Universal House of Justice</a>. </p>
 *
 * <p>Following elements which are declared as constants are registered by
 * this class: </p>
 *
 * <ul>
 *  <li>{@link #DAY_OF_DIVISION}</li>
 *  <li>{@link #DAY_OF_WEEK}</li>
 *  <li>{@link #DAY_OF_YEAR}</li>
 *  <li>{@link #AYYAM_I_HA}</li>
 *  <li>{@link #MONTH_OF_YEAR}</li>
 *  <li>{@link #YEAR_OF_VAHID}</li>
 *  <li>{@link #VAHID}</li>
 *  <li>{@link #KULL_I_SHAI}</li>
 *  <li>{@link #YEAR_OF_ERA}</li>
 *  <li>{@link #ERA}</li>
 * </ul>
 *
 * <p>Furthermore, all elements defined in {@code EpochDays} are supported. </p>
 *
 * <p><strong>Formatting and parsing:</strong> When using format patterns the
 * {@link net.time4j.format.expert.PatternType#DYNAMIC dynamic pattern type}
 * is strongly recommended instead of CLDR-like pattern types because this calendar
 * is structurally different from month-based calendars. Following symbol-element
 * table holds: </p>
 *
 * <div style="margin-top:5px;">
 * <table border="1">
 * <caption>Mapping of dynamic pattern symbols</caption>
 * <tr>
 *  <th>element</th><th>symbol</th><th>type</th>
 * </tr>
 * <tr>
 *  <td>ERA</td><td>G</td><td>text</td>
 * </tr>
 * <tr>
 *  <td>YEAR_OF_ERA</td><td>y/Y</td><td>number</td>
 * </tr>
 * <tr>
 *  <td>KULL_I_SHAI</td><td>k/K</td><td>number</td>
 * </tr>
 * <tr>
 *  <td>VAHID</td><td>v/V</td><td>number</td>
 * </tr>
 * <tr>
 *  <td>YEAR_OF_VAHID</td><td>x/X</td><td>number/text</td>
 * </tr>
 * <tr>
 *  <td>MONTH_OF_YEAR</td><td>m/M</td><td>number/text</td>
 * </tr>
 * <tr>
 *  <td>AYYAM_I_HA</td><td>A</td><td>text</td>
 * </tr>
 * <tr>
 *  <td>DAY_OF_DIVISION</td><td>d/D</td><td>number</td>
 * </tr>
 * <tr>
 *  <td>DAY_OF_WEEK</td><td>E</td><td>text</td>
 * </tr>
 * </table>
 * </div>
 *
 * <p>It is strongly recommended to use the or-operator &quot;|&quot; in format patterns because not every date of
 * this calendar has a month. Example: </p>
 *
 * <pre>
 *     ChronoFormatter&lt;BadiCalendar&gt; f =
 *      ChronoFormatter.ofPattern(
 *          &quot;k.v.x.m.d|k.v.x.A.d&quot;,
 *          PatternType.DYNAMIC,
 *          Locale.GERMAN,
 *          BadiCalendar.axis());
 *     assertThat(
 *      f.print(BadiCalendar.of(5, 11, BadiMonth.JALAL, 13)),
 *      is(&quot;1.5.11.2.13&quot;));
 *     assertThat(
 *      f.print(BadiCalendar.ofIntercalary(5, 11, 2)),
 *      is(&quot;1.5.11.Aiyam-e Ha'.2&quot;));
 * </pre>
 *
 * @author  Meno Hochschild
 * @since   5.3
 */
/*[deutsch]
 * <p>Repr&auml;sentiert den von der Baha'i-Gemeinde verwendeten Kalender. </p>
 *
 * <p>Der Kalender setzt sich aus 19 Tagen je Monat, dann aus 19 Monaten je Jahr (zuz&uuml;glich einer Periode
 * von 4 oder 5 eingeschobenen Tagen nach dem achtzehnten Monat, genannt Ayyam-i-Ha), dann aus 19 Jahren je
 * Einheitszyklus (Vahid) und schließlich aus 19 Einheitszyklen je Hauptzyklus (kull-i-shai) zusammen. Die
 * Tage fangen zum Sonnenuntergang des vorherigen Tages an, so da&szlig; die Woche am Freitagabend beginnt.
 * Das erste Jahr des Kalenders beginnt zum Datum 1844-03-21. Jahre vor 2015 fangen immer am 21. M&aumlr;z
 * an, danach an dem Tag (von Sonnenuntergang zu Sonnenuntergang), der den Fr&uuml;hlingspunkt in Teheran
 * enth&auml;lt. Dieser Regelwechsel folgt einer Entscheidung, die vom
 * <a href="https://en.wikipedia.org/wiki/Universal_House_of_Justice">Universal House of Justice</a>
 * getroffen wurde. </p>
 *
 * <p>Folgende als Konstanten deklarierte Elemente werden von dieser Klasse registriert: </p>
 *
 * <ul>
 *  <li>{@link #DAY_OF_DIVISION}</li>
 *  <li>{@link #DAY_OF_WEEK}</li>
 *  <li>{@link #DAY_OF_YEAR}</li>
 *  <li>{@link #AYYAM_I_HA}</li>
 *  <li>{@link #MONTH_OF_YEAR}</li>
 *  <li>{@link #YEAR_OF_VAHID}</li>
 *  <li>{@link #VAHID}</li>
 *  <li>{@link #KULL_I_SHAI}</li>
 *  <li>{@link #YEAR_OF_ERA}</li>
 *  <li>{@link #ERA}</li>
 * </ul>
 *
 * <p>Au&slig;erdem werden alle Elemente von {@code EpochDays} unterst&uuml;tzt. </p>
 *
 * <p><strong>Formatieren und Interpretation:</strong> Wenn Formatmuster verwendet werden,
 * wird der {@link net.time4j.format.expert.PatternType#DYNAMIC dynamische Formatmustertyp}
 * anstelle von CLDR-basierten Formatmustertypen empfohlen, weil dieser Kalender strukturell
 * von monatsbasierten Kalendern verschieden ist. Folgende Symbol-Element-Tabelle gilt: </p>
 *
 * <div style="margin-top:5px;">
 * <table border="1">
 * <caption>Zuordnung von dynamischen Mustersymbolen</caption>
 * <tr>
 *  <th>Element</th><th>Symbol</th><th>Typ</th>
 * </tr>
 * <tr>
 *  <td>ERA</td><td>G</td><td>text</td>
 * </tr>
 * <tr>
 *  <td>YEAR_OF_ERA</td><td>y/Y</td><td>number</td>
 * </tr>
 * <tr>
 *  <td>KULL_I_SHAI</td><td>k/K</td><td>number</td>
 * </tr>
 * <tr>
 *  <td>VAHID</td><td>v/V</td><td>number</td>
 * </tr>
 * <tr>
 *  <td>YEAR_OF_VAHID</td><td>x/X</td><td>number/text</td>
 * </tr>
 * <tr>
 *  <td>MONTH_OF_YEAR</td><td>m/M</td><td>number/text</td>
 * </tr>
 * <tr>
 *  <td>AYYAM_I_HA</td><td>A</td><td>text</td>
 * </tr>
 * <tr>
 *  <td>DAY_OF_DIVISION</td><td>d/D</td><td>number</td>
 * </tr>
 * <tr>
 *  <td>DAY_OF_WEEK</td><td>E</td><td>text</td>
 * </tr>
 * </table>
 * </div>
 *
 * <p>Weil nicht jedes Datum dieses Kalenders einen Monat hat, ist es angeraten, mit dem Oder-Operator
 * &quot;|&quot; in der Formatierung zu arbeiten. Beispiel: </p>
 *
 * <pre>
 *     ChronoFormatter&lt;BadiCalendar&gt; f =
 *      ChronoFormatter.ofPattern(
 *          &quot;k.v.x.m.d|k.v.x.A.d&quot;,
 *          PatternType.DYNAMIC,
 *          Locale.GERMAN,
 *          BadiCalendar.axis());
 *     assertThat(
 *      f.print(BadiCalendar.of(5, 11, BadiMonth.JALAL, 13)),
 *      is(&quot;1.5.11.2.13&quot;));
 *     assertThat(
 *      f.print(BadiCalendar.ofIntercalary(5, 11, 2)),
 *      is(&quot;1.5.11.Aiyam-e Ha'.2&quot;));
 * </pre>
 *
 * @author  Meno Hochschild
 * @since   5.3
 */
@CalendarType("bahai")
public final class BadiCalendar
    extends Calendrical<BadiCalendar.Unit, BadiCalendar> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * Format attribute which controls the content of some text elements like months or weekdays.
     *
     * <p>Standard value is: {@link FormattedContent#TRANSCRIPTION}. Example: </p>
     *
     * <pre>
     *  ChronoFormatter&lt;BadiCalendar&gt; f =
     *      ChronoFormatter
     *          .ofPattern(
     *              &quot;k-v-x-MMMM-d|k-v-x-A-d&quot;,
     *              PatternType.DYNAMIC,
     *              Locale.ENGLISH,
     *              BadiCalendar.axis())
     *          .with(BadiCalendar.TEXT_CONTENT_ATTRIBUTE, FormattedContent.HTML);
     *  System.out.println(f.print(BadiCalendar.of(5, 11, BadiMonth.MASHIYYAT, 15)));
     * </pre>
     *
     * <p>Output with underlined sh:
     * &quot;1-5-11-Ma<span style="text-decoration: underline;">sh</span>íyyat-15&quot;</p>
     *
     * @see     #YEAR_OF_VAHID
     * @see     #MONTH_OF_YEAR
     * @see     #DAY_OF_WEEK
     */
    /*[deutsch]
     * Formatattribut, das den Inhalt von einigen Textelementen wie Monaten oder Wochentagen steuert.
     *
     * <p>Standardwert ist: {@link FormattedContent#TRANSCRIPTION}. Beispiel: </p>
     *
     * <pre>
     *  ChronoFormatter&lt;BadiCalendar&gt; f =
     *      ChronoFormatter
     *          .ofPattern(
     *              &quot;k-v-x-MMMM-d|k-v-x-A-d&quot;,
     *              PatternType.DYNAMIC,
     *              Locale.ENGLISH,
     *              BadiCalendar.axis())
     *          .with(BadiCalendar.TEXT_CONTENT_ATTRIBUTE, FormattedContent.HTML);
     *  System.out.println(f.print(BadiCalendar.of(5, 11, BadiMonth.MASHIYYAT, 15)));
     * </pre>
     *
     * <p>Ausgabe mit unterstrichenem sh:
     * &quot;1-5-11-Ma<span style="text-decoration: underline;">sh</span>íyyat-15&quot;</p>
     *
     * @see     #YEAR_OF_VAHID
     * @see     #MONTH_OF_YEAR
     * @see     #DAY_OF_WEEK
     */
    public static final AttributeKey<FormattedContent> TEXT_CONTENT_ATTRIBUTE =
        Attributes.createKey("FORMATTED_CONTENT", FormattedContent.class);

    private static final SolarTime TEHERAN =
        SolarTime.ofLocation()
            .easternLongitude(51, 25, 0.0)
            .northernLatitude(35, 42, 0.0)
            .usingCalculator(StdSolarCalculator.TIME4J)
            .build();

    private static final int KULL_I_SHAI_INDEX = 0;
    private static final int VAHID_INDEX = 1;
    private static final int YEAR_INDEX = 2;
    private static final int DAY_OF_DIVISION_INDEX = 3;
    private static final int DAY_OF_YEAR_INDEX = 4;
    private static final int YOE_INDEX = 5;

    private static final int[] NEWROZ = {
        15785, 16150, 16515, 16881, 17246, 17611, 17976, 18342, 18707, 19072,
        19437, 19803, 20168, 20533, 20898, 21263, 21629, 21994, 22359, 22724,
        23090, 23455, 23820, 24185, 24551, 24916, 25281, 25646, 26012, 26377,
        26742, 27107, 27473, 27838, 28203, 28568, 28934, 29299, 29664, 30029,
        30395, 30760, 31125, 31490, 31855, 32221, 32586, 32951, 33316, 33682,
        34047, 34412, 34777, 35143, 35508, 35873, 36238, 36604, 36969, 37334,
        37699, 38065, 38430, 38795, 39160, 39526, 39891, 40256, 40621, 40987,
        41352, 41717, 42082, 42448, 42813, 43178, 43543, 43908, 44274, 44639,
        45004, 45369, 45735, 46100, 46465, 46830, 47196, 47561, 47926, 48291,
        48657, 49022, 49387, 49752, 50118, 50483, 50848, 51213, 51579, 51944,
        52309, 52674, 53040, 53405, 53770, 54135, 54501, 54866, 55231, 55596,
        55961, 56327, 56692, 57057, 57422, 57788, 58153, 58518, 58883, 59249,
        59614, 59979, 60344, 60710, 61075, 61440, 61805, 62171, 62536, 62901,
        63266, 63632, 63997, 64362, 64727, 65093, 65458, 65823, 66188, 66554,
        66919, 67284, 67649, 68014, 68380, 68745, 69110, 69475, 69841, 70206,
        70571, 70936, 71302, 71667, 72032, 72397, 72763, 73128, 73493, 73858,
        74224, 74589, 74954, 75319, 75685, 76050, 76415, 76780, 77146, 77511,
        77876, 78241, 78607, 78972, 79337, 79702, 80067, 80433, 80798, 81163,
        81528, 81894, 82259, 82624, 82989, 83355, 83720, 84085, 84450, 84816,
        85181, 85546, 85911, 86277, 86642, 87007, 87372, 87738, 88103, 88468,
        88833, 89199, 89564, 89929, 90294, 90660, 91025, 91390, 91755, 92120,
        92486, 92851, 93216, 93581, 93947, 94312, 94677, 95042, 95408, 95773,
        96138, 96503, 96869, 97234, 97599, 97964, 98330, 98695, 99060, 99425,
        99791, 100156, 100521, 100886, 101252, 101617, 101982, 102347, 102713, 103078,
        103443, 103808, 104173, 104539, 104904, 105269, 105634, 106000, 106365, 106730,
        107095, 107461, 107826, 108191, 108556, 108922, 109287, 109652, 110017, 110383,
        110748, 111113, 111478, 111844, 112209, 112574, 112939, 113305, 113670, 114035,
        114400, 114766, 115131, 115496, 115861, 116226, 116592, 116957, 117322, 117687,
        118053, 118418, 118783, 119148, 119514, 119879, 120244, 120609, 120975, 121340,
        121705, 122070, 122436, 122801, 123166, 123531, 123897, 124262, 124627, 124992,
        125358, 125723, 126088, 126453, 126819, 127184, 127549, 127914, 128279, 128645,
        129010, 129375, 129740, 130106, 130471, 130836, 131201, 131567, 131932, 132297,
        132662, 133028, 133393, 133758, 134123, 134489, 134854, 135219, 135584, 135950,
        136315, 136680, 137045, 137411, 137776, 138141, 138506, 138872, 139237, 139602,
        139967, 140332, 140698, 141063, 141428, 141793, 142159, 142524, 142889, 143254,
        143620, 143985, 144350, 144715, 145081, 145446, 145811, 146176, 146542, 146907,
        147272, 147637, 148003, 148368, 148733, 149098, 149464, 149829, 150194, 150559,
        150924, 151290, 151655, 152020, 152385, 152751, 153116, 153481, 153846, 154212,
        154577, 154942, 155307, 155673, 156038, 156403, 156768, 157134, 157499, 157864,
        158229, 158595, 158960, 159325, 159690, 160056, 160421, 160786, 161151, 161517,
        161882, 162247, 162612, 162978, 163343, 163708, 164073, 164438, 164804, 165169,
        165534, 165899, 166265, 166630, 166995, 167360, 167726, 168091, 168456, 168821,
        169187, 169552, 169917, 170282, 170648, 171013, 171378, 171743, 172109, 172474,
        172839, 173204, 173570, 173935, 174300, 174665, 175031, 175396, 175761, 176126,
        176491, 176857, 177222, 177587, 177952, 178318, 178683, 179048, 179413, 179779,
        180144, 180509, 180874, 181240, 181605, 181970, 182335, 182701, 183066, 183431,
        183796, 184162, 184527, 184892, 185257, 185623, 185988, 186353, 186718, 187084,
        187449, 187814, 188179, 188544, 188910, 189275, 189640, 190005, 190371, 190736,
        191101, 191466, 191832, 192197, 192562, 192927, 193293, 193658, 194023, 194388,
        194754, 195119, 195484, 195849, 196215, 196580, 196945, 197310, 197676, 198041,
        198406, 198771, 199136, 199502, 199867, 200232, 200597, 200963, 201328, 201693,
        202058, 202424, 202789, 203154, 203519, 203885, 204250, 204615, 204980, 205346,
        205711, 206076, 206441, 206807, 207172, 207537, 207902, 208268, 208633, 208998,
        209363, 209729, 210094, 210459, 210824, 211189, 211555, 211920, 212285, 212650,
        213016, 213381, 213746, 214111, 214477, 214842, 215207, 215572, 215938, 216303,
        216668, 217033, 217399, 217764, 218129, 218494, 218860, 219225, 219590, 219955,
        220321, 220686, 221051, 221416, 221782, 222147, 222512, 222877, 223242, 223608,
        223973, 224338, 224703, 225069, 225434, 225799, 226164, 226530, 226895, 227260,
        227625, 227991, 228356, 228721, 229086, 229452, 229817, 230182, 230547, 230913,
        231278, 231643, 232008, 232374, 232739, 233104, 233469, 233835, 234200, 234565,
        234930, 235295, 235661, 236026, 236391, 236756, 237122, 237487, 237852, 238217,
        238583, 238948, 239313, 239678, 240044, 240409, 240774, 241139, 241505, 241870,
        242235, 242600, 242966, 243331, 243696, 244061, 244427, 244792, 245157, 245522,
        245888, 246253, 246618, 246983, 247348, 247714, 248079, 248444, 248809, 249175,
        249540, 249905, 250270, 250636, 251001, 251366, 251731, 252097, 252462, 252827,
        253192, 253558, 253923, 254288, 254653, 255019, 255384, 255749, 256114, 256480,
        256845, 257210, 257575, 257941, 258306, 258671, 259036, 259401, 259767, 260132,
        260497, 260862, 261228, 261593, 261958, 262323, 262689, 263054, 263419, 263784,
        264150, 264515, 264880, 265245, 265611, 265976, 266341, 266706, 267072, 267437,
        267802, 268167, 268533, 268898, 269263, 269628, 269994, 270359, 270724, 271089,
        271454, 271820, 272185, 272550, 272915, 273281, 273646, 274011, 274376, 274742,
        275107, 275472, 275837, 276203, 276568, 276933, 277298, 277664, 278029, 278394,
        278759, 279125, 279490, 279855, 280220, 280586, 280951, 281316, 281681, 282047,
        282412, 282777, 283142, 283507, 283873, 284238, 284603, 284968, 285334, 285699,
        286064, 286429, 286795, 287160, 287525, 287890, 288256, 288621, 288986, 289351,
        289717, 290082, 290447, 290812, 291178, 291543, 291908, 292273, 292639, 293004,
        293369, 293734, 294100, 294465, 294830, 295195, 295560, 295926, 296291, 296656,
        297021, 297387, 297752, 298117, 298482, 298848, 299213, 299578, 299943, 300309,
        300674, 301039, 301404, 301770, 302135, 302500, 302865, 303231, 303596, 303961,
        304326, 304692, 305057, 305422, 305787, 306153, 306518, 306883, 307248, 307613,
        307979, 308344, 308709, 309074, 309440, 309805, 310170, 310535, 310901, 311266,
        311631, 311996, 312362, 312727, 313092, 313457, 313823, 314188, 314553, 314918,
        315284, 315649, 316014, 316379, 316745, 317110, 317475, 317840, 318206, 318571,
        318936, 319301, 319666, 320032, 320397, 320762, 321127, 321493, 321858, 322223,
        322588, 322954, 323319, 323684, 324049, 324415, 324780, 325145, 325510, 325876,
        326241, 326606, 326971, 327337, 327702, 328067, 328432, 328798, 329163, 329528,
        329893, 330259, 330624, 330989, 331354, 331719, 332085, 332450, 332815, 333180,
        333546, 333911, 334276, 334641, 335007, 335372, 335737, 336102, 336468, 336833,
        337198, 337563, 337929, 338294, 338659, 339024, 339390, 339755, 340120, 340485,
        340851, 341216, 341581, 341946, 342312, 342677, 343042, 343407, 343772, 344138,
        344503, 344868, 345233, 345599, 345964, 346329, 346694, 347060, 347425, 347790,
        348155, 348521, 348886
    };

    /**
     * <p>Represents the Bahai era. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die Bahai-&Auml;ra. </p>
     */
    @FormattableElement(format = "G", dynamic = true)
    public static final ChronoElement<BadiEra> ERA =
        new StdEnumDateElement<BadiEra, BadiCalendar>("ERA", BadiCalendar.class, BadiEra.class, 'G') {
            @Override
            protected TextAccessor accessor(
                AttributeQuery attributes,
                OutputContext outputContext,
                boolean leap
            ) {
                Locale lang = attributes.get(Attributes.LANGUAGE, Locale.ROOT);
                TextWidth width = attributes.get(Attributes.TEXT_WIDTH, TextWidth.WIDE);
                return BadiEra.accessor(lang, width);
            }
        };

    /**
     * <p>Represents the proleptic year of era (relative to gregorian year 1844). </p>
     *
     * <p>Note that this kind of year definition which counts years since the Bahai era deviates from the.
     * 19-cycle. For the standard way to count years see the elements {@link #KULL_I_SHAI}, {@link #VAHID}
     * and {@link #YEAR_OF_VAHID}. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert das proleptische Jahr seit der Bahai-&Auml;ra (1844). </p>
     *
     * <p>Achtung: Diese Jahresdefinition weicht vom 19er-Zyklus ab. Der Standardweg verwendet stattdessen
     * die Elemente {@link #KULL_I_SHAI}, {@link #VAHID} und {@link #YEAR_OF_VAHID}. </p>
     */
    @FormattableElement(format = "Y", alt = "y", dynamic = true)
    public static final StdCalendarElement<Integer, BadiCalendar> YEAR_OF_ERA =
        new StdIntegerDateElement<>("YEAR_OF_ERA", BadiCalendar.class, 1, 3 * 361, 'Y');

    /**
     * <p>Represents the major cycle (kull-i-shai). </p>
     *
     * <p>This calendar supports the values 1-3. However, only the first major cycle can be interpreted as safe
     * while the higher values are an astronomic approximation. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den Hauptzyklus (kull-i-shai). </p>
     *
     * <p>Dieser Kalender unterst&uuml;tzt die Werte 1-3. Allerdings kann nur der erste Hauptzyklus als
     * gesichert angesehen werden. Die h&ouml;heren Werte sind lediglich eine astronomische Ann&auml;herung. </p>
     */
    @FormattableElement(format = "K", alt = "k", dynamic = true)
    public static final ChronoElement<Integer> KULL_I_SHAI =
        new StdIntegerDateElement<BadiCalendar>("KULL_I_SHAI", BadiCalendar.class, 1, 3, 'K') {
            @Override
            public String getDisplayName(Locale language) {
                return CalendarText.getInstance("bahai", language).getTextForms().get("K");
            }
        };

    /**
     * <p>Represents the vahid cycle which consists of 19 years. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den Vahid-Zyklus, der aus 19 Jahren besteht. </p>
     */
    @FormattableElement(format = "V", alt = "v", dynamic = true)
    public static final StdCalendarElement<Integer, BadiCalendar> VAHID =
        new StdIntegerDateElement<BadiCalendar>("VAHID", BadiCalendar.class, 1, 19, 'V') {
            @Override
            public String getDisplayName(Locale language) {
                return CalendarText.getInstance("bahai", language).getTextForms().get("V");
            }
        };

    /**
     * <p>Represents the year of vahid cycle. </p>
     *
     * <p>The dynamic pattern symbol X or x will print the year of vahid either as text (big symbol letter)
     * or as number (small symbol letter). </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert das Jahr des assoziierten Vahid-Zyklus. </p>
     *
     * <p>Das dynamische Formatmustersymbol X oder x wird dieses Jahr entweder als Text (Gro&szlig;buchstabe)
     * oder als Zahl (Kleinbuchstabe) formatieren. </p>
     */
    @FormattableElement(format = "X", alt = "x", dynamic = true)
    public static final TextElement<Integer> YEAR_OF_VAHID = YOV.SINGLETON;

    /**
     * <p>Represents the month if available. </p>
     *
     * <p><strong>Warning:</strong> A Badi date does not always have a month. If the
     * date is an intercalary day (Ayyam-i-Ha) then any access via {@code get(MONTH_OF_YEAR)}
     * to this element will be rejected by raising an exception. Users have first to make sure
     * that the date is not such an intercalary day. </p>
     *
     * <p>However, it is always possible to query the date for the minimum or maximum month
     * or to set the date to a month-related day even if the actual date is an intercalary day. </p>
     *
     * @see     #isIntercalaryDay()
     * @see     #hasMonth()
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den Monat, wenn vorhanden. </p>
     *
     * <p><strong>Warnung:</strong> Ein Badi-Datum hat nicht immer einen Monat. Wenn
     * es einen Erg&auml;nzungstag darstellt (Ayyam-i-Ha), dann wird jeder Zugriff per {@code get(MONTH_OF_YEAR)}
     * auf dieses Element mit einer Ausnahme quittiert. Anwender m&uuml;ssen zuerst sicherstellen, da&szlig; das
     * Datum kein solcher Erg&auml;nzungstag ist. </p>
     *
     * <p>Allerdings ist es immer m&ouml;glich, das aktuelle Datum nach dem minimalen oder maximalen
     * Monat zu fragen oder das aktuelle Datum auf einen monatsbezogenen Tag zu setzen,
     * selbst wenn das Datum ein Erg&auml;nzungstag ist. </p>
     *
     * @see     #isIntercalaryDay()
     * @see     #hasMonth()
     */
    @FormattableElement(format = "M", alt = "m", dynamic = true)
    public static final StdCalendarElement<BadiMonth, BadiCalendar> MONTH_OF_YEAR = MonthElement.SINGLETON;

    /**
     * <p>Represents the period of intercalary days if available. </p>
     *
     * <p><strong>Warning:</strong> A Badi date often does not have such a period. If the
     * date is not an intercalary day (Ayyam-i-Ha) then any access via {@code get(AYYAM_I_HA)}
     * to this element will be rejected by raising an exception. Users have first to make sure
     * that the date is such an intercalary day. </p>
     *
     * <p>This element cannot be formatted in a numeric way but only as text. Therefore the dynamic
     * format symbol A is only permitted as big letter. </p>
     *
     * @see     #isIntercalaryDay()
     * @see     #hasMonth()
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die Erg&auml;nzungstage, wenn vorhanden. </p>
     *
     * <p><strong>Warnung:</strong> Ein Badi-Datum liegt oft nicht auf einem Erg&auml;nzungstag.
     * Wenn es nicht einen Erg&auml;nzungstag darstellt (Ayyam-i-Ha), dann wird jeder Zugriff per
     * {@code get(AYYAM_I_HA)} auf dieses Element mit einer Ausnahme quittiert. Anwender m&uuml;ssen
     * zuerst sicherstellen, da&szlig; das Datum ein solcher Erg&auml;nzungstag ist. </p>
     *
     * <p>Dieses Element kann nicht numerisch formatiert werden, sondern nur als Text. Deshalb ist
     * das dynamische Formatmustersymbol A nur als Gro&szlig;buchstabe zul&auml;ssig. </p>
     *
     * @see     #isIntercalaryDay()
     * @see     #hasMonth()
     */
    @FormattableElement(format = "A", dynamic = true)
    public static final ChronoElement<BadiIntercalaryDays> AYYAM_I_HA = IntercalaryAccess.SINGLETON;

    /**
     * <p>Represents the day of month or an intercalary day. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den Tag des Monats oder einen Erg&auml;nzungstag. </p>
     */
    @FormattableElement(format = "D", alt = "d", dynamic = true)
    public static final StdCalendarElement<Integer, BadiCalendar> DAY_OF_DIVISION =
        new StdIntegerDateElement<>("DAY_OF_DIVISION", BadiCalendar.class, 1, 19, 'D');

    /**
     * <p>Represents the day of year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den Tag des Jahres. </p>
     */
    public static final StdCalendarElement<Integer, BadiCalendar> DAY_OF_YEAR =
        new StdIntegerDateElement<>("DAY_OF_YEAR", BadiCalendar.class, 1, 365, '\u0000');

    /**
     * <p>Represents the day of week. </p>
     *
     * <p>If the day-of-week is set to a new value then Time4J handles the calendar week
     * as starting on Saturday. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den Tag der Woche. </p>
     *
     * <p>Wenn der Tag der Woche auf einen neuen Wert gesetzt wird, behandelt Time4J die
     * Kalenderwoche so, da&szlig; sie am Samstag beginnt. </p>
     */
    @FormattableElement(format = "E", dynamic = true)
    public static final StdCalendarElement<Weekday, BadiCalendar> DAY_OF_WEEK = DowElement.SINGLETON;

    private static final CalendarSystem<BadiCalendar> CALSYS;
    private static final TimeAxis<BadiCalendar.Unit, BadiCalendar> ENGINE;

    static {
        CALSYS = new Transformer();

        TimeAxis.Builder<BadiCalendar.Unit, BadiCalendar> builder =
            TimeAxis.Builder.setUp(
                BadiCalendar.Unit.class,
                BadiCalendar.class,
                new Merger(),
                CALSYS)
            .appendElement(
                ERA,
                new EraRule())
            .appendElement(
                YEAR_OF_ERA,
                new IntegerRule(YOE_INDEX),
                Unit.YEARS)
            .appendElement(
                KULL_I_SHAI,
                new IntegerRule(KULL_I_SHAI_INDEX))
            .appendElement(
                VAHID,
                new IntegerRule(VAHID_INDEX),
                Unit.VAHID_CYCLES)
            .appendElement(
                YEAR_OF_VAHID,
                new IntegerRule(YEAR_INDEX),
                Unit.YEARS)
            .appendElement(
                MONTH_OF_YEAR,
                new MonthRule(),
                Unit.MONTHS)
            .appendElement(
                AYYAM_I_HA,
                IntercalaryAccess.SINGLETON)
            .appendElement(
                DAY_OF_DIVISION,
                new IntegerRule(DAY_OF_DIVISION_INDEX),
                Unit.DAYS)
            .appendElement(
                DAY_OF_YEAR,
                new IntegerRule(DAY_OF_YEAR_INDEX),
                Unit.DAYS)
            .appendElement(
                DAY_OF_WEEK,
                new WeekdayRule(),
                Unit.DAYS)
            .appendUnit(
                Unit.VAHID_CYCLES,
                new FUnitRule(Unit.VAHID_CYCLES),
                Unit.VAHID_CYCLES.getLength(),
                Collections.singleton(Unit.YEARS))
            .appendUnit(
                Unit.YEARS,
                new FUnitRule(Unit.YEARS),
                Unit.YEARS.getLength(),
                Collections.singleton(Unit.VAHID_CYCLES))
            .appendUnit(
                Unit.MONTHS,
                new FUnitRule(Unit.MONTHS),
                Unit.MONTHS.getLength())
            .appendUnit(
                Unit.WEEKS,
                new FUnitRule(Unit.WEEKS),
                Unit.WEEKS.getLength(),
                Collections.singleton(Unit.DAYS))
            .appendUnit(
                Unit.DAYS,
                new FUnitRule(Unit.DAYS),
                Unit.DAYS.getLength(),
                Collections.singleton(Unit.WEEKS));
        ENGINE = builder.build();
    }

    private static final long serialVersionUID = 7091925253640345123L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final int major;
    private transient final int cycle;
    private transient final int year;
    private transient final int division;
    private transient final int day;

    //~ Konstruktoren -----------------------------------------------------

    private BadiCalendar(
        int major,
        int cycle,
        int year,
        int division,
        int day
    ) {
        super();

        this.major = major;
        this.cycle = cycle;
        this.year = year;
        this.division = division;
        this.day = day;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new instance of a Badi calendar date. </p>
     *
     * @param   kullishay       major cycle of 361 years (only values 1, 2 or 3 are permitted)
     * @param   vahid           19-year-cycle (in range 1-19)
     * @param   yearOfVahid     year in range 1-19
     * @param   division        either {@code BadiMonth} or {@code BadiIntercalaryDays}
     * @param   day             day in range 1-19 (1-4/5 in case of Ayyam-i-Ha)
     * @return  new instance of {@code BadiCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Badi-Kalenderdatum. </p>
     *
     * @param   kullishay       major cycle of 361 years (only values 1, 2 or 3 are permitted)
     * @param   vahid           19-year-cycle (in range 1-19)
     * @param   yearOfVahid     year in range 1-19
     * @param   division        either {@code BadiMonth} or {@code BadiIntercalaryDays}
     * @param   day             day in range 1-19 (1-4/5 in case of Ayyam-i-Ha)
     * @return  new instance of {@code BadiCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    public static BadiCalendar ofComplete(
        int kullishay,
        int vahid,
        int yearOfVahid,
        BadiDivision division,
        int day
    ) {

        if ((kullishay < 1) || (kullishay > 3)) {
            throw new IllegalArgumentException("Major cycle (kull-i-shai) out of range 1-3: " + kullishay);
        } else if ((vahid < 1) || (vahid > 19)) {
            throw new IllegalArgumentException("Vahid cycle out of range 1-19: " + vahid);
        } else if ((yearOfVahid < 1) || (yearOfVahid > 19)) {
            throw new IllegalArgumentException("Year of vahid out of range 1-19: " + yearOfVahid);
        } else if (division instanceof BadiMonth) {
            if ((day < 1) || (day > 19)) {
                throw new IllegalArgumentException("Day out of range 1-19: " + day);
            } else {
                return new BadiCalendar(kullishay, vahid, yearOfVahid, BadiMonth.class.cast(division).getValue(), day);
            }
        } else if (division == BadiIntercalaryDays.AYYAM_I_HA) {
            int max = isLeapYear(kullishay, vahid, yearOfVahid) ? 5 : 4;
            if ((day < 1) || (day > max)) {
                throw new IllegalArgumentException("Day out of range 1-" + max + ": " + day);
            } else {
                return new BadiCalendar(kullishay, vahid, yearOfVahid, 0, day);
            }
        } else if (division == null) {
            throw new NullPointerException("Missing Badi month or Ayyam-i-Ha.");
        } else {
            throw new IllegalArgumentException("Invalid implementation of Badi division: " + division);
        }

    }

    /**
     * <p>Creates a new instance of a Badi calendar date. </p>
     *
     * @param   era             Bahai era
     * @param   yearOfEra       year of era in range 1-1083
     * @param   division        either {@code BadiMonth} or {@code BadiIntercalaryDays}
     * @param   day             day in range 1-19 (1-4/5 in case of Ayyam-i-Ha)
     * @return  new instance of {@code BadiCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Badi-Kalenderdatum. </p>
     *
     * @param   era             Bahai era
     * @param   yearOfEra       year of era in range 1-1083
     * @param   division        either {@code BadiMonth} or {@code BadiIntercalaryDays}
     * @param   day             day in range 1-19 (1-4/5 in case of Ayyam-i-Ha)
     * @return  new instance of {@code BadiCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    public static BadiCalendar ofComplete(
        BadiEra era,
        int yearOfEra,
        BadiDivision division,
        int day
    ) {

        if (era == null) {
            throw new NullPointerException("Missing Bahai era.");
        } else if ((yearOfEra < 1) || (yearOfEra > 1083)) {
            throw new IllegalArgumentException("Year of era out of range 1-1083: " + yearOfEra);
        }

        BadiCalendar prototype = BadiCalendar.axis().getMinimum().with(YEAR_OF_ERA, yearOfEra);
        int kullishay = prototype.getKullishai();
        int vahid = prototype.getVahid();
        int yearOfVahid = prototype.getYearOfVahid();

        if (division instanceof BadiMonth) {
            if ((day < 1) || (day > 19)) {
                throw new IllegalArgumentException("Day out of range 1-19: " + day);
            } else {
                return new BadiCalendar(kullishay, vahid, yearOfVahid, BadiMonth.class.cast(division).getValue(), day);
            }
        } else if (division == BadiIntercalaryDays.AYYAM_I_HA) {
            int max = isLeapYear(kullishay, vahid, yearOfVahid) ? 5 : 4;
            if ((day < 1) || (day > max)) {
                throw new IllegalArgumentException("Day out of range 1-" + max + ": " + day);
            } else {
                return new BadiCalendar(kullishay, vahid, yearOfVahid, 0, day);
            }
        } else if (division == null) {
            throw new NullPointerException("Missing Badi month or Ayyam-i-Ha.");
        } else {
            throw new IllegalArgumentException("Invalid implementation of Badi division: " + division);
        }

    }

    /**
     * <p>Creates a new instance of a Badi calendar date in first major cycle (gregorian years 1844-2204). </p>
     *
     * @param   vahid           19-year-cycle (in range 1-19)
     * @param   yearOfVahid     year in range 1-19
     * @param   month           Badi month
     * @param   day             day in range 1-19
     * @return  new instance of {@code BadiCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Badi-Datum im ersten Hauptzyklus (gregorianische Jahre 1844-2204). </p>
     *
     * @param   vahid           19-year-cycle (in range 1-19)
     * @param   yearOfVahid     year in range 1-19
     * @param   month           Badi month
     * @param   day             day in range 1-19
     * @return  new instance of {@code BadiCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    public static BadiCalendar of(
        int vahid,
        int yearOfVahid,
        BadiMonth month,
        int day
    ) {

        return BadiCalendar.ofComplete(1, vahid, yearOfVahid, month, day);

    }

    /**
     * <p>Creates a new instance of a Badi calendar date in first major cycle (gregorian years 1844-2204). </p>
     *
     * @param   vahid           19-year-cycle (in range 1-19)
     * @param   yearOfVahid     year in range 1-19
     * @param   month           month in range 1-19
     * @param   day             day in range 1-19
     * @return  new instance of {@code BadiCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Badi-Datum im ersten Hauptzyklus (gregorianische Jahre 1844-2204). </p>
     *
     * @param   vahid           19-year-cycle (in range 1-19)
     * @param   yearOfVahid     year in range 1-19
     * @param   month           month in range 1-19
     * @param   day             day in range 1-19
     * @return  new instance of {@code BadiCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    public static BadiCalendar of(
        int vahid,
        int yearOfVahid,
        int month,
        int day
    ) {

        return BadiCalendar.ofComplete(1, vahid, yearOfVahid, BadiMonth.valueOf(month), day);

    }

    /**
     * <p>Creates a new instance of a Badi calendar date in first major cycle (gregorian years 1844-2204). </p>
     *
     * @param   vahid           19-year-cycle (in range 1-19)
     * @param   yearOfVahid     year in range 1-19
     * @param   day             day in range 1-4/5
     * @return  new instance of {@code BadiCalendar} in the Ayyam-i-Ha-period
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Badi-Datum im ersten Hauptzyklus (gregorianische Jahre 1844-2204). </p>
     *
     * @param   vahid           19-year-cycle (in range 1-19)
     * @param   yearOfVahid     year in range 1-19
     * @param   day             day in range 1-4/5
     * @return  new instance of {@code BadiCalendar} in the Ayyam-i-Ha-period
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    public static BadiCalendar ofIntercalary(
        int vahid,
        int yearOfVahid,
        int day
    ) {

        return BadiCalendar.ofComplete(1, vahid, yearOfVahid, BadiIntercalaryDays.AYYAM_I_HA, day);

    }

    /**
     * <p>Obtains the current calendar date in system time. </p>
     *
     * <p>Convenient short-cut for: {@code SystemClock.inLocalView().now(BadiCalendar.axis())}. </p>
     *
     * @return  current calendar date in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(Chronology)
     */
    /*[deutsch]
     * <p>Ermittelt das aktuelle Kalenderdatum in der Systemzeit. </p>
     *
     * <p>Bequeme Abk&uuml;rzung f&uuml;r:
     * {@code SystemClock.inLocalView().now(BadiCalendar.axis())}. </p>
     *
     * @return  current calendar date in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(net.time4j.engine.Chronology)
     */
    public static BadiCalendar nowInSystemTime() {

        return SystemClock.inLocalView().now(BadiCalendar.axis());

    }

    /**
     * <p>Yields the major cycle (kull-i-shai) which is 361 years long. </p>
     *
     * @return  int
     * @see     #KULL_I_SHAI
     */
    /*[deutsch]
     * <p>Liefert den Hauptzyklus (kull-i-shai), der 361 Jahre lang ist. </p>
     *
     * @return  int
     * @see     #KULL_I_SHAI
     */
    public int getKullishai() {

        return this.major;

    }

    /**
     * <p>Yields the 19-year-cycle (vahid = unity). </p>
     *
     * @return  int
     * @see     #VAHID
     */
    /*[deutsch]
     * <p>Liefert den 19-Jahre-Zyklus (vahid = Einheit). </p>
     *
     * @return  int
     * @see     #VAHID
     */
    public int getVahid() {

        return this.cycle;

    }

    /**
     * <p>Yields the Badi year related to the vahid cycle. </p>
     *
     * @return  int (1-19)
     * @see     #YEAR_OF_VAHID
     */
    /*[deutsch]
     * <p>Liefert das Badi-Jahr des aktuellen Vahid-Zyklus. </p>
     *
     * @return  int (1-19)
     * @see     #YEAR_OF_VAHID
     */
    public int getYearOfVahid() {

        return this.year;

    }

    /**
     * <p>Yields the proleptic Badi year related to the Bahai era. </p>
     *
     * @return  int (1-1083)
     * @see     #YEAR_OF_ERA
     */
    /*[deutsch]
     * <p>Liefert das proleptische Badi-Jahr relativ zur Bahai-&Auml;ra. </p>
     *
     * @return  int (1-1083)
     * @see     #YEAR_OF_ERA
     */
    public int getYearOfEra() {

        return this.getRelatedGregorianYear() - 1843;

    }

    /**
     * <p>Yields the Badi month if available. </p>
     *
     * @return  month enum
     * @throws  ChronoException if this date is an intercalary day (Ayyam-i-Ha)
     * @see     #MONTH_OF_YEAR
     * @see     #isIntercalaryDay()
     * @see     #hasMonth()
     */
    /*[deutsch]
     * <p>Liefert den Badi-Monat, wenn vorhanden. </p>
     *
     * @return  month enum
     * @throws  ChronoException if this date is an intercalary day (Ayyam-i-Ha)
     * @see     #MONTH_OF_YEAR
     * @see     #isIntercalaryDay()
     * @see     #hasMonth()
     */
    public BadiMonth getMonth() {

        if (this.division == 0) {
            throw new ChronoException(
                "Intercalary days (Ayyam-i-Ha) do not represent any month: " + this.toString());
        }

        return BadiMonth.valueOf(this.division);

    }

    /**
     * Obtains either the month or the Ayyam-i-Ha-period.
     *
     * @return  BadiDivision
     */
    /*[deutsch]
     * Liefert entweder den Monat oder die Ayyam-i-Ha-Periode.
     *
     * @return  BadiDivision
     */
    public BadiDivision getDivision() {

        return this.isIntercalaryDay() ? BadiIntercalaryDays.AYYAM_I_HA : this.getMonth();

    }

    /**
     * <p>Yields the day of either Badi month or Ayyam-i-Ha. </p>
     *
     * @return  int (1-4/5/19)
     * @see     #DAY_OF_DIVISION
     */
    /*[deutsch]
     * <p>Liefert den Tag des Badi-Monats oder der Ayyam-i-Ha-Periode. </p>
     *
     * @return  int (1-4/5/19)
     * @see     #DAY_OF_DIVISION
     */
    public int getDayOfDivision() {

        return this.day;

    }

    /**
     * <p>Determines the day of standard-week (with seven days). </p>
     *
     * @return  Weekday
     * @see     #DAY_OF_WEEK
     */
    /*[deutsch]
     * <p>Ermittelt den Wochentag bezogen auf eine 7-Tage-Woche. </p>
     *
     * @return  Weekday
     * @see     #DAY_OF_WEEK
     */
    public Weekday getDayOfWeek() {

        long utcDays = CALSYS.transform(this);
        return Weekday.valueOf(MathUtils.floorModulo(utcDays + 5, 7) + 1);

    }

    /**
     * <p>Yields the day of year. </p>
     *
     * @return  int
     * @see     #DAY_OF_YEAR
     */
    /*[deutsch]
     * <p>Liefert den Tag des Jahres. </p>
     *
     * @return  int
     * @see     #DAY_OF_YEAR
     */
    public int getDayOfYear() {

        switch (this.division) {
            case 0:
                return 18 * 19 + this.day;
            case 19:
                return 18 * 19 + (this.isLeapYear() ? 5 : 4) + this.day;
            default:
                return (this.division - 1) * 19 + this.day;
        }

    }

    /**
     * <p>Is this date an intercalary day? </p>
     *
     * <p>A date in the Badi calendar has either a month or is an intercalary day. </p>
     *
     * @return  boolean
     * @see     #hasMonth()
     * @see     BadiIntercalaryDays#AYYAM_I_HA
     */
    /*[deutsch]
     * <p>Liegt dieses Datum auf einem Erg&auml;nzungstag? </p>
     *
     * <p>Ein Datum im Badi-Kalender hat entweder einen Monat
     * oder ist ein Erg&auml;nzungstag (eingeschobener Tag). </p>
     *
     * @return  boolean
     * @see     #hasMonth()
     * @see     BadiIntercalaryDays#AYYAM_I_HA
     */
    public boolean isIntercalaryDay() {

        return (this.division == 0);

    }

    /**
     * <p>Does this date contain a month? </p>
     *
     * <p>A date in the Badi calendar has either a month or is an intercalary day. </p>
     *
     * @return  boolean
     * @see     #isIntercalaryDay()
     */
    /*[deutsch]
     * <p>Liegt dieses Datum in einem Monat? </p>
     *
     * <p>Ein Datum im Badi-Kalender hat entweder einen Monat
     * oder ist ein Erg&auml;nzungstag (eingeschobener Tag). </p>
     *
     * @return  boolean
     * @see     #isIntercalaryDay()
     */
    public boolean hasMonth() {

        return (this.division > 0);

    }

    /**
     * <p>Is the year of this date a leap year? </p>
     *
     * @return  boolean
     */
    /*[deutsch]
     * <p>Liegt dieses Datum in einem Schaltjahr? </p>
     *
     * @return  boolean
     */
    public boolean isLeapYear() {

        return isLeapYear(this.major, this.cycle, this.year);

    }

    /**
     * <p>Is given Badi year a leap year? </p>
     *
     * @param   kullishay       major cycle of 361 years (only values 1, 2 or 3 are permitted)
     * @param   vahid           19-year-cycle (in range 1-19)
     * @param   yearOfVahid     year in range 1-19
     * @return  boolean
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    /*[deutsch]
     * <p>Ist das angegebene Badi-Jahr ein Schaltjahr? </p>
     *
     * @param   kullishay       major cycle of 361 years (only values 1, 2 or 3 are permitted)
     * @param   vahid           19-year-cycle (in range 1-19)
     * @param   yearOfVahid     year in range 1-19
     * @return  boolean
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    public static boolean isLeapYear(
        int kullishay,
        int vahid,
        int yearOfVahid
    ) {

        if ((kullishay < 1) || (kullishay > 3)) {
            throw new IllegalArgumentException("Major cycle (kull-i-shai) out of range 1-3: " + kullishay);
        } else if ((vahid < 1) || (vahid > 19)) {
            throw new IllegalArgumentException("Vahid cycle out of range 1-19: " + vahid);
        } else if ((yearOfVahid < 1) || (yearOfVahid > 19)) {
            throw new IllegalArgumentException("Year out of range 1-19: " + yearOfVahid);
        }

        int relgregyear = getRelatedGregorianYear(kullishay, vahid, yearOfVahid);

        if (relgregyear < 2015) {
            return GregorianMath.isLeapYear(relgregyear + 1);
        } else {
            int index = relgregyear - 2015;
            return (NEWROZ[index + 1] - NEWROZ[index] == 366);
        }

    }

    /**
     * <p>Queries if given parameter values form a well defined calendar date. </p>
     *
     * @param   kullishay       major cycle of 361 years (only values 1, 2 or 3 are permitted)
     * @param   vahid           19-year-cycle (in range 1-19)
     * @param   yearOfVahid     year in range 1-19
     * @param   division        either {@code BadiMonth} or {@code BadiIntercalaryDays}
     * @param   day             day in range 1-19 (1-4/5 in case of Ayyam-i-Ha)
     * @return  {@code true} if valid else  {@code false}
     * @see     #ofComplete(int, int, int, BadiDivision, int)
     */
    /*[deutsch]
     * <p>Pr&uuml;ft, ob die angegebenen Parameter ein wohldefiniertes Kalenderdatum beschreiben. </p>
     *
     * @param   kullishay       major cycle of 361 years (only values 1, 2 or 3 are permitted)
     * @param   vahid           19-year-cycle (in range 1-19)
     * @param   yearOfVahid     year in range 1-19
     * @param   division        either {@code BadiMonth} or {@code BadiIntercalaryDays}
     * @param   day             day in range 1-19 (1-4/5 in case of Ayyam-i-Ha)
     * @return  {@code true} if valid else  {@code false}
     * @see     #ofComplete(int, int, int, BadiDivision, int)
     */
    public static boolean isValid(
        int kullishay,
        int vahid,
        int yearOfVahid,
        BadiDivision division,
        int day
    ) {

        if ((kullishay < 1) || (kullishay > 3)) {
            return false;
        } else if ((vahid < 1) || (vahid > 19)) {
            return false;
        } else if ((yearOfVahid < 1) || (yearOfVahid > 19)) {
            return false;
        }

        if (division instanceof BadiMonth) {
            return ((day >= 1) && (day <= 19));
        } else if (division == BadiIntercalaryDays.AYYAM_I_HA) {
            return ((day >= 1) && (day <= (isLeapYear(kullishay, vahid, yearOfVahid) ? 5 : 4)));
        } else {
            return false;
        }

    }

    /**
     * <p>Creates a new local timestamp with this date and given wall time. </p>
     *
     * <p>If the time {@link PlainTime#midnightAtEndOfDay() T24:00} is used
     * then the resulting timestamp will automatically be normalized such
     * that the timestamp will contain the following day instead. </p>
     *
     * @param   time    wall time
     * @return  general timestamp as composition of this date and given time
     */
    /*[deutsch]
     * <p>Erzeugt einen allgemeinen Zeitstempel mit diesem Datum und der angegebenen Uhrzeit. </p>
     *
     * <p>Wenn {@link PlainTime#midnightAtEndOfDay() T24:00} angegeben wird,
     * dann wird der Zeitstempel automatisch so normalisiert, da&szlig; er auf
     * den n&auml;chsten Tag verweist. </p>
     *
     * @param   time    wall time
     * @return  general timestamp as composition of this date and given time
     */
    public GeneralTimestamp<BadiCalendar> at(PlainTime time) {

        return GeneralTimestamp.of(this, time);

    }

    /**
     * <p>Is equivalent to {@code at(PlainTime.of(hour, minute))}. </p>
     *
     * @param   hour        hour of day in range (0-24)
     * @param   minute      minute of hour in range (0-59)
     * @return  general timestamp as composition of this date and given time
     * @throws  IllegalArgumentException if any argument is out of range
     */
    /*[deutsch]
     * <p>Entspricht {@code at(PlainTime.of(hour, minute))}. </p>
     *
     * @param   hour        hour of day in range (0-24)
     * @param   minute      minute of hour in range (0-59)
     * @return  general timestamp as composition of this date and given time
     * @throws  IllegalArgumentException if any argument is out of range
     */
    public GeneralTimestamp<BadiCalendar> atTime(
        int hour,
        int minute
    ) {

        return this.at(PlainTime.of(hour, minute));

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof BadiCalendar) {
            BadiCalendar that = (BadiCalendar) obj;
            return (
                (this.major == that.major)
                    && (this.cycle == that.cycle)
                    && (this.year == that.year)
                    && (this.division == that.division)
                    && (this.day == that.day));
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return (361 * this.major + 19 * this.cycle + this.year) * 512 + this.division * 19 + this.day;

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(32);
        sb.append("Bahai-");
        sb.append(this.major);
        sb.append('-');
        sb.append(this.cycle);
        sb.append('-');
        sb.append(this.year);
        sb.append('-');
        if (this.division == 0) {
            sb.append("Ayyam-i-Ha-");
        } else {
            sb.append(this.division);
            sb.append('-');
        }
        sb.append(this.day);
        return sb.toString();

    }

    @Override
    public boolean contains(ChronoElement<?> element) {

        if (element == MONTH_OF_YEAR) {
            return this.hasMonth();
        } else if (element == AYYAM_I_HA) {
            return this.isIntercalaryDay();
        } else if (this.getRegisteredElements().contains(element)) {
            return true;
        }

        // external element
        return isAccessible(this, element);

    }

    @Override
    public <V> boolean isValid(
        ChronoElement<V> element,
        V value
    ) {

        if ((element == MONTH_OF_YEAR) || (element == AYYAM_I_HA) || (element == ERA)) {
            return (value != null);
        }

        return super.isValid(element, value);

    }

    /**
     * <p>Returns the associated time axis. </p>
     *
     * @return  chronology
     */
    /*[deutsch]
     * <p>Liefert die zugeh&ouml;rige Zeitachse. </p>
     *
     * @return  chronology
     */
    public static TimeAxis<Unit, BadiCalendar> axis() {

        return ENGINE;

    }

    @Override
    protected TimeAxis<Unit, BadiCalendar> getChronology() {

        return ENGINE;

    }

    @Override
    protected BadiCalendar getContext() {

        return this;

    }

    /*
     * Obtains the standard week model of this calendar.
     *
     * This calendar starts on Saturday (more precisely at sunset on Friday). The weekend
     * is neither standardized nor used, therefore this method is kept private.
     */
    private static Weekmodel getDefaultWeekmodel() {

        return Weekmodel.of(Weekday.SATURDAY, 1, Weekday.SATURDAY, Weekday.SUNDAY);

    }

    private int getRelatedGregorianYear() {

        return getRelatedGregorianYear(this.major, this.cycle, this.year);

    }

    private static int getRelatedGregorianYear(
        int kullishay,
        int vahid,
        int yearOfVahid
    ) {

        return (kullishay - 1) * 361 + (vahid - 1) * 19 + yearOfVahid + 1843;

    }

    private BadiCalendar withDayOfYear(int value) {

        int pDiv;
        int pDay;

        if (value <= 18 * 19) {
            pDiv = ((value - 1) / 19) + 1;
            pDay = ((value - 1) % 19) + 1;
        } else if (value <= 18 * 19 + (this.isLeapYear() ? 5 : 4)) {
            pDiv = 0;
            pDay = value - 18 * 19;
        } else {
            pDiv = 19;
            pDay = value - (this.isLeapYear() ? 5 : 4) - 18 * 19;
        }

        return new BadiCalendar(this.major, this.cycle, this.year, pDiv, pDay);

    }

    private static <V> boolean isAccessible(
        BadiCalendar cal,
        ChronoElement<V> element
    ) {

        try {
            return cal.isValid(element, cal.get(element));
        } catch (ChronoException ex) {
            return false;
        }

    }

    private static Locale getLocale(AttributeQuery attributes) {

        return attributes.get(Attributes.LANGUAGE, Locale.ROOT);

    }

    private static FormattedContent getFormattedContent(AttributeQuery attributes) {

        return attributes.get(TEXT_CONTENT_ATTRIBUTE, FormattedContent.TRANSCRIPTION);

    }

    /**
     * @serialData  Uses <a href="../../../../serialized-form.html#net.time4j.calendar.bahai/SPX">
     *              a dedicated serialization form</a> as proxy. The first byte contains
     *              the type-ID {@code 19}. Then the kull-i-shai-cycle, the vahid-cycle, the year of vahid
     *              and finally the month and day-of-month as bytes. Ayyam-i-Ha will be modelled as zero.
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.BAHAI);

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
     * <p>Defines come calendar units for the Badi calendar. </p>
     */
    /*[deutsch]
     * <p>Definiert einige kalendarische Zeiteinheiten f&uuml;r den Badi-Kalender. </p>
     */
    public static enum Unit
        implements ChronoUnit {

        //~ Statische Felder/Initialisierungen ----------------------------

        /**
         * <p>Cycles which last each 19 years. </p>
         */
        /*[deutsch]
         * <p>Jahreszyklen zu je 19 Jahren. </p>
         */
        VAHID_CYCLES(19 * 365.2424 * 86400.0),

        /**
         * <p>Years are defined as vernal equinox years and not as tropical years. </p>
         */
        /*[deutsch]
         * <p>Jahre starten zum Fr&uuml;hlingspunkt und sind nicht als tropische Jahre definiert. </p>
         */
        YEARS(365.2424 * 86400.0),

        /**
         * <p>The month arithmetic handles the intercalary days (Ayyam-i-Ha) as extension of eighteenth month. </p>
         */
        /*[deutsch]
         * <p>Die Monatsarithmetik behandelt die Erg&auml;nzungstage (Ayyam-i-Ha) als eine Erweiterung
         * des achtzehnten Monats. </p>
         */
        MONTHS(19 * 86400.0),

        /**
         * <p>Weeks consist of seven days. </p>
         */
        /*[deutsch]
         * <p>Wochen bestehen aus sieben Tagen. </p>
         */
        WEEKS(7 * 86400.0),

        /**
         * <p>The universal day unit. </p>
         */
        /*[deutsch]
         * <p>Die universelle Tageseinheit. </p>
         */
        DAYS(86400.0);

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

            return true;

        }

        /**
         * <p>Calculates the difference between given calendar dates in this unit. </p>
         *
         * @param   start   start date (inclusive)
         * @param   end     end date (exclusive)
         * @return  difference counted in this unit
         */
        /*[deutsch]
         * <p>Berechnet die Differenz zwischen den angegebenen Datumsparametern in dieser Zeiteinheit. </p>
         *
         * @param   start   start date (inclusive)
         * @param   end     end date (exclusive)
         * @return  difference counted in this unit
         */
        public long between(
            BadiCalendar start,
            BadiCalendar end
        ) {

            return start.until(end, this);

        }

    }

    private static class Transformer
        implements CalendarSystem<BadiCalendar> {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final long EPOCH = PlainDate.of(1844, 3, 21).getDaysSinceEpochUTC();

        //~ Methoden ------------------------------------------------------

        @Override
        public BadiCalendar transform(long utcDays) {

            if (utcDays < EPOCH) {
                throw new IllegalArgumentException("Not defined before Bahai era: " + utcDays);
            } else if (utcDays < NEWROZ[0]) {
                PlainDate date = PlainDate.of(utcDays, EpochDays.UTC);
                int yoe = date.getYear() - 1843;
                int month = date.getMonth();
                if ((month <= 2) || ((month == 3) && (date.getDayOfMonth() < 21))) {
                    yoe--;
                }
                int yov = MathUtils.floorModulo(yoe - 1, 19) + 1;
                int vahid = MathUtils.floorDivide(yoe - 1, 19) + 1;
                BadiCalendar newroz = new BadiCalendar(1, vahid, yov, 1, 1);
                return newroz.withDayOfYear(MathUtils.safeCast(utcDays - this.transform(newroz) + 1));
            } else {
                for (int index = 0, max = NEWROZ.length - 2; index <= max; index++) {
                    if (utcDays < NEWROZ[index + 1]) {
                        int doy = MathUtils.safeCast(utcDays - NEWROZ[index] + 1);
                        int yoe = index + 2015 - 1843;
                        int m = MathUtils.floorDivide(yoe - 1, 361) + 1;
                        int vahid = MathUtils.floorDivide(yoe - (m - 1) * 361 - 1, 19) + 1;
                        int yov = MathUtils.floorModulo(yoe - 1, 19) + 1;
                        BadiCalendar newroz = new BadiCalendar(m, vahid, yov, 1, 1);
                        return newroz.withDayOfYear(doy);
                    }
                }
                throw new IllegalArgumentException("Out of range: " + utcDays);
            }

        }

        @Override
        public long transform(BadiCalendar date) {

            int relgregyear = date.getRelatedGregorianYear();
            int doy = date.getDayOfYear();

            if (relgregyear < 2015) {
                return PlainDate.of(relgregyear, 3, 21).getDaysSinceEpochUTC() + doy - 1;
            } else {
                return NEWROZ[relgregyear - 2015] + doy - 1;
            }

        }

        @Override
        public long getMinimumSinceUTC() {

            return EPOCH;

        }

        @Override
        public long getMaximumSinceUTC() {

            return NEWROZ[NEWROZ.length - 1] - 1;

        }

        @Override
        public List<CalendarEra> getEras() {

            return Collections.singletonList(BadiEra.BAHAI);

        }

    }

    private static class EraRule
        implements ElementRule<BadiCalendar, BadiEra> {

        //~ Methoden ------------------------------------------------------

        @Override
        public BadiEra getValue(BadiCalendar context) {

            return BadiEra.BAHAI;

        }

        @Override
        public BadiEra getMinimum(BadiCalendar context) {

            return BadiEra.BAHAI;

        }

        @Override
        public BadiEra getMaximum(BadiCalendar context) {

            return BadiEra.BAHAI;

        }

        @Override
        public boolean isValid(
            BadiCalendar context,
            BadiEra value
        ) {

            return (value != null);

        }

        @Override
        public BadiCalendar withValue(
            BadiCalendar context,
            BadiEra value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing era value.");
            }

            return context;

        }

        @Override
        public ChronoElement<?> getChildAtFloor(BadiCalendar context) {

            return YEAR_OF_ERA;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(BadiCalendar context) {

            return YEAR_OF_ERA;

        }

    }

    private static class IntegerRule
        implements IntElementRule<BadiCalendar> {

        //~ Instanzvariablen ----------------------------------------------

        private final int index;

        //~ Konstruktoren -------------------------------------------------

        IntegerRule(int index) {
            super();

            this.index = index;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public int getInt(BadiCalendar context) {

            switch (this.index) {
                case KULL_I_SHAI_INDEX:
                    return context.major;
                case VAHID_INDEX:
                    return context.cycle;
                case YEAR_INDEX:
                    return context.year;
                case DAY_OF_DIVISION_INDEX:
                    return context.day;
                case DAY_OF_YEAR_INDEX:
                    return context.getDayOfYear();
                case YOE_INDEX:
                    return context.getYearOfEra();
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public boolean isValid(BadiCalendar context, int value) {

            int max = this.getMax(context);
            return ((1 <= value) && (max >= value));

        }

        @Override
        public BadiCalendar withValue(BadiCalendar context, int value, boolean lenient) {

            if (!this.isValid(context, value)) {
                throw new IllegalArgumentException("Out of range: " + value);
            }

            int d = context.day;

            switch (this.index) {
                case KULL_I_SHAI_INDEX:
                    if ((d == 5) && context.isIntercalaryDay() && !isLeapYear(value, context.cycle, context.year)) {
                        d = 4;
                    }
                    return new BadiCalendar(value, context.cycle, context.year, context.division, d);
                case VAHID_INDEX:
                    if ((d == 5) && context.isIntercalaryDay() && !isLeapYear(context.major, value, context.year)) {
                        d = 4;
                    }
                    return new BadiCalendar(context.major, value, context.year, context.division, d);
                case YEAR_INDEX:
                    if ((d == 5) && context.isIntercalaryDay() && !isLeapYear(context.major, context.cycle, value)) {
                        d = 4;
                    }
                    return new BadiCalendar(context.major, context.cycle, value, context.division, d);
                case DAY_OF_DIVISION_INDEX:
                    return new BadiCalendar(context.major, context.cycle, context.year, context.division, value);
                case DAY_OF_YEAR_INDEX:
                    return context.withDayOfYear(value);
                case YOE_INDEX:
                    int m = MathUtils.floorDivide(value - 1, 361) + 1;
                    int v = MathUtils.floorDivide(value - (m - 1) * 361 - 1, 19) + 1;
                    int yov = MathUtils.floorModulo(value - 1, 19) + 1;
                    if ((d == 5) && context.isIntercalaryDay() && !isLeapYear(m, v, yov)) {
                        d = 4;
                    }
                    return BadiCalendar.ofComplete(m, v, yov, context.getDivision(), d);
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public Integer getValue(BadiCalendar context) {

            return Integer.valueOf(this.getInt(context));

        }

        @Override
        public Integer getMinimum(BadiCalendar context) {

            return Integer.valueOf(1);

        }

        @Override
        public Integer getMaximum(BadiCalendar context) {

            return Integer.valueOf(this.getMax(context));

        }

        @Override
        public boolean isValid(
            BadiCalendar context,
            Integer value
        ) {

            return ((value != null) && this.isValid(context, value.intValue()));

        }

        @Override
        public BadiCalendar withValue(
            BadiCalendar context,
            Integer value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing new value.");
            }

            return this.withValue(context, value.intValue(), lenient);

        }

        @Override
        public ChronoElement<?> getChildAtFloor(BadiCalendar context) {

            switch (this.index) {
                case KULL_I_SHAI_INDEX:
                    return VAHID;
                case VAHID_INDEX:
                    return YEAR_OF_VAHID;
                case YEAR_INDEX:
                case YOE_INDEX:
                    return MONTH_OF_YEAR;
                case DAY_OF_DIVISION_INDEX:
                case DAY_OF_YEAR_INDEX:
                    return null;
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(BadiCalendar context) {

            switch (this.index) {
                case KULL_I_SHAI_INDEX:
                    return VAHID;
                case VAHID_INDEX:
                    return YEAR_OF_VAHID;
                case YEAR_INDEX:
                case YOE_INDEX:
                    return MONTH_OF_YEAR;
                case DAY_OF_DIVISION_INDEX:
                case DAY_OF_YEAR_INDEX:
                    return null;
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        private int getMax(BadiCalendar context) {

            switch (this.index) {
                case KULL_I_SHAI_INDEX:
                    return 3;
                case VAHID_INDEX:
                case YEAR_INDEX:
                    return 19;
                case DAY_OF_DIVISION_INDEX:
                    if (context.isIntercalaryDay()) {
                        return context.isLeapYear() ? 5 : 4;
                    } else {
                        return 19;
                    }
                case DAY_OF_YEAR_INDEX:
                    return context.isLeapYear() ? 366 : 365;
                case YOE_INDEX:
                    return 3 * 361;
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

    }

    private static class MonthRule
        implements ElementRule<BadiCalendar, BadiMonth> {

        //~ Methoden ------------------------------------------------------

        @Override
        public BadiMonth getValue(BadiCalendar context) {

            return context.getMonth();

        }

        @Override
        public BadiMonth getMinimum(BadiCalendar context) {

            return BadiMonth.BAHA;

        }

        @Override
        public BadiMonth getMaximum(BadiCalendar context) {

            return BadiMonth.ALA;

        }

        @Override
        public boolean isValid(
            BadiCalendar context,
            BadiMonth value
        ) {

            return (value != null);

        }

        @Override
        public BadiCalendar withValue(
            BadiCalendar context,
            BadiMonth value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing Badi month.");
            } else {
                int d = context.isIntercalaryDay() ? 19 : context.day;
                return new BadiCalendar(context.major, context.cycle, context.year, value.getValue(), d);
            }

        }

        @Override
        public ChronoElement<?> getChildAtFloor(BadiCalendar context) {

            return DAY_OF_DIVISION;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(BadiCalendar context) {

            return DAY_OF_DIVISION;

        }

    }

    private static class MonthElement
        extends StdEnumDateElement<BadiMonth, BadiCalendar> {

        //~ Statische Felder/Initialisierungen ----------------------------

        static final MonthElement SINGLETON = new MonthElement();

        private static final long serialVersionUID = -5483090643555757806L;

        //~ Konstruktoren -------------------------------------------------

        private MonthElement() {
            super("MONTH_OF_YEAR", BadiCalendar.class, BadiMonth.class, 'M');

        }

        //~ Methoden ------------------------------------------------------

        @Override
        protected boolean isSingleton() {

            return true;

        }

        @Override
        public void print(
            ChronoDisplay context,
            Appendable buffer,
            AttributeQuery attributes
        ) throws IOException {

            BadiMonth value = context.get(this);
            buffer.append(this.accessor(attributes).print(value));

        }

        @Override
        public BadiMonth parse(
            CharSequence text,
            ParsePosition status,
            AttributeQuery attributes
        ) {

            return this.accessor(attributes).parse(text, status, BadiMonth.class, attributes);

        }

        private TextAccessor accessor(AttributeQuery attributes) {

            Locale lang = getLocale(attributes);
            FormattedContent fc = getFormattedContent(attributes);
            CalendarText ct = CalendarText.getInstance("bahai", lang);
            return ct.getTextForms("M", BadiMonth.class, fc.variant());

        }

    }

    private static class DowElement
        extends StdWeekdayElement<BadiCalendar> {

        //~ Statische Felder/Initialisierungen ----------------------------

        static final DowElement SINGLETON = new DowElement();

        private static final long serialVersionUID = -1733732651700208755L;

        //~ Konstruktoren -------------------------------------------------

        private DowElement() {
            super(BadiCalendar.class, BadiCalendar.getDefaultWeekmodel());

        }

        //~ Methoden ------------------------------------------------------

        @Override
        protected boolean isSingleton() {

            return true;

        }

        @Override
        public void print(
            ChronoDisplay context,
            Appendable buffer,
            AttributeQuery attributes
        ) throws IOException {

            Weekday value = context.get(this).roll(2);
            buffer.append(this.accessor(attributes).print(value));

        }

        @Override
        public Weekday parse(
            CharSequence text,
            ParsePosition status,
            AttributeQuery attributes
        ) {

            return this.accessor(attributes).parse(text, status, Weekday.class, attributes).roll(-2);

        }

        private TextAccessor accessor(AttributeQuery attributes) {

            Locale lang = getLocale(attributes);
            FormattedContent fc = getFormattedContent(attributes);
            CalendarText ct = CalendarText.getInstance("bahai", lang);
            return ct.getTextForms("D", Weekday.class, fc.variant());

        }

    }

    private static class YOV
        extends DisplayElement<Integer>
        implements TextElement<Integer> {

        //~ Statische Felder/Initialisierungen ----------------------------

        static final YOV SINGLETON = new YOV();

        private static final long serialVersionUID = -8280579801733395557L;

        //~ Konstruktoren -------------------------------------------------

        private YOV() {
            super("YEAR_OF_VAHID");
        }

        //~ Methoden ------------------------------------------------------

        @Override
        public char getSymbol() {

            return 'X';

        }

        @Override
        public void print(
            ChronoDisplay context,
            Appendable buffer,
            AttributeQuery attributes
        ) throws IOException, ChronoException {

            int value = context.getInt(this);
            Enum<?> e = enumAccess().getEnumConstants()[value - 1];
            buffer.append(this.accessor(attributes).print(e));

        }

        @Override
        public Integer parse(
            CharSequence text,
            ParsePosition status,
            AttributeQuery attributes
        ) {

            Enum<?> e = this.accessor(attributes).parse(text, status, enumAccess(), attributes);

            if (e == null) {
                return null;
            } else {
                return Integer.valueOf(e.ordinal() + 1);
            }

        }

        @Override
        public Class<Integer> getType() {

            return Integer.class;

        }

        @Override
        public Integer getDefaultMinimum() {

            return Integer.valueOf(1);

        }

        @Override
        public Integer getDefaultMaximum() {

            return Integer.valueOf(19);

        }

        @Override
        public boolean isDateElement() {

            return true;

        }

        @Override
        public boolean isTimeElement() {

            return false;

        }

        @Override
        protected boolean isSingleton() {

            return true;

        }

        private TextAccessor accessor(AttributeQuery attributes) {

            Locale lang = getLocale(attributes);
            FormattedContent fc = getFormattedContent(attributes);
            CalendarText ct = CalendarText.getInstance("bahai", lang);
            return ct.getTextForms("YOV", enumAccess(), fc.variant());

        }

        private static Class<BadiMonth> enumAccess() {

            // uses BadiMonth-enum as intermediate helper class only (it has 19 instances, too)
            return BadiMonth.class;

        }

    }

    private static class IntercalaryAccess
        extends BasicElement<BadiIntercalaryDays>
        implements TextElement<BadiIntercalaryDays>, ElementRule<BadiCalendar, BadiIntercalaryDays> {

        //~ Statische Felder/Initialisierungen ----------------------------

        static final IntercalaryAccess SINGLETON = new IntercalaryAccess();

        private static final long serialVersionUID = -772152174221291354L;

        //~ Konstruktoren -------------------------------------------------

        private IntercalaryAccess() {
            super("AYYAM_I_HA");
        }

        //~ Methoden ------------------------------------------------------

        @Override
        public char getSymbol() {

            return 'A';

        }

        @Override
        public BadiIntercalaryDays getValue(BadiCalendar context) {

            if (context.isIntercalaryDay()) {
                return BadiIntercalaryDays.AYYAM_I_HA;
            } else {
                throw new ChronoException("The actual calendar date is not an intercalary day: " + context);
            }

        }

        @Override
        public BadiIntercalaryDays getMinimum(BadiCalendar context) {

            return BadiIntercalaryDays.AYYAM_I_HA;

        }

        @Override
        public BadiIntercalaryDays getMaximum(BadiCalendar context) {

            return BadiIntercalaryDays.AYYAM_I_HA;

        }

        @Override
        public boolean isValid(
            BadiCalendar context,
            BadiIntercalaryDays value
        ) {

            return (value == BadiIntercalaryDays.AYYAM_I_HA);

        }

        @Override
        public BadiCalendar withValue(
            BadiCalendar context,
            BadiIntercalaryDays value,
            boolean lenient
        ) {

            if (value != BadiIntercalaryDays.AYYAM_I_HA) {
                throw new IllegalArgumentException("Expected Ayyam-i-Ha: " + value);
            }

            int d = Math.min(context.day, context.isLeapYear() ? 5 : 4);
            return new BadiCalendar(context.major, context.cycle, context.year, 0, d);

        }

        @Override
        public ChronoElement<?> getChildAtFloor(BadiCalendar context) {

            return DAY_OF_DIVISION;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(BadiCalendar context) {

            return DAY_OF_DIVISION;

        }

        @Override
        public void print(
            ChronoDisplay context,
            Appendable buffer,
            AttributeQuery attributes
        ) throws IOException, ChronoException {

            BadiIntercalaryDays value = context.get(this);
            Locale lang = attributes.get(Attributes.LANGUAGE, Locale.ROOT);
            buffer.append(this.accessor(lang, attributes).print(value));

        }

        @Override
        public BadiIntercalaryDays parse(
            CharSequence text,
            ParsePosition status,
            AttributeQuery attributes
        ) {

            Locale lang = attributes.get(Attributes.LANGUAGE, Locale.ROOT);
            return this.accessor(lang, attributes).parse(text, status, this.getType(), attributes);

        }

        @Override
        public Class<BadiIntercalaryDays> getType() {

            return BadiIntercalaryDays.class;

        }

        @Override
        public BadiIntercalaryDays getDefaultMinimum() {

            return BadiIntercalaryDays.AYYAM_I_HA;

        }

        @Override
        public BadiIntercalaryDays getDefaultMaximum() {

            return BadiIntercalaryDays.AYYAM_I_HA;

        }

        @Override
        public String getDisplayName(Locale language) {

            return BadiIntercalaryDays.AYYAM_I_HA.getDisplayName(language);

        }

        @Override
        public boolean isDateElement() {

            return true;

        }

        @Override
        public boolean isTimeElement() {

            return false;

        }

        @Override
        protected boolean isSingleton() {

            return true;

        }

        private TextAccessor accessor(
            Locale lang,
            AttributeQuery attributes
        ) {

            FormattedContent fc = attributes.get(TEXT_CONTENT_ATTRIBUTE, FormattedContent.TRANSCRIPTION);
            CalendarText ct = CalendarText.getInstance("bahai", lang);
            String nameKey = "A";

            if ((fc == FormattedContent.MEANING) && ct.getTextForms().containsKey("a")) {
                nameKey = "a";
            }

            return ct.getTextForms(nameKey, this.getType());

        }

    }

    private static class WeekdayRule
        implements ElementRule<BadiCalendar, Weekday> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Weekday getValue(BadiCalendar context) {

            return context.getDayOfWeek();

        }

        @Override
        public Weekday getMinimum(BadiCalendar context) {

            if (
                (context.major == 1)
                && (context.cycle == 1)
                && (context.year == 1)
                && (context.division == 1)
                && (context.day <= 2)
            ) {
                return Weekday.THURSDAY;
            }

            return Weekday.SATURDAY;

        }

        @Override
        public Weekday getMaximum(BadiCalendar context) {

            if (
                (context.major == 3)
                && (context.cycle == 19)
                && (context.year == 19)
                && (context.division == 19)
                && (context.day >= 14)
            ) {
                return Weekday.THURSDAY;
            }

            return Weekday.FRIDAY;

        }

        @Override
        public boolean isValid(
            BadiCalendar context,
            Weekday value
        ) {

            if (value == null) {
                return false;
            }

            Weekmodel model = getDefaultWeekmodel();
            int w = value.getValue(model);
            int wMin = this.getMinimum(context).getValue(model);
            int wMax = this.getMaximum(context).getValue(model);
            return ((wMin <= w) && (w <= wMax));

        }

        @Override
        public BadiCalendar withValue(
            BadiCalendar context,
            Weekday value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing weekday.");
            }

            Weekmodel model = getDefaultWeekmodel();
            int oldValue = context.getDayOfWeek().getValue(model);
            int newValue = value.getValue(model);
            return context.plus(CalendarDays.of(newValue - oldValue));

        }

        @Override
        public ChronoElement<?> getChildAtFloor(BadiCalendar context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(BadiCalendar context) {

            return null;

        }

    }

    private static class Merger
        implements ChronoMerger<BadiCalendar> {

        //~ Methoden ------------------------------------------------------

        @Override
        public BadiCalendar createFrom(
            TimeSource<?> clock,
            AttributeQuery attributes
        ) {

            TZID tzid;

            if (attributes.contains(Attributes.TIMEZONE_ID)) {
                tzid = attributes.get(Attributes.TIMEZONE_ID);
            } else if (attributes.get(Attributes.LENIENCY, Leniency.SMART).isLax()) {
                tzid = Timezone.ofSystem().getID();
            } else {
                return null;
            }

            StartOfDay startOfDay = attributes.get(Attributes.START_OF_DAY, this.getDefaultStartOfDay());
            return Moment.from(clock.currentTime()).toGeneralTimestamp(ENGINE, tzid, startOfDay).toDate();

        }

        @Override
        public BadiCalendar createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean lenient,
            boolean preparsing
        ) {

            int major = entity.getInt(KULL_I_SHAI);

            if (major == Integer.MIN_VALUE) {
                major = 1; // smart parsing
            } else if ((major < 1) || (major > 3)) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Major cycle out of range: " + major);
                return null;
            }

            int vahid = entity.getInt(VAHID);
            boolean hasVahid = true;

            if (vahid == Integer.MIN_VALUE) {
                hasVahid = false;
            } else if ((vahid < 1) || (vahid > 19)) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Vahid cycle out of range: " + vahid);
                return null;
            }

            int year = entity.getInt(YEAR_OF_VAHID);

            if (year == Integer.MIN_VALUE) {
                if (entity.contains(YEAR_OF_ERA)) {
                    BadiCalendar prototype =
                        BadiCalendar.axis().getMinimum().with(YEAR_OF_ERA, entity.getInt(YEAR_OF_ERA));
                    major = prototype.getKullishai();
                    vahid = prototype.getVahid();
                    year = prototype.getYearOfVahid();
                } else {
                    entity.with(ValidationElement.ERROR_MESSAGE, "Missing year-of-vahid.");
                    return null;
                }
            } else if (!hasVahid) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Missing vahid cycle.");
                return null;
            } else if ((year < 1) || (year > 19)) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Badi year-of-vahid out of range: " + year);
                return null;
            }

            BadiCalendar cal = null;

            if (entity.contains(MONTH_OF_YEAR)) {
                int month = entity.get(MONTH_OF_YEAR).getValue();
                int dom = entity.getInt(DAY_OF_DIVISION);

                if ((dom >= 1) && (dom <= 19)) {
                    cal = new BadiCalendar(major, vahid, year, month, dom);
                } else {
                    entity.with(ValidationElement.ERROR_MESSAGE, "Invalid Badi date.");
                }
            } else if (entity.contains(AYYAM_I_HA)) {
                int day = entity.getInt(DAY_OF_DIVISION);

                if ((day >= 1) && (day <= (isLeapYear(major, vahid, year) ? 5 : 4))) {
                    cal = new BadiCalendar(major, vahid, year, 0, day);
                } else {
                    entity.with(ValidationElement.ERROR_MESSAGE, "Invalid Badi date.");
                }
            } else {
                int doy = entity.getInt(DAY_OF_YEAR);
                boolean leap = isLeapYear(major, vahid, year);
                if (doy != Integer.MIN_VALUE) {
                    if ((doy >= 1) && (doy <= (leap ? 366 : 365))) {
                        int pDiv;
                        int pDay;
                        if (doy <= 18 * 19) {
                            pDiv = ((doy - 1) / 19) + 1;
                            pDay = ((doy - 1) % 19) + 1;
                        } else if (doy <= 18 * 19 + (leap ? 5 : 4)) {
                            pDiv = 0;
                            pDay = doy - 18 * 19;
                        } else {
                            pDiv = 19;
                            pDay = doy - (leap ? 5 : 4) - 18 * 19;
                        }
                        cal = new BadiCalendar(major, vahid, year, pDiv, pDay);
                    } else {
                        entity.with(ValidationElement.ERROR_MESSAGE, "Invalid Badi date.");
                    }
                }
            }

            return cal;

        }

        @Override
        public int getDefaultPivotYear() {

            return PlainDate.axis().getDefaultPivotYear() - 1844; // not relevant for dynamic pattern type

        }

        @Override
        public StartOfDay getDefaultStartOfDay() {

            return StartOfDay.definedBy(TEHERAN.sunset());

        }

    }

    private static class FUnitRule
        implements UnitRule<BadiCalendar> {

        //~ Instanzvariablen ----------------------------------------------

        private final Unit unit;

        //~ Konstruktoren -------------------------------------------------

        FUnitRule(Unit unit) {
            super();

            this.unit = unit;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public BadiCalendar addTo(BadiCalendar date, long amount) {

            switch (this.unit) {
                case VAHID_CYCLES:
                    amount = MathUtils.safeMultiply(amount, 19);
                    // fall-through
                case YEARS:
                    long yy = MathUtils.safeAdd(elapsedYears(date), amount);
                    int majorY = MathUtils.safeCast(MathUtils.floorDivide(yy, 361)) + 1;
                    int remainderY = MathUtils.floorModulo(yy, 361);
                    int cycleY = MathUtils.floorDivide(remainderY, 19) + 1;
                    int yearY = MathUtils.floorModulo(remainderY, 19) + 1;
                    int dod = date.day;
                    if ((date.day == 5) && !isLeapYear(majorY, cycleY, yearY)) {
                        dod = 4;
                    }
                    return BadiCalendar.ofComplete(majorY, cycleY, yearY, date.getDivision(), dod);
                case MONTHS: // interprete ayyam-i-ha as extension of month 18
                    long ym = MathUtils.safeAdd(elapsedMonths(date), amount);
                    int majorM = MathUtils.safeCast(MathUtils.floorDivide(ym, 6859)) + 1;
                    int remainderM = MathUtils.floorModulo(ym, 6859);
                    int cycleM = MathUtils.floorDivide(remainderM, 361) + 1;
                    remainderM = MathUtils.floorModulo(remainderM, 361);
                    int yearM = MathUtils.floorDivide(remainderM, 19) + 1;
                    int month = MathUtils.floorModulo(remainderM, 19) + 1;
                    int dom = (date.isIntercalaryDay() ? 19 : date.day);
                    return BadiCalendar.ofComplete(majorM, cycleM, yearM, BadiMonth.valueOf(month), dom);
                case WEEKS:
                    amount = MathUtils.safeMultiply(amount, 7);
                    // fall-through
                case DAYS:
                    long utcDays = MathUtils.safeAdd(CALSYS.transform(date), amount);
                    return CALSYS.transform(utcDays);
                default:
                    throw new UnsupportedOperationException(this.unit.name());
            }

        }

        @Override
        public long between(BadiCalendar start, BadiCalendar end) {

            switch (this.unit) {
                case VAHID_CYCLES:
                    return BadiCalendar.Unit.YEARS.between(start, end) / 19;
                case YEARS:
                    int deltaY = elapsedYears(end) - elapsedYears(start);
                    if ((deltaY > 0) && (end.getDayOfYear() < start.getDayOfYear())) {
                        deltaY--;
                    } else if ((deltaY < 0) && (end.getDayOfYear() > start.getDayOfYear())) {
                        deltaY++;
                    }
                    return deltaY;
                case MONTHS: // interprete ayyam-i-ha as extension of month 18
                    long deltaM = elapsedMonths(end) - elapsedMonths(start);
                    int sdom = (start.isIntercalaryDay() ? (start.day + 19) : start.day);
                    int edom = (end.isIntercalaryDay() ? (end.day + 19) : end.day);
                    if ((deltaM > 0) && (edom < sdom)) {
                        deltaM--;
                    } else if ((deltaM < 0) && (edom > sdom)) {
                        deltaM++;
                    }
                    return deltaM;
                case WEEKS:
                    return BadiCalendar.Unit.DAYS.between(start, end) / 7;
                case DAYS:
                    return CALSYS.transform(end) - CALSYS.transform(start);
                default:
                    throw new UnsupportedOperationException(this.unit.name());
            }

        }

        private static int elapsedYears(BadiCalendar date) {
            return (((date.major - 1) * 19) + (date.cycle - 1)) * 19 + date.year - 1;
        }

        private static int elapsedMonths(BadiCalendar date) {
            int m = (date.isIntercalaryDay() ? 18 : date.getMonth().getValue());
            return 19 * elapsedYears(date) + m - 1;
        }

    }

}
