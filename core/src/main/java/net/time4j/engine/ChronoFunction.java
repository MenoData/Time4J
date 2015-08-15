/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoFunction.java) is part of project Time4J.
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
 * <p>Represents any temporal query using the strategy pattern approach. </p>
 *
 * @param   <T> generic type of source
 * @param   <R> generic type of result
 * @author  Meno Hochschild
 * @see     ChronoEntity#get(ChronoFunction)
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine beliebige zeitliche Abfrage entsprechend
 * dem Strategie-Entwurfsmuster. </p>
 *
 * @param   <T> generic type of source
 * @param   <R> generic type of result
 * @author  Meno Hochschild
 * @see     ChronoEntity#get(ChronoFunction)
 */
public interface ChronoFunction<T, R> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Reads and evaluates given time value context to a specific result
     * of type R. </p>
     *
     * <p>Will be called by {@link ChronoEntity#get(ChronoFunction)}.
     * Concrete implementations must document if they rather yield
     * {@code null} or throw an exception in case of undefined results. </p>
     *
     * @param   context     time context to be evaluated
     * @return  result of query or {@code null} if undefined
     * @throws  ChronoException if this query is not executable
     */
    /*[deutsch]
     * <p>Liest und interpretiert den angegebenen Zeitwertkontext. </p>
     *
     * <p>Wird von {@link ChronoEntity#get(ChronoFunction)} aufgerufen.
     * Konkrete Implementierungen m&uuml;ssen dokumentieren, ob
     * sie im Fall von undefinierten Ergebnissen eher {@code null}
     * zur&uuml;ckgeben oder stattdessen eine Ausnahme werfen. </p>
     *
     * @param   context     time context to be evaluated
     * @return  result of query or {@code null} if undefined
     * @throws  ChronoException if this query is not executable
     */
    R apply(T context);

}
