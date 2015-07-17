/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (StdCalendarElement.java) is part of project Time4J.
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

import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoOperator;


/**
 * <p>Extends a chronological element by some standard ways of
 * manipulation. </p>
 *
 * @param   <V> generic type of element values
 * @param   <T> generic type of target entity an operator is applied to
 * @author  Meno Hochschild
 * @since   3.5/4.3
 */
/*[deutsch]
 * <p>Erweitert ein chronologisches Element um diverse
 * Standardmanipulationen. </p>
 *
 * @param   <V> generic type of element values
 * @param   <T> generic type of target entity an operator is applied to
 * @author  Meno Hochschild
 * @since   3.5/4.3
 */
public interface StdCalendarElement<V, T>
    extends ChronoElement<V> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Sets any local entity to the minimum of this element. </p>
     *
     * @return  ChronoOperator
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Setzt eine beliebige Entit&auml;t auf das Elementminimum. </p>
     *
     * @return  ChronoOperator
     * @since   3.5/4.3
     */
    ChronoOperator<T> minimized();

    /**
     * <p>Sets any local entity to the maximum of this element. </p>
     *
     * @return  ChronoOperator
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Setzt eine beliebige Entit&auml;t auf das Elementmaximum. </p>
     *
     * @return  ChronoOperator
     * @since   3.5/4.3
     */
    ChronoOperator<T> maximized();

    /**
     * <p>Adjusts any local entity such that this element gets the previous value. </p>
     *
     * <p>The operator throws a {@code ChronoException} if there is no
     * base unit available for this element. </p>
     *
     * @return  ChronoOperator
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Passt eine beliebige Entit&auml;t so an, da&szlig; dieses Element
     * den vorherigen Wert bekommt. </p>
     *
     * <p>Der Operator wirft eine {@code ChronoException}, wenn er auf einen
     * Zeitpunkt angewandt wird, dessen Zeitachse keine Basiseinheit zu diesem
     * Element kennt. </p>
     *
     * @return  ChronoOperator
     * @since   3.5/4.3
     */
    ChronoOperator<T> decremented();

    /**
     * <p>Adjusts any local entity such that this element gets the next value. </p>
     *
     * <p>The operator throws a {@code ChronoException} if there is no
     * base unit available for this element. </p>
     *
     * @return  ChronoOperator
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Passt eine beliebige Entit&auml;t so an, da&szlig; dieses Element
     * den n&auml;chsten Wert bekommt. </p>
     *
     * <p>Der Operator wirft eine {@code ChronoException}, wenn er auf einen
     * Zeitpunkt angewandt wird, dessen Zeitachse keine Basiseinheit zu diesem
     * Element kennt. </p>
     *
     * @return  ChronoOperator
     * @since   3.5/4.3
     */
    ChronoOperator<T> incremented();

    /**
     * <p>Rounds down an entity by setting all child elements to minimum. </p>
     *
     * @return  ChronoOperator
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Rundet eine Entit&auml;t ab, indem alle Kindselemente dieses
     * Elements auf ihr Minimum gesetzt werden. </p>
     *
     * @return  ChronoOperator
     * @since   3.5/4.3
     */
    ChronoOperator<T> atFloor();

    /**
     * <p>Rounds up an entity by setting all child elements to maximum. </p>
     *
     * @return  ChronoOperator
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Rundet eine Entit&auml;t auf, indem alle Kindselemente dieses
     * Elements auf ihr Maximum gesetzt werden. </p>
     *
     * @return  ChronoOperator
     * @since   3.5/4.3
     */
    ChronoOperator<T> atCeiling();

}
