/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimezoneIDProcessor.java) is part of project Time4J.
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

import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.tz.TZID;
import net.time4j.tz.TimeZone;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * <p>Verarbeitet eine Zeitzonen-ID. </p>
 *
 * @author  Meno Hochschild
 */
enum TimezoneIDProcessor
    implements FormatProcessor<TZID> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /** Singleton. */
    INSTANCE;

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
        }

        int start = -1;
        int printed = 0;

        if (buffer instanceof CharSequence) {
            start = ((CharSequence) buffer).length();
        }

        String canonical = tzid.canonical();
        buffer.append(canonical);
        printed = canonical.length();

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

        Leniency leniency =
            step.getAttribute(Attributes.LENIENCY, attributes, Leniency.SMART);

        // Zeitzonen-ID einlesen
        StringBuilder name = new StringBuilder();

        while (pos < len) {
            char c = text.charAt(pos);

            if ( // siehe Theory-Datei in TZDB
                (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z')
                || (c == '-')
                || (c == '_')
                || (c == '/')
            ) {
                name.append(c);
                pos++;
            } else {
                break;
            }
        }

        if (!Character.isLetter(name.charAt(name.length() - 1))) {
            name.deleteCharAt(name.length() - 1);
            pos--;
        }

        String key = name.toString();

        // Offset prüfen
        if (key.isEmpty()) {
            status.setError(start, "Missing valid time zone id.");
            return;
        } else if (key.startsWith("Etc/GMT")) {
            status.setError(
                start,
                "Inverse Etc/GMT-Offsets are not supported, "
                + "use UTC-Offsets instead.");
            return;
        } else if (key.equals("Z")) {
            parsedResult.put(ZonalElement.TIMEZONE_ID, ZonalOffset.UTC);
            status.setPosition(pos);
            return;
        } else if (
            key.equals("UTC")
            || key.equals("GMT")
            || key.equals("UT")
        ) {
            if (len > pos) {
                char c = text.charAt(pos);
                if ((c == '+') || (c == '-')) {
                    status.setPosition(pos);
                    TimezoneOffsetProcessor.EXTENDED_LONG_PARSER.parse(
                        text, status, attributes, parsedResult, step);
                    return;
                }
            }
            parsedResult.put(ZonalElement.TIMEZONE_ID, ZonalOffset.UTC);
            status.setPosition(pos);
            return;
        }

        // binäre Suche
        List<TZID> zones = TimeZone.getAvailableIDs();
        int low = 0;
        int high = zones.size() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            TZID zone = zones.get(mid);
            int cmp = zone.canonical().compareTo(key);

            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                parsedResult.put(ZonalElement.TIMEZONE_ID, zone);
                status.setPosition(pos);
                return;
            }
        }

        status.setError(start, "Cannot parse to time zone id: " + key);

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

}
