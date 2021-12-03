/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
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
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.engine.TimeSpan;
import net.time4j.format.Attributes;
import net.time4j.format.expert.ChronoParser;
import net.time4j.format.expert.ChronoPrinter;
import net.time4j.format.expert.Iso8601Format;
import net.time4j.format.expert.IsoDecimalStyle;
import net.time4j.format.expert.ParseLog;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    /**
     * <p>Represents the full day from 00:00 inclusive to 24:00 exclusive. </p>
     *
     * @since   5.4
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den vollen Tag von 00:00 inklusive bis 24:00 exklusive. </p>
     *
     * @since   5.4
     */
    public static final ClockInterval FULL_DAY =
        ClockInterval.between(PlainTime.midnightAtStartOfDay(), PlainTime.midnightAtEndOfDay());

    private static final long serialVersionUID = -6020908050362634577L;

    private static final Comparator<ChronoInterval<PlainTime>> COMPARATOR =
        new IntervalComparator<>(PlainTime.axis());

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
     * <p>Creates a finite half-open interval between given wall times. </p>
     *
     * <p>For better handling of time 24:00, it is recommended to directly use the overloaded variant with
     * arguments of type {@code PlainTime}. </p>
     *
     * @param   start   time of lower boundary (inclusive)
     * @param   end     time of upper boundary (exclusive)
     * @return  new time interval
     * @throws  IllegalArgumentException if start is after end
     * @see     #between(PlainTime, PlainTime)
     * @since   4.11
     */
    /*[deutsch]
     * <p>Erzeugt ein begrenztes halb-offenes Intervall zwischen den
     * angegebenen Uhrzeiten. </p>
     *
     * <p>F&uuml;r die spezielle Uhrzeit 24:00 wird empfohlen, die &uuml;berladene Variante mit Argumenten
     * des Typs {@code PlainTime} zu verwenden. </p>
     *
     * @param   start   time of lower boundary (inclusive)
     * @param   end     time of upper boundary (exclusive)
     * @return  new time interval
     * @throws  IllegalArgumentException if start is after end
     * @see     #between(PlainTime, PlainTime)
     * @since   4.11
     */
    public static ClockInterval between(
        LocalTime start,
        LocalTime end
    ) {

        return ClockInterval.between(PlainTime.from(start), PlainTime.from(end));

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
     * <p>Creates a finite half-open interval between given start time and
     * midnight at end of day (exclusive). </p>
     *
     * @param   start       time of lower boundary (inclusive)
     * @return  new time interval
     * @see     #since(PlainTime)
     * @since   4.11
     */
    /*[deutsch]
     * <p>Erzeugt ein begrenztes halb-offenes Intervall zwischen der
     * angegebenen Startzeit und Mitternacht zu Ende des Tages (exklusive). </p>
     *
     * @param   start       time of lower boundary (inclusive)
     * @return  new time interval
     * @see     #since(PlainTime)
     * @since   4.11
     */
    public static ClockInterval since(LocalTime start) {

        return ClockInterval.since(PlainTime.from(start));

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
     * <p>Creates a finite half-open interval between midnight at start of day
     * and given end time. </p>
     *
     * @param   end     time of upper boundary (exclusive)
     * @return  new time interval
     * @see     #until(PlainTime)
     * @since   4.11
     */
    /*[deutsch]
     * <p>Erzeugt ein begrenztes halb-offenes Intervall zwischen Mitternacht
     * zu Beginn des Tages und der angegebenen Endzeit. </p>
     *
     * @param   end     time of upper boundary (exclusive)
     * @return  new time interval
     * @see     #until(PlainTime)
     * @since   4.11
     */
    public static ClockInterval until(LocalTime end) {

        return ClockInterval.until(PlainTime.from(end));

    }

    /**
     * <p>Converts an arbitrary clock interval to an interval of this type. </p>
     *
     * @param   interval    any kind of clock interval
     * @return  ClockInterval
     * @since   3.34/4.29
     */
    /*[deutsch]
     * <p>Konvertiert ein beliebiges Intervall zu einem Intervall dieses Typs. </p>
     *
     * @param   interval    any kind of clock interval
     * @return  ClockInterval
     * @since   3.34/4.29
     */
    public static ClockInterval from(ChronoInterval<PlainTime> interval) {

        if (interval instanceof ClockInterval) {
            return ClockInterval.class.cast(interval);
        } else {
            return new ClockInterval(interval.getStart(), interval.getEnd());
        }

    }

    /**
     * <p>Creates a timestamp interval for given calendar date based on this clock interval. </p>
     *
     * @param   date    gregorian calendar date
     * @return  new timestamp interval
     * @since   5.0
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Zeitstempelintervall zum angegebenen Datum basierend auf diesem Uhrzeitintervall. </p>
     *
     * @param   date    gregorian calendar date
     * @return  new timestamp interval
     * @since   5.0
     */
    public TimestampInterval on(PlainDate date) {

        return TimestampInterval.between(
            date.at(this.getStartAsClockTime()),
            date.at(this.getEndAsClockTime()));

    }

    /**
     * <p>Yields the start time point. </p>
     *
     * @return  start time point
     * @since   4.11
     */
    /*[deutsch]
     * <p>Liefert den Startzeitpunkt. </p>
     *
     * @return  start time point
     * @since   4.11
     */
    public PlainTime getStartAsClockTime() {

        return this.getStart().getTemporal();

    }

    /**
     * <p>Yields the start time point. </p>
     *
     * @return  start time point
     * @since   4.11
     */
    /*[deutsch]
     * <p>Liefert den Startzeitpunkt. </p>
     *
     * @return  start time point
     * @since   4.11
     */
    public LocalTime getStartAsLocalTime() {

        return this.getStartAsClockTime().toTemporalAccessor();

    }

    /**
     * <p>Yields the end time point. </p>
     *
     * @return  end time point
     * @since   4.11
     */
    /*[deutsch]
     * <p>Liefert den Endzeitpunkt. </p>
     *
     * @return  end time point
     * @since   4.11
     */
    public PlainTime getEndAsClockTime() {

        return this.getEnd().getTemporal();

    }

    /**
     * <p>Yields the end time point. </p>
     *
     * <p>The end time 24:00 (midnight at end of day) will be mapped to midnight at start of next day (00:00). </p>
     *
     * @return  end time point
     * @since   4.11
     */
    /*[deutsch]
     * <p>Liefert den Endzeitpunkt. </p>
     *
     * <p>Die Zeit 24:00 (Mitternacht am Ende des Tages) wird auf Mitternacht zu Beginn des n&auml;chsten
     * Tages abgebildet (00:00). </p>
     *
     * @return  end time point
     * @since   4.11
     */
    public LocalTime getEndAsLocalTime() {

        return this.getEndAsClockTime().toTemporalAccessor();

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
     * <p>Obtains a stream iterating over every clock time which is the result of addition of given duration
     * to start until the end of this interval is reached. </p>
     *
     * <p>The stream size is limited to {@code Integer.MAX_VALUE - 1} else an {@code ArithmeticException}
     * will be thrown. </p>
     *
     * @param   duration    duration which has to be added to the start multiple times
     * @throws  IllegalArgumentException if the duration is not positive
     * @throws  IllegalStateException if this interval has no canonical form
     * @return  stream consisting of distinct clock times which are the result of adding the duration to the start
     * @see     #toCanonical()
     * @see     #stream(Duration, PlainTime, PlainTime)
     * @since   4.18
     */
    /*[deutsch]
     * <p>Erzeugt einen {@code Stream}, der jeweils eine Uhrzeit als Vielfaches der Dauer angewandt auf
     * den Start und bis zum Ende dieses Intervalls geht. </p>
     *
     * <p>Die Gr&ouml;&szlig;e des {@code Stream} ist maximal {@code Integer.MAX_VALUE - 1}, ansonsten wird
     * eine {@code ArithmeticException} geworfen. </p>
     *
     * @param   duration    duration which has to be added to the start multiple times
     * @throws  IllegalArgumentException if the duration is not positive
     * @throws  IllegalStateException if this interval has no canonical form
     * @return  stream consisting of distinct clock times which are the result of adding the duration to the start
     * @see     #toCanonical()
     * @see     #stream(Duration, PlainTime, PlainTime)
     * @since   4.18
     */
    public Stream<PlainTime> stream(Duration<ClockUnit> duration) {

        ClockInterval interval = this.toCanonical();
        return ClockInterval.stream(duration, interval.getStartAsClockTime(), interval.getEndAsClockTime());

    }

    /**
     * <p>Obtains a stream iterating over every clock time which is the result of addition of given duration
     * to start until the end is reached. </p>
     *
     * <p>This static method avoids the costs of constructing an instance of {@code ClockInterval}.
     * The stream size is limited to {@code Integer.MAX_VALUE - 1} else an {@code ArithmeticException}
     * will be thrown. </p>
     *
     * @param   duration    duration which has to be added to the start multiple times
     * @param   start       start boundary - inclusive
     * @param   end         end boundary - exclusive
     * @throws  IllegalArgumentException if start is after end or if the duration is not positive
     * @return  stream consisting of distinct clock times which are the result of adding the duration to the start
     * @since   4.18
     */
    /*[deutsch]
     * <p>Erzeugt einen {@code Stream}, der jeweils eine Uhrzeit als Vielfaches der Dauer angewandt auf
     * den Start und bis zum Ende geht. </p>
     *
     * <p>Diese statische Methode vermeidet die Kosten der Intervallerzeugung. Die Gr&ouml;&szlig;e des
     * {@code Stream} ist maximal {@code Integer.MAX_VALUE - 1}, ansonsten wird eine {@code ArithmeticException}
     * geworfen. </p>
     *
     * @param   duration    duration which has to be added to the start multiple times
     * @param   start       start boundary - inclusive
     * @param   end         end boundary - exclusive
     * @throws  IllegalArgumentException if start is after end or if the duration is not positive
     * @return  stream consisting of distinct clock times which are the result of adding the duration to the start
     * @since   4.18
     */
    public static Stream<PlainTime> stream(
        Duration<ClockUnit> duration,
        PlainTime start,
        PlainTime end
    ) {

        if (!duration.isPositive()) {
            throw new IllegalArgumentException("Duration must be positive: " + duration);
        }

        int comp = start.compareTo(end);

        if (comp > 0) {
            throw new IllegalArgumentException("Start after end: " + start + "/" + end);
        } else if (comp == 0) {
            return Stream.empty();
        }

        double secs = 0.0;

        for (TimeSpan.Item<ClockUnit> item : duration.getTotalLength()) {
            secs += item.getUnit().getLength() * item.getAmount();
        }

        double est; // first estimate

        if (secs < 1.0) {
            est = (ClockUnit.NANOS.between(start, end) / (secs * 1_000_000_000));
        } else {
            est = (ClockUnit.SECONDS.between(start, end) / secs);
        }

        if (Double.compare(est, Integer.MAX_VALUE) >= 0) {
            throw new ArithmeticException();
        }

        int n = (int) Math.floor(est);
        boolean backwards = false;

        while ((n > 0) && !start.plus(duration.multipliedBy(n)).isBefore(end)) {
            n--;
            backwards = true;
        }

        int size = n + 1;

        if (!backwards) {
            do {
                size = Math.addExact(n, 1);
                n++;
            } while (start.plus(duration.multipliedBy(n)).isBefore(end));
        }

        if (size == 1) {
            return Stream.of(start); // short-cut
        }

        return IntStream.range(0, size).mapToObj(index -> start.plus(duration.multipliedBy(index)));

    }

    /**
     * <p>Obtains a random time within this interval. </p>
     *
     * @return  random time within this interval
     * @throws  IllegalStateException if this interval is empty or if there is no canonical form
     * @see     #toCanonical()
     * @since   5.0
     */
    /*[deutsch]
     * <p>Liefert eine Zufallszeit innerhalb dieses Intervalls. </p>
     *
     * @return  random time within this interval
     * @throws  IllegalStateException if this interval is empty or if there is no canonical form
     * @see     #toCanonical()
     * @since   5.0
     */
    public PlainTime random() {

        ClockInterval interval = this.toCanonical();

        if (interval.isEmpty()) {
            throw new IllegalStateException("Cannot get random time in an empty interval: " + this);
        } else {
            long s = interval.getStartAsClockTime().get(PlainTime.NANO_OF_DAY).longValue();
            long e = interval.getEndAsClockTime().get(PlainTime.NANO_OF_DAY).longValue();
            long randomNum = ThreadLocalRandom.current().nextLong(s, e);
            return PlainTime.midnightAtStartOfDay().plus(randomNum, ClockUnit.NANOS);
        }

    }

    /**
     * <p>Prints the canonical form of this interval in given basic ISO-8601 style. </p>
     *
     * @param   decimalStyle    iso-compatible decimal style
     * @param   precision       controls the precision of output format with constant length
     * @return  String
     * @throws  IllegalStateException if there is no canonical form (for example for [00:00/24:00])
     * @see     #toCanonical()
     * @since   4.18
     */
    /*[deutsch]
     * <p>Formatiert die kanonische Form dieses Intervalls im angegebenen <i>basic</i> ISO-8601-Stil. </p>
     *
     * @param   decimalStyle    iso-compatible decimal style
     * @param   precision       controls the precision of output format with constant length
     * @return  String
     * @throws  IllegalStateException if there is no canonical form (for example for [00:00/24:00])
     * @see     #toCanonical()
     * @since   4.18
     */
    public String formatBasicISO(
        IsoDecimalStyle decimalStyle,
        ClockUnit precision
    ) {

        ClockInterval interval = this.toCanonical();
        StringBuilder buffer = new StringBuilder();
        ChronoPrinter<PlainTime> printer = Iso8601Format.ofBasicTime(decimalStyle, precision);
        printer.print(interval.getStartAsClockTime(), buffer);
        buffer.append('/');
        printer.print(interval.getEndAsClockTime(), buffer);
        return buffer.toString();

    }

    /**
     * <p>Prints the canonical form of this interval in given extended ISO-8601 style. </p>
     *
     * @param   decimalStyle    iso-compatible decimal style
     * @param   precision       controls the precision of output format with constant length
     * @return  String
     * @throws  IllegalStateException if there is no canonical form (for example for [00:00/24:00])
     * @see     #toCanonical()
     * @since   4.18
     */
    /*[deutsch]
     * <p>Formatiert die kanonische Form dieses Intervalls im angegebenen <i>extended</i> ISO-8601-Stil. </p>
     *
     * @param   decimalStyle    iso-compatible decimal style
     * @param   precision       controls the precision of output format with constant length
     * @return  String
     * @throws  IllegalStateException if there is no canonical form (for example for [00:00/24:00])
     * @see     #toCanonical()
     * @since   4.18
     */
    public String formatExtendedISO(
        IsoDecimalStyle decimalStyle,
        ClockUnit precision
    ) {

        ClockInterval interval = this.toCanonical();
        StringBuilder buffer = new StringBuilder();
        ChronoPrinter<PlainTime> printer = Iso8601Format.ofExtendedTime(decimalStyle, precision);
        printer.print(interval.getStartAsClockTime(), buffer);
        buffer.append('/');
        printer.print(interval.getEndAsClockTime(), buffer);
        return buffer.toString();

    }

    /**
     * <p>Interpretes given text as interval using a localized interval pattern. </p>
     *
     * <p>If given parser does not contain a reference to a locale then the interval pattern
     * &quot;{0}/{1}&quot; will be used. </p>
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
     * das Intervallmuster &quot;{0}/{1}&quot; verwendet. </p>
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
     * <p>About usage see also {@link DateInterval#parse(String, ChronoParser, String)}. </p>
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
     * <p>Zur Verwendung siehe auch: {@link DateInterval#parse(String, ChronoParser, String)}. </p>
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

        return IntervalParser.parsePattern(text, ClockIntervalFactory.INSTANCE, parser, intervalPattern);

    }

    /**
     * <p>Interpretes given text as interval. </p>
     *
     * <p>This method can also accept a hyphen as alternative to solidus as separator
     * between start and end component unless the start component is a period. </p>
     *
     * @param   text        text to be parsed
     * @param   parser      format object for parsing start and end components
     * @param   policy      strategy for parsing interval boundaries
     * @return  result
     * @throws  ParseException if parsing does not work
     * @throws  IndexOutOfBoundsException if the start position is at end of text or even behind
     * @since   4.18
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text als Intervall. </p>
     *
     * <p>Diese Methode kann auch einen Bindestrich als Alternative zum Schr&auml;gstrich als Trennzeichen zwischen
     * Start- und Endkomponente interpretieren, es sei denn, die Startkomponente ist eine Periode. </p>
     *
     * @param   text        text to be parsed
     * @param   parser      format object for parsing start and end components
     * @param   policy      strategy for parsing interval boundaries
     * @return  result
     * @throws  ParseException if parsing does not work
     * @throws  IndexOutOfBoundsException if the start position is at end of text or even behind
     * @since   4.18
     */
    public static ClockInterval parse(
        CharSequence text,
        ChronoParser<PlainTime> parser,
        BracketPolicy policy
    ) throws ParseException {

        ParseLog plog = new ParseLog();
        ClockInterval interval =
            IntervalParser.of(
                ClockIntervalFactory.INSTANCE,
                parser,
                policy
            ).parse(text, plog, parser.getAttributes());

        if ((interval == null) || plog.isError()) {
            throw new ParseException(plog.getErrorMessage(), plog.getErrorIndex());
        } else if (
            (plog.getPosition() < text.length())
            && !parser.getAttributes().get(Attributes.TRAILING_CHARACTERS, Boolean.FALSE).booleanValue()
        ) {
            throw new ParseException("Trailing characters found: " + text, plog.getPosition());
        } else {
            return interval;
        }

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
        ).parse(text, status, startFormat.getAttributes());

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

        ChronoParser<PlainTime> parser = (
            (text.indexOf(':') == -1) ? Iso8601Format.BASIC_WALL_TIME : Iso8601Format.EXTENDED_WALL_TIME);
        ParseLog plog = new ParseLog();

        ClockInterval result =
            new IntervalParser<>(
                ClockIntervalFactory.INSTANCE,
                parser,
                parser,
                BracketPolicy.SHOW_NEVER,
                '/'
            ).parse(text, plog, parser.getAttributes());

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
