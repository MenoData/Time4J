/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HebrewCalendar.java) is part of project Time4J.
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
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.SystemClock;
import net.time4j.Weekday;
import net.time4j.Weekmodel;
import net.time4j.base.MathUtils;
import net.time4j.base.TimeSource;
import net.time4j.calendar.astro.SolarTime;
import net.time4j.calendar.service.GenericDatePatterns;
import net.time4j.calendar.service.StdEnumDateElement;
import net.time4j.calendar.service.StdIntegerDateElement;
import net.time4j.calendar.service.StdWeekdayElement;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.CalendarDays;
import net.time4j.engine.CalendarEra;
import net.time4j.engine.Calendrical;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoFunction;
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
import net.time4j.format.CalendarType;
import net.time4j.format.Leniency;
import net.time4j.format.LocalizedPatternSupport;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


/**
 * <p>Represents the calendar used by the worldwide Jewish community, but mainly in Israel for religious purposes. </p>
 *
 * <h4>Introduction</h4>
 *
 * <p>It is a lunisolar calendar which defines years consisting of 12 or 13 months. The month cycle
 * generally follows the lunar cycle of synodic moon. However, every two or three years, an extra
 * leap month called ADAR-I will be inserted to synchronize the calendar with the solar year. This
 * is done by help of the metonic cycle. This synchronization is not perfect because the mean length
 * of the hebrew year of 365.2468 days is slightly longer than the tropical solar year. See also
 * <a href="https://en.wikipedia.org/wiki/Hebrew_calendar">Wikipedia</a>. The implementation of this
 * calendar is based on the book &quot;Calendrical calculations&quot; of Dershowitz/Reingold. The
 * civil hebrew day starts at 18:00 o&#39;clock on the previous day. Time4J enables users to use
 * the sunset as begin of day for religious purposes. </p>
 *
 * <h4>Following elements which are declared as constants are registered by this class</h4>
 *
 * <ul>
 *  <li>{@link #DAY_OF_WEEK}</li>
 *  <li>{@link #DAY_OF_MONTH}</li>
 *  <li>{@link #DAY_OF_YEAR}</li>
 *  <li>{@link #WEEKDAY_IN_MONTH}</li>
 *  <li>{@link #MONTH_OF_YEAR}</li>
 *  <li>{@link #YEAR_OF_ERA}</li>
 *  <li>{@link #ERA}</li>
 * </ul>
 *
 * <p>Furthermore, all elements defined in {@code EpochDays} and {@link CommonElements} are supported. </p>
 *
 * <h4>Support for unicode ca-extensions</h4>
 *
 * <pre>
 *      Locale locale = Locale.forLanguageTag(&quot;en-u-ca-hebrew&quot;);
 *      ChronoFormatter&lt;CalendarDate&gt; f = ChronoFormatter.ofGenericCalendarStyle(DisplayMode.FULL, locale);
 *      assertThat(
 *          f.format(PlainDate.of(2017, 10, 1)),
 *          is(&quot;Sunday, Tishri 11, 5778 AM&quot;));
 * </pre>
 *
 * @author  Meno Hochschild
 * @since   3.37/4.32
 */
/*[deutsch]
 * <p>Repr&auml;sentiert den Kalender, der von der weltweitenj&uuml;dischen Gemeinschaft,
 * haupts&auml;chlich aber in Israel f&uuml;r religi&ouml;se Zwecke, verwendet wird. </p>
 *
 * <h4>Einleitung</h4>
 *
 * <p>Es handelt sich um einen kombinierten Sonnen- und Mondkalender, dessen Jahre aus 12 oder 13
 * Monaten bestehen. Grunds&auml;tzlich folgt der Monatszyklus dem Mondzyklus basierend auf dem
 * synodischen Mond. Mit Hilfe des metonischen Zyklus wird ungef&auml;hr alle 2 oder 3 Jahre ein
 * Schaltmonat namens ADAR-I eingef&uuml;gt, damit die L&auml;nge des hebr&auml;ischen Jahres an
 * die eines Sonnenjahres angeglichen wird. Die Angleichung ist jedoch nicht exakt, da im Vergleich
 * zum tropischen Sonnenjahr die mittlere L&auml;nge von 365,2468 Tagen minimal l&auml;nger ist. Siehe
 * auch <a href="https://en.wikipedia.org/wiki/Hebrew_calendar">Wikipedia</a>. Die Implementierung dieses
 * Kalenders st&uuml;tzt sich auf das Buch &quot;Calendrical calculations&quot; von Dershowitz/Reingold. Der
 * zivile hebr&auml;ische Tag f&auml;ngt um 18 Uhr am Vortag an. F&uuml;r religi&ouml;se Zwecke ist es im
 * Rahmen von Time4J auch m&ouml;glich, den exakten Sonnenuntergang des Vortags als Tagesbeginn zu berechnen. </p>
 *
 * <h4>Registriert sind folgende als Konstanten deklarierte Elemente</h4>
 *
 * <ul>
 *  <li>{@link #DAY_OF_WEEK}</li>
 *  <li>{@link #DAY_OF_MONTH}</li>
 *  <li>{@link #DAY_OF_YEAR}</li>
 *  <li>{@link #WEEKDAY_IN_MONTH}</li>
 *  <li>{@link #MONTH_OF_YEAR}</li>
 *  <li>{@link #YEAR_OF_ERA}</li>
 *  <li>{@link #ERA}</li>
 * </ul>
 *
 * <p>Au&slig;erdem werden alle Elemente von {@code EpochDays} und {@link CommonElements} unterst&uuml;tzt. </p>
 *
 * <h4>Unterst&uuml;tzung f&uuml;r Unicode-ca-Erweiterungen</h4>
 *
 * <pre>
 *      Locale locale = Locale.forLanguageTag(&quot;en-u-ca-hebrew&quot;);
 *      ChronoFormatter&lt;CalendarDate&gt; f = ChronoFormatter.ofGenericCalendarStyle(DisplayMode.FULL, locale);
 *      assertThat(
 *          f.format(PlainDate.of(2017, 10, 1)),
 *          is(&quot;Sunday, Tishri 11, 5778 AM&quot;));
 * </pre>
 *
 * @author  Meno Hochschild
 * @since   3.37/4.32
 */
