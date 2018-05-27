/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (GregorianTextElement.java) is part of project Time4J.
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

package net.time4j.format.internal;

import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoException;
import net.time4j.format.Leniency;
import net.time4j.format.OutputContext;
import net.time4j.format.TextElement;
import net.time4j.format.TextWidth;

import java.io.IOException;
import java.text.ParsePosition;
import java.util.Locale;


/**
 * <p>A text element optimized for performance in standard use cases. </p>
 *
 * @param   <V> generic type of element values
 * @author  Meno Hochschild
 * @since   3.15/4.12
 */
/*[deutsch]
 * <p>Repr&auml;sentiert ein Textelement, das f&uuml;r Standardszenarien optimiert ist. </p>
 *
 * @param   <V> generic type of element values
 * @author  Meno Hochschild
 * @since   3.15/4.12
 */
public interface GregorianTextElement<V>
    extends TextElement<V> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Converts the element value in given context to a formatted text. </p>
     *
     * @param   context         time context with the value of this element
     * @param   buffer          format buffer any text output will be sent to
     * @param   language        the language to be applied
     * @param   textWidth       the desired text width
     * @param   outputContext   format context
     * @throws  IOException if writing to buffer fails
     * @throws  ChronoException if there is no suitable element rule for evaluating the value
     * @see     3.15/4.12
     */
    /*[deutsch]
     * <p>Wandelt dieses im angegebenen Zeitwertkontext enthaltene Element zu einem Text um. </p>
     *
     * @param   context         time context with the value of this element
     * @param   buffer          format buffer any text output will be sent to
     * @param   language        the language to be applied
     * @param   textWidth       the desired text width
     * @param   outputContext   format context
     * @throws  IOException if writing to buffer fails
     * @throws  ChronoException if there is no suitable element rule for evaluating the value
     * @see     3.15/4.12
     */
    void print(
        ChronoDisplay context,
        Appendable buffer,
        Locale language,
        TextWidth textWidth,
        OutputContext outputContext
    ) throws IOException, ChronoException;

    /**
     * <p>Interpretes the given text as element value. </p>
     *
     * @param   text            text to be parsed
     * @param   status          current parsing position
     * @param   language        the language to be applied
     * @param   textWidth       the desired text width
     * @param   outputContext   format context
     * @param   leniency        leniency mode
     * @return  parsed element value or {@code null} if parsing was not successful
     * @since   3.15/4.12
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text ab einer bestimmten Position
     * als Elementwert. </p>
     *
     * @param   text            text to be parsed
     * @param   status          current parsing position
     * @param   language        the language to be applied
     * @param   textWidth       the desired text width
     * @param   outputContext   format context
     * @param   leniency        leniency mode
     * @return  parsed element value or {@code null} if parsing was not successful
     * @since   3.15/4.12
     */
    V parse(
        CharSequence text,
        ParsePosition status,
        Locale language,
        TextWidth textWidth,
        OutputContext outputContext,
        Leniency leniency
    );

}
