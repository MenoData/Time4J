/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2022 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (MinguoCalendar.java) is part of project Time4J.
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
import net.time4j.base.MathUtils;
import net.time4j.base.TimeSource;
import net.time4j.calendar.service.GenericDatePatterns;
import net.time4j.calendar.service.RelatedGregorianYearRule;
import net.time4j.calendar.service.StdEnumDateElement;
import net.time4j.calendar.service.StdIntegerDateElement;
import net.time4j.calendar.service.StdWeekdayElement;
import net.time4j.calendar.service.WeekdayRule;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.CalendarEra;
import net.time4j.engine.Calendrical;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.ChronoMerger;
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
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * <p>The Minguo calendar used in Taiwan (Republic Of China) uses as only difference to western gregorian
 * calendar a different year numbering with the Minguo era 1912-01-01. </p>
 *
 * <p>See also: <a href="https://en.wikipedia.org/wiki/Minguo_calendar">Wikipedia</a>. </p>
 *
 * <p><strong>Following elements which are declared as constants are registered by this class</strong></p>
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
 * <p><strong>Support for unicode ca-extensions</strong></p>
 *
 * <pre>
 *      Locale locale = Locale.forLanguageTag(&quot;en-u-ca-roc&quot;);
 *      ChronoFormatter&lt;CalendarDate&gt; f = ChronoFormatter.ofGenericCalendarStyle(DisplayMode.FULL, locale);
 *      assertThat(
 *          f.format(PlainDate.of(2017, 10, 1)),
 *          is(&quot;Sunday, October 1, 106 Minguo&quot;));
 * </pre>
 *
 * @author  Meno Hochschild
 * @since   3.13/4.10
 */
/*[deutsch]
 * Der Minguo-Kalender wird in Taiwan (Republik von China) verwendet und hat als einzige Differenz
 * zum gregorianischen Kalender eine andere Jahresz&auml;hlung, indem als Ausgangspunkt die Minguo-&Auml;ra
 * 1912-01-01 benutzt wird. </p>
 *
 * <p>Siehe auch: <a href="https://en.wikipedia.org/wiki/Minguo_calendar">Wikipedia</a>. </p>
 *
 * <p><strong>Registriert sind folgende als Konstanten deklarierte Elemente</strong></p>
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
 * <p><strong>Unterst&uuml;tzung f&uuml;r Unicode-ca-Erweiterungen</strong></p>
 *
 * <pre>
 *      Locale locale = Locale.forLanguageTag(&quot;en-u-ca-roc&quot;);
 *      ChronoFormatter&lt;CalendarDate&gt; f = ChronoFormatter.ofGenericCalendarStyle(DisplayMode.FULL, locale);
 *      assertThat(
 *          f.format(PlainDate.of(2017, 10, 1)),
 *          is(&quot;Sunday, October 1, 106 Minguo&quot;));
 * </pre>
 *
 * @author  Meno Hochschild
 * @since   3.13/4.10
 */
