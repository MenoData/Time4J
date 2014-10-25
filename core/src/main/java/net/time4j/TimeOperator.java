/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimeOperator.java) is part of project Time4J.
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

package net.time4j;

import net.time4j.engine.AdvancedElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoOperator;


/**
 * <p>Definiert eine Manipulation von Uhrzeitobjekten nach
 * dem Strategy-Entwurfsmuster. </p>
 *
 * @author      Meno Hochschild
 * @concurrency <immutable>
 */
final class TimeOperator
    extends ElementOperator<PlainTime> {

    //~ Instanzvariablen --------------------------------------------------

    private final ChronoOperator<PlainTime> opCache;
    private final ChronoOperator<PlainTimestamp> tsCache;
    private final ChronoOperator<Moment> moCache;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruiert eine neue Instanz. </p>
     *
     * @param   element         element an operator will be applied on
     * @param   type            type of operator
     */
    TimeOperator(
        AdvancedElement<?> element,
        int type
    ) {
        this(element, type, null);

    }

    /**
     * <p>Konstruiert eine neue Instanz. </p>
     *
     * @param   element         element an operator will be applied on
     * @param   type            operator type
     * @param   value           lenient value of element
     */
    TimeOperator(
        final AdvancedElement<?> element,
        final int type,
        final Object value // optional
    ) {
        super(element, type);

        switch (type) {
            case OP_NEW_VALUE:
                this.opCache = newValue(element, value);
                this.tsCache = newValueTS(element, value);
                break;
            case OP_MINIMIZE:
                this.opCache = element.minimized(PlainTime.class);
                this.tsCache = element.minimized(PlainTimestamp.class);
                break;
            case OP_MAXIMIZE:
                this.opCache = element.maximized(PlainTime.class);
                this.tsCache = element.maximized(PlainTimestamp.class);
                break;
            case OP_DECREMENT:
                this.opCache = element.decremented(PlainTime.class);
                this.tsCache = element.decremented(PlainTimestamp.class);
                break;
            case OP_INCREMENT:
                this.opCache = element.incremented(PlainTime.class);
                this.tsCache = element.incremented(PlainTimestamp.class);
                break;
            case OP_FLOOR:
                this.opCache = child(element, false, PlainTime.class);
                this.tsCache = child(element, false, PlainTimestamp.class);
                break;
            case OP_CEILING:
                this.opCache = child(element, true, PlainTime.class);
                this.tsCache = child(element, true, PlainTimestamp.class);
                break;
            case OP_LENIENT:
                this.opCache = lenient(element, value);
                this.tsCache = lenientTS(element, value);
                break;
            default:
                throw new AssertionError("Unknown: " + this.getType());
        }

        this.moCache = new Moment.Operator(this.tsCache, element, type);

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public PlainTime apply(PlainTime entity) {

        return this.opCache.apply(entity);

    }

    @Override
    public ChronoOperator<Moment> inStdTimezone() {

        return this.moCache;

    }

    @Override
    ChronoOperator<PlainTimestamp> onTimestamp() {

        return this.tsCache;

    }

    private static <V extends Comparable<V>, T extends ChronoEntity<T>>
    ChronoOperator<T> child(
        AdvancedElement<V> element,
        boolean up,
        Class<T> context
    ) {

        String compare = element.name();

        if (
            compare.equals("MILLI_OF_SECOND")
            || compare.equals("MILLI_OF_DAY")
        ) {
            return new FractionOperator<T>('3', up);
        } else if (
            compare.equals("MICRO_OF_SECOND")
            || compare.equals("MICRO_OF_DAY")
        ) {
            return new FractionOperator<T>('6', up);
        } else if (
            compare.equals("NANO_OF_SECOND")
            || compare.equals("NANO_OF_DAY")
        ) {
            return new FractionOperator<T>('9', up);
        }

        if (up) {
            return element.atCeiling(context);
        } else {
            return element.atFloor(context);
        }

    }

    private static <V extends Comparable<V>>
    ChronoOperator<PlainTime> newValue(
        AdvancedElement<V> element,
        Object value
    ) {

        return element.newValue(
            element.getType().cast(value),
            PlainTime.class);

    }

    private static <V extends Comparable<V>>
    ChronoOperator<PlainTimestamp> newValueTS(
        AdvancedElement<V> element,
        Object value
    ) {

        return new ValueOperator(
            element.newValue(
                element.getType().cast(value),
                PlainTimestamp.class),
            value
        );

    }

    private static <V extends Comparable<V>>
    ChronoOperator<PlainTime> lenient(
        AdvancedElement<V> element,
        Object value
    ) {

        return element.setLenient(
            element.getType().cast(value),
            PlainTime.class);

    }

    private static <V extends Comparable<V>>
    ChronoOperator<PlainTimestamp> lenientTS(
        AdvancedElement<V> element,
        Object value
    ) {

        return new ValueOperator(
            element.setLenient(
                element.getType().cast(value),
                PlainTimestamp.class),
            Number.class.cast(value)
        );

    }

}
