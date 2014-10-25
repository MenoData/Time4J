/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (DateOperator.java) is part of project Time4J.
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
import net.time4j.engine.ChronoOperator;


/**
 * <p>Definiert eine Manipulation von Datumsobjekten nach
 * dem Strategy-Entwurfsmuster. </p>
 *
 * @author      Meno Hochschild
 * @concurrency <immutable>
 */
final class DateOperator
    extends ElementOperator<PlainDate> {

    //~ Instanzvariablen --------------------------------------------------

    private final ChronoOperator<PlainDate> opCache;
    private final ChronoOperator<PlainTimestamp> tsCache;
    private final ChronoOperator<Moment> moCache;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruiert eine neue Instanz. </p>
     *
     * @param   element         element an operator will be applied on
     * @param   type            type of operator
     */
    DateOperator(
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
    DateOperator(
        AdvancedElement<?> element,
        int type,
        Object value // optional
    ) {
        super(element, type);

        switch (type) {
            case OP_NEW_VALUE:
                this.opCache = newValue(element, value);
                this.tsCache = newValueTS(element, value);
                break;
            case OP_MINIMIZE:
                this.opCache = element.minimized(PlainDate.class);
                this.tsCache = element.minimized(PlainTimestamp.class);
                break;
            case OP_MAXIMIZE:
                this.opCache = element.maximized(PlainDate.class);
                this.tsCache = element.maximized(PlainTimestamp.class);
                break;
            case OP_DECREMENT:
                this.opCache = element.decremented(PlainDate.class);
                this.tsCache = element.decremented(PlainTimestamp.class);
                break;
            case OP_INCREMENT:
                this.opCache = element.incremented(PlainDate.class);
                this.tsCache = element.incremented(PlainTimestamp.class);
                break;
            case OP_FLOOR:
                this.opCache = element.atFloor(PlainDate.class);
                this.tsCache = element.atFloor(PlainTimestamp.class);
                break;
            case OP_CEILING:
                this.opCache = element.atCeiling(PlainDate.class);
                this.tsCache = element.atCeiling(PlainTimestamp.class);
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
    public PlainDate apply(PlainDate entity) {

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

    private static <V extends Comparable<V>>
    ChronoOperator<PlainDate> newValue(
        AdvancedElement<V> element,
        Object value
    ) {

        return element.newValue(
            element.getType().cast(value),
            PlainDate.class);

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
    ChronoOperator<PlainDate> lenient(
        AdvancedElement<V> element,
        Object value
    ) {

        return element.setLenient(
            element.getType().cast(value),
            PlainDate.class);

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
            value
        );

    }

}
