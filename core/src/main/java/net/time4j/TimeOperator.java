/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
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

import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoOperator;
import net.time4j.engine.StdOperator;


/**
 * <p>Definiert eine Manipulation von Uhrzeitobjekten nach
 * dem Strategy-Entwurfsmuster. </p>
 *
 * @author      Meno Hochschild
 */
final class TimeOperator
    extends ElementOperator<PlainTime> {

    //~ Instanzvariablen --------------------------------------------------

    private final Object opDelegate;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruiert eine neue Instanz. </p>
     *
     * @param   element         element an operator will be applied on
     * @param   type            type of operator
     */
    TimeOperator(
        ChronoElement<?> element,
        int type
    ) {
        this(element, type, null);

    }

    /**
     * <p>Konstruiert eine neue Instanz. </p>
     *
     * @param   element         element an operator will be applied on
     * @param   type            operator type
     * @param   value           lenient or new value of element
     */
    TimeOperator(
        final ChronoElement<?> element,
        final int type,
        final Object value // optional
    ) {
        super(element, type);

        switch (type) {
            case OP_NEW_VALUE:
                this.opDelegate = newValue(element, value);
                break;
            case OP_MINIMIZE:
                this.opDelegate = StdOperator.minimized(element);
                break;
            case OP_MAXIMIZE:
                this.opDelegate = StdOperator.maximized(element);
                break;
            case OP_DECREMENT:
                this.opDelegate = StdOperator.decremented(element);
                break;
            case OP_INCREMENT:
                this.opDelegate = StdOperator.incremented(element);
                break;
            case OP_FLOOR:
                this.opDelegate = child(element, false);
                break;
            case OP_CEILING:
                this.opDelegate = child(element, true);
                break;
            case OP_LENIENT:
                this.opDelegate = lenient(element, value);
                break;
            default:
                throw new AssertionError("Unknown: " + this.getType());
        }

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public PlainTime apply(PlainTime entity) {

        ChronoOperator<PlainTime> operator = (ChronoOperator<PlainTime>) this.opDelegate;
        return operator.apply(entity);

    }

    @Override
    @SuppressWarnings("unchecked")
    ChronoOperator<PlainTimestamp> onTimestamp() {

        return (ChronoOperator<PlainTimestamp>) this.opDelegate;

    }

    private static <V> Object newValue(
        ChronoElement<V> element,
        Object value
    ) {

        V v = element.getType().cast(value);
        return ValueOperator.of(StdOperator.newValue(v, element), value);

    }

    private static <V> Object lenient(
        ChronoElement<V> element,
        Object value
    ) {

        V v = element.getType().cast(value);
        return ValueOperator.of(StdOperator.setLenient(v, element), value);

    }

    private static <V, T extends ChronoEntity<T>> ChronoOperator<T> child(
        ChronoElement<V> element,
        boolean up
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
            return StdOperator.atCeiling(element);
        } else {
            return StdOperator.atFloor(element);
        }

    }

}
