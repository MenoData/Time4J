/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SingleUnitTimeSpan.java) is part of project Time4J.
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

import net.time4j.Duration;
import net.time4j.IsoDateUnit;
import net.time4j.PrettyTime;
import net.time4j.base.MathUtils;
import net.time4j.engine.TimePoint;
import net.time4j.engine.TimeSpan;
import net.time4j.format.TextWidth;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


/**
 * <p>Represents a time span in one calendrical unit only. </p>
 *
 * @param   <U> generic type of calendrical units
 * @author  Meno Hochschild
 * @since   3.21/4.17
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Zeitspanne in nur einer kalendarischen Zeiteinheit. </p>
 *
 * @param   <U> generic type of calendrical units
 * @author  Meno Hochschild
 * @since   3.21/4.17
 */
public abstract class SingleUnitTimeSpan<U extends IsoDateUnit, D extends SingleUnitTimeSpan<U, D>>
    implements TimeSpan<U>, Comparable<D>, Serializable {

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial      count of units
     */
    /*[deutsch]
     * @serial      Anzahl der Zeiteinheiten
     */
    private final int amount;

    /**
     * @serial      type of unit
     */
    /*[deutsch]
     * @serial      Einheitstyp
     */
    private final U unit;

    //~ Konstruktoren -----------------------------------------------------

    // package-private
    SingleUnitTimeSpan(
        int amount,
        U unit
    ) {
        super();

        this.amount = amount;
        this.unit = unit;

        this.checkConsistency(unit);

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Yields the count of units as integer-based amount. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert die Anzahl der Zeiteinheiten als Integer-Betrag. </p>
     *
     * @return  int
     */
    public int getAmount() {

        return this.amount;

    }

    /**
     * <p>Yields the associated unit. </p>
     *
     * @return  calendrical unit
     */
    /*[deutsch]
     * <p>Liefert die assoziierte Zeiteinheit. </p>
     *
     * @return  calendrical unit
     */
    public U getUnit() {

        return this.unit;

    }

    @Override
    public int compareTo(D other) {

        if (this.unit.equals(other.getUnit())) {
            return ((this.amount < other.getAmount()) ? -1 : (this.amount == other.getAmount()) ? 0 : 1);
        } else {
            throw new ClassCastException("Durations with different units are not comparable.");
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

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof SingleUnitTimeSpan) {
            SingleUnitTimeSpan<?, ?> that = SingleUnitTimeSpan.class.cast(obj);
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
     * <p>Prints in ISO-8601-format &quot;PnU&quot; (n=amount, U=unit). </p>
     *
     * <p>Negative durations will get a preceding sign before &quot;P&quot;. Note that gregorian
     * and week-based years have the same representation using the symbol Y. </p>
     *
     * @return  the duration in ISO-8601-format
     */
    /*[deutsch]
     * <p>Liefert einen ISO-8601-kompatiblen String im Format &quot;PnU&quot; (n=Betrag, U=Einheit). </p>
     *
     * <p>Eine negative Dauer bekommt ein Minuszeichen vorangestellt. Zu beachten: Gregorianische und
     * wochenbasierte Jahre haben die gleiche Repr&auml;sentation mit dem Symbol Y. </p>
     *
     * @return  the duration in ISO-8601-format
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
        sb.append(this.unit.getSymbol());
        return sb.toString();

    }

    /**
     * <p>Prints this duration in a localized way with given text width. </p>
     *
     * @param   locale  the locale to be applied
     * @param   width   the text width to be applied
     * @return  formatted localized representation of this duration
     * @since   5.0
     */
    /*[deutsch]
     * <p>Liefert eine formatierte und lokalisierte Darstellung dieser Dauer mit der angegebenen Textbreite aus. </p>
     *
     * @param   locale  the locale to be applied
     * @param   width   the text width to be applied
     * @return  formatted localized representation of this duration
     * @since   5.0
     */
    public String toString(
        Locale locale,
        TextWidth width
    ) {

        return PrettyTime.of(locale).print(this.toStdDuration(), width);

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
    public D abs() {

        long value = this.amount;
        return this.with(MathUtils.safeCast(Math.abs(value)));

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
    public D inverse() {

        return this.with(MathUtils.safeNegate(this.amount));

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
    public D plus(int amount) {

        if (amount == 0) {
            return this.self();
        }

        long value = this.amount;
        return this.with(MathUtils.safeCast(value + amount));

    }

    /**
     * <p>Yields a copy with the added duration. </p>
     *
     * @param   duration    the duration to be added
     * @return  result of addition as immutable copy
     * @throws  ArithmeticException if numeric overflow occurs
     */
    /*[deutsch]
     * <p>Liefert eine Kopie mit der addierten Dauer. </p>
     *
     * @param   duration    the duration to be added
     * @return  result of addition as immutable copy
     * @throws  ArithmeticException if numeric overflow occurs
     */
    public D plus(D duration) {

        if (duration.isEmpty()) {
            return this.self();
        }

        long value = this.amount;
        return this.with(MathUtils.safeCast(value + duration.getAmount()));

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
    public D minus(int amount) {

        if (amount == 0) {
            return this.self();
        }

        long value = this.amount;
        return this.with(MathUtils.safeCast(value - amount));

    }

    /**
     * <p>Yields a copy with the subtracted duration. </p>
     *
     * @param   duration    the duration to be added
     * @return  result of subtraction as immutable copy
     * @throws  ArithmeticException if numeric overflow occurs
     */
    /*[deutsch]
     * <p>Liefert eine Kopie mit der subtrahierten Dauer. </p>
     *
     * @param   duration    the duration to be added
     * @return  result of subtraction as immutable copy
     * @throws  ArithmeticException if numeric overflow occurs
     */
    public D minus(D duration) {

        if (duration.isEmpty()) {
            return this.self();
        }

        long value = this.amount;
        return this.with(MathUtils.safeCast(value - duration.getAmount()));

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
    public D multipliedBy(int factor) {

        switch (factor) {
            case -1:
                return this.inverse();
            case 1:
                return this.self();
            default:
                return this.with(MathUtils.safeMultiply(this.amount, factor));
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
    public Duration<U> toStdDuration() {

        return Duration.of(this.amount, this.unit);

    }

    // package-private
    abstract D with(int amount);

    // package-private
    abstract D self();

    // called by subclasses
    static int parsePeriod(
        String period,
        char symbol
    ) throws ParseException {

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

        if ((len == index + 1) && (period.charAt(index) == symbol)) {
            index++; // consume unit symbol
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

    /**
     * @serialData  Checks the consistency
     * @param       in      object input stream
     * @throws      InvalidObjectException in any case of inconsistencies
     */
    final void readObject(ObjectInputStream in)
        throws IOException {

        this.checkConsistency(this.unit);

    }

    // used in deserialization and constructors
    void checkConsistency(U unit) {

        if (unit == null) {
            throw new NullPointerException("Missing unit.");
        }

    }

}
