/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
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

import java.util.Locale;
import java.util.concurrent.TimeUnit;


/**
 * <p>Das Element f&uuml;r die Genauigkeit einer Uhrzeitangabe. </p>
 *
 * @author  Meno Hochschild
 */
class PrecisionElement<U extends Comparable<U>>
    implements ChronoElement<U> {

    //~ Statische Felder/Initialisierungen --------------------------------

    static final ChronoElement<ClockUnit> CLOCK_PRECISION =
        new PrecisionElement<ClockUnit>(ClockUnit.class, ClockUnit.HOURS, ClockUnit.NANOS);
    static final ChronoElement<TimeUnit> TIME_PRECISION =
        new PrecisionElement<TimeUnit>(TimeUnit.class, TimeUnit.DAYS, TimeUnit.NANOSECONDS);

    //~ Instanzvariablen --------------------------------------------------

    private final Class<U> type;
    private transient final U min;
    private transient final U max;

    //~ Konstruktoren -----------------------------------------------------

    private PrecisionElement(
        Class<U> type,
        U min,
        U max
    ) {
        super();

        this.type = type;
        this.min = min;
        this.max = max;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public String name() {

        return "PRECISION";

    }

    @Override
    public Class<U> getType() {

        return this.type;

    }

    @Override
    public U getDefaultMinimum() {

        return this.min;

    }

    @Override
    public U getDefaultMaximum() {

        return this.max;

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

        U u1 = o1.get(this);
        U u2 = o2.get(this);

        if (this.type == ClockUnit.class) {
            return u1.compareTo(u2);
        } else {
            return u2.compareTo(u1);
        }

    }

    @Override
    public String getDisplayName(Locale language) {

        return this.name();

    }

    @Override
    public char getSymbol() {

        return '\u0000';

    }

    @Override
    public boolean isLenient() {

        return false;

    }

    /**
     * @serialData  serves for singleton semantic
     * @return      replacement object
     */
    private Object readResolve() {

        return ((this.type == ClockUnit.class) ? CLOCK_PRECISION : TIME_PRECISION);

    }

}
