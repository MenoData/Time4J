/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SntpMessage.java) is part of project Time4J.
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


/**
 * <p>Message for the SNTP-protocol (RFC 4330). </p>
 *
 * <p>NTP-timestamps will be calculated at best in microsecond precision. </p>
 *
 * @author  Meno Hochschild
 * @since   2.1
 */
/*[deutsch]
 * <p>Nachricht f&uuml;r das SNTP-Protokoll (RFC 4330). </p>
 *
 * <p>NTP-Zeitstempel werden maximal in Mikrosekundengenauigkeit berechnet. </p>
 *
 * @author  Meno Hochschild
 * @since   2.1
 */
public final class SntpMessage {

    //~ Statische Felder/Initialisierungen --------------------------------

    // Mikrosekunden seit [1900-01-01T00:00:00Z] (relativ zu 1.1.1970)
    private static final long OFFSET_1900 = 2208988800000000L;

    // Mikrosekunden seit [2036-02-07T06:28:16Z] (ohne Schaltsekunden!)
    private static final long OFFSET_2036 = -2085978496000000L;

    private static final int MIO = 1000000;
    private static final double MIO_AS_DOUBLE = 1000000.0;
    private static final byte[] NULL_REF_ID = new byte[] {0, 0, 0, 0};
    private static final double BIT08 = 256.0;
    private static final double BIT16 = 65536.0;

    //~ Instanzvariablen --------------------------------------------------

    private final byte leapIndicator;
    private final byte version;
    private final byte mode;
    private final short stratum;
    private final short pollInterval;
    private final byte precision;
    private final double rootDelay;
    private final double rootDispersion;
    private final byte[] refID;
    private final double referenceTimestamp;
    private final double originateTimestamp;
    private final double receiveTimestamp;
    private final double transmitTimestamp;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruiert eine Anfrage des Clients. </p>
     *
     * @param   version4    NTP4-Version statt NTP3-Version verwenden?
     */
    SntpMessage(boolean version4) {
        super();

        this.leapIndicator = 0;
        this.version = (byte) (version4 ? 4 : 3);
        this.mode = 3;
        this.stratum = 0;
        this.pollInterval = 0;
        this.precision = 0;
        this.rootDelay = 0;
        this.rootDispersion = 0;
        this.refID = NULL_REF_ID;
        this.referenceTimestamp = 0;
        this.originateTimestamp = 0;
        this.receiveTimestamp = 0;
        this.transmitTimestamp = getLocalTimestamp();

    }

