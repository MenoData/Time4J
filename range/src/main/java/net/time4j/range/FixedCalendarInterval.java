/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FixedCalendarInterval.java) is part of project Time4J.
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

import net.time4j.PlainDate;
import net.time4j.base.MathUtils;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.Temporal;

import java.io.Serializable;


/**
 * <p>Represents a fixed calendar interval. </p>
 *
 * @author  Meno Hochschild
 * @since   3.21/4.17
 */
/*[deutsch]
 * <p>Repr&auml;sentiert ein festes Kalenderintervall. </p>
 *
 * @author  Meno Hochschild
 * @since   3.21/4.17
 */
public abstract class FixedCalendarInterval<T extends FixedCalendarInterval<T>>
    extends ChronoEntity<T>
    implements Temporal<T>, Comparable<T>, ChronoInterval<PlainDate>, Iterable<PlainDate>, Serializable {

    //~ Konstruktoren -----------------------------------------------------

    // only for Time4J-subclasses
    FixedCalendarInterval() {
        super();

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>A calendar interval is always finite. </p>
     *
     * @return {@code true}
     */
    /*[deutsch]
     * <p>Ein Kalenderintervall ist immer endlich. </p>
     *
     * @return  {@code true}
     */
    @Override
    public final boolean isFinite() {

        return true;

    }

    /**
     * <p>A calendar interval is never empty. </p>
     *
     * @return {@code false}
     */
    /*[deutsch]
     * <p>Ein Kalenderintervall ist niemals leer. </p>
     *
     * @return  {@code false}
     */
    @Override
    public final boolean isEmpty() {

        return false;

    }

    @Override
    public boolean isAfter(T other) {

        return (this.compareTo(other) > 0);

    }

    @Override
    public boolean isBefore(T other) {

        return (this.compareTo(other) < 0);

    }

    @Override
    public boolean isSimultaneous(T other) {

        return (this.compareTo(other) == 0);

    }

    // helper method for toString() in subclasses
    static void formatYear(
        StringBuilder sb,
        int year
    ) {

        int value = year;

        if (value < 0) {
            sb.append('-');
            value = MathUtils.safeNegate(year);
        }

        if (value >= 10000) {
            if (year > 0) {
                sb.append('+');
            }
        } else if (value < 1000) {
            sb.append('0');
            if (value < 100) {
                sb.append('0');
                if (value < 10) {
                    sb.append('0');
                }
            }
        }

        sb.append(value);

    }

}