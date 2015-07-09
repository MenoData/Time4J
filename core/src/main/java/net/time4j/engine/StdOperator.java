/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (StdOperator.java) is part of project Time4J.
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
 * <p>Factory for producing standard chronological operators which are applicable
 * on most chronological entities. </p>
 *
 * @param   <T> generic target type of operator
 * @author  Meno Hochschild
 * @since   3.5/4.3
 */
/*[deutsch]
 * <p>Standard-Operator, der auf die meisten chronologischen Entit&auml;ten anwendbar ist. </p>
 *
 * @param   <T> generic target type of operator
 * @author  Meno Hochschild
 * @since   3.5/4.3
 */
public final class StdOperator<T extends ChronoEntity<T>>
    implements ChronoOperator<T> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int NEW_VALUE_MODE = 0;
    private static final int MIN_MODE = 1;
    private static final int MAX_MODE = 2;
    private static final int FLOOR_MODE = 3;
    private static final int CEILING_MODE = 4;
    private static final int LENIENT_MODE = 5;
    private static final int DECREMENTING_MODE = 6;
    private static final int INCREMENTING_MODE = 7;

    //~ Instanzvariablen ----------------------------------------------

    private final int mode;
    private final ChronoElement<?> element;
    private final Object value;

    //~ Konstruktoren -------------------------------------------------

    private StdOperator(
        int mode,
        ChronoElement<?> element
    ) {
        this(mode, element, null);

    }

    private StdOperator(
        int mode,
        ChronoElement<?> element,
        Object value // optional
    ) {
        super();

        if (element == null) {
            throw new NullPointerException("Missing chronological element.");
        }

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
                throw new UnsupportedOperationException("Unknown mode: " + this.mode);
        }

    }

    /**
     * <p>Yields a new operator which can set any entity to its minimum
     * element value. </p>
     *
     * @param   <T> generic type of target entity
     * @param   element     associated chronological element
     * @return  operator
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Liefert einen Operator, der eine beliebige Entit&auml;t auf
     * das Elementminimum setzt. </p>
     *
     * @param   <T> generic type of target entity
     * @param   element     associated chronological element
     * @return  operator
     * @since   3.5/4.3
     */
    public static <T extends ChronoEntity<T>> ChronoOperator<T> minimized(ChronoElement<?> element) {

        return new StdOperator<T>(MIN_MODE, element);

    }

    /**
     * <p>Yields a new operator which can set any entity to its maximum
     * element value. </p>
     *
     * @param   <T> generic type of target entity
     * @param   element     associated chronological element
     * @return  operator
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Liefert einen Operator, der eine beliebige Entit&auml;t auf
     * das Elementmaximum setzt. </p>
     *
     * @param   <T> generic type of target entity
     * @param   element     associated chronological element
     * @return  operator
     * @since   3.5/4.3
     */
    public static <T extends ChronoEntity<T>> ChronoOperator<T> maximized(ChronoElement<?> element) {

        return new StdOperator<T>(MAX_MODE, element);

    }

    /**
     * <p>Yields a new operator which can set any entity such that its
     * actual element value gets the decremented value. </p>
     *
     * @param   <T> generic type of target entity
     * @param   element     associated chronological element
     * @return  operator
     * @see     TimeAxis#getBaseUnit(ChronoElement)
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Liefert einen Operator, der eine beliebige Entit&auml;t so
     * anpasst, da&szlig; dieses Element den vorherigen Wert bekommt. </p>
     *
     * @param   <T> generic type of target entity
     * @param   element     associated chronological element
     * @return  operator
     * @see     TimeAxis#getBaseUnit(ChronoElement)
     * @since   3.5/4.3
     */
    public static <T extends ChronoEntity<T>> ChronoOperator<T> decremented(ChronoElement<?> element) {

        return new StdOperator<T>(DECREMENTING_MODE, element);

    }

    /**
     * <p>Yields a new operator which can set any entity such that its
     * actual element value gets the incremented value. </p>
     *
     * @param   <T> generic type of target entity
     * @param   element     associated chronological element
     * @return  operator
     * @see     TimeAxis#getBaseUnit(ChronoElement)
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Liefert einen Operator, der eine beliebige Entit&auml;t so
     * anpasst, da&szlig; dieses Element den n&auml;chsten Wert bekommt. </p>
     *
     * @param   <T> generic type of target entity
     * @param   element     associated chronological element
     * @return  operator
     * @see     TimeAxis#getBaseUnit(ChronoElement)
     * @since   3.5/4.3
     */
    public static <T extends ChronoEntity<T>> ChronoOperator<T> incremented(ChronoElement<?> element) {

        return new StdOperator<T>(INCREMENTING_MODE, element);

    }

    /**
     * <p>Yields an operator which rounds any entity down so that the child
     * elements will be set to the minimum. </p>
     *
     * @param   <T> generic type of target entity
     * @param   element     associated chronological element
     * @return  operator
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Liefert einen Operator, der eine Entit&auml;t abrundet, indem alle
     * Kindselemente dieses Elements auf ihr Minimum gesetzt werden. </p>
     *
     * @param   <T> generic type of target entity
     * @param   element     associated chronological element
     * @return  operator
     * @since   3.5/4.3
     */
    public static <T extends ChronoEntity<T>> ChronoOperator<T> atFloor(ChronoElement<?> element) {

        return new StdOperator<T>(FLOOR_MODE, element);

    }

    /**
     * <p>Yields an operator which rounds any entity up so that the child
     * elements will be set to the maximum. </p>
     *
     * @param   <T> generic type of target entity
     * @param   element     associated chronological element
     * @return  operator
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Liefert einen Operator, der eine Entit&auml;t aufrundet, indem alle
     * Kindselemente dieses Elements auf ihr Maximum gesetzt werden. </p>
     *
     * @param   <T> generic type of target entity
     * @param   element     associated chronological element
     * @return  operator
     * @since   3.5/4.3
     */
    public static <T extends ChronoEntity<T>> ChronoOperator<T> atCeiling(ChronoElement<?> element) {

        return new StdOperator<T>(CEILING_MODE, element);

    }

    /**
     * <p>Yields an operator which sets any entity such that its actual
     * element value will be set in lenient mode to given value. </p>
     *
     * @param   <T> generic type of target entity
     * @param   <V> generic element value type
     * @param   value       new element value
     * @param   element     associated chronological element
     * @return  operator
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Liefert einen Operator, der eine beliebige Entit&auml;t so
     * anpasst, da&szlig; dieses Element auf den angegebenen Wert im
     * Nachsichtigkeitsmodus gesetzt wird. </p>
     *
     * @param   <T> generic type of target entity
     * @param   <V> generic element value type
     * @param   value       new element value
     * @param   element     associated chronological element
     * @return  operator
     * @since   3.5/4.3
     */
    public static <T extends ChronoEntity<T>, V> ChronoOperator<T> setLenient(
        V value,
        ChronoElement<V> element
    ) {

        return new StdOperator<T>(LENIENT_MODE, element, value);

    }

    /**
     * <p>Yields an operator which sets any entity such that its actual
     * element value will be set in normal mode to given value. </p>
     *
     * @param   <T> generic type of target entity
     * @param   <V> generic element value type
     * @param   value       new element value
     * @param   element     associated chronological element
     * @return  operator
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Liefert einen Operator, der eine beliebige Entit&auml;t so
     * anpasst, da&szlig; dieses Element auf den angegebenen Wert im
     * Standardmodus gesetzt wird. </p>
     *
     * @param   <T> generic type of target entity
     * @param   <V> generic element value type
     * @param   value       new element value
     * @param   element     associated chronological element
     * @return  operator
     * @since   3.5/4.3
     */
    public static <T extends ChronoEntity<T>, V> ChronoOperator<T> newValue(
        V value,
        ChronoElement<V> element
    ) {

        return new StdOperator<T>(NEW_VALUE_MODE, element, value);

    }

    private <V> T min(
        ChronoEntity<T> entity,
        ChronoElement<V> element
    ) {

        return entity.with(element, entity.getMinimum(element));

    }

    private <V> T max(
        ChronoEntity<T> entity,
        ChronoElement<V> element
    ) {

        return entity.with(element, entity.getMaximum(element));

    }

    private <V> T floor(
        ChronoEntity<T> entity,
        ChronoElement<V> element
    ) {

        T ctx = entity.getContext();
        ChronoElement<?> e = element;

        while ((e = ctx.getChronology().getRule(e).getChildAtFloor(ctx)) != null) {
            ctx = withFloor(ctx, e);
        }

        return ctx;

    }

    private <V> T ceiling(
        ChronoEntity<T> entity,
        ChronoElement<V> element
    ) {

        T ctx = entity.getContext();
        ChronoElement<?> e = element;

        while ((e = ctx.getChronology().getRule(e).getChildAtCeiling(ctx)) != null) {
            ctx = withCeiling(ctx, e);
        }

        return ctx;

    }

    private <V> T value(
        ChronoEntity<T> entity,
        ChronoElement<V> element,
        Object value,
        boolean lenient
    ) {

        T ctx = entity.getContext();

        return ctx.getChronology().getRule(element).withValue(
            ctx,
            element.getType().cast(value),
            lenient
        );

    }

    private <V> T withFloor(
        T context,
        ChronoElement<V> element
    ) {

        ElementRule<T, V> rule = context.getChronology().getRule(element);

        return rule.withValue(
            context,
            rule.getMinimum(context),
            element.isLenient()
        );

    }

    private <V> T withCeiling(
        T context,
        ChronoElement<V> element
    ) {

        ElementRule<T, V> rule = context.getChronology().getRule(element);

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

        if (entity instanceof TimePoint) {
            TimePoint<?, ?> tp = TimePoint.class.cast(entity);
            Object answer = add(tp, this.element, forward);
            return entity.getChronology().getChronoType().cast(answer);
        } else {
            throw new ChronoException(
                "Base units not supported by: "
                    + entity.getChronology().getChronoType());
        }

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
