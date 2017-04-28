/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (IsoDefaultPivotYear.java) is part of project Time4J.
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

import net.time4j.base.GregorianMath;
import net.time4j.base.MathUtils;


/**
 * <p>Holder for the default pivot year. </p>
 *
 * @author  Meno Hochschild
 * @since   3.32/4.27
 */
class IsoDefaultPivotYear {

    //~ Statische Felder/Initialisierungen --------------------------------

    static final int VALUE; // calculate only once during start up

    static {
        long mjd =
            EpochDays.MODIFIED_JULIAN_DATE.transform(
                MathUtils.floorDivide(System.currentTimeMillis(), 86400 * 1000),
                EpochDays.UNIX);
        VALUE = (GregorianMath.readYear(GregorianMath.toPackedDate(mjd)) + 20);
    }

    //~ Konstruktoren -----------------------------------------------------

    private IsoDefaultPivotYear() {
        // no instantiation
    }

}
