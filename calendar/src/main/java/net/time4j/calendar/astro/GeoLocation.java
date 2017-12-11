/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (GeoLocation.java) is part of project Time4J.
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

package net.time4j.calendar.astro;


/**
 * Describes a geographical position with latitude and longitude.
 *
 * @author  Meno Hochschild
 * @since   3.38/4.33
 */
/**
 * Beschreibt eine geographische Position mit Breiten- und L&auml;ngengrad.
 *
 * @author  Meno Hochschild
 * @since   3.38/4.33
 */
public interface GeoLocation {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Obtains the geographical latitude of this instance. </p>
     *
     * @return  latitude in decimal degrees ({@code -90.0 <= x <= +90.0})
     */
    /*[deutsch]
     * <p>Liefert den geographischen Breitengrad dieser Instanz. </p>
     *
     * @return  latitude in decimal degrees ({@code -90.0 <= x <= +90.0})
     */
    double getLatitude();

    /**
     * <p>Obtains the geographical longitude of this instance. </p>
     *
     * @return  longitude in decimal degrees ({@code -180.0 <= x < 180.0})
     */
    /*[deutsch]
     * <p>Liefert den geographischen L&auml;ngengrad dieser Instanz. </p>
     *
     * @return  longitude in decimal degrees ({@code -180.0 <= x < 180.0})
     */
    double getLongitude();

    /**
     * <p>Obtains the geographical altitude of this instance relative to sea level. </p>
     *
     * @return  altitude in meters ({@code 0 <= x < 11,0000})
     */
    /*[deutsch]
     * <p>Liefert die geographische H&ouml;he dieser Instanz relativ zum Meeresspiegel. </p>
     *
     * @return  altitude in meters ({@code 0 <= x < 11,0000})
     */
    int getAltitude();

}
