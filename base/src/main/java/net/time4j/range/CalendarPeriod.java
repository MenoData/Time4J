/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CalendarPeriod.java) is part of project Time4J.
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

import java.io.Serializable;
import java.util.stream.Stream;


/**
 * <p>Represents a closed interval between two incomplete calendar dates like months, weeks, quarters or years. </p>
 *
 * @param   <T> generic type of incomplete calendar date (without day part)
 * @author  Meno Hochschild
 * @since   5.0
 * @doctags.concurrency {immutable}
 */
/*[deutsch]
 * <p>Repr&auml;sentiert ein geschlossenes Intervall zwischen zwei unvollst&auml;ndigen Datumsangaben wie
 * Monaten, Wochen, Quartalen oder Jahren. </p>
 *
 * @param   <T> generic type of incomplete calendar date (without day part)
 * @author  Meno Hochschild
 * @since   5.0
 * @doctags.concurrency {immutable}
 */
public final class CalendarPeriod<T extends FixedCalendarInterval<T>>
    implements ChronoInterval<T>, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = -1570485272742024241L;

    //~ Instanzvariablen --------------------------------------------------

    private final T t1;
    private final T t2;

    private final FixedCalendarTimeLine<T> timeLine;

    //~ Konstruktoren -----------------------------------------------------

    private CalendarPeriod(
        T t1,
        T t2,
        FixedCalendarTimeLine<T> timeLine
    ) {
        super();

        if (t1.isAfter(t2)) {
            throw new IllegalArgumentException("Start after end: " + t1 + "/" + t2);
        }

        this.t1 = t1;
        this.t2 = t2;
        this.timeLine = timeLine;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * Creates a calendar period for given year range.
     *
     * @param   y1  first calendar year
     * @param   y2  last calendar year (inclusive)
     * @return  CalendarPeriod
     */
    /*[deutsch]
     * Erzeugt eine {@code CalendarPeriod} f&uuml;r die angegebene Jahresspanne.
     *
     * @param   y1  first calendar year
     * @param   y2  last calendar year (inclusive)
     * @return  CalendarPeriod
     */
    public static CalendarPeriod<CalendarYear> between(
        CalendarYear y1,
        CalendarYear y2
    ) {

        return new CalendarPeriod<>(y1, y2, FixedCalendarTimeLine.forYears());

    }

    /**
     * Creates a calendar period for given quarter year range.
     *
     * @param   q1  first quarter year
     * @param   q2  last quarter year (inclusive)
     * @return  CalendarPeriod
     */
    /*[deutsch]
     * Erzeugt eine {@code CalendarPeriod} f&uuml;r die angegebene Quartalsspanne.
     *
     * @param   q1  first quarter year
     * @param   q2  last quarter year (inclusive)
     * @return  CalendarPeriod
     */
    public static CalendarPeriod<CalendarQuarter> between(
        CalendarQuarter q1,
        CalendarQuarter q2
    ) {

        return new CalendarPeriod<>(q1, q2, FixedCalendarTimeLine.forQuarters());

    }

    /**
     * Creates a calendar period for given month range.
     *
     * @param   m1  first calendar month
     * @param   m2  last calendar month (inclusive)
     * @return  CalendarPeriod
     */
    /*[deutsch]
     * Erzeugt eine {@code CalendarPeriod} f&uuml;r die angegebene Monatsspanne.
     *
     * @param   m1  first calendar month
     * @param   m2  last calendar month (inclusive)
     * @return  CalendarPeriod
     */
    public static CalendarPeriod<CalendarMonth> between(
        CalendarMonth m1,
        CalendarMonth m2
    ) {

        return new CalendarPeriod<>(m1, m2, FixedCalendarTimeLine.forMonths());

    }

    /**
     * Creates a calendar period for given calendar week range.
     *
     * @param   w1  first calendar week
     * @param   w2  last calendar week (inclusive)
     * @return  CalendarPeriod
     */
    /*[deutsch]
     * Erzeugt eine {@code CalendarPeriod} f&uuml;r die angegebene Wochenspanne.
     *
     * @param   w1  first calendar week
     * @param   w2  last calendar week (inclusive)
     * @return  CalendarPeriod
     */
    public static CalendarPeriod<CalendarWeek> between(
        CalendarWeek w1,
        CalendarWeek w2
    ) {

        return new CalendarPeriod<>(w1, w2, FixedCalendarTimeLine.forWeeks());

    }

    @Override
    public Boundary<T> getStart() {

        return Boundary.ofClosed(t1);

    }

    @Override
    public Boundary<T> getEnd() {

        return Boundary.ofClosed(t2);

    }

    @Override
    public boolean isEmpty() {

        return false;

    }

    @Override
    public boolean isFinite() {

        return true;

    }

    @Override
    public boolean contains(T temporal) {

        return (!temporal.isBefore(this.t1) && !temporal.isAfter(this.t2));

    }

    @Override
    public boolean contains(ChronoInterval<T> other) {

        if (!other.isFinite()) {
            return false;
        }

        T startB = other.getStart().getTemporal();

        if (other.getStart().isOpen()) {
            startB = this.timeLine.stepForward(startB);
        }

        if ((startB == null) || (this.timeLine.compare(this.t1, startB) > 0)) {
            return false;
        }

        T endB = other.getEnd().getTemporal();

        if (other.getEnd().isOpen() && (this.timeLine.compare(startB, endB) == 0)) {
            return (this.timeLine.compare(startB, this.t2) <= 0);
        } else {
            if (other.getEnd().isOpen()) {
                endB = this.timeLine.stepBackwards(endB);
            }
            return ((endB != null) && this.timeLine.compare(this.t2, endB) >= 0);
        }

    }

    @Override
    public boolean isAfter(T temporal) {

        return this.t1.isAfter(temporal);

    }

    @Override
    public boolean isBefore(T temporal) {

        return this.t2.isBefore(temporal);

    }

    @Override
    public boolean isBefore(ChronoInterval<T> other) {

        if (other.getStart().isInfinite()) {
            return false;
        }

        T startB = other.getStart().getTemporal();

        if (other.getStart().isOpen()) {
            startB = this.timeLine.stepForward(startB);
        }

        if (startB == null) { // exotic case: start in infinite future
            return true;
        } else {
            return (this.timeLine.compare(this.t2, startB) < 0);
        }

    }

    @Override
    public boolean abuts(ChronoInterval<T> other) {

        if (other instanceof CalendarPeriod) {
            CalendarPeriod<?> that = CalendarPeriod.class.cast(other);
            return (this.t2.toProlepticNumber() + 1 == that.t1.toProlepticNumber())
                || (that.t2.toProlepticNumber() + 1 == this.t1.toProlepticNumber());
        }

        if (other.isEmpty()) {
            return false;
        }

        T startB = other.getStart().getTemporal();

        if ((startB != null) && other.getStart().isOpen()) {
            startB = this.timeLine.stepForward(startB);
        }

        T endA = this.timeLine.stepForward(this.t2);
        T endB = other.getEnd().getTemporal();

        if ((endB != null) && other.getEnd().isClosed()) {
            endB = this.timeLine.stepForward(endB);
        }

        if ((endA == null) || (startB == null)) {
            return (endB != null) && (this.timeLine.compare(this.t1, endB) == 0);
        } else if (endB == null) {
            return (this.timeLine.compare(endA, startB) == 0);
        }

        return (this.timeLine.compare(endA, startB) == 0) ^ (this.timeLine.compare(this.t1, endB) == 0);

    }

    /**
     * <p>Obtains a stream for fixed calendar intervals like years, quarters, months or weeks. </p>
     *
     * <p>The produced stream has at least one element. </p>
     *
     * @return  Stream
     */
    /*[deutsch]
     * <p>Liefert einen {@code Stream} von festen Kalenderintervallen wie Jahren, Quartalen, Monaten oder Wochen. </p>
     *
     * <p>Der so produzierte {@code Stream} hat wenigstens ein Element. </p>
     *
     * @return  Stream
     */
    public Stream<T> stream() {

        return this.timeLine.stream(this.t1, this.t2);

    }

    /**
     * <p>Obtains the delta between start and end in the smallest defined units. </p>
     *
     * <p>If start and end are equal then the delta is zero. </p>
     *
     * @return  difference in smallest defined units
     * @since   5.0
     */
    /*[deutsch]
     * <p>Liefert die Differenz zwischen Start und Ende in der kleinsten definierten Einheit. </p>
     *
     * <p>Falls Start und Ende zusammenfallen, ist die Differenz null. </p>
     *
     * @return  difference in smallest defined units
     * @since   5.0
     */
    public long delta() {

        return (this.t2.toProlepticNumber() - this.t1.toProlepticNumber());

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof CalendarPeriod) {
            CalendarPeriod<?> that = CalendarPeriod.class.cast(obj);
            return (this.t1.equals(that.t1) && this.t2.equals(that.t2) && this.timeLine.equals(that.timeLine));
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return 7 * this.t1.hashCode() ^ 31 * this.t2.hashCode();

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(this.t1);
        sb.append('/');
        sb.append(this.t2);
        return sb.toString();

    }

}
