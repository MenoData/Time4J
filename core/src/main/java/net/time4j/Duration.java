/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Duration.java) is part of project Time4J.
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
import net.time4j.engine.AbstractDuration;
import net.time4j.engine.AbstractMetric;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.ChronoUnit;
import net.time4j.engine.Normalizer;
import net.time4j.engine.TimeMetric;
import net.time4j.engine.TimePoint;
import net.time4j.engine.TimeSpan;
import net.time4j.format.TimeSpanFormatter;
import net.time4j.tz.Timezone;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.time4j.CalendarUnit.*;
import static net.time4j.ClockUnit.*;


/**
 * <p>ISO-8601-compatible duration between two time points. </p>
 *
 * <p>Instances can be created by following factory methods: </p>
 *
 * <ul>
 *  <li>{@link #of(long, IsoUnit) of(long, U)}</li>
 *  <li>{@link #ofCalendarUnits(int, int, int)}</li>
 *  <li>{@link #ofClockUnits(int, int, int)}</li>
 *  <li>{@link #ofPositive()} (<i>builder</i>-pattern)</li>
 *  <li>{@link #ofNegative()} (<i>builder</i>-pattern)</li>
 *  <li>{@link #parsePeriod(String)}</li>
 *  <li>{@link #parseCalendarPeriod(String)}</li>
 *  <li>{@link #parseClockPeriod(String)}</li>
 *  <li>{@link #parseWeekBasedPeriod(String)}</li>
 * </ul>
 *
 * <p>All instances are <i>immutable</i>, but changed copies can be created
 * by using the methods {@code plus()}, {@code with()}, {@code union()},
 * {@code multipliedBy()}, {@code abs()} and {@code inverse()}. The time
 * units {@code ClockUnit.MILLIS} and {@code ClockUnit.MICROS} will
 * automatically normalized to nanoseconds. In every other case a normalization
 * must be  explicitly triggered by {@code with(Normalizer)}. </p>
 *
 * <p>Note: The definition of an optional negative sign is not part of
 * ISO-8061, but part of the XML-schema-specification and defines the
 * position of two time points relative to each other. A manipulation of
 * the sign is possible with the method {@code inverse()}. </p>
 *
 * <p>The time arithmetic handles the addition of a duration to a time point
 * and the subtraction of a duration from a time point as dependent on the
 * sign of the duration as described in the
 * <a href="engine/AbstractDuration.html#algorithm">standard algorithm</a>
 * of the super class. </p>
 *
 * @param       <U> generic type of time units
 * @author      Meno Hochschild
 */
/*[deutsch]
 * <p>ISO-konforme Zeitspanne zwischen zwei Zeitpunkten. </p>
 *
 * <p>Instanzen k&ouml;nnen &uuml;ber folgende Fabrikmethoden erzeugt
 * werden: </p>
 *
 * <ul>
 *  <li>{@link #of(long, IsoUnit) of(long, U)}</li>
 *  <li>{@link #ofCalendarUnits(int, int, int)}</li>
 *  <li>{@link #ofClockUnits(int, int, int)}</li>
 *  <li>{@link #ofPositive()} (<i>builder</i>-Muster)</li>
 *  <li>{@link #ofNegative()} (<i>builder</i>-Muster)</li>
 *  <li>{@link #parsePeriod(String)}</li>
 *  <li>{@link #parseCalendarPeriod(String)}</li>
 *  <li>{@link #parseClockPeriod(String)}</li>
 *  <li>{@link #parseWeekBasedPeriod(String)}</li>
 * </ul>
 *
 * <p>Alle Instanzen sind <i>immutable</i>, aber ge&auml;nderte Kopien lassen
 * sich &uuml;ber die Methoden {@code plus()}, {@code with()}, {@code union()},
 * {@code multipliedBy()}, {@code abs()} und {@code inverse()} erzeugen.
 * Hierbei werden die Zeiteinheiten {@code ClockUnit.MILLIS} und
 * {@code ClockUnit.MICROS} intern immer zu Nanosekunden normalisiert.
 * Ansonsten mu&szlig; eine Normalisierung explizit mittels
 * {@code with(Normalizer)} angesto&szlig;en werden. </p>
 *
 * <p>Notiz: Die Definition eines optionalen negativen Vorzeichens ist streng
 * genommen nicht Bestandteil des ISO-Standards, ist aber Bestandteil der
 * XML-Schema-Spezifikation und legt die Lage zweier Zeitpunkte relativ
 * zueinander fest. Eine Manipulation des Vorzeichens ist mit der Methode
 * {@code inverse()} m&ouml;glich. </p>
 *
 * <p>Die Zeitarithmetik behandelt die Addition und Subtraktion einer Zeitspanne
 * bezogen auf einen Zeitpunkt abh&auml;ngig vom Vorzeichen der Zeitspanne wie
 * im <a href="engine/AbstractDuration.html#algorithm">Standardalgorithmus</a>
 * von Time4J beschrieben. </p>
 *
 * @param       <U> generic type of time units
 * @author      Meno Hochschild
 */