    /**
     * <p>Konstruiert eine Antwort des Servers. </p>
     *
     * @param   data                    Antwort-Daten des Servers
     * @param   expectedOriginateTS     erwarteter Zeitstempel im NTP-Format
     * @param   expectedVersion         erwartete NTP-Version des Servers
     * @throws  IOException bei Plausibilit&auml;tsverletzungen
     */
    SntpMessage(
        byte[] data,
        double expectedOriginateTS,
        byte expectedVersion
    ) throws IOException {
        super();

        this.leapIndicator = (byte) ((data[0] >> 6) & 0x3);
        this.version = (byte) ((data[0] >> 3) & 0x7);
        this.mode = (byte) (data[0] & 0x7);
        this.stratum = toUnsigned(data[1]);
        this.pollInterval = toUnsigned(data[2]);
        this.precision = data[3];

        this.rootDelay = (
            (data[4] * BIT08)
            + toUnsigned(data[5])
            + (toUnsigned(data[6]) / BIT08)
            + (toUnsigned(data[7]) / BIT16)
        );

        int dispersion = 0;
        for (int i = 0; i < 4; i++) {
            int unsigned = (data[i + 8] & 0xFF);
            dispersion |= (unsigned << (24 - i * 8));
        }
        this.rootDispersion = (dispersion / BIT16);

        byte[] r = new byte[4];
        r[0] = data[12];
        r[1] = data[13];
        r[2] = data[14];
        r[3] = data[15];
        this.refID = r;

        this.referenceTimestamp = decode(data, 16);
        this.originateTimestamp = decode(data, 24);
        this.receiveTimestamp = decode(data, 32);
        this.transmitTimestamp = decode(data, 40);

        // Plausibilitätsprüfungen
        if (this.transmitTimestamp == 0) {
            throw new IOException(
                "Server hasn't sent any transmit timestamp.");
        } else if (
            Math.abs(expectedOriginateTS - this.originateTimestamp) >= 0.001
            && (this.mode == 4)
        ) {
            throw new IOException(
                "Originate timestamp does not match sent timestamp: "
                + this.originateTimestamp
                + " (expected = "
                + expectedOriginateTS
                + ")"
            );
        } else if (
            (this.mode != 4)
            && (this.mode != 5)
        ) {
            throw new IOException(
                "Unexpected server mode: " + this.mode);
        } else if (
            (this.leapIndicator < 0)
            || (this.leapIndicator > 3)
        ) {
            throw new IOException(
                "Unexpected leap indicator: " + this.leapIndicator);
        } else if (
            (this.version < 1)
            || (this.version > 4)
            || ((this.mode == 4) && (this.version != expectedVersion))
        ) {
            throw new IOException(
                "Unexpected ntp version: " + this.version);
        } else if (
            (this.stratum < 0)
            || (this.stratum > 15)
        ) {
            throw new IOException(
                "Unexpected stratum: " + this.stratum);
        }

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Yields the LI-bits as appointment of a coming leap second. </p>
     *
     * <ul><li>0 - no warning. </li>
     * <li>1 - last minute of day has 61 seconds. </li>
     * <li>2 - last minute of day has 59 seconds. </li>
     * <li>3 - alert state (missing clock synchronization). </li></ul>
     *
     * @return  byte
     * @since   2.1
     */
    /*[deutsch]
     * <p>Ermittelt die LI-Bits als Ank&uuml;ndigung einer bevorstehenden
     * Schaltsekunde. </p>
     *
     * <ul><li>0 - Keine Warnung. </li>
     * <li>1 - Letzte Minute des Tages hat 61 Sekunden. </li>
     * <li>2 - Letzte Minute des Tages hat 59 Sekunden. </li>
     * <li>3 - Alarmzustand (fehlende Uhrzeitsynchronisation). </li></ul>
     *
     * @return  byte
     * @since   2.1
     */
    public byte getLeapIndicator() {

        return this.leapIndicator;

    }

    /**
     * <p>Displays the NTP-version. </p>
     *
     * @return  3 (if only IPv4 is supported) else 4
     * @since   2.1
     */
    /*[deutsch]
     * <p>Zeigt die NTP-Version an. </p>
     *
     * @return  3 (wenn nur IPv4 unterst&uuml;tzt wird), sonst 4
     * @since   2.1
     */
    public byte getVersion() {

        return this.version;

    }

    /**
     * <p>Displays the mode. </p>
     *
     * @return  3 (client-mode) or 4 (server-mode) oder 5 (broadcast)
     * @since   2.1
     */
    /*[deutsch]
     * <p>Zeigt den Modus an. </p>
     *
     * @return  3 (Client-Modus) oder 4 (Server-Modus) oder 5 (Broadcast)
     * @since   2.1
     */
    public byte getMode() {

        return this.mode;

    }

    /**
     * <p>Displays the stratum-value indicating the <i>distance</i> of
     * the original time source. </p>
     *
     * <p>Usually it is the count of involved clock servers respective
     * layers. The value {@code 0} indicates a <i>kiss-o&#39;-death</i>-message
     * by which the server signals to the client that repeated requests should
     * be immediately stopped. </p>
     *
     * @return  0 (unspecified) oder 1 (direkt) oder 2-15 (sekund&auml;r)
     * @since   2.1
     */
    /*[deutsch]
     * <p>Zeigt den Stratum-Wert als Ma&szlig; f&uuml;r die
     * <i>Entfernung</i> der Zeitquelle an. </p>
     *
     * <p>In der Regel handelt es sich um die Anzahl der  beteiligten
     * Uhrzeit-Server bzw. Schichten. Der Wert {@code 0} zeigt eine
     * <i>kiss-o&#39;-death</i>-Nachricht an, womit der Server dem
     * Client signalisiert, das wiederholte Senden von Anfragen sofort
     * einzustellen. </p>
     *
     * @return  0 (unspezifiziert) oder 1 (direkt) oder 2-15 (sekund&auml;r)
     * @since   2.1
     */
    public short getStratum() {

        return this.stratum;

    }

    /**
     * <p>Yields the maximum interval between two successful server messages
     * in seconds as exponent for base 2. </p>
     *
     * <p>This property is only relevant if the server is sending messages
     * in the <i>broadcast</i>-mode. In all other cases the server should
     * send the value{@code 0}. </p>
     *
     * @return  broadcast-mode: value in range {@code 4 <= pollInterval <= 17}
     * @since   2.1
     */
    /*[deutsch]
     * <p>Liefert das maximale Intervall zwischen zwei erfolgreichen
     * Server-Nachrichten in Sekunden als Exponent zur Basis 2. </p>
     *
     * <p>Diese Eigenschaft ist nur von Belang, wenn der Server Nachrichten
     * im <i>broadcast</i>-Modus sendet. Sonst sollte der Server den Wert
     * {@code 0} senden. </p>
     *
     * @return  broadcast-Modus: Wert im Bereich {@code 4 <= pollInterval <= 17}
     * @since   2.1
     */
    public int getPollInterval() {

        return this.pollInterval;

    }

    /**
     * <p>Yields the precision of the server clock in seconds as exponent
     * for the base 2. </p>
     *
     * @return  int in the usual range {@code -6 <= precision <= -23}
     * @since   2.1
     */
    /*[deutsch]
     * <p>Liefert die Genauigkeit der Systemuhr auf dem Server in Sekunden
     * als Exponent zur Basis 2. </p>
     *
     * @return  int im &uuml;blichen Bereich {@code -6 <= precision <= -23}
     * @since   2.1
     */
    public int getPrecision() {

        return this.precision;

    }

    /**
     * <p>Yields the total delay in seconds relative to the primary source. </p>
     *
     * <p>This information only concerns the NTP-server, not the actual
     * network traffic with the client. </p>
     *
     * @return  round-trip-delay in seconds
     * @since   2.1
     */
    /*[deutsch]
     * <p>Liefert die gesamte Verz&ouml;gerung in Sekunden relativ zur
     * prim&auml;ren Referenzquelle. </p>
     *
     * <p>Es handelt sich um eine Information, die nur den NTP-Server betrifft,
     * nicht aber den aktuellen Netzverkehr mit dem Client. </p>
     *
     * @return  round-trip-delay in Sekunden
     * @since   2.1
     */
    public double getRootDelay() {

        return this.rootDelay;

    }

    /**
     * <p>Yields the maximum error in seconds relative to the primary
     * source. </p>
     *
     * <p>This information only concerns the NTP-server, not the actual
     * network traffic with the client. </p>
     *
     * @return  maximum error in seconds
     * @since   2.1
     */
    /*[deutsch]
     * <p>Liefert den maximalen Fehler in Sekunden relativ zur
     * prim&auml;ren Referenzquelle. </p>
     *
     * <p>Es handelt sich um eine Information, die nur den NTP-Server betrifft,
     * nicht aber den aktuellen Netzverkehr mit dem Client. </p>
     *
     * @return  maximaler Fehler in Sekunden
     * @since   2.1
     */
    public double getRootDispersion() {

        return this.rootDispersion;

    }

    /**
     * <p>Identifies a reference source. </p>
     *
     * <p>If the connected server is a primary NTP-server (stratum = 1) then
     * the return value will usually be a string from following list: </p>
     *
     * <ul>
     *  <li>LOCL - uncalibrated local clock</li>
     *  <li>CESM - calibrated Cesium clock</li>
     *  <li>RBDM - calibrated Rubidium clock</li>
     *  <li>PPS - calibrated quartz clock or other pulse-per-second source</li>
     *  <li>IRIG - Inter-Range Instrumentation Group</li>
     *  <li>ACTS - NIST telephone modem service</li>
     *  <li>USNO - USNO telephone modem service</li>
     *  <li>PTB - PTB (Germany) telephone modem service</li>
     *  <li>TDF - Allouis (France) Radio 164 kHz</li>
     *  <li>DCF - Mainflingen (Germany) Radio 77.5 kHz</li>
     *  <li>MSF - Rugby (UK) Radio 60 kHz</li>
     *  <li>WWV - Ft. Collins (US) Radio 2.5, 5, 10, 15, 20 MHz</li>
     *  <li>WWVB - Boulder (US) Radio 60 kHz</li>
     *  <li>WWVH - Kauai Hawaii (US) Radio 2.5, 5, 10, 15 MHz</li>
     *  <li>CHU - Ottawa (Canada) Radio 3330, 7335, 14670 kHz</li>
     *  <li>LORC - LORAN-C radionavigation getChronology</li>
     *  <li>OMEG - OMEGA radionavigation getChronology</li>
     *  <li>GPS - Global positioning getChronology</li>
     * </ul>
     *
     * <p>A secondary IPv4-server will return a 32-bit-IP-address else the
     * return value represents the first 32 bits of a MD5-hash value of a
     * IPv6- or NSAP-address of the synchronization source. </p>
     *
     * <p>In case of a ({@code stratum == 0}) - reply of the server the
     * return value describes a kiss-or-death-message. The client should
     * then not repeat the request (or at least not within the next minute).
     * A non-exhausting selection: </p>
     *
     * <ul>
     *  <li>ACST - Association belongs to an anycast server</li>
     *  <li>AUTH - Server authentication failed</li>
     *  <li>AUTO - Autokey sequence failed</li>
     *  <li>BCST - Association belongs to a broadcast server</li>
     *  <li>CRYP - Cryptographic authentication or identification failed</li>
     *  <li>DENY - Access denied by remote server</li>
     *  <li>DROP - Lost peer in symmetric mode</li>
     *  <li>RSTR - Access denied due to local policy</li>
     *  <li>INIT - Association has not yet synchronized for the first time</li>
     *  <li>MCST - Association belongs to a manycast server</li>
     *  <li>NKEY - No key found. Either the key was never installed or is not
     * trusted</li>
     *  <li>RATE - Rate exceeded. The server has temporarily denied access
     * because the client exceeded the rate threshold</li>
     *  <li>RMOT - Somebody is tinkering with the association from a remote
     * host running ntpdc. Not to worry unless some rascal has stolen your
     * keys</li>
     *  <li>STEP - A step change in getChronology time has occurred, but the
     * association has not yet resynchronized</li>
     * </ul>
     *
     * @return  String
     * @since   2.1
     */
    /*[deutsch]
     * <p>Identifiziert eine Referenzquelle. </p>
     *
     * <p>Wenn es sich um einen prim&auml;ren NTP-Server handelt (stratum = 1),
     * dann wird der R&uuml;ckgabewert in der Regel ein String aus der folgenden
     * Liste sein: </p>
     *
     * <ul>
     *  <li>LOCL - uncalibrated local clock</li>
     *  <li>CESM - calibrated Cesium clock</li>
     *  <li>RBDM - calibrated Rubidium clock</li>
     *  <li>PPS - calibrated quartz clock or other pulse-per-second source</li>
     *  <li>IRIG - Inter-Range Instrumentation Group</li>
     *  <li>ACTS - NIST telephone modem service</li>
     *  <li>USNO - USNO telephone modem service</li>
     *  <li>PTB - PTB (Germany) telephone modem service</li>
     *  <li>TDF - Allouis (France) Radio 164 kHz</li>
     *  <li>DCF - Mainflingen (Germany) Radio 77.5 kHz</li>
     *  <li>MSF - Rugby (UK) Radio 60 kHz</li>
     *  <li>WWV - Ft. Collins (US) Radio 2.5, 5, 10, 15, 20 MHz</li>
     *  <li>WWVB - Boulder (US) Radio 60 kHz</li>
     *  <li>WWVH - Kauai Hawaii (US) Radio 2.5, 5, 10, 15 MHz</li>
     *  <li>CHU - Ottawa (Canada) Radio 3330, 7335, 14670 kHz</li>
     *  <li>LORC - LORAN-C radionavigation getChronology</li>
     *  <li>OMEG - OMEGA radionavigation getChronology</li>
     *  <li>GPS - Global positioning getChronology</li>
     * </ul>
     *
     * <p>Ein sekund&auml;rer IPv4-Server wird hier eine 32-Bit-IP-Adresse
     * liefern, sonst handelt es sich um die ersten 32 Bits eines MD5-Haschwerts
     * einer IPv6- oder NSAP-Adresse der Synchronisationsquelle. </p>
     *
     * <p>Im Fall einer ({@code stratum == 0}) - Antwort des Serves beschreibt
     * der R&uuml;ckgabewert eine kiss-or-death-Nachricht. Der Client sollte
     * dann nicht die Anfrage wiederholen (oder wenigstens nicht innerhalb der
     * n&auml;chsten Minute). Eine nicht ersch&ouml;pfende Auswahl: </p>
     *
     * <ul>
     *  <li>ACST - Association belongs to an anycast server</li>
     *  <li>AUTH - Server authentication failed</li>
     *  <li>AUTO - Autokey sequence failed</li>
     *  <li>BCST - Association belongs to a broadcast server</li>
     *  <li>CRYP - Cryptographic authentication or identification failed</li>
     *  <li>DENY - Access denied by remote server</li>
     *  <li>DROP - Lost peer in symmetric mode</li>
     *  <li>RSTR - Access denied due to local policy</li>
     *  <li>INIT - Association has not yet synchronized for the first time</li>
     *  <li>MCST - Association belongs to a manycast server</li>
     *  <li>NKEY - No key found. Either the key was never installed or is not
     * trusted</li>
     *  <li>RATE - Rate exceeded. The server has temporarily denied access
     * because the client exceeded the rate threshold</li>
     *  <li>RMOT - Somebody is tinkering with the association from a remote
     * host running ntpdc. Not to worry unless some rascal has stolen your
     * keys</li>
     *  <li>STEP - A step change in getChronology time has occurred, but the
     * association has not yet resynchronized</li>
     * </ul>
     *
     * @return  String
     * @since   2.1
     */
    public String getReferenceIdentifier() {

        return this.getRefIDAsString();

    }

    /**
     * <p>NTP-timestamp when the time of the server was set or corrected
     * last time. </p>
     *
     * <p>The method yields {@code 0} if no request has been sent yet. </p>
     *
     * @return  NTP-time in seconds since 1900-01-01
     * @since   2.1
     */
    /*[deutsch]
     * <p>NTP-Timestamp wann die Zeit auf dem Server zuletzt gesetzt oder
     * korrigiert wurde. </p>
     *
     * <p>Die Methode liefert {@code 0}, wenn noch nichts gesendet worden
     * ist. </p>
     *
     * @return  NTP-time in seconds since 1900-01-01
     * @since   2.1
     */
    public double getReferenceTimestamp() {

        return this.referenceTimestamp;

    }

    /**
     * <p>NTP-timestamp when the client request was sent. </p>
     *
     * <p>The method yields {@code 0} if no request has been sent yet. </p>
     *
     * @return  NTP-time in seconds since 1900-01-01
     * @since   2.1
     */
    /*[deutsch]
     * <p>NTP-Timestamp wann die Zeitabfrage vom Client gesendet wurde. </p>
     *
     * <p>Die Methode liefert {@code 0}, wenn noch nichts gesendet worden
     * ist. </p>
     *
     * @return  NTP-time in seconds since 1900-01-01
     * @since   2.1
     */
    public double getOriginateTimestamp() {

        return this.originateTimestamp;

    }

    /**
     * <p>NTP-timestamp when the server received the client request. </p>
     *
     * <p>The method yields {@code 0} if no request has been sent yet. </p>
     *
     * @return  NTP-time in seconds since 1900-01-01
     * @since   2.1
     */
    /*[deutsch]
     * <p>NTP-Timestamp wann die Zeitabfrage vom Server empfangen wurde. </p>
     *
     * <p>Die Methode liefert {@code 0}, wenn noch nichts gesendet worden
     * ist. </p>
     *
     * @return  NTP-time in seconds since 1900-01-01
     * @since   2.1
     */
    public double getReceiveTimestamp() {

        return this.receiveTimestamp;

    }

    /**
     * <p>NTP-timestamp when the client or server request was sent. </p>
     *
     * @return  NTP-time in seconds since 1900-01-01
     * @since   2.1
     */
    /*[deutsch]
     * <p>NTP-Timestamp wann die Abfrage bzw. Antwort vom Client bzw. vom
     * Server gesendet wurde. </p>
     *
     * @return  NTP-time in seconds since 1900-01-01
     * @since   2.1
     */
    public double getTransmitTimestamp() {

        return this.transmitTimestamp;

    }

    /**
     * <p>Returns a human-readable form of the internal state. </p>
     *
     * @return  String
     */
    /*[deutsch]
     * <p>Gibt den internen Zustand in menschenlesbarer Form aus. </p>
     *
     * @return  String
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(300);

        sb.append(this.getClass().getName());
        sb.append("[version=");
        sb.append(this.version);

        sb.append(", mode=");
        switch (this.mode) {
            case 3:
                sb.append("client");
                break;
            case 4:
                sb.append("server");
                break;
            case 5:
                sb.append("broadcast");
                break;
            default:
                sb.append(this.mode);
        }

        sb.append(", leap-indicator=");
        sb.append(this.leapIndicator);

        sb.append(", stratum=");
        sb.append(this.stratum);

        sb.append(", poll-interval=");
        sb.append(this.pollInterval);

        sb.append(", precision=");
        sb.append(this.precision);

        sb.append(", root-delay=");
        sb.append(this.rootDelay);

        sb.append(", root-dispersion=");
        sb.append(this.rootDispersion);

        sb.append(", reference-identifier=");
        sb.append(this.getRefIDAsString());

        sb.append(", reference-timestamp=");
        sb.append(toString(this.referenceTimestamp));
        sb.append(", originate-timestamp=");
        sb.append(toString(this.originateTimestamp));
        sb.append(", receive-timestamp=");
        sb.append(toString(this.receiveTimestamp));
        sb.append(", transmit-timestamp=");
        sb.append(toString(this.transmitTimestamp));
        sb.append(']');

        return sb.toString();

    }

    /**
     * <p>Converts given NTP-timestamp to a microsecond value relative to
     * the UNIX- epoch. </p>
     *
     * @param   ntpTimestamp    NTP-timestamp (seconds since 1900-01-01)
     * @return  long-Wert relative to 1970-01-01 in microseconds without
     *          leap seconds
     * @since   2.1
     */
    /*[deutsch]
     * <p>Wandelt den NTP-Timestamp in einen Mikrosekundenwert relativ
     * zur UNIX-Epoche um. </p>
     *
     * @param   ntpTimestamp    NTP-Timestamp (Sekunden seit 1.1.1900)
     * @return  long-Wert relativ zum 1. Januar 1970 (Mikrosekundenwert
     *          ohne Z&auml;hlung von UTC-Schaltsekunden)
     * @since   2.1
     */
    public static long convert(double ntpTimestamp) {

        return (long) ((ntpTimestamp * MIO) - OFFSET_1900);

    }

    /**
     * <p>Liefert die rohen Bytes dieser {@code SntpMessage} zum Versenden
     * an den NTP-Server. </p>
     *
     * @return  byte-Array
     */
    byte[] getBytes() {

        byte[] ret = new byte[48];

        ret[0] =
            (byte) (
                (this.leapIndicator << 6)
                | (this.version << 3)
                | this.mode
            );

        // wird nie ausgewertet, da nur auf die Client-Message angewandt
        if (this.mode != 3) {

            ret[1] = (byte) this.stratum;
            ret[2] = (byte) this.pollInterval;
            ret[3] = this.precision;

            int rdelay = (int) (this.rootDelay * BIT16);
            ret[4] = (byte) ((rdelay >> 24) & 0xFF);
            ret[5] = (byte) ((rdelay >> 16) & 0xFF);
            ret[6] = (byte) ((rdelay >> 8) & 0xFF);
            ret[7] = (byte) (rdelay & 0xFF);

            long rdisp = (long) (this.rootDispersion * BIT16);
            ret[8] = (byte) ((rdisp >> 24) & 0xFF);
            ret[9] = (byte) ((rdisp >> 16) & 0xFF);
            ret[10] = (byte) ((rdisp >> 8) & 0xFF);
            ret[11] = (byte) (rdisp & 0xFF);

            for (int i = 0; i < 4; i++) {
                ret[12 + i] = this.refID[i];
            }

            encode(ret, 16, this.referenceTimestamp);
            encode(ret, 24, this.originateTimestamp);
            encode(ret, 32, this.receiveTimestamp);

        }

        encode(ret, 40, this.transmitTimestamp);

        return ret;

    }

    /**
     * <p>Liefert den aktuellen lokalen NTP-Timestamp. </p>
     *
     * @return  aktuelle Zeit im NTP-Format (Sekunden seit 1.1.1900)
     */
    static double getLocalTimestamp() {

        long ut1 = SystemClock.MONOTONIC.currentTimeInMicros();
        return ((ut1 + OFFSET_1900) / MIO_AS_DOUBLE);

    }

    private String getRefIDAsString() {

        StringBuilder sb = new StringBuilder();

        if (
            (this.stratum == 0)
            || (this.stratum == 1)
        ) {

            for (int i = 0; i < 4; i++) {
                char c = (char) this.refID[i];
                if (c == 0) {
                    break;
                } else {
                    sb.append(c);
                }
            }

        } else if (this.version == 3) {

            sb.append(toUnsigned(this.refID[0]));
            sb.append('.');
            sb.append(toUnsigned(this.refID[1]));
            sb.append('.');
            sb.append(toUnsigned(this.refID[2]));
            sb.append('.');
            sb.append(toUnsigned(this.refID[3]));

        } else if (this.version == 4) {

            int ref = 0;

            for (int i = 0; i < 4; i++) {
                int unsigned = (this.refID[i] & 0xFF);
                ref |= (unsigned << (24 - i * 8));
            }

            return Integer.toHexString(ref);

        } else {

            sb.append('?');

        }

        return sb.toString();

    }

    private static String toString(double ntpTimestamp) {

        long micros = convert(ntpTimestamp);
        Moment m =
            Moment.of(
                micros / MIO,
                (int) ((micros % MIO) * 1000),
                TimeScale.POSIX);
        return m.toString();

    }

    // NTP-Timestamp aus byte-Array dekodieren
    private static double decode(
        byte[] data,
        int pointer
    ) {

        long ntp = 0L;

        for (int i = 0; i < 8; i++) {
            long unsigned = (data[i + pointer] & 0xFF);
            ntp |= (unsigned << (56 - i * 8));
        }

        // Festkomma vor Bit 32, deshalb Bits nach rechts schieben
        long integer = ((ntp >>> 32) & 0xFFFFFFFFL);
        long fraction = (((ntp & 0xFFFFFFFFL) * MIO) >>> 32);
        long off = (((integer & 0x80000000L) == 0) ? OFFSET_2036 : OFFSET_1900);
        long ut1 = (integer * MIO) + fraction - off;

        return ((ut1 + OFFSET_1900) / MIO_AS_DOUBLE);

    }

    // NTP-Timestamp als byte-Array kodieren
    private static void encode(
        byte[] data,
        int pointer,
        double timestamp
    ) {

        // UT1-Mikrosekunden konstruieren
        long ut1 = convert(timestamp);
        boolean before2036 = (ut1 + OFFSET_2036 < 0);
        long micros;

        if (before2036) {
            micros = ut1 + OFFSET_1900;
        } else {
            micros = ut1 + OFFSET_2036; // siehe RFC 4330, Abschnitt 3
        }

        // Festkomma vor Bit 32, deshalb Bits nach links schieben
        long integer = micros / MIO;
        long fraction = ((micros % MIO) << 32) / MIO;

        if (before2036) {
            integer |= 0x80000000L; // siehe RFC 4330, Abschnitt 3
        }

        long ntp = ((integer << 32) | fraction);

        // Binäre Darstellung erzeugen
        for (int i = 7; i >= 0; i--) {

            data[i + pointer] = (byte) (ntp & 0xFF);
            ntp >>>= 8;

        }

        // niedrigstes Byte nur als Zufallszahl (siehe RFC 4330, Abschnitt 3)
        data[7 + pointer] = (byte) (Math.random() * BIT08);

    }

    // Byte-Konvertierung
    private static short toUnsigned(byte b) {

        short unsignedByte = b;

        if ((b & 0x80) == 0x80) {
            unsignedByte = (short) (128 + (b & 0x7f));
        }

        return unsignedByte;

    }

}
