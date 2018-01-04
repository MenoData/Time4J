/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CopticCalendar.java) is part of project Time4J.
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
import net.time4j.calendar.service.GenericDatePatterns;
import net.time4j.calendar.service.StdEnumDateElement;
import net.time4j.calendar.service.StdIntegerDateElement;
import net.time4j.calendar.service.StdWeekdayElement;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.CalendarDays;
import net.time4j.engine.CalendarEra;
import net.time4j.engine.CalendarSystem;
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
import net.time4j.engine.StartOfDay;
import net.time4j.engine.TimeAxis;
import net.time4j.engine.UnitRule;
import net.time4j.engine.ValidationElement;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarType;
import net.time4j.format.Leniency;
import net.time4j.format.LocalizedPatternSupport;
import net.time4j.history.ChronoHistory;
import net.time4j.history.HistoricDate;
import net.time4j.history.HistoricEra;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


/**
 * <p>Represents the calendar used by the Coptic church in Egypt. </p>
 *
 * <p>It is a solar calendar which defines years consisting of 13 months. The first 12 months are always 30 days long.
 * The last month has 5 or 6 days depending if a Coptic year is a leap year or not. The leap year rule is the same
 * as defined in Julian Calendar, namely every fourth year. Years are counted since the era of martyrs where
 * the Julian year AD 284 is counted as Coptic year 1. See also
 * <a href="https://en.wikipedia.org/wiki/Coptic_calendar">Wikipedia</a>. According to the book
 * &quot;Calendrical calculations&quot; of Dershowitz/Reingold, the Coptic day starts at sunset
 * on the previous day. Time4J will also assume that despite of the fact that the ancient Egypt
 * calendar (the historic ancestor of the Coptic calendar) started the day at sunrise. We assume
 * here an adaptation of the Coptic calendar to the habits of Islamic calendar in Egypt. </p>
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
 *  <li>{@link #YEAR_OF_ERA}</li>
 *  <li>{@link #ERA}</li>
 * </ul>
 *
 * <p>Furthermore, all elements defined in {@code EpochDays} and {@link CommonElements} are supported. </p>
 *
 * <p>Example of usage: </p>
 *
 * <pre>
 *     ChronoFormatter&lt;CopticCalendar&gt; formatter =
 *       ChronoFormatter.setUp(CopticCalendar.axis(), Locale.ENGLISH)
 *       .addPattern(&quot;EEE, d. MMMM yyyy&quot;, PatternType.NON_ISO_DATE).build();
 *     PlainDate today = SystemClock.inLocalView().today();
 *     CopticCalendar copticDate = today.transform(CopticCalendar.class); // conversion at noon
 *     System.out.println(formatter.format(copticDate));
 * </pre>
 *
 * @author  Meno Hochschild
 * @since   3.11/4.8
 */
/*[deutsch]
 * <p>Repr&auml;sentiert den Kalender, der von der koptischen Kirche in &Auml;gypten verwendet wird. </p>
 *
 * <p>Es handelt sich um einen Sonnenkalender, dessen Jahre aus 13 Monaten bestehen. Die ersten 12 Monate
 * sind immer 30 Tage lang, w&auml;hrend der letzte Monat 5 oder 6 Tage lang ist, je nachdem ob ein Schaltjahr
 * vorliegt oder nicht. Die Schaltjahrregel ist die gleiche wie im julianischen Kalender, n&auml;mlich
 * alle 4 Jahre. Jahre werden seit der &Auml;ra der M&auml;rtyrer gez&auml;hlt, also seit dem julianischen
 * Jahr AD 284. Siehe auch <a href="https://en.wikipedia.org/wiki/Coptic_calendar">Wikipedia</a>. Nach dem
 * Buch &quot;Calendrical calculations&quot; von Dershowitz/Reingold f&auml;ngt der koptische Tag zum
 * Sonnenuntergang des Vortags an. Time4J wird das auch annehmen, obwohl der alte &auml;gyptische Kalender,
 * von dem der koptische Kalender abgeleitet ist, den Tag zum Sonnenaufgang begann. Es wird hier implizit
 * eine Anpassung des koptischen Kalenders an die Gewohnheiten des islamischen Kalenders in &Auml;gypten
 * angenommen. </p>
 *
 * <p>Registriert sind folgende als Konstanten deklarierte Elemente: </p>
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
 * <p>Anwendungsbeispiel: </p>
 *
 * <pre>
 *     ChronoFormatter&lt;CopticCalendar&gt; formatter =
 *       ChronoFormatter.setUp(CopticCalendar.axis(), Locale.ENGLISH)
 *       .addPattern(&quot;EEE, d. MMMM yyyy&quot;, PatternType.NON_ISO_DATE).build();
 *     PlainDate today = SystemClock.inLocalView().today();
 *     CopticCalendar copticDate = today.transform(CopticCalendar.class); // Konversion zu 12 Uhr mittags
 *     System.out.println(formatter.format(copticDate));
 * </pre>
 *
 * @author  Meno Hochschild
 * @since   3.11/4.8
 */
