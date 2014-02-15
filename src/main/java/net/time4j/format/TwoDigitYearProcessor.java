/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TwoDigitYearProcessor.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Time4J. If not, see <http://www.gnu.org/licenses/>.
 * -----------------------------------------------------------------------
 */

package net.time4j.format;

import net.time4j.base.GregorianMath;
import net.time4j.base.MathUtils;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.EpochDays;

import java.io.IOException;
import java.util.Map;
import java.util.Set;


/**
 * <p>Formatroutine zur Verarbeitung von zweistelligen Jahreszahlen. </p>
 *
 * @author      Meno Hochschild
 * @concurrency <immutable>
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
            Integer.valueOf(
                GregorianMath.readYear(GregorianMath.toPackedDate(mjd)) + 20);
    }

    //~ Instanzvariablen --------------------------------------------------

    private final ChronoElement<Integer> element;

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

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public void print(
        ChronoEntity<?> formattable,
        Appendable buffer,
        Attributes attributes,
        Set<ElementPosition> positions, // optional
        FormatStep step
    ) throws IOException {

        int year = formattable.get(this.element).intValue();

        if (year < 0) {
            throw new IllegalArgumentException(
                "Negative year cannot be printed as two-digit-year: " + year);
        }

        int yy = MathUtils.floorModulo(year, 100);
        String digits = Integer.toString(yy);

        char zeroDigit =
            step.getAttribute(
                Attributes.ZERO_DIGIT,
                attributes,
                Character.valueOf('0'))
            .charValue();

        if (zeroDigit != '0') {
            int diff = zeroDigit - '0';
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
            buffer.append(zeroDigit);
            printed++;
        }

        buffer.append(digits);
        printed += digits.length();

        if (
            (start != -1)
            && (printed > 0)
            && (positions != null)
        ) {
            positions.add(
                new ElementPosition(this.element, start, start + printed));
        }

    }

    @Override
    public void parse(
        CharSequence text,
        ParseLog status,
        Attributes attributes,
        Map<ChronoElement<?>, Object> parsedResult,
        FormatStep step
    ) {

        int len = text.length();
        int start = status.getPosition();
        int pos = start;

        if (pos >= len) {
            status.setError(pos, "Missing digits for: " + this.element.name());
            return;
        }

        char zeroDigit =
            step.getAttribute(
                Attributes.ZERO_DIGIT,
                attributes,
                Character.valueOf('0')
            ).charValue();

        int reserved = step.getReserved();
        int effectiveMax = 2;

        if (reserved > 0) {
            int digitCount = 0;

            // Wieviele Ziffern hat der ganze Ziffernblock?
            for (int i = pos; i < len; i++) {
                int digit = text.charAt(i) - zeroDigit;

                if ((digit >= 0) && (digit <= 9)) {
                    digitCount++;
                } else {
                    break;
                }
            }

            effectiveMax = Math.min(2, digitCount - reserved);
        }

        int minPos = pos + 2;
        int maxPos = Math.min(len, pos + effectiveMax);
        int yearOfCentury = 0;
        boolean first = true;

        while (pos < maxPos) {
            int digit = text.charAt(pos) - zeroDigit;

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
            status.setError(start, "Not enough digits found.");
            return;
        }

        assert ((yearOfCentury >= 0) && (yearOfCentury <= 99));

        int pivotYear =
            step.getAttribute(
                Attributes.PIVOT_YEAR,
                attributes,
                DEFAULT_PIVOT_YEAR);

        int value = toYear(yearOfCentury, pivotYear);
        parsedResult.put(this.element, Integer.valueOf(value));
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
