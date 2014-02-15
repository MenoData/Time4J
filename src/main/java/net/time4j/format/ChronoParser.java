/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoParser.java) is part of project Time4J.
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

package net.time4j.format;


/**
 * <p>Interpretiert einen Text als Entit&auml;t. </p>
 *
 * @param   <T> generic type of chronological entity to be parsed
 * @author  Meno Hochschild
 */
public interface ChronoParser<T> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Interpretiert den angegebenen Text ab der angegebenen Position. </p>
     *
     * <p>Implementierungshinweis: Eine Implementierung wird den Text erst
     * ab der angegebenen Position {@code status.getPosition()} auswerten und
     * dann in der Statusinformation nach einer erfolgreichen Interpretierung
     * die aktuelle Position neu setzen oder im Fehlerfall den Fehlerindex auf
     * die fehlerhafte Stelle im Text setzen. </p>
     *
     * @param   text        text to be parsed
     * @param   status      parser information (always as new instance)
     * @param   attributes  control attributes
     * @return  result or {@code null} if parsing does not work
     * @throws  IndexOutOfBoundsException if the start position is at end of
     *          text or even behind
     */
    T parse(
        CharSequence    text,
        ParseLog        status,
        Attributes attributes
    );

}