@CalendarType("hebrew")
public final class HebrewCalendar
    extends Calendrical<HebrewCalendar.Unit, HebrewCalendar>
    implements LocalizedPatternSupport {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long FIXED_EPOCH = PlainDate.of(-3760, 9, 7).get(EpochDays.RATA_DIE);

    private static final int YEAR_INDEX = 0;
    private static final int DAY_OF_MONTH_INDEX = 2;
    private static final int DAY_OF_YEAR_INDEX = 3;

    /**
     * <p>Represents the Hebrew era. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die hebr&auml;ische &Auml;ra. </p>
     */
    @FormattableElement(format = "G")
    public static final ChronoElement<HebrewEra> ERA =
        new StdEnumDateElement<HebrewEra, HebrewCalendar>("ERA", HebrewCalendar.class, HebrewEra.class, 'G');

    /**
     * <p>Represents the Hebrew year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert das hebr&auml;ische Jahr. </p>
     */
    @FormattableElement(format = "y")
    public static final StdCalendarElement<Integer, HebrewCalendar> YEAR_OF_ERA =
        new StdIntegerDateElement<HebrewCalendar>(
            "YEAR_OF_ERA",
            HebrewCalendar.class,
            1,
            9999,
            'y',
            null,
            null);

    /**
     * <p>Represents the Hebrew month. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den hebr&auml;ischen Monat. </p>
     */
    @FormattableElement(format = "M", standalone = "L")
    public static final StdCalendarElement<HebrewMonth, HebrewCalendar> MONTH_OF_YEAR =
        new StdEnumDateElement<HebrewMonth, HebrewCalendar>(
            "MONTH_OF_YEAR",
            HebrewCalendar.class,
            HebrewMonth.class,
            'M'
        ) {
            @Override
            protected boolean hasLeapMonth(ChronoDisplay context) {
                return HebrewCalendar.isLeapYear(context.getInt(YEAR_OF_ERA));
            }
            @Override
            public int printToInt(
                HebrewMonth value,
                ChronoDisplay context,
                AttributeQuery attributes
            ) {
                switch (attributes.get(HebrewMonth.order(), HebrewMonth.Order.CIVIL)) {
                    case CIVIL:
                        return value.getCivilValue(this.hasLeapMonth(context));
                    case BIBILICAL:
                        return value.getBiblicalValue(this.hasLeapMonth(context));
                    default:
                        return this.numerical(value);
                }
            }
            @Override
            public boolean parseFromInt(
                ChronoEntity<?> entity,
                int value
            ) {
                if ((value >= 1) && (value <= 13)) {
                    entity.with(ParsedMonthElement.INSTANCE, value);
                    return true;
                }
                return false;
            }
        };

    /**
     * <p>Represents the Hebrew day of month. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den hebr&auml;ischen Tag des Monats. </p>
     */
    @FormattableElement(format = "d")
    public static final StdCalendarElement<Integer, HebrewCalendar> DAY_OF_MONTH =
        new StdIntegerDateElement<HebrewCalendar>("DAY_OF_MONTH", HebrewCalendar.class, 1, 30, 'd');

    /**
     * <p>Represents the Hebrew day of year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den hebr&auml;ischen Tag des Jahres. </p>
     */
    @FormattableElement(format = "D")
    public static final StdCalendarElement<Integer, HebrewCalendar> DAY_OF_YEAR =
        new StdIntegerDateElement<HebrewCalendar>("DAY_OF_YEAR", HebrewCalendar.class, 1, 355, 'D');

    /**
     * <p>Represents the Hebrew day of week. </p>
     *
     * <p>If the day-of-week is set to a new value then Time4J handles the Hebrew calendar week
     * as starting on Sunday (more precisely on Saturday evening). </p>
     *
     * @see     #getDefaultWeekmodel()
     * @see     CommonElements#localDayOfWeek(Chronology, Weekmodel)
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den hebr&auml;ischen Tag der Woche. </p>
     *
     * <p>Wenn der Tag der Woche auf einen neuen Wert gesetzt wird, behandelt Time4J die hebr&auml;ische
     * Kalenderwoche so, da&szlig; sie am Sonntag beginnt (genauer am Samstagabend). </p>
     *
     * @see     #getDefaultWeekmodel()
     * @see     CommonElements#localDayOfWeek(Chronology, Weekmodel)
     */
    @FormattableElement(format = "E")
    public static final StdCalendarElement<Weekday, HebrewCalendar> DAY_OF_WEEK =
        new StdWeekdayElement<HebrewCalendar>(HebrewCalendar.class, getDefaultWeekmodel());

    private static final WeekdayInMonthElement<HebrewCalendar> WIM_ELEMENT =
        new WeekdayInMonthElement<HebrewCalendar>(HebrewCalendar.class, DAY_OF_MONTH, DAY_OF_WEEK);

    /**
     * <p>Element with the ordinal day-of-week within given calendar month. </p>
     */
    /*[deutsch]
     * <p>Element mit dem x-ten Wochentag im Monat. </p>
     */
    @FormattableElement(format = "F")
    public static final OrdinalWeekdayElement<HebrewCalendar> WEEKDAY_IN_MONTH = WIM_ELEMENT;

    private static final EraYearMonthDaySystem<HebrewCalendar> CALSYS;
    private static final TimeAxis<HebrewCalendar.Unit, HebrewCalendar> ENGINE;

    static {
        CALSYS = new Transformer();

        TimeAxis.Builder<HebrewCalendar.Unit, HebrewCalendar> builder =
            TimeAxis.Builder.setUp(
                HebrewCalendar.Unit.class,
                HebrewCalendar.class,
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
                MONTH_OF_YEAR,
                new MonthRule(),
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
                CommonElements.RELATED_GREGORIAN_YEAR,
                new RelatedGregorianYearRule<HebrewCalendar>(CALSYS, DAY_OF_YEAR))
            .appendUnit(
                Unit.YEARS,
                new HebrewUnitRule(Unit.YEARS),
                Unit.YEARS.getLength(),
                Collections.singleton(Unit.MONTHS))
            .appendUnit(
                Unit.MONTHS,
                new HebrewUnitRule(Unit.MONTHS),
                Unit.MONTHS.getLength(),
                Collections.singleton(Unit.YEARS))
            .appendUnit(
                Unit.WEEKS,
                new HebrewUnitRule(Unit.WEEKS),
                Unit.WEEKS.getLength(),
                Collections.singleton(Unit.DAYS))
            .appendUnit(
                Unit.DAYS,
                new HebrewUnitRule(Unit.DAYS),
                Unit.DAYS.getLength(),
                Collections.singleton(Unit.WEEKS))
            .appendExtension(
                new CommonElements.Weekengine(
                    HebrewCalendar.class,
                    DAY_OF_MONTH,
                    DAY_OF_YEAR,
                    getDefaultWeekmodel()));
        ENGINE = builder.build();
    }

    // TODO: update
//    private static final long serialVersionUID = -8248846000788617742L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final int year;
    private transient final HebrewMonth month;
    private transient final int dom;

    //~ Konstruktoren -----------------------------------------------------

    private HebrewCalendar(
        int year,
        HebrewMonth month,
        int dom
    ) {
        super();

        this.year = year;
        this.month = month;
        this.dom = dom;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new instance of a Hebrew calendar date. </p>
     *
     * @param   year    Hebrew year in the range 1-9999
     * @param   month   Hebrew month
     * @param   dom     Hebrew day of month in range 1-30
     * @return  new instance of {@code HebrewCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    /*[deutsch]
     * <p>Erzeugt ein neues hebr&auml;isches Kalenderdatum. </p>
     *
     * @param   year    Hebrew year in the range 1-9999
     * @param   month   Hebrew month
     * @param   dom     Hebrew day of month in range 1-30
     * @return  new instance of {@code HebrewCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    public static HebrewCalendar of(
        int year,
        HebrewMonth month,
        int dom
    ) {

        int m = month.getValue();

        if (!CALSYS.isValid(HebrewEra.ANNO_MUNDI, year, m, dom)) {
            throw new IllegalArgumentException(
                "Invalid Hebrew date: year=" + year + ", month=" + month + ", day=" + dom);
        }

        return new HebrewCalendar(year, month, dom);

    }

    /**
     * <p>Creates a new instance of a Hebrew calendar date. </p>
     *
     * @param   year    Hebrew year in the range 1-9999
     * @param   month   Hebrew civil month in the range 1-13
     * @param   dom     Hebrew day of month in range 1-30
     * @return  new instance of {@code HebrewCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @see     HebrewMonth#getCivilValue(boolean)
     */
    /*[deutsch]
     * <p>Erzeugt ein neues hebr&auml;isches Kalenderdatum. </p>
     *
     * @param   year    Hebrew year in the range 1-9999
     * @param   month   Hebrew civil month in the range 1-13
     * @param   dom     Hebrew day of month in range 1-30
     * @return  new instance of {@code HebrewCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @see     HebrewMonth#getCivilValue(boolean)
     */
    public static HebrewCalendar ofCivil(
        int year,
        int month,
        int dom
    ) {

        HebrewMonth m = HebrewMonth.valueOfCivil(month, isLeapYear(year));
        return HebrewCalendar.of(year, m, dom);

    }

    /**
     * <p>Creates a new instance of a Hebrew calendar date. </p>
     *
     * @param   year    Hebrew year in the range 1-9999
     * @param   month   Hebrew biblical month in the range 1-13
     * @param   dom     Hebrew day of month in range 1-30
     * @return  new instance of {@code HebrewCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @see     HebrewMonth#getBiblicalValue(boolean)
     */
    /*[deutsch]
     * <p>Erzeugt ein neues hebr&auml;isches Kalenderdatum. </p>
     *
     * @param   year    Hebrew year in the range 1-9999
     * @param   month   Hebrew biblical month in the range 1-13
     * @param   dom     Hebrew day of month in range 1-30
     * @return  new instance of {@code HebrewCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @see     HebrewMonth#getBiblicalValue(boolean)
     */
    public static HebrewCalendar ofBiblical(
        int year,
        int month,
        int dom
    ) {

        HebrewMonth m = HebrewMonth.valueOfBiblical(month, isLeapYear(year));
        return HebrewCalendar.of(year, m, dom);

    }

    /**
     * <p>Obtains the current calendar date in system time. </p>
     *
     * <p>Convenient short-cut for: {@code SystemClock.inLocalView().now(HebrewCalendar.axis())}.
     * Attention: The Hebrew calendar changes the date in the evening at 6 PM (on previous day).
     * If users wish more control over the start of day then following code might be used: </p>
     *
     * <pre>
     *     SolarTime jerusalem = SolarTime.ofLocation(31.779167, 35.223611);
     *     HebrewCalendar currentHebrewDate =
     *          SystemClock.currentMoment().toGeneralTimestamp(
     *              HebrewCalendar.axis(),
     *              Timezone.ofSystem().getID(),
     *              StartOfDay.definedBy(jerusalem.sunset())
     *          ).toDate();
     * </pre>
     *
     * @return  current calendar date in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(Chronology)
     * @see     net.time4j.engine.StartOfDay#definedBy(ChronoFunction)
     * @see     SolarTime#sunset()
     */
    /*[deutsch]
     * <p>Ermittelt das aktuelle Kalenderdatum in der Systemzeit. </p>
     *
     * <p>Bequeme Abk&uuml;rzung f&uuml;r: {@code SystemClock.inLocalView().now(HebrewCalendar.axis())}.
     * Achtung: Der hebr&auml;ische Kalender wechselt das Datum am Abend des Vortags um 18 Uhr. Wenn
     * Anwender mehr Kontrolle &uuml;ber den Start des Tages w&uuml;nschen, dann k&ouml;nnen sie zum
     * Beispiel folgenden Code verwenden: </p>
     *
     * <pre>
     *     SolarTime jerusalem = SolarTime.ofLocation(31.779167, 35.223611);
     *     HebrewCalendar currentHebrewDate =
     *          SystemClock.currentMoment().toGeneralTimestamp(
     *              HebrewCalendar.axis(),
     *              Timezone.ofSystem().getID(),
     *              StartOfDay.definedBy(jerusalem.sunset())
     *          ).toDate();
     * </pre>
     *
     * @return  current calendar date in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(Chronology)
     * @see     net.time4j.engine.StartOfDay#definedBy(ChronoFunction)
     * @see     SolarTime#sunset()
     */
    public static HebrewCalendar nowInSystemTime() {

        return SystemClock.inLocalView().now(HebrewCalendar.axis());

    }

    /**
     * <p>Yields the Hebrew era. </p>
     *
     * @return  {@link HebrewEra#ANNO_MUNDI}
     */
    /*[deutsch]
     * <p>Liefert die hebr&auml;ische &Auml;ra. </p>
     *
     * @return  {@link HebrewEra#ANNO_MUNDI}
     */
    public HebrewEra getEra() {

        return HebrewEra.ANNO_MUNDI;

    }

    /**
     * <p>Yields the Hebrew year. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert das hebr&auml;ische Jahr. </p>
     *
     * @return  int
     */
    public int getYear() {

        return this.year;

    }

    /**
     * <p>Yields the Hebrew month. </p>
     *
     * @return  enum
     */
    /*[deutsch]
     * <p>Liefert den hebr&auml;ischen Monat. </p>
     *
     * @return  enum
     */
    public HebrewMonth getMonth() {

        return this.month;

    }

    /**
     * <p>Yields the Hebrew day of month. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert den hebr&auml;ischen Tag des Monats. </p>
     *
     * @return  int
     */
    public int getDayOfMonth() {

        return this.dom;

    }

    /**
     * <p>Determines the day of week. </p>
     *
     * <p>The Hebrew calendar also uses a 7-day-week. </p>
     *
     * @return  Weekday
     */
    /*[deutsch]
     * <p>Ermittelt den Wochentag. </p>
     *
     * <p>Der hebr&auml;ische Kalendar verwendet ebenfalls eine 7-Tage-Woche. </p>
     *
     * @return  Weekday
     */
    public Weekday getDayOfWeek() {

        long utcDays = CALSYS.transform(this);
        return Weekday.valueOf(MathUtils.floorModulo(utcDays + 5, 7) + 1);

    }

    /**
     * <p>Yields the Hebrew day of year. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert den hebr&auml;ischen Tag des Jahres. </p>
     *
     * @return  int
     */
    public int getDayOfYear() {

        return this.getInt(DAY_OF_YEAR);

    }

    /**
     * <p>Yields the length of current Hebrew month in days. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge des aktuellen hebr&auml;ischen Monats in Tagen. </p>
     *
     * @return  int
     */
    public int lengthOfMonth() {

        return lengthOfMonth(this.year, this.month);

    }

    /**
     * <p>Yields the length of current Hebrew year in days. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge des aktuellen hebr&auml;ischen Jahres in Tagen. </p>
     *
     * @return  int
     */
    public int lengthOfYear() {

        return lengthOfYear(this.year);

    }

    /**
     * <p>Is the given hebrew year a leap year which has 13 instead of 12 months? </p>
     *
     * <p>The years 3, 6, 8, 11, 14, 17 and 19 in the 19 year Metonic cycle are leap years. </p>
     *
     * @param   year    the hebrew year
     * @return  boolean
     * @throws  IllegalArgumentException if the year is not positive
     */
    /*[deutsch]
     * <p>Ist das angegebene hebr&auml;ische Jahr ein Schaltjahr, das 13 statt 12 Monate hat? </p>
     *
     * <p>Die Jahre 3, 6, 8, 11, 14, 17 und 19 im metonischen 19-Jahre-Zyklus sind Schaltjahre. </p>
     *
     * @param   year    the hebrew year
     * @return  boolean
     * @throws  IllegalArgumentException if the year is not positive
     */
    public static boolean isLeapYear(int year) {

        if (year < 0) {
            throw new IllegalArgumentException("Hebrew year is not positive: " + year);
        }

        return (((7 * year + 1) % 19) < 7);

    }

    /**
     * <p>Is the year of this date a leap year which has 13 instead of 12 months? </p>
     *
     * @return  boolean
     * @see     #isLeapYear(int)
     */
    /*[deutsch]
     * <p>Liegt dieses Datum in einem Schaltjahr, das 13 statt 12 Monate hat? </p>
     *
     * @return  boolean
     * @see     #isLeapYear(int)
     */
    public boolean isLeapYear() {

        return isLeapYear(this.year);

    }

    /**
     * <p>Is the year of this date a sabbatical year as described in the bible? </p>
     *
     * <p>Every seventh year is a sabbatical year, see Exodus 23:10-11. </p>
     *
     * @return  boolean
     */
    /*[deutsch]
     * <p>Liegt dieses Datum in einem Sabbatjahr wie in der Bibel beschrieben? </p>
     *
     * <p>Jedes siebente Jahr ist ein Sabbatjahr, siehe das zweite Buch Mose (Exodus 23:10-11). </p>
     *
     * @return  boolean
     */
    public boolean isSabbaticalYear() {

        return ((this.year % 7) == 0);

    }

    /**
     * <p>Queries if given parameter values form a well defined calendar date. </p>
     *
     * @param   yearOfEra   the year of era to be checked
     * @param   month       the month to be checked
     * @param   dayOfMonth  the day of month to be checked
     * @return  {@code true} if valid else  {@code false}
     * @see     #of(int, HebrewMonth, int)
     */
    /*[deutsch]
     * <p>Pr&uuml;ft, ob die angegebenen Parameter ein wohldefiniertes Kalenderdatum beschreiben. </p>
     *
     * @param   yearOfEra   the year of era to be checked
     * @param   month       the month to be checked
     * @param   dayOfMonth  the day of month to be checked
     * @return  {@code true} if valid else  {@code false}
     * @see     #of(int, HebrewMonth, int)
     */
    public static boolean isValid(
        int yearOfEra,
        HebrewMonth month,
        int dayOfMonth
    ) {

        return CALSYS.isValid(HebrewEra.ANNO_MUNDI, yearOfEra, month.getValue(), dayOfMonth);

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
    public GeneralTimestamp<HebrewCalendar> at(PlainTime time) {

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
    public GeneralTimestamp<HebrewCalendar> atTime(
        int hour,
        int minute
    ) {

        return this.at(PlainTime.of(hour, minute));

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof HebrewCalendar) {
            HebrewCalendar that = (HebrewCalendar) obj;
            return (
                (this.dom == that.dom)
                && (this.month == that.month)
                && (this.year == that.year)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return (17 * this.dom + 31 * this.month.getValue() + 37 * this.year);

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(32);
        sb.append("AM-");
        String y = String.valueOf(this.year);
        for (int i = y.length(); i < 4; i++) {
            sb.append('0');
        }
        sb.append(y);
        sb.append('-');
        sb.append(this.month.name());
        sb.append('-');
        if (this.dom < 10) {
            sb.append('0');
        }
        sb.append(this.dom);
        return sb.toString();

    }

    /**
     * <p>Obtains the standard week model of this calendar. </p>
     *
     * <p>The Hebrew calendar usually starts on Sunday (more precisely on Saturday evening).
     * Friday and Saturday are considered as weekend (as usual in Israel). </p>
     *
     * @return  Weekmodel
     */
    /*[deutsch]
     * <p>Ermittelt das Standardwochenmodell dieses Kalenders. </p>
     *
     * <p>Der hebr&auml;ische Kalender startet am Sonntag (genauer am Samstagabend). Freitag und Samstag
     * gelten als Wochenende (wie in Israel &uuml;blich). </p>
     *
     * @return  Weekmodel
     */
    public static Weekmodel getDefaultWeekmodel() {

        return Weekmodel.of(Weekday.SUNDAY, 1, Weekday.FRIDAY, Weekday.SATURDAY);

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
    public static TimeAxis<Unit, HebrewCalendar> axis() {

        return ENGINE;

    }

    @Override
    protected TimeAxis<Unit, HebrewCalendar> getChronology() {

        return ENGINE;

    }

    @Override
    protected HebrewCalendar getContext() {

        return this;

    }

    private static int lengthOfMonth(
        int year,
        HebrewMonth month
    ) {

        int ylen;

        switch (month) {
            case IYAR:
            case TAMUZ:
            case ELUL:
            case TEVET:
            case ADAR_II:
                return 29;
            case HESHVAN:
                ylen = lengthOfYear(year);
                return ((ylen == 355 || ylen == 385) ? 30 : 29);
            case KISLEV:
                ylen = lengthOfYear(year);
                return ((ylen == 353 || ylen == 383) ? 29 : 30);
            default:
                return 30;
        }
    }

    private static int lengthOfYear(int year) {

        return (int) (hcNewYear(year + 1)  - hcNewYear(year));

    }

    private static long hcNewYear(int year) {

        return FIXED_EPOCH + hcDelay1(year) + hcDelay2(year);

    }

    private static int hcDelay2(int year) {

        int y1 = hcDelay1(year);
        int y2 = hcDelay1(year + 1);
        return ((y2 - y1 == 356) ? 2 : ((y1 - hcDelay1(year - 1) == 382) ? 1 : 0));

    }

    private static int hcDelay1(int year) {

        int days = (int) Math.floor(hcMolad(year, HebrewMonth.TISHRI) - FIXED_EPOCH + 0.5);
        return (((3 * (days + 1)) % 7) < 3 ? days + 1 : days);

    }

    private static double hcMolad(
        int year,
        HebrewMonth month
    ) {

        int m = month.getValue() + 6;
        if (m > 13) {
            m -= 13;
        }
        int y = ((m < 7) ? year + 1 : year);
        long monthElapsed = m - 7 + MathUtils.floorDivide(235 * y - 234, 19);
        return FIXED_EPOCH - (876.0 / 25920.0) + monthElapsed * (29.5 + (793.0 / 25920.0));

    }

    /**
     * @serialData  Uses <a href="../../../serialized-form.html#net.time4j.calendar.SPX">
     *              a dedicated serialization form</a> as proxy. The first byte contains
     *              the type-ID {@code 12}. Then the year is written as int, finally
     *              {@code month.getValue()} and day-of-month as bytes.
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.HEBREW);

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
     * <p>Defines come calendar units for the Hebrew calendar. </p>
     */
    /*[deutsch]
     * <p>Definiert einige kalendarische Zeiteinheiten f&uuml;r den hebr&auml;ischen Kalender. </p>
     */
    public static enum Unit
        implements ChronoUnit {

        //~ Statische Felder/Initialisierungen ----------------------------

        YEARS(365.2468 * 86400.0),

        MONTHS(30 * 86400.0),

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
         * <p>Calculates the difference between given Hebrew dates in this unit. </p>
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
            HebrewCalendar start,
            HebrewCalendar end
        ) {

            return (int) start.until(end, this); // safe

        }

    }

    private static class Transformer
        implements EraYearMonthDaySystem<HebrewCalendar> {

        //~ Methoden ------------------------------------------------------

        @Override
        public boolean isValid(
            CalendarEra era,
            int yearOfEra,
            int monthOfYear,
            int dayOfMonth
        ) {

            return (
                (era == HebrewEra.ANNO_MUNDI)
                && (yearOfEra >= 1)
                && (yearOfEra <= 9999)
                && (monthOfYear >= 1)
                && (monthOfYear <= 13)
                && (dayOfMonth >= 1)
                && (dayOfMonth <= getLengthOfMonth(era, yearOfEra, monthOfYear))
            );

        }

        @Override
        public int getLengthOfMonth(
            CalendarEra era,
            int yearOfEra,
            int monthOfYear
        ) {

            if (era != HebrewEra.ANNO_MUNDI) {
                throw new IllegalArgumentException("Invalid era: " + era);
            }

            if (
                (yearOfEra >= 1)
                && (yearOfEra <= 9999)
                && (monthOfYear >= 1)
                && (monthOfYear <= 13)
            ) {
                return lengthOfMonth(yearOfEra, HebrewMonth.valueOf(monthOfYear));
            }

            throw new IllegalArgumentException(
                "Out of bounds: year=" + yearOfEra + ", month=" + HebrewMonth.valueOf(monthOfYear));

        }

        @Override
        public int getLengthOfYear(
            CalendarEra era,
            int yearOfEra
        ) {

            if (era != HebrewEra.ANNO_MUNDI) {
                throw new IllegalArgumentException("Invalid era: " + era);
            }

            if ((yearOfEra >= 1) && (yearOfEra <= 9999)) {
                return lengthOfYear(yearOfEra);
            }

            throw new IllegalArgumentException("Out of bounds: year=" + yearOfEra);

        }

        @Override
        public HebrewCalendar transform(long utcDays) {

            long fixedDays = EpochDays.RATA_DIE.transform(utcDays, EpochDays.UTC);
            int y = (int) MathUtils.floorDivide(98496 * (fixedDays - FIXED_EPOCH), 35975351);
            int year = y - 1;

            while (hcNewYear(y) <= fixedDays) {
                year = y;
                y++;
            }

            fixedDays -= (hcNewYear(year) - 1);
            boolean leap = isLeapYear(year);
            int month = 1;

            for (int m = 1; m < 13; m++) {
                if ((m == 6) && !leap) {
                    month = m + 1;
                    continue;
                }
                int len = lengthOfMonth(year, HebrewMonth.valueOf(m));
                if (fixedDays - len <= 0) {
                    break;
                } else {
                    fixedDays -= len;
                    month = m + 1;
                }
            }

            return HebrewCalendar.of(year, HebrewMonth.valueOf(month), (int) fixedDays);

        }

        @Override
        public long transform(HebrewCalendar date) {

            long utcDays = EpochDays.UTC.transform(hcNewYear(date.year), EpochDays.RATA_DIE) + date.dom - 1;
            boolean leap = isLeapYear(date.year);

            for (int m = 1, n = date.month.getValue(); m < n; m++) {
                if (leap || (m != 6)) {
                    utcDays += lengthOfMonth(date.year, HebrewMonth.valueOf(m));
                }
            }

            return utcDays;

        }

        @Override
        public long getMinimumSinceUTC() {

            HebrewCalendar min = new HebrewCalendar(1, HebrewMonth.TISHRI, 1);
            return this.transform(min);

        }

        @Override
        public long getMaximumSinceUTC() {

            HebrewCalendar max = new HebrewCalendar(9999, HebrewMonth.ELUL, 29);
            return this.transform(max);

        }

        @Override
        public List<CalendarEra> getEras() {

            CalendarEra era = HebrewEra.ANNO_MUNDI;
            return Collections.singletonList(era);

        }

    }

    private static class IntegerRule
        implements IntElementRule<HebrewCalendar> {

        //~ Instanzvariablen ----------------------------------------------

        private final int index;

        //~ Konstruktoren -------------------------------------------------

        IntegerRule(int index) {
            super();

            this.index = index;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Integer getValue(HebrewCalendar context) {

            return Integer.valueOf(this.getInt(context));

        }

        @Override
        public Integer getMinimum(HebrewCalendar context) {

            switch (this.index) {
                case YEAR_INDEX:
                case DAY_OF_MONTH_INDEX:
                case DAY_OF_YEAR_INDEX:
                    return Integer.valueOf(1);
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public Integer getMaximum(HebrewCalendar context) {

            return Integer.valueOf(this.getMax(context));

        }

        @Override
        public boolean isValid(
            HebrewCalendar context,
            Integer value
        ) {

            if (value == null) {
                return false;
            }

            Integer min = this.getMinimum(context);
            Integer max = this.getMaximum(context);
            return ((min.compareTo(value) <= 0) && (max.compareTo(value) >= 0));

        }

        @Override
        public HebrewCalendar withValue(
            HebrewCalendar context,
            Integer value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing new value.");
            }

            return this.withValue(context, value.intValue(), lenient);

        }

        @Override
        public ChronoElement<?> getChildAtFloor(HebrewCalendar context) {

            if (this.index == YEAR_INDEX) {
                return MONTH_OF_YEAR;
            }

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(HebrewCalendar context) {

            if (this.index == YEAR_INDEX) {
                return MONTH_OF_YEAR;
            }

            return null;

        }

        @Override
        public int getInt(HebrewCalendar context) {

            switch (this.index) {
                case YEAR_INDEX:
                    return context.year;
                case DAY_OF_MONTH_INDEX:
                    return context.dom;
                case DAY_OF_YEAR_INDEX:
                    int doy = 0;
                    for (int m = 1; m < context.month.getValue(); m++) {
                        doy += CALSYS.getLengthOfMonth(HebrewEra.ANNO_MUNDI, context.year, m);
                    }
                    return doy + context.dom;
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public boolean isValid(
            HebrewCalendar context,
            int value
        ) {

            return ((value <= this.getMax(context)) && (1 <= value));

        }

        @Override
        public HebrewCalendar withValue(
            HebrewCalendar context,
            int value,
            boolean lenient
        ) {

            if (!this.isValid(context, value)) {
                throw new IllegalArgumentException("Out of range: " + value);
            }

            switch (this.index) {
                case YEAR_INDEX:
                    int dmax = CALSYS.getLengthOfMonth(HebrewEra.ANNO_MUNDI, value, context.month.getValue());
                    int d = Math.min(context.dom, dmax);
                    return HebrewCalendar.of(value, context.getMonth(), d);
                case DAY_OF_MONTH_INDEX:
                    return new HebrewCalendar(context.year, context.month, value);
                case DAY_OF_YEAR_INDEX:
                    int delta = value - this.getInt(context);
                    return context.plus(CalendarDays.of(delta));
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        private int getMax(HebrewCalendar context) {

            switch (this.index) {
                case YEAR_INDEX:
                    return 9999;
                case DAY_OF_MONTH_INDEX:
                    return lengthOfMonth(context.year, context.month);
                case DAY_OF_YEAR_INDEX:
                    return lengthOfYear(context.year);
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

    }

    private static class MonthRule
        implements ElementRule<HebrewCalendar, HebrewMonth> {

        //~ Methoden ------------------------------------------------------

        @Override
        public HebrewMonth getValue(HebrewCalendar context) {

            return context.month;

        }

        @Override
        public HebrewMonth getMinimum(HebrewCalendar context) {

            return HebrewMonth.TISHRI;

        }

        @Override
        public HebrewMonth getMaximum(HebrewCalendar context) {

            return HebrewMonth.ELUL;

        }

        @Override
        public boolean isValid(
            HebrewCalendar context,
            HebrewMonth value
        ) {

            return ((value != null) && ((value != HebrewMonth.ADAR_I) || context.isLeapYear()));

        }

        @Override
        public HebrewCalendar withValue(
            HebrewCalendar context,
            HebrewMonth value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing month.");
            } else if ((value == HebrewMonth.ADAR_I) && !context.isLeapYear()) {
                throw new IllegalArgumentException("ADAR-I cannot be set in a standard year: " + context);
            }

            int dmax = lengthOfMonth(context.year, value);
            int d = Math.min(context.dom, dmax);
            return new HebrewCalendar(context.year, value, d);

        }

        @Override
        public ChronoElement<?> getChildAtFloor(HebrewCalendar context) {

            return DAY_OF_MONTH;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(HebrewCalendar context) {

            return DAY_OF_MONTH;

        }

    }

    private static class EraRule
        implements ElementRule<HebrewCalendar, HebrewEra> {

        //~ Methoden ------------------------------------------------------

        @Override
        public HebrewEra getValue(HebrewCalendar context) {

            return HebrewEra.ANNO_MUNDI;

        }

        @Override
        public HebrewEra getMinimum(HebrewCalendar context) {

            return HebrewEra.ANNO_MUNDI;

        }

        @Override
        public HebrewEra getMaximum(HebrewCalendar context) {

            return HebrewEra.ANNO_MUNDI;

        }

        @Override
        public boolean isValid(
            HebrewCalendar context,
            HebrewEra value
        ) {

            return (value != null);

        }

        @Override
        public HebrewCalendar withValue(
            HebrewCalendar context,
            HebrewEra value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing era value.");
            }

            return context;

        }

        @Override
        public ChronoElement<?> getChildAtFloor(HebrewCalendar context) {

            return YEAR_OF_ERA;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(HebrewCalendar context) {

            return YEAR_OF_ERA;

        }

    }

    private static class WeekdayRule
        implements ElementRule<HebrewCalendar, Weekday> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Weekday getValue(HebrewCalendar context) {

            return context.getDayOfWeek();

        }

        @Override
        public Weekday getMinimum(HebrewCalendar context) {

            return Weekday.SUNDAY;

        }

        @Override
        public Weekday getMaximum(HebrewCalendar context) {

            return Weekday.SATURDAY;

        }

        @Override
        public boolean isValid(
            HebrewCalendar context,
            Weekday value
        ) {

            return (value != null);

        }

        @Override
        public HebrewCalendar withValue(
            HebrewCalendar context,
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
        public ChronoElement<?> getChildAtFloor(HebrewCalendar context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(HebrewCalendar context) {

            return null;

        }

    }

    private static enum ParsedMonthElement
        implements ChronoElement<Integer> {

        INSTANCE;

        //~ Methoden ------------------------------------------------------

        @Override
        public Class<Integer> getType() {
            return Integer.class;
        }

        @Override
        public char getSymbol() {
            return '\u0000';
        }

        @Override
        public int compare(
            ChronoDisplay o1,
            ChronoDisplay o2
        ) {
            return o1.get(this).compareTo(o2.get(this));
        }

        @Override
        public Integer getDefaultMinimum() {
            return Integer.valueOf(1);
        }

        @Override
        public Integer getDefaultMaximum() {
            return Integer.valueOf(13);
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
            return this.name();
        }

    }

    private static class Merger
        implements ChronoMerger<HebrewCalendar> {

        //~ Methoden ------------------------------------------------------

        @Override
        public String getFormatPattern(
            DisplayStyle style,
            Locale locale
        ) {

            return GenericDatePatterns.get("hebrew", style, locale);

        }

        @Override
        public HebrewCalendar createFrom(
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
        public HebrewCalendar createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean preparsing
        ) {

            boolean lenient = attributes.get(Attributes.LENIENCY, Leniency.SMART).isLax();
            return this.createFrom(entity, attributes, lenient, preparsing);

        }

        @Override
        public HebrewCalendar createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean lenient,
            boolean preparsing
        ) {

            int year = entity.getInt(YEAR_OF_ERA);

            if (year == Integer.MIN_VALUE) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Missing Hebrew year.");
                return null;
            }

            HebrewMonth month = null;

            if (entity.contains(ParsedMonthElement.INSTANCE)) {
                int m = entity.getInt(ParsedMonthElement.INSTANCE);
                switch (attributes.get(HebrewMonth.order(), HebrewMonth.Order.CIVIL)) {
                    case CIVIL:
                        month = HebrewMonth.valueOfCivil(m, isLeapYear(year));
                        break;
                    case BIBILICAL:
                        month = HebrewMonth.valueOfBiblical(m, isLeapYear(year));
                        break;
                    default:
                        month = HebrewMonth.valueOf(m);
                }
            } else if (entity.contains(MONTH_OF_YEAR)) {
                month = entity.get(MONTH_OF_YEAR);
            }

            if (month != null) {
                int dom = entity.getInt(DAY_OF_MONTH);

                if (dom != Integer.MIN_VALUE) {
                    if (CALSYS.isValid(HebrewEra.ANNO_MUNDI, year, month.getValue(), dom)) {
                        return HebrewCalendar.of(year, month, dom);
                    } else {
                        entity.with(ValidationElement.ERROR_MESSAGE, "Invalid Hebrew date.");
                    }
                }
            } else {
                int doy = entity.getInt(DAY_OF_YEAR);
                if (doy != Integer.MIN_VALUE) {
                    if (doy > 0) {
                        int m = 1;
                        int daycount = 0;
                        boolean leap = isLeapYear(year);
                        while (m <= 13) {
                            if ((m == 6) && !leap) {
                                continue;
                            }
                            int len = lengthOfMonth(year, HebrewMonth.valueOf(m));
                            if (doy > daycount + len) {
                                m++;
                                daycount += len;
                            } else {
                                return HebrewCalendar.of(year, HebrewMonth.valueOf(m), doy - daycount);
                            }
                        }
                    }
                    entity.with(ValidationElement.ERROR_MESSAGE, "Invalid Hebrew date.");
                }
            }

            return null;

        }

        @Override
        public ChronoDisplay preformat(HebrewCalendar context, AttributeQuery attributes) {

            return context;

        }

        @Override
        public Chronology<?> preparser() {

            return null;

        }

        @Override
        public StartOfDay getDefaultStartOfDay() {

            return StartOfDay.EVENING;

        }

        @Override
        public int getDefaultPivotYear() {

            return HebrewCalendar.nowInSystemTime().getYear() + 20;

        }

    }

    private static class HebrewUnitRule
        implements UnitRule<HebrewCalendar> {

        //~ Instanzvariablen ----------------------------------------------

        private final Unit unit;

        //~ Konstruktoren -------------------------------------------------

        HebrewUnitRule(Unit unit) {
            super();

            this.unit = unit;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public HebrewCalendar addTo(HebrewCalendar date, long amount) {

            switch (this.unit) {
                case YEARS:
                    int y = MathUtils.safeCast(MathUtils.safeAdd(date.year, amount));
                    if ((y < 1) || (y > 9999)) {
                        throw new IllegalArgumentException("Resulting year out of bounds: " + y);
                    }
                    HebrewMonth m = date.month;
                    if ((m == HebrewMonth.ADAR_I) && !isLeapYear(y)) {
                        m = HebrewMonth.SHEVAT; // one month backwards
                    }
                    int d = Math.min(date.dom, lengthOfMonth(y, m));
                    return new HebrewCalendar(y, m, d);
                case MONTHS:
                    int yNum = date.year;
                    int mNum = date.month.getValue();
                    for (long i = Math.abs(amount); i > 0; i--) {
                        if (amount > 0) {
                            mNum++;
                            if ((mNum == 6) && !isLeapYear(yNum)) {
                                mNum++;
                            } else if (mNum == 14) {
                                mNum = 1;
                                yNum++;
                            }
                        } else {
                            mNum--;
                            if ((mNum == 6) && !isLeapYear(yNum)) {
                                mNum--;
                            } else if (mNum == 0) {
                                mNum = 13;
                                yNum--;
                            }
                        }
                    }
                    HebrewMonth newMonth = HebrewMonth.valueOf(mNum);
                    int dAdjusted = Math.min(date.dom, lengthOfMonth(yNum, newMonth));
                    return HebrewCalendar.of(yNum, newMonth, dAdjusted);
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
        public long between(HebrewCalendar start, HebrewCalendar end) {

            switch (this.unit) {
                case YEARS:
                    int yDelta = end.year - start.year;
                    if (yDelta > 0) {
                        if (end.month.getValue() < start.month.getValue()) {
                            yDelta--;
                        } else if ((end.month.getValue() == start.month.getValue()) && (end.dom < start.dom)) {
                            yDelta--;
                        }
                    } else if (yDelta < 0) {
                        if (end.month.getValue() > start.month.getValue()) {
                            yDelta++;
                        } else if ((end.month.getValue() == start.month.getValue()) && (end.dom > start.dom)) {
                            yDelta++;
                        }
                    }
                    return yDelta;
                case MONTHS:
                    HebrewCalendar s = start;
                    HebrewCalendar e = end;
                    boolean negative = false;
                    if (start.isAfter(end)) {
                        s = end;
                        e = start;
                        negative = true;
                    }
                    int delta = 0;
                    int y = s.year;
                    int m = s.month.getValue();
                    while ((y < e.year) || ((y == e.year) && (m < e.month.getValue()))) {
                        m++;
                        delta++;
                        if ((m == 6) && !isLeapYear(y)) {
                            m++;
                        } else if (m == 14) {
                            m = 1;
                            y++;
                        }
                    }
                    if ((delta > 0) && (s.dom > e.dom)) {
                        delta--;
                    }
                    return (negative ? -delta : delta);
                case WEEKS:
                    return HebrewCalendar.Unit.DAYS.between(start, end) / 7;
                case DAYS:
                    return CALSYS.transform(end) - CALSYS.transform(start);
                default:
                    throw new UnsupportedOperationException(this.unit.name());
            }

        }

    }

}
