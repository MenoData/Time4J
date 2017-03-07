/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ZonalDateTime.java) is part of project Time4J.
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

package net.time4j;

import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.format.RawValues;
import net.time4j.format.TemporalFormatter;
import net.time4j.scale.TimeScale;
import net.time4j.scale.UniversalTime;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.ParseException;
import java.text.ParsePosition;

import static net.time4j.PlainTime.SECOND_OF_MINUTE;
import static net.time4j.format.Attributes.TIMEZONE_ID;


/**
 * <p>Combination of UTC-moment and timezone. </p>
 *
 * <p>An instance can be created by {@code Moment.inLocalView()} or
 * {@code Moment.inZonalView(...)}. This type mainly serves for various
 * type conversions and incorporates a valid local timestamp as well as an
 * universal time in UTC. If users wish to apply any kind of data
 * manipulation then an object of this type has first to be converted
 * to a local timestamp or to a global UTC-moment. Example: </p>
 *
 * <pre>
 *  Moment moment = ...;
 *  ZonalDateTime zdt = moment.inLocalView();
 *
 *  // manipulation on local timeline
 *  PlainTimestamp localTSP = zdt.toTimestamp().plus(30, ClockUnit.SECONDS);
 *
 *  // manipulation on global timeline
 *  Moment globalTSP = zdt.toMoment().plus(30, SI.SECONDS);
 * </pre>
 *
 * <p>This class supports all elements which are supported by {@link Moment}
 * and {@link PlainTimestamp}, too. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 * @see     Moment#inLocalView()
 * @see     Moment#inZonalView(TZID)
 * @see     Moment#inZonalView(String)
 */
/*[deutsch]
 * <p>Kombination aus UTC-Moment und Zeitzone. </p>
 *
 * <p>Eine Instanz kann mit Hilfe von {@code Moment.inLocalView()} oder
 * {@code Moment.inZonalView(...)} erzeugt werden. Dieser Typ dient vorwiegend
 * der Typkonversion und verk&ouml;rpert sowohl einen g&uuml;ltigen lokalen
 * Zeitstempel als auch eine Universalzeit in UTC. Wenn Anwender irgendeine
 * Art von Datenmanipulation anwenden m&ouml;chten, dann mu&szlig; ein
 * Objekt dieses Typs zuerst in einen lokalen Zeitstempel oder einen
 * globalen UTC-Moment umgewandelt werden. Beispiel: </p>
 *
 * <pre>
 *  Moment moment = ...;
 *  ZonalDateTime zdt = moment.inLocalView();
 *
 *  // manipulation on local timeline
 *  PlainTimestamp localTSP = zdt.toTimestamp().plus(30, ClockUnit.SECONDS);
 *
 *  // manipulation on global timeline
 *  Moment globalTSP = zdt.toMoment().plus(30, SI.SECONDS);
 * </pre>
 *
 * <p>Diese Klasse unterst&uuml;tzt alle Elemente, die auch von {@link Moment}
 * und {@link PlainTimestamp} unterst&uuml;tzt werden. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 * @see     Moment#inLocalView()
 * @see     Moment#inZonalView(TZID)
 * @see     Moment#inZonalView(String)
 */
