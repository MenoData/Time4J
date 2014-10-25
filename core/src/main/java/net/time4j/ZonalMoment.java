/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ZonalMoment.java) is part of project Time4J.
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

import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoValues;
import net.time4j.format.ChronoFormatter;
import net.time4j.format.ParseLog;
import net.time4j.scale.TimeScale;
import net.time4j.scale.UniversalTime;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.io.IOException;
import java.text.ParseException;

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
 *  ZonalMoment zm = moment.inLocalView();
 *
 *  // manipulation on local timeline
 *  PlainTimestamp localTSP = zm.toTimestamp().plus(30, ClockUnit.SECONDS);
 *
 *  // manipulation on global timeline
 *  Moment globalTSP = zm.toMoment().plus(30, SI.SECONDS);
 * </pre>
 *
 * @author  Meno Hochschild
 * @since   1.3
 * @concurrency This class is immutable as long as the underlying timezone
 *              data are immutable.
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
 *  ZonalMoment zm = moment.inLocalView();
 *
 *  // Manipulation auf dem lokalen Zeitstrahl
 *  PlainTimestamp localTSP = zm.toTimestamp().plus(30, ClockUnit.SECONDS);
 *
 *  // Manipulation auf dem globalen Zeitstrahl
 *  Moment globalTSP = zm.toMoment().plus(30, SI.SECONDS);
 * </pre>
 *
 * @author  Meno Hochschild
 * @since   1.3
 * @concurrency This class is immutable as long as the underlying timezone
 *              data are immutable.
 * @see     Moment#inLocalView()
 * @see     Moment#inZonalView(TZID)
 * @see     Moment#inZonalView(String)
 */
public final class ZonalMoment
    implements ChronoValues, UniversalTime {

    //~ Instanzvariablen --------------------------------------------------

    private final Moment moment;
    private final Timezone zone;
    private transient final PlainTimestamp timestamp;

    //~ Konstruktoren -----------------------------------------------------

    private ZonalMoment(
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

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Erzeugt einen zonalen Zeitstempel. </p>
     *
     * @param   moment          global timestamp
     * @param   tz              timezone
     * @throws  IllegalArgumentException if leapsecond shall be formatted
     *          with non-full-minute-timezone-offset
     */
    static ZonalMoment of(
        Moment moment,
        Timezone tz
    ) {

        return new ZonalMoment(moment, tz);

    }

    @Override
    public boolean contains(ChronoElement<?> element) {

        return this.timestamp.contains(element);

    }

    @Override
    public <V> V get(ChronoElement<V> element) {

        if (
            this.moment.isLeapSecond()
            && (element == SECOND_OF_MINUTE)
        ) {
            return element.getType().cast(Integer.valueOf(60));
        }

        return this.timestamp.get(element);

    }

    // benutzt in ChronoFormatter/FractionProcessor
    @Override
    public <V> V getMinimum(ChronoElement<V> element) {

        return this.timestamp.getMinimum(element);

    }

    // benutzt in ChronoFormatter/FractionProcessor
    @Override
    public <V> V getMaximum(ChronoElement<V> element) {

        V max = this.timestamp.getMaximum(element);

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
     * @param   formatter   helps to format this instance
     * @return  formatted string
     * @since   1.3
     */
    /*[deutsch]
     * <p>Erzeugt eine formatierte Ausgabe dieser Instanz. </p>
     *
     * @param   formatter   helps to format this instance
     * @return  formatted string
     * @since   1.3
     */
    public String print(ChronoFormatter<Moment> formatter) {

        AttributeQuery aq = new ZOM(this, formatter.getDefaultAttributes());
        StringBuilder sb = new StringBuilder();

        try {
            formatter.print(this.moment, sb, aq, false);
        } catch (IOException ex) {
            throw new AssertionError(ex); // never happen
        }

        return sb.toString();

    }

    /**
     * <p>Parses given text to a {@code ZonalMoment}. </p>
     *
     * @param   text        text to be parsed
     * @param   formatter   helps to parse given text
     * @return  parsed zonal moment
     * @throws  IndexOutOfBoundsException if the text is empty
     * @throws  ParseException if the text is not parseable
     * @since   1.3
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text als {@code ZonalMoment}. </p>
     *
     * @param   text        text to be parsed
     * @param   formatter   helps to parse given text
     * @return  parsed zonal moment
     * @throws  IndexOutOfBoundsException if the text is empty
     * @throws  ParseException if the text is not parseable
     * @since   1.3
     */
    public static ZonalMoment parse(
        String text,
        ChronoFormatter<Moment> formatter
    ) throws ParseException {

        ParseLog plog = new ParseLog();
        Moment moment = formatter.parse(text, plog);
        Timezone tz;

        if (moment == null) {
            throw new ParseException(
                plog.getErrorMessage(),
                plog.getErrorIndex()
            );
        } else if (plog.getRawValues().hasTimezone()) {
            tz =
                toTimezone(
                    plog.getRawValues().getTimezone(),
                    text);
        } else if (formatter.getDefaultAttributes().contains(TIMEZONE_ID)) {
            tz =
                toTimezone(
                    formatter.getDefaultAttributes().get(TIMEZONE_ID),
                    text);
        } else {
            throw new ParseException("Missing timezone: " + text, 0);
        }

        return ZonalMoment.of(moment, tz);

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof ZonalMoment) {
            ZonalMoment that = (ZonalMoment) obj;
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
     * <p>For debugging support. </p>
     *
     * @return  String
     */
    /*[deutsch]
     * <p>Debugging-Unterst&uuml;tzung. </p>
     *
     * @return  String
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("[moment=");
        sb.append(this.moment);
        sb.append(", timestamp=");
        sb.append(this.timestamp);
        sb.append(", timezone=");
        sb.append(this.zone);
        sb.append(']');
        return sb.toString();

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
