/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (JapaneseCalendar.java) is part of project Time4J.
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

import net.time4j.GeneralTimestamp;
import net.time4j.Moment;
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.SystemClock;
import net.time4j.Weekday;
import net.time4j.Weekmodel;
import net.time4j.base.GregorianMath;
import net.time4j.base.MathUtils;
import net.time4j.base.ResourceLoader;
import net.time4j.base.TimeSource;
import net.time4j.calendar.service.GenericDatePatterns;
import net.time4j.calendar.service.StdIntegerDateElement;
import net.time4j.calendar.service.StdWeekdayElement;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.CalendarDate;
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
import net.time4j.engine.DisplayStyle;
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
import net.time4j.format.Leniency;
import net.time4j.format.LocalizedPatternSupport;
import net.time4j.format.NumberSystem;
import net.time4j.format.OutputContext;
import net.time4j.format.TextAccessor;
import net.time4j.format.TextElement;
import net.time4j.format.TextWidth;
import net.time4j.format.internal.DualFormatElement;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.net.URI;
import java.text.ParsePosition;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static net.time4j.calendar.Nengo.Selector.*;


/**
 * <p>Represents the Japanese calendar from 701 AD (julian) until now. </p>
 *
 * <p>It is a mixed calendar, lunisolar before Meiji 6 (= 1873-01-01) and then gregorian. A special era system
 * is used to count years where a Japanese era is also called {@code Nengo}. The first year of an era or nengo
 * always has the number 1. Before Meiji, nengos were announced at any irregular time, often even more than
 * one nengo during the reign of an emperor. In midage during the nanboku-ch&#333;-period (1334-1392), there
 * were even two concurrent nengo systems in effect (southern versus northern court causing a certain kind
 * of non-temporal state in year counting and sorting). In modern times, nengos are associated only once with
 * the actual emperor or tenno. </p>
 *
 * <p>While the gregorian period since Meiji 6 uses exactly the same mnnths from January to December) and
 * day-dating methods like in Western calendar, the lunisolar period in old times was completely different.
 * All months were either 29 or 30 days long. The month length depended on the astronomical event of New Moon,
 * but did often not follow exactly the new moon due to historical deviations and misleading calculations. The
 * Japanese people had originally adopted their calendar from China. Therefore the old lunisolar calendar knew
 * sometimes leap months in order to compensate for the difference between lunar cycles and the revolution
 * of the earth around the sun. Such a leap month could be inserted any time during a lunisolar year,
 * usually every 2 or 3 years. A leap month has the same number as the preceding month but is indicated
 * by a special char (Time4J uses the asterisk for non-East-Asian languages). See also
 * <a href="https://en.wikipedia.org/wiki/Japanese_calendar">Wikipedia</a>. </p>
 *
 * <p>Note: The lunisolar calendar part does not use astronomical calculations but depends on the original
 * chronological tables from Paul Y. Tsuchihashi for the purpose of greatest historical accuracy. </p>
 *
 * <p>Following elements which are declared as constants are registered by
 * this class: </p>
 *
 * <ul>
 *  <li>{@link #DAY_OF_WEEK}</li>
 *  <li>{@link #DAY_OF_MONTH}</li>
 *  <li>{@link #DAY_OF_YEAR}</li>
 *  <li>{@link #WEEKDAY_IN_MONTH}</li>
 *  <li>{@link #MONTH_OF_YEAR}</li>
 *  <li>{@link #MONTH_AS_ORDINAL}</li>
 *  <li>{@link #KOKI_YEAR}</li>
 *  <li>{@link #YEAR_OF_ERA}</li>
 *  <li>{@link #ERA}</li>
 * </ul>
 *
 * <p>Furthermore, all elements defined in {@code EpochDays} and {@link CommonElements} are supported. </p>
 *
 * <p>Example of usage: </p>
 *
 * <pre>
 *     ChronoFormatter&lt;JapaneseCalendar&gt; formatter =
 *       ChronoFormatter.ofPattern(
 *         &quot;MMMM/dd, G y&quot;,
 *         PatternType.NON_ISO_DATE,
 *         Locale.ENGLISH,
 *         JapaneseCalendar.axis());
 *     JapaneseCalendar jcal = JapaneseCalendar.ofGregorian(Nengo.HEISEI, 29, 4, 14);
 *     System.out.println(formatter.format(jcal)); // April/14, Heisei 29
 *
 *     ChronoFormatter&lt;JapaneseCalendar&gt; parser =
 *       ChronoFormatter.ofPattern(
 *         &quot;Gy年M月d日&quot;,
 *         PatternType.NON_ISO_DATE,
 *         Locale.JAPANESE,
 *         JapaneseCalendar.axis()
 *       ).with(Leniency.LAX); // use parsed nengo (attention: Ansei-1 == Kaei-7)
 *     System.out.println(parser.parse(&quot;安政元年閏7月14日&quot;)); // Ansei-1(1854)-*7-14
 * </pre>
 *
 * <p>The second example also uses the preferred form for the first year of a nengo as &quot;元&quot; (gannen).
 * Another special feature: Two-digit-years using a pivot year are effectively switched off even if the pattern
 * &quot;yy&quot; is used (but users should avoid this pattern and prefer &quot;y&quot;). </p>
 *
 * @author  Meno Hochschild
 * @see     Nengo
 * @see     EastAsianMonth
 * @see     NumberSystem#JAPANESE
 * @since   3.32/4.27
 */
/*[deutsch]
 * <p>Repr&auml;sentiert den japanischen Kalender von 701 AD (julianisch) bis heute. </p>
 *
 * <p>Es ist ein gemischter Kalender, lunisolar vor Meiji 6 (= 1873-01-01) und dann gregorianisch. Eine
 * besondere Jahresz&auml;hlung wird bis heute verwendet, indem die Jahre mit einer dazugeh&ouml;rigen
 * &Auml;ra (auch {@code Nengo} genannt) charakterisiert werden. Das erste Jahr zu einem Nengo hat immer
 * die Nummer 1. Vor Meiji wurden Nengos zu beliebigen Zeiten willk&uuml;rlich verk&uuml;ndet, oft auch
 * mehr als ein Nengo w&auml;hrend der Herrschaft eines Kaisers. Im Mittelalter w&auml;hrend der
 * Nanboku-ch&#333;-Zeit (1334-1392) gab es sogar zwei konkurrierende Nengo-Systeme, da zwei Kaiserh&ouml;fe
 * miteinander stritten (Nord-S&uuml;d-Schisma, was eine bestimmte Art von nicht-temporalem Zustand induziert).
 * In modernen Zeiten gibt es aber nur einen Nengo pro Herrschaftsperiod eines Kaisers oder Tenno. </p>
 *
 * <p>W&auml;hrend die moderne gregorianische Zeit nach Meiji 6 exakt die gleichen Monate von Januar bis Dezember
 * und dieselbe Tagesdatierung wie der westliche Kalender verwendet, ist die lunisolare Zeit g&auml;nzlich anders.
 * Alle Monate waren entweder 29 oder 30 Tage lang. Die Monatsl&auml;nge hing vom astronomischen Ereignis der
 * Sichtung des Neumonds ab, folgte aber oft wegen historischer Abweichungen und Fehlberechnungen nicht exakt
 * dem Neumond. Urspr&uuml;nglich hatten die Japaner den lunisolaren Kalender von China &uuml;bernommen. Deshalb
 * gab es auch Schaltmonate, die die Differenz zwischen den lunaren Zyklen und der Umdrehung der Erde um die
 * Sonne ausglichen. Solche Schaltmonate konnten zu beliebigen Zeiten innerhalb eines lunisolaren Jahres
 * eingef&uuml;gt werden, meistens alle zwei oder drei Jahre. Ein Schaltmonat hat die gleiche Nummer wie der
 * vorangehende Monat, wird aber zus&auml;tzlich mit einem speziellen Zeichen markiert (Time4J verwendet das
 * Sternchen f&uuml;r alle nicht-ostasiatischen Sprachen)). Siehe auch
 * <a href="https://en.wikipedia.org/wiki/Japanese_calendar">Wikipedia</a>. </p>
 *
 * <p>Hinweis: Der lunisolare Kalenderteil verwendet keine astronomischen Berechnungen, sondern die Originaldaten
 * von Paul Y. Tsuchihashi, um eine gr&ouml;&szlig;tm&ouml;gliche historische Detailtreue zu erzielen. </p>
 *
 * <p>Registriert sind folgende als Konstanten deklarierte Elemente: </p>
 *
 * <ul>
 *  <li>{@link #DAY_OF_WEEK}</li>
 *  <li>{@link #DAY_OF_MONTH}</li>
 *  <li>{@link #DAY_OF_YEAR}</li>
 *  <li>{@link #WEEKDAY_IN_MONTH}</li>
 *  <li>{@link #MONTH_OF_YEAR}</li>
 *  <li>{@link #MONTH_AS_ORDINAL}</li>
 *  <li>{@link #KOKI_YEAR}</li>
 *  <li>{@link #YEAR_OF_ERA}</li>
 *  <li>{@link #ERA}</li>
 * </ul>
 *
 * <p>Au&slig;erdem werden alle Elemente von {@code EpochDays} und {@link CommonElements} unterst&uuml;tzt. </p>
 *
 * <p>Anwendungsbeispiel: </p>
 *
 * <pre>
 *     ChronoFormatter&lt;JapaneseCalendar&gt; formatter =
 *       ChronoFormatter.ofPattern(
 *         &quot;MMMM/dd, G y&quot;,
 *         PatternType.NON_ISO_DATE,
 *         Locale.ENGLISH,
 *         JapaneseCalendar.axis());
 *     JapaneseCalendar jcal = JapaneseCalendar.ofGregorian(Nengo.HEISEI, 29, 4, 14);
 *     System.out.println(formatter.format(jcal)); // April/14, Heisei 29
 *
 *     ChronoFormatter&lt;JapaneseCalendar&gt; parser =
 *       ChronoFormatter.ofPattern(
 *         &quot;Gy年M月d日&quot;,
 *         PatternType.NON_ISO_DATE,
 *         Locale.JAPANESE,
 *         JapaneseCalendar.axis()
 *       ).with(Leniency.LAX); // verwende den Original-Nengo (beachte: Ansei-1 == Kaei-7)
 *     System.out.println(parser.parse(&quot;安政元年閏7月14日&quot;)); // Ansei-1(1854)-*7-14
 * </pre>
 *
 * <p>Das zweite Beispiel verwendet die bevorzugte Form f&uuml;r das erste Jahr eines Nengo, n&auml;mlich
 * &quot;元&quot; (gannen). Eine andere Spezialit&auml;t: Zweistellige Jahresangaben mit Kippjahr sind
 * de facto ausgeschaltet, sogar dann, wenn das Formatmuster &quot;yy&quot; verwendet wird (aber Anwender
 * sollten trotzdem das Muster &quot;y&quot; bevorzugen). </p>
 *
 * @author  Meno Hochschild
 * @see     Nengo
 * @see     EastAsianMonth
 * @see     NumberSystem#JAPANESE
 * @since   3.32/4.27
 */
