/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FractionProcessor.java) is part of project Time4J.
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

package net.time4j.format.expert;

import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.format.Attributes;
import net.time4j.format.Leniency;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;


/**
 * <p>Fraktionale Formatierung eines Sekundenbruchteils. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
final class FractionProcessor
    implements FormatProcessor<Integer> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int MRD_MINUS_1 = 999999999;

    //~ Instanzvariablen --------------------------------------------------

    private final FormatProcessor<Void> decimalSeparator;
    private final ChronoElement<Integer> element;
    private final int minDigits;
    private final int maxDigits;
    private final boolean fixedWidth;

    // quick path optimization
    private final char zeroDigit;
    private final Leniency lenientMode;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruiert eine neue Instanz. </p>
     *
     * @param   element             element to be formatted in a fractional way
     * @param   minDigits           minimum count of digits
     * @param   maxDigits           maximum count of digits
     * @param   decimalSeparator    shall decimal separator be visible?
     */
    FractionProcessor(
        ChronoElement<Integer> element,
        int minDigits,
        int maxDigits,
        boolean decimalSeparator
    ) {
        super();

        this.element = element;
        this.minDigits = minDigits;
        this.maxDigits = maxDigits;
        this.fixedWidth = (!decimalSeparator && (minDigits == maxDigits));

        this.decimalSeparator = (
            decimalSeparator
            ? new LiteralProcessor(Attributes.DECIMAL_SEPARATOR)
            : null);

        if (element == null) {
            throw new NullPointerException("Missing element.");
        } else if (minDigits < 0) {
            throw new IllegalArgumentException(
                "Negative min digits: " + minDigits);
        } else if (minDigits > maxDigits) {
            throw new IllegalArgumentException(
                "Max smaller than min: " + maxDigits + " < " + minDigits);
        }

        if (minDigits > 9) {
            throw new IllegalArgumentException(
                "Min digits out of range: " + minDigits);
        } else if (maxDigits > 9) {
            throw new IllegalArgumentException(
                "Max digits out of range: " + maxDigits);
        }

        this.zeroDigit = '0';
        this.lenientMode = Leniency.SMART;

    }

    private FractionProcessor(
        FormatProcessor<Void> decimalSeparator,
        ChronoElement<Integer> element,
        int minDigits,
        int maxDigits,
        boolean fixedWidth,
        char zeroDigit,
        Leniency lenientMode
    ) {
        super();

        this.decimalSeparator = decimalSeparator;
        this.element = element;
        this.minDigits = minDigits;
        this.maxDigits = maxDigits;
        this.fixedWidth = fixedWidth;

        // quick path members
        this.zeroDigit = zeroDigit;
        this.lenientMode = lenientMode;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public void print(
        ChronoDisplay formattable,
        Appendable buffer,
        AttributeQuery attributes,
        Set<ElementPosition> positions, // optional
        boolean quickPath
    ) throws IOException {

        BigDecimal value = toDecimal(formattable.get(this.element));
        BigDecimal min = toDecimal(formattable.getMinimum(this.element));
        BigDecimal max = toDecimal(formattable.getMaximum(this.element));

        if (value.compareTo(max) > 0) {
            value = max;
        }

        BigDecimal fraction =
            value.subtract(min).divide(
                max.subtract(min).add(BigDecimal.ONE),
                9,
                RoundingMode.FLOOR);

        fraction = (
            (fraction.compareTo(BigDecimal.ZERO) == 0)
            ? BigDecimal.ZERO
            : fraction.stripTrailingZeros()
        );

        char zeroChar = (
            quickPath
                ? this.zeroDigit
                : attributes.get(Attributes.ZERO_DIGIT, Character.valueOf('0')).charValue());

        int start = -1;
        int printed = 0;

        if (buffer instanceof CharSequence) {
            start = ((CharSequence) buffer).length();
        }

        if (fraction.scale() == 0) {
            // scale ist 0, wenn value das Minimum ist
            if (this.minDigits > 0) {
                if (this.hasDecimalSeparator()) {
                    this.decimalSeparator.print(
                        formattable,
                        buffer,
                        attributes,
                        positions,
                        quickPath);
                    printed++;
                }

                for (int i = 0; i < this.minDigits; i++) {
                    buffer.append(zeroChar);
                }

                printed += this.minDigits;
            }
        } else {
            if (this.hasDecimalSeparator()) {
                this.decimalSeparator.print(
                    formattable,
                    buffer,
                    attributes,
                    positions,
                    quickPath);
                printed++;
            }

            int outputScale =
                Math.min(
                    Math.max(fraction.scale(), this.minDigits),
                    this.maxDigits);
            fraction = fraction.setScale(outputScale, RoundingMode.FLOOR);
            String digits = fraction.toPlainString();
            int diff = zeroChar - '0';

            for (int i = 2, n = digits.length(); i < n; i++) {
                char c = (char) (digits.charAt(i) + diff);
                buffer.append(c);
                printed++;
            }
        }

        if (
            (start != -1)
            && (printed > 1)
            && (positions != null)
        ) {
            positions.add( // Zählung ohne Dezimaltrennzeichen
                new ElementPosition(this.element, start + 1, start + printed));
        }

    }

    @Override
    public void parse(
        CharSequence text,
        ParseLog status,
        AttributeQuery attributes,
        ParsedValues parsedResult,
        boolean quickPath
    ) {

        Leniency leniency = (quickPath ? this.lenientMode : attributes.get(Attributes.LENIENCY, Leniency.SMART));
        int effectiveMin = 0;
        int effectiveMax = 9;

        if (!leniency.isLax() || this.fixedWidth) {
            effectiveMin = this.minDigits;
            effectiveMax = this.maxDigits;
        }

        int len = text.length();

        if (status.getPosition() >= len) {
            if (effectiveMin > 0) {
                status.setError(
                    status.getPosition(),
                    "Expected fraction digits not found for: "
                        + this.element.name());
            }
            return;
        }

        if (this.hasDecimalSeparator()) {
            this.decimalSeparator.parse(
                text,
                status,
                attributes,
                null,
                quickPath);

            if (status.isError()) {
                if (effectiveMin == 0) {
                    status.clearError();
                }
                return;
            }
        }

        int current = status.getPosition();
        int minEndPos = current + effectiveMin;
        int maxEndPos = Math.min(current + effectiveMax, len);

        if ((minEndPos > len) && leniency.isStrict()) {
            status.setError(
                status.getPosition(),
                "Expected at least " + effectiveMin + " digits.");
            return;
        }

        char zeroChar = (
            quickPath
                ? this.zeroDigit
                : attributes.get(Attributes.ZERO_DIGIT, Character.valueOf('0')).charValue());

        long total = 0;

        while (current < maxEndPos) {
            int digit = text.charAt(current) - zeroChar;

            if ((digit >= 0) && (digit <= 9)) {
                total = total * 10 + digit;
                current++;
            } else if ((current < minEndPos) && leniency.isStrict()) {
                status.setError(
                    status.getPosition(),
                    "Expected at least " + effectiveMin + " digits.");
                return;
            } else {
                break;
            }
        }

        BigDecimal fraction = new BigDecimal(total);
        fraction = fraction.movePointLeft(current - status.getPosition());

        if (this.element.name().equals("NANO_OF_SECOND")) {
            int num = this.getRealValue(fraction, 0, MRD_MINUS_1);
            parsedResult.put(this.element, num);
        } else {
            // hier nur prototypischer Wert, später fraktionalen Wert bestimmen
            parsedResult.put(FractionalElement.FRACTION, fraction);
            parsedResult.put(this.element, this.element.getDefaultMinimum());
        }

        status.setPosition(current);

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof FractionProcessor) {
            FractionProcessor that = (FractionProcessor) obj;
            return (
                this.element.equals(that.element)
                && (this.minDigits == that.minDigits)
                && (this.maxDigits == that.maxDigits)
                && (this.hasDecimalSeparator() == that.hasDecimalSeparator())
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return (
            7 * this.element.hashCode()
            + 31 * (this.minDigits + this.maxDigits * 10)
        );

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(64);
        sb.append(this.getClass().getName());
        sb.append("[element=");
        sb.append(this.element.name());
        sb.append(", min-digits=");
        sb.append(this.minDigits);
        sb.append(", max-digits=");
        sb.append(this.maxDigits);
        sb.append(']');
        return sb.toString();

    }

    @Override
    public ChronoElement<Integer> getElement() {

        return this.element;

    }

    @Override
    public FormatProcessor<Integer> withElement(
        ChronoElement<Integer> element
    ) {

        if (this.element == element) {
            return this;
        }

        return new FractionProcessor(
            element,
            this.minDigits,
            this.maxDigits,
            this.hasDecimalSeparator()
        );

    }

    @Override
    public boolean isNumerical() {

        return true;

    }

    @Override
    public FormatProcessor<Integer> quickPath(
        ChronoFormatter<?> formatter,
        AttributeQuery attributes,
        int reserved
    ) {

        return new FractionProcessor(
            this.decimalSeparator,
            this.element,
            this.minDigits,
            this.maxDigits,
            this.fixedWidth,
            attributes.get(Attributes.ZERO_DIGIT, Character.valueOf('0')).charValue(),
            attributes.get(Attributes.LENIENCY, Leniency.SMART)
        );

    }

    /**
     * <p>Aktualisiert das prototypische Parse-Ergebnis mit dem richtigen
     * Wert. </p>
     *
     * <p>In der ersten Phase wurde prototypisch nur das Standardminimum
     * des Elements als Wert angenommen. In dieser Phase wird stattdessen der
     * geparste {@code BigDecimal}-Wert in den neuen Elementwert
     * &uuml;bersetzt und damit das Ergebnis angepasst. </p>
     *
     * @param   entity  prototypical result of parsing
     * @param   parsed  intermediate buffer for parsed values
     * @return  updated result object
     */
    ChronoEntity<?> update(
        ChronoEntity<?> entity,
        ParsedValues parsed
    ) {

        if (!parsed.contains(FractionalElement.FRACTION)) {
            return entity;
        }

        BigDecimal fraction = parsed.get(FractionalElement.FRACTION);
        int min = entity.getMinimum(this.element).intValue();
        int max = entity.getMaximum(this.element).intValue();
        int num = this.getRealValue(fraction, min, max);

        parsed.with(FractionalElement.FRACTION, null); // mutable
        parsed.with(this.element, num); // mutable

        return entity.with(this.element, num);

    }

    private int getRealValue(
        BigDecimal fraction,
        int min,
        int max
    ) {

        BigDecimal low = BigDecimal.valueOf(min);

        BigDecimal range =
            BigDecimal.valueOf(max)
            .subtract(low)
            .add(BigDecimal.ONE);

        BigDecimal value =
            fraction.multiply(range)
            .setScale(0, RoundingMode.FLOOR)
            .add(low);

        return value.intValueExact();

    }

    private static BigDecimal toDecimal(Number num) {

        return BigDecimal.valueOf(num.longValue());

    }

    private boolean hasDecimalSeparator() {

        return (this.decimalSeparator != null);

    }

}
