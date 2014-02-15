/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (OrdinalWeekdayElement.java) is part of project Time4J.
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
 * <p>Das Element f&uuml;r den x-ten Wochentag im Monat. </p>
 *
 * <p>Eine Instanz ist erh&auml;ltlich &uuml;ber den Ausdruck
 * {@link PlainDate#WEEKDAY_IN_MONTH}. Dieses Interface bietet neben
 * den vom Interface {@code AdjustableElement} geerbten Methoden
 * weitere Spezialmethoden zum Setzen des Wochentags im Monat. </p>
 *
 * @param   <T> generic operator target type
 * @author  Meno Hochschild
 */
public interface OrdinalWeekdayElement<T>
    extends AdjustableElement<Integer, T> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Definiert einen Versteller, der ein Datum auf den ersten angegebenen
     * Wochentag eines Monats setzt. </p>
     *
     * @param   dayOfWeek   first day of week in month
     * @return  operator directly applicable on local types without time zone
     */
    ZonalOperator<T> setToFirst(Weekday dayOfWeek);

    /**
     * <p>Definiert einen Versteller, der ein Datum auf den zweiten angegebenen
     * Wochentag eines Monats setzt. </p>
     *
     * @param   dayOfWeek   second day of week in month
     * @return  operator directly applicable on local types without time zone
     */
    ZonalOperator<T> setToSecond(Weekday dayOfWeek);

    /**
     * <p>Definiert einen Versteller, der ein Datum auf den dritten angegebenen
     * Wochentag eines Monats setzt. </p>
     *
     * @param   dayOfWeek   third day of week in month
     * @return  operator directly applicable on local types without time zone
     */
    ZonalOperator<T> setToThird(Weekday dayOfWeek);

    /**
     * <p>Definiert einen Versteller, der ein Datum auf den vierten angegebenen
     * Wochentag eines Monats setzt. </p>
     *
     * @param   dayOfWeek   fourth day of week in month
     * @return  operator directly applicable on local types without time zone
     */
    ZonalOperator<T> setToFourth(Weekday dayOfWeek);

    /**
     * <p>Definiert einen Versteller, der ein Datum auf den letzten angegebenen
     * Wochentag eines Monats setzt. </p>
     *
     * @param   dayOfWeek   last day of week in month
     * @return  operator directly applicable on local types without time zone
     */
    ZonalOperator<T> setToLast(Weekday dayOfWeek);

}
