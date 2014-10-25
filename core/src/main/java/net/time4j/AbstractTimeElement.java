/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AbstractTimeElement.java) is part of project Time4J.
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
import net.time4j.engine.ChronoFunction;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * <p>Abstrakte Basisklasse f&uuml;r Uhrzeit-Elemente, die bereits alle
 * Methoden des Interface {@code AdjustableElement} vordefiniert. </p>
 *
 * @param   <V> generic type of element values
 * @author  Meno Hochschild
 */
abstract class AbstractTimeElement<V extends Comparable<V>>
    extends AdvancedElement<V>
    implements AdjustableElement<V, PlainTime> {

    //~ Instanzvariablen --------------------------------------------------

    private transient final Map<Integer, ElementOperator<PlainTime>> cache;

    //~ Konstruktoren -----------------------------------------------------

    AbstractTimeElement(String name) {
        super(name);

        Map<Integer, ElementOperator<PlainTime>> ops =
            new HashMap<Integer, ElementOperator<PlainTime>>();
        ops.put(
            ElementOperator.OP_MINIMIZE,
            new TimeOperator(this, ElementOperator.OP_MINIMIZE));
        ops.put(
            ElementOperator.OP_MAXIMIZE,
            new TimeOperator(this, ElementOperator.OP_MAXIMIZE));
        ops.put(
            ElementOperator.OP_DECREMENT,
            new TimeOperator(this, ElementOperator.OP_DECREMENT));
        ops.put(
            ElementOperator.OP_INCREMENT,
            new TimeOperator(this, ElementOperator.OP_INCREMENT));
        ops.put(
            ElementOperator.OP_FLOOR,
            new TimeOperator(this, ElementOperator.OP_FLOOR));
        ops.put(
            ElementOperator.OP_CEILING,
            new TimeOperator(this, ElementOperator.OP_CEILING));
        this.cache = Collections.unmodifiableMap(ops);

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public ElementOperator<PlainTime> newValue(V value) {

        return new TimeOperator(this, ElementOperator.OP_NEW_VALUE, value);

    }

    @Override
    public ElementOperator<PlainTime> minimized() {

        return this.cache.get(Integer.valueOf(ElementOperator.OP_MINIMIZE));

    }

    @Override
    public ElementOperator<PlainTime> maximized() {

        return this.cache.get(Integer.valueOf(ElementOperator.OP_MAXIMIZE));

    }

    @Override
    public ElementOperator<PlainTime> decremented() {

        return this.cache.get(Integer.valueOf(ElementOperator.OP_DECREMENT));

    }

    @Override
    public ElementOperator<PlainTime> incremented() {

        return this.cache.get(Integer.valueOf(ElementOperator.OP_INCREMENT));

    }

    @Override
    public ElementOperator<PlainTime> atFloor() {

        return this.cache.get(Integer.valueOf(ElementOperator.OP_FLOOR));

    }

    @Override
    public ElementOperator<PlainTime> atCeiling() {

        return this.cache.get(Integer.valueOf(ElementOperator.OP_CEILING));

    }

    public ElementOperator<PlainTime> setLenient(V value) {

        return new TimeOperator(this, ElementOperator.OP_LENIENT, value);

    }

    @Override
    public ChronoFunction<Moment, V> inStdTimezone() {

        return this.in(Timezone.ofSystem());

    }

    @Override
    public ChronoFunction<Moment, V> inTimezone(TZID tzid) {

        return this.in(Timezone.of(tzid));

    }

    @Override
    public ChronoFunction<Moment, V> in(Timezone tz) {

        return new ZonalQuery<V>(this, tz);

    }

    @Override
    public ChronoFunction<Moment, V> atUTC() {

        return this.at(ZonalOffset.UTC);

    }

    @Override
    public ChronoFunction<Moment, V> at(ZonalOffset offset) {

        return new ZonalQuery<V>(this, offset);

    }

}
