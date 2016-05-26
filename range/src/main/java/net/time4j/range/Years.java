/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Years.java) is part of project Time4J.
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

package net.time4j.range;

import net.time4j.CalendarUnit;
import net.time4j.Duration;
import net.time4j.IsoDateUnit;
import net.time4j.base.MathUtils;
import net.time4j.engine.TimePoint;
import net.time4j.engine.TimeSpan;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;


/**
 * <p>Represents a time span in gregorian or week-based years. </p>
 *
 * @param   <U> generic type of year units
 * @author  Meno Hochschild
 * @since   3.21/4.17
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Zeitspanne in gregorianischen oder wochenbasierten Jahren. </p>
 *
 * @param   <U> generic type of year units
 * @author  Meno Hochschild
 * @since   3.21/4.17
 */
public final class Years<U extends IsoDateUnit>
    implements TimeSpan<U>, Comparable<Years<U>>, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Constant for zero gregorian years. </p>
     */
    /*[deutsch]
     * <p>Konstante f&uuml;r null gregorianische Jahre. </p>
     */
    public static final Years<CalendarUnit> ZERO = new Years<CalendarUnit>(0, CalendarUnit.YEARS);

    /**
     * <p>Constant for exactly one gregorian year. </p>
     */
    /*[deutsch]
     * <p>Konstante f&uuml;r genau ein gregorianisches Jahr. </p>
     */
    public static final Years<CalendarUnit> ONE = new Years<CalendarUnit>(1, CalendarUnit.YEARS);

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serialField     count of units
     */
    /*[deutsch]
     * @serialField     Anzahl der Zeiteinheiten
     */
    private final int amount;

    /**
     * @serialField     year-type
     */
    /*[deutsch]
     * @serialField     Jahrestyp
     */
    private final U unit;

    //~ Konstruktoren -----------------------------------------------------

    private Years(
        int amount,
        U unit
    ) {
        super();

        if (unit.equals(CalendarUnit.YEARS) || unit.equals(CalendarUnit.weekBasedYears())) {
            this.amount = amount;
            this.unit = unit;
        } else {
            throw new IllegalArgumentException("Invalid year unit: " + unit);
        }

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Obtains a time span in given gregorian years. </p>
     *
     * @param   years       count of gregorian years, maybe negative
     * @return  time span in years
     * @see     CalendarUnit#YEARS
     */
    /*[deutsch]
     * <p>Erh&auml;lt eine Zeitspanne in den angegebenen gregorianischen Jahren. </p>
     *
     * @param   years       count of gregorian years, maybe negative
     * @return  time span in years
     * @see     CalendarUnit#YEARS
     */
    public static Years<CalendarUnit> ofGregorian(int years) {

        return ((years == 0) ? ZERO : (years == 1) ? ONE : new Years<CalendarUnit>(years, CalendarUnit.YEARS));

    }

    /**
     * <p>Obtains a time span in given week-based years. </p>
     *
     * <p>Week-based years have a length of either 364 or 371 days. </p>
     *
     * @param   years       count of week-based years, maybe negative
     * @return  time span in years
     * @see     CalendarUnit#weekBasedYears()
     */
    /*[deutsch]
     * <p>Erh&auml;lt eine Zeitspanne in den angegebenen wochenbasierten Jahren. </p>
     *
     * <p>Wochenbasierte Jahre haben eine L&auml;nge von entweder 364 oder 371 Tagen. </p>
     *
     * @param   years       count of week-based years, maybe negative
     * @return  time span in years
     * @see     CalendarUnit#weekBasedYears()
     */
    public static Years<IsoDateUnit> ofWeekBased(int years) {

        return new Years<IsoDateUnit>(years, CalendarUnit.weekBasedYears());

    }

    /**
     * <p>Yields the years as integer. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert die Jahre als Integer. </p>
     *
     * @return  int
     */
    public int getAmount() {

        return this.amount;

    }

    /**
     * <p>Yields the associated unit. </p>
     *
     * @return  year unit
     * @see     CalendarUnit#YEARS
     * @see     CalendarUnit#weekBasedYears()
     */
    /*[deutsch]
     * <p>Liefert die assoziierte Jahreseinheit. </p>
     *
     * @return  year unit
     * @see     CalendarUnit#YEARS
     * @see     CalendarUnit#weekBasedYears()
     */
    public U getUnit() {

        return this.unit;

    }

    @Override
    public int compareTo(Years<U> other) {

        if (this.unit.equals(other.unit)) {
            return ((this.amount < other.amount) ? -1 : (this.amount == other.amount) ? 0 : 1);
        } else {
            throw new ClassCastException("Years with different units are not comparable.");
        }

    }

    @Override
    public List<Item<U>> getTotalLength() {

        if (this.isEmpty()) {
            return Collections.emptyList();
        }

        long value = this.amount; // prevents anomaly for Integer.MIN_VALUE
        Item<U> item = Item.of(Math.abs(value), this.unit);
        return Collections.singletonList(item);

    }

    @Override
    public boolean contains(IsoDateUnit unit) {

        return (this.unit.equals(unit) && (this.amount != 0));

    }

    @Override
    public long getPartialAmount(IsoDateUnit unit) {

        return (this.unit.equals(unit) ? this.amount : 0);

    }

    @Override
    public boolean isNegative() {

        return (this.amount < 0);

    }

    @Override
    public boolean isPositive() {

        return (this.amount > 0);

    }

    @Override
    public boolean isEmpty() {

        return (this.amount == 0);

    }

    @Override
    public <T extends TimePoint<? super U, T>> T addTo(T time) {

        return time.plus(this.amount, this.unit);

    }

    @Override
    public <T extends TimePoint<? super U, T>> T subtractFrom(T time) {

        return time.minus(this.amount, this.unit);

    }

    /**
     * <p>Determines the temporal distance between given dates/time-points in gregorian years. </p>
     *
     * @param   <T> generic type of time-points
     * @param   t1      first time-point
     * @param   t2      second time-point
     * @return  result of year difference
     * @see     net.time4j.PlainDate
     * @see     net.time4j.PlainTimestamp
     */
    /*[deutsch]
     * <p>Bestimmt die gregorianische Jahresdifferenz zwischen den angegebenen Zeitpunkten. </p>
     *
     * @param   <T> generic type of time-points
     * @param   t1      first time-point
     * @param   t2      second time-point
     * @return  result of year difference
     * @see     net.time4j.PlainDate
     * @see     net.time4j.PlainTimestamp
     */
    public static <T extends TimePoint<? super CalendarUnit, T>> Years<CalendarUnit> between(T t1, T t2) {

        long delta = CalendarUnit.YEARS.between(t1, t2);
        return Years.ofGregorian(MathUtils.safeCast(delta));

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof Years) {
            Years<?> that = Years.class.cast(obj);
            return ((this.amount == that.amount) && this.unit.equals(that.unit));
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return (this.amount ^ this.unit.hashCode());

    }

    /**
     * <p>Prints in ISO-8601-format &quot;PnY&quot;. </p>
     *
     * <p>Negative years will get a preceding sign. Note that gregorian and week-based years have
     * the same representation. </p>
     *
     * @return  the number of years in ISO-8601-format
     */
    /*[deutsch]
     * <p>Liefert einen ISO-8601-kompatiblen String im Format &quot;PnY&quot;. </p>
     *
     * <p>Negative Jahre bekommen ein Minuszeichen vorangestellt. Zu beachten: Gregorianische und
     * wochenbasierte Jahre haben die gleiche Repr&auml;sentation. </p>
     *
     * @return  the number of years in ISO-8601-format
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        if (this.amount < 0) {
            sb.append('-');
        }
        sb.append('P');
        long value = this.amount; // prevents anomaly for Integer.MIN_VALUE
        sb.append(Math.abs(value));
        sb.append('Y');
        return sb.toString();

    }

    /**
     * <p>Parses the canonical ISO-8601-format &quot;PnY&quot; with possible preceding minus-char. </p>
     *
     * @param   period      the formatted string to be parsed
     * @return  parsed instance
     * @throws  ParseException if given argument cannot be parsed
     */
    /*[deutsch]
     * <p>Interpretiert das kanonische ISO-8601-Format &quot;PnY&quot; mit optionalem vorangehenden Minus-Zeichen. </p>
     *
     * @param   period      the formatted string to be parsed
     * @return  parsed instance
     * @throws  ParseException if given argument cannot be parsed
     */
    public static Years<CalendarUnit> parseGregorian(String period) throws ParseException {

        int amount = parsePeriod(period);
        return Years.ofGregorian(amount);

    }

    /**
     * <p>Like {@code parseGregorian(period)} but interpretes years as week-based. </p>
     *
     * @param   period      the formatted string to be parsed
     * @return  parsed instance
     * @throws  ParseException if given argument cannot be parsed
     * @see     #parseGregorian(String)
     * @see     CalendarUnit#weekBasedYears()
     */
    /*[deutsch]
     * <p>Wie {@code parseGregorian(period)}, aber mit wochenbasierten Jahren. </p>
     *
     * @param   period      the formatted string to be parsed
     * @return  parsed instance
     * @throws  ParseException if given argument cannot be parsed
     * @see     #parseGregorian(String)
     * @see     CalendarUnit#weekBasedYears()
     */
    public static Years<IsoDateUnit> parseWeekBased(String period) throws ParseException {

        int amount = parsePeriod(period);
        return Years.ofWeekBased(amount);

    }

    /**
     * <p>Yields a copy with the absolute amount. </p>
     *
     * @return  immutable copy with the absolute amount
     * @throws  ArithmeticException if numeric overflow occurs (only in case of {@code Integer.MIN_VALUE})
     */
    /*[deutsch]
     * <p>Liefert eine Kopie mit dem absoluten Betrag. </p>
     *
     * @return  immutable copy with the absolute amount
     * @throws  ArithmeticException if numeric overflow occurs (only in case of {@code Integer.MIN_VALUE})
     */
    public Years<U> abs() {

        long value = this.amount;
        return new Years<U>(MathUtils.safeCast(Math.abs(value)), this.unit);

    }

    /**
     * <p>Yields a copy with the negated amount. </p>
     *
     * @return  immutable copy with the reverse sign
     * @throws  ArithmeticException if numeric overflow occurs (only in case of {@code Integer.MIN_VALUE})
     */
    /*[deutsch]
     * <p>Liefert eine Kopie mit dem negierten Betrag. </p>
     *
     * @return  immutable copy with the reverse sign
     * @throws  ArithmeticException if numeric overflow occurs (only in case of {@code Integer.MIN_VALUE})
     */
    public Years<U> inverse() {

        return new Years<U>(MathUtils.safeNegate(this.amount), this.unit);

    }

    /**
     * <p>Yields a copy with the added amount. </p>
     *
     * @param   amount      the amount to be added
     * @return  result of addition as immutable copy
     * @throws  ArithmeticException if numeric overflow occurs
     */
    /*[deutsch]
     * <p>Liefert eine Kopie mit dem addierten Betrag. </p>
     *
     * @param   amount      the amount to be added
     * @return  result of addition as immutable copy
     * @throws  ArithmeticException if numeric overflow occurs
     */
    public Years<U> plus(int amount) {

        if (amount == 0) {
            return this;
        }

        long value = this.amount;
        return new Years<U>(MathUtils.safeCast(value + amount), this.unit);

    }

    /**
     * <p>Yields a copy with the added amount. </p>
     *
     * @param   years       the amount to be added
     * @return  result of addition as immutable copy
     * @throws  ArithmeticException if numeric overflow occurs
     */
    /*[deutsch]
     * <p>Liefert eine Kopie mit dem addierten Betrag. </p>
     *
     * @param   years       the amount to be added
     * @return  result of addition as immutable copy
     * @throws  ArithmeticException if numeric overflow occurs
     */
    public Years<U> plus(Years<U> years) {

        if (years.isEmpty()) {
            return this;
        }

        long value = this.amount;
        return new Years<U>(MathUtils.safeCast(value + years.amount), this.unit);

    }

    /**
     * <p>Yields a copy with the subtracted amount. </p>
     *
     * @param   amount      the amount to be subtracted
     * @return  result of subtraction as immutable copy
     * @throws  ArithmeticException if numeric overflow occurs
     */
    /*[deutsch]
     * <p>Liefert eine Kopie mit dem subtrahierten Betrag. </p>
     *
     * @param   amount      the amount to be subtracted
     * @return  result of subtraction as immutable copy
     * @throws  ArithmeticException if numeric overflow occurs
     */
    public Years<U> minus(int amount) {

        if (amount == 0) {
            return this;
        }

        long value = this.amount;
        return new Years<U>(MathUtils.safeCast(value - amount), this.unit);

    }

    /**
     * <p>Yields a copy with the subtracted amount. </p>
     *
     * @param   years       the amount to be subtracted
     * @return  result of subtraction as immutable copy
     * @throws  ArithmeticException if numeric overflow occurs
     */
    /*[deutsch]
     * <p>Liefert eine Kopie mit dem subtrahierten Betrag. </p>
     *
     * @param   years       the amount to be subtracted
     * @return  result of subtraction as immutable copy
     * @throws  ArithmeticException if numeric overflow occurs
     */
    public Years<U> minus(Years<U> years) {

        if (years.isEmpty()) {
            return this;
        }

        long value = this.amount;
        return new Years<U>(MathUtils.safeCast(value - years.amount), this.unit);

    }

    /**
     * <p>Yields a copy with the multiplied amount. </p>
     *
     * @param   factor      multiplication factor to be applied
     * @return  immutable copy with the multiplied amount
     * @throws  ArithmeticException if numeric overflow occurs
     */
    /*[deutsch]
     * <p>Liefert eine Kopie mit dem multiplizierten Betrag. </p>
     *
     * @param   factor      multiplication factor to be applied
     * @return  immutable copy with the multiplied amount
     * @throws  ArithmeticException if numeric overflow occurs
     */
    public Years<U> multipliedBy(int factor) {

        switch (factor) {
            case -1:
                return this.inverse();
            case 1:
                return this;
            default:
                return new Years<U>(MathUtils.safeMultiply(this.amount, factor), this.unit);
        }

    }

    /**
     * <p>Converts this instance to a general duration with the same amount and unit. </p>
     *
     * @return  Duration
     */
    /*[deutsch]
     * <p>Konvertiert diese Instanz zu einer allgemeinen Dauer mit demselben Betrag und derselben Einheit. </p>
     *
     * @return  Duration
     */
    public Duration<U> toDuration() {

        return Duration.of(this.amount, this.unit);

    }

    private static int parsePeriod(String period) throws ParseException {

        if (period.isEmpty()) {
            throw new ParseException("Empty period.", 0);
        }

        boolean negative = false;
        int index = 0;
        int len = period.length();

        if (period.charAt(0) == '-') {
            index++;
            negative = true;
        }

        if ((index < len) && (period.charAt(index) != 'P')) {
            throw new ParseException("Missing P-literal: " + period, index);
        }

        index++;
        long total = 0;
        int old = index;

        for (int i = index, n = Math.min(len, 10); i < n; i++) {
            char c = period.charAt(i);
            if ((c >= '0') && (c <= '9')) {
                int digit = (c - '0');
                total = total * 10 + digit;
                index++;
            } else {
                break;
            }
        }

        if (index == old) {
            throw new ParseException("Missing digits: " + period, index);
        }

        if ((len == index + 1) && (period.charAt(index) == 'Y')) {
            index++; // consume Y
            try {
                if (negative) {
                    total = MathUtils.safeNegate(total);
                }
                return MathUtils.safeCast(total);
            } catch (ArithmeticException ae) {
                throw new ParseException(ae.getMessage(), index);
            }
        }

        throw new ParseException("Unparseable format: " + period, index);

    }

}
