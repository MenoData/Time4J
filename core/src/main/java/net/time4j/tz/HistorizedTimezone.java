/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HistorizedTimezone.java) is part of project Time4J.
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

package net.time4j.tz;

import net.time4j.base.GregorianDate;
import net.time4j.base.UnixTime;
import net.time4j.base.WallTime;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.List;


/**
 * <p>Provider-abh&auml;ngige Implementierung einer Zeitzone. </p>
 *
 * @author      Meno Hochschild
 * @serial      include
 */
final class HistorizedTimezone
    extends Timezone {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = 1738909257417361021L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final TZID id;
    private transient final TransitionHistory history;
    private transient final TransitionStrategy strategy;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Standard-Konstruktor. </p>
     *
     * @param   id          timezone id
     * @param   history     offset transition model
     * @throws  IllegalArgumentException if a fixed zonal offset is combined
     *          with a non-empty history
     */
    HistorizedTimezone(
        TZID id,
        TransitionHistory history
    ) {
        this(id, history, Timezone.DEFAULT_CONFLICT_STRATEGY);

    }

    /**
     * <p>Erweiterter Konstruktor. </p>
     *
     * @param   id          timezone id
     * @param   history     offset transition model
     * @param   strategy    transition strategy
     * @throws  IllegalArgumentException if a fixed zonal offset is combined
     *          with a non-empty history
     */
    HistorizedTimezone(
        TZID id,
        TransitionHistory history,
        TransitionStrategy strategy
    ) {
        super();

        if (id == null) {
            throw new NullPointerException("Missing timezone id.");
        } else if (
            (id instanceof ZonalOffset)
            && !history.isEmpty()
        ) {
            throw new IllegalArgumentException(
                "Fixed zonal offset can't be combined with offset transitions: "
                + id.canonical());
        } else if (history == null) {
            throw new NullPointerException("Missing timezone history.");
        } else if (strategy == null) {
            throw new NullPointerException("Missing transition strategy.");
        }

        this.id = id;
        this.history = history;
        this.strategy = strategy;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public TZID getID() {

        return this.id;

    }

    @Override
    public ZonalOffset getOffset(UnixTime ut) {

        ZonalTransition t = this.history.getStartTransition(ut);

        return (
            (t == null)
            ? this.history.getInitialOffset()
            : ZonalOffset.ofTotalSeconds(t.getTotalOffset())
        );

    }

    @Override
    public ZonalOffset getStandardOffset(UnixTime ut) {

        ZonalTransition t = this.history.getStartTransition(ut);

        return (
            (t == null)
            ? this.history.getInitialOffset()
            : ZonalOffset.ofTotalSeconds(t.getStandardOffset())
        );

    }

    @Override
    public ZonalOffset getDaylightSavingOffset(UnixTime ut){

        ZonalTransition t = this.history.getStartTransition(ut);

        return (
            (t == null)
            ? ZonalOffset.UTC
            : ZonalOffset.ofTotalSeconds(t.getDaylightSavingOffset())
        );

    }

    @Override
    public ZonalOffset getOffset(
        GregorianDate localDate,
        WallTime localTime
    ) {

        List<ZonalOffset> offsets =
            this.history.getValidOffsets(localDate, localTime);

        if (offsets.size() == 1) {
            return offsets.get(0);
        } else {
            ZonalTransition conflict = this.history.getConflictTransition(localDate, localTime);
            return ZonalOffset.ofTotalSeconds(conflict.getTotalOffset());
        }

    }

    @Override
    public boolean isInvalid(
        GregorianDate localDate,
        WallTime localTime
    ) {

        ZonalTransition t =
            this.history.getConflictTransition(localDate, localTime);
        return ((t != null) && t.isGap());

    }

    @Override
    public boolean isDaylightSaving(UnixTime ut) {

        ZonalTransition t = this.history.getStartTransition(ut);
        return ((t != null) && t.isDaylightSaving());

    }

    @Override
    public boolean isFixed() {

        return this.history.isEmpty();

    }

    @Override
    public TransitionHistory getHistory() {

        return this.history;

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof HistorizedTimezone) {
            HistorizedTimezone that = (HistorizedTimezone) obj;
            return (
                this.id.canonical().equals(that.id.canonical())
                && this.history.equals(that.history)
                && this.strategy.equals(that.strategy)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return this.id.canonical().hashCode();

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(32);
        sb.append('[');
        sb.append(this.getClass().getName());
        sb.append(':');
        sb.append(this.id.canonical());
        sb.append(",history={");
        sb.append(this.history);
        sb.append("},strategy=");
        sb.append(this.strategy);
        sb.append(']');
        return sb.toString();

    }

    @Override
    public TransitionStrategy getStrategy() {

        return this.strategy;

    }

    @Override
    public Timezone with(TransitionStrategy strategy) {

        if (this.strategy == strategy) {
            return this;
        }

        return new HistorizedTimezone(this.id, this.history, strategy);

    }

    /**
     * @serialData  Uses a specialized serialisation form as proxy. The format
     *              is bit-compressed. The first byte contains in the four
     *              most significant bits the type id {@code 14}. If there is
     *              a non-default transition strategy then the lowest bit is
     *              set to {@code 1} else to {@code 0}. After that the data bits
     *              for the id, history and optionally the special strategy
     *              follow.
     *
     * Schematic algorithm:
     *
     * <pre>
     *  boolean specialStrategy =
            (getStrategy() != Timezone.DEFAULT_CONFLICT_STRATEGY);
     *  int header = (14 &lt;&lt; 4);
     *
     *  if (specialStrategy) {
     *      header |= 1;
     *  }
     *
     *  out.writeByte(header);
     *  out.writeObject(tz.getID());
     *  out.writeObject(tz.getHistory());
     *
     *  if (specialStrategy) {
     *      out.writeObject(tz.getStrategy());
     *  }
     * </pre>
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.HISTORIZED_TIMEZONE_TYPE);

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
