/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HistoricEra.java) is part of project Time4J.
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

package net.time4j.history;

import net.time4j.engine.CalendarEra;


/**
 * <p>Represents a historic era dividing the local timeline at roughly the point
 * of Jesu birth in the context of the julian/gregorian calendar. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine historische &Auml;ra, die ungef&auml;hr den angenommenen
 * Zeitpunkt von Jesu Geburt im julianisch/gregorianischen Kalender als
 * Teilung der Zeitskala benutzt. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
public enum HistoricEra
    implements CalendarEra {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>BC = Before Christian</p>
     */
    /*[deutsch]
     * <p>&Auml;ra vor Christi Geburt. </p>
     */
    BC,

    /**
     * <p>AD = Anno Domini</p>
     */
    /*[deutsch]
     * <p>&Auml;ra nach Christi Geburt. </p>
     */
    AD;

    //~ Methoden ----------------------------------------------------------

    @Override
    public int getValue() {

        return this.ordinal();

    }

}
