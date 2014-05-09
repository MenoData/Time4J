/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AdjustableElement.java) is part of project Time4J.
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

import net.time4j.engine.ChronoElement;


/**
 * <p>Erweitert ein chronologisches Element um diverse
 * Standardmanipulationen. </p>
 *
 * @param   <V> generic type of element values
 * @param   <T> generic type of target entity an operator is applied to
 * @author  Meno Hochschild
 */
public interface AdjustableElement<V, T>
    extends ChronoElement<V> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Setzt eine beliebige Entit&auml;t auf das Elementminimum. </p>
     *
     * @return  operator directly applicable on local types without timezone
     */
    ElementOperator<T> minimized();

    /**
     * <p>Setzt eine beliebige Entit&auml;t auf das Elementmaximum. </p>
     *
     * @return  operator directly applicable on local types without timezone
     */
    ElementOperator<T> maximized();

    /**
     * <p>Passt eine beliebige Entit&auml;t so an, da&szlig; dieses Element
     * den vorherigen Wert bekommt. </p>
     *
     * <p>Der Operator wirft eine {@code ChronoException}, wenn er auf einen
     * Zeitpunkt angewandt wird, dessen Zeitachse keine Basiseinheit zu diesem
     * Element kennt. </p>
     *
     * @return  operator directly applicable on local types without timezone
     *          and requiring a base unit in given chronology for decrementing
     * @see     net.time4j.engine.TimeAxis#getBaseUnit(ChronoElement)
     */
    ElementOperator<T> decremented();

    /**
     * <p>Passt eine beliebige Entit&auml;t so an, da&szlig; dieses Element
     * den n&auml;chsten Wert bekommt. </p>
     *
     * <p>Der Operator wirft eine {@code ChronoException}, wenn er auf einen
     * Zeitpunkt angewandt wird, dessen Zeitachse keine Basiseinheit zu diesem
     * Element kennt. </p>
     *
     * @return  operator directly applicable on local types without timezone
     *          and requiring a base unit in given chronology for incrementing
     * @see     net.time4j.engine.TimeAxis#getBaseUnit(ChronoElement)
     */
    ElementOperator<T> incremented();

    /**
     * <p>Rundet eine Entit&auml;t ab, indem alle Kindselemente dieses
     * Elements auf ihr Minimum gesetzt werden. </p>
     *
     * @return  operator directly applicable on local types without timezone
     */
    ElementOperator<T> atFloor();

    /**
     * <p>Rundet eine Entit&auml;t auf, indem alle Kindselemente dieses
     * Elements auf ihr Maximum gesetzt werden. </p>
     *
     * @return  operator directly applicable on local types without timezone
     */
    ElementOperator<T> atCeiling();

}
