/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ArrayTransitionModel.java) is part of project Time4J.
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

package net.time4j.tz.model;

import net.time4j.base.GregorianDate;
import net.time4j.base.UnixTime;
import net.time4j.base.WallTime;
import net.time4j.tz.TransitionHistory;
import net.time4j.tz.ZonalOffset;
import net.time4j.tz.ZonalTransition;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * <p>Array-basiertes &Uuml;bergangsmodell. </p>
 *
 * @author      Meno Hochschild
 * @since       2.2
 * @serial      include
 * @concurrency <immutable>
 */
final class ArrayTransitionModel
    implements TransitionHistory, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = -1754640139112323489L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final ZonalTransition[] transitions;

    // Cache
    private transient int hash = 0;

    //~ Konstruktoren -----------------------------------------------------

    ArrayTransitionModel(List<ZonalTransition> transitions) {
        super();

        if (transitions.isEmpty()) {
            throw new IllegalArgumentException("Missing timezone transition.");
        }

        ZonalTransition[] tmp = new ZonalTransition[transitions.size()];
        tmp = transitions.toArray(tmp);
        Arrays.sort(tmp);
        this.transitions = tmp;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public ZonalOffset getInitialOffset() {

        return ZonalOffset.ofTotalSeconds(
            this.transitions[0].getPreviousOffset());

    }

    @Override
    public ZonalTransition getStartTransition(UnixTime ut) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ZonalTransition getNextTransition(UnixTime ut) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ZonalTransition getConflictTransition(
        GregorianDate localDate,
        WallTime localTime
    ) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ZonalOffset> getValidOffsets(
        GregorianDate localDate,
        WallTime localTime
    ) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ZonalTransition> getStdTransitions() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ZonalTransition> getTransitions(
        UnixTime startInclusive,
        UnixTime endExclusive
    ) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isEmpty() {

        return false;

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof ArrayTransitionModel) {
            ArrayTransitionModel that = (ArrayTransitionModel) obj;
            return Arrays.equals(this.transitions, that.transitions);
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        int h = this.hash;

        if (h == 0) {
            h = Arrays.deepHashCode(this.transitions);
            this.hash = h;
        }

        return h;

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(this.transitions.length * 32);
        sb.append(this.getClass().getName());
        sb.append("[transitions=");
        sb.append(this.transitions);
        sb.append(']');
        return sb.toString();

    }

    /**
     * <p>Benutzt in der Serialisierung. </p>
     *
     * @return  Array
     */
    ZonalTransition[] getTransitions() {

        return this.transitions;

    }

    /**
     * @serialData  Uses a specialized serialisation form as proxy. The format
     *              is bit-compressed. The first byte contains in the five
     *              most significant bits the type id {@code 26}. Then the
     *              data bytes for the internal transitions follow.
     *
     * Schematic algorithm:
     *
     * <pre>
     *  int header = (26 << 3);
     *  out.writeByte(header);
     *  out.writeInt(getTransitions().get(0).getPreviousOffset());
     *  out.writeInt(getTransitions().size());
     *
     *  for (ZonalTransition transition : getTransitions()) {
     *      out.writeLong(transition.getPosixTime());
     *      out.writeInt(transition.getTotalOffset());
     *      out.writeInt(transition.getDaylightSavingOffset());
     *  }
     * </pre>
     */
    private Object writeReplace() throws ObjectStreamException {

        return new SPX(this, SPX.ARRAY_TRANSITION_MODEL_TYPE);

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
