/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AdjustableElement.java) is part of project Time4J.
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

import net.time4j.engine.ChronoElement;


/**
 * <p>Extends a chronological element by some standard ways of
 * manipulation. </p>
 *
 * @param   <V> generic type of element values
 * @param   <T> generic type of target entity an operator is applied to
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Erweitert ein chronologisches Element um diverse
 * Standardmanipulationen. </p>
 *
 * @param   <V> generic type of element values
 * @param   <T> generic type of target entity an operator is applied to
 * @author  Meno Hochschild
 */
public interface AdjustableElement<V, T>
    extends ZonalElement<V> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Sets any local entity to given new value of this element. </p>
     *
     * @param   value   new element value
     * @return  operator directly applicable also on {@code PlainTimestamp}
     * @since   2.0
     * @see     net.time4j.engine.ChronoEntity#with(ChronoElement,Object)
     *          ChronoEntity.with(ChronoElement, V)
     */
    /*[deutsch]
     * <p>Setzt eine beliebige Entit&auml;t auf den angegebenen Wert. </p>
     *
     * @param   value   new element value
     * @return  operator directly applicable also on {@code PlainTimestamp}
     * @since   2.0
     * @see     net.time4j.engine.ChronoEntity#with(ChronoElement,Object)
     *          ChronoEntity.with(ChronoElement, V)
     */
    ElementOperator<T> newValue(V value);

    /**
     * <p>Sets any local entity to the minimum of this element. </p>
     *
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    /*[deutsch]
     * <p>Setzt eine beliebige Entit&auml;t auf das Elementminimum. </p>
     *
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    ElementOperator<T> minimized();

    /**
     * <p>Sets any local entity to the maximum of this element. </p>
     *
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    /*[deutsch]
     * <p>Setzt eine beliebige Entit&auml;t auf das Elementmaximum. </p>
     *
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    ElementOperator<T> maximized();

    /**
     * <p>Adjusts any local entity such that this element gets the previous
     * value. </p>
     *
     * <p>The operator throws a {@code ChronoException} if there is no
     * base unit available for this element. </p>
     *
     * @return  operator directly applicable also on {@code PlainTimestamp}
     *          and requiring a base unit in given chronology for decrementing
     * @see     net.time4j.engine.TimeAxis#getBaseUnit(ChronoElement)
     */
    /*[deutsch]
     * <p>Passt eine beliebige Entit&auml;t so an, da&szlig; dieses Element
     * den vorherigen Wert bekommt. </p>
     *
     * <p>Der Operator wirft eine {@code ChronoException}, wenn er auf einen
     * Zeitpunkt angewandt wird, dessen Zeitachse keine Basiseinheit zu diesem
     * Element kennt. </p>
     *
     * @return  operator directly applicable also on {@code PlainTimestamp}
     *          and requiring a base unit in given chronology for decrementing
     * @see     net.time4j.engine.TimeAxis#getBaseUnit(ChronoElement)
     */
    ElementOperator<T> decremented();

    /**
     * <p>Adjusts any local entity such that this element gets the next
     * value. </p>
     *
     * <p>The operator throws a {@code ChronoException} if there is no
     * base unit available for this element. </p>
     *
     * @return  operator directly applicable also on {@code PlainTimestamp}
     *          and requiring a base unit in given chronology for incrementing
     * @see     net.time4j.engine.TimeAxis#getBaseUnit(ChronoElement)
     */
    /*[deutsch]
     * <p>Passt eine beliebige Entit&auml;t so an, da&szlig; dieses Element
     * den n&auml;chsten Wert bekommt. </p>
     *
     * <p>Der Operator wirft eine {@code ChronoException}, wenn er auf einen
     * Zeitpunkt angewandt wird, dessen Zeitachse keine Basiseinheit zu diesem
     * Element kennt. </p>
     *
     * @return  operator directly applicable also on {@code PlainTimestamp}
     *          and requiring a base unit in given chronology for incrementing
     * @see     net.time4j.engine.TimeAxis#getBaseUnit(ChronoElement)
     */
    ElementOperator<T> incremented();

    /**
     * <p>Rounds down an entity by setting all child elements to minimum. </p>
     *
     * <p>If there is no child element then this operator will not do anything (no-op). Example: </p>
     *
     * <pre>
     *     PlainDate date = PlainDate.of(2016, 11, 24);
     *     // DAY_OF_WEEK has no time element as child in context of plain calendar dates
     *     System.out.println(date.with(DAY_OF_WEEK.atFloor())); // 2016-11-24
     *
     *     PlainTimestamp tsp = date.atTime(20, 45);
     *     // DAY_OF_WEEK has now child elements which can be set to zero
     *     System.out.println(tsp.with(DAY_OF_WEEK.atFloor())); // 2016-11-24T00
     * </pre>
     *
     * @return  operator directly applicable on local types without timezone
     */
    /*[deutsch]
     * <p>Rundet eine Entit&auml;t ab, indem alle Kindselemente dieses Elements auf ihr Minimum gesetzt werden. </p>
     *
     * <p>Wenn es kein Kindselement gibt, wird dieser Operator nichts tun (no-op). Beispiel: </p>
     *
     * <pre>
     *     PlainDate date = PlainDate.of(2016, 11, 24);
     *     // DAY_OF_WEEK hat kein Uhrzeitelement im Kontext eines reinen Kalenderdatums
     *     System.out.println(date.with(DAY_OF_WEEK.atFloor())); // 2016-11-24
     *
     *     PlainTimestamp tsp = date.atTime(20, 45);
     *     // DAY_OF_WEEK hat jetzt Kindselemente, die genullt werden k&ouml;nnen
     *     System.out.println(tsp.with(DAY_OF_WEEK.atFloor())); // 2016-11-24T00
     * </pre>
     *
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    ElementOperator<T> atFloor();

    /**
     * <p>Rounds up an entity by setting all child elements to maximum. </p>
     *
     * <p>If there is no child element then this operator will not do anything (no-op). Example: </p>
     *
     * <pre>
     *     PlainDate date = PlainDate.of(2016, 11, 24);
     *     // DAY_OF_WEEK has no time element as child in context of plain calendar dates
     *     System.out.println(date.with(DAY_OF_WEEK.atCeiling())); // 2016-11-24
     *
     *     PlainTimestamp tsp = date.atTime(20, 45);
     *     // DAY_OF_WEEK has now child elements which can be all maximized
     *     System.out.println(tsp.with(DAY_OF_WEEK.atCeiling())); // 2016-11-24T23:59:59,999999999
     * </pre>
     *
     * @return  operator directly applicable on local types without timezone
     */
    /*[deutsch]
     * <p>Rundet eine Entit&auml;t auf, indem alle Kindselemente dieses Elements auf ihr Maximum gesetzt werden. </p>
     *
     * <p>Wenn es kein Kindselement gibt, wird dieser Operator nichts tun (no-op). Beispiel: </p>
     *
     * <pre>
     *     PlainDate date = PlainDate.of(2016, 11, 24);
     *     // DAY_OF_WEEK hat kein Uhrzeitelement im Kontext eines reinen Kalenderdatums
     *     System.out.println(date.with(DAY_OF_WEEK.atCeiling())); // 2016-11-24
     *
     *     PlainTimestamp tsp = date.atTime(20, 45);
     *     // DAY_OF_WEEK hat jetzt Kindselemente, die alle maximiert werden k&ouml;nnen
     *     System.out.println(tsp.with(DAY_OF_WEEK.atCeiling())); // 2016-11-24T23:59:59,999999999
     * </pre>
     *
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    ElementOperator<T> atCeiling();

}