public final class ZonalDateTime
    implements ChronoDisplay, UniversalTime {

    //~ Instanzvariablen --------------------------------------------------

    private final Moment moment;
    private final Timezone zone;
    private transient final PlainTimestamp timestamp;

    //~ Konstruktoren -----------------------------------------------------

    private ZonalDateTime(
        Moment moment,
        Timezone tz
    ) {
        super();

        this.zone = tz;
        ZonalOffset offset = tz.getOffset(moment);

        if (moment.isLeapSecond()) {
            if (
                (offset.getFractionalAmount() != 0)
                || ((offset.getAbsoluteSeconds() % 60) != 0)
            ) {
                throw new IllegalArgumentException(
                    "Leap second can only be represented "
                    + " with timezone-offset in full minutes: "
                    + offset);
            }
        }

        this.moment = moment;
        this.timestamp = PlainTimestamp.from(moment, offset);

    }

    private ZonalDateTime(
        PlainTimestamp tsp,
        ZonalOffset offset
    ) {
        super();

        this.moment = tsp.at(offset);
        this.zone = Timezone.of(offset);
        this.timestamp = tsp;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Erzeugt einen zonalen Moment. </p>
     *
     * @param   moment          global timestamp
     * @param   tz              timezone
     * @return  ZonalDateTime
     * @throws  IllegalArgumentException if leapsecond shall be formatted
     *          with non-full-minute-timezone-offset
     */
    static ZonalDateTime of(
        Moment moment,
        Timezone tz
    ) {

        return new ZonalDateTime(moment, tz);

    }

    /**
     * <p>Erzeugt einen zonalen Moment. </p>
     *
     * @param   tsp             zonal timestamp
     * @param   offset          timezone offset
     * @return  ZonalDateTime
     */
    static ZonalDateTime of(
        PlainTimestamp tsp,
        ZonalOffset offset
    ) {

        return new ZonalDateTime(tsp, offset);

    }

    /**
     * <p>Compares this instance with another instance on the global timeline (UTC). </p>
     *
     * <p>If the UTC-times are equal then and only then the local timestamps will be taken into account. </p>
     *
     * @param   zdt     other instance to be compared with
     * @return  negative, zero or positive integer if this instance is earlier, simultaneous or later than given arg
     * @see     #compareByLocalTimestamp(ZonalDateTime)
     * @since   3.16/4.13
     */
    /*[deutsch]
     * <p>Vergleicht diese Instanz mit der angegebenen Instanz auf der globalen Zeitachse (UTC). </p>
     *
     * <p>Die lokalen Zeitstempel werden genau dann in Betracht gezogen, wenn die UTC-Zeitpunkte gleich sind. </p>
     *
     * @param   zdt     other instance to be compared with
     * @return  negative, zero or positive integer if this instance is earlier, simultaneous or later than given arg
     * @see     #compareByLocalTimestamp(ZonalDateTime)
     * @since   3.16/4.13
     */
    public int compareByMoment(ZonalDateTime zdt) {

        int cmp = this.moment.compareTo(zdt.moment);

        if (cmp == 0) {
            cmp = this.timestamp.compareTo(zdt.timestamp);
        }

        return cmp;

    }

    /**
     * <p>Compares this instance with another instance on the local timeline. </p>
     *
     * <p>If the local timestamps are equal then and only then the UTC-times will be taken into account. </p>
     *
     * @param   zdt     other instance to be compared with
     * @return  negative, zero or positive integer if this instance is earlier, simultaneous or later than given arg
     * @see     #compareByMoment(ZonalDateTime)
     * @since   3.16/4.13
     */
    /*[deutsch]
     * <p>Vergleicht diese Instanz mit der angegebenen Instanz auf der lokalen Zeitachse. </p>
     *
     * <p>Die UTC-Zeiten werden genau dann in Betracht gezogen, wenn die lokalen Zeitstempel gleich sind. </p>
     *
     * @param   zdt     other instance to be compared with
     * @return  negative, zero or positive integer if this instance is earlier, simultaneous or later than given arg
     * @see     #compareByMoment(ZonalDateTime)
     * @since   3.16/4.13
     */
    public int compareByLocalTimestamp(ZonalDateTime zdt) {

        int cmp = this.timestamp.compareTo(zdt.timestamp);

        if (cmp == 0) {
            cmp = this.moment.compareTo(zdt.moment);
        }

        return cmp;

    }

    @Override
    public boolean contains(ChronoElement<?> element) {

        return (
            this.timestamp.contains(element)
            || this.moment.contains(element)
        );

    }

    @Override
    public <V> V get(ChronoElement<V> element) {

        if (
            this.moment.isLeapSecond()
            && (element == SECOND_OF_MINUTE)
        ) {
            return element.getType().cast(Integer.valueOf(60));
        }

        if (this.timestamp.contains(element)) {
            return this.timestamp.get(element);
        } else {
            return this.moment.get(element);
        }

    }

    @Override
    public int getInt(ChronoElement<Integer> element) {

        if (this.moment.isLeapSecond() && (element == SECOND_OF_MINUTE)) {
            return 60;
        }

        int value = this.timestamp.getInt(element);

        if (value == Integer.MIN_VALUE) {
            value = this.moment.getInt(element);
        }

        return value;

    }

    // benutzt in ChronoFormatter/FractionProcessor
    @Override
    public <V> V getMinimum(ChronoElement<V> element) {

        if (this.timestamp.contains(element)) {
            return this.timestamp.getMinimum(element);
        } else {
            return this.moment.getMinimum(element);
        }

    }

    // benutzt in ChronoFormatter/FractionProcessor
    @Override
    public <V> V getMaximum(ChronoElement<V> element) {

        V max;

        if (this.timestamp.contains(element)) {
            max = this.timestamp.getMaximum(element);
        } else {
            max = this.moment.getMaximum(element);
        }

        if (
            (element == SECOND_OF_MINUTE)
            && (this.timestamp.getYear() >= 1972)
        ) {
            PlainTimestamp ts = this.timestamp.with(element, max);

            if (!this.zone.isInvalid(ts, ts)) {
                Moment transformed = ts.in(this.zone);
                Moment test = transformed.plus(1, SI.SECONDS);

                if (test.isLeapSecond()) {
                    return element.getType().cast(Integer.valueOf(60));
                }
            }
        }

        return max;

    }

    /**
     * <p>This object always has a timezone. </p>
     *
     * @return  {@code true}
     */
    /*[deutsch]
     * <p>Dieses Objekt hat immer eine Zeitzone. </p>
     *
     * @return  {@code true}
     */
    @Override
    public boolean hasTimezone() {

        return true;

    }

    @Override
    public TZID getTimezone() {

        return this.zone.getID();

    }

    /**
     * <p>Yields the timezone offset. </p>
     *
     * @return	offset relative to UTC+00:00
     * @since	2.0
     */
    /*[deutsch]
     * <p>Liefert den Zeitzonen-Offset. </p>
     *
     * @return	offset relative to UTC+00:00
     * @since	2.0
     */
    public ZonalOffset getOffset() {

        return this.zone.getOffset(this.moment);

    }

    /**
     * <p>Converts this object to a global UTC-moment. </p>
     *
     * @return  Moment
     */
    /*[deutsch]
     * <p>Konvertiert dieses Objekt zu einem globalen UTC-Moment. </p>
     *
     * @return  Moment
     */
    public Moment toMoment() {

        return this.moment;

    }

    /**
     * <p>Converts this object to a zonal timestamp. </p>
     *
     * @return  PlainTimestamp
     */
    /*[deutsch]
     * <p>Konvertiert dieses Objekt zu einem zonalen Zeitstempel. </p>
     *
     * @return  PlainTimestamp
     */
    public PlainTimestamp toTimestamp() {

        return this.timestamp;

    }

    @Override
    public long getElapsedTime(TimeScale scale) {

        return this.moment.getElapsedTime(scale);

    }

    @Override
    public int getNanosecond(TimeScale scale) {

        return this.moment.getNanosecond(scale);

    }

    @Override
    public boolean isLeapSecond() {

        return this.moment.isLeapSecond();

    }

    @Override
    public long getPosixTime() {

        return this.moment.getPosixTime();

    }

    @Override
    public int getNanosecond() {

        return this.moment.getNanosecond();

    }

    /**
     * <p>Creates a formatted output of this instance. </p>
     *
     * @param   printer     helps to format this instance
     * @return  formatted string
     * @since   3.0
     */
    /*[deutsch]
     * <p>Erzeugt eine formatierte Ausgabe dieser Instanz. </p>
     *
     * @param   printer     helps to format this instance
     * @return  formatted string
     * @since   3.0
     */
    public String print(TemporalFormatter<Moment> printer) {

        return printer.withTimezone(this.getTimezone()).format(this.moment);

    }

    /**
     * <p>Parses given text to a {@code ZonalDateTime}. </p>
     *
     * @param   text        text to be parsed
     * @param   parser      helps to parse given text
     * @return  parsed result
     * @throws  IndexOutOfBoundsException if the text is empty
     * @throws  ParseException if the text is not parseable
     * @since   3.0
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text als {@code ZonalDateTime}. </p>
     *
     * @param   text        text to be parsed
     * @param   parser      helps to parse given text
     * @return  parsed result
     * @throws  IndexOutOfBoundsException if the text is empty
     * @throws  ParseException if the text is not parseable
     * @since   3.0
     */
    public static ZonalDateTime parse(
        String text,
        TemporalFormatter<Moment> parser
    ) throws ParseException {

        ParsePosition pos = new ParsePosition(0);
        RawValues rawValues = new RawValues();
        Moment moment = parser.parse(text, pos, rawValues);
        Timezone tz;

        if (moment == null) {
            moment = parser.parse(text); // will throw an exception with better error message
        }

        if (moment == null) {
            throw new ParseException("Cannot parse: " + text, pos.getErrorIndex());
        } else if (rawValues.get().hasTimezone()) {
            tz = toTimezone(rawValues.get().getTimezone(), text);
        } else if (parser.getAttributes().contains(TIMEZONE_ID)) {
            tz = toTimezone(parser.getAttributes().get(TIMEZONE_ID), text);
        } else {
            throw new ParseException("Missing timezone: " + text, 0);
        }

        return ZonalDateTime.of(moment, tz);

    }

    /**
     * <p>Parses given text to a {@code ZonalDateTime}. </p>
     *
     * @param   text        text to be parsed
     * @param   parser      helps to parse given text
     * @param   position    parse position (always as new instance)
     * @return  parsed result or {@code null} if parsing does not work (for example missing timezone information)
     * @throws  IndexOutOfBoundsException if the text is empty
     * @throws  IllegalArgumentException if timezone data cannot be loaded
     * @since   3.16/4.13
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text als {@code ZonalDateTime}. </p>
     *
     * @param   text        text to be parsed
     * @param   parser      helps to parse given text
     * @param   position    parse position (always as new instance)
     * @return  parsed result or {@code null} if parsing does not work (for example missing timezone information)
     * @throws  IndexOutOfBoundsException if the text is empty
     * @throws  IllegalArgumentException if timezone data cannot be loaded
     * @since   3.16/4.13
     */
    public static ZonalDateTime parse(
        String text,
        TemporalFormatter<Moment> parser,
        ParsePosition position
    ) {

        RawValues rawValues = new RawValues();
        Moment moment = parser.parse(text, position, rawValues);
        Timezone tz;

        if (moment == null) {
            return null;
        } else if (rawValues.get().hasTimezone()) {
            tz = Timezone.of(rawValues.get().getTimezone());
        } else if (parser.getAttributes().contains(TIMEZONE_ID)) {
            tz = Timezone.of(parser.getAttributes().get(TIMEZONE_ID));
        } else {
            position.setErrorIndex(0);
            return null;
        }

        return ZonalDateTime.of(moment, tz);

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof ZonalDateTime) {
            ZonalDateTime that = (ZonalDateTime) obj;
            return (
                this.moment.equals(that.moment)
                && this.zone.equals(that.zone)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return (this.moment.hashCode() ^ this.zone.hashCode());

    }

    /**
     * <p>Yields a canonical representation in ISO-like-style. </p>
     *
     * @return  String suitable only for debugging purposes
     * @see     #print(TemporalFormatter)
     */
    /*[deutsch]
     * <p>Liefert eine kanonische Darstellung &auml;hnlich zu ISO-8601. </p>
     *
     * @return  String suitable only for debugging purposes
     * @see     #print(TemporalFormatter)
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(40);
        sb.append(this.timestamp.getCalendarDate());
        sb.append('T');
        int hour = this.timestamp.getHour();
        if (hour < 10) {
            sb.append('0');
        }
        sb.append(hour);
        sb.append(':');
        int minute = this.timestamp.getMinute();
        if (minute < 10) {
            sb.append('0');
        }
        sb.append(minute);
        sb.append(':');
        if (this.isLeapSecond()) {
            sb.append("60");
        } else {
            int second = this.timestamp.getSecond();
            if (second < 10) {
                sb.append('0');
            }
            sb.append(second);
        }
        int n = this.timestamp.getNanosecond();
        if (n != 0) {
            PlainTime.printNanos(sb, n);
        }
        sb.append(this.getOffset());
        TZID tzid = this.getTimezone();
        boolean offset = (tzid instanceof ZonalOffset);

        if (!offset) {
            sb.append('[');
            sb.append(tzid.canonical());
            sb.append(']');
        }

        return sb.toString();

    }

    /**
     * <p>Writes this instance to given output (serialization). </p>
     *
     * <p><strong>Warning:</strong> Serializing this instance is a heavy-weight-operation because the
     * whole relevant timezone data will be written to given stream, not only the timezone-id. </p>
     *
     * @param   output      object output
     * @throws IOException if writing fails
     * @since   3.1
     */
    /*[deutsch]
     * <p>Schreibt diese Instanz in den angegebenen Ausgabestrom (Serialisierung). </p>
     *
     * <p><strong>Warnung:</strong> Die Serialisierung dieser Instanz ist schwergewichtig, weil alle
     * relevanten Zeitzonendaten komplett geschrieben werden, nicht nur die Zeitzonen-ID. </p>
     *
     * @param   output      object output
     * @throws  IOException if writing fails
     * @since   3.1
     */
    public void write(ObjectOutput output) throws IOException {

        output.writeObject(this.moment);
        output.writeObject(this.zone);

    }

    /**
     * <p>This is the reverse operation of {@link #write(ObjectOutput)}. </p>
     *
     * @param   input       object input
     * @return  reconstructed instance of serialized {@code ZonalDateTime}
     * @throws  IOException if reading fails
     * @throws  ClassNotFoundException if class-loading fails
     * @throws  IllegalArgumentException in case of inconsistent data
     * @since   3.1
     */
    /*[deutsch]
     * <p>Das ist die Umkehroperation zu {@link #write(ObjectOutput)}. </p>
     *
     * @param   input       object input
     * @return  reconstructed instance of serialized {@code ZonalDateTime}
     * @throws  IOException if reading fails
     * @throws  ClassNotFoundException if class-loading fails
     * @throws  IllegalArgumentException in case of inconsistent data
     * @since   3.1
     */
    public static ZonalDateTime read(ObjectInput input) throws IOException, ClassNotFoundException {

        Moment moment = (Moment) input.readObject();
        Timezone tz = (Timezone) input.readObject();
        return new ZonalDateTime(moment, tz);

    }

    private static Timezone toTimezone(
        TZID tzid,
        String text
    ) throws ParseException {

        try {
            return Timezone.of(tzid);
        } catch (IllegalArgumentException iae) {
            ParseException pe =
                new ParseException("Timezone error: " + text, 0);
            pe.initCause(iae);
            throw pe;
        }

    }

}
