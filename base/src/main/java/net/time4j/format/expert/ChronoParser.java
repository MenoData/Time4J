/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2024 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoParser.java) is part of project Time4J.
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
import net.time4j.format.Attributes;

import java.text.ParseException;


/**
 * <p>Interpretes a text as chronological entity. </p>
 *
 * @param   <T> generic type of chronological entity to be parsed
 * @author  Meno Hochschild
 * @since   3.0
 * @see     net.time4j.engine.ChronoEntity
 */
/*[deutsch]
 * <p>Interpretiert einen Text als Entit&auml;t. </p>
 *
 * @param   <T> generic type of chronological entity to be parsed
 * @author  Meno Hochschild
 * @since   3.0
 * @see     net.time4j.engine.ChronoEntity
 */
public interface ChronoParser<T> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Interpretes given text as chronological entity starting
     * at the begin of text. </p>
     *
     * @param   text        text to be parsed
     * @return  parse result
     * @throws  IndexOutOfBoundsException if the text is empty
     * @throws  ParseException if the text is not parseable
     * @since   4.18
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text ab dem Anfang. </p>
     *
     * @param   text        text to be parsed
     * @return  parse result
     * @throws  IndexOutOfBoundsException if the text is empty
     * @throws  ParseException if the text is not parseable
     * @since   4.18
     */
    default T parse(CharSequence text) throws ParseException {

        ParseLog status = new ParseLog();
        T result = this.parse(text, status, this.getAttributes());

        if (result == null) {
            throw new ParseException(
                status.getErrorMessage(),
                status.getErrorIndex()
            );
        }

        return result;

    }

    /**
     * <p>Interpretes given text as chronological entity starting
     * at the specified position in parse log. </p>
     *
     * <p>Equivalent to {@code parse(text, status, getAttributes())}. </p>
     *
     * @param   text        text to be parsed
     * @param   status      parser information (always as new instance)
     * @return  result or {@code null} if parsing does not work
     * @throws  IndexOutOfBoundsException if the start position is at end of text or even behind
     * @since   4.18
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text ab der im Log angegebenen Position. </p>
     *
     * <p>&Auml;quivalent zu {@code parse(text, status, getAttributes())}. </p>
     *
     * @param   text        text to be parsed
     * @param   status      parser information (always as new instance)
     * @return  result or {@code null} if parsing does not work
     * @throws  IndexOutOfBoundsException if the start position is at end of text or even behind
     * @since   4.18
     */
    default T parse(
        CharSequence    text,
        ParseLog        status
    ) {

        return this.parse(text, status, this.getAttributes());

    }

    /**
     * <p>Interpretes given text as chronological entity starting
     * at the specified position in parse log. </p>
     *
     * <p>Implementation note: Any implementation will parse the text first
     * at the position {@code status.getPosition()} and then set the new
     * position in the parse log if successful. In case of error the
     * error index in the parse log will be updated instead. </p>
     *
     * @param   text        text to be parsed
     * @param   status      parser information (always as new instance)
     * @param   attributes  control attributes
     * @return  result or {@code null} if parsing does not work
     * @throws  IndexOutOfBoundsException if the start position is at end of text or even behind
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text ab der im Log angegebenen Position. </p>
     *
     * <p>Implementierungshinweis: Eine Implementierung wird den Text erst
     * ab der angegebenen Position {@code status.getPosition()} auswerten und
     * dann in der Statusinformation nach einer erfolgreichen Interpretierung
     * die aktuelle Position neu setzen oder im Fehlerfall den Fehlerindex auf
     * die fehlerhafte Stelle im Text setzen. </p>
     *
     * @param   text        text to be parsed
     * @param   status      parser information (always as new instance)
     * @param   attributes  control attributes
     * @return  result or {@code null} if parsing does not work
     * @throws  IndexOutOfBoundsException if the start position is at end of text or even behind
     */
    T parse(
        CharSequence    text,
        ParseLog        status,
        AttributeQuery  attributes
    );

    /**
     * <p>Returns the global format attributes which are active if they are not
     * overridden by sectional attributes. </p>
     *
     * @return  global control attributes valid for the whole formatter
     *          (can be overridden by sectional attributes however)
     * @since   4.18
     */
    /*[deutsch]
     * <p>Ermittelt die globalen Standardattribute, welche genau dann wirksam sind,
     * wenn sie nicht durch sektionale Attribute &uuml;berschrieben werden. </p>
     *
     * @return  global control attributes valid for the whole formatter
     *          (can be overridden by sectional attributes however)
     * @since   4.18
     */
    default AttributeQuery getAttributes() {

        return Attributes.empty();

    }
    
    /**
     * <p>Creates a simple placeholder in situations where parsing
     * is not used. </p>
     * 
     * @param   <T> generic type of chronological entity to be parsed
     * @return  dummy parser
     * @since   5.9.4
     */
    /*[deutsch]
     * <p>Erzeugt einen einfachen Platzhalter in Situationen, in denen
     * das Parsen nicht gebraucht wird. </p>
     * 
     * @param   <T> generic type of chronological entity to be parsed
     * @return  dummy parser
     * @since   5.9.4
     */
    static <T> ChronoParser<T> unsupported() {
        return (CharSequence text, ParseLog status, AttributeQuery attrs) -> {
            throw new UnsupportedOperationException("Parsing not used.");
        };
    }

}
