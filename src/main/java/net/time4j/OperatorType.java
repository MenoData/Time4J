/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (OperatorType.java) is part of project Time4J.
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

package net.time4j;

/**
 * <p>Kennzeichnet einige Standardoperatoren. </p>
 *
 * @author  Meno Hochschild
 */
enum OperatorType {

    //~ Statische Felder/Initialisierungen --------------------------------

    MINIMIZE, MAXIMIZE, DECREMENT, INCREMENT, FLOOR, CEILING,
    LENIENT, ROLLING, WIM, YOW,
    NAV_NEXT, NAV_PREVIOUS, NAV_NEXT_OR_SAME, NAV_PREVIOUS_OR_SAME;

}
