/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (GregorianMath.java) is part of project Time4J.
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
 * <p>Contains some calendrical tools for the rules of gregorian calendar. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Enth&auml;lt kalendarische Hilfsmittel f&uuml;r die Regeln des
 * gregorianischen Kalenders. </p>
 *
 * @author  Meno Hochschild
 */
public final class GregorianMath {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * Minimum of supported year range (-999999999).
     */
    /*[deutsch]
     * Minimal unterst&uuml;tze Jahreszahl (-999999999).
     */
    public static final int MIN_YEAR = -999999999;

    /**
     * Maximum of supported year range (999999999).
     */
    /*[deutsch]
     * Maximal unterst&uuml;tze Jahreszahl (999999999).
     */
    public static final int MAX_YEAR = 999999999;

    // Tage zwischen [0000-03-01] und [1970-01-01] minus MJD-Epoche
    private static final int OFFSET = 719468 - 40587;

    //~ Konstruktoren -----------------------------------------------------

    private GregorianMath() {
        // keine Instanzierung
    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Queries if given year is a gregorian leap year. </p>
     *
     * @param   year    number of proleptic year
     * @return  {@code true} if it is a leap year else {@code false}
     */
    /*[deutsch]
     * <p>Ist das angegebene Jahr ein gregorianisches Schaltjahr? </p>
     *
     * @param   year    proleptisches Jahr
     * @return  {@code true} if it is a leap year else {@code false}
     */
    public static boolean isLeapYear(int year) {

        if ((year % 4) != 0) {
            return false;
        }

        return ((year % 100) != 0) || ((year % 400) == 0);

    }

    /**
     * <p>Determines the maximum length of month in days dependent on given
     * year (leap years!) and month. </p>
     *
     * @param   year    proleptic iso year
     * @param   month   gregorian month (1-12)
     * @return  length of month in days
     * @throws  IllegalArgumentException if month is out of range (1-12)
     */
    /*[deutsch]
     * <p>Ermittelt die maximale L&auml;nge des Monats in Tagen abh&auml;ngig
     * vom angegebenen Jahr (Schaltjahre!) und Monat. </p>
     *
     * @param   year    proleptic iso year
     * @param   month   gregorian month (1-12)
     * @return  length of month in days
     * @throws  IllegalArgumentException if month is out of range (1-12)
     */
    public static int getLengthOfMonth(
        int year,
        int month
    ) {

        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                return (isLeapYear(year) ? 29 : 28);
            default:
                throw new IllegalArgumentException("Invalid month: " + month);
        }

    }

    /**
     * <p>Queries if given values form a well defined gregorian date. </p>
     *
     * <p>This method only checks the range limits, not if the date is
     * historically correct. </p>
     *
     * @param   year        proleptic iso year [(-999999999) - 999999999]
     * @param   month       gregorian month (1-12)
     * @param   dayOfMonth  day of month (1-31)
     * @return  {@code true} if valid else  {@code false}
     * @see     #checkDate(int, int, int)
     */
    /*[deutsch]
     * <p>Handelt es sich um ein wohldefiniertes gregorianisches Datum? </p>
     *
     * <p>Hier werden nur die Bereichsgrenzen &uuml;berpr&uuml;ft, nicht die
     * historische Sinnhaftigkeit. </p>
     *
     * @param   year        proleptic iso year [(-999999999) - 999999999]
     * @param   month       gregorian month (1-12)
     * @param   dayOfMonth  day of month (1-31)
     * @return  {@code true} if valid else  {@code false}
     * @see     #checkDate(int, int, int)
     */
    public static boolean isValid(
        int year,
        int month,
        int dayOfMonth
    ) {

        return (
            (year >= MIN_YEAR)
            && (year <= MAX_YEAR)
            && (month >= 1)
            && (month <= 12)
            && (dayOfMonth >= 1)
            && (dayOfMonth <= getLengthOfMonth(year, month))
        );

    }

    /**
     * <p>Checks the range limits of date values according to the rules
     * of gregorian calendar. </p>
     *
     * @param   year        proleptic iso year [(-999999999) - 999999999]
     * @param   month       gregorian month (1-12)
     * @param   dayOfMonth  day of month (1-31)
     * @throws  IllegalArgumentException if any argument is out of range
     * @see     #isValid(int, int, int)
     */
    /*[deutsch]
     * <p>&Uuml;berpr&uuml;ft die Bereichsgrenzen der Datumswerte nach
     * den gregorianischen Kalenderregeln. </p>
     *
     * @param   year        proleptic iso year [(-999999999) - 999999999]
     * @param   month       gregorian month (1-12)
     * @param   dayOfMonth  day of month (1-31)
     * @throws  IllegalArgumentException if any argument is out of range
     * @see     #isValid(int, int, int)
     */
    public static void checkDate(
        int year,
        int month,
        int dayOfMonth
    ) {

        if (year < MIN_YEAR || year > MAX_YEAR) {
            throw new IllegalArgumentException(
                "YEAR out of range: " + year);
        } else if ((month < 1) || (month > 12)) {
            throw new IllegalArgumentException(
                "MONTH out of range: " + month);
        } else if ((dayOfMonth < 1) || (dayOfMonth > 31)) {
            throw new IllegalArgumentException(
                "DAY_OF_MONTH out of range: " + dayOfMonth);
        } else if (dayOfMonth > getLengthOfMonth(year, month)) {
            throw new IllegalArgumentException(
                "DAY_OF_MONTH exceeds month length in given year: "
                + toString(year, month, dayOfMonth));
        }

    }

    /**
     * <p>Returns the day of week for given gregorian date. </p>
     *
     * <p>This method is based on ISO-8601 and assumes that Monday is the
     * first day of week. </p>
     *
     * @param   year        proleptic iso year
     * @param   month       gregorian month (1-12)
     * @param   dayOfMonth  day of month (1-31)
     * @return  day of week (monday = 1, ..., sunday = 7)
     * @throws  IllegalArgumentException if the month or the day are
     *          out of range
     */
    /*[deutsch]
     * <p>Liefert den Tag des Woche f&uuml;r das angegebene Datum. </p>
     *
     * <p>Diese Methode setzt gem&auml;&szlig; dem ISO-8601-Standard den
     * Montag als ersten Tag der Woche voraus. </p>
     *
     * @param   year        proleptic iso year
     * @param   month       gregorian month (1-12)
     * @param   dayOfMonth  day of month (1-31)
     * @return  day of week (monday = 1, ..., sunday = 7)
     * @throws  IllegalArgumentException if the month or the day are
     *          out of range
     */
    public static int getDayOfWeek(
        int year,
        int month,
        int dayOfMonth
    ) {

        if ((dayOfMonth < 1) || (dayOfMonth > 31)) {
            throw new IllegalArgumentException(
                "Day out of range: " + dayOfMonth);
        } else if (dayOfMonth > getLengthOfMonth(year, month)) {
            throw new IllegalArgumentException(
                "Day exceeds month length: "
                + toString(year, month, dayOfMonth));
        }

        int m = gaussianWeekTerm(month);
        int y = (year % 100);
        int c = MathUtils.floorDivide(year, 100);

        if (y < 0) {
            y += 100;
        }

        if (month <= 2) { // Januar oder Februar
            y--;
            if (y < 0) {
                y = 99;
                c--;
            }
        }

        // Gauß'sche Wochentagsformel
        int k = MathUtils.floorDivide(c, 4);
        int w = ((dayOfMonth + m + y + (y / 4) + k - 2 * c) % 7);

        if (w <= 0) {
            w += 7;
        }

        return w;

    }

