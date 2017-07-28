/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (PersianAlgorithm.java) is part of project Time4J.
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

import net.time4j.CalendarUnit;
import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;
import net.time4j.base.MathUtils;
import net.time4j.calendar.astro.AstronomicalSeason;
import net.time4j.calendar.astro.SolarTime;
import net.time4j.engine.AttributeKey;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.EpochDays;
import net.time4j.format.Attributes;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.ZonalOffset;


/**
 * <p>Enumeration of different calculation methods for the Persian calendar. </p>
 *
 * @author  Meno Hochschild
 * @since   3.33/4.28
 */
/*[deutsch]
 * <p>Aufz&auml;hlung verschiedener Berechnungsmethoden f&uuml;r den persischen Kalender. </p>
 *
 * @author  Meno Hochschild
 * @since   3.33/4.28
 */
public enum PersianAlgorithm {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Based on the work of the Polish astronomer
     * <a href="http://www.astro.uni.torun.pl/~kb/Papers/EMP/PersianC-EMP.htm">Kazimierz Borkowski</a>. </p>
     *
     * <p>This is the default and uses a refinement of the Khayyam 33-year-rule based on astronomical
     * calculations for the Teheran mean time. However, users should keep following things in mind when
     * discussing the possible errors: </p>
     *
     * <ul>
     *     <li>Borkowski uses the offset of {@code +03:25:30} which deviates from the Iranian Standard Time
     *     {@code +03:30}. In addition, it is questionable if the historic ancestor of the Persian calendar
     *     has used any of these offsets in ancient times. </li>
     *     <li>The astronomical calculations are not published in detail but seem to deviate by several minutes
     *     in the far past or future compared with what we can derive from the work of the Belgian astronomer
     *     Jean Meeus. </li>
     *     <li>The delta-T-calculation is somehow simplistic compared with the more accurate formulas
     *     used in Time4J. </li>
     *     <li>The mean solar time is used. It is for debate if the Persian calendar is rather based
     *     on the apparent solar time. </li>
     * </ul>
     */
    /*[deutsch]
     * <p>Fu&szlig;t auf der Arbeit des polnischen Astronomen
     * <a href="http://www.astro.uni.torun.pl/~kb/Papers/EMP/PersianC-EMP.htm">Kazimierz Borkowski</a>. </p>
     *
     * <p>Diese Standardeinstellung verwendet eine Verfeinerung der Khayyam-33-Jahreszyklus-Regel,
     * indem astronomische Berechnungen f&uuml;r die mittlere Zeit von Teheran zugrundegelegt werden.
     * Zur Fehlerdiskussion gibt es allerdings folgende Punkte zu bedenken: </p>
     *
     * <ul>
     *     <li>Borkowski verwendet die Zeitverschiebung von {@code +03:25:30}, die von der Standardzeit
     *     des Iran abweicht {@code +03:30}. Au&szlig;erdem ist es fraglich, ob der historische Vorg&auml;nger
     *     des persischen Kalenders irgendeine solche Verschiebung in alten Zeiten benutzt hat. </li>
     *     <li>Die astronomischen Grundlagen sind von Borkowski nicht im Detail ver&ouml;ffentlicht worden,
     *     scheinen aber von dem, was wir aus der Arbeit des belgischen Astronomen Jean Meeus bekommen
     *     k&ouml;nnen, in der fernen Vergangenheit oder Zukunft um mehrere Minuten abzuweichen. </li>
     *     <li>Die Delta-T-Berechnung ist verglichen mit den genaueren Formeln, die in Time4J verwendet
     *     werden, besonders in der fernen Zukunft stark vereinfacht. </li>
     *     <li>Es wird die mittlere Ortszeit verwendet. Allerdings kann dar&uuml;ber debattiert werden,
     *     ob nicht die wahre Ortszeit herangezogen werden sollte. </li>
     * </ul>
     */
    BORKOWSKI() {
        @Override
        boolean isLeapYear(
            int pYear,
            ZonalOffset offset
        ) {
            checkYear(pYear);
            PersianCalendar nextYear = new PersianCalendar(pYear + 1, 1, 1);
            PersianCalendar thisYear = new PersianCalendar(pYear, 1, 1);
            return (this.transform(nextYear, offset) - this.transform(thisYear, offset) == 366L);
        }
        @Override
        PersianCalendar transform(
            long utcDays,
            ZonalOffset offset
        ) {
            PlainDate date = PlainDate.of(utcDays, EpochDays.UTC);
            int pyear = date.getYear() - 621;
            if (date.getMonth() < 3) {
                pyear--; // optimization
            }
            PlainDate equinox = vernalEquinox(pyear);
            long delta = CalendarUnit.DAYS.between(equinox, date);
            while (delta < 0) {
                pyear--;
                equinox = vernalEquinox(pyear);
                delta = CalendarUnit.DAYS.between(equinox, date);
            }
            int pmonth = 1;
            while (pmonth < 12) {
                int len = ((pmonth <= 6) ? 31 : 30);
                if (delta < len) {
                    break;
                } else {
                    delta -= len;
                    pmonth++;
                }
            }
            int pdom = (int) (delta + 1);
            return PersianCalendar.of(pyear, pmonth, pdom);
        }
        @Override
        long transform(
            PersianCalendar date,
            ZonalOffset offset
        ) {
            int pyear = date.getYear();
            int pmonth = date.getMonth().getValue();
            long utcDays = vernalEquinox(pyear).getDaysSinceEpochUTC();
            utcDays += ((pmonth) - 1) * 31 - ((pmonth / 7) * (pmonth - 7)) + date.getDayOfMonth() - 1;
            return utcDays;
        }
        private PlainDate vernalEquinox(int pyear) {
            int[] breaks =
                new int[] {
                    -61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181,
                    1210, 1635, 2060, 2097, 2192, 2262, 2324, 2394, 2456, 3178
                };
            int max = breaks[breaks.length - 1];
            if ((pyear < 1) || (pyear >= max)) {
                throw new IllegalArgumentException("Persian year out of range 1-" + max + ": " + pyear);
            }
            int gyear = pyear + 621;
            int leapP = -14;
            int previousY = breaks[0];
            int delta = 0;
            for (int i = 1; i < breaks.length; i++) {
                int currentY = breaks[i];
                delta = currentY - previousY;
                if (pyear < currentY) {
                    break;
                }
                leapP += ((delta / 33) * 8 + (delta % 33) / 4);
                previousY = currentY;
            }
            int n = pyear - previousY;
            leapP += ((n / 33) * 8 + ((n % 33) + 3) / 4);
            if (((delta % 33) == 4) && (delta - n == 4)) {
                leapP++;
            }
            int leapG = gyear / 4 - ((gyear / 100 + 1) * 3) / 4 - 150;
            int marchDay = 20 + leapP - leapG;
            return PlainDate.of(gyear, 3, marchDay);
        }
    },

