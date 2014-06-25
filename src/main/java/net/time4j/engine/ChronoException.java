/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoException.java) is part of project Time4J.
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

package net.time4j.engine;


/**
 * <p>Indicates a chronological error situation. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Signalisiert eine chronologische Fehlersituation. </p>
 *
 * @author  Meno Hochschild
 */
public class ChronoException
    extends RuntimeException {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = -6646794951280971956L;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * Creates a new instanceo of <code>ChronoException</code>
     * with given error message.
     *
     * @param   msg     detailed error message
     */
    /*[deutsch]
     * Erzeugt eine neue Instanz von <code>ChronoException</code>
     * mit der angegebenen Detailmeldung.
     *
     * @param   msg     detailed error message
     */
    public ChronoException(String msg) {
        super(msg);

    }

    /**
     * Creates a new instanceo of <code>ChronoException</code>
     * with given error message and the cause.
     *
     * @param   msg     detailed error message
     * @param   ex      cause
     */
    /*[deutsch]
     * Erzeugt eine neue Instanz von <code>ChronoException</code>
     * mit der angegebenen Detailmeldung und der Ursache.
     *
     * @param   msg     detailed error message
     * @param   ex      cause
     */
    public ChronoException(
        String msg,
        Exception ex
    ) {
        super(msg, ex);

    }

}
