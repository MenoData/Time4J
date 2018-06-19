/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SimpleDate.java) is part of project Time4J.
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

package net.time4j.scale;

import net.time4j.base.GregorianDate;
import net.time4j.base.GregorianMath;


/**
 * A simplified gregorian date which serves for isolation in class loading (avoiding {@code PlainDate}).
 *
 * @since   5.0
 * @author  Meno Hochschild
 */
final class SimpleDate
    implements GregorianDate {

    //~ Instanzvariablen --------------------------------------------------

    private final int year;
    private final int month;
    private final int dayOfMonth;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * Standard constructor.
     *
     * @param   year        proleptic iso year [(-999999999) - 999999999]
     * @param   month       gregorian month (1-12)
     * @param   dayOfMonth  day of month (1-31)
     * @throws  IllegalArgumentException if any argument is out of range
     */
    SimpleDate(
        int year,
        int month,
        int dayOfMonth
    ) {
        super();

        GregorianMath.checkDate(year, month, dayOfMonth);
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public int getYear() {
        return this.year;
    }

    @Override
    public int getMonth() {
        return this.month;
    }

    @Override
    public int getDayOfMonth() {
        return this.dayOfMonth;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof SimpleDate) {
            SimpleDate that = (SimpleDate) obj;
            return (this.year == that.year) && (this.month == that.month) && (this.dayOfMonth == that.dayOfMonth);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return 7 * this.year + 23 * this.month + 31 * this.dayOfMonth;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(10);
        if (this.year >= 10_000) {
            sb.append('+');
        } else if (this.year < 0) {
            sb.append('-');
        }
        int y = Math.abs(this.year);
        if (y < 1_000) {
            sb.append('0');
            if (y < 100) {
                sb.append('0');
                if (y < 10) {
                    sb.append('0');
                }
            }
        }
        sb.append(y);
        sb.append('-');
        if (this.month < 10) {
            sb.append('0');
        }
        sb.append(this.month);
        sb.append('-');
        if (this.dayOfMonth < 10) {
            sb.append('0');
        }
        sb.append(this.dayOfMonth);
        return sb.toString();
    }

}
