/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
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
import net.time4j.format.SignPolicy;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.time4j.CalendarUnit.CENTURIES;
import static net.time4j.CalendarUnit.DAYS;
import static net.time4j.CalendarUnit.DECADES;
import static net.time4j.CalendarUnit.MILLENNIA;
import static net.time4j.CalendarUnit.MONTHS;
import static net.time4j.CalendarUnit.QUARTERS;
import static net.time4j.CalendarUnit.WEEKS;
import static net.time4j.CalendarUnit.YEARS;
import static net.time4j.ClockUnit.HOURS;
import static net.time4j.ClockUnit.MICROS;
import static net.time4j.ClockUnit.MILLIS;
import static net.time4j.ClockUnit.MINUTES;
import static net.time4j.ClockUnit.NANOS;
import static net.time4j.ClockUnit.SECONDS;


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
 *  <li>{@link #parse(String)}</li>
 *  <li>{@link #parseCalendarPeriod(String)}</li>
 *  <li>{@link #parseClockPeriod(String)}</li>
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
 * position of two time point relative to each other. A manipulation of
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
 * @concurrency <immutable>
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
 *  <li>{@link #parse(String)}</li>
 *  <li>{@link #parseCalendarPeriod(String)}</li>
 *  <li>{@link #parseClockPeriod(String)}</li>
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
 * @concurrency <immutable>
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

    private static final Object SIGN_KEY = new Object();
    private static final long MRD = 1000000000L;
    private static final long MIO = 1000000L;

    @SuppressWarnings("rawtypes")
    private static final Duration ZERO = new Duration();

    private static final
    Comparator<Item<? extends ChronoUnit>> ITEM_COMPARATOR =
        new Comparator<Item<? extends ChronoUnit>>() {
            @Override
            public int compare(
                Item<? extends ChronoUnit> o1,
                Item<? extends ChronoUnit> o2
            ) {
                return Duration.compare(o1.getUnit(), o2.getUnit());
            }
        };

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
     * @see     PlainTimestamp
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
     * @see     PlainTimestamp
     */
    public static Normalizer<IsoUnit> STD_PERIOD = new TimestampNormalizer();

    /**
     * <p>Normalizes the calendrical items of a duration on the base
     * {@code 1 year = 12 months} - without converting the days to months. </p>
     *
     * <p>Weeks will be normalized to days if weeks do not represent
     * the only calendrical duration items. Only time units of type
     * {@link CalendarUnit} will be normalized. </p>
     *
     * @see     PlainDate
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
     * @see     PlainDate
     */
    public static Normalizer<CalendarUnit> STD_CALENDAR_PERIOD =
        new DateNormalizer();

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
    public static Normalizer<ClockUnit> STD_CLOCK_PERIOD = new TimeNormalizer();

    private static final int PRINT_STYLE_NORMAL = 0;
    private static final int PRINT_STYLE_ISO = 1;
    private static final int PRINT_STYLE_XML = 2;
    private static final long serialVersionUID = -6321211763598951499L;

    private static final
    TimeMetric<CalendarUnit, Duration<CalendarUnit>> YMD_METRIC =
        Duration.in(YEARS, MONTHS, DAYS);
    private static final
    TimeMetric<ClockUnit, Duration<ClockUnit>> CLOCK_METRIC =
        Duration.in(HOURS, MINUTES, SECONDS, NANOS);

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

        this.negative = (empty ? false : negative);

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
     * @param   <U> generic unit type
     * @return  empty duration
     */
    /*[deutsch]
     * <p>Liefert eine leere Zeitspanne ohne Einheiten. </p>
     *
     * @param   <U> generic unit type
     * @return  empty duration
     */
    @SuppressWarnings("unchecked")
    public static <U extends IsoUnit> Duration<U> ofZero() {

        return (Duration<U>) ZERO;

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
    public static <U extends IsoUnit>
    TimeMetric<U, Duration<U>> in(U... units) {

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
    public static
    TimeMetric<CalendarUnit, Duration<CalendarUnit>> inYearsMonthsDays() {

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
            } else {
                this.throwMixedSignsException(originalAmount, originalUnit);
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
            } else {
                this.throwMixedSignsException(originalAmount, originalUnit);
            }
        }

        return new Duration<U>(temp, resultNegative);

    }

    /**
     * <p>Creates a duration as union of this instance and given timespan
     * where partial amounts of equal units will be summed up. </p>
     *
     * <p>In contrast to {@code union()}, this method only handles timespans
     * with the same unit type. Further details can be seen in the description
     * of {@link #union(TimeSpan)}. </p>
     *
     * @param   timespan    other time span this duration will be merged
     *                      with by adding the partial amounts
     * @return  new merged duration
     * @throws  IllegalStateException if the result gets mixed signs by
     *          adding the partial amounts
     * @throws  IllegalArgumentException if different units of same length exist
     * @throws  ArithmeticException in case of long overflow
     */
    /*[deutsch]
     * <p>Erzeugt eine neue Zeitspanne als Vereinigung dieser und der
     * angegebenen Zeitspanne, wobei Betr&auml;ge zu gleichen Zeiteinheiten
     * addiert werden. </p>
     *
     * <p>Diese Methode vereinigt anders als {@code union()} nur
     * Zeitspannen mit dem gleichen Einheitstyp. Weitere Details sind
     * gleich und der Beschreibung von {@link #union(TimeSpan)} zu
     * entnehmen. </p>
     *
     * @param   timespan    other time span this duration will be merged
     *                      with by adding the partial amounts
     * @return  new merged duration
     * @throws  IllegalStateException if the result gets mixed signs by
     *          adding the partial amounts
     * @throws  IllegalArgumentException if different units of same length exist
     * @throws  ArithmeticException in case of long overflow
     */
    public Duration<U> plus(TimeSpan<? extends U> timespan) {

        if (this.isEmpty()) {
            if (isEmpty(timespan)) {
                return this;
            } else if (timespan instanceof Duration) {
                Duration<U> result = cast(timespan);
                return result;
            }
        }

        Map<U, Long> map = new HashMap<U, Long>();

        for (int i = 0, n = this.count(); i < n; i++) {
            Item<U> item = this.getTotalLength().get(i);
            map.put(
                item.getUnit(),
                Long.valueOf(
                    MathUtils.safeMultiply(
                        item.getAmount(),
                        (this.isNegative() ? -1 : 1)
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

        Boolean neg = null;

        if (this.isNegative() == tsign) {
            neg = Boolean.valueOf(this.isNegative());
        } else {
            for (Map.Entry<U, Long> entry : map.entrySet()) {
                boolean nsign = (entry.getValue().longValue() < 0);
                if (neg == null) {
                    neg = Boolean.valueOf(nsign);
                } else if (neg.booleanValue() != nsign) {
                    throw new IllegalStateException(
                        "Mixed signs in result time span not allowed: "
                        + this
                        + " UNION "
                        + timespan);
                }
            }
        }

        if (neg.booleanValue()) {
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

        return Duration.create(map, neg.booleanValue());

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
                    (amount < 0) ? - 1 : 1
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
     * <p><i>union of timespans with date and time units</i></p>
     * <pre>
     *  Duration&lt;CalendarUnit&gt; dateDuration =
     *      Duration.ofCalendarUnits(2, 7, 10);
     *  Duration&lt;ClockUnit&gt; timeDuration =
     *      Duration.ofClockUnits(0, 30, 0);
     *  System.out.println(dateDuration.union(timeDuration)); // P2Y7M10DT30M
     * </pre>
     *
     * <p><i>union as addition of timespans</i></p>
     * <pre>
     *  Duration&lt;CalendarUnit&gt; p1 =
     *      Duration.ofCalendarUnits(0, 0, 10);
     *  Duration&lt;CalendarUnit&gt; p2 =
     *      Duration.of(21, CalendarUnit.DAYS);
     *  System.out.println(p1.union(p2)); // P31D
     * </pre>
     *
     * <p>If the signs of both timespans are different then the signs of
     * all partial amounts must be equal in the result in order to define
     * the sign of the whole result in a unique way. Example: </p>
     *
     * <pre>
     *  Duration&lt;CalendarUnit&gt; duration =
     *      Duration.of(4, DAYS)
     *      .union(Duration.ofCalendarUnits(0, 1, 34))
     *      .inverse(); // [-P1M30D] (OK)
     *  Duration&lt;CalendarUnit&gt; duration =
     *      Duration.ofCalendarUnits(0, 5, 4)
     *      .union(Duration.ofCalendarUnits(0, 1, 34))
     *      .inverse(); // [P+1M-30D] (throws IllegalStateException)
     * </pre>
     *
     * @param   timespan    other time span this duration is to be merged with
     * @return  new merged duration with {@code IsoUnit} as unit type
     * @throws  IllegalStateException if the result gets mixed signs by
     *          adding the partial amounts
     * @throws  IllegalArgumentException if different units of same length exist
     * @throws  ArithmeticException in case of long overflow
     */
    /*[deutsch]
     * <p>Erzeugt eine neue Zeitspanne als Vereinigung dieser und der
     * angegebenen Zeitspanne, wobei Betr&auml;ge zu gleichen Zeiteinheiten
     * addiert werden. </p>
     *
     * <p><i>Vereinigung von Zeitspannen in Datum und Uhrzeit</i></p>
     * <pre>
     *  Duration&lt;CalendarUnit&gt; dateDuration =
     *      Duration.ofCalendarUnits(2, 7, 10);
     *  Duration&lt;ClockUnit&gt; timeDuration =
     *      Duration.ofClockUnits(0, 30, 0);
     *  System.out.println(dateDuration.union(timeDuration)); // P2Y7M10DT30M
     * </pre>
     *
     * <p><i>Vereinigung als Addition von Zeitspannen</i></p>
     * <pre>
     *  Duration&lt;CalendarUnit&gt; p1 =
     *      Duration.ofCalendarUnits(0, 0, 10);
     *  Duration&lt;CalendarUnit&gt; p2 =
     *      Duration.of(21, CalendarUnit.DAYS);
     *  System.out.println(p1.union(p2)); // P31D
     * </pre>
     *
     * <p>Falls die Vorzeichen beider Zeitspannen verschieden sind, m&uuml;ssen
     * im Ergebnis trotzdem die Vorzeichen aller Betr&auml;ge gleich sein, damit
     * eindeutig das Vorzeichen der Ergebnis-Zeitspanne feststeht. Beispiel in
     * Pseudo-Code: [P4D] union [-P1M34D] = [-P1M30D]. Hingegen f&uuml;hrt die
     * Vereinigung [P5M4D] union [-P4M34D] zum Abbruch, weil [P+1M-30D] keine
     * sinnvolle Vorzeichenregelung erlaubt. </p>
     *
     * @param   timespan    other time span this duration is to be merged with
     * @return  new merged duration with {@code IsoUnit} as unit type
     * @throws  IllegalStateException if the result gets mixed signs by
     *          adding the partial amounts
     * @throws  IllegalArgumentException if different units of same length exist
     * @throws  ArithmeticException in case of long overflow
     */
    public Duration<IsoUnit> union(TimeSpan<? extends IsoUnit> timespan) {

        Duration<IsoUnit> zero = ofZero();
        return zero.plus(this).plus(timespan);

    }

    /**
     * <p>Normalizes this duration by given normalizer. </p>
     *
     * @param   normalizer  help object for normalizing this duration
     * @return  new normalized duration while this duration remains unaffected
     * @see     #STD_PERIOD
     * @see     #STD_CALENDAR_PERIOD
     * @see     #STD_CLOCK_PERIOD
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
     */
    public Duration<U> with(Normalizer<U> normalizer) {

        return convert(normalizer.normalize(this));

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
     * &quot;T&quot; separates date and time part. </p>
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
     * @see     #parse(String)
     */
    /*[deutsch]
     * <p>Liefert eine kanonische Darstellung, die optional mit einem negativen
     * Vorzeichen beginnt, dann mit dem Buchstaben &quot;P&quot; fortsetzt,
     * gefolgt von einer Reihe von alphanumerischen Zeichen analog zur
     * ISO8601-Definition. </p>
     *
     * <p>Beispiel: Im ISO8601-Format ist eine Zeitspanne von 1 Monat, 3 Tagen
     * und 4 Stunden als &quot;P1M3DT4H&quot; beschrieben, wobei der Buchstabe
     * &quot;T&quot; Datums- und Uhrzeitteil trennt. </p>
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
     * @see     #parse(String)
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
     * @return  String
     * @throws  ChronoException if this duration is negative or if any special
     *          units shall be output, but units of type {@code CalendarUnit}
     *          will be translated to iso-compatible units if necessary
     * @see     #parse(String)
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
     * @return  String
     * @throws  ChronoException if this duration is negative or if any special
     *          units shall be output, but units of type {@code CalendarUnit}
     *          will be translated to iso-compatible units if necessary
     * @see     #parse(String)
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
     * @return  String
     * @throws  ChronoException if any special units shall be
     *          output, but units of type {@code CalendarUnit} will be
     *          translated to xml-compatible units if necessary
     * @see     #parse(String)
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
     * @return  String
     * @throws  ChronoException if any special units shall be
     *          output, but units of type {@code CalendarUnit} will be
     *          translated to xml-compatible units if necessary
     * @see     #parse(String)
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
     * method {@code toStringXML()} takes in account these characteristics
     * of XML-schema (leaving aside the fact that XML-schema is potentially
     * design for unlimited big amounts but Time4J can define durations
     * only in long range with nanosecond precision at best). </p>
     *
     * <p>Examples for supported formats: </p>
     *
     * <pre>
     *  date := -P7Y4M3D (negative: 7 years, 4 months, 3 days)
     *  time := PT3H2M1,4S (positive: 3 hours, 2 minutes, 1400 milliseconds)
     *  date-time := P1Y1M5DT15H59M10.400S (dot as decimal separator)
     * </pre>
     *
     * @param   period          duration in canonical, ISO-8601-compatible or
     *                          XML-schema-compatible format (P-string)
     * @return  parsed duration in all possible standard units of date and time
     * @throws  ParseException if parsing fails
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
     * <p>Beispiele f&uuml;r unterst&uuml;tzte Formate: </p>
     *
     * <pre>
     *  date := -P7Y4M3D (negativ: 7 Jahre, 4 Monate, 3 Tage)
     *  time := PT3H2M1,4S (positiv: 3 Stunden, 2 Minuten, 1400 Millisekunden)
     *  date-time := P1Y1M5DT15H59M10.400S (Punkt als Dezimaltrennzeichen)
     * </pre>
     *
     * @param   period          duration in canonical, ISO-8601-compatible or
     *                          XML-schema-compatible format (P-string)
     * @return  parsed duration in all possible standard units of date and time
     * @throws  ParseException if parsing fails
     * @see     #parseCalendarPeriod(String)
     * @see     #parseClockPeriod(String)
     * @see     #toString()
     * @see     #toStringISO()
     * @see     #toStringXML()
     */
    public static Duration<IsoUnit> parse(String period)
        throws ParseException {

        return parse(period, IsoUnit.class);

    }

    /**
     * <p>Parses a canonical representation with only date units to a
     * calendrical duration. </p>
     *
     * @param   period          duration in canonical, ISO-8601-compatible or
     *                          XML-schema-compatible format (P-string)
     * @return  parsed calendrical duration
     * @throws  ParseException if parsing fails
     * @see     #parse(String)
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
     * @see     #parse(String)
     * @see     #parseClockPeriod(String)
     */
    public static Duration<CalendarUnit> parseCalendarPeriod(String period)
        throws ParseException {

        return parse(period, CalendarUnit.class);

    }

    /**
     * <p>Parses a canonical representation with only wall time units to a
     * time-only duration. </p>
     *
     * @param   period          duration in canonical, ISO-8601-compatible or
     *                          XML-schema-compatible format (P-string)
     * @return  parsed time-only duration
     * @throws  ParseException if parsing fails
     * @see     #parse(String)
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
     * @see     #parse(String)
     * @see     #parseCalendarPeriod(String)
     */
    public static Duration<ClockUnit> parseClockPeriod(String period)
        throws ParseException {

        return parse(period, ClockUnit.class);

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
        long nanos = 0;
        long seconds = 0;
        long weeksAsDays = 0;

        for (
            int index = 0, limit = this.getTotalLength().size();
            index < limit;
            index++
        ) {
            Item<U> item = this.getTotalLength().get(index);
            U unit = item.getUnit();

            if (!timeAppended && !unit.isCalendrical()) {
                sb.append('T');
                timeAppended = true;
            }

            long amount = item.getAmount();
            char symbol = unit.getSymbol();

            if ((symbol > '0') && (symbol <= '9')) {
                assert (symbol == '9');
                nanos = amount;
            } else if (symbol == 'S') {
                seconds = amount;
            } else {
                if (
                    xml
                    || (style == PRINT_STYLE_ISO)
                ) {
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
                                    sb.append(
                                        MathUtils.safeMultiply(amount, 7));
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

        return sb.toString();

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
            U key = entry.getKey();

            if (amount == 0) {
                continue;
            } else if (key == MILLIS) {
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
            int cmp = compare(midUnit, unit);

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

    private static int compare(
        ChronoUnit u1,
        ChronoUnit u2
    ) {

        int result = Double.compare(u2.getLength(), u1.getLength());

        if (
            (result == 0)
            && !u1.equals(u2)
        ) {
            throw new IllegalArgumentException(
                "Mixing different units of same length not allowed.");
        }

        return result;

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

    private void throwMixedSignsException(
        long amount,
        ChronoUnit unit
    ) {

        StringBuilder sb = new StringBuilder(128);
        sb.append("Mixed signs in result time span not allowed: ");
        sb.append(this);
        sb.append(" + (");
        sb.append(amount);
        sb.append(' ');
        sb.append(unit);
        sb.append(')');
        throw new IllegalStateException(sb.toString());

    }

    private static <U extends IsoUnit>
    Duration<U> convert(TimeSpan<U> timespan) {

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

    private static <U extends IsoUnit> Duration<U> parse(
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

            if (calendrical) {
                if (type == ClockUnit.class) {
                    throw new ParseException(
                        "Format symbol \'T\' expected: " + period, index);
                } else {
                    parseItems(period, index, period.length(), true, items);
                }
            } else {
                if (sep > index) {
                    if (type == ClockUnit.class) {
                        throw new ParseException(
                            "Unexpected date component found: " + period,
                            index);
                    } else {
                        parseItems(period, index, sep, true, items);
                    }
                }
                if (type == CalendarUnit.class) {
                    throw new ParseException(
                        "Unexpected time component found: " + period, sep);
                } else {
                    parseItems(
                        period,
                        sep + 1,
                        period.length(),
                        false,
                        items);
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

    private static <U extends ChronoUnit> void parseItems(
        String period,
        int from,
        int to,
        boolean date,
        List<Item<U>> items
    ) throws ParseException {

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
                if ((num == null) || date) {
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
                ChronoUnit unit = (
                    date
                    ? parseDateSymbol(c, period, i)
                    : parseTimeSymbol(c, period, i));
                last = addParsedItem(unit, last, amount, period, i, items);
            }

        }

        if (!endOfItem) {
            throw new ParseException("Unit symbol expected: " + period, to);
        }

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
     *              the data bytes for date and time component follow.
     *
     * Schematic algorithm:
     *
     * <pre>
     *      boolean useLong = ...;
     *      byte header = (6 << 4);
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
     *      if (getTotalLength().size() > 0) {
     *          out.writeBoolean(isNegative());
     *      }
     * </pre>
     */
    private Object writeReplace() throws ObjectStreamException {

        return new SPX(this, SPX.DURATION_TYPE);

    }

    /**
     * @serialData  Blocks because a serialization proxy is required.
     * @throws      InvalidObjectException (always)
     */
    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {

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

            ZonalOffset o1 = this.getOffset(t1);
            ZonalOffset o2 = this.getOffset(t2);
            t2 = t2.plus(
                o1.getIntegralAmount() - o2.getIntegralAmount(),
                SECONDS);
            t2 = t2.plus(
                o1.getFractionalAmount() - o2.getFractionalAmount(),
                NANOS);
            Duration<IsoUnit> duration = this.metric.between(t1, t2);

            if (negative) {
                duration = duration.inverse();
            }

            return duration;

        }

        private ZonalOffset getOffset(ChronoEntity<?> entity) {

            PlainDate date = entity.get(PlainDate.COMPONENT);
            PlainTime time = entity.get(PlainTime.COMPONENT);
            return this.tz.getStrategy().getOffset(date, time, this.tz);

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
     * <p>First example (parsing a Joda-Time-Period): </p>
     *
     * <pre>
     *  String jodaPattern =
     *      "'P'[-Y'Y'][-M'M'][-W'W'][-D'D']['T'[-h'H'][-m'M']]";
     *  Duration.Formatter&lt;IsoUnit&gt; f =
     *      Duration.Formatter.ofPattern(IsoUnit.class, jodaPattern);
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
     * @see     Duration#parse(String)
     * @see     #ofPattern(Class, String)
     * @concurrency <immutable>
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
     *  String jodaPattern =
     *      "'P'[-Y'Y'][-M'M'][-W'W'][-D'D']['T'[-h'H'][-m'M']]";
     *  Duration.Formatter&lt;IsoUnit&gt; f =
     *      Duration.Formatter.ofPattern(IsoUnit.class, jodaPattern);
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
     * @see     Duration#parse(String)
     * @see     #ofPattern(Class, String)
     * @concurrency <immutable>
     */
    public static final class Formatter<U extends IsoUnit> {

        //~ Instanzvariablen ----------------------------------------------

    	private final Class<U> type;
    	private final List<FormatItem> items;
        private final String pattern;

        //~ Konstruktoren -------------------------------------------------

        private Formatter(
            Class<U> type,
            List<FormatItem> items,
            String pattern
        ) {
            super();

            if (type == null) {
                throw new NullPointerException("Missing unit type.");
            } else if (items.isEmpty()) {
            	throw new IllegalArgumentException("Missing format pattern.");
            }

            this.type = type;
            this.items = Collections.unmodifiableList(items);
            this.pattern = pattern;

        }

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Equivalent to {@code ofPattern(IsoUnit.class, pattern)}. </p>
         *
         * @param   pattern format pattern
         * @return  new formatter instance
         * @since   1.2
         * @see     #ofPattern(Class, String)
         */
        /*[deutsch]
         * <p>&Auml;quivalent zu {@code ofPattern(IsoUnit.class, pattern)}. </p>
         *
         * @param   pattern format pattern
         * @return  new formatter instance
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
         *  <tr><td>[]</td><td>optional section for parsing</td></tr>
         *  <tr><td>#{}</td><td>reserved chars for future use</td></tr>
         * </table>
         *
         * <p>All letters in range a-z and A-Z are always reserved chars
         * and must be escaped by apostrophes for interpretation as literals.
         * If such a letter is repeated then the count of symbols controls
         * the minimum width for formatted output. If necessary a number
         * (of units) will be padded from left with the zero digt. Optional
         * sections let the parser be error-tolerant and continue with the
         * next section in case of errors. </p>
         *
         * @param   <U>     generic unit type
         * @param   type    reified unit type
         * @param   pattern format pattern
         * @return  new formatter instance
         * @since   1.2
         */
        /*[deutsch]
         * <p>Konstruiert eine neue Formatinstanz. </p>
         *
         * <p>Benutzt ein Formatmuster mit Symbolen wie folgt: <br>&nbsp;</p>
         *
         * <table border="1">
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
         *  <tr><td>[]</td><td>optionaler Abschnitt beim Parsen</td></tr>
         *  <tr><td>#{}</td><td>zuk&uuml;nftige reservierte Zeichen</td></tr>
         * </table>
         *
         * <p>Alle Buchstaben im Bereich a-z und A-Z sind grunds&auml;tzlich
         * reservierte Zeichen und m&uuml;ssen als Literale in Apostrophe
         * gefasst werden. Wird ein Buchstabensymbol mehrfach wiederholt,
         * dann regelt die Anzahl der Symbole die Mindestbreite in der
         * formatierten Ausgabe. Bei Bedarf wird eine Zahl (von Einheiten)
         * von links mit der Nullziffer aufgef&uuml;llt. Optionale Abschnitte
         * regeln, da&szlig; der Interpretationsvorgang bei Fehlern nicht
         * sofort abbricht, sondern mit dem n&auml;chsten Abschnitt
         * fortsetzt. </p>
         *
         * @param   <U>     generic unit type
         * @param   type    reified unit type
         * @param   pattern format pattern
         * @return  new formatter instance
         * @since   1.2
         */
        public static <U extends IsoUnit> Formatter<U> ofPattern(
            Class<U> type,
            String pattern
        ) {

            int n = pattern.length();
            List<List<FormatItem>> stack = new ArrayList<List<FormatItem>>();
            stack.add(new ArrayList<FormatItem>());

            for (int i = 0; i < n; i++) {
                char c = pattern.charAt(i);

                if (isSymbol(c)) {
                    int start = i++;
                    while ((i < n) && pattern.charAt(i) == c) {
                        i++;
                    }
                    addSymbol(c, i - start, stack);
                    i--;
                } else if (c == '\'') { // Literalsektion
                    int start = i++;
                    while (i < n) {
                        if (pattern.charAt(i) == '\'') {
                            if (
                                (i + 1 < n)
                                && (pattern.charAt(i + 1) == '\'')
                            ) {
                                i++;
                            } else {
                                break;
                            }
                        }
                        i++;
                    }
                    if (i >= n) {
                        throw new IllegalArgumentException(
                            "String literal in pattern not closed: " + pattern);
                    }
                    if (start + 1 == i) {
                        addLiteral('\'', stack);
                    } else {
                        String s = pattern.substring(start + 1, i);
                        addLiteral(s.replace("''", "'"), stack);
                    }
                } else if (c == '[') {
                    startOptionalSection(stack);
                } else if (c == ']') {
                    endOptionalSection(stack);
                } else if (c == '.') {
                    lastOn(stack).add(new SeparatorItem('.', ','));
                } else if (c == ',') {
                    lastOn(stack).add(new SeparatorItem(',', '.'));
                } else if (c == '-') {
                    lastOn(stack).add(
                        new SignItem(SignPolicy.SHOW_WHEN_NEGATIVE));
                } else if (c == '+') {
                    lastOn(stack).add(
                        new SignItem(SignPolicy.SHOW_ALWAYS));
                } else if ((c == '#') || (c == '{') || (c == '}')) {
                    throw new IllegalArgumentException(
                        "Pattern contains reserved character: '" + c + "'");
                } else {
                    addLiteral(c, stack);
                }
            }

            if (stack.size() > 1) {
                throw new IllegalArgumentException(
                    "Open square bracket without closing one.");
            } else if (stack.isEmpty()) {
                throw new IllegalArgumentException("Empty or invalid pattern.");
            }

        	return new Formatter<U>(type, stack.get(0), pattern);

        }

        /**
         * <p>Yields the underlying format pattern. </p>
         *
         * @return  String
         * @since   1.2
         */
        /**
         * <p>Liefert das zugrundeliegende Formatmuster. </p>
         *
         * @return  String
         * @since   1.2
         */
        public String getPattern() {

            return this.pattern;

        }

        /**
         * <p>Yields the associated reified unit type. </p>
         *
         * @return  Class
         * @since   1.2
         */
        /**
         * <p>Liefert den zugeh&ouml;rigen Zeiteinheitstyp. </p>
         *
         * @return  Class
         * @since   1.2
         */
        public Class<U> getType() {

            return this.type;

        }

        /**
         * <p>Creates a textual output of given duration. </p>
         *
         * @param   duration	duration object
         * @return  textual represenation of duration
         * @throws	IllegalArgumentException if some aspects of duration
         *          prevents printing (for example too many nanoseconds)
         * @since   1.2
         */
        /*[deutsch]
         * <p>Erzeugt eine textuelle Ausgabe der angegebenen Dauer. </p>
         *
         * @param   duration	duration object
         * @return  textual represenation of duration
         * @throws	IllegalArgumentException if some aspects of duration
         *          prevents printing (for example too many nanoseconds)
         * @since   1.2
         */
        public String format(Duration<?> duration) {

        	StringBuilder buffer = new StringBuilder();

            try {
                this.print(duration, buffer);
            } catch (IOException ex) {
                throw new AssertionError(ex); // should never happen
			}

        	return buffer.toString();

        }

        /**
         * <p>Creates a textual output of given duration and writes to
         * the buffer. </p>
         *
         * @param   duration	duration object
         * @param   buffer      I/O-buffer where the result is written to
         * @throws	IllegalArgumentException if some aspects of duration
         *          prevents printing (for example too many nanoseconds)
         * @throws  IOException if writing into buffer fails
         * @since   1.2
         */
        /*[deutsch]
         * <p>Erzeugt eine textuelle Ausgabe der angegebenen Dauer und
         * schreibt sie in den Puffer. </p>
         *
         * @param   duration	duration object
         * @param   buffer      I/O-buffer where the result is written to
         * @throws	IllegalArgumentException if some aspects of duration
         *          prevents printing (for example too many nanoseconds)
         * @throws  IOException if writing into buffer fails
         * @since   1.2
         */
        public void print(
            Duration<?> duration,
            Appendable buffer
        ) throws IOException {

			for (FormatItem item : this.items) {
				item.print(duration, buffer);
			}

        }

        /**
         * <p>Equivalent to {@code parse(text, 0)}. </p>
         *
         * @param   text	custom textual representation to be parsed
         * @return  parsed duration
         * @throws	ParseException (for example in case of mixed signs)
         * @since   1.2
         * @see     #parse(CharSequence, int)
         */
        /*[deutsch]
         * <p>&Auml;quivalent zu {@code parse(text, 0)}. </p>
         *
         * @param   text	custom textual representation to be parsed
         * @return  parsed duration
         * @throws	ParseException (for example in case of mixed signs)
         * @since   1.2
         * @see     #parse(CharSequence, int)
         */
        public Duration<U> parse(CharSequence text) throws ParseException {

            return this.parse(text, 0);

        }

        /**
         * <p>Analyzes given text according to format pattern and parses the
         * text to a duration. </p>
         *
         * @param   text	custom textual representation to be parsed
         * @param   offset  start position for the parser
         * @return  parsed duration
         * @throws	ParseException (for example in case of mixed signs)
         * @since   1.2
         */
        /*[deutsch]
         * <p>Interpretiert den angegebenen Text entsprechend dem
         * voreingestellten Formatmuster als Dauer. </p>
         *
         * @param   text	custom textual representation to be parsed
         * @param   offset  start position for the parser
         * @return  parsed duration
         * @throws	ParseException (for example in case of mixed signs)
         * @since   1.2
         */
        public Duration<U> parse(
            CharSequence text,
            int offset
        ) throws ParseException {

            int pos = offset;
			Map<Object, Long> unitsToValues = new HashMap<Object, Long>();

			for (FormatItem item : this.items) {
				int reply = item.parse(unitsToValues, text, pos);

				if (reply < 0) {
					throw new ParseException("Cannot parse: " + text, ~reply);
				} else {
					pos = reply;
				}
			}

            if (pos == offset) {
                throw new ParseException("Cannot parse: " + text, offset);
            }

			Long sign = unitsToValues.remove(SIGN_KEY);
			boolean negative = ((sign != null) && (sign.longValue() < 0));
			Map<U, Long> map = new HashMap<U, Long>();

			for (Object key : unitsToValues.keySet()) {
				if (this.type.isInstance(key)) {
					map.put(this.type.cast(key), unitsToValues.get(key));
				} else {
					throw new ParseException(
                        "Duration type mismatched: " + unitsToValues, pos);
				}
			}

			return Duration.create(map, negative);

        }

        private static boolean isSymbol(char c) {

            return (
                ((c >= 'A') && (c <= 'Z'))
                || ((c >= 'a') && (c <= 'z'))
            );

        }

        private static void addSymbol(
        	char symbol,
        	int count,
        	List<List<FormatItem>> stack
        ) {

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

        	List<FormatItem> items = stack.get(stack.size() - 1);

            if (symbol == 'f') {
                items.add(new FractionItem(count));
            } else {
                items.add(new NumberItem(count, unit));
            }

        }

        private static void addLiteral(
        	char literal,
        	List<List<FormatItem>> stack
        ) {

        	addLiteral(String.valueOf(literal), stack);

        }

        private static void addLiteral(
        	String literal,
        	List<List<FormatItem>> stack
        ) {

        	lastOn(stack).add(new LiteralItem(literal));

        }

        private static void startOptionalSection(List<List<FormatItem>> stack) {

            stack.add(new ArrayList<FormatItem>());

        }

        private static void endOptionalSection(List<List<FormatItem>> stack) {

        	int last = stack.size() - 1;

            if (last < 1) {
                throw new IllegalArgumentException(
                    "Closing square bracket without open one.");
            }

        	List<FormatItem> items = stack.remove(last);
        	stack.get(last - 1).add(new OptionalSectionItem(items));

        }

        private static List<FormatItem> lastOn(List<List<FormatItem>> stack) {

            return stack.get(stack.size() - 1);

        }

    }

    private abstract static class FormatItem {

        //~ Methoden ------------------------------------------------------

    	abstract void print(
    		Duration<?> duration,
    		Appendable buffer
    	) throws IOException;

    	abstract int parse(
    		Map<Object, Long> unitsToValues,
    		CharSequence text,
    		int pos
    	);

    }

    private static class NumberItem
    	extends FormatItem {


        //~ Instanzvariablen ----------------------------------------------

    	private final int minWidth;
    	private final IsoUnit unit;

        //~ Konstruktoren -------------------------------------------------

    	private NumberItem(
    		int minWidth,
    		IsoUnit unit
    	) {
    		super();

    		if (minWidth < 1 || minWidth > 18) {
    			throw new IllegalArgumentException(
                    "Min width out of bounds: " + minWidth);
    		} else if (unit == null) {
    			throw new NullPointerException("Missing unit.");
    		}

    		this.minWidth = minWidth;
    		this.unit = unit;

    	}

        //~ Methoden ------------------------------------------------------

		@Override
		void print(
    		Duration<?> duration,
    		Appendable buffer
    	) throws IOException {

			String num = String.valueOf(duration.getPartialAmount(this.unit));

			for (int i = this.minWidth - num.length(); i > 0; i--) {
				buffer.append('0');
			}

			buffer.append(num);

		}

		@Override
		int parse(
            Map<Object, Long> unitsToValues,
            CharSequence text,
            int start
        ) {

			long total = 0;
			int pos = start;

			for (int i = start, n = text.length(); i < n; i++) {
				char c = text.charAt(i);
				if ((c >= '0') && (c <= '9')) {
					if (i - start >= 18) {
						return ~start; // too many digits
					}
					int digit = (c - '0');
					total = total * 10 + digit;
					pos++;
				} else {
					break;
				}
			}

			if (pos == start) {
				return ~start; // digits expected
			}

			Long value = Long.valueOf(total);
			Object old = unitsToValues.put(this.unit, value);

			if ((old == null) || old.equals(value)) {
				return pos;
			} else {
				return ~start; // ambivalent parsing
			}

		}

    }

    private static class FractionItem
		extends FormatItem {


	    //~ Instanzvariablen ----------------------------------------------

    	private final int width;

	    //~ Konstruktoren -------------------------------------------------

		private FractionItem(int width) {
			super();

    		if (width < 1 || width > 9) {
    			throw new IllegalArgumentException(
                    "Fraction width out of bounds: " + width);
    		}

			this.width = width;

		}

	    //~ Methoden ------------------------------------------------------

		@Override
		void print(
    		Duration<?> duration,
    		Appendable buffer
    	) throws IOException {

			String num = String.valueOf(duration.getPartialAmount(NANOS));
			int len = num.length();

			if (len > 9) {
				throw new IllegalArgumentException(
					"Too many nanoseconds, consider normalization: "
                    + duration);
			}

			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < 9 - len; i++) {
				sb.append('0');
			}

			sb.append(num);
			buffer.append(sb.toString().substring(0, this.width));

		}

		@Override
		int parse(
            Map<Object, Long> unitsToValues,
            CharSequence text,
            int start
        ) {

			StringBuilder fraction = new StringBuilder();
			int pos = start;

			for (
                int i = start, n = Math.min(text.length(), start + this.width);
                i < n;
                i++
            ) {
				char c = text.charAt(i);
				if ((c >= '0') && (c <= '9')) {
					fraction.append(c);
					pos++;
				} else {
					break;
				}
			}

			if (pos == start) {
				return ~start; // digits expected
			}

			for (int i = 0, n = pos - start; i < 9 - n; i++) {
				fraction.append('0');
			}

			Long value = Long.valueOf(Long.parseLong(fraction.toString()));
			Object old = unitsToValues.put(NANOS, value);

			if ((old == null) || old.equals(value)) {
				return pos;
			} else {
				return ~start; // ambivalent parsing
			}

		}

	}

    private static class SeparatorItem
		extends FormatItem {


	    //~ Instanzvariablen ----------------------------------------------

    	private final char separator;
    	private final char alt;

	    //~ Konstruktoren -------------------------------------------------

		private SeparatorItem(
			char separator,
			char alt
	    ) {
			super();

			this.separator = separator;
			this.alt = alt;

		}

	    //~ Methoden ------------------------------------------------------

		@Override
		void print(
    		Duration<?> duration,
    		Appendable buffer
    	) throws IOException {

			buffer.append(this.separator);

		}

		@Override
		int parse(
            Map<Object, Long> unitsToValues,
            CharSequence text,
            int start
        ) {

			if (start >= text.length()) {
                return ~start; // end of text
            }

            char c = text.charAt(start);

            if ((c != this.separator) && (c != this.alt)) {
				return ~start; // decimal separator expected
			}

			return start + 1;

		}

	}

    private static class LiteralItem
		extends FormatItem {


	    //~ Instanzvariablen ----------------------------------------------

		private final String literal;

	    //~ Konstruktoren -------------------------------------------------

		private LiteralItem(String literal) {
			super();

			if (literal.isEmpty()) {
				throw new IllegalArgumentException("Literal is empty.");
			}

			this.literal = literal;

		}

	    //~ Methoden ------------------------------------------------------

		@Override
		void print(
    		Duration<?> duration,
    		Appendable buffer
    	) throws IOException {

			buffer.append(this.literal);

		}

		@Override
		int parse(
            Map<Object, Long> unitsToValues,
            CharSequence text,
            int start
        ) {

            int end = start + this.literal.length();

            if (end > text.length()) {
                return ~start; // end of line
            }

            for (int i = start; i < end; i++) {
                if (text.charAt(i) != this.literal.charAt(i - start)) {
                    return ~start; // literal expected
                }
            }

			return end;

		}

	}

    private static class SignItem
		extends FormatItem {


	    //~ Instanzvariablen ----------------------------------------------

		private final SignPolicy policy;

	    //~ Konstruktoren -------------------------------------------------

		private SignItem(SignPolicy policy) {
			super();

			if (policy == null) {
				throw new NullPointerException("Missing sign policy.");
			}

			this.policy = policy;

		}

	    //~ Methoden ------------------------------------------------------

		@Override
		void print(
    		Duration<?> duration,
    		Appendable buffer
    	) throws IOException {

			switch (this.policy) {
				case SHOW_WHEN_NEGATIVE:
					if (duration.isNegative()) {
						buffer.append('-');
					}
					break;
				case SHOW_ALWAYS:
					buffer.append(duration.isNegative() ? '-' : '+');
					break;
				default:
					throw new UnsupportedOperationException(this.policy.name());
			}

		}

		@Override
		int parse(
            Map<Object, Long> unitsToValues,
            CharSequence text,
            int start
        ) {

			if (start >= text.length()) {
				if (this.policy == SignPolicy.SHOW_ALWAYS) {
					return ~start; // sign expected
				} else {
					Long old = unitsToValues.put(SIGN_KEY, Long.valueOf(1));
					if ((old != null) && (old.longValue() != 1)) {
						return ~start; // mixed signs
					}
					return start;
				}
			}

			char c = text.charAt(start);
			Long sign = Long.valueOf(1);
			int ret = start;

			switch (this.policy) {
				case SHOW_WHEN_NEGATIVE:
					if (c == '+') {
						return ~start; // positive sign not allowed
					} else if (c == '-') {
						sign = Long.valueOf(-1);
						ret = start + 1;
					}
					break;
				case SHOW_ALWAYS:
					if (c == '+') {
						ret = start + 1;
					} else if (c == '-') {
						sign = Long.valueOf(-1);
						ret = start + 1;
					} else {
						return ~start; // sign expected
					}
					break;
				default:
					throw new UnsupportedOperationException(this.policy.name());
			}

			Long old = unitsToValues.put(SIGN_KEY, sign);

			if ((old != null) && (old.longValue() != sign.longValue())) {
				return ~start; // mixed signs
			}

			return ret;

		}

	}

    private static class OptionalSectionItem
		extends FormatItem {


	    //~ Instanzvariablen ----------------------------------------------

    	private final List<FormatItem> items;

	    //~ Konstruktoren -------------------------------------------------

    	private OptionalSectionItem(List<FormatItem> items) {
			super();

			if (items.isEmpty()) {
				throw new IllegalArgumentException(
                    "Optional section is empty.");
			}

			this.items = items;

		}

	    //~ Methoden ------------------------------------------------------

		@Override
		void print(
    		Duration<?> duration,
    		Appendable buffer
    	) throws IOException {

			for (FormatItem item : this.items) {
				item.print(duration, buffer);
			}

		}

		@Override
		int parse(
            Map<Object, Long> unitsToValues,
            CharSequence text,
            int start
        ) {

			int pos = start;
			Map<Object, Long> store = new HashMap<Object, Long>(unitsToValues);

			for (FormatItem item : this.items) {
				int reply = item.parse(store, text, pos);

				if (reply < 0) {
					return start;
				} else {
					pos = reply;
				}
			}

			unitsToValues.putAll(store);
			return pos;

		}

	}

    private static class TimestampNormalizer
        implements Normalizer<IsoUnit> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Duration<IsoUnit>
        normalize(TimeSpan<? extends IsoUnit> timespan) {

            int count = timespan.getTotalLength().size();
            List<Item<IsoUnit>> items =
                new ArrayList<Item<IsoUnit>>(count);
            long years = 0, months = 0, weeks = 0, days = 0;
            long hours = 0, minutes = 0, seconds = 0, nanos = 0;

            for (int i = 0; i < count; i++) {
                Item<? extends IsoUnit> item =
                    timespan.getTotalLength().get(i);
                long amount = item.getAmount();
                IsoUnit unit = item.getUnit();

                if (unit instanceof CalendarUnit) {
                    switch ((CalendarUnit.class.cast(unit))) {
                        case MILLENNIA:
                            years =
                                MathUtils.safeAdd(
                                    MathUtils.safeMultiply(amount, 1000),
                                    years
                                );
                            break;
                        case CENTURIES:
                            years =
                                MathUtils.safeAdd(
                                    MathUtils.safeMultiply(amount, 100),
                                    years
                                );
                            break;
                        case DECADES:
                            years =
                                MathUtils.safeAdd(
                                    MathUtils.safeMultiply(amount, 10),
                                    years
                                );
                            break;
                        case YEARS:
                            years = MathUtils.safeAdd(amount, years);
                            break;
                        case QUARTERS:
                            months =
                                MathUtils.safeAdd(
                                    MathUtils.safeMultiply(amount, 3),
                                    months
                                );
                            break;
                        case MONTHS:
                            months = MathUtils.safeAdd(amount, months);
                            break;
                        case WEEKS:
                            weeks = amount;
                            break;
                        case DAYS:
                            days = amount;
                            break;
                        default:
                            throw new UnsupportedOperationException(
                                unit.toString());
                    }
                } else if (unit instanceof ClockUnit) {
                    switch ((ClockUnit.class.cast(unit))) {
                        case HOURS:
                            hours = amount;
                            break;
                        case MINUTES:
                            minutes = amount;
                            break;
                        case SECONDS:
                            seconds = amount;
                            break;
                        case MILLIS:
                            nanos =
                                MathUtils.safeAdd(
                                    MathUtils.safeMultiply(amount, MIO),
                                    nanos
                                );
                            break;
                        case MICROS:
                            nanos =
                                MathUtils.safeAdd(
                                    MathUtils.safeMultiply(amount, 1000L),
                                    nanos
                                );
                            break;
                        case NANOS:
                            nanos = MathUtils.safeAdd(amount, nanos);
                            break;
                        default:
                            throw new UnsupportedOperationException(
                                unit.toString());
                    }
                } else {
                    items.add(Item.of(amount, unit));
                }
            }

            long f = 0, s = 0, n = 0, h = 0;

            if ((hours | minutes | seconds | nanos) != 0) {
                f = nanos % MRD;
                seconds = MathUtils.safeAdd(seconds, nanos / MRD);
                s = seconds % 60;
                minutes = MathUtils.safeAdd(minutes, seconds / 60);
                n = minutes % 60;
                hours = MathUtils.safeAdd(hours, minutes / 60);
                h = hours % 24;
                days = MathUtils.safeAdd(days, hours / 24);
            }

            IsoUnit unit;

            if ((years | months | days) != 0) {
                long y = MathUtils.safeAdd(years, months / 12);
                long m = months % 12;
                long d =
                    MathUtils.safeAdd(
                        MathUtils.safeMultiply(weeks, 7),
                        days
                    );

                if (y != 0) {
                    unit = YEARS;
                    items.add(Item.of(y, unit));
                }
                if (m != 0) {
                    unit = MONTHS;
                    items.add(Item.of(m, unit));
                }
                if (d != 0) {
                    unit = DAYS;
                    items.add(Item.of(d, unit));
                }
            } else if (weeks != 0) {
                unit = WEEKS;
                items.add(Item.of(weeks, unit));
            }

            if (h != 0) {
                unit = HOURS;
                items.add(Item.of(h, unit));
            }

            if (n != 0) {
                unit = MINUTES;
                items.add(Item.of(n, unit));
            }

            if (s != 0) {
                unit = SECONDS;
                items.add(Item.of(s, unit));
            }

            if (f != 0) {
                unit = NANOS;
                items.add(Item.of(f, unit));
            }

            return new Duration<IsoUnit>(
                items,
                timespan.isNegative()
            );

        }

    }

    private static class DateNormalizer
        implements Normalizer<CalendarUnit> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Duration<CalendarUnit>
        normalize(TimeSpan<? extends CalendarUnit> timespan) {

            int count = timespan.getTotalLength().size();
            long years = 0, months = 0, weeks = 0, days = 0;

            for (int i = 0; i < count; i++) {
                Item<? extends CalendarUnit> item =
                    timespan.getTotalLength().get(i);
                long amount = item.getAmount();
                CalendarUnit unit = item.getUnit();

                switch (unit) {
                    case MILLENNIA:
                        years =
                            MathUtils.safeAdd(
                                MathUtils.safeMultiply(amount, 1000),
                                years
                            );
                        break;
                    case CENTURIES:
                        years =
                            MathUtils.safeAdd(
                                MathUtils.safeMultiply(amount, 100),
                                years
                            );
                        break;
                    case DECADES:
                        years =
                            MathUtils.safeAdd(
                                MathUtils.safeMultiply(amount, 10),
                                years
                            );
                        break;
                    case YEARS:
                        years = MathUtils.safeAdd(amount, years);
                        break;
                    case QUARTERS:
                        months =
                            MathUtils.safeAdd(
                                MathUtils.safeMultiply(amount, 3),
                                months
                            );
                        break;
                    case MONTHS:
                        months = MathUtils.safeAdd(amount, months);
                        break;
                    case WEEKS:
                        weeks = amount;
                        break;
                    case DAYS:
                        days = amount;
                        break;
                    default:
                        throw new UnsupportedOperationException(
                            unit.toString());
                }
            }

            boolean negative = timespan.isNegative();

            if ((years | months | days) != 0) {
                long y = MathUtils.safeAdd(years, months / 12);
                long m = months % 12;
                long d =
                    MathUtils.safeAdd(
                        MathUtils.safeMultiply(weeks, 7),
                        days
                    );
                return Duration.ofCalendarUnits(y, m, d, negative);
            } else if (weeks != 0) {
                if (negative) {
                    weeks = MathUtils.safeNegate(weeks);
                }
                return Duration.of(weeks, WEEKS);
            }

            return Duration.of(0, DAYS);

        }

    }

    private static class TimeNormalizer
        implements Normalizer<ClockUnit> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Duration<ClockUnit>
        normalize(TimeSpan<? extends ClockUnit> timespan) {

            int count = timespan.getTotalLength().size();
            long hours = 0, minutes = 0, seconds = 0, nanos = 0;

            for (int i = 0; i < count; i++) {
                Item<? extends ClockUnit> item =
                    timespan.getTotalLength().get(i);
                long amount = item.getAmount();
                ClockUnit unit = item.getUnit();

                switch (unit) {
                    case HOURS:
                        hours = amount;
                        break;
                    case MINUTES:
                        minutes = amount;
                        break;
                    case SECONDS:
                        seconds = amount;
                        break;
                    case MILLIS:
                        nanos =
                            MathUtils.safeAdd(
                                MathUtils.safeMultiply(amount, MIO),
                                nanos
                            );
                        break;
                    case MICROS:
                        nanos =
                            MathUtils.safeAdd(
                                MathUtils.safeMultiply(amount, 1000L),
                                nanos
                            );
                        break;
                    case NANOS:
                        nanos = MathUtils.safeAdd(amount, nanos);
                        break;
                    default:
                        throw new UnsupportedOperationException(unit.name());
                }
            }

            long f = 0, s = 0, n = 0, h = 0;

            if ((hours | minutes | seconds | nanos) != 0) {
                f = nanos % MRD;
                seconds = MathUtils.safeAdd(seconds, nanos / MRD);
                s = seconds % 60;
                minutes = MathUtils.safeAdd(minutes, seconds / 60);
                n = minutes % 60;
                hours = MathUtils.safeAdd(hours, minutes / 60);
                h = hours;
            }

            return Duration.ofClockUnits(
                h,
                n,
                s,
                f,
                timespan.isNegative()
            );

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

}
