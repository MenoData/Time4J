/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AttributeKey.java) is part of project Time4J.
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
 * <p>Definiert einen Attributschl&uuml;ssel zum typsicheren Zugriff auf ein
 * Formatattribut. </p>
 *
 * <p>Attribute sind auf Typen beschr&auml;nkt, die <i>immutable</i> und
 * serialisierbar sind. Gibt es vordefinierte Attribute, so sind deren
 * Schl&uuml;ssel &uuml;ber die entsprechenden Konstanten in der Klasse
 * {@link net.time4j.format.Attributes} zu verwenden, nicht neue zu
 * erzeugen. </p>
 *
 * @param   <A> generic immutable type of attribute value
 * @author  Meno Hochschild
 * @see     AttributeQuery
 */
public interface AttributeKey<A> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Name des gesuchten Formatattributs. </p>
     *
     * @return  String
     */
    String name();

    /**
     * <p>Typ des gesuchten Formatattributs. </p>
     *
     * @return  Class
     */
    Class<A> type();

}
