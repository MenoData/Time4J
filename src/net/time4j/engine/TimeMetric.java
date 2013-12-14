/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimeMetric.java) is part of project Time4J.
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

package net.time4j.engine;


/**
 * <p>Berechnet Abst&auml;nde auf einer Zeitskala als Zeitspannen. </p>
 *
 * @param   <U> Zeiteinheitstyp, der steuert, welche Zeittypen anwendbar sind
 * @param   <P> Zeitspannentyp, der den Ergebnistyp festlegt
 * @author  Meno Hochschild
 */
public interface TimeMetric<U, P extends TimeSpan<?>> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Berechnet den zeitlichen Abstand zwischen zwei Zeitpunkten. </p>
     *
     * @param   <T> generischer Zeitpunkttyp
     * @param   start   erster Bezugszeitpunkt
     * @param   end     zweiter Bezugszeitpunkt
     * @return  berechnete Zeitspanne
     */
    <T extends TimePoint<? super U, T>> P between(
        T start,
        T end
    );

}
