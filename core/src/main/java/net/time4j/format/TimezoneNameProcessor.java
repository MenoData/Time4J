/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimezoneNameProcessor.java) is part of project Time4J.
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

package net.time4j.format;

import net.time4j.base.UnixTime;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.tz.NameStyle;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * <p>Verarbeitet einen Zeitzonen-Namen. </p>
 *
 * @author  Meno Hochschild
 */
final class TimezoneNameProcessor
    implements FormatProcessor<TZID> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final ConcurrentMap<Locale, TZNames> CACHE_ABBREVIATIONS =
        new ConcurrentHashMap<Locale, TZNames>();
    private static final ConcurrentMap<Locale, TZNames> CACHE_ZONENAMES =
        new ConcurrentHashMap<Locale, TZNames>();
    private static final int MAX = 25;
    
    private static final boolean WITH_OLSON_MODULE;
    
    static {
        boolean hasOlsonModule = true;
        try {
            Class.forName("net.time4j.tz.olson.StdZoneIdentifier");
        } catch (ClassNotFoundException ex) {
            hasOlsonModule = false;
        }
        WITH_OLSON_MODULE = hasOlsonModule;
    }

    //~ Instanzvariablen --------------------------------------------------

    private final boolean abbreviated;
    private final FormatProcessor<TZID> fallback;
    private final Set<TZID> preferredZones;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Erzeugt eine neue Instanz. </p>
     *
     * @param   abbreviated     abbreviations to be used?
     * @param   preferredZones  preferred timezone ids for resolving duplicates
     */
    TimezoneNameProcessor(
        boolean abbreviated,
        Set<TZID> preferredZones
    ) {
        super();

        this.abbreviated = abbreviated;
        this.fallback = new LocalizedGMTProcessor(abbreviated);
        this.preferredZones = new LinkedHashSet<TZID>(preferredZones);

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public void print(
        ChronoEntity<?> formattable,
        Appendable buffer,
        AttributeQuery attributes,
        Set<ElementPosition> positions,
        FormatStep step
    ) throws IOException {

        if (!formattable.hasTimezone()) {
            throw new IllegalArgumentException(
                "Cannot extract timezone id from: " + formattable);
        }

        TZID tzid = formattable.getTimezone();

        if (tzid instanceof ZonalOffset) {
            this.fallback.print(
                formattable, buffer, attributes, positions, step);
            return;
        }

        String name;

        if (formattable instanceof UnixTime) {
            Timezone zone = Timezone.of(tzid);
            UnixTime ut = UnixTime.class.cast(formattable);

            name =
                zone.getDisplayName(
                    this.getStyle(zone.isDaylightSaving(ut)),
                    step.getAttribute(
                        Attributes.LOCALE, attributes, Locale.ROOT));
        } else {
            throw new IllegalArgumentException(
                "Cannot extract timezone name from: " + formattable);
        }

        int start = -1;
        int printed;

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
        AttributeQuery attributes,
        Map<ChronoElement<?>, Object> parsedResult,
        FormatStep step
    ) {

        int len = text.length();
        int start = status.getPosition();
        int pos = start;

        if (pos >= len) {
            status.setError(start, "Missing timezone name.");
            return;
        }

        Locale locale =
            step.getAttribute(Attributes.LOCALE, attributes, Locale.ROOT);
        Leniency leniency =
            step.getAttribute(Attributes.LENIENCY, attributes, Leniency.SMART);

        // short evaluation
        StringBuilder name = new StringBuilder();

        while (pos < len) {
            char c = text.charAt(pos);

            if (
                Character.isLetter(c)
                || (!this.abbreviated 
                    && (Character.isWhitespace(c)) || (c == '\''))
            ) {
                name.append(c);
                pos++;
            } else {
                break;
            }
        }

        String key = name.toString();

        // fallback-case (fixed offset)
        if (
            key.startsWith("GMT")
            || key.startsWith("UT")
        ) {
            this.fallback.parse(text, status, attributes, parsedResult, step);
            return;
        }

        // Zeitzonennamen im Cache suchen und ggf. Cache füllen
        ConcurrentMap<Locale, TZNames> cache = (
            this.abbreviated
            ? CACHE_ABBREVIATIONS
            : CACHE_ZONENAMES);

        TZNames tzNames = cache.get(locale);

        if (tzNames == null) {
            Map<String, List<TZID>> stdNames =
                this.getTimezoneNameMap(locale, false);
            Map<String, List<TZID>> dstNames =
                this.getTimezoneNameMap(locale, true);
            tzNames = new TZNames(stdNames, dstNames);

            if (cache.size() < MAX) {
                TZNames tmp = cache.putIfAbsent(locale, tzNames);

                if (tmp != null) {
                    tzNames = tmp;
                }
            }
        }

        // Zeitzonen-IDs bestimmen
        boolean daylightSaving = false;
        List<TZID> zones = tzNames.search(key, false);

        if (zones.isEmpty()) {
            zones = tzNames.search(key, true);
            if (zones.isEmpty()) {
                int[] lenbuf = new int[1];
                lenbuf[0] = 0;
                zones = tzNames.search(text, start, false, lenbuf);
                if (zones.isEmpty()) {
                    zones = tzNames.search(text, start, true, lenbuf);
                    if (!zones.isEmpty()) {
                        daylightSaving = true;
                    }
                }
                pos = start + lenbuf[0];
            } else {
                daylightSaving = true;
            }
        }

        if (zones.isEmpty()) {
            status.setError(
                start,
                "Unknown timezone name: " + key);
            return;
        } else if (
            WITH_OLSON_MODULE
            && (zones.size() > 1)
            && !leniency.isLax()
        ) { // tz name not unique
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
            
            if (candidates.isEmpty()) {
                status.setError(
                    start,
                    "Time zone id not found among preferred timezones.");
                return;
            } else {
                zones = candidates;
            }
        }

        if (
            (zones.size() == 1)
            || leniency.isLax()
        ) {
            parsedResult.put(ZonalElement.TIMEZONE_ID, zones.get(0));
            status.setPosition(pos);
            status.setDaylightSaving(daylightSaving);
        } else {
            status.setError(
                start,
                "Time zone name is not unique: \"" + key + "\" in " + zones);
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

    private Map<String, List<TZID>> getTimezoneNameMap(
        Locale locale,
        boolean daylightSaving
    ) {

        List<TZID> zones;
        Map<String, List<TZID>> map = new HashMap<String, List<TZID>>();

        for (TZID tzid : Timezone.getAvailableIDs()) {
            Timezone zone = Timezone.of(tzid);

            String tzName =
                zone.getDisplayName(this.getStyle(daylightSaving), locale);
            zones = map.get(tzName);

            if (zones == null) {
                zones = new ArrayList<TZID>();
                map.put(tzName, zones);
            }

            zones.add(tzid);
        }

        return Collections.unmodifiableMap(map);

    }

    private NameStyle getStyle(boolean daylightSaving) {

        if (daylightSaving) {
            return (
                this.abbreviated
                ? NameStyle.SHORT_DAYLIGHT_TIME
                : NameStyle.LONG_DAYLIGHT_TIME);
        } else {
            return (
                this.abbreviated
                ? NameStyle.SHORT_STANDARD_TIME
                : NameStyle.LONG_STANDARD_TIME);
        }

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class TZNames {

        //~ Instanzvariablen ----------------------------------------------

        private final Map<String, List<TZID>> stdNames;
        private final Map<String, List<TZID>> dstNames;

        //~ Konstruktoren -------------------------------------------------

        TZNames(
            Map<String, List<TZID>> stdNames,
            Map<String, List<TZID>> dstNames
        ) {
            super();

            this.stdNames = stdNames;
            this.dstNames = dstNames;

        }

        //~ Methoden ------------------------------------------------------

        // quick search via hash-access
        List<TZID> search(
            String key,
            boolean daylightSaving
        ) {

            Map<String, List<TZID>> names = (
                daylightSaving
                ? this.dstNames
                : this.stdNames);

            if (names.containsKey(key)) {
                return names.get(key);
            }

            return Collections.emptyList();

        }

        // slow linear search
        List<TZID> search(
            CharSequence text,
            int start,
            boolean daylightSaving,
            int[] lenbuf
        ) {

            Map<String, List<TZID>> names = (
                daylightSaving
                ? this.dstNames
                : this.stdNames);

            int len = text.length();

            for (String name : names.keySet()) {
                int pos = start;
                boolean found = true;
                int count = name.length();

                while (pos < start + count) {
                    if (pos >= len) {
                        found = false;
                        break;
                    }

                    if (name.charAt(pos - start) == text.charAt(pos)) {
                        pos++;
                    } else {
                        found = false;
                        break;
                    }
                }

                if (found) {
                    lenbuf[0] = count;
                    return names.get(name);
                }
            }

            return Collections.emptyList();

        }

    }

}
