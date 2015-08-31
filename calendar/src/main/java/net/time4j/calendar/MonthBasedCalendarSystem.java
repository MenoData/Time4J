/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (MonthBasedCalendarSystem.java) is part of project Time4J.
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

package net.time4j.calendar;

import net.time4j.engine.CalendarEra;
import net.time4j.engine.CalendarSystem;


/**
 * <p>Month-based calendar system abstraction. </p>
 *
 * @param   <D> generic type of calendar date (subtype of {@code Calendrical} or {@code CalendarVariant})
 * @author  Meno Hochschild
 * @since   3.5/4.3
 */
/*[deutsch]
 * <p>Monatsbasierte Kalendersystemabstraktion. </p>
 *
 * @param   <D> generic type of calendar date (subtype of {@code Calendrical} or {@code CalendarVariant})
 * @author  Meno Hochschild
 * @since   3.5/4.3
 */
interface MonthBasedCalendarSystem<D>
    extends CalendarSystem<D> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Validates the given calendar date parameters. </p>
     *
     * @param   era         calendar era
     * @param   yearOfEra   calendar year
     * @param   monthOfYear calendar month
     * @param   dayOfMonth  calendar day of month
     * @return  {@code true} if valid else {@code false}
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Sind die angegebenen Datumsparameter g&uuml;ltig? </p>
     *
     * @param   era         calendar era
     * @param   yearOfEra   calendar year
     * @param   monthOfYear calendar month
     * @param   dayOfMonth  calendar day of month
     * @return  {@code true} if valid else {@code false}
     * @since   3.5/4.3
     */
    boolean isValid(
        CalendarEra era,
        int yearOfEra,
        int monthOfYear,
        int dayOfMonth
    );

    /**
     * <p>Yields the length of given  month. </p>
     *
     * @param   era         calendar era
     * @param   yearOfEra   calendar year
     * @param   monthOfYear calendar month
     * @return  length of month in days
     * @throws  IllegalArgumentException if any parameter is wrong or out of bounds
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Bestimmt die L&auml;nge des angegebenen Monats. </p>
     *
     * @param   era         calendar era
     * @param   yearOfEra   calendar year
     * @param   monthOfYear calendar month
     * @return  length of month in days
     * @throws  IllegalArgumentException if any parameter is wrong or out of bounds
     * @since   3.5/4.3
     */
    int getLengthOfMonth(
        CalendarEra era,
        int yearOfEra,
        int monthOfYear
    );

    /**
     * <p>Yields the length of given  year. </p>
     *
     * @param   era         calendar era
     * @param   yearOfEra   calendar year
     * @return  length of year in days
     * @throws  IllegalArgumentException if any parameter is wrong or out of bounds
     * @since   3.6/4.4
     */
    /*[deutsch]
     * <p>Bestimmt die L&auml;nge des angegebenen Jahres. </p>
     *
     * @param   era         calendar era
     * @param   yearOfEra   calendar year
     * @return  length of year in days
     * @throws  IllegalArgumentException if any parameter is wrong or out of bounds
     * @since   3.6/4.4
     */
    int getLengthOfYear(
        CalendarEra era,
        int yearOfEra
    );

}
