/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimeAxis.java) is part of project Time4J.
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

package net.time4j.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * <p>A time axis is a dynamic view on a chronology where a system of
 * registered time units is used to define a time arithmetic for any
 * time points belonging to this time axis respective chronology. </p>
 *
 * @param   <U> generic type of time units
 * @param   <T> generic type of time context compatible to {@link TimePoint})
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Eine Zeitachse ist eine dynamische Sicht auf eine Chronologie,
 * in der mit Hilfe eines Systems von Zeiteinheiten eine Zeitarithmetik auf
 * beliebigen Zeitpunkten der Chronologie definiert wird. </p>
 *
 * @param   <U> generic type of time units
 * @param   <T> generic type of time context compatible to {@link TimePoint})
 * @author  Meno Hochschild
 */
public final class TimeAxis<U, T extends TimePoint<U, T>>
    extends Chronology<T>
    implements Comparator<U>, TimeLine<T> {

    //~ Instanzvariablen --------------------------------------------------

    private final Class<U> unitType;
    private final Map<U, UnitRule<T>> unitRules;
    private final Map<U, Double> unitLengths;
    private final Map<U, Set<U>> convertibleUnits;
    private final Map<ChronoElement<?>, U> baseUnits;
    private final T min;
    private final T max;
    private final CalendarSystem<T> calendarSystem;
    private final ChronoElement<T> self;
    private final TimeLine<T> timeline;

    //~ Konstruktoren -----------------------------------------------------

    private TimeAxis(
        Class<T> chronoType,
        Class<U> unitType,
        ChronoMerger<T> merger,
        Map<ChronoElement<?>, ElementRule<T, ?>> ruleMap,
        Map<U, UnitRule<T>> unitRules,
        final Map<U, Double> unitLengths,
        Map<U, Set<U>> convertibleUnits,
        List<ChronoExtension> extensions,
        Map<ChronoElement<?>, U> baseUnits,
        T min,
        T max,
        CalendarSystem<T> calendarSystem, // optional
        TimeLine<T> timeline // optional
    ) {
        super(chronoType, merger, ruleMap, extensions);

        this.unitType = unitType;
        this.unitRules = Collections.unmodifiableMap(unitRules);
        this.unitLengths = Collections.unmodifiableMap(unitLengths);
        this.convertibleUnits = Collections.unmodifiableMap(convertibleUnits);
        this.baseUnits = Collections.unmodifiableMap(baseUnits);
        this.min = min;
        this.max = max;
        this.calendarSystem = calendarSystem;
        this.self = new SelfElement<T>(chronoType, min, max);

        if (timeline == null) {
            List<U> registeredUnits = new ArrayList<U>(unitRules.keySet());
            Collections.sort(
                registeredUnits,
                new Comparator<U>() {
                    @Override
                    public int compare(
                        U unit1,
                        U unit2
                    ) {
                        return Double.compare(
                            getLength(unitLengths, unit1),
                            getLength(unitLengths, unit2));
                    }
                });
            U step = registeredUnits.get(0);
            this.timeline = new DefaultTimeLine<U, T>(step, min, max);
        } else {
            this.timeline = timeline;
        }

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Returns the type of supported time units. </p>
     *
     * @return  reified type of time unit
     */
    /*[deutsch]
     * <p>Liefert den Zeiteinheitstyp. </p>
     *
     * @return  reified type of time unit
     */
    public Class<U> getUnitType() {

        return this.unitType;

    }

    /**
     * <p>Returns all registered time units. </p>
     *
     * @return  unmodifiable set of registered units without duplicates
     */
    /*[deutsch]
     * <p>Liefert alle registrierten Zeiteinheiten. </p>
     *
     * @return  unmodifiable set of registered units without duplicates
     */
    public Set<U> getRegisteredUnits() {

        return this.unitRules.keySet();

    }

    /**
     * <p>Queries if given time unit is registered. </p>
     *
     * @param   unit    time unit (optional)
     * @return  {@code true} if registered else {@code false}
     */
    /*[deutsch]
     * <p>Ist die angegebene Zeiteinheit registriert? </p>
     *
     * @param   unit    time unit (optional)
     * @return  {@code true} if registered else {@code false}
     */
    public boolean isRegistered(U unit) {

        return this.unitRules.containsKey(unit);

    }

    /**
     * <p>Queries if given time unit is supported. </p>
     *
     * <p>A time unit is supported if it is either registered or
     * if it defines a suitable rule. </p>
     *
     * @param   unit    time unit (optional)
     * @return  {@code true} if supported else {@code false}
     * @see     BasicUnit#derive(Chronology)
     */
    /*[deutsch]
     * <p>Wird die angegebene Zeiteinheit unterst&uuml;tzt? </p>
     *
     * <p>Unterst&uuml;tzung ist gegeben, wenn die Einheit entweder registriert
     * ist oder eine zu dieser Chronologie passende Regel definiert. </p>
     *
     * @param   unit    time unit (optional)
     * @return  {@code true} if supported else {@code false}
     * @see     BasicUnit#derive(Chronology)
     */
    public boolean isSupported(U unit) {

        if (this.isRegistered(unit)) {
            return true;
        } else if (unit instanceof BasicUnit) {
            return (BasicUnit.class.cast(unit).derive(this) != null);
        } else {
            return false;
        }

    }

    /**
     * <p>Returns the length of given time unit in seconds as it is
     * usual or estimated on this time axis. </p>
     *
     * <p>Example: In ISO-systems the year has  {@code 365.2425 * 86400}
     * seconds by default (mean average), in a julian calender
     * {@code 365.25 * 86400} seconds however. Daylight-saving-transitions
     * or UTC-leapseconds are not counted here. </p>
     *
     * <p>Note: If given time unit is not registered then Time4J tries
     * to interprete the unit as {@code ChronoUnit}. If this fails, too,
     * then the length is not calculatable. </p>
     *
     * @param   unit    time unit
     * @return  estimated standard length in seconds or
     *          {@code Double.NaN} if not calculatable
     * @see     ChronoUnit
     */
    /*[deutsch]
     * <p>Liefert die in dieser Chronologie &uuml;bliche L&auml;nge der
     * angegebenen Zeiteinheit in Sekunden. </p>
     *
     * <p>Beispiel: In ISO-Systemen hat das Jahr standardm&auml;&szlig;ig
     * {@code 365.2425 * 86400} Sekunden, in einem julianischen Kalender
     * hingegen {@code 365.25 * 86400} Sekunden. DST-&Uuml;berg&auml;nge
     * in Zeitzonen oder UTC-Schaltsekunden werden nicht mitgez&auml;hlt. </p>
     *
     * <p>Hinweis: Ist die angegebene Zeiteinheit nicht registriert, wird
     * versucht, die Zeiteinheit als {@code ChronoUnit} zu interpretieren.
     * Schl&auml;gt auch das fehl, ist die L&auml;nge nicht ermittelbar. </p>
     *
     * @param   unit    time unit
     * @return  estimated standard length in seconds or
     *          {@code Double.NaN} if not calculatable
     * @see     ChronoUnit
     */
    public double getLength(U unit) {

        return getLength(this.unitLengths, unit);

    }

    /**
     * <p>Queries if given time units are convertible. </p>
     *
     * <p>Convertibility means that there exists a fixed integer factor
     * for conversion between the units. Examples for convertible units
     * are weeks/days (factor 7) or years/months (factor 12) in ISO-systems.
     * Otherwise minutes and seconds will only be convertible with factor
     * {@code 60} if there is no UTC-context with possible leap seconds. </p>
     *
     * <p>If two time units are convertible then the length of a time unit
     * ({@code getLength()}) can be used to convert time units by applying
     * the rounded quotient of lengths of units. </p>
     *
     * @param   unit1   first time unit
     * @param   unit2   second time unit
     * @return  {@code true} if convertible else {@code false}
     * @see     #getLength(Object) getLength(U)
     */
    /*[deutsch]
     * <p>Sind die angegebenen Zeiteinheiten ineinander konvertierbar? </p>
     *
     * <p>Konvertierbarkeit bedeutet, da&szlig; immer ein konstanter
     * ganzzahliger Faktor zur Umrechnung zwischen den Zeiteinheiten
     * angewandt werden kann. Beispiele f&uuml;r konvertierbare Einheiten
     * sind in ISO-basierten Kalendersystemen die Paare Wochen/Tage
     * (Faktor 7) oder Jahre/Monate (Faktor 12). Andererseits sind Minuten
     * und Sekunden zueinander nur dann konvertierbar mit dem Faktor
     * {@code 60}, wenn kein UTC-Kontext mit m&ouml;glichen Schaltsekunden
     * vorliegt. </p>
     *
     * <p>Ist die Konvertierbarkeit gegeben, darf die L&auml;nge einer
     * Zeiteinheit mittels der Methode {@code getLength()} herangezogen
     * werden, um Zeiteinheiten umzurechnen, indem als Faktor der gerundete
     * Quotient der L&auml;ngen der Zeiteinheiten bestimmt wird. </p>
     *
     * @param   unit1   first time unit
     * @param   unit2   second time unit
     * @return  {@code true} if convertible else {@code false}
     * @see     #getLength(Object) getLength(U)
     */
    public boolean isConvertible(
        U unit1,
        U unit2
    ) {

        Set<U> set = this.convertibleUnits.get(unit1);
        return ((set != null) && set.contains(unit2));

    }

    /**
     * <p>Compares time units by ascending precision
     * (that is descending length). </p>
     */
    /*[deutsch]
     * <p>Vergleicht Zeiteinheiten nach aufsteigender Genauigkeit. </p>
     */
    @Override
    public int compare(
        U unit1,
        U unit2
    ) {

        return Double.compare(this.getLength(unit2), this.getLength(unit1));

    }

    /**
     * <p>Queries if given element has a base unit. </p>
     *
     * @param   element     chronological element (optional)
     * @return  {@code true} if given element has a base unit else {@code false}
     * @see     #getBaseUnit(ChronoElement)
     */
    /*[deutsch]
     * <p>Ermittelt, ob das angegebene Element eine Basiseinheit hat. </p>
     *
     * @param   element     chronological element (optional)
     * @return  {@code true} if given element has a base unit else {@code false}
     * @see     #getBaseUnit(ChronoElement)
     */
    public boolean hasBaseUnit(ChronoElement<?> element) {

        if (element == null) {
            return false;
        }

        boolean found = this.baseUnits.containsKey(element);

        if (
            !found
            && (element instanceof BasicElement)
        ) {
            ChronoElement<?> parent = ((BasicElement) element).getParent();
            found = ((parent != null) && this.baseUnits.containsKey(parent));
        }

        return found;

    }

    /**
     * <p>Returns the base unit of given element if available. </p>
     *
     * <p>Only registred elements can have a base unit unless the element
     * is a {@code BasicElement} and refers another registered element with
     * a base unit. </p>
     *
     * @param   element     chronological element
     * @return  found base unit
     * @throws  ChronoException if there is no base unit
     * @see     #hasBaseUnit(ChronoElement)
     * @see     BasicElement#getParent()
     */
    /*[deutsch]
     * <p>Liefert die Basiseinheit zum angegebenen Element. </p>
     *
     * <p>Nur registrierte Elemente k&ouml;nnen eine Basiseinheit haben, es
     * sei denn, das angegebene Element ist ein {@code BasicElement} und
     * referenziert ein anderes auf dieser Zeitachse registriertes Element
     * mit einer Basiseinheit. </p>
     *
     * @param   element     chronological element
     * @return  found base unit
     * @throws  ChronoException if there is no base unit
     * @see     #hasBaseUnit(ChronoElement)
     * @see     BasicElement#getParent()
     */
    public U getBaseUnit(ChronoElement<?> element) {

        if (element == null) {
            throw new NullPointerException("Missing element.");
        }

        U baseUnit = this.baseUnits.get(element);

        if (
            (baseUnit == null)
            && (element instanceof BasicElement)
        ) {
            ChronoElement<?> parent = ((BasicElement) element).getParent();
            baseUnit = this.baseUnits.get(parent);
        }

        if (baseUnit == null) {
            throw new ChronoException(
                "Base unit not found for: " + element.name());
        }

        return baseUnit;

    }

    /**
     * <p>Yields the minimum of this time axis. </p>
     *
     * @return  earliest possible time point
     */
    /*[deutsch]
     * <p>Ermittelt das Minimum auf der Zeitachse. </p>
     *
     * @return  earliest possible time point
     */
    public T getMinimum() {

        return this.min;

    }

    /**
     * <p>Yields the maximum of this time axis. </p>
     *
     * @return  latest possible time point
     */
    /*[deutsch]
     * <p>Ermittelt das Maximum auf der Zeitachse. </p>
     *
     * @return  latest possible time point
     */
    public T getMaximum() {

        return this.max;

    }

    @Override
    public boolean hasCalendarSystem() {

        return (this.calendarSystem != null);

    }

    @Override
    public CalendarSystem<T> getCalendarSystem() {

        if (this.calendarSystem == null) {
            return super.getCalendarSystem();
        } else {
            return this.calendarSystem;
        }

    }

    @Override
    @Deprecated
    public T createFrom(
        ChronoEntity<?> entity,
        AttributeQuery attributes,
        boolean preparsing
    ) {

        if (entity.contains(this.self)) {
            return entity.get(this.self);
        }

        return super.createFrom(entity, attributes, preparsing);

    }

    @Override
    public T createFrom(
        ChronoEntity<?> entity,
        AttributeQuery attributes,
        boolean lenient,
        boolean preparsing
    ) {

        if (entity.contains(this.self)) {
            return entity.get(this.self);
        }

        return super.createFrom(entity, attributes, lenient, preparsing);

    }

    /**
     * <p>Yields this time axis as chronological self-referencing
     * element. </p>
     *
     * @return  self-referencing element
     */
    /*[deutsch]
     * <p>Liefert diese Zeitachse als chronologisches Element mit
     * Selbstbezug. </p>
     *
     * @return  self-referencing element
     */
    public ChronoElement<T> element() {

        return this.self;

    }

    @Override
    public T stepForward(T timepoint) {

        return this.timeline.stepForward(timepoint);

    }

    @Override
    public T stepBackwards(T timepoint) {

        return this.timeline.stepBackwards(timepoint);

    }

    /**
     * <p>Liefert die chronologische Regel zur angegebenen Zeiteinheit. </p>
     *
     * @param   unit    time unit
     * @return  unit rule or {@code null} if not registered
     */
    UnitRule<T> getRule(U unit) {

        if (unit == null) {
            throw new NullPointerException("Missing chronological unit.");
        }

        if (this.isRegistered(unit)) {
            return this.unitRules.get(unit);
        } else if (unit instanceof BasicUnit) {
            UnitRule<T> rule = BasicUnit.class.cast(unit).derive(this);
            if (rule != null) {
                return rule;
            }
        }

        throw new RuleNotFoundException(this, unit);

    }

    private static <U> double getLength(
        Map<U, Double> unitLengths,
        U unit
    ) {

        Double length = unitLengths.get(unit);

        if (length == null) {
            if (unit instanceof ChronoUnit) {
                return ChronoUnit.class.cast(unit).getLength();
            } else {
                return Double.NaN;
            }
        } else {
            return length.doubleValue();
        }

    }

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Creates a builder for a new time axis respective chronology
     * and will only be used during loading a class of type
     * {@code TimePoint (T)} in a <i>static initializer</i>. </p>
     *
     * <p>Instances of this class will be created by the static factory
     * methods {@code setUp()}. </p>
     *
     * @param       <U> generic type of time unit
     * @param       <T> generic type of time context
     * @author      Meno Hochschild
     * @see         #setUp(Class,Class,ChronoMerger,TimePoint,TimePoint)
     * @see         #setUp(Class,Class,ChronoMerger,CalendarSystem)
     */
    /*[deutsch]
     * <p>Erzeugt einen Builder f&uuml;r eine neue Zeitachse respektive
     * Chronologie und wird ausschlie&szlig;lich beim Laden einer
     * {@code TimePoint}-Klasse T in einem <i>static initializer</i>
     * benutzt. </p>
     *
     * <p>Instanzen dieser Klasse werden &uuml;ber die statischen
     * {@code setUp()}-Fabrikmethoden erzeugt. </p>
     *
     * @param       <U> generic type of time unit
     * @param       <T> generic type of time context
     * @author      Meno Hochschild
     * @see         #setUp(Class,Class,ChronoMerger,TimePoint,TimePoint)
     * @see         #setUp(Class,Class,ChronoMerger,CalendarSystem)
     */
    public static final class Builder<U, T extends TimePoint<U, T>>
        extends Chronology.Builder<T> {

        //~ Instanzvariablen ----------------------------------------------

        private final Class<U> unitType;
        private final Map<U, UnitRule<T>> unitRules;
        private final Map<U, Double> unitLengths;
        private final Map<U, Set<U>> convertibleUnits;
        private final Map<ChronoElement<?>, U> baseUnits;
        private final T min;
        private final T max;
        private final CalendarSystem<T> calendarSystem;
        private TimeLine<T> timeline = null;

        //~ Konstruktoren -------------------------------------------------

        private Builder(
            Class<U> unitType,
            Class<T> chronoType,
            ChronoMerger<T> merger,
            T min,
            T max,
            CalendarSystem<T> calendarSystem, // optional
            TimeLine<T> timeline
        ) {
            super(chronoType, merger);

            if (unitType == null) {
                throw new NullPointerException("Missing unit type.");
            } else if (min == null) {
                throw new NullPointerException("Missing minimum of range.");
            } else if (max == null) {
                throw new NullPointerException("Missing maximum of range.");
            } else if (
                Calendrical.class.isAssignableFrom(chronoType)
                && (calendarSystem == null)
            ) {
                throw new NullPointerException("Missing calendar system.");
            }

            this.unitType = unitType;
            this.unitRules = new HashMap<U, UnitRule<T>>();
            this.unitLengths = new HashMap<U, Double>();
            this.convertibleUnits = new HashMap<U, Set<U>>();
            this.baseUnits = new HashMap<ChronoElement<?>, U>();
            this.min = min;
            this.max = max;
            this.calendarSystem = calendarSystem;
            this.timeline = timeline;

        }

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Creates a builder for building a chronological but
         * non-calendrical system. </p>
         *
         * @param   <U> generic type of time unit
         * @param   <T> generic type of time context
         * @param   unitType        reified type of time units
         * @param   chronoType      reified chronological type
         * @param   merger          generic replacement for static
         *                          creation of time points
         * @param   min             minimum value on time axis
         * @param   max             maximum value on time axis
         * @return  new {@code Builder} object
         */
        /*[deutsch]
         * <p>Erzeugt ein Hilfsobjekt zum Bauen eines chronologischen, aber
         * nicht kalendarischen Systems. </p>
         *
         * @param   <U> generic type of time unit
         * @param   <T> generic type of time context
         * @param   unitType        reified type of time units
         * @param   chronoType      reified chronological type
         * @param   merger          generic replacement for static
         *                          creation of time points
         * @param   min             minimum value on time axis
         * @param   max             maximum value on time axis
         * @return  new {@code Builder} object
         */
        public static <U, T extends TimePoint<U, T>> Builder<U, T> setUp(
            Class<U> unitType,
            Class<T> chronoType,
            ChronoMerger<T> merger,
            T min,
            T max
        ) {

            return new Builder<U, T>(
                unitType,
                chronoType,
                merger,
                min,
                max,
                null,
                null);

        }

        /**
         * <p>Creates a builder for building a time axis for
         * plain calendrical objects. </p>
         *
         * @param   <U> generic type of time unit
         * @param   <D> generic type of date context
         * @param   unitType        reified type of time units
         * @param   chronoType      reified chronological type
         * @param   merger          generic replacement for static
         *                          creation of time points
         * @param   calendarSystem  calender system
         * @return  new {@code Builder} object
         */
        /*[deutsch]
         * <p>Erzeugt ein Hilfsobjekt zum Bauen einer Zeitachse f&uuml;r
         * reine Datumsangaben. </p>
         *
         * @param   <U> generic type of time unit
         * @param   <D> generic type of date context
         * @param   unitType        reified type of time units
         * @param   chronoType      reified chronological type
         * @param   merger          generic replacement for static
         *                          creation of time points
         * @param   calendarSystem  calender system
         * @return  new {@code Builder} object
         */
        public static <U, D extends Calendrical<U, D>> Builder<U, D> setUp(
            Class<U> unitType,
            Class<D> chronoType,
            ChronoMerger<D> merger,
            CalendarSystem<D> calendarSystem
        ) {

            final CalendarSystem<D> calsys = calendarSystem;

            Builder<U, D> builder =
                new Builder<U, D>(
                    unitType,
                    chronoType,
                    merger,
                    calsys.transform(calsys.getMinimumSinceUTC()),
                    calsys.transform(calsys.getMaximumSinceUTC()),
                    calsys,
                    null
                );

            for (EpochDays element : EpochDays.values()) {
                builder.appendElement(element, element.derive(calsys));
            }

            return builder;

        }

        @Override
        public <V> Builder<U, T> appendElement(
            ChronoElement<V> element,
            ElementRule<T, V> rule
        ) {

            super.appendElement(element, rule);
            return this;

        }

        /**
         * <p>Registers a new element with associated rule and a base
         * unit. </p>
         *
         * @param   <V> generic type of element values
         * @param   element     chronological element to be registered
         * @param   rule        associated element rule
         * @param   baseUnit    base unit for rolling operations
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given element is already
         *          registered (duplicate)
         */
        /*[deutsch]
         * <p>Registriert ein neues Element mitsamt der assoziierten Regel
         * und einer Basiseinheit. </p>
         *
         * @param   <V> generic type of element values
         * @param   element     chronological element to be registered
         * @param   rule        associated element rule
         * @param   baseUnit    base unit for rolling operations
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given element is already
         *          registered (duplicate)
         */
        public <V> Builder<U, T> appendElement(
            ChronoElement<V> element,
            ElementRule<T, V> rule,
            U baseUnit
        ) {

            if (baseUnit == null) {
                throw new NullPointerException("Missing base unit.");
            }

            super.appendElement(element, rule);
            this.baseUnits.put(element, baseUnit);
            return this;

        }

        /**
         * <p>Registers a non-convertible time unit with an associated
         * unit rule. </p>
         *
         * <p>Is equivalent to {@link #appendUnit(Object,UnitRule,double,Set)
         * appendUnit(U, rule, length, Collections.emptySet())}. </p>
         *
         * @param   unit                time unit to be registered
         * @param   rule                associated unit rule
         * @param   length              estimated standard length in seconds
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given time unit is already
         *          registered (duplicate) or if given length does not represent
         *          any decimal number
         */
        /*[deutsch]
         * <p>Registriert eine neue nicht-konvertierbare Zeiteinheit mitsamt
         * Einheitsregel. </p>
         *
         * <p>Entspricht {@link #appendUnit(Object,UnitRule,double,Set)
         * appendUnit(U, rule, length, Collections.emptySet())}. </p>
         *
         * @param   unit                time unit to be registered
         * @param   rule                associated unit rule
         * @param   length              estimated standard length in seconds
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given time unit is already
         *          registered (duplicate) or if given length does not represent
         *          any decimal number
         */
        public Builder<U, T> appendUnit(
            U unit,
            UnitRule<T> rule,
            double length
        ) {

            Set<U> none = Collections.emptySet();
            return this.appendUnit(unit, rule, length, none);

        }

        /**
         * <p>Registers a new time unit with an associated unit rule. </p>
         *
         * <p>The unit rule defines the time arithmetic for addition and
         * subtraction of given unit suitable for the time axis. </p>
         *
         * <p>If the unit has a length of a whole day then the given length
         * must be equal to {@code 86400.0}. The time unit of a second has
         * always the length {@code 1.0}. </p>
         *
         * <p>The default length of a time unit primarily serves for
         * conversion purposes. Rare anomalies like leap seconds or
         * timezone-induced jumps are not taken into account. Therefore
         * this estimated length is usually not equal to the real length
         * in a given time context. In case of non-convertible units the
         * estimated length has only informational meaning. Example: Months
         * have as length in ISO-systems the length of a gregorian year
         * in seconds divided by {@code 12} while in coptic calendar the
         * divisor {@code 13} is used. However, the definition of the length
         * of a month in days is not suitable because months and days are
         * not convertible. </p>
         *
         * <p>Convertibility exists if a time unit con be converted to
         * another time unit using a fixed integer factor. If this is not
         * always the case then an empty {@code Set} is to be used. Example
         * minutes/seconds: Without an UTC-context (with possible leapseonds)
         * minutes are convertible to seconds using a constant factor of
         * {@code 60}. In an UTC-context however, there is no convertibility
         * and hence the second will be missing in the argument.
         * {@code convertibleUnits} to minutes as unit to be registered
         * (and reverse, too).. </p>
         *
         * @param   unit                time unit to be registered
         * @param   rule                associated unit rule
         * @param   length              estimated standard length in seconds
         * @param   convertibleUnits    other time units which {@code unit}
         *                              can be converted to
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given time unit is already
         *          registered (duplicate) or if given length does not represent
         *          any decimal number
         */
        /*[deutsch]
         * <p>Registriert eine neue Zeiteinheit mitsamt Einheitsregel. </p>
         *
         * <p>Die Regel zur Zeiteinheit definiert die Zeitarithmetik in der
         * Addition und Subtraktion passend zur aktuellen Chronologie. </p>
         *
         * <p>Liegt eine Zeiteinheit mit Tagesl&auml;nge vor, so ist stets
         * die L&auml;nge von {@code 86400.0} anzugeben. Die Zeiteinheit der
         * Sekunde selbst hat die L&auml;nge {@code 1.0}. </p>
         *
         * <p>Die Standardl&auml;nge einer Zeiteinheit dient in erster Linie
         * der Konversion von zwei nach dem Kriterium der Genauigkeit
         * benachbarten Zeiteinheiten. Seltene Anomalien wie Schaltsekunden
         * oder zeitzonenbedingte L&uuml;cken werden dabei nicht kalkuliert.
         * Deshalb ist die Standardl&auml;nge nicht mit der im gegebenen
         * Zeitkontext gegebenen realen L&auml;nge einer Zeiteinheit
         * gleichzusetzen. Sind au&szlig;erdem Zeiteinheiten nicht
         * konvertierbar, so hat auch die Standardl&auml;nge nur eine
         * rein informelle Bedeutung. Beispiel: Monate haben in ISO-Systemen
         * die L&auml;nge eines Jahres in Sekunden dividiert durch {@code 12},
         * w&auml;hrend im koptischen Kalender konstant der Dividend {@code 13}
         * betr&auml;gt. Hingegen ist eine Definition der L&auml;nge eines
         * Monats in Form von x Tagen ungeeignet, weil Monate und Tage
         * zueinander nicht konvertierbar sind. </p>
         *
         * <p>Als konvertierbar gilt die Zeiteinheit genau dann, wenn sie sich
         * mit einem festen ganzzahligen Faktor in eine andere Zeiteinheit
         * umrechnen l&auml;sst. Ist das nicht der Fall, dann ist ein leeres
         * {@code Set} anzugeben. Beispiel Minuten/Sekunden: Ohne einen
         * vorhandenen UTC-Kontext (mit m&ouml;glichen Schaltsekunden) sind
         * Minuten zu konstant 60 Sekunden konvertierbar. In einem UTC-Kontext
         * darf jedoch die Sekundeneinheit nicht als konvertierbar angegeben
         * werden und fehlt deshalb im Argument {@code convertibleUnits} zu
         * Minuten als zu registrierender Zeiteinheit (und umgekehrt). </p>
         *
         * @param   unit                time unit to be registered
         * @param   rule                associated unit rule
         * @param   length              estimated standard length in seconds
         * @param   convertibleUnits    other time units which {@code unit}
         *                              can be converted to
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given time unit is already
         *          registered (duplicate) or if given length does not represent
         *          any decimal number
         */
        public Builder<U, T> appendUnit(
            U unit,
            UnitRule<T> rule,
            double length,
            Set<? extends U> convertibleUnits
        ) {

            if (unit == null) {
                throw new NullPointerException("Missing time unit.");
            } else if (rule == null) {
                throw new NullPointerException("Missing unit rule.");
            }

            this.checkUnitDuplicates(unit);

            if (convertibleUnits.contains(null)) {
                throw new NullPointerException(
                    "Found convertible unit which is null.");
            }

            if (Double.isNaN(length)) {
                throw new IllegalArgumentException("Not a number: " + length);
            } else if (Double.isInfinite(length)) {
                throw new IllegalArgumentException("Infinite: " + length);
            }

            this.unitRules.put(unit, rule);
            this.unitLengths.put(unit, length);
            Set<U> set = new HashSet<U>(convertibleUnits);
            set.remove(unit); // Selbstbezug entfernen
            this.convertibleUnits.put(unit, set);
            return this;

        }

        @Override
        public Builder<U, T> appendExtension(ChronoExtension extension) {

            super.appendExtension(extension);
            return this;

        }

        /**
         * <p>Defines the argument as timeline to be used for stepping
         * forward or backwards. </p>
         *
         * @param   timeline    time line to be used
         * @return  this instance for method chaining
         * @since   2.0
         */
        /*[deutsch]
         * <p>Definiert das Argument als Zeitstrahl, auf dem schrittweise
         * vorw&auml;rts oder r&uuml;ckw&auml;rts gegangen wird. </p>
         *
         * @param   timeline    time line to be used
         * @return  this instance for method chaining
         * @since   2.0
         */
        public Builder<U, T> withTimeLine(TimeLine<T> timeline) {

            if (timeline == null) {
                throw new NullPointerException("Missing time line.");
            }

            this.timeline = timeline;
            return this;

        }

        /**
         * <p>Creates and registers a time axis. </p>
         *
         * @return  new chronology as time axis
         * @throws  IllegalStateException if already registered or in
         *          case of inconsistencies
         */
        /*[deutsch]
         * <p>Erzeugt und registriert eine Zeitachse. </p>
         *
         * @return  new chronology as time axis
         * @throws  IllegalStateException if already registered or in
         *          case of inconsistencies
         */
        @Override
        public TimeAxis<U, T> build() {

            if (this.unitRules.isEmpty()) {
                throw new IllegalStateException("No time unit was registered.");
            }

            TimeAxis<U, T> engine =
                new TimeAxis<U, T>(
                    this.chronoType,
                    this.unitType,
                    this.merger,
                    this.ruleMap,
                    this.unitRules,
                    this.unitLengths,
                    this.convertibleUnits,
                    this.extensions,
                    this.baseUnits,
                    this.min,
                    this.max,
                    this.calendarSystem,
                    this.timeline
                );

            Chronology.register(engine);
            return engine;

        }

        private void checkUnitDuplicates(U unit) {

            if (this.time4j) {
                return;
            }

            // Instanzprüfung
            for (U key : this.unitRules.keySet()) {
                if (key.equals(unit)) {
                    throw new IllegalArgumentException(
                        "Unit duplicate found: " + unit.toString());
                }
            }

            // Namensprüfung
            if (unit instanceof Enum) {
                String name = Enum.class.cast(unit).name();

                for (U key : this.unitRules.keySet()) {
                    if (
                        (key instanceof Enum)
                        && Enum.class.cast(key).name().equals(name)
                    ) {
                        throw new IllegalArgumentException(
                            "Unit duplicate found: " + name);
                    }
                }
            }

        }

    }

    private static class SelfElement<T extends TimePoint<?, T>>
        extends BasicElement<T>
        implements ElementRule<T, T> {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final long serialVersionUID = 4777240530511579802L;

        //~ Instanzvariablen ----------------------------------------------

        private final Class<T> type;
        private final T min;
        private final T max;

        //~ Konstruktoren -------------------------------------------------

        private SelfElement(
            Class<T> type,
            T min,
            T max
        ) {
            super(type.getName() + "-AXIS");

            this.type = type;
            this.min = min;
            this.max = max;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Class<T> getType() {

            return this.type;

        }

        @Override
        public T getDefaultMinimum() {

            return this.min;

        }

        @Override
        public T getDefaultMaximum() {

            return this.max;

        }

        @Override
        public boolean isDateElement() {

            return false;

        }

        @Override
        public boolean isTimeElement() {

            return false;

        }

        @Override
        public T getValue(T context) {

            return context;

        }

        @Override
        public T getMinimum(T context) {

            return this.getDefaultMinimum();

        }

        @Override
        public T getMaximum(T context) {

            return this.getDefaultMaximum();

        }

        @Override
        public boolean isValid(
            T context,
            T value
        ) {

            return (value != null);

        }

        @Override
        public T withValue(
            T context,
            T value,
            boolean lenient
        ) {

            if (value == null) {
                throw new NullPointerException("Missing value.");
            }

            return value;

        }

        @Override
        public ChronoElement<?> getChildAtFloor(T context) {

            throw new UnsupportedOperationException();

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(T context) {

            throw new UnsupportedOperationException();

        }

        @Override
        @SuppressWarnings("unchecked")
        protected <X extends ChronoEntity<X>> ElementRule<X, T> derive(
            Chronology<X> chronology
        ) {

            if (chronology.getChronoType().equals(this.type)) {
                return (ElementRule<X, T>) this;
            }

            return null;

        }

        @Override
        protected boolean isSingleton() {

            return true; // used only once per chronology

        }

        @Override
        protected String getVeto(Chronology<?> chronology) {

            return null;

        }

    }

    private static class DefaultTimeLine<U, T extends TimePoint<U, T>>
        implements TimeLine<T> {

        //~ Instanzvariablen ----------------------------------------------

        private final U step;
        private final T min;
        private final T max;

        //~ Konstruktoren -------------------------------------------------

        DefaultTimeLine(
            U step,
            T min,
            T max
        ) {
            super();

            this.step = step;
            this.min = min;
            this.max = max;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public T stepForward(T timepoint) {

            if (timepoint.compareTo(this.max) >= 0) {
                return null;
            }

            return timepoint.plus(1, this.step);

        }

        @Override
        public T stepBackwards(T timepoint) {

            if (timepoint.compareTo(this.min) <= 0) {
                return null;
            }

            return timepoint.minus(1, this.step);

        }

    }

}
