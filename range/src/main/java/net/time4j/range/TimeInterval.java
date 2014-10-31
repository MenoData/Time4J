/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimeInterval.java) is part of project Time4J.
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

import net.time4j.ClockUnit;
import net.time4j.Duration;
import net.time4j.PlainTime;
import net.time4j.engine.TimeLine;
import net.time4j.format.ChronoFormatter;
import net.time4j.format.ParseLog;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.text.ParseException;

import static net.time4j.ClockUnit.HOURS;
import static net.time4j.ClockUnit.NANOS;
import static net.time4j.range.IntervalEdge.CLOSED;
import static net.time4j.range.IntervalEdge.OPEN;


/**
 * <p>Defines a finite wall time interval on the local timeline. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 */
/*[deutsch]
 * <p>Definiert ein endliches Uhrzeitintervall auf dem lokalen Zeitstrahl. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 */
public final class TimeInterval
    extends IsoInterval<PlainTime>
    implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = 1L;

    //~ Konstruktoren -----------------------------------------------------

    // package-private
    TimeInterval(
        Boundary<PlainTime> start,
        Boundary<PlainTime> end
    ) {
        super(start, end);

        if (start.isInfinite() || end.isInfinite()) {
            throw new IllegalArgumentException(
                "Time intervals must be finite.");
        }

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a finite half-open interval between given wall times. </p>
     *
     * @param   start   time of lower boundary (inclusive)
     * @param   end     time of upper boundary (exclusive)
     * @return  new moment interval
     * @throws  IllegalArgumentException if start is after end
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt ein begrenztes halb-offenes Intervall zwischen den
     * angegebenen Uhrzeiten. </p>
     *
     * @param   start   time of lower boundary (inclusive)
     * @param   end     time of upper boundary (exclusive)
     * @return  new moment interval
     * @throws  IllegalArgumentException if start is after end
     * @since   2.0
     */
    public static TimeInterval between(
        PlainTime start,
        PlainTime end
    ) {

        return new TimeInterval(
            Boundary.of(CLOSED, start),
            Boundary.of(OPEN, end));

    }

    /**
     * <p>Creates a finite half-open interval between given start time and
     * midnight at end of day (exclusive). </p>
     *
     * <p>Note: The special wall time 24:00 does not belong to the created
     * interval. </p>
     *
     * @param   start       time of lower boundary (inclusive)
     * @return  new time interval
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt ein begrenztes halb-offenes Intervall zwischen der
     * angegebenen Startzeit und Mitternacht zu Ende des Tages (exklusive). </p>
     *
     * <p>Zu beachten: Die spezielle Uhrzeit 24:00 geh&ouml;rt nicht zum
     * erzeugten Intervall. </p>
     *
     * @param   start       time of lower boundary (inclusive)
     * @return  new time interval
     * @since   2.0
     */
    public static TimeInterval since(PlainTime start) {

        return between(start, PlainTime.midnightAtEndOfDay());

    }

    /**
     * <p>Creates a finite half-open interval between midnight at start of day
     * and given end time. </p>
     *
     * @param   end     time of upper boundary (exclusive)
     * @return  new time interval
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt ein begrenztes halb-offenes Intervall zwischen Mitternacht
     * zu Beginn des Tages und der angegebenen Endzeit. </p>
     *
     * @param   end     time of upper boundary (exclusive)
     * @return  new time interval
     * @since   2.0
     */
    public static TimeInterval until(PlainTime end) {

        return between(PlainTime.midnightAtStartOfDay(), end);

    }

    @Override
    public TimeInterval withStart(Boundary<PlainTime> boundary) {

        return new TimeInterval(boundary, this.getEnd());

    }

    @Override
    public TimeInterval withEnd(Boundary<PlainTime> boundary) {

        return new TimeInterval(this.getStart(), boundary);

    }

    @Override
    public TimeInterval withStart(PlainTime temporal) {

        Boundary<PlainTime> boundary =
            Boundary.of(this.getStart().getEdge(), temporal);
        return new TimeInterval(boundary, this.getEnd());

    }

    @Override
    public TimeInterval withEnd(PlainTime temporal) {

        Boundary<PlainTime> boundary =
            Boundary.of(this.getEnd().getEdge(), temporal);
        return new TimeInterval(this.getStart(), boundary);

    }

    /**
     * <p>Yields the length of this interval. </p>
     *
     * @return  duration in hours, minutes, seconds and nanoseconds
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge dieses Intervalls. </p>
     *
     * @return  duration in hours, minutes, seconds and nanoseconds
     * @since   2.0
     */
    public Duration<ClockUnit> getDuration() {

        PlainTime t1 = this.getStart().getTemporal();
        PlainTime t2 = this.getEnd().getTemporal();

        if (
            (t2.getHour() == 24)
            && this.getEnd().isClosed()
        ) {
            if (this.getStart().isClosed()) {
                if (t1.equals(PlainTime.midnightAtStartOfDay())) {
                    return Duration.of(24, HOURS).plus(1, NANOS);
                } else {
                    t1 = t1.minus(1, NANOS);
                }
            }
        } else if (this.getStart().isOpen()) {
            t1 = t1.plus(1, NANOS);
        } else if (this.getEnd().isClosed()) {
            t2 = t2.plus(1, NANOS);
        }

        return Duration.inClockUnits().between(t1, t2);

    }

    /**
     * <p>Interpretes given text as interval. </p>
     *
     * @param   text        text to be parsed
     * @param   formatter   format object for parsing start and end boundaries
     * @return  parsed interval
     * @throws  IndexOutOfBoundsException if the start position is at end of
     *          text or even behind
     * @throws  ParseException if the text is not parseable
     * @since   2.0
     * @see     BracketPolicy#SHOW_WHEN_NON_STANDARD
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text als Intervall. </p>
     *
     * @param   text        text to be parsed
     * @param   formatter   format object for parsing start and end boundaries
     * @return  parsed interval
     * @throws  IndexOutOfBoundsException if the start position is at end of
     *          text or even behind
     * @throws  ParseException if the text is not parseable
     * @since   2.0
     * @see     BracketPolicy#SHOW_WHEN_NON_STANDARD
     */
    public static TimeInterval parse(
        String text,
        ChronoFormatter<PlainTime> formatter
    ) throws ParseException {

        return IntervalParser.of(
             TimeIntervalFactory.INSTANCE,
             formatter,
             BracketPolicy.SHOW_WHEN_NON_STANDARD
        ).parse(text);

    }

    /**
     * <p>Interpretes given text as interval. </p>
     *
     * @param   text        text to be parsed
     * @param   formatter   format object for parsing start and end boundaries
     * @param   policy      strategy for parsing interval boundaries
     * @param   status      parser information (always as new instance)
     * @return  result or {@code null} if parsing does not work
     * @throws  IndexOutOfBoundsException if the start position is at end of
     *          text or even behind
     * @since   2.0
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text als Intervall. </p>
     *
     * @param   text        text to be parsed
     * @param   formatter   format object for parsing start and end boundaries
     * @param   policy      strategy for parsing interval boundaries
     * @param   status      parser information (always as new instance)
     * @return  result or {@code null} if parsing does not work
     * @throws  IndexOutOfBoundsException if the start position is at end of
     *          text or even behind
     * @since   2.0
     */
    public static TimeInterval parse(
        CharSequence text,
        ChronoFormatter<PlainTime> formatter,
        BracketPolicy policy,
        ParseLog status
    ) {

        return IntervalParser.of(
             TimeIntervalFactory.INSTANCE,
             formatter,
             policy
        ).parse(text, status, formatter.getDefaultAttributes());

    }

    @Override
    protected TimeLine<PlainTime> getTimeLine() {

        return PlainTime.axis();

    }

    /**
     * @serialData  Uses
     *              <a href="../../../serialized-form.html#net.time4j.range.SPX">
     *              a dedicated serialization form</a> as proxy. The first byte
     *              contains the type-ID {@code 51} in the six most significant
     *              bits. The next bytes represent the start and the end
     *              boundary.
     *
     * Schematic algorithm:
     *
     * <pre>
     *  int header = 51;
     *  header <<= 2;
     *  out.writeByte(header);
     *  out.writeObject(getStart());
     *  out.writeObject(getEnd());
     * </pre>
     */
    private Object writeReplace() throws ObjectStreamException {

        return new SPX(this, SPX.TIME_TYPE);

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
