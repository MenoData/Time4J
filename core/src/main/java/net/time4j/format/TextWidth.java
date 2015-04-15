/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TextWidth.java) is part of project Time4J.
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

package net.time4j.format;


/**
 * <p>Defines the width of a formatted output of chronological element values
 * as text. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Definiert die Breite einer formatierten Ausgabe von chronologischen
 * Elementwerten als Text. </p>
 *
 * @author  Meno Hochschild
 */
public enum TextWidth {

    //~ Statische Felder/Initialisierungen --------------------------------

    /** Full text width. */
    /*[deutsch] Volle Textbreite. */
    WIDE,

    /** Abbreviation (long form). */
    /*[deutsch] Abk&uuml;rzung (Langform). */
    ABBREVIATED,

    /**
     * Abbreviation (short form - usually identical with {@link #ABBREVIATED}).
     */
    /*[deutsch]
     * Abk&uuml;rzung (Kurzform - meist identisch mit {@link #ABBREVIATED}).
     */
    SHORT,

    /** Symbol form (typically only one single letter). */
    /*[deutsch] Symbolform (typischerweise nur ein Buchstabe). */
    NARROW;

}
