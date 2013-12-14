/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoCondition.java) is part of project Time4J.
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
 * <p>Repr&auml;sentiert eine zeitliche Bedingung. </p>
 *
 * <p>Die g&auml;ngigsten Beispiele w&auml;ren etwa Freitag, der 13. oder
 * die Frage, ob ein Feiertag oder ein Wochenende vorliegen. Dieses Interface
 * ist im Prinzip sehr &auml;hnlich zu {@code ChronoQuery<T, Boolean>}, erlaubt
 * aber eine bessere sprachliche Klarheit und wirft keine Ausnahme. </p>
 *
 * @author  Meno Hochschild
 * @see     ChronoEntity#matches(ChronoCondition)
 */
// TODO: Ab Java 8 aktiv => extends Predicate<T>
public interface ChronoCondition<T> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Entscheidet, ob der angegebene Zeitwertkontext diese Bedingung
     * erf&uuml;llt. </p>
     *
     * @param   context     zu bewertender Zeitwertkontext
     * @return  {@code true} wenn der Zeitwertkontext dieser Bedingung
     *          gen&uuml;gt, sonst {@code false}
     */
    boolean test(T context);

}
