/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (GregorianDate.java) is part of project Time4J.
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
     * @return  proleptic iso year in range
     *          {@link GregorianMath#MIN_YEAR} - {@link GregorianMath#MAX_YEAR}
     */
    int getYear();

    /**
     * <p>Liefert den gregorianischen Monat als Integer. </p>
     *
     * @return  gregorian month in range (1 = January, ..., 12 = December)
     */
    int getMonth();

    /**
     * <p>Liefert den Tag des Monats. </p>
     *
     * @return  day of month in range {@code 1 <= dayOfMonth <= 31}
     */
    int getDayOfMonth();

    /**
     * <p>Liefert eine kanonische Darstellung im ISO-Format
     * &quot;YYYY-MM-DD&quot;. </p>
     *
     * @return  date in ISO-8601-format YYYY-MM-DD
     */
    @Override
    String toString();

}