    /**
     * <p>Returns the year from given binary compressed date. </p>
     *
     * @param   packedDate  packed date in binary format
     * @return  proleptic iso year
     * @see     #toPackedDate(long)
     */
    /*[deutsch]
     * <p>Liefert das Jahr des angegebenen bin&auml;r gepackten Datums. </p>
     *
     * @param   packedDate  packed date in binary format
     * @return  proleptic iso year
     * @see     #toPackedDate(long)
     */
    public static int readYear(long packedDate) {

        return (int) ((packedDate >> 32));

    }

    /**
     * <p>Returns the month from given binary compressed date. </p>
     *
     * @param   packedDate  packed date in binary format
     * @return  gregorian month (1-12)
     * @see     #toPackedDate(long)
     */
    /*[deutsch]
     * <p>Liefert den Monat des angegebenen bin&auml;r gepackten Datums. </p>
     *
     * @param   packedDate  packed date in binary format
     * @return  gregorian month (1-12)
     * @see     #toPackedDate(long)
     */
    public static int readMonth(long packedDate) {

        return (int) ((packedDate >> 16) & 0xFF);

    }

    /**
     * <p>Returns the day of month from given binary compressed date. </p>
     *
     * @param   packedDate  packed date in binary format
     * @return  day of month (1-31)
     * @see     #toPackedDate(long)
     */
    /*[deutsch]
     * <p>Liefert den Tag des Monats im angegebenen bin&auml;r gepackten
     * Datum. </p>
     *
     * @param   packedDate  packed date in binary format
     * @return  day of month (1-31)
     * @see     #toPackedDate(long)
     */
    public static int readDayOfMonth(long packedDate) {

        return (int) (packedDate & 0xFF);

    }

    /**
     * <p>Calculates the gregorian Date based on given modified julian date
     * in binary compressed format. </p>
     *
     * <p>Applications can extract the single date components from the result
     * of this method by mean of {@code readYear()}, {@code readMonth()} and
     * {@code readDayOfMonth()}. </p>
     *
     * @param   mjd         days since [1858-11-17] (modified julian date)
     * @return  packed date in binary format
     * @throws  IllegalArgumentException if the calculated year is not in
     *          range [(-999999999)-999999999)]
     * @see     #readYear(long)
     * @see     #readMonth(long)
     * @see     #readDayOfMonth(long)
     */
    /*[deutsch]
     * <p>Berechnet das gregorianische Datum auf Basis des angegebenen
     * modifizierten julianischen Datums. </p>
     *
     * <p>Mit Hilfe von {@code readYear()}, {@code readMonth()} und
     * {@code readDayOfMonth()} k&ouml;nnen aus dem Ergebnis die einzelnen
     * Datumselemente extrahiert werden. </p>
     *
     * @param   mjd         days since [1858-11-17] (modified julian date)
     * @return  packed date in binary format
     * @throws  IllegalArgumentException if the calculated year is not in
     *          range [(-999999999)-999999999)]
     * @see     #readYear(long)
     * @see     #readMonth(long)
     * @see     #readDayOfMonth(long)
     */
    public static long toPackedDate(long mjd) {

        long y;
        int m;
        int d;

        long days = MathUtils.safeAdd(mjd, OFFSET);

        // TODO: Optimierung der Performance im Jahresbereich 1901-2099
        //       mit vereinfachtem Schaltjahresalgorithmus
        // if (days >= year1901 && days < year2100) { ... }

        long q400 = MathUtils.floorDivide(days, 146097);
        int r400 = MathUtils.floorModulo(days, 146097);

        if (r400 == 146096) {
            y = (q400 + 1) * 400;
            m = 2;
            d = 29;
        } else {
            int q100 = (r400 / 36524);
            int r100 = (r400 % 36524);

            int q4 = (r100 / 1461);
            int r4 = (r100 % 1461);

            if (r4 == 1460) {
                y = (q400 * 400 + q100 * 100 + (q4 + 1) * 4);
                m = 2;
                d = 29;
            } else {
                int q1 = (r4 / 365);
                int r1 = (r4 % 365);

                y = (q400 * 400 + q100 * 100 + q4 * 4 + q1);
                m = (((r1 + 31) * 5) / 153) + 2;
                d = r1 - (((m + 1) * 153) / 5) + 123;

                if (m > 12) {
                    y++;
                    m -= 12;
                }
            }
        }

        if (y < GregorianMath.MIN_YEAR || y > GregorianMath.MAX_YEAR) {
            throw new IllegalArgumentException(
                "Year out of range: " + y);
        }

        long result = (y << 32);
        result |= (m << 16);
        result |= d;
        return result;

    }

