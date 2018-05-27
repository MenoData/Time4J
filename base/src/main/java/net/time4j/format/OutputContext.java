/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (OutputContext.java) is part of project Time4J.
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
 * <p>Determines in which output context the formatting is to be performed. </p>
 *
 * @author  Meno Hochschild
 * @see     Attributes#OUTPUT_CONTEXT
 */
/*[deutsch]
 * <p>Definiert, in welchem Ausgabekontext eine Formatierung erfolgen soll. </p>
 *
 * @author  Meno Hochschild
 * @see     Attributes#OUTPUT_CONTEXT
 */
public enum OutputContext {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Standard format context (for example weekday names within a
     * formatted calendar date). </p>
     */
    /*[deutsch]
     * <p>Standardformatierung (zum Beispiel Wochentagsnamen in einer
     * formatierten Datumsausgabe). </p>
     */
    FORMAT,

    /**
     * <p>Stand-alone-output (for example weekday names as column headers in
     * a month calendar). </p>
     */
    /*[deutsch]
     * <p>Alleinstehende Ausgabe (zum Beispiel Wochentagsnamen als
     * Spalten&uuml;berschriften in einer Monatstabelle). </p>
     */
    STANDALONE;

}
