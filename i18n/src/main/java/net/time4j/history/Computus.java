/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Computus.java) is part of project Time4J.
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

import net.time4j.PlainDate;
import net.time4j.engine.EpochDays;


/**
 * <p>Provides procedures how to determine Easter Sunday. </p>
 *
 * <p>The algorithms are based on the work of Heiner Lichtenberg in 1997. </p>
 *
 * @author  Meno Hochschild
 * @since   3.16/4.13
 */
/*[deutsch]
 * <p>Stellt Verfahren zur Berechnung des Osterfests bereit. </p>
 *
 * <p>Die hier verwendeten Algorithmen fu&szlig;en auf der Arbeit von Heiner Lichtenberg 1997. </p>
 *
 * @author  Meno Hochschild
 * @since   3.16/4.13
 */
public enum Computus {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Applies the Julian/Gregorian calendar and is used by catholic and protestantic churches. </p>
     */
    /*[deutsch]
     * <p>Wendet den julianisch/gregorianischen Kalender an und wird von der katholischen Kirche
     * und den protestantischen Kirchen verwendet. </p>
     */
    WESTERN,

    /**
     * <p>Applies the Julian calendar and is used by Orthodox church in East Europe. </p>
     */
    /*[deutsch]
     * <p>Wendet den julianischen Kalender an und wird von der orthodoxen Kirche in Osteuropa verwendet. </p>
     */
    EASTERN;

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Calculates the date of Easter Sunday. </p>
     *
     * @param   yearOfEra       calendar year
     * @return  calendar date of Easter Sunday (ISO-8601)
     * @throws  IllegalArgumentException if {@code yearOfEra < 532} (the first table from Beda Venerabilis)
     * @since   3.16/4.13
     */
    /*[deutsch]
     * <p>Berechnet das Datum des Ostersonntags. </p>
     *
     * @param   yearOfEra       calendar year
     * @return  calendar date of Easter Sunday (ISO-8601)
     * @throws  IllegalArgumentException if {@code yearOfEra < 532} (the first table from Beda Venerabilis)
     * @since   3.16/4.13
     */
    public PlainDate easterSunday(int yearOfEra) {

        if (yearOfEra < 532) {
            throw new IllegalArgumentException("Out of range: " + yearOfEra);
        }

        boolean gregorian = ((this == WESTERN) && (yearOfEra > 1582));

        int k = yearOfEra / 100;
        int m = 15;
        int s = 0;

        if (gregorian) {
            m += ((3 * k + 3) / 4);
            m -= ((8 * k + 13) / 25);
            s = 2 - ((3 * k + 3) / 4);
        }

        int a = yearOfEra % 19;
        int d = (19 * a + m) % 30;
        int r = (d / 29) + ((d / 28) - (d / 29)) * (a / 11);
        int og = 21 + d - r;
        int sz = 7 - ((yearOfEra + (yearOfEra / 4) + s) % 7);
        int oe = 7 - ((og - sz) % 7);

        int dom = og + oe;
        int month = 3;

        if (dom > 31) {
            month++;
            dom -= 31;
        }

        return (
            gregorian
                ? PlainDate.of(yearOfEra, month, dom)
                : PlainDate.of(JulianMath.toMJD(yearOfEra, month, dom), EpochDays.MODIFIED_JULIAN_DATE));

    }

}
