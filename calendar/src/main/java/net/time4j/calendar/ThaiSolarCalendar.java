/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ThaiSolarCalendar.java) is part of project Time4J.
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

import net.time4j.CalendarUnit;
import net.time4j.GeneralTimestamp;
import net.time4j.Moment;
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.SystemClock;
import net.time4j.Weekday;
import net.time4j.Weekmodel;
import net.time4j.base.GregorianMath;
import net.time4j.base.TimeSource;
import net.time4j.calendar.service.GenericDatePatterns;
import net.time4j.calendar.service.StdEnumDateElement;
import net.time4j.calendar.service.StdIntegerDateElement;
import net.time4j.calendar.service.StdWeekdayElement;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.CalendarEra;
import net.time4j.engine.Calendrical;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.ChronoMerger;
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

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * <p>The Thai solar calendar calendar used in Thailand uses as only difference to western gregorian
 * calendar a different year numbering with the Buddhist era mainly. </p>
 *
 * <p>This class supports the calendar reform of 1940/41 after that the begin of year moved from 1st of April
 * to 1st of January. See also: <a href="https://en.wikipedia.org/wiki/Thai_solar_calendar">Wikipedia</a>. </p>
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
 * </ul>
 *
 * <p>Furthermore, all elements defined in {@code EpochDays} and {@link CommonElements} are supported. </p>
 *
 * <p>The date arithmetic uses the ISO-compatible class {@code CalendarUnit} and always delegate to
 * the ISO-equivalent {@code PlainDate} due to the fact that this calendar has always been a derivate
 * of the western gregorian calendar (years are intended as approximate solar years). However, if
 * applied on dates around the year 1940 where the begin of year was moved from 1st of April to 1st of
 * January users might observe some changes of buddhist year numbering which appear strange on first
 * glance. Example: {@code ThaiSolarCalendar.ofBuddhist(2482, FEBRUARY, 1).plus(2, CalendarUnit.YEARS)}
 * results in {@code ThaiSolarCalendar.ofBuddhist(2485, FEBRUARY, 1)}. The addition of two (solar) years
 * corresponds to the addition of apparently three buddhist years in this edge case. If translated to
 * its ISO-equivalent the reason is clear: [1940-02-01] + 2 years = [1942-02-01]. </p>
 *
 * @author  Meno Hochschild
 * @since   3.19/4.15
 */
/*[deutsch]
 * Der Thai-Kalender wird in Thailand verwendet und hat als einzige Differenz
 * zum gregorianischen Kalender eine andere Jahresz&auml;hlung, indem als Ausgangspunkt gew&ouml;hnlich
 * die buddhistische &Auml;ra benutzt wird. </p>
 *
 * <p>Diese Klasse unterst&uuml;tzt die Kalenderreform von 1940/41, als der Beginn des Jahres vom ersten
 * April auf den ersten Januar vorverlegt wurde. Siehe auch:
 * <a href="https://en.wikipedia.org/wiki/Thai_solar_calendar">Wikipedia</a>. </p>
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
 * </ul>
 *
 * <p>Au&slig;erdem werden alle Elemente von {@code EpochDays} und {@link CommonElements} unterst&uuml;tzt. </p>
 *
 * <p>Die Datumsarithmetik benutzt die ISO-kompatible Klasse {@code CalendarUnit} und delegiert immer an
 * das ISO-Gegenst&uuml;ck {@code PlainDate}, weil dieser Kalender nur eine Abwandlung des westlichen
 * gregorianischen Kalenders darstellt und Jahre als gen&auml;herte Sonnenjahre versteht. Wenn allerdings
 * die Datumsarithmetik auf Thai-Datumsangaben um das Jahr 1940 herum angewandt wird (als der Beginn des
 * Jahres vom ersten April auf den ersten Januar vorgezogen wurde), sind auf den ersten Blick unerwartete
 * &Auml;nderungen der buddhistischen Jahresz&auml;hlung m&ouml;glich. Beispiel:
 * {@code ThaiSolarCalendar.ofBuddhist(2482, FEBRUARY, 1).plus(2, CalendarUnit.YEARS)}
 * ergibt {@code ThaiSolarCalendar.ofBuddhist(2485, FEBRUARY, 1). Die Addition von zwei
 * (Sonnen-)Jahren entspricht in diesem Einzelfall der Addition von scheinbar drei
 * buddhistischen Jahren. Wenn die Datumsangaben zu ISO &uuml;bersetzt werden, ist der
 * Grund sofort klar: [1940-02-01] + 2 years = [1942-02-01] </p>
 *
 * @author  Meno Hochschild
 * @since   3.19/4.15
 */
@CalendarType("buddhist")
public final class ThaiSolarCalendar
    extends Calendrical<CalendarUnit, ThaiSolarCalendar>
    implements LocalizedPatternSupport {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final PlainDate MIN_ISO = PlainDate.of(-542, 4, 1);

    /**
     * <p>Represents the Thai era. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die Thai-&Auml;ra. </p>
     */
    @FormattableElement(format = "G")
    public static final ChronoElement<ThaiSolarEra> ERA =
        new StdEnumDateElement<ThaiSolarEra, ThaiSolarCalendar>(
            "ERA", ThaiSolarCalendar.class, ThaiSolarEra.class, 'G');

    /**
     * <p>Represents the Thai year, usually in buddhist counting. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert das Thai-Jahr, meistens in buddhistischer Z&auml;hlweise. </p>
     */
    @FormattableElement(format = "y")
    public static final StdCalendarElement<Integer, ThaiSolarCalendar> YEAR_OF_ERA =
        new StdIntegerDateElement<ThaiSolarCalendar>(
            "YEAR_OF_ERA",
            ThaiSolarCalendar.class,
            1,
            GregorianMath.MAX_YEAR + 543,
            'y',
            null,
            null);

    /**
     * <p>Represents the month. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den Monat. </p>
     */
    @FormattableElement(format = "M", standalone = "L")
    public static final StdCalendarElement<Month, ThaiSolarCalendar> MONTH_OF_YEAR =
        new StdEnumDateElement<Month, ThaiSolarCalendar>(
            "MONTH_OF_YEAR",
            ThaiSolarCalendar.class,
            Month.class,
            'M');

    /**
     * <p>Represents the day of month. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den Tag des Monats. </p>
     */
    @FormattableElement(format = "d")
    public static final StdCalendarElement<Integer, ThaiSolarCalendar> DAY_OF_MONTH =
        new StdIntegerDateElement<ThaiSolarCalendar>("DAY_OF_MONTH", ThaiSolarCalendar.class, 1, 31, 'd');

    /**
     * <p>Represents the day of year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den Tag des Jahres. </p>
     */
    @FormattableElement(format = "D")
    public static final StdCalendarElement<Integer, ThaiSolarCalendar> DAY_OF_YEAR =
        new StdIntegerDateElement<ThaiSolarCalendar>("DAY_OF_YEAR", ThaiSolarCalendar.class, 1, 365, 'D');

    /**
     * <p>Represents the day of week. </p>
     *
     * <p>If the day-of-week is set to a new value then Time4J handles the calendar week
     * as starting on Sunday. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den Tag der Woche. </p>
     *
     * <p>Wenn der Tag der Woche auf einen neuen Wert gesetzt wird, behandelt Time4J die
     * Kalenderwoche so, da&szlig; sie am Sonntag beginnt. </p>
     */
    @FormattableElement(format = "E")
    public static final StdCalendarElement<Weekday, ThaiSolarCalendar> DAY_OF_WEEK =
        new StdWeekdayElement<ThaiSolarCalendar>(ThaiSolarCalendar.class);

    private static final Map<Object, ChronoElement<?>> CHILDREN;
    private static final EraYearMonthDaySystem<ThaiSolarCalendar> CALSYS;
    private static final TimeAxis<CalendarUnit, ThaiSolarCalendar> ENGINE;

    static {
        Map<Object, ChronoElement<?>> children = new HashMap<Object, ChronoElement<?>>();
        children.put(ERA, YEAR_OF_ERA);
        children.put(YEAR_OF_ERA, MONTH_OF_YEAR);
        children.put(MONTH_OF_YEAR, DAY_OF_MONTH);
        CHILDREN = Collections.unmodifiableMap(children);

        CALSYS = new Transformer();

        TimeAxis.Builder<CalendarUnit, ThaiSolarCalendar> builder =
            TimeAxis.Builder.setUp(
                CalendarUnit.class,
                ThaiSolarCalendar.class,
                new Merger(),
                CALSYS)
            .appendElement(
                ERA,
                FieldRule.of(ERA))
            .appendElement(
                YEAR_OF_ERA,
                FieldRule.of(YEAR_OF_ERA),
                CalendarUnit.YEARS)
            .appendElement(
                MONTH_OF_YEAR,
                FieldRule.of(MONTH_OF_YEAR),
                CalendarUnit.MONTHS)
            .appendElement(
                CommonElements.RELATED_GREGORIAN_YEAR,
                new RelatedGregorianYearRule<ThaiSolarCalendar>(CALSYS, DAY_OF_YEAR))
                .appendElement(
                DAY_OF_MONTH,
                FieldRule.of(DAY_OF_MONTH),
                CalendarUnit.DAYS)
            .appendElement(
                DAY_OF_YEAR,
                FieldRule.of(DAY_OF_YEAR),
                CalendarUnit.DAYS)
            .appendElement(
                DAY_OF_WEEK,
                FieldRule.of(DAY_OF_WEEK),
                CalendarUnit.DAYS);
            registerUnits(builder);
        ENGINE = builder.build();
    }

    private static final long serialVersionUID = -6628190121085147706L;

    //~ Instanzvariablen --------------------------------------------------

    private final PlainDate iso;

    //~ Konstruktoren -----------------------------------------------------

    private ThaiSolarCalendar(PlainDate iso) {
        super();

        if (iso.isBefore(MIN_ISO)) {
            throw new IllegalArgumentException("Before buddhist era: " + iso);
        }

        this.iso = iso;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new instance of a Thai solar calendar date. </p>
     *
     * @param   yearOfEra   buddhist year of era {@code >= 1}
     * @param   month       gregorian month
     * @param   dayOfMonth  day of month
     * @return  new instance of {@code ThaiSolarCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.19/4.15
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Thai-Solar-Kalenderdatum. </p>
     *
     * @param   yearOfEra   buddhist year of era {@code >= 1}
     * @param   month       gregorian month
     * @param   dayOfMonth  day of month
     * @return  new instance of {@code ThaiSolarCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.19/4.15
     */
    public static ThaiSolarCalendar ofBuddhist(
        int yearOfEra,
        Month month,
        int dayOfMonth
    ) {

        return ThaiSolarCalendar.of(ThaiSolarEra.BUDDHIST, yearOfEra, month.getValue(), dayOfMonth);

    }

    /**
     * <p>Creates a new instance of a Thai solar calendar date. </p>
     *
     * @param   yearOfEra   buddhist year of era {@code >= 1}
     * @param   month       gregorian month (1-12)
     * @param   dayOfMonth  day of month
     * @return  new instance of {@code ThaiSolarCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.19/4.15
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Thai-Solar-Kalenderdatum. </p>
     *
     * @param   yearOfEra   buddhist year of era {@code >= 1}
     * @param   month       gregorian month (1-12)
     * @param   dayOfMonth  day of month
     * @return  new instance of {@code ThaiSolarCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.19/4.15
     */
    public static ThaiSolarCalendar ofBuddhist(
        int yearOfEra,
        int month,
        int dayOfMonth
    ) {

        return ThaiSolarCalendar.of(ThaiSolarEra.BUDDHIST, yearOfEra, month, dayOfMonth);

    }

    /**
     * <p>Creates a new instance of a Thai solar calendar date. </p>
     *
     * @param   era         Thai era
     * @param   yearOfEra   Thai year of era {@code >= 1}
     * @param   month       gregorian month (1-12)
     * @param   dayOfMonth  day of month
     * @return  new instance of {@code ThaiSolarCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.19/4.15
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Thai-Solar-Kalenderdatum. </p>
     *
     * @param   era         Thai era
     * @param   yearOfEra   Thai year of era {@code >= 1}
     * @param   month       gregorian month (1-12)
     * @param   dayOfMonth  day of month
     * @return  new instance of {@code ThaiSolarCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.19/4.15
     */
    public static ThaiSolarCalendar of(
        ThaiSolarEra era,
        int yearOfEra,
        int month,
        int dayOfMonth
    ) {

        int prolepticYear = era.toIsoYear(yearOfEra, month);
        PlainDate iso = PlainDate.of(prolepticYear, month, dayOfMonth);
        return new ThaiSolarCalendar(iso);

    }

    /**
     * <p>Obtains the current calendar date in system time. </p>
     *
     * <p>Convenient short-cut for: {@code SystemClock.inLocalView().now(ThaiSolarCalendar.axis())}. </p>
     *
     * @return  current calendar date in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(net.time4j.engine.Chronology)
     * @since   3.23/4.19
     */
    /*[deutsch]
     * <p>Ermittelt das aktuelle Kalenderdatum in der Systemzeit. </p>
     *
     * <p>Bequeme Abk&uuml;rzung f&uuml;r: {@code SystemClock.inLocalView().now(ThaiSolarCalendar.axis())}. </p>
     *
     * @return  current calendar date in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(net.time4j.engine.Chronology)
     * @since   3.23/4.19
     */
    public static ThaiSolarCalendar nowInSystemTime() {

        return SystemClock.inLocalView().now(ThaiSolarCalendar.axis());

    }

    /**
     * <p>Yields the buddhist era. </p>
     *
     * @return  {@link ThaiSolarEra#BUDDHIST}
     * @since   3.19/4.15
     */
    /*[deutsch]
     * <p>Liefert die buddhistische &Auml;ra. </p>
     *
     * @return  {@link ThaiSolarEra#BUDDHIST}
     * @since   3.19/4.15
     */
    public ThaiSolarEra getEra() {

        return ThaiSolarEra.BUDDHIST;

    }

    /**
     * <p>Yields the buddhist Thai year. </p>
     *
     * @return  int
     * @see     ThaiSolarEra#BUDDHIST
     * @since   3.19/4.15
     */
    /*[deutsch]
     * <p>Liefert das buddhistische Thai-Jahr. </p>
     *
     * @return  int
     * @see     ThaiSolarEra#BUDDHIST
     * @since   3.19/4.15
     */
    public int getYear() {

        int isoYear = this.iso.getYear();

        if ((isoYear >= 1941) || (this.iso.getMonth() >= 4)) {
            return isoYear + 543;
        } else {
            return isoYear + 542;
        }

    }

    /**
     * <p>Yields the (gregorian) month. </p>
     *
     * @return  enum
     * @since   3.19/4.15
     */
    /*[deutsch]
     * <p>Liefert den (gregorianischen) Monat. </p>
     *
     * @return  enum
     * @since   3.19/4.15
     */
    public Month getMonth() {

        return Month.valueOf(this.iso.getMonth());

    }

    /**
     * <p>Yields the day of month. </p>
     *
     * @return  int
     * @since   3.19/4.15
     */
    /*[deutsch]
     * <p>Liefert den Tag des Monats. </p>
     *
     * @return  int
     * @since   3.19/4.15
     */
    public int getDayOfMonth() {

        return this.iso.getDayOfMonth();

    }

    /**
     * <p>Determines the day of week. </p>
     *
     * @return  Weekday
     * @since   3.19/4.15
     */
    /*[deutsch]
     * <p>Ermittelt den Wochentag. </p>
     *
     * @return  Weekday
     * @since   3.19/4.15
     */
    public Weekday getDayOfWeek() {

        return this.iso.getDayOfWeek();

    }

    /**
     * <p>Yields the day of year. </p>
     *
     * @return  int
     * @since   3.19/4.15
     */
    /*[deutsch]
     * <p>Liefert den Tag des Jahres. </p>
     *
     * @return  int
     * @since   3.19/4.15
     */
    public int getDayOfYear() {

        int doy = this.iso.get(PlainDate.DAY_OF_YEAR);

        if (this.iso.getYear() < 1941) {
            if (this.iso.getMonth() >= 4) {
                doy -= (this.iso.isLeapYear() ? 91 : 90);
            } else {
                doy += 275; // 91 + 92 + 92
            }
        }

        return doy;

    }

    /**
     * <p>Yields the length of current month in days. </p>
     *
     * @return  int
     * @since   3.19/4.15
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge des aktuellen Monats in Tagen. </p>
     *
     * @return  int
     * @since   3.19/4.15
     */
    public int lengthOfMonth() {

        return this.iso.lengthOfMonth();

    }

    /**
     * <p>Yields the length of current year in days. </p>
     *
     * @return  int
     * @since   3.19/4.15
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge des aktuellen Jahres in Tagen. </p>
     *
     * @return  int
     * @since   3.19/4.15
     */
    public int lengthOfYear() {

        int isoYear = this.iso.getYear();

        if (isoYear >= 1941) {
            return this.iso.lengthOfYear();
        } else if (this.iso.getMonth() >= 4) {
            if (isoYear == 1940) {
                return 275;
            } else {
                return (GregorianMath.isLeapYear(isoYear + 1) ? 366 : 365);
            }
        } else {
            return (this.iso.isLeapYear() ? 366 : 365);
        }

    }

    /**
     * <p>Is the year of this date a leap year? </p>
     *
     * @return  boolean
     * @since   3.19/4.15
     */
    /*[deutsch]
     * <p>Liegt dieses Datum in einem Schaltjahr? </p>
     *
     * @return  boolean
     * @since   3.19/4.15
     */
    public boolean isLeapYear() {

        return (this.lengthOfYear() == 366);

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
     * @since   3.19/4.15
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
     * @since   3.19/4.15
     */
    public GeneralTimestamp<ThaiSolarCalendar> at(PlainTime time) {

        return GeneralTimestamp.of(this, time);

    }

    /**
     * <p>Is equivalent to {@code at(PlainTime.of(hour, minute))}. </p>
     *
     * @param   hour        hour of day in range (0-24)
     * @param   minute      minute of hour in range (0-59)
     * @return  general timestamp as composition of this date and given time
     * @throws  IllegalArgumentException if any argument is out of range
     * @since   3.19/4.15
     */
    /*[deutsch]
     * <p>Entspricht {@code at(PlainTime.of(hour, minute))}. </p>
     *
     * @param   hour        hour of day in range (0-24)
     * @param   minute      minute of hour in range (0-59)
     * @return  general timestamp as composition of this date and given time
     * @throws  IllegalArgumentException if any argument is out of range
     * @since   3.19/4.15
     */
    public GeneralTimestamp<ThaiSolarCalendar> atTime(
        int hour,
        int minute
    ) {

        return this.at(PlainTime.of(hour, minute));

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof ThaiSolarCalendar) {
            ThaiSolarCalendar that = (ThaiSolarCalendar) obj;
            return this.iso.equals(that.iso);
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return this.iso.hashCode();

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(32);
        sb.append(this.getEra());
        sb.append('-');
        sb.append(this.getYear());
        sb.append('-');
        int m = this.getMonth().getValue();
        if (m < 10) {
            sb.append('0');
        }
        sb.append(m);
        sb.append('-');
        int d = this.getDayOfMonth();
        if (d < 10) {
            sb.append('0');
        }
        sb.append(d);
        return sb.toString();

    }

    /**
     * <p>Returns the associated time axis. </p>
     *
     * @return  chronology
     * @since   3.19/4.15
     */
    /*[deutsch]
     * <p>Liefert die zugeh&ouml;rige Zeitachse. </p>
     *
     * @return  chronology
     * @since   3.19/4.15
     */
    public static TimeAxis<CalendarUnit, ThaiSolarCalendar> axis() {

        return ENGINE;

    }

    @Override
    protected TimeAxis<CalendarUnit, ThaiSolarCalendar> getChronology() {

        return ENGINE;

    }

    @Override
    protected ThaiSolarCalendar getContext() {

        return this;

    }

    // serialization support
    PlainDate toISO() {

        return this.iso;

    }

    private static void registerUnits(TimeAxis.Builder<CalendarUnit, ThaiSolarCalendar> builder) {

        Set<CalendarUnit> monthly =
            EnumSet.range(CalendarUnit.MILLENNIA, CalendarUnit.MONTHS);
        Set<CalendarUnit> daily =
            EnumSet.range(CalendarUnit.WEEKS, CalendarUnit.DAYS);

        for (CalendarUnit unit : CalendarUnit.values()) {
            builder.appendUnit(
                unit,
                new ThaiUnitRule(unit),
                unit.getLength(),
                (unit.compareTo(CalendarUnit.WEEKS) < 0) ? monthly : daily
            );
        }

    }

    /**
     * @return replacement object in serialization graph
     * @serialData Uses <a href="../../../serialized-form.html#net.time4j.calendar.SPX">
     * a dedicated serialization form</a> as proxy. The first byte contains
     * the type-ID {@code 8}. Then the associated gregorian date is written.
     */
    private Object writeReplace() {

        return new SPX(this, SPX.THAI_SOLAR);

    }

    /**
     * @param in object input stream
     * @throws InvalidObjectException (always)
     * @serialData Blocks because a serialization proxy is required.
     */
    private void readObject(ObjectInputStream in)
        throws IOException {

        throw new InvalidObjectException("Serialization proxy required.");

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class Transformer
        implements EraYearMonthDaySystem<ThaiSolarCalendar> {

        //~ Methoden ------------------------------------------------------

        @Override
        public boolean isValid(
            CalendarEra era,
            int yearOfEra,
            int monthOfYear,
            int dayOfMonth
        ) {
            try {
                if ((era instanceof ThaiSolarEra) && (yearOfEra >= 1)) {
                    int prolepticYear =
                        ThaiSolarEra.class.cast(era).toIsoYear(yearOfEra, monthOfYear);
                    return (
                        (prolepticYear <= GregorianMath.MAX_YEAR)
                        && (monthOfYear >= 1)
                        && (monthOfYear <= 12)
                        && (dayOfMonth >= 1)
                        && (dayOfMonth <= GregorianMath.getLengthOfMonth(prolepticYear, monthOfYear))
                    );
                }
            } catch (RuntimeException re) {
                // 1940-anomaly or ArithmeticException => okay, we return false anyway
            }

            return false;
        }

        @Override
        public int getLengthOfMonth(
            CalendarEra era,
            int yearOfEra,
            int monthOfYear
        ) {
            try {
                int prolepticYear = ThaiSolarEra.class.cast(era).toIsoYear(yearOfEra, monthOfYear);
                return GregorianMath.getLengthOfMonth(prolepticYear, monthOfYear);
            } catch (RuntimeException re) {
                throw new IllegalArgumentException(re.getMessage(), re);
            }
        }

        @Override
        public int getLengthOfYear(
            CalendarEra era,
            int yearOfEra
        ) {
            if (yearOfEra < 1) {
                throw new IllegalArgumentException("Out of bounds: " + yearOfEra);
            } else if (era.equals(ThaiSolarEra.BUDDHIST)) {
                int y = yearOfEra - 543;
                if (y == 1940) {
                    return 275;
                } else if (y < 1940) {
                    y++;
                }
                return (GregorianMath.isLeapYear(y) ? 366 : 365);
            } else if (era.equals(ThaiSolarEra.RATTANAKOSIN)) {
                return (GregorianMath.isLeapYear(yearOfEra + 1782) ? 366 : 365);
            } else {
                throw new IllegalArgumentException("Invalid calendar era: " + era);
            }
        }

        @Override
        public ThaiSolarCalendar transform(long utcDays) {
            return new ThaiSolarCalendar(PlainDate.of(utcDays, EpochDays.UTC));
        }

        @Override
        public long transform(ThaiSolarCalendar date) {
            return date.iso.get(EpochDays.UTC);
        }

        @Override
        public long getMinimumSinceUTC() {
            return MIN_ISO.getDaysSinceEpochUTC();
        }

        @Override
        public long getMaximumSinceUTC() {
            return PlainDate.axis().getCalendarSystem().getMaximumSinceUTC();
        }

        @Override
        public List<CalendarEra> getEras() {
            CalendarEra e0 = ThaiSolarEra.RATTANAKOSIN;
            CalendarEra e1 = ThaiSolarEra.BUDDHIST;
            return Arrays.asList(e0, e1);
        }

    }

    private static class FieldRule<V extends Comparable<V>>
        implements ElementRule<ThaiSolarCalendar, V> {

        //~ Instanzvariablen ----------------------------------------------

        private final ChronoElement<V> element;

        //~ Konstruktoren -------------------------------------------------

        private FieldRule(ChronoElement<V> element) {
            super();

            this.element = element;

        }

        //~ Methoden ------------------------------------------------------

        static <V extends Comparable<V>> FieldRule<V> of(ChronoElement<V> element) {

            return new FieldRule<V>(element);

        }

        @Override
        public V getValue(ThaiSolarCalendar context) {

            Object result;

            if (this.element == ERA) {
                result = context.getEra();
            } else if (this.element.equals(YEAR_OF_ERA)) {
                result = context.getYear();
            } else if (this.element.equals(MONTH_OF_YEAR)) {
                result = context.getMonth();
            } else if (this.element.equals(DAY_OF_MONTH)) {
                result = context.getDayOfMonth();
            } else if (this.element.equals(DAY_OF_YEAR)) {
                result = context.getDayOfYear();
            } else if (this.element.equals(DAY_OF_WEEK)) {
                result = context.getDayOfWeek();
            } else {
                throw new ChronoException("Missing rule for: " + this.element.name());
            }

            return this.element.getType().cast(result);

        }

        @Override
        public V getMinimum(ThaiSolarCalendar context) {

            Object result;

            if (this.element == ERA) {
                result = ThaiSolarEra.BUDDHIST;
            } else if (Integer.class.isAssignableFrom(this.element.getType())) {
                result = Integer.valueOf(1);
            } else if (this.element.equals(MONTH_OF_YEAR)) {
                result = ((context.iso.getYear() >= 1941) ? Month.JANUARY : Month.APRIL);
            } else if (this.element.equals(DAY_OF_WEEK)) {
                result = Weekday.SUNDAY;
            } else {
                throw new ChronoException("Missing rule for: " + this.element.name());
            }

            return this.element.getType().cast(result);

        }

        @Override
        public V getMaximum(ThaiSolarCalendar context) {

            Object result;

            if (this.element == ERA) {
                result = ThaiSolarEra.BUDDHIST;
            } else if (this.element.equals(YEAR_OF_ERA)) {
                result = Integer.valueOf(GregorianMath.MAX_YEAR + 543);
            } else if (this.element.equals(MONTH_OF_YEAR)) {
                result = ((context.getYear() >= 2483) ? Month.DECEMBER : Month.MARCH);
            } else if (this.element.equals(DAY_OF_MONTH)) {
                result = Integer.valueOf(context.lengthOfMonth());
            } else if (this.element.equals(DAY_OF_YEAR)) {
                result = Integer.valueOf(context.lengthOfYear());
            } else if (this.element.equals(DAY_OF_WEEK)) {
                result = Weekday.SATURDAY;
            } else {
                throw new ChronoException("Missing rule for: " + this.element.name());
            }

            return this.element.getType().cast(result);

        }

        @Override
        public boolean isValid(
            ThaiSolarCalendar context,
            V value
        ) {

            if (value == null) {
                return false;
            } else if (this.element.getType().isEnum()) {
                if (this.element.equals(MONTH_OF_YEAR) && (context.getYear() == 2483)) {
                    return (Month.class.cast(value).getValue() >= 4);
                }
                return true;
            }

            V min = this.getMinimum(context);
            V max = this.getMaximum(context);

            return ((min.compareTo(value) <= 0) && (value.compareTo(max) <= 0));

        }

        @Override
        public ThaiSolarCalendar withValue(
            ThaiSolarCalendar context,
            V value,
            boolean lenient
        ) {

            if (!this.isValid(context, value)) {
                throw new IllegalArgumentException("Out of range: " + value);
            }

            if (this.element == ERA) {
                return context;
            } else if (this.element.equals(YEAR_OF_ERA)) {
                ThaiSolarCalendar tsc = ThaiSolarCalendar.ofBuddhist(toNumber(value), context.getMonth(), 1);
                return tsc.with(DAY_OF_MONTH, Math.min(context.getDayOfMonth(), tsc.lengthOfMonth()));
            } else if (this.element.equals(MONTH_OF_YEAR)) {
                ThaiSolarCalendar tsc = ThaiSolarCalendar.ofBuddhist(context.getYear(), Month.class.cast(value), 1);
                return tsc.with(DAY_OF_MONTH, Math.min(context.getDayOfMonth(), tsc.lengthOfMonth()));
            } else if (this.element.equals(DAY_OF_MONTH)) {
                PlainDate date = context.iso.with(PlainDate.DAY_OF_MONTH, toNumber(value));
                return new ThaiSolarCalendar(date);
            } else if (this.element.equals(DAY_OF_YEAR)) {
                int minMonth = ((context.iso.getYear() >= 1941) ? 1 : 4);
                ThaiSolarCalendar start = ThaiSolarCalendar.ofBuddhist(context.getYear(), minMonth, 1);
                PlainDate date = start.iso.plus(toNumber(value) - 1, CalendarUnit.DAYS);
                return new ThaiSolarCalendar(date);
            } else if (this.element.equals(DAY_OF_WEEK)) {
                PlainDate date =
                    context.iso.with(Weekmodel.of(Weekday.SUNDAY, 1).localDayOfWeek(), Weekday.class.cast(value));
                return new ThaiSolarCalendar(date);
            }

            throw new ChronoException("Missing rule for: " + this.element.name());

        }

        // optional
        @Override
        public ChronoElement<?> getChildAtFloor(ThaiSolarCalendar context) {

            return CHILDREN.get(this.element);

        }

        // optional
        @Override
        public ChronoElement<?> getChildAtCeiling(ThaiSolarCalendar context) {

            return CHILDREN.get(this.element);

        }

        private static int toNumber(Object value) {

            return Integer.class.cast(value).intValue();

        }

    }

    private static class ThaiUnitRule
        implements UnitRule<ThaiSolarCalendar> {

        //~ Instanzvariablen ----------------------------------------------

        private final CalendarUnit unit;

        //~ Konstruktoren -------------------------------------------------

        ThaiUnitRule(CalendarUnit unit) {
            super();

            this.unit = unit;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public ThaiSolarCalendar addTo(
            ThaiSolarCalendar date,
            long amount
        ) {

            return new ThaiSolarCalendar(date.iso.plus(amount, this.unit));

        }

        @Override
        public long between(
            ThaiSolarCalendar start,
            ThaiSolarCalendar end
        ) {

            return this.unit.between(start.iso, end.iso);

        }

    }

    private static class Merger
        implements ChronoMerger<ThaiSolarCalendar> {

        //~ Methoden ------------------------------------------------------

        @Override
        public String getFormatPattern(
            DisplayStyle style,
            Locale locale
        ) {

            return GenericDatePatterns.get("buddhist", style, locale);

        }

        @Override
        public StartOfDay getDefaultStartOfDay() {

            return StartOfDay.MIDNIGHT;

        }

        @Override
        public ThaiSolarCalendar createFrom(
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
        public ThaiSolarCalendar createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean preparsing
        ) {

            boolean lenient = attributes.get(Attributes.LENIENCY, Leniency.SMART).isLax();
            return this.createFrom(entity, attributes, lenient, preparsing);

        }

        @Override
        public ThaiSolarCalendar createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean lenient,
            boolean preparsing
        ) {

            if (entity.contains(PlainDate.COMPONENT)) {
                return new ThaiSolarCalendar(entity.get(PlainDate.COMPONENT));
            }

            ThaiSolarEra era;

            if (entity.contains(ERA)) {
                era = entity.get(ERA);
            } else if (lenient) {
                era = ThaiSolarEra.BUDDHIST;
            } else {
                entity.with(ValidationElement.ERROR_MESSAGE, "Missing Thai era.");
                return null;
            }

            int yearOfEra = entity.getInt(YEAR_OF_ERA);

            if (yearOfEra == Integer.MIN_VALUE) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Missing Thai year.");
                return null;
            }

            if (entity.contains(MONTH_OF_YEAR)) {
                int month = entity.get(MONTH_OF_YEAR).getValue();
                int dom = entity.getInt(DAY_OF_MONTH);
                if (dom != Integer.MIN_VALUE) {
                    if (CALSYS.isValid(era, yearOfEra, month, dom)) {
                        return ThaiSolarCalendar.of(era, yearOfEra, month, dom);
                    } else {
                        entity.with(ValidationElement.ERROR_MESSAGE, "Invalid Thai calendar date.");
                    }
                }
            } else {
                int doy = entity.getInt(DAY_OF_YEAR);
                if (doy != Integer.MIN_VALUE) {
                    if (doy > 0) {
                        int offset = ((era == ThaiSolarEra.RATTANAKOSIN) || (yearOfEra < 2484) ? 3 : 0);
                        int year = era.toIsoYear(yearOfEra, 4);
                        int month = 1 + offset;
                        int daycount = 0;
                        while (month <= 12 + offset) {
                            int realYear = year;
                            int realMonth = month;
                            if (realMonth > 12) {
                                if ((era == ThaiSolarEra.BUDDHIST) && (realYear == 1940)) {
                                    break; // day-of-year out of bounds
                                }
                                realYear++;
                                realMonth -= 12;
                            }
                            int len = GregorianMath.getLengthOfMonth(realYear, realMonth);
                            if (doy > daycount + len) {
                                month++;
                                daycount += len;
                            } else {
                                int dom = doy - daycount;
                                return ThaiSolarCalendar.of(era, yearOfEra, realMonth, dom);
                            }
                        }
                    }
                    entity.with(ValidationElement.ERROR_MESSAGE, "Invalid Thai calendar date.");
                }
            }

            return null;

        }

        @Override
        public ChronoDisplay preformat(
            ThaiSolarCalendar context,
            AttributeQuery attributes
        ) {

            return context;

        }

        @Override
        public Chronology<?> preparser() {

            return null;

        }

    }

}