/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (IntervalCreator.java) is part of project Time4J.
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
 * <p>Generische Intervallfabrik. </p>
 *
 * @author  Meno Hochschild
 * @since   3.25/4.21
 */
interface IntervalCreator<T, I extends ChronoInterval<T>> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates an interval between given boundaries. </p>
     *
     * @param   start   lower boundary
     * @param   end     upper boundary
     * @return  new interval
     * @throws  IllegalArgumentException if start is after end
     */
    /*[deutsch]
     * <p>Erzeugt ein Intervall zwischen den angegebenen Intervallgrenzen. </p>
     *
     * @param   start   lower boundary
     * @param   end     upper boundary
     * @return  new interval
     * @throws  IllegalArgumentException if start is after end
     */
    I between(
        Boundary<T> start,
        Boundary<T> end
    );

    /**
     * <p>Liegt ein kalendarischer Typ vor? </p>
     *
     * @return  {@code false} as default value
     */
    default boolean isCalendrical() {
        return false;
    }

}
