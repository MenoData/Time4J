/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SystemTemporalFormatter.java) is part of project Time4J.
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

package net.time4j;

import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoException;
import net.time4j.engine.Chronology;
import net.time4j.format.Attributes;
import net.time4j.format.Leniency;
import net.time4j.format.RawValues;
import net.time4j.format.TemporalFormatter;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


/**
 * <p>A temporal formatter of the system format engine. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 * @doctags.concurrency <immutable>
 */
final class SystemTemporalFormatter<T>
    implements TemporalFormatter<T> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final String RFC_1123_WIDE = "EEE, d MMM yyyy HH:mm:ss Z";
    private static final String RFC_1123_SHORT = "d MMM yyyy HH:mm:ss Z";
    private static final Date PROLEPTIC_GREGORIAN = new Date(Long.MIN_VALUE);
    private static final PlainDate UNIX_EPOCH_DATE = PlainDate.of(1970, 1, 1);

    private static final Map<Class<?>, Chronology<?>> SUPPORTED_TYPES;

    static {
        Map<Class<?>, Chronology<?>> tmp = new HashMap<Class<?>, Chronology<?>>();
        tmp.put(PlainDate.class, PlainDate.axis());
        tmp.put(PlainTime.class, PlainTime.axis());
        tmp.put(PlainTimestamp.class, PlainTimestamp.axis());
        tmp.put(Moment.class, Moment.axis());
        tmp.put(ZonalDateTime.class, null);
        SUPPORTED_TYPES = Collections.unmodifiableMap(tmp);
    }

    //~ Instanzvariablen --------------------------------------------------

    private final Class<T> type;
    private final String pattern;
    private final Locale locale;
    private final Leniency leniency;
    private final String tzid;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * Standard-Konstruktor.
     *
     * @param   type        chronological type
     * @param   pattern     format pattern
     * @param   locale      language and country setting
     * @param   leniency    lenient setting
     * @param   tzid        timezone id (optional)
     */
    SystemTemporalFormatter(
        Class<T> type,
        String pattern,
        Locale locale,
        Leniency leniency,
        String tzid
    ) {
        super();

        if (type == null) {
            throw new NullPointerException("Missing chronological type");
        } else if (pattern.isEmpty()) {
            throw new IllegalArgumentException("Format pattern is empty.");
        } else if (locale == null) {
            throw new NullPointerException("Locale is not specified.");
        } else if (leniency == null){
            throw new NullPointerException("Missing leniency.");
        }

        this.type = type;
        this.pattern = pattern;
        this.locale = locale;
        this.leniency = leniency;
        this.tzid = tzid;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public String format(T formattable) {

        StringBuilder buffer = new StringBuilder();

        try {
            this.formatToBuffer(formattable, buffer);
            return buffer.toString();
        } catch (IOException ioe) {
            throw new AssertionError(ioe); // cannot happen
        }

    }

    @Override
    public void formatToBuffer(T formattable, Appendable buffer) throws IOException {

        String text;

        if (this.type.equals(PlainDate.class)) {
            PlainDate date = PlainDate.class.cast(formattable);
            Date jud = TemporalType.JAVA_UTIL_DATE.from(date.atStartOfDay().atUTC());
            text = this.setUpLocal().format(jud);
        } else if (this.type.equals(PlainTime.class)) {
            PlainTime time = PlainTime.class.cast(formattable);
            Date jud = TemporalType.JAVA_UTIL_DATE.from(UNIX_EPOCH_DATE.at(time).atUTC());
            text = this.setUpLocal().format(jud);
        } else if (this.type.equals(PlainTimestamp.class)) {
            PlainTimestamp timestamp = PlainTimestamp.class.cast(formattable);
            Date jud = TemporalType.JAVA_UTIL_DATE.from(timestamp.atUTC());
            text = this.setUpLocal().format(jud);
        } else if (this.type.equals(Moment.class)) {
            Moment moment = Moment.class.cast(formattable);
            Date jud = TemporalType.JAVA_UTIL_DATE.from(moment);
            if (this.tzid == null) {
                throw new IllegalArgumentException("Cannot print moment without timezone.");
            }
            String p = this.pattern;
            if (p.equals(SystemFormatEngine.RFC_1123_PATTERN)) {
                p = RFC_1123_WIDE;
            }
            SimpleDateFormat sdf = setUp(p, this.locale, !this.leniency.isStrict(), this.tzid);
            text = sdf.format(jud);
        } else if (this.type.equals(ZonalDateTime.class)) {
            ZonalDateTime zdt = ZonalDateTime.class.cast(formattable);
            Date jud = TemporalType.JAVA_UTIL_DATE.from(zdt.toMoment());
            String timezone = (
                this.tzid == null
                ? zdt.getTimezone().canonical()
                : this.tzid);
            SimpleDateFormat sdf = setUp(this.pattern, this.locale, !this.leniency.isStrict(), timezone);
            text = sdf.format(jud);
        } else {
            throw new IllegalArgumentException("Not formattable: " + formattable);
        }

        buffer.append(text);

    }

    @Override
    public T parse(CharSequence text) throws ParseException {

        ParsePosition pp = new ParsePosition(0);
        T result;

        try {
            result = this.parseInternal(text, pp, true, null);

            if ((result == null) || (pp.getErrorIndex() > -1)) {
                throw new ParseException("Cannot parse: " + text, pp.getErrorIndex());
            }
        } catch (RuntimeException re) {
            ParseException pe = new ParseException(re.getMessage(), pp.getErrorIndex());
            pe.initCause(re);
            throw pe;
        }

        return result;

    }

    @Override
    public T parse(CharSequence text, ParsePosition position) {

        return this.parseInternal(text, position, false, null);

    }

    @Override
    public T parse(CharSequence text, ParsePosition position, RawValues rawValues) {

        if (rawValues == null) {
            throw new NullPointerException("Missing raw values.");
        }

        return this.parseInternal(text, position, false, rawValues);

    }

    @Override
    public TemporalFormatter<T> withTimezone(TZID tzid) {

        return this.withTimezone(tzid.canonical());

    }

    @Override
    public TemporalFormatter<T> withTimezone(String tzid) {

        if (tzid.isEmpty()) {
            throw new IllegalArgumentException("Timezone id must not be empty.");
        }

        return new SystemTemporalFormatter<T>(
            this.type,
            this.pattern,
            this.locale,
            this.leniency,
            tzid
        );

    }

    @Override
    public TemporalFormatter<T> with(Locale locale) {

        return new SystemTemporalFormatter<T>(
            this.type,
            this.pattern,
            locale,
            this.leniency,
            this.tzid
        );

    }

    @Override
    public TemporalFormatter<T> with(Leniency leniency) {

        return new SystemTemporalFormatter<T>(
            this.type,
            this.pattern,
            this.locale,
            leniency,
            this.tzid
        );

    }

    @Override
    public AttributeQuery getAttributes() {

        Chronology<?> chronology = SUPPORTED_TYPES.get(this.type);
        Attributes.Builder ab = (
            (chronology == null)
            ? new Attributes.Builder()
            : new Attributes.Builder(chronology));
        ab.setLanguage(this.locale);
        ab.set(Attributes.LENIENCY, this.leniency);
        if (this.tzid != null) {
            ab.setTimezone(this.tzid);
        }
        return ab.build();

    }

    private SimpleDateFormat setUpLocal() {

        return setUp(this.pattern, this.locale, !this.leniency.isStrict(), "GMT");

    }

    private static SimpleDateFormat setUp(
        String pattern,
        Locale locale,
        boolean lenient,
        String tzid
    ) {

        SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone(tzid), locale);
        gcal.setGregorianChange(PROLEPTIC_GREGORIAN);
        sdf.setCalendar(gcal);
        sdf.setLenient(lenient);
        return sdf;

    }

    private T parseInternal(
        CharSequence text,
        ParsePosition position,
        boolean wantsException,
        RawValues rawValues
    ) {

        String parseable = text.toString();
        Object result;

        if (this.type.equals(PlainDate.class)) {
            SimpleDateFormat sdf = this.setUpLocal();
            Date jud = sdf.parse(parseable, position);
            PlainTimestamp tsp = TemporalType.JAVA_UTIL_DATE.translate(jud).toZonalTimestamp(ZonalOffset.UTC);
            result = tsp.getCalendarDate();
            updateRawValues(rawValues, sdf, null);
        } else if (this.type.equals(PlainTime.class)) {
            SimpleDateFormat sdf = this.setUpLocal();
            Date jud = sdf.parse(parseable, position);
            PlainTimestamp tsp = TemporalType.JAVA_UTIL_DATE.translate(jud).toZonalTimestamp(ZonalOffset.UTC);
            result = tsp.getWallTime();
            updateRawValues(rawValues, sdf, null);
        } else if (this.type.equals(PlainTimestamp.class)) {
            SimpleDateFormat sdf = this.setUpLocal();
            Date jud = sdf.parse(parseable, position);
            PlainTimestamp tsp = TemporalType.JAVA_UTIL_DATE.translate(jud).toZonalTimestamp(ZonalOffset.UTC);
            result = tsp;
            updateRawValues(rawValues, sdf, null);
        } else if (this.type.equals(Moment.class)) {
            String timezone = (
                (this.tzid == null)
                ? "GMT-18:00"
                : this.tzid);
            String realPattern = this.pattern;
            if (realPattern.equals(SystemFormatEngine.RFC_1123_PATTERN)) {
                String test = parseable.substring(position.getIndex());
                if ((test.length() >= 4) && (test.charAt(3) == ',')) {
                    realPattern = RFC_1123_WIDE;
                } else {
                    realPattern = RFC_1123_SHORT;
                }
                int count = 0;
                for (int i = test.length() - 1; i >= 0 && count < 2; i--) {
                    if (test.charAt(i) == ':') {
                        count++;
                    }
                }
                if (count >= 2) {
                    realPattern = realPattern.replace(":ss", "");
                }
            }
            SimpleDateFormat sdf = setUp(realPattern, this.locale, !this.leniency.isStrict(), timezone);
            Date jud = sdf.parse(parseable, position);
            Moment moment = TemporalType.JAVA_UTIL_DATE.translate(jud);
            Calendar cal = sdf.getCalendar();
            int offset = cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET);
            if (offset == -1080) {
                position.setErrorIndex(text.length());
                if (wantsException) {
                    throw new IllegalArgumentException("Cannot parse text without timezone: " + parseable);
                } else {
                    return null;
                }
            }
            result = moment;
            TZID parsedTimezone = this.getParsedTimezone(cal, offset, timezone);
            updateRawValues(rawValues, sdf, parsedTimezone);
        } else if (this.type.equals(ZonalDateTime.class)) {
            String timezone = (
                (this.tzid == null)
                ? "GMT-18:00"
                : this.tzid);
            SimpleDateFormat sdf = setUp(this.pattern, this.locale, !this.leniency.isStrict(), timezone);
            Date jud = sdf.parse(parseable, position);
            Moment moment = TemporalType.JAVA_UTIL_DATE.translate(jud);
            Calendar cal = sdf.getCalendar();
            int offset = cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET);
            if (offset == -1080) {
                position.setErrorIndex(position.getIndex());
                if (wantsException) {
                    throw new IllegalArgumentException("Cannot parse text without timezone: " + parseable);
                } else {
                    return null;
                }
            } else if (!cal.getTimeZone().getID().equals(timezone)) {
                result = moment.inZonalView(cal.getTimeZone().getID());
            } else {
                ZonalOffset zo = ZonalOffset.ofTotalSeconds(offset / 1000);
                if ((this.tzid != null) && Timezone.of(this.tzid).getOffset(moment).equals(zo)) {
                    result = moment.inZonalView(this.tzid);
                } else {
                    result = moment.inZonalView(zo);
                }
            }
            TZID parsedTimezone = this.getParsedTimezone(cal, offset, timezone);
            updateRawValues(rawValues, sdf, parsedTimezone);
        } else {
            result = null;
        }

        return this.type.cast(result);

    }

    private TZID getParsedTimezone(
        Calendar cal,
        int offset,
        String timezone
    ) {

        if (!cal.getTimeZone().getID().equals(timezone)) {
            return Timezone.of(cal.getTimeZone().getID()).getID();
        } else {
            ZonalOffset zo = ZonalOffset.ofTotalSeconds(offset / 1000);
            return ((this.tzid == null) ? zo : null);
        }

    }

    private static void updateRawValues(
        RawValues rawValues,
        SimpleDateFormat sdf,
        TZID parsedTimezone
    ) {

        if (rawValues != null) {
            rawValues.accept(new Parsed(sdf.getCalendar(), parsedTimezone));
        }

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class Parsed
        implements ChronoDisplay {

        //~ Instanzvariablen ----------------------------------------------

        private final Map<ChronoElement<?>, Object> values;
        private final TZID tzid;

        //~ Konstruktoren -------------------------------------------------

        Parsed(
            Calendar cal,
            TZID tzid
        ) {
            super();

            Map<ChronoElement<?>, Object> map = new HashMap<ChronoElement<?>, Object>();
            map.put(PlainDate.YEAR, cal.get(Calendar.YEAR));
            map.put(PlainDate.MONTH_AS_NUMBER, cal.get(Calendar.MONTH) + 1);
            map.put(PlainDate.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR));
            map.put(PlainDate.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
            int wd = cal.get(Calendar.DAY_OF_WEEK) - 1;
            map.put(PlainDate.DAY_OF_WEEK, Weekday.valueOf((wd == 0) ? 7 : wd));
            map.put(PlainTime.AM_PM_OF_DAY, Meridiem.values()[cal.get(Calendar.AM_PM)]);
            map.put(PlainTime.DIGITAL_HOUR_OF_AMPM, cal.get(Calendar.HOUR));
            map.put(PlainTime.DIGITAL_HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
            map.put(PlainTime.MINUTE_OF_HOUR, cal.get(Calendar.MINUTE));
            map.put(PlainTime.SECOND_OF_MINUTE, cal.get(Calendar.SECOND));
            map.put(PlainTime.MILLI_OF_SECOND, cal.get(Calendar.MILLISECOND));
            int fw = cal.getFirstDayOfWeek() - 1;
            map.put(
                Weekmodel.of(Weekday.valueOf((fw == 0) ? 7 : fw), cal.getMinimalDaysInFirstWeek()).weekOfYear(),
                cal.get(Calendar.WEEK_OF_YEAR));
            this.values = Collections.unmodifiableMap(map);
            this.tzid = tzid;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public boolean contains(ChronoElement<?> element) {

            return this.values.containsKey(element);

        }

        @Override
        public <V> V get(ChronoElement<V> element) {

            this.check(element);
            return element.getType().cast(this.values.get(element));

        }

        @Override
        public <V> V getMinimum(ChronoElement<V> element) {

            this.check(element);
            return element.getDefaultMinimum();

        }

        @Override
        public <V> V getMaximum(ChronoElement<V> element) {

            this.check(element);
            return element.getDefaultMaximum();

        }

        @Override
        public boolean hasTimezone() {

            return (this.tzid != null);

        }

        @Override
        public TZID getTimezone() {

            if (this.tzid == null) {
                throw new ChronoException("Timezone was not parsed.");
            }

            return this.tzid;

        }

        private void check(ChronoElement<?> element) {

            if (!this.values.containsKey(element)) {
                throw new ChronoException("Element not supported: " + element.name());
            }

        }

    }

}
