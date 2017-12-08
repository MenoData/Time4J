/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
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
 * @author  Meno Hochschild
 * @since   3.38/4.33
 */
/*[deutsch]
 * <p>Enth&auml;lt Methoden zur Bestimmung der Position des Monds. </p>
 *
 * @author  Meno Hochschild
 * @since   3.38/4.33
 */
public class MoonPosition
    implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int MIO = 1000000;
    private static final long serialVersionUID = -5075806426064082268L;

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
     * @serial  the distance between the centers of earth and moon in kilometers
     * @since   3.38/4.33
     */
    private final double distance;

    //~ Konstruktoren -----------------------------------------------------

    private MoonPosition(
        double rightAscension,
        double declination,
        double distance
    ) {
        super();

        this.rightAscension = rightAscension;
        this.declination = declination;
        this.distance = distance;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Calculates the position of moon in celestial coordinates at given moment. </p>
     *
     * @param   moment  the time when the position of moon is to be determined
     * @return  moon position
     */
    /*[deutsch]
     * <p>Berechnet die Position des Monds in &auml;quatorialen Koordinaten zum angegebenen Zeitpunkt. </p>
     *
     * @param   moment  the time when the position of moon is to be determined
     * @return  moon position
     */
    public static MoonPosition at(Moment moment) {

        return calculateMeeus(JulianDay.ofEphemerisTime(moment).getCenturyJ2000());

    }

    /**
     * <p>Obtains the right ascension of moon in degrees. </p>
     *
     * @return  double
     */
    /*[deutsch]
     * <p>Liefert die Rektaszension des Monds in Grad. </p>
     *
     * @return  double
     */
    public double getRightAscension() {

        return this.rightAscension;

    }

    /**
     * <p>Obtains the declination of moon in degrees. </p>
     *
     * @return  double
     */
    /*[deutsch]
     * <p>Liefert die Deklination des Monds in Grad. </p>
     *
     * @return  double
     */
    public double getDeclination() {

        return this.declination;

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
                && (this.distance == that.distance)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return hashCode(this.rightAscension)
            + 31 * hashCode(this.declination)
            + 37 * hashCode(this.distance);

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(100);
        sb.append("moon-position[ra=");
        sb.append(this.rightAscension);
        sb.append(",decl=");
        sb.append(this.declination);
        sb.append(",dist=");
        sb.append(this.distance);
        sb.append(']');
        return sb.toString();

    }

    // max error given by J. Meeus: 10'' in longitude and 4'' in latitude
    static MoonPosition calculateMeeus(double jct) { // jct = julian centuries since J2000 in ephemeris time

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

        double[] no = new double[2];
        StdSolarCalculator.nutations(jct, no);
        double obliquityRad = Math.toRadians(StdSolarCalculator.meanObliquity(jct) + no[1]);
        double lngRad = Math.toRadians(meanLongitude + (sumL / MIO) + no[0]);
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

        return new MoonPosition(
            Math.toDegrees(ra),
            Math.toDegrees(decl),
            distance
        );

    }

    private static int hashCode(double value) {

        long bits = Double.doubleToLongBits(value);
        return (int)(bits ^ (bits >>> 32));

    }

    private static double normalize(double angleInDegrees) {
        return angleInDegrees - 360 * Math.floor(angleInDegrees / 360);
    }

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

}
