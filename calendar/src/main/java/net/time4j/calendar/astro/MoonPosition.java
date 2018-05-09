/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (MoonPosition.java) is part of project Time4J.
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
 * <p>Contains methods for calculating the position of the moon. </p>
 *
 * <p>The position refers to the gemometric center of the moon. </p>
 *
 * @author  Meno Hochschild
 * @since   3.38/4.33
 */
/*[deutsch]
 * <p>Enth&auml;lt Methoden zur Bestimmung der Position des Monds. </p>
 *
 * <p>Die Position bezieht sich auf die gemometrische Mitte des Mondes. </p>
 *
 * @author  Meno Hochschild
 * @since   3.38/4.33
 */
public class MoonPosition
    implements EquatorialCoordinates, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    // Meeus - table 47.a
    private static final int[] A_D = {
        0, 2, 2, 0, 0, 0, 2, 2, 2, 2, 0, 1, 0, 2, 0, 0, 4, 0, 4, 2, 2, 1, 1, 2, 2, 4, 2, 0, 2, 2, 1, 2,
        0, 0, 2, 2, 2, 4, 0, 3, 2, 4, 0, 2, 2, 2, 4, 0, 4, 1, 2, 0, 1, 3, 4, 2, 0, 1, 2, 2
    };
    private static final int[] A_M = {
        0, 0, 0, 0, 1, 0, 0, -1, 0, -1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, -1, 0, 0, 0, 1, 0, -1, 0, -2,
        1, 2, -2, 0, 0, -1, 0, 0, 1, -1, 2, 2, 1, -1, 0, 0, -1, 0, 1, 0, 1, 0, 0, -1, 2, 1, 0, 0
    };
    private static final int[] A_M2 = {
        1, -1, 0, 2, 0, 0, -2, -1, 1, 0, -1, 0, 1, 0, 1, 1, -1, 3, -2, -1, 0, -1, 0, 1, 2, 0, -3, -2, -1, -2, 1, 0,
        2, 0, -1, 1, 0, -1, 2, -1, 1, -2, -1, -1, -2, 0, 1, 4, 0, -2, 0, 2, 1, -2, -3, 2, 1, -1, 3, -1
    };
    private static final int[] A_F = {
        0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, -2, 2, -2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0,
        0, 0, 0, -2, 2, 0, 2, 0, 0, 0, 0, 0, 0, -2, 0, 0, 0, 0, -2, -2, 0, 0, 0, 0, 0, 0, 0, -2
    };
    private static final int[] COEFF_L = {
        6288774, 1274027, 658314, 213618, -185116, -114332, 58793, 57066, 53322, 45758, -40923, -34720, -30383,
        15327, -12528, 10980, 10675, 10034, 8548, -7888, -6766, -5163, 4987, 4036, 3994, 3861, 3665, -2689,
        -2602, 2390, -2348, 2236, -2120, -2069, 2048, -1773, -1595, 1215, -1110, -892, -810, 759, -713, -700,
        691, 596, 549, 537, 520, -487, -399, -381, 351, -340, 330, 327, -323, 299, 294, 0
    };
    private static final int[] COEFF_R = {
        -20905355, -3699111, -2955968, -569925, 48888, -3149, 246158, -152138, -170733, -204586, -129620, 108743,
        104755, 10321, 0, 79661, -34782, -23210, -21636, 24208, 30824, -8379, -16675, -12831, -10445, -11650,
        14403, -7003, 0, 10056, 6322, -9884, 5751, 0, -4950, 4130, 0, -3958, 0, 3258, 2616, -1897, -2117, 2354,
        0, 0, -1423, -1117, -1571, -1739, 0, -4421, 0, 0, 0, 0, 1165, 0, 0, 8752
    };

    // Meeus - table 47.b
    private static final int[] B_D = {
        0, 0, 0, 2, 2, 2, 2, 0, 2, 0, 2, 2, 2, 2, 2, 2, 2, 0, 4, 0, 0, 0, 1, 0, 0, 0, 1, 0, 4, 4,
        0, 4, 2, 2, 2, 2, 0, 2, 2, 2, 2, 4, 2, 2, 0, 2, 1, 1, 0, 2, 1, 2, 0, 4, 4, 1, 4, 1, 4, 2
    };
    private static final int[] B_M = {
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 1, -1, -1, -1, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0,
        0, 0, 0, 0, -1, 0, 0, 0, 0, 1, 1, 0, -1, -2, 0, 1, 1, 1, 1, 1, 0, -1, 1, 0, -1, 0, 0, 0, -1, -2
    };
    private static final int[] B_M2 = {
        0, 1, 1, 0, -1, -1, 0, 2, 1, 2, 0, -2, 1, 0, -1, 0, -1, -1, -1, 0, 0, -1, 0, 1, 1, 0, 0, 3, 0, -1,
        1, -2, 0, 2, 1, -2, 3, 2, -3, -1, 0, 0, 1, 0, 1, 1, 0, 0, -2, -1, 1, -2, 2, -2, -1, 1, 1, -1, 0, 0
    };
    private static final int[] B_F = {
        1, 1, -1, -1, 1, -1, 1, 1, -1, -1, -1, -1, 1, -1, 1, 1, -1, -1, -1, 1, 3, 1, 1, 1, -1, -1, -1, 1, -1, 1,
        -3, 1, -3, -1, -1, 1, -1, 1, -1, 1, 1, 1, 1, -1, 3, -1, -1, 1, -1, -1, 1, -1, 1, -1, -1, -1, -1, -1, -1, 1
    };
    private static final int[] COEFF_B = {
        5128122, 280602, 277693, 173237, 55413, 46271, 32573, 17198, 9266, 8822, 8216, 4324, 4200, -3359, 2463,
        2211, 2065, -1870, 1828, -1794, -1749, -1565, -1491, -1475, -1410, -1344, -1335, 1107, 1021, 833, 777,
        671, 607, 596, 491, -451, 439, 422, 421, -366, -351, 331, 315, 302, -283, -229, 223, 223, -220, -220,
        -185, 181, -177, 176, 166, -164, 132, -119, 115, 107
    };

    private static final int MIO = 1000000;
    private static final long serialVersionUID = 5736859564589473324L;

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  the right ascension of moon in degrees
     * @since   3.38/4.33
     */
    private final double rightAscension;

    /**
     * @serial  the declination of moon in degrees
     * @since   3.38/4.33
     */
    private final double declination;

    /**
     * @serial  the azimuth of moon in degrees (compass orientation)
     * @since   3.38/4.33
     */
    private final double azimuth;

    /**
     * @serial  the elevation of moon above or below the horizon in degrees
     * @since   3.38/4.33
     */
    private final double elevation;

    /**
     * @serial  the distance between the centers of earth and moon in kilometers
     * @since   3.38/4.33
     */
    private final double distance;

    //~ Konstruktoren -----------------------------------------------------

    private MoonPosition(
        double rightAscension,
        double declination,
        double azimuth,
        double elevation,
        double distance
    ) {
        super();

        this.rightAscension = rightAscension;
        this.declination = declination;
        this.azimuth = azimuth;
        this.elevation = elevation;
        this.distance = distance;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Calculates the position of moon at given moment and geographical location. </p>
     *
     * @param   moment      the time when the position of moon is to be determined
     * @param   location    geographical location of observer
     * @return  moon position
     */
    /*[deutsch]
     * <p>Berechnet die Position des Mondes zum angegebenen Zeitpunkt und am angegebenen Beobachterstandpunkt. </p>
     *
     * @param   moment      the time when the position of moon is to be determined
     * @param   location    geographical location of observer
     * @return  moon position
     */
    public static MoonPosition at(
        Moment moment,
        GeoLocation location
    ) {

        double[] data = calculateMeeus(JulianDay.ofEphemerisTime(moment).getCenturyJ2000());
        double ra = Math.toRadians(data[2]);
        double decl = Math.toRadians(data[3]);
        double distance = data[4];
        double latRad = Math.toRadians(location.getLatitude());
        double lngRad = Math.toRadians(location.getLongitude());
        double cosLatitude = Math.cos(latRad);
        double sinLatitude = Math.sin(latRad);
        int altitude = location.getAltitude();

        double mjd = JulianDay.ofMeanSolarTime(moment).getMJD();
        double nutationCorr = data[0] * Math.cos(Math.toRadians(data[1])); // needed for apparent sidereal time
        double tau = AstroUtils.gmst(mjd) + Math.toRadians(nutationCorr) + lngRad - ra;

        // transformation to horizontal coordinate system
        double sinElevation = sinLatitude * Math.sin(decl) + cosLatitude * Math.cos(decl) * Math.cos(tau);
        double elevation = Math.toDegrees(Math.asin(sinElevation));

        double dip = StdSolarCalculator.TIME4J.getGeodeticAngle(location.getLatitude(), altitude);

        if (elevation >= -0.5 - dip) { // if below horizon then we don't apply any correction for refraction
            double parallax = Math.toDegrees(Math.asin(6378.14 / distance));
            double factorTemperaturePressure = AstroUtils.refractionFactorOfStdAtmosphere(altitude);
            double refraction = factorTemperaturePressure * AstroUtils.getRefraction(elevation) / 60;
            elevation = elevation - parallax + refraction; // apparent elevation
            // elevation = elevation - (8.0 / 60); // simplified formula
        }

        double azimuth = // atan2 chosen for correct quadrant
            Math.toDegrees(Math.atan2(Math.sin(tau), Math.cos(tau) * sinLatitude - Math.tan(decl) * cosLatitude)) + 180;

        return new MoonPosition(data[2], data[3], azimuth, elevation, distance);

    }

    /**
     * <p>Determines the event when the moon enters or exits given zodiac constellation. </p>
     *
     * @param   zodiac  the astronomical zodiac constellation defined by IAU
     * @return  event when the moon enters or leaves the zodiac constellation
     * @since   4.37
     */
    /*[deutsch]
     * <p>Bestimmt das Ereignis, wenn der Mond das angegebene Tierkreissternbild betritt oder verl&auml;sst. </p>
     *
     * @param   zodiac  the astronomical zodiac constellation defined by IAU
     * @return  event when the moon enters or leaves the zodiac constellation
     * @since   4.37
     */
    public static Zodiac.Event inConstellationOf(Zodiac zodiac) {

        return Zodiac.Event.ofConstellation('L', zodiac);

    }

    /**
     * <p>Determines the event when the moon enters or exits given zodiac sign (for horoscope purpose). </p>
     *
     * @param   zodiac  the astronomical zodiac sign
     * @return  event when the moon enters or leaves the zodiac sign
     * @throws  IllegalArgumentException if the zodiac is {@link Zodiac#OPHIUCHUS}
     * @since   4.37
     */
    /*[deutsch]
     * <p>Bestimmt das Ereignis, wenn der Mond das angegebene Tierkreiszeichen betritt oder verl&auml;sst
     * (f&uuml;r Horoskope). </p>
     *
     * @param   zodiac  the astronomical zodiac sign
     * @return  event when the moon enters or leaves the zodiac sign
     * @throws  IllegalArgumentException if the zodiac is {@link Zodiac#OPHIUCHUS}
     * @since   4.37
     */
    public static Zodiac.Event inSignOf(Zodiac zodiac) {

        return Zodiac.Event.ofSign('L', zodiac);

    }

    @Override
    public double getRightAscension() {

        return this.rightAscension;

    }

    @Override
    public double getDeclination() {

        return this.declination;

    }

    /**
     * <p>Obtains the azimuth of moon in degrees (compass orientation). </p>
     *
     * <p>The azimuth is the result of a coordinate transformation of right ascension and declination
     * to the horizon system. </p>
     *
     * @return  azimuth in degrees measured from the north (compass orientation)
     */
    /*[deutsch]
     * <p>Liefert den Azimuth des Mondes in Grad (Kompassorientierung). </p>
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
     * <p>Obtains the elevation of moon relative to the horizon in degrees. </p>
     *
     * <p>The elevation is the result of a coordinate transformation of right ascension and declination
     * to the horizon system. </p>
     *
     * @return  elevation in degrees (positive if above the horizon and negative if below the horizon)
     */
    /*[deutsch]
     * <p>Liefert die H&ouml;he des Monds relativ zum Horizont in Grad. </p>
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
     * <p>Obtains the distance between the centers of earth and moon in kilometers. </p>
     *
     * @return  double
     */
    /*[deutsch]
     * <p>Liefert die Entfernung zwischen den Mittelpunkten von Erde und Mond in Kilometern. </p>
     *
     * @return  double
     */
    public double getDistance() {

        return this.distance;

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof MoonPosition) {
            MoonPosition that = (MoonPosition) obj;
            return (
                (this.rightAscension == that.rightAscension)
                && (this.declination == that.declination)
                && (this.azimuth == that.azimuth)
                && (this.elevation == that.elevation)
                && (this.distance == that.distance)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return AstroUtils.hashCode(this.rightAscension)
            + 31 * AstroUtils.hashCode(this.declination)
            + 37 * AstroUtils.hashCode(this.distance);

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(100);
        sb.append("moon-position[ra=");
        sb.append(this.rightAscension);
        sb.append(",decl=");
        sb.append(this.declination);
        sb.append(",azimuth=");
        sb.append(this.azimuth);
        sb.append(",elevation=");
        sb.append(this.elevation);
        sb.append(",distance=");
        sb.append(this.distance);
        sb.append(']');
        return sb.toString();

    }

    // max error given by J. Meeus: 10'' in longitude and 4'' in latitude
    static double[] calculateMeeus(double jct) { // jct = julian centuries since J2000 in ephemeris time

        // Meeus (47.1): L'
        double meanLongitude =
            normalize(
                218.3164477
                    + (481267.88123421 + (-0.0015786 + (1.0 / 538841 + (-1.0 / 65194000) * jct) * jct) * jct) * jct);

        // Meeus (47.2): D
        double meanElongation =
            normalize(
                297.8501921
                    + (445267.1114034 + (-0.0018819 + (1.0 / 545868 + (1.0 / 113065000) * jct) * jct) * jct) * jct);

        // Meeus (47.3): M
        double meanAnomalySun =
            normalize(
                357.5291092 + (35999.0502909 + (-0.0001536 + (1.0 / 24490000) * jct) * jct) * jct);

        // Meeus (47.4): M'
        double meanAnomalyMoon =
            normalize(
                134.9633964
                    + (477198.8675055 + (0.0087414 + (1.0 / 69699 + (1.0 / 14712000) * jct) * jct) * jct) * jct);

        // Meeus (47.5): F
        double meanDistance =
            normalize(
                93.272095
                    + (483202.0175233 + (-0.0036539 + (-1.0 / 3526000 + (1.0 / 863310000) * jct) * jct) * jct) * jct);

        // Meeus (47.6)
        double e = 1 - (0.002516 + 0.0000074 * jct) * jct;
        double ee = e * e;

        double sumL = 0.0;
        double sumR = 0.0;

        for (int i = A_D.length - 1; i >= 0; i--) {
            double eFactor;
            switch (A_M[i]) {
                case -1:
                case 1:
                    eFactor = e;
                    break;
                case -2:
                case 2:
                    eFactor = ee;
                    break;
                default:
                    eFactor = 1;
            }
            double arg = Math.toRadians(
                A_D[i] * meanElongation + A_M[i] * meanAnomalySun + A_M2[i] * meanAnomalyMoon + A_F[i] * meanDistance);
            sumL += (COEFF_L[i] * eFactor * Math.sin(arg));
            sumR += (COEFF_R[i] * eFactor * Math.cos(arg));
        }

        double sumB = 0.0;

        for (int i = B_D.length - 1; i >= 0; i--) {
            double eFactor;
            switch (B_M[i]) {
                case -1:
                case 1:
                    eFactor = e;
                    break;
                case -2:
                case 2:
                    eFactor = ee;
                    break;
                default:
                    eFactor = 1;
            }
            double arg =
                B_D[i] * meanElongation + B_M[i] * meanAnomalySun + B_M2[i] * meanAnomalyMoon + B_F[i] * meanDistance;
            sumB += (COEFF_B[i] * eFactor * Math.sin(Math.toRadians(arg)));
        }

        double a1 = 119.75 + 131.849 * jct;
        double a2 = 53.09 + 479264.29 * jct;
        double a3 = 313.45 + 481266.484 * jct;

        sumL += (
            3958 * Math.sin(Math.toRadians(a1))
                + 1962 * Math.sin(Math.toRadians(meanLongitude - meanDistance))
                + 318 * Math.sin(Math.toRadians(a2))
        );

        sumB += (
            -2235 * Math.sin(Math.toRadians(meanLongitude))
                + 382 * Math.sin(Math.toRadians(a3))
                + 175 * Math.sin(Math.toRadians(a1 - meanDistance))
                + 175 * Math.sin(Math.toRadians(a1 + meanDistance))
                + 127 * Math.sin(Math.toRadians(meanLongitude - meanAnomalyMoon))
                - 115 * Math.sin(Math.toRadians(meanLongitude + meanAnomalyMoon))
        );

        double[] result = new double[5];
        StdSolarCalculator.nutations(jct, result);
        double trueObliquity = StdSolarCalculator.meanObliquity(jct) + result[1];
        double obliquityRad = Math.toRadians(trueObliquity);
        double lngRad = Math.toRadians(meanLongitude + (sumL / MIO) + result[0]);
        double latRad = Math.toRadians(sumB / MIO);
        double distance = 385000.56 + (sumR / 1000); // in km between centers of Earth and Moon

        double ra =
            Math.atan2(
                Math.sin(lngRad) * Math.cos(obliquityRad) - Math.tan(latRad) * Math.sin(obliquityRad),
                Math.cos(lngRad)
            );
        double decl =
            Math.asin(
                Math.sin(latRad) * Math.cos(obliquityRad)
                + Math.cos(latRad) * Math.sin(obliquityRad) * Math.sin(lngRad)
            );

        // already set: result[0] = nutation-in-longitude
        result[1] = trueObliquity; // in degrees
        result[2] = AstroUtils.toRange_0_360(Math.toDegrees(ra));
        result[3] = Math.toDegrees(decl);
        result[4] = distance; // in km
        return result;

    }

    // max error given by J. Meeus: 10'' in longitude
    static double lunarLongitude(double jde) { // apparent moon longitude in degrees

        double jct = (jde - 2451545.0) / 36525; // julian centuries (J2000)

        // Meeus (47.1): L'
        double meanLongitude =
            normalize(
                218.3164477
                    + (481267.88123421 + (-0.0015786 + (1.0 / 538841 + (-1.0 / 65194000) * jct) * jct) * jct) * jct);

        // Meeus (47.2): D
        double meanElongation =
            normalize(
                297.8501921
                    + (445267.1114034 + (-0.0018819 + (1.0 / 545868 + (1.0 / 113065000) * jct) * jct) * jct) * jct);

        // Meeus (47.3): M
        double meanAnomalySun =
            normalize(
                357.5291092 + (35999.0502909 + (-0.0001536 + (1.0 / 24490000) * jct) * jct) * jct);

        // Meeus (47.4): M'
        double meanAnomalyMoon =
            normalize(
                134.9633964
                    + (477198.8675055 + (0.0087414 + (1.0 / 69699 + (1.0 / 14712000) * jct) * jct) * jct) * jct);

        // Meeus (47.5): F
        double meanDistance =
            normalize(
                93.272095
                    + (483202.0175233 + (-0.0036539 + (-1.0 / 3526000 + (1.0 / 863310000) * jct) * jct) * jct) * jct);

        // Meeus (47.6)
        double e = 1 - (0.002516 + 0.0000074 * jct) * jct;
        double ee = e * e;

        double sumL = 0.0;

        for (int i = A_D.length - 1; i >= 0; i--) {
            double eFactor;
            switch (A_M[i]) {
                case -1:
                case 1:
                    eFactor = e;
                    break;
                case -2:
                case 2:
                    eFactor = ee;
                    break;
                default:
                    eFactor = 1;
            }
            double arg = Math.toRadians(
                A_D[i] * meanElongation + A_M[i] * meanAnomalySun + A_M2[i] * meanAnomalyMoon + A_F[i] * meanDistance);
            sumL += (COEFF_L[i] * eFactor * Math.sin(arg));
        }

        double a1 = 119.75 + 131.849 * jct;
        double a2 = 53.09 + 479264.29 * jct;

        sumL += (
            3958 * Math.sin(Math.toRadians(a1))
                + 1962 * Math.sin(Math.toRadians(meanLongitude - meanDistance))
                + 318 * Math.sin(Math.toRadians(a2))
        );

        double[] result = new double[5];
        StdSolarCalculator.nutations(jct, result);
        return AstroUtils.toRange_0_360(meanLongitude + (sumL / MIO) + result[0]);

    }

    private static double normalize(double angleInDegrees) {
        return angleInDegrees - 360 * Math.floor(angleInDegrees / 360);
    }

}
