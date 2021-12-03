/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (NetTimeConfiguration.java) is part of project Time4J.
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
 * <p>Contains the configuration parameters of a connection to an internet
 * time server. </p>
 *
 * <p>Implementations of this <strong>SPI-interface</strong> must be
 * <i>immutable</i> or at least <i>threadsafe</i>. Instances can be
 * created manually or via a {@code ServiceLoader}. </p>
 *
 * <p><strong>Note:</strong> All implementations must have a public no-arg constructor. </p>
 *
 * @author  Meno Hochschild
 * @since   2.1
 * @see     NetTimeConnector
 * @see     java.util.ServiceLoader
 */
/*[deutsch]
 * <p>Enth&auml;lt die Konfiguration einer Verbindung zu einem
 * Internet-Uhrzeit-Server. </p>
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
 * @see     NetTimeConnector
 * @see     java.util.ServiceLoader
 */
public interface NetTimeConfiguration {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Default timeout is {@code 60} seconds. </p>
     */
    /*[deutsch]
     * <p>Standardwartezeit: {@code 60} Sekunden. </p>
     */
    static final int DEFAULT_CONNECTION_TIMEOUT = 60;

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Yields the internet address of a time server. </p>
     *
     * <p>Possible values are also addresses of older time servers which
     * still support the elder protocol (RFC 867), for example: </p>
     *
     * <ul><li>time.nist.gov (US-government)</li>
     * <li>time.ien.it (electrotechn. institute in Italy)</li></ul>
     *
     * <p>Alternatively, addresses of modern NTP-servers are permitted
     * which support the protocols NTP3 or NTP4: </p>
     *
     * <ul><li>ptbtime1.ptb.de</li>
     * <li>ptbtime2.ptb.de</li>
     * <li>ntp2.lrz-muenchen.de</li></ul>
     *
     * @return  time server address
     * @since   2.1
     */
    /*[deutsch]
     * <p>Liefert die Internet-Adresse eines Uhrzeit-Servers. </p>
     *
     * <p>M&ouml;gliche Eigenschaftenwerte sind auch die Adressen von
     * Uhrzeit-Servern im Netz, die noch das &auml;ltere Protokoll
     * unterst&uuml;tzen (RFC 867), zum Beispiel: </p>
     *
     * <ul><li>time.nist.gov (US-Regierung)</li>
     * <li>time.ien.it (elektrotechn. Institut in Italien)</li></ul>
     *
     * <p>Alternativ sind die Adressen von moderneren NTP-Servern
     * m&ouml;glich, die die Protokolle NTP3 oder NTP4 unterst&uuml;tzen: </p>
     *
     * <ul><li>ptbtime1.ptb.de</li>
     * <li>ptbtime2.ptb.de</li>
     * <li>ntp2.lrz-muenchen.de</li></ul>
     *
     * @return  Uhrzeit-Server-Adresse
     * @since   2.1
     */
    String getTimeServerAddress();

    /**
     * <p>Yields the port of a time server. </p>
     *
     * <p>The DAYTIME-protocol assumes as default value {@code 13} while
     * NTP-protocols usually use the value {@code 123}. </p>
     *
     * @return  int
     * @since   2.1
     */
    /*[deutsch]
     * <p>Liefert den Port des Uhrzeit-Servers. </p>
     *
     * <p>Als Standardwerte werden f&uuml;r das DAYTIME-Protokoll der Wert
     * {@code 13} und f&uuml;r die NTP-Protokolle der Wert {@code 123}
     * verwendet. </p>
     *
     * @return  int
     * @since   2.1
     */
    int getTimeServerPort();

    /**
     * <p>Determines the maximum time out when connecting to an internet
     * time server. </p>
     *
     * <p>The value {@code 0} indicates an unlimited waiting time. The
     * default value is internally {@code 60} seconds. </p>
     *
     * @return  maximum waiting time in seconds {@code >= 0}
     * @since   2.1
     * @see     #DEFAULT_CONNECTION_TIMEOUT
     */
    /*[deutsch]
     * <p>Gibt die maximale Wartezeit beim Verbindungsaufbau an. </p>
     *
     * <p>Der Wert {@code 0} zeigt eine unbegrenzte Wartezeit an. Als
     * Standardwert gilt intern immer {@code 60} Sekunden. </p>
     *
     * @return  maximum waiting time in seconds {@code >= 0}
     * @since   2.1
     * @see     #DEFAULT_CONNECTION_TIMEOUT
     */
    int getConnectionTimeout();

    /**
     * <p>Determines the time window within which an existing shift between
     * the local clock and the internet clock will be synchronized (after
     * a successful connection) if the local clock is too quick. </p>
     *
     * <p>The value {@code 0} causes the immediate synchronization and is the
     * default. Else the local clock will only be synchronized with the
     * internet clock after this clock shift window has been passed. Within
     * this time window the local clock will be slowly adjusted to the internet
     * clock. The time window should always be choosen such that it is smaller
     * than the time between two connections. Its main purpose is slowing
     * down a local clock such that applications will not notice any
     * backwards running time within the scope of expected precision. </p>
     *
     * <p>If the local clock is too slow however then the synchronization
     * will happen immediately, and this configuration parameter is not
     * applied. </p>
     *
     * @return  time window in seconds ({@code >= 0})
     * @since   2.1
     */
    /*[deutsch]
     * <p>Legt das Zeitfenster fest, innerhalb dessen nach einer erfolgreichen
     * Verbindung ein bestehender Offset zwischen lokaler Uhr und Internet-Uhr
     * synchronisiert wird, wenn die lokale Uhr der Internet-Uhr vorauszueilen
     * droht. </p>
     *
     * <p>Der Wert {@code 0} f&uuml;hrt zur sofortigen Anpassung des Offset
     * und ist Standardvorgabe. Sonst wird die lokale Uhr erst nach Ablauf
     * des Zeitfensters mit der Internet-Uhr nachgezogen sein. W&auml;hrend
     * des Zeitfensters findet eine graduelle Anpassung statt. Das Zeitfenster
     * sollte immer kleiner als die Zeit zwischen zwei Verbindungen sein
     * und dient dazu, eine lokale Uhr so langsam abzubremsen, da&szlig;
     * Anwendungen innerhalb der von ihnen erwarteten Genauigkeit keine
     * r&uuml;ckl&auml;ufige Zeit bemerken. War die lokale Uhr hingegen zu
     * langsam, findet immer eine sofortige Anpassung statt, und diese
     * Konfigurationseigenschaft spielt dann keine Rolle. </p>
     *
     * @return  Zeitfenster in Sekunden ({@code >= 0})
     * @since   2.1
     */
    int getClockShiftWindow();

}
