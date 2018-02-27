/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (EastAsianCalendar.java) is part of project Time4J.
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
import net.time4j.PlainTime;
import net.time4j.Weekday;
import net.time4j.base.MathUtils;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.Calendrical;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ElementRule;
import net.time4j.engine.IntElementRule;
import net.time4j.engine.UnitRule;
import net.time4j.format.CalendarType;

import java.util.Locale;


/**
 * <p>Base class of all calendars which are derivates of the lunisolar rural calendar invented in China. </p>
 *
 * <p>The underlying calendrical algorithms are based on the astronomical formula of Jean Meeus and the book
 * &quot;Calendrical Calculations&quot; by Dershowitz/Reingold. The differences between the concrete subclasses
 * are mainly limited to the supported date range and the geographical meridian defining the timezone offset
 * for evaluating the astronomical data. </p>
 *
 * @author  Meno Hochschild
 * @since   3.40/4.35
 */
/*[deutsch]
 * <p>Basisklasse aller Kalender, die vom lunisolaren Bauernkalender in China abgeleitet sind. </p>
 *
 * <p>Die zugrundeliegenden kalendarischen Algorithmen fu&szlig;en auf den astronomischen Formeln von Jean Meeus
 * und dem Buch &quot;Calendrical Calculations&quot; von Dershowitz/Reingold. Die verschiedenen konkreten
 * Kalenderklassen unterscheiden sich haupts&auml;chlich durch unterschiedliche G&uuml;ltigkeitsspannen
 * und den geographischen Meridian, auf den sich die astronomischen Berechnungen beziehen. </p>
 *
 * @author  Meno Hochschild
 * @since   3.40/4.35
 */
