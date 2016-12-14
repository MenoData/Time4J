/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (DecimalProcessor.java) is part of project Time4J.
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
import net.time4j.format.Attributes;
import net.time4j.format.Leniency;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Set;


/**
 * <p>Dezimal-Formatierung eines chronologischen Elements. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
final class DecimalProcessor
    implements FormatProcessor<BigDecimal> {

    //~ Instanzvariablen --------------------------------------------------

    private final FormatProcessor<Void> decimalSeparator;
    private final ChronoElement<BigDecimal> element;
    private final int precision;
    private final int scale;

    // quick path optimization
    private final char zeroDigit;
    private final Leniency lenientMode;
    private final int protectedLength;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruiert eine neue Instanz. </p>
     *
     * @param   element         element to be formatted
     * @param   precision       total count of digits
     * @param   scale           digits after decimal separator
     * @throws  IllegalArgumentException in case of inconsistencies
     */
    DecimalProcessor(
        ChronoElement<BigDecimal> element,
        int precision,
        int scale
    ) {
        super();

        this.decimalSeparator =
            new LiteralProcessor(Attributes.DECIMAL_SEPARATOR);
        this.element = element;
        this.precision = precision;
        this.scale = scale;

        if (element == null) {
            throw new NullPointerException("Missing element.");
        } else if (precision < 2) {
            throw new IllegalArgumentException(
                "Precision must be >= 2: " + precision);
        } else if (scale >= precision) {
            throw new IllegalArgumentException(
                "Precision must be bigger than scale: "
                + precision + "," + scale);
        } else if (scale < 1) {
            throw new IllegalArgumentException(
                "Scale must be bigger than zero.");
        }

        this.zeroDigit = '0';
        this.lenientMode = Leniency.SMART;
        this.protectedLength = 0;

    }

    private DecimalProcessor(
        FormatProcessor<Void> decimalSeparator,
        ChronoElement<BigDecimal> element,
        int precision,
        int scale,
        char zeroDigit,
        Leniency lenientMode,
        int protectedLength
    ) {
        super();

        this.decimalSeparator = decimalSeparator;
        this.element = element;
        this.precision = precision;
        this.scale = scale;
        this.zeroDigit = zeroDigit;
        this.lenientMode = lenientMode;
        this.protectedLength = protectedLength;

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

        BigDecimal value =
            formattable.get(this.element).setScale(
                this.scale,
                RoundingMode.FLOOR);
        String digits = value.toPlainString();
        int p = 0;
        int s = 0;
        int separator = -1;
        int n = digits.length();

        for (int i = 0; i < n; i++) {
            char c = digits.charAt(i);
            if (c == '.') {
                separator = i;
            } else if (separator >= 0) {
                s++;
            } else {
                p++;
            }
        }

        int delta = this.precision - this.scale - p;

        if (delta < 0) {
            throw new IllegalArgumentException(
                "Integer part of element value exceeds fixed format width: "
                + digits);
        }

        StringBuilder sb = new StringBuilder(this.precision + 1);

        for (int i = 0; i < delta; i++) {
            sb.append('0');
        }

        for (int i = 0; i < p; i++) {
            sb.append(digits.charAt(i));
        }

        this.decimalSeparator.print(formattable, sb, attributes, positions, quickPath);

        for (int i = 0; i < s; i++) {
            sb.append(digits.charAt(p + 1 + i));
        }

        for (int i = 0; i < this.scale - s; i++) {
            sb.append('0');
        }

        digits = sb.toString();

        char zeroChar = (
            quickPath
                ? this.zeroDigit
                : attributes.get(Attributes.ZERO_DIGIT, Character.valueOf('0')).charValue());

        if (zeroChar != '0') {
            int diff = zeroChar - '0';
            char[] characters = digits.toCharArray();

            for (int i = 0; i < characters.length; i++) {
                char c = characters[i];
                if ((c >= '0') && (c <= '9')) {
                    characters[i] = (char) (c + diff);
                }
            }

            digits = new String(characters);
        }

        int start = -1;
        int printed = digits.length();

        if (buffer instanceof CharSequence) {
            start = ((CharSequence) buffer).length();
        }

        buffer.append(digits);

        if (
            (start != -1)
            && (printed > 0)
            && (positions != null)
        ) {
            positions.add(new ElementPosition(this.element, start, start + printed));
        }

    }

    @Override
    public void parse(
        CharSequence text,
        ParseLog status,
        AttributeQuery attributes,
        ParsedEntity<?> parsedResult,
        boolean quickPath
    ) {

        int len = text.length();
        int start = status.getPosition();
        int pos = start;

        int protectedChars = (quickPath ? this.protectedLength : attributes.get(Attributes.PROTECTED_CHARACTERS, 0));

        if (protectedChars > 0) {
            len -= protectedChars;
        }

        if (pos >= len) {
            status.setError(pos, "Missing digits for: " + this.element.name());
            status.setWarning();
            return;
        }

        char zeroChar = (
            quickPath
                ? this.zeroDigit
                : attributes.get(Attributes.ZERO_DIGIT, Character.valueOf('0')).charValue());

        int maxPos = Math.min(len, pos + 18);
        long intpart = 0;
        boolean first = true;
        int p = 0;

        while (pos + p < maxPos) {
            int digit = text.charAt(pos + p) - zeroChar;

            if ((digit >= 0) && (digit <= 9)) {
                intpart = intpart * 10 + digit;
                p++;
                first = false;
            } else if (first) {
                status.setError(start, "Digit expected.");
                return;
            } else {
                break;
            }
        }

        Leniency leniency = (quickPath ? this.lenientMode : attributes.get(Attributes.LENIENCY, Leniency.SMART));

        if (leniency.isStrict() && (p != (this.precision - this.scale))) {
            status.setError(pos, "Integer part does not match expected width.");
            return;
        }

        pos += p;
        status.setPosition(pos);

        this.decimalSeparator.parse(
            text,
            status,
            attributes,
            null,
            quickPath);

        if (status.isError()) {
            return;
        } else {
            pos++;
        }

        maxPos = Math.min(len, pos + 18);
        int s = 0;
        long fraction = 0;

        while (pos + s < maxPos) {
            int digit = text.charAt(pos + s) - zeroChar;

            if ((digit >= 0) && (digit <= 9)) {
                fraction = fraction * 10 + digit;
                s++;
            } else {
                break;
            }
        }

        if (s == 0) {
            status.setError(pos, "Fraction part expected.");
            return;
        } else if (leniency.isStrict() && (s != this.scale)) {
            status.setError(
                pos,
                "Fraction part does not match expected width.");
            return;
        }

        pos += s;
        status.setPosition(pos);

        BigDecimal i = new BigDecimal(intpart);
        BigDecimal f = new BigDecimal(BigInteger.valueOf(fraction), s);
        BigDecimal value = i.add(f).stripTrailingZeros();
        parsedResult.put(this.element, value);

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof DecimalProcessor) {
            DecimalProcessor that = (DecimalProcessor) obj;
            return (
                this.element.equals(that.element)
                && (this.precision == that.precision)
                && (this.scale == that.scale)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return (
            7 * this.element.hashCode()
            + 31 * (this.scale + this.precision * 10)
        );

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(64);
        sb.append(this.getClass().getName());
        sb.append("[element=");
        sb.append(this.element.name());
        sb.append(", precision=");
        sb.append(this.precision);
        sb.append(", scale=");
        sb.append(this.scale);
        sb.append(']');
        return sb.toString();

    }

    @Override
    public ChronoElement<BigDecimal> getElement() {

        return this.element;

    }

    @Override
    public FormatProcessor<BigDecimal> withElement(
        ChronoElement<BigDecimal> element
    ) {

        if (this.element == element) {
            return this;
        }

        return new DecimalProcessor(
            element,
            this.precision,
            this.scale
        );

    }

    @Override
    public boolean isNumerical() {

        return true;

    }

    @Override
    public FormatProcessor<BigDecimal> quickPath(
        ChronoFormatter<?> formatter,
        AttributeQuery attributes,
        int reserved
    ) {

        return new DecimalProcessor(
            this.decimalSeparator,
            this.element,
            this.precision,
            this.scale,
            attributes.get(Attributes.ZERO_DIGIT, '0').charValue(),
            attributes.get(Attributes.LENIENCY, Leniency.SMART),
            attributes.get(Attributes.PROTECTED_CHARACTERS, 0).intValue()
        );

    }

}
