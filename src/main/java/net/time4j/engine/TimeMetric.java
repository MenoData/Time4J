/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimeMetric.java) is part of project Time4J.
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

package net.time4j.engine;


/**
 * <p>Berechnet Abst&auml;nde auf einer Zeitskala als Zeitspannen. </p>
 *
 * @param   <U> generic type of time unit
 * @param   <P> generic type of duration type
 * @author  Meno Hochschild
 */
public interface TimeMetric<U, P extends TimeSpan<?>> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Berechnet den zeitlichen Abstand zwischen zwei Zeitpunkten. </p>
     *
     * @param   <T> generic type of time point
     * @param   start   first time point
     * @param   end     second time point
     * @return  calculated time span between given time points, will be
     *          negative if {@code start} is after {@code end}
     */
    <T extends TimePoint<? super U, T>> P between(
        T start,
        T end
    );

}
