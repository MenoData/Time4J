/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SunPosition.java) is part of project Time4J.
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

import net.time4j.Moment;

import java.io.Serializable;


/**
 * <p>Contains methods for calculating the position of the sun. </p>
 *
 * @author  Meno Hochschild
 * @since   3.38/4.33
 */
/*[deutsch]
 * <p>Enth&auml;lt Methoden zur Bestimmung der Position der Sonne. </p>
 *
 * @author  Meno Hochschild
 * @since   3.38/4.33
 */
public class SunPosition
    implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int MIO = 1000000;
    //private static final long serialVersionUID = -5075806426064082268L;

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  the right ascension of sun in degrees
     * @since   3.38/4.33
     */
    private final double rightAscension;

    /**
     * @serial  the declination of sun in degrees
     * @since   3.38/4.33
     */
    private final double declination;

    //~ Konstruktoren -----------------------------------------------------

    private SunPosition(
        double rightAscension,
        double declination
    ) {
        super();

        this.rightAscension = rightAscension;
        this.declination = declination;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Calculates the position of sun in celestial coordinates at given moment. </p>
     *
     * @param   moment  the time when the position of sun is to be determined
     * @return  sun position
     */
    /*[deutsch]
     * <p>Berechnet die Position der Sonne in &auml;quatorialen Koordinaten zum angegebenen Zeitpunkt. </p>
     *
     * @param   moment  the time when the position of sun is to be determined
     * @return  sun position
     */
    public static SunPosition at(Moment moment) {

        double jde = JulianDay.ofEphemerisTime(moment).getValue();
        StdSolarCalculator calculator = StdSolarCalculator.TIME4J;
        return new SunPosition(calculator.rightAscension(jde), calculator.declination(jde));

    }

    /**
     * <p>Obtains the right ascension of sun in degrees. </p>
     *
     * @return  double
     */
    /*[deutsch]
     * <p>Liefert die Rektaszension der Sonne in Grad. </p>
     *
     * @return  double
     */
    public double getRightAscension() {

        return this.rightAscension;

    }

    /**
     * <p>Obtains the declination of sun in degrees. </p>
     *
     * @return  double
     */
    /*[deutsch]
     * <p>Liefert die Deklination der Sonne in Grad. </p>
     *
     * @return  double
     */
    public double getDeclination() {

        return this.declination;

    }

//    /**
//     * <p>Performs a transformation to the horizon system and obtains the azimuth in degrees. </p>
//     *
//     * @param   location    geographical location
//     * @return  azimuth in degrees measured from the north (compass orientation)
//     */
//    /*[deutsch]
//     * <p>F&uuml;hrt eine Koordinatentransformation zum Horizontsystem aus und liefert den Azimuth in Grad. </p>
//     *
//     * @param   location    geographical location
//     * @return  azimuth in degrees measured from the north (compass orientation)
//     */
//    public double getAzimuth(GeoLocation location) {
//        throw new UnsupportedOperationException("Not yet implemented.");
//    }
//
//    /**
//     * <p>Performs a transformation to the horizon system and obtains the elevation respective altitude in degrees. </p>
//     *
//     * @param   location    geographical location
//     * @return  elevation in degrees
//     */
//    /*[deutsch]
//     * <p>F&uuml;hrt eine Koordinatentransformation zum Horizontsystem aus und liefert die H&ouml;he in Grad. </p>
//     *
//     * @param   location    geographical location
//     * @return  elevation in degrees
//     */
//    public double getElevation(GeoLocation location) {
//        throw new UnsupportedOperationException("Not yet implemented.");
//    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof SunPosition) {
            SunPosition that = (SunPosition) obj;
            return (
                (this.rightAscension == that.rightAscension)
                && (this.declination == that.declination)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return hashCode(this.rightAscension) + 31 * hashCode(this.declination);

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(100);
        sb.append("sun-position[ra=");
        sb.append(this.rightAscension);
        sb.append(",decl=");
        sb.append(this.declination);
        sb.append(']');
        return sb.toString();

    }

    private static int hashCode(double value) {

        long bits = Double.doubleToLongBits(value);
        return (int)(bits ^ (bits >>> 32));

    }

}
