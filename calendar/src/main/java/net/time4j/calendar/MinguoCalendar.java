/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
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
 * The Minguo calendar used in Taiwan (Republic Of China) uses as only difference to western gregorian
 * calendar a different year numbering with the Minguo era 1912-01-01. </p>
 *
 * <p>See also: <a href="https://en.wikipedia.org/wiki/Minguo_calendar">Wikipedia</a>. </p>
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
 * <p>Furthermore, all elements defined in {@code EpochDays} are supported. </p>
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
 * <p>Au&slig;erdem werden alle Elemente von {@code EpochDays} unterst&uuml;tzt. </p>
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
     * <p>Represents the Minguo era. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die Minguo-&Auml;ra. </p>
     */
    @FormattableElement(format = "G")
    public static final ChronoElement<MinguoEra> ERA =
        new StdEnumDateElement<MinguoEra, MinguoCalendar>(
            "ERA", MinguoCalendar.class, MinguoEra.class, 'G');

    /**
     * <p>Represents the Minguo year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert das Minguo-Jahr. </p>
     */
    @FormattableElement(format = "y")
    public static final StdCalendarElement<Integer, MinguoCalendar> YEAR_OF_ERA =
        new StdIntegerDateElement<MinguoCalendar>(
            "YEAR_OF_ERA",
            MinguoCalendar.class,
            1,
            GregorianMath.MAX_YEAR - 1911,
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
    public static final StdCalendarElement<Month, MinguoCalendar> MONTH_OF_YEAR =
        new StdEnumDateElement<Month, MinguoCalendar>(
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
        new StdIntegerDateElement<MinguoCalendar>("DAY_OF_MONTH", MinguoCalendar.class, 1, 31, 'd');

    /**
     * <p>Represents the day of year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den Tag des Jahres. </p>
     */
    @FormattableElement(format = "D")
    public static final StdCalendarElement<Integer, MinguoCalendar> DAY_OF_YEAR =
        new StdIntegerDateElement<MinguoCalendar>("DAY_OF_YEAR", MinguoCalendar.class, 1, 365, 'D');

    /**
     * <p>Represents the day of week. </p>
     * <p/>
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
    public static final StdCalendarElement<Weekday, MinguoCalendar> DAY_OF_WEEK =
        new StdWeekdayElement<MinguoCalendar>(MinguoCalendar.class);

    private static final Map<Object, ChronoElement<?>> CHILDREN;
    private static final MonthBasedCalendarSystem<MinguoCalendar> CALSYS;
    private static final TimeAxis<CalendarUnit, MinguoCalendar> ENGINE;

    static {
        Map<Object, ChronoElement<?>> children = new HashMap<Object, ChronoElement<?>>();
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

    private MinguoCalendar(PlainDate iso) {
        super();

        this.iso = iso;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new instance of a Minguo calendar date. </p>
     *
     * @param era        Minguo era
     * @param yearOfEra  Minguo year of era
     * @param month      gregorian month
     * @param dayOfMonth day of month
     * @return new instance of {@code MinguoCalendar}
     * @throws IllegalArgumentException in case of any inconsistencies
     * @since 3.13/4.10
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
     * @param era        Minguo era
     * @param yearOfEra  Minguo year of era
     * @param month      gregorian month
     * @param dayOfMonth day of month
     * @return new instance of {@code MinguoCalendar}
     * @throws IllegalArgumentException in case of any inconsistencies
     * @since 3.13/4.10
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
     * <p>Creates a new local timestamp with this date and given wall time. </p>
     * <p/>
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
        sb.append(' ');
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
     * @return replacement object in serialization graph
     * @serialData Uses <a href="../../../serialized-form.html#net.time4j.calendar.SPX">
     * a dedicated serialization form</a> as proxy. The first byte contains
     * the type-ID {@code 6}. Then the associated gregorian date is written.
     */
    private Object writeReplace() {

        return new SPX(this, SPX.MINGUO);

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
        implements MonthBasedCalendarSystem<MinguoCalendar> {

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

            return new FieldRule<V>(element);

        }

        @Override
        public V getValue(MinguoCalendar context) {

            String name = this.element.name();
            Object result;

            if (name.equals("ERA")) {
                result = context.getEra();
            } else if (name.equals("YEAR_OF_ERA")) {
                result = context.getYear();
            } else if (name.equals("MONTH_OF_YEAR")) {
                result = context.getMonth();
            } else if (name.equals("DAY_OF_MONTH")) {
                result = context.getDayOfMonth();
            } else if (name.equals("DAY_OF_YEAR")) {
                result = context.getDayOfYear();
            } else if (name.equals("DAY_OF_WEEK")) {
                result = context.getDayOfWeek();
            } else {
                throw new ChronoException("Missing rule for: " + name);
            }

            return this.element.getType().cast(result);

        }

        @Override
        public V getMinimum(MinguoCalendar context) {

            String name = this.element.name();
            Object result;

            if (name.equals("ERA")) {
                result = MinguoEra.BEFORE_ROC;
            } else if (Integer.class.isAssignableFrom(this.element.getType())) {
                result = Integer.valueOf(1);
            } else if (name.equals("MONTH_OF_YEAR")) {
                result = Month.JANUARY;
            } else if (name.equals("DAY_OF_WEEK")) {
                result = Weekmodel.of(Locale.TAIWAN).localDayOfWeek().getDefaultMinimum();
            } else {
                throw new ChronoException("Missing rule for: " + name);
            }

            return this.element.getType().cast(result);

        }

        @Override
        public V getMaximum(MinguoCalendar context) {

            String name = this.element.name();
            Object result;

            if (name.equals("ERA")) {
                result = MinguoEra.ROC;
            } else if (name.equals("YEAR_OF_ERA")) {
                MinguoEra era = context.getEra();
                result = (
                    (era == MinguoEra.ROC)
                        ? Integer.valueOf(GregorianMath.MAX_YEAR - 1911)
                        : Integer.valueOf(1912 - GregorianMath.MIN_YEAR));
            } else if (name.equals("MONTH_OF_YEAR")) {
                result = Month.DECEMBER;
            } else if (name.equals("DAY_OF_MONTH")) {
                result = context.iso.getMaximum(PlainDate.DAY_OF_MONTH);
            } else if (name.equals("DAY_OF_YEAR")) {
                result = context.iso.getMaximum(PlainDate.DAY_OF_YEAR);
            } else if (name.equals("DAY_OF_WEEK")) {
                result = Weekmodel.of(Locale.TAIWAN).localDayOfWeek().getDefaultMaximum();
            } else {
                throw new ChronoException("Missing rule for: " + name);
            }

            return this.element.getType().cast(result);

        }

        @Override
        public boolean isValid(
            MinguoCalendar context,
            V value
        ) {

            V min = this.getMinimum(context);
            V max = this.getMaximum(context);

            return ((value != null) && (min.compareTo(value) <= 0) && (value.compareTo(max) <= 0));

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

            String name = this.element.name();

            if (name.equals("ERA")) {
                MinguoEra era = MinguoEra.class.cast(value);
                int yearOfEra = context.getYear();
                if (era == MinguoEra.ROC) {
                    yearOfEra = Math.min(GregorianMath.MAX_YEAR - 1911, yearOfEra);
                } else {
                    yearOfEra = Math.min(1912 - GregorianMath.MIN_YEAR, yearOfEra);
                }
                MinguoCalendar mc = MinguoCalendar.of(era, yearOfEra, context.getMonth(), 1);
                return mc.with(DAY_OF_MONTH, Math.min(context.getDayOfMonth(), mc.lengthOfMonth()));
            } else if (name.equals("YEAR_OF_ERA")) {
                MinguoCalendar mc = MinguoCalendar.of(context.getEra(), toNumber(value), context.getMonth(), 1);
                return mc.with(DAY_OF_MONTH, Math.min(context.getDayOfMonth(), mc.lengthOfMonth()));
            } else if (name.equals("MONTH_OF_YEAR")) {
                PlainDate date = context.iso.with(PlainDate.MONTH_OF_YEAR, Month.class.cast(value));
                return new MinguoCalendar(date);
            } else if (name.equals("DAY_OF_MONTH")) {
                PlainDate date = context.iso.with(PlainDate.DAY_OF_MONTH, toNumber(value));
                return new MinguoCalendar(date);
            } else if (name.equals("DAY_OF_YEAR")) {
                PlainDate date = context.iso.with(PlainDate.DAY_OF_YEAR, toNumber(value));
                return new MinguoCalendar(date);
            } else if (name.equals("DAY_OF_WEEK")) {
                PlainDate date =
                    context.iso.with(Weekmodel.of(Locale.TAIWAN).localDayOfWeek(), Weekday.class.cast(value));
                return new MinguoCalendar(date);
            }

            throw new ChronoException("Missing rule for: " + name);

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
            DisplayStyle style,
            Locale locale
        ) {

            return GenericDatePatterns.get("roc", style, locale);

        }

        @Override
        public StartOfDay getDefaultStartOfDay() {

            return StartOfDay.MIDNIGHT;

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
            boolean preparsing
        ) {

            if (entity.contains(PlainDate.COMPONENT)) {
                return new MinguoCalendar(entity.get(PlainDate.COMPONENT));
            }

            MinguoEra era;

            if (entity.contains(ERA)) {
                era = entity.get(ERA);
            } else if (!attributes.get(Attributes.LENIENCY, Leniency.SMART).isStrict()) {
                era = MinguoEra.ROC;
            } else {
                entity.with(ValidationElement.ERROR_MESSAGE, "Missing Minguo era.");
                return null;
            }

            if (!entity.contains(YEAR_OF_ERA)) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Missing Minguo year.");
                return null;
            }

            int yearOfEra = entity.get(YEAR_OF_ERA).intValue();
            int prolepticYear = toProlepticYear(era, yearOfEra);

            if (entity.contains(MONTH_OF_YEAR) && entity.contains(DAY_OF_MONTH)) {
                int month = entity.get(MONTH_OF_YEAR).getValue();
                int dom = entity.get(DAY_OF_MONTH).intValue();
                if (CALSYS.isValid(era, yearOfEra, month, dom)) {
                    return MinguoCalendar.of(era, yearOfEra, month, dom);
                } else {
                    entity.with(ValidationElement.ERROR_MESSAGE, "Invalid Minguo date.");
                }
            } else if (entity.contains(DAY_OF_YEAR)) {
                int doy = entity.get(DAY_OF_YEAR).intValue();
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

            return null;

        }

        @Override
        public ChronoDisplay preformat(
            MinguoCalendar context,
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