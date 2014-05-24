/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AttributeQuery.java) is part of project Time4J.
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
 * <p>Typsichere Abfrage von Formatattributen zur Steuerung eines
 * Parse-Vorgangs. </p>
 *
 * @author  Meno Hochschild
 * @see     ChronoMerger#createFrom(ChronoEntity, AttributeQuery, boolean)
 */
public interface AttributeQuery {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Ermittelt, ob ein Formatattribut zum angegebenen Schl&uuml;ssel
     * existiert. </p>
     *
     * @param   key     attribute key
     * @return  {@code true} if attribute exists else {@code false}
     */
    boolean contains(AttributeKey<?> key);

    /**
     * <p>Ermittelt ein Formatattribut vom angegebenen Typ. </p>
     *
     * @param   <A> generic type of attribute value
     * @param   key     attribute key
     * @return  attribute value
     * @throws  java.util.NoSuchElementException if attribute does not exist
     */
    <A> A get(AttributeKey<A> key);

    /**
     * <p>Ermittelt ein Formatattribut vom angegebenen Typ. </p>
     *
     * @param   <A> generic type of attribute value
     * @param   key             attribute key
     * @param   defaultValue    replacement value to be used if attribute does
     *                          not exist
     * @return  attribute value
     */
    <A> A get(
        AttributeKey<A> key,
        A defaultValue
    );

}
