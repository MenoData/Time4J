/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoOperator.java) is part of project Time4J.
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
 * <p>Wendet auf chronologische Objekte eine funktionale Berechnung an und
 * liefert das ge&auml;nderte Objekt zur&uuml;ck. </p>
 *
 * <p>Technische Notiz: Dieses Interface ist in einer Java-pre-8-Umgebung
 * (also Java 6 + 7) nur dann geeignet, wenn der Typ T auf einen finalen Typ
 * eingeschr&auml;nkt wird. Sonst gibt es wegen der mangelnden Typinferenz bei
 * Ausdr&uuml;cken wie {@code entity.with(operator)} einen Compiler-Fehler. Ab
 * Java 8 besteht diese Einschr&auml;nkung nicht mehr. </p>
 *
 * @param   <T> generic type of entities this operator can be applied to
 * @author  Meno Hochschild
 */
// TODO: Ab Java 8 aktiv => extends UnaryOperator<T>
public interface ChronoOperator<T> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Passt die angegebene Entit&auml;t an. </p>
     *
     * <p>Wird von {@link ChronoEntity#with(ChronoOperator)} aufgerufen. </p>
     *
     * @param   entity      chronological entity to be adjusted
     * @return  adjusted copy of argument which itself remains unaffected
     * @throws  ChronoException if there is no element rule for adjusting
     * @throws  IllegalArgumentException if any invalid value is tried
     * @throws  ArithmeticException in case of numerical overflow
     */
    T apply(T entity);

}
