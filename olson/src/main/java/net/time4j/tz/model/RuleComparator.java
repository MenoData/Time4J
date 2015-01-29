/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (RuleComparator.java) is part of project Time4J.
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

package net.time4j.tz.model;

import java.util.Comparator;

/**
 * <p>Compares daylight saving rules in ascending order. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 */
enum RuleComparator
    implements Comparator<DaylightSavingRule> {

    //~ Statische Felder/Initialisierungen --------------------------------

    INSTANCE;

    //~ Methoden ----------------------------------------------------------

    @Override
    public int compare(DaylightSavingRule o1, DaylightSavingRule o2) {

        int result = o1.getDate(2000).compareTo(o2.getDate(2000)); // leap year

        if (result == 0) {
            result = o1.getTimeOfDay().compareTo(o2.getTimeOfDay());
        }

        return result;

    }

}
