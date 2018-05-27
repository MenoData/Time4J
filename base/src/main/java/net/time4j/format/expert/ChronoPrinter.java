/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
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
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoFunction;
import net.time4j.format.Attributes;

import java.io.IOException;
import java.util.Collections;
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
     * <p>Creates a text output and writes it into given buffer. </p>
     *
     * <p>Note: Implementations have to call {@code query.apply(...)}
     * at the end to return a possibly meaningful result. An example
     * would be a query which produces just the identical input so
     * the result of printing a {@code Moment} will be the formatted
     * form of the original {@code Moment}. </p>
     *
     * @param   <R> generic result type
     * @param   formattable chronological entity to be formatted
     * @param   buffer      format buffer any text output will be sent to
     * @param   attributes  control attributes
     * @param   query       custom query returning any kind of result
     * @return  result of query
     * @throws  IllegalArgumentException if the object is not formattable
     * @throws  IOException if writing into buffer fails
     * @deprecated  Beginning with v5.0, this method will be removed. Implementors are asked to override
     *              {@link #print(Object, StringBuilder, AttributeQuery) print(T, StringBuilder, AttributeQuery}
     *              as soon as possible in order to simplify the future migration.
     */
    /*[deutsch]
     * <p>Erzeugt eine Textausgabe und schreibt sie in den angegebenen
     * Puffer. </p>
     *
     * <p>Notiz: Implementierungen m&uuml;ssen schlie&szlig;lich
     * {@code query.apply(...)} aufrufen, um ein Ergebnis
     * zur&uuml;ckzugeben. Ein Beispiel w&auml;re eine Abfrage, die
     * einfach die identische Eingabe zur&uuml;ckgibt, so da&szlig;
     * das Ergebnis dieser Methode angewandt auf einen {@code Moment}
     * die vorformatierte Form des urspr&uuml;nglichen {@code Moment}
     * sein wird.</p>
     *
     * @param   <R> generic result type
     * @param   formattable chronological entity to be formatted
     * @param   buffer      format buffer any text output will be sent to
     * @param   attributes  control attributes
     * @param   query       custom query returning any kind of result
     * @return  result of query
     * @throws  IllegalArgumentException if the object is not formattable
     * @throws  IOException if writing into buffer fails
     * @deprecated  Beginning with v5.0, this method will be removed. Implementors are asked to override
     *              {@link #print(Object, StringBuilder, AttributeQuery) print(T, StringBuilder, AttributeQuery}
     *              as soon as possible in order to simplify the future migration.
     */
    @Deprecated
    <R> R print(
        T formattable,
        Appendable buffer,
        AttributeQuery attributes,
        ChronoFunction<ChronoDisplay, R> query
    ) throws IOException;

    /**
     * <p>Prints given chronological entity as formatted text. </p>
     *
     * @param   formattable     object to be formatted
     * @return  formatted text
     * @throws  IllegalArgumentException if given object is not formattable
     * @since   4.18
     */
    /*[deutsch]
     * <p>Formatiert das angegebene Objekt als Text. </p>
     *
     * @param   formattable     object to be formatted
     * @return  formatted text
     * @throws  IllegalArgumentException if given object is not formattable
     * @since   4.18
     */
    default String format(T formattable) {

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
     * <p>Starting with v5.0, this method will become the SAM-method of this interface and has to be implemented
     * in any suitable way. </p>
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
     * <p>Beginnend mit v5.0 wird diese Methode die SAM-method dieses Interface sein und mu&szlig; in
     * einer geeigneten Weise implementiert werden. </p>
     *
     * @param   formattable     object to be formatted
     * @param   buffer          text output buffer
     * @param   attributes      format attributes which can control formatting
     * @return  unmodifiable set of element positions in formatted text, maybe empty
     * @throws  IllegalArgumentException if given object is not formattable
     * @since   4.18
     */
    default Set<ElementPosition> print( // TODO: remove default-keyword with v5.0
        T formattable,
        StringBuilder buffer,
        AttributeQuery attributes
    ) {

        try {
            return this.print(formattable, buffer, attributes, c -> Collections.emptySet());
        } catch (IOException ioe) {
            throw new AssertionError(ioe);
        }

    }

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
