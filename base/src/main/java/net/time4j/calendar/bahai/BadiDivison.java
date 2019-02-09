/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2019 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (BadiDivison.java) is part of project Time4J.
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

package net.time4j.calendar.bahai;

import java.util.Comparator;


/**
 * <p>Generalizes the concept of year divisions for the Badi calendar. </p>
 *
 * @author  Meno Hochschild
 * @since   5.3
 */
/*[deutsch]
 * <p>Verallgemeinert das Konzept einer Jahresteilung f&uuml;r den Badi-Kalender. </p>
 *
 * @author  Meno Hochschild
 * @since   5.3
 */
public interface BadiDivison {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Obtains a comparator comparing either Badi months or the Ayyam-i-Ha-period. </p>
     *
     * @return  Comparator
     */
    /*[deutsch]
     * <p>Liefert einen {@code Comparator}, der entweder Badi-Monate oder die Ayyam-i-Ha-Periode miteinander
     * vergleicht. </p>
     *
     * @return  Comparator
     */
    static Comparator<BadiDivison> comparator() {
        return (o1, o2) -> {
            if (o1 instanceof BadiMonth) {
                if (o2 instanceof BadiMonth) {
                    return BadiMonth.class.cast(o1).compareTo(BadiMonth.class.cast(o2));
                } else {
                    return (o1 == BadiMonth.ALA) ? 1 : -1;
                }
            } else if (o2 instanceof BadiMonth) {
                return (o2 == BadiMonth.ALA) ? -1 : 1;
            } else {
                return 0;
            }
        };
    }

}
