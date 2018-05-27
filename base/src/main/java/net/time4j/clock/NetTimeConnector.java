/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (NetTimeConnector.java) is part of project Time4J.
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

package net.time4j.clock;

import net.time4j.Moment;
import net.time4j.SI;
import net.time4j.SystemClock;
import net.time4j.scale.TimeScale;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ServiceLoader;


/**
 * <p>Represents an abstract connection object to an internet time server. </p>
 *
 * <p>A socket connection can be established by {@code connect()} and then the difference
 * between the local computer clock using {@code SystemClock.MONOTONIC} and the net time
 * can be queried. Next time queries of this clock are based on this difference
 * and the local clock - until the next CONNECT. </p>
 *
 * @param   <C> generic configuration type
 * @author  Meno Hochschild
 * @see     SystemClock#MONOTONIC
 */
/*[deutsch]
 * <p>Stellt ein abstraktes Verbindungsobjekt zu einem Uhrzeit-Server dar. </p>
 *
 * <p>Mit Hilfe von {@code connect()} wird eine Socket-Verbindung aufgebaut, der Uhrzeit-Server
 * abgefragt und dann die Differenz zwischen lokaler Rechneruhr ({@code SystemClock.MONOTONIC})
 * und Netzzeit notiert. Weitere normale Zeitanfragen an diese Klasse basieren bis zum n&auml;chsten
 * CONNECT auf dieser Zeitdifferenz und der lokalen Rechneruhr. </p>
 *
 * @param   <C> generischer Konfigurationstyp
 * @author  Meno Hochschild
 * @see     SystemClock#MONOTONIC
 */
