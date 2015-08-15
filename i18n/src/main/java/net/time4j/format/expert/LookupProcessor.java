/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
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
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * <p>Text-Formatierung eines chronologischen Elements mit Hilfe von
 * String-Ressourcen. </p>
 *
 * @param   <V> generic type of element values
 * @author  Meno Hochschild
 * @since   3.0
 */
final class LookupProcessor<V extends Enum<V>>
    implements FormatProcessor<V> {

    //~ Instanzvariablen ----------------------------------------------

    private final ChronoElement<V> element;
    private final Map<V, String> resources;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruiert eine neue Instanz. </p>
     *
     * @param   element     element to be formatted
     * @param   resources   text resources
     */
    LookupProcessor(
        ChronoElement<V> element,
        Map<V, String> resources
    ) {
        super();

        Map<V, String> tmp = new EnumMap<V, String>(element.getType());
        tmp.putAll(resources);

        this.element = element;
        this.resources = Collections.unmodifiableMap(tmp);

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public void print(
        ChronoDisplay formattable,
        Appendable buffer,
        AttributeQuery attributes,
        Set<ElementPosition> positions,
        FormatStep step
    ) throws IOException {

        if (buffer instanceof CharSequence) {
            CharSequence cs = (CharSequence) buffer;
            int offset = cs.length();
            this.print(formattable, buffer);

            if (positions != null) {
                positions.add(
                    new ElementPosition(this.element, offset, cs.length()));
            }
        } else {
            this.print(formattable, buffer);
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

        int start = status.getPosition();
        int len = text.length();

        int protectedChars =
            step.getAttribute(
                Attributes.PROTECTED_CHARACTERS,
                attributes,
                0
            ).intValue();

        if (protectedChars > 0) {
            len -= protectedChars;
        }

        if (start >= len) {
            status.setError(start, "Missing chars for: " + this.element.name());
            status.setWarning();
            return;
        }

        Class<V> type = this.element.getType();
        AttributeQuery query = step.getQuery(attributes);
        boolean caseInsensitive =
            query.get(
                Attributes.PARSE_CASE_INSENSITIVE,
                Boolean.TRUE
            ).booleanValue();
        Locale locale = query.get(Attributes.LANGUAGE, Locale.getDefault());
        int maxCount = len - start;

        for (V value : type.getEnumConstants()) {
            String test = this.getString(value);

            if (caseInsensitive) {
                String upper = test.toUpperCase(locale);
                int count = test.length();

                if (count <= maxCount) {
                    String s =
                        text.subSequence(start, start + count)
                            .toString()
                            .toUpperCase(locale);
                    if (upper.equals(s)) {
                        parsedResult.put(this.element, value);
                        status.setPosition(start + count);
                        return;
                    }
                }
            } else {
                int count = test.length();

                if (count <= maxCount) {
                    CharSequence cs =
                        text.subSequence(start, start + count);

                    if (test.equals(cs.toString())) {
                        parsedResult.put(this.element, value);
                        status.setPosition(start + count);
                        return;
                    }
                }
            }

        }

        status.setError(start, "Enum value could not be parsed.");

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

        return new LookupProcessor<V>(element, this.resources);

    }

    @Override
    public boolean isNumerical() {

        return false;

    }

    private void print(
        ChronoDisplay formattable,
        Appendable buffer
    ) throws IOException {

        V value = formattable.get(this.element);
        buffer.append(this.getString(value));

    }

    private String getString(V value) {

        String test = this.resources.get(value);

        if (test == null) { // Ersatzwert, wenn keine Ressource da ist
            test = value.toString();
        }

        return test;

    }

}
