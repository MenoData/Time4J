/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (JulianCalendar.java) is part of project Time4J.
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
import net.time4j.base.MathUtils;
import net.time4j.base.TimeSource;
import net.time4j.calendar.service.GenericDatePatterns;
import net.time4j.calendar.service.StdIntegerDateElement;
import net.time4j.calendar.service.StdWeekdayElement;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.CalendarDays;
import net.time4j.engine.CalendarEra;
import net.time4j.engine.Calendrical;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoMerger;
import net.time4j.engine.ChronoUnit;
import net.time4j.engine.Chronology;
import net.time4j.engine.DisplayStyle;
import net.time4j.engine.ElementRule;
import net.time4j.engine.FormattableElement;
import net.time4j.engine.StartOfDay;
import net.time4j.engine.TimeAxis;
import net.time4j.engine.UnitRule;
import net.time4j.engine.ValidationElement;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarType;
import net.time4j.format.Leniency;
import net.time4j.format.LocalizedPatternSupport;
import net.time4j.format.TextElement;
import net.time4j.history.ChronoHistory;
import net.time4j.history.HistoricDate;
import net.time4j.history.HistoricEra;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


/**
 * <p>Represents the proleptic Julian calendar. </p>
 *
 * <p>It is de facto the ancestor of modern gregorian calendar but does not reflect any historic anomalies
 * and applies its leap year rules even backwards into the far past. The main difference to gregorian
 * calendar is the leap year rule which considers every year as leap year whose number is divisible by four. </p>
 *
 * <p>Following elements which are declared as constants are registered by
 * this class: </p>
 *
 * <ul>
 *  <li>{@link #DAY_OF_WEEK}</li>
 *  <li>{@link #DAY_OF_MONTH}</li>
 *  <li>{@link #DAY_OF_YEAR}</li>
 *  <li>{@link #MONTH_OF_YEAR}</li>
 *  <li>{@link #YEAR_OF_ERA}</li>
 *  <li>{@link #ERA}</li>
 *  <li>{@link #DATE}</li>
 * </ul>
 *
 * <p>Furthermore, all elements defined in {@code EpochDays} and {@link CommonElements} are supported. </p>
 *
 * <p>Example of usage: </p>
 *
 * <pre>
 *     ChronoFormatter&lt;JulianCalendar&gt; formatter =
 *       ChronoFormatter.ofPattern(
 *          &quot;E, d.MMMM yyyy&quot;, PatternType.CLDR, Locale.ENGLISH, JulianCalendar.axis());
 *     PlainDate today = SystemClock.inLocalView().today();
 *     JulianCalendar julianDate = today.transform(JulianCalendar.class);
 *     System.out.println(formatter.format(julianDate));
 * </pre>
 *
 * @author  Meno Hochschild
 * @since   3.15/4.12
 */
/*[deutsch]
 * <p>Repr&auml;sentiert den proleptischen julianischen Kalender. </p>
 *
 * <p>De facto handelt es sich um den Vorg&auml;nger des modernen gregorianischen Kalenders.
 * Jedoch werden historische Anomalien nicht erfasst. Die Schaltjahresregel, die jedes durch
 * vier teilbare Jahr als Schaltjahr ansieht, wird sogar bis in die ferne Vergangenheit angewandt. </p>
 *
 * <p>Registriert sind folgende als Konstanten deklarierte Elemente: </p>
 *
 * <ul>
 *  <li>{@link #DAY_OF_WEEK}</li>
 *  <li>{@link #DAY_OF_MONTH}</li>
 *  <li>{@link #DAY_OF_YEAR}</li>
 *  <li>{@link #MONTH_OF_YEAR}</li>
 *  <li>{@link #YEAR_OF_ERA}</li>
 *  <li>{@link #ERA}</li>
 *  <li>{@link #DATE}</li>
 * </ul>
 *
 * <p>Au&slig;erdem werden alle Elemente von {@code EpochDays} und {@link CommonElements} unterst&uuml;tzt. </p>
 *
 * <p>Anwendungsbeispiel: </p>
 *
 * <pre>
 *     ChronoFormatter&lt;JulianCalendar&gt; formatter =
 *       ChronoFormatter.ofPattern(
 *          &quot;E, d.MMMM yyyy&quot;, PatternType.CLDR, Locale.ENGLISH, JulianCalendar.axis());
 *     PlainDate today = SystemClock.inLocalView().today();
 *     JulianCalendar julianDate = today.transform(JulianCalendar.class);
 *     System.out.println(formatter.format(julianDate));
 * </pre>
 *
 * @author  Meno Hochschild
 * @since   3.15/4.12
 */
