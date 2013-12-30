/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (GregorianDate.java) is part of project Time4J.
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

package net.time4j.base;


/**
 * <p>Definiert ein allgemeines Datum, das auf den gregorianischen
 * Kalenderregeln beruht. </p>
 *
 * @author  Meno Hochschild
 */
public interface GregorianDate {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Liefert das proleptische Jahr entsprechend dem ISO-8601-Standard. </p>
     *
     * @return  int im Bereich
     *          {@link GregorianMath#MIN_YEAR} - {@link GregorianMath#MAX_YEAR}
     */
    int getYear();

    /**
     * <p>Liefert den gregorianischen Monat als Integer. </p>
     *
     * @return  int (1 = Januar, ..., 12 = Dezember)
     */
    int getMonth();

    /**
     * <p>Liefert den Tag des Monats. </p>
     *
     * @return  int im Bereich {@code 1 <= dayOfMonth <= 31}
     */
    int getDayOfMonth();

    /**
     * <p>Liefert eine kanonische Darstellung im ISO-Format
     * &quot;YYYY-MM-DD&quot;. </p>
     *
     * @return  Datum im ISO-8601-Format
     */
    @Override
    String toString();

}
