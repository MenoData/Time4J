/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (EthiopianCalendar.java) is part of project Time4J.
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

import net.time4j.Moment;
import net.time4j.PlainDate;
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
 * <p>Represents the calendar used in Ethiopia. </p>
 *
 * <p>It is built on the base of the <a href="CopticCalendar.html">Coptic calendar</a>
 * but uses two different eras. Another difference to Coptic calendar is the day starting
 * in the morning at 06:00 (usual approximation for sunrise). For more details see
 * <a href="http://ayannanahmias.com/2011/06/15/ethiopian-calendar/">Intro to the Ethiopic Calendar</a>. </p>
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
 * <p>Example of usage: </p>
 *
 * <pre>
 *     ChronoFormatter&lt;EthiopianCalendar&gt; formatter =
 *       ChronoFormatter.setUp(EthiopianCalendar.axis(), Locale.ENGLISH)
 *       .addPattern(&quot;EEE, d. MMMM yyyy&quot;, PatternType.NON_ISO_DATE).build();
 *     PlainDate today = SystemClock.inLocalView().today();
 *     EthiopianCalendar ethiopianDate = today.transform(EthiopianCalendar.class); // conversion at noon
 *     System.out.println(formatter.format(ethiopianDate));
 * </pre>
 *
 * @author  Meno Hochschild
 * @since   3.11/4.8
 */
/*[deutsch]
 * <p>Repr&auml;sentiert den Kalender, der in &Auml;thiopien als offizieller Kalender verwendet wird. </p>
 *
 * <p>Er ist im wesentlichen wie der <a href="CopticCalendar.html">koptische Kalender</a> aufgebaut, verwendet
 * aber zwei andere &Auml;ras. Eine weitere Differenz zum koptischen Kalender ist, da&szlig; der Kalendertag
 * am Morgen um 06:00 beginnt (typische N&auml;herung f&uuml;r den Sonnenaufgang). Mehr Details siehe
 * <a href="http://ayannanahmias.com/2011/06/15/ethiopian-calendar/">Intro to the Ethiopic Calendar</a>. </p>
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
 * <p>Anwendungsbeispiel: </p>
 *
 * <pre>
 *     ChronoFormatter&lt;EthiopianCalendar&gt; formatter =
 *       ChronoFormatter.setUp(EthiopianCalendar.axis(), Locale.ENGLISH)
 *       .addPattern(&quot;EEE, d. MMMM yyyy&quot;, PatternType.NON_ISO_DATE).build();
 *     PlainDate today = SystemClock.inLocalView().today();
 *     EthiopianCalendar ethiopianDate = today.transform(EthiopianCalendar.class); // conversion at noon
 *     System.out.println(formatter.format(ethiopianDate));
 * </pre>
 *
 * @author  Meno Hochschild
 * @since   3.11/4.8
 */
