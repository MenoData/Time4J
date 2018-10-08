/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (GenericWindows.java) is part of project Time4J.
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

import net.time4j.engine.TimeLine;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Comparator;
import java.util.List;


/**
 * @serial  include
 */
final class GenericWindows<T>
    extends IntervalCollection<T> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = 7068295351485872982L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final TimeLine<T> timeLine;

    //~ Konstruktoren -----------------------------------------------------

    GenericWindows(
        TimeLine<T> timeLine,
        List<ChronoInterval<T>> intervals
    ) {
        super(intervals);

        if (timeLine == null) {
            throw new NullPointerException("Missing timeline.");
        }

        this.timeLine = timeLine;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    ChronoInterval<T> newInterval(
        Boundary<T> start,
        Boundary<T> end
    ) {

        SimpleInterval.Factory<T> factory =  SimpleInterval.on(this.timeLine);

        if (start.isInfinite()) {
            T e = end.getTemporal();
            if (e != null) {
                if (this.timeLine.isCalendrical()) {
                    if (end.isOpen()) {
                        e = this.timeLine.stepBackwards(e);
                    }
                } else if (end.isClosed()) {
                    e = this.timeLine.stepForward(e);
                }
            }
            if (e == null) {
                return new SimpleInterval<>(start, Boundary.infiniteFuture(), this.timeLine);
            } else {
                return factory.until(e);
            }
        } else if (end.isInfinite()) {
            T s = start.getTemporal();
            if (start.isOpen()) {
                s = this.timeLine.stepForward(s);
            }
            return factory.since(s);
        } else {
            T s = start.getTemporal();
            if (start.isOpen()) {
                s = this.timeLine.stepForward(s);
            }
            T e = end.getTemporal();
            if (this.timeLine.isCalendrical()) {
                if (end.isOpen()) {
                    e = this.timeLine.stepBackwards(e);
                }
            } else if (end.isClosed()) {
                e = this.timeLine.stepForward(e);
            }
            return factory.between(s, e);
        }

    }

    @Override
    Comparator<ChronoInterval<T>> getComparator() {

        return new IntervalComparator<>(this.timeLine);

    }

    @Override
    IntervalCollection<T> create(List<ChronoInterval<T>> intervals) {

        return new GenericWindows<>(this.timeLine, intervals);

    }

    @Override
    TimeLine<T> getTimeLine() {

        return this.timeLine;

    }

    /**
     * @serialData  Uses
     *              <a href="../../../serialized-form.html#net.time4j.range.SPX">
     *              a dedicated serialization form</a> as proxy. The first byte
     *              contains the type-ID 44 in the six most significant
     *              bits. The next bytes represent the timeline and then
     *              all contained intervals.
     *
     * Schematic algorithm:
     *
     * <pre>
       int header = 44;
       header &lt;&lt;= 2;
       out.writeByte(header);
       out.writeObject(getTimeLine());
       out.writeInt(getIntervals().size());

       for (ChronoInterval&lt;?&lt; part : getIntervals()) {
           out.writeObject(part.getStart().getTemporal());
           out.writeObject(part.getEnd().getTemporal());
       }
      </pre>
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.GENERIC_WINDOW_ID);

    }

    /**
     * @serialData  Blocks because a serialization proxy is required.
     * @param       in      object input stream
     * @throws      InvalidObjectException (always)
     */
    private void readObject(ObjectInputStream in)
        throws IOException {

        throw new InvalidObjectException("Serialization proxy required.");

    }

}
