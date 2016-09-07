/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
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

import net.time4j.i18n.UTF8ResourceControl;
import net.time4j.tz.NameStyle;
import net.time4j.tz.TZID;
import net.time4j.tz.ZoneNameProvider;
import net.time4j.tz.olson.AFRICA;
import net.time4j.tz.olson.AMERICA;
import net.time4j.tz.olson.ANTARCTICA;
import net.time4j.tz.olson.ASIA;
import net.time4j.tz.olson.ATLANTIC;
import net.time4j.tz.olson.AUSTRALIA;
import net.time4j.tz.olson.EUROPE;
import net.time4j.tz.olson.INDIAN;
import net.time4j.tz.olson.PACIFIC;

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

        Map<String, Set<String>> temp = new HashMap<String, Set<String>>();

        for (AFRICA tzid : AFRICA.values()) {
            addTerritory(temp, tzid.getCountry(), tzid);
        }
        for (AMERICA tzid : AMERICA.values()) {
            addTerritory(temp, tzid.getCountry(), tzid);
        }
        for (TZID tzid : AMERICA.ARGENTINA.values()) {
            addTerritory(temp, "AR", tzid);
        }
        for (TZID tzid : AMERICA.INDIANA.values()) {
            addTerritory(temp, "US", tzid);
        }
        for (TZID tzid : AMERICA.KENTUCKY.values()) {
            addTerritory(temp, "US", tzid);
        }
        for (TZID tzid : AMERICA.NORTH_DAKOTA.values()) {
            addTerritory(temp, "US", tzid);
        }
        for (ANTARCTICA tzid : ANTARCTICA.values()) {
            addTerritory(temp, tzid.getCountry(), tzid);
        }
        for (ASIA tzid : ASIA.values()) {
            addTerritory(temp, tzid.getCountry(), tzid);
        }
        for (ATLANTIC tzid : ATLANTIC.values()) {
            addTerritory(temp, tzid.getCountry(), tzid);
        }
        for (AUSTRALIA tzid : AUSTRALIA.values()) {
            addTerritory(temp, tzid.getCountry(), tzid);
        }
        for (EUROPE tzid : EUROPE.values()) {
            addTerritory(temp, tzid.getCountry(), tzid);
        }
        for (INDIAN tzid : INDIAN.values()) {
            addTerritory(temp, tzid.getCountry(), tzid);
        }
        for (PACIFIC tzid : PACIFIC.values()) {
            addTerritory(temp, tzid.getCountry(), tzid);
        }

        temp.put("SJ", Collections.singleton("Arctic/Longyearbyen"));
        TERRITORIES = Collections.unmodifiableMap(temp);

        // CLDR29 - supplemental\metaZones.xml - primaryZones
        Map<String, String> primaries = new HashMap<String, String>();
        addPrimary(primaries, "CL", AMERICA.SANTIAGO);
        addPrimary(primaries, "CN", ASIA.SHANGHAI);
        addPrimary(primaries, "DE", EUROPE.BERLIN);
        addPrimary(primaries, "EC", AMERICA.GUAYAQUIL);
        addPrimary(primaries, "ES", EUROPE.MADRID);
        addPrimary(primaries, "MH", PACIFIC.MAJURO);
        addPrimary(primaries, "MY", ASIA.KUALA_LUMPUR);
        addPrimary(primaries, "NZ", PACIFIC.AUCKLAND);
        addPrimary(primaries, "PT", EUROPE.LISBON);
        addPrimary(primaries, "UA", EUROPE.KIEV);
        addPrimary(primaries, "UZ", ASIA.TASHKENT);
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

    private static void addTerritory(
        Map<String, Set<String>> map,
        String country,
        TZID tz
    ) {

        Set<String> preferred = map.get(country);

        if (preferred == null) {
            preferred = new LinkedHashSet<String>();
            map.put(country, preferred);
        }

        preferred.add(tz.canonical());

    }

    private static void addPrimary(
        Map<String, String> map,
        String country,
        TZID tz
    ) {

        map.put(country, tz.canonical());

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
