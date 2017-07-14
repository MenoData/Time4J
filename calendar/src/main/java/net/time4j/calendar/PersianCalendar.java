/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (PersianCalendar.java) is part of project Time4J.
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
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


/**
 * <p>Represents the Solar Hijri calendar which is officially used in Iran and Afghanistan. </p>
 *
 * <p>It is a solar calendar which is in close agreement with the astronomical seasons. The vernal equinox
 * serves as the first day of the Persian year (Farvardin 1st). If it is observed before noon at local time
 * in Teheran then the associated day is the first day of the year else the next day. More details about the
 * length of the vernal-equinox-year see:
 * <a href="http://aramis.obspm.fr/~heydari/divers/ir-cal-eng.html">A concise review of the Iranian calendar</a> </p>
 *
 * <p>The calendar year is divided into 12 Persian months. The first 6 months are 31 days long. The following
 * months are 30 days long with the exception of the last month whose length is 29 days in normal years and
 * 30 days in leap years. The algorithm is based on the excellent work of Borkowski who describes the details at
 * <a href="http://www.astro.uni.torun.pl/~kb/Papers/EMP/PersianC-EMP.htm">The Persian calendar for 3000 years</a>.
 * It is in agreement with the well known cycle proposed by Omar Khayam for the years 1178-1633 (ISO: 1799-2254).
 * However, dates calculated in far future beyond 2123 can still be wrong due to the uncertainty of astronomical
 * term delta-T and should be considered as approximation. </p>
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
 *     ChronoFormatter&lt;PersianCalendar&gt; formatter =
 *       ChronoFormatter.setUp(PersianCalendar.axis(), new Locale(&quot;fa&quot;))
 *       .addPattern(&quot;EEE, d. MMMM yy&quot;, PatternType.NON_ISO_DATE).build();
 *     PlainDate today = SystemClock.inLocalView().today();
 *     PersianCalendar jalali = today.transform(PersianCalendar.class);
 *     System.out.println(formatter.format(jalali));
 * </pre>
 *
 * @author  Meno Hochschild
 * @since   3.9/4.6
 */
/*[deutsch]
 * <p>Repr&auml;sentiert den persischen Kalender, der offiziell in Iran und Afghanistan benutzt wird. </p>
 *
 * <p>Es handelt sich um einen solaren Kalender, der in enger &Uuml;bereinstimmung mit den astronomischen
 * Saisons existiert. Als Startpunkt dient die astronomische Fr&uuml;hlingstagundnachtgleiche. Wird sie vor
 * 12 Uhr lokaler Ortszeit in Teheran beobachtet, dann ist der assoziierte Tag der erste Tag des persischen
 * Kalenders, sonst der n&auml;chste Tag. Mehr Details zur L&auml;nge des persischen Jahres:
 * <a href="http://aramis.obspm.fr/~heydari/divers/ir-cal-eng.html">A concise review of the Iranian calendar</a> </p>
 *
 * <p>Das Kalendarjahr wird in 12 persische Monate geteilt. Die ersten 6 Monate sind 31 Tage lang, die folgenden
 * nur 30 Tage mit Ausnahme des letzten Monats, dessen L&auml;nge 29 oder 30 Tage betr&auml;gt je nachdem ob ein
 * Schaltjahr vorliegt. Der Algorithmus basiert auf der ausgezeichneten Arbeit von Borkowski, der die Details auf
 * <a href="http://www.astro.uni.torun.pl/~kb/Papers/EMP/PersianC-EMP.htm">The Persian calendar for 3000 years</a>.
 * beschreibt.  Allerdings k&ouml;nnen Datumswerte nach dem gregorianischen Jahr 2123 falsch sein, weil der
 * astronomische Term delta-T sich nicht beliebig genau in die Zukunft prognostizieren l&auml;sst. Solche Datumswerte
 * weit in der Zukunft sind daher als N&auml;herung anzusehen.</p>
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
 *     ChronoFormatter&lt;PersianCalendar&gt; formatter =
 *       ChronoFormatter.setUp(PersianCalendar.axis(), new Locale(&quot;fa&quot;))
 *       .addPattern(&quot;EEE, d. MMMM yy&quot;, PatternType.NON_ISO_DATE).build();
 *     PlainDate today = SystemClock.inLocalView().today();
 *     PersianCalendar jalali = today.transform(PersianCalendar.class);
 *     System.out.println(formatter.format(jalali));
 * </pre>
 *
 * @author  Meno Hochschild
 * @since   3.9/4.6
 */
