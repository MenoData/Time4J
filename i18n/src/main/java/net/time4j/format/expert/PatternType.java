/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (PatternType.java) is part of project Time4J.
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

package net.time4j.format.expert;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.Weekday;
import net.time4j.Weekmodel;
import net.time4j.engine.BridgeChronology;
import net.time4j.engine.CalendarVariant;
import net.time4j.engine.Calendrical;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoExtension;
import net.time4j.engine.Chronology;
import net.time4j.engine.EpochDays;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.CalendarType;
import net.time4j.format.ChronoPattern;
import net.time4j.format.DisplayMode;
import net.time4j.format.FormatEngine;
import net.time4j.format.NumberSystem;
import net.time4j.format.OutputContext;
import net.time4j.format.TextElement;
import net.time4j.format.TextWidth;
import net.time4j.format.internal.DualFormatElement;
import net.time4j.history.ChronoHistory;
import net.time4j.i18n.UltimateFormatEngine;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * <p>Collection of different format patterns. </p>
 *
 * @author  Meno Hochschild
 * @see     net.time4j.format.expert.ChronoFormatter.Builder#addPattern(String, PatternType)
 * @since   3.0
 */
/*[deutsch]
 * <p>Sammlung von verschiedenen Standard-Formatmustern. </p>
 *
 * @author  Meno Hochschild
 * @see     net.time4j.format.expert.ChronoFormatter.Builder#addPattern(String, PatternType)
 * @since   3.0
 */
