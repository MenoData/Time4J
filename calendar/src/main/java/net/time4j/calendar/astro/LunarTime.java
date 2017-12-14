/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (LunarTime.java) is part of project Time4J.
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

import net.time4j.CalendarUnit;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.engine.CalendarDate;
import net.time4j.scale.TimeScale;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;


/**
 * <p>Contains various routines to determine times of some moon events like moonrise or moonset. </p>
 *
 * @author  Meno Hochschild
 * @since   3.38/4.33
 */
/*[deutsch]
 * <p>Enth&auml;lt diverse Methoden zur Bestimmung der Zeit von lunaren Ereignissen
 * wie Mondaufgang oder Monduntergang. </p>
 *
 * @author  Meno Hochschild
 * @since   3.38/4.33
 */
public final class LunarTime
    implements GeoLocation, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int MRD = 1000000000;

    private static final long serialVersionUID = -8029871830105935048L;

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  the geographical latitude in degrees
     * @since   3.38/4.33
     */
    private final double latitude;

    /**
     * @serial  the geographical longitude in degrees
     * @since   3.38/4.33
     */
    private final double longitude;

    /**
     * @serial  the geographical altitude in meters
     * @since   3.38/4.33
     */
    private final int altitude;

    /**
     * @serial  zone identifier for the interpretation of calendar date input
     * @since   3.38/4.33
     */
    private final TZID observerZoneID;

    //~ Konstruktoren -----------------------------------------------------

    private LunarTime(
        double latitude,
        double longitude,
        int altitude,
        TZID observerZoneID
    ) {
        super();

        check(latitude, longitude, altitude, observerZoneID);

        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.observerZoneID = observerZoneID;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Obtains a builder for creating a new instance of local lunar time. </p>
     *
     * <p>This method is the recommended approach if any given geographical position is described
     * in degrees including arc minutes and arc seconds in order to avoid manual conversions to
     * decimal degrees. </p>
     *
     * @param   observerZoneID  timezone identifier associated with geographical position
     * @return  builder for creating a new instance of local lunar time
     */
    /*[deutsch]
     * <p>Liefert einen {@code Builder} zur Erzeugung einer neuen Instanz einer lokalen Mondzeit. </p>
     *
     * <p>Diese Methode ist der empfohlene Ansatz, wenn irgendeine geographische Positionsangabe
     * in Grad mit Bogenminuten und Bogensekunden vorliegt, um manuelle Umrechnungen in Dezimalangaben
     * zu vermeiden. </p>
     *
     * @param   observerZoneID  timezone identifier associated with geographical position
     * @return  builder for creating a new instance of local lunar time
     */
    public static LunarTime.Builder ofLocation(TZID observerZoneID) {

        return new Builder(observerZoneID);

    }

    /**
     * <p>Obtains the lunar time for given geographical location. </p>
     *
     * <p>This method handles the geographical location in decimal degrees only. If these data are given
     * in degrees, arc minutes and arc seconds then users should apply the {@link #ofLocation(TZID) builder}
     * approach instead. </p>
     *
     * @param   observerZoneID  timezone identifier associated with geographical position
     * @param   latitude        geographical latitude in decimal degrees ({@code -90.0 <= x <= +90.0})
     * @param   longitude       geographical longitude in decimal degrees ({@code -180.0 <= x < 180.0})
     * @return  instance of local lunar time
     * @throws  IllegalArgumentException if the coordinates are out of range or the timezone cannot be loaded
     */
    /*[deutsch]
     * <p>Liefert die Mondzeit zur angegebenen geographischen Position. </p>
     *
     * <p>Diese Methode nimmt geographische Angaben nur in Dezimalgrad entgegen.
     * Wenn diese Daten aber in Grad, Bogenminuten und Bogensekunden vorliegen,
     * sollten Anwender den {@link #ofLocation(TZID) Builder-Ansatz} bevorzugen. </p>
     *
     * @param   observerZoneID  timezone identifier associated with geographical position
     * @param   latitude        geographical latitude in decimal degrees ({@code -90.0 <= x <= +90.0})
     * @param   longitude       geographical longitude in decimal degrees ({@code -180.0 <= x < 180.0})
     * @return  instance of local lunar time
     * @throws  IllegalArgumentException if the coordinates are out of range or the timezone cannot be loaded
     */
    public static LunarTime ofLocation(
        TZID observerZoneID,
        double latitude,
        double longitude
    ) {

        return LunarTime.ofLocation(observerZoneID, latitude, longitude, 0);

    }

    /**
     * <p>Obtains the lunar time for given geographical location. </p>
     *
     * <p>This method handles the geographical location in decimal degrees only. If these data are given
     * in degrees, arc minutes and arc seconds then users should apply the {@link #ofLocation(TZID) builder}
     * approach instead. </p>
     *
     * @param   observerZoneID  timezone identifier associated with geographical position
     * @param   latitude        geographical latitude in decimal degrees ({@code -90.0 <= x <= +90.0})
     * @param   longitude       geographical longitude in decimal degrees ({@code -180.0 <= x < 180.0})
     * @param   altitude        geographical altitude relative to sea level in meters ({@code 0 <= x < 11,0000})
     * @return  instance of local lunar time
     * @throws  IllegalArgumentException if the coordinates are out of range or the timezone cannot be loaded
     */
    /*[deutsch]
     * <p>Liefert die Mondzeit zur angegebenen geographischen Position. </p>
     *
     * <p>Diese Methode nimmt geographische Angaben nur in Dezimalgrad entgegen.
     * Wenn diese Daten aber in Grad, Bogenminuten und Bogensekunden vorliegen,
     * sollten Anwender den {@link #ofLocation(TZID) Builder-Ansatz} bevorzugen. </p>
     *
     * @param   observerZoneID  timezone identifier associated with geographical position
     * @param   latitude        geographical latitude in decimal degrees ({@code -90.0 <= x <= +90.0})
     * @param   longitude       geographical longitude in decimal degrees ({@code -180.0 <= x < 180.0})
     * @param   altitude        geographical altitude relative to sea level in meters ({@code 0 <= x < 11,0000})
     * @return  instance of local lunar time
     * @throws  IllegalArgumentException if the coordinates are out of range or the timezone cannot be loaded
     */
    public static LunarTime ofLocation(
        TZID observerZoneID,
        double latitude,
        double longitude,
        int altitude
    ) {

        return new LunarTime(latitude, longitude, altitude, observerZoneID);

    }

    @Override
    public double getLatitude() {

        return this.latitude;

    }

    @Override
    public double getLongitude() {

        return this.longitude;

    }

    @Override
    public int getAltitude() {

        return this.altitude;

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof LunarTime){
            LunarTime that = (LunarTime) obj;
            return (
                (this.altitude == that.altitude)
                    && (Double.compare(this.latitude, that.latitude) == 0)
                    && (Double.compare(this.longitude, that.longitude) == 0)
                    && this.observerZoneID.canonical().equals(that.observerZoneID.canonical())
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return (7 * hashCode(this.latitude) + 31 * hashCode(this.longitude) + 37 * this.altitude);

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("LunarTime[");
        sb.append(",observer-tz=");
        sb.append(this.observerZoneID.canonical());
        sb.append(",latitude=");
        sb.append(this.latitude);
        sb.append(",longitude=");
        sb.append(this.longitude);
        if (this.altitude != 0) {
            sb.append(",altitude=");
            sb.append(this.altitude);
        }
        sb.append(']');
        return sb.toString();

    }

    /**
     * <p>Determines moonrise and moonset on given calendar date. </p>
     *
     * @param   date            calendar date
     * @return  data with moonrise and moonset
     */
    /*[deutsch]
     * <p>Ermittelt die Daten von Mondaufgang und Monduntergang zum angegebenen Kalenderdatum. </p>
     *
     * @param   date            calendar date
     * @return  data with moonrise and moonset
     */
    public Moonlight on(CalendarDate date) {

        // initialization
        PlainDate d = SolarTime.toGregorian(date);
        Timezone tz = Timezone.of(this.observerZoneID);
        Moment start =
            ((tz.getHistory() == null)
                ? d.at(PlainTime.midnightAtStartOfDay()).in(tz)
                : d.atFirstMoment(this.observerZoneID));

        double mjd0 = JulianDay.ofMeanSolarTime(start).getMJD();
        double longitudeRad = Math.toRadians(this.longitude);
        double cosLatitude = Math.cos(Math.toRadians(this.latitude));
        double sinLatitude = Math.sin(Math.toRadians(this.latitude));
        double geodeticAngle = StdSolarCalculator.TIME4J.getGeodeticAngle(this.latitude, this.altitude);
        double refraction = StdSolarCalculator.refractionOfStdAtmosphere(this.altitude) / 60;
        double deltaT = TimeScale.deltaT(d);
        double hour = 1.0;
        double y_minus = sinAlt(mjd0, 0.0, longitudeRad, cosLatitude, sinLatitude, geodeticAngle, refraction, deltaT);
        double[] result = new double[4];

        // declaration of result data
        boolean above = (y_minus > 0.0); // at start of day
        boolean rises = false;
        boolean sets = false;
        double risingHour = Double.NaN;
        double settingHour = Double.NaN;

        // loop over 2-hour-search-intervals applying quadratic interpolation
        do {
            double y_0 =
                sinAlt(mjd0, hour, longitudeRad, cosLatitude, sinLatitude, geodeticAngle, refraction, deltaT);
            double y_plus =
                sinAlt(mjd0, hour + 1, longitudeRad, cosLatitude, sinLatitude, geodeticAngle, refraction, deltaT);
            int count =
                interpolate(y_minus, y_0, y_plus, result);
            if (count == 1) {
                double root = result[2];
                if (Double.isNaN(root)) {
                    root = result[3];
                }
                if (y_minus < 0.0) {
                    risingHour = hour + root;
                    rises = true;
                } else {
                    settingHour = hour + root;
                    sets = true;
                }
            } else if (count == 2) {
                if (result[1] < 0.0) {
                    risingHour = hour + result[3];
                    settingHour = hour + result[2];
                } else {
                    risingHour = hour + result[2];
                    settingHour = hour + result[3];
                }
                rises = true;
                sets = true;
            }
            y_minus = y_plus;
            hour += 2.0;
        } while (!((hour > 25.0) || (rises && sets))); // (> 25.0)-condition cares about possible 25-h-day (end-of-DST)

        // evaluate moonrise and moonset
        Moment rising = null;
        Moment setting = null;

        if (rises) {
            rising = add(start, risingHour);
            if (!rising.toZonalTimestamp(this.observerZoneID).getCalendarDate().equals(d)) {
                rising = null;
                // rises = false;
            }
        }
        if (sets) {
            setting = add(start, settingHour);
            if (!setting.toZonalTimestamp(this.observerZoneID).getCalendarDate().equals(d)) {
                setting = null;
                // sets = false;
            }
        }

        return new Moonlight(d, this.observerZoneID, rising, setting, above);

    }

    private static Moment add(
        Moment start,
        double hourValue
    ) {

        double total = hourValue * 3600;
        long secs = (long) Math.floor(total);
        long nanos = (long) ((total - secs) * MRD);

        return start
            .plus(secs, TimeUnit.SECONDS)
            .plus(nanos, TimeUnit.NANOSECONDS)
            .with(Moment.PRECISION, TimeUnit.SECONDS);

    }

    // sinus of moon altitude above or below horizon
    private static double sinAlt(
        double mjd0, // earliest moment of calendar date (usually midnight)
        double hour,
        double longitudeRad,
        double cosLatitude,
        double sinLatitude,
        double geodeticAngle,
        double refraction,
        double deltaT
    ) {

        double mjd = mjd0 + hour / 24.0; // UT
        double jct = toJulianCenturies(mjd + (deltaT / 86400));
        double[] data = MoonPosition.calculateMeeus(jct);
        double nutationCorr = data[0] * Math.cos(Math.toRadians(data[1])); // for apparent sidereal time
        double tau = gmst(mjd) + Math.toRadians(nutationCorr) + longitudeRad - Math.toRadians(data[2]);
        double decl = Math.toRadians(data[3]);

        // transformation to horizontal coordinate system
        double sinAltitude = sinLatitude * Math.sin(decl) + cosLatitude * Math.cos(decl) * Math.cos(tau);

        // about impact of horizontal parallax on moon diameter see also Meeus (chapter 15)
        double correction = 0.7275 * getHorizontalParallax(data[4]) - refraction - geodeticAngle;

        // we search for the roots of this function
        return sinAltitude - Math.sin(Math.toRadians(correction));

    }

    // mean sidereal time of Greenwich in radians
    private static double gmst(double mjd) {

        double mjd0 = Math.floor(mjd);
        double ut = 86400 * (mjd - mjd0);
        double jct0 = toJulianCenturies(mjd0);
        double jct = toJulianCenturies(mjd);
        double gmstInSecs =
            24110.54841 + 8640184.812866 * jct0 + 1.0027379093 * ut + (0.093104 - 0.0000062 * jct) * jct * jct;
        double gmstInDays = gmstInSecs / 86400;
        return (gmstInDays - Math.floor(gmstInDays)) * 2 * Math.PI;

    }

    private static double toJulianCenturies(double mjd) {

        return (mjd - 51544.5) / 36525;

    }

    // quadratic interpolation
    private static int interpolate(
        double y_minus, // = f(-1)
        double y_0, // = f(0)
        double y_plus, // = f(+1)
        double[] result // xe, ye, root1, root2
    ) {

        double a = 0.5 * (y_plus + y_minus) - y_0;
        double b = 0.5 * (y_plus - y_minus);

        double xe = -b / (2.0 * a);
        double ye = (a * xe + b) * xe + y_0;
        double dis = b * b - 4 * a * y_0;

        double root1 = Double.NaN;
        double root2 = Double.NaN;
        int count = 0;

        if (dis >= 0) {
            double dx = 0.5 * Math.sqrt(dis) / Math.abs(a);
            if (Math.abs(xe - dx) <= 1.0) {
                root1 = xe - dx;
                count++;
            }
            if (Math.abs(xe + dx) <= 1.0) {
                root2 = xe + dx;
                count++;
            }
        }

        result[0] = xe;
        result[1] = ye;
        result[2] = root1;
        result[3] = root2;

        return count;

    }

    // formula by Meeus
    private static double getHorizontalParallax(double distance) {

        return Math.toDegrees(Math.asin(6378.14 / distance));

    }

    private static int hashCode(double value) {

        long bits = Double.doubleToLongBits(value);
        return (int)(bits ^ (bits >>> 32));

    }

    private static void check(
        double latitude,
        double longitude,
        int altitude,
        TZID observerZoneID
    ) {

        if (Double.isNaN(latitude) || Double.isInfinite(latitude)) {
            throw new IllegalArgumentException("Latitude must be a finite value: " + latitude);
        } else if (Double.isNaN(longitude) || Double.isInfinite(longitude)) {
            throw new IllegalArgumentException("Longitude must be a finite value: " + longitude);
        } else if ((Double.compare(latitude, 90.0) > 0) || (Double.compare(latitude, -90.0) < 0)) {
            throw new IllegalArgumentException("Degrees out of range -90.0 <= latitude <= +90.0: " + latitude);
        } else if ((Double.compare(longitude, 180.0) >= 0) || (Double.compare(longitude, -180.0) < 0)) {
            throw new IllegalArgumentException("Degrees out of range -180.0 <= longitude < +180.0: " + longitude);
        } else if ((altitude < 0) || (altitude >= 11000)) {
            throw new IllegalArgumentException("Meters out of range 0 <= altitude < +11,000: " + altitude);
        } else {
            Timezone.of(observerZoneID); // try to load - can throw IllegalArgumentException
        }

    }

    /**
     * @serialData  Checks the sanity of the state.
     * @param       in                          object input stream
     * @throws IOException                 in any case of I/O-errors
     * @throws      IllegalArgumentException    in any case of inconsistent state
     */
    private void readObject(ObjectInputStream in)
        throws IOException {

        check(this.latitude, this.longitude, this.altitude, this.observerZoneID);

    }

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Helper class to construct a new instance of {@code LunarTime}. </p>
     *
     * @author  Meno Hochschild
     * @since   3.38/4.33
     */
    /*[deutsch]
     * <p>Hilfsklasse f&uuml;r die Erzeugung einer neuen Instanz von {@code LunarTime}. </p>
     *
     * @author  Meno Hochschild
     * @since   3.38/4.33
     */
    public static class Builder {

        //~ Instanzvariablen ----------------------------------------------

        private double latitude = Double.NaN;
        private double longitude = Double.NaN;
        private int altitude = 0;
        private final TZID observerZoneID;

        //~ Konstruktoren -------------------------------------------------

        private Builder(TZID observerZoneID) {
            super();

            this.observerZoneID = observerZoneID;

        }

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Sets the northern latitude in degrees, arc minutes and arc seconds. </p>
         *
         * @param   degrees     degrees in range {@code 0 <= x <= 90}
         * @param   minutes     arc minutes in range {@code 0 <= x < 60}
         * @param   seconds     arc seconds in range {@code 0.0 <= x < 60.0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if any parameter is out of range
         * @throws  IllegalStateException if the latitude has already been set
         * @see     #southernLatitude(int, int, double)
         */
        /*[deutsch]
         * <p>Setzt die n&ouml;rdliche geographische Breite in Grad, Bogenminuten und Bogensekunden. </p>
         *
         * @param   degrees     degrees in range {@code 0 <= x <= 90}
         * @param   minutes     arc minutes in range {@code 0 <= x < 60}
         * @param   seconds     arc seconds in range {@code 0.0 <= x < 60.0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if any parameter is out of range
         * @throws  IllegalStateException if the latitude has already been set
         * @see     #southernLatitude(int, int, double)
         */
        public Builder northernLatitude(
            int degrees,
            int minutes,
            double seconds
        ) {

            check(degrees, minutes, seconds, 90);

            if (Double.isNaN(this.latitude)) {
                this.latitude = degrees + minutes / 60.0 + seconds / 3600.0;
                return this;
            } else {
                throw new IllegalStateException("Latitude has already been set.");
            }

        }

        /**
         * <p>Sets the southern latitude in degrees, arc minutes and arc seconds. </p>
         *
         * @param   degrees     degrees in range {@code 0 <= x <= 90}
         * @param   minutes     arc minutes in range {@code 0 <= x < 60}
         * @param   seconds     arc seconds in range {@code 0.0 <= x < 60.0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if any parameter is out of range
         * @throws  IllegalStateException if the latitude has already been set
         * @see     #northernLatitude(int, int, double)
         */
        /*[deutsch]
         * <p>Setzt die s&uuml;dliche geographische Breite in Grad, Bogenminuten und Bogensekunden. </p>
         *
         * @param   degrees     degrees in range {@code 0 <= x <= 90}
         * @param   minutes     arc minutes in range {@code 0 <= x < 60}
         * @param   seconds     arc seconds in range {@code 0.0 <= x < 60.0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if any parameter is out of range
         * @throws  IllegalStateException if the latitude has already been set
         * @see     #northernLatitude(int, int, double)
         */
        public Builder southernLatitude(
            int degrees,
            int minutes,
            double seconds
        ) {

            check(degrees, minutes, seconds, 90);

            if (Double.isNaN(this.latitude)) {
                this.latitude = -1 * (degrees + minutes / 60.0 + seconds / 3600.0);
                return this;
            } else {
                throw new IllegalStateException("Latitude has already been set.");
            }

        }

        /**
         * <p>Sets the eastern longitude in degrees, arc minutes and arc seconds. </p>
         *
         * @param   degrees     degrees in range {@code 0 <= x < 180}
         * @param   minutes     arc minutes in range {@code 0 <= x < 60}
         * @param   seconds     arc seconds in range {@code 0.0 <= x < 60.0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if any parameter is out of range
         * @throws  IllegalStateException if the longitude has already been set
         * @see     #westernLongitude(int, int, double)
         */
        /*[deutsch]
         * <p>Setzt die &ouml;stliche geographische L&auml;nge in Grad, Bogenminuten und Bogensekunden. </p>
         *
         * @param   degrees     degrees in range {@code 0 <= x < 180}
         * @param   minutes     arc minutes in range {@code 0 <= x < 60}
         * @param   seconds     arc seconds in range {@code 0.0 <= x < 60.0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if any parameter is out of range
         * @throws  IllegalStateException if the longitude has already been set
         * @see     #westernLongitude(int, int, double)
         */
        public Builder easternLongitude(
            int degrees,
            int minutes,
            double seconds
        ) {

            check(degrees, minutes, seconds, 179);

            if (Double.isNaN(this.longitude)) {
                this.longitude = degrees + minutes / 60.0 + seconds / 3600.0;
                return this;
            } else {
                throw new IllegalStateException("Longitude has already been set.");
            }

        }

        /**
         * <p>Sets the western longitude in degrees, arc minutes and arc seconds. </p>
         *
         * @param   degrees     degrees in range {@code 0 <= x <= 180}
         * @param   minutes     arc minutes in range {@code 0 <= x < 60}
         * @param   seconds     arc seconds in range {@code 0.0 <= x < 60.0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if any parameter is out of range
         * @throws  IllegalStateException if the longitude has already been set
         * @see     #easternLongitude(int, int, double)
         */
        /*[deutsch]
         * <p>Setzt die westliche geographische L&auml;nge in Grad, Bogenminuten und Bogensekunden. </p>
         *
         * @param   degrees     degrees in range {@code 0 <= x <= 180}
         * @param   minutes     arc minutes in range {@code 0 <= x < 60}
         * @param   seconds     arc seconds in range {@code 0.0 <= x < 60.0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if any parameter is out of range
         * @throws  IllegalStateException if the longitude has already been set
         * @see     #easternLongitude(int, int, double)
         */
        public Builder westernLongitude(
            int degrees,
            int minutes,
            double seconds
        ) {

            check(degrees, minutes, seconds, 180);

            if (Double.isNaN(this.longitude)) {
                this.longitude = -1 * (degrees + minutes / 60.0 + seconds / 3600.0);
                return this;
            } else {
                throw new IllegalStateException("Longitude has already been set.");
            }

        }

        /**
         * <p>Sets the altitude in meters. </p>
         *
         * <p>The altitude is used to model a geodetic correction as well as a refraction correction based
         * on the simple assumption of a standard atmosphere. Users should keep in mind that the local
         * topology with mountains breaking the horizon line and special weather conditions cannot be taken
         * into account. </p>
         *
         * <p>Attention: Users should also apply an algorithm which is capable of altitude corrections. </p>
         *
         * @param   altitude    geographical altitude relative to sea level in meters ({@code 0 <= x < 11,0000})
         * @return  this instance for method chaining
         */
        /*[deutsch]
         * <p>Setzt die H&ouml;he in Metern. </p>
         *
         * <p>Die H&ouml;henangabe dient der Modellierung einer geod&auml;tischen Korrektur und auch einer
         * Korrektur der atmosph&auml;rischen Lichtbeugung basierend auf der einfachen Annahme einer
         * Standardatmosph&auml;re. Anwender m&uuml;ssen im Auge behalten, da&szlig; die lokale Topologie
         * mit Bergen, die die Horizontlinie unterbrechen und spezielle Wetterbedingungen nicht berechenbar
         * sind. </p>
         *
         * <p>Achtung: Anwender sollten auch einen Algorithmus w&auml;hlen, der in der Lage ist,
         * H&ouml;henkorrekturen vorzunehmen. </p>
         *
         * @param   altitude    geographical altitude relative to sea level in meters ({@code 0 <= x < 11,0000})
         * @return  this instance for method chaining
         */
        public Builder atAltitude(int altitude) {

            if ((altitude < 0) || (altitude >= 11000)) {
                throw new IllegalArgumentException("Meters out of range 0 <= altitude < +11,000: " + altitude);
            }

            this.altitude = altitude;
            return this;

        }

        /**
         * <p>Finishes the build-process. </p>
         *
         * @return  new configured instance of {@code LunarTime}
         * @throws  IllegalStateException if either latitude or longitude have not yet been set
         */
        /*[deutsch]
         * <p>Schlie&szlig;t den Erzeugungs- und Konfigurationsprozess ab. </p>
         *
         * @return  new configured instance of {@code LunarTime}
         * @throws  IllegalStateException if either latitude or longitude have not yet been set
         */
        public LunarTime build() {

            if (Double.isNaN(this.latitude)) {
                throw new IllegalStateException("Latitude was not yet set.");
            } else if (Double.isNaN(this.longitude)) {
                throw new IllegalStateException("Longitude was not yet set.");
            }

            return new LunarTime(this.latitude, this.longitude, this.altitude, this.observerZoneID);

        }

        private static void check(
            int degrees,
            int minutes,
            double seconds,
            int max
        ) {

            if (
                degrees < 0
                || degrees > max
                || ((degrees == max) && (max != 179) && (minutes > 0 || Double.compare(seconds, 0.0) > 0))
            ) {
                double v = degrees + minutes / 60.0 + seconds / 3600.0;
                throw new IllegalArgumentException("Degrees out of range: " + degrees + " (decimal=" + v + ")");
            } else if (minutes < 0 || minutes >= 60) {
                throw new IllegalArgumentException("Arc minutes out of range: " + minutes);
            } else if (Double.isNaN(seconds) || Double.isInfinite(seconds)) {
                throw new IllegalArgumentException("Arc seconds must be finite.");
            } else if (Double.compare(seconds, 0.0) < 0 || Double.compare(seconds, 60.0) >= 0) {
                throw new IllegalArgumentException("Arc seconds out of range: " + seconds);
            }

        }

    }

    /**
     * <p>Collects all moon presence data for a given calendar date and zone of observer. </p>
     *
     * @author  Meno Hochschild
     * @since   3.38/4.33
     */
    /*[deutsch]
     * <p>Sammelt alle Mondpr&auml;senzdaten f&uuml;r einen gegebenen Kalendertag und die Beobachterzeitzone. </p>
     *
     * @author  Meno Hochschild
     * @since   3.38/4.33
     */
    public static class Moonlight {

        //~ Instanzvariablen ----------------------------------------------

        private final TZID observerZoneID;
        private final Moment startOfDay;
        private final Moment endOfDay;
        private final Moment moonrise;
        private final Moment moonset;
        private final boolean above; // at start of day

        //~ Konstruktoren -------------------------------------------------

        private Moonlight(
            PlainDate date,
            TZID observerZoneID,
            Moment moonrise,
            Moment moonset,
            boolean above
        ) {
            super();

            this.observerZoneID = observerZoneID;
            Timezone tz = Timezone.of(observerZoneID);
            PlainDate next = date.plus(1, CalendarUnit.DAYS);

            if (tz.getHistory() == null) {
                this.startOfDay = date.atStartOfDay().in(tz);
                this.endOfDay = next.atStartOfDay().in(tz);
            } else {
                this.startOfDay = date.atFirstMoment(observerZoneID);
                this.endOfDay = next.atFirstMoment(observerZoneID);
            }

            this.moonrise = moonrise;
            this.moonset = moonset;
            this.above = above;

        }

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Obtains the moment of moonrise if it exists. </p>
         *
         * @return  moment of moonrise or {@code null}
         */
        /*[deutsch]
         * <p>Liefert den Moment des Mondaufgangs wenn vorhanden. </p>
         *
         * @return  moment of moonrise or {@code null}
         */
        public Moment moonrise() {

            return this.moonrise;

        }

        /**
         * <p>Obtains the timestamp of moonrise in the local observer timezoone if it exists. </p>
         *
         * @return  local timestamp of moonrise or {@code null}
         */
        /*[deutsch]
         * <p>Liefert den Zeitstempel des Mondaufgangs in der lokalen Zeitzone des Beobachters wenn vorhanden. </p>
         *
         * @return  local timestamp of moonrise or {@code null}
         */
        public PlainTimestamp moonriseLocal() {

            if (this.moonrise == null) {
                return null;
            }

            return this.moonrise.toZonalTimestamp(this.observerZoneID);

        }

        /**
         * <p>Obtains the timestamp of moonrise in given timezoone if it exists. </p>
         *
         * @param   tzid    timezone identifier (which maybe deviates from local observer timezone)
         * @return  zonal timestamp of moonrise or {@code null}
         */
        /*[deutsch]
         * <p>Liefert den Zeitstempel des Mondaufgangs in der angegebenen Zeitzone wenn vorhanden. </p>
         *
         * @param   tzid    timezone identifier (which maybe deviates from local observer timezone)
         * @return  zonal timestamp of moonrise or {@code null}
         */
        public PlainTimestamp moonrise(TZID tzid) {

            if (this.moonrise == null) {
                return null;
            }

            return this.moonrise.toZonalTimestamp(tzid);

        }

        /**
         * <p>Obtains the moment of moonset if it exists. </p>
         *
         * @return  moment of moonset (exclusive) or {@code null}
         */
        /*[deutsch]
         * <p>Liefert den Moment des Monduntergangs wenn vorhanden. </p>
         *
         * @return  moment of moonset (exclusive) or {@code null}
         */
        public Moment moonset() {

            return this.moonset;

        }

        /**
         * <p>Obtains the timestamp of moonset in the local observer timezoone if it exists. </p>
         *
         * @return  local timestamp of moonset or {@code null}
         */
        /*[deutsch]
         * <p>Liefert den Zeitstempel des Monduntergangs in der lokalen Zeitzone des Beobachters wenn vorhanden. </p>
         *
         * @return  local timestamp of moonset or {@code null}
         */
        public PlainTimestamp moonsetLocal() {

            if (this.moonset == null) {
                return null;
            }

            return this.moonset.toZonalTimestamp(this.observerZoneID);

        }

        /**
         * <p>Obtains the timestamp of moonset in given timezoone if it exists. </p>
         *
         * @param   tzid    timezone identifier (which maybe deviates from local observer timezone)
         * @return  zonal timestamp of moonset or {@code null}
         */
        /*[deutsch]
         * <p>Liefert den Zeitstempel des Monduntergangs in der angegebenen Zeitzone wenn vorhanden. </p>
         *
         * @param   tzid    timezone identifier (which maybe deviates from local observer timezone)
         * @return  zonal timestamp of moonset or {@code null}
         */
        public PlainTimestamp moonset(TZID tzid) {

            if (this.moonset == null) {
                return null;
            }

            return this.moonset.toZonalTimestamp(tzid);

        }

        /**
         * <p>Is the moon above the horizon at given moment? </p>
         *
         * <p>Keep in mind that the moon can even be invisible (New Moon) if it is above the horizon. </p>
         *
         * @param   moment  the instant to be queried
         * @return  boolean
         */
        /*[deutsch]
         * <p>Ist zum angegebenen Moment der Mond &uuml;ber dem Horizont? </p>
         *
         * <p>Zu beachten: Der Mond kann auch dann unsichtbar sein (Neumond), wenn er &uuml;ber dem Horizont ist. </p>
         *
         * @param   moment  the instant to be queried
         * @return  boolean
         */
        public boolean isPresent(Moment moment) {

            if (moment.isBefore(this.startOfDay) || !moment.isBefore(this.endOfDay)) {
                return false;
            } else if (this.moonrise == null) {
                if (this.moonset == null) {
                    return this.above;
                } else {
                    assert this.above;
                    return moment.isBefore(this.moonset);
                }
            } else if (this.moonset == null) {
                assert !this.above;
                return !moment.isBefore(this.moonrise);
            } else if (this.moonrise.isBefore(this.moonset)) {
                assert !this.above;
                return !moment.isBefore(this.moonrise) && moment.isBefore(this.moonset);
            } else {
                assert this.above;
                return moment.isBefore(this.moonset) || !moment.isBefore(this.moonrise);
            }

        }

        /**
         * <p>Checks if the moon is always above the horizon. </p>
         *
         * <p>Keep in mind that the moon can even be invisible (New Moon) if it is above the horizon. </p>
         *
         * @return  {@code true} if the moon is always above the horizon else {@code false}
         */
        /*[deutsch]
         * <p>Pr&uuml;ft, ob der Mond immer oberhalb des Horizonts ist. </p>
         *
         * <p>Zu beachten: Der Mond kann auch dann unsichtbar sein (Neumond), wenn er &uuml;ber dem Horizont ist. </p>
         *
         * @return  {@code true} if the moon is always above the horizon else {@code false}
         */
        public boolean isPresentAllDay() {

            return (this.above && (this.moonrise == null) && (this.moonset == null));

        }

        /**
         * <p>Checks if the moon is always below the horizon. </p>
         *
         * <p>Keep in mind that the moon can even be invisible (New Moon) if it is above the horizon. </p>
         *
         * @return  {@code true} if the moon is always below the horizon else {@code false}
         */
        /*[deutsch]
         * <p>Pr&uuml;ft, ob der Mond immer unterhalb des Horizonts ist. </p>
         *
         * <p>Zu beachten: Der Mond kann auch dann unsichtbar sein (Neumond), wenn er &uuml;ber dem Horizont ist. </p>
         *
         * @return  {@code true} if the moon is always below the horizon else {@code false}
         */
        public boolean isAbsent() {

            return (this.length() == 0);

        }

        /**
         * <p>Obtains the length of moonlight in seconds. </p>
         *
         * <p>Note: This method ignores the phase of moon. </p>
         *
         * @return  physical length of moonlight in seconds (without leap seconds)
         * @see     TimeUnit#SECONDS
         */
        /*[deutsch]
         * <p>Liefert die Mondscheindauer in Sekunden. </p>
         *
         * <p>Hinweis: Diese Methode ignoriert die Mondphase. </p>
         *
         * @return  physical length of moonlight in seconds (without leap seconds)
         * @see     TimeUnit#SECONDS
         */
        public int length() {

            if (this.moonrise == null) {
                if (this.moonset == null) {
                    if (this.above) {
                        return (int) this.startOfDay.until(this.endOfDay, TimeUnit.SECONDS);
                    } else {
                        return 0;
                    }
                } else {
                    assert this.above;
                    return (int) this.startOfDay.until(this.moonset, TimeUnit.SECONDS);
                }
            } else if (this.moonset == null) {
                assert !this.above;
                return (int) this.moonrise.until(this.endOfDay, TimeUnit.SECONDS);
            } else if (this.moonrise.isBefore(this.moonset)) {
                assert !this.above;
                return (int) this.moonrise.until(this.moonset, TimeUnit.SECONDS);
            } else {
                assert this.above;
                long sum = this.startOfDay.until(this.moonset, TimeUnit.SECONDS);
                sum += this.moonrise.until(this.endOfDay, TimeUnit.SECONDS);
                return (int) sum;
            }

        }

        /**
         * <p>For debugging purposes. </p>
         *
         * @return  String
         */
        /*[deutsch]
         * <p>F&uuml;r Debugging-Zwecke. </p>
         *
         * @return  String
         */
        @Override
        public String toString() {

            StringBuilder sb = new StringBuilder(128);
            sb.append("Moonlight[");
            sb.append("tz=");
            sb.append(this.observerZoneID.canonical());
            sb.append(" | ");

            if (this.moonrise == null) {
                if (this.moonset == null){
                    sb.append("always ");
                    sb.append(this.above ? "up" : "down");
                } else {
                    sb.append("moonset=");
                    sb.append(this.moonset.toZonalTimestamp(this.observerZoneID));
                }
            } else if (this.moonset == null) {
                sb.append("moonrise=");
                sb.append(this.moonrise.toZonalTimestamp(this.observerZoneID));
            } else if (this.moonrise.isBefore(this.moonset)) {
                sb.append("moonrise=");
                sb.append(this.moonrise.toZonalTimestamp(this.observerZoneID));
                sb.append(" | moonset=");
                sb.append(this.moonset.toZonalTimestamp(this.observerZoneID));
            } else {
                sb.append("moonset=");
                sb.append(this.moonset.toZonalTimestamp(this.observerZoneID));
                sb.append(" | moonrise=");
                sb.append(this.moonrise.toZonalTimestamp(this.observerZoneID));
            }
            sb.append(" | length=");
            sb.append(this.length());
            sb.append(']');
            return sb.toString();

        }

    }

}
