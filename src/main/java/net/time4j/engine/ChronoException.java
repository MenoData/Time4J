/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoException.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Time4J. If not, see <http://www.gnu.org/licenses/>.
 * -----------------------------------------------------------------------
 */

package net.time4j.engine;


/**
 * <p>Signalisiert eine chronologische Fehlersituation. </p>
 *
 * @author  Meno Hochschild
 */
public class ChronoException
    extends RuntimeException {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = -8254443297873771377L;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * Erzeugt eine neue Instanz von <code>ChronoException</code>
     * mit der angegebenen Detailmeldung.
     *
     * @param   msg     detaillierte Fehlermeldung
     */
    public ChronoException(String msg) {
        super(msg);

    }

    /**
     * Erzeugt eine neue Instanz von <code>ChronoException</code>
     * mit der angegebenen Detailmeldung.
     *
     * @param   msg     detaillierte Fehlermeldung
     * @param   ex      urspr&uuml;ngliche Ausnahme
     */
    public ChronoException(
        String msg,
        Exception ex
    ) {
        super(msg, ex);

    }

}
