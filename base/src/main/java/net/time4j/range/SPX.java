/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SPX.java) is part of project Time4J.
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
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.Quarter;
import net.time4j.engine.TimeLine;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Serialisierungsform f&uuml;r Intervalle. </p>
 *
 * @author  Meno Hochschild
 * @serial  include
 */
final class SPX
    implements Externalizable {

    //~ Statische Felder/Initialisierungen ----------------------------

    /** Serialisierungstyp von {@code DateInterval}. */
    static final int DATE_TYPE = 32;

    /** Serialisierungstyp von {@code ClockInterval}. */
    static final int TIME_TYPE = 33;

    /** Serialisierungstyp von {@code TimestampInterval}. */
    static final int TIMESTAMP_TYPE = 34;

    /** Serialisierungstyp von {@code MomentInterval}. */
    static final int MOMENT_TYPE = 35;

    /** Serialisierungstyp von {@code CalendarYear}. */
    static final int YEAR_TYPE = 36;

    /** Serialisierungstyp von {@code CalendarQuarter}. */
    static final int QUARTER_TYPE = 37;

    /** Serialisierungstyp von {@code CalendarMonth}. */
    static final int MONTH_TYPE = 38;

    /** Serialisierungstyp von {@code CalendarWeek}. */
    static final int WEEK_TYPE = 39;

    /** Serialisierungstyp von {@code DateWindows}. */
    static final int DATE_WINDOW_ID = 40;

    /** Serialisierungstyp von {@code ClockWindows}. */
    static final int CLOCK_WINDOW_ID = 41;

    /** Serialisierungstyp von {@code TimestampWindows}. */
    static final int TIMESTAMP_WINDOW_ID = 42;

    /** Serialisierungstyp von {@code MomentWindows}. */
    static final int MOMENT_WINDOW_ID = 43;

    /** Serialisierungstyp von {@code GenericWindows}. */
    static final int GENERIC_WINDOW_ID = 44;

    /** Serialisierungstyp von {@code Boundary}. */
    static final int BOUNDARY_TYPE = 57;

    private static final long serialVersionUID = 1L;

    //~ Instanzvariablen ----------------------------------------------

    private transient Object obj;
    private transient int type;

    //~ Konstruktoren -------------------------------------------------

    /**
     * <p>Benutzt in der Deserialisierung gem&auml;&szlig; dem Kontrakt
     * von {@code Externalizable}. </p>
     */
    public SPX() {
        super();

    }

    /**
     * <p>Benutzt in der Serialisierung (writeReplace). </p>
     *
     * @param   obj     object to be serialized
     * @param   type    serialization type (corresponds to type of obj)
     */
    SPX(
        Object obj,
        int type
    ) {
        super();

        this.obj = obj;
        this.type = type;

    }

    //~ Methoden ------------------------------------------------------

    /**
     * <p>Implementation method of interface {@link Externalizable}. </p>
     *
     * <p>The first byte contains within the 6 most-significant bits the type
     * of the object to be serialized. Then the data bytes follow in a
     * bit-compressed representation. </p>
     *
     * @serialData  data layout see {@code writeReplace()}-method of object to be serialized
     * @param       out     output stream
     * @throws      IOException in case of I/O-problems
     */
    /*[deutsch]
     * <p>Implementierungsmethode des Interface {@link Externalizable}. </p>
     *
     * <p>Das erste Byte enth&auml;lt um 2 Bits nach links verschoben den
     * Typ des zu serialisierenden Objekts. Danach folgen die Daten-Bits
     * in einer bit-komprimierten Darstellung. </p>
     *
     * @serialData  data layout see {@code writeReplace()}-method of object to be serialized
     * @param       out     output stream
     * @throws      IOException in case of I/O-problems
     */
    @Override
    public void writeExternal(ObjectOutput out)
        throws IOException {

        if (this.type == BOUNDARY_TYPE) {
            Boundary<?> boundary = (Boundary<?>) this.obj;
            int header = (BOUNDARY_TYPE << 2);

            if (boundary.equals(Boundary.infinitePast())) {
                header |= 1;
                out.writeByte(header);
            } else if (boundary.equals(Boundary.infiniteFuture())) {
                header |= 2;
                out.writeByte(header);
            } else {
                out.writeByte(header);
                out.writeByte(boundary.isOpen() ? 1 : 0);
                out.writeObject(boundary.getTemporal());
            }
        } else {
            int header = (this.type << 2);
            out.writeByte(header);

            switch (this.type) {
                case DATE_TYPE:
                case TIME_TYPE:
                case TIMESTAMP_TYPE:
                case MOMENT_TYPE:
                    ChronoInterval<?> interval = (ChronoInterval<?>) this.obj;
                    writeBoundary(interval.getStart(), out);
                    writeBoundary(interval.getEnd(), out);
                    break;
                case YEAR_TYPE:
                    CalendarYear cy = (CalendarYear) this.obj;
                    out.writeInt(cy.getValue());
                    break;
                case QUARTER_TYPE:
                    CalendarQuarter cq = (CalendarQuarter) this.obj;
                    out.writeInt(cq.getYear());
                    out.writeInt(cq.getQuarter().getValue());
                    break;
                case MONTH_TYPE:
                    CalendarMonth cm = (CalendarMonth) this.obj;
                    out.writeInt(cm.getYear());
                    out.writeInt(cm.getMonth().getValue());
                    break;
                case WEEK_TYPE:
                    CalendarWeek cw = (CalendarWeek) this.obj;
                    out.writeInt(cw.getYear());
                    out.writeInt(cw.getWeek());
                    break;
                case DATE_WINDOW_ID:
                case CLOCK_WINDOW_ID:
                case TIMESTAMP_WINDOW_ID:
                case MOMENT_WINDOW_ID:
                    IntervalCollection<?> window =
                        IntervalCollection.class.cast(this.obj);
                    out.writeInt(window.getSize());
                    for (ChronoInterval<?> part : window.getIntervals()) {
                        writeBoundary(part.getStart(), out);
                        writeBoundary(part.getEnd(), out);
                    }
                    break;
                case GENERIC_WINDOW_ID:
                    IntervalCollection<?> gwindow =
                        IntervalCollection.class.cast(this.obj);
                    out.writeObject(gwindow.getTimeLine());
                    out.writeInt(gwindow.getSize());
                    for (ChronoInterval<?> part : gwindow.getIntervals()) {
                        out.writeObject(part.getStart().getTemporal());
                        out.writeObject(part.getEnd().getTemporal());
                    }
                    break;
                default:
                    throw new InvalidClassException("Unknown serialized type.");
            }
        }

    }

    /**
     * <p>Implementation method of interface {@link Externalizable}. </p>
     *
     * @param   in      input stream
     * @throws  IOException in case of I/O-problems
     * @throws  ClassNotFoundException if class-loading fails
     */
    /*[deutsch]
     * <p>Implementierungsmethode des Interface {@link Externalizable}. </p>
     *
     * @param   in      input stream
     * @throws  IOException in case of I/O-problems
     * @throws  ClassNotFoundException if class-loading fails
     */
    @Override
    public void readExternal(ObjectInput in)
        throws IOException, ClassNotFoundException {

        byte header = in.readByte();
        int typeInfo = (header & 0xFF) >> 2;

        switch (typeInfo) {
            case DATE_TYPE:
                this.obj = readDateInterval(in);
                break;
            case TIME_TYPE:
                this.obj = readClockInterval(in);
                break;
            case TIMESTAMP_TYPE:
                this.obj = readTimestampInterval(in);
                break;
            case MOMENT_TYPE:
                this.obj = readMomentInterval(in);
                break;
            case YEAR_TYPE:
                this.obj = readCalendarYear(in);
                break;
            case QUARTER_TYPE:
                this.obj = readCalendarQuarter(in);
                break;
            case MONTH_TYPE:
                this.obj = readCalendarMonth(in);
                break;
            case WEEK_TYPE:
                this.obj = readCalendarWeek(in);
                break;
            case DATE_WINDOW_ID:
                this.obj = readDateWindows(in);
                break;
            case CLOCK_WINDOW_ID:
                this.obj = readClockWindows(in);
                break;
            case TIMESTAMP_WINDOW_ID:
                this.obj = readTimestampWindows(in);
                break;
            case MOMENT_WINDOW_ID:
                this.obj = readMomentWindows(in);
                break;
            case GENERIC_WINDOW_ID:
                this.obj = readGenericWindows(in);
                break;
            case BOUNDARY_TYPE:
                this.obj = readBoundary(in, header);
                break;
            default:
                throw new StreamCorruptedException("Unknown serialized type.");
        }

    }

    private Object readResolve() throws ObjectStreamException {

        return this.obj;

    }

    private static DateInterval readDateInterval(ObjectInput in)
        throws IOException, ClassNotFoundException {

        Object o1 = readBoundary(in);
        Object o2 = readBoundary(in);

        Boundary<PlainDate> start = getBoundary(o1, PlainDate.class, false);
        Boundary<PlainDate> end = getBoundary(o2, PlainDate.class, true);

        if ((start != null) && (end != null)) {
            return new DateInterval(start, end);
        }

        throw new StreamCorruptedException();

    }

    private static ClockInterval readClockInterval(ObjectInput in)
        throws IOException, ClassNotFoundException {

        Object o1 = readBoundary(in);
        Object o2 = readBoundary(in);

        Boundary<PlainTime> start = getBoundary(o1, PlainTime.class, false);
        Boundary<PlainTime> end = getBoundary(o2, PlainTime.class, true);

        if ((start != null) && (end != null)) {
            return new ClockInterval(start, end);
        }

        throw new StreamCorruptedException();

    }

    private static TimestampInterval readTimestampInterval(ObjectInput in)
        throws IOException, ClassNotFoundException {

        Object o1 = readBoundary(in);
        Object o2 = readBoundary(in);

        Boundary<PlainTimestamp> start =
            getBoundary(o1, PlainTimestamp.class, false);
        Boundary<PlainTimestamp> end =
            getBoundary(o2, PlainTimestamp.class, true);

        if ((start != null) && (end != null)) {
            return new TimestampInterval(start, end);
        }

        throw new StreamCorruptedException();

    }

    private static MomentInterval readMomentInterval(ObjectInput in)
        throws IOException, ClassNotFoundException {

        Object o1 = readBoundary(in);
        Object o2 = readBoundary(in);

        Boundary<Moment> start = getBoundary(o1, Moment.class, false);
        Boundary<Moment> end = getBoundary(o2, Moment.class, true);

        if ((start != null) && (end != null)) {
            return new MomentInterval(start, end);
        }

        throw new StreamCorruptedException();

    }

    private static CalendarYear readCalendarYear(ObjectInput in)
        throws IOException {

        int year = in.readInt();
        return CalendarYear.of(year);

    }

    private static CalendarQuarter readCalendarQuarter(ObjectInput in)
        throws IOException {

        int year = in.readInt();
        int quarter = in.readInt();
        return CalendarQuarter.of(year, Quarter.valueOf(quarter));

    }

    private static CalendarMonth readCalendarMonth(ObjectInput in)
        throws IOException {

        int year = in.readInt();
        int month = in.readInt();
        return CalendarMonth.of(year, Month.valueOf(month));

    }

    private static CalendarWeek readCalendarWeek(ObjectInput in)
        throws IOException {

        int year = in.readInt();
        int week = in.readInt();
        return CalendarWeek.of(year, week);

    }

    private static IntervalCollection<PlainDate> readDateWindows(
        ObjectInput in
    ) throws IOException, ClassNotFoundException {

        int size = in.readInt();

        if (size == 0) {
            return DateWindows.EMPTY;
        }

        List<ChronoInterval<PlainDate>> intervals = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            intervals.add(readDateInterval(in));
        }

        intervals.sort(DateInterval.comparator());
        return DateWindows.EMPTY.plus(intervals);

    }

    private static IntervalCollection<PlainTime> readClockWindows(
        ObjectInput in
    ) throws IOException, ClassNotFoundException {

        int size = in.readInt();

        if (size == 0) {
            return ClockWindows.EMPTY;
        }

        List<ChronoInterval<PlainTime>> intervals = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            intervals.add(readClockInterval(in));
        }

        intervals.sort(ClockInterval.comparator());
        return ClockWindows.EMPTY.plus(intervals);

    }

    private static IntervalCollection<PlainTimestamp> readTimestampWindows(
        ObjectInput in
    ) throws IOException, ClassNotFoundException {

        int size = in.readInt();

        if (size == 0) {
            return TimestampWindows.EMPTY;
        }

        List<ChronoInterval<PlainTimestamp>> intervals = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            intervals.add(readTimestampInterval(in));
        }

        intervals.sort(TimestampInterval.comparator());
        return TimestampWindows.EMPTY.plus(intervals);

    }

    private static IntervalCollection<Moment> readMomentWindows(ObjectInput in)
        throws IOException, ClassNotFoundException {

        int size = in.readInt();

        if (size == 0) {
            return MomentWindows.EMPTY;
        }

        List<ChronoInterval<Moment>> intervals = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            intervals.add(readMomentInterval(in));
        }

        intervals.sort(MomentInterval.comparator());
        return MomentWindows.EMPTY.plus(intervals);

    }

    @SuppressWarnings("unchecked")
    private static IntervalCollection<?> readGenericWindows(ObjectInput in)
        throws IOException, ClassNotFoundException {

        TimeLine<?> timeLine = TimeLine.class.cast(in.readObject());
        int size = in.readInt();

        List<ChronoInterval<?>> intervals = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            Object s = in.readObject();
            Object e = in.readObject();

            intervals.add(new SimpleInterval(s, e, timeLine));
        }

        intervals.sort(new IntervalComparator(timeLine));
        return new GenericWindows(timeLine, intervals);

    }

    // serialization of a single boundary object
    private static Object readBoundary(
        ObjectInput in,
        byte header
    ) throws IOException, ClassNotFoundException {

        int past = (header & 0x1);

        if (past == 1) {
            return Boundary.infinitePast();
        }

        int future = (header & 0x2);

        if (future == 2) {
            return Boundary.infiniteFuture();
        }

        int openClosed = in.readByte();
        IntervalEdge edge;

        switch (openClosed) {
            case 0:
                edge = IntervalEdge.CLOSED;
                break;
            case 1:
                edge = IntervalEdge.OPEN;
                break;
            default:
                throw new StreamCorruptedException("Invalid edge state.");
        }

        Object t = in.readObject();
        return Boundary.of(edge, t);

    }

    private static void writeBoundary(
        Boundary<?> boundary,
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

    private static Object readBoundary(ObjectInput in)
        throws IOException, ClassNotFoundException {

        int header = (in.readByte() & 0xFF);

        if ((header & 0x1) == 1) {
            return Boundary.infinitePast();
        } else if ((header & 0x2) == 2) {
            return Boundary.infiniteFuture();
        }

        IntervalEdge edge = (
            ((header & 0x4) == 4)
            ? IntervalEdge.OPEN
            : IntervalEdge.CLOSED);

        Object t = in.readObject();
        return Boundary.of(edge, t);

    }

    private static <T> Boundary<T> getBoundary(
        Object obj,
        Class<T> type,
        boolean ending
    ) {

        Boundary<T> ret = null;

        if (obj instanceof Boundary) {
            Boundary<?> b = cast(obj);

            if (b.isInfinite()) {
                Boundary<T> expected;

                if (ending) {
                    expected = Boundary.infiniteFuture();
                } else {
                    expected = Boundary.infinitePast();
                }

                if (expected.equals(obj)) {
                    ret = expected;
                }
            } else if (type.isInstance(b.getTemporal())) {
                ret = cast(b);
            }

        }

        return ret;

    }

    @SuppressWarnings("unchecked")
    private static <T> T cast(Object obj) {

        return (T) obj;

    }

}
