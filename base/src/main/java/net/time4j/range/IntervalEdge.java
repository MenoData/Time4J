/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (IntervalEdge.java) is part of project Time4J.
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

package net.time4j.range;


/**
 * <p>Characterize the type of an interval boundary. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 */
/*[deutsch]
 * <p>Characterisiert den Typ einer Intervallgrenze. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 */
enum IntervalEdge {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * A closed interval edge is included for any temporal computation.
     *
     * @since   2.0
     */
    /*[deutsch]
     * Eine geschlossene Intervallgrenze ist f&uuml;r jedwede Zeitrechnung
     * immer inklusive.
     *
     * @since   2.0
     */
    CLOSED,

    /**
     * An open interval edge is excluded for any temporal computation.
     *
     * @since   2.0
     */
    /*[deutsch]
     * Eine offene Intervallgrenze ist f&uuml;r jedwede Zeitrechnung
     * immer exklusive.
     *
     * @since   2.0
     */
    OPEN;

}
