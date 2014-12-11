/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (IsoInterval.java) is part of project Time4J.
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

import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoFunction;
import net.time4j.engine.Temporal;
import net.time4j.engine.TimeLine;
import net.time4j.format.Attributes;
import net.time4j.format.ChronoFormatter;
import net.time4j.format.ChronoPrinter;

import java.io.IOException;


/**
 * <p>Represents an abstract temporal interval on a timeline for
 * ISO-8601-types. </p>
 *
 * <p>Note that the start of an interval is always included. The end
 * is included for date intervals by default and excluded for other
 * interval types. This default setting can be overwritten however. </p>
 *
 * @param   <T> temporal type of time points within a given interval
 * @param   <I> generic self-referencing interval type
 * @author  Meno Hochschild
 * @since   2.0
 */
/*[deutsch]
 * <p>Repr&auml;sentiert ein abstraktes Zeitintervall auf einem
 * Zeitstrahl f&uuml;r ISO-8601-Typen. </p>
 *
 * <p>Hinweis: Der Start eines Intervalls ist immer inklusive. Das Ende
 * ist f&uuml;r Datumsintervalle inklusive und sonst exklusive per Vorgabe.
 * Diese Standardeinstellung kann jedoch &uuml;berschrieben werden. </p>
 *
 * @param   <T> temporal type of time points within a given interval
 * @param   <I> generic self-referencing interval type
 * @author  Meno Hochschild
 * @since   2.0
 */
