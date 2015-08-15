/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (OrdinalProcessor.java) is part of project Time4J.
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
import net.time4j.format.NumberType;
import net.time4j.format.PluralCategory;
import net.time4j.format.PluralRules;

import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * <p>Ordinalzahl-Formatierung eines chronologischen Elements. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
final class OrdinalProcessor
    implements FormatProcessor<Integer> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final Map<PluralCategory, String> ENGLISH_ORDINALS;

    static {
        Map<PluralCategory, String> map =
            new EnumMap<PluralCategory, String>(PluralCategory.class);
        map.put(PluralCategory.ONE, "st");
        map.put(PluralCategory.TWO, "nd");
        map.put(PluralCategory.FEW, "rd");
        map.put(PluralCategory.OTHER, "th");
        ENGLISH_ORDINALS = Collections.unmodifiableMap(map);
    }

    //~ Instanzvariablen --------------------------------------------------

    private final ChronoElement<Integer> element;
    private final Map<PluralCategory, String> indicators; // null = english

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruiert eine neue Instanz. </p>
     *
     * @param   element         element to be formatted
     * @param   indicators      ordinal indicators to be used as suffixes
     * @throws  IllegalArgumentException in case of inconsistencies
     */
    OrdinalProcessor(
        ChronoElement<Integer> element,
        Map<PluralCategory, String> indicators
    ) {
        super();

        if (element == null) {
            throw new NullPointerException("Missing element.");
        }

        this.element = element;

        if (indicators == null) {
            this.indicators = null;
        } else if (!indicators.containsKey(PluralCategory.OTHER)) {
            throw new IllegalArgumentException(
                "Missing plural category OTHER: " + indicators);
        } else {
            this.indicators =
                Collections.unmodifiableMap(
                    new EnumMap<PluralCategory, String>(indicators));
        }

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public void print(
        ChronoDisplay formattable,
        Appendable buffer,
        AttributeQuery attributes,
        Set<ElementPosition> positions, // optional
        FormatStep step
    ) throws IOException {

        int value = formattable.get(this.element).intValue();

        if (value < 0) {
            throw new IllegalArgumentException(
                "Cannot format negative ordinal numbers: " + formattable);
        }

        String digits = Integer.toString(value);

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

        buffer.append(digits);
        printed += digits.length();

        String indicator = this.getIndicator(step, attributes, value);
        buffer.append(indicator);
        printed += indicator.length();

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
        AttributeQuery attributes,
        Map<ChronoElement<?>, Object> parsedResult,
        FormatStep step
    ) {

        Leniency leniency =
            step.getAttribute(
                Attributes.LENIENCY,
                attributes,
                Leniency.SMART);

        int effectiveMin = 1;
        int effectiveMax = 9;

        int len = text.length();
        int start = status.getPosition();
        int pos = start;

        int protectedChars =
            step.getAttribute(
                Attributes.PROTECTED_CHARACTERS,
                attributes,
                0
            ).intValue();

        if (protectedChars > 0) {
            len -= protectedChars;
        }

        if (pos >= len) {
            status.setError(pos, "Missing digits for: " + this.element.name());
            status.setWarning();
            return;
        }

        char zeroDigit =
            step.getAttribute(
                Attributes.ZERO_DIGIT,
                attributes,
                Character.valueOf('0')
            ).charValue();

        int reserved = step.getReserved();

        if (
            (reserved > 0)
            && (protectedChars <= 0)
        ) {
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

            effectiveMax = Math.min(effectiveMax, digitCount - reserved);
        }

        int minPos = pos + effectiveMin;
        int maxPos = Math.min(len, pos + effectiveMax);
        long total = 0;
        boolean first = true;

        while (pos < maxPos) {
            int digit = text.charAt(pos) - zeroDigit;

            if ((digit >= 0) && (digit <= 9)) {
                total = total * 10 + digit;
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

        int value =  (int) total; // safe (see effectiveMax)
        String indicator = this.getIndicator(step, attributes, value);
        int endPos = pos + indicator.length();

        if (endPos >= len) {
            status.setError(
                pos,
                "Missing or wrong ordinal indicator for: "
                + this.element.name());
            return;
        }

        String test = text.subSequence(pos, endPos).toString();

        if (test.equals(indicator)) {
            pos = endPos;
        } else if (!leniency.isLax()) {
            status.setError(
                pos,
                "Wrong ordinal indicator for: "
                + this.element.name()
                + " (expected=" + indicator + ", found=" + test + ")");
            return;
        }

        parsedResult.put(this.element, Integer.valueOf(value));
        status.setPosition(pos);

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof OrdinalProcessor) {
            OrdinalProcessor that = (OrdinalProcessor) obj;
            return (
                this.element.equals(that.element)
                && this.getIndicators().equals(that.getIndicators())
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return (
            7 * this.element.hashCode()
            + 31 * this.getIndicators().hashCode()
        );

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(64);
        sb.append(this.getClass().getName());
        sb.append("[element=");
        sb.append(this.element.name());
        sb.append(", indicators=");
        sb.append(this.getIndicators());
        sb.append(']');
        return sb.toString();

    }

    private String getIndicator(
        FormatStep step,
        AttributeQuery attributes,
        int value
    ) {

        Locale lang;

        if (this.isEnglish()) {
            lang = Locale.ENGLISH;
        } else {
            lang = step.getAttribute(Attributes.LANGUAGE, attributes, Locale.ROOT);
        }

        PluralCategory category =
            PluralRules.of(lang, NumberType.ORDINALS).getCategory(value);

        if (!this.getIndicators().containsKey(category)) {
            category = PluralCategory.OTHER;
        }

        return this.getIndicators().get(category);

    }

    private boolean isEnglish() {

        return (this.indicators == null);

    }

    private Map<PluralCategory, String> getIndicators() {

        if (this.isEnglish()) {
            return ENGLISH_ORDINALS;
        }

        return this.indicators;

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

        return new OrdinalProcessor(e, this.indicators);

    }

    @Override
    public boolean isNumerical() {

        // there is also a string suffix!
        return false;

    }

}
