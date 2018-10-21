/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SimpleFormatter.java) is part of project Time4J.
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

package net.time4j.format.platform;

import net.time4j.Meridiem;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.TemporalType;
import net.time4j.ZonalDateTime;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.Chronology;
import net.time4j.format.Attributes;
import net.time4j.format.DisplayMode;
import net.time4j.format.Leniency;
import net.time4j.format.RawValues;
import net.time4j.format.TemporalFormatter;
import net.time4j.format.internal.FormatUtils;
import net.time4j.tz.NameStyle;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.text.DateFormat;
import java.text.FieldPosition;
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
import java.util.Set;
import java.util.TimeZone;


/**
 * <p>A temporal formatter which uses platform specific resources based on {@code SimpleDateFormat}. </p>
 *
 * <p>The only supported types are: </p>
 *
 * <ul>
 *     <li>{@code PlainDate}</li>
 *     <li>{@code PlainTime}</li>
 *     <li>{@code PlainTimestamp}</li>
 *     <li>{@code Moment}</li>
 * </ul>
 *
 * @author  Meno Hochschild
 * @since   4.0
 */
/*[deutsch]
 * <p>Ein Zeitformatierer, der die plattformspezifischen Ressourcen auf Basis von {@code SimpleDateFormat} nutzt. </p>
 *
 * <p>Folgende Zeittypen werden unterst&uuml;tzt: </p>
 *
 * <ul>
 *     <li>{@code PlainDate}</li>
 *     <li>{@code PlainTime}</li>
 *     <li>{@code PlainTimestamp}</li>
 *     <li>{@code Moment}</li>
 * </ul>
 *
 * @author  Meno Hochschild
 * @since   4.0
 */
