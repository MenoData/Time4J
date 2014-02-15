/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Quarter.java) is part of project Time4J.
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

package net.time4j;


/**
 * <p>Repr&auml;sentiert ein Quartal (meist eines Jahres). </p>
 *
 * @author  Meno Hochschild
 */
public enum Quarter {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Erstes Quartal mit dem numerischen Wert {@code 1}. </p>
     */
    Q1,

    /**
     * <p>Zweites Quartal mit dem numerischen Wert {@code 2}. </p>
     */
    Q2,

    /**
     * <p>Drittes Quartal mit dem numerischen Wert {@code 3}. </p>
     */
    Q3,

    /**
     * <p>Letztes Quartal mit dem numerischen Wert {@code 4}. </p>
     */
    Q4;

    private static final Quarter[] ENUMS = Quarter.values(); // Cache

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Liefert die zum kalendarischen Integer-Wert passende
     * Enum-Konstante. </p>
     *
     * @param   quarter     value in the range [1-4]
     * @return  enum
     * @throws  IllegalArgumentException if given argument is out of range
     */
    public static Quarter valueOf(int quarter) {

        if ((quarter < 1) || (quarter > 4)) {
            throw new IllegalArgumentException("Out of range: " + quarter);
        }

        return ENUMS[quarter - 1];

    }

    /**
     * <p>Liefert den korrespondierenden kalendarischen Integer-Wert. </p>
     *
     * @return  int (Q1 = 1, Q2 = 2, Q3 = 3, Q4 = 4)
     */
    public int getValue() {

        return (this.ordinal() + 1);

    }

    /**
     * <p>Ermittelt das n&auml;chste Quartal. </p>
     *
     * <p>Auf {@code Q4} angewandt ist das Ergebnis {@code Q1}. </p>
     *
     * @return  next quarter rolling at last quarter
     */
    public Quarter next() {

        return this.roll(1);

    }

    /**
     * <p>Ermittelt das vorherige Quartal. </p>
     *
     * <p>Auf {@code Q1} angewandt ist das Ergebnis {@code Q4}. </p>
     *
     * @return  previous quarter rolling at first quarter
     */
    public Quarter previous() {

        return this.roll(-1);

    }

    /**
     * <p>Rollt um die angegebene Anzahl von Quartalen vor oder
     * zur&uuml;ck. </p>
     *
     * @param   quarters    count of quarteryears (maybe negative)
     * @return  result of rolling operation
     */
    public Quarter roll(int quarters) {

        return Quarter.valueOf(
            (this.ordinal() + (quarters % 4 + 4)) % 4 + 1);

    }

}
