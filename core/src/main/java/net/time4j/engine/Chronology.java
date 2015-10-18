/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
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
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * <p>Represents a system of chronological elements which form any kind
 * of temporal value. </p>
 *
 * @param   <T> generic type compatible to {@link ChronoEntity}
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Repr&auml;sentiert ein System von chronologischen Elementen, die
 * zusammen einen zeitlichen Wert formen. </p>
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
     * <p>Returns the chronological type. </p>
     *
     * @return  type of time context
     */
    /*[deutsch]
     * <p>Liefert den chronologischen Typ. </p>
     *
     * @return  type of time context
     */
    public Class<T> getChronoType() {

        return this.chronoType;

    }

    /**
     * <p>Returns all registered chronological elements. </p>
     *
     * @return  unmodifiable set of elements without duplicates
     */
    /*[deutsch]
     * <p>Liefert den zu dieser Chronologie zugeh&ouml;rigen Satz
     * von registrierten chronologischen Elementen. </p>
     *
     * @return  unmodifiable set of elements without duplicates
     */
    public Set<ChronoElement<?>> getRegisteredElements() {

        return this.ruleMap.keySet();

    }

    /**
     * <p>Queries if given chronological element is registered together
     * with its element rule. </p>
     *
     * @param   element     element to be asked (optional)
     * @return  {@code true} if registered else {@code false}
     */
    /*[deutsch]
     * <p>Ist das angegebene chronologische Element inklusive Regel
     * registriert? </p>
     *
     * @param   element     element to be asked (optional)
     * @return  {@code true} if registered else {@code false}
     */
    public boolean isRegistered(ChronoElement<?> element) {

        return ((element != null) && this.ruleMap.containsKey(element));

    }

    /**
     * <p>Queries if given chronological element is supported by this
     * chronology. </p>
     *
     * <p>The element will be supported if it is either registered or
     * defines a suitable element rule for this chronology. </p>
     *
     * @param   element     element to be asked (optional)
     * @return  {@code true} if supported else {@code false}
     */
    /*[deutsch]
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
                || (this.getDerivedRule(element, false) != null)
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
    public ChronoDisplay preformat(
        T context,
        AttributeQuery attributes
    ) {

        return this.merger.preformat(context, attributes);

    }

    @Override
    public Chronology<?> preparser() {

        return this.merger.preparser();

    }

    @Override
    public String getFormatPattern(
        DisplayStyle style,
        Locale locale
    ) {

        return this.merger.getFormatPattern(style, locale);

    }

    /**
     * <p>Returns all registered chronological extensions. </p>
     *
     * <p>This method will be called by format-API in order to collect
     * all extension elements which are relevant for formatting. </p>
     *
     * @return  unmodifiable list of extensions
     */
    /*[deutsch]
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
     * <p>Queries if this chronology has a calendar system. </p>
     *
     * @return  {@code true} if this chronology has a calendar system else {@code false}
     * @see     #getCalendarSystem()
     */
    /*[deutsch]
     * <p>Ermittelt, ob diese Chronologie ein Kalendersystem hat. </p>
     *
     * @return  {@code true} if this chronology has a calendar system else {@code false}
     * @see     #getCalendarSystem()
     */
    public boolean hasCalendarSystem() {

        return false;

    }

    /**
     * <p>Returns the associated calendar system if available. </p>
     *
     * @return  calendar system, not {@code null}
     * @throws  ChronoException if the calendar system is unavailable or if there is more than one variant
     * @see     #hasCalendarSystem()
     */
    /*[deutsch]
     * <p>Liefert das assoziierte Kalendersystem, wenn verf&uuml;gbar. </p>
     *
     * @return  calendar system, not {@code null}
     * @throws  ChronoException if the calendar system is unavailable or if there is more than one variant
     * @see     #hasCalendarSystem()
     */
    public CalendarSystem<T> getCalendarSystem() {

        throw new ChronoException("Calendar system is not available.");

    }

    /**
     * <p>Returns the calendar system for given calendar variant if available. </p>
     *
     * @param   variant     name of calendar variant
     * @return  calendar system, not {@code null}
     * @throws  ChronoException if a calendar system is unavailable for given variant (invalid variant name)
     * @since   3.4/4.3
     * @see     CalendarVariant#getVariant()
     */
    /*[deutsch]
     * <p>Liefert das Kalendersystem zur angegebenen Kalendervariante, wenn verf&uuml;gbar. </p>
     *
     * @param   variant     name of calendar variant
     * @return  calendar system, not {@code null}
     * @throws  ChronoException if a calendar system is unavailable for given variant (invalid variant name)
     * @since   3.4/4.3
     * @see     CalendarVariant#getVariant()
     */
    public CalendarSystem<T> getCalendarSystem(String variant) {

        throw new ChronoException("Calendar variant is not available: " + variant);

    }

    /**
     * <p>Returns a typed singleton per {@code ChronoEntity}-class. </p>
     *
     * @param   <T> generic type of time context
     * @param   chronoType  chronological type
     * @return  chronology or {@code null} if not found
     */
    /*[deutsch]
     * <p>Liefert ein getyptes Singleton pro {@code ChronoEntity}-Klasse. </p>
     *
     * @param   <T> generic type of time context
     * @param   chronoType  chronological type
     * @return  chronology or {@code null} if not found
     */
    public static <T extends ChronoEntity<T>> Chronology<T> lookup(Class<T> chronoType) {

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
     * <p>Die Registrierung ist zur Unterst&uuml;tzung der Methode {@link #lookup(Class)} gedacht und wird
     * einmalig nach Konstruktion einer Chronologie w&auml;hrend des Ladens der assoziierten Entit&auml;tsklasse
     * aufgerufen. </p>
     *
     * @param   chronology  new instance to be registered
     */
    static void register(Chronology<?> chronology) {

        CHRONOS.add(new ChronoReference(chronology, QUEUE));

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

        if (element == null) {
            throw new NullPointerException("Missing chronological element.");
        }

        ElementRule<?, ?> rule = this.ruleMap.get(element);

        if (rule == null) {
            rule = this.getDerivedRule(element, true);

            if (rule == null) {
                throw new RuleNotFoundException(this, element);
            }
        }

        return cast(rule); // type-safe

    }

    // optional
    private ElementRule<T, ?> getDerivedRule(
        ChronoElement<?> element,
        boolean wantsVeto
    ) {

        if (element instanceof BasicElement) {
            BasicElement<?> e = BasicElement.class.cast(element);

            String veto = (wantsVeto ? e.getVeto(this) : null);

            if (veto == null) {
                return e.derive(this);
            } else {
                throw new RuleNotFoundException(veto);
            }
        }

        return null;

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

    @SuppressWarnings("unchecked")
    private static <T> T cast(Object obj) {

        return (T) obj;

    }

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Builder for creating a new chronology without any time axis.
     *
     * <p>This class will be used during loading of a {@code ChronoEntity}-class
     * T in a <i>static initializer</i>. </p>
     *
     * @param       <T> generic type of time context
     * @author      Meno Hochschild
     */
    /*[deutsch]
     * <p>Erzeugt eine neue Chronologie ohne Zeitachse und wird
     * beim Laden einer {@code ChronoEntity}-Klasse T in einem
     * <i>static initializer</i> benutzt. </p>
     *
     * @param       <T> generic type of time context
     * @author      Meno Hochschild
     */
    public static class Builder<T extends ChronoEntity<T>> {

        //~ Instanzvariablen ----------------------------------------------

        final Class<T> chronoType;
        final boolean time4j;
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

            if (merger == null) {
                throw new NullPointerException("Missing chronological merger.");
            }

            this.chronoType = chronoType;
            this.time4j = chronoType.getName().startsWith("net.time4j.");
            this.merger = merger;
            this.ruleMap = new HashMap<ChronoElement<?>, ElementRule<T, ?>>();
            this.extensions = new ArrayList<ChronoExtension>();

        }

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Creates a builder for building a new chronological system. </p>
         *
         * @param   <T> generic type of time context
         * @param   chronoType      chronological type
         * @param   chronoMerger    creates a new instance of T from another
         *                          source (clock or parsed values)
         * @return  new {@code Builder} object
         * @throws  UnsupportedOperationException if T represents a subclass
         *          of {@code TimePoint}
         */
        /*[deutsch]
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
         * <p>Registers a new element together with its associated
         * element rule. </p>
         *
         * @param   <V> generic type of element value
         * @param   element     chronological element to be registered
         * @param   rule        rule associated with the element
         * @return  this instance for method chaining
         * @throws  IllegalArgumentException if given element is already
         *          registered (duplicate)
         */
        /*[deutsch]
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
         * <p>Registers a state extension which can create models with their
         * own state separated from standard time value context. </p>
         *
         * @param   extension   chronological extension
         * @return  this instance for method chaining
         */
        /*[deutsch]
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
         * <p>Finishes the build of a new chronology. </p>
         *
         * <p>Internally the new chronology will be weakly registered for
         * {@code lookup()}. Therefore it is strongly recommended to
         * reference the created chronology in a static constant within
         * the chronological type in question. </p>
         *
         * @return  new instance of chronology
         * @throws  IllegalStateException if already registered
         * @see     Chronology#lookup(Class)
         */
        /*[deutsch]
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

            if (this.time4j) {
                return;
            } else if (element == null) {
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
