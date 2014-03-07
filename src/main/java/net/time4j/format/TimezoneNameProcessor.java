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

    private static final ConcurrentMap<Locale, TZNames> CACHE =
        new ConcurrentHashMap<Locale, TZNames>();
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
        this.preferredZones = new LinkedHashSet<TZID>(preferredZones);

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
        Leniency leniency =
            step.getAttribute(Attributes.LENIENCY, attributes, Leniency.SMART);

        // Zeitzonennamen einlesen (nur Buchstaben)
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

        // fallback-case (fixed offset)
        if (
            key.startsWith("GMT")
            || key.startsWith("UT")
        ) {
            this.fallback.parse(text, status, attributes, parsedResult, step);
            return;
        }

        // Zeitzonennamen im Cache suchen und ggf. Cache füllen
        TZNames tzNames = CACHE.get(locale);

        if (tzNames == null) {
            Map<String, List<TZID>> stdNames = this.getTZNames(locale, false);
            Map<String, List<TZID>> dstNames = this.getTZNames(locale, true);
            tzNames = new TZNames(stdNames, dstNames);

            if (CACHE.size() < MAX) {
                TZNames tmp = CACHE.putIfAbsent(locale, tzNames);

                if (tmp != null) {
                    tzNames = tmp;
                }
            }
        }

        // Zeitzonen-IDs bestimmen
        boolean daylightSaving = false;
        List<TZID> zones = null;

        if (tzNames.stdNames.containsKey(key)) {
            zones = tzNames.stdNames.get(key);
        } else if (tzNames.dstNames.containsKey(key)) {
            daylightSaving = true;
            zones = tzNames.dstNames.get(key);
        }

        if ((zones == null) || zones.isEmpty()) {
            status.setError(
                start,
                "Unknown time zone name: " + key);
            return;
        } else if (
            (zones.size() > 1)
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

            zones = candidates;
        }

        if (zones.isEmpty()) {
            status.setError(
                start,
                "Time zone id not found among preferred time zones.");
        } else if (
            (zones.size() == 1)
            || leniency.isLax()
        ) {
            parsedResult.put(ZonalElement.TIMEZONE_ID, zones.get(0));
            status.setPosition(pos);
            status.setDaylightSaving(daylightSaving);
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

    private Map<String, List<TZID>> getTZNames(
        Locale locale,
        boolean daylightSaving
    ) {

        List<TZID> zones;
        Map<String, List<TZID>> map = new HashMap<String, List<TZID>>();

        for (TZID tzid : TimeZone.getAvailableIDs()) {
            TimeZone zone = TimeZone.of(tzid);

            String tzName =
                zone.getDisplayName(daylightSaving, this.abbreviated, locale)
                    .toUpperCase(locale);
            zones = map.get(tzName);

            if (zones == null) {
                zones = new ArrayList<TZID>();
                map.put(tzName, zones);
            }

            zones.add(tzid);
        }

        return Collections.unmodifiableMap(map);

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

    }

}
