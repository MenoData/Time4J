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
 * @see     ChronoMerger#createFrom(ChronoEntity, AttributeQuery)
 */
public interface AttributeQuery {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Ermittelt, ob ein Formatattribut zum angegebenen Schl&uuml;ssel
     * existiert. </p>
     *
     * @param   key     Attributschl&uuml;ssel
     * @return  {@code true} wenn das Attribut existiert, sonst {@code false}
     */
    boolean contains(AttributeKey<?> key);

    /**
     * <p>Ermittelt ein Formatattribut vom angegebenen Typ. </p>
     *
     * @param   <A> generischer Attributtyp
     * @param   key     Attributschl&uuml;ssel
     * @return  Formatattribut
     * @throws  java.util.NoSuchElementException wenn das Attribut nicht
     *          vorhanden ist
     */
    <A> A get(AttributeKey<A> key);

    /**
     * <p>Ermittelt ein Formatattribut vom angegebenen Typ. </p>
     *
     * @param   <A> generischer Attributtyp
     * @param   key     Attributschl&uuml;ssel
     * @param   defaultValue    Ersatzwert, wenn das Attribut nicht existiert
     * @return  Formatattribut
     */
    <A> A get(
        AttributeKey<A> key,
        A defaultValue
    );

}
