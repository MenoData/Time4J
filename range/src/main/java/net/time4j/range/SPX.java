/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
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
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.engine.Temporal;

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

    /** Serialisierungstyp von {@code DateWindows}. */
    static final int DATE_WINDOW_ID = 40;

    /** Serialisierungstyp von {@code ClockWindows}. */
    static final int CLOCK_WINDOW_ID = 41;

    /** Serialisierungstyp von {@code TimestampWindows}. */
    static final int TIMESTAMP_WINDOW_ID = 42;

    /** Serialisierungstyp von {@code MomentWindows}. */
    static final int MOMENT_WINDOW_ID = 43;

    /** Serialisierungstyp von {@code Boundary}. */
    static final int BOUNDARY_TYPE = 57;

    // for backwards compatibility pre v2.2
    private static final int OLD_DATE_TYPE = 50;
    private static final int OLD_TIME_TYPE = 51;
    private static final int OLD_TIMESTAMP_TYPE = 52;
    private static final int OLD_MOMENT_TYPE = 53;
    private static final int OLD_DATE_WINDOW_ID = 60;
    private static final int OLD_CLOCK_WINDOW_ID = 61;
    private static final int OLD_TIMESTAMP_WINDOW_ID = 62;
    private static final int OLD_MOMENT_WINDOW_ID = 63;

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
     * @serialData  data layout see {@code writeReplace()}-method of object
     *              to be serialized
     * @param       out     output stream
     * @throws      IOException
     */
    /*[deutsch]
     * <p>Implementierungsmethode des Interface {@link Externalizable}. </p>
     *
     * <p>Das erste Byte enth&auml;lt um 2 Bits nach links verschoben den
     * Typ des zu serialisierenden Objekts. Danach folgen die Daten-Bits
     * in einer bit-komprimierten Darstellung. </p>
     *
     * @serialData  data layout see {@code writeReplace()}-method of object
     *              to be serialized
     * @param       out     output stream
     * @throws      IOException
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

//                case OLD_DATE_TYPE:
//                case OLD_TIME_TYPE:
//                case OLD_TIMESTAMP_TYPE:
//                case OLD_MOMENT_TYPE:
//                    ChronoInterval<?> ci = (ChronoInterval<?>) this.obj;
//                    out.writeObject(ci.getStart());
//                    out.writeObject(ci.getEnd());
//                    break;
//                case OLD_DATE_WINDOW_ID:
//                case OLD_CLOCK_WINDOW_ID:
//                case OLD_TIMESTAMP_WINDOW_ID:
//                case OLD_MOMENT_WINDOW_ID:
//                    IntervalCollection<?> ic =
//                        IntervalCollection.class.cast(this.obj);
//                    out.writeObject(ic.getIntervals());
//                    break;

                default:
                    throw new InvalidClassException("Unknown serialized type.");
            }
        }

    }

    /**
     * <p>Implementation method of interface {@link Externalizable}. </p>
     *
     * @param   in      input stream
     * @throws  IOException
     * @throws  ClassNotFoundException
     */
    /*[deutsch]
     * <p>Implementierungsmethode des Interface {@link Externalizable}. </p>
     *
     * @param   in      input stream
     * @throws  IOException
     * @throws  ClassNotFoundException
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
            case DATE_WINDOW_ID:
                this.obj = readDateWindow2(in);
                break;
            case CLOCK_WINDOW_ID:
                this.obj = readClockWindow2(in);
                break;
            case TIMESTAMP_WINDOW_ID:
                this.obj = readTimestampWindow2(in);
                break;
            case MOMENT_WINDOW_ID:
                this.obj = readMomentWindow2(in);
                break;
            case BOUNDARY_TYPE:
                this.obj = readBoundary(in, header);
                break;

            // for backward compatibility
            case OLD_DATE_TYPE:
                this.obj = readDateInterval(in, true);
                break;
            case OLD_TIME_TYPE:
                this.obj = readClockInterval(in, true);
                break;
            case OLD_TIMESTAMP_TYPE:
                this.obj = readTimestampInterval(in, true);
                break;
            case OLD_MOMENT_TYPE:
                this.obj = readMomentInterval(in, true);
                break;
            case OLD_DATE_WINDOW_ID:
                this.obj = readDateWindow(in);
                break;
            case OLD_CLOCK_WINDOW_ID:
                this.obj = readClockWindow(in);
                break;
            case OLD_TIMESTAMP_WINDOW_ID:
                this.obj = readTimestampWindow(in);
                break;
            case OLD_MOMENT_WINDOW_ID:
                this.obj = readMomentWindow(in);
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

        return readDateInterval(in, false);

    }

    private static DateInterval readDateInterval(
        ObjectInput in,
        boolean pre22
    ) throws IOException, ClassNotFoundException {

        Object o1 = (pre22 ? in.readObject() : readBoundary(in));
        Object o2 = (pre22 ? in.readObject() : readBoundary(in));

        Boundary<PlainDate> start = getBoundary(o1, PlainDate.class, false);
        Boundary<PlainDate> end = getBoundary(o2, PlainDate.class, true);

        if ((start != null) && (end != null)) {
            return new DateInterval(start, end);
        }

        throw new StreamCorruptedException();

    }

    private static ClockInterval readClockInterval(ObjectInput in)
        throws IOException, ClassNotFoundException {

        return readClockInterval(in, false);

    }

    private static ClockInterval readClockInterval(
        ObjectInput in,
        boolean pre22
    ) throws IOException, ClassNotFoundException {

        Object o1 = (pre22 ? in.readObject() : readBoundary(in));
        Object o2 = (pre22 ? in.readObject() : readBoundary(in));

        Boundary<PlainTime> start = getBoundary(o1, PlainTime.class, false);
        Boundary<PlainTime> end = getBoundary(o2, PlainTime.class, true);

        if ((start != null) && (end != null)) {
            return new ClockInterval(start, end);
        }

        throw new StreamCorruptedException();

    }

    private static TimestampInterval readTimestampInterval(ObjectInput in)
        throws IOException, ClassNotFoundException {

        return readTimestampInterval(in, false);

    }

    private static TimestampInterval readTimestampInterval(
        ObjectInput in,
        boolean pre22
    ) throws IOException, ClassNotFoundException {

        Object o1 = (pre22 ? in.readObject() : readBoundary(in));
        Object o2 = (pre22 ? in.readObject() : readBoundary(in));

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

        return readMomentInterval(in, false);

    }

    private static MomentInterval readMomentInterval(
        ObjectInput in,
        boolean pre22
    ) throws IOException, ClassNotFoundException {

        Object o1 = (pre22 ? in.readObject() : readBoundary(in));
        Object o2 = (pre22 ? in.readObject() : readBoundary(in));

        Boundary<Moment> start = getBoundary(o1, Moment.class, false);
        Boundary<Moment> end = getBoundary(o2, Moment.class, true);

        if ((start != null) && (end != null)) {
            return new MomentInterval(start, end);
        }

        throw new StreamCorruptedException();

    }

    private static IntervalCollection<PlainDate> readDateWindow2(
        ObjectInput in
    ) throws IOException, ClassNotFoundException {

        int size = in.readInt();

        if (size == 0) {
            return DateWindows.EMPTY;
        }

        List<ChronoInterval<PlainDate>> intervals =
            new ArrayList<ChronoInterval<PlainDate>>(size);

        for (int i = 0; i < size; i++) {
            intervals.add(readDateInterval(in));
        }

        return DateWindows.EMPTY.plus(intervals);

    }

    private static Object readDateWindow(ObjectInput in)
        throws IOException, ClassNotFoundException {

        List<ChronoInterval<PlainDate>> intervals = cast(in.readObject());
        return DateWindows.EMPTY.plus(intervals);

    }

    private static IntervalCollection<PlainTime> readClockWindow2(
        ObjectInput in
    ) throws IOException, ClassNotFoundException {

        int size = in.readInt();

        if (size == 0) {
            return ClockWindows.EMPTY;
        }

        List<ChronoInterval<PlainTime>> intervals =
            new ArrayList<ChronoInterval<PlainTime>>(size);

        for (int i = 0; i < size; i++) {
            intervals.add(readClockInterval(in));
        }

        return ClockWindows.EMPTY.plus(intervals);

    }

    private static Object readClockWindow(ObjectInput in)
        throws IOException, ClassNotFoundException {

        List<ChronoInterval<PlainTime>> intervals = cast(in.readObject());
        return ClockWindows.EMPTY.plus(intervals);

    }

    private static IntervalCollection<PlainTimestamp> readTimestampWindow2(
        ObjectInput in
    ) throws IOException, ClassNotFoundException {

        int size = in.readInt();

        if (size == 0) {
            return TimestampWindows.EMPTY;
        }

        List<ChronoInterval<PlainTimestamp>> intervals =
            new ArrayList<ChronoInterval<PlainTimestamp>>(size);

        for (int i = 0; i < size; i++) {
            intervals.add(readTimestampInterval(in));
        }

        return TimestampWindows.EMPTY.plus(intervals);

    }

    private static Object readTimestampWindow(ObjectInput in)
        throws IOException, ClassNotFoundException {

        List<ChronoInterval<PlainTimestamp>> intervals = cast(in.readObject());
        return TimestampWindows.EMPTY.plus(intervals);

    }

    private static IntervalCollection<Moment> readMomentWindow2(ObjectInput in)
        throws IOException, ClassNotFoundException {

        int size = in.readInt();

        if (size == 0) {
            return MomentWindows.EMPTY;
        }

        List<ChronoInterval<Moment>> intervals =
            new ArrayList<ChronoInterval<Moment>>(size);

        for (int i = 0; i < size; i++) {
            intervals.add(readMomentInterval(in));
        }

        return MomentWindows.EMPTY.plus(intervals);

    }

    private static Object readMomentWindow(ObjectInput in)
        throws IOException, ClassNotFoundException {

        List<ChronoInterval<Moment>> intervals = cast(in.readObject());
        return MomentWindows.EMPTY.plus(intervals);

    }

    // serialization of a single boundary object
    @SuppressWarnings("unchecked")
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
        return Boundary.of(edge, Temporal.class.cast(t));

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

    @SuppressWarnings("unchecked")
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
        return Boundary.of(edge, Temporal.class.cast(t));

    }

    private static <T extends Temporal<? super T>> Boundary<T> getBoundary(
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
