/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (OffsetSign.java) is part of project Time4J.
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

package net.time4j.tz;


/**
 * <p>Represents the sign of a zonal shift. </p>
 *
 * @since   2.0
 */
/*[deutsch]
 * <p>Repr&auml;sentiert das Vorzeichen der zonalen Verschiebung. </p>
 *
 * @since   2.0
 */
public enum OffsetSign {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Negative sign (west for Greenwich meridian). </p>
     */
    /*[deutsch]
     * <p>Negatives Vorzeichen. </p>
     */
    BEHIND_UTC,

    /**
     * <p>Positive sign (also in case of zero offset). </p>
     */
    /*[deutsch]
     * <p>Positives Vorzeichen (auch bei Null-Offset). </p>
     */
    AHEAD_OF_UTC;

}
