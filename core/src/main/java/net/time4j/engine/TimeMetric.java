/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
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
 * <p>Computes temporal distances on a time axis as time spans. </p>
 *
 * @param   <U> generic type of time unit
 * @param   <P> generic type of duration type
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Berechnet Abst&auml;nde auf einer Zeitachse als Zeitspannen. </p>
 *
 * @param   <U> generic type of time unit
 * @param   <P> generic type of duration type
 * @author  Meno Hochschild
 */
public interface TimeMetric<U, P> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Computes the temporal distance between two time points. </p>
     *
     * <p><strong>Important note:</strong> This method might not work in Java 6 under some circumstances.
     * In case of any problem users can use the equivalent method {@code until()} defined in the class
     * {@code TimePoint}. </p>
     *
     * @param   <T> generic type of time point
     * @param   start   first time point
     * @param   end     second time point
     * @return  calculated time span between given time points, will be
     *          negative if {@code start} is after {@code end}
     * @see     TimePoint#until(TimePoint, TimeMetric)
     */
    /*[deutsch]
     * <p>Berechnet den zeitlichen Abstand zwischen zwei Zeitpunkten. </p>
     *
     * <p><strong>Wichtiger Hinweis:</strong> Diese Methode mag in Java 6 unter bestimmten Umst&auml;nden nicht
     * funktionieren. Ist das der Fall, k&ouml;nnen Anwender auf die &auml;quivalente Methode
     * {@code until()} definiert in der Klasse {@code TimePoint} ausweichen. </p>
     *
     * @param   <T> generic type of time point
     * @param   start   first time point
     * @param   end     second time point
     * @return  calculated time span between given time points, will be
     *          negative if {@code start} is after {@code end}
     * @see     TimePoint#until(TimePoint, TimeMetric)
     */
    <T extends TimePoint<? super U, T>> P between(
        T start,
        T end
    );

}
