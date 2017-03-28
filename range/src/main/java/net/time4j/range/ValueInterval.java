/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ValueInterval.java) is part of project Time4J.
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

package net.time4j.range;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;


/**
 * <p>Represents a temporal interval with an associated value. </p>
 *
 * <p>Value intervals are either based on subclasses of {@code IsoInterval} or {@code FixedCalendarInterval}
 * and can be created by the interval instance methods named {@code withValue(V)}. The serializability
 * is determined by the serializability of the value type. </p>
 *
 * @param   <T> temporal type of time points within a given interval
 * @param   <I> generic interval type
 * @param   <V> value type associated with a value interval
 * @author  Meno Hochschild
 * @see     IsoInterval#withValue(Object) IsoInterval.withValue(V)
 * @see     FixedCalendarInterval#withValue(Object) FixedCalendarInterval.withValue(V)
 * @since   3.31/4.26
 */
/*[deutsch]
 * <p>Repr&auml;sentiert ein Zeitintervall, mit dem ein Wert assoziiert ist. </p>
 *
 * <p>Wertintervalle basieren auf Subklassen von entweder {@code IsoInterval} oder {@code FixedCalendarInterval}
 * und werden dort &uuml;ber Instanzmethoden mit dem Namen {@code withValue(V)} erzeugt. Die Serialisierbarkeit
 * ist genau dann gegeben, wenn der Werttyp serialisierbar ist. </p>
 *
 * @param   <T> temporal type of time points within a given interval
 * @param   <I> generic interval type
 * @param   <V> value type associated with a value interval
 * @author  Meno Hochschild
 * @see     IsoInterval#withValue(Object) IsoInterval.withValue(V)
 * @see     FixedCalendarInterval#withValue(Object) FixedCalendarInterval.withValue(V)
 * @since   3.31/4.26
 */
public class ValueInterval<T, I extends ChronoInterval<T>, V>
    implements ChronoInterval<T>, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = -5542033333136556857L;

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  interval delegate
     */
    private final I interval;

    /**
     * @serial  assigned value
     */
    private final V value;

    //~ Konstruktoren -----------------------------------------------------

    ValueInterval(
        I interval,
        V value
    ) {
        super();

        if (value == null) {
            throw new NullPointerException("Missing value.");
        }

        this.interval = interval;
        this.value = value;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Obtains the associated value. </p>
     *
     * @return  value associated with this interval
     */
    /*[deutsch]
     * <p>Liefert den mit diesem Intervall assoziierten Wert. </p>
     *
     * @return  value associated with this interval
     */
    public V getValue() {

        return this.value;

    }

    /**
     * <p>Assigns this value interval with given new value. </p>
     *
     * @param   value   associated value, not {@code null}
     * @return  new value interval
     */
    /*[deutsch]
     * <p>Weist diesem Wertintervall den angegebenen neuen Wert zu. </p>
     *
     * @param   value   associated value, not {@code null}
     * @return  new value interval
     */
    public ValueInterval<T, I, V> withValue(V value) {

        return new ValueInterval<>(this.interval, value);

    }

    /**
     * <p>Obtains the interval delegate instance. </p>
     *
     * @return  wrapped interval delegate
     */
    /*[deutsch]
     * <p>Liefert die Intervalldelegationsinstanz. </p>
     *
     * @return  wrapped interval delegate
     */
    public I getBoundaries() {

        return this.interval;

    }

    @Override
    public Boundary<T> getStart() {

        return this.interval.getStart();

    }

    @Override
    public Boundary<T> getEnd() {

        return this.interval.getEnd();

    }

    @Override
    public boolean isEmpty() {

        return this.interval.isEmpty();

    }

    @Override
    public boolean contains(T temporal) {

        return this.interval.contains(temporal);

    }

    @Override
    public boolean contains(ChronoInterval<T> other) {

        return this.interval.contains(other);

    }

    @Override
    public boolean isAfter(T temporal) {

        return this.interval.isAfter(temporal);

    }

    @Override
    public boolean isBefore(T temporal) {

        return this.interval.isBefore(temporal);

    }

    @Override
    public boolean isBefore(ChronoInterval<T> other) {

        return this.interval.isBefore(other);

    }

    @Override
    public boolean abuts(ChronoInterval<T> other) {

        return this.interval.abuts(other);

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof ValueInterval) {
            ValueInterval<?, ?, ?> that = ValueInterval.class.cast(obj);
            return this.interval.equals(that.interval) && this.value.equals(that.value);
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return 7 * this.interval.hashCode() + 31 * this.value.hashCode();

    }

    @Override
    public String toString() {

        return this.interval + "=>" + this.value;

    }

    /**
     * @serialData  Checks the consistency.
     * @param       in      object input stream
     * @throws      IOException if the data are not consistent
     */
    private void readObject(ObjectInputStream in)
        throws IOException {

        if ((this.interval == null) || (this.value == null)) {
            throw new StreamCorruptedException();
        }

    }

}