/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (IndianCalendar.java) is part of project Time4J.
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
import net.time4j.base.GregorianMath;
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
 * <p>Represents the national calendar of India. </p>
 *
 * <p>It is a reform calendar synchronized with the gregorian calendar and was introduced in year 1957 as an attempt
 * to unify the various local calendars used in India. A special leap year rule was introduced as follows:
 * <i>First add to the year of Saka era the number 78, then determine if the sum is a gregorian leap year.</i>
 * See also <a href="https://en.wikipedia.org/wiki/Indian_national_calendar">Wikipedia</a>. Note that the
 * Indian national calendar is not widely used despite of its official status. Most Indian people prefer
 * the gregorian calendar. The calendar day starts at midnight, like in gregorian calendar but in contrast
 * to the old Hinduist tradition. </p>
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
 *     ChronoFormatter&lt;IndianCalendar&gt; formatter =
 *       ChronoFormatter.ofPattern(
 *         &quot;EEE, d. MMMM yyyy&quot;, PatternType.CLDR_DATE, Locale.ENGLISH, IndianCalendar.axis());
 *     PlainDate today = SystemClock.inLocalView().today();
 *     IndianCalendar indianDate = today.transform(IndianCalendar.class);
 *     System.out.println(formatter.format(indianDate));
 * </pre>
 *
 * @author  Meno Hochschild
 * @see     IndianEra
 * @see     IndianMonth
 * @see     net.time4j.format.NumberSystem#BENGALI
 * @see     net.time4j.format.NumberSystem#DEVANAGARI
 * @see     net.time4j.format.NumberSystem#GUJARATI
 * @see     net.time4j.format.NumberSystem#TELUGU
 * @since   3.32/4.27
 */
/*[deutsch]
 * <p>Repr&auml;sentiert den indischen Nationalkalender. </p>
 *
 * <p>Es handelt sich um einen Reformkalender, der mit dem gregorianischen Kalender synchronisiert ist und
 * im Jahre 1957 als Versuch eingef&uuml;hrt wurde, die verschiedenen lokalen Kalender in Indien zu
 * vereinen. Eine spezielle Schaltjahresregel wurde wie folgt eingef&uuml;hrt: <i>Zuerst addiere zum
 * Jahr der Saka-&Auml;ra die Zahl 78, dann bestimme, ob die Summe ein gregorianisches Schaltjahr darstellt.</i>
 * Siehe auch <a href="https://en.wikipedia.org/wiki/Indian_national_calendar">Wikipedia</a>. Hinweis:
 * Der indische Nationalkalender ist trotz seines offiziellen Status nicht sehr gebr&auml;uchlich. Die
 * meisten Inder bevorzugen den gregorianischen Kalender. Die Kalendertage beginnen um Mitternacht,
 * wie im gregorianischen Kalender, aber im Gegensatz zur alten hinduistischen Tradition. </p>
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
 *     ChronoFormatter&lt;IndianCalendar&gt; formatter =
 *       ChronoFormatter.ofPattern(
 *         &quot;EEE, d. MMMM yyyy&quot;, PatternType.CLDR_DATE, Locale.ENGLISH, IndianCalendar.axis());
 *     PlainDate today = SystemClock.inLocalView().today();
 *     IndianCalendar indianDate = today.transform(IndianCalendar.class);
 *     System.out.println(formatter.format(indianDate));
 * </pre>
 *
 * @author  Meno Hochschild
 * @see     IndianEra
 * @see     IndianMonth
 * @see     net.time4j.format.NumberSystem#BENGALI
 * @see     net.time4j.format.NumberSystem#DEVANAGARI
 * @see     net.time4j.format.NumberSystem#GUJARATI
 * @see     net.time4j.format.NumberSystem#TELUGU
 * @since   3.32/4.27
 */
