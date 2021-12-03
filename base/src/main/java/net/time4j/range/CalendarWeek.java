/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CalendarWeek.java) is part of project Time4J.
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

package net.time4j.range;

import net.time4j.CalendarUnit;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.SystemClock;
import net.time4j.Weekcycle;
import net.time4j.Weekday;
import net.time4j.Weekmodel;
import net.time4j.base.GregorianDate;
import net.time4j.base.GregorianMath;
import net.time4j.base.MathUtils;
import net.time4j.base.TimeSource;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoMerger;
import net.time4j.engine.Chronology;
import net.time4j.engine.EpochDays;
import net.time4j.engine.FormattableElement;
import net.time4j.engine.IntElementRule;
import net.time4j.engine.TimeLine;
import net.time4j.engine.ValidationElement;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.CalendarType;
import net.time4j.format.Leniency;
import net.time4j.format.LocalizedPatternSupport;
import net.time4j.format.NumberType;
import net.time4j.format.PluralCategory;
import net.time4j.format.PluralRules;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import net.time4j.tz.Timezone;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.text.ParseException;
import java.time.format.FormatStyle;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;


/**
 * <p>Represents the calendar week starting on Monday according to ISO-8601-paper. </p>
 *
 * <p>The elements registered by this class are: </p>
 *
 * <ul>
 *  <li>{@link #YEAR_OF_WEEKDATE}</li>
 *  <li>{@link #WEEK_OF_YEAR}</li>
 * </ul>
 *
 * <p>This class offers localized formatting, also style-based. However, the week model is not localized
 * and fixed to ISO-8601. Example for using the German abbreviation KW for a calendar week: </p>
 *
 * <pre>
 *     ChronoFormatter&lt;CalendarWeek&gt; f =
 *          ChronoFormatter.setUp(CalendarWeek.chronology(), Locale.GERMAN)
 *          .addPattern(&quot;w. &#39;KW&#39;&quot;, PatternType.CLDR).build();
 *     System.out.println(f.format(CalendarWeek.of(2016, 4)); // 4. KW
 * </pre>
 *
 * <p>Note: The current calendar week can be determined by an expression like:
 * {@link #nowInSystemTime()} or in a more general way
 * {@code CalendarWeek current = SystemClock.inLocalView().now(CalendarWeek.chronology())}. </p>
 *
 * @author  Meno Hochschild
 * @since   3.21/4.17
 */
/*[deutsch]
 * <p>Repr&auml;sentiert die Kalenderwoche entsprechend der ISO-8601-Norm. </p>
 *
 * <p>Die von dieser Klasse registrierten Elemente sind: </p>
 *
 * <ul>
 *  <li>{@link #YEAR_OF_WEEKDATE}</li>
 *  <li>{@link #WEEK_OF_YEAR}</li>
 * </ul>
 *
 * <p>Diese Klasse bietet lokalisierte Formatierungsoptionen an, auch stilbasiert. Allerdings ist das
 * Wochenmodell auf ISO-8601 fest eingestellt. Beispiel mit der deutschen Abk&uuml;rzung &quot;KW&quot;: </p>
 *
 * <pre>
 *     ChronoFormatter&lt;CalendarWeek&gt; f =
 *          ChronoFormatter.setUp(CalendarWeek.chronology(), Locale.GERMAN)
 *          .addPattern(&quot;w. &#39;KW&#39;&quot;, PatternType.CLDR).build();
 *     System.out.println(f.format(CalendarWeek.of(2016, 4)); // 4. KW
 * </pre>
 *
 * <p>Hinweis: Die aktuelle Kalenderwoche kann mit einem Ausdruck wie folgt bestimmt werden:
 * {@link #nowInSystemTime()} oder allgemeiner
 * {@code CalendarWeek current = SystemClock.inLocalView().now(CalendarWeek.chronology())}. </p>
 *
 * @author  Meno Hochschild
 * @since   3.21/4.17
 */
