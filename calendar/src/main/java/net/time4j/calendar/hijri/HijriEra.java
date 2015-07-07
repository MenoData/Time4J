/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HijriEra.java) is part of project Time4J.
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

package net.time4j.calendar.hijri;

import net.time4j.engine.CalendarEra;


/**
 * <p>The Hijri calendar only supports one single era called &quot;Anno Hegirae&quot; with the
 * numerical value {@code 1}. </p>
 *
 * @author  Meno Hochschild
 * @since   3.5/4.3
 */
/*[deutsch]
 * <p>Der islamische Kalender unterst&uuml;tzt nur eine einzige &Auml;ra, die &quot;Anno Hegirae&quot;
 * genannt wird und den numerischen Wert {@code 1} hat. </p>
 *
 * @author  Meno Hochschild
 * @since   3.5/4.3
 */
public enum HijriEra
    implements CalendarEra {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * Singleton instance (often abbreviated as &quot;AH&quot;).
     */
    /*[deutsch]
     * Singleton-Instanz (oft als &quot;AH&quot; abgek&uuml;rzt).
     */
    ANNO_HEGIRAE;

    //~ Methoden ----------------------------------------------------------

    @Override
    public int getValue() {

        return 1;

    }

}
