/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimezoneGenericProcessor.java) is part of project Time4J.
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

import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.format.Attributes;
import net.time4j.format.Leniency;
import net.time4j.tz.NameStyle;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * <p>Verarbeitet einen Zeitzonen-Namen im <i>generic non-location</i>-Format. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
final class TimezoneGenericProcessor
    implements FormatProcessor<TZID> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final Map<NameStyle, ConcurrentMap<Locale, TZNames>> CACHE_ZONENAMES =
        new EnumMap<NameStyle, ConcurrentMap<Locale, TZNames>>(NameStyle.class);
    private static final int MAX = 25; // maximum size of cache
    private static final String DEFAULT_PROVIDER = "DEFAULT";

    static {
        for (NameStyle style : NameStyle.values()) {
            CACHE_ZONENAMES.put(style, new ConcurrentHashMap<Locale, TZNames>());
        }
    }

    //~ Instanzvariablen --------------------------------------------------

    private final NameStyle style;
    private final FormatProcessor<TZID> fallback;
    private final Set<TZID> preferredZones;

    // quick path optimization
    private final Leniency lenientMode;
    private final Locale locale;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Erzeugt eine neue Instanz. </p>
     *
     * @param   style   filter for selecting the zone names
     */
    TimezoneGenericProcessor(NameStyle style) {
        super();

        this.style = style;
        this.fallback = new LocalizedGMTProcessor(style.isAbbreviation());
        this.preferredZones = null;

        this.lenientMode = Leniency.SMART;
        this.locale = Locale.ROOT;

    }

    /**
     * <p>Erzeugt eine neue Instanz. </p>
     *
     * @param   style           filter for selecting the zone names
     * @param   preferredZones  preferred timezone ids for resolving duplicates
     */
    TimezoneGenericProcessor(
        NameStyle style,
        Set<TZID> preferredZones
    ) {
        super();

        this.style = style;
        this.fallback = new LocalizedGMTProcessor(style.isAbbreviation());
        this.preferredZones = Collections.unmodifiableSet(new LinkedHashSet<TZID>(preferredZones));

        this.lenientMode = Leniency.SMART;
        this.locale = Locale.ROOT;

    }

    private TimezoneGenericProcessor(
        NameStyle style,
        FormatProcessor<TZID> fallback,
        Set<TZID> preferredZones,
        Leniency lenientMode,
        Locale locale
    ) {
        super();

        this.style = style;
        this.fallback = fallback;
        this.preferredZones = preferredZones;

        // quick path members
        this.lenientMode = lenientMode;
        this.locale = locale;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public void print(
        ChronoDisplay formattable,
        Appendable buffer,
        AttributeQuery attributes,
        Set<ElementPosition> positions,
        boolean quickPath
    ) throws IOException {

        TZID tzid;

        if (formattable.hasTimezone()) {
            tzid = formattable.getTimezone();
        } else if (attributes.contains(Attributes.TIMEZONE_ID)) {
            tzid = attributes.get(Attributes.TIMEZONE_ID);
        } else {
            throw new IllegalArgumentException(
                "Cannot extract timezone name in style " + this.style + " from: " + formattable);
        }

        if (tzid instanceof ZonalOffset) { // TODO: fallback-Einstellung prüfen, wenn mehr Daten vorhanden sind
            this.fallback.print(formattable, buffer, attributes, positions, quickPath);
            return;
        }

        String name =
            Timezone.of(tzid).getDisplayName(
                this.style,
                quickPath
                    ? this.locale
                    : attributes.get(Attributes.LANGUAGE, Locale.ROOT));

        if (name.equals(tzid.canonical())) { // name of given style not available => fallback
            this.fallback.print(formattable, buffer, attributes, positions, quickPath);
            return;
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

    }

    @Override
    public void parse(
        CharSequence text,
        ParseLog status,
        AttributeQuery attributes,
        ParsedEntity<?> parsedResult,
        boolean quickPath
    ) {

        int len = text.length();
        int start = status.getPosition();
        int pos = start;

        if (pos >= len) {
            status.setError(start, "Missing timezone name in style " + this.style + ".");
            return;
        }

        Locale lang = (quickPath ? this.locale : attributes.get(Attributes.LANGUAGE, Locale.ROOT));
        Leniency leniency = (quickPath ? this.lenientMode : attributes.get(Attributes.LENIENCY, Leniency.SMART));

        // evaluation of relevant part of input which might contain the generic timezone name
        StringBuilder name = new StringBuilder();

        while (pos < len) {
            char c = text.charAt(pos);

            if (
                Character.isLetter(c) // tz names must start with a letter
                || (!this.style.isAbbreviation() && (pos > start) && !Character.isDigit(c))
            ) {
                // long tz names can contain almost every char - with the exception of digits
                name.append(c);
                pos++;
            } else {
                break;
            }
        }

        String key = name.toString().trim();
        pos = start + key.length();

        // fallback-case (fixed offset)
        if (key.startsWith("GMT") || key.startsWith("UT")) {
            this.fallback.parse(text, status, attributes, parsedResult, quickPath);
            return; // TODO: fallback-Einstellung prüfen, wenn mehr Daten vorhanden sind
        }

        // Zeitzonennamen im Cache suchen und ggf. Cache füllen
        ConcurrentMap<Locale, TZNames> cache = CACHE_ZONENAMES.get(this.style);
        TZNames tzNames = cache.get(lang);

        if (tzNames == null) {
            Map<String, List<TZID>> genericNames = this.getTimezoneNameMap(lang);
            tzNames = new TZNames(genericNames);

            if (cache.size() < MAX) {
                TZNames tmp = cache.putIfAbsent(lang, tzNames);

                if (tmp != null) {
                    tzNames = tmp;
                }
            }
        }

        // Zeitzonen-IDs bestimmen
        int[] lenbuf = new int[1];
        lenbuf[0] = pos;
        List<TZID> genericZones = readZones(tzNames, key, lenbuf);
        int sum = genericZones.size();

        if (sum == 0) {
            status.setError(
                start,
                "Unknown timezone name: " + key);
            return;
        }

        if ((sum > 1) && !leniency.isStrict()) {
            genericZones = excludeWinZones(genericZones);
            sum = genericZones.size();
        }

        List<TZID> genericZonesOriginal = genericZones;

        if ((sum > 1) && !leniency.isLax()) {
            TZID pref = attributes.get(Attributes.TIMEZONE_ID, ZonalOffset.UTC);
            if (pref instanceof ZonalOffset) {
                genericZones = this.resolveUsingPreferred(genericZones, lang, leniency);
            } else {
                boolean resolved = false;
                for (TZID tzid : genericZones) {
                    if (tzid.canonical().equals(pref.canonical())) {
                        genericZones = Collections.singletonList(tzid);
                        resolved = true;
                        break;
                    }
                }
                if (!resolved) {
                    genericZones = this.resolveUsingPreferred(genericZones, lang, leniency);
                }
            }
        }

        sum = genericZones.size();
        
        if (sum == 0) {
            List<String> candidates = new ArrayList<String>();
            for (TZID tzid : genericZonesOriginal) {
                candidates.add(tzid.canonical());
            }
            status.setError(
                start,
                "Time zone name \""
                    + key
                    + "\" not found among preferred timezones in locale "
                    + lang
                    + ", style=" + this.style
                    + ", candidates=" + candidates);
            return;
        }

        // remove alternative provider zones if default provider zone exists
        if (sum > 1) {
            List<TZID> filtered = null;
            for (TZID id : genericZones) {
                if (id.canonical().indexOf('~') == -1) {
                    if (filtered == null) {
                        filtered = new ArrayList<TZID>();
                    }
                    filtered.add(id);
                }
            }
            if (filtered != null) {
                genericZones = filtered;
                sum = genericZones.size();
            }
        }

        // final step: determining the result
        if ((sum == 1) || leniency.isLax()) {
            parsedResult.put(TimezoneElement.TIMEZONE_ID, genericZones.get(0));
            status.setPosition(lenbuf[0]);
        } else {
            status.setError(
                start,
                "Time zone name of style " + this.style + " is not unique: \"" + key + "\" in "
                + toString(genericZones));
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

        return new TimezoneGenericProcessor(
            this.style,
            this.fallback,
            this.preferredZones,
            attributes.get(Attributes.LENIENCY, Leniency.SMART),
            attributes.get(Attributes.LANGUAGE, Locale.ROOT)
        );

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof TimezoneGenericProcessor) {
            TimezoneGenericProcessor that = (TimezoneGenericProcessor) obj;
            return (
                (this.style == that.style)
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

        return this.style.hashCode();

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(64);
        sb.append(this.getClass().getName());
        sb.append("[style=");
        sb.append(this.style);
        sb.append(", preferredZones=");
        sb.append(this.preferredZones);
        sb.append(']');
        return sb.toString();

    }

    private Map<String, List<TZID>> getTimezoneNameMap(Locale locale) {

        List<TZID> zones;
        Map<String, List<TZID>> map = new HashMap<String, List<TZID>>();

        for (TZID tzid : Timezone.getAvailableIDs()) {
            String tzName = Timezone.getDisplayName(tzid, this.style, locale);

            if (tzName.equals(tzid.canonical())) {
                continue; // registrierte NameProvider haben nichts gefunden!
            }

            zones = map.get(tzName);

            if (zones == null) {
                zones = new ArrayList<TZID>();
                map.put(tzName, zones);
            }

            zones.add(tzid);
        }

        return Collections.unmodifiableMap(map);

    }

    private static List<TZID> readZones(
        TZNames tzNames,
        String key,
        int[] lenbuf
    ) {
        
        List<TZID> zones = tzNames.search(key);

        if (zones.isEmpty()) {
            int last = key.length() - 1;
            if (!Character.isLetter(key.charAt(last))) { // maybe interpunctuation char?
                zones = tzNames.search(key.substring(0, last));
                if (!zones.isEmpty()) {
                    lenbuf[0]--;
                }
            }
        }
        
        return zones;
        
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

        private final Map<String, List<TZID>> names;

        //~ Konstruktoren -------------------------------------------------

        TZNames(Map<String, List<TZID>> names) {
            super();

            this.names = names;

        }

        //~ Methoden ------------------------------------------------------

        // quick search via hash-access
        List<TZID> search(String key) {

            if (this.names.containsKey(key)) {
                return this.names.get(key);
            }

            return Collections.emptyList();

        }

    }

}
