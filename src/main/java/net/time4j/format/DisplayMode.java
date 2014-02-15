/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (DisplayMode.java) is part of project Time4J.
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
 * <p>Definiert Anzeigestile, wie detailliert chronologische Informationen
 * dargestellt werden. </p>
 *
 * @author  Meno Hochschild
 */
public enum DisplayMode {

    //~ Statische Felder/Initialisierungen --------------------------------

    /** Volle Anzeige mit maximalem Detailgehalt. */
    FULL,

    /** Gespr&auml;chige Anzeige mit vielen Details. */
    LONG,

    /** Normale Anzeige mit wenigen Details. */
    MEDIUM,

    /** Verk&uuml;rzte Anzeige, typischerweise numerisch. */
    SHORT;

}
