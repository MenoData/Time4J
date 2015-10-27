/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AncientJulianLeapYears.java) is part of project Time4J.
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

import java.util.Arrays;


/**
 * <p>Represents a historic leap year pattern for the early days of julian calendar before AD 8. </p>
 *
 * <p>The real historic leap years as triennal pattern before AD 8 are not yet known for certainty and are
 * controversely debated among historicians. Anyway, all non-proleptic patterns start the julian calendar
 * in 45 BC and are to be considered invalid before that year. These patterns mainly serve for comparison
 * and must not be interpreted as absolute truth. </p>
 *
 * @author  Meno Hochschild
 * @since   3.11/4.8
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine historische Schaltjahrsequenz f&uuml;r die ersten Jahre
 * des julianischen Kalenders vor AD 8. </p>
 *
 * <p>Die realen historischen Schaltjahre vor AD 8 (im 3-Jahresrhythmus) sind noch nicht mit Sicherheit
 * bekannt und unter Historikern umstritten. Wie auch immer, alle nicht-proleptischen Sequenzen lassen den
 * julianischen Kalender erst im Jahre 45 BC beginnen und sind davor ung&uuml;ltig. Diese Sequenzen dienen
 * haupts&auml;chlich dem Vergleich und d&uuml;rfen nicht als absolute Wahrheit interpretiert werden. </p>
 *
 * @author  Meno Hochschild
 * @since   3.11/4.8
 */
public final class AncientJulianLeapYears {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int[] SEQUENCE_SCALIGER = {42, 39, 36, 33, 30, 27, 24, 21, 18, 15, 12, 9};
    private static final HistoricDate AD8 = HistoricDate.of(HistoricEra.AD, 8, 1, 1);
    private static final HistoricDate BC45 = HistoricDate.of(HistoricEra.BC, 45, 1, 1);
    private static final long MJD_OF_AD8 = -676021L; // CalendarAlgorithm.JULIAN.toMJD(AD8);

    /**
     * <p>Proposed by Joseph Justus Scaliger in year 1583, the leap years are assumed to be
     * 42, 39, 36, 33, 30, 27, 24, 21, 18, 15, 12, 9 (BC). </p>
     *
     * <p>This is the most widely used assumption among historicians. </p>
     */
    /*[deutsch]
     * <p>Von Joseph Justus Scaliger im Jahre 1583 vorgeschlagen: Die Schaltjahre vor AD 8 werden
     * als 42, 39, 36, 33, 30, 27, 24, 21, 18, 15, 12, 9 (BC) angenommen. </p>
     *
     * <p>Das ist die am weitesten verbreitete Vermutung unter Historikern. </p>
     */
    public static final AncientJulianLeapYears SCALIGER = new AncientJulianLeapYears(SEQUENCE_SCALIGER);

    //~ Instanzvariablen --------------------------------------------------

    private final int[] leaps;
    private final Calculus calculus;

    //~ Konstruktoren -----------------------------------------------------

