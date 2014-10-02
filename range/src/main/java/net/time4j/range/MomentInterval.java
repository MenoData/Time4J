/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (MomentInterval.java) is part of project Time4J.
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
import net.time4j.PlainTimestamp;
import net.time4j.engine.TimeLine;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;

import static net.time4j.range.IntervalEdge.CLOSED;
import static net.time4j.range.IntervalEdge.OPEN;


/**
 * <p>Defines a moment interval on global timeline. </p>
 *
 * @author  Meno Hochschild
 * @since   1.3
 */
/*[deutsch]
 * <p>Definiert ein Momentintervall auf dem globalen Zeitstrahl. </p>
 *
 * @author  Meno Hochschild
 * @since   1.3
 */
public final class MomentInterval
    extends ChronoInterval<Moment>
    implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = 1L;

    //~ Konstruktoren -----------------------------------------------------

    // package-private
    MomentInterval(
        Boundary<Moment> start,
        Boundary<Moment> end
    ) {
        super(start, end);

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a finite half-open interval between given time points. </p>
     *
     * @param   start   moment of lower boundary (inclusive)
     * @param   end     moment of upper boundary (exclusive)
     * @return  new moment interval
     * @throws  IllegalArgumentException if start is after end
     * @since   1.3
     */
    /*[deutsch]
     * <p>Erzeugt ein begrenztes halb-offenes Intervall zwischen den
     * angegebenen Zeitpunkten. </p>
     *
     * @param   start   moment of lower boundary (inclusive)
     * @param   end     moment of upper boundary (exclusive)
     * @return  new moment interval
     * @throws  IllegalArgumentException if start is after end
     * @since   1.3
     */
    public static MomentInterval between(
        Moment start,
        Moment end
    ) {

        return new MomentInterval(
            Boundary.of(CLOSED, start),
            Boundary.of(OPEN, end));

    }

    /**
     * <p>Creates an infinite half-open interval since given start
     * timestamp. </p>
     *
     * @param   start       moment of lower boundary (inclusive)
     * @return  new moment interval
     * @since   1.3
     */
    /*[deutsch]
     * <p>Erzeugt ein unbegrenztes halb-offenes Intervall ab dem angegebenen
     * Startzeitpunkt. </p>
     *
     * @param   start       moment of lower boundary (inclusive)
     * @return  new moment interval
     * @since   1.3
     */
    public static MomentInterval since(Moment start) {

        Boundary<Moment> future = Boundary.infiniteFuture();
        return new MomentInterval(Boundary.of(CLOSED, start), future);

    }

    /**
     * <p>Creates an infinite open interval until given end timestamp. </p>
     *
     * @param   end     moment of upper boundary (exclusive)
     * @return  new moment interval
     * @since   1.3
     */
    /*[deutsch]
     * <p>Erzeugt ein unbegrenztes offenes Intervall bis zum angegebenen
     * Endzeitpunkt. </p>
     *
     * @param   end     moment of upper boundary (exclusive)
     * @return  new moment interval
     * @since   1.3
     */
    public static MomentInterval until(Moment end) {

        Boundary<Moment> past = Boundary.infinitePast();
        return new MomentInterval(past, Boundary.of(OPEN, end));

    }

    @Override
    public MomentInterval withStart(Boundary<Moment> boundary) {

        return new MomentInterval(boundary, this.getEnd());

    }

    @Override
    public MomentInterval withEnd(Boundary<Moment> boundary) {

        return new MomentInterval(this.getStart(), boundary);

    }

    @Override
    public MomentInterval withStart(Moment temporal) {

        Boundary<Moment> boundary =
            Boundary.of(this.getStart().getEdge(), temporal);
        return new MomentInterval(boundary, this.getEnd());

    }

    @Override
    public MomentInterval withEnd(Moment temporal) {

        Boundary<Moment> boundary =
            Boundary.of(this.getEnd().getEdge(), temporal);
        return new MomentInterval(this.getStart(), boundary);

    }

    /**
     * <p>Converts this instance to a local timestamp interval in the system
     * timezone. </p>
     *
     * @return  local timestamp interval in system timezone (leap seconds will
     *          always be lost)
     * @since   1.3
     * @see     Timezone#ofSystem()
     * @see     #toZonalInterval(TZID)
     * @see     #toZonalInterval(String)
     */
    /*[deutsch]
     * <p>Wandelt diese Instanz in ein lokales Zeitstempelintervall um. </p>
     *
     * @return  local timestamp interval in system timezone (leap seconds will
     *          always be lost)
     * @since   1.3
     * @see     Timezone#ofSystem()
     * @see     #toZonalInterval(TZID)
     * @see     #toZonalInterval(String)
     */
    public TimestampInterval toLocalInterval() {

        Boundary<PlainTimestamp> b1;
        Boundary<PlainTimestamp> b2;

        if (this.getStart().isInfinite()) {
            b1 = Boundary.infinitePast();
        } else {
            PlainTimestamp t1 =
                this.getStart().getTemporal().toLocalTimestamp();
            b1 = Boundary.of(this.getStart().getEdge(), t1);
        }

        if (this.getEnd().isInfinite()) {
            b2 = Boundary.infiniteFuture();
        } else {
            PlainTimestamp t2 = this.getEnd().getTemporal().toLocalTimestamp();
            b2 = Boundary.of(this.getEnd().getEdge(), t2);
        }

        return new TimestampInterval(b1, b2);

    }

    /**
     * <p>Converts this instance to a zonal timestamp interval
     * in given timezone. </p>
     *
     * @param   tzid    timezone id
     * @return  zonal timestamp interval in given timezone (leap seconds will
     *          always be lost)
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   1.3
     * @see     #toLocalInterval()
     */
    /*[deutsch]
     * <p>Wandelt diese Instanz in ein zonales Zeitstempelintervall um. </p>
     *
     * @param   tzid    timezone id
     * @return  zonal timestamp interval in given timezone (leap seconds will
     *          always be lost)
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   1.3
     * @see     #toLocalInterval()
     */
    public TimestampInterval toZonalInterval(TZID tzid) {

        Boundary<PlainTimestamp> b1;
        Boundary<PlainTimestamp> b2;

        if (this.getStart().isInfinite()) {
            b1 = Boundary.infinitePast();
        } else {
            PlainTimestamp t1 =
                this.getStart().getTemporal().toZonalTimestamp(tzid);
            b1 = Boundary.of(this.getStart().getEdge(), t1);
        }

        if (this.getEnd().isInfinite()) {
            b2 = Boundary.infiniteFuture();
        } else {
            PlainTimestamp t2 =
                this.getEnd().getTemporal().toZonalTimestamp(tzid);
            b2 = Boundary.of(this.getEnd().getEdge(), t2);
        }

        return new TimestampInterval(b1, b2);

    }

    /**
     * <p>Converts this instance to a zonal timestamp interval
     * in given timezone. </p>
     *
     * @param   tzid    timezone id
     * @return  zonal timestamp interval in given timezone (leap seconds will
     *          always be lost)
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   1.3
     * @see     #toZonalInterval(TZID)
     * @see     #toLocalInterval()
     */
    /*[deutsch]
     * <p>Wandelt diese Instanz in ein zonales Zeitstempelintervall um. </p>
     *
     * @param   tzid    timezone id
     * @return  zonal timestamp interval in given timezone (leap seconds will
     *          always be lost)
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   1.3
     * @see     #toZonalInterval(TZID)
     * @see     #toLocalInterval()
     */
    public TimestampInterval toZonalInterval(String tzid) {

        Boundary<PlainTimestamp> b1;
        Boundary<PlainTimestamp> b2;

        if (this.getStart().isInfinite()) {
            b1 = Boundary.infinitePast();
        } else {
            PlainTimestamp t1 =
                this.getStart().getTemporal().toZonalTimestamp(tzid);
            b1 = Boundary.of(this.getStart().getEdge(), t1);
        }

        if (this.getEnd().isInfinite()) {
            b2 = Boundary.infiniteFuture();
        } else {
            PlainTimestamp t2 =
                this.getEnd().getTemporal().toZonalTimestamp(tzid);
            b2 = Boundary.of(this.getEnd().getEdge(), t2);
        }

        return new TimestampInterval(b1, b2);

    }

    @Override
    protected TimeLine<Moment> getTimeLine() {

        return Moment.axis();

    }

    /**
     * @serialData  Uses
     *              <a href="../../../serialized-form.html#net.time4j.range.SPX">
     *              a dedicated serialization form</a> as proxy. The first byte
     *              contains the type-ID {@code 53} in the six most significant
     *              bits. The next bytes represent the start and the end
     *              boundary.
     *
     * Schematic algorithm:
     *
     * <pre>
     *  int header = 53;
     *  header <<= 2;
     *  out.writeByte(header);
     *  out.writeObject(getStart());
     *  out.writeObject(getEnd());
     * </pre>
     */
    private Object writeReplace() throws ObjectStreamException {

        return new SPX(this, SPX.MOMENT_TYPE);

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
