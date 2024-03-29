/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2023 Meno Hochschild, <http://www.menodata.de/>
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
import net.time4j.scale.LeapSeconds;
import net.time4j.scale.TimeScale;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;


/**
 * Contains various routines to determine solar time.
 *
 * <p>Notice: Most chronological functions use the astronomical equation of time. Hence they are only applicable
 * for years between -2000 and +3000 otherwise an {@code IllegalArgumentException} will be thrown. </p>
 *
 * <p><strong>Example for sunrise and sunset on the top of Africas highest mountain Kilimanjaro:</strong></p>
 *
 * <pre>
 *     PlainDate date = PlainDate.of(2017, 12, 22);
 *     TZID tzid = () -&gt; &quot;Africa/Dar_es_Salaam&quot;; // Tanzania: UTC+03:00
 *
 *     // high altitude implies earlier sunrise and later sunset
 *     SolarTime kibo5895 =
 *       SolarTime.ofLocation()
 *          .southernLatitude(3, 4, 0)
 *          .easternLongitude(37, 21, 33)
 *          .atAltitude(5895)
 *          .usingCalculator(StdSolarCalculator.TIME4J) // this calculator takes into account the altitude
 *          .build();
 *
 *     assertThat(
 *       date.get(kibo5895.sunrise()).get().toZonalTimestamp(tzid).toTime(),
 *       is(PlainTime.of(6, 10, 35)));
 *     assertThat(
 *       date.get(kibo5895.sunset()).get().toZonalTimestamp(tzid).toTime(),
 *       is(PlainTime.of(18, 47, 47)));
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
 * <p><strong>Interpretation of calendar dates:</strong></p>
 *
 * <p>This class interpretes every calendar date input as on LMT (Local Mean Time) unless the builder-method
 * {@link net.time4j.calendar.astro.SolarTime.Builder#inTimezone(TZID)} is called. Users only need
 * to pay attention to this subtile difference if a few areas along the international date border
 * like Kiribati or Samoa are involved. Following example demonstrates the difference: </p>
 *
 * <p><i>LMT-date</i></p>
 *
 * <pre>
 *     TZID tzid = Timezone.of(&quot;Pacific/Apia&quot;).getID();
 *     SolarTime apia = SolarTime.ofLocation().southernLatitude(13, 50, 0).westernLongitude(171, 45, 0).build();
 *     assertThat(
 *       PlainDate.of(2011, 12, 31).get(apia.sunrise()).get().toZonalTimestamp(tzid),
 *       is(PlainTimestamp.of(2012, 1, 1, 7, 2, 13))); // civil date is one day later than LMT-date
 * </pre>
 *
 * <p><i>Zoned date</i></p>
 *
 * <pre>
 *     TZID tzid = Timezone.of(&quot;Pacific/Apia&quot;).getID();
 *     SolarTime apia =
 *       SolarTime.ofLocation()
 *         .southernLatitude(13, 50, 0)
 *         .westernLongitude(171, 45, 0)
 *         .inTimezone(tzid)
 *         .build();
 *     assertThat(
 *       PlainDate.of(2012, 1, 1).get(apia.sunrise()).get().toZonalTimestamp(tzid),
 *       is(PlainTimestamp.of(2012, 1, 1, 7, 2, 13))); // civil date is same as input
 * </pre>
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
 * <p><strong>Beispielrechnung f&uuml;r den Sonnenaufgang und -untergang
 * auf Afrikas h&ouml;chstem Berg Kilimanjaro:</strong></p>
 *
 * <pre>
 *     PlainDate date = PlainDate.of(2017, 12, 22);
 *     TZID tzid = () -&gt; &quot;Africa/Dar_es_Salaam&quot;; // Tanzania: UTC+03:00
 *
 *     // die gro&szlig;e H&ouml;he berechnet einen fr&uuml;heren Sonnenaufgang und sp&auml;teren Sonnenuntergang
 *     SolarTime kibo5895 =
 *       SolarTime.ofLocation()
 *          .southernLatitude(3, 4, 0)
 *          .easternLongitude(37, 21, 33)
 *          .atAltitude(5895)
 *          .usingCalculator(StdSolarCalculator.TIME4J) // ber&uuml;cksichtigt die H&ouml;he
 *          .build();
 *
 *     assertThat(
 *       date.get(kibo5895.sunrise()).get().toZonalTimestamp(tzid).toTime(),
 *       is(PlainTime.of(6, 10, 35)));
 *     assertThat(
 *       date.get(kibo5895.sunset()).get().toZonalTimestamp(tzid).toTime(),
 *       is(PlainTime.of(18, 47, 47)));
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
 * <p><strong>Interpretation von Kalenderdaten:</strong></p>
 *
 * <p>Diese Klasse interpretiert jedes Kalenderdatum als LMT-Datum (Local Mean Time = Mittlere Ortszeit),
 * es sei denn, die <i>Builder</i>-Methode {@link net.time4j.calendar.astro.SolarTime.Builder#inTimezone(TZID)}
 * wird aufgerufen. Anwender brauchen sich nur um diesen feinen Unterschied Gedanken zu machen, wenn einige
 * wenige Gebiete der Erde entlang der internationalen Datumsgrenze wie Kiribati oder Samoa beteiligt sind.
 * Folgende Beispiele demonstrieren den Unterschied: </p>
 *
 * <p><i>LMT-Datum</i></p>
 *
 * <pre>
 *     TZID tzid = Timezone.of(&quot;Pacific/Apia&quot;).getID();
 *     SolarTime apia = SolarTime.ofLocation().southernLatitude(13, 50, 0).westernLongitude(171, 45, 0).build();
 *     assertThat(
 *       PlainDate.of(2011, 12, 31).get(apia.sunrise()).get().toZonalTimestamp(tzid),
 *       is(PlainTimestamp.of(2012, 1, 1, 7, 2, 13))); // Zonendatum ist einen Tag nach dem LMT-Datum
 * </pre>
 *
 * <p><i>Zonendatum</i></p>
 *
 * <pre>
 *     TZID tzid = Timezone.of(&quot;Pacific/Apia&quot;).getID();
 *     SolarTime apia =
 *       SolarTime.ofLocation()
 *         .southernLatitude(13, 50, 0)
 *         .westernLongitude(171, 45, 0)
 *         .inTimezone(tzid)
 *         .build();
 *     assertThat(
 *       PlainDate.of(2012, 1, 1).get(apia.sunrise()).get().toZonalTimestamp(tzid),
 *       is(PlainTimestamp.of(2012, 1, 1, 7, 2, 13))); // Zonendatum entspricht dem Eingabedatum
 * </pre>
 *
 * @author  Meno Hochschild
 * @since   3.33/4.28
 */
public final class SolarTime
    implements GeoLocation, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    static final double SUN_RADIUS = 16.0;
    static final double STD_REFRACTION = 34.0;
    static final double STD_ZENITH = 90.0 + (SUN_RADIUS + STD_REFRACTION) / 60.0;
    static final String DECLINATION = "declination";
    static final String RIGHT_ASCENSION = "right-ascension";
    static final double ARC_MIN = 1.0 / 60;

    private static final Calculator DEFAULT_CALCULATOR;
    private static final ConcurrentMap<String, Calculator> CALCULATORS;

    static {
        Calculator loaded = null;
        ConcurrentMap<String, Calculator> calculators = new ConcurrentHashMap<>();
        for (Calculator calculator : ResourceLoader.getInstance().services(Calculator.class)) {
            loaded = calculator;
            calculators.put(calculator.name(), calculator);
        }
        for (Calculator calculator : StdSolarCalculator.values()) {
            calculators.put(calculator.name(), calculator);
        }
        CALCULATORS = calculators;
        DEFAULT_CALCULATOR = ((loaded == null) ? StdSolarCalculator.NOAA : loaded);
    }

    private static final SolarTime JERUSALEM = // temple area
        SolarTime.ofLocation()
            .easternLongitude(35, 14, 5)
            .northernLatitude(31, 46, 44)
            .atAltitude(721)
            .usingCalculator(StdSolarCalculator.TIME4J)
            .build();
    private static final SolarTime MECCA = // kaaba
        SolarTime.ofLocation()
            .easternLongitude(39, 49, 34.06)
            .northernLatitude(21, 25, 21.22)
            .atAltitude(298)
            .usingCalculator(StdSolarCalculator.TIME4J)
            .build();

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

    /**
     * @serial  zone identifier for the interpretation of calendar date input (optional)
     * @since   3.38/4.33
     */
    private final TZID observerZoneID;

    //~ Konstruktoren -----------------------------------------------------

    private SolarTime(
        double latitude,
        double longitude,
        int altitude,
        String calculator,
        TZID observerZoneID
    ) {
        super();

        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.calculator = calculator;
        this.observerZoneID = observerZoneID;

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
     * <p>The default calculator is usually {@link StdSolarCalculator#NOAA} unless another calculator was
     * set up via the service loader mechnism. </p>
     *
     * <p>This method handles the geographical location in decimal degrees only. If these data are given
     * in degrees, arc minutes and arc seconds then users should apply the {@link #ofLocation() builder}
     * approach instead. </p>
     *
     * @param   latitude    geographical latitude in decimal degrees ({@code -90.0 <= x <= +90.0})
     * @param   longitude   geographical longitude in decimal degrees ({@code -180.0 <= x < 180.0})
     * @return  instance of local solar time
     * @throws  IllegalArgumentException if the coordinates are out of range
     * @see     #ofLocation(double, double, int, String)
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Liefert die Sonnenzeit zur angegebenen geographischen Position auf Meeresh&ouml;he. </p>
     *
     * <p>Die Standardberechnungsmethode ist gew&ouml;hnlich {@link StdSolarCalculator#NOAA}, es sei denn,
     * eine andere Methode wurde &uuml;ber den {@code ServiceLoader}-Mechanismus geladen. </p>
     *
     * <p>Diese Methode nimmt geographische Angaben nur in Dezimalgrad entgegen. Wenn diese Daten aber
     * in Grad, Bogenminuten und Bogensekunden vorliegen, sollten Anwender den {@link #ofLocation() Builder-Ansatz}
     * bevorzugen. </p>
     *
     * @param   latitude    geographical latitude in decimal degrees ({@code -90.0 <= x <= +90.0})
     * @param   longitude   geographical longitude in decimal degrees ({@code -180.0 <= x < 180.0})
     * @return  instance of local solar time
     * @throws  IllegalArgumentException if the coordinates are out of range
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
     * @throws  IllegalArgumentException if the coordinates are out of range or the calculator is unknown
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
     * @throws  IllegalArgumentException if the coordinates are out of range or the calculator is unknown
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
        return new SolarTime(latitude, longitude, altitude, calculator, null);

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
     * @throws  IllegalArgumentException if the coordinates are out of range
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
     * @throws  IllegalArgumentException if the coordinates are out of range
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
        return new SolarTime(latitude, longitude, altitude, name, null);

    }

    /**
     * <p>Obtains an instance of solar time for the temple area in Jerusalem which has a prominent meaning
     * in Hebrew calendar and time. </p>
     *
     * @return  SolarTime
     * @since   3.37/4.32
     */
    /*[deutsch]
     * <p>Liefert eine Instanz der Sonnenzeit f&uuml;r das Tempelgebiet in Jerusalem, das eine besondere
     * Bedeutung im hebr&auml;ischen Kalender hat. </p>
     *
     * @return  SolarTime
     * @since   3.37/4.32
     */
    public static SolarTime ofJerusalem() {

        return JERUSALEM;

    }

    /**
     * <p>Obtains an instance of solar time for the Kaaba in Mecca which has a prominent meaning
     * in islamic calendar. </p>
     *
     * <p>The start of day in Mecca can be obtained by the expression
     * {@code StartOfDay.definedBy(SolarTime.ofMecca().sunset())}. </p>
     *
     * @return  SolarTime
     * @see     net.time4j.engine.StartOfDay#definedBy(ChronoFunction)
     * @since   3.37/4.32
     */
    /*[deutsch]
     * <p>Liefert eine Instanz der Sonnenzeit f&uuml;r die Kaaba in Mekka, die eine besondere
     * Bedeutung im islamischen Kalender hat. </p>
     *
     * <p>Der Beginn des islamischen Tages in Mekka kann mittels des Ausdrucks
     * {@code StartOfDay.definedBy(SolarTime.ofMecca().sunset())} bestimmt werden. </p>
     *
     * @return  SolarTime
     * @see     net.time4j.engine.StartOfDay#definedBy(ChronoFunction)
     * @since   3.37/4.32
     */
    public static SolarTime ofMecca() {

        return MECCA;

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

    /**
     * <p>Obtains the underlying calculator. </p>
     *
     * @return  String
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Liefert die zugrundeliegende Berechnungsmethode. </p>
     *
     * @return  String
     * @since   3.34/4.29
     */
    public Calculator getCalculator() {

        return CALCULATORS.get(this.calculator);

    }

    /**
     * <p>Obtains the optional observer timezone which might be associated with any calendar date input. </p>
     *
     * <p>Usually, this setting is not relevant for most regions of Earth but can be specified via the
     * builder approach to handle some special cases. </p>
     *
     * @return  optional zone identifier associated with this geographical position
     * @see     #ofLocation()
     * @see     Builder#inTimezone(TZID)
     * @since   5.6
     */
    /*[deutsch]
     * <p>Liefert die optionale Beobachterzeitzone, die mit einer beliebigen Kalenderdatumseingabe
     * verkn&uuml;pft ist. </p>
     *
     * <p>Gew&ouml;hnlich ist diese Einstellung f&uuml;r die meisten Gebiete der Erde nicht von Bedeutung,
     * kann aber &uuml;ber den {@code Builder}-Ansatz konfiguriert werden, um einige Spezialf&auml;lle
     * abzuhandeln. </p>
     *
     * @return  optional zone identifier associated with this geographical position
     * @see     #ofLocation()
     * @see     Builder#inTimezone(TZID)
     * @since   5.6
     */
    public Optional<TZID> getObserverZoneID() {

        return (this.observerZoneID == null) ? Optional.empty() : Optional.of(this.observerZoneID);

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
     * <p>The result might be undefined (indicated by {@code Optional.empty()}) if the sun does not
     * rise on given day (midnight sun or polar night). </p>
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
     * <p>Das Ergebnis kann undefiniert sein (angezeigt durch {@code Optional.empty()})
     * wenn die Sonne an einem Tag gar nicht aufgeht (Mitternachtssonne oder Polarnacht). </p>
     *
     * @return  sunrise function applicable on any calendar date
     * @since   3.34/4.29
     */
    public ChronoFunction<CalendarDate, Optional<Moment>> sunrise() {

        return date -> this.getCalculator().sunrise(toLMT(date), this.latitude, this.longitude, this.zenithAngle());

    }

    /**
     * <p>Calculates the time of given twilight at sunrise and the location of this instance. </p>
     *
     * <p>Note: The precision is generally constrained to minutes. And the atmospheric refraction
     * is here not taken into account. </p>
     *
     * <p>The result might be undefined (indicated by {@code Optional.empty()}) if the twilight event does not
     * happen on given day. </p>
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
     * <p>Das Ergebnis kann undefiniert sein (angezeigt durch {@code Optional.empty()})
     * wenn das D&auml;mmerungsereignis zum angegebenen Tag nicht eintritt. </p>
     *
     * @param   twilight    relevant definition of twilight
     * @return  twilight function at sunrise applicable on any calendar date
     * @since   3.34/4.29
     */
    public ChronoFunction<CalendarDate, Optional<Moment>> sunrise(Twilight twilight) {

        double effAngle = 90.0 + this.geodeticAngle() + twilight.getAngle();
        return date -> this.getCalculator().sunrise(toLMT(date), this.latitude, this.longitude, effAngle);

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
     * <p>The result might be undefined (indicated by {@code Optional.empty()}) if the sun does not
     * set on given day (midnight sun or polar night). </p>
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
     * <p>Das Ergebnis kann undefiniert sein (angezeigt durch {@code Optional.empty()})
     * wenn die Sonne an einem Tag gar nicht untergeht (Mitternachtssonne oder Polarnacht). </p>
     *
     * @return  sunset function applicable on any calendar date
     * @since   3.34/4.29
     */
    public ChronoFunction<CalendarDate, Optional<Moment>> sunset() {

        return date -> this.getCalculator().sunset(toLMT(date), this.latitude, this.longitude, this.zenithAngle());

    }

    /**
     * <p>Calculates the time of given twilight at sunset and the location of this instance. </p>
     *
     * <p>Note: The precision is generally constrained to minutes. And the atmospheric refraction
     * is here not taken into account. </p>
     *
     * <p>The result might be undefined (indicated by {@code Optional.empty()}) if the twilight event does not
     * happen on given day. </p>
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
     * <p>Das Ergebnis kann undefiniert sein (angezeigt durch {@code Optional.empty()})
     * wenn das D&auml;mmerungsereignis zum angegebenen Tag nicht eintritt. </p>
     *
     * @param   twilight    relevant definition of twilight
     * @return  twilight function at sunset applicable on any calendar date
     * @since   3.34/4.29
     */
    public ChronoFunction<CalendarDate, Optional<Moment>> sunset(Twilight twilight) {

        double effAngle = 90.0 + this.geodeticAngle() + twilight.getAngle();
        return date -> this.getCalculator().sunset(toLMT(date), this.latitude, this.longitude, effAngle);

    }

    /**
     * <p>Queries a given calendar date for its associated sunshine data. </p>
     *
     * <p>The timezone parameter enables users to query for solar time data described in terms of a potentially
     * quite different zone of the earth. However, the parameter does not interprete the input calendar date. </p>
     *
     * <p>The calculation is only possible if the underlying calculator supports the feature of solar declination.
     * Otherwise an {@code UnsupportedOperationException} will be thrown. </p>
     *
     * @param   tzid    the identifier of the timezone any local times of the result refer to
     * @return  function for obtaining sunshine data
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Fragt zu einem gegebenen Kalenderdatum die damit verbundenen Sonnenscheindaten ab. </p>
     *
     * <p>Der Zeitzonenparameter erm&ouml;glicht es, die Sonnenzeitdaten im Kontext einer eventuell
     * ganz anderen Zeitzone zu beschreiben. Er dient jedoch nicht der Interpretation des Eingabedatums. </p>
     *
     * <p>Die Berechnung ist nur m&ouml;glich, wenn der zugrundeliegende Algorithmus das Merkmal der Sonnendeklination
     * unterst&uuml;tzt, sonst wird eine {@code UnsupportedOperationException} geworfen. </p>
     *
     * @param   tzid    the identifier of the timezone any local times of the result refer to
     * @return  function for obtaining sunshine data
     * @since   3.34/4.29
     */
    public ChronoFunction<CalendarDate, Sunshine> sunshine(TZID tzid) {

        return date -> {
            PlainDate d = toGregorian(toLMT(date));
            Calculator c = this.getCalculator();
            double zenith = this.zenithAngle();
            Optional<Moment> start = c.sunrise(date, this.latitude, this.longitude, zenith);
            Optional<Moment> end = c.sunset(date, this.latitude, this.longitude, zenith);
            boolean absent = false;
            if (!start.isPresent() && !end.isPresent()) {
                double elevation = this.getHighestElevationOfSun(d);
                if (Double.compare(elevation, 90 - zenith) < 0) {
                    absent = true;
                }
            }
            return new Sunshine(d, start, end, tzid, absent);
        };

    }

    /**
     * <p>Determines if the sun is invisible all day on a given calendar date. </p>
     *
     * <p>The calculation is only possible if the underlying calculator supports the feature of solar declination.
     * Otherwise an {@code UnsupportedOperationException} will be thrown. </p>
     *
     * @return  ChronoCondition
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Ermittelt, ob an einem gegebenen Kalenderdatum Polarnacht herrscht. </p>
     *
     * <p>Die Berechnung ist nur m&ouml;glich, wenn der zugrundeliegende Algorithmus das Merkmal der Sonnendeklination
     * unterst&uuml;tzt, sonst wird eine {@code UnsupportedOperationException} geworfen. </p>
     *
     * @return  ChronoCondition
     * @since   3.34/4.29
     */
    public ChronoCondition<CalendarDate> polarNight() {

        return date -> {
            if (Double.compare(Math.abs(this.latitude), 66.0) < 0) {
                return false;
            }
            PlainDate d = toGregorian(toLMT(date));
            Calculator c = this.getCalculator();
            double zenith = this.zenithAngle();
            Optional<Moment> start = c.sunrise(date, this.latitude, this.longitude, zenith);
            Optional<Moment> end = c.sunset(date, this.latitude, this.longitude, zenith);
            if (start.isPresent() || end.isPresent()) {
                return false;
            }
            double elevation = this.getHighestElevationOfSun(d);
            return (Double.compare(elevation, 90 - zenith) < 0);
        };

    }

    /**
     * <p>Determines if the sun is visible all day on a given calendar date. </p>
     *
     * <p>The calculation is only possible if the underlying calculator supports the feature of solar declination.
     * Otherwise an {@code UnsupportedOperationException} will be thrown. </p>
     *
     * @return  ChronoCondition
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Ermittelt, ob an einem gegebenen Kalenderdatum Mitternachtssonne herrscht. </p>
     *
     * <p>Die Berechnung ist nur m&ouml;glich, wenn der zugrundeliegende Algorithmus das Merkmal der Sonnendeklination
     * unterst&uuml;tzt, sonst wird eine {@code UnsupportedOperationException} geworfen. </p>
     *
     * @return  ChronoCondition
     * @since   3.34/4.29
     */
    public ChronoCondition<CalendarDate> midnightSun() {

        return date -> {
            if (Double.compare(Math.abs(this.latitude), 66.0) < 0) {
                return false;
            }
            PlainDate d = toGregorian(toLMT(date));
            Calculator c = this.getCalculator();
            double zenith = this.zenithAngle();
            Optional<Moment> start = c.sunrise(date, this.latitude, this.longitude, zenith);
            Optional<Moment> end = c.sunset(date, this.latitude, this.longitude, zenith);
            if (start.isPresent() || end.isPresent()) {
                return false;
            }
            double elevation = this.getHighestElevationOfSun(d);
            return (Double.compare(elevation, 90 - zenith) > 0);
        };

    }

    /**
     * <p>Calculates the moment of noon at the location of this instance (solar transit). </p>
     *
     * <p>Note 1: The transit time does not tell if the sun is above or below the horizon. </p>
     * <p>Note 2: Here, the term noon is not related to civil timekeeping but the sundial time. </p>
     *
     * @return  noon function applicable on any calendar date
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Berechnet den Moment der h&ouml;chsten Position der Sonne an der Position dieser Instanz. </p>
     *
     * <p>Hinweis 1: Die Transit-Zeit besagt nicht, ob die Sonne &uuml;ber oder unter dem Horizont ist. </p>
     * <p>Hinweis 2: Der Mittagsbegriff ist hier nicht auf die b&uuml;rgerliche Zeit bezogen, sondern
     * auf die Sonnenuhr (lokale Ortszeit). </p>
     *
     * @return  noon function applicable on any calendar date
     * @since   3.34/4.29
     */
    public ChronoFunction<CalendarDate, Moment> transitAtNoon() {

        return date -> transitAtNoon(toLMT(date), this.longitude, this.calculator);

    }

    /**
     * <p>Calculates the moment of midnight at the location of this instance (lowest position of sun). </p>
     *
     * <p>Note 1: The transit time does not tell if the sun is above or below the horizon. </p>
     * <p>Note 2: Here, the term midnight is not related to civil timekeeping but the sundial time. </p>
     *
     * @return  midnight function applicable on any calendar date
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Berechnet den Moment der niedrigsten Position der Sonne an der Position dieser Instanz. </p>
     *
     * <p>Hinweis 1: Die Transit-Zeit besagt nicht, ob die Sonne &uuml;ber oder unter dem Horizont ist. </p>
     * <p>Hinweis 2: Der Mitternachtsbegriff ist hier nicht auf die b&uuml;rgerliche Zeit bezogen, sondern
     * auf die Sonnenuhr (lokale Ortszeit). </p>
     *
     * @return  midnight function applicable on any calendar date
     * @since   3.34/4.29
     */
    public ChronoFunction<CalendarDate, Moment> transitAtMidnight() {

        return date -> transitAtMidnight(toLMT(date), this.longitude, this.calculator);

    }

    /**
     * <p>Calculates the moment before noon when the given shadow occurs on given date
     * at the location of this instance. </p>
     *
     * <p>The function is not supported in polar regions (+/- 66°) because the angle of sun
     * is often changing too slowly to guarantee reliable results. Furthermore, the function
     * might obtain an empty result if the given shadow is too short for the date in question. </p>
     *
     * @param   objectHeight    the height of object in meters, must be positive
     * @param   shadowLength    the length of shadow thrown by the object in meters, must not be negative
     * @return  function for calculating the time of shadow applicable on any calendar date in minute precision
     * @throws  IllegalArgumentException if one or both parameters are not finite or out of range
     * @throws  UnsupportedOperationException if this instance is within the arctic or antarctic circle
     * @see     SunPosition#getShadowLength(double)
     * @see     #timeOfShadowAfterNoon(double, double)
     * @since   5.7
     */
    /*[deutsch]
     * <p>Berechnet den Moment in der ersten Tagesh&auml;lfte, zu dem der fragliche Schatten beobachtet
     * wird, an der Position dieser Instanz. </p>
     *
     * <p>Die Funktion wird in Polargebieten (+/- 66°) nicht unterst&uuml;tzt, weil sich der Winkel der Sonne
     * oft zu wenig ver&auml;ndert, um zuverl&auml;ssige Ergebnisse zu erm&ouml;glichen. Au&szlig;erdem kann
     * die Funktion ohne Ergebnis sein, wenn der angegebene Schatten zum fraglichen Tag zu kurz ist. </p>
     *
     * @param   objectHeight    the height of object in meters, must be positive
     * @param   shadowLength    the length of shadow thrown by the object in meters, must not be negative
     * @return  function for calculating the time of shadow applicable on any calendar date in minute precision
     * @throws  IllegalArgumentException if one or both parameters are not finite or out of range
     * @throws  UnsupportedOperationException if this instance is within the arctic or antarctic circle
     * @see     SunPosition#getShadowLength(double)
     * @see     #timeOfShadowAfterNoon(double, double)
     * @since   5.7
     */
    public ChronoFunction<CalendarDate, Optional<Moment>> timeOfShadowBeforeNoon(
        double objectHeight,
        double shadowLength
    ) {

        this.checkShadow(objectHeight, shadowLength);
        return date -> timeOfShadow(date, false, objectHeight, shadowLength);

    }

    /**
     * <p>Calculates the moment after noon when the given shadow occurs on given date
     * at the location of this instance. </p>
     *
     * <p>The function is not supported in polar regions (+/- 66°) because the angle of sun
     * is often changing too slowly to guarantee reliable results. Furthermore, the function
     * might obtain an empty result if the given shadow is too short for the date in question. </p>
     *
     * @param   objectHeight    the height of object in meters, must be positive
     * @param   shadowLength    the length of shadow thrown by the object in meters, must not be negative
     * @return  function for calculating the time of shadow applicable on any calendar date in minute precision
     * @throws  IllegalArgumentException if one or both parameters are not finite or out of range
     * @throws  UnsupportedOperationException if this instance is within the arctic or antarctic circle
     * @see     SunPosition#getShadowLength(double)
     * @see     #timeOfShadowBeforeNoon(double, double)
     * @since   5.7
     */
    /*[deutsch]
     * <p>Berechnet den Moment in der zweiten Tagesh&auml;lfte, zu dem der fragliche Schatten beobachtet
     * wird, an der Position dieser Instanz. </p>
     *
     * <p>Die Funktion wird in Polargebieten (+/- 66°) nicht unterst&uuml;tzt, weil sich der Winkel der Sonne
     * oft zu wenig ver&auml;ndert, um zuverl&auml;ssige Ergebnisse zu erm&ouml;glichen. Au&szlig;erdem kann
     * die Funktion ohne Ergebnis sein, wenn der angegebene Schatten zum fraglichen Tag zu kurz ist. </p>
     *
     * @param   objectHeight    the height of object in meters, must be positive
     * @param   shadowLength    the length of shadow thrown by the object in meters, must not be negative
     * @return  function for calculating the time of shadow applicable on any calendar date in minute precision
     * @throws  IllegalArgumentException if one or both parameters are not finite or out of range
     * @throws  UnsupportedOperationException if this instance is within the arctic or antarctic circle
     * @see     SunPosition#getShadowLength(double)
     * @see     #timeOfShadowBeforeNoon(double, double)
     * @since   5.7
     */
    public ChronoFunction<CalendarDate, Optional<Moment>> timeOfShadowAfterNoon(
        double objectHeight,
        double shadowLength
    ) {

        this.checkShadow(objectHeight, shadowLength);
        return date -> timeOfShadow(date, true, objectHeight, shadowLength);

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
                    && (this.altitude == that.altitude)
                    && equalZones(this.observerZoneID, that.observerZoneID)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return (
            this.calculator.hashCode()
                + 7 * Double.hashCode(this.latitude)
                + 31 * Double.hashCode(this.longitude)
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
        if (this.observerZoneID != null) {
            sb.append(",observerZoneID=");
            sb.append(this.observerZoneID.canonical());
        }
        sb.append(']');
        return sb.toString();

    }

    /**
     * <p>Determines the apparent solar time of any moment at given local time zone offset. </p>
     *
     * <p>Based on the astronomical equation of time. The default calculator is usually
     * {@link StdSolarCalculator#NOAA} unless another calculator was set up via the service loader mechnism. </p>
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
     * gew&ouml;hnlich {@link StdSolarCalculator#NOAA}, es sei denn, eine andere Methode wurde
     * &uuml;ber den {@code ServiceLoader}-Mechanismus geladen. </p>
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
     * <p>Determines the apparent solar time of any moment at given local time zone offset. </p>
     *
     * <p>Based on the astronomical equation of time. </p>
     *
     * @param   offset      the time zone offset which might depend on the geographical longitude
     * @param   calculator  name of solar time calculator
     * @return  function for getting the apparent solar time
     * @see     ZonalOffset#atLongitude(OffsetSign, int, int, double)
     * @see     #equationOfTime(Moment, String)
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
     * @since   3.34/4.29
     */
    public static ChronoFunction<Moment, PlainTimestamp> apparentAt(
        ZonalOffset offset,
        String calculator
    ) {

        return context -> {
            PlainTimestamp meanSolarTime = onAverage(context, offset);
            double eot = equationOfTime(context, calculator);
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
     * <p>See also <a href="https://en.wikipedia.org/wiki/Equation_of_time">Wikipedia</a>.
     * Relation: mean-solar-time + equation-of-time = apparent-solar-time</p>
     *
     * <p>The default calculator is usually {@link StdSolarCalculator#NOAA} unless another calculator was
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
     * <p>Die Standardberechnungsmethode ist gew&ouml;hnlich {@link StdSolarCalculator#NOAA}, es sei denn,
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
        double result = Math.toDegrees(Math.asin(sinElevation));

        if (Double.isNaN(result)) {
            throw new UnsupportedOperationException(
                "Solar declination not supported by: " + this.getCalculator().name());
        }

        return result;

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
        int nanos = (int) ((elapsed - secs) * 1_000_000_000);
        TimeScale scale = TimeScale.UT;
        if (!LeapSeconds.getInstance().isEnabled()) {
            secs += (86400 * 730);
            scale = TimeScale.POSIX;
        }
        Moment m1 = Moment.of(secs, nanos, scale);
        double eot = c.equationOfTime(JulianDay.getValue(m1, TimeScale.TT)); // first step

        secs = (long) Math.floor(eot);
        nanos = (int) ((eot - secs) * 1_000_000_000);
        Moment m2 = m1.minus(secs, TimeUnit.SECONDS).minus(nanos, TimeUnit.NANOSECONDS);
        eot = c.equationOfTime(JulianDay.getValue(m2, TimeScale.TT)); // second step

        secs = (long) Math.floor(eot);
        nanos = (int) ((eot - secs) * 1_000_000_000);
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

    private void checkShadow(
        double objectHeight,
        double shadowLength
    ) {
        if (!Double.isFinite(objectHeight) || (objectHeight <= 0.0)) {
            throw new IllegalArgumentException("Object height must be finite and positive.");
        } else if (!Double.isFinite(shadowLength) || (shadowLength < 0.0)) {
            throw new IllegalArgumentException("Length of shadow must be finite and not negative.");
        } else if (Math.abs(this.latitude) > 66.0) {
            throw new UnsupportedOperationException("Cannot calculate time of shadow for polar regions.");
        }
    }

    private Optional<Moment> timeOfShadow(
        CalendarDate date,
        boolean afterNoon,
        double objectHeight,
        double shadowLength
    ) {
        PlainDate d = toGregorian(toLMT(date));
        Optional<Moment> riseset = afterNoon ? d.get(sunset()) : d.get(sunrise());
        assert riseset.isPresent();
        Moment noon = d.get(transitAtNoon());
        double maxE = SunPosition.at(noon, this).getElevation();

        if (maxE <= ARC_MIN) {
            return riseset;
        } else {
            double elevation = (
                (shadowLength == 0.0)
                    ? 90.0
                    : Math.toDegrees(Math.atan(objectHeight / shadowLength)));

            if (elevation > maxE + ARC_MIN) {
                return Optional.empty();
            } else {
                return Optional.of(timeOfShadow(riseset.get().getPosixTime(), noon.getPosixTime(), elevation));
            }
        }
    }

    private Moment timeOfShadow(
        long lowSun,
        long highSun,
        double elevation
    ) {
        Moment center = Moment.of(Math.addExact(lowSun, highSun) / 2, TimeScale.POSIX);
        double sunpos = SunPosition.at(center, this).getElevation();

        if (Math.abs(sunpos - elevation) < ARC_MIN) {
            return center;
        } else if (Double.compare(elevation, sunpos) > 0.0) {
            return timeOfShadow(center.getPosixTime(), highSun, elevation);
        } else {
            return timeOfShadow(lowSun, center.getPosixTime(), elevation);
        }
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
        if (!Double.isFinite(latitude)) {
            throw new IllegalArgumentException("Latitude must be a finite value: " + latitude);
        } else if (!Double.isFinite(longitude)) {
            throw new IllegalArgumentException("Longitude must be a finite value: " + longitude);
        } else if ((Double.compare(latitude, 90.0) > 0) || (Double.compare(latitude, -90.0) < 0)) {
            throw new IllegalArgumentException("Degrees out of range -90.0 <= latitude <= +90.0: " + latitude);
        } else if ((Double.compare(longitude, 180.0) >= 0) || (Double.compare(longitude, -180.0) < 0)) {
            throw new IllegalArgumentException("Degrees out of range -180.0 <= longitude < +180.0: " + longitude);
        } else if (!Double.isFinite(altitude)) {
            throw new IllegalArgumentException("Altitude must be finite: " + altitude);
        } else if ((altitude < 0) || (altitude >= 11_000)) {
            throw new IllegalArgumentException("Meters out of range 0 <= altitude < +11,000: " + altitude);
        } else if (calculator.isEmpty()) {
            throw new IllegalArgumentException("Missing calculator.");
        } else if (!CALCULATORS.containsKey(calculator)) {
            throw new IllegalArgumentException("Unknown calculator: " + calculator);
        }
    }

    private CalendarDate toLMT(CalendarDate input) {
        if ((this.observerZoneID == null) || (Math.abs(this.longitude) < 150.0)) {
            return input;
        }

        PlainDate d = toGregorian(input);
        PlainTimestamp noon = d.at(PlainTime.of(12));

        if (!noon.isValid(this.observerZoneID)) {
            throw new ChronoException(
                "Calendar date does not exist in zone: " + input + " (" + this.observerZoneID.canonical() + ")");
        }

        ZonalOffset lmtOffset = ZonalOffset.atLongitude(new BigDecimal(this.longitude));
        return noon.inTimezone(this.observerZoneID).toZonalTimestamp(lmtOffset).getCalendarDate();
    }

    private static boolean equalZones(
        TZID z1,
        TZID z2
    ) {
        if (z1 == null) {
            return (z2 == null);
        } else if (z2 == null) {
            return false;
        } else {
            return z1.canonical().equals(z2.canonical());
        }
    }

    /**
     * @serialData  Checks the sanity of the state.
     * @param       in                          object input stream
     * @throws      IOException                 in any case of I/O-errors
     * @throws      ClassNotFoundException      in any case of I/O-errors
     * @throws      IllegalArgumentException    in any case of inconsistent state
     */
    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {

        in.defaultReadObject();
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
        private TZID observerZoneID = null;

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
         * into account. If this method is not called then a default altitude of zero is assumed. </p>
         *
         * <p>Attention: Users should also apply a calculator which is capable of altitude corrections. </p>
         *
         * @param   altitude    geographical altitude relative to sea level in meters ({@code 0 <= x < 11,0000})
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the argument is not finite or out of range
         * @see     #usingCalculator(SolarTime.Calculator)
         * @see     StdSolarCalculator#CC
         * @see     StdSolarCalculator#TIME4J
         */
        /*[deutsch]
         * <p>Setzt die H&ouml;he in Metern. </p>
         *
         * <p>Die H&ouml;henangabe dient der Modellierung einer geod&auml;tischen Korrektur und auch einer
         * Korrektur der atmosph&auml;rischen Lichtbeugung basierend auf der einfachen Annahme einer
         * Standardatmosph&auml;re. Anwender m&uuml;ssen im Auge behalten, da&szlig; die lokale Topologie
         * mit Bergen, die die Horizontlinie unterbrechen und spezielle Wetterbedingungen nicht berechenbar
         * sind. Wenn diese Methode nicht aufgerufen wird, wird eine H&ouml;he von 0 Metern angenommen. </p>
         *
         * <p>Achtung: Anwender sollten auch einen {@code Calculator} nehmen, der in der Lage ist,
         * H&ouml;henkorrekturen vorzunehmen. </p>
         *
         * @param   altitude    geographical altitude relative to sea level in meters ({@code 0 <= x < 11,0000})
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the argument is not finite or out of range
         * @see     #usingCalculator(SolarTime.Calculator)
         * @see     StdSolarCalculator#CC
         * @see     StdSolarCalculator#TIME4J
         */
        public Builder atAltitude(int altitude) {

            if (!Double.isFinite(altitude)) {
                throw new IllegalArgumentException("Altitude must be finite: " + altitude);
            } else if ((altitude < 0) || (altitude >= 11_000)) {
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
         * @throws  IllegalArgumentException if the argument is not valid
         */
        /*[deutsch]
         * <p>Setzt die Referenz auf das zugrundeliegende Berechnungsverfahren. </p>
         *
         * @param   calculator  name of solar time calculator
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the argument is not valid
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
         * <p>Helps to associate any calendar date input with given timezone. </p>
         *
         * <p>If this method is not called then every calendar date input will be interpreted
         * as LMT (=<i>Local Mean Time</i>). The subtile difference between an LMT-date and
         * a zoned date is usually invisible but can be relevant for a few areas near the
         * international date border (for example Kiribati or Samoa). Note that calendar dates
         * need to exist in given timezone otherwise a {@code ChronoException} will be thrown. </p>
         *
         * @param   observerZoneID      zone identifier associated with chosen geographical position
         * @return  this instance for method chaining
         * @since   3.38/4.33
         */
        /*[deutsch]
         * <p>Hilft, jede beliebige Kalenderdatumseingabe mit der angegebenen Zeitzonen zu verkn&uuml;pfen. </p>
         *
         * <p>Wenn diese Methode nicht aufgerufen wird, wird jede Kalenderdatumseingabe als LMT
         * (=<i>Local Mean Time</i> = Mittlere Ortzeit) interpretiert. Der subtile Unterschied
         * zwischen einem Ortsdatum und einem Zonendatum ist normalerweise nicht sichtbar, kann
         * aber f&uuml;r wenige Gebiete der Erde relevant sein, die entlang der internationalen
         * Datumslinie liegen (zum Beispiel Kiribati oder Samoa). Zu beachten: Ein Kalenderdatum
         * mu&szlig; in der angegebenen Zeitzone existieren, sonst wird eine {@code ChronoException}
         * geworfen. </p>
         *
         * @param   observerZoneID      zone identifier associated with chosen geographical position
         * @return  this instance for method chaining
         * @since   3.38/4.33
         */
        public Builder inTimezone(TZID observerZoneID) {

            if (observerZoneID == null) {
                throw new NullPointerException("Missing timezone identifier.");
            }

            this.observerZoneID = observerZoneID;
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

            return new SolarTime(this.latitude, this.longitude, this.altitude, this.calculator, this.observerZoneID);

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
     * <p><strong>Note:</strong> All implementations must have a public no-arg constructor. </p>
     *
     * @see     java.util.ServiceLoader
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Ein SPI-Interface, das eine Fassade f&uuml;r die Berechnung von Sonnenaufgang oder Sonnenuntergang
     * darstellt. </p>
     *
     * <p><strong>Hinweis:</strong> Alle Implementierungen m&uuml;ssen einen
     * &ouml;ffentlichen und parameterlosen Konstruktor haben. </p>
     *
     * @see     java.util.ServiceLoader
     * @since   3.34/4.29
     */
    public interface Calculator {

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
         * <p>The zenith angle can be represented by the result of the method
         * {@link #getZenithAngle(double, int) getZenithAngle(latitude, altitude)}. </p>
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
         * <p>Der Zenitwinkel kann mittels des Ergebnisses der Methode
         * {@link #getZenithAngle(double, int) getZenithAngle(latitude, altitude)}
         * berechnet werden. </p>
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
         * <p>The zenith angle can be represented by the result of the method
         * {@link #getZenithAngle(double, int) getZenithAngle(latitude, altitude)}. </p>
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
         * <p>Der Zenitwinkel kann mittels des Ergebnisses der Methode
         * {@link #getZenithAngle(double, int) getZenithAngle(latitude, altitude)}
         * berechnet werden. </p>
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
         * <p>Calculates a value suitable for given time and feature. </p>
         *
         * <p>Subclasses overriding this method document which features are supported.
         * At least the feature of &quot;declination&quot; should be supported. </p>
         *
         * @param   jde             julian day in ephemeris time
         * @param   nameOfFeature   describes what kind of value shall be calculated
         * @return  result value or {@code Double.NaN} if the feature is not supported
         */
        /*[deutsch]
         * <p>Berechnet einen Wert passend zur angegebenen Zeit und zum angegebenen Merkmal. </p>
         *
         * <p>Subklassen, die diese Methode &uuml;berschreiben, dokumentieren, welche Merkmale
         * unterst&uuml;tzt werden. Wenigstens das Merkmal &quot;declination&quot; sollte
         * dabei sein. </p>
         *
         * @param   jde             julian day in ephemeris time
         * @param   nameOfFeature   describes what kind of value shall be calculated
         * @return  result value or {@code Double.NaN} if the feature is not supported
         */
        default double getFeature(
            double jde,
            String nameOfFeature
        ) {

            return Double.NaN;

        }

        /**
         * <p>Calculates the additional geodetic angle due to the extra altitude of the observer. </p>
         *
         * <p>The default implementation just returns {@code 0.0}. Negative altitudes modelling
         * a valley between hight mountains are not supported but might be approximated by an angle
         * calculation using the altitude difference between valley and mountain and also using
         * the distance between valley and mountain resulting in a negative angle. </p>
         *
         * @param   latitude    the geographical latitude in degrees
         * @param   altitude    the altitude of the observer in meters, not negative
         * @return  geodetic angle correction in degrees
         * @since   3.36/4.31
         */
        /*[deutsch]
         * <p>Berechnet die zus&auml;tzliche geod&auml;tische Winkelkorrektur, die der H&ouml;he
         * des Beobachters auf der Erdoberfl&auml;che geschuldet ist. </p>
         *
         * <p>Die Standardimplementierung liefert nur {@code 0.0}. Negative H&ouml;hen, die ein
         * Tal zwischen hohen Bergen modellieren, werden nicht unterst&uuml;tzt. Eine solche
         * Ausnahmesituation mu&szlig; durch eine Winkelberechnung mit der H&ouml;hendifferenz
         * zwischen Berg und Tal sowie der Entfernung Berg-Tal gel&ouml;st werden, resultierend
         * in einem negativen Winkel. </p>
         *
         * @param   latitude    the geographical latitude in degrees
         * @param   altitude    the altitude of the observer in meters, not negative
         * @return  geodetic angle correction in degrees
         * @since   3.36/4.31
         */
        default double getGeodeticAngle(
            double latitude,
            int altitude
        ) {

            return 0.0;

        }

        /**
         * <p>Calculates the angle of the sun relative to the zenith at sunrise or sunset. </p>
         *
         * <p>The default implementation just uses the standard refraction angle of 34 arc minutes,
         * adds to it {@code 90°} and the {@link #getGeodeticAngle(double, int) geodetic angle correction}.
         * In case users do not want to take into account the geodetic angle correction, they might simply
         * subtract latter one. Example for the situation of Denver in USA which is suited in a valley with
         * far mountain ranges: </p>
         * 
         * <pre>
         *  double zenith = 
         *      calculator.getZenithAngle(latitude, altitude) 
         *      - calculator.getGeodeticAngle(latitude, altitude);
         * </pre>
         *
         * @param   latitude    the geographical latitude in degrees
         * @param   altitude    the altitude of the observer in meters, not negative
         * @return  effective zenith angle in degrees
         * @since   3.36/4.31
         */
        /*[deutsch]
         * <p>Berechnet den Winkel der Sonne bei Sonnenauf- oder Sonnenuntergang relativ zum Zenit. </p>
         *
         * <p>Die Standardimplementierung verwendet nur den normalen Refraktionswinkel von 34 Bogenminuten und
         * addiert dazu {@code 90°} und die {@link #getGeodeticAngle(double, int) geod&auml;tische Winkelkorrektur}.
         * Wird als Zenitwinkel eine Implementierung ohne Ber&uuml;cksichtigung der geod&auml;tischen 
         * Winkelkorrektur gew&uuml;nscht, darf letztere einfach subtrahiert werden. Beispiel f&uuml;r die
         * Situation von Denver in den USA, das in einem weiten Tal mit fernen Bergketten gelegen ist: </p>
         * 
         * <pre>
         *  double zenith = 
         *      calculator.getZenithAngle(latitude, altitude) 
         *      - calculator.getGeodeticAngle(latitude, altitude);
         * </pre>
         *
         * @param   latitude    the geographical latitude in degrees
         * @param   altitude    the altitude of the observer in meters, not negative
         * @return  effective zenith angle in degrees
         * @since   3.36/4.31
         */
        default double getZenithAngle(
            double latitude,
            int altitude
        ) {

            return STD_ZENITH + this.getGeodeticAngle(latitude, altitude);

        }

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
            Optional<Moment> start,
            Optional<Moment> end,
            TZID tzid,
            boolean absent
        ) {
            super();

            Timezone tz = Timezone.of(tzid);
            boolean zoneHistory = (tz.getHistory() != null);

            if (absent) { // polar night
                this.startUTC = null;
                this.endUTC = null;
                this.startLocal = null;
                this.endLocal = null;
            } else if (start.isPresent()) {
                this.startUTC = start.get();
                this.startLocal = this.startUTC.toZonalTimestamp(tzid);
                if (end.isPresent()) { // standard use-case
                    this.endUTC = end.get();
                    this.endLocal = this.endUTC.toZonalTimestamp(tzid);
                } else if (zoneHistory) {
                    PlainDate next = date.plus(1, CalendarUnit.DAYS);
                    this.endUTC = next.atFirstMoment(tzid);
                    this.endLocal = next.atStartOfDay(tzid);
                } else {
                    PlainDate next = date.plus(1, CalendarUnit.DAYS);
                    this.endUTC = next.atStartOfDay().in(tz);
                    this.endLocal = this.endUTC.toZonalTimestamp(tzid);
                }
            } else if (end.isPresent()) {
                if (zoneHistory) {
                    this.startUTC = date.atFirstMoment(tzid);
                    this.startLocal = date.atStartOfDay(tzid);
                    this.endUTC = end.get();
                    this.endLocal = this.endUTC.toZonalTimestamp(tzid);
                } else {
                    this.startUTC = date.atStartOfDay().in(tz);
                    this.startLocal = this.startUTC.toZonalTimestamp(tzid);
                    this.endUTC = end.get();
                    this.endLocal = this.endUTC.toZonalTimestamp(tzid);
                }
            } else if (zoneHistory) { // midnight sun
                this.startUTC = date.atFirstMoment(tzid);
                this.startLocal = date.atStartOfDay(tzid);
                PlainDate next = date.plus(1, CalendarUnit.DAYS);
                this.endUTC = next.atFirstMoment(tzid);
                this.endLocal = next.atStartOfDay(tzid);
            } else { // midnight sun
                this.startUTC = date.atStartOfDay().in(tz);
                this.startLocal = this.startUTC.toZonalTimestamp(tzid);
                PlainDate next = date.plus(1, CalendarUnit.DAYS);
                this.endUTC = next.atStartOfDay().in(tz);
                this.endLocal = this.endUTC.toZonalTimestamp(tzid);
            }

        }

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Obtains the moment of sunrise if it exists. </p>
         *
         * <p>Note: If there is no sunrise but the sun is already above the horizon at the start of day
         * then this method yields the start of day. </p>
         *
         * @return  moment of sunrise
         * @throws  IllegalStateException in case of absent sunshine (polar night)
         * @see     #isAbsent()
         */
        /*[deutsch]
         * <p>Liefert den Moment des Sonnenaufgangs wenn vorhanden. </p>
         *
         * <p>Hinweis: Wenn es keinen Sonnenaufgang gibt, aber die Sonne schon &uuml;ber dem Horizont
         * steht, dann liefert diese Methode den Anfang des Kalendertages. </p>
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
         * <p>Note: If there is no sunset but the sun is still above the horizon at the end of day
         * then this method yields the end of day. </p>
         *
         * @return  moment of sunset (exclusive)
         * @throws  IllegalStateException in case of absent sunshine (polar night)
         * @see     #isAbsent()
         */
        /*[deutsch]
         * <p>Liefert den Moment des Sonnenuntergangs wenn vorhanden. </p>
         *
         * <p>Hinweis: Wenn es keinen Sonnenuntergang gibt, aber die Sonne noch &uuml;ber dem Horizont
         * steht, dann liefert diese Methode das Ende des Kalendertages. </p>
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
         * <p>Note: If there is no sunrise but the sun is already above the horizon at the start of day
         * then this method yields the start of day. </p>
         *
         * @return  local timestamp of sunrise
         * @throws  IllegalStateException in case of absent sunshine (polar night)
         * @see     #isAbsent()
         */
        /*[deutsch]
         * <p>Liefert die lokale Zeit des Sonnenaufgangs wenn vorhanden. </p>
         *
         * <p>Hinweis: Wenn es keinen Sonnenaufgang gibt, aber die Sonne schon &uuml;ber dem Horizont
         * steht, dann liefert diese Methode den Anfang des Kalendertages. </p>
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
         * <p>Note: If there is no sunset but the sun is still above the horizon at the end of day
         * then this method yields the end of day. </p>
         *
         * @return  local timestamp of sunset (exclusive)
         * @throws  IllegalStateException in case of absent sunshine (polar night)
         * @see     #isAbsent()
         */
        /*[deutsch]
         * <p>Liefert die lokale Zeit des Sonnenuntergangs wenn vorhanden. </p>
         *
         * <p>Hinweis: Wenn es keinen Sonnenuntergang gibt, aber die Sonne noch &uuml;ber dem Horizont
         * steht, dann liefert diese Methode das Ende des Kalendertages. </p>
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
