/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (GenericInterval.java) is part of project Time4J.
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


final class GenericInterval<T extends ChronoEntity<T> & Temporal<? super T>>
    extends ChronoInterval<T> {

    //~ Instanzvariablen --------------------------------------------------

    private final TimeLine<T> timeline;

    //~ Konstruktoren -----------------------------------------------------

    GenericInterval(
        TimeLine<T> timeline,
        Boundary<T> start,
        Boundary<T> end
    ) {
        super(start, end);

        // null-check implicitly done in GenericIntervalFactory
        this.timeline = timeline;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public ChronoInterval<T> withStart(Boundary<T> boundary) {

        return new GenericInterval<T>(this.timeline, boundary, this.getEnd());

    }

    @Override
    public ChronoInterval<T> withEnd(Boundary<T> boundary) {

        return new GenericInterval<T>(this.timeline, this.getStart(), boundary);

    }

    @Override
    public ChronoInterval<T> withStart(T temporal) {

        Boundary<T> boundary =
            Boundary.of(this.getStart().getEdge(), temporal);
        return new GenericInterval<T>(this.timeline, boundary, this.getEnd());

    }

    @Override
    public ChronoInterval<T> withEnd(T temporal) {

        Boundary<T> boundary =
            Boundary.of(this.getEnd().getEdge(), temporal);
        return new GenericInterval<T>(this.timeline, this.getStart(), boundary);

    }

    @Override
    protected TimeLine<T> getTimeLine() {

        return this.timeline;

    }

}
