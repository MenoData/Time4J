/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (WinZoneName.java) is part of project Time4J.
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
import javax.xml.namespace.QName;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


/**
 * <p>Represents a windows timezone name which can be mapped to an IANA/Olson-ID
 * using a territory information. </p>
 *
 * <p>Example: </p>
 *
 * <pre>
 *  WinZoneName wzn = WinZoneName.of(&quot;Eastern Standard Time&quot;);
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
 *  WinZoneName wzn = WinZoneName.of(&quot;Eastern Standard Time&quot;);
 *  TZID winzone = wzn.resolveSmart(Locale.US);
 *  System.out.println(winzone.canonical());
 *  // output: WINDOWS~America/New_York
 * </pre>
 *
 * @author  Meno Hochschild
 * @since   2.2
 */
public final class WinZoneName
    implements Comparable<WinZoneName>, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    // Map<country, Map<tzid, name>>
    private static final Map<String, Map<String, String>> REPOSITORY;

    // Map<country, Set<tzid>>
    private static final Map<String, Set<String>> PREFERRED_KEYS;

    // Map<name, Map<country, Set<tzid>>>
    private static final Map<String, Map<String, Set<TZID>>> NAME_BASED_MAP;

    private static final long serialVersionUID = -6071278077083785308L;

    static {
        REPOSITORY = loadCLDR(); // TODO: optimize storage size
        PREFERRED_KEYS = prepareSmartMode();
        NAME_BASED_MAP = prepareResolvers();
    }

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  name of windows zone
     */
    private final String name;

    //~ Konstruktoren -----------------------------------------------------

    private WinZoneName(String name) {
        super();

        this.name = name;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a name reference to a windows zone. </p>
     *
     * @param   name    standardized windows zone name
     * @return  new instance of {@code WinZoneName}
     * @throws  IllegalArgumentException if given name is not supported
     * @since   2.2
     */
    /*[deutsch]
     * <p>Erzeugt einen Namensbezug zu einer Windows-Zeitzone. </p>
     *
     * @param   name    standardized windows zone name
     * @return  new instance of {@code WinZoneName}
     * @throws  IllegalArgumentException if given name is not supported
     * @since   2.2
     */
    public static WinZoneName of(String name) {

        check(name);
        return new WinZoneName(name);

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof WinZoneName) {
            WinZoneName that = (WinZoneName) obj;
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
    public int compareTo(WinZoneName other) {

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

    private static Map<String, Map<String, String>> loadCLDR() {

        Map<String, Map<String, String>> repository =
            new HashMap<String, Map<String, String>>();

        try {
            XMLInputFactory f = XMLInputFactory.newInstance();
            f.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
            f.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
            String source = "data/windowsZones.xml";
            InputStream is =
                WinZoneName.class.getClassLoader().getResourceAsStream(source);
            XMLEventReader reader = f.createXMLEventReader(is);

            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();

                if (event.isStartElement()) {
                    StartElement element = (StartElement) event;
                    if (element.getName().getLocalPart().equals("mapZone")) {
                        String country = getAttribute(element, "territory");
                        String id = getAttribute(element, "type");
                        String name = getAttribute(element, "other");
                        fill(repository, country, id, name);
                    }
                }
            }
        } catch (FactoryConfigurationError error) {
            throw new IllegalStateException(error);
        } catch (XMLStreamException ex) {
            throw new IllegalStateException(ex);
        }

        return Collections.unmodifiableMap(repository);

    }

    private static String getAttribute(
        StartElement element,
        String name
    ) {

        return element.getAttributeByName(new QName(name)).getValue();

    }

    private static void fill(
        Map<String, Map<String, String>> repository,
        String country,
        String id,
        String name
    ) {

        Map<String, String> data = repository.get(country);

        if (data == null) {
            data = new HashMap<String, String>();
            repository.put(country, data);
        }

        for (String tzid : id.split(" ")) {
            // assumption: no ambivalent mapping from ids to names
            data.put("WINDOWS~" + tzid, name);
        }

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
