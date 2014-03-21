/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (LeapSeconds.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Time4J. If not, see <http://www.gnu.org/licenses/>.
 * -----------------------------------------------------------------------
 */

package net.time4j.scale;

import net.time4j.base.GregorianDate;
import net.time4j.base.GregorianMath;
import net.time4j.base.MathUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;


/**
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
 * Laufzeit mittels {@code register()} registriert werden k&ouml;nnen. Das
 * Setzen einer der beiden Properties kann die Performance verbessern. </p>
 *
 * @author      Meno Hochschild
 * @concurrency <threadsafe>
 */
public final class LeapSeconds
    implements Iterable<LeapSecondEvent> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>System-Property &quot;net.time4j.scale.leapseconds.suppressed&quot;,
     * die regelt, da&szlig; keine Schaltsekunden geladen werden. </p>
     *
     * <p>Defined values: &quot;true&quot; or &quot;false&quot; </p>
     */
    public static final boolean SUPPRESS_UTC_LEAPSECONDS =
        Boolean.getBoolean("net.time4j.scale.leapseconds.suppressed");

    /**
     * <p>System-Property &quot;net.time4j.scale.leapseconds.final&quot;, die
     * regelt, da&szlig; Schaltsekunden nur einmalig zum Systemstart festgelegt
     * werden k&ouml;nnen. </p>
     *
     * <p>Defined values: &quot;true&quot; or &quot;false&quot; </p>
     */
    public static final boolean FINAL_UTC_LEAPSECONDS =
        Boolean.getBoolean("net.time4j.scale.leapseconds.final");

    private static final ExtendedLSE[] EMPTY_ARRAY = new ExtendedLSE[0];
    private static final LeapSeconds INSTANCE = new LeapSeconds();
    private static final long UNIX_OFFSET = 2 * 365 * 86400;
    private static final long MJD_OFFSET = 40587;

    //~ Instanzvariablen --------------------------------------------------

    private final String provider;
    private final List<ExtendedLSE> list;
    private final ExtendedLSE[] reverseFinal;
    private volatile ExtendedLSE[] reverseVolatile;
    private final boolean supportsNegativeLS;

    //~ Konstruktoren -----------------------------------------------------

    private LeapSeconds() {
        super();

        if (SUPPRESS_UTC_LEAPSECONDS) {

            this.provider = "<none>";
            this.list = Collections.emptyList();
            this.reverseFinal = EMPTY_ARRAY;
            this.reverseVolatile = EMPTY_ARRAY;
            this.supportsNegativeLS = false;

        } else {

            ServiceLoader<Provider> sl =
                ServiceLoader.load(Provider.class);

            Provider loaded = null;
            int leapCount = 0;

            // Provider mit den meisten Schaltsekunden wählen
            for (Provider temp : sl) {
                int currentCount = temp.getLeapSecondTable().size();

                if (currentCount > leapCount) {
                    loaded = temp;
                    leapCount = currentCount;
                }
            }

            if (loaded == null) {
                loaded = new DefaultLeapSecondService(); // leapseconds.data
            }

            SortedSet<ExtendedLSE> sortedLS =
                new TreeSet<ExtendedLSE>(new EventComparator());

            for (
                Map.Entry<GregorianDate, Integer> entry
                : loaded.getLeapSecondTable().entrySet()
            ) {
                GregorianDate date = entry.getKey();
                long unixTime = toPosix(date);

                sortedLS.add(
                    new SimpleLeapSecondEvent(
                        date,
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
            this.provider = loaded.toString();

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
     * <p>Liefert die Singleton-Instanz. </p>
     *
     * @return  singleton instance
     */
    public static LeapSeconds getInstance() {

        return INSTANCE;

    }

    /**
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
     * <p>K&ouml;nnen nachtr&auml;glich UTC-Schaltsekunden registriert
     * werden? </p>
     *
     * <p>Ist die Schaltsekundenunterst&uuml;tzung abgeschaltet, dann ist
     * eine Registrierung niemals m&ouml;glich, und diese Methode wird dann
     * de facto ignoriert. </p>
     *
     * @return  {@code true} if the method {@code register()} can be called
     *          without exception else {@code false}
     * @see     #register(int, int, int, boolean)
     * @see     #FINAL_UTC_LEAPSECONDS
     * @see     #isEnabled()
     */
    public boolean isExtensible() {

        return (!FINAL_UTC_LEAPSECONDS && this.isEnabled());

    }

    /**
     * <p>Ermittelt die Anzahl aller registrierten Schaltsekunden. </p>
     *
     * @return  count of registered leap seconds
     */
    public int getCount() {

        return this.getEventsInDescendingOrder().length;

    }

    /**
     * <p>Registriert eine neue Schaltsekunde, indem als Datum der Tag
     * der Umstellung und die absolute Verschiebung definiert werden. </p>
     *
     * @param   year        proleptic iso year
     * @param   month       gregorian month in range (1-12)
     * @param   dayOfMonth  day of month in range (1-31)
     * @param   negativeLS  Is the leap second negative ({@code true}) or
     *                      positive ({@code false})?
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
    public void register(
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
            this.list.add(createLSE(date, shift, last));
            this.reverseVolatile = this.initReverse();
        }

    }

    /**
     * <p>Werden auch negative Schaltsekunden unterst&uuml;tzt? </p>
     *
     * @return  {@code true} if negative leap seconds are supported
     *          else {@code false}
     * @see     Provider#supportsNegativeLS()
     */
    public boolean supportsNegativeLS() {

        return this.supportsNegativeLS;

    }

    /**
     * <p>Iteriert &uuml;ber alle Schaltsekundenereignisse in zeitlich
     * absteigender Reihenfolge. </p>
     *
     * @return  {@code Iterator} over all stored leap second events
     *          which enables for-each-support
     */
    @Override
    public Iterator<LeapSecondEvent> iterator() {

        final LeapSecondEvent[] events = this.getEventsInDescendingOrder();

        return new Iterator<LeapSecondEvent>() {
            private int index = 0;
            @Override
            public boolean hasNext() {
                return (this.index < events.length);
            }
            @Override
            public LeapSecondEvent next() {
                if (this.index >= events.length) {
                    throw new NoSuchElementException();
                }
                LeapSecondEvent event = events[this.index];
                this.index++;
                return event;
            }
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };

    }

    /**
     * <p>Ermittelt die Verschiebung in Sekunden passend zur letzten Minute
     * des angegebenen Datums. </p>
     *
     * <p>Das Ergebnis der Methode kann zum Sekundenwert {@code 59} addiert
     * werden, um das Maximum eines Sekundenfelds im angegebenen Zeitkontext
     * zu erhalten. Das Verhalten der Methode ist undefiniert, wenn die
     * angegebenen Bereichsgrenzen der Argumentwerte nicht beachtet werden. </p>
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
     * <p>Ermittelt die zum angegebenen Zeitpunkt neuesten eingef&uuml;gten
     * Schaltsekunden, wenn dieser Zeitpunkt &uuml;berhaupt eine Schaltsekunde
     * repr&auml;sentiert. </p>
     *
     * <p>Historische Schaltsekunden von fr&uuml;heren Jahren werden hierbei
     * nicht mitgez&auml;hlt. Falls jemals Schaltsekundenereignisse definiert
     * sein sollten, bei denen mehr als eine Schaltsekunde eingef&uuml;gt
     * wird, dann wird diese Methode entsprechend den zum Argument passenden
     * Anteil liefern. </p>
     *
     * <p>Folgendes Szenario erhellt den Zweck dieser Methode: Wenn ein
     * UTC-Zeitstempel zun&auml;chst in einen lokalen Wert transformiert wird,
     * gehen alle Schaltsekundeninformationen verloren, auch die letzte, wenn
     * vorhanden. Nach einer Umrechnung in die einzelnen Feldwerte Jahr, Monat
     * usw. ist die berechnete Datumszeit zu gro&szlig;, weil die Schaltsekunde
     * f&auml;lschlicherweise als erste Sekunde der n&auml;chsten Minute
     * angesehen wird. Daher kann diese Methode herangezogen werden, um
     * zu entscheiden, ob ein UTC-Wert aktuell eine Schaltsekunde ist. Wenn
     * ja, ist das Ergebnis dieser Methode von der berechneten Zeit abzuziehen
     * (Subtraktion auf die berechnete Datumszeit angewandt). </p>
     *
     * @param   utc     elapsed SI-seconds relative to UTC epoch
     *                  [1972-01-01T00:00:00Z] including leap seconds
     * @return  shift of second element ({@code 0} if the argument does not
     *          represent a leap second)
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
     * <p>Reichert einen UNIX-Zeitstempel mit Schaltsekunden an und wandelt
     * ihn in einen UTC-Zeitstempel um. </p>
     *
     * <p>Notiz: Eine Schaltsekunde kann selbst nicht wiederhergestellt werden,
     * da die Abbildung zwischen der UNIX-Zeit und UTC nicht bijektiv ist.
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
            return unixTime;
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
            if (events[i].utc() == utc) {
                return (events[i].getShift() == 1);
            } else if (events[i].utc() < utc) {
                break;
            }
        }

        return false;

    }

    /**
     * <p>F&uuml;r Debugging-Zwecke. </p>
     *
     * @return  table of leap seconds as String
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(2048);
        sb.append("[PROVIDER=");
        sb.append(this.provider);
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

        ExtendedLSE lse =
            new ExtendedLSE() {
                @Override
                public GregorianDate getDate() {
                    return date;
                }
                @Override
                public int getShift() {
                    return shift;
                }
                @Override
                public long utc() {
                    return Long.MIN_VALUE;
                }
                @Override
                public long raw() {
                    return toPosix(date) + (1 - 2 * 365) * 86400 - 1;
                }
            };

        int diff = (int) (last.utc() - last.raw() + shift);
        return new SimpleLeapSecondEvent(lse, diff);

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

    //~ Innere Interfaces -------------------------------------------------

    /**
     * <p>Dieses <strong>SPI-Interface</strong> beschreibt, wann
     * UTC-Schaltsekunden eingef&uuml;hrt worden sind. </p>
     *
     * <p>Wird beim Laden der Klasse {@code LeapSeconds} ausgewertet. Wenn
     * eine Implementierung zum Beispiel per eigener Konfiguration keine
     * Schaltsekunden definiert, so wird angenommen, da&szlig; generell
     * keine Schaltsekunden verwendet werden sollen, also die POSIX-Zeit
     * statt UTC. </p>
     *
     * @author  Meno Hochschild
     * @spec    All implementations must have a public no-arg constructor.
     */
    public interface Provider {

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Liefert alle UTC-Schaltsekunden mit Datum und Vorzeichen. </p>
         *
         * <p>Als Schl&uuml;ssel wird der Umstellungstag in der UTC-Zeitzone
         * genommen. Der zugeordnete Wert bezeichnet das Vorzeichen der
         * Schaltsekunde. Ist der Wert {@code +1}, dann handelt es sich um
         * eine positive Schaltsekunde. Ist der Wert {@code -1}, dann wird
         * eine negative Schaltsekunde angenommen. Andere Werte werden nicht
         * unterst&uuml;tzt. </p>
         *
         * @return  map from leap second event day to sign of leap second
         */
        Map<GregorianDate, Integer> getLeapSecondTable();

        /**
         * <p>Werden auch negative Schaltsekunden unterst&uuml;tzt? </p>
         *
         * <p>Bis jetzt hat es real noch nie negative Schaltsekunden gegeben.
         * Solange das der Fall ist, darf ein Provider aus Gr&uuml;nden der
         * besseren Performance hier {@code false} zur&uuml;ckgeben. </p>
         *
         * @return  {@code true} if supported else {@code false}
         */
        boolean supportsNegativeLS();

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class IsoDate
        implements GregorianDate, Serializable {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final long serialVersionUID = 786391662682108754L;

        //~ Instanzvariablen ----------------------------------------------

        /**
         * @serial  proleptic iso year
         */
        private final int year;

        /**
         * @serial  gregorian month in range (1-12)
         */
        private final int month;

        /**
         * @serial  day of month in range (1-31)
         */
        private final int dayOfMonth;

        //~ Konstruktoren -------------------------------------------------

        IsoDate(
            int year,
            int month,
            int dayOfMonth
        ) {
            super();

            GregorianMath.checkDate(year, month, dayOfMonth);

            this.year = year;
            this.month = month;
            this.dayOfMonth = dayOfMonth;

        }

        //~ Methoden ----------------------------------------------------------

        @Override
        public int getYear() {

            return this.year;

        }

        @Override
        public int getMonth() {

            return this.month;

        }

        @Override
        public int getDayOfMonth() {

            return this.dayOfMonth;

        }

        @Override
        public boolean equals(Object obj) {

            if (this == obj) {
                return true;
            } else if (obj instanceof IsoDate) {
                IsoDate that = (IsoDate) obj;
                return (
                    (this.dayOfMonth == that.dayOfMonth)
                    && (this.month == that.month)
                    && (this.year == that.year)
                );
            } else {
                return false;
            }

        }

        @Override
        public int hashCode() {

            return this.year + 31 * this.month + 37 * this.dayOfMonth;

        }

        @Override
        public String toString() {

            StringBuilder sb = new StringBuilder();

            if (this.year < 0) {
                sb.append('-');
            }

            int y = Math.abs(this.year);

            if (y < 1000) {
                sb.append('0');
                if (y < 100) {
                    sb.append('0');
                    if (y < 10) {
                        sb.append('0');
                    }
                }
            }

            sb.append(y);
            sb.append('-');

            if (this.month < 10) {
                sb.append('0');
            }

            sb.append(this.month);
            sb.append('-');

            if (this.dayOfMonth < 10) {
                sb.append('0');
            }

            sb.append(this.dayOfMonth);
            return sb.toString();

        }

        /**
         * @serialData  Checks the consistency.
         */
        private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {

            in.defaultReadObject();

            int y = this.year;
            int m = this.month;
            int d = this.dayOfMonth;

            if (!GregorianMath.isValid(y, m, d)) {
                throw new InvalidObjectException("Corrupt date.");
            }

        }

    }

    private static class SimpleLeapSecondEvent
        implements ExtendedLSE, Serializable {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final long serialVersionUID = 5986185471610524587L;

        //~ Instanzvariablen ----------------------------------------------

        /**
         * @serial  date of leap second day
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

        // Standard-Konstruktor
        SimpleLeapSecondEvent(
            GregorianDate date,
            long rawTime,
            int shift
        ) {
            super();

            this.date =
                new IsoDate( // defensives Kopieren
                    date.getYear(), date.getMonth(), date.getDayOfMonth());
            this.shift = shift;

            this._utc = Long.MIN_VALUE;
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
            sb.append(this.date);
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

    private static class DefaultLeapSecondService
        implements Provider {

        //~ Instanzvariablen ----------------------------------------------

        private final String source;
        private final Map<GregorianDate, Integer> table;

        //~ Konstruktoren -------------------------------------------------

        DefaultLeapSecondService() {
            super();

            this.table = new LinkedHashMap<GregorianDate, Integer>(50);
            InputStream is = null;
            String name = "data/leapseconds.data";
            ClassLoader cl = Thread.currentThread().getContextClassLoader();

            if (cl != null) {
                is = cl.getResourceAsStream(name);
            }

            if ((cl == null) || (is == null)) {
                cl = LeapSeconds.Provider.class.getClassLoader();
                is = cl.getResourceAsStream(name);
            }

            if (is != null) {

                this.source = cl.getResource(name).toString();

                try {

                    BufferedReader br =
                        new BufferedReader(
                            new InputStreamReader(is, "US-ASCII"));

                    String line;

                    while ((line = br.readLine()) != null) {

                        if (line.startsWith("#")) {
                            continue; // Kommentarzeile überspringen
                        }

                        int comma = line.indexOf(',');
                        String date;
                        Boolean sign = null;

                        if (comma == -1) {
                            date = line.trim();
                            sign = Boolean.TRUE;
                        } else {
                            date = line.substring(0, comma).trim();
                            String s = line.substring(comma + 1).trim();

                            if (s.length() == 1) {
                                char c = s.charAt(0);
                                if (c == '+') {
                                    sign = Boolean.TRUE;
                                } else if (c == '-') {
                                    sign = Boolean.FALSE;
                                }
                            }

                            if (sign == null) {
                                throw new IllegalStateException(
                                    "Missing leap second sign.");
                            }
                        }

                        int year = Integer.parseInt(date.substring(0, 4));
                        int month = Integer.parseInt(date.substring(5, 7));
                        int dom = Integer.parseInt(date.substring(8, 10));

                        Object old =
                            this.table.put(
                                new IsoDate(year, month, dom),
                                Integer.valueOf(sign.booleanValue() ? 1 : -1)
                            );

                        if (old != null) {
                            throw new IllegalStateException(
                                "Duplicate leap second event found.");
                        }

                    }

                } catch (UnsupportedEncodingException uee) {
                    throw new AssertionError(uee);
                } catch (IllegalStateException ise) {
                    throw ise;
                } catch (Exception ex) {
                    throw new IllegalStateException(ex);
                } finally {
                    try {
                        is.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace(System.err);
                    }
                }

            } else {
                this.source = "";
                System.out.println("Warning: File \"" + name + "\" not found.");
            }

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Map<GregorianDate, Integer> getLeapSecondTable() {

            return Collections.unmodifiableMap(this.table);

        }

        @Override
        public boolean supportsNegativeLS() {

            return true;

        }

        @Override
        public String toString() {

            return this.source;

        }

    }

    private static class EventComparator
        implements Comparator<ExtendedLSE> {

        //~ Methoden ------------------------------------------------------

        @Override
        public int compare(
            ExtendedLSE o1,
            ExtendedLSE o2
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

    }

}