public enum PatternType
    implements ChronoPattern<PatternType> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>This standard pattern is applicable on many chronologies and follows the standard
     * <a href="http://www.unicode.org/reports/tr35/tr35-dates.html">LDML</a> of unicode-consortium. </p>
     *
     * <p>If not explicitly stated otherwise the count of symbols always
     * controls the minimum count of digits in case of a numerical element.
     * Is an element shorter then the zero digit will be used for padding. </p>
     *
     * <p>Non-ISO-chronologies are also supported if their elements define CLDR-symbols. </p>
     *
     * <div style="margin-top:5px;">
     * <table border="1">
     *  <caption>Legend</caption>
     *  <tr>
     *      <th>Element</th>
     *      <th>Symbol</th>
     *      <th>Description</th>
     *  </tr>
     *  <tr>
     *      <td>{@link ChronoHistory#era() ERA}</td>
     *      <td>G</td>
     *      <td>One to three symbols indicate an abbreviation, four symbols
     *      indicate the long form and five symbols stand for a letter. The era
     *      is based on the chronological history of the current format locale. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link ChronoHistory#yearOfEra() YEAR_OF_ERA}</td>
     *      <td>y</td>
     *      <td>The count of symbols normally controls the minimum count of
     *      digits. If it is 2 however then the year will be printed with
     *      exact two digits using the attribute {@link Attributes#PIVOT_YEAR}.
     *      Important: If the era is not present then this symbol will simply
     *      be mapped to {@link PlainDate#YEAR}. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainDate#YEAR_OF_WEEKDATE}</td>
     *      <td>Y</td>
     *      <td>Represents the year in an ISO-8601 week date and behaves
     *      like the calendar year in formatting. The week-based year can
     *      deviate from the calendar year however because it is bound to
     *      the week cycle. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainDate#YEAR}</td>
     *      <td>u</td>
     *      <td>Proleptic ISO-8601 calendar year. This year never uses
     *      a pivot year, also not for &quot;uu&quot;. A positive sign
     *      will be used exactly if the year has more digits than given
     *      by count of symbols. In contrast to the symbol y, this year
     *      can never be the historized year-of-era. </td>
     *  </tr>
     *  <tr>
     *      <td>RELATED_GREGORIAN_YEAR</td>
     *      <td>r</td>
     *      <td>The related gregorian year corresponds to the begin of the calendar year
     *      in non-gregorian calender systems. In ISO-calendar systems, it is identical
     *      to the proleptic ISO-8601 calendar year. For formatting or parsing, only the
     *      ASCII-digits 0-9 will be used, even if other parts of the format use
     *      alternative digits or other numeral systems. The count of symbols is
     *      defined within the range 1-9. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainDate#QUARTER_OF_YEAR}</td>
     *      <td>Q</td>
     *      <td>One or two symbols for the numerical form, three symbols
     *      for the abbreviation, four for the full name and five for
     *      a letter symbol (NARROW). </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainDate#QUARTER_OF_YEAR}</td>
     *      <td>q</td>
     *      <td>Like Q, but in the version {@link OutputContext#STANDALONE}.
     *      In some languages (not english) the stand-alone-version requires
     *      a special grammar. </td>
     *  </tr>
     *  <tr>
     *      <td>MONTH_OF_YEAR</td>
     *      <td>M</td>
     *      <td>One or two symbols for the numerical form, three symbols
     *      for the abbreviation, four for the full name and five for
     *      a letter symbol (NARROW). Important: If the era is not present
     *      then this symbol will simply be mapped to {@link PlainDate#MONTH_OF_YEAR}. </td>
     *  </tr>
     *  <tr>
     *      <td>MONTH_OF_YEAR</td>
     *      <td>L</td>
     *      <td>Like M, but in the version {@link OutputContext#STANDALONE}.
     *      In some languages (not english) the stand-alone-version requires
     *      a special grammar. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link Weekmodel#weekOfYear()}</td>
     *      <td>w</td>
     *      <td>One or two symbols for the country-dependent week of year. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link Weekmodel#weekOfMonth()}</td>
     *      <td>W</td>
     *      <td>One symbol for the country-dependent week of month. </td>
     *  </tr>
     *  <tr>
     *      <td>DAY_OF_MONTH</td>
     *      <td>d</td>
     *      <td>One or two symbols for the day of month. Important: If the era is not present
     *      then this symbol will simply be mapped to {@link PlainDate#DAY_OF_MONTH}. </td>
     *  </tr>
     *  <tr>
     *      <td>DAY_OF_YEAR</td>
     *      <td>D</td>
     *      <td>One, two or three symbols for the day of year. Important: If the era is not present
     *      then this symbol will simply be mapped to {@link PlainDate#DAY_OF_YEAR}. </td>
     *  </tr>
     *  <tr>
     *      <td>WEEKDAY_IN_MONTH</td>
     *      <td>F</td>
     *      <td>One symbol for the weekday in month. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link EpochDays#MODIFIED_JULIAN_DATE}</td>
     *      <td>g</td>
     *      <td>The count of symbols usually controls the minimum count of
     *      digits of modified julian year, that is the count of days relative
     *      to 1858-11-17. Is only supported by calendrical types like
     *      {@code PlainDate}. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainDate#DAY_OF_WEEK}</td>
     *      <td>E</td>
     *      <td>One to three symbols for the abbreviation, four for the full
     *      name, five for a letter symbol or six for the short form. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link Weekmodel#localDayOfWeek()}</td>
     *      <td>e</td>
     *      <td>Like E, but if there are only one or two symbols then the
     *      formatter will choose the localized numerical form. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link Weekmodel#localDayOfWeek()}</td>
     *      <td>c</td>
     *      <td>Like e, but in the version {@link OutputContext#STANDALONE}.
     *      In some languages (not english) the stand-alone-version requires
     *      a special grammar. However, 2 symbols are not allowed. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainTime#AM_PM_OF_DAY}</td>
     *      <td>a</td>
     *      <td>1-3 symbols for the short form, 4 symbols for the full text form
     *      and 5 symbols for the narrow form. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link net.time4j.DayPeriod#fixed()}</td>
     *      <td>b</td>
     *      <td>1-3 symbols for the short form, 4 symbols for the full text form
     *      and 5 symbols for the narrow form. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link net.time4j.DayPeriod#approximate()}</td>
     *      <td>B</td>
     *      <td>1-3 symbols for the short form, 4 symbols for the full text form
     *      and 5 symbols for the narrow form. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainTime#CLOCK_HOUR_OF_AMPM}</td>
     *      <td>h</td>
     *      <td>One or two symbols for the numerical form. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainTime#DIGITAL_HOUR_OF_DAY}</td>
     *      <td>H</td>
     *      <td>One or two symbols for the numerical form. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainTime#DIGITAL_HOUR_OF_AMPM}</td>
     *      <td>K</td>
     *      <td>One or two symbols for the numerical form. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainTime#CLOCK_HOUR_OF_DAY}</td>
     *      <td>k</td>
     *      <td>One or two symbols for the numerical form. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainTime#MINUTE_OF_HOUR}</td>
     *      <td>m</td>
     *      <td>One or two symbols for the numerical form. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainTime#SECOND_OF_MINUTE}</td>
     *      <td>s</td>
     *      <td>One or two symbols for the numerical form. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainTime#NANO_OF_SECOND}</td>
     *      <td>S</td>
     *      <td>The count of symbols (1-9) controls the minimum and maximum
     *      count of digits to be printed. The decimal separation char will
     *      not be printed. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainTime#MILLI_OF_DAY}</td>
     *      <td>A</td>
     *      <td>The count of symbols (1-9) controls the minimum
     *      count of digits to be printed. </td>
     *  </tr>
     *  <tr>
     *      <td>TIMEZONE_NAME</td>
     *      <td>z</td>
     *      <td>1-3 symbols for the abbreviation, 4 symbols for the full
     *      timezone name. The specific non-location format will be used,
     *      for example &quot;Pacific Daylight Time&quot; (PDT) or
     *      &quot;Pacific Standard Time&quot; (PST).  This symbol
     *      can only be applied on the type {@link Moment}.</td>
     *  </tr>
     *  <tr>
     *      <td>TIMEZONE_OFFSET</td>
     *      <td>Z</td>
     *      <td>1-3 symbols =&gt; see xxxx, 4 symbols =&gt; see OOOO,
     *      5 symbols = &gt; see XXXXX.</td>
     *  </tr>
     *  <tr>
     *      <td>LOCALIZED_GMT_OFFSET</td>
     *      <td>O</td>
     *      <td>One symbol for the abbreviation or 4 symbols for the long
     *      variant. The GMT-prefix can be suppressed by help of the format
     *      attribute {@link Attributes#NO_GMT_PREFIX}. See also
     *      {@link ChronoFormatter.Builder#addLongLocalizedOffset()}
     *      or its short counter part. </td>
     *  </tr>
     *  <tr>
     *      <td>TIMEZONE_ID</td>
     *      <td>V</td>
     *      <td>The count of pattern symbols must always be 2. This symbol
     *      can only be applied on the type {@link Moment}. </td>
     *  </tr>
     *  <tr>
     *      <td>ISO_TIMEZONE_OFFSET</td>
     *      <td>X</td>
     *      <td>One symbol: &#x00B1;HH[mm], two symbols: &#x00B1;HHmm, three
     *      symbols: &#x00B1;HH:mm, four symbols: &#x00B1;HHmm[ss[.{fraction}]],
     *      five symbols: &#x00B1;HH:mm[:ss[.{fraction}]]. If the timezone
     *      offset is equal to {@code 0} then the letter &quot;Z&quot; will
     *      be used. </td>
     *  </tr>
     *  <tr>
     *      <td>ISO_TIMEZONE_OFFSET</td>
     *      <td>x</td>
     *      <td>Like X but without the special char &quot;Z&quot; if the
     *      timezone offset is equal to {@code 0}. </td>
     *  </tr>
     * </table>
     * </div>
     *
     * <p>Special notes for the Ethiopian calendar: </p>
     *
     * <p>The Ethiopian year will use the Ethiopic numerals in Amharic. This default behaviour can be overridden
     * on builder-level. And the clock time (symbol &quot;h&quot;) will use the Ethiopian time starting
     * at 6 AM in the morning. </p>
     *
     * <p><strong>Warning: </strong> CLDR-like date patterns are only applicable when the calendar in question
     * always contains a month for any date. This underlying assumption is not true for example for the
     * French revolutionary calendar. </p>
     */
    /*[deutsch]
     * <p>Dieses Standardmuster ist auf viele Chronologien anwendbar und folgt der Norm
     * <a href="http://www.unicode.org/reports/tr35/tr35-dates.html">LDML</a>
     * des Unicode-Konsortiums. </p>
     *
     * <p>Wenn nicht explizit anders angegeben, steuert die Anzahl der Symbole
     * immer die minimale Anzahl der zu formatierenden Stellen, ein numerisches
     * Element vorausgesetzt. Ist also ein Element in der Darstellung
     * k&uuml;rzer, dann wird mit der Null-Ziffer aufgef&uuml;llt. </p>
     *
     * <p>Nicht-ISO-Chronologien werden auch unterst&uuml;tzt, wenn ihre Elemente CLDR-Symbole definieren. </p>
     *
     * <div style="margin-top:5px;">
     * <table border="1">
     *  <caption>Legende</caption>
     *  <tr>
     *      <th>Element</th>
     *      <th>Symbol</th>
     *      <th>Beschreibung</th>
     *  </tr>
     *  <tr>
     *      <td>{@link ChronoHistory#era() ERA}</td>
     *      <td>G</td>
     *      <td>Ein bis drei Symbole implizieren eine Abk&uuml;rzung, vier
     *      Symbole die Langform und f&uuml;nf Symbole stehen f&uuml;r ein
     *      Buchstabensymbol. Die &Auml;ra basiert auf dem Ausdruck
     *      {@code ChronoHistory.of(format-locale)}. </td>
     *  </tr>
     *  <tr>
     *      <td>YEAR_OF_ERA</td>
     *      <td>y</td>
     *      <td>Die Anzahl der Symbole regelt normalerweise die minimale
     *      Ziffernzahl. Ist sie jedoch 2, dann wird das Jahr zweistellig
     *      angezeigt - mit dem Attribut {@link Attributes#PIVOT_YEAR}.
     *      Wichtig: Ist die &Auml;ra nicht im Muster vorhanden, wird dieses
     *      Symbol dem Element {@link PlainDate#YEAR} zugeordnet. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainDate#YEAR_OF_WEEKDATE}</td>
     *      <td>Y</td>
     *      <td>Entspricht dem Jahr in einem ISO-Wochendatum und verh&auml;lt
     *      sich in der Formatierung wie das Kalenderjahr. Das wochenbasierte
     *      Jahr kann im Wochenmodell des ISO-8601-Standards vom Kalenderjahr
     *      abweichen, weil es an den Wochenzyklus gebunden ist. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainDate#YEAR}</td>
     *      <td>u</td>
     *      <td>Proleptisches ISO-Kalenderjahr. Diese Jahresangabe erfolgt
     *      nie mit Kippjahr, auch nicht f&uuml;r &quot;uu&quot;. Ein
     *      positives Vorzeichen wird genau dann ausgegeben, wenn das Jahr
     *      mehr Stellen hat als an Symbolen vorgegeben. Im Kontrast zum
     *      Symbol y kann dieses Jahr niemals das historische Jahr einer
     *      &Auml;ra sein. </td>
     *  </tr>
     *  <tr>
     *      <td>RELATED_GREGORIAN_YEAR</td>
     *      <td>r</td>
     *      <td>In nicht-gregorianischen Kalendersystemen entspricht es dem
     *      ISO-Jahr des Beginns des jeweiligen Kalenderjahres. F&uuml;r ISO-Kalender
     *      ist es identisch mit dem proleptischen ISO-Kalenderjahr. Zur Formatierung
     *      werden immer die ASCII-Ziffern 0-9 verwendet, selbst wenn andere Teile
     *      des Formats andere numerische Ziffern oder sogar andere Numeralsystem
     *      verwenden. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainDate#QUARTER_OF_YEAR}</td>
     *      <td>Q</td>
     *      <td>Ein oder zwei Symbole f&uuml;r die numerische Form, drei
     *      f&uuml;r die Abk&uuml;rzung, vier f&uuml;r den vollen Namen
     *      oder f&uuml;nf f&uuml;r ein Buchstabensymbol (NARROW).
     *  </tr>
     *  <tr>
     *      <td>{@link PlainDate#QUARTER_OF_YEAR}</td>
     *      <td>q</td>
     *      <td>Wie Q, aber in der {@link OutputContext#STANDALONE
     *      Stand-Alone-Version}. In manchen Sprachen (nicht englisch)
     *      erfordert die alleinstehende Variante eine besondere
     *      Deklination. </td>
     *  </tr>
     *  <tr>
     *      <td>MONTH_OF_YEAR</td>
     *      <td>M</td>
     *      <td>Ein oder zwei Symbole f&uuml;r die numerische Form, drei
     *      f&uuml;r die Abk&uuml;rzung, vier f&uuml;r den vollen Namen
     *      oder f&uuml;nf f&uuml;r ein Buchstabensymbol (NARROW).
     *      Wichtig: Ist die &Auml;ra nicht im Muster vorhanden, wird dieses
     *      Symbol dem Element {@link PlainDate#MONTH_OF_YEAR} zugeordnet. </td>
     *  </tr>
     *  <tr>
     *      <td>MONTH_OF_YEAR</td>
     *      <td>L</td>
     *      <td>Wie M, aber in der {@link OutputContext#STANDALONE
     *      Stand-Alone-Version}. In manchen Sprachen (nicht englisch)
     *      erfordert die alleinstehende Variante eine besondere
     *      Deklination. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link Weekmodel#weekOfYear()}</td>
     *      <td>w</td>
     *      <td>Ein oder zwei Symbole f&uuml;r die l&auml;nderabh&auml;ngige
     *      Woche des Jahres. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link Weekmodel#weekOfMonth()}</td>
     *      <td>W</td>
     *      <td>Ein Symbol f&uuml;r die l&auml;nderabh&auml;ngige
     *      Woche des Monats. </td>
     *  </tr>
     *  <tr>
     *      <td>DAY_OF_MONTH</td>
     *      <td>d</td>
     *      <td>Ein oder zwei Symbole f&uuml;r den Tag des Monats.
     *      Wichtig: Ist die &Auml;ra nicht im Muster vorhanden, wird dieses
     *      Symbol dem Element {@link PlainDate#DAY_OF_MONTH} zugeordnet.</td>
     *  </tr>
     *  <tr>
     *      <td>DAY_OF_YEAR</td>
     *      <td>D</td>
     *      <td>Ein, zwei oder drei Symbole f&uuml;r den Tag des Jahres.
     *      Wichtig: Ist die &Auml;ra nicht im Muster vorhanden, wird dieses
     *      Symbol dem Element {@link PlainDate#DAY_OF_YEAR} zugeordnet. </td>
     *  </tr>
     *  <tr>
     *      <td>WEEKDAY_IN_MONTH</td>
     *      <td>F</td>
     *      <td>Ein Symbol f&uuml;r den Wochentag im Monat. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link EpochDays#MODIFIED_JULIAN_DATE}</td>
     *      <td>g</td>
     *      <td>Die Anzahl der Symbole regelt wie &uuml;blich die minimale
     *      Anzahl der Stellen des modifizierten julianischen Jahres, also
     *      der Anzahl der Tage seit 1858-11-17. Wird nur von reinen
     *      Datumsklassen wie {@code PlainDate} unterst&uuml;tzt. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainDate#DAY_OF_WEEK}</td>
     *      <td>E</td>
     *      <td>Ein bis drei Symbole f&uuml;r die Abk&uuml;rzung, vier
     *      f&uuml;r den vollen Namen, f&uuml;nf f&uuml;r ein Buchstabensymbol
     *      oder sechs f&uuml;r die Kurzform. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link Weekmodel#localDayOfWeek()}</td>
     *      <td>e</td>
     *      <td>Wie E, aber wenn ein oder zwei Symbole angegeben sind, dann
     *      wird die lokalisierte numerische Form gew&auml;hlt. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link Weekmodel#localDayOfWeek()}</td>
     *      <td>c</td>
     *      <td>Wie e, aber in der {@link OutputContext#STANDALONE
     *      Stand-Alone-Version}. In manchen Sprachen (nicht englisch)
     *      erfordert die alleinstehende Variante eine besondere
     *      Deklination. Zu beachten: 2 Symbole sind nicht erlaubt. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainTime#AM_PM_OF_DAY}</td>
     *      <td>a</td>
     *      <td>Ein- bis drei Symbole f&uuml;r die Kurzform, 4 Symbole f&uuml;r
     *      die volle Textform und f&uuml;nf f&uuml;r die Symbolform. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link net.time4j.DayPeriod#fixed()}</td>
     *      <td>b</td>
     *      <td>Ein- bis drei Symbole f&uuml;r die Kurzform, 4 Symbole f&uuml;r
     *      die volle Textform und f&uuml;nf f&uuml;r die Symbolform. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link net.time4j.DayPeriod#approximate()}</td>
     *      <td>B</td>
     *      <td>Ein- bis drei Symbole f&uuml;r die Kurzform, 4 Symbole f&uuml;r
     *      die volle Textform und f&uuml;nf f&uuml;r die Symbolform. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainTime#CLOCK_HOUR_OF_AMPM}</td>
     *      <td>h</td>
     *      <td>Ein oder zwei Symbole f&uuml;r die numerische Form. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainTime#DIGITAL_HOUR_OF_DAY}</td>
     *      <td>H</td>
     *      <td>Ein oder zwei Symbole f&uuml;r die numerische Form. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainTime#DIGITAL_HOUR_OF_AMPM}</td>
     *      <td>K</td>
     *      <td>Ein oder zwei Symbole f&uuml;r die numerische Form. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainTime#CLOCK_HOUR_OF_DAY}</td>
     *      <td>k</td>
     *      <td>Ein oder zwei Symbole f&uuml;r die numerische Form. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainTime#MINUTE_OF_HOUR}</td>
     *      <td>m</td>
     *      <td>Ein oder zwei Symbole f&uuml;r die numerische Form. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainTime#SECOND_OF_MINUTE}</td>
     *      <td>s</td>
     *      <td>Ein oder zwei Symbole f&uuml;r die numerische Form. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainTime#NANO_OF_SECOND}</td>
     *      <td>S</td>
     *      <td>Die Anzahl der Symbole (1-9) regelt die minimale und maximale
     *      Anzahl der zu formatierenden Ziffern. Das Dezimaltrennzeichen
     *      wird nicht ausgegeben. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainTime#MILLI_OF_DAY}</td>
     *      <td>A</td>
     *      <td>Die Anzahl der Symbole (1-9) regelt die minimale Anzahl der
     *      zu formatierenden Ziffern. </td>
     *  </tr>
     *  <tr>
     *      <td>TIMEZONE_NAME</td>
     *      <td>z</td>
     *      <td>1-3 Symbole f&uuml;r die Kurzform, 4 Symbole f&uuml;r den
     *      langen Zeitzonennamen. Es wird das <i>specific non-location format</i> verwendet,
     *      zum Beispiel &quot;Pacific Daylight Time&quot; (PDT) oder
     *      &quot;Pacific Standard Time&quot; (PST). Dieses Symbol kann
     *      nur auf den Typ {@link Moment} angewandt werden. </td>
     *  </tr>
     *  <tr>
     *      <td>TIMEZONE_OFFSET</td>
     *      <td>Z</td>
     *      <td>1-3 Symbole =&gt; siehe xxxx, 4 Symbole =&gt; siehe OOOO,
     *      5 Symbole = &gt; siehe XXXXX.</td>
     *  </tr>
     *  <tr>
     *      <td>LOCALIZED_GMT_OFFSET</td>
     *      <td>O</td>
     *      <td>Ein Symbol f&uuml;r die Kurzform oder 4 Symbole f&uuml;r die
     *      Langform. Das GMT-Pr&auml;fix kann mit Hilfe des Attributs
     *      {@link Attributes#NO_GMT_PREFIX} unterdr&uuml;ckt werden. Siehe auch
     *      {@link ChronoFormatter.Builder#addLongLocalizedOffset()} oder
     *      sein kurzes Gegenst&uuml;ck. </td>
     *  </tr>
     *  <tr>
     *      <td>TIMEZONE_ID</td>
     *      <td>V</td>
     *      <td>Es werden immer zwei Symbole erwartet. Dieses Symbol kann
     *      nur auf den Typ {@link Moment} angewandt werden. </td>
     *  </tr>
     *  <tr>
     *      <td>ISO_TIMEZONE_OFFSET</td>
     *      <td>X</td>
     *      <td>Ein Symbol: &#x00B1;HH[mm], zwei Symbole: &#x00B1;HHmm, drei
     *      Symbole: &#x00B1;HH:mm, vier Symbole: &#x00B1;HHmm[ss[.{fraction}]],
     *      f&uuml;nf Symbole: &#x00B1;HH:mm[:ss[.{fraction}]]. Ist der
     *      Zeitzonen-Offset gleich {@code 0}, dann wird das Buchstabensymbol
     *      &quot;Z&quot; verwendet. </td>
     *  </tr>
     *  <tr>
     *      <td>ISO_TIMEZONE_OFFSET</td>
     *      <td>x</td>
     *      <td>Wie X, aber ohne das Spezialzeichen &quot;Z&quot;, wenn der
     *      Zeitzonen-Offset gleich {@code 0} ist. </td>
     *  </tr>
     * </table>
     * </div>
     *
     * <p>Anmerkungen f&uuml;r den &auml;thiopischen Kalender: </p>
     *
     * <p>Das &auml;thiopische Jahr wird in Amharic die &auml;thiopischen Numerale verwenden. Dieses
     * Standardverhalten kann auf <i>builder</i>-Ebene &uuml;berschrieben werden. Und die Uhrzeit
     * (Symbol &quot;h&quot;) wird die &auml;thiopische Variante mit 6 Uhr morgens als Tagesbeginn
     * nutzen. </p>
     *
     * <p><strong>Warnung: </strong> CLDR-basierte Datumsmuster sind nur anwendbar, wenn der fragliche
     * Kalender immer zu irgeneinem beliebigen Datum einen Monat enth&auml;lt. Diese implizite Annahme
     * ist zum Beispiel f&uuml;r den franz&ouml;sischen Revolutionskalender nicht immer gegeben. </p>
     */
    CLDR,

    /**
     * <p>Follows the format pattern description of class
     * {@link java.text.SimpleDateFormat}, which is very near, but not
     * exactly the same as CLDR. </p>
     *
     * <p>The permitted count of digits is usually unlimited. Users should treat this setting
     * only as approximation to any real implementation of {@code SimpleDateFormat}. For example,
     * this pattern style is only applicable on ISO-compatible chronologies. Android will define
     * some symbols in a different way. Other deviations from {@link #CLDR}: </p>
     *
     * <div style="margin-top:5px;">
     * <table border="1">
     *  <caption>Legend</caption>
     *  <tr>
     *      <th>Element</th>
     *      <th>Symbol</th>
     *      <th>Description</th>
     *  </tr>
     *  <tr>
     *      <td>ISO_DAY_OF_WEEK</td>
     *      <td>u</td>
     *      <td>Corresponds to the weekday-numbering of ISO-8601-standard
     *      ({@code Weekmodel.ISO.localDayOfWeek()}), that is:
     *      Mo=1, Di=2, Mi=3, Do=4, Fr=5, Sa=6, So=7. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link Weekmodel#boundedWeekOfMonth()}</td>
     *      <td>W</td>
     *      <td>One symbol for the country-dependent week of month. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainTime#MILLI_OF_SECOND}</td>
     *      <td>S</td>
     *      <td>No fractional but only integral display of millisecond. </td>
     *  </tr>
     *  <tr>
     *      <td>QUARTER_OF_YEAR</td>
     *      <td>Q</td>
     *      <td>Not supported (as work-around: use CLDR). </td>
     *  </tr>
     *  <tr>
     *      <td>QUARTER_OF_YEAR</td>
     *      <td>q</td>
     *      <td>Not supported (as work-around: use CLDR). </td>
     *  </tr>
     *  <tr>
     *      <td>RELATED_GREGORIAN_YEAR</td>
     *      <td>r</td>
     *      <td>Not supported (as work-around: use CLDR). </td>
     *  </tr>
     *  <tr>
     *      <td>MODIFIED_JULIAN_DATE</td>
     *      <td>g</td>
     *      <td>Not supported (as work-around: use CLDR). </td>
     *  </tr>
     *  <tr>
     *      <td>{local-day-of-week-number}</td>
     *      <td>e</td>
     *      <td>Not supported (as work-around: use CLDR). </td>
     *  </tr>
     *  <tr>
     *      <td>{local-day-of-week-number}</td>
     *      <td>c</td>
     *      <td>Not supported (as work-around: use CLDR). </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainTime#AM_PM_OF_DAY}</td>
     *      <td>a</td>
     *      <td>Only the short form is supported. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link net.time4j.DayPeriod#fixed()}</td>
     *      <td>b</td>
     *      <td>Not supported (as work-around: use CLDR). </td>
     *  </tr>
     *  <tr>
     *      <td>{@link net.time4j.DayPeriod#approximate()}</td>
     *      <td>B</td>
     *      <td>Not supported (as work-around: use CLDR). </td>
     *  </tr>
     *  <tr>
     *      <td>RFC_822_TIMEZONE_OFFSET</td>
     *      <td>Z</td>
     *      <td>Equivalent to CLDR-xx.</td>
     *  </tr>
     *  <tr>
     *      <td>LOCALIZED_GMT_OFFSET</td>
     *      <td>O</td>
     *      <td>Not supported (as work-around: use CLDR). </td>
     *  </tr>
     *  <tr>
     *      <td>TIMEZONE_ID</td>
     *      <td>V</td>
     *      <td>Not supported (as work-around: use CLDR). </td>
     *  </tr>
     *  <tr>
     *      <td>ISO_TIMEZONE_OFFSET</td>
     *      <td>X</td>
     *      <td>Like in CLDR, but with only three symbols as upper limit. </td>
     *  </tr>
     *  <tr>
     *      <td>ISO_TIMEZONE_OFFSET</td>
     *      <td>x</td>
     *      <td>Not supported (as work-around: use CLDR). </td>
     *  </tr>
     * </table>
     * </div>
     */
    /*[deutsch]
     * <p>Folgt der Formatmusterbeschreibung der Klasse
     * {@link java.text.SimpleDateFormat}, die sich stark, aber
     * nicht exakt an CLDR orientiert. </p>
     *
     * <p>Die erlaubte Anzahl der Symbole ist in der Regel nach oben offen. Anwender sollten
     * diese Einstellung nur als N&auml;herung zu einer realen Implementierung der Klasse
     * {@code SimpleDateFormat} ansehen. Zum Beispiel ist dieser Musterstil nur auf
     * ISO-Chronologien anwendbar. Android wird einige Symbole anders definieren. Andere
     * Unterschiede zu {@link #CLDR}: </p>
     *
     * <div style="margin-top:5px;">
     * <table border="1">
     *  <caption>Legende</caption>
     *  <tr>
     *      <th>Element</th>
     *      <th>Symbol</th>
     *      <th>Beschreibung</th>
     *  </tr>
     *  <tr>
     *      <td>ISO_DAY_OF_WEEK</td>
     *      <td>u</td>
     *      <td>Entspricht der Wochentagsnummerierung des
     *      ISO-8601-Formats ({@code Weekmodel.ISO.localDayOfWeek()}),
     *      also: Mo=1, Di=2, Mi=3, Do=4, Fr=5, Sa=6, So=7. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link Weekmodel#boundedWeekOfMonth()}</td>
     *      <td>W</td>
     *      <td>Ein Symbol f&uuml;r die l&auml;nderabh&auml;ngige
     *      Woche des Monats. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainTime#MILLI_OF_SECOND}</td>
     *      <td>S</td>
     *      <td>Keine fraktionale, sondern nur integrale Darstellung der
     *      Millisekunde. </td>
     *  </tr>
     *  <tr>
     *      <td>QUARTER_OF_YEAR</td>
     *      <td>Q</td>
     *      <td>Unterst&uuml;tzung nicht hier, sondern nur in CLDR. </td>
     *  </tr>
     *  <tr>
     *      <td>QUARTER_OF_YEAR</td>
     *      <td>q</td>
     *      <td>Unterst&uuml;tzung nicht hier, sondern nur in CLDR. </td>
     *  </tr>
     *  <tr>
     *      <td>RELATED_GREGORIAN_YEAR</td>
     *      <td>r</td>
     *      <td>Unterst&uuml;tzung nicht hier, sondern nur in CLDR. </td>
     *  </tr>
     *  <tr>
     *      <td>MODIFIED_JULIAN_DATE</td>
     *      <td>g</td>
     *      <td>Unterst&uuml;tzung nicht hier, sondern nur in CLDR. </td>
     *  </tr>
     *  <tr>
     *      <td>{local-day-of-week-number}</td>
     *      <td>e</td>
     *      <td>Unterst&uuml;tzung nicht hier, sondern nur in CLDR. </td>
     *  </tr>
     *  <tr>
     *      <td>{local-day-of-week-number}</td>
     *      <td>c</td>
     *      <td>Unterst&uuml;tzung nicht hier, sondern nur in CLDR. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainTime#AM_PM_OF_DAY}</td>
     *      <td>a</td>
     *      <td>Nur die Kurzform wird unterst&uuml;tzt. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link net.time4j.DayPeriod#fixed()}</td>
     *      <td>b</td>
     *      <td>Unterst&uuml;tzung nicht hier, sondern nur in CLDR. </td>
     *  </tr>
     *  <tr>
     *      <td>{@link net.time4j.DayPeriod#approximate()}</td>
     *      <td>B</td>
     *      <td>Unterst&uuml;tzung nicht hier, sondern nur in CLDR. </td>
     *  </tr>
     *  <tr>
     *      <td>RFC_822_TIMEZONE_OFFSET</td>
     *      <td>Z</td>
     *      <td>Entspricht CLDR-xx.</td>
     *  </tr>
     *  <tr>
     *      <td>LOCALIZED_GMT_OFFSET</td>
     *      <td>O</td>
     *      <td>Unterst&uuml;tzung nicht hier, sondern nur in CLDR. </td>
     *  </tr>
     *  <tr>
     *      <td>TIMEZONE_ID</td>
     *      <td>V</td>
     *      <td>Unterst&uuml;tzung nicht hier, sondern nur in CLDR. </td>
     *  </tr>
     *  <tr>
     *      <td>ISO_TIMEZONE_OFFSET</td>
     *      <td>X</td>
     *      <td>Wie in CLDR, aber nur mit maximal drei Symbolen. </td>
     *  </tr>
     *  <tr>
     *      <td>ISO_TIMEZONE_OFFSET</td>
     *      <td>x</td>
     *      <td>Unterst&uuml;tzung nicht hier, sondern nur in CLDR. </td>
     *  </tr>
     * </table>
     * </div>
     */
    SIMPLE_DATE_FORMAT,

    /**
     * <p>CLDR-variant with the only difference how the symbol &quot;H&quot; will be interpreted. </p>
     *
     * <p>Deviations from {@link #CLDR}: </p>
     *
     * <div style="margin-top:5px;">
     * <table border="1">
     *  <caption>Legend</caption>
     *  <tr>
     *      <th>Element</th>
     *      <th>Symbol</th>
     *      <th>Description</th>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainTime#HOUR_FROM_0_TO_24}</td>
     *      <td>H</td>
     *      <td>Hour of day - in full range 0-24 (the value 24 is only permitted if
     *      all other time parts are zero). </td>
     *  </tr>
     * </table>
     * </div>
     *
     * @since   3.4/4.3
     */
    /*[deutsch]
     * <p>CLDR-Variante, die das Symbol &quot;H&quot; als volle Stunde im Bereich 0-24 interpretiert. </p>
     *
     * <p>Unterschiede zu {@link #CLDR}: </p>
     *
     * <div style="margin-top:5px;">
     * <table border="1">
     *  <caption>Legende</caption>
     *  <tr>
     *      <th>Element</th>
     *      <th>Symbol</th>
     *      <th>Beschreibung</th>
     *  </tr>
     *  <tr>
     *      <td>{@link PlainTime#HOUR_FROM_0_TO_24}</td>
     *      <td>H</td>
     *      <td>Volle Stunde im Bereich 0-24 (der Wert 24 ist nur dann erlaubt,
     *      wenn alle anderen Uhrzeitanteile {@code 0} sind). </td>
     *  </tr>
     * </table>
     * </div>
     *
     * @since   3.4/4.3
     */
    CLDR_24,

    /**
     * <p>A small subset of CLDR applicable on many calendar chronologies which have registered the
     * associated elements with same symbols. </p>
     *
     * <p>If not explicitly stated otherwise the count of symbols always
     * controls the minimum count of digits in case of a numerical element.
     * Is an element shorter then the zero digit will be used for padding. </p>
     *
     * <div style="margin-top:5px;">
     * <table border="1">
     *  <caption>Legend</caption>
     *  <tr>
     *      <th>Element</th>
     *      <th>Symbol</th>
     *      <th>Description</th>
     *  </tr>
     *  <tr>
     *      <td>ERA</td>
     *      <td>G</td>
     *      <td>One to three symbols indicate an abbreviation, four symbols
     *      indicate the long form and five symbols stand for a letter. </td>
     *  </tr>
     *  <tr>
     *      <td>YEAR_OF_ERA</td>
     *      <td>y</td>
     *      <td>The count of symbols normally controls the minimum count of
     *      digits. If it is 2 however then the year will be printed with
     *      exact two digits using the attribute {@link Attributes#PIVOT_YEAR}. </td>
     *  </tr>
     *  <tr>
     *      <td>MONTH_OF_YEAR</td>
     *      <td>M</td>
     *      <td>One or two symbols for the numerical form, three symbols
     *      for the abbreviation, four for the full name and five for
     *      a letter symbol (NARROW). </td>
     *  </tr>
     *  <tr>
     *      <td>MONTH_OF_YEAR</td>
     *      <td>L</td>
     *      <td>Like M, but in the version {@link OutputContext#STANDALONE}.
     *      In some languages (not english) the stand-alone-version requires
     *      a special grammar. </td>
     *  </tr>
     *  <tr>
     *      <td>WEEK_OF_YEAR</td>
     *      <td>w</td>
     *      <td>One or two symbols for the country-dependent week of year. </td>
     *  </tr>
     *  <tr>
     *      <td>WEEK_OF_MONTH</td>
     *      <td>W</td>
     *      <td>One symbol for the country-dependent week of month. </td>
     *  </tr>
     *  <tr>
     *      <td>DAY_OF_MONTH</td>
     *      <td>d</td>
     *      <td>One or two symbols for the day of month. </td>
     *  </tr>
     *  <tr>
     *      <td>DAY_OF_YEAR</td>
     *      <td>D</td>
     *      <td>One, two or three symbols for the day of year. </td>
     *  </tr>
     *  <tr>
     *      <td>DAY_OF_WEEK</td>
     *      <td>E</td>
     *      <td>One to three symbols for the abbreviation, four for the full
     *      name, five for a letter symbol or six for the short form. </td>
     *  </tr>
     *  <tr>
     *      <td>WEEKDAY_IN_MONTH</td>
     *      <td>F</td>
     *      <td>One symbol for the weekday in month. </td>
     *  </tr>
     *  <tr>
     *      <td>LOCAL_DAY_OF_WEEK</td>
     *      <td>e</td>
     *      <td>Like E, but if there are only one or two symbols then the
     *      formatter will choose the localized numerical form. </td>
     *  </tr>
     *  <tr>
     *      <td>LOCAL_DAY_OF_WEEK</td>
     *      <td>c</td>
     *      <td>Like e, but in the version {@link OutputContext#STANDALONE}.
     *      In some languages (not english) the stand-alone-version requires
     *      a special grammar. However, 2 symbols are not allowed. </td>
     *  </tr>
     *  <tr>
     *      <td>RELATED_GREGORIAN_YEAR</td>
     *      <td>r</td>
     *      <td>The related gregorian year corresponds to the begin of the calendar year
     *      in non-gregorian calender systems. For formatting or parsing, only the
     *      ASCII-digits 0-9 will be used, even if other parts of the format use
     *      alternative digits or other numeral systems. The count of symbols is
     *      defined within the range 1-9. </td>
     *  </tr>
     * </table>
     * </div>
     *
     * <p>Special notes for the Ethiopian calendar: </p>
     *
     * <p>The Ethiopian year will use the Ethiopic numerals in Amharic. This default behaviour
     * can be overridden on builder-level using a sectional attribute for the number system. </p>
     *
     * <p><strong>Warning: </strong> CLDR-like date patterns are only applicable when the calendar in question
     * always contains a month for any date. This underlying assumption is not true for example for the
     * French revolutionary calendar. </p>
     *
     * @since   3.33/4.28
     */
    /*[deutsch]
     * <p>Eine kleine Untermenge von CLDR, die auf viele Kalenderchronologien anwendbar ist, die die
     * assoziierten Elemente mit gleichen Symbolen registriert haben. </p>
     *
     * <p>Wenn nicht explizit anders angegeben, steuert die Anzahl der Symbole
     * immer die minimale Anzahl der zu formatierenden Stellen, ein numerisches
     * Element vorausgesetzt. Ist also ein Element in der Darstellung
     * k&uuml;rzer, dann wird mit der Null-Ziffer aufgef&uuml;llt. </p>
     *
     * <div style="margin-top:5px;">
     * <table border="1">
     *  <caption>Legende</caption>
     *  <tr>
     *      <th>Element</th>
     *      <th>Symbol</th>
     *      <th>Beschreibung</th>
     *  </tr>
     *  <tr>
     *      <td>ERA</td>
     *      <td>G</td>
     *      <td>Ein bis drei Symbole implizieren eine Abk&uuml;rzung, vier
     *      Symbole die Langform und f&uuml;nf Symbole stehen f&uuml;r ein
     *      Buchstabensymbol. </td>
     *  </tr>
     *  <tr>
     *      <td>YEAR_OF_ERA</td>
     *      <td>y</td>
     *      <td>Die Anzahl der Symbole regelt normalerweise die minimale
     *      Ziffernzahl. Ist sie jedoch 2, dann wird das Jahr zweistellig
     *      angezeigt - mit dem Attribut {@link Attributes#PIVOT_YEAR}. </td>
     *  </tr>
     *  <tr>
     *      <td>MONTH_OF_YEAR</td>
     *      <td>M</td>
     *      <td>Ein oder zwei Symbole f&uuml;r die numerische Form, drei
     *      f&uuml;r die Abk&uuml;rzung, vier f&uuml;r den vollen Namen
     *      oder f&uuml;nf f&uuml;r ein Buchstabensymbol (NARROW). </td>
     *  </tr>
     *  <tr>
     *      <td>MONTH_OF_YEAR</td>
     *      <td>L</td>
     *      <td>Wie M, aber in der {@link OutputContext#STANDALONE
     *      Stand-Alone-Version}. In manchen Sprachen (nicht englisch)
     *      erfordert die alleinstehende Variante eine besondere
     *      Deklination. </td>
     *  </tr>
     *  <tr>
     *      <td>WEEK_OF_YEAR</td>
     *      <td>w</td>
     *      <td>Ein oder zwei Symbole f&uuml;r die l&auml;nderabh&auml;ngige
     *      Woche des Jahres. </td>
     *  </tr>
     *  <tr>
     *      <td>WEEK_OF_MONTH</td>
     *      <td>W</td>
     *      <td>Ein Symbol f&uuml;r die l&auml;nderabh&auml;ngige
     *      Woche des Monats. </td>
     *  </tr>
     *  <tr>
     *      <td>DAY_OF_MONTH</td>
     *      <td>d</td>
     *      <td>Ein oder zwei Symbole f&uuml;r den Tag des Monats. </td>
     *  </tr>
     *  <tr>
     *      <td>DAY_OF_YEAR</td>
     *      <td>D</td>
     *      <td>Ein, zwei oder drei Symbole f&uuml;r den Tag des Jahres. </td>
     *  </tr>
     *  <tr>
     *      <td>DAY_OF_WEEK</td>
     *      <td>E</td>
     *      <td>Ein bis drei Symbole f&uuml;r die Abk&uuml;rzung, vier
     *      f&uuml;r den vollen Namen, f&uuml;nf f&uuml;r ein Buchstabensymbol
     *      oder sechs f&uuml;r die Kurzform. </td>
     *  </tr>
     *  <tr>
     *      <td>WEEKDAY_IN_MONTH</td>
     *      <td>F</td>
     *      <td>Ein Symbol f&uuml;r den Wochentag im Monat. </td>
     *  </tr>
     *  <tr>
     *      <td>LOCAL_DAY_OF_WEEK</td>
     *      <td>e</td>
     *      <td>Wie E, aber wenn ein oder zwei Symbole angegeben sind, dann
     *      wird die lokalisierte numerische Form gew&auml;hlt. </td>
     *  </tr>
     *  <tr>
     *      <td>LOCAL_DAY_OF_WEEK</td>
     *      <td>c</td>
     *      <td>Wie e, aber in der {@link OutputContext#STANDALONE
     *      Stand-Alone-Version}. In manchen Sprachen (nicht englisch)
     *      erfordert die alleinstehende Variante eine besondere
     *      Deklination. Zu beachten: 2 Symbole sind nicht erlaubt. </td>
     *  </tr>
     *  <tr>
     *      <td>RELATED_GREGORIAN_YEAR</td>
     *      <td>r</td>
     *      <td>In nicht-gregorianischen Kalendersystemen entspricht es dem
     *      ISO-Jahr des Beginns des jeweiligen Kalenderjahres. Zur Formatierung
     *      werden immer die ASCII-Ziffern 0-9 verwendet, selbst wenn andere Teile
     *      des Formats andere numerische Ziffern oder sogar andere Numeralsystem
     *      verwenden. Die Anzahl der Symbole ist im Bereich 1-9 zu w&auml;hlen. </td>
     *  </tr>
     * </table>
     * </div>
     *
     * <p>Anmerkungen f&uuml;r den &auml;thiopischen Kalender: </p>
     *
     * <p>Das &auml;thiopische Jahr wird per Standard in Amharic die &auml;thiopischen Numerale verwenden.
     * Diese Vorgabe kann auf <i>builder</i>-Ebene mit einem sektionalen Attribut f&uuml;r das Zahlsystem
     * &uuml;berschrieben werden. </p>
     *
     * <p><strong>Warnung: </strong> CLDR-basierte Datumsmuster sind nur anwendbar, wenn der fragliche
     * Kalender immer zu irgeneinem beliebigen Datum einen Monat enth&auml;lt. Diese implizite Annahme
     * ist zum Beispiel f&uuml;r den franz&ouml;sischen Revolutionskalender nicht immer gegeben. </p>
     *
     * @since   3.33/4.28
     */
    CLDR_DATE,

    /**
     * <p>A small subset of CLDR applicable on any non-ISO-chronology which has registered the
     * associated elements with same symbols. </p>
     *
     * <p>If not explicitly stated otherwise the count of symbols always
     * controls the minimum count of digits in case of a numerical element.
     * Is an element shorter then the zero digit will be used for padding. </p>
     *
     * <div style="margin-top:5px;">
     * <table border="1">
     *  <caption>Legend</caption>
     *  <tr>
     *      <th>Element</th>
     *      <th>Symbol</th>
     *      <th>Description</th>
     *  </tr>
     *  <tr>
     *      <td>ERA</td>
     *      <td>G</td>
     *      <td>One to three symbols indicate an abbreviation, four symbols
     *      indicate the long form and five symbols stand for a letter. </td>
     *  </tr>
     *  <tr>
     *      <td>YEAR_OF_ERA</td>
     *      <td>y</td>
     *      <td>The count of symbols normally controls the minimum count of
     *      digits. If it is 2 however then the year will be printed with
     *      exact two digits using the attribute {@link Attributes#PIVOT_YEAR}. </td>
     *  </tr>
     *  <tr>
     *      <td>MONTH_OF_YEAR</td>
     *      <td>M</td>
     *      <td>One or two symbols for the numerical form, three symbols
     *      for the abbreviation, four for the full name and five for
     *      a letter symbol (NARROW). </td>
     *  </tr>
     *  <tr>
     *      <td>MONTH_OF_YEAR</td>
     *      <td>L</td>
     *      <td>Like M, but in the version {@link OutputContext#STANDALONE}.
     *      In some languages (not english) the stand-alone-version requires
     *      a special grammar. </td>
     *  </tr>
     *  <tr>
     *      <td>WEEK_OF_YEAR</td>
     *      <td>w</td>
     *      <td>One or two symbols for the country-dependent week of year. </td>
     *  </tr>
     *  <tr>
     *      <td>WEEK_OF_MONTH</td>
     *      <td>W</td>
     *      <td>One symbol for the country-dependent week of month. </td>
     *  </tr>
     *  <tr>
     *      <td>DAY_OF_MONTH</td>
     *      <td>d</td>
     *      <td>One or two symbols for the day of month. </td>
     *  </tr>
     *  <tr>
     *      <td>DAY_OF_YEAR</td>
     *      <td>D</td>
     *      <td>One, two or three symbols for the day of year. </td>
     *  </tr>
     *  <tr>
     *      <td>DAY_OF_WEEK</td>
     *      <td>E</td>
     *      <td>One to three symbols for the abbreviation, four for the full
     *      name, five for a letter symbol or six for the short form. </td>
     *  </tr>
     *  <tr>
     *      <td>WEEKDAY_IN_MONTH</td>
     *      <td>F</td>
     *      <td>One symbol for the weekday in month. </td>
     *  </tr>
     *  <tr>
     *      <td>LOCAL_DAY_OF_WEEK</td>
     *      <td>e</td>
     *      <td>Like E, but if there are only one or two symbols then the
     *      formatter will choose the localized numerical form. </td>
     *  </tr>
     *  <tr>
     *      <td>LOCAL_DAY_OF_WEEK</td>
     *      <td>c</td>
     *      <td>Like e, but in the version {@link OutputContext#STANDALONE}.
     *      In some languages (not english) the stand-alone-version requires
     *      a special grammar. However, 2 symbols are not allowed. </td>
     *  </tr>
     *  <tr>
     *      <td>RELATED_GREGORIAN_YEAR</td>
     *      <td>r</td>
     *      <td>The related gregorian year corresponds to the begin of the calendar year
     *      in non-gregorian calender systems. For formatting or parsing, only the
     *      ASCII-digits 0-9 will be used, even if other parts of the format use
     *      alternative digits or other numeral systems. The count of symbols is
     *      defined within the range 1-9. </td>
     *  </tr>
     * </table>
     * </div>
     *
     * <p>Special notes for the Ethiopian calendar: </p>
     *
     * <p>The Ethiopian year will use the Ethiopic numerals in Amharic. This default behaviour
     * can be overridden on builder-level. </p>
     *
     * @since       3.5/4.3
     * @deprecated  Use {@link #CLDR_DATE} as replacement because the name of this pattern type can be
     *              confusing considering the fact that not all calendars can be handled by this pattern type
     *              (for example, the {@code FrenchRepublicanCalendar} is not suitable here)
     */
    /*[deutsch]
     * <p>Eine kleine Untermenge von CLDR, die auf jede Non-ISO-Chronologie anwendbar ist, die die
     * assoziierten Elemente mit gleichen Symbolen registriert hat. </p>
     *
     * <p>Wenn nicht explizit anders angegeben, steuert die Anzahl der Symbole
     * immer die minimale Anzahl der zu formatierenden Stellen, ein numerisches
     * Element vorausgesetzt. Ist also ein Element in der Darstellung
     * k&uuml;rzer, dann wird mit der Null-Ziffer aufgef&uuml;llt. </p>
     *
     * <div style="margin-top:5px;">
     * <table border="1">
     *  <caption>Legende</caption>
     *  <tr>
     *      <th>Element</th>
     *      <th>Symbol</th>
     *      <th>Beschreibung</th>
     *  </tr>
     *  <tr>
     *      <td>ERA</td>
     *      <td>G</td>
     *      <td>Ein bis drei Symbole implizieren eine Abk&uuml;rzung, vier
     *      Symbole die Langform und f&uuml;nf Symbole stehen f&uuml;r ein
     *      Buchstabensymbol. </td>
     *  </tr>
     *  <tr>
     *      <td>YEAR_OF_ERA</td>
     *      <td>y</td>
     *      <td>Die Anzahl der Symbole regelt normalerweise die minimale
     *      Ziffernzahl. Ist sie jedoch 2, dann wird das Jahr zweistellig
     *      angezeigt - mit dem Attribut {@link Attributes#PIVOT_YEAR}. </td>
     *  </tr>
     *  <tr>
     *      <td>MONTH_OF_YEAR</td>
     *      <td>M</td>
     *      <td>Ein oder zwei Symbole f&uuml;r die numerische Form, drei
     *      f&uuml;r die Abk&uuml;rzung, vier f&uuml;r den vollen Namen
     *      oder f&uuml;nf f&uuml;r ein Buchstabensymbol (NARROW). </td>
     *  </tr>
     *  <tr>
     *      <td>MONTH_OF_YEAR</td>
     *      <td>L</td>
     *      <td>Wie M, aber in der {@link OutputContext#STANDALONE
     *      Stand-Alone-Version}. In manchen Sprachen (nicht englisch)
     *      erfordert die alleinstehende Variante eine besondere
     *      Deklination. </td>
     *  </tr>
     *  <tr>
     *      <td>WEEK_OF_YEAR</td>
     *      <td>w</td>
     *      <td>Ein oder zwei Symbole f&uuml;r die l&auml;nderabh&auml;ngige
     *      Woche des Jahres. </td>
     *  </tr>
     *  <tr>
     *      <td>WEEK_OF_MONTH</td>
     *      <td>W</td>
     *      <td>Ein Symbol f&uuml;r die l&auml;nderabh&auml;ngige
     *      Woche des Monats. </td>
     *  </tr>
     *  <tr>
     *      <td>DAY_OF_MONTH</td>
     *      <td>d</td>
     *      <td>Ein oder zwei Symbole f&uuml;r den Tag des Monats. </td>
     *  </tr>
     *  <tr>
     *      <td>DAY_OF_YEAR</td>
     *      <td>D</td>
     *      <td>Ein, zwei oder drei Symbole f&uuml;r den Tag des Jahres. </td>
     *  </tr>
     *  <tr>
     *      <td>DAY_OF_WEEK</td>
     *      <td>E</td>
     *      <td>Ein bis drei Symbole f&uuml;r die Abk&uuml;rzung, vier
     *      f&uuml;r den vollen Namen, f&uuml;nf f&uuml;r ein Buchstabensymbol
     *      oder sechs f&uuml;r die Kurzform. </td>
     *  </tr>
     *  <tr>
     *      <td>WEEKDAY_IN_MONTH</td>
     *      <td>F</td>
     *      <td>Ein Symbol f&uuml;r den Wochentag im Monat. </td>
     *  </tr>
     *  <tr>
     *      <td>LOCAL_DAY_OF_WEEK</td>
     *      <td>e</td>
     *      <td>Wie E, aber wenn ein oder zwei Symbole angegeben sind, dann
     *      wird die lokalisierte numerische Form gew&auml;hlt. </td>
     *  </tr>
     *  <tr>
     *      <td>LOCAL_DAY_OF_WEEK</td>
     *      <td>c</td>
     *      <td>Wie e, aber in der {@link OutputContext#STANDALONE
     *      Stand-Alone-Version}. In manchen Sprachen (nicht englisch)
     *      erfordert die alleinstehende Variante eine besondere
     *      Deklination. Zu beachten: 2 Symbole sind nicht erlaubt. </td>
     *  </tr>
     *  <tr>
     *      <td>RELATED_GREGORIAN_YEAR</td>
     *      <td>r</td>
     *      <td>In nicht-gregorianischen Kalendersystemen entspricht es dem
     *      ISO-Jahr des Beginns des jeweiligen Kalenderjahres. Zur Formatierung
     *      werden immer die ASCII-Ziffern 0-9 verwendet, selbst wenn andere Teile
     *      des Formats andere numerische Ziffern oder sogar andere Numeralsystem
     *      verwenden. Die Anzahl der Symbole ist im Bereich 1-9 zu w&auml;hlen. </td>
     *  </tr>
     * </table>
     * </div>
     *
     * <p>Anmerkungen f&uuml;r den &auml;thiopischen Kalender: </p>
     *
     * <p>Das &auml;thiopische Jahr wird per Standard in Amharic die &auml;thiopischen Numerale verwenden.
     * Diese Vorgabe kann auf <i>builder</i>-Ebene &uuml;berschrieben werden. </p>
     *
     * @since       3.5/4.3
     * @deprecated  Use {@link #CLDR_DATE} as replacement because the name of this pattern type can be
     *              confusing considering the fact that not all calendars can be handled by this pattern type
     *              (for example, the {@code FrenchRepublicanCalendar} is not suitable here)
     */
    @Deprecated
    NON_ISO_DATE,

    /**
     * <p>Resolves a pattern such that the chronology used in current context determines the meaning
     * of any pattern symbols. </p>
     *
     * <p>In contrast to other pattern types like {@code CLDR}, the meaning of pattern symbols is not
     * defined by any external standard. The elements associated with symbols are looked up among the registered
     * elements of a chronology including those which can be found via any chronological extension. If the
     * found element is a {@link TextElement text element} then it will be treated as such, and the count of
     * symbols determines the text width (1 = NARROW, 2 = SHORT, 3 = ABBREVIATED, 4 = WIDE). Otherwise
     * this pattern type tries to resolve the element in question as {@code ChronoElement<Integer>}, and
     * the count of symbols will determine the min width of displayed/parsed digits and apply some padding
     * if necessary. The maximum width is always 9, and no sign is used. </p>
     *
     * @see    ChronoElement#getSymbol()
     * @since   3.33/4.20
     */
    /*[deutsch]
     * <p>L&ouml;st ein Muster so auf, da&szlig; die im aktuellen Kontext benutzte Chronologie die
     * Bedeutung von Mustersymbolen festlegt. </p>
     *
     * <p>Im Kontrast zu anderen Mustertypen wie {@code CLDR} ist die Bedeutung von Mustersymbolen nicht
     * durch irgendeinen externen Standard festgelegt. Die mit den Symbolen verkn&uuml;pften Elemente
     * werden unter den registrierten Elementen einer Chronologie gesucht, notfalls auch in den chronologischen
     * Erweiterungen. Falls das gefundene Element ein {@link TextElement} ist, wird es als solches behandelt,
     * und die Anzahl der Symbole bestimmt dann die Textbreite (1 = NARROW, 2 = SHORT, 3 = ABBREVIATED, 4 = WIDE).
     * Sonst versucht dieser Mustertyp das fragliche Element als {@code ChronoElement<Integer>} aufzul&ouml;sen,
     * und die Anzahl der Symbole wird die Mindestbreite der angezeigten/interpretierten Ziffern festlegen,
     * unter Umst&auml;nden auch mit Auff&uuml;llen von F&uuml;llzeichen. Im numerischen Fall ist die maximale
     * Breite 9, und ein Vorzeichen wird nie verwendet. </p>
     *
     * @see    ChronoElement#getSymbol()
     * @since   3.33/4.20
     */
    DYNAMIC;

    //~ Methoden ----------------------------------------------------------

    @Override
    public FormatEngine<PatternType> getFormatEngine() {

        return UltimateFormatEngine.INSTANCE;

    }

    /**
     * <p>Registers a format symbol. </p>
     *
     * @param   builder     serves for construction of {@code ChronoFormatter}
     * @param   locale      current language- and country setting
     * @param   symbol      pattern symbol to be interpreted
     * @param   count       count of symbols in format pattern
     * @return  map of elements which will replace other already registered
     *          elements after pattern processing
     * @throws  IllegalArgumentException if symbol resolution fails
     */
    /*[deutsch]
     * <p>Registriert ein Formatsymbol. </p>
     *
     * @param   builder     serves for construction of {@code ChronoFormatter}
     * @param   locale      current language- and country setting
     * @param   symbol      pattern symbol to be interpreted
     * @param   count       count of symbols in format pattern
     * @return  map of elements which will replace other already registered
     *          elements after pattern processing
     * @throws  IllegalArgumentException if symbol resolution fails
     */
    Map<ChronoElement<?>, ChronoElement<?>> registerSymbol(
        ChronoFormatter.Builder<?> builder,
        Locale locale,
        char symbol,
        int count
    ) {

        Chronology<?> chronology = getEffectiveChronology(builder);

        switch (this) {
            case CLDR:
                return cldr(builder, locale, symbol, count);
            case SIMPLE_DATE_FORMAT:
                return sdf(builder, chronology, locale, symbol, count);
            case CLDR_24:
                return cldr24(builder, locale, symbol, count);
            case NON_ISO_DATE:
                if (isISO(chronology)) {
                    throw new IllegalArgumentException("Choose CLDR or CLDR_DATE for ISO-8601-chronology.");
                }
                return general(builder, chronology, symbol, count, locale);
            case CLDR_DATE:
                Class<?> type = chronology.getChronoType();
                if (Calendrical.class.isAssignableFrom(type) || CalendarVariant.class.isAssignableFrom(type)) {
                    return general(builder, chronology, symbol, count, locale);
                } else {
                    throw new IllegalArgumentException("No calendar chronology.");
                }
            case DYNAMIC:
                return dynamic(builder, symbol, count, locale);
            default:
                throw new UnsupportedOperationException(this.name());
        }

    }

    private static boolean isGeneralSymbol(char symbol) {

        switch (symbol) {
            case 'D':
            case 'E':
            case 'G':
            case 'L':
            case 'M':
            case 'd':
            case 'y':
            case 'r':
            case 'w':
            case 'W':
            case 'e':
            case 'c':
            case 'F':
                return true;
            default:
                return false;
        }

    }

    private static boolean isISO(Chronology<?> chronology) {

        return getCalendarType(chronology).equals(CalendarText.ISO_CALENDAR_TYPE);

    }

    private static String getCalendarType(Chronology<?> chronology) {

        CalendarType ctype = chronology.getChronoType().getAnnotation(CalendarType.class);
        return ((ctype == null) ? CalendarText.ISO_CALENDAR_TYPE : ctype.value());

    }

    private static ChronoElement<Integer> findEthiopianHour(Chronology<?> chronology) {

        for (ChronoExtension ext : chronology.getExtensions()) {
            for (ChronoElement<?> e : ext.getElements(Locale.ROOT, Attributes.empty())) {
                if (e.name().equals("ETHIOPIAN_HOUR")) {
                    return cast(e);
                }
            }
        }

        return null;

    }

    private static Chronology<?> getEffectiveChronology(ChronoFormatter.Builder<?> builder) {

        Chronology<?> chronology = builder.getChronology();

        while (chronology instanceof BridgeChronology) {
            chronology = chronology.preparser();
        }

        return chronology;

    }

    private Map<ChronoElement<?>, ChronoElement<?>> cldr(
        ChronoFormatter.Builder<?> builder,
        Locale locale,
        char symbol,
        int count
    ) {

        Chronology<?> chronology = getEffectiveChronology(builder);

        if (isGeneralSymbol(symbol) && !isISO(chronology)) {
            return this.general(builder, chronology, symbol, count, locale);
        } else if ((symbol == 'h') && getCalendarType(chronology).equals("ethiopic")) {
            ChronoElement<Integer> ethioHour = findEthiopianHour(chronology);
            if (ethioHour == null) {
                throw new IllegalArgumentException("Ethiopian time not available.");
            }
            addNumber(ethioHour, symbol, builder, count, false);
            return Collections.emptyMap();
        } else {
            return this.cldrISO(builder, chronology, locale, symbol, count, false);
        }

    }

    private Map<ChronoElement<?>, ChronoElement<?>> cldrISO(
        ChronoFormatter.Builder<?> builder,
        Chronology<?> chronology,
        Locale locale,
        char symbol,
        int count,
        boolean sdf
    ) {

        TextWidth width;

        switch (symbol) {
            case 'G':
                if (count <= 3) {
                    width = TextWidth.ABBREVIATED;
                } else if ((count == 4) || sdf) {
                    width = TextWidth.WIDE;
                } else if (count == 5) {
                    width = TextWidth.NARROW;
                } else {
                    throw new IllegalArgumentException(
                        "Too many pattern letters (G): " + count);
                }
                builder.startSection(Attributes.TEXT_WIDTH, width);
                ChronoHistory history = ChronoHistory.of(locale);
                TextElement<?> eraElement = TextElement.class.cast(history.era());
                builder.addText(eraElement);
                builder.endSection();
                Map<ChronoElement<?>, ChronoElement<?>> replacement =
                    new HashMap<ChronoElement<?>, ChronoElement<?>>();
                replacement.put(PlainDate.YEAR, history.yearOfEra());
                replacement.put(PlainDate.MONTH_OF_YEAR, history.month());
                replacement.put(PlainDate.MONTH_AS_NUMBER, history.month());
                replacement.put(PlainDate.DAY_OF_MONTH, history.dayOfMonth());
                replacement.put(PlainDate.DAY_OF_YEAR, history.dayOfYear());
                return replacement;
            case 'y':
                if (count == 2) {
                    builder.addTwoDigitYear(PlainDate.YEAR);
                } else {
                    builder.addYear(PlainDate.YEAR, count, false);
                }
                break;
            case 'Y':
                if (count == 2) {
                    builder.addTwoDigitYear(PlainDate.YEAR_OF_WEEKDATE);
                } else {
                    builder.addYear(PlainDate.YEAR_OF_WEEKDATE, count, false);
                }
                break;
            case 'u':
                builder.addYear(PlainDate.YEAR, count, true);
                break;
            case 'r':
                builder.startSection(Attributes.NUMBER_SYSTEM, NumberSystem.ARABIC);
                builder.startSection(Attributes.ZERO_DIGIT, '0');
                builder.addYear(PlainDate.YEAR, count, true);
                builder.endSection();
                builder.endSection();
                break;
            case 'Q':
                addQuarterOfYear(builder, count);
                break;
            case 'q':
                builder.startSection(
                    Attributes.OUTPUT_CONTEXT, OutputContext.STANDALONE);
                try {
                    addQuarterOfYear(builder, count);
                } finally {
                    builder.endSection();
                }
                break;
            case 'M':
                addMonth(builder, Math.min(count, sdf ? 4 : count));
                break;
            case 'L':
                builder.startSection(
                    Attributes.OUTPUT_CONTEXT, OutputContext.STANDALONE);
                try {
                    addMonth(builder, count);
                } finally {
                    builder.endSection();
                }
                break;
            case 'w':
                if (count <= 2) {
                    addNumber(Weekmodel.of(locale).weekOfYear(), symbol, builder, count, sdf);
                } else {
                    throw new IllegalArgumentException(
                        "Too many pattern letters (w): " + count);
                }
                break;
            case 'W':
                if (count == 1) {
                    builder.addFixedInteger(Weekmodel.of(locale).weekOfMonth(), 1);
                } else {
                    throw new IllegalArgumentException(
                        "Too many pattern letters (W): " + count);
                }
                break;
            case 'd':
                addNumber(PlainDate.DAY_OF_MONTH, symbol, builder, count, sdf);
                break;
            case 'D':
                if (count < 3) {
                    builder.addInteger(PlainDate.DAY_OF_YEAR, count, 3);
                } else if ((count == 3) || sdf) {
                    builder.addFixedInteger(PlainDate.DAY_OF_YEAR, count);
                } else {
                    throw new IllegalArgumentException(
                        "Too many pattern letters (D): " + count);
                }
                break;
            case 'F':
                if ((count == 1) || sdf) {
                    builder.addFixedInteger(PlainDate.WEEKDAY_IN_MONTH, count);
                } else {
                    throw new IllegalArgumentException(
                        "Too many pattern letters (F): " + count);
                }
                break;
            case 'g':
                builder.addLongNumber(
                    EpochDays.MODIFIED_JULIAN_DATE,
                    count,
                    18,
                    SignPolicy.SHOW_WHEN_NEGATIVE);
                break;
            case 'E':
                if (count <= 3) {
                    width = TextWidth.ABBREVIATED;
                } else if ((count == 4) || sdf) {
                    width = TextWidth.WIDE;
                } else if (count == 5) {
                    width = TextWidth.NARROW;
                } else if (count == 6) {
                    width = TextWidth.SHORT;
                } else {
                    throw new IllegalArgumentException(
                        "Too many pattern letters (E): " + count);
                }
                builder.startSection(Attributes.TEXT_WIDTH, width);
                builder.addText(PlainDate.DAY_OF_WEEK);
                builder.endSection();
                break;
            case 'e':
                if (count <= 2) {
                    builder.addFixedNumerical(
                        Weekmodel.of(locale).localDayOfWeek(), count);
                } else {
                    cldrISO(builder, chronology, locale, 'E', count, sdf);
                }
                break;
            case 'c':
                if (count == 2) {
                    throw new IllegalArgumentException(
                        "Invalid pattern count of 2 for symbol 'c'.");
                }
                builder.startSection(
                    Attributes.OUTPUT_CONTEXT, OutputContext.STANDALONE);
                try {
                    if (count == 1) {
                        builder.addFixedNumerical(
                            Weekmodel.of(locale).localDayOfWeek(), 1);
                    } else {
                        cldrISO(builder, chronology, locale, 'E', count, sdf);
                    }
                } finally {
                    builder.endSection();
                }
                break;
            case 'a':
                width = (sdf ? TextWidth.ABBREVIATED : getPeriodWidth(count));
                builder.startSection(Attributes.TEXT_WIDTH, width);
                builder.addText(PlainTime.AM_PM_OF_DAY);
                builder.endSection();
                if (getCalendarType(chronology).equals("ethiopic")) {
                    // AM/PM-marker denotes western reference!
                    ChronoElement<Integer> ethioHour = findEthiopianHour(chronology);
                    if (ethioHour == null) {
                        throw new IllegalArgumentException("Ethiopian time not available.");
                    }
                    Map<ChronoElement<?>, ChronoElement<?>> stdHours =
                        new HashMap<ChronoElement<?>, ChronoElement<?>>();
                    stdHours.put(ethioHour, PlainTime.CLOCK_HOUR_OF_AMPM);
                    return stdHours;
                }
                break;
            case 'b':
                width = getPeriodWidth(count);
                builder.startSection(Attributes.TEXT_WIDTH, width);
                builder.addDayPeriodFixed();
                builder.endSection();
                break;
            case 'B':
                width = getPeriodWidth(count);
                builder.startSection(Attributes.TEXT_WIDTH, width);
                builder.addDayPeriodApproximate();
                builder.endSection();
                break;
            case 'h':
                addNumber(PlainTime.CLOCK_HOUR_OF_AMPM, symbol, builder, count, sdf);
                break;
            case 'H':
                addNumber(PlainTime.DIGITAL_HOUR_OF_DAY, symbol, builder, count, sdf);
                break;
            case 'K':
                addNumber(PlainTime.DIGITAL_HOUR_OF_AMPM, symbol, builder, count, sdf);
                break;
            case 'k':
                addNumber(PlainTime.CLOCK_HOUR_OF_DAY, symbol, builder, count, sdf);
                break;
            case 'm':
                addNumber(PlainTime.MINUTE_OF_HOUR, symbol, builder, count, sdf);
                break;
            case 's':
                addNumber(PlainTime.SECOND_OF_MINUTE, symbol, builder, count, sdf);
                break;
            case 'S':
                builder.addFraction(
                    PlainTime.NANO_OF_SECOND, count, count, false);
                break;
            case 'A':
                builder.addInteger(PlainTime.MILLI_OF_DAY, count, 9);
                break;
            case 'z':
                try {
                    if (count < 4) {
                        builder.addShortTimezoneName();
                    } else if ((count == 4) || sdf) {
                        builder.addLongTimezoneName();
                    } else {
                        throw new IllegalArgumentException(
                            "Too many pattern letters (z): " + count);
                    }
                } catch (IllegalStateException ise) {
                    throw new IllegalArgumentException(ise.getMessage());
                }
                break;
            case 'Z':
                if (count < 4) {
                    builder.addTimezoneOffset(
                        DisplayMode.LONG,
                        false,
                        Collections.singletonList("+0000"));
                } else if (count == 4) {
                    builder.addLongLocalizedOffset();
                } else if (count == 5) {
                    builder.addTimezoneOffset(
                        DisplayMode.LONG,
                        true,
                        Collections.singletonList("Z"));
                } else {
                    throw new IllegalArgumentException(
                        "Too many pattern letters (Z): " + count);
                }
                break;
            case 'O':
                if (count == 1) {
                    builder.addShortLocalizedOffset();
                } else if (count == 4) {
                    builder.addLongLocalizedOffset();
                } else {
                    throw new IllegalArgumentException(
                        "Count of pattern letters is not 1 or 4: " + count);
                }
                break;
            case 'V':
                if (count == 2) {
                    try {
                        builder.addTimezoneID();
                    } catch (IllegalStateException ise) {
                        throw new IllegalArgumentException(ise.getMessage());
                    }
                } else {
                    throw new IllegalArgumentException(
                        "Count of pattern letters is not 2: " + count);
                }
                break;
            case 'X':
                addOffset(builder, symbol, count, true);
                break;
            case 'x':
                addOffset(builder, symbol, count, false);
                break;
            default:
                throw new IllegalArgumentException(
                    "Unsupported pattern symbol: " + symbol);
        }

        return Collections.emptyMap();

    }

    private static TextWidth getPeriodWidth(int count) {

        if (count <= 3) {
            return TextWidth.ABBREVIATED;
        } else if (count == 4) {
            return TextWidth.WIDE;
        } else if (count == 5) {
            return TextWidth.NARROW;
        } else {
            throw new IllegalArgumentException(
                "Too many pattern letters: " + count);
        }

    }

    private Map<ChronoElement<?>, ChronoElement<?>> sdf(
        ChronoFormatter.Builder<?> builder,
        Chronology<?> chronology,
        Locale locale,
        char symbol,
        int count
    ) {

        switch (symbol) {
            case 'W':
                builder.addFixedInteger(
                    Weekmodel.of(locale).boundedWeekOfMonth(),
                    count);
                break;
            case 'u':
                builder.addFixedNumerical(PlainDate.DAY_OF_WEEK, count);
                break;
            case 'S':
                builder.addFixedInteger(PlainTime.MILLI_OF_SECOND, count);
                break;
            case 'Z':
                addOffset(builder, symbol, 2, false);
                break;
            case 'b':
            case 'B':
            case 'Q':
            case 'q':
            case 'r':
            case 'g':
            case 'e':
            case 'c':
            case 'O':
            case 'V':
            case 'x':
                throw new IllegalArgumentException(
                    "CLDR pattern symbol not supported"
                    + " in SimpleDateFormat-style: "
                    + symbol);
            case 'X':
                if (count >= 4) {
                    throw new IllegalArgumentException(
                        "Too many pattern letters (X): " + count);
                }
                return cldrISO(builder, chronology, locale, 'X', count, true);
            default:
                return cldrISO(builder, chronology, locale, symbol, count, true);
        }

        return Collections.emptyMap();

    }

    private Map<ChronoElement<?>, ChronoElement<?>> cldr24(
        ChronoFormatter.Builder<?> builder,
        Locale locale,
        char symbol,
        int count
    ) {

        if (symbol == 'H') {
            addNumber(PlainTime.HOUR_FROM_0_TO_24, symbol, builder, count, false);
            return Collections.emptyMap();
        }

        return cldr(builder, locale, symbol, count);

    }

    private static void addOffset(
        ChronoFormatter.Builder<?> builder,
        char symbol,
        int count,
        boolean zulu
    ) {

        switch (count) {
            case 1:
                builder.addTimezoneOffset(
                    DisplayMode.SHORT,
                    false,
                    Collections.singletonList(zulu ? "Z" : "+00"));
                break;
            case 2:
                builder.addTimezoneOffset(
                    DisplayMode.MEDIUM,
                    false,
                    Collections.singletonList(zulu ? "Z" : "+0000"));
                break;
            case 3:
                builder.addTimezoneOffset(
                    DisplayMode.MEDIUM,
                    true,
                    Collections.singletonList(zulu ? "Z" : "+00:00"));
                break;
            case 4:
                builder.addTimezoneOffset(
                    DisplayMode.LONG,
                    false,
                    Collections.singletonList(zulu ? "Z" : "+0000"));
                break;
            case 5:
                builder.addTimezoneOffset(
                    DisplayMode.LONG,
                    true,
                    Collections.singletonList(zulu ? "Z" : "+00:00"));
                break;
            default:
                throw new IllegalArgumentException(
                    "Too many pattern letters (" + symbol + "): " + count);
        }

    }

    private static void addNumber(
        ChronoElement<Integer> element,
        char symbol,
        ChronoFormatter.Builder<?> builder,
        int count,
        boolean sdf
    ) {

        if (count == 1) {
            builder.addInteger(element, 1, 2);
        } else if ((count == 2) || sdf) {
            builder.addFixedInteger(element, count);
        } else {
            throw new IllegalArgumentException(
                "Too many pattern letters (" + symbol + "): " + count);
        }

    }

    private static void addMonth(
        ChronoFormatter.Builder<?> builder,
        int count
    ) {

        switch (count) {
            case 1:
                builder.addInteger(PlainDate.MONTH_AS_NUMBER, 1, 2);
                break;
            case 2:
                builder.addFixedInteger(PlainDate.MONTH_AS_NUMBER, 2);
                break;
            case 3:
                builder.startSection(
                    Attributes.TEXT_WIDTH, TextWidth.ABBREVIATED);
                builder.addText(PlainDate.MONTH_OF_YEAR);
                builder.endSection();
                break;
            case 4:
                builder.startSection(
                    Attributes.TEXT_WIDTH, TextWidth.WIDE);
                builder.addText(PlainDate.MONTH_OF_YEAR);
                builder.endSection();
                break;
            case 5:
                builder.startSection(
                    Attributes.TEXT_WIDTH, TextWidth.NARROW);
                builder.addText(PlainDate.MONTH_OF_YEAR);
                builder.endSection();
                break;
            default:
                throw new IllegalArgumentException(
                    "Too many pattern letters for month: " + count);
        }

    }

    private static void addQuarterOfYear(
        ChronoFormatter.Builder<?> builder,
        int count
    ) {

        switch (count) {
            case 1:
            case 2:
                builder.addFixedNumerical(PlainDate.QUARTER_OF_YEAR, count);
                break;
            case 3:
                builder.startSection(
                    Attributes.TEXT_WIDTH, TextWidth.ABBREVIATED);
                builder.addText(PlainDate.QUARTER_OF_YEAR);
                builder.endSection();
                break;
            case 4:
                builder.startSection(
                    Attributes.TEXT_WIDTH, TextWidth.WIDE);
                builder.addText(PlainDate.QUARTER_OF_YEAR);
                builder.endSection();
                break;
            case 5:
                builder.startSection(
                    Attributes.TEXT_WIDTH, TextWidth.NARROW);
                builder.addText(PlainDate.QUARTER_OF_YEAR);
                builder.endSection();
                break;
            default:
                throw new IllegalArgumentException(
                    "Too many pattern letters for quarter-of-year: " + count);
        }

    }

    private Map<ChronoElement<?>, ChronoElement<?>> general(
        ChronoFormatter.Builder<?> builder,
        Chronology<?> chronology,
        char symbol,
        int count,
        Locale locale
    ) {

        Set<ChronoElement<?>> elements = getElements(chronology, symbol, locale);
        String chronoType = builder.getChronology().getChronoType().getName();
        ChronoElement<?> element = find(elements, symbol, chronoType);
        TextElement<?> textElement;
        ChronoElement<Integer> intElement;

        if (Integer.class.isAssignableFrom(element.getType())) {
            textElement = null;
            if (element instanceof DualFormatElement) {
                textElement = cast(element);
            }
            intElement = cast(element);
        } else if (element instanceof TextElement) {
            textElement = cast(element);
            intElement = null;
        } else {
            throw new IllegalStateException("Implementation error: " + element + " in \"" + chronoType + "\"");
        }

        switch (symbol) {
            case 'G':
                TextWidth eraWidth;
                if (count <= 3) {
                    eraWidth = TextWidth.ABBREVIATED;
                } else if (count == 4) {
                    eraWidth = TextWidth.WIDE;
                } else if (count == 5) {
                    eraWidth = TextWidth.NARROW;
                } else {
                    throw new IllegalArgumentException(
                        "Too many pattern letters (G): " + count);
                }
                builder.startSection(Attributes.TEXT_WIDTH, eraWidth);
                builder.addText(textElement);
                builder.endSection();
                break;
            case 'y':
                boolean hasSpecialAttribute = false;
                if (locale.getLanguage().equals("am") && getCalendarType(chronology).equals("ethiopic")) {
                    hasSpecialAttribute = true;
                    builder.startSection(Attributes.NUMBER_SYSTEM, NumberSystem.ETHIOPIC);
                }
                if (count == 2) {
                    builder.addTwoDigitYear(intElement);
                } else {
                    builder.addYear(intElement, count, false);
                }
                if (hasSpecialAttribute) {
                    builder.endSection();
                }
                break;
            case 'r':
                builder.startSection(Attributes.NUMBER_SYSTEM, NumberSystem.ARABIC);
                builder.startSection(Attributes.ZERO_DIGIT, '0');
                builder.addYear(intElement, count, true);
                builder.endSection();
                builder.endSection();
                break;
            case 'M':
                addMonth(builder, Math.min(count, count), textElement);
                break;
            case 'L':
                builder.startSection(
                    Attributes.OUTPUT_CONTEXT, OutputContext.STANDALONE);
                try {
                    addMonth(builder, count, textElement);
                } finally {
                    builder.endSection();
                }
                break;
            case 'w':
                addNumber(intElement, symbol, builder, count, false);
                break;
            case 'W':
                if (count == 1) {
                    builder.addFixedInteger(intElement, 1);
                } else {
                    throw new IllegalArgumentException(
                        "Too many pattern letters (W): " + count);
                }
                break;
            case 'd':
                addNumber(intElement, symbol, builder, count, false);
                break;
            case 'D':
                if (count < 3) {
                    builder.addInteger(intElement, count, 3);
                } else if (count == 3) {
                    builder.addFixedInteger(intElement, count);
                } else {
                    throw new IllegalArgumentException(
                        "Too many pattern letters (D): " + count);
                }
                break;
            case 'E':
                TextWidth width;
                if (count <= 3) {
                    width = TextWidth.ABBREVIATED;
                } else if (count == 4) {
                    width = TextWidth.WIDE;
                } else if (count == 5) {
                    width = TextWidth.NARROW;
                } else if (count == 6) {
                    width = TextWidth.SHORT;
                } else {
                    throw new IllegalArgumentException(
                        "Too many pattern letters (E): " + count);
                }
                builder.startSection(Attributes.TEXT_WIDTH, width);
                builder.addText(textElement);
                builder.endSection();
                break;
            case 'e':
                if (count <= 2) {
                    ChronoElement<Weekday> wde = cast(element);
                    builder.addFixedNumerical(wde, count);
                } else {
                    general(builder, chronology, 'E', count, locale);
                }
                break;
            case 'c':
                if (count == 2) {
                    throw new IllegalArgumentException(
                        "Invalid pattern count of 2 for symbol 'c'.");
                }
                builder.startSection(
                    Attributes.OUTPUT_CONTEXT, OutputContext.STANDALONE);
                try {
                    if (count == 1) {
                        ChronoElement<Weekday> wde = cast(element);
                        builder.addFixedNumerical(wde, 1);
                    } else {
                        general(builder, chronology, 'E', count, locale);
                    }
                } finally {
                    builder.endSection();
                }
                break;
            case 'F':
                if (count == 1) {
                    builder.addFixedInteger(intElement, count);
                } else {
                    throw new IllegalArgumentException(
                        "Too many pattern letters (F): " + count);
                }
                break;
            default:
                throw new IllegalArgumentException(
                    "Unsupported pattern symbol: " + symbol);
        }

        return Collections.emptyMap();

    }

    private Map<ChronoElement<?>, ChronoElement<?>> dynamic(
        ChronoFormatter.Builder<?> builder,
        char symbol,
        int count,
        Locale locale
    ) {

        ChronoElement<?> found = null;
        Chronology<?> chronology = getEffectiveChronology(builder);

        for (ChronoElement<?> element : chronology.getRegisteredElements()) {
            if (element.getSymbol() == symbol) {
                found = element;
                break;
            }
        }

        if (found == null) {
            for (ChronoExtension extension : chronology.getExtensions()) {
                for (ChronoElement<?> element : extension.getElements(locale, Attributes.empty())) {
                    if (element.getSymbol() == symbol) {
                        found = element;
                        break;
                    }
                }
                if (found != null) {
                    break;
                }
            }
        }

        if (found == null) {
            throw new IllegalArgumentException("Cannot resolve symbol: " + symbol);
        } else if (found instanceof TextElement) {
            switch (count) {
                case 1:
                    builder.startSection(Attributes.TEXT_WIDTH, TextWidth.NARROW);
                    break;
                case 2:
                    builder.startSection(Attributes.TEXT_WIDTH, TextWidth.SHORT);
                    break;
                case 3:
                    builder.startSection(Attributes.TEXT_WIDTH, TextWidth.ABBREVIATED);
                    break;
                case 4:
                    builder.startSection(Attributes.TEXT_WIDTH, TextWidth.WIDE);
                    break;
                default:
                    throw new IllegalArgumentException("Illegal count of symbols: " + symbol);
            }
            TextElement<?> textElement = cast(found);
            builder.addText(textElement);
            builder.endSection();
        } else if (found.getType() == Integer.class) {
            ChronoElement<Integer> intElement = cast(found);
            builder.addInteger(intElement, count, 9);
        } else {
            throw new IllegalArgumentException("Can only handle integer or text elements: " + found);
        }

        return Collections.emptyMap();

    }

    private static Set<ChronoElement<?>> getElements(
        Chronology<?> chronology,
        char symbol,
        Locale locale
    ) {

        if ((symbol == 'w') || (symbol == 'W') || (symbol == 'e') || (symbol == 'c')) {
            for (ChronoExtension extension : chronology.getExtensions()) {
                for (ChronoElement<?> element : extension.getElements(locale, Attributes.empty())) {
                    if (
                        ((symbol == 'e' || symbol == 'c') && element.name().equals("LOCAL_DAY_OF_WEEK"))
                        || ((symbol == 'w') && element.name().equals("WEEK_OF_YEAR"))
                        || ((symbol == 'W') && element.name().equals("WEEK_OF_MONTH"))
                    ) {
                        Set<ChronoElement<?>> result = new HashSet<ChronoElement<?>>();
                        result.add(element);
                        return result;
                    }
                }
            }
            return Collections.emptySet();
        } else {
            return chronology.getRegisteredElements();
        }

    }

    private static ChronoElement<?> find(
        Set<ChronoElement<?>> elements,
        char symbol,
        String chronoType
    ) {

        char c = ((symbol == 'L') ? 'M' : ((symbol == 'c') ? 'e' : symbol));

        for (ChronoElement<?> element : elements) {
            if (element.isDateElement() && (element.getSymbol() == c)) {
                return element;
            }
        }

        throw new IllegalArgumentException(
            "Cannot find any chronological date element for symbol " + symbol + " in \"" + chronoType + "\".");

    }

    private static <V extends Enum<V>> void addMonth(
        ChronoFormatter.Builder<?> builder,
        int count,
        TextElement<?> textElement
    ) {

        switch (count) {
            case 1:
            case 2:
                if (Enum.class.isAssignableFrom(textElement.getType())) {
                    ChronoElement<V> enumElement = cast(textElement);
                    if (count == 1) {
                        builder.addNumerical(enumElement, 1, 2);
                    } else if (count == 2) {
                        builder.addFixedNumerical(enumElement, 2);
                    }
                } else {
                    builder.startSection(
                        DualFormatElement.COUNT_OF_PATTERN_SYMBOLS, Integer.valueOf(count));
                    builder.addText(textElement);
                    builder.endSection();
                }
                break;
            case 3:
                builder.startSection(
                    Attributes.TEXT_WIDTH, TextWidth.ABBREVIATED);
                builder.addText(textElement);
                builder.endSection();
                break;
            case 4:
                builder.startSection(
                    Attributes.TEXT_WIDTH, TextWidth.WIDE);
                builder.addText(textElement);
                builder.endSection();
                break;
            case 5:
                builder.startSection(
                    Attributes.TEXT_WIDTH, TextWidth.NARROW);
                builder.addText(textElement);
                builder.endSection();
                break;
            default:
                throw new IllegalArgumentException(
                    "Too many pattern letters for month: " + count);
        }

    }

    @SuppressWarnings("unchecked")
    private static <T> T cast(Object obj) {

        return (T) obj;

    }

}
