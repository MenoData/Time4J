/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (UtcComparator.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Time4J. If not, see <http://www.gnu.org/licenses/>.
 * -----------------------------------------------------------------------
 */

package net.time4j.scale;

import de.menodata.annotations4j.Stateless;
import java.io.Serializable;
import java.util.Comparator;


/**
 * <p>Wird im {@code UniversalTime}-Interface als
 * {@link UniversalTime#COMPARATOR Konstante} definiert. </p>
 *
 * @author  Meno Hochschild
 */
@Stateless
class UtcComparator
    implements Comparator<UniversalTime>, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = -2757783518742396595L;

    //~ Methoden ----------------------------------------------------------

    @Override
    public int compare(
        UniversalTime t1,
        UniversalTime t2
    ) {

        long u1 = t1.getEpochTime();
        long u2 = t2.getEpochTime();

        if (u1 < u2) {
            return -1;
        } else if (u1 > u2) {
            return 1;
        } else {
            int result = t1.getNanosecond() - t2.getNanosecond();
            return ((result > 0) ? 1 : ((result < 0) ? -1 : 0));
        }

    }

}
