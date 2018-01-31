/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (EastAsianYear.java) is part of project Time4J.
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

package net.time4j.calendar;


/**
 * <p>Represents a way to specify the year used in Chinese calendar and its derivates. </p>
 *
 * @author  Meno Hochschild
 * @since   3.40/4.35
 */
/*[deutsch]
 * <p>Repr&auml;sentiert einen Weg, das Jahr im chinesischen Kalender oder dessen Ableitungen anzugeben. </p>
 *
 * @author  Meno Hochschild
 * @since   3.40/4.35
 */
@FunctionalInterface
public interface EastAsianYear {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Determines the East Asian year corresponding to given related gregorian year. </p>
     *
     * @param   relatedGregorianYear    the gregorian calendar year which contains the first day of East Asian year
     * @return  EastAsianYear
     */
    /*[deutsch]
     * <p>Bestimmt das ostasiatische Jahr, das dem angegebenen gregorianischen Kalenderjahr entspricht, so da&szlig;
     * das gregorianische Jahr den Neujahrstag des ostasiatischen Jahres enth&auml;lt. </p>
     *
     * @param   relatedGregorianYear    the gregorian calendar year which contains the first day of East Asian year
     * @return  EastAsianYear
     */
    static EastAsianYear forGregorian(int relatedGregorianYear) {
        return () -> relatedGregorianYear + 2637;
    }

    /**
     * Marks the traditional introduction date of sexagesimal cyclic years in Julian year 2637 BCE
     * by the legendary yellow emperor Huang-di.
     *
     * @return  count of sexagesimal cyclic years since Julian year 2637 BCE
     */
    /*[deutsch]
     * Kennzeichnet das Datum, das traditionell mit der Einf&uuml;hrung der sexagesimalen Jahreszyklen
     * durch den mythischen Kaiser Huang-di im (julianischen) Jahr 2637 BC verkn&uuml;pft ist.
     *
     * @return  count of sexagesimal cyclic years since Julian year 2637 BCE
     */
    int getElapsedCyclicYears();

}
