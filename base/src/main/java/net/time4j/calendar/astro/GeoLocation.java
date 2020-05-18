/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2020 Meno Hochschild, <http://www.menodata.de/>
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
 * Describes a geographical position with latitude, longitude and optionally altitude.
 *
 * @author  Meno Hochschild
 * @since   3.38/4.33
 */
/*[deutsch]
 * Beschreibt eine geographische Position mit Breiten- und L&auml;ngengrad sowie optional H&ouml;he.
 *
 * @author  Meno Hochschild
 * @since   3.38/4.33
 */
public interface GeoLocation {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates an instance with given geographical coordinates on sea level. </p>
     *
     * @param   latitude    geographical latitude in decimal degrees ({@code -90.0 <= x <= +90.0})
     * @param   longitude   geographical longitude in decimal degrees ({@code -180.0 <= x < 180.0})
     * @return  GeoLocation
     * @throws  IllegalArgumentException if the coordinates are out of range
     */
    /*[deutsch]
     * <p>Erzeugt eine Instanz mit den angegebenen geographischen Koordinaten auf Meeresh&ouml;he. </p>
     *
     * @param   latitude    geographical latitude in decimal degrees ({@code -90.0 <= x <= +90.0})
     * @param   longitude   geographical longitude in decimal degrees ({@code -180.0 <= x < 180.0})
     * @return  GeoLocation
     * @throws  IllegalArgumentException if the coordinates are out of range
     */
    static GeoLocation of(
        double latitude,
        double longitude
    ) {

        return GeoLocation.of(latitude, longitude, 0);

    }

    /**
     * <p>Creates an instance with given geographical coordinates. </p>
     *
     * @param   latitude    geographical latitude in decimal degrees ({@code -90.0 <= x <= +90.0})
     * @param   longitude   geographical longitude in decimal degrees ({@code -180.0 <= x < 180.0})
     * @param   altitude    geographical altitude relative to sea level in meters ({@code 0 <= x < 11,0000})
     * @return  GeoLocation
     * @throws  IllegalArgumentException if the coordinates are out of range
     */
    /*[deutsch]
     * <p>Erzeugt eine Instanz mit den angegebenen geographischen Koordinaten. </p>
     *
     * @param   latitude    geographical latitude in decimal degrees ({@code -90.0 <= x <= +90.0})
     * @param   longitude   geographical longitude in decimal degrees ({@code -180.0 <= x < 180.0})
     * @param   altitude    geographical altitude relative to sea level in meters ({@code 0 <= x < 11,0000})
     * @return  GeoLocation
     * @throws  IllegalArgumentException if the coordinates are out of range
     */
    static GeoLocation of(
        double latitude,
        double longitude,
        int altitude
    ) {

        if (!Double.isFinite(latitude)) {
            throw new IllegalArgumentException("Latitude must be a finite value: " + latitude);
        } else if (!Double.isFinite(longitude)) {
            throw new IllegalArgumentException("Longitude must be a finite value: " + longitude);
        } else if ((Double.compare(latitude, 90.0) > 0) || (Double.compare(latitude, -90.0) < 0)) {
            throw new IllegalArgumentException("Degrees out of range -90.0 <= latitude <= +90.0: " + latitude);
        } else if ((Double.compare(longitude, 180.0) >= 0) || (Double.compare(longitude, -180.0) < 0)) {
            throw new IllegalArgumentException("Degrees out of range -180.0 <= longitude < +180.0: " + longitude);
        } else if ((altitude < 0) || (altitude >= 11_000)) {
            throw new IllegalArgumentException("Meters out of range 0 <= altitude < +11,000: " + altitude);
        }

        return new GeoLocation() {
            @Override
            public double getLatitude() {
                return latitude;
            }
            @Override
            public double getLongitude() {
                return longitude;
            }
            @Override
            public int getAltitude() {
                return altitude;
            }
        };

    }

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
