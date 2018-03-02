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
 * <p>The position refers to the gemometric center of the sun. </p>
 *
 * @author  Meno Hochschild
 * @since   3.38/4.33
 */
/*[deutsch]
 * <p>Enth&auml;lt Methoden zur Bestimmung der Position der Sonne. </p>
 *
 * <p>Die Position bezieht sich auf die gemometrische Mitte der Sonne. </p>
 *
 * @author  Meno Hochschild
 * @since   3.38/4.33
 */
public class SunPosition
    implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = -3023032442869934354L;

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

    /**
     * @serial  the azimuth of sun in degrees (compass orientation)
     * @since   3.38/4.33
     */
    private final double azimuth;

    /**
     * @serial  the elevation of sun above or below the horizon in degrees
     * @since   3.38/4.33
     */
    private final double elevation;

    //~ Konstruktoren -----------------------------------------------------

    private SunPosition(
        double rightAscension,
        double declination,
        double azimuth,
        double elevation
    ) {
        super();

        this.rightAscension = rightAscension;
        this.declination = declination;
        this.azimuth = azimuth;
        this.elevation = elevation;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Calculates the position of sun at given moment and geographical location. </p>
     *
     * @param   moment      the time when the position of sun is to be determined
     * @param   location    geographical location of observer
     * @return  sun position
     */
    /*[deutsch]
     * <p>Berechnet die Position der Sonne zum angegebenen Zeitpunkt und am angegebenen Beobachterstandpunkt. </p>
     *
     * @param   moment      the time when the position of sun is to be determined
     * @param   location    geographical location of observer
     * @return  sun position
     */
    public static SunPosition at(
        Moment moment,
        GeoLocation location
    ) {

        JulianDay jd = JulianDay.ofEphemerisTime(moment);
        double jct = jd.getCenturyJ2000();
        StdSolarCalculator calculator = StdSolarCalculator.TIME4J;
        double[] result = new double[2];
        StdSolarCalculator.nutations(jct, result);
        double nutation = result[0];
        double obliquity = StdSolarCalculator.meanObliquity(jct) + result[1];
        double ra = Math.toRadians(calculator.rightAscension(jd.getValue()));
        double decl = Math.toRadians(calculator.declination(jd.getValue()));
        double latRad = Math.toRadians(location.getLatitude());
        double lngRad = Math.toRadians(location.getLongitude());
        double cosLatitude = Math.cos(latRad);
        double sinLatitude = Math.sin(latRad);
        int altitude = location.getAltitude();

        double mjd = JulianDay.ofMeanSolarTime(moment).getMJD();
        double nutationCorr = nutation * Math.cos(Math.toRadians(obliquity)); // needed for apparent sidereal time
        double tau = AstroUtils.gmst(mjd) + Math.toRadians(nutationCorr) + lngRad - ra;

        // transformation to horizontal coordinate system
        double sinElevation = sinLatitude * Math.sin(decl) + cosLatitude * Math.cos(decl) * Math.cos(tau);
        double elevation = Math.toDegrees(Math.asin(sinElevation));

        double dip = calculator.getGeodeticAngle(location.getLatitude(), altitude);

        if (elevation >= -0.5 - dip) { // if below horizon then we don't apply any correction for refraction
            double factorTemperaturePressure = AstroUtils.refractionFactorOfStdAtmosphere(altitude);
            double refraction = factorTemperaturePressure * AstroUtils.getRefraction(elevation) / 60;
            elevation += refraction; // apparent elevation
            // elevation += (50.0 / 60); // simplified formula
        }

        double azimuth = // atan2 chosen for correct quadrant
            Math.toDegrees(Math.atan2(Math.sin(tau), Math.cos(tau) * sinLatitude - Math.tan(decl) * cosLatitude)) + 180;

        return new SunPosition(Math.toDegrees(ra), Math.toDegrees(decl), azimuth, elevation);

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

    /**
     * <p>Obtains the azimuth of sun in degrees (compass orientation). </p>
     *
     * <p>The azimuth is the result of a coordinate transformation of right ascension and declination
     * to the horizon system. </p>
     *
     * @return  azimuth in degrees measured from the north (compass orientation)
     */
    /*[deutsch]
     * <p>Liefert den Azimuth der Sonne in Grad (Kompassorientierung). </p>
     *
     * <p>Der Azimuth ergibt sich aus der Rektaszension und der Deklination
     * durch eine Koordinatentransformation zum Horizontsystem. </p>
     *
     * @param   location    geographical location
     * @return  azimuth in degrees measured from the north (compass orientation)
     */
    public double getAzimuth() {

        return this.azimuth;

    }

    /**
     * <p>Obtains the elevation of sun relative to the horizon in degrees. </p>
     *
     * <p>The elevation is the result of a coordinate transformation of right ascension and declination
     * to the horizon system. </p>
     *
     * @return  elevation in degrees (positive if above the horizon and negative if below the horizon)
     */
    /*[deutsch]
     * <p>Liefert die H&ouml;he der Sonne relativ zum Horizont in Grad. </p>
     *
     * <p>Die H&ouml;he ergibt sich aus der Rektaszension und der Deklination
     * durch eine Koordinatentransformation zum Horizontsystem. </p>
     *
     * @return  elevation in degrees (positive if above the horizon and negative if below the horizon)
     */
    public double getElevation() {

        return this.elevation;

    }

    /**
     * <p>Determines the length of shadow casted by an object of given height. </p>
     *
     * <p>If the sun is at or below the horizon then the length of shadow is positive infinity. </p>
     *
     * @param   objectHeight    the height of object in meters
     * @return  length of shadow in meters (or positive infinity)
     * @throws  IllegalArgumentException if the object height is not finite and positive
     * @since   3.40/4.35
     */
    /*[deutsch]
     * <p>Bestimmt die L&auml;nge des Schattens, der von einem Objekt der angegebenen H&ouml;he geworfen wird. </p>
     *
     * <p>Wenn die Sonne am oder unter dem Horizont steht, ist die Schattenl&auml;nge positiv unendlich. </p>
     *
     * @param   objectHeight    the height of object in meters
     * @return  length of shadow in meters (or positive infinity)
     * @throws  IllegalArgumentException if the object height is not finite and positive
     * @since   3.40/4.35
     */
    public double getShadowLength(double objectHeight) {

        final double e = this.getElevation();

        if (Double.isInfinite(objectHeight) || Double.isNaN(objectHeight)) {
            throw new IllegalArgumentException("Object height must be finite and positive: " + objectHeight);
        } else {
            if (objectHeight <= 0.0) {
                throw new IllegalArgumentException("Object height must be greater than zero: " + objectHeight);
            } else if (e <= 0.0) {
                return Double.POSITIVE_INFINITY;
            } else if (e == 90.0) {
                return 0.0;
            } else {
                return objectHeight / Math.tan(Math.toRadians(e));
            }
        }

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof SunPosition) {
            SunPosition that = (SunPosition) obj;
            return (
                (this.rightAscension == that.rightAscension)
                && (this.declination == that.declination)
                && (this.azimuth == that.azimuth)
                && (this.elevation == that.elevation)
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
        sb.append(",azimuth=");
        sb.append(this.azimuth);
        sb.append(",elevation=");
        sb.append(this.elevation);
        sb.append(']');
        return sb.toString();

    }

    private static int hashCode(double value) {

        long bits = Double.doubleToLongBits(value);
        return (int)(bits ^ (bits >>> 32));

    }

}
