/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (DayCycles.java) is part of project Time4J.
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

import java.io.Serializable;


/**
 * <p>Verk&ouml;rpert das Additionsergebnis auf einer Uhrzeit, wenn auch ein
 * tageweiser &Uuml;berlauf gez&auml;hlt werden soll. </p>
 *
 * @author      Meno Hochschild
 * @concurrency <immutable>
 * @see         PlainTime#roll(long,ClockUnit)
 */
public final class DayCycles implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = -4124961309622141228L;

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  day overflow
     */
    private final long days;

    /**
     * @serial  wall time
     */
    private final PlainTime time;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruiert eine neue Instanz mit &Uuml;berlauf und Uhrzeit. </p>
     *
     * @param   days    day overflow
     * @param   time    wall time result of last calculation
     */
    DayCycles(long days, PlainTime time) {
        super();
        this.days = days;
        this.time = time;
    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Ermittelt den tageweise &Uuml;berlauf im Additionsergebnis. </p>
     *
     * @return  count of day cycles ({@code 0} if without overflow)
     */
    public long getDayOverflow() {
        return this.days;
    }

    /**
     * <p>Ermittelt die Uhrzeit. </p>
     *
     * @return  wall time
     */
    public PlainTime getWallTime() {
        return this.time;
    }

}
