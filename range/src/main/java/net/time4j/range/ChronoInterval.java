/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoInterval.java) is part of project Time4J.
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

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.engine.Temporal;
import net.time4j.engine.TimeLine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * <p>Represents a temporal interval on a timeline. </p>
 *
 * @param   <T> temporal type of time points within a given interval
 * @author  Meno Hochschild
 * @since   1.3
 */
/**
 * <p>Repr&auml;sentiert ein Zeitintervall auf einem Zeitstrahl. </p>
 *
 * @param   <T> temporal type of time points within a given interval
 * @author  Meno Hochschild
 * @since   1.3
 */
public abstract class ChronoInterval<T extends Temporal<? super T>> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final Map<TimeLine<?>, IntervalFactory<?>> FACTORIES;

    static {
        Map<TimeLine<?>, IntervalFactory<?>> map =
            new HashMap<TimeLine<?>, IntervalFactory<?>>();
        map.put(PlainDate.axis(), DateIntervalFactory.INSTANCE);
        map.put(PlainTime.axis(), TimeIntervalFactory.INSTANCE);
        map.put(PlainTimestamp.axis(), TimestampIntervalFactory.INSTANCE);
        map.put(Moment.axis(), MomentIntervalFactory.INSTANCE);
        FACTORIES = Collections.unmodifiableMap(map);
    }

    //~ Instanzvariablen --------------------------------------------------

    private final Boundary<T> start;
    private final Boundary<T> end;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Paket-privater Standardkonstruktor f&uuml;r Subklassen. </p>
     *
     * @param   start   untere Intervallgrenze
     * @param   end     obere Intervallgrenze
     * @throws  IllegalArgumentException if start is after end
     */
    ChronoInterval(
        Boundary<T> start,
        Boundary<T> end
    ) {
        super();


        if (start.isAfter(end)) { // NPE-check
            throw new IllegalArgumentException(
                "Start after end: " + start + "/" + end);
        } else if (
            end.isOpen() // NPE-check
            && start.isOpen()
            && start.isSimultaneous(end)
        ) {
            if (start.isInfinite()) {
                throw new IllegalArgumentException(
                    "Infinite boundaries must not be equal.");
            } else {
                throw new IllegalArgumentException(
                    "Open start after open end: " + start + "/" + end);
            }
        }

        this.start = start;
        this.end = end;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Determines a generic factory for finite intervals
     * on given timeline. </p>
     *
     * <p>User will usually use this method only for creating finite
     * intervals on specific non-basic timelines. </p>
     *
     * @param   <T> generic temporal type
     * @param   timeline    time axis
     * @return  generic interval factory which only supports finite intervals
     * @since   1.3
     * @see     DateInterval#between(PlainDate,PlainDate)
     * @see     TimeInterval#between(PlainTime,PlainTime)
     * @see     TimestampInterval#between(PlainTimestamp,PlainTimestamp)
     * @see     MomentInterval#between(Moment,Moment)
     */
    /*[deutsch]
     * <p>Bestimmt eine Fabrik f&uuml;r begrenzte Intervalle
     * auf dem angegebenen Zeitstrahl. </p>
     *
     * <p>Anwender werden diese Methode normalerweise nur f&uuml;r die
     * Erzeugung von begrenzten Intervallen auf einem speziellen
     * Nicht-Standard-Zeitstrahl verwenden. </p>
     *
     * @param   <T> generic temporal type
     * @param   timeline    time axis
     * @return  generic interval factory which only supports finite intervals
     * @since   1.3
     * @see     DateInterval#between(PlainDate,PlainDate)
     * @see     TimeInterval#between(PlainTime,PlainTime)
     * @see     TimestampInterval#between(PlainTimestamp,PlainTimestamp)
     * @see     MomentInterval#between(Moment,Moment)
     */
    @SuppressWarnings("unchecked")
    public static <T extends Temporal<? super T>> IntervalFactory<T> on(
        TimeLine<T> timeline
    ) {

        IntervalFactory<?> factory = FACTORIES.get(timeline);

        if (factory == null) {
            return new GenericIntervalFactory<T>(timeline);
        } else {
            return (IntervalFactory<T>) factory;
        }

    }

    /**
     * <p>Yields the lower bound of this interval. </p>
     *
     * @return  start interval boundary
     * @since   1.3
     */
    /*[deutsch]
     * <p>Liefert die untere Grenze dieses Intervalls. </p>
     *
     * @return  start interval boundary
     * @since   1.3
     */
    public final Boundary<T> getStart() {

        return this.start;

    }

    /**
     * <p>Yields the upper bound of this interval. </p>
     *
     * @return  end interval boundary
     * @since   1.3
     */
    /*[deutsch]
     * <p>Liefert die obere Grenze dieses Intervalls. </p>
     *
     * @return  end interval boundary
     * @since   1.3
     */
    public final Boundary<T> getEnd() {

        return this.end;

    }

    /**
     * <p>Yields a copy of this interval with given start boundary. </p>
     *
     * @param   boundary    new start interval boundary
     * @return  possibly changed copy of this interval
     * @throws  IllegalArgumentException if given boundary is infinite and
     *          the concrete interval does not support infinite boundaries
     *          or if new start is after end
     * @since   1.3
     */
    /*[deutsch]
     * <p>Liefert eine Kopie dieses Intervalls mit der angegebenen unteren
     * Grenze. </p>
     *
     * @param   boundary    new start interval boundary
     * @return  possibly changed copy of this interval
     * @throws  IllegalArgumentException if given boundary is infinite and
     *          the concrete interval does not support infinite boundaries
     *          or if new start is after end
     * @since   1.3
     */
    public abstract ChronoInterval<T> withStart(Boundary<T> boundary);

    /**
     * <p>Yields a copy of this interval with given end boundary. </p>
     *
     * @param   boundary    new end interval boundary
     * @return  possibly changed copy of this interval
     * @throws  IllegalArgumentException if given boundary is infinite and
     *          the concrete interval does not support infinite boundaries
     *          or if new end is before start
     * @since   1.3
     */
    /*[deutsch]
     * <p>Liefert eine Kopie dieses Intervalls mit der angegebenen oberen
     * Grenze. </p>
     *
     * @param   boundary    new end interval boundary
     * @return  possibly changed copy of this interval
     * @throws  IllegalArgumentException if given boundary is infinite and
     *          the concrete interval does not support infinite boundaries
     *          or if new end is before start
     * @since   1.3
     */
    public abstract ChronoInterval<T> withEnd(Boundary<T> boundary);

    /**
     * <p>Yields a copy of this interval with given start time. </p>
     *
     * @param   temporal    new start timepoint
     * @return  possibly changed copy of this interval
     * @throws  IllegalArgumentException if new start is after end
     * @since   1.3
     */
    /*[deutsch]
     * <p>Liefert eine Kopie dieses Intervalls mit der angegebenen
     * Startzeit. </p>
     *
     * @param   temporal    new start timepoint
     * @return  possibly changed copy of this interval
     * @throws  IllegalArgumentException if new start is after end
     * @since   1.3
     */
    public abstract ChronoInterval<T> withStart(T temporal);

    /**
     * <p>Yields a copy of this interval with given end time. </p>
     *
     * @param   temporal    new end timepoint
     * @return  possibly changed copy of this interval
     * @throws  IllegalArgumentException if new end is before start
     * @since   1.3
     */
    /*[deutsch]
     * <p>Liefert eine Kopie dieses Intervalls mit der angegebenen Endzeit. </p>
     *
     * @param   temporal    new end timepoint
     * @return  possibly changed copy of this interval
     * @throws  IllegalArgumentException if new end is before start
     * @since   1.3
     */
    public abstract ChronoInterval<T> withEnd(T temporal);

    /**
     * <p>Determines if this interval has finite boundaries. </p>
     *
     * @return  {@code true} if start and end are finite else {@code false}
     * @since   1.3
     */
    /*[deutsch]
     * <p>Ermittelt, ob dieses Intervall endliche Grenzen hat. </p>
     *
     * @return  {@code true} if start and end are finite else {@code false}
     * @since   1.3
     */
    public boolean isFinite() {

        return !(this.start.isInfinite() || this.end.isInfinite());

    }

    /**
     * <p>Determines if this interval is empty. </p>
     *
     * @return  {@code true} if this interval does not contain any time point
     *          else {@code false}
     * @since   1.3
     */
    /*[deutsch]
     * <p>Ermittelt, ob dieses Intervall leer ist. </p>
     *
     * @return  {@code true} if this interval does not contain any time point
     *          else {@code false}
     * @since   1.3
     */
    public boolean isEmpty() {

        return (
            this.isFinite()
            && this.start.getTemporal().isSimultaneous(this.end.getTemporal())
            && (this.start.getEdge() != this.end.getEdge())); // half-open

    }

    /**
     * <p>Queries if given time point belongs to this interval. </p>
     *
     * @param   temporal    time point to be queried, maybe {@code null}
     * @return  {@code true} if given time point is not {@code null} and
     *          belongs to this interval else {@code false}
     */
    /*[deutsch]
     * <p>Ermittelt, ob der angegebene Zeitpunkt zu diesem Intervall
     * geh&ouml;rt. </p>
     *
     * @param   temporal    time point to be queried, maybe {@code null}
     * @return  {@code true} if given time point is not {@code null} and
     *          belongs to this interval else {@code false}
     */
    public boolean contains(T temporal) {

        if (temporal == null) {
            return false;
        }

        boolean startCondition;

        if (this.start.isInfinite()) {
            startCondition = true;
        } else if (this.start.isOpen()) {
            startCondition = this.start.getTemporal().isBefore(temporal);
        } else { // closed
            startCondition = !this.start.getTemporal().isAfter(temporal);
        }

        if (!startCondition) {
            return false; // short-cut
        }

        boolean endCondition;

        if (this.end.isInfinite()) {
            endCondition = true;
        } else if (this.end.isOpen()) {
            endCondition = this.end.getTemporal().isAfter(temporal);
        } else { // closed
            endCondition = !this.end.getTemporal().isBefore(temporal);
        }

        return endCondition;

    }

    @Override
    public final boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof ChronoInterval) {
            ChronoInterval<?> that = (ChronoInterval) obj;
            return (
                this.start.equals(that.start)
                && this.end.equals(that.end)
                && this.getTimeLine().equals(that.getTimeLine())
            );
        } else {
            return false;
        }

    }

    @Override
    public final int hashCode() {

        return (17 * this.start.hashCode() + 37 * this.end.hashCode());

    }

    @Override
    public final String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(this.start.isOpen() ? '(' : '[');
        sb.append(this.start.isInfinite() ? "-∞" : this.start.getTemporal());
        sb.append('/');
        sb.append(this.end.isInfinite() ? "+∞" : this.end.getTemporal());
        sb.append(this.end.isOpen() ? ')' : ']');
        return sb.toString();

    }

    /**
     * <p>Liefert die zugeh&ouml;rige Zeitachse. </p>
     *
     * @return  associated {@code TimeLine}
     * @since   1.3
     */
    protected abstract TimeLine<T> getTimeLine();

}
