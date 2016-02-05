/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TwoDigitYearProcessor.java) is part of project Time4J.
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

import net.time4j.base.GregorianMath;
import net.time4j.base.MathUtils;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.EpochDays;
import net.time4j.format.Attributes;
import net.time4j.format.Leniency;

import java.io.IOException;
import java.util.Set;


/**
 * <p>Formatroutine zur Verarbeitung von zweistelligen Jahreszahlen. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
final class TwoDigitYearProcessor
    implements FormatProcessor<Integer> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final Integer DEFAULT_PIVOT_YEAR;

    static {
        long mjd =
            EpochDays.MODIFIED_JULIAN_DATE.transform(
                MathUtils.floorDivide(System.currentTimeMillis(), 86400 * 1000),
                EpochDays.UNIX);
        DEFAULT_PIVOT_YEAR =
            Integer.valueOf(GregorianMath.readYear(GregorianMath.toPackedDate(mjd)) + 20);
    }

    //~ Instanzvariablen --------------------------------------------------

    private final ChronoElement<Integer> element;

    // quick path optimization
    private final int reserved;
    private final char zeroDigit;
    private final Leniency lenientMode;
    private final int protectedLength;
    private final int pivotYear;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruiert eine neue benutzerdefinierte Formatverarbeitung. </p>
     *
     * @param   element     year element to be formatted
     * @throws  IllegalArgumentException if no year element is given
     */
    TwoDigitYearProcessor(ChronoElement<Integer> element) {
        super();

        if (element.name().startsWith("YEAR")) {
            this.element = element;
        } else {
            throw new IllegalArgumentException(
                "Year element required: " + element);
        }

        this.reserved = 0;
        this.zeroDigit = '0';
        this.lenientMode = Leniency.SMART;
        this.protectedLength = 0;
        this.pivotYear = DEFAULT_PIVOT_YEAR;

    }

    private TwoDigitYearProcessor(
        ChronoElement<Integer> element,
        int reserved,
        char zeroDigit,
        Leniency lenientMode,
        int protectedLength,
        int pivotYear
    ) {
        super();

        this.element = element;
        this.reserved = reserved;
        this.zeroDigit = zeroDigit;
        this.lenientMode = lenientMode;
        this.protectedLength = protectedLength;
        this.pivotYear = pivotYear;

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

        int year = formattable.getInt(this.element);

        if (year < 0) {
            throw new IllegalArgumentException(
                "Negative year cannot be printed as two-digit-year: " + year);
        }

        int yy = MathUtils.floorModulo(year, 100);
        String digits = Integer.toString(yy);

        char zeroChar = (
            quickPath
                ? this.zeroDigit
                : attributes.get(Attributes.ZERO_DIGIT, Character.valueOf('0')).charValue());

        if (zeroChar != '0') {
            int diff = zeroChar - '0';
            char[] characters = digits.toCharArray();

            for (int i = 0; i < characters.length; i++) {
                characters[i] = (char) (characters[i] + diff);
            }

            digits = new String(characters);
        }

        int start = -1;
        int printed = 0;

        if (buffer instanceof CharSequence) {
            start = ((CharSequence) buffer).length();
        }

        if (yy < 10) {
            buffer.append(zeroChar);
            printed++;
        }

        buffer.append(digits);
        printed += digits.length();

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
            status.setError(
                start,
                "Missing digits for: " + this.element.name());
            status.setWarning();
            return;
        }

        Leniency leniency = (quickPath ? this.lenientMode : attributes.get(Attributes.LENIENCY, Leniency.SMART));
        int effectiveMax = leniency.isStrict() ? 2 : 9;

        char zeroChar = (
            quickPath
                ? this.zeroDigit
                : attributes.get(Attributes.ZERO_DIGIT, Character.valueOf('0')).charValue());

        if ((this.reserved > 0) && (protectedChars <= 0)) {
            int digitCount = 0;

            // Wieviele Ziffern hat der ganze Ziffernblock?
            for (int i = start; i < len; i++) {
                int digit = text.charAt(i) - zeroChar;

                if ((digit >= 0) && (digit <= 9)) {
                    digitCount++;
                } else {
                    break;
                }
            }

            effectiveMax = Math.min(effectiveMax, digitCount - this.reserved);
        }

        int minPos = start + 2;
        int maxPos = Math.min(len, start + effectiveMax);
        int yearOfCentury = 0;
        boolean first = true;
        int pos = start;

        while (pos < maxPos) {
            int digit = text.charAt(pos) - zeroChar;

            if ((digit >= 0) && (digit <= 9)) {
                yearOfCentury = yearOfCentury * 10 + digit;
                pos++;
                first = false;
            } else if (first) {
                status.setError(start, "Digit expected.");
                return;
            } else {
                break;
            }
        }

        if (pos < minPos) {
            status.setError(
                start,
                "Not enough digits found for: " + this.element.name());
            return;
        }

        int value;

        if (pos == start + 2) {
            int py = (quickPath ? this.pivotYear : attributes.get(Attributes.PIVOT_YEAR, DEFAULT_PIVOT_YEAR));
            assert ((yearOfCentury >= 0) && (yearOfCentury <= 99));
            value = toYear(yearOfCentury, py);
        } else {
            value = yearOfCentury; // absolutes Jahr (kein Kippjahr)
        }

        parsedResult.put(this.element, value);
        status.setPosition(pos);

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof TwoDigitYearProcessor) {
            TwoDigitYearProcessor that = (TwoDigitYearProcessor) obj;
            return this.element.equals(that.element);
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return this.element.hashCode();

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(64);
        sb.append(this.getClass().getName());
        sb.append("[element=");
        sb.append(this.element.name());
        sb.append(']');
        return sb.toString();

    }

    @Override
    public ChronoElement<Integer> getElement() {

        return this.element;

    }

    @Override
    public FormatProcessor<Integer> withElement(ChronoElement<Integer> e) {

        if (this.element == e) {
            return this;
        }

        return new TwoDigitYearProcessor(e);

    }

    @Override
    public boolean isNumerical() {

        return true;

    }

    @Override
    public FormatProcessor<Integer> quickPath(
        AttributeQuery attributes,
        int reserved
    ) {

        return new TwoDigitYearProcessor(
            this.element,
            reserved,
            attributes.get(Attributes.ZERO_DIGIT, Character.valueOf('0')).charValue(),
            attributes.get(Attributes.LENIENCY, Leniency.SMART),
            attributes.get(Attributes.PROTECTED_CHARACTERS, 0).intValue(),
            attributes.get(Attributes.PIVOT_YEAR, DEFAULT_PIVOT_YEAR).intValue()
        );

    }

    private static int toYear(
        int yearOfCentury,
        int pivotYear
    ) {

        int century;

        if (yearOfCentury >= (pivotYear % 100)) {
            century = (((pivotYear / 100) - 1) * 100);
        } else {
            century = ((pivotYear / 100) * 100);
        }

        return (century + yearOfCentury);

    }

}
