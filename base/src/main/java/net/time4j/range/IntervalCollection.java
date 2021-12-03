/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (IntervalCollection.java) is part of project Time4J.
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
import net.time4j.engine.TimeLine;

import java.io.Serializable;
import java.time.Instant;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;


/**
 * <p>Represents a sorted list of arbitrary possibly overlapping intervals
 * (no series) whose boundaries can be changed in many ways. </p>
 *
 * <p>Any instance can first be achieved by calling one of the static
 * {@code onXYZAxis()}-methods and then be filled with any count of
 * typed intervals via {@code plus(...)}-methods. All intervals are
 * stored with closed start if they have finite start. Empty intervals
 * are never stored. </p>
 *
 * @param   <T> generic type characterizing the associated time axis
 * @author  Meno Hochschild
 * @serial  exclude
 * @since   2.0
 * @see     DateInterval#comparator()
 * @see     ClockInterval#comparator()
 * @see     TimestampInterval#comparator()
 * @see     MomentInterval#comparator()
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine sortierte Liste von beliebigen sich m&ouml;glicherweise
 * &uuml;berlappenden Intervallen (keine Reihe), deren Grenzen auf vielf&auml;ltige Weise
 * ge&auml;ndert werden k&ouml;nnen. </p>
 *
 * <p>Zuerst kann eine Instanz mit Hilfe von statischen Fabrikmethoden
 * wie {@code onXYZAxis()} erhalten und dann mit einer beliebigen Zahl
 * von typisierten Intervallen gef&uuml;llt werden - via {@code plus(...)}
 * -Methoden. Alle Intervalle werden so gespeichert, da&szlig; sie den
 * Start inklusive haben, wenn dieser endlich ist. Leere Intervalle werden
 * nie gespeichert. </p>
 *
 * @param   <T> generic type characterizing the associated time axis
 * @author  Meno Hochschild
 * @serial  exclude
 * @since   2.0
 * @see     DateInterval#comparator()
 * @see     ClockInterval#comparator()
 * @see     TimestampInterval#comparator()
 * @see     MomentInterval#comparator()
 */
