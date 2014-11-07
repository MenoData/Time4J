/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AdvancedElement.java) is part of project Time4J.
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


/**
 * <p>Default implementation of a chronological element with some
 * element-related manipulation methods. </p>
 *
 * @param   <V> generic type of element values
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Standardimplementierung eines chronologischen Elements mit verschiedenen
 * element-bezogenen Manipulationsmethoden. </p>
 *
 * @param   <V> generic type of element values
 * @author  Meno Hochschild
 */
public abstract class AdvancedElement<V extends Comparable<V>>
    extends BasicElement<V> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int NEW_VALUE_MODE = 0;
    private static final int MIN_MODE = 1;
    private static final int MAX_MODE = 2;
    private static final int FLOOR_MODE = 3;
    private static final int CEILING_MODE = 4;
    private static final int LENIENT_MODE = 5;
    private static final int DECREMENTING_MODE = 6;
    private static final int INCREMENTING_MODE = 7;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Used by subclasses which usually assign an instance to a
     * static constant (creating a singleton). </p>
     *
     * @param   name            name of element
     * @throws  IllegalArgumentException if the name is empty or if it only
     *          contains <i>white space</i> (spaces, tabs etc.)
     * @see     ChronoElement#name()
     */
    /*[deutsch]
     * <p>Konstruktor f&uuml;r Subklassen, die eine so erzeugte Instanz
     * in der Regel statischen Konstanten zuweisen und damit Singletons
     * erzeugen. </p>
     *
     * @param   name            name of element
     * @throws  IllegalArgumentException if the name is empty or if it only
     *          contains <i>white space</i> (spaces, tabs etc.)
     * @see     ChronoElement#name()
     */
    protected AdvancedElement(String name) {
        super(name);

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Compares the values of this element based on their natural order. </p>
     *
     * @throws  ChronoException if this element is not registered in any entity
     *          and/or if no element rule exists to extract the element value
     */
    /*[deutsch]
     * <p>Vergleicht die Werte dieses Elements auf Basis ihrer
     * nat&uuml;rlichen Ordnung. </p>
     *
     * @throws  ChronoException if this element is not registered in any entity
     *          and/or if no element rule exists to extract the element value
     */
    @Override
    public int compare(
        ChronoDisplay o1,
        ChronoDisplay o2
    ) {

        return o1.get(this).compareTo(o2.get(this));

    }

    /**
     * <p>Yields a new operator which can set any entity to its minimum
     * element value. </p>
     *
     * @param   <T> generic type of target entity
     * @param   context     context type
     * @return  operator
     */
    /*[deutsch]
     * <p>Liefert einen Operator, der eine beliebige Entit&auml;t auf
     * das Elementminimum setzt. </p>
     *
     * @param   <T> generic type of target entity
     * @param   context     context type
     * @return  operator
     */
    public <T extends ChronoEntity<T>> ChronoOperator<T> minimized(
        Class<T> context
    ) {

        return new StdOperator<T>(MIN_MODE, this);

    }

    /**
     * <p>Yields a new operator which can set any entity to its maximum
     * element value. </p>
     *
     * @param   <T> generic type of target entity
     * @param   context     context type
     * @return  operator
     */
    /*[deutsch]
     * <p>Liefert einen Operator, der eine beliebige Entit&auml;t auf
     * das Elementmaximum setzt. </p>
     *
     * @param   <T> generic type of target entity
     * @param   context     context type
     * @return  operator
     */
    public <T extends ChronoEntity<T>> ChronoOperator<T> maximized(
        Class<T> context
    ) {

        return new StdOperator<T>(MAX_MODE, this);

    }

    /**
     * <p>Yields a new operator which can set any entity such that its
     * actual element value gets the decremented value. </p>
     *
     * @param   <T> generic type of target entity
     * @param   context     context type
     * @return  decrementing operator
     * @see     TimeAxis#getBaseUnit(ChronoElement)
     */
    /*[deutsch]
     * <p>Liefert einen Operator, der eine beliebige Entit&auml;t so
     * anpasst, da&szlig; dieses Element den vorherigen Wert bekommt. </p>
     *
     * @param   <T> generic type of target entity
     * @param   context     context type
     * @return  decrementing operator
     * @see     TimeAxis#getBaseUnit(ChronoElement)
     */
    public <T extends ChronoEntity<T>> ChronoOperator<T> decremented(
        Class<T> context
    ) {

        return new StdOperator<T>(DECREMENTING_MODE, this);

    }

    /**
     * <p>Yields a new operator which can set any entity such that its
     * actual element value gets the incremented value. </p>
     *
     * @param   <T> generic type of target entity
     * @param   context     context type
     * @return  incrementing operator
     * @see     TimeAxis#getBaseUnit(ChronoElement)
     */
    /*[deutsch]
     * <p>Liefert einen Operator, der eine beliebige Entit&auml;t so
     * anpasst, da&szlig; dieses Element den n&auml;chsten Wert bekommt. </p>
     *
     * @param   <T> generic type of target entity
     * @param   context     context type
     * @return  incrementing operator
     * @see     TimeAxis#getBaseUnit(ChronoElement)
     */
    public <T extends ChronoEntity<T>> ChronoOperator<T> incremented(
        Class<T> context
    ) {

        return new StdOperator<T>(INCREMENTING_MODE, this);

    }

    /**
     * <p>Yields an operator which rounds any entity down so that the child
     * elements will be set to the minimum. </p>
     *
     * @param   <T> generic type of target entity
     * @param   context     context type
     * @return  operator
     */
    /*[deutsch]
     * <p>Liefert einen Operator, der eine Entit&auml;t abrundet, indem alle
     * Kindselemente dieses Elements auf ihr Minimum gesetzt werden. </p>
     *
     * @param   <T> generic type of target entity
     * @param   context     context type
     * @return  operator
     */
    public <T extends ChronoEntity<T>> ChronoOperator<T> atFloor(
        Class<T> context
    ) {

        return new StdOperator<T>(FLOOR_MODE, this);

    }

    /**
     * <p>Yields an operator which rounds any entity up so that the child
     * elements will be set to the maximum. </p>
     *
     * @param   <T> generic type of target entity
     * @param   context     context type
     * @return  operator
     */
    /*[deutsch]
     * <p>Liefert einen Operator, der eine Entit&auml;t aufrundet, indem alle
     * Kindselemente dieses Elements auf ihr Maximum gesetzt werden. </p>
     *
     * @param   <T> generic type of target entity
     * @param   context     context type
     * @return  operator
     */
    public <T extends ChronoEntity<T>> ChronoOperator<T> atCeiling(
        Class<T> context
    ) {

        return new StdOperator<T>(CEILING_MODE, this);

    }

    /**
     * <p>Yields an operator which sets any entity such that its actual
     * element value will be set in lenient mode to given value. </p>
     *
     * @param   <T> generic type of target entity
     * @param   value       new element value
     * @param   context     context type
     * @return  operator
     */
    /*[deutsch]
     * <p>Liefert einen Operator, der eine beliebige Entit&auml;t so
     * anpasst, da&szlig; dieses Element auf den angegebenen Wert im
     * Nachsichtigkeitsmodus gesetzt wird. </p>
     *
     * @param   <T> generic type of target entity
     * @param   value       new element value
     * @param   context     context type
     * @return  operator
     */
    public <T extends ChronoEntity<T>> ChronoOperator<T> setLenient(
        V value,
        Class<T> context
    ) {

        return new StdOperator<T>(LENIENT_MODE, this, value);

    }

    /**
     * <p>Yields an operator which sets any entity such that its actual
     * element value will be set in normal mode to given value. </p>
     *
     * @param   <T> generic type of target entity
     * @param   value       new element value
     * @param   context     context type
     * @return  operator
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liefert einen Operator, der eine beliebige Entit&auml;t so
     * anpasst, da&szlig; dieses Element auf den angegebenen Wert im
     * Standardmodus gesetzt wird. </p>
     *
     * @param   <T> generic type of target entity
     * @param   value       new element value
     * @param   context     context type
     * @return  operator
     * @since   2.0
     */
    public <T extends ChronoEntity<T>> ChronoOperator<T> newValue(
        V value,
        Class<T> context
    ) {

        return new StdOperator<T>(NEW_VALUE_MODE, this, value);

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class StdOperator<T extends ChronoEntity<T>>
        implements ChronoOperator<T> {

        //~ Instanzvariablen ----------------------------------------------

        private final int mode;
        private final ChronoElement<?> element;
        private final Object value;

        //~ Konstruktoren -------------------------------------------------

        StdOperator(
            int mode,
            ChronoElement<?> element
        ) {
            this(mode, element, null);

        }

        StdOperator(
            int mode,
            ChronoElement<?> element,
            Object value // optional
        ) {
            super();

            this.mode = mode;
            this.element = element;
            this.value = value;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public T apply(T entity) {

            switch (this.mode) {
                case NEW_VALUE_MODE:
                    return value(entity, this.element, this.value, false);
                case MIN_MODE:
                    return min(entity, this.element);
                case MAX_MODE:
                    return max(entity, this.element);
                case FLOOR_MODE:
                    return floor(entity, this.element);
                case CEILING_MODE:
                    return ceiling(entity, this.element);
                case LENIENT_MODE:
                    return value(entity, this.element, this.value, true);
                case DECREMENTING_MODE:
                    return this.move(entity, false);
                case INCREMENTING_MODE:
                    return this.move(entity, true);
                default:
                    throw new UnsupportedOperationException(
                        "Unknown mode: " + this.mode);
            }

        }

        private static <T extends ChronoEntity<T>, V> T min(
            ChronoEntity<T> entity,
            ChronoElement<V> element
        ) {

            return entity.with(element, entity.getMinimum(element));

        }

        private static <T extends ChronoEntity<T>, V> T max(
            ChronoEntity<T> entity,
            ChronoElement<V> element
        ) {

            return entity.with(element, entity.getMaximum(element));

        }

        private static <T extends ChronoEntity<T>, V> T floor(
            ChronoEntity<T> entity,
            ChronoElement<V> element
        ) {

            T ctx = entity.getContext();
            ChronoElement<?> e = element;

            while ((e = getRule(ctx, e).getChildAtFloor(ctx)) != null) {
                ctx = withFloor(ctx, e);
            }

            return ctx;

        }

        private static <T extends ChronoEntity<T>, V> T ceiling(
            ChronoEntity<T> entity,
            ChronoElement<V> element
        ) {

            T ctx = entity.getContext();
            ChronoElement<?> e = element;

            while ((e = getRule(ctx, e).getChildAtCeiling(ctx)) != null) {
                ctx = withCeiling(ctx, e);
            }

            return ctx;

        }

        private static <T extends ChronoEntity<T>, V> T value(
            ChronoEntity<T> entity,
            ChronoElement<V> element,
            Object value,
            boolean lenient
        ) {

            T ctx = entity.getContext();

            return getRule(ctx, element).withValue(
                ctx,
                element.getType().cast(value),
                lenient
            );

        }

        private static <T extends ChronoEntity<T>, V> ElementRule<T, V> getRule(
            T context,
            ChronoElement<V> element
        ) {

            return context.getChronology().getRule(element);

        }

        private static <T extends ChronoEntity<T>, V> T withFloor(
            T context,
            ChronoElement<V> element
        ) {

            ElementRule<T, V> rule = getRule(context, element);

            return rule.withValue(
                context,
                rule.getMinimum(context),
                element.isLenient()
            );

        }

        private static <T extends ChronoEntity<T>, V> T withCeiling(
            T context,
            ChronoElement<V> element
        ) {

            ElementRule<T, V> rule = getRule(context, element);

            return rule.withValue(
                context,
                rule.getMaximum(context),
                element.isLenient()
            );

        }

        private T move(
            T entity,
            boolean forward
        ) {

            T result = null;

            if (entity instanceof TimePoint) {
                TimePoint<?, ?> tp = TimePoint.class.cast(entity);
                Object answer = add(tp, this.element, forward);
                result = entity.getChronology().getChronoType().cast(answer);
            } else {
                throw new ChronoException(
                    "Base units not supported by: "
                    + entity.getChronology().getChronoType());
            }

            return result;

        }

        // wildcard capture
        private static <U, T extends TimePoint<U, T>> T add(
            TimePoint<U, T> context,
            ChronoElement<?> element,
            boolean forward
        ) {

            U unit = context.getChronology().getBaseUnit(element);

            if (forward) {
                return context.plus(1, unit);
            } else {
                return context.minus(1, unit);
            }

        }

    }

}