    /**
     * <p>Calculates the modified julian date. </p>
     *
     * @param   date    gregorian date
     * @return  days since [1858-11-17] (modified julian date)
     * @throws  IllegalArgumentException if the argument is out of range
     * @see     #toMJD(int, int, int)
     */
    /*[deutsch]
     * <p>Ermittelt das modifizierte julianische Datum. </p>
     *
     * @param   date    gregorian date
     * @return  days since [1858-11-17] (modified julian date)
     * @throws  IllegalArgumentException if the argument is out of range
     * @see     #toMJD(int, int, int)
     */
    public static long toMJD(GregorianDate date) {

        return GregorianMath.toMJD(
            date.getYear(), date.getMonth(), date.getDayOfMonth());
    }

    /**
     * <p>Calculates the modified julian date. </p>
     *
     * @param   year        proleptic iso year [(-999999999) - 999999999]
     * @param   month       gregorian month (1-12)
     * @param   dayOfMonth  day of month in range (1-31)
     * @return  days since [1858-11-17] (modified julian date)
     * @throws  IllegalArgumentException if any argument is out of range
     */
    /*[deutsch]
     * <p>Ermittelt das modifizierte julianische Datum. </p>
     *
     * @param   year        proleptic iso year [(-999999999) - 999999999]
     * @param   month       gregorian month (1-12)
     * @param   dayOfMonth  day of month in range (1-31)
     * @return  days since [1858-11-17] (modified julian date)
     * @throws  IllegalArgumentException if any argument is out of range
     */
    // TODO: Optimierung der Performance mit Jahres-Array im Bereich 1901-2099
    public static long toMJD(
        int year,
        int month,
        int dayOfMonth
    ) {

        checkDate(year, month, dayOfMonth);

        long y = year;
        int m = month;

        if (m < 3) {
            y--;
            m += 12;
        }

        long days = (
            (y * 365)
            + MathUtils.floorDivide(y, 4)
            - MathUtils.floorDivide(y, 100)
            + MathUtils.floorDivide(y, 400)
            + (((m + 1) * 153) / 5) - 123
            + dayOfMonth
        );

        return days - OFFSET;

    }

    /**
     * <p>Liefert eine ISO-konforme Standard-Darstellung eines Datums. </p>
     *
     * @param   year    proleptic iso year
     * @param   month   gregorian month
     * @param   dom     day of month
     * @return  String in iso format YYYY-MM-DD
     */
    static String toString(int year, int month, int dom) {

        StringBuilder calendar = new StringBuilder();
        calendar.append(year);
        calendar.append('-');
        if (month < 10) {
            calendar.append('0');
        }
        calendar.append(month);
        calendar.append('-');
        if (dom < 10) {
            calendar.append('0');
        }
        calendar.append(dom);
        return calendar.toString();

    }

    // entspricht dem Ausdruck [2.6 * m - 0.2] in der Gauß-Formel
    private static int gaussianWeekTerm(int month) {

        switch (month) {
            case 1:
                return 28;
            case 2:
                return 31;
            case 3:
                return 2;
            case 4:
                return 5;
            case 5:
                return 7;
            case 6:
                return 10;
            case 7:
                return 12;
            case 8:
                return 15;
            case 9:
                return 18;
            case 10:
                return 20;
            case 11:
                return 23;
            case 12:
                return 25;
            default:
                throw new IllegalArgumentException(
                    "Month out of range: " + month);
        }

    }

}
