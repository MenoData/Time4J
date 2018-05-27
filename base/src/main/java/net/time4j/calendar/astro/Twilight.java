/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Twilight.java) is part of project Time4J.
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

package net.time4j.calendar.astro;


/**
 * <p>Enumeration of various twilight definitions. </p>
 *
 * <p>See also <a href="https://en.wikipedia.org/wiki/Twilight">Wikipedia</a>. </p>
 *
 * @author 	Meno Hochschild
 * @since 	3.34/4.29
 */
/*[deutsch]
 * <p>Aufz&auml;hlung verschiedener D&auml;mmerungsdefinitionen. </p>
 *
 * <p>Siehe auch <a href="https://en.wikipedia.org/wiki/Twilight">Wikipedia</a>. </p>
 *
 * @author 	Meno Hochschild
 * @since 	3.34/4.29
 */
public enum Twilight {

	//~ Statische Felder/Initialisierungen --------------------------------

	/**
	 * <p>Marks the time when the sun is 4 degrees below the horizon. </p>
	 *
	 * <p>Mainly used by photographers. There is no official definition but usually photographers
	 * talk about the blue hour when the sun is between 4 and eight degrees below the horizon.
	 * See also <a href="https://en.wikipedia.org/wiki/Blue_hour">Wikipedia</a>. </p>
	 */
	/*[deutsch]
	 * <p>Markiert die Zeit, wenn die Sonne 4 Grad unter dem Horizont ist. </p>
	 *
	 * <p>Haupts&auml;chlich von Fotografen verwendet. Es gibt keine offizielle Definition, aber
	 * normalerweise sprechen Fotografen von der blauen Stunden, wenn die Sonne zwischen 4 und 8 Grad
	 * unter dem Horizont steht. Siehe auch <a href="https://en.wikipedia.org/wiki/Blue_hour">Wikipedia</a>. </p>
	 */
	BLUE_HOUR(4.0),

	/**
	 * <p>Marks the time when the sun is 6 degrees below the horizon. </p>
	 */
	/*[deutsch]
	 * <p>Markiert die Zeit, wenn die Sonne 6 Grad unter dem Horizont ist. </p>
	 */
	CIVIL(6.0),

	/**
	 * <p>Marks the time when the sun is 12 degrees below the horizon. </p>
	 */
	/*[deutsch]
	 * <p>Markiert die Zeit, wenn die Sonne 12 Grad unter dem Horizont ist. </p>
	 */
	NAUTICAL(12.0),

	/**
	 * <p>Marks the time when the sun is 18 degrees below the horizon. </p>
	 *
	 * <p>Is the sun even deeper below the horizon then people talk about night. </p>
	 */
	/*[deutsch]
	 * <p>Markiert die Zeit, wenn die Sonne 18 Grad unter dem Horizont ist. </p>
	 *
	 * <p>Steht die Sonne noch tiefer, spricht man von tiefer Nacht. </p>
	 */
	ASTRONOMICAL(18.0);

	//~ Instanzvariablen --------------------------------------------------

	private transient final double angle;

	//~ Konstruktoren -----------------------------------------------------

	private Twilight(double angle) {
		this.angle = angle;
	}

	//~ Methoden ----------------------------------------------------------

	/**
	 * <p>Obtains the associated angle of the sun relative to the horizon in degrees. </p>
	 *
	 * @return	angle in degrees
	 */
	/*[deutsch]
	 * <p>Liefert den assozierten Winkel in Grad, unter dem die Sonne relativ zum Horizont erscheint. </p>
	 *
	 * @return	angle in degrees
	 */
	double getAngle() {

		return this.angle;

	}

}
