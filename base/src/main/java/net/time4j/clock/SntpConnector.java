/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SntpConnector.java) is part of project Time4J.
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
import net.time4j.SystemClock;
import net.time4j.scale.TimeScale;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ServiceLoader;


/**
 * <p>Connects to a modern time server using the NTP-protocol. </p>
 *
 * <p>This class needs a socket connection via the port 123. The exact
 * configuration can be set in the constructors but can also be changed
 * at runtime. It is recommended not to connect to the NTP-server during
 * or near a leap second because the NTP-protocol only repeats such a
 * timestamp causing ambivalences. </p>
 *
 * <p>Note: This implementation is <i>threadsafe</i>. </p>
 *
 * @author  Meno Hochschild
 * @since   2.1
 */
/*[deutsch]
 * <p>Nimmt die Verbindung zu einem modernen Uhrzeit-Server gem&auml;&szlig;
 * dem NTP-Protokoll auf. </p>
 *
 * <p>Diese Klasse ben&ouml;tigt einen Internet-Zugang &uuml;ber
 * Port 123 (NTP). Die genaue Konfiguration wird zun&auml;chst im
 * Konstruktor festgelegt, kann aber zur Laufzeit ge&auml;ndert werden.
 * Es wird empfohlen, nicht w&auml;hrend oder nahe einer Schaltsekunde
 * zum NTP-Server zu verbinden, weil das NTP-Protokoll solch einen
 * Zeitstempel nur wiederholt und sich somit hier ambivalent zeigt. </p>
 *
 * <p>Die Physikalisch-Technische Bundesanstalt in Braunschweig (PTB),
 * die dort eine Atomuhr betreibt, ben&ouml;tigt als Adresse den Wert
 * &quot;ptbtime1.ptb.de&quot; und das Protokoll NTP4. Eine Alternative
 * ist die Adresse &quot;ptbtime2.ptb.de&quot;. </p>
 *
 * <p>Hinweis: Diese Implementierung ist <i>threadsafe</i>, also gegen
 * konkurrierende Zugriffe gesch&uuml;tzt. </p>
 *
 * @author  Meno Hochschild
 * @since   2.1
 */
