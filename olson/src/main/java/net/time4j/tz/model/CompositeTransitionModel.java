/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CompositeTransitionModel.java) is part of project Time4J.
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * <p>&Uuml;bergangsmodell als Komposition eines {@code ArrayTransitionModel}
 * mit einem {@code RuleBasedTransitionModel}. </p>
 *
 * @author      Meno Hochschild
 * @since       2.2
 * @serial      include
 * @concurrency <immutable>
 */
final class CompositeTransitionModel
    implements TransitionHistory, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

//    private static final long serialVersionUID = -9104454852317745314L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final ArrayTransitionModel arrayModel;
    private transient final RuleBasedTransitionModel ruleModel;

    // Cache
    private transient final ZonalTransition last;

    //~ Konstruktoren -----------------------------------------------------

    CompositeTransitionModel(
        List<ZonalTransition> transitions,
        List<DaylightSavingRule> rules
    ) {
        super();

        this.arrayModel = new ArrayTransitionModel(transitions);
        ZonalTransition[] array = this.arrayModel.getTransitions();
        this.last = array[array.length - 1];
        this.ruleModel = new RuleBasedTransitionModel(this.last, rules, true);

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public ZonalOffset getInitialOffset() {

        return this.arrayModel.getInitialOffset();

    }

    @Override
    public ZonalTransition getStartTransition(UnixTime ut) {

        if (ut.getPosixTime() < this.last.getPosixTime()) {
            return this.arrayModel.getStartTransition(ut);
        } else {
            ZonalTransition result = this.ruleModel.getStartTransition(ut);
            return ((result == null) ? this.last : result);
        }

    }

    @Override
    public ZonalTransition getNextTransition(UnixTime ut) {

        ZonalTransition result = this.arrayModel.getNextTransition(ut);

        if (result == null) {
            result = this.ruleModel.getNextTransition(ut);
        }

        return result;

    }

    @Override
    public ZonalTransition getConflictTransition(
        GregorianDate localDate,
        WallTime localTime
    ) {

        return this.arrayModel.getConflictTransition(
            localDate,
            localTime,
            this.ruleModel);

    }

    @Override
    public List<ZonalOffset> getValidOffsets(
        GregorianDate localDate,
        WallTime localTime
    ) {

        return this.arrayModel.getValidOffsets(
            localDate,
            localTime,
            this.ruleModel);

    }

    @Override
    public List<ZonalTransition> getStdTransitions() {

        // condition: std-transitions built by enhanced array cache
        return this.arrayModel.getStdTransitions();

    }

    @Override
    public List<ZonalTransition> getTransitions(
        UnixTime start,
        UnixTime end
    ) {

        List<ZonalTransition> result = new ArrayList<ZonalTransition>();
        result.addAll(this.arrayModel.getTransitions(start, end));
        result.addAll(this.ruleModel.getTransitions(start, end));
        return Collections.unmodifiableList(result);

    }

    @Override
    public boolean isEmpty() {

        return false;

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof CompositeTransitionModel) {
            CompositeTransitionModel that = (CompositeTransitionModel) obj;
            return (
                this.arrayModel.equals(that.arrayModel)
                && this.ruleModel.equals(that.ruleModel));
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return 17 * this.arrayModel.hashCode() + 37 * this.ruleModel.hashCode();

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(32);
        sb.append(this.getClass().getName());
        sb.append("history=");
        sb.append(this.arrayModel);
        sb.append(",last-rules=");
        sb.append(this.ruleModel);
        sb.append(']');
        return sb.toString();

    }

    /**
     * <p>Benutzt in der Serialisierung. </p>
     *
     * @return  TransitionHistory
     */
    TransitionHistory getArrayModel() {

        return this.arrayModel;

    }

    /**
     * <p>Benutzt in der Serialisierung. </p>
     *
     * @return  TransitionHistory
     */
    TransitionHistory getRuleModel() {

        return this.ruleModel;

    }

    /**
     * @serialData  Uses a specialized serialisation form as proxy. The format
     *              is bit-compressed. The first byte contains in the five
     *              most significant bits the type id {@code 27}. Then the
     *              data bytes for the internal transitions and rules follow.
     *
     * Schematic algorithm:
     *
     * <pre>
     *  int header = (27 << 3);
     *  out.writeByte(header);
     *  out.writeObject(getArrayModel());
     *  out.writeObject(getRuleModel());
     * </pre>
     */
    private Object writeReplace() throws ObjectStreamException {

        return new SPX(this, SPX.COMPOSITE_TRANSITION_MODEL_TYPE);

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
