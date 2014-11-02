/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
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

import net.time4j.engine.Temporal;

import java.util.Comparator;


/**
 * <p>Standardimplementierung zum Sortieren von Intervallen zuerst nach
 * Start und dann nach L&auml;nge. </p>
 *
 * @author  Meno Hochschild
 */
final class IntervalComparator
    <T extends Temporal<? super T>, I extends IsoInterval<T, I>>
    implements Comparator<I> {

    //~ Statische Felder/Initialisierungen --------------------------------

    @SuppressWarnings("rawtypes")
    private static final Comparator<?> INSTANCE = new IntervalComparator();

    //~ Konstruktoren -----------------------------------------------------

    private IntervalComparator() {
        // singleton

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Liefert die Singleton-Instanz. </p>
     *
     * @param   <T> temporal type
     * @param   <I> interval type
     * @return  singleton instance
     */
    @SuppressWarnings("unchecked")
    static <T extends Temporal<? super T>, I extends IsoInterval<T, I>>
    Comparator<I> getInstance() {

        return (Comparator<I>) INSTANCE;

    }

    @Override
    public int compare(I o1, I o2) {

        if (o1.getStart().isInfinite()) {
            if (o2.getStart().isInfinite()) {
                return this.compareEnd(o1, o2);
            } else {
                return -1;
            }
        } else if (o2.getStart().isInfinite()) {
            return 1;
        }

        T start1 = o1.getTemporalOfClosedStart();
        T start2 = o2.getTemporalOfClosedStart();

        if (start1.isBefore(start2)) {
            return -1;
        } else if (start1.isAfter(start2)) {
            return 1;
        } else {
            return this.compareEnd(o1, o2);
        }

    }

    private int compareEnd(I o1, I o2) {

        if (o1.getEnd().isInfinite()) {
            if (o2.getEnd().isInfinite()) {
                return 0;
            } else {
                return 1;
            }
        } else if (o2.getEnd().isInfinite()) {
            return -1;
        }

        T end1 = o1.getTemporalOfOpenEnd();
        T end2 = o2.getTemporalOfOpenEnd();

        if (end1.isBefore(end2)) {
            return -1;
        } else if (end1.isAfter(end2)) {
            return 1;
        } else {
            return 0;
        }

    }

}