    /**
     * <p>Uses a 33-year-cycle to determine a simple leap year rule (discovered by Omar Khayyam). </p>
     *
     * <p>If the remainder of the division of persian year by 33 is one of following numbers then and only then
     * it is a leap year: 1, 5, 9, 13, 17, 22, 26, 30. The rule is correct for the Persian years 1178-1633
     * (gregorian: 1799-2254). </p>
     */
    /*[deutsch]
     * <p>Verwendet einen 33-Jahreszyklus, um ein Schaltjahr zu bestimmen (entdeckt von Omar Khayyam). </p>
     *
     * <p>Wenn der Rest der Division eines persischen Jahres durch 33 genau eine der folgenden Zahlen ist,
     * handelt es sich um ein Schaltjahr: 1, 5, 9, 13, 17, 22, 26, 30. Die Regel funktioniert f&uuml;r
     * die persischen Jahre 1178-1633 (gregorianisch: 1799-2254). </p>
     */
    KHAYYAM() {
        @Override
        boolean isLeapYear(
            int pYear,
            ZonalOffset offset
        ) {
            checkYear(pYear);
            int m = pYear % 33;
            return (m == 1 || m == 5 || m == 9 || m == 13 || m == 17 || m == 22 || m == 26 || m == 30);
        }
        @Override
        PersianCalendar transform(
            long utcDays,
            ZonalOffset offset
        ) {
            checkRange(utcDays);
            long delta = (utcDays + REFERENCE_ZERO_KHAYYAM);
            int cycles = (int) (delta / (LENGTH_OF_KHAYYAM_CYCLE));
            int remainder = (int) (delta % (LENGTH_OF_KHAYYAM_CYCLE));
            int pYear = cycles * 33;
            int pMonth = 1;
            int pDay = 1;
            for (int j = 0; j < 33; j++) {
                int len = (
                    (j == 1 || j == 5 || j == 9 || j == 13 || j == 17 || j == 22 || j == 26 || j == 30)
                        ? 366
                        : 365);
                if (remainder >= len) {
                    remainder -= len;
                    pYear++;
                } else {
                    break;
                }
            }
            for (int m = 1; m < 12; m++) {
                int len = (m <= 6 ? 31 : 30);
                if (remainder >= len) {
                    remainder -= len;
                    pMonth++;
                } else {
                    break;
                }
            }
            pDay += remainder;
            return new PersianCalendar(pYear, pMonth, pDay);
        }
        @Override
        long transform(
            PersianCalendar date,
            ZonalOffset offset
        ) {
            int pYear = date.getYear();
            long utcDays = ((pYear / 33) * LENGTH_OF_KHAYYAM_CYCLE) - REFERENCE_ZERO_KHAYYAM;
            int yearOfCycle = pYear % 33;
            for (int j = 0; j < yearOfCycle; j++) {
                int len = (
                    (j == 1 || j == 5 || j == 9 || j == 13 || j == 17 || j == 22 || j == 26 || j == 30)
                        ? 366
                        : 365);
                utcDays += len;
            }
            int m = date.getMonth().getValue();
            if (m <= 7) {
                utcDays += (31 * (m - 1));
            } else {
                utcDays += (30 * (m - 1) + 6);
            }
            return utcDays + date.getDayOfMonth() - 1;
        }
    },

    /**
     * <p>A popular proposal of Ahmad Birashk which uses a complex system of cycles and grand cycles. </p>
     *
     * <p>It works for most years but sometimes it is not in agreement with the astronomical nature of
     * the Persian calendar. For example, the year 2025 (gregorian) is expected to break this rule. </p>
     */
    /*[deutsch]
     * <p>Ein popul&auml;rer Vorschlag von Ahmad Birashk, die ein kompliziertes System von Zyklen und
     * &uuml;bergeordneten Gro&szlig;zyklen verwendet. </p>
     *
     * <p>Die Regel funktioniert f&uuml;r die meisten Jahre, aber manchmal ist sie nicht deckungsgleich
     * mit der astronomischen Natur des persischen Kalenders. Zum Beispiel wird das Jahr 2025
     * (gregorianisch) diese Regel brechen. </p>
     */
    BIRASHK() {
        @Override
        boolean isLeapYear(
            int pYear,
            ZonalOffset offset
        ) {
            checkYear(pYear);
            return (MathUtils.floorModulo(((MathUtils.floorModulo(pYear - 474, 2820) + 512) * 31), 128) < 31);
        }
        @Override
        PersianCalendar transform(
            long utcDays,
            ZonalOffset offset
        ) {
            checkRange(utcDays);
            int d0 = (int) (utcDays - START_OF_BIRASHK_CYCLE);
            int n2820 = MathUtils.floorDivide(d0, 1029983);
            int d1 = MathUtils.floorModulo(d0, 1029983);
            int y2820 = (
                (d1 == 1029982)
                ? 2820
                : MathUtils.floorDivide(128 * d1 + 46878, 46751));
            int pYear = 474 + 2820 * n2820 + y2820;
            int pMonth = 1;
            int pDay = 1;
            int remainder = (int) (utcDays - this.transform(new PersianCalendar(pYear, 1, 1), offset));
            for (int m = 1; m < 12; m++) {
                int len = (m <= 6 ? 31 : 30);
                if (remainder >= len) {
                    remainder -= len;
                    pMonth++;
                } else {
                    break;
                }
            }
            pDay += remainder;
            return new PersianCalendar(pYear, pMonth, pDay);
        }
        @Override
        long transform(
            PersianCalendar date,
            ZonalOffset offset
        ) {
            int y = date.getYear() - 474;
            int yy = MathUtils.floorModulo(y, 2820) + 474;
            long utcDays = -492998; // persian epoch;
            utcDays += (1029983 * MathUtils.floorDivide(y, 2820));
            utcDays += (365 * (yy - 1));
            utcDays += (MathUtils.floorDivide(31 * yy - 5, 128));
            int m = date.getMonth().getValue();
            if (m <= 7) {
                utcDays += (31 * (m - 1));
            } else {
                utcDays += (30 * (m - 1) + 6);
            }
            return utcDays + date.getDayOfMonth();
        }
    },

