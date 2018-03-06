/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChineseCalendar.java) is part of project Time4J.
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

import net.time4j.SystemClock;
import net.time4j.Weekday;
import net.time4j.Weekmodel;
import net.time4j.calendar.service.StdIntegerDateElement;
import net.time4j.calendar.service.StdWeekdayElement;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.CalendarEra;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.ChronoFunction;
import net.time4j.engine.ChronoUnit;
import net.time4j.engine.Chronology;
import net.time4j.engine.ElementRule;
import net.time4j.engine.FormattableElement;
import net.time4j.engine.IntElementRule;
import net.time4j.engine.TimeAxis;
import net.time4j.engine.ValidationElement;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarType;
import net.time4j.format.DisplayElement;
import net.time4j.format.LocalizedPatternSupport;
import net.time4j.format.TextElement;
import net.time4j.format.TextWidth;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * <p>Represents the Chinese calendar supported in the gregorian range 1645-01-28/3000-01-27. </p>
 *
 * <p>It is a lunisolar calendar which defines years consisting of 12 or 13 months. See also
 * <a href="https://en.wikipedia.org/wiki/Chinese_calendar">Wikipedia</a>. </p>
 *
 * <p>Following elements which are declared as constants are registered by this class</p>
 *
 * <ul>
 *  <li>{@link #DAY_OF_WEEK}</li>
 *  <li>{@link #DAY_OF_MONTH}</li>
 *  <li>{@link #DAY_OF_YEAR}</li>
 *  <li>{@link #WEEKDAY_IN_MONTH}</li>
 *  <li>{@link #MONTH_OF_YEAR}</li>
 *  <li>{@link #MONTH_AS_ORDINAL}</li>
 *  <li>{@link #SOLAR_TERM}</li>
 *  <li>{@link #YEAR_OF_CYCLE}</li>
 *  <li>{@link #YEAR_OF_ERA}</li>
 *  <li>{@link #CYCLE}</li>
 *  <li>{@link #ERA}</li>
 * </ul>
 *
 * <p>Furthermore, all elements defined in {@code EpochDays} and {@link CommonElements} are supported. </p>
 *
 * <p><strong>Accuracy note</strong></p>
 *
 * <p>The calculations are based on the algorithms of Dershowitz/Reingold in their book
 * &quot;Calendrical calculations&quot; and astronomical calculations in nowadays precision.
 * Old scholar Chinese astronomers had applied less precise calculations in historic times
 * so tiny deviations for the period 1645-1906 are possible in some rare cases. That means,
 * this class rather models a theoretical ideal than the historic reality. </p>
 *
 * <p>The future data from 1907 onwards had been compared to the published data of
 * <a href="http://www.hko.gov.hk/gts/time/conversion.htm">Hongkong observatory</a>.
 * Only the dates 2057-09-29 and 2097-08-08 deviate by one day (so all following days of same month).
 * However, the observatory has explicitly declared these dates to be uncertain because their calculations
 * of New Moon are not accurate enough to decide which month start exactly is right (the results are very
 * close to local midnight). </p>
 *
 * <p>How to determine the festivals of New Year and Qing-Ming as gregorian dates: </p>
 *
 * <pre>
 *     ChineseCalendar newyear = ChineseCalendar.ofNewYear(2020);
 *     ChineseCalendar qingming = newyear.with(ChineseCalendar.SOLAR_TERM, SolarTerm.MINOR_03_QINGMING_015);
 *     System.out.println(newyear.transform(PlainDate.axis())); // 2020-01-25
 *     System.out.println(qingming.transform(PlainDate.axis())); // 2020-04-04
 * </pre>
 *
 * <p>Formatting with cyclic years and solar terms: </p>
 *
 * <pre>
 *     ChronoFormatter&lt;ChineseCalendar&gt; formatter =
 *       ChronoFormatter.setUp(ChineseCalendar.axis(), Locale.ENGLISH)
 *         .addPattern(&quot;EEE, d. MMMM r(U) &quot;, PatternType.CLDR_DATE)
 *         .addText(ChineseCalendar.SOLAR_TERM)
 *         .build();
 *     PlainDate winter =
 *       AstronomicalSeason.WINTER_SOLSTICE
 *         .inYear(2018)
 *         .toZonalTimestamp(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 8))
 *         .getCalendarDate();
 *     ChineseCalendar chineseDate = winter.transform(ChineseCalendar.class);
 *     assertThat(
 *       formatter.with(Locale.CHINESE).parse(&quot;周六, 16. 十一月 2018(戊戌) 冬至&quot;),
 *       is(chineseDate));
 *     assertThat(
 *       formatter.format(chineseDate),
 *       is(&quot;Sat, 16. M11 2018(wù-xū) dōngzhì&quot;));
 * </pre>
 *
 * <p>Leap months can be formatted in various ways. Following example shows how to customize numerical printing
 * when a non-Chinese language is used: </p>
 *
 * <pre>
 *     ChronoFormatter&lt;ChineseCalendar&gt; f =
 *       ChronoFormatter.ofPattern(&quot;M/d, U(r)&quot;, PatternType.CLDR, Locale.ENGLISH, ChineseCalendar.axis())
 *         .with(EastAsianMonth.LEAP_MONTH_IS_TRAILING, true)
 *         .with(EastAsianMonth.LEAP_MONTH_INDICATOR, &#39;b&#39;);
 *     ChineseCalendar cc =
 *       ChineseCalendar.of(EastAsianYear.forGregorian(2020), EastAsianMonth.valueOf(4).withLeap(), 5);
 *     assertThat(
 *       f.format(cc),
 *       is(&quot;4b/5, &quot; + cc.getYear().getDisplayName(Locale.ENGLISH) + &quot;(2020)&quot;));
 * </pre>
 *
 * @author  Meno Hochschild
 * @since   3.40/4.35
 */
/*[deutsch]
 * <p>Repr&auml;sentiert den chinesischen Kalender mit dem unterst&uuml;tzen (gregorianischen)
 * Bereich 1645-01-28/3000-01-27. </p>
 *
 * <p>Es handelt sich um einen lunisolaren Kalender, dessen Jahre aus 12 oder 13 Monaten bestehen.
 * Siehe auch <a href="https://en.wikipedia.org/wiki/Chinese_calendar">Wikipedia</a>. </p>
 *
 * <p>Registriert sind folgende als Konstanten deklarierte Elemente</p>
 *
 * <ul>
 *  <li>{@link #DAY_OF_WEEK}</li>
 *  <li>{@link #DAY_OF_MONTH}</li>
 *  <li>{@link #DAY_OF_YEAR}</li>
 *  <li>{@link #WEEKDAY_IN_MONTH}</li>
 *  <li>{@link #MONTH_OF_YEAR}</li>
 *  <li>{@link #MONTH_AS_ORDINAL}</li>
 *  <li>{@link #SOLAR_TERM}</li>
 *  <li>{@link #YEAR_OF_CYCLE}</li>
 *  <li>{@link #YEAR_OF_ERA}</li>
 *  <li>{@link #CYCLE}</li>
 *  <li>{@link #ERA}</li>
 * </ul>
 *
 * <p>Au&slig;erdem werden alle Elemente von {@code EpochDays} und {@link CommonElements} unterst&uuml;tzt. </p>
 *
 * <p><strong>Hinweis zur Genauigkeit</strong></p>
 *
 * <p>Die Berechnungen fu&szlig;en auf den Algorithmen von Dershowitz/Reingold in ihrem Buch
 * &quot;Calendrical calculations&quot; und astronomischen Verfahren in der heute verf&uuml;gbaren
 * Genauigkeit. Chinesische Astronomen der alten Schule hatten weniger genaue Berechnungsverfahren
 * zur Hand, so da&szlig; kleine Abweichungen f&uuml;r die Periode 1645-1906 in seltenen F&auml;llen
 * m&ouml;glich sind. Das bedeutet, diese Klasse modelliert eher ein theoretisches Ideal als die
 * historische Wirklichkeit. </p>
 *
 * <p>Zuk&uuml;nftige Datumsangaben von 1907 aufw&auml;rts wurden mit den ver&ouml;ffentlichten Daten
 * des <a href="http://www.hko.gov.hk/gts/time/conversion.htm">Observatoriums von Hongkong</a> verglichen.
 * Nur die Tage 2057-09-29 und 2097-08-08 weichen um einen Tag ab (so auch die folgenden Tage des jeweils
 * gleichen Monats). Allerdings hat das Observatorium ausdr&uuml;cklich diese Tage als ungesichert
 * erkl&auml;rt, weil dessen Berechnungen nicht genau genug sind, um zu entscheiden, welcher Monatsbeginn
 * wirklich der richtige ist (die Ergebnisse sind sehr nahe an der &ouml;rtlichen Mitternacht). </p>
 *
 * <p>Wie k&ouml;nnen die Feiertage des chinesischen Neujahrs und von Qing-Ming als gregorianische
 * Datumsangaben bestimmt werden? </p>
 *
 * <pre>
 *     ChineseCalendar newyear = ChineseCalendar.ofNewYear(2020);
 *     ChineseCalendar qingming = newyear.with(ChineseCalendar.SOLAR_TERM, SolarTerm.MINOR_03_QINGMING_015);
 *     System.out.println(newyear.transform(PlainDate.axis())); // 2020-01-25
 *     System.out.println(qingming.transform(PlainDate.axis())); // 2020-04-04
 * </pre>
 *
 * <p>Formatierung mit zyklischen Jahren und Sonnenmonaten: </p>
 *
 * <pre>
 *     ChronoFormatter&lt;ChineseCalendar&gt; formatter =
 *       ChronoFormatter.setUp(ChineseCalendar.axis(), Locale.ENGLISH)
 *         .addPattern(&quot;EEE, d. MMMM r(U) &quot;, PatternType.CLDR_DATE)
 *         .addText(ChineseCalendar.SOLAR_TERM)
 *         .build();
 *     PlainDate winter =
 *       AstronomicalSeason.WINTER_SOLSTICE
 *         .inYear(2018)
 *         .toZonalTimestamp(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 8))
 *         .getCalendarDate();
 *     ChineseCalendar chineseDate = winter.transform(ChineseCalendar.class);
 *     assertThat(
 *       formatter.with(Locale.CHINESE).parse(&quot;周六, 16. 十一月 2018(戊戌) 冬至&quot;),
 *       is(chineseDate));
 *     assertThat(
 *       formatter.format(chineseDate),
 *       is(&quot;Sat, 16. M11 2018(wù-xū) dōngzhì&quot;));
 * </pre>
 *
 * <p>Schaltmonate k&ouml;nnen verschieden formatiert werden. Das folgende Beispiel zeigt, wie man
 * die numerische Formatierung im Fall einer nicht-chinesischen Sprache anpassen kann: </p>
 *
 * <pre>
 *     ChronoFormatter&lt;ChineseCalendar&gt; f =
 *       ChronoFormatter.ofPattern(&quot;M/d, U(r)&quot;, PatternType.CLDR, Locale.ENGLISH, ChineseCalendar.axis())
 *         .with(EastAsianMonth.LEAP_MONTH_IS_TRAILING, true)
 *         .with(EastAsianMonth.LEAP_MONTH_INDICATOR, &#39;b&#39;);
 *     ChineseCalendar cc =
 *       ChineseCalendar.of(EastAsianYear.forGregorian(2020), EastAsianMonth.valueOf(4).withLeap(), 5);
 *     assertThat(
 *       f.format(cc),
 *       is(&quot;4b/5, &quot; + cc.getYear().getDisplayName(Locale.ENGLISH) + &quot;(2020)&quot;));
 * </pre>
 *
 * @author  Meno Hochschild
 * @since   3.40/4.35
 */
@CalendarType("chinese")
public final class ChineseCalendar
    extends EastAsianCalendar<ChineseCalendar.Unit, ChineseCalendar>
    implements LocalizedPatternSupport {

    //~ Statische Felder/Initialisierungen --------------------------------

    // see also: http://www.math.nus.edu.sg/aslaksen/calendar/LeapMonths.nb
    private static final int[] LEAP_MONTHS = {
        4281, 5, 4284, 4, 4287, 1, 4289, 6, 4292, 5, 4295, 3, 4297, 8, 4300, 6, 4303, 4, 4306, 2,
        4308, 7, 4311, 5, 4314, 3, 4316, 8, 4319, 6, 4322, 4, 4325, 3, 4327, 7, 4330, 5, 4333, 3,
        4335, 7, 4338, 6, 4341, 4, 4344, 3, 4346, 7, 4349, 5, 4352, 3, 4354, 8, 4357, 6, 4360, 4,
        4363, 2, 4365, 7, 4368, 5, 4371, 4, 4373, 9, 4376, 6, 4379, 4, 4382, 3, 4384, 7, 4387, 5,
        4390, 4, 4392, 9, 4395, 6, 4398, 5, 4401, 2, 4403, 7, 4406, 5, 4409, 3, 4411, 10, 4414, 6,
        4417, 5, 4420, 3, 4422, 7, 4425, 5, 4428, 4, 4431, 2, 4433, 6, 4436, 4, 4439, 2, 4441, 7,
        4444, 5, 4447, 3, 4450, 2, 4452, 6, 4455, 4, 4458, 3, 4460, 7, 4463, 5, 4466, 4, 4468, 9,
        4471, 6, 4474, 4, 4477, 3, 4479, 7, 4482, 5, 4485, 4, 4487, 8, 4490, 7, 4493, 5, 4496, 3,
        4498, 8, 4501, 5, 4504, 4, 4506, 10, 4509, 6, 4512, 5, 4515, 3, 4517, 7, 4520, 5, 4523, 4,
        4526, 2, 4528, 6, 4531, 5, 4534, 3, 4536, 8, 4539, 5, 4542, 4, 4545, 2, 4547, 6, 4550, 5,
        4553, 2, 4555, 7, 4558, 5, 4561, 4, 4564, 2, 4566, 6, 4569, 5, 4572, 3, 4574, 7, 4577, 6,
        4580, 4, 4583, 2, 4585, 7, 4588, 5, 4591, 3, 4593, 8, 4596, 6, 4599, 4, 4602, 3, 4604, 7,
        4607, 5, 4610, 4, 4612, 8, 4615, 6, 4618, 4, 4620, 10, 4623, 6, 4626, 5, 4629, 3, 4631, 8,
        4634, 5, 4637, 4, 4640, 2, 4642, 7, 4645, 5, 4648, 4, 4650, 9, 4653, 6, 4656, 4, 4659, 2,
        4661, 6, 4664, 5, 4667, 3, 4669, 11, 4672, 6, 4675, 5, 4678, 2, 4680, 7, 4683, 5, 4686, 3,
        4688, 8, 4691, 6, 4694, 4, 4697, 3, 4699, 7, 4702, 5, 4705, 4, 4707, 8, 4710, 6, 4713, 4,
        4716, 3, 4718, 7, 4721, 5, 4724, 4, 4726, 8, 4729, 6, 4732, 4, 4735, 2, 4737, 7, 4740, 5,
        4743, 4, 4745, 9, 4748, 6, 4751, 4, 4754, 3, 4756, 7, 4759, 5, 4762, 4, 4764, 11, 4767, 6,
        4770, 5, 4773, 2, 4775, 7, 4778, 5, 4781, 4, 4783, 11, 4786, 6, 4789, 5, 4792, 3, 4794, 7,
        4797, 6, 4800, 4, 4802, 10, 4805, 6, 4808, 5, 4811, 3, 4813, 7, 4816, 6, 4819, 4, 4822, 2,
        4824, 6, 4827, 5, 4830, 3, 4832, 7, 4835, 6, 4838, 4, 4840, 9, 4843, 6, 4846, 4, 4849, 3,
        4851, 7, 4854, 5, 4857, 4, 4859, 9, 4862, 7, 4865, 5, 4868, 3, 4870, 8, 4873, 5, 4876, 4,
        4878, 11, 4881, 6, 4884, 5, 4887, 3, 4889, 7, 4892, 6, 4895, 5, 4898, 1, 4900, 7, 4903, 5,
        4906, 3, 4908, 8, 4911, 6, 4914, 4, 4917, 2, 4919, 6, 4922, 5, 4925, 3, 4927, 7, 4930, 6,
        4933, 4, 4936, 2, 4938, 6, 4941, 5, 4944, 3, 4946, 7, 4949, 6, 4952, 4, 4954, 10, 4957, 7,
        4960, 5, 4963, 3, 4965, 8, 4968, 6, 4971, 4, 4974, 3, 4976, 7, 4979, 5, 4982, 4, 4984, 8,
        4987, 6, 4990, 5, 4993, 1, 4995, 7, 4998, 5, 5001, 4, 5003, 8, 5006, 6, 5009, 5, 5012, 2,
        5014, 7, 5017, 5, 5020, 4, 5022, 10, 5025, 6, 5028, 4, 5031, 2, 5033, 6, 5036, 5, 5039, 3,
        5041, 8, 5044, 6, 5047, 5, 5050, 2, 5052, 7, 5055, 5, 5058, 3, 5060, 8, 5063, 6, 5066, 4,
        5069, 3, 5071, 7, 5074, 5, 5077, 4, 5079, 8, 5082, 7, 5085, 5, 5088, 3, 5090, 8, 5093, 5,
        5096, 4, 5098, 8, 5101, 6, 5104, 5, 5107, 3, 5109, 7, 5112, 5, 5115, 4, 5117, 10, 5120, 6,
        5123, 5, 5126, 3, 5128, 7, 5131, 5, 5134, 4, 5136, 10, 5139, 6, 5142, 5, 5145, 2, 5147, 7,
        5150, 5, 5153, 4, 5156, 1, 5158, 6, 5161, 5, 5164, 3, 5166, 7, 5169, 6, 5172, 4, 5175, 1,
        5177, 7, 5180, 5, 5183, 3, 5185, 7, 5188, 6, 5191, 4, 5193, 8, 5196, 7, 5199, 5, 5202, 4,
        5204, 7, 5207, 6, 5210, 4, 5212, 9, 5215, 7, 5218, 5, 5221, 3, 5223, 7, 5226, 6, 5229, 4,
        5231, 10, 5234, 7, 5237, 5, 5240, 3, 5242, 8, 5245, 6, 5248, 4, 5250, 11, 5253, 6, 5256, 5,
        5259, 3, 5261, 8, 5264, 6, 5267, 5, 5270, 1, 5272, 7, 5275, 5, 5278, 3, 5280, 8, 5283, 6,
        5286, 4, 5289, 2, 5291, 7, 5294, 5, 5297, 3, 5299, 7, 5302, 6, 5305, 4, 5308, 3, 5310, 7,
        5313, 5, 5316, 3, 5318, 7, 5321, 6, 5324, 4, 5327, 3, 5329, 7, 5332, 5, 5335, 3, 5337, 8,
        5340, 6, 5343, 4, 5346, 2, 5348, 7, 5351, 5, 5354, 4, 5356, 9, 5359, 6, 5362, 5, 5364, 11,
        5367, 7, 5370, 5, 5373, 4, 5375, 9, 5378, 6, 5381, 5, 5384, 2, 5386, 7, 5389, 6, 5392, 4,
        5394, 8, 5397, 6, 5400, 5, 5403, 3, 5405, 7, 5408, 6, 5411, 4, 5413, 8, 5416, 6, 5419, 5,
        5422, 3, 5424, 7, 5427, 6, 5430, 3, 5432, 8, 5435, 6, 5438, 4, 5441, 3, 5443, 7, 5446, 6,
        5449, 4, 5451, 9, 5454, 7, 5457, 5, 5460, 3, 5462, 8, 5465, 5, 5468, 4, 5470, 9, 5473, 6,
        5476, 5, 5479, 3, 5481, 8, 5484, 6, 5487, 4, 5489, 9, 5492, 6, 5495, 5, 5498, 3, 5500, 7,
        5503, 6, 5506, 4, 5508, 10, 5511, 6, 5514, 5, 5517, 3, 5519, 7, 5522, 6, 5525, 4, 5527, 10,
        5530, 6, 5533, 5, 5536, 3, 5538, 7, 5541, 6, 5544, 4, 5546, 11, 5549, 7, 5552, 5, 5555, 3,
        5557, 8, 5560, 6, 5563, 4, 5565, 9, 5568, 7, 5571, 5, 5574, 4, 5576, 8, 5579, 6, 5582, 4,
        5584, 8, 5587, 7, 5590, 5, 5593, 4, 5595, 8, 5598, 6, 5601, 5, 5603, 10, 5606, 7, 5609, 5,
        5612, 3, 5614, 8, 5617, 6, 5620, 4, 5622, 10, 5625, 6, 5628, 5, 5631, 3, 5633, 8, 5636, 6
    };

    /**
     * <p>Represents the Chinese era. </p>
     *
     * <p>This element is effectively read-only. Its value cannot be changed in a direct and meaningful way. </p>
     *
     * @see     #YEAR_OF_ERA
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die chinesische &Auml;ra. </p>
     *
     * <p>Dieses Element ist effektiv nur zur Anzeige. Sein Wert kann nicht direkt und sinnvoll
      * ge&auml;ndert werden. </p>
     *
     * @see     #YEAR_OF_ERA
     */
    @FormattableElement(format = "G")
    public static final ChronoElement<ChineseEra> ERA = EraElement.INSTANCE;

    /**
     * <p>Represents the cycle number related to the introduction of sexagesimal cycles
     * by the legendary yellow emperor Huang-Di on -2636-02-15 (gregorian). </p>
     *
     * <p><strong>This kind of counting is NOT in common use in China and only
     * offered for technical reasons.</strong> Prefer just the cyclic year together
     * with the related gregorian year instead. </p>
     *
     * @see     #YEAR_OF_CYCLE
     * @see     CommonElements#RELATED_GREGORIAN_YEAR
     */
    /*[deutsch]
     * <p>Nummer des Jahreszyklus relativ zur Einf&uuml;hrung der sexagesimalen Zyklen
     * durch den legendenhaften Kaiser Huang-Di am Tag -2636-02-15 (gregorianisch). </p>
     *
     * <p><strong>Diese Art der Z&auml;hlung ist in China NICHT gebr&auml;uchlich und wird nur aus
     * technischen Gr&uuml;nden angeboten.</strong> Dem zyklischen Jahr in Verbindung mit einer
     * gregorianischen Bezugsjahresangabe ist der Vorzug zu geben. </p>
     *
     * @see     #YEAR_OF_CYCLE
     * @see     CommonElements#RELATED_GREGORIAN_YEAR
     */
    public static final ChronoElement<Integer> CYCLE =
        new StdIntegerDateElement<ChineseCalendar>(
            "CYCLE",
            ChineseCalendar.class,
            72,
            94,
            '\u0000',
            null,
            null);

    /**
     * <p>Represents the Chinese year related to the Chinese era. </p>
     *
     * <p><strong>This kind of year counting is NOT in common use in China today and only
     * offered for historical reasons.</strong> Prefer the cyclic year instead. </p>
     *
     * @see     #ERA
     * @see     #YEAR_OF_CYCLE
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert das chinesische Jahr relativ zur chinesischen &Auml;ra. </p>
     *
     * <p><strong>Diese Art der Jahresz&auml;hlung ist in China heute NICHT gebr&auml;uchlich und wird nur aus
     * historischen Gr&uuml;nden angeboten.</strong> Dem zyklischen Jahr ist der Vorzug zu geben. </p>
     *
     * @see     #ERA
     * @see     #YEAR_OF_CYCLE
     */
    @FormattableElement(format = "y")
    public static final StdCalendarElement<Integer, ChineseCalendar> YEAR_OF_ERA =
        new StdIntegerDateElement<ChineseCalendar>(
            "YEAR_OF_ERA",
            ChineseCalendar.class,
            1,
            5636,
            'y',
            null,
            null);

    /**
     * <p>Represents the Chinese year related to the current sexagesimal cycle. </p>
     *
     * <p>This is the standard way to specify a Chinese year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert das chinesische Jahr des aktuellen sexagesimalen Zyklus. </p>
     *
     * <p>Das ist der Standardweg, ein chinesisches Jahr zu definieren. </p>
     */
    @FormattableElement(format = "U")
    public static final TextElement<CyclicYear> YEAR_OF_CYCLE = EastAsianCY.SINGLETON;

    /**
     * <p>Represents the solar term as one of 24 possible stations of the sun on the ecliptic. </p>
     *
     * <p>When manipulating then this element behaves as if the next solar term on or after new year
     * is set. Example: </p>
     *
     * <pre>
     *     ChineseCalendar date = PlainDate.of(2017, 12, 22).transform(ChineseCalendar.axis());
     *     date = date.with(ChineseCalendar.SOLAR_TERM, SolarTerm.MINOR_03_QINGMING_015);
     *     System.out.println(date.transform(PlainDate.axis())); // 2017-04-04
     * </pre>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den Sonnenmonat als eine von 24 m&ouml;glichen Stationen auf der Ekliptik der Sonne. </p>
     *
     * <p>In Manipulationen verh&auml;lt sich dieses Element so, da&szlig; der am oder nach dem aktuellen
     * Neujahrstag passende Sonnenmonat bestimmt wird. Beispiel: </p>
     *
     * <pre>
     *     ChineseCalendar date = PlainDate.of(2017, 12, 22).transform(ChineseCalendar.axis());
     *     date = date.with(ChineseCalendar.SOLAR_TERM, SolarTerm.MINOR_03_QINGMING_015);
     *     System.out.println(date.transform(PlainDate.axis())); // 2017-04-04
     * </pre>
     */
    public static final ChronoElement<SolarTerm> SOLAR_TERM = EastAsianST.getInstance();

    /**
     * <p>Represents the Chinese month. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den chinesischen Monat. </p>
     */
    @FormattableElement(format = "M", standalone = "L")
    public static final TextElement<EastAsianMonth> MONTH_OF_YEAR = EastAsianME.SINGLETON_EA;

    /**
     * <p>Represents the ordinal index of a Chinese month in the range {@code 1-12/13}. </p>
     *
     * <p>This element can be used in conjunction with
     * {@link net.time4j.format.expert.ChronoFormatter.Builder#addOrdinal(ChronoElement, Map) ordinal formatting}. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die Ordnungsnummer eines chinesischen Monats im Bereich {@code 1-12/13}. </p>
     *
     * <p>Dieses Element kann in Verbindung mit einem
     * {@link net.time4j.format.expert.ChronoFormatter.Builder#addOrdinal(ChronoElement, Map) OrdinalFormat}
     * verwendet werden. </p>
     */
    public static final StdCalendarElement<Integer, ChineseCalendar> MONTH_AS_ORDINAL =
        new StdIntegerDateElement<ChineseCalendar>(
            "MONTH_AS_ORDINAL", ChineseCalendar.class, 1, 12, '\u0000', null, null);

    /**
     * <p>Represents the Chinese day of month. </p>
     *
     * <p>Months have either 29 or 30 days. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den chinesischen Tag des Monats. </p>
     *
     * <p>Monate haben entweder 29 oder 30 Tage. </p>
     */
    @FormattableElement(format = "d")
    public static final StdCalendarElement<Integer, ChineseCalendar> DAY_OF_MONTH =
        new StdIntegerDateElement<ChineseCalendar>("DAY_OF_MONTH", ChineseCalendar.class, 1, 30, 'd');

    /**
     * <p>Represents the Chinese day of year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den chinesischen Tag des Jahres. </p>
     */
    @FormattableElement(format = "D")
    public static final StdCalendarElement<Integer, ChineseCalendar> DAY_OF_YEAR =
        new StdIntegerDateElement<ChineseCalendar>("DAY_OF_YEAR", ChineseCalendar.class, 1, 355, 'D');

    /**
     * <p>Represents the Chinese day of week. </p>
     *
     * <p>If the day-of-week is set to a new value then Time4J handles the Chinese calendar week
     * as starting on Sunday (like in China). </p>
     *
     * @see     #getDefaultWeekmodel()
     * @see     CommonElements#localDayOfWeek(Chronology, Weekmodel)
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den chinesischen Tag der Woche. </p>
     *
     * <p>Wenn der Tag der Woche auf einen neuen Wert gesetzt wird, behandelt Time4J die chinesische
     * Kalenderwoche so, da&szlig; sie am Sonntag beginnt (wie in China). </p>
     *
     * @see     #getDefaultWeekmodel()
     * @see     CommonElements#localDayOfWeek(Chronology, Weekmodel)
     */
    @FormattableElement(format = "E")
    public static final StdCalendarElement<Weekday, ChineseCalendar> DAY_OF_WEEK =
        new StdWeekdayElement<ChineseCalendar>(ChineseCalendar.class, getDefaultWeekmodel());

    private static final WeekdayInMonthElement<ChineseCalendar> WIM_ELEMENT =
        new WeekdayInMonthElement<ChineseCalendar>(ChineseCalendar.class, DAY_OF_MONTH, DAY_OF_WEEK);

    /**
     * <p>Element with the ordinal day-of-week within given calendar month. </p>
     */
    /*[deutsch]
     * <p>Element mit dem x-ten Wochentag im Monat. </p>
     */
    @FormattableElement(format = "F")
    public static final OrdinalWeekdayElement<ChineseCalendar> WEEKDAY_IN_MONTH = WIM_ELEMENT;

    private static final EastAsianCS<ChineseCalendar> CALSYS;
    private static final TimeAxis<ChineseCalendar.Unit, ChineseCalendar> ENGINE;

    static {
        CALSYS = new Transformer();

        TimeAxis.Builder<ChineseCalendar.Unit, ChineseCalendar> builder =
            TimeAxis.Builder.setUp(
                ChineseCalendar.Unit.class,
                ChineseCalendar.class,
                new Merger(),
                CALSYS)
            .appendElement(
                ERA,
                EraElement.INSTANCE)
            .appendElement(
                CYCLE,
                EastAsianCalendar.<ChineseCalendar>getCycleRule(YEAR_OF_CYCLE))
            .appendElement(
                YEAR_OF_ERA,
                new YearOfEraRule())
            .appendElement(
                YEAR_OF_CYCLE,
                EastAsianCalendar.<ChineseCalendar>getYearOfCycleRule(MONTH_OF_YEAR),
                Unit.YEARS)
            .appendElement(
                SOLAR_TERM,
                EastAsianST.<ChineseCalendar>getInstance())
            .appendElement(
                MONTH_OF_YEAR,
                EastAsianCalendar.<ChineseCalendar>getMonthOfYearRule(DAY_OF_MONTH),
                Unit.MONTHS)
            .appendElement(
                MONTH_AS_ORDINAL,
                EastAsianCalendar.<ChineseCalendar>getMonthAsOrdinalRule(DAY_OF_MONTH),
                Unit.MONTHS)
            .appendElement(
                DAY_OF_MONTH,
                EastAsianCalendar.<ChineseCalendar>getDayOfMonthRule(),
                Unit.DAYS)
            .appendElement(
                DAY_OF_YEAR,
                EastAsianCalendar.<ChineseCalendar>getDayOfYearRule(),
                Unit.DAYS)
            .appendElement(
                DAY_OF_WEEK,
                new WeekdayRule<ChineseCalendar>(
                    getDefaultWeekmodel(),
                    new ChronoFunction<ChineseCalendar, CalendarSystem<ChineseCalendar>>() {
                        @Override
                        public CalendarSystem<ChineseCalendar> apply(ChineseCalendar context) {
                            return CALSYS;
                        }
                    }
                ),
                Unit.DAYS)
            .appendElement(
                WIM_ELEMENT,
                WeekdayInMonthElement.getRule(WIM_ELEMENT))
            .appendElement(
                CommonElements.RELATED_GREGORIAN_YEAR,
                new RelatedGregorianYearRule<ChineseCalendar>(CALSYS, DAY_OF_YEAR))
            .appendUnit(
                Unit.CYCLES,
                EastAsianCalendar.<ChineseCalendar>getUnitRule(EastAsianCalendar.UNIT_CYCLES),
                Unit.CYCLES.getLength(),
                Collections.singleton(Unit.YEARS))
            .appendUnit(
                Unit.YEARS,
                EastAsianCalendar.<ChineseCalendar>getUnitRule(EastAsianCalendar.UNIT_YEARS),
                Unit.YEARS.getLength(),
                Collections.singleton(Unit.CYCLES))
            .appendUnit(
                Unit.MONTHS,
                EastAsianCalendar.<ChineseCalendar>getUnitRule(EastAsianCalendar.UNIT_MONTHS),
                Unit.MONTHS.getLength(),
                Collections.<Unit>emptySet())
            .appendUnit(
                Unit.WEEKS,
                EastAsianCalendar.<ChineseCalendar>getUnitRule(EastAsianCalendar.UNIT_WEEKS),
                Unit.WEEKS.getLength(),
                Collections.singleton(Unit.DAYS))
            .appendUnit(
                Unit.DAYS,
                EastAsianCalendar.<ChineseCalendar>getUnitRule(EastAsianCalendar.UNIT_DAYS),
                Unit.DAYS.getLength(),
                Collections.singleton(Unit.WEEKS))
            .appendExtension(
                new CommonElements.Weekengine(
                    ChineseCalendar.class,
                    DAY_OF_MONTH,
                    DAY_OF_YEAR,
                    getDefaultWeekmodel()));
        ENGINE = builder.build();
    }

    private static final long serialVersionUID = 8743381746750717307L;

    //~ Konstruktoren -----------------------------------------------------

    private ChineseCalendar(
        int cycle,
        int yearOfCycle,
        EastAsianMonth month,
        int dayOfMonth,
        long utcDays
    ) {
        super(cycle, yearOfCycle, month, dayOfMonth, utcDays);

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new instance of a Chinese calendar date on traditional New Year. </p>
     *
     * @param   gregorianYear   gregorian calendar year
     * @return  new instance of {@code ChineseCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    /*[deutsch]
     * <p>Erzeugt ein neues chinesisches Kalenderdatum am traditionellen Neujahrstag. </p>
     *
     * @param   gregorianYear   gregorian calendar year
     * @return  new instance of {@code ChineseCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    public static ChineseCalendar ofNewYear(int gregorianYear) {

        return ChineseCalendar.of(EastAsianYear.forGregorian(gregorianYear), EastAsianMonth.valueOf(1), 1);

    }

    /**
     * <p>Creates a new instance of a Chinese calendar date. </p>
     *
     * @param   year        references the year using different systems like eras or sexagesimal cycles
     * @param   month       the month which might be a leap month
     * @param   dayOfMonth  the day of month to be checked
     * @return  new instance of {@code ChineseCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    /*[deutsch]
     * <p>Erzeugt ein neues chinesisches Kalenderdatum. </p>
     *
     * @param   year        references the year using different systems like eras or sexagesimal cycles
     * @param   month       the month which might be a leap month
     * @param   dayOfMonth  the day of month to be checked
     * @return  new instance of {@code ChineseCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    public static ChineseCalendar of(
        EastAsianYear year,
        EastAsianMonth month,
        int dayOfMonth
    ) {

        int cycle = year.getCycle();
        int yearOfCycle = year.getYearOfCycle().getNumber();
        return ChineseCalendar.of(cycle, yearOfCycle, month, dayOfMonth);

    }

    /**
     * <p>Obtains the current calendar date in system time. </p>
     *
     * <p>Convenient short-cut for: {@code SystemClock.inLocalView().now(ChineseCalendar.axis())}. </p>
     *
     * @return  current calendar date in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(Chronology)
     */
    /*[deutsch]
     * <p>Ermittelt das aktuelle Kalenderdatum in der Systemzeit. </p>
     *
     * <p>Bequeme Abk&uuml;rzung f&uuml;r: {@code SystemClock.inLocalView().now(ChineseCalendar.axis())}. </p>
     *
     * @return  current calendar date in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(net.time4j.engine.Chronology)
     */
    public static ChineseCalendar nowInSystemTime() {

        return SystemClock.inLocalView().now(ChineseCalendar.axis());

    }

    /**
     * <p>Queries if given parameter values form a well defined calendar date. </p>
     *
     * @param   year        the year to be checked
     * @param   month       the month to be checked
     * @param   dayOfMonth  the day of month to be checked
     * @return  {@code true} if valid else {@code false}
     */
    /*[deutsch]
     * <p>Pr&uuml;ft, ob die angegebenen Parameter ein wohldefiniertes Kalenderdatum beschreiben. </p>
     *
     * @param   year        the year to be checked
     * @param   month       the month to be checked
     * @param   dayOfMonth  the day of month to be checked
     * @return  {@code true} if valid else {@code false}
     */
    public static boolean isValid(
        EastAsianYear year,
        EastAsianMonth month,
        int dayOfMonth
    ) {

        int cycle = year.getCycle();
        int yearOfCycle = year.getYearOfCycle().getNumber();
        return CALSYS.isValid(cycle, yearOfCycle, month, dayOfMonth);

    }

    /**
     * <p>Obtains the standard week model of this calendar. </p>
     *
     * <p>The Chinese calendar usually starts on Sunday. </p>
     *
     * @return  Weekmodel
     */
    /*[deutsch]
     * <p>Ermittelt das Standardwochenmodell dieses Kalenders. </p>
     *
     * <p>Der chinesische Kalender startet normalerweise am Sonntag. </p>
     *
     * @return  Weekmodel
     */
    public static Weekmodel getDefaultWeekmodel() {

        return Weekmodel.of(Locale.CHINA);

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
    public static TimeAxis<Unit, ChineseCalendar> axis() {

        return ENGINE;

    }

    @Override
    protected TimeAxis<Unit, ChineseCalendar> getChronology() {

        return ENGINE;

    }

    @Override
    protected ChineseCalendar getContext() {

        return this;

    }

    @Override
    EastAsianCS<ChineseCalendar> getCalendarSystem() {

        return CALSYS;

    }

    static ChineseCalendar of(
        int cycle,
        int yearOfCycle,
        EastAsianMonth month,
        int dayOfMonth
    ) {

        long utcDays = CALSYS.transform(cycle, yearOfCycle, month, dayOfMonth);
        return new ChineseCalendar(cycle, yearOfCycle, month, dayOfMonth, utcDays);

    }

    /**
     * @serialData  Uses <a href="../../../serialized-form.html#net.time4j.calendar.SPX">
     *              a dedicated serialization form</a> as proxy. The first byte contains
     *              the type-ID {@code 14}. Then the cycle, year-of-cycle and the month number
     *              are written as byte, finally the leap state of month as boolean and
     *              the day-of-month as byte.
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.CHINESE);

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
     * <p>Defines some calendar units for the Chinese calendar. </p>
     */
    /*[deutsch]
     * <p>Definiert einige kalendarische Zeiteinheiten f&uuml;r den chinesischen Kalender. </p>
     */
    public static enum Unit
        implements ChronoUnit {

        //~ Statische Felder/Initialisierungen ----------------------------

        CYCLES(EastAsianCS.MEAN_TROPICAL_YEAR * 86400.0 * 60),

        YEARS(EastAsianCS.MEAN_TROPICAL_YEAR * 86400.0),

        MONTHS(EastAsianCS.MEAN_SYNODIC_MONTH * 86400.0),

        WEEKS(7 * 86400.0),

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
         * <p>Calculates the difference between given Chinese dates in this unit. </p>
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
        public int between(
            ChineseCalendar start,
            ChineseCalendar end
        ) {

            return (int) start.until(end, this); // safe

        }

    }

    private static class Transformer
        extends EastAsianCS<ChineseCalendar> {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final ZonalOffset OFFSET_OLD_CHINA =
            ZonalOffset.atLongitude(OffsetSign.AHEAD_OF_UTC, 116, 25, 0.0);
        private static final ZonalOffset OFFSET_NEW_CHINA =
            ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 8);
        private static final long OFFSET_SWITCH_CHINA = -15705L; // 1929-01-01

        //~ Methoden ------------------------------------------------------

        @Override
        public List<CalendarEra> getEras() {
            return Arrays.<CalendarEra>asList(ChineseEra.values());
        }

        @Override
        ChineseCalendar create(
            int cycle,
            int yearOfCycle,
            EastAsianMonth eam,
            int dayOfMonth,
            long utcDays
        ) {
            return new ChineseCalendar(cycle, yearOfCycle, eam, dayOfMonth, utcDays);
        }

        @Override
        ZonalOffset getOffset(long utcDays) {
            return (utcDays < OFFSET_SWITCH_CHINA) ? OFFSET_OLD_CHINA : OFFSET_NEW_CHINA;
        }

        @Override
        int[] getLeapMonths() {
            return LEAP_MONTHS;
        }

    }

    private static class EraElement
        extends DisplayElement<ChineseEra>
        implements TextElement<ChineseEra>, ElementRule<ChineseCalendar, ChineseEra> {

        //~ Statische Felder/Initialisierungen ----------------------------

        static final EraElement INSTANCE = new EraElement();

        private static final long serialVersionUID = -7868534502157983978L;

        //~ Konstruktoren -------------------------------------------------

        private EraElement() {
            super("ERA");

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Class<ChineseEra> getType() {
            return ChineseEra.class;
        }

        @Override
        public char getSymbol() {
            return 'G';
        }

        @Override
        public ChineseEra getDefaultMinimum() {
            return ChineseEra.QING_SHUNZHI_1644_1662;
        }

        @Override
        public ChineseEra getDefaultMaximum() {
            return ChineseEra.YELLOW_EMPEROR;
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
        public ChineseEra getValue(ChineseCalendar context) {
            int relgregyear = this.getRelatedGregorianYear(context);

            if (relgregyear < 1662) {
                return ChineseEra.QING_SHUNZHI_1644_1662;
            } else if (relgregyear < 1723) {
                return ChineseEra.QING_KANGXI_1662_1723;
            } else if (relgregyear < 1736) {
                return ChineseEra.QING_YONGZHENG_1723_1736;
            } else if (relgregyear < 1796) {
                return ChineseEra.QING_QIANLONG_1736_1796;
            } else if (relgregyear < 1821) {
                return ChineseEra.QING_JIAQING_1796_1821;
            } else if (relgregyear < 1851) {
                return ChineseEra.QING_DAOGUANG_1821_1851;
            } else if (relgregyear < 1862) {
                return ChineseEra.QING_XIANFENG_1851_1862;
            } else if (relgregyear < 1875) {
                return ChineseEra.QING_TONGZHI_1862_1875;
            } else if (relgregyear < 1909) {
                return ChineseEra.QING_GUANGXU_1875_1909;
            } else if (context.getDaysSinceEpochUTC() < -21873L) { // < 1912-02-12
                return ChineseEra.QING_XUANTONG_1909_1912;
            } else {
                return ChineseEra.YELLOW_EMPEROR; // fallback
            }
        }

        @Override
        public ChineseEra getMinimum(ChineseCalendar context) {
            return ChineseEra.QING_SHUNZHI_1644_1662;
        }

        @Override
        public ChineseEra getMaximum(ChineseCalendar context) {
            return ChineseEra.YELLOW_EMPEROR;
        }

        @Override
        public boolean isValid(
            ChineseCalendar context,
            ChineseEra value
        ) {
            return (this.getValue(context) == value);
        }

        @Override
        public ChineseCalendar withValue(
            ChineseCalendar context,
            ChineseEra value,
            boolean lenient
        ) {
            if (this.isValid(context, value)) {
                return context;
            } else if (value == null) {
                throw new IllegalArgumentException("Missing Chinese era.");
            } else {
                throw new IllegalArgumentException("Chinese era is read-only.");
            }
        }

        @Override
        public ChronoElement<?> getChildAtFloor(ChineseCalendar context) {
            return YEAR_OF_ERA;
        }

        @Override
        public ChronoElement<?> getChildAtCeiling(ChineseCalendar context) {
            return YEAR_OF_ERA;
        }

        @Override
        public void print(
            ChronoDisplay context,
            Appendable buffer,
            AttributeQuery attributes
        ) throws IOException, ChronoException {
            Locale locale = attributes.get(Attributes.LANGUAGE, Locale.ROOT);
            TextWidth width = attributes.get(Attributes.TEXT_WIDTH, TextWidth.WIDE);
            String name = context.get(this).getDisplayName(locale, width);
            buffer.append(name);
        }

        @Override
        public ChineseEra parse(
            CharSequence text,
            ParsePosition status,
            AttributeQuery attributes
        ) {
            Locale locale = attributes.get(Attributes.LANGUAGE, Locale.ROOT);
            boolean caseInsensitive = attributes.get(Attributes.PARSE_CASE_INSENSITIVE, Boolean.TRUE).booleanValue();
            boolean partialCompare = attributes.get(Attributes.PARSE_PARTIAL_COMPARE, Boolean.FALSE).booleanValue();
            TextWidth width = attributes.get(Attributes.TEXT_WIDTH, TextWidth.WIDE);
            int offset = status.getIndex();

            for (ChineseEra era : ChineseEra.values()) {
                String name = era.getDisplayName(locale, width);
                int end = Math.max(Math.min(offset + name.length(), text.length()), offset);
                if (end > offset) {
                    String test = text.subSequence(offset, end).toString();
                    if (caseInsensitive) {
                        name = name.toLowerCase(locale);
                        test = test.toLowerCase(locale);
                    }
                    if (name.equals(test) || (partialCompare && name.startsWith(test))) {
                        status.setIndex(end);
                        return era;
                    }
                }
            }

            if (!locale.getLanguage().isEmpty() && !locale.getLanguage().equals("zh")) {
                // use root locale as fallback
                for (ChineseEra era : ChineseEra.values()) {
                    String name = era.getDisplayName(Locale.ROOT, width);
                    int end = Math.max(Math.min(offset + name.length(), text.length()), offset);
                    if (end > offset) {
                        String test = text.subSequence(offset, end).toString();
                        if (caseInsensitive) {
                            name = name.toLowerCase(Locale.ROOT);
                            test = test.toLowerCase(Locale.ROOT);
                        }
                        if (name.equals(test) || (partialCompare && name.startsWith(test))) {
                            status.setIndex(end);
                            return era;
                        }
                    }
                }
            }

            status.setErrorIndex(offset);
            return null;
        }

        @Override
        protected boolean isSingleton() {
            return true;
        }

        private Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }

        private int getRelatedGregorianYear(ChineseCalendar context) {
            int cycle = context.getCycle();
            int y = context.getYear().getNumber();
            return (cycle - 1) * 60 + y - 2637;
        }

    }

    private static class YearOfEraRule
        implements IntElementRule<ChineseCalendar> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Integer getValue(ChineseCalendar context) {
            return Integer.valueOf(this.getInt(context));
        }

        @Override
        public Integer getMinimum(ChineseCalendar context) {
            ChineseEra era = context.get(ERA);
            return Integer.valueOf(era.getMinYearOfEra());
        }

        @Override
        public Integer getMaximum(ChineseCalendar context) {
            ChineseEra era = context.get(ERA);
            return Integer.valueOf(era.getMaxYearOfEra());
        }

        @Override
        public boolean isValid(
            ChineseCalendar context,
            Integer value
        ) {
            return ((value != null) && this.isValid(context, value.intValue()));
        }

        @Override
        public ChineseCalendar withValue(
            ChineseCalendar context,
            Integer value,
            boolean lenient
        ) {
            if (value == null) {
                throw new IllegalArgumentException("Missing year of era.");
            }
            return this.withValue(context, value.intValue(), lenient);
        }

        @Override
        public ChronoElement<?> getChildAtFloor(ChineseCalendar context) {
            return MONTH_OF_YEAR;
        }

        @Override
        public ChronoElement<?> getChildAtCeiling(ChineseCalendar context) {
            return MONTH_OF_YEAR;
        }

        @Override
        public int getInt(ChineseCalendar context) {
            int relgregyear = EraElement.INSTANCE.getRelatedGregorianYear(context);
            if (relgregyear < 1662) {
                return relgregyear - 1644 + 1;
            } else if (relgregyear < 1723) {
                return relgregyear - 1662 + 1;
            } else if (relgregyear < 1736) {
                return relgregyear - 1723 + 1;
            } else if (relgregyear < 1796) {
                return relgregyear - 1736 + 1;
            } else if (relgregyear < 1821) {
                return relgregyear - 1796 + 1;
            } else if (relgregyear < 1851) {
                return relgregyear - 1821 + 1;
            } else if (relgregyear < 1862) {
                return relgregyear - 1851 + 1;
            } else if (relgregyear < 1875) {
                return relgregyear - 1862 + 1;
            } else if (relgregyear < 1909) {
                return relgregyear - 1875 + 1;
            } else if (context.getDaysSinceEpochUTC() < -21873L) { // < 1912-02-12
                return relgregyear - 1909 + 1;
            } else {
                return relgregyear + 2637 + 61;
            }
        }

        @Override
        public boolean isValid(
            ChineseCalendar context,
            int value
        ) {
            ChineseEra era = context.get(ERA);
            return ((value >= era.getMinYearOfEra()) && (value <= era.getMaxYearOfEra()));
        }

        @Override
        public ChineseCalendar withValue(
            ChineseCalendar context,
            int value,
            boolean lenient
        ) {
            if (this.isValid(context, value)) {
                int yoe = this.getInt(context);
                return context.plus(value - yoe, Unit.YEARS);
            } else {
                throw new IllegalArgumentException("Invalid year of era: " + value);
            }
        }
    }

    private static class Merger
        extends AbstractMergerEA<ChineseCalendar> {

        //~ Konstruktoren -------------------------------------------------

        Merger() {
            super(ChineseCalendar.class);

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public ChineseCalendar createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean lenient,
            boolean preparsing
        ) {

            EastAsianYear eastAsianYear = null;
            int relgregyear = entity.getInt(CommonElements.RELATED_GREGORIAN_YEAR);

            if (relgregyear == Integer.MIN_VALUE) {
                if (entity.contains(YEAR_OF_CYCLE)) {
                    CyclicYear cy = entity.get(YEAR_OF_CYCLE);
                    int cycle = entity.getInt(CYCLE);
                    if (cycle == Integer.MIN_VALUE) {
                        if (entity.contains(ERA)) {
                            ChineseEra era = entity.get(ERA);
                            if (era.isQingDynasty()) {
                                eastAsianYear = cy.inQingDynasty(era);
                            }
                        }
                    } else {
                        eastAsianYear = cy.inCycle(cycle);
                    }
                } else if (entity.contains(ERA)) {
                    int yoe = entity.getInt(YEAR_OF_ERA);
                    if (yoe != Integer.MIN_VALUE) {
                        ChineseEra era = entity.get(ERA);
                        eastAsianYear = EastAsianYear.forGregorian(era.getStartAsGregorianYear() + yoe - 1);
                    }
                }
            } else {
                eastAsianYear = EastAsianYear.forGregorian(relgregyear);
            }

            if (eastAsianYear == null) {
                entity.with(
                    ValidationElement.ERROR_MESSAGE,
                    "Cannot determine East Asian year.");
                return null;
            } else if (entity.contains(MONTH_OF_YEAR)) {
                EastAsianMonth month = entity.get(MONTH_OF_YEAR);
                int dom = entity.getInt(DAY_OF_MONTH);
                if (dom != Integer.MIN_VALUE) {
                    return ChineseCalendar.of(eastAsianYear, month, dom);
                }
            } else {
                int doy = entity.getInt(DAY_OF_YEAR);
                if ((doy != Integer.MIN_VALUE) && (doy >= 1)) {
                    ChineseCalendar cc = ChineseCalendar.of(eastAsianYear, EastAsianMonth.valueOf(1), 1);
                    return cc.plus(doy - 1, Unit.DAYS);
                }
            }

            return null;

        }

    }

}
