/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
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
 * <p>Repr&auml;sentiert eine beliebige zeitliche Abfrage. </p>
 *
 * @param   <T> generic type of source
 * @param   <R> generic type of result
 * @author  Meno Hochschild
 * @see     ChronoEntity#get(ChronoFunction)
 */
// TODO: Ab Java 8 aktiv => extends Function<T, R>
public interface ChronoFunction<T, R> {

    //~ Methoden ----------------------------------------------------------

    /**
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
