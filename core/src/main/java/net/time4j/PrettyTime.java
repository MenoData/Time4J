/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (PrettyTime.java) is part of project Time4J.
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

import net.time4j.base.MathUtils;
import net.time4j.base.TimeSource;
import net.time4j.base.UnixTime;
import net.time4j.engine.TimeSpan;
import net.time4j.format.NumberType;
import net.time4j.format.PluralCategory;
import net.time4j.format.PluralRules;
import net.time4j.format.TextWidth;
import net.time4j.format.UnitPatterns;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;

import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static net.time4j.CalendarUnit.DAYS;
import static net.time4j.CalendarUnit.MONTHS;
import static net.time4j.CalendarUnit.WEEKS;
import static net.time4j.CalendarUnit.YEARS;
import static net.time4j.ClockUnit.HOURS;
import static net.time4j.ClockUnit.MICROS;
import static net.time4j.ClockUnit.MILLIS;
import static net.time4j.ClockUnit.MINUTES;
import static net.time4j.ClockUnit.NANOS;
import static net.time4j.ClockUnit.SECONDS;


/**
 * <p>Enables formatted output as usually used in social media in different
 * languages. </p>
 *
 * <p>Parsing is not included because there is no general solution for all
 * locales. Instead users must keep the backing duration object and use it
 * for printing. </p>
 *
 * @author  Meno Hochschild
 * @since   1.2
 * @concurrency <immutable>
 */
/*[deutsch]
 * <p>Erm&ouml;glicht formatierte Ausgaben einer Dauer f&uuml;r soziale Medien
 * (&quot;social media style&quot;) in verschiedenen Sprachen. </p>
 *
 * <p>Der R&uuml;ckweg der Interpretation (<i>parsing</i>) ist nicht enthalten,
 * weil so nicht alle Sprachen unterst&uuml;tzt werden k&ouml;nnen. Stattdessen
 * werden Anwender angehalten, das korrespondierende Dauer-Objekt im Hintergrund
 * zu halten und es f&uuml;r die formatierte Ausgabe zu nutzen. </p>
 *
 * @author  Meno Hochschild
 * @since   1.2
 * @concurrency <immutable>
 */
public final class PrettyTime {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int MIO = 1000000;
    private static final char UNICODE_RLM = '\u200F';

    private static final ConcurrentMap<Locale, PrettyTime> LANGUAGE_MAP =
        new ConcurrentHashMap<Locale, PrettyTime>();
    private static final IsoUnit[] STD_UNITS;
    private static final Set<IsoUnit> SUPPORTED_UNITS;

    static {
        IsoUnit[] units =
            {YEARS, MONTHS, WEEKS, DAYS, HOURS, MINUTES, SECONDS};
        STD_UNITS = units;

        Set<IsoUnit> tmp = new HashSet<IsoUnit>();
        for (IsoUnit unit : units) {
            tmp.add(unit);
        }
        tmp.add(MILLIS);
        tmp.add(MICROS);
        tmp.add(NANOS);
        SUPPORTED_UNITS = Collections.unmodifiableSet(tmp);
    }

    //~ Instanzvariablen --------------------------------------------------

    private final PluralRules rules;
    private final Locale locale;
    private final TimeSource<?> refClock;
    private final char zeroDigit;
    private final IsoUnit emptyUnit;
    private final int minusOrientation;
    private final char minusSign;

    //~ Konstruktoren -----------------------------------------------------

