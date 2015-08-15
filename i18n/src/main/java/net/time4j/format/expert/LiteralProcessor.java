/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (LiteralProcessor.java) is part of project Time4J.
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

import net.time4j.engine.AttributeKey;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.format.Attributes;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * <p>Formatiert ein Literal. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
final class LiteralProcessor
    implements FormatProcessor<Void> {

    //~ Instanzvariablen --------------------------------------------------

    private final char single;
    private final char alt;
    private final String multi;
    private final AttributeKey<Character> attribute;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruktor f&uuml;r eine feste Literalzeichenfolge. </p>
     *
     * @param   literal     literal char sequence
     * @throws  IllegalArgumentException in case of inconsistencies
     */
    LiteralProcessor(String literal) {
        super();

        if (literal.isEmpty()) {
            throw new IllegalArgumentException("Missing literal.");
        }

        this.single = literal.charAt(0);
        this.alt = this.single;
        this.attribute = null;
        this.multi = ((literal.length() == 1) ? null : literal);

        if (this.single < ' ') {
            throw new IllegalArgumentException(
                "Literal must not start with non-printable char.");
        }

    }

    /**
     * <p>Konstruktor f&uuml;r ein einzelnes Zeichen mit Alternative. </p>
     *
     * @param   literal     preferred literal char
     * @param   alt         alternative literal char for parsing
     * @throws  IllegalArgumentException in case of inconsistencies
     * @since   3.1
     */
    LiteralProcessor(
        char literal,
        char alt
    ) {
        super();

        this.single = literal;
        this.alt = alt;
        this.attribute = null;
        this.multi = null;

        if ((literal < ' ') || (alt < ' ')) {
            throw new IllegalArgumentException(
                "Literal must not start with non-printable char.");
        }

    }

    /**
     * <p>Konstruktor f&uuml;r ein Literalzeichen, das in einem Attribut
     * enthalten ist. </p>
     *
     * @param   attribute       attribute key
     */
    LiteralProcessor(AttributeKey<Character> attribute) {
        super();

        if (attribute == null) {
            throw new NullPointerException("Missing format attribute.");
        }

        this.single = '\u0000';
        this.alt = this.single;
        this.attribute = attribute;
        this.multi = null;

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

        if (this.attribute != null) {
            char literal =
                step.getAttribute(
                    this.attribute,
                    attributes,
                    null
                ).charValue();
            buffer.append(literal);
        } else if (this.multi == null) {
            buffer.append(this.single);
        } else {
            buffer.append(this.multi);
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

        if (this.multi == null) {
            this.parseChar(text, status, attributes, step);
        } else {
            this.parseMulti(text, status, attributes, step);
        }

    }

    private void parseChar(
        CharSequence text,
        ParseLog status,
        AttributeQuery attributes,
        FormatStep step
    ) {

        int offset = status.getPosition();
        boolean error = false;
        char c = '\u0000';
        char literal = this.single;

        if (this.attribute != null) {
            literal =
                step.getAttribute(
                    this.attribute,
                    attributes,
                    Character.valueOf('\u0000')
                ).charValue();
        }

        if (
            (offset >= text.length())
            || (literal == '\u0000')
        ) {
            error = true;
        } else {
            c = text.charAt(offset);
            char alternative = this.alt;

            if (
                (this.attribute != null)
                && Attributes.DECIMAL_SEPARATOR.name().equals(this.attribute.name())
                && Locale.ROOT.equals(attributes.get(Attributes.LANGUAGE, Locale.ROOT))
            ) { // Spezialfall: ISO-8601
                alternative = (
                    (literal == ',')
                    ? '.'
                    : ((literal == '.') ? ',' : literal)
                );
            }

            boolean caseInsensitive =
                step.getAttribute(
                    Attributes.PARSE_CASE_INSENSITIVE,
                    attributes,
                    Boolean.TRUE
                ).booleanValue();

            if (caseInsensitive) {
                if (
                    !charEqualsIgnoreCase(c, literal)
                    && !charEqualsIgnoreCase(c, alternative)
                ) {
                    error = true;
                }
            } else {
                error = ((c != literal) && (c != alternative));
            }
        }

        if (error) {
            StringBuilder msg = new StringBuilder("Expected: [");
            msg.append(literal);
            msg.append("], found: [");
            if (c != '\u0000') {
                msg.append(c);
            }
            msg.append(']');
            status.setError(offset, msg.toString());
        } else {
            status.setPosition(offset + 1);
        }

    }

    private void parseMulti(
        CharSequence text,
        ParseLog status,
        AttributeQuery attributes,
        FormatStep step
    ) {

        int offset = status.getPosition();
        boolean error = false;
        int len = this.multi.length();
        String compare = "";

        if (offset >= text.length()) {
            error = true;
        } else if (offset + len > text.length()) {
            error = true;
            compare = text.subSequence(offset, text.length()).toString();
        }

        if (!error) {
            boolean caseInsensitive =
                step.getAttribute(
                    Attributes.PARSE_CASE_INSENSITIVE,
                    attributes,
                    Boolean.TRUE
                ).booleanValue();

            if (caseInsensitive) {
                if (!subSequenceEqualsIgnoreCase(text, offset, this.multi, 0, len)) {
                    error = true;
                    compare = text.subSequence(offset, offset + len).toString();
                }
            } else {
                for (int i = 0; i < len; i++) {
                    if (this.multi.charAt(i) != text.charAt(i + offset)) {
                        error = true;
                        compare = text.subSequence(offset, offset + len).toString();
                        break;
                    }
                }
            }
        }

        if (error) {
            StringBuilder msg = new StringBuilder("Expected: [");
            msg.append(this.multi);
            msg.append("], found: [");
            msg.append(compare);
            msg.append(']');
            status.setError(offset, msg.toString());
        } else {
            status.setPosition(offset + len);
        }

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof LiteralProcessor) {
            LiteralProcessor that = (LiteralProcessor) obj;
            if (this.attribute != null) {
                return this.attribute.equals(that.attribute);
            } else if (this.multi == null) {
                return (
                    (that.multi == null)
                    && (this.single == that.single)
                    && (this.alt == that.alt)
                );
            } else {
                return this.multi.equals(that.multi);
            }
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        String ref = (
            (this.attribute == null)
            ? ((this.multi == null) ? "" : this.multi)
            : this.attribute.name());

        return (this.single ^ ref.hashCode());

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName());
        sb.append("[literal=");
        if (this.attribute != null) {
            sb.append('{');
            sb.append(this.attribute);
            sb.append('}');
        } else if (this.multi == null) {
            sb.append(this.single);
            if (this.alt != this.single) {
                sb.append(", alternative=");
                sb.append(this.alt);
            }
        } else {
            sb.append(this.multi);
        }
        sb.append(']');
        return sb.toString();

    }

    // optional
    @Override
    public ChronoElement<Void> getElement() {

        return null;

    }

    @Override
    public FormatProcessor<Void> withElement(ChronoElement<Void> element) {

        return this;

    }

    @Override
    public boolean isNumerical() {

        return false;

    }

    private static boolean charEqualsIgnoreCase(
        char c1,
        char c2
    ) {

        return (
            (c1 == c2)
            || (Character.toUpperCase(c1) == Character.toUpperCase(c2))
            || (Character.toLowerCase(c1) == Character.toLowerCase(c2))
        );

    }

    private static boolean subSequenceEqualsIgnoreCase(
        CharSequence sequence1,
        int offset1,
        CharSequence sequence2,
        int offset2,
        int len
    ) {

        int l1 = sequence1.length();
        int l2 = sequence2.length();

        for (int i = 0; i < len; i++) {
            if (i + offset1 >= l1) {
                return (i + offset2 >= l2);
            } else if (i + offset2 >= l2) {
                return false;
            }

            char c1 = sequence1.charAt(i + offset1);
            char c2 = sequence2.charAt(i + offset2);

            if (!charEqualsIgnoreCase(c1, c2)) {
                return false;
            }
        }

        return true;

    }

}