@CalendarType("indian")
public final class IndianCalendar
    extends Calendrical<IndianCalendar.Unit, IndianCalendar>
    implements LocalizedPatternSupport {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int MAX_YEAR = GregorianMath.MAX_YEAR - 78;

    private static final int YEAR_INDEX = 0;
    private static final int DAY_OF_MONTH_INDEX = 2;
    private static final int DAY_OF_YEAR_INDEX = 3;

    /**
     * <p>Represents the Indian era. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die indische &Auml;ra. </p>
     */
    @FormattableElement(format = "G")
    public static final ChronoElement<IndianEra> ERA =
        new StdEnumDateElement<IndianEra, IndianCalendar>("ERA", IndianCalendar.class, IndianEra.class, 'G');

    /**
     * <p>Represents the Indian year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert das indische Jahr. </p>
     */
    @FormattableElement(format = "y")
    public static final StdCalendarElement<Integer, IndianCalendar> YEAR_OF_ERA =
        new StdIntegerDateElement<IndianCalendar>(
            "YEAR_OF_ERA",
            IndianCalendar.class,
            1,
            MAX_YEAR,
            'y',
            null,
            null);

    /**
     * <p>Represents the Indian month. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den indischen Monat. </p>
     */
    @FormattableElement(format = "M", standalone = "L")
    public static final StdCalendarElement<IndianMonth, IndianCalendar> MONTH_OF_YEAR =
        new StdEnumDateElement<IndianMonth, IndianCalendar>(
            "MONTH_OF_YEAR",
            IndianCalendar.class,
            IndianMonth.class,
            'M');

    /**
     * <p>Represents the Indian day of month. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den indischen Tag des Monats. </p>
     */
    @FormattableElement(format = "d")
    public static final StdCalendarElement<Integer, IndianCalendar> DAY_OF_MONTH =
        new StdIntegerDateElement<IndianCalendar>("DAY_OF_MONTH", IndianCalendar.class, 1, 31, 'd');

    /**
     * <p>Represents the Indian day of year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den indischen Tag des Jahres. </p>
     */
    @FormattableElement(format = "D")
    public static final StdCalendarElement<Integer, IndianCalendar> DAY_OF_YEAR =
        new StdIntegerDateElement<IndianCalendar>("DAY_OF_YEAR", IndianCalendar.class, 1, 365, 'D');

    /**
     * <p>Represents the Indian day of week. </p>
     *
     * <p>If the day-of-week is set to a new value then Time4J handles the Indian calendar week
     * as starting on Sunday. </p>
     *
     * @see     #getDefaultWeekmodel()
     * @see     CommonElements#localDayOfWeek(Chronology, Weekmodel)
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den indischen Tag der Woche. </p>
     *
     * <p>Wenn der Tag der Woche auf einen neuen Wert gesetzt wird, behandelt Time4J die indische
     * Kalenderwoche so, da&szlig; sie am Sonntag beginnt. </p>
     *
     * @see     #getDefaultWeekmodel()
     * @see     CommonElements#localDayOfWeek(Chronology, Weekmodel)
     */
    @FormattableElement(format = "E")
    public static final StdCalendarElement<Weekday, IndianCalendar> DAY_OF_WEEK =
        new StdWeekdayElement<IndianCalendar>(IndianCalendar.class, getDefaultWeekmodel());

    private static final WeekdayInMonthElement<IndianCalendar> WIM_ELEMENT =
        new WeekdayInMonthElement<IndianCalendar>(IndianCalendar.class, DAY_OF_MONTH, DAY_OF_WEEK);

    /**
     * <p>Element with the ordinal day-of-week within given calendar month. </p>
     */
    /*[deutsch]
     * <p>Element mit dem x-ten Wochentag im Monat. </p>
     */
    @FormattableElement(format = "F")
    public static final OrdinalWeekdayElement<IndianCalendar> WEEKDAY_IN_MONTH = WIM_ELEMENT;

    private static final EraYearMonthDaySystem<IndianCalendar> CALSYS;
    private static final TimeAxis<IndianCalendar.Unit, IndianCalendar> ENGINE;

    static {
        CALSYS = new Transformer();

        TimeAxis.Builder<IndianCalendar.Unit, IndianCalendar> builder =
            TimeAxis.Builder.setUp(
                IndianCalendar.Unit.class,
                IndianCalendar.class,
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
                new WeekdayRule<IndianCalendar>(
                    getDefaultWeekmodel(),
                    new ChronoFunction<IndianCalendar, CalendarSystem<IndianCalendar>>() {
                        @Override
                        public CalendarSystem<IndianCalendar> apply(IndianCalendar context) {
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
                new RelatedGregorianYearRule<IndianCalendar>(CALSYS, DAY_OF_YEAR))
            .appendUnit(
                Unit.YEARS,
                new IndianUnitRule(Unit.YEARS),
                Unit.YEARS.getLength(),
                Collections.singleton(Unit.MONTHS))
            .appendUnit(
                Unit.MONTHS,
                new IndianUnitRule(Unit.MONTHS),
                Unit.MONTHS.getLength(),
                Collections.singleton(Unit.YEARS))
            .appendUnit(
                Unit.WEEKS,
                new IndianUnitRule(Unit.WEEKS),
                Unit.WEEKS.getLength(),
                Collections.singleton(Unit.DAYS))
            .appendUnit(
                Unit.DAYS,
                new IndianUnitRule(Unit.DAYS),
                Unit.DAYS.getLength(),
                Collections.singleton(Unit.WEEKS))
            .appendExtension(
                new CommonElements.Weekengine(
                    IndianCalendar.class,
                    DAY_OF_MONTH,
                    DAY_OF_YEAR,
                    getDefaultWeekmodel()));
        ENGINE = builder.build();
    }

    private static final long serialVersionUID = 7482205842000661998L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final int iyear;
    private transient final int imonth;
    private transient final int idom;

    //~ Konstruktoren -----------------------------------------------------

    private IndianCalendar(
        int iyear,
        int imonth,
        int idom
    ) {
        super();

        this.iyear = iyear;
        this.imonth = imonth;
        this.idom = idom;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new instance of an Indian calendar date. </p>
     *
     * @param   iyear   Indian year in the range 1-999999921
     * @param   imonth  Indian month
     * @param   idom    Indian day of month in range 1-31
     * @return  new instance of {@code IndianCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    /*[deutsch]
     * <p>Erzeugt ein neues indisches Kalenderdatum. </p>
     *
     * @param   iyear   Indian year in the range 1-999999921
     * @param   imonth  Indian month
     * @param   idom    Indian day of month in range 1-31
     * @return  new instance of {@code IndianCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    public static IndianCalendar of(
        int iyear,
        IndianMonth imonth,
        int idom
    ) {

        return IndianCalendar.of(iyear, imonth.getValue(), idom);

    }

    /**
     * <p>Creates a new instance of an Indian calendar date. </p>
     *
     * @param   iyear   Indian year in the range 1-999999921
     * @param   imonth  Indian month in range 1-12
     * @param   idom    Indian day of month in range 1-31
     * @return  new instance of {@code IndianCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    /*[deutsch]
     * <p>Erzeugt ein neues indisches Kalenderdatum. </p>
     *
     * @param   iyear   Indian year in the range 1-999999921
     * @param   imonth  Indian month in range 1-12
     * @param   idom    Indian day of month in range 1-31
     * @return  new instance of {@code IndianCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    public static IndianCalendar of(
        int iyear,
        int imonth,
        int idom
    ) {

        if (!CALSYS.isValid(IndianEra.SAKA, iyear, imonth, idom)) {
            throw new IllegalArgumentException(
                "Invalid Indian date: year=" + iyear + ", month=" + imonth + ", day=" + idom);
        }

        return new IndianCalendar(iyear, imonth, idom);

    }

    /**
     * <p>Obtains the current calendar date in system time. </p>
     *
     * <p>Convenient short-cut for: {@code SystemClock.inLocalView().now(IndianCalendar.axis())}. </p>
     *
     * @return  current calendar date in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(Chronology)
     */
    /*[deutsch]
     * <p>Ermittelt das aktuelle Kalenderdatum in der Systemzeit. </p>
     *
     * <p>Bequeme Abk&uuml;rzung f&uuml;r: {@code SystemClock.inLocalView().now(IndianCalendar.axis())}. </p>
     *
     * @return  current calendar date in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(net.time4j.engine.Chronology)
     */
    public static IndianCalendar nowInSystemTime() {

        return SystemClock.inLocalView().now(IndianCalendar.axis());

    }

    /**
     * <p>Yields the Indian era. </p>
     *
     * @return  {@link IndianEra#SAKA}
     */
    /*[deutsch]
     * <p>Liefert die indische &Auml;ra. </p>
     *
     * @return  {@link IndianEra#SAKA}
     */
    public IndianEra getEra() {

        return IndianEra.SAKA;

    }

    /**
     * <p>Yields the Indian year. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert das indische Jahr. </p>
     *
     * @return  int
     */
    public int getYear() {

        return this.iyear;

    }

    /**
     * <p>Yields the Indian month. </p>
     *
     * @return  enum
     */
    /*[deutsch]
     * <p>Liefert den indischen Monat. </p>
     *
     * @return  enum
     */
    public IndianMonth getMonth() {

        return IndianMonth.valueOf(this.imonth);

    }

    /**
     * <p>Yields the Indian day of month. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert den indischen Tag des Monats. </p>
     *
     * @return  int
     */
    public int getDayOfMonth() {

        return this.idom;

    }

    /**
     * <p>Determines the day of week. </p>
     *
     * <p>The Indian calendar also uses a 7-day-week. </p>
     *
     * @return  Weekday
     */
    /*[deutsch]
     * <p>Ermittelt den Wochentag. </p>
     *
     * <p>Der indische Kalendar verwendet ebenfalls eine 7-Tage-Woche. </p>
     *
     * @return  Weekday
     */
    public Weekday getDayOfWeek() {

        long utcDays = CALSYS.transform(this);
        return Weekday.valueOf(MathUtils.floorModulo(utcDays + 5, 7) + 1);

    }

    /**
     * <p>Yields the Indian day of year. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert den indischen Tag des Jahres. </p>
     *
     * @return  int
     */
    public int getDayOfYear() {

        return this.get(DAY_OF_YEAR).intValue();

    }

    /**
     * <p>Yields the length of current Indian month in days. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge des aktuellen indischen Monats in Tagen. </p>
     *
     * @return  int
     */
    public int lengthOfMonth() {

        return CALSYS.getLengthOfMonth(IndianEra.SAKA, this.iyear, this.imonth);

    }

    /**
     * <p>Yields the length of current Indian year in days. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge des aktuellen indischen Jahres in Tagen. </p>
     *
     * @return  int
     */
    public int lengthOfYear() {

        return this.isLeapYear() ? 366 : 365;

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

        return GregorianMath.isLeapYear(this.iyear + 78);

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

        return CALSYS.isValid(IndianEra.SAKA, yearOfEra, month, dayOfMonth);

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
    public GeneralTimestamp<IndianCalendar> at(PlainTime time) {

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
    public GeneralTimestamp<IndianCalendar> atTime(
        int hour,
        int minute
    ) {

        return this.at(PlainTime.of(hour, minute));

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof IndianCalendar) {
            IndianCalendar that = (IndianCalendar) obj;
            return (
                (this.idom == that.idom)
                && (this.imonth == that.imonth)
                && (this.iyear == that.iyear)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return (17 * this.idom + 31 * this.imonth + 37 * this.iyear);

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(32);
        sb.append("Saka-");
        String y = String.valueOf(this.iyear);
        for (int i = y.length(); i < 4; i++) {
            sb.append('0');
        }
        sb.append(y);
        sb.append('-');
        if (this.imonth < 10) {
            sb.append('0');
        }
        sb.append(this.imonth);
        sb.append('-');
        if (this.idom < 10) {
            sb.append('0');
        }
        sb.append(this.idom);
        return sb.toString();

    }

    /**
     * <p>Obtains the standard week model of this calendar. </p>
     *
     * <p>The Indian calendar usually starts on Sunday. </p>
     *
     * @return  Weekmodel
     */
    /*[deutsch]
     * <p>Ermittelt das Standardwochenmodell dieses Kalenders. </p>
     *
     * <p>Der indische Kalender startet normalerweise am Sonntag. </p>
     *
     * @return  Weekmodel
     */
    public static Weekmodel getDefaultWeekmodel() {

        return Weekmodel.of(Weekday.SUNDAY, 1, Weekday.SUNDAY, Weekday.SUNDAY);

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
    public static TimeAxis<Unit, IndianCalendar> axis() {

        return ENGINE;

    }

    @Override
    protected TimeAxis<Unit, IndianCalendar> getChronology() {

        return ENGINE;

    }

    @Override
    protected IndianCalendar getContext() {

        return this;

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

        return new SPX(this, SPX.INDIAN);

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
     * <p>Defines some calendar units for the Indian calendar. </p>
     */
    /*[deutsch]
     * <p>Definiert einige kalendarische Zeiteinheiten f&uuml;r den indischen Kalender. </p>
     */
    public static enum Unit
        implements ChronoUnit {

        //~ Statische Felder/Initialisierungen ----------------------------

        YEARS(365.2425 * 86400.0),

        MONTHS((365.2425 * 86400.0) / 12),

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
            IndianCalendar start,
            IndianCalendar end
        ) {

            return start.until(end, this);

        }

    }

    private static class Transformer
        implements EraYearMonthDaySystem<IndianCalendar> {

        //~ Methoden ------------------------------------------------------

        @Override
        public boolean isValid(
            CalendarEra era,
            int yearOfEra,
            int monthOfYear,
            int dayOfMonth
        ) {

            return (
                (era == IndianEra.SAKA)
                && (yearOfEra >= 1)
                && (yearOfEra <= MAX_YEAR)
                && (monthOfYear >= 1)
                && (monthOfYear <= (yearOfEra == MAX_YEAR ? 10 : 12))
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

            if (era != IndianEra.SAKA) {
                throw new IllegalArgumentException("Invalid era: " + era);
            }

            if ((yearOfEra >= 1) && (yearOfEra <= MAX_YEAR) && (monthOfYear >= 1)) {
                if ((yearOfEra == MAX_YEAR) && (monthOfYear == 10)) {
                    return 10; // edge case
                } else if (monthOfYear == 1) {
                    return (GregorianMath.isLeapYear(yearOfEra + 78) ? 31 : 30);
                } else if (monthOfYear <= 6) {
                    return 31;
                } else if (monthOfYear <= 12) {
                    return 30;
                }
            }

            throw new IllegalArgumentException("Out of bounds: year=" + yearOfEra + ", month=" + monthOfYear);

        }

        @Override
        public int getLengthOfYear(
            CalendarEra era,
            int yearOfEra
        ) {

            if (era != IndianEra.SAKA) {
                throw new IllegalArgumentException("Invalid era: " + era);
            }

            if ((yearOfEra >= 1) && (yearOfEra < MAX_YEAR)) {
                return (GregorianMath.isLeapYear(yearOfEra + 78) ? 366 : 365);
            } else if (yearOfEra == MAX_YEAR) {
                return 285; // edge case
            }

            throw new IllegalArgumentException("Out of bounds: year=" + yearOfEra);

        }

        @Override
        public IndianCalendar transform(long utcDays) {

            PlainDate date = PlainDate.of(utcDays, EpochDays.UTC);
            int y = date.getYear();
            int m = date.getMonth();
            int d = date.getDayOfMonth();
            boolean leap = GregorianMath.isLeapYear(y);
            int ve = (leap ? 21 : 22);

            int year = date.getYear() - 78;
            int month;
            int dom;

            if (m == 12 && d >= 22) {
                month = 10;
                dom = d - 21;
            } else if (m == 12) {
                month = 9;
                dom = d + 9;
            } else if (m == 11 && d >= 22) {
                month = 9;
                dom = d - 21;
            } else if (m == 11) {
                month = 8;
                dom = d + 9;
            } else if (m == 10 && d >= 23) {
                month = 8;
                dom = d - 22;
            } else if (m == 10) {
                month = 7;
                dom = d + 8;
            } else if (m == 9 && d >= 23) {
                month = 7;
                dom = d - 22;
            } else if (m == 9) {
                month = 6;
                dom = d + 9;
            } else if (m == 8 && d >= 23) {
                month = 6;
                dom = d - 22;
            } else if (m == 8) {
                month = 5;
                dom = d + 9;
            } else if (m == 7 && d >= 23) {
                month = 5;
                dom = d - 22;
            } else if (m == 7) {
                month = 4;
                dom = d + 9;
            } else if (m == 6 && d >= 22) {
                month = 4;
                dom = d - 21;
            } else if (m == 6) {
                month = 3;
                dom = d + 10;
            } else if (m == 5 && d >= 22) {
                month = 3;
                dom = d - 21;
            } else if (m == 5) {
                month = 2;
                dom = d + 10;
            } else if (m == 4 && d >= 21) {
                month = 2;
                dom = d - 20;
            } else if (m == 4) {
                month = 1;
                dom = d + (leap ? 11 : 10);
            } else if (m == 3 && d >= ve) {
                month = 1;
                dom = d - ve + 1;
            } else if (m == 3) {
                year--;
                month = 12;
                dom = d + (leap ? 10 : 9);
            } else if (m == 2 && d >= 20) {
                year--;
                month = 12;
                dom = d - 19;
            } else if (m == 2) {
                year--;
                month = 11;
                dom = d + 11;
            } else if (m == 1 && d >= 21) {
                year--;
                month = 11;
                dom = d - 20;
            } else {
                year--;
                month = 10;
                dom = d + 10;
            }

            return IndianCalendar.of(year, month, dom);

        }

        @Override
        public long transform(IndianCalendar date) {

            int y = date.iyear + 78;
            boolean leap = GregorianMath.isLeapYear(y);
            long newYear = PlainDate.of(y, 3, (leap ? 21 : 22)).get(EpochDays.UTC);
            int days = 0;

            for (int m = 1; m < date.imonth; m++) {
                switch (m) {
                    case 1:
                        days += (leap ? 31 : 30);
                        break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        days += 31;
                        break;
                    default:
                        days += 30;
                }
            }

            days += (date.idom - 1);
            return newYear + days;

        }

        @Override
        public long getMinimumSinceUTC() {

            IndianCalendar min = new IndianCalendar(1, 1, 1);
            return this.transform(min);

        }

        @Override
        public long getMaximumSinceUTC() {

            IndianCalendar max = new IndianCalendar(MAX_YEAR, 10, 10);
            return this.transform(max);

        }

        @Override
        public List<CalendarEra> getEras() {

            CalendarEra era = IndianEra.SAKA;
            return Collections.singletonList(era);

        }

    }

    private static class IntegerRule
        implements IntElementRule<IndianCalendar> {

        //~ Instanzvariablen ----------------------------------------------

        private final int index;

        //~ Konstruktoren -------------------------------------------------

        IntegerRule(int index) {
            super();

            this.index = index;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public int getInt(IndianCalendar context) {

            switch (this.index) {
                case YEAR_INDEX:
                    return context.iyear;
                case DAY_OF_MONTH_INDEX:
                    return context.idom;
                case DAY_OF_YEAR_INDEX:
                    int doy = 0;
                    for (int m = 1; m < context.imonth; m++) {
                        doy += CALSYS.getLengthOfMonth(IndianEra.SAKA, context.iyear, m);
                    }
                    return doy + context.idom;
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public boolean isValid(IndianCalendar context, int value) {

            int min = this.getMin();
            int max = this.getMax(context);
            return ((min <= value) && (max >= value));

        }

        @Override
        public IndianCalendar withValue(IndianCalendar context, int value, boolean lenient) {

            if (!this.isValid(context, value)) {
                throw new IllegalArgumentException("Out of range: " + value);
            }

            switch (this.index) {
                case YEAR_INDEX:
                    int dmax = CALSYS.getLengthOfMonth(IndianEra.SAKA, value, context.imonth);
                    int d = Math.min(context.idom, dmax);
                    return new IndianCalendar(value, context.imonth, d);
                case DAY_OF_MONTH_INDEX:
                    return new IndianCalendar(context.iyear, context.imonth, value);
                case DAY_OF_YEAR_INDEX:
                    int delta = value - this.getValue(context).intValue();
                    return context.plus(CalendarDays.of(delta));
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public Integer getValue(IndianCalendar context) {

            return Integer.valueOf(this.getInt(context));

        }

        @Override
        public Integer getMinimum(IndianCalendar context) {

            return Integer.valueOf(this.getMin());

        }

        @Override
        public Integer getMaximum(IndianCalendar context) {

            return Integer.valueOf(this.getMax(context));

        }

        @Override
        public boolean isValid(
            IndianCalendar context,
            Integer value
        ) {

            return ((value != null) && this.isValid(context, value.intValue()));

        }

        @Override
        public IndianCalendar withValue(
            IndianCalendar context,
            Integer value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing new value.");
            }

            return this.withValue(context, value.intValue(), lenient);

        }

        @Override
        public ChronoElement<?> getChildAtFloor(IndianCalendar context) {

            if (this.index == YEAR_INDEX) {
                return MONTH_OF_YEAR;
            }

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(IndianCalendar context) {

            if (this.index == YEAR_INDEX) {
                return MONTH_OF_YEAR;
            }

            return null;

        }

        private int getMin() {

            switch (this.index) {
                case YEAR_INDEX:
                case DAY_OF_MONTH_INDEX:
                case DAY_OF_YEAR_INDEX:
                    return 1;
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        private int getMax(IndianCalendar context) {

            switch (this.index) {
                case YEAR_INDEX:
                    return MAX_YEAR;
                case DAY_OF_MONTH_INDEX:
                    return CALSYS.getLengthOfMonth(IndianEra.SAKA, context.iyear, context.imonth);
                case DAY_OF_YEAR_INDEX:
                    return CALSYS.getLengthOfYear(IndianEra.SAKA, context.iyear);
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

    }

    private static class MonthRule
        implements ElementRule<IndianCalendar, IndianMonth> {

        //~ Methoden ------------------------------------------------------

        @Override
        public IndianMonth getValue(IndianCalendar context) {

            return context.getMonth();

        }

        @Override
        public IndianMonth getMinimum(IndianCalendar context) {

            return IndianMonth.CHAITRA;

        }

        @Override
        public IndianMonth getMaximum(IndianCalendar context) {

            return IndianMonth.PHALGUNA;

        }

        @Override
        public boolean isValid(
            IndianCalendar context,
            IndianMonth value
        ) {

            return (value != null);

        }

        @Override
        public IndianCalendar withValue(
            IndianCalendar context,
            IndianMonth value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing month.");
            }

            int m = value.getValue();
            int dmax = CALSYS.getLengthOfMonth(IndianEra.SAKA, context.iyear, m);
            int d = Math.min(context.idom, dmax);
            return new IndianCalendar(context.iyear, m, d);

        }

        @Override
        public ChronoElement<?> getChildAtFloor(IndianCalendar context) {

            return DAY_OF_MONTH;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(IndianCalendar context) {

            return DAY_OF_MONTH;

        }

    }

    private static class EraRule
        implements ElementRule<IndianCalendar, IndianEra> {

        //~ Methoden ------------------------------------------------------

        @Override
        public IndianEra getValue(IndianCalendar context) {

            return IndianEra.SAKA;

        }

        @Override
        public IndianEra getMinimum(IndianCalendar context) {

            return IndianEra.SAKA;

        }

        @Override
        public IndianEra getMaximum(IndianCalendar context) {

            return IndianEra.SAKA;

        }

        @Override
        public boolean isValid(
            IndianCalendar context,
            IndianEra value
        ) {

            return (value != null);

        }

        @Override
        public IndianCalendar withValue(
            IndianCalendar context,
            IndianEra value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing era value.");
            }

            return context;

        }

        @Override
        public ChronoElement<?> getChildAtFloor(IndianCalendar context) {

            return YEAR_OF_ERA;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(IndianCalendar context) {

            return YEAR_OF_ERA;

        }

    }

    private static class Merger
        implements ChronoMerger<IndianCalendar> {

        //~ Methoden ------------------------------------------------------

        @Override
        public String getFormatPattern(
            DisplayStyle style,
            Locale locale
        ) {

            return GenericDatePatterns.get("indian", style, locale);

        }

        @Override
        public IndianCalendar createFrom(
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
        public IndianCalendar createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean preparsing
        ) {

            boolean lenient = attributes.get(Attributes.LENIENCY, Leniency.SMART).isLax();
            return this.createFrom(entity, attributes, lenient, preparsing);

        }

        @Override
        public IndianCalendar createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean lenient,
            boolean preparsing
        ) {

            int year = entity.getInt(YEAR_OF_ERA);

            if (year == Integer.MIN_VALUE) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Missing Indian year.");
                return null;
            }

            if (entity.contains(MONTH_OF_YEAR)) {
                int month = entity.get(MONTH_OF_YEAR).getValue();
                int dom = entity.getInt(DAY_OF_MONTH);

                if (dom != Integer.MIN_VALUE) {
                    if (CALSYS.isValid(IndianEra.SAKA, year, month, dom)) {
                        return IndianCalendar.of(year, month, dom);
                    } else {
                        entity.with(ValidationElement.ERROR_MESSAGE, "Invalid Indian date.");
                    }
                }
            } else {
                int doy = entity.getInt(DAY_OF_YEAR);
                if (doy != Integer.MIN_VALUE) {
                    if (doy > 0) {
                        int month = 1;
                        int daycount = 0;
                        while (month <= 12) {
                            int len = CALSYS.getLengthOfMonth(IndianEra.SAKA, year, month);
                            if (doy > daycount + len) {
                                month++;
                                daycount += len;
                            } else {
                                return IndianCalendar.of(year, month, doy - daycount);
                            }
                        }
                    }
                    entity.with(ValidationElement.ERROR_MESSAGE, "Invalid Indian date.");
                }
            }

            return null;

        }

        @Override
        public ChronoDisplay preformat(IndianCalendar context, AttributeQuery attributes) {

            return context;

        }

        @Override
        public Chronology<?> preparser() {

            return null;

        }

        @Override
        public StartOfDay getDefaultStartOfDay() {

            return StartOfDay.MIDNIGHT;

        }

        @Override
        public int getDefaultPivotYear() {

            return PlainDate.axis().getDefaultPivotYear() - 78;

        }

    }

    private static class IndianUnitRule
        implements UnitRule<IndianCalendar> {

        //~ Instanzvariablen ----------------------------------------------

        private final Unit unit;

        //~ Konstruktoren -------------------------------------------------

        IndianUnitRule(Unit unit) {
            super();

            this.unit = unit;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public IndianCalendar addTo(IndianCalendar date, long amount) {

            switch (this.unit) {
                case YEARS:
                    amount = MathUtils.safeMultiply(amount, 12);
                    // fall-through
                case MONTHS:
                    long ym = MathUtils.safeAdd(ymValue(date), amount);
                    int year = MathUtils.safeCast(MathUtils.floorDivide(ym, 12));
                    int month = MathUtils.floorModulo(ym, 12) + 1;
                    int dom =
                        Math.min(
                            date.idom,
                            CALSYS.getLengthOfMonth(IndianEra.SAKA, year, month));
                    return IndianCalendar.of(year, month, dom);
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
        public long between(IndianCalendar start, IndianCalendar end) {

            switch (this.unit) {
                case YEARS:
                    return IndianCalendar.Unit.MONTHS.between(start, end) / 12;
                case MONTHS:
                    long delta = ymValue(end) - ymValue(start);
                    if ((delta > 0) && (end.idom < start.idom)) {
                        delta--;
                    } else if ((delta < 0) && (end.idom > start.idom)) {
                        delta++;
                    }
                    return delta;
                case WEEKS:
                    return IndianCalendar.Unit.DAYS.between(start, end) / 7;
                case DAYS:
                    return CALSYS.transform(end) - CALSYS.transform(start);
                default:
                    throw new UnsupportedOperationException(this.unit.name());
            }

        }

        private static int ymValue(IndianCalendar date) {

            return date.iyear * 12 + date.imonth - 1;

        }

    }

}
