/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (MomentWindows.java) is part of project Time4J.
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

import net.time4j.Moment;
import net.time4j.engine.TimeLine;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Comparator;
import java.util.List;


/**
 * @serial  include
 */
final class MomentWindows
    extends IntervalCollection<Moment> {

    //~ Statische Felder/Initialisierungen --------------------------------

    static final IntervalCollection<Moment> EMPTY = new MomentWindows();

    private static final long serialVersionUID = 6628458032332509882L;

    //~ Konstruktoren -----------------------------------------------------

    private MomentWindows() {
        super();

    }

    private MomentWindows(List<ChronoInterval<Moment>> intervals) {
        super(intervals);

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    ChronoInterval<Moment> newInterval(
        Boundary<Moment> start,
        Boundary<Moment> end
    ) {

        return MomentIntervalFactory.INSTANCE.between(start, end);

    }

    @Override
    Comparator<ChronoInterval<Moment>> getComparator() {

        return MomentInterval.comparator();

    }

    @Override
    IntervalCollection<Moment> create(List<ChronoInterval<Moment>> intervals) {

        if (intervals.isEmpty()) {
            return MomentWindows.EMPTY;
        }

        return new MomentWindows(intervals);

    }

    @Override
    TimeLine<Moment> getTimeLine() {

        return Moment.axis();

    }

    @Override
    boolean isCalendrical() {

        return false;

    }

    @Override
    boolean isAfter(Moment t1, Moment t2) {

        return t1.isAfter(t2);

    }

    @Override
    boolean isBefore(Moment t1, Moment t2) {

        return t1.isBefore(t2);

    }

    /**
     * @serialData  Uses
     *              <a href="../../../serialized-form.html#net.time4j.range.SPX">
     *              a dedicated serialization form</a> as proxy. The first byte
     *              contains the type-ID 43 in the six most significant
     *              bits. The next bytes represent all contained intervals.
     *
     * Schematic algorithm:
     *
     * <pre>
       int header = 43;
       header &lt;&lt;= 2;
       out.writeByte(header);
       out.writeInt(getIntervals().size());

       for (ChronoInterval&lt;?&lt; part : getIntervals()) {
           writeBoundary(part.getStart(), out);
           writeBoundary(part.getEnd(), out);
       }

       private static void writeBoundary(
           Boundary&lt;?&lt; boundary,
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

        return new SPX(this, SPX.MOMENT_WINDOW_ID);

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
