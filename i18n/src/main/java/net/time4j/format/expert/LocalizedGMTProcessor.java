/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

    // private static final char UNICODE_LRM = '\u200E';
    private static final ZonalOffset PROTOTYPE = ZonalOffset.ofTotalSeconds(3600 * 18);
    private static final ConcurrentMap<Locale, String> UTC_LITERALS = new ConcurrentHashMap<Locale, String>();
    private static final ConcurrentMap<Locale, Info> STD_PATTERN_INFOS = new ConcurrentHashMap<Locale, Info>();

    //~ Instanzvariablen --------------------------------------------------

    private final boolean abbreviated;

    // quick path optimization
    private final boolean caseInsensitive;
    private final boolean noPrefix;
    private final Locale locale;
    private final String plusSign;
    private final String minusSign;
    private final char zeroDigit;
    private final Leniency lenientMode;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Erzeugt eine neue Instanz. </p>
     *
     * @param   abbreviated     short form of localized gmt offset?
     */
    LocalizedGMTProcessor(boolean abbreviated) {
        this(abbreviated, true, false, Locale.ROOT, "+", "-", '0', Leniency.SMART);

    }

    private LocalizedGMTProcessor(
        boolean abbreviated,
        boolean caseInsensitive,
        boolean noPrefix,
        Locale locale,
        String plusSign,
        String minusSign,
        char zeroDigit,
        Leniency lenientMode
    ) {
        super();

        this.abbreviated = abbreviated;

        // quick path members
        this.caseInsensitive = caseInsensitive;
        this.noPrefix = noPrefix;
        this.locale = locale;
        this.plusSign = plusSign;
        this.minusSign = minusSign;
        this.zeroDigit = zeroDigit;
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

        Locale loc = (quickPath ? this.locale : attributes.get(Attributes.LANGUAGE, Locale.ROOT));
        char zeroChar = (
            quickPath
                ? this.zeroDigit
                : attributes.get(Attributes.ZERO_DIGIT, Character.valueOf('0')).charValue());
        String plus = (quickPath ? this.plusSign : attributes.get(AttributeSet.PLUS_SIGN, "+"));
        String minus = (quickPath ? this.minusSign : attributes.get(AttributeSet.MINUS_SIGN, "-"));

// hack for cldr-version before v30
//        if ("ar".equals(loc.getLanguage()) && (zeroChar == '0')) {
//            plus = UNICODE_LRM + "+";
//            minus = UNICODE_LRM + "\u002D";
//        }

        boolean np = (
            quickPath
                ? this.noPrefix
                : attributes.get(Attributes.NO_GMT_PREFIX, Boolean.FALSE).booleanValue());

        int total = offset.getIntegralAmount();
        int fraction = offset.getFractionalAmount();

        if (!np && (total == 0) && (fraction == 0)) {
            String literal = getLiteralUTC(loc);
            buffer.append(literal);
            printed = literal.length();
        } else {
            Info info = getPatternInfo(loc);

            for (int p = 0, n = info.pattern.length(); p < n; p++) {
                char c = info.pattern.charAt(p);

                // literal
                if ((info.start > p) || (info.end <= p)) {
                    if (!np) {
                        buffer.append(c);
                        printed++;
                    }
                    continue;
                }

                // offset sign
                if (offset.getSign() == BEHIND_UTC) {
                    buffer.append(minus);
                    printed += minus.length();
                } else {
                    buffer.append(plus);
                    printed += plus.length();
                }

                // hour part
                int h = offset.getAbsoluteHours();
                int m = offset.getAbsoluteMinutes();
                int s = offset.getAbsoluteSeconds();

                if ((h < 10) && !this.abbreviated) {
                    buffer.append(zeroChar);
                    printed++;
                }

                String hours = String.valueOf(h);

                for (int i = 0; i < hours.length(); i++) {
                    char digit = (char) (hours.charAt(i) - '0' + zeroChar);
                    buffer.append(digit);
                    printed++;
                }

                // minute part
                if ((m != 0) || (s != 0) || !this.abbreviated) {
                    buffer.append(info.separator);
                    printed += info.separator.length();

                    if (m < 10) {
                        buffer.append(zeroChar);
                        printed++;
                    }

                    String minutes = String.valueOf(m);

                    for (int i = 0; i < minutes.length(); i++) {
                        char digit = (char) (minutes.charAt(i) - '0' + zeroChar);
                        buffer.append(digit);
                        printed++;
                    }

                    // second part
                    if (s != 0) {
                        buffer.append(info.separator);
                        printed += info.separator.length();

                        if (s < 10) {
                            buffer.append(zeroChar);
                            printed++;
                        }

                        String seconds = String.valueOf(s);

                        for (int i = 0; i < seconds.length(); i++) {
                            char digit = (char) (seconds.charAt(i) - '0' + zeroChar);
                            buffer.append(digit);
                            printed++;
                        }
                    }

                }

                p = (info.end - 1);
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
        ParsedValues parsedResult,
        boolean quickPath
    ) {

        int len = text.length();
        int start = status.getPosition();
        int pos = start;

        if (pos >= len) {
            status.setError(start, "Missing localized time zone offset.");
            return;
        }

        Locale loc = (quickPath ? this.locale : attributes.get(Attributes.LANGUAGE, Locale.ROOT));
        boolean rtl = CalendarText.isRTL(loc);

        boolean np = (
            quickPath
                ? this.noPrefix
                : attributes.get(Attributes.NO_GMT_PREFIX, Boolean.FALSE).booleanValue());
        boolean ignoreCase = (
            quickPath
                ? this.caseInsensitive
                : attributes.get(Attributes.PARSE_CASE_INSENSITIVE, Boolean.TRUE).booleanValue());
        char zeroChar = (
            quickPath
                ? this.zeroDigit
                : attributes.get(Attributes.ZERO_DIGIT, Character.valueOf('0')).charValue());

        String plus = (quickPath ? this.plusSign : attributes.get(AttributeSet.PLUS_SIGN, "+"));
        String minus = (quickPath ? this.minusSign : attributes.get(AttributeSet.MINUS_SIGN, "-"));

// hack for cldr-version before v30
//        if ("ar".equals(loc.getLanguage()) && (zeroChar == '0')) {
//            plus = UNICODE_LRM + "+";
//            minus = UNICODE_LRM + "\u002D";
//        }

        Info info = getPatternInfo(loc);
        int n = info.pattern.length();
        ZonalOffset offset = null;
        int old = pos;

        for (int p = 0; p < n; p++) {
            char c = info.pattern.charAt(p);

            // literal
            if ((info.start > p) || (info.end <= p)) {
                if (!np) {
                    char test = (pos < len) ? text.charAt(pos) : '\u0000';
                    if (
                        (!ignoreCase && (c == test))
                        || (ignoreCase && charEqualsIgnoreCase(c, test))
                    ) {
                        pos++;
                    } else {
                        int zl = parseUTC(text, len, old, loc, ignoreCase); // try other literal
                        if (zl > 0) {
                            parsedResult.put(TimezoneElement.TIMEZONE_OFFSET, ZonalOffset.UTC);
                            status.setPosition(old + zl);
                        } else {
                            status.setError(
                                start,
                                "Literal mismatched in localized time zone offset.");
                        }
                        return;
                    }
                }
                continue;
            }

            OffsetSign sign;
            int parsedLen = LiteralProcessor.subSequenceEquals(text, pos, plus, ignoreCase, rtl);

            if (parsedLen == -1) {
                parsedLen = LiteralProcessor.subSequenceEquals(text, pos, minus, ignoreCase, rtl);

                if (parsedLen == -1) {
                    int zl = (np ? 0 : parseUTC(text, len, old, loc, ignoreCase)); // no sign => try UTC

                    if (zl > 0) {
                        parsedResult.put(TimezoneElement.TIMEZONE_OFFSET, ZonalOffset.UTC);
                        status.setPosition(old + zl);
                        return;
                    } else {
                        status.setError(
                            start,
                            "Missing sign in localized time zone offset.");
                        return;
                    }
                } else {
                    sign = BEHIND_UTC;
                }
            } else {
                sign = AHEAD_OF_UTC;
            }

            pos += parsedLen;
            int hours = parseHours(text, pos, zeroChar);

            if (hours == -1000) {
                status.setError(
                    pos,
                    "Missing hour part in localized time zone offset.");
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
                        "Missing minute part in localized time zone offset.");
                }
                return;
            }

            Leniency leniency = (quickPath ? this.lenientMode : attributes.get(Attributes.LENIENCY, Leniency.SMART));
            int seplen = LiteralProcessor.subSequenceEquals(text, pos, info.separator, ignoreCase, rtl);

            if (seplen != -1) {
                pos += seplen;
            } else if (this.abbreviated) {
                parsedResult.put(
                    TimezoneElement.TIMEZONE_OFFSET,
                    ZonalOffset.ofHours(sign, hours));
                status.setPosition(pos);
                return;
            } else if (leniency.isStrict()) {
                status.setError(pos, "Mismatch of localized time zone offset separator.");
                return;
            }

            int minutes = parseTwoDigits(text, pos, zeroChar);

            if (minutes == -1000) {
                status.setError(
                    pos,
                    "Minute part in localized time zone offset does not match expected pattern mm.");
                return;
            }

            pos += 2;
            int seconds = 0;

            if (pos < len) {
                seplen = LiteralProcessor.subSequenceEquals(text, pos, info.separator, ignoreCase, rtl);

                if (seplen != -1) {
                    pos += seplen;
                    seconds = parseTwoDigits(text, pos, zeroChar);

                    if (seconds == -1000) {
                        pos -= seplen;
                    } else {
                        pos += 2;
                    }
                }
            }

            if ((seconds == 0) || (seconds == -1000)) {
                offset = ZonalOffset.ofHoursMinutes(sign, hours, minutes);
            } else {
                int total = hours * 3600 + minutes * 60 + seconds;
                if (sign == OffsetSign.BEHIND_UTC) {
                    total = -total;
                }
                offset = ZonalOffset.ofTotalSeconds(total);
            }

            p = (info.end - 1);
        }

        if (offset == null) {
            status.setError(
                pos,
                "Unable to determine localized time zone offset.");
        } else {
            parsedResult.put(TimezoneElement.TIMEZONE_OFFSET, offset);
            status.setPosition(pos);
        }

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
        ChronoFormatter<?> formatter,
        AttributeQuery attributes,
        int reserved
    ) {

        return new LocalizedGMTProcessor(
            this.abbreviated,
            attributes.get(Attributes.PARSE_CASE_INSENSITIVE, Boolean.TRUE).booleanValue(),
            attributes.get(Attributes.NO_GMT_PREFIX, Boolean.FALSE).booleanValue(),
            attributes.get(Attributes.LANGUAGE, Locale.ROOT),
            attributes.get(AttributeSet.PLUS_SIGN, "+"),
            attributes.get(AttributeSet.MINUS_SIGN, "-"),
            attributes.get(Attributes.ZERO_DIGIT, Character.valueOf('0')).charValue(),
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
            }
        }

        throw new IllegalArgumentException(
            "Cannot extract timezone offset from format attributes for: "
            + formattable);

    }

    private static int parseUTC(
        CharSequence text,
        int len,
        int pos,
        Locale loc,
        boolean ignoreCase
    ) {

        String gmtPrefix = getLiteralUTC(loc);
        String[] zeroOffsets = {"GMT", gmtPrefix, "UTC", "UT"};

        for (String zeroOffset : zeroOffsets) {
            int test = zeroOffset.length();

            if (len - pos >= test) {
                String compare = text.subSequence(pos, pos + test).toString();

                if (
                    (ignoreCase && compare.equalsIgnoreCase(zeroOffset))
                    || (!ignoreCase && compare.equals(zeroOffset))
                ) {
                    return test;
                }
            }
        }

        return 0;

    }

    private static int parseTwoDigits(
        CharSequence text,
        int pos,
        char zeroDigit
    ) {

        int total = 0;

        for (int i = 0; i < 2; i++) {
            int digit;

            if (pos + i >= text.length()) {
                return -1000;
            } else {
                digit = text.charAt(pos + i) - zeroDigit;
            }

            if ((digit >= 0) && (digit <= 9)) {
                total = total * 10 + digit;
            } else {
                return -1000;
            }
        }

        return total;

    }

    private static int parseHours(
        CharSequence text,
        int pos,
        char zeroDigit
    ) {

        int total = 0;

        for (int i = 0; i < 2; i++) {
            int digit;

            if (pos + i >= text.length()) {
                if (i == 0) {
                    return -1000;
                } else {
                    return ~total;
                }
            } else {
                digit = text.charAt(pos + i) - zeroDigit;
            }

            if ((digit >= 0) && (digit <= 9)) {
                total = total * 10 + digit;
            } else if (i == 0) {
                return -1000;
            } else {
                return ~total;
            }
        }

        return total;

    }

    private static boolean charEqualsIgnoreCase(
        char c1,
        char c2
    ) {

        return (
            (c1 == c2)
            || (Character.toUpperCase(c1) == Character.toUpperCase(c2))
            || (Character.toLowerCase(c1) == Character.toLowerCase(c2))
        );

    }

    private static String getLiteralUTC(Locale locale) {

        String pattern = UTC_LITERALS.get(locale);

        if (pattern == null) {
            pattern = ZonalOffset.UTC.getStdFormatPattern(locale);
            String old = UTC_LITERALS.putIfAbsent(locale, pattern);
            if (old != null) {
                pattern = old;
            }
        }

        return pattern;

    }

    private static Info getPatternInfo(Locale locale) {

        Info info = STD_PATTERN_INFOS.get(locale);

        if (info == null){
            String offsetPattern = PROTOTYPE.getStdFormatPattern(locale);

            for (int i = 0, n = offsetPattern.length(); i < n; i++) {
                if (offsetPattern.charAt(i) == '\u00B1') {
                    int sep1 = offsetPattern.indexOf("hh", i) + 2;
                    int sep2 = offsetPattern.indexOf("mm", sep1);
                    info = new Info(offsetPattern, offsetPattern.substring(sep1, sep2), i, sep2 + 2);
                    Info old = STD_PATTERN_INFOS.putIfAbsent(locale, info);
                    if (old != null) {
                        info = old;
                    }
                    break;
                }
            }
        }

        assert (info != null);
        return info;

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class Info {

        //~ Instanzvariablen ----------------------------------------------

        private final String pattern;
        private final String separator;
        private final int start;
        private final int end;

        //~ Konstruktoren -------------------------------------------------

        Info(
            String pattern,
            String separator,
            int start,
            int end
        ) {
            super();

            this.pattern = pattern;
            this.separator = separator;
            this.start = start;
            this.end = end;

        }

    }

}
