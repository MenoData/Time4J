/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (PrecisionElement.java) is part of project Time4J.
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

package net.time4j;

import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;


/**
 * <p>Das Element f&uuml;r die Genauigkeit einer Uhrzeitangabe. </p>
 *
 * @author  Meno Hochschild
 */
enum PrecisionElement
    implements ChronoElement<ClockUnit> {

    //~ Statische Felder/Initialisierungen --------------------------------

    PRECISION;

    //~ Methoden ----------------------------------------------------------

    @Override
    public Class<ClockUnit> getType() {

        return ClockUnit.class;

    }

    @Override
    public ClockUnit getDefaultMinimum() {

        return ClockUnit.HOURS;

    }

    @Override
    public ClockUnit getDefaultMaximum() {

        return ClockUnit.NANOS;

    }

    @Override
    public boolean isDateElement() {

        return false;

    }

    @Override
    public boolean isTimeElement() {

        return true;

    }

    @Override
    public int compare(
        ChronoDisplay o1,
        ChronoDisplay o2
    ) {

        return o1.get(this).compareTo(o2.get(this));

    }

    @Override
    public char getSymbol() {

        return '\u0000';

    }

    @Override
    public boolean isLenient() {

        return false;

    }

}
