/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FrenchRepublicanCalendar.java) is part of project Time4J.
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

package net.time4j.calendar.frenchrev;

import net.time4j.GeneralTimestamp;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.SystemClock;
import net.time4j.Weekday;
import net.time4j.Weekmodel;
import net.time4j.base.MathUtils;
import net.time4j.base.TimeSource;
import net.time4j.calendar.StdCalendarElement;
import net.time4j.calendar.service.DualYearOfEraElement;
import net.time4j.calendar.service.StdEnumDateElement;
import net.time4j.calendar.service.StdIntegerDateElement;
import net.time4j.calendar.service.StdWeekdayElement;
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
import net.time4j.format.NumberSystem;
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
 * <p>Represents the calendar used in French Revolution between 1792 and 1805. </p>
 *
 * <p>Its design is radically different from standard year-month-day-calendars like the gregorian one. The year started
 * at autumnal equinox at the longitude of Paris observatory and was divided into 12 months with the constant length
 * of 30 days. In order to complete the revolution of the earth around the sun, 5 or 6 complementary days were
 * added after the last month. These complementary days are not part of any month and were sometimes also called
 * &quot;Sansculottides&quot;. The sixth complementary day only happened in leap years depending on which day the
 * autumnal equinox of next year started, usually every fourth year. </p>
 *
 * <p>Napol&eacute;on abolished the calendar with begin of gregorian year 1806. Interestingly, the Paris Commune
 * of 1871 reintroduced it for 18 days until its militarian destruction. </p>
 *
 * <p>This calendar had originally no seven-day-week. Instead the months were divided each into three decades
 * of 10 days, and the 10th day was considered as day of rest - like Sunday in the gregorian calendar. However,
 * Time4J makes both the decade design and the standard seven-day-week available, last one mainly for comparison
 * purposes and especially after April 1802 when the calendar abandoned the original decade system. More calendar
 * details see also <a href="https://en.wikipedia.org/wiki/French_Republican_Calendar">Wikipedia</a>. </p>
 *
 * <p>Following elements which are declared as constants are registered by
 * this class: </p>
 *
 * <ul>
 *  <li>{@link #DAY_OF_DECADE}</li>
 *  <li>{@link #DAY_OF_WEEK}</li>
 *  <li>{@link #DAY_OF_MONTH}</li>
 *  <li>{@link #DAY_OF_YEAR}</li>
 *  <li>{@link #DECADE_OF_MONTH}</li>
 *  <li>{@link #MONTH_OF_YEAR}</li>
 *  <li>{@link #SANSCULOTTIDES}</li>
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
 *  <td>YEAR_OF_ERA</td><td>Y</td><td>number</td>
 * </tr>
 * <tr>
 *  <td>MONTH_OF_YEAR</td><td>M</td><td>text</td>
 * </tr>
 * <tr>
 *  <td>SANSCULOTTIDES</td><td>S</td><td>text</td>
 * </tr>
 * <tr>
 *  <td>DAY_OF_MONTH</td><td>D</td><td>number</td>
 * </tr>
 * <tr>
 *  <td>DAY_OF_DECADE</td><td>C</td><td>text</td>
 * </tr>
 * <tr>
 *  <td>DAY_OF_WEEK</td><td>E</td><td>text</td>
 * </tr>
 * </table>
 * </div>
 *
 * <p>Note: The standalone form of some enums like the republican month can be printed in a capitalized way
 * if the formatter is first constructed on builder level by using a sectional attribute for the output context.
 * Alternatively, users can simply modify the formatter by calling
 * {@code f.with(Attributes.OUTPUT_CONTEXT, OutputContext.STANDALONE)}. </p>
 *
 * <p>Furthermore: The abbreviated form of the republican month is usually numeric with two arabic digits. For
 * more control about the numeric representation, the builder offers extra fine-tuned methods. </p>
 *
 * <p>It is strongly recommended to use the or-operator &quot;|&quot; in format patterns because not every date of
 * this calendar has a month. Example: </p>
 *
 * <pre>
 *    ChronoFormatter&lt;FrenchRepublicanCalendar&gt; f =
 *      ChronoFormatter.ofPattern(
 *        &quot;[D. MMMM|SSSS]&#39;, an &#39;Y&quot;,
 *        PatternType.DYNAMIC,
 *        Locale.FRENCH,
 *        FrenchRepublicanCalendar.axis());
 *
 *    FrenchRepublicanCalendar cal = PlainDate.of(2018, 9, 23).transform(FrenchRepublicanCalendar.axis());
 *    System.out.println(f.format(cal)); // output =&gt; 1. vendémiaire, an CCXXVII
 *
 *    cal = cal.minus(CalendarDays.ONE);
 *    System.out.println(f.format(cal)); // output =&gt; jour de la révolution, an CCXXVI
 * </pre>
 *
 * @author  Meno Hochschild
 * @see     FrenchRepublicanEra
 * @see     FrenchRepublicanMonth
 * @see     Sansculottides
 * @see     DayOfDecade
 * @since   3.33/4.28
 * @doctags.concurrency {immutable}
 */
/*[deutsch]
 * <p>Repr&auml;sentiert den in der franz&ouml;sischen Revolution von 1792 bis 1805 verwendeten Kalender. </p>
 *
 * <p>Sein Entwurf ist unterscheidet sich radikal vom Muster von Standard-Jahr-Monat-Tag-Kalendern wie dem
 * gregorianischen. Das Jahr begann immer zum Herbstanfang, der am L&auml;ngengrad des Pariser Observatoriums
 * beobachtet wurde und wurde in 12 Monate mit der konstanten L&auml;nge von jeweils 30 Tagen eingeteilt. Um
 * den Umlauf der Erde um die Sonne komplett zu machen, wurden 5 oder 6 Erg&auml;nzungstage nach dem letzten
 * Monat hinzugef&uuml;gt. Diese Erg&auml;nzungstage waren nicht Bestandteil eines Monats und wurden manchmal
 * auch &quot;Sansculottides&quot; genannt. Der sechste Erg&auml;nzungstag trat nur in Schaltjahren auf,
 * wenn der Herbstanfang des n&auml;chsten Jahres dies erforderte, gew&ouml;hnlich alle vier Jahre. </p>
 *
 * <p>Napol&eacute;on schaffte den Kalender mit Beginn des Jahres 1806 ab. Interessanterweise f&uuml;hrte
 * die Pariser Kommune von 1871 den Kalender f&uuml;r 18 Tage bis zu ihrer milit&auml;rischen Niederschlagung
 * wieder ein. </p>
 *
 * <p>Dieser Kalender hatte urspr&uuml;nglich keine 7-Tage-Woche. Stattdessen wurden die Monate in jeweils
 * drei Dekaden von 10 Tagen L&auml;nge unterteilt. Und der zehnte Tag wurde als Ruhetag vergleichbar dem
 * Sonntag angegangen. Allerdings stellt Time4J sowohl das Dekadenmuster als auch die gew&ouml;hnliche
 * 7-Tage-Woche zur Verf&uuml;gung, haupts&auml;chlich f&uuml;r Vergleichszwecke und speziell nach April 1802,
 * als der Kalender das Dekadensystem aufgab und zur 7-Tage-Woche zur&uuml;ckkehrte. Mehr Kalenderdetails
 * siehe auch <a href="https://en.wikipedia.org/wiki/French_Republican_Calendar">Wikipedia</a>. </p>
 *
 * <p>Folgende als Konstanten deklarierte Elemente werden von dieser Klasse registriert: </p>
 *
 * <ul>
 *  <li>{@link #DAY_OF_DECADE}</li>
 *  <li>{@link #DAY_OF_WEEK}</li>
 *  <li>{@link #DAY_OF_MONTH}</li>
 *  <li>{@link #DAY_OF_YEAR}</li>
 *  <li>{@link #DECADE_OF_MONTH}</li>
 *  <li>{@link #MONTH_OF_YEAR}</li>
 *  <li>{@link #SANSCULOTTIDES}</li>
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
 *  <th>element</th><th>symbol</th><th>type</th>
 * </tr>
 * <tr>
 *  <td>ERA</td><td>G</td><td>text</td>
 * </tr>
 * <tr>
 *  <td>YEAR_OF_ERA</td><td>Y</td><td>number</td>
 * </tr>
 * <tr>
 *  <td>MONTH_OF_YEAR</td><td>M</td><td>text</td>
 * </tr>
 * <tr>
 *  <td>SANSCULOTTIDES</td><td>S</td><td>text</td>
 * </tr>
 * <tr>
 *  <td>DAY_OF_MONTH</td><td>D</td><td>number</td>
 * </tr>
 * <tr>
 *  <td>DAY_OF_DECADE</td><td>C</td><td>text</td>
 * </tr>
 * <tr>
 *  <td>DAY_OF_WEEK</td><td>E</td><td>text</td>
 * </tr>
 * </table>
 * </div>
 *
 * <p>Hinweis: Die <i>standalone</i>-Form einiger Enums wie dem republikanischen Monat ist in einer
 * kapitalisierten Weise erh&auml;ltlich, wenn der Formatierer zuerst mittels seines <i>builder</i>
 * konstruiert und ein sektionales Attribut f&uuml;r den Ausgabekontext verwendet wird. Alternativ
 * kann man am Formatierer {@code f.with(Attributes.OUTPUT_CONTEXT, OutputContext.STANDALONE)} aufrufen. </p>
 *
 * <p>Au&szlig;erdem ist die abgek&uuml;rzte Schreibweise des republikanischen Monats gew&ouml;hnlich
 * numerisch mit zwei arabischen Ziffern. Um mehr Kontrolle &uuml;ber die numerische Repr&auml;sentation
 * zu bekommen, bietet der <i>builder</i> weitere feingranulare Methoden an. </p>
 *
 * <p>Weil nicht jedes Datum dieses Kalenders einen Monat hat, ist es angeraten, mit dem Oder-Operator
 * &quot;|&quot; in der Formatierung zu arbeiten. Beispiel: </p>
 *
 * <pre>
 *    ChronoFormatter&lt;FrenchRepublicanCalendar&gt; f =
 *      ChronoFormatter.ofPattern(
 *        &quot;[D. MMMM|SSSS]&#39;, an &#39;Y&quot;,
 *        PatternType.DYNAMIC,
 *        Locale.FRENCH,
 *        FrenchRepublicanCalendar.axis());
 *
 *    FrenchRepublicanCalendar cal = PlainDate.of(2018, 9, 23).transform(FrenchRepublicanCalendar.axis());
 *    System.out.println(f.format(cal)); // Ausgabe =&gt; 1. vendémiaire, an CCXXVII
 *
 *    cal = cal.minus(CalendarDays.ONE);
 *    System.out.println(f.format(cal)); // Ausgabe =&gt; jour de la révolution, an CCXXVI
 * </pre>
 *
 * @author  Meno Hochschild
 * @see     FrenchRepublicanEra
 * @see     FrenchRepublicanMonth
 * @see     Sansculottides
 * @see     DayOfDecade
 * @since   3.33/4.28
 * @doctags.concurrency {immutable}
 */
@CalendarType("extra/frenchrev")
public final class FrenchRepublicanCalendar
    extends Calendrical<FrenchRepublicanCalendar.Unit, FrenchRepublicanCalendar> {

    //~ Statische Felder/Initialisierungen --------------------------------

    // value chosen in order to make withEndOfFranciade() always working for equinox algorithm
    static final int MAX_YEAR = 1202; // < 3000 - 1792 + 1

    private static final int YEAR_INDEX = 0;
    private static final int DECADE_INDEX = 1;
    private static final int DAY_OF_MONTH_INDEX = 2;
    private static final int DAY_OF_YEAR_INDEX = 3;

    /**
     * <p>Represents the Republican era of the French revolution. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die republikanische &Auml;ra der franz&ouml;sischen Revolution. </p>
     */
    @FormattableElement(format = "G")
    public static final ChronoElement<FrenchRepublicanEra> ERA =
        new StdEnumDateElement<FrenchRepublicanEra, FrenchRepublicanCalendar>(
            "ERA", FrenchRepublicanCalendar.class, FrenchRepublicanEra.class, 'G');

    /**
     * <p>Represents the republican year since 1792-09-22 (in range 1-1202). </p>
     *
     * <p>The year is printed as roman number when using a pattern and the French language. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert das republikanische Jahr gez&auml;hlt seit 1792-09-22 (im Bereich 1-1202). </p>
     *
     * <p>Das Jahr wird als r&ouml;mische Zahl formatiert, wenn ein Formatmuster und die franz&ouml;sische
     * Sprache verwendet werden. </p>
     */
    @FormattableElement(format = "Y")
    public static final StdCalendarElement<Integer, FrenchRepublicanCalendar> YEAR_OF_ERA =
        new YearOfEraElement();

    private static final SansculottidesAccess SANSCULOTTIDES_ACCESS = new SansculottidesAccess();
    private static final DayOfDecadeAccess DAY_OF_DECADE_ACCESS = new DayOfDecadeAccess();

    /**
     * <p>Represents the complementary days of the French revolutionary calendar. </p>
     *
     * <p><strong>Warning:</strong> A French republican date does usually not have a complementary day
     * so any access via {@code get(SANSCULOTTIDES)} will throw an exception unless
     * users make sure that the day in question is indeed a complementary day. </p>
     *
     * <p>However, it is always possible to query the date for the minimum or maximum complementary day
     * or to set the date to a complementary day even if the actual date is not a complementary day. </p>
     *
     * @see     #hasSansculottides()
     * @see     #hasMonth()
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die Erg&auml;nzungstage des franz&ouml;sischen Revolutionskalenders. </p>
     *
     * <p><strong>Warnung:</strong> Ein franz&ouml;sisches Republikdatum hat meistens keinen
     * Erg&auml;nzungstag, so da&szlig; jeder Zugriff via {@code get(SANSCULOTTIDES)} eine
     * Ausnahme werfen w&uuml;rde, es sei denn, Anwender stellen sicher, da&szlig; der aktuelle
     * Tag tats&auml;chlich ein Erg&auml;nzungstag ist. </p>
     *
     * <p>Allerdings ist es immer m&ouml;glich, das aktuelle Datum nach dem minimalen oder maximalen
     * Erg&auml;nzungstag zu fragen oder das aktuelle Datum auf einen Erg&auml;nzungstag zu setzen,
     * selbst wenn das Datum kein Erg&auml;nzungstag ist. </p>
     *
     * @see     #hasSansculottides()
     * @see     #hasMonth()
     */
    @FormattableElement(format = "S")
    public static final ChronoElement<Sansculottides> SANSCULOTTIDES = SANSCULOTTIDES_ACCESS;

    /**
     * <p>Represents the month (Vend&eacute;miaire - Fructidor) if available. </p>
     *
     * <p><strong>Warning:</strong> A French republican date does not always have a month. If the
     * date is a complementary day (Sansculottides) then any access via {@code get(MONTH_OF_YEAR)}
     * to this element will be rejected by raising an exception. Users have first to make sure
     * that the date is not such a complementary day. </p>
     *
     * <p>However, it is always possible to query the date for the minimum or maximum month
     * or to set the date to a month-related day even if the actual date is a complementary day. </p>
     *
     * @see     #hasSansculottides()
     * @see     #hasMonth()
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den Monat (Vend&eacute;miaire - Fructidor), wenn vorhanden. </p>
     *
     * <p><strong>Warnung:</strong> Ein franz&ouml;sisches Republikdatum hat nicht immer einen Monat. Wenn
     * es einen Erg&auml;nzungstag darstellt (Sansculottides), dann wird jeder Zugriff per {@code get(MONTH_OF_YEAR)}
     * auf dieses Element mit einer Ausnahme quittiert. Anwender m&uuml;ssen zuerst sicherstellen, da&szlig; das Datum
     * kein solcher Erg&auml;nzungstag ist. </p>
     *
     * <p>Allerdings ist es immer m&ouml;glich, das aktuelle Datum nach dem minimalen oder maximalen
     * Monat zu fragen oder das aktuelle Datum auf einen monatsbezogenen Tag zu setzen,
     * selbst wenn das Datum ein Erg&auml;nzungstag ist. </p>
     *
     * @see     #hasSansculottides()
     * @see     #hasMonth()
     */
    @FormattableElement(format = "M")
    public static final StdCalendarElement<FrenchRepublicanMonth, FrenchRepublicanCalendar> MONTH_OF_YEAR =
        new StdEnumDateElement<FrenchRepublicanMonth, FrenchRepublicanCalendar>(
            "MONTH_OF_YEAR",
            FrenchRepublicanCalendar.class,
            FrenchRepublicanMonth.class,
            'M');

    /**
     * <p>Yields the decade of republican month if available. </p>
     *
     * <p><strong>Warning:</strong> A French republican date does not always have a decade. If the
     * date is a complementary day (Sansculottides) then any access to this element will be rejected
     * by raising an exception. Users have first to make sure that the date is not such a complementary
     * day. </p>
     *
     * @see     #hasSansculottides()
     * @see     #hasMonth()
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die Dekade des Monats, wenn vorhanden. </p>
     *
     * <p><strong>Warnung:</strong> Ein franz&ouml;sisches Republikdatum hat nicht immer eine Dekade. Wenn
     * es einen Erg&auml;nzungstag darstellt (Sansculottides), dann wird jeder Zugriff auf dieses Element
     * mit einer Ausnahme quittiert. Anwender m&uuml;ssen zuerst sicherstellen, da&szlig; das Datum
     * kein solcher Erg&auml;nzungstag ist. </p>
     *
     * @see     #hasSansculottides()
     * @see     #hasMonth()
     */
    public static final StdCalendarElement<Integer, FrenchRepublicanCalendar> DECADE_OF_MONTH =
        new StdIntegerDateElement<FrenchRepublicanCalendar>(
            "DECADE_OF_MONTH", FrenchRepublicanCalendar.class, 1, 3, '\u0000', null, null);

    /**
     * <p>Represents the days of decade which consists of ten days. </p>
     *
     * <p><strong>Warning:</strong> A French republican date does not always have a day of decade. If the
     * date is a complementary day (Sansculottides) then any access to this element will be rejected
     * by raising an exception. Users have first to make sure that the date is not such a complementary
     * day. </p>
     *
     * @see     #hasSansculottides()
     * @see     #hasMonth()
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die Dekadentage des franz&ouml;sischen Revolutionskalenders. </p>
     *
     * <p><strong>Warnung:</strong> Ein franz&ouml;sisches Republikdatum hat nicht immer einen Dekadentag. Wenn
     * es einen Erg&auml;nzungstag darstellt (Sansculottides), dann wird jeder Zugriff auf dieses Element
     * mit einer Ausnahme quittiert. Anwender m&uuml;ssen zuerst sicherstellen, da&szlig; das Datum
     * kein solcher Erg&auml;nzungstag ist. </p>
     *
     * @see     #hasSansculottides()
     * @see     #hasMonth()
     */
    @FormattableElement(format = "C")
    public static final ChronoElement<DayOfDecade> DAY_OF_DECADE = DAY_OF_DECADE_ACCESS;

    /**
     * <p>Represents the day of month if available. </p>
     *
     * <p><strong>Warning:</strong> A French republican date does not always have a month. If the
     * date is a complementary day (Sansculottides) then any access to this element will be rejected
     * by raising an exception. Users have first to make sure that the date is not such a complementary
     * day. </p>
     *
     * @see     #hasSansculottides()
     * @see     #hasMonth()
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den Tag des Monats, wenn vorhanden. </p>
     *
     * <p><strong>Warnung:</strong> Ein franz&ouml;sisches Republikdatum hat nicht immer einen Monat. Wenn
     * es einen Erg&auml;nzungstag darstellt (Sansculottides), dann wird jeder Zugriff auf dieses Element
     * mit einer Ausnahme quittiert. Anwender m&uuml;ssen zuerst sicherstellen, da&szlig; das Datum
     * kein solcher Erg&auml;nzungstag ist. </p>
     *
     * @see     #hasSansculottides()
     * @see     #hasMonth()
     */
    @FormattableElement(format = "D")
    public static final StdCalendarElement<Integer, FrenchRepublicanCalendar> DAY_OF_MONTH =
        new StdIntegerDateElement<FrenchRepublicanCalendar>("DAY_OF_MONTH", FrenchRepublicanCalendar.class, 1, 30, 'D');

    /**
     * <p>Represents the day of year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den Tag des Jahres. </p>
     */
    public static final StdCalendarElement<Integer, FrenchRepublicanCalendar> DAY_OF_YEAR =
        new StdIntegerDateElement<FrenchRepublicanCalendar>(
            "DAY_OF_YEAR", FrenchRepublicanCalendar.class, 1, 365, '\u0000');

    /**
     * <p>Represents the day of week where the week is seven days long. </p>
     *
     * <p>If the day-of-week is set to a new value then Time4J handles the calendar week
     * as starting on Sunday. </p>
     *
     * @see     #DAY_OF_DECADE
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den Tag der Woche, die 7 Tage lang ist. </p>
     *
     * <p>Wenn der Tag der Woche auf einen neuen Wert gesetzt wird, behandelt Time4J die
     * Kalenderwoche so, da&szlig; sie am Sonntag beginnt. </p>
     *
     * @see     #DAY_OF_DECADE
     */
    @FormattableElement(format = "E")
    public static final StdCalendarElement<Weekday, FrenchRepublicanCalendar> DAY_OF_WEEK =
        new StdWeekdayElement<FrenchRepublicanCalendar>(FrenchRepublicanCalendar.class, getDefaultWeekmodel());

    private static final CalendarSystem<FrenchRepublicanCalendar> CALSYS;
    private static final TimeAxis<FrenchRepublicanCalendar.Unit, FrenchRepublicanCalendar> ENGINE;
    private static final FrenchRepublicanAlgorithm DEFAULT_ALGORITHM = FrenchRepublicanAlgorithm.EQUINOX;

    static {
        CALSYS = new Transformer();

        TimeAxis.Builder<FrenchRepublicanCalendar.Unit, FrenchRepublicanCalendar> builder =
            TimeAxis.Builder.setUp(
                FrenchRepublicanCalendar.Unit.class,
                FrenchRepublicanCalendar.class,
                new Merger(),
                CALSYS)
            .appendElement(
                ERA,
                new EraRule())
            .appendElement(
                YEAR_OF_ERA,
                new IntegerRule(YEAR_INDEX),
                Unit.YEARS)
            .appendElement(
                SANSCULOTTIDES,
                SANSCULOTTIDES_ACCESS)
            .appendElement(
                MONTH_OF_YEAR,
                new MonthRule(),
                Unit.MONTHS)
            .appendElement(
                DECADE_OF_MONTH,
                new IntegerRule(DECADE_INDEX),
                Unit.DECADES)
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
                DAY_OF_DECADE,
                DAY_OF_DECADE_ACCESS)
            .appendUnit(
                Unit.YEARS,
                new FUnitRule(Unit.YEARS),
                Unit.YEARS.getLength())
            .appendUnit(
                Unit.MONTHS,
                new FUnitRule(Unit.MONTHS),
                Unit.MONTHS.getLength())
            .appendUnit(
                Unit.DECADES,
                new FUnitRule(Unit.DECADES),
                Unit.DECADES.getLength())
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

    //private static final long serialVersionUID = 7482205842000661998L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final int fyear;
    private transient final int fdoy;

    //~ Konstruktoren -----------------------------------------------------

    // also called by FrenchRepublicanAlgorithm-enum
    FrenchRepublicanCalendar(
        int fyear,
        int fdoy
    ) {
        super();

        this.fyear = fyear;
        this.fdoy = fdoy;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new instance of a French republican date. </p>
     *
     * @param   year        republican year in range 1-1202
     * @param   month       republican month as enum
     * @param   dayOfMonth  day of month in range 1-30
     * @return  new instance of {@code FrenchRepublicanCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    /*[deutsch]
     * <p>Erzeugt ein neues franz&ouml;sisches Republikdatum. </p>
     *
     * @param   year        republican year in range 1-1202
     * @param   month       republican month as enum
     * @param   dayOfMonth  day of month in range 1-30
     * @return  new instance of {@code FrenchRepublicanCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    public static FrenchRepublicanCalendar of(
        int year,
        FrenchRepublicanMonth month,
        int dayOfMonth
    ) {

        return FrenchRepublicanCalendar.of(year, month.getValue(), dayOfMonth);

    }

    /**
     * <p>Creates a new instance of a French republican date. </p>
     *
     * @param   year        republican year in range 1-1202
     * @param   month       republican month in range 1-12
     * @param   dayOfMonth  day of month in range 1-30
     * @return  new instance of {@code FrenchRepublicanCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    /*[deutsch]
     * <p>Erzeugt ein neues franz&ouml;sisches Republikdatum. </p>
     *
     * @param   year        republican year in range 1-1202
     * @param   month       republican month in range 1-12
     * @param   dayOfMonth  day of month in range 1-30
     * @return  new instance of {@code FrenchRepublicanCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    public static FrenchRepublicanCalendar of(
        int year,
        int month,
        int dayOfMonth
    ) {

        if ((year < 1) || (year > MAX_YEAR) || (month < 1) || (month > 12) || (dayOfMonth < 1) || (dayOfMonth > 30)) {
            throw new IllegalArgumentException(
                "Invalid French republican date: year=" + year + ", month=" + month + ", day=" + dayOfMonth);
        }

        return new FrenchRepublicanCalendar(year, (month - 1) * 30 + dayOfMonth);

    }

    /**
     * <p>Creates a new instance of a French republican date as complementary day. </p>
     *
     * @param   year            republican year in range 1-1202
     * @param   sansculottides  the complementary day
     * @return  new instance of {@code FrenchRepublicanCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    /*[deutsch]
     * <p>Erzeugt ein neues franz&ouml;sisches Republikdatum als Erg&auml;nzungstag. </p>
     *
     * @param   year            republican year in range 1-1202
     * @param   sansculottides  the complementary day
     * @return  new instance of {@code FrenchRepublicanCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    public static FrenchRepublicanCalendar of(
        int year,
        Sansculottides sansculottides
    ) {

        if ((year < 1) || (year > MAX_YEAR)) {
            throw new IllegalArgumentException("Year out of range: " + year);
        } else if ((sansculottides == Sansculottides.LEAP_DAY) && !isLeapYear(year)) {
            throw new IllegalArgumentException("Day of Revolution only exists in leap years: " + year);
        }

        return new FrenchRepublicanCalendar(year, 360 + sansculottides.getValue());

    }

    /**
     * <p>Obtains the current calendar date in system time. </p>
     *
     * <p>Convenient short-cut for: {@code SystemClock.inLocalView().now(FrenchRepublicanCalendar.axis())}. </p>
     *
     * @return  current calendar date in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(Chronology)
     */
    /*[deutsch]
     * <p>Ermittelt das aktuelle Kalenderdatum in der Systemzeit. </p>
     *
     * <p>Bequeme Abk&uuml;rzung f&uuml;r:
     * {@code SystemClock.inLocalView().now(FrenchRepublicanCalendar.axis())}. </p>
     *
     * @return  current calendar date in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(net.time4j.engine.Chronology)
     */
    public static FrenchRepublicanCalendar nowInSystemTime() {

        return SystemClock.inLocalView().now(FrenchRepublicanCalendar.axis());

    }

    /**
     * <p>Yields the republican era. </p>
     *
     * @return  {@link FrenchRepublicanEra#REPUBLICAN}
     */
    /*[deutsch]
     * <p>Liefert die republikanische &Auml;ra. </p>
     *
     * @return  {@link FrenchRepublicanEra#REPUBLICAN}
     */
    public FrenchRepublicanEra getEra() {

        return FrenchRepublicanEra.REPUBLICAN;

    }

    /**
     * <p>Yields the republican year. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert das republikanische Jahr. </p>
     *
     * @return  int
     */
    public int getYear() {

        return this.fyear;

    }

    /**
     * <p>Yields the republican month if available. </p>
     *
     * @return  month enum
     * @throws  ChronoException if this date is a complementary day
     * @see     #MONTH_OF_YEAR
     * @see     #hasSansculottides()
     * @see     #hasMonth()
     */
    /*[deutsch]
     * <p>Liefert den republikanischen Monat, wenn vorhanden. </p>
     *
     * @return  month enum
     * @throws  ChronoException if this date is a complementary day
     * @see     #MONTH_OF_YEAR
     * @see     #hasSansculottides()
     * @see     #hasMonth()
     */
    public FrenchRepublicanMonth getMonth() {

        if (this.fdoy > 360) {
            throw new ChronoException(
                "Complementary days (sansculottides) do not represent any month: " + this.toString());
        }

        int m = ((this.fdoy - 1) / 30) + 1;
        return FrenchRepublicanMonth.valueOf(m);

    }

    /**
     * <p>Yields the decade of current republican month if available. </p>
     *
     * @return  int (1, 2 or 3)
     * @throws  ChronoException if this date is a complementary day
     * @see     #DECADE_OF_MONTH
     * @see     #hasSansculottides()
     * @see     #hasMonth()
     */
    /*[deutsch]
     * <p>Liefert die Dekade des aktuellen republikanischen Monats, wenn vorhanden. </p>
     *
     * @return  int (1, 2 or 3)
     * @throws  ChronoException if this date is a complementary day
     * @see     #DECADE_OF_MONTH
     * @see     #hasSansculottides()
     * @see     #hasMonth()
     */
    public int getDecade() {

        if (this.fdoy > 360) {
            throw new ChronoException(
                "Complementary days (sansculottides) do not represent any decade: " + this.toString());
        }

        return (((this.fdoy - 1) % 30) / 10) + 1;

    }

    /**
     * <p>Yields the day of month if available. </p>
     *
     * @return  int (1-30)
     * @throws  ChronoException if this date is a complementary day
     * @see     #DAY_OF_MONTH
     * @see     #hasSansculottides()
     * @see     #hasMonth()
     */
    /*[deutsch]
     * <p>Liefert den Tag des Monats, wenn vorhanden. </p>
     *
     * @return  int (1-30)
     * @throws  ChronoException if this date is a complementary day
     * @see     #DAY_OF_MONTH
     * @see     #hasSansculottides()
     * @see     #hasMonth()
     */
    public int getDayOfMonth() {

        if (this.fdoy > 360) {
            throw new ChronoException(
                "Complementary days (sansculottides) are not part of any month: " + this.toString());
        }

        return ((this.fdoy - 1) % 30) + 1;

    }

    /**
     * <p>Yields the day of decade if available (ten-day-week). </p>
     *
     * @return  enum
     * @throws  ChronoException if this date is a complementary day
     * @see     #DAY_OF_DECADE
     * @see     #hasSansculottides()
     * @see     #hasMonth()
     */
    /*[deutsch]
     * <p>Liefert den Tag des Monats, wenn vorhanden (Zehn-Tage-Woche). </p>
     *
     * @return  enum
     * @throws  ChronoException if this date is a complementary day
     * @see     #DAY_OF_DECADE
     * @see     #hasSansculottides()
     * @see     #hasMonth()
     */
    public DayOfDecade getDayOfDecade() {

        if (this.hasSansculottides()) {
            throw new ChronoException("Day of decade does not exist on sansculottides: " + this.toString());
        }

        return DayOfDecade.valueOf(((this.fdoy - 1) % 10) + 1);

    }

    /**
     * <p>Determines the day of standard-week (with seven days). </p>
     *
     * @return  Weekday
     */
    /*[deutsch]
     * <p>Ermittelt den Wochentag bezogen auf eine 7-Tage-Woche. </p>
     *
     * @return  Weekday
     */
    public Weekday getDayOfWeek() {

        long utcDays = CALSYS.transform(this);
        return Weekday.valueOf(MathUtils.floorModulo(utcDays + 5, 7) + 1);

    }

    /**
     * <p>Yields the day of year. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert den Tag des Jahres. </p>
     *
     * @return  int
     */
    public int getDayOfYear() {

        return this.fdoy;

    }

    /**
     * <p>Yields the complementary day if available. </p>
     *
     * @return  Sansculottides enum
     * @throws  ChronoException if this date is not a complementary day
     * @see     #hasSansculottides()
     * @see     #hasMonth()
     */
    /*[deutsch]
     * <p>Liefert den Erg&auml;nzungstag, wenn vorhanden. </p>
     *
     * @return  Sansculottides enum
     * @throws  ChronoException if this date is not a complementary day
     * @see     #hasSansculottides()
     * @see     #hasMonth()
     */
    public Sansculottides getSansculottides() {

        if (this.fdoy <= 360) {
            throw new ChronoException(
                "Not a sansculottides day: " + this.toString());
        }

        return Sansculottides.valueOf(this.fdoy - 360);

    }

    /**
     * <p>Is this date a complementary day? </p>
     *
     * <p>A date in the French revolutionary calendar has either a month or is a complementary day. </p>
     *
     * @return  boolean
     * @see     #hasMonth()
     * @see     #MONTH_OF_YEAR
     * @see     #DAY_OF_MONTH
     * @see     #DECADE_OF_MONTH
     * @see     #DAY_OF_DECADE
     * @see     #SANSCULOTTIDES
     */
    /*[deutsch]
     * <p>Liegt dieses Datum auf einem Erg&auml;nzungstag? </p>
     *
     * <p>Ein Datum im franz&ouml;sischen Revolutionskalender hat entweder einen Monat
     * oder ist ein Erg&auml;nzungstag. </p>
     *
     * @return  boolean
     * @see     #hasMonth()
     * @see     #MONTH_OF_YEAR
     * @see     #DAY_OF_MONTH
     * @see     #DECADE_OF_MONTH
     * @see     #DAY_OF_DECADE
     * @see     #SANSCULOTTIDES
     */
    public boolean hasSansculottides() {

        return (this.fdoy > 360);

    }

    /**
     * <p>Does this date contain a month? </p>
     *
     * <p>A date in the French revolutionary calendar has either a month or is a complementary day. </p>
     *
     * @return  boolean
     * @see     #hasSansculottides()
     * @see     #MONTH_OF_YEAR
     * @see     #DAY_OF_MONTH
     * @see     #DECADE_OF_MONTH
     * @see     #DAY_OF_DECADE
     * @see     #SANSCULOTTIDES
     */
    /*[deutsch]
     * <p>Liegt dieses Datum in einem Monat? </p>
     *
     * <p>Ein Datum im franz&ouml;sischen Revolutionskalender hat entweder einen Monat
     * oder ist ein Erg&auml;nzungstag. </p>
     *
     * @return  boolean
     * @see     #hasSansculottides()
     * @see     #MONTH_OF_YEAR
     * @see     #DAY_OF_MONTH
     * @see     #DECADE_OF_MONTH
     * @see     #DAY_OF_DECADE
     * @see     #SANSCULOTTIDES
     */
    public boolean hasMonth() {

        return (this.fdoy <= 360);

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

        return isLeapYear(this.fyear);

    }

    /**
     * <p>Is given republican year a leap year? </p>
     *
     * @param   year    republican year to be checked (in range 1-1202)
     * @return  boolean
     */
    /*[deutsch]
     * <p>Ist das angegebene republikanische Jahr ein Schaltjahr? </p>
     *
     * @param   year    republican year to be checked (in range 1-1202)
     * @return  boolean
     */
    public static boolean isLeapYear(int year) {

        return DEFAULT_ALGORITHM.isLeapYear(year);

    }

    /**
     * <p>Obtains an alternative date view specific for given algorithm. </p>
     *
     * @param   algorithm   calendar computation
     * @return  French republican date (possibly modified)
     * @throws  IllegalArgumentException in case of date overflow
     * @since   3.33/4.28
     */
    /*[deutsch]
     * <p>Erh&auml;lt eine alternative Datumssicht spezifisch f&uuml;r den angegebenen Algorithmus. </p>
     *
     * @param   algorithm   calendar computation
     * @return  French republican date (possibly modified)
     * @throws  IllegalArgumentException in case of date overflow
     * @since   3.33/4.28
     */
    public Date getDate(FrenchRepublicanAlgorithm algorithm) {

        if (algorithm == DEFAULT_ALGORITHM) {
            return new Date(this, DEFAULT_ALGORITHM);
        }

        long utcDays = DEFAULT_ALGORITHM.transform(this);
        return new Date(algorithm.transform(utcDays), algorithm);

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
    public GeneralTimestamp<FrenchRepublicanCalendar> at(PlainTime time) {

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
    public GeneralTimestamp<FrenchRepublicanCalendar> atTime(
        int hour,
        int minute
    ) {

        return this.at(PlainTime.of(hour, minute));

    }

    /**
     * <p>Obtains the next leap day when the franciade ends. </p>
     *
     * @return  end of franciade
     * @see     Sansculottides#LEAP_DAY
     */
    /*[deutsch]
     * <p>Liefert den n&auml;chsten Schalttag am Ende einer &quot;franciade&quot;. </p>
     *
     * @return  end of franciade
     * @see     Sansculottides#LEAP_DAY
     */
    public FrenchRepublicanCalendar withEndOfFranciade() {

        int y = this.fyear;

        while (!isLeapYear(y)) {
            y++;
        }

        return new FrenchRepublicanCalendar(y, 366);

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof FrenchRepublicanCalendar) {
            FrenchRepublicanCalendar that = (FrenchRepublicanCalendar) obj;
            return ((this.fyear == that.fyear) && (this.fdoy == that.fdoy));
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return (17 * this.fdoy + 37 * this.fyear);

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(32);
        sb.append("French-Republic-");
        sb.append(NumberSystem.ROMAN.toNumeral(this.fyear));
        sb.append('-');
        if (this.fdoy > 360) {
            sb.append("Sansculottides-");
            sb.append(String.valueOf(this.fdoy - 360));
        } else {
            int m = this.getMonth().getValue();
            if (m < 10) {
                sb.append('0');
            }
            sb.append(m);
            sb.append('-');
            int dom = this.getDayOfMonth();
            if (dom < 10) {
                sb.append('0');
            }
            sb.append(dom);
        }
        return sb.toString();

    }

    @Override
    public boolean contains(ChronoElement<?> element) {

        if (
            element == MONTH_OF_YEAR
            || element == DECADE_OF_MONTH
            || element == DAY_OF_DECADE
            || element == DAY_OF_MONTH
        ) {
            return this.hasMonth();
        } else if (element == SANSCULOTTIDES) {
            return this.hasSansculottides();
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

        if (element == MONTH_OF_YEAR) {
            return (value != null);
        } else if (element == SANSCULOTTIDES) {
            return SANSCULOTTIDES_ACCESS.isValid(this, Sansculottides.class.cast(value));
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
    public static TimeAxis<Unit, FrenchRepublicanCalendar> axis() {

        return ENGINE;

    }

    @Override
    protected TimeAxis<Unit, FrenchRepublicanCalendar> getChronology() {

        return ENGINE;

    }

    @Override
    protected FrenchRepublicanCalendar getContext() {

        return this;

    }

    /**
     * <p>Obtains the standard week model of this calendar. </p>
     *
     * <p>This calendar historically starts on Sunday. </p>
     *
     * @return  Weekmodel
     */
    /*[deutsch]
     * <p>Ermittelt das Standardwochenmodell dieses Kalenders. </p>
     *
     * <p>Dieser Kalender startet historisch am Sonntag. </p>
     *
     * @return  Weekmodel
     */
    private static Weekmodel getDefaultWeekmodel() {

        return Weekmodel.of(Weekday.SUNDAY, 1, Weekday.SUNDAY, Weekday.SUNDAY); // historic condition

    }

    private static <V> boolean isAccessible(
        FrenchRepublicanCalendar fcal,
        ChronoElement<V> element
    ) {

        try {
            return fcal.isValid(element, fcal.get(element));
        } catch (ChronoException ex) {
            return false;
        }

    }

    /**
     * @serialData  Uses <a href="../../../serialized-form.html#net.time4j.calendar.SPX">
     *              a dedicated serialization form</a> as proxy. The first byte contains
     *              the type-ID {@code 10}. Then the year is written as int, finally
     *              month and day-of-month as bytes.
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return null; // new SPX(this, SPX.INDIAN);

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
     * <p>Defines come calendar units for the French revolutionary calendar. </p>
     */
    /*[deutsch]
     * <p>Definiert einige kalendarische Zeiteinheiten f&uuml;r den franz&ouml;sischen Revolutionskalender. </p>
     */
    public static enum Unit
        implements ChronoUnit {

        //~ Statische Felder/Initialisierungen ----------------------------

        /**
         * <p>Years are defined as equinox years and not as tropical years. </p>
         */
        /*[deutsch]
         * <p>Jahre sind als Herbstbezogene Jahre und nicht als tropische Jahre definiert. </p>
         */
        YEARS(365.242374 * 86400.0), // length => J. Meeus and Savoie 1992, p. 42

        /**
         * <p>The month arithmetic handles the sansculottides as extension of last month Fructidor. </p>
         *
         * <p>The resulting date is always within a republican month. Example: </p>
         *
         * <pre>
         *     FrenchRepublicanCalendar cal = FrenchRepublicanCalendar.of(1, Sansculottides.COMPLEMENTARY_DAY_3);
         *     FrenchRepublicanCalendar next = cal.plus(1, FrenchRepublicanCalendar.Unit.MONTHS);
         *     System.out.println(next); // French-Republic-II-01-30 (30th of Vend&eacute;miaire II)
         * </pre>
         */
        /*[deutsch]
         * <p>Die Monatsarithmetik behandelt die Erg&auml;nzungstage (sansculottides) als eine Erweiterung
         * des letzten Monats Fructidor. </p>
         *
         * <p>Das Ergebnisdatum ist immer innerhalb eines republikanischen Monats. Beispiel: </p>
         *
         * <pre>
         *     FrenchRepublicanCalendar cal = FrenchRepublicanCalendar.of(1, Sansculottides.COMPLEMENTARY_DAY_3);
         *     FrenchRepublicanCalendar next = cal.plus(1, FrenchRepublicanCalendar.Unit.MONTHS);
         *     System.out.println(next); // French-Republic-II-01-30 (30th of Vend&eacute;miaire II)
         * </pre>
         */
        MONTHS(30 * 86400.0),

        /**
         * <p>Decades consist of ten days where complementary days will be ignored or skipped. </p>
         */
        /*[deutsch]
         * <p>Dekaden bestehen aus zehn Tagen, wobei Erg&auml;nzungstage ignoriert oder &uuml;bersprungen werden. </p>
         */
        DECADES(10 * 86400.0),

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
         * <p>Calculates the difference between given Indian dates in this unit. </p>
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
            FrenchRepublicanCalendar start,
            FrenchRepublicanCalendar end
        ) {

            return start.until(end, this);

        }

    }

    /**
     * <p>Static view of calendar date taking into account possibly different calendar algorithms. </p>
     *
     * <p>Note: Only elements registered in the French republican calendar chronology are supported. </p>
     *
     * @see     #getDate(FrenchRepublicanAlgorithm)
     * @see     FrenchRepublicanAlgorithm#attribute()
     * @since   3.33/4.28
     */
    /*[deutsch]
     * <p>Statische Ansicht eines Kalenderdatums, das auf verschiedenen Kalenderalgorithmen basieren kann. </p>
     *
     * <p>Hinweis: Nur in der Chronologie des franz&ouml;sischen Revolutionskalenders registrierte Elemente
     * werden unterst&uuml;tzt. </p>
     *
     * @see     #getDate(FrenchRepublicanAlgorithm)
     * @see     FrenchRepublicanAlgorithm#attribute()
     * @since   3.33/4.28
     */
    public static final class Date
        implements ChronoDisplay {

        //~ Instanzvariablen ----------------------------------------------

        private final FrenchRepublicanCalendar delegate;
        private final FrenchRepublicanAlgorithm algorithm;

        //~ Konstruktoren -------------------------------------------------

        private Date(
            FrenchRepublicanCalendar delegate,
            FrenchRepublicanAlgorithm algorithm
        ) {
            super();

            this.delegate = delegate;
            this.algorithm = algorithm;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public boolean contains(ChronoElement<?> element) {
            return ENGINE.isRegistered(element);
        }

        @Override
        public <V> V get(ChronoElement<V> element) {
            if (element == DAY_OF_WEEK) {
                long utcDays = this.algorithm.transform(this.delegate);
                return element.getType().cast(Weekday.valueOf(MathUtils.floorModulo(utcDays + 5, 7) + 1));
            } else if (element instanceof EpochDays) {
                EpochDays ed = EpochDays.class.cast(element);
                long utcDays = this.algorithm.transform(this.delegate);
                return element.getType().cast(Long.valueOf(ed.transform(utcDays, EpochDays.UTC)));
            } else if (ENGINE.isRegistered(element)) {
                return this.delegate.get(element);
            } else {
                throw new ChronoException("French republican dates only support registered elements.");
            }
        }

        @Override
        public int getInt(ChronoElement<Integer> element) {
            if (ENGINE.isRegistered(element)) {
                return this.delegate.getInt(element);
            } else {
                return Integer.MIN_VALUE;
            }
        }

        @Override
        public <V> V getMinimum(ChronoElement<V> element) {
            if (ENGINE.isRegistered(element)) {
                return this.delegate.getMinimum(element);
            } else {
                throw new ChronoException("French republican dates only support registered elements.");
            }
        }

        @Override
        public <V> V getMaximum(ChronoElement<V> element) {
            if (ENGINE.isRegistered(element)) {
                return this.delegate.getMaximum(element);
            } else {
                throw new ChronoException("French republican dates only support registered elements.");
            }
        }

        /**
         * <p>This date has no timezone (offset). </p>
         *
         * @return  {@code false}
         */
        /*[deutsch]
         * <p>Dieses Datum hat keine Zeitzone oder Verschiebung. </p>
         *
         * @return  {@code false}
         */
        @Override
        public boolean hasTimezone() {
            return false;
        }

        /**
         * <p>Always throws an exception. </p>
         *
         * @return  (nothing)
         * @throws  ChronoException (always)
         */
        /*[deutsch]
         * <p>Wirft immer eine Ausnahme. </p>
         *
         * @return  (nothing)
         * @throws  ChronoException (always)
         */
        @Override
        public TZID getTimezone() {
            throw new ChronoException("Timezone not available.");
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (obj instanceof Date) {
                Date that = (Date) obj;
                if (this.algorithm != that.algorithm) {
                    return false;
                } else {
                    return this.delegate.equals(that.delegate);
                }
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return 7 * this.delegate.hashCode() + 31 * this.algorithm.hashCode();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.delegate);
            sb.append('[');
            sb.append(this.algorithm);
            sb.append(']');
            return sb.toString();
        }

    }

    private static class Transformer
        implements CalendarSystem<FrenchRepublicanCalendar> {

        //~ Methoden ------------------------------------------------------

        @Override
        public FrenchRepublicanCalendar transform(long utcDays) {

            return DEFAULT_ALGORITHM.transform(utcDays);

        }

        @Override
        public long transform(FrenchRepublicanCalendar date) {

            return DEFAULT_ALGORITHM.transform(date);

        }

        @Override
        public long getMinimumSinceUTC() {

            FrenchRepublicanCalendar min = new FrenchRepublicanCalendar(1, 1);
            return this.transform(min);

        }

        @Override
        public long getMaximumSinceUTC() {

            FrenchRepublicanCalendar max = new FrenchRepublicanCalendar(MAX_YEAR, 366);
            return this.transform(max);

        }

        @Override
        public List<CalendarEra> getEras() {

            CalendarEra era = FrenchRepublicanEra.REPUBLICAN;
            return Collections.singletonList(era);

        }

    }

    private static class YearOfEraElement
        extends DualYearOfEraElement<FrenchRepublicanCalendar> {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final long serialVersionUID = 7337125729623271040L;

        //~ Konstruktoren -------------------------------------------------

        private YearOfEraElement() {
            super(FrenchRepublicanCalendar.class, 1, MAX_YEAR, 'Y');

        }

        //~ Methoden ------------------------------------------------------

        @Override
        protected NumberSystem getNumberSystem(AttributeQuery attributes) {

            String pattern = attributes.get(Attributes.FORMAT_PATTERN, "");

            if (
                pattern.contains("Y")
                && attributes.get(Attributes.LANGUAGE, Locale.ROOT).getLanguage().equals("fr")
            ) {
                return NumberSystem.ROMAN;
            }

            return attributes.get(Attributes.NUMBER_SYSTEM, NumberSystem.ROMAN);

        }

    }

    private static class IntegerRule
        implements IntElementRule<FrenchRepublicanCalendar> {

        //~ Instanzvariablen ----------------------------------------------

        private final int index;

        //~ Konstruktoren -------------------------------------------------

        IntegerRule(int index) {
            super();

            this.index = index;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public int getInt(FrenchRepublicanCalendar context) {

            switch (this.index) {
                case YEAR_INDEX:
                    return context.fyear;
                case DECADE_INDEX:
                    return context.getDecade();
                case DAY_OF_MONTH_INDEX:
                    return context.getDayOfMonth();
                case DAY_OF_YEAR_INDEX:
                    return context.fdoy;
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public boolean isValid(FrenchRepublicanCalendar context, int value) {

            if ((this.index == DAY_OF_MONTH_INDEX || this.index == DECADE_INDEX) && context.hasSansculottides()) {
                return false;
            }

            int min = this.getMin(context);
            int max = this.getMax(context);
            return ((min <= value) && (max >= value));

        }

        @Override
        public FrenchRepublicanCalendar withValue(FrenchRepublicanCalendar context, int value, boolean lenient) {

            if ((this.index == DAY_OF_MONTH_INDEX) && context.hasSansculottides()) {
                throw new IllegalArgumentException("Day of month not defined on sansculottides: " + value);
            } else if ((this.index == DECADE_INDEX) && context.hasSansculottides()) {
                throw new IllegalArgumentException("Decade of month not defined on sansculottides: " + value);
            } else if (!this.isValid(context, value)) {
                throw new IllegalArgumentException("Out of range: " + value);
            }

            switch (this.index) {
                case YEAR_INDEX:
                    int dmax = DEFAULT_ALGORITHM.isLeapYear(value) ? 366 : 365;
                    return new FrenchRepublicanCalendar(value, Math.min(context.fdoy, dmax));
                case DECADE_INDEX:
                    int dom = (value - 1) * 10 + ((context.getDayOfMonth() - 1) % 10) + 1;
                    return FrenchRepublicanCalendar.of(context.fyear, context.getMonth(), dom);
                case DAY_OF_MONTH_INDEX:
                    return FrenchRepublicanCalendar.of(context.fyear, context.getMonth(), value);
                case DAY_OF_YEAR_INDEX:
                    return new FrenchRepublicanCalendar(context.fyear, value);
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public Integer getValue(FrenchRepublicanCalendar context) {

            return Integer.valueOf(this.getInt(context));

        }

        @Override
        public Integer getMinimum(FrenchRepublicanCalendar context) {

            return Integer.valueOf(this.getMin(context));

        }

        @Override
        public Integer getMaximum(FrenchRepublicanCalendar context) {

            return Integer.valueOf(this.getMax(context));

        }

        @Override
        public boolean isValid(
            FrenchRepublicanCalendar context,
            Integer value
        ) {

            return ((value != null) && this.isValid(context, value.intValue()));

        }

        @Override
        public FrenchRepublicanCalendar withValue(
            FrenchRepublicanCalendar context,
            Integer value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing new value.");
            }

            return this.withValue(context, value.intValue(), lenient);

        }

        @Override
        public ChronoElement<?> getChildAtFloor(FrenchRepublicanCalendar context) {

            if (this.index == YEAR_INDEX) {
                return MONTH_OF_YEAR;
            } else if (this.index == DECADE_INDEX) {
                return DAY_OF_DECADE;
            }

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(FrenchRepublicanCalendar context) {

            if (this.index == YEAR_INDEX) {
                return SANSCULOTTIDES;
            } else if (this.index == DECADE_INDEX) {
                return DAY_OF_DECADE;
            }

            return null;

        }

        private int getMin(FrenchRepublicanCalendar context) {

            switch (this.index) {
                case YEAR_INDEX:
                case DAY_OF_YEAR_INDEX:
                    return 1;
                case DECADE_INDEX:
                case DAY_OF_MONTH_INDEX:
                    if (context.hasSansculottides()) {
                        throw new ChronoException(
                            "Complementary days (sansculottides) are not part of any month or decade: " + context);
                    } else {
                        return 1;
                    }
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        private int getMax(FrenchRepublicanCalendar context) {

            switch (this.index) {
                case YEAR_INDEX:
                    return MAX_YEAR;
                case DECADE_INDEX:
                case DAY_OF_MONTH_INDEX:
                    if (context.hasSansculottides()) {
                        throw new ChronoException(
                            "Complementary days (sansculottides) are not part of any month: " + context);
                    } else {
                        return ((this.index == DAY_OF_MONTH_INDEX) ? 30 : 3);
                    }
                case DAY_OF_YEAR_INDEX:
                    return DEFAULT_ALGORITHM.isLeapYear(context.fyear) ? 366 : 365;
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

    }

    private static class MonthRule
        implements ElementRule<FrenchRepublicanCalendar, FrenchRepublicanMonth> {

        //~ Methoden ------------------------------------------------------

        @Override
        public FrenchRepublicanMonth getValue(FrenchRepublicanCalendar context) {

            return context.getMonth();

        }

        @Override
        public FrenchRepublicanMonth getMinimum(FrenchRepublicanCalendar context) {

            return FrenchRepublicanMonth.VENDEMIAIRE;

        }

        @Override
        public FrenchRepublicanMonth getMaximum(FrenchRepublicanCalendar context) {

            return FrenchRepublicanMonth.FRUCTIDOR;

        }

        @Override
        public boolean isValid(
            FrenchRepublicanCalendar context,
            FrenchRepublicanMonth value
        ) {

            return (value != null);

        }

        @Override
        public FrenchRepublicanCalendar withValue(
            FrenchRepublicanCalendar context,
            FrenchRepublicanMonth value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing republican month.");
            } else if (context.hasSansculottides()) {
                return FrenchRepublicanCalendar.of(context.fyear, value, 30);
            } else {
                return FrenchRepublicanCalendar.of(context.fyear, value, context.getDayOfMonth());
            }

        }

        @Override
        public ChronoElement<?> getChildAtFloor(FrenchRepublicanCalendar context) {

            return DAY_OF_MONTH;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(FrenchRepublicanCalendar context) {

            return DAY_OF_MONTH;

        }

    }

    private static class EraRule
        implements ElementRule<FrenchRepublicanCalendar, FrenchRepublicanEra> {

        //~ Methoden ------------------------------------------------------

        @Override
        public FrenchRepublicanEra getValue(FrenchRepublicanCalendar context) {

            return FrenchRepublicanEra.REPUBLICAN;

        }

        @Override
        public FrenchRepublicanEra getMinimum(FrenchRepublicanCalendar context) {

            return FrenchRepublicanEra.REPUBLICAN;

        }

        @Override
        public FrenchRepublicanEra getMaximum(FrenchRepublicanCalendar context) {

            return FrenchRepublicanEra.REPUBLICAN;

        }

        @Override
        public boolean isValid(
            FrenchRepublicanCalendar context,
            FrenchRepublicanEra value
        ) {

            return (value != null);

        }

        @Override
        public FrenchRepublicanCalendar withValue(
            FrenchRepublicanCalendar context,
            FrenchRepublicanEra value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing era value.");
            }

            return context;

        }

        @Override
        public ChronoElement<?> getChildAtFloor(FrenchRepublicanCalendar context) {

            return YEAR_OF_ERA;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(FrenchRepublicanCalendar context) {

            return YEAR_OF_ERA;

        }

    }

    private static class DayOfDecadeAccess
        extends BasicElement<DayOfDecade>
        implements TextElement<DayOfDecade>, ElementRule<FrenchRepublicanCalendar, DayOfDecade> {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final long serialVersionUID = -8211850819064695450L;

        //~ Konstruktoren -------------------------------------------------

        DayOfDecadeAccess() {
            super("DAY_OF_DECADE");
        }

        //~ Methoden ------------------------------------------------------

        @Override
        public char getSymbol() {

            return 'C';

        }

        @Override
        public DayOfDecade getValue(FrenchRepublicanCalendar context) {

            return context.getDayOfDecade(); // may throw an exception

        }

        @Override
        public DayOfDecade getMinimum(FrenchRepublicanCalendar context) {

            if (context.hasSansculottides()) {
                throw new ChronoException("Cannot get minimum for day of decade on sansculottides: " + context);
            }

            return DayOfDecade.PRIMIDI;

        }

        @Override
        public DayOfDecade getMaximum(FrenchRepublicanCalendar context) {

            if (context.hasSansculottides()) {
                throw new ChronoException("Cannot get maximum for day of decade on sansculottides: " + context);
            }

            return DayOfDecade.DECADI;

        }

        @Override
        public boolean isValid(
            FrenchRepublicanCalendar context,
            DayOfDecade value
        ) {

            return (value != null) && !context.hasSansculottides();

        }

        @Override
        public FrenchRepublicanCalendar withValue(
            FrenchRepublicanCalendar context,
            DayOfDecade value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing day of decade.");
            } else if (context.hasSansculottides()) {
                throw new IllegalArgumentException("Cannot set day of decade on sansculottides.");
            }

            int oldValue = ((context.fdoy - 1) % 10) + 1;
            int delta = value.getValue() - oldValue;

            if (delta == 0) {
                return context;
            } else {
                return new FrenchRepublicanCalendar(context.fyear, context.fdoy + delta);
            }

        }

        @Override
        public ChronoElement<?> getChildAtFloor(FrenchRepublicanCalendar context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(FrenchRepublicanCalendar context) {

            return null;

        }

        @Override
        public void print(
            ChronoDisplay context,
            Appendable buffer,
            AttributeQuery attributes
        ) throws IOException, ChronoException {

            DayOfDecade value = context.get(this);
            Locale lang = attributes.get(Attributes.LANGUAGE, Locale.ROOT);
            buffer.append(this.accessor(lang, attributes).print(value));

        }

        @Override
        public DayOfDecade parse(
            CharSequence text,
            ParsePosition status,
            AttributeQuery attributes
        ) {

            Locale lang = attributes.get(Attributes.LANGUAGE, Locale.ROOT);
            return this.accessor(lang, attributes).parse(text, status, this.getType(), attributes);

        }

        @Override
        public Class<DayOfDecade> getType() {

            return DayOfDecade.class;

        }

        @Override
        public DayOfDecade getDefaultMinimum() {

            return DayOfDecade.PRIMIDI;

        }

        @Override
        public DayOfDecade getDefaultMaximum() {

            return DayOfDecade.DECADI;

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

            TextWidth width = attributes.get(Attributes.TEXT_WIDTH, TextWidth.WIDE);
            OutputContext oc = attributes.get(Attributes.OUTPUT_CONTEXT, OutputContext.FORMAT);
            String variant = ((width == TextWidth.NARROW) ? "N" : (oc == OutputContext.FORMAT ? "w" : "W"));
            return CalendarText.getInstance("extra/frenchrev", lang).getTextForms(this.name(), this.getType(), variant);

        }

    }

    private static class SansculottidesAccess
        extends BasicElement<Sansculottides>
        implements TextElement<Sansculottides>, ElementRule<FrenchRepublicanCalendar, Sansculottides> {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final long serialVersionUID = -6615947737325572130L;

        //~ Konstruktoren -------------------------------------------------

        SansculottidesAccess() {
            super("SANSCULOTTIDES");
        }

        //~ Methoden ------------------------------------------------------

        @Override
        public char getSymbol() {

            return 'S';

        }

        @Override
        public Sansculottides getValue(FrenchRepublicanCalendar context) {

            return context.getSansculottides(); // may throw an exception

        }

        @Override
        public Sansculottides getMinimum(FrenchRepublicanCalendar context) {

            return Sansculottides.COMPLEMENTARY_DAY_1;

        }

        @Override
        public Sansculottides getMaximum(FrenchRepublicanCalendar context) {

            return (context.isLeapYear() ? Sansculottides.LEAP_DAY : Sansculottides.COMPLEMENTARY_DAY_5);

        }

        @Override
        public boolean isValid(
            FrenchRepublicanCalendar context,
            Sansculottides value
        ) {

            if (value != null) {
                if (context.isLeapYear() || (value != Sansculottides.LEAP_DAY)) {
                    return true;
                }
            }

            return false;

        }

        @Override
        public FrenchRepublicanCalendar withValue(
            FrenchRepublicanCalendar context,
            Sansculottides value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing sansculottides value.");
            }

            return FrenchRepublicanCalendar.of(context.fyear, value);

        }

        @Override
        public ChronoElement<?> getChildAtFloor(FrenchRepublicanCalendar context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(FrenchRepublicanCalendar context) {

            return null;

        }

        @Override
        public void print(
            ChronoDisplay context,
            Appendable buffer,
            AttributeQuery attributes
        ) throws IOException, ChronoException {

            Sansculottides value = context.get(this);
            Locale lang = attributes.get(Attributes.LANGUAGE, Locale.ROOT);
            OutputContext oc = attributes.get(Attributes.OUTPUT_CONTEXT, OutputContext.FORMAT);
            buffer.append(this.accessor(lang, oc).print(value));

        }

        @Override
        public Sansculottides parse(
            CharSequence text,
            ParsePosition status,
            AttributeQuery attributes
        ) {

            int index = status.getIndex();
            Locale lang = attributes.get(Attributes.LANGUAGE, Locale.ROOT);
            OutputContext oc = attributes.get(Attributes.OUTPUT_CONTEXT, OutputContext.FORMAT);
            Sansculottides result = this.accessor(lang, oc).parse(text, status, this.getType(), attributes);

            if ((result == null) && attributes.get(Attributes.PARSE_MULTIPLE_CONTEXT, Boolean.TRUE)) {
                status.setErrorIndex(-1);
                status.setIndex(index);
                oc = ((oc == OutputContext.FORMAT) ? OutputContext.STANDALONE : OutputContext.FORMAT);
                result = this.accessor(lang, oc).parse(text, status, this.getType(), attributes);
            }

            return result;

        }

        @Override
        public Class<Sansculottides> getType() {

            return Sansculottides.class;

        }

        @Override
        public Sansculottides getDefaultMinimum() {

            return Sansculottides.COMPLEMENTARY_DAY_1;

        }

        @Override
        public Sansculottides getDefaultMaximum() {

            return Sansculottides.COMPLEMENTARY_DAY_5;

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
            OutputContext outputContext
        ) {

            String variant = ((outputContext == OutputContext.FORMAT) ? "w" : "W");
            return CalendarText.getInstance("extra/frenchrev", lang).getTextForms(this.name(), this.getType(), variant);

        }

    }

    private static class WeekdayRule
        implements ElementRule<FrenchRepublicanCalendar, Weekday> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Weekday getValue(FrenchRepublicanCalendar context) {

            return context.getDayOfWeek();

        }

        @Override
        public Weekday getMinimum(FrenchRepublicanCalendar context) {

            return getDefaultWeekmodel().getFirstDayOfWeek();

        }

        @Override
        public Weekday getMaximum(FrenchRepublicanCalendar context) {

            return getDefaultWeekmodel().getFirstDayOfWeek().roll(6);

        }

        @Override
        public boolean isValid(
            FrenchRepublicanCalendar context,
            Weekday value
        ) {

            return (value != null);

        }

        @Override
        public FrenchRepublicanCalendar withValue(
            FrenchRepublicanCalendar context,
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
        public ChronoElement<?> getChildAtFloor(FrenchRepublicanCalendar context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(FrenchRepublicanCalendar context) {

            return null;

        }

    }

    private static class Merger
        implements ChronoMerger<FrenchRepublicanCalendar> {

        //~ Methoden ------------------------------------------------------

        @Override
        public FrenchRepublicanCalendar createFrom(
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
        public FrenchRepublicanCalendar createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean preparsing
        ) {

            boolean lenient = attributes.get(Attributes.LENIENCY, Leniency.SMART).isLax();
            return this.createFrom(entity, attributes, lenient, preparsing);

        }

        @Override
        public FrenchRepublicanCalendar createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean lenient,
            boolean preparsing
        ) {

            FrenchRepublicanAlgorithm algorithm =
                attributes.get(FrenchRepublicanAlgorithm.attribute(), DEFAULT_ALGORITHM);
            int year = entity.getInt(YEAR_OF_ERA);

            if (year == Integer.MIN_VALUE) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Missing republican year.");
                return null;
            } else if ((year < 1) || (year > MAX_YEAR)) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Republican year out of range: " + year);
                return null;
            }

            FrenchRepublicanCalendar cal = null;

            if (entity.contains(MONTH_OF_YEAR)) {
                int month = entity.get(MONTH_OF_YEAR).getValue();
                int dom = entity.getInt(DAY_OF_MONTH);

                if ((dom == Integer.MIN_VALUE) && entity.contains(DAY_OF_DECADE)) {
                    int decade = entity.getInt(DECADE_OF_MONTH);
                    if (decade != Integer.MIN_VALUE) {
                        dom = (decade - 1) * 10 + entity.get(DAY_OF_DECADE).getValue();
                    }
                }

                if (dom != Integer.MIN_VALUE) {
                    if ((dom >= 1) && (dom <= 30)) {
                        cal = FrenchRepublicanCalendar.of(year, month, dom);
                    } else {
                        entity.with(ValidationElement.ERROR_MESSAGE, "Invalid republican date.");
                    }
                }
            } else if (entity.contains(SANSCULOTTIDES)) {
                Sansculottides s = entity.get(SANSCULOTTIDES);
                int doy = s.getValue() + 360;
                if ((doy == 6) && !algorithm.isLeapYear(year)) {
                    entity.with(ValidationElement.ERROR_MESSAGE, "Republican date is no leap year.");
                } else {
                    cal = new FrenchRepublicanCalendar(year, doy);
                }
            } else {
                int doy = entity.getInt(DAY_OF_YEAR);
                if (doy != Integer.MIN_VALUE) {
                    if ((doy >= 1) && (doy <= (algorithm.isLeapYear(year) ? 366 : 365))) {
                        cal = new FrenchRepublicanCalendar(year, doy);
                    }
                    entity.with(ValidationElement.ERROR_MESSAGE, "Invalid republican date.");
                }
            }

            if ((cal != null) && (algorithm != DEFAULT_ALGORITHM)) {
                cal = DEFAULT_ALGORITHM.transform(algorithm.transform(cal));
            }

            return cal;

        }

        @Override
        public ChronoDisplay preformat(FrenchRepublicanCalendar context, AttributeQuery attributes) {

            FrenchRepublicanAlgorithm algorithm =
                attributes.get(FrenchRepublicanAlgorithm.attribute(), DEFAULT_ALGORITHM);

            if (algorithm == DEFAULT_ALGORITHM) {
                return context;
            }

            return context.getDate(algorithm);

        }

        @Override
        public Chronology<?> preparser() {

            return null;

        }

        @Override
        public String getFormatPattern(
            DisplayStyle style,
            Locale locale
        ) {

            throw new UnsupportedOperationException("Localized format patterns are not available.");

        }

        @Override
        public StartOfDay getDefaultStartOfDay() {

            return StartOfDay.MIDNIGHT;

        }

        @Override
        public int getDefaultPivotYear() {

            return PlainDate.axis().getDefaultPivotYear() - 1792; // not relevant for dynamic pattern type

        }

    }

    private static class FUnitRule
        implements UnitRule<FrenchRepublicanCalendar> {

        //~ Instanzvariablen ----------------------------------------------

        private final Unit unit;

        //~ Konstruktoren -------------------------------------------------

        FUnitRule(Unit unit) {
            super();

            this.unit = unit;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public FrenchRepublicanCalendar addTo(FrenchRepublicanCalendar date, long amount) {

            switch (this.unit) {
                case YEARS:
                    int y = MathUtils.safeCast(MathUtils.safeAdd(date.fyear, amount));
                    if ((y < 1) || (y > MAX_YEAR)) {
                        throw new IllegalArgumentException("Resulting year out of bounds: " + y);
                    }
                    int doy = Math.min(date.fdoy, isLeapYear(y) ? 366 : 365);
                    return new FrenchRepublicanCalendar(y, doy);
                case MONTHS: // interprete sansculottides as extension of fructidor
                    long ym = MathUtils.safeAdd(ymValue(date), amount);
                    int year = MathUtils.safeCast(MathUtils.floorDivide(ym, 12));
                    int month = MathUtils.floorModulo(ym, 12) + 1;
                    int dom = (date.hasSansculottides() ? 30 : date.getDayOfMonth());
                    return FrenchRepublicanCalendar.of(year, month, dom);
                case DECADES: // interprete sansculottides as extension of last decade of fructidor
                    long dec = MathUtils.safeAdd(decValue(date), amount);
                    int year2 = MathUtils.safeCast(MathUtils.floorDivide(dec, 36));
                    int rem = MathUtils.floorModulo(dec, 36);
                    int month2 = MathUtils.floorDivide(rem, 3) + 1;
                    int decOfMonth = MathUtils.floorModulo(rem, 3);
                    int dod = (((date.hasSansculottides() ? 30 : date.getDayOfMonth()) - 1) % 10) + 1;
                    int dom2 = decOfMonth * 10 + dod;
                    return FrenchRepublicanCalendar.of(year2, month2, dom2);
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
        public long between(FrenchRepublicanCalendar start, FrenchRepublicanCalendar end) {

            switch (this.unit) {
                case YEARS:
                    int deltaY = end.fyear - start.fyear;
                    if ((deltaY > 0) && (end.fdoy < start.fdoy)) {
                        deltaY--;
                    } else if ((deltaY < 0) && (end.fdoy > start.fdoy)) {
                        deltaY++;
                    }
                    return deltaY;
                case MONTHS: // interprete sansculottides as extension of fructidor
                    long deltaM = ymValue(end) - ymValue(start);
                    int sdom = (start.hasSansculottides() ? (start.fdoy - 330) : start.getDayOfMonth());
                    int edom = (end.hasSansculottides() ? (end.fdoy - 330) : end.getDayOfMonth());
                    if ((deltaM > 0) && (edom < sdom)) {
                        deltaM--;
                    } else if ((deltaM < 0) && (edom > sdom)) {
                        deltaM++;
                    }
                    return deltaM;
                case DECADES: // interprete sansculottides as extension of last decade of fructidor
                    long deltaD = decValue(end) - decValue(start);
                    int sdod = (start.hasSansculottides() ? (start.fdoy - 350) : start.getDayOfDecade().getValue());
                    int edod = (end.hasSansculottides() ? (end.fdoy - 350) : end.getDayOfDecade().getValue());
                    if ((deltaD > 0) && (edod < sdod)) {
                        deltaD--;
                    } else if ((deltaD < 0) && (edod > sdod)) {
                        deltaD++;
                    }
                    return deltaD;
                case WEEKS:
                    return FrenchRepublicanCalendar.Unit.DAYS.between(start, end) / 7;
                case DAYS:
                    return CALSYS.transform(end) - CALSYS.transform(start);
                default:
                    throw new UnsupportedOperationException(this.unit.name());
            }

        }

        private static int ymValue(FrenchRepublicanCalendar date) {

            int m = (date.hasSansculottides() ? 12 : date.getMonth().getValue());
            return date.fyear * 12 + m - 1;

        }

        private static int decValue(FrenchRepublicanCalendar date) {

            int dec = (date.hasSansculottides() ? 3 : date.getDecade());
            return ymValue(date) * 3 + dec - 1;

        }

    }

}
