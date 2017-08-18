/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SolarTime.java) is part of project Time4J.
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

import net.time4j.ClockUnit;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.base.ResourceLoader;
import net.time4j.engine.CalendarDate;
import net.time4j.engine.ChronoFunction;
import net.time4j.engine.EpochDays;
import net.time4j.scale.TimeScale;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.TZID;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


/**
 * Contains various routines to determine solar time.
 *
 * @author  Meno Hochschild
 * @since   3.33/4.28
 */
/*[deutsch]
 * Enth&auml;lt verschiedene Hilfsmittel zur Bestimmung der Sonnenzeit.
 *
 * @author  Meno Hochschild
 * @since   3.33/4.28
 */
public final class SolarTime
    implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final double STD_ZENITH = 90.0 + (50 / 60.0);
    private static final EventCalculator DEFAULT_CALCULATOR;

    static {
        EventCalculator loaded = null;
        for (EventCalculator calculator : ResourceLoader.getInstance().services(EventCalculator.class)) {
            loaded = calculator;
            break;
        }
        DEFAULT_CALCULATOR = ((loaded == null) ? StdCalculator.WILLIAMS : loaded);
    }

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  the geographical latitude in degrees
     * @since   3.34/4.29
     */
    private final double latitude;

    /**
     * @serial  the geographical longitude in degrees
     * @since   3.34/4.29
     */
    private final double longitude;

    /**
     * @serial  the elevation or altitude in meters
     * @since   3.34/4.29
     */
    private final double elevation;

    //~ Konstruktoren -----------------------------------------------------

    private SolarTime(
        double latitude,
        double longitude,
        double elevation
    ) {
        super();

        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Obtains the solar time for given geographical location at sea level. </p>
     *
     * @param   latitude    geographical latitude in degrees ({@code -90.0 <= x <= +90.0})
     * @param   longitude   geographical longitude in degrees ({@code -180.0 <= x < 180.0})
     * @return  instance of local solar time
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Liefert die Sonnenzeit zur angegebenen geographischen Position auf Meeresh&ouml;he. </p>
     *
     * @param   latitude    geographical latitude in degrees ({@code -90.0 <= x <= +90.0})
     * @param   longitude   geographical longitude in degrees ({@code -180.0 <= x < 180.0})
     * @return  instance of local solar time
     * @since   3.34/4.29
     */
    public static SolarTime ofLocation(
        double latitude,
        double longitude
    ) {

        return ofLocation(latitude, longitude, 0);

    }

    /**
     * <p>Obtains the solar time for given geographical location. </p>
     *
     * @param   latitude    geographical latitude in degrees ({@code -90.0 <= x <= +90.0})
     * @param   longitude   geographical longitude in degrees ({@code -180.0 <= x < 180.0})
     * @param   elevation   elevation or altitude relative to sea level in meters ({@code -1,000 <= x < 10,0000})
     * @return  instance of local solar time
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Liefert die Sonnenzeit zur angegebenen geographischen Position. </p>
     *
     * @param   latitude    geographical latitude in degrees ({@code -90.0 <= x <= +90.0})
     * @param   longitude   geographical longitude in degrees ({@code -180.0 <= x < 180.0})
     * @param   elevation   elevation or altitude relative to sea level in meters ({@code -1,000 <= x < 10,0000})
     * @return  instance of local solar time
     * @since   3.34/4.29
     */
    // private because elevation is not yet supported
    private static SolarTime ofLocation(
        double latitude,
        double longitude,
        double elevation
    ) {

        check(latitude, longitude, elevation);

        return new SolarTime(latitude, longitude, elevation);

    }

    /**
     * <p>Obtains the geographical latitude of this instance. </p>
     *
     * @return  latitude in degrees
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Liefert den geographischen Breitengrad dieser Instanz. </p>
     *
     * @return  latitude in degrees
     * @since   3.34/4.29
     */
    public double getLatitude() {

        return this.latitude;

    }

    /**
     * <p>Obtains the geographical longitude of this instance. </p>
     *
     * @return  longitude in degrees
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Liefert den geographischen L&auml;ngengrad dieser Instanz. </p>
     *
     * @return  longitude in degrees
     * @since   3.34/4.29
     */
    public double getLongitude() {

        return this.longitude;

    }

    /**
     * <p>Obtains the geographical elevation of this instance relative to sea level. </p>
     *
     * @return  elevation in meters
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Liefert die geographische H&ouml;he dieser Instanz relativ zum Meeresspiegel. </p>
     *
     * @return  elevation in meters
     * @since   3.34/4.29
     */
    // private because elevation is not yet supported
    private double getElevation() {

        return this.elevation;

    }

    /**
     * <p>Calculates the moment of sunrise at the location of this instance. </p>
     *
     * <p>Example: </p>
     *
     * <pre>
     *     SolarTime hamburg = SolarTime.ofLocation(53.55, 10.0);
     *     Optional&lt;Moment&gt; result = PlainDate.nowInSystemTime().get(hamburg.sunrise());
     *     System.out.println(result.get().toZonalTimestamp(() -&gt; &quot;Europe/Berlin&quot;));
     * </pre>
     *
     * <p>Note: The precision is generally constrained to minutes. </p>
     *
     * @return  sunrise function applicable on any calendar date
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Berechnet den Moment des Sonnenaufgangs an der Position dieser Instanz. </p>
     *
     * <p>Beispiel: </p>
     *
     * <pre>
     *     SolarTime hamburg = SolarTime.ofLocation(53.55, 10.0);
     *     Optional&lt;Moment&gt; result = PlainDate.nowInSystemTime().get(hamburg.sunrise());
     *     System.out.println(result.get().toZonalTimestamp(() -&gt; &quot;Europe/Berlin&quot;));
     * </pre>
     *
     * <p>Hinweis: Die Genauigkeit liegt generell im Minutenbereich. </p>
     *
     * @return  sunrise function applicable on any calendar date
     * @since   3.34/4.29
     */
    public ChronoFunction<CalendarDate, Optional<Moment>> sunrise() {

        double zenith = STD_ZENITH;

        return date -> DEFAULT_CALCULATOR.sunrise(date, this.latitude, this.longitude, zenith);

    }

    /**
     * <p>Calculates the local time of sunrise at the location of this instance in given timezone. </p>
     *
     * <p>Note: The precision is generally constrained to minutes. It is possible in some rare edge cases
     * that the calculated clock time is related to the previous day. </p>
     *
     * @param   tzid    the identifier of the timezone the local time of the result refers to
     * @return  sunrise function applicable on any calendar date
     * @see     #sunrise()
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Berechnet die lokale Uhrzeit des Sonnenaufgangs an der Position dieser Instanz
     * in der angegebenen Zeitzone. </p>
     *
     * <p>Hinweis: Die Genauigkeit liegt generell im Minutenbereich. Es ist in seltenen F&auml;llen
     * m&ouml;glich, da&szlig; die ermittelte Uhrzeit zum vorherigen Tag geh&ouml;rt. </p>
     *
     * @param   tzid    the identifier of the timezone the local time of the result refers to
     * @return  sunrise function applicable on any calendar date
     * @see     #sunrise()
     * @since   3.34/4.29
     */
    public ChronoFunction<CalendarDate, Optional<PlainTime>> sunrise(TZID tzid) {

        double zenith = STD_ZENITH;

        return date -> {
            Optional<Moment> m = DEFAULT_CALCULATOR.sunrise(date, latitude, longitude, zenith);
            if (m.isPresent()) {
                return Optional.of(m.get().toZonalTimestamp(tzid).getWallTime());
            } else {
                return Optional.empty();
            }
        };

    }

    /**
     * <p>Calculates the moment of sunset at the location of this instance. </p>
     *
     * <p>Example: </p>
     *
     * <pre>
     *     SolarTime hamburg = SolarTime.ofLocation(53.55, 10.0);
     *     Optional&lt;Moment&gt; result = PlainDate.nowInSystemTime().get(hamburg.sunset());
     *     System.out.println(result.get().toZonalTimestamp(() -&gt; &quot;Europe/Berlin&quot;));
     * </pre>
     *
     * <p>Note: The precision is generally constrained to minutes. </p>
     *
     * @return  sunset function applicable on any calendar date
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Berechnet den Moment des Sonnenuntergangs an der Position dieser Instanz. </p>
     *
     * <p>Beispiel: </p>
     *
     * <pre>
     *     SolarTime hamburg = SolarTime.ofLocation(53.55, 10.0);
     *     Optional&lt;Moment&gt; result = PlainDate.nowInSystemTime().get(hamburg.sunset());
     *     System.out.println(result.get().toZonalTimestamp(() -&gt; &quot;Europe/Berlin&quot;));
     * </pre>
     *
     * <p>Hinweis: Die Genauigkeit liegt generell im Minutenbereich. </p>
     *
     * @return  sunset function applicable on any calendar date
     * @since   3.34/4.29
     */
    public ChronoFunction<CalendarDate, Optional<Moment>> sunset() {

        double zenith = STD_ZENITH;

        return date -> DEFAULT_CALCULATOR.sunset(date, this.latitude, this.longitude, zenith);

    }

    /**
     * <p>Calculates the local time of sunset at the location of this instance in given timezone. </p>
     *
     * <p>Note: The precision is generally constrained to minutes. It is possible in some rare edge cases
     * that the calculated clock time is related to the next day. </p>
     *
     * @param   tzid    the identifier of the timezone the local time of the result refers to
     * @return  sunset function applicable on any calendar date
     * @see     #sunset()
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Berechnet die lokale Uhrzeit des Sonnenuntergangs an der Position dieser Instanz
     * in der angegebenen Zeitzone. </p>
     *
     * <p>Hinweis: Die Genauigkeit liegt generell im Minutenbereich. Es ist in seltenen F&auml;llen
     * m&ouml;glich, da&szlig; die ermittelte Uhrzeit zum n&auml;chsten Tag geh&ouml;rt. </p>
     *
     * @param   tzid    the identifier of the timezone the local time of the result refers to
     * @return  sunset function applicable on any calendar date
     * @see     #sunset()
     * @since   3.34/4.29
     */
    public ChronoFunction<CalendarDate, Optional<PlainTime>> sunset(TZID tzid) {

        double zenith = STD_ZENITH;

        return date -> {
            Optional<Moment> m = DEFAULT_CALCULATOR.sunset(date, latitude, longitude, zenith);
            if (m.isPresent()) {
                return Optional.of(m.get().toZonalTimestamp(tzid).getWallTime());
            } else {
                return Optional.empty();
            }
        };

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof SolarTime){
            SolarTime that = (SolarTime) obj;
            return (
                (Double.compare(this.latitude, that.latitude) == 0)
                    || (Double.compare(this.longitude, that.longitude) == 0)
                    || (Double.compare(this.elevation, that.elevation) == 0));
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return (
            7 * Double.hashCode(this.latitude)
                + 31 * Double.hashCode(this.longitude)
                + 37 * Double.hashCode(this.elevation)
        );

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("SolarTime[latitude=");
        sb.append(this.latitude);
        sb.append(",longitude=");
        sb.append(this.longitude);
        if (Double.compare(this.elevation, 0.0) != 0) {
            sb.append(",elevation=");
            sb.append(this.elevation);
        }
        sb.append(']');
        return sb.toString();

    }

    /**
     * <p>Determines the apparent solar time of any moment at given local time zone offset. </p>
     *
     * <p>Based on the astronomical equation of time. </p>
     *
     * @param   offset      the time zone offset which might depend on the geographical longitude
     * @return  function for getting the apparent solar time
     * @see     ZonalOffset#atLongitude(OffsetSign, int, int, double)
     * @see     #equationOfTime(Moment)
     */
    /*[deutsch]
     * <p>Ermittelt die wahre Ortszeit zur angegebenen lokalen Zeitzonendifferenz. </p>
     *
     * <p>Basiert auf der astronomischen Zeitgleichung. </p>
     *
     * @param   offset      the time zone offset which might depend on the geographical longitude
     * @return  function for getting the apparent solar time
     * @see     ZonalOffset#atLongitude(OffsetSign, int, int, double)
     * @see     #equationOfTime(Moment)
     */
    public static ChronoFunction<Moment, PlainTimestamp> apparentAt(ZonalOffset offset) {

        return context -> {
            PlainTimestamp meanSolarTime = onAverage(context, offset);
            double eot = equationOfTime(context);
            long secs = (long) Math.floor(eot);
            int nanos = (int) ((eot - secs) * 1_000_000_000);
            return meanSolarTime.plus(secs, ClockUnit.SECONDS).plus(nanos, ClockUnit.NANOS);
        };

    }

    /**
     * <p>Determines the mean solar time of any moment at given local time zone offset. </p>
     *
     * @param   offset      the time zone offset which might depend on the geographical longitude
     * @return  function for getting the mean solar time
     * @see     ZonalOffset#atLongitude(OffsetSign, int, int, double)
     */
    /*[deutsch]
     * <p>Ermittelt die mittlere Ortszeit zur angegebenen lokalen Zeitzonendifferenz. </p>
     *
     * @param   offset      the time zone offset which might depend on the geographical longitude
     * @return  function for getting the mean solar time
     * @see     ZonalOffset#atLongitude(OffsetSign, int, int, double)
     */
    public static ChronoFunction<Moment, PlainTimestamp> onAverage(ZonalOffset offset) {

        return context -> onAverage(context, offset);

    }

    /**
     * <p>Determines the difference between apparent and mean solar time at given moment. </p>
     *
     * <p>See also <a href="https://en.wikipedia.org/wiki/Equation_of_time">Wikipedia</a>. </p>
     *
     * <p>Relation: mean-solar-time + equation-of-time = apparent-solar-time</p>
     *
     * @param   moment  the moment when to determine the equation of time
     * @return  difference between apparent solar time and mean solar time in seconds
     * @throws  IllegalArgumentException if the moment is out of year range -2000/+3000
     */
    /*[deutsch]
     * <p>Bestimmt die Differenz zwischen wahrer und mittlerer Sonnenzeit zum angegebenen Zeitpunkt. </p>
     *
     * <p>Siehe auch <a href="https://de.wikipedia.org/wiki/Zeitgleichung">Wikipedia</a>. </p>
     *
     * <p>Relation: mittlere Sonnezeit + Zeitgleichung = wahre Sonnenzeit</p>
     *
     * @param   moment  the moment when to determine the equation of time
     * @return  difference between apparent solar time and mean solar time in seconds
     * @throws  IllegalArgumentException if the moment is out of year range -2000/+3000
     */
    public static double equationOfTime(Moment moment) {

        double jde = JulianDay.getValue(moment, TimeScale.TT);
        return equationOfTime(jde);

    }

    // Meeus p.185 (lower accuracy model), returns units of second
    // other source: http://adsabs.harvard.edu/full/1989MNRAS.238.1529H
    private static double equationOfTime(double jde) {

        double jct = (jde - 2451545.0) / 36525; // julian centuries (J2000)
        double tanEpsilonHalf = Math.tan(Math.toRadians(obliquity(jct) / 2));
        double y = tanEpsilonHalf * tanEpsilonHalf;
        double l2Rad = Math.toRadians(2 * meanLongitude(jct));
        double e = excentricity(jct);
        double mRad = Math.toRadians(meanAnomaly(jct));
        double sinM = Math.sin(mRad);

        double eot =
            y * Math.sin(l2Rad)
            - 2 * e * sinM
            + 4 * e * y * sinM * Math.cos(l2Rad)
            - y * y * Math.sin(2 * l2Rad) / 2
            - 5 * e * e * Math.sin(2 * mRad) / 4;

        return Math.toDegrees(eot) * 240;

    }

    // Meeus (22.2), in degrees
    private static double obliquity(double jct) {

        double obliquity = 23.0 + 26.0 / 60 + (21.448 + (-46.815 + (-0.00059 + 0.001813 * jct) * jct) * jct) / 3600;
        double corr = 0.00256 * Math.cos(Math.toRadians(125.04 - 1934.136 * jct)); // Meeus (25.8)
        return obliquity + corr;

    }

    // Meeus (25.2), in degrees
    private static double meanLongitude(double jct) {

        return 280.46646 + (36000.76983 + 0.0003032 * jct) * jct;

    }

    // Meeus (25.3), in degrees
    private static double meanAnomaly(double jct) {

        return 357.52911 + (35999.05029 - 0.0001537 * jct) * jct;

    }

    // Meeus (25.4)
    private static double excentricity(double jct) {

        return 0.016708634 - (0.000042037 + 0.0000001267 * jct) * jct;

    }

    private static PlainTimestamp onAverage(Moment context, ZonalOffset offset) {

        Moment ut =
            Moment.of(
                context.getElapsedTime(TimeScale.UT) + 2 * 365 * 86400,
                context.getNanosecond(TimeScale.UT),
                TimeScale.POSIX);
        return ut.toZonalTimestamp(offset);

    }

    private static void check(
        double latitude,
        double longitude,
        double elevation
    ) {

        if ((Double.compare(latitude, 90.0) > 0) || (Double.compare(latitude, -90.0) < 0)) {
            throw new IllegalArgumentException("Degrees out of range -90.0 <= latitude <= +90.0: " + latitude);
        } else if ((Double.compare(longitude, 180.0) >= 0) || (Double.compare(longitude, -180.0) < 0)) {
            throw new IllegalArgumentException("Degrees out of range -180.0 <= longitude < +180.0: " + longitude);
        } else if ((elevation < -1000) || (elevation > 9999)) {
            throw new IllegalArgumentException("Meters out of range -1000 <= elevation < +10,000: " + elevation);
        }

    }

    /**
     * @serialData  Checks the sanity of the state.
     * @param       in                          object input stream
     * @throws      IOException                 in any case of I/O-errors
     * @throws      IllegalArgumentException    in any case of inconsistent state
     */
    private void readObject(ObjectInputStream in)
        throws IOException {

        check(this.latitude, this.longitude, this.elevation);

    }

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>An SPI-interface representing a facade for the calculation engine regarding sunrise or sunset. </p>
     *
     * @since   3.34/4.29
     * @doctags.spec    All implementations must have a public no-arg constructor.
     */
    /*[deutsch]
     * <p>Ein SPI-Interface, das eine Fassade f&uuml;r die Berechnung von Sonnenaufgang oder Sonnenuntergang
     * darstellt. </p>
     *
     * @since   3.34/4.29
     * @doctags.spec    All implementations must have a public no-arg constructor.
     */
    public interface EventCalculator {

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Obtains the name of the calculation method. </p>
         *
         * @return  String
         */
        /*[deutsch]
         * <p>Liefert den Namen der Berechnungsmethode. </p>
         *
         * @return  String
         */
        String name();

        /**
         * <p>Calculates the moment of sunrise. </p>
         *
         * @param   date        the local calendar date
         * @param   latitude    geographical latitude in degrees, positive for North, negative for South
         * @param   longitude   geographical longitude in degrees, positive for East, negative for West
         * @param   zenith      the distance of the center of the sun from geographical local zenith in degrees
         * @return  moment of sunrise if it exists for given parameters else not present (polar day or night)
         * @throws  IllegalArgumentException if any parameter is out of range
         */
        /*[deutsch]
         * <p>Berechnet den Zeitpunkt des Sonnenaufgangs. </p>
         *
         * @param   date        the local calendar date
         * @param   latitude    geographical latitude in degrees, positive for North, negative for South
         * @param   longitude   geographical longitude in degrees, positive for East, negative for West
         * @param   zenith      the distance of the center of the sun from geographical local zenith in degrees
         * @return  moment of sunrise if it exists for given parameters else not present (polar day or night)
         * @throws  IllegalArgumentException if any parameter is out of range
         */
        Optional<Moment> sunrise(
            CalendarDate date,
            double latitude,
            double longitude,
            double zenith
        );

        /**
         * <p>Calculates the moment of sunset. </p>
         *
         * @param   date        the local calendar date
         * @param   latitude    geographical latitude in degrees, positive for North, negative for South
         * @param   longitude   geographical longitude in degrees, positive for East, negative for West
         * @param   zenith      the distance of the center of the sun from geographical local zenith in degrees
         * @return  moment of sunset if it exists for given parameters else not present (polar day or night)
         * @throws  IllegalArgumentException if any parameter is out of range
         */
        /*[deutsch]
         * <p>Berechnet den Zeitpunkt des Sonnenuntergangs. </p>
         *
         * @param   date        the local calendar date
         * @param   latitude    geographical latitude in degrees, positive for North, negative for South
         * @param   longitude   geographical longitude in degrees, positive for East, negative for West
         * @param   zenith      the distance of the center of the sun from geographical local zenith in degrees
         * @return  moment of sunset if it exists for given parameters else not present (polar day or night)
         * @throws  IllegalArgumentException if any parameter is out of range
         */
        Optional<Moment> sunset(
            CalendarDate date,
            double latitude,
            double longitude,
            double zenith
        );

    }

/*
 * More links:
 *
 * http://www.nrel.gov/docs/fy08osti/34302.pdf
 * https://github.com/KosherJava/zmanim/tree/master/src/net/sourceforge/zmanim/util
 * http://www.kosherjava.com/zmanim/docs/api/
 * http://www.jstott.me.uk/jsuntimes/doc-1.0/
 * https://github.com/caarmen/SunriseSunset
 *
 * https://en.wikipedia.org/wiki/Twilight
 * https://astronomy.stackexchange.com/questions/12824/how-long-does-a-sunrise-or-sunset-take
 *
 * http://midcdmz.nrel.gov/solpos/spa.html
 * https://web.archive.org/web/20161202180207/http://williams.best.vwh.net/sunrise_sunset_algorithm.htm
 * http://rredc.nrel.gov/solar/codesandalgorithms/solpos/
 * https://www.esrl.noaa.gov/gmd/grad/solcalc/calcdetails.html
 * http://www.sciencedirect.com/science/article/pii/S0038092X12000400?via%3Dihub
 * https://github.com/KlausBrunner/solarpositioning
 * https://github.com/mikereedell/sunrisesunsetlib-java/issues/33
 */

    private static enum StdCalculator
        implements EventCalculator {

        //~ Statische Felder/Initialisierungen ----------------------------

        /*
            URL:
              http://www.edwilliams.org/sunrise_sunset_algorithm.htm

            Source:
              Almanac for Computers, 1990
              published by Nautical Almanac Office
              United States Naval Observatory
              Washington, DC 20392

            Inputs:
              day, month, year:      date of sunrise/sunset
              latitude, longitude:   location for sunrise/sunset
              zenith:                Sun's zenith for sunrise/sunset
                offical      = 90 degrees 50'
                civil        = 96 degrees
                nautical     = 102 degrees
                astronomical = 108 degrees

            NOTE: longitude is positive for East and negative for West
            NOTE: the algorithm assumes the use of a calculator with the
            trig functions in "degree" (rather than "radian") mode. Most
            programming languages assume radian arguments, requiring back
            and forth convertions. The factor is 180/pi. So, for instance,
            the equation RA = atan(0.91764 * tan(L)) would be coded as RA
            = (180/pi)*atan(0.91764 * tan((pi/180)*L)) to give a degree
            answer with a degree input for L.

            1. first calculate the day of the year

                N1 = floor(275 * month / 9)
                N2 = floor((month + 9) / 12)
                N3 = (1 + floor((year - 4 * floor(year / 4) + 2) / 3))
                N = N1 - (N2 * N3) + day - 30

            2. convert the longitude to hour value and calculate an approximate time

                lngHour = longitude / 15

                if rising time is desired:
                    t = N + ((6 - lngHour) / 24)
                if setting time is desired:
                    t = N + ((18 - lngHour) / 24)

            3. calculate the Sun's mean anomaly

                M = (0.9856 * t) - 3.289

            4. calculate the Sun's true longitude

                L = M + (1.916 * sin(M)) + (0.020 * sin(2 * M)) + 282.634
                NOTE: L potentially needs to be adjusted into the range [0,360) by adding/subtracting 360

            5a. calculate the Sun's right ascension

                RA = atan(0.91764 * tan(L))
                NOTE: RA potentially needs to be adjusted into the range [0,360) by adding/subtracting 360

            5b. right ascension value needs to be in the same quadrant as L

                Lquadrant  = (floor( L/90)) * 90
                RAquadrant = (floor(RA/90)) * 90
                RA = RA + (Lquadrant - RAquadrant)

            5c. right ascension value needs to be converted into hours

                RA = RA / 15

            6. calculate the Sun's declination

                sinDec = 0.39782 * sin(L)
                cosDec = cos(asin(sinDec))

            7a. calculate the Sun's local hour angle

                cosH = (cos(zenith) - (sinDec * sin(latitude))) / (cosDec * cos(latitude))

                if (cosH >  1)
                    the sun never rises on this location (on the specified date)
                if (cosH < -1)
                    the sun never sets on this location (on the specified date)

            7b. finish calculating H and convert into hours

                if rising time is desired:
                    H = 360 - acos(cosH)
                if setting time is desired:
                    H = acos(cosH)

                H = H / 15

            8. calculate local mean time of rising/setting

                T = H + RA - (0.06571 * t) - 6.622

            9. adjust back to UTC

                UT = T - lngHour
                NOTE: UT potentially needs to be adjusted into the range [0,24) by adding/subtracting 24

            10. convert UT value to local time zone of latitude/longitude

                localT = UT + localOffset
        */
        WILLIAMS {
            @Override
            public Optional<Moment> sunrise(CalendarDate date, double latitude, double longitude, double zenith) {
                return event(date, latitude, longitude, zenith, true);
            }
            @Override
            public Optional<Moment> sunset(CalendarDate date, double latitude, double longitude, double zenith) {
                return event(date, latitude, longitude, zenith, false);
            }
            private Optional<Moment> event(
                CalendarDate date,
                double latitude,
                double longitude,
                double zenith,
                boolean sunrise
            ) {
                PlainDate d;
                if (date instanceof PlainDate) {
                    d = (PlainDate) date;
                } else {
                    d = PlainDate.of(date.getDaysSinceEpochUTC(), EpochDays.UTC);
                }
                int doy = d.getDayOfYear();
                double lngHour = longitude / 15;
                double t = doy + (((sunrise ? 6 : 18) - lngHour) / 24);
                double m = (0.9856 * t) - 3.289; // mean anomaly of sun in degrees
                double L = // true longitude of sun in degrees
                    m + (1.916 * Math.sin(Math.toRadians(m))) + (0.020 * Math.sin(2 * Math.toRadians(m))) + 282.634;
                L = adjustRange(L);
                double RA = // right ascension of sun in degrees
                    Math.toDegrees(Math.atan(0.91764 * Math.tan(Math.toRadians(L))));
                RA = adjustRange(RA);
                double Lquadrant  = Math.floor(L / 90) * 90;
                double RAquadrant = Math.floor(RA / 90) * 90;
                RA = (RA + (Lquadrant - RAquadrant)) / 15; // RA in same quadrant as L
                double sinDec = 0.39782 * Math.sin(Math.toRadians(L));
                double cosDec = Math.cos(Math.asin(sinDec));
                double latInRad = Math.toRadians(latitude);
                double cosH = // local hour angle of sun
                    (Math.cos(Math.toRadians(zenith)) - (sinDec * Math.sin(latInRad))) / (cosDec * Math.cos(latInRad));
                if (Double.compare(cosH,  1.0) > 0) {
                    // the sun never rises on this location (on the specified date)
                    return Optional.empty();
                } else if (Double.compare(cosH, -1.0) < 0) {
                    // the sun never sets on this location (on the specified date)
                    return Optional.empty();
                }
                double H = Math.toDegrees(Math.acos(cosH));
                if (sunrise) {
                    H = 360 - H;
                }
                H = H / 15;
                double lmt = H + RA - (0.06571 * t) - 6.622;
                // deviation: (range adjustment applied on lmt, not ut)
                if (Double.compare(0.0, lmt) > 0) {
                    lmt += 24;
                } else if (Double.compare(24.0, lmt) <= 0) {
                    lmt -= 24;
                }
                double ut =  lmt - lngHour;
                int tod = (int) Math.floor(ut * 3600);
                long secs = d.get(EpochDays.UTC) * 86400 + tod;
                // we truncate/neglect the fractional seconds here and round to full minutes
                Moment utc = Moment.of(Math.round(secs / 60.0) * 60, TimeScale.UT);
                return Optional.of(utc.with(Moment.PRECISION, TimeUnit.MINUTES));
            }
            private double adjustRange(double value) { // range [0.0, 360.0)
                if (Double.compare(0.0, value) > 0) {
                    value += 360;
                } else if (Double.compare(value, 360.0) >= 0) {
                    value -= 360;
                }
                return value;
            }
        }

    }

}
