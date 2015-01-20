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

import net.time4j.Moment;
import net.time4j.SystemClock;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.time4j.CalendarUnit.YEARS;

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

//    private static final long serialVersionUID = -1754640139112323489L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final ZonalTransition[] transitions;

    // Cache
    private transient final List<ZonalTransition> stdTransitions;
    private transient int hash = 0;

    //~ Konstruktoren -----------------------------------------------------

    ArrayTransitionModel(List<ZonalTransition> transitions) {
        this(transitions, true);

    }

    ArrayTransitionModel(
        List<ZonalTransition> transitions,
        boolean checkSanity
    ) {
        super();

        if (transitions.isEmpty()) {
            throw new IllegalArgumentException("Missing timezone transitions.");
        }

        // initialize state
        int n = transitions.size();
        ZonalTransition[] tmp = new ZonalTransition[n];
        tmp = transitions.toArray(tmp);
        Arrays.sort(tmp);
        this.transitions = tmp;

        if (checkSanity) {
            int previous = tmp[0].getTotalOffset();

            for (int i = 1; i < n; i++) {
                if (previous != tmp[i].getPreviousOffset()) {
                    throw new IllegalArgumentException(
                        "Model inconsistency detected: " + transitions);
                } else {
                    previous = tmp[i].getTotalOffset();
                }
            }
        }

        // fill cache
        Moment end =
            SystemClock.inZonalView(ZonalOffset.UTC)
                .now()
                .plus(1, YEARS)
                .atUTC();
        this.stdTransitions =
            getTransitions(this.transitions, Moment.UNIX_EPOCH, end);

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public ZonalOffset getInitialOffset() {

        return ZonalOffset.ofTotalSeconds(
            this.transitions[0].getPreviousOffset());

    }

    @Override
    public ZonalTransition getStartTransition(UnixTime ut) {

        int index = search(ut.getPosixTime(), this.transitions);

        return (
            (index == 0)
            ? null
            : this.transitions[index - 1]);

    }

    @Override
    public ZonalTransition getNextTransition(UnixTime ut) {

        int index = search(ut.getPosixTime(), this.transitions);

        return (
            (index == this.transitions.length)
            ? null
            : this.transitions[index]);

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

        return this.stdTransitions;

    }

    @Override
    public List<ZonalTransition> getTransitions(
        UnixTime startInclusive,
        UnixTime endExclusive
    ) {

        return getTransitions(this.transitions, startInclusive, endExclusive);

    }

    private static List<ZonalTransition> getTransitions(
        ZonalTransition[] transitions,
        UnixTime startInclusive,
        UnixTime endExclusive
    ) {

        long start = startInclusive.getPosixTime();
        long end = endExclusive.getPosixTime();

        if (start > end) {
            throw new IllegalArgumentException("Start after end.");
        }

        int i1 = search(start, transitions);
        int i2 = search(end, transitions);

        if (i2 == 0) {
            return Collections.emptyList();
        } else if ((i1 > 0) && (transitions[i1 - 1].getPosixTime() == start)) {
            i1--;
        }

        i2--;

        if (transitions[i2].getPosixTime() == end) {
            i2--;
        }

        if (i1 > i2) {
            return Collections.emptyList();
        } else {
            List<ZonalTransition> result =
                new ArrayList<ZonalTransition>(i2 - i1 + 1);
            for (int i = i1; i <= i2; i++) {
                result.add(transitions[i]);
            }
            return Collections.unmodifiableList(result);
        }

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

        StringBuilder sb = new StringBuilder(32);
        sb.append(this.getClass().getName());
        sb.append("[transition-count=");
        sb.append(this.transitions.length);
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

    private static int search(
        long posixTime,
        ZonalTransition[] transitions
    ) {

        int low = 0;
        int high = transitions.length - 1;

        while (low <= high) {
            int middle = (low + high) / 2;

            if (transitions[middle].getPosixTime() <= posixTime) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }

        return low;

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
