/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ClockInterval.java) is part of project Time4J.
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
import net.time4j.format.expert.ChronoParser;
import net.time4j.format.expert.Iso8601Format;
import net.time4j.format.expert.ParseLog;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Comparator;

import static net.time4j.ClockUnit.HOURS;
import static net.time4j.ClockUnit.NANOS;
import static net.time4j.range.IntervalEdge.CLOSED;
import static net.time4j.range.IntervalEdge.OPEN;


/**
 * <p>Defines a finite wall time interval on the local timeline. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 * @doctags.concurrency {immutable}
 */
/*[deutsch]
 * <p>Definiert ein endliches Uhrzeitintervall auf dem lokalen Zeitstrahl. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 * @doctags.concurrency {immutable}
 */
public final class ClockInterval
    extends IsoInterval<PlainTime, ClockInterval>
    implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = -6020908050362634577L;

    private static final Comparator<ChronoInterval<PlainTime>> COMPARATOR =
        new IntervalComparator<>(false, PlainTime.axis());

    //~ Konstruktoren -----------------------------------------------------

    // package-private
    ClockInterval(
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
     * <p>Defines a comparator which sorts intervals first
     * by start boundary and then by length. </p>
     *
     * @return  Comparator
     * @since   2.0
     */
    /*[deutsch]
     * <p>Definiert ein Vergleichsobjekt, das Intervalle zuerst nach dem
     * Start und dann nach der L&auml;nge sortiert. </p>
     *
     * @return  Comparator
     * @since   2.0
     */
    public static Comparator<ChronoInterval<PlainTime>> comparator() {

        return COMPARATOR;

    }

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
    public static ClockInterval between(
        PlainTime start,
        PlainTime end
    ) {

        return new ClockInterval(
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
    public static ClockInterval since(PlainTime start) {

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
    public static ClockInterval until(PlainTime end) {

        return between(PlainTime.midnightAtStartOfDay(), end);

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

        PlainTime t1 = this.getTemporalOfClosedStart();
        PlainTime t2 = this.getEnd().getTemporal();

        if (this.getEnd().isClosed()) {
            if (t2.getHour() == 24) {
                if (t1.equals(PlainTime.midnightAtStartOfDay())) {
                    return Duration.of(24, HOURS).plus(1, NANOS);
                } else {
                    t1 = t1.minus(1, NANOS);
                }
            } else {
                t2 = t2.plus(1, NANOS);
            }
        }

        return Duration.inClockUnits().between(t1, t2);

    }

    /**
     * <p>Moves this interval along the time axis by given units. </p>
     *
     * @param   amount  amount of units
     * @param   unit    time unit for moving
     * @return  moved copy of this interval
     */
    public ClockInterval move(
        long amount,
        ClockUnit unit
    ) {

        if (amount == 0) {
            return this;
        }

        Boundary<PlainTime> s;
        Boundary<PlainTime> e;

        if (this.getStart().isInfinite()) {
            s = Boundary.infinitePast();
        } else {
            s =
                Boundary.of(
                    this.getStart().getEdge(),
                    this.getStart().getTemporal().plus(amount, unit));
        }

        if (this.getEnd().isInfinite()) {
            e = Boundary.infiniteFuture();
        } else {
            e =
                Boundary.of(
                    this.getEnd().getEdge(),
                    this.getEnd().getTemporal().plus(amount, unit));
        }

        return new ClockInterval(s, e);

    }

    /**
     * <p>Interpretes given text as interval. </p>
     *
     * @param   text        text to be parsed
     * @param   parser      format object for parsing start and end boundaries
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
     * @param   parser      format object for parsing start and end boundaries
     * @return  parsed interval
     * @throws  IndexOutOfBoundsException if the start position is at end of
     *          text or even behind
     * @throws  ParseException if the text is not parseable
     * @since   2.0
     * @see     BracketPolicy#SHOW_WHEN_NON_STANDARD
     */
    public static ClockInterval parse(
        String text,
        ChronoParser<PlainTime> parser
    ) throws ParseException {

        return IntervalParser.of(
             ClockIntervalFactory.INSTANCE,
             parser,
             BracketPolicy.SHOW_WHEN_NON_STANDARD
        ).parse(text);

    }

    /**
     * <p>Interpretes given text as interval. </p>
     *
     * @param   text        text to be parsed
     * @param   parser      format object for parsing start and end boundaries
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
     * @param   parser      format object for parsing start and end boundaries
     * @param   policy      strategy for parsing interval boundaries
     * @param   status      parser information (always as new instance)
     * @return  result or {@code null} if parsing does not work
     * @throws  IndexOutOfBoundsException if the start position is at end of
     *          text or even behind
     * @since   2.0
     */
    public static ClockInterval parse(
        CharSequence text,
        ChronoParser<PlainTime> parser,
        BracketPolicy policy,
        ParseLog status
    ) {

        return IntervalParser.of(
             ClockIntervalFactory.INSTANCE,
             parser,
             policy
        ).parse(text, status, IsoInterval.extractDefaultAttributes(parser));

    }

    /**
     * <p>Interpretes given ISO-conforming text as interval. </p>
     *
     * <p>Equivalent to {@link #parse(String,ChronoParser)
     * parse(text, Iso8601Format#EXTENDED_WALL_TIME)},
     * but can also understand the basic ISO-format. </p>
     *
     * @param   text        text to be parsed
     * @return  parsed interval
     * @throws  IndexOutOfBoundsException if given text is empty
     * @throws  ParseException if the text is not parseable
     * @since   2.1
     * @see     BracketPolicy#SHOW_NEVER
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen ISO-konformen Text als Intervall. </p>
     *
     * <p>&Auml;quivalent zu {@link #parse(String,ChronoParser)
     * parse(text, Iso8601Format#EXTENDED_WALL_TIME)},
     * kann aber auch das <i>basic</i>-ISO-Format verstehen. </p>
     *
     * @param   text        text to be parsed
     * @return  parsed interval
     * @throws  IndexOutOfBoundsException if given text is empty
     * @throws  ParseException if the text is not parseable
     * @since   2.1
     * @see     BracketPolicy#SHOW_NEVER
     */
    public static ClockInterval parseISO(String text) throws ParseException {

        ChronoParser<PlainTime> parser;
        ParseLog plog = new ParseLog();

        if (text.length() > 3 && text.charAt(2) == ':') {
            parser = Iso8601Format.EXTENDED_WALL_TIME;
        } else {
            parser = Iso8601Format.BASIC_WALL_TIME;
        }

        ClockInterval result =
            IntervalParser.of(
                ClockIntervalFactory.INSTANCE,
                parser,
                parser,
                BracketPolicy.SHOW_NEVER,
                '/',
                null // without abbreviation of end component
            ).parse(text, plog, IsoInterval.extractDefaultAttributes(parser));

        if (
            (result == null)
            || plog.isError()
        ) {
            throw new ParseException(
                plog.getErrorMessage(), plog.getErrorIndex());
        } else {
            return result;
        }

    }

    @Override
    TimeLine<PlainTime> getTimeLine() {

        return PlainTime.axis();

    }

    @Override
    IntervalFactory<PlainTime, ClockInterval> getFactory() {

        return ClockIntervalFactory.INSTANCE;

    }

    @Override
    ClockInterval getContext() {

        return this;

    }

    /**
     * @serialData  Uses
     *              <a href="../../../serialized-form.html#net.time4j.range.SPX">
     *              a dedicated serialization form</a> as proxy. The first byte
     *              contains the type-ID {@code 33} in the six most significant
     *              bits. The next bytes represent the start and the end
     *              boundary.
     *
     * Schematic algorithm:
     *
     * <pre>
       int header = 33;
       header &lt;&lt;= 2;
       out.writeByte(header);
       writeBoundary(getStart(), out);
       writeBoundary(getEnd(), out);

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

        return new SPX(this, SPX.TIME_TYPE);

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
