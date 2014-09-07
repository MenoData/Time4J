/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (OrdinalWeekdayElement.java) is part of project Time4J.
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
 * <p>The element for the ordinal weekday in month. </p>
 *
 * <p>An instance can be obtained using the expression
 * {@link PlainDate#WEEKDAY_IN_MONTH}. This interface inherits from
 * {@code AdjustableElement} and offers additional operator for setting
 * the weekday in month. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Das Element f&uuml;r den x-ten Wochentag im Monat. </p>
 *
 * <p>Eine Instanz ist erh&auml;ltlich &uuml;ber den Ausdruck
 * {@link PlainDate#WEEKDAY_IN_MONTH}. Dieses Interface bietet neben
 * den vom Interface {@code AdjustableElement} geerbten Methoden
 * weitere Spezialmethoden zum Setzen des Wochentags im Monat. </p>
 *
 * @author  Meno Hochschild
 */
public interface OrdinalWeekdayElement
    extends AdjustableElement<Integer, PlainDate> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Defines an operator which moves a date to the first given weekday
     * in month. </p>
     *
     * @param   dayOfWeek   first day of week in month
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    /*[deutsch]
     * <p>Definiert einen Versteller, der ein Datum auf den ersten angegebenen
     * Wochentag eines Monats setzt. </p>
     *
     * @param   dayOfWeek   first day of week in month
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    ElementOperator<PlainDate> setToFirst(Weekday dayOfWeek);

    /**
     * <p>Defines an operator which moves a date to the second given weekday
     * in month. </p>
     *
     * @param   dayOfWeek   second day of week in month
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    /*[deutsch]
     * <p>Definiert einen Versteller, der ein Datum auf den zweiten angegebenen
     * Wochentag eines Monats setzt. </p>
     *
     * @param   dayOfWeek   second day of week in month
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    ElementOperator<PlainDate> setToSecond(Weekday dayOfWeek);

    /**
     * <p>Defines an operator which moves a date to the third given weekday
     * in month. </p>
     *
     * @param   dayOfWeek   third day of week in month
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    /*[deutsch]
     * <p>Definiert einen Versteller, der ein Datum auf den dritten angegebenen
     * Wochentag eines Monats setzt. </p>
     *
     * @param   dayOfWeek   third day of week in month
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    ElementOperator<PlainDate> setToThird(Weekday dayOfWeek);

    /**
     * <p>Defines an operator which moves a date to the fourth given weekday
     * in month. </p>
     *
     * @param   dayOfWeek   fourth day of week in month
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    /*[deutsch]
     * <p>Definiert einen Versteller, der ein Datum auf den vierten angegebenen
     * Wochentag eines Monats setzt. </p>
     *
     * @param   dayOfWeek   fourth day of week in month
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    ElementOperator<PlainDate> setToFourth(Weekday dayOfWeek);

    /**
     * <p>Defines an operator which moves a date to the last given weekday
     * in month. </p>
     *
     * @param   dayOfWeek   last day of week in month
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    /*[deutsch]
     * <p>Definiert einen Versteller, der ein Datum auf den letzten angegebenen
     * Wochentag eines Monats setzt. </p>
     *
     * @param   dayOfWeek   last day of week in month
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    ElementOperator<PlainDate> setToLast(Weekday dayOfWeek);

}
