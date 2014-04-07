/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (OperatorDelegate.java) is part of project Time4J.
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

import net.time4j.engine.AdvancedElement;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoOperator;
import net.time4j.tz.TZID;
import net.time4j.tz.TransitionStrategy;


/**
 * <p>Delegationsoperator. </p>
 *
 * @param       <V> generic type of element values
 * @param       <T> generic target type for a {@code ChronoOperator}
 * @author      Meno Hochschild
 * @concurrency <immutable>
 */
final class OperatorDelegate<V extends Comparable<V>, T extends ChronoEntity<T>>
    implements ZonalOperator<T> {

    //~ Instanzvariablen --------------------------------------------------

    private final ChronoElement<V> element;
    private final OperatorType type;
    private final ChronoOperator<T> opCache;
    private final ChronoOperator<PlainTimestamp> tsCache;
    private final ChronoOperator<Moment> moCache;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruiert eine neue Instanz. </p>
     *
     * @param   element         element an operator will be applied on
     * @param   type            type of operator
     */
    OperatorDelegate(
        AdvancedElement<V> element,
        OperatorType type
    ) {
        this(element, type, null);

    }

    /**
     * <p>Konstruiert eine neue Instanz. </p>
     *
     * @param   element         element an operator will be applied on
     * @param   type            operator type
     * @param   value           value of element
     */
    OperatorDelegate(
        final AdvancedElement<V> element,
        final OperatorType type,
        final V value // optional
    ) {
        super();

        this.element = element;
        this.type = type;

        switch (type) {
            case MINIMIZE:
                this.opCache = element.min();
                this.tsCache = element.min();
                break;
            case MAXIMIZE:
                this.opCache = element.max();
                this.tsCache = element.max();
                break;
            case DECREMENT:
                this.opCache = element.previous();
                this.tsCache = element.previous();
                break;
            case INCREMENT:
                this.opCache = element.next();
                this.tsCache = element.next();
                break;
            case FLOOR:
                this.opCache = createChildOperator(element, false);
                this.tsCache = createChildOperator(element, false);
                break;
            case CEILING:
                this.opCache = createChildOperator(element, true);
                this.tsCache = createChildOperator(element, true);
                break;
            case LENIENT:
                this.opCache = element.lenient(value);
                this.tsCache = element.lenient(value);
                break;
            default:
                throw new UnsupportedOperationException(type.name());
        }

        this.moCache = new Moment.Operator(this.tsCache, element, type);

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public T apply(T entity) {

        return this.opCache.apply(entity);

    }

    @Override
    public ChronoOperator<Moment> inStdTimezone() {

        return this.moCache;

    }

    @Override
    public ChronoOperator<Moment> inTimezone(
        TZID tzid,
        TransitionStrategy strategy
    ) {

        return new Moment.Operator(
            this.tsCache,
            tzid,
            strategy,
            this.element,
            this.type
        );

    }

    @Override
    public ChronoOperator<PlainTimestamp> onTimestamp() {

        return this.tsCache;

    }

    private static <T extends ChronoEntity<T>, V extends Comparable<V>>
    ChronoOperator<T> createChildOperator(
        AdvancedElement<V> element,
        boolean up
    ) {

        Object compare = element; // stellt JDK-6 zufrieden

        if (
            (compare == PlainTime.MILLI_OF_SECOND)
            || (compare == PlainTime.MILLI_OF_DAY)
        ) {
            return new FractionOperator<T>('3', up);
        } else if (
            (compare == PlainTime.MICRO_OF_SECOND)
            || (compare == PlainTime.MICRO_OF_DAY)
        ) {
            return new FractionOperator<T>('6', up);
        } else if (
            (compare == PlainTime.NANO_OF_SECOND)
            || (compare == PlainTime.NANO_OF_DAY)
        ) {
            return new FractionOperator<T>('9', up);
        }

        if (up) {
            return element.ceiling();
        } else {
            return element.floor();
        }

    }

}
