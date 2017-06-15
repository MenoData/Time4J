/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
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

package net.time4j.calendar;

import net.time4j.Weekday;
import net.time4j.engine.ChronoOperator;


/**
 * <p>The element for the ordinal weekday in month. </p>
 *
 * <p>This interface offers additional operators for setting the weekday in month. </p>
 *
 * @param   <T> generic type of calendar chronology
 * @author  Meno Hochschild
 * @since   3.33/4.28
 */
/*[deutsch]
 * <p>Das Element f&uuml;r den x-ten Wochentag im Monat. </p>
 *
 * <p>Dieses Interface bietet weitere Spezialmethoden zum Setzen des Wochentags im Monat. </p>
 *
 * @param   <T> generic type of calendar chronology
 * @author  Meno Hochschild
 * @since   3.33/4.28
 */
public interface OrdinalWeekdayElement<T>
    extends StdCalendarElement<Integer, T> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Defines an operator which moves a date to the first given weekday in month. </p>
     *
     * <p>Equivalent to {@link #setTo(int, Weekday) setTo(1, dayOfWeek)}. </p>
     *
     * @param   dayOfWeek   first day of week in month
     * @return  lenient operator
     */
    /*[deutsch]
     * <p>Definiert einen Versteller, der ein Datum auf den ersten angegebenen Wochentag eines Monats setzt. </p>
     *
     * <p>&Auml;quivalent zu {@link #setTo(int, Weekday) setTo(1, dayOfWeek)}. </p>
     *
     * @param   dayOfWeek   first day of week in month
     * @return  lenient operator
     */
    ChronoOperator<T> setToFirst(Weekday dayOfWeek);

    /**
     * <p>Defines an operator which moves a date to the last given weekday in month. </p>
     *
     * <p>Equivalent to {@link #setTo(int, Weekday) setTo(Integer.MAX_VALUE, dayOfWeek)}.
     * The new day will never be in next month. </p>
     *
     * @param   dayOfWeek   last day of week in month
     * @return  lenient operator
     */
    /*[deutsch]
     * <p>Definiert einen Versteller, der ein Datum auf den letzten angegebenen Wochentag eines Monats setzt. </p>
     *
     * <p>&Auml;quivalent zu {@link #setTo(int, Weekday) setTo(Integer.MAX_VALUE, dayOfWeek)}.
     * Der neue Tag wird niemals im n&auml;chsten Monat liegen. </p>
     *
     * @param   dayOfWeek   last day of week in month
     * @return  lenient operator
     */
    ChronoOperator<T> setToLast(Weekday dayOfWeek);

    /**
     * <p>Defines an operator which moves a date to the given ordinal weekday in month. </p>
     *
     * <p>If given ordinal number is {@code Integer.MAX_VALUE} then the last weekday in month
     * will be determined. This operator behaves in a lenient way, any overflow of the ordinal
     * number will be transferred to another month. Note that this behaviour will also happen
     * if the current month might be shorter than a full calendar week as exotic edge case. </p>
     *
     * @param   ordinal     ordinal number
     * @param   dayOfWeek   last day of week in month
     * @return  lenient operator
     */
    /*[deutsch]
     * <p>Definiert einen Versteller, der ein Datum auf den x-ten angegebenen Wochentag eines Monats setzt. </p>
     *
     * <p>Wenn die angegebene Ordnungsnummer {@code Integer.MAX_VALUE} ist, wird der letzte
     * Wochentag des Monats bestimmt. Dieser Operator verh&auml;lt sich nachsichtig, d. h., jedweder
     * &Uuml;berlauf der Ordnungsnummer wird auf einen anderen Monat &uuml;bertragen. Zu beachten,
     * dieses Verhalten wird auch auftreten, wenn als exotischer Randfall der aktuelle Monat
     * k&uuml;rzer als eine Kalenderwoche sein sollte. </p>
     *
     * @param   ordinal     ordinal number
     * @param   dayOfWeek   last day of week in month
     * @return  lenient operator
     */
    ChronoOperator<T> setTo(
        int ordinal,
        Weekday dayOfWeek
    );

}
