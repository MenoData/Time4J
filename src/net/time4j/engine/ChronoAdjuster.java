/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoAdjuster.java) is part of project Time4J.
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
import java.io.Serializable;


/**
 * <p>Passt eine beliebige chronologische Entit&auml;t an. </p>
 *
 * <p>Die g&auml;ngigsten Beispiele w&auml;ren etwa die Suche nach dem
 * zweiten Sonntag im April oder dem letzten Tag eines Monats. </p>
 *
 * @author  Meno Hochschild
 */
final class ChronoAdjuster<T extends ChronoEntity<T>>
    implements ChronoOperator<T>, Serializable {

    //~ Statische Felder/Initialisierungen ----------------------------

    private static final int MIN_MODE = 1;
    private static final int MAX_MODE = 2;
    private static final int FLOOR_MODE = 3;
    private static final int CEILING_MODE = 4;
    private static final int LENIENT_MODE = 5;
    private static final int STRICT_MODE = 6;

    private static final long serialVersionUID = 1L;

    //~ Instanzvariablen --------------------------------------------------

    private final int mode;
    private final ChronoElement<?> element;
    private final Object value;

    //~ Konstruktoren -----------------------------------------------------

    private ChronoAdjuster(
        int mode,
        ChronoElement<?> element
    ) {
        this(mode, element, null);

    }

    private ChronoAdjuster(
        int mode,
        ChronoElement<?> element,
        @Nullable Object value
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
     * @param   entity  anzupassende chronologische Entit&auml;t
     * @return  angepasste chronologische Entit&auml;t
     * @throws  ChronoException wenn keine Regel zur Anpassung gefunden wird
     * @throws  IllegalArgumentException wenn das Setzen eines falschen Werts
     *          versucht wird
     * @throws  ArithmeticException bei numerischem &Uuml;berlauf
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
            case STRICT_MODE:
                return value(entity, this.element, this.value, false);
            default:
                throw new UnsupportedOperationException(
                    "Unknown mode: " + this.mode);
        }

    }

    /**
     * <p>Erzeugt einen neuen Versteller, der minimiert. </p>
     *
     * @param   element     Bezugselement
     * @return  neue Instanz
     */
    static <T extends ChronoEntity<T>>
    ChronoOperator<T> createMinimizer(ChronoElement<?> element) {

        return new ChronoAdjuster<T>(MIN_MODE, element);

    }

    /**
     * <p>Erzeugt einen neuen Versteller, der maximiert. </p>
     *
     * @param   element     Bezugselement
     * @return  neue Instanz
     */
    static <T extends ChronoEntity<T>>
    ChronoOperator<T> createMaximizer(ChronoElement<?> element) {

        return new ChronoAdjuster<T>(MAX_MODE, element);

    }

    /**
     * <p>Erzeugt einen neuen Versteller, der eine untere Grenze setzt. </p>
     *
     * @param   element     Bezugselement
     * @return  neue Instanz
     */
    static <T extends ChronoEntity<T>>
    ChronoOperator<T> createFloor(ChronoElement<?> element) {

        return new ChronoAdjuster<T>(FLOOR_MODE, element);

    }

    /**
     * <p>Erzeugt einen neuen Versteller, der eine obere Grenze setzt. </p>
     *
     * @param   element     Bezugselement
     * @return  neue Instanz
     */
    static <T extends ChronoEntity<T>>
    ChronoOperator<T> createCeiling(ChronoElement<?> element) {

        return new ChronoAdjuster<T>(CEILING_MODE, element);

    }

    /**
     * <p>Erzeugt einen neuen Versteller im Nachsichtigkeitsmodus. </p>
     *
     * @param   element     Bezugselement
     * @param   value       zu setzender Wert
     * @return  neue Instanz
     */
    static <T extends ChronoEntity<T>, V>
    ChronoOperator<T> createLenientSetter(
        ChronoElement<V> element,
        V value
    ) {

        return new ChronoAdjuster<T>(LENIENT_MODE, element, value);

    }

    /**
     * <p>Erzeugt einen neuen Versteller im strikten Modus. </p>
     *
     * @param   element     Bezugselement
     * @param   value       zu setzender Wert
     * @return  neue Instanz
     */
    static <T extends ChronoEntity<T>, V>
    ChronoOperator<T> createStrictSetter(
        ChronoElement<V> element,
        V value
    ) {

        return new ChronoAdjuster<T>(STRICT_MODE, element, value);

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

}
