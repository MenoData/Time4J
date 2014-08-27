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
import net.time4j.base.UnixTime;
import net.time4j.format.PluralCategory;
import net.time4j.format.PluralRules;
import net.time4j.format.TextWidth;
import net.time4j.format.UnitPatterns;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


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

    //~ Instanzvariablen --------------------------------------------------

    private final PluralRules rules;
    private final Locale language;
    private final Moment reference;

    //~ Konstruktoren -----------------------------------------------------

    private PrettyTime(
        Locale language,
        Moment reference
    ) {
        super();

        this.rules = PluralRules.of(language); // throws NPE if locale == null
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
     * <p>Yields the reference time point for formatting of relative times. </p>
     *
     * @return  reference time or current system time if not yet specified
     * @see     #withReference(UnixTime)
     * @see     #print(UnixTime)
     */
    /*[deutsch]
     * <p>Liefert die Bezugszeit f&uuml;r formatierte Ausgaben der relativen
     * Zeit. </p>
     *
     * @return  Bezugszeit oder die aktuelle Systemzeit, wenn die Bezugszeit
     *          noch nicht angegeben wurde
     * @see     #withReference(UnixTime)
     * @see     #print(UnixTime)
     */
    public Moment getReference() {

        if (this.reference == null) {
            return SystemClock.INSTANCE.currentTime();
        }

        return this.reference;

    }

    /**
     * <p>Yields a changed copy of this instance with given reference time. </p>
     *
     * <p>If given reference timestamp is {@code null} then the reference time
     * will always be the current system time. </p>
     *
     * @param   moment  new reference time (maybe {@code null})
     * @return  new instance of {@code PrettyTime} with changed reference time
     * @see     #getReference()
     * @see     #print(UnixTime)
     */
    /*[deutsch]
     * <p>Legt die Bezugszeit f&uuml;r relative Zeitangaben neu fest. </p>
     *
     * <p>Wenn die angegebene Bezugszeit {@code null} ist, dann wird sie
     * immer auf die aktuelle Systemzeit gesetzt. </p>
     *
     * @param   moment  new reference time (maybe {@code null})
     * @return  new instance of {@code PrettyTime} with changed reference time
     * @see     #getReference()
     * @see     #print(UnixTime)
     */
    public PrettyTime withReference(UnixTime moment) {

        if (moment == null) {
            return PrettyTime.of(this.language);
        }

        return new PrettyTime(this.language, Moment.from(moment));

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

        String num = NumberFormat.getIntegerInstance(language).format(amount);
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

        String num = NumberFormat.getIntegerInstance(language).format(amount);
        return pattern.replace("{0}", num);

    }

    /**
     * <p>Formats given duration. </p>
     *
     * @param   duration    object representing a duration which might contain
     *                      several units and quantities
     * @return  formatted output
     */
    /*[deutsch]
     * <p>Formatiert die angegebene Dauer. </p>
     *
     * @param   duration    Dauer-Objekt
     * @return  formatierte Ausgabe
     */
    public String print(Duration<?> duration) {

        throw new UnsupportedOperationException("Not yet implemented.");

    }

    /**
     * <p>Formats given time point relative to {@link #getReference()}. </p>
     *
     * @param   moment      relative time point
     * @return  formatted output of relative time, either in past or in future
     */
    /*[deutsch]
     * <p>Formatiert den angegebenen Zeitpunkt relativ zu
     * {@link #getReference()}. </p>
     *
     * @param   moment      relative time point
     * @return  formatted output of relative time, either in past or in future
     */
    public String print(UnixTime moment) {

        throw new UnsupportedOperationException("Not yet implemented.");

    }

    private PluralCategory getCategory(long amount) {

        return this.rules.getCategory(amount);

    }

}
