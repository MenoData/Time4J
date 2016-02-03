/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimezoneOffsetProcessor.java) is part of project Time4J.
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
import net.time4j.format.DisplayMode;
import net.time4j.format.Leniency;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.time4j.format.DisplayMode.FULL;
import static net.time4j.format.DisplayMode.LONG;
import static net.time4j.format.DisplayMode.MEDIUM;
import static net.time4j.format.DisplayMode.SHORT;
import static net.time4j.tz.OffsetSign.AHEAD_OF_UTC;
import static net.time4j.tz.OffsetSign.BEHIND_UTC;


/**
 * <p>Verarbeitet einen festen Zeitzonen-Offset. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
final class TimezoneOffsetProcessor
    implements FormatProcessor<TZID> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Spezial-Instanz nur zum Parsen im LONG-extended-Format ohne
     * Ersatztext (Formatieren nicht m&ouml;glich). </p>
     */
    static final TimezoneOffsetProcessor EXTENDED_LONG_PARSER =
        new TimezoneOffsetProcessor();

    //~ Instanzvariablen --------------------------------------------------

    private final DisplayMode precision;
    private final boolean extended;
    private final List<String> zeroOffsets;

    // quick path optimization
    private final boolean caseInsensitive;
    private final Leniency lenientMode;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Erzeugt eine neue Instanz. </p>
     *
     * @param   precision       display mode of offset format
     * @param   extended        extended or basic ISO-8601-mode
     * @param   zeroOffsets     list of replacement texts if offset is zero
     * @throws  IllegalArgumentException if replacement text is white-space-only
     */
    TimezoneOffsetProcessor(
        DisplayMode precision,
        boolean extended,
        List<String> zeroOffsets
    ) {
        super();

        if (precision == null) {
            throw new NullPointerException("Missing display mode.");
        } else if (zeroOffsets.isEmpty()) {
            throw new IllegalArgumentException("Missing zero offsets.");
        }

        List<String> offsets = new ArrayList<String>(zeroOffsets);

        for (String zo : offsets) {
            if (zo.trim().isEmpty()) {
                throw new IllegalArgumentException(
                    "Zero offset must not be white-space-only.");
            }
        }

        this.precision = precision;
        this.extended = extended;
        this.zeroOffsets = Collections.unmodifiableList(offsets);

        this.caseInsensitive = true;
        this.lenientMode = Leniency.SMART;

    }

    private TimezoneOffsetProcessor() {
        super();

        this.precision = LONG;
        this.extended = true;
        this.zeroOffsets = Collections.emptyList();

        this.caseInsensitive = true;
        this.lenientMode = Leniency.SMART;

    }

    private TimezoneOffsetProcessor(
        DisplayMode precision,
        boolean extended,
        List<String> zeroOffsets,
        boolean caseInsensitive,
        Leniency lenientMode
    ) {
        super();

        this.precision = precision;
        this.extended = extended;
        this.zeroOffsets = zeroOffsets;

        this.caseInsensitive = caseInsensitive;
        this.lenientMode = lenientMode;

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

        int start = -1;
        int printed = 0;

        if (buffer instanceof CharSequence) {
            start = ((CharSequence) buffer).length();
        }

        TZID tzid = null;
        ZonalOffset offset;

        if (formattable.hasTimezone()) {
            tzid = formattable.getTimezone();
        }

        if (tzid == null) {
            offset = getOffset(formattable, attributes);
        } else if (tzid instanceof ZonalOffset) {
            offset = (ZonalOffset) tzid;
        } else if (formattable instanceof UnixTime) {
            offset = Timezone.of(tzid).getOffset((UnixTime) formattable);
        } else {
            throw new IllegalArgumentException(
                "Cannot extract timezone offset from: " + formattable);
        }

        int total = offset.getIntegralAmount();
        int fraction = offset.getFractionalAmount();

        if ((total | fraction) == 0) {
            String zeroOffset = this.zeroOffsets.get(0);
            buffer.append(zeroOffset);
            printed = zeroOffset.length();
        } else {
            boolean negative = ((total < 0) || (fraction < 0));
            buffer.append(negative ? '-' : '+');
            printed++;

            int absValue = Math.abs(total);
            int h = absValue / 3600;
            int m = (absValue / 60) % 60;
            int s = absValue % 60;

            if (h < 10) {
                buffer.append('0');
                printed++;
            }

            String hours = String.valueOf(h);
            buffer.append(hours);
            printed += hours.length();

            if (
                (this.precision != SHORT)
                || (m != 0)
            ) {
                if (this.extended) {
                    buffer.append(':');
                    printed++;
                }

                if (m < 10) {
                    buffer.append('0');
                    printed++;
                }

                String minutes = String.valueOf(m);
                buffer.append(minutes);
                printed += minutes.length();

                if (
                    (this.precision != SHORT)
                    && (this.precision != MEDIUM)
                ) {
                    if ((this.precision == FULL) || ((s | fraction) != 0)) {
                        if (this.extended) {
                            buffer.append(':');
                            printed++;
                        }

                        if (s < 10) {
                            buffer.append('0');
                            printed++;
                        }

                        String seconds = String.valueOf(s);
                        buffer.append(seconds);
                        printed += seconds.length();

                        if (fraction != 0) {
                            buffer.append('.');
                            printed++;
                            String f = String.valueOf(Math.abs(fraction));
                            for (int i = 0, n = 9 - f.length(); i < n; i++) {
                                buffer.append('0');
                                printed++;
                            }
                            buffer.append(f);
                            printed += f.length();
                        }
                    }
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
        boolean quickPath
    ) {

        int len = text.length();
        int start = status.getPosition();
        int pos = start;

        if (pos >= len) {
            status.setError(start, "Missing timezone offset.");
            return;
        }

        for (String zeroOffset : this.zeroOffsets) {
            int zl = zeroOffset.length();

            if (len - pos >= zl) {
                String compare = text.subSequence(pos, pos + zl).toString();

                boolean ignoreCase = (
                    quickPath
                        ? this.caseInsensitive
                        : attributes.get(Attributes.PARSE_CASE_INSENSITIVE, Boolean.TRUE).booleanValue());

                if (
                    (ignoreCase && compare.equalsIgnoreCase(zeroOffset))
                    || (!ignoreCase && compare.equals(zeroOffset))
                ) {
                    parsedResult.put(TimezoneElement.TIMEZONE_OFFSET, ZonalOffset.UTC);
                    status.setPosition(pos + zl);
                    return;
                }
            }
        }

        char c = text.charAt(pos);
        pos++;
        OffsetSign sign;

        if (c == '+') {
            sign = AHEAD_OF_UTC;
        } else if (c == '-') {
            sign = BEHIND_UTC;
        } else {
            status.setError(start, "Missing sign of timezone offset.");
            return;
        }

        Leniency leniency = (quickPath ? this.lenientMode : attributes.get(Attributes.LENIENCY, Leniency.SMART));
        int hours = parseNum(text, pos, leniency);

        if (hours == -1000) {
            status.setError(
                pos,
                "Hour part in timezone offset "
                + "does not match expected pattern HH.");
            return;
        }

        if (hours < 0) {
            hours = ~hours;
            pos++;
        } else {
            pos += 2;
        }

        if (pos >= len) {
            if (this.precision == SHORT) {
                parsedResult.put(
                    TimezoneElement.TIMEZONE_OFFSET,
                    ZonalOffset.ofHours(sign, hours));
                status.setPosition(pos);
            } else {
                status.setError(
                    pos,
                    "Missing minute part in timezone offset.");
            }
            return;
        }

        int colon = 0;

        if (this.extended) {
            if (text.charAt(pos) == ':') {
                colon = 1;
            } else if (this.precision == SHORT) {
                parsedResult.put(
                    TimezoneElement.TIMEZONE_OFFSET,
                    ZonalOffset.ofHours(sign, hours));
                status.setPosition(pos);
                return;
            } else {
                status.setError(pos, "Colon expected in timezone offset.");
                return;
            }
        }

        int minutes = parseNum(text, pos + colon, Leniency.STRICT);
        int seconds = 0;
        int fraction = 0;

        if (minutes == -1000) {
            if (this.precision == SHORT) {
                parsedResult.put(
                    TimezoneElement.TIMEZONE_OFFSET,
                    ZonalOffset.ofHours(sign, hours));
                status.setPosition(pos);
            } else {
                status.setError(
                    pos + colon,
                    "Minute part in timezone offset "
                    + "does not match expected pattern mm.");
            }
            return;
        }

        pos += colon;
        pos += 2;

        if (
            (pos < len)
            && ((this.precision == LONG) || (this.precision == FULL))
        ) {
            colon = 0;

            if (this.extended) {
                if (text.charAt(pos) == ':') {
                    colon = 1;
                    seconds = parseNum(text, pos + colon, Leniency.STRICT);
                } else if (this.precision == FULL) {
                    status.setError(pos, "Colon expected in timezone offset.");
                    return;
                } else {
                    seconds = -1000;
                }
            } else {
                seconds = parseNum(text, pos, Leniency.STRICT);
            }

            if (seconds == -1000) {
                if (this.precision == FULL) {
                    status.setError(
                        pos,
                        "Second part in timezone offset "
                        + "does not match expected pattern ss.");
                    return;
                } else {
                    seconds = 0;
                }
            } else {
                pos += colon;
                pos += 2;

                if (pos + 10 <= len) {
                    char dot = text.charAt(pos);

                    if (dot == '.') {
                        pos++;

                        for (int i = pos, n = pos + 9; i < n; i++) {
                            char digit = text.charAt(i);

                            if ((digit >= '0') && (digit <= '9')) {
                                fraction = fraction * 10 + (digit - '0');
                                pos++;
                            } else {
                                status.setError(
                                    pos,
                                    "9 digits in fractional part of "
                                    + "timezone offset expected.");
                                return;
                            }
                        }
                    }
                }
            }

        }

        ZonalOffset offset;

        if (
            (seconds == 0)
            && (fraction == 0)
        ) {
            offset = ZonalOffset.ofHoursMinutes(sign, hours, minutes);
        } else {
            int total = hours * 3600 + minutes * 60 + seconds;

            if (sign == BEHIND_UTC) {
                total = -total;
                fraction = -fraction;
            }

            offset = ZonalOffset.ofTotalSeconds(total, fraction);
        }

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

    @Override
    public FormatProcessor<TZID> quickPath(
        AttributeQuery attributes,
        int reserved
    ) {

        return new TimezoneOffsetProcessor(
            this.precision,
            this.extended,
            this.zeroOffsets,
            attributes.get(Attributes.PARSE_CASE_INSENSITIVE, Boolean.TRUE).booleanValue(),
            attributes.get(Attributes.LENIENCY, Leniency.SMART)
        );

    }

    private static ZonalOffset getOffset(
        ChronoDisplay formattable,
        AttributeQuery attributes
    ) {

        if (attributes.contains(Attributes.TIMEZONE_ID)) {
            TZID tzid = attributes.get(Attributes.TIMEZONE_ID);

            if (tzid instanceof ZonalOffset) {
                return (ZonalOffset) tzid;
            } else if (tzid != null) {
                throw new IllegalArgumentException(
                    "Use a timezone offset instead of ["
                    + tzid.canonical()
                    + "] when formatting ["
                    + formattable
                    + "].");
            }
        }

        throw new IllegalArgumentException(
            "Cannot extract timezone offset from format attributes for: "
            + formattable);

    }

    private static int parseNum(
        CharSequence text,
        int pos,
        Leniency leniency
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
            } else if ((i == 0) || leniency.isStrict()) {
                return -1000;
            } else {
                return ~total;
            }
        }

        return total;

    }

}
