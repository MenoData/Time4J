/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FixedCalendarInterval.java) is part of project Time4J.
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
import net.time4j.PlainDate;
import net.time4j.engine.ChronoEntity;

import java.io.Serializable;
import java.util.stream.Stream;


/**
 * <p>Represents a fixed calendar interval. </p>
 *
 * @author  Meno Hochschild
 * @since   3.21/4.17
 */
/*[deutsch]
 * <p>Repr&auml;sentiert ein festes Kalenderintervall. </p>
 *
 * @author  Meno Hochschild
 * @since   3.21/4.17
 */
public abstract class FixedCalendarInterval<T extends FixedCalendarInterval<T>>
    extends ChronoEntity<T>
    implements Comparable<T>, ChronoInterval<PlainDate>, Iterable<PlainDate>, Serializable {

    //~ Konstruktoren -----------------------------------------------------

    // only for Time4J-subclasses
    FixedCalendarInterval() {
        super();

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>A calendar interval is always finite. </p>
     *
     * @return {@code true}
     */
    /*[deutsch]
     * <p>Ein Kalenderintervall ist immer endlich. </p>
     *
     * @return  {@code true}
     */
    @Override
    public final boolean isFinite() {

        return true;

    }

    /**
     * <p>A calendar interval is never empty. </p>
     *
     * @return {@code false}
     */
    /*[deutsch]
     * <p>Ein Kalenderintervall ist niemals leer. </p>
     *
     * @return  {@code false}
     */
    @Override
    public final boolean isEmpty() {

        return false;

    }

    @Override
    public boolean contains(ChronoInterval<PlainDate> other) {

        if (!other.isFinite()) {
            return false;
        }

        PlainDate startA = this.getStart().getTemporal();
        PlainDate startB = other.getStart().getTemporal();

        if (other.getStart().isOpen()) {
            if (startB.equals(PlainDate.axis().getMaximum())) {
                return false;
            }
            startB = startB.plus(1, CalendarUnit.DAYS);
        }

        if (startA.isAfter(startB)) {
            return false;
        }

        PlainDate endA = this.getEnd().getTemporal();
        PlainDate endB = other.getEnd().getTemporal();

        if (other.getEnd().isOpen()) {
            if (startB.isSimultaneous(endB)) {
                return !startB.isAfter(endA);
            } else if (endB.equals(PlainDate.axis().getMinimum())) {
                return false;
            }
            endB = endB.minus(1, CalendarUnit.DAYS);
        }

        return !endA.isBefore(endB);

    }

    @Override
    public boolean isBefore(ChronoInterval<PlainDate> other) {

        if (other.getStart().isInfinite()) {
            return false;
        }

        PlainDate endA = this.getEnd().getTemporal();
        PlainDate startB = other.getStart().getTemporal();

        if (other.getStart().isOpen()) {
            if (startB.equals(PlainDate.axis().getMaximum())) {
                return true;
            }
            startB = startB.plus(1, CalendarUnit.DAYS);
        }

        return endA.isBefore(startB);

    }

    @Override
    public boolean abuts(ChronoInterval<PlainDate> other) {

        if (other.isEmpty()) {
            return false;
        }

        PlainDate startA = this.getStart().getTemporal();
        PlainDate startB = other.getStart().getTemporal();

        if ((startB != null) && other.getStart().isOpen()) {
            startB = startB.plus(1, CalendarUnit.DAYS);
        }

        PlainDate endA = this.getEnd().getTemporal();
        PlainDate endB = other.getEnd().getTemporal();

        try {
            endA = endA.plus(1, CalendarUnit.DAYS);
        } catch (ArithmeticException ex) {
            return ((endB != null) && startA.isSimultaneous(endB));
        }

        try {
            if (endB == null) {
                return ((startB != null) && endA.isSimultaneous(startB));
            } else if (other.getEnd().isClosed()) {
                endB = endB.plus(1, CalendarUnit.DAYS);
            }
        } catch (ArithmeticException ex) {
            return ((startB != null) && endA.isSimultaneous(startB));
        }

        if (startB == null) {
            return startA.isSimultaneous(endB);
        } else if (endB == null) {
            return endA.isSimultaneous(startB);
        }

        return (endA.isSimultaneous(startB) ^ startA.isSimultaneous(endB));

    }

    /**
     * <p>Queries if this object and given object have the same position
     * on the time axis. </p>
     *
     * @param   other    object this instance is compared to
     * @return  {@code true} if this instance is temporally equal to {@code other} else {@code false}
     */
    /*[deutsch]
     * <p>Sind dieses Objekt und das angegebene Argument zeitlich gleich? </p>
     *
     * @param   other    object this instance is compared to
     * @return  {@code true} if this instance is temporally equal to {@code other} else {@code false}
     */
    public boolean isSimultaneous(T other) {

        return (this.compareTo(other) == 0);

    }

    /**
     * <p>Converts this fixed interval to a date interval with flexible boundaries which can participate in
     * any kind of interval boundary manipulations. </p>
     *
     * @return  DateInterval
     */
    /*[deutsch]
     * <p>Konvertiert dieses feste Intervall zu einem Intervall mit flexiblen Grenzen, die
     * Gegenstand von beliebigen Manipulationen sein k&ouml;nnen. </p>
     *
     * @return  DateInterval
     */
    public DateInterval toFlexInterval() {

        return new DateInterval(this.getStart(), this.getEnd());

    }

    /**
     * <p>Obtains a stream iterating over every calendar date of this interval. </p>
     *
     * @return  daily stream
     * @since   4.24
     */
    /*[deutsch]
     * <p>Erzeugt einen {@code Stream}, der &uuml;ber jedes Kalenderdatum dieses Intervalls geht. </p>
     *
     * @return  daily stream
     * @since   4.24
     */
    public Stream<PlainDate> streamDaily() {

        return DateInterval.streamDaily(this.getStart().getTemporal(), this.getEnd().getTemporal());

    }

    /**
     * <p>Creates a combination of this interval with an associated value. </p>
     *
     * @param   <V> generic value type
     * @param   value   associated value, not {@code null}
     * @return  new value interval
     * @since   3.31/4.26
     */
    /*[deutsch]
     * <p>Erzeugt eine Kombination dieses Intervalls mit einem assoziierten Wert. </p>
     *
     * @param   <V> generic value type
     * @param   value   associated value, not {@code null}
     * @return  new value interval
     * @since   3.31/4.26
     */
    public <V> ValueInterval<PlainDate, T, V> withValue(V value) {

        return new ValueInterval<>(this.getContext(), value);

    }

    // helper method for toString() in subclasses
    static void formatYear(
        StringBuilder sb,
        int year
    ) {

        int value = year;

        if (value < 0) {
            sb.append('-');
            value = Math.negateExact(year);
        }

        if (value >= 10000) {
            if (year > 0) {
                sb.append('+');
            }
        } else if (value < 1000) {
            sb.append('0');
            if (value < 100) {
                sb.append('0');
                if (value < 10) {
                    sb.append('0');
                }
            }
        }

        sb.append(value);

    }

    // continuous number range (without any gap)
    abstract long toProlepticNumber();

}