/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
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
 * <p>Represents the rolling result of a plain time if a possible day overflow
 * is to be taken into account. </p>
 *
 * @author              Meno Hochschild
 * @see                 PlainTime#roll(long,ClockUnit)
 */
/*[deutsch]
 * <p>Verk&ouml;rpert das Rollergebnis auf einer Uhrzeit, wenn auch ein
 * tageweiser &Uuml;berlauf gez&auml;hlt werden soll. </p>
 *
 * @author              Meno Hochschild
 * @see                 PlainTime#roll(long,ClockUnit)
 */
public final class DayCycles implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = -4124961309622141228L;

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  day overflow
     */
    /*[deutsch]
     * @serial  &Uuml;berlauf in Tagen
     */
    private final long days;

    /**
     * @serial  wall time
     */
    /*[deutsch]
     * @serial  Uhrzeit
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
     * <p>Gets the day overflow after rolling a plain time. </p>
     *
     * @return  count of day cycles ({@code 0} if without overflow)
     */
    /*[deutsch]
     * <p>Ermittelt den tageweise &Uuml;berlauf im Additionsergebnis. </p>
     *
     * @return  count of day cycles ({@code 0} if without overflow)
     */
    public long getDayOverflow() {
        return this.days;
    }

    /**
     * <p>Gets the rolled wall time. </p>
     *
     * @return  wall time (never 24:00)
     */
    /*[deutsch]
     * <p>Ermittelt die Uhrzeit. </p>
     *
     * @return  wall time (never 24:00)
     */
    public PlainTime getWallTime() {
        return this.time;
    }

}
