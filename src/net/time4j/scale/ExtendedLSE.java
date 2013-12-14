/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ExtendedLSE.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Time4J. If not, see <http://www.gnu.org/licenses/>.
 * -----------------------------------------------------------------------
 */

package net.time4j.scale;


/**
 * <p>Erweiterung des Ereignis-Interface um UTC-Methoden. </p>
 *
 * @author  Meno Hochschild
 */
interface ExtendedLSE
    extends LeapSecondEvent {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Definiert die Sekunde des Schaltsekundenereignisses
     * als UTC-Zeit mit Schaltsekunden. </p>
     *
     * @return  UTC-Zeit in SI-Sekunden relativ zur UTC-Epoche 1972-01-01 mit
     *          Schaltsekunden
     */
    long utc();

    /**
     * <p>Definiert die Sekunde des Schaltsekundenereignisses
     * als UTC-Zeit ohne Schaltsekunden. </p>
     *
     * @return  UTC-Zeit in SI-Sekunden relativ zur UTC-Epoche 1972-01-01 ohne
     *          Schaltsekunden
     */
    long raw();

}
