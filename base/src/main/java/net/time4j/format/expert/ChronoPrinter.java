/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoPrinter.java) is part of project Time4J.
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

package net.time4j.format.expert;

import net.time4j.engine.AttributeQuery;
import net.time4j.format.Attributes;

import java.util.Set;


/**
 * <p>Prints a chronological entity. </p>
 *
 * @param   <T> generic type of chronological entity to be formatted
 * @author  Meno Hochschild
 * @since   3.0
 * @see     net.time4j.engine.ChronoEntity
 */
/*[deutsch]
 * <p>Erzeugt eine formatierte Ausgabe einer Entit&auml;t. </p>
 *
 * @param   <T> generic type of chronological entity to be formatted
 * @author  Meno Hochschild
 * @since   3.0
 * @see     net.time4j.engine.ChronoEntity
 */
public interface ChronoPrinter<T> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Prints given chronological entity as formatted text. </p>
     *
     * @param   formattable     object to be formatted
     * @return  formatted text
     * @throws  IllegalArgumentException if given object is not formattable
     * @since   5.0
     */
    /*[deutsch]
     * <p>Formatiert das angegebene Objekt als Text. </p>
     *
     * @param   formattable     object to be formatted
     * @return  formatted text
     * @throws  IllegalArgumentException if given object is not formattable
     * @since   5.0
     */
    default String print(T formattable) {

        StringBuilder buffer = new StringBuilder();
        this.print(formattable, buffer, this.getAttributes());
        return buffer.toString();

    }

    /**
     * <p>Prints given chronological entity as formatted text and writes
     * the text into given buffer. </p>
     *
     * <p>Equivalent to {@code print(formattable, buffer, getAttributes())}. </p>
     *
     * @param   formattable     object to be formatted
     * @param   buffer          text output buffer
     * @return  unmodifiable set of element positions in formatted text, maybe empty
     * @throws  IllegalArgumentException if given object is not formattable
     * @since   4.18
     */
    /*[deutsch]
     * <p>Formatiert das angegebene Objekt als Text und schreibt ihn in
     * den Puffer. </p>
     *
     * <p>Entspricht {@code print(formattable, buffer, getAttributes())}. </p>
     *
     * @param   formattable     object to be formatted
     * @param   buffer          text output buffer
     * @return  unmodifiable set of element positions in formatted text, maybe empty
     * @throws  IllegalArgumentException if given object is not formattable
     * @since   4.18
     */
    default Set<ElementPosition> print(
        T formattable,
        StringBuilder buffer
    ) {

        return this.print(formattable, buffer, this.getAttributes());

    }

    /**
     * <p>Prints given chronological entity as formatted text and writes
     * the text into given buffer. </p>
     *
     * @param   formattable     object to be formatted
     * @param   buffer          text output buffer
     * @param   attributes      format attributes which can control formatting
     * @return  unmodifiable set of element positions in formatted text, maybe empty
     * @throws  IllegalArgumentException if given object is not formattable
     * @since   4.18
     */
    /*[deutsch]
     * <p>Formatiert das angegebene Objekt als Text und schreibt ihn in
     * den Puffer. </p>
     *
     * @param   formattable     object to be formatted
     * @param   buffer          text output buffer
     * @param   attributes      format attributes which can control formatting
     * @return  unmodifiable set of element positions in formatted text, maybe empty
     * @throws  IllegalArgumentException if given object is not formattable
     * @since   4.18
     */
    Set<ElementPosition> print(
        T formattable,
        StringBuilder buffer,
        AttributeQuery attributes
    );

    /**
     * <p>Returns the global format attributes which are active if they are not
     * overridden by sectional attributes. </p>
     *
     * @return  global control attributes valid for the whole formatter
     *          (can be overridden by sectional attributes however)
     * @since   4.18
     */
    /*[deutsch]
     * <p>Ermittelt die globalen Standardattribute, welche genau dann wirksam sind,
     * wenn sie nicht durch sektionale Attribute &uuml;berschrieben werden. </p>
     *
     * @return  global control attributes valid for the whole formatter
     *          (can be overridden by sectional attributes however)
     * @since   4.18
     */
    default AttributeQuery getAttributes() {

        return Attributes.empty();

    }

}
