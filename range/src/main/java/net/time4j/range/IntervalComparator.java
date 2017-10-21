/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (IntervalComparator.java) is part of project Time4J.
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

import net.time4j.engine.TimeLine;

import java.util.Comparator;


/**
 * <p>Standardimplementierung zum Sortieren von Intervallen zuerst nach
 * Start und dann nach L&auml;nge. </p>
 *
 * @author  Meno Hochschild
 */
final class IntervalComparator<T>
    implements Comparator<ChronoInterval<T>> {

    //~ Instanzvariablen --------------------------------------------------

    private final boolean calendrical;
    private final TimeLine<T> axis;

    //~ Konstruktoren -----------------------------------------------------

    IntervalComparator(TimeLine<T> axis) {
        super();

        this.calendrical = axis.isCalendrical();
        this.axis = axis;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public int compare(
        ChronoInterval<T> o1,
        ChronoInterval<T> o2
    ) {

        Boundary<T> bs1 = o1.getStart();
        Boundary<T> bs2 = o2.getStart();

        if (bs1.isInfinite()) {
            if (bs2.isInfinite()) {
                return this.compareEnd(o1, o2);
            } else {
                return -1;
            }
        } else if (bs2.isInfinite()) {
            return 1;
        }

        T start1 = bs1.getTemporal();
        T start2 = bs2.getTemporal();

        if (bs1.isOpen()) {
            start1 = this.axis.stepForward(start1);
        }
        if (bs2.isOpen()) {
            start2 = this.axis.stepForward(start2);
        }

        // open max condition (rare edge case)
        if (start1 == null) {
            if (start2 == null) {
                return 0;
            } else {
                return 1;
            }
        } else if (start2 == null) {
            return -1;
        }

        int delta = this.axis.compare(start1, start2);

        if (delta == 0) {
            delta = this.compareEnd(o1, o2);
        }

        return delta;

    }

    private int compareEnd(
        ChronoInterval<T> o1,
        ChronoInterval<T> o2
    ) {

        Boundary<T> be1 = o1.getEnd();
        Boundary<T> be2 = o2.getEnd();

        if (be1.isInfinite()) {
            if (be2.isInfinite()) {
                return 0;
            } else {
                return 1;
            }
        } else if (be2.isInfinite()) {
            return -1;
        }

        T end1 = be1.getTemporal();
        T end2 = be2.getTemporal();

        if (this.calendrical) {
            if (be1.isOpen()) {
                end1 = this.axis.stepBackwards(end1);
            }
            if (be2.isOpen()) {
                end2 = this.axis.stepBackwards(end2);
            }

            // closed min condition (rare edge case)
            if (end1 == null) {
                if (end2 == null) {
                    return 0;
                } else {
                    return -1;
                }
            } else if (end2 == null) {
                return 1;
            }
        } else {
            if (be1.isClosed()) {
                end1 = this.axis.stepForward(end1);
            }
            if (be2.isClosed()) {
                end2 = this.axis.stepForward(end2);
            }

            // closed max condition (rare edge case)
            if (end1 == null) {
                if (end2 == null) {
                    return 0;
                } else {
                    return 1;
                }
            } else if (end2 == null) {
                return -1;
            }
        }

        // default case
        return this.axis.compare(end1, end2);

    }

}
