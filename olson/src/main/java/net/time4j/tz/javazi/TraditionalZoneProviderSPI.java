/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TraditionalZoneProviderSPI.java) is part of project Time4J.
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

package net.time4j.tz.javazi;

import net.time4j.tz.NameStyle;
import net.time4j.tz.Timezone;
import net.time4j.tz.TransitionHistory;
import net.time4j.tz.ZoneProvider;
import net.time4j.tz.spi.ZoneNameProviderSPI;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;


/**
 * <p>SPI-implementation for evaluation of &quot;lib/zi&quot;-repository
 * (old JVM-timezones before Java 8). </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 * @doctags.exclude
 */
public class TraditionalZoneProviderSPI
    implements ZoneProvider {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final ZoneProvider NAME_PROVIDER = new ZoneNameProviderSPI();

    //~ Instanzvariablen --------------------------------------------------

    private final String version;
    private final Map<String, String> aliases;

    //~ Konstruktoren -----------------------------------------------------

    public TraditionalZoneProviderSPI() {
        super();

        String v = JVMZoneReader.getVersion();

        if (v.startsWith("tzdata")) {
            v = v.substring(6);
        }

        this.version = v;
        this.aliases = JVMZoneReader.getZoneAliases();

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Set<String> getAvailableIDs() {

        Set<String> zones = new HashSet<String>();
        Collections.addAll(zones, TimeZone.getAvailableIDs());
        return Collections.unmodifiableSet(zones);

    }

    @Override
    public Map<String, String> getAliases() {

        return this.aliases;

    }

    @Override
    public String getFallback() {

        return "";

    }

    @Override
    public String getName() {

        return "TZDB";

    }

    @Override
    public String getLocation() {

        return "{java.home}/lib/zi";

    }

    @Override
    public String getVersion() {

        return this.version;

    }

    @Override
    public TransitionHistory load(String zoneID) {

        return JVMZoneReader.getHistory(zoneID);

    }

    @Override
    public Set<String> getPreferredIDs(
        Locale locale,
        boolean smart
    ) {

        return NAME_PROVIDER.getPreferredIDs(locale, smart);

    }

    @Override
    public String getDisplayName(
        String tzid,
        NameStyle style,
        Locale locale
    ) {

        return NAME_PROVIDER.getDisplayName(tzid, style, locale);

    }

}
