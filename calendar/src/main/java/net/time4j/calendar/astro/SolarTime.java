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
import net.time4j.engine.ChronoFunction;
import net.time4j.engine.EpochDays;
import net.time4j.scale.TimeScale;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.TZID;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;


/**
 * Contains various routines to determine solar time.
 *
 * <p>Notice: Most chronological functions use the astronomical equation of time. Hence they are only applicable
 * for years between -2000 and +3000 otherwise an {@code IllegalArgumentException} will be thrown. </p>
 *
 * <p>Example for sunrise and sunset on the top of Africas highest mountain Kilimanjaro: </p>
 *
 * <pre>
 *     PlainDate date = PlainDate.of(2017, 12, 22);
 *     TZID tzid = Timezone.of(&quot;Africa/Dar_es_Salaam&quot;).getID(); // Tanzania: UTC+03:00
 *
 *     // high altitude implies earlier sunrise and later sunset
 *     SolarTime kibo5895 =
 *       SolarTime.ofLocation().southernLatitude(3, 4, 0).easternLongitude(37, 21, 33).atAltitude(5895).build();
 *
 *     assertThat(
 *       date.get(kibo5895.sunrise(tzid)).get(),
 *       is(PlainTime.of(6, 10, 34)));
 *     assertThat(
 *       date.get(kibo5895.sunset(tzid)).get(),
 *       is(PlainTime.of(18, 47, 48)));
 * </pre>
 *
 * <p><strong>About limitations of accuracy:</strong></p>
 *
 * <p>Time4J only models a spherical geometry but not local topology with mountains which can break the
 * horizon line. Furthermore, special weather conditions which can have a bigger impact on atmospheric
 * refraction are not calculatable. The concrete calculator in use is also a limiting factor for accuracy.
 * Users should therefore not expect more than minute precision. This can be even worse for polar regions
 * because the sun crosses then the horizon under a very shallow angle. </p>
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
 * <p>Beispielrechnung f&uuml;r den Sonnenaufgang und -untergang auf Afrikas h&ouml;chstem Berg Kilimanjaro: </p>
 *
 * <pre>
 *     PlainDate date = PlainDate.of(2017, 12, 22);
 *     TZID tzid = Timezone.of(&quot;Africa/Dar_es_Salaam&quot;).getID(); // Tanzania: UTC+03:00
 *
 *     // gro&szlig;e H&ouml;he verursacht fr&uuml;heren Sonnenaufgang und sp&auml;teren Sonnenuntergang
 *     SolarTime kibo5895 =
 *       SolarTime.ofLocation().southernLatitude(3, 4, 0).easternLongitude(37, 21, 33).atAltitude(5895).build();
 *
 *     assertThat(
 *       date.get(kibo5895.sunrise(tzid)).get(),
 *       is(PlainTime.of(6, 10, 34)));
 *     assertThat(
 *       date.get(kibo5895.sunset(tzid)).get(),
 *       is(PlainTime.of(18, 47, 48)));
 * </pre>
 *
 * <p><strong>&Uuml;ber die Grenzen der Genauigkeit:</strong></p>
 *
 * <p>Time4J modelliert nur eine sp&auml;rische Geometrie, aber nicht eine lokale Topologie mit Bergen,
 * die die Horizontlinie unterbrechen k&ouml;nnen. Au&szlig;erdem sind besondere Wetterbedingungen mit
 * ihrem Einflu&szlig; auf die atmosph&auml;rische Lichtbeugung nicht berechenbar. Das konkret verwendete
 * Berechnungsverfahren ist ebenfalls ein begrenzender Faktor f&uuml;r die erreichbare Genauigkeit.
 * Anwender sollten daher nicht mehr als Minutengenauigkeit erwarten. In Polargebieten ist sie sogar
 * schlechter, weil die Sonne dann nur unter einem sehr flachen Winkel den Horizont quert. </p>
 *
 * @author  Meno Hochschild
 * @since   3.33/4.28
 */
public final class SolarTime
    implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    static final double SUN_RADIUS = 16.0;
    static final double STD_REFRACTION = 34.0;
    static final double STD_ZENITH = 90.0 + (SUN_RADIUS + STD_REFRACTION) / 60.0;
    static final String DECLINATION = "declination";

    private static final Calculator DEFAULT_CALCULATOR;
    private static final ConcurrentMap<String, Calculator> CALCULATORS;

    static {
        Calculator loaded = null;
        ConcurrentMap<String, Calculator> calculators = new ConcurrentHashMap<String, Calculator>();
        for (Calculator calculator : ResourceLoader.getInstance().services(Calculator.class)) {
            loaded = calculator;
            calculators.put(calculator.name(), calculator);
        }
        for (Calculator calculator : StdSolarCalculator.values()) {
            calculators.put(calculator.name(), calculator);
        }
        CALCULATORS = calculators;
        DEFAULT_CALCULATOR = ((loaded == null) ? StdSolarCalculator.TIME4J : loaded);
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
     * <p>Obtains a builder for creating a new instance of local solar time. </p>
     *
     * <p>This method is the recommended approach if any given geographical position is described
     * in degrees including arc minutes and arc seconds in order to avoid manual conversions to
     * decimal degrees. </p>
     *
     * @return  builder for creating a new instance of local solar time
     * @since   3.35/4.30
     */
    /*[deutsch]
     * <p>Liefert einen {@code Builder} zur Erzeugung einer neuen Instanz einer lokalen Sonnenzeit. </p>
     *
     * <p>Diese Methode ist der empfohlene Ansatz, wenn irgendeine geographische Positionsangabe
     * in Grad mit Bogenminuten und Bogensekunden vorliegt, um manuelle Umrechnungen in Dezimalangaben
     * zu vermeiden. </p>
     *
     * @return  builder for creating a new instance of local solar time
     * @since   3.35/4.30
     */
    public static SolarTime.Builder ofLocation() {

        return new Builder();

    }

    /**
     * <p>Obtains the solar time for given geographical location at sea level. </p>
     *
     * <p>The default calculator is usually {@link Calculator#NOAA} unless another calculator was
     * set up via the service loader mechnism. </p>
     *
     * <p>This method handles the geographical location in decimal degrees only. If these data are given
     * in degrees, arc minutes and arc seconds then users should apply the {@link #ofLocation() builder}
     * approach instead. </p>
     *
     * @param   latitude    geographical latitude in decimal degrees ({@code -90.0 <= x <= +90.0})
     * @param   longitude   geographical longitude in decimal degrees ({@code -180.0 <= x < 180.0})
     * @return  instance of local solar time
     * @see     #ofLocation(double, double, int, String)
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Liefert die Sonnenzeit zur angegebenen geographischen Position auf Meeresh&ouml;he. </p>
     *
     * <p>Die Standardberechnungsmethode ist gew&ouml;hnlich {@link Calculator#NOAA}, es sei denn,
     * eine andere Methode wurde &uuml;ber den {@code ServiceLoader}-Mechanismus geladen. </p>
     *
     * <p>Diese Methode nimmt geographische Angaben nur in Dezimalgrad entgegen. Wenn diese Daten aber
     * in Grad, Bogenminuten und Bogensekunden vorliegen, sollten Anwender den {@link #ofLocation() Builder-Ansatz}
     * bevorzugen. </p>
     *
     * @param   latitude    geographical latitude in decimal degrees ({@code -90.0 <= x <= +90.0})
     * @param   longitude   geographical longitude in decimal degrees ({@code -180.0 <= x < 180.0})
     * @return  instance of local solar time
     * @see     #ofLocation(double, double, int, String)
     * @since   3.34/4.29
     */
    public static SolarTime ofLocation(
        double latitude,
        double longitude
    ) {

        return ofLocation(latitude, longitude, 0, DEFAULT_CALCULATOR);

    }

    /**
     * <p>Obtains the solar time for given geographical location. </p>
     *
     * <p>This method handles the geographical location in decimal degrees only. If these data are given
     * in degrees, arc minutes and arc seconds then users should apply the {@link #ofLocation() builder}
     * approach instead. </p>
     *
     * @param   latitude    geographical latitude in decimal degrees ({@code -90.0 <= x <= +90.0})
     * @param   longitude   geographical longitude in decimal degrees ({@code -180.0 <= x < 180.0})
     * @param   altitude    geographical altitude relative to sea level in meters ({@code 0 <= x < 11,0000})
     * @param   calculator  name of solar time calculator
     * @return  instance of local solar time
     * @see     Calculator#name()
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Liefert die Sonnenzeit zur angegebenen geographischen Position. </p>
     *
     * <p>Diese Methode nimmt geographische Angaben nur in Dezimalgrad entgegen. Wenn diese Daten aber
     * in Grad, Bogenminuten und Bogensekunden vorliegen, sollten Anwender den {@link #ofLocation() Builder-Ansatz}
     * bevorzugen. </p>
     *
     * @param   latitude    geographical latitude in decimal degrees ({@code -90.0 <= x <= +90.0})
     * @param   longitude   geographical longitude in decimal degrees ({@code -180.0 <= x < 180.0})
     * @param   altitude    geographical altitude relative to sea level in meters ({@code 0 <= x < 11,0000})
     * @param   calculator  name of solar time calculator
     * @return  instance of local solar time
     * @see     Calculator#name()
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
     * <p>Obtains the solar time for given geographical location. </p>
     *
     * <p>This method handles the geographical location in decimal degrees only. If these data are given
     * in degrees, arc minutes and arc seconds then users should apply the {@link #ofLocation() builder}
     * approach instead. </p>
     *
     * @param   latitude    geographical latitude in decimal degrees ({@code -90.0 <= x <= +90.0})
     * @param   longitude   geographical longitude in decimal degrees ({@code -180.0 <= x < 180.0})
     * @param   altitude    geographical altitude relative to sea level in meters ({@code 0 <= x < 11,0000})
     * @param   calculator  instance of solar time calculator
     * @return  instance of local solar time
     * @since   3.36/4.31
     */
    /*[deutsch]
     * <p>Liefert die Sonnenzeit zur angegebenen geographischen Position. </p>
     *
     * <p>Diese Methode nimmt geographische Angaben nur in Dezimalgrad entgegen. Wenn diese Daten aber
     * in Grad, Bogenminuten und Bogensekunden vorliegen, sollten Anwender den {@link #ofLocation() Builder-Ansatz}
     * bevorzugen. </p>
     *
     * @param   latitude    geographical latitude in decimal degrees ({@code -90.0 <= x <= +90.0})
     * @param   longitude   geographical longitude in decimal degrees ({@code -180.0 <= x < 180.0})
     * @param   altitude    geographical altitude relative to sea level in meters ({@code 0 <= x < 11,0000})
     * @param   calculator  instance of solar time calculator
     * @return  instance of local solar time
     * @since   3.36/4.31
     */
    public static SolarTime ofLocation(
        double latitude,
        double longitude,
        int altitude,
        Calculator calculator
    ) {

        String name = calculator.name();
        CALCULATORS.putIfAbsent(name, calculator);
        check(latitude, longitude, altitude, name);
        return new SolarTime(latitude, longitude, altitude, name);

    }

    /**
     * <p>Obtains the geographical latitude of this instance. </p>
     *
     * @return  latitude in decimal degrees
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Liefert den geographischen Breitengrad dieser Instanz. </p>
     *
     * @return  latitude in decimal degrees
     * @since   3.34/4.29
     */
    public double getLatitude() {

        return this.latitude;

    }

    /**
     * <p>Obtains the geographical longitude of this instance. </p>
     *
     * @return  longitude in decimal degrees
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Liefert den geographischen L&auml;ngengrad dieser Instanz. </p>
     *
     * @return  longitude in decimal degrees
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
    public ChronoFunction<CalendarDate, Moment> sunrise() {

        return new ChronoFunction<CalendarDate, Moment>() {
            @Override
            public Moment apply(CalendarDate date) {
                return SolarTime.this.getCalculator().sunrise(date, SolarTime.this.latitude, SolarTime.this.longitude, SolarTime.this.zenithAngle());
            }
        };

    }

    /**
     * <p>Calculates the time of given twilight at sunrise and the location of this instance. </p>
     *
     * <p>Note: The precision is generally constrained to minutes. And the atmospheric refraction
     * is here not taken into account. </p>
     *
     * @param   twilight    relevant definition of twilight
     * @return  twilight function at sunrise applicable on any calendar date
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Berechnet die Zeit der angegebenen D&auml;mmerung zum Sonnenaufgang an der Position dieser Instanz. </p>
     *
     * <p>Hinweis: Die Genauigkeit liegt generell im Minutenbereich. Die atmosp&auml;rische Lichtbeugung wird
     * hier nicht ber&uuml;cksichtigt. </p>
     *
     * @param   twilight    relevant definition of twilight
     * @return  twilight function at sunrise applicable on any calendar date
     * @since   3.34/4.29
     */
    public ChronoFunction<CalendarDate, Moment> sunrise(Twilight twilight) {

        final double effAngle = 90.0 + this.geodeticAngle() + twilight.getAngle();
        return new ChronoFunction<CalendarDate, Moment>() {
            @Override
            public Moment apply(CalendarDate date) {
                return SolarTime.this.getCalculator().sunrise(
                    date, SolarTime.this.latitude, SolarTime.this.longitude, effAngle);
            }
        };

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
    public ChronoFunction<CalendarDate, PlainTime> sunrise(final TZID tzid) {

        return new ChronoFunction<CalendarDate, PlainTime>() {
            @Override
            public PlainTime apply(CalendarDate date) {
                Moment m = SolarTime.this.getCalculator().sunrise(
                    date, latitude, longitude, SolarTime.this.zenithAngle());
                if (m != null) {
                    return m.toZonalTimestamp(tzid).getWallTime();
                } else {
                    return null;
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
    public ChronoFunction<CalendarDate, Moment> sunset() {

        return new ChronoFunction<CalendarDate, Moment>() {
            @Override
            public Moment apply(CalendarDate date) {
                return SolarTime.this.getCalculator().sunset(
                    date, SolarTime.this.latitude, SolarTime.this.longitude, SolarTime.this.zenithAngle());
            }
        };

    }

    /**
     * <p>Calculates the time of given twilight at sunset and the location of this instance. </p>
     *
     * <p>Note: The precision is generally constrained to minutes. And the atmospheric refraction
     * is here not taken into account. </p>
     *
     * @param   twilight    relevant definition of twilight
     * @return  twilight function at sunset applicable on any calendar date
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Berechnet die Zeit der angegebenen D&auml;mmerung zum Sonnenuntergang an der Position dieser Instanz. </p>
     *
     * <p>Hinweis: Die Genauigkeit liegt generell im Minutenbereich. Die atmosp&auml;rische Lichtbeugung wird
     * hier nicht ber&uuml;cksichtigt. </p>
     *
     * @param   twilight    relevant definition of twilight
     * @return  twilight function at sunset applicable on any calendar date
     * @since   3.34/4.29
     */
    public ChronoFunction<CalendarDate, Moment> sunset(Twilight twilight) {

        final double effAngle = 90.0 + this.geodeticAngle() + twilight.getAngle();
        return new ChronoFunction<CalendarDate, Moment>() {
            @Override
            public Moment apply(CalendarDate date) {
                return SolarTime.this.getCalculator().sunset(
                    date, SolarTime.this.latitude, SolarTime.this.longitude, effAngle);
            }
        };

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
    public ChronoFunction<CalendarDate, PlainTime> sunset(final TZID tzid) {

        return new ChronoFunction<CalendarDate, PlainTime>() {
            @Override
            public PlainTime apply(CalendarDate date) {
                Moment m =
                    SolarTime.this.getCalculator().sunset(date, latitude, longitude, SolarTime.this.zenithAngle());
                if (m == null) {
                    return null;
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
                return transitAtNoon(date, SolarTime.this.longitude, SolarTime.this.calculator);
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
                return transitAtMidnight(date, SolarTime.this.longitude, SolarTime.this.calculator);
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

    // used in test classes, too
    double getHighestElevationOfSun(PlainDate date) {

        Moment noon = date.get(this.transitAtNoon());
        double jde = JulianDay.getValue(noon, TimeScale.TT);
        double decInRad = Math.toRadians(this.getCalculator().getFeature(jde, DECLINATION));
        double latInRad = Math.toRadians(this.latitude);
        double sinElevation = // Extra term left out => Math.cos(Math.toRadians(trueNoon)) := 1.0 (per definition)
            Math.sin(latInRad) * Math.sin(decInRad) + Math.cos(latInRad) * Math.cos(decInRad); // Meeus (13.6)
        return Math.toDegrees(Math.asin(sinElevation));

    }

    static PlainDate toGregorian(CalendarDate date) {

        if (date instanceof PlainDate) {
            return (PlainDate) date;
        } else {
            return PlainDate.of(date.getDaysSinceEpochUTC(), EpochDays.UTC);
        }

    }

    static Moment fromLocalEvent(
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
        return utc.with(Moment.PRECISION, precision(calculator));

    }

    private static Moment transitAtMidnight(
        CalendarDate date,
        double longitude,
        String calculator
    ) {

        Moment utc = fromLocalEvent(date, 0, longitude, calculator);
        return utc.with(Moment.PRECISION, precision(calculator));

    }

    private static TimeUnit precision(String calculator) {

        return (calculator.equals(StdSolarCalculator.SIMPLE.name()) ? TimeUnit.MINUTES : TimeUnit.SECONDS);

    }

    private double geodeticAngle() {

        return this.getCalculator().getGeodeticAngle(this.latitude, this.altitude);

    }

    private double zenithAngle() {

        return this.getCalculator().getZenithAngle(this.latitude, this.altitude);

    }

    private static void check(
        double latitude,
        double longitude,
        int altitude,
        String calculator
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
        } else if (calculator.isEmpty()) {
            throw new IllegalArgumentException("Missing calculator.");
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
     * <p>Helper class to construct a new instance of {@code SolarTime}. </p>
     *
     * @author  Meno Hochschild
     * @since   3.35/4.30
     */
    /*[deutsch]
     * <p>Hilfsklasse f&uuml;r die Erzeugung einer neuen Instanz von {@code SolarTime}. </p>
     *
     * @author  Meno Hochschild
     * @since   3.35/4.30
     */
    public static class Builder {

        //~ Instanzvariablen ----------------------------------------------

        private double latitude = Double.NaN;
        private double longitude = Double.NaN;
        private int altitude = 0;
        private String calculator = DEFAULT_CALCULATOR.name();

        //~ Konstruktoren -------------------------------------------------

        private Builder() {
            super();

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
         * <p>Sets the reference to the solar time calculator to be used. </p>
         *
         * @param   calculator  name of solar time calculator
         * @return  this instance for method chaining
         */
        /*[deutsch]
         * <p>Setzt die Referenz auf das zugrundeliegende Berechnungsverfahren. </p>
         *
         * @param   calculator  name of solar time calculator
         * @return  this instance for method chaining
         */
        public Builder usingCalculator(String calculator) {

            if (calculator.isEmpty()) {
                throw new IllegalArgumentException("Missing calculator.");
            } else if (!CALCULATORS.containsKey(calculator)) {
                throw new IllegalArgumentException("Unknown calculator: " + calculator);
            }

            this.calculator = calculator;
            return this;

        }

        /**
         * <p>Sets the solar time calculator to be used. </p>
         *
         * @param   calculator  instance of solar time calculator
         * @return  this instance for method chaining
         * @since   3.36/4.31
         */
        /*[deutsch]
         * <p>Setzt das zugrundeliegende Berechnungsverfahren. </p>
         *
         * @param   calculator  instance of solar time calculator
         * @return  this instance for method chaining
         * @since   3.36/4.31
         */
        public Builder usingCalculator(Calculator calculator) {

            CALCULATORS.putIfAbsent(calculator.name(), calculator);
            this.calculator = calculator.name();
            return this;

        }

        /**
         * <p>Finishes the build-process. </p>
         *
         * @return  new configured instance of {@code SolarTime}
         * @throws  IllegalStateException if either latitude or longitude have not yet been set
         */
        /*[deutsch]
         * <p>Schlie&szlig;t den Erzeugungs- und Konfigurationsprozess ab. </p>
         *
         * @return  new configured instance of {@code SolarTime}
         * @throws  IllegalStateException if either latitude or longitude have not yet been set
         */
        public SolarTime build() {

            if (Double.isNaN(this.latitude)) {
                throw new IllegalStateException("Latitude was not yet set.");
            } else if (Double.isNaN(this.longitude)) {
                throw new IllegalStateException("Longitude was not yet set.");
            }

            return new SolarTime(this.latitude, this.longitude, this.altitude, this.calculator);

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
     * <p>An SPI-interface representing a facade for the calculation engine regarding sunrise or sunset. </p>
     *
     * @see     java.util.ServiceLoader
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Ein SPI-Interface, das eine Fassade f&uuml;r die Berechnung von Sonnenaufgang oder Sonnenuntergang
     * darstellt. </p>
     *
     * @see     java.util.ServiceLoader
     * @since   3.34/4.29
     */
    public interface Calculator {

        //~ Statische Felder/Initialisierungen ----------------------------

        /**
         * Follows closely the algorithms published by NOAA (National Oceanic and Atmospheric Administration).
         *
         * <p>See {@link StdSolarCalculator#NOAA}. </p>
         */
        /*[deutsch]
         * Folgt nahe den Algorithmen, die von der NOAA (National Oceanic and Atmospheric Administration)
         * ver&ouml;ffentlicht wurden.
         *
         * <p>Siehe {@link StdSolarCalculator#NOAA}. </p>
         */
        @Deprecated
        String NOAA = "NOAA";

        /**
         * Simple and relatively fast but rather imprecise calculator.
         *
         * <p>See {@link StdSolarCalculator#SIMPLE}. </p>
         */
        /*[deutsch]
         * Einfache und relativ schnelle aber eher ungenaue Berechnungsmethode.
         *
         * <p>Siehe {@link StdSolarCalculator#SIMPLE}. </p>
         */
        @Deprecated
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

        /**
         * <p>Calculates a value suitable for given time and feature. </p>
         *
         * <p>Subclasses overriding this method document which features are supported.
         * At least the feature &quot;declination&quot; must be supported by subclasses. </p>
         *
         * @param   jde             julian day in ephemeris time
         * @param   nameOfFeature   describes what kind of value shall be calculated
         * @return  result value or {@code Double.NaN} if the feature is not supported
         * @see     #declination(double)
         */
        /*[deutsch]
         * <p>Berechnet einen Wert passend zur angegebenen Zeit und zum angegebenen Merkmal. </p>
         *
         * <p>Subklassen, die diese Methode &uuml;berschreiben, dokumentieren, welche Merkmale
         * unterst&uuml;tzt werden. Mindestens das Merkmal &quot;declination&quot; mu&szlig;
         * von Subklassen unterst&uuml;tzt werden. </p>
         *
         * @param   jde             julian day in ephemeris time
         * @param   nameOfFeature   describes what kind of value shall be calculated
         * @return  result value or {@code Double.NaN} if the feature is not supported
         * @see     #declination(double)
         */
        double getFeature(
            double jde,
            String nameOfFeature
        );

        /**
         * <p>Calculates the additional geodetic angle due to the extra altitude of the observer. </p>
         *
         * <p>The default implementation just returns {@code 0.0}. </p>
         *
         * @param   latitude    the geographical latitude in degrees
         * @param   altitude    the altitude of the observer in meters
         * @return  geodetic angle correction in degrees
         * @since   3.36/4.31
         */
        /*[deutsch]
         * <p>Berechnet die zus&auml;tzliche geod&auml;tische Winkelkorrektur, die der H&ouml;he
         * des Beobachters auf der Erdoberfl&auml;che geschuldet ist. </p>
         *
         * <p>Die Standardimplementierung liefert nur {@code 0.0}. </p>
         *
         * @param   latitude    the geographical latitude in degrees
         * @param   altitude    the altitude of the observer in meters
         * @return  geodetic angle correction in degrees
         * @since   3.36/4.31
         */
        double getGeodeticAngle(
            double latitude,
            int altitude
        );

        /**
         * <p>Calculates the angle of the sun relative to the zenith at sunrise or sunset. </p>
         *
         * <p>The default implementation just uses the standard refraction angle of 34 arc minutes,
         * adds to it {@code 90°} and the {@link #getGeodeticAngle(double, int) geodetic angle correction}. </p>
         *
         * @param   latitude    the geographical latitude in degrees
         * @param   altitude    the altitude of the observer in meters
         * @return  effective zenith angle in degrees
         * @since   3.36/4.31
         */
        /*[deutsch]
         * <p>Berechnet den Winkel der Sonne bei Sonnenauf- oder Sonnenuntergang relativ zum Zenit. </p>
         *
         * <p>Die Standardimplementierung verwendet nur den normalen Refraktionswinkel von 34 Bogenminuten und
         * addiert dazu {@code 90°} und die {@link #getGeodeticAngle(double, int) geod&auml;tische Winkelkorrektur}.
         * </p>
         *
         * @param   latitude    the geographical latitude in degrees
         * @param   altitude    the altitude of the observer in meters
         * @return  effective zenith angle in degrees
         * @since   3.36/4.31
         */
        double getZenithAngle(
            double latitude,
            int altitude
        );

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

}
