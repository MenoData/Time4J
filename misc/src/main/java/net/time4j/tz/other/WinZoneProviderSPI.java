/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
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

package net.time4j.tz.other;

import net.time4j.tz.NameStyle;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.TransitionHistory;
import net.time4j.tz.ZoneProvider;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * <p>SPI-implementation for support of Windows timezones. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 * @exclude
 */
public class WinZoneProviderSPI
    implements ZoneProvider {

    //~ Methoden ----------------------------------------------------------

    @Override
    public Set<String> getAvailableIDs() {

        Set<String> zones = new HashSet<String>();

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

        return WindowsZone.getPreferredIDs(locale.getCountry(), smart);

    }

    @Override
    public String getDisplayName(
        String tzid,
        NameStyle style,
        Locale locale
    ) {

        if (tzid.isEmpty()) {
            return "";
        }

        Map<String, String> map = WindowsZone.idsToNames(locale.getCountry());
        String name = map.get("WINDOWS~" + tzid);
        return ((name == null) ? "" : name);

    }

}