    private PrettyTime(
        Locale locale,
        TimeSource<?> refClock,
        char zeroDigit,
        IsoUnit emptyUnit
    ) {
        super();

        if (emptyUnit == null) {
            throw new NullPointerException("Missing zero time unit.");
        } else if (refClock == null) {
            throw new NullPointerException("Missing reference clock.");
        }

        // throws NPE if language == null
        this.rules = PluralRules.of(locale, NumberType.CARDINALS);
        this.locale = locale;
        this.refClock = refClock;
        this.zeroDigit = zeroDigit;
        this.emptyUnit = emptyUnit;

        char c = DecimalFormatSymbols.getInstance(locale).getMinusSign();
        String s = NumberFormat.getInstance(locale).format(-123L);
        this.minusOrientation = (
            (s.charAt(s.length() - 1) == c)
            ? RIGHT
            : LEFT);
        this.minusSign = c;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Gets an instance of {@code PrettyTime} for given language,
     * possibly cached. </p>
     *
     * @param   locale    the language an instance is searched for
     * @return  pretty time object for formatting durations or relative time
     * @since   1.2
     */
    /*[deutsch]
     * <p>Liefert eine Instanz von {@code PrettyTime} f&uuml;r die angegebene
     * Sprache, eventuell aus dem Cache. </p>
     *
     * @param   locale    the language an instance is searched for
     * @return  pretty time object for formatting durations or relative time
     * @since   1.2
     */
    public static PrettyTime of(Locale locale) {

        PrettyTime ptime = LANGUAGE_MAP.get(locale);

        if (ptime == null) {
            ptime =
                new PrettyTime(
                    locale,
                    SystemClock.INSTANCE,
                    DecimalFormatSymbols.getInstance(locale).getZeroDigit(),
                    SECONDS);
            PrettyTime old = LANGUAGE_MAP.putIfAbsent(locale, ptime);

            if (old != null) {
                ptime = old;
            }
        }

        return ptime;

    }

    /**
     * <p>Gets the language of this instance. </p>
     *
     * @return  language
     * @since   1.2
     */
    /*[deutsch]
     * <p>Liefert die Bezugssprache. </p>
     *
     * @return  Spracheinstellung
     * @since   1.2
     */
    public Locale getLocale() {

        return this.locale;

    }

    /**
     * <p>Yields the reference clock for formatting of relative times. </p>
     *
     * @return  reference clock or system clock if not yet specified
     * @since   1.2
     * @see     #withReferenceClock(TimeSource)
     * @see     #printRelative(UnixTime, TZID)
     * @see     #printRelative(UnixTime, String)
     */
    /*[deutsch]
     * <p>Liefert die Bezugsuhr f&uuml;r formatierte Ausgaben der relativen
     * Zeit. </p>
     *
     * @return  Zeitquelle oder die Systemuhr, wenn noch nicht angegeben
     * @since   1.2
     * @see     #withReferenceClock(TimeSource)
     * @see     #printRelative(UnixTime, TZID)
     * @see     #printRelative(UnixTime, String)
     */
    public TimeSource<?> getReferenceClock() {

        return this.refClock;

    }

    /**
     * <p>Yields a changed copy of this instance with given reference
     * clock. </p>
     *
     * <p>If given reference clock is {@code null} then the reference clock
     * will always be the system clock. </p>
     *
     * @param   clock   new reference clock (maybe {@code null})
     * @return  new instance of {@code PrettyTime} with changed reference clock
     * @since   1.2
     * @see     #getReferenceClock()
     * @see     #printRelative(UnixTime, TZID)
     * @see     #printRelative(UnixTime, String)
     */
    /*[deutsch]
     * <p>Legt die Bezugszeit f&uuml;r relative Zeitangaben neu fest. </p>
     *
     * <p>Wenn die angegebene Bezugsuhr {@code null} ist, wird die
     * Systemuhr verwendet. </p>
     *
     * @param   clock   new reference clock
     * @return  new instance of {@code PrettyTime} with changed reference clock
     * @since   1.2
     * @see     #getReferenceClock()
     * @see     #printRelative(UnixTime, TZID)
     * @see     #printRelative(UnixTime, String)
     */
    public PrettyTime withReferenceClock(TimeSource<?> clock) {

        return new PrettyTime(
            this.locale,
            clock,
            this.zeroDigit,
            this.emptyUnit);

    }

    /**
     * <p>Defines the localized zero digit. </p>
     *
     * <p>In most languages the zero digit is just ASCII-&quot;0&quot;, but
     * in arabic locales the digit can also be the char {@code U+0660}. By
     * default Time4J will try to use the JDK-setting. This method can override
     * it however. </p>
     *
     * @param   zeroDigit   localized zero digit
     * @return  changed copy of this instance
     * @since   1.2
     * @see     java.text.DecimalFormatSymbols#getZeroDigit()
     */
    /*[deutsch]
     * <p>Definiert die lokalisierte Nullziffer. </p>
     *
     * <p>In den meisten Sprachen ist die Nullziffer ASCII-&quot;0&quot;,
     * aber im arabischen Sprachraum kann das Zeichen auch {@code U+0660}
     * sein. Per Vorgabe wird Time4J versuchen, die JDK-Einstellung zu
     * verwenden. Diese Methode &uuml;berschreibt jedoch den Standard. </p>
     *
     * @param   zeroDigit   localized zero digit
     * @return  changed copy of this instance
     * @since   1.2
     * @see     java.text.DecimalFormatSymbols#getZeroDigit()
     */
    public PrettyTime withZeroDigit(char zeroDigit) {

        return new PrettyTime(
            this.locale,
            this.refClock,
            zeroDigit,
            this.emptyUnit);

    }

    /**
     * <p>Defines the time unit used for formatting an empty duration. </p>
     *
     * <p>Time4J uses seconds as default. This method can override the
     * default however. </p>
     *
     * @param   emptyUnit   time unit for usage in an empty duration
     * @return  changed copy of this instance
     * @since   1.2
     */
    /*[deutsch]
     * <p>Definiert die Zeiteinheit f&uuml;r die Verwendung in der
     * Formatierung einer leeren Dauer. </p>
     *
     * <p>Vorgabe ist die Sekundeneinheit. Diese Methode kann die Vorgabe
     * jedoch &uuml;berschreiben. </p>
     *
     * @param   emptyUnit   time unit for usage in an empty duration
     * @return  changed copy of this instance
     * @since   1.2
     */
    public PrettyTime withEmptyUnit(CalendarUnit emptyUnit) {

        return new PrettyTime(
            this.locale,
            this.refClock,
            Character.valueOf(this.zeroDigit),
            emptyUnit);

    }

    /**
     * <p>Defines the time unit used for formatting an empty duration. </p>
     *
     * <p>Time4J uses seconds as default. This method can override the
     * default however. </p>
     *
     * @param   emptyUnit   time unit for usage in an empty duration
     * @return  changed copy of this instance
     * @since   1.2
     */
    /*[deutsch]
     * <p>Definiert die Zeiteinheit f&uuml;r die Verwendung in der
     * Formatierung einer leeren Dauer. </p>
     *
     * <p>Vorgabe ist die Sekundeneinheit. Diese Methode kann die Vorgabe
     * jedoch &uuml;berschreiben. </p>
     *
     * @param   emptyUnit   time unit for usage in an empty duration
     * @return  changed copy of this instance
     * @since   1.2
     */
    public PrettyTime withEmptyUnit(ClockUnit emptyUnit) {

        return new PrettyTime(
            this.locale,
            this.refClock,
            Character.valueOf(this.zeroDigit),
            emptyUnit);

    }

    /**
     * <p>Formats given duration in calendar units. </p>
     *
     * <p>Note: Millennia, centuries and decades are automatically normalized
     * to years while quarter-years are normalized to months. </p>
     *
     * @param   amount  count of units (quantity)
     * @param   unit    calendar unit
     * @param   width   text width (ABBREVIATED as synonym for SHORT)
     * @return  formatted output
     * @since   1.2
     * @see     #print(Duration, TextWidth)
     */
    /*[deutsch]
     * <p>Formatiert die angegebene Dauer in kalendarischen Zeiteinheiten. </p>
     *
     * <p>Hinweis: Jahrtausende, Jahrhunderte und Dekaden werden automatisch
     * zu Jahren normalisiert, w&auml;hrend Quartale zu Monaten normalisiert
     * werden. </p>
     *
     * @param   amount  Anzahl der Einheiten
     * @param   unit    kalendarische Zeiteinheit
     * @param   width   text width (ABBREVIATED as synonym for SHORT)
     * @return  formatierte Ausgabe
     * @since   1.2
     * @see     #print(Duration, TextWidth)
     */
    public String print(
        long amount,
        CalendarUnit unit,
        TextWidth width
    ) {

        UnitPatterns p = UnitPatterns.of(this.locale);
        String pattern;

        switch (unit) {
            case MILLENNIA:
                amount = MathUtils.safeMultiply(amount, 1000);
                pattern = p.getYears(width, this.getCategory(amount));
                break;
            case CENTURIES:
                amount = MathUtils.safeMultiply(amount, 100);
                pattern = p.getYears(width, this.getCategory(amount));
                break;
            case DECADES:
                amount = MathUtils.safeMultiply(amount, 10);
                pattern = p.getYears(width, this.getCategory(amount));
                break;
            case YEARS:
                pattern = p.getYears(width, this.getCategory(amount));
                break;
            case QUARTERS:
                amount = MathUtils.safeMultiply(amount, 3);
                pattern = p.getMonths(width, this.getCategory(amount));
                break;
            case MONTHS:
                pattern = p.getMonths(width, this.getCategory(amount));
                break;
            case WEEKS:
                pattern = p.getWeeks(width, this.getCategory(amount));
                break;
            case DAYS:
                pattern = p.getDays(width, this.getCategory(amount));
                break;
            default:
                throw new UnsupportedOperationException(unit.name());
        }

        return this.format(pattern, amount);

    }

    /**
     * <p>Formats given duration in clock units. </p>
     *
     * @param   amount  count of units (quantity)
     * @param   unit    clock unit
     * @param   width   text width (ABBREVIATED as synonym for SHORT)
     * @return  formatted output
     * @since   1.2
     * @see     #print(Duration, TextWidth)
     */
    /*[deutsch]
     * <p>Formatiert die angegebene Dauer in Uhrzeiteinheiten. </p>
     *
     * @param   amount  Anzahl der Einheiten
     * @param   unit    Uhrzeiteinheit
     * @param   width   text width (ABBREVIATED as synonym for SHORT)
     * @return  formatierte Ausgabe
     * @since   1.2
     * @see     #print(Duration, TextWidth)
     */
    public String print(
        long amount,
        ClockUnit unit,
        TextWidth width
    ) {

        UnitPatterns p = UnitPatterns.of(this.locale);
        String pattern;

        switch (unit) {
            case HOURS:
                pattern = p.getHours(width, this.getCategory(amount));
                break;
            case MINUTES:
                pattern = p.getMinutes(width, this.getCategory(amount));
                break;
            case SECONDS:
                pattern = p.getSeconds(width, this.getCategory(amount));
                break;
            case MILLIS:
                pattern = p.getMillis(width, this.getCategory(amount));
                break;
            case MICROS:
                pattern = p.getMicros(width, this.getCategory(amount));
                break;
            case NANOS:
                pattern = p.getNanos(width, this.getCategory(amount));
                break;
            default:
                throw new UnsupportedOperationException(unit.name());
        }

        return this.format(pattern, amount);

    }

    /**
     * <p>Formats given duration. </p>
     *
     * <p>A localized output is only supported for the units
     * {@link CalendarUnit#YEARS}, {@link CalendarUnit#MONTHS},
     * {@link CalendarUnit#WEEKS}, {@link CalendarUnit#DAYS} and
     * all {@link ClockUnit}-units. This methode does not perform
     * any normalization. </p>
     *
     * <p>Note: If the local script variant is from right to left
     * then a unicode-RLM-marker will automatically be inserted
     * before each number. </p>
     *
     * @param   duration    object representing a duration which might contain
     *                      several units and quantities
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @return  formatted list output
     * @since   1.2
     */
    /*[deutsch]
     * <p>Formatiert die angegebene Dauer. </p>
     *
     * <p>Eine lokalisierte Ausgabe ist nur f&uuml;r die Zeiteinheiten
     * {@link CalendarUnit#YEARS}, {@link CalendarUnit#MONTHS},
     * {@link CalendarUnit#WEEKS}, {@link CalendarUnit#DAYS} und
     * alle {@link ClockUnit}-Instanzen vorhanden. Diese Methode
     * f&uuml;hrt keine Normalisierung durch. </p>
     *
     * <p>Hinweis: Wenn die lokale Skript-Variante von rechts nach links
     * geht, wird automatisch ein Unicode-RLM-Marker vor jeder Nummer
     * eingef&uuml;gt. </p>
     *
     * @param   duration    object representing a duration which might contain
     *                      several units and quantities
     * @param   width       text width (ABBREVIATED as synonym for SHORT)
     * @return  formatted list output
     * @since   1.2
     */
    public String print(
        Duration<?> duration,
        TextWidth width
    ) {

        if (duration.isEmpty()) {
            if (this.emptyUnit.isCalendrical()) {
                CalendarUnit unit = CalendarUnit.class.cast(this.emptyUnit);
                return this.print(0, unit, width);
            } else {
                ClockUnit unit = ClockUnit.class.cast(this.emptyUnit);
                return this.print(0, unit, width);
            }
        }

        int len = duration.getTotalLength().size();
        boolean negative = duration.isNegative();

        if (len == 1) {
            TimeSpan.Item<? extends IsoUnit> item =
                duration.getTotalLength().get(0);
            return this.format(item, negative, width);
        }

        Object[] parts = new Object[len];

        for (int i = 0; i < len; i++) {
            parts[i] =
                this.format(duration.getTotalLength().get(i), negative, width);
        }

        UnitPatterns p = UnitPatterns.of(this.locale);
        return MessageFormat.format(p.getListPattern(width, len), parts);

    }

    /**
     * <p>Formats given time point relative to the current time of
     * {@link #getReferenceClock()} as duration in at most second
     * precision or less. </p>
     *
     * @param   moment      relative time point
     * @param   tzid        time zone id for translating to a local duration
     * @return  formatted output of relative time, either in past or in future
     * @since   1.2
     */
    /*[deutsch]
     * <p>Formatiert den angegebenen Zeitpunkt relativ zur aktuellen Zeit
     * der Referenzuhr {@link #getReferenceClock()} als Dauer in maximal
     * Sekundengenauigkeit. </p>
     *
     * @param   moment      relative time point
     * @param   tzid        time zone id for translating to a local duration
     * @return  formatted output of relative time, either in past or in future
     * @since   1.2
     */
    public String printRelative(
        UnixTime moment,
        TZID tzid
    ) {

        return this.print(moment, Timezone.of(tzid));

    }

    /**
     * <p>Formats given time point relative to the current time of
     * {@link #getReferenceClock()} as duration in at most second
     * precision or less. </p>
     *
     * @param   moment      relative time point
     * @param   tzid        time zone id for translating to a local duration
     * @return  formatted output of relative time, either in past or in future
     * @since   1.2
     */
    /*[deutsch]
     * <p>Formatiert den angegebenen Zeitpunkt relativ zur aktuellen Zeit
     * der Referenzuhr {@link #getReferenceClock()} als Dauer in maximal
     * Sekundengenauigkeit. </p>
     *
     * @param   moment      relative time point
     * @param   tzid        time zone id for translating to a local duration
     * @return  formatted output of relative time, either in past or in future
     * @since   1.2
     */
    public String printRelative(
        UnixTime moment,
        String tzid
    ) {

        return this.print(moment, Timezone.of(tzid));

    }

    private String print(
        UnixTime ut,
        Timezone tz
    ) {

        UnixTime ref = this.getReferenceClock().currentTime();

        PlainTimestamp start =
            PlainTimestamp.from(
                ref,
                tz.getOffset(ref));
        PlainTimestamp end =
            PlainTimestamp.from(
                ut,
                tz.getOffset(ut));

        Duration<IsoUnit> duration =
            Duration.in(tz, STD_UNITS).between(start, end);

        if (duration.isEmpty()) {
            return UnitPatterns.of(this.locale).getNowWord();
        }

        TimeSpan.Item<IsoUnit> item = duration.getTotalLength().get(0);
        long amount = item.getAmount();
        IsoUnit unit = item.getUnit();
        String pattern;

        if (duration.isNegative()) {
            if (unit.isCalendrical()) {
                pattern = this.getPastPattern(amount, (CalendarUnit) unit);
            } else {
                pattern = this.getPastPattern(amount, (ClockUnit) unit);
            }
        } else {
            if (unit.isCalendrical()) {
                pattern = this.getFuturePattern(amount, (CalendarUnit) unit);
            } else {
                pattern = this.getFuturePattern(amount, (ClockUnit) unit);
            }
        }

        return this.format(pattern, amount);

    }

    private String getPastPattern(
        long amount,
        CalendarUnit unit
    ) {

        UnitPatterns patterns = UnitPatterns.of(this.locale);
        PluralCategory category = this.getCategory(amount);

        switch (unit) {
            case YEARS:
                return patterns.getYearsInPast(category);
            case MONTHS:
                return patterns.getMonthsInPast(category);
            case WEEKS:
                return patterns.getWeeksInPast(category);
            case DAYS:
                return patterns.getDaysInPast(category);
            default:
                throw new UnsupportedOperationException(unit.name());
        }

    }

    private String getFuturePattern(
        long amount,
        CalendarUnit unit
    ) {

        UnitPatterns patterns = UnitPatterns.of(this.locale);
        PluralCategory category = this.getCategory(amount);

        switch (unit) {
            case YEARS:
                return patterns.getYearsInFuture(category);
            case MONTHS:
                return patterns.getMonthsInFuture(category);
            case WEEKS:
                return patterns.getWeeksInFuture(category);
            case DAYS:
                return patterns.getDaysInFuture(category);
            default:
                throw new UnsupportedOperationException(unit.name());
        }

    }

    private String getPastPattern(
        long amount,
        ClockUnit unit
    ) {

        UnitPatterns patterns = UnitPatterns.of(this.locale);
        PluralCategory category = this.getCategory(amount);

        switch (unit) {
            case HOURS:
                return patterns.getHoursInPast(category);
            case MINUTES:
                return patterns.getMinutesInPast(category);
            case SECONDS:
                return patterns.getSecondsInPast(category);
            default:
                throw new UnsupportedOperationException(unit.name());
        }

    }

    private String getFuturePattern(
        long amount,
        ClockUnit unit
    ) {

        UnitPatterns patterns = UnitPatterns.of(this.locale);
        PluralCategory category = this.getCategory(amount);

        switch (unit) {
            case HOURS:
                return patterns.getHoursInFuture(category);
            case MINUTES:
                return patterns.getMinutesInFuture(category);
            case SECONDS:
                return patterns.getSecondsInFuture(category);
            default:
                throw new UnsupportedOperationException(unit.name());
        }

    }

    private PluralCategory getCategory(long amount) {

        return this.rules.getCategory(Math.abs(amount));

    }

    private String format(
        TimeSpan.Item<? extends IsoUnit> item,
        boolean negative,
        TextWidth width
    ) {
    	
        return this.format(item.getAmount(), item.getUnit(), negative, width);
    	
    }

    private String format(
        long amount,
        IsoUnit unit,
        boolean negative,
        TextWidth width
    ) {

        long value = amount;

        if (negative) {
            value = MathUtils.safeNegate(amount);
        }

        if (SUPPORTED_UNITS.contains(unit)) {
            if (unit.isCalendrical()) {
                CalendarUnit u = CalendarUnit.class.cast(unit);
                return this.print(value, u, width);
            } else {
                ClockUnit u = ClockUnit.class.cast(unit);
                if (u == NANOS) { // Duration has no internal MILLIS or MICROS
                    if ((amount % MIO) == 0) {
                        u = MILLIS;
                        value = value / MIO;
                    } else if ((amount % 1000) == 0) {
                        u = MICROS;
                        value = value / 1000;
                    }
                }
                return this.print(value, u, width);
            }
        } else if (unit instanceof OverflowUnit) {
            CalendarUnit u = OverflowUnit.class.cast(unit).getCalendarUnit();
            if (SUPPORTED_UNITS.contains(u)) {
                return this.print(value, u, width);
            }
        } else if (unit.equals(CalendarUnit.weekBasedYears())) {
            return this.print(value, CalendarUnit.YEARS, width);
        }

        return this.format(value) + " " + unit.toString(); // fallback

    }

    private String format(
        String pattern,
        long amount
    ) {

        for (int i = 0, n = pattern.length(); i < n; i++) {
            if (
                (i < n - 2)
                && (pattern.charAt(i) == '{')
                && (pattern.charAt(i + 1) == '0')
                && (pattern.charAt(i + 2) == '}')
            ) {
                StringBuilder sb = new StringBuilder(pattern);
                sb.replace(i, i + 3, this.format(amount));
                return sb.toString();
            }
        }

        return pattern;

    }

    private String format(long amount) {

        StringBuilder sb = new StringBuilder();
        String num = String.valueOf(Math.abs(amount));
        char zero = this.zeroDigit;

        if ((this.minusOrientation == LEFT) && (amount < 0)) {
            sb.append(this.minusSign);
        } else if (this.minusOrientation == RIGHT) {
            sb.append(UNICODE_RLM);
        }

        for (int i = 0, n = num.length(); i < n; i++) {
            char c = num.charAt(i);
            if (zero != '0') {
                c = (char) (c + zero - '0');
            }
            sb.append(c);
        }

        if ((this.minusOrientation == RIGHT) && (amount < 0)) {
            sb.append(this.minusSign);
        }

        return sb.toString();

    }

}
