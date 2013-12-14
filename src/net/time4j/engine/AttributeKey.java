/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AttributeKey.java) is part of project Time4J.
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
 * <p>Definiert einen Attributschl&uuml;ssel zum typsicheren Zugriff auf ein
 * Formatattribut. </p>
 *
 * <p>Attribute sind auf Typen beschr&auml;nkt, die <i>immutable</i> sind. </p>
 *
 * @param   <A> generischer Attributtyp
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