@CalendarType("japanese")
public final class JapaneseCalendar
    extends Calendrical<JapaneseCalendar.Unit, JapaneseCalendar>
    implements LocalizedPatternSupport {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int YEAR_OF_NENGO_INDEX = 0;
    private static final int MONTH_AS_ORDINAL_INDEX = 1;
    private static final int DAY_OF_MONTH_INDEX = 2;
    private static final int DAY_OF_YEAR_INDEX = 3;
    private static final int RELATED_GREGORIAN_YEAR_INDEX = 4;
    private static final int KOKI_INDEX = 5;

    private static final int MRD = 1000000000;
    private static final long EPOCH_1873 = -36158L; // PlainDate.of(1873, 1, 1).getDaysSinceEpochUTC();

    private static final byte[] LEAP_INDICATORS;
    private static final int[] LUNISOLAR_MONTHS;
    private static final long[] START_OF_YEAR;

    static {
        String path = "data/tsuchihashi.data";
        URI uri = ResourceLoader.getInstance().locate("calendar", JapaneseCalendar.class, path);
        InputStream is = ResourceLoader.getInstance().load(uri, true);

        try {
            if (is == null) {
                is = ResourceLoader.getInstance().load(JapaneseCalendar.class, path, true);
            }

            DataInputStream in = new DataInputStream(is);
            long epochDays = -464176; // = JulianCalendar.of(HistoricEra.AD, 701, 2, 13).get(EpochDays.UTC);
            int arrlen = 1872 - 701 + 1;
            byte[] leapIndicators = new byte[arrlen];
            int[] lunisolarMonths = new int[arrlen];
            long[] startOfYear = new long[arrlen];

            for (int index = 0; index < arrlen; index++) {
                byte b = in.readByte();
                int s = in.readShort();
                leapIndicators[index] = b;
                lunisolarMonths[index] = s;
                startOfYear[index] = epochDays;

                int lengthOfYear = 0;
                for (int m = 1; m <= ((b == 0) ? 12 : 13); m++) {
                    lengthOfYear += (((s & 0x1) == 1) ? 30 : 29);
                    s >>>= 1;
                }
                epochDays += lengthOfYear;
            }
            LEAP_INDICATORS = leapIndicators;
            LUNISOLAR_MONTHS = lunisolarMonths;
            START_OF_YEAR = startOfYear;
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }
    }

    /**
     * <p>Represents the Japanese era (nengo). </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die japanische &Auml;ra (Nengo). </p>
     */
    @FormattableElement(format = "G")
    public static final TextElement<Nengo> ERA = Nengo.Element.SINGLETON;

    /**
     * <p>Represents the Japanese year associated with a nengo. </p>
     *
     * <p>Its maximum value corresponds to the value 1 of next nengo if present. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert das japanische Jahr, das mit einem Nengo verbunden ist. </p>
     *
     * <p>Sein Maximalwert entspricht dem Jahr 1 des n&auml;chsten Nengos, falls vorhanden. </p>
     */
    @FormattableElement(format = "y")
    public static final StdCalendarElement<Integer, JapaneseCalendar> YEAR_OF_ERA = new YearOfNengoElement();

    /**
     * <p>Counts the years since the supposed foundation date of Japan by the legendary emperor Jimmu. </p>
     *
     * <p>This imperial way of counting years was used from 1873 until the end of Second World War (called
     * K&#333;ki) and is 660 years in advance of gregorian years. However, the standard way of counting years
     * in this calendar is the {@link #YEAR_OF_ERA nengo-based year}. </p>
     */
    /*[deutsch]
     * <p>Z&auml;hlt die Jahre seit dem angenommenen Gr&uuml;ndungsdatum Japans durch den mythischen
     * Herrscher Jimmu. </p>
     *
     * <p>Diese imperiale Art der Jahresz&auml;hlung war im Gebrauch von 1873 bis zum Ende des zweiten
     * Weltkriegs (genannt K&#333;ki) und eilt dem gregorianischen Jahr um 660 Jahre voraus. Jedoch ist
     * die normale Jahresz&auml;hlung in diesem Kalender das {@link #YEAR_OF_ERA nengo-basierte Jahr}. </p>
     */
    public static final ChronoElement<Integer> KOKI_YEAR =
        new StdIntegerDateElement<JapaneseCalendar>(
            "KOKI_YEAR",
            JapaneseCalendar.class,
            701 + 660,
            MRD + 659,
            '\u0000',
            null,
            null);

    /**
     * <p>Represents the Japanese month. </p>
     *
     * <p>If used in combination with numeric formatting (via pattern length smaller than 3, M or MM) then
     * the format attribute {@link Attributes#NUMBER_SYSTEM} will be taken into account. The East Asian
     * languages Japanese, Chinese and Korean usually define an extra literal behind the number for the
     * month. In such a case, the strong recommendation is to use numerical formatting together with the
     * literal, for example the pattern &quot;M月&quot; in Japanese. Patterns using &quot;MMM&quot; or even
     * &quot;MMMM&quot; (without the literal &quot;月&quot;) should only be used if the context is in modern
     * times after 1872 and gregorian month names are wished. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den japanischen Monat. </p>
     *
     * <p>Wenn in Kombination mit numerischer Formatierung verwendet (Formatmusterl&auml;nge kleiner als 3,
     * M oder MM), dann wird das Formatattribut {@link Attributes#NUMBER_SYSTEM} ber&uuml;cksichtigt. Die
     * ostasiatischen Sprachen Japanisch, Chinesisch und Koreanisch definieren ein besonderes Literalzeichen
     * hinter der Monatsnummer. In einem solchen Fall lautet die dringende Empfehlung, zur numerischen
     * Formatierung in Verbindung mit dem Literal zu wechseln, zum Beispiel das Formatmuster &quot;M月&quot;
     * im Japanischen. Formatmuster, die &quot;MMM&quot; oder sogar &quot;MMMM&quot; enthalten (ohne das
     * Literal &quot;月&quot;), sollten nur dann verwendet werden, wenn der Kontext ein moderner ist (nach 1872)
     * und gregorianische Monatsnamen gew&uuml;nscht sind. </p>
     */
    @FormattableElement(format = "M", standalone = "L")
    public static final TextElement<EastAsianMonth> MONTH_OF_YEAR = new MonthPrimitiveElement();

    /**
     * <p>Represents the ordinal index of a Japanese month. </p>
     *
     * <p>The value is only identical to the regular month number since Meiji 6 (1873). If users want
     * to get the regular month number all times then it is only safe to use the expression
     * {@code japaneseDate.get(MONTH_OF_YEAR).getNumber()}, not this element. Following example
     * illustrates the difference for the lunisolar year Kaei-7 (1854) which contains a leap month: </p>
     *
     * <div style="margin-top:5px;">
     * <table border="1">
     * <caption>Month number/index during the lunisolar year Kaei 7</caption>
     * <tr>
     *  <td>{@link #MONTH_OF_YEAR}</td>
     *  <td>1</td>
     *  <td>2</td>
     *  <td>3</td>
     *  <td>4</td>
     *  <td>5</td>
     *  <td>6</td>
     *  <td>7</td>
     *  <td>*7</td>
     *  <td>8</td>
     *  <td>9</td>
     *  <td>10</td>
     *  <td>11</td>
     *  <td>12</td>
     * </tr>
     * <tr>
     *  <td>MONTH_AS_ORDINAL</td>
     *  <td>1</td>
     *  <td>2</td>
     *  <td>3</td>
     *  <td>4</td>
     *  <td>5</td>
     *  <td>6</td>
     *  <td>7</td>
     *  <td>8</td>
     *  <td>9</td>
     *  <td>10</td>
     *  <td>11</td>
     *  <td>12</td>
     *  <td>13</td>
     * </tr>
     * </table>
     * </div>
     *
     * <p>This element can be used in conjunction with
     * {@link net.time4j.format.expert.ChronoFormatter.Builder#addOrdinal(ChronoElement, Map) ordinal formatting}. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die Ordnungsnummer eines japanischen Monats. </p>
     *
     * <p>Der Wert ist nur dann identisch mit der regul&auml;ren Monatsnummer seit Meiji 6 (1873).
     * Wenn Anwender die regul&auml;re Monatsnummer f&uuml;r alle Zeiten brauchen, dann sollten sie
     * sicherheitshalber  den Ausdruck {@code japaneseDate.get(MONTH_OF_YEAR).getNumber()} statt
     * dieses Element verwenden. Folgendes Beispiel illustriert den Unterschied f&uuml;r das
     * lunisolare Jahr Kaei-7 (1854), das einen Schaltmonat enth&auml;lt: </p>
     *
     * <div style="margin-top:5px;">
     * <table border="1">
     * <caption>Monatsnummer/-index w&auml;hrend des lunisolaren Jahres Kaei 7</caption>
     * <tr>
     *  <td>{@link #MONTH_OF_YEAR}</td>
     *  <td>1</td>
     *  <td>2</td>
     *  <td>3</td>
     *  <td>4</td>
     *  <td>5</td>
     *  <td>6</td>
     *  <td>7</td>
     *  <td>*7</td>
     *  <td>8</td>
     *  <td>9</td>
     *  <td>10</td>
     *  <td>11</td>
     *  <td>12</td>
     * </tr>
     * <tr>
     *  <td>get(MONTH_AS_ORDINAL)</td>
     *  <td>1</td>
     *  <td>2</td>
     *  <td>3</td>
     *  <td>4</td>
     *  <td>5</td>
     *  <td>6</td>
     *  <td>7</td>
     *  <td>8</td>
     *  <td>9</td>
     *  <td>10</td>
     *  <td>11</td>
     *  <td>12</td>
     *  <td>13</td>
     * </tr>
     * </table>
     * </div>
     *
     * <p>Dieses Element kann in Verbindung mit einem
     * {@link net.time4j.format.expert.ChronoFormatter.Builder#addOrdinal(ChronoElement, Map) OrdinalFormat}
     * verwendet werden. </p>
     */
    public static final StdCalendarElement<Integer, JapaneseCalendar> MONTH_AS_ORDINAL =
        new StdIntegerDateElement<JapaneseCalendar>(
            "MONTH_AS_ORDINAL", JapaneseCalendar.class, 1, 12, '\u0000', null, null);

    /**
     * <p>Represents the Japanese day of month. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den japanischen Tag des Monats. </p>
     */
    @FormattableElement(format = "d")
    public static final StdCalendarElement<Integer, JapaneseCalendar> DAY_OF_MONTH =
        new StdIntegerDateElement<JapaneseCalendar>("DAY_OF_MONTH", JapaneseCalendar.class, 1, 31, 'd');

    /**
     * <p>Represents the Japanese day of year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den japanischen Tag des Jahres. </p>
     */
    @FormattableElement(format = "D")
    public static final StdCalendarElement<Integer, JapaneseCalendar> DAY_OF_YEAR =
        new StdIntegerDateElement<JapaneseCalendar>("DAY_OF_YEAR", JapaneseCalendar.class, 1, 365, 'D');

    /**
     * <p>Represents the Japanese day of week. </p>
     *
     * <p>If the day-of-week is set to a new value then Time4J handles the Japanese calendar week
     * as starting on Sunday. </p>
     *
     * @see     #getDefaultWeekmodel()
     * @see     CommonElements#localDayOfWeek(Chronology, Weekmodel)
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den japanischen Tag der Woche. </p>
     *
     * <p>Wenn der Tag der Woche auf einen neuen Wert gesetzt wird, behandelt Time4J die japanische
     * Kalenderwoche so, da&szlig; sie am Sonntag beginnt. </p>
     *
     * @see     #getDefaultWeekmodel()
     * @see     CommonElements#localDayOfWeek(Chronology, Weekmodel)
     */
    @FormattableElement(format = "E")
    public static final StdCalendarElement<Weekday, JapaneseCalendar> DAY_OF_WEEK =
        new StdWeekdayElement<JapaneseCalendar>(JapaneseCalendar.class, getDefaultWeekmodel());

    private static final WeekdayInMonthElement<JapaneseCalendar> WIM_ELEMENT =
        new WeekdayInMonthElement<JapaneseCalendar>(JapaneseCalendar.class, DAY_OF_MONTH, DAY_OF_WEEK);

    /**
     * <p>Element with the ordinal day-of-week within given calendar month. </p>
     */
    /*[deutsch]
     * <p>Element mit dem x-ten Wochentag im Monat. </p>
     */
    @FormattableElement(format = "F")
    public static final OrdinalWeekdayElement<JapaneseCalendar> WEEKDAY_IN_MONTH = WIM_ELEMENT;

    private static final Transformer CALSYS;
    private static final TimeAxis<JapaneseCalendar.Unit, JapaneseCalendar> ENGINE;

    static {
        CALSYS = new Transformer();

        TimeAxis.Builder<JapaneseCalendar.Unit, JapaneseCalendar> builder =
            TimeAxis.Builder.setUp(
                JapaneseCalendar.Unit.class,
                JapaneseCalendar.class,
                new Merger(),
                CALSYS)
            .appendElement(
                ERA,
                new NengoRule(),
                Unit.ERAS)
            .appendElement(
                YEAR_OF_ERA,
                new IntegerRule(YEAR_OF_NENGO_INDEX),
                Unit.YEARS)
            .appendElement(
                MONTH_OF_YEAR,
                MonthPrimitiveElement.SINGLETON,
                Unit.MONTHS)
            .appendElement(
                MONTH_AS_ORDINAL,
                new IntegerRule(MONTH_AS_ORDINAL_INDEX),
                Unit.MONTHS)
            .appendElement(
                DAY_OF_MONTH,
                new IntegerRule(DAY_OF_MONTH_INDEX),
                Unit.DAYS)
            .appendElement(
                DAY_OF_YEAR,
                new IntegerRule(DAY_OF_YEAR_INDEX),
                Unit.DAYS)
            .appendElement(
                DAY_OF_WEEK,
                new WeekdayRule(),
                Unit.DAYS)
            .appendElement(
                WIM_ELEMENT,
                WeekdayInMonthElement.getRule(WIM_ELEMENT))
            .appendElement(
                KOKI_YEAR,
                new IntegerRule(KOKI_INDEX),
                Unit.YEARS)
            .appendElement(
                CommonElements.RELATED_GREGORIAN_YEAR,
                new IntegerRule(RELATED_GREGORIAN_YEAR_INDEX))
            .appendUnit(
                Unit.ERAS,
                new JapaneseUnitRule(Unit.ERAS),
                Unit.ERAS.getLength())
            .appendUnit(
                Unit.YEARS,
                new JapaneseUnitRule(Unit.YEARS),
                Unit.YEARS.getLength())
            .appendUnit(
                Unit.MONTHS,
                new JapaneseUnitRule(Unit.MONTHS),
                Unit.MONTHS.getLength())
            .appendUnit(
                Unit.WEEKS,
                new JapaneseUnitRule(Unit.WEEKS),
                Unit.WEEKS.getLength(),
                Collections.singleton(Unit.DAYS))
            .appendUnit(
                Unit.DAYS,
                new JapaneseUnitRule(Unit.DAYS),
                Unit.DAYS.getLength(),
                Collections.singleton(Unit.WEEKS));
        ENGINE = builder.build();
    }

    private static final long serialVersionUID = -153630575450868922L;

    //~ Instanzvariablen --------------------------------------------------

    // temporal state
    private transient final int relgregyear;
    private transient final int dayOfYear;

    // state for resolving ambivalences in nengo representation
    private transient final Nengo nengo;

    // other state for performance reasons
    private transient final EastAsianMonth month;
    private transient final int dayOfMonth;

    //~ Konstruktoren -----------------------------------------------------

    private JapaneseCalendar(
        Nengo nengo,
        int relgregyear,
        int dayOfYear
    ) {
        this(nengo, relgregyear, dayOfYear, getMonth(relgregyear, dayOfYear), getDayOfMonth(relgregyear, dayOfYear));

    }

    private JapaneseCalendar(
        Nengo nengo,
        int relgregyear,
        int dayOfYear,
        EastAsianMonth month,
        int dayOfMonth
    ) {
        super();

        this.nengo = nengo;
        this.relgregyear = relgregyear;
        this.dayOfYear = dayOfYear;
        this.month = month;
        this.dayOfMonth = dayOfMonth;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a modern Japanese calendar for all dates since Meiji 6 (gregorian calendar rules). </p>
     *
     * <p>Leaving the gregorian condition aside, equivalent to
     * {@code JapaneseCalendar.of(nengo, yearOfNengo, EastAsianMonth.valueOf(month), dayOfMonth, Leniency.SMART)}. </p>
     *
     * @param   nengo           Japanese era (Meiji or later)
     * @param   yearOfNengo     year of nengo starting with number 1 (if Meiji then starting with 6)
     * @param   month           gregorian month (1-12)
     * @param   dayOfMonth      day of month {@code >= 1}
     * @return  new instance of {@code JapaneseCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @see     #of(Nengo, int, EastAsianMonth, int, Leniency)
     */
    /*[deutsch]
     * <p>Erzeugt einen modernen japanischen Kalender f&uuml;r jedes Datum seit Meiji 6
     * (gregorianische Kalenderregeln). </p>
     *
     * <p>Die gregorianische Bedingung au&szlig;er Acht lassend, &auml;quivalent zu
     * {@code JapaneseCalendar.of(nengo, yearOfNengo, EastAsianMonth.valueOf(month), dayOfMonth, Leniency.SMART)}. </p>
     *
     * @param   nengo           Japanese era (Meiji or later)
     * @param   yearOfNengo     year of nengo starting with number 1 (if Meiji then starting with 6)
     * @param   month           gregorian month (1-12)
     * @param   dayOfMonth      day of month {@code >= 1}
     * @return  new instance of {@code JapaneseCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @see     #of(Nengo, int, EastAsianMonth, int, Leniency)
     */
    public static JapaneseCalendar ofGregorian(
        Nengo nengo,
        int yearOfNengo,
        int month,
        int dayOfMonth
    ) {

        if (!nengo.isModern() || ((nengo == Nengo.MEIJI) && (yearOfNengo < 6))) {
            throw new IllegalArgumentException("Cannot create modern calendar with lunisolar calendar year.");
        }

        return JapaneseCalendar.of(nengo, yearOfNengo, EastAsianMonth.valueOf(month), dayOfMonth, Leniency.SMART);

    }

    /**
     * <p>Equivalent to {@code JapaneseCalendar.of(nengo, yearOfNengo, month, dayOfMonth, Leniency.SMART)}. </p>
     *
     * @param   nengo           Japanese era
     * @param   yearOfNengo     year of nengo starting with number 1
     * @param   month           japanese month (must not be a leap month for all years after or equal to Meiji 6)
     * @param   dayOfMonth      day of month {@code >= 1}
     * @return  new instance of {@code JapaneseCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @see     #of(Nengo, int, EastAsianMonth, int, Leniency)
     */
    /*[deutsch]
     * <p>&Auml;quivalent zu {@code JapaneseCalendar.of(nengo, yearOfNengo, month, dayOfMonth, Leniency.SMART)}. </p>
     *
     * @param   nengo           Japanese era
     * @param   yearOfNengo     year of nengo starting with number 1
     * @param   month           japanese month (must not be a leap month for all years after or equal to Meiji 6)
     * @param   dayOfMonth      day of month {@code >= 1}
     * @return  new instance of {@code JapaneseCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @see     #of(Nengo, int, EastAsianMonth, int, Leniency)
     */
    public static JapaneseCalendar of(
        Nengo nengo,
        int yearOfNengo,
        EastAsianMonth month,
        int dayOfMonth
    ) {

        return JapaneseCalendar.of(nengo, yearOfNengo, month, dayOfMonth, Leniency.SMART);

    }

    /**
     * <p>Creates a new instance of a Japanese calendar date. </p>
     *
     * <p>The leniency mainly handles the transition of nengos and is strongly recommended to be smart.
     * The strict and the lax mode do not try to change given nengo in case of transition. If such a nengo
     * is not really appropriate because the nengo was not yet introduced on that day then the strict mode
     * will throw an exception while the lax mode does not do anything. The smart mode however, will
     * adjust the nengo to a better matching nengo. Example for the transition from Sh&#333;wa to Heisei
     * which happened on 1989-01-08 (Heisei-1-01-08): </p>
     *
     * <div style="margin-top:5px;">
     * <table border="1">
     * <caption>Output in different leniency modes</caption>
     * <tr>
     *     <th>Input</th>
     *     <th>Strict</th>
     *     <th>Smart</th>
     *     <th>Lax</th>
     * </tr>
     * <tr>
     *  <td>Heisei-1-01-08</td><td>Heisei-1-01-08</td><td>Heisei-1-01-08</td><td>Heisei-1-01-08</td>
     * </tr>
     * <tr>
     *  <td>Heisei-1-01-07</td><td>{exception}</td><td>Sh&#333;wa-64-01-07</td><td>Heisei-1-01-07</td>
     * </tr>
     * <tr>
     *  <td>Sh&#333;wa-64-01-08</td><td>{exception}</td><td>Heisei-1-01-08</td><td>Sh&#333;wa-64-01-08</td>
     * </tr>
     * <tr>
     *  <td>Sh&#333;wa-64-01-07</td><td>Sh&#333;wa-64-01-07</td><td>Sh&#333;wa-64-01-07</td><td>Sh&#333;wa-64-01-07</td>
     * </tr>
     * </table>
     * </div>
     *
     * @param   nengo           Japanese era
     * @param   yearOfNengo     year of nengo starting with number 1
     * @param   month           japanese month (must not be a leap month for all years after or equal to Meiji 6)
     * @param   dayOfMonth      day of month {@code >= 1}
     * @param   leniency        helps to resolve ambivalent input if not strict
     * @return  new instance of {@code JapaneseCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    /*[deutsch]
     * <p>Erzeugt ein neues japanisches Kalenderdatum. </p>
     *
     * <p>Das Nachsichtigkeitsargument ist haupts&auml;chlich f&uuml;r den &Uuml;bergang von einem Nengo
     * zum anderen wichtig. Es wird dringend {@code Leniency.SMART} empfohlen. Der strikte und der laxe Modus
     * versuchen beide nicht, in einem solchen Fall den Nengo zu &auml;ndern. Wenn ein gegebener Nengo wegen
     * seines Einf&uuml;hrungszeitpunkts nicht geeignet sein sollte, wird der strikte Modus eine Ausnahme
     * werfen, w&auml;hrend der laxe Modus nichts tut. Der SMART-Modus aber wird den Nengo entsprechend
     * ab&auml;ndern. Beispiel f&uuml;r den &Uuml;bergang von Sh&#333;wa zu Heisei, der am 8. Januar 1989
     * (entsprechend HEISEI-1-01-08) stattfand: </p>
     *
     * <div style="margin-top:5px;">
     * <table border="1">
     * <caption>Ausgabe in verschiedenen Nachsichtigkeitsstufen</caption>
     * <tr>
     *     <th>Eingabe</th>
     *     <th>STRICT</th>
     *     <th>SMART</th>
     *     <th>LAX</th>
     * </tr>
     * <tr>
     *  <td>Heisei-1-01-08</td><td>Heisei-1-01-08</td><td>Heisei-1-01-08</td><td>Heisei-1-01-08</td>
     * </tr>
     * <tr>
     *  <td>Heisei-1-01-07</td><td>{exception}</td><td>Sh&#333;wa-64-01-07</td><td>Heisei-1-01-07</td>
     * </tr>
     * <tr>
     *  <td>Sh&#333;wa-64-01-08</td><td>{exception}</td><td>Heisei-1-01-08</td><td>Sh&#333;wa-64-01-08</td>
     * </tr>
     * <tr>
     *  <td>Sh&#333;wa-64-01-07</td><td>Sh&#333;wa-64-01-07</td><td>Sh&#333;wa-64-01-07</td><td>Sh&#333;wa-64-01-07</td>
     * </tr>
     * </table>
     * </div>
     *
     * @param   nengo           Japanese era
     * @param   yearOfNengo     year of nengo starting with number 1
     * @param   month           japanese month (must not be a leap month for all years after or equal to Meiji 6)
     * @param   dayOfMonth      day of month {@code >= 1}
     * @param   leniency        helps to resolve ambivalent input if not strict
     * @return  new instance of {@code JapaneseCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    public static JapaneseCalendar of(
        Nengo nengo,
        int yearOfNengo,
        EastAsianMonth month,
        int dayOfMonth,
        Leniency leniency
    ) {

        if (yearOfNengo < 1) {
            throw new IllegalArgumentException("Year of nengo smaller than 1: " + yearOfNengo);
        } else if (dayOfMonth < 1) {
            throw new IllegalArgumentException("Day of month smaller than 1: " + dayOfMonth);
        }

        int relgregyear = nengo.getFirstRelatedGregorianYear() + yearOfNengo - 1;
        Nengo next = nengo.findNext();

        if ((next != null) && (next.getFirstRelatedGregorianYear() < relgregyear)) {
            throw new IllegalArgumentException("Year of nengo out of range: " + nengo + "/" + yearOfNengo);
        }

        int dayOfYear = 0;

        if (relgregyear >= 1873) {
            if (month.isLeap()) {
                throw new IllegalArgumentException("Lunisolar leap month not valid in modern times: " + month);
            } else if (dayOfMonth > GregorianMath.getLengthOfMonth(relgregyear, month.getNumber())) {
                throw new IllegalArgumentException("Day of month out of range: " + dayOfMonth);
            }
            for (int m = 1, max = month.getNumber(); m < max; m++) {
                dayOfYear += GregorianMath.getLengthOfMonth(relgregyear, m);
            }
            dayOfYear += dayOfMonth;
        } else {
            int s = LUNISOLAR_MONTHS[relgregyear - 701];
            int index = getMonthIndex(relgregyear, month);
            if (month.isLeap() && (index != LEAP_INDICATORS[relgregyear - 701])) {
                throw new IllegalArgumentException("Invalid leap month: " + month);
            }
            for (int m = 1; m <= index; m++) {
                int len = (((s & 0x1) == 1) ? 30 : 29);
                if (m == index) {
                    if (dayOfMonth > len) {
                        throw new IllegalArgumentException("Day of month out of range: " + dayOfMonth);
                    } else {
                        dayOfYear += dayOfMonth;
                    }
                } else {
                    dayOfYear += len;
                    s >>>= 1;
                }
            }
        }

        if ((relgregyear == 1872) && (month.getNumber() == 12) && (dayOfMonth >= 3)) { // edge case
            if (leniency.isStrict()) {
                throw new IllegalArgumentException("Last month of lunisolar calendar had only 2 days.");
            }
            int day = dayOfMonth - 2;
            return new JapaneseCalendar(Nengo.MEIJI, 1873, day, EastAsianMonth.valueOf(1), day);
        }

        // check if date is before introduction of nengo
        long utcDays = JapaneseCalendar.transform(relgregyear, dayOfYear);
        CALSYS.check(utcDays);
        Nengo candidate = findBestNengo(nengo.matches(NORTHERN_COURT), relgregyear, utcDays);

        switch (leniency) {
            case STRICT:
                if (candidate != nengo) {
                    throw new IllegalArgumentException("Nengo should be: " + candidate + ", but was: " + nengo);
                }
                break;
            case SMART:
                nengo = candidate;
                break;
            default:
                // no-op
        }

        return new JapaneseCalendar(nengo, relgregyear, dayOfYear, month, dayOfMonth);

    }

    /**
     * <p>Obtains the current calendar date in system time. </p>
     *
     * <p>Convenient short-cut for: {@code SystemClock.inLocalView().now(JapaneseCalendar.axis())}. </p>
     *
     * @return      current calendar date in system time zone using the system clock
     * @see         SystemClock#inLocalView()
     * @see         net.time4j.ZonalClock#now(Chronology)
     */
    /*[deutsch]
     * <p>Ermittelt das aktuelle Kalenderdatum in der Systemzeit. </p>
     *
     * <p>Bequeme Abk&uuml;rzung f&uuml;r: {@code SystemClock.inLocalView().now(JapaneseCalendar.axis())}. </p>
     *
     * @return      current calendar date in system time zone using the system clock
     * @see         SystemClock#inLocalView()
     * @see         net.time4j.ZonalClock#now(Chronology)
     */
    public static JapaneseCalendar nowInSystemTime() {

        return SystemClock.inLocalView().now(JapaneseCalendar.axis());

    }

    /**
     * <p>Yields the Japanese era (nengo). </p>
     *
     * @return  Nengo
     * @see     #ERA
     */
    /*[deutsch]
     * <p>Liefert die japanische &Auml;ra (Nengo). </p>
     *
     * @return  Nengo
     * @see     #ERA
     */
    public Nengo getEra() {

        return this.nengo;

    }

    /**
     * <p>Yields the Japanese year which belongs to a nengo. </p>
     *
     * @return  int {@code >= 1}
     * @see     #YEAR_OF_ERA
     */
    /*[deutsch]
     * <p>Liefert das japanische Jahr, das mit einem Nengo verkn&uuml;pft ist. </p>
     *
     * @return  int {@code >= 1}
     * @see     #YEAR_OF_ERA
     */
    public int getYear() {

        return (this.relgregyear - this.nengo.getFirstRelatedGregorianYear() + 1);

    }

    /**
     * <p>Yields the Japanese month. </p>
     *
     * @return  japanese month
     * @see     #MONTH_OF_YEAR
     */
    /*[deutsch]
     * <p>Liefert den japanischen Monat. </p>
     *
     * @return  japanese month
     * @see     #MONTH_OF_YEAR
     */
    public EastAsianMonth getMonth() {

        return this.month;

    }

    /**
     * <p>Yields the Japanese day of month. </p>
     *
     * @return  int
     * @see     #DAY_OF_MONTH
     */
    /*[deutsch]
     * <p>Liefert den japanischen Tag des Monats. </p>
     *
     * @return  int
     * @see     #DAY_OF_MONTH
     */
    public int getDayOfMonth() {

        return this.dayOfMonth;

    }

    /**
     * <p>Determines the day of week. </p>
     *
     * <p>The Japanese calendar also uses a 7-day-week. </p>
     *
     * @return  Weekday
     * @see     #DAY_OF_WEEK
     */
    /*[deutsch]
     * <p>Ermittelt den Wochentag. </p>
     *
     * <p>Der japanische Kalendar verwendet ebenfalls eine 7-Tage-Woche. </p>
     *
     * @return  Weekday
     * @see     #DAY_OF_WEEK
     */
    public Weekday getDayOfWeek() {

        long utcDays = CALSYS.transform(this);
        return Weekday.valueOf(MathUtils.floorModulo(utcDays + 5, 7) + 1);

    }

    /**
     * <p>Yields the Japanese day of year. </p>
     *
     * @return  int {@code >= 1}
     * @see     #DAY_OF_YEAR
     */
    /*[deutsch]
     * <p>Liefert den japanischen Tag des Jahres. </p>
     *
     * @return  int {@code >= 1}
     * @see     #DAY_OF_YEAR
     */
    public int getDayOfYear() {

        return this.dayOfYear;

    }

    /**
     * <p>Yields the length of current Japanese month in days. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge des aktuellen japanischen Monats in Tagen. </p>
     *
     * @return  int
     */
    public int lengthOfMonth() {

        return getLengthOfMonth(this.relgregyear, this.month);

    }

    /**
     * <p>Yields the length of current Japanese year in days. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge des aktuellen japanischen Jahres in Tagen. </p>
     *
     * @return  int
     */
    public int lengthOfYear() {

        return getLengthOfYear(this.relgregyear);

    }

    /**
     * <p>Is the year of this date a leap year? </p>
     *
     * <p>Note: If this instance is before Meiji 6 (lunisolar period) then the lunisolar year is considered
     * as a leap year if and only if it contains a leap month. </p>
     *
     * @return  boolean
     */
    /*[deutsch]
     * <p>Liegt dieses Datum in einem Schaltjahr? </p>
     *
     * <p>Hinweis: Falls diese Instanz vor Meiji 6 liegt (lunisolarer Kalender), dann wird das lunisolare Jahr
     * genau dann als Schaltjahr angesehen, wenn es einen Schaltmonat enth&auml;lt. </p>
     *
     * @return  boolean
     */
    public boolean isLeapYear() {

        if (this.relgregyear >= 1873) {
            return GregorianMath.isLeapYear(this.relgregyear);
        } else {
            return (LEAP_INDICATORS[this.relgregyear - 701] > 0);
        }

    }

    /**
     * <p>Creates a new local timestamp with this date and given wall time. </p>
     *
     * <p>If the time {@link PlainTime#midnightAtEndOfDay() T24:00} is used
     * then the resulting timestamp will automatically be normalized such
     * that the timestamp will contain the following day instead. </p>
     *
     * @param   time wall time
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
    public GeneralTimestamp<JapaneseCalendar> at(PlainTime time) {

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
    public GeneralTimestamp<JapaneseCalendar> atTime(
        int hour,
        int minute
    ) {

        return this.at(PlainTime.of(hour, minute));

    }

    /**
     * <p>Compares first by the temporal position then by nengo position. </p>
     *
     * <p>The natural ordering is consistent with {@link #equals(Object)} because the non-temporal state
     * is also taken into consideration. If users wish a temporal ordering only then they might consider
     * the methods {@code isBefore()}, {@code isAfter()} or {@code isSimultaneous()}. </p>
     *
     * @param   other   the other Japanese calendar to be compared with
     * @return  a negative integer, zero, or a positive integer as this object
     *          is less than, equal to, or greater than the specified object
     * @see     #isBefore(CalendarDate)
     * @see     #isAfter(CalendarDate)
     * @see     #isSimultaneous(CalendarDate)
     */
    /*[deutsch]
     * <p>Vergleicht zuerst nach der zeitlichen Position, dann nach der Nengo-Position. </p>
     *
     * <p>Die nat&uuml;rliche Ordnung ist konsistent mit {@link #equals(Object)}, weil der
     * nicht-zeitliche Zustand auch ber&uuml;cksichtigt wird. Wenn eine rein zeitliche
     * Sortierung gew&uuml;nscht ist, dann sollten Anwender die Methoden {@code isBefore()},
     * {@code isAfter()} oder {@code isSimultaneous()} in Betracht ziehen. </p>
     *
     * @param   other   the other Japanese calendar to be compared with
     * @return  a negative integer, zero, or a positive integer as this object
     *          is less than, equal to, or greater than the specified object
     * @see     #isBefore(CalendarDate)
     * @see     #isAfter(CalendarDate)
     * @see     #isSimultaneous(CalendarDate)
     */
    @Override
    public int compareTo(JapaneseCalendar other) {

        int result = super.compareTo(other);

        if (result == 0) {
            result = this.nengo.getValue() - other.nengo.getValue();
            if (result == 0) {
                boolean n1 = this.nengo.matches(NORTHERN_COURT);
                boolean n2 = other.nengo.matches(NORTHERN_COURT);
                result = ((!n1 && n2) ? -1 : ((n1 && !n2) ? 1 : 0));
            }
        }

        return result;

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof JapaneseCalendar) {
            JapaneseCalendar that = (JapaneseCalendar) obj;
            return (
                (this.relgregyear == that.relgregyear)
                && (this.dayOfYear == that.dayOfYear)
                && (this.nengo == that.nengo)
                && (this.dayOfMonth == that.dayOfMonth)
                && this.month.equals(that.month)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return (17 * this.relgregyear + 31 * this.dayOfYear);

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(32);
        sb.append(this.nengo.getDisplayName(Locale.ROOT));
        sb.append('-');
        sb.append(this.getYear());
        sb.append('(');
        sb.append(this.relgregyear);
        sb.append(")-");
        if (this.month.isLeap()) {
            sb.append('*');
        }
        int m = this.month.getNumber();
        if ((this.relgregyear >= 1873) && (m < 10)) {
            sb.append('0');
        }
        sb.append(m);
        sb.append('-');
        int dom = this.getDayOfMonth();
        if (dom < 10) {
            sb.append('0');
        }
        sb.append(dom);
        return sb.toString();

    }

    /**
     * <p>Obtains the standard week model of this calendar. </p>
     *
     * <p>The Japanese calendar usually starts on Sunday. </p>
     *
     * @return  Weekmodel
     */
    /*[deutsch]
     * <p>Ermittelt das Standardwochenmodell dieses Kalenders. </p>
     *
     * <p>Der japanische Kalender startet normalerweise am Sonntag. </p>
     *
     * @return  Weekmodel
     */
    public static Weekmodel getDefaultWeekmodel() {

        return Weekmodel.of(Locale.JAPAN);

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
    public static TimeAxis<Unit, JapaneseCalendar> axis() {

        return ENGINE;

    }

    @Override
    protected TimeAxis<Unit, JapaneseCalendar> getChronology() {

        return ENGINE;

    }

    @Override
    protected JapaneseCalendar getContext() {

        return this;

    }

    @Override
    protected int compareByTime(CalendarDate date) {

        JapaneseCalendar that;

        if (date instanceof JapaneseCalendar) {
            that = JapaneseCalendar.class.cast(date);
        } else {
            that = CALSYS.transform(date.getDaysSinceEpochUTC());
        }

        if (this.relgregyear < that.relgregyear) {
            return -1;
        } else if (this.relgregyear > that.relgregyear) {
            return 1;
        } else if (this.dayOfYear < that.dayOfYear) {
            return -1;
        } else if (this.dayOfYear > that.dayOfYear) {
            return 1;
        } else {
            return 0;
        }

    }

    private JapaneseCalendar tryWithNorthernCourt() {

        if ((this.relgregyear >= 1332) && (this.relgregyear < 1394)) {
            Nengo nengo = Nengo.ofRelatedGregorianYear(this.relgregyear, NORTHERN_COURT);

            while (nengo.getStartAsDaysSinceEpochUTC() > this.getDaysSinceEpochUTC()) {
                nengo = nengo.findPrevious();
            }

            return new JapaneseCalendar(nengo, this.relgregyear, this.dayOfYear, this.month, this.dayOfMonth);
        }

        return this;

    }

    private static Nengo findBestNengo(
        boolean northern,
        int relgregyear,
        long utcDays
    ) {

        Nengo nengo;

        if (northern && (relgregyear >= 1332) && (relgregyear < 1394)) {
            nengo = Nengo.ofRelatedGregorianYear(relgregyear, NORTHERN_COURT);
        } else {
            nengo = Nengo.ofRelatedGregorianYear(relgregyear, OFFICIAL);
        }

        while (nengo.getStartAsDaysSinceEpochUTC() > utcDays) {
            Nengo previous = nengo.findPrevious();
            if (previous != null) {
                nengo = previous;
            } else {
                break;
            }
        }

        return nengo;

    }

    private static EastAsianMonth getMonth(
        int relgregyear,
        int dayOfYear
    ) {

        int doy = 0;

        if (dayOfYear >= 1) {
            if (relgregyear >= 1873) {
                for (int m = 1; m <= 12; m++) {
                    doy += GregorianMath.getLengthOfMonth(relgregyear, m);
                    if (doy >= dayOfYear) {
                        return EastAsianMonth.valueOf(m);
                    }
                }
            } else {
                int b = LEAP_INDICATORS[relgregyear - 701];
                int s = LUNISOLAR_MONTHS[relgregyear - 701];
                for (int m = 1, max = ((b == 0) ? 12 : 13); m <= max; m++) {
                    doy += (((s & 0x1) == 1) ? 30 : 29);
                    s >>>= 1;
                    if (doy >= dayOfYear) {
                        int num = m;
                        if ((b > 0) && (b <= m)) {
                            num--;
                        }
                        EastAsianMonth month = EastAsianMonth.valueOf(num);
                        if (m == b) {
                            month = month.withLeap();
                        }
                        return month;
                    }
                }
            }
        }

        throw new IllegalArgumentException("Day of year out of range: " + dayOfYear);

    }

    private static int getLengthOfMonth(
        int relgregyear,
        EastAsianMonth month
    ) {

        if (relgregyear >= 1873) {
            return GregorianMath.getLengthOfMonth(relgregyear, month.getNumber());
        } else if ((relgregyear == 1872) && (month.getNumber() == 12)) {
            return 2; // edge case
        } else {
            int index = getMonthIndex(relgregyear, month);
            int s = LUNISOLAR_MONTHS[relgregyear - 701];
            for (int m = 1; m <= index; m++) {
                if (m == index) {
                    return (((s & 0x1) == 1) ? 30 : 29);
                }
                s >>>= 1;
            }
            throw new AssertionError();
        }

    }

    private static int getLengthOfYear(int relgregyear) {

        if (relgregyear >= 1873) {
            return GregorianMath.isLeapYear(relgregyear) ? 366 : 365;
        } else if (relgregyear == 1872) {
            return (int) (EPOCH_1873 - START_OF_YEAR[1872 - 701]); // edge case
        } else {
            int index = relgregyear - 701;
            int s = LUNISOLAR_MONTHS[index];
            int lengthOfYear = 0;
            for (int m = 1, max = ((LEAP_INDICATORS[index] == 0) ? 12 : 13); m <= max; m++) {
                lengthOfYear += (((s & 0x1) == 1) ? 30 : 29);
                s >>>= 1;
            }
            return lengthOfYear;
        }

    }

    private static int getMonthIndex(
        int relgregyear,
        EastAsianMonth month
    ) {

        int num = month.getNumber();

        if (relgregyear >= 1873) {
            return num;
        }

        byte b = LEAP_INDICATORS[relgregyear - 701];

        if (month.isLeap() || ((b > 0) && (num >= b))) {
            return num + 1;
        }

        return num;

    }

    private static int getDayOfMonth(
        int relgregyear,
        int dayOfYear
    ) {

        int dom = dayOfYear;
        EastAsianMonth month = getMonth(relgregyear, dayOfYear);

        if (relgregyear >= 1873) {
            for (int m = 1, num = month.getNumber(); m < num; m++) {
                dom -= GregorianMath.getLengthOfMonth(relgregyear, m);
            }
        } else {
            int index = getMonthIndex(relgregyear, month);
            int s = LUNISOLAR_MONTHS[relgregyear - 701];
            for (int m = 1; m < index; m++) {
                dom -= (((s & 0x1) == 1) ? 30 : 29);
                s >>>= 1;
            }
        }

        return dom;

    }

    private static int getArrayIndex(long epochDays) {

        int low = 0;
        int high = START_OF_YEAR.length - 1;

        while (low <= high) {
            int middle = ((low + high) >> 1);
            if (START_OF_YEAR[middle] <= epochDays) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }

        return low - 1;

    }

    // also used in serialization
    static long transform(
        int relgregyear,
        int dayOfYear
    ) {

        if (relgregyear >= 1873) {
            return PlainDate.of(relgregyear, dayOfYear).getDaysSinceEpochUTC();
        } else {
            return START_OF_YEAR[relgregyear - 701] + dayOfYear - 1;
        }

    }

    private static JapaneseCalendar create(
        JapaneseCalendar context,
        int relgregyear,
        EastAsianMonth month,
        int dayOfMonth
    ) {

        Nengo nengo = Nengo.ofRelatedGregorianYear(relgregyear);

        JapaneseCalendar jcal =
            JapaneseCalendar.of(
                nengo,
                relgregyear - nengo.getFirstRelatedGregorianYear() + 1,
                month,
                dayOfMonth,
                Leniency.SMART);

        if (context.nengo.matches(NORTHERN_COURT)) {
            jcal = jcal.tryWithNorthernCourt();
        }

        return jcal;

    }

    /**
     * @return      replacement object in serialization graph
     * @serialData  Uses <a href="../../../serialized-form.html#net.time4j.calendar.SPX">
     *              a dedicated serialization form</a> as proxy. The first byte contains
     *              the type-ID {@code 9}. Then the related gregorian year and the day-of-year
     *              are written as int-primitives. Note that the serialization round-trip
     *              might fail for calendar objects created in lax mode due to normalization.
     */
    private Object writeReplace() {

        return new SPX(this, SPX.JAPANESE);

    }

    /**
     * @param       in  object input stream
     * @throws      InvalidObjectException (always)
     * @serialData  Blocks because a serialization proxy is required.
     */
    private void readObject(ObjectInputStream in)
        throws IOException {

        throw new InvalidObjectException("Serialization proxy required.");

    }

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Defines some calendar units for the Japanese calendar. </p>
     */
    /*[deutsch]
     * <p>Definiert einige kalendarische Zeiteinheiten f&uuml;r den japanischen Kalender. </p>
     */
    public static enum Unit
        implements ChronoUnit {

        //~ Statische Felder/Initialisierungen ----------------------------

        /**
         * <p>The fuzzy unit of a nengo respective era. </p>
         *
         * <p>Nengos have very different and unpredictable lengths. Time4J uses {@code Integer.MAX_VALUE}
         * as length to indicate this behaviour. If any nengo is one of the northern court (1332-1393)
         * then it will first be converted to its southern court equivalent before applying any era arithmetics. </p>
         *
         * <p>If necessary in case of addition, Time4J will adjust the smaller elements year-of-nengo, month-of-year
         * and day-of-month such that they still fit within the context of the newly calculated nengo. However,
         * the main purpose of this unit is to count the nengo distance between two Japanese dates. </p>
         */
        /*[deutsch]
         * <p>Die unscharfe Zeiteinheit eines Nengos beziehungsweise einer &Auml;ra. </p>
         *
         * <p>Nengos haben sehr verschiedene und nicht vorhersagbare L&auml;ngen. Time4J verwendet
         * {@code Integer.MAX_VALUE} als L&auml;nge, um dieses Verhalten anzuzeigen. Wenn irgendein Nengo
         * einer des Nordhofs ist (1332-1393), dann wird er zuerst zu seinem S&uuml;dhof-Gegenst&uum;ck
         * umgewandelt, bevor irgendeine &Auml;ra-Arithmetik angewandt wird. </p>
         *
         * <p>Bei Bedarf wird Time4J im Fall der Addition die kleineren Elemente wie year-of-nengo, month-of-year
         * und day-of-month so anpassen, da&szlig; sie noch in den Kontext des neu berechneten Nengos passen.
         * Jedoch ist die Hauptanwendung dieser Zeiteinheit die Z&auml;hlung der Nengos zwischen zwei japanischen
         * Datumsangaben. </p>
         */
        ERAS(Integer.MAX_VALUE),

        /**
         * <p>The unit of a gregorian or lunisolar year. </p>
         *
         * <p>Note that lunisolar years sometimes consist of 13 instead of 12 months. </p>
         */
        /*[deutsch]
         * <p>Die Zeiteinheit eines gregorianischen oder lunisolaren Jahres. </p>
         *
         * <p>Zu beachten: Lunisolare Jahre bestehen manchmal aus 13 statt aus 12 Monaten. </p>
         */
        YEARS(31556952.0), // 365.2425 * 86400.0

        /**
         * <p>The unit of a gregorian or lunisolar month. </p>
         *
         * <p>Note: The month arithmetic is limited to amounts smaller than {@code 25000}
         * otherwise an {@code ArithmeticException} will be thrown. </p>
         */
        /*[deutsch]
         * <p>Die Zeiteinheit eines gregorianischen oder lunisolaren Monats. </p>
         *
         * <p>Hinweis: Die Monatsarithmetik ist auf Betr&auml;ge kleiner als {@code 25000} beschr&auml;nkt,
         * sonst wird eine {@code ArithmeticException} geworfen. </p>
         */
        MONTHS(30 * 86400.0),

        /**
         * <p>The Japanese calendar uses a 7-day-week. </p>
         */
        /*[deutsch]
         * <p>Der japanische Kalender verwendet eine 7-Tage-Woche. </p>
         */
        WEEKS(7 * 86400.0),

        /**
         * <p>Standard unit for days. </p>
         *
         * @see     net.time4j.engine.CalendarDays
         */
        /*[deutsch]
         * <p>Standardzeiteinheit f&uuml;r Tage. </p>
         *
         * @see     net.time4j.engine.CalendarDays
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
         * <p>Calculates the difference between given Coptic dates in this unit. </p>
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
            JapaneseCalendar start,
            JapaneseCalendar end
        ) {

            return start.until(end, this);

        }

    }

    private static class Transformer
        implements CalendarSystem<JapaneseCalendar> {

        //~ Methoden ------------------------------------------------------

        @Override
        public JapaneseCalendar transform(long utcDays) {

            if (utcDays >= EPOCH_1873) {
                PlainDate date = PlainDate.of(utcDays, EpochDays.UTC);
                int relgregyear = date.getYear();
                Nengo nengo = findBestNengo(false, relgregyear, utcDays);

                return new JapaneseCalendar(
                    nengo,
                    relgregyear,
                    date.getDayOfYear(),
                    EastAsianMonth.valueOf(date.getMonth()),
                    date.getDayOfMonth()
                );
            }

            int index = getArrayIndex(utcDays);

            if (index < 0) {
                throw new IllegalArgumentException("Out of bounds: " + utcDays);
            }

            int relgregyear = index + 701;
            int dayOfYear = (int) (utcDays - START_OF_YEAR[index] + 1);
            Nengo nengo = findBestNengo(false, relgregyear, utcDays);

            return new JapaneseCalendar(
                nengo,
                relgregyear,
                dayOfYear
            );

        }

        @Override
        public long transform(JapaneseCalendar date) {

            return JapaneseCalendar.transform(date.relgregyear, date.dayOfYear);

        }

        @Override
        public long getMinimumSinceUTC() {

            return START_OF_YEAR[0];

        }

        @Override
        public long getMaximumSinceUTC() {

            return 365241779741L; // PlainDate.axis().getCalendarSystem().getMaximumSinceUTC();

        }

        @Override
        @SuppressWarnings("unchecked")
        public List<CalendarEra> getEras() {

            Object eras = Nengo.list();
            return (List<CalendarEra>) eras;

        }

        void check(long utcDays) {

            if ((utcDays < this.getMinimumSinceUTC()) || (utcDays > this.getMaximumSinceUTC())) {
                throw new IllegalArgumentException("Japanese calendar out of supported range.");
            }

        }

    }

    private static class JapaneseUnitRule
        implements UnitRule<JapaneseCalendar> {

        //~ Instanzvariablen ----------------------------------------------

        private final Unit unit;

        //~ Konstruktoren -------------------------------------------------

        JapaneseUnitRule(Unit unit) {
            super();

            this.unit = unit;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public JapaneseCalendar addTo(JapaneseCalendar date, long amount) {

            switch (this.unit) {
                case ERAS:
                    try {
                        return erasAdded(date, amount);
                    } catch (IndexOutOfBoundsException ex) {
                        throw new IllegalArgumentException(ex);
                    }
                case YEARS:
                    try {
                        int r = MathUtils.safeAdd(date.relgregyear, MathUtils.safeCast(amount));
                        EastAsianMonth month = date.month;
                        int num = month.getNumber();
                        if (r >= 1873) {
                            if (month.isLeap()) {
                                month = EastAsianMonth.valueOf(num); // no leap
                            }
                        } else if (month.isLeap() && (LEAP_INDICATORS[r - 701] != num + 1)) {
                            month = EastAsianMonth.valueOf(num); // no leap
                        }
                        int dom = Math.min(date.dayOfMonth, getLengthOfMonth(r, month));
                        return JapaneseCalendar.create(date, r, month, dom);
                    } catch (IndexOutOfBoundsException ex) {
                        throw new IllegalArgumentException(ex);
                    }
                case MONTHS:
                    try {
                        checkAmountOfMonths(amount);
                        int y = date.relgregyear;
                        int m = getMonthIndex(y, date.month);
                        int delta = ((amount > 0) ? 1 : -1);
                        while (amount != 0) {
                            m += delta;
                            if (y >= 1873) {
                                if (m == 0) {
                                    y--;
                                    m = ((y >= 1873) || (LEAP_INDICATORS[y - 701] == 0) ? 12 : 13);
                                } else if (m == 13) {
                                    y++;
                                    m = 1;
                                }
                            } else {
                                if (m == 0) {
                                    y--;
                                    m = ((LEAP_INDICATORS[y - 701] == 0) ? 12 : 13);
                                } else if (m > ((LEAP_INDICATORS[y - 701] == 0) ? 12 : 13)) {
                                    y++;
                                    m = 1;
                                }
                            }
                            amount -= delta;
                        }
                        if (y >= 1873) {
                            Nengo nengo = Nengo.ofRelatedGregorianYear(y, MODERN);
                            EastAsianMonth month = EastAsianMonth.valueOf(m);
                            int dom = Math.min(date.dayOfMonth, getLengthOfMonth(y, month));
                            return JapaneseCalendar.of(
                                nengo,
                                y - nengo.getFirstRelatedGregorianYear() + 1,
                                month,
                                dom,
                                Leniency.SMART);
                        }
                        int num = m;
                        boolean leap = false;
                        byte b = LEAP_INDICATORS[y - 701];
                        if (b > 0) {
                            if (b <= m) {
                                num--;
                            }
                            if (b == m) {
                                leap = true;
                            }
                        }
                        EastAsianMonth month = EastAsianMonth.valueOf(num);
                        if (leap) {
                            month = month.withLeap();
                        }
                        int dom = Math.min(date.dayOfMonth, getLengthOfMonth(y, month));
                        return JapaneseCalendar.create(date, y, month, dom);
                    } catch (IndexOutOfBoundsException ex) {
                        throw new IllegalArgumentException(ex);
                    }
                case WEEKS:
                    amount = MathUtils.safeMultiply(amount, 7);
                    // fall-through
                case DAYS:
                    long utcDays = MathUtils.safeAdd(CALSYS.transform(date), amount);
                    JapaneseCalendar jcal = CALSYS.transform(utcDays);
                    if (date.nengo.matches(NORTHERN_COURT)) {
                        jcal = jcal.tryWithNorthernCourt();
                    }
                    return jcal;
                default:
                    throw new UnsupportedOperationException(this.unit.name());
            }

        }

        @Override
        public long between(JapaneseCalendar start, JapaneseCalendar end) {

            switch (this.unit) {
                case ERAS:
                    return erasBetween(start, end);
                case YEARS:
                    int years = end.relgregyear - start.relgregyear;
                    if (years == 0) {
                        return 0L;
                    } else {
                        int mEnd = getMonthIndex(end.relgregyear, end.month);
                        int mStart = getMonthIndex(start.relgregyear, start.month);
                        if (years > 0) {
                            if ((mStart > mEnd) || (mStart == mEnd && start.dayOfMonth > end.dayOfMonth)) {
                                years--;
                            }
                        } else if (years < 0) {
                            if ((mStart < mEnd) || (mStart == mEnd && start.dayOfMonth < end.dayOfMonth)) {
                                years++;
                            }
                        }
                        return years;
                    }
                case MONTHS:
                    int yEnd = end.relgregyear;
                    int mEnd = getMonthIndex(end.relgregyear, end.month);
                    int y = start.relgregyear;
                    int m = getMonthIndex(start.relgregyear, start.month);
                    int delta = end.compareByTime(start); // possible values: -1, 0, 1
                    int amount = 0;
                    while ((y != yEnd) || (m != mEnd)) {
                        m += delta;
                        if (y >= 1873) {
                            if (m == 0) {
                                y--;
                                m = ((y >= 1873) || (LEAP_INDICATORS[y - 701] == 0) ? 12 : 13);
                            } else if (m == 13) {
                                y++;
                                m = 1;
                            }
                        } else {
                            if (m == 0) {
                                y--;
                                m = ((LEAP_INDICATORS[y - 701] == 0) ? 12 : 13);
                            } else if (m > ((LEAP_INDICATORS[y - 701] == 0) ? 12 : 13)) {
                                y++;
                                m = 1;
                            }
                        }
                        amount += delta;
                        checkAmountOfMonths(amount);
                    }
                    if ((amount > 0) && (start.dayOfMonth > end.dayOfMonth)) {
                        amount--;
                    } else if ((amount < 0) && (start.dayOfMonth < end.dayOfMonth)) {
                        amount++;
                    }
                    return amount;
                case WEEKS:
                    return Unit.DAYS.between(start, end) / 7;
                case DAYS:
                    return CALSYS.transform(end) - CALSYS.transform(start);
                default:
                    throw new UnsupportedOperationException(this.unit.name());
            }

        }

        private static JapaneseCalendar erasAdded(
            JapaneseCalendar date,
            long amount
        ) {

            Nengo nengo = date.nengo;
            int yon = date.getYear();
            EastAsianMonth month = date.month;
            int dom = date.dayOfMonth;

            if (nengo.matches(NORTHERN_COURT)) { // conversion to southern court
                nengo = Nengo.ofRelatedGregorianYear(date.relgregyear);
                yon = date.relgregyear - nengo.getFirstRelatedGregorianYear() + 1;
            }

            Nengo nengoNew =
                Nengo.ofIndexOfficial(MathUtils.safeAdd(nengo.getIndexOfficial(), MathUtils.safeCast(amount)));
            Nengo next = nengoNew.findNext();

            if (next != null) {
                int ymax = next.getFirstRelatedGregorianYear() - nengoNew.getFirstRelatedGregorianYear() + 1;
                if (yon > ymax) {
                    yon = ymax;
                }
            }

            int relgregyear = yon - 1 + nengoNew.getFirstRelatedGregorianYear();

            if (relgregyear >= 1873) {
                if (month.isLeap()) {
                    month = EastAsianMonth.valueOf(month.getNumber()); // no leap
                }
            } else if (month.isLeap() && (LEAP_INDICATORS[relgregyear - 701] == 0)) {
                month = EastAsianMonth.valueOf(month.getNumber()); // no leap
            }

            int dmax = getLengthOfMonth(relgregyear, month);

            if (dom > dmax) {
                dom = dmax;
            }

            return JapaneseCalendar.of(nengoNew, yon, month, dom);

        }

        private static int erasBetween(
            JapaneseCalendar start,
            JapaneseCalendar end
        ) {

            Nengo s = start.nengo;
            int ys = start.getYear();
            int ms = getMonthIndex(start.relgregyear, start.month);
            int ds = start.dayOfMonth;

            if (s.matches(NORTHERN_COURT)) {
                s = Nengo.ofRelatedGregorianYear(start.relgregyear);
                ys = start.relgregyear - s.getFirstRelatedGregorianYear() + 1;
            }

            Nengo e = end.nengo;
            int ye = end.getYear();
            int me = getMonthIndex(end.relgregyear, end.month);
            int de = end.dayOfMonth;

            if (e.matches(NORTHERN_COURT)) {
                e = Nengo.ofRelatedGregorianYear(end.relgregyear);
                ye = end.relgregyear - e.getFirstRelatedGregorianYear() + 1;
            }

            int eras = e.getIndexOfficial() - s.getIndexOfficial();

            if (eras > 0) {
                if ((ys > ye) || ((ys == ye) && ((ms > me) || ((ms == me) && (ds > de))))) {
                    eras--;
                }
            } else if (eras < 0) {
                if ((ys < ye) || ((ys == ye) && ((ms < me) || ((ms == me) && (ds < de))))) {
                    eras++;
                }
            }

            return eras;

        }

        private static void checkAmountOfMonths(long amount) {

            if (Math.abs(amount) >= 25000) {
                throw new ArithmeticException("Month arithmetic limited to delta smaller than 25000.");
            }

        }

    }

    private static class NengoRule
        implements ElementRule<JapaneseCalendar, Nengo> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Nengo getValue(JapaneseCalendar context) {

            return context.nengo;

        }

        @Override
        public Nengo getMinimum(JapaneseCalendar context) {

            return ERA.getDefaultMinimum();

        }

        @Override
        public Nengo getMaximum(JapaneseCalendar context) {

            return ERA.getDefaultMaximum();

        }

        @Override
        public boolean isValid(
            JapaneseCalendar context,
            Nengo value
        ) {

            return (value != null);

        }

        @Override
        public JapaneseCalendar withValue(
            JapaneseCalendar context,
            Nengo value,
            boolean lenient
        ) {

            int yon = context.getYear();
            EastAsianMonth month = context.month;
            int dom = context.dayOfMonth;
            Nengo next = value.findNext();

            if (next != null) {
                int ymax = next.getFirstRelatedGregorianYear() - value.getFirstRelatedGregorianYear() + 1;
                if (yon > ymax) {
                    yon = ymax;
                }
            }

            int relgregyear = yon - 1 + value.getFirstRelatedGregorianYear();

            if (relgregyear >= 1873) {
                if (month.isLeap()) {
                    month = EastAsianMonth.valueOf(month.getNumber()); // no leap
                }
            } else if (month.isLeap() && (LEAP_INDICATORS[relgregyear - 701] == 0)) {
                month = EastAsianMonth.valueOf(month.getNumber()); // no leap
            }

            int dmax = getLengthOfMonth(relgregyear, month);

            if (dom > dmax) {
                dom = dmax;
            }

            return JapaneseCalendar.of(value, yon, month, dom, lenient ? Leniency.LAX : Leniency.SMART);

        }

        @Override
        public ChronoElement<?> getChildAtFloor(JapaneseCalendar context) {

            return YEAR_OF_ERA;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(JapaneseCalendar context) {

            return YEAR_OF_ERA;

        }

    }

    private static class IntegerRule
        implements IntElementRule<JapaneseCalendar> {

        //~ Instanzvariablen ----------------------------------------------

        private final int index;

        //~ Konstruktoren -------------------------------------------------

        IntegerRule(int index) {
            super();

            this.index = index;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public int getInt(JapaneseCalendar context) {

            switch (this.index) {
                case YEAR_OF_NENGO_INDEX:
                    return context.getYear();
                case DAY_OF_MONTH_INDEX:
                    return context.dayOfMonth;
                case DAY_OF_YEAR_INDEX:
                    return context.dayOfYear;
                case MONTH_AS_ORDINAL_INDEX:
                    return getMonthIndex(context.relgregyear, context.month);
                case RELATED_GREGORIAN_YEAR_INDEX:
                    return context.relgregyear;
                case KOKI_INDEX:
                    return context.relgregyear + 660;
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        int getMin() {

            switch (this.index) {
                case YEAR_OF_NENGO_INDEX:
                case DAY_OF_MONTH_INDEX:
                case DAY_OF_YEAR_INDEX:
                case MONTH_AS_ORDINAL_INDEX:
                    return 1;
                case RELATED_GREGORIAN_YEAR_INDEX:
                    return 701;
                case KOKI_INDEX:
                    return 701 + 660;
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        int getMax(JapaneseCalendar context) {

            switch (this.index) {
                case YEAR_OF_NENGO_INDEX:
                    Nengo current = context.nengo;
                    Nengo next = current.findNext();
                    if (next != null) {
                        // we allow for example Shōwa 64 (equivalent to Heisei 1)
                        return next.getFirstRelatedGregorianYear() - current.getFirstRelatedGregorianYear() + 1;
                    } else {
                        return MRD - Nengo.Element.SINGLETON.getDefaultMaximum().getFirstRelatedGregorianYear();
                    }
                case DAY_OF_MONTH_INDEX:
                    return getLengthOfMonth(context.relgregyear, context.month);
                case DAY_OF_YEAR_INDEX:
                    return getLengthOfYear(context.relgregyear);
                case MONTH_AS_ORDINAL_INDEX:
                    return (context.relgregyear >= 1873 || LEAP_INDICATORS[context.relgregyear - 701] == 0) ? 12 : 13;
                case RELATED_GREGORIAN_YEAR_INDEX:
                    return MRD - 1;
                case KOKI_INDEX:
                    return MRD + 660 - 1;
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public boolean isValid(
            JapaneseCalendar context,
            int value
        ) {

            switch (this.index) {
                case YEAR_OF_NENGO_INDEX:
                case DAY_OF_MONTH_INDEX:
                case DAY_OF_YEAR_INDEX:
                case MONTH_AS_ORDINAL_INDEX:
                case KOKI_INDEX:
                    return ((value >= 1) && (value <= this.getMax(context)));
                case RELATED_GREGORIAN_YEAR_INDEX:
                    return (context.relgregyear == value);
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public JapaneseCalendar withValue(
            JapaneseCalendar context,
            int value,
            boolean lenient
        ) {

            if (this.isValid(context, value)) {
                switch (this.index) {
                    case YEAR_OF_NENGO_INDEX:
                        return yearsAdded(context, context.nengo.getFirstRelatedGregorianYear() + value - 1);
                    case DAY_OF_MONTH_INDEX:
                        return JapaneseCalendar.create(context, context.relgregyear, context.month, value);
                    case DAY_OF_YEAR_INDEX:
                        return new JapaneseCalendar(context.nengo, context.relgregyear, value);
                    case MONTH_AS_ORDINAL_INDEX:
                        EastAsianMonth m;
                        if (context.relgregyear >= 1873) {
                            m = EastAsianMonth.valueOf(value);
                        } else {
                            byte b = LEAP_INDICATORS[context.relgregyear - 701];
                            if (value == b) {
                                m = EastAsianMonth.valueOf(value - 1).withLeap();
                            } else if (value > b) {
                                m = EastAsianMonth.valueOf(value - 1);
                            } else {
                                m = EastAsianMonth.valueOf(value);
                            }
                        }
                        return context.with(MONTH_OF_YEAR, m);
                    case RELATED_GREGORIAN_YEAR_INDEX:
                        return context;
                    case KOKI_INDEX:
                        return yearsAdded(context, value - 660);
                    default:
                        throw new UnsupportedOperationException("Unknown element index: " + this.index);
                }
            } else if (this.index == RELATED_GREGORIAN_YEAR_INDEX) {
                throw new IllegalArgumentException("The related gregorian year is read-only.");
            } else {
                throw new IllegalArgumentException("Out of range: " + value);
            }

        }

        @Override
        public Integer getValue(JapaneseCalendar context) {

            return Integer.valueOf(this.getInt(context));

        }

        @Override
        public Integer getMinimum(JapaneseCalendar context) {

            return Integer.valueOf(this.getMin());

        }

        @Override
        public Integer getMaximum(JapaneseCalendar context) {

            return Integer.valueOf(this.getMax(context));

        }

        @Override
        public boolean isValid(JapaneseCalendar context, Integer value) {

            return ((value != null) && this.isValid(context, value.intValue()));

        }

        @Override
        public JapaneseCalendar withValue(
            JapaneseCalendar context,
            Integer value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Not nullable.");
            }

            return this.withValue(context, value.intValue(), lenient);

        }

        @Override
        public ChronoElement<?> getChildAtFloor(JapaneseCalendar context) {

            switch (this.index) {
                case YEAR_OF_NENGO_INDEX:
                    return MONTH_OF_YEAR;
                case DAY_OF_MONTH_INDEX:
                case DAY_OF_YEAR_INDEX:
                case RELATED_GREGORIAN_YEAR_INDEX:
                case KOKI_INDEX:
                    return null;
                case MONTH_AS_ORDINAL_INDEX:
                    return DAY_OF_MONTH;
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(JapaneseCalendar context) {

            return this.getChildAtFloor(context);

        }

        private static JapaneseCalendar yearsAdded(
            JapaneseCalendar context,
            int relgregyear
        ) {

            EastAsianMonth month = context.month;
            int num = month.getNumber();

            if (relgregyear >= 1873) {
                if (month.isLeap()) {
                    month = EastAsianMonth.valueOf(month.getNumber()); // no leap
                }
            } else if (month.isLeap() && (LEAP_INDICATORS[relgregyear - 701] != num + 1)) {
                month = EastAsianMonth.valueOf(month.getNumber()); // no leap
            }

            int dom = Math.min(context.dayOfMonth, getLengthOfMonth(relgregyear, month));
            return JapaneseCalendar.create(context, relgregyear, month, dom);

        }

    }

    private static class YearOfNengoElement
        extends StdIntegerDateElement<JapaneseCalendar>
        implements DualFormatElement {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final long serialVersionUID = -8502388572788955989L;

        //~ Konstruktoren -------------------------------------------------

        private YearOfNengoElement() {
            super("YEAR_OF_ERA",
                JapaneseCalendar.class,
                1,
                MRD - Nengo.Element.SINGLETON.getDefaultMaximum().getFirstRelatedGregorianYear(),
                'y',
                null,
                null);
        }

        //~ Methoden ------------------------------------------------------

        @Override
        public void print(
            ChronoDisplay context,
            Appendable buffer,
            AttributeQuery attributes
        ) throws IOException, ChronoException {

            NumberSystem numsys = attributes.get(Attributes.NUMBER_SYSTEM, NumberSystem.ARABIC);
            char zeroChar = (
                attributes.contains(Attributes.ZERO_DIGIT)
                    ? attributes.get(Attributes.ZERO_DIGIT).charValue()
                    : (numsys.isDecimal() ? numsys.getDigits().charAt(0) : '0'));
            this.print(context, buffer, attributes, numsys, zeroChar, 1, 9);

        }

        @Override
        public void print(
            ChronoDisplay context,
            Appendable buffer,
            AttributeQuery attributes,
            NumberSystem numsys,
            char zeroChar,
            int minDigits,
            int maxDigits
        ) throws IOException, ChronoException {

            int num = context.getInt(this);

            if (
                (num == 1)
                && (numsys == NumberSystem.ARABIC)
                && attributes.get(Attributes.LANGUAGE, Locale.ROOT).getLanguage().equals("ja")
            ) {
                buffer.append('元'); // gannen
            } else {
                String s = numsys.toNumeral(num);
                if (numsys.isDecimal()) {
                    int len = s.length();
                    for (int i = 0, n = minDigits - len; i < n; i++) {
                        buffer.append(zeroChar);
                    }
                }
                buffer.append(s);
            }

        }

        @Override
        public Integer parse(
            CharSequence text,
            ParsePosition status,
            AttributeQuery attributes
        ) {

            NumberSystem numsys = attributes.get(Attributes.NUMBER_SYSTEM, NumberSystem.ARABIC);
            int start = status.getIndex();
            int pos = start;

            if (
                (numsys == NumberSystem.ARABIC)
                && (text.charAt(pos) == '元')
                && attributes.get(Attributes.LANGUAGE, Locale.ROOT).getLanguage().equals("ja")
            ) {
                status.setIndex(pos + 1);
                return Integer.valueOf(1);
            }

            char zeroChar = (
                attributes.contains(Attributes.ZERO_DIGIT)
                    ? attributes.get(Attributes.ZERO_DIGIT).charValue()
                    : (numsys.isDecimal() ? numsys.getDigits().charAt(0) : '0'));
            Leniency leniency =
                (numsys.isDecimal() ? Leniency.SMART : attributes.get(Attributes.LENIENCY, Leniency.SMART));
            int value = 0;

            if (numsys.isDecimal()) {
                for (int i = pos, n = Math.min(pos + 9, text.length()); i < n; i++) {
                    int digit = text.charAt(i) - zeroChar;
                    if ((digit >= 0) && (digit <= 9)) {
                        value = value * 10 + digit;
                        pos++;
                    } else {
                        break;
                    }
                }
            } else {
                int len = 0;

                for (int i = pos, n = text.length(); i < n; i++) {
                    if (numsys.contains(text.charAt(i))) {
                        len++;
                    } else {
                        break;
                    }
                }

                if (len > 0) {
                    value = numsys.toInteger(text.subSequence(pos, pos + len).toString(), leniency);
                    pos += len;
                }
            }

            if (pos == start) {
                status.setErrorIndex(start);
                return null;
            } else {
                status.setIndex(pos);
                return Integer.valueOf(value);
            }

        }

        @Override
        public Integer parse(
            CharSequence text,
            ParsePosition status,
            AttributeQuery attributes,
            ChronoEntity<?> parsedResult
        ) {

            return this.parse(text, status, attributes);

        }

    }



    private static class MonthPrimitiveElement
        implements TextElement<EastAsianMonth>, ElementRule<JapaneseCalendar, EastAsianMonth>, Serializable {

        //~ Statische Felder/Initialisierungen ----------------------------

        static final MonthPrimitiveElement SINGLETON = new MonthPrimitiveElement();

        private static final long serialVersionUID = -2978966174642315851L;

        //~ Konstruktoren -------------------------------------------------

        private MonthPrimitiveElement() {
            super();

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public String name() {

            return "MONTH_OF_YEAR";

        }

        @Override
        public Class<EastAsianMonth> getType() {

            return EastAsianMonth.class;

        }

        @Override
        public char getSymbol() {

            return 'M';

        }

        @Override
        public int compare(
            ChronoDisplay o1,
            ChronoDisplay o2
        ) {

            EastAsianMonth m1 = o1.get(this);
            EastAsianMonth m2 = o2.get(this);
            return m1.compareTo(m2);

        }

        @Override
        public EastAsianMonth getDefaultMinimum() {

            return EastAsianMonth.valueOf(1);

        }

        @Override
        public EastAsianMonth getDefaultMaximum() {

            return EastAsianMonth.valueOf(12);

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
        public boolean isLenient() {

            return false;

        }

        @Override
        public String getDisplayName(Locale language) {

            String key = "L_month";
            String lname = CalendarText.getIsoInstance(language).getTextForms().get(key);
            return ((lname == null) ? this.name() : lname);

        }

        @Override
        public EastAsianMonth getValue(JapaneseCalendar context) {

            return context.month;

        }

        @Override
        public EastAsianMonth getMinimum(JapaneseCalendar context) {

            return EastAsianMonth.valueOf(1);

        }

        @Override
        public EastAsianMonth getMaximum(JapaneseCalendar context) {

            EastAsianMonth max = EastAsianMonth.valueOf(12);

            if ((context.relgregyear < 1873) && (LEAP_INDICATORS[context.relgregyear - 701] == 13)) {
                return max.withLeap();
            } else {
                return max;
            }

        }

        @Override
        public boolean isValid(
            JapaneseCalendar context,
            EastAsianMonth value
        ) {

            if (value == null) {
                return false;
            } else if (context.relgregyear >= 1873) {
                return !value.isLeap();
            } else if (value.isLeap()) {
                return (LEAP_INDICATORS[context.relgregyear - 701] == value.getNumber() + 1);
            } else {
                return true;
            }

        }

        @Override
        public JapaneseCalendar withValue(
            JapaneseCalendar context,
            EastAsianMonth value,
            boolean lenient
        ) {

            if (this.isValid(context, value)) {
                int dom = Math.min(context.dayOfMonth, getLengthOfMonth(context.relgregyear, value));
                return JapaneseCalendar.create(context, context.relgregyear, value, dom);
            } else {
                throw new IllegalArgumentException("Invalid month: " + value);
            }

        }

        @Override
        public ChronoElement<?> getChildAtFloor(JapaneseCalendar context) {

            return DAY_OF_MONTH;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(JapaneseCalendar context) {

            return DAY_OF_MONTH;

        }

        /**
         * @serialData  Preserves the singleton semantic
         * @return      singleton instance
         */
        private Object readResolve() throws ObjectStreamException {

            return SINGLETON;

        }

        @Override
        public void print(
            ChronoDisplay context,
            Appendable buffer,
            AttributeQuery attributes
        ) throws IOException, ChronoException {

            Locale loc = attributes.get(Attributes.LANGUAGE, Locale.ROOT);
            int count = attributes.get(DualFormatElement.COUNT_OF_PATTERN_SYMBOLS, Integer.valueOf(0)).intValue();
            EastAsianMonth eam = context.get(MONTH_OF_YEAR);
            int num = eam.getNumber();

            if (context.get(CommonElements.RELATED_GREGORIAN_YEAR) >= 1873) {
                if (count == 0) {
                    TextWidth tw = attributes.get(Attributes.TEXT_WIDTH, TextWidth.WIDE);
                    OutputContext oc = attributes.get(Attributes.OUTPUT_CONTEXT, OutputContext.FORMAT);
                    buffer.append(CalendarText.getIsoInstance(loc).getStdMonths(tw, oc).print(Month.valueOf(num)));
                } else {
                    NumberSystem numsys = attributes.get(Attributes.NUMBER_SYSTEM, NumberSystem.ARABIC);
                    String s = numsys.toNumeral(num);
                    if (numsys.isDecimal()) {
                        char zeroDigit = numsys.getDigits().charAt(0);
                        int padding = count - s.length();
                        while (padding > 0) {
                            buffer.append(zeroDigit);
                            padding--;
                        }
                    }
                    buffer.append(s);
                }
            } else {
                if (eam.isLeap()) {
                    char defaultLI =
                        CalendarText.getInstance("generic", loc).getTextForms().get("leap-month").charAt(0);
                    buffer.append(attributes.get(EastAsianMonth.LEAP_MONTH_INDICATOR, defaultLI));
                }
                NumberSystem numsys = attributes.get(Attributes.NUMBER_SYSTEM, NumberSystem.ARABIC);
                buffer.append(numsys.toNumeral(num)); // no padding in lunisolar case
            }

        }

        @Override
        public EastAsianMonth parse(
            CharSequence text,
            ParsePosition status,
            AttributeQuery attributes
        ) {

            Locale loc = attributes.get(Attributes.LANGUAGE, Locale.ROOT);
            int count = attributes.get(DualFormatElement.COUNT_OF_PATTERN_SYMBOLS, Integer.valueOf(0)).intValue();
            int start = status.getIndex();

            if (count == 0) {
                TextWidth tw = attributes.get(Attributes.TEXT_WIDTH, TextWidth.WIDE);
                OutputContext oc = attributes.get(Attributes.OUTPUT_CONTEXT, OutputContext.FORMAT);
                TextAccessor accessor = CalendarText.getIsoInstance(loc).getStdMonths(tw, oc);

                for (int num = 1; num <= 12; num++) {
                    Month gregorianMonth = accessor.parse(text, status, Month.class, attributes);
                    if (gregorianMonth != null) {
                        return EastAsianMonth.valueOf(gregorianMonth.getValue());
                    } else { // reset
                        status.setIndex(start);
                        status.setErrorIndex(-1);
                    }
                }
            }

            char defaultLI =
                CalendarText.getInstance("generic", loc).getTextForms().get("leap-month").charAt(0);
            char li = attributes.get(EastAsianMonth.LEAP_MONTH_INDICATOR, defaultLI).charValue();
            int pos = start;
            boolean leap = false;

            if (text.charAt(pos) == li) {
                leap = true;
                pos++;
            }

            NumberSystem numsys = attributes.get(Attributes.NUMBER_SYSTEM, NumberSystem.ARABIC);
            int total = 0;
            boolean decimal = numsys.isDecimal();
            int minPos = pos + 1;
            int maxPos = (decimal ? pos + 2 : pos + 9); // safe

            if (decimal) {
                char zeroDigit = numsys.getDigits().charAt(0);
                while (pos < maxPos) {
                    int digit = text.charAt(pos) - zeroDigit;

                    if ((digit >= 0) && (digit <= 9)) {
                        total = total * 10 + digit;
                        pos++;
                    } else {
                        break;
                    }
                }
            } else {
                while (pos < maxPos) {
                    if (numsys.contains(text.charAt(pos))) {
                        pos++;
                    } else {
                        break;
                    }
                }

                try {
                    if (pos >= minPos) {
                        Leniency leniency = attributes.get(Attributes.LENIENCY, Leniency.SMART);
                        total = numsys.toInteger(text.subSequence(minPos - 1, pos).toString(), leniency);
                    }
                } catch (NumberFormatException nfe) {
                    status.setErrorIndex(start);
                    return null;
                }
            }

            if ((pos < minPos) || (total < 1) || (total > 12)) {
                status.setErrorIndex(start);
                return null;
            }

            EastAsianMonth month = EastAsianMonth.valueOf(total);

            if (leap) {
                month = month.withLeap();
            }

            status.setIndex(pos);
            return month;

        }

    }

    private static class WeekdayRule
        implements ElementRule<JapaneseCalendar, Weekday> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Weekday getValue(JapaneseCalendar context) {

            return context.getDayOfWeek();

        }

        @Override
        public Weekday getMinimum(JapaneseCalendar context) {

            return Weekday.SUNDAY;

        }

        @Override
        public Weekday getMaximum(JapaneseCalendar context) {

            return Weekday.SATURDAY;

        }

        @Override
        public boolean isValid(
            JapaneseCalendar context,
            Weekday value
        ) {

            return (value != null);

        }

        @Override
        public JapaneseCalendar withValue(
            JapaneseCalendar context,
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
        public ChronoElement<?> getChildAtFloor(JapaneseCalendar context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(JapaneseCalendar context) {

            return null;

        }

    }

    private static class Merger
        implements ChronoMerger<JapaneseCalendar> {

        //~ Methoden ------------------------------------------------------

        @Override
        public String getFormatPattern(
            DisplayStyle style,
            Locale locale
        ) {

            return GenericDatePatterns.get("japanese", style, locale);

        }

        @Override
        public StartOfDay getDefaultStartOfDay() {

            return StartOfDay.MIDNIGHT;

        }

        @Override
        public int getDefaultPivotYear() {

            return 100; // two-digit-years are effectively switched off

        }

        @Override
        public JapaneseCalendar createFrom(
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
        @Deprecated
        public JapaneseCalendar createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean preparsing
        ) {

            boolean lenient = attributes.get(Attributes.LENIENCY, Leniency.SMART).isLax();
            return this.createFrom(entity, attributes, lenient, preparsing);

        }

        @Override
        public JapaneseCalendar createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean lenient,
            boolean preparsing
        ) {

            Nengo nengo = entity.get(ERA);

            if (nengo == null) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Missing Japanese nengo/era.");
                return null;
            }

            int year = entity.getInt(YEAR_OF_ERA);

            if (year == Integer.MIN_VALUE) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Missing Japanese year.");
                return null;
            }

            int relgregyear = nengo.getFirstRelatedGregorianYear() + year - 1;
            EastAsianMonth month = null;

            if (entity.contains(MONTH_OF_YEAR)) {
                month = entity.get(MONTH_OF_YEAR);
            } else if (entity.contains(MONTH_AS_ORDINAL)) {
                int mIndex = entity.getInt(MONTH_AS_ORDINAL);

                if (relgregyear >= 1873) {
                    month = EastAsianMonth.valueOf(mIndex);
                } else {
                    byte b = LEAP_INDICATORS[relgregyear - 701];
                    if (mIndex == b) {
                        month = EastAsianMonth.valueOf(mIndex - 1).withLeap();
                    } else if (mIndex > b) {
                        month = EastAsianMonth.valueOf(mIndex - 1);
                    } else {
                        month = EastAsianMonth.valueOf(mIndex);
                    }
                }
            }

            if (month != null) {
                int dom = entity.getInt(DAY_OF_MONTH);
                if (dom == Integer.MIN_VALUE) {
                    entity.with(ValidationElement.ERROR_MESSAGE, "Missing Japanese day of month.");
                    return null;
                } else {
                    Leniency leniency =
                        (lenient ? Leniency.LAX : attributes.get(Attributes.LENIENCY, Leniency.SMART));
                    return JapaneseCalendar.of(nengo, year, month, dom, leniency);
                }
            } else {
                int doy = entity.getInt(DAY_OF_YEAR);
                if (doy != Integer.MIN_VALUE) {
                    if (doy <= getLengthOfYear(relgregyear)) {
                        try {
                            month = getMonth(relgregyear, doy);
                            int dom = getDayOfMonth(relgregyear, doy);
                            Leniency leniency =
                                (lenient ? Leniency.LAX : attributes.get(Attributes.LENIENCY, Leniency.SMART));
                            return JapaneseCalendar.of(nengo, year, month, dom, leniency);
                        } catch (IllegalArgumentException ex) {
                            entity.with(ValidationElement.ERROR_MESSAGE, "Invalid Japanese date.");
                            return null;
                        }
                    }
                }
            }

            return null;

        }

        @Override
        public ChronoDisplay preformat(JapaneseCalendar context, AttributeQuery attributes) {

            return context;

        }

        @Override
        public Chronology<?> preparser() {

            return null;

        }

    }

}