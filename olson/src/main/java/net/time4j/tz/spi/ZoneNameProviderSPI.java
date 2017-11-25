/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ZoneNameProviderSPI.java) is part of project Time4J.
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
import net.time4j.i18n.UTF8ResourceControl;
import net.time4j.tz.NameStyle;
import net.time4j.tz.ZoneNameProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.DateFormatSymbols;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * <p>Special implementation of {@code ZoneNameProvider} whose only purpose is
 * to assist in resolving timezone names to ids. </p>
 *
 * @author  Meno Hochschild
 * @since   3.1
 */
public class ZoneNameProviderSPI
    implements ZoneNameProvider {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final ConcurrentMap<Locale, Map<String, Map<NameStyle, String>>> NAMES =
        new ConcurrentHashMap<Locale, Map<String, Map<NameStyle, String>>>();

    private static final Set<String> GMT_ZONES;
    private static final Map<String, Set<String>> TERRITORIES;
    private static final Map<String, String> PRIMARIES;
    private static final ResourceBundle.Control CONTROL;

    static {
        Set<String> gmtZones = new HashSet<String>();
        gmtZones.add("Z");
        gmtZones.add("GMT");
        gmtZones.add("GMT0");
        gmtZones.add("Greenwich");
        gmtZones.add("UCT");
        gmtZones.add("UTC");
        gmtZones.add("UTC0");
        gmtZones.add("Universal");
        gmtZones.add("Zulu");
        GMT_ZONES = Collections.unmodifiableSet(gmtZones);

        String name = "data/zone1970.tab";
        Map<String, Set<String>> temp = new HashMap<String, Set<String>>();
        loadTerritories(temp, name);
        TERRITORIES = Collections.unmodifiableMap(temp);

        // CLDR32 - supplemental\metaZones.xml - primaryZones
        Map<String, String> primaries = new HashMap<String, String>();
        addPrimary(primaries, "CL", "America/Santiago");
        addPrimary(primaries, "CN", "Asia/Shanghai");
        addPrimary(primaries, "DE", "Europe/Berlin");
        addPrimary(primaries, "EC", "America/Guayaquil");
        addPrimary(primaries, "ES", "Europe/Madrid");
        addPrimary(primaries, "MH", "Pacific/Majuro");
        addPrimary(primaries, "MY", "Asia/Kuala_Lumpur");
        addPrimary(primaries, "NZ", "Pacific/Auckland");
        addPrimary(primaries, "PT", "Europe/Lisbon");
        addPrimary(primaries, "UA", "Europe/Kiev");
        addPrimary(primaries, "UZ", "Asia/Tashkent");
        PRIMARIES = Collections.unmodifiableMap(primaries);

        CONTROL =
            new UTF8ResourceControl() {
                protected String getModuleName() {
                    return "olson";
                }
                protected Class<?> getModuleRef() {
                    return ZoneNameProviderSPI.class;
                }
            };
    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Set<String> getPreferredIDs(
        Locale locale,
        boolean smart
    ) {

        String country = locale.getCountry();

        if (smart) {
            if (country.equals("US")) {
                Set<String> tzids = new LinkedHashSet<String>();
                tzids.add("America/New_York");
                tzids.add("America/Chicago");
                tzids.add("America/Denver");
                tzids.add("America/Los_Angeles");
                tzids.add("America/Anchorage");
                tzids.add("Pacific/Honolulu");
                tzids.add("America/Adak");
                return Collections.unmodifiableSet(tzids);
            } else {
                String primaryZone = PRIMARIES.get(country);

                if (primaryZone != null) {
                    return Collections.singleton(primaryZone);
                }
            }
        }

        Set<String> result = TERRITORIES.get(country);

        if (result == null) {
            result = Collections.emptySet();
        }

        return result;

    }

    @Override
    public String getDisplayName(
        String tzid,
        NameStyle style,
        Locale locale
    ) {

        if (GMT_ZONES.contains(tzid)) {
            return ""; // falls back to canonical identifier (Z for ZonalOffset.UTC)
        }

        Map<String, Map<NameStyle, String>> map = NAMES.get(locale);

        if (map == null) {
            DateFormatSymbols symbols = DateFormatSymbols.getInstance(locale);
            String[][] zoneNames = symbols.getZoneStrings();
            map = new HashMap<String, Map<NameStyle, String>>();

            for (String[] arr : zoneNames) {
                Map<NameStyle, String> names = new EnumMap<NameStyle, String>(NameStyle.class);
                names.put(NameStyle.LONG_STANDARD_TIME, arr[1]);
                names.put(NameStyle.SHORT_STANDARD_TIME, arr[2]);
                names.put(NameStyle.LONG_DAYLIGHT_TIME, arr[3]);
                names.put(NameStyle.SHORT_DAYLIGHT_TIME, arr[4]);
                // TODO: Wenn Daten verfügbar sind, dann hier einfügen (und NameStyle erweitern)
                map.put(arr[0], names);
            }

            Map<String, Map<NameStyle, String>> old = NAMES.putIfAbsent(locale, map);

            if (old != null) {
                map = old;
            }
        }

        Map<NameStyle, String> styledNames = map.get(tzid);

        if (styledNames != null) {
            return styledNames.get(style);
        }

        return "";

// *************************************************************************************
// OLD CODE
// *************************************************************************************
//        Timezone tz = Timezone.of("java.util.TimeZone~" + tzid, ZonalOffset.UTC);
//
//        if (tz.isFixed() && tz.getOffset(Moment.UNIX_EPOCH).equals(ZonalOffset.UTC)) {
//            return "";
//        }
//
//        return tz.getDisplayName(style, locale);
// *************************************************************************************

    }

    @Override
    public String getStdFormatPattern(
        boolean zeroOffset,
        Locale locale
    ) {

        return getBundle(locale).getString(zeroOffset ? "utc-literal" : "offset-pattern");

    }

    static void loadTerritories(
        Map<String, Set<String>> map,
        String name
    ) {

        URI uri = ResourceLoader.getInstance().locate("olson", ZoneNameProviderSPI.class, name);
        InputStream is = ResourceLoader.getInstance().load(uri, true);

        if (is == null) {
            is = ZoneNameProviderSPI.class.getClassLoader().getResourceAsStream(name);
        }

        if (is != null) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith("#") || line.isEmpty()) {
                        continue; // Kommentarzeile überspringen
                    }
                    String[] columns = line.split("\t");
                    if (columns.length >= 3) {
                        for (String country : columns[0].split(",")) {
                            addTerritory(map, country, columns[2]);
                        }
                    }
                }
            } catch (UnsupportedEncodingException uee) {
                throw new AssertionError(uee);
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            } finally {
                try {
                    is.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace(System.err);
                }
            }
        } else {
            System.err.println("Warning: File \"" + name + "\" not found.");
        }

    }

    private static void addTerritory(
        Map<String, Set<String>> map,
        String country,
        String tzid
    ) {

        Set<String> preferred = map.get(country);

        if (preferred == null) {
            preferred = new LinkedHashSet<String>();
            map.put(country, preferred);
        }

        preferred.add(tzid);

    }

    private static void addPrimary(
        Map<String, String> map,
        String country,
        String tzid
    ) {

        map.put(country, tzid);

    }

    /**
     * <p>Gets a resource bundle for given calendar type and locale. </p>
     *
     * @param   desired         locale (language and/or country)
     * @return  {@code ResourceBundle}
     */
    private static ResourceBundle getBundle(Locale desired) {

        return ResourceBundle.getBundle(
            "zones/tzname",
            desired,
            ZoneNameProviderSPI.class.getClassLoader(),
            CONTROL);

    }

}
