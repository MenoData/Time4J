/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (MilZoneProviderSPI.java) is part of project Time4J.
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

import net.time4j.tz.NameStyle;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.TransitionHistory;
import net.time4j.tz.ZonalOffset;
import net.time4j.tz.ZoneModelProvider;
import net.time4j.tz.ZoneNameProvider;
import net.time4j.tz.other.MilitaryZone;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * <p>SPI-implementation for support of military timezones. </p>
 *
 * @author  Meno Hochschild
 * @since   3.1
 */
public class MilZoneProviderSPI
    implements ZoneModelProvider, ZoneNameProvider {

    //~ Methoden ----------------------------------------------------------

    @Override
    public Set<String> getAvailableIDs() {

        Set<String> zones = new HashSet<>();

        for (TZID tzid : MilitaryZone.values()) {
            zones.add(tzid.canonical());
        }

        return Collections.unmodifiableSet(zones);

    }

    @Override
    public Map<String, String> getAliases() {

        return Collections.emptyMap();

    }

    @Override
    public String getFallback() {

        return "";

    }

    @Override
    public String getName() {

        return "MILITARY";

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

        return Timezone.of(ZonalOffset.parse(zoneID)).getHistory();

    }

    @Override
    public Set<String> getPreferredIDs(
        Locale locale,
        boolean smart
    ) {

        return this.getAvailableIDs();

    }

    @Override
    public String getDisplayName(
        String tzid,
        NameStyle style,
        Locale locale
    ) {

        ZonalOffset offset = ZonalOffset.parse(tzid);

        for (MilitaryZone m : MilitaryZone.values()) {
            if (m.getOffset().equals(offset)) {
                return (style.isAbbreviation() ? m.getSymbol() : m.toString());
            }
        }

        return "";

    }

}