@CalendarType("persian")
public final class PersianCalendar
    extends Calendrical<PersianCalendar.Unit, PersianCalendar>
    implements LocalizedPatternSupport {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int YEAR_INDEX = 0;
    private static final int DAY_OF_MONTH_INDEX = 2;
    private static final int DAY_OF_YEAR_INDEX = 3;

    /**
     * <p>Represents the Persian era. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die persische &Auml;ra. </p>
     */
    @FormattableElement(format = "G")
    public static final ChronoElement<PersianEra> ERA =
        new StdEnumDateElement<PersianEra, PersianCalendar>(
            "ERA", PersianCalendar.class, PersianEra.class, 'G');

    /**
     * <p>Represents the Persian year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert das persische Jahr. </p>
     */
    @FormattableElement(format = "y")
    public static final StdCalendarElement<Integer, PersianCalendar> YEAR_OF_ERA =
        new StdIntegerDateElement<PersianCalendar>(
            "YEAR_OF_ERA",
            PersianCalendar.class,
            1,
            3000,
            'y',
            null,
            null);

    /**
     * <p>Represents the Persian month. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den persischen Monat. </p>
     */
    @FormattableElement(format = "M", standalone = "L")
    public static final StdCalendarElement<PersianMonth, PersianCalendar> MONTH_OF_YEAR =
        new StdEnumDateElement<PersianMonth, PersianCalendar>(
            "MONTH_OF_YEAR",
            PersianCalendar.class,
            PersianMonth.class,
            'M');

    /**
     * <p>Represents the Persian day of month. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den persischen Tag des Monats. </p>
     */
    @FormattableElement(format = "d")
    public static final StdCalendarElement<Integer, PersianCalendar> DAY_OF_MONTH =
        new StdIntegerDateElement<PersianCalendar>("DAY_OF_MONTH", PersianCalendar.class, 1, 31, 'd');

    /**
     * <p>Represents the Persian day of year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den persischen Tag des Jahres. </p>
     */
    @FormattableElement(format = "D")
    public static final StdCalendarElement<Integer, PersianCalendar> DAY_OF_YEAR =
        new StdIntegerDateElement<PersianCalendar>("DAY_OF_YEAR", PersianCalendar.class, 1, 365, 'D');

    /**
     * <p>Represents the Persian day of week. </p>
     *
     * <p>If the day-of-week is set to a new value then Time4J handles the Persian calendar week
     * as starting on Saturday. </p>
     *
     * @see     #getDefaultWeekmodel()
     * @see     CommonElements#localDayOfWeek(Chronology, Weekmodel)
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den persischen Tag der Woche. </p>
     *
     * <p>Wenn der Tag der Woche auf einen neuen Wert gesetzt wird, behandelt Time4J die persische
     * Kalenderwoche so, da&szlig; sie am Samstag beginnt. </p>
     *
     * @see     #getDefaultWeekmodel()
     * @see     CommonElements#localDayOfWeek(Chronology, Weekmodel)
     */
    @FormattableElement(format = "E")
    public static final StdCalendarElement<Weekday, PersianCalendar> DAY_OF_WEEK =
        new StdWeekdayElement<PersianCalendar>(PersianCalendar.class, getDefaultWeekmodel());

    private static final WeekdayInMonthElement<PersianCalendar> WIM_ELEMENT =
        new WeekdayInMonthElement<PersianCalendar>(PersianCalendar.class, DAY_OF_MONTH, DAY_OF_WEEK);

    /**
     * <p>Element with the ordinal day-of-week within given calendar month. </p>
     */
    /*[deutsch]
     * <p>Element mit dem x-ten Wochentag im Monat. </p>
     */
    @FormattableElement(format = "F")
    public static final OrdinalWeekdayElement<PersianCalendar> WEEKDAY_IN_MONTH = WIM_ELEMENT;

    private static final PersianAlgorithm DEFAULT_COMPUTATION = PersianAlgorithm.BORKOWSKI;
    private static final EraYearMonthDaySystem<PersianCalendar> CALSYS;
    private static final TimeAxis<PersianCalendar.Unit, PersianCalendar> ENGINE;

    static {
        CALSYS = new Transformer();

        TimeAxis.Builder<PersianCalendar.Unit, PersianCalendar> builder =
            TimeAxis.Builder.setUp(
                PersianCalendar.Unit.class,
                PersianCalendar.class,
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
                new RelatedGregorianYearRule<PersianCalendar>(CALSYS, DAY_OF_YEAR))
            .appendUnit(
                Unit.YEARS,
                new PersianUnitRule(Unit.YEARS),
                Unit.YEARS.getLength(),
                Collections.singleton(Unit.MONTHS))
            .appendUnit(
                Unit.MONTHS,
                new PersianUnitRule(Unit.MONTHS),
                Unit.MONTHS.getLength(),
                Collections.singleton(Unit.YEARS))
            .appendUnit(
                Unit.WEEKS,
                new PersianUnitRule(Unit.WEEKS),
                Unit.WEEKS.getLength(),
                Collections.singleton(Unit.DAYS))
            .appendUnit(
                Unit.DAYS,
                new PersianUnitRule(Unit.DAYS),
                Unit.DAYS.getLength(),
                Collections.singleton(Unit.WEEKS))
            .appendExtension(
                new CommonElements.Weekengine(
                    PersianCalendar.class,
                    DAY_OF_MONTH,
                    DAY_OF_YEAR,
                    getDefaultWeekmodel()
                )
            );
        ENGINE = builder.build();
    }

    private static final long serialVersionUID = -411339992208638290L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final int pyear;
    private transient final int pmonth;
    private transient final int pdom;

    //~ Konstruktoren -----------------------------------------------------

    // must not be called outside of package
    PersianCalendar(
        int pyear,
        int pmonth,
        int pdom
    ) {
        super();

        this.pyear = pyear;
        this.pmonth = pmonth;
        this.pdom = pdom;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new instance of a Persian calendar date. </p>
     *
     * @param   pyear   Persian year in the range 1-3000
     * @param   pmonth  Persian month
     * @param   pdom    Persian day of month
     * @return  new instance of {@code PersianCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.9/4.6
     */
    /*[deutsch]
     * <p>Erzeugt ein neues persisches Kalenderdatum. </p>
     *
     * @param   pyear   Persian year in the range 1-3000
     * @param   pmonth  Persian month
     * @param   pdom    Persian day of month
     * @return  new instance of {@code PersianCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.9/4.6
     */
    public static PersianCalendar of(
        int pyear,
        PersianMonth pmonth,
        int pdom
    ) {

        return PersianCalendar.of(pyear, pmonth.getValue(), pdom);

    }

    /**
     * <p>Creates a new instance of a Persian calendar date. </p>
     *
     * @param   pyear   Persian year in the range 1-3000
     * @param   pmonth  Persian month
     * @param   pdom    Persian day of month
     * @return  new instance of {@code PersianCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.9/4.6
     */
    /*[deutsch]
     * <p>Erzeugt ein neues persisches Kalenderdatum. </p>
     *
     * @param   pyear   Persian year in the range 1-3000
     * @param   pmonth  Persian month
     * @param   pdom    Persian day of month
     * @return  new instance of {@code PersianCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.9/4.6
     */
    public static PersianCalendar of(
        int pyear,
        int pmonth,
        int pdom
    ) {

        if (!CALSYS.isValid(PersianEra.ANNO_PERSICO, pyear, pmonth, pdom)) {
            throw new IllegalArgumentException(
                "Invalid Persian date: year=" + pyear + ", month=" + pmonth + ", day=" + pdom);
        }

        return new PersianCalendar(pyear, pmonth, pdom);

    }

    /**
     * <p>Obtains the current calendar date in system time. </p>
     *
     * <p>Convenient short-cut for: {@code SystemClock.inLocalView().now(PersianCalendar.axis())}. </p>
     *
     * @return  current calendar date in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(net.time4j.engine.Chronology)
     * @since   3.23/4.19
     */
    /*[deutsch]
     * <p>Ermittelt das aktuelle Kalenderdatum in der Systemzeit. </p>
     *
     * <p>Bequeme Abk&uuml;rzung f&uuml;r: {@code SystemClock.inLocalView().now(PersianCalendar.axis())}. </p>
     *
     * @return  current calendar date in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(net.time4j.engine.Chronology)
     * @since   3.23/4.19
     */
    public static PersianCalendar nowInSystemTime() {

        return SystemClock.inLocalView().now(PersianCalendar.axis());

    }

    /**
     * <p>Yields the Persian era. </p>
     *
     * @return  {@link PersianEra#ANNO_PERSICO}
     * @since   3.9/4.6
     */
    /*[deutsch]
     * <p>Liefert die persische &Auml;ra. </p>
     *
     * @return  {@link PersianEra#ANNO_PERSICO}
     * @since   3.9/4.6
     */
    public PersianEra getEra() {

        return PersianEra.ANNO_PERSICO;

    }

    /**
     * <p>Yields the Persian year. </p>
     *
     * @return  int
     * @since   3.9/4.6
     */
    /*[deutsch]
     * <p>Liefert das persische Jahr. </p>
     *
     * @return  int
     * @since   3.9/4.6
     */
    public int getYear() {

        return this.pyear;

    }

    /**
     * <p>Yields the Persian month. </p>
     *
     * @return  enum
     * @since   3.9/4.6
     */
    /*[deutsch]
     * <p>Liefert den persischen Monat. </p>
     *
     * @return  enum
     * @since   3.9/4.6
     */
    public PersianMonth getMonth() {

        return PersianMonth.valueOf(this.pmonth);

    }

    /**
     * <p>Yields the Persian day of month. </p>
     *
     * @return  int
     * @since   3.9/4.6
     */
    /*[deutsch]
     * <p>Liefert den persischen Tag des Monats. </p>
     *
     * @return  int
     * @since   3.9/4.6
     */
    public int getDayOfMonth() {

        return this.pdom;

    }

    /**
     * <p>Determines the day of week. </p>
     *
     * <p>The Persian calendar also uses a 7-day-week. </p>
     *
     * @return  Weekday
     * @since   3.9/4.6
     */
    /*[deutsch]
     * <p>Ermittelt den Wochentag. </p>
     *
     * <p>Der persische Kalendar verwendet ebenfalls eine 7-Tage-Woche. </p>
     *
     * @return  Weekday
     * @since   3.9/4.6
     */
    public Weekday getDayOfWeek() {

        long utcDays = CALSYS.transform(this);
        return Weekday.valueOf(MathUtils.floorModulo(utcDays + 5, 7) + 1);

    }

    /**
     * <p>Yields the Persian day of year. </p>
     *
     * @return  int
     * @since   3.9/4.6
     */
    /*[deutsch]
     * <p>Liefert den persischen Tag des Jahres. </p>
     *
     * @return  int
     * @since   3.9/4.6
     */
    public int getDayOfYear() {

        return this.get(DAY_OF_YEAR).intValue();

    }

    /**
     * <p>Obtains an alternative date view specific for given algorithm. </p>
     *
     * @param   algorithm   calendar computation
     * @return  Persian date (possibly modified)
     * @throws  IllegalArgumentException in case of date overflow
     * @since   3.33/4.28
     */
    /*[deutsch]
     * <p>Erh&auml;lt eine alternative Datumssicht spezifisch f&uuml;r den angegebenen Algorithmus. </p>
     *
     * @param   algorithm   calendar computation
     * @return  Persian date (possibly modified)
     * @throws  IllegalArgumentException in case of date overflow
     * @since   3.33/4.28
     */
    public Date getDate(PersianAlgorithm algorithm) {

        ZonalOffset offset = PersianAlgorithm.STD_OFFSET;

        if (algorithm == DEFAULT_COMPUTATION) {
            return new Date(this, DEFAULT_COMPUTATION, offset);
        }

        long utcDays = DEFAULT_COMPUTATION.transform(this, offset);
        return new Date(algorithm.transform(utcDays, offset), algorithm, offset);

    }

    /**
     * <p>Obtains an astronomical date view specific for given timezone offset. </p>
     *
     * @param   offset      timezone offset
     * @return  Persian date based on astronomical calculations for given offset (possibly modified)
     * @throws  IllegalArgumentException in case of date overflow
     * @see     PersianAlgorithm#ASTRONOMICAL
     * @see     #getDate(PersianAlgorithm)
     * @since   3.33/4.28
     */
    /*[deutsch]
     * <p>Erh&auml;lt eine astronomische Datumssicht spezifisch f&uuml;r die angegebene Zeitzonenverschiebung. </p>
     *
     * @param   offset      timezone offset
     * @return  Persian date based on astronomical calculations for given offset (possibly modified)
     * @throws  IllegalArgumentException in case of date overflow
     * @see     PersianAlgorithm#ASTRONOMICAL
     * @see     #getDate(PersianAlgorithm)
     * @since   3.33/4.28
     */
    public Date getDate(ZonalOffset offset) {

        if (offset == null) {
            throw new NullPointerException("Missing timezone offset.");
        }

        PersianAlgorithm algorithm = PersianAlgorithm.ASTRONOMICAL;
        long utcDays = DEFAULT_COMPUTATION.transform(this, PersianAlgorithm.STD_OFFSET);
        return new Date(algorithm.transform(utcDays, offset), algorithm, offset);

    }

    /**
     * <p>Yields the length of current Persian month in days. </p>
     *
     * @return  int
     * @since   3.9/4.6
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge des aktuellen persischen Monats in Tagen. </p>
     *
     * @return  int
     * @since   3.9/4.6
     */
    public int lengthOfMonth() {

        return CALSYS.getLengthOfMonth(PersianEra.ANNO_PERSICO, this.pyear, this.pmonth);

    }

    /**
     * <p>Yields the length of current Persian year in days. </p>
     *
     * @return  int
     * @since   3.9/4.6
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge des aktuellen persischen Jahres in Tagen. </p>
     *
     * @return  int
     * @since   3.9/4.6
     */
    public int lengthOfYear() {

        return CALSYS.getLengthOfYear(PersianEra.ANNO_PERSICO, this.pyear);

    }

    /**
     * <p>Is the year of this date a leap year? </p>
     *
     * @return  boolean
     * @since   3.9/4.6
     */
    /*[deutsch]
     * <p>Liegt dieses Datum in einem Schaltjahr? </p>
     *
     * @return  boolean
     * @since   3.9/4.6
     */
    public boolean isLeapYear() {

        return (this.lengthOfYear() > 365);

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
     * @since   3.9/4.6
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
     * @since   3.9/4.6
     */
    public GeneralTimestamp<PersianCalendar> at(PlainTime time) {

        return GeneralTimestamp.of(this, time);

    }

    /**
     * <p>Is equivalent to {@code at(PlainTime.of(hour, minute))}. </p>
     *
     * @param   hour        hour of day in range (0-24)
     * @param   minute      minute of hour in range (0-59)
     * @return  general timestamp as composition of this date and given time
     * @throws  IllegalArgumentException if any argument is out of range
     * @since   3.9/4.6
     */
    /*[deutsch]
     * <p>Entspricht {@code at(PlainTime.of(hour, minute))}. </p>
     *
     * @param   hour        hour of day in range (0-24)
     * @param   minute      minute of hour in range (0-59)
     * @return  general timestamp as composition of this date and given time
     * @throws  IllegalArgumentException if any argument is out of range
     * @since   3.9/4.6
     */
    public GeneralTimestamp<PersianCalendar> atTime(
        int hour,
        int minute
    ) {

        return this.at(PlainTime.of(hour, minute));

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof PersianCalendar) {
            PersianCalendar that = (PersianCalendar) obj;
            return (
                (this.pdom == that.pdom)
                && (this.pmonth == that.pmonth)
                && (this.pyear == that.pyear)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return (17 * this.pdom + 31 * this.pmonth + 37 * this.pyear);

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(32);
        sb.append("AP-");
        String y = String.valueOf(this.pyear);
        for (int i = y.length(); i < 4; i++) {
            sb.append('0');
        }
        sb.append(y);
        sb.append('-');
        if (this.pmonth < 10) {
            sb.append('0');
        }
        sb.append(this.pmonth);
        sb.append('-');
        if (this.pdom < 10) {
            sb.append('0');
        }
        sb.append(this.pdom);
        return sb.toString();

    }

    /**
     * <p>Obtains the standard week model of this calendar. </p>
     *
     * <p>The persian calendar usually starts on Saturday. </p>
     *
     * @return  Weekmodel
     * @since   3.24/4.20
     */
    /*[deutsch]
     * <p>Ermittelt das Standardwochenmodell dieses Kalenders. </p>
     *
     * <p>Der persische Kalender startet normalerweise am Samstag. </p>
     *
     * @return  Weekmodel
     * @since   3.24/4.20
     */
    public static Weekmodel getDefaultWeekmodel() {

        return Weekmodel.of(Weekday.SATURDAY, 1, Weekday.FRIDAY, Weekday.FRIDAY); // Iran

    }

    /**
     * <p>Returns the associated time axis. </p>
     *
     * @return  chronology
     * @since   3.9/4.6
     */
    /*[deutsch]
     * <p>Liefert die zugeh&ouml;rige Zeitachse. </p>
     *
     * @return  chronology
     * @since   3.9/4.6
     */
    public static TimeAxis<Unit, PersianCalendar> axis() {

        return ENGINE;

    }

    @Override
    protected TimeAxis<Unit, PersianCalendar> getChronology() {

        return ENGINE;

    }

    @Override
    protected PersianCalendar getContext() {

        return this;

    }

    /**
     * @serialData  Uses <a href="../../../serialized-form.html#net.time4j.calendar.SPX">
     *              a dedicated serialization form</a> as proxy. The first byte contains
     *              the type-ID {@code 2}. Then the year is written as int, finally
     *              month and day-of-month as bytes.
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.PERSIAN);

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

    /**
     * <p>Defines come calendar units for the Persian calendar. </p>
     *
     * @since   3.9/4.6
     */
    /*[deutsch]
     * <p>Definiert einige kalendarische Zeiteinheiten f&uuml;r den persischen Kalender. </p>
     *
     * @since   3.9/4.6
     */
    public static enum Unit
        implements ChronoUnit {

        //~ Statische Felder/Initialisierungen ----------------------------

        YEARS(365.2424 * 86400.0), // rounded length of vernal equinox year

        MONTHS(365.2424 * 86400.0 / 12),

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
         * <p>Calculates the difference between given Persian dates in this unit. </p>
         *
         * @param   start   start date (inclusive)
         * @param   end     end date (exclusive)
         * @return  difference counted in this unit
         * @since   3.9/4.6
         */
        /*[deutsch]
         * <p>Berechnet die Differenz zwischen den angegebenen Datumsparametern in dieser Zeiteinheit. </p>
         *
         * @param   start   start date (inclusive)
         * @param   end     end date (exclusive)
         * @return  difference counted in this unit
         * @since   3.9/4.6
         */
        public int between(
            PersianCalendar start,
            PersianCalendar end
        ) {

            return (int) start.until(end, this); // safe

        }

    }

    /**
     * <p>Static view of calendar date taking into account possibly different calendar algorithms. </p>
     *
     * <p>Following example demonstrates the difference between the standard algorithm and the algorithm
     * proposed by A. Birashk: </p>
     *
     * <pre>
     *      PersianCalendar pcal = PersianCalendar.of(1403, 12, 30); // = 2025-03-20 (gregorian)
     *      PersianCalendar.Date birashk = pcal.getDate(PersianAlgorithm.BIRASHK); // AP-1404-01-01[BIRASHK]
     *
     *      assertThat(birashk.get(PersianCalendar.YEAR_OF_ERA), is(Integer.valueOf(1404)));
     *      assertThat(birashk.getInt(PersianCalendar.YEAR_OF_ERA), is(1404));
     *      assertThat(birashk.get(PersianCalendar.MONTH_OF_YEAR), is(PersianMonth.FARVARDIN));
     *      assertThat(birashk.getInt(PersianCalendar.DAY_OF_MONTH), is(1));
     *      assertThat(birashk.getInt(PersianCalendar.DAY_OF_YEAR), is(1));
     *      assertThat(birashk.get(PersianCalendar.DAY_OF_WEEK), is(pcal.getDayOfWeek()));
     *      assertThat(birashk.getMaximum(PersianCalendar.DAY_OF_MONTH), is(31));
     *      assertThat(birashk.getMaximum(PersianCalendar.DAY_OF_YEAR), is(366));
     * </pre>
     *
     * <p>Note: Only elements registered in the Persian calendar chronology are supported. </p>
     *
     * @see     #getDate(PersianAlgorithm)
     * @see     #getDate(ZonalOffset)
     * @see     PersianAlgorithm#attribute()
     * @since   3.33/4.28
     */
    /*[deutsch]
     * <p>Statische Ansicht eines Kalenderdatums, das auf verschiedenen Kalenderalgorithmen basieren kann. </p>
     *
     * <p>Das folgende Beispiel zeigt den Unterschied zwischen dem Standardalgorithmus und dem
     * von A. Birashk vorgeschlagenen Algorithmus: </p>
     *
     * <pre>
     *      PersianCalendar pcal = PersianCalendar.of(1403, 12, 30); // = 2025-03-20 (gregorian)
     *      PersianCalendar.Date birashk = pcal.getDate(PersianAlgorithm.BIRASHK); // AP-1404-01-01[BIRASHK]
     *
     *      assertThat(birashk.get(PersianCalendar.YEAR_OF_ERA), is(Integer.valueOf(1404)));
     *      assertThat(birashk.getInt(PersianCalendar.YEAR_OF_ERA), is(1404));
     *      assertThat(birashk.get(PersianCalendar.MONTH_OF_YEAR), is(PersianMonth.FARVARDIN));
     *      assertThat(birashk.getInt(PersianCalendar.DAY_OF_MONTH), is(1));
     *      assertThat(birashk.getInt(PersianCalendar.DAY_OF_YEAR), is(1));
     *      assertThat(birashk.get(PersianCalendar.DAY_OF_WEEK), is(pcal.getDayOfWeek()));
     *      assertThat(birashk.getMaximum(PersianCalendar.DAY_OF_MONTH), is(31));
     *      assertThat(birashk.getMaximum(PersianCalendar.DAY_OF_YEAR), is(366));
     * </pre>
     *
     * <p>Hinweis: Nur in der persischen Kalenderchronologie registrierte Elemente werden unterst&uuml;tzt. </p>
     *
     * @see     #getDate(PersianAlgorithm)
     * @see     #getDate(ZonalOffset)
     * @see     PersianAlgorithm#attribute()
     * @since   3.33/4.28
     */
    public static final class Date
        implements ChronoDisplay {

        //~ Instanzvariablen ----------------------------------------------

        private final PersianCalendar delegate;
        private final PersianAlgorithm algorithm;
        private final ZonalOffset offset;

        //~ Konstruktoren -------------------------------------------------

        private Date(
            PersianCalendar delegate,
            PersianAlgorithm algorithm,
            ZonalOffset offset
        ) {
            super();

            this.delegate = delegate;
            this.algorithm = algorithm;
            this.offset = offset;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public boolean contains(ChronoElement<?> element) {
            return ENGINE.isRegistered(element);
        }

        @Override
        public <V> V get(ChronoElement<V> element) {
            if (element == DAY_OF_WEEK) {
                long utcDays = this.algorithm.transform(this.delegate, this.offset);
                return element.getType().cast(Weekday.valueOf(MathUtils.floorModulo(utcDays + 5, 7) + 1));
            } else if (element == DAY_OF_YEAR) {
                int doy = 0;
                for (int m = 1; m < this.delegate.pmonth; m++) {
                    if (m <= 6) {
                        doy += 31;
                    } else {
                        doy += 30;
                    }
                }
                return element.getType().cast(Integer.valueOf(doy + this.delegate.pdom));
            } else if (element == WEEKDAY_IN_MONTH) {
                return element.getType().cast(Integer.valueOf(MathUtils.floorDivide(this.delegate.pdom - 1, 7) + 1));
            } else if (element == CommonElements.RELATED_GREGORIAN_YEAR) {
                return element.getType().cast(Integer.valueOf(this.delegate.getYear() + 621));
            } else if (element instanceof EpochDays) {
                EpochDays ed = EpochDays.class.cast(element);
                long utcDays = this.algorithm.transform(this.delegate, this.offset);
                return element.getType().cast(Long.valueOf(ed.transform(utcDays, EpochDays.UTC)));
            } else if (ENGINE.isRegistered(element)) {
                return this.delegate.get(element);
            } else {
                throw new ChronoException("Persian dates only support registered elements.");
            }
        }

        @Override
        public int getInt(ChronoElement<Integer> element) {
            if (element == DAY_OF_MONTH) {
                return this.delegate.pdom;
            } else if (element == YEAR_OF_ERA) {
                return this.delegate.pyear;
            } else if (element == DAY_OF_YEAR) {
                int doy = 0;
                for (int m = 1; m < this.delegate.pmonth; m++) {
                    if (m <= 6) {
                        doy += 31;
                    } else {
                        doy += 30;
                    }
                }
                return doy + this.delegate.pdom;
            } else if (element == WEEKDAY_IN_MONTH) {
                return MathUtils.floorDivide(this.delegate.pdom - 1, 7) + 1;
            } else if (element == CommonElements.RELATED_GREGORIAN_YEAR) {
                return (this.delegate.getYear() + 621);
            } else if (ENGINE.isRegistered(element)) {
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
                throw new ChronoException("Persian dates only support registered elements.");
            }
        }

        @Override
        public <V> V getMaximum(ChronoElement<V> element) {
            if (element == DAY_OF_MONTH) {
                int month = this.delegate.pmonth;
                int max;
                if (month <= 6) {
                    max = 31;
                } else if (month <= 11) {
                    max = 30;
                } else {
                    max = (this.algorithm.isLeapYear(this.delegate.pyear, this.offset) ? 30 : 29);
                }
                return element.getType().cast(Integer.valueOf(max));
            } else if (element == DAY_OF_YEAR) {
                int max = (this.algorithm.isLeapYear(this.delegate.pyear, this.offset) ? 366 : 365);
                return element.getType().cast(Integer.valueOf(max));
            } else if (element == WEEKDAY_IN_MONTH) {
                int dom = this.delegate.pdom;
                int max = this.getMaximum(DAY_OF_MONTH).intValue();
                while (dom + 7 <= max) {
                    dom += 7;
                }
                return element.getType().cast(MathUtils.floorDivide(dom - 1, 7) + 1);
            } else if (ENGINE.isRegistered(element)) {
                return this.delegate.getMaximum(element);
            } else {
                throw new ChronoException("Persian dates only support registered elements.");
            }
        }

        /**
         * <p>This date can only have a timezone (offset) if the underlying algorithm is
         * {@link PersianAlgorithm#ASTRONOMICAL}. </p>
         *
         * @return  boolean
         */
        /*[deutsch]
         * <p>Dieses Datum kann nur dann eine Zeitzonenverschiebung haben, wenn der zugrundeliegende
         * Algorithmus {@link PersianAlgorithm#ASTRONOMICAL} ist. </p>
         *
         * @return  boolean
         */
        @Override
        public boolean hasTimezone() {
            return (this.algorithm == PersianAlgorithm.ASTRONOMICAL);
        }

        /**
         * <p>If the underlying algorithm is {@link PersianAlgorithm#ASTRONOMICAL} then
         * this method will yield the timezone offset, usually Teheran Standard Time. </p>
         *
         * @return  timezone offset
         * @throws  ChronoException if the algorithm is not astronomical
         */
        /*[deutsch]
         * <p>Wenn der zugrundeliegende Algorithmus {@link PersianAlgorithm#ASTRONOMICAL} ist,
         * liefert diese Methode die Zeitzonenverschiebung, normalerweise die Standardzeit des Iran. </p>
         *
         * @return  timezone offset
         * @throws  ChronoException if the algorithm is not astronomical
         */
        @Override
        public ZonalOffset getTimezone() {
            if (this.hasTimezone()) {
                return this.offset;
            } else {
                throw new ChronoException("Timezone offset not defined.");
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (obj instanceof Date) {
                Date that = (Date) obj;
                if (this.algorithm != that.algorithm) {
                    return false;
                } else if ((this.algorithm == PersianAlgorithm.ASTRONOMICAL) && !this.offset.equals(that.offset)) {
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
            if (this.algorithm == PersianAlgorithm.ASTRONOMICAL) {
                sb.append(this.offset.toString());
            }
            sb.append(']');
            return sb.toString();
        }

    }

    private static class Transformer
        implements EraYearMonthDaySystem<PersianCalendar> {

        //~ Methoden ------------------------------------------------------

        @Override
        public boolean isValid(
            CalendarEra era,
            int yearOfEra,
            int monthOfYear,
            int dayOfMonth
        ) {

            return (
                (era == PersianEra.ANNO_PERSICO)
                && (yearOfEra >= 1)
                && (yearOfEra <= 3000)
                && (monthOfYear >= 1)
                && (monthOfYear <= 12)
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

            if (era != PersianEra.ANNO_PERSICO) {
                throw new IllegalArgumentException("Invalid era: " + era);
            }

            if (
                (era == PersianEra.ANNO_PERSICO)
                && (yearOfEra >= 1)
                && (yearOfEra <= 3000)
                && (monthOfYear >= 1)
                && (monthOfYear <= 12)
            ) {
                if (monthOfYear <= 6) {
                    return 31;
                } else if (monthOfYear <= 11) {
                    return 30;
                } else {
                    return ((this.getLengthOfYear(era, yearOfEra) == 365) ? 29 : 30);
                }
            }

            throw new IllegalArgumentException("Out of bounds: year=" + yearOfEra + ", month=" + monthOfYear);

        }

        @Override
        public int getLengthOfYear(
            CalendarEra era,
            int yearOfEra
        ) {

            if (era != PersianEra.ANNO_PERSICO) {
                throw new IllegalArgumentException("Invalid era: " + era);
            }

            return DEFAULT_COMPUTATION.isLeapYear(yearOfEra) ? 366 : 365;

        }

        @Override
        public PersianCalendar transform(long utcDays) {

            return DEFAULT_COMPUTATION.transform(utcDays, PersianAlgorithm.STD_OFFSET);

        }

        @Override
        public long transform(PersianCalendar date) {

            return DEFAULT_COMPUTATION.transform(date, PersianAlgorithm.STD_OFFSET);

        }

        @Override
        public long getMinimumSinceUTC() {

            PersianCalendar min = new PersianCalendar(1, 1, 1);
            return this.transform(min);

        }

        @Override
        public long getMaximumSinceUTC() {

            PersianCalendar max = new PersianCalendar(3001, 1, 1);
            return this.transform(max) - 1;

        }

        @Override
        public List<CalendarEra> getEras() {

            CalendarEra era = PersianEra.ANNO_PERSICO;
            return Collections.singletonList(era);

        }

    }

    private static class IntegerRule
        implements ElementRule<PersianCalendar, Integer> {

        //~ Instanzvariablen ----------------------------------------------

        private final int index;

        //~ Konstruktoren -------------------------------------------------

        IntegerRule(int index) {
            super();

            this.index = index;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Integer getValue(PersianCalendar context) {

            switch (this.index) {
                case YEAR_INDEX:
                    return context.pyear;
                case DAY_OF_MONTH_INDEX:
                    return context.pdom;
                case DAY_OF_YEAR_INDEX:
                    int doy = 0;
                    for (int m = 1; m < context.pmonth; m++) {
                        doy += CALSYS.getLengthOfMonth(PersianEra.ANNO_PERSICO, context.pyear, m);
                    }
                    return doy + context.pdom;
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public Integer getMinimum(PersianCalendar context) {

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
        public Integer getMaximum(PersianCalendar context) {

            switch (this.index) {
                case YEAR_INDEX:
                    return 3000;
                case DAY_OF_MONTH_INDEX:
                    return CALSYS.getLengthOfMonth(PersianEra.ANNO_PERSICO, context.pyear, context.pmonth);
                case DAY_OF_YEAR_INDEX:
                    return CALSYS.getLengthOfYear(PersianEra.ANNO_PERSICO, context.pyear);
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public boolean isValid(
            PersianCalendar context,
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
        public PersianCalendar withValue(
            PersianCalendar context,
            Integer value,
            boolean lenient
        ) {

            if (!this.isValid(context, value)) {
                throw new IllegalArgumentException("Out of range: " + value);
            }

            switch (this.index) {
                case YEAR_INDEX:
                    int y = value.intValue();
                    int dmax = CALSYS.getLengthOfMonth(PersianEra.ANNO_PERSICO, y, context.pmonth);
                    int d = Math.min(context.pdom, dmax);
                    return PersianCalendar.of(y, context.pmonth, d);
                case DAY_OF_MONTH_INDEX:
                    return new PersianCalendar(context.pyear, context.pmonth, value.intValue());
                case DAY_OF_YEAR_INDEX:
                    int delta = value.intValue() - this.getValue(context).intValue();
                    return context.plus(CalendarDays.of(delta));
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public ChronoElement<?> getChildAtFloor(PersianCalendar context) {

            if (this.index == YEAR_INDEX) {
                return MONTH_OF_YEAR;
            }

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(PersianCalendar context) {

            if (this.index == YEAR_INDEX) {
                return MONTH_OF_YEAR;
            }

            return null;

        }

    }

    private static class MonthRule
        implements ElementRule<PersianCalendar, PersianMonth> {

        //~ Methoden ------------------------------------------------------

        @Override
        public PersianMonth getValue(PersianCalendar context) {

            return context.getMonth();

        }

        @Override
        public PersianMonth getMinimum(PersianCalendar context) {

            return PersianMonth.FARVARDIN;

        }

        @Override
        public PersianMonth getMaximum(PersianCalendar context) {

            return PersianMonth.ESFAND;

        }

        @Override
        public boolean isValid(
            PersianCalendar context,
            PersianMonth value
        ) {

            return (value != null);

        }

        @Override
        public PersianCalendar withValue(
            PersianCalendar context,
            PersianMonth value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing month.");
            }

            int m = value.getValue();
            int dmax = CALSYS.getLengthOfMonth(PersianEra.ANNO_PERSICO, context.pyear, m);
            int d = Math.min(context.pdom, dmax);
            return new PersianCalendar(context.pyear, m, d);

        }

        @Override
        public ChronoElement<?> getChildAtFloor(PersianCalendar context) {

            return DAY_OF_MONTH;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(PersianCalendar context) {

            return DAY_OF_MONTH;

        }

    }

    private static class EraRule
        implements ElementRule<PersianCalendar, PersianEra> {

        //~ Methoden ------------------------------------------------------

        @Override
        public PersianEra getValue(PersianCalendar context) {

            return PersianEra.ANNO_PERSICO;

        }

        @Override
        public PersianEra getMinimum(PersianCalendar context) {

            return PersianEra.ANNO_PERSICO;

        }

        @Override
        public PersianEra getMaximum(PersianCalendar context) {

            return PersianEra.ANNO_PERSICO;

        }

        @Override
        public boolean isValid(
            PersianCalendar context,
            PersianEra value
        ) {

            return (value != null);

        }

        @Override
        public PersianCalendar withValue(
            PersianCalendar context,
            PersianEra value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing era value.");
            }

            return context;

        }

        @Override
        public ChronoElement<?> getChildAtFloor(PersianCalendar context) {

            return YEAR_OF_ERA;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(PersianCalendar context) {

            return YEAR_OF_ERA;

        }

    }

    private static class WeekdayRule
        implements ElementRule<PersianCalendar, Weekday> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Weekday getValue(PersianCalendar context) {

            return context.getDayOfWeek();

        }

        @Override
        public Weekday getMinimum(PersianCalendar context) {

            return Weekday.SATURDAY;

        }

        @Override
        public Weekday getMaximum(PersianCalendar context) {

            return Weekday.FRIDAY;

        }

        @Override
        public boolean isValid(
            PersianCalendar context,
            Weekday value
        ) {

            return (value != null);

        }

        @Override
        public PersianCalendar withValue(
            PersianCalendar context,
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
        public ChronoElement<?> getChildAtFloor(PersianCalendar context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(PersianCalendar context) {

            return null;

        }

    }

    private static class Merger
        implements ChronoMerger<PersianCalendar> {

        //~ Methoden ------------------------------------------------------

        @Override
        public String getFormatPattern(
            DisplayStyle style,
            Locale locale
        ) {

            return GenericDatePatterns.get("persian", style, locale);

        }

        @Override
        public StartOfDay getDefaultStartOfDay() {

            return StartOfDay.MIDNIGHT;

        }

        @Override
        public int getDefaultPivotYear() {

            return PlainDate.axis().getDefaultPivotYear() - 621;

        }

        @Override
        public PersianCalendar createFrom(
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
        public PersianCalendar createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean preparsing
        ) {

            boolean lenient = attributes.get(Attributes.LENIENCY, Leniency.SMART).isLax();
            return this.createFrom(entity, attributes, lenient, preparsing);

        }

        @Override
        public PersianCalendar createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean lenient,
            boolean preparsing
        ) {

            int pyear = entity.getInt(YEAR_OF_ERA);

            if (pyear == Integer.MIN_VALUE) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Missing Persian year.");
                return null;
            }

            PersianAlgorithm algorithm = attributes.get(PersianAlgorithm.attribute(), DEFAULT_COMPUTATION);
            ZonalOffset offset = PersianAlgorithm.STD_OFFSET;

            if ((algorithm == PersianAlgorithm.ASTRONOMICAL) && attributes.contains(Attributes.TIMEZONE_ID)) {
                TZID tzid = attributes.get(Attributes.TIMEZONE_ID);
                if (tzid instanceof ZonalOffset) {
                    offset = (ZonalOffset) tzid;
                }
            }

            if (entity.contains(MONTH_OF_YEAR)) {
                int pmonth = entity.get(MONTH_OF_YEAR).getValue();
                int pdom = entity.getInt(DAY_OF_MONTH);
                if (pdom != Integer.MIN_VALUE) {
                    if (algorithm.isValid(pyear, pmonth, pdom, offset)) {
                        PersianCalendar cal = new PersianCalendar(pyear, pmonth, pdom);
                        if (algorithm != DEFAULT_COMPUTATION) {
                            long utcDays = algorithm.transform(cal, offset);
                            cal = DEFAULT_COMPUTATION.transform(utcDays, PersianAlgorithm.STD_OFFSET);
                        }
                        return cal;
                    } else {
                        entity.with(ValidationElement.ERROR_MESSAGE, "Invalid Persian date.");
                    }
                }
            } else {
                int pdoy = entity.getInt(DAY_OF_YEAR);
                if (pdoy != Integer.MIN_VALUE) {
                    if (pdoy > 0) {
                        int pmonth = 1;
                        int daycount = 0;
                        while (pmonth <= 12) {
                            int len;
                            if (pmonth <= 6) {
                                len = 31;
                            } else if (pmonth <= 11) {
                                len = 30;
                            } else {
                                len = (algorithm.isLeapYear(pyear, offset) ? 30 : 29);
                            }
                            if (pdoy > daycount + len) {
                                pmonth++;
                                daycount += len;
                            } else if (algorithm.isValid(pyear, pmonth, pdoy - daycount, offset)) {
                                PersianCalendar cal = new PersianCalendar(pyear, pmonth, pdoy - daycount);
                                if (algorithm != DEFAULT_COMPUTATION) {
                                    long utcDays = algorithm.transform(cal, offset);
                                    cal = DEFAULT_COMPUTATION.transform(utcDays, PersianAlgorithm.STD_OFFSET);
                                }
                                return cal;
                            } else {
                                break;
                            }
                        }
                    }
                    entity.with(ValidationElement.ERROR_MESSAGE, "Invalid Persian date.");
                }
            }

            return null;

        }

        @Override
        public ChronoDisplay preformat(
            PersianCalendar context,
            AttributeQuery attributes
        ) {

            PersianAlgorithm algorithm = attributes.get(PersianAlgorithm.attribute(), DEFAULT_COMPUTATION);

            if (algorithm == DEFAULT_COMPUTATION) {
                return context;
            }

            if ((algorithm == PersianAlgorithm.ASTRONOMICAL) && attributes.contains(Attributes.TIMEZONE_ID)) {
                ZonalOffset offset = PersianAlgorithm.STD_OFFSET;
                TZID tzid = attributes.get(Attributes.TIMEZONE_ID);
                if (tzid instanceof ZonalOffset) {
                    offset = (ZonalOffset) tzid;
                }
                return context.getDate(offset);
            }

            return context.getDate(algorithm);

        }

        @Override
        public Chronology<?> preparser() {

            return null;

        }

    }

    private static class PersianUnitRule
        implements UnitRule<PersianCalendar> {

        //~ Instanzvariablen ----------------------------------------------

        private final Unit unit;

        //~ Konstruktoren -------------------------------------------------

        PersianUnitRule(Unit unit) {
            super();

            this.unit = unit;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public PersianCalendar addTo(PersianCalendar date, long amount) {

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
                            date.pdom,
                            CALSYS.getLengthOfMonth(PersianEra.ANNO_PERSICO, year, month));
                    return PersianCalendar.of(year, month, dom);
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
        public long between(PersianCalendar start, PersianCalendar end) {

            switch (this.unit) {
                case YEARS:
                    return PersianCalendar.Unit.MONTHS.between(start, end) / 12;
                case MONTHS:
                    long delta = ymValue(end) - ymValue(start);
                    if ((delta > 0) && (end.pdom < start.pdom)) {
                        delta--;
                    } else if ((delta < 0) && (end.pdom > start.pdom)) {
                        delta++;
                    }
                    return delta;
                case WEEKS:
                    return PersianCalendar.Unit.DAYS.between(start, end) / 7;
                case DAYS:
                    return CALSYS.transform(end) - CALSYS.transform(start);
                default:
                    throw new UnsupportedOperationException(this.unit.name());
            }

        }

        private static int ymValue(PersianCalendar date) {

            return date.pyear * 12 + date.pmonth - 1;

        }

    }

}
