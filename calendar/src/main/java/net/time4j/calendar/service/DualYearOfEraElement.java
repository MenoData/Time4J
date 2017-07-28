/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (DualYearOfEraElement.java) is part of project Time4J.
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

package net.time4j.calendar.service;

import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.format.Attributes;
import net.time4j.format.Leniency;
import net.time4j.format.NumberSystem;
import net.time4j.format.TextWidth;
import net.time4j.format.internal.DualFormatElement;

import java.io.IOException;
import java.text.ParsePosition;


/**
 * <p>Supports special year formatting. </p>
 *
 * @param   <T> generic chronological type
 * @author  Meno Hochschild
 * @since   3.33/4.28
 */
public abstract class DualYearOfEraElement<T extends ChronoEntity<T>>
    extends StdIntegerDateElement<T>
    implements DualFormatElement {

    //~ Konstruktoren -----------------------------------------------------

    /**
     * For subclasses.
     *
     * @param   chronoType  reified chronological type
     * @param   defaultMin  the default minimum year
     * @param   defaultMax  the default maximum year
     * @param   symbol      the format symbol used in patterns
     */
    protected DualYearOfEraElement(
        Class<T> chronoType,
        int defaultMin,
        int defaultMax,
        char symbol
    ) {
        super("YEAR_OF_ERA", chronoType, defaultMin, defaultMax, symbol, null, null);

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public void print(
        ChronoDisplay context,
        Appendable buffer,
        AttributeQuery attributes
    ) throws IOException, ChronoException {

        NumberSystem numsys = getNumberSystem(attributes);
        TextWidth width = attributes.get(Attributes.TEXT_WIDTH, TextWidth.NARROW);
        int minDigits;
        switch (width) {
            case NARROW:
                minDigits = 1;
                break;
            case SHORT:
                minDigits = 2;
                break;
            case ABBREVIATED:
                minDigits = 3;
                break;
            default:
                minDigits = 4;
        }
        char zeroChar = (
            attributes.contains(Attributes.ZERO_DIGIT)
                ? attributes.get(Attributes.ZERO_DIGIT).charValue()
                : (numsys.isDecimal() ? numsys.getDigits().charAt(0) : '0'));
        this.print(context, buffer, attributes, numsys, zeroChar, minDigits, 9);

    }

    @Override
    public void print(
        ChronoDisplay context,
        Appendable buffer,
        AttributeQuery attributes,
        NumberSystem numsys,
        char zeroChar,
        int minDigits,
        int maxDigits
    ) throws IOException, ChronoException {

        int num = context.getInt(this);
        String s = numsys.toNumeral(num);
        if (numsys.isDecimal()) {
            int len = s.length();
            for (int i = 0, n = minDigits - len; i < n; i++) {
                buffer.append(zeroChar);
            }
        }
        buffer.append(s);

    }

    @Override
    public Integer parse(
        CharSequence text,
        ParsePosition status,
        AttributeQuery attributes
    ) {

        NumberSystem numsys = getNumberSystem(attributes);
        int start = status.getIndex();
        int pos = start;
        char zeroChar = (
            attributes.contains(Attributes.ZERO_DIGIT)
                ? attributes.get(Attributes.ZERO_DIGIT).charValue()
                : (numsys.isDecimal() ? numsys.getDigits().charAt(0) : '0'));
        Leniency leniency =
            (numsys.isDecimal() ? Leniency.SMART : attributes.get(Attributes.LENIENCY, Leniency.SMART));
        int value = 0;

        if (numsys.isDecimal()) {
            for (int i = pos, n = Math.min(pos + 9, text.length()); i < n; i++) {
                int digit = text.charAt(i) - zeroChar;
                if ((digit >= 0) && (digit <= 9)) {
                    value = value * 10 + digit;
                    pos++;
                } else {
                    break;
                }
            }
        } else {
            int len = 0;

            for (int i = pos, n = text.length(); i < n; i++) {
                if (numsys.contains(text.charAt(i))) {
                    len++;
                } else {
                    break;
                }
            }

            if (len > 0) {
                value = numsys.toInteger(text.subSequence(pos, pos + len).toString(), leniency);
                pos += len;
            }
        }

        if (pos == start) {
            status.setErrorIndex(start);
            return null;
        } else {
            status.setIndex(pos);
            return Integer.valueOf(value);
        }

    }

    @Override
    public Integer parse(
        CharSequence text,
        ParsePosition status,
        AttributeQuery attributes,
        ChronoEntity<?> parsedResult
    ) {

        return this.parse(text, status, attributes);

    }

    /**
     * Obtains the number system to be used for year formatting.
     *
     * @param   attributes  format attributes of the embedding formatter
     * @return  number system
     */
    protected abstract NumberSystem getNumberSystem(AttributeQuery attributes);

}
