/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (WindowsZone.java) is part of project Time4J.
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

package net.time4j.tz.other;

import net.time4j.format.internal.FormatUtils;
import net.time4j.tz.NameStyle;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZoneNameProvider;
import net.time4j.tz.spi.WinZoneProviderSPI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;


/**
 * <p>Represents a windows timezone name which can be mapped to an IANA/Olson-ID
 * using a territory information. </p>
 *
 * <p>Example: </p>
 *
 * <pre>
 *  WindowsZone wzn = WindowsZone.of(&quot;Eastern Standard Time&quot;);
 *  TZID winzone = wzn.resolveSmart(Locale.US);
 *  System.out.println(winzone.canonical());
 *  // output: WINDOWS~America/New_York
 * </pre>
 *
 * <p>The <i>reverse</i> way: </p>
 *
 * <pre>
 *  String winzone = WindowsZone.toString(&quot;America/New_York&quot;, Locale.US);
 *  System.out.println(winzone);
 *  // output: Eastern Standard Time
 * </pre>
 *
 * <p><strong>Note:</strong> A perfect roundtrip is often not possible because there are many more IANA-zone
 * identifiers than windows zones. For best results, it is recommended to use the tzdata-module and not the
 * standard timezone data of the underlying platform. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Windows-Zeitzone, die mit Hilfe einer
 * L&auml;nderinformation zu einer IANA/Olson-ID umgeformt werden kann. </p>
 *
 * <p>Beispiel: </p>
 *
 * <pre>
 *  WindowsZone wzn = WindowsZone.of(&quot;Eastern Standard Time&quot;);
 *  TZID winzone = wzn.resolveSmart(Locale.US);
 *  System.out.println(winzone.canonical());
 *  // output: WINDOWS~America/New_York
 * </pre>
 *
 * <p>Der <i>umgekehrte</i> Weg: </p>
 *
 * <pre>
 *  String winzone = WindowsZone.toString(&quot;America/New_York&quot;, Locale.US);
 *  System.out.println(winzone);
 *  // output: Eastern Standard Time
 * </pre>
 *
 * <p><strong>Hinweis:</strong> Ein perfekter Kreisweg ist oft nicht m&ouml;glich, weil es viel mehr
 * IANA-Zeitzonenkennungen als Windows-Zeitzonen gibt. Um beste Ergebnisse zu erzielen, wird empfohlen,
 * das tzdata-Modul und nicht die Standardzeitzonendaten der jeweiligen Plattform zu nutzen. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 */