@CalendarType("coptic")
public final class CopticCalendar
    extends Calendrical<CopticCalendar.Unit, CopticCalendar>
    implements LocalizedPatternSupport {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long DIOCLETIAN;

    static {
        PlainDate diocletian = ChronoHistory.PROLEPTIC_JULIAN.convert(HistoricDate.of(HistoricEra.AD, 284, 8, 29));
        DIOCLETIAN = diocletian.get(EpochDays.UTC);
    }

    private static final int YEAR_INDEX = 0;
    private static final int DAY_OF_MONTH_INDEX = 2;
    private static final int DAY_OF_YEAR_INDEX = 3;

    /**
     * <p>Represents the Coptic era. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die koptische &Auml;ra. </p>
     */
    @FormattableElement(format = "G")
    public static final ChronoElement<CopticEra> ERA =
        new StdEnumDateElement<CopticEra, CopticCalendar>(
            "ERA", CopticCalendar.class, CopticEra.class, 'G');

    /**
     * <p>Represents the Coptic year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert das koptische Jahr. </p>
     */
    @FormattableElement(format = "y")
    public static final StdCalendarElement<Integer, CopticCalendar> YEAR_OF_ERA =
        new StdIntegerDateElement<CopticCalendar>(
            "YEAR_OF_ERA",
            CopticCalendar.class,
            1,
            9999,
            'y',
            null,
            null);

    /**
     * <p>Represents the Coptic month. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den koptischen Monat. </p>
     */
    @FormattableElement(format = "M", standalone = "L")
    public static final StdCalendarElement<CopticMonth, CopticCalendar> MONTH_OF_YEAR =
        new StdEnumDateElement<CopticMonth, CopticCalendar>(
            "MONTH_OF_YEAR",
            CopticCalendar.class,
            CopticMonth.class,
            'M');

    /**
     * <p>Represents the Coptic day of month. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den koptischen Tag des Monats. </p>
     */
    @FormattableElement(format = "d")
    public static final StdCalendarElement<Integer, CopticCalendar> DAY_OF_MONTH =
        new StdIntegerDateElement<CopticCalendar>("DAY_OF_MONTH", CopticCalendar.class, 1, 30, 'd');

    /**
     * <p>Represents the Coptic day of year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den koptischen Tag des Jahres. </p>
     */
    @FormattableElement(format = "D")
    public static final StdCalendarElement<Integer, CopticCalendar> DAY_OF_YEAR =
        new StdIntegerDateElement<CopticCalendar>("DAY_OF_YEAR", CopticCalendar.class, 1, 365, 'D');

    /**
     * <p>Represents the Coptic day of week. </p>
     *
     * <p>If the day-of-week is set to a new value then Time4J handles the Coptic calendar week
     * as starting on Saturday (like in Egypt). </p>
     *
     * @see     #getDefaultWeekmodel()
     * @see     CommonElements#localDayOfWeek(Chronology, Weekmodel)
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den koptischen Tag der Woche. </p>
     *
     * <p>Wenn der Tag der Woche auf einen neuen Wert gesetzt wird, behandelt Time4J die koptische
     * Kalenderwoche so, da&szlig; sie am Samstag beginnt (wie in &Auml;gypten). </p>
     *
     * @see     #getDefaultWeekmodel()
     * @see     CommonElements#localDayOfWeek(Chronology, Weekmodel)
     */
    @FormattableElement(format = "E")
    public static final StdCalendarElement<Weekday, CopticCalendar> DAY_OF_WEEK =
        new StdWeekdayElement<CopticCalendar>(CopticCalendar.class, getDefaultWeekmodel());

    private static final WeekdayInMonthElement<CopticCalendar> WIM_ELEMENT =
        new WeekdayInMonthElement<CopticCalendar>(CopticCalendar.class, DAY_OF_MONTH, DAY_OF_WEEK);

    /**
     * <p>Element with the ordinal day-of-week within given calendar month. </p>
     */
    /*[deutsch]
     * <p>Element mit dem x-ten Wochentag im Monat. </p>
     */
    @FormattableElement(format = "F")
    public static final OrdinalWeekdayElement<CopticCalendar> WEEKDAY_IN_MONTH = WIM_ELEMENT;

    private static final EraYearMonthDaySystem<CopticCalendar> CALSYS;
    private static final TimeAxis<CopticCalendar.Unit, CopticCalendar> ENGINE;

    static {
        CALSYS = new Transformer();

        TimeAxis.Builder<CopticCalendar.Unit, CopticCalendar> builder =
            TimeAxis.Builder.setUp(
                CopticCalendar.Unit.class,
                CopticCalendar.class,
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
                new WeekdayRule<CopticCalendar>(
                    getDefaultWeekmodel(),
                    new ChronoFunction<CopticCalendar, CalendarSystem<CopticCalendar>>() {
                        @Override
                        public CalendarSystem<CopticCalendar> apply(CopticCalendar context) {
                            return context.getChronology().getCalendarSystem();
                        }
                    }
                ),
                Unit.DAYS)
            .appendElement(
                WIM_ELEMENT,
                WeekdayInMonthElement.getRule(WIM_ELEMENT))
            .appendElement(
                CommonElements.RELATED_GREGORIAN_YEAR,
                new RelatedGregorianYearRule<CopticCalendar>(CALSYS, DAY_OF_YEAR))
            .appendUnit(
                Unit.YEARS,
                new CopticUnitRule(Unit.YEARS),
                Unit.YEARS.getLength(),
                Collections.singleton(Unit.MONTHS))
            .appendUnit(
                Unit.MONTHS,
                new CopticUnitRule(Unit.MONTHS),
                Unit.MONTHS.getLength(),
                Collections.singleton(Unit.YEARS))
            .appendUnit(
                Unit.WEEKS,
                new CopticUnitRule(Unit.WEEKS),
                Unit.WEEKS.getLength(),
                Collections.singleton(Unit.DAYS))
            .appendUnit(
                Unit.DAYS,
                new CopticUnitRule(Unit.DAYS),
                Unit.DAYS.getLength(),
                Collections.singleton(Unit.WEEKS))
            .appendExtension(
                new CommonElements.Weekengine(
                    CopticCalendar.class,
                    DAY_OF_MONTH,
                    DAY_OF_YEAR,
                    getDefaultWeekmodel()));
        ENGINE = builder.build();
    }

    private static final long serialVersionUID = -8248846000788617742L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final int cyear;
    private transient final int cmonth;
    private transient final int cdom;

    //~ Konstruktoren -----------------------------------------------------

    private CopticCalendar(
        int cyear,
        int cmonth,
        int cdom
    ) {
        super();

        this.cyear = cyear;
        this.cmonth = cmonth;
        this.cdom = cdom;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new instance of a Coptic calendar date. </p>
     *
     * @param   cyear   Coptic year in the range 1-9999
     * @param   cmonth  Coptic month
     * @param   cdom    Coptic day of month in the range 1-30
     * @return  new instance of {@code CopticCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Erzeugt ein neues koptisches Kalenderdatum. </p>
     *
     * @param   cyear   Coptic year in the range 1-9999
     * @param   cmonth  Coptic month
     * @param   cdom    Coptic day of month in the range 1-30
     * @return  new instance of {@code CopticCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.11/4.8
     */
    public static CopticCalendar of(
        int cyear,
        CopticMonth cmonth,
        int cdom
    ) {

        return CopticCalendar.of(cyear, cmonth.getValue(), cdom);

    }

    /**
     * <p>Creates a new instance of a Coptic calendar date. </p>
     *
     * @param   cyear   Coptic year in the range 1-9999
     * @param   cmonth  Coptic month in the range 1-13
     * @param   cdom    Coptic day of month in the range 1-30
     * @return  new instance of {@code CopticCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Erzeugt ein neues koptisches Kalenderdatum. </p>
     *
     * @param   cyear   Coptic year in the range 1-9999
     * @param   cmonth  Coptic month in the range 1-13
     * @param   cdom    Coptic day of month in the range 1-30
     * @return  new instance of {@code CopticCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.11/4.8
     */
    public static CopticCalendar of(
        int cyear,
        int cmonth,
        int cdom
    ) {

        if (!CALSYS.isValid(CopticEra.ANNO_MARTYRUM, cyear, cmonth, cdom)) {
            throw new IllegalArgumentException(
                "Invalid Coptic date: year=" + cyear + ", month=" + cmonth + ", day=" + cdom);
        }

        return new CopticCalendar(cyear, cmonth, cdom);

    }

    /**
     * <p>Obtains the current calendar date in system time. </p>
     *
     * <p>Convenient short-cut for: {@code SystemClock.inLocalView().now(CopticCalendar.axis())}.
     * Attention: The Coptic calendar changes the date in the evening at 6 PM. </p>
     *
     * @return  current calendar date in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(net.time4j.engine.Chronology)
     * @since   3.23/4.19
     */
    /*[deutsch]
     * <p>Ermittelt das aktuelle Kalenderdatum in der Systemzeit. </p>
     *
     * <p>Bequeme Abk&uuml;rzung f&uuml;r: {@code SystemClock.inLocalView().now(CopticCalendar.axis())}.
     * Achtung: Der koptische Kalender wechselt das Datum am Abend um 18 Uhr. </p>
     *
     * @return  current calendar date in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(net.time4j.engine.Chronology)
     * @since   3.23/4.19
     */
    public static CopticCalendar nowInSystemTime() {

        return SystemClock.inLocalView().now(CopticCalendar.axis());

    }

    /**
     * <p>Yields the Coptic era. </p>
     *
     * @return  {@link CopticEra#ANNO_MARTYRUM}
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Liefert die koptische &Auml;ra. </p>
     *
     * @return  {@link CopticEra#ANNO_MARTYRUM}
     * @since   3.11/4.8
     */
    public CopticEra getEra() {

        return CopticEra.ANNO_MARTYRUM;

    }

    /**
     * <p>Yields the Coptic year. </p>
     *
     * @return  int
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Liefert das koptische Jahr. </p>
     *
     * @return  int
     * @since   3.11/4.8
     */
    public int getYear() {

        return this.cyear;

    }

    /**
     * <p>Yields the Coptic month. </p>
     *
     * @return  enum
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Liefert den koptischen Monat. </p>
     *
     * @return  enum
     * @since   3.11/4.8
     */
    public CopticMonth getMonth() {

        return CopticMonth.valueOf(this.cmonth);

    }

    /**
     * <p>Yields the Coptic day of month. </p>
     *
     * @return  int
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Liefert den koptischen Tag des Monats. </p>
     *
     * @return  int
     * @since   3.11/4.8
     */
    public int getDayOfMonth() {

        return this.cdom;

    }

    /**
     * <p>Determines the day of week. </p>
     *
     * <p>The Coptic calendar also uses a 7-day-week. </p>
     *
     * @return  Weekday
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Ermittelt den Wochentag. </p>
     *
     * <p>Der koptische Kalendar verwendet ebenfalls eine 7-Tage-Woche. </p>
     *
     * @return  Weekday
     * @since   3.11/4.8
     */
    public Weekday getDayOfWeek() {

        long utcDays = CALSYS.transform(this);
        return Weekday.valueOf(MathUtils.floorModulo(utcDays + 5, 7) + 1);

    }

    /**
     * <p>Yields the Coptic day of year. </p>
     *
     * @return  int
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Liefert den koptischen Tag des Jahres. </p>
     *
     * @return  int
     * @since   3.11/4.8
     */
    public int getDayOfYear() {

        return this.get(DAY_OF_YEAR).intValue();

    }

    /**
     * <p>Yields the length of current Coptic month in days. </p>
     *
     * @return  int
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge des aktuellen koptischen Monats in Tagen. </p>
     *
     * @return  int
     * @since   3.11/4.8
     */
    public int lengthOfMonth() {

        return CALSYS.getLengthOfMonth(CopticEra.ANNO_MARTYRUM, this.cyear, this.cmonth);

    }

    /**
     * <p>Yields the length of current Coptic year in days. </p>
     *
     * @return  int
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge des aktuellen koptischen Jahres in Tagen. </p>
     *
     * @return  int
     * @since   3.11/4.8
     */
    public int lengthOfYear() {

        return this.isLeapYear() ? 366 : 365;

    }

    /**
     * <p>Is the year of this date a leap year? </p>
     *
     * @return  boolean
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Liegt dieses Datum in einem Schaltjahr? </p>
     *
     * @return  boolean
     * @since   3.11/4.8
     */
    public boolean isLeapYear() {

        return ((this.cyear % 4) == 3);

    }

    /**
     * <p>Queries if given parameter values form a well defined calendar date. </p>
     *
     * @param   yearOfEra   the year of era to be checked
     * @param   month       the month to be checked
     * @param   dayOfMonth  the day of month to be checked
     * @return  {@code true} if valid else  {@code false}
     * @see     #of(int, int, int)
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Pr&uuml;ft, ob die angegebenen Parameter ein wohldefiniertes Kalenderdatum beschreiben. </p>
     *
     * @param   yearOfEra   the year of era to be checked
     * @param   month       the month to be checked
     * @param   dayOfMonth  the day of month to be checked
     * @return  {@code true} if valid else  {@code false}
     * @see     #of(int, int, int)
     * @since   3.34/4.29
     */
    public static boolean isValid(
        int yearOfEra,
        int month,
        int dayOfMonth
    ) {

        return CALSYS.isValid(CopticEra.ANNO_MARTYRUM, yearOfEra, month, dayOfMonth);

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
     * @since   3.11/4.8
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
     * @since   3.11/4.8
     */
    public GeneralTimestamp<CopticCalendar> at(PlainTime time) {

        return GeneralTimestamp.of(this, time);

    }

    /**
     * <p>Is equivalent to {@code at(PlainTime.of(hour, minute))}. </p>
     *
     * @param   hour        hour of day in range (0-24)
     * @param   minute      minute of hour in range (0-59)
     * @return  general timestamp as composition of this date and given time
     * @throws  IllegalArgumentException if any argument is out of range
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Entspricht {@code at(PlainTime.of(hour, minute))}. </p>
     *
     * @param   hour        hour of day in range (0-24)
     * @param   minute      minute of hour in range (0-59)
     * @return  general timestamp as composition of this date and given time
     * @throws  IllegalArgumentException if any argument is out of range
     * @since   3.11/4.8
     */
    public GeneralTimestamp<CopticCalendar> atTime(
        int hour,
        int minute
    ) {

        return this.at(PlainTime.of(hour, minute));

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof CopticCalendar) {
            CopticCalendar that = (CopticCalendar) obj;
            return (
                (this.cdom == that.cdom)
                && (this.cmonth == that.cmonth)
                && (this.cyear == that.cyear)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return (17 * this.cdom + 31 * this.cmonth + 37 * this.cyear);

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(32);
        sb.append("A.M.-");
        String y = String.valueOf(this.cyear);
        for (int i = y.length(); i < 4; i++) {
            sb.append('0');
        }
        sb.append(y);
        sb.append('-');
        if (this.cmonth < 10) {
            sb.append('0');
        }
        sb.append(this.cmonth);
        sb.append('-');
        if (this.cdom < 10) {
            sb.append('0');
        }
        sb.append(this.cdom);
        return sb.toString();

    }

    /**
     * <p>Obtains the standard week model of this calendar. </p>
     *
     * <p>The Coptic calendar usually starts on Saturday. </p>
     *
     * @return  Weekmodel
     * @since   3.24/4.20
     */
    /*[deutsch]
     * <p>Ermittelt das Standardwochenmodell dieses Kalenders. </p>
     *
     * <p>Der koptische Kalender startet normalerweise am Samstag. </p>
     *
     * @return  Weekmodel
     * @since   3.24/4.20
     */
    public static Weekmodel getDefaultWeekmodel() {

        return Weekmodel.of(Weekday.SATURDAY, 1, Weekday.FRIDAY, Weekday.SATURDAY); // Egypt

    }

    /**
     * <p>Returns the associated time axis. </p>
     *
     * @return  chronology
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Liefert die zugeh&ouml;rige Zeitachse. </p>
     *
     * @return  chronology
     * @since   3.11/4.8
     */
    public static TimeAxis<Unit, CopticCalendar> axis() {

        return ENGINE;

    }

    @Override
    protected TimeAxis<Unit, CopticCalendar> getChronology() {

        return ENGINE;

    }

    @Override
    protected CopticCalendar getContext() {

        return this;

    }

    /**
     * @serialData  Uses <a href="../../../serialized-form.html#net.time4j.calendar.SPX">
     *              a dedicated serialization form</a> as proxy. The first byte contains
     *              the type-ID {@code 3}. Then the year is written as int, finally
     *              month and day-of-month as bytes.
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.COPTIC);

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
     * <p>Defines some calendar units for the Coptic calendar. </p>
     *
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Definiert einige kalendarische Zeiteinheiten f&uuml;r den koptischen Kalender. </p>
     *
     * @since   3.11/4.8
     */
    public static enum Unit
        implements ChronoUnit {

        //~ Statische Felder/Initialisierungen ----------------------------

        YEARS(365.25 * 86400.0),

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
         * <p>Calculates the difference between given Coptic dates in this unit. </p>
         *
         * @param   start   start date (inclusive)
         * @param   end     end date (exclusive)
         * @return  difference counted in this unit
         * @since   3.11/4.8
         */
        /*[deutsch]
         * <p>Berechnet die Differenz zwischen den angegebenen Datumsparametern in dieser Zeiteinheit. </p>
         *
         * @param   start   start date (inclusive)
         * @param   end     end date (exclusive)
         * @return  difference counted in this unit
         * @since   3.11/4.8
         */
        public int between(
            CopticCalendar start,
            CopticCalendar end
        ) {

            return (int) start.until(end, this); // safe

        }

    }

    private static class Transformer
        implements EraYearMonthDaySystem<CopticCalendar> {

        //~ Methoden ------------------------------------------------------

        @Override
        public boolean isValid(
            CalendarEra era,
            int yearOfEra,
            int monthOfYear,
            int dayOfMonth
        ) {

            return (
                (era == CopticEra.ANNO_MARTYRUM)
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

            if (era != CopticEra.ANNO_MARTYRUM) {
                throw new IllegalArgumentException("Invalid era: " + era);
            }

            if (
                (yearOfEra >= 1)
                && (yearOfEra <= 9999)
                && (monthOfYear >= 1)
                && (monthOfYear <= 13)
            ) {
                if (monthOfYear <= 12) {
                    return 30;
                } else {
                    return ((yearOfEra % 4) == 3) ? 6 : 5;
                }
            }

            throw new IllegalArgumentException("Out of bounds: year=" + yearOfEra + ", month=" + monthOfYear);

        }

        @Override
        public int getLengthOfYear(
            CalendarEra era,
            int yearOfEra
        ) {

            if (era != CopticEra.ANNO_MARTYRUM) {
                throw new IllegalArgumentException("Invalid era: " + era);
            }

            if (
                (yearOfEra >= 1)
                && (yearOfEra <= 9999)
            ) {
                return ((yearOfEra % 4) == 3) ? 366 : 365;
            }

            throw new IllegalArgumentException("Out of bounds: year=" + yearOfEra);

        }

        @Override
        public CopticCalendar transform(long utcDays) {

            int cyear =
                MathUtils.safeCast(
                    MathUtils.floorDivide(
                        MathUtils.safeAdd(
                            MathUtils.safeMultiply(
                                4,
                                MathUtils.safeSubtract(utcDays, DIOCLETIAN)),
                            1463),
                        1461));

            int startOfYear =  MathUtils.safeCast(this.transform(new CopticCalendar(cyear, 1, 1)));
            int cmonth = 1 + MathUtils.safeCast(MathUtils.floorDivide(utcDays - startOfYear, 30));
            int startOfMonth = MathUtils.safeCast(this.transform(new CopticCalendar(cyear, cmonth, 1)));
            int cdom = 1 + MathUtils.safeCast(MathUtils.safeSubtract(utcDays, startOfMonth));

            return CopticCalendar.of(cyear, cmonth, cdom);

        }

        @Override
        public long transform(CopticCalendar date) {

            return (
                DIOCLETIAN - 1
                + 365 * (date.cyear - 1) + MathUtils.floorDivide(date.cyear, 4)
                + 30 * (date.cmonth - 1) + date.cdom);

        }

        @Override
        public long getMinimumSinceUTC() {

            CopticCalendar min = new CopticCalendar(1, 1, 1);
            return this.transform(min);

        }

        @Override
        public long getMaximumSinceUTC() {

            CopticCalendar max = new CopticCalendar(9999, 13, 6);
            return this.transform(max);

        }

        @Override
        public List<CalendarEra> getEras() {

            CalendarEra era = CopticEra.ANNO_MARTYRUM;
            return Collections.singletonList(era);

        }

    }

    private static class IntegerRule
        implements ElementRule<CopticCalendar, Integer> {

        //~ Instanzvariablen ----------------------------------------------

        private final int index;

        //~ Konstruktoren -------------------------------------------------

        IntegerRule(int index) {
            super();

            this.index = index;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Integer getValue(CopticCalendar context) {

            switch (this.index) {
                case YEAR_INDEX:
                    return context.cyear;
                case DAY_OF_MONTH_INDEX:
                    return context.cdom;
                case DAY_OF_YEAR_INDEX:
                    int doy = 0;
                    for (int m = 1; m < context.cmonth; m++) {
                        doy += CALSYS.getLengthOfMonth(CopticEra.ANNO_MARTYRUM, context.cyear, m);
                    }
                    return doy + context.cdom;
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public Integer getMinimum(CopticCalendar context) {

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
        public Integer getMaximum(CopticCalendar context) {

            switch (this.index) {
                case YEAR_INDEX:
                    return 9999;
                case DAY_OF_MONTH_INDEX:
                    return CALSYS.getLengthOfMonth(CopticEra.ANNO_MARTYRUM, context.cyear, context.cmonth);
                case DAY_OF_YEAR_INDEX:
                    return CALSYS.getLengthOfYear(CopticEra.ANNO_MARTYRUM, context.cyear);
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public boolean isValid(
            CopticCalendar context,
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
        public CopticCalendar withValue(
            CopticCalendar context,
            Integer value,
            boolean lenient
        ) {

            if (!this.isValid(context, value)) {
                throw new IllegalArgumentException("Out of range: " + value);
            }

            switch (this.index) {
                case YEAR_INDEX:
                    int y = value.intValue();
                    int dmax = CALSYS.getLengthOfMonth(CopticEra.ANNO_MARTYRUM, y, context.cmonth);
                    int d = Math.min(context.cdom, dmax);
                    return CopticCalendar.of(y, context.cmonth, d);
                case DAY_OF_MONTH_INDEX:
                    return new CopticCalendar(context.cyear, context.cmonth, value.intValue());
                case DAY_OF_YEAR_INDEX:
                    int delta = value.intValue() - this.getValue(context).intValue();
                    return context.plus(CalendarDays.of(delta));
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public ChronoElement<?> getChildAtFloor(CopticCalendar context) {

            if (this.index == YEAR_INDEX) {
                return MONTH_OF_YEAR;
            }

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(CopticCalendar context) {

            if (this.index == YEAR_INDEX) {
                return MONTH_OF_YEAR;
            }

            return null;

        }

    }

    private static class MonthRule
        implements ElementRule<CopticCalendar, CopticMonth> {

        //~ Methoden ------------------------------------------------------

        @Override
        public CopticMonth getValue(CopticCalendar context) {

            return context.getMonth();

        }

        @Override
        public CopticMonth getMinimum(CopticCalendar context) {

            return CopticMonth.TOUT;

        }

        @Override
        public CopticMonth getMaximum(CopticCalendar context) {

            return CopticMonth.NASIE;

        }

        @Override
        public boolean isValid(
            CopticCalendar context,
            CopticMonth value
        ) {

            return (value != null);

        }

        @Override
        public CopticCalendar withValue(
            CopticCalendar context,
            CopticMonth value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing month.");
            }

            int m = value.getValue();
            int dmax = CALSYS.getLengthOfMonth(CopticEra.ANNO_MARTYRUM, context.cyear, m);
            int d = Math.min(context.cdom, dmax);
            return new CopticCalendar(context.cyear, m, d);

        }

        @Override
        public ChronoElement<?> getChildAtFloor(CopticCalendar context) {

            return DAY_OF_MONTH;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(CopticCalendar context) {

            return DAY_OF_MONTH;

        }

    }

    private static class EraRule
        implements ElementRule<CopticCalendar, CopticEra> {

        //~ Methoden ------------------------------------------------------

        @Override
        public CopticEra getValue(CopticCalendar context) {

            return CopticEra.ANNO_MARTYRUM;

        }

        @Override
        public CopticEra getMinimum(CopticCalendar context) {

            return CopticEra.ANNO_MARTYRUM;

        }

        @Override
        public CopticEra getMaximum(CopticCalendar context) {

            return CopticEra.ANNO_MARTYRUM;

        }

        @Override
        public boolean isValid(
            CopticCalendar context,
            CopticEra value
        ) {

            return (value != null);

        }

        @Override
        public CopticCalendar withValue(
            CopticCalendar context,
            CopticEra value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing era value.");
            }

            return context;

        }

        @Override
        public ChronoElement<?> getChildAtFloor(CopticCalendar context) {

            return YEAR_OF_ERA;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(CopticCalendar context) {

            return YEAR_OF_ERA;

        }

    }

    private static class Merger
        implements ChronoMerger<CopticCalendar> {

        //~ Methoden ------------------------------------------------------

        @Override
        public String getFormatPattern(
            DisplayStyle style,
            Locale locale
        ) {

            return GenericDatePatterns.get("coptic", style, locale);

        }

        @Override
        public CopticCalendar createFrom(
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
        public CopticCalendar createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean preparsing
        ) {

            boolean lenient = attributes.get(Attributes.LENIENCY, Leniency.SMART).isLax();
            return this.createFrom(entity, attributes, lenient, preparsing);

        }

        @Override
        public CopticCalendar createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean lenient,
            boolean preparsing
        ) {

            int cyear = entity.getInt(YEAR_OF_ERA);

            if (cyear == Integer.MIN_VALUE) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Missing Coptic year.");
                return null;
            }

            if (entity.contains(MONTH_OF_YEAR)) {
                int cmonth = entity.get(MONTH_OF_YEAR).getValue();
                int cdom = entity.getInt(DAY_OF_MONTH);

                if (cdom != Integer.MIN_VALUE) {
                    if (CALSYS.isValid(CopticEra.ANNO_MARTYRUM, cyear, cmonth, cdom)) {
                        return CopticCalendar.of(cyear, cmonth, cdom);
                    } else {
                        entity.with(ValidationElement.ERROR_MESSAGE, "Invalid Coptic date.");
                    }
                }
            } else {
                int cdoy = entity.getInt(DAY_OF_YEAR);
                if (cdoy != Integer.MIN_VALUE) {
                    if (cdoy > 0) {
                        int cmonth = 1;
                        int daycount = 0;
                        while (cmonth <= 13) {
                            int len = CALSYS.getLengthOfMonth(CopticEra.ANNO_MARTYRUM, cyear, cmonth);
                            if (cdoy > daycount + len) {
                                cmonth++;
                                daycount += len;
                            } else {
                                return CopticCalendar.of(cyear, cmonth, cdoy - daycount);
                            }
                        }
                    }
                    entity.with(ValidationElement.ERROR_MESSAGE, "Invalid Coptic date.");
                }
            }

            return null;

        }

        @Override
        public ChronoDisplay preformat(CopticCalendar context, AttributeQuery attributes) {

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

            return PlainDate.axis().getDefaultPivotYear() - 284;

        }

    }

    private static class CopticUnitRule
        implements UnitRule<CopticCalendar> {

        //~ Instanzvariablen ----------------------------------------------

        private final Unit unit;

        //~ Konstruktoren -------------------------------------------------

        CopticUnitRule(Unit unit) {
            super();

            this.unit = unit;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public CopticCalendar addTo(CopticCalendar date, long amount) {

            switch (this.unit) {
                case YEARS:
                    amount = MathUtils.safeMultiply(amount, 13);
                    // fall-through
                case MONTHS:
                    long ym = MathUtils.safeAdd(ymValue(date), amount);
                    int year = MathUtils.safeCast(MathUtils.floorDivide(ym, 13));
                    int month = MathUtils.floorModulo(ym, 13) + 1;
                    int dom =
                        Math.min(
                            date.cdom,
                            CALSYS.getLengthOfMonth(CopticEra.ANNO_MARTYRUM, year, month));
                    return CopticCalendar.of(year, month, dom);
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
        public long between(CopticCalendar start, CopticCalendar end) {

            switch (this.unit) {
                case YEARS:
                    return CopticCalendar.Unit.MONTHS.between(start, end) / 13;
                case MONTHS:
                    long delta = ymValue(end) - ymValue(start);
                    if ((delta > 0) && (end.cdom < start.cdom)) {
                        delta--;
                    } else if ((delta < 0) && (end.cdom > start.cdom)) {
                        delta++;
                    }
                    return delta;
                case WEEKS:
                    return CopticCalendar.Unit.DAYS.between(start, end) / 7;
                case DAYS:
                    return CALSYS.transform(end) - CALSYS.transform(start);
                default:
                    throw new UnsupportedOperationException(this.unit.name());
            }

        }

        private static int ymValue(CopticCalendar date) {

            return date.cyear * 13 + date.cmonth - 1;

        }

    }

}
