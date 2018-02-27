/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (KoreanCalendar.java) is part of project Time4J.
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

import net.time4j.PlainDate;
import net.time4j.SystemClock;
import net.time4j.Weekday;
import net.time4j.Weekmodel;
import net.time4j.calendar.service.StdIntegerDateElement;
import net.time4j.calendar.service.StdWeekdayElement;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.CalendarEra;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoFunction;
import net.time4j.engine.ChronoUnit;
import net.time4j.engine.Chronology;
import net.time4j.engine.ElementRule;
import net.time4j.engine.FormattableElement;
import net.time4j.engine.TimeAxis;
import net.time4j.engine.ValidationElement;
import net.time4j.format.CalendarType;
import net.time4j.format.LocalizedPatternSupport;
import net.time4j.format.TextElement;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * <p>Represents the traditional Koran calendar supported in the gregorian range 1645-01-28/3000-01-27. </p>
 *
 * <h4>Introduction</h4>
 *
 * <p>It is a lunisolar calendar which defines years consisting of 12 or 13 months. See also
 * <a href="https://en.wikipedia.org/wiki/Chinese_calendar">Wikipedia</a>. </p>
 *
 * <h4>Following elements which are declared as constants are registered by this class</h4>
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
 * <p>Furthermore, all elements defined in {@code KoreanEra}, {@code EpochDays}
 * and {@link CommonElements} are supported. </p>
 *
 * <h4>Example of usage</h4>
 *
 * <pre>
 *     ChronoFormatter&lt;KoreanCalendar&gt; formatter =
 *       ChronoFormatter.setUp(KoreanCalendar.axis(), Locale.ENGLISH)
 *       .addPattern(&quot;EEE, d. MMMM U(r)&quot;, PatternType.CLDR_DATE).build();
 *     PlainDate today = SystemClock.inLocalView().today();
 *     KoreanCalendar koreanDate = today.transform(KoreanCalendar.class);
 *     System.out.println(formatter.format(koreanDate));
 * </pre>
 *
 * @see     ChineseCalendar
 * @author  Meno Hochschild
 * @since   3.40/4.35
 */
/*[deutsch]
 * <p>Repr&auml;sentiert den koreanischen Kalender mit dem unterst&uuml;tzen (gregorianischen)
 * Bereich 1645-01-28/3000-01-27. </p>
 *
 * <h4>Einleitung</h4>
 *
 * <p>Es handelt sich um einen lunisolaren Kalender, dessen Jahre aus 12 oder 13 Monaten bestehen.
 * Siehe auch <a href="https://en.wikipedia.org/wiki/Chinese_calendar">Wikipedia</a>. </p>
 *
 * <h4>Registriert sind folgende als Konstanten deklarierte Elemente</h4>
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
 * <p>Au&slig;erdem werden alle Elemente von {@code KoreanEra}, {@code EpochDays}
 * und {@link CommonElements} unterst&uuml;tzt. </p>
 *
 * <h4>Anwendungsbeispiel</h4>
 *
 * <pre>
 *     ChronoFormatter&lt;KoreanCalendar&gt; formatter =
 *       ChronoFormatter.setUp(KoreanCalendar.axis(), Locale.ENGLISH)
 *       .addPattern(&quot;EEE, d. MMMM U(r)&quot;, PatternType.CLDR_DATE).build();
 *     PlainDate today = SystemClock.inLocalView().today();
 *     KoreanCalendar koreanDate = today.transform(KoreanCalendar.class);
 *     System.out.println(formatter.format(koreanDate));
 * </pre>
 *
 * @see     ChineseCalendar
 * @author  Meno Hochschild
 * @since   3.40/4.35
 */
