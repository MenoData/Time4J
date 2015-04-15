/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (PluralCategory.java) is part of project Time4J.
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
 * <p>Enumeration of CLDR plural categories. </p>
 *
 * @author  Meno Hochschild
 * @since   1.2
 * @see     PluralRules
 */
/*[deutsch]
 * <p>Aufz&auml;hlung der CLDR-Pluralkategorien. </p>
 *
 * @author  Meno Hochschild
 * @since   1.2
 * @see     PluralRules
 */
public enum PluralCategory {

    //~ Statische Felder/Initialisierungen --------------------------------

    ZERO,

    ONE,

    TWO,

    FEW,

    MANY,

    /**
     * <p>This category serves as fallback if any other category cannot be found
     * in CLDR-data. </p>
     */
    /*[deutsch]
     * <p>Diese Kategorie dient als Ausweichoption, wenn irgendeine andere
     * Kategorie nicht in den CLDR-Daten verf&uuml;gbar ist. </p>
     */
    OTHER;

}
