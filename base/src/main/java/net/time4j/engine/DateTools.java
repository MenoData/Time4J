/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (DateTools.java) is part of project Time4J.
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

package net.time4j.engine;


/**
 * Small helper class due to lack of private interface methods in Java 8.
 *
 * @author  Meno Hochschild
 * @since   5.8
 */
final class DateTools {

    //~ Konstruktoren -----------------------------------------------------

    private DateTools() {
        // no instantiation
    }

    //~ Methoden ----------------------------------------------------------

    // conversion routine used by interface CalendarDate
    static <T> T convert(
        long utcDays,
        CalendarSystem<T> calsys,
        Chronology<T> target
    ) {

        if ((calsys.getMinimumSinceUTC() > utcDays) || (calsys.getMaximumSinceUTC() < utcDays)) {
            throw new ArithmeticException("Cannot transform <" + utcDays + "> to: " + target.getChronoType().getName());
        } else {
            return calsys.transform(utcDays);
        }

    }

}
