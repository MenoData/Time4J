/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (EquatorialCoordinates.java) is part of project Time4J.
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
 * Describes a celestial coordinate system which projects the earth equator and poles onto the celestial sphere
 * using right ascension and declination in the reference frame J2000 as epoch.
 *
 * <p>See also: <a href="https://en.wikipedia.org/wiki/Celestial_coordinate_system">Wikipedia</a>.
 * The effect of precession will have an impact on right ascension and declination if times far away
 * from year 2000 are considered. </p>
 *
 * @author  Meno Hochschild
 * @since   4.37
 */
/*[deutsch]
 * Beschreibt ein Himmelskoordinatensystem, das den Erd&auml;quator und die Erdpole auf die Himmelskugel
 * projiziert, indem die Rektaszension und die Deklination mit Hilfe der J2000-Epoche als Polarkoordinaten
 * benutzt werden.
 *
 * <p>Siehe auch: <a href="https://de.wikipedia.org/wiki/Astronomische_Koordinatensysteme">Wikipedia</a>.
 * Der Effekt der Pr&auml;zession wirkt sich auf Rektaszension und Deklination aus, wenn Zeiten weit weg
 * vom gregorianischen Jahr 2000 betrachtet werden. </p>
 *
 * @author  Meno Hochschild
 * @since   4.37
 */
public interface EquatorialCoordinates {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Obtains the right ascension in degrees. </p>
     *
     * @return  double
     */
    /*[deutsch]
     * <p>Liefert die Rektaszension in Grad. </p>
     *
     * @return  double
     */
    double getRightAscension();

    /**
     * <p>Obtains the declination in degrees. </p>
     *
     * @return  double
     */
    /*[deutsch]
     * <p>Liefert die Deklination in Grad. </p>
     *
     * @return  double
     */
    double getDeclination();

}
