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
import net.time4j.PlainTimestamp;
import net.time4j.engine.ChronoFunction;
import net.time4j.scale.TimeScale;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.ZonalOffset;


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
public class SolarTime {

    //~ Konstruktoren -----------------------------------------------------

    private SolarTime() {
        // no instantiation
    }

    //~ Methoden ----------------------------------------------------------

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
    public static ChronoFunction<Moment, PlainTimestamp> apparentAt(final ZonalOffset offset) {

        return new ChronoFunction<Moment, PlainTimestamp>() {
            @Override
            public PlainTimestamp apply(Moment context) {
                PlainTimestamp meanSolarTime = onAverage(context, offset);
                double eot = equationOfTime(context);
                long secs = (long) Math.floor(eot);
                int nanos = (int) ((eot - secs) * 1000000000);
                return meanSolarTime.plus(secs, ClockUnit.SECONDS).plus(nanos, ClockUnit.NANOS);
            }
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
    public static ChronoFunction<Moment, PlainTimestamp> onAverage(final ZonalOffset offset) {

        return new ChronoFunction<Moment, PlainTimestamp>() {
            @Override
            public PlainTimestamp apply(Moment context) {
                return onAverage(context, offset);
            }
        };

    }

    /**
     * <p>Determines the difference between apparent and mean solar time at given moment. </p>
     *
     * <p>See also <a href="https://en.wikipedia.org/wiki/Equation_of_time">Wikipedia</a>. </p>
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

        return 23.0 + 26.0 / 60 + (21.448 + (-46.815 + (-0.00059 + 0.001813 * jct) * jct) * jct) / 3600;

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

}
