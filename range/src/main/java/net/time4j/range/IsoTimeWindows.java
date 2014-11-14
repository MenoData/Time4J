/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (IsoTimeWindows.java) is part of project Time4J.
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
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.engine.Temporal;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * <p>Concrete {@code TimeWindows}-implementation. </p>
 *
 * @author  Meno Hochschild
 * @serial  include
 */
final class IsoTimeWindows
    <T extends Temporal<? super T>, I extends IsoInterval<T, I>>
    extends TimeWindows<I> {

    //~ Statische Felder/Initialisierungen --------------------------------

    static final int DATE_WINDOW_ID = 60;
    static final int CLOCK_WINDOW_ID = 61;
    static final int TIMESTAMP_WINDOW_ID = 62;
    static final int MOMENT_WINDOW_ID = 63;

    static final TimeWindows<DateInterval> DATE_INSTANCE =
        new IsoTimeWindows<PlainDate, DateInterval>(
            DATE_WINDOW_ID, DateInterval.comparator(), true);
    static final TimeWindows<ClockInterval> CLOCK_INSTANCE =
        new IsoTimeWindows<PlainTime, ClockInterval>(
            CLOCK_WINDOW_ID, ClockInterval.comparator(), false);
    static final TimeWindows<TimestampInterval> TIMESTAMP_INSTANCE =
        new IsoTimeWindows<PlainTimestamp, TimestampInterval>(
            TIMESTAMP_WINDOW_ID, TimestampInterval.comparator(), false);
    static final TimeWindows<MomentInterval> MOMENT_INSTANCE =
        new IsoTimeWindows<Moment, MomentInterval>(
            MOMENT_WINDOW_ID, MomentInterval.comparator(), false);

    private static final long serialVersionUID = 1L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final int typeID;
    private transient final Comparator<I> comparator;
    private transient final boolean calendrical;

    //~ Konstruktoren -----------------------------------------------------

    private IsoTimeWindows(
        int typeID,
        Comparator<I> comparator,
        boolean calendrical
    ) {
        super();

        this.typeID = typeID;
        this.comparator = comparator;
        this.calendrical = calendrical;

    }

    private IsoTimeWindows(
        int typeID,
        Comparator<I> comparator,
        boolean calendrical,
        List<I> intervals
    ) {
        super(intervals);

        this.typeID = typeID;
        this.comparator = comparator;
        this.calendrical = calendrical;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public TimeWindows<I> gaps() {

        List<I> gaps = new ArrayList<I>();
        T previous = null;

        for (int i = 0, n = this.toIntervals().size() - 1; i < n; i++) {
            I current = this.toIntervals().get(i);

            if (current.getEnd().isInfinite()) {
                break;
            }

            T gapStart = current.getEnd().getTemporal();

            if (current.getEnd().isClosed()) {
                gapStart = current.getTimeLine().stepForward(gapStart);
                if (gapStart == null) {
                    break;
                }
            }

            if (
                (previous == null)
                || gapStart.isAfter(previous)
            ) {
                previous = gapStart;
            } else {
                gapStart = previous;
            }

            T gapEnd = this.toIntervals().get(i + 1).getStart().getTemporal();

            if (
                (gapEnd == null)
                || !gapEnd.isAfter(gapStart)
            ) {
                continue;
            }

            IntervalEdge edge = IntervalEdge.OPEN;

            if (this.calendrical) {
                edge = IntervalEdge.CLOSED;
                gapEnd = current.getTimeLine().stepBackwards(gapEnd);
                if (gapEnd == null) {
                    continue;
                }
            }

            Boundary<T> s = Boundary.ofClosed(gapStart);
            Boundary<T> e = Boundary.of(edge, gapEnd);
            gaps.add(current.getFactory().between(s, e));
        }

        return this.create(gaps);

    }

    @Override
    Comparator<I> getComparator() {

        return this.comparator;

    }

    @Override
    TimeWindows<I> create(List<I> intervals) {

        return new IsoTimeWindows<T, I>(
            this.typeID,
            this.comparator,
            this.calendrical,
            intervals);

    }

    @Override
    int getTypeID() {

        return this.typeID;

    }

    /**
     * @serialData  Uses
     *              <a href="../../../serialized-form.html#net.time4j.range.SPX">
     *              a dedicated serialization form</a> as proxy. The first byte
     *              contains the type-ID in the six most significant
     *              bits. The type-ID is 60 for date intervals, 61 for clock
     *              intervals, 62 for timestamp intervals and 63 for moment
     *              intervals. The next bytes represent the start and the end
     *              boundary.
     *
     * Schematic algorithm:
     *
     * <pre>
     *  int header = 60; // or 61 or 62 or 63
     *  header <<= 2;
     *  out.writeByte(header);
     *  out.writeObject(toIntervals());
     * </pre>
     */
    private Object writeReplace() throws ObjectStreamException {

        return new SPX(this, this.getTypeID());

    }

    /**
     * @serialData  Blocks because a serialization proxy is required.
     * @throws      InvalidObjectException (always)
     */
    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {

        throw new InvalidObjectException("Serialization proxy required.");

    }

}
