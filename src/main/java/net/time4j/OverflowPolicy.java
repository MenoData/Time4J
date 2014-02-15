/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (OverflowPolicy.java) is part of project Time4J.
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
 * <p>Handles the day overflow at the end of month after an addition to
 * a calendar date. </p>
 *
 * @author  Meno Hochschild
 * @see     CalendarUnit
 */
enum OverflowPolicy {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * Standard policy which resets the invalid day of month to the previous
     * valid one.
     */
    PREVIOUS_VALID_DATE,

    /**
     * Resolves the invalid day of month to the next valid one.
     */
    NEXT_VALID_DATE,

    /**
     * Always moves the day of month to the last day of month even if valid.
     */
    END_OF_MONTH,

    /**
     * Any carry-over will be transferred to the next month.
     */
    CARRY_OVER,

    /**
     * This policy causes an exception in case of day overflow.
     */
    UNLESS_INVALID;

}