public abstract class IntervalCollection<T>
    extends AbstractCollection<ChronoInterval<T>>
    implements Serializable {

    //~ Instanzvariablen --------------------------------------------------

    private transient final List<ChronoInterval<T>> intervals;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>For subclasses only. </p>
     */
    IntervalCollection() {
        super();

        this.intervals = Collections.emptyList();

    }

    /**
     * <p>For subclasses only. </p>
     *
     * @param   intervals   sorted list of intervals
     */
    IntervalCollection(List<ChronoInterval<T>> intervals) {
        super();

        this.intervals = Collections.unmodifiableList(intervals);

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Yields an empty instance on the date axis. </p>
     *
     * @return  empty {@code IntervalCollection} for date intervals
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liefert eine leere Instanz auf der Datumsachse. </p>
     *
     * @return  empty {@code IntervalCollection} for date intervals
     * @since   2.0
     */
    public static IntervalCollection<PlainDate> onDateAxis() {

        return DateWindows.EMPTY;

    }

    /**
     * <p>Yields an empty instance on the walltime axis. </p>
     *
     * @return  empty {@code IntervalCollection} for clock intervals
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liefert eine leere Instanz auf der Uhrzeitachse. </p>
     *
     * @return  empty {@code IntervalCollection} for clock intervals
     * @since   2.0
     */
    public static IntervalCollection<PlainTime> onClockAxis() {

        return ClockWindows.EMPTY;

    }

    /**
     * <p>Yields an empty instance on the timestamp axis. </p>
     *
     * @return  empty {@code IntervalCollection} for timestamp intervals
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liefert eine leere Instanz auf der Zeitstempelachse. </p>
     *
     * @return  empty {@code IntervalCollection} for timestamp intervals
     * @since   2.0
     */
    public static IntervalCollection<PlainTimestamp> onTimestampAxis() {

        return TimestampWindows.EMPTY;

    }

    /**
     * <p>Yields an empty instance on the UTC-axis. </p>
     *
     * @return  empty {@code IntervalCollection} for moment intervals
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liefert eine leere Instanz auf der UTC-Achse. </p>
     *
     * @return  empty {@code IntervalCollection} for moment intervals
     * @since   2.0
     */
    public static IntervalCollection<Moment> onMomentAxis() {

        return MomentWindows.EMPTY;

    }

    /**
     * <p>Yields an empty instance for intervals with the component type {@code java.util.Date}. </p>
     *
     * @return  empty {@code IntervalCollection} for old {@code java.util.Date}-intervals
     * @since   3.25/4.21
     */
    /*[deutsch]
     * <p>Liefert eine leere Instanz f&uuml;r Intervalle mit dem Komponententyp {@code java.util.Date}. </p>
     *
     * @return  empty {@code IntervalCollection} for old {@code java.util.Date}-intervals
     * @since   3.25/4.21
     */
    public static IntervalCollection<Date> onTraditionalTimeLine() {

        return on(SimpleInterval.onTraditionalTimeLine().getTimeLine());

    }

    /**
     * <p>Yields an empty instance for intervals with the component type {@code java.time.Instant}. </p>
     *
     * @return  empty {@code IntervalCollection} for {@code java.time.Instant}-intervals
     * @since   4.21
     */
    /*[deutsch]
     * <p>Liefert eine leere Instanz f&uuml;r Intervalle mit dem Komponententyp {@code java.time.Instant}. </p>
     *
     * @return  empty {@code IntervalCollection} for {@code java.time.Instant}-intervals
     * @since   4.21
     */
    public static IntervalCollection<Instant> onInstantTimeLine() {

        return on(SimpleInterval.onInstantTimeLine().getTimeLine());

    }

    /**
     * <p>Yields an empty instance for intervals on given timeline. </p>
     *
     * @param   <T> generic type of time points
     * @param   timeLine    the associated timeline
     * @return  empty generic {@code IntervalCollection}
     * @see     net.time4j.engine.TimeAxis
     * @see     net.time4j.engine.CalendarFamily#getTimeLine(String)
     * @see     net.time4j.engine.CalendarFamily#getTimeLine(net.time4j.engine.VariantSource)
     * @see     CalendarYear#timeline()
     * @see     CalendarQuarter#timeline()
     * @see     CalendarMonth#timeline()
     * @see     CalendarWeek#timeline()
     * @since   5.0
     */
    /*[deutsch]
     * <p>Liefert eine leere Instanz f&uuml;r Intervalle auf dem angegebenen Zeitstrahl. </p>
     *
     * @param   <T> generic type of time points
     * @param   timeLine    the associated timeline
     * @return  empty generic {@code IntervalCollection}
     * @see     net.time4j.engine.TimeAxis
     * @see     net.time4j.engine.CalendarFamily#getTimeLine(String)
     * @see     net.time4j.engine.CalendarFamily#getTimeLine(net.time4j.engine.VariantSource)
     * @see     CalendarYear#timeline()
     * @see     CalendarQuarter#timeline()
     * @see     CalendarMonth#timeline()
     * @see     CalendarWeek#timeline()
     * @since   5.0
     */
    @SuppressWarnings("unchecked")
    public static <T> IntervalCollection<T> on(TimeLine<T> timeLine) {

        if (timeLine.equals(PlainDate.axis())) {
            return (IntervalCollection<T>) onDateAxis();
        } else if (timeLine.equals(PlainTime.axis())) {
            return (IntervalCollection<T>) onClockAxis();
        } else if (timeLine.equals(PlainTimestamp.axis())) {
            return (IntervalCollection<T>) onTimestampAxis();
        } else if (timeLine.equals(Moment.axis())) {
            return (IntervalCollection<T>) onMomentAxis();
        }

        return new GenericWindows<>(timeLine, Collections.emptyList());

    }

    /**
     * <p>Returns all appended intervals. </p>
     *
     * <p>Note that all contained finite intervals have each a closed start. </p>
     *
     * @return  unmodifiable list of intervals sorted by start and then by length
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liefert alle hinzugef&uuml;gten Intervalle. </p>
     *
     * <p>Hinweis: Alle enthaltenen endlichen Intervalle haben einen geschlossenen Start. </p>
     *
     * @return  unmodifiable list of intervals sorted by start and then by length
     * @since   2.0
     */
    public List<ChronoInterval<T>> getIntervals() {

        return this.intervals;

    }

    /**
     * <p>Obtains an interval iterator. </p>
     *
     * @return  Iterator
     * @since   3.35/4.30
     */
    /*[deutsch]
     * <p>Liefert einen Intervall-Iterator. </p>
     *
     * @return  Iterator
     * @since   3.35/4.30
     */
    @Override
    public Iterator<ChronoInterval<T>> iterator() {

        return this.intervals.iterator();

    }

    /**
     * <p>Obtains the count of stored intervals. </p>
     *
     * @return  int
     * @since   3.35/4.30
     */
    /*[deutsch]
     * <p>Liefert die Anzahl der gespeicherten Intervalle. </p>
     *
     * @return  int
     * @since   3.35/4.30
     */
    @Override
    public int size() {

        return this.intervals.size();

    }

    /**
     * <p>Gives an answer if this instance contains no intervals. </p>
     *
     * @return  {@code true} if there are no intervals else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>Gibt eine Antwort, ob diese Instanz keine Intervalle enth&auml;lt. </p>
     *
     * @return  {@code true} if there are no intervals else {@code false}
     * @since   2.0
     */
    @Override
    public boolean isEmpty() {

        return this.intervals.isEmpty();

    }

    /**
     * <p>Queries if there is no intersection of intervals. </p>
     *
     * @return  {@code true} if there is no intersection else {@code false}
     * @since   3.24/4.20
     */
    /*[deutsch]
     * <p>Ermittelt, ob keine &Uuml;berschneidung von Intervallen existiert. </p>
     *
     * @return  {@code true} if there is no intersection else {@code false}
     * @since   3.24/4.20
     */
    public boolean isDisjunct() {

        for (int i = 0, n = this.intervals.size() - 1; i < n; i++) {
            ChronoInterval<T> current = this.intervals.get(i);
            ChronoInterval<T> next = this.intervals.get(i + 1);
            if (current.getEnd().isInfinite() || next.getStart().isInfinite()) {
                return false;
            } else if (current.getEnd().isOpen()) {
                if (this.isAfter(current.getEnd().getTemporal(), next.getStart().getTemporal())) {
                    return false;
                }
            } else if (!this.isBefore(current.getEnd().getTemporal(), next.getStart().getTemporal())) {
                return false;
            }
        }

        return true;

    }

    /**
     * <p>Queries if any interval of this collection contains given temporal. </p>
     *
     * @param   temporal    time point to be queried
     * @return  {@code true} if given time point belongs to any interval of this collection else {@code false}
     * @since   3.35/4.30
     */
    /*[deutsch]
     * <p>Fragt ab, ob irgendein Intervall dieser Menge den angegebenen Zeitpunkt enth&auml;lt. </p>
     *
     * @param   temporal    time point to be queried
     * @return  {@code true} if given time point belongs to any interval of this collection else {@code false}
     * @since   3.35/4.30
     */
    public boolean encloses(T temporal) {

        for (ChronoInterval<T> interval : this.intervals) {
            if (interval.contains(temporal)) {
                return true;
            } else if (interval.isAfter(temporal)) {
                break;
            }
        }

        return false;

    }

    /**
     * <p>Queries if given interval is stored in this collection. </p>
     *
     * @param   interval    the interval to be checked
     * @return  boolean
     * @since   3.35/4.30
     */
    /*[deutsch]
     * <p>Ermittelt, ob das angegebene Intervall in dieser Menge gespeichert ist. </p>
     *
     * @param   interval    the interval to be checked
     * @return  boolean
     * @since   3.35/4.30
     */
    public boolean contains(ChronoInterval<T> interval) {

        for (ChronoInterval<T> i : this.intervals) {
            if (i.equals(interval)) {
                return true;
            }
        }

        return false;

    }

    /**
     * <p>Returns the overall minimum of this interval collection. </p>
     *
     * <p>The minimum is always inclusive, if finite. </p>
     *
     * @return  lower limit of this instance or {@code null} if infinite
     * @throws  NoSuchElementException if there are no intervals
     * @since   2.0
     * @see     #isEmpty()
     */
    /*[deutsch]
     * <p>Liefert das totale Minimum dieser Intervall-Menge. </p>
     *
     * <p>Das Minimum ist immer inklusive, wenn endlich. </p>
     *
     * @return  lower limit of this instance or {@code null} if infinite
     * @throws  NoSuchElementException if there are no intervals
     * @since   2.0
     * @see     #isEmpty()
     */
    public T getMinimum() {

        if (this.isEmpty()) {
            throw new NoSuchElementException(
                "Empty time windows have no minimum.");
        }

        return this.intervals.get(0).getStart().getTemporal();

    }

    /**
     * <p>Returns the overall maximum of this interval collection. </p>
     *
     * <p>The maximum is always inclusive, if finite. </p>
     *
     * @return  upper limit of this instance or {@code null} if infinite
     * @throws  NoSuchElementException if there are no intervals
     * @since   2.0
     * @see     #isEmpty()
     */
    /*[deutsch]
     * <p>Liefert das totale Maximum dieser Intervall-Menge. </p>
     *
     * <p>Das Maximum ist immer inklusive, wenn endlich. </p>
     *
     * @return  upper limit of this instance or {@code null} if infinite
     * @throws  NoSuchElementException if there are no intervals
     * @since   2.0
     * @see     #isEmpty()
     */
    public T getMaximum() {

        if (this.isEmpty()) {
            throw new NoSuchElementException(
                "Empty time windows have no maximum.");
        }

        int n = this.intervals.size();
        Boundary<T> upper = this.intervals.get(n - 1).getEnd();
        T max = upper.getTemporal();

        if (upper.isInfinite()) {
            return null;
        }

        if (this.isCalendrical()) {
            if (upper.isOpen()) {
                max = this.getTimeLine().stepBackwards(max);
            }

            for (int i = n - 2; i >= 0; i--) {
                Boundary<T> test = this.intervals.get(i).getEnd();
                T candidate = test.getTemporal();

                if (test.isInfinite()) {
                    return null;
                } else if (test.isOpen()) {
                    candidate = this.getTimeLine().stepBackwards(candidate);
                }

                if (this.isAfter(candidate, max)) {
                    max = candidate;
                }
            }
        } else {
            T last = null;

            if (upper.isClosed()) {
                T next = this.getTimeLine().stepForward(max);
                if (next == null) {
                    last = max;
                } else {
                    max = next;
                }
            }

            for (int i = n - 2; i >= 0; i--) {
                Boundary<T> test = this.intervals.get(i).getEnd();
                T candidate = test.getTemporal();

                if (test.isInfinite()) {
                    return null;
                } else if (last != null) {
                    continue;
                } else if (test.isClosed()) {
                    T next = this.getTimeLine().stepForward(candidate);
                    if (next == null) {
                        last = candidate;
                        continue;
                    } else {
                        candidate = next;
                    }
                }

                if (this.isAfter(candidate, max)) {
                    max = candidate;
                }
            }

            if (last != null) {
                max = last;
            } else {
                max = this.getTimeLine().stepBackwards(max);
            }
        }

        return max;

    }

    /**
     * <p>Yields the full min-max-range of this instance. </p>
     *
     * @return  minimum range interval spanning over all enclosed intervals
     * @since   3.7/4.5
     */
    /*[deutsch]
     * <p>Liefert den vollen min-max-Bereich dieser Instanz. </p>
     *
     * @return  minimum range interval spanning over all enclosed intervals
     * @since   3.7/4.5
     */
    public ChronoInterval<T> getRange() {

        Boundary<T> start = Boundary.infinitePast();
        Boundary<T> end = Boundary.infiniteFuture();

        T min = this.getMinimum();
        T max = this.getMaximum();

        if (min != null) {
            start = Boundary.ofClosed(min);
        }

        if (max != null) {
            if (this.isCalendrical()) {
                end = Boundary.ofClosed(max);
            } else {
                T max2 = this.getTimeLine().stepForward(max);
                if (max2 != null) {
                    end = Boundary.ofOpen(max2);
                } else {
                    end = Boundary.ofClosed(max);
                }
            }
        }

        return this.newInterval(start, end);

    }

    /**
     * <p>Adds the given interval to this interval collection. </p>
     *
     * <p>An empty interval will be ignored. </p>
     *
     * @param   interval    the new interval to be added
     * @return  new IntervalCollection-instance containing a sum of
     *          the own intervals and the given one while this instance
     *          remains unaffected
     * @throws  IllegalArgumentException if given interval is finite and has
     *          open start which cannot be adjusted to one with closed start
     * @since   2.0
     */
    /*[deutsch]
     * <p>F&uuml;gt das angegebene Intervall hinzu. </p>
     *
     * <p>Ein leeres Intervall wird ignoriert. </p>
     *
     * @param   interval    the new interval to be added
     * @return  new IntervalCollection-instance containing a sum of
     *          the own intervals and the given one while this instance
     *          remains unaffected
     * @throws  IllegalArgumentException if given interval is finite and has
     *          open start which cannot be adjusted to one with closed start
     * @since   2.0
     */
    public IntervalCollection<T> plus(ChronoInterval<T> interval) {

        if (interval.isEmpty()) {
            return this;
        }

        List<ChronoInterval<T>> windows = new ArrayList<>(this.intervals);
        windows.add(this.adjust(interval));
        windows.sort(this.getComparator());
        return this.create(windows);

    }

    /**
     * <p>Adds the given intervals to this interval collection. </p>
     *
     * <p>Empty intervals will be ignored. </p>
     *
     * @param   intervals       the new intervals to be added
     * @return  new IntervalCollection-instance containing a sum of
     *          the own intervals and the given one while this instance remains unaffected
     * @throws  IllegalArgumentException if given list contains a finite
     *          interval with open start which cannot be adjusted to one with closed start
     * @since   3.35/4.30
     */
    /*[deutsch]
     * <p>F&uuml;gt die angegebenen Intervalle hinzu. </p>
     *
     * <p>Leere Intervalle werden ignoriert. </p>
     *
     * @param   intervals       the new intervals to be added
     * @return  new IntervalCollection-instance containing a sum of
     *          the own intervals and the given one while this instance remains unaffected
     * @throws  IllegalArgumentException if given list contains a finite
     *          interval with open start which cannot be adjusted to one with closed start
     * @since   3.35/4.30
     */
    public IntervalCollection<T> plus(Collection<? extends ChronoInterval<T>> intervals) {

        if (intervals.isEmpty()) {
            return this;
        }

        List<ChronoInterval<T>> windows = new ArrayList<>(this.intervals);

        for (ChronoInterval<T> i : intervals) {
            if (!i.isEmpty()) {
                windows.add(this.adjust(i));
            }
        }

        windows.sort(this.getComparator());
        return this.create(windows);

    }

    /**
     * <p>Equivalent to {@code plus(other.getIntervals())}. </p>
     *
     * @param   other       another interval collection whose intervals are to be added to this instance
     * @return  new interval collection containing the intervals of this instance and the argument
     * @throws  IllegalArgumentException if given collection contains a finite
     *          interval with open start which cannot be adjusted to one with closed start
     * @since   3.7/4.5
     */
    /*[deutsch]
     * <p>&Auml;quivalent zu {@code plus(other.getIntervals())}. </p>
     *
     * @param   other       another interval collection whose intervals are to be added to this instance
     * @return  new interval collection containing the intervals of this instance and the argument
     * @throws  IllegalArgumentException if given collection contains a finite
     *          interval with open start which cannot be adjusted to one with closed start
     * @since   3.7/4.5
     */
    public IntervalCollection<T> plus(IntervalCollection<T> other) {

        if (this == other) {
            return this;
        }

        return this.plus(other.getIntervals());

    }

    /**
     * <p>Subtracts all timepoints of given interval from this interval collection. </p>
     *
     * @param   interval    other interval to be subtracted from this
     * @return  new interval collection containing all timepoints of
     *          this instance excluding those of given interval
     * @throws  IllegalArgumentException if given interval is finite and has
     *          open start which cannot be adjusted to one with closed start
     * @since   2.2
     */
    /*[deutsch]
     * <p>Subtrahiert alle im angegebenen Zeitintervall enthaltenen
     * Zeitpunkte von dieser Intervallmenge. </p>
     *
     * @param   interval    other interval to be subtracted from this
     * @return  new interval collection containing all timepoints of
     *          this instance excluding those of given interval
     * @throws  IllegalArgumentException if given interval is finite and has
     *          open start which cannot be adjusted to one with closed start
     * @since   2.2
     */
    public IntervalCollection<T> minus(ChronoInterval<T> interval) {

        if (this.isEmpty() || interval.isEmpty()) {
            return this;
        }

        ChronoInterval<T> minuend = this.intervals.get(0);
        ChronoInterval<T> iv = this.adjust(interval);

        if (
            !minuend.getStart().isInfinite()
            && iv.isBefore(minuend.getStart().getTemporal())
        ) {
            return this;
        }

        List<ChronoInterval<T>> parts = new ArrayList<>();
        IntervalCollection<T> subtrahend = this.create(Collections.singletonList(iv));
        IntervalCollection<T> diff = subtrahend.withComplement(minuend);

        if (!diff.isEmpty()) {
            parts.addAll(diff.intervals);
        }

        for (int i = 1, n = this.intervals.size(); i < n; i++) {
            minuend = this.intervals.get(i);

            if (
                !minuend.getStart().isInfinite()
                && iv.isBefore(minuend.getStart().getTemporal())
            ) {
                parts.add(minuend);
                for (int j = i + 1; j < n; j++) {
                    parts.add(this.intervals.get(j));
                }
                break; // short cut
            } else {
                diff = subtrahend.withComplement(minuend);
                if (!diff.isEmpty()) {
                    parts.addAll(diff.intervals);
                }
            }
        }

        parts.sort(this.getComparator());
        return this.create(parts);

    }

    /**
     * <p>Subtracts all timepoints of given intervals from this interval
     * collection. </p>
     *
     * @param   intervals   collection of intervals to be subtracted
     * @return  new interval collection containing all timepoints of
     *          this instance excluding those of given intervals
     * @throws  IllegalArgumentException if given list contains a finite
     *          interval with open start which cannot be adjusted to one
     *          with closed start
     * @since   3.35/4.30
     */
    /*[deutsch]
     * <p>Subtrahiert alle in den angegebenen Zeitintervallen enthaltenen
     * Zeitpunkte von dieser Intervallmenge. </p>
     *
     * @param   intervals   collection of intervals to be subtracted
     * @return  new interval collection containing all timepoints of
     *          this instance excluding those of given intervals
     * @throws  IllegalArgumentException if given list contains a finite
     *          interval with open start which cannot be adjusted to one
     *          with closed start
     * @since   3.35/4.30
     */
    public IntervalCollection<T> minus(Collection<? extends ChronoInterval<T>> intervals) {

        if (this.isEmpty() || intervals.isEmpty()) {
            return this;
        }

        List<ChronoInterval<T>> parts = new ArrayList<>();
        List<ChronoInterval<T>> list = new ArrayList<>();

        for (ChronoInterval<T> i : intervals) {
            if (!i.isEmpty()) {
                list.add(this.adjust(i));
            }
        }

        list.sort(this.getComparator());
        IntervalCollection<T> subtrahend = this.create(list);

        for (int i = 0, n = this.intervals.size(); i < n; i++) {
            ChronoInterval<T> minuend = this.intervals.get(i);
            IntervalCollection<T> diff = subtrahend.withComplement(minuend);

            if (!diff.isEmpty()) {
                parts.addAll(diff.intervals);
            }
        }

        parts.sort(this.getComparator());
        return this.create(parts);

    }

    /**
     * <p>Equivalent to {@code minus(other.getIntervals())}. </p>
     *
     * @param   other       another interval collection whose intervals are to be subtracted from this instance
     * @return  new interval collection containing all timepoints of this instance excluding those of argument
     * @throws  IllegalArgumentException if given collection contains a finite
     *          interval with open start which cannot be adjusted to one with closed start
     * @since   3.7/4.5
     */
    /*[deutsch]
     * <p>&Auml;quivalent zu {@code minus(other.getIntervals())}. </p>
     *
     * @param   other       another interval collection whose intervals are to be subtracted from this instance
     * @return  new interval collection containing all timepoints of this instance excluding those of argument
     * @throws  IllegalArgumentException if given collection contains a finite
     *          interval with open start which cannot be adjusted to one with closed start
     * @since   3.7/4.5
     */
    public IntervalCollection<T> minus(IntervalCollection<T> other) {

        if (this == other) {
            List<ChronoInterval<T>> zero = Collections.emptyList();
            return this.create(zero);
        }

        return this.minus(other.getIntervals());

    }

    /**
     * <p>Determines a filtered version of this interval collection within
     * given range. </p>
     *
     * @param   timeWindow  time window filter
     * @return  new interval collection containing only timepoints within
     *          given range
     * @throws  IllegalArgumentException if given window is finite and has
     *          open start which cannot be adjusted to one with closed start
     * @since   2.1
     */
    /*[deutsch]
     * <p>Bestimmt eine gefilterte Version dieser Intervallmenge
     * innerhalb der angegebenen Grenzen. </p>
     *
     * @param   timeWindow  time window filter
     * @return  new interval collection containing only timepoints within
     *          given range
     * @throws  IllegalArgumentException if given window is finite and has
     *          open start which cannot be adjusted to one with closed start
     * @since   2.1
     */
    public IntervalCollection<T> withTimeWindow(ChronoInterval<T> timeWindow) {

        return this.withFilter(this.adjust(timeWindow));

    }

    /**
     * <p>Determines the complement of this interval collection within
     * given range. </p>
     *
     * @param   timeWindow  time window filter
     * @return  new interval collection containing all timepoints within
     *          given range which do not belong to this instance
     * @throws  IllegalArgumentException if given window is finite and has
     *          open start which cannot be adjusted to one with closed start
     * @since   2.1
     */
    /*[deutsch]
     * <p>Bestimmt die Komplement&auml;rmenge zu dieser Intervallmenge
     * innerhalb der angegebenen Grenzen. </p>
     *
     * @param   timeWindow  time window filter
     * @return  new interval collection containing all timepoints within
     *          given range which do not belong to this instance
     * @throws  IllegalArgumentException if given window is finite and has
     *          open start which cannot be adjusted to one with closed start
     * @since   2.1
     */
    public IntervalCollection<T> withComplement(ChronoInterval<T> timeWindow) {

        if (timeWindow.isEmpty()) {
            List<ChronoInterval<T>> zero = Collections.emptyList();
            return this.create(zero);
        }

        ChronoInterval<T> window = this.adjust(timeWindow);
        IntervalCollection<T> coll = this.withFilter(window);

        if (coll.isEmpty()) {
            return this.create(Collections.singletonList(window));
        }

        Boundary<T> lower = window.getStart();
        Boundary<T> upper = window.getEnd();
        List<ChronoInterval<T>> gaps = new ArrayList<>();

        // left edge
        T min = coll.getMinimum();

        if (min != null) {
            if (lower.isInfinite()) {
                this.addLeft(gaps, min);
            } else {
                T s = lower.getTemporal();
                if (lower.isOpen()) {
                    s = this.getTimeLine().stepBackwards(s);
                    if (s == null) {
                        this.addLeft(gaps, min);
                    } else {
                        this.addLeft(gaps, s, min);
                    }
                } else {
                    this.addLeft(gaps, s, min);
                }
            }
        }

        // inner gaps
        gaps.addAll(coll.withGaps().getIntervals());

        // right edge
        T max = coll.getMaximum();

        if (max != null) {
            T s = this.getTimeLine().stepForward(max);
            if (s != null) {
                Boundary<T> bs = Boundary.ofClosed(s);
                Boundary<T> be;
                if (upper.isInfinite()) {
                    be = upper;
                    gaps.add(this.newInterval(bs, be));
                } else if (this.isCalendrical()) {
                    if (upper.isClosed()) {
                        be = upper;
                    } else {
                        T e = upper.getTemporal();
                        e = this.getTimeLine().stepBackwards(e);
                        be = Boundary.ofClosed(e);
                    }
                    if (!this.isAfter(s, be.getTemporal())) {
                        gaps.add(this.newInterval(bs, be));
                    }
                } else {
                    if (upper.isOpen()) {
                        be = upper;
                    } else {
                        T e = upper.getTemporal();
                        e = this.getTimeLine().stepForward(e);
                        if (e == null) {
                            be = Boundary.infiniteFuture();
                        } else {
                            be = Boundary.ofOpen(e);
                        }
                    }
                    if (this.isBefore(s, be.getTemporal())) {
                        gaps.add(this.newInterval(bs, be));
                    }
                }
            }
        }

        return this.create(gaps);

    }

    /**
     * <p>Searches for all gaps with time points which are not covered by any
     * interval of this instance. </p>
     *
     * <p><img src="doc-files/withGaps.jpg" alt="withGaps"></p>
     *
     * @return  new interval collection containing the inner gaps between
     *          the own intervals while this instance remains unaffected
     * @since   2.0
     */
    /*[deutsch]
     * <p>Sucht die L&uuml;cken mit allen Zeitpunkten, die nicht zu irgendeinem
     * Intervall dieser Instanz geh&ouml;ren. </p>
     *
     * <p><img src="doc-files/withGaps.jpg" alt="withGaps"></p>
     *
     * @return  new interval collection containing the inner gaps between
     *          the own intervals while this instance remains unaffected
     * @since   2.0
     */
    public IntervalCollection<T> withGaps() {

        int len = this.intervals.size();

        if (len == 0) {
            return this;
        } else if (len == 1) {
            List<ChronoInterval<T>> zero = Collections.emptyList();
            return this.create(zero);
        }

        List<ChronoInterval<T>> gaps = new ArrayList<>();
        T previous = null;

        for (int i = 0, n = len - 1; i < n; i++) {
            ChronoInterval<T> current = this.intervals.get(i);

            if (current.getEnd().isInfinite()) {
                break;
            }

            T gapStart = current.getEnd().getTemporal();

            if (current.getEnd().isClosed()) {
                gapStart = this.getTimeLine().stepForward(gapStart);
                if (gapStart == null) {
                    break;
                }
            }

            if ((previous == null) || this.isAfter(gapStart, previous)) {
                previous = gapStart;
            } else {
                gapStart = previous;
            }

            T gapEnd = this.intervals.get(i + 1).getStart().getTemporal();

            if ((gapEnd == null) || !this.isAfter(gapEnd, gapStart)) {
                continue;
            }

            IntervalEdge edge = IntervalEdge.OPEN;

            if (this.isCalendrical()) {
                edge = IntervalEdge.CLOSED;
                gapEnd = this.getTimeLine().stepBackwards(gapEnd);
                if (gapEnd == null) {
                    continue;
                }
            }

            Boundary<T> s = Boundary.ofClosed(gapStart);
            Boundary<T> e = Boundary.of(edge, gapEnd);
            gaps.add(this.newInterval(s, e));
        }

        return this.create(gaps);

    }

    /**
     * <p>Combines all intervals to disjunct blocks which neither overlap nor meet each other. </p>
     *
     * <p>Any overlapping or abutting intervals will be merged to one block. If the interval boundaries
     * are still to be kept then consider {@link #withSplits()} instead. </p>
     *
     * <p><img src="doc-files/withBlocks.jpg" alt="withBlocks"></p>
     *
     * @return  new interval collection containing disjunct merged blocks
     *          while this instance remains unaffected
     * @since   2.0
     */
    /*[deutsch]
     * <p>Kombiniert alle Intervalle zu disjunkten Bl&ouml;cken, die sich
     * weder &uuml;berlappen noch ber&uuml;hren. </p>
     *
     * <p>Alle Intervalle, die sich &uuml;berlappen oder sich ber&uuml;hren, werden zu jeweils
     * einem Block verschmolzen. Wenn die Intervallgrenzen noch erhalten bleiben sollen, dann
     * ist {@link #withSplits()} wahrscheinlich die sinnvollere Methode. </p>
     *
     * <p><img src="doc-files/withBlocks.jpg" alt="withBlocks"></p>
     *
     * @return  new interval collection containing disjunct merged blocks
     *          while this instance remains unaffected
     * @since   2.0
     */
    public IntervalCollection<T> withBlocks() {

        if (this.intervals.size() < 2) {
            return this;
        }

        Boundary<T> s;
        Boundary<T> e;

        boolean calendrical = this.isCalendrical();
        IntervalEdge edge = (
            calendrical
            ? IntervalEdge.CLOSED
            : IntervalEdge.OPEN);

        List<ChronoInterval<T>> gaps = this.withGaps().intervals;
        List<ChronoInterval<T>> blocks = new ArrayList<>();
        T start = this.getMinimum();

        for (int i = 0, n = gaps.size(); i < n; i++) {
            T end = gaps.get(i).getStart().getTemporal();

            if (calendrical) {
                end = this.getTimeLine().stepBackwards(end);
            }

            s = this.createStartBoundary(start);
            e = Boundary.of(edge, end);
            blocks.add(this.newInterval(s, e));

            Boundary<T> b = gaps.get(i).getEnd();
            start = b.getTemporal();

            if (b.isClosed()) {
                start = this.getTimeLine().stepForward(start);
            }
        }

        T max = this.getMaximum();
        s = this.createStartBoundary(start);

        if ((max != null) && !calendrical) {
            max = this.getTimeLine().stepForward(max);
        }

        if (max == null) {
            e = Boundary.infiniteFuture();
        } else {
            e = Boundary.of(edge, max);
        }

        blocks.add(this.newInterval(s, e));
        return this.create(blocks);

    }

    /**
     * <p>Combines all intervals to disjunct blocks which never overlap but still might meet each other. </p>
     *
     * <p>Similar to {@link #withBlocks()} but all boundaries will be temporally conserved. </p>
     *
     * <p><img src="doc-files/withSplits.jpg" alt="withSplits"></p>
     *
     * @return  new interval collection containing disjunct splitted sections
     *          while this instance remains unaffected
     * @since   3.24/4.20
     */
    /*[deutsch]
     * <p>Kombiniert alle Intervalle zu disjunkten Bl&ouml;cken, die sich
     * nicht &uuml;berlappen, aber noch sich ber&uuml;hren k&ouml;nnen. </p>
     *
     * <p>&Auml;hnlich wie {@link #withBlocks()}, aber alle Intervallgrenzen werden zeitlich beibehalten. </p>
     *
     * <p><img src="doc-files/withSplits.jpg" alt="withSplits"></p>
     *
     * @return  new interval collection containing disjunct splitted sections
     *          while this instance remains unaffected
     * @since   3.24/4.20
     */
    public IntervalCollection<T> withSplits() {

        if (this.isDisjunct()) {
            return this;
        }

        List<Boundary<T>> dividers = new ArrayList<>(); // always finite
        Boundary<T> infinitePast = null;
        Boundary<T> infiniteFuture = null;

        for (ChronoInterval<T> interval : this.intervals) {
            Boundary<T> start = interval.getStart();
            Boundary<T> end = interval.getEnd();

            if (start.isInfinite()) {
                infinitePast = Boundary.infinitePast();
            } else {
                int index = this.searchFiniteBoundary(dividers, start);
                if (index < 0) {
                    dividers.add(-index - 1, start);
                }
            }

            if (end.isInfinite()) {
                infiniteFuture = Boundary.infiniteFuture();
            } else {
                if (end.isClosed()) {
                    T time = this.getTimeLine().stepForward(end.getTemporal());
                    if (time == null) {
                        infiniteFuture = Boundary.infiniteFuture();
                        continue;
                    }
                    end = Boundary.ofClosed(time);
                } else {
                    end = Boundary.ofClosed(end.getTemporal());
                }
                int index = this.searchFiniteBoundary(dividers, end);
                if (index < 0) {
                    dividers.add(-index - 1, end);
                }
            }
        }

        List<ChronoInterval<T>> splitted = new ArrayList<>();
        Boundary<T> start = infinitePast;

        for (Boundary<T> divider : dividers) {
            T time = divider.getTemporal();
            boolean nextInterval = this.encloses(time);
            if (start != null) {
                if (this.isCalendrical()) {
                    time = this.getTimeLine().stepBackwards(time);
                    if (time != null) {
                        splitted.add(this.newInterval(start, Boundary.ofClosed(time)));
                    }
                } else {
                    splitted.add(this.newInterval(start, Boundary.ofOpen(time)));
                }
            }
            if (nextInterval) {
                start = divider;
            } else {
                start = null;
            }
        }

        if ((start != null) && (infiniteFuture != null)) {
            splitted.add(this.newInterval(start, infiniteFuture));
        }

        return this.create(splitted);

    }

    /**
     * <p>Determines the intersection of all contained intervals. </p>
     *
     * <p>Note: This instance remains unaffected as specified for immutable
     * classes. </p>
     *
     * <p><img src="doc-files/withIntersection.jpg" alt="withIntersection"></p>
     *
     * @return  new interval collection containing the intersection interval,
     *          maybe empty (if there is no intersection)
     * @since   2.0
     */
    /*[deutsch]
     * <p>Ermittelt die Schnittmenge aller enthaltenen Intervalle. </p>
     *
     * <p>Hinweis: Diese Instanz bleibt unver&auml;ndert, weil die Klasse
     * <i>immutable</i> (unver&auml;nderlich) ist. </p>
     *
     * <p><img src="doc-files/withIntersection.jpg" alt="withIntersection"></p>
     *
     * @return  new interval collection containing the intersection interval,
     *          maybe empty (if there is no intersection)
     * @since   2.0
     */
    public IntervalCollection<T> withIntersection() {

        int len = this.intervals.size();

        if (len < 2) {
            return this;
        } else {
            return this.create(this.intersect(this.intervals));
        }

    }

    /**
     * <p>Equivalent to {@code plus(other).withBlocks()}. </p>
     *
     * <p>Note: Before version 3.7/4.5 the behaviour was just giving an unmerged collection. </p>
     *
     * @param   other       another interval collection whose intervals are to be added to this instance
     * @return  new merged interval collection with disjunct blocks
     * @since   2.0
     */
    /*[deutsch]
     * <p>&Auml;quivalent zu {@code plus(other).withBlocks()}. </p>
     *
     * <p>Hinweis: Vor der Version 3.7/4.5 war das Verhalten so, da&szlig; nur eine Intervallmenge ohne
     * notwendig disjunkte Bl&ouml;cke geliefert wurde. </p>
     *
     * @param   other       another interval collection whose intervals are to be added to this instance
     * @return  new merged interval collection with disjunct blocks
     * @since   2.0
     */
    public IntervalCollection<T> union(IntervalCollection<T> other) {

        return this.plus(other).withBlocks();

    }

    /**
     * <p>Determines the intersection. </p>
     *
     * @param   other       another interval collection
     * @return  new interval collection with disjunct blocks containing all time points in both interval collections
     * @since   3.8/4.5
     */
    /*[deutsch]
     * <p>Ermittelt die gemeinsame Schnittmenge. </p>
     *
     * @param   other       another interval collection
     * @return  new interval collection with disjunct blocks containing all time points in both interval collections
     * @since   3.8/4.5
     */
    public IntervalCollection<T> intersect(IntervalCollection<T> other) {

        if (this.isEmpty() || other.isEmpty()) {
            List<ChronoInterval<T>> zero = Collections.emptyList();
            return this.create(zero);
        }

        List<ChronoInterval<T>> list = new ArrayList<>();

        for (ChronoInterval<T> a : this.intervals) {
            for (ChronoInterval<T> b : other.intervals) {
                List<ChronoInterval<T>> candidates = new ArrayList<>(2);
                candidates.add(a);
                candidates.add(b);
                candidates.sort(this.getComparator());
                candidates = this.intersect(candidates);
                if (!candidates.isEmpty()) {
                    list.addAll(candidates);
                }
            }
        }

        list.sort(this.getComparator());
        return this.create(list).withBlocks();

    }

    /**
     * <p>Determines the difference which holds all time points either in this <i>xor</i> the other collection. </p>
     *
     * @param   other       another interval collection
     * @return  new interval collection with disjunct blocks containing all time points which are in only one
     *          of both interval collections
     * @since   3.8/4.5
     */
    /*[deutsch]
     * <p>Ermittelt die Differenz, die alle Zeitpunkte entweder in dieser oder in der anderen enth&auml;lt. </p>
     *
     * @param   other       another interval collection
     * @return  new interval collection with disjunct blocks containing all time points which are in only one
     *          of both interval collections
     * @since   3.8/4.5
     */
    public IntervalCollection<T> xor(IntervalCollection<T> other) {

        if (this.isEmpty()) {
            return other;
        } else if (other.isEmpty()) {
            return this;
        }

        T min1 = this.getMinimum();
        T max1 = this.getMaximum();
        T min2 = other.getMinimum();
        T max2 = other.getMaximum();

        T min;
        T max;

        if ((min1 == null) || (min2 == null)) {
            min = null;
        } else {
            min = (this.isAfter(min1, min2) ? min2 : min1);
        }

        if ((max1 == null) || (max2 == null)) {
            max = null;
        } else {
            max = (this.isBefore(max1, max2) ? max2 : max1);
        }

        Boundary<T> start = this.createStartBoundary(min);
        Boundary<T> end;

        if (max == null) {
            end = Boundary.infiniteFuture();
        } else if (this.isCalendrical()) {
            end = Boundary.ofClosed(max);
        } else {
            max = this.getTimeLine().stepForward(max);
            if (max == null) {
                end = Boundary.infiniteFuture();
            } else {
                end = Boundary.ofOpen(max);
            }
        }

        ChronoInterval<T> window = this.newInterval(start, end);
        List<ChronoInterval<T>> list = new ArrayList<>();
        IntervalCollection<T> ic1 = this.withComplement(window).intersect(other);
        IntervalCollection<T> ic2 = other.withComplement(window).intersect(this);
        list.addAll(ic1.getIntervals());
        list.addAll(ic2.getIntervals());
        list.sort(this.getComparator());
        return this.create(list).withBlocks();

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof IntervalCollection) {
            IntervalCollection<?> that = IntervalCollection.class.cast(obj);

            return (
                this.getTimeLine().equals(that.getTimeLine())
                && this.intervals.equals(that.intervals)
            );
        }

        return false;

    }

    @Override
    public int hashCode() {

        return this.intervals.hashCode();

    }

    /**
     * <p>For debugging purposes. </p>
     *
     * @return  String
     */
    /*[deutsch]
     * <p>F&uuml;r Debugging-Zwecke. </p>
     *
     * @return  String
     */
    @Override
    public String toString() {

        int n = this.intervals.size();
        StringBuilder sb = new StringBuilder(n * 30);
        sb.append('{');

        for (int i = 0; i < n; i++) {
            sb.append(this.intervals.get(i));

            if (i < n - 1) {
                sb.append(',');
            }
        }

        return sb.append('}').toString();

    }

    // Anzahl der internen Intervalle
    int getSize() {

        return this.intervals.size();

    }

    /**
     * <p>Definiert ein Vergleichsobjekt zum Sortieren der Intervalle
     * zuerst nach dem Start und dann nach dem Ende. </p>
     *
     * @return  Comparator for intervals
     */
    abstract Comparator<ChronoInterval<T>> getComparator();

    /**
     * <p>Erzeugt eine neue ge&auml;nderte Kopie dieser Instanz. </p>
     *
     * @param   intervals   new sorted list of intervals
     * @return  IntervalCollection
     */
    abstract IntervalCollection<T> create(List<ChronoInterval<T>> intervals);

    /**
     * <p>Liefert die zugeh&ouml;rige Zeitachse. </p>
     *
     * @return  TimeLine
     */
    abstract TimeLine<T> getTimeLine();

    boolean isAfter(T t1, T t2) {

        return (this.getTimeLine().compare(t1, t2) > 0);

    }

    boolean isBefore(T t1, T t2) {

        return (this.getTimeLine().compare(t1, t2) < 0);

    }

    /**
     * <p>Erzeugt ein Intervall zwischen den angegebenen Grenzen. </p>
     *
     * @return  new interval
     */
    abstract ChronoInterval<T> newInterval(
        Boundary<T> start,
        Boundary<T> end
    );

    /**
     * <p>Kalendarische Intervalle sind bevorzugt geschlossen und m&uuml;ssen
     * diese Methode so &uuml;berschreiben, da&szlig; sie {@code true}
     * zur&uuml;ckgeben. </p>
     *
     * @return  boolean
     */
    boolean isCalendrical() {

        return this.getTimeLine().isCalendrical();

    }

    private ChronoInterval<T> adjust(ChronoInterval<T> interval) {

        Boundary<T> start = interval.getStart();

        if (start.isOpen() && !start.isInfinite()) {
            T s = this.getTimeLine().stepForward(start.getTemporal());

            if (s == null) {
                throw new IllegalArgumentException(
                    "Interval start with open maximum: " + interval);
            } else {
                start = Boundary.ofClosed(s);
                return this.newInterval(start, interval.getEnd());
            }
        }

        return interval;

    }

    private IntervalCollection<T> withFilter(ChronoInterval<T> window) {

        if (window.isEmpty()) {
            List<ChronoInterval<T>> zero = Collections.emptyList();
            return this.create(zero);
        }

        Boundary<T> lower = window.getStart();
        Boundary<T> upper = window.getEnd();

        if (
            this.isEmpty()
            || (lower.isInfinite() && upper.isInfinite())
        ) {
            return this;
        }

        List<ChronoInterval<T>> parts = new ArrayList<>();

        for (ChronoInterval<T> interval : this.intervals) {
            if (
                interval.isFinite()
                && window.contains(interval.getStart().getTemporal())
                && window.contains(interval.getEnd().getTemporal())
            ) {
                parts.add(interval);
                continue;
            }

            List<ChronoInterval<T>> pair = new ArrayList<>(2);
            pair.add(window);
            pair.add(interval);
            pair.sort(this.getComparator());
            IntervalCollection<T> is = this.create(pair).withIntersection();

            if (!is.isEmpty()) {
                parts.add(is.getIntervals().get(0));
            }
        }

        return this.create(parts);

    }

    private Boundary<T> createStartBoundary(T start) {

        if (start == null) {
            return Boundary.infinitePast();
        } else {
            return Boundary.ofClosed(start);
        }

    }

    private void addLeft(
        List<ChronoInterval<T>> gaps,
        T min
    ) {

        T e = this.getTimeLine().stepBackwards(min);

        if (e != null) {
            Boundary<T> be;

            if (this.isCalendrical()) {
                be = Boundary.ofClosed(e);
            } else {
                be = Boundary.ofOpen(min);
            }

            Boundary<T> bs = Boundary.infinitePast();
            gaps.add(this.newInterval(bs, be));
        }

    }

    private void addLeft(
        List<ChronoInterval<T>> gaps,
        T start,
        T min
    ) {

        if (this.isBefore(start, min)) {
            Boundary<T> be;

            if (this.isCalendrical()) {
                be = Boundary.ofClosed(this.getTimeLine().stepBackwards(min));
            } else {
                be = Boundary.ofOpen(min);
            }

            Boundary<T> bs = Boundary.ofClosed(start);
            gaps.add(this.newInterval(bs, be));
        }

    }

    private List<ChronoInterval<T>> intersect(List<ChronoInterval<T>> components) {

        int len = components.size();

        if (len < 2) {
            return components;
        }

        T latestStart = components.get(len - 1).getStart().getTemporal();
        T earliestEnd = null;

        for (int i = 0; i < len; i++) {
            Boundary<T> b = components.get(i).getEnd();
            T candidate = b.getTemporal();

            if (b.isInfinite()) {
                continue;
            } else if (this.isCalendrical()) {
                if (b.isOpen()) {
                    candidate = this.getTimeLine().stepBackwards(candidate);
                }
            } else if (b.isClosed()) {
                candidate = this.getTimeLine().stepForward(candidate);
                if (candidate == null) {
                    continue;
                }
            }

            if ((earliestEnd == null) || this.isBefore(candidate, earliestEnd)) {
                earliestEnd = candidate;
            }
        }

        Boundary<T> s = null;
        Boundary<T> e = null;

        if (earliestEnd == null) {
            s = this.createStartBoundary(latestStart);
            e = Boundary.infiniteFuture();
        } else if (this.isCalendrical()) {
            if (!this.isBefore(earliestEnd, latestStart)) {
                s = this.createStartBoundary(latestStart);
                e = Boundary.ofClosed(earliestEnd);
            }
        } else if (this.isAfter(earliestEnd, latestStart)) {
            s = this.createStartBoundary(latestStart);
            e = Boundary.ofOpen(earliestEnd);
        }

        if ((s == null) || (e == null)) {
            return Collections.emptyList();
        } else {
            ChronoInterval<T> interval = this.newInterval(s, e);
            return Collections.singletonList(interval);
        }

    }

    private int searchFiniteBoundary(
        List<Boundary<T>> list,
        Boundary<T> key
    ) {

        int low = 0;
        int high = list.size()-1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            Boundary<T> midVal = list.get(mid);
            T t1 = midVal.getTemporal();
            T t2 = key.getTemporal();
            int cmp = this.getTimeLine().compare(t1, t2);

            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return mid; // key found
            }
        }

        return -(low + 1);  // key not found

    }

}
