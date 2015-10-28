/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Calculus.java) is part of project Time4J.
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

package net.time4j.history;


/**
 * <p>Calculator engine for historic dates. </p>
 *
 * @author  Meno Hochschild
 * @since   3.11/4.8
 */
interface Calculus {

    //~ Methoden ----------------------------------------------------------

    /**
     * Converts given historic date tuple to a modified julian date.
     *
     * @param   date    historic date
     * @return  modified julian date
     * @throws  IllegalArgumentException if argument is out of range
     */
    long toMJD(HistoricDate date);

    /**
     * Converts given modified julian date to a historic date tuple.
     *
     * @param   mjd     modified julian date
     * @return  historic date
     * @throws  IllegalArgumentException if argument is out of range
     */
    HistoricDate fromMJD(long mjd);

    /**
     * Checks if given historic date tuple is valid.
     *
     * @param   date    historic date
     * @return  {@code true} if valid else {@code false}
     */
    boolean isValid(HistoricDate date);

    /**
     * Determines the length of associated month of given historic date tuple.
     *
     * @param   date    historic date
     * @return  int
     * @throws  IllegalArgumentException if argument is out of range
     */
    int getMaximumDayOfMonth(HistoricDate date);

}
