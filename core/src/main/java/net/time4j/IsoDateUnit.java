/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (IsoDateUnit.java) is part of project Time4J.
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
 * <p>Represents a calendar unit conforming to ISO-8601. </p>
 *
 * <p>Most applications will simply use a standard unit of type
 * {@code CalendarUnit}. Other units must inherit from the class
 * {@link net.time4j.engine.BasicUnit} appropriately. </p>
 *
 * @author  Meno Hochschild
 * @see     CalendarUnit
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine ISO-konforme kalendarische Zeiteinheit. </p>
 *
 * <p>Die meisten Anwendungen werden einfach eine Standardeinheit vom
 * Typ {@code CalendarUnit} benutzen. Andere Zeiteinheiten m&uuml;ssen
 * von der Klasse {@link net.time4j.engine.BasicUnit} erben. </p>
 *
 * @author  Meno Hochschild
 * @see     CalendarUnit
 */
public interface IsoDateUnit
    extends IsoUnit {

}
