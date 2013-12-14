/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (GregorianMath.java) is part of project Time4J.
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
 * <p>Enth&auml;lt kalendarische Hilfsmittel f&uuml;r die Regeln des
 * gregorianischen Kalenders. </p>
 *
 * @author  Meno Hochschild
 */
public final class GregorianMath {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * Minimal unterst&uuml;tze Jahreszahl (-999999999).
     */
    public static final int MIN_YEAR = -999999999;

    /**
     * Maximal unterst&uuml;tze Jahreszahl (999999999).
     */
    public static final int MAX_YEAR = 999999999;

    // Tage zwischen [0000-03-01] und [1970-01-01]
    private static final int OFFSET = 719468;

    //~ Konstruktoren -----------------------------------------------------

    private GregorianMath() {
        // keine Instanzierung
    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Ist das angegebene Jahr ein gregorianisches Schaltjahr? </p>
     *
     * @param   year    Jahr
     * @return  {@code true} wenn ein Schaltjahr vorliegt, sonst {@code false}
     */
    public static boolean isLeapYear(int year) {

        if ((year % 4) != 0) {
            return false;
        }

        return ((year % 100) != 0) || ((year % 400) == 0);

    }

    /**
     * <p>Ermittelt die maximale L&auml;nge des Monats in Tagen abh&auml;ngig
     * vom angegebenen Jahr (Schaltjahre!) und Monat. </p>
     *
     * @param   year    Jahresangabe
     * @param   month   Monatsangabe (1-12)
     * @return  Monatsl&auml;nge in Tagen
     * @throws  IllegalArgumentException wenn Monat nicht im Bereich 1-12 ist
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
     * <p>Handelt es sich um ein wohldefiniertes gregorianisches Datum? </p>
     *
     * <p>Hier werden nur die Bereichsgrenzen &uuml;berpr&uuml;ft, nicht die
     * historische Sinnhaftigkeit. Aus technischen Gr&uuml;nden ist der
     * Jahresbereich ebenfalls beschr&auml;nkt, was aber der praktischen
     * N&uuml;tzlichkeit keinen Abbruch tut. </p>
     *
     * @param   year        Jahr [(-999999999) - 999999999]
     * @param   month       Monat (1-12)
     * @param   dayOfMonth  Tag des Monats (1-31)
     * @return  {@code true} wenn g&uuml;ltig, sonst {@code false}
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
     * <p>&Uuml;berpr&uuml;ft die Bereichsgrenzen der Datumswerte nach
     * den gregorianischen Kalenderregeln. </p>
     *
     * <p>Hier werden nur die Bereichsgrenzen &uuml;berpr&uuml;ft, nicht die
     * historische Sinnhaftigkeit. Aus technischen Gr&uuml;nden ist der
     * Jahresbereich ebenfalls beschr&auml;nkt, was aber der praktischen
     * N&uuml;tzlichkeit keinen Abbruch tut. </p>
     *
     * @param   year        Jahr [(-999999999) - 999999999]
     * @param   month       Monat (1-12)
     * @param   dayOfMonth  Tag des Monats (1-31)
     * @throws  IllegalArgumentException wenn ein Argument im falschen
     *          Bereich liegt
     * @see     #isValid(int, int, int)
     */
    public static void checkDate(
        int year,
        int month,
        int dayOfMonth
    ) {

        if (year < MIN_YEAR || year > MAX_YEAR) {
            throw new IllegalArgumentException(
                "Year out of range: " + year);
        } else if ((month < 1) || (month > 12)) {
            throw new IllegalArgumentException(
                "Month out of range: " + month);
        } else if ((dayOfMonth < 1) || (dayOfMonth > 31)) {
            throw new IllegalArgumentException(
                "Day out of range: " + dayOfMonth);
        } else if (dayOfMonth > getLengthOfMonth(year, month)) {
            throw new IllegalArgumentException(
                "Day exceeds month length: "
                + toString(year, month, dayOfMonth));
        }

    }

    /**
     * <p>Liefert den Tag des Woche f&uuml;r das angegebene Datum. </p>
     *
     * <p>Diese Methode setzt gem&auml;&szlig; dem ISO-8601-Standard den
     * Montag als ersten Tag der Woche voraus. </p>
     *
     * @param   year        Jahr
     * @param   month       Monat (1-12)
     * @param   dayOfMonth  Tag des Monats (1-31)
     * @return  Wochentag (Montag = 1, ..., Sonntag = 7)
     * @throws  IllegalArgumentException wenn der Monat oder der Tag nicht im
     *          richtigen Bereich liegen
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
     * <p>Liefert das Jahr des angegebenen bin&auml;r gepackten Datums. </p>
     *
     * @param   packedDate  gepacktes Datum
     * @return  proleptisches ISO-Jahr
     * @see     #toPackedDate(long)
     */
    public static int readYear(long packedDate) {

        return (int) ((packedDate >> 32) & 0xFFFFFFFF);

    }

    /**
     * <p>Liefert den Monat des angegebenen bin&auml;r gepackten Datums. </p>
     *
     * @param   packedDate  gepacktes Datum
     * @return  Monat (1-12)
     * @see     #toPackedDate(long)
     */
    public static int readMonth(long packedDate) {

        return (int) ((packedDate >> 16) & 0xFF);

    }

    /**
     * <p>Liefert den Tag des Monats im angegebenen bin&auml;r gepackten
     * Datum. </p>
     *
     * @param   packedDate  gepacktes Datum
     * @return  Tag des Monats (1-31)
     * @see     #toPackedDate(long)
     */
    public static int readDayOfMonth(long packedDate) {

        return (int) (packedDate & 0xFF);

    }

    /**
     * <p>Berechnet das gregorianische Datum auf Basis der angegebenen
     * UNIX-Zeit in Sekunden. </p>
     *
     * <p>Mit Hilfe von {@code readYear()}, {@code readMonth()} und
     * {@code readDayOfMonth()} k&ouml;nnen aus dem Ergebnis die einzelnen
     * Datumselemente extrahiert werden. </p>
     *
     * @param   posixTime   Zeit in Sekunden seit [1970-01-01T00:00:00Z]
     *                      ohne Ber&uuml;cksichtigung von UTC-Schaltsekunden
     * @return  gepacktes Datum im Bin&auml;rformat
     * @throws  IllegalArgumentException wenn das berechnete Jahr nicht im
     *          Bereich [(-999999999)-999999999)] liegt
     * @see     #readYear(long)
     * @see     #readMonth(long)
     * @see     #readDayOfMonth(long)
     */
    public static long toPackedDate(long posixTime) {

        long y;
        int m;
        int d;

        long days = MathUtils.floorDivide(posixTime, 86400) + OFFSET;

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
     * <p>Ermittelt die Anzahl von Sekunden seit der UNIX-Epoche f&uuml;r
     * das angegebene gregorianische Datum. </p>
     *
     * <p>Hinweis: Das Ergebnis stellt die Uhrzeit zu Mitternacht dar. </p>
     *
     * @param   date    gregorianisches Datum
     * @return  Anzahl der Sekunden seit [1970-01-01T00:00:00Z]
     *          ohne Ber&uuml;cksichtigung von UTC-Schaltsekunden
     * @throws  IllegalArgumentException wenn das Argument im falschen
     *          Bereich liegt
     */
    public static long toPosixTime(GregorianDate date) {

        return GregorianMath.toPosixTime(
            date.getYear(), date.getMonth(), date.getDayOfMonth());
    }

    /**
     * <p>Ermittelt die Anzahl von Sekunden seit der UNIX-Epoche f&uuml;r
     * das angegebene gregorianische Datum. </p>
     *
     * <p>Hinweis: Das Ergebnis stellt die Uhrzeit zu Mitternacht dar. </p>
     *
     * @param   year        Jahr [(-999999999) - 999999999]
     * @param   month       Monat (1-12)
     * @param   dayOfMonth  Tag des Monats (1-31)
     * @return  Anzahl der Sekunden seit [1970-01-01T00:00:00Z]
     *          ohne Ber&uuml;cksichtigung von UTC-Schaltsekunden
     * @throws  IllegalArgumentException wenn ein Argument im falschen
     *          Bereich liegt
     */
    // TODO: Optimierung der Performance mit Jahres-Array im Bereich 1901-2099
    public static long toPosixTime(
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

        return MathUtils.safeMultiply(days - OFFSET, 86400);

    }

    /**
     * <p>Liefert eine ISO-konforme Standard-Darstellung eines Datums. </p>
     *
     * @param   year    Jahr
     * @param   month   Monat
     * @param   dom     Tag des Monats
     * @return  String im Format YYYY-MM-DD
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
