/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimeAxis.java) is part of project Time4J.
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

package net.time4j.engine;

import de.menodata.annotations4j.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * <p>Eine Zeitachse ist eine dynamische Sicht auf eine Chronologie,
 * in der mit Hilfe eines Systems von Zeiteinheiten eine Zeitarithmetik auf
 * beliebigen Zeitpunkten der Chronologie definiert wird. </p>
 *
 * @param   <U> generischer Zeiteinheitstyp
 * @param   <T> generischer Zeitpunkttyp (kompatibel zu {@link TimePoint})
 * @author  Meno Hochschild
 */
public final class TimeAxis<U, T>
    extends Chronology<T>
    implements Comparator<U> {

    //~ Instanzvariablen --------------------------------------------------

    private final Class<U> unitType;
    private final Map<U, UnitRule<T>> unitRules;
    private final Map<U, Double> unitLengths;
    private final Map<U, Set<U>> convertibleUnits;
    private final Map<ChronoElement<?>, U> baseUnits;
    private final T min;
    private final T max;
    private final CalendarSystem<T> calendarSystem;

    //~ Konstruktoren -----------------------------------------------------

    private TimeAxis(
        Class<T> chronoType,
        Class<U> unitType,
        ChronoMerger<T> merger,
        Map<ChronoElement<?>, ElementRule<T, ?>> ruleMap,
        Map<U, UnitRule<T>> unitRules,
        Map<U, Double> unitLengths,
        Map<U, Set<U>> convertibleUnits,
        List<ChronoExtension> extensions,
        Map<ChronoElement<?>, U> baseUnits,
        T min,
        T max,
        @Nullable CalendarSystem<T> calendarSystem
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

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Liefert den Zeiteinheitstyp. </p>
     *
     * @return  Zeiteinheitenklasse
     */
    public Class<U> getUnitType() {

        return this.unitType;

    }

    /**
     * <p>Liefert alle registrierten Zeiteinheiten. </p>
     *
     * @return  unver&auml;nderliche Menge von Einheiten ohne Duplikate
     */
    public Set<U> getRegisteredUnits() {

        return this.unitRules.keySet();

    }

    /**
     * <p>Ist die angegebene Zeiteinheit registriert? </p>
     *
     * @param   unit    Zeiteinheit
     * @return  {@code true} wenn registriert, sonst {@code false}
     */
    public boolean isRegistered(@Nullable U unit) {

        return this.unitRules.containsKey(unit);

    }

    /**
     * <p>Wird die angegebene Zeiteinheit unterst&uuml;tzt? </p>
     *
     * <p>Unterst&uuml;tzung ist gegeben, wenn die Einheit entweder registriert
     * ist oder eine zu dieser Chronologie passende Regel definiert. </p>
     *
     * @param   unit    Zeiteinheit
     * @return  {@code true} wenn unterst&uuml;tzt, sonst {@code false}
     * @see     UnitRule.Source
     */
    public boolean isSupported(@Nullable U unit) {

        if (this.isRegistered(unit)) {
            return true;
        } else if (unit instanceof UnitRule.Source) {
            UnitRule.Source ruleSource = UnitRule.Source.class.cast(unit);
            return (ruleSource.derive(narrow(this)) != null);
        } else {
            return false;
        }

    }

    /**
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
     * @param   unit    Zeiteinheit
     * @return  gesch&auml;tzte Standard-L&auml;nge in Sekunden oder
     *          {@code Double.NaN} wenn nicht ermittelbar
     * @see     ChronoUnit
     */
    public double getLength(U unit) {

        Double length = this.unitLengths.get(unit);

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

    /**
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
     * @param   unit1   erste Zeiteinheit
     * @param   unit2   zweite Zeiteinheit
     * @return  {@code true} wenn konvertierbar, sonst {@code false}
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
     * <p>Vergleicht Zeiteinheiten nach aufsteigender Genauigkeit. </p>
     *
     * @param   unit1   erste Zeiteinheit
     * @param   unit2   zweite Zeiteinheit
     * @return  Vergleichsergebnis
     */
    @Override
    public int compare(
        U unit1,
        U unit2
    ) {

        return Double.compare(this.getLength(unit2), this.getLength(unit1));

    }

    /**
     * <p>Liefert die Basiseinheit zum angegebenen Element. </p>
     *
     * @param   element     chronologisches Element
     * @return  gesuchte Basiseinheit oder {@code null}
     */
    @Nullable
    public U getBaseUnit(ChronoElement<?> element) {

        return this.baseUnits.get(element);

    }

    /**
     * <p>Ermittelt das Minimum auf der Zeitachse. </p>
     *
     * @return  fr&uuml;hestm&ouml;glicher Zeitpunkt
     */
    public T getMinimum() {

        return this.min;

    }

    /**
     * <p>Ermittelt das Maximum auf der Zeitachse. </p>
     *
     * @return  sp&auml;testm&ouml;glicher Zeitpunkt
     */
    public T getMaximum() {

        return this.max;

    }

    @Override
    public List<CalendarEra> getEras() {

        if (this.calendarSystem == null) {
            return super.getEras();
        } else {
            return this.calendarSystem.getEras();
        }

    }

    /**
     * <p>Liefert die chronologische Regel zur angegebenen Zeiteinheit. </p>
     *
     * @param   unit    Zeiteinheit
     * @return  Regelobjekt oder {@code null} wenn nicht registriert
     */
    @Nullable
    UnitRule<T> getRule(U unit) {

        return this.unitRules.get(unit);

    }

    /**
     * <p>Liefert das assoziierte Kalendersystem, wenn verf&uuml;gbar. </p>
     *
     * @return  Kalendersystem
     * @throws  ChronoException wenn kein Kalendersystem verf&uuml;gbar ist
     */
    @Override
    CalendarSystem<T> getCalendarSystem() {

        if (this.calendarSystem == null) {
            return super.getCalendarSystem();
        } else {
            return this.calendarSystem;
        }

    }

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Erzeugt eine neue Zeitachse respektive Chronologie und wird
     * ausschlie&szlig;lich beim Laden einer {@code TimePoint}-Klasse T
     * in einem <i>static initializer</i> benutzt. </p>
     *
     * <p>Instanzen dieser Klasse werden &uuml;ber die statischen
     * {@code setUp()}-Fabrikmethoden erzeugt. </p>
     *
     * @param   <U> Zeiteinheitstyp
     * @param   <T> Typ des Zeitwertkontexts
     * @author  Meno Hochschild
     * @see     #setUp(Class,Class,ChronoMerger,TimePoint,TimePoint)
     * @see     #setUp(Class,Class,ChronoMerger,CalendarSystem)
     */
    public static final class Builder<U, T>
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

        //~ Konstruktoren -------------------------------------------------

        private Builder(
            Class<U> unitType,
            Class<T> chronoType,
            ChronoMerger<T> merger,
            T min,
            T max,
            @Nullable CalendarSystem<T> calendarSystem
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

        }

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Erzeugt ein Hilfsobjekt zum Bauen eines chronologischen, aber
         * nicht kalendarischen Systems. </p>
         *
         * @param   <U> Zeiteinheitstyp
         * @param   <T> Typ des Zeitwertkontexts
         * @param   unitType        Typ der zugeh&ouml;rigen Zeiteinheiten
         * @param   chronoType      chronologischer Typ (Zeitpunktklasse)
         * @param   merger          generischer Ersatz f&uuml;r eine statische
         *                          Konstruktion von Zeitpunkten
         * @param   min             Minimalwert auf der Zeitachse
         * @param   max             Maximalwert auf der Zeitachse
         * @return  neues {@code Builder}-Objekt
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
                null);

        }

        /**
         * <p>Erzeugt ein Hilfsobjekt zum Bauen einer Zeitachse f&uuml;r
         * reine Datumsangaben. </p>
         *
         * @param   <U> Zeiteinheitstyp
         * @param   <D> Datumstyp des Zeitwertkontexts
         * @param   unitType        Typ der zugeh&ouml;rigen Zeiteinheiten
         * @param   chronoType      chronologischer Typ (Datumsklasse)
         * @param   merger          generischer Ersatz f&uuml;r eine statische
         *                          Konstruktion von Datumsangaben
         * @param   calendarSystem  Kalendersystem
         * @return  neues {@code Builder}-Objekt
         */
        public static <U, D extends Calendrical<U, D>> Builder<U, D> setUp(
            Class<U> unitType,
            Class<D> chronoType,
            ChronoMerger<D> merger,
            CalendarSystem<D> calendarSystem
        ) {

            final CalendarSystem<D> calsys = calendarSystem;

            return new Builder<U, D>(
                unitType,
                chronoType,
                merger,
                calsys.transform(calsys.getMinimumOfEpochDays()),
                calsys.transform(calsys.getMaximumOfEpochDays()),
                calendarSystem
            );

        }

        /**
         * <p>Registriert ein neues Element mitsamt der assoziierten Regel. </p>
         *
         * @param   <V> Elementwerttyp
         * @param   element     zu registrierendes chronologisches Element
         * @param   rule        mit dem Element zu installierende Regel
         * @return  diese Instanz f&uuml;r Methodenverkettungen
         * @throws  IllegalArgumentException wenn das Element schon
         *          registriert wurde (Duplikat)
         */
        @Override
        public <V> Builder<U, T> appendElement(
            ChronoElement<V> element,
            ElementRule<T, V> rule
        ) {

            super.appendElement(element, rule);
            return this;

        }

        /**
         * <p>Registriert ein neues Element mitsamt der assoziierten Regel
         * und einer Basiseinheit. </p>
         *
         * @param   <V> Elementwerttyp
         * @param   element     zu registrierendes chronologisches Element
         * @param   rule        mit dem Element zu installierende Regel
         * @param   baseUnit    Basiseinheit f&uuml;r Rolloperationen
         * @return  diese Instanz f&uuml;r Methodenverkettungen
         * @throws  IllegalArgumentException wenn das Element schon
         *          registriert wurde (Duplikat)
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
         * <p>Registriert eine neue nicht-konvertierbare Zeiteinheit mitsamt
         * Einheitsregel. </p>
         *
         * <p>Entspricht {@link #appendUnit(Object,UnitRule,double,Set)
         * appendUnit(U, rule, length, Collections.emptySet())}. </p>
         *
         * @param   unit                zu registrierende Zeiteinheit
         * @param   rule                Regel zur Zeiteinheit
         * @param   length              Standardl&auml;nge in Sekunden
         * @return  diese Instanz f&uuml;r Methodenverkettungen
         * @throws  IllegalArgumentException wenn die Zeiteinheit schon
         *          registriert wurde (Duplikat) oder wenn keine Dezimalzahl
         *          als Standardl&auml;nge angegeben wird
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
         * @param   unit                zu registrierende Zeiteinheit
         * @param   rule                Regel zur Zeiteinheit
         * @param   length              Standardl&auml;nge in Sekunden
         * @param   convertibleUnits    andere Zeiteinheiten, zu denen
         *                              {@code unit} konvertiert werden kann
         * @return  diese Instanz f&uuml;r Methodenverkettungen
         * @throws  IllegalArgumentException wenn die Zeiteinheit schon
         *          registriert wurde (Duplikat) oder wenn keine Dezimalzahl
         *          als Standardl&auml;nge angegeben wird
         */
        public Builder<U, T> appendUnit(
            U unit,
            UnitRule<T> rule,
            double length,
            Set<? extends U> convertibleUnits
        ) {

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

        /**
         * <p>Erzeugt und registriert eine Zeitachse. </p>
         *
         * @return  neue Zeitachse
         * @throws  IllegalStateException wenn bereits registriert oder
         *          bei inkonsistentem Zustand
         */
        @Override
        public TimeAxis<U, T> build() {

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
                    this.calendarSystem
                );

            Chronology.register(engine);
            return engine;

        }

        private void checkUnitDuplicates(U unit) {

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

}
