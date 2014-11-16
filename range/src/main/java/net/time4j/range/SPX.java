/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
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
    static final int DATE_TYPE = 50;

    /** Serialisierungstyp von {@code ClockInterval}. */
    static final int TIME_TYPE = 51;

    /** Serialisierungstyp von {@code TimestampInterval}. */
    static final int TIMESTAMP_TYPE = 52;

    /** Serialisierungstyp von {@code MomentInterval}. */
    static final int MOMENT_TYPE = 53;

    /** Serialisierungstyp von {@code Boundary}. */
    static final int BOUNDARY_TYPE = 57;

    /** Serialisierungstyp von {@code DateWindows}. */
    static final int DATE_WINDOW_ID = 60;

    /** Serialisierungstyp von {@code ClockWindows}. */
    static final int CLOCK_WINDOW_ID = 61;

    /** Serialisierungstyp von {@code TimestampWindows}. */
    static final int TIMESTAMP_WINDOW_ID = 62;

    /** Serialisierungstyp von {@code MomentWindows}. */
    static final int MOMENT_WINDOW_ID = 63;

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
     * <p>Implementierungsmethode des Interface {@link Externalizable}. </p>
     *
     * <p>Das erste Byte enth&auml;lt um 4 Bits nach links verschoben den
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

        switch (this.type) {
            case DATE_TYPE:
                this.writeDateInterval(out);
                break;
            case TIME_TYPE:
                this.writeClockInterval(out);
                break;
            case TIMESTAMP_TYPE:
                this.writeTimestampInterval(out);
                break;
            case MOMENT_TYPE:
                this.writeMomentInterval(out);
                break;
            case BOUNDARY_TYPE:
                this.writeBoundary(out);
                break;
            case DATE_WINDOW_ID:
            case CLOCK_WINDOW_ID:
            case TIMESTAMP_WINDOW_ID:
            case MOMENT_WINDOW_ID:
                int header = (this.type << 2);
                out.writeByte(header);
                IntervalCollection<?> window =
                    IntervalCollection.class.cast(this.obj);
                out.writeObject(window.getIntervals());
                break;
            default:
                throw new InvalidClassException("Unknown serialized type.");
        }

    }

    /**
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

        switch (header >> 2) {
            case DATE_TYPE:
                this.obj = this.readDateInterval(in);
                break;
            case TIME_TYPE:
                this.obj = this.readClockInterval(in);
                break;
            case TIMESTAMP_TYPE:
                this.obj = this.readTimestampInterval(in);
                break;
            case MOMENT_TYPE:
                this.obj = this.readMomentInterval(in);
                break;
            case BOUNDARY_TYPE:
                this.readBoundary(in, header);
                break;
            case DATE_WINDOW_ID:
                this.obj = this.readDateWindow(in);
                break;
            case CLOCK_WINDOW_ID:
                this.obj = this.readClockWindow(in);
                break;
            case TIMESTAMP_WINDOW_ID:
                this.obj = this.readTimestampWindow(in);
                break;
            case MOMENT_WINDOW_ID:
                this.obj = this.readMomentWindow(in);
                break;
            default:
                throw new StreamCorruptedException("Unknown serialized type.");
        }

    }

    private Object readResolve() throws ObjectStreamException {

        return this.obj;

    }

    private void writeDateInterval(ObjectOutput out)
        throws IOException {

        DateInterval interval = (DateInterval) this.obj;
        out.writeByte(DATE_TYPE << 2);
        out.writeObject(interval.getStart());
        out.writeObject(interval.getEnd());

    }

    private Object readDateInterval(ObjectInput in)
        throws IOException, ClassNotFoundException {

        Object o1 = in.readObject();
        Object o2 = in.readObject();

        Boundary<PlainDate> start = getBoundary(o1, PlainDate.class, false);
        Boundary<PlainDate> end = getBoundary(o2, PlainDate.class, true);

        if ((start != null) && (end != null)) {
            return new DateInterval(start, end);
        }

        throw new StreamCorruptedException();

    }

    private void writeClockInterval(ObjectOutput out)
        throws IOException {

        ClockInterval interval = (ClockInterval) this.obj;
        out.writeByte(TIME_TYPE << 2);
        out.writeObject(interval.getStart());
        out.writeObject(interval.getEnd());

    }

    private Object readClockInterval(ObjectInput in)
        throws IOException, ClassNotFoundException {

        Object o1 = in.readObject();
        Object o2 = in.readObject();

        Boundary<PlainTime> start = getBoundary(o1, PlainTime.class, false);
        Boundary<PlainTime> end = getBoundary(o2, PlainTime.class, true);

        if ((start != null) && (end != null)) {
            return new ClockInterval(start, end);
        }

        throw new StreamCorruptedException();

    }

    private void writeTimestampInterval(ObjectOutput out)
        throws IOException {

        TimestampInterval interval = (TimestampInterval) this.obj;
        out.writeByte(TIMESTAMP_TYPE << 2);
        out.writeObject(interval.getStart());
        out.writeObject(interval.getEnd());

    }

    private Object readTimestampInterval(ObjectInput in)
        throws IOException, ClassNotFoundException {

        Object o1 = in.readObject();
        Object o2 = in.readObject();

        Boundary<PlainTimestamp> start =
            getBoundary(o1, PlainTimestamp.class, false);
        Boundary<PlainTimestamp> end =
            getBoundary(o2, PlainTimestamp.class, true);

        if ((start != null) && (end != null)) {
            return new TimestampInterval(start, end);
        }

        throw new StreamCorruptedException();

    }

    private void writeMomentInterval(ObjectOutput out)
        throws IOException {

        MomentInterval interval = (MomentInterval) this.obj;
        out.writeByte(MOMENT_TYPE << 2);
        out.writeObject(interval.getStart());
        out.writeObject(interval.getEnd());

    }

    private Object readMomentInterval(ObjectInput in)
        throws IOException, ClassNotFoundException {

        Object o1 = in.readObject();
        Object o2 = in.readObject();

        Boundary<Moment> start = getBoundary(o1, Moment.class, false);
        Boundary<Moment> end = getBoundary(o2, Moment.class, true);

        if ((start != null) && (end != null)) {
            return new MomentInterval(start, end);
        }

        throw new StreamCorruptedException();

    }

    private Object readDateWindow(ObjectInput in)
        throws IOException, ClassNotFoundException {

        List<ChronoInterval<PlainDate>> intervals = cast(in.readObject());
        return DateWindows.EMPTY.plus(intervals);

    }

    private Object readClockWindow(ObjectInput in)
        throws IOException, ClassNotFoundException {

        List<ChronoInterval<PlainTime>> intervals = cast(in.readObject());
        return ClockWindows.EMPTY.plus(intervals);

    }

    private Object readTimestampWindow(ObjectInput in)
        throws IOException, ClassNotFoundException {

        List<ChronoInterval<PlainTimestamp>> intervals = cast(in.readObject());
        return TimestampWindows.EMPTY.plus(intervals);

    }

    private Object readMomentWindow(ObjectInput in)
        throws IOException, ClassNotFoundException {

        List<ChronoInterval<Moment>> intervals = cast(in.readObject());
        return MomentWindows.EMPTY.plus(intervals);

    }

    private void writeBoundary(ObjectOutput out)
        throws IOException {

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

    }

    @SuppressWarnings("unchecked")
    private Object readBoundary(
        ObjectInput in,
        byte header
    ) throws IOException, ClassNotFoundException {

        int past = (header & 0x1);

        if (past == 1) {
            return Boundary.infinitePast();
        }

        int future = (header & 0x2);

        if (future == 1) {
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
