/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Chronology.java) is part of project Time4J.
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

import net.time4j.base.TimeSource;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * <p>Repr&auml;sentiert ein System von chronologischen Elementen. </p>
 *
 * @param   <T> generic type compatible to {@link ChronoEntity}
 * @author  Meno Hochschild
 */
public class Chronology<T extends ChronoEntity<T>>
    implements ChronoMerger<T> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final List<ChronoReference> CHRONOS =
        new CopyOnWriteArrayList<ChronoReference>();
    private static final ReferenceQueue<Chronology<?>> QUEUE =
        new ReferenceQueue<Chronology<?>>();

    //~ Instanzvariablen --------------------------------------------------

    private final Class<T> chronoType;
    private final ChronoMerger<T> merger;
    private final Map<ChronoElement<?>, ElementRule<T, ?>> ruleMap;
    private final List<ChronoExtension> extensions;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Standard-Konstruktor. </p>
     *
     * <p>Implementierungshinweis: Subklassen sollten grunds&auml;tzlich
     * dem Singleton-Muster folgen und deshalb keinen &ouml;ffentlichen
     * Konstruktor bieten. </p>
     *
     * @param   chronoType      chronological type
     * @param   chronoMerger    creates a new instance of T based on infos
     *                          from another source
     * @param   ruleMap         registered elements and rules
     * @param   extensions      optional extensions
     */
    Chronology(
        Class<T> chronoType,
        ChronoMerger<T> chronoMerger,
        Map<ChronoElement<?>, ElementRule<T, ?>> ruleMap,
        List<ChronoExtension> extensions
    ) {
        super();

        if (chronoType == null) {
            throw new NullPointerException("Missing chronological type.");
        } else if (chronoMerger == null) {
            throw new NullPointerException("Missing chronological merger.");
        }

        this.chronoType = chronoType;
        this.merger = chronoMerger;
        this.ruleMap = Collections.unmodifiableMap(ruleMap);
        this.extensions = Collections.unmodifiableList(extensions);

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Liefert den chronologischen Typ. </p>
     *
     * @return  type of time context
     */
    public Class<T> getChronoType() {

        return this.chronoType;

    }

    /**
     * <p>Liefert den zu dieser Chronologie zugeh&ouml;rigen Satz
     * von registrierten chronologischen Elementen. </p>
     *
     * @return  unmodifiable set of elements without duplicates
     */
    public Set<ChronoElement<?>> getRegisteredElements() {

        return this.ruleMap.keySet();

    }

    /**
     * <p>Ist das angegebene chronologische Element inklusive Regel
     * registriert? </p>
     *
     * @param   element     element to be asked (optional)
     * @return  {@code true} if registered else {@code false}
     */
    public boolean isRegistered(ChronoElement<?> element) {

        if (element == null) {
            return false;
        }

        return this.ruleMap.containsKey(element);

    }

    /**
     * <p>Wird das angegebene chronologische Element unterst&uuml;tzt? </p>
     *
     * <p>Unterst&uuml;tzung ist gegeben, wenn das Element entweder registriert
     * ist oder eine zu dieser Chronologie passende Regel definiert. </p>
     *
     * @param   element     element to be asked (optional)
     * @return  {@code true} if supported else {@code false}
     */
    public boolean isSupported(ChronoElement<?> element) {

        if (element == null) {
            return false;
        } else {
            return (
                this.isRegistered(element)
                || (this.getDerivedRule(element) != null)
                || (this.getEpochRule(element) != null)
            );
        }

    }

    @Override
    public T createFrom(
        TimeSource<?> clock,
        AttributeQuery attributes
    ) {

        if (attributes == null) {
            throw new NullPointerException("Missing attributes.");
        }

        return this.merger.createFrom(clock, attributes);

    }

    @Override
    public T createFrom(
        ChronoEntity<?> entity,
        AttributeQuery attributes,
        boolean preparsing
    ) {

        if (attributes == null) {
            throw new NullPointerException("Missing attributes.");
        }

        return this.merger.createFrom(entity, attributes, preparsing);

    }

    @Override
    public ChronoEntity<?> preformat(
        T context,
        AttributeQuery attributes
    ) {

        return this.merger.preformat(context, attributes);

    }

    @Override
    public Chronology<?> preparser() {

        return this.merger.preparser();

    }

    /**
     * <p>Liefert die registrierten chronologischen Erweiterungen. </p>
     *
     * <p>Diese Methode wird vom Format-API aufgerufen, um zus&auml;tzlich
     * zu den registrierten Elementen auch alle Erweiterungselemente zu
     * sammeln, die f&uuml;r die Formatierung von Bedeutung sind. </p>
     *
     * @return  unmodifiable list of extensions
     */
    public List<ChronoExtension> getExtensions() {

        return this.extensions;

    }

    /**
     * <p>Ermittelt, ob diese Chronologie ein Kalendersystem hat. </p>
     *
     * @return  {@code true} if this chronology has a calendar system
     *          else {@code false}
     * @see     #getCalendarSystem()
     */
    public boolean hasCalendarSystem() {

        return false;

    }

    /**
     * <p>Liefert das assoziierte Kalendersystem, wenn verf&uuml;gbar. </p>
     *
     * @return  calendar system, not {@code null}
     * @throws  ChronoException if the calendar system is unavailable
     * @see     #hasCalendarSystem()
     */
    public CalendarSystem<T> getCalendarSystem() {

        throw new ChronoException("Calendar system is not available.");

    }

    /**
     * <p>Liefert ein getyptes Singleton pro {@code ChronoEntity}-Klasse. </p>
     *
     * @param   <T> generic type of time context
     * @param   chronoType  chronological type
     * @return  chronology or {@code null} if not found
     */
    public static <T extends ChronoEntity<T>>
    Chronology<T> lookup(Class<T> chronoType) {

        try {
            // Initialisierung der Klasse anstoßen, wenn noch nicht erfolgt
            Class.forName(
                chronoType.getName(),
                true,
                chronoType.getClassLoader());
        } catch (ClassNotFoundException cnfe) {
            throw new IllegalStateException(cnfe);
        }

        Chronology<?> ret = null;
        boolean purged = false;

        for (ChronoReference cref : CHRONOS) {
            Chronology<?> chronology = cref.get();

            if (chronology == null) {
                purged = true;
            } else if (chronology.getChronoType() == chronoType) {
                ret = chronology;
                break;
            }
        }

        if (purged) {
            purgeQueue();
        }

        return cast(ret); // type-safe

    }

    /**
     * <p>Registriert die angegebene Chronologie. </p>
     *
     * <p>Die Registrierung ist zur Unterst&uuml;tzung der Methode
     * {@link #lookup(Class)} gedacht und wird einmalig nach Konstruktion
     * einer Chronologie aufgerufen. </p>
     *
     * @param   chronology  new instance to be registered
     * @throws  IllegalStateException if already registered
     */
    static void register(Chronology<?> chronology) {

        synchronized (CHRONOS) {
            Class<?> chronoType = chronology.getChronoType();

            for (ChronoReference cref : CHRONOS) {
                Chronology<?> test = cref.get();
                if (
                    (test != null)
                    && (test.getChronoType() == chronoType)
                ) {
                    throw new IllegalStateException(
                        chronoType.getName() + " is already installed.");
                }
            }

            CHRONOS.add(new ChronoReference(chronology, QUEUE));
        }

    }

    /**
     * <p>Bestimmt eine chronologische Regel zum angegebenen Element. </p>
     *
     * @param   <V> Elementwerttyp
     * @param   element     chronologisches Element
     * @return  Regelobjekt
     * @throws  RuleNotFoundException if given element is not registered in
     *          this chronology and there is also no element rule which can
     *          be derived from element
     */
    <V> ElementRule<T, V> getRule(ChronoElement<V> element) {

        return this.getRule(element, true);

    }

    private <V> ElementRule<T, V> getRule(
        ChronoElement<V> element,
        boolean withEpochMechanism
    ) {

        if (element == null) {
            throw new NullPointerException("Missing chronological element.");
        }

        ElementRule<?, ?> rule = this.ruleMap.get(element);

        if (rule == null) {
            rule = this.getDerivedRule(element);

            if ((rule == null) && withEpochMechanism) {
                rule = this.getEpochRule(element);

                if (rule == null) {
                    throw new RuleNotFoundException(this, element);
                }
            }
        }

        return cast(rule); // type-safe

    }

    // optional
    private <V> ElementRule<T, V> getDerivedRule(ChronoElement<V> element) {

        if (element instanceof BasicElement) {
            BasicElement<V> e = (BasicElement<V>) element;
            return e.derive(this);
        }

        return null;

    }

    // optional
    private <V> ElementRule<?, ?> getEpochRule(ChronoElement<V> element) {

        ElementRule<?, ?> ret = null;

        if (Calendrical.class.isAssignableFrom(this.chronoType)) {
            Chronology<?> foreign = null;
            boolean purged = false;

            for (ChronoReference cref : CHRONOS) {
                Chronology<?> c = cref.get();

                if (c == null) {
                    purged = true;
                } else if (
                    (c != this)
                    && c.isRegistered(element)
                    && Calendrical.class.isAssignableFrom(c.getChronoType())
                ) {
                    foreign = c;
                    break;
                }
            }

            if (purged) {
                purgeQueue();
            }

            if (foreign != null) {
                ret =
                    createRuleByEpoch(
                        element,
                        foreign.getChronoType(),
                        this.chronoType
                    );
            }
        }

        return ret;

    }

    // vom GC behandelte Referenzen wegräumen
    private static void purgeQueue() {

        ChronoReference cref;

        while ((cref = (ChronoReference) QUEUE.poll()) != null) {
            for (ChronoReference test : CHRONOS) {
                if (test.name.equals(cref.name)) {
                    CHRONOS.remove(test);
                    break;
                }
            }
        }

    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static ElementRule<?, ?> createRuleByEpoch(
        ChronoElement<?> element,
        Class fc, // foreign chronology
        Class tc // this chronology
    ) {


        ElementRule rule = Chronology.lookup(fc).getRule(element, false);
        return new TransformingRule(rule, fc, tc);

    }

    @SuppressWarnings("unchecked")
    private static <T> T cast(Object obj) {

        return (T) obj;

    }

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Erzeugt eine neue Chronologie ohne Zeitachse und wird meist
     * beim Laden einer {@code ChronoEntity}-Klasse T in einem
     * <i>static initializer</i> benutzt. </p>
     *
     * @param       <T> generic type of time context
     * @author      Meno Hochschild
     * @concurrency <mutable>
     */
    public static class Builder<T extends ChronoEntity<T>> {

        //~ Instanzvariablen ----------------------------------------------

        final Class<T> chronoType;
        final ChronoMerger<T> merger;
        final Map<ChronoElement<?>, ElementRule<T, ?>> ruleMap;
        final List<ChronoExtension> extensions;

        //~ Konstruktoren -------------------------------------------------

        /**
         * <p>Konstruiert eine neue Instanz. </p>
         *
         * @param   chronoType      chronological type
         * @param   merger          creates a new instance of T from another
         *                          source (clock or parsed values)
         */
        Builder(
            Class<T> chronoType,
            ChronoMerger<T> merger
        ) {
            super();

            if (chronoType == null) {
                throw new NullPointerException("Missing chronological type.");
            } else if (merger == null) {
                throw new NullPointerException("Missing chronological merger.");
            }

            this.chronoType = chronoType;
            this.merger = merger;
            this.ruleMap = new HashMap<ChronoElement<?>, ElementRule<T, ?>>();
            this.extensions = new ArrayList<ChronoExtension>();

        }

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Erzeugt ein Hilfsobjekt zum Bauen eines chronologischen
         * Systems. </p>
         *
         * @param   <T> generic type of time context
         * @param   chronoType      chronological type
         * @param   chronoMerger    creates a new instance of T from another
         *                          source (clock or parsed values)
         * @return  new {@code Builder} object
         * @throws  UnsupportedOperationException if T represents a subclass
         *          of {@code TimePoint}
         */
        public static <T extends ChronoEntity<T>> Builder<T> setUp(
            Class<T> chronoType,
            ChronoMerger<T> chronoMerger
        ) {

            if (TimePoint.class.isAssignableFrom(chronoType)) {
                throw new UnsupportedOperationException(
                    "This builder cannot construct a chronology "
                    + "with a time axis, use TimeAxis.Builder instead.");
            }

            return new Builder<T>(chronoType, chronoMerger);

        }

        /**
         * <p>Registriert ein neues Element mitsamt der assoziierten Regel. </p>
         *
         * @param   <V> generic type of element value
         * @param   element     chronological element to be registered
         * @param   rule        rule associated with the element
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given element is already
         *          registered (duplicate)
         */
        public <V> Builder<T> appendElement(
            ChronoElement<V> element,
            ElementRule<T, V> rule
        ) {

            this.checkElementDuplicates(element);
            this.ruleMap.put(element, rule);
            return this;

        }

        /**
         * <p>Registriert eine Zustandserweiterung, die Modelle mit einem
         * eigenen Zustand separat vom Zeitwertkontext erzeugen kann. </p>
         *
         * @param   extension   chronological extension
         * @return  this instance for method chaining
         */
        public Builder<T> appendExtension(ChronoExtension extension) {

            if (extension == null) {
                throw new NullPointerException(
                    "Missing chronological extension.");
            } else if (!this.extensions.contains(extension)) {
                this.extensions.add(extension);
            }

            return this;

        }

        /**
         * <p>Schlie&szlig;t den Build-Vorgang ab. </p>
         *
         * <p>Intern wird die neue Chronologie f&uuml;r {@code lookup()}
         * schwach registriert. Es wird daher empfohlen, da&szlig; eine
         * Anwendung zus&auml;tzlich die erzeugte Chronologie in einer
         * eigenen statischen Konstanten referenziert. </p>
         *
         * @return  new instance of chronology
         * @throws  IllegalStateException if already registered
         * @see     Chronology#lookup(Class)
         */
        public Chronology<T> build() {

            final Chronology<T> chronology =
                new Chronology<T>(
                    this.chronoType,
                    this.merger,
                    this.ruleMap,
                    this.extensions
                );

            Chronology.register(chronology);
            return chronology;

        }

        private void checkElementDuplicates(ChronoElement<?> element) {

            if (element == null) {
                throw new NullPointerException(
                    "Static initialization problem: "
                    + "Check if given element statically refer "
                    + "to any chronology causing premature class loading.");
            }

            String elementName = element.name();

            for (ChronoElement<?> key : this.ruleMap.keySet()) {
                if (
                    key.equals(element)
                    || key.name().equals(elementName)
                ) {
                    throw new IllegalArgumentException(
                        "Element duplicate found: " + elementName);
                }
            }

        }

    }

    // Transformiert eine Elementregel zwischen S und T via Calendrical
    private static class TransformingRule
        <S extends Calendrical<?, S>, T extends Calendrical<?, T>, V>
        implements ElementRule<T, V> {

        //~ Instanzvariablen ----------------------------------------------

        private final ElementRule<S, V> rule;
        private final Class<S> s;
        private final Class<T> t;

        //~ Konstruktoren -------------------------------------------------

        TransformingRule(
            ElementRule<S, V> rule,
            Class<S> s,
            Class<T> t
        ) {
            super();

            this.rule = rule;
            this.s = s;
            this.t = t;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public V getValue(T context) {

            S src = context.transform(this.s);
            return this.rule.getValue(src);

        }

        @Override
        public T withValue(
            T context,
            V value,
            boolean lenient
        ) {

            S src = context.transform(this.s);
            return this.rule.withValue(src, value, lenient).transform(this.t);

        }

        @Override
        public boolean isValid(
            T context,
            V value
        ) {

            S src = context.transform(this.s);
            return this.rule.isValid(src, value);

        }

        @Override
        public V getMinimum(T context) {

            S src = context.transform(this.s);
            return this.rule.getMinimum(src);

        }

        @Override
        public V getMaximum(T context) {

            S src = context.transform(this.s);
            return this.rule.getMaximum(src);

        }

        @Override
        public ChronoElement<?> getChildAtFloor(T context) {

            S src = context.transform(this.s);
            return this.rule.getChildAtFloor(src);

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(T context) {

            S src = context.transform(this.s);
            return this.rule.getChildAtCeiling(src);

        }

    }

    // Schwache Referenz auf ein chronologisches System
    private static class ChronoReference
        extends WeakReference<Chronology<?>> {

        //~ Instanzvariablen ----------------------------------------------

        private final String name;

        //~ Konstruktoren -------------------------------------------------

        ChronoReference(
            Chronology<?> chronology,
            ReferenceQueue<Chronology<?>> queue
        ) {
            super(chronology, queue);
            this.name = chronology.chronoType.getName();

        }

    }

}
