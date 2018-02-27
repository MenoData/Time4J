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

import net.time4j.base.MathUtils;


/**
 * <p>Represents a way to specify the year used in Chinese calendar and its derivates. </p>
 *
 * @author  Meno Hochschild
 * @since   3.40/4.35
 * @see     CyclicYear#inQingDynasty(ChineseEra)
 * @see     CyclicYear#inCycle(int)
 */
/*[deutsch]
 * <p>Repr&auml;sentiert einen Weg, das Jahr im chinesischen Kalender oder dessen Ableitungen anzugeben. </p>
 *
 * @author  Meno Hochschild
 * @since   3.40/4.35
 * @see     CyclicYear#inQingDynasty(ChineseEra)
 * @see     CyclicYear#inCycle(int)
 */
public abstract class EastAsianYear {

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
    public static EastAsianYear forGregorian(final int relatedGregorianYear) {
        return new EastAsianYear() {
            @Override
            public int getElapsedCyclicYears() {
                return MathUtils.safeAdd(relatedGregorianYear, 2636);
            }
        };
    }

    /**
     * <p>Determines the East Asian year corresponding to given minguo year
     * which starts counting in gregorian year 1912 or later. </p>
     *
     * <p>Used in Taiwan. </p>
     *
     * @param   minguoYear    the minguo year which contains the first day of East Asian year
     * @return  EastAsianYear
     * @throws  IllegalArgumentException if the year is smaller than 1
     */
    /*[deutsch]
     * <p>Bestimmt das ostasiatische Jahr, das dem angegebenen Minguo-Kalenderjahr entspricht, so da&szlig;
     * das Minguo-Jahr (ab gregorianisch 1912) den Neujahrstag des ostasiatischen Jahres enth&auml;lt. </p>
     *
     * <p>Verwendet in Taiwan. </p>
     *
     * @param   relatedGregorianYear    the gregorian calendar year which contains the first day of East Asian year
     * @return  EastAsianYear
     * @throws  IllegalArgumentException if the year is smaller than 1
     */
    public static EastAsianYear forMinguo(int minguoYear) {
        if (minguoYear < 1) {
            throw new IllegalArgumentException("Minguo year must not be smaller than 1: " + minguoYear);
        }
        return forGregorian(MathUtils.safeAdd(minguoYear, 1911));
    }

    /**
     * <p>Determines the East Asian year corresponding to given dangi year
     * which starts counting in year BC 2333 or later. </p>
     *
     * @param   dangiYear       the dangi year which contains the first day of East Asian year
     * @return  EastAsianYear
     * @throws  IllegalArgumentException if the year is smaller than 1
     * @see     KoreanEra
     */
    /*[deutsch]
     * <p>Bestimmt das ostasiatische Jahr, das dem angegebenen Dangi-Kalenderjahr entspricht, so da&szlig;
     * das Dangi-Jahr den Neujahrstag des ostasiatischen Jahres enth&auml;lt. </p>
     *
     * @param   dangiYear       the dangi year which contains the first day of East Asian year
     * @return  EastAsianYear
     * @throws  IllegalArgumentException if the year is smaller than 1
     * @see     KoreanEra
     */
    public static EastAsianYear forDangi(int dangiYear) {
        if (dangiYear < 1) {
            throw new IllegalArgumentException("Dangi year must not be smaller than 1: " + dangiYear);
        }
        return forGregorian(MathUtils.safeAdd(dangiYear, -2333));
    }

    /**
     * <p>Determines the East Asian year corresponding to given Juche year
     * which starts counting in gregorian year 1912 or later. </p>
     *
     * <p>Used in North Korea. </p>
     *
     * @param   jucheYear       the Juche year which contains the first day of East Asian year
     * @return  EastAsianYear
     * @throws  IllegalArgumentException if the year is smaller than 1
     * @see     KoreanEra
     */
    /*[deutsch]
     * <p>Bestimmt das ostasiatische Jahr, das dem angegebenen Juche-Kalenderjahr entspricht, so da&szlig;
     * das Juche-Jahr (ab gregorianisch 1912) den Neujahrstag des ostasiatischen Jahres enth&auml;lt. </p>
     *
     * <p>Verwendet in Nordkorea. </p>
     *
     * @param   relatedGregorianYear    the gregorian calendar year which contains the first day of East Asian year
     * @return  EastAsianYear
     * @throws  IllegalArgumentException if the year is smaller than 1
     * @see     KoreanEra
     */
    public static EastAsianYear forJuche(int jucheYear) {
        if (jucheYear < 1) {
            throw new IllegalArgumentException("Juche year must not be smaller than 1: " + jucheYear);
        }
        return forGregorian(MathUtils.safeAdd(jucheYear, 1911));
    }

    /**
     * <p>Determines the number of associated sexagesimal year cycle. </p>
     *
     * @return  number of cycle
     */
    /*[deutsch]
     * <p>Bestimmt die Nummer des assoziierten sexagesimalen Jahreszyklus. </p>
     *
     * @return  number of cycle
     */
    public final int getCycle() {
        int extYear = this.getElapsedCyclicYears() + 1;
        return MathUtils.floorDivide(extYear - 1, 60) + 1;
    }

    /**
     * <p>Determines the cyclic year. </p>
     *
     * @return  cyclic year
     */
    /*[deutsch]
     * <p>Bestimmt das zyklische Jahr. </p>
     *
     * @return  cyclic year
     */
    public final CyclicYear getYearOfCycle() {
        int extYear = this.getElapsedCyclicYears() + 1;
        int yearOfCycle = MathUtils.floorModulo(extYear, 60);
        if (yearOfCycle == 0) {
            yearOfCycle = 60;
        }
        return CyclicYear.of(yearOfCycle);
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
    public abstract int getElapsedCyclicYears();

}
