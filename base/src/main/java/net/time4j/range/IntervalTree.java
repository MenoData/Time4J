/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (IntervalTree.java) is part of project Time4J.
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

import java.time.Instant;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 * <p>Represents an augmented interval tree holding intervals for easy and quick search. </p>
 *
 * <p>The semantics of augmented interval trees (AVL-trees) is described for example
 * in <a href="https://en.wikipedia.org/wiki/Interval_tree">Wikipedia</a>. Empty intervals
 * are never stored. An interval tree is also like a read-only collection of intervals. </p>
 *
 * @param   <T> the temporal type of time points in intervals
 * @param   <I> the type of intervals stored in the tree
 * @author  Meno Hochschild
 * @since   3.25/4.21
 */
/*[deutsch]
 * <p>Repr&auml;sentiert einen angereicherten AVL-Baum, der Intervalle zum einfachen und schnellen
 * Suchen speichert. </p>
 *
 * <p>Die Semantik von angereicherten AVL-B&auml;men ist zum Beispiel auf
 * <a href="https://en.wikipedia.org/wiki/Interval_tree">Wikipedia</a> beschrieben.
 * Leere Intervalle werden nie gespeichert. Ein Intervallbaum verh&auml;lt sich auch
 * wie eine Nur-Lese-Collection von Intervallen. </p>
 *
 * @param   <T> the temporal type of time points in intervals
 * @param   <I> the type of intervals stored in the tree
 * @author  Meno Hochschild
 * @since   3.25/4.21
 */
