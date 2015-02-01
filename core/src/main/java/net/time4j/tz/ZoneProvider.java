/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ZoneProvider.java) is part of project Time4J.
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
 * provides all necessary data for a given timezone id. </p>
 *
 * <p>Implementations are usually stateless and should normally not
 * try to manage a cache. Instead Time4J uses its own cache. The
 * fact that this interface is used per {@code java.util.ServiceLoader}
 * requires a concrete implementation to offer a public no-arg
 * constructor. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
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
 * @since   2.0
 * @see     java.util.ServiceLoader
 */
public interface ZoneProvider {

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
     * <p>Example: &quot;PST&quot; => &quot;America/Los_Angeles&quot;. </p>
     *
     * @return  map from all timezone aliases to canoncial ids
     */
    /*[deutsch]
     * <p>Liefert eine Alias-Tabelle, in der die Schl&uuml;ssel alternative
     * Zonen-IDs darstellen und in der die zugeordneten Werte wieder
     * Aliasnamen oder letztlich kanonische Zonen-IDs sind. </p>
     *
     * <p>Beispiel: &quot;PST&quot; => &quot;America/Los_Angeles&quot;. </p>
     *
     * @return  map from all timezone aliases to canoncial ids
     */
    Map<String, String> getAliases();

    /**
     * <p>Loads an offset transition table for given timezone id. </p>
     *
     * <p>This callback method has a second argument which indicates if
     * Time4J wants this method to return exactly matching data (default)
     * or permits the use of aliases (only possible if the method
     * {@code isFallbackEnabled()} returns {@code true}). </p>
     *
     * @param   zoneID      timezone id (i.e. &quot;Europe/London&quot;)
     * @param   fallback    fallback allowed if a timezone id cannot be
     *                      found, not even by alias?
     * @return  timezone history or {@code null} if there are no data
     * @throws  IllegalStateException if timezone database is broken
     * @see     #getAvailableIDs()
     * @see     #getAliases()
     * @see     #isFallbackEnabled()
     * @see     java.util.TimeZone#getTimeZone(String)
     */
    /*[deutsch]
     * <p>L&auml;dt die Zeitzonendaten zur angegebenen Zonen-ID. </p>
     *
     * <p>Diese Methode wird von {@code Timezone} aufgerufen. Das zweite
     * Argument ist normalerweise {@code false}, so da&szlig; es sich um
     * eine exakte Suchanforderung handelt. Nur wenn die Methode
     * {@code isFallbackEnabled()} den Wert {@code true} zur&uuml;ckgibt
     * und vorher weder die exakte Suche noch die Alias-Suche erfolgreich
     * waren, kann ein erneuter Aufruf mit dem zweiten Argument
     * {@code true} erfolgen. </p>
     *
     * @param   zoneID      timezone id (i.e. &quot;Europe/London&quot;)
     * @param   fallback    fallback allowed if a timezone id cannot be
     *                      found, not even by alias?
     * @return  timezone history or {@code null} if there are no data
     * @throws  IllegalStateException if timezone database is broken
     * @see     #getAvailableIDs()
     * @see     #getAliases()
     * @see     #isFallbackEnabled()
     * @see     java.util.TimeZone#getTimeZone(String)
     */
    TransitionHistory load(
        String zoneID,
        boolean fallback
    );

    /**
     * <p>Determines if in case of a failed search another timezone should
     * be permitted as alternative with possibly different rules. </p>
     *
     * @return  boolean
     * @see     #load(String, boolean)
     */
    /*[deutsch]
     * <p>Soll eine alternative Zeitzone mit eventuell anderen Regeln
     * geliefert werden, wenn die Suche nach einer Zeitzone erfolglos
     * war? </p>
     *
     * @return  boolean
     * @see     #load(String, boolean)
     */
    boolean isFallbackEnabled();

    /**
     * <p>Gets the name of the underlying repository. </p>
     *
     * <p>The Olson/IANA-repository has the name
     * &quot;TZDB&quot;. </p>
     *
     * @return  String
     */
    /*[deutsch]
     * <p>Gibt den Namen dieser Zeitzonendatenbank an. </p>
     *
     * <p>Die Olson/IANA-Zeitzonendatenbank hat den Namen
     * &quot;TZDB&quot;. </p>
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

}