public final class WindowsZone
    implements Comparable<WindowsZone>, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final Locale WORLDWIDE = new Locale("", "001");
    private static final long serialVersionUID = -6071278077083785308L;

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  name of windows zone
     */
    private final String name;

    //~ Konstruktoren -----------------------------------------------------

    private WindowsZone(String name) {
        super();

        this.name = name;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Yields all available names of windows zones. </p>
     *
     * @return  unmodifiable set of zone names for Windows
     * @since   2.3
     */
    /*[deutsch]
     * <p>Liefert alle verf&uuml;gbaren Namen von Windows-Zeitzonen. </p>
     *
     * @return  unmodifiable set of zone names for Windows
     * @since   2.3
     */
    public static Set<String> getAvailableNames() {

        return WinZoneProviderSPI.NAME_BASED_MAP.keySet();

    }

    /**
     * <p>Creates a name reference to a windows zone. </p>
     *
     * @param   name    standardized windows zone name
     * @return  new instance of {@code WindowsZone}
     * @throws  IllegalArgumentException if given name is not supported
     * @since   2.2
     * @see     #getAvailableNames()
     * @see     #toString()
     */
    /*[deutsch]
     * <p>Erzeugt einen Namensbezug zu einer Windows-Zeitzone. </p>
     *
     * @param   name    standardized windows zone name
     * @return  new instance of {@code WindowsZone}
     * @throws  IllegalArgumentException if given name is not supported
     * @since   2.2
     * @see     #getAvailableNames()
     * @see     #toString()
     */
    public static WindowsZone of(String name) {

        check(name);
        return new WindowsZone(name);

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof WindowsZone) {
            WindowsZone that = (WindowsZone) obj;
            return this.name.equals(that.name);
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return this.name.hashCode();

    }

    /**
     * <p>Returns the name of this windows zone reference. </p>
     *
     * @return  name of windows zone
     */
    /*[deutsch]
     * <p>Liefert den Namen dieser Zeitzonenreferenz. </p>
     *
     * @return  name of windows zone
     */
    @Override
    public String toString() {

        return this.name;

    }

    /**
     * <p>Deduces the windows name of given timezone reference and region. </p>
     *
     * @param   tzid    timezone identifier (usually a IANA/Olson-ID like &quot;America/New_York&quot;)
     * @param   locale  regional setting referring to a country
     * @return  name of windows zone, maybe empty if resolving fails
     * @see     #toString(String, Locale)
     * @since   3.41/4.36
     */
    /*[deutsch]
     * <p>Leitet den Windows-Namen aus der angegebenen Zeitzonenreferenz in der jeweiligen Region ab. </p>
     *
     * @param   tzid    timezone identifier (usually a IANA/Olson-ID like &quot;America/New_York&quot;)
     * @param   locale  regional setting referring to a country
     * @return  name of windows zone, maybe empty if resolving fails
     * @see     #toString(String, Locale)
     * @since   3.41/4.36
     */
    public static String toString(
        TZID tzid,
        Locale locale
    ) {

        return toString(tzid.canonical(), locale);

    }

    /**
     * <p>Deduces the windows name of given timezone reference and region. </p>
     *
     * <p>This method first tries all to automatically normalize given timezone identifier
     * before resolving to the windows name. Normalizing works best if the tzdata-module
     * is present. </p>
     *
     * @param   tzid    timezone identifier (usually a IANA/Olson-ID like &quot;America/New_York&quot;)
     * @param   locale  regional setting referring to a country
     * @return  name of windows zone, maybe empty if resolving fails
     * @see     Timezone#normalize(String)
     * @since   3.41/4.36
     */
    /*[deutsch]
     * <p>Leitet den Windows-Namen aus der angegebenen Zeitzonenreferenz in der jeweiligen Region ab. </p>
     *
     * <p>Diese Methode versucht zuerst das Beste, die angegebene Zeitzonenkennung zu normalisieren,
     * bevor der Windows-Name ermittelt wird. Das funktioniert am ehesten, wenn das tzdata-Modul
     * pr&auml;sent ist. </p>
     *
     * @param   tzid    timezone identifier (usually a IANA/Olson-ID like &quot;America/New_York&quot;)
     * @param   locale  regional setting referring to a country
     * @return  name of windows zone, maybe empty if resolving fails
     * @see     Timezone#normalize(String)
     * @since   3.41/4.36
     */
    public static String toString(
        String tzid,
        Locale locale
    ) {

        String zoneID = Timezone.normalize(tzid).canonical();
        ZoneNameProvider znp = new WinZoneProviderSPI();
        String name = znp.getDisplayName(zoneID, NameStyle.LONG_STANDARD_TIME, locale);

        if (name.isEmpty()) {
            name = znp.getDisplayName(zoneID, NameStyle.LONG_STANDARD_TIME, WORLDWIDE);
        }

        return name;

    }

    /**
     * <p>The natural order is based on the lexicographical order of the
     * underlying names of windows zones. </p>
     *
     * @param   other   another windows zone name reference
     * @return  a negative integer, zero, or a positive integer as this object
     *          is less than, equal to, or greater than the specified object.
     */
    /*[deutsch]
     * <p>Die nat&uuml;rliche Ordnung basiert auf der lexikographischen
     * Reihenfolge der zugrundeliegenden Namen von Windows-Zeitzonen. </p>
     *
     * @param   other   another windows zone name reference
     * @return  a negative integer, zero, or a positive integer as this object
     *          is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(WindowsZone other) {

        return this.name.compareTo(other.name);

    }

    /**
     * <p>Resolves this name reference to a set of various zone ids for given
     * country. </p>
     *
     * @param   country     country reference
     * @return  set of ids belonging to this windows zone
     * @since   2.2
     */
    /*[deutsch]
     * <p>L&ouml;st diese Namensreferenz zu einem Satz von Zonen-IDs zum
     * angegebenen Land auf. </p>
     *
     * @param   country     country reference
     * @return  set of ids belonging to this windows zone
     * @since   2.2
     */
    public Set<TZID> resolve(Locale country) {

        Set<TZID> ids = WinZoneProviderSPI.NAME_BASED_MAP.get(this.name).get(FormatUtils.getRegion(country));

        if (ids == null) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(ids);
        }

    }

    /**
     * <p>Resolves this name reference to at most one zone id for given
     * country. </p>
     *
     * <p>Normally windows zones cannot be resolved to one single zone id,
     * but there is usually one preferred zone id based on the fact that
     * the daylight saving rules for this name and given country are often
     * the same for all belonging zone ids in the recent past. This method
     * tries its best to yield a result but applications have to check if
     * a result is available. </p>
     *
     * <p><strong>Important:</strong> The country reference must be given, that is,
     * {@code country.getCountry()} must not be empty otherwise this method
     * will not return any result. </p>
     *
     * @param   country     country reference
     * @return  preferred zone id belonging to this windows zone
     *          or not present if given country is not related to this name
     * @since   5.0
     */
    /*[deutsch]
     * <p>L&ouml;st diese Namensreferenz zu maximal einer Zonen-ID zum
     * angegebenen Land auf. </p>
     *
     * <p>Normalerweise lassen sich Windows-Zeitzonen nicht zu einer eindeutigen
     * Zonen-ID aufl&ouml;sen, aber es gibt gew&ouml;hnlich eine bevorzugte
     * Zeitzonen-ID, deren Zeitumstellungsregeln sich in der j&uuml;ngsten
     * Vergangenheit oft nicht von denen anderer Zeitzonen-IDs der gleichen
     * Windows-Zeitzone unterscheiden. Diese Methode versucht das Beste, um
     * eine solche bevorzugte Zeitzone zu ermitteln, aber Anwendungen sind
     * verpflichtet zu pr&uuml;fen, ob ein Ergebnis existiert. </p>
     *
     * <p><strong>Wichtig:</strong> Die Landesreferenz mu&szlig; angegeben sein,
     * das hei&szlig;t, {@code country.getCountry()} darf nicht leer sein, sonst
     * wird diese Methoden kein Ergebnis liefern. </p>
     *
     * @param   country     country reference
     * @return  preferred zone id belonging to this windows zone
     *          or not present if given country is not related to this name
     * @since   5.0
     */
    public Optional<TZID> resolveSmart(Locale country) {

        Set<TZID> ids = this.resolve(country);

        if (ids.size() != 1) {
            String region = country.getCountry();
            if (!region.isEmpty() && !region.equals("001")) {
                // in case of ambivalence, the 001-region uses a reasonable fallback and preferred default
                ids = WinZoneProviderSPI.NAME_BASED_MAP.get(this.name).get("001");
            }
        }

        if (ids.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(ids.iterator().next()); // assume first entry as most relevant
        }

    }

    /**
     * <p>Yields the repository version. </p>
     *
     * @return  String
     * @since   2.3
     */
    /*[deutsch]
     * <p>Liefert die zugrundeliegende Version der CLDR-Daten. </p>
     *
     * @return  String
     * @since   2.3
     */
    static String getVersion() {

        return WinZoneProviderSPI.WIN_NAME_VERSION;

    }

    private static void check(String name) {

        if (
            name.isEmpty()
            || !WinZoneProviderSPI.NAME_BASED_MAP.keySet().contains(name)
        ) {
            throw new IllegalArgumentException("Unknown windows zone: " + name);
        }

    }

    /**
     * @serialData  Checks the consistency.
     * @param       in      object input stream
     * @throws      ClassNotFoundException if the class of a serialized object could not be found.
     * @throws      IOException if an I/O error occurs.
     * @throws      IllegalArgumentException in case of inconsistencies
     */
    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {

        in.defaultReadObject();
        check(this.name);

    }

}
