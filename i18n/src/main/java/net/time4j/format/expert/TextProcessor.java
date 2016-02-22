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
import net.time4j.format.Leniency;
import net.time4j.format.OutputContext;
import net.time4j.format.TextElement;
import net.time4j.format.TextWidth;
import net.time4j.format.internal.GregorianTextElement;
import net.time4j.history.internal.HistorizedElement;

import java.io.IOException;
import java.util.Locale;
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

    // quick path optimization
    private final GregorianTextElement<V> gte;
    private final Locale language;
    private final TextWidth tw;
    private final OutputContext oc;
    private final Leniency lenientMode;
    private final int protectedLength;

    //~ Konstruktoren -----------------------------------------------------

    private TextProcessor(
        TextElement<V> element,
        boolean protectedMode,
        Locale language,
        TextWidth tw,
        OutputContext oc,
        Leniency lenientMode,
        int protectedLength
    ) {
        super();

        if (element == null) {
            throw new NullPointerException("Missing element.");
        }

        this.element = element;
        this.protectedMode = protectedMode;

        // quick path members
        this.gte = (element instanceof GregorianTextElement) ? (GregorianTextElement<V>) element : null;
        this.language = language;
        this.tw = tw;
        this.oc = oc;
        this.lenientMode = lenientMode;
        this.protectedLength = protectedLength;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Konstruiert eine neue Instanz. </p>
     *
     * @param   element     element to be formatted
     * @return  new processor instance
     */
    static <V> TextProcessor<V> create(TextElement<V> element) {

        return new TextProcessor<V>(
            element, false, Locale.ROOT, TextWidth.WIDE, OutputContext.FORMAT, Leniency.SMART, 0);

    }

    /**
     * <p>Konstruiert eine neue Instanz. </p>
     *
     * @param   element     element to be formatted
     * @return  new processor instance whose element cannot be changed
     */
    static <V> TextProcessor<V> createProtected(TextElement<V> element) {

        return new TextProcessor<V>(
            element, true, Locale.ROOT, TextWidth.WIDE, OutputContext.FORMAT, Leniency.SMART, 0);

    }

    @Override
    public void print(
        ChronoDisplay formattable,
        Appendable buffer,
        AttributeQuery attributes,
        Set<ElementPosition> positions, // optional
        boolean quickPath
    ) throws IOException {

        try {
            if (buffer instanceof CharSequence) {
                CharSequence cs = (CharSequence) buffer;
                int offset = cs.length();
                this.print(formattable, buffer, attributes, quickPath);

                if (positions != null) {
                    positions.add(new ElementPosition(this.element, offset, cs.length()));
                }
            } else {
                this.print(formattable, buffer, attributes, quickPath);
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
        ParsedValues parsedResult,
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

        Object value;

        if (quickPath && (this.gte != null) && (this.lenientMode != null)) {
            value = this.gte.parse(text, status.getPP(), this.language, this.tw, this.oc, this.lenientMode);
        } else if (this.element instanceof HistorizedElement) {
            value = ((HistorizedElement) this.element).parse(text, status.getPP(), attributes, parsedResult);
        } else {
            value = this.element.parse(text, status.getPP(), attributes);
        }

        if (status.isError()) {
            Class<V> valueType = this.element.getType();
            if (valueType.isEnum()) {
                status.setError(status.getErrorIndex(), "No suitable enum found: " + valueType.getName());
            } else {
                status.setError(status.getErrorIndex(), "Unparseable element: " + this.element.name());
            }
        } else if (value == null) {
            status.setError(start, "No interpretable value.");
        } else {
            parsedResult.put(this.element, value);
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

    @Override
    public FormatProcessor<V> quickPath(
        AttributeQuery attributes,
        int reserved
    ) {

        Leniency leniency = attributes.get(Attributes.LENIENCY, Leniency.SMART);
        boolean multipleContext = attributes.get(Attributes.PARSE_MULTIPLE_CONTEXT, Boolean.TRUE).booleanValue();
        boolean caseInsensitive = attributes.get(Attributes.PARSE_CASE_INSENSITIVE, Boolean.TRUE).booleanValue();
        boolean partialCompare = attributes.get(Attributes.PARSE_PARTIAL_COMPARE, Boolean.FALSE).booleanValue();

        if ((leniency == Leniency.STRICT) && (multipleContext || caseInsensitive || partialCompare)) {
            leniency = null;
        } else if ((leniency == Leniency.SMART) && (!multipleContext || !caseInsensitive || partialCompare)) {
            leniency = null;
        } else if (!multipleContext || !caseInsensitive || !partialCompare) { // lax mode
            leniency = null;
        }

        return new TextProcessor<V>(
            this.element,
            this.protectedMode,
            attributes.get(Attributes.LANGUAGE, Locale.ROOT),
            attributes.get(Attributes.TEXT_WIDTH, TextWidth.WIDE),
            attributes.get(Attributes.OUTPUT_CONTEXT, OutputContext.FORMAT),
            leniency,
            attributes.get(Attributes.PROTECTED_CHARACTERS, 0)
        );

    }

    private void print(
        ChronoDisplay formattable,
        Appendable buffer,
        AttributeQuery attributes,
        boolean quickPath
    ) throws IOException {

        if ((this.gte != null) && quickPath) {
            this.gte.print(formattable, buffer, this.language, this.tw, this.oc);
        } else {
            this.element.print(formattable, buffer, attributes);
        }

    }

}
