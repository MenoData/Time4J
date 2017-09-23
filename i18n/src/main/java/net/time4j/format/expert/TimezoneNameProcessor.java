/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
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

package net.time4j.format.expert;

import net.time4j.base.UnixTime;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.FlagElement;
import net.time4j.format.Attributes;
import net.time4j.format.Leniency;
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
 * @since   3.0
 */
final class TimezoneNameProcessor
    implements FormatProcessor<TZID> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final ConcurrentMap<Locale, TZNames> CACHE_ABBREVIATIONS =
        new ConcurrentHashMap<Locale, TZNames>();
    private static final ConcurrentMap<Locale, TZNames> CACHE_ZONENAMES =
        new ConcurrentHashMap<Locale, TZNames>();
    private static final int MAX = 25; // maximum size of cache
    private static final String DEFAULT_PROVIDER = "DEFAULT";

    //~ Instanzvariablen --------------------------------------------------

    private final boolean abbreviated;
    private final FormatProcessor<TZID> fallback;
    private final Set<TZID> preferredZones;

    // quick path optimization
    private final Leniency lenientMode;
    private final Locale locale;
    private final int protectedLength;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Erzeugt eine neue Instanz. </p>
     *
     * @param   abbreviated     abbreviations to be used?
     */
    TimezoneNameProcessor(boolean abbreviated) {
        super();

        this.abbreviated = abbreviated;
        this.fallback = new LocalizedGMTProcessor(abbreviated);
        this.preferredZones = null;

        this.lenientMode = Leniency.SMART;
        this.locale = Locale.ROOT;
        this.protectedLength = 0;

    }

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
        this.preferredZones = Collections.unmodifiableSet(new LinkedHashSet<TZID>(preferredZones));

        this.lenientMode = Leniency.SMART;
        this.locale = Locale.ROOT;
        this.protectedLength = 0;

    }

    private TimezoneNameProcessor(
        boolean abbreviated,
        FormatProcessor<TZID> fallback,
        Set<TZID> preferredZones,
        Leniency lenientMode,
        Locale locale,
        int protectedLength
    ) {
        super();

        this.abbreviated = abbreviated;
        this.fallback = fallback;
        this.preferredZones = preferredZones;

        // quick path members
        this.lenientMode = lenientMode;
        this.locale = locale;
        this.protectedLength = protectedLength;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public int print(
        ChronoDisplay formattable,
        Appendable buffer,
        AttributeQuery attributes,
        Set<ElementPosition> positions,
        boolean quickPath
    ) throws IOException {

        if (!formattable.hasTimezone()) {
            throw new IllegalArgumentException(
                "Cannot extract timezone name from: " + formattable);
        }

        TZID tzid = formattable.getTimezone();

        if (tzid instanceof ZonalOffset) {
            return this.fallback.print(formattable, buffer, attributes, positions, quickPath);
        }

        String name;

        if (formattable instanceof UnixTime) {
            Timezone zone = Timezone.of(tzid);
            UnixTime ut = UnixTime.class.cast(formattable);

            name =
                zone.getDisplayName(
                    this.getStyle(zone.isDaylightSaving(ut)),
                    quickPath
                        ? this.locale
                        : attributes.get(Attributes.LANGUAGE, Locale.ROOT));
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
                    TimezoneElement.TIMEZONE_ID,
                    start,
                    start + printed));
        }

        return printed;

    }

    @Override
    public void parse(
        CharSequence text,
        ParseLog status,
        AttributeQuery attributes,
        ParsedEntity<?> parsedResult,
        boolean quickPath
    ) {

        int start = status.getPosition();
        int len = text.length();
        int protectedChars = (quickPath ? this.protectedLength : attributes.get(Attributes.PROTECTED_CHARACTERS, 0));

        if (protectedChars > 0) {
            len -= protectedChars;
        }

        if (start >= len) {
            status.setError(start, "Missing timezone name.");
            return;
        }

        Locale lang = (quickPath ? this.locale : attributes.get(Attributes.LANGUAGE, Locale.ROOT));
        Leniency leniency = (quickPath ? this.lenientMode : attributes.get(Attributes.LENIENCY, Leniency.SMART));
        String key = this.extractRelevantKey(text, start, len);

        // fallback-case (fixed offset)
        if (key.startsWith("GMT") || key.startsWith("UT")) {
            this.fallback.parse(text, status, attributes, parsedResult, quickPath);
            return;
        }

        // Zeitzonennamen im Cache suchen und ggf. Cache füllen
        ConcurrentMap<Locale, TZNames> cache = (this.abbreviated ? CACHE_ABBREVIATIONS : CACHE_ZONENAMES);
        TZNames tzNames = cache.get(lang);

        if (tzNames == null) {
            ZoneLabels stdNames = this.createZoneNames(lang, false);
            ZoneLabels dstNames = this.createZoneNames(lang, true);
            tzNames = new TZNames(stdNames, dstNames);

            if (cache.size() < MAX) {
                TZNames tmp = cache.putIfAbsent(lang, tzNames);

                if (tmp != null) {
                    tzNames = tmp;
                }
            }
        }

        // prefix-matching of tz names
        List<TZID> stdZones = new ArrayList<TZID>();
        List<TZID> dstZones = new ArrayList<TZID>();
        int[] lenbuf = new int[2];
        tzNames.search(text.subSequence(0, len), start, stdZones, dstZones, lenbuf);

        int sum = stdZones.size() + dstZones.size();

        if (sum == 0) {
            status.setError(
                start,
                "\"" + key + "\" does not match any known timezone name.");
            return;
        }

        // exclude win-zones if needed
        if ((sum > 1) && !leniency.isStrict()) {
            stdZones = excludeWinZones(stdZones);
            dstZones = excludeWinZones(dstZones);
            sum = stdZones.size() + dstZones.size();
        }

        // match against preferred zones
        List<TZID> stdZonesOriginal = stdZones;
        List<TZID> dstZonesOriginal = dstZones;

        if ((sum > 1) && !leniency.isLax()) {
            TZID pref = attributes.get(Attributes.TIMEZONE_ID, ZonalOffset.UTC);
            boolean resolved = false;
            if (!(pref instanceof ZonalOffset)) {
                for (TZID tzid : stdZones) {
                    if (tzid.canonical().equals(pref.canonical())) {
                        stdZones = Collections.singletonList(tzid);
                        dstZones = Collections.emptyList();
                        resolved = true;
                        break;
                    }
                }
                if (!resolved) {
                    for (TZID tzid : dstZones) {
                        if (tzid.canonical().equals(pref.canonical())) {
                            stdZones = Collections.emptyList();
                            dstZones = Collections.singletonList(tzid);
                            resolved = true;
                            break;
                        }
                    }
                }
            }
            if (!resolved) {
                if (stdZones.size() > 0) {
                    stdZones = this.resolveUsingPreferred(stdZones, lang, leniency);
                }
                if (dstZones.size() > 0) {
                    dstZones = this.resolveUsingPreferred(dstZones, lang, leniency);
                }
            }
        }

        sum = stdZones.size() + dstZones.size();

        // abort if no match against preferred zones
        if (sum == 0) {
            List<String> candidates = new ArrayList<String>();
            for (TZID tzid : stdZonesOriginal) {
                candidates.add(tzid.canonical());
            }
            for (TZID tzid : dstZonesOriginal) {
                candidates.add(tzid.canonical());
            }
            status.setError(
                start,
                "Time zone name \""
                    + key
                    + "\" not found among preferred timezones in locale "
                    + lang
                    + ", candidates=" + candidates);
            return;
        }

        // merge the zone-lists
        boolean daylightSaving = false;
        List<TZID> zones;

        if (stdZones.size() > 0) {
            zones = stdZones;
            if (
                (sum == 2)
                && (dstZones.size() == 1)
                && (stdZones.get(0).canonical().equals(dstZones.get(0).canonical())) // special case
            ) {
                // ignore dstZones (sum == 1)
            } else if (!dstZones.isEmpty()) {
                zones = new ArrayList<TZID>(zones);
                zones.addAll(dstZones); // for better error message if not unique
            }
        } else {
            zones = dstZones;
            daylightSaving = true;
        }

        // final step
        if ((zones.size() == 1) || leniency.isLax()) {
            parsedResult.put(TimezoneElement.TIMEZONE_ID, zones.get(0));
            parsedResult.put(FlagElement.DAYLIGHT_SAVING, Boolean.valueOf(daylightSaving));
            status.setPosition(lenbuf[daylightSaving ? 1 : 0]);
        } else {
            status.setError(
                start,
                "Time zone name is not unique: \"" + key + "\" in " + toString(zones));
        }

    }

    @Override
    public ChronoElement<TZID> getElement() {

        return TimezoneElement.TIMEZONE_ID;

    }

    @Override
    public FormatProcessor<TZID> withElement(ChronoElement<TZID> element) {

        return this;

    }

    @Override
    public boolean isNumerical() {

        return false;

    }

    @Override
    public FormatProcessor<TZID> quickPath(
        ChronoFormatter<?> formatter,
        AttributeQuery attributes,
        int reserved
    ) {

        return new TimezoneNameProcessor(
            this.abbreviated,
            this.fallback,
            this.preferredZones,
            attributes.get(Attributes.LENIENCY, Leniency.SMART),
            attributes.get(Attributes.LANGUAGE, Locale.ROOT),
            attributes.get(Attributes.PROTECTED_CHARACTERS, 0)
        );

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof TimezoneNameProcessor) {
            TimezoneNameProcessor that = (TimezoneNameProcessor) obj;
            return (
                (this.abbreviated == that.abbreviated)
                && ((this.preferredZones == null)
                    ? (that.preferredZones == null)
                    : this.preferredZones.equals(that.preferredZones))
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return ((this.preferredZones == null) ? 0 : this.preferredZones.hashCode()) + (this.abbreviated ? 1 : 0);

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(64);
        sb.append(this.getClass().getName());
        sb.append("[abbreviated=");
        sb.append(this.abbreviated);
        sb.append(", preferredZones=");
        sb.append(this.preferredZones);
        sb.append(']');
        return sb.toString();

    }

    private String extractRelevantKey(
        CharSequence text,
        int offset,
        int len
    ) {

        // evaluation of relevant part of input which might contain the timezone name
        StringBuilder name = new StringBuilder();
        int pos = offset;

        while (pos < len) {
            char c = text.charAt(pos);

            if (
                Character.isLetter(c) // tz names must start with a letter
                || (!this.abbreviated && (pos > offset) && !Character.isDigit(c))
            ) {
                // long tz names can contain almost every char - with the exception of digits
                name.append(c);
                pos++;
            } else {
                break;
            }
        }

        return name.toString().trim();

    }

    private ZoneLabels createZoneNames(
        Locale locale,
        boolean daylightSaving
    ) {

        ZoneLabels.Node node = null;
        NameStyle style = this.getStyle(daylightSaving);

        for (TZID tzid : Timezone.getAvailableIDs()) {
            String tzName = Timezone.getDisplayName(tzid, style, locale);

            if (tzName.equals(tzid.canonical())) {
                continue; // registrierte NameProvider haben nichts gefunden!
            }

            node = ZoneLabels.insert(node, tzName, tzid);
        }

        return new ZoneLabels(node);

    }

    private static List<TZID> excludeWinZones(List<TZID> zones) {

        if (zones.size() > 1) {
            List<TZID> candidates = new ArrayList<TZID>(zones);

            for (int i = 1, n = zones.size(); i < n; i++) {
                TZID tzid = zones.get(i);

                if (tzid.canonical().startsWith("WINDOWS~")) {
                    candidates.remove(tzid);
                }
            }

            if (!candidates.isEmpty()) {
                return candidates;
            }
        }

        return zones;

    }
    
    private List<TZID> resolveUsingPreferred(
        List<TZID> zones,
        Locale locale,
        Leniency leniency
    ) {

        Map<String, List<TZID>> matched = new HashMap<String, List<TZID>>();
        matched.put(DEFAULT_PROVIDER, new ArrayList<TZID>());

        for (TZID tz : zones) {
            String id = tz.canonical();
            Set<TZID> prefs = this.preferredZones;
            String provider = DEFAULT_PROVIDER;
            int index = id.indexOf('~');

            if (index >= 0) {
                provider = id.substring(0, index);
            }

            if (prefs == null) {
                prefs =
                    Timezone.getPreferredIDs(
                        locale,
                        leniency.isSmart(),
                        provider);
            }

            for (TZID p : prefs) {
                if (p.canonical().equals(id)) {
                    List<TZID> candidates = matched.get(provider);
                    if (candidates == null) {
                        candidates = new ArrayList<TZID>();
                        matched.put(provider, candidates);
                    }
                    candidates.add(p);
                    break;
                }
            }
        }

        List<TZID> candidates = matched.get(DEFAULT_PROVIDER);
        List<TZID> result = zones;

        if (candidates.isEmpty()) {
            matched.remove(DEFAULT_PROVIDER);
            boolean found = false;
            for (String provider : matched.keySet()) {
                candidates = matched.get(provider);
                if (!candidates.isEmpty()) {
                    found = true;
                    result = candidates;
                    break;
                }
            }
            if (!found) {
                result = Collections.emptyList();
            }
        } else {
            result = candidates;
        }

        return result;

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

    private static String toString(List<TZID> ids) {

        StringBuilder sb = new StringBuilder(ids.size() * 16);
        sb.append('{');
        boolean first = true;

        for (TZID tzid : ids) {
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            sb.append(tzid.canonical());
        }

        return sb.append('}').toString();

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class TZNames {

        //~ Instanzvariablen ----------------------------------------------

        private final ZoneLabels stdNames;
        private final ZoneLabels dstNames;

        //~ Konstruktoren -------------------------------------------------

        TZNames(
            ZoneLabels stdNames,
            ZoneLabels dstNames
        ) {
            super();

            this.stdNames = stdNames;
            this.dstNames = dstNames;

        }

        //~ Methoden ------------------------------------------------------

        void search(
            CharSequence text,
            int offset,
            List<TZID> stdZones,
            List<TZID> dstZones,
            int[] lenbuf
        ) {

            String stdKey = this.stdNames.longestPrefixOf(text, offset);
            int stdLen = stdKey.length();
            lenbuf[0] = offset + stdLen;

            String dstKey = this.dstNames.longestPrefixOf(text, offset);
            int dstLen = dstKey.length();
            lenbuf[1] = offset + dstLen;

            if (dstLen > stdLen) {
                dstZones.addAll(this.dstNames.find(dstKey));
            } else if (dstLen < stdLen) {
                stdZones.addAll(this.stdNames.find(stdKey));
            } else if (stdLen > 0) {
                stdZones.addAll(this.stdNames.find(stdKey));
                dstZones.addAll(this.dstNames.find(dstKey));
            }

        }

    }

}
