/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HistorizedElement.java) is part of project Time4J.
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

package net.time4j.history.internal;

import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoException;
import net.time4j.format.TextElement;

import java.io.IOException;


/**
 * <p>Marks a specialized historic integer-based element which can be text, too. </p>
 *
 * @author  Meno Hochschild
 * @since   3.14/4.11
 */
public interface HistorizedElement
    extends TextElement<Integer> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Converts the element value in given context to a formatted text. </p>
     *
     * @param   context     time context with the value of this element
     * @param   buffer      format buffer any text output will be sent to
     * @param   attributes  query for control attributes
     * @param   minDigits   minimum count of digits
     * @param   maxDigits   maximum count of digits
     * @throws  IOException if writing to buffer fails
     * @throws  ChronoException if there is no suitable element rule for evaluating the value
     * @since   3.15/4.12
     */
    /*[deutsch]
     * <p>Wandelt dieses im angegebenen Zeitwertkontext enthaltene Element zu
     * einem Text um. </p>
     *
     * @param   context     time context with the value of this element
     * @param   buffer      format buffer any text output will be sent to
     * @param   attributes  query for control attributes
     * @param   minDigits   minimum count of digits
     * @param   maxDigits   maximum count of digits
     * @throws  IOException if writing to buffer fails
     * @throws  ChronoException if there is no suitable element rule for evaluating the value
     * @since   3.15/4.12
     */
    void print(
        ChronoDisplay context,
        Appendable buffer,
        AttributeQuery attributes,
        int minDigits,
        int maxDigits
    ) throws IOException, ChronoException;

}
