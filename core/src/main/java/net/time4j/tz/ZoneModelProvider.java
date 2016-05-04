/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ZoneModelProvider.java) is part of project Time4J.
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

package net.time4j.tz;

import java.util.Map;
import java.util.Set;


/**
 * <p>SPI interface which encapsulates the timezone repository and
 * provides all necessary data and transitions for a given timezone id. </p>
 *
 * <p>Implementations are usually stateless and should normally not
 * try to manage a cache. Instead Time4J uses its own cache. The
 * fact that this interface is used per {@code java.util.ServiceLoader}
 * requires a concrete implementation to offer a public no-arg
 * constructor. </p>
 *
 * @author  Meno Hochschild
 * @since   3.20/4.16
 * @see     java.util.ServiceLoader
 */
/*[deutsch]
 * <p>SPI-Interface, das eine Zeitzonendatenbank kapselt und passend zu
 * einer Zeitzonen-ID (hier als String statt als {@code TZID}) die
 * Zeitzonendaten liefert. </p>
 *
 * <p>Implementierungen sind in der Regel zustandslos und halten keinen
 * Cache. Letzterer sollte normalerweise der Klasse {@code Timezone}
 * vorbehalten sein. Weil dieses Interface mittels eines
 * {@code java.util.ServiceLoader} genutzt wird, mu&szlig; eine
 * konkrete Implementierung einen &ouml;ffentlichen Konstruktor ohne
 * Argumente definieren. </p>
 *
 * @author  Meno Hochschild
 * @since   3.20/4.16
 * @see     java.util.ServiceLoader
 */
public interface ZoneModelProvider {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Gets all available and supported timezone identifiers. </p>
     *
     * @return  unmodifiable set of timezone ids
     * @see     java.util.TimeZone#getAvailableIDs()
     */
    /*[deutsch]
     * <p>Liefert alle verf&uuml;gbaren Zeitzonenkennungen. </p>
     *
     * @return  unmodifiable set of timezone ids
     * @see     java.util.TimeZone#getAvailableIDs()
     */
    Set<String> getAvailableIDs();

    /**
     * <p>Gets an alias table whose keys represent alternative identifiers
     * mapped to other aliases or finally canonical timezone IDs.. </p>
     *
     * <p>Example: &quot;PST&quot; =&gt; &quot;America/Los_Angeles&quot;. </p>
     *
     * @return  map from all timezone aliases to canoncial ids
     */
    /*[deutsch]
     * <p>Liefert eine Alias-Tabelle, in der die Schl&uuml;ssel alternative
     * Zonen-IDs darstellen und in der die zugeordneten Werte wieder
     * Aliasnamen oder letztlich kanonische Zonen-IDs sind. </p>
     *
     * <p>Beispiel: &quot;PST&quot; =&gt; &quot;America/Los_Angeles&quot;. </p>
     *
     * @return  map from all timezone aliases to canoncial ids
     */
    Map<String, String> getAliases();

    /**
     * <p>Loads an offset transition history for given timezone id. </p>
     *
     * <p>The argument never contains the provider name as prefix. It is
     * instead the part after the &quot;~&quot;-char (if not absent). </p>
     *
     * @param   zoneID      timezone id (i.e. &quot;Europe/London&quot;)
     * @return  timezone history or {@code null} if there are no data
     * @throws  IllegalArgumentException if given id is wrong
     * @throws  IllegalStateException if timezone database is broken
     * @see     #getAvailableIDs()
     * @see     #getAliases()
     * @see     java.util.TimeZone#getTimeZone(String)
     */
    /*[deutsch]
     * <p>L&auml;dt die Zeitzonendaten zur angegebenen Zonen-ID. </p>
     *
     * <p>Das erste Argument enth&auml;lt nie den Provider-Namen als
     * Pr&auml;fix. Stattdessen ist es der Teil nach dem Zeichen
     * &quot;~&quot; (falls vorhanden). </p>
     *
     * @param   zoneID      timezone id (i.e. &quot;Europe/London&quot;)
     * @return  timezone history or {@code null} if there are no data
     * @throws  IllegalArgumentException if given id is wrong
     * @throws  IllegalStateException if timezone database is broken
     * @see     #getAvailableIDs()
     * @see     #getAliases()
     * @see     java.util.TimeZone#getTimeZone(String)
     */
    TransitionHistory load(String zoneID);

