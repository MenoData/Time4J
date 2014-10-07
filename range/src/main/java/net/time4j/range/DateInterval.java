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
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.engine.TimeLine;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;

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

        ChronoInterval<PlainDate> base = this.getCalculationBase();

        return Duration.inYearsMonthsDays().between(
            base.getStart().getTemporal(),
            base.getEnd().getTemporal());

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

        ChronoInterval<PlainDate> base = this.getCalculationBase();

        return Duration.in(units).between(
            base.getStart().getTemporal(),
            base.getEnd().getTemporal());

    }

    @Override
    protected TimeLine<PlainDate> getTimeLine() {

        return PlainDate.axis();

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