    /**
     * <p>Based on the astronomical calculation of vernal equinox as start of Persian year
     * usually in the Iranian Standard time. </p>
     *
     * <p>The implementation works as follows: </p>
     *
     * <ul>
     *     <li>The vernal equinox is evaluated by {@link net.time4j.calendar.astro.AstronomicalSeason#inYear(int)}.</li>
     *     <li>The time zone offset is chosen as {@code UTC+03:30} if not specified otherwise. </li>
     *     <li>If the apparent vernal equinox is after noon then the next day is the start of Persian year. </li>
     *     <li>The maximum supported Persian year is 2378. </li>
     * </ul>
     */
    /*[deutsch]
     * <p>Fu&szlig;t auf der astronomischen Berechnung des Fr&uuml;hlingsbeginns als Start des persischen Jahres
     * normalerweise in der iranischen Standardzeit. </p>
     *
     * <p>Die Implementierung arbeitet wie folgt: </p>
     *
     * <ul>
     *     <li>Der Fr&uuml;hlingsbeginn wird mit Hilfe von
     *     {@link net.time4j.calendar.astro.AstronomicalSeason#inYear(int)} berechnet.</li>
     *     <li>Die Zeitzonenverschiebung wird als {@code UTC+03:30} angenommen, wenn nicht anders angegeben. </li>
     *     <li>Wenn der Fr&uuml;hlingsbeginn (wahre Ortszeit) nach dem Mittag liegt, wird der n&auml;chste
     *     Tag der Start des persischen Jahres sein. </li>
     *     <li>Das maximal unterst&uuml;tzte persische Jahr ist 2378. </li>
     * </ul>
     */
    ASTRONOMICAL() {
        @Override
        int getMaxPersianYear() {
            return 3000 - 622;
        }
        @Override
        boolean isLeapYear(
            int pYear,
            ZonalOffset offset
        ) {
            if (pYear < 1 || pYear > this.getMaxPersianYear()) {
                throw new IllegalArgumentException("Out of range: " + pYear);
            }
            PersianCalendar nextYear = new PersianCalendar(pYear + 1, 1, 1);
            PersianCalendar thisYear = new PersianCalendar(pYear, 1, 1);
            return (this.transform(nextYear, offset) - this.transform(thisYear, offset) == 366L);
        }
        @Override
        PersianCalendar transform(
            long utcDays,
            ZonalOffset offset
        ) {
            if ((utcDays < -492997L) || (utcDays > 375548L)) {
                throw new IllegalArgumentException("Out of range: " + utcDays);
            }
            PlainDate date = PlainDate.of(utcDays, EpochDays.UTC);
            int pyear = date.getYear() - 621;
            if (date.getMonth() < 3) {
                pyear--; // optimization
            }
            PlainDate equinox = vernalEquinox(pyear, offset);
            long delta = CalendarUnit.DAYS.between(equinox, date);
            while (delta < 0) {
                pyear--;
                equinox = vernalEquinox(pyear, offset);
                delta = CalendarUnit.DAYS.between(equinox, date);
            }
            int pmonth = 1;
            while (pmonth < 12) {
                int len = ((pmonth <= 6) ? 31 : 30);
                if (delta < len) {
                    break;
                } else {
                    delta -= len;
                    pmonth++;
                }
            }
            int pdom = (int) (delta + 1);
            return new PersianCalendar(pyear, pmonth, pdom);
        }
        @Override
        long transform(
            PersianCalendar date,
            ZonalOffset offset
        ) {
            int pyear = date.getYear();
            int pmonth = date.getMonth().getValue();
            long utcDays = vernalEquinox(pyear, offset).getDaysSinceEpochUTC();
            utcDays += ((pmonth) - 1) * 31 - ((pmonth / 7) * (pmonth - 7)) + date.getDayOfMonth() - 1;
            return utcDays;
        }
        private PlainDate vernalEquinox(
            int pyear,
            ZonalOffset offset
        ) {
            PlainTimestamp tsp =
                AstronomicalSeason.VERNAL_EQUINOX.inYear(pyear + 621).get(SolarTime.apparentAt(offset));
            if (tsp.getHour() >= 12) {
                return tsp.getCalendarDate().plus(1, CalendarUnit.DAYS);
            } else {
                return tsp.getCalendarDate();
            }
        }
    };

