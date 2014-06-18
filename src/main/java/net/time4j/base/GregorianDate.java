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
 * <p>Defines a common calendar date which is based on gregorian calendar
 * rules. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Definiert ein allgemeines Datum, das auf den gregorianischen
 * Kalenderregeln beruht. </p>
 *
 * @author  Meno Hochschild
 */
public interface GregorianDate {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Yields the proleptic year according to ISO-8601. </p>
     *
     * <p>The term <i>proleptic</i> means that the gregorian calendar rules
     * are applied backwards even before the introduction of this calendar.
     * Second: The year numbering is just the mathematical one as defined
     * in ISO-8601 such that there is a year zero and even negative years:
     * -2 = BC 3, -1 = BC 2, 0 = BC 1, 1 = AD 1, 2 = AD 2, ... </p>
     *
     * @return  proleptic iso year in range
     *          {@link GregorianMath#MIN_YEAR} - {@link GregorianMath#MAX_YEAR}
     */
    /*[deutsch]
     * <p>Liefert das proleptische Jahr entsprechend dem ISO-8601-Standard. </p>
     *
     * <p>Der Begriff <i>proleptic</i> bedeutet, da&szlig; die gregorianischen
     * Kalenderregeln r&uuml;ckw&auml;rts sogar vor der Einf&uuml;hrung
     * des gregorianischen Kalenders angewandt werden. Zweitens: Die
     * Jahresz&auml;hlung ist eine rein mathematische wie in ISO-8601
     * definiert (und von Astronomen benutzt) so, da&szlig; es ein Jahr 0
     * und sogar negative Jahre gibt:
     * -2 = BC 3, -1 = BC 2, 0 = BC 1, 1 = AD 1, 2 = AD 2, ... </p>
     *
     * @return  proleptic iso year in range
     *          {@link GregorianMath#MIN_YEAR} - {@link GregorianMath#MAX_YEAR}
     */
    int getYear();

    /**
     * <p>Yields the gregorian month as integer. </p>
     *
     * @return  gregorian month in range (1 = January, ..., 12 = December)
     */
    /*[deutsch]
     * <p>Liefert den gregorianischen Monat als Integer. </p>
     *
     * @return  gregorian month in range (1 = January, ..., 12 = December)
     */
    int getMonth();

    /**
     * <p>Yields the day of month. </p>
     *
     * @return  day of month in range {@code 1 <= dayOfMonth <= 31}
     */
    /*[deutsch]
     * <p>Liefert den Tag des Monats. </p>
     *
     * @return  day of month in range {@code 1 <= dayOfMonth <= 31}
     */
    int getDayOfMonth();

    /**
     * <p>Yields a canonical representation in ISO-format
     * &quot;YYYY-MM-DD&quot;. </p>
     *
     * @return  date in ISO-8601-format YYYY-MM-DD
     */
    /*[deutsch]
     * <p>Liefert eine kanonische Darstellung im ISO-Format
     * &quot;YYYY-MM-DD&quot;. </p>
     *
     * @return  date in ISO-8601-format YYYY-MM-DD
     */
    @Override
    String toString();

}
