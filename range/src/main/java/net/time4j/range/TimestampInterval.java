/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimestampInterval.java) is part of project Time4J.
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

import net.time4j.Duration;
import net.time4j.IsoUnit;
import net.time4j.Moment;
import net.time4j.PlainTimestamp;
import net.time4j.engine.TimeLine;
import net.time4j.format.ChronoFormatter;
import net.time4j.format.ParseLog;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.text.ParseException;

import static net.time4j.range.IntervalEdge.CLOSED;
import static net.time4j.range.IntervalEdge.OPEN;


/**
 * <p>Defines a timestamp interval on local timeline. </p>
 *
 * @author  Meno Hochschild
 * @since   1.3
 */
/*[deutsch]
 * <p>Definiert ein Zeitstempelintervall auf dem lokalen Zeitstrahl. </p>
 *
 * @author  Meno Hochschild
 * @since   1.3
 */
public final class TimestampInterval
    extends ChronoInterval<PlainTimestamp>
    implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = 1L;

    //~ Konstruktoren -----------------------------------------------------

    // package-private
    TimestampInterval(
        Boundary<PlainTimestamp> start,
        Boundary<PlainTimestamp> end
    ) {
        super(start, end);

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a finite half-open interval between given time points. </p>
     *
     * @param   start   timestamp of lower boundary (inclusive)
     * @param   end     timestamp of upper boundary (exclusive)
     * @return  new moment interval
     * @throws  IllegalArgumentException if start is after end
     * @since   1.3
     */
    /*[deutsch]
     * <p>Erzeugt ein begrenztes halb-offenes Intervall zwischen den
     * angegebenen Zeitpunkten. </p>
     *
     * @param   start   timestamp of lower boundary (inclusive)
     * @param   end     timestamp of upper boundary (exclusive)
     * @return  new moment interval
     * @throws  IllegalArgumentException if start is after end
     * @since   1.3
     */
    public static TimestampInterval between(
        PlainTimestamp start,
        PlainTimestamp end
    ) {

        return new TimestampInterval(
            Boundary.of(CLOSED, start),
            Boundary.of(OPEN, end));

    }

    /**
     * <p>Creates an infinite half-open interval since given start
     * timestamp. </p>
     *
     * @param   start       timestamp of lower boundary (inclusive)
     * @return  new timestamp interval
     * @since   1.3
     */
    /*[deutsch]
     * <p>Erzeugt ein unbegrenztes halb-offenes Intervall ab dem angegebenen
     * Startzeitpunkt. </p>
     *
     * @param   start       timestamp of lower boundary (inclusive)
     * @return  new timestamp interval
     * @since   1.3
     */
    public static TimestampInterval since(PlainTimestamp start) {

        Boundary<PlainTimestamp> future = Boundary.infiniteFuture();
        return new TimestampInterval(Boundary.of(CLOSED, start), future);

    }

    /**
     * <p>Creates an infinite open interval until given end timestamp. </p>
     *
     * @param   end     timestamp of upper boundary (exclusive)
     * @return  new timestamp interval
     * @since   1.3
     */
    /*[deutsch]
     * <p>Erzeugt ein unbegrenztes offenes Intervall bis zum angegebenen
     * Endzeitpunkt. </p>
     *
     * @param   end     timestamp of upper boundary (exclusive)
     * @return  new timestamp interval
     * @since   1.3
     */
    public static TimestampInterval until(PlainTimestamp end) {

        Boundary<PlainTimestamp> past = Boundary.infinitePast();
        return new TimestampInterval(past, Boundary.of(OPEN, end));

    }

    @Override
    public TimestampInterval withStart(Boundary<PlainTimestamp> boundary) {

        return new TimestampInterval(boundary, this.getEnd());

    }

    @Override
    public TimestampInterval withEnd(Boundary<PlainTimestamp> boundary) {

        return new TimestampInterval(this.getStart(), boundary);

    }

    @Override
    public TimestampInterval withStart(PlainTimestamp temporal) {

        Boundary<PlainTimestamp> boundary =
            Boundary.of(this.getStart().getEdge(), temporal);
        return new TimestampInterval(boundary, this.getEnd());

    }

    @Override
    public TimestampInterval withEnd(PlainTimestamp temporal) {

        Boundary<PlainTimestamp> boundary =
            Boundary.of(this.getEnd().getEdge(), temporal);
        return new TimestampInterval(this.getStart(), boundary);

    }

    /**
     * <p>Combines this local timestamp interval with the timezone offset
     * UTC+00:00 to a global UTC-interval. </p>
     *
     * @return  global timestamp interval interpreted at offset UTC+00:00
     * @see     #at(ZonalOffset)
     */
    /*[deutsch]
     * <p>Kombiniert dieses lokale Zeitstempelintervall mit UTC+00:00 zu
     * einem globalen UTC-Intervall. </p>
     *
     * @return  global timestamp interval interpreted at offset UTC+00:00
     * @see     #at(ZonalOffset)
     */
    public MomentInterval atUTC() {

        return this.at(ZonalOffset.UTC);

    }

    /**
     * <p>Combines this local timestamp interval with given timezone offset
     * to a global UTC-interval. </p>
     *
     * @param   offset  timezone offset
     * @return  global timestamp interval interpreted at given offset
     * @since   1.3
     * @see     #atUTC()
     * @see     #in(Timezone)
     */
    /*[deutsch]
     * <p>Kombiniert dieses lokale Zeitstempelintervall mit dem angegebenen
     * Zeitzonen-Offset zu einem globalen UTC-Intervall. </p>
     *
     * @param   offset  timezone offset
     * @return  global timestamp interval interpreted at given offset
     * @since   1.3
     * @see     #atUTC()
     * @see     #in(Timezone)
     */
    public MomentInterval at(ZonalOffset offset) {

        Boundary<Moment> b1;
        Boundary<Moment> b2;

        if (this.getStart().isInfinite()) {
            b1 = Boundary.infinitePast();
        } else {
            Moment m1 = this.getStart().getTemporal().at(offset);
            b1 = Boundary.of(this.getStart().getEdge(), m1);
        }

        if (this.getEnd().isInfinite()) {
            b2 = Boundary.infiniteFuture();
        } else {
            Moment m2 = this.getEnd().getTemporal().at(offset);
            b2 = Boundary.of(this.getEnd().getEdge(), m2);
        }

        return new MomentInterval(b1, b2);

    }

    /**
     * <p>Combines this local timestamp interval with the system timezone
     * to a global UTC-interval. </p>
     *
     * @return  global timestamp interval interpreted in system timezone
     * @since   1.3
     * @see     Timezone#ofSystem()
     */
    /*[deutsch]
     * <p>Kombiniert dieses lokale Zeitstempelintervall mit der System-Zeitzone
     * zu einem globalen UTC-Intervall. </p>
     *
     * @return  global timestamp interval interpreted in system timezone
     * @since   1.3
     * @see     Timezone#ofSystem()
     */
    public MomentInterval inStdTimezone() {

        return this.in(Timezone.ofSystem());

    }

    /**
     * <p>Combines this local timestamp interval with given timezone
     * to a global UTC-interval. </p>
     *
     * @param   tzid        timezone id
     * @return  global timestamp interval interpreted in given timezone
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   1.3
     * @see     Timezone#of(TZID)
     * @see     #inStdTimezone()
     */
    /*[deutsch]
     * <p>Kombiniert dieses lokale Zeitstempelintervall mit der angegebenen
     * Zeitzone zu einem globalen UTC-Intervall. </p>
     *
     * @param   tzid        timezone id
     * @return  global timestamp interval interpreted in given timezone
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   1.3
     * @see     Timezone#of(TZID)
     * @see     #inStdTimezone()
     */
    public MomentInterval inTimezone(TZID tzid) {

        return this.in(Timezone.of(tzid));

    }

    /**
     * <p>Combines this local timestamp interval with given timezone
     * to a global UTC-interval. </p>
     *
     * @param   tz      timezone
     * @return  global timestamp intervall interpreted in given timezone
     * @since   1.3
     */
    /*[deutsch]
     * <p>Kombiniert dieses lokale Zeitstempelintervall mit der angegebenen
     * Zeitzone zu einem globalen UTC-Intervall. </p>
     *
     * @param   tz      timezone
     * @return  global timestamp intervall interpreted in given timezone
     * @since   1.3
     */
    public MomentInterval in(Timezone tz) {

        Boundary<Moment> b1;
        Boundary<Moment> b2;

        if (this.getStart().isInfinite()) {
            b1 = Boundary.infinitePast();
        } else {
            Moment m1 = this.getStart().getTemporal().in(tz);
            b1 = Boundary.of(this.getStart().getEdge(), m1);
        }

        if (this.getEnd().isInfinite()) {
            b2 = Boundary.infiniteFuture();
        } else {
            Moment m2 = this.getEnd().getTemporal().in(tz);
            b2 = Boundary.of(this.getEnd().getEdge(), m2);
        }

        return new MomentInterval(b1, b2);

    }

    /**
     * <p>Yields the length of this interval in given units. </p>
     *
     * @param   units   time units to be used in calculation
     * @return  duration in given units
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   1.3
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge dieses Intervalls in den angegebenen
     * Zeiteinheiten. </p>
     *
     * @param   units   time units to be used in calculation
     * @return  duration in given units
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   1.3
     */
    public <U extends IsoUnit> Duration<U> getDuration(U... units) {

        return Duration.in(units).between(
            this.getTemporalOfClosedStart(),
            this.getTemporalOfOpenEnd()
        );

    }

    /**
     * <p>Yields the length of this interval in given units and applies
     * a timezone offset correction . </p>
     *
     * @param   tz      timezone
     * @param   units   time units to be used in calculation
     * @return  duration in given units including a zonal correction
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   1.3
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge dieses Intervalls in den angegebenen
     * Zeiteinheiten und wendet eine Zeitzonenkorrektur an. </p>
     *
     * @param   tz      timezone
     * @param   units   time units to be used in calculation
     * @return  duration in given units including a zonal correction
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   1.3
     */
    public Duration<IsoUnit> getDuration(
        Timezone tz,
        IsoUnit... units
    ) {

        return Duration.in(tz, units).between(
            this.getTemporalOfClosedStart(),
            this.getTemporalOfOpenEnd()
        );

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
     * @since   1.3
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
     * @since   1.3
     * @see     BracketPolicy#SHOW_WHEN_NON_STANDARD
     */
    public static TimestampInterval parse(
        String text,
        ChronoFormatter<PlainTimestamp> formatter
    ) throws ParseException {

        return IntervalParser.of(
             TimestampIntervalFactory.INSTANCE,
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
     * @since   1.3
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
     * @since   1.3
     */
    public static TimestampInterval parse(
        CharSequence text,
        ChronoFormatter<PlainTimestamp> formatter,
        BracketPolicy policy,
        ParseLog status
    ) {

        return IntervalParser.of(
             TimestampIntervalFactory.INSTANCE,
             formatter,
             policy
        ).parse(text, status, formatter.getDefaultAttributes());

    }

    @Override
    protected TimeLine<PlainTimestamp> getTimeLine() {

        return PlainTimestamp.axis();

    }

    /**
     * @serialData  Uses
     *              <a href="../../../serialized-form.html#net.time4j.range.SPX">
     *              a dedicated serialization form</a> as proxy. The first byte
     *              contains the type-ID {@code 52} in the six most significant
     *              bits. The next bytes represent the start and the end
     *              boundary.
     *
     * Schematic algorithm:
     *
     * <pre>
     *  int header = 52;
     *  header <<= 2;
     *  out.writeByte(header);
     *  out.writeObject(getStart());
     *  out.writeObject(getEnd());
     * </pre>
     */
    private Object writeReplace() throws ObjectStreamException {

        return new SPX(this, SPX.TIMESTAMP_TYPE);

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