public final class Duration<U extends IsoUnit>
    extends AbstractDuration<U>
    implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final char ISO_DECIMAL_SEPARATOR = (
        Boolean.getBoolean("net.time4j.format.iso.decimal.dot")
        ? '.'
        : ',' // Empfehlung des ISO-Standards
    );

    private static final long MRD = 1000000000L;
    private static final long MIO = 1000000L;

    @SuppressWarnings("rawtypes")
    private static final Duration ZERO = new Duration();

    private static final Duration.Formatter<CalendarUnit> CF_EXT_CAL =
        createAlternativeDateFormat(true, false);
    private static final Duration.Formatter<CalendarUnit> CF_EXT_ORD =
        createAlternativeDateFormat(true, true);
    private static final Duration.Formatter<CalendarUnit> CF_BAS_CAL =
        createAlternativeDateFormat(false, false);
    private static final Duration.Formatter<CalendarUnit> CF_BAS_ORD =
        createAlternativeDateFormat(false, true);
    private static final Duration.Formatter<ClockUnit> TF_EXT =
        createAlternativeTimeFormat(true);
    private static final Duration.Formatter<ClockUnit> TF_BAS =
        createAlternativeTimeFormat(false);

    private static final Comparator<Item<? extends ChronoUnit>> ITEM_COMPARATOR = StdNormalizer.comparator();

    /**
     * <p>Normalizes the duration items on the base of
     * {@code 1 year = 12 months} and {@code 1 day = 24 hours} and
     * {@code 1 hour = 60 minutes} and {@code 1 minute = 60 seconds} -
     * without converting days to months. </p>
     *
     * <p>Attention: Timezone-dependent changes of length of day or
     * leapseconds are ignored. That is why this normalization should
     * only be applied on ISO-timestamps without timezone reference.
     * Only time units of enum types {@link CalendarUnit} and
     * {@link ClockUnit} will be normalized. </p>
     *
     * <p>Weeks will be normalized to days if weeks do not represent
     * the only calendrical duration items. </p>
     *
     * @see     PlainTimestamp#normalize(TimeSpan)
     */
    /*[deutsch]
     * <p>Normalisiert die Zeitspannenelemente einer Zeitspanne auf der Basis
     * {@code 1 Jahr = 12 Monate} und {@code 1 Tag = 24 Stunden} und
     * {@code 1 Stunde = 60 Minuten} und {@code 1 Minute = 60 Sekunden},
     * jedoch ohne die Tage zu Monaten zu konvertieren. </p>
     *
     * <p>VORSICHT: Zeitzonenbedingte Ver&auml;nderungen der Tagesl&auml;nge
     * oder Schaltsekunden werden hier ignoriert. Deshalb sollte diese
     * Normalisierung m&ouml;glichst nur auf ISO-Zeitstempel ohne Zeitzonen-
     * oder UTC-Unterst&uuml;tzung angewandt werden. Nur Zeiteinheiten der
     * Enums {@link CalendarUnit} und {@link ClockUnit} k&ouml;nnen normalisiert
     * werden. </p>
     *
     * <p>Wochen werden genau dann zu Tagen konvertiert, wenn sie nicht das
     * einzige datumsbezogene Zeitspannenelement darstellen. </p>
     *
     * @see     PlainTimestamp#normalize(TimeSpan)
     */
    public static Normalizer<IsoUnit> STD_PERIOD = StdNormalizer.ofMixedUnits();

    /**
     * <p>Normalizes the calendrical items of a duration on the base
     * {@code 1 year = 12 months} - without converting the days to months. </p>
     *
     * <p>Weeks will be normalized to days if weeks do not represent
     * the only calendrical duration items. Only time units of type
     * {@link CalendarUnit} will be normalized. </p>
     *
     * @see     PlainDate#normalize(TimeSpan)
     */
    /*[deutsch]
     * <p>Normalisiert die Datumselemente einer Zeitspanne auf der Basis
     * {@code 1 Jahr = 12 Monate}, jedoch ohne die Tage zu Monaten zu
     * konvertieren. </p>
     *
     * <p>Wochen werden genau dann zu Tagen konvertiert, wenn sie nicht das
     * einzige datumsbezogene Zeitspannenelement darstellen. Nur Zeiteinheiten
     * des Enums {@link CalendarUnit} werden normalisiert. </p>
     *
     * @see     PlainDate#normalize(TimeSpan)
     */
    public static Normalizer<CalendarUnit> STD_CALENDAR_PERIOD = StdNormalizer.ofCalendarUnits();

    /**
     * <p>Normalizes the wall time items of a duration on the base
     * {@code 1 day = 24 hours} und {@code 1 hour = 60 minutes} and
     * {@code 1 minute = 60 seconds}. </p>
     *
     * <p>Attention: Timezone-dependent changes of length of day or
     * leapseconds are ignored. That is why this normalization should
     * only be applied on ISO-timestamps without timezone reference.
     * Only time units of enum type {@link ClockUnit} will be normalized. </p>
     *
     * @see     PlainTime
     */
    /*[deutsch]
     * <p>Normalisiert die Uhrzeitelemente einer Zeitspanne auf der Basis
     * {@code 1 Tag = 24 Stunden} und {@code 1 Stunde = 60 Minuten} und
     * {@code 1 Minute = 60 Sekunden}. </p>
     *
     * <p>VORSICHT: Zeitzonenbedingte Ver&auml;nderungen der Tagesl&auml;nge
     * oder UTC-Schaltsekunden werden hier ignoriert. Deshalb sollte diese
     * Normalisierung nicht auf Zeitzonen- oder UTC-sensible Zeitpunkttypen
     * angewandt werden. Nur Zeiteinheiten des Enums {@link ClockUnit}
     * werden normalisiert. </p>
     *
     * @see     PlainTime
     */
    public static Normalizer<ClockUnit> STD_CLOCK_PERIOD = StdNormalizer.ofClockUnits();

    private static final int PRINT_STYLE_NORMAL = 0;
    private static final int PRINT_STYLE_ISO = 1;
    private static final int PRINT_STYLE_XML = 2;
    private static final long serialVersionUID = -6321211763598951499L;

    private static final TimeMetric<CalendarUnit, Duration<CalendarUnit>> YMD_METRIC =
        Duration.in(YEARS, MONTHS, DAYS);
    private static final TimeMetric<ClockUnit, Duration<ClockUnit>> CLOCK_METRIC =
        Duration.in(HOURS, MINUTES, SECONDS, NANOS);
    private static final TimeMetric<IsoDateUnit, Duration<IsoDateUnit>> WEEK_BASED_METRIC =
        Duration.in(CalendarUnit.weekBasedYears(), WEEKS, DAYS);

    private static final int SUPER_TYPE = -1;
    private static final int CALENDAR_TYPE = 0;
    private static final int CLOCK_TYPE = 1;
    private static final int WEEK_BASED_TYPE = 2;

    //~ Instanzvariablen --------------------------------------------------

    private transient final List<Item<U>> items;
    private transient final boolean negative;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * Standard-Konstruktor.
     *
     * @param   items           list of duration items
     * @param   negative        negative duration indicated?
     * @throws  IllegalArgumentException if different units of same length exist
     */
    Duration(
        List<Item<U>> items,
        boolean negative
    ) {
        super();

        boolean empty = items.isEmpty();

        if (empty) {
            this.items = Collections.emptyList();
        } else {
            Collections.sort(items, ITEM_COMPARATOR);
            this.items = Collections.unmodifiableList(items);
        }

        this.negative = (!empty && negative);

    }

    // Kopiekonstruktor (siehe inverse())
    private Duration(
        Duration<U> duration,
        boolean inverse
    ) {
        super();

        this.items = duration.items;
        this.negative = (inverse ? !duration.negative : duration.negative);

    }

    // leere Zeitspanne
    private Duration() {
        super();

        this.items = Collections.emptyList();
        this.negative = false;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Gets an empty duration without units. </p>
     *
     * <p>This method can also be used for generic upcasting of the
     * type U to {@code IsoUnit}: </p>
     *
     * <pre>
     * 	Duration&lt;CalendarUnit&gt; dur = Duration.ofCalendarUnits(2, 5, 30);
     * 	Duration&lt;IsoUnit&gt; upcasted = Duration.ofZero().plus(dur);
     * </pre>
     *
     * @param   <U> generic unit type
     * @return  empty duration
     */
    /*[deutsch]
     * <p>Liefert eine leere Zeitspanne ohne Einheiten. </p>
     *
     * <p>Diese Methode kann auch daf&uuml;r benutzt werden, den generischen
     * Typparameter U zu {@code IsoUnit} zu &auml;ndern: </p>
     *
     * <pre>
     * 	Duration&lt;CalendarUnit&gt; dur = Duration.ofCalendarUnits(2, 5, 30);
     * 	Duration&lt;IsoUnit&gt; upcasted = Duration.ofZero().plus(dur);
     * </pre>
     *
     * @param   <U> generic unit type
     * @return  empty duration
     */
    @SuppressWarnings("unchecked")
    public static <U extends IsoUnit> Duration<U> ofZero() {

        return ZERO;

    }

    /**
     * <p>Creates a new duration which only knows one unit. </p>
     *
     * <p>Is the given amount is negative then the duration will be
     * negative, too. Is the amount equal to {@code 0} then an empty
     * duration will be returned. Milliseconds or microseconds will
     * be automatically normalized to nanoseconds. </p>
     *
     * @param   <U> generic unit type
     * @param   amount      amount as count of units
     * @param   unit        single time unit
     * @return  new duration
     */
    /*[deutsch]
     * <p>Erzeugt eine neue Zeitspanne, die auf nur einer Zeiteinheit
     * beruht. </p>
     *
     * <p>Ist der angegebene Betrag negativ, so wird auch die Zeitspanne
     * negativ sein. Ist er {@code 0}, wird eine leere Zeitspanne
     * zur&uuml;ckgegeben. Millisekunden oder Mikrosekunden werden
     * automatisch zu Nanosekunden normalisiert. </p>
     *
     * @param   <U> generic unit type
     * @param   amount      amount as count of units
     * @param   unit        single time unit
     * @return  new duration
     */
    public static <U extends IsoUnit> Duration<U> of(
        long amount,
        U unit
    ) {

        if (amount == 0) {
            return ofZero();
        }

        U u = unit;
        long value = amount;

        if (amount < 0) {
            value = MathUtils.safeNegate(amount);
        }

        if (unit instanceof ClockUnit) {
	        switch (unit.getSymbol()) {
	            case '3':
	                u = cast(ClockUnit.NANOS);
	                value = MathUtils.safeMultiply(value, MIO);
	                break;
	            case '6':
	                u = cast(ClockUnit.NANOS);
	                value = MathUtils.safeMultiply(value, 1000);
	                break;
	            default:
	                // no-op
	        }
        }

        List<Item<U>> items = new ArrayList<Item<U>>(1);
        items.add(Item.of(value, u));
        return new Duration<U>(items, (amount < 0));

    }

    /**
     * <p>Konstructs a new positive duration for combined date- and time items
     * by applying the builder pattern. </p>
     *
     * @return  help object for building a positive {@code Duration}
     */
    /*[deutsch]
     * <p>Konstruiert &uuml;ber den Umweg des <i>builder</i>-Entwurfsmusters
     * eine neue ISO-konforme positive Zeitspanne für kombinierte Datums- und
     * Uhrzeiteinheiten. </p>
     *
     * @return  help object for building a positive {@code Duration}
     */
    public static Builder ofPositive() {

        return new Builder(false);

    }

    /**
     * <p>Konstructs a new negative duration for combined date- and time items
     * by applying the builder pattern. </p>
     *
     * @return  help object for building a negative {@code Duration}
     */
    /*[deutsch]
     * <p>Konstruiert &uuml;ber den Umweg des <i>builder</i>-Entwurfsmusters
     * eine neue ISO-konforme negative Zeitspanne für kombinierte Datums- und
     * Uhrzeiteinheiten. </p>
     *
     * @return  help object for building a negative {@code Duration}
     */
    public static Builder ofNegative() {

        return new Builder(true);

    }

    /**
     * <p>Creates a positive duration in years, months and days. </p>
     *
     * <p>All arguments must not be negative. Is any argument equal to
     * {@code 0} then it will be ignored. If a negative duration is needed
     * then an application can simply call {@code inverse()} on the result. </p>
     *
     * @param   years       amount in years
     * @param   months      amount in months
     * @param   days        amount in days
     * @return  new duration
     * @throws  IllegalArgumentException if any argument is negative
     * @see     #inverse()
     */
    /*[deutsch]
     * <p>Erzeugt eine positive Zeitspanne in Jahren, Monaten und Tagen. </p>
     *
     * <p>Alle Argumente d&uuml;rfen nicht negativ sein. Ist ein Argument
     * gleich {@code 0}, wird es ignoriert. Wird eine negative Zeitspanne
     * gew&uuml;nscht, kann auf dem Ergebnis einfach {@code inverse()}
     * aufgerufen werden. </p>
     *
     * @param   years       amount in years
     * @param   months      amount in months
     * @param   days        amount in days
     * @return  new duration
     * @throws  IllegalArgumentException if any argument is negative
     * @see     #inverse()
     */
    public static Duration<CalendarUnit> ofCalendarUnits(
        int years,
        int months,
        int days
    ) {

        return Duration.ofCalendarUnits(years, months, days, false);

    }

    /**
     * <p>Creates a positive duratioon in hours, minutes and seconds. </p>
     *
     * <p>All arguments must not be negative. Is any argument equal to
     * {@code 0} then it will be ignored. If a negative duration is needed
     * then an application can simply call {@code inverse()} on the result. </p>
     *
     * @param   hours       amount in hours
     * @param   minutes     amount in minutes
     * @param   seconds     amount in seconds
     * @return  new duration
     * @throws  IllegalArgumentException if any argument is negative
     * @see     #inverse()
     */
    /*[deutsch]
     * <p>Erzeugt eine positive Zeitspanne in Stunden, Minuten und
     * Sekunden. </p>
     *
     * <p>Alle Argumente d&uuml;rfen nicht negativ sein. Ist ein Argument
     * gleich {@code 0}, wird es ignoriert. Wird eine negative Zeitspanne
     * gew&uuml;nscht, kann auf dem Ergebnis einfach {@code inverse()}
     * aufgerufen werden. </p>
     *
     * @param   hours       amount in hours
     * @param   minutes     amount in minutes
     * @param   seconds     amount in seconds
     * @return  new duration
     * @throws  IllegalArgumentException if any argument is negative
     * @see     #inverse()
     */
    public static Duration<ClockUnit> ofClockUnits(
        int hours,
        int minutes,
        int seconds
    ) {

        return Duration.ofClockUnits(hours, minutes, seconds, 0, false);

    }

    /**
     * <p>Constructs a metric for any kind of standard units in
     * normalized form. </p>
     *
     * <p><strong>Important:</strong> If the smallest unit is missing which
     * fits the precision of timepoints to be compared then a remainder of
     * subtraction will exist. The result of distance calculation will not
     * express the full temporal distance in this case. For the completeness
     * of calculation, the day unit is required if the distance between
     * two dates is needed. </p>
     *
     * <p><strong>Example with different unit types:</strong> If this method
     * is called with different unit types then it is strongly recommended to
     * first assign the units to static constants of type {@code IsoUnit} in
     * order to avoid compiler problems with generics. This practice also
     * helps to improve the readability of code. </p>
     *
     * <pre>
     *  private static final IsoUnit DAYS = CalendarUnit.DAYS;
     *  private static final IsoUnit HOURS = ClockUnit.HOURS;
     *  private static final IsoUnit MINUTES = ClockUnit.MINUTES;
     *
     *  PlainTimestamp start = PlainTimestamp.of(2014, 3, 28, 0, 30);
     *  PlainTimestamp end = PlainTimestamp.of(2014, 4, 5, 14, 15);
     *  Duration&lt;IsoUnit&gt; duration =
     *      Duration.in(DAYS, HOURS, MINUTES).between(start, end);
     *  System.out.println(duration); // output: P8DT13H45M
     * </pre>
     *
     * @param   <U> generic unit type
     * @param   units       time units to be used in calculation
     * @return  immutable metric for calculating a duration in given units
     * @throws  IllegalArgumentException if no time unit is given or
     *          if there are unit duplicates
     */
    /*[deutsch]
     * <p>Konstruiert eine Metrik f&uuml;r beliebige Standard-Zeiteinheiten
     * in normalisierter Form. </p>
     *
     * <p><strong>Wichtig:</strong> Fehlt die der Pr&auml;zision der zu
     * vergleichenden Zeitpunkte entsprechende kleinste Zeiteinheit, wird
     * im allgemeinen ein Subtraktionsrest &uuml;brigbleiben. Das Ergebnis
     * der Metrikberechnung wird dann nicht den vollst&auml;ndigen zeitlichen
     * Abstand zwischen den Zeitpunkten ausdr&uuml;cken. F&uuml;r die
     * Vollst&auml;ndigkeit der Berechnung ist bei Datumsangaben mindestens
     * die explizite Angabe der Tageseinheit notwendig. </p>
     *
     * <p><strong>Beispiel mit verschiedenen Einheitstypen:</strong> Wenn diese
     * Methode mit verschiedenen Zeiteinheitstypen aufgerufen wird, dann wird
     * dringend empfohlen, zuerst die Einheiten statischen Konstanten vom Typ
     * {@code IsoUnit} zuzuweisen, um Compiler-Probleme mit Generics zu
     * vermeiden. Diese Praxis hilft auch, die Lesbarkeit des Code zu
     * verbessern. </p>
     *
     * <pre>
     *  private static final IsoUnit DAYS = CalendarUnit.DAYS;
     *  private static final IsoUnit HOURS = ClockUnit.HOURS;
     *  private static final IsoUnit MINUTES = ClockUnit.MINUTES;
     *
     *  PlainTimestamp start = PlainTimestamp.of(2014, 3, 28, 0, 30);
     *  PlainTimestamp end = PlainTimestamp.of(2014, 4, 5, 14, 15);
     *  Duration&lt;IsoUnit&gt; duration =
     *      Duration.in(DAYS, HOURS, MINUTES).between(start, end);
     *  System.out.println(duration); // output: P8DT13H45M
     * </pre>
     *
     * @param   <U> generic unit type
     * @param   units       time units to be used in calculation
     * @return  immutable metric for calculating a duration in given units
     * @throws  IllegalArgumentException if no time unit is given or
     *          if there are unit duplicates
     */
    public static <U extends IsoUnit> TimeMetric<U, Duration<U>> in(U... units) {

        return new Metric<U>(units);

    }

    /**
     * <p>Constructs a metric in years, months and days. </p>
     *
     * <p>Finally the resulting duration will be normalized such that
     * smaller units will be converted to bigger units if possible. </p>
     *
     * @return  immutable metric for calculating a duration in years,
     *          months and days
     * @see     #in(IsoUnit[]) in(U[])
     * @see     CalendarUnit#YEARS
     * @see     CalendarUnit#MONTHS
     * @see     CalendarUnit#DAYS
     */
    /*[deutsch]
     * <p>Konstruiert eine Metrik in Jahren, Monaten und Tagen. </p>
     *
     * <p>Am Ende wird die Darstellung automatisch normalisiert, also kleine
     * Zeiteinheiten so weit wie m&ouml;glich in gro&szlig;e Einheiten
     * umgerechnet. </p>
     *
     * @return  immutable metric for calculating a duration in years,
     *          months and days
     * @see     #in(IsoUnit[]) in(U[])
     * @see     CalendarUnit#YEARS
     * @see     CalendarUnit#MONTHS
     * @see     CalendarUnit#DAYS
     */
    public static TimeMetric<CalendarUnit, Duration<CalendarUnit>> inYearsMonthsDays() {

        return YMD_METRIC;

    }

    /**
     * <p>Constructs a metric in hours, minutes, seconds and nanoseconds. </p>
     *
     * <p>Finally the resulting duration will be normalized such that
     * smaller units will be converted to bigger units if possible. </p>
     *
     * @return  immutable metric for calculating a duration in clock units
     * @see     #in(IsoUnit[]) in(U[])
     * @see     ClockUnit#HOURS
     * @see     ClockUnit#MINUTES
     * @see     ClockUnit#SECONDS
     * @see     ClockUnit#NANOS
     */
    /*[deutsch]
     * <p>Konstruiert eine Metrik in Stunden, Minuten, Sekunden und Nanos. </p>
     *
     * <p>Am Ende wird die Darstellung automatisch normalisiert, also kleine
     * Zeiteinheiten so weit wie m&ouml;glich in gro&szlig;e Einheiten
     * umgerechnet. </p>
     *
     * @return  immutable metric for calculating a duration in clock units
     * @see     #in(IsoUnit[]) in(U[])
     * @see     ClockUnit#HOURS
     * @see     ClockUnit#MINUTES
     * @see     ClockUnit#SECONDS
     * @see     ClockUnit#NANOS
     */
    public static TimeMetric<ClockUnit, Duration<ClockUnit>> inClockUnits() {

        return CLOCK_METRIC;

    }

    /**
     * <p>Constructs a metric in week-based years, weeks and days. </p>
     *
     * <p>Finally the resulting duration will be normalized such that
     * smaller units will be converted to bigger units if possible. </p>
     *
     * @return  immutable metric for calculating a duration in week-based years, weeks and days
     * @see     #in(IsoUnit[]) in(U[])
     * @see     CalendarUnit#weekBasedYears()
     * @see     CalendarUnit#WEEKS
     * @see     CalendarUnit#DAYS
     * @since   3.21/4.17
     */
    /*[deutsch]
     * <p>Konstruiert eine Metrik in wochen-basierten Jahren, Wochen und Tagen. </p>
     *
     * <p>Am Ende wird die Darstellung automatisch normalisiert, also kleine
     * Zeiteinheiten so weit wie m&ouml;glich in gro&szlig;e Einheiten umgerechnet. </p>
     *
     * @return  immutable metric for calculating a duration in week-based years, weeks and days
     * @see     #in(IsoUnit[]) in(U[])
     * @see     CalendarUnit#weekBasedYears()
     * @see     CalendarUnit#WEEKS
     * @see     CalendarUnit#DAYS
     * @since   3.21/4.17
     */
    public static TimeMetric<IsoDateUnit, Duration<IsoDateUnit>> inWeekBasedUnits() {

        return WEEK_BASED_METRIC;

    }

    /**
     * <p>Helps to evaluate the zonal duration between two timestamps
     * and applies an offset correction if necessary. </p>
     *
     * <p>Following example handles the change from winter time to summer time
     * in Germany causing 4 instead of 5 hours difference: </p>
     *
     * <pre>
     *  PlainTimestamp start = PlainTimestamp.of(2014, 3, 30, 0, 0);
     *  PlainTimestamp end = PlainTimestamp.of(2014, 3, 30, 5, 0);
     *  IsoUnit hours = ClockUnit.HOURS;
     *  System.out.println(
     *      Duration.in(Timezone.of(&quot;Europe/Berlin&quot;), hours)
     *          .between(start, end).toString()); // output: PT4H
     * </pre>
     *
     * @param   tz          timezone
     * @param   units       time units to be used in calculation
     * @return  zonal metric for calculating a duration in given units
     * @throws  IllegalArgumentException if no time unit is given or
     *          if there are unit duplicates
     * @since   1.2
     */
    /*[deutsch]
     * <p>Hilfsmethode zur Bestimmung der lokalen beziehungsweise zonalen
     * Dauer zwischen zwei Zeitstempeln, die bei Bedarf eine Offset-Korrektur
     * anwendet. </p>
     *
     * <p>Das folgende Beispiel behandelt den Wechsel von der Winterzeit zur
     * Sommerzeit in Deutschland, so da&szlig; nur 4 statt 5 Stunden Differenz
     * errechnet werden: </p>
     *
     * <pre>
     *  PlainTimestamp start = PlainTimestamp.of(2014, 3, 30, 0, 0);
     *  PlainTimestamp end = PlainTimestamp.of(2014, 3, 30, 5, 0);
     *  IsoUnit hours = ClockUnit.HOURS;
     *  System.out.println(
     *      Duration.in(Timezone.of(&quot;Europe/Berlin&quot;), hours)
     *          .between(start, end).toString()); // Ausgabe: PT4H
     * </pre>
     *
     * @param   tz          timezone
     * @param   units       time units to be used in calculation
     * @return  zonal metric for calculating a duration in given units
     * @throws  IllegalArgumentException if no time unit is given or
     *          if there are unit duplicates
     * @since   1.2
     */
    public static TimeMetric<IsoUnit, Duration<IsoUnit>> in(
        Timezone tz,
        IsoUnit... units
    ) {

        return new ZonalMetric(tz, units);

    }

    @Override
    public List<Item<U>> getTotalLength() {

        return this.items;

    }

    @Override
    public boolean isNegative() {

        return this.negative;

    }

    /**
     * <p>Queries if this duration contains given time unit. </p>
     *
     * <p>Any time unit is also part of this duration if it is a fractional
     * part of a second (digit symbol) which is to be converted first. </p>
     *
     * @param   unit    time unit to be checked (optional)
     * @return  {@code true} if this duration contains given unit
     *          else {@code false}
     * @see     #getPartialAmount(IsoUnit) getPartialAmount(U)
     */
    /*[deutsch]
     * <p>Ist die angegebene Zeiteinheit in dieser Zeitspanne enthalten? </p>
     *
     * <p>Eine Zeiteinheit ist auch dann enthalten, wenn sie als
     * Sekundenbruchteil (Ziffer in Symboldarstellung) erst konvertiert
     * werden mu&szlig;. </p>
     *
     * @param   unit    time unit to be checked (optional)
     * @return  {@code true} if this duration contains given unit
     *          else {@code false}
     * @see     #getPartialAmount(IsoUnit) getPartialAmount(U)
     */
    @Override
    public boolean contains(IsoUnit unit) {

        if (unit == null) {
            return false;
        }

        boolean fractional = isFractionUnit(unit);

        for (int i = 0, n = this.items.size(); i < n; i++) {
            Item<U> item = this.items.get(i);
            U u = item.getUnit();

            if (
                u.equals(unit)
                || (fractional && isFractionUnit(u))
            ) {
                return (item.getAmount() > 0);
            }
        }

        return false;

    }

    /**
     * <p>Gets the partial amount associated with given time unit. </p>
     *
     * <p>If this duration does not contain given time unit then this method
     * will yield the value {@code 0}. Fractional parts of seconds which
     * are known by their numerical symbols will automatically be converted.
     * That means if a duration stores nanoseconds, but is queried for
     * microseconds then the nanosecond amount will be multiplied by factor
     * {@code 1000} and finally returned. </p>
     *
     * @param   unit    time unit the amount is queried for (optional)
     * @return  non-negative amount associated with given unit ({@code >= 0})
     */
    /*[deutsch]
     * <p>Liefert den Betrag zu einer Zeiteinheit. </p>
     *
     * <p>Wenn die angegebene Zeiteinheit nicht in der Zeitspanne enthalten ist,
     * liefert die Methode den Wert {@code 0}. Sekundenbruchteile, die an der
     * Symboldarstellung ihrer Einheiten erkennbar sind, werden automatisch
     * konvertiert. Konkret: Wenn eine Zeitspanne z.B. Nanosekunden speichert,
     * aber nach Mikrosekunden gefragt wird, dann wird der in der Zeitspanne
     * enthaltene Nanosekundenwert mit dem Faktor {@code 1000} multipliziert
     * und zur&uuml;ckgegeben. </p>
     *
     * @param   unit    time unit the amount is queried for (optional)
     * @return  non-negative amount associated with given unit ({@code >= 0})
     */
    @Override
    public long getPartialAmount(IsoUnit unit) {

        if (unit == null) {
            return 0;
        }

        boolean fractional = isFractionUnit(unit);

        for (int i = 0, n = this.items.size(); i < n; i++) {
            Item<U> item = this.items.get(i);
            U u = item.getUnit();

            if (u.equals(unit)) {
                return item.getAmount();
            } else if (
                fractional
                && isFractionUnit(u)
            ) {
                int d1 = u.getSymbol() - '0';
                int d2 = unit.getSymbol() - '0';
                int factor = 1;

                for (int j = 0, m = Math.abs(d1 - d2); j < m; j++) {
                    factor *= 10;
                }

                if (d1 >= d2) {
                    return item.getAmount() / factor;
                } else {
                    return item.getAmount() * factor;
                }
            }
        }

        return 0;

    }

    /**
     * <p>Creates a {@code Comparator} which compares durations based on
     * their lengths. </p>
     *
     * <p>Internally, the comparing algorithm uses the expression
     * {@code base.plus(duration1).compareTo(base.plus(duration2))}.
     * The given basis time point is necessary because some durations
     * with flexible units like months have else no fixed length. </p>
     *
     * @param   <U> generic unit type
     * @param   <T> generic type of time point
     * @param   base    base time point which durations will use for comparison
     * @return  {@code Comparator} for plain durations
     * @see     TimePoint#compareTo(TimePoint) TimePoint.compareTo(T)
     */
    /*[deutsch]
     * <p>Liefert ein Hilfsobjekt zum Vergleichen von Zeitspannenobjekten
     * auf Basis ihrer L&auml;nge. </p>
     *
     * <p>Erzeugt einen {@code Comparator}, der letztlich auf dem Ausdruck
     * {@code base.plus(duration1).compareTo(base.plus(duration2))} beruht.
     * Der Basiszeitpunkt ist notwendig, weil sonst Zeitspannenobjekte dieser
     * Klasse nicht notwendig eine physikalisch feste L&auml;nge haben.
     * Zum Beispiel sind Monate variable Zeiteinheiten mit unterschiedlich
     * vielen Tagen. </p>
     *
     * @param   <U> generic unit type
     * @param   <T> generic type of time point
     * @param   base    base time point which durations will use for comparison
     * @return  {@code Comparator} for plain durations
     * @see     TimePoint#compareTo(TimePoint) TimePoint.compareTo(T)
     */
    public static <U extends IsoUnit, T extends TimePoint<? super U, T>>
    Comparator<Duration<U>> comparator(T base) {

        return new LengthComparator<U, T>(base);

    }

    /**
     * <p>Gets a copy of this duration where given amount will be added
     * to the partial amount of this duration in given unit. </p>
     *
     * <p>The method also takes in account the sign of the duration.
     * Example: </p>
     *
     * <pre>
     *  System.out.println(Duration.of(5, MONTHS).plus(-6, MONTHS));
     *  // output: -P1M
     * </pre>
     *
     * <p>Notes: Is the amount to be added equal to {@code 0} then the method
     * will simply yield this duration. Mixed signs are not permitted and will
     * be rejected by an exception. For example following expression is not
     * allowed: </p>
     *
     * <pre>
     *  Duration.of(-1, MONTHS).plus(30, DAYS); // throws IllegalStateException
     * </pre>
     *
     * @param   amount      temporal amount to be added (maybe negative)
     * @param   unit        associated time unit
     * @return  new changed duration while this duration remains unaffected
     * @throws  IllegalStateException if the result gets mixed signs by
     *          adding the partial amounts
     * @throws  IllegalArgumentException if different units of same length exist
     * @throws  ArithmeticException in case of long overflow
     * @see     #with(long, IsoUnit) with(long, U)
     */
    /*[deutsch]
     * <p>Liefert eine Kopie dieser Instanz, in der der angegebene Betrag zum
     * mit der angegebenen Zeiteinheit assoziierten Feldwert addiert wird. </p>
     *
     * <p>Die Methode ber&uuml;cksichtigt auch das Vorzeichen der Zeitspanne.
     * Beispiel: </p>
     *
     * <pre>
     *  System.out.println(Duration.of(5, MONTHS).plus(-6, MONTHS));
     *  // output: -P1M
     * </pre>
     *
     * <p>Notiz: Ist der zu addierende Betrag gleich {@code 0}, liefert die
     * Methode einfach diese Instanz selbst zur&uuml;ck. Gemischte Vorzeichen
     * im Ergebnis sind nicht zul&auml;ssig und werden mit einem Abbruch
     * quittiert: </p>
     *
     * <pre>
     *  Duration.of(-1, MONTHS).plus(30, DAYS); // throws IllegalStateException
     * </pre>
     *
     * @param   amount      temporal amount to be added (maybe negative)
     * @param   unit        associated time unit
     * @return  new changed duration while this duration remains unaffected
     * @throws  IllegalStateException if the result gets mixed signs by
     *          adding the partial amounts
     * @throws  IllegalArgumentException if different units of same length exist
     * @throws  ArithmeticException in case of long overflow
     * @see     #with(long, IsoUnit) with(long, U)
     */
    public Duration<U> plus(
        long amount,
        U unit
    ) {

        if (unit == null) {
            throw new NullPointerException("Missing chronological unit.");
        }

        long originalAmount = amount;
        U originalUnit = unit;
        boolean negatedValue = false;

        if (amount == 0) {
            return this;
        } else if (amount < 0) {
            amount = MathUtils.safeNegate(amount);
            negatedValue = true;
        }

        // Millis und Micros ersetzen
        List<Item<U>> temp = new ArrayList<Item<U>>(this.getTotalLength());
        Item<U> item = replaceFraction(amount, unit);

        if (item != null) {
            amount = item.getAmount();
            unit = item.getUnit();
        }

        if (this.isEmpty()) {
            temp.add((item == null) ? Item.of(amount, unit) : item);
            return new Duration<U>(temp, negatedValue);
        }

        // Items aktualisieren
        int index = this.getIndex(unit);
        boolean resultNegative = this.isNegative();

        if (index < 0) { // Einheit nicht vorhanden
            if (this.isNegative() == negatedValue) {
                temp.add(Item.of(amount, unit));
            } else { // mixed signs possible => last try
                return this.plus(Duration.of(originalAmount, originalUnit));
            }
        } else {
            long sum =
                MathUtils.safeAdd(
                    MathUtils.safeMultiply(
                        temp.get(index).getAmount(),
                        (this.isNegative() ? -1 : 1)
                    ),
                    MathUtils.safeMultiply(
                        amount,
                        (negatedValue ? -1 : 1)
                    )
                );

            if (sum == 0) {
                temp.remove(index);
            } else if (
                (this.count() == 1)
                || (this.isNegative() == (sum < 0))
            ) {
                long absSum = ((sum < 0) ? MathUtils.safeNegate(sum) : sum);
                temp.set(index, Item.of(absSum, unit));
                resultNegative = (sum < 0);
            } else { // mixed signs possible => last try
                return this.plus(Duration.of(originalAmount, originalUnit));
            }
        }

        return new Duration<U>(temp, resultNegative);

    }

    /**
     * <p>Creates a duration as union of this instance and given timespan
     * where partial amounts of equal units will be summed up. </p>
     *
     * <p>In order to sum up timespans with different unit types, following
     * trick can be applied: </p>
     *
     * <pre>
     *  Duration&lt;IsoUnit&gt; zero = Duration.ofZero();
     *  Duration&lt;IsoUnit&gt; result = zero.plus(this).plus(timespan);
     * </pre>
     *
     * <p><strong>Note about sign handling:</strong> If this duration and
     * given timespan have different signs then Time4J tries to apply a
     * normalization in the hope the mixed signs disappear. Otherwise
     * this method will throw an exception in case of mixed signs for
     * different duration items. So it is strongly recommended only to
     * merge durations with equal signs.</p>
     *
     * @param   timespan    other time span this duration will be merged
     *                      with by adding the partial amounts
     * @return  new merged duration
     * @throws  IllegalStateException if the result gets mixed signs by
     *          adding the partial amounts
     * @throws  IllegalArgumentException if different units of same length exist
     * @throws  ArithmeticException in case of long overflow
     * @see     #union(TimeSpan)
     */
    /*[deutsch]
     * <p>Erzeugt eine neue Zeitspanne als Vereinigung dieser und der
     * angegebenen Zeitspanne, wobei Betr&auml;ge zu gleichen Zeiteinheiten
     * addiert werden. </p>
     *
     * <p>Um Zeitspannen mit verschiedenen Einheitstypen zu vereinigen, kann
     * folgender Kniff angewandt werden: </p>
     *
     * <pre>
     *  Duration&lt;IsoUnit&gt; zero = Duration.ofZero();
     *  Duration&lt;IsoUnit&gt; result = zero.plus(this).plus(timespan);
     * </pre>
     *
     * <p><strong>Hinweis zur Vorzeichenbehandlung:</strong> Wenn diese Dauer
     * und die angegebene Zeitspanne verschiedene Vorzeichen haben, wird
     * Time4J bei Bedarf eine automatische Normalisierung durchf&uuml;hren.
     * Sind dann immer noch gemischte Vorzeichen f&uuml;r einzelne
     * Dauerelemente vorhanden, wird eine Ausnahme geworfen. Es wird
     * deshalb empfohlen, nur Zeitspannen mit gleichen
     * Vorzeichen zusammenzuf&uuml;hren.</p>
     *
     * @param   timespan    other time span this duration will be merged
     *                      with by adding the partial amounts
     * @return  new merged duration
     * @throws  IllegalStateException if the result gets mixed signs by
     *          adding the partial amounts
     * @throws  IllegalArgumentException if different units of same length exist
     * @throws  ArithmeticException in case of long overflow
     * @see	    #union(TimeSpan)
     */
    @SuppressWarnings("unchecked")
    public Duration<U> plus(TimeSpan<? extends U> timespan) {

    	Duration<U> result = merge(this, timespan);

    	if (result == null) {
            long[] sums = new long[4];
            sums[0] = 0;
            sums[1] = 0;
            sums[2] = 0;
            sums[3] = 0;

            if (summarize(this, sums) && summarize(timespan, sums)) {
                long months = sums[0];
                long days = sums[1];
                long secs = sums[2];
                long nanos = sums[3];
                long daytime;

                if (nanos != 0) {
                    daytime = nanos;
                } else if (secs != 0) {
                    daytime = secs;
                } else {
                    daytime = days;
                }

                if (!hasMixedSigns(months, daytime)) {
                    boolean neg = ((months < 0) || (daytime < 0));

                    if (neg) {
                        months = MathUtils.safeNegate(months);
                        days = MathUtils.safeNegate(days);
                        secs = MathUtils.safeNegate(secs);
                        nanos = MathUtils.safeNegate(nanos);
                    }

                    long years = months / 12;
                    months = months % 12;
                    long nanosecs = 0;
                    if (nanos != 0) {
                        nanosecs = nanos % MRD;
                        secs = nanos / MRD;
                    }
                    long hours = secs / 3600;
                    secs = secs % 3600;
                    long minutes = secs / 60;
                    secs = secs % 60;

                    Map<IsoUnit, Long> map = new HashMap<IsoUnit, Long>();
                    map.put(YEARS, years);
                    map.put(MONTHS, months);
                    map.put(DAYS, days);
                    map.put(HOURS, hours);
                    map.put(MINUTES, minutes);
                    map.put(SECONDS, secs);
                    map.put(NANOS, nanosecs);
                    return (Duration<U>) Duration.create(map, neg);
                }
            }

            throw new IllegalStateException(
                    "Mixed signs in result time span not allowed: "
                    + this
                    + " PLUS "
                    + timespan);
    	}

    	return result;

    }

    /**
     * <p>Gets a copy of this duration where the partial amount associated
     * with given time unit is changed. </p>
     *
     * <p>Equivalent to {@code plus(amount - getAmount(unit), unit)}. </p>
     *
     * @param   amount      temporal amount to be set (maybe negative)
     * @param   unit        associated time unit
     * @return  new changed duration while this duration remains unaffected
     * @throws  IllegalStateException if the result gets mixed signs by
     *          setting the partial amounts
     * @throws  IllegalArgumentException if different units of same length exist
     * @throws  ArithmeticException in case of long overflow
     * @see     #plus(long, IsoUnit) plus(long, U)
     */
    /*[deutsch]
     * <p>Liefert eine Kopie dieser Instanz mit dem angegebenen ge&auml;nderten
     * Wert. </p>
     *
     * <p>Entspricht {@code plus(amount - getAmount(unit), unit)}. </p>
     *
     * @param   amount      temporal amount to be set (maybe negative)
     * @param   unit        associated time unit
     * @return  new changed duration while this duration remains unaffected
     * @throws  IllegalStateException if the result gets mixed signs by
     *          setting the partial amounts
     * @throws  IllegalArgumentException if different units of same length exist
     * @throws  ArithmeticException in case of long overflow
     * @see     #plus(long, IsoUnit) plus(long, U)
     */
    public Duration<U> with(
        long amount,
        U unit
    ) {

        long absAmount =
            ((amount < 0) ? MathUtils.safeNegate(amount) : amount);
        Item<U> item = replaceFraction(absAmount, unit);

        if (item != null) {
            absAmount = item.getAmount();
            unit = item.getUnit();
        }

        long oldAmount = this.getPartialAmount(unit);

        return this.plus(
            MathUtils.safeSubtract(
                MathUtils.safeMultiply(
                    absAmount,
                    (amount < 0) ? -1 : 1
                ),
                MathUtils.safeMultiply(
                    oldAmount,
                    this.isNegative() ? -1 : 1
                )
            ),
            unit
        );

    }

    /**
     * <p>Gets the absolute always non-negative copy of this duration. </p>
     *
     * <p>Example: </p>
     *
     * <pre>
     *  System.out.println(Duration.of(-5, MONTHS).abs());
     *  // output: P5M
     * </pre>
     *
     * @return  new positive duration if this duration is negative else this
     *          duration unchanged
     * @see     #isNegative()
     * @see     #inverse()
     */
    /*[deutsch]
     * <p>Liefert die absolute immer positive Variante dieser Zeitspanne. </p>
     *
     * <p>Beispiel: </p>
     *
     * <pre>
     *  System.out.println(Duration.of(-5, MONTHS).abs());
     *  // output: P5M
     * </pre>
     *
     * @return  new positive duration if this duration is negative else this
     *          duration unchanged
     * @see     #isNegative()
     * @see     #inverse()
     */
    public Duration<U> abs() {

        if (this.isNegative()) {
            return this.inverse();
        } else {
            return this;
        }

    }

    /**
     * <p>Gets a copy of this duration with reversed sign. </p>
     *
     * <p>A double call of this method will yield an equal duration so
     * following invariant holds: </p>
     *
     * <pre>
     *  System.out.println(this.inverse().inverse().equals(this));
     *  // output: true
     * </pre>
     *
     * <p>For the special case of an empty duration, this method has no
     * effect and just returns the same instance. The method is equivalent
     * to the expression {@code multipliedBy(-1)}. </p>
     *
     * <p>Example: </p>
     *
     * <pre>
     *  System.out.println(Duration.of(-5, MONTHS).inverse());
     *  // output: P5M
     * </pre>
     *
     * @return  new negative duration if this duration is positive else a new
     *          positive duration with the same partial amounts and units
     * @see     #isNegative()
     * @see     #multipliedBy(int)
     */
    /*[deutsch]
     * <p>Liefert eine Kopie dieser Instanz, die das negative &Auml;quivalent
     * darstellt. </p>
     *
     * <p>Ein zweifacher Aufruf dieser Methode liefert wieder eine
     * inhaltlich gleiche Instanz. Also gilt immer folgende Beziehung:
     * {@code this.inverse().inverse().equals(this) == true}. Liegt der
     * Sonderfall einer leeren Zeitspanne vor, dann ist diese Methode ohne
     * Wirkung und liefert nur die gleiche Instanz zur&uuml;ck. Entspricht
     * dem Ausdruck {@code multipliedBy(-1)}. </p>
     *
     * <p>Beispiel: {@code [-P5M].inverse()} wird zu {@code [P5M]}. </p>
     *
     * @return  new negative duration if this duration is positive else a new
     *          positive duration with the same partial amounts and units
     * @see     #isNegative()
     * @see     #multipliedBy(int)
     */
    @Override
    public Duration<U> inverse() {

        return this.multipliedBy(-1);

    }

    /**
     * <p>Multiplies all partial amounts of this duration by given factor. </p>
     *
     * <p>Is the factor equal to {@code 0} then the new duration is empty.
     * If the factor {@code 1} is specified then the method will just yield
     * this instance unaffected. In the case of a negative factor the sign
     * will also be inverted. </p>
     *
     * @param   factor  multiplication factor
     * @return  new duration with all amounts multiplied while this duration
     *          remains unaffected
     * @throws  ArithmeticException in case of long overflow
     */
    /*[deutsch]
     * <p>Multipliziert alle enthaltenen Betr&auml;ge mit dem angegebenen
     * Faktor. </p>
     *
     * <p>Ist der Faktor {@code 0}, ist die neue Zeitspanne leer. Mit dem
     * Faktor {@code 1} wird diese Instanz selbst unver&auml;ndert
     * zur&uuml;ckgegeben. Bei einem negativen Faktor wird zus&auml;tzlich
     * das Vorzeichen ge&auml;ndert. </p>
     *
     * @param   factor  multiplication factor
     * @return  new duration with all amounts multiplied while this duration
     *          remains unaffected
     * @throws  ArithmeticException in case of long overflow
     */
    public Duration<U> multipliedBy(int factor) {

        if (
            this.isEmpty()
            || (factor == 1)
        ) {
            return this;
        } else if (factor == 0) {
            return ofZero();
        } else if (factor == -1) {
            return new Duration<U>(this, true);
        }

        List<Item<U>> newItems = new ArrayList<Item<U>>(this.count());
        int scalar = Math.abs(factor);

        for (int i = 0, n = this.count(); i < n; i++) {
            Item<U> item = this.getTotalLength().get(i);
            newItems.add(
                Item.of(
                    MathUtils.safeMultiply(item.getAmount(), scalar),
                    item.getUnit()
                )
            );
        }

        return new Duration<U>(
            newItems,
            ((factor < 0) ? !this.isNegative() : this.isNegative())
        );

    }

    /**
     * <p>Creates a duration as union of this instance and given timespan
     * where partial amounts of equal units will be summed up. </p>
     *
     * <p>The list result of this method can be used in time arithmetic as
     * follows: </p>
     *
     * <pre>
     *  Duration&lt;CalendarUnit&gt; dateDur =
     *      Duration.ofCalendarUnits(2, 7, 10);
     *  Duration&lt;ClockUnit&gt; timeDur =
     *      Duration.ofClockUnits(0, 30, 0);
     *  PlainTimestamp tsp = PlainTimestamp.of(2014, 1, 1, 0, 0);
     *
     *  for (Duration&lt;?&gt; dur : Duration.ofZero().plus(dateDur).union(timeDur)) {
     *  	tsp = tsp.plus(dur);
     *  }
     *
     *  System.out.println(tsp); // 2016-08-11T00:30
     * </pre>
     *
     * <p>Note that this example will even work in case of mixed signs. No
     * exception will be thrown. Instead this duration and the other one would
     * just be added to the timestamp within a loop - step by step. In contrast
     * to {@code plus(TimeSpan)}, Time4J does not try to normalize the durations
     * in order to produce a unique sign (on best effort base) in case of
     * mixed signs. </p>
     *
     * @param   timespan    other time span this duration is to be merged with
     * @return  unmodifiable list with one new merged duration or two unmerged
     *          durations in case of mixed signs
     * @throws  IllegalArgumentException if different units of same length exist
     * @throws  ArithmeticException in case of long overflow
     * @see	    #plus(TimeSpan)
     */
    /*[deutsch]
     * <p>Erzeugt eine neue Zeitspanne als Vereinigung dieser und der
     * angegebenen Zeitspanne, wobei Betr&auml;ge zu gleichen Zeiteinheiten
     * addiert werden. </p>
     *
     * <p>Das Listenergebnis dieser Methode kann in der Zeitarithmetik wie folgt
     * genutzt werden: </p>
     *
     * <pre>
     *  Duration&lt;CalendarUnit&gt; dateDur =
     *    Duration.ofCalendarUnits(2, 7, 10);
     *  Duration&lt;ClockUnit&gt; timeDur =
     *    Duration.ofClockUnits(0, 30, 0);
     *  PlainTimestamp tsp = PlainTimestamp.of(2014, 1, 1, 0, 0);
     *
     *  for (Duration&lt;?&gt; dur : Duration.ofZero().plus(dateDur).union(timeDur)) {
     *    tsp = tsp.plus(dur);
     *  }
     *
     *  System.out.println(tsp); // 2016-08-11T00:30
     * </pre>
     *
     * <p>Zu beachten: Dieses Beispiel funktioniert sogar, wenn beide
     * Dauer-Objekte wegen gemischter Vorzeichen nicht zusammengef&uuml;hrt
     * werden k&ouml;nnen. Stattdessen werden dann diese Dauer und die
     * angegebene Zeitspanne Schritt f&uuml;r Schritt innerhalb der Schleife
     * zum Zeitstempel aufaddiert. Anders als in {@code plus(TimeSpan)}
     * versucht Time4J hier nicht, im Fall gemischter Vorzeichen mit Hilfe
     * einer Normalisierung ein eindeutiges Vorzeichen herzustellen. </p>
     *
     * @param   timespan    other time span this duration is to be merged with
     * @return  unmodifiable list with one new merged duration or two unmerged
     *          durations in case of mixed signs
     * @throws  IllegalArgumentException if different units of same length exist
     * @throws  ArithmeticException in case of long overflow
     * @see	    #plus(TimeSpan)
     */
	public List<Duration<U>> union(TimeSpan<? extends U> timespan) {

		Duration<U> merged = merge(this, timespan);

		if (merged == null) {
			List<Duration<U>> result = new ArrayList<Duration<U>>();
			result.add(this);
			Duration<U> empty = ofZero();
			Duration<U> other = empty.plus(timespan);
			result.add(other);
			return Collections.unmodifiableList(result);
		}

		return Collections.singletonList(merged);

	}

    /**
     * <p>Creates a composition of a calendar period and a clock period. </p>
     *
     * @param   calendarPeriod  calendrical duration
     * @param   clockPeriod     duration with clock units
     * @return  composite duration
     * @since   3.0
     * @see     #toCalendarPeriod()
     * @see     #toClockPeriod()
     */
    /*[deutsch]
     * <p>Bildet eine aus kalendarischer Dauer und Uhrzeitperiode zusammengesetzte Dauer. </p>
     *
     * @param   calendarPeriod  calendrical duration
     * @param   clockPeriod     duration with clock units
     * @return  composite duration
     * @since   3.0
     * @see     #toCalendarPeriod()
     * @see     #toClockPeriod()
     */
    public static Duration<IsoUnit> compose(
        Duration<CalendarUnit> calendarPeriod,
        Duration<ClockUnit> clockPeriod
    ) {

        Duration<IsoUnit> dur = Duration.ofZero();
        return dur.plus(calendarPeriod).plus(clockPeriod);

    }

    /**
     * <p>Extracts a new duration with all contained calendar units only. </p>
     *
     * <p>The clock time part will be removed. </p>
     *
     * @return  new duration with calendar units only
     * @since   3.0
     * @see     #compose(Duration, Duration)
     * @see     #toClockPeriod()
     */
    /*[deutsch]
     * <p>Extrahiert eine neue Dauer, die nur alle kalendarischen Zeiteinheiten
     * dieser Dauer enth&auml;lt. </p>
     *
     * <p>Der Uhrzeitanteil wird entfernt. </p>
     *
     * @return  new duration with calendar units only
     * @since   3.0
     * @see     #compose(Duration, Duration)
     * @see     #toClockPeriod()
     */
    public Duration<CalendarUnit> toCalendarPeriod() {

        if (this.isEmpty()) {
            return Duration.ofZero();
        }

        List<Item<CalendarUnit>> calItems = new ArrayList<Item<CalendarUnit>>();

        for (Item<U> item : this.items) {
            if (item.getUnit() instanceof CalendarUnit) {
                calItems.add(Item.of(item.getAmount(), CalendarUnit.class.cast(item.getUnit())));
            }
        }

        if (calItems.isEmpty()) {
            return Duration.ofZero();
        }

        return new Duration<CalendarUnit>(calItems, this.isNegative());

    }

    /**
     * <p>Extracts a new duration with all contained clock units only. </p>
     *
     * <p>The calendrical part will be removed. </p>
     *
     * @return  new duration with clock units only
     * @since   3.0
     * @see     #compose(Duration, Duration)
     * @see     #toCalendarPeriod()
     */
    /*[deutsch]
     * <p>Extrahiert eine neue Dauer, die nur alle Uhrzeiteinheiten
     * dieser Dauer enth&auml;lt. </p>
     *
     * <p>Der kalendarische Teil wird entfernt. </p>
     *
     * @return  new duration with clock units only
     * @since   3.0
     * @see     #compose(Duration, Duration)
     * @see     #toCalendarPeriod()
     */
    public Duration<ClockUnit> toClockPeriod() {

        if (this.isEmpty()) {
            return Duration.ofZero();
        }

        List<Item<ClockUnit>> clockItems = new ArrayList<Item<ClockUnit>>();

        for (Item<U> item : this.items) {
            if (item.getUnit() instanceof ClockUnit) {
                clockItems.add(Item.of(item.getAmount(), ClockUnit.class.cast(item.getUnit())));
            }
        }

        if (clockItems.isEmpty()) {
            return Duration.ofZero();
        }

        return new Duration<ClockUnit>(clockItems, this.isNegative());

    }

    /**
     * <p>Extracts a new duration with all contained clock units only. </p>
     *
     * <p>The calendrical part will be removed with the exception of the days
     * which will be converted into hours (on the base 1 day = 24 hours). </p>
     *
     * @return  new duration with clock units only
     * @since   3.28/4.24
     * @see     #compose(Duration, Duration)
     * @see     #toCalendarPeriod()
     */
    /*[deutsch]
     * <p>Extrahiert eine neue Dauer, die nur alle Uhrzeiteinheiten
     * dieser Dauer enth&auml;lt. </p>
     *
     * <p>Der kalendarische Teil wird bis auf die Tage entfernt. Die Tage selber werden in Stunden
     * (auf der Basis 1 Tag = 24 Stunden) umgerechnet. </p>
     *
     * @return  new duration with clock units only
     * @since   3.28/4.24
     * @see     #compose(Duration, Duration)
     * @see     #toCalendarPeriod()
     */
    public Duration<ClockUnit> toClockPeriodWithDaysAs24Hours() {

        if (this.isEmpty()) {
            return Duration.ofZero();
        }

        List<Item<ClockUnit>> clockItems = new ArrayList<Item<ClockUnit>>();
        long extraHours = 0L;

        for (Item<U> item : this.items) {
            if (item.getUnit() instanceof ClockUnit) {
                clockItems.add(Item.of(item.getAmount(), ClockUnit.class.cast(item.getUnit())));
            } else if (item.getUnit().equals(CalendarUnit.DAYS)) {
                extraHours = MathUtils.safeMultiply(item.getAmount(), 24);
            }
        }

        if (extraHours != 0L) {
            boolean hasHours = false;
            for (int i = 0, n = clockItems.size(); i < n; i++) {
                Item<ClockUnit> item = clockItems.get(i);
                if (item.getUnit() == ClockUnit.HOURS) {
                    hasHours = true;
                    item = Item.of(MathUtils.safeAdd(item.getAmount(), extraHours), ClockUnit.HOURS);
                    clockItems.set(i, item);
                    break;
                }
            }
            if (!hasHours) {
                clockItems.add(Item.of(extraHours, ClockUnit.HOURS));
            }
        } else if (clockItems.isEmpty()) {
            return Duration.ofZero();
        }

        return new Duration<ClockUnit>(clockItems, this.isNegative());

    }

    /**
     * <p>Normalizes this duration by given normalizer. </p>
     *
     * @param   normalizer  help object for normalizing this duration
     * @return  new normalized duration while this duration remains unaffected
     * @see     #STD_PERIOD
     * @see     #STD_CALENDAR_PERIOD
     * @see     #STD_CLOCK_PERIOD
     * @see     #approximateHours(int)
     * @see     #approximateMinutes(int)
     * @see     #approximateSeconds(int)
     * @see     ClockUnit#only()
     */
    /*[deutsch]
     * <p>Normalisiert diese Zeitspanne &uuml;ber den angegebenen
     * Mechanismus. </p>
     *
     * @param   normalizer  help object for normalizing this duration
     * @return  new normalized duration while this duration remains unaffected
     * @see     #STD_PERIOD
     * @see     #STD_CALENDAR_PERIOD
     * @see     #STD_CLOCK_PERIOD
     * @see     #approximateHours(int)
     * @see     #approximateMinutes(int)
     * @see     #approximateSeconds(int)
     * @see     ClockUnit#only()
     */
    public Duration<U> with(Normalizer<U> normalizer) {

        return convert(normalizer.normalize(this));

    }

    /**
     * <p>Yields an approximate normalizer in steps of hours which
     * finally uses years, months, days and rounded hours. </p>
     *
     * <p>The rounding algorithm consists of a combination of integer division and
     * multiplication using the given step width. Example for suppressing hours (and
     * all smaller units) by mean of an extra big step width of 24 hours (= 1 day): </p>
     *
     * <pre>
     *  Duration&lt;IsoUnit&gt; dur =
     *      Duration.ofPositive().years(2).months(13).days(35)
     *              .minutes(132).build()
     *              .with(Duration.approximateHours(24));
     *  System.out.println(dur); // output: P3Y2M4D
     * </pre>
     *
     * <p>Another example for rounded hours using static imports: </p>
     *
     * <pre>
     *  System.out.println(Duration.&lt;IsoUnit&gt;of(7, HOURS).with(approximateHours(3)));
     *  // output: PT6H
     * </pre>
     *
     * @param 	steps   rounding step width
     * @return	new normalizer for fuzzy and approximate durations
     * @throws	IllegalArgumentException if the argument is not positive
     * @since	2.0
     */
    /*[deutsch]
     * <p>Liefert einen Normalisierer, der eine Dauer in Stundenschritten auf
     * N&auml;herungsbasis mit Jahren, Monaten, Tagen und gerundeten Stunden
     * erstellt. </p>
     *
     * <p>Der Rundungsalgorithmus wendet zuerst die Integerdivision und dann eine
     * Multiplikation unter Benutzung des angegebenen Schrittfaktors an. Beispiel
     * f&uuml;r das Unterdr&uuml;cken von Stunden mittels einer extra gro&szlig;en
     * Schrittweite von 24 Stunden (= 1 Tag): </p>
     *
     * <pre>
     *  Duration&lt;IsoUnit&gt; dur =
     *      Duration.ofPositive().years(2).months(13).days(35)
     *              .minutes(132).build()
     *              .with(Duration.approximateHours(24));
     *  System.out.println(dur); // Ausgabe: P3Y2M4D
     * </pre>
     *
     * <p>Ein anderes Beispiel f&uuml;r gerundete Stunden mit Hilfe von <i>static imports</i>: </p>
     *
     * <pre>
     *  System.out.println(Duration.&lt;IsoUnit&gt;of(7, HOURS).with(approximateHours(3)));
     *  // Ausgabe: PT6H
     * </pre>
     *
     * @param 	steps   rounding step width
     * @return	new normalizer for fuzzy and approximate durations
     * @throws	IllegalArgumentException if the argument is not positive
     * @since	2.0
     */
    public static Normalizer<IsoUnit> approximateHours(int steps) {

        return new ApproximateNormalizer(steps, HOURS);

    }

    /**
     * <p>Yields an approximate normalizer in steps of minutes which
     * finally uses years, months, days, hours and rounded minutes. </p>
     *
     * @param 	steps   rounding step width
     * @return	new normalizer for fuzzy and approximate durations
     * @throws	IllegalArgumentException if the argument is not positive
     * @see     #approximateHours(int)
     * @since	2.0
     */
    /*[deutsch]
     * <p>Liefert einen Normalisierer, der eine Dauer in Minutenschritten auf
     * N&auml;herungsbasis mit Jahren, Monaten, Tagen, Stunden und gerundeten
     * Minuten erstellt. </p>
     *
     * @param 	steps   rounding step width
     * @return	new normalizer for fuzzy and approximate durations
     * @throws	IllegalArgumentException if the argument is not positive
     * @see     #approximateHours(int)
     * @since	2.0
     */
    public static Normalizer<IsoUnit> approximateMinutes(int steps) {

        return new ApproximateNormalizer(steps, MINUTES);

    }

    /**
     * <p>Yields an approximate normalizer in steps of seconds which finally
     * uses years, months, days, hours, minutes and rounded seconds. </p>
     *
     * @param 	steps   rounding step width
     * @return	new normalizer for fuzzy and approximate durations
     * @throws	IllegalArgumentException if the argument is not positive
     * @see     #approximateHours(int)
     * @since	2.0
     */
    /*[deutsch]
     * <p>Liefert einen Normalisierer, der eine Dauer in Sekundenschritten auf
     * N&auml;herungsbasis mit Jahren, Monaten, Tagen, Stunden, Minuten und
     * gerundeten Sekunden erstellt. </p>
     *
     * @param 	steps   rounding step width
     * @return	new normalizer for fuzzy and approximate durations
     * @throws	IllegalArgumentException if the argument is not positive
     * @see     #approximateHours(int)
     * @since	2.0
     */
    public static Normalizer<IsoUnit> approximateSeconds(int steps) {

        return new ApproximateNormalizer(steps, SECONDS);

    }

    /**
     * <p>Creates a normalizer which yields an approximate duration based on the maximum unit
     * of the original duration (but not smaller than seconds). </p>
     *
     * @return	new normalizer for fuzzy and approximate durations of only one unit
     *          (either years, months, days, hours, minutes or seconds)
     * @see     #approximateHours(int)
     * @see     #approximateMinutes(int)
     * @see     #approximateSeconds(int)
     * @since	3.14/4.11
     */
    /*[deutsch]
     * <p>Liefert einen Normalisierer, der eine gen&auml;herte Dauer basierend auf der
     * gr&ouml;&szlig;ten Zeiteinheit der urspr&uuml;nglichen Dauer (aber nicht kleiner
     * als Sekunden) erstellt. </p>
     *
     * @return	new normalizer for fuzzy and approximate durations of only one unit
     *          (either years, months, days, hours, minutes or seconds)
     * @see     #approximateHours(int)
     * @see     #approximateMinutes(int)
     * @see     #approximateSeconds(int)
     * @since	3.14/4.11
     */
    public static Normalizer<IsoUnit> approximateMaxUnitOnly() {

        return new ApproximateNormalizer(false);

    }

    /**
     * <p>Like {@code approximateMaxUnitOnly()} but can create week-based durations
     * if the count of days is bigger than {@code 6}.  </p>
     *
     * @return	new normalizer for fuzzy and approximate durations of only one unit
     *          (either years, months, weeks/days, hours, minutes or seconds)
     * @see     #approximateMaxUnitOnly()
     * @since	3.14/4.11
     */
    /*[deutsch]
     * <p>Wie {@code approximateMaxUnitOnly()}, kann aber eine wochenbasierte Dauer erzeugen,
     * wenn die Anzahl der Tage gr&ouml;&szlig;er als {@code 6} Tage ist. </p>
     *
     * @return	new normalizer for fuzzy and approximate durations of only one unit
     *          (either years, months, weeks/days, hours, minutes or seconds)
     * @see     #approximateMaxUnitOnly()
     * @since	3.14/4.11
     */
    public static Normalizer<IsoUnit> approximateMaxUnitOrWeeks() {

        return new ApproximateNormalizer(true);

    }

    /**
     * <p>Based on all stored duration items and the sign. </p>
     *
     * @return  {@code true} if {@code obj} is also a {@code Duration},
     *          has the same units and amounts, the same sign and the same
     *          calendrical status else {@code false}
     * @see     #getTotalLength()
     * @see     #isNegative()
     */
    /*[deutsch]
     * <p>Basiert auf allen gespeicherten Zeitspannenelementen und dem
     * Vorzeichen. </p>
     *
     * @return  {@code true} if {@code obj} is also a {@code Duration},
     *          has the same units and amounts, the same sign and the same
     *          calendrical status else {@code false}
     * @see     #getTotalLength()
     * @see     #isNegative()
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof Duration) {
            Duration<?> that = Duration.class.cast(obj);
            return (
                (this.negative == that.negative)
                && this.getTotalLength().equals(that.getTotalLength())
            );
        } else {
            return false;
        }

    }

    /**
     * <p>Computes the hash code. </p>
     */
    /*[deutsch]
     * <p>Basiert auf allen gespeicherten Zeitspannenelementen und dem
     * Vorzeichen passend zur Definition von {@code equals()}. </p>
     */
    @Override
    public int hashCode() {

        int hash = this.getTotalLength().hashCode();

        if (this.negative) {
            hash ^= hash;
        }

        return hash;

    }

    /**
     * <p>Gets a canonical representation which optionally starts with a
     * negative sign then continues with the letter &quot;P&quot;, followed
     * by a sequence of alphanumerical chars similar to the definition given
     * in ISO-8601. </p>
     *
     * <p>Example: In ISO-8601 a duration of one month, three days and four
     * hours is described as &quot;P1M3DT4H&quot;. The special char
     * &quot;T&quot; separates date and time part. Units are normally
     * printed using their symbols, as second alternative using the output
     * of their {@code toString()}-method within curly brackets. </p>
     *
     * <p>Is the duration negative then the representation will have a
     * preceding minus sign as specified by XML-schema (for example
     * &quot;-P2D&quot;) while an empty duration will always have the
     * format &quot;PT0S&quot; (second as universal unit). If the second
     * part is also fractional then this method will use the comma as
     * decimal separator char as recommended by ISO-8601. </p>
     *
     * <p>Note: The latter ISO-recommendation to use the comma as decimal
     * separator char can be overriden by setting the system property
     * &quot;net.time4j.format.iso.decimal.dot&quot; to &quot;true&quot;
     * so that the english variation of a dot will be choosen instead. </p>
     *
     * @see     #toStringISO()
     * @see     #toStringXML()
     * @see     #parsePeriod(String)
     */
    /*[deutsch]
     * <p>Liefert eine kanonische Darstellung, die optional mit einem negativen
     * Vorzeichen beginnt, dann mit dem Buchstaben &quot;P&quot; fortsetzt,
     * gefolgt von einer Reihe von alphanumerischen Zeichen analog zur
     * ISO8601-Definition. </p>
     *
     * <p>Beispiel: Im ISO8601-Format ist eine Zeitspanne von 1 Monat, 3 Tagen
     * und 4 Stunden als &quot;P1M3DT4H&quot; beschrieben, wobei der Buchstabe
     * &quot;T&quot; Datums- und Uhrzeitteil trennt. Einheiten werden in der
     * Regel als Symbole ausgegeben, andernfalls wird die Ausgabe ihrer
     * {@code toString()}-Methode in geschweiften Klammern benutzt. </p>
     *
     * <p>Ist die Zeitspanne negativ, so wird in &Uuml;bereinstimmung mit der
     * XML-Schema-Norm ein Minuszeichen vorangestellt (z.B. &quot;-P2D&quot;),
     * w&auml;hrend eine leere Zeitspanne das Format &quot;PT0S&quot;
     * (Sekunde als universelles Zeitma&szlig;) hat. Hat der Sekundenteil einen
     * Bruchteil, wird als Dezimaltrennzeichen das Komma entsprechend der
     * Empfehlung des ISO-Standards gew&auml;hlt. </p>
     *
     * <p>Hinweis: Die ISO-Empfehlung, ein Komma als Dezimaltrennzeichen zu
     * verwenden, kann mit Hilfe der bool'schen System-Property
     * &quot;net.time4j.format.iso.decimal.dot&quot; so ge&auml;ndert
     * werden, da&szlig; die angels&auml;chsiche Variante mit Punkt statt
     * Komma verwendet wird. </p>
     *
     * @see     #toStringISO()
     * @see     #toStringXML()
     * @see     #parsePeriod(String)
     */
    @Override
    public String toString() {

        return this.toString(PRINT_STYLE_NORMAL);

    }

    /**
     * <p>Gets a canonical representation which starts with the letter
     * &quot;P&quot;, followed by a sequence of alphanumerical chars as
     * defined in ISO-8601. </p>
     *
     * <p>A negative sign is not defined in ISO-8601 and will be rejected
     * by this method with an exception. An empty duration will always have
     * the format &quot;PT0S&quot; (second as universal unit). If the second
     * part is also fractional then this method will use the comma as
     * decimal separator char as recommended by ISO-8601. </p>
     *
     * <p>Note: The latter ISO-recommendation to use the comma as decimal
     * separator char can be overriden by setting the system property
     * &quot;net.time4j.format.iso.decimal.dot&quot; to &quot;true&quot;
     * so that the english variation of a dot will be choosen instead.
     * Furthermore, weeks are normalized to days if there are other
     * calendrical units like years or months. </p>
     *
     * <p>Only units of types {@code CalendarUnit} or {@code ClockUnit} can
     * be printed. </p>
     *
     * @return  String
     * @throws  ChronoException if this duration is negative or if any special
     *          units shall be output, but units of type {@code CalendarUnit}
     *          will be translated to iso-compatible units if necessary
     * @see     #parsePeriod(String)
     * @see     IsoUnit#getSymbol()
     */
    /*[deutsch]
     * <p>Liefert eine ISO-konforme Darstellung, die mit dem Buchstaben
     * &quot;P&quot; beginnt, gefolgt von einer Reihe von alphanumerischen
     * Zeichen analog zur ISO8601-Definition. </p>
     *
     * <p>Ein negatives Vorzeichen ist im ISO-8601-Standard nicht vorgesehen.
     * In diesem Fall wirft die Methode eine Ausnahme. Eine leere Zeitspanne
     * hat das Format &quot;PT0S&quot;. Hat der Sekundenteil einen
     * Bruchteil, wird als Dezimaltrennzeichen das Komma entsprechend der
     * Empfehlung des ISO-Standards gew&auml;hlt. </p>
     *
     * <p>Hinweis: Die ISO-Empfehlung, ein Komma als Dezimaltrennzeichen zu
     * verwenden, kann mit Hilfe der bool'schen System-Property
     * &quot;net.time4j.format.iso.decimal.dot&quot; so ge&auml;ndert
     * werden, da&szlig; die angels&auml;chsiche Variante mit Punkt statt
     * Komma verwendet wird. Es gilt auch, da&szlig; ein vorhandenes Wochenfeld
     * zu Tagen auf der Basis (1 Woche = 7 Tage) normalisiert wird, wenn
     * zugleich auch andere Kalendereinheiten vorhanden sind. </p>
     *
     * <p>Nur Einheiten der Typen {@code CalendarUnit} oder {@code ClockUnit
     * k&ouml;nnen angezeigt werden. </p>
     *
     * @return  String
     * @throws  ChronoException if this duration is negative or if any special
     *          units shall be output, but units of type {@code CalendarUnit}
     *          will be translated to iso-compatible units if necessary
     * @see     #parsePeriod(String)
     * @see     IsoUnit#getSymbol()
     */
    public String toStringISO() {

        return this.toString(PRINT_STYLE_ISO);

    }

    /**
     * <p>Gets a canonical representation conforming to XML-schema which
     * optionally starts with a negative sign then continues with the letter
     * &quot;P&quot;, followed by a sequence of alphanumerical chars similar
     * to the definition given in ISO-8601. </p>
     *
     * <p>Is the duration negative then the representation will have a
     * preceding minus sign as specified by XML-schema (for example
     * &quot;-P2D&quot;) while an empty duration will always have the
     * format &quot;PT0S&quot; (second as universal unit). If the second
     * part is also fractional then this method will use the dot as
     * decimal separator char (deviating specification in XML-schema).
     * Weeks will always be normalized to days. </p>
     *
     * <p>Only units of types {@code CalendarUnit} or {@code ClockUnit} can
     * be printed. </p>
     *
     * @return  String
     * @throws  ChronoException if any special units shall be
     *          output, but units of type {@code CalendarUnit} will be
     *          translated to xml-compatible units if necessary
     * @see     #parsePeriod(String)
     * @see     IsoUnit#getSymbol()
     */
    /*[deutsch]
     * <p>Liefert eine XML-konforme Darstellung, die optional mit einem
     * negativen Vorzeichen beginnt, dann mit dem Buchstaben &quot;P&quot;
     * fortsetzt, gefolgt von einer Reihe von alphanumerischen Zeichen analog
     * zur ISO8601-Definition. </p>
     *
     * <p>Ist die Zeitspanne negativ, so wird in &Uuml;bereinstimmung mit der
     * XML-Schema-Norm ein Minuszeichen vorangestellt (z.B. &quot;-P2D&quot;),
     * w&auml;hrend eine leere Zeitspanne das Format &quot;PT0S&quot;
     * (Sekunde als universelles Zeitma&szlig;) hat. Hat der Sekundenteil einen
     * Bruchteil, wird als Dezimaltrennzeichen der Punkt anders als in der
     * Empfehlung des ISO-Standards gew&auml;hlt. Es gilt auch, da&szlig;
     * ein vorhandenes Wochenfeld zu Tagen auf der Basis (1 Woche = 7 Tage)
     * normalisiert wird. </p>
     *
     * <p>Nur Einheiten der Typen {@code CalendarUnit} oder {@code ClockUnit
     * k&ouml;nnen angezeigt werden. </p>
     *
     * @return  String
     * @throws  ChronoException if any special units shall be
     *          output, but units of type {@code CalendarUnit} will be
     *          translated to xml-compatible units if necessary
     * @see     #parsePeriod(String)
     * @see     IsoUnit#getSymbol()
     */
    public String toStringXML() {

        return this.toString(PRINT_STYLE_XML);

    }

    /**
     * <p>Parses a canonical representation to a duration. </p>
     *
     * <p>Canonical representations which start with the literal P are
     * also called &quot;period&quot; in Time4J (P-string). This format
     * is strongly recommended for storage in databases or XML. Syntax
     * in a notation similar to regular expressions: </p>
     *
     * <pre>
     *  sign := [-]?
     *  amount := [0-9]+
     *  fraction := [,\.]{amount}
     *  years-months-days := ({amount}Y)?({amount}M)?({amount}D)?
     *  weeks := ({amount}W)?
     *  date := {years-months-days} | {weeks}
     *  time := ({amount}H)?({amount}M)?({amount}{fraction}?S)?
     *  period := {sign}P{date}(T{time})? | PT{time}
     * </pre>
     *
     * <p>The units MILLENNIA, CENTURIES, DECADES and QUARTERS defined in
     * {@link CalendarUnit} are supported but not special units like
     * {@code CalendarUnit.weekBasedYears()}. </p>
     *
     * <p>Furthermore there is the constraint that the symbols P and T
     * must be followed by at least one duration item of amount and unit.
     * All items with zero amount will be ignored however. The only item
     * which is allowed to have a fractional part is SECONDS and can contain
     * a comma as well as a dot as decimal separator. In ISO-8601 the comma
     * is the preferred char, in XML-schema only the dot is allowed. If this
     * parser is used in context of XML-schema (type xs:duration) it must
     * be stated that week items are missing in contrast to ISO-8601. The
     * method {@code toStringXML()} takes into account these characteristics
     * of XML-schema (leaving aside the fact that XML-schema is potentially
     * designed for unlimited big amounts but Time4J can define durations
     * only in long range with nanosecond precision at best). </p>
     *
     * <p>Note: The alternative ISO-formats PYYYY-MM-DDThh:mm:ss and
     * PYYYY-DDDThh:mm:ss and their basic variants are supported since
     * version v2.0. </p>
     *
     * <p>Examples for supported formats: </p>
     *
     * <pre>
     *  date := -P7Y4M3D (negative: 7 years, 4 months, 3 days)
     *  time := PT3H2M1,4S (positive: 3 hours, 2 minutes, 1400 milliseconds)
     *  date-time := P1Y1M5DT15H59M10.400S (dot as decimal separator)
     *  alternative := P0000-02-15T17:45
     * </pre>
     *
     * @param   period          duration in canonical, ISO-8601-compatible or
     *                          XML-schema-compatible format (P-string)
     * @return  parsed duration in all possible standard units of date and time
     * @throws  ParseException if parsing fails
     * @since   1.2.1
     * @see     #parseCalendarPeriod(String)
     * @see     #parseClockPeriod(String)
     * @see     #toString()
     * @see     #toStringISO()
     * @see     #toStringXML()
     */
    /*[deutsch]
     * <p>Parst eine kanonische Darstellung zu einer Dauer. </p>
     *
     * <p>Kanonische Darstellungen, die mit dem Literal P beginnen, werden
     * in Time4J auch als &quot;period&quot; bezeichnet (P-string). Dieses
     * Format ist erste Wahl, wenn es um das Speichern einer Dauer in
     * Datenbanken oder XML geht. Syntax in RegExp-&auml;hnlicher Notation: </p>
     *
     * <pre>
     *  sign := [-]?
     *  amount := [0-9]+
     *  fraction := [,\.]{amount}
     *  years-months-days := ({amount}Y)?({amount}M)?({amount}D)?
     *  weeks := ({amount}W)?
     *  date := {years-months-days} | {weeks}
     *  time := ({amount}H)?({amount}M)?({amount}{fraction}?S)?
     *  period := {sign}P{date}(T{time})? | PT{time}
     * </pre>
     *
     * <p>Die in {@link CalendarUnit} definierten Zeiteinheiten MILLENNIA,
     * CENTURIES, DECADES und QUARTERS werden mitsamt ihren Symbolen ebenfalls
     * unterst&uuml;tzt, nicht aber spezielle Zeiteinheiten wie zum Beispiel
     * {@code CalendarUnit.weekBasedYears()}. </p>
     *
     * <p>Weiterhin gilt die Einschr&auml;nkung, da&szlig; die Symbole P und T
     * mindestens ein Zeitfeld nach sich ziehen m&uuml;ssen. Alle Felder mit
     * {@code 0}-Betr&auml;gen werden beim Parsen ignoriert. Das einzig erlaubte
     * Dezimalfeld der Sekunden kann sowohl einen Punkt wie auch ein Komma
     * als Dezimaltrennzeichen haben. Im ISO-Standard ist das Komma das
     * bevorzugte Zeichen, in XML-Schema nur der Punkt zul&auml;ssig. Speziell
     * f&uuml;r die Verwendung in XML-Schema (Typ xs:duration) ist zu beachten,
     * da&szlig; Wochenfelder anders als im ISO-Standard nicht vorkommen. Die
     * Methode {@code toStringXML()} ber&uuml;cksichtigt diese Besonderheiten
     * von XML-Schema (abgesehen davon, da&szlig; XML-Schema potentiell
     * unbegrenzt gro&szlig;e Zahlen zul&auml;&szlig;t, aber Time4J eine
     * Zeitspanne nur im long-Bereich mit maximal Nanosekunden-Genauigkeit
     * definiert). </p>
     *
     * <p>Hinweis: Die alternativen ISO-Formate PYYYY-MM-DDThh:mm:ss und
     * PYYYY-DDDThh:mm:ss und ihre Basisvarianten werden seit Version v2.0
     * ebenfalls unterst&uuml;tzt. </p>
     *
     * <p>Beispiele f&uuml;r unterst&uuml;tzte Formate: </p>
     *
     * <pre>
     *  date := -P7Y4M3D (negativ: 7 Jahre, 4 Monate, 3 Tage)
     *  time := PT3H2M1,4S (positiv: 3 Stunden, 2 Minuten, 1400 Millisekunden)
     *  date-time := P1Y1M5DT15H59M10.400S (Punkt als Dezimaltrennzeichen)
     *  alternative := P0000-02-15T17:45
     * </pre>
     *
     * @param   period          duration in canonical, ISO-8601-compatible or
     *                          XML-schema-compatible format (P-string)
     * @return  parsed duration in all possible standard units of date and time
     * @throws  ParseException if parsing fails
     * @since   1.2.1
     * @see     #parseCalendarPeriod(String)
     * @see     #parseClockPeriod(String)
     * @see     #toString()
     * @see     #toStringISO()
     * @see     #toStringXML()
     */
    public static Duration<IsoUnit> parsePeriod(String period)
        throws ParseException {

        return parsePeriod(period, IsoUnit.class);

    }

    /**
     * <p>Parses a canonical representation with only date units to a
     * calendrical duration. </p>
     *
     * @param   period          duration in canonical, ISO-8601-compatible or
     *                          XML-schema-compatible format (P-string)
     * @return  parsed calendrical duration
     * @throws  ParseException if parsing fails
     * @see     #parsePeriod(String)
     * @see     #parseClockPeriod(String)
     */
    /*[deutsch]
     * <p>Parst eine kanonische Darstellung nur mit
     * Datumskomponenten zu einer Dauer. </p>
     *
     * @param   period          duration in canonical, ISO-8601-compatible or
     *                          XML-schema-compatible format (P-string)
     * @return  parsed calendrical duration
     * @throws  ParseException if parsing fails
     * @see     #parsePeriod(String)
     * @see     #parseClockPeriod(String)
     */
    public static Duration<CalendarUnit> parseCalendarPeriod(String period)
        throws ParseException {

        return parsePeriod(period, CalendarUnit.class);

    }

    /**
     * <p>Parses a canonical representation with only wall time units to a
     * time-only duration. </p>
     *
     * @param   period          duration in canonical, ISO-8601-compatible or
     *                          XML-schema-compatible format (P-string)
     * @return  parsed time-only duration
     * @throws  ParseException if parsing fails
     * @see     #parsePeriod(String)
     * @see     #parseCalendarPeriod(String)
     */
    /*[deutsch]
     * <p>Parst eine kanonische Darstellung nur mit
     * Uhrzeitkomponenten zu einer Dauer. </p>
     *
     * @param   period          duration in canonical, ISO-8601-compatible or
     *                          XML-schema-compatible format (P-string)
     * @return  parsed time-only duration
     * @throws  ParseException if parsing fails
     * @see     #parsePeriod(String)
     * @see     #parseCalendarPeriod(String)
     */
    public static Duration<ClockUnit> parseClockPeriod(String period)
        throws ParseException {

        return parsePeriod(period, ClockUnit.class);

    }

    /**
     * <p>Parses a canonical representation with only week-based units (Y, W and D) to a
     * calendrical duration where years are interpreted as week-based years. </p>
     *
     * @param   period          duration in canonical or ISO-8601-compatible format (P-string)
     * @return  parsed calendrical duration
     * @throws  ParseException if parsing fails
     * @see     #parsePeriod(String)
     * @see     CalendarUnit#weekBasedYears()
     * @since   3.21/4.17
     */
    /*[deutsch]
     * <p>Parst eine kanonische Darstellung nur mit wochen-basierten
     * Datumskomponenten (Y, W, D) zu einer Dauer, in der Jahr als
     * wochenbasierte Jahre interpretiert werden. </p>
     *
     * @param   period          duration in canonical or ISO-8601-compatible format (P-string)
     * @return  parsed calendrical duration
     * @throws  ParseException if parsing fails
     * @see     #parsePeriod(String)
     * @see     CalendarUnit#weekBasedYears()
     * @since   3.21/4.17
     */
    public static Duration<IsoDateUnit> parseWeekBasedPeriod(String period)
        throws ParseException {

        return parsePeriod(period, IsoDateUnit.class);

    }

    /**
     * <p>Equivalent to {@link net.time4j.Duration.Formatter#ofPattern(String)}. </p>
     *
     * @param   pattern format pattern
     * @return  new formatter instance
     * @throws  IllegalArgumentException in any case of pattern inconsistencies or failures
     * @since   3.0
     */
    /*[deutsch]
     * <p>&Auml;quivalent zu {@link net.time4j.Duration.Formatter#ofPattern(String)}. </p>
     *
     * @param   pattern format pattern
     * @return  new formatter instance
     * @throws  IllegalArgumentException in any case of pattern inconsistencies or failures
     * @since   3.0
     */
    public static Duration.Formatter<IsoUnit> formatter(String pattern) {

        return Duration.Formatter.ofPattern(pattern);

    }

    /**
     * <p>Equivalent to {@link net.time4j.Duration.Formatter#ofPattern(Class, String)}. </p>
     *
     * @param   <U>     generic unit type
     * @param   type    reified unit type
     * @param   pattern format pattern
     * @return  new formatter instance
     * @throws  IllegalArgumentException in any case of pattern inconsistencies or failures
     * @since   3.0
     */
    /*[deutsch]
     * <p>&Auml;quivalent zu {@link net.time4j.Duration.Formatter#ofPattern(Class, String)}. </p>
     *
     * @param   <U>     generic unit type
     * @param   type    reified unit type
     * @param   pattern format pattern
     * @return  new formatter instance
     * @throws  IllegalArgumentException in any case of pattern inconsistencies or failures
     * @since   3.0
     */
    public static <U extends IsoUnit> Duration.Formatter<U> formatter(
        Class<U> type,
        String pattern
    ) {

        return Duration.Formatter.ofPattern(type, pattern);

    }

    private String toString(int style) {

        if (
            (style == PRINT_STYLE_ISO)
            && this.isNegative()
        ) {
            throw new ChronoException("Negative sign not allowed in ISO-8601.");
        }

        if (this.isEmpty()) {
            return "PT0S";
        }

        boolean xml = (style == PRINT_STYLE_XML);
        StringBuilder sb = new StringBuilder();

        if (this.isNegative()) {
            sb.append('-');
        }

        sb.append('P');
        boolean timeAppended = false;
        boolean weekBased = false;
        long nanos = 0;
        long seconds = 0;
        long weeksAsDays = 0;

        for (int index = 0, limit = this.count(); index < limit; index++) {
            Item<U> item = this.getTotalLength().get(index);
            U unit = item.getUnit();

            if (!timeAppended && !unit.isCalendrical()) {
                sb.append('T');
                timeAppended = true;
            }

            long amount = item.getAmount();
            char symbol = unit.getSymbol();

            if (unit == Weekcycle.YEARS)  {
                weekBased = true;
            }

            if ((symbol > '0') && (symbol <= '9')) {
                assert (symbol == '9');
                nanos = amount;
            } else if (symbol == 'S') {
                seconds = amount;
            } else {
                if (xml || (style == PRINT_STYLE_ISO)) {
                    switch (symbol) {
                        case 'D':
                            if (weeksAsDays != 0) {
                                amount = MathUtils.safeAdd(amount, weeksAsDays);
                                weeksAsDays = 0;
                            }
                            sb.append(amount);
                            break;
                        case 'M':
                        case 'Y':
                        case 'H':
                            sb.append(amount);
                            break;
                        case 'W':
                            if (limit == 1) {
                                if (xml) {
                                    sb.append(MathUtils.safeMultiply(amount, 7));
                                    symbol = 'D';
                                } else {
                                    sb.append(amount);
                                }
                            } else {
                                weeksAsDays = MathUtils.safeMultiply(amount, 7);
                                if (this.contains(DAYS)) {
                                    continue;
                                } else {
                                    sb.append(weeksAsDays);
                                    weeksAsDays = 0;
                                    symbol = 'D';
                                }
                            }
                            break;
                        case 'Q':
                            sb.append(MathUtils.safeMultiply(amount, 3));
                            symbol = 'M';
                            break;
                        case 'E':
                            sb.append(MathUtils.safeMultiply(amount, 10));
                            symbol = 'Y';
                            break;
                        case 'C':
                            sb.append(MathUtils.safeMultiply(amount, 100));
                            symbol = 'Y';
                            break;
                        case 'I':
                            sb.append(MathUtils.safeMultiply(amount, 1000));
                            symbol = 'Y';
                            break;
                        default:
                            String mode = xml ? "XML" : "ISO";
                            throw new ChronoException(
                                "Special units cannot be output in "
                                + mode + "-mode: "
                                + this.toString(PRINT_STYLE_NORMAL));
                    }
                } else {
                    sb.append(amount);
                }

                if (symbol == '\u0000') {
                    sb.append('{');
                    sb.append(unit);
                    sb.append('}');
                } else {
                    sb.append(symbol);
                }
            }

        }

        if (nanos != 0) {
            seconds = MathUtils.safeAdd(seconds, nanos / MRD);
            sb.append(seconds);
            sb.append(xml ? '.' : ISO_DECIMAL_SEPARATOR);
            String f = String.valueOf(nanos % MRD);
            for (int i = 0, len = 9 - f.length(); i < len; i++) {
                sb.append('0');
            }
            sb.append(f);
            sb.append('S');
        } else if (seconds != 0) {
            sb.append(seconds);
            sb.append('S');
        }

        if (weekBased) {
            boolean representable = !timeAppended;
            if (representable) {
                for (int index = 0, limit = this.count(); index < limit; index++) {
                    Object unit = this.getTotalLength().get(index).getUnit();
                    if ((unit != Weekcycle.YEARS) && (unit != CalendarUnit.WEEKS) && (unit != CalendarUnit.DAYS)) {
                        representable = false;
                        break;
                    }
                }
            }
            if (!representable) {
                int pos = sb.indexOf("Y");
                sb.replace(pos, pos + 1, "{WEEK_BASED_YEARS}");
            }
        }

        return sb.toString();

    }

    private static boolean hasMixedSigns(
        long months,
        long daytime
    ) {

        return (
            ((months < 0) && (daytime > 0))
            || ((months > 0) && (daytime < 0)));

    }

    private int count() {

        return this.getTotalLength().size();

    }

    // wildcard capture
    private static <U> boolean isEmpty(TimeSpan<U> timespan) {

        List<Item<U>> items = timespan.getTotalLength();

        for (int i = 0, n = items.size(); i < n; i++) {
            if (items.get(i).getAmount() > 0) {
                return false;
            }
        }

        return true;

    }

    private static Duration<CalendarUnit> ofCalendarUnits(
        long years,
        long months,
        long days,
        boolean negative
    ) {

        List<Item<CalendarUnit>> items = new ArrayList<Item<CalendarUnit>>(3);

        if (years != 0) {
            items.add(Item.of(years, YEARS));
        }

        if (months != 0) {
            items.add(Item.of(months, MONTHS));
        }

        if (days != 0) {
            items.add(Item.of(days, DAYS));
        }

        return new Duration<CalendarUnit>(items, negative);

    }

    private static Duration<ClockUnit> ofClockUnits(
        long hours,
        long minutes,
        long seconds,
        long nanos,
        boolean negative
    ) {

        List<Item<ClockUnit>> items = new ArrayList<Item<ClockUnit>>(4);

        if (hours != 0) {
            items.add(Item.of(hours, HOURS));
        }

        if (minutes != 0) {
            items.add(Item.of(minutes, MINUTES));
        }

        if (seconds != 0) {
            items.add(Item.of(seconds, SECONDS));
        }

        if (nanos != 0) {
            items.add(Item.of(nanos, NANOS));
        }

        return new Duration<ClockUnit>(items, negative);

    }

    private static <U extends IsoUnit> Duration<U> create(
        Map<U, Long> map,
        boolean negative
    ) {

        if (map.isEmpty()) {
            return ofZero();
        }

        List<Item<U>> temp = new ArrayList<Item<U>>(map.size());
        long nanos = 0;

        for (Map.Entry<U, Long> entry : map.entrySet()) {
            long amount = entry.getValue().longValue();

            if (amount != 0) {
                U key = entry.getKey();

                if (key == MILLIS) {
                    nanos =
                        MathUtils.safeAdd(
                            nanos,
                            MathUtils.safeMultiply(amount, MIO));
                } else if (key == MICROS) {
                    nanos =
                        MathUtils.safeAdd(
                            nanos,
                            MathUtils.safeMultiply(amount, 1000));
                } else if (key == NANOS) {
                    nanos = MathUtils.safeAdd(nanos, amount);
                } else {
                    temp.add(Item.of(amount, key));
                }
            }
        }

        if (nanos != 0) {
            U key = cast(NANOS);
            temp.add(Item.of(nanos, key));
        } else if (temp.isEmpty()) {
            return ofZero();
        }

        return new Duration<U>(temp, negative);

    }

    // binäre Suche
    private int getIndex(ChronoUnit unit) {

        return getIndex(unit, this.getTotalLength());

    }

    // binäre Suche
    private static <U extends ChronoUnit> int getIndex(
        ChronoUnit unit,
        List<Item<U>> list
    ) {

        int low = 0;
        int high = list.size() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            ChronoUnit midUnit = list.get(mid).getUnit();
            int cmp = StdNormalizer.compare(midUnit, unit);

            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return mid; // gefunden
            }
        }

        return -1;

    }

    // optional
    private static <U extends IsoUnit> Item<U> replaceFraction(
        long amount,
        U unit
    ) {

        if (unit.equals(MILLIS)) {
            amount = MathUtils.safeMultiply(amount, MIO);
            unit = cast(NANOS);
        } else if (unit.equals(MICROS)) {
            amount = MathUtils.safeMultiply(amount, 1000L);
            unit = cast(NANOS);
        } else {
            return null;
        }

        return Item.of(amount, unit);

    }

    private static <U extends IsoUnit> Duration<U> merge(
    	Duration<U> duration,
    	TimeSpan<? extends U> timespan
    ) {

        if (duration.isEmpty()) {
            if (isEmpty(timespan)) {
                return duration;
            } else if (timespan instanceof Duration) {
                return cast(timespan);
            }
        }

        Map<U, Long> map = new HashMap<U, Long>();

        for (int i = 0, n = duration.count(); i < n; i++) {
            Item<U> item = duration.getTotalLength().get(i);
            map.put(
                item.getUnit(),
                Long.valueOf(
                    MathUtils.safeMultiply(
                        item.getAmount(),
                        (duration.isNegative() ? -1 : 1)
                    )
                )
            );
        }

        boolean tsign = timespan.isNegative();

        for (int i = 0, n = timespan.getTotalLength().size(); i < n; i++) {
            TimeSpan.Item<? extends U> e = timespan.getTotalLength().get(i);
            U unit = e.getUnit();
            long amount = e.getAmount();

            // Millis und Micros ersetzen
            Item<U> item = replaceFraction(amount, unit);

            if (item != null) {
                amount = item.getAmount();
                unit = item.getUnit();
            }

            // Items aktualisieren
            if (map.containsKey(unit)) {
                map.put(
                    unit,
                    Long.valueOf(
                        MathUtils.safeAdd(
                            map.get(unit).longValue(),
                            MathUtils.safeMultiply(amount, (tsign ? -1 : 1))
                        )
                    )
                );
            } else {
                map.put(
                    unit,
                    MathUtils.safeMultiply(amount, (tsign ? -1 : 1))
                );
            }
        }

        boolean negative = false;

        if (duration.isNegative() == tsign) {
        	negative = tsign;
        } else {
            boolean firstScan = true;
            for (Map.Entry<U, Long> entry : map.entrySet()) {
                boolean nsign = (entry.getValue().longValue() < 0);
                if (firstScan) {
                    negative = nsign;
                    firstScan = false;
                } else if (negative != nsign) {
                	return null; // mixed signs
                }
            }
        }

        if (negative) {
            for (Map.Entry<U, Long> entry : map.entrySet()) {
                long value = entry.getValue().longValue();
                map.put(
                    entry.getKey(),
                    Long.valueOf(
                        (value < 0)
                        ? MathUtils.safeNegate(value)
                        : value)
                );
            }
        }

        return Duration.create(map, negative);

    }

    private static <U extends IsoUnit> boolean summarize(
        TimeSpan<? extends U> timespan,
        long[] sums
    ) {

        long months = sums[0];
        long days = sums[1];
        long secs = sums[2];
        long nanos = sums[3];

        for (Item<? extends U> item : timespan.getTotalLength()) {
            U unit = item.getUnit();
            long amount = item.getAmount();

            if (timespan.isNegative()) {
                amount = MathUtils.safeNegate(amount);
            }

            if (unit instanceof CalendarUnit) {
                CalendarUnit cu = CalendarUnit.class.cast(unit);
                switch (cu) {
                    case MILLENNIA:
                        months =
                            MathUtils.safeAdd(months,
                                MathUtils.safeMultiply(amount, 12 * 1000));
                        break;
                    case CENTURIES:
                        months =
                            MathUtils.safeAdd(months,
                                MathUtils.safeMultiply(amount, 12 * 100));
                        break;
                    case DECADES:
                        months =
                            MathUtils.safeAdd(months,
                                MathUtils.safeMultiply(amount, 12 * 10));
                        break;
                    case YEARS:
                        months =
                            MathUtils.safeAdd(months,
                                MathUtils.safeMultiply(amount, 12));
                        break;
                    case QUARTERS:
                        months =
                            MathUtils.safeAdd(months,
                                MathUtils.safeMultiply(amount, 3));
                        break;
                    case MONTHS:
                        months = MathUtils.safeAdd(months, amount);
                        break;
                    case WEEKS:
                        days =
                            MathUtils.safeAdd(days,
                                MathUtils.safeMultiply(amount, 7));
                        break;
                    case DAYS:
                        days = MathUtils.safeAdd(days, amount);
                        break;
                    default:
                        throw new UnsupportedOperationException(cu.name());
                }
            } else if (unit instanceof ClockUnit) {
                ClockUnit cu = ClockUnit.class.cast(unit);
                switch (cu) {
                    case HOURS:
                        secs =
                            MathUtils.safeAdd(secs,
                                MathUtils.safeMultiply(amount, 3600));
                        break;
                    case MINUTES:
                        secs =
                            MathUtils.safeAdd(secs,
                                MathUtils.safeMultiply(amount, 60));
                        break;
                    case SECONDS:
                        secs = MathUtils.safeAdd(secs, amount);
                        break;
                    case MILLIS:
                        nanos =
                            MathUtils.safeAdd(nanos,
                                MathUtils.safeMultiply(amount, MIO));
                        break;
                    case MICROS:
                        nanos =
                            MathUtils.safeAdd(nanos,
                                MathUtils.safeMultiply(amount, 1000));
                        break;
                    case NANOS:
                        nanos = MathUtils.safeAdd(nanos, amount);
                        break;
                    default:
                        throw new UnsupportedOperationException(cu.name());
                }
            } else {
                return false;
            }
        }

        if (nanos != 0) {
            nanos =
                MathUtils.safeAdd(nanos,
                    MathUtils.safeMultiply(days, 86400L * MRD));
            nanos =
                MathUtils.safeAdd(nanos,
                    MathUtils.safeMultiply(secs, MRD));
            days = 0;
            secs = 0;
        } else if (secs != 0) {
            secs =
                MathUtils.safeAdd(secs,
                    MathUtils.safeMultiply(days, 86400L));
            days = 0;
        }

        sums[0] = months;
        sums[1] = days;
        sums[2] = secs;
        sums[3] = nanos;

        return true;

    }

    private static <U extends IsoUnit> Duration<U> convert(
        TimeSpan<U> timespan
    ) {

        if (timespan instanceof Duration) {
            return cast(timespan);
        } else {
            Duration<U> zero = ofZero();
            return zero.plus(timespan);
        }

    }

    private boolean isFractionUnit(IsoUnit unit) {

        char symbol = unit.getSymbol();
        return ((symbol >= '1') && (symbol <= '9'));

    }

    @SuppressWarnings("unchecked")
    private static <T> T cast(Object obj) {

        return (T) obj;

    }

    //~ Parse-Routinen ----------------------------------------------------

    private static <U extends IsoUnit> Duration<U> parsePeriod(
        String period,
        Class<U> type
    ) throws ParseException {

        int index = 0;
        boolean negative = false;

        if (period.length() == 0) {
            throw new ParseException("Empty period string.", index);
        } else if (period.charAt(0) == '-') {
            negative = true;
            index = 1;
        }

        try {

            if (period.charAt(index) != 'P') {
                throw new ParseException(
                    "Format symbol \'P\' expected: " + period, index);
            } else {
                index++;
            }

            List<Item<U>> items = new ArrayList<Item<U>>();
            int sep = period.indexOf('T', index);
            boolean calendrical = (sep == -1);
            int typeID = SUPER_TYPE;

            if (type == CalendarUnit.class) {
                typeID = CALENDAR_TYPE;
            } else if (type == ClockUnit.class) {
                typeID = CLOCK_TYPE;
            } else if (type == IsoDateUnit.class) {
                typeID = WEEK_BASED_TYPE;
            }

            if (calendrical) {
                if (typeID == CLOCK_TYPE) {
                    throw new ParseException(
                        "Format symbol \'T\' expected: " + period, index);
                } else {
                    parse(period, index, period.length(), ((typeID == SUPER_TYPE) ? CALENDAR_TYPE : typeID), items);
                }
            } else {
                boolean alternative = false;
                if (sep > index) {
                    if (typeID == CLOCK_TYPE) {
                        throw new ParseException(
                            "Unexpected date component found: " + period,
                            index);
                    } else {
                        alternative = parse(period, index, sep, CALENDAR_TYPE, items);
                    }
                }
                if (type == CalendarUnit.class) {
                    throw new ParseException(
                        "Unexpected time component found: " + period, sep);
                } else if (alternative) {
                    parseAlt(period, sep + 1, period.length(), false, items);
                } else {
                    parse(period, sep + 1, period.length(), CLOCK_TYPE, items);
                }
            }

            return new Duration<U>(items, negative);

        } catch (IndexOutOfBoundsException ex) {
            ParseException pe =
                new ParseException(
                    "Unexpected termination of period string: " + period,
                    index);
            pe.initCause(ex);
            throw pe;
        }

    }

    private static <U extends ChronoUnit> boolean parse(
        String period,
        int from,
        int to,
        int typeID,
        List<Item<U>> items
    ) throws ParseException {

        // alternative format?
        char ending = period.charAt(to - 1);

        if ((ending >= '0') && (ending <= '9') && (typeID != WEEK_BASED_TYPE)) {
            parseAlt(period, from, to, (typeID == CALENDAR_TYPE), items);
            return true;
        }

        if (from == to) {
            throw new ParseException(period, from);
        }

        StringBuilder num = null;
        boolean endOfItem = false;
        ChronoUnit last = null;
        int index = from;
        boolean decimal = false;

        for (int i = from; i < to; i++) {
            char c = period.charAt(i);

            if ((c >= '0') && (c <= '9')) {
                if (num == null) {
                    num = new StringBuilder();
                    endOfItem = false;
                    index = i;
                }
                num.append(c);
            } else if ((c == ',') || (c == '.')) {
                if ((num == null) || (typeID != CLOCK_TYPE)) {
                    throw new ParseException(
                        "Decimal separator misplaced: " + period, i);
                } else {
                    endOfItem = true;
                    long amount = parseAmount(period, num.toString(), index);
                    ChronoUnit unit = SECONDS;
                    last =
                        addParsedItem(unit, last, amount, period, i, items);
                    num = null;
                    decimal = true;
                }
            } else if (endOfItem) {
                throw new ParseException(
                    "Unexpected char \'" + c + "\' found: " + period, i);
            } else if (decimal) {
                if (c != 'S') {
                    throw new ParseException(
                        "Second symbol expected: " + period, i);
                } else if (num == null) {
                    throw new ParseException(
                        "Decimal separator misplaced: " + period, i - 1);
                } else if (num.length() > 9) {
                    num.delete(9, num.length());
                }
                for (int j = num.length(); j < 9; j++) {
                    num.append('0');
                }
                endOfItem = true;
                long amount = parseAmount(period, num.toString(), index);
                ChronoUnit unit = NANOS;
                num = null;
                last = addParsedItem(unit, last, amount, period, i, items);
            } else {
                endOfItem = true;
                long amount =
                    parseAmount(
                        period,
                        (num == null) ? String.valueOf(c) : num.toString(),
                        index);
                num = null;
                ChronoUnit unit;
                if (typeID == CLOCK_TYPE) {
                    unit = parseTimeSymbol(c, period, i);
                } else if (typeID == WEEK_BASED_TYPE) {
                    unit = parseWeekBasedSymbol(c, period, i);
                } else {
                    unit = parseDateSymbol(c, period, i);
                }
                last = addParsedItem(unit, last, amount, period, i, items);
            }

        }

        if (!endOfItem) {
            throw new ParseException("Unit symbol expected: " + period, to);
        }

        return false;

    }

    private static <U extends ChronoUnit> void parseAlt(
        String period,
        int from,
        int to,
        boolean date,
        List<Item<U>> items
    ) throws ParseException {

        boolean extended = false;

        if (date) {
            if (from + 4 < to) {
                extended = (period.charAt(from + 4) == '-');
            }
            boolean ordinalStyle = (
                extended
                ? (from + 8 == to)
                : (from + 7 == to));
            Duration<?> dur = getAlternativeDateFormat(extended, ordinalStyle).parse(period, from);
            long years = dur.getPartialAmount(YEARS);
            long months;
            long days;
            if (ordinalStyle) {
                months = 0;
                days = dur.getPartialAmount(DAYS);
                // ISO does not specify any constraint here
            } else {
                months = dur.getPartialAmount(MONTHS);
                days = dur.getPartialAmount(DAYS);
                if (months > 12) {
                    throw new ParseException(
                        "ISO-8601 prohibits months-part > 12: " + period,
                        from + 4 + (extended ? 1 : 0));
                }
                if (days > 30) {
                    throw new ParseException(
                        "ISO-8601 prohibits days-part > 30: " + period,
                        from + 6 + (extended ? 2 : 0));
                }
            }
            if (years > 0) {
                U unit = cast(YEARS);
                items.add(Item.of(years, unit));
            }
            if (months > 0) {
                U unit = cast(MONTHS);
                items.add(Item.of(months, unit));
            }
            if (days > 0) {
                U unit = cast(DAYS);
                items.add(Item.of(days, unit));
            }
        } else {
            if (from + 2 < to) {
                extended = (period.charAt(from + 2) == ':');
            }
            Duration<?> dur = getAlternativeTimeFormat(extended).parse(period, from);
            long hours = dur.getPartialAmount(HOURS);
            if (hours > 0) {
                if (hours > 24) {
                    throw new ParseException(
                        "ISO-8601 prohibits hours-part > 24: " + period,
                        from);
                }
                U unit = cast(HOURS);
                items.add(Item.of(hours, unit));
            }
            long minutes = dur.getPartialAmount(MINUTES);
            if (minutes > 0) {
                if (minutes > 60) {
                    throw new ParseException(
                        "ISO-8601 prohibits minutes-part > 60: " + period,
                        from + 2 + (extended ? 1 : 0));
                }
                U unit = cast(MINUTES);
                items.add(Item.of(minutes, unit));
            }
            long seconds = dur.getPartialAmount(SECONDS);
            if (seconds > 0) {
                if (seconds > 60) {
                    throw new ParseException(
                        "ISO-8601 prohibits seconds-part > 60: " + period,
                        from + 4 + (extended ? 2 : 0));
                }
                U unit = cast(SECONDS);
                items.add(Item.of(seconds, unit));
            }
            long nanos = dur.getPartialAmount(NANOS);
            if (nanos > 0) {
                U unit = cast(NANOS);
                items.add(Item.of(nanos, unit));
            }
        }

    }

    private static Duration.Formatter<CalendarUnit> createAlternativeDateFormat(
        boolean extended,
        boolean ordinalStyle
    ) {

        String pattern;

        if (extended) {
            if (ordinalStyle) {
                pattern = "YYYY-DDD";
            } else {
                pattern = "YYYY-MM-DD";
            }
        } else {
            if (ordinalStyle) {
                pattern = "YYYYDDD";
            } else {
                pattern = "YYYYMMDD";
            }
        }

        return Duration.Formatter.ofPattern(CalendarUnit.class, pattern);

    }

    private static Duration.Formatter<CalendarUnit> getAlternativeDateFormat(
        boolean extended,
        boolean ordinalStyle
    ) {

        if (extended) {
            return (ordinalStyle ? CF_EXT_ORD : CF_EXT_CAL);
        } else {
            return (ordinalStyle ? CF_BAS_ORD : CF_BAS_CAL);
        }

    }

    private static Duration.Formatter<ClockUnit> createAlternativeTimeFormat(boolean extended) {

        String pattern = (
            extended
            ? "hh[:mm[:ss[,fffffffff]]]"
            : "hh[mm[ss[,fffffffff]]]"
        );

        return Duration.Formatter.ofPattern(ClockUnit.class, pattern);

    }

    private static Duration.Formatter<ClockUnit> getAlternativeTimeFormat(boolean extended) {

        return (extended ? TF_EXT : TF_BAS);

    }

    private static CalendarUnit parseDateSymbol(
        char c,
        String period,
        int index
    ) throws ParseException {

        switch (c) {
            case 'I':
                return MILLENNIA;
            case 'C':
                return CENTURIES;
            case 'E':
                return DECADES;
            case 'Y':
                return YEARS;
            case 'Q':
                return QUARTERS;
            case 'M':
                return MONTHS;
            case 'W':
                return WEEKS;
            case 'D':
                return DAYS;
            default:
                throw new ParseException(
                    "Symbol \'" + c + "\' not supported: " + period, index);
        }

    }

    private static ClockUnit parseTimeSymbol(
        char c,
        String period,
        int index
    ) throws ParseException {

        switch (c) {
            case 'H':
                return HOURS;
            case 'M':
                return MINUTES;
            case 'S':
                return SECONDS;
            default:
                throw new ParseException(
                    "Symbol \'" + c + "\' not supported: " + period, index);
        }

    }

    private static IsoDateUnit parseWeekBasedSymbol(
        char c,
        String period,
        int index
    ) throws ParseException {

        switch (c) {
            case 'Y':
                return CalendarUnit.weekBasedYears();
            case 'W':
                return WEEKS;
            case 'D':
                return DAYS;
            default:
                throw new ParseException(
                    "Symbol \'" + c + "\' not supported: " + period, index);
        }

    }

    private static <U extends ChronoUnit> ChronoUnit addParsedItem(
        ChronoUnit unit,
        ChronoUnit last, // optional
        long amount,
        String period,
        int index,
        List<Item<U>> items
    ) throws ParseException {

        if (
            (last == null)
            || (Double.compare(unit.getLength(), last.getLength()) < 0)
        ) {
            if (amount != 0) {
                U reified = cast(unit);
                items.add(Item.of(amount, reified));
            }
            return unit;
        } else if (Double.compare(unit.getLength(), last.getLength()) == 0) {
            throw new ParseException(
                "Duplicate unit items: " + period, index);
        } else {
            throw new ParseException(
                "Wrong order of unit items: " + period, index);
        }

    }

    private static long parseAmount(
        String period,
        String number,
        int index
    ) throws ParseException {

        try {
            return Long.parseLong(number);
        } catch (NumberFormatException nfe) {
            ParseException pe = new ParseException(period, index);
            pe.initCause(nfe);
            throw pe;
        }

    }

    /**
     * @serialData  Uses <a href="../../serialized-form.html#net.time4j.SPX">
     *              a dedicated serialization form</a> as proxy. The layout
     *              is bit-compressed. The first byte contains within the
     *              four most significant bits the type id {@code 6} and as
     *              least significant bit the value 1 if long should be used
     *              for transferring the item amounts (else using int). Then
     *              the data bytes for the duration items follow. The byte
     *              sequence optionally ends with the sign information.
     *
     * Schematic algorithm:
     *
     * <pre>
     *      boolean useLong = ...;
     *      byte header = (6 &lt;&lt; 4);
     *      if (useLong) header |= 1;
     *      out.writeByte(header);
     *      out.writeInt(getTotalLength().size());
     *      for (Item&lt;U&gt; item : getTotalLength()) {
     *          if (useLong) {
     *              out.writeLong(item.getAmount());
     *          } else {
     *              out.writeInt((int) item.getAmount());
     *          }
     *          out.writeObject(item.getUnit());
     *      }
     *      if (getTotalLength().size() &gt; 0) {
     *          out.writeBoolean(isNegative());
     *      }
     * </pre>
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.DURATION_TYPE);

    }

    /**
     * @serialData  Blocks because a serialization proxy is required.
     * @param       in      object input stream
     * @throws      InvalidObjectException (always)
     *
     */
    private void readObject(ObjectInputStream in)
        throws IOException {

        throw new InvalidObjectException("Serialization proxy required.");

    }

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Builder class for constructing a duration conforming to ISO-8601
     * which consists of years, months, days and any wall time units. </p>
     *
     * <p>The week unit is not possible in builder because this unit should
     * be stand-alone according to ISO-8601. A week-based duration can be
     * created by expression {@code Duration.of(amount, CalendarUnit.WEEKS)}
     * however. </p>
     *
     * <p>A builder instance must be created by {@link Duration#ofPositive()}
     * or {@link Duration#ofNegative()}. Note that the builder is only be
     * designed for single-thread-environments that is always creating a
     * new builder instance per thread. </p>
     */
    /*[deutsch]
     * <p>Hilfsobjekt zum Bauen einer ISO-konformen Zeitspanne bestehend aus
     * Jahren, Monaten, Tagen und allen Uhrzeiteinheiten. </p>
     *
     * <p>Lediglich die Wocheneinheit ist ausgenommen, da eine wochenbasierte
     * Zeitspanne nach dem ISO-Standard f&uuml;r sich alleine stehen sollte.
     * Eine wochenbasierte Zeitspanne kann auf einfache Weise mit dem Ausdruck
     * {@code Duration.of(amount, CalendarUnit.WEEKS)} erzeugt werden. </p>
     *
     * <p>Eine Instanz wird mittels {@link Duration#ofPositive()} oder
     * {@link Duration#ofNegative()} erzeugt. Diese Instanz ist nur zur
     * lokalen Verwendung in einem Thread gedacht, da keine Thread-Sicherheit
     * gegeben ist. </p>
     */
    public static class Builder {

        //~ Instanzvariablen ----------------------------------------------

        private final List<Item<IsoUnit>> items;
        private final boolean negative;

        private boolean millisSet = false;
        private boolean microsSet = false;
        private boolean nanosSet = false;

        //~ Konstruktoren -------------------------------------------------

        /**
         * <p>Konstruiert ein Hilfsobjekt zum Bauen einer Zeitspanne. </p>
         *
         * @param   negative    Is a negative duration asked for?
         */
        Builder(boolean negative) {
            super();

            this.items = new ArrayList<Item<IsoUnit>>(10);
            this.negative = negative;

        }

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Adds a year item. </p>
         *
         * @param   num     count of years {@code >= 0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the argument is negative
         * @throws  IllegalStateException if already called
         * @see     CalendarUnit#YEARS
         */
        /*[deutsch]
         * <p>Erzeugt eine L&auml;nge in Jahren. </p>
         *
         * @param   num     count of years {@code >= 0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the argument is negative
         * @throws  IllegalStateException if already called
         * @see     CalendarUnit#YEARS
         */
        public Builder years(int num) {

            this.set(num, YEARS);
            return this;

        }

        /**
         * <p>Adds a month item. </p>
         *
         * @param   num     count of months {@code >= 0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the argument is negative
         * @throws  IllegalStateException if already called
         * @see     CalendarUnit#MONTHS
         */
        /*[deutsch]
         * <p>Erzeugt eine L&auml;nge in Monaten. </p>
         *
         * @param   num     count of months {@code >= 0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the argument is negative
         * @throws  IllegalStateException if already called
         * @see     CalendarUnit#MONTHS
         */
        public Builder months(int num) {

            this.set(num, MONTHS);
            return this;

        }

        /**
         * <p>Adds a day item. </p>
         *
         * @param   num     count of days {@code >= 0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the argument is negative
         * @throws  IllegalStateException if already called
         * @see     CalendarUnit#DAYS
         */
        /*[deutsch]
         * <p>Erzeugt eine L&auml;nge in Tagen. </p>
         *
         * @param   num     count of days {@code >= 0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the argument is negative
         * @throws  IllegalStateException if already called
         * @see     CalendarUnit#DAYS
         */
        public Builder days(int num) {

            this.set(num, DAYS);
            return this;

        }

        /**
         * <p>Adds a hour item. </p>
         *
         * @param   num     count of hours {@code >= 0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the argument is negative
         * @throws  IllegalStateException if already called
         * @see     ClockUnit#HOURS
         */
        /*[deutsch]
         * <p>Erzeugt eine L&auml;nge in Stunden. </p>
         *
         * @param   num     count of hours {@code >= 0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the argument is negative
         * @throws  IllegalStateException if already called
         * @see     ClockUnit#HOURS
         */
        public Builder hours(int num) {

            this.set(num, HOURS);
            return this;

        }

        /**
         * <p>Adds a minute item. </p>
         *
         * @param   num     count of minutes {@code >= 0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the argument is negative
         * @throws  IllegalStateException if already called
         * @see     ClockUnit#MINUTES
         */
        /*[deutsch]
         * <p>Erzeugt eine L&auml;nge in Minuten. </p>
         *
         * @param   num     count of minutes {@code >= 0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the argument is negative
         * @throws  IllegalStateException if already called
         * @see     ClockUnit#MINUTES
         */
        public Builder minutes(int num) {

            this.set(num, MINUTES);
            return this;

        }

        /**
         * <p>Adds a second item. </p>
         *
         * @param   num     count of seconds {@code >= 0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the argument is negative
         * @throws  IllegalStateException if already called
         * @see     ClockUnit#SECONDS
         */
        /*[deutsch]
         * <p>Erzeugt eine L&auml;nge in Sekunden. </p>
         *
         * @param   num     count of seconds {@code >= 0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the argument is negative
         * @throws  IllegalStateException if already called
         * @see     ClockUnit#SECONDS
         */
        public Builder seconds(int num) {

            this.set(num, SECONDS);
            return this;

        }

        /**
         * <p>Adds a millisecond item. </p>
         *
         * <p>The argument will automatically be normalized to nanoseconds. </p>
         *
         * @param   num     count of milliseconds {@code >= 0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the argument is negative
         * @throws  IllegalStateException if already called
         */
        /*[deutsch]
         * <p>Erzeugt eine L&auml;nge in Millisekunden. </p>
         *
         * <p>Es wird eine Normalisierung durchgef&uuml;hrt, indem das Argument
         * mit dem Faktor {@code 1} Million multipliziert und in Nanosekunden
         * gespeichert wird. </p>
         *
         * @param   num     count of milliseconds {@code >= 0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the argument is negative
         * @throws  IllegalStateException if already called
         */
        public Builder millis(int num) {

            this.millisCalled();
            this.update(num, MIO);
            return this;

        }

        /**
         * <p>Adds a microsecond item. </p>
         *
         * <p>The argument will automatically be normalized to nanoseconds. </p>
         *
         * @param   num     count of microseconds {@code >= 0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the argument is negative
         * @throws  IllegalStateException if already called
         */
        /*[deutsch]
         * <p>Erzeugt eine L&auml;nge in Mikrosekunden. </p>
         *
         * <p>Es wird eine Normalisierung durchgef&uuml;hrt, indem das Argument
         * mit dem Faktor {@code 1000} multipliziert und in Nanosekunden
         * gespeichert wird. </p>
         *
         * @param   num     count of microseconds {@code >= 0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the argument is negative
         * @throws  IllegalStateException if already called
         */
        public Builder micros(int num) {

            this.microsCalled();
            this.update(num, 1000L);
            return this;

        }

        /**
         * <p>Adds a nanosecond item. </p>
         *
         * @param   num     count of nanoseconds {@code >= 0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the argument is negative
         * @throws  IllegalStateException if already called
         * @see     ClockUnit#NANOS
         */
        /*[deutsch]
         * <p>Erzeugt eine L&auml;nge in Nanosekunden. </p>
         *
         * @param   num     count of nanoseconds {@code >= 0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the argument is negative
         * @throws  IllegalStateException if already called
         * @see     ClockUnit#NANOS
         */
        public Builder nanos(int num) {

            this.nanosCalled();
            this.update(num, 1L);
            return this;

        }

        /**
         * <p>Creates a new duration conforming to ISO-8601. </p>
         *
         * @return  new {@code Duration}
         */
        /*[deutsch]
         * <p>Erzeugt eine neue ISO-konforme Zeitspanne. </p>
         *
         * @return  new {@code Duration}
         */
        public Duration<IsoUnit> build() {

            if (this.items.isEmpty()) {
                throw new IllegalStateException("Not set any amount and unit.");
            }

            return new Duration<IsoUnit>(
                this.items,
                this.negative
            );

        }

        private Builder set(
            long amount,
            IsoUnit unit
        ) {

            for (int i = 0, n = this.items.size(); i < n; i++) {
                if (this.items.get(i).getUnit() == unit) {
                    throw new IllegalStateException(
                        "Already registered: " + unit);
                }
            }

            if (amount != 0) {
                Item<IsoUnit> item = Item.of(amount, unit);
                this.items.add(item);
            }

            return this;

        }

        private void update(
            long amount,
            long factor
        ) {

            if (amount >= 0) {
                IsoUnit unit = NANOS;

                for (int i = this.items.size() - 1; i >= 0; i--) {
                    Item<IsoUnit> item = this.items.get(i);
                    if (item.getUnit().equals(NANOS)) {
                        this.items.set(
                            i,
                            Item.of(
                                MathUtils.safeAdd(
                                    MathUtils.safeMultiply(amount, factor),
                                    item.getAmount()
                                ),
                                unit
                            )
                        );
                        return;
                    }
                }

                if (amount != 0) {
                    this.items.add(
                        Item.of(
                            MathUtils.safeMultiply(amount, factor),
                            unit
                        )
                    );
                }

            } else {
                throw new IllegalArgumentException(
                    "Illegal negative amount: " + amount);
            }

        }

        private void millisCalled() {

            if (this.millisSet) {
                throw new IllegalStateException(
                    "Called twice for: " + MILLIS.name());
            }

            this.millisSet = true;

        }

        private void microsCalled() {

            if (this.microsSet) {
                throw new IllegalStateException(
                    "Called twice for: " + MICROS.name());
            }

            this.microsSet = true;

        }

        private void nanosCalled() {

            if (this.nanosSet) {
                throw new IllegalStateException(
                    "Called twice for: " + NANOS.name());
            }

            this.nanosSet = true;

        }

    }

    private static class ZonalMetric
        implements TimeMetric<IsoUnit, Duration<IsoUnit>> {

        //~ Instanzvariablen ----------------------------------------------

        private final Timezone tz;
        private final TimeMetric<IsoUnit, Duration<IsoUnit>> metric;

        //~ Konstruktoren -------------------------------------------------

        private ZonalMetric(
            Timezone tz,
            IsoUnit... units
        ) {
            super();

            if (tz == null) {
                throw new NullPointerException("Missing timezone.");
            }

            this.tz = tz;
            this.metric = new Metric<IsoUnit>(units);

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public <T extends TimePoint<? super IsoUnit, T>>
        Duration<IsoUnit> between(
            T start,
            T end
        ) {

            T t1 = start;
            T t2 = end;
            boolean negative = false;

            if (start.compareTo(end) > 0) {
                t1 = end;
                t2 = start;
                negative = true;
            }

            int o1 = this.getOffset(t1);
            int o2 = this.getOffset(t2);
            t2 = t2.plus(o1 - o2, SECONDS);
            Duration<IsoUnit> duration = this.metric.between(t1, t2);

            if (negative) {
                duration = duration.inverse();
            }

            return duration;

        }

        private int getOffset(ChronoEntity<?> entity) {

            return this.tz.getStrategy().getOffset(
                entity.get(PlainDate.COMPONENT),
                entity.get(PlainTime.COMPONENT),
                this.tz
            ).getIntegralAmount();

        }

    }

    /**
     * <p>Non-localized and user-defined format for durations based on a
     * pattern containing some standard symbols and literals. </p>
     *
     * <p>Note: For storing purposes users should normally use the canonical
     * or ISO- or XML-representation of a duration, not this custom format.
     * Otherwise, if users want a localized output then the class
     * {@link PrettyTime} is usually the best choice. This class is mainly
     * designed for handling non-standardized formats. </p>
     *
     * <p>First example (parsing a Joda-Time-Period using a max width of 2): </p>
     *
     * <pre>
     *  Duration.Formatter&lt;IsoUnit&gt; f = Duration.Formatter.ofJodaStyle();
     *  Duration&lt;?&gt; dur = f.parse("P-2Y-15DT-30H-5M");
     *  System.out.println(dur); // output: -P2Y15DT30H5M
     * </pre>
     *
     * <p>Second example (printing a wall-time-like duration): </p>
     *
     * <pre>
     *  Duration.Formatter&lt;ClockUnit&gt; f =
     *      Duration.Formatter.ofPattern(ClockUnit.class, "+hh:mm:ss");
     *  String s = f.print(Duration.ofClockUnits(27, 30, 5));
     *  System.out.println(s); // output: +27:30:05
     * </pre>
     *
     * @param   <U> generic type of time units
     * @since   1.2
     * @see     Duration#toString()
     * @see     Duration#parsePeriod(String)
     * @see     #ofPattern(Class, String)
     */
    /*[deutsch]
     * <p>Nicht-lokalisiertes benutzerdefiniertes Dauerformat, das auf
     * Symbolmustern beruht. </p>
     *
     * <p>Hinweis: Zum Speichern sollten Anwender normalerweise die kanonische
     * Form oder die ISO- oder die XML-Form einer Dauer verwenden, nicht dieses
     * benutzerdefinierte Format. Wird andererseits eine lokalisierte Ausgabe
     * gew&uuml;nscht, dann ist die Klasse {@link PrettyTime} erste Wahl.
     * Diese Klasse ist vor allem f&uuml;r die Verarbeitung von nicht-
     * standardisierten Formaten zust&auml;ndig. </p>
     *
     * <p>Beispiel 1 (Analyse einer Joda-Time-Periode): </p>
     *
     * <pre>
     *  Duration.Formatter&lt;IsoUnit&gt; f = Duration.Formatter.ofJodaStyle();
     *  Duration&lt;?&gt; dur = f.parse("P-2Y-15DT-30H-5M");
     *  System.out.println(dur); // Ausgabe: -P2Y15DT30H5M
     * </pre>
     *
     * <p>Zweites Beispiel (Ausgabe einer uhrzeit&auml;hnlichen Dauer): </p>
     *
     * <pre>
     *  Duration.Formatter&lt;ClockUnit&gt; f =
     *      Duration.Formatter.ofPattern(ClockUnit.class, "+hh:mm:ss");
     *  String s = f.print(Duration.ofClockUnits(27, 30, 5));
     *  System.out.println(s); // Ausgabe: +27:30:05
     * </pre>
     *
     * @param   <U> generic type of time units
     * @since   1.2
     * @see     Duration#toString()
     * @see     Duration#parsePeriod(String)
     * @see     #ofPattern(Class, String)
     */
    public static final class Formatter<U extends IsoUnit>
        extends TimeSpanFormatter<U, Duration<U>> {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final String JODA_PATTERN =
            "'P'[-#################Y'Y'][-#################M'M'][-#################W'W'][-#################D'D']"
            + "['T'[-#################h'H'][-#################m'M'][-#################s'S'[.fffffffff]]]";

        //~ Konstruktoren -------------------------------------------------

        private Formatter(
            Class<U> type,
            String pattern
        ) {
            super(type, pattern);

        }

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Handles Joda-Time-style-patterns which in general follow XML-schema
         * - with the exception of sign handling. </p>
         *
         * <p>The sign handling of Joda-Time allows and even enforces in contrast
         * to XML-schema negative signs not before the P-symbol but for every
         * single duration item repeatedly. Warning: Mixed signs are never supported
         * by Time4J. </p>
         *
         * @return  new formatter instance for parsing Joda-Style period expressions
         * @since   3.0
         * @see     #ofPattern(Class, String)
         */
        /*[deutsch]
         * <p>Behandelt Joda-Time-Stil-Formatmuster, die im allgemeinen XML-Schema
         * folgen - mit der Ausnahme der Vorzeichenbehandlung. </p>
         *
         * <p>Die Vorzeichenbehandlung von Joda-Time erlaubt und erzwingt im Kontrast
         * zu XML-Schema negative Vorzeichen nicht vor dem P-Symbol, sondern wiederholt
         * f&uuml;r jedes einzelne Dauerelement. Warnung: Gemischte Vorzeichen werden
         * von Time4J dennoch nicht unterst&uuml;tzt. </p>
         *
         * @return  new formatter instance for parsing Joda-Style period expressions
         * @since   3.0
         * @see     #ofPattern(Class, String)
         */
        public static Formatter<IsoUnit> ofJodaStyle() {

            return ofPattern(IsoUnit.class, JODA_PATTERN);

        }

        /**
         * <p>Equivalent to {@code ofPattern(IsoUnit.class, pattern)}. </p>
         *
         * @param   pattern format pattern
         * @return  new formatter instance
         * @throws  IllegalArgumentException in any case of pattern inconsistencies or failures
         * @since   1.2
         * @see     #ofPattern(Class, String)
         */
        /*[deutsch]
         * <p>&Auml;quivalent zu {@code ofPattern(IsoUnit.class, pattern)}. </p>
         *
         * @param   pattern format pattern
         * @return  new formatter instance
         * @throws  IllegalArgumentException in any case of pattern inconsistencies or failures
         * @since   1.2
         * @see     #ofPattern(Class, String)
         */
        public static Formatter<IsoUnit> ofPattern(String pattern) {

            return ofPattern(IsoUnit.class, pattern);

        }

        /**
         * <p>Constructs a new instance of duration formatter. </p>
         *
         * <p>Uses a pattern with symbols as followed: <br>&nbsp;</p>
         *
         * <table border="1">
         *  <caption>Legend</caption>
         *  <tr><th>Symbol</th><th>Description</th></tr>
         *  <tr><td>+</td><td>sign of duration, printing + or -</td></tr>
         *  <tr><td>-</td><td>sign of duration, printing only -</td></tr>
         *  <tr><td>I</td><td>{@link CalendarUnit#MILLENNIA}</td></tr>
         *  <tr><td>C</td><td>{@link CalendarUnit#CENTURIES}</td></tr>
         *  <tr><td>E</td><td>{@link CalendarUnit#DECADES}</td></tr>
         *  <tr><td>Y</td><td>{@link CalendarUnit#YEARS}</td></tr>
         *  <tr><td>Q</td><td>{@link CalendarUnit#QUARTERS}</td></tr>
         *  <tr><td>M</td><td>{@link CalendarUnit#MONTHS}</td></tr>
         *  <tr><td>W</td><td>{@link CalendarUnit#WEEKS}</td></tr>
         *  <tr><td>D</td><td>{@link CalendarUnit#DAYS}</td></tr>
         *  <tr><td>h</td><td>{@link ClockUnit#HOURS}</td></tr>
         *  <tr><td>m</td><td>{@link ClockUnit#MINUTES}</td></tr>
         *  <tr><td>s</td><td>{@link ClockUnit#SECONDS}</td></tr>
         *  <tr><td>,</td><td>decimal separator, comma is preferred</td></tr>
         *  <tr><td>.</td><td>decimal separator, dot is preferred</td></tr>
         *  <tr><td>f</td>
         *    <td>{@link ClockUnit#NANOS} as fraction, (1-9) chars</td></tr>
         *  <tr><td>'</td><td>apostroph, for escaping literal chars</td></tr>
         *  <tr><td>[]</td><td>optional section</td></tr>
         *  <tr><td>{}</td><td>section with plural forms, since v2.0</td></tr>
         *  <tr><td>#</td><td>placeholder for an optional digit, since v3.0</td></tr>
         *  <tr><td>|</td><td>joins two parsing sections by or-logic, since v3.26/4.22</td></tr>
         * </table>
         *
         * <p>All letters in range a-z and A-Z are always reserved chars
         * and must be escaped by apostrophes for interpretation as literals.
         * If such a letter is repeated then the count of symbols controls
         * the minimum width for formatted output. Such a minimum width also
         * reserves this area for parsing of any preceding item. If necessary a
         * number (of units) will be padded from left with the zero digit. The
         * unit symbol (with exception of &quot;f&quot;) can be preceded by
         * any count of char &quot;#&quot; (&gt;= 0). The sum of min width and
         * count of #-chars define the maximum width for formatted output and
         * parsing.</p>
         *
         * <p>Optional sections let the parser be error-tolerant and continue
         * with the next section in case of errors. Since v2.3: During printing,
         * an optional section will only be printed if there is any non-zero
         * part. </p>
         *
         * <p><strong>Enhancement since version v2.0: plural forms</strong></p>
         *
         * <p>Every expression inside curly brackets represents a combination
         * of amount, separator and pluralized unit name and has following
         * syntax: </p>
         *
         * <p>{[symbol]:[separator]:[locale]:[CATEGORY=LITERAL][:...]}</p>
         *
         * <p>The symbol is one of following chars:
         * I, C, E, Y, Q, M, W, D, h, m, s, f (legend see table above)</p>
         *
         * <p>Afterwards the definition of separator chars follows. The
         * empty separator (represented by zero space between colons) is
         * permitted, too. The next section denotes the locale necessary
         * for determination of suitable plural rules. The form
         * [language]-[country]-[variant] can be used, for example
         * &quot;en-US&quot; or &quot;en_US&quot;. At least the language
         * must be present. The underscore is an acceptable alternative
         * for the minus-sign. Finally there must be a sequence of
         * name-value-pairs in the form CATEGORY=LITERAL. Every category
         * label must be the name of a {@link net.time4j.format.PluralCategory plural category}.
         * The category OTHER must exist. Example: </p>
         *
         * <pre>
         *  Duration.Formatter&lt;CalendarUnit&gt; formatter =
         *      Duration.Formatter.ofPattern(
         *          CalendarUnit.class,
         *          &quot;{D: :en:ONE=day:OTHER=days}&quot;);
         *  String s = formatter.format(Duration.of(3, DAYS));
         *  System.out.println(s); // output: 3 days
         * </pre>
         *
         * <p><strong>Enhancement since version v3.0: numerical placeholders</strong></p>
         *
         * <p>Before version v3.0, the maximum numerical width was always 18. Now it is
         * the sum of min width and the count of preceding #-chars. Example: </p>
         *
         * <pre>
         *  Duration.Formatter&lt;CalendarUnit&gt; formatter1 =
         *      Duration.Formatter.ofPattern(CalendarUnit.class, &quot;D&quot;);
         *  formatter1.format(Duration.of(123, DAYS)); throws IllegalArgumentException
         *
         *  Duration.Formatter&lt;CalendarUnit&gt; formatter2 =
         *      Duration.Formatter.ofPattern(CalendarUnit.class, &quot;##D&quot;);
         *  String s = formatter2.format(Duration.of(123, DAYS));
         *  System.out.println(s); // output: 123
         * </pre>
         *
         * <p><strong>Enhancement since version v3.26/4.22: or-logic</strong></p>
         *
         * <p>The character &quot;|&quot; starts a new section which will not be used for printing
         * but parsing in case of preceding errors. For example, following pattern enables parsing
         * a duration in days for two different languages: </p>
         *
         * <pre>
         *  &quot;{D: :en:ONE=day:OTHER=days}|{D: :de:ONE=Tag:OTHER=Tage}&quot;
         * </pre>
         *
         * @param   <U>     generic unit type
         * @param   type    reified unit type
         * @param   pattern format pattern
         * @return  new formatter instance
         * @throws  IllegalArgumentException in any case of pattern inconsistencies or failures
         * @since   1.2
         */
        /*[deutsch]
         * <p>Konstruiert eine neue Formatinstanz. </p>
         *
         * <p>Benutzt ein Formatmuster mit Symbolen wie folgt: <br>&nbsp;</p>
         *
         * <table border="1">
         *  <caption>Legende</caption>
         *  <tr><th>Symbol</th><th>Beschreibung</th></tr>
         *  <tr><td>+</td><td>Vorzeichen der Dauer, gibt + oder - aus</td></tr>
         *  <tr><td>-</td><td>Vorzeichen der Dauer, gibt nur - aus</td></tr>
         *  <tr><td>I</td><td>{@link CalendarUnit#MILLENNIA}</td></tr>
         *  <tr><td>C</td><td>{@link CalendarUnit#CENTURIES}</td></tr>
         *  <tr><td>E</td><td>{@link CalendarUnit#DECADES}</td></tr>
         *  <tr><td>Y</td><td>{@link CalendarUnit#YEARS}</td></tr>
         *  <tr><td>Q</td><td>{@link CalendarUnit#QUARTERS}</td></tr>
         *  <tr><td>M</td><td>{@link CalendarUnit#MONTHS}</td></tr>
         *  <tr><td>W</td><td>{@link CalendarUnit#WEEKS}</td></tr>
         *  <tr><td>D</td><td>{@link CalendarUnit#DAYS}</td></tr>
         *  <tr><td>h</td><td>{@link ClockUnit#HOURS}</td></tr>
         *  <tr><td>m</td><td>{@link ClockUnit#MINUTES}</td></tr>
         *  <tr><td>s</td><td>{@link ClockUnit#SECONDS}</td></tr>
         *  <tr><td>,</td><td>Dezimaltrennzeichen, vorzugsweise Komma</td></tr>
         *  <tr><td>.</td><td>Dezimaltrennzeichen, vorzugsweise Punkt</td></tr>
         *  <tr><td>f</td>
         *    <td>{@link ClockUnit#NANOS} als Bruchteil, (1-9) Zeichen</td></tr>
         *  <tr><td>'</td><td>Apostroph, zum Markieren von Literalen</td></tr>
         *  <tr><td>[]</td><td>optionaler Abschnitt</td></tr>
         *  <tr><td>{}</td><td>Abschnitt mit Pluralformen, seit v2.0</td></tr>
         *  <tr><td>#</td><td>numerischer Platzhalter, seit v3.0</td></tr>
         *  <tr><td>|</td><td>verbindet zwei Abschnitte per oder-Logik, seit v3.26/4.22</td></tr>
         * </table>
         *
         * <p>Alle Buchstaben im Bereich a-z und A-Z sind grunds&auml;tzlich
         * reservierte Zeichen und m&uuml;ssen als Literale in Apostrophe
         * gefasst werden. Wird ein Buchstabensymbol mehrfach wiederholt,
         * dann regelt die Anzahl der Symbole die Mindestbreite in der formatierten
         * Ausgabe. Solch eine Mindestbreite reserviert auch das zugeh&ouml;rige Element,
         * wenn vorangehende Dauerelemente interpretiert werden. Bei Bedarf wird eine
         * Zahl (von Einheiten) von links mit der Nullziffer aufgef&uuml;llt. Ein
         * Einheitensymbol kann eine beliebige Zahl von numerischen Platzhaltern
         * &quot;#&quot; vorangestellt haben (&gt;= 0). Die Summe aus minimaler Breite
         * und der Anzahl der #-Zeichen definiert die maximale Breite, die ein
         * Dauerelement numerisch haben darf. </p>
         *
         * <p>Optionale Abschnitte regeln, da&szlig; der Interpretationsvorgang
         * bei Fehlern nicht sofort abbricht, sondern mit dem n&auml;chsten
         * Abschnitt fortsetzt und den fehlerhaften Abschnitt ignoriert. Seit
         * v2.3 gilt auch, da&szlig; optionale Abschnitte nur dann etwas
         * ausgeben, wenn es darin irgendeine von {code 0} verschiedene
         * Dauerkomponente gibt. </p>
         *
         * <p><strong>Erweiterung seit Version v2.0: Pluralformen</strong></p>
         *
         * <p>Jeder in geschweifte Klammern gefasste Ausdruck symbolisiert
         * eine Kombination aus Betrag, Trennzeichen und pluralisierten
         * Einheitsnamen und hat folgende Syntax: </p>
         *
         * <p>{[symbol]:[separator]:[locale]:[CATEGORY=LITERAL][:...]}</p>
         *
         * <p>Das Symbol ist eines von folgenden Zeichen: I, C, E, Y, Q, M,
         * W, D, h, m, s, f (Bedeutung siehe Tabelle)</p>
         *
         * <p>Danach folgen Trennzeichen, abgetrennt durch einen Doppelpunkt.
         * Eine leere Zeichenkette ist auch zul&auml;ssig. Danach folgt eine
         * Lokalisierungsangabe zum Bestimmen der Pluralregeln in der Form
         * [language]-[country]-[variant], zum Beispiel &quot;de-DE&quot; oder
         * &quot;en_US&quot;. Mindestens mu&szlig; die Sprache vorhanden sein.
         * Der Unterstrich wird neben dem Minuszeichen ebenfalls interpretiert.
         * Schlie&szlig;lich folgt eine Sequenz von Name-Wert-Paaren in
         * der Form CATEGORY=LITERAL. Jede Kategoriebezeichnung ist der Name
         * einer {@link PluralCategory Pluralkategorie}. Die Kategorie OTHER
         * mu&szlig; enthalten sein. Beispiel: </p>
         *
         * <pre>
         *  Duration.Formatter&lt;CalendarUnit&gt; formatter =
         *      Duration.Formatter.ofPattern(
         *          CalendarUnit.class,
         *          &quot;{D: :de:ONE=Tag:OTHER=Tage}&quot;);
         *  String s = formatter.format(Duration.of(3, DAYS));
         *  System.out.println(s); // output: 3 Tage
         * </pre>
         *
         * <p><strong>Erweiterung seit Version v3.0: numerische Platzhalter</strong></p>
         *
         * <p>Vor Version 3.0 war die maximale numerische Breite immer 18 Zeichen lang,
         * nun immer die Summe aus minimaler Breite und der Anzahl der vorangehenden
         * #-Zeichen. Beispiel: </p>
         *
         * <pre>
         *  Duration.Formatter&lt;CalendarUnit&gt; formatter1 =
         *      Duration.Formatter.ofPattern(CalendarUnit.class, &quot;D&quot;);
         *  formatter1.format(Duration.of(123, DAYS)); throws IllegalArgumentException
         *
         *  Duration.Formatter&lt;CalendarUnit&gt; formatter2 =
         *      Duration.Formatter.ofPattern(CalendarUnit.class, &quot;##D&quot;);
         *  String s = formatter2.format(Duration.of(123, DAYS));
         *  System.out.println(s); // output: 123
         * </pre>
         *
         * <p><strong>Erweiterung seit Version v3.26/4.22: oder-Logik</strong></p>
         *
         * <p>Das Zeichen &quot;|&quot; beginnt einen neuen Abschnitt, der nicht zur Textausgabe,
         * aber beim Interpretieren (Parsen) verwendet wird, falls im vorangehenden Abschnitt
         * Fehler auftraten. Zum Beispiel erm&ouml;glicht folgendes Muster das Interpretieren
         * einer Dauer in Tagen f&uuml;r zwei verschiedene Sprachen: </p>
         *
         * <pre>
         *  &quot;{D: :en:ONE=day:OTHER=days}|{D: :de:ONE=Tag:OTHER=Tage}&quot;
         * </pre>
         *
         * @param   <U>     generic unit type
         * @param   type    reified unit type
         * @param   pattern format pattern
         * @return  new formatter instance
         * @throws  IllegalArgumentException in any case of pattern inconsistencies or failures
         * @since   1.2
         */
        public static <U extends IsoUnit> Formatter<U> ofPattern(
            Class<U> type,
            String pattern
        ) {

            return new Formatter<U>(type, pattern);

        }

        @Override
        protected Duration<U> convert(Map<U, Long> map, boolean negative) {

            return Duration.create(map, negative);

        }

        @SuppressWarnings("unchecked")
        @Override
        protected U getUnit(char symbol) {

            IsoUnit unit;

            switch (symbol) {
                case 'I':
                    unit = MILLENNIA;
                    break;
                case 'C':
                    unit = CENTURIES;
                    break;
                case 'E':
                    unit = DECADES;
                    break;
                case 'Y':
                    unit = YEARS;
                    break;
                case 'Q':
                    unit = QUARTERS;
                    break;
                case 'M':
                    unit = MONTHS;
                    break;
                case 'W':
                    unit = WEEKS;
                    break;
                case 'D':
                    unit = DAYS;
                    break;
                case 'h':
                    unit = HOURS;
                    break;
                case 'm':
                    unit = MINUTES;
                    break;
                case 's':
                    unit = SECONDS;
                    break;
                case 'f':
                    unit = NANOS;
                    break;
                default:
                    throw new IllegalArgumentException(
                        "Unsupported pattern symbol: " + symbol);
            }

            try {
                return (U) unit;
            } catch (ClassCastException cce) {
                throw new IllegalArgumentException(cce.getMessage(), cce);
            }

        }

    }

    private static class Metric<U extends IsoUnit>
        extends AbstractMetric<U, Duration<U>> {

        //~ Konstruktoren -------------------------------------------------

        private Metric(U... units) {
            super((units.length > 1), units);

        }

        //~ Methoden ------------------------------------------------------

        @Override
        protected Duration<U> createEmptyTimeSpan() {

            return ofZero();

        }

        @Override
        protected Duration<U> createTimeSpan(
            List<TimeSpan.Item<U>> items,
            boolean negative
        ) {

            return new Duration<U>(items, negative);

        }

        @Override
        @SuppressWarnings("unchecked")
        protected TimeSpan.Item<U> resolve(TimeSpan.Item<U> item) {

            IsoUnit unit = item.getUnit();

            if (unit.equals(ClockUnit.MILLIS)) {
                return Item.of(MathUtils.safeMultiply(item.getAmount(), 1000000L), (U) ClockUnit.NANOS);
            } else if (unit.equals(ClockUnit.MICROS)) {
                return Item.of(MathUtils.safeMultiply(item.getAmount(), 1000L), (U) ClockUnit.NANOS);
            }

            return item;

        }

    }

    private static class LengthComparator
        <U extends IsoUnit, T extends TimePoint<? super U, T>>
        implements Comparator<Duration<U>> {

        //~ Instanzvariablen ----------------------------------------------

        private final T base;

        //~ Konstruktoren -------------------------------------------------

        private LengthComparator(T base) {
            super();

            if (base == null) {
                throw new NullPointerException("Missing base time point.");
            }

            this.base = base;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public int compare(
            Duration<U> d1,
            Duration<U> d2
        ) {

            boolean sign1 = d1.isNegative();
            boolean sign2 = d2.isNegative();

            if (sign1 && !sign2) {
                return -1;
            } else if (!sign1 && sign2) {
                return 1;
            } else if (d1.isEmpty() && d2.isEmpty()) {
                return 0;
            }

            return this.base.plus(d1).compareTo(this.base.plus(d2));

        }

    }

    private static class ApproximateNormalizer
        implements Normalizer<IsoUnit> {

        //~ Instanzvariablen ----------------------------------------------

        private final boolean daysToWeeks;
        private final int steps;
        private final ClockUnit unit;

        //~ Konstruktoren -------------------------------------------------

        ApproximateNormalizer(boolean daysToWeeks) {
            super();

            this.daysToWeeks = daysToWeeks;
            this.steps = 1;
            this.unit = null;

        }

        ApproximateNormalizer(
            int steps,
            ClockUnit unit
        ) {
            super();

            if (steps < 1) {
                throw new IllegalArgumentException(
                    "Step width is not positive: " + steps);
            } else if (unit.compareTo(SECONDS) > 0) {
                throw new IllegalArgumentException("Unsupported unit.");
            }

            this.daysToWeeks = false;
            this.steps = steps;
            this.unit = unit;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Duration<IsoUnit> normalize(TimeSpan<? extends IsoUnit> dur) {

            double total = 0.0;
            IsoUnit umax = null;

            for (int i = 0, n = dur.getTotalLength().size(); i < n; i++) {
                Item<? extends IsoUnit> item = dur.getTotalLength().get(i);
                total += (item.getAmount() * item.getUnit().getLength());
                if ((umax == null) && (item.getAmount() > 0)) {
                    umax = item.getUnit();
                }
            }

            if (umax == null) {
                return Duration.ofZero();
            }

            // rounding applied
            IsoUnit u = ((this.unit == null) ? umax : this.unit);
            double len = u.getLength() * this.steps;
            long value = (long) (total + (len / 2.0)); // half-up
            int lint = (int) Math.floor(len);
            value = MathUtils.floorDivide(value, lint) * lint;

            // standard normalization
            int y, m, w, d, h, min = 0, s = 0;

            y = safeCast(value / YEARS.getLength());
            value -= y * YEARS.getLength();
            m = safeCast(value / MONTHS.getLength());
            value -= m * MONTHS.getLength();
            w = safeCast(value / WEEKS.getLength());
            value -= w * WEEKS.getLength();
            d = safeCast(value / DAYS.getLength());
            value -= d * DAYS.getLength();
            h = safeCast(value / HOURS.getLength());

            if (HOURS.equals(this.unit)) {
                h = (h / this.steps);
                h *= this.steps;
            } else {
                value -= h * HOURS.getLength();
                min = safeCast(value / MINUTES.getLength());
                if (MINUTES.equals(this.unit)) {
                    min = (min / this.steps);
                    min *= this.steps;
                } else { // seconds
                    value -= min * MINUTES.getLength();
                    s = safeCast(value / SECONDS.getLength());
                    s = (s / this.steps);
                    s *= this.steps;
                }
            }

            d += w * 7;
            w = 0;

            // special case of max unit
            if (this.unit == null) {
                if (y > 0) {
                    m = d = h = min = s = 0;
                } else if (m > 0) {
                    d = h = min = s = 0;
                } else if (d > 0) {
                    if ((d >= 7) && this.daysToWeeks) {
                        w = (int) ((d + 3.5) / 7);
                        d = 0;
                    }
                    h = min = s = 0;
                } else if (h > 0) {
                    min = s = 0;
                } else if (min > 0) {
                    s = 0;
                }
                if (w > 0) {
                    IsoUnit weekUnit = CalendarUnit.WEEKS;
                    return Duration.of(dur.isNegative() ? -w : w, weekUnit);
                }
            }

            // build
            Duration<IsoUnit> duration =
                Duration.ofPositive()
                .years(y).months(m).days(d)
                .hours(h).minutes(min).seconds(s).build();

            if (dur.isNegative()) {
                duration = duration.inverse();
            }

            return duration;

        }

        private static int safeCast(double num) {

            if (num < Integer.MIN_VALUE || num > Integer.MAX_VALUE) {
                throw new ArithmeticException("Out of range: " + num);
            } else {
                return (int) num;
            }

        }

    }

}
