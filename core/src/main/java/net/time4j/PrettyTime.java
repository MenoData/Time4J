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
import net.time4j.engine.TimeMetric;
import net.time4j.engine.TimeSpan;
import net.time4j.format.NumberType;
import net.time4j.format.PluralCategory;
import net.time4j.format.PluralRules;
import net.time4j.format.TextWidth;
import net.time4j.format.UnitPatterns;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static net.time4j.CalendarUnit.DAYS;
import static net.time4j.CalendarUnit.MONTHS;
import static net.time4j.CalendarUnit.WEEKS;
import static net.time4j.CalendarUnit.YEARS;
import static net.time4j.ClockUnit.HOURS;
import static net.time4j.ClockUnit.MINUTES;
import static net.time4j.ClockUnit.SECONDS;


/**
 * <p>Enables formatted output as usually used in social media. </p>
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
 * (&quot;social media style&quot;). </p>
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

    private static final ConcurrentMap<Locale, PrettyTime> LANGUAGE_MAP =
        new ConcurrentHashMap<Locale, PrettyTime>();

    private static final TimeMetric<IsoUnit, Duration<IsoUnit>> STD_METRIC;

    static {
        IsoUnit[] units =
            {YEARS, MONTHS, WEEKS, DAYS, HOURS, MINUTES, SECONDS};
        STD_METRIC = Duration.in(units);
    }

    //~ Instanzvariablen --------------------------------------------------

    private final PluralRules rules;
    private final Locale language;
    private final TimeSource<?> reference;

    //~ Konstruktoren -----------------------------------------------------

    private PrettyTime(
        Locale language,
        TimeSource<?> reference
    ) {
        super();

        // throws NPE if language == null
        this.rules = PluralRules.of(language, NumberType.CARDINALS);
        this.language = language;
        this.reference = reference;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Gets an instance of {@code PrettyTime} for given language,
     * possibly cached. </p>
     *
     * @param   language    the language an instance is searched for
     * @return  pretty time object for formatting durations or relative time
     */
    /*[deutsch]
     * <p>Liefert eine Instanz von {@code PrettyTime} f&uuml;r die angegebene
     * Sprache, eventuell aus dem Cache. </p>
     *
     * @param   language    the language an instance is searched for
     * @return  pretty time object for formatting durations or relative time
     */
    public static PrettyTime of(Locale language) {

        PrettyTime ptime = LANGUAGE_MAP.get(language);

        if (ptime == null) {
            ptime = new PrettyTime(language, null);
            PrettyTime old = LANGUAGE_MAP.putIfAbsent(language, ptime);

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
     */
    /*[deutsch]
     * <p>Liefert die Bezugssprache. </p>
     *
     * @return  Spracheinstellung
     */
    public Locale getLanguage() {

        return this.language;

    }

    /**
     * <p>Yields the reference clock for formatting of relative times. </p>
     *
     * @return  reference clock or system clock if not yet specified
     * @see     #withReference(TimeSource)
     * @see     #print(UnixTime, TZID)
     * @see     #print(UnixTime, String)
     */
    /*[deutsch]
     * <p>Liefert die Bezugsuhr f&uuml;r formatierte Ausgaben der relativen
     * Zeit. </p>
     *
     * @return  Zeitquelle oder die Systemuhr, wenn noch nicht angegeben
     * @see     #withReference(TimeSource)
     * @see     #print(UnixTime, TZID)
     * @see     #print(UnixTime, String)
     */
    public TimeSource<?> getReferenceClock() {

        if (this.reference == null) {
            return SystemClock.INSTANCE;
        }

        return this.reference;

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
     * @see     #getReferenceClock()
     * @see     #print(UnixTime, TZID)
     * @see     #print(UnixTime, String)
     */
    /*[deutsch]
     * <p>Legt die Bezugszeit f&uuml;r relative Zeitangaben neu fest. </p>
     *
     * <p>Wenn die angegebene Bezugsuhr {@code null} ist, wird die
     * Systemuhr verwendet. </p>
     *
     * @param   clock   new reference clock (maybe {@code null})
     * @return  new instance of {@code PrettyTime} with changed reference clock
     * @see     #getReferenceClock()
     * @see     #print(UnixTime, TZID)
     * @see     #print(UnixTime, String)
     */
    public PrettyTime withReferenceClock(TimeSource<?> clock) {

        if (clock == null) {
            return PrettyTime.of(this.language);
        }

        return new PrettyTime(this.language, clock);

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
     */
    public String print(
        long amount,
        CalendarUnit unit,
        TextWidth width
    ) {

        UnitPatterns p = UnitPatterns.of(language);
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

        String num =
            NumberFormat.getIntegerInstance(this.language).format(amount);
        return pattern.replace("{0}", num);

    }

    /**
     * <p>Formats given duration in clock units. </p>
     *
     * <p>Note: Subsecond parts will be normalized to full seconds. </p>
     *
     * @param   amount  count of units (quantity)
     * @param   unit    clock unit
     * @param   width   text width (ABBREVIATED as synonym for SHORT)
     * @return  formatted output
     */
    /*[deutsch]
     * <p>Formatiert die angegebene Dauer in Uhrzeiteinheiten. </p>
     *
     * <p>Hinweis: Sekundenbruchteile werden zu vollen Sekunden
     * normalisiert. </p>
     *
     * @param   amount  Anzahl der Einheiten
     * @param   unit    Uhrzeiteinheit
     * @param   width   text width (ABBREVIATED as synonym for SHORT)
     * @return  formatierte Ausgabe
     */
    public String print(
        long amount,
        ClockUnit unit,
        TextWidth width
    ) {

        UnitPatterns p = UnitPatterns.of(language);
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
                amount = amount / 1000;
                pattern = p.getSeconds(width, this.getCategory(amount));
                break;
            case MICROS:
                amount = amount / 1000000;
                pattern = p.getSeconds(width, this.getCategory(amount));
                break;
            case NANOS:
                amount = amount / 1000000000;
                pattern = p.getSeconds(width, this.getCategory(amount));
                break;
            default:
                throw new UnsupportedOperationException(unit.name());
        }

        String num =
            NumberFormat.getIntegerInstance(this.language).format(amount);
        return pattern.replace("{0}", num);

    }

//    /**
//     * <p>Formats given duration. </p>
//     *
//     * @param   duration    object representing a duration which might contain
//     *                      several units and quantities
//     * @param   width       text width (ABBREVIATED as synonym for SHORT)
//     * @return  formatted output
//     */
//    /*[deutsch]
//     * <p>Formatiert die angegebene Dauer. </p>
//     *
//     * @param   duration    object representing a duration which might contain
//     *                      several units and quantities
//     * @param   width       text width (ABBREVIATED as synonym for SHORT)
//     * @return  formatted output
//     */
//    public String print(
//        Duration<?> duration,
//        TextWidth width
//    ) {
//
//        throw new UnsupportedOperationException("Not yet implemented.");
//
//    }

    /**
     * <p>Formats given time point relative to the current time of
     * {@link #getReferenceClock()}. </p>
     *
     * @param   moment      relative time point
     * @param   tzid        time zone id for translating to a local duration
     * @return  formatted output of relative time, either in past or in future
     */
    /*[deutsch]
     * <p>Formatiert den angegebenen Zeitpunkt relativ zur aktuellen Zeit
     * der Referenzuhr {@link #getReferenceClock()}. </p>
     *
     * @param   moment      relative time point
     * @param   tzid        time zone id for translating to a local duration
     * @return  formatted output of relative time, either in past or in future
     */
    public String print(
        UnixTime moment,
        TZID tzid
    ) {

        return this.print(moment, Timezone.of(tzid));

    }

    /**
     * <p>Formats given time point relative to the current time of
     * {@link #getReferenceClock()}. </p>
     *
     * @param   moment      relative time point
     * @param   tzid        time zone id for translating to a local duration
     * @return  formatted output of relative time, either in past or in future
     */
    /*[deutsch]
     * <p>Formatiert den angegebenen Zeitpunkt relativ zur aktuellen Zeit
     * der Referenzuhr {@link #getReferenceClock()}. </p>
     *
     * @param   moment      relative time point
     * @param   tzid        time zone id for translating to a local duration
     * @return  formatted output of relative time, either in past or in future
     */
    public String print(
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

        Duration<IsoUnit> duration = STD_METRIC.between(start, end);

        if (duration.isEmpty()) {
            return UnitPatterns.of(this.language).getNowWord();
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

        String num =
            NumberFormat.getIntegerInstance(this.language).format(amount);
        return pattern.replace("{0}", num);

    }

    private String getPastPattern(
        long amount,
        CalendarUnit unit
    ) {

        UnitPatterns patterns = UnitPatterns.of(this.language);
        PluralCategory category = this.getCategory(amount);

        switch (unit) {
            case YEARS:
                return patterns.getPastYears(category);
            case MONTHS:
                return patterns.getPastMonths(category);
            case WEEKS:
                return patterns.getPastWeeks(category);
            case DAYS:
                return patterns.getPastDays(category);
            default:
                throw new UnsupportedOperationException(unit.name());
        }

    }

    private String getFuturePattern(
        long amount,
        CalendarUnit unit
    ) {

        UnitPatterns patterns = UnitPatterns.of(this.language);
        PluralCategory category = this.getCategory(amount);

        switch (unit) {
            case YEARS:
                return patterns.getFutureYears(category);
            case MONTHS:
                return patterns.getFutureMonths(category);
            case WEEKS:
                return patterns.getFutureWeeks(category);
            case DAYS:
                return patterns.getFutureDays(category);
            default:
                throw new UnsupportedOperationException(unit.name());
        }

    }

    private String getPastPattern(
        long amount,
        ClockUnit unit
    ) {

        UnitPatterns patterns = UnitPatterns.of(this.language);
        PluralCategory category = this.getCategory(amount);

        switch (unit) {
            case HOURS:
                return patterns.getPastHours(category);
            case MINUTES:
                return patterns.getPastMinutes(category);
            case SECONDS:
                return patterns.getPastSeconds(category);
            default:
                throw new UnsupportedOperationException(unit.name());
        }

    }

    private String getFuturePattern(
        long amount,
        ClockUnit unit
    ) {

        UnitPatterns patterns = UnitPatterns.of(this.language);
        PluralCategory category = this.getCategory(amount);

        switch (unit) {
            case HOURS:
                return patterns.getFutureHours(category);
            case MINUTES:
                return patterns.getFutureMinutes(category);
            case SECONDS:
                return patterns.getFutureSeconds(category);
            default:
                throw new UnsupportedOperationException(unit.name());
        }

    }

    private PluralCategory getCategory(long amount) {

        return this.rules.getCategory(amount);

    }

}
