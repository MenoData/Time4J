/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoInterval.java) is part of project Time4J.
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

package net.time4j.range;

import net.time4j.engine.Temporal;


/**
 * <p>Represents a temporal interval on a timeline. </p>
 *
 * @param   <T> temporal type of time points within a given interval
 * @author  Meno Hochschild
 * @since   2.0
 */
/**
 * <p>Repr&auml;sentiert ein Zeitintervall auf einem Zeitstrahl. </p>
 *
 * @param   <T> temporal type of time points within a given interval
 * @author  Meno Hochschild
 * @since   2.0
 */
public interface ChronoInterval<T extends Temporal<? super T>> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Yields the lower bound of this interval. </p>
     *
     * @return  start interval boundary
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liefert die untere Grenze dieses Intervalls. </p>
     *
     * @return  start interval boundary
     * @since   2.0
     */
    Boundary<T> getStart();

    /**
     * <p>Yields the upper bound of this interval. </p>
     *
     * @return  end interval boundary
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liefert die obere Grenze dieses Intervalls. </p>
     *
     * @return  end interval boundary
     * @since   2.0
     */
    Boundary<T> getEnd();

    /**
     * <p>Determines if this interval has finite boundaries. </p>
     *
     * @return  {@code true} if start and end are finite else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>Ermittelt, ob dieses Intervall endliche Grenzen hat. </p>
     *
     * @return  {@code true} if start and end are finite else {@code false}
     * @since   2.0
     */
    boolean isFinite();

    /**
     * <p>Determines if this interval is empty. </p>
     *
     * @return  {@code true} if this interval does not contain any time point
     *          else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>Ermittelt, ob dieses Intervall leer ist. </p>
     *
     * @return  {@code true} if this interval does not contain any time point
     *          else {@code false}
     * @since   2.0
     */
    boolean isEmpty();

    /**
     * <p>Queries if given time point belongs to this interval. </p>
     *
     * @param   temporal    time point to be queried
     * @return  {@code true} if given time point belongs to this interval
     *          else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>Ermittelt, ob der angegebene Zeitpunkt zu diesem Intervall
     * geh&ouml;rt. </p>
     *
     * @param   temporal    time point to be queried
     * @return  {@code true} if given time point belongs to this interval
     *          else {@code false}
     * @since   2.0
     */
    boolean contains(T temporal);

    /**
     * <p>Is this interval after the given time point? </p>
     *
     * @param   temporal    reference time point
     * @return  {@code true} if this interval is after given time point
     *          else {@code false}
     */
    /*[deutsch]
     * <p>Liegt dieses Intervall nach dem angegebenen Zeitpunkt? </p>
     *
     * @param   temporal    reference time point
     * @return  {@code true} if this interval is after given time point
     *          else {@code false}
     */
    boolean isAfter(T temporal);

    /**
     * <p>Is this interval before the given time point? </p>
     *
     * @param   temporal    reference time point
     * @return  {@code true} if this interval is before given time point
     *          else {@code false}
     */
    /*[deutsch]
     * <p>Liegt dieses Intervall vor dem angegebenen Zeitpunkt? </p>
     *
     * @param   temporal    reference time point
     * @return  {@code true} if this interval is before given time point
     *          else {@code false}
     */
    boolean isBefore(T temporal);
    
}
