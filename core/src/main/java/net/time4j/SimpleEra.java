/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SimpleEra.java) is part of project Time4J.
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

import net.time4j.base.GregorianDate;
import net.time4j.engine.CalendarEra;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ElementRule;


/**
 * <p>Repr&auml;sentiert eine vereinfachte &Auml;ra, die den angenommenen
 * Zeitpunkt von Jesu Geburt im proleptischen gregorianischen Kalender als
 * Teilung der Zeitskala benutzt. </p>
 *
 * @author  Meno Hochschild
 */
enum SimpleEra
    implements CalendarEra {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>&Auml;ra vor Christi Geburt. </p>
     *
     * <p>BC = Before Christian</p>
     */
    BC {
        @Override
        public GregorianDate getDate() {
            return PlainDate.of(0, Month.DECEMBER, 31);
        }
        @Override
        public boolean isStarting() {
            return false;
        }
    },

    /**
     * <p>&Auml;ra nach Christi Geburt. </p>
     *
     * <p>AD = Anno Domini</p>
     */
    AD {
        @Override
        public GregorianDate getDate() {
            return PlainDate.of(1, Month.JANUARY, 1);
        }
        @Override
        public boolean isStarting() {
            return true;
        }
    };

    //~ Methoden ----------------------------------------------------------

    @Override
    public int getValue() {

        return this.ordinal();

    }

}
