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
import net.time4j.base.GregorianDate;
import net.time4j.base.UnixTime;
import net.time4j.base.WallTime;
import net.time4j.scale.TimeScale;
import net.time4j.tz.ZonalOffset;
import net.time4j.tz.ZonalTransition;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * <p>Array-basiertes &Uuml;bergangsmodell. </p>
 *
 * @author      Meno Hochschild
 * @since       2.2
 * @serial      include
 */
final class ArrayTransitionModel
    extends TransitionModel {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = -5264909488983076587L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final ZonalTransition[] transitions;

    // Cache
    private transient final List<ZonalTransition> stdTransitions;
    private transient int hash = 0;

    //~ Konstruktoren -----------------------------------------------------

    ArrayTransitionModel(List<ZonalTransition> transitions) {
        this(transitions, true, true);

    }

    ArrayTransitionModel(
        List<ZonalTransition> transitions,
        boolean create,
        boolean sanityCheck
    ) {
        super();

        if (transitions.isEmpty()) {
            throw new IllegalArgumentException("Missing timezone transitions.");
        }

        // initialize state
        int n = transitions.size();
        ZonalTransition[] tmp = new ZonalTransition[n];
        tmp = transitions.toArray(tmp);

        if (create) {
            Arrays.sort(tmp);
        }

        if (sanityCheck) {
            checkSanity(tmp, transitions);
        }

        this.transitions = tmp;

        // fill standard transition cache
        long end = TransitionModel.getFutureMoment(1);
        this.stdTransitions = getTransitions(this.transitions, 0L, end);

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

        return this.getConflictTransition(localDate, localTime, null);

    }

    @Override
    public List<ZonalOffset> getValidOffsets(
        GregorianDate localDate,
        WallTime localTime
    ) {

        return this.getValidOffsets(localDate, localTime, null);

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

        return getTransitions(
            this.transitions,
            startInclusive.getPosixTime(),
            endExclusive.getPosixTime());

    }

    @Override
    public void dump(Appendable buffer) throws IOException {

        this.dump(this.transitions.length, buffer);

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
            h = Arrays.hashCode(this.transitions);
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
        sb.append(",hash=");
        sb.append(this.hashCode());
        sb.append(']');
        return sb.toString();

    }

    /**
     * <p>Wird von {@link #getConflictTransition(GregorianDate, WallTime)}
     * aufgerufen. </p>
     *
     * @param   localDate   local date in timezone
     * @param   localTime   local wall time in timezone
     * @param   ruleModel   optional last rules
     * @return  conflict transition on the local time axis for gaps or
     *          overlaps else {@code null}
     */
    ZonalTransition getConflictTransition(
        GregorianDate localDate,
        WallTime localTime,
        RuleBasedTransitionModel ruleModel // from CompositeTransitionModel
    ) {

        long localSecs = TransitionModel.toLocalSecs(localDate, localTime);
        int index = searchLocal(localSecs, this.transitions);

        if (index == this.transitions.length) {
            return (
                (ruleModel == null)
                ? null
                : ruleModel.getConflictTransition(localDate, localSecs));
        }

        ZonalTransition test = this.transitions[index];

        if (test.isGap()) {
            assert (test.getPosixTime() + test.getTotalOffset() > localSecs);
            if (test.getPosixTime() + test.getPreviousOffset() <= localSecs) {
                return test;
            }
        } else if (test.isOverlap()) {
            assert (test.getPosixTime() + test.getPreviousOffset() > localSecs);
            if (test.getPosixTime() + test.getTotalOffset() <= localSecs) {
                return test;
            }
        }

        return null;

    }

    /**
     * <p>Wird von {@link #getValidOffsets(GregorianDate, WallTime)}
     * aufgerufen. </p>
     *
     * @param   localDate   local date in timezone
     * @param   localTime   local wall time in timezone
     * @param   ruleModel   optional last rules
     * @return  unmodifiable list of shifts which fits the given local time
     */
    List<ZonalOffset> getValidOffsets(
        GregorianDate localDate,
        WallTime localTime,
        RuleBasedTransitionModel ruleModel // from CompositeTransitionModel
    ) {

        long localSecs = TransitionModel.toLocalSecs(localDate, localTime);
        int index = searchLocal(localSecs, this.transitions);

        if (index == this.transitions.length) {
            if (ruleModel == null) {
                ZonalTransition last =
                    this.transitions[this.transitions.length - 1];
                return TransitionModel.toList(last.getTotalOffset());
            } else {
                return ruleModel.getValidOffsets(localDate, localSecs);
            }
        }

        ZonalTransition test = this.transitions[index];

        if (test.isGap()) {
            assert (test.getPosixTime() + test.getTotalOffset() > localSecs);
            if (test.getPosixTime() + test.getPreviousOffset() <= localSecs) {
                return Collections.emptyList();
            }
        } else if (test.isOverlap()) {
            assert (test.getPosixTime() + test.getPreviousOffset() > localSecs);
            if (test.getPosixTime() + test.getTotalOffset() <= localSecs) {
                return TransitionModel.toList(
                    test.getTotalOffset(),
                    test.getPreviousOffset());
            }
        }

        return TransitionModel.toList(test.getPreviousOffset());

    }

    // Called by CompositeTransitionModel
    void dump(
        int size,
        Appendable buffer
    ) throws IOException {

        for (int i = 0; i < size; i++) {
            ZonalTransition transition = this.transitions[i];
            TransitionModel.dump(transition, buffer);
        }

    }

    // Called by CompositeTransitionModel
    ZonalTransition getLastTransition() {

        return this.transitions[this.transitions.length - 1];

    }

    // Called by CompositeTransitionModel
    boolean equals(
        ArrayTransitionModel other,
        int s1,
        int s2
    ) {

        int n1 = Math.min(s1, this.transitions.length);
        int n2 = Math.min(s2, other.transitions.length);

        if (n1 != n2) {
            return false;
        }

        for (int i = 0; i < n1; i++) {
            if (!this.transitions[i].equals(other.transitions[i])) {
                return false;
            }
        }

        return true;

    }

    // Called by CompositeTransitionModel
    int hashCode(int size) {

        int n = Math.min(size, this.transitions.length);
        ZonalTransition[] tmp = new ZonalTransition[n];
        System.arraycopy(this.transitions, 0, tmp, 0, n);
        return Arrays.hashCode(tmp);

    }

    // Called by CompositeTransitionModel
    static void checkSanity(
        ZonalTransition[] transitions,
        List<ZonalTransition> original
    ) {

        int previous = transitions[0].getTotalOffset();

        for (int i = 1; i < transitions.length; i++) {
            if (previous != transitions[i].getPreviousOffset()) {
                Moment m =
                    Moment.of(transitions[i].getPosixTime(), TimeScale.POSIX);
                throw new IllegalArgumentException(
                    "Model inconsistency detected at: " + m
                    + " (" + transitions[i].getPosixTime() + ") "
                    + " in transitions: " + original);
            } else {
                previous = transitions[i].getTotalOffset();
            }
        }

    }

    /**
     * <p>Benutzt in der Serialisierung. </p>
     *
     * @param   out     serialization stream
     */
    void writeTransitions(ObjectOutput out) throws IOException {

        this.writeTransitions(this.transitions.length, out);

    }

    /**
     * <p>Benutzt in der Serialisierung. </p>
     *
     * @param   size    maximum count of transitions to be serialized
     * @param   out     serialization stream
     */
    void writeTransitions(
        int size,
        ObjectOutput out
    ) throws IOException {

        SPX.writeTransitions(this.transitions, size, out);

    }

    private static List<ZonalTransition> getTransitions(
        ZonalTransition[] transitions,
        long startInclusive,
        long endExclusive
    ) {

        long start = startInclusive;
        long end = endExclusive;

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

    // returns index of first transition after posixTime
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

    // returns index of first transition after local date and time
    private static int searchLocal(
        long localSecs,
        ZonalTransition[] transitions
    ) {

        int low = 0;
        int high = transitions.length - 1;

        while (low <= high) {
            int middle = (low + high) / 2;
            ZonalTransition zt = transitions[middle];
            int offset = Math.max(zt.getTotalOffset(), zt.getPreviousOffset());

            if (zt.getPosixTime() + offset <= localSecs) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }

        return low;

    }

    /**
     * @serialData  Uses a specialized serialisation form as proxy. The format
     *              is bit-compressed. The first byte contains the type id
     *              {@code 126}. Then the data bytes for the internal
     *              transitions follow. The complex algorithm exploits the
     *              fact that allmost all transitions happen at full hours
     *              around midnight in local standard time. Insight in details
     *              see source code.
     *
     * @return  replacement object
     */
    private Object writeReplace() {

        return new SPX(this, SPX.ARRAY_TRANSITION_MODEL_TYPE);

    }

    /**
     * @param       in  serialization stream
     * @serialData  Blocks because a serialization proxy is required.
     * @throws      InvalidObjectException (always)
     */
    private void readObject(ObjectInputStream in)
        throws InvalidObjectException {

        throw new InvalidObjectException("Serialization proxy required.");

    }

}