@CalendarType("roc")
public final class MinguoCalendar
    extends Calendrical<CalendarUnit, MinguoCalendar>
    implements LocalizedPatternSupport {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Represents the Minguo era which cannot be changed. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die Minguo-&Auml;ra, die nicht ge&auml;ndert werden kann. </p>
     */
    @FormattableElement(format = "G")
    public static final ChronoElement<MinguoEra> ERA =
        new StdEnumDateElement<>("ERA", MinguoCalendar.class, MinguoEra.class, 'G');

    /**
     * <p>Represents the Minguo year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert das Minguo-Jahr. </p>
     */
    @FormattableElement(format = "y")
    public static final StdCalendarElement<Integer, MinguoCalendar> YEAR_OF_ERA =
        new StdIntegerDateElement<>(
            "YEAR_OF_ERA",
            MinguoCalendar.class,
            1,
            GregorianMath.MAX_YEAR - 1911,
            'y');

    /**
     * <p>Represents the month. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den Monat. </p>
     */
    @FormattableElement(format = "M", alt = "L")
    public static final StdCalendarElement<Month, MinguoCalendar> MONTH_OF_YEAR =
        new StdEnumDateElement<>(
            "MONTH_OF_YEAR",
            MinguoCalendar.class,
            Month.class,
            'M');

    /**
     * <p>Represents the day of month. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den Tag des Monats. </p>
     */
    @FormattableElement(format = "d")
    public static final StdCalendarElement<Integer, MinguoCalendar> DAY_OF_MONTH =
        new StdIntegerDateElement<>("DAY_OF_MONTH", MinguoCalendar.class, 1, 31, 'd');

    /**
     * <p>Represents the day of year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den Tag des Jahres. </p>
     */
    @FormattableElement(format = "D")
    public static final StdCalendarElement<Integer, MinguoCalendar> DAY_OF_YEAR =
        new StdIntegerDateElement<>("DAY_OF_YEAR", MinguoCalendar.class, 1, 365, 'D');

    /**
     * <p>Represents the day of week. </p>
     *
     * <p>If the day-of-week is set to a new value then Time4J handles the calendar week
     * as starting on Sunday. </p>
     *
     * @see     #getDefaultWeekmodel()
     * @see     CommonElements#localDayOfWeek(net.time4j.engine.Chronology, Weekmodel)
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den Tag der Woche. </p>
     *
     * <p>Wenn der Tag der Woche auf einen neuen Wert gesetzt wird, behandelt Time4J die
     * Kalenderwoche so, da&szlig; sie am Sonntag beginnt. </p>
     *
     * @see     #getDefaultWeekmodel()
     * @see     CommonElements#localDayOfWeek(Chronology, Weekmodel)
     */
    @FormattableElement(format = "E")
    public static final StdCalendarElement<Weekday, MinguoCalendar> DAY_OF_WEEK =
        new StdWeekdayElement<>(MinguoCalendar.class, getDefaultWeekmodel());

    private static final WeekdayInMonthElement<MinguoCalendar> WIM_ELEMENT =
        new WeekdayInMonthElement<>(MinguoCalendar.class, DAY_OF_MONTH, DAY_OF_WEEK);

    /**
     * <p>Element with the ordinal day-of-week within given calendar month. </p>
     */
    /*[deutsch]
     * <p>Element mit dem x-ten Wochentag im Monat. </p>
     */
    @FormattableElement(format = "F")
    public static final OrdinalWeekdayElement<MinguoCalendar> WEEKDAY_IN_MONTH = WIM_ELEMENT;

    private static final Map<Object, ChronoElement<?>> CHILDREN;
    private static final EraYearMonthDaySystem<MinguoCalendar> CALSYS;
    private static final TimeAxis<CalendarUnit, MinguoCalendar> ENGINE;

    static {
        Map<Object, ChronoElement<?>> children = new HashMap<>();
        children.put(ERA, YEAR_OF_ERA);
        children.put(YEAR_OF_ERA, MONTH_OF_YEAR);
        children.put(MONTH_OF_YEAR, DAY_OF_MONTH);
        CHILDREN = Collections.unmodifiableMap(children);

        CALSYS = new Transformer();

        TimeAxis.Builder<CalendarUnit, MinguoCalendar> builder =
            TimeAxis.Builder.setUp(
                CalendarUnit.class,
                MinguoCalendar.class,
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
                new RelatedGregorianYearRule<>(CALSYS, DAY_OF_YEAR))
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
                new WeekdayRule<>(
                    getDefaultWeekmodel(),
                    (context) -> CALSYS
                ),
                CalendarUnit.DAYS)
            .appendElement(
                WIM_ELEMENT,
                WeekdayInMonthElement.getRule(WIM_ELEMENT))
            .appendExtension(
                new CommonElements.Weekengine(
                    MinguoCalendar.class,
                    DAY_OF_MONTH,
                    DAY_OF_YEAR,
                    getDefaultWeekmodel()));
            registerUnits(builder);
        ENGINE = builder.build();
    }

    private static final long serialVersionUID = -6628190121085147706L;

    //~ Instanzvariablen --------------------------------------------------

    private final PlainDate iso;

    //~ Konstruktoren -----------------------------------------------------

    private MinguoCalendar(PlainDate iso) {
        super();

        this.iso = iso;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new instance of a Minguo calendar date. </p>
     *
     * @param   era         Minguo era
     * @param   yearOfEra   Minguo year of era
     * @param   month       gregorian month
     * @param   dayOfMonth  day of month
     * @return  new instance of {@code MinguoCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.13/4.10
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Minguo-Kalenderdatum. </p>
     *
     * @param   era         Minguo era
     * @param   yearOfEra   Minguo year of era
     * @param   month       gregorian month
     * @param   dayOfMonth  day of month
     * @return  new instance of {@code MinguoCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.13/4.10
     */
    public static MinguoCalendar of(
        MinguoEra era,
        int yearOfEra,
        Month month,
        int dayOfMonth
    ) {

        return MinguoCalendar.of(era, yearOfEra, month.getValue(), dayOfMonth);

    }

    /**
     * <p>Creates a new instance of an Minguo calendar date. </p>
     *
     * @param   era         Minguo era
     * @param   yearOfEra   Minguo year of era
     * @param   month       gregorian month
     * @param   dayOfMonth  day of month
     * @return  new instance of {@code MinguoCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.13/4.10
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Minguo-Kalenderdatum. </p>
     *
     * @param   era         Minguo era
     * @param   yearOfEra   Minguo year of era
     * @param   month       gregorian month
     * @param   dayOfMonth  day of month
     * @return  new instance of {@code MinguoCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.13/4.10
     */
    public static MinguoCalendar of(
        MinguoEra era,
        int yearOfEra,
        int month,
        int dayOfMonth
    ) {

        int prolepticYear = toProlepticYear(era, yearOfEra);
        PlainDate iso = PlainDate.of(prolepticYear, month, dayOfMonth);
        return new MinguoCalendar(iso);

    }

    /**
     * <p>Obtains the current calendar date in system time. </p>
     *
     * <p>Convenient short-cut for: {@code SystemClock.inLocalView().now(MinguoCalendar.axis())}. </p>
     *
     * @return  current calendar date in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(net.time4j.engine.Chronology)
     * @since   3.23/4.19
     */
    /*[deutsch]
     * <p>Ermittelt das aktuelle Kalenderdatum in der Systemzeit. </p>
     *
     * <p>Bequeme Abk&uuml;rzung f&uuml;r: {@code SystemClock.inLocalView().now(MinguoCalendar.axis())}. </p>
     *
     * @return  current calendar date in system time zone using the system clock
     * @see     SystemClock#inLocalView()
     * @see     net.time4j.ZonalClock#now(net.time4j.engine.Chronology)
     * @since   3.23/4.19
     */
    public static MinguoCalendar nowInSystemTime() {

        return SystemClock.inLocalView().now(MinguoCalendar.axis());

    }

    /**
     * <p>Yields the Minguo era. </p>
     *
     * @return enum
     * @since 3.13/4.10
     */
    /*[deutsch]
     * <p>Liefert die Minguo-&Auml;ra. </p>
     *
     * @return  enum
     * @since   3.13/4.10
     */
    public MinguoEra getEra() {

        return ((this.iso.getYear() < 1912) ? MinguoEra.BEFORE_ROC : MinguoEra.ROC);

    }

    /**
     * <p>Yields the Minguo year. </p>
     *
     * @return int
     * @since 3.13/4.10
     */
    /*[deutsch]
     * <p>Liefert das Minguo-Jahr. </p>
     *
     * @return  int
     * @since   3.13/4.10
     */
    public int getYear() {

        MinguoEra era = this.getEra();
        return ((era == MinguoEra.ROC) ? this.iso.getYear() - 1911 : 1912 - this.iso.getYear());

    }

    /**
     * <p>Yields the (gregorian) month. </p>
     *
     * @return enum
     * @since 3.13/4.10
     */
    /*[deutsch]
     * <p>Liefert den (gregorianischen) Monat. </p>
     *
     * @return  enum
     * @since   3.13/4.10
     */
    public Month getMonth() {

        return Month.valueOf(this.iso.getMonth());

    }

    /**
     * <p>Yields the day of month. </p>
     *
     * @return int
     * @since 3.13/4.10
     */
    /*[deutsch]
     * <p>Liefert den Tag des Monats. </p>
     *
     * @return  int
     * @since   3.13/4.10
     */
    public int getDayOfMonth() {

        return this.iso.getDayOfMonth();

    }

    /**
     * <p>Determines the day of week. </p>
     *
     * @return Weekday
     * @since 3.13/4.10
     */
    /*[deutsch]
     * <p>Ermittelt den Wochentag. </p>
     *
     * @return  Weekday
     * @since   3.13/4.10
     */
    public Weekday getDayOfWeek() {

        return this.iso.get(PlainDate.DAY_OF_WEEK);

    }

    /**
     * <p>Yields the day of year. </p>
     *
     * @return int
     * @since 3.13/4.10
     */
    /*[deutsch]
     * <p>Liefert den Tag des Jahres. </p>
     *
     * @return  int
     * @since   3.13/4.10
     */
    public int getDayOfYear() {

        return this.iso.get(PlainDate.DAY_OF_YEAR);

    }

    /**
     * <p>Yields the length of current month in days. </p>
     *
     * @return int
     * @since 3.13/4.10
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge des aktuellen Monats in Tagen. </p>
     *
     * @return  int
     * @since   3.13/4.10
     */
    public int lengthOfMonth() {

        return this.iso.lengthOfMonth();

    }

    /**
     * <p>Yields the length of current year in days. </p>
     *
     * @return int
     * @since 3.13/4.10
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge des aktuellen Jahres in Tagen. </p>
     *
     * @return  int
     * @since   3.13/4.10
     */
    public int lengthOfYear() {

        return this.iso.lengthOfYear();

    }

    /**
     * <p>Is the year of this date a leap year? </p>
     *
     * @return boolean
     * @since 3.13/4.10
     */
    /*[deutsch]
     * <p>Liegt dieses Datum in einem Schaltjahr? </p>
     *
     * @return  boolean
     * @since   3.13/4.10
     */
    public boolean isLeapYear() {

        return this.iso.isLeapYear();

    }

    /**
     * <p>Queries if given parameter values form a well defined calendar date. </p>
     *
     * @param   era         the era to be checked
     * @param   yearOfEra   the year of era to be checked
     * @param   month       the month to be checked
     * @param   dayOfMonth  the day of month to be checked
     * @return  {@code true} if valid else  {@code false}
     * @see     #of(MinguoEra, int, int, int)
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Pr&uuml;ft, ob die angegebenen Parameter ein wohldefiniertes Kalenderdatum beschreiben. </p>
     *
     * @param   era         the era to be checked
     * @param   yearOfEra   the year of era to be checked
     * @param   month       the month to be checked
     * @param   dayOfMonth  the day of month to be checked
     * @return  {@code true} if valid else  {@code false}
     * @see     #of(MinguoEra, int, int, int)
     * @since   3.34/4.29
     */
    public static boolean isValid(
        MinguoEra era,
        int yearOfEra,
        int month,
        int dayOfMonth
    ) {

        return CALSYS.isValid(era, yearOfEra, month, dayOfMonth);

    }

    /**
     * <p>Creates a new local timestamp with this date and given wall time. </p>
     *
     * <p>If the time {@link PlainTime#midnightAtEndOfDay() T24:00} is used
     * then the resulting timestamp will automatically be normalized such
     * that the timestamp will contain the following day instead. </p>
     *
     * @param time wall time
     * @return general timestamp as composition of this date and given time
     * @since 3.13/4.10
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
     * @since   3.13/4.10
     */
    public GeneralTimestamp<MinguoCalendar> at(PlainTime time) {

        return GeneralTimestamp.of(this, time);

    }

    /**
     * <p>Is equivalent to {@code at(PlainTime.of(hour, minute))}. </p>
     *
     * @param hour   hour of day in range (0-24)
     * @param minute minute of hour in range (0-59)
     * @return general timestamp as composition of this date and given time
     * @throws IllegalArgumentException if any argument is out of range
     * @since 3.13/4.10
     */
    /*[deutsch]
     * <p>Entspricht {@code at(PlainTime.of(hour, minute))}. </p>
     *
     * @param   hour        hour of day in range (0-24)
     * @param   minute      minute of hour in range (0-59)
     * @return  general timestamp as composition of this date and given time
     * @throws  IllegalArgumentException if any argument is out of range
     * @since   3.13/4.10
     */
    public GeneralTimestamp<MinguoCalendar> atTime(
        int hour,
        int minute
    ) {

        return this.at(PlainTime.of(hour, minute));

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof MinguoCalendar) {
            MinguoCalendar that = (MinguoCalendar) obj;
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
     * <p>Obtains the standard week model of this calendar. </p>
     *
     * <p>The Minguo calendar usually starts on Sunday. </p>
     *
     * @return  Weekmodel
     * @since   3.24/4.20
     */
    /*[deutsch]
     * <p>Ermittelt das Standardwochenmodell dieses Kalenders. </p>
     *
     * <p>Der Minguo-Kalender startet normalerweise am Sonntag. </p>
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
     * @return chronology
     * @since 3.13/4.10
     */
    /*[deutsch]
     * <p>Liefert die zugeh&ouml;rige Zeitachse. </p>
     *
     * @return  chronology
     * @since   3.13/4.10
     */
    public static TimeAxis<CalendarUnit, MinguoCalendar> axis() {

        return ENGINE;

    }

    @Override
    protected TimeAxis<CalendarUnit, MinguoCalendar> getChronology() {

        return ENGINE;

    }

    @Override
    protected MinguoCalendar getContext() {

        return this;

    }

    // deserialization
    PlainDate toISO() {

        return this.iso;

    }

    private static int toProlepticYear(
        MinguoEra era,
        int yearOfEra
    ) {

        return (
            (era == MinguoEra.ROC)
            ? MathUtils.safeAdd(yearOfEra, 1911)
            : MathUtils.safeSubtract(1912, yearOfEra));

    }

    private static void registerUnits(TimeAxis.Builder<CalendarUnit, MinguoCalendar> builder) {

        Set<CalendarUnit> monthly =
            EnumSet.range(CalendarUnit.MILLENNIA, CalendarUnit.MONTHS);
        Set<CalendarUnit> daily =
            EnumSet.range(CalendarUnit.WEEKS, CalendarUnit.DAYS);

        for (CalendarUnit unit : CalendarUnit.values()) {
            builder.appendUnit(
                unit,
                new MinguoUnitRule(unit),
                unit.getLength(),
                (unit.compareTo(CalendarUnit.WEEKS) < 0) ? monthly : daily
            );
        }

    }

    /**
     * @serialData  Uses <a href="../../../serialized-form.html#net.time4j.calendar.SPX">
     *              a dedicated serialization form</a> as proxy. The first byte contains
     *              the type-ID {@code 6}. Then the associated gregorian date is written.
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX6(this);

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
        implements EraYearMonthDaySystem<MinguoCalendar> {

        //~ Methoden ------------------------------------------------------

        @Override
        public boolean isValid(
            CalendarEra era,
            int yearOfEra,
            int monthOfYear,
            int dayOfMonth
        ) {
            try {
                if (era instanceof MinguoEra) {
                    int prolepticYear = toProlepticYear(MinguoEra.class.cast(era), yearOfEra);

                    return (
                        (yearOfEra >= 1)
                        && (prolepticYear <= GregorianMath.MAX_YEAR)
                        && (monthOfYear >= 1)
                        && (monthOfYear <= 12)
                        && (dayOfMonth >= 1)
                        && (dayOfMonth <= GregorianMath.getLengthOfMonth(prolepticYear, monthOfYear))
                    );
                }
            } catch (ArithmeticException ae) {
                // okay, we return false anyway
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
                int prolepticYear = toProlepticYear(MinguoEra.class.cast(era), yearOfEra);
                return PlainDate.of(prolepticYear, monthOfYear, 1).lengthOfMonth();
            } catch (RuntimeException re) {
                throw new IllegalArgumentException(re.getMessage(), re);
            }
        }

        @Override
        public int getLengthOfYear(
            CalendarEra era,
            int yearOfEra
        ) {
            try {
                int prolepticYear = toProlepticYear(MinguoEra.class.cast(era), yearOfEra);
                return PlainDate.of(prolepticYear, Month.JANUARY, 1).lengthOfYear();
            } catch (RuntimeException re) {
                throw new IllegalArgumentException(re.getMessage(), re);
            }
        }

        @Override
        public MinguoCalendar transform(long utcDays) {
            return new MinguoCalendar(PlainDate.of(utcDays, EpochDays.UTC));
        }

        @Override
        public long transform(MinguoCalendar date) {
            return date.iso.get(EpochDays.UTC);
        }

        @Override
        public long getMinimumSinceUTC() {
            return PlainDate.axis().getCalendarSystem().getMinimumSinceUTC();
        }

        @Override
        public long getMaximumSinceUTC() {
            return PlainDate.axis().getCalendarSystem().getMaximumSinceUTC();
        }

        @Override
        public List<CalendarEra> getEras() {
            CalendarEra e0 = MinguoEra.BEFORE_ROC;
            CalendarEra e1 = MinguoEra.ROC;
            return Arrays.asList(e0, e1);
        }

    }

    private static class FieldRule<V extends Comparable<V>>
        implements ElementRule<MinguoCalendar, V> {

        //~ Instanzvariablen ----------------------------------------------

        private final ChronoElement<V> element;

        //~ Konstruktoren -------------------------------------------------

        private FieldRule(ChronoElement<V> element) {
            super();

            this.element = element;

        }

        //~ Methoden ------------------------------------------------------

        static <V extends Comparable<V>> FieldRule<V> of(ChronoElement<V> element) {

            return new FieldRule<>(element);

        }

        @Override
        public V getValue(MinguoCalendar context) {

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
            } else {
                throw new ChronoException("Missing rule for: " + this.element.name());
            }

            return this.element.getType().cast(result);

        }

        @Override
        public V getMinimum(MinguoCalendar context) {

            Object result;

            if (this.element == ERA) {
                result = MinguoEra.BEFORE_ROC;
            } else if (Integer.class.isAssignableFrom(this.element.getType())) {
                result = Integer.valueOf(1);
            } else if (this.element.equals(MONTH_OF_YEAR)) {
                result = Month.JANUARY;
            } else {
                throw new ChronoException("Missing rule for: " + this.element.name());
            }

            return this.element.getType().cast(result);

        }

        @Override
        public V getMaximum(MinguoCalendar context) {

            Object result;

            if (this.element == ERA) {
                result = MinguoEra.ROC;
            } else if (this.element.equals(YEAR_OF_ERA)) {
                MinguoEra era = context.getEra();
                result = (
                    (era == MinguoEra.ROC)
                        ? Integer.valueOf(GregorianMath.MAX_YEAR - 1911)
                        : Integer.valueOf(1912 - GregorianMath.MIN_YEAR));
            } else if (this.element.equals(MONTH_OF_YEAR)) {
                result = Month.DECEMBER;
            } else if (this.element.equals(DAY_OF_MONTH)) {
                result = context.iso.getMaximum(PlainDate.DAY_OF_MONTH);
            } else if (this.element.equals(DAY_OF_YEAR)) {
                result = context.iso.getMaximum(PlainDate.DAY_OF_YEAR);
            } else {
                throw new ChronoException("Missing rule for: " + this.element.name());
            }

            return this.element.getType().cast(result);

        }

        @Override
        public boolean isValid(
            MinguoCalendar context,
            V value
        ) {

            if (value == null) {
                return false;
            } else if (this.element == ERA) {
                return value.equals(context.getEra());
            }

            V min = this.getMinimum(context);
            V max = this.getMaximum(context);

            return ((min.compareTo(value) <= 0) && (value.compareTo(max) <= 0));

        }

        @Override
        public MinguoCalendar withValue(
            MinguoCalendar context,
            V value,
            boolean lenient
        ) {

            if (!this.isValid(context, value)) {
                throw new IllegalArgumentException("Out of range: " + value);
            }

            if (this.element == ERA) {
                return context;
            } else if (this.element.equals(YEAR_OF_ERA)) {
                MinguoCalendar mc = MinguoCalendar.of(context.getEra(), toNumber(value), context.getMonth(), 1);
                return mc.with(DAY_OF_MONTH, Math.min(context.getDayOfMonth(), mc.lengthOfMonth()));
            } else if (this.element.equals(MONTH_OF_YEAR)) {
                PlainDate date = context.iso.with(PlainDate.MONTH_OF_YEAR, Month.class.cast(value));
                return new MinguoCalendar(date);
            } else if (this.element.equals(DAY_OF_MONTH)) {
                PlainDate date = context.iso.with(PlainDate.DAY_OF_MONTH, toNumber(value));
                return new MinguoCalendar(date);
            } else if (this.element.equals(DAY_OF_YEAR)) {
                PlainDate date = context.iso.with(PlainDate.DAY_OF_YEAR, toNumber(value));
                return new MinguoCalendar(date);
            }

            throw new ChronoException("Missing rule for: " + this.element.name());

        }

        // optional
        @Override
        public ChronoElement<?> getChildAtFloor(MinguoCalendar context) {

            return CHILDREN.get(this.element);

        }

        // optional
        @Override
        public ChronoElement<?> getChildAtCeiling(MinguoCalendar context) {

            return CHILDREN.get(this.element);

        }

        private static int toNumber(Object value) {

            return Integer.class.cast(value).intValue();

        }

    }

    private static class MinguoUnitRule
        implements UnitRule<MinguoCalendar> {

        //~ Instanzvariablen ----------------------------------------------

        private final CalendarUnit unit;

        //~ Konstruktoren -------------------------------------------------

        MinguoUnitRule(CalendarUnit unit) {
            super();

            this.unit = unit;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public MinguoCalendar addTo(
            MinguoCalendar date,
            long amount
        ) {

            return new MinguoCalendar(date.iso.plus(amount, this.unit));

        }

        @Override
        public long between(
            MinguoCalendar start,
            MinguoCalendar end
        ) {

            return this.unit.between(start.iso, end.iso);

        }

    }

    private static class Merger
        implements ChronoMerger<MinguoCalendar> {

        //~ Methoden ------------------------------------------------------

        @Override
        public String getFormatPattern(
            FormatStyle style,
            Locale locale
        ) {

            return GenericDatePatterns.get("roc", style, locale);

        }

        @Override
        public MinguoCalendar createFrom(
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
        public MinguoCalendar createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean lenient,
            boolean preparsing
        ) {

            if (entity.contains(PlainDate.COMPONENT)) {
                return new MinguoCalendar(entity.get(PlainDate.COMPONENT));
            }

            MinguoEra era;

            if (entity.contains(ERA)) {
                era = entity.get(ERA);
            } else if (lenient) {
                era = MinguoEra.ROC;
            } else {
                entity.with(ValidationElement.ERROR_MESSAGE, "Missing Minguo era.");
                return null;
            }

            int yearOfEra = entity.getInt(YEAR_OF_ERA);

            if (yearOfEra == Integer.MIN_VALUE) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Missing Minguo year.");
                return null;
            }

            int prolepticYear = toProlepticYear(era, yearOfEra);

            if (entity.contains(MONTH_OF_YEAR)) {
                int month = entity.get(MONTH_OF_YEAR).getValue();
                int dom = entity.getInt(DAY_OF_MONTH);
                if (dom != Integer.MIN_VALUE) {
                    if (CALSYS.isValid(era, yearOfEra, month, dom)) {
                        return MinguoCalendar.of(era, yearOfEra, month, dom);
                    } else {
                        entity.with(ValidationElement.ERROR_MESSAGE, "Invalid Minguo date.");
                    }
                }
            } else {
                int doy = entity.getInt(DAY_OF_YEAR);
                if (doy != Integer.MIN_VALUE) {
                    if (doy > 0) {
                        int month = 1;
                        int daycount = 0;
                        while (month <= 12) {
                            int len = GregorianMath.getLengthOfMonth(prolepticYear, month);
                            if (doy > daycount + len) {
                                month++;
                                daycount += len;
                            } else {
                                int dom = doy - daycount;
                                return MinguoCalendar.of(era, yearOfEra, month, dom);
                            }
                        }
                    }
                    entity.with(ValidationElement.ERROR_MESSAGE, "Invalid Minguo date.");
                }
            }

            return null;

        }

        @Override
        public int getDefaultPivotYear() {

            return PlainDate.axis().getDefaultPivotYear() - 1911;

        }

    }

}