/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (LeapSeconds.java) is part of project Time4J.
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

package net.time4j.scale;

import net.time4j.base.GregorianDate;
import net.time4j.base.GregorianMath;
import net.time4j.base.MathUtils;
import net.time4j.base.ResourceLoader;
import net.time4j.base.UnixTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * <p>Holds all leap seconds occurred since the official start of UTC in
 * 1972. </p>
 *
 * <p>The source is either an implementation of the SPI-interface
 * {@code Provider} loaded by a {@code ServiceLoader} or an internal
 * standard implementation of {@code Provider} which accesses the file
 * &quot;leapseconds.data&quot;. This resource file must be in the
 * classpath (in folder data). It has the format of a CSV-ASCII-text
 * which has two columns separated by comma. The first column denotes
 * the calendar day after the leap second shift in ISO-8601-format (for
 * example 1972-07-01). The second column determines the sign of the
 * leap second (+/-). </p>
 *
 * <p>The source will mainly be loaded by the context classloader else
 * by application classloader. If there is no source at all then Time4J
 * assumes that leap seconds shall not be used. </p>
 *
 * <p>The system property &quot;time4j.scale.leapseconds.suppressed&quot;
 * determines if leap seconds shall be active at all. If this system
 * property has the value {@code true} then this class will never
 * register any leap seconds equal if the underlying sources are filled
 * or not. Furthermore, the system property
 * &quot;time4j.scale.leapseconds.final&quot; determines if leap seconds
 * are only registered at system start or if new ones can be lazily
 * registered at runtime using the methods {@code registerXYZ()}.
 * Setting one of both properties can improve the performance. </p>
 *
 * @author      Meno Hochschild
 */
/*[deutsch]
 * <p>Ermittelt alle seit dem offiziellen Start von UTC 1972 aufgetretenen
 * Schaltsekunden. </p>
 *
 * <p>Als Quelle dient entweder eine &uuml;ber einen {@code ServiceLoader}
 * gefundene Implementierung des SPI-Interface {@code Provider} oder
 * bei Nichtvorhandensein eine interne Standard-Implementierung, die auf
 * die Datei &quot;leapseconds.data&quot; zugreift. Diese Datei mu&szlig;
 * im Klassenpfad liegen (im data-Ordner). Sie hat das Format einer
 * CSV-ASCII-Textdatei, worin zwei Spalten mit Komma getrennt vorkommen.
 * Die erste Spalte definiert den Tag nach der Umstellung im ISO-8601-Format
 * als reines Datum ohne Uhrzeitanteil (z.B. 1972-07-01). Die zweite Spalte
 * repr&auml;sentiert das Vorzeichen der Schaltsekunde (+/-). </p>
 *
 * <p>Geladen wird die Quelle bevorzugt &uuml;ber den Kontext-ClassLoader.
 * Wird die Quelle nicht gefunden, so wird angenommen, da&szlig; keine
 * Schaltsekunden verwendet werden sollen. </p>
 *
 * <p>Die System-Property &quot;time4j.scale.leapseconds.suppressed&quot;
 * entscheidet, ob Schaltsekunden &uuml;berhaupt aktiviert sind. Wenn diese
 * System-Property den Wert {@code true} hat, wird diese Klasse niemals
 * Schaltsekunden registrieren, gleichg&uuml;ltig, ob die zugrundeliegenden
 * Quellen gef&uuml;llt sind. Daneben gibt es noch die System-Property
 * &quot;time4j.scale.leapseconds.final&quot;, die festlegt, ob Schaltsekunden
 * nur zum Systemstart registriert werden oder auch nachtr&auml;glich zur
 * Laufzeit mittels {@code registerXYZ()} registriert werden k&ouml;nnen. Das
 * Setzen einer der beiden Properties kann die Performance verbessern. </p>
 *
 * @author      Meno Hochschild
 */
public final class LeapSeconds
    implements Iterable<LeapSecondEvent>, Comparator<LeapSecondEvent> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>System property &quot;net.time4j.scale.leapseconds.suppressed&quot;
     * which determines that no leap seconds shall be loaded and used. </p>
     *
     * <p>Defined values: &quot;true&quot; (suppressed) or &quot;false&quot;
     * (active - default). </p>
     */
    /*[deutsch]
     * <p>System-Property &quot;net.time4j.scale.leapseconds.suppressed&quot;,
     * die regelt, da&szlig; keine Schaltsekunden geladen werden. </p>
     *
     * <p>Definierte Werte: &quot;true&quot; (unterdr&uuml;ckt) oder
     & quot;false&quot; (aktiv - Standard). </p>
     */
    public static final boolean SUPPRESS_UTC_LEAPSECONDS =
        Boolean.getBoolean("net.time4j.scale.leapseconds.suppressed");

    /**
     * <p>System property &quot;net.time4j.scale.leapseconds.final&quot;
     * which determines that leap seconds can be loaded only one time at
     * system start. </p>
     *
     * <p>Defined values: &quot;true&quot; (final) or &quot;false&quot;
     * (enables lazy regisration - default). </p>
     */
    /*[deutsch]
     * <p>System-Property &quot;net.time4j.scale.leapseconds.final&quot;, die
     * regelt, da&szlig; Schaltsekunden nur einmalig zum Systemstart festgelegt
     * werden k&ouml;nnen. </p>
     *
     * <p>Definierte Werte: &quot;true&quot; (final) oder &quot;false&quot;
     * (nachtr&auml;gliche Registrierung m&oumL;glich - Standard). </p>
     */
    public static final boolean FINAL_UTC_LEAPSECONDS =
        Boolean.getBoolean("net.time4j.scale.leapseconds.final");

    /**
     * <p>System property &quot;net.time4j.scale.leapseconds.path&quot;
     * which determines the path of the leap second file. </p>
     *
     * <p>Setting this property will usually suppress other leap second
     * providers even if they have registered more leap seconds. The path
     * is an URL which must be understood by
     * {@link ClassLoader#getResourceAsStream(String)}. The default
     * value is: &quot;data/leapseconds.data&quot; (relative to
     * class path). </p>
     *
     * @since   2.1.2
     */
    /*[deutsch]
     * <p>System-Property &quot;net.time4j.scale.leapseconds.path&quot;, die
     * den Pfad der Schaltsekundendatei festlegt. </p>
     *
     * <p>Das Setzen dieser Property wird gew&ouml;hnlich andere
     * {@code LeapSecondProvider} ausschalten, selbst wenn sie mehr
     * Schaltsekunden registriert haben. Der Pfad ist ein URL, der von
     * {@link ClassLoader#getResourceAsStream(String)} verstanden werden
     * mu&szlig;. Standardwert ist: &quot;data/leapseconds.data&quot;
     * (relativ zum Klassenpfad). </p>
     *
     * @since   2.1.2
     */
    public static final String PATH_TO_LEAPSECONDS =
        System.getProperty(
            "net.time4j.scale.leapseconds.path",
            "data/leapseconds.data");

    private static final ExtendedLSE[] EMPTY_ARRAY = new ExtendedLSE[0];
    private static final LeapSeconds INSTANCE = new LeapSeconds();
    private static final long UNIX_OFFSET = 2 * 365 * 86400;
    private static final long MJD_OFFSET = 40587;

    //~ Instanzvariablen --------------------------------------------------

    private final LeapSecondProvider provider;
    private final List<ExtendedLSE> list;
    private final ExtendedLSE[] reverseFinal;
    private volatile ExtendedLSE[] reverseVolatile;
    private final boolean supportsNegativeLS;

    //~ Konstruktoren -----------------------------------------------------

    private LeapSeconds() {
        super();

        LeapSecondProvider loaded = null;
        int leapCount = 0;

        if (!SUPPRESS_UTC_LEAPSECONDS) {
            // Provider mit den meisten Schaltsekunden wählen
            for (LeapSecondProvider temp : ResourceLoader.getInstance().services(LeapSecondProvider.class)) {
                int currentCount = temp.getLeapSecondTable().size();

                if (currentCount > leapCount) {
                    loaded = temp;
                    leapCount = currentCount;
                }
            }
        }

        if (
            (loaded == null)
            || (leapCount == 0)
        ) {
            this.provider = null;
            this.list = Collections.emptyList();
            this.reverseFinal = EMPTY_ARRAY;
            this.reverseVolatile = EMPTY_ARRAY;
            this.supportsNegativeLS = false;
        } else {
            SortedSet<ExtendedLSE> sortedLS = new TreeSet<ExtendedLSE>(this);

            for (
                Map.Entry<GregorianDate, Integer> entry
                : loaded.getLeapSecondTable().entrySet()
            ) {
                GregorianDate date = entry.getKey();
                long unixTime = toPosix(date);

                sortedLS.add(
                    new SimpleLeapSecondEvent(
                        date,
                        Long.MIN_VALUE,
                        unixTime + (1 - 2 * 365) * 86400 - 1,
                        entry.getValue().intValue()
                    )
                );
            }

            extend(sortedLS);

            if (FINAL_UTC_LEAPSECONDS) {
                this.list =
                    Collections.unmodifiableList(
                        new ArrayList<ExtendedLSE>(sortedLS));
            } else {
                this.list = new CopyOnWriteArrayList<ExtendedLSE>(sortedLS);
            }

            this.reverseFinal = this.initReverse();
            this.reverseVolatile = this.reverseFinal;
            this.provider = loaded;

            if (FINAL_UTC_LEAPSECONDS) {
                boolean snls = loaded.supportsNegativeLS();
                if (snls) {
                    boolean hasNegativeLS = false;
                    for (ExtendedLSE event : this.list) {
                        if (event.getShift() < 0) {
                            hasNegativeLS = true;
                            break;
                        }
                    }
                    snls = hasNegativeLS;
                }
                this.supportsNegativeLS = snls;
            } else {
                this.supportsNegativeLS = true;
            }
        }

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Returns the singleton instance. </p>
     *
     * @return  singleton instance
     */
    /*[deutsch]
     * <p>Liefert die Singleton-Instanz. </p>
     *
     * @return  singleton instance
     */
    public static LeapSeconds getInstance() {

        return INSTANCE;

    }

    /**
     * <p>Queries if the leap second support is activated. </p>
     *
     * @return  {@code true} if leap seconds are supported and are also
     *          registered else {@code false}
     * @see     #SUPPRESS_UTC_LEAPSECONDS
     */
    /*[deutsch]
     * <p>Ist die Schaltsekundenunterst&uuml;tzung aktiviert? </p>
     *
     * @return  {@code true} if leap seconds are supported and are also
     *          registered else {@code false}
     * @see     #SUPPRESS_UTC_LEAPSECONDS
     */
    public boolean isEnabled() {

        return !this.list.isEmpty();

    }

    /**
     * <p>Queries if a lazy registration of leap seconds is possible. </p>
     *
     * <p>If the leap second support is switched off then a registration of
     * leap seconds is never possible so this method will be ignored. </p>
     *
     * @return  {@code true} if the method {@code registerXYZ()} can be
     *          called without exception else {@code false}
     * @see     #registerPositiveLS(int, int, int)
     * @see     #registerNegativeLS(int, int, int)
     * @see     #FINAL_UTC_LEAPSECONDS
     * @see     #isEnabled()
     */
    /*[deutsch]
     * <p>K&ouml;nnen nachtr&auml;glich UTC-Schaltsekunden registriert
     * werden? </p>
     *
     * <p>Ist die Schaltsekundenunterst&uuml;tzung abgeschaltet, dann ist
     * eine Registrierung niemals m&ouml;glich, und diese Methode wird dann
     * de facto ignoriert. </p>
     *
     * @return  {@code true} if the method {@code registerXYZ()} can be
     *          called without exception else {@code false}
     * @see     #registerPositiveLS(int, int, int)
     * @see     #registerNegativeLS(int, int, int)
     * @see     #FINAL_UTC_LEAPSECONDS
     * @see     #isEnabled()
     */
    public boolean isExtensible() {

        return (!FINAL_UTC_LEAPSECONDS && this.isEnabled());

    }

    /**
     * <p>Yields the count of all registered leap seconds. </p>
     *
     * @return  count of registered leap seconds
     */
    /*[deutsch]
     * <p>Ermittelt die Anzahl aller registrierten Schaltsekunden. </p>
     *
     * @return  count of registered leap seconds
     */
    public int getCount() {

        return this.getEventsInDescendingOrder().length;

    }

    /**
     * <p>Yields the count of all registered leap seconds which happened before given timestamp. </p>
     *
     * @param   until   the upper search limit (exclusive)
     * @return  count of registered leap seconds happening before
     * @see     #getCount()
     * @since   3.28/4.24
     */
    /*[deutsch]
     * <p>Ermittelt die Anzahl aller registrierten Schaltsekunden, die vor dem angegebenen Zeitstempel
     * existieren. </p>
     *
     * @param   until   the upper search limit (exclusive)
     * @return  count of registered leap seconds happening before
     * @see     #getCount()
     * @since   3.28/4.24
     */
    public int getCount(UnixTime until) {

        long ut = until.getPosixTime();
        return MathUtils.safeCast(this.enhance(ut) + UNIX_OFFSET - ut);

    }

    /**
     * <p>Registers a new positive leap second by defining the
     * switch-over-day. </p>
     *
     * @param   year        proleptic iso year
     * @param   month       gregorian month in range (1-12)
     * @param   dayOfMonth  day of month in range (1-31)
     * @throws  IllegalStateException if support of leap seconds is switched
     *          off by configuration or if the value of system property
     *          &quot;net.time4j.utc.leapseconds.final&quot; is {@code true}
     * @throws  IllegalArgumentException if the new event is not after the
     *          last stored event or if the date is invalid
     * @see     #isExtensible()
     * @see     #isEnabled()
     * @see     #SUPPRESS_UTC_LEAPSECONDS
     * @see     #FINAL_UTC_LEAPSECONDS
     */
    /*[deutsch]
     * <p>Registriert eine neue positive Schaltsekunde, indem als Datum
     * der Tag der Umstellung definiert wird. </p>
     *
     * @param   year        proleptic iso year
     * @param   month       gregorian month in range (1-12)
     * @param   dayOfMonth  day of month in range (1-31)
     * @throws  IllegalStateException if support of leap seconds is switched
     *          off by configuration or if the value of system property
     *          &quot;net.time4j.utc.leapseconds.final&quot; is {@code true}
     * @throws  IllegalArgumentException if the new event is not after the
     *          last stored event or if the date is invalid
     * @see     #isExtensible()
     * @see     #isEnabled()
     * @see     #SUPPRESS_UTC_LEAPSECONDS
     * @see     #FINAL_UTC_LEAPSECONDS
     */
    public void registerPositiveLS(
        int year,
        int month,
        int dayOfMonth
    ) {

        this.register(year, month, dayOfMonth, false);

    }

    /**
     * <p>Registers a new negative leap second by defining the
     * switch-over-day. </p>
     *
     * @param   year        proleptic iso year
     * @param   month       gregorian month in range (1-12)
     * @param   dayOfMonth  day of month in range (1-31)
     * @throws  IllegalStateException if support of leap seconds is switched
     *          off by configuration or if the value of system property
     *          &quot;net.time4j.utc.leapseconds.final&quot; is {@code true}
     * @throws  IllegalArgumentException if the new event is not after the
     *          last stored event or if the date is invalid
     * @see     #isExtensible()
     * @see     #isEnabled()
     * @see     #SUPPRESS_UTC_LEAPSECONDS
     * @see     #FINAL_UTC_LEAPSECONDS
     */
    /*[deutsch]
     * <p>Registriert eine neue negative Schaltsekunde, indem als Datum
     * der Tag der Umstellung definiert wird. </p>
     *
     * @param   year        proleptic iso year
     * @param   month       gregorian month in range (1-12)
     * @param   dayOfMonth  day of month in range (1-31)
     * @throws  IllegalStateException if support of leap seconds is switched
     *          off by configuration or if the value of system property
     *          &quot;net.time4j.utc.leapseconds.final&quot; is {@code true}
     * @throws  IllegalArgumentException if the new event is not after the
     *          last stored event or if the date is invalid
     * @see     #isExtensible()
     * @see     #isEnabled()
     * @see     #SUPPRESS_UTC_LEAPSECONDS
     * @see     #FINAL_UTC_LEAPSECONDS
     */
    public void registerNegativeLS(
        int year,
        int month,
        int dayOfMonth
    ) {

        this.register(year, month, dayOfMonth, true);

    }

    /**
     * <p>Queries if negative leap seconds are supported. </p>
     *
     * @return  {@code true} if negative leap seconds are supported
     *          else {@code false}
     * @see     LeapSecondProvider#supportsNegativeLS()
     */
    /*[deutsch]
     * <p>Werden auch negative Schaltsekunden unterst&uuml;tzt? </p>
     *
     * @return  {@code true} if negative leap seconds are supported
     *          else {@code false}
     * @see     LeapSecondProvider#supportsNegativeLS()
     */
    public boolean supportsNegativeLS() {

        return this.supportsNegativeLS;

    }

    /**
     * <p>Iterates over all leap second events in descending temporal
     * order. </p>
     *
     * @return  {@code Iterator} over all stored leap second events
     *          which enables for-each-support
     */
    /*[deutsch]
     * <p>Iteriert &uuml;ber alle Schaltsekundenereignisse in zeitlich
     * absteigender Reihenfolge. </p>
     *
     * @return  {@code Iterator} over all stored leap second events
     *          which enables for-each-support
     */
    @Override
    public Iterator<LeapSecondEvent> iterator() {

        final LeapSecondEvent[] events = this.getEventsInDescendingOrder();
        return Collections.unmodifiableList(Arrays.asList(events)).iterator();

    }

    /**
     * <p>Yields the shift in seconds suitable for the last minute
     * of given calendar date. </p>
     *
     * <p>The result of this method can be added to the second value
     * {@code 59} in order to calculate the maximum of the element
     * SECOND_OF_MINUTE in given time context. The behaviour of the
     * method is undefined if given calendar date is undefined. </p>
     *
     * @param   date    day of possible leap second event in the last minute
     * @return  shift of second element (most of the times just {@code 0})
     */
    /*[deutsch]
     * <p>Ermittelt die Verschiebung in Sekunden passend zur letzten Minute
     * des angegebenen Datums. </p>
     *
     * <p>Das Ergebnis der Methode kann zum Sekundenwert {@code 59} addiert
     * werden, um das Maximum des Elements SECOND_OF_MINUTE im angegebenen
     * Zeitkontext zu erhalten. Das Verhalten der Methode ist undefiniert,
     * wenn die angegebenen Bereichsgrenzen der Argumentwerte nicht beachtet
     * werden. </p>
     *
     * @param   date    day of possible leap second event in the last minute
     * @return  shift of second element (most of the times just {@code 0})
     */
    public int getShift(GregorianDate date) {

        int year = date.getYear();

        // Schaltsekundenereignisse gibt es erst seit Juni 1972
        if (year >= 1972) {

            ExtendedLSE[] events = this.getEventsInDescendingOrder();

            for (int i = 0; i < events.length; i++) {
                ExtendedLSE event = events[i];
                GregorianDate lsDate = event.getDate();

                // Ist es der Umstellungstag?
                if (
                    (year == lsDate.getYear())
                    && (date.getMonth() == lsDate.getMonth())
                    && (date.getDayOfMonth() == lsDate.getDayOfMonth())
                ) {
                    return event.getShift();
                }
            }
        }

        return 0;

    }

    /**
     * <p>Yields the shift in seconds dependent on if given UTC time point
     * represents a leap second or not. </p>
     *
     * @param   utc     elapsed SI-seconds relative to UTC epoch
     *                  [1972-01-01T00:00:00Z] including leap seconds
     * @return  {@code 1, 0, -1} if the argument denotes a positive leap second,
                no leap second or a negative leap second
     */
    /*[deutsch]
     * <p>Ermittelt die Verschiebung in Sekunden, wenn dieser Zeitpunkt
     * &uuml;berhaupt eine Schaltsekunde repr&auml;sentiert. </p>
     *
     * @param   utc     elapsed SI-seconds relative to UTC epoch
     *                  [1972-01-01T00:00:00Z] including leap seconds
     * @return  {@code 1, 0, -1} if the argument denotes a positive leap second,
                no leap second or a negative leap second
     */
    public int getShift(long utc) {

        if (utc <= 0) {
            return 0;
        }

        ExtendedLSE[] events = this.getEventsInDescendingOrder();

        for (int i = 0; i < events.length; i++) {
            ExtendedLSE lse = events[i];

            if (utc > lse.utc()) {
                return 0;
            } else {
                long start = lse.utc() - lse.getShift();
                if (utc > start) { // Schaltbereich
                    return (int) (utc - start);
                }
            }
        }

        return 0;

    }

    /**
     * <p>Yields the next leap second event after given UTC time point. </p>
     *
     * @param   utc     elapsed SI-seconds relative to UTC epoch
     *                  [1972-01-01T00:00:00Z] including leap seconds
     * @return  following leap second event or {@code null} if not known
     * @since   2.1
     */
    /*[deutsch]
     * <p>Ermittelt das zum angegebenen UTC-Zeitstempel n&auml;chste
     * Schaltsekundenereignis. </p>
     *
     * @param   utc     elapsed SI-seconds relative to UTC epoch
     *                  [1972-01-01T00:00:00Z] including leap seconds
     * @return  following leap second event or {@code null} if not known
     * @since   2.1
     */
    public LeapSecondEvent getNextEvent(long utc) {

        ExtendedLSE[] events = this.getEventsInDescendingOrder();
        LeapSecondEvent result = null;

        for (int i = 0; i < events.length; i++) {
            ExtendedLSE lse = events[i];

            if (utc >= lse.utc()) {
                break;
            } else {
                result = lse;
            }
        }

        return result;

    }

    /**
     * <p>Enhances an UNIX-timestamp with leap seconds and converts it to an
     * UTC-timestamp. </p>
     *
     * <p>Note: A leap second itself cannot be restored because the mapping
     * between UNIX- and UTC-time is not bijective. Hence the result of this
     * method can not represent a leap second. </p>
     *
     * @param   unixTime    elapsed time in seconds relative to UNIX epoch
     *                      [1970-01-01T00:00:00Z] without leap seconds
     * @return  elapsed SI-seconds relative to UTC epoch
     *          [1972-01-01T00:00:00Z] including leap seconds
     * @see     #strip(long)
     */
    /*[deutsch]
     * <p>Reichert einen UNIX-Zeitstempel mit Schaltsekunden an und wandelt
     * ihn in einen UTC-Zeitstempel um. </p>
     *
     * <p>Notiz: Eine Schaltsekunde kann selbst nicht wiederhergestellt werden,
     * da die Abbildung zwischen der UNIX- und UTC-Zeit nicht bijektiv ist.
     * Das Ergebnis dieser Methode stellt also keine aktuelle Schaltsekunde
     * dar. </p>
     *
     * @param   unixTime    elapsed time in seconds relative to UNIX epoch
     *                      [1970-01-01T00:00:00Z] without leap seconds
     * @return  elapsed SI-seconds relative to UTC epoch
     *          [1972-01-01T00:00:00Z] including leap seconds
     * @see     #strip(long)
     */
    public long enhance(long unixTime) {

        long epochTime = unixTime - UNIX_OFFSET;

        if (unixTime <= 0) {
            return epochTime;
        }

        // Lineare Suche hier besser als binäre Suche, weil in der
        // Praxis meistens mit aktuellen Datumswerten gesucht wird
        final ExtendedLSE[] events = this.getEventsInDescendingOrder();

        for (int i = 0; i < events.length; i++) {
            ExtendedLSE lse = events[i];

            if (lse.raw() < epochTime) {
                return MathUtils.safeAdd(epochTime, lse.utc() - lse.raw());
            }
        }

        return epochTime;

    }

    /**
     * <p>Converts given UTC-timestamp to an UNIX-timestamp without
     * leap seconds. </p>
     *
     * <p>This method is the reversal of {@code enhance()}. Note that
     * there is no bijective mapping, that is sometimes the expression
     * {@code enhance(strip(val)) != val} is {@code true}. </p>
     *
     * @param   utc     elapsed SI-seconds relative to UTC epoch
     *                  [1972-01-01T00:00:00Z] including leap seconds
     * @return  elapsed time in seconds relative to UNIX epoch
     *          [1970-01-01T00:00:00Z] without leap seconds
     * @see     #enhance(long)
     */
    /*[deutsch]
     * <p>Konvertiert die UTC-Angabe zu einem UNIX-Zeitstempel ohne
     * Schaltsekunden. </p>
     *
     * <p>Diese Methode ist die Umkehrung zu {@code enhance()}. Zu
     * beachten ist, da&szlig; keine bijektive Abbildung besteht, d.h. es gilt
     * manchmal: {@code enhance(strip(val)) != val}. </p>
     *
     * @param   utc     elapsed SI-seconds relative to UTC epoch
     *                  [1972-01-01T00:00:00Z] including leap seconds
     * @return  elapsed time in seconds relative to UNIX epoch
     *          [1970-01-01T00:00:00Z] without leap seconds
     * @see     #enhance(long)
     */
    public long strip(long utc) {

        if (utc <= 0) {
            return utc + UNIX_OFFSET;
        }

        // Lineare Suche hier besser als binäre Suche, weil in der
        // Praxis meistens mit aktuellen Datumswerten gesucht wird
        final ExtendedLSE[] events = this.getEventsInDescendingOrder();
        boolean snls = this.supportsNegativeLS;

        for (int i = 0; i < events.length; i++) {
            ExtendedLSE lse = events[i];

            if (
                (lse.utc() - lse.getShift() < utc)
                || (snls && (lse.getShift() < 0) && (lse.utc() < utc))
            ) {
                utc = MathUtils.safeAdd(utc, lse.raw() - lse.utc());
                break;
            }
        }

        return utc + UNIX_OFFSET;

    }

    /**
     * <p>Queries if given UTC-timestamp represents a registered
     * positive leap second. </p>
     *
     * @param   utc     elapsed SI-seconds relative to UTC epoch
     *                  [1972-01-01T00:00:00Z] including leap seconds
     * @return  {@code true} if the argument represents a registered
     *          positive leap second else {@code false}
     */
    /*[deutsch]
     * <p>Ist die angegebene UTC-Zeit eine registrierte positive
     * Schaltsekunde? </p>
     *
     * @param   utc     elapsed SI-seconds relative to UTC epoch
     *                  [1972-01-01T00:00:00Z] including leap seconds
     * @return  {@code true} if the argument represents a registered
     *          positive leap second else {@code false}
     */
    public boolean isPositiveLS(long utc) {

        if (utc <= 0) {
            return false;
        }

        final ExtendedLSE[] events = this.getEventsInDescendingOrder();

        for (int i = 0; i < events.length; i++) {
            long comp = events[i].utc();

            if (comp == utc) {
                return (events[i].getShift() == 1);
            } else if (comp < utc) {
                break;
            }
        }

        return false;

    }

    /**
     * <p>Determines the expiration date of underlying data. </p>
     *
     * @return  immutable date of expiration
     * @since   2.3
     * @throws  IllegalStateException if leap seconds are not activated
     */
    /*[deutsch]
     * <p>Bestimmt das Verfallsdatum der zugrundeliegenden Daten. </p>
     *
     * @return  immutable date of expiration
     * @since   2.3
     * @throws  IllegalStateException if leap seconds are not activated
     */
    public GregorianDate getDateOfExpiration() {

        if (!this.isEnabled()) {
            throw new IllegalStateException("Leap seconds not activated.");
        }

        return this.provider.getDateOfExpiration();

    }

    /**
     * <p>Compares two leap second events by their date in ascending order. </p>
     *
     * @param   o1  first leap second event
     * @param   o2  second leap second event
     * @return  {@code -1}, {@code 0} or {@code 1} if first event is before,
     *          equal to or later than second event
     * @since   2.3
     */
    /*[deutsch]
     * <p>Vergleicht zwei Schaltsekundenereignisse nach ihrem Datum in
     * aufsteigender Reihenfolge. </p>
     * 
     * @param   o1  first leap second event
     * @param   o2  second leap second event
     * @return  {@code -1}, {@code 0} or {@code 1} if first event is before,
     *          equal to or later than second event
     * @since   2.3
     */
    @Override
    public int compare(
        LeapSecondEvent o1,
        LeapSecondEvent o2
    ) {

        GregorianDate d1 = o1.getDate();
        GregorianDate d2 = o2.getDate();

        int y1 = d1.getYear();
        int y2 = d2.getYear();

        if (y1 < y2) {
            return -1;
        } else if (y1 > y2) {
            return 1;
        }

        int m1 = d1.getMonth();
        int m2 = d2.getMonth();

        if (m1 < m2) {
            return -1;
        } else if (m1 > m2) {
            return 1;
        }

        int dom1 = d1.getDayOfMonth();
        int dom2 = d2.getDayOfMonth();

        return (dom1 < dom2 ? -1 : (dom1 == dom2 ? 0 : 1));

    }

    /**
     * <p>For debugging purposes. </p>
     *
     * @return  table of leap seconds as String
     */
    /*[deutsch]
     * <p>F&uuml;r Debugging-Zwecke. </p>
     *
     * @return  table of leap seconds as String
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(2048);
        sb.append("[PROVIDER=");
        sb.append(this.provider);
        if (this.provider != null) {
            sb.append(",EXPIRES=");
            sb.append(format(this.getDateOfExpiration()));
        }
        sb.append(",EVENTS=[");

        if (this.isEnabled()) {
            boolean first = true;
            for (Object event : this.list) {
                if (first) {
                    first = false;
                } else {
                    sb.append('|');
                }
                sb.append(event);
            }
        } else {
            sb.append("NOT SUPPORTED");
        }

        return sb.append("]]").toString();

    }

    private void register(
        int year,
        int month,
        int dayOfMonth,
        boolean negativeLS
    ) {

        if (FINAL_UTC_LEAPSECONDS) {
            throw new IllegalStateException(
                "Leap seconds are final, "
                + "change requires edit of system property "
                + "\"time4j.utc.leapseconds.final\" "
                + "and reboot of JVM.");
        } else if (SUPPRESS_UTC_LEAPSECONDS) {
            throw new IllegalStateException(
                "Leap seconds are not supported, "
                + "change requires edit of system property "
                + "\"time4j.utc.leapseconds.suppressed\" "
                + "and reboot of JVM.");
        }

        synchronized (this) {
            GregorianMath.checkDate(year, month, dayOfMonth);

            if (!this.isEnabled()) {
                throw new IllegalStateException("Leap seconds not activated.");
            }

            ExtendedLSE last = this.reverseVolatile[0];
            GregorianDate date = last.getDate();
            boolean ok = false;

            if (year > date.getYear()) {
                ok = true;
            } else if (year == date.getYear()) {
                if (month > date.getMonth()) {
                    ok = true;
                } else if (month == date.getMonth()) {
                    if (dayOfMonth > date.getDayOfMonth()) {
                        ok = true;
                    }
                }
            }

            if (!ok) {
                throw new IllegalArgumentException(
                    "New leap second must be after last leap second.");
            }

            int shift = (negativeLS ? -1 : 1);
            GregorianDate newDate =
                this.provider.getDateOfEvent(year, month, dayOfMonth);
            this.list.add(createLSE(newDate, shift, last));
            this.reverseVolatile = this.initReverse();
        }

    }

    // Ereignisse in zeitlich absteigender Reihenfolge auf (das neueste zuerst)
    private ExtendedLSE[] getEventsInDescendingOrder() {

        if (SUPPRESS_UTC_LEAPSECONDS || FINAL_UTC_LEAPSECONDS) {
            return this.reverseFinal;
        } else {
            return this.reverseVolatile;
        }

    }

    private static void extend(SortedSet<ExtendedLSE> sortedColl) {

        List<ExtendedLSE> tmp = new ArrayList<ExtendedLSE>(sortedColl.size());
        int diff = 0;

        for (ExtendedLSE lse : sortedColl) {
            if (lse.utc() == Long.MIN_VALUE) {
                diff += lse.getShift();
                tmp.add(new SimpleLeapSecondEvent(lse, diff));
            } else {
                tmp.add(lse);
            }
        }

        sortedColl.clear();
        sortedColl.addAll(tmp);

    }

    private static ExtendedLSE createLSE(
        final GregorianDate date,
        final int shift,
        ExtendedLSE last
    ) {

        long raw = toPosix(date) + (1 - 2 * 365) * 86400 - 1;
        int diff = (int) (last.utc() - last.raw() + shift);

        return new SimpleLeapSecondEvent(
            date,
            raw + diff,
            raw,
            shift);

    }

    private static long toPosix(GregorianDate date) {

        return MathUtils.safeMultiply(
            MathUtils.safeSubtract(
                GregorianMath.toMJD(date),
                MJD_OFFSET
            ),
            86400
        );

    }

    private ExtendedLSE[] initReverse() {

        List<ExtendedLSE> tmp =
            new ArrayList<ExtendedLSE>(this.list.size());
        tmp.addAll(this.list);
        Collections.reverse(tmp);
        return tmp.toArray(new ExtendedLSE[tmp.size()]);

    }

    private static String format(GregorianDate date) {

        return String.format(
            "%1$04d-%2$02d-%3$02d",
            date.getYear(),
            date.getMonth(),
            date.getDayOfMonth());

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class SimpleLeapSecondEvent
        implements ExtendedLSE, Serializable {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final long serialVersionUID = 5986185471610524587L;

        //~ Instanzvariablen ----------------------------------------------

        /**
         * @serial  date of leap second event
         */
        private final GregorianDate date;

        /**
         * @serial  shift in seconds
         */
        private final int shift;

        /**
         * @serial  UTC time including leap seconds
         */
        private final long _utc;

        /**
         * @serial  UTC time without leap seconds
         */
        private final long _raw;

        //~ Konstruktoren -------------------------------------------------

        SimpleLeapSecondEvent(
            GregorianDate date,
            long utcTime,
            long rawTime,
            int shift
        ) {
            super();

            this.date = date;
            this.shift = shift;
            this._utc = utcTime;
            this._raw = rawTime;

        }

        // Anreicherung mit der UTC-Zeit
        SimpleLeapSecondEvent(
            ExtendedLSE lse,
            int diff
        ) {
            super();

            this.date = lse.getDate();
            this.shift = lse.getShift();

            this._utc = lse.raw() + diff;
            this._raw = lse.raw();

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public GregorianDate getDate() {
            return this.date;
        }

        @Override
        public int getShift() {
            return this.shift;
        }

        @Override
        public long utc() {
            return this._utc;
        }

        @Override
        public long raw() {
            return this._raw;
        }

        @Override
        public String toString() {

            StringBuilder sb = new StringBuilder(128);
            sb.append(LeapSecondEvent.class.getName());
            sb.append('[');
            sb.append(format(this.date));
            sb.append(": utc=");
            sb.append(this._utc);
            sb.append(", raw=");
            sb.append(this._raw);
            sb.append(" (shift=");
            sb.append(this.shift);
            sb.append(")]");
            return sb.toString();

        }

    }

}
