/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FormatProcessor.java) is part of project Time4J.
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

import java.io.IOException;
import java.util.Map;
import java.util.Set;


/**
 * <p>F&uuml;hrt das elementweise Parsen und Formatieren aus. </p>
 *
 * <p><strong>Specification:</strong>
 * Implementations must be immutable. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
interface FormatProcessor<V> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Erzeugt eine Textausgabe und speichert sie im angegebenen Puffer. </p>
     *
     * @param   formattable     object to be formatted
     * @param   buffer          format buffer any text output will be sent to
     * @param   attributes      non-sectional control attributes
     * @param   positions       positions of elements in text (optional)
     * @param   step            current formatting step
     * @throws  IllegalArgumentException if the object is not formattable
     * @throws  IOException if writing into buffer fails
     */
    void print(
        ChronoDisplay formattable,
        Appendable buffer,
        AttributeQuery attributes,
        Set<ElementPosition> positions,
        FormatStep step
    ) throws IOException;

    /**
     * <p>Interpretiert den angegebenen Text. </p>
     *
     * <p>Implementierungshinweis: Eine Implementierung wird den Text erst
     * ab der angegebenen Position {@code status.getPosition()} auswerten und
     * dann in der Statusinformation nach einer erfolgreichen Interpretierung
     * die aktuelle Position neu setzen oder im Fehlerfall den Fehlerindex auf
     * die fehlerhafte Stelle im Text setzen. </p>
     *
     * @param   text            text to be parsed
     * @param   status          parser information (always as new instance)
     * @param   attributes      non-sectional control attributes
     * @param   parsedResult    result buffer for parsed values
     * @param   step            current formatting step
     */
    void parse(
        CharSequence text,
        ParseLog status,
        AttributeQuery attributes,
        Map<ChronoElement<?>, Object> parsedResult,
        FormatStep step
    );

    /**
     * <p>Liefert das zugeh&ouml;rige chronologische Element,
     * falls definiert. </p>
     *
     * @return  element or {@code null} if not relevant
     */
    ChronoElement<V> getElement();

    /**
     * <p>Liefert eine Kopie mit dem neuen chronologischen Element. </p>
     *
     * @param   element     chronological element
     * @return  copy of this processor maybe modified
     */
    FormatProcessor<V> withElement(ChronoElement<V> element);

    /**
     * <p>Werden nur Zahlen verarbeitet? </p>
     *
     * @return  {@code true} if only digit processing happens else {@code false}
     */
    boolean isNumerical();

}
