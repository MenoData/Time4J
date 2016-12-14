/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
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

    // quick path optimization
    private final int reserved;
    private final int protectedLength;
    private final char zeroDigit;
    private final Leniency lenientMode;
    private final Locale locale;

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
        } else {
            this.indicators =
                Collections.unmodifiableMap(
                    new EnumMap<PluralCategory, String>(indicators));
            if (!this.indicators.containsKey(PluralCategory.OTHER)) {
                throw new IllegalArgumentException(
                    "Missing plural category OTHER: " + indicators);
            }
        }

        this.reserved = 0;
        this.protectedLength = 0;
        this.zeroDigit = '0';
        this.lenientMode = Leniency.SMART;
        this.locale = Locale.ROOT;

    }

    private OrdinalProcessor(
        ChronoElement<Integer> element,
        Map<PluralCategory, String> indicators,
        int reserved,
        int protectedLength,
        char zeroDigit,
        Leniency lenientMode,
        Locale locale
    ) {
        super();

        this.element = element;
        this.indicators = indicators;

        // quick path members
        this.reserved = reserved;
        this.protectedLength = protectedLength;
        this.zeroDigit = zeroDigit;
        this.lenientMode = lenientMode;
        this.locale = locale;

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

        int value = formattable.getInt(this.element);

        if (value < 0) {
            if (value == Integer.MIN_VALUE) {
                throw new IllegalArgumentException(
                    "Format context \"" + formattable + "\" without element: " + this.element);
            } else {
                throw new IllegalArgumentException(
                    "Cannot format negative ordinal numbers: " + formattable);
            }
        }

        String digits = Integer.toString(value);

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

        buffer.append(digits);
        printed += digits.length();

        String indicator = this.getIndicator(attributes, quickPath, value);
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
        ParsedEntity<?> parsedResult,
        boolean quickPath
    ) {

        int effectiveMin = 1;
        int effectiveMax = 9;

        int len = text.length();
        int start = status.getPosition();
        int pos = start;

        Leniency leniency = (quickPath ? this.lenientMode : attributes.get(Attributes.LENIENCY, Leniency.SMART));
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

        if ((this.reserved > 0) && (protectedChars <= 0)) {
            int digitCount = 0;

            // Wieviele Ziffern hat der ganze Ziffernblock?
            for (int i = pos; i < len; i++) {
                int digit = text.charAt(i) - zeroChar;

                if ((digit >= 0) && (digit <= 9)) {
                    digitCount++;
                } else {
                    break;
                }
            }

            effectiveMax = Math.min(effectiveMax, digitCount - this.reserved);
        }

        int minPos = pos + effectiveMin;
        int maxPos = Math.min(len, pos + effectiveMax);
        long total = 0;
        boolean first = true;

        while (pos < maxPos) {
            int digit = text.charAt(pos) - zeroChar;

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
        String indicator = this.getIndicator(attributes, quickPath, value);
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

        parsedResult.put(this.element, value);
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
        AttributeQuery attributes,
        boolean quickPath,
        int value
    ) {

        Locale lang;

        if (this.isEnglish()) {
            lang = Locale.ENGLISH;
        } else {
            lang = (quickPath ? this.locale : attributes.get(Attributes.LANGUAGE, Locale.ROOT));
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

    @Override
    public FormatProcessor<Integer> quickPath(
        ChronoFormatter<?> formatter,
        AttributeQuery attributes,
        int reserved
    ) {

        return new OrdinalProcessor(
            this.element,
            this.indicators,
            reserved,
            attributes.get(Attributes.PROTECTED_CHARACTERS, Integer.valueOf(0)).intValue(),
            attributes.get(Attributes.ZERO_DIGIT, Character.valueOf('0')).charValue(),
            attributes.get(Attributes.LENIENCY, Leniency.SMART),
            attributes.get(Attributes.LANGUAGE, Locale.ROOT)
        );

    }

}
