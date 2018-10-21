/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CalendarFamily.java) is part of project Time4J.
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

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * <p>Represents a set of various calendar systems as members of a family. </p>
 *
 * @param   <T> generic type compatible to {@link CalendarVariant}
 * @author  Meno Hochschild
 * @since   3.4/4.3
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Familie von miteinander verwandten Kalendersystemen. </p>
 *
 * @param   <T> generic type compatible to {@link CalendarVariant}
 * @author  Meno Hochschild
 * @since   3.4/4.3
 */
public final class CalendarFamily<T extends CalendarVariant<T>>
    extends Chronology<T> {

    //~ Instanzvariablen --------------------------------------------------

    private final Map<String, ? extends CalendarSystem<T>> calendars; // must never be exposed

    //~ Konstruktoren -----------------------------------------------------

    private CalendarFamily(
        Class<T> chronoType,
        ChronoMerger<T> chronoMerger,
        Map<ChronoElement<?>, ElementRule<T, ?>> ruleMap,
        List<ChronoExtension> extensions,
        Map<String, ? extends CalendarSystem<T>> calendars
    ) {
        super(chronoType, chronoMerger, ruleMap, extensions);

        this.calendars = calendars;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public boolean hasCalendarSystem() {

        return true;

    }

    /**
     * <p>Not supported because the variant is missing. </p>
     *
     * @return  nothing
     * @throws  ChronoException (always)
     * @see     #getCalendarSystem(String)
     * @see     Chronology#getCalendarSystem(VariantSource)
     */
    /*[deutsch]
     * <p>Nicht unterst&uuml;tzt, weil der Variantenbezug fehlt. </p>
     *
     * @return  nothing
     * @throws  ChronoException (always)
     * @see     #getCalendarSystem(String)
     * @see     Chronology#getCalendarSystem(VariantSource)
     */
    @Override
    public CalendarSystem<T> getCalendarSystem() {

        throw new ChronoException("Cannot determine calendar system without variant.");

    }

    @Override
    public CalendarSystem<T> getCalendarSystem(String variant) {

        if (variant.isEmpty()) {
            return this.getCalendarSystem();
        }

        CalendarSystem<T> result = this.calendars.get(variant);

        if (result == null) {
            return super.getCalendarSystem(variant);
        } else {
            return result;
        }

    }

    /**
     * <p>Obtains an object which is useful for generic interval support. </p>
     *
     * @param   variant     name of calendar variant
     * @return  serializable timeline which is variant-specific
     * @since   3.36/4.31
     */
    /*[deutsch]
     * <p>Liefert ein Objekt, das f&uuml;r die allgemeine Intervallunterst&uuml;tzung n&uuml;tzlich ist. </p>
     *
     * @param   variant     name of calendar variant
     * @return  serializable timeline which is variant-specific
     * @since   3.36/4.31
     */
    public TimeLine<T> getTimeLine(String variant) {

        return new CalendarTimeLine<T>(this, variant);

    }

    /**
     * <p>Obtains an object which is useful for generic interval support. </p>
     *
     * @param   variant     calendar variant
     * @return  serializable timeline which is variant-specific
     * @since   5.0
     */
    /*[deutsch]
     * <p>Liefert ein Objekt, das f&uuml;r die allgemeine Intervallunterst&uuml;tzung n&uuml;tzlich ist. </p>
     *
     * @param   variant     calendar variant
     * @return  serializable timeline which is variant-specific
     * @since   5.0
     */
    public TimeLine<T> getTimeLine(VariantSource variant) {

        return this.getTimeLine(variant.getVariant());

    }

    @Override
    public boolean isSupported(ChronoElement<?> element) {

        return super.isSupported(element) || (element instanceof EpochDays);

    }

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Creates a builder for a new calendar family
     * and will only be used during loading a class of a calendar variant
     * in a <i>static initializer</i>. </p>
     *
     * <p>Instances of this class will be created by the static factory method {@code setUp()}. </p>
     *
     * @param       <T> generic type of time context
     * @author      Meno Hochschild
     * @see         #setUp(Class,ChronoMerger,Map)
     * @since       3.4/4.3
     */
    /*[deutsch]
     * <p>Erzeugt einen Builder f&uuml;r eine neue Kalenderfamilie und wird ausschlie&szlig;lich beim Laden einer
     * Klasse zu einer Kalendervariante in einem <i>static initializer</i> benutzt. </p>
     *
     * <p>Instanzen dieser Klasse werden &uuml;ber die statische {@code setUp()}-Fabrikmethode erzeugt. </p>
     *
     * @param       <T> generic type of time context
     * @author      Meno Hochschild
     * @see         #setUp(Class,ChronoMerger,Map)
     * @since       3.4/4.3
     */
    public static final class Builder<T extends CalendarVariant<T>>
        extends Chronology.Builder<T> {

        //~ Instanzvariablen ----------------------------------------------

        private final Map<String, ? extends CalendarSystem<T>> calendars;

        //~ Konstruktoren -------------------------------------------------

        private Builder(
            Class<T> chronoType,
            ChronoMerger<T> merger,
            Map<String, ? extends CalendarSystem<T>> calendars
        ) {
            super(chronoType, merger);

            if (calendars.isEmpty()) {
                throw new IllegalArgumentException("Missing calendar variants.");
            }

            this.calendars = calendars;

        }

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Creates a builder for building a calendar family. </p>
         *
         * @param   <T> generic type of time context
         * @param   chronoType      reified chronological type
         * @param   merger          generic replacement for static creation of variant objects
         * @param   calendars       map of variant names to calendar systems
         * @return  new {@code Builder} object
         * @throws  IllegalArgumentException if no calendar system is specified
         * @since   3.4/4.3
         */
        /*[deutsch]
         * <p>Erzeugt ein Hilfsobjekt zum Bauen einer Kalenderfamilie. </p>
         *
         * @param   <T> generic type of time context
         * @param   chronoType      reified chronological type
         * @param   merger          generic replacement for static creation of variant objects
         * @param   calendars       map of variant names to calendar systems
         * @return  new {@code Builder} object
         * @throws  IllegalArgumentException if no calendar system is specified
         * @since   3.4/4.3
         */
        public static <T extends CalendarVariant<T>> Builder<T> setUp(
            Class<T> chronoType,
            ChronoMerger<T> merger,
            Map<String, ? extends CalendarSystem<T>> calendars
        ) {

            return new Builder<T>(chronoType, merger, calendars);

        }

        @Override
        public <V> Builder<T> appendElement(
            ChronoElement<V> element,
            ElementRule<T, V> rule
        ) {

            super.appendElement(element, rule);
            return this;

        }

        @Override
        public Builder<T> appendExtension(ChronoExtension extension) {

            super.appendExtension(extension);
            return this;

        }

        /**
         * <p>Creates and registers a calendar family. </p>
         *
         * @return  new chronology as calendar family
         * @throws  IllegalStateException if already registered or in case of inconsistencies
         * @since   3.4/4.3
         */
        /*[deutsch]
         * <p>Erzeugt und registriert eine Kalenderfamilie. </p>
         *
         * @return  new chronology as calendar family
         * @throws  IllegalStateException if already registered or in case of inconsistencies
         * @since   3.4/4.3
         */
        @Override
        public CalendarFamily<T> build() {

            CalendarFamily<T> engine =
                new CalendarFamily<T>(
                    this.chronoType,
                    this.merger,
                    this.ruleMap,
                    this.extensions,
                    this.calendars
                );

            Chronology.register(engine);
            return engine;

        }

    }

    private static class CalendarTimeLine<D extends CalendarVariant<D>>
        implements TimeLine<D>, Serializable {

        //~ Instanzvariablen ----------------------------------------------

        private transient final CalendarSystem<D> calsys;

        // only for deserialization
        private final Class<D> chronoType;
        private final String variant;

        //~ Konstruktoren -------------------------------------------------

        private CalendarTimeLine(
            Chronology<D> chronology,
            String variant
        ) {
            super();

            this.calsys = chronology.getCalendarSystem(variant);
            this.chronoType = chronology.getChronoType();
            this.variant = variant;
        }

        //~ Methoden ------------------------------------------------------

        @Override
        public D stepForward(D timepoint) {
            if (timepoint.getDaysSinceEpochUTC() == this.calsys.getMaximumSinceUTC()) {
                return null;
            }
            return timepoint.plus(CalendarDays.ONE);
        }

        @Override
        public D stepBackwards(D timepoint) {
            if (timepoint.getDaysSinceEpochUTC() == this.calsys.getMinimumSinceUTC()) {
                return null;
            }
            return timepoint.minus(CalendarDays.ONE);
        }

        @Override
        public boolean isCalendrical() {
            return true;
        }

        @Override
        public int compare(D o1, D o2) {
            // pure time comparison, we don't take into account the variant in context of timeline
            long t1 = o1.getDaysSinceEpochUTC();
            long t2 = o2.getDaysSinceEpochUTC();

            if (t1 < t2) {
                return - 1;
            } else if (t1 > t2) {
                return 1;
            } else {
                return 0;
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (obj instanceof CalendarTimeLine) {
                CalendarTimeLine<?> that = (CalendarTimeLine<?>) obj;
                return this.chronoType == that.chronoType && this.variant.equals(that.variant);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return this.chronoType.hashCode() + 31 * this.variant.hashCode();
        }

        // reconstruct the calendar system in deserialization
        private Object readResolve() throws ObjectStreamException {
            Chronology<D> chronology = Chronology.lookup(this.chronoType);
            return new CalendarTimeLine<D>(chronology, this.variant);
        }

    }

}
