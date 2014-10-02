/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (GenericIntervalFactory.java) is part of project Time4J.
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

import net.time4j.engine.Calendrical;
import net.time4j.engine.Temporal;
import net.time4j.engine.TimeLine;

import static net.time4j.range.IntervalEdge.CLOSED;
import static net.time4j.range.IntervalEdge.OPEN;


final class GenericIntervalFactory<T extends Temporal<? super T>>
    implements IntervalFactory<T> {

    //~ Instanzvariablen --------------------------------------------------

    private final TimeLine<T> timeline;

    //~ Konstruktoren -----------------------------------------------------

    GenericIntervalFactory(TimeLine<T> timeline) {
        super();

        if (timeline == null) {
            throw new NullPointerException("Missing timeline");
        }

        this.timeline = timeline;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public ChronoInterval<T> between(
        T start,
        T end
    ) {

        Boundary<T> t1 = Boundary.of(CLOSED, start);
        Boundary<T> t2;

        if (end instanceof Calendrical) {
            t2 = Boundary.of(CLOSED, end);
        } else {
            t2 = Boundary.of(OPEN, end);
        }

        return new GenericInterval<T>(this.timeline, t1, t2);

    }

}
