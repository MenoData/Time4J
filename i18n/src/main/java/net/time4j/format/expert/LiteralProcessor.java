/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
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

    // quick path optimization
    private final boolean caseInsensitive;

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
        this.multi = literal;

        if (this.single < ' ') {
            throw new IllegalArgumentException(
                "Literal must not start with non-printable char.");
        }

        this.caseInsensitive = true;

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

        this.caseInsensitive = true;

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

        this.caseInsensitive = true;

    }

    private LiteralProcessor(
        char single,
        char alt,
        String multi,
        AttributeKey<Character> attribute,
        boolean caseInsensitive
    ) {
        super();

        this.single = single;
        this.alt = alt;
        this.multi = multi;
        this.attribute = attribute;
        this.caseInsensitive = caseInsensitive;

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

        if (this.attribute != null) {
            char literal = attributes.get(this.attribute, null).charValue();
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
        boolean quickPath
    ) {

        if (this.multi == null) {
            this.parseChar(text, status, attributes, quickPath);
        } else {
            this.parseMulti(text, status, attributes, quickPath);
        }

    }

    private void parseChar(
        CharSequence text,
        ParseLog status,
        AttributeQuery attributes,
        boolean quickPath
    ) {

        int offset = status.getPosition();
        boolean error = false;
        char c = '\u0000';
        char literal = this.single;

        if (this.attribute != null) {
            literal = attributes.get(this.attribute, Character.valueOf('\u0000')).charValue();
        }

        if ((offset >= text.length()) || (literal == '\u0000')) {
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

            boolean caseInsensitive = (
                quickPath
                    ? this.caseInsensitive
                    : attributes.get(Attributes.PARSE_CASE_INSENSITIVE, Boolean.TRUE).booleanValue());

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
        boolean quickPath
    ) {

        int offset = status.getPosition();
        int len = this.multi.length();

        boolean caseInsensitive = (
            quickPath
                ? this.caseInsensitive
                : attributes.get(Attributes.PARSE_CASE_INSENSITIVE, Boolean.TRUE).booleanValue());

        int parsedLen = subSequenceEquals(text, offset, this.multi, caseInsensitive);

        if (parsedLen == -1) {
            StringBuilder msg = new StringBuilder("Expected: [");
            msg.append(this.multi);
            msg.append("], found: [");
            msg.append(text.subSequence(offset, Math.min(offset + len, text.length())));
            msg.append(']');
            status.setError(offset, msg.toString());
        } else {
            status.setPosition(offset + parsedLen);
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

    @Override
    public FormatProcessor<Void> quickPath(
        AttributeQuery attributes,
        int reserved
    ) {

        return new LiteralProcessor(
            this.single,
            this.alt,
            this.multi,
            this.attribute,
            attributes.get(Attributes.PARSE_CASE_INSENSITIVE, Boolean.TRUE).booleanValue()
        );

    }

    // also used by LocalizedGMTProcessor
    static int subSequenceEquals(
        CharSequence test,
        int offset,
        CharSequence expected,
        boolean caseInsensitive
    ) {

        int j = 0;
        int max = test.length();
        int len = expected.length();

        for (int i = 0; i < len; i++) {
            char c = '\u0000';
            char exp = expected.charAt(i);

            if (isBidi(exp)) {
                continue;
            }

            while ((j + offset < max) && isBidi(c = test.charAt(j + offset))) {
                j++;
            }

            if (j + offset >= max) {
                return -1;
            } else {
                j++;
            }

            if (caseInsensitive) {
                if (!charEqualsIgnoreCase(c, exp)) {
                    return -1;
                }
            } else if (c != exp) {
                return -1;
            }
        }

        while ((j + offset < max) && isBidi(test.charAt(j + offset))) {
            j++;
        }

        return j;

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

    private static boolean isBidi(char c) {

        return ((c == '\u200E') || (c == '\u200F') || (c == '\u061C')); // LRM, RLM, ALM

    }

}
