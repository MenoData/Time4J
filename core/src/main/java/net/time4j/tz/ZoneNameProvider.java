/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ZoneNameProvider.java) is part of project Time4J.
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

import java.util.Locale;
import java.util.Set;


/**
 * <p>SPI interface which encapsulates the timezone name repository. </p>
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
 * <p>SPI-Interface, das eine Datenbank f&uuml;r Zeitzonennamen kapselt. </p>
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
public interface ZoneNameProvider {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Gets a {@code Set} of preferred timezone IDs for given
     * ISO-3166-country code. </p>
     *
     * <p>This information is necessary to enable parsing of ambivalent
     * timezone names. </p>
     *
     * @param   locale      ISO-3166-alpha-2-country to be evaluated
     * @param   smart       if {@code true} then try to select zone ids such
     *                      that there is only one preferred id per zone name
     * @return  unmodifiable set of preferred timezone ids
     */
    /*[deutsch]
     * <p>Liefert die f&uuml;r einen gegebenen ISO-3166-L&auml;ndercode
     * bevorzugten Zeitzonenkennungen. </p>
     *
     * <p>Diese Information ist f&uuml;r die Interpretation von mehrdeutigen Zeitzonennamen
     * notwendig. </p>
     *
     * @param   locale      ISO-3166-alpha-2-country to be evaluated
     * @param   smart       if {@code true} then try to select zone ids such
     *                      that there is only one preferred id per zone name
     * @return  unmodifiable set of preferred timezone ids
     */
    Set<String> getPreferredIDs(
        Locale locale,
        boolean smart
    );

    /**
     * <p>Returns the name of this timezone suitable for presentation to
     * users in given style and locale. </p>
     *
     * <p>The first argument never contains the provider name as prefix. It is
     * instead the part after the &quot;~&quot;-char (if not absent). </p>
     *
     * @param   zoneID      timezone id (i.e. &quot;Europe/London&quot;)
     * @param   style       name style
     * @param   locale      language setting
     * @return  localized timezone name for display purposes
     *          or empty if not supported
     * @see     java.util.TimeZone#getDisplayName(boolean,int,Locale)
     *          java.util.TimeZone.getDisplayName(boolean,int,Locale)
     */
    /*[deutsch]
     * <p>Liefert den anzuzeigenden Zeitzonennamen. </p>
     *
     * <p>Das erste Argument enth&auml;lt nie den Provider-Namen als
     * Pr&auml;fix. Stattdessen ist es der Teil nach dem Zeichen
     * &quot;~&quot; (falls vorhanden). </p>
     *
     * @param   zoneID      timezone id (i.e. &quot;Europe/London&quot;)
     * @param   style       name style
     * @param   locale      language setting
     * @return  localized timezone name for display purposes
     *          or empty if not supported
     * @see     java.util.TimeZone#getDisplayName(boolean,int,Locale)
     *          java.util.TimeZone.getDisplayName(boolean,int,Locale)
     */
    String getDisplayName(
        String zoneID,
        NameStyle style,
        Locale locale
    );

    /**
     * <p>Obtains a typical localized format pattern in minute precision. </p>
     *
     * <p>The character &quot;&#x00B1;&quot; represents a localized offset sign. And the double letters
     * &quot;hh&quot; and &quot;mm&quot; represent localized digits of hour respective minute part of the
     * offset. All other characters are to be interpreted as literals. Many locales return the format
     * &quot;GMT&#x00B1;hh:mm&quot;. </p>
     *
     * @param   zeroOffset  Is the offset to be formatted equal to UTC?
     * @param   locale      language setting
     * @return  localized offset pattern
     * @since   3.23/4.19
     */
    /*[deutsch]
     * <p>Ermittelt ein typisches sprachspezifisches Formatmuster in Minutengenauigkeit. </p>
     *
     * <p>Das Zeichen &quot;&#x00B1;&quot; repr&auml;sentiert ein lokalisiertes Vorzeichen. Und die
     * gedoppelten Buchstaben &quot;hh&quot; und &quot;mm&quot; repr&auml;sentieren lokalisierte
     * Dezimalziffern des Stunden- bzw. Minutenteils dieser Instanz. Alle anderen Zeichen m&uuml;ssen
     * als Literale interpretiert werden. Viele Sprachen liefern das Format &quot;GMT&#x00B1;hh:mm&quot;. </p>
     *
     * @param   zeroOffset  Is the offset to be formatted equal to UTC?
     * @param   locale      language setting
     * @return  localized offset pattern
     * @since   3.23/4.19
     */
    String getStdFormatPattern(
        boolean zeroOffset,
        Locale locale
    );

}