@CalendarType("ethiopic")
public final class EthiopianCalendar
    extends Calendrical<EthiopianCalendar.Unit, EthiopianCalendar>
    implements LocalizedPatternSupport {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int DELTA_ALEM_MIHRET = 5500;
    private static final long MIHRET_EPOCH;

    static {
        PlainDate mihret = ChronoHistory.PROLEPTIC_JULIAN.convert(HistoricDate.of(HistoricEra.AD, 8, 8, 29));
        MIHRET_EPOCH = mihret.get(EpochDays.UTC);
    }

    private static final int YEAR_INDEX = 0;
    private static final int DAY_OF_MONTH_INDEX = 2;
    private static final int DAY_OF_YEAR_INDEX = 3;

    /**
     * <p>Represents the Ethiopian era. </p>
     *
     * <p>A change of the era has no effect. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die &auml;thiopische &Auml;ra. </p>
     *
     * <p>Eine &Auml;nderung der &Auml;ra hat keine Wirkung. </p>
     */
    @FormattableElement(format = "G")
    public static final TextElement<EthiopianEra> ERA =
        new StdEnumDateElement<EthiopianEra, EthiopianCalendar>(
            "ERA", EthiopianCalendar.class, EthiopianEra.class, 'G');

    /**
     * <p>Represents the Ethiopian year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert das &auml;thiopische Jahr. </p>
     */
    @FormattableElement(format = "y")
    public static final StdCalendarElement<Integer, EthiopianCalendar> YEAR_OF_ERA =
        new StdIntegerDateElement<EthiopianCalendar>(
            "YEAR_OF_ERA",
            EthiopianCalendar.class,
            1,
            9999,
            'y',
            null,
            null);

    /**
     * <p>Represents the Ethiopian month. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den &auml;thiopischen Monat. </p>
     */
    @FormattableElement(format = "M", standalone = "L")
    public static final StdCalendarElement<EthiopianMonth, EthiopianCalendar> MONTH_OF_YEAR =
        new StdEnumDateElement<EthiopianMonth, EthiopianCalendar>(
            "MONTH_OF_YEAR",
            EthiopianCalendar.class,
            EthiopianMonth.class,
            'M');

    /**
     * <p>Represents the Ethiopian day of month. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den &auml;thiopischen Tag des Monats. </p>
     */
    @FormattableElement(format = "d")
    public static final StdCalendarElement<Integer, EthiopianCalendar> DAY_OF_MONTH =
        new StdIntegerDateElement<EthiopianCalendar>("DAY_OF_MONTH", EthiopianCalendar.class, 1, 30, 'd');

    /**
     * <p>Represents the Ethiopian day of year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den &auml;thiopischen Tag des Jahres. </p>
     */
    @FormattableElement(format = "D")
    public static final StdCalendarElement<Integer, EthiopianCalendar> DAY_OF_YEAR =
        new StdIntegerDateElement<EthiopianCalendar>("DAY_OF_YEAR", EthiopianCalendar.class, 1, 365, 'D');

    /**
     * <p>Represents the Ethiopian day of week. </p>
     *
     * <p>If the day-of-week is set to a new value then Time4J handles the Ethiopian calendar week
     * as starting on Sunday (deviation from Coptic calendar). </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den &auml;thiopischen Tag der Woche. </p>
     *
     * <p>Wenn der Tag der Woche auf einen neuen Wert gesetzt wird, behandelt Time4J die &auml;thiopische
     * Kalenderwoche so, da&szlig; sie am Sonntag beginnt (Abweichung vom koptischen Kalender). </p>
     */
    @FormattableElement(format = "E")
    public static final StdCalendarElement<Weekday, EthiopianCalendar> DAY_OF_WEEK =
        new StdWeekdayElement<EthiopianCalendar>(EthiopianCalendar.class);

    private static final MonthBasedCalendarSystem<EthiopianCalendar> CALSYS;
    private static final TimeAxis<EthiopianCalendar.Unit, EthiopianCalendar> ENGINE;

    static {
        CALSYS = new Transformer();

        TimeAxis.Builder<EthiopianCalendar.Unit, EthiopianCalendar> builder =
            TimeAxis.Builder.setUp(
                EthiopianCalendar.Unit.class,
                EthiopianCalendar.class,
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
            .appendUnit(
                Unit.YEARS,
                new EthiopianUnitRule(Unit.YEARS),
                Unit.YEARS.getLength(),
                Collections.singleton(Unit.MONTHS))
            .appendUnit(
                Unit.MONTHS,
                new EthiopianUnitRule(Unit.MONTHS),
                Unit.MONTHS.getLength(),
                Collections.singleton(Unit.YEARS))
            .appendUnit(
                Unit.WEEKS,
                new EthiopianUnitRule(Unit.WEEKS),
                Unit.WEEKS.getLength(),
                Collections.singleton(Unit.DAYS))
            .appendUnit(
                Unit.DAYS,
                new EthiopianUnitRule(Unit.DAYS),
                Unit.DAYS.getLength(),
                Collections.singleton(Unit.WEEKS));
        ENGINE = builder.build();
    }

    private static final long serialVersionUID = -1632000525062084751L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final int mihret; // year expressed in era amete mihret
    private transient final int emonth;
    private transient final int edom;

    //~ Konstruktoren -----------------------------------------------------

    private EthiopianCalendar(
        int mihret,
        int emonth,
        int edom
    ) {
        super();

        this.mihret = mihret;
        this.emonth = emonth;
        this.edom = edom;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new instance of an Ethiopian calendar date. </p>
     *
     * @param   era         Ethiopian era
     * @param   yearOfEra   Ethiopian year of era in the range 1-9999 (1-15499 if amete alem)
     * @param   month       Ethiopian month
     * @param   dayOfMonth  Ethiopian day of month
     * @return  new instance of {@code EthiopianCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Erzeugt ein neues &auml;thiopisches Kalenderdatum. </p>
     *
     * @param   era         Ethiopian era
     * @param   yearOfEra   Ethiopian year of era in the range 1-9999 (1-15499 if amete alem)
     * @param   month       Ethiopian month
     * @param   dayOfMonth  Ethiopian day of month
     * @return  new instance of {@code EthiopianCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.11/4.8
     */
    public static EthiopianCalendar of(
        EthiopianEra era,
        int yearOfEra,
        EthiopianMonth month,
        int dayOfMonth
    ) {

        return EthiopianCalendar.of(era, yearOfEra, month.getValue(), dayOfMonth);

    }

    /**
     * <p>Creates a new instance of an Ethiopian calendar date. </p>
     *
     * @param   era         Ethiopian era
     * @param   yearOfEra   Ethiopian year of era in the range 1-9999 (1-15499 if amete alem)
     * @param   month       Ethiopian month
     * @param   dayOfMonth  Ethiopian day of month
     * @return  new instance of {@code EthiopianCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Erzeugt ein neues &auml;thiopisches Kalenderdatum. </p>
     *
     * @param   era         Ethiopian era
     * @param   yearOfEra   Ethiopian year of era in the range 1-9999 (1-15499 if amete alem)
     * @param   month       Ethiopian month
     * @param   dayOfMonth  Ethiopian day of month
     * @return  new instance of {@code EthiopianCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.11/4.8
     */
    public static EthiopianCalendar of(
        EthiopianEra era,
        int yearOfEra,
        int month,
        int dayOfMonth
    ) {

        if (!CALSYS.isValid(era, yearOfEra, month, dayOfMonth)) {
            throw new IllegalArgumentException(
                "Invalid Ethiopian date: era="
                    + era + ", year=" + yearOfEra + ", month=" + month + ", day=" + dayOfMonth);
        }

        return new EthiopianCalendar(mihret(era, yearOfEra), month, dayOfMonth);

    }

    /**
     * <p>Yields the Ethiopian era. </p>
     *
     * @return  enum
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Liefert die &auml;thiopische &Auml;ra. </p>
     *
     * @return  enum
     * @since   3.11/4.8
     */
    public EthiopianEra getEra() {

        return ((this.mihret < 1) ? EthiopianEra.AMETE_ALEM : EthiopianEra.AMETE_MIHRET);

    }

    /**
     * <p>Yields the Ethiopian year. </p>
     *
     * @return  int
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Liefert das &auml;thiopische Jahr. </p>
     *
     * @return  int
     * @since   3.11/4.8
     */
    public int getYearOfEra() {

        return ((this.mihret < 1) ? this.mihret + DELTA_ALEM_MIHRET : this.mihret);

    }

    /**
     * <p>Yields the Ethiopian month. </p>
     *
     * @return  enum
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Liefert den &auml;thiopischen Monat. </p>
     *
     * @return  enum
     * @since   3.11/4.8
     */
    public EthiopianMonth getMonth() {

        return EthiopianMonth.valueOf(this.emonth);

    }

    /**
     * <p>Yields the Ethiopian day of month. </p>
     *
     * @return  int
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Liefert den &auml;thiopischen Tag des Monats. </p>
     *
     * @return  int
     * @since   3.11/4.8
     */
    public int getDayOfMonth() {

        return this.edom;

    }

    /**
     * <p>Determines the day of week. </p>
     *
     * <p>The Ethiopian calendar also uses a 7-day-week. </p>
     *
     * @return  Weekday
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Ermittelt den Wochentag. </p>
     *
     * <p>Der &auml;thiopische Kalendar verwendet ebenfalls eine 7-Tage-Woche. </p>
     *
     * @return  Weekday
     * @since   3.11/4.8
     */
    public Weekday getDayOfWeek() {

        long utcDays = CALSYS.transform(this);
        return Weekday.valueOf(MathUtils.floorModulo(utcDays + 5, 7) + 1);

    }

    /**
     * <p>Yields the Ethiopian day of year. </p>
     *
     * @return  int
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Liefert den &auml;thiopischen Tag des Jahres. </p>
     *
     * @return  int
     * @since   3.11/4.8
     */
    public int getDayOfYear() {

        return this.get(DAY_OF_YEAR).intValue();

    }

    /**
     * <p>Yields the length of current Ethiopian month in days. </p>
     *
     * @return  int
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge des aktuellen &auml;thiopischen Monats in Tagen. </p>
     *
     * @return  int
     * @since   3.11/4.8
     */
    public int lengthOfMonth() {

        return CALSYS.getLengthOfMonth(this.getEra(), this.getYearOfEra(), this.emonth);

    }

    /**
     * <p>Yields the length of current Ethiopian year in days. </p>
     *
     * @return  int
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge des aktuellen &auml;thiopischen Jahres in Tagen. </p>
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

        return ((this.getYearOfEra() % 4) == 3);

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof EthiopianCalendar) {
            EthiopianCalendar that = (EthiopianCalendar) obj;
            return (
                (this.edom == that.edom)
                && (this.emonth == that.emonth)
                && (this.mihret == that.mihret)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return (17 * this.edom + 31 * this.emonth + 37 * this.mihret);

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(32);
        sb.append(this.getEra());
        sb.append('-');
        String y = String.valueOf(this.getYearOfEra());
        for (int i = y.length(); i < 4; i++) {
            sb.append('0');
        }
        sb.append(y);
        sb.append('-');
        if (this.emonth < 10) {
            sb.append('0');
        }
        sb.append(this.emonth);
        sb.append('-');
        if (this.edom < 10) {
            sb.append('0');
        }
        sb.append(this.edom);
        return sb.toString();

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
    public static TimeAxis<Unit, EthiopianCalendar> axis() {

        return ENGINE;

    }

    @Override
    protected TimeAxis<Unit, EthiopianCalendar> getChronology() {

        return ENGINE;

    }

    @Override
    protected EthiopianCalendar getContext() {

        return this;

    }

    private static int mihret(
        CalendarEra era,
        int yearOfEra
    ) {

        return (EthiopianEra.AMETE_ALEM.equals(era) ? yearOfEra - DELTA_ALEM_MIHRET : yearOfEra);

    }

    /**
     * @serialData  Uses <a href="../../serialized-form.html#net.time4j.SPX">
     *              a dedicated serialization form</a> as proxy. The first byte contains
     *              the type-ID {@code 4}. Then the era ordinal is written as byte, the year-of-era
     *              is written as int, finally month and day-of-month written as bytes.
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.ETHIOPIAN);

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
     * <p>Defines come calendar units for the Ethiopian calendar. </p>
     *
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Definiert einige kalendarische Zeiteinheiten f&uuml;r den &auml;thiopischen Kalender. </p>
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
         * <p>Calculates the difference between given Ethiopian dates in this unit. </p>
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
            EthiopianCalendar start,
            EthiopianCalendar end
        ) {

            return (int) start.until(end, this); // safe

        }

    }

    private static class Transformer
        implements MonthBasedCalendarSystem<EthiopianCalendar> {

        //~ Methoden ------------------------------------------------------

        @Override
        public boolean isValid(
            CalendarEra era,
            int yearOfEra,
            int monthOfYear,
            int dayOfMonth
        ) {

            return (
                (era instanceof EthiopianEra)
                && (yearOfEra >= 1)
                && (yearOfEra <= (EthiopianEra.AMETE_ALEM.equals(era) ? 15499 : 9999))
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

            checkEra(era);

            if (
                (yearOfEra >= 1)
                && (yearOfEra <= (EthiopianEra.AMETE_ALEM.equals(era) ? 15499 : 9999))
                && (monthOfYear >= 1)
                && (monthOfYear <= 13)
            ) {
                if (monthOfYear <= 12) {
                    return 30;
                } else {
                    return ((yearOfEra % 4) == 3) ? 6 : 5;
                }
            }

            throw new IllegalArgumentException(
                "Out of bounds: era=" + era + ", year=" + yearOfEra + ", month=" + monthOfYear);

        }

        @Override
        public int getLengthOfYear(
            CalendarEra era,
            int yearOfEra
        ) {

            checkEra(era);

            if (
                (yearOfEra >= 1)
                && (yearOfEra <= (EthiopianEra.AMETE_ALEM.equals(era) ? 15499 : 9999))
            ) {
                return ((yearOfEra % 4) == 3) ? 366 : 365;
            }

            throw new IllegalArgumentException("Out of bounds: era=" + era + ", year=" + yearOfEra);

        }

        @Override
        public EthiopianCalendar transform(long utcDays) {

            int mihret =
                MathUtils.safeCast(
                    MathUtils.floorDivide(
                        MathUtils.safeAdd(
                            MathUtils.safeMultiply(
                                4,
                                MathUtils.safeSubtract(utcDays, MIHRET_EPOCH)),
                            1463),
                        1461));

            int startOfYear = MathUtils.safeCast(this.transform(new EthiopianCalendar(mihret, 1, 1)));
            int emonth = 1 + MathUtils.safeCast(MathUtils.floorDivide(utcDays - startOfYear, 30));
            int startOfMonth = MathUtils.safeCast(this.transform(new EthiopianCalendar(mihret, emonth, 1)));
            int edom = 1 + MathUtils.safeCast(MathUtils.safeSubtract(utcDays, startOfMonth));
            EthiopianEra era = EthiopianEra.AMETE_MIHRET;

            if (mihret < 1) {
                mihret += DELTA_ALEM_MIHRET;
                era = EthiopianEra.AMETE_ALEM;
            }

            return EthiopianCalendar.of(era, mihret, emonth, edom);

        }

        @Override
        public long transform(EthiopianCalendar date) {

            return (
                MIHRET_EPOCH - 1
                    + 365 * (date.mihret - 1) + MathUtils.floorDivide(date.mihret, 4)
                    + 30 * (date.emonth - 1) + date.edom);

        }

        @Override
        public long getMinimumSinceUTC() {

            EthiopianCalendar min = new EthiopianCalendar(1 - DELTA_ALEM_MIHRET, 1, 1);
            return this.transform(min);

        }

        @Override
        public long getMaximumSinceUTC() {

            EthiopianCalendar max = new EthiopianCalendar(9999, 13, 6);
            return this.transform(max);

        }

        @Override
        public List<CalendarEra> getEras() {


            CalendarEra era0 = EthiopianEra.AMETE_ALEM;
            CalendarEra era1 = EthiopianEra.AMETE_MIHRET;
            return Arrays.asList(era0, era1);

        }

        private static void checkEra(CalendarEra era) {

            if (!(era instanceof EthiopianEra)) {
                throw new IllegalArgumentException("Invalid era: " + era);
            }

        }

    }

    private static class IntegerRule
        implements ElementRule<EthiopianCalendar, Integer> {

        //~ Instanzvariablen ----------------------------------------------

        private final int index;

        //~ Konstruktoren -------------------------------------------------

        IntegerRule(int index) {
            super();

            this.index = index;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Integer getValue(EthiopianCalendar context) {

            switch (this.index) {
                case YEAR_INDEX:
                    return context.getYearOfEra();
                case DAY_OF_MONTH_INDEX:
                    return context.edom;
                case DAY_OF_YEAR_INDEX:
                    int doy = 0;
                    for (int m = 1; m < context.emonth; m++) {
                        doy += CALSYS.getLengthOfMonth(context.getEra(), context.getYearOfEra(), m);
                    }
                    return doy + context.edom;
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public Integer getMinimum(EthiopianCalendar context) {

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
        public Integer getMaximum(EthiopianCalendar context) {

            switch (this.index) {
                case YEAR_INDEX:
                    return ((context.getEra() == EthiopianEra.AMETE_ALEM) ? 15499 : 9999);
                case DAY_OF_MONTH_INDEX:
                    return CALSYS.getLengthOfMonth(EthiopianEra.AMETE_MIHRET, context.mihret, context.emonth);
                case DAY_OF_YEAR_INDEX:
                    return CALSYS.getLengthOfYear(EthiopianEra.AMETE_MIHRET, context.mihret);
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public boolean isValid(
            EthiopianCalendar context,
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
        public EthiopianCalendar withValue(
            EthiopianCalendar context,
            Integer value,
            boolean lenient
        ) {

            if (
                lenient && (
                    (this.index == DAY_OF_MONTH_INDEX)
                    || (this.index == DAY_OF_YEAR_INDEX)
                )
            ) {
                long delta = MathUtils.safeSubtract(value.longValue(), this.getValue(context).longValue());
                return context.plus(CalendarDays.of(delta));
            }

            if (!this.isValid(context, value)) {
                throw new IllegalArgumentException("Out of range: " + value);
            }

            switch (this.index) {
                case YEAR_INDEX:
                    EthiopianEra era = context.getEra();
                    int y = value.intValue();
                    int dmax = CALSYS.getLengthOfMonth(era, y, context.emonth);
                    int d = Math.min(context.edom, dmax);
                    return EthiopianCalendar.of(era, y, context.emonth, d);
                case DAY_OF_MONTH_INDEX:
                    return new EthiopianCalendar(context.mihret, context.emonth, value.intValue());
                case DAY_OF_YEAR_INDEX:
                    int delta = value.intValue() - this.getValue(context).intValue();
                    return context.plus(CalendarDays.of(delta));
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public ChronoElement<?> getChildAtFloor(EthiopianCalendar context) {

            if (this.index == YEAR_INDEX) {
                return MONTH_OF_YEAR;
            }

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(EthiopianCalendar context) {

            if (this.index == YEAR_INDEX) {
                return MONTH_OF_YEAR;
            }

            return null;

        }

    }

    private static class MonthRule
        implements ElementRule<EthiopianCalendar, EthiopianMonth> {

        //~ Methoden ------------------------------------------------------

        @Override
        public EthiopianMonth getValue(EthiopianCalendar context) {

            return context.getMonth();

        }

        @Override
        public EthiopianMonth getMinimum(EthiopianCalendar context) {

            return EthiopianMonth.MESKEREM;

        }

        @Override
        public EthiopianMonth getMaximum(EthiopianCalendar context) {

            return EthiopianMonth.PAGUMEN;

        }

        @Override
        public boolean isValid(
            EthiopianCalendar context,
            EthiopianMonth value
        ) {

            return (value != null);

        }

        @Override
        public EthiopianCalendar withValue(
            EthiopianCalendar context,
            EthiopianMonth value,
            boolean lenient
        ) {

            int m = value.getValue();
            int dmax = CALSYS.getLengthOfMonth(EthiopianEra.AMETE_MIHRET, context.mihret, m);
            int d = Math.min(context.edom, dmax);
            return new EthiopianCalendar(context.mihret, m, d);

        }

        @Override
        public ChronoElement<?> getChildAtFloor(EthiopianCalendar context) {

            return DAY_OF_MONTH;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(EthiopianCalendar context) {

            return DAY_OF_MONTH;

        }

    }

    private static class EraRule
        implements ElementRule<EthiopianCalendar, EthiopianEra> {

        //~ Methoden ------------------------------------------------------

        @Override
        public EthiopianEra getValue(EthiopianCalendar context) {

            return context.getEra();

        }

        @Override
        public EthiopianEra getMinimum(EthiopianCalendar context) {

            return EthiopianEra.AMETE_ALEM;

        }

        @Override
        public EthiopianEra getMaximum(EthiopianCalendar context) {

            return EthiopianEra.AMETE_MIHRET;

        }

        @Override
        public boolean isValid(
            EthiopianCalendar context,
            EthiopianEra value
        ) {

            return (value != null);

        }

        @Override
        public EthiopianCalendar withValue(
            EthiopianCalendar context,
            EthiopianEra value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing era value.");
            }

            return context;

        }

        @Override
        public ChronoElement<?> getChildAtFloor(EthiopianCalendar context) {

            return YEAR_OF_ERA;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(EthiopianCalendar context) {

            return YEAR_OF_ERA;

        }

    }

    private static class WeekdayRule
        implements ElementRule<EthiopianCalendar, Weekday> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Weekday getValue(EthiopianCalendar context) {

            return context.getDayOfWeek();

        }

        @Override
        public Weekday getMinimum(EthiopianCalendar context) {

            return Weekday.SUNDAY;

        }

        @Override
        public Weekday getMaximum(EthiopianCalendar context) {

            return Weekday.SATURDAY;

        }

        @Override
        public boolean isValid(
            EthiopianCalendar context,
            Weekday value
        ) {

            return (value != null);

        }

        @Override
        public EthiopianCalendar withValue(
            EthiopianCalendar context,
            Weekday value,
            boolean lenient
        ) {

            Weekmodel model = Weekmodel.of(Weekday.SUNDAY, 1);
            int oldValue = context.getDayOfWeek().getValue(model);
            int newValue = value.getValue(model);
            return context.plus(CalendarDays.of(newValue - oldValue));

        }

        @Override
        public ChronoElement<?> getChildAtFloor(EthiopianCalendar context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(EthiopianCalendar context) {

            return null;

        }

    }

    private static class Merger
        implements ChronoMerger<EthiopianCalendar> {

        //~ Methoden ------------------------------------------------------

        @Override
        public String getFormatPattern(
            DisplayStyle style,
            Locale locale
        ) {

            return GenericDatePatterns.get("ethiopic", style, locale);

        }

        @Override
        public EthiopianCalendar createFrom(
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

            StartOfDay startOfDay = attributes.get(Attributes.START_OF_DAY, StartOfDay.MORNING);
            return Moment.from(clock.currentTime()).toGeneralTimestamp(ENGINE, tzid, startOfDay).toDate();

        }

        @Override
        public EthiopianCalendar createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean preparsing
        ) {

            if (!entity.contains(YEAR_OF_ERA)) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Missing Ethiopian year.");
                return null;
            } else if (!entity.contains(ERA)) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Missing Ethiopian era.");
            }

            int year = entity.get(YEAR_OF_ERA).intValue();
            EthiopianEra era = entity.get(ERA);

            if (entity.contains(MONTH_OF_YEAR) && entity.contains(DAY_OF_MONTH)) {
                int month = entity.get(MONTH_OF_YEAR).getValue();
                int dom = entity.get(DAY_OF_MONTH).intValue();
                if (CALSYS.isValid(era, year, month, dom)) {
                    return EthiopianCalendar.of(era, year, month, dom);
                } else {
                    entity.with(ValidationElement.ERROR_MESSAGE, "Invalid Ethiopian date.");
                }
            } else if (entity.contains(DAY_OF_YEAR)) {
                int doy = entity.get(DAY_OF_YEAR).intValue();
                if (doy > 0) {
                    int month = 1;
                    int daycount = 0;
                    while (month <= 13) {
                        int len = CALSYS.getLengthOfMonth(era, year, month);
                        if (doy > daycount + len) {
                            month++;
                            daycount += len;
                        } else {
                            return EthiopianCalendar.of(era, year, month, doy - daycount);
                        }
                    }
                }
                entity.with(ValidationElement.ERROR_MESSAGE, "Invalid Ethiopian date.");
            }

            return null;

        }

        @Override
        public ChronoDisplay preformat(EthiopianCalendar context, AttributeQuery attributes) {

            return context;

        }

        @Override
        public Chronology<?> preparser() {

            return null;

        }

    }

    private static class EthiopianUnitRule
        implements UnitRule<EthiopianCalendar> {

        //~ Instanzvariablen ----------------------------------------------

        private final Unit unit;

        //~ Konstruktoren -------------------------------------------------

        EthiopianUnitRule(Unit unit) {
            super();

            this.unit = unit;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public EthiopianCalendar addTo(EthiopianCalendar date, long amount) {

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
                            date.edom,
                            CALSYS.getLengthOfMonth(EthiopianEra.AMETE_MIHRET, year, month));
                    return EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, year, month, dom);
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
        public long between(EthiopianCalendar start, EthiopianCalendar end) {

            int factor = 1;

            switch (this.unit) {
                case YEARS:
                    factor = 13;
                    // fall-through
                case MONTHS:
                    long delta = ymValue(end) - ymValue(start);
                    if (factor > 1) {
                        delta = delta / factor;
                    }
                    if ((delta > 0) && (end.edom < start.edom)) {
                        delta--;
                    } else if ((delta < 0) && (end.edom > start.edom)) {
                        delta++;
                    }
                    return delta;
                case WEEKS:
                    factor = 7;
                    // fall-through
                case DAYS:
                    long days = CALSYS.transform(end) - CALSYS.transform(start);
                    if (factor > 1) {
                        days = days / factor;
                    }
                    return days;
                default:
                    throw new UnsupportedOperationException(this.unit.name());
            }

        }

        private static int ymValue(EthiopianCalendar date) {

            return date.mihret * 13 + date.emonth - 1;

        }

    }

}
