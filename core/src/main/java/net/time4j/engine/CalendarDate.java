/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CalendarDate.java) is part of project Time4J.
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
 * <p>Represents a general calendar date. </p>
 *
 * @author  Meno Hochschild
 * @since   3.8/4.5
 */
/*[deutsch]
 * <p>Repr&auml;sentiert ein allgemeines Kalenderdatum. </p>
 *
 * @author  Meno Hochschild
 * @since   3.8/4.5
 */
public interface CalendarDate
    extends Temporal<CalendarDate> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Counts the elapsed days since UTC epoch. </p>
     *
     * @return  count of days relative to UTC epoch [1972-01-01]
     * @see     EpochDays#UTC
     * @since   3.8/4.5
     */
    /*[deutsch]
     * <p>Z&auml;hlt die seit der UTC-Epoche verstrichenen Tage. </p>
     *
     * @return  count of days relative to UTC epoch [1972-01-01]
     * @see     EpochDays#UTC
     * @since   3.8/4.5
     */
    long getDaysSinceEpochUTC();

}
