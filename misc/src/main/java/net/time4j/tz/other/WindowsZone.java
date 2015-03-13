/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
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

import net.time4j.tz.TZID;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
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
 * @author  Meno Hochschild
 * @since   2.2
 */
public final class WindowsZone
    implements Comparable<WindowsZone>, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    // Map<country, Map<tzid, name>>
    private static final Map<String, Map<String, String>> REPOSITORY;

    // Map<country, Set<tzid>>
    private static final Map<String, Set<String>> PREFERRED_KEYS;

    // Map<name, Map<country, Set<tzid>>>
    private static final Map<String, Map<String, Set<TZID>>> NAME_BASED_MAP;

    // Version of windowsZones.xml
    private static final String VERSION;

    private static final String VKEY = "VERSION";
    private static final long serialVersionUID = -6071278077083785308L;

    static {
        Map<String, Map<String, String>> map = loadData();
        VERSION = map.get(VKEY).keySet().iterator().next();
        map.remove(VKEY);
        REPOSITORY = Collections.unmodifiableMap(map);
        PREFERRED_KEYS = prepareSmartMode();
        NAME_BASED_MAP = prepareResolvers();
    }

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

        return NAME_BASED_MAP.keySet();

    }

    /**
     * <p>Creates a name reference to a windows zone. </p>
     *
     * @param   name    standardized windows zone name
     * @return  new instance of {@code WindowsZone}
     * @throws  IllegalArgumentException if given name is not supported
     * @since   2.2
     * @see     #getAvailableNames()
     */
    /*[deutsch]
     * <p>Erzeugt einen Namensbezug zu einer Windows-Zeitzone. </p>
     *
     * @param   name    standardized windows zone name
     * @return  new instance of {@code WindowsZone}
     * @throws  IllegalArgumentException if given name is not supported
     * @since   2.2
     * @see     #getAvailableNames()
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

        Set<TZID> ids = NAME_BASED_MAP.get(this.name).get(country.getCountry());

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
     * the result is {@code null}. </p>
     *
     * @param   country     country reference
     * @return  preferred zone id belonging to this windows zone
     *          or {@code null} if given country is not related to this name
     * @since   2.2
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
     * verpflichtet zu pr&uuml;fen, ob das Ergebnis {@code null} ist. </p>
     *
     * @param   country     country reference
     * @return  preferred zone id belonging to this windows zone
     *          or {@code null} if given country is not related to this name
     * @since   2.2
     */
    public TZID resolveSmart(Locale country) {

        Set<TZID> ids = this.resolve(country);

        if (ids.size() > 1) {
            ids = NAME_BASED_MAP.get(this.name).get("001");
        }

        switch (ids.size()) {
            case 0:
                return null;
            case 1:
                return ids.iterator().next();
            default:
                throw new AssertionError(
                    "Ambivalent windows zone: " + this.name);
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

        return VERSION;

    }

    // called by WinZoneProviderSPI
    static Map<String, String> idsToNames(String country) {

        Map<String, String> map = REPOSITORY.get(country);

        if (map == null) {
            return Collections.emptyMap();
        } else {
            return Collections.unmodifiableMap(map);
        }

    }

    // called by WinZoneProviderSPI
    static Set<String> getPreferredIDs(
        String country,
        boolean smart
    ) {

        return (
            smart
            ? getPreferences(country)
            : idsToNames(country).keySet());

    }

    private static Set<String> getPreferences(String country) {

        Set<String> preferences = PREFERRED_KEYS.get(country);

        if (preferences == null) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(preferences);
        }

    }

    private static Map<String, Set<String>> prepareSmartMode() {

        Map<String, Set<String>> preferredKeys =
            new HashMap<String, Set<String>>();

        for (String country : REPOSITORY.keySet()) {
            Map<String, String> map = idsToNames(country);
            Set<String> keys = map.keySet();

            if (keys.size() >= 2) {
                keys = new HashSet<String>();
                Set<String> names = new HashSet<String>(map.values());

                for (String name : names) {
                    for (Map.Entry<String, String> e : getFallback()) {
                        if (e.getValue().equals(name)) {
                            keys.add(e.getKey());
                        }
                    }
                }
            }

            preferredKeys.put(country, keys);
        }

        return Collections.unmodifiableMap(preferredKeys);

    }

    private static Set<Map.Entry<String, String>> getFallback() {

        return idsToNames("001").entrySet();

    }

    private static Map<String, Map<String, Set<TZID>>> prepareResolvers() {

        Map<String, Map<String, Set<TZID>>> nameBasedMap =
            new HashMap<String, Map<String, Set<TZID>>>();

        for (String country : REPOSITORY.keySet()) {
            Map<String, String> idsToNames = REPOSITORY.get(country);

            for (Map.Entry<String, String> e : idsToNames.entrySet()) {
                String id = e.getKey();
                String name = e.getValue();

                Map<String, Set<TZID>> countryToIds = nameBasedMap.get(name);
                if (countryToIds == null) {
                    countryToIds = new HashMap<String, Set<TZID>>();
                    nameBasedMap.put(name, countryToIds);
                }

                Set<TZID> ids = countryToIds.get(country);
                if (ids == null) {
                    ids = new HashSet<TZID>();
                    countryToIds.put(country, ids);
                }

                ids.add(new WinZoneID(id));
            }
        }

        return Collections.unmodifiableMap(nameBasedMap);

    }

    private static Map<String, Map<String, String>> loadData() {

        ObjectInputStream ois = null;

        try {
            InputStream is = null;
            String source = "data/winzone.ser";
            ClassLoader loader = Thread.currentThread().getContextClassLoader();

            if (loader != null) {
                is = loader.getResourceAsStream(source);
            }

            if (is == null) {
                loader = WindowsZone.class.getClassLoader();
                is = loader.getResourceAsStream(source);
            }

            if (is == null) {
                throw new FileNotFoundException(source);
            } else {
                ois = new ObjectInputStream(is);
                String version = ois.readUTF();
                Map<String, Map<String, String>> data = cast(ois.readObject());
                Map<String, Map<String, String>> map =
                    new HashMap<String, Map<String, String>>(data);
                map.put(VKEY, Collections.singletonMap(version, version));
                return map;
            }
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException(ex);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
        }

    }

    @SuppressWarnings("unchecked")
    private static <T> T cast(Object obj) {

        return (T) obj;

    }

    private static void check(String name) {

        if (
            name.isEmpty()
            || !NAME_BASED_MAP.keySet().contains(name)
        ) {
            throw new IllegalArgumentException("Unknown windows zone: " + name);
        }

    }

    /**
     * @serialData  Checks the consistency.
     * @throws      InvalidObjectException in case of inconsistencies
     */
    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {

        in.defaultReadObject();
        check(this.name);

    }

}