    private AncientJulianLeapYears(final int... values) {
        super();

        int[] buffer = new int[values.length];

        for (int i = 0; i < values.length; i++) {
            buffer[i] = 1 - values[i];
        }

        Arrays.sort(buffer);
        this.leaps = buffer;

        if (this.leaps.length == 0) {
            throw new IllegalArgumentException("Missing leap years.");
        } else if ((this.leaps[0] < -44) || (this.leaps[this.leaps.length - 1] >= 8)) {
            throw new IllegalArgumentException("Out of range: " + Arrays.toString(values));
        }

        int previous = buffer[0];

        for (int i = 1; i < values.length; i++) {
            if (buffer[i] == previous) {
                throw new IllegalArgumentException("Contains duplicates: " + Arrays.toString(values));
            }
            previous = buffer[i];
        }

        this.calculus =
            new Calculus() {
                @Override
                public long toMJD(HistoricDate date) {
                    if (date.compareTo(AD8) >= 0) {
                        return CalendarAlgorithm.JULIAN.toMJD(date);
                    } else if (date.compareTo(BC45) < 0) {
                        throw new IllegalArgumentException("Not valid before BC 45: " + date);
                    }
                    long mjd = MJD_OF_AD8;
                    int target = getProlepticYear(date);
                    for (int year = 7; year >= target; year--) {
                        if (isLeapYear(year)) {
                            mjd -= 366;
                        } else {
                            mjd -= 365;
                        }
                    }
                    for (int month = 1; month < date.getMonth(); month++) {
                        mjd += this.getMaximumDayOfMonth(target, month);
                    }
                    return mjd + date.getDayOfMonth() - 1;
                }

                @Override
                public HistoricDate fromMJD(long mjd) {
                    if (mjd >= MJD_OF_AD8) {
                        return CalendarAlgorithm.JULIAN.fromMJD(mjd);
                    }
                    long test = MJD_OF_AD8;
                    for (int year = 7; year >= -44; year--) {
                        if (isLeapYear(year)) {
                            test -= 366;
                        } else {
                            test -= 365;
                        }
                        if (test <= mjd) {
                            for (int month = 1; month <= 12; month++) {
                                int len = this.getMaximumDayOfMonth(year, month);
                                if (test + len > mjd) {
                                    HistoricEra era = ((year <= 0) ? HistoricEra.BC : HistoricEra.AD);
                                    int yearOfEra = ((year <= 0) ? 1 - year : year);
                                    return HistoricDate.of(era, yearOfEra, month, (int) (mjd - test + 1));
                                } else {
                                    test += len;
                                }
                            }
                        }
                    }
                    throw new IllegalArgumentException("Not valid before BC 45: " + mjd);
                }

                @Override
                public boolean isValid(HistoricDate date) {
                    if (date != null) {
                        if ((date.getEra() == HistoricEra.AD) || date.getYearOfEra() <= 45) {
                            int y = getProlepticYear(date);
                            if (y >= 8) {
                                return CalendarAlgorithm.JULIAN.isValid(date);
                            } else {
                                return date.getDayOfMonth() <= this.getMaximumDayOfMonth(y, date.getMonth());
                            }
                        }
                    }
                    return false;
                }

                @Override
                public int getMaximumDayOfMonth(HistoricDate date) {
                    if (date.compareTo(AD8) >= 0) {
                        return CalendarAlgorithm.JULIAN.getMaximumDayOfMonth(date);
                    } else if (date.compareTo(BC45) < 0) {
                        throw new IllegalArgumentException("Not valid before BC 45: " + date);
                    } else {
                        return this.getMaximumDayOfMonth(getProlepticYear(date), date.getMonth());
                    }
                }

                private int getMaximumDayOfMonth(
                    int prolepticYear,
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
                            return (this.isLeapYear(prolepticYear) ? 29 : 28);
                        default:
                            throw new IllegalArgumentException("Invalid month: " + month);
                    }
                }

                private int getProlepticYear(HistoricDate date) {
                    return ((date.getEra() == HistoricEra.BC) ? 1 - date.getYearOfEra() : date.getYearOfEra());
                }

                private boolean isLeapYear(int prolepticYear) {
                    return (Arrays.binarySearch(AncientJulianLeapYears.this.leaps, prolepticYear) >= 0);
                }
            };

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new sequence of historical julian leap years before AD 8. </p>
     *
     * <p>Example: In order to model the proposal of Matzat (1883), users can use the code
     * {@code of(44, 41, 38, 35, 32, 29, 26, 23, 20, 17, 14, 11, -3)}. The last parameter {@code -3}
     * stands here for the year AD 4 using the formula {@code 1 - year} which shall also be
     * a leap year in the version of Matzat. For an overview about different proposals see
     * <a href="http://en.wikipedia.org/wiki/Julian_calendar">Wikipedia</a>. </p>
     *
     * @param   bcYears     positive numbers for BC-years
     * @return  new instance
     * @throws  IllegalArgumentException if given years are missing or out of range {@code BC 45 <= bcYear < AD 8}
     * @since   3.8/4.11
     */
    /*[deutsch]
     * <p>Erzeugt eine neue historische Sequenz von julianischen Schaltjahren vor dem Jahre AD 8. </p>
     *
     * <p>Beispiel: Um den Vorschlag von Matzat (1883) zu modellieren, k&ouml;nnen Anwender den Ausdruck
     * {@code of(44, 41, 38, 35, 32, 29, 26, 23, 20, 17, 14, 11, -3)} verwenden. Der letzte Parameter
     * {@code -3} steht hier f&uuml;r das Jahr AD 4 (Formel: {@code 1 - year}), das laut Matzat auch ein
     * Schaltjahr gewesen sein soll. Eine &Uuml;bersicht der verschiedenen Versionen und Vorschl&auml;ge
     * gibt es auf <a href="http://en.wikipedia.org/wiki/Julian_calendar">Wikipedia</a>. </p>
     *
     * @param   bcYears     positive numbers for BC-years
     * @return  new instance
     * @throws  IllegalArgumentException if given years are missing or out of range {@code BC 45 <= bcYear < AD 8}
     * @since   3.8/4.11
     */
    public static AncientJulianLeapYears of(int... bcYears) {

        if (Arrays.equals(bcYears, SEQUENCE_SCALIGER)){
            return SCALIGER;
        }

        return new AncientJulianLeapYears(bcYears);

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof AncientJulianLeapYears) {
            AncientJulianLeapYears that = (AncientJulianLeapYears) obj;
            return this.leaps == that.leaps;
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return Arrays.hashCode(this.leaps);

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < this.leaps.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            int bcYear = 1 - this.leaps[i];
            if (bcYear > 0) {
                sb.append("BC ");
                sb.append(bcYear);
            } else {
                sb.append("AD ");
                sb.append(this.leaps[i]);
            }
        }

        return sb.toString();

    }

    /**
     * <p>Returns the leap years in ascending order (as extended years). </p>
     *
     * @return  leap years in ascending order (usually negative numbers)
     */
    int[] getPattern() {

        return this.leaps;

    }

    /**
     * <p>Creates a suitable calculation engine based on {@code CalendarAlgorithm.JULIAN}. </p>
     *
     * @return  Calculus
     */
    Calculus getCalculus() {

        return this.calculus;

    }

}
