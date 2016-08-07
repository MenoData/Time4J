/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
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
import net.time4j.format.Attributes;
import net.time4j.format.expert.ChronoFormatter;
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
import java.util.Locale;

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
public final class ClockInterval
    extends IsoInterval<PlainTime, ClockInterval>
    implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = -6020908050362634577L;

    private static final ChronoFormatter<PlainTime> BASIC_ISO =
        Iso8601Format.BASIC_WALL_TIME.with(Attributes.TRAILING_CHARACTERS, true);
    private static final ChronoFormatter<PlainTime> EXTENDED_ISO =
        Iso8601Format.EXTENDED_WALL_TIME.with(Attributes.TRAILING_CHARACTERS, true);

    private static final Comparator<ChronoInterval<PlainTime>> COMPARATOR =
        new IntervalComparator<PlainTime>(false, PlainTime.axis());

    //~ Konstruktoren -----------------------------------------------------

    // package-private
    ClockInterval(
        Boundary<PlainTime> start,
        Boundary<PlainTime> end
    ) {
        super(start, end);

        if (start.isInfinite() || end.isInfinite()) {
            throw new IllegalArgumentException(
                "Clock (time) intervals must be finite.");
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
     * @return  new time interval
     * @throws  IllegalArgumentException if start is after end
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt ein begrenztes halb-offenes Intervall zwischen den
     * angegebenen Uhrzeiten. </p>
     *
     * @param   start   time of lower boundary (inclusive)
     * @param   end     time of upper boundary (exclusive)
     * @return  new time interval
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
     * <p>Interpretes given text as interval using a localized interval pattern. </p>
     *
     * <p>If given printer does not contain a reference to a locale then the interval pattern
     * &quot;{0}/{1}&quot; will be used. Brackets representing interval boundaries cannot be parsed. </p>
     *
     * @param   text        text to be parsed
     * @param   parser      format object for parsing start and end components
     * @return  parsed interval
     * @throws  IndexOutOfBoundsException if given text is empty
     * @throws  ParseException if the text is not parseable
     * @since   3.9/4.6
     * @see     #parse(String, ChronoParser, String)
     * @see     net.time4j.format.FormatPatternProvider#getIntervalPattern(Locale)
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text als Intervall mit Hilfe eines lokalisierten
     * Intervallmusters. </p>
     *
     * <p>Falls der angegebene Formatierer keine Referenz zu einer Sprach- und L&auml;ndereinstellung hat, wird
     * das Intervallmuster &quot;{0}/{1}&quot; verwendet. Klammern, die Intervallgrenzen darstellen, k&ouml;nnen
     * nicht interpretiert werden. </p>
     *
     * @param   text        text to be parsed
     * @param   parser      format object for parsing start and end components
     * @return  parsed interval
     * @throws  IndexOutOfBoundsException if given text is empty
     * @throws  ParseException if the text is not parseable
     * @since   3.9/4.6
     * @see     #parse(String, ChronoParser, String)
     * @see     net.time4j.format.FormatPatternProvider#getIntervalPattern(Locale)
     */
    public static ClockInterval parse(
        String text,
        ChronoParser<PlainTime> parser
    ) throws ParseException {

        return parse(text, parser, IsoInterval.getIntervalPattern(parser));

    }

    /**
     * <p>Interpretes given text as interval using given interval pattern. </p>
     *
     * <p>Brackets representing interval boundaries cannot be parsed. </p>
     *
     * @param   text                text to be parsed
     * @param   parser              format object for parsing start and end components
     * @param   intervalPattern     interval pattern containing placeholders {0} and {1} (for start and end)
     * @return  parsed interval
     * @throws  IndexOutOfBoundsException if given text is empty
     * @throws  ParseException if the text is not parseable
     * @since   3.9/4.6
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text als Intervall mit Hilfe des angegebenen
     * Intervallmusters. </p>
     *
     * <p>Klammern, die Intervallgrenzen darstellen, k&ouml;nnen nicht interpretiert werden. </p>
     *
     * @param   text                text to be parsed
     * @param   parser              format object for parsing start and end components
     * @param   intervalPattern     interval pattern containing placeholders {0} and {1} (for start and end)
     * @return  parsed interval
     * @throws  IndexOutOfBoundsException if given text is empty
     * @throws  ParseException if the text is not parseable
     * @since   3.9/4.6
     */
    public static ClockInterval parse(
        String text,
        ChronoParser<PlainTime> parser,
        String intervalPattern
    ) throws ParseException {

        ParseLog plog = new ParseLog();
        ClockInterval interval =
            IntervalParser.parseCustom(text, ClockIntervalFactory.INSTANCE, parser, intervalPattern, plog);

        if (plog.isError()) {
            throw new ParseException(plog.getErrorMessage(), plog.getErrorIndex());
        } else if (interval == null) {
            throw new ParseException("Parsing of interval failed: " + text, plog.getPosition());
        }

        return interval;

    }

    /**
     * <p>Interpretes given text as interval. </p>
     *
     * <p>Similar to {@link #parse(CharSequence, ChronoParser, char, ChronoParser, BracketPolicy, ParseLog)}.
     * Since version v3.9/4.6 this method can also accept a hyphen as alternative to solidus as separator
     * between start and end component unless the start component is a period. </p>
     *
     * @param   text        text to be parsed
     * @param   parser      format object for parsing start and end components
     * @param   policy      strategy for parsing interval boundaries
     * @param   status      parser information (always as new instance)
     * @return  result or {@code null} if parsing does not work
     * @throws  IndexOutOfBoundsException if the start position is at end of text or even behind
     * @since   2.0
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text als Intervall. </p>
     *
     * <p>&Auml;hnlich wie {@link #parse(CharSequence, ChronoParser, char, ChronoParser, BracketPolicy, ParseLog)}.
     * Seit der Version v3.9/4.6 kann diese Methode auch einen Bindestrich als Alternative zum Schr&auml;gstrich
     * als Trennzeichen zwischen Start- und Endkomponente, es sei denn, die Startkomponente ist eine Periode. </p>
     *
     * @param   text        text to be parsed
     * @param   parser      format object for parsing start and end components
     * @param   policy      strategy for parsing interval boundaries
     * @param   status      parser information (always as new instance)
     * @return  result or {@code null} if parsing does not work
     * @throws  IndexOutOfBoundsException if the start position is at end of text or even behind
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
     * <p>Interpretes given text as interval. </p>
     *
     * <p>This method is mainly intended for parsing technical interval formats similar to ISO-8601
     * which are not localized. </p>
     *
     * @param   text        text to be parsed
     * @param   startFormat format object for parsing start component
     * @param   separator   char separating start and end component
     * @param   endFormat   format object for parsing end component
     * @param   policy      strategy for parsing interval boundaries
     * @param   status      parser information (always as new instance)
     * @return  result or {@code null} if parsing does not work
     * @throws  IndexOutOfBoundsException if the start position is at end of text or even behind
     * @since   3.9/4.6
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text als Intervall. </p>
     *
     * <p>Diese Methode ist vor allem f&uuml;r technische nicht-lokalisierte Intervallformate &auml;hnlich
     * wie in ISO-8601 definiert gedacht. </p>
     *
     * @param   text        text to be parsed
     * @param   startFormat format object for parsing start component
     * @param   separator   char separating start and end component
     * @param   endFormat   format object for parsing end component
     * @param   policy      strategy for parsing interval boundaries
     * @param   status      parser information (always as new instance)
     * @return  result or {@code null} if parsing does not work
     * @throws  IndexOutOfBoundsException if the start position is at end of text or even behind
     * @since   3.9/4.6
     */
    public static ClockInterval parse(
        CharSequence text,
        ChronoParser<PlainTime> startFormat,
        char separator,
        ChronoParser<PlainTime> endFormat,
        BracketPolicy policy,
        ParseLog status
    ) {

        return IntervalParser.of(
            ClockIntervalFactory.INSTANCE,
            startFormat,
            endFormat,
            policy,
            separator
        ).parse(text, status, IsoInterval.extractDefaultAttributes(startFormat));

    }

    /**
     * <p>Interpretes given ISO-conforming text as interval. </p>
     *
     * <p>Examples for supported formats: </p>
     *
     * <ul>
     *     <li>09:45/PT5H</li>
     *     <li>PT5H/14:45</li>
     *     <li>0945/PT5H</li>
     *     <li>PT5H/1445</li>
     *     <li>PT01:55:30/14:15:30</li>
     *     <li>04:01:30.123/24:00:00.000</li>
     *     <li>04:01:30,123/24:00:00,000</li>
     * </ul>
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
     * <p>Beispiele f&uuml;r unterst&uuml;tzte Formate: </p>
     *
     * <ul>
     *     <li>09:45/PT5H</li>
     *     <li>PT5H/14:45</li>
     *     <li>0945/PT5H</li>
     *     <li>PT5H/1445</li>
     *     <li>PT01:55:30/14:15:30</li>
     *     <li>04:01:30.123/24:00:00.000</li>
     *     <li>04:01:30,123/24:00:00,000</li>
     * </ul>
     *
     * @param   text        text to be parsed
     * @return  parsed interval
     * @throws  IndexOutOfBoundsException if given text is empty
     * @throws  ParseException if the text is not parseable
     * @since   2.1
     * @see     BracketPolicy#SHOW_NEVER
     */
    public static ClockInterval parseISO(String text) throws ParseException {

        if (text.isEmpty()) {
            throw new IndexOutOfBoundsException("Empty text.");
        }

        ChronoParser<PlainTime> parser = ((text.indexOf(':') == -1) ? BASIC_ISO : EXTENDED_ISO);
        ParseLog plog = new ParseLog();

        ClockInterval result =
            new IntervalParser<PlainTime, ClockInterval>(
                ClockIntervalFactory.INSTANCE,
                parser,
                parser,
                BracketPolicy.SHOW_NEVER,
                '/'
            ).parse(text, plog, IsoInterval.extractDefaultAttributes(parser));

        if ((result == null) || plog.isError()) {
            throw new ParseException(plog.getErrorMessage(), plog.getErrorIndex());
        } else if (plog.getPosition() < text.length()) {
            throw new ParseException("Trailing characters found: " + text, plog.getPosition());
        } else {
            return result;
        }

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
     *  int header = 33;
     *  header &lt;&lt;= 2;
     *  out.writeByte(header);
     *  writeBoundary(getStart(), out);
     *  writeBoundary(getEnd(), out);
     *
     *  private static void writeBoundary(
     *      Boundary&lt;?&gt; boundary,
     *      ObjectOutput out
     *  ) throws IOException {
     *      if (boundary.equals(Boundary.infinitePast())) {
     *          out.writeByte(1);
     *      } else if (boundary.equals(Boundary.infiniteFuture())) {
     *          out.writeByte(2);
     *      } else {
     *          out.writeByte(boundary.isOpen() ? 4 : 0);
     *          out.writeObject(boundary.getTemporal());
     *      }
     *  }
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
