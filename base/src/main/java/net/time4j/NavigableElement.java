/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (NavigableElement.java) is part of project Time4J.
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
 * <p>Defines additional enum-based operators for setting new element values
 * taking into account the old element value. </p>
 *
 * @param   <V> generic enum type of element values
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Definiert weitere enum-basierte Operatoren zum gezielten Setzen eines
 * neuen Elementwerts unter Ber&uuml;cksichtigung des aktuellen Werts. </p>
 *
 * @param   <V> generic enum type of element values
 * @author  Meno Hochschild
 */
public interface NavigableElement<V extends Enum<V>>
    extends AdjustableElement<V, PlainDate> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Moves a time point to the first given element value which is after
     * the current element value. </p>
     *
     * <p>Example for a date which shall be moved to a special weekday: </p>
     *
     * <pre>
     *  import static net.time4j.Month.MARCH;
     *  import static net.time4j.PlainDate.DAY_OF_WEEK;
     *  import static net.time4j.Weekday.MONDAY;
     *
     *  PlainDate date = PlainDate.of(2013, MARCH, 7); // Thursday
     *  System.out.println(date.with(DAY_OF_WEEK.setToNext(MONDAY)));
     *  // output: 2013-03-11 (first monday after march 7th)
     * </pre>
     *
     * @param   value   new element value which is after current value
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    /*[deutsch]
     * <p>Setzt einen Zeitpunkt auf den ersten angegebenen Wert, der nach dem
     * aktuellen Wert liegt. </p>
     *
     * <p>Beispiel f&uuml;r ein Datum, das auf einen bestimmten Wochentag
     * gesetzt werden soll: </p>
     *
     * <pre>
     *  import static net.time4j.Month.MARCH;
     *  import static net.time4j.PlainDate.DAY_OF_WEEK;
     *  import static net.time4j.Weekday.MONDAY;
     *
     *  PlainDate date = PlainDate.of(2013, MARCH, 7); // Donnerstag
     *  System.out.println(date.with(DAY_OF_WEEK.setToNext(MONDAY)));
     *  // Ausgabe: 2013-03-11 (erster Montag nach dem 7. M&auml;rz)
     * </pre>
     *
     * @param   value   new element value which is after current value
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    ElementOperator<PlainDate> setToNext(V value);

    /**
     * <p>Moves a time point to the first given element value which is before
     * the current element value. </p>
     *
     * <p>Example for a date which shall be moved to a special weekday: </p>
     *
     * <pre>
     *  import static net.time4j.IsoElement.DAY_OF_WEEK;
     *  import static net.time4j.Month.MARCH;
     *  import static net.time4j.Weekday.THURSDAY;
     *
     *  PlainDate date = PlainDate.of(2013, MARCH, 7); // Thursday
     *  System.out.println(date.with(DAY_OF_WEEK.setToPrevious(THURSDAY)));
     *  // output: 2013-02-28 (Thursday one week earlier)
     * </pre>
     *
     * @param   value   new element value which is before current value
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    /*[deutsch]
     * <p>Setzt einen Zeitpunkt auf den ersten angegebenen Wert, der vor dem
     * aktuellen Wert liegt. </p>
     *
     * <p>Beispiel f&uuml;r ein Datum, das auf einen bestimmten Wochentag
     * gesetzt werden soll: </p>
     *
     * <pre>
     *  import static net.time4j.IsoElement.DAY_OF_WEEK;
     *  import static net.time4j.Month.MARCH;
     *  import static net.time4j.Weekday.THURSDAY;
     *
     *  PlainDate date = PlainDate.of(2013, MARCH, 7); // Donnerstag
     *  System.out.println(date.with(DAY_OF_WEEK.setToPrevious(THURSDAY)));
     *  // Ausgabe: 2013-02-28 (Donnerstag eine Woche fr&uuml;her)
     * </pre>
     *
     * @param   value   new element value which is before current value
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    ElementOperator<PlainDate> setToPrevious(V value);

    /**
     * <p>Moves a time point to the first given element value which is after
     * or equal to the current element value. </p>
     *
     * <p>Is the current element value equal to the given element value then
     * there is no movement. </p>
     *
     * @param   value   new element value which is either after current value
     *                  or the same
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    /*[deutsch]
     * <p>Setzt einen Zeitpunkt auf den ersten angegebenen Wert setzt, der nach
     * oder gleich dem aktuellen Wert liegt. </p>
     *
     * <p>Ist der aktuelle Wert gleich dem angegebenen Wert, gibt es keine
     * Verschiebung des Zeitpunkts. </p>
     *
     * @param   value   new element value which is either after current value
     *                  or the same
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    ElementOperator<PlainDate> setToNextOrSame(V value);

    /**
     * <p>Moves a time point to the first given element value which is before
     * or equal to the current element value. </p>
     *
     * <p>Is the current element value equal to the given element value then
     * there is no movement. </p>
     *
     * @param   value   new element value which is either before current value
     *                  or the same
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    /*[deutsch]
     * <p>Setzt einen Zeitpunkt auf den ersten angegebenen Wert, der vor oder
     * gleich dem aktuellen Wert liegt. </p>
     *
     * <p>Ist der aktuelle Wert gleich dem angegebenen Wert, gibt es keine
     * Verschiebung des Zeitpunkts. </p>
     *
     * @param   value   new element value which is either before current value
     *                  or the same
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    ElementOperator<PlainDate> setToPreviousOrSame(V value);

}
