/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (NumericalElement.java) is part of project Time4J.
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

import net.time4j.engine.ChronoElement;


/**
 * <p>A chronological element which allows a numerical representation. </p>
 *
 * @param   <V> generic type of element values
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Repr&auml;sentiert ein chronologisches Element, das eine numerische
 * Darstellung erlaubt. </p>
 *
 * @param   <V> generic type of element values
 * @author  Meno Hochschild
 */
public interface NumericalElement<V>
    extends ChronoElement<V> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Translates given element value to a numerical integer. </p>
     *
     * @param   value       value to be converted to int
     * @return  Integer-representation of given value
     */
    /*[deutsch]
     * <p>Ermittelt eine numerische Darstellung des angegebenen Werts. </p>
     *
     * @param   value       value to be converted to int
     * @return  Integer-representation of given value
     */
    int numerical(V value);

}
