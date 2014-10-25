/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CalendarDateElement.java) is part of project Time4J.
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

package net.time4j;


/**
 * <p>Represents the calendar date. </p>
 *
 * @author  Meno Hochschild
 * @since   1.2
 */
/*[deutsch]
 * <p>Repr&auml;sentiert das Kalenderdatum. </p>
 *
 * @author  Meno Hochschild
 * @since   1.2
 */
public interface CalendarDateElement
    extends ZonalElement<PlainDate> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Adjusts a calendar date to the first day of next month. </p>
     *
     * @return  operator also applicable on {@code PlainTimestamp}
     * @since   1.2
     */
    /*[deutsch]
     * <p>Setzt ein Datum auf den ersten Tag des n&auml;chsten Monats. </p>
     *
     * @return  operator also applicable on {@code PlainTimestamp}
     * @since   1.2
     */
    ElementOperator<PlainDate> firstDayOfNextMonth();

    /**
     * <p>Adjusts a calendar date to the first day of next quarter year. </p>
     *
     * @return  operator also applicable on {@code PlainTimestamp}
     * @since   1.2
     */
    /*[deutsch]
     * <p>Setzt ein Datum auf den ersten Tag des n&auml;chsten Quartals. </p>
     *
     * @return  operator also applicable on {@code PlainTimestamp}
     * @since   1.2
     */
    ElementOperator<PlainDate> firstDayOfNextQuarter();

    /**
     * <p>Adjusts a calendar date to the first day of next year. </p>
     *
     * @return  operator also applicable on {@code PlainTimestamp}
     * @since   1.2
     */
    /*[deutsch]
     * <p>Setzt ein Datum auf den ersten Tag des n&auml;chsten Jahres. </p>
     *
     * @return  operator also applicable on {@code PlainTimestamp}
     * @since   1.2
     */
    ElementOperator<PlainDate> firstDayOfNextYear();

    /**
     * <p>Adjusts a calendar date to the last day of previous month. </p>
     *
     * @return  operator also applicable on {@code PlainTimestamp}
     * @since   1.2
     */
    /*[deutsch]
     * <p>Setzt ein Datum auf den letzten Tag des vorherigen Monats. </p>
     *
     * @return  operator also applicable on {@code PlainTimestamp}
     * @since   1.2
     */
    ElementOperator<PlainDate> lastDayOfPreviousMonth();

    /**
     * <p>Adjusts a calendar date to the last day of previous quarter year. </p>
     *
     * @return  operator also applicable on {@code PlainTimestamp}
     * @since   1.2
     */
    /*[deutsch]
     * <p>Setzt ein Datum auf den letzten Tag des vorherigen Quartals. </p>
     *
     * @return  operator also applicable on {@code PlainTimestamp}
     * @since   1.2
     */
    ElementOperator<PlainDate> lastDayOfPreviousQuarter();

    /**
     * <p>Adjusts a calendar date to the last day of previous year. </p>
     *
     * @return  operator also applicable on {@code PlainTimestamp}
     * @since   1.2
     */
    /*[deutsch]
     * <p>Setzt ein Datum auf den letzten Tag des vorherigen Jahres. </p>
     *
     * @return  operator also applicable on {@code PlainTimestamp}
     * @since   1.2
     */
    ElementOperator<PlainDate> lastDayOfPreviousYear();

}
