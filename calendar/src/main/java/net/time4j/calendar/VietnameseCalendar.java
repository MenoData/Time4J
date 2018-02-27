/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (VietnameseCalendar.java) is part of project Time4J.
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
 * <p>Represents the Vietnamese calendar supported in the gregorian range 1813-02-01/3000-01-27. </p>
 *
 * <h4>Introduction</h4>
 *
 * <p>It is a lunisolar calendar which is supposed to be structurally identical to the Chinese calendar.
 * See also the page of <a href="https://www.informatik.uni-leipzig.de/~duc/amlich/">Ho Ngoc Duc</a>. The
 * historic details of the calendar rules before 1954 are somehow debatable. Probably different authorities or
 * institutions in Vietnam used slightly different versions of Chinese calendar. Time4J follows the rules
 * described on <a href="https://en.wikipedia.org/wiki/Vietnamese_calendar">Wikipedia</a>. </p>
 *
 * <ul>
 *     <li>1813–1840: It was essentially the Shíxiàn calendar introduced
 *     by the Chinese Qing dynasty to the Vietnamese Nguyễn dynasty.</li>
 *     <li>1841-1954: Hiệp-kỷ-calendar began to differ from Shíxiàn due
 *     to longitudinal differences between Vietnam and China.</li>
 *     <li>1955-1967: UTC+8 is used for calendar calculations.</li>
 *     <li>since 1968: UTC+7 is used (first in North Vietnam,
 *     since reunification in 1975 also in Southern part).</li>
 * </ul>
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
 *  <li>{@link #CYCLE}</li>
 * </ul>
 *
 * <p>Furthermore, all elements defined in {@code EpochDays} and {@link CommonElements} are supported. </p>
 *
 * <h4>Example of usage</h4>
 *
 * <pre>
 *     ChronoFormatter&lt;VietnameseCalendar&gt; formatter =
 *       ChronoFormatter.setUp(VietnameseCalendar.axis(), Locale.ENGLISH)
 *       .addPattern(&quot;EEE, d. MMMM U(r)&quot;, PatternType.CLDR_DATE).build();
 *     PlainDate today = SystemClock.inLocalView().today();
 *     VietnameseCalendar vietDate = today.transform(VietnameseCalendar.class);
 *     System.out.println(formatter.format(vietDate));
 * </pre>
 *
 * @see     ChineseCalendar
 * @author  Meno Hochschild
 * @since   3.40/4.35
 */
/*[deutsch]
 * <p>Repr&auml;sentiert den vietnamesischen Kalender mit dem unterst&uuml;tzen (gregorianischen)
 * Bereich 1813-02-01/3000-01-27. </p>
 *
 * <h4>Einleitung</h4>
 *
 * <p>Es handelt sich um einen lunisolaren Kalender, der strukturell mit dem chinesischen Kalender identisch ist.
 * Siehe auch die Webseite von <a href="https://www.informatik.uni-leipzig.de/~duc/amlich/">Ho Ngoc Duc</a>. Die
 * historischen Details der Kalenderregeln vor 1954 sind leicht unsicher. Wahrscheinlich haben verschiedene
 * Autorit&auml;ten und Institutionen in Vietnam leicht ver&auml;nderte Versionen des chinesischen Kalenders
 * benutzt. Time4J folgt den in <a href="https://en.wikipedia.org/wiki/Vietnamese_calendar">Wikipedia</a>
 * beschriebenen Regeln. </p>
 *
 * <ul>
 *     <li>1813–1840: Der Shíxiàn-Kalender der chinesischen Qing-Dynastie wurde von der vietnamesischen
 *     Nguyễn-Dynastie angewandt.</li>
 *     <li>1841-1954: Der sogenannte Hiệp-kỷ-Kalender fing an, sich von Shíxiàn durch longitudinale
 *     Zeitzonendifferenzen zwischen Vietnam und China zu unterscheiden.</li>
 *     <li>1955-1967: UTC+8 wird f&uuml;r die kalendarischen Berechnungen verwendet.</li>
 *     <li>since 1968: UTC+7 wird angewandt (zuerst in Nordvietnam, seit der Wiedervereinigung im Jahre 1975
 *     auch in S&uuml;dvietnam).</li>
 * </ul>
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
 *  <li>{@link #CYCLE}</li>
 * </ul>
 *
 * <p>Au&slig;erdem werden alle Elemente von {@code EpochDays} und {@link CommonElements} unterst&uuml;tzt. </p>
 *
 * <h4>Anwendungsbeispiel</h4>
 *
 * <pre>
 *     ChronoFormatter&lt;VietnameseCalendar&gt; formatter =
 *       ChronoFormatter.setUp(VietnameseCalendar.axis(), Locale.ENGLISH)
 *       .addPattern(&quot;EEE, d. MMMM U(r)&quot;, PatternType.CLDR_DATE).build();
 *     PlainDate today = SystemClock.inLocalView().today();
 *     VietnameseCalendar vietDate = today.transform(VietnameseCalendar.class);
 *     System.out.println(formatter.format(vietDate));
 * </pre>
 *
 * @see     ChineseCalendar
 * @author  Meno Hochschild
 * @since   3.40/4.35
 */
@CalendarType("vietnam")
public final class VietnameseCalendar
    extends EastAsianCalendar<VietnameseCalendar.Unit, VietnameseCalendar>
    implements LocalizedPatternSupport {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int[] LEAP_MONTHS = {
        4450, 2, 4452, 6, 4455, 4, 4458, 3, 4460, 7, 4463, 5, 4466, 4, 4468, 9, 4471, 6, 4474, 4,
        4477, 3, 4479, 7, 4482, 5, 4485, 4, 4487, 8, 4490, 7, 4493, 5, 4496, 3, 4498, 8, 4501, 5,
        4504, 4, 4506, 10, 4509, 6, 4512, 5, 4515, 3, 4517, 7, 4520, 5, 4523, 4, 4526, 2, 4528, 6,
        4531, 5, 4534, 3, 4536, 8, 4539, 5, 4542, 4, 4545, 2, 4547, 6, 4550, 5, 4553, 3, 4555, 7,
        4558, 6, 4561, 4, 4564, 2, 4566, 6, 4569, 5, 4572, 3, 4574, 7, 4577, 6, 4580, 4, 4583, 2,
        4585, 7, 4588, 5, 4591, 3, 4593, 8, 4596, 6, 4599, 4, 4602, 3, 4604, 7, 4607, 5, 4610, 4,
        4612, 8, 4615, 6, 4618, 4, 4621, 2, 4623, 7, 4626, 5, 4629, 3, 4631, 8, 4634, 5, 4637, 4,
        4640, 2, 4642, 7, 4645, 5, 4648, 4, 4650, 9, 4653, 6, 4656, 4, 4659, 2, 4661, 6, 4664, 5,
        4667, 3, 4669, 11, 4672, 6, 4675, 5, 4678, 2, 4680, 7, 4683, 5, 4686, 3, 4688, 8, 4691, 6,
        4694, 4, 4697, 3, 4699, 7, 4702, 5, 4705, 4, 4707, 8, 4710, 6, 4713, 4, 4716, 3, 4718, 7,
        4721, 5, 4724, 4, 4726, 8, 4729, 6, 4732, 4, 4735, 2, 4737, 7, 4740, 5, 4743, 4, 4745, 9,
        4748, 6, 4751, 4, 4754, 3, 4756, 7, 4759, 5, 4762, 4, 4764, 11, 4767, 6, 4770, 5, 4773, 2,
        4775, 7, 4778, 5, 4781, 4, 4784, 1, 4786, 6, 4789, 5, 4792, 3, 4794, 7, 4797, 6, 4800, 4,
        4802, 10, 4805, 6, 4808, 5, 4811, 3, 4813, 7, 4816, 6, 4819, 4, 4822, 2, 4824, 6, 4827, 5,
        4830, 3, 4832, 7, 4835, 6, 4838, 4, 4840, 9, 4843, 6, 4846, 4, 4849, 3, 4851, 7, 4854, 5,
        4857, 4, 4859, 11, 4862, 7, 4865, 5, 4868, 3, 4870, 8, 4873, 5, 4876, 4, 4878, 11, 4881, 6,
        4884, 5, 4887, 3, 4889, 7, 4892, 6, 4895, 4, 4898, 1, 4900, 6, 4903, 5, 4906, 3, 4908, 8,
        4911, 6, 4914, 4, 4917, 2, 4919, 6, 4922, 5, 4925, 3, 4927, 7, 4930, 6, 4933, 4, 4936, 2,
        4938, 6, 4941, 5, 4944, 3, 4946, 7, 4949, 6, 4952, 4, 4954, 10, 4957, 7, 4960, 5, 4963, 3,
        4965, 8, 4968, 6, 4971, 4, 4974, 3, 4976, 7, 4979, 5, 4982, 4, 4984, 8, 4987, 6, 4990, 5,
        4993, 1, 4995, 7, 4998, 5, 5001, 4, 5003, 8, 5006, 6, 5009, 5, 5012, 2, 5014, 7, 5017, 5,
        5020, 4, 5022, 10, 5025, 6, 5028, 4, 5031, 2, 5033, 6, 5036, 5, 5039, 3, 5041, 7, 5044, 6,
        5047, 5, 5050, 2, 5052, 7, 5055, 5, 5058, 3, 5060, 8, 5063, 6, 5066, 4, 5069, 3, 5071, 7,
        5074, 5, 5077, 4, 5079, 8, 5082, 7, 5085, 5, 5088, 3, 5090, 8, 5093, 5, 5096, 4, 5098, 8,
        5101, 6, 5104, 5, 5107, 3, 5109, 7, 5112, 5, 5115, 4, 5117, 10, 5120, 6, 5123, 5, 5126, 3,
        5128, 7, 5131, 5, 5134, 4, 5136, 10, 5139, 6, 5142, 5, 5145, 2, 5147, 7, 5150, 6, 5153, 4,
        5156, 1, 5158, 6, 5161, 5, 5164, 3, 5166, 7, 5169, 6, 5172, 4, 5174, 10, 5177, 7, 5180, 5,
        5183, 3, 5185, 7, 5188, 6, 5191, 4, 5193, 8, 5196, 7, 5199, 5, 5202, 3, 5204, 7, 5207, 6,
        5210, 4, 5212, 10, 5215, 6, 5218, 5, 5221, 3, 5223, 7, 5226, 5, 5229, 4, 5231, 9, 5234, 7,
        5237, 5, 5240, 3, 5242, 8, 5245, 6, 5248, 4, 5250, 11, 5253, 6, 5256, 5, 5259, 3, 5261, 8,
        5264, 6, 5267, 5, 5270, 1, 5272, 7, 5275, 5, 5278, 3, 5280, 8, 5283, 6, 5286, 4, 5289, 2,
        5291, 7, 5294, 5, 5297, 3, 5299, 7, 5302, 6, 5305, 4, 5308, 3, 5310, 7, 5313, 5, 5316, 3,
        5318, 7, 5321, 6, 5324, 4, 5327, 2, 5329, 7, 5332, 5, 5335, 3, 5337, 8, 5340, 6, 5343, 4,
        5346, 3, 5348, 7, 5351, 5, 5354, 4, 5356, 9, 5359, 6, 5362, 5, 5364, 11, 5367, 7, 5370, 5,
        5373, 4, 5375, 9, 5378, 6, 5381, 5, 5384, 2, 5386, 7, 5389, 6, 5392, 4, 5394, 8, 5397, 6,
        5400, 5, 5403, 3, 5405, 7, 5408, 6, 5411, 3, 5413, 8, 5416, 6, 5419, 5, 5422, 3, 5424, 7,
        5427, 6, 5430, 3, 5432, 8, 5435, 6, 5438, 4, 5441, 3, 5443, 7, 5446, 6, 5449, 4, 5451, 9,
        5454, 7, 5457, 5, 5460, 3, 5462, 8, 5465, 5, 5468, 4, 5470, 9, 5473, 6, 5476, 5, 5479, 3,
        5481, 8, 5484, 6, 5487, 5, 5489, 9, 5492, 7, 5495, 5, 5498, 3, 5500, 7, 5503, 6, 5506, 4,
        5508, 10, 5511, 6, 5514, 5, 5517, 3, 5519, 7, 5522, 6, 5525, 4, 5527, 10, 5530, 6, 5533, 5,
        5536, 3, 5538, 7, 5541, 6, 5544, 4, 5546, 11, 5549, 7, 5552, 5, 5555, 3, 5557, 8, 5560, 6,
        5563, 4, 5565, 9, 5568, 7, 5571, 5, 5574, 4, 5576, 8, 5579, 6, 5582, 4, 5584, 8, 5587, 7,
        5590, 5, 5593, 4, 5595, 8, 5598, 6, 5601, 4, 5603, 10, 5606, 7, 5609, 5, 5612, 3, 5614, 8,
        5617, 6, 5620, 4, 5622, 10, 5625, 6, 5628, 5, 5631, 3, 5633, 8, 5636, 6
    };

    /**
     * <p>Represents the cycle number related to the introduction of sexagesimal cycles
     * by the legendary Chinese yellow emperor Huang-Di on -2636-02-15 (gregorian). </p>
     *
     * <p><strong>This kind of counting is NOT in common use in Vietnam and only
     * offered for technical reasons.</strong> Prefer just the cyclic year together
     * with the related gregorian year instead. </p>
     *
     * @see     #YEAR_OF_CYCLE
     * @see     CommonElements#RELATED_GREGORIAN_YEAR
     */
    /*[deutsch]
     * <p>Nummer des Jahreszyklus relativ zur Einf&uuml;hrung der sexagesimalen Zyklen
     * durch den legendenhaften chinesischen Kaiser Huang-Di am Tag -2636-02-15 (gregorianisch). </p>
     *
     * <p><strong>Diese Art der Z&auml;hlung ist in Vietnam NICHT gebr&auml;uchlich und wird nur aus
     * technischen Gr&uuml;nden angeboten.</strong> Dem zyklischen Jahr in Verbindung mit einer
     * gregorianischen Bezugsjahresangabe ist der Vorzug zu geben. </p>
     *
     * @see     #YEAR_OF_CYCLE
     * @see     CommonElements#RELATED_GREGORIAN_YEAR
     */
    public static final ChronoElement<Integer> CYCLE =
        new StdIntegerDateElement<VietnameseCalendar>(
            "CYCLE",
            VietnameseCalendar.class,
            75,
            94,
            '\u0000',
            null,
            null);

    /**
     * <p>Represents the Vietnamese year related to the current sexagesimal cycle. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert das vietnamesische Jahr des aktuellen sexagesimalen Zyklus. </p>
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
     * <p>Represents the Vietnamese month. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den vietnamesischen Monat. </p>
     */
    @FormattableElement(format = "M", standalone = "L")
    public static final TextElement<EastAsianMonth> MONTH_OF_YEAR = EastAsianME.SINGLETON_EA;

    /**
     * <p>Represents the ordinal index of a Vietnamese month in the range {@code 1-12/13}. </p>
     *
     * <p>This element can be used in conjunction with
     * {@link net.time4j.format.expert.ChronoFormatter.Builder#addOrdinal(ChronoElement, Map) ordinal formatting}. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die Ordnungsnummer eines vietnamesischen Monats im Bereich {@code 1-12/13}. </p>
     *
     * <p>Dieses Element kann in Verbindung mit einem
     * {@link net.time4j.format.expert.ChronoFormatter.Builder#addOrdinal(ChronoElement, Map) OrdinalFormat}
     * verwendet werden. </p>
     */
    public static final StdCalendarElement<Integer, VietnameseCalendar> MONTH_AS_ORDINAL =
        new StdIntegerDateElement<VietnameseCalendar>(
            "MONTH_AS_ORDINAL", VietnameseCalendar.class, 1, 12, '\u0000', null, null);

    /**
     * <p>Represents the Vietnamese day of month. </p>
     *
     * <p>Months have either 29 or 30 days. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den vietnamesischen Tag des Monats. </p>
     *
     * <p>Monate haben entweder 29 oder 30 Tage. </p>
     */
    @FormattableElement(format = "d")
    public static final StdCalendarElement<Integer, VietnameseCalendar> DAY_OF_MONTH =
        new StdIntegerDateElement<VietnameseCalendar>("DAY_OF_MONTH", VietnameseCalendar.class, 1, 30, 'd');

    /**
     * <p>Represents the Vietnamese day of year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den vietnamesischen Tag des Jahres. </p>
     */
    @FormattableElement(format = "D")
    public static final StdCalendarElement<Integer, VietnameseCalendar> DAY_OF_YEAR =
        new StdIntegerDateElement<VietnameseCalendar>("DAY_OF_YEAR", VietnameseCalendar.class, 1, 355, 'D');

    /**
     * <p>Represents the Vietnamese day of week. </p>
     *
     * <p>If the day-of-week is set to a new value then Time4J handles the Chinese calendar week
     * as starting on Monday (like in modern Vietnam). </p>
     *
     * @see     #getDefaultWeekmodel()
     * @see     CommonElements#localDayOfWeek(Chronology, Weekmodel)
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den vietnamesischen Tag der Woche. </p>
     *
     * <p>Wenn der Tag der Woche auf einen neuen Wert gesetzt wird, behandelt Time4J die vietnamesische
     * Kalenderwoche so, da&szlig; sie am Montag beginnt (wie im heutigen Vietnam). </p>
     *
     * @see     #getDefaultWeekmodel()
     * @see     CommonElements#localDayOfWeek(Chronology, Weekmodel)
     */
    @FormattableElement(format = "E")
    public static final StdCalendarElement<Weekday, VietnameseCalendar> DAY_OF_WEEK =
        new StdWeekdayElement<VietnameseCalendar>(VietnameseCalendar.class, getDefaultWeekmodel());

    private static final WeekdayInMonthElement<VietnameseCalendar> WIM_ELEMENT =
        new WeekdayInMonthElement<VietnameseCalendar>(VietnameseCalendar.class, DAY_OF_MONTH, DAY_OF_WEEK);

    /**
     * <p>Element with the ordinal day-of-week within given calendar month. </p>
     */
    /*[deutsch]
     * <p>Element mit dem x-ten Wochentag im Monat. </p>
     */
    @FormattableElement(format = "F")
    public static final OrdinalWeekdayElement<VietnameseCalendar> WEEKDAY_IN_MONTH = WIM_ELEMENT;

    private static final EastAsianCS<VietnameseCalendar> CALSYS;
    private static final TimeAxis<VietnameseCalendar.Unit, VietnameseCalendar> ENGINE;

    static {
        CALSYS = new Transformer();

        TimeAxis.Builder<VietnameseCalendar.Unit, VietnameseCalendar> builder =
            TimeAxis.Builder.setUp(
                VietnameseCalendar.Unit.class,
                VietnameseCalendar.class,
                new Merger(),
                CALSYS)
            .appendElement(
                CYCLE,
                EastAsianCalendar.<VietnameseCalendar>getCycleRule(YEAR_OF_CYCLE))
            .appendElement(
                YEAR_OF_CYCLE,
                EastAsianCalendar.<VietnameseCalendar>getVietYearOfCycleRule(MONTH_OF_YEAR),
                Unit.YEARS)
            .appendElement(
                SOLAR_TERM,
                EastAsianST.<VietnameseCalendar>getInstance())
            .appendElement(
                MONTH_OF_YEAR,
                EastAsianCalendar.<VietnameseCalendar>getMonthOfYearRule(DAY_OF_MONTH),
                Unit.MONTHS)
            .appendElement(
                MONTH_AS_ORDINAL,
                EastAsianCalendar.<VietnameseCalendar>getMonthAsOrdinalRule(DAY_OF_MONTH),
                Unit.MONTHS)
            .appendElement(
                DAY_OF_MONTH,
                EastAsianCalendar.<VietnameseCalendar>getDayOfMonthRule(),
                Unit.DAYS)
            .appendElement(
                DAY_OF_YEAR,
                EastAsianCalendar.<VietnameseCalendar>getDayOfYearRule(),
                Unit.DAYS)
            .appendElement(
                DAY_OF_WEEK,
                new WeekdayRule<VietnameseCalendar>(
                    getDefaultWeekmodel(),
                    new ChronoFunction<VietnameseCalendar, CalendarSystem<VietnameseCalendar>>() {
                        @Override
                        public CalendarSystem<VietnameseCalendar> apply(VietnameseCalendar context) {
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
                new RelatedGregorianYearRule<VietnameseCalendar>(CALSYS, DAY_OF_YEAR))
            .appendUnit(
                Unit.CYCLES,
                EastAsianCalendar.<VietnameseCalendar>getUnitRule(EastAsianCalendar.UNIT_CYCLES),
                Unit.CYCLES.getLength(),
                Collections.singleton(Unit.YEARS))
            .appendUnit(
                Unit.YEARS,
                EastAsianCalendar.<VietnameseCalendar>getUnitRule(EastAsianCalendar.UNIT_YEARS),
                Unit.YEARS.getLength(),
                Collections.singleton(Unit.CYCLES))
            .appendUnit(
                Unit.MONTHS,
                EastAsianCalendar.<VietnameseCalendar>getUnitRule(EastAsianCalendar.UNIT_MONTHS),
                Unit.MONTHS.getLength(),
                Collections.<Unit>emptySet())
            .appendUnit(
                Unit.WEEKS,
                EastAsianCalendar.<VietnameseCalendar>getUnitRule(EastAsianCalendar.UNIT_WEEKS),
                Unit.WEEKS.getLength(),
                Collections.singleton(Unit.DAYS))
            .appendUnit(
                Unit.DAYS,
                EastAsianCalendar.<VietnameseCalendar>getUnitRule(EastAsianCalendar.UNIT_DAYS),
                Unit.DAYS.getLength(),
                Collections.singleton(Unit.WEEKS))
            .appendExtension(
                new CommonElements.Weekengine(
                    VietnameseCalendar.class,
                    DAY_OF_MONTH,
                    DAY_OF_YEAR,
                    getDefaultWeekmodel()));
        ENGINE = builder.build();
    }

    private static final long serialVersionUID = -3151525803739185874L;

    //~ Konstruktoren -----------------------------------------------------

    private VietnameseCalendar(
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
     * <p>Creates a new instance of a Vietnamese calendar date on New Year (Tet). </p>
     *
     * @param   gregorianYear   gregorian calendar year
     * @return  new instance of {@code VietnameseCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    /*[deutsch]
     * <p>Erzeugt ein neues vietnamesisches Kalenderdatum am Neujahrstag (Tet). </p>
     *
     * @param   gregorianYear   gregorian calendar year
     * @return  new instance of {@code VietnameseCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    public static VietnameseCalendar ofTet(int gregorianYear) {

        return VietnameseCalendar.of(EastAsianYear.forGregorian(gregorianYear), EastAsianMonth.valueOf(1), 1);

    }

    /**
     * <p>Creates a new instance of a Vietnamese calendar date. </p>
     *
     * @param   year        references the year using sexagesimal cycles
     * @param   month       the month which might be a leap month
     * @param   dayOfMonth  the day of month to be checked
     * @return  new instance of {@code ChineseCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    /*[deutsch]
     * <p>Erzeugt ein neues vietnamesisches Kalenderdatum. </p>
     *
     * @param   year        references the year using sexagesimal cycles
     * @param   month       the month which might be a leap month
     * @param   dayOfMonth  the day of month to be checked
     * @return  new instance of {@code ChineseCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    public static VietnameseCalendar of(
        EastAsianYear year,
        EastAsianMonth month,
        int dayOfMonth
    ) {

        int cycle = year.getCycle();
        int yearOfCycle = year.getYearOfCycle().getNumber();
        return VietnameseCalendar.of(cycle, yearOfCycle, month, dayOfMonth);

    }

    /**
     * <p>Obtains the current calendar date in system time. </p>
     *
     * <p>Convenient short-cut for: {@code SystemClock.inLocalView().now(VietnameseCalendar.axis())}. </p>
     *
     * @return  current calendar date in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(Chronology)
     */
    /*[deutsch]
     * <p>Ermittelt das aktuelle Kalenderdatum in der Systemzeit. </p>
     *
     * <p>Bequeme Abk&uuml;rzung f&uuml;r: {@code SystemClock.inLocalView().now(VietnameseCalendar.axis())}. </p>
     *
     * @return  current calendar date in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(net.time4j.engine.Chronology)
     */
    public static VietnameseCalendar nowInSystemTime() {

        return SystemClock.inLocalView().now(VietnameseCalendar.axis());

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
     * <p>The modern Vietnamese calendar usually starts on Monday. </p>
     *
     * @return  Weekmodel
     */
    /*[deutsch]
     * <p>Ermittelt das Standardwochenmodell dieses Kalenders. </p>
     *
     * <p>Der moderne vietnamesische Kalender startet normalerweise am Montag. </p>
     *
     * @return  Weekmodel
     */
    public static Weekmodel getDefaultWeekmodel() {

        return Weekmodel.of(new Locale("vi", "VN"));

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
    public static TimeAxis<Unit, VietnameseCalendar> axis() {

        return ENGINE;

    }

    @Override
    protected TimeAxis<Unit, VietnameseCalendar> getChronology() {

        return ENGINE;

    }

    @Override
    protected VietnameseCalendar getContext() {

        return this;

    }

    @Override
    EastAsianCS<VietnameseCalendar> getCalendarSystem() {

        return CALSYS;

    }

    static VietnameseCalendar of(
        int cycle,
        int yearOfCycle,
        EastAsianMonth month,
        int dayOfMonth
    ) {

        long utcDays = CALSYS.transform(cycle, yearOfCycle, month, dayOfMonth);
        return new VietnameseCalendar(cycle, yearOfCycle, month, dayOfMonth, utcDays);

    }

    /**
     * @serialData  Uses <a href="../../../serialized-form.html#net.time4j.calendar.SPX">
     *              a dedicated serialization form</a> as proxy. The first byte contains
     *              the type-ID {@code 16}. Then the cycle, year-of-cycle and the month number
     *              are written as byte, finally the leap state of month as boolean and
     *              the day-of-month as byte.
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.VIETNAMESE);

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
     * <p>Defines some calendar units for the Vietnamese calendar. </p>
     */
    /*[deutsch]
     * <p>Definiert einige kalendarische Zeiteinheiten f&uuml;r den vietnamesischen Kalender. </p>
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
         * <p>Calculates the difference between given Vietnamese dates in this unit. </p>
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
            VietnameseCalendar start,
            VietnameseCalendar end
        ) {

            return (int) start.until(end, this); // safe

        }

    }

    private static class Transformer
        extends EastAsianCS<VietnameseCalendar> {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final List<ZonalOffset> OFFSETS;

        static {
            List<ZonalOffset> offsets = new ArrayList<ZonalOffset>(5);
            offsets.add(ZonalOffset.atLongitude(OffsetSign.AHEAD_OF_UTC, 116, 25, 0.0)); // Old China
            offsets.add(ZonalOffset.atLongitude(OffsetSign.AHEAD_OF_UTC, 107, 35, 0.0)); // Huế
            offsets.add(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 8));
            offsets.add(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 7));
            OFFSETS = Collections.unmodifiableList(offsets);
        }

        private static final long OFFSET_SWITCH_1841 = PlainDate.of(1841, 1, 1).getDaysSinceEpochUTC();
        private static final long OFFSET_SWITCH_1954 = PlainDate.of(1954, 7, 1).getDaysSinceEpochUTC();
        private static final long OFFSET_SWITCH_1968 = PlainDate.of(1968, 1, 1).getDaysSinceEpochUTC();

        private static final long MIN_LIMIT = PlainDate.of(1813, 2, 1).getDaysSinceEpochUTC(); // new year 1813

        //~ Methoden ------------------------------------------------------

        @Override
        public long getMinimumSinceUTC() {
            return MIN_LIMIT;
        }

        @Override
        public List<CalendarEra> getEras() {
            return Collections.emptyList();
        }

        @Override
        VietnameseCalendar create(
            int cycle,
            int yearOfCycle,
            EastAsianMonth eam,
            int dayOfMonth,
            long utcDays
        ) {
            return new VietnameseCalendar(cycle, yearOfCycle, eam, dayOfMonth, utcDays);
        }

        @Override
        ZonalOffset getOffset(long utcDays) {
            return (utcDays < OFFSET_SWITCH_1841)
                ? OFFSETS.get(0)
                : ((utcDays < OFFSET_SWITCH_1954
                    ? OFFSETS.get(1)
                    : (utcDays < OFFSET_SWITCH_1968 ? OFFSETS.get(2) : OFFSETS.get(3))));
        }

        @Override
        int[] getLeapMonths() {
            return LEAP_MONTHS;
        }

        @Override
        boolean isValid(
            int cycle,
            int yearOfCycle,
            EastAsianMonth month,
            int dayOfMonth
        ) {
            if ((cycle < 75) || ((cycle == 75) && (yearOfCycle < 10))) {
                return false;
            } else {
                return super.isValid(cycle, yearOfCycle, month, dayOfMonth);
            }
        }

    }

    private static class Merger
        extends AbstractMergerEA<VietnameseCalendar> {

        //~ Konstruktoren -------------------------------------------------

        Merger() {
            super(VietnameseCalendar.class);

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public VietnameseCalendar createFrom(
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
                    if (cycle != Integer.MIN_VALUE) {
                        eastAsianYear = cy.inCycle(cycle);
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
                    return VietnameseCalendar.of(eastAsianYear, month, dom);
                }
            } else {
                int doy = entity.getInt(DAY_OF_YEAR);
                if ((doy != Integer.MIN_VALUE) && (doy >= 1)) {
                    VietnameseCalendar cc = VietnameseCalendar.of(eastAsianYear, EastAsianMonth.valueOf(1), 1);
                    return cc.plus(doy - 1, Unit.DAYS);
                }
            }

            return null;

        }

    }

}
