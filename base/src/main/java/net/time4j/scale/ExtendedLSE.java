/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ExtendedLSE.java) is part of project Time4J.
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
     * @return  elapsed UTC time in SI-seconds relative to UTC epoch 1972-01-01
     *          including leap seconds
     */
    long utc();

    /**
     * <p>Definiert die Sekunde des Schaltsekundenereignisses
     * als UTC-Zeit ohne Schaltsekunden. </p>
     *
     * @return  elapsed UTC time in SI-seconds relative to UTC epoch 1972-01-01
     *          without leap seconds
     */
    long raw();

}
