/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (IsoTimeUnit.java) is part of project Time4J.
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


/**
 * <p>Repr&auml;sentiert eine ISO-konforme Uhrzeiteinheit. </p>
 *
 * <p>Die meisten Anwendungen werden einfach eine Standardeinheit vom
 * Typ {@code ClockUnit} benutzen. Andere Zeiteinheiten m&uuml;ssen
 * das Interface {@link net.time4j.engine.UnitRule.Source} geeignet
 * implementieren und beachten, da&szlig; die L&auml;nge der Zeiteinheit
 * stets unterhalb eines Tages bleiben mu&szlig;. </p>
 *
 * @author  Meno Hochschild
 * @see     ClockUnit
 */
public interface IsoTimeUnit
    extends IsoUnit {

}