public abstract class NetTimeConnector<C extends NetTimeConfiguration>
    extends AbstractClock {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int MIO = 1000000;

    //~ Instanzvariablen --------------------------------------------------

    private final Moment startMoment;
    private final C defaultNTC;
    private volatile ConnectionResult result;
    private volatile PrintWriter writer;
    private volatile C ntc;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Creates a configured instance. </p>
     *
     * @param   ntc             Verbindungskonfiguration
     */
    /*[deutsch]
     * <p>Erzeugt eine konfigurierte Instanz. </p>
     *
     * @param   ntc             Verbindungskonfiguration
     */
    protected NetTimeConnector(C ntc) {
        super();

        if (ntc == null) {
            throw new NullPointerException("Missing configuration parameters.");
        }

        this.startMoment = SystemClock.MONOTONIC.currentTime();
        this.defaultNTC = ntc;
        this.result = null;
        this.writer = null;
        this.ntc = ntc;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Yields the current time after a connection has been established
     * at least once. </p>
     *
     * <p>If there was no connection yet then this method just displays
     * the time of clock construction. Note that this method is still
     * sensible for any local clock change triggered by the underlying
     * operating system. If an application needs a fresh internet time then
     * following code can be used instead (causing network traffic): </p>
     *
     * <pre>
     *  NetTimeConnector&lt;?&gt; clock = ...;
     *  clock.connect();
     *  Moment currentTime = clock.getLastConnectionTime();
     * </pre>
     *
     * @return  Moment
     * @see     #isRunning()
     */
    /*[deutsch]
     * <p>Liefert die aktuelle Zeit, nachdem eine Verbindung wenigstens
     * einmal hergestellt wurde. </p>
     *
     * <p>Hat es noch keine Verbindung gegeben, dann zeigt diese Methode
     * lediglich die Zeit an, zu der diese Uhr konstruiert wurde. Zu
     * beachten: Diese Methode reagiert empfindlich auf jedwede &Auml;nderung
     * der lokalen Uhr, die vom zugrundeliegenden Betriebssystem verursacht
     * wird. Wenn eine Anwendung direkt die Internet-Zeit ben&ouml;tigt,
     * dann kann stattdessen folgender Code verwendet werden (verursacht
     * eine Netzwerkverbindung): </p>
     *
     * <pre>
     *  NetTimeConnector&lt;?&gt; clock = ...;
     *  clock.connect();
     *  Moment currentTime = clock.getLastConnectionTime();
     * </pre>
     *
     * @return  Moment
     * @see     #isRunning()
     */
    @Override
    public Moment currentTime() {

        final ConnectionResult cr = this.result;

        if (cr == null) {
            return this.startMoment;
        }

        long localMicros = SystemClock.MONOTONIC.realTimeInMicros();
        long amount =
            localMicros
            + cr.getActualOffset(localMicros)
            - extractMicros(cr.lastMoment);
        return cr.lastMoment.plus(amount * 1000, SI.NANOSECONDS);

    }

    /**
     * <p>The clock is running as soon as there was established a connection
     * at least once. </p>
     *
     * <p>If there is not yet any connection then this clock will only display
     * the time of its construction. </p>
     *
     * @return  boolean
     * @see     #connect()
     */
    /*[deutsch]
     * <p>Die Uhr l&auml;uft, sobald wenigstens einmal eine Verbindung
     * hergestellt wurde. </p>
     *
     * <p>Ist noch keine Verbindung hergestellt worden, zeigt diese Instanz
     * solange nur die Zeit an, zu der sie konstruiert wurde. </p>
     *
     * @return  boolean
     * @see     #connect()
     */
    public boolean isRunning() {

        return (this.result != null);

    }

    /**
     * <p>Queries a time server for the current time. </p>
     *
     * <p>The result can then be achieved by the method {@code currentTime()}
     * which is based on the network offset calculated in last connection.
     * A connection to the server only happens in this method and not in
     * the method {@code currentTime()}. </p>
     *
     * @throws  IOException if connection fails or in case of any
     *          inconsistent server answers
     * @see     #currentTime()
     */
    /*[deutsch]
     * <p>Fragt einen Server nach der aktuellen Uhrzeit ab. </p>
     *
     * <p>Das Ergebnis kann dann mit Hilfe der Methode {@code currentTime()}
     * abgelesen werden, welche auf dem durch die letzte Abfrage gewonnenen
     * Netzwerk-Offset basiert. Somit findet eine Verbindung zum Server nur
     * hier und nicht in der besagten Zeitermittlungsmethode statt. </p>
     *
     * @throws  IOException bei Verbindungsfehlern oder inkonsistenten Antworten
     * @see     #currentTime()
     */
    public final void connect() throws IOException {

        try {
            Moment moment = this.doConnect();
            long localMicros = SystemClock.MONOTONIC.realTimeInMicros();
            final ConnectionResult cr = this.result;
            long currentOffset = (
                (cr == null)
                ? Long.MIN_VALUE : cr.getActualOffset(localMicros));

            this.result =
                new ConnectionResult(
                    moment,
                    localMicros,
                    currentOffset,
                    this.getNetTimeConfiguration().getClockShiftWindow()
                );
        } catch (ParseException pe) {
            throw new IOException("Cannot read server reply.", pe);
        }

    }

    /**
     * <p>Queries the configuration parameters to be used for the next
     * connection. </p>
     *
     * <p>The configuration will be determined by a {@code ServiceLoader}.
     * If not available then this connector will just choose the configuration
     * which was given during construction of this instance. Any possible
     * {@code IllegalStateException} will be logged to the error console.
     * This method delegates to {@code loadNetTimeConfiguration()}. </p>
     *
     * @return  {@code true} if successful else {@code false}
     * @see     #loadNetTimeConfiguration()
     */
    /*[deutsch]
     * <p>Liest die Konfiguration f&uuml;r den n&auml;chsten Verbindungsaufbau
     * neu ein. </p>
     *
     * <p>Die Verbindungskonfiguration wird &uuml;ber den SPI-Service
     * {@link NetTimeConfiguration} ermittelt. Falls nicht vorhanden,
     * wird die Standardkonfiguration gew&auml;hlt, die bei Konstruktion
     * dieser Instanz angegeben wurde. Wenn das Laden der
     * Konfiguration eine {@code IllegalStateException} verursacht, wird
     * diese abgefangen und auf die Fehlerkonsole geloggt. Das eigentliche
     * Laden geschieht intern mittels {@code loadNetTimeConfiguration()}. </p>
     *
     * @return  {@code true} wenn erfolgreich konfiguriert, sonst {@code false}
     * @see     #loadNetTimeConfiguration()
     */
    public final boolean reconfigure() {

        try {
            this.ntc = this.loadNetTimeConfiguration();
            return true;
        } catch (IllegalStateException ex) {
            ex.printStackTrace(System.err);
            return false;
        }

    }

    /**
     * <p>Yields the current configuration parameters. </p>
     *
     * @return  configuration object
     * @see     #reconfigure()
     */
    /*[deutsch]
     * <p>Liefert die aktuell geladene Konfiguration. </p>
     *
     * @return  Konfigurationsobjekt
     * @see     #reconfigure()
     */
    public C getNetTimeConfiguration() {

        return this.ntc;

    }

    /**
     * <p>Installs a logging stream for any messages during connection. </p>
     *
     * @param   out     output stream ({@code null} disables logging)
     */
    /*[deutsch]
     * <p>Installiert einen Strom zum Loggen. </p>
     *
     * @param   out     Ausgabestrom ({@code null} schaltet das Loggen ab)
     */
    public void setLogWriter(PrintWriter out) {

        this.writer = out;

    }

    /**
     * <p>Determines if the internal logging is enabled. </p>
     *
     * @return  {@code true} if there is any installed log writer
     *          else {@code false}
     * @see     #setLogWriter(PrintWriter)
     */
    /*[deutsch]
     * <p>Ermittelt, ob das interne Logging eingeschaltet ist. </p>
     *
     * @return  {@code true} wenn ein Log-Writer existiert, sonst {@code false}
     * @see     #setLogWriter(PrintWriter)
     */
    public boolean isLogEnabled() {

        return (this.writer != null);

    }

    /**
     * <p>Yields the time of last connection. </p>
     *
     * @return  moment of last connection or {@code null} if there was not
     *          any connection yet
     * @see     #connect()
     */
    /*[deutsch]
     * <p>Liefert die w&auml;hrend des letzten Verbindungsaufbaus ermittelte
     * Netz-Zeit. </p>
     *
     * @return  moment of last connection or {@code null} if there was not
     *          any connection yet
     * @see     #connect()
     */
    public Moment getLastConnectionTime() {

        final ConnectionResult cr = this.result;
        return ((cr == null) ? null : cr.lastMoment);

    }

    /**
     * <p>Yields the last offset between net time and local time in
     * microseconds. </p>
     *
     * @return  offset in microseconds ({@code 0} if there was not any
     *          connection yet)
     * @see     #connect()
     */
    /*[deutsch]
     * <p>Liefert die zuletzt ermittelte Differenz zwischen Netz-Zeit und
     * lokaler Zeit in Mikrosekunden. </p>
     *
     * @return  offset in microseconds ({@code 0} if there was not any
     *          connection yet)
     * @see     #connect()
     */
    public long getLastOffsetInMicros() {

        return this.getLastOffset(SystemClock.MONOTONIC.realTimeInMicros());

    }

    /**
     * <p>Will be called by {@code connect()}. </p>
     *
     * @return  queried current time
     * @throws  IOException in case of any connection failure
     * @throws  ParseException if the server reply is not readable
     * @see     #connect()
     */
    /*[deutsch]
     * <p>Wird von {@code connect()} aufgerufen. </p>
     *
     * @return  gelesener Zeitpunkt
     * @throws  IOException bei Verbindungsfehlern
     * @throws  ParseException wenn die Antwort des Servers unlesbar ist
     * @see     #connect()
     */
    protected abstract Moment doConnect() throws IOException, ParseException;

    /**
     * <p>Loads the configuration parameters via a {@code ServiceLoader}. </p>
     *
     * <p>Subclasses should always start by calling
     * {@code super.loadNetTimeConfiguration()} if this
     * method is overridden. </p>
     *
     * @return  loaded configuration parameters
     * @throws  IllegalStateException in case of any configuration error
     * @see     #reconfigure()
     */
    /*[deutsch]
     * <p>Laden der Konfiguration über einen SPI-Service. </p>
     *
     * <p>Subklassen sollten zuerst mit {@code super()} diese Methode aufrufen,
     * wenn sie sie &uuml;berschreiben. </p>
     *
     * @return  geladene Konfiguration
     * @throws  IllegalStateException bei Konfigurationsfehlern
     * @see     #reconfigure()
     */
    protected synchronized C loadNetTimeConfiguration() {

        ServiceLoader<C> sl = ServiceLoader.load(this.getConfigurationType());
        C loaded = null;

        for (C cfg : sl) {
            if (cfg != null) {
                loaded = cfg;
                break;
            }
        }

        if (loaded == null) {
            loaded = this.defaultNTC;
        }

        String addr = loaded.getTimeServerAddress();
        int port = loaded.getTimeServerPort();

        if (
            (addr == null)
            || addr.trim().isEmpty()
        ) {
            throw new IllegalStateException("Missing time server address.");
        } else if (loaded.getConnectionTimeout() < 0) {
            throw new IllegalStateException("Negative time out.");
        } else if ((port < 0) || (port > 65536)) {
            throw new IllegalStateException("Port out of range: " + port);
        } else if (loaded.getClockShiftWindow() < 0) {
            throw new IllegalStateException("Clock shift window is negative.");
        }

        return loaded;

    }

    /**
     * <p>Logs given message if a {@code PrintWriter} has been installed. </p>
     *
     * @param   prefix      optional prefix
     * @param   message     message to be logged
     * @see     #setLogWriter(PrintWriter)
     */
    /*[deutsch]
     * <p>Loggt die angegebene Nachricht, falls ein {@code PrintWriter}
     * installiert ist. </p>
     *
     * @param   prefix      optionales Pr&auml;fix vor der eigentlichen Meldung
     * @param   message     zu loggende Meldung
     * @see     #setLogWriter(PrintWriter)
     */
    protected void log(
        String prefix, // nullable
        String message
    ) {

        final PrintWriter out = this.writer;

        if (out != null) {
            if (prefix == null) {
                out.println(message);
            } else {
                out.println(prefix + message);
            }
        }

    }

    /**
     * <p>Yields the configuration type. </p>
     *
     * @return  configuration type
     */
    /*[deutsch]
     * <p>Liefert den Typ der Verbindungskonfiguration. </p>
     *
     * @return  Konfigurationstyp
     */
    protected abstract Class<C> getConfigurationType();

    /**
     * <p>Liefert die aktuelle Differenz zwischen Netz-Zeit und lokaler Zeit in Mikrosekunden. </p>
     *
     * @param   micros  aktuelle lokale Zeit in Mikrosekunden
     * @return  Mikrosekunden-Offset ({@code 0}, wenn noch keine Verbindung hergestellt wurde)
     */
    long getLastOffset(long micros) {

        final ConnectionResult cr = this.result;
        return ((cr == null) ? 0 : cr.getActualOffset(micros));

    }

    private static long extractMicros(Moment time) {

        return time.getElapsedTime(TimeScale.UTC) * MIO + time.getNanosecond(TimeScale.UTC) / 1000;

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class ConnectionResult {

        //~ Instanzvariablen ----------------------------------------------

        private final Moment lastMoment;
        private final long startTime;
        private final long startOffset;
        private final long endOffset;
        private final int window;

        //~ Konstruktoren -------------------------------------------------

        ConnectionResult(
            Moment time,
            long localMicros,
            long startOffset,
            int window
        ) {
            super();

            this.lastMoment = time;
            this.startTime = localMicros;
            this.startOffset = startOffset;
            this.endOffset = (extractMicros(time) - localMicros);
            this.window = window * MIO;

        }

        // Ermittelt den aktuellen Offset in maximal Mikrosekundengenauigkeit
        long getActualOffset(long micros) {

            if (
                (this.window == 0)
                || (this.startOffset <= this.endOffset)
                || (micros - this.startTime >= this.window)
            ) {
                return this.endOffset; // sofortige Anpassung
            }

            double t = Math.max(0.0, micros - this.startTime);
            double modulation = (1 + Math.cos(Math.PI * t / this.window)) / 2;

            return Math.round(
                this.endOffset
                + modulation * (this.startOffset - this.endOffset)
            );

        }

    }

}