@CalendarType("iso8601")
public final class CalendarWeek
    extends FixedCalendarInterval<CalendarWeek>
    implements LocalizedPatternSupport {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Defines an element for the week-based year in an
     * ISO-8601-weekdate. </p>
     *
     * <p>The week-based year is usually the same as the calendar year.
     * However, at the begin or end of a calendar year the situation is
     * different because the first week of the weekdate can start after
     * New Year and the last week of the weekdate can end before the last
     * day of the calendar year. Examples: </p>
     *
     * <ul><li>Sunday, [1995-01-01] =&gt; [1994-W52-7]</li>
     * <li>Tuesday, [1996-31-12] =&gt; [1997-W01-2]</li></ul>
     */
    /*[deutsch]
     * <p>Definiert ein Element f&uuml;r das wochenbasierte Jahr in einem
     * ISO-Wochendatum. </p>
     *
     * <p>Das wochenbasierte Jahr stimmt in der Regel mit dem
     * Kalenderjahr &uuml;berein. Ausnahmen sind der Beginn und
     * das Ende des Kalenderjahres, weil die erste Woche des Jahres
     * erst nach Neujahr anfangen und die letzte Woche des Jahres
     * bereits vor Sylvester enden kann. Beispiele: </p>
     *
     * <ul><li>Sonntag, [1995-01-01] =&gt; [1994-W52-7]</li>
     * <li>Dienstag, [1996-31-12] =&gt; [1997-W01-2]</li></ul>
     */
    @FormattableElement(format = "Y")
    public static final ChronoElement<Integer> YEAR_OF_WEEKDATE = PlainDate.YEAR_OF_WEEKDATE;

    /**
     * <p>Element with the week of year in the value range {@code 1-52/53}. </p>
     *
     * <p>The calendar week always starts on Monday. The first week which has at least four days within current
     * calendar year is considered as the first week of year. </p>
     *
     * @see     Weekmodel#ISO
     * @see     Weekmodel#weekOfYear()
     */
    /*[deutsch]
     * <p>Element mit der Kalenderwoche des Jahres (Wertebereich {@code 1-52/53}). </p>
     *
     * <p>Die Kalenderwoche startet immer am Montag. Als erste Kalenderwoche des Jahres gilt die, die innerhalb
     * des aktuellen Kalenderjahres wenigstens vier Tage hat. </p>
     *
     * @see     Weekmodel#ISO
     * @see     Weekmodel#weekOfYear()
     */
    @FormattableElement(format = "w")
    public static final ChronoElement<Integer> WEEK_OF_YEAR = Weekmodel.ISO.weekOfYear();

    private static final Chronology<CalendarWeek> ENGINE =
        Chronology.Builder
            .setUp(CalendarWeek.class, new Merger())
            .appendElement(YEAR_OF_WEEKDATE, new YearRule())
            .appendElement(WEEK_OF_YEAR, new WeekRule())
            .build();

    private static final ChronoFormatter<CalendarWeek> PARSER =
        ChronoFormatter.setUp(CalendarWeek.chronology(), Locale.ROOT)
            .addPattern("YYYY[-]'W'ww", PatternType.CLDR).build();

    private static final long serialVersionUID = -3948942660009645060L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final int year;
    private transient final int week;
    private transient final Boundary<PlainDate> start;
    private transient final Boundary<PlainDate> end;
    private transient final int lastWeek;

    //~ Konstruktoren -----------------------------------------------------

    private CalendarWeek(
        int year,
        int week
    ) {
        super();

        int wmax = maximumOfWeek(year);

        if ((year < GregorianMath.MIN_YEAR) || (year > GregorianMath.MAX_YEAR)) {
            throw new IllegalArgumentException("Year out of bounds: " + year);
        } else if ((week < 1) || (week > wmax)) {
            throw new IllegalArgumentException("Week-of-year out of bounds: " + week);
        }

        this.year = year;
        this.week = week;
        this.lastWeek = wmax;

        PlainDate date = PlainDate.of(this.year, week, Weekday.MONDAY);
        this.start = Boundary.ofClosed(date);
        this.end = (
            (year == GregorianMath.MAX_YEAR)
                ? Boundary.ofClosed(PlainDate.axis().getMaximum())
                : Boundary.ofClosed(date.plus(6, CalendarUnit.DAYS)));

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new instance based on given week-based year and week-of-year
     * according to ISO-8601. </p>
     *
     * <p>In order to create the last calendar week of given year, it is recommended to use following
     * expression: </p>
     *
     * <pre>
     *     CalendarWeek first = CalendarWeek.of(2016, 1);
     *     CalendarWeek last = first.withLastWeekOfYear();
     * </pre>
     *
     * @param   yearOfWeekdate  week-based year within range {@code -999,999,999 / +999,999,999}
     * @param   weekOfYear      week of year based on ISO-8601 in range {@code 1-52/53}
     * @return  new instance
     * @throws  IllegalArgumentException if any argument is out of range
     * @see     #isValid(int, int)
     * @see     #withLastWeekOfYear()
     */
    /*[deutsch]
     * <p>Erzeugt eine neue Instanz mit dem angegebenen wochenbasierten Jahr und der ISO-Kalenderwoche. </p>
     *
     * <p>Um die letzte Kalenderwoche des angegebenen Jahres zu erhalten, wird empfohlen, folgenden
     * Ausdruck zu verwenden: </p>
     *
     * <pre>
     *     CalendarWeek first = CalendarWeek.of(2016, 1);
     *     CalendarWeek last = first.withLastWeekOfYear();
     * </pre>
     *
     * @param   yearOfWeekdate  week-based year within range {@code -999,999,999 / +999,999,999}
     * @param   weekOfYear      week of year based on ISO-8601 in range {@code 1-52/53}
     * @return  new instance
     * @throws  IllegalArgumentException if any argument is out of range
     * @see     #isValid(int, int)
     * @see     #withLastWeekOfYear()
     */
    public static CalendarWeek of(
        int yearOfWeekdate,
        int weekOfYear
    ) {

        return new CalendarWeek(yearOfWeekdate, weekOfYear);

    }

    /**
     * <p>Obtains the current calendar week in system time. </p>
     *
     * <p>Convenient short-cut for: {@code SystemClock.inLocalView().now(CalendarWeek.chronology())}. </p>
     *
     * @return  current calendar week in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(net.time4j.engine.Chronology)
     * @since   3.24/4.20
     */
    /*[deutsch]
     * <p>Ermittelt die aktuelle Kalenderwoche in der Systemzeit. </p>
     *
     * <p>Bequeme Abk&uuml;rzung f&uuml;r: {@code SystemClock.inLocalView().now(CalendarWeek.chronology())}. </p>
     *
     * @return  current calendar week in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(net.time4j.engine.Chronology)
     * @since   3.24/4.20
     */
    public static CalendarWeek nowInSystemTime() {

        return SystemClock.inLocalView().now(CalendarWeek.chronology());

    }

    /**
     * <p>Combines this calendar week with given day of week to a calendar date. </p>
     *
     * @param   dayOfWeek       day of week in range MONDAY - SUNDAY
     * @return  calendar date
     * @throws  IllegalArgumentException if the result is beyond the maximum of date axis (exotic edge case)
     */
    /*[deutsch]
     * <p>Kombiniert diese Kalenderwoche mit dem angegebenen Wochentag zu einem Kalenderdatum. </p>
     *
     * @param   dayOfWeek       day of week in range MONDAY - SUNDAY
     * @return  calendar date
     * @throws  IllegalArgumentException if the result is beyond the maximum of date axis (exotic edge case)
     */
    public PlainDate at(Weekday dayOfWeek) {

        if (dayOfWeek == Weekday.MONDAY) {
            return this.start.getTemporal(); // short cut
        }

        return PlainDate.of(this.year, this.week, dayOfWeek);

    }

    /**
     * <p>Yields the year number. </p>
     *
     * @return  int
     * @see     #YEAR_OF_WEEKDATE
     */
    /*[deutsch]
     * <p>Liefert die Jahreszahl. </p>
     *
     * @return  int
     * @see     #YEAR_OF_WEEKDATE
     */
    public int getYear() {

        return this.year;

    }

    /**
     * <p>Yields the number of calendar week. </p>
     *
     * <p>Note: The last week of year can be obtained by the expression {@code getMaximum(WEEK_OF_YEAR)}. </p>
     *
     * @return  int
     * @see     #WEEK_OF_YEAR
     * @see     ChronoEntity#getMaximum(ChronoElement)
     * @see     #withLastWeekOfYear()
     */
    /*[deutsch]
     * <p>Liefert die Kalenderwoche. </p>
     *
     * <p>Hinweis: Die letzte Kalenderwoche des Jahres kann mit Hilfe von {@code getMaximum(WEEK_OF_YEAR)}
     * ermittelt werden. </p>
     *
     * @return  int
     * @see     #WEEK_OF_YEAR
     * @see     ChronoEntity#getMaximum(ChronoElement)
     * @see     #withLastWeekOfYear()
     */
    public int getWeek() {

        return this.week;

    }

    @Override
    public Boundary<PlainDate> getStart() {

        return this.start;

    }

    @Override
    public Boundary<PlainDate> getEnd() {

        return this.end;

    }

    @Override
    public boolean contains(PlainDate temporal) {

        return (!temporal.isBefore(this.start.getTemporal())) && !temporal.isAfter(this.end.getTemporal());

    }

    @Override
    public boolean isAfter(PlainDate temporal) {

        return this.start.getTemporal().isAfter(temporal);

    }

    @Override
    public boolean isBefore(PlainDate temporal) {

        return this.end.getTemporal().isBefore(temporal);

    }

    /**
     * <p>Validates given year-of-weekdate and week-of-year. </p>
     *
     * @param   yearOfWeekdate  the year of weekdate (can be different from calendar year near New Year)
     * @param   weekOfYear      the week of weekbased year
     * @return  {@code true} if valid else {@code false}
     * @see     #of(int, int)
     * @since   4.37
     */
    /*[deutsch]
     * <p>Validiert das angegebene wochenbasierte Jahr und die Woche des Jahres. </p>
     *
     * @param   yearOfWeekdate  the year of weekdate (can be different from calendar year near New Year)
     * @param   weekOfYear      the week of weekbased year
     * @return  {@code true} if valid else {@code false}
     * @see     #of(int, int)
     * @since   4.37
     */
    public static boolean isValid(
        int yearOfWeekdate,
        int weekOfYear
    ) {

        if ((yearOfWeekdate < GregorianMath.MIN_YEAR) || (yearOfWeekdate > GregorianMath.MAX_YEAR)) {
            return false;
        }

        return (weekOfYear >= 1) && (weekOfYear <= maximumOfWeek(yearOfWeekdate));

    }

    /**
     * <p>Calendar week always consist of seven days. </p>
     *
     * <p>The only exception is if the length is limited to five days in the last week of year
     * {@code 999,999,999} due to arithmetical reasons. </p>
     *
     * @return  {@code 7}
     */
    /*[deutsch]
     * <p>Eine Kalenderwoche hat immer 7 Tage. </p>
     *
     * <p>Einzige Ausnahme ist, wenn aus arithmetischen Gr&uuml;nden die L&auml;nge in der letzten Woche
     * des Jahres {@code 999.999.999} auf 5 beschr&auml;nkt ist. </p>
     *
     * @return  {@code 7}
     */
    public int length() {

        return ((this.year == GregorianMath.MAX_YEAR) && (this.week == 52)) ? 5 : 7;

    }

    /**
     * <p>Converts given gregorian date to a calendar week. </p>
     *
     * @param   date    gregorian calendar date (for example {@code PlainDate}
     * @return  CalendarWeek
     * @throws  IllegalArgumentException if given date is invalid
     * @since   3.28/4.24
     */
    /*[deutsch]
     * <p>Konvertiert das angegebene gregorianische Datum zu einer Kalenderwoche. </p>
     *
     * @param   date    gregorian calendar date (for example {@code PlainDate}
     * @return  CalendarWeek
     * @throws  IllegalArgumentException if given date is invalid
     * @since   3.28/4.24
     */
    public static CalendarWeek from(GregorianDate date) {

        PlainDate iso = PlainDate.from(date); // includes validation
        return CalendarWeek.of(iso.getInt(PlainDate.YEAR_OF_WEEKDATE), iso.getInt(Weekmodel.ISO.weekOfYear()));

    }

    /**
     * <p>Yields the last calendar week of the year of this instance. </p>
     *
     * @return  calendar week with week number {@code 52} or {@code 53}
     */
    /*[deutsch]
     * <p>Liefert die letzte Kalenderwoche des Jahres dieser Instanz. </p>
     *
     * @return  calendar week with week number {@code 52} or {@code 53}
     */
    public CalendarWeek withLastWeekOfYear() {

        if (this.week == this.lastWeek) {
            return this;
        }

        return CalendarWeek.of(this.year, this.lastWeek);

    }

    /**
     * <p>Adds given years to this calendar week. </p>
     *
     * @param   years       the count of week-based years to be added
     * @return  result of addition
     */
    /*[deutsch]
     * <p>Addiert die angegebenen Jahre zu dieser Kalenderwoche. </p>
     *
     * @param   years       the count of week-based years to be added
     * @return  result of addition
     */
    public CalendarWeek plus(Years<Weekcycle> years) {

        if (years.isEmpty()) {
            return this;
        }

        int y = MathUtils.safeAdd(this.year, years.getAmount());
        int effectiveWeek = this.week;

        if ((this.week == 53) && (maximumOfWeek(y) < 53)) {
            effectiveWeek = 52;
        }

        return CalendarWeek.of(y, effectiveWeek);

    }

    /**
     * <p>Adds given weeks to this calendar week. </p>
     *
     * @param   weeks       the count of weeks to be added
     * @return  result of addition
     */
    /*[deutsch]
     * <p>Addiert die angegebenen Wochen zu dieser Kalenderwoche. </p>
     *
     * @param   weeks       the count of weeks to be added
     * @return  result of addition
     */
    public CalendarWeek plus(Weeks weeks) {

        if (weeks.isEmpty()) {
            return this;
        }

        PlainDate date = this.start.getTemporal().plus(weeks.getAmount(), CalendarUnit.WEEKS);
        int y = date.getInt(YEAR_OF_WEEKDATE);
        int w = date.getInt(WEEK_OF_YEAR);
        return CalendarWeek.of(y, w);

    }

    /**
     * <p>Subtracts given years from this calendar week. </p>
     *
     * @param   years       the count of week-based years to be subtracted
     * @return  result of subtraction
     */
    /*[deutsch]
     * <p>Subtrahiert die angegebenen Jahre von dieser Kalenderwoche. </p>
     *
     * @param   years       the count of week-based years to be subtracted
     * @return  result of subtraction
     */
    public CalendarWeek minus(Years<Weekcycle> years) {

        if (years.isEmpty()) {
            return this;
        }

        int y = MathUtils.safeSubtract(this.year, years.getAmount());
        int effectiveWeek = this.week;

        if ((this.week == 53) && (maximumOfWeek(y) < 53)) {
            effectiveWeek = 52;
        }

        return CalendarWeek.of(y, effectiveWeek);

    }

    /**
     * <p>Subtracts given weeks from this calendar week. </p>
     *
     * @param   weeks       the count of weeks to be subtracted
     * @return  result of subtraction
     */
    /*[deutsch]
     * <p>Subtrahiert die angegebenen Wochen von dieser Kalenderwoche. </p>
     *
     * @param   weeks       the count of weeks to be subtracted
     * @return  result of subtraction
     */
    public CalendarWeek minus(Weeks weeks) {

        if (weeks.isEmpty()) {
            return this;
        }

        PlainDate date = this.start.getTemporal().minus(weeks.getAmount(), CalendarUnit.WEEKS);
        int y = date.getInt(YEAR_OF_WEEKDATE);
        int w = date.getInt(WEEK_OF_YEAR);
        return CalendarWeek.of(y, w);

    }

    @Override
    public int compareTo(CalendarWeek other) {

        if (this.year < other.year) {
            return -1;
        } else if (this.year > other.year) {
            return 1;
        } else {
            return (this.week - other.week);
        }

    }

    /**
     * <p>Iterates over all seven days from Monday to Sunday. </p>
     *
     * @return  Iterator
     */
    /*[deutsch]
     * <p>Iteriert &uuml;ber alle sieben Tage von Montag bis Sonntag. </p>
     *
     * @return  Iterator
     */
    @Override
    public Iterator<PlainDate> iterator() {

        return new Iter();

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof CalendarWeek) {
            CalendarWeek that = (CalendarWeek) obj;
            return ((this.year == that.year) && (this.week == that.week));
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return this.year ^ (this.week >> 16);

    }

    /**
     * <p>Outputs this instance as a String in CLDR-format &quot;YYYY-'W'ww&quot; (like &quot;2016-W01&quot;). </p>
     *
     * @return  String
     * @see     #parseISO(String)
     */
    /*[deutsch]
     * <p>Gibt diese Instanz als String im CLDR-Format &quot;YYYY-'W'ww&quot; (wie &quot;2016-W01&quot;) aus. </p>
     *
     * @return  String
     * @see     #parseISO(String)
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        formatYear(sb, this.year);
        sb.append("-W");
        if (this.week < 10) {
            sb.append('0');
        }
        sb.append(this.week);
        return sb.toString();

    }

    /**
     * <p>Interpretes given ISO-conforming text as calendar week. </p>
     *
     * <p>The underlying parser uses the CLDR-pattern &quot;YYYY[-]'W'ww&quot;. </p>
     *
     * @param   text        text to be parsed
     * @return  parsed calendar week
     * @throws  IndexOutOfBoundsException if given text is empty
     * @throws  ParseException if the text is not parseable
     * @see     #toString()
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen ISO-kompatiblen Text als Kalenderwoche. </p>
     *
     * <p>Der zugrundeliegende Interpretierer verwendet das CLDR-Formatmuster &quot;YYYY[-]'W'ww&quot;. </p>
     *
     * @param   text        text to be parsed
     * @return  parsed calendar week
     * @throws  IndexOutOfBoundsException if given text is empty
     * @throws  ParseException if the text is not parseable
     * @see     #toString()
     */
    public static CalendarWeek parseISO(String text) throws ParseException {

        return PARSER.parse(text);

    }

    /**
     * <p>Yields the associated chronology. </p>
     *
     * @return  the underlying rule engine
     */
    /*[deutsch]
     * <p>Liefert die assoziierte Chronologie. </p>
     *
     * @return  the underlying rule engine
     */
    public static Chronology<CalendarWeek> chronology() {

        return ENGINE;

    }

    /**
     * <p>Obtains a timeline for this type. </p>
     *
     * @return  singleton timeline
     * @since   5.0
     */
    /*[deutsch]
     * <p>Liefert einen Zeitstrahl f&uuml;r diesen Typ. </p>
     *
     * @return  singleton timeline
     * @since   5.0
     */
    public static TimeLine<CalendarWeek> timeline() {

        return FixedCalendarTimeLine.forWeeks();

    }

    @Override
    public String getFormatPattern(
        FormatStyle style,
        Locale locale
    ) {

        PluralCategory pc = PluralRules.of(locale, NumberType.ORDINALS).getCategory(this.week);
        Map<String, String> textForms = CalendarText.getIsoInstance(locale).getTextForms();
        String ywKey = "F_yw";
        String pattern = textForms.get(ywKey + "_" + pc.name().toLowerCase());

        if (pattern == null) {
            pattern = textForms.get(ywKey);
            if (pattern == null) {
                pattern = getIsoPattern(style);
            }
        }

        return pattern;

    }

    @Override
    public boolean useDynamicFormatPattern() {

        return true;

    }

    @Override
    protected Chronology<CalendarWeek> getChronology() {

        return ENGINE;

    }

    @Override
    protected CalendarWeek getContext() {

        return this;

    }

    @Override
    long toProlepticNumber() {

        return ((this.at(Weekday.MONDAY).getDaysSinceEpochUTC() / 7) + 1);

    }

    static CalendarWeek from(long prolepticNumber) {

        PlainDate iso = PlainDate.of(Math.multiplyExact(prolepticNumber, 7), EpochDays.UTC);
        return CalendarWeek.of(iso.getInt(PlainDate.YEAR_OF_WEEKDATE), iso.getInt(Weekmodel.ISO.weekOfYear()));

    }

    private static int maximumOfWeek(int yearOfWeekdate) {

        return PlainDate.of(yearOfWeekdate, 7, 1).getMaximum(WEEK_OF_YEAR).intValue();

    }

    private static String getIsoPattern(FormatStyle style) {

        return (style == FormatStyle.SHORT) ? "YYYY'W'ww" : "YYYY-'W'ww";

    }

    /**
     * @serialData  Uses <a href="../../../serialized-form.html#net.time4j.range.SPX">
     *              a dedicated serialization form</a> as proxy. The format
     *              is bit-compressed. The first byte contains in the six
     *              most significant bits the type-ID {@code 39}. Then the year number
     *              and the quarter number are written as int-primitives.
     *
     * Schematic algorithm:
     *
     * <pre>
     *  int header = 39;
     *  header &lt;&lt;= 2;
     *  out.writeByte(header);
     *  out.writeInt(getYear());
     *  out.writeInt(getWeek());
     * </pre>
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.WEEK_TYPE);

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

    private static class Merger
        implements ChronoMerger<CalendarWeek> {

        //~ Methoden ------------------------------------------------------

        @Override
        public CalendarWeek createFrom(
            TimeSource<?> clock,
            AttributeQuery attributes
        ) {

            Timezone zone;

            if (attributes.contains(Attributes.TIMEZONE_ID)) {
                zone = Timezone.of(attributes.get(Attributes.TIMEZONE_ID));
            } else if (attributes.get(Attributes.LENIENCY, Leniency.SMART).isLax()) {
                zone = Timezone.ofSystem();
            } else {
                return null;
            }

            PlainDate date = Moment.from(clock.currentTime()).toZonalTimestamp(zone.getID()).toDate();
            return CalendarWeek.of(date.getInt(YEAR_OF_WEEKDATE), date.getInt(WEEK_OF_YEAR));

        }

        @Override
        public CalendarWeek createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean lenient,
            boolean preparsing
        ) {

            int y = entity.getInt(YEAR_OF_WEEKDATE);
            int w = entity.getInt(WEEK_OF_YEAR);

            if (
                (y >= GregorianMath.MIN_YEAR)
                && (y <= GregorianMath.MAX_YEAR)
                && (w >= 1)
                && (w <= maximumOfWeek(y))
            ) {
                return CalendarWeek.of(y, w);
            } else if (y > Integer.MIN_VALUE) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Year out of bounds: " + y);
            } else if (w > Integer.MIN_VALUE) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Week-of-year out of bounds: " + w);
            }

            return null;

        }

        @Override
        public String getFormatPattern(
            FormatStyle style,
            Locale locale
        ) {

            StringBuilder sb = new StringBuilder();

            if (!locale.equals(Locale.ROOT)) {
                Map<String, String> textForms = CalendarText.getIsoInstance(locale).getTextForms();

                for (PluralCategory pc : PluralCategory.values()) {
                    String key = "F_yw";
                    if (pc != PluralCategory.OTHER) {
                        key += "_" + pc.name().toLowerCase();
                    }
                    String pattern = textForms.get(key);
                    if (pattern != null) {
                        if (sb.length() > 0) {
                            sb.append('|');
                        }
                        sb.append(pattern);
                    }
                }
            }

            if (sb.length() == 0) {
                sb.append(getIsoPattern(style));
            }

            return sb.toString();

        }

    }

    private static class YearRule
        implements IntElementRule<CalendarWeek> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Integer getValue(CalendarWeek context) {

            return Integer.valueOf(context.year);

        }

        @Override
        public Integer getMinimum(CalendarWeek context) {

            return Integer.valueOf(GregorianMath.MIN_YEAR);

        }

        @Override
        public Integer getMaximum(CalendarWeek context) {

            return Integer.valueOf(GregorianMath.MAX_YEAR);

        }

        @Override
        public boolean isValid(
            CalendarWeek context,
            Integer value
        ) {

            if (value == null) {
                return false;
            }

            int v = value.intValue();
            return ((v >= GregorianMath.MIN_YEAR) && (v <= GregorianMath.MAX_YEAR));

        }

        @Override
        public CalendarWeek withValue(
            CalendarWeek context,
            Integer value,
            boolean lenient
        ) {

            if (this.isValid(context, value)) {
                int y = value.intValue();
                return CalendarWeek.of(y, Math.min(maximumOfWeek(y), context.week));
            } else {
                throw new IllegalArgumentException("Not valid: " + value);
            }

        }

        @Override
        public ChronoElement<?> getChildAtFloor(CalendarWeek context) {

            return WEEK_OF_YEAR;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(CalendarWeek context) {

            return WEEK_OF_YEAR;

        }

        @Override
        public int getInt(CalendarWeek context) {

            return context.year;

        }

        @Override
        public boolean isValid(
            CalendarWeek context,
            int value
        ) {

            return ((value >= GregorianMath.MIN_YEAR) && (value <= GregorianMath.MAX_YEAR));

        }

        @Override
        public CalendarWeek withValue(
            CalendarWeek context,
            int value,
            boolean lenient
        ) {

            if (this.isValid(context, value)) {
                int w = context.week;
                if (w == 53) {
                    w = Math.min(maximumOfWeek(value), context.week);
                }
                return CalendarWeek.of(value, w);
            } else {
                throw new IllegalArgumentException("Not valid: " + value);
            }

        }

    }

    private static class WeekRule
        implements IntElementRule<CalendarWeek> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Integer getValue(CalendarWeek context) {

            return Integer.valueOf(context.week);

        }

        @Override
        public Integer getMinimum(CalendarWeek context) {

            return Integer.valueOf(1);

        }

        @Override
        public Integer getMaximum(CalendarWeek context) {

            return Integer.valueOf(context.lastWeek);

        }

        @Override
        public boolean isValid(
            CalendarWeek context,
            Integer value
        ) {

            if (value == null) {
                return false;
            }

            int v = value.intValue();
            return ((v >= 1) && ((v < 53) || (v == context.lastWeek)));

        }

        @Override
        public CalendarWeek withValue(
            CalendarWeek context,
            Integer value,
            boolean lenient
        ) {

            if (this.isValid(context, value)) {
                return CalendarWeek.of(context.year, value.intValue());
            } else {
                throw new IllegalArgumentException("Not valid: " + value);
            }

        }

        @Override
        public ChronoElement<?> getChildAtFloor(CalendarWeek context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(CalendarWeek context) {

            return null;

        }

        @Override
        public int getInt(CalendarWeek context) {

            return context.week;

        }

        @Override
        public boolean isValid(
            CalendarWeek context,
            int value
        ) {

            return ((value >= 1) && ((value < 53) || (value == context.lastWeek)));

        }

        @Override
        public CalendarWeek withValue(
            CalendarWeek context,
            int value,
            boolean lenient
        ) {

            if (this.isValid(context, value)) {
                return CalendarWeek.of(context.year, value);
            } else {
                throw new IllegalArgumentException("Not valid: " + value);
            }

        }

    }

    private class Iter
        implements Iterator<PlainDate> {

        //~ Instanzvariablen ----------------------------------------------

        private int count = 0;
        private int max = CalendarWeek.this.length();

        //~ Methoden ------------------------------------------------------

        @Override
        public boolean hasNext() {
            return (this.count < this.max);
        }

        @Override
        public PlainDate next() {
            if (this.count >= this.max) {
                throw new NoSuchElementException();
            } else {
                PlainDate result = CalendarWeek.this.start.getTemporal().plus(this.count, CalendarUnit.DAYS);
                this.count++;
                return result;
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}