/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (LookupProcessor.java) is part of project Time4J.
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

import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;


/**
 * <p>Text-Formatierung eines chronologischen Elements mit Hilfe von
 * String-Ressourcen. </p>
 *
 * @param   <V> generic type of element values
 * @author  Meno Hochschild
 * @since   3.0
 */
final class LookupProcessor<V>
    implements FormatProcessor<V> {

    //~ Instanzvariablen ----------------------------------------------

    private final ChronoElement<V> element;
    private final Map<V, String> resources;

    // quick path optimization
    private final int protectedLength;
    private final boolean caseInsensitive;
    private final Locale locale;

    //~ Konstruktoren -----------------------------------------------------

    private LookupProcessor(
        ChronoElement<V> element,
        Map<V, String> resources
    ) {
        super();

        this.element = element;
        this.resources = Collections.unmodifiableMap(resources);

        this.protectedLength = 0;
        this.caseInsensitive = true;
        this.locale = Locale.getDefault(Locale.Category.FORMAT);

    }

    private LookupProcessor(
        ChronoElement<V> element,
        Map<V, String> resources,
        int protectedLength,
        boolean caseInsensitive,
        Locale locale
    ) {
        super();

        this.element = element;
        this.resources = resources;

        // quick path members
        this.protectedLength = protectedLength;
        this.caseInsensitive = caseInsensitive;
        this.locale = locale;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Konstruiert eine neue Instanz mit Hilfe einer {@code Map}. </p>
     *
     * @param   element     element to be formatted
     * @param   resources   text resources
     * @throws  IllegalArgumentException if there not enough text resources to match all values of an enum element type
     */
    static <V> LookupProcessor create(
        ChronoElement<V> element,
        Map<V, String> resources
    ) {

        Map<V, String> map;
        Class<V> keyType = element.getType();

        if (keyType.isEnum()) {
            if (resources.size() < keyType.getEnumConstants().length) {
                throw new IllegalArgumentException("Not enough text resources defined for enum: " + keyType.getName());
            }
            map = createMap(keyType);
        } else {
            map = new HashMap<>(resources.size());
        }

        map.putAll(resources);
        return new LookupProcessor<>(element, map);

    }

    /**
     * <p>Konstruiert eine neue Instanz mit Hilfe einer Konvertierfunktion. </p>
     *
     * @param   element     element to be formatted
     * @param   converter   text converter
     * @since   5.0
     */
    static <V extends Enum<V>> LookupProcessor create(
        ChronoElement<V> element,
        Function<V, String> converter
    ) {

        Map<V, String> map = new EnumMap<>(element.getType());

        for (V value : element.getType().getEnumConstants()) {
            map.put(value, converter.apply(value));
        }

        return new LookupProcessor<>(element, map);

    }

    @Override
    public int print(
        ChronoDisplay formattable,
        Appendable buffer,
        AttributeQuery attributes,
        Set<ElementPosition> positions,
        boolean quickPath
    ) throws IOException {

        if (buffer instanceof CharSequence) {
            CharSequence cs = (CharSequence) buffer;
            int offset = cs.length();
            int printed = this.print(formattable, buffer);
            if (positions != null) {
                positions.add(new ElementPosition(this.element, offset, cs.length()));
            }
            return printed;
        } else {
            return this.print(formattable, buffer);
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

        int start = status.getPosition();
        int len = text.length();
        int protectedChars = (quickPath ? this.protectedLength : attributes.get(Attributes.PROTECTED_CHARACTERS, 0));

        if (protectedChars > 0) {
            len -= protectedChars;
        }

        if (start >= len) {
            status.setError(start, "Missing chars for: " + this.element.name());
            status.setWarning();
            return;
        }

        boolean ignoreCase = (
            quickPath
                ? this.caseInsensitive
                : attributes.get(Attributes.PARSE_CASE_INSENSITIVE, Boolean.TRUE).booleanValue());
        Locale loc = (
            quickPath
                ? this.locale
                : attributes.get(Attributes.LANGUAGE, Locale.getDefault(Locale.Category.FORMAT)));
        int maxCount = len - start;

        for (V value : this.resources.keySet()) {
            String test = this.getString(value);

            if (ignoreCase) {
                String upper = test.toUpperCase(loc);
                int count = test.length();

                if (count <= maxCount) {
                    String s = text.subSequence(start, start + count).toString().toUpperCase(loc);

                    if (upper.equals(s)) {
                        parsedResult.put(this.element, value);
                        status.setPosition(start + count);
                        return;
                    }
                }
            } else {
                int count = test.length();

                if (count <= maxCount) {
                    CharSequence cs = text.subSequence(start, start + count);

                    if (test.equals(cs.toString())) {
                        parsedResult.put(this.element, value);
                        status.setPosition(start + count);
                        return;
                    }
                }
            }
        }

        status.setError(start, "Element value could not be parsed: " + this.element.name());

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof LookupProcessor) {
            LookupProcessor<?> that = (LookupProcessor) obj;
            return (
                this.element.equals(that.element)
                && this.resources.equals(that.resources)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return (
            7 * this.element.hashCode()
            + 31 * this.resources.hashCode()
        );

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(512);
        sb.append(this.getClass().getName());
        sb.append("[element=");
        sb.append(this.element.name());
        sb.append(", resources=");
        sb.append(this.resources);
        sb.append(']');
        return sb.toString();

    }

    @Override
    public ChronoElement<V> getElement() {

        return this.element;

    }

    @Override
    public FormatProcessor<V> withElement(ChronoElement<V> element) {

        if (this.element == element) {
            return this;
        }

        return new LookupProcessor<>(element, this.resources);

    }

    @Override
    public boolean isNumerical() {

        return false;

    }

    @Override
    public FormatProcessor<V> quickPath(
        ChronoFormatter<?> formatter,
        AttributeQuery attributes,
        int reserved
    ) {

        return new LookupProcessor<>(
            this.element,
            this.resources,
            attributes.get(Attributes.PROTECTED_CHARACTERS, Integer.valueOf(0)).intValue(),
            attributes.get(Attributes.PARSE_CASE_INSENSITIVE, Boolean.TRUE).booleanValue(),
            attributes.get(Attributes.LANGUAGE, Locale.getDefault(Locale.Category.FORMAT))
        );

    }

    private int print(
        ChronoDisplay formattable,
        Appendable buffer
    ) throws IOException {

        V value = formattable.get(this.element);
        String text = this.getString(value);
        buffer.append(text);
        return text.length();

    }

    private String getString(V value) {

        String test = this.resources.get(value);

        if (test == null) { // Ersatzwert, wenn keine Ressource da ist
            test = value.toString();
        }

        return test;

    }

    @SuppressWarnings("unchecked")
    private static <V, K extends Enum<K>> Map<V, String> createMap(Class<V> keyType) {

        Class<K> clazz = (Class<K>) keyType;
        return (Map<V, String>) new EnumMap<K, String>(clazz);

    }

}