public class IntervalTree<T, I extends ChronoInterval<T>>
    extends AbstractCollection<I> {

    //~ Instanzvariablen --------------------------------------------------

    private final Node<T, I> root;
    private final int size;
    private final TimeLine<T> timeLine;

    // optimization of iterator()
    private volatile List<I> intervals = null;

    //~ Konstruktoren -----------------------------------------------------

    private IntervalTree(
        Collection<I> intervals,
        TimeLine<T> timeLine
    ) {
        super();

        if (timeLine == null) {
            throw new NullPointerException("Missing timeline.");
        }

        Node<T, I> r = null;
        int count = 0;

        for (I interval : intervals) {
            if (!interval.isEmpty()) {
                r = insert(r, interval, timeLine);
                count = Math.incrementExact(count);
            }
        }

        this.root = r;
        this.size = count;
        this.timeLine = timeLine;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates an interval tree on the date axis filled with given date intervals. </p>
     *
     * @param   <I> the type of intervals stored in the tree
     * @param   intervals   collection of date intervals
     * @return  new interval tree
     * @throws  ArithmeticException if the count of intervals overflows an int
     */
    /*[deutsch]
     * <p>Erzeugt einen Intervallbaum auf der Datumsachse gef&uuml;llt mit den angegebenen Datumsintervallen. </p>
     *
     * @param   <I> the type of intervals stored in the tree
     * @param   intervals   collection of date intervals
     * @return  new interval tree
     * @throws  ArithmeticException if the count of intervals overflows an int
     */
    public static <I extends ChronoInterval<PlainDate>> IntervalTree<PlainDate, I> onDateAxis(
        Collection<I> intervals
    ) {

        return IntervalTree.on(PlainDate.axis(), intervals);

    }

    /**
     * <p>Creates an interval tree on the clock axis filled with given clock intervals. </p>
     *
     * @param   <I> the type of intervals stored in the tree
     * @param   intervals   collection of clock intervals
     * @return  new interval tree
     * @throws  ArithmeticException if the count of intervals overflows an int
     */
    /*[deutsch]
     * <p>Erzeugt einen Intervallbaum auf der Uhrzeitachse gef&uuml;llt mit den angegebenen Uhrzeitintervallen. </p>
     *
     * @param   <I> the type of intervals stored in the tree
     * @param   intervals   collection of clock intervals
     * @return  new interval tree
     * @throws  ArithmeticException if the count of intervals overflows an int
     */
    public static <I extends ChronoInterval<PlainTime>> IntervalTree<PlainTime, I> onClockAxis(
        Collection<I> intervals
    ) {

        return IntervalTree.on(PlainTime.axis(), intervals);

    }

    /**
     * <p>Creates an interval tree on the timestamp axis filled with given timestamp intervals. </p>
     *
     * @param   <I> the type of intervals stored in the tree
     * @param   intervals   collection of timestamp intervals
     * @return  new interval tree
     * @throws  ArithmeticException if the count of intervals overflows an int
     */
    /*[deutsch]
     * <p>Erzeugt einen Intervallbaum auf der kombinierten Datum-Zeit-Achse gef&uuml;llt mit den
     * angegebenen Zeitstempelintervallen. </p>
     *
     * @param   <I> the type of intervals stored in the tree
     * @param   intervals   collection of timestamp intervals
     * @return  new interval tree
     * @throws  ArithmeticException if the count of intervals overflows an int
     */
    public static <I extends ChronoInterval<PlainTimestamp>> IntervalTree<PlainTimestamp, I> onTimestampAxis(
        Collection<I> intervals
    ) {

        return IntervalTree.on(PlainTimestamp.axis(), intervals);

    }

    /**
     * <p>Creates an interval tree on the moment axis (UTC) filled with given moment intervals. </p>
     *
     * @param   <I> the type of intervals stored in the tree
     * @param   intervals   collection of moment intervals
     * @return  new interval tree
     * @throws  ArithmeticException if the count of intervals overflows an int
     */
    /*[deutsch]
     * <p>Erzeugt einen Intervallbaum auf der Momentachse (UTC) gef&uuml;llt mit den angegebenen Momentintervallen. </p>
     *
     * @param   <I> the type of intervals stored in the tree
     * @param   intervals   collection of moment intervals
     * @return  new interval tree
     * @throws  ArithmeticException if the count of intervals overflows an int
     */
    public static <I extends ChronoInterval<Moment>> IntervalTree<Moment, I> onMomentAxis(
        Collection<I> intervals
    ) {

        return IntervalTree.on(Moment.axis(), intervals);

    }

    /**
     * <p>Creates an interval tree for the legacy type {@code java.util.Date} filled with given simple intervals. </p>
     *
     * @param   intervals   collection of simple intervals
     * @return  new interval tree
     * @throws  ArithmeticException if the count of intervals overflows an int
     */
    /*[deutsch]
     * <p>Erzeugt einen Intervallbaum f&uuml;r den Type {@code java.util.Date} gef&uuml;llt mit den angegebenen
     * vereinfachten Intervallen. </p>
     *
     * @param   intervals   collection of simple intervals
     * @return  new interval tree
     * @throws  ArithmeticException if the count of intervals overflows an int
     */
    public static IntervalTree<Date, SimpleInterval<Date>> onTraditionalTimeLine(
        Collection<SimpleInterval<Date>> intervals
    ) {

        return IntervalTree.on(SimpleInterval.onTraditionalTimeLine().getTimeLine(), intervals);

    }

    /**
     * <p>Creates an interval tree for the type {@code java.time.Instant} filled with given simple intervals. </p>
     *
     * @param   intervals   collection of simple intervals
     * @return  new interval tree
     * @throws  ArithmeticException if the count of intervals overflows an int
     */
    /*[deutsch]
     * <p>Erzeugt einen Intervallbaum f&uuml;r den Type {@code java.time.Instant} gef&uuml;llt mit den angegebenen
     * vereinfachten Intervallen. </p>
     *
     * @param   intervals   collection of simple intervals
     * @return  new interval tree
     * @throws  ArithmeticException if the count of intervals overflows an int
     */
    public static IntervalTree<Instant, SimpleInterval<Instant>> onInstantTimeLine(
        Collection<SimpleInterval<Instant>> intervals
    ) {

        return IntervalTree.on(SimpleInterval.onInstantTimeLine().getTimeLine(), intervals);

    }

    /**
     * <p>Creates an interval tree on a timeline filled with given intervals. </p>
     *
     * @param   <T> the temporal type of time points in intervals
     * @param   <I> the type of intervals stored in the tree
     * @param   timeLine    the underlying timeline
     * @param   intervals   collection of intervals
     * @return  new interval tree
     * @throws  ArithmeticException if the count of intervals overflows an int
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
     * <p>Erzeugt einen Intervallbaum auf einem Zeitstrahl gef&uuml;llt mit den angegebenen Momentintervallen. </p>
     *
     * @param   <T> the temporal type of time points in intervals
     * @param   <I> the type of intervals stored in the tree
     * @param   timeLine    the underlying timeline
     * @param   intervals   collection of intervals
     * @return  new interval tree
     * @throws  ArithmeticException if the count of intervals overflows an int
     * @see     net.time4j.engine.TimeAxis
     * @see     net.time4j.engine.CalendarFamily#getTimeLine(String)
     * @see     net.time4j.engine.CalendarFamily#getTimeLine(net.time4j.engine.VariantSource)
     * @see     CalendarYear#timeline()
     * @see     CalendarQuarter#timeline()
     * @see     CalendarMonth#timeline()
     * @see     CalendarWeek#timeline()
     * @since   5.0
     */
    public static <T, I extends ChronoInterval<T>> IntervalTree<T, I> on(
        TimeLine<T> timeLine,
        Collection<I> intervals
    ) {

        return new IntervalTree<>(intervals, timeLine);

    }

    /**
     * <p>Checks if this tree contains no intervals. </p>
     *
     * @return  {@code true} if empty else {@code false}
     */
    /*[deutsch]
     * <p>Ermittelt, ob dieser Baum leer ist. </p>
     *
     * @return  {@code true} if empty else {@code false}
     */
    @Override
    public boolean isEmpty() {

        return (this.root == null);

    }

    /**
     * <p>Collects all stored intervals into a new list and then obtains an iterator for this list. </p>
     *
     * <p>This method is only useful if users really want to iterate over <strong>all</strong> stored
     * intervals. Otherwise a customized {@code Visitor}-implementation is more flexible. </p>
     *
     * @return  an {@code Iterator} which is read-only
     */
    /*[deutsch]
     * <p>Sammelt alle gespeicherten Intervalle in eine neue Liste und liefert dann einen {@code Iterator}
     * f&uuml;r diese Liste. </p>
     *
     * <p>Diese Methode ist nur sinnvoll, wenn Anwender alle gespeicherten Intervalle brauchen. Ansonsten
     * ist eine benutzerdefinierte {@code Visitor}-Implementierung flexibler. </p>
     *
     * @return  an {@code Iterator} which is read-only
     */
    @Override
    public Iterator<I> iterator() {

        List<I> i = this.intervals;

        if (i == null) {
            Collector collector = new Collector();
            this.accept(collector);
            i = Collections.unmodifiableList(collector.visited);
            this.intervals = i;
        }

        return i.iterator();

    }

    /**
     * <p>Obtains the count of stored intervals. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Ermittelt die Anzahl der gespeicherten Intervalle. </p>
     *
     * @return  int
     */
    @Override
    public int size() {

        return this.size;

    }

    /**
     * <p>Obtains a list of all stored intervals which intersect given point in time. </p>
     *
     * @param   timepoint   the point in time to be checked
     * @return  unmodifiable list of all stored intervals which contain given point in time, maybe empty
     */
    /*[deutsch]
     * <p>Liefert eine Liste aller gespeicherten Intervalle, die den angegebenen Suchzeitpunkt enthalten. </p>
     *
     * @param   timepoint   the point in time to be checked
     * @return  unmodifiable list of all stored intervals which contain given point in time, maybe empty
     */
    public List<I> findIntersections(T timepoint) {

        List<I> found = new ArrayList<>();
        findIntersections(timepoint, this.timeLine.stepForward(timepoint), this.root, found);
        return Collections.unmodifiableList(found);

    }

    /**
     * <p>Obtains a list of all stored intervals which intersect given search interval. </p>
     *
     * @param   interval    the search interval
     * @return  unmodifiable list of all stored intervals which intersect the search interval, maybe empty
     */
    /*[deutsch]
     * <p>Liefert eine Liste aller gespeicherten Intervalle, die sich mit dem angegebenen Suchintervall
     * &uuml;berschneiden. </p>
     *
     * @param   interval    the search interval
     * @return  unmodifiable list of all stored intervals which intersect the search interval, maybe empty
     */
    public List<I> findIntersections(ChronoInterval<T> interval) {

        // trivial case
        if (interval.isEmpty()) {
            return Collections.emptyList();
        }

        // make search interval half-open
        T low = interval.getStart().getTemporal();
        T high = interval.getEnd().getTemporal();

        if ((low != null) && interval.getStart().isOpen()) {
            low = this.timeLine.stepForward(low);
        }

        if ((high != null) && interval.getEnd().isClosed()) {
            high = this.timeLine.stepForward(high);
        }

        // collect recursively
        List<I> found = new ArrayList<>();
        findIntersections(low, high, this.root, found);
        return Collections.unmodifiableList(found);

    }

    /**
     * <p>Queries if given interval is stored in this tree. </p>
     *
     * @param   interval    the interval to be checked
     * @return  boolean
     */
    /*[deutsch]
     * <p>Ermittelt, ob das angegebene Intervall in diesem Baum gespeichert ist. </p>
     *
     * @param   interval    the interval to be checked
     * @return  boolean
     */
    public boolean contains(ChronoInterval<T> interval) {

        // trivial case
        if (interval.isEmpty()) {
            return false;
        }

        T low = interval.getStart().getTemporal();

        if ((low != null) && interval.getStart().isOpen()) {
            low = this.timeLine.stepForward(low);
        }

        List<ChronoInterval<T>> found = new ArrayList<>();
        this.findByEquals(interval, low, found, this.root);
        return !found.isEmpty();

    }

    /**
     * <p>Accepts given interval tree visitor. </p>
     *
     * <p>All nodes will be visited in ascending order, first sorted by start then by end. </p>
     *
     * <p>Example: </p>
     *
     * <pre>
     *     DateInterval i1 = DateInterval.between(PlainDate.of(2014, 2, 28), PlainDate.of(2014, 5, 31));
     *     DateInterval i2 = DateInterval.between(PlainDate.of(2014, 5, 31), PlainDate.of(2014, 6, 1));
     *     DateInterval i3 = DateInterval.between(PlainDate.of(2014, 6, 15), PlainDate.of(2014, 6, 30));
     *     IntervalTree&lt;PlainDate, DateInterval&gt; tree = IntervalTree.onDateAxis(Arrays.asList(i3, i1, i2));
     *
     *     tree.accept(
     *       (interval) -&gt; {
     *         System.out.println(interval);
     *         return false;
     *       }
     *     );
     *
     *     // output:
     *     [2014-02-28/2014-05-31]
     *     [2014-05-31/2014-06-01]
     *     [2014-06-15/2014-06-30]
     * </pre>
     *
     * @param   visitor     the interval tree visitor
     */
    /*[deutsch]
     * <p>Nimmt den angegebenen Baumbesucher an. </p>
     *
     * <p>Alle Knoten werden in aufsteigender Reihenfolge besucht, zuerst sortiert nach dem Start, dann
     * nach dem Ende eines Intervalls. </p>
     *
     * <p>Beispiel: </p>
     *
     * <pre>
     *     DateInterval i1 = DateInterval.between(PlainDate.of(2014, 2, 28), PlainDate.of(2014, 5, 31));
     *     DateInterval i2 = DateInterval.between(PlainDate.of(2014, 5, 31), PlainDate.of(2014, 6, 1));
     *     DateInterval i3 = DateInterval.between(PlainDate.of(2014, 6, 15), PlainDate.of(2014, 6, 30));
     *     IntervalTree&lt;PlainDate, DateInterval&gt; tree = IntervalTree.onDateAxis(Arrays.asList(i3, i1, i2));
     *
     *     tree.accept(
     *       (interval) -&gt; {
     *         System.out.println(interval);
     *         return false;
     *       }
     *     );
     *
     *     // Ausgabe:
     *     [2014-02-28/2014-05-31]
     *     [2014-05-31/2014-06-01]
     *     [2014-06-15/2014-06-30]
     * </pre>
     *
     * @param   visitor     the interval tree visitor
     */
    public void accept(Visitor<I> visitor) {

        accept(visitor, this.root);

    }

    private static <T, I extends ChronoInterval<T>> Node<T, I> insert(
        Node<T, I> node,
        I interval,
        TimeLine<T> timeLine
    ) {

        if (node == null) {
            return new Node<>(interval);
        }

        if (compareAtStart(node.interval.getStart(), interval.getStart(), timeLine) > 0) {
            node.left = insert(node.left, interval, timeLine);
        } else {
            node.right = insert(node.right, interval, timeLine);
        }

        node.height = Math.max(getHeight(node.left), getHeight(node.right)) + 1;
        node.max = findMax(node, timeLine);
        int balance = getBalance(node);

        if (balance < -1) {
            if (getBalance(node.right) > 0) {
                node.right = rightRotate(node.right, timeLine);
            }
            return leftRotate(node, timeLine);
        } else if (balance > 1) {
            if (getBalance(node.left) < 0) {
                node.left = leftRotate(node.left, timeLine);
            }
            return rightRotate(node, timeLine);
        }

        return node;

    }

    private static <T, I extends ChronoInterval<T>> Node<T, I> leftRotate(
        Node<T, I> n,
        TimeLine<T> timeLine
    ) {

        Node<T, I> r = n.right;
        n.right = r.left;
        r.left = n;
        n.height = Math.max(getHeight(n.left), getHeight(n.right)) + 1;
        r.height = Math.max(getHeight(r.left), getHeight(r.right)) + 1;
        n.max = findMax(n, timeLine);
        r.max = findMax(r, timeLine);
        return r;

    }

    private static <T, I extends ChronoInterval<T>> Node<T, I> rightRotate(
        Node<T, I> n,
        TimeLine<T> timeLine
    ) {

        Node<T, I> r = n.left;
        n.left = r.right;
        r.right = n;
        n.height = Math.max(getHeight(n.left), getHeight(n.right)) + 1;
        r.height = Math.max(getHeight(r.left), getHeight(r.right)) + 1;
        n.max = findMax(n, timeLine);
        r.max = findMax(r, timeLine);
        return r;

    }

    private static int getHeight(Node<?, ?> node) {

        return ((node == null) ? 0 : node.height);

    }

    private static <T, I extends ChronoInterval<T>> Boundary<T> findMax(
        Node<T, I> n,
        TimeLine<T> timeLine
    ) {

        if ((n.left == null) && (n.right == null)) {
            return n.max;
        } else if (n.left == null) {
            if (compareAtEnd(n.right.max, n.max, timeLine) > 0) {
                return n.right.max;
            } else {
                return n.max;
            }
        } else if (n.right == null) {
            if (compareAtEnd(n.left.max, n.max, timeLine) > 0) {
                return n.left.max;
            } else {
                return n.max;
            }
        }

        Boundary<T> maximized;

        if (compareAtEnd(n.left.max, n.right.max, timeLine) < 0) {
            maximized = n.right.max;
        } else {
            maximized = n.left.max;
        }

        if (compareAtEnd(n.max, maximized, timeLine) > 0) {
            maximized = n.max;
        }

        return maximized;

    }

    private static <T> int compareAtStart(
        Boundary<T> b1,
        Boundary<T> b2,
        TimeLine<T> timeLine
    ) {

        if (b1.isInfinite()) {
            return ((b2.isInfinite() ? 0 : -1));
        } else if (b2.isInfinite()) {
            return 1;
        }

        T t1 = b1.getTemporal();
        T t2 = b2.getTemporal();

        if (b1.getEdge() != b2.getEdge()) {
            if (b1.isOpen() && b2.isClosed()) {
                t2 = timeLine.stepBackwards(t2);
                if (t2 == null) {
                    return 1;
                }
            } else if (b1.isClosed() && b2.isOpen()) {
                t1 = timeLine.stepBackwards(t1);
                if (t1 == null) {
                    return -1;
                }
            }
        }

        return timeLine.compare(t1, t2);

    }

    private static <T> int compareAtEnd(
        Boundary<T> b1,
        Boundary<T> b2,
        TimeLine<T> timeLine
    ) {

        if (b1.isInfinite()) {
            return ((b2.isInfinite() ? 0 : 1));
        } else if (b2.isInfinite()) {
            return -1;
        }

        T t1 = b1.getTemporal();
        T t2 = b2.getTemporal();

        if (b1.getEdge() != b2.getEdge()) {
            if (b1.isOpen() && b2.isClosed()) {
                t2 = timeLine.stepForward(t2);
                if (t2 == null) {
                    return -1;
                }
            } else if (b1.isClosed() && b2.isOpen()) {
                t1 = timeLine.stepForward(t1);
                if (t1 == null) {
                    return 1;
                }
            }
        }

        return timeLine.compare(t1, t2);

    }

    private static int getBalance(Node<?, ?> node) {

        if (node == null) {
            return 0;
        }

        return getHeight(node.left) - getHeight(node.right);

    }

    private void findIntersections(
        T low, // inclusive if not null
        T high, // exclusive if not null
        Node<T, I> node,
        List<I> found
    ) {

        if (node == null) {
            return;
        }

        // If the node's max interval is before the search interval, no children will match (short-cut)
        if ((low != null) && !node.max.isInfinite()) {
            if (node.max.isOpen()) {
                if (this.timeLine.compare(node.max.getTemporal(), low) <= 0) {
                    return;
                }
            } else if (this.timeLine.compare(node.max.getTemporal(), low) < 0) {
                return;
            }
        }

        // left children
        findIntersections(low, high, node.left, found);

        // check: (start < high)
        T start = node.interval.getStart().getTemporal();
        boolean c1 = (start == null || high == null);

        if (!c1) {
            if (node.interval.getStart().isClosed()) {
                c1 = (this.timeLine.compare(start, high) < 0);
            } else {
                T startClosed = this.timeLine.stepForward(start);
                c1 = ((startClosed != null) && (this.timeLine.compare(startClosed, high) < 0));
            }
        }

        if (c1) {
            // check: (end > low)
            T end = node.interval.getEnd().getTemporal();
            boolean c2 = (end == null || low == null);

            if (!c2) {
                if (node.interval.getEnd().isOpen()) {
                    c2 = (this.timeLine.compare(low, end) < 0);
                } else {
                    c2 = (this.timeLine.compare(low, end) <= 0);
                }
            }

            if (c2) {
                found.add(node.interval);
            }
        } else {
            return; // short-cut: start >= high (interval nodes are primarily sorted by start)
        }

        // right children
        findIntersections(low, high, node.right, found);

    }

    private boolean findByEquals(
        ChronoInterval<T> interval,
        T low,
        List<ChronoInterval<T>> found,
        Node<T, I> node
    ) {

        if (node == null) {
            return false;
        }

        if (this.findByEquals(interval, low, found, node.left)) {
            return true;
        }

        if (interval.equals(node.interval)) {
            found.add(interval);
            return true; // cancel search
        }

        // short-cut (all further stored intervals are after given search interval)
        if (
            ((low != null) && node.interval.isAfter(low))
            || (interval.getStart().isInfinite() && !node.interval.getStart().isInfinite())
        ) {
            return true; // cancel search
        }

        return this.findByEquals(interval, low, found, node.right);

    }

    private static <T, I extends ChronoInterval<T>> boolean accept(
        Visitor<I> visitor,
        Node<T, I> node
    ) {

        if (node == null) {
            return false;
        }

        if (accept(visitor, node.left)) {
            return true;
        }

        if (visitor.visited(node.interval)) {
            return true;
        }

        return accept(visitor, node.right);

    }

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Callback interface for tree traversal according to the visitor pattern design. </p>
     *
     * @param   <I> the type of visited intervals
     */
    /*[deutsch]
     * <p>Callback-Interface zum Abwandern eines Intervallbaums entsprechend dem Besucher-Entwurfsmuster. </p>
     *
     * @param   <I> the type of visited intervals
     */
    @FunctionalInterface
    public interface Visitor<I> {

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Called recursively during traversal of tree for every interval node. </p>
         *
         * @param   interval    visited interval
         * @return  {@code true} if further traversal shall be cancelled else {@code false}
         */
        /*[deutsch]
         * <p>Wird w&auml;hrend des Abgehens des Baums rekursiv f&uuml;r jeden Intervallknoten aufgerufen. </p>
         *
         * @param   interval    visited interval
         * @return  {@code true} if further traversal shall be cancelled else {@code false}
         */
        boolean visited(I interval);

    }

    private static class Node<T, I extends ChronoInterval<T>> {

        //~ Instanzvariablen ----------------------------------------------

        private final I interval;

        // tree organization
        Node<T, I> left = null;
        Node<T, I> right = null;
        int height;
        Boundary<T> max;

        //~ Konstruktoren -------------------------------------------------

        Node(I interval) {
            super();

            this.interval = interval;
            this.height = 1;
            this.max = interval.getEnd();

        }

    }

    private class Collector
        implements Visitor<I> {

        //~ Instanzvariablen ----------------------------------------------

        private List<I> visited = new ArrayList<>(IntervalTree.this.size());

        //~ Methoden ------------------------------------------------------

        @Override
        public boolean visited(I interval) {

            this.visited.add(interval);
            return false;

        }

    }

}
