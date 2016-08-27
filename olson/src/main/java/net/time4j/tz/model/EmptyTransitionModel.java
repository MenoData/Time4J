/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (EmptyTransitionModel.java) is part of project Time4J.
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
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static net.time4j.tz.model.TransitionModel.NEW_LINE;


/**
 * <p>Transition history without any defined transition. </p>
 *
 * @author      Meno Hochschild
 * @since       2.3
 * @serial      include
 */
final class EmptyTransitionModel
    implements TransitionHistory, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = 1374714021808040253L;

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  fixed offset in seconds
     */
    private final ZonalOffset offset;

    //~ Konstruktoren -----------------------------------------------------

    EmptyTransitionModel(ZonalOffset offset) {
        super();

        this.offset = offset;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public ZonalOffset getInitialOffset() {

        return this.offset;

    }

    @Override
    public ZonalTransition getStartTransition(UnixTime ut) {

        return null;

    }

    @Override
    public ZonalTransition getNextTransition(UnixTime ut) {

        return null;

    }

    @Override
    public ZonalTransition getConflictTransition(
        GregorianDate localDate,
        WallTime localTime
    ) {

        return null;

    }

    @Override
    public List<ZonalOffset> getValidOffsets(
        GregorianDate localDate,
        WallTime localTime
    ) {

        return Collections.singletonList(this.offset);

    }

    @Override
    public List<ZonalTransition> getStdTransitions() {

        return Collections.emptyList();

    }

    @Override
    public List<ZonalTransition> getTransitions(
        UnixTime startInclusive,
        UnixTime endExclusive
    ) {

        return Collections.emptyList();

    }

    @Override
    public boolean isEmpty() {

        return true;

    }

    @Override
    public void dump(Appendable buffer) throws IOException {

        buffer.append("*** Fixed offset:").append(NEW_LINE).append(">>> ");
        buffer.append(this.getInitialOffset().canonical()).append(NEW_LINE);

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof EmptyTransitionModel) {
            EmptyTransitionModel that = (EmptyTransitionModel) obj;
            return this.offset.equals(that.offset);
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return this.offset.hashCode();

    }

    @Override
    public String toString() {

        return "EmptyTransitionModel=" + this.offset.canonical();

    }

}
