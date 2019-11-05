/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2019 Meno Hochschild, <http://www.menodata.de/>
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
 * @param   <P> generic type of duration object
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Berechnet Abst&auml;nde auf einer Zeitachse als Zeitspannen. </p>
 *
 * @param   <U> generic type of time unit
 * @param   <P> generic type of duration object
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

    /**
     * <p>Obtains a modified metric which has reversible characteristics. </p>
     *
     * <p>Usually metrics are not reversible by default. The default implementation throws
     * an <code>UnsupportedOperationException</code>. Overriding implementations should
     * document the details of reversal characteristics. </p>
     *
     * @return  modified reversible time metric
     * @throws  UnsupportedOperationException if not supported
     * @since   5.5
     */
    /*[deutsch]
     * <p>Liefert eine ver&auml;nderte Metrik mit umkehrbaren Eigenschaften. </p>
     *
     * <p>Normalerweise sind Metriken nicht per se umkehrbar. Die Standardimplementierung
     * wirft eine <code>UnsupportedOperationException</code>. Implementierungen, die diese
     * Methode &uuml;berschreiben, sollten die Einzelheiten der Umkehrbarkeit einer Metrik
     * beschreiben. </p>
     *
     * @return  modified reversible time metric
     * @throws  UnsupportedOperationException if not supported
     * @since   5.5
     */
    default TimeMetric<U, P> reversible() {
        throw new UnsupportedOperationException("Not reversible.");
    }

}