public abstract class IsoInterval
    <T extends Temporal<? super T>, I extends IsoInterval<T, I>>
    implements ChronoInterval<T> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final ChronoFunction<ChronoDisplay, Void> NO_RESULT =
        new ChronoFunction<ChronoDisplay, Void>() {
            @Override
            public Void apply(ChronoDisplay context) {
                return null;
            }
        };

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
    IsoInterval(
        Boundary<T> start,
        Boundary<T> end
    ) {
        super();


        if (Boundary.isAfter(start, end)) { // NPE-check
            throw new IllegalArgumentException(
                "Start after end: " + start + "/" + end);
        } else if (
            end.isOpen() // NPE-check
            && start.isOpen()
            && Boundary.isSimultaneous(start, end)
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

    @Override
    public final Boundary<T> getStart() {

        return this.start;

    }

    @Override
    public final Boundary<T> getEnd() {

        return this.end;

    }

    /**
     * <p>Yields a copy of this interval with given start time. </p>
     *
     * @param   temporal    new start timepoint
     * @return  changed copy of this interval
     * @throws  IllegalArgumentException if new start is after end
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liefert eine Kopie dieses Intervalls mit der angegebenen
     * Startzeit. </p>
     *
     * @param   temporal    new start timepoint
     * @return  changed copy of this interval
     * @throws  IllegalArgumentException if new start is after end
     * @since   2.0
     */
    public I withStart(T temporal) {

        IntervalEdge edge = this.start.getEdge();
        Boundary<T> b = Boundary.of(edge, temporal);
        return this.getFactory().between(b, this.end);

    }

    /**
     * <p>Yields a copy of this interval with given end time. </p>
     *
     * @param   temporal    new end timepoint
     * @return  changed copy of this interval
     * @throws  IllegalArgumentException if new end is before start
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liefert eine Kopie dieses Intervalls mit der angegebenen Endzeit. </p>
     *
     * @param   temporal    new end timepoint
     * @return  changed copy of this interval
     * @throws  IllegalArgumentException if new end is before start
     * @since   2.0
     */
    public I withEnd(T temporal) {

        IntervalEdge edge = this.end.getEdge();
        Boundary<T> b = Boundary.of(edge, temporal);
        return this.getFactory().between(this.start, b);

    }

    /**
     * <p>Excludes the upper boundary from this interval. </p>
     *
     * @return  changed copy of this interval excluding upper boundary
     * @since   2.0
     */
    /*[deutsch]
     * <p>Nimmt die obere Grenze von diesem Intervall aus. </p>
     *
     * @return  changed copy of this interval excluding upper boundary
     * @since   2.0
     */
    public I withOpenEnd() {

        Boundary<T> b;

        if (this.end.isInfinite()) {
            b = Boundary.infiniteFuture();
        } else {
            b = Boundary.of(IntervalEdge.OPEN, this.end.getTemporal());
        }

        return this.getFactory().between(this.start, b);

    }

    /**
     * <p>Includes the upper boundary of this interval. </p>
     *
     * @return  changed copy of this interval including upper boundary
     * @throws  IllegalStateException if the end is infinite future
     * @since   2.0
     */
    /*[deutsch]
     * <p>Schlie&szlig;t die obere Grenze dieses Intervall ein. </p>
     *
     * @return  changed copy of this interval including upper boundary
     * @throws  IllegalStateException if the end is infinite future
     * @since   2.0
     */
    public I withClosedEnd() {

        Boundary<T> b;

        if (this.getEnd().isInfinite()) {
            throw new IllegalStateException(
                "Infinite future cannot be included.");
        } else {
            b = Boundary.of(IntervalEdge.CLOSED, this.getEnd().getTemporal());
        }

        return this.getFactory().between(this.start, b);

    }

    @Override
    public boolean isFinite() {

        return !(this.start.isInfinite() || this.end.isInfinite());

    }

    @Override
    public boolean isEmpty() {

        return (
            this.isFinite()
            && this.start.getTemporal().isSimultaneous(this.end.getTemporal())
            && (this.start.getEdge() != this.end.getEdge())); // half-open

    }

    @Override
    public boolean isBefore(T temporal) {

        if (temporal == null) {
            throw new NullPointerException();
        } else if (this.end.isInfinite()) {
            return false;
        }

        T endA = this.end.getTemporal();

        if (this.end.isOpen()) {
            endA = this.getTimeLine().stepBackwards(endA);
            if (endA == null) {
                return true;
            }
        }

        return endA.isBefore(temporal);

    }

    /**
     * <p>Is this interval before the other one? </p>
     *
     * <p>Equivalent to the expression
     * {@code (precedes(other) || meets(other))}. </p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval is before the other one
     *          else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liegt dieses Intervall vor dem anderen? </p>
     *
     * <p>&Auml;quivalent zum Ausdruck
     * {@code (precedes(other) || meets(other))}. </p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval is before the other one
     *          else {@code false}
     * @since   2.0
     */
    public boolean isBefore(I other) {

        if (
            other.getStart().isInfinite()
            || this.end.isInfinite()
        ) {
            return false;
        }

        T endA = this.end.getTemporal();
        T startB = other.getStart().getTemporal();

        if (this.end.isOpen()) {
            endA = this.getTimeLine().stepBackwards(endA);
            if (endA == null) {
                return true;
            }
        }

        return endA.isBefore(startB);

    }

    @Override
    public boolean isAfter(T temporal) {

        if (temporal == null) {
            throw new NullPointerException();
        } else if (this.start.isInfinite()) {
            return false;
        }

        return this.start.getTemporal().isAfter(temporal);

    }

    /**
     * <p>Is this interval after the other one? </p>
     *
     * <p>Equivalent to the expression
     * {@code (precededBy(other) || metBy(other))}. </p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval is after the other one
     *          else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liegt dieses Intervall nach dem anderen? </p>
     *
     * <p>&Auml;quivalent zum Ausdruck
     * {@code (precededBy(other) || metBy(other))}. </p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval is after the other one
     *          else {@code false}
     * @since   2.0
     */
    public boolean isAfter(I other) {

        return other.isBefore(this.getContext());

    }

    @Override
    public boolean contains(T temporal) {

        if (temporal == null) {
            throw new NullPointerException();
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

    /**
     * <p>Does this interval contain the other one? </p>
     *
     * <p>In contrast to {@link #encloses} the interval boundaries may also
     * be equal. </p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval contains the other one
     *          else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>Enth&auml;lt dieses Intervall das andere Intervall? </p>
     *
     * <p>Im Unterschied zu {@link #encloses} d&uuml;rfen die Grenzen der
     * Intervalle auch gleich sein. </p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval contains the other one
     *          else {@code false}
     * @since   2.0
     */
    public boolean contains(I other) {

        if (!other.isFinite()) {
            return false;
        }

        T startA = this.start.getTemporal();
        T startB = other.getStart().getTemporal();

        if ((startA != null) && startA.isAfter(startB)) {
            return false;
        }

        T endA = this.end.getTemporal();
        T endB = other.getEnd().getTemporal();

        if (endA == null) {
            return true;
        }

        if (
            other.getEnd().isOpen()
            && startB.isSimultaneous(endB)
        ) {
            if (this.end.isOpen()) {
                endA = this.getTimeLine().stepBackwards(endA);
            }
            if ((endA == null) || startB.isAfter(endA)) {
                return false;
            }
        } else if (this.getFactory().isCalendrical()) {
            if (this.end.isOpen()) {
                endA = this.getTimeLine().stepBackwards(endA);
            }
            if (other.getEnd().isOpen()) {
                endB = this.getTimeLine().stepBackwards(endB);
            }
            if (
                (endA == null)
                || (endB == null) // dann startB = infinite_past
                || endA.isBefore(endB)
            ) {
                return false;
            }
        } else {
            if (this.end.isClosed()) {
                endA = this.getTimeLine().stepForward(endA);
                if (endA == null) {
                    return true;
                }
            }
            if (other.getEnd().isClosed()) {
                endB = this.getTimeLine().stepForward(endB);
                if (endB == null) {
                    return false;
                }
            }
            return !endA.isBefore(endB);
        }

        return true;

    }

    /**
     * <p>Changes this interval to an empty interval with the same
     * start anchor. </p>
     *
     * @return  new empty interval with same start
     * @throws  IllegalStateException if the start is infinite
     * @since   2.0
     */
    /*[deutsch]
     * <p>Wandelt dieses Intervall in ein leeres Intervall mit dem gleichen
     * Startanker um. </p>
     *
     * @return  new empty interval with same start
     * @throws  IllegalStateException if the start is infinite
     * @since   2.0
     */
    public I collapse() {

        if (this.start.isInfinite()) {
            throw new IllegalStateException(
                "An interval with infinite past cannot be collapsed.");
        }

        Boundary<T> b =
            Boundary.of(IntervalEdge.OPEN, this.start.getTemporal());
        return this.getFactory().between(this.start, b);

    }

    /**
     * <p>Compares the boundaries (start and end) and also the time axis
     * of this and the other interval. </p>
     *
     * <p>Note: Two intervals which are {@link #equivalentTo} to each other
     * are not always equal to each other. For example a half-open date interval
     * whose end is one day later than the end of a closed date interval with
     * same start is considered <i>equivalent</i>, but not <i>equal</i>. </p>
     *
     * @param   obj     object to be compared with this
     * @return  {@code true} if given object is also an interval on the
     *          same time axis and has the same boundaries as this interval
     *          else {@code false}
     */
    /*[deutsch]
     * <p>Vergleicht die Intervallgrenzen und die assoziierte Zeitachse
     * dieses Intervalls mit denen des angegebenen Objekts. </p>
     *
     * <p>Hinweis: Zwei Intervalle, die {@link #equivalentTo} zueinander sind,
     * sind nicht immer gleich im Sinne dieser Methode. Zum Beispiel ist ein
     * halb-offenes Datumsintervall, dessen Ende einen Tag sp&auml;ter als
     * das Ende eines geschlossenen Datumsintervalls mit gleichem Start liegt,
     * <i>&auml;quivalent</i>, aber nicht <i>gleich</i>. </p>
     *
     * @param   obj     object to be compared with this
     * @return  {@code true} if given object is also an interval on the
     *          same time axis and has the same boundaries as this interval
     *          else {@code false}
     */
    @Override
    public final boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof IsoInterval) {
            IsoInterval<?, ?> that = IsoInterval.class.cast(obj);
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

    /**
     * <p>Yields a descriptive string using the standard output
     * of the method {@code toString()} of start and end. </p>
     *
     * @return  String
     */
    /*[deutsch]
     * <p>Liefert eine Beschreibung, die auf der Standardausgabe von
     * {@code toString()} angewandt auf Start und Ende beruht.. </p>
     *
     * @return  String
     */
    @Override
    public final String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(this.start.isOpen() ? '(' : '[');
        sb.append(
            this.start.isInfinite()
            ? "-\u221E"
            : this.start.getTemporal());
        sb.append('/');
        sb.append(
            this.end.isInfinite()
            ? "+\u221E"
            : this.end.getTemporal());
        sb.append(this.end.isOpen() ? ')' : ']');
        return sb.toString();

    }

    /**
     * <p>Equivalent to
     * {@code print(printer, BracketPolicy.SHOW_WHEN_NON_STANDARD}. </p>
     *
     * @param   printer     format object for printing start and end
     * @return  formatted string in format {start}/{end}
     * @since   2.0
     * @see     BracketPolicy#SHOW_WHEN_NON_STANDARD
     * @see     #print(ChronoPrinter, BracketPolicy)
     */
    /*[deutsch]
     * <p>Entspricht
     * {@code print(printer, BracketPolicy.SHOW_WHEN_NON_STANDARD}. </p>
     *
     * @param   printer     format object for printing start and end
     * @return  formatted string in format {start}/{end}
     * @since   2.0
     * @see     BracketPolicy#SHOW_WHEN_NON_STANDARD
     * @see     #print(ChronoPrinter, BracketPolicy)
     */
    public String print(ChronoPrinter<T> printer) {

        return this.print(printer, BracketPolicy.SHOW_WHEN_NON_STANDARD);

    }

    /**
     * <p>Prints the start and end separated by a slash using given
     * formatter. </p>
     *
     * <p>Note: Infinite boundaries are printed either as &quot;-&#x221E;&quot;
     * or &quot;+&#x221E;&quot;. Example for an ISO-representation: </p>
     *
     * <pre>
     *  DateInterval interval =
     *      DateInterval.between(
     *          PlainDate.of(2014, 1, 31),
     *          PlainDate.of(2014, 4, 2));
     *  System.out.println(
     *      interval.print(
     *          Iso8601Format.BASIC_CALENDAR_DATE,
     *          BracketPolicy.SHOW_NEVER));
     *  // output: 20140131/20140402
     * </pre>
     *
     * @param   printer     format object for printing start and end
     * @param   policy      strategy for printing interval boundaries
     * @return  formatted string in format {start}/{end}
     * @since   2.0
     */
    /*[deutsch]
     * <p>Formatiert den Start und das Ende getrennt mit einem
     * Schr&auml;gstrich unter Benutzung des angegebenen Formatierers. </p>
     *
     * <p>Hinweis: Unendliche Intervallgrenzen werden entweder als
     * &quot;-&#x221E;&quot; oder &quot;+&#x221E;&quot; ausgegeben.
     * Beispiel f&uuml;r eine ISO-Darstellung: </p>
     *
     * <pre>
     *  DateInterval interval =
     *      DateInterval.between(
     *          PlainDate.of(2014, 1, 31),
     *          PlainDate.of(2014, 4, 2));
     *  System.out.println(
     *      interval.print(
     *          Iso8601Format.BASIC_CALENDAR_DATE,
     *          BracketPolicy.SHOW_NEVER));
     *  // output: 20140131/20140402
     * </pre>
     *
     * @param   printer     format object for printing start and end
     * @param   policy      strategy for printing interval boundaries
     * @return  formatted string in format {start}/{end}
     * @since   2.0
     */
    public String print(
        ChronoPrinter<T> printer,
        BracketPolicy policy
    ) {

        AttributeQuery attrs = extractDefaultAttributes(printer);
        boolean showBoundaries = policy.display(this);
        StringBuilder sb = new StringBuilder(64);

        if (showBoundaries) {
            sb.append(this.start.isOpen() ? '(' : '[');
        }

        try {
            if (this.start.isInfinite()) {
                sb.append("-\u221E");
            } else {
                printer.print(this.start.getTemporal(), sb, attrs, NO_RESULT);
            }

            sb.append('/');

            if (this.end.isInfinite()) {
                sb.append("+\u221E");
            } else {
                printer.print(this.end.getTemporal(), sb, attrs, NO_RESULT);
            }
        } catch (IOException ioe) {
            throw new AssertionError(ioe);
        }

        if (showBoundaries) {
            sb.append(this.end.isOpen() ? ')' : ']');
        }

        return sb.toString();

    }

    /**
     * <p>Does this interval equal the other one taking into account the
     * open or closed state of the boundaries? </p>
     *
     * <p>Relation diagram: </p>
     *
     * <p><img src="doc-files/equivalent.jpg" /></p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval is temporally equivalent to
     *          the other one else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>Ist dieses Intervall gleich dem anderen Intervall unter
     * Ber&uuml;cksichtigung des offen/geschlossen-Zustands der
     * Intervallgrenzen? </p>
     *
     * <p>Relation diagram: </p>
     *
     * <p><img src="doc-files/equivalent.jpg" /></p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval is temporally equivalent to
     *          the other one else {@code false}
     * @since   2.0
     */
    public boolean equivalentTo(I other) {

        if (this.getContext() == other) {
            return true;
        }

        T startA = this.start.getTemporal();
        T startB = other.getStart().getTemporal();

        if (startA == null) {
            if (startB != null) {
                return false;
            }
        } else if (startB == null) {
            return false;
        } else if (!startA.isSimultaneous(startB)) {
            return false;
        }

        T endA = this.end.getTemporal();
        T endB = other.getEnd().getTemporal();

        if (endA == null) {
            return (endB == null);
        } else if (endB == null) {
            return false;
        }

        if (this.getFactory().isCalendrical()) {
            if (this.end.isOpen()) {
                endA = this.getTimeLine().stepBackwards(endA);
            }
            if (other.getEnd().isOpen()) {
                endB = this.getTimeLine().stepBackwards(endB);
            }
        } else {
            if (this.end.isClosed()) {
                endA = this.getTimeLine().stepForward(endA);
            }
            if (other.getEnd().isClosed()) {
                endB = this.getTimeLine().stepForward(endB);
            }
        }

        if (endA == null) {
            return (endB == null);
        } else if (endB == null) {
            return false;
        } else {
            return endA.isSimultaneous(endB);
        }

    }

    /**
     * <p>Does this interval precede the other one such that there is a gap
     * between? </p>
     *
     * <p>Relation diagram: </p>
     *
     * <p><img src="doc-files/precedes.jpg" /></p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval is before the other such
     *          that there is a gap between else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liegt dieses Intervall so vor dem anderen, da&szlig; dazwischen
     * eine L&uuml;cke existiert? </p>
     *
     * <p>Relation diagram: </p>
     *
     * <p><img src="doc-files/precedes.jpg" /></p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval is before the other such
     *          that there is a gap between else {@code false}
     * @since   2.0
     */
    public boolean precedes(I other) {

        if (
            other.getStart().isInfinite()
            || this.end.isInfinite()
        ) {
            return false;
        }

        T endA = this.end.getTemporal();

        if (this.end.isClosed()) {
            endA = this.getTimeLine().stepForward(endA);
            if (endA == null) {
                return false;
            }
        }

        return endA.isBefore(other.getStart().getTemporal());

    }

    /**
     * <p>Equivalent to {@code other.precedes(this)}. </p>
     *
     * <p>Relation diagram: </p>
     *
     * <p><img src="doc-files/precededBy.jpg" /></p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval is after the other such
     *          that there is a gap between else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>&Auml;quivalent to {@code other.precedes(this)}. </p>
     *
     * <p>Relation diagram: </p>
     *
     * <p><img src="doc-files/precededBy.jpg" /></p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval is after the other such
     *          that there is a gap between else {@code false}
     * @since   2.0
     */
    public boolean precededBy(I other) {

        return other.precedes(this.getContext());

    }

    /**
     * <p>Does this interval precede the other one such that there is no gap
     * between? </p>
     *
     * <p>Relation diagram: </p>
     *
     * <p><img src="doc-files/meets.jpg" /></p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval is before the other such
     *          that there is no gap between else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liegt dieses Intervall so vor dem anderen, da&szlig; dazwischen
     * keine L&uuml;cke existiert? </p>
     *
     * <p>Relation diagram: </p>
     *
     * <p><img src="doc-files/meets.jpg" /></p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval is before the other such
     *          that there is no gap between else {@code false}
     * @since   2.0
     */
    public boolean meets(I other) {

        if (
            other.getStart().isInfinite()
            || this.end.isInfinite()
        ) {
            return false;
        }

        T endA = this.end.getTemporal();

        if (this.end.isClosed()) {
            endA = this.getTimeLine().stepForward(endA);
            if (endA == null) {
                return false;
            }
        }

        if (endA.isSimultaneous(other.getStart().getTemporal())) {
            T startA = this.start.getTemporal();
            T endB = other.getEnd().getTemporal();

            if ((startA == null) || (endB == null)) {
                return true;
            } else {
                return startA.isBefore(endB); // excludes empty.meets(empty)
            }
        }

        return false;

    }

    /**
     * <p>Equivalent to {@code other.meets(this)}. </p>
     *
     * <p>Relation diagram: </p>
     *
     * <p><img src="doc-files/metBy.jpg" /></p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval is after the other such
     *          that there is no gap between else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>&Auml;quivalent to {@code other.meets(this)}. </p>
     *
     * <p>Relation diagram: </p>
     *
     * <p><img src="doc-files/metBy.jpg" /></p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval is after the other such
     *          that there is no gap between else {@code false}
     * @since   2.0
     */
    public boolean metBy(I other) {

        return other.meets(this.getContext());

    }

    /**
     * <p>Does this interval overlaps the other one such that the start
     * of this interval is still before the start of the other one? </p>
     *
     * <p>Relation diagram: </p>
     *
     * <p><img src="doc-files/overlaps.jpg" /></p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval overlaps the other such
     *          that the start of this interval is still before the start
     *          of the other one else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>&Uuml;berlappt dieses Intervall so das andere, da&szlig; der
     * Start dieses Intervalls noch vor dem Start des anderen liegt? </p>
     *
     * <p>Relation diagram: </p>
     *
     * <p><img src="doc-files/overlaps.jpg" /></p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval overlaps the other such
     *          that the start of this interval is still before the start
     *          of the other one else {@code false}
     * @since   2.0
     */
    public boolean overlaps(I other) {

        if (
            other.getStart().isInfinite()
            || this.end.isInfinite()
        ) {
            return false;
        }

        T startA = this.start.getTemporal();
        T startB = other.getStart().getTemporal();

        if ((startA != null) && !startA.isBefore(startB)) {
            return false;
        }

        T endA = this.end.getTemporal();
        T endB = other.getEnd().getTemporal();

        if (this.getFactory().isCalendrical()) {
            if (this.end.isOpen()) {
                endA = this.getTimeLine().stepBackwards(endA);
            }

            if ((endA == null) || endA.isBefore(startB)) {
                return false;
            } else if (endB == null) {
                return true;
            }

            if (other.getEnd().isOpen()) {
                endB = this.getTimeLine().stepBackwards(endB);
            }
        } else {
            if (this.end.isClosed()) {
                endA = this.getTimeLine().stepForward(endA);
                if (endA == null) {
                    return (endB == null);
                }
            }

            if (!endA.isAfter(startB)) {
                return false;
            }

            if (other.getEnd().isClosed()) {
                endB = this.getTimeLine().stepForward(endB);
            }
        }

        return ((endB == null) || endA.isBefore(endB));

    }

    /**
     * <p>Equivalent to {@code other.overlaps(this)}. </p>
     *
     * <p>Relation diagram: </p>
     *
     * <p><img src="doc-files/overlappedBy.jpg" /></p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if the other interval overlaps this such
     *          that the start of the other one is still before the start
     *          of this interval else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>&Auml;quivalent to {@code other.overlaps(this)}. </p>
     *
     * <p>Relation diagram: </p>
     *
     * <p><img src="doc-files/overlappedBy.jpg" /></p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if the other interval overlaps this such
     *          that the start of the other one is still before the start
     *          of this interval else {@code false}
     * @since   2.0
     */
    public boolean overlappedBy(I other) {

        return other.overlaps(this.getContext());

    }

    /**
     * <p>Does this interval finish the other one such that both end
     * time points are equal and the start of this interval is after the
     * start of the other one? </p>
     *
     * <p>Relation diagram: </p>
     *
     * <p><img src="doc-files/finishes.jpg" /></p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval has the same end point as
     *          the other one and a later start else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>Beendet dieses Intervall so das andere, da&szlig; bei gleichen
     * Endzeitpunkten der Start dieses Intervalls nach dem Start des anderen
     * liegt? </p>
     *
     * <p>Relation diagram: </p>
     *
     * <p><img src="doc-files/finishes.jpg" /></p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval has the same end point as
     *          the other one and a later start else {@code false}
     * @since   2.0
     */
    public boolean finishes(I other) {

        if (this.start.isInfinite()) {
            return false;
        }

        T startA = this.start.getTemporal();
        T startB = other.getStart().getTemporal();
        T endA = this.end.getTemporal();
        T endB = other.getEnd().getTemporal();

        boolean empty = (
            this.end.isOpen()
            && (endA != null)
            && startA.isSimultaneous(endA)
        );

        if (
            empty
            || ((startB != null) && !startB.isBefore(startA))
        ) {
            return false;
        }

        if (endB == null) {
            return (endA == null);
        }
        if (endA == null) {
            return (endB == null);
        }

        if (this.getFactory().isCalendrical()) {
            if (this.end.isOpen()) {
                endA = this.getTimeLine().stepBackwards(endA);
            }
            if (other.getEnd().isOpen()) {
                endB = this.getTimeLine().stepBackwards(endB);
            }

            if ((endA == null) || (endB == null) || startA.isAfter(endB)) {
                return false;
            }
        } else {
            if (this.end.isClosed()) {
                endA = this.getTimeLine().stepForward(endA);
            }
            if (other.getEnd().isClosed()) {
                endB = this.getTimeLine().stepForward(endB);
            }

            if ((endB != null) && !startA.isBefore(endB)) {
                return false;
            }

            if (endA == null) {
                return (endB == null);
            } else if (endB == null) {
                return false;
            }
        }

        return endA.isSimultaneous(endB);

    }

    /**
     * <p>Equivalent to {@code other.finishes(this)}. </p>
     *
     * <p>Relation diagram: </p>
     *
     * <p><img src="doc-files/finishedBy.jpg" /></p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval has the same end point as
     *          the other one and an earlier start else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>&Auml;quivalent to {@code other.finishes(this)}. </p>
     *
     * <p>Relation diagram: </p>
     *
     * <p><img src="doc-files/finishedBy.jpg" /></p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval has the same end point as
     *          the other one and an earlier start else {@code false}
     * @since   2.0
     */
    public boolean finishedBy(I other) {

        return other.finishes(this.getContext());

    }

    /**
     * <p>Does this interval start the other one such that both start
     * time points are equal and the end of this interval is before the
     * end of the other one? </p>
     *
     * <p>Relation diagram: </p>
     *
     * <p><img src="doc-files/starts.jpg" /></p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval has the same start point as
     *          the other one and an earlier end else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>Beginnt dieses Intervall so das andere, da&szlig; bei gleichen
     * Beginnzeitpunkten das Ende dieses Intervalls vor dem Ende des anderen
     * liegt? </p>
     *
     * <p>Relation diagram: </p>
     *
     * <p><img src="doc-files/starts.jpg" /></p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval has the same start point as
     *          the other one and an earlier end else {@code false}
     * @since   2.0
     */
    public boolean starts(I other) {

        if (this.end.isInfinite()) {
            return false;
        }

        T startA = this.start.getTemporal();
        T startB = other.getStart().getTemporal();

        if (startB == null) {
            if (startA != null) {
                return false;
            }
        } else if (startA == null) {
            if (startB != null) {
                return false;
            }
        } else if (!startA.isSimultaneous(startB)) {
            return false;
        }

        T endA = this.end.getTemporal();
        T endB = other.getEnd().getTemporal();

        if (
            this.end.isOpen()
            && (startA != null)
            && startA.isSimultaneous(endA)
        ) {
            return true;
        }

        if (endB == null) {
            if (this.end.isClosed()) {
                return true;
            } else if (startB == null) {
                return (this.getTimeLine().stepBackwards(endA) != null);
            } else {
                return endA.isAfter(startB);
            }
        }

        if (this.getFactory().isCalendrical()) {
            if (this.end.isOpen()) {
                endA = this.getTimeLine().stepBackwards(endA);
            }
            if (other.getEnd().isOpen()) {
                endB = this.getTimeLine().stepBackwards(endB);
            }
            if ((endA == null) || (endB == null) || !endA.isBefore(endB)) {
                return false;
            }
        } else {
            if (this.end.isClosed()) {
                endA = this.getTimeLine().stepForward(endA);
                if (endA == null) {
                    return false;
                }
            }
            if (other.getEnd().isClosed()) {
                endB = this.getTimeLine().stepForward(endB);
            }
            if ((endB != null) && !endA.isBefore(endB)) {
                return false;
            }
        }

        if (this.end.isClosed()) {
            return true;
        } else if (startB == null) {
            return (this.getTimeLine().stepBackwards(endA) != null);
        } else {
            return endA.isAfter(startB);
        }

    }

    /**
     * <p>Equivalent to {@code other.starts(this)}. </p>
     *
     * <p>Relation diagram: </p>
     *
     * <p><img src="doc-files/startedBy.jpg" /></p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval has the same start point as
     *          the other one and a later end else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>&Auml;quivalent to {@code other.starts(this)}. </p>
     *
     * <p>Relation diagram: </p>
     *
     * <p><img src="doc-files/startedBy.jpg" /></p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval has the same start point as
     *          the other one and a later end else {@code false}
     * @since   2.0
     */
    public boolean startedBy(I other) {

        return other.starts(this.getContext());

    }

    /**
     * <p>Does this interval enclose the other one such that this start
     * is before the start of the other one and this end is after the end
     * of the other one? </p>
     *
     * <p>Relation diagram: </p>
     *
     * <p><img src="doc-files/encloses.jpg" /></p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval has the earlier start point and
     *          later end compared to the other one else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>Umfasst dieses Intervall so das andere, da&szlig; der Start dieses
     * Intervalls vor dem Start des anderen und das Ende dieses Intervalls
     * nach dem Ende des anderen Intervalls liegt? </p>
     *
     * <p>Relation diagram: </p>
     *
     * <p><img src="doc-files/encloses.jpg" /></p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval has the earlier start point and
     *          later end compared to the other one else {@code false}
     * @since   2.0
     */
    public boolean encloses(I other) {

        if (!other.isFinite()) {
            return false;
        }

        T startA = this.start.getTemporal();
        T startB = other.getStart().getTemporal();

        if ((startA != null) && !startA.isBefore(startB)) {
            return false;
        }

        T endA = this.end.getTemporal();
        T endB = other.getEnd().getTemporal();

        if (endA == null) {
            return true;
        }

        if (
            other.getEnd().isOpen()
            && startB.isSimultaneous(endB)
        ) {
            if (this.end.isOpen()) {
                endA = this.getTimeLine().stepBackwards(endA);
            }
            if ((endA == null) || startB.isAfter(endA)) {
                return false; // if startB == endA: interval B has zero duration
            }
        } else if (this.getFactory().isCalendrical()) {
            if (this.end.isOpen()) {
                endA = this.getTimeLine().stepBackwards(endA);
            }
            if (other.getEnd().isOpen()) {
                endB = this.getTimeLine().stepBackwards(endB);
            }
            if (
                (endA == null)
                || (endB == null) // dann startB = infinite_past
                || !endA.isAfter(endB)
            ) {
                return false;
            }
        } else {
            if (this.end.isClosed()) {
                endA = this.getTimeLine().stepForward(endA);
            }
            if (other.getEnd().isClosed()) {
                endB = this.getTimeLine().stepForward(endB);
                if (endB == null) {
                    return false;
                }
            }
            if ((endA != null) && !endA.isAfter(endB)) {
                return false;
            }
        }

        return true;

    }

    /**
     * <p>Equivalent to {@code other.encloses(this)}. </p>
     *
     * <p>Relation diagram: </p>
     *
     * <p><img src="doc-files/enclosedBy.jpg" /></p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval has the later start point and
     *          earlier end compared to the other one else {@code false}
     * @since   2.0
     */
    /*[deutsch]
     * <p>&Auml;quivalent to {@code other.encloses(this)}. </p>
     *
     * <p>Relation diagram: </p>
     *
     * <p><img src="doc-files/enclosedBy.jpg" /></p>
     *
     * @param   other   another interval whose relation to this interval
     *                  is to be investigated
     * @return  {@code true} if this interval has the later start point and
     *          earlier end compared to the other one else {@code false}
     * @since   2.0
     */
    public boolean enclosedBy(I other) {

        return other.encloses(this.getContext());

    }

    /**
     * <p>Liefert die zugeh&ouml;rige Zeitachse. </p>
     *
     * @return  associated {@code TimeLine}
     */
    abstract TimeLine<T> getTimeLine();

    /**
     * <p>Liefert die zugeh&ouml;rige Fabrik. </p>
     *
     * @return  IntervalFactory
     */
    abstract IntervalFactory<T, I> getFactory();

    /**
     * <p>Liefert die Rechenbasis zur Ermittlung einer Dauer. </p>
     *
     * @return  &auml;quivalenter Zeitpunkt bei geschlossener unterer Grenze
     * @throws  UnsupportedOperationException wenn unendlich
     */
    T getTemporalOfClosedStart() {

        T temporal = this.start.getTemporal();

        if (temporal == null) {
            throw new UnsupportedOperationException(
                "An infinite interval has no finite duration.");
        } else {
            return temporal;
        }

    }

    /**
     * <p>Liefert die Rechenbasis zur Ermittlung einer Dauer. </p>
     *
     * @return  &auml;quivalenter Zeitpunkt bei offener oberer Grenze oder
     *          {@code null} wenn angewandt auf das geschlossene Maximum
     * @throws  UnsupportedOperationException wenn unendlich
     */
    T getTemporalOfOpenEnd() {

        T temporal = this.end.getTemporal();

        if (temporal == null) {
            throw new UnsupportedOperationException(
                "An infinite interval has no finite duration.");
        } else if (this.end.isClosed()) {
            return this.getTimeLine().stepForward(temporal);
        } else {
            return temporal;
        }

    }

    /**
     * <p>Bestimmt die Standard-Attribute, wenn das Argument ein
     * Format-Objekt ist. </p>
     *
     * @param   obj     object possibly containing format attributes
     * @return  attribute query
     */
    static AttributeQuery extractDefaultAttributes(Object obj) {

        if (obj instanceof ChronoFormatter) {
            ChronoFormatter<?> fmt = ChronoFormatter.class.cast(obj);
            return fmt.getDefaultAttributes();
        } else {
            return Attributes.empty();
        }

    }

    /**
     * <p>Liefert den Selbstbezug. </p>
     *
     * @return  this instance
     */
    abstract I getContext();

}
