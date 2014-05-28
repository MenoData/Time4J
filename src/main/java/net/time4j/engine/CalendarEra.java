/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CalendarEra.java) is part of project Time4J.
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

import net.time4j.base.GregorianDate;


/**
 * <p>Repr&auml;sentiert eine &Auml;ra in einem Kalendersystem. </p>
 *
 * <p>Eine &Auml;ra ist grunds&auml;tzlich datumsbezogen und definiert ein
 * Bezugsdatum, das als Beginn oder Ende der &Auml;ra interpretiert werden
 * kann. Nicht alle Kalender kennen &uuml;berhaupt eine &Auml;ra, insbesondere
 * alle ISO-Kalendersysteme haben dieses Konzept nicht. Wenn eine &Auml;ra
 * existiert, ver&auml;ndert sich die Bedeutung einer Jahresangabe im
 * jeweiligen Kalendersystem dahingehend, da&szlig; die Jahresangabe dann
 * als Jahr innerhalb einer &Auml;ra zu interpretieren ist. Jahre innerhalb
 * einer &Auml;ra sind grunds&auml;tzlich positive Zahlen {@code >= 1}. </p>
 *
 * @author  Meno Hochschild
 */
public interface CalendarEra {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Liefert den kanonischen nicht lokalisierten Namen. </p>
     *
     * @return  String
     */
    String name();

    /**
     * <p>Liefert eine Ordnungsnummer, die so skaliert ist, da&szlig;
     * jede &Auml;ra, die das ISO-Datum der UTC-Epoche [1972-01-01]
     * enth&auml;lt, den Wert {@code 1} ergibt. </p>
     *
     * @return  int
     */
    int getValue();

    /**
     * <p>Liefert das Bezugsdatum (Start oder Ende einer &Auml;ra). </p>
     *
     * @return  gregorian date
     * @see     #isStarting()
     */
    GregorianDate getDate();

    /**
     * <p>Definiert das zugeh&ouml;rige Bezugsdatum den Start oder das Ende
     * dieser &Auml;ra? </p>
     *
     * <p>Ein System von &Auml;ren darf maximal eine &Auml;ra mit Endedatum
     * definieren, und dann nur als allererste &Auml;ra. In einer solchen
     * &Auml;ra ist das Jahr 1 das letzte und nicht das erste Jahr! </p>
     *
     * @return  boolean
     * @see     #getDate()
     */
    boolean isStarting();

}
