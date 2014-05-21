/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Duration.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Time4J. If not, see <http://www.gnu.org/licenses/>.
 * -----------------------------------------------------------------------
 */

package net.time4j;

import net.time4j.base.MathUtils;
import net.time4j.engine.AbstractDuration;
import net.time4j.engine.AbstractMetric;
import net.time4j.engine.ChronoException;
import net.time4j.engine.ChronoOperator;
import net.time4j.engine.ChronoUnit;
import net.time4j.engine.Normalizer;
import net.time4j.engine.TimeMetric;
import net.time4j.engine.TimePoint;
import net.time4j.engine.TimeSpan;
import net.time4j.tz.TZID;

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
 * sich &uuml;ber die Methoden {@code plus()}, {@code minus()}, {@code with()},
 * {@code union()}, {@code multipliedBy()}, {@code abs()} und {@code inverse()}
 * erzeugen. Hierbei werden die Zeiteinheiten {@code ClockUnit.MILLIS} und
 * {@code ClockUnit.MICROS} intern immer zu Nanosekunden normalisiert. Ansonsten
 * mu&szlig; eine Normalisierung explizit mittels {@code with(Normalizer)}
 * angesto&szlig;en werden. </p>
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

    /**
     * @serial  list of amounts and units
     */
    private final List<Item<U>> items;

    /**
     * @serial  marks a negative time span
     */
    private final boolean negative;

    //~ Konstruktoren -----------------------------------------------------

    // Standard-Konstruktor
    private Duration(
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
     * <p>Erzeugt eine neue Zeitspanne, die auf nur einer Zeiteinheit
     * beruht. </p>
     *
     * <p>Ist der angegebene Betrag negativ, so wird auch die Zeitspanne
     * negativ sein. Ist er {@code 0}, wird eine leere Zeitspanne
     * generiert. </p>
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

        List<Item<U>> items = new ArrayList<Item<U>>(1);
        items.add(
            Item.of(
                ((amount < 0) ? MathUtils.safeNegate(amount) : amount),
                unit)
            );
        return new Duration<U>(items, (amount < 0));

    }

    /**
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
     * <p>Konstruiert eine Metrik f&uuml;r beliebige Standard-Zeiteinheiten
     * in normalisierter Form. </p>
     *
     * <p><strong>WICHTIG:</strong> Fehlt die der Pr&auml;zision der zu
     * vergleichenden Zeitpunkte entsprechende kleinste Zeiteinheit, wird
     * im allgemeinen ein Subtraktionsrest &uuml;brigbleiben. Das Ergebnis
     * der Metrikberechnung wird dann nicht den vollst&auml;ndigen zeitlichen
     * Abstand zwischen den Zeitpunkten ausdr&uuml;cken. F&uuml;r die
     * Vollst&auml;ndigkeit der Berechnung ist bei Datumsangaben mindestens
     * die explizite Angabe der Tageseinheit notwendig. </p>
     *
     * @param   <U> generic unit type
     * @param   units       time units to be used in calculation
     * @return  immutable metric for calculating a duration in given units
     * @throws  IllegalArgumentException if any time unit is missing or
     *          if there are unit duplicates
     */
    public static <U extends IsoUnit>
    TimeMetric<U, Duration<U>> in(U... units) {

        return new Metric<U>(units);

    }

    /**
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
    public static
    TimeMetric<ClockUnit, Duration<ClockUnit>> inClockUnits() {

        return CLOCK_METRIC;

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
     * <p>Ist die angegebene Zeiteinheit in dieser Zeitspanne enthalten? </p>
     *
     * <p>Eine Zeiteinheit ist auch dann enthalten, wenn sie als
     * Sekundenbruchteil (Ziffer in Symboldarstellung) erst konvertiert
     * werden mu&szlig;. </p>
     *
     * @param   unit    time unit to be checked (optional)
     * @return  {@code true} if this duration contains given unit
     *          else {@code false}
     * @see     #getPartialAmount(ChronoUnit) getPartialAmount(U)
     */
    @Override
    public boolean contains(ChronoUnit unit) {

        if (unit instanceof IsoUnit) {
            IsoUnit isoUnit = (IsoUnit) unit;
            boolean fractional = isFractionUnit(isoUnit);

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
        }

        return false;

    }

    /**
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
    public long getPartialAmount(ChronoUnit unit) {

        if (unit instanceof IsoUnit) {
            IsoUnit isoUnit = (IsoUnit) unit;
            boolean fractional = isFractionUnit(isoUnit);

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
                    int d2 = isoUnit.getSymbol() - '0';
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
        }

        return 0;

    }

    /**
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
     * <p>Liefert eine Kopie dieser Instanz, in der der angegebene Betrag zum
     * mit der angegebenen Zeiteinheit assoziierten Feldwert addiert wird. </p>
     *
     * <p>Die Methode ber&uuml;cksichtigt auch das Vorzeichen der Zeitspanne.
     * Beispiel in Pseudo-Code: {@code [P5M].plus(-6, CalendarUnit.MONTHS)} wird
     * zu {@code [-P1M]}. Ist der zu addierende Betrag {@code 0}, liefert die
     * Methode einfach diese Instanz selbst. </p>
     *
     * <p>Notiz: Gemischte Vorzeichen im Ergebnis sind nicht zul&auml;ssig und
     * werden mit einem Abbruch quittiert. Zum Beispiel ist folgender Ausdruck
     * nicht erlaubt: {@code [-P1M].plus(30, CalendarUnit.DAYS)}</p>
     *
     * @param   amount      temporal amount to be added (maybe negative)
     * @param   unit        associated time unit
     * @return  new changed duration while this duration remains unaffected
     * @throws  IllegalStateException if the result gets mixed signs by
     *          adding the partial amounts
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
     * <p>Liefert eine Kopie dieser Instanz, in der der angegebene Betrag
     * vom mit der angegebenen Zeiteinheit assoziierten Feldwert subtrahiert
     * wird. </p>
     *
     * <p>Entspricht {@code plus(-amount, unit)}. </p>
     *
     * @param   amount      temporal amount to be subtracted (maybe negative)
     * @param   unit        associated time unit
     * @return  new changed duration while this duration remains unaffected
     * @throws  IllegalStateException if the result gets mixed signs by
     *          subtracting the partial amounts
     * @throws  ArithmeticException in case of long overflow
     * @see     #plus(long, IsoUnit) plus(long, U)
     */
    public Duration<U> minus(
        long amount,
        U unit
    ) {

        return this.plus(MathUtils.safeNegate(amount), unit);

    }

    /**
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
     * @throws  ArithmeticException in case of long overflow
     */
    public Duration<U> plus(TimeSpan<? extends U> timespan) {

        return add(this, timespan, false);

    }

    /**
     * <p>Erzeugt eine neue Zeitspanne als Vereinigung dieser und der
     * angegebenen Zeitspanne, wobei die Betr&auml;ge ds Arguments zu
     * gleichen Zeiteinheiten subtrahiert werden. </p>
     *
     * <p>Weitere Details siehe {@link #plus(TimeSpan)}. </p>
     *
     * @param   timespan    other time span this duration will be merged
     *                      with by subtracting the partial amounts
     * @return  new merged duration
     * @throws  IllegalStateException if the result gets mixed signs by
     *          subtracting the partial amounts
     * @throws  ArithmeticException in case of long overflow
     */
    public Duration<U> minus(TimeSpan<? extends U> timespan) {

        return add(this, timespan, true);

    }

    /**
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
     * <p>Liefert die absolute immer positive Variante dieser Zeitspanne. </p>
     *
     * <p>Beispiel: {@code [-P5M].abs()} wird zu {@code [P5M]}. </p>
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
     * <p>Notiz: Anders als in {@code javax.xml.datatype.Duration} ist die
     * Anforderung an gleiche Vorzeichen hier h&auml;rter, weil diese Klasse
     * auch zur Verwendung in Zeitzonenkontexten vorgesehen ist, wo kein
     * Verla&szlig; auf feste Umrechnungen à la 1 Tag = 24 Stunden besteht.
     * Allerdings besteht die M&ouml;glichkeit, Zeitspannen vor der Vereinigung
     * geeignet zu normalisieren. </p>
     *
     * @param   timespan    other time span this duration is to be merged with
     * @return  new merged duration with {@code IsoUnit} as unit type
     * @throws  IllegalStateException if the result gets mixed signs by
     *          adding the partial amounts
     */
    public Duration<IsoUnit> union(TimeSpan<? extends IsoUnit> timespan) {

        Duration<IsoUnit> zero = ofZero();
        return zero.plus(this).plus(timespan);

    }

    /**
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
     * <p>Wendet diese Dauer so auf einen {@code Moment} an, da&szlig; in
     * der angegebenen Zeitzone der fr&uuml;here lokale Zeitstempel berechnet
     * wird. </p>
     *
     * @param   timezone    timezone id
     * @return  operator applicable on {@code Moment}-objects
     * @see     #later(TZID)
     */
    public ChronoOperator<Moment> earlier(final TZID timezone) {

        return new ChronoOperator<Moment>() {
            @Override
            public Moment apply(Moment entity) {
                PlainTimestamp ts =
                    entity.inTimezone(timezone).minus(Duration.this);
                return ts.at(timezone);
            }
        };

    }

    /**
     * <p>Wendet diese Dauer so auf einen {@code Moment} an, da&szlig; in
     * der angegebenen Zeitzone der sp&auml;tere lokale Zeitstempel berechnet
     * wird. </p>
     *
     * @param   timezone    timezone id
     * @return  operator applicable on {@code Moment}-objects
     * @see     #earlier(TZID)
     */
    public ChronoOperator<Moment> later(final TZID timezone) {

        return new ChronoOperator<Moment>() {
            @Override
            public Moment apply(Moment entity) {
                PlainTimestamp ts =
                    entity.inTimezone(timezone).plus(Duration.this);
                return ts.at(timezone);
            }
        };

    }

    /**
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
     * <p>Parst eine kanonische Darstellung zu einer Dauer. </p>
     *
     * <p>Syntax in RegExp-&auml;hnlicher Notation: </p>
     *
     * <pre>
     *  sign := [-]?
     *  amount := [0-9]+
     *  fraction := [,\.]{amount}
     *  years-months-days := ({amount}Y)?({amount}M)?({amount}D)?
     *  weeks := ({amount}W)?
     *  date := {years-months-days} | {weeks}
     *  time := ({amount}H)?({amount}M)?({amount}{fraction}?S)?
     *  duration := {sign}P{date}(T{time})? | PT{time}
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
     * @param   duration        duration in canonical, ISO-8601-compatible or
     *                          XML-schema-compatible format
     * @return  parsed duration in all possible standard units of date and time
     * @throws  ParseException if parsing fails
     * @see     #parseCalendarPeriod(String)
     * @see     #parseClockPeriod(String)
     * @see     #toString()
     * @see     #toStringISO()
     * @see     #toStringXML()
     */
    public static Duration<IsoUnit> parse(String duration)
        throws ParseException {

        return parse(duration, IsoUnit.class);

    }

    /**
     * <p>Parst eine kanonische Darstellung nur mit
     * Datumskomponenten zu einer Dauer. </p>
     *
     * @param   duration        duration in canonical, ISO-8601-compatible or
     *                          XML-schema-compatible format
     * @return  parsed calendrical duration
     * @throws  ParseException if parsing fails
     * @see     #parse(String)
     * @see     #parseClockPeriod(String)
     */
    public static
    Duration<CalendarUnit> parseCalendarPeriod(String duration)
        throws ParseException {

        return parse(duration, CalendarUnit.class);

    }

    /**
     * <p>Parst eine kanonische Darstellung nur mit
     * Uhrzeitkomponenten zu einer Dauer. </p>
     *
     * @param   duration        duration in canonical, ISO-8601-compatible or
     *                          XML-schema-compatible format
     * @return  parsed time-only duration
     * @throws  ParseException if parsing fails
     * @see     #parse(String)
     * @see     #parseCalendarPeriod(String)
     */
    public static
    Duration<ClockUnit> parseClockPeriod(String duration)
        throws ParseException {

        return parse(duration, ClockUnit.class);

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
                                if (this.getIndex(DAYS) < 0) {
                                    sb.append(weeksAsDays);
                                    weeksAsDays = 0;
                                    symbol = 'D';
                                } else {
                                    continue;
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

    private static <U extends IsoUnit> Duration<U> add(
        Duration<U> duration,
        TimeSpan<? extends U> timespan,
        boolean inverse
    ) {

        if (duration.isEmpty()) {
            if (isEmpty(timespan)) {
                return duration;
            } else if (timespan instanceof Duration) {
                Duration<U> result = cast(timespan);
                return (inverse ? result.inverse() : result);
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

        if (inverse) {
            tsign = !tsign;
        }

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

        if (duration.isNegative() == tsign) {
            neg = Boolean.valueOf(duration.isNegative());
        } else {
            for (Map.Entry<U, Long> entry : map.entrySet()) {
                boolean nsign = (entry.getValue().longValue() < 0);
                if (neg == null) {
                    neg = Boolean.valueOf(nsign);
                } else if (neg.booleanValue() != nsign) {
                    throw new IllegalStateException(
                        "Mixed signs in result time span not allowed: "
                        + duration
                        + " UNION "
                        + (inverse ? "-" : "") + timespan);
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

        return Double.compare(u2.getLength(), u1.getLength());

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
        String duration,
        Class<U> type
    ) throws ParseException {

        int index = 0;
        boolean negative = false;

        if (duration.length() == 0) {
            throw new ParseException("Empty duration string.", index);
        } else if (duration.charAt(0) == '-') {
            negative = true;
            index = 1;
        }

        try {

            if (duration.charAt(index) != 'P') {
                throw new ParseException(
                    "Format symbol \'P\' expected: " + duration, index);
            } else {
                index++;
            }

            List<Item<U>> items = new ArrayList<Item<U>>();
            int sep = duration.indexOf('T', index);
            boolean calendrical = (sep == -1);

            if (calendrical) {
                if (type == ClockUnit.class) {
                    throw new ParseException(
                        "Format symbol \'T\' expected: " + duration, index);
                } else {
                    parseItems(duration, index, duration.length(), true, items);
                }
            } else {
                if (sep > index) {
                    if (type == ClockUnit.class) {
                        throw new ParseException(
                            "Unexpected date component found: " + duration,
                            index);
                    } else {
                        parseItems(duration, index, sep, true, items);
                    }
                }
                if (type == CalendarUnit.class) {
                    throw new ParseException(
                        "Unexpected time component found: " + duration, sep);
                } else {
                    parseItems(
                        duration,
                        sep + 1,
                        duration.length(),
                        false,
                        items);
                }
            }

            return new Duration<U>(items, negative);

        } catch (IndexOutOfBoundsException ex) {
            ParseException pe =
                new ParseException(
                    "Unexpected termination of duration: " + duration, index);
            pe.initCause(ex);
            throw pe;
        }

    }

    private static <U extends ChronoUnit> void parseItems(
        String duration,
        int from,
        int to,
        boolean date,
        List<Item<U>> items
    ) throws ParseException {

        if (from == to) {
            throw new ParseException(duration, from);
        }

        StringBuilder num = null;
        boolean endOfItem = false;
        ChronoUnit last = null;
        int index = from;
        boolean decimal = false;

        for (int i = from; i < to; i++) {
            char c = duration.charAt(i);

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
                        "Decimal separator misplaced: " + duration, i);
                } else {
                    endOfItem = true;
                    long amount = parseAmount(duration, num.toString(), index);
                    ChronoUnit unit = SECONDS;
                    last =
                        addParsedItem(unit, last, amount, duration, i, items);
                    num = null;
                    decimal = true;
                }
            } else if (endOfItem) {
                throw new ParseException(
                    "Unexpected char \'" + c + "\' found: " + duration, i);
            } else if (decimal) {
                if (c != 'S') {
                    throw new ParseException(
                        "Second symbol expected: " + duration, i);
                } else if (num == null) {
                    throw new ParseException(
                        "Decimal separator misplaced: " + duration, i - 1);
                } else if (num.length() > 9) {
                    num.delete(9, num.length());
                }
                for (int j = num.length(); j < 9; j++) {
                    num.append('0');
                }
                endOfItem = true;
                long amount = parseAmount(duration, num.toString(), index);
                ChronoUnit unit = NANOS;
                num = null;
                last = addParsedItem(unit, last, amount, duration, i, items);
            } else {
                endOfItem = true;
                long amount =
                    parseAmount(
                        duration,
                        (num == null) ? String.valueOf(c) : num.toString(),
                        index);
                num = null;
                ChronoUnit unit = (
                    date
                    ? parseDateSymbol(c, duration, i)
                    : parseTimeSymbol(c, duration, i));
                last = addParsedItem(unit, last, amount, duration, i, items);
            }

        }

        if (!endOfItem) {
            throw new ParseException("Unit symbol expected: " + duration, to);
        }

    }

    private static CalendarUnit parseDateSymbol(
        char c,
        String duration,
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
                    "Symbol \'" + c + "\' not supported: " + duration, index);
        }

    }

    private static ClockUnit parseTimeSymbol(
        char c,
        String duration,
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
                    "Symbol \'" + c + "\' not supported: " + duration, index);
        }

    }

    private static <U extends ChronoUnit> ChronoUnit addParsedItem(
        ChronoUnit unit,
        ChronoUnit last, // optional
        long amount,
        String duration,
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
        } else if (unit.getLength() == last.getLength()) {
            throw new ParseException(
                "Duplicate unit items: " + duration, index);
        } else {
            throw new ParseException(
                "Wrong order of unit items: " + duration, index);
        }

    }

    private static long parseAmount(
        String duration,
        String number,
        int index
    ) throws ParseException {

        try {
            return Long.parseLong(number);
        } catch (NumberFormatException nfe) {
            ParseException pe = new ParseException(duration, index);
            pe.initCause(nfe);
            throw pe;
        }

    }

    //~ Innere Klassen ----------------------------------------------------

    /**
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
         * <p>Erzeugt eine L&auml;nge in Jahren. </p>
         *
         * @param   num     count of years {@code >= 0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the argument is negative
         * @throws  IllegalStateException if already called
         */
        public Builder years(int num) {

            this.set(num, YEARS);
            return this;

        }

        /**
         * <p>Erzeugt eine L&auml;nge in Monaten. </p>
         *
         * @param   num     count of months {@code >= 0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the argument is negative
         * @throws  IllegalStateException if already called
         */
        public Builder months(int num) {

            this.set(num, MONTHS);
            return this;

        }

        /**
         * <p>Erzeugt eine L&auml;nge in Tagen. </p>
         *
         * @param   num     count of days {@code >= 0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the argument is negative
         * @throws  IllegalStateException if already called
         */
        public Builder days(int num) {

            this.set(num, DAYS);
            return this;

        }

        /**
         * <p>Erzeugt eine L&auml;nge in Stunden. </p>
         *
         * @param   num     count of hours {@code >= 0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the argument is negative
         * @throws  IllegalStateException if already called
         */
        public Builder hours(int num) {

            this.set(num, HOURS);
            return this;

        }

        /**
         * <p>Erzeugt eine L&auml;nge in Minuten. </p>
         *
         * @param   num     count of minutes {@code >= 0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the argument is negative
         * @throws  IllegalStateException if already called
         */
        public Builder minutes(int num) {

            this.set(num, MINUTES);
            return this;

        }

        /**
         * <p>Erzeugt eine L&auml;nge in Sekunden. </p>
         *
         * @param   num     count of seconds {@code >= 0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the argument is negative
         * @throws  IllegalStateException if already called
         */
        public Builder seconds(int num) {

            this.set(num, SECONDS);
            return this;

        }

        /**
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
         * <p>Erzeugt eine L&auml;nge in Nanosekunden. </p>
         *
         * @param   num     count of nanoseconds {@code >= 0}
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if the argument is negative
         * @throws  IllegalStateException if already called
         */
        public Builder nanos(int num) {

            this.nanosCalled();
            this.update(num, 1L);
            return this;

        }

        /**
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

        //~ Instanzvariablen ----------------------------------------------

        private final boolean calendrical;

        //~ Konstruktoren -------------------------------------------------

        private Metric(U... units) {
            super((units.length > 1), units);

            boolean c = true;

            for (U unit : units) {
                if (!unit.isCalendrical()) {
                    c = false;
                    break;
                }
            }

            this.calendrical = c;

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
