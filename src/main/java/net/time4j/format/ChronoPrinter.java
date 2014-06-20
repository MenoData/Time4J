/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoPrinter.java) is part of project Time4J.
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

package net.time4j.format;

import java.io.IOException;


/**
 * <p>Prints a chronological entity. </p>
 *
 * @param   <T> generic type of chronological entity to be formatted
 * @author  Meno Hochschild
 * @see     net.time4j.engine.ChronoEntity
 */
/*[deutsch]
 * <p>Erzeugt eine formatierte Ausgabe einer Entit&auml;t. </p>
 *
 * @param   <T> generic type of chronological entity to be formatted
 * @author  Meno Hochschild
 * @see     net.time4j.engine.ChronoEntity
 */
public interface ChronoPrinter<T> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a text output and writes it into given buffer. </p>
     *
     * <p>Note: Implementations must document the type and content of
     * the result to be returned. </p>
     *
     * @param   formattable  chronological entity to be formatted
     * @param   buffer       format buffer any text output will be sent to
     * @param   attributes   control attributes
     * @return  result (will be redefined by subclasses in covariant way)
     * @throws  IllegalArgumentException if the object is not formattable
     * @throws  IOException if writing into buffer fails
     */
    /*[deutsch]
     * <p>Erzeugt eine Textausgabe und schreibt sie in den angegebenen
     * Puffer. </p>
     *
     * <p>Notiz: Implementierungen m&uuml;ssen dokumentieren, was f&uuml;r ein
     * Ergebnis zur&uuml;ckgeliefert wird. </p>
     *
     * @param   formattable  chronological entity to be formatted
     * @param   buffer       format buffer any text output will be sent to
     * @param   attributes   control attributes
     * @return  result (will be redefined by subclasses in covariant way)
     * @throws  IllegalArgumentException if the object is not formattable
     * @throws  IOException if writing into buffer fails
     */
    Object print(
        T formattable,
        Appendable buffer,
        Attributes attributes
    ) throws IOException;

}
