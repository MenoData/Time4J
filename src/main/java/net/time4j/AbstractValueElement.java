/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AbstractValueElement.java) is part of project Time4J.
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
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoOperator;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;


/**
 * <p>Abstrakte Basisklasse f&uuml;r ISO-Elemente, die bereits alle
 * Methoden des Interface {@code AdjustableElement} vordefiniert. </p>
 *
 * @param   <V> generic type of element values
 * @param   <T> generic target type for a {@code ChronoOperator}
 * @author  Meno Hochschild
 */
abstract class AbstractValueElement
    <V extends Comparable<V>, T extends ChronoEntity<T>>
    extends AdvancedElement<V> {

    //~ Instanzvariablen --------------------------------------------------

    private transient final Map<OperatorType, ZonalOperator<T>> cache;

    //~ Konstruktoren -----------------------------------------------------

    AbstractValueElement(String name) {
        super(name);

        Map<OperatorType, ZonalOperator<T>> ops =
            new EnumMap<OperatorType, ZonalOperator<T>>(OperatorType.class);
        ops.put(
            OperatorType.MINIMIZE,
            new OperatorDelegate<V, T>(this, OperatorType.MINIMIZE));
        ops.put(
            OperatorType.MAXIMIZE,
            new OperatorDelegate<V, T>(this, OperatorType.MAXIMIZE));
        ops.put(
            OperatorType.DECREMENT,
            new OperatorDelegate<V, T>(this, OperatorType.DECREMENT));
        ops.put(
            OperatorType.INCREMENT,
            new OperatorDelegate<V, T>(this, OperatorType.INCREMENT));
        ops.put(
            OperatorType.FLOOR,
            new OperatorDelegate<V, T>(this, OperatorType.FLOOR));
        ops.put(
            OperatorType.CEILING,
            new OperatorDelegate<V, T>(this, OperatorType.CEILING));
        this.cache = Collections.unmodifiableMap(ops);

    }

    //~ Methoden ----------------------------------------------------------

    public ZonalOperator<T> minimized() {

        return this.cache.get(OperatorType.MINIMIZE);

    }

    public ZonalOperator<T> maximized() {

        return this.cache.get(OperatorType.MAXIMIZE);

    }

    public ZonalOperator<T> decremented() {

        return this.cache.get(OperatorType.DECREMENT);

    }

    public ZonalOperator<T> incremented() {

        return this.cache.get(OperatorType.INCREMENT);

    }

    public ZonalOperator<T> atFloor() {

        return this.cache.get(OperatorType.FLOOR);

    }

    public ZonalOperator<T> atCeiling() {

        return this.cache.get(OperatorType.CEILING);

    }

    public ChronoOperator<T> setLenient(V value) {

        return new OperatorDelegate<V, T>(this, OperatorType.LENIENT, value);

    }

    public ChronoOperator<T> rolledBy(long units) {

        return new OperatorDelegate<V, T>(this, units, this.getRollMax());

    }

    /**
     * <p>Gibt das Rollmaximum an. </p>
     *
     * @return  rolling maximum or {@code null} if context-dependent maximum
     *          is sufficient
     */
    protected V getRollMax() {

        return null;

    }

}
