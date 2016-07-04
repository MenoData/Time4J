/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AbstractDateElement.java) is part of project Time4J.
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

import net.time4j.engine.ChronoFunction;
import net.time4j.format.DisplayElement;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;


/**
 * <p>Abstrakte Basisklasse f&uuml;r Datumselemente, die bereits alle
 * Methoden des Interface {@code AdjustableElement} vordefiniert. </p>
 *
 * @param   <V> generic type of element values
 * @author  Meno Hochschild
 */
abstract class AbstractDateElement<V extends Comparable<V>>
    extends DisplayElement<V>
    implements AdjustableElement<V, PlainDate> {

    //~ Instanzvariablen --------------------------------------------------

    private transient final ElementOperator<PlainDate> minimizer;
    private transient final ElementOperator<PlainDate> maximizer;

    //~ Konstruktoren -----------------------------------------------------

    AbstractDateElement(String name) {
        super(name);

        this.minimizer = new DateOperator(this, ElementOperator.OP_MINIMIZE);
        this.maximizer = new DateOperator(this, ElementOperator.OP_MAXIMIZE);

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public ElementOperator<PlainDate> newValue(V value) {

        return new DateOperator(this, ElementOperator.OP_NEW_VALUE, value);

    }

    @Override
    public ElementOperator<PlainDate> minimized() {

        return this.minimizer;

    }

    @Override
    public ElementOperator<PlainDate> maximized() {

        return this.maximizer;

    }

    @Override
    public ElementOperator<PlainDate> decremented() {

        return new DateOperator(this, ElementOperator.OP_DECREMENT);

    }

    @Override
    public ElementOperator<PlainDate> incremented() {

        return new DateOperator(this, ElementOperator.OP_INCREMENT);

    }

    @Override
    public ElementOperator<PlainDate> atFloor() {

        return new DateOperator(this, ElementOperator.OP_FLOOR);

    }

    @Override
    public ElementOperator<PlainDate> atCeiling() {

        return new DateOperator(this, ElementOperator.OP_CEILING);

    }

    public ElementOperator<PlainDate> setLenient(V value) {

        return new DateOperator(this, ElementOperator.OP_LENIENT, value);

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
