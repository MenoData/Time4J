/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (LocalizedGMTProcessor.java) is part of project Time4J.
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
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.Leniency;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static net.time4j.tz.OffsetSign.AHEAD_OF_UTC;
import static net.time4j.tz.OffsetSign.BEHIND_UTC;


/**
 * <p>Verarbeitet einen lokalisierten Zeitzonen-Offset. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
final class LocalizedGMTProcessor
    implements FormatProcessor<TZID> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final char UNICODE_LRM = '\u200E';

    //~ Instanzvariablen --------------------------------------------------

    private final boolean abbreviated;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Erzeugt eine neue Instanz. </p>
     *
     * @param   abbreviated     short form of localized gmt offset?
     */
    LocalizedGMTProcessor(boolean abbreviated) {
        super();

        this.abbreviated = abbreviated;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public void print(
        ChronoDisplay formattable,
        Appendable buffer,
        AttributeQuery attributes,
        Set<ElementPosition> positions,
        FormatStep step
    ) throws IOException {

        int start = -1;
        int printed;

        if (buffer instanceof CharSequence) {
            start = ((CharSequence) buffer).length();
        }

        TZID tzid = null;
        ZonalOffset offset;

        if (formattable.hasTimezone()) {
            tzid = formattable.getTimezone();
        }

        if (tzid == null) {
            offset = getOffset(formattable, step, attributes);
        } else if (tzid instanceof ZonalOffset) {
            offset = (ZonalOffset) tzid;
        } else if (formattable instanceof UnixTime) {
            offset = Timezone.of(tzid).getOffset((UnixTime) formattable);
        } else {
            throw new IllegalArgumentException(
                "Cannot extract timezone offset from: " + formattable);
        }

        Locale locale = step.getAttribute(Attributes.LANGUAGE, attributes, Locale.ROOT);
        String gmtPrefix = getGMTPrefix(locale);
        buffer.append(gmtPrefix);
        printed = gmtPrefix.length();

        int total = offset.getIntegralAmount();
        int fraction = offset.getFractionalAmount();

        if ((total != 0) || (fraction != 0)) {

            // hack for arabic locales: LRM-marker for following sign required
            if ("ar".equals(locale.getLanguage())) {
                buffer.append(UNICODE_LRM);
                printed++;
            }

            buffer.append((offset.getSign() == BEHIND_UTC) ? '-' : '+');
            printed++;

            int h = offset.getAbsoluteHours();
            int m = offset.getAbsoluteMinutes();

            if ((h < 10) && !this.abbreviated) {
                buffer.append('0');
                printed++;
            }

            String hours = String.valueOf(h);

            for (int i = 0; i < hours.length(); i++) {
                buffer.append(hours.charAt(i));
                printed++;
            }

            if ((m != 0) || !this.abbreviated) {
                buffer.append(':');
                printed++;

                if (m < 10) {
                    buffer.append('0');
                    printed++;
                }

                String minutes = String.valueOf(m);

                for (int i = 0; i < minutes.length(); i++) {
                    buffer.append(minutes.charAt(i));
                    printed++;
                }
            }
        }

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
        Map<ChronoElement<?>, Object> parsedResult,
        FormatStep step
    ) {

        int len = text.length();
        int start = status.getPosition();
        int pos = start;

        if (pos >= len) {
            status.setError(start, "Missing localized GMT offset.");
            return;
        }

        Locale locale = step.getAttribute(Attributes.LANGUAGE, attributes, Locale.ROOT);
        String gmtPrefix = getGMTPrefix(locale);
        String[] zeroOffsets = { "GMT", gmtPrefix, "UTC", "UT" };
        boolean found = false;
        boolean caseInsensitive =
            step.getAttribute(
                Attributes.PARSE_CASE_INSENSITIVE,
                attributes,
                Boolean.TRUE
            ).booleanValue();

        for (String zeroOffset : zeroOffsets) {
            int zl = zeroOffset.length();

            if (len - pos >= zl) {
                String compare = text.subSequence(pos, pos + zl).toString();

                if (
                    (caseInsensitive && compare.equalsIgnoreCase(zeroOffset))
                    || (!caseInsensitive && compare.equals(zeroOffset))
                ) {
                    found = true;
                    pos += zl;
                    break;
                }
            }
        }

        if (!found) {
            status.setError(
                start,
                "Missing prefix in localized offset: " + gmtPrefix);
            return;
        } else if (pos >= len) {
            parsedResult.put(TimezoneElement.TIMEZONE_OFFSET, ZonalOffset.UTC);
            status.setPosition(pos);
            return;
        }

        char c = text.charAt(pos);

        // hack for arabic locales: LRM-marker before sign possible
        if (
            (c == UNICODE_LRM)
            && "ar".equals(locale.getLanguage())
        ) {
            if (pos + 1 >= len) {
                parsedResult.put(TimezoneElement.TIMEZONE_OFFSET, ZonalOffset.UTC);
                status.setPosition(pos);
                return;
            }

            pos++;
            c = text.charAt(pos);
        }

        OffsetSign sign;

        if (c == '+') {
            sign = AHEAD_OF_UTC;
            pos++;
        } else if (c == '-') {
            sign = BEHIND_UTC;
            pos++;
        } else {
            parsedResult.put(TimezoneElement.TIMEZONE_OFFSET, ZonalOffset.UTC);
            status.setPosition(pos);
            return;
        }

        int hours = parseHours(text, pos);

        if (hours == -1000) {
            status.setError(
                pos,
                "Missing hour part in localized GMT offset.");
            return;
        }

        if (hours < 0) {
            hours = ~hours;
            pos++;
        } else {
            pos += 2;
        }

        if (pos >= len) {
            if (this.abbreviated) {
                parsedResult.put(
                    TimezoneElement.TIMEZONE_OFFSET,
                    ZonalOffset.ofHours(sign, hours));
                status.setPosition(pos);
            } else {
                status.setError(
                    pos,
                    "Missing minute part in localized GMT offset.");
            }
            return;
        }

        Leniency leniency =
            step.getAttribute(
                Attributes.LENIENCY,
                attributes,
                Leniency.SMART
            );

        if (text.charAt(pos) == ':') {
            pos++;
        } else if (this.abbreviated) {
            parsedResult.put(
                TimezoneElement.TIMEZONE_OFFSET,
                ZonalOffset.ofHours(sign, hours));
            status.setPosition(pos);
            return;
        } else if (leniency.isStrict()) {
            status.setError(pos, "Colon expected in localized GMT offset.");
            return;
        }

        int minutes = parseMinutes(text, pos);

        if (minutes == -1000) {
            status.setError(
                pos,
                "Minute part in localized GMT offset "
                + "does not match expected pattern mm.");
            return;
        }

        pos += 2;
        ZonalOffset offset = ZonalOffset.ofHoursMinutes(sign, hours, minutes);
        parsedResult.put(TimezoneElement.TIMEZONE_OFFSET, offset);
        status.setPosition(pos);

    }

    @Override
    public ChronoElement<TZID> getElement() {

        return TimezoneElement.TIMEZONE_OFFSET;

    }

    @Override
    public FormatProcessor<TZID> withElement(ChronoElement<TZID> element) {

        return this;

    }

    @Override
    public boolean isNumerical() {

        return false;

    }

    private static String getGMTPrefix(Locale locale) {

        CalendarText ct = CalendarText.getIsoInstance(locale);

        if (ct.getTextForms().isEmpty()) {
            return "GMT";
        }

        return ct.getTextForms().get("prefixGMTOffset");

    }

    private static ZonalOffset getOffset(
        ChronoDisplay formattable,
        FormatStep step,
        AttributeQuery attributes
    ) {

        AttributeQuery aq = step.getQuery(attributes);

        if (aq.contains(Attributes.TIMEZONE_ID)) {
            TZID tzid = aq.get(Attributes.TIMEZONE_ID);

            if (tzid instanceof ZonalOffset) {
                return (ZonalOffset) tzid;
            }
        }

        throw new IllegalArgumentException(
            "Cannot extract timezone offset from format attributes for: "
            + formattable);

    }

    private static int parseMinutes(
        CharSequence text,
        int pos
    ) {

        int total = 0;

        for (int i = 0; i < 2; i++) {
            char c;

            if (pos + i >= text.length()) {
                c = '\u0000';
            } else {
                c = text.charAt(pos + i);
            }

            if ((c >= '0') && (c <= '9')) {
                total = total * 10 + (c - '0');
            } else {
                return -1000;
            }
        }

        return total;

    }

    private static int parseHours(
        CharSequence text,
        int pos
    ) {

        int total = 0;

        for (int i = 0; i < 2; i++) {
            char c;

            if (pos + i >= text.length()) {
                c = '\u0000';
            } else {
                c = text.charAt(pos + i);
            }

            if ((c >= '0') && (c <= '9')) {
                total = total * 10 + (c - '0');
            } else if (i == 0) {
                return -1000;
            } else {
                return ~total;
            }
        }

        return total;

    }

}
