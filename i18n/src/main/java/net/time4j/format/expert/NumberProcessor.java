/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (NumberProcessor.java) is part of project Time4J.
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

import net.time4j.PlainDate;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.format.Attributes;
import net.time4j.format.Leniency;
import net.time4j.format.NumberSystem;
import net.time4j.format.NumericalElement;
import net.time4j.history.internal.HistorizedElement;

import java.io.IOException;
import java.util.Set;


/**
 * <p>Ganzzahl-Formatierung eines chronologischen Elements. </p>
 *
 * @param   <V> generic type of element values (Integer, Long or Enum)
 * @author  Meno Hochschild
 * @since   3.0
 */
class NumberProcessor<V>
    implements FormatProcessor<V> {

    //~ Instanzvariablen --------------------------------------------------

    private final ChronoElement<V> element;
    private final boolean fixedWidth;
    private final int minDigits;
    private final int maxDigits;
    private final SignPolicy signPolicy;
    private final boolean protectedMode;
    private final boolean yearOfEra;

    // quick path optimization
    private final Leniency lenientMode;
    private final int reserved;
    private final char zeroDigit;
    private final NumberSystem numberSystem;
    private final int protectedLength;
    private final int scaleOfNumsys;

    // high-speed optimization
    private final boolean fixedInt;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruiert eine neue Instanz. </p>
     *
     * @param   element         element to be formatted
     * @param   fixedWidth      fixed-width-mode
     * @param   minDigits       minimum count of digits
     * @param   maxDigits       maximum count of digits
     * @param   signPolicy      sign policy
     * @param   protectedMode   allow replacement?
     * @throws  IllegalArgumentException in case of inconsistencies
     */
    NumberProcessor(
        ChronoElement<V> element,
        boolean fixedWidth,
        int minDigits,
        int maxDigits,
        SignPolicy signPolicy,
        boolean protectedMode
    ) {
        this(
            element, fixedWidth, minDigits, maxDigits, signPolicy, protectedMode,
            0, '0', NumberSystem.ARABIC, Leniency.SMART, 0, false);

    }

    private NumberProcessor(
        ChronoElement<V> element,
        boolean fixedWidth,
        int minDigits,
        int maxDigits,
        SignPolicy signPolicy,
        boolean protectedMode,
        int reserved,
        char zeroDigit,
        NumberSystem numberSystem,
        Leniency lenientMode,
        int protectedLength,
        boolean fixedInt
    ) {
        super();

        this.element = element;
        this.fixedWidth = fixedWidth;
        this.minDigits = minDigits;
        this.maxDigits = maxDigits;
        this.signPolicy = signPolicy;
        this.protectedMode = protectedMode;
        this.fixedInt = fixedInt;

        if (element == null) {
            throw new NullPointerException("Missing element.");
        } else if (signPolicy == null) {
            throw new NullPointerException("Missing sign policy.");
        } else if (minDigits < 1) {
            throw new IllegalArgumentException(
                "Not positive: " + minDigits);
        } else if (minDigits > maxDigits) {
            throw new IllegalArgumentException(
                "Max smaller than min: " + maxDigits + " < " + minDigits);
        } else if (fixedWidth && (minDigits != maxDigits)) {
            throw new IllegalArgumentException(
                "Variable width in fixed-width-mode: "
                    + maxDigits + " != " + minDigits);
        } else if (fixedWidth && (signPolicy != SignPolicy.SHOW_NEVER)) {
            throw new IllegalArgumentException(
                "Sign policy must be SHOW_NEVER in fixed-width-mode.");
        }

        int scale = this.getScale(numberSystem);

        if (numberSystem.isDecimal()) {
            if (minDigits > scale) {
                throw new IllegalArgumentException(
                    "Min digits out of range: " + minDigits);
            } else if (maxDigits > scale) {
                throw new IllegalArgumentException(
                    "Max digits out of range: " + maxDigits);
            }
        }

        this.yearOfEra = (this.element.name().equals("YEAR_OF_ERA"));

        // quick path members
        this.reserved = reserved;
        this.zeroDigit = zeroDigit;
        this.numberSystem = numberSystem;
        this.lenientMode = lenientMode;
        this.protectedLength = protectedLength;
        this.scaleOfNumsys = scale;

    }

    //~ Methoden ----------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Override
    public void print(
        ChronoDisplay formattable,
        Appendable buffer,
        AttributeQuery attributes,
        Set<ElementPosition> positions, // optional
        boolean quickPath
    ) throws IOException {

        int start = ((buffer instanceof CharSequence) ? ((CharSequence) buffer).length() : -1);
        int printed = 0;

        NumberSystem numsys;
        char zeroChar;

        if (quickPath) {
            numsys = this.numberSystem;
            zeroChar = this.zeroDigit;
        } else {
            numsys = attributes.get(Attributes.NUMBER_SYSTEM, NumberSystem.ARABIC);
            zeroChar = (
                attributes.contains(Attributes.ZERO_DIGIT)
                    ? attributes.get(Attributes.ZERO_DIGIT).charValue()
                    : (numsys.isDecimal() ? numsys.getDigits().charAt(0) : '0'));
        }

        if (quickPath && this.fixedInt) {
            int v = formattable.getInt((ChronoElement<Integer>) this.element);
            if (v < 0) {
                if (v == Integer.MIN_VALUE) {
                    throw new IllegalArgumentException(
                        "Format context \"" + formattable + "\" without element: " + this.element);
                } else {
                    throw new IllegalArgumentException(
                        "Negative value not allowed according to sign policy.");
                }
            }
            int count = length(v);
            if (count > this.maxDigits) {
                throw new IllegalArgumentException(
                    "Element " + this.element.name()
                        + " cannot be printed as the formatted value " + v
                        + " exceeds the maximum width of " + this.maxDigits + ".");
            }
            for (int i = 0, n = this.minDigits - count; i < n; i++) {
                buffer.append('0');
                printed++;
            }
            if (count == 2) {
                appendTwoDigits(v, buffer, '0');
            } else if (count == 1) {
                buffer.append((char) (v + 48));
            } else if (v >= 2000 && v < 2100) {
                buffer.append('2');
                buffer.append('0');
                appendTwoDigits(v - 2000, buffer, '0');
            } else if (v >= 1900 && v < 2000) {
                buffer.append('1');
                buffer.append('9');
                appendTwoDigits(v - 1900, buffer, '0');
            } else {
                buffer.append(Integer.toString(v));
            }
            printed += count;
        } else if (this.yearOfEra && (this.element instanceof HistorizedElement)) {
            HistorizedElement te = HistorizedElement.class.cast(this.element);
            StringBuilder sb = new StringBuilder();
            te.print(formattable, sb, attributes, numsys, zeroChar, this.minDigits, this.maxDigits);
            buffer.append(sb.toString());
            printed = sb.length();
        } else {
            char defaultZeroChar = numsys.getDigits().charAt(0);
            Class<V> type = this.element.getType();
            boolean negative = false;
            boolean decimal = numsys.isDecimal();
            String digits = null;
            int x;
            int count;

            if (type == Integer.class) {
                int v = formattable.getInt((ChronoElement<Integer>) this.element);
                if (v == Integer.MIN_VALUE) {
                    throw new IllegalArgumentException(
                        "Format context \"" + formattable + "\" without element: " + this.element);
                }
                negative = (v < 0);
                x = Math.abs(v);
                count = length(x);
            } else if (type == Long.class) {
                V value = formattable.get(this.element);
                long v = Long.class.cast(value).longValue();
                negative = (v < 0);
                digits = (
                    (v == Long.MIN_VALUE)
                        ? "9223372036854775808"
                        : Long.toString(Math.abs(v))
                );
                x = Integer.MIN_VALUE; // satisfies compiler
                count = digits.length();
                defaultZeroChar = '0';
            } else if (Enum.class.isAssignableFrom(type)) {
                V value = formattable.get(this.element);
                int v = -1;
                if (this.element instanceof NumericalElement) {
                    v = ((NumericalElement<V>) this.element).numerical(value);
                    negative = (v < 0);
                } else {
                    for (Object e : type.getEnumConstants()) {
                        if (e.equals(value)) {
                            v = Enum.class.cast(e).ordinal();
                            break;
                        }
                    }
                    if (v == -1) {
                        throw new AssertionError(
                            "Enum broken: " + value + " / " + type.getName());
                    }
                }
                if (v == Integer.MIN_VALUE) {
                    throw new IllegalArgumentException("Cannot print: " + this.element);
                }
                x = Math.abs(v);
                count = length(x);
            } else {
                throw new IllegalArgumentException("Not formattable: " + this.element);
            }

            if (decimal) {
                if (zeroChar != defaultZeroChar) { // rare case
                    int diff = zeroChar - defaultZeroChar;
                    if (digits == null) {
                        digits = numsys.toNumeral(x);
                    }
                    char[] characters = digits.toCharArray();
                    for (int i = 0; i < characters.length; i++) {
                        characters[i] = (char) (characters[i] + diff);
                    }
                    digits = new String(characters);
                }
                if (count > this.maxDigits) {
                    if (digits == null) {
                        digits = numsys.toNumeral(x);
                    }
                    throw new IllegalArgumentException(
                        "Element " + this.element.name()
                            + " cannot be printed as the formatted value " + digits
                            + " exceeds the maximum width of " + this.maxDigits + ".");
                }
            }

            if (negative) {
                if (this.signPolicy == SignPolicy.SHOW_NEVER) {
                    throw new IllegalArgumentException(
                        "Negative value not allowed according to sign policy.");
                } else {
                    buffer.append('-');
                    printed++;
                }
            } else {
                switch (this.signPolicy) {
                    case SHOW_ALWAYS:
                        buffer.append('+');
                        printed++;
                        break;
                    case SHOW_WHEN_BIG_NUMBER:
                        if (decimal && (count > this.minDigits)) {
                            buffer.append('+');
                            printed++;
                        }
                        break;
                    default:
                        // no-op
                }
            }

            if (decimal) {
                for (int i = 0, n = this.minDigits - count; i < n; i++) {
                    buffer.append(zeroChar);
                    printed++;
                }
            }

            if (digits == null) {
                if (decimal) {
                    if (count == 2) {
                        appendTwoDigits(x, buffer, zeroChar);
                    } else if (count == 1) {
                        buffer.append((char) (x + zeroChar));
                    } else if (x >= 2000 && x < 2100) {
                        buffer.append((char) (2 + zeroChar));
                        buffer.append(zeroChar);
                        appendTwoDigits(x - 2000, buffer, zeroChar);
                    } else if (x >= 1900 && x < 2000) {
                        buffer.append((char) (1 + zeroChar));
                        buffer.append((char) (9 + zeroChar));
                        appendTwoDigits(x - 1900, buffer, zeroChar);
                    } else {
                        buffer.append(numsys.toNumeral(x));
                    }
                } else {
                    count = numsys.toNumeral(x, buffer);
                }
            } else {
                buffer.append(digits);
                count = digits.length();
            }

            printed += count;
        }

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

        if (quickPath && this.fixedInt) {
            if (start >= len) {
                status.setError(start, "Missing digits for: " + this.element.name());
                status.setWarning();
                return;
            }
            char sign = text.charAt(start);
            if ((sign == '-') || (sign == '+')) {
                status.setError(
                    start,
                    "Sign not allowed due to sign policy.");
                return;
            }
            int minPos = start + this.minDigits;
            int maxPos = Math.min(len, minPos); // maxDigits == minDigits
            int total = 0;
            int pos = start;
            while (pos < maxPos) {
                int digit = text.charAt(pos) - '0';
                if ((digit >= 0) && (digit <= 9)) {
                    total = total * 10 + digit;
                    pos++;
                } else {
                    break;
                }
            }
            if (pos < minPos) {
                if (pos == start) {
                    status.setError(start, "Digit expected.");
                } else {
                    status.setError(
                        start,
                        "Not enough digits found for: " + this.element.name());
                }
                return;
            }
            parsedResult.put(this.element, total);
            status.setPosition(pos);
            return;
        }

        int protectedChars = (quickPath ? this.protectedLength : attributes.get(Attributes.PROTECTED_CHARACTERS, 0));

        if (protectedChars > 0) {
            len -= protectedChars;
        }

        if (start >= len) {
            status.setError(start, "Missing digits for: " + this.element.name());
            status.setWarning();
            return;
        }

        if (this.yearOfEra && (this.element instanceof HistorizedElement)) {
            HistorizedElement te = HistorizedElement.class.cast(this.element);
            Object value = te.parse(text, status.getPP(), attributes, parsedResult);
            if (status.isError()) {
                status.setError(status.getErrorIndex(), "Unparseable element: " + this.element.name());
            } else if (value == null) {
                status.setError(start, "No interpretable value.");
            } else {
                parsedResult.put(this.element, value);
            }
            return;
        }

        NumberSystem numsys;
        char zeroChar;
        int effectiveMin = 1;
        int effectiveMax;
        boolean decimal;

        if (quickPath) {
            numsys = this.numberSystem;
            decimal = numsys.isDecimal();
            effectiveMax = this.scaleOfNumsys;
            zeroChar = this.zeroDigit;
        } else {
            numsys = attributes.get(Attributes.NUMBER_SYSTEM, NumberSystem.ARABIC);
            decimal = numsys.isDecimal();
            effectiveMax = this.getScale(numsys);
            zeroChar = (
                attributes.contains(Attributes.ZERO_DIGIT)
                    ? attributes.get(Attributes.ZERO_DIGIT).charValue()
                    : (decimal ? numsys.getDigits().charAt(0) : '0'));
        }

        Leniency leniency = (quickPath ? this.lenientMode : attributes.get(Attributes.LENIENCY, Leniency.SMART));

        if (decimal && (this.fixedWidth || !leniency.isLax())) {
            effectiveMin = this.minDigits;
            effectiveMax = this.maxDigits;
        }

        int pos = start;
        boolean negative = false;
        char sign = text.charAt(pos);

        if ((sign == '-') || (sign == '+')) {
            if (
                (this.signPolicy == SignPolicy.SHOW_NEVER)
                && (this.fixedWidth || leniency.isStrict())
            ) {
                status.setError(
                    start,
                    "Sign not allowed due to sign policy.");
                return;
            } else if (
                (this.signPolicy == SignPolicy.SHOW_WHEN_NEGATIVE)
                && (sign == '+')
                && leniency.isStrict()
            ) {
                status.setError(
                    start,
                    "Positive sign not allowed due to sign policy.");
                return;
            }
            negative = (sign == '-');
            pos++;
            start++;
        } else if (
            (this.signPolicy == SignPolicy.SHOW_ALWAYS)
            && leniency.isStrict()
        ) {
            status.setError(start, "Missing sign of number.");
            return;
        }

        if (pos >= len) {
            status.setError(
                start,
                "Missing digits for: " + this.element.name());
            return;
        }

        if (
            !this.fixedWidth
            && (this.reserved > 0)
            && (protectedChars <= 0)
        ) {
            int digitCount = 0;

            // Wieviele Ziffern hat der ganze Ziffernblock?
            if (decimal) {
                for (int i = pos; i < len; i++) {
                    int digit = text.charAt(i) - zeroChar;

                    if ((digit >= 0) && (digit <= 9)) {
                        digitCount++;
                    } else {
                        break;
                    }
                }
            } else {
                for (int i = pos; i < len; i++) {
                    if (numsys.contains(text.charAt(i))) {
                        digitCount++;
                    } else {
                        break;
                    }
                }
            }

            effectiveMax = Math.min(effectiveMax, digitCount - this.reserved);
        }

        int minPos = pos + effectiveMin;
        int maxPos = Math.min(len, pos + effectiveMax);
        long total = 0;

        if (decimal) {
            while (pos < maxPos) {
                int digit = text.charAt(pos) - zeroChar;

                if ((digit >= 0) && (digit <= 9)) {
                    total = total * 10 + digit;
                    pos++;
                } else {
                    break;
                }
            }
        } else {
            int digitCount = 0;

            while (pos < maxPos) {
                if (numsys.contains(text.charAt(pos))) {
                    digitCount++;
                    pos++;
                } else {
                    break;
                }
            }

            try {
                if (digitCount > 0) {
                    total = numsys.toInteger(text.subSequence(pos - digitCount, pos).toString(), leniency);
                }
            } catch (NumberFormatException nfe) {
                status.setError(start, nfe.getMessage());
                return;
            }
        }

        if (pos < minPos) {
            if (pos == start) {
                status.setError(start, "Digit expected.");
                return;
            } else if (this.fixedWidth || !leniency.isLax()) {
                status.setError(
                    start,
                    "Not enough digits found for: " + this.element.name());
                return;
            }
        }

        if (negative) {
            if ((total == 0) && leniency.isStrict()) {
                status.setError(start - 1, "Negative zero is not allowed.");
                return;
            }
            total = -total;
        } else if (
            (this.signPolicy == SignPolicy.SHOW_WHEN_BIG_NUMBER)
            && leniency.isStrict()
            && decimal
        ) {
            if ((sign == '+') && (pos <= minPos)) {
                status.setError(
                    start - 1,
                    "Positive sign only allowed for big number.");
            } else if ((sign != '+') && (pos > minPos)) {
                status.setError(
                    start,
                    "Positive sign must be present for big number.");
            }
        }

        Object value = null;
        Class<V> type = this.element.getType();

        if (type == Integer.class) {
            parsedResult.put(this.element, (int) total);
            status.setPosition(pos);
            return;
        } else if (type == Long.class) {
            value = Long.valueOf(total);
        } else if (this.element == PlainDate.MONTH_OF_YEAR) {
            parsedResult.put(PlainDate.MONTH_AS_NUMBER, (int) total);
            status.setPosition(pos);
            return;
        } else if (Enum.class.isAssignableFrom(type)) {
            if (this.element instanceof NumericalElement) { // Normalfall
                NumericalElement<V> ne = (NumericalElement<V>) this.element;
                for (Object e : type.getEnumConstants()) {
                    if (ne.numerical(type.cast(e)) == total) {
                        value = e;
                        break;
                    }
                }
            } else {
                for (Object e : type.getEnumConstants()) { // Ausweichoption
                    if (Enum.class.cast(e).ordinal() == total) {
                        value = e;
                        break;
                    }
                }
            }

            if (value == null) {
                status.setError(
                    ((sign == '-') || (sign == '+') ? start - 1 : start),
                    "["
                        + this.element.name()
                        + "] No enum found for value: "
                        + total);
                return;
            }
        } else {
            throw new IllegalArgumentException(
                "Not parseable: " + this.element);
        }

        parsedResult.put(this.element, value);
        status.setPosition(pos);

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof NumberProcessor) {
            NumberProcessor<?> that = (NumberProcessor<?>) obj;
            return (
                this.element.equals(that.element)
                && (this.fixedWidth == that.fixedWidth)
                && (this.minDigits == that.minDigits)
                && (this.maxDigits == that.maxDigits)
                && (this.signPolicy == that.signPolicy)
                && (this.protectedMode == that.protectedMode)
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
        sb.append(", fixed-width-mode=");
        sb.append(this.fixedWidth);
        sb.append(", min-digits=");
        sb.append(this.minDigits);
        sb.append(", max-digits=");
        sb.append(this.maxDigits);
        sb.append(", sign-policy=");
        sb.append(this.signPolicy);
        sb.append(", protected-mode=");
        sb.append(this.protectedMode);
        sb.append(']');
        return sb.toString();

    }

    @Override
    public ChronoElement<V> getElement() {

        return this.element;

    }

    @Override
    public FormatProcessor<V> withElement(ChronoElement<V> element) {

        if (
            this.protectedMode
            || (this.element == element)
        ) {
            return this;
        }

        return new NumberProcessor<V>(
            element,
            this.fixedWidth,
            this.minDigits,
            this.maxDigits,
            this.signPolicy,
            false
        );

    }

    @Override
    public boolean isNumerical() {

        return true;

    }

    @Override
    public FormatProcessor<V> quickPath(
        ChronoFormatter<?> formatter,
        AttributeQuery attributes,
        int reserved
    ) {

        NumberSystem numsys = attributes.get(Attributes.NUMBER_SYSTEM, NumberSystem.ARABIC);

        char zeroChar =
            attributes.contains(Attributes.ZERO_DIGIT)
            ? attributes.get(Attributes.ZERO_DIGIT).charValue()
            : (numsys.isDecimal() ? numsys.getDigits().charAt(0) : '0');

        int plen = attributes.get(Attributes.PROTECTED_CHARACTERS, 0);
        boolean hasFixedInt = (
            (numsys == NumberSystem.ARABIC)
            && (zeroChar == '0')
            && this.fixedWidth
            && (plen == 0)
            && (this.element.getType() == Integer.class)
            && !this.yearOfEra
        );

        return new NumberProcessor<V>(
            this.element,
            this.fixedWidth,
            this.minDigits,
            this.maxDigits,
            this.signPolicy,
            this.protectedMode,
            reserved,
            zeroChar,
            numsys,
            attributes.get(Attributes.LENIENCY, Leniency.SMART),
            plen,
            hasFixedInt
        );

    }

    private int getScale(NumberSystem numsys) {

        if (numsys.isDecimal()) {
            return ((this.element.getType() == Long.class) ? 18 : 9);
        } else {
            return 100; // sufficiently large
        }

    }

    private static final int[] THRESHOLDS =
        { 9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE };

    private static int length(int v) {

        assert (v >= 0);

        for (int i = 0; ; i++) {
            if (v <= THRESHOLDS[i]) return i + 1;
        }

    }

    private static void appendTwoDigits(
        int dd, // must consist of two digits only
        Appendable buffer,
        char zeroDigit
    ) throws IOException {

        int q = ((dd * 103) >>> 10);        // q = dd / 10;
        int r = dd - ((q << 3) + (q << 1)); // r = dd - (q * 10);
        buffer.append((char) (q + zeroDigit));
        buffer.append((char) (r + zeroDigit));

    }

}
