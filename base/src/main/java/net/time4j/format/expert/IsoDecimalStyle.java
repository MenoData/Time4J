/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (IsoDecimalStyle.java) is part of project Time4J.
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


/**
 * <p>Determines how to print the decimal separator in ISO-format. </p>
 *
 * @author  Meno Hochschild
 * @since   4.18
 */
/*[deutsch]
 * <p>Legt den Stil f&uuml;r das Dezimaltrennzeichen im ISO-Format fest. </p>
 *
 * @author  Meno Hochschild
 * @since   4.18
 */
public enum IsoDecimalStyle {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Official recommendation of original ISO-8601-paper. </p>
     */
    /*[deutsch]
     * <p>Offizielle Empfehlung des ISO-8601-Originals. </p>
     */
    COMMA,

    /**
     * <p>Widely used in any English speaking context, mandated by many ISO-derivates like XML-schema. </p>
     */
    /*[deutsch]
     * <p>Oft im englischsprachigen Kontext verwendet, zwingend in vielen ISO-Ableitungen wie XML-Schema. </p>
     */
    DOT

}
