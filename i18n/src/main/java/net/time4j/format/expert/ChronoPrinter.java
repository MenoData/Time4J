/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
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

import java.io.IOException;


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
     */
    <R> R print(
        T formattable,
        Appendable buffer,
        AttributeQuery attributes,
        ChronoFunction<ChronoDisplay, R> query
    ) throws IOException;

}