    /**
     * <p>Determines if in case of a failed search another {@code ZoneModelProvider}
     * should be called as alternative with possibly different rules. </p>
     *
     * <p>The special name &quot;DEFAULT&quot; can be used to denote the
     * default zone provider. Note that the fallback provider will only affect
     * the rules but not the id or display names of a new timezone. </p>
     *
     * @return  name of alternative provider or empty if no fallback happens
     * @see     #load(String)
     */
    /*[deutsch]
     * <p>Legt fest, ob ein alternativer {@code ZoneModelProvider} mit eventuell
     * anderen Regeln gerufen werden soll, wenn die Suche nach einer Zeitzone
     * erfolglos war. </p>
     *
     * <p>Der spezielle Name &quot;DEFAULT&quot; kann verwendet werden, um
     * den Standard-{@code ZoneModelProvider} anzuzeigen. Zu beachten: Die
     * Alternative wird nur die Regeln betreffen, nicht aber die ID oder
     * Anzeigenamen einer neuen Zeitzone. </p>
     *
     * @return  name of alternative provider or empty if no fallback happens
     * @see     #load(String)
     */
    String getFallback();

    /**
     * <p>Gets the name of the underlying repository. </p>
     *
     * <p>The Olson/IANA-repository (and any provider which makes use of
     * these data (direct or indirect)) has the name &quot;TZDB&quot;.
     * The names &quot;java.util.TimeZone&quot; and &quot;DEFAULT&quot;
     * are reserved and cannot be used. </p>
     *
     * @return  String
     */
    /*[deutsch]
     * <p>Gibt den Namen dieser Zeitzonendatenbank an. </p>
     *
     * <p>Die Olson/IANA-Zeitzonendatenbank hat den Namen
     * &quot;TZDB&quot;. Jeder {@code ZoneModelProvider}, der sich auf diese
     * Daten bezieht, mu&szlig; diesen Namen haben. Die Namen
     * &quot;java.util.TimeZone&quot; and &quot;DEFAULT&quot; sind
     * reserviert und k&ouml;nnen nicht verwendet werden. </p>
     *
     * @return  String
     */
    String getName();

    /**
     * <p>Describes the location or source of the repository. </p>
     *
     * @return  String which refers to an URI or empty if unknown
     */
    /*[deutsch]
     * <p>Beschreibt die Quelle der Zeitzonendatenbank. </p>
     *
     * @return  String which refers to an URI or empty if unknown
     */
    String getLocation();

    /**
     * <p>Queries the version of the underlying repository. </p>
     *
     * <p>In most cases the version has the Olson format starting with
     * a four-digit year number followed by a small letter in range
     * a-z. </p>
     *
     * @return  String (for example &quot;2011n&quot;) or empty if unknown
     */
    /*[deutsch]
     * <p>Liefert die Version der Zeitzonendatenbank. </p>
     *
     * <p>Meist liegt die Version im Olson-Format vor. Dieses Format sieht
     * als Versionskennung eine 4-stellige Jahreszahl gefolgt von einem
     * Buchstaben im Bereich a-z vor. </p>
     *
     * @return  String (for example &quot;2011n&quot;) or empty if unknown
     */
    String getVersion();

    /**
     * <p>Queries if specific zone names are to be used. </p>
     *
     * @return  specific name repository or {@code null} if not relevant
     */
    /*[deutsch]
     * <p>Ermittelt, ob spezifische Zeitzonennamen zu verwenden sind. </p>
     *
     * @return  specific name repository or {@code null} if not relevant
     */
    ZoneNameProvider getSpecificZoneNameRepository();

}