@CalendarType("julian")
public final class JulianCalendar
    extends Calendrical<JulianCalendar.Unit, JulianCalendar>
    implements LocalizedPatternSupport {

    //~ Statische Felder/Initialisierungen --------------------------------

    // maximum of year-of-era
    private static final int YMAX = 999999999;

    // Tage zwischen [0000-03-01] und [1972-01-01] (julianische Datumsangaben)
    private static final int OFFSET = 719470 + 2 * 365;

    private static final int YEAR_INDEX = 0;
    private static final int DAY_OF_MONTH_INDEX = 2;
    private static final int DAY_OF_YEAR_INDEX = 3;

    /**
     * <p>Represents the Julian date. </p>
     *
     * <p>This element is identical to {@code ChronoHistory.PROLEPTIC_JULIAN.date()}. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert das julianische Datum. </p>
     *
     * <p>Dieses Element entspricht {@code ChronoHistory.PROLEPTIC_JULIAN.date()}. </p>
     */
    public static final ChronoElement<HistoricDate> DATE = ChronoHistory.PROLEPTIC_JULIAN.date();

    /**
     * <p>Represents the Julian era. </p>
     *
     * <p>This element is identical to {@code ChronoHistory.PROLEPTIC_JULIAN.era()}. Valid values are
     * either {@code HistoricEra.AD} or {@code HistoricEra.BC}. However, the era cannot be changed
     * by the expression {@code julianDate.with(ERA, historicEra)}. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die julianische &Auml;ra. </p>
     *
     * <p>Dieses Element entspricht {@code ChronoHistory.PROLEPTIC_JULIAN.era()}. Die g&uuml;ltigen Werte
     * sind entweder {@code HistoricEra.AD} oder {@code HistoricEra.BC}. Allerdings kann die &Auml;ra
     * nicht durch den Ausdruck {@code julianDate.with(ERA, historicEra)} ge&auml;ndert werden. </p>
     */
    @FormattableElement(format = "G")
    public static final ChronoElement<HistoricEra> ERA = ChronoHistory.PROLEPTIC_JULIAN.era();

    /**
     * <p>Represents the Julian year. </p>
     *
     * <p>This element is identical to {@code ChronoHistory.PROLEPTIC_JULIAN.yearOfEra()}. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert das julianische Jahr. </p>
     *
     * <p>Dieses Element entspricht {@code ChronoHistory.PROLEPTIC_JULIAN.yearOfEra()}. </p>
     */
    @FormattableElement(format = "y")
    public static final ChronoElement<Integer> YEAR_OF_ERA = ChronoHistory.PROLEPTIC_JULIAN.yearOfEra();

    /**
     * <p>Represents the Julian month. </p>
     *
     * <p>This element is identical to {@code ChronoHistory.PROLEPTIC_JULIAN.month()}. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den julianischen Monat. </p>
     *
     * <p>Dieses Element entspricht {@code ChronoHistory.PROLEPTIC_JULIAN.month()}. </p>
     */
    @FormattableElement(format = "M", standalone = "L")
    public static final TextElement<Integer> MONTH_OF_YEAR = ChronoHistory.PROLEPTIC_JULIAN.month();

    /**
     * <p>Represents the Julian day of month. </p>
     *
     * <p>This element is identical to {@code ChronoHistory.PROLEPTIC_JULIAN.dayOfMonth()}. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den julianischen Tag des Monats. </p>
     *
     * <p>Dieses Element entspricht {@code ChronoHistory.PROLEPTIC_JULIAN.dayOfMonth()}. </p>
     */
    @FormattableElement(format = "d")
    public static final ChronoElement<Integer> DAY_OF_MONTH = ChronoHistory.PROLEPTIC_JULIAN.dayOfMonth();

    /**
     * <p>Represents the Julian day of year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den julianischen Tag des Jahres. </p>
     */
    @FormattableElement(format = "D")
    public static final ChronoElement<Integer> DAY_OF_YEAR =
        new StdIntegerDateElement<JulianCalendar>("DAY_OF_YEAR", JulianCalendar.class, 1, 365, 'D');

    /**
     * <p>Represents the Julian day of week. </p>
     *
     * <p>If the day-of-week is set to a new value then Time4J handles the Julian calendar week
     * as starting on Sunday which is close to historic use. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den julianischen Tag der Woche. </p>
     *
     * <p>Wenn der Tag der Woche auf einen neuen Wert gesetzt wird, behandelt Time4J die julianische
     * Kalenderwoche so, da&szlig; sie am Sonntag beginnt, was dem historischen Gebrauch nahe kommt. </p>
     */
    @FormattableElement(format = "E")
    public static final ChronoElement<Weekday> DAY_OF_WEEK =
        new StdWeekdayElement<JulianCalendar>(JulianCalendar.class);

    private static final EraYearMonthDaySystem<JulianCalendar> CALSYS;
    private static final TimeAxis<JulianCalendar.Unit, JulianCalendar> ENGINE;

    static {
        CALSYS = new Transformer();

        TimeAxis.Builder<JulianCalendar.Unit, JulianCalendar> builder =
            TimeAxis.Builder.setUp(
                JulianCalendar.Unit.class,
                JulianCalendar.class,
                new Merger(),
                CALSYS)
            .appendElement(
                DATE,
                new DateRule())
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
                CommonElements.RELATED_GREGORIAN_YEAR,
                new RelatedGregorianYearRule<JulianCalendar>(CALSYS, DAY_OF_YEAR))
            .appendUnit(
                Unit.YEARS,
                new JulianUnitRule(Unit.YEARS),
                Unit.YEARS.getLength(),
                Collections.singleton(Unit.MONTHS))
            .appendUnit(
                Unit.MONTHS,
                new JulianUnitRule(Unit.MONTHS),
                Unit.MONTHS.getLength(),
                Collections.singleton(Unit.YEARS))
            .appendUnit(
                Unit.WEEKS,
                new JulianUnitRule(Unit.WEEKS),
                Unit.WEEKS.getLength(),
                Collections.singleton(Unit.DAYS))
            .appendUnit(
                Unit.DAYS,
                new JulianUnitRule(Unit.DAYS),
                Unit.DAYS.getLength(),
                Collections.singleton(Unit.WEEKS))
            .appendExtension(
                new CommonElements.Weekengine(
                    JulianCalendar.class,
                    DAY_OF_MONTH,
                    DAY_OF_YEAR,
                    getDefaultWeekmodel()));
        ENGINE = builder.build();
    }

    private static final long serialVersionUID = 3038883058279104976L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final int prolepticYear;
    private transient final int month;
    private transient final int dom;

    //~ Konstruktoren -----------------------------------------------------

    private JulianCalendar(
        int prolepticYear,
        int month,
        int dom
    ) {
        super();

        this.prolepticYear = prolepticYear;
        this.month = month;
        this.dom = dom;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new instance of a Julian calendar date. </p>
     *
     * @param   era         either {@code HistoricEra.AD} or {@code HistoricEra.BC}
     * @param   yearOfEra   year of era in range 1 until 999,999,999
     * @param   month       month (1-12)
     * @param   dayOfMonth  day of month (1-31)
     * @return  new instance of {@code JulianCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.15/4.12
     */
    /*[deutsch]
     * <p>Erzeugt ein neues julianisches Kalenderdatum. </p>
     *
     * @param   era         either {@code HistoricEra.AD} or {@code HistoricEra.BC}
     * @param   yearOfEra   year of era in range 1 until 999,999,999
     * @param   month       month (1-12)
     * @param   dayOfMonth  day of month (1-31)
     * @return  new instance of {@code JulianCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.15/4.12
     */
    public static JulianCalendar of(
        HistoricEra era,
        int yearOfEra,
        int month,
        int dayOfMonth
    ) {

        if (era == null) {
            throw new NullPointerException("Missing Julian era.");
        } else if (!CALSYS.isValid(era, yearOfEra, month, dayOfMonth)) {
            throw new IllegalArgumentException("Out of bounds: " + toString(era, yearOfEra, month, dayOfMonth));
        }

        if (era == HistoricEra.AD) {
            return new JulianCalendar(yearOfEra, month, dayOfMonth);
        } else { // BC
            return new JulianCalendar(MathUtils.safeSubtract(1, yearOfEra), month, dayOfMonth);
        }

    }

    /**
     * <p>Creates a new instance of a Julian calendar date. </p>
     *
     * @param   era         either {@code HistoricEra.AD} or {@code HistoricEra.BC}
     * @param   yearOfEra   year of era in range 1 until 999,999,999
     * @param   month       month (1-12)
     * @param   dayOfMonth  day of month (1-31)
     * @return  new instance of {@code JulianCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.15/4.12
     */
    /*[deutsch]
     * <p>Erzeugt ein neues julianisches Kalenderdatum. </p>
     *
     * @param   era         either {@code HistoricEra.AD} or {@code HistoricEra.BC}
     * @param   yearOfEra   year of era in range 1 until 999,999,999
     * @param   month       month (1-12)
     * @param   dayOfMonth  day of month (1-31)
     * @return  new instance of {@code JulianCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.15/4.12
     */
    public static JulianCalendar of(
        HistoricEra era,
        int yearOfEra,
        Month month,
        int dayOfMonth
    ) {

        return JulianCalendar.of(era, yearOfEra, month.getValue(), dayOfMonth);

    }

    /**
     * <p>Obtains the current calendar date in system time. </p>
     *
     * <p>Convenient short-cut for: {@code SystemClock.inLocalView().now(JulianCalendar.axis())}. </p>
     *
     * @return  current calendar date in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(net.time4j.engine.Chronology)
     * @since   3.23/4.19
     */
    /*[deutsch]
     * <p>Ermittelt das aktuelle Kalenderdatum in der Systemzeit. </p>
     *
     * <p>Bequeme Abk&uuml;rzung f&uuml;r: {@code SystemClock.inLocalView().now(JulianCalendar.axis())}. </p>
     *
     * @return  current calendar date in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(net.time4j.engine.Chronology)
     * @since   3.23/4.19
     */
    public static JulianCalendar nowInSystemTime() {

        return SystemClock.inLocalView().now(JulianCalendar.axis());

    }

    /**
     * <p>Yields the Julian era. </p>
     *
     * @return  either {@code HistoricEra.AD} or {@code HistoricEra.BC}
     * @since   3.15/4.12
     */
    /*[deutsch]
     * <p>Liefert die julianische &Auml;ra. </p>
     *
     * @return  either {@code HistoricEra.AD} or {@code HistoricEra.BC}
     * @since   3.15/4.12
     */
    public HistoricEra getEra() {

        return ((this.prolepticYear >= 1) ? HistoricEra.AD : HistoricEra.BC);

    }

    /**
     * <p>Yields the Julian year within current era. </p>
     *
     * @return  int ({@code 1 <= year <= 999,999,999})
     * @since   3.15/4.12
     */
    /*[deutsch]
     * <p>Liefert das julianische Jahr innerhalb der aktuellen &Auml;ra. </p>
     *
     * @return  int ({@code 1 <= year <= 999,999,999})
     * @since   3.15/4.12
     */
    public int getYear() {

        return ((this.prolepticYear >= 1) ? this.prolepticYear : MathUtils.safeSubtract(1, this.prolepticYear));

    }

    /**
     * <p>Yields the Julian month. </p>
     *
     * @return  enum
     * @since   3.15/4.12
     */
    /*[deutsch]
     * <p>Liefert den julianischen Monat. </p>
     *
     * @return  enum
     * @since   3.15/4.12
     */
    public Month getMonth() {

        return Month.valueOf(this.month);

    }

    /**
     * <p>Yields the Julian day of month. </p>
     *
     * @return  int
     * @since   3.15/4.12
     */
    /*[deutsch]
     * <p>Liefert den julianischen Tag des Monats. </p>
     *
     * @return  int
     * @since   3.15/4.12
     */
    public int getDayOfMonth() {

        return this.dom;

    }

    /**
     * <p>Determines the day of week. </p>
     *
     * @return  Weekday
     * @since   3.15/4.12
     */
    /*[deutsch]
     * <p>Ermittelt den Wochentag. </p>
     *
     * @return  Weekday
     * @since   3.15/4.12
     */
    public Weekday getDayOfWeek() {

        long utcDays = CALSYS.transform(this);
        return Weekday.valueOf(MathUtils.floorModulo(utcDays + 5, 7) + 1);

    }

    /**
     * <p>Yields the Julian day of year. </p>
     *
     * @return  int
     * @since   3.15/4.12
     */
    /*[deutsch]
     * <p>Liefert den julianschen Tag des Jahres. </p>
     *
     * @return  int
     * @since   3.15/4.12
     */
    public int getDayOfYear() {

        return this.get(DAY_OF_YEAR).intValue();

    }

    /**
     * <p>Yields the length of current Julian month in days. </p>
     *
     * @return  int
     * @since   3.15/4.12
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge des aktuellen julianischen Monats in Tagen. </p>
     *
     * @return  int
     * @since   3.15/4.12
     */
    public int lengthOfMonth() {

        return lengthOfMonth(this.prolepticYear, this.month);

    }

    /**
     * <p>Yields the length of current Julian year in days. </p>
     *
     * @return  int
     * @since   3.15/4.12
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge des aktuellen julianischen Jahres in Tagen. </p>
     *
     * @return  int
     * @since   3.15/4.12
     */
    public int lengthOfYear() {

        return this.isLeapYear() ? 366 : 365;

    }

    /**
     * <p>Is the year of this date a leap year? </p>
     *
     * @return  boolean
     * @since   3.15/4.12
     */
    /*[deutsch]
     * <p>Liegt dieses Datum in einem Schaltjahr? </p>
     *
     * @return  boolean
     * @since   3.15/4.12
     */
    public boolean isLeapYear() {

        return ((this.prolepticYear % 4) == 0);

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
     * @since   3.15/4.12
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
     * @since   3.15/4.12
     */
    public GeneralTimestamp<JulianCalendar> at(PlainTime time) {

        return GeneralTimestamp.of(this, time);

    }

    /**
     * <p>Is equivalent to {@code at(PlainTime.of(hour, minute))}. </p>
     *
     * @param   hour        hour of day in range (0-24)
     * @param   minute      minute of hour in range (0-59)
     * @return  general timestamp as composition of this date and given time
     * @throws  IllegalArgumentException if any argument is out of range
     * @since   3.15/4.12
     */
    /*[deutsch]
     * <p>Entspricht {@code at(PlainTime.of(hour, minute))}. </p>
     *
     * @param   hour        hour of day in range (0-24)
     * @param   minute      minute of hour in range (0-59)
     * @return  general timestamp as composition of this date and given time
     * @throws  IllegalArgumentException if any argument is out of range
     * @since   3.15/4.12
     */
    public GeneralTimestamp<JulianCalendar> atTime(
        int hour,
        int minute
    ) {

        return this.at(PlainTime.of(hour, minute));

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof JulianCalendar) {
            JulianCalendar that = (JulianCalendar) obj;
            return (
                (this.dom == that.dom)
                && (this.month == that.month)
                && (this.prolepticYear == that.prolepticYear)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return (17 * this.dom + 31 * this.month + 37 * this.prolepticYear);

    }

    @Override
    public String toString() {

        return toString(this.getEra(), this.getYear(), this.month, this.dom);

    }

    /**
     * <p>Obtains the standard week model of this calendar. </p>
     *
     * <p>The Julian calendar usually starts on Sunday. </p>
     *
     * @return  Weekmodel
     * @since   3.24/4.20
     */
    /*[deutsch]
     * <p>Ermittelt das Standardwochenmodell dieses Kalenders. </p>
     *
     * <p>Der julianische Kalender startet normalerweise am Sonntag. </p>
     *
     * @return  Weekmodel
     * @since   3.24/4.20
     */
    public static Weekmodel getDefaultWeekmodel() {

        return Weekmodel.of(Weekday.SUNDAY, 1);

    }

    /**
     * <p>Returns the associated time axis. </p>
     *
     * @return  chronology
     * @since   3.15/4.12
     */
    /*[deutsch]
     * <p>Liefert die zugeh&ouml;rige Zeitachse. </p>
     *
     * @return  chronology
     * @since   3.15/4.12
     */
    public static TimeAxis<Unit, JulianCalendar> axis() {

        return ENGINE;

    }

    @Override
    protected TimeAxis<Unit, JulianCalendar> getChronology() {

        return ENGINE;

    }

    @Override
    protected JulianCalendar getContext() {

        return this;

    }

    // used in serialization
    int getProlepticYear() {

        return this.prolepticYear;

    }

    private static String toString(
        CalendarEra era,
        int yearOfEra,
        int month,
        int dom
    ) {

        StringBuilder sb = new StringBuilder(32);
        sb.append("JULIAN-");
        sb.append(era.name());
        sb.append('-');
        String y = String.valueOf(yearOfEra);
        for (int i = y.length(); i < 4; i++) {
            sb.append('0');
        }
        sb.append(y);
        sb.append('-');
        if (month < 10) {
            sb.append('0');
        }
        sb.append(month);
        sb.append('-');
        if (dom < 10) {
            sb.append('0');
        }
        sb.append(dom);
        return sb.toString();

    }

    private static int lengthOfMonth(
        int pYear,
        int monthOfYear
    ) {

        switch (monthOfYear) {
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                return ((pYear % 4) == 0) ? 29 : 28;
            default:
                return 31;
        }

    }

    /**
     * @serialData  Uses <a href="../../../serialized-form.html#net.time4j.calendar.SPX">
     *              a dedicated serialization form</a> as proxy. The first byte contains
     *              the type-ID {@code 7}. Then the year is written as int, finally
     *              month and day-of-month as bytes.
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.JULIAN);

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
     * <p>Defines come calendar units for the Julian calendar. </p>
     *
     * @since   3.15/4.12
     */
    /*[deutsch]
     * <p>Definiert einige kalendarische Zeiteinheiten f&uuml;r den julianischen Kalender. </p>
     *
     * @since   3.15/4.12
     */
    public static enum Unit
        implements ChronoUnit {

        //~ Statische Felder/Initialisierungen ----------------------------

        YEARS(365.25 * 86400.0),

        MONTHS(365.25 * 86400.0 / 12),

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
         * <p>Calculates the difference between given Julian dates in this unit. </p>
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
            JulianCalendar start,
            JulianCalendar end
        ) {

            return (int) start.until(end, this); // safe

        }

    }

    private static class Transformer
        implements EraYearMonthDaySystem<JulianCalendar> {

        //~ Methoden ------------------------------------------------------

        @Override
        public boolean isValid(
            CalendarEra era,
            int yearOfEra,
            int monthOfYear,
            int dayOfMonth
        ) {

            int pYear;

            if (era == HistoricEra.AD) {
                pYear = yearOfEra;
            } else if (era == HistoricEra.BC) {
                pYear = MathUtils.safeSubtract(1, yearOfEra);
            } else {
                return false;
            }

            if (yearOfEra < 1 || yearOfEra > YMAX || monthOfYear < 1 || monthOfYear > 12 || dayOfMonth < 1) {
                return false;
            }

            return (dayOfMonth <= lengthOfMonth(pYear, monthOfYear));

        }

        @Override
        public int getLengthOfMonth(
            CalendarEra era,
            int yearOfEra,
            int monthOfYear
        ) {

            int pYear;

            if (era == HistoricEra.AD) {
                pYear = yearOfEra;
            } else if (era == HistoricEra.BC) {
                pYear = MathUtils.safeSubtract(1, yearOfEra);
            } else {
                throw new IllegalArgumentException("Invalid era: " + era);
            }

            if (yearOfEra >= 1 && yearOfEra <= YMAX && monthOfYear >= 1 && monthOfYear <= 12) {
                return lengthOfMonth(pYear, monthOfYear);
            }

            throw new IllegalArgumentException("Out of bounds: year=" + yearOfEra + ", month=" + monthOfYear);

        }

        @Override
        public int getLengthOfYear(
            CalendarEra era,
            int yearOfEra
        ) {

            int pYear;

            if (era == HistoricEra.AD) {
                pYear = yearOfEra;
            } else if (era == HistoricEra.BC) {
                pYear = MathUtils.safeSubtract(1, yearOfEra);
            } else {
                throw new IllegalArgumentException("Invalid era: " + era);
            }

            if ((yearOfEra >= 1) && (yearOfEra <= YMAX)) {
                return ((pYear % 4) == 0) ? 366 : 365;
            }

            throw new IllegalArgumentException("Out of bounds: year=" + yearOfEra);

        }

        @Override
        public JulianCalendar transform(long utcDays) {

            long y;
            int m;
            int d;

            long days = MathUtils.safeAdd(utcDays, OFFSET);

            long q4 = MathUtils.floorDivide(days, 1461);
            int r4 =  MathUtils.floorModulo(days, 1461);

            if (r4 == 1460) {
                y = (q4 + 1) * 4;
                m = 2;
                d = 29;
            } else {
                int q1 = (r4 / 365);
                int r1 = (r4 % 365);

                y = q4 * 4 + q1;
                m = (((r1 + 31) * 5) / 153) + 2;
                d = r1 - (((m + 1) * 153) / 5) + 123;

                if (m > 12) {
                    y++;
                    m -= 12;
                }
            }

            HistoricEra era = ((y >= 1) ? HistoricEra.AD : HistoricEra.BC);
            int yearOfEra = MathUtils.safeCast((y >= 1) ? y : MathUtils.safeSubtract(1, y));
            return JulianCalendar.of(era, yearOfEra, m, d);

        }

        @Override
        public long transform(JulianCalendar date) {

            long y = date.prolepticYear;
            int m = date.month;

            if (m < 3) {
                y--;
                m += 12;
            }

            long days = (
                (y * 365)
                    + MathUtils.floorDivide(y, 4)
                    + (((m + 1) * 153) / 5) - 123
                    + date.dom);

            return days - OFFSET;

        }

        @Override
        public long getMinimumSinceUTC() {

            JulianCalendar min = new JulianCalendar(1 - YMAX, 1, 1);
            return this.transform(min);

        }

        @Override
        public long getMaximumSinceUTC() {

            JulianCalendar max = new JulianCalendar(YMAX, 12, 31);
            return this.transform(max);

        }

        @Override
        public List<CalendarEra> getEras() {

            CalendarEra bc = HistoricEra.BC;
            CalendarEra ad = HistoricEra.AD;
            return Collections.unmodifiableList(Arrays.asList(bc, ad));

        }

    }

    private static class IntegerRule
        implements ElementRule<JulianCalendar, Integer> {

        //~ Instanzvariablen ----------------------------------------------

        private final int index;

        //~ Konstruktoren -------------------------------------------------

        IntegerRule(int index) {
            super();

            this.index = index;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Integer getValue(JulianCalendar context) {

            switch (this.index) {
                case YEAR_INDEX:
                    return context.getYear();
                case DAY_OF_MONTH_INDEX:
                    return context.dom;
                case DAY_OF_YEAR_INDEX:
                    int doy = 0;
                    for (int m = 1; m < context.month; m++) {
                        doy += lengthOfMonth(context.prolepticYear, m);
                    }
                    return doy + context.dom;
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public Integer getMinimum(JulianCalendar context) {

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
        public Integer getMaximum(JulianCalendar context) {

            switch (this.index) {
                case YEAR_INDEX:
                    return YMAX;
                case DAY_OF_MONTH_INDEX:
                    return lengthOfMonth(context.prolepticYear, context.month);
                case DAY_OF_YEAR_INDEX:
                    return (((context.prolepticYear % 4) == 0) ? 366 : 365);
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public boolean isValid(
            JulianCalendar context,
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
        public JulianCalendar withValue(
            JulianCalendar context,
            Integer value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing element value.");
            }

            switch (this.index) {
                case YEAR_INDEX:
                    int y = value.intValue();
                    int pYear = (context.getEra() == HistoricEra.AD) ? y : MathUtils.safeSubtract(1, y);
                    int dmax = lengthOfMonth(pYear, context.month);
                    int d = Math.min(context.dom, dmax);
                    return JulianCalendar.of(context.getEra(), y, context.month, d);
                case DAY_OF_MONTH_INDEX:
                    return JulianCalendar.of(context.getEra(), context.getYear(), context.month, value.intValue());
                case DAY_OF_YEAR_INDEX:
                    int doy = value.intValue();
                    if ((doy >= 1) && (doy <= context.lengthOfYear())) {
                        int delta = value.intValue() - this.getValue(context).intValue();
                        return context.plus(CalendarDays.of(delta));
                    } else {
                        throw new IllegalArgumentException("Invalid day of year: " + value);
                    }
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public ChronoElement<?> getChildAtFloor(JulianCalendar context) {

            if (this.index == YEAR_INDEX) {
                return MONTH_OF_YEAR;
            }

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(JulianCalendar context) {

            if (this.index == YEAR_INDEX) {
                return MONTH_OF_YEAR;
            }

            return null;

        }

    }

    private static class MonthRule
        implements ElementRule<JulianCalendar, Integer> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Integer getValue(JulianCalendar context) {

            return Integer.valueOf(context.month);

        }

        @Override
        public Integer getMinimum(JulianCalendar context) {

            return Integer.valueOf(1);

        }

        @Override
        public Integer getMaximum(JulianCalendar context) {

            return Integer.valueOf(12);

        }

        @Override
        public boolean isValid(
            JulianCalendar context,
            Integer value
        ) {

            if (value == null) {
                return false;
            }

            int m = value.intValue();
            return ((m >= 1) && (m <= 12));

        }

        @Override
        public JulianCalendar withValue(
            JulianCalendar context,
            Integer value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing month.");
            }

            int m = value.intValue();
            int dmax = lengthOfMonth(context.prolepticYear, m);
            int d = Math.min(context.dom, dmax);
            return new JulianCalendar(context.prolepticYear, m, d);

        }

        @Override
        public ChronoElement<?> getChildAtFloor(JulianCalendar context) {

            return DAY_OF_MONTH;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(JulianCalendar context) {

            return DAY_OF_MONTH;

        }

    }

    private static class EraRule
        implements ElementRule<JulianCalendar, HistoricEra> {

        //~ Methoden ------------------------------------------------------

        @Override
        public HistoricEra getValue(JulianCalendar context) {

            return context.getEra();

        }

        @Override
        public HistoricEra getMinimum(JulianCalendar context) {

            return HistoricEra.BC;

        }

        @Override
        public HistoricEra getMaximum(JulianCalendar context) {

            return HistoricEra.AD;

        }

        @Override
        public boolean isValid(
            JulianCalendar context,
            HistoricEra value
        ) {

            return context.getEra().equals(value);

        }

        @Override
        public JulianCalendar withValue(
            JulianCalendar context,
            HistoricEra value,
            boolean lenient
        ) {

            if (!this.isValid(context, value)) {
                throw new IllegalArgumentException("Julian era cannot be changed.");
            }

            return context;

        }

        @Override
        public ChronoElement<?> getChildAtFloor(JulianCalendar context) {

            return YEAR_OF_ERA;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(JulianCalendar context) {

            return YEAR_OF_ERA;

        }

    }

    private static class DateRule
        implements ElementRule<JulianCalendar, HistoricDate> {

        //~ Methoden ------------------------------------------------------

        @Override
        public HistoricDate getValue(JulianCalendar context) {

            return HistoricDate.of(context.getEra(), context.getYear(), context.month, context.dom);

        }

        @Override
        public HistoricDate getMinimum(JulianCalendar context) {

            return HistoricDate.of(HistoricEra.BC, YMAX, 1, 1);

        }

        @Override
        public HistoricDate getMaximum(JulianCalendar context) {

            return HistoricDate.of(HistoricEra.AD, YMAX, 12, 31);

        }

        @Override
        public boolean isValid(
            JulianCalendar context,
            HistoricDate value
        ) {

            if (value == null) {
                return false;
            }

            return CALSYS.isValid(value.getEra(), value.getYearOfEra(), value.getMonth(), value.getDayOfMonth());

        }

        @Override
        public JulianCalendar withValue(
            JulianCalendar context,
            HistoricDate value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing historic date value.");
            }

            return JulianCalendar.of(value.getEra(), value.getYearOfEra(), value.getMonth(), value.getDayOfMonth());

        }

        @Override
        public ChronoElement<?> getChildAtFloor(JulianCalendar context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(JulianCalendar context) {

            return null;

        }

    }

    private static class WeekdayRule
        implements ElementRule<JulianCalendar, Weekday> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Weekday getValue(JulianCalendar context) {

            return context.getDayOfWeek();

        }

        @Override
        public Weekday getMinimum(JulianCalendar context) {

            return Weekday.SUNDAY;

        }

        @Override
        public Weekday getMaximum(JulianCalendar context) {

            return Weekday.SATURDAY;

        }

        @Override
        public boolean isValid(
            JulianCalendar context,
            Weekday value
        ) {

            return (value != null);

        }

        @Override
        public JulianCalendar withValue(
            JulianCalendar context,
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
        public ChronoElement<?> getChildAtFloor(JulianCalendar context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(JulianCalendar context) {

            return null;

        }

    }

    private static class Merger
        implements ChronoMerger<JulianCalendar> {

        //~ Methoden ------------------------------------------------------

        @Override
        public String getFormatPattern(
            DisplayStyle style,
            Locale locale
        ) {

            return GenericDatePatterns.get("generic", style, locale); // uses era

        }

        @Override
        public JulianCalendar createFrom(
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
        public JulianCalendar createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean preparsing
        ) {

            boolean lenient = attributes.get(Attributes.LENIENCY, Leniency.SMART).isLax();
            return this.createFrom(entity, attributes, lenient, preparsing);

        }

        @Override
        public JulianCalendar createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean lenient,
            boolean preparsing
        ) {

            if (!entity.contains(ERA)) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Missing Julian era.");
                return null;
            }

            HistoricEra era = entity.get(ERA);
            int yearOfEra = entity.getInt(YEAR_OF_ERA);

            if (yearOfEra == Integer.MIN_VALUE) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Missing Julian year.");
                return null;
            }

            int month = entity.getInt(MONTH_OF_YEAR);
            if (month != Integer.MIN_VALUE) {
                int dom = entity.getInt(DAY_OF_MONTH);

                if (dom != Integer.MIN_VALUE) {
                    if (CALSYS.isValid(era, yearOfEra, month, dom)) {
                        return JulianCalendar.of(era, yearOfEra, month, dom);
                    } else {
                        entity.with(ValidationElement.ERROR_MESSAGE, "Invalid Julian date.");
                    }
                }
            }

            int doy = entity.getInt(DAY_OF_YEAR);
            if (doy != Integer.MIN_VALUE) {
                if (doy > 0) {
                    int m = 1;
                    int pYear = ((era == HistoricEra.AD) ? yearOfEra : MathUtils.safeSubtract(1, yearOfEra));
                    int daycount = 0;
                    while (m <= 12) {
                        int len = lengthOfMonth(pYear, m);
                        if (doy > daycount + len) {
                            m++;
                            daycount += len;
                        } else {
                            return JulianCalendar.of(era, yearOfEra, m, doy - daycount);
                        }
                    }
                }
                entity.with(ValidationElement.ERROR_MESSAGE, "Invalid Julian date.");
            }

            return null;

        }

        @Override
        public ChronoDisplay preformat(JulianCalendar context, AttributeQuery attributes) {

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

            return PlainDate.axis().getDefaultPivotYear();

        }

    }

    private static class JulianUnitRule
        implements UnitRule<JulianCalendar> {

        //~ Instanzvariablen ----------------------------------------------

        private final Unit unit;

        //~ Konstruktoren -------------------------------------------------

        JulianUnitRule(Unit unit) {
            super();

            this.unit = unit;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public JulianCalendar addTo(JulianCalendar date, long amount) {

            switch (this.unit) {
                case YEARS:
                    amount = MathUtils.safeMultiply(amount, 12);
                    // fall-through
                case MONTHS:
                    long ym = MathUtils.safeAdd(ymValue(date), amount);
                    int pYear = MathUtils.safeCast(MathUtils.floorDivide(ym, 12));
                    int month = MathUtils.floorModulo(ym, 12) + 1;
                    int dom =
                        Math.min(
                            date.dom,
                            lengthOfMonth(pYear, month));
                    HistoricEra era = ((pYear >= 1) ? HistoricEra.AD : HistoricEra.BC);
                    int yearOfEra = ((pYear >= 1) ? pYear : MathUtils.safeSubtract(1, pYear));
                    return JulianCalendar.of(era, yearOfEra, month, dom);
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
        public long between(JulianCalendar start, JulianCalendar end) {

            switch (this.unit) {
                case YEARS:
                    return JulianCalendar.Unit.MONTHS.between(start, end) / 12;
                case MONTHS:
                    long delta = ymValue(end) - ymValue(start);
                    if ((delta > 0) && (end.dom < start.dom)) {
                        delta--;
                    } else if ((delta < 0) && (end.dom > start.dom)) {
                        delta++;
                    }
                    return delta;
                case WEEKS:
                    return JulianCalendar.Unit.DAYS.between(start, end) / 7;
                case DAYS:
                    return CALSYS.transform(end) - CALSYS.transform(start);
                default:
                    throw new UnsupportedOperationException(this.unit.name());
            }

        }

        private static int ymValue(JulianCalendar date) {

            return date.prolepticYear * 12 + date.month - 1;

        }

    }

}
