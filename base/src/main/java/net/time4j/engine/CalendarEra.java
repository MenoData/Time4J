/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
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


/**
 * <p>Represents an era in a calendar system. </p>
 *
 * <p>An era is date-related and defines a reference date which can be
 * interpreted as start or end of an era. Not all calendar systems know
 * an era concept, however. Especially all ISO-calendar systems have no
 * era according to ISO-8601-standard. If an era exists in a given
 * calendar system then the meaning of a year changes such that it
 * means the year of an era counting as positive number from {@code >= 1}. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
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
     * <p>Yields the canonical non-localized name. </p>
     *
     * @return  String
     */
    /*[deutsch]
     * <p>Liefert den kanonischen nicht lokalisierten Namen. </p>
     *
     * @return  String
     */
    String name();

}
