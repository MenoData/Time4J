/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (DateInterval.java) is part of project Time4J.
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

import net.time4j.CalendarUnit;
import net.time4j.Duration;
import net.time4j.Iso8601Format;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.Weekmodel;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.TimeLine;
import net.time4j.format.Attributes;
import net.time4j.format.ChronoFormatter;
import net.time4j.format.ParseLog;
import net.time4j.format.SignPolicy;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Locale;

import static net.time4j.PlainDate.DAY_OF_MONTH;
import static net.time4j.PlainDate.DAY_OF_WEEK;
import static net.time4j.PlainDate.DAY_OF_YEAR;
import static net.time4j.PlainDate.MONTH_AS_NUMBER;
import static net.time4j.PlainDate.YEAR;
import static net.time4j.PlainDate.YEAR_OF_WEEKDATE;
import static net.time4j.range.IntervalEdge.CLOSED;


/**
 * <p>Defines a date interval. </p>
 *
 * @author  Meno Hochschild
 * @since   1.3
 */
/*[deutsch]
 * <p>Definiert ein Datumsintervall. </p>
 *
 * @author  Meno Hochschild
 * @since   1.3
 */
public final class DateInterval
    extends ChronoInterval<PlainDate>
    implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = 1L;

    //~ Konstruktoren -----------------------------------------------------

    // package-private
    DateInterval(
        Boundary<PlainDate> start,
        Boundary<PlainDate> end
    ) {
        super(start, end);

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a closed interval between given dates. </p>
     *
     * @param   start   date of lower boundary (inclusive)
     * @param   end     date of upper boundary (inclusive)
     * @return  new date interval
     * @throws  IllegalArgumentException if start is after end
     * @since   1.3
     */
    /*[deutsch]
     * <p>Erzeugt ein geschlossenes Intervall zwischen den angegebenen
     * Datumswerten. </p>
     *
     * @param   start   date of lower boundary (inclusive)
     * @param   end     date of upper boundary (inclusive)
     * @return  new date interval
     * @throws  IllegalArgumentException if start is after end
     * @since   1.3
     */
    public static DateInterval between(
        PlainDate start,
        PlainDate end
    ) {

        return new DateInterval(
            Boundary.of(CLOSED, start),
            Boundary.of(CLOSED, end));

    }

    /**
     * <p>Creates an infinite interval since given start date. </p>
     *
     * @param   start   date of lower boundary (inclusive)
     * @return  new date interval
     * @since   1.3
     */
    /*[deutsch]
     * <p>Erzeugt ein unbegrenztes Intervall ab dem angegebenen
     * Startdatum. </p>
     *
     * @param   start   date of lower boundary (inclusive)
     * @return  new date interval
     * @since   1.3
     */
    public static DateInterval since(PlainDate start) {

        Boundary<PlainDate> future = Boundary.infiniteFuture();
        return new DateInterval(Boundary.of(CLOSED, start), future);

    }

    /**
     * <p>Creates an infinite interval until given end date. </p>
     *
     * @param   end     date of upper boundary (inclusive)
     * @return  new date interval
     * @since   1.3
     */
    /*[deutsch]
     * <p>Erzeugt ein unbegrenztes Intervall bis zum angegebenen
     * Endedatum. </p>
     *
     * @param   end     date of upper boundary (inclusive)
     * @return  new date interval
     * @since   1.3
     */
    public static DateInterval until(PlainDate end) {

        Boundary<PlainDate> past = Boundary.infinitePast();
        return new DateInterval(past, Boundary.of(CLOSED, end));

    }

    /**
     * <p>Creates a closed interval including only given date. </p>
     *
     * @param   date    single contained date
     * @return  new date interval
     * @since   1.3
     */
    /*[deutsch]
     * <p>Erzeugt ein geschlossenes Intervall, das nur das angegebene
     * Datum enth&auml;lt. </p>
     *
     * @param   date    single contained date
     * @return  new date interval
     * @since   1.3
     */
    public static DateInterval atomic(PlainDate date) {

        return between(date, date);

    }

    @Override
    public DateInterval withStart(Boundary<PlainDate> boundary) {

        return new DateInterval(boundary, this.getEnd());

    }

    @Override
    public DateInterval withEnd(Boundary<PlainDate> boundary) {

        return new DateInterval(this.getStart(), boundary);

    }

    @Override
    public DateInterval withStart(PlainDate temporal) {

        Boundary<PlainDate> boundary =
            Boundary.of(this.getStart().getEdge(), temporal);
        return new DateInterval(boundary, this.getEnd());

    }

    @Override
    public DateInterval withEnd(PlainDate temporal) {

        Boundary<PlainDate> boundary =
            Boundary.of(this.getEnd().getEdge(), temporal);
        return new DateInterval(this.getStart(), boundary);

    }

    /**
     * <p>Removes the upper boundary from this interval. </p>
     *
     * @return  changed copy of this interval excluding upper boundary
     */
    /*[deutsch]
     * <p>Nimmt die obere Grenze von diesem Intervall aus. </p>
     *
     * @return  changed copy of this interval excluding upper boundary
     */
    public DateInterval withOpenEnd() {

        if (this.getEnd().isInfinite()) {
            return this;
        }

        Boundary<PlainDate> boundary =
            Boundary.of(IntervalEdge.OPEN, this.getEnd().getTemporal());
        return this.withEnd(boundary);

    }

    /**
     * <p>Converts this instance to a timestamp interval with
     * dates from midnight to midnight. </p>
     *
     * <p>The resulting interval is half-open if this interval is finite. </p>
     *
     * @return  timestamp interval (from midnight to midnight)
     * @since   1.3
     */
    /*[deutsch]
     * <p>Wandelt diese Instanz in ein Zeitstempelintervall
     * mit Datumswerten von Mitternacht zu Mitternacht um. </p>
     *
     * <p>Das Ergebnisintervall ist halb-offen, wenn dieses Intervall
     * endlich ist. </p>
     *
     * @return  timestamp interval (from midnight to midnight)
     * @since   1.3
     */
    public TimestampInterval toFullDays() {

        Boundary<PlainTimestamp> b1;
        Boundary<PlainTimestamp> b2;

        if (this.getStart().isInfinite()) {
            b1 = Boundary.infinitePast();
        } else {
            PlainDate d1 = this.getStart().getTemporal();
            PlainTimestamp t1;
            if (this.getStart().isOpen()) {
                t1 = d1.at(PlainTime.midnightAtEndOfDay());
            } else {
                t1 = d1.atStartOfDay();
            }
            b1 = Boundary.of(IntervalEdge.CLOSED, t1);
        }

        if (this.getEnd().isInfinite()) {
            b2 = Boundary.infiniteFuture();
        } else {
            PlainDate d2 = this.getEnd().getTemporal();
            PlainTimestamp t2;
            if (this.getEnd().isOpen()) {
                t2 = d2.atStartOfDay();
            } else {
                t2 = d2.at(PlainTime.midnightAtEndOfDay());
            }
            b2 = Boundary.of(IntervalEdge.OPEN, t2);
        }

        return new TimestampInterval(b1, b2);

    }

    /**
     * <p>Yields the length of this interval in days. </p>
     *
     * @return  duration in days as long primitive
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   1.3
     * @see     #getDurationInYearsMonthsDays()
     * @see     #getDuration(CalendarUnit[]) getDuration(CalendarUnit...)
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge dieses Intervalls in Tagen. </p>
     *
     * @return  duration in days as long primitive
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   1.3
     * @see     #getDurationInYearsMonthsDays()
     * @see     #getDuration(CalendarUnit[]) getDuration(CalendarUnit...)
     */
    public long getLengthInDays() {

        if (this.isFinite()) {
            long days = CalendarUnit.DAYS.between(
                this.getStart().getTemporal(),
                this.getEnd().getTemporal());
            if (this.getStart().isOpen()) {
                days--;
            }
            if (this.getEnd().isClosed()) {
                days++;
            }
            return days;
        } else {
            throw new UnsupportedOperationException(
                "An infinite interval has no finite duration.");
        }

    }

    /**
     * <p>Yields the length of this interval in years, months and days. </p>
     *
     * @return  duration in years, months and days
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   1.3
     * @see     #getLengthInDays()
     * @see     #getDuration(CalendarUnit[]) getDuration(CalendarUnit...)
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge dieses Intervalls in Jahren, Monaten und
     * Tagen. </p>
     *
     * @return  duration in years, months and days
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   1.3
     * @see     #getLengthInDays()
     * @see     #getDuration(CalendarUnit[]) getDuration(CalendarUnit...)
     */
    public Duration<CalendarUnit> getDurationInYearsMonthsDays() {

        return Duration.inYearsMonthsDays().between(
            this.getTemporalOfClosedStart(),
            this.getTemporalOfOpenEnd()
        );

    }

    /**
     * <p>Yields the length of this interval in given calendrical units. </p>
     *
     * @param   units   calendrical units as calculation base
     * @return  duration in given units
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   1.3
     * @see     #getLengthInDays()
     * @see     #getDurationInYearsMonthsDays()
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge dieses Intervalls in den angegebenen
     * kalendarischen Zeiteinheiten. </p>
     *
     * @param   units   calendrical units as calculation base
     * @return  duration in given units
     * @throws  UnsupportedOperationException if this interval is infinite
     * @since   1.3
     * @see     #getLengthInDays()
     * @see     #getDurationInYearsMonthsDays()
     */
    public Duration<CalendarUnit> getDuration(CalendarUnit... units) {

        return Duration.in(units).between(
            this.getTemporalOfClosedStart(),
            this.getTemporalOfOpenEnd()
        );

    }

    /**
     * <p>Interpretes given text as interval. </p>
     *
     * <p>Example: </p>
     *
     * <pre>
     *  System.out.println(
     *      &quot;20120101/20140620&quot,
     *      Iso8601Format.BASIC_CALENDAR_DATE));
     *  // output: [2012-01-01/2014-06-20]
     * </pre>
     *
     * @param   text        text to be parsed
     * @param   formatter   format object for parsing start and end boundaries
     * @return  parsed interval
     * @throws  IndexOutOfBoundsException if the text is empty
     * @throws  ParseException if the text is not parseable
     * @since   1.3
     * @see     BracketPolicy#SHOW_WHEN_NON_STANDARD
     * @see     Iso8601Format
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text als Intervall. </p>
     *
     * <p>Beispiel: </p>
     *
     * <pre>
     *  System.out.println(
     *      &quot;20120101/20140620&quot,
     *      Iso8601Format.BASIC_CALENDAR_DATE));
     *  // Ausgabe: [2012-01-01/2014-06-20]
     * </pre>
     *
     * @param   text        text to be parsed
     * @param   formatter   format object for parsing start and end boundaries
     * @return  parsed interval
     * @throws  IndexOutOfBoundsException if the text is empty
     * @throws  ParseException if the text is not parseable
     * @since   1.3
     * @see     BracketPolicy#SHOW_WHEN_NON_STANDARD
     * @see     Iso8601Format
     */
    public static DateInterval parse(
        String text,
        ChronoFormatter<PlainDate> formatter
    ) throws ParseException {

        return IntervalParser.of(
             DateIntervalFactory.INSTANCE,
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
     * @see     #parse(String, ChronoFormatter)
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
     * @see     #parse(String, ChronoFormatter)
     */
    public static DateInterval parse(
        CharSequence text,
        ChronoFormatter<PlainDate> formatter,
        BracketPolicy policy,
        ParseLog status
    ) {

        return IntervalParser.of(
             DateIntervalFactory.INSTANCE,
             formatter,
             policy
        ).parse(text, status, formatter.getDefaultAttributes());

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
     *      DateInterval.parseISO(&quot;2012-01-01/2014-06-20&quot;));
     *  // output: [2012-01-01/2014-06-20]
     *
     *  System.out.println(DateInterval.parseISO(&quot;2012-01-01/08-11&quot;));
     *  // output: [2012-01-01/2012-08-11]
     *
     *  System.out.println(DateInterval.parseISO(&quot;2012-W01-1/W06-4&quot;));
     *  // output: [2012-01-02/2012-02-09]
     *
     *  System.out.println(DateInterval.parseISO(&quot;2012-001/366&quot;));
     *  // output: [2012-01-01/2012-12-31]
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
     * @since   1.3
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
     *      DateInterval.parseISO(&quot;2012-01-01/2014-06-20&quot;));
     *  // Ausgabe: [2012-01-01/2014-06-20]
     *
     *  System.out.println(DateInterval.parseISO(&quot;2012-01-01/08-11&quot;));
     *  // Ausgabe: [2012-01-01/2012-08-11]
     *
     *  System.out.println(DateInterval.parseISO(&quot;2012-W01-1/W06-4&quot;));
     *  // Ausgabe: [2012-01-02/2012-02-09]
     *
     *  System.out.println(DateInterval.parseISO(&quot;2012-001/366&quot;));
     *  // Ausgabe: [2012-01-01/2012-12-31]
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
     * @since   1.3
     * @see     BracketPolicy#SHOW_NEVER
     */
    public static DateInterval parseISO(String text) throws ParseException {

        if (text.isEmpty()) {
            throw new IndexOutOfBoundsException("Empty text.");
        }

        // prescan for format analysis
		int start = 0;
		int n = Math.min(text.length(), 33);
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

        for (int i = start; i < n; i++) {
            char c = text.charAt(i);
            if (c == '-') {
                literals++;
            } else if (c == 'W') {
                weekStyle = true;
                break;
            } else if (c == '/') {
                break;
            }
        }

        boolean extended = (literals > 0);

        if (!weekStyle) {
            ordinalStyle = (
                (literals == 1)
                || ((literals == 0) && (componentLength == 7)));
        }

        // start format
        ChronoFormatter<PlainDate> startFormat;

        if (extended) {
            if (ordinalStyle) {
                startFormat = Iso8601Format.EXTENDED_ORDINAL_DATE;
            } else if (weekStyle) {
                startFormat = Iso8601Format.EXTENDED_WEEK_DATE;
            } else {
                startFormat = Iso8601Format.EXTENDED_CALENDAR_DATE;
            }
        } else {
            if (ordinalStyle) {
                startFormat = Iso8601Format.BASIC_ORDINAL_DATE;
            } else if (weekStyle) {
                startFormat = Iso8601Format.BASIC_WEEK_DATE;
            } else {
                startFormat = Iso8601Format.BASIC_CALENDAR_DATE;
            }
        }

        // end format
        ChronoFormatter<PlainDate> endFormat;

        if (sameFormat) {
            endFormat = startFormat;
        } else{
            endFormat = abbreviatedFormat(extended, weekStyle, ordinalStyle);
        }

        // create interval
        return IntervalParser.of(
             DateIntervalFactory.INSTANCE,
             startFormat,
             endFormat,
             BracketPolicy.SHOW_NEVER
        ).parse(text);

    }

    @Override
    protected TimeLine<PlainDate> getTimeLine() {

        return PlainDate.axis();

    }

    private static ChronoFormatter<PlainDate> abbreviatedFormat(
        boolean extended,
        boolean weekStyle,
        boolean ordinalStyle
    ) {

        ChronoFormatter.Builder<PlainDate> builder =
            ChronoFormatter.setUp(PlainDate.class, Locale.ROOT);

        ChronoElement<Integer> year = (weekStyle ? YEAR_OF_WEEKDATE : YEAR);
        if (extended) {
            int p = (ordinalStyle ? 3 : 5);
            builder.startSection(Attributes.PROTECTED_CHARACTERS, p);
            builder.addCustomized(
                year,
                NoopPrinter.NOOP,
                (weekStyle ? YearParser.YEAR_OF_WEEKDATE : YearParser.YEAR));
        } else {
            int p = (ordinalStyle ? 3 : 4);
            builder.startSection(Attributes.PROTECTED_CHARACTERS, p);
            builder.addInteger(year, 4, 9, SignPolicy.SHOW_WHEN_BIG_NUMBER);
        }
        builder.endSection();

        if (weekStyle) {
            builder.startSection(Attributes.PROTECTED_CHARACTERS, 1);
            builder.addCustomized(
                Weekmodel.ISO.weekOfYear(),
                NoopPrinter.NOOP,
                extended
                    ? FixedNumParser.EXTENDED_WEEK_OF_YEAR
                    : FixedNumParser.BASIC_WEEK_OF_YEAR);
            builder.endSection();
            builder.addFixedNumerical(DAY_OF_WEEK, 1);
        } else if (ordinalStyle) {
            builder.addFixedInteger(DAY_OF_YEAR, 3);
        } else {
            builder.startSection(Attributes.PROTECTED_CHARACTERS, 2);
            if (extended) {
                builder.addCustomized(
                    MONTH_AS_NUMBER,
                    NoopPrinter.NOOP,
                    FixedNumParser.CALENDAR_MONTH);
            } else {
                builder.addFixedInteger(MONTH_AS_NUMBER, 2);
            }
            builder.endSection();
            builder.addFixedInteger(DAY_OF_MONTH, 2);
        }

        return builder.build();

    }

    /**
     * @serialData  Uses
     *              <a href="../../../serialized-form.html#net.time4j.range.SPX">
     *              a dedicated serialization form</a> as proxy. The first byte
     *              contains the type-ID {@code 50} in the six most significant
     *              bits. The next bytes represent the start and the end
     *              boundary.
     *
     * Schematic algorithm:
     *
     * <pre>
     *  int header = 50;
     *  header <<= 2;
     *  out.writeByte(header);
     *  out.writeObject(getStart());
     *  out.writeObject(getEnd());
     * </pre>
     */
    private Object writeReplace() throws ObjectStreamException {

        return new SPX(this, SPX.DATE_TYPE);

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
