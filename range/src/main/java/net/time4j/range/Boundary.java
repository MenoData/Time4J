/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
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
import java.io.ObjectStreamException;
import java.io.Serializable;


/**
 * <p>Represents an interval boundary, either the lower one or the
 * upper one. </p>
 *
 * <p>Note: All {@code Temporal}-methods like {@code isAfter()} etc. only
 * compare the temporal state, not the property open/closed. </p>
 *
 * @param   <T> generic temporal type
 * @author  Meno Hochschild
 * @since   1.3
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Intervallgrenze, die entweder die untere
 * oder die obere Grenze eines Intervalls ist. </p>
 *
 * <p>Hinweis: Alle {@code Temporal}-methoden wie {@code isAfter()} etc.
 * vergleichen nur den zeitlichen Zustand, nicht die Eigenschaft
 * open/closed (offen/geschlossen). </p>
 *
 * @param   <T> generic temporal type
 * @author  Meno Hochschild
 * @since   1.3
 */
public final class Boundary<T extends Temporal<? super T>>
    implements Temporal<Boundary<T>>, Serializable {

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

    private final int mode;
    private final IntervalEdge edge;
    private final T temporal;

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
     * @since   1.3
     */
    /*[deutsch]
     * <p>Definiert eine symbolische offene Grenze f&uuml;r die unbegrenzte
     * Vergangenheit. </p>
     *
     * @param   <T> generic temporal type
     * @return  open boundary in infinite past without defined time
     * @since   1.3
     */
    @SuppressWarnings("unchecked")
    public static <T extends Temporal<? super T>> Boundary<T> infinitePast() {

        return (Boundary<T>) INFINITE_PAST;

    }

    /**
     * <p>Defines a symbolic open boundary for the infinite future. </p>
     *
     * @param   <T> generic temporal type
     * @return  open boundary in infinite future without defined time
     * @since   1.3
     */
    /*[deutsch]
     * <p>Definiert eine symbolische offene Grenze f&uuml;r die unbegrenzte
     * Zukunft. </p>
     *
     * @param   <T> generic temporal type
     * @return  open boundary in infinite future without defined time
     * @since   1.3
     */
    @SuppressWarnings("unchecked")
    public static <T extends Temporal<? super T>> Boundary<T> infiniteFuture() {

        return (Boundary<T>) INFINITE_FUTURE;

    }

    /**
     * <p>Defines a finite boundary for the specified time point. </p>
     *
     * @param   <T> generic temporal type
     * @param   edge        boundary type
     * @param   temporal    time of the boundary
     * @return  finite boundary
     * @since   1.3
     * @see     #infinitePast()
     * @see     #infiniteFuture()
     * @see     #isOpen()
     * @see     #isClosed()
     */
    /*[deutsch]
     * <p>Definiert eine feste Grenze f&uuml;r den angegebenen
     * Zeitpunkt. </p>
     *
     * @param   <T> generic temporal type
     * @param   edge        boundary type
     * @param   temporal    time of the boundary
     * @return  finite boundary
     * @since   1.3
     * @see     #infinitePast()
     * @see     #infiniteFuture()
     * @see     #isOpen()
     * @see     #isClosed()
     */
    public static <T extends Temporal<? super T>> Boundary<T> of(
        IntervalEdge edge,
        T temporal
    ) {

        return new Boundary<T>(edge, temporal);

    }

    /**
     * <p>Yields the time point of this interval boundary. </p>
     *
     * @return  time point or {@code null} if infinite
     * @since   1.3
     * @see     #isInfinite()
     */
    /*[deutsch]
     * <p>Liefert den Zeitpunkt dieser Intervallgrenze. </p>
     *
     * @return  time point or {@code null} if infinite
     * @since   1.3
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
     * @since   1.3
     * @see     #of(IntervalEdge, Temporal)
     */
    /*[deutsch]
     * <p>Gibt an, ob diese Intervalgrenze offen ist. </p>
     *
     * <p>Wenn offen, dann geh&ouml;rt die assoziierte Zeit dieser Grenze
     * nicht zu einem Intervall mit dieser Grenze. </p>
     *
     * @return  {@code true} if open else {@code false}
     * @since   1.3
     * @see     #of(IntervalEdge, Temporal)
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
     * @since   1.3
     * @see     #of(IntervalEdge, Temporal)
     */
    /*[deutsch]
     * <p>Gibt an, ob diese Intervalgrenze geschlossen ist. </p>
     *
     * <p>Wenn geschlossen, dann geh&ouml;rt die assoziierte Zeit dieser Grenze
     * zu einem Intervall mit dieser Grenze. </p>
     *
     * @return  {@code true} if closed else {@code false}
     * @since   1.3
     * @see     #of(IntervalEdge, Temporal)
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
     * @since   1.3
     * @see     #getTemporal()
     */
    /*[deutsch]
     * <p>Gibt an, ob diese Intervalgrenze unbegrenzt ist. </p>
     *
     * <p>Wenn unbegrenzt, dann hat diese Intervallgrenze keine definierte
     * Zeit. </p>
     *
     * @return  {@code true} if infinite else {@code false}
     * @since   1.3
     * @see     #getTemporal()
     */
    public boolean isInfinite() {

        return (this.temporal == null);

    }

    @Override
    public boolean isAfter(Boundary<T> other) {

        if (this.mode == PAST) {
            return false;
        } else if (this.mode == FUTURE) {
            return (other.mode != FUTURE);
        } else if (other.mode == PAST) {
            return true;
        } else if (other.mode == FUTURE) {
            return false;
        } else {
            return this.temporal.isAfter(other.temporal);
        }

    }

    @Override
    public boolean isBefore(Boundary<T> other) {

        if (this.mode == PAST) {
            return (other.mode != PAST);
        } else if (this.mode == FUTURE) {
            return false;
        } else if (other.mode == PAST) {
            return false;
        } else if (other.mode == FUTURE) {
            return true;
        } else {
            return this.temporal.isBefore(other.temporal);
        }

    }

    @Override
    public boolean isSimultaneous(Boundary<T> other) {

        if (this.mode == PAST) {
            return (other.mode == PAST);
        } else if (this.mode == FUTURE) {
            return (other.mode == FUTURE);
        } else if (other.mode == PAST) {
            return false;
        } else if (other.mode == FUTURE) {
            return false;
        } else {
            return this.temporal.isSimultaneous(other.temporal);
        }

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof Boundary) {
            Boundary<?> that = (Boundary) obj;
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
     * @return  String (either &quot;(-∞)&quot;, &quot;(+∞)&quot; or
     *          &quot;(temporal)&quot; for finite open boundary or
     *          &quot;[temporal]&quot; for finite closed boundary)
     */
    /*[deutsch]
     * <p>Liefert eine Beschreibung. </p>
     *
     * @return  String (either &quot;(-∞)&quot;, &quot;(+∞)&quot; or
     *          &quot;(temporal)&quot; for finite open boundary or
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
     */
    IntervalEdge getEdge() {

        return this.edge;

    }

    /**
     * @serialData  Uses
     *              <a href="../../../serialized-form.html#net.time4j.range.SPX">
     *              a dedicated serialization form</a> as proxy. The format
     *              is bit-compressed. The first byte contains in the six most
     *              significant bits the type-ID {@code 60}. The lowest bit is
     *              {@code 1} if this instance is infinite past. The bit (2)
     *              will be set if this instance is infinite future. After this
     *              header byte and in case of finite boundary, one byte
     *              follows describing the open/closed-state. Finally the
     *              bytes for the temporal follow.
     *
     * Schematic algorithm:
     *
     * <pre>
     *  int header = 60;
     *  header <<= 2;
     *
     *  if (this == Boundary.infinitePast()) {
     *      header |= 1;
     *      out.writeByte(header);
     *  } else if (this == Boundary.infiniteFuture()) {
     *      header |= 2;
     *      out.writeByte(header);
     *  } else {
     *      out.writeByte(header);
     *      out.writeByte(isOpen() ? 1 : 0);
     *      out.writeObject(getTemporal());
     *  }
     * </pre>
     */
    private Object writeReplace() throws ObjectStreamException {

        return new SPX(this, SPX.BOUNDARY_TYPE);

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
