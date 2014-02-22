/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimezoneNameProcessor.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Time4J. If not, see <http://www.gnu.org/licenses/>.
 * -----------------------------------------------------------------------
 */

package net.time4j.format;

import net.time4j.base.UnixTime;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.tz.TZID;
import net.time4j.tz.TimeZone;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * <p>Verarbeitet einen Zeitzonen-Namen. </p>
 *
 * @author  Meno Hochschild
 */
final class TimezoneNameProcessor
    implements FormatProcessor<TZID> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final Map<Locale, Map<String, List<TZID>>> CACHE =
        new ConcurrentHashMap<Locale, Map<String, List<TZID>>>();
    private static final int MAX = 10;

    //~ Instanzvariablen --------------------------------------------------

    private final boolean abbreviated;
    private final FormatProcessor<TZID> fallback;
    private final Set<TZID> preferredZones;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Erzeugt eine neue Instanz. </p>
     *
     * @param   abbreviated     abbreviations to be used?
     * @param   preferredZones  preferred time zone ids for resolving duplicates
     */
    TimezoneNameProcessor(
        boolean abbreviated,
        Set<TZID> preferredZones
    ) {
        super();

        this.abbreviated = abbreviated;
        this.fallback = new LocalizedGMTProcessor(abbreviated);
        this.preferredZones = preferredZones;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public void print(
        ChronoEntity<?> formattable,
        Appendable buffer,
        Attributes attributes,
        Set<ElementPosition> positions,
        FormatStep step
    ) throws IOException {

        TZID tzid = formattable.get(TimeZone.identifier());

        if (tzid == null) {
            throw new IllegalArgumentException(
                "Cannot extract time zone id from: " + formattable);
        } else if (tzid instanceof ZonalOffset) {
            this.fallback.print(
                formattable, buffer, attributes, positions, step);
            return;
        }

        String name;

        if (formattable instanceof UnixTime) {
            TimeZone zone = TimeZone.of(tzid);
            UnixTime ut = UnixTime.class.cast(formattable);
            name =
                zone.getDisplayName(
                    zone.isDaylightSaving(ut),
                    this.abbreviated,
                    step.getAttribute(
                        Attributes.LOCALE, attributes, Locale.ROOT));
        } else {
            throw new IllegalArgumentException(
                "Cannot extract time zone name from: " + formattable);
        }

        int start = -1;
        int printed = 0;

        if (buffer instanceof CharSequence) {
            start = ((CharSequence) buffer).length();
        }

        buffer.append(name);
        printed = name.length();

        if (
            (start != -1)
            && (printed > 0)
            && (positions != null)
        ) {
            positions.add(
                new ElementPosition(
                    ZonalElement.TIMEZONE_ID,
                    start,
                    start + printed));
        }

    }

    @Override
    public void parse(
        CharSequence text,
        ParseLog status,
        Attributes attributes,
        Map<ChronoElement<?>, Object> parsedResult,
        FormatStep step
    ) {

        int len = text.length();
        int start = status.getPosition();
        int pos = start;

        if (pos >= len) {
            status.setError(start, "Missing time zone name.");
            return;
        }

        Locale locale =
            step.getAttribute(Attributes.LOCALE, attributes, Locale.ROOT);
        StringBuilder name = new StringBuilder();

        while (pos < len) {
            char c = text.charAt(pos);

            if (Character.isLetter(c)) {
                name.append(c);
                pos++;
            } else {
                break;
            }
        }

        String key = name.toString().toUpperCase(locale);

        if (
            key.startsWith("GMT")
            || key.startsWith("UT")
        ) {
            this.fallback.parse(text, status, attributes, parsedResult, step);
            return;
        }

        Map<String, List<TZID>> cached = CACHE.get(locale);

        if (cached == null) {
            cached = this.fillCache(locale);
        }

        List<TZID> zones = cached.get(key);

        if ((zones == null) || zones.isEmpty()) {
            status.setError(
                start,
                "Unknown time zone name: " + key);
            return;
        } else if (zones.size() > 1) { // tz name not unique
            List<TZID> candidates = new ArrayList<TZID>(zones);

            for (TZID tz : zones) {
                boolean found = false;

                for (TZID p : this.preferredZones) {
                    if (p.canonical().equals(tz.canonical())) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    candidates.remove(tz);
                }
            }

            zones = candidates;
        }

        if (zones.size() == 1) {
            parsedResult.put(ZonalElement.TIMEZONE_ID, zones.get(0));
            status.setPosition(pos);
        } else if (zones.isEmpty()) {
            status.setError(
                start,
                "Time zone id not found among preferred time zones.");
        } else {
            status.setError(
                start,
                "Time zone name is not unique: " + key);
        }

    }

    @Override
    public ChronoElement<TZID> getElement() {

        return ZonalElement.TIMEZONE_ID;

    }

    @Override
    public FormatProcessor<TZID> withElement(ChronoElement<TZID> element) {

        return this;

    }

    @Override
    public boolean isNumerical() {

        return false;

    }

    private Map<String, List<TZID>> fillCache(Locale locale) {

        List<TZID> zones;
        Map<String, List<TZID>> map = new HashMap<String, List<TZID>>();

        for (TZID tzid : TimeZone.getAvailableIDs()) {
            TimeZone zone = TimeZone.of(tzid);

            String winterTime =
                zone.getDisplayName(false, this.abbreviated, locale)
                    .toUpperCase(locale);
            zones = map.get(winterTime);

            if (zones == null) {
                zones = new ArrayList<TZID>();
                map.put(winterTime, zones);
            }

            zones.add(tzid);

            String summerTime =
                zone.getDisplayName(true, this.abbreviated, locale)
                    .toUpperCase(locale);

            zones = map.get(summerTime);

            if (zones == null) {
                zones = new ArrayList<TZID>();
                map.put(summerTime, zones);
            }

            zones.add(tzid);
        }

        if (CACHE.size() < MAX) {
            CACHE.put(locale, map);
        }

        return map;

    }

}