public class SntpConnector
    extends NetTimeConnector<SntpConfiguration> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int MIO = 1000000;

    //~ Instanzvariablen --------------------------------------------------

    private volatile SntpMessage lastReply = null;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Creates a new instance which is configured by a
     * {@code ServiceLoader}. </p>
     *
     * @throws  IllegalStateException if no configuration could be found
     * @see     ServiceLoader
     */
    /*[deutsch]
     * <p>Konstruiert eine neue Instanz, die &uuml;ber einen
     * {@code ServiceLoader} konfiguriert ist. </p>
     *
     * @throws  IllegalStateException if no configuration could be found
     * @see     ServiceLoader
     */
    public SntpConnector() {
        super(initConfiguration());

    }

    /**
     * <p>Creates a new instance which is configured by given argument. </p>
     *
     * @param   ntc     SNTP-configuration
     */
    /*[deutsch]
     * <p>Konstruiert eine neue Instanz, die wie angegeben
     * konfiguriert ist. </p>
     *
     * @param   ntc     vorgesehene Konfiguration
     */
    public SntpConnector(SntpConfiguration ntc) {
        super(ntc);

    }

    /**
     * <p>Creates a new instance which uses a default configuration
     * using the specified NTP4-server. </p>
     *
     * <p>Example: </p>
     *
     * <pre>
     *  SntpConnector clock = new SntpConnector(&quot;ptbtime1.ptb.de&quot;);
     *  clock.connect();
     *  System.out.println(clock.currentTime());
     * </pre>
     *
     * @param   server  NTP4-server
     */
    /*[deutsch]
     * <p>Konstruiert eine neue Instanz, die zum angegebenen NTP-Server
     * verbindet. </p>
     *
     * <p>Beispiel: </p>
     *
     * <pre>
     *  SntpConnector clock = new SntpConnector(&quot;ptbtime1.ptb.de&quot;);
     *  clock.connect();
     *  System.out.println(clock.currentTime());
     * </pre>
     *
     * @param   server  NTP4-server
     */
    public SntpConnector(String server) {
        super(new SimpleNtpConfiguration(server));

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Returns the current time in milliseconds since the Unix epoch
     * [1970-01-01T00:00:00,000Z]. </p>
     *
     * <p>UTC leap seconds are never counted. </p>
     *
     * @return  count of milliseconds since UNIX-epoch without leap seconds
     * @since   2.1
     */
    /*[deutsch]
     * <p>Liefert die aktuelle Zeit in Millisekunden seit dem Beginn der
     * UNIX-Epoche, n&auml;mlich [1970-01-01T00:00:00,000Z]. </p>
     *
     * <p>Es handelt sich immer um eine Zeitangabe ohne UTC-Schaltsekunden. </p>
     *
     * @return  count of milliseconds since UNIX-epoch without leap seconds
     * @since   2.1
     */
    public long currentTimeInMillis() {

        if (!this.isRunning()) {
            Moment m = this.currentTime();

            return (
                m.getPosixTime() * 1000
                + m.getNanosecond() / MIO
            );
        }

        long millis = SystemClock.MONOTONIC.currentTimeInMillis();
        return (millis + (this.getLastOffset(millis * 1000) / 1000));

    }

    /**
     * <p>Returns the current time in microseconds since the Unix epoch
     * [1970-01-01T00:00:00,000000Z]. </p>
     *
     * <p>UTC leap seconds are never counted. </p>
     *
     * @return  count of microseconds since UNIX-epoch without leap seconds
     * @since   2.1
     */
    /*[deutsch]
     * <p>Liefert die aktuelle Zeit in Mikrosekunden seit dem Beginn der
     * UNIX-Epoche, n&auml;mlich [1970-01-01T00:00:00,000000Z]. </p>
     *
     * <p>Es handelt sich immer um eine Zeitangabe ohne UTC-Schaltsekunden. </p>
     *
     * @return  count of microseconds since UNIX-epoch without leap seconds
     * @since   2.1
     */
    public long currentTimeInMicros() {

        if (!this.isRunning()) {
            Moment m = this.currentTime();

            return (
                m.getPosixTime() * MIO
                + m.getNanosecond() / 1000
            );
        }

        long micros = SystemClock.MONOTONIC.currentTimeInMicros();
        return (micros + this.getLastOffset(micros));

    }

    /**
     * <p>Returns the last received message of the NTP-server. </p>
     *
     * @return  server message or {@code null} if not yet received
     * @since   2.1
     * @see     #connect()
     */
    /*[deutsch]
     * <p>Liefert die zuletzt erhaltene Nachricht des Servers. </p>
     *
     * @return  Server-Nachricht oder {@code null}, wenn noch nicht empfangen
     * @since   2.1
     * @see     #connect()
     */
    public SntpMessage getLastReply() {

        return this.lastReply;

    }

    @Override
    protected Moment doConnect() throws IOException {

        // Konfigurationswerte holen
        final SntpConfiguration config = this.getNetTimeConfiguration();

        short requestCount = config.getRequestCount();

        if (requestCount <= 0) {
            return SystemClock.MONOTONIC.currentTime();
        }

        String address = config.getTimeServerAddress();
        int port = config.getTimeServerPort();
        int timeout = config.getConnectionTimeout();
        boolean version4 = config.isNTP4();
        long pollInterval = config.getRequestInterval() * 1000;

        // lokaler Offset-Mittelwert in Mikrosekunden
        long sum = 0;
        long averageOffset = 0;

        // UDP-Socket öffnen
        DatagramSocket socket = null;

        try {

            socket = new DatagramSocket();
            socket.setSoTimeout(timeout * 1000);

            for (int i = 1; i <= requestCount; i++) {

                // Zeitanfrage formulieren
                this.log(null, "Connecting NTP-Server, waiting for reply...");
                InetAddress iaddr = InetAddress.getByName(address);
                SntpMessage requestMessage = new SntpMessage(version4);
                double transmitTS = requestMessage.getTransmitTimestamp();
                byte version = requestMessage.getVersion();
                byte[] data = requestMessage.getBytes();

                // Zeitanfrage abschicken
                DatagramPacket request =
                    new DatagramPacket(data, data.length, iaddr, port);
                socket.send(request);

                // Antwort abwarten
                DatagramPacket reply = new DatagramPacket(data, data.length);
                socket.receive(reply);

                // Sofort eigenen Timestamp notieren
                double destinationTimestamp = SntpMessage.getLocalTimestamp();

                // Antwort auspacken
                SntpMessage replyMessage =
                    new SntpMessage(reply.getData(), transmitTS, version);
                this.lastReply = replyMessage;

                if (this.isLogEnabled()) {
                    this.log("NTP-Server connected: ", replyMessage.toString());
                }

                // Annahme gleicher Netzlaufzeiten für Anfrage und Antwort
                // round-trip-delay: (D - O) - (T - R) = 2 * Netzlaufzeit
                // REAL-LOCAL-TIME = T + Netzlaufzeit = D + localClockOffset
                double localClockOffset = (
                    replyMessage.getReceiveTimestamp()
                    - replyMessage.getOriginateTimestamp()
                    + replyMessage.getTransmitTimestamp()
                    - destinationTimestamp
                ) / 2.0;

                sum += (localClockOffset * MIO);
                averageOffset = (sum / i);

                if (replyMessage.getStratum() == 0) {

                    this.log("NTP-Server replied: ", "<kiss-o'-death>");
                    break;

                } else if (
                    (i > 1)
                    && (i < requestCount)
                ) {

                    try {
                        Thread.sleep(pollInterval);
                    } catch (InterruptedException ie) {
                        this.log(null, "NTP-Connection interrupted.");
                        break;
                    }

                }

            }

        } finally {
            if (socket != null) {
                socket.close();
            }
        }

        long micros = SystemClock.MONOTONIC.currentTimeInMicros() + averageOffset;
        long seconds = micros / MIO;
        int nanosecond = (int) ((micros % MIO) * 1000);
        byte leapIndicator = this.lastReply.getLeapIndicator();

        if (leapIndicator == 3) {
            throw new IOException(
                "Alarm condition: "
                + "NTP-Server is not synchronized with any clock source.");
        } else if (leapIndicator == 1) {
            this.log(null, "Positive leap second announced.");
        } else if (leapIndicator == 2) {
            this.log(null, "Negative leap second announced.");
        }

        return Moment.of(seconds, nanosecond, TimeScale.POSIX);

    }

    @Override
    protected SntpConfiguration loadNetTimeConfiguration() {

        final SntpConfiguration sc = super.loadNetTimeConfiguration();
        short rcount = sc.getRequestCount();
        int rinterval = sc.getRequestInterval();

        if ((rcount < 0) || (rcount >= 1000)) {
            throw new IllegalStateException("Wrong request count: " + rcount);
        } else if (rinterval <= 0) {
            throw new IllegalStateException("Wrong request interval.");
        }

        return sc;

    }

    @Override
    protected Class<SntpConfiguration> getConfigurationType() {

        return SntpConfiguration.class;

    }

    private static SntpConfiguration initConfiguration() {

        ServiceLoader<SntpConfiguration> sl =
            ServiceLoader.load(SntpConfiguration.class);

        for (SntpConfiguration cfg : sl) {
            if (cfg != null) {
                return cfg;
            }
        }

        throw new IllegalStateException("SNTP-configuration not found.");

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class SimpleNtpConfiguration
        implements SntpConfiguration {

        //~ Instanzvariablen ----------------------------------------------

        private final String server;

        //~ Konstruktoren -------------------------------------------------

        SimpleNtpConfiguration(String server) {
            super();

            if (server == null) {
                throw new NullPointerException("Missing time server address.");
            }

            this.server = server;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public String getTimeServerAddress() {

            return this.server;

        }

        @Override
        public int getTimeServerPort() {

            return 123;

        }

        @Override
        public int getConnectionTimeout() {

            return DEFAULT_CONNECTION_TIMEOUT;

        }

        @Override
        public boolean isNTP4() {

            return true;

        }

        @Override
        public int getRequestInterval() {

            return 60 * 4;

        }

        @Override
        public short getRequestCount() {

            return 1;

        }

        @Override
        public int getClockShiftWindow() {

            return 0;

        }

        @Override
        public String toString() {

            StringBuilder sb = new StringBuilder();
            sb.append("SimpleNtpConfiguration:[server=");
            sb.append(this.server);
            sb.append(",port=");
            sb.append(this.getTimeServerPort());
            sb.append(']');
            return sb.toString();

        }

    }

}