@CalendarType("dangi")
public final class KoreanCalendar
    extends EastAsianCalendar<KoreanCalendar.Unit, KoreanCalendar>
    implements LocalizedPatternSupport {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int[] LEAP_MONTHS = {
        4281, 5, 4284, 4, 4287, 1, 4289, 6, 4292, 5, 4295, 3, 4297, 7, 4300, 6, 4303, 4, 4306, 2,
        4308, 7, 4311, 5, 4314, 3, 4316, 8, 4319, 6, 4322, 4, 4325, 3, 4327, 7, 4330, 5, 4333, 3,
        4335, 7, 4338, 6, 4341, 4, 4344, 3, 4346, 7, 4349, 5, 4352, 3, 4354, 8, 4357, 6, 4360, 4,
        4363, 2, 4365, 7, 4368, 5, 4371, 3, 4373, 9, 4376, 6, 4379, 4, 4382, 3, 4384, 7, 4387, 5,
        4390, 4, 4392, 9, 4395, 6, 4398, 5, 4401, 2, 4403, 7, 4406, 5, 4409, 3, 4411, 10, 4414, 6,
        4417, 5, 4420, 3, 4422, 7, 4425, 6, 4428, 4, 4431, 2, 4433, 6, 4436, 4, 4439, 3, 4441, 6,
        4444, 5, 4447, 3, 4450, 2, 4452, 6, 4455, 4, 4458, 3, 4460, 7, 4463, 5, 4466, 4, 4468, 9,
        4471, 6, 4474, 4, 4477, 3, 4479, 7, 4482, 5, 4485, 4, 4487, 11, 4490, 7, 4493, 5, 4496, 3,
        4498, 8, 4501, 5, 4504, 4, 4506, 10, 4509, 6, 4512, 5, 4515, 3, 4517, 7, 4520, 5, 4523, 4,
        4525, 12, 4528, 6, 4531, 5, 4534, 3, 4536, 8, 4539, 5, 4542, 4, 4545, 2, 4547, 6, 4550, 5,
        4553, 2, 4555, 7, 4558, 5, 4561, 4, 4564, 2, 4566, 6, 4569, 5, 4572, 3, 4574, 7, 4577, 6,
        4580, 4, 4583, 2, 4585, 7, 4588, 5, 4591, 3, 4593, 8, 4596, 6, 4599, 4, 4602, 3, 4604, 7,
        4607, 5, 4610, 4, 4612, 8, 4615, 6, 4618, 4, 4620, 10, 4623, 6, 4626, 5, 4629, 3, 4631, 8,
        4634, 5, 4637, 4, 4640, 2, 4642, 7, 4645, 5, 4648, 3, 4650, 9, 4653, 5, 4656, 4, 4659, 2,
        4661, 6, 4664, 5, 4667, 3, 4669, 11, 4672, 6, 4675, 5, 4678, 2, 4680, 7, 4683, 5, 4686, 3,
        4688, 8, 4691, 6, 4694, 4, 4697, 3, 4699, 7, 4702, 5, 4705, 4, 4707, 8, 4710, 6, 4713, 4,
        4716, 3, 4718, 7, 4721, 5, 4724, 4, 4726, 8, 4729, 6, 4732, 4, 4735, 3, 4737, 7, 4740, 5,
        4743, 4, 4745, 9, 4748, 6, 4751, 4, 4754, 3, 4756, 7, 4759, 5, 4762, 4, 4764, 9, 4767, 6,
        4770, 5, 4773, 2, 4775, 7, 4778, 5, 4781, 4, 4783, 11, 4786, 6, 4789, 5, 4792, 3, 4794, 7,
        4797, 6, 4800, 4, 4802, 10, 4805, 6, 4808, 4, 4811, 3, 4813, 7, 4816, 6, 4819, 4, 4822, 2,
        4824, 7, 4827, 5, 4830, 3, 4832, 7, 4835, 6, 4838, 4, 4840, 9, 4843, 6, 4846, 4, 4849, 3,
        4851, 7, 4854, 5, 4857, 4, 4859, 9, 4862, 7, 4865, 5, 4868, 3, 4870, 8, 4873, 5, 4876, 4,
        4878, 11, 4881, 6, 4884, 5, 4887, 3, 4889, 8, 4892, 6, 4895, 4, 4898, 1, 4900, 6, 4903, 5,
        4906, 3, 4908, 8, 4911, 6, 4914, 4, 4917, 2, 4919, 6, 4922, 5, 4925, 3, 4927, 7, 4930, 6,
        4933, 4, 4936, 2, 4938, 6, 4941, 5, 4944, 3, 4946, 7, 4949, 6, 4952, 4, 4955, 2, 4957, 7,
        4960, 5, 4963, 3, 4965, 8, 4968, 6, 4971, 4, 4974, 3, 4976, 7, 4979, 5, 4982, 4, 4984, 8,
        4987, 6, 4990, 5, 4993, 2, 4995, 7, 4998, 5, 5001, 4, 5003, 8, 5006, 6, 5009, 5, 5012, 2,
        5014, 7, 5017, 5, 5020, 4, 5022, 10, 5025, 6, 5028, 4, 5031, 2, 5033, 6, 5036, 5, 5039, 3,
        5041, 8, 5044, 6, 5047, 5, 5050, 2, 5052, 7, 5055, 5, 5058, 3, 5060, 8, 5063, 6, 5066, 4,
        5069, 3, 5071, 7, 5074, 5, 5077, 4, 5079, 8, 5082, 6, 5085, 5, 5088, 3, 5090, 8, 5093, 5,
        5096, 4, 5098, 8, 5101, 6, 5104, 5, 5107, 3, 5109, 7, 5112, 5, 5115, 4, 5117, 8, 5120, 6,
        5123, 5, 5126, 3, 5128, 7, 5131, 5, 5134, 4, 5136, 10, 5139, 6, 5142, 5, 5145, 2, 5147, 7,
        5150, 5, 5153, 4, 5156, 2, 5158, 6, 5161, 5, 5164, 3, 5166, 7, 5169, 6, 5172, 4, 5175, 1,
        5177, 7, 5180, 5, 5183, 3, 5185, 8, 5188, 6, 5191, 4, 5193, 8, 5196, 7, 5199, 5, 5202, 4,
        5204, 8, 5207, 6, 5210, 4, 5212, 8, 5215, 7, 5218, 5, 5221, 3, 5223, 7, 5226, 6, 5229, 4,
        5231, 10, 5234, 7, 5237, 5, 5240, 3, 5242, 8, 5245, 5, 5248, 4, 5250, 11, 5253, 6, 5256, 5,
        5259, 3, 5261, 8, 5264, 6, 5267, 5, 5270, 1, 5272, 7, 5275, 5, 5278, 3, 5280, 8, 5283, 6,
        5286, 4, 5289, 2, 5291, 7, 5294, 5, 5297, 3, 5299, 8, 5302, 6, 5305, 4, 5308, 3, 5310, 7,
        5313, 5, 5316, 3, 5318, 7, 5321, 6, 5324, 4, 5327, 3, 5329, 7, 5332, 5, 5335, 3, 5337, 8,
        5340, 6, 5343, 4, 5345, 10, 5348, 7, 5351, 5, 5354, 4, 5356, 9, 5359, 6, 5362, 5, 5364, 11,
        5367, 7, 5370, 5, 5373, 4, 5375, 9, 5378, 6, 5381, 5, 5384, 1, 5386, 7, 5389, 6, 5392, 4,
        5394, 8, 5397, 6, 5400, 5, 5403, 3, 5405, 7, 5408, 6, 5411, 4, 5413, 8, 5416, 6, 5419, 5,
        5422, 3, 5424, 7, 5427, 6, 5430, 3, 5432, 8, 5435, 6, 5438, 4, 5441, 3, 5443, 7, 5446, 6,
        5449, 4, 5451, 9, 5454, 7, 5457, 5, 5460, 3, 5462, 8, 5465, 5, 5468, 4, 5470, 9, 5473, 6,
        5476, 5, 5479, 3, 5481, 8, 5484, 6, 5487, 4, 5489, 9, 5492, 6, 5495, 5, 5498, 3, 5500, 7,
        5503, 6, 5506, 4, 5508, 10, 5511, 6, 5514, 5, 5517, 3, 5519, 7, 5522, 6, 5525, 4, 5527, 10,
        5530, 6, 5533, 5, 5536, 3, 5538, 7, 5541, 6, 5544, 4, 5546, 11, 5549, 7, 5552, 5, 5555, 3,
        5557, 8, 5560, 6, 5563, 4, 5565, 9, 5568, 7, 5571, 5, 5574, 4, 5576, 8, 5579, 6, 5582, 4,
        5584, 11, 5587, 7, 5590, 5, 5593, 4, 5595, 8, 5598, 6, 5601, 5, 5603, 10, 5606, 7, 5609, 5,
        5612, 3, 5614, 8, 5617, 6, 5620, 4, 5622, 10, 5625, 6, 5628, 5, 5631, 4, 5633, 9, 5636, 6
    };

    /**
     * <p>Represents the Korean era. </p>
     *
     * <p>This element is effectively read-only. Its value cannot be changed in a direct and meaningful way.
     * The dangi era can also be used in conjunction with {@code PlainDate}. </p>
     *
     * @see     #YEAR_OF_ERA
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die koreanische &Auml;ra. </p>
     *
     * <p>Dieses Element ist effektiv nur zur Anzeige. Sein Wert kann nicht direkt und sinnvoll ge&auml;ndert
     * werden. Die Dangi-&Auml;ra kann auch in Verbindung mit {@code PlainDate} verwendet werden. </p>
     *
     * @see     #YEAR_OF_ERA
     */
    @FormattableElement(format = "G")
    public static final ChronoElement<KoreanEra> ERA = KoreanEra.DANGI.era();

    /**
     * <p>Represents the cycle number related to the introduction of sexagesimal cycles
     * by the legendary yellow emperor Huang-Di on -2636-02-15 (gregorian). </p>
     *
     * <p><strong>This kind of counting is NOT in common use in Korea and only
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
     * <p><strong>Diese Art der Z&auml;hlung ist in Korea NICHT gebr&auml;uchlich und wird nur aus
     * technischen Gr&uuml;nden angeboten.</strong> Dem zyklischen Jahr in Verbindung mit einer
     * gregorianischen Bezugsjahresangabe ist der Vorzug zu geben. </p>
     *
     * @see     #YEAR_OF_CYCLE
     * @see     CommonElements#RELATED_GREGORIAN_YEAR
     */
    public static final ChronoElement<Integer> CYCLE =
        new StdIntegerDateElement<KoreanCalendar>(
            "CYCLE",
            KoreanCalendar.class,
            72,
            94,
            '\u0000',
            null,
            null);

    /**
     * <p>Represents the Korean year related to the Korean era. </p>
     *
     * <p><strong>This kind of year counting is NOT in common use in Korea today and only
     * offered for historical reasons.</strong> Prefer the cyclic year instead. The dangi era
     * can also be used in conjunction with {@code PlainDate}. It was in use in South Korea
     * from 1952 until 1961: </p>
     *
     * <pre>
     *    ChronoFormatter&lt;PlainDate&gt; f =
     *      ChronoFormatter.setUp(PlainDate.axis(), Locale.ENGLISH)
     *        .addText(KoreanEra.DANGI.era())
     *        .addLiteral(&quot; &quot;)
     *        .addInteger(KoreanEra.DANGI.yearOfEra(), 1, 4)
     *        .addPattern(&quot;, MM/dd&quot;, PatternType.CLDR)
     *        .build();
     *    assertThat(
     *      f.format(PlainDate.of(2018, 10, 1)),
     *      is(&quot;Dangi 4351, 10/01&quot;));
     *    assertThat(
     *      f.parse(&quot;Dangi 4351, 10/01&quot;),
     *      is(PlainDate.of(2018, 10, 1)));
     * </pre>
     *
     * @see     #ERA
     * @see     #YEAR_OF_CYCLE
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert das koreanische Jahr relativ zur koreanischen &Auml;ra. </p>
     *
     * <p><strong>Diese Art der Jahresz&auml;hlung ist in Korea heute NICHT gebr&auml;uchlich und
     * wird nur aus historischen Gr&uuml;nden angeboten.</strong> Dem zyklischen Jahr ist der Vorzug
     * zu geben. Die Dangi-&Auml;ra kann auch in Verbindung mit {@code PlainDate} verwendet werden
     * (verwendet in S&uuml;dkorea von 1952 bis 1961): </p>
     *
     * <pre>
     *    ChronoFormatter&lt;PlainDate&gt; f =
     *      ChronoFormatter.setUp(PlainDate.axis(), Locale.ENGLISH)
     *        .addText(KoreanEra.DANGI.era())
     *        .addLiteral(&quot; &quot;)
     *        .addInteger(KoreanEra.DANGI.yearOfEra(), 1, 4)
     *        .addPattern(&quot;, MM/dd&quot;, PatternType.CLDR)
     *        .build();
     *    assertThat(
     *      f.format(PlainDate.of(2018, 10, 1)),
     *      is(&quot;Dangi 4351, 10/01&quot;));
     *    assertThat(
     *      f.parse(&quot;Dangi 4351, 10/01&quot;),
     *      is(PlainDate.of(2018, 10, 1)));
     * </pre>
     *
     * @see     #ERA
     * @see     #YEAR_OF_CYCLE
     */
    @FormattableElement(format = "y")
    public static final ChronoElement<Integer> YEAR_OF_ERA = KoreanEra.DANGI.yearOfEra();

    /**
     * <p>Represents the Korean year related to the current sexagesimal cycle. </p>
     *
     * <p>This is the standard way to specify a Korean year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert das koreanische Jahr des aktuellen sexagesimalen Zyklus. </p>
     *
     * <p>Das ist der Standardweg, ein koreanisches Jahr zu definieren. </p>
     */
    @FormattableElement(format = "U")
    public static final TextElement<CyclicYear> YEAR_OF_CYCLE = EastAsianCY.SINGLETON;

    /**
     * <p>Represents the solar term as one of 24 possible stations of the sun on the ecliptic. </p>
     *
     * @see     ChineseCalendar#SOLAR_TERM
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den Sonnenmonat als eine von 24 m&ouml;glichen Stationen auf der Ekliptik der Sonne. </p>
     *
     * @see     ChineseCalendar#SOLAR_TERM
     */
    public static final ChronoElement<SolarTerm> SOLAR_TERM = EastAsianST.getInstance();

    /**
     * <p>Represents the Korean month. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den koreanischen Monat. </p>
     */
    @FormattableElement(format = "M", standalone = "L")
    public static final TextElement<EastAsianMonth> MONTH_OF_YEAR = EastAsianME.SINGLETON_EA;

    /**
     * <p>Represents the ordinal index of a Korean month in the range {@code 1-12/13}. </p>
     *
     * <p>This element can be used in conjunction with
     * {@link net.time4j.format.expert.ChronoFormatter.Builder#addOrdinal(ChronoElement, Map) ordinal formatting}. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die Ordnungsnummer eines koreanischen Monats im Bereich {@code 1-12/13}. </p>
     *
     * <p>Dieses Element kann in Verbindung mit einem
     * {@link net.time4j.format.expert.ChronoFormatter.Builder#addOrdinal(ChronoElement, Map) OrdinalFormat}
     * verwendet werden. </p>
     */
    public static final StdCalendarElement<Integer, KoreanCalendar> MONTH_AS_ORDINAL =
        new StdIntegerDateElement<KoreanCalendar>(
            "MONTH_AS_ORDINAL", KoreanCalendar.class, 1, 12, '\u0000', null, null);

    /**
     * <p>Represents the Korean day of month. </p>
     *
     * <p>Months have either 29 or 30 days. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den koreanischen Tag des Monats. </p>
     *
     * <p>Monate haben entweder 29 oder 30 Tage. </p>
     */
    @FormattableElement(format = "d")
    public static final StdCalendarElement<Integer, KoreanCalendar> DAY_OF_MONTH =
        new StdIntegerDateElement<KoreanCalendar>("DAY_OF_MONTH", KoreanCalendar.class, 1, 30, 'd');

    /**
     * <p>Represents the Korean day of year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den koreanischen Tag des Jahres. </p>
     */
    @FormattableElement(format = "D")
    public static final StdCalendarElement<Integer, KoreanCalendar> DAY_OF_YEAR =
        new StdIntegerDateElement<KoreanCalendar>("DAY_OF_YEAR", KoreanCalendar.class, 1, 355, 'D');

    /**
     * <p>Represents the Korean day of week. </p>
     *
     * <p>If the day-of-week is set to a new value then Time4J handles the Korean calendar week
     * as starting on Sunday (like in South-Korea). </p>
     *
     * @see     #getDefaultWeekmodel()
     * @see     CommonElements#localDayOfWeek(Chronology, Weekmodel)
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den koreanischen Tag der Woche. </p>
     *
     * <p>Wenn der Tag der Woche auf einen neuen Wert gesetzt wird, behandelt Time4J die koreanische
     * Kalenderwoche so, da&szlig; sie am Sonntag beginnt (wie in S&uuml;dkorea). </p>
     *
     * @see     #getDefaultWeekmodel()
     * @see     CommonElements#localDayOfWeek(Chronology, Weekmodel)
     */
    @FormattableElement(format = "E")
    public static final StdCalendarElement<Weekday, KoreanCalendar> DAY_OF_WEEK =
        new StdWeekdayElement<KoreanCalendar>(KoreanCalendar.class, getDefaultWeekmodel());

    private static final WeekdayInMonthElement<KoreanCalendar> WIM_ELEMENT =
        new WeekdayInMonthElement<KoreanCalendar>(KoreanCalendar.class, DAY_OF_MONTH, DAY_OF_WEEK);

    /**
     * <p>Element with the ordinal day-of-week within given calendar month. </p>
     */
    /*[deutsch]
     * <p>Element mit dem x-ten Wochentag im Monat. </p>
     */
    @FormattableElement(format = "F")
    public static final OrdinalWeekdayElement<KoreanCalendar> WEEKDAY_IN_MONTH = WIM_ELEMENT;

    private static final EastAsianCS<KoreanCalendar> CALSYS;
    private static final TimeAxis<KoreanCalendar.Unit, KoreanCalendar> ENGINE;

    static {
        CALSYS = new Transformer();

        TimeAxis.Builder<KoreanCalendar.Unit, KoreanCalendar> builder =
            TimeAxis.Builder.setUp(
                KoreanCalendar.Unit.class,
                KoreanCalendar.class,
                new Merger(),
                CALSYS)
            .appendElement(
                ERA,
                new EraRule())
            .appendElement(
                CYCLE,
                EastAsianCalendar.<KoreanCalendar>getCycleRule(YEAR_OF_CYCLE))
            .appendElement(
                YEAR_OF_ERA,
                new YearOfEraRule(),
                Unit.YEARS)
            .appendElement(
                YEAR_OF_CYCLE,
                EastAsianCalendar.<KoreanCalendar>getYearOfCycleRule(MONTH_OF_YEAR),
                Unit.YEARS)
            .appendElement(
                SOLAR_TERM,
                EastAsianST.<KoreanCalendar>getInstance())
            .appendElement(
                MONTH_OF_YEAR,
                EastAsianCalendar.<KoreanCalendar>getMonthOfYearRule(DAY_OF_MONTH),
                Unit.MONTHS)
            .appendElement(
                MONTH_AS_ORDINAL,
                EastAsianCalendar.<KoreanCalendar>getMonthAsOrdinalRule(DAY_OF_MONTH),
                Unit.MONTHS)
            .appendElement(
                DAY_OF_MONTH,
                EastAsianCalendar.<KoreanCalendar>getDayOfMonthRule(),
                Unit.DAYS)
            .appendElement(
                DAY_OF_YEAR,
                EastAsianCalendar.<KoreanCalendar>getDayOfYearRule(),
                Unit.DAYS)
            .appendElement(
                DAY_OF_WEEK,
                new WeekdayRule<KoreanCalendar>(getDefaultWeekmodel(),
                    new ChronoFunction<KoreanCalendar, CalendarSystem<KoreanCalendar>>() {
                        @Override
                        public CalendarSystem<KoreanCalendar> apply(KoreanCalendar context) {
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
                new RelatedGregorianYearRule<KoreanCalendar>(CALSYS, DAY_OF_YEAR))
            .appendUnit(
                Unit.CYCLES,
                EastAsianCalendar.<KoreanCalendar>getUnitRule(EastAsianCalendar.UNIT_CYCLES),
                Unit.CYCLES.getLength(),
                Collections.singleton(Unit.YEARS))
            .appendUnit(
                Unit.YEARS,
                EastAsianCalendar.<KoreanCalendar>getUnitRule(EastAsianCalendar.UNIT_YEARS),
                Unit.YEARS.getLength(),
                Collections.singleton(Unit.CYCLES))
            .appendUnit(
                Unit.MONTHS,
                EastAsianCalendar.<KoreanCalendar>getUnitRule(EastAsianCalendar.UNIT_MONTHS),
                Unit.MONTHS.getLength(),
                Collections.<Unit>emptySet())
            .appendUnit(
                Unit.WEEKS,
                EastAsianCalendar.<KoreanCalendar>getUnitRule(EastAsianCalendar.UNIT_WEEKS),
                Unit.WEEKS.getLength(),
                Collections.singleton(Unit.DAYS))
            .appendUnit(
                Unit.DAYS,
                EastAsianCalendar.<KoreanCalendar>getUnitRule(EastAsianCalendar.UNIT_DAYS),
                Unit.DAYS.getLength(),
                Collections.singleton(Unit.WEEKS))
            .appendExtension(
                new CommonElements.Weekengine(
                    KoreanCalendar.class,
                    DAY_OF_MONTH,
                    DAY_OF_YEAR,
                    getDefaultWeekmodel()));
        ENGINE = builder.build();
    }

    private static final long serialVersionUID = -4284841131270593971L;

    //~ Konstruktoren -----------------------------------------------------

    private KoreanCalendar(
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
     * <p>Creates a new instance of a Korean calendar date on traditional New Year. </p>
     *
     * @param   gregorianYear   gregorian calendar year
     * @return  new instance of {@code KoreanCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    /*[deutsch]
     * <p>Erzeugt ein neues koreanisches Kalenderdatum am traditionellen Neujahrstag. </p>
     *
     * @param   gregorianYear   gregorian calendar year
     * @return  new instance of {@code KoreanCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    public static KoreanCalendar ofNewYear(int gregorianYear) {

        return KoreanCalendar.of(EastAsianYear.forGregorian(gregorianYear), EastAsianMonth.valueOf(1), 1);

    }

    /**
     * <p>Creates a new instance of a Korean calendar date. </p>
     *
     * @param   year        references the year using different systems like eras or sexagesimal cycles
     * @param   month       the month which might be a leap month
     * @param   dayOfMonth  the day of month to be checked
     * @return  new instance of {@code KoreanCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    /*[deutsch]
     * <p>Erzeugt ein neues koreanisches Kalenderdatum. </p>
     *
     * @param   year        references the year using different systems like eras or sexagesimal cycles
     * @param   month       the month which might be a leap month
     * @param   dayOfMonth  the day of month to be checked
     * @return  new instance of {@code KoreanCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    public static KoreanCalendar of(
        EastAsianYear year,
        EastAsianMonth month,
        int dayOfMonth
    ) {

        int cycle = year.getCycle();
        int yearOfCycle = year.getYearOfCycle().getNumber();
        return KoreanCalendar.of(cycle, yearOfCycle, month, dayOfMonth);

    }

    /**
     * <p>Obtains the current calendar date in system time. </p>
     *
     * <p>Convenient short-cut for: {@code SystemClock.inLocalView().now(KoreanCalendar.axis())}. </p>
     *
     * @return  current calendar date in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(Chronology)
     */
    /*[deutsch]
     * <p>Ermittelt das aktuelle Kalenderdatum in der Systemzeit. </p>
     *
     * <p>Bequeme Abk&uuml;rzung f&uuml;r: {@code SystemClock.inLocalView().now(KoreanCalendar.axis())}. </p>
     *
     * @return  current calendar date in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(net.time4j.engine.Chronology)
     */
    public static KoreanCalendar nowInSystemTime() {

        return SystemClock.inLocalView().now(KoreanCalendar.axis());

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
     * <p>The Korean calendar usually starts on Sunday (in South Korea). </p>
     *
     * @return  Weekmodel
     */
    /*[deutsch]
     * <p>Ermittelt das Standardwochenmodell dieses Kalenders. </p>
     *
     * <p>Der koreanische Kalender startet (in S&uuml;dkorea) normalerweise am Sonntag. </p>
     *
     * @return  Weekmodel
     */
    public static Weekmodel getDefaultWeekmodel() {

        return Weekmodel.of(new Locale("ko", "KR"));

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
    public static TimeAxis<Unit, KoreanCalendar> axis() {

        return ENGINE;

    }

    @Override
    protected TimeAxis<Unit, KoreanCalendar> getChronology() {

        return ENGINE;

    }

    @Override
    protected KoreanCalendar getContext() {

        return this;

    }

    @Override
    EastAsianCS<KoreanCalendar> getCalendarSystem() {

        return CALSYS;

    }

    static KoreanCalendar of(
        int cycle,
        int yearOfCycle,
        EastAsianMonth month,
        int dayOfMonth
    ) {

        long utcDays = CALSYS.transform(cycle, yearOfCycle, month, dayOfMonth);
        return new KoreanCalendar(cycle, yearOfCycle, month, dayOfMonth, utcDays);

    }

    /**
     * @serialData  Uses <a href="../../../serialized-form.html#net.time4j.calendar.SPX">
     *              a dedicated serialization form</a> as proxy. The first byte contains
     *              the type-ID {@code 15}. Then the cycle, year-of-cycle and the month number
     *              are written as byte, finally the leap state of month as boolean and
     *              the day-of-month as byte.
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.KOREAN);

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
     * <p>Defines some calendar units for the Korean calendar. </p>
     */
    /*[deutsch]
     * <p>Definiert einige kalendarische Zeiteinheiten f&uuml;r den koreanischen Kalender. </p>
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
         * <p>Calculates the difference between given Korean dates in this unit. </p>
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
            KoreanCalendar start,
            KoreanCalendar end
        ) {

            return (int) start.until(end, this); // safe

        }

    }

    private static class Transformer
        extends EastAsianCS<KoreanCalendar> {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final List<ZonalOffset> OFFSETS;

        static {
            List<ZonalOffset> offsets = new ArrayList<ZonalOffset>(5);
            offsets.add(ZonalOffset.atLongitude(OffsetSign.AHEAD_OF_UTC, 126, 58, 0.0));
            offsets.add(ZonalOffset.ofHoursMinutes(OffsetSign.AHEAD_OF_UTC, 8, 30));
            offsets.add(ZonalOffset.ofHoursMinutes(OffsetSign.AHEAD_OF_UTC, 9, 0));
            offsets.add(ZonalOffset.ofHoursMinutes(OffsetSign.AHEAD_OF_UTC, 8, 30));
            offsets.add(ZonalOffset.ofHoursMinutes(OffsetSign.AHEAD_OF_UTC, 9, 0));
            OFFSETS = Collections.unmodifiableList(offsets);
        }

        private static final long DATE_1908_04_01 = PlainDate.of(1908, 4, 1).getDaysSinceEpochUTC();
        private static final long DATE_1912_01_01 = PlainDate.of(1912, 1, 1).getDaysSinceEpochUTC();
        private static final long DATE_1954_03_21 = PlainDate.of(1954, 3, 21).getDaysSinceEpochUTC();
        private static final long DATE_1961_08_10 = PlainDate.of(1961, 8, 10).getDaysSinceEpochUTC();

        //~ Methoden ------------------------------------------------------

        @Override
        public List<CalendarEra> getEras() {
            return Collections.<CalendarEra>singletonList(KoreanEra.DANGI);
        }

        @Override
        KoreanCalendar create(
            int cycle,
            int yearOfCycle,
            EastAsianMonth eam,
            int dayOfMonth,
            long utcDays
        ) {
            return new KoreanCalendar(cycle, yearOfCycle, eam, dayOfMonth, utcDays);
        }

        @Override
        ZonalOffset getOffset(long utcDays) {
            if (utcDays < DATE_1908_04_01) {
                return OFFSETS.get(0);
            } else if (utcDays < DATE_1912_01_01) {
                return OFFSETS.get(1);
            } else if (utcDays < DATE_1954_03_21) {
                return OFFSETS.get(2);
            } else if (utcDays < DATE_1961_08_10) {
                return OFFSETS.get(3);
            } else {
                return OFFSETS.get(4);
            }
        }

        @Override
        int[] getLeapMonths() {
            return LEAP_MONTHS;
        }

    }

    private static class EraRule
        implements ElementRule<KoreanCalendar, KoreanEra> {

        //~ Methoden ------------------------------------------------------

        @Override
        public KoreanEra getValue(KoreanCalendar context) {
            return KoreanEra.DANGI;
        }

        @Override
        public KoreanEra getMinimum(KoreanCalendar context) {
            return KoreanEra.DANGI;
        }

        @Override
        public KoreanEra getMaximum(KoreanCalendar context) {
            return KoreanEra.DANGI;
        }

        @Override
        public boolean isValid(
            KoreanCalendar context,
            KoreanEra value
        ) {
            return (value == KoreanEra.DANGI);
        }

        @Override
        public KoreanCalendar withValue(
            KoreanCalendar context,
            KoreanEra value,
            boolean lenient
        ) {
            if (this.isValid(context, value)) {
                return context;
            } else {
                throw new IllegalArgumentException("Invalid Korean era: " + value);
            }
        }

        @Override
        public ChronoElement<?> getChildAtFloor(KoreanCalendar context) {
            throw new AbstractMethodError("Never called.");
        }

        @Override
        public ChronoElement<?> getChildAtCeiling(KoreanCalendar context) {
            throw new AbstractMethodError("Never called.");
        }

    }

    private static class YearOfEraRule
        implements ElementRule<KoreanCalendar, Integer> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Integer getValue(KoreanCalendar context) {
            return Integer.valueOf(this.getInt(context));
        }

        @Override
        public Integer getMinimum(KoreanCalendar context) {
            return Integer.valueOf(1645 + 2333);
        }

        @Override
        public Integer getMaximum(KoreanCalendar context) {
            return Integer.valueOf(2999 + 2333);
        }

        @Override
        public boolean isValid(
            KoreanCalendar context,
            Integer value
        ) {
            if (value == null) {
                return false;
            }
            int min = this.getMinimum(context).intValue();
            int max = this.getMaximum(context).intValue();
            return ((value >= min) && (value <= max));
        }

        @Override
        public KoreanCalendar withValue(
            KoreanCalendar context,
            Integer value,
            boolean lenient
        ) {
            if (value == null) {
                throw new IllegalArgumentException("Missing year of era.");
            } else if (this.isValid(context, value)) {
                int yoe = this.getInt(context);
                return context.plus(value - yoe, KoreanCalendar.Unit.YEARS);
            } else {
                throw new IllegalArgumentException("Invalid year of era: " + value);
            }
        }

        @Override
        public ChronoElement<?> getChildAtFloor(KoreanCalendar context) {
            throw new AbstractMethodError("Never called.");
        }

        @Override
        public ChronoElement<?> getChildAtCeiling(KoreanCalendar context) {
            throw new AbstractMethodError("Never called.");
        }

        private int getInt(KoreanCalendar context) {
            return 60 * context.getCycle() + context.getYear().getNumber() - 364;
        }

    }

    private static class Merger
        extends AbstractMergerEA<KoreanCalendar> {

        //~ Konstruktoren -------------------------------------------------

        Merger() {
            super(KoreanCalendar.class);

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public KoreanCalendar createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean lenient,
            boolean preparsing
        ) {

            EastAsianYear eastAsianYear = null;
            int relgregyear = entity.getInt(CommonElements.RELATED_GREGORIAN_YEAR);

            if (relgregyear == Integer.MIN_VALUE) {
                if (entity.contains(YEAR_OF_CYCLE) && entity.contains(CYCLE)) {
                    CyclicYear cy = entity.get(YEAR_OF_CYCLE);
                    int cycle = entity.getInt(CYCLE);
                    eastAsianYear = cy.inCycle(cycle);
                } else {
                    int yoe = entity.getInt(KoreanEra.DANGI.yearOfEra());
                    if (yoe != Integer.MIN_VALUE) {
                        eastAsianYear = EastAsianYear.forDangi(yoe);
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
                    return KoreanCalendar.of(eastAsianYear, month, dom);
                }
            } else {
                int doy = entity.getInt(DAY_OF_YEAR);
                if ((doy != Integer.MIN_VALUE) && (doy >= 1)) {
                    KoreanCalendar cc = KoreanCalendar.of(eastAsianYear, EastAsianMonth.valueOf(1), 1);
                    return cc.plus(doy - 1, Unit.DAYS);
                }
            }

            return null;

        }

    }

}
