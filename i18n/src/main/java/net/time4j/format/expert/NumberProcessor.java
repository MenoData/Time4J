/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
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

import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.Leniency;
import net.time4j.format.NumberSystem;
import net.time4j.format.NumericalElement;
import net.time4j.history.internal.HistorizedElement;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;


/**
 * <p>Ganzzahl-Formatierung eines chronologischen Elements. </p>
 *
 * @param   <V> generic type of element values (Integer, Long or Enum)
 * @author  Meno Hochschild
 * @since   3.0
 */
final class NumberProcessor<V>
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
    private final int reserved;
    private final char zeroDigit;
    private final NumberSystem numberSystem;
    private final Leniency lenientMode;
    private final int protectedLength;

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
            0, '0', NumberSystem.ARABIC, Leniency.SMART, 0);

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
        int protectedLength
    ) {
        super();

        this.element = element;
        this.fixedWidth = fixedWidth;
        this.minDigits = minDigits;
        this.maxDigits = maxDigits;
        this.signPolicy = signPolicy;
        this.protectedMode = protectedMode;

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

        int scale = this.getScale(NumberSystem.ARABIC);

        if (minDigits > scale) {
            throw new IllegalArgumentException(
                "Min digits out of range: " + minDigits);
        } else if (maxDigits > scale) {
            throw new IllegalArgumentException(
                "Max digits out of range: " + maxDigits);
        }

        this.yearOfEra = (this.element.name().equals("YEAR_OF_ERA"));

        // quick path members
        this.reserved = reserved;
        this.zeroDigit = zeroDigit;
        this.numberSystem = numberSystem;
        this.lenientMode = lenientMode;
        this.protectedLength = protectedLength;

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
        NumberSystem numsys = (quickPath ? this.numberSystem : this.getNumberSystem(attributes));
        char zeroChar = '\u0000';

        if (numsys == NumberSystem.ARABIC) {
            zeroChar = (
                quickPath
                    ? this.zeroDigit
                    : attributes.get(Attributes.ZERO_DIGIT, Character.valueOf('0')).charValue());
        }

        if (this.yearOfEra && (this.element instanceof HistorizedElement)) {
            HistorizedElement te = HistorizedElement.class.cast(this.element);
            StringBuilder sb = new StringBuilder();
            te.print(formattable, sb, attributes, numsys, zeroChar, this.minDigits, this.maxDigits);
            buffer.append(sb.toString());
            printed = sb.length();
        } else {
            Class<V> type = this.element.getType();
            boolean negative = false;
            String digits;

            if (type == Integer.class) {
                int v = formattable.getInt((ChronoElement<Integer>) this.element);
                negative = (v < 0);
                digits = toNumeral(numsys, v);
            } else if (type == Long.class) {
                V value = formattable.get(this.element);
                long v = Long.class.cast(value).longValue();
                negative = (v < 0);
                digits = (
                    (v == Long.MIN_VALUE)
                        ? "9223372036854775808"
                        : Long.toString(Math.abs(v))
                );
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
                digits = toNumeral(numsys, v);
            } else {
                throw new IllegalArgumentException("Not formattable: " + this.element);
            }

            if (numsys == NumberSystem.ARABIC) {
                if (zeroChar != '0') {
                    int diff = zeroChar - '0';
                    char[] characters = digits.toCharArray();

                    for (int i = 0; i < characters.length; i++) {
                        characters[i] = (char) (characters[i] + diff);
                    }

                    digits = new String(characters);
                }
                if (digits.length() > this.maxDigits) {
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
                        if (digits.length() > this.minDigits) {
                            buffer.append('+');
                            printed++;
                        }
                        break;
                    default:
                        // no-op
                }
            }

            if (numsys == NumberSystem.ARABIC) {
                for (int i = 0, n = this.minDigits - digits.length(); i < n; i++) {
                    buffer.append(zeroChar);
                    printed++;
                }
            }

            buffer.append(digits);
            printed += digits.length();
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
        ParsedValues parsedResult,
        boolean quickPath
    ) {

        int len = text.length();
        int start = status.getPosition();
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

        NumberSystem numsys = (quickPath ? this.numberSystem : this.getNumberSystem(attributes));
        Leniency leniency = (quickPath ? this.lenientMode : attributes.get(Attributes.LENIENCY, Leniency.SMART));

        int effectiveMin = 1;
        int effectiveMax = this.getScale(numsys);

        if (this.fixedWidth || !leniency.isLax()) {
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

        char zeroChar = (
            quickPath
                ? this.zeroDigit
                : attributes.get(Attributes.ZERO_DIGIT, Character.valueOf('0')).charValue());

        if (
            !this.fixedWidth
            && (this.reserved > 0)
            && (protectedChars <= 0)
        ) {
            int digitCount = 0;

            // Wieviele Ziffern hat der ganze Ziffernblock?
            if (numsys == NumberSystem.ARABIC) {
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

        if (numsys == NumberSystem.ARABIC) {
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
            && (numsys == NumberSystem.ARABIC)
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
        AttributeQuery attributes,
        int reserved
    ) {

        return new NumberProcessor<V>(
            this.element,
            this.fixedWidth,
            this.minDigits,
            this.maxDigits,
            this.signPolicy,
            this.protectedMode,
            reserved,
            attributes.get(Attributes.ZERO_DIGIT, '0').charValue(),
            this.getNumberSystem(attributes),
            attributes.get(Attributes.LENIENCY, Leniency.SMART),
            attributes.get(Attributes.PROTECTED_CHARACTERS, 0)
        );

    }

    private int getScale(NumberSystem numsys) {

        if (numsys == NumberSystem.ARABIC) {
            return ((this.element.getType() == Long.class) ? 18 : 9);
        } else {
            return Integer.MAX_VALUE;
        }

    }

    private static String toNumeral(
        NumberSystem numsys,
        int v
    ) {

        if ((v == Integer.MIN_VALUE) && (numsys == NumberSystem.ARABIC)) {
            return "2147483648";
        }

        return numsys.toNumeral(Math.abs(v));

    }

    private NumberSystem getNumberSystem(AttributeQuery attrs) {

        NumberSystem defaultNumberSystem = NumberSystem.ARABIC;

        if (
            this.yearOfEra
            && attrs.get(Attributes.LANGUAGE, Locale.ROOT).getLanguage().equals("am")
            && attrs.get(Attributes.CALENDAR_TYPE, CalendarText.ISO_CALENDAR_TYPE).equals("ethiopic")
        ) {
            defaultNumberSystem = NumberSystem.ETHIOPIC;
        }

        return attrs.get(Attributes.NUMBER_SYSTEM, defaultNumberSystem);

    }

}
