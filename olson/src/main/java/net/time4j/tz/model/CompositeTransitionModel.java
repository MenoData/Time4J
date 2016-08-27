/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
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
import net.time4j.tz.ZonalOffset;
import net.time4j.tz.ZonalTransition;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
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
 */
final class CompositeTransitionModel
    extends TransitionModel {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = 1749643877954103721L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final int size;
    private transient final ArrayTransitionModel arrayModel;
    private transient final RuleBasedTransitionModel ruleModel;
    private transient final ZonalTransition last;

    // Cache
    private transient int hash = 0;

    //~ Konstruktoren -----------------------------------------------------

    CompositeTransitionModel(
        int size,
        List<ZonalTransition> transitions,
        List<DaylightSavingRule> rules,
        boolean create,
        boolean sanityCheck
    ) {
        super();

        this.size = size;
        this.arrayModel =
            new ArrayTransitionModel(transitions, create, sanityCheck);
        this.last = this.arrayModel.getLastTransition();
        this.ruleModel =
            new RuleBasedTransitionModel(this.last, rules, create);

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
    public void dump(Appendable buffer) throws IOException {

        this.arrayModel.dump(this.size, buffer);
        this.ruleModel.dump(buffer);

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof CompositeTransitionModel) {
            CompositeTransitionModel that = (CompositeTransitionModel) obj;
            return (
                this.arrayModel.equals(that.arrayModel, this.size, that.size)
                && this.ruleModel.getRules().equals(that.ruleModel.getRules()));
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        int h = this.hash;

        if (h == 0) {
            h = this.arrayModel.hashCode(this.size);
            h += (37 * this.ruleModel.getRules().hashCode());
            this.hash = h;
        }

        return h;

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(32);
        sb.append(this.getClass().getName());
        sb.append("[transition-count=");
        sb.append(this.size);
        sb.append(",hash=");
        sb.append(this.hashCode());
        sb.append(",last-rules=");
        sb.append(this.ruleModel.getRules());
        sb.append(']');
        return sb.toString();

    }

    /**
     * <p>Benutzt in der Serialisierung. </p>
     *
     * @param   out     serialization stream
     */
    void writeTransitions(ObjectOutput out) throws IOException {

        this.arrayModel.writeTransitions(this.size, out);

    }

    /**
     * <p>Benutzt in der Serialisierung. </p>
     *
     * @return  list of daylight saving rules
     */
    List<DaylightSavingRule> getRules() {

        return this.ruleModel.getRules();

    }

    /**
     * @serialData  Uses a specialized serialisation form as proxy. The format
     *              is bit-compressed. The first byte contains the type id
     *              {@code 127}. Then the data bytes for the internal
     *              transitions and rules follow. The complex algorithm
     *              exploits the fact that allmost all transitions happen
     *              at full hours around midnight in local standard time.
     *              Insight in details see source code.
     *
     * @return  replacement object
     */
    private Object writeReplace() {

        return new SPX(this, SPX.COMPOSITE_TRANSITION_MODEL_TYPE);

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
