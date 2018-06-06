/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (WinZoneProviderSPI.java) is part of project Time4J.
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

package net.time4j.tz.spi;

import net.time4j.base.ResourceLoader;
import net.time4j.format.internal.FormatUtils;
import net.time4j.tz.NameStyle;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.TransitionHistory;
import net.time4j.tz.ZoneModelProvider;
import net.time4j.tz.ZoneNameProvider;
import net.time4j.tz.other.WindowsZone;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * <p>SPI-implementation for support of Windows timezones. </p>
 *
 * @author  Meno Hochschild
 * @since   3.1
 */
public class WinZoneProviderSPI
    implements ZoneModelProvider, ZoneNameProvider {

    //~ Statische Felder/Initialisierungen --------------------------------

    // Map<country, Map<tzid, name>>
    private static final Map<String, Map<String, String>> REPOSITORY;

    // Map<country, Set<tzid>>
    private static final Map<String, Set<String>> PREFERRED_KEYS;

    // Map<name, Map<country, Set<tzid>>>
    public static final Map<String, Map<String, Set<TZID>>> NAME_BASED_MAP;

    // Version of windowsZones.xml
    public static final String WIN_NAME_VERSION;

    private static final String VKEY = "VERSION";

    static {
        Map<String, Map<String, String>> map = loadData();
        WIN_NAME_VERSION = map.get(VKEY).keySet().iterator().next();
        map.remove(VKEY);
        REPOSITORY = Collections.unmodifiableMap(map);
        PREFERRED_KEYS = prepareSmartMode();
        NAME_BASED_MAP = prepareResolvers();
    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Set<String> getAvailableIDs() {

        Set<String> zones = new HashSet<>();

        for (TZID tzid : Timezone.getAvailableIDs("DEFAULT")) {
            zones.add("WINDOWS~" + tzid.canonical());
        }

        return Collections.unmodifiableSet(zones);

    }

    @Override
    public Map<String, String> getAliases() {

        return Collections.emptyMap();

    }

    @Override
    public String getFallback() {

        return "DEFAULT";

    }

    @Override
    public String getName() {

        return "WINDOWS";

    }

    @Override
    public String getLocation() {

        return "";

    }

    @Override
    public String getVersion() {

        return "";

    }

    @Override
    public TransitionHistory load(String zoneID) {

        return null; // uses fallback

    }

    @Override
    public Set<String> getPreferredIDs(
        Locale locale,
        boolean smart
    ) {

        return getPreferredIDs(FormatUtils.getRegion(locale), smart);

    }

    @Override
    public String getDisplayName(
        String tzid,
        NameStyle style, // unused
        Locale locale
    ) {

        if (tzid.isEmpty()) {
            return "";
        }

        Map<String, String> map = idsToNames(FormatUtils.getRegion(locale));
        String name = map.get("WINDOWS~" + tzid);
        return ((name == null) ? "" : name);

    }

    private static Map<String, String> idsToNames(String country) {

        Map<String, String> map = REPOSITORY.get(country);

        if (map == null) {
            return Collections.emptyMap();
        } else {
            return Collections.unmodifiableMap(map);
        }

    }

    private static Set<String> getPreferredIDs(
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

    private static Map<String, Map<String, String>> loadData() {

        ObjectInputStream ois = null;

        try {
            String source = "data/winzone.ser";
            URI uri = ResourceLoader.getInstance().locate("base", WindowsZone.class, source);
            InputStream is = ResourceLoader.getInstance().load(uri, true);
            if (is == null) {
                is = ResourceLoader.getInstance().load(WindowsZone.class, source, true);
            }
            ois = new ObjectInputStream(is);
            String version = ois.readUTF();
            Map<String, Map<String, String>> data = cast(ois.readObject());
            Map<String, Map<String, String>> map = new HashMap<>(data);
            map.put(VKEY, Collections.singletonMap(version, version));
            return map;
        } catch (ClassNotFoundException | IOException ex) {
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

    private static Map<String, Set<String>> prepareSmartMode() {

        Map<String, Set<String>> preferredKeys = new HashMap<>();

        for (String country : REPOSITORY.keySet()) {
            Map<String, String> map = idsToNames(country);
            Set<String> keys = map.keySet();

            if (keys.size() >= 2) {
                keys = new HashSet<>();
                Set<String> names = new HashSet<>(map.values());

                for (String name : names) {
                    for (Map.Entry<String, String> e : getFallbackSet()) {
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

    private static Set<Map.Entry<String, String>> getFallbackSet() {

        return idsToNames("001").entrySet();

    }

    private static Map<String, Map<String, Set<TZID>>> prepareResolvers() {

        Map<String, Map<String, Set<TZID>>> nameBasedMap = new HashMap<>();

        for (String country : REPOSITORY.keySet()) {
            Map<String, String> idsToNames = REPOSITORY.get(country);

            for (Map.Entry<String, String> e : idsToNames.entrySet()) {
                String id = e.getKey();
                String name = e.getValue();

                Map<String, Set<TZID>> countryToIds = nameBasedMap.get(name);
                if (countryToIds == null) {
                    countryToIds = new HashMap<>();
                    nameBasedMap.put(name, countryToIds);
                }

                Set<TZID> ids = countryToIds.get(country);
                if (ids == null) {
                    ids = new HashSet<>();
                    countryToIds.put(country, ids);
                }

                ids.add(new WinZoneID(id));
            }
        }

        return Collections.unmodifiableMap(nameBasedMap);

    }

}