public abstract class EastAsianCalendar<U, D extends EastAsianCalendar<U, D>>
    extends Calendrical<U, D> {

    //~ Statische Felder/Initialisierungen ------------------------------------

    static final int DAY_OF_MONTH_INDEX = 0;
    static final int DAY_OF_YEAR_INDEX = 1;
    static final int MONTH_AS_ORDINAL_INDEX = 2;
    static final int CYCLE_INDEX = 3;

    static final int UNIT_CYCLES = 0;
    static final int UNIT_YEARS = 1;
    static final int UNIT_MONTHS = 2;
    static final int UNIT_WEEKS = 3;
    static final int UNIT_DAYS = 4;

    //~ Instanzvariablen ------------------------------------------------------

    private transient final int cycle;
    private transient final int yearOfCycle;
    private transient final EastAsianMonth month;
    private transient final int dayOfMonth;

    // redundant for performance reasons
    private transient final long utcDays;
    private transient final int leapMonth;

    //~ Konstruktoren ---------------------------------------------------------

    // for subclasses only
    EastAsianCalendar(
        int cycle,
        int yearOfCycle,
        EastAsianMonth month,
        int dayOfMonth,
        long utcDays
    ) {
        super();

        this.cycle = cycle;
        this.yearOfCycle = yearOfCycle;
        this.month = month;
        this.dayOfMonth = dayOfMonth;

        this.utcDays = utcDays;
        this.leapMonth = this.getCalendarSystem().getLeapMonth(cycle, yearOfCycle);

    }

    //~ Methoden --------------------------------------------------------------

    /**
     * <p>Obtains the cyclic year (using a sexagesimal cycle). </p>
     *
     * @return  CyclicYear
     */
    /*[deutsch]
     * <p>Liefert das zyklische Jahr (unter Verwendung eines 60-Jahre-Zyklus). </p>
     *
     * @return  CyclicYear
     */
    public CyclicYear getYear() {

        return CyclicYear.of(this.yearOfCycle);

    }

    /**
     * <p>Obtains the solar term as one of 24 on the ecliptic of the sun. </p>
     *
     * @return  SolarTerm
     */
    /*[deutsch]
     * <p>Liefert den Sonnenmonat (als eine von 24 Stationen auf der Ekliptik der Sonne). </p>
     *
     * @return  SolarTerm
     */
    public SolarTerm getSolarTerm() {

        return EastAsianST.<D>getInstance().getValue(this.getContext());

    }

    /**
     * <p>Yields the (lunar) month including the numerical value and the possible leap month flag. </p>
     *
     * @return  EastAsianMonth
     */
    /*[deutsch]
     * <p>Liefert den (Mond-)Monat einschlie&szlig;lich seiner Nummer
     * und eines m&ouml;glichen Schaltmonatskennzeichens. </p>
     *
     * @return  EastAsianMonth
     */
    public EastAsianMonth getMonth() {

        return this.month;

    }

    /**
     * <p>Yields the day of month in the range {@code 1-29/30}. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert den Tag des Monats im Bereich {@code 1-29/30}. </p>
     *
     * @return  int
     */
    public int getDayOfMonth() {

        return this.dayOfMonth;

    }

    /**
     * <p>Determines the day of week. </p>
     *
     * @return  Weekday
     */
    /*[deutsch]
     * <p>Ermittelt den Wochentag. </p>
     *
     * @return  Weekday
     */
    public Weekday getDayOfWeek() {

        return Weekday.valueOf(MathUtils.floorModulo(this.utcDays + 5, 7) + 1);

    }

    /**
     * <p>Yields the day of year. </p>
     *
     * @return  int {@code >= 1}
     */
    /*[deutsch]
     * <p>Liefert den Tag des Jahres. </p>
     *
     * @return  int {@code >= 1}
     */
    public int getDayOfYear() {

        return (int) (this.utcDays - this.getCalendarSystem().newYear(this.cycle, this.yearOfCycle) + 1);

    }

    /**
     * <p>Is the year of this date a leap year such that it contains a leap month? </p>
     *
     * @return  boolean
     */
    /*[deutsch]
     * <p>Liegt dieses Datum in einem Schaltjahr, das per Definition also einen Schaltmonat enth&auml;lt? </p>
     *
     * @return  boolean
     */
    public boolean isLeapYear() {

        return (this.leapMonth > 0);

    }

    /**
     * <p>Yields the length of current month in days. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge des aktuellen Monats in Tagen. </p>
     *
     * @return  int
     */
    public int lengthOfMonth() {

        long nextNewMoon = this.getCalendarSystem().newMoonOnOrAfter(this.utcDays + 1);
        return (int) (this.dayOfMonth + nextNewMoon - this.utcDays - 1);

    }

    /**
     * <p>Yields the length of current year in days. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge des aktuellen Jahres in Tagen. </p>
     *
     * @return  int
     */
    public int lengthOfYear() {

        int c = this.cycle;
        int y = this.yearOfCycle;
        y++;

        if (y > 60) {
            y = 1;
            c++;
        }

        long ny1 = this.getCalendarSystem().newYear(this.cycle, this.yearOfCycle);
        long ny2 = this.getCalendarSystem().newYear(c, y);
        return (int) (ny2 - ny1);

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
    public GeneralTimestamp<D> at(PlainTime time) {

        return GeneralTimestamp.of(this.getContext(), time);

    }

    /**
     * <p>Is equivalent to {@code at(PlainTime.of(hour, minute))}. </p>
     *
     * @param   hour        hour of day in range (0-24)
     * @param   minute      minute of hour in range (0-59)
     * @return  general timestamp as composition of this date and given time
     * @throws  IllegalArgumentException if any argument is out of range
     * @see     #at(PlainTime)
     */
    /*[deutsch]
     * <p>Entspricht {@code at(PlainTime.of(hour, minute))}. </p>
     *
     * @param   hour        hour of day in range (0-24)
     * @param   minute      minute of hour in range (0-59)
     * @return  general timestamp as composition of this date and given time
     * @throws  IllegalArgumentException if any argument is out of range
     * @see     #at(PlainTime)
     */
    public GeneralTimestamp<D> atTime(
        int hour,
        int minute
    ) {

        return this.at(PlainTime.of(hour, minute));

    }

    // overridden for performance reasons
    @Override
    public long getDaysSinceEpochUTC() {

        return this.utcDays;

    }

    // overridden for performance reasons
    @Override
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() == obj.getClass()) {
            EastAsianCalendar<?, ?> that = (EastAsianCalendar<?, ?>) obj;
            return (
                (this.cycle == that.cycle)
                && (this.yearOfCycle == that.yearOfCycle)
                && (this.dayOfMonth == that.dayOfMonth)
                && this.month.equals(that.month)
                && (this.utcDays == that.utcDays)
            );
        } else {
            return false;
        }

    }

    // overridden for performance reasons
    @Override
    public int hashCode() {

        long value = this.utcDays;
        return (int) (value ^ (value >>> 32));

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        String ct = this.getClass().getAnnotation(CalendarType.class).value();
        sb.append(ct.equals("dangi") ? "korean" : ct);
        sb.append('[');
        sb.append(this.getYear().getDisplayName(Locale.ROOT));
        sb.append('(');
        sb.append(this.getInt(CommonElements.RELATED_GREGORIAN_YEAR));
        sb.append(")-");
        sb.append(this.month.toString());
        sb.append('-');
        if (this.dayOfMonth < 10) {
            sb.append('0');
        }
        sb.append(this.dayOfMonth);
        sb.append(']');
        return sb.toString();

    }

    static <D extends EastAsianCalendar<?, D>>  ElementRule<D, Integer> getCycleRule(ChronoElement<?> c) {
        return new IntegerElementRule<D>(CYCLE_INDEX, c);
    }

    static <D extends EastAsianCalendar<?, D>> ElementRule<D, CyclicYear> getYearOfCycleRule(ChronoElement<?> c) {
        return new CyclicYearRule<D>(c, false);
    }

    static <D extends EastAsianCalendar<?, D>> ElementRule<D, CyclicYear> getVietYearOfCycleRule(ChronoElement<?> c) {
        return new CyclicYearRule<D>(c, true);
    }

    static <D extends EastAsianCalendar<?, D>>  ElementRule<D, EastAsianMonth> getMonthOfYearRule(ChronoElement<?> c) {
        return new MonthRule<D>(c);
    }

    static <D extends EastAsianCalendar<?, D>> ElementRule<D, Integer> getMonthAsOrdinalRule(ChronoElement<?> c) {
        return new IntegerElementRule<D>(MONTH_AS_ORDINAL_INDEX, c);
    }

    static <D extends EastAsianCalendar<?, D>> ElementRule<D, Integer> getDayOfMonthRule() {
        return new IntegerElementRule<D>(DAY_OF_MONTH_INDEX, null);
    }

    static <D extends EastAsianCalendar<?, D>> ElementRule<D, Integer> getDayOfYearRule() {
        return new IntegerElementRule<D>(DAY_OF_YEAR_INDEX, null);
    }

    static <D extends EastAsianCalendar<?, D>> UnitRule<D> getUnitRule(int index) {
        return new EastAsianUnitRule<D>(index);
    }

    // internal continous count of sexagesimal year cycles relative to gregorian year -2636
    int getCycle() {
        return this.cycle;
    }

    // is zero if no leap year
    int getLeapMonth() {
        return this.leapMonth;
    }

    // needs to be overridden due to calendar specific zone offsets and epochs
    abstract EastAsianCS<D> getCalendarSystem();

    //~ Innere Klassen ----------------------------------------------------

    private static class IntegerElementRule<D extends EastAsianCalendar<?, D>>
        implements IntElementRule<D> {

        //~ Instanzvariablen ----------------------------------------------

        private final ChronoElement<?> child;
        private final int index;

        //~ Konstruktoren -------------------------------------------------

        private IntegerElementRule(
            int index,
            ChronoElement<?> child
        ) {
            super();

            this.index = index;
            this.child = child;
        }

        //~ Methoden ------------------------------------------------------

        @Override
        public int getInt(D context) {
            switch (this.index) {
                case DAY_OF_MONTH_INDEX:
                    return context.getDayOfMonth();
                case DAY_OF_YEAR_INDEX:
                    return context.getDayOfYear();
                case MONTH_AS_ORDINAL_INDEX:
                    int num = context.getMonth().getNumber();
                    int lm = context.getLeapMonth();
                    if (((lm > 0) && (lm < num)) || context.getMonth().isLeap()) {
                        num++;
                    }
                    return num;
                case CYCLE_INDEX:
                    return context.getCycle();
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }
        }

        @Override
        public boolean isValid(
            D context,
            int value
        ) {
            if (value < 1) {
                return false;
            } else if (this.index == DAY_OF_MONTH_INDEX) {
                if (value > 30) {
                    return false;
                } else if (value == 30) {
                    return (context.lengthOfMonth() == 30);
                } else { // range 1-29
                    return true;
                }
            } else if (this.index == DAY_OF_YEAR_INDEX) {
                return (value <= context.lengthOfYear());
            } else if (this.index == MONTH_AS_ORDINAL_INDEX) {
                return (value <= 12) || ((value == 13) && (context.getLeapMonth() > 0));
            } else if (this.index == CYCLE_INDEX) {
                CalendarSystem<D> calsys = context.getCalendarSystem();
                int min = calsys.transform(calsys.getMinimumSinceUTC()).getCycle();
                int max = calsys.transform(calsys.getMaximumSinceUTC()).getCycle();
                return ((value >= min) && (value <= max));
            } else {
                throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }
        }

        @Override
        public D withValue(
            D context,
            int value,
            boolean lenient
        ) {
            switch (this.index) {
                case DAY_OF_MONTH_INDEX:
                    if (lenient) {
                        long utcDays = context.getDaysSinceEpochUTC() + value - context.getDayOfMonth();
                        return context.getCalendarSystem().transform(utcDays);
                    } else if ((value < 1) || (value > 30) || ((value == 30) && context.lengthOfMonth() < 30)) {
                        throw new IllegalArgumentException("Day of month out of range: " + value);
                    } else {
                        long utcDays = context.getDaysSinceEpochUTC() + value - context.getDayOfMonth();
                        return context.getCalendarSystem().create(
                            context.getCycle(), context.getYear().getNumber(), context.getMonth(), value, utcDays);
                    }
                case DAY_OF_YEAR_INDEX:
                    if (!lenient && ((value < 1) || (value > context.lengthOfYear()))) {
                        throw new IllegalArgumentException("Day of year out of range: " + value);
                    }
                    long utcDaysNew = context.getDaysSinceEpochUTC() + value - context.getDayOfYear();
                    return context.getCalendarSystem().transform(utcDaysNew);
                case MONTH_AS_ORDINAL_INDEX:
                    if (this.isValid(context, value)) {
                        int lm = context.getLeapMonth();
                        boolean leap = false;
                        if ((lm > 0) && (lm < value)) {
                            if (value == lm + 1) {
                                leap = true;
                            }
                            value--;
                        }
                        EastAsianMonth eam = EastAsianMonth.valueOf(value);
                        if (leap) {
                            eam = eam.withLeap();
                        }
                        return MonthRule.withMonth(context, eam);
                    } else {
                        throw new IllegalArgumentException("Ordinal month out of range: " + value);
                    }
                case CYCLE_INDEX:
                    if (this.isValid(context, value)) {
                        int delta = value - context.getCycle();
                        UnitRule<D> rule = EastAsianCalendar.getUnitRule(UNIT_CYCLES);
                        return rule.addTo(context, delta);
                    } else {
                        throw new IllegalArgumentException("Sexagesimal cycle out of range: " + value);
                    }
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }
        }

        @Override
        public Integer getValue(D context) {
            return Integer.valueOf(this.getInt(context));
        }

        @Override
        public Integer getMinimum(D context) {
            if (this.index == CYCLE_INDEX) {
                CalendarSystem<D> calsys = context.getCalendarSystem();
                return Integer.valueOf(calsys.transform(calsys.getMinimumSinceUTC()).getCycle());
            } else {
                return Integer.valueOf(1);
            }
        }

        @Override
        public Integer getMaximum(D context) {
            int max;
            switch (this.index) {
                case DAY_OF_MONTH_INDEX:
                    max = context.lengthOfMonth();
                    break;
                case DAY_OF_YEAR_INDEX:
                    max = context.lengthOfYear();
                    break;
                case MONTH_AS_ORDINAL_INDEX:
                    max = (context.isLeapYear() ? 13 : 12);
                    break;
                case CYCLE_INDEX:
                    CalendarSystem<D> calsys = context.getCalendarSystem();
                    max = calsys.transform(calsys.getMaximumSinceUTC()).getCycle();
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }
            return Integer.valueOf(max);
        }

        @Override
        public boolean isValid(
            D context,
            Integer value
        ) {
            return ((value != null) && this.isValid(context, value.intValue()));
        }

        @Override
        public D withValue(
            D context,
            Integer value,
            boolean lenient
        ) {
            if (value == null) {
                throw new IllegalArgumentException("Missing element value.");
            }
            return this.withValue(context, value.intValue(), lenient);
        }

        @Override
        public ChronoElement<?> getChildAtFloor(D context) {
            return this.child;
        }

        @Override
        public ChronoElement<?> getChildAtCeiling(D context) {
            return this.child;
        }

    }

    private static class CyclicYearRule<D extends EastAsianCalendar<?, D>>
        implements ElementRule<D, CyclicYear> {

        //~ Instanzvariablen ----------------------------------------------

        private final ChronoElement<?> child;
        private final boolean vietnam;

        //~ Konstruktoren -------------------------------------------------

        private CyclicYearRule(
            ChronoElement<?> child,
            boolean vietnam
        ) {
            super();

            this.child = child;
            this.vietnam = vietnam;
        }

        //~ Methoden ------------------------------------------------------

        @Override
        public CyclicYear getValue(D context) {
            return context.getYear();
        }

        @Override
        public CyclicYear getMinimum(D context) {
            if (this.vietnam) {
                return (context.getCycle() == 75) ? CyclicYear.of(10) : CyclicYear.of(1);
            } else {
                return (context.getCycle() == 72) ? CyclicYear.of(22) : CyclicYear.of(1);
            }
        }

        @Override
        public CyclicYear getMaximum(D context) {
            return (context.getCycle() == 94) ? CyclicYear.of(56) : CyclicYear.of(60);
        }

        @Override
        public boolean isValid(
            D context,
            CyclicYear value
        ) {
            CyclicYear min = this.getMinimum(context);
            CyclicYear max = this.getMaximum(context);
            return (value != null) && (min.compareTo(value) <= 0) && (max.compareTo(value) >= 0);
        }

        @Override
        public D withValue(
            D context,
            CyclicYear value,
            boolean lenient
        ) {
            if (this.isValid(context, value)) {
                EastAsianCS<D> cs = context.getCalendarSystem();
                int dom = context.getDayOfMonth();
                EastAsianMonth eam = context.getMonth();
                int y = value.getNumber();
                int cycle = context.getCycle();
                if (eam.isLeap() && (eam.getNumber() != cs.getLeapMonth(cycle, y))) {
                    eam = EastAsianMonth.valueOf(eam.getNumber());
                }
                if (dom <= 29) {
                    long utcDays = cs.transform(cycle, y, eam, dom);
                    return cs.create(cycle, y, eam, dom, utcDays);
                } else {
                    long utcDays = cs.transform(cycle, y, eam, 1);
                    D first = cs.transform(utcDays);
                    dom = Math.min(dom, first.lengthOfMonth());
                    return cs.create(cycle, y, eam, dom, utcDays + dom - 1);
                }
            } else {
                throw new IllegalArgumentException("Invalid cyclic year: " + value);
            }
        }

        @Override
        public ChronoElement<?> getChildAtFloor(D context) {
            return this.child;
        }

        @Override
        public ChronoElement<?> getChildAtCeiling(D context) {
            return this.child;
        }
    }

    private static class MonthRule<D extends EastAsianCalendar<?, D>>
        implements ElementRule<D, EastAsianMonth> {

        //~ Instanzvariablen ----------------------------------------------

        private final ChronoElement<?> child;

        //~ Konstruktoren -------------------------------------------------

        private MonthRule(ChronoElement<?> child) {
            super();

            this.child = child;
        }

        //~ Methoden ------------------------------------------------------

        @Override
        public EastAsianMonth getValue(D context) {
            return context.getMonth();
        }

        @Override
        public EastAsianMonth getMinimum(D context) {
            return EastAsianMonth.valueOf(1);
        }

        @Override
        public EastAsianMonth getMaximum(D context) {
            return EastAsianMonth.valueOf(12); // no leap month until gregorian year 3000
        }

        @Override
        public boolean isValid(
            D context,
            EastAsianMonth value
        ) {
            return ((value != null) && (!value.isLeap() || (value.getNumber() == context.getLeapMonth())));
        }

        @Override
        public D withValue(
            D context,
            EastAsianMonth value,
            boolean lenient
        ) {
            if (this.isValid(context, value)) {
                return withMonth(context, value);
            } else {
                throw new IllegalArgumentException("Invalid month: " + value);
            }
        }

        @Override
        public ChronoElement<?> getChildAtFloor(D context) {
            return this.child;
        }

        @Override
        public ChronoElement<?> getChildAtCeiling(D context) {
            return this.child;
        }

        static <D extends EastAsianCalendar<?, D>> D withMonth(
            D context,
            EastAsianMonth eam
        ) {
            EastAsianCS<D> cs = context.getCalendarSystem();
            int dom = context.getDayOfMonth();
            int y = context.getYear().getNumber();
            if (dom <= 29) {
                long utcDays = cs.transform(context.getCycle(), y, eam, dom);
                return cs.create(context.getCycle(), y, eam, dom, utcDays);
            } else {
                long utcDays = cs.transform(context.getCycle(), y, eam, 1);
                D first = cs.transform(utcDays);
                dom = Math.min(dom, first.lengthOfMonth());
                return cs.create(context.getCycle(), y, eam, dom, utcDays + dom - 1);
            }
        }

    }

    private static class EastAsianUnitRule<D extends EastAsianCalendar<?, D>>
        implements UnitRule<D> {

        //~ Instanzvariablen ----------------------------------------------

        private final int index;

        //~ Konstruktoren -------------------------------------------------

        EastAsianUnitRule(int index) {
            super();

            this.index = index;
        }

        //~ Methoden ------------------------------------------------------

        @Override
        public D addTo(D date, long amount) {

            EastAsianCS<D> cs = date.getCalendarSystem();
            int dom = date.getDayOfMonth();
            int c = date.getCycle();
            int y = date.getYear().getNumber();
            EastAsianMonth month = date.getMonth();

            switch (this.index) {
                case UNIT_CYCLES:
                    amount = MathUtils.safeMultiply(amount, 60);
                    // fall-through
                case UNIT_YEARS:
                    long years = MathUtils.safeAdd(c * 60 + y - 1, amount);
                    c = MathUtils.safeCast(MathUtils.floorDivide(years, 60));
                    y = MathUtils.floorModulo(years, 60) + 1;
                    if (month.isLeap() && (cs.getLeapMonth(c, y) != month.getNumber())) {
                        month = EastAsianMonth.valueOf(month.getNumber()); // no leap
                    }
                    return create(c, y, month, dom, cs);
                case UNIT_MONTHS:
                    checkAmountOfMonths(amount);
                    int delta = ((amount > 0) ? 1 : -1);
                    int num = month.getNumber();
                    boolean leap = month.isLeap();
                    int lm = cs.getLeapMonth(c, y);
                    while (amount != 0) {
                        if (leap) {
                            leap = false;
                            if (delta == 1) {
                                num++;
                            }
                        } else {
                            if ((delta == 1) && (lm == num)) {
                                leap = true;
                            } else if ((delta == -1) && (lm == num - 1)) {
                                leap = true;
                                num--;
                            } else {
                                num += delta;
                            }
                        }
                        if (!leap) {
                            if (num == 13) {
                                num = 1;
                                y++;
                                if (y == 61) {
                                    y = 1;
                                    c++;
                                }
                                lm = cs.getLeapMonth(c, y);
                            } else if (num == 0) {
                                num = 12; // no leap month until gregorian year 3000
                                y--;
                                if (y == 0) {
                                    y = 60;
                                    c--;
                                }
                                lm = cs.getLeapMonth(c, y);
                            }
                        }
                        amount -= delta;
                    }
                    month = EastAsianMonth.valueOf(num);
                    if (leap) {
                        month = month.withLeap();
                    }
                    return create(c, y, month, dom, cs);
                case UNIT_WEEKS:
                    amount = MathUtils.safeMultiply(amount, 7);
                    // fall-through
                case UNIT_DAYS:
                    long utcDays = MathUtils.safeAdd(date.getDaysSinceEpochUTC(), amount);
                    return cs.transform(utcDays);
                default:
                    throw new UnsupportedOperationException();
            }

        }

        @Override
        public long between(D start, D end) {

            return between(start, end, this.index);

        }

        private static <D extends EastAsianCalendar<?, D>> long between(
            D start,
            D end,
            int index
        ) {

            EastAsianCS<D> calsys = start.getCalendarSystem();

            switch (index) {
                case UNIT_CYCLES:
                    return between(start, end, UNIT_YEARS) / 60;
                case UNIT_YEARS:
                    int deltaY =
                        end.getCycle() * 60 + end.getYear().getNumber()
                            - start.getCycle() * 60 - start.getYear().getNumber();
                    if (deltaY > 0) {
                        int mComp = start.getMonth().compareTo(end.getMonth());
                        if ((mComp > 0) || ((mComp == 0) && (start.getDayOfMonth() > end.getDayOfMonth()))) {
                            deltaY--;
                        }
                    } else if (deltaY < 0) {
                        int mComp = start.getMonth().compareTo(end.getMonth());
                        if ((mComp < 0) || ((mComp == 0) && (start.getDayOfMonth() < end.getDayOfMonth()))) {
                            deltaY++;
                        }
                    }
                    return deltaY;
                case UNIT_MONTHS:
                    D s = start;
                    D e = end;
                    boolean negative = false;
                    if (start.isAfter(end)) {
                        s = end;
                        e = start;
                        negative = true;
                    }
                    int c = s.getCycle();
                    int y = s.getYear().getNumber();
                    EastAsianMonth month = s.getMonth();
                    int num = month.getNumber();
                    boolean leap = month.isLeap();
                    int lm = calsys.getLeapMonth(c, y);
                    int amount = 0;
                    while ((c != e.getCycle()) || (y != e.getYear().getNumber()) || !month.equals(e.getMonth())) {
                        if (leap) {
                            leap = false;
                            num++;
                        } else {
                            if (lm == num) {
                                leap = true;
                            } else {
                                num++;
                            }
                        }
                        if (!leap) {
                            if (num == 13) {
                                num = 1;
                                y++;
                                if (y == 61) {
                                    y = 1;
                                    c++;
                                }
                                lm = calsys.getLeapMonth(c, y);
                            } else if (num == 0) {
                                num = 12; // no leap month until gregorian year 3000
                                y--;
                                if (y == 0) {
                                    y = 60;
                                    c--;
                                }
                                lm = calsys.getLeapMonth(c, y);
                            }
                        }
                        month = EastAsianMonth.valueOf(num);
                        if (leap) {
                            month = month.withLeap();
                        }
                        amount++;
                    }
                    if ((amount > 0) && (s.getDayOfMonth() > e.getDayOfMonth())) {
                        amount--;
                    }
                    return (negative ? -amount : amount);
                case UNIT_WEEKS:
                    return (end.getDaysSinceEpochUTC() - start.getDaysSinceEpochUTC()) / 7;
                case UNIT_DAYS:
                    return end.getDaysSinceEpochUTC() - start.getDaysSinceEpochUTC();
                default:
                    throw new UnsupportedOperationException();
            }

        }

        private static <D extends EastAsianCalendar<?, D>> D create(
            int cycle,
            int yearOfCycle,
            EastAsianMonth month,
            int dom,
            EastAsianCS<D> calsys
        ) {
            if (dom <= 29) {
                long utcDays = calsys.transform(cycle, yearOfCycle, month, dom);
                return calsys.create(cycle, yearOfCycle, month, dom, utcDays);
            } else {
                long utcDays = calsys.transform(cycle, yearOfCycle, month, 1);
                D first = calsys.transform(utcDays);
                dom = Math.min(dom, first.lengthOfMonth());
                return calsys.create(cycle, yearOfCycle, month, dom, utcDays + dom - 1);
            }
        }

        private static void checkAmountOfMonths(long amount) {
            if ((amount > 1200) || (amount < -1200)) {
                throw new ArithmeticException("Month arithmetic limited to delta not greater than 1200.");
            }
        }

    }

}
