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

import net.time4j.CalendarUnit;
import net.time4j.ClockUnit;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.base.ResourceLoader;
import net.time4j.engine.CalendarDate;
import net.time4j.engine.ChronoCondition;
import net.time4j.engine.ChronoException;
import net.time4j.engine.ChronoFunction;
import net.time4j.engine.EpochDays;
import net.time4j.scale.TimeScale;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.TZID;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * Contains various routines to determine solar time.
 *
 * <p>Notice: Most chronological functions use the astronomical equation of time. Hence they are only applicable
 * for years between -2000 and +3000 otherwise an {@code IllegalArgumentException} will be thrown. </p>
 *
 * @author  Meno Hochschild
 * @since   3.33/4.28
 */
/*[deutsch]
 * Enth&auml;lt verschiedene Hilfsmittel zur Bestimmung der Sonnenzeit.
 *
 * <p>Notiz: Die meisten chronologischen Funktionen verwenden die astronomische Zeitgleichung. Daher sind
 * sie nur f&uuml;r Jahre zwischen -2000 und +3000 anwendbar, sonst wird gegebenenfalls eine
 * {@code IllegalArgumentException} geworfen. </p>
 *
 * @author  Meno Hochschild
 * @since   3.33/4.28
 */
public final class SolarTime
    implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final double EQUATORIAL_RADIUS = 6378137.0;
    private static final double POLAR_RADIUS = 6356752.3;
    private static final double STD_ZENITH = 90.0 + (50.0 / 60.0);
    private static final Calculator DEFAULT_CALCULATOR;
    private static final Map<String, Calculator> CALCULATORS;

    static {
        Calculator loaded = null;
        Map<String, Calculator> calculators = new HashMap<String, Calculator>();
        for (Calculator calculator : ResourceLoader.getInstance().services(Calculator.class)) {
            loaded = calculator;
            calculators.put(calculator.name(), calculator);
        }
        calculators.put(Calculator.SIMPLE, StdCalculator.SIMPLE);
        calculators.put(Calculator.NOAA, StdCalculator.NOAA);
        CALCULATORS = Collections.unmodifiableMap(calculators);
        DEFAULT_CALCULATOR = ((loaded == null) ? StdCalculator.NOAA : loaded);
    }

    private static final long serialVersionUID = -4816619838743247977L;

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
     * @serial  the geographical altitude in meters
     * @since   3.34/4.29
     */
    private final int altitude;

    /**
     * @serial  name of the calculator for this instance
     * @since   3.34/4.29
     */
    private final String calculator;

    //~ Konstruktoren -----------------------------------------------------

    private SolarTime(
        double latitude,
        double longitude,
        int altitude,
        String calculator
    ) {
        super();

        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.calculator = calculator;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Obtains the solar time for given geographical location at sea level. </p>
     *
     * <p>The default calculator is usually {@link Calculator#NOAA} unless another calculator was
     * set up via the service loader mechnism. </p>
     *
     * @param   latitude    geographical latitude in degrees ({@code -90.0 <= x <= +90.0})
     * @param   longitude   geographical longitude in degrees ({@code -180.0 <= x < 180.0})
     * @return  instance of local solar time
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Liefert die Sonnenzeit zur angegebenen geographischen Position auf Meeresh&ouml;he. </p>
     *
     * <p>Die Standardberechnungsmethode ist gew&ouml;hnlich {@link Calculator#NOAA}, es sei denn,
     * eine andere Methode wurde &uuml;ber den {@code ServiceLoader}-Mechanismus geladen. </p>
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

        return ofLocation(latitude, longitude, 0, DEFAULT_CALCULATOR.name());

    }

    /**
     * <p>Obtains the solar time for given geographical location. </p>
     *
     * @param   latitude    geographical latitude in degrees ({@code -90.0 <= x <= +90.0})
     * @param   longitude   geographical longitude in degrees ({@code -180.0 <= x < 180.0})
     * @param   altitude    geographical altitude relative to sea level in meters ({@code -1,000 <= x < 10,0000})
     * @param   calculator  name of solar time calculator
     * @return  instance of local solar time
     * @see     Calculator#NOAA
     * @see     Calculator#SIMPLE
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Liefert die Sonnenzeit zur angegebenen geographischen Position. </p>
     *
     * @param   latitude    geographical latitude in degrees ({@code -90.0 <= x <= +90.0})
     * @param   longitude   geographical longitude in degrees ({@code -180.0 <= x < 180.0})
     * @param   altitude    geographical altitude relative to sea level in meters ({@code -1,000 <= x < 10,0000})
     * @param   calculator  name of solar time calculator
     * @return  instance of local solar time
     * @see     Calculator#NOAA
     * @see     Calculator#SIMPLE
     * @since   3.34/4.29
     */
    public static SolarTime ofLocation(
        double latitude,
        double longitude,
        int altitude,
        String calculator
    ) {

        check(latitude, longitude, altitude, calculator);

        return new SolarTime(latitude, longitude, altitude, calculator);

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
     * <p>Obtains the geographical altitude of this instance relative to sea level. </p>
     *
     * @return  altitude in meters
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Liefert die geographische H&ouml;he dieser Instanz relativ zum Meeresspiegel. </p>
     *
     * @return  altitude in meters
     * @since   3.34/4.29
     */
    public int getAltitude() {

        return this.altitude;

    }

    /**
     * <p>Obtains the name of the underlying calculator. </p>
     *
     * @return  String
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Liefert den Namen der zugrundeliegenden Berechnungsmethode. </p>
     *
     * @return  String
     * @since   3.34/4.29
     */
    public Calculator getCalculator() {

        return CALCULATORS.get(this.calculator);

    }

    /**
     * <p>Calculates the moment of sunrise at the location of this instance. </p>
     *
     * <p>Example: </p>
     *
     * <pre>
     *     SolarTime hamburg = SolarTime.ofLocation(53.55, 10.0);
     *     Moment result = PlainDate.nowInSystemTime().get(hamburg.sunrise());
     *     System.out.println(result.toZonalTimestamp(() -&gt; &quot;Europe/Berlin&quot;));
     * </pre>
     *
     * <p>Note: The precision is generally constrained to minutes. If there is no sunrise then
     * the function will just yield {@code null}. </p>
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
     *     Moment result = PlainDate.nowInSystemTime().get(hamburg.sunrise());
     *     System.out.println(result.toZonalTimestamp(() -&gt; &quot;Europe/Berlin&quot;));
     * </pre>
     *
     * <p>Hinweis: Die Genauigkeit liegt generell im Minutenbereich. Gibt es keinen Sonnenaufgang,
     * wird die Funktion lediglich {@code null} liefern. </p>
     *
     * @return  sunrise function applicable on any calendar date
     * @since   3.34/4.29
     */
    public ChronoFunction<CalendarDate, Moment> sunrise() {

        return new ChronoFunction<CalendarDate, Moment>() {
            @Override
            public Moment apply(CalendarDate date) {
                return SolarTime.this.getCalculator().sunrise(date, latitude, longitude, zenithAngle());
            }
        };

    }

    /**
     * <p>Calculates the time of given twilight at sunrise and the location of this instance. </p>
     *
     * <p>Note: The precision is generally constrained to minutes. If there is no sunrise then
     * the function will just yield {@code null}. </p>
     *
     * @param   twilight    relevant definition of twilight
     * @return  twilight function at sunrise applicable on any calendar date
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Berechnet die Zeit der angegebenen D&auml;mmerung zum Sonnenaufgang an der Position dieser Instanz. </p>
     *
     * <p>Hinweis: Die Genauigkeit liegt generell im Minutenbereich. Gibt es keinen Sonnenaufgang,
     * wird die Funktion lediglich {@code null} liefern. </p>
     *
     * @param   twilight    relevant definition of twilight
     * @return  twilight function at sunrise applicable on any calendar date
     * @since   3.34/4.29
     */
    public ChronoFunction<CalendarDate, Moment> sunrise(Twilight twilight) {

        final double effAngle = 90.0 + this.sunAngleOfAltitude() + twilight.getAngle();
        return new ChronoFunction<CalendarDate, Moment>() {
            @Override
            public Moment apply(CalendarDate date) {
                return SolarTime.this.getCalculator().sunrise(date, latitude, longitude, effAngle);
            }
        };

    }

    /**
     * <p>Calculates the local time of sunrise at the location of this instance in given timezone. </p>
     *
     * <p>Note: The precision is generally constrained to minutes. It is possible in some rare edge cases
     * that the calculated clock time is related to the previous day. If there is no sunrise then an exception
     * will be thrown. </p>
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
     * m&ouml;glich, da&szlig; die ermittelte Uhrzeit zum vorherigen Tag geh&ouml;rt. Gibt es keinen
     * Sonnenaufgang, wird die Funktion eine Ausnahme werfen. </p>
     *
     * @param   tzid    the identifier of the timezone the local time of the result refers to
     * @return  sunrise function applicable on any calendar date
     * @see     #sunrise()
     * @since   3.34/4.29
     */
    public ChronoFunction<CalendarDate, PlainTime> sunrise(final TZID tzid) {

        return new ChronoFunction<CalendarDate, PlainTime>() {
            @Override
            public PlainTime apply(CalendarDate date) {
                Moment m = SolarTime.this.getCalculator().sunrise(date, latitude, longitude, zenithAngle());
                if (m == null) {
                    throw new ChronoException("No sunrise event.");
                } else {
                    return m.toZonalTimestamp(tzid).getWallTime();
                }
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
     *     Moment result = PlainDate.nowInSystemTime().get(hamburg.sunset());
     *     System.out.println(result.toZonalTimestamp(() -&gt; &quot;Europe/Berlin&quot;));
     * </pre>
     *
     * <p>Note: The precision is generally constrained to minutes. If there is no sunset then
     * the function will just yield {@code null}. </p>
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
     *     Moment result = PlainDate.nowInSystemTime().get(hamburg.sunset());
     *     System.out.println(result.toZonalTimestamp(() -&gt; &quot;Europe/Berlin&quot;));
     * </pre>
     *
     * <p>Hinweis: Die Genauigkeit liegt generell im Minutenbereich. Gibt es keinen Sonnenuntergang,
     * wird die Funktion lediglich {@code null} liefern. </p>
     *
     * @return  sunset function applicable on any calendar date
     * @since   3.34/4.29
     */
    public ChronoFunction<CalendarDate, Moment> sunset() {

        return new ChronoFunction<CalendarDate, Moment>() {
            @Override
            public Moment apply(CalendarDate date) {
                return SolarTime.this.getCalculator().sunset(date, latitude, longitude, zenithAngle());
            }
        };

    }

    /**
     * <p>Calculates the time of given twilight at sunset and the location of this instance. </p>
     *
     * <p>Note: The precision is generally constrained to minutes. If there is no sunset then
     * the function will just yield {@code null}. </p>
     *
     * @param   twilight    relevant definition of twilight
     * @return  twilight function at sunset applicable on any calendar date
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Berechnet die Zeit der angegebenen D&auml;mmerung zum Sonnenuntergang an der Position dieser Instanz. </p>
     *
     * <p>Hinweis: Die Genauigkeit liegt generell im Minutenbereich. Gibt es keinen Sonnenuntergang,
     * wird die Funktion lediglich {@code null} liefern. </p>
     *
     * @param   twilight    relevant definition of twilight
     * @return  twilight function at sunset applicable on any calendar date
     * @since   3.34/4.29
     */
    public ChronoFunction<CalendarDate, Moment> sunset(Twilight twilight) {

        final double effAngle = 90.0 + this.sunAngleOfAltitude() + twilight.getAngle();
        return new ChronoFunction<CalendarDate, Moment>() {
            @Override
            public Moment apply(CalendarDate date) {
                return SolarTime.this.getCalculator().sunset(date, latitude, longitude, effAngle);
            }
        };

    }

    /**
     * <p>Calculates the local time of sunset at the location of this instance in given timezone. </p>
     *
     * <p>Note: The precision is generally constrained to minutes. It is possible in some rare edge cases
     * that the calculated clock time is related to the next day. If there is no sunset then an exception
     * will be thrown. </p>
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
     * m&ouml;glich, da&szlig; die ermittelte Uhrzeit zum n&auml;chsten Tag geh&ouml;rt. Gibt es keinen
     * Sonnenuntergang, wird die Funktion eine Ausnahme werfen. </p>
     *
     * @param   tzid    the identifier of the timezone the local time of the result refers to
     * @return  sunset function applicable on any calendar date
     * @see     #sunset()
     * @since   3.34/4.29
     */
    public ChronoFunction<CalendarDate, PlainTime> sunset(final TZID tzid) {

        return new ChronoFunction<CalendarDate, PlainTime>() {
            @Override
            public PlainTime apply(CalendarDate date) {
                Moment m = SolarTime.this.getCalculator().sunset(date, latitude, longitude, zenithAngle());
                if (m == null) {
                    throw new ChronoException("No sunset event.");
                } else {
                    return m.toZonalTimestamp(tzid).getWallTime();
                }
            }
        };

    }

    /**
     * <p>Queries a given calendar date for its associated sunshine data. </p>
     *
     * @param   tzid    the identifier of the timezone any local times of the result refer to
     * @return  function for obtaining sunshine data
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Fragt zu einem gegebenen Kalenderdatum die damit verbundenen Sonnenscheindaten ab. </p>
     *
     * @param   tzid    the identifier of the timezone any local times of the result refer to
     * @return  function for obtaining sunshine data
     * @since   3.34/4.29
     */
    public ChronoFunction<CalendarDate, Sunshine> sunshine(final TZID tzid) {

        return new ChronoFunction<CalendarDate, Sunshine>() {
            @Override
            public Sunshine apply(CalendarDate date) {
                PlainDate d = toGregorian(date);
                Calculator c = SolarTime.this.getCalculator();
                double zenith = SolarTime.this.zenithAngle();
                Moment start = c.sunrise(date, SolarTime.this.latitude, SolarTime.this.longitude, zenith);
                Moment end = c.sunset(date, SolarTime.this.latitude, SolarTime.this.longitude, zenith);
                boolean absent = false;
                if (start == null && end == null) {
                    double elevation = SolarTime.this.getHighestElevationOfSun(d);
                    if (Double.compare(elevation, 90 - zenith) < 0) {
                        absent = true;
                    }
                }
                return new Sunshine(d, start, end, tzid, absent);
            }
        };

    }

    /**
     * <p>Determines if the sun is invisible all day on a given calendar date. </p>
     *
     * @return  ChronoCondition
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Ermittelt, ob an einem gegebenen Kalenderdatum Polarnacht herrscht. </p>
     *
     * @return  ChronoCondition
     * @since   3.34/4.29
     */
    public ChronoCondition<CalendarDate> polarNight() {

        return new ChronoCondition<CalendarDate>() {
            @Override
            public boolean test(CalendarDate date) {
                if (Double.compare(Math.abs(SolarTime.this.latitude), 66.0) < 0) {
                    return false;
                }
                PlainDate d = toGregorian(date);
                Calculator c = SolarTime.this.getCalculator();
                double zenith = SolarTime.this.zenithAngle();
                Moment start = c.sunrise(date, SolarTime.this.latitude, SolarTime.this.longitude, zenith);
                Moment end = c.sunset(date, SolarTime.this.latitude, SolarTime.this.longitude, zenith);
                if (start != null || end != null) {
                    return false;
                }
                double elevation = SolarTime.this.getHighestElevationOfSun(d);
                return (Double.compare(elevation, 90 - zenith) < 0);
            }
        };

    }

    /**
     * <p>Determines if the sun is visible all day on a given calendar date. </p>
     *
     * @return  ChronoCondition
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Ermittelt, ob an einem gegebenen Kalenderdatum Mitternachtssonne herrscht. </p>
     *
     * @return  ChronoCondition
     * @since   3.34/4.29
     */
    public ChronoCondition<CalendarDate> midnightSun() {

        return new ChronoCondition<CalendarDate>() {
            @Override
            public boolean test(CalendarDate date) {
                if (Double.compare(Math.abs(SolarTime.this.latitude), 66.0) < 0) {
                    return false;
                }
                PlainDate d = toGregorian(date);
                Calculator c = SolarTime.this.getCalculator();
                double zenith = SolarTime.this.zenithAngle();
                Moment start = c.sunrise(date, SolarTime.this.latitude, SolarTime.this.longitude, zenith);
                Moment end = c.sunset(date, SolarTime.this.latitude, SolarTime.this.longitude, zenith);
                if (start != null || end != null) {
                    return false;
                }
                double elevation = SolarTime.this.getHighestElevationOfSun(d);
                return (Double.compare(elevation, 90 - zenith) > 0);
            }
        };

    }

    /**
     * <p>Calculates the moment of noon at the location of this instance (solar transit). </p>
     *
     * <p>Note: The transit time does not tell if the sun is above or below the horizon. </p>
     *
     * @return  noon function applicable on any calendar date
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Berechnet den Moment der h&ouml;chsten Position der Sonne an der Position dieser Instanz. </p>
     *
     * <p>Hinweis: Die Transit-Zeit besagt nicht, ob die Sonne &uuml;ber oder unter dem Horizont ist. </p>
     *
     * @return  noon function applicable on any calendar date
     * @since   3.34/4.29
     */
    public ChronoFunction<CalendarDate, Moment> transitAtNoon() {

        return new ChronoFunction<CalendarDate, Moment>() {
            @Override
            public Moment apply(CalendarDate date) {
                return transitAtNoon(date, longitude, calculator);
            }
        };

    }

    /**
     * <p>Calculates the local time of noon at the location of this instance in given timezone. </p>
     *
     * <p>Note: The transit time does not tell if the sun is above or below the horizon. </p>
     *
     * @param   tzid    the identifier of the timezone the local time of the result refers to
     * @return  noon function applicable on any calendar date
     * @see     #transitAtNoon()
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Berechnet die lokale Uhrzeit des Mittags an der Position dieser Instanz
     * in der angegebenen Zeitzone. </p>
     *
     * <p>Hinweis: Die Transit-Zeit besagt nicht, ob die Sonne &uuml;ber oder unter dem Horizont ist. </p>
     *
     * @param   tzid    the identifier of the timezone the local time of the result refers to
     * @return  noon function applicable on any calendar date
     * @see     #transitAtNoon()
     * @since   3.34/4.29
     */
    public ChronoFunction<CalendarDate, PlainTime> transitAtNoon(final TZID tzid) {

        return new ChronoFunction<CalendarDate, PlainTime>() {
            @Override
            public PlainTime apply(CalendarDate date) {
                Moment m = transitAtNoon(date, SolarTime.this.longitude, SolarTime.this.calculator);
                return m.toZonalTimestamp(tzid).getWallTime();
            }
        };

    }

    /**
     * <p>Calculates the moment of midnight at the location of this instance (lowest position of sun). </p>
     *
     * <p>Note: The transit time does not tell if the sun is above or below the horizon. </p>
     *
     * @return  midnight function applicable on any calendar date
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Berechnet den Moment der niedrigsten Position der Sonne an der Position dieser Instanz. </p>
     *
     * <p>Hinweis: Die Transit-Zeit besagt nicht, ob die Sonne &uuml;ber oder unter dem Horizont ist. </p>
     *
     * @return  midnight function applicable on any calendar date
     * @since   3.34/4.29
     */
    public ChronoFunction<CalendarDate, Moment> transitAtMidnight() {

        return new ChronoFunction<CalendarDate, Moment>() {
            @Override
            public Moment apply(CalendarDate date) {
                return transitAtMidnight(date, longitude, calculator);
            }
        };

    }

    /**
     * <p>Calculates the local time of midnight at the location of this instance in given timezone. </p>
     *
     * <p>Note: The transit time does not tell if the sun is above or below the horizon. </p>
     *
     * @param   tzid    the identifier of the timezone the local time of the result refers to
     * @return  midnight function applicable on any calendar date
     * @see     #transitAtMidnight()
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Berechnet die lokale Uhrzeit von Mitternacht an der Position dieser Instanz
     * in der angegebenen Zeitzone. </p>
     *
     * <p>Hinweis: Die Transit-Zeit besagt nicht, ob die Sonne &uuml;ber oder unter dem Horizont ist. </p>
     *
     * @param   tzid    the identifier of the timezone the local time of the result refers to
     * @return  midnight function applicable on any calendar date
     * @see     #transitAtMidnight()
     * @since   3.34/4.29
     */
    public ChronoFunction<CalendarDate, PlainTime> transitAtMidnight(final TZID tzid) {

        return new ChronoFunction<CalendarDate, PlainTime>() {
            @Override
            public PlainTime apply(CalendarDate date) {
                Moment m = transitAtMidnight(date, SolarTime.this.longitude, SolarTime.this.calculator);
                return m.toZonalTimestamp(tzid).getWallTime();
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
                this.calculator.equals(that.calculator)
                    && (Double.compare(this.latitude, that.latitude) == 0)
                    && (Double.compare(this.longitude, that.longitude) == 0)
                    && (this.altitude == that.altitude));
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return (
            this.calculator.hashCode()
                + 7 * Double.valueOf(this.latitude).hashCode()
                + 31 * Double.valueOf(this.longitude).hashCode()
                + 37 * this.altitude
        );

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("SolarTime[latitude=");
        sb.append(this.latitude);
        sb.append(",longitude=");
        sb.append(this.longitude);
        if (this.altitude != 0) {
            sb.append(",altitude=");
            sb.append(this.altitude);
        }
        if (!this.calculator.equals(DEFAULT_CALCULATOR.name())) {
            sb.append(",calculator=");
            sb.append(this.calculator);
        }
        sb.append(']');
        return sb.toString();

    }

    /**
     * <p>Determines the apparent solar time of any moment at given local time zone offset. </p>
     *
     * <p>Based on the astronomical equation of time. The default calculator is usually
     * {@link Calculator#NOAA} unless another calculator was set up via the service loader mechnism. </p>
     *
     * @param   offset      the time zone offset which might depend on the geographical longitude
     * @return  function for getting the apparent solar time
     * @see     ZonalOffset#atLongitude(OffsetSign, int, int, double)
     * @see     #equationOfTime(Moment)
     */
    /*[deutsch]
     * <p>Ermittelt die wahre Ortszeit zur angegebenen lokalen Zeitzonendifferenz. </p>
     *
     * <p>Basiert auf der astronomischen Zeitgleichung. Die Standardberechnungsmethode ist
     * gew&ouml;hnlich {@link Calculator#NOAA}, es sei denn, eine andere Methode wurde
     * &uuml;ber den {@code ServiceLoader}-Mechanismus geladen. </p>
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
     * <p>Determines the apparent solar time of any moment at given local time zone offset. </p>
     *
     * <p>Based on the astronomical equation of time. </p>
     *
     * @param   offset      the time zone offset which might depend on the geographical longitude
     * @param   calculator  name of solar time calculator
     * @return  function for getting the apparent solar time
     * @see     ZonalOffset#atLongitude(OffsetSign, int, int, double)
     * @see     #equationOfTime(Moment, String)
     * @see     Calculator#NOAA
     * @see     Calculator#SIMPLE
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Ermittelt die wahre Ortszeit zur angegebenen lokalen Zeitzonendifferenz. </p>
     *
     * <p>Basiert auf der astronomischen Zeitgleichung. </p>
     *
     * @param   offset      the time zone offset which might depend on the geographical longitude
     * @param   calculator  name of solar time calculator
     * @return  function for getting the apparent solar time
     * @see     ZonalOffset#atLongitude(OffsetSign, int, int, double)
     * @see     #equationOfTime(Moment, String)
     * @see     Calculator#NOAA
     * @see     Calculator#SIMPLE
     * @since   3.34/4.29
     */
    public static ChronoFunction<Moment, PlainTimestamp> apparentAt(
        final ZonalOffset offset,
        final String calculator
    ) {

        return new ChronoFunction<Moment, PlainTimestamp>() {
            @Override
            public PlainTimestamp apply(Moment context) {
                PlainTimestamp meanSolarTime = onAverage(context, offset);
                double eot = equationOfTime(context, calculator);
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
     * <p>See also <a href="https://en.wikipedia.org/wiki/Equation_of_time">Wikipedia</a>.
     * Relation: mean-solar-time + equation-of-time = apparent-solar-time</p>
     *
     * <p>The default calculator is usually {@link Calculator#NOAA} unless another calculator was
     * set up via the service loader mechnism. </p>
     *
     * @param   moment  the moment when to determine the equation of time
     * @return  difference between apparent solar time and mean solar time in seconds
     * @throws  IllegalArgumentException if the moment is out of year range -2000/+3000
     */
    /*[deutsch]
     * <p>Bestimmt die Differenz zwischen wahrer und mittlerer Sonnenzeit zum angegebenen Zeitpunkt. </p>
     *
     * <p>Siehe auch <a href="https://de.wikipedia.org/wiki/Zeitgleichung">Wikipedia</a>.
     * Relation: mittlere Sonnenzeit + Zeitgleichung = wahre Sonnenzeit</p>
     *
     * <p>Die Standardberechnungsmethode ist gew&ouml;hnlich {@link Calculator#NOAA}, es sei denn,
     * eine andere Methode wurde &uuml;ber den {@code ServiceLoader}-Mechanismus geladen. </p>
     *
     * @param   moment  the moment when to determine the equation of time
     * @return  difference between apparent solar time and mean solar time in seconds
     * @throws  IllegalArgumentException if the moment is out of year range -2000/+3000
     */
    public static double equationOfTime(Moment moment) {

        double jde = JulianDay.getValue(moment, TimeScale.TT);
        return DEFAULT_CALCULATOR.equationOfTime(jde);

    }

    /**
     * <p>Determines the difference between apparent and mean solar time at given moment. </p>
     *
     * <p>See also <a href="https://en.wikipedia.org/wiki/Equation_of_time">Wikipedia</a>. </p>
     *
     * <p>Relation: mean-solar-time + equation-of-time = apparent-solar-time</p>
     *
     * @param   moment      the moment when to determine the equation of time
     * @param   calculator  name of solar time calculator
     * @return  difference between apparent solar time and mean solar time in seconds
     * @throws  IllegalArgumentException if the moment is out of year range -2000/+3000 or if the calculator is unknown
     * @see     Calculator#NOAA
     * @see     Calculator#SIMPLE
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Bestimmt die Differenz zwischen wahrer und mittlerer Sonnenzeit zum angegebenen Zeitpunkt. </p>
     *
     * <p>Siehe auch <a href="https://de.wikipedia.org/wiki/Zeitgleichung">Wikipedia</a>. </p>
     *
     * <p>Relation: mittlere Sonnenzeit + Zeitgleichung = wahre Sonnenzeit</p>
     *
     * @param   moment      the moment when to determine the equation of time
     * @param   calculator  name of solar time calculator
     * @return  difference between apparent solar time and mean solar time in seconds
     * @throws  IllegalArgumentException if the moment is out of year range -2000/+3000 or if the calculator is unknown
     * @see     Calculator#NOAA
     * @see     Calculator#SIMPLE
     * @since   3.34/4.29
     */
    public static double equationOfTime(
        Moment moment,
        String calculator
    ) {

        if (calculator == null) {
            throw new NullPointerException("Missing calculator parameter.");
        } else if (CALCULATORS.containsKey(calculator)) {
            double jde = JulianDay.getValue(moment, TimeScale.TT);
            return CALCULATORS.get(calculator).equationOfTime(jde);
        } else {
            throw new IllegalArgumentException("Unknown calculator: " + calculator);
        }

    }

    // used in test classes
    double getHighestElevationOfSun(PlainDate date) {

        Moment noon = date.get(this.transitAtNoon());
        double jde = JulianDay.getValue(noon, TimeScale.TT);
        double decInRad = Math.toRadians(this.getCalculator().declination(jde));
        double latInRad = Math.toRadians(this.latitude);
        double sinElevation = // Extra term left out => Math.cos(Math.toRadians(trueNoon)) := 1.0 (per definition)
            Math.sin(latInRad) * Math.sin(decInRad) + Math.cos(latInRad) * Math.cos(decInRad); // Meeus (13.6)
        return Math.toDegrees(Math.asin(sinElevation));

    }

    private static PlainTimestamp onAverage(Moment context, ZonalOffset offset) {

        Moment ut =
            Moment.of(
                context.getElapsedTime(TimeScale.UT) + 2 * 365 * 86400,
                context.getNanosecond(TimeScale.UT),
                TimeScale.POSIX);
        return ut.toZonalTimestamp(offset);

    }

    private static Moment transitAtNoon(
        CalendarDate date,
        double longitude,
        String calculator
    ) {

        Moment utc = fromLocalEvent(date, 12, longitude, calculator);
        return utc.with(Moment.PRECISION, calculator.equals(Calculator.SIMPLE) ? TimeUnit.MINUTES : TimeUnit.SECONDS);

    }

    private static Moment transitAtMidnight(
        CalendarDate date,
        double longitude,
        String calculator
    ) {

        Moment utc = fromLocalEvent(date, 0, longitude, calculator);
        return utc.with(Moment.PRECISION, calculator.equals(Calculator.SIMPLE) ? TimeUnit.MINUTES : TimeUnit.SECONDS);

    }

    private static Moment fromLocalEvent(
        CalendarDate date,
        int hourOfEvent,
        double longitude,
        String calculator
    ) {

        // numerical approximation of equation-of-time in two steps
        Calculator c = CALCULATORS.get(calculator);
        double elapsed = date.getDaysSinceEpochUTC() * 86400 + hourOfEvent * 3600 - longitude * 240;
        long secs = (long) Math.floor(elapsed);
        int nanos = (int) ((elapsed - secs) * 1000000000);
        Moment m1 = Moment.of(secs, nanos, TimeScale.UT);
        double eot = c.equationOfTime(JulianDay.getValue(m1, TimeScale.TT)); // first step

        secs = (long) Math.floor(eot);
        nanos = (int) ((eot - secs) * 1000000000);
        Moment m2 = m1.minus(secs, TimeUnit.SECONDS).minus(nanos, TimeUnit.NANOSECONDS);
        eot = c.equationOfTime(JulianDay.getValue(m2, TimeScale.TT)); // second step

        secs = (long) Math.floor(eot);
        nanos = (int) ((eot - secs) * 1000000000);
        return m1.minus(secs, TimeUnit.SECONDS).minus(nanos, TimeUnit.NANOSECONDS);

    }

    private static PlainDate toGregorian(CalendarDate date) {

        if (date instanceof PlainDate) {
            return (PlainDate) date;
        } else {
            return PlainDate.of(date.getDaysSinceEpochUTC(), EpochDays.UTC);
        }

    }

    private double earthRadius() {

        // curvature radius of earth rotation ellipsoid in the prime vertical (east-west), see also:
        // https://en.wikipedia.org/wiki/Earth_radius#Radii_of_curvature
        double lat = Math.toRadians(this.latitude);
        double r1 = EQUATORIAL_RADIUS * Math.cos(lat);
        double r2 = POLAR_RADIUS * Math.sin(lat);
        return EQUATORIAL_RADIUS * EQUATORIAL_RADIUS / Math.sqrt(r1 * r1 + r2 * r2);

    }

    private double sunAngleOfAltitude() {

        if (this.altitude == 0) {
            return 0.0;
        }

        double r = this.earthRadius();
        return Math.toDegrees(Math.acos(r / (r + this.altitude)));

    }

    private double zenithAngle() {

        return STD_ZENITH + this.sunAngleOfAltitude();

    }

    private static void check(
        double latitude,
        double longitude,
        double elevation,
        String calculator
    ) {

        if ((Double.compare(latitude, 90.0) > 0) || (Double.compare(latitude, -90.0) < 0)) {
            throw new IllegalArgumentException("Degrees out of range -90.0 <= latitude <= +90.0: " + latitude);
        } else if ((Double.compare(longitude, 180.0) >= 0) || (Double.compare(longitude, -180.0) < 0)) {
            throw new IllegalArgumentException("Degrees out of range -180.0 <= longitude < +180.0: " + longitude);
        } else if ((elevation < -1000) || (elevation > 9999)) {
            throw new IllegalArgumentException("Meters out of range -1000 <= elevation < +10,000: " + elevation);
        } else if (calculator == null) {
            throw new NullPointerException("Missing calculator.");
        } else if (!CALCULATORS.containsKey(calculator)) {
            throw new IllegalArgumentException("Unknown calculator: " + calculator);
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

        check(this.latitude, this.longitude, this.altitude, this.calculator);

    }

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>An SPI-interface representing a facade for the calculation engine regarding sunrise or sunset. </p>
     *
     * <p><strong>Implementation note: </strong> All implementations must have a public no-arg constructor. </p>
     *
     * @see     java.util.ServiceLoader
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Ein SPI-Interface, das eine Fassade f&uuml;r die Berechnung von Sonnenaufgang oder Sonnenuntergang
     * darstellt. </p>
     *
     * <p><strong>Implementierungshinweis: </strong> Alle Implementierungen m&uuml;ssen einen
     * &ouml;ffentlichen Konstruktor ohne Argumente haben. </p>
     *
     * @see     java.util.ServiceLoader
     * @since   3.34/4.29
     */
    public interface Calculator {

        //~ Statische Felder/Initialisierungen ----------------------------

        /**
         * Follows closely the algorithms published by NOAA (National Oceanic and Atmospheric Administration).
         *
         * <p>The <a href="https://www.esrl.noaa.gov/gmd/grad/solcalc/">website</a> of NOAA also links
         * to the calculation details. This is the default calculator with reasonably good precision.
         * However, Time4J also applies a delta-T-correction while original NOAA does not do this adjustment. </p>
         *
         * <p>Although the precision is theoretically often better than one minute (for non-polar regions,
         * beyond +/-72 degrees latitude rather in range of ten minutes), users should consider the fact
         * that local topology or the actual weather conditions are not taken into account. Therefore
         * truncating the results to minute precision should be considered. Example: </p>
         *
         * <pre>
         *     PlainDate date = PlainDate.of(2009, 9, 6);
         *     SolarTime atlanta = SolarTime.ofLocation(33.766667, -84.416667, 0, SolarTime.Calculator.NOAA);
         *     TZID tzid = () -&gt; &quot;America/New_York&quot;;
         *     assertThat(
         *       date.get(atlanta.sunrise())
         *         .get()
         *         .toZonalTimestamp(tzid)
         *         .with(PlainTime.PRECISION, ClockUnit.MINUTES),
         *       is(PlainTimestamp.of(2009, 9, 6, 7, 15)));
         * </pre>
         */
        /*[deutsch]
         * Folgt nahe den Algorithmen, die von der NOAA (National Oceanic and Atmospheric Administration)
         * ver&ouml;ffentlicht wurden.
         *
         * <p>Die <a href="https://www.esrl.noaa.gov/gmd/grad/solcalc/">Webseite</a> der NOAA verlinkt
         * auch zu den Berechnungsdetails. Dies ist die Standardberechnungsmethode mit recht guter Genauigkeit.
         * Allerdings wendet Time4J eine delta-T-Korrektur an, w&auml;hrend Original-NOAA diese Korrektur
         * nicht anwendet. </p>
         *
         * <p>Obwohl die Genauigkeit theoretisch oft besser als eine Minute ist (f&uuml;r nicht-polare Breiten,
         * jenseits von +/-72 Grad Breite jedoch eher im Bereich von 10 Minuten)), sollten Anwender auch die Tatsache
         * in Betracht ziehen, da&szlig; die lokale Topologie oder die aktuellen Wetterbedingungen nicht
         * ber&uuml;cksichtigt werden. Deshalb ist das Abschneiden der Sekundenteile in den Ergebnissen
         * meistens angeraten. Beispiel: </p>
         *
         * <pre>
         *     PlainDate date = PlainDate.of(2009, 9, 6);
         *     SolarTime atlanta = SolarTime.ofLocation(33.766667, -84.416667, 0, SolarTime.Calculator.NOAA);
         *     TZID tzid = () -&gt; &quot;America/New_York&quot;;
         *     assertThat(
         *       date.get(atlanta.sunrise())
         *         .get()
         *         .toZonalTimestamp(tzid)
         *         .with(PlainTime.PRECISION, ClockUnit.MINUTES),
         *       is(PlainTimestamp.of(2009, 9, 6, 7, 15)));
         * </pre>
         */
        String NOAA = "NOAA";

        /**
         * Simple and relatively fast but rather imprecise calculator.
         *
         * <p>This calculator was once published in &quot;Almanac for Computers, 1990 by Nautical Almanac Office
         * in United States Naval Observatory (USNO)&quot;. </p>
         *
         * <p>Ed Williams has used this book as the source for
         * <a href="http://www.edwilliams.org/sunrise_sunset_algorithm.htm">his algorithmic proposal</a>. Mike
         * Reedell has then used the proposal of Williams to realize his popular sunrise/sunset-library written
         * in Java. Leaving aside general precision requirements, this method cannot be recommended for the
         * polar regions. So the scope of this method is constrained to the latitudes in range
         * {@code -65.0 <= latitude <= +65.0} otherwise the results are expected to be unusable. </p>
         *
         * <p>However, if users only use this method for actual years and non-polar regions, then
         * the precision of sunrise or sunset events remain within two minutes (and the equation
         * of time within one minute). </p>
         */
        /*[deutsch]
         * Einfache und relativ schnelle aber eher ungenaue Berechnungsmethode.
         *
         * <p>Diese Berechnungsmethode wurde urspr&uuml;nglich im &quot;Almanac for Computers, 1990
         * vom Nautical Almanac Office in United States Naval Observatory (USNO)&quot;
         * ver&ouml;ffentlicht. </p>
         *
         * <p>Ed Williams hat dieses Buch als die Quelle
         * <a href="http://www.edwilliams.org/sunrise_sunset_algorithm.htm">seines algorithmischen Vorschlags</a>
         * verwendet. Mike Reedell hat schlie&szlig;lich den Vorschlag von Williams benutzt, um seine weit
         * verbreitete sunrise/sunset-library in der Programmiersprache Java zu realisieren. Auch wenn allgemeine
         * Genauigkeitsanforderungen beiseite gelassen werden, kann diese Methode nicht f&uuml;r die
         * polaren Breiten empfohlen werden. Somit ist diese Methode auf geographische Breiten im Bereich
         * {@code -65.0 <= latitude <= +65.0} beschr&auml;nkt, sonst sind die Ergebnisse unbrauchbar.  </p>
         *
         * <p>Allerdings verbleibt die Genauigkeit f&uuml;r Sonnenauf- oder Sonnenuntergang noch innerhalb
         * von zwei Minuten (und f&uuml;r die Zeitgleichung innerhalb einer Minute), wenn Anwender diese
         * Methode nur f&uuml;r aktuelle Jahre und die nicht-polaren Breiten benutzen. </p>
         */
        String SIMPLE = "SIMPLE";

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
         * @return  moment of sunrise if it exists for given parameters else {@code null} (polar day or night)
         * @throws  IllegalArgumentException if any parameter is out of range
         */
        /*[deutsch]
         * <p>Berechnet den Zeitpunkt des Sonnenaufgangs. </p>
         *
         * @param   date        the local calendar date
         * @param   latitude    geographical latitude in degrees, positive for North, negative for South
         * @param   longitude   geographical longitude in degrees, positive for East, negative for West
         * @param   zenith      the distance of the center of the sun from geographical local zenith in degrees
         * @return  moment of sunrise if it exists for given parameters else {@code null} (polar day or night)
         * @throws  IllegalArgumentException if any parameter is out of range
         */
        Moment sunrise(
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
         * @return  moment of sunset if it exists for given parameters else {@code null} (polar day or night)
         * @throws  IllegalArgumentException if any parameter is out of range
         */
        /*[deutsch]
         * <p>Berechnet den Zeitpunkt des Sonnenuntergangs. </p>
         *
         * @param   date        the local calendar date
         * @param   latitude    geographical latitude in degrees, positive for North, negative for South
         * @param   longitude   geographical longitude in degrees, positive for East, negative for West
         * @param   zenith      the distance of the center of the sun from geographical local zenith in degrees
         * @return  moment of sunset if it exists for given parameters else {@code null} (polar day or night)
         * @throws  IllegalArgumentException if any parameter is out of range
         */
        Moment sunset(
            CalendarDate date,
            double latitude,
            double longitude,
            double zenith
        );

        /**
         * <p>Calculates the difference between true and mean solar time. </p>
         *
         * @param   jde     julian day in ephemeris time
         * @return  value in seconds
         */
        /*[deutsch]
         * <p>Berechnet die Differenz zwischen wahrer und mittlerer Ortszeit. </p>
         *
         * @param   jde     julian day in ephemeris time
         * @return  value in seconds
         */
        double equationOfTime(double jde);

        /**
         * <p>Determines the declination of sun. </p>
         *
         * @param   jde     julian day in ephemeris time
         * @return  declination of sun in degrees
         */
        /*[deutsch]
         * <p>Bestimmt die Deklination der Sonne. </p>
         *
         * @param   jde     julian day in ephemeris time
         * @return  declination of sun in degrees
         */
        double declination(double jde);

    }

    /**
     * <p>Collects various data around sunrise and sunset. </p>
     *
     * @author  Meno Hochschild
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Sammelt verschiedene Daten um Sonnenauf- oder Sonnenuntergang herum. </p>
     *
     * @author  Meno Hochschild
     * @since   3.34/4.29
     */
    public static class Sunshine {

        //~ Instanzvariablen ----------------------------------------------

        private final Moment startUTC;
        private final Moment endUTC;
        private final PlainTimestamp startLocal;
        private final PlainTimestamp endLocal;

        //~ Konstruktoren -------------------------------------------------

        private Sunshine(
            PlainDate date,
            Moment start,
            Moment end,
            TZID tzid,
            boolean absent
        ) {
            super();

            if (absent) { // polar night
                this.startUTC = null;
                this.endUTC = null;
                this.startLocal = null;
                this.endLocal = null;
            } else if (start != null) {
                this.startUTC = start;
                this.startLocal = this.startUTC.toZonalTimestamp(tzid);
                if (end != null) { // standard use-case
                    this.endUTC = end;
                    this.endLocal = this.endUTC.toZonalTimestamp(tzid);
                } else {
                    PlainDate next = date.plus(1, CalendarUnit.DAYS);
                    this.endUTC = next.atFirstMoment(tzid);
                    this.endLocal = next.atStartOfDay(tzid);
                }
            } else if (end != null) {
                this.startUTC = date.atFirstMoment(tzid);
                this.startLocal = date.atStartOfDay(tzid);
                this.endUTC = end;
                this.endLocal = this.endUTC.toZonalTimestamp(tzid);
            } else { // midnight sun
                this.startUTC = date.atFirstMoment(tzid);
                this.startLocal = date.atStartOfDay(tzid);
                PlainDate next = date.plus(1, CalendarUnit.DAYS);
                this.endUTC = next.atFirstMoment(tzid);
                this.endLocal = next.atStartOfDay(tzid);
            }

        }

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Obtains the moment of sunrise if it exists. </p>
         *
         * @return  moment of sunrise
         * @throws  IllegalStateException in case of absent sunshine (polar night)
         * @see     #isAbsent()
         */
        /*[deutsch]
         * <p>Liefert den Moment des Sonnenaufgangs wenn vorhanden. </p>
         *
         * @return  moment of sunrise
         * @throws  IllegalStateException in case of absent sunshine (polar night)
         * @see     #isAbsent()
         */
        public Moment startUTC() {

            return checkAndGet(this.startUTC);

        }

        /**
         * <p>Obtains the moment of sunset if it exists. </p>
         *
         * @return  moment of sunset (exclusive)
         * @throws  IllegalStateException in case of absent sunshine (polar night)
         * @see     #isAbsent()
         */
        /*[deutsch]
         * <p>Liefert den Moment des Sonnenuntergangs wenn vorhanden. </p>
         *
         * @return  moment of sunset (exclusive)
         * @throws  IllegalStateException in case of absent sunshine (polar night)
         * @see     #isAbsent()
         */
        public Moment endUTC() {

            return checkAndGet(this.endUTC);

        }

        /**
         * <p>Obtains the local timestamp of sunrise if it exists. </p>
         *
         * @return  local timestamp of sunrise
         * @throws  IllegalStateException in case of absent sunshine (polar night)
         * @see     #isAbsent()
         */
        /*[deutsch]
         * <p>Liefert die lokale Zeit des Sonnenaufgangs wenn vorhanden. </p>
         *
         * @return  local timestamp of sunrise
         * @throws  IllegalStateException in case of absent sunshine (polar night)
         * @see     #isAbsent()
         */
        public PlainTimestamp startLocal() {

            return checkAndGet(this.startLocal);

        }

        /**
         * <p>Obtains the local timestamp of sunset if it exists. </p>
         *
         * @return  local timestamp of sunset (exclusive)
         * @throws  IllegalStateException in case of absent sunshine (polar night)
         * @see     #isAbsent()
         */
        /*[deutsch]
         * <p>Liefert die lokale Zeit des Sonnenuntergangs wenn vorhanden. </p>
         *
         * @return  local timestamp of sunset (exclusive)
         * @throws  IllegalStateException in case of absent sunshine (polar night)
         * @see     #isAbsent()
         */
        public PlainTimestamp endLocal() {

            return checkAndGet(this.endLocal);

        }

        /**
         * <p>Is there any sunshine at given moment? </p>
         *
         * @param   moment  the instant to be queried
         * @return  boolean
         */
        /*[deutsch]
         * <p>Scheint zum angegebenen Moment die Sonne? </p>
         *
         * @param   moment  the instant to be queried
         * @return  boolean
         */
        public boolean isPresent(Moment moment) {

            if (this.isAbsent()) {
                return false;
            }

            return (!this.startUTC.isAfter(moment) && moment.isBefore(this.endUTC));

        }

        /**
         * <p>Is there any sunshine at given local timestamp? </p>
         *
         * @param   tsp     the local timestamp to be queried
         * @return  boolean
         */
        /*[deutsch]
         * <p>Scheint zur angegebenen lokalen Zeit die Sonne? </p>
         *
         * @param   tsp     the local timestamp to be queried
         * @return  boolean
         */
        public boolean isPresent(PlainTimestamp tsp) {

            if (this.isAbsent()) {
                return false;
            }

            return (!this.startLocal.isAfter(tsp) && tsp.isBefore(this.endLocal));

        }

        /**
         * <p>Checks if any sunshine is unavailable (polar night). </p>
         *
         * @return  {@code true} if this instance corresponds to a polar night else {@code false}
         */
        /*[deutsch]
         * <p>Pr&uuml;ft, ob gar kein Sonnenschein vorhanden ist (Polarnacht). </p>
         *
         * @return  {@code true} if this instance corresponds to a polar night else {@code false}
         */
        public boolean isAbsent() {

            return (this.startUTC == null); // sufficient, see constructor

        }

        /**
         * <p>Obtains the length of sunshine in seconds. </p>
         *
         * @return  physical length of sunshine in seconds (without leap seconds)
         * @see     TimeUnit#SECONDS
         */
        /*[deutsch]
         * <p>Liefert die Sonnenscheindauer in Sekunden. </p>
         *
         * @return  physical length of sunshine in seconds (without leap seconds)
         * @see     TimeUnit#SECONDS
         */
        public int length() {

            if (this.isAbsent()) {
                return 0;
            }

            return (int) this.startUTC.until(this.endUTC, TimeUnit.SECONDS); // safe cast

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

            if (this.isAbsent()) {
                return "Polar night";
            }

            StringBuilder sb = new StringBuilder(128);
            sb.append("Sunshine[");
            sb.append("utc=");
            sb.append(this.startUTC);
            sb.append('/');
            sb.append(this.endUTC);
            sb.append(",local=");
            sb.append(this.startLocal);
            sb.append('/');
            sb.append(this.endLocal);
            sb.append(",length=");
            sb.append(this.length());
            sb.append(']');
            return sb.toString();

        }

        private static <T> T checkAndGet(T value) {

            if (value == null) {
                throw new IllegalStateException("Sunshine is absent (polar night).");
            } else {
                return value;
            }

        }

    }

    private static enum StdCalculator
        implements Calculator {

        //~ Statische Felder/Initialisierungen ----------------------------

        /*
            URL:
              http://www.edwilliams.org/sunrise_sunset_algorithm.htm
              https://babel.hathitrust.org/cgi/pt?id=uiug.30112059294311;view=1up;seq=25

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
                NOTE: T potentially needs to be adjusted into the range [0,24) by adding/subtracting 24

            10. convert UT value to local time zone of latitude/longitude

                localT = UT + localOffset
        */
        SIMPLE {
            @Override
            public Moment sunrise(CalendarDate date, double latitude, double longitude, double zenith) {
                return event(date, latitude, longitude, zenith, true);
            }
            @Override
            public Moment sunset(CalendarDate date, double latitude, double longitude, double zenith) {
                return event(date, latitude, longitude, zenith, false);
            }
            @Override
            public double equationOfTime(double jde) {
                // => page B8, formula 1 (precision about 0.8 minutes)
                double t = time0(jde);
                return (
                    -7.66 * Math.sin(Math.toRadians(0.9856 * t - 3.8))
                    - 9.78 * Math.sin(Math.toRadians(1.9712 * t + 17.96))
                ) * 60;
            }
            @Override
            public double declination(double jde) {
                double t0 = time0(jde);
                double L = trueLongitudeOfSunInDegrees(t0);
                double sinDec = 0.39782 * Math.sin(Math.toRadians(L));
                return Math.toDegrees(Math.asin(sinDec));
            }
            private double time0(double jde) {
                PlainTimestamp tsp = JulianDay.ofEphemerisTime(jde).toMoment().toZonalTimestamp(ZonalOffset.UTC);
                return tsp.getCalendarDate().getDayOfYear() + tsp.getWallTime().get(PlainTime.SECOND_OF_DAY) / 86400.0;
            }
            private double trueLongitudeOfSunInDegrees(double t0) {
                double M = // mean anomaly of sun in degrees
                    (0.9856 * t0) - 3.289;
                double L =
                    M + (1.916 * Math.sin(Math.toRadians(M))) + (0.020 * Math.sin(2 * Math.toRadians(M))) + 282.634;
                return adjustRange(L);
            }
            private Moment event(
                CalendarDate date,
                double latitude,
                double longitude,
                double zenith,
                boolean sunrise
            ) {
                // => page B5/B6/B7
                PlainDate d = toGregorian(date);
                int doy = d.getDayOfYear();
                double lngHour = longitude / 15;
                double t0 = doy + (((sunrise ? 6 : 18) - lngHour) / 24);
                double L = trueLongitudeOfSunInDegrees(t0);
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
                if ((Double.compare(cosH, 1.0) > 0) || (Double.compare(cosH, -1.0) < 0)) {
                    // the sun never rises or sets on this location (on the specified date)
                    return null;
                }
                double H = Math.toDegrees(Math.acos(cosH));
                if (sunrise) {
                    H = 360 - H;
                }
                H = H / 15;
                double lmt = H + RA - (0.06571 * t0) - 6.622;
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
                return utc.with(Moment.PRECISION, TimeUnit.MINUTES);
            }
            private double adjustRange(double value) { // range [0.0, 360.0)
                while (Double.compare(0.0, value) > 0) {
                    value += 360;
                }
                while (Double.compare(value, 360.0) >= 0) {
                    value -= 360;
                }
                return value;
            }
        },

        NOAA() {
            @Override
            public Moment sunrise(CalendarDate date, double latitude, double longitude, double zenith) {
                return this.event(true, date, latitude, longitude, zenith);
            }
            @Override
            public Moment sunset(CalendarDate date, double latitude, double longitude, double zenith) {
                return this.event(false, date, latitude, longitude, zenith);
            }
            // Meeus p.185 (lower accuracy model), returns units of second
            // other source: http://adsabs.harvard.edu/full/1989MNRAS.238.1529H
            @Override
            public double equationOfTime(double jde) {
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
            @Override
            public double declination(double jde) {
                double jct = (jde - 2451545.0) / 36525;
                return Math.toDegrees(declinationRad(jct));
            }
            private Moment event(
                boolean rise,
                CalendarDate date,
                double latitude,
                double longitude,
                double zenith
            ) {
                Moment m = fromLocalEvent(date, 12, longitude, this.name()); // noon
                double jde = JulianDay.getValue(m, TimeScale.TT);
                double H = localHourAngle(rise, jde, latitude, zenith);
                if (Double.isNaN(H)) {
                    return null;
                } else {
                    H = localHourAngle(rise, jde + H / 86400, latitude, zenith); // corrected for local time of day
                    if (Double.isNaN(H)) {
                        return null;
                    } else {
                        long secs = (long) Math.floor(H);
                        int nanos = (int) ((H - secs) * 1000000000);
                        Moment utc = m.plus(secs, TimeUnit.SECONDS).plus(nanos, TimeUnit.NANOSECONDS);
                        return utc.with(Moment.PRECISION, TimeUnit.SECONDS);
                    }
                }
            }
            private double localHourAngle(boolean rise, double jde, double latitude, double zenith) {
                double jct = (jde - 2451545.0) / 36525; // julian centuries (J2000)
                double H = localHourAngle(jct, latitude, zenith);
                if (Double.isNaN(H)) {
                    return Double.NaN;
                } else {
                    if (rise) {
                        H = -H;
                    }
                    return H;
                }
            }
            // Meeus (22.2), in degrees
            private double obliquity(double jct) {
                double obliquity =
                    23.0 + 26.0 / 60 + (21.448 + (-46.815 + (-0.00059 + 0.001813 * jct) * jct) * jct) / 3600;
                double corr = 0.00256 * Math.cos(Math.toRadians(125.04 - 1934.136 * jct)); // Meeus (25.8)
                return obliquity + corr;
            }
            // Meeus (25.2), in degrees
            private double meanLongitude(double jct) {
                return (280.46646 + (36000.76983 + 0.0003032 * jct) * jct) % 360;
            }
            // Meeus (25.3), in degrees
            private double meanAnomaly(double jct) {
                return 357.52911 + (35999.05029 - 0.0001537 * jct) * jct;
            }
            // Meeus (25.4), unit-less
            private double excentricity(double jct) {
                return 0.016708634 - (0.000042037 + 0.0000001267 * jct) * jct;
            }
            // W2-term in NOAA-Excel-sheet
            private double localHourAngle(
                double jct,
                double latitude,
                double zenith
            ) {
                double latInRad = Math.toRadians(latitude);
                double decInRad = declinationRad(jct);
                double cosH =
                    (Math.cos(Math.toRadians(zenith)) - (Math.sin(decInRad) * Math.sin(latInRad)))
                        / (Math.cos(decInRad) * Math.cos(latInRad));
                if ((Double.compare(cosH, 1.0) > 0) || (Double.compare(cosH, -1.0) < 0)) {
                    // the sun never rises or sets on this location (on the specified date)
                    return Double.NaN;
                }
                return Math.toDegrees(Math.acos(cosH)) * 240; // in decimal seconds
            }
            // T2-term in NOAA-Excel-sheet (in radians)
            private double declinationRad(double jct) {
                return Math.asin(
                    Math.sin(Math.toRadians(obliquity(jct))) * Math.sin(Math.toRadians(apparentLongitude(jct))));
            }
            // P2-term in NOAA-Excel-sheet
            private double apparentLongitude(double jct) {
                return meanLongitude(jct)
                    + equationOfCenter(jct)
                    - 0.00569
                    - 0.00478 * Math.sin(Math.toRadians(125.04 - 1934.136 * jct));
            }
            // L2-term in NOAA-Excel-sheet
            private double equationOfCenter(double jct) {
                double j2 = Math.toRadians(meanAnomaly(jct));
                return (
                    Math.sin(j2) * (1.914602 - (0.004817 + 0.000014 * jct) * jct)
                    + Math.sin(2 * j2) * (0.019993 - 0.000101 * jct)
                    + Math.sin(3 * j2) * 0.000289
                );
            }
        }

    }

}
