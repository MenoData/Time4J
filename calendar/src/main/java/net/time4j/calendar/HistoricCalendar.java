/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HistoricCalendar.java) is part of project Time4J.
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
import net.time4j.base.TimeSource;
import net.time4j.calendar.service.GenericDatePatterns;
import net.time4j.calendar.service.StdEnumDateElement;
import net.time4j.calendar.service.StdIntegerDateElement;
import net.time4j.calendar.service.StdWeekdayElement;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.BasicElement;
import net.time4j.engine.CalendarDays;
import net.time4j.engine.CalendarEra;
import net.time4j.engine.CalendarFamily;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.CalendarVariant;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.ChronoFunction;
import net.time4j.engine.ChronoMerger;
import net.time4j.engine.ChronoOperator;
import net.time4j.engine.Chronology;
import net.time4j.engine.DisplayStyle;
import net.time4j.engine.ElementRule;
import net.time4j.engine.EpochDays;
import net.time4j.engine.FormattableElement;
import net.time4j.engine.IntElementRule;
import net.time4j.engine.StartOfDay;
import net.time4j.engine.ValidationElement;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.CalendarType;
import net.time4j.format.DisplayElement;
import net.time4j.format.Leniency;
import net.time4j.format.LocalizedPatternSupport;
import net.time4j.format.NumberSystem;
import net.time4j.format.TextElement;
import net.time4j.format.internal.DualFormatElement;
import net.time4j.history.ChronoHistory;
import net.time4j.history.HistoricDate;
import net.time4j.history.HistoricEra;
import net.time4j.history.NewYearStrategy;
import net.time4j.history.YearDefinition;
import net.time4j.history.internal.HistoricAttribute;
import net.time4j.i18n.HistoricExtension;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * <p>Represents the historic christian calendar used in most European countries. </p>
 *
 * <p>Following elements which are declared as constants are registered by this class: </p>
 *
 * <ul>
 *  <li>{@link #DAY_OF_WEEK}</li>
 *  <li>{@link #DAY_OF_MONTH}</li>
 *  <li>{@link #DAY_OF_YEAR}</li>
 *  <li>{@link #WEEKDAY_IN_MONTH}</li>
 *  <li>{@link #MONTH_OF_YEAR}</li>
 *  <li>{@link #RELATED_STANDARD_YEAR}</li>
 *  <li>{@link #CENTURY_OF_ERA}</li>
 *  <li>{@link #ERA}</li>
 * </ul>
 *
 * <p>Furthermore, all elements defined in {@code EpochDays} and {@link CommonElements} are supported. </p>
 *
 * <p>Example: </p>
 *
 * <pre>
 *      ChronoHistory history = ChronoHistory.of(Locale.UK);
 *      ChronoFormatter&lt;HistoricCalendar&gt; f =
 *          ChronoFormatter.ofStyle(DisplayMode.FULL, Locale.ENGLISH, HistoricCalendar.family()).with(history);
 *      HistoricCalendar cal = HistoricCalendar.of(history, HistoricEra.AD, 1603, 3, 24);
 *      String text = &quot;Thursday, March 24, 1602/03 AD&quot; // dual dating for the historic year
 *      assertThat(f.format(cal), is(text));
 *      assertThat(f.parse(text), is(cal));
 * </pre>
 *
 * <p>Any gregorian date (ISO-8601) can be transformed to {@code HistoricCalendar} this simple way: </p>
 *
 * <pre>
 *     ChronoHistory history = ChronoHistory.ofFirstGregorianReform();
 *     HistoricCalendar cal = PlainDate.of(1582, 10, 5).transform(HistoricCalendar.family(), history)
 *     System.out.println(cal); // AD-1582-09-25[...], ten days were cut off by pope Gregor
 * </pre>
 *
 * @author  Meno Hochschild
 * @since   3.36/4.31
 */
/*[deutsch]
 * <p>Repr&auml;sentiert den historischen christlichen Kalender, der in vielen europ&auml;ischen L&auml;ndern
 * benutzt wurde. </p>
 *
 * <p>Registriert sind folgende als Konstanten deklarierte Elemente: </p>
 *
 * <ul>
 *  <li>{@link #DAY_OF_WEEK}</li>
 *  <li>{@link #DAY_OF_MONTH}</li>
 *  <li>{@link #DAY_OF_YEAR}</li>
 *  <li>{@link #WEEKDAY_IN_MONTH}</li>
 *  <li>{@link #MONTH_OF_YEAR}</li>
 *  <li>{@link #RELATED_STANDARD_YEAR}</li>
 *  <li>{@link #CENTURY_OF_ERA}</li>
 *  <li>{@link #ERA}</li>
 * </ul>
 *
 * <p>Au&slig;erdem werden alle Elemente von {@code EpochDays} und {@link CommonElements} unterst&uuml;tzt. </p>
 *
 * <p>Beispiel: </p>
 *
 * <pre>
 *      ChronoHistory history = ChronoHistory.of(Locale.UK);
 *      ChronoFormatter&lt;HistoricCalendar&gt; f =
 *          ChronoFormatter.ofStyle(DisplayMode.FULL, Locale.ENGLISH, HistoricCalendar.family()).with(history);
 *      HistoricCalendar cal = HistoricCalendar.of(history, HistoricEra.AD, 1603, 3, 24);
 *      String text = &quot;Thursday, March 24, 1602/03 AD&quot; // duales Jahresformat
 *      assertThat(f.format(cal), is(text));
 *      assertThat(f.parse(text), is(cal));
 * </pre>
 *
 * <p>Jedes gregorianische Datum (ISO-8601) kann zu einem {@code HistoricCalendar} auf folgende einfache
 * Art und Weise transformiert werden: </p>
 *
 * <pre>
 *     ChronoHistory history = ChronoHistory.ofFirstGregorianReform();
 *     HistoricCalendar cal = PlainDate.of(1582, 10, 5).transform(HistoricCalendar.family(), history)
 *     System.out.println(cal); // AD-1582-09-25[...], ten days were cut off by pope Gregor
 * </pre>
 *
 * @author  Meno Hochschild
 * @since   3.36/4.31
 */
@CalendarType("historic")
public final class HistoricCalendar
    extends CalendarVariant<HistoricCalendar>
    implements LocalizedPatternSupport {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int YEAR_INDEX = 0;
    private static final int DAY_OF_MONTH_INDEX = 2;
    private static final int DAY_OF_YEAR_INDEX = 3;
    private static final int CENTURY_INDEX = 4;
    private static final int CONTINUOUS_DOM_INDEX = 5;

    /**
     * <p>Represents the historic era. </p>
     *
     * <p>The era value cannot be changed in any way which makes sense
     * so this element is like a display-only element. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die historische &Auml;ra. </p>
     *
     * <p>Der &Auml;ra-Wert kann nicht auf eine sinnvolle Weise ge&auml;ndert werden,
     * so da&szlig; sich dieses Element wie ein reines Anzeigeelement verh&auml;lt. </p>
     */
    @FormattableElement(format = "G")
    public static final ChronoElement<HistoricEra> ERA = new EraElement();

    /**
     * <p>Represents the historic century. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert das historische Jahrhundert. </p>
     */
    public static final ChronoElement<Integer> CENTURY_OF_ERA =
        new SimpleElement(
            "CENTURY_OF_ERA",
            ChronoHistory.ofFirstGregorianReform().centuryOfEra().getDefaultMinimum(),
            ChronoHistory.ofFirstGregorianReform().centuryOfEra().getDefaultMaximum());

    /**
     * <p>Represents the related standard year within current era which starts on first of January. </p>
     *
     * <p>Note: Getting true historic years which take care of different new-year-rules
     * is possible via the expression {@link #getYear()}. This year definition is similar to
     * {@link CommonElements#RELATED_GREGORIAN_YEAR} but takes into account the change
     * from julian to gregorian calendar. </p>
     *
     * <p>When used in formatting of historic dates, Time4J will apply dual dating unless
     * a special year definition has been set as {@link ChronoHistory#YEAR_DEFINITION format attribute}. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert das assoziierte Standardjahr der aktuellen &Auml;ra, das am ersten Januar beginnt. </p>
     *
     * <p>Hinweis: Um wahre historische Jahre zu erhalten, die sich um verschiedene Neujahrsregeln k&uuml;mmern,
     * kann folgender Ausdruck verwendet werden: {@link #getYear()}. Diese Jahresdefinition ist &auml;hnlich zu
     * {@link CommonElements#RELATED_GREGORIAN_YEAR}, ber&uuml;cksichtigt aber den Wechsel vom julianischen
     * zum gregorianischen Kalender. </p>
     *
     * <p>Wenn historische Datumsangaben formatiert werden, wird Time4J ein duales Jahrformat anwenden, es sei denn,
     * eine andere Jahresdefinition wurde als {@link ChronoHistory#YEAR_DEFINITION Formatattribut} angegeben. </p>
     */
    @FormattableElement(format = "y")
    public static final TextElement<Integer> RELATED_STANDARD_YEAR = new YearElement();

    /**
     * <p>Represents the historic month. </p>
     *
     * <p>The expression {@code with(MONTH_OF_YEAR.incremented())} works in the following way: </p>
     *
     * <ul>
     *     <li>If possible the current day of month will be kept.</li>
     *     <li>If the new month has a shorter length than the current day of month then the day of month
     *     will be the maximum day of the new month.</li>
     *     <li>If this date is the first day of month and is not valid on next month due to a gap
     *     on the date line then the new day of month will be corrected to be the first valid day
     *     of the new month.</li>
     * </ul>
     *
     * <p>Example (AD-1610-09-01 is invalid!): </p>
     *
     * <pre>
     *     ChronoHistory history = ChronoHistory.of(new Locale(&quot;de&quot;, &quot;DE&quot;, &quot;PREUSSEN&quot;));
     *     HistoricCalendar cal = HistoricCalendar.of(history, HistoricEra.AD, 1610, 8, 1);
     *     System.out.println(cal.with(HistoricCalendar.MONTH_OF_YEAR.incremented()));
     *     // AD-1610-09-02[...]
     * </pre>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den historischen Monat. </p>
     *
     * <p>Der Ausdruck {@code with(MONTH_OF_YEAR.incremented())} funktioniert auf folgende Art und Weise: </p>
     *
     * <ul>
     *     <li>Wenn m&ouml;glich, wird der aktuelle Tag des Monats beibehalten.</li>
     *     <li>Wenn der neue Monat eine k&uuml;rzere L&auml;nge hat als der aktuelle Tag des Monats es anzeigt,
     *     dann wird der Tag des Monats der letzte Tag des neues Monats sein.</li>
     *     <li>Wenn dieses Datum am ersten Tag des Monats liegt und der erste Tag des neuen Monats wegen einer
     *     L&uuml;cke auf der Datumslinie ung&uuml;ltig ist, dann wird als Tag des neuen Monats das erste
     *     g&uuml;ltige Datum genommen.</li>
     * </ul>
     *
     * <p>Beispiel (AD-1610-09-01 ist ung&uuml;ltig!): </p>
     *
     * <pre>
     *     ChronoHistory history = ChronoHistory.of(new Locale(&quot;de&quot;, &quot;DE&quot;, &quot;PREUSSEN&quot;));
     *     HistoricCalendar cal = HistoricCalendar.of(history, HistoricEra.AD, 1610, 8, 1);
     *     System.out.println(cal.with(HistoricCalendar.MONTH_OF_YEAR.incremented()));
     *     // AD-1610-09-02[...]
     * </pre>
     */
    @FormattableElement(format = "M", standalone = "L")
    public static final StdCalendarElement<Month, HistoricCalendar> MONTH_OF_YEAR =
        new StdEnumDateElement<Month, HistoricCalendar>(
            "MONTH_OF_YEAR",
            HistoricCalendar.class,
            Month.class,
            'M',
            new MonthOperator(true),
            new MonthOperator(false)
        ) {
            @Override
            protected String getCalendarType(AttributeQuery attributes) {
                return CalendarText.ISO_CALENDAR_TYPE;
            }
        };

    /**
     * <p>Represents the historic day of month. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den historischen Tag des Monats. </p>
     */
    @FormattableElement(format = "d")
    public static final StdCalendarElement<Integer, HistoricCalendar> DAY_OF_MONTH =
        new StdIntegerDateElement<HistoricCalendar>("DAY_OF_MONTH", HistoricCalendar.class, 1, 31, 'd');

    /**
     * <p>Represents the historic day of year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den historischen Tag des Jahres. </p>
     */
    @FormattableElement(format = "D")
    public static final StdCalendarElement<Integer, HistoricCalendar> DAY_OF_YEAR =
        new StdIntegerDateElement<HistoricCalendar>("DAY_OF_YEAR", HistoricCalendar.class, 1, 365, 'D');

    /**
     * <p>Represents the historic day of week. </p>
     *
     * <p>If the day-of-week is set to a new value then Time4J handles the historic calendar week
     * as starting on Sunday (usual in christian context). </p>
     *
     * @see     #getDefaultWeekmodel()
     * @see     CommonElements#localDayOfWeek(net.time4j.engine.Chronology, Weekmodel)
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den historischen Tag der Woche. </p>
     *
     * <p>Wenn der Tag der Woche auf einen neuen Wert gesetzt wird, behandelt Time4J die historische
     * Kalenderwoche so, da&szlig; sie am Sonntag beginnt (Normalfall im christlichen Kontext. </p>
     *
     * @see     #getDefaultWeekmodel()
     * @see     CommonElements#localDayOfWeek(Chronology, Weekmodel)
     */
    @FormattableElement(format = "E")
    public static final StdCalendarElement<Weekday, HistoricCalendar> DAY_OF_WEEK =
        new StdWeekdayElement<HistoricCalendar>(HistoricCalendar.class, getDefaultWeekmodel());

    private static final ChronoElement<Integer> CONTINUOUS_DOM =
        new SimpleElement("HC_CONTINUOUS_DOM", 1, 31);
    private static final WeekdayInMonthElement<HistoricCalendar> WIM_ELEMENT =
        new WeekdayInMonthElement<HistoricCalendar>(HistoricCalendar.class, CONTINUOUS_DOM, DAY_OF_WEEK);

    /**
     * <p>Element with the ordinal day-of-week within given calendar month. </p>
     */
    /*[deutsch]
     * <p>Element mit dem x-ten Wochentag im Monat. </p>
     */
    @FormattableElement(format = "F")
    public static final OrdinalWeekdayElement<HistoricCalendar> WEEKDAY_IN_MONTH = WIM_ELEMENT;

    private static final Map<String, CalendarSystem<HistoricCalendar>> CALSYS;
    private static final CalendarFamily<HistoricCalendar> ENGINE;

    static {
        Map<String, CalendarSystem<HistoricCalendar>> map = new VariantMap();
        ChronoHistory stdHistory = ChronoHistory.ofFirstGregorianReform();
        map.put(stdHistory.getVariant(), new Transformer(stdHistory));
        CALSYS = map;

        CalendarFamily.Builder<HistoricCalendar> builder =
            CalendarFamily.Builder.setUp(
                HistoricCalendar.class,
                new Merger(),
                CALSYS)
            .appendElement(
                PlainDate.COMPONENT,
                new GregorianDateRule())
            .appendElement(
                ERA,
                new EraRule())
            .appendElement(
                CENTURY_OF_ERA,
                new IntegerRule(CENTURY_INDEX))
            .appendElement(
                RELATED_STANDARD_YEAR,
                new IntegerRule(YEAR_INDEX))
            .appendElement(
                MONTH_OF_YEAR,
                new MonthRule())
            .appendElement(
                CommonElements.RELATED_GREGORIAN_YEAR,
                new RelatedGregorianYearRule<HistoricCalendar>(CALSYS, DAY_OF_YEAR))
            .appendElement(
                CONTINUOUS_DOM,
                new IntegerRule(CONTINUOUS_DOM_INDEX))
            .appendElement(
                DAY_OF_MONTH,
                new IntegerRule(DAY_OF_MONTH_INDEX))
            .appendElement(
                DAY_OF_YEAR,
                new IntegerRule(DAY_OF_YEAR_INDEX))
            .appendElement(
                DAY_OF_WEEK,
                new WeekdayRule<HistoricCalendar>(
                    getDefaultWeekmodel(),
                    new ChronoFunction<HistoricCalendar, CalendarSystem<HistoricCalendar>>() {
                        @Override
                        public CalendarSystem<HistoricCalendar> apply(HistoricCalendar context) {
                            return context.getChronology().getCalendarSystem(context.getVariant());
                        }
                    }
                ))
            .appendElement(
                WIM_ELEMENT,
                WeekdayInMonthElement.getRule(WIM_ELEMENT))
            .appendExtension(
                new CommonElements.Weekengine(
                    HistoricCalendar.class,
                    DAY_OF_MONTH,
                    DAY_OF_YEAR,
                    getDefaultWeekmodel()));
        ENGINE = builder.build();
    }

    private static final long serialVersionUID = 7723641381724201009L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final HistoricDate date;

    /**
     * @serial  The corresponding gregorian date.
     */
    private final PlainDate gregorian;

    /**
     * @serial  The associated calendar history.
     */
    private final ChronoHistory history;

    //~ Konstruktoren -----------------------------------------------------

    private HistoricCalendar(
        ChronoHistory history,
        HistoricDate date
    ) {
        super();

        this.gregorian = history.convert(date);
        this.date = date;
        this.history = history;

    }

    private HistoricCalendar(
        ChronoHistory history,
        PlainDate gregorian
    ) {
        super();

        this.date = history.convert(gregorian);
        this.gregorian = gregorian;
        this.history = history;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Constructs a new historic calendar. </p>
     *
     * <p>Equivalent to
     * {@link #of(ChronoHistory, HistoricEra, int, YearDefinition, int, int)}
     * of(history, era, relatedStandardYear, historicMonth, historicDayOfMonth, YearDefinition.DUAL_DATING)}. </p>
     *
     * @param   history             historization model
     * @param   era                 historic era
     * @param   relatedStandardYear the related standard year of era ({@code >= 1}) starting on January the first
     * @param   historicMonth       historic month (1-12)
     * @param   historicDayOfMonth  historic day of month (1-31)
     * @return  new historic calendar
     * @throws  IllegalArgumentException if any argument is out of required maximum range or invalid for other reasons
     */
    /*[deutsch]
     * <p>Konstruiert einen neuen historischen Kalender. </p>
     *
     * <p>&Auml;quivalent zu
     * {@link #of(ChronoHistory, HistoricEra, int, YearDefinition, int, int)}
     * of(history, era, relatedStandardYear, historicMonth, historicDayOfMonth, YearDefinition.DUAL_DATING)}. </p>
     *
     * @param   history             historization model
     * @param   era                 historic era
     * @param   relatedStandardYear the related standard year of era ({@code >= 1}) starting on January the first
     * @param   historicMonth       historic month (1-12)
     * @param   historicDayOfMonth  historic day of month (1-31)
     * @return  new historic calendar
     * @throws  IllegalArgumentException if any argument is out of required maximum range or invalid for other reasons
     */
    public static HistoricCalendar of(
        ChronoHistory history,
        HistoricEra era,
        int relatedStandardYear,
        int historicMonth,
        int historicDayOfMonth
    ) {

        return HistoricCalendar.of(
            history, era, relatedStandardYear, YearDefinition.DUAL_DATING, historicMonth, historicDayOfMonth);

    }

    /**
     * <p>Constructs a new historic calendar. </p>
     *
     * @param   history             historization model
     * @param   era                 historic era
     * @param   yearOfEra           year of era which will be interpreted
     *                              according to given year definition ({@code >= 1})
     * @param   yearDefinition      defines a strategy how to interprete year of era
     * @param   historicMonth       historic month (1-12)
     * @param   historicDayOfMonth  historic day of month (1-31)
     * @return  new historic calendar
     * @throws  IllegalArgumentException if any argument is out of required maximum range or invalid for other reasons
     */
    /*[deutsch]
     * <p>Konstruiert einen neuen historischen Kalender. </p>
     *
     * @param   history             historization model
     * @param   era                 historic era
     * @param   yearOfEra           year of era which will be interpreted
     *                              according to given year definition ({@code >= 1})
     * @param   yearDefinition      defines a strategy how to interprete year of era
     * @param   historicMonth       historic month (1-12)
     * @param   historicDayOfMonth  historic day of month (1-31)
     * @return  new historic calendar
     * @throws  IllegalArgumentException if any argument is out of required maximum range or invalid for other reasons
     */
    public static HistoricCalendar of(
        ChronoHistory history,
        HistoricEra era,
        int yearOfEra,
        YearDefinition yearDefinition,
        int historicMonth,
        int historicDayOfMonth
    ) {

        HistoricDate date =
            HistoricDate.of(
                era, yearOfEra, historicMonth, historicDayOfMonth, yearDefinition, history.getNewYearStrategy());
        return HistoricCalendar.of(history, date);

    }

    /**
     * <p>Obtains the current historic calendar date in system time. </p>
     *
     * <p>Convenient short-cut for:
     * {@code SystemClock.inLocalView().now(HistoricCalendar.family(), history, StartOfDay.MIDNIGHT).toDate())}. </p>
     *
     * @param   history         historization model
     * @return  current historic calendar in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     */
    /*[deutsch]
     * <p>Ermittelt das aktuelle historische Kalenderdatum in der Systemzeit. </p>
     *
     * <p>Bequeme Abk&uuml;rzung f&uuml;r:
     * {@code SystemClock.inLocalView().now(HistoricCalendar.family(), history, StartOfDay.MIDNIGHT).toDate())}. </p>
     *
     * @param   history         historization model
     * @return  current historic calendar in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     */
    public static HistoricCalendar nowInSystemTime(ChronoHistory history) {

        return SystemClock.inLocalView().now(HistoricCalendar.family(), history, StartOfDay.MIDNIGHT).toDate();

    }

    /**
     * <p>Obtains the underlying calendar history. </p>
     *
     * @return  ChronoHistory
     */
    /*[deutsch]
     * <p>Liefert die zugrundeliegende Kalenderhistorie. </p>
     *
     * @return  ChronoHistory
     */
    public ChronoHistory getHistory() {

        return this.history;

    }

    /**
     * <p>Yields the historic era. </p>
     *
     * @return  HistoricEra
     */
    /*[deutsch]
     * <p>Liefert die historische &Auml;ra. </p>
     *
     * @return  HistoricEra
     */
    public HistoricEra getEra() {

        return this.date.getEra();

    }

    /**
     * <p>Obtains the century of era. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert das Jahrhundert der &Auml;ra. </p>
     *
     * @return  int
     */
    public int getCentury() {

        return this.getInt(this.history.centuryOfEra());

    }

    /**
     * <p>Obtains the true historic year as displayed in historic documents. </p>
     *
     * <p><strong>Important:</strong> The begin of a historic year can deviate from first of January.
     * Historic years are often not synchronized with month cycles. Users can apply the expression
     * {@code getInt(RELATED_STANDARD_YEAR)} in order to obtain the year beginning on first of January. </p>
     *
     * @return  int
     * @see     HistoricDate#getYearOfEra(NewYearStrategy)
     * @see     ChronoHistory#getNewYearStrategy()
     */
    /*[deutsch]
     * <p>Liefert das wahre historische Jahr, wie es in historischen Dokumenten angezeigt wird. </p>
     *
     * <p><strong>Wichtig:</strong>  Der Beginn eines historischen Jahres kann oft vom ersten Januar abweichen.
     * Historische Jahre sind oft nicht mit Monatszyklen synchronisiert. Um das am ersten Januar beginnende
     * Standardjahr zu ermitteln, k&ouml;nnen Anwender den Ausdruck {@code getInt(RELATED_STANDARD_YEAR)}
     * verwenden. </p>
     *
     * @return  int
     * @see     HistoricDate#getYearOfEra(NewYearStrategy)
     * @see     ChronoHistory#getNewYearStrategy()
     */
    public int getYear() {

        return this.date.getYearOfEra(this.history.getNewYearStrategy());

    }

    /**
     * <p>Yields the historic month. </p>
     *
     * @return  enum
     */
    /*[deutsch]
     * <p>Liefert den historischen Monat. </p>
     *
     * @return  enum
     */
    public Month getMonth() {

        return Month.valueOf(this.date.getMonth());

    }

    /**
     * <p>Yields the historic day of month. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert den historischen Tag des Monats. </p>
     *
     * @return  int
     */
    public int getDayOfMonth() {

        return this.date.getDayOfMonth();

    }

    /**
     * <p>Determines the day of week. </p>
     *
     * <p>The week model has never been subject to historical changes leaving aside radical calendar reforms
     * like the Frenh revolutionary calendar. </p>
     *
     * @return  Weekday
     */
    /*[deutsch]
     * <p>Ermittelt den Wochentag. </p>
     *
     * <p>Das Wochenmodell hat sich historisch nie ge&auml;ndert (au&szlig;er in radikalen Kalenderreformen
     * wie dem franz&ouml;sischen Revolutionskalender). </p>
     *
     * @return  Weekday
     */
    public Weekday getDayOfWeek() {

        long utcDays = this.gregorian.getDaysSinceEpochUTC();
        return Weekday.valueOf(MathUtils.floorModulo(utcDays + 5, 7) + 1);

    }

    /**
     * <p>Yields the historic day of year. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert den historischen Tag des Jahres. </p>
     *
     * @return  int
     */
    public int getDayOfYear() {

        return this.get(DAY_OF_YEAR).intValue();

    }

    @Override
    public String getVariant() {

        return this.history.getVariant();

    }

    /**
     * <p>Yields the length of current historic month in days. </p>
     *
     * @return  length of historic month in days or {@code -1} if the length cannot be determined
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge des aktuellen historischen Monats in Tagen. </p>
     *
     * @return  length of historic month in days or {@code -1} if the length cannot be determined
     */
    public int lengthOfMonth() {

        try {
            PlainDate min = this.with(DAY_OF_MONTH, this.getMinimum(DAY_OF_MONTH).intValue()).gregorian;
            PlainDate max = this.with(DAY_OF_MONTH, this.getMaximum(DAY_OF_MONTH).intValue()).gregorian;
            return ((int) CalendarDays.between(min, max).getAmount()) + 1;
        } catch (RuntimeException re) {
            return -1; // only in very exotic circumstances
        }

    }

    /**
     * <p>Yields the length of current historic year in days. </p>
     *
     * @return  length of historic year in days or {@code -1} if the length cannot be determined
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge des aktuellen historischen Jahres in Tagen. </p>
     *
     * @return  length of historic year in days or {@code -1} if the length cannot be determined
     */
    public int lengthOfYear() {

        return this.history.getLengthOfYear(this.getEra(), this.getYear());

    }

    /**
     * <p>Obtains the start of this year (which is often not the first of January). </p>
     *
     * @return new year
     */
    /*[deutsch]
     * <p>Liefert den Beginn dieses historischen Jahres (oft nicht am ersten Januar). </p>
     *
     * @return  new year
     */
    public HistoricCalendar withNewYear() {

        HistoricDate newYear = this.history.getBeginOfYear(this.getEra(), this.getYear());
        return HistoricCalendar.of(this.history, newYear);

    }

    /**
     * <p>Convenient short form for {@code with(DAY_OF_MONTH.incremented())}
     * or {@code plus(CalendarDays.ONE)}. </p>
     *
     * @return  copy of this instance at next day
     * @see     #plus(CalendarDays)
     * @see     CalendarDays#ONE
     */
    /*[deutsch]
     * <p>Kurzform f&uuml;r {@code with(DAY_OF_MONTH.incremented())}
     * oder {@code plus(CalendarDays.ONE)}. </p>
     *
     * @return  copy of this instance at next day
     * @see     #plus(CalendarDays)
     * @see     CalendarDays#ONE
     */
    public HistoricCalendar nextDay() {

        return this.with(DAY_OF_MONTH.incremented());

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
    public GeneralTimestamp<HistoricCalendar> at(PlainTime time) {

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
    public GeneralTimestamp<HistoricCalendar> atTime(
        int hour,
        int minute
    ) {

        return this.at(PlainTime.of(hour, minute));

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof HistoricCalendar) {
            HistoricCalendar that = (HistoricCalendar) obj;
            return (
                this.gregorian.equals(that.gregorian)
                    && this.history.equals(that.history)
                    && this.date.equals(that.date));
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return (17 * this.gregorian.hashCode() + 31 * this.history.hashCode());

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(32);
        sb.append(this.date);
        sb.append('[');
        sb.append(this.history);
        sb.append(']');
        return sb.toString();

    }

    /**
     * <p>Obtains the standard week model of this calendar. </p>
     *
     * <p>Christian calendars usually starts on Sunday. </p>
     *
     * @return  Weekmodel
     */
    /*[deutsch]
     * <p>Ermittelt das Standardwochenmodell dieses Kalenders. </p>
     *
     * <p>Christliche Kalender starten gew&ouml;hnlich am Sonntag. </p>
     *
     * @return  Weekmodel
     */
    public static Weekmodel getDefaultWeekmodel() {

        return Weekmodel.of(Weekday.SUNDAY, 1);

    }

    /**
     * <p>Returns the associated calendar family. </p>
     *
     * @return  chronology as calendar family
     */
    /*[deutsch]
     * <p>Liefert die zugeh&ouml;rige Kalenderfamilie. </p>
     *
     * @return  chronology as calendar family
     */
    public static CalendarFamily<HistoricCalendar> family() {

        return ENGINE;

    }

    @Override
    protected CalendarFamily<HistoricCalendar> getChronology() {

        return ENGINE;

    }

    @Override
    protected HistoricCalendar getContext() {

        return this;

    }

    private static HistoricCalendar of(
        ChronoHistory history,
        HistoricDate date
    ) {

        if (history.isValid(date)) {
            return new HistoricCalendar(history, date);
        } else {
            throw new IllegalArgumentException(
                "Historic date \"" + date + "\" invalid in history: " + history);
        }

    }

    private static ChronoHistory getHistory(AttributeQuery attributes) {

        if (attributes.contains(HistoricAttribute.CALENDAR_HISTORY)) {
            return attributes.get(HistoricAttribute.CALENDAR_HISTORY);
        } else if (
            attributes.get(Attributes.CALENDAR_TYPE, CalendarText.ISO_CALENDAR_TYPE).equals("historic")
            && attributes.contains(Attributes.CALENDAR_VARIANT)
        ) {
            return ChronoHistory.from(attributes.get(Attributes.CALENDAR_VARIANT));
        } else if (!attributes.get(Attributes.LENIENCY, Leniency.SMART).isStrict()) {
            return ChronoHistory.of(attributes.get(Attributes.LANGUAGE, Locale.ROOT));
        } else {
            return null; // calendar history was not defined (and we don't use just the locale in strict mode)
        }

    }

    /**
     * @serialData  Uses <a href="../../../serialized-form.html#net.time4j.calendar.SPX">
     *              a dedicated serialization form</a> as proxy. The first byte contains
     *              the type-ID {@code 11}. Then the calendar history is written out as UTF-string.
     *              Finally the UTC-epoch-days of corresponding gregorian date will be serialized as long.
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.HISTORIC);

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

    private static class Transformer
        implements CalendarSystem<HistoricCalendar> {

        //~ Instanzvariablen ----------------------------------------------

        private final ChronoHistory history;

        //~ Konstruktoren -------------------------------------------------

        Transformer(ChronoHistory history) {
            super();

            this.history = history;
        }

        //~ Methoden ------------------------------------------------------

        @Override
        public HistoricCalendar transform(long utcDays) {

            return new HistoricCalendar(this.history, PlainDate.of(utcDays, EpochDays.UTC));

        }

        @Override
        public long transform(HistoricCalendar date) {

            return date.gregorian.getDaysSinceEpochUTC();

        }

        @Override
        public long getMinimumSinceUTC() {

            PlainDate prototype = PlainDate.of(2000, 1, 1);
            HistoricDate hd = prototype.getMinimum(this.history.date());
            return this.history.convert(hd).getDaysSinceEpochUTC();

        }

        @Override
        public long getMaximumSinceUTC() {

            PlainDate prototype = PlainDate.of(2000, 1, 1);
            HistoricDate hd = prototype.getMaximum(this.history.date());
            return this.history.convert(hd).getDaysSinceEpochUTC();

        }

        @Override
        public List<CalendarEra> getEras() {

            List<CalendarEra> eras = new ArrayList<CalendarEra>();
            for (HistoricEra era : HistoricEra.values()) {
                eras.add(era);
            }
            return Collections.unmodifiableList(eras);

        }

    }

    private static class VariantMap
        extends ConcurrentHashMap<String, CalendarSystem<HistoricCalendar>> {

        //~ Methoden ------------------------------------------------------

        @Override
        public CalendarSystem<HistoricCalendar> get(Object key) {

            CalendarSystem<HistoricCalendar> calsys = super.get(key);

            if (calsys == null) {
                String variant = key.toString();

                try {
                    calsys = new Transformer(ChronoHistory.from(variant));
                } catch (IllegalArgumentException ex) {
                    return null;
                }

                CalendarSystem<HistoricCalendar> old = this.putIfAbsent(variant, calsys);

                if (old != null) {
                    calsys = old;
                }
            }

            return calsys;

        }

    }

    private static class IntegerRule
        implements IntElementRule<HistoricCalendar> {

        //~ Instanzvariablen ----------------------------------------------

        private final int index;

        //~ Konstruktoren -------------------------------------------------

        IntegerRule(int index) {
            super();

            this.index = index;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Integer getValue(HistoricCalendar context) {

            return Integer.valueOf(this.getInt(context));

        }

        @Override
        public Integer getMinimum(HistoricCalendar context) {

            if (this.index == CONTINUOUS_DOM_INDEX) {
                int max = context.date.getDayOfMonth();
                HistoricEra era = context.date.getEra();
                int yoe = context.date.getYearOfEra();
                int month = context.date.getMonth();
                for (int dom = 1; dom <= max; dom++) {
                    HistoricDate hd = HistoricDate.of(era, yoe, month, dom);
                    if (context.history.isValid(hd)) {
                        return Integer.valueOf(dom);
                    }
                }
            }

            return context.getMinimum(this.getElement(context));

        }

        @Override
        public Integer getMaximum(HistoricCalendar context) {

            if (this.index == CONTINUOUS_DOM_INDEX) {
                int max = context.getMaximum(context.history.dayOfMonth()).intValue();
                HistoricEra era = context.date.getEra();
                int yoe = context.date.getYearOfEra();
                int month = context.date.getMonth();
                int invalid = 0;
                for (int dom = 1; dom <= max; dom++) {
                    HistoricDate hd = HistoricDate.of(era, yoe, month, dom);
                    if (!context.history.isValid(hd)) {
                        invalid++;
                    }
                }
                return Integer.valueOf(max - invalid);
            }

            return context.getMaximum(this.getElement(context));

        }

        @Override
        public boolean isValid(
            HistoricCalendar context,
            Integer value
        ) {

            if ((value == null) || (this.index == CONTINUOUS_DOM_INDEX)) {
                return false;
            }

            return context.isValid(this.getElement(context), value);

        }

        @Override
        public HistoricCalendar withValue(
            HistoricCalendar context,
            Integer value,
            boolean lenient
        ) {

            return context.with(this.getElement(context), value);

        }

        @Override
        public ChronoElement<?> getChildAtFloor(HistoricCalendar context) {

            switch (this.index) {
                case DAY_OF_MONTH_INDEX:
                case DAY_OF_YEAR_INDEX:
                    return null;
                default:
                    throw new UnsupportedOperationException("Never called.");
            }

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(HistoricCalendar context) {

            switch (this.index) {
                case DAY_OF_MONTH_INDEX:
                case DAY_OF_YEAR_INDEX:
                    return null;
                default:
                    throw new UnsupportedOperationException("Never called.");
            }

        }

        private ChronoElement<Integer> getElement(HistoricCalendar context) {

            switch (this.index) {
                case YEAR_INDEX:
                    return context.history.yearOfEra();
                case DAY_OF_MONTH_INDEX:
                    return context.history.dayOfMonth();
                case DAY_OF_YEAR_INDEX:
                    return context.history.dayOfYear();
                case CENTURY_INDEX:
                    return context.history.centuryOfEra();
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public int getInt(HistoricCalendar context) {

            switch (this.index) {
                case YEAR_INDEX:
                    return context.date.getYearOfEra();
                case DAY_OF_MONTH_INDEX:
                    return context.date.getDayOfMonth();
                case CONTINUOUS_DOM_INDEX:
                    int current = context.date.getDayOfMonth(); // already validated
                    HistoricEra era = context.date.getEra();
                    int yoe = context.date.getYearOfEra();
                    int month = context.date.getMonth();
                    int invalid = 0;
                    for (int dom = 1; dom < current; dom++) {
                        HistoricDate hd = HistoricDate.of(era, yoe, month, dom);
                        if (!context.history.isValid(hd)) {
                            invalid++;
                        }
                    }
                    return current - invalid;
                default:
                    return context.getInt(this.getElement(context));
            }

        }

        @Override
        public boolean isValid(
            HistoricCalendar context,
            int value
        ) {

            if (this.index == CONTINUOUS_DOM_INDEX) {
                return false;
            }

            return context.isValid(this.getElement(context), value);

        }

        @Override
        public HistoricCalendar withValue(
            HistoricCalendar context,
            int value,
            boolean lenient
        ) {

            return context.with(this.getElement(context), value);

        }

    }

    private static class MonthRule
        implements ElementRule<HistoricCalendar, Month> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Month getValue(HistoricCalendar context) {

            return context.getMonth();

        }

        @Override
        public Month getMinimum(HistoricCalendar context) {

            return Month.valueOf(context.getMinimum(context.history.month()).intValue());

        }

        @Override
        public Month getMaximum(HistoricCalendar context) {

            return Month.valueOf(context.getMaximum(context.history.month()).intValue());

        }

        @Override
        public boolean isValid(
            HistoricCalendar context,
            Month value
        ) {

            if (value == null) {
                return false;
            }

            return context.isValid(context.history.month(), value.getValue());

        }

        @Override
        public HistoricCalendar withValue(
            HistoricCalendar context,
            Month value,
            boolean lenient
        ) {

            return context.with(context.history.month(), value.getValue());

        }

        @Override
        public ChronoElement<?> getChildAtFloor(HistoricCalendar context) {

            return DAY_OF_MONTH;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(HistoricCalendar context) {

            return DAY_OF_MONTH;

        }

    }

    private static class GregorianDateRule
        implements ElementRule<HistoricCalendar, PlainDate> {

        //~ Methoden ------------------------------------------------------

        @Override
        public PlainDate getValue(HistoricCalendar context) {

            return context.gregorian;

        }

        @Override
        public PlainDate getMinimum(HistoricCalendar context) {

            return context.history.convert(context.gregorian.getMinimum(context.history.date()));

        }

        @Override
        public PlainDate getMaximum(HistoricCalendar context) {

            return context.history.convert(context.gregorian.getMaximum(context.history.date()));

        }

        @Override
        public boolean isValid(
                HistoricCalendar context,
                PlainDate value
        ) {

            if (value == null) {
                return false;
            }

            try {
                context.history.convert(value);
                return true;
            } catch (IllegalArgumentException iae) {
                return false;
            }

        }

        @Override
        public HistoricCalendar withValue(
            HistoricCalendar context,
            PlainDate value,
            boolean lenient
        ) {

            return new HistoricCalendar(context.history, value);

        }

        @Override
        public ChronoElement<?> getChildAtFloor(HistoricCalendar context) {

            throw new UnsupportedOperationException("Never called.");

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(HistoricCalendar context) {

            throw new UnsupportedOperationException("Never called.");

        }

    }

    private static class EraRule
        implements ElementRule<HistoricCalendar, HistoricEra> {

        //~ Methoden ------------------------------------------------------

        @Override
        public HistoricEra getValue(HistoricCalendar context) {

            return context.getEra();

        }

        @Override
        public HistoricEra getMinimum(HistoricCalendar context) {

            HistoricEra era = context.getEra();

            if (era == HistoricEra.AD) {
                return HistoricEra.BC;
            } else {
                return era;
            }

        }

        @Override
        public HistoricEra getMaximum(HistoricCalendar context) {

            HistoricEra era = context.getEra();

            if (era == HistoricEra.BC) {
                return HistoricEra.AD;
            } else {
                return era;
            }

        }

        @Override
        public boolean isValid(
            HistoricCalendar context,
            HistoricEra value
        ) {

            if (value == null) {
                return false;
            }

            return (context.date.getEra() == value);

        }

        @Override
        public HistoricCalendar withValue(
            HistoricCalendar context,
            HistoricEra value,
            boolean lenient
        ) {

            if (value != null) {
                if (context.date.getEra() == value) {
                    return context;
                }
            }

            throw new IllegalArgumentException(value.name());

        }

        @Override
        public ChronoElement<?> getChildAtFloor(HistoricCalendar context) {

            throw new UnsupportedOperationException("Never called.");

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(HistoricCalendar context) {

            throw new UnsupportedOperationException("Never called.");

        }

    }

    private static class EraElement
        extends DisplayElement<HistoricEra>
        implements TextElement<HistoricEra> {

        //~ Statische Felder/Initialisierungen --------------------------------

        private static final long serialVersionUID = -4614710504356171166L;

        //~ Konstruktoren -------------------------------------------------

        EraElement() {
            super("ERA");

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Class<HistoricEra> getType() {

            return HistoricEra.class;

        }

        @Override
        public char getSymbol() {

            return 'G';

        }

        @Override
        public HistoricEra getDefaultMinimum() {

            return HistoricEra.BC;

        }

        @Override
        public HistoricEra getDefaultMaximum() {

            return HistoricEra.AD;

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
        public void print(
            ChronoDisplay context,
            Appendable buffer,
            AttributeQuery attributes
        ) throws IOException, ChronoException {

            if (context instanceof HistoricCalendar) {
                HistoricCalendar hc = HistoricCalendar.class.cast(context);
                TextElement.class.cast(hc.history.era()).print(hc, buffer, attributes);
            } else {
                throw new ChronoException("Cannot cast to historic calendar: " + context);
            }

        }

        @Override
        public HistoricEra parse(
            CharSequence text,
            ParsePosition status,
            AttributeQuery attributes
        ) {

            ChronoHistory history = getHistory(attributes);

            if (history == null) {
                return null;
            } else {
                Object result = TextElement.class.cast(history.era()).parse(text, status, attributes);
                return HistoricEra.class.cast(result);
            }

        }

        private Object readResolve() throws ObjectStreamException {

            return ERA;

        }

    }

    private static class SimpleElement
        extends DisplayElement<Integer> {

        //~ Statische Felder/Initialisierungen --------------------------------

        private static final long serialVersionUID = 3808762239145701486L;

        //~ Instanzvariablen ----------------------------------------------

        private transient final Integer min;
        private transient final Integer max;

        //~ Konstruktoren -------------------------------------------------

        private SimpleElement(
            String name,
            int min,
            int max
        ) {
            super(name);

            this.min = Integer.valueOf(min);
            this.max = Integer.valueOf(max);

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Class<Integer> getType() {

            return Integer.class;

        }

        @Override
        public Integer getDefaultMinimum() {

            return this.min;

        }

        @Override
        public Integer getDefaultMaximum() {

            return this.max;

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
        protected boolean doEquals(BasicElement<?> obj) {

            SimpleElement that = (SimpleElement) obj;
            return (this.min.equals(that.min) && this.max.equals(that.max));

        }

        private Object readResolve() throws ObjectStreamException {

            String n = this.name();

            if (n.equals("HC_CONTINUOUS_DOM")) {
                return CONTINUOUS_DOM;
            } else if (n.equals("CENTURY_OF_ERA")) {
                return CENTURY_OF_ERA;
            } else {
                throw new InvalidObjectException("Unknown element: " + n);
            }

        }

    }

    private static class YearElement
        extends SimpleElement
        implements DualFormatElement {

        //~ Statische Felder/Initialisierungen --------------------------------

        private static final long serialVersionUID = 6400379438892131807L;

        //~ Konstruktoren -------------------------------------------------

        private YearElement() {
            super("YEAR_OF_ERA", 1, GregorianMath.MAX_YEAR);

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public char getSymbol() {

            return 'y';

        }

        @Override
        public void print(
            ChronoDisplay context,
            Appendable buffer,
            AttributeQuery attributes
        ) throws IOException, ChronoException {

            if (context instanceof HistoricCalendar) {
                HistoricCalendar hc = HistoricCalendar.class.cast(context);
                TextElement<Integer> element = hc.history.yearOfEra();
                element.print(hc, buffer, attributes);
            } else {
                throw new ChronoException("Cannot cast to historic calendar: " + context);
            }

        }

        @Override
        public Integer parse(
            CharSequence text,
            ParsePosition status,
            AttributeQuery attributes
        ) {

            ChronoHistory history = getHistory(attributes);

            if (history == null) {
                return null;
            } else {
                TextElement<Integer> element = history.yearOfEra();
                return element.parse(text, status, attributes);
            }

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

            if (context instanceof HistoricCalendar) {
                HistoricCalendar hc = HistoricCalendar.class.cast(context);
                DualFormatElement element = DualFormatElement.class.cast(hc.history.yearOfEra());
                element.print(context, buffer, attributes, numsys, zeroChar, minDigits, maxDigits);
            } else {
                throw new ChronoException("Cannot cast to historic calendar: " + context);
            }

        }

        @Override
        public Integer parse(
            CharSequence text,
            ParsePosition status,
            AttributeQuery attributes,
            ChronoEntity<?> parsedResult
        ) {

            ChronoHistory history = getHistory(attributes);

            if (history == null) {
                return null;
            } else {
                DualFormatElement element = DualFormatElement.class.cast(history.yearOfEra());
                return element.parse(text, status, attributes, parsedResult);
            }

        }

        private Object readResolve() throws ObjectStreamException {

            return RELATED_STANDARD_YEAR;

        }

    }

    private static class MonthOperator
        implements ChronoOperator<HistoricCalendar> {

        //~ Instanzvariablen ----------------------------------------------

        private final boolean backwards;

        //~ Konstruktoren -------------------------------------------------

        MonthOperator(boolean backwards) {
            super();

            this.backwards = backwards;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public HistoricCalendar apply(HistoricCalendar cal) {

            HistoricEra era = cal.date.getEra();
            int yoe = cal.date.getYearOfEra();
            int month = cal.date.getMonth() + (this.backwards ? -1 : 1);
            int dom = cal.date.getDayOfMonth();

            if (month > 12) {
                month = 1;
                if (era == HistoricEra.BC) {
                    yoe--;
                    if (yoe == 0) {
                        era = HistoricEra.AD;
                        yoe = 1;
                    }
                } else {
                    yoe++;
                }
            } else if (month < 1) {
                month = 12;
                if (era == HistoricEra.BC) {
                    yoe++;
                } else {
                    yoe--;
                    if ((yoe == 0) && (era == HistoricEra.AD)) {
                        yoe = 1;
                        era = HistoricEra.BC;
                    }
                }
            }

            HistoricDate d = HistoricDate.of(era, yoe, month, dom);
            int original = dom;

            while ((dom > 1) && !cal.history.isValid(d)) {
                dom--; // takes into account different month lengths and most (but not all) gaps
                d = HistoricDate.of(era, yoe, month, dom);
            }

            if (dom == 1) { // special edge case if the first of month is within a gap on the date line
                dom = original;
                while ((dom <= 31) && !cal.history.isValid(d)) {
                    dom++;
                    d = HistoricDate.of(era, yoe, month, dom);
                }
            }

            return new HistoricCalendar(cal.history, d); // includes final validation

        }

    }

    private static class Merger
        implements ChronoMerger<HistoricCalendar> {

        //~ Methoden ------------------------------------------------------

        @Override
        public String getFormatPattern(
            DisplayStyle style,
            Locale locale
        ) {

            return GenericDatePatterns.get("generic", style, locale);

        }

        @Override
        public StartOfDay getDefaultStartOfDay() {

            return StartOfDay.MIDNIGHT;

        }

        @Override
        public int getDefaultPivotYear() {

            return PlainDate.axis().getDefaultPivotYear();

        }

        @Override
        public HistoricCalendar createFrom(
            TimeSource<?> clock,
            AttributeQuery attributes
        ) {

            String variant = attributes.get(Attributes.CALENDAR_VARIANT, "");

            if (variant.isEmpty()) {
                return null;
            }

            TZID tzid;

            if (attributes.contains(Attributes.TIMEZONE_ID)) {
                tzid = attributes.get(Attributes.TIMEZONE_ID);
            } else if (attributes.get(Attributes.LENIENCY, Leniency.SMART).isLax()) {
                tzid = Timezone.ofSystem().getID();
            } else {
                return null;
            }

            StartOfDay startOfDay = attributes.get(Attributes.START_OF_DAY, this.getDefaultStartOfDay());
            return Moment.from(clock.currentTime()).toGeneralTimestamp(ENGINE, variant, tzid, startOfDay).toDate();

        }

        @Override
        public HistoricCalendar createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean lenient,
            boolean preparsing
        ) {

            ChronoHistory history = getHistory(attributes);

            if (history == null) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Cannot find any calendar history.");
                return null;
            } else {
                if (entity.contains(ERA)) {
                    HistoricEra era = entity.get(ERA);
                    entity.with(ERA, null);
                    entity.with(history.era(), era);
                }
                if (entity.contains(RELATED_STANDARD_YEAR)) {
                    int yoe = entity.getInt(RELATED_STANDARD_YEAR);
                    entity.with(RELATED_STANDARD_YEAR, null);
                    entity.with(history.yearOfEra(), yoe);
                }
                if (entity.contains(DAY_OF_YEAR)) {
                    int doy = entity.getInt(DAY_OF_YEAR);
                    entity.with(DAY_OF_YEAR, null);
                    entity.with(history.dayOfYear(), doy);
                } else {
                    if (entity.contains(MONTH_OF_YEAR)) {
                        Month month = entity.get(MONTH_OF_YEAR);
                        entity.with(MONTH_OF_YEAR, null);
                        entity.with(history.month(), month.getValue());
                    }
                    if (entity.contains(DAY_OF_MONTH)) {
                        int dom = entity.getInt(DAY_OF_MONTH);
                        entity.with(DAY_OF_MONTH, null);
                        entity.with(history.dayOfMonth(), dom);
                    }
                }
            }

            HistoricExtension extension = new HistoricExtension();
            entity = extension.resolve(entity, history, attributes);

            if (entity.contains(PlainDate.COMPONENT)) {
                return new HistoricCalendar(history, entity.get(PlainDate.COMPONENT));
            } else {
                return null;
            }

        }

        @Override
        public ChronoDisplay preformat(HistoricCalendar context, AttributeQuery attributes) {

            return context;

        }

        @Override
        public Chronology<?> preparser() {

            return null;

        }

    }

}
