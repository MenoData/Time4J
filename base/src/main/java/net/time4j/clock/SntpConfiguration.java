/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SntpConfiguration.java) is part of project Time4J.
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


/**
 * <p>Represents a configuration for a connection to a NTP-Server. </p>
 *
 * <p>Implementations of this <strong>SPI-interface</strong> must be
 * <i>immutable</i> or at least <i>threadsafe</i>. Instances can be
 * created manually or via a {@code ServiceLoader}. </p>
 *
 * <p><strong>Note:</strong> All implementations must have a public
 * no-arg constructor. </p>
 *
 * @author  Meno Hochschild
 * @since   2.1
 * @see     java.util.ServiceLoader
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Konfiguration f&uuml;r eine Verbindung zu
 * einem NTP-Server. </p>
 *
 * <p>Implementierungen dieses <strong>SPI-Interface</strong> m&uuml;ssen
 * <i>immutable</i> oder wenigstens <i>threadsafe</i> sein. Instanzen werden
 * manuell oder &uuml;ber einen {@code ServiceLoader}-Mechanismus erzeugt. </p>
 *
 * <p><strong>Hinweis:</strong> Alle Implementierungen m&uuml;ssen einen
 * &ouml;ffentlichen und parameterlosen Konstruktor haben. </p>
 *
 * @author  Meno Hochschild
 * @since   2.1
 * @see     java.util.ServiceLoader
 */
public interface SntpConfiguration
    extends NetTimeConfiguration {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Determines if NTP3 or NTP4 should be used. </p>
     *
     * @return  {@code true} if NTP4 shall be used else {@code false}
     * @since   2.1
     */
    /*[deutsch]
     * <p>Bestimmt, ob NTP3 oder NTP4 verwendet werden soll. </p>
     *
     * @return  {@code true} wenn NTP4 Verwendung findet, sonst {@code false}
     * @since   2.1
     */
    boolean isNTP4();

    /**
     * <p>Determines the rate by which a SNTP-client sends queries during
     * a connection for the purpose of the calculation of an arithmetic
     * mean value. </p>
     *
     * <p>Default value is {@code 60 * 4} (that is 4 minutes). This method
     * is only relevant if {@code getRequestCount()} yields at least the
     * value {@code 2}. One single connection request can contain several
     * queries this way. Note that some servers will block requests if
     * they happen too often. </p>
     *
     * @return  time distance between two messages in seconds as positive number
     * @since   2.1
     */
    /*[deutsch]
     * <p>Bestimmt die Rate, mit der ein SNTP-Client w&auml;hrend einer
     * Verbindung Anfragen sendet, um dann &uuml;ber die Ergebnisse zu
     * mitteln. </p>
     *
     * <p>Standardwert ist {@code 60 * 4} (respektive 4 Minuten). Diese Methode
     * ist nur dann von Bedeutung, wenn {@code getRequestCount()} mindestens
     * den Wert {@code 2} liefert. Ein einzelnes Verbindungsereignis (CONNECT)
     * kann also mehrere Teilanfragen enthalten. </p>
     *
     * @return  zeitlicher Abstand zwischen zwei Nachrichten in Sekunden als
     *          positive Zahl
     * @since   2.1
     */
    int getRequestInterval();

    /**
     * <p>Determines how often a SNTP-Client will send queries during a
     * connection for the purpose of the calculation of an arithmetic
     * mean value. </p>
     *
     * <p>Default value is {@code 1} (single query). The value {@code 0}
     * is also permitted and effectively stops any query. If any bigger
     * value ({@code > 1}) is configured then an asynchronous connection
     * to the NTP-server is recommended. One single connection request can
     * contain several queries this way. Note that some servers will block
     * requests if they happen too often. </p>
     *
     * @return  number in range {@code 0 <= x < 1000}
     * @since   2.1
     * @see     SntpConnector#connect()
     */
    /*[deutsch]
     * <p>Bestimmt, wie oft ein SNTP-Client w&auml;hrend einer Verbindung
     * Anfragen sendet, um dann &uuml;ber die Ergebnisse zu mitteln. </p>
     *
     * <p>Standardwert ist {@code 1} (einmalige Anfrage). Der Wert {@code 0}
     * ist auch zugelassen und f&uuml;hrt zum Abschalten der Anfragen. Wird
     * ein gr&ouml;&szlig;erer Wert ({@code > 1}) konfiguriert, empfiehlt sich
     * eine asynchrone Verbindung zum NTP-Server in einem separaten Thread.
     * Ein einzelnes Verbindungsereignis (CONNECT) kann also mehrere Anfragen
     * enthalten. </p>
     *
     * @return  Zahl im Bereich {@code 0 <= x < 1000}
     * @since   2.1
     * @see     SntpConnector#connect()
     */
    short getRequestCount();

}
