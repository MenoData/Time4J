/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
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

import net.time4j.Duration;
import net.time4j.IsoUnit;
import net.time4j.Moment;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.SI;
import net.time4j.Weekmodel;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.format.DisplayMode;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.ChronoParser;
import net.time4j.format.expert.Iso8601Format;
import net.time4j.format.expert.ParseLog;
import net.time4j.format.expert.SignPolicy;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static net.time4j.PlainDate.*;
import static net.time4j.format.Attributes.PROTECTED_CHARACTERS;
import static net.time4j.range.IntervalEdge.CLOSED;
import static net.time4j.range.IntervalEdge.OPEN;


/**
 * <p>Defines a moment interval on global timeline. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 */
/*[deutsch]
 * <p>Definiert ein Momentintervall auf dem globalen Zeitstrahl. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 */
public final class MomentInterval
    extends IsoInterval<Moment, MomentInterval>
    implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = -5403584519478162113L;

    private static final ChronoFormatter<Moment> EXT_C = calendarFormat(true);
    private static final ChronoFormatter<Moment> EXT_O = ordinalFormat(true);
    private static final ChronoFormatter<Moment> EXT_W = weekdateFormat(true);
    private static final ChronoFormatter<Moment> BAS_C = calendarFormat(false);
    private static final ChronoFormatter<Moment> BAS_O = ordinalFormat(false);
    private static final ChronoFormatter<Moment> BAS_W = weekdateFormat(false);

    private static final Comparator<ChronoInterval<Moment>> COMPARATOR =
        new IntervalComparator<Moment>(false, Moment.axis());

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
     * <p>Defines a comparator which sorts intervals first
     * by start boundary and then by length. </p>
     *
     * @return  Comparator
     * @throws  IllegalArgumentException if applied on intervals which have
     *          boundaries with extreme values
     * @since   2.0
     */
    /*[deutsch]
     * <p>Definiert ein Vergleichsobjekt, das Intervalle zuerst nach dem
     * Start und dann nach der L&auml;nge sortiert. </p>
     *
     * @return  Comparator
     * @throws  IllegalArgumentException if applied on intervals which have
     *          boundaries with extreme values
     * @since   2.0
     */
    public static Comparator<ChronoInterval<Moment>> comparator() {

        return COMPARATOR;

    }

    /**
     * <p>Creates a finite half-open interval between given time points. </p>
     *
     * @param   start   moment of lower boundary (inclusive)
     * @param   end     moment of upper boundary (exclusive)
     * @return  new moment interval
     * @throws  IllegalArgumentException if start is after end
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt ein begrenztes halb-offenes Intervall zwischen den
     * angegebenen Zeitpunkten. </p>
     *
     * @param   start   moment of lower boundary (inclusive)
     * @param   end     moment of upper boundary (exclusive)
     * @return  new moment interval
     * @throws  IllegalArgumentException if start is after end
     * @since   2.0
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
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt ein unbegrenztes halb-offenes Intervall ab dem angegebenen
     * Startzeitpunkt. </p>
     *
     * @param   start       moment of lower boundary (inclusive)
     * @return  new moment interval
     * @since   2.0
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
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt ein unbegrenztes offenes Intervall bis zum angegebenen
     * Endzeitpunkt. </p>
     *
     * @param   end     moment of upper boundary (exclusive)
     * @return  new moment interval
     * @since   2.0
     */
    public static MomentInterval until(Moment end) {

        Boundary<Moment> past = Boundary.infinitePast();
        return new MomentInterval(past, Boundary.of(OPEN, end));

    }

    /**
     * <p>Converts this instance to a local timestamp interval in the system
     * timezone. </p>
     *
     * @return  local timestamp interval in system timezone (leap seconds will
     *          always be lost)
     * @since   2.0
     * @see     Timezone#ofSystem()
     * @see     #toZonalInterval(TZID)
     * @see     #toZonalInterval(String)
     */
    /*[deutsch]
     * <p>Wandelt diese Instanz in ein lokales Zeitstempelintervall um. </p>
     *
     * @return  local timestamp interval in system timezone (leap seconds will
     *          always be lost)
     * @since   2.0
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
     * @since   2.0
     * @see     #toLocalInterval()
     */
    /*[deutsch]
     * <p>Wandelt diese Instanz in ein zonales Zeitstempelintervall um. </p>
     *
     * @param   tzid    timezone id
     * @return  zonal timestamp interval in given timezone (leap seconds will
     *          always be lost)
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     * @since   2.0
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
     * @since   2.0
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
     * @since   2.0
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

    /**
     * <p>Yields the nominal duration of this interval in given timezone and units. </p>
     *
     * @param   tz          timezone
     * @param   units       time units to be used in calculation
     * @return  nominal duration
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   3.0
     * @see     #getSimpleDuration()
     * @see     #getRealDuration()
     */
    /*[deutsch]
     * <p>Liefert die nominelle Dauer dieses Intervalls in der angegebenen Zeitzone
     * und den angegebenen Zeiteinheiten. </p>
     *
     * @param   tz          timezone
     * @param   units       time units to be used in calculation
     * @return  nominal duration
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   3.0
     * @see     #getSimpleDuration()
     * @see     #getRealDuration()
     */
    public Duration<IsoUnit> getNominalDuration(
        Timezone tz,
        IsoUnit... units
    ) {

        return this.toZonalInterval(tz.getID()).getDuration(tz, units);

    }

    /**
     * <p>Yields the length of this interval on the POSIX-scale. </p>
     *
     * @return  machine time duration on POSIX-scale
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   2.0
     * @see     #getRealDuration()
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge dieses Intervalls auf der POSIX-Skala. </p>
     *
     * @return  machine time duration on POSIX-scale
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   2.0
     * @see     #getRealDuration()
     */
    public MachineTime<TimeUnit> getSimpleDuration() {

        Moment tsp = this.getTemporalOfOpenEnd();
        boolean max = (tsp == null);

        if (max) { // max reached
            tsp = this.getEnd().getTemporal();
        }

        MachineTime<TimeUnit> result =
            MachineTime.ON_POSIX_SCALE.between(
                this.getTemporalOfClosedStart(),
                tsp);

        if (max) {
            return result.plus(1, TimeUnit.NANOSECONDS);
        }

        return result;

    }

    /**
     * <p>Yields the length of this interval on the UTC-scale. </p>
     *
     * @return  machine time duration on UTC-scale
     * @throws  UnsupportedOperationException if start is before year 1972
     *          or if this interval is infinite
     * @since   2.0
     * @see     #getSimpleDuration()
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge dieses Intervalls auf der UTC-Skala. </p>
     *
     * @return  machine time duration on UTC-scale
     * @throws  UnsupportedOperationException if start is before year 1972
     *          or if this interval is infinite
     * @since   2.0
     * @see     #getSimpleDuration()
     */
    public MachineTime<SI> getRealDuration() {

        Moment tsp = this.getTemporalOfOpenEnd();
        boolean max = (tsp == null);

        if (max) { // max reached
            tsp = this.getEnd().getTemporal();
        }

        MachineTime<SI> result =
            MachineTime.ON_UTC_SCALE.between(
                this.getTemporalOfClosedStart(),
                tsp);

        if (max) {
            return result.plus(1, SI.NANOSECONDS);
        }

        return result;

    }

    /**
     * <p>Moves this interval along the POSIX-axis by given time units. </p>
     *
     * @param   amount  amount of units
     * @param   unit    time unit for moving
     * @return  moved copy of this interval
     */
    public MomentInterval move(
        long amount,
        TimeUnit unit
    ) {

        if (amount == 0) {
            return this;
        }

        Boundary<Moment> s;
        Boundary<Moment> e;

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

        return new MomentInterval(s, e);

    }

    /**
     * <p>Moves this interval along the UTC-axis by given SI-units. </p>
     *
     * @param   amount  amount of units
     * @param   unit    time unit for moving
     * @return  moved copy of this interval
     */
    public MomentInterval move(
        long amount,
        SI unit
    ) {

        if (amount == 0) {
            return this;
        }

        Boundary<Moment> s;
        Boundary<Moment> e;

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

        return new MomentInterval(s, e);

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
    public static MomentInterval parse(
        String text,
        ChronoParser<Moment> parser
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
    public static MomentInterval parse(
        String text,
        ChronoParser<Moment> parser,
        String intervalPattern
    ) throws ParseException {

        ParseLog plog = new ParseLog();
        MomentInterval interval =
            IntervalParser.parseCustom(text, MomentIntervalFactory.INSTANCE, parser, intervalPattern, plog);

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
    public static MomentInterval parse(
        CharSequence text,
        ChronoParser<Moment> parser,
        BracketPolicy policy,
        ParseLog status
    ) {

        return IntervalParser.of(
            MomentIntervalFactory.INSTANCE,
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
    public static MomentInterval parse(
        CharSequence text,
        ChronoParser<Moment> startFormat,
        char separator,
        ChronoParser<Moment> endFormat,
        BracketPolicy policy,
        ParseLog status
    ) {

        return IntervalParser.of(
            MomentIntervalFactory.INSTANCE,
            startFormat,
            endFormat,
            policy,
            separator,
            null
        ).parse(text, status, IsoInterval.extractDefaultAttributes(startFormat));

    }

    /**
     * <p>Interpretes given ISO-conforming text as interval. </p>
     *
     * <p>All styles are supported, namely calendar dates, ordinal dates
     * and week dates, either in basic or in extended format. Mixed date
     * styles for start and end are not allowed however. Furthermore, one
     * of start or end can also be represented by a period string. If not
     * then the end component may exist in an abbreviated form as
     * documented in ISO-8601-paper leaving out higher-order elements
     * like the calendar year (which will be overtaken from the start
     * component instead). In latter case, the timezone offset of the
     * end component is optional, too. Examples for supported formats: </p>
     *
     * <pre>
     *  System.out.println(
     *      MomentInterval.parseISO(
     *          &quot;2012-01-01T14:15Z/2014-06-20T16:00Z&quot;));
     *  // output: [2012-01-01T14:15:00Z/2014-06-20T16:00:00Z]
     *
     *  System.out.println(
     *      MomentInterval.parseISO(
     *          &quot;2012-01-01T14:15Z/08-11T16:00+00:01&quot;));
     *  // output: [2012-01-01T14:15:00Z/2012-08-11T15:00:00Z]
     *
     *  System.out.println(
     *      MomentInterval.parseISO(
     *          &quot;2012-01-01T14:15Z/16:00&quot;));
     *  // output: [2012-01-01T14:15:00Z/2012-01-01T16:00:00Z]
     *
     *  System.out.println(
     *      MomentInterval.parseISO(
     *          &quot;2012-01-01T14:15Z/P2DT1H45M&quot;));
     *  // output: [2012-01-01T14:15:00Z/2012-01-03T16:00:00Z]
     * </pre>
     *
     * <p>This method dynamically creates an appropriate interval format.
     * If performance is more important then a static fixed formatter might
     * be considered. </p>
     *
     * @param   text        text to be parsed
     * @return  parsed interval
     * @throws  IndexOutOfBoundsException if given text is empty
     * @throws  ParseException if the text is not parseable
     * @since   2.0
     * @see     BracketPolicy#SHOW_NEVER
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen ISO-konformen Text als Intervall. </p>
     *
     * <p>Alle Stile werden unterst&uuml;tzt, n&auml;mlich Kalendardatum,
     * Ordinaldatum und Wochendatum, sowohl im Basisformat als auch im
     * erweiterten Format. Gemischte Datumsstile von Start und Ende
     * sind jedoch nicht erlaubt. Au&szlig;erdem darf eine der beiden
     * Komponenten Start und Ende als P-String vorliegen. Wenn nicht, dann
     * darf die Endkomponente auch in einer abgek&uuml;rzten Schreibweise
     * angegeben werden, in der weniger pr&auml;zise Elemente wie das
     * Kalenderjahr ausgelassen und von der Startkomponente &uuml;bernommen
     * werden. In letzterem Fall ist auch der Offset der Endkomponente
     * optional. Beispiele f&uuml;r unterst&uuml;tzte Formate: </p>
     *
     * <pre>
     *  System.out.println(
     *      MomentInterval.parseISO(
     *          &quot;2012-01-01T14:15Z/2014-06-20T16:00Z&quot;));
     *  // Ausgabe: [2012-01-01T14:15:00Z/2014-06-20T16:00:00Z]
     *
     *  System.out.println(
     *      MomentInterval.parseISO(
     *          &quot;2012-01-01T14:15Z/08-11T16:00+00:01&quot;));
     *  // Ausgabe: [2012-01-01T14:15:00Z/2012-08-11T15:00:00Z]
     *
     *  System.out.println(
     *      MomentInterval.parseISO(
     *          &quot;2012-01-01T14:15Z/16:00&quot;));
     *  // Ausgabe: [2012-01-01T14:15:00Z/2012-01-01T16:00:00Z]
     *
     *  System.out.println(
     *      MomentInterval.parseISO(
     *          &quot;2012-01-01T14:15Z/P2DT1H45M&quot;));
     *  // Ausgabe: [2012-01-01T14:15:00Z/2012-01-03T16:00:00Z]
     * </pre>
     *
     * <p>Intern wird das notwendige Intervallformat dynamisch ermittelt. Ist
     * das Antwortzeitverhalten wichtiger, sollte einem statisch initialisierten
     * konstanten Format der Vorzug gegeben werden. </p>
     *
     * @param   text        text to be parsed
     * @return  parsed interval
     * @throws  IndexOutOfBoundsException if given text is empty
     * @throws  ParseException if the text is not parseable
     * @since   2.0
     * @see     BracketPolicy#SHOW_NEVER
     */
    public static MomentInterval parseISO(String text)
        throws ParseException {

        if (text.isEmpty()) {
            throw new IndexOutOfBoundsException("Empty text.");
        }

        // betrifft nur Endkomponente, wenn kein P
        boolean hasT = false;
        int protectedArea = 0;

        // prescan for format analysis
		int start = 0;
		int n = Math.min(text.length(), 83);
        boolean sameFormat = true;
        int componentLength = 0;

        for (int i = 1; i < n; i++) {
            if (text.charAt(i) == '/') {
                if (text.charAt(0) == 'P') {
                    start = i + 1;
                    componentLength = n - i - 1;
                    hasT = true;
                } else if (
                    (i + 1 < n)
                    && (text.charAt(i + 1) == 'P')
                ) {
                    componentLength = i;
                    hasT = true;
                } else {
                    sameFormat = (2 * i + 1 == n) && hasSameOffsets(text, i, n);
                    componentLength = i;
                    protectedArea = n - i - 1;
                    for (int j = i + 1; j < n; j++) {
                        if (text.charAt(j) == 'T') {
                            protectedArea = n - j;
                            hasT = true;
                            break;
                        }
                    }
                }
                break;
            }
        }

        int literals = 0;
        boolean ordinalStyle = false;
        boolean weekStyle = false;
        int timeLength = 0;

        for (int i = start; i < n; i++) {
            char c = text.charAt(i);
            if (c == '/') {
                break;
            } else if ((c == 'T') || (timeLength > 0)) {
                timeLength++;
            } else if (c == '-') {
                literals++;
            } else if (c == 'W') {
                weekStyle = true;
            }
        }

        boolean extended = (literals > 0);

        if (!weekStyle) {
            ordinalStyle = (
                (literals == 1)
                || (
                    (literals == 0)
                    && (componentLength - timeLength == 7)));
        }

        // start format
        ChronoFormatter<Moment> startFormat;

        if (ordinalStyle) {
            startFormat = (extended ? EXT_O : BAS_O);
        } else if (weekStyle) {
            startFormat = (extended ? EXT_W : BAS_W);
        } else {
            startFormat = (extended ? EXT_C : BAS_C);
        }

        // end format
        ChronoFormatter<Moment> endFormat;

        if (sameFormat) {
            endFormat = startFormat;
        } else {
            endFormat =
                abbreviatedFormat(
                    extended, weekStyle, ordinalStyle, protectedArea, hasT);
        }

        // create interval
        return IntervalParser.of(
            MomentIntervalFactory.INSTANCE,
            startFormat,
            endFormat,
            BracketPolicy.SHOW_NEVER,
            '/',
            Moment.axis()
        ).parse(text);

    }

    @Override
    IntervalFactory<Moment, MomentInterval> getFactory() {

        return MomentIntervalFactory.INSTANCE;

    }

    @Override
    MomentInterval getContext() {

        return this;

    }

    private static boolean hasSameOffsets(
        String text,
        int solidus,
        int len
    ) {

        if (text.charAt(solidus - 1) == 'Z') {
            return (text.charAt(len - 1) == 'Z');
        }

        for (int i = solidus - 1; i >= 0; i--) {
            char test = text.charAt(i);
            if ((test == '+') || (test == '-')) {
                int index = i + len - solidus;
                if (index < 0) {
                    break;
                } else {
                    char compare = text.charAt(index);
                    return ((compare == '+') || (compare == '-'));
                }
            }
        }

        return false;

    }

    private static ChronoFormatter<Moment> calendarFormat(boolean extended) {

        ChronoFormatter.Builder<Moment> builder =
            ChronoFormatter.setUp(Moment.class, Locale.ROOT);
        builder.addInteger(YEAR, 4, 9, SignPolicy.SHOW_WHEN_BIG_NUMBER);

        if (extended) {
            builder.addLiteral('-');
        }

        builder.addFixedInteger(MONTH_AS_NUMBER, 2);

        if (extended) {
            builder.addLiteral('-');
        }

        builder.addFixedInteger(DAY_OF_MONTH, 2);
        builder.addLiteral('T');
        addWallTime(builder, extended);
        addOffset(builder, extended);

        return builder.build();

    }

    private static ChronoFormatter<Moment> ordinalFormat(boolean extended) {

        ChronoFormatter.Builder<Moment> builder =
            ChronoFormatter.setUp(Moment.class, Locale.ROOT);
        builder.addInteger(YEAR, 4, 9, SignPolicy.SHOW_WHEN_BIG_NUMBER);

        if (extended) {
            builder.addLiteral('-');
        }

        builder.addFixedInteger(DAY_OF_YEAR, 3).build();
        builder.addLiteral('T');
        addWallTime(builder, extended);
        addOffset(builder, extended);

        return builder.build();

    }

    private static ChronoFormatter<Moment> weekdateFormat(boolean extended) {

        ChronoFormatter.Builder<Moment> builder =
            ChronoFormatter.setUp(Moment.class, Locale.ROOT);
        builder.addInteger(
                YEAR_OF_WEEKDATE,
                4,
                9,
                SignPolicy.SHOW_WHEN_BIG_NUMBER);

        if (extended) {
            builder.addLiteral('-');
        }

        builder.addLiteral('W');
        builder.addFixedInteger(Weekmodel.ISO.weekOfYear(), 2);

        if (extended) {
            builder.addLiteral('-');
        }

        builder.addFixedNumerical(DAY_OF_WEEK, 1).build();
        builder.addLiteral('T');
        addWallTime(builder, extended);
        addOffset(builder, extended);

        return builder.build();

    }

    private static ChronoFormatter<Moment> abbreviatedFormat(
        boolean extended,
        boolean weekStyle,
        boolean ordinalStyle,
        int protectedArea,
        boolean hasT
    ) {

        ChronoFormatter.Builder<Moment> builder =
            ChronoFormatter.setUp(Moment.class, Locale.ROOT);

        ChronoElement<Integer> year = (weekStyle ? YEAR_OF_WEEKDATE : YEAR);
        if (extended) {
            int p = (ordinalStyle ? 3 : 5) + protectedArea;
            builder.startSection(PROTECTED_CHARACTERS, p);
            builder.addCustomized(
                year,
                NoopPrinter.NOOP,
                (weekStyle ? YearParser.YEAR_OF_WEEKDATE : YearParser.YEAR));
        } else {
            int p = (ordinalStyle ? 3 : 4) + protectedArea;
            builder.startSection(PROTECTED_CHARACTERS, p);
            builder.addInteger(year, 4, 9, SignPolicy.SHOW_WHEN_BIG_NUMBER);
        }
        builder.endSection();

        if (weekStyle) {
            builder.startSection(PROTECTED_CHARACTERS, 1 + protectedArea);
            builder.addCustomized(
                Weekmodel.ISO.weekOfYear(),
                NoopPrinter.NOOP,
                extended
                    ? FixedNumParser.EXTENDED_WEEK_OF_YEAR
                    : FixedNumParser.BASIC_WEEK_OF_YEAR);
            builder.endSection();
            builder.startSection(PROTECTED_CHARACTERS, protectedArea);
            builder.addFixedNumerical(DAY_OF_WEEK, 1);
            builder.endSection();
        } else if (ordinalStyle) {
            builder.startSection(PROTECTED_CHARACTERS, protectedArea);
            builder.addFixedInteger(DAY_OF_YEAR, 3);
            builder.endSection();
        } else {
            builder.startSection(PROTECTED_CHARACTERS, 2 + protectedArea);
            if (extended) {
                builder.addCustomized(
                    MONTH_AS_NUMBER,
                    NoopPrinter.NOOP,
                    FixedNumParser.CALENDAR_MONTH);
            } else {
                builder.addFixedInteger(MONTH_AS_NUMBER, 2);
            }
            builder.endSection();
            builder.startSection(PROTECTED_CHARACTERS, protectedArea);
            builder.addFixedInteger(DAY_OF_MONTH, 2);
            builder.endSection();
        }

        if (hasT) {
            builder.addLiteral('T');
        }

        addWallTime(builder, extended);

        builder.startOptionalSection();
        addOffset(builder, extended);
        builder.endSection();

        return builder.build();

    }

    private static <T extends ChronoEntity<T>> void addWallTime(
        ChronoFormatter.Builder<T> builder,
        boolean extended
    ) {

        builder.addCustomized(
            PlainTime.COMPONENT,
            extended
                ? Iso8601Format.EXTENDED_WALL_TIME
                : Iso8601Format.BASIC_WALL_TIME);

    }

    private static <T extends ChronoEntity<T>> void addOffset(
        ChronoFormatter.Builder<T> builder,
        boolean extended
    ) {

        builder.addTimezoneOffset(
            DisplayMode.SHORT,
            extended,
            Collections.singletonList("Z"));

    }

    /**
     * @serialData  Uses
     *              <a href="../../../serialized-form.html#net.time4j.range.SPX">
     *              a dedicated serialization form</a> as proxy. The first byte
     *              contains the type-ID {@code 35} in the six most significant
     *              bits. The next bytes represent the start and the end
     *              boundary.
     *
     * Schematic algorithm:
     *
     * <pre>
     *  int header = 35;
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
