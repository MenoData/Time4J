/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
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

import net.time4j.ClockUnit;
import net.time4j.Duration;
import net.time4j.IsoUnit;
import net.time4j.Moment;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.Weekmodel;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.TimeLine;
import net.time4j.format.Attributes;
import net.time4j.format.expert.SignPolicy;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.ChronoParser;
import net.time4j.format.expert.Iso8601Format;
import net.time4j.format.expert.ParseLog;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Locale;

import static net.time4j.PlainDate.DAY_OF_MONTH;
import static net.time4j.PlainDate.DAY_OF_WEEK;
import static net.time4j.PlainDate.DAY_OF_YEAR;
import static net.time4j.PlainDate.MONTH_AS_NUMBER;
import static net.time4j.PlainDate.YEAR;
import static net.time4j.PlainDate.YEAR_OF_WEEKDATE;
import static net.time4j.range.IntervalEdge.CLOSED;
import static net.time4j.range.IntervalEdge.OPEN;


/**
 * <p>Defines a timestamp interval on local timeline. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 */
/*[deutsch]
 * <p>Definiert ein Zeitstempelintervall auf dem lokalen Zeitstrahl. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 */
public final class TimestampInterval
    extends IsoInterval<PlainTimestamp, TimestampInterval>
    implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = -3965530927182499606L;

    private static final ChronoFormatter<PlainTimestamp> EXT_O =
        ordinalFormat(true);
    private static final ChronoFormatter<PlainTimestamp> EXT_W =
        weekdateFormat(true);
    private static final ChronoFormatter<PlainTimestamp> BAS_O =
        ordinalFormat(false);
    private static final ChronoFormatter<PlainTimestamp> BAS_W =
        weekdateFormat(false);
    private static final Comparator<ChronoInterval<PlainTimestamp>> COMPARATOR =
        new IntervalComparator<PlainTimestamp>(false, PlainTimestamp.axis());

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
    public static Comparator<ChronoInterval<PlainTimestamp>> comparator() {

        return COMPARATOR;

    }

    /**
     * <p>Creates a finite half-open interval between given time points. </p>
     *
     * @param   start   timestamp of lower boundary (inclusive)
     * @param   end     timestamp of upper boundary (exclusive)
     * @return  new timestamp interval
     * @throws  IllegalArgumentException if start is after end
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt ein begrenztes halb-offenes Intervall zwischen den
     * angegebenen Zeitpunkten. </p>
     *
     * @param   start   timestamp of lower boundary (inclusive)
     * @param   end     timestamp of upper boundary (exclusive)
     * @return  new timestamp interval
     * @throws  IllegalArgumentException if start is after end
     * @since   2.0
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
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt ein unbegrenztes halb-offenes Intervall ab dem angegebenen
     * Startzeitpunkt. </p>
     *
     * @param   start       timestamp of lower boundary (inclusive)
     * @return  new timestamp interval
     * @since   2.0
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
     * @since   2.0
     */
    /*[deutsch]
     * <p>Erzeugt ein unbegrenztes offenes Intervall bis zum angegebenen
     * Endzeitpunkt. </p>
     *
     * @param   end     timestamp of upper boundary (exclusive)
     * @return  new timestamp interval
     * @since   2.0
     */
    public static TimestampInterval until(PlainTimestamp end) {

        Boundary<PlainTimestamp> past = Boundary.infinitePast();
        return new TimestampInterval(past, Boundary.of(OPEN, end));

    }

    /**
     * <p>Combines this local timestamp interval with the timezone offset
     * UTC+00:00 to a global UTC-interval. </p>
     *
     * @return  global timestamp interval interpreted at offset UTC+00:00
     * @since   2.0
     * @see     #at(ZonalOffset)
     */
    /*[deutsch]
     * <p>Kombiniert dieses lokale Zeitstempelintervall mit UTC+00:00 zu
     * einem globalen UTC-Intervall. </p>
     *
     * @return  global timestamp interval interpreted at offset UTC+00:00
     * @since   2.0
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
     * @since   2.0
     * @see     #atUTC()
     * @see     #in(Timezone)
     */
    /*[deutsch]
     * <p>Kombiniert dieses lokale Zeitstempelintervall mit dem angegebenen
     * Zeitzonen-Offset zu einem globalen UTC-Intervall. </p>
     *
     * @param   offset  timezone offset
     * @return  global timestamp interval interpreted at given offset
     * @since   2.0
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
     * @since   2.0
     * @see     Timezone#ofSystem()
     */
    /*[deutsch]
     * <p>Kombiniert dieses lokale Zeitstempelintervall mit der System-Zeitzone
     * zu einem globalen UTC-Intervall. </p>
     *
     * @return  global timestamp interval interpreted in system timezone
     * @since   2.0
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
     * @since   2.0
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
     * @since   2.0
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
     * @since   2.0
     */
    /*[deutsch]
     * <p>Kombiniert dieses lokale Zeitstempelintervall mit der angegebenen
     * Zeitzone zu einem globalen UTC-Intervall. </p>
     *
     * @param   tz      timezone
     * @return  global timestamp intervall interpreted in given timezone
     * @since   2.0
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
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge dieses Intervalls in den angegebenen
     * Zeiteinheiten. </p>
     *
     * @param   units   time units to be used in calculation
     * @return  duration in given units
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   2.0
     */
    public <U extends IsoUnit> Duration<U> getDuration(U... units) {

        PlainTimestamp tsp = this.getTemporalOfOpenEnd();
        boolean max = (tsp == null);

        if (max) { // max reached
            tsp = this.getEnd().getTemporal();
        }

        Duration<U> result =
            Duration.in(units).between(this.getTemporalOfClosedStart(), tsp);

        if (max) {
            for (U unit : units) {
                if (unit.equals(ClockUnit.NANOS)) {
                    return result.plus(1, unit);
                }
            }
        }

        return result;

    }

    /**
     * <p>Yields the length of this interval in given units and applies
     * a timezone offset correction . </p>
     *
     * @param   tz      timezone
     * @param   units   time units to be used in calculation
     * @return  duration in given units including a zonal correction
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge dieses Intervalls in den angegebenen
     * Zeiteinheiten und wendet eine Zeitzonenkorrektur an. </p>
     *
     * @param   tz      timezone
     * @param   units   time units to be used in calculation
     * @return  duration in given units including a zonal correction
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   2.0
     */
    public Duration<IsoUnit> getDuration(
        Timezone tz,
        IsoUnit... units
    ) {

        PlainTimestamp tsp = this.getTemporalOfOpenEnd();
        boolean max = (tsp == null);

        if (max) { // max reached
            tsp = this.getEnd().getTemporal();
        }

        Duration<IsoUnit> result =
            Duration.in(tz, units).between(
                this.getTemporalOfClosedStart(),
                tsp);

        if (max) {
            for (IsoUnit unit : units) {
                if (unit.equals(ClockUnit.NANOS)) {
                    return result.plus(1, unit);
                }
            }
        }

        return result;

    }

    /**
     * <p>Moves this interval along the time axis by given units. </p>
     *
     * @param   amount  amount of units
     * @param   unit    time unit for moving
     * @return  moved copy of this interval
     */
    public TimestampInterval move(
        long amount,
        IsoUnit unit
    ) {

        if (amount == 0) {
            return this;
        }

        Boundary<PlainTimestamp> s;
        Boundary<PlainTimestamp> e;

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

        return new TimestampInterval(s, e);

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
    public static TimestampInterval parse(
        String text,
        ChronoParser<PlainTimestamp> parser
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
    public static TimestampInterval parse(
        String text,
        ChronoParser<PlainTimestamp> parser,
        String intervalPattern
    ) throws ParseException {

        ParseLog plog = new ParseLog();
        TimestampInterval interval =
            IntervalParser.parseCustom(text, TimestampIntervalFactory.INSTANCE, parser, intervalPattern, plog);

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
    public static TimestampInterval parse(
        CharSequence text,
        ChronoParser<PlainTimestamp> parser,
        BracketPolicy policy,
        ParseLog status
    ) {

        return IntervalParser.of(
            TimestampIntervalFactory.INSTANCE,
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
    public static TimestampInterval parse(
        CharSequence text,
        ChronoParser<PlainTimestamp> startFormat,
        char separator,
        ChronoParser<PlainTimestamp> endFormat,
        BracketPolicy policy,
        ParseLog status
    ) {

        return IntervalParser.of(
            TimestampIntervalFactory.INSTANCE,
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
     * component instead). Examples for supported formats: </p>
     *
     * <pre>
     *  System.out.println(
     *      TimestampInterval.parseISO(
     *          &quot;2012-01-01T14:15/2014-06-20T16:00&quot;));
     *  // output: [2012-01-01T14:15/2014-06-20T16:00]
     *
     *  System.out.println(
     *      TimestampInterval.parseISO(
     *          &quot;2012-01-01T14:15/08-11T16:00&quot;));
     *  // output: [2012-01-01T14:15/2012-08-11T16:00]
     *
     *  System.out.println(
     *      TimestampInterval.parseISO(
     *          &quot;2012-01-01T14:15/16:00&quot;));
     *  // output: [2012-01-01T14:15/2012-01-01T16:00]
     *
     *  System.out.println(
     *      TimestampInterval.parseISO(
     *          &quot;2012-01-01T14:15/P2DT1H45M&quot;));
     *  // output: [2012-01-01T14:15/2012-01-03T16:00]
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
     * werden. Beispiele f&uuml;r unterst&uuml;tzte Formate: </p>
     *
     * <pre>
     *  System.out.println(
     *      TimestampInterval.parseISO(
     *          &quot;2012-01-01T14:15/2014-06-20T16:00&quot;));
     *  // output: [2012-01-01T14:15/2014-06-20T16:00]
     *
     *  System.out.println(
     *      TimestampInterval.parseISO(
     *          &quot;2012-01-01T14:15/08-11T16:00&quot;));
     *  // output: [2012-01-01T14:15/2012-08-11T16:00]
     *
     *  System.out.println(
     *      TimestampInterval.parseISO(
     *          &quot;2012-01-01T14:15/16:00&quot;));
     *  // output: [2012-01-01T14:15/2012-01-01T16:00]
     *
     *  System.out.println(
     *      TimestampInterval.parseISO(
     *          &quot;2012-01-01T14:15/P2DT1H45M&quot;));
     *  // output: [2012-01-01T14:15/2012-01-03T16:00]
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
    public static TimestampInterval parseISO(String text)
        throws ParseException {

        if (text.isEmpty()) {
            throw new IndexOutOfBoundsException("Empty text.");
        }

        // prescan for format analysis
		int start = 0;
		int n = Math.min(text.length(), 71);
        boolean sameFormat = true;
        int componentLength = 0;

        for (int i = 1; i < n; i++) {
            if (text.charAt(i) == '/') {
                if (text.charAt(0) == 'P') {
                    start = i + 1;
                    componentLength = n - i - 1;
                } else if (
                    (i + 1 < n)
                    && (text.charAt(i + 1) == 'P')
                ) {
                    componentLength = i;
                } else {
                    sameFormat = (2 * i + 1 == n);
                    componentLength = i;
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
            } else if (c == '-') {
                literals++;
            } else if ((c == 'T') || (timeLength > 0)) {
                timeLength++;
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
        ChronoFormatter<PlainTimestamp> startFormat;

        if (ordinalStyle) {
            startFormat = (extended ? EXT_O : BAS_O);
        } else if (weekStyle) {
            startFormat = (extended ? EXT_W : BAS_W);
        } else if (extended) {
            startFormat = Iso8601Format.EXTENDED_DATE_TIME;
        } else {
            startFormat = Iso8601Format.BASIC_DATE_TIME;
        }

        // end format
        ChronoFormatter<PlainTimestamp> endFormat;

        if (sameFormat) {
            endFormat = startFormat;
        } else {
            boolean hasT = true;
            if (n - 1 - componentLength < timeLength) {
                timeLength--; // end component without date or T-symbol
                hasT = false;
            }
            endFormat =
                abbreviatedFormat(
                    extended, weekStyle, ordinalStyle, timeLength, hasT);
        }

        // create interval
        return IntervalParser.of(
            TimestampIntervalFactory.INSTANCE,
            startFormat,
            endFormat,
            BracketPolicy.SHOW_NEVER,
            '/',
            PlainTimestamp.axis()
        ).parse(text);

    }

    @Override
    TimeLine<PlainTimestamp> getTimeLine() {

        return PlainTimestamp.axis();

    }

    @Override
    IntervalFactory<PlainTimestamp, TimestampInterval> getFactory() {

        return TimestampIntervalFactory.INSTANCE;

    }

    @Override
    TimestampInterval getContext() {

        return this;

    }

    private static ChronoFormatter<PlainTimestamp>
    ordinalFormat(boolean extended) {

        ChronoFormatter.Builder<PlainTimestamp> builder =
            ChronoFormatter.setUp(PlainTimestamp.class, Locale.ROOT);
        builder.addInteger(YEAR, 4, 9, SignPolicy.SHOW_WHEN_BIG_NUMBER);

        if (extended) {
            builder.addLiteral('-');
        }

        builder.addFixedInteger(DAY_OF_YEAR, 3).build();
        builder.addLiteral('T');
        builder.addCustomized(
            PlainTime.COMPONENT,
            extended
                ? Iso8601Format.EXTENDED_WALL_TIME
                : Iso8601Format.BASIC_WALL_TIME);

        return builder.build();

    }

    private static ChronoFormatter<PlainTimestamp>
    weekdateFormat(boolean extended) {

        ChronoFormatter.Builder<PlainTimestamp> builder =
            ChronoFormatter.setUp(PlainTimestamp.class, Locale.ROOT);
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
        builder.addCustomized(
            PlainTime.COMPONENT,
            extended
                ? Iso8601Format.EXTENDED_WALL_TIME
                : Iso8601Format.BASIC_WALL_TIME);

        return builder.build();

    }

    private static ChronoFormatter<PlainTimestamp> abbreviatedFormat(
        boolean extended,
        boolean weekStyle,
        boolean ordinalStyle,
        int timeLength,
        boolean hasT
    ) {

        ChronoFormatter.Builder<PlainTimestamp> builder =
            ChronoFormatter.setUp(PlainTimestamp.class, Locale.ROOT);

        ChronoElement<Integer> year = (weekStyle ? YEAR_OF_WEEKDATE : YEAR);
        if (extended) {
            int p = (ordinalStyle ? 3 : 5) + timeLength;
            builder.startSection(Attributes.PROTECTED_CHARACTERS, p);
            builder.addCustomized(
                year,
                NoopPrinter.NOOP,
                (weekStyle ? YearParser.YEAR_OF_WEEKDATE : YearParser.YEAR));
        } else {
            int p = (ordinalStyle ? 3 : 4) + timeLength;
            builder.startSection(Attributes.PROTECTED_CHARACTERS, p);
            builder.addInteger(year, 4, 9, SignPolicy.SHOW_WHEN_BIG_NUMBER);
        }
        builder.endSection();

        if (weekStyle) {
            builder.startSection(
                Attributes.PROTECTED_CHARACTERS,
                1 + timeLength);
            builder.addCustomized(
                Weekmodel.ISO.weekOfYear(),
                NoopPrinter.NOOP,
                extended
                    ? FixedNumParser.EXTENDED_WEEK_OF_YEAR
                    : FixedNumParser.BASIC_WEEK_OF_YEAR);
            builder.endSection();
            builder.startSection(Attributes.PROTECTED_CHARACTERS, timeLength);
            builder.addFixedNumerical(DAY_OF_WEEK, 1);
            builder.endSection();
        } else if (ordinalStyle) {
            builder.startSection(Attributes.PROTECTED_CHARACTERS, timeLength);
            builder.addFixedInteger(DAY_OF_YEAR, 3);
            builder.endSection();
        } else {
            builder.startSection(
                Attributes.PROTECTED_CHARACTERS,
                2 + timeLength);
            if (extended) {
                builder.addCustomized(
                    MONTH_AS_NUMBER,
                    NoopPrinter.NOOP,
                    FixedNumParser.CALENDAR_MONTH);
            } else {
                builder.addFixedInteger(MONTH_AS_NUMBER, 2);
            }
            builder.endSection();
            builder.startSection(Attributes.PROTECTED_CHARACTERS, timeLength);
            builder.addFixedInteger(DAY_OF_MONTH, 2);
            builder.endSection();
        }

        if (hasT) {
            builder.addLiteral('T');
        }

        builder.addCustomized(
            PlainTime.COMPONENT,
            extended
                ? Iso8601Format.EXTENDED_WALL_TIME
                : Iso8601Format.BASIC_WALL_TIME);

        return builder.build();

    }

    /**
     * @serialData  Uses
     *              <a href="../../../serialized-form.html#net.time4j.range.SPX">
     *              a dedicated serialization form</a> as proxy. The first byte
     *              contains the type-ID {@code 34} in the six most significant
     *              bits. The next bytes represent the start and the end
     *              boundary.
     *
     * Schematic algorithm:
     *
     * <pre>
     *  int header = 34;
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
