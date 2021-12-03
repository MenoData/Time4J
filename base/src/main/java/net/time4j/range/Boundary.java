/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Boundary.java) is part of project Time4J.
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

import net.time4j.engine.Temporal;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;


/**
 * <p>Represents an interval boundary, either the lower one or the
 * upper one. </p>
 *
 * @param   <T> generic temporal type
 * @author  Meno Hochschild
 * @since   2.0
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Intervallgrenze, die entweder die untere
 * oder die obere Grenze eines Intervalls ist. </p>
 *
 * @param   <T> generic temporal type
 * @author  Meno Hochschild
 * @since   2.0
 */
public final class Boundary<T>
    implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int NORMAL = 0;
    private static final int PAST = 1;
    private static final int FUTURE = 2;

    @SuppressWarnings("rawtypes")
    private static final Boundary INFINITE_PAST = new Boundary(false);

    @SuppressWarnings("rawtypes")
    private static final Boundary INFINITE_FUTURE = new Boundary(true);

    private static final long serialVersionUID = -8193246842948266154L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final int mode;
    private transient final IntervalEdge edge;
    private transient final T temporal;

    //~ Konstruktoren -----------------------------------------------------

    private Boundary(boolean future) {
        super();

        this.mode = (future ? FUTURE : PAST);
        this.edge = IntervalEdge.OPEN;
        this.temporal = null;

    }

    private Boundary(
        IntervalEdge edge,
        T temporal
    ) {
        super();

        if (edge == null) {
            throw new NullPointerException("Missing boundary type.");
        } else if (temporal == null) {
            throw new NullPointerException("Missing boundary time.");
        }

        this.mode = NORMAL;
        this.edge = edge;
        this.temporal = temporal;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Defines a symbolic open boundary for the infinite past. </p>
     *
     * @param   <T> generic temporal type
     * @return  open boundary in infinite past without defined time
     * @since   2.0
     */
    /*[deutsch]
     * <p>Definiert eine symbolische offene Grenze f&uuml;r die unbegrenzte
     * Vergangenheit. </p>
     *
     * @param   <T> generic temporal type
     * @return  open boundary in infinite past without defined time
     * @since   2.0
     */
    @SuppressWarnings("unchecked")
    public static <T> Boundary<T> infinitePast() {

        return (Boundary<T>) INFINITE_PAST;

    }

    /**
     * <p>Defines a symbolic open boundary for the infinite future. </p>
     *
     * @param   <T> generic temporal type
     * @return  open boundary in infinite future without defined time
     * @since   2.0
     */
    /*[deutsch]
     * <p>Definiert eine symbolische offene Grenze f&uuml;r die unbegrenzte
     * Zukunft. </p>
     *
     * @param   <T> generic temporal type
     * @return  open boundary in infinite future without defined time
     * @since   2.0
     */
    @SuppressWarnings("unchecked")
    public static <T> Boundary<T> infiniteFuture() {

        return (Boundary<T>) INFINITE_FUTURE;

    }

    /**
     * <p>Defines a finite open boundary for the specified time
     * point. </p>
     *
     * @param   <T> generic temporal type
     * @param   temporal    time of the boundary
     * @return  finite open boundary
     * @since   2.0
     * @see     #infinitePast()
     * @see     #infiniteFuture()
     * @see     #isOpen()
     */
    /*[deutsch]
     * <p>Definiert eine feste offene Grenze f&uuml;r den angegebenen
     * Zeitpunkt. </p>
     *
     * @param   <T> generic temporal type
     * @param   temporal    time of the boundary
     * @return  finite open boundary
     * @since   2.0
     * @see     #infinitePast()
     * @see     #infiniteFuture()
     * @see     #isOpen()
     */
    public static <T> Boundary<T> ofOpen(T temporal) {

        return new Boundary<>(IntervalEdge.OPEN, temporal);

    }

    /**
     * <p>Defines a finite closed boundary for the specified
     * time point. </p>
     *
     * @param   <T> generic temporal type
     * @param   temporal    time of the boundary
     * @return  finite closed boundary
     * @since   2.0
     * @see     #infinitePast()
     * @see     #infiniteFuture()
     * @see     #isClosed()
     */
    /*[deutsch]
     * <p>Definiert eine feste geschlossene Grenze f&uuml;r den
     * angegebenen Zeitpunkt. </p>
     *
     * @param   <T> generic temporal type
     * @param   temporal    time of the boundary
     * @return  finite closed boundary
     * @since   2.0
     * @see     #infinitePast()
     * @see     #infiniteFuture()
     * @see     #isClosed()
     */
    public static <T> Boundary<T> ofClosed(T temporal) {

        return new Boundary<>(IntervalEdge.CLOSED, temporal);

    }

    /**
     * <p>Definiert eine feste Grenze f&uuml;r den angegebenen
     * Zeitpunkt. </p>
     *
     * @param   <T> generic temporal type
     * @param   edge        boundary type
     * @param   temporal    time of the boundary
     * @return  finite boundary
     * @since   2.0
     * @see     #infinitePast()
     * @see     #infiniteFuture()
     * @see     #isOpen()
     * @see     #isClosed()
     */
    static <T> Boundary<T> of(
        IntervalEdge edge,
        T temporal
    ) {

        return new Boundary<>(edge, temporal);

    }

    /**
     * <p>Yields the time point of this interval boundary. </p>
     *
     * @return  time point or {@code null} if infinite
     * @since   2.0
     * @see     #isInfinite()
     */
    /*[deutsch]
     * <p>Liefert den Zeitpunkt dieser Intervallgrenze. </p>
     *
     * @return  time point or {@code null} if infinite
     * @since   2.0
     * @see     #isInfinite()
     */
    public T getTemporal() {

        return this.temporal;

    }

    /**
     * <p>Determines if this boundary is open. </p>
     *
     * <p>If open then the associated time point does not belong to a given
     * interval with this boundary. </p>
     *
     * @return  {@code true} if open else {@code false}
     * @since   2.0
     * @see     #ofOpen(Object) ofOpen(T)
     */
    /*[deutsch]
     * <p>Gibt an, ob diese Intervalgrenze offen ist. </p>
     *
     * <p>Wenn offen, dann geh&ouml;rt die assoziierte Zeit dieser Grenze
     * nicht zu einem Intervall mit dieser Grenze. </p>
     *
     * @return  {@code true} if open else {@code false}
     * @since   2.0
     * @see     #ofOpen(Object) ofOpen(T)
     */
    public boolean isOpen() {

        return (this.edge == IntervalEdge.OPEN);

    }

    /**
     * <p>Determines if this boundary is closed. </p>
     *
     * <p>If closed then the associated time point belongs to a given
     * interval with this boundary. </p>
     *
     * @return  {@code true} if closed else {@code false}
     * @since   2.0
     * @see     #ofClosed(Object) ofClosed(T)
     */
    /*[deutsch]
     * <p>Gibt an, ob diese Intervalgrenze geschlossen ist. </p>
     *
     * <p>Wenn geschlossen, dann geh&ouml;rt die assoziierte Zeit dieser Grenze
     * zu einem Intervall mit dieser Grenze. </p>
     *
     * @return  {@code true} if closed else {@code false}
     * @since   2.0
     * @see     #ofClosed(Object) ofClosed(T)
     */
    public boolean isClosed() {

        return (this.edge == IntervalEdge.CLOSED);

    }

    /**
     * <p>Determines if this boundary is infinite. </p>
     *
     * <p>If infinite then the associated time point is not defined. </p>
     *
     * @return  {@code true} if infinite else {@code false}
     * @since   2.0
     * @see     #getTemporal()
     */
    /*[deutsch]
     * <p>Gibt an, ob diese Intervalgrenze unbegrenzt ist. </p>
     *
     * <p>Wenn unbegrenzt, dann hat diese Intervallgrenze keine definierte
     * Zeit. </p>
     *
     * @return  {@code true} if infinite else {@code false}
     * @since   2.0
     * @see     #getTemporal()
     */
    public boolean isInfinite() {

        return (this.temporal == null);

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof Boundary) {
            Boundary<?> that = Boundary.class.cast(obj);
            if (
                (this.edge != that.edge)
                || (this.mode != that.mode)
            ) {
                return false;
            } else if (this.temporal == null) {
                return (that.temporal == null);
            } else {
                return this.temporal.equals(that.temporal);
            }
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        int hash = 37 + (this.temporal != null ? this.temporal.hashCode() : 0);
        hash = 83 * hash + 5 * this.mode;
        return 83 * hash + this.edge.hashCode();

    }

    /**
     * <p>Yields a descriptive string. </p>
     *
     * @return  String (either &quot;(-&#x221E;)&quot;, &quot;(+&#x221E;)&quot;
     *          or &quot;(temporal)&quot; for finite open boundary or
     *          &quot;[temporal]&quot; for finite closed boundary)
     */
    /*[deutsch]
     * <p>Liefert eine Beschreibung. </p>
     *
     * @return  String (either &quot;(-&#x221E;)&quot;, &quot;(+&#x221E;)&quot;
     *          or &quot;(temporal)&quot; for finite open boundary or
     *          &quot;[temporal]&quot; for finite closed boundary)
     */
    @Override
    public String toString() {

        if (this.mode == PAST) {
            return "(-\u221E)";
        } else if (this.mode == FUTURE) {
            return "(+\u221E)";
        } else {
            boolean open = this.isOpen();
            StringBuilder sb = new StringBuilder();
            sb.append(open ? '(' : '[');
            sb.append(this.temporal);
            sb.append(open ? ')' : ']');
            return sb.toString();
        }

    }

    /**
     * <p>Liefert den Intervallrand. </p>
     *
     * @return  IntervalEdge
     * @since   2.0
     */
    IntervalEdge getEdge() {

        return this.edge;

    }

    /**
     * <p>Rein temporaler Vergleich ohne Ansehen der open/closed-Bedingung. </p>
     *
     * @param   <T> generic temporal type
     * @param   start   starting boundary
     * @param   end     ending boundary
     * @return  {@code true} if start is after end else {@code false}
     * @since   2.0
     */
    static <T extends Temporal<? super T>> boolean isAfter(
        Boundary<T> start,
        Boundary<T> end
    ) {

        if (start.mode == PAST) {
            return false;
        } else if (start.mode == FUTURE) {
            return (end.mode != FUTURE);
        } else if (end.mode == PAST) {
            return true;
        } else if (end.mode == FUTURE) {
            return false;
        } else {
            return start.temporal.isAfter(end.temporal);
        }

    }

    /**
     * <p>Rein temporaler Vergleich ohne Ansehen der open/closed-Bedingung. </p>
     *
     * @param   <T> generic temporal type
     * @param   start   starting boundary
     * @param   end     ending boundary
     * @return  {@code true} if start is simultaneous to end else {@code false}
     * @since   2.0
     */
    static <T extends Temporal<? super T>> boolean isSimultaneous(
        Boundary<T> start,
        Boundary<T> end
    ) {

        if (start.mode == PAST) {
            return (end.mode == PAST);
        } else if (start.mode == FUTURE) {
            return (end.mode == FUTURE);
        } else if (end.mode == PAST) {
            return false;
        } else if (end.mode == FUTURE) {
            return false;
        } else {
            return start.temporal.isSimultaneous(end.temporal);
        }

    }

    /**
     * @serialData  Uses
     *              <a href="../../../serialized-form.html#net.time4j.range.SPX">
     *              a dedicated serialization form</a> as proxy. The format
     *              is bit-compressed. The first byte contains in the six most
     *              significant bits the type-ID {@code 57}. The lowest bit is
     *              {@code 1} if this instance is infinite past. The bit (2)
     *              will be set if this instance is infinite future. After this
     *              header byte and in case of finite boundary, one byte
     *              follows describing the open/closed-state. Finally the
     *              bytes for the temporal follow.
     *
     * Schematic algorithm:
     *
     * <pre>
       int header = 57;
       header &lt;&lt;= 2;

       if (this == Boundary.infinitePast()) {
           header |= 1;
           out.writeByte(header);
       } else if (this == Boundary.infiniteFuture()) {
           header |= 2;
           out.writeByte(header);
       } else {
           out.writeByte(header);
           out.writeByte(isOpen() ? 1 : 0);
           out.writeObject(getTemporal());
       }
      </pre>
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.BOUNDARY_TYPE);

    }

    /**
     * @serialData  Blocks because a serialization proxy is required.
     * @param       in      object input stream
     * @throws      InvalidObjectException (always)
     */
    private void readObject(ObjectInputStream in)
        throws IOException {

        throw new InvalidObjectException("Serialization proxy required.");

    }

}
