/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimeWindows.java) is part of project Time4J.
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * <p>Represents a sorted list of intervals. </p>
 *
 * <p>Any instance can first be achieved by calling one of the static
 * {@code onXYZAxis()}-methods and then be filled with any count of
 * typed intervals. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 * @concurrency <immutable>
 * @see     DateInterval#comparator()
 * @see     ClockInterval#comparator()
 * @see     TimestampInterval#comparator()
 * @see     MomentInterval#comparator()
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine sortierte Liste von Intervallen. </p>
 *
 * <p>Zuerst kann eine Instanz mit Hilfe von statischen Fabrikmethoden
 * wie {@code onXYZAxis()} erhalten und dann mit einer beliebigen Zahl
 * von typisierten Intervallen gef&uuml;llt werden. </p>
 *
 * @author  Meno Hochschild
 * @since   2.0
 * @concurrency <immutable>
 * @see     DateInterval#comparator()
 * @see     ClockInterval#comparator()
 * @see     TimestampInterval#comparator()
 * @see     MomentInterval#comparator()
 */
public abstract class TimeWindows<I>
    implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = 1L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final List<I> intervals;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>For package-private subclasses only. </p>
     */
    TimeWindows() {
        super();

        this.intervals = Collections.emptyList();

    }

    /**
     * <p>For package-private subclasses only. </p>
     *
     * @param   intervals   sorted list of intervals
     */
    TimeWindows(List<I> intervals) {
        super();

        this.intervals = Collections.unmodifiableList(intervals);

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Yields an empty instance on the date axis. </p>
     *
     * @return  empty {@code TimeWindows} for date intervals
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liefert eine leere Zeitfenster-Instanz auf der Datumsachse. </p>
     *
     * @return  empty {@code TimeWindows} for date intervals
     * @since   2.0
     */
    public static TimeWindows<DateInterval> onDateAxis() {

        return IsoTimeWindows.DATE_INSTANCE;

    }

    /**
     * <p>Yields an empty instance on the walltime axis. </p>
     *
     * @return  empty {@code TimeWindows} for clock intervals
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liefert eine leere Zeitfenster-Instanz auf der Uhrzeitachse. </p>
     *
     * @return  empty {@code TimeWindows} for clock intervals
     * @since   2.0
     */
    public static TimeWindows<ClockInterval> onClockAxis() {

        return IsoTimeWindows.CLOCK_INSTANCE;

    }

    /**
     * <p>Yields an empty instance on the timestamp axis. </p>
     *
     * @return  empty {@code TimeWindows} for timestamp intervals
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liefert eine leere Zeitfenster-Instanz auf der Zeitstempelachse. </p>
     *
     * @return  empty {@code TimeWindows} for timestamp intervals
     * @since   2.0
     */
    public static TimeWindows<TimestampInterval> onTimestampAxis() {

        return IsoTimeWindows.TIMESTAMP_INSTANCE;

    }

    /**
     * <p>Yields an empty instance on the UTC-axis. </p>
     *
     * @return  empty {@code TimeWindows} for moment intervals
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liefert eine leere Zeitfenster-Instanz auf der UTC-Achse. </p>
     *
     * @return  empty {@code TimeWindows} for moment intervals
     * @since   2.0
     */
    public static TimeWindows<MomentInterval> onMomentAxis() {

        return IsoTimeWindows.MOMENT_INSTANCE;

    }

    /**
     * <p>Returns the internal list of intervals. </p>
     *
     * @return  unmodifiable sorted list of intervals
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liefert die interne Intervall-Liste. </p>
     *
     * @return  unmodifiable sorted list of intervals
     * @since   2.0
     */
    public List<I> toIntervals() {

        return this.intervals;

    }

    /**
     * <p>Adds the given interval to this instance of time windows. </p>
     *
     * @param   interval    the new time window to be added
     * @return  new TimeWindows-instance containing a sum of the own intervals
     *          and the given one
     * @since   2.0
     */
    /*[deutsch]
     * <p>F&uuml;gt das angegebene Intervall hinzu. </p>
     *
     * @param   interval    the new time window to be added
     * @return  new TimeWindows-instance containing a sum of the own intervals
     *          and the given one
     * @since   2.0
     */
    public TimeWindows<I> append(I interval) {

        if (interval == null) {
            throw new NullPointerException("Missing interval.");
        }

        List<I> windows = new ArrayList<I>(this.intervals);
        windows.add(interval);
        Collections.sort(windows, this.getComparator());
        return this.create(windows);

    }

    /**
     * <p>Adds the given intervals to this instance of time windows. </p>
     *
     * @param   intervals   the new time windows to be added
     * @return  new TimeWindows-instance containing a sum of the own intervals
     *          and the given list
     * @since   2.0
     */
    /*[deutsch]
     * <p>F&uuml;gt die angegebenen Intervalle hinzu. </p>
     *
     * @param   intervals   the new time windows to be added
     * @return  new TimeWindows-instance containing a sum of the own intervals
     *          and the given list
     * @since   2.0
     */
    public TimeWindows<I> append(List<I> intervals) {

        List<I> windows = new ArrayList<I>(this.intervals);
        windows.addAll(intervals);
        Collections.sort(windows, this.getComparator());
        return this.create(windows);

    }

    /**
     * <p>Searches for all time points which are not covered by any interval
     * of this instance. </p>
     *
     * @return  new TimeWindows-instance containing the gaps between
     *          the own intervals
     * @since   2.0
     */
    /*[deutsch]
     * <p>Sucht nach allen Zeitpunkten, die nicht zu irgendeinem Intervall
     * dieser Instanz geh&ouml;ren. </p>
     *
     * @return  new TimeWindows-instance containing the gaps between
     *          the own intervals
     * @since   2.0
     */
    public abstract TimeWindows<I> gaps();

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof TimeWindows) {
            TimeWindows<?> that = TimeWindows.class.cast(obj);
            if (this.getTypeID() == that.getTypeID()) {
                return this.intervals.equals(that.intervals);
            }
        }

        return false;

    }

    @Override
    public int hashCode() {

        return this.intervals.hashCode();

    }

    @Override
    public String toString() {

        if (this.intervals.isEmpty()) {
            return "{}";
        }

        int n = this.intervals.size();
        int stop = n - 1;
        StringBuilder sb = new StringBuilder(n * 30);
        sb.append('{');

        for (int i = 0; ; i++) {
            sb.append(this.intervals.get(i));

            if (i == stop) {
                break;
            } else {
                sb.append(',');
            }
        }

        return sb.append('}').toString();

    }

    /**
     * <p>Defines a comparator for sorting intervals first by start then
     * by end. </p>
     *
     * @return  Comparator for intervals
     */
    abstract Comparator<I> getComparator();

    /**
     * <p>Creates a new changed copy of this instance. </p>
     *
     * @param   intervals   new sorted list of intervals
     * @return  TimeWindows
     */
    abstract TimeWindows<I> create(List<I> intervals);

    /**
     * <p>Yields the internal type identifier used in serialization. </p>
     *
     * @return  int
     */
    abstract int getTypeID();

}
