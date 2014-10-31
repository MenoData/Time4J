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

import net.time4j.engine.ChronoEntity;
import net.time4j.engine.Temporal;
import net.time4j.engine.TimeLine;
import net.time4j.format.ChronoFormatter;


/**
 * <p>Represents an abstract temporal interval on a timeline for
 * ISO-8601-types. </p>
 *
 * @param   <T> temporal type of time points within a given interval
 * @author  Meno Hochschild
 * @since   2.0
 */
/**
 * <p>Repr&auml;sentiert ein abstraktes Zeitintervall auf einem
 * Zeitstrahl f&uuml;r ISO-8601-Typen. </p>
 *
 * @param   <T> temporal type of time points within a given interval
 * @author  Meno Hochschild
 * @since   2.0
 */
public abstract class IsoInterval
    <T extends ChronoEntity<T> & Temporal<? super T>>
    implements ChronoInterval<T> {

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
     * <p>Yields a copy of this interval with given start boundary. </p>
     *
     * @param   boundary    new start interval boundary
     * @return  possibly changed copy of this interval
     * @throws  IllegalArgumentException if given boundary is infinite and
     *          the concrete interval does not support infinite boundaries
     *          or if new start is after end
     * @since   2.0
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
     * @since   2.0
     */
    public abstract IsoInterval<T> withStart(Boundary<T> boundary);

    /**
     * <p>Yields a copy of this interval with given end boundary. </p>
     *
     * @param   boundary    new end interval boundary
     * @return  possibly changed copy of this interval
     * @throws  IllegalArgumentException if given boundary is infinite and
     *          the concrete interval does not support infinite boundaries
     *          or if new end is before start
     * @since   2.0
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
     * @since   2.0
     */
    public abstract IsoInterval<T> withEnd(Boundary<T> boundary);

    /**
     * <p>Yields a copy of this interval with given start time. </p>
     *
     * @param   temporal    new start timepoint
     * @return  possibly changed copy of this interval
     * @throws  IllegalArgumentException if new start is after end
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liefert eine Kopie dieses Intervalls mit der angegebenen
     * Startzeit. </p>
     *
     * @param   temporal    new start timepoint
     * @return  possibly changed copy of this interval
     * @throws  IllegalArgumentException if new start is after end
     * @since   2.0
     */
    public abstract IsoInterval<T> withStart(T temporal);

    /**
     * <p>Yields a copy of this interval with given end time. </p>
     *
     * @param   temporal    new end timepoint
     * @return  possibly changed copy of this interval
     * @throws  IllegalArgumentException if new end is before start
     * @since   2.0
     */
    /*[deutsch]
     * <p>Liefert eine Kopie dieses Intervalls mit der angegebenen Endzeit. </p>
     *
     * @param   temporal    new end timepoint
     * @return  possibly changed copy of this interval
     * @throws  IllegalArgumentException if new end is before start
     * @since   2.0
     */
    public abstract IsoInterval<T> withEnd(T temporal);

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
        } else if (obj instanceof IsoInterval) {
            IsoInterval<?> that = (IsoInterval<?>) obj;
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
     * {@code print(formatter, BracketPolicy.SHOW_WHEN_NON_STANDARD}. </p>
     *
     * @param   formatter   format object for printing start and end
     * @return  formatted string in format {start}/{end}
     * @since   2.0
     * @see     BracketPolicy#SHOW_WHEN_NON_STANDARD
     * @see     #print(ChronoFormatter, BracketPolicy)
     */
    /*[deutsch]
     * <p>Entspricht
     * {@code print(formatter, BracketPolicy.SHOW_WHEN_NON_STANDARD}. </p>
     *
     * @param   formatter   format object for printing start and end
     * @return  formatted string in format {start}/{end}
     * @since   2.0
     * @see     BracketPolicy#SHOW_WHEN_NON_STANDARD
     * @see     #print(ChronoFormatter, BracketPolicy)
     */
    public String print(ChronoFormatter<T> formatter) {

        return this.print(formatter, BracketPolicy.SHOW_WHEN_NON_STANDARD);

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
     * @param   formatter   format object for printing start and end
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
     * @param   formatter   format object for printing start and end
     * @param   policy      strategy for printing interval boundaries
     * @return  formatted string in format {start}/{end}
     * @since   2.0
     */
    public String print(
        ChronoFormatter<T> formatter,
        BracketPolicy policy
    ) {

        boolean showBoundaries = policy.display(this);
        StringBuilder sb = new StringBuilder(64);

        if (showBoundaries) {
            sb.append(this.start.isOpen() ? '(' : '[');
        }

        if (this.start.isInfinite()) {
            sb.append("-\u221E");
        } else {
            formatter.print(this.start.getTemporal(), sb);
        }

        sb.append('/');

        if (this.end.isInfinite()) {
            sb.append("+\u221E");
        } else {
            formatter.print(this.end.getTemporal(), sb);
        }

        if (showBoundaries) {
            sb.append(this.end.isOpen() ? ')' : ']');
        }

        return sb.toString();

    }

    /**
     * <p>Liefert die zugeh&ouml;rige Zeitachse. </p>
     *
     * @return  associated {@code TimeLine}
     * @since   2.0
     */
    protected abstract TimeLine<T> getTimeLine();

    /**
     * <p>Liefert die Rechenbasis zur Ermittlung einer Dauer. </p>
     *
     * @return  &auml;quivalenter Zeitpunkt bei geschlossener unterer Grenze
     * @throws  UnsupportedOperationException wenn unendlich
     * @since   2.0
     */
    T getTemporalOfClosedStart() {

        T temporal = this.start.getTemporal();

        if (temporal == null) {
            throw new UnsupportedOperationException(
                "An infinite interval has no finite duration.");
        } if (this.start.isOpen()) {
            return this.getTimeLine().stepForward(temporal);
        } else {
            return temporal;
        }

    }

    /**
     * <p>Liefert die Rechenbasis zur Ermittlung einer Dauer. </p>
     *
     * @return  &auml;quivalenter Zeitpunkt bei offener oberer Grenze
     * @throws  UnsupportedOperationException wenn unendlich
     * @since   2.0
     */
    T getTemporalOfOpenEnd() {

        T temporal = this.end.getTemporal();

        if (temporal == null) {
            throw new UnsupportedOperationException(
                "An infinite interval has no finite duration.");
        } if (this.end.isClosed()) {
            return this.getTimeLine().stepForward(temporal);
        } else {
            return temporal;
        }

    }

}
