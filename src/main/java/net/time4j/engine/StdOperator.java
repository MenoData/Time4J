/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (StdOperator.java) is part of project Time4J.
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


/**
 * <p>Passt eine beliebige chronologische Entit&auml;t an. </p>
 *
 * <p>Die g&auml;ngigsten Beispiele w&auml;ren etwa die Suche nach dem
 * zweiten Sonntag im April oder dem letzten Tag eines Monats. </p>
 *
 * @author  Meno Hochschild
 */
final class StdOperator<T extends ChronoEntity<T>>
    implements ChronoOperator<T> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int MIN_MODE = 1;
    private static final int MAX_MODE = 2;
    private static final int FLOOR_MODE = 3;
    private static final int CEILING_MODE = 4;
    private static final int LENIENT_MODE = 5;
    private static final int DECREMENTING_MODE = 6;
    private static final int INCREMENTING_MODE = 7;

    //~ Instanzvariablen --------------------------------------------------

    private final int mode;
    private final ChronoElement<?> element;
    private final Object value;

    //~ Konstruktoren -----------------------------------------------------

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

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Passt die angegebene Entit&auml;t an. </p>
     *
     * @param   entity      chronological entity to be adjusted
     * @return  adjusted copy of argument which itself remains unaffected
     * @throws  ChronoException if there is no element rule for adjusting
     * @throws  IllegalArgumentException if an invalid value is tried
     * @throws  ArithmeticException in case of numerical overflow
     */
    @Override
    public T apply(T entity) {

        switch (this.mode) {
            case MIN_MODE:
                return minimize(entity, this.element);
            case MAX_MODE:
                return maximize(entity, this.element);
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

    /**
     * <p>Erzeugt einen neuen Versteller, der minimiert. </p>
     *
     * @param   element     reference element
     * @return  new operator
     */
    static <T extends ChronoEntity<T>>
    ChronoOperator<T> createMinimizer(ChronoElement<?> element) {

        return new StdOperator<T>(MIN_MODE, element);

    }

    /**
     * <p>Erzeugt einen neuen Versteller, der maximiert. </p>
     *
     * @param   element     reference element
     * @return  new operator
     */
    static <T extends ChronoEntity<T>>
    ChronoOperator<T> createMaximizer(ChronoElement<?> element) {

        return new StdOperator<T>(MAX_MODE, element);

    }

    /**
     * <p>Erzeugt einen neuen Versteller, der eine untere Grenze setzt. </p>
     *
     * @param   element     reference element
     * @return  new operator
     */
    static <T extends ChronoEntity<T>>
    ChronoOperator<T> createFloor(ChronoElement<?> element) {

        return new StdOperator<T>(FLOOR_MODE, element);

    }

    /**
     * <p>Erzeugt einen neuen Versteller, der eine obere Grenze setzt. </p>
     *
     * @param   element     reference element
     * @return  new operator
     */
    static <T extends ChronoEntity<T>>
    ChronoOperator<T> createCeiling(ChronoElement<?> element) {

        return new StdOperator<T>(CEILING_MODE, element);

    }

    /**
     * <p>Erzeugt einen neuen Versteller, der eine Basiseinheit
     * subtrahiert. </p>
     *
     * @param   element     reference element
     * @return  new operator
     */
    static <T extends ChronoEntity<T>>
    ChronoOperator<T> createPrevious(ChronoElement<?> element) {

        return new StdOperator<T>(DECREMENTING_MODE, element);

    }

    /**
     * <p>Erzeugt einen neuen Versteller, der eine Basiseinheit addiert. </p>
     *
     * @param   element     reference element
     * @return  new operator
     */
    static <T extends ChronoEntity<T>>
    ChronoOperator<T> createNext(ChronoElement<?> element) {

        return new StdOperator<T>(INCREMENTING_MODE, element);

    }

    /**
     * <p>Erzeugt einen neuen Versteller im Nachsichtigkeitsmodus. </p>
     *
     * @param   element     reference element
     * @param   value       value to be set in lenient mode
     * @return  new operator
     */
    static <T extends ChronoEntity<T>, V>
    ChronoOperator<T> createLenientSetter(
        ChronoElement<V> element,
        V value
    ) {

        return new StdOperator<T>(LENIENT_MODE, element, value);

    }

    private static <T extends ChronoEntity<T>, V> T minimize(
        ChronoEntity<T> entity,
        ChronoElement<V> element
    ) {

        return entity.with(element, entity.getMinimum(element));

    }

    private static <T extends ChronoEntity<T>, V> T maximize(
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
        }

        if (result == null) {
            throw new ChronoException(
                "No base unit defined for: " + this.element.name());
        } else {
            return result;
        }

    }

    // wildcard capture
    private static <U, T extends TimePoint<U, T>> T add(
        TimePoint<U, T> context,
        ChronoElement<?> element,
        boolean forward
    ) {

        U unit = context.getChronology().getBaseUnit(element);

        if (unit == null) {
            return null;
        } else if (forward) {
            return context.plus(1, unit);
        } else {
            return context.minus(1, unit);
        }

    }

}
