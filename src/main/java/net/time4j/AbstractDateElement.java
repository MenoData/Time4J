/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AbstractDateElement.java) is part of project Time4J.
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * <p>Abstrakte Basisklasse f&uuml;r Datumselemente, die bereits alle
 * Methoden des Interface {@code AdjustableElement} vordefiniert. </p>
 *
 * @param   <V> generic type of element values
 * @author  Meno Hochschild
 */
abstract class AbstractDateElement<V extends Comparable<V>>
    extends AdvancedElement<V>
    implements AdjustableElement<V, DateOperator> {

    //~ Instanzvariablen --------------------------------------------------

    private transient final Map<Integer, DateOperator> cache;

    //~ Konstruktoren -----------------------------------------------------

    AbstractDateElement(String name) {
        super(name);

        Map<Integer, DateOperator> ops =
            new HashMap<Integer, DateOperator>();
        ops.put(
            ElementOperator.OP_MINIMIZE,
            new DateOperator(this, ElementOperator.OP_MINIMIZE));
        ops.put(
            ElementOperator.OP_MAXIMIZE,
            new DateOperator(this, ElementOperator.OP_MAXIMIZE));
        ops.put(
            ElementOperator.OP_DECREMENT,
            new DateOperator(this, ElementOperator.OP_DECREMENT));
        ops.put(
            ElementOperator.OP_INCREMENT,
            new DateOperator(this, ElementOperator.OP_INCREMENT));
        ops.put(
            ElementOperator.OP_FLOOR,
            new DateOperator(this, ElementOperator.OP_FLOOR));
        ops.put(
            ElementOperator.OP_CEILING,
            new DateOperator(this, ElementOperator.OP_CEILING));
        this.cache = Collections.unmodifiableMap(ops);

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public DateOperator minimized() {

        return this.cache.get(Integer.valueOf(ElementOperator.OP_MINIMIZE));

    }

    @Override
    public DateOperator maximized() {

        return this.cache.get(Integer.valueOf(ElementOperator.OP_MAXIMIZE));

    }

    @Override
    public DateOperator decremented() {

        return this.cache.get(Integer.valueOf(ElementOperator.OP_DECREMENT));

    }

    @Override
    public DateOperator incremented() {

        return this.cache.get(Integer.valueOf(ElementOperator.OP_INCREMENT));

    }

    @Override
    public DateOperator atFloor() {

        return this.cache.get(Integer.valueOf(ElementOperator.OP_FLOOR));

    }

    @Override
    public DateOperator atCeiling() {

        return this.cache.get(Integer.valueOf(ElementOperator.OP_CEILING));

    }

    public DateOperator setLenient(V value) {

        return new DateOperator(this, ElementOperator.OP_LENIENT, value);

    }

}
