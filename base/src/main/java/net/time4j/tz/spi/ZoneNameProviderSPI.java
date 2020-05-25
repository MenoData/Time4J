/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2020 Meno Hochschild, <http://www.menodata.de/>
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
import net.time4j.format.internal.FormatUtils;
import net.time4j.format.internal.PropertyBundle;
import net.time4j.tz.NameStyle;
import net.time4j.tz.ZoneNameProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.zone.ZoneRulesException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
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

    private static final ConcurrentMap<Locale, Map<String, Map<NameStyle, String>>> NAMES = new ConcurrentHashMap<>();

    private static final Set<String> GMT_ZONES;
    private static final Map<String, Set<String>> TERRITORIES;
    private static final Map<String, String> PRIMARIES;

    static {
        Set<String> gmtZones = new HashSet<>();
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
        Map<String, Set<String>> temp = new HashMap<>();
        loadTerritories(temp, name);
        TERRITORIES = Collections.unmodifiableMap(temp);

        // CLDR35 - supplemental\metaZones.xml - primaryZones
        Map<String, String> primaries = new HashMap<>();
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
    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Set<String> getPreferredIDs(
        Locale locale,
        boolean smart
    ) {

        String country = FormatUtils.getRegion(locale);

        if (smart) {
            if (country.equals("US")) {
                Set<String> tzids = new LinkedHashSet<>();
                tzids.add("America/New_York");
                tzids.add("America/Chicago");
                tzids.add("America/Denver");
                tzids.add("America/Los_Angeles");
                tzids.add("America/Anchorage");
                tzids.add("Pacific/Honolulu");
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
            map = new HashMap<>();

            for (String[] arr : zoneNames) {
                Map<NameStyle, String> names = new EnumMap<>(NameStyle.class);
                names.put(NameStyle.LONG_STANDARD_TIME, arr[1]);
                names.put(NameStyle.SHORT_STANDARD_TIME, arr[2]);
                names.put(NameStyle.LONG_DAYLIGHT_TIME, arr[3]);
                names.put(NameStyle.SHORT_DAYLIGHT_TIME, arr[4]);
                if (arr.length >= 7) {
                    names.put(NameStyle.LONG_GENERIC_TIME, arr[5]); // data introduced in Java-8
                    names.put(NameStyle.SHORT_GENERIC_TIME, arr[6]);  // data introduced in Java-8
                } else { // before 8u60
                    try {
                        ZoneId zoneId = ZoneId.of(arr[0]);
                        DateTimeFormatter threetenLong =
                            DateTimeFormatter.ofPattern("zzzz", locale).withZone(zoneId);
                        DateTimeFormatter threetenShort =
                            DateTimeFormatter.ofPattern("z", locale).withZone(zoneId);
                        String s1 = threetenLong.format(LocalDate.MAX);
                        String s2 = threetenShort.format(LocalDate.MAX);
                        names.put(NameStyle.LONG_GENERIC_TIME, s1);
                        names.put(NameStyle.SHORT_GENERIC_TIME, s2);
                    } catch (ZoneRulesException ex) {
                        names.put(NameStyle.LONG_GENERIC_TIME, "");
                        names.put(NameStyle.SHORT_GENERIC_TIME, "");
                    }
                }
                map.put(arr[0], names); // tz-id
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

        URI uri = ResourceLoader.getInstance().locate("base", ZoneNameProviderSPI.class, name);
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

        map.computeIfAbsent(country, k -> new LinkedHashSet<>()).add(tzid);

    }

    private static void addPrimary(
        Map<String, String> map,
        String country,
        String tzid
    ) {

        map.put(country, tzid);

    }

    private static PropertyBundle getBundle(Locale desired) {

        return PropertyBundle.load("zones/tzname", desired);

    }

}