    static final ZonalOffset STD_OFFSET = ZonalOffset.ofHoursMinutes(OffsetSign.AHEAD_OF_UTC, 3, 30);

    private static final int LENGTH_OF_KHAYYAM_CYCLE = 365 * 33 + 8;
    private static final long REFERENCE_ZERO_KHAYYAM = 493363L;
    private static final long START_OF_BIRASHK_CYCLE = -319872L;

    private static final AttributeKey<PersianAlgorithm> ATTRIBUTE =
        Attributes.createKey("PERSIAN_ALGORITHM", PersianAlgorithm.class);

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Format attribute which helps to resolve algorithmic differences between various Persian dates. </p>
     *
     * <p>Following example demonstrates the difference between the standard algorithm and the algorithm
     * proposed by A. Birashk: </p>
     *
     * <pre>
     *      PersianCalendar pcal = PersianCalendar.of(1403, 12, 30); // = 2025-03-20 (gregorian)
     *      ChronoFormatter&lt;PersianCalendar&gt; f =
     *          ChronoFormatter
     *              .ofPattern(&quot;dd.MM.yyyy G&quot;, PatternType.CLDR, Locale.ROOT, PersianCalendar.axis())
     *              .with(PersianAlgorithm.attribute(), PersianAlgorithm.BIRASHK);
     *      assertThat(f.parse(&quot;01.01.1404 AP&quot;), is(pcal));
     *      assertThat(f.format(pcal), is(&quot;01.01.1404 AP&quot;));
     * </pre>
     *
     * <p>Standard value is: {@link PersianAlgorithm#BORKOWSKI}. It is also possible to use the
     * {@link #ASTRONOMICAL astronomical algorithm} in combination with an arbitrary timezone offset
     * by setting the format attribute {@link Attributes#TIMEZONE_ID} to any {@code ZonalOffset}. </p>
     *
     * @return  format attribute key
     */
    /*[deutsch]
     * <p>Formatattribut, das hilft, m&ouml;gliche algorithmische Differenzen von verschiedenen
     * persischen Datumsangaben aufzul&ouml;sen. </p>
     *
     * <p>Das folgende Beispiel zeigt den Unterschied zwischen dem Standardalgorithmus und dem
     * von A. Birashk vorgeschlagenen Algorithmus: </p>
     *
     * <pre>
     *      PersianCalendar pcal = PersianCalendar.of(1403, 12, 30); // = 2025-03-20 (gregorian)
     *      ChronoFormatter&lt;PersianCalendar&gt; f =
     *          ChronoFormatter
     *              .ofPattern(&quot;dd.MM.yyyy G&quot;, PatternType.CLDR, Locale.ROOT, PersianCalendar.axis())
     *              .with(PersianAlgorithm.attribute(), PersianAlgorithm.BIRASHK);
     *      assertThat(f.parse(&quot;01.01.1404 AP&quot;), is(pcal));
     *      assertThat(f.format(pcal), is(&quot;01.01.1404 AP&quot;));
     * </pre>
     *
     * <p>Standardwert ist: {@link PersianAlgorithm#BORKOWSKI}. Es ist auch m&ouml;glich, den
     * {@link #ASTRONOMICAL astronomischen Algorithmus} in Verbindung mit einer beliebigen
     * Zeitzonenverschiebung zu nutzen, indem das Formatattribut {@link Attributes#TIMEZONE_ID}
     * auf irgendeinen {@code ZonalOffset} gesetzt wird. </p>
     *
     * @return  format attribute key
     */
    public static AttributeKey<PersianAlgorithm> attribute() {

        return ATTRIBUTE;

    }

    /**
     * <p>Determines if given persian year is a leap year. </p>
     *
     * @param   persianYear     the persian year to be queried
     * @return  {@code true} if the year is a leap year else {@code false}
     * @throws  IllegalArgumentException if the year is out of range 1-3000
     */
    /*[deutsch]
     * <p>Bestimmt, ob das angegebene persische Jahr ein Schaltjahr ist. </p>
     *
     * @param   persianYear     the persian year to be queried
     * @return  {@code true} if the year is a leap year else {@code false}
     * @throws  IllegalArgumentException if the year is out of range 1-3000
     */
    public boolean isLeapYear(int persianYear) {

        return this.isLeapYear(persianYear, STD_OFFSET);

    }

    // must be kept private because we don't allow PersianCalendar to have multiple timelines!!!
    abstract boolean isLeapYear(
        int persianYear,
        ZonalOffset offset
    );

    // must be kept private because we don't allow PersianCalendar to have multiple timelines!!!
    abstract PersianCalendar transform(
        long utcDays,
        ZonalOffset offset
    );

    // must be kept private because we don't allow PersianCalendar to have multiple timelines!!!
    abstract long transform(
        PersianCalendar date,
        ZonalOffset offset
    );

    // default value: 3000
    int getMaxPersianYear() {

        return 3000;

    }

    // validation
    boolean isValid(
        int pYear,
        int pMonth,
        int pDayOfMonth,
        ZonalOffset offset
    ) {

        if (pYear < 1 || pYear > this.getMaxPersianYear() || pMonth < 1 || pMonth > 12 || pDayOfMonth < 1) {
            return false;
        } else if (pMonth <= 6) {
            return pDayOfMonth <= 31;
        } else if (pMonth <= 11) {
            return pDayOfMonth <= 30;
        } else {
            return pDayOfMonth <= (this.isLeapYear(pYear, offset) ? 30 : 29);
        }

    }

    private static void checkRange(long utcDays) {

        CalendarSystem<PersianCalendar> calsys = PersianCalendar.axis().getCalendarSystem();

        if ((utcDays < calsys.getMinimumSinceUTC() || utcDays > calsys.getMaximumSinceUTC())) {
            throw new IllegalArgumentException("Out of range: " + utcDays);
        }

    }

    private static void checkYear(int persianYear) {

        if (persianYear < 1 || persianYear > 3000) {
            throw new IllegalArgumentException("Out of range: " + persianYear);
        }

    }

}
