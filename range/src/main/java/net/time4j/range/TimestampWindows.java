/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimestampWindows.java) is part of project Time4J.
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

import net.time4j.PlainTimestamp;
import net.time4j.engine.TimeLine;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Comparator;
import java.util.List;


/**
 * @serial  include
 */
final class TimestampWindows
    extends IntervalCollection<PlainTimestamp> {

    //~ Statische Felder/Initialisierungen --------------------------------

    static final IntervalCollection<PlainTimestamp> EMPTY =
        new TimestampWindows();

    private static final long serialVersionUID = 5075857786753409078L;

    //~ Konstruktoren -----------------------------------------------------

    private TimestampWindows() {
        super();

    }

    private TimestampWindows(List<ChronoInterval<PlainTimestamp>> intervals) {
        super(intervals);

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    ChronoInterval<PlainTimestamp> newInterval(
        Boundary<PlainTimestamp> start,
        Boundary<PlainTimestamp> end
    ) {

        return TimestampIntervalFactory.INSTANCE.between(start, end);

    }

    @Override
    Comparator<ChronoInterval<PlainTimestamp>> getComparator() {

        return TimestampInterval.comparator();

    }

    @Override
    IntervalCollection<PlainTimestamp> create(
        List<ChronoInterval<PlainTimestamp>> intervals) {

        if (intervals.isEmpty()) {
            return TimestampWindows.EMPTY;
        }

        return new TimestampWindows(intervals);

    }

    @Override
    TimeLine<PlainTimestamp> getTimeLine() {

        return PlainTimestamp.axis();

    }

    @Override
    boolean isAfter(PlainTimestamp t1, PlainTimestamp t2) {

        return t1.isAfter(t2);

    }

    @Override
    boolean isBefore(PlainTimestamp t1, PlainTimestamp t2) {

        return t1.isBefore(t2);

    }

    /**
     * @serialData  Uses
     *              <a href="../../../serialized-form.html#net.time4j.range.SPX">
     *              a dedicated serialization form</a> as proxy. The first byte
     *              contains the type-ID 42 in the six most significant
     *              bits. The next bytes represent all contained intervals.
     *
     * Schematic algorithm:
     *
     * <pre>
       int header = 42;
       header &lt;&lt;= 2;
       out.writeByte(header);
       out.writeInt(getIntervals().size());

       for (ChronoInterval&lt;?&gt; part : getIntervals()) {
           writeBoundary(part.getStart(), out);
           writeBoundary(part.getEnd(), out);
       }

       private static void writeBoundary(
           Boundary&lt;?&gt; boundary,
           ObjectOutput out
       ) throws IOException {
           if (boundary.equals(Boundary.infinitePast())) {
               out.writeByte(1);
           } else if (boundary.equals(Boundary.infiniteFuture())) {
               out.writeByte(2);
           } else {
               out.writeByte(boundary.isOpen() ? 4 : 0);
               out.writeObject(boundary.getTemporal());
           }
       }
      </pre>
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.TIMESTAMP_WINDOW_ID);

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