public final class SimpleFormatter<T>
    implements TemporalFormatter<T> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final String RFC_1123_PATTERN = "<RFC-1123>";
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
        SUPPORTED_TYPES = Collections.unmodifiableMap(tmp);
    }

    /**
     * <p>Defines the RFC-1123-format which is for example used in mail headers (technical internet-timestamp). </p>
     *
     * <p>Equivalent to the pattern &quot;[EEE, ]d MMM yyyy HH:mm[:ss] XX&quot;
     * where the timezone offset XX is modified such that in case of zero
     * offset the expression &quot;GMT&quot; is preferred. &quot;UT&quot; or &quot;Z&quot;
     * will be accepted as zero offset, too. The text elements will always be interpreted
     * in English and are case-insensitive. If no extra timezone is specified then this
     * formatter will use the timezone UTC as default for printing. </p>
     *
     * <p>Note: In contrast to the RFC-1123-standard this constant does not
     * support military timezone abbreviations (A-Y) or north-american
     * timezone names (EST, EDT, CST, CDT, MST, MDT, PST, PDT). </p>
     */
    /*[deutsch]
     * <p>Definiert das RFC-1123-Format, das zum Beispiel in Mail-Headers
     * verwendet wird (technischer Internet-Zeitstempel). </p>
     *
     * <p>Entspricht &quot;[EEE, ]d MMM yyyy HH:mm[:ss] XX&quot;, wobei
     * der Zeitzonen-Offset XX so modifiziert ist, da&szlig; im Fall eines
     * Null-Offsets bevorzugt der Ausdruck &quot;GMT&quot; benutzt wird. Als
     * Null-Offset werden auch &quot;UT&quot; oder &quot;Z&quot; akzeptiert.
     * Die Textelemente werden ohne Beachtung der Gro&szlig;- oder
     * Kleinschreibung in Englisch interpretiert. Wird keine extra Zeitzone
     * angegeben, wird dieser Formatierer die UTC-Zeitzone als Vorgabe zum
     * Formatieren verwenden. </p>
     *
     * <p>Zu beachten: Im Gegensatz zum RFC-1123-Standard unterst&uuml;tzt diese
     * Konstante keine milit&auml;rischen Zeitzonen (A-Y) oder nordamerikanischen
     * Zeitzonennamen (EST, EDT, CST, CDT, MST, MDT, PST, PDT). </p>
     */
    public static final SimpleFormatter<Moment> RFC_1123 =
        new SimpleFormatter<Moment>(
            Moment.class,
            RFC_1123_PATTERN,
            Locale.ENGLISH,
            Leniency.SMART,
            "GMT");

    //~ Instanzvariablen --------------------------------------------------

    private final Class<T> type;
    private final String pattern;
    private final Locale locale;
    private final Leniency leniency;
    private final String tzid;

    //~ Konstruktoren -----------------------------------------------------

    private SimpleFormatter(
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

    /**
     * <p>Creates a new formatter which uses the given pattern and locale
     * for formatting and parsing plain dates. </p>
     *
     * <p>The pattern is only validated during printing or parsing. </p>
     *
     * @param   pattern     format definition as pattern as defined by {@code SimpleDateFormat}
     * @param   locale      locale setting
     * @return  format object for formatting {@code PlainDate}-objects using given locale
     * @since   5.0
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Format-Objekt f&uuml;r reine Datumsobjekte mit Hilfe des angegebenen Musters
     * in der angegebenen Sprach- und L&auml;ndereinstellung. </p>
     *
     * <p>Das Formatmuster wird nur beim Formatieren oder Interpretieren gepr&uuml;ft. </p>
     *
     * @param   pattern     format definition as pattern as defined by {@code SimpleDateFormat}
     * @param   locale      locale setting
     * @return  format object for formatting {@code PlainDate}-objects using given locale
     * @since   5.0
     */
    public static SimpleFormatter<PlainDate> ofDatePattern(
        String pattern,
        Locale locale
    ) {

        return new SimpleFormatter<PlainDate>(
            PlainDate.class,
            pattern,
            locale,
            Leniency.SMART,
            null
        );

    }

    /**
     * <p>Constructs a style-based formatter for plain date objects. </p>
     *
     * @param   style       format style
     * @param   locale      format locale
     * @return  new {@code SimpleFormatter}-instance
     * @throws  IllegalStateException if no localized format pattern can be retrieved
     * @since   5.0
     */
    /*[deutsch]
     * <p>Konstruiert einen Formatierer f&uuml;r reine Datumsobjekte. </p>
     *
     * @param   style       format style
     * @param   locale      format locale
     * @return  new {@code SimpleFormatter}-instance
     * @throws  IllegalStateException if no localized format pattern can be retrieved
     * @since   5.0
     */
    public static SimpleFormatter<PlainDate> ofDateStyle(
        DisplayMode style,
        Locale locale
    ) {

        DateFormat df = DateFormat.getDateInstance(style.getStyleValue(), locale);
        String pattern = getFormatPattern(df);
        return SimpleFormatter.ofDatePattern(pattern, locale);

    }

    /**
     * <p>Creates a new formatter which uses the given pattern and locale
     * for formatting and parsing plain times. </p>
     *
     * <p>The pattern is only validated during printing or parsing. </p>
     *
     * @param   pattern     format definition as pattern as defined by {@code SimpleDateFormat}
     * @param   locale      locale setting
     * @return  format object for formatting {@code PlainTime}-objects using given locale
     * @since   5.0
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Format-Objekt f&uuml;r reine Uhrzeitobjekte mit Hilfe des angegebenen Musters
     * in der angegebenen Sprach- und L&auml;ndereinstellung. </p>
     *
     * <p>Das Formatmuster wird nur beim Formatieren oder Interpretieren gepr&uuml;ft. </p>
     *
     * @param   pattern     format definition as pattern as defined by {@code SimpleDateFormat}
     * @param   locale      locale setting
     * @return  format object for formatting {@code PlainTime}-objects using given locale
     * @since   5.0
     */
    public static SimpleFormatter<PlainTime> ofTimePattern(
        String pattern,
        Locale locale
    ) {

        return new SimpleFormatter<PlainTime> (
            PlainTime.class,
            pattern,
            locale,
            Leniency.SMART,
            null
        );

    }

    /**
     * <p>Constructs a style-based formatter for plain time objects. </p>
     *
     * @param   style       format style
     * @param   locale      format locale
     * @return  new {@code SimpleFormatter}-instance
     * @throws  IllegalStateException if no localized format pattern can be retrieved
     * @since   5.0
     */
    /*[deutsch]
     * <p>Konstruiert einen Formatierer f&uuml;r reine Uhrzeitobjekte. </p>
     *
     * @param   style       format style
     * @param   locale      format locale
     * @return  new {@code SimpleFormatter}-instance
     * @throws  IllegalStateException if no localized format pattern can be retrieved
     * @since   5.0
     */
    public static SimpleFormatter<PlainTime> ofTimeStyle(
        DisplayMode style,
        Locale locale
    ) {

        DateFormat df = DateFormat.getTimeInstance(style.getStyleValue(), locale);
        String pattern = FormatUtils.removeZones(getFormatPattern(df));
        return SimpleFormatter.ofTimePattern(pattern, locale);

    }

    /**
     * <p>Creates a new formatter which uses the given pattern and locale
     * for formatting and parsing plain timestamps. </p>
     *
     * <p>The pattern is only validated during printing or parsing. </p>
     *
     * @param   pattern     format definition as pattern as defined by {@code SimpleDateFormat}
     * @param   locale      locale setting
     * @return  format object for formatting {@code PlainTimestamp}-objects using given locale
     * @since   5.0
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Format-Objekt f&uuml;r einfache Zeitstempelobjekte mit Hilfe des angegebenen Musters
     * in der angegebenen Sprach- und L&auml;ndereinstellung. </p>
     *
     * <p>Das Formatmuster wird nur beim Formatieren oder Interpretieren gepr&uuml;ft. </p>
     *
     * @param   pattern     format definition as pattern as defined by {@code SimpleDateFormat}
     * @param   locale      locale setting
     * @return  format object for formatting {@code PlainTimestamp}-objects using given locale
     * @since   5.0
     */
    public static SimpleFormatter<PlainTimestamp> ofTimestampPattern(
        String pattern,
        Locale locale
    ) {

        return new SimpleFormatter<PlainTimestamp>(
            PlainTimestamp.class,
            pattern,
            locale,
            Leniency.SMART,
            null
        );

    }

    /**
     * <p>Constructs a style-based formatter for plain timestamp objects. </p>
     *
     * @param   dateStyle   format style for the date component
     * @param   timeStyle   format style for the time component
     * @param   locale      format locale
     * @return  new {@code SimpleFormatter}-instance
     * @throws  IllegalStateException if no localized format pattern can be retrieved
     * @since   5.0
     */
    /*[deutsch]
     * <p>Konstruiert einen Formatierer f&uuml;r einfache Zeitstempelobjekte. </p>
     *
     * @param   dateStyle   format style for the date component
     * @param   timeStyle   format style for the time component
     * @param   locale      format locale
     * @return  new {@code SimpleFormatter}-instance
     * @throws  IllegalStateException if no localized format pattern can be retrieved
     * @since   5.0
     */
    public static SimpleFormatter<PlainTimestamp> ofTimestampStyle(
        DisplayMode dateStyle,
        DisplayMode timeStyle,
        Locale locale
    ) {

        DateFormat df = DateFormat.getDateTimeInstance(dateStyle.getStyleValue(), timeStyle.getStyleValue(), locale);
        String pattern = FormatUtils.removeZones(getFormatPattern(df));
        return SimpleFormatter.ofTimestampPattern(pattern, locale);

    }

    /**
     * <p>Constructs a pattern-based formatter for global timestamp objects (moments). </p>
     *
     * <p>The given timezone parameter can help to compensate missing timezone or offset informations in input
     * or to resolve possibly ambivalent timezone names in input. </p>
     *
     * @param   pattern     format pattern
     * @param   locale      format locale
     * @param   tzid        timezone id
     * @return  new format object for formatting {@code Moment}-objects using given locale and timezone
     * @throws  IllegalArgumentException if resolving of pattern fails or the timezone cannot be loaded
     * @since   5.0
     */
    /*[deutsch]
     * <p>Konstruiert einen Formatierer f&uuml;r globale Zeitstempelobjekte (Momente). </p>
     *
     * <p>Der angegebene Zeitzonenparameter hilft, fehlende Zeitzonen- oder Offset-Informationen in der Eingabe
     * zu ersetzen oder m&ouml;glicherweise mehrdeutige Zeitzonennamen aufzul&ouml;sen. </p>
     *
     * @param   pattern     format pattern
     * @param   locale      format locale
     * @param   tzid        timezone id
     * @return  new format object for formatting {@code Moment}-objects using given locale and timezone
     * @throws  IllegalArgumentException if resolving of pattern fails or the timezone cannot be loaded
     * @since   5.0
     */
    public static SimpleFormatter<Moment> ofMomentPattern(
        String pattern,
        Locale locale,
        TZID tzid
    ) {

        return new SimpleFormatter<Moment>(
            Moment.class,
            pattern,
            locale,
            Leniency.SMART,
            tzid.canonical()
        );

    }

    /**
     * <p>Constructs a style-based formatter for global timestamp objects (moments). </p>
     *
     * @param   dateStyle   format style for the date component
     * @param   timeStyle   format style for the time component
     * @param   locale      format locale
     * @param   tzid        timezone id
     * @return  new {@code SimpleFormatter}-instance
     * @throws  IllegalStateException if no localized format pattern can be retrieved
     * @since   5.0
     */
    /*[deutsch]
     * <p>Konstruiert einen Formatierer f&uuml;r globale Zeitstempelobjekte (Momente). </p>
     *
     * @param   dateStyle   format style for the date component
     * @param   timeStyle   format style for the time component
     * @param   locale      format locale
     * @param   tzid        timezone id
     * @return  new {@code SimpleFormatter}-instance
     * @throws  IllegalStateException if no localized format pattern can be retrieved
     * @since   5.0
     */
    public static SimpleFormatter<Moment> ofMomentStyle(
        DisplayMode dateStyle,
        DisplayMode timeStyle,
        Locale locale,
        TZID tzid
    ) {

        DateFormat df = DateFormat.getDateTimeInstance(dateStyle.getStyleValue(), timeStyle.getStyleValue(), locale);
        String pattern = getFormatPattern(df);
        return SimpleFormatter.ofMomentPattern(pattern, locale, tzid);

    }

    @Override
    public String format(T formattable) {

        return this.print(formattable);

    }

    @Override
    public String print(T formattable) {

        StringBuilder buffer = new StringBuilder();

        try {
            this.print(formattable, buffer);
            return buffer.toString();
        } catch (IOException ioe) {
            throw new AssertionError(ioe); // cannot happen
        }

    }

    @Override
    public T parse(CharSequence text) throws ParseException {

        ParsePosition pp = new ParsePosition(0);
        T result;

        try {
            result = this.parseInternal(text, pp, null);

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
    public T parse(CharSequence text, RawValues rawValues) throws ParseException {

        if (rawValues == null) {
            throw new NullPointerException("Missing raw values.");
        }

        ParsePosition pp = new ParsePosition(0);
        T result;

        try {
            result = this.parseInternal(text, pp, rawValues);

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
    public TemporalFormatter<T> withTimezone(TZID tzid) {

        return this.withTimezone(tzid.canonical());

    }

    @Override
    public TemporalFormatter<T> withTimezone(String tzid) {

        return new SimpleFormatter<T>(
            this.type,
            this.pattern,
            this.locale,
            this.leniency,
            tzid
        );

    }

    @Override
    public TemporalFormatter<T> with(Locale locale) {

        return new SimpleFormatter<T>(
            this.type,
            this.pattern,
            locale,
            this.leniency,
            this.tzid
        );

    }

    @Override
    public TemporalFormatter<T> with(Leniency leniency) {

        return new SimpleFormatter<T>(
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

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof SimpleFormatter) {
            SimpleFormatter<?> that = (SimpleFormatter<?>) obj;
            return (
                this.type.equals(that.type)
                && this.pattern.equals(that.pattern)
                && this.locale.equals(that.locale)
                && (this.leniency == that.leniency)
                && ((this.tzid == null) ? (that.tzid == null) : this.tzid.equals(that.tzid))
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return 17 * this.pattern.hashCode() + 31 * this.locale.hashCode() + 37 * this.tzid.hashCode();

    }

    private void print(T formattable, Appendable buffer) throws IOException {

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
            if (this.tzid == null) {
                throw new IllegalArgumentException("Cannot print moment without timezone.");
            }
            String realPattern = this.pattern;
            if (realPattern.equals(RFC_1123_PATTERN)) {
                realPattern = RFC_1123_WIDE;
            }
            SimpleFormatter<ZonalDateTime> sf =
                new SimpleFormatter<ZonalDateTime>(
                    ZonalDateTime.class, realPattern, this.locale, this.leniency, this.tzid);
            text = sf.print(moment.inZonalView(this.tzid));
        } else if (this.type.equals(ZonalDateTime.class)) {
            ZonalDateTime zdt = ZonalDateTime.class.cast(formattable);
            Moment moment = zdt.toMoment();
            Date jud = TemporalType.JAVA_UTIL_DATE.from(moment);
            String timezone = (
                this.tzid == null
                    ? zdt.getTimezone().canonical()
                    : this.tzid);
            Timezone tz = Timezone.of(timezone);
            String replaceTZ = "GMT" + tz.getOffset(moment).toString();
            XCalendar gcal = new XCalendar(TimeZone.getTimeZone(replaceTZ), this.locale);
            SimpleDateFormat sdf = setUp(this.pattern, this.locale, gcal, !this.leniency.isStrict());
            FieldPosition fp = new FieldPosition(DateFormat.TIMEZONE_FIELD);
            StringBuffer sb = new StringBuffer();
            text = sdf.format(jud, sb, fp).toString();
            int start = fp.getBeginIndex();
            int end = fp.getEndIndex();

            if (
                (end > start)
                && (start > 0)
                && !(tz.getID() instanceof ZonalOffset)
                && this.hasTimezoneField()
            ) {
                boolean dst = tz.isDaylightSaving(moment);
                boolean abbreviated = !this.pattern.contains("zzzz");
                NameStyle style = (
                    abbreviated
                        ? (dst ? NameStyle.SHORT_DAYLIGHT_TIME : NameStyle.SHORT_STANDARD_TIME)
                        : (dst ? NameStyle.LONG_DAYLIGHT_TIME : NameStyle.LONG_STANDARD_TIME)
                );
                String name = tz.getDisplayName(style, this.locale);
                text = text.substring(0, start) + name + text.substring(end);
            }
        } else {
            throw new IllegalArgumentException("Not formattable: " + formattable);
        }

        buffer.append(text);

    }

    private boolean hasTimezoneField() {

        boolean literal = false;

        for (int i = this.pattern.length() - 1; i >= 0; i--) {
            char c = this.pattern.charAt(i);
            if (c == '\'') {
                literal = !literal;
            } else if (!literal && (c == 'z')) {
                return true;
            }
        }

        return false;

    }

    private static void updateRawValues(
        RawValues rawValues,
        SimpleDateFormat sdf
    ) {

        if (rawValues != null) {
            rawValues.accept(new Parsed(XCalendar.class.cast(sdf.getCalendar())));
        }

    }

    private SimpleDateFormat setUpLocal() {

        XCalendar gcal = new XCalendar(TimeZone.getTimeZone("GMT"), this.locale);
        return setUp(this.pattern, this.locale, gcal, !this.leniency.isStrict());

    }

    private static SimpleDateFormat setUp(
        String pattern,
        Locale locale,
        XCalendar gcal,
        boolean lenient
    ) {

        SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
        sdf.setCalendar(gcal);
        sdf.setLenient(lenient);
        return sdf;

    }

    private T parseInternal(
        CharSequence text,
        ParsePosition position,
        RawValues rawValues
    ) {

        String parseable = text.toString();
        Object result;

        if (this.type.equals(PlainDate.class)) {
            SimpleDateFormat sdf = this.setUpLocal();
            Date jud = sdf.parse(parseable, position);
            PlainTimestamp tsp = TemporalType.JAVA_UTIL_DATE.translate(jud).toZonalTimestamp(ZonalOffset.UTC);
            result = tsp.getCalendarDate();
            updateRawValues(rawValues, sdf);
        } else if (this.type.equals(PlainTime.class)) {
            SimpleDateFormat sdf = this.setUpLocal();
            Date jud = sdf.parse(parseable, position);
            PlainTimestamp tsp = TemporalType.JAVA_UTIL_DATE.translate(jud).toZonalTimestamp(ZonalOffset.UTC);
            result = tsp.getWallTime();
            updateRawValues(rawValues, sdf);
        } else if (this.type.equals(PlainTimestamp.class)) {
            SimpleDateFormat sdf = this.setUpLocal();
            Date jud = sdf.parse(parseable, position);
            result = TemporalType.JAVA_UTIL_DATE.translate(jud).toZonalTimestamp(ZonalOffset.UTC);
            updateRawValues(rawValues, sdf);
        } else if (this.type.equals(Moment.class)) {
            String realPattern = this.pattern;
            if (realPattern.equals(RFC_1123_PATTERN)) {
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
            SimpleFormatter<ZonalDateTime> sf =
                new SimpleFormatter<ZonalDateTime>(
                    ZonalDateTime.class, realPattern, this.locale, this.leniency, this.tzid);
            ZonalDateTime zdt = sf.parseInternal(text, position, rawValues);
            result = ((zdt == null) ? null : zdt.toMoment());
        } else if (this.type.equals(ZonalDateTime.class)) {
            String timezone = (
                (this.tzid == null)
                ? "GMT-18:00"
                : this.tzid.replace("UTC", "GMT"));
            XCalendar gcal = new XCalendar(TimeZone.getTimeZone(timezone), this.locale);
            SimpleDateFormat sdf = setUp(this.pattern, this.locale, gcal, !this.leniency.isStrict());
            Date jud = sdf.parse(parseable, position);

            if (
                (jud == null)
                || (position.getErrorIndex() >= 0)
            ) {
                return null;
            }

            Parsed parsed = new Parsed(gcal);
            int offset = gcal.get(Calendar.ZONE_OFFSET) + gcal.get(Calendar.DST_OFFSET);

            if (offset == -1080) {
                position.setErrorIndex(position.getIndex());
                throw new IllegalArgumentException("Cannot parse text without timezone: " + parseable);
            } else if (!gcal.getTimeZone().getID().equals(timezone)) {
                timezone = gcal.getTimeZone().getID();
            } else {
                ZonalOffset zo = ZonalOffset.ofTotalSeconds(offset / 1000);
                if (
                    (this.tzid != null)
                    && (gcal.getTimeZone().getOffset(jud.getTime()) == offset)
                ) {
                    timezone = this.tzid;
                } else {
                    timezone = zo.canonical();
                }
            }

            TZID parsedTimezone = Timezone.of(timezone).getID();
            parsed.setTimezone(parsedTimezone);
            PlainTimestamp tsp =
                PlainTimestamp.axis().createFrom(parsed, this.getAttributes(), this.leniency.isLax(), false);
            if (tsp == null) {
                result = null;
            } else {
                result = tsp.inTimezone(parsedTimezone).inZonalView(parsedTimezone);
            }
            if (rawValues != null) {
                rawValues.accept(parsed);
            }
        } else {
            result = null;
        }

        return this.type.cast(result);

    }

    private static String getFormatPattern(DateFormat df) {

        if (df instanceof SimpleDateFormat) {
            return SimpleDateFormat.class.cast(df).toPattern();
        }

        throw new IllegalStateException("Cannot retrieve format pattern: " + df);

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class Parsed
        extends ChronoEntity<Parsed> {

        //~ Instanzvariablen ----------------------------------------------

        private final Map<ChronoElement<?>, Object> values;
        private TZID tzid = null;

        //~ Konstruktoren -------------------------------------------------

        Parsed(XCalendar cal) {
            super();

            Map<ChronoElement<?>, Object> map = new HashMap<ChronoElement<?>, Object>();
            if (cal.isSet(Calendar.YEAR)) {
                map.put(PlainDate.YEAR, cal.getRawValue(Calendar.YEAR));
            }
            if (cal.isSet(Calendar.MONTH)) {
                map.put(PlainDate.MONTH_AS_NUMBER, cal.getRawValue(Calendar.MONTH) + 1);
            }
            if (cal.isSet(Calendar.DAY_OF_YEAR)) {
                map.put(PlainDate.DAY_OF_YEAR, cal.getRawValue(Calendar.DAY_OF_YEAR));
            }
            if (cal.isSet(Calendar.DAY_OF_MONTH)) {
                map.put(PlainDate.DAY_OF_MONTH, cal.getRawValue(Calendar.DAY_OF_MONTH));
            }
            if (cal.isSet(Calendar.AM_PM)) {
                map.put(PlainTime.AM_PM_OF_DAY, Meridiem.values()[cal.getRawValue(Calendar.AM_PM)]);
            }
            if (cal.isSet(Calendar.HOUR)) {
                map.put(PlainTime.DIGITAL_HOUR_OF_AMPM, cal.getRawValue(Calendar.HOUR));
            }
            if (cal.isSet(Calendar.HOUR_OF_DAY)) {
                map.put(PlainTime.DIGITAL_HOUR_OF_DAY, cal.getRawValue(Calendar.HOUR_OF_DAY));
            }
            if (cal.isSet(Calendar.MINUTE)) {
                map.put(PlainTime.MINUTE_OF_HOUR, cal.getRawValue(Calendar.MINUTE));
            }
            if (cal.isSet(Calendar.SECOND)) {
                map.put(PlainTime.SECOND_OF_MINUTE, cal.getRawValue(Calendar.SECOND));
            }
            if (cal.isSet(Calendar.MILLISECOND)) {
                map.put(PlainTime.MILLI_OF_SECOND, cal.getRawValue(Calendar.MILLISECOND));
            }
            this.values = Collections.unmodifiableMap(map);

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
        public int getInt(ChronoElement<Integer> element) {

            if (this.values.containsKey(element)) {
                return Integer.class.cast(this.values.get(element)).intValue();
            } else {
                return Integer.MIN_VALUE;
            }

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
        public <V> boolean isValid(
            ChronoElement<V> element,
            V value // optional
        ) {

            return (element != null);

        }

        @Override
        public <V> Parsed with(
            ChronoElement<V> element,
            V value // optional
        ) {

            if (element == null) {
                throw new NullPointerException();
            } else if (value == null) {
                this.values.remove(element);
            } else {
                this.values.put(element, value);
            }

            return this;

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

        @Override
        public Set<ChronoElement<?>> getRegisteredElements() {

            return Collections.unmodifiableSet(this.values.keySet());

        }

        void setTimezone(TZID tzid) {

            this.tzid = tzid;

        }

        @Override
        protected Chronology<Parsed> getChronology() {

            throw new UnsupportedOperationException(
                "Parsed values do not have any chronology.");

        }

        private void check(ChronoElement<?> element) {

            if (!this.values.containsKey(element)) {
                throw new ChronoException("Element not supported: " + element.name());
            }

        }

    }

    private static class XCalendar
        extends GregorianCalendar {

        //~ Konstruktoren -------------------------------------------------

        XCalendar(
            TimeZone tz,
            Locale locale
        ) {
            super(tz, locale);

            this.setGregorianChange(PROLEPTIC_GREGORIAN);

        }

        //~ Methoden ------------------------------------------------------

        int getRawValue(int field) {

            return super.internalGet(field);

        }

    }

}
