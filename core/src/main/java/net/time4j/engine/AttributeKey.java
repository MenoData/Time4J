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
 * <p>Defines a key for a format attribute as type-safe accessor. </p>
 *
 * <p>Attributes are constrained to types which are <i>immutable</i> and
 * serializable. If there are predefined attributes then the associated
 * keys are to be used via the corresponding constants in the class
 * {@link net.time4j.format.Attributes} instead of creating new ones. </p>
 *
 * @param   <A> generic immutable type of attribute value
 * @author  Meno Hochschild
 * @see     AttributeQuery
 */
/*[deutsch]
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
     * <p>Name of associated format attribute. </p>
     *
     * @return  String
     */
    /*[deutsch]
     * <p>Name des assoziierten Formatattributs. </p>
     *
     * @return  String
     */
    String name();

    /**
     * <p>Type of associated format attribute. </p>
     *
     * @return  Class
     */
    /**
     * <p>Typ des assoziierten Formatattributs. </p>
     *
     * @return  Class
     */
    Class<A> type();

}
