/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TextProcessor.java) is part of project Time4J.
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
import net.time4j.engine.ChronoException;
import net.time4j.format.Attributes;
import net.time4j.format.TextElement;

import java.io.IOException;
import java.util.Map;
import java.util.Set;


/**
 * <p>Text-Formatierung eines chronologischen Elements. </p>
 *
 * @param   <V> generic type of element values (String or Enum)
 * @author  Meno Hochschild
 * @since   3.0
 */
final class TextProcessor<V>
    implements FormatProcessor<V> {

    //~ Instanzvariablen ----------------------------------------------

    private final TextElement<V> element;
    private final boolean protectedMode;

    //~ Konstruktoren -----------------------------------------------------

    private TextProcessor(
        TextElement<V> element,
        boolean protectedMode
    ) {
        super();

        if (element == null) {
            throw new NullPointerException("Missing element.");
        }

        this.element = element;
        this.protectedMode = protectedMode;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Konstruiert eine neue Instanz. </p>
     *
     * @param   element     element to be formatted
     * @return  new processor instance
     */
    static <V> TextProcessor<V> create(TextElement<V> element) {

        return new TextProcessor<V>(element, false);

    }

    /**
     * <p>Konstruiert eine neue Instanz. </p>
     *
     * @param   element     element to be formatted
     * @return  new processor instance whose element cannot be changed
     */
    static <V> TextProcessor<V> createProtected(TextElement<V> element) {

        return new TextProcessor<V>(element, true);

    }

    @Override
    public void print(
        ChronoDisplay formattable,
        Appendable buffer,
        AttributeQuery attributes,
        Set<ElementPosition> positions, // optional
        FormatStep step
    ) throws IOException {

        try {
            if (buffer instanceof CharSequence) {
                CharSequence cs = (CharSequence) buffer;
                int offset = cs.length();
                this.element.print(formattable, buffer, step.getQuery(attributes));

                if (positions != null) {
                    positions.add(
                        new ElementPosition(this.element, offset, cs.length()));
                }
            } else {
                this.element.print(formattable, buffer, step.getQuery(attributes));
            }
        } catch (ChronoException ce) {
            throw new IllegalArgumentException(ce);
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

        TextElement<?> te = TextElement.class.cast(this.element);
        Object value = te.parse(text, status.getPP(), step.getQuery(attributes));

        if (status.isError()) {
            Class<V> valueType = this.element.getType();
            if (valueType.isEnum()) {
                status.setError(status.getErrorIndex(), "No suitable enum found: " + valueType.getName());
            } else {
                status.setError(status.getErrorIndex(), "Unparseable element: " + this.element.name());
            }
        } else {
            if (value == null) {
                status.setError(start, "No interpretable value.");
            } else {
                parsedResult.put(this.element, value);
            }
        }

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof TextProcessor) {
            TextProcessor<?> that = (TextProcessor) obj;
            return (
                this.element.equals(that.element)
                && (this.protectedMode == that.protectedMode));
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
        sb.append(",protected-mode=");
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

        if (this.protectedMode || (this.element == element)) {
            return this;
        } else if (element instanceof TextElement) {
            return TextProcessor.create((TextElement<V>) element);
        } else {
            throw new IllegalArgumentException(
                "Text element required: "
                + element.getClass().getName());
        }

    }

    @Override
    public boolean isNumerical() {

        return false;

    }

}
