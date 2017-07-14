/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FrenchRepublicanAlgorithm.java) is part of project Time4J.
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

package net.time4j.calendar.frenchrev;

import net.time4j.CalendarUnit;
import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;
import net.time4j.base.MathUtils;
import net.time4j.calendar.astro.AstronomicalSeason;
import net.time4j.calendar.astro.SolarTime;
import net.time4j.engine.AttributeKey;
import net.time4j.engine.EpochDays;
import net.time4j.format.Attributes;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.ZonalOffset;


/**
 * Various calendar algorithm variants for the French revolutionary calendar.
 *
 * @author  Meno Hochschild
 * @since   3.33/4.28
 */
/*[deutsch]
 * Verschiedene Kalenderalgorithmen f&uuml;r den franz&ouml;sischen Revolutionskalender.
 *
 * @author  Meno Hochschild
 * @since   3.33/4.28
 */
public enum FrenchRepublicanAlgorithm {

    //~ Statische Felder/Initialisierungen ------------------------------------

    /**
     * The standard legal algorithm of the French revolutionary calendar strictly based on autumnal equinox.
     */
    /*[deutsch]
     * Das gesetzliche Standardberechnungsverfahren f&uuml;r den franz&ouml;sischen Revolutionskalender,
     * das streng auf dem astronomischen Herbstanfang beruht.
     */
    EQUINOX() {
        @Override
        public boolean isLeapYear(int fyear) {
            if (fyear < 1 || fyear > FrenchRepublicanCalendar.MAX_YEAR) {
                throw new IllegalArgumentException("Out of range: " + fyear);
            }
            long thisYear = autumnalEquinox(fyear).getDaysSinceEpochUTC();
            long nextYear = autumnalEquinox(fyear + 1).getDaysSinceEpochUTC();
            return ((nextYear - thisYear) == 366L);
        }
        @Override
        FrenchRepublicanCalendar transform(long utcDays) {
            check(utcDays);
            PlainDate date = PlainDate.of(utcDays, EpochDays.UTC);
            int fyear = date.getYear() - 1791;
            if (date.getMonth() < 9) {
                fyear--; // optimization
            }
            PlainDate equinox = autumnalEquinox(fyear);
            long delta = CalendarUnit.DAYS.between(equinox, date);
            while (delta < 0) {
                fyear--;
                equinox = autumnalEquinox(fyear);
                delta = CalendarUnit.DAYS.between(equinox, date);
            }
            int fdoy = (int) (delta + 1);
            return new FrenchRepublicanCalendar(fyear, fdoy);
        }
        @Override
        long transform(FrenchRepublicanCalendar cal) {
            long newYear = autumnalEquinox(cal.getYear()).getDaysSinceEpochUTC();
            return newYear + cal.getDayOfYear() - 1;
        }
        private PlainDate autumnalEquinox(int fyear) {
            PlainTimestamp tsp =
                AstronomicalSeason.AUTUMNAL_EQUINOX
                    .inYear(fyear + 1791)
                    .get(SolarTime.apparentAt(PARIS_OBSERVATORY));
            return tsp.getCalendarDate();
        }
    },

    /**
     * <p>This algorithmic variant proposed by Charles-Gilbert Romme (leader of the calendar commission)
     * would have treated the republican year as leap year similar to the gregorian calendar rules. </p>
     *
     * <p>Years divisible by four, but leaving out centuries unless divisible by 400 would have been
     * considered as leap years, that is: 16, 20, 24, ..., 96, 104, etc. However, this proposal was never
     * realized because Romme had soon be sent to the guillotine. </p>
     *
     * <p><strong>Important:</strong> This algorithm still applies the equinox rule for all dates before 1806-01-01,
     * the date of the abolition of the French revolutionary calendar. </p>
     */
    /*[deutsch]
     * <p>Diese algorithmische Variante, die einst vom Vorsitzenden der Kalenderkommission Charles-Gilbert Romme
     * vorgeschlagen wurde, h&auml;tte ein republikanisches Jahr als Schaltjahr &auml;hnlich wie in den
     * gregorianischen Kalenderregeln angesehen. </p>
     *
     * <p>Jahre, die durch 4 teilbar, aber keine vollen Jahrhunderte darstellen, es sei denn,
     * sie sind durch 400 teilbar, w&auml;ren dann als Schaltjahre behandelt worden, zum Beispiel:
     * 16, 20, 24, ..., 96, 104, usw. Allerdings wurde dieser Vorschlag nie realisiert, weil Romme bald
     * danach unter die Guillotine kam. </p>
     *
     * <p><strong>Wichtig:</strong> Dieser Algorithmus wendet immer noch die astronomische Herbstanfang-Regel an,
     * wenn das Datum vor 1806-01-01 liegt, dem Datum der Aufhebung des franz&ouml;sischen Revolutionskalenders. </p>
     */
    ROMME() {
        @Override
        public boolean isLeapYear(int fyear) {
            if (fyear < 1 || fyear > FrenchRepublicanCalendar.MAX_YEAR) {
                throw new IllegalArgumentException("Out of range: " + fyear);
            } else if (fyear == 3 || fyear == 7 || fyear == 11) {
                return true;
            } else if (fyear >= 15) { // year 4000 is not considered due to range restriction (1-1202)
                return ((fyear & 3) == 0) && (((fyear % 100) != 0) || ((fyear % 400) == 0));
            } else {
                return false;
            }
        }
        @Override
        FrenchRepublicanCalendar transform(long utcDays) {
            if (utcDays < ABOLITION) {
                return EQUINOX.transform(utcDays);
            } else {
                check(utcDays);
                int y = (int) (MathUtils.floorDivide((utcDays - EPOCH + 2) * 4000, 1460969) + 1);
                long newYear = this.transform(new FrenchRepublicanCalendar(y, 1));
                if (newYear > utcDays) {
                    newYear = this.transform(new FrenchRepublicanCalendar(y - 1, 1));
                    y--;
                }
                int doy = (int) (utcDays - newYear + 1);
                return new FrenchRepublicanCalendar(y, doy);
            }
        }
        @Override
        long transform(FrenchRepublicanCalendar date) {
            if (date.getYear() < 15) {
                return EQUINOX.transform(date);
            } else {
                int y = date.getYear() - 1;
                return
                    EPOCH - 1
                        + 365 * y
                        + MathUtils.floorDivide(y, 4)
                        - MathUtils.floorDivide(y, 100)
                        + MathUtils.floorDivide(y, 400)
                        + date.getDayOfYear();
            }
        }
    };

    private static final ZonalOffset PARIS_OBSERVATORY =
        ZonalOffset.atLongitude(OffsetSign.AHEAD_OF_UTC, 2, 20, 14.025); // Paris meridian (Wikipedia)

    private static final long ABOLITION = PlainDate.of(1806, 1, 1).get(EpochDays.UTC); // XIV-04-11
    private static final long EPOCH = PlainDate.of(1792, 9, 22).get(EpochDays.UTC); // I-01-01

    private static final AttributeKey<FrenchRepublicanAlgorithm> ATTRIBUTE =
        Attributes.createKey("FRENCH_REPUBLICAN_ALGORITHM", FrenchRepublicanAlgorithm.class);

    //~ Methoden --------------------------------------------------------------

    /**
     * <p>Format attribute which helps to resolve algorithmic differences between various French republican dates. </p>
     *
     * <p>Standard value is: {@link FrenchRepublicanAlgorithm#EQUINOX}. </p>
     */
    /*[deutsch]
     * <p>Formatattribut, das hilft, m&ouml;gliche algorithmische Differenzen von verschiedenen
     * Datumsangaben des franz&ouml;sischen Revolutionskalenders aufzul&ouml;sen. </p>
     *
     * <p>Standardwert ist: {@link FrenchRepublicanAlgorithm#EQUINOX}. </p>
     */
    public static AttributeKey<FrenchRepublicanAlgorithm> attribute() {

        return ATTRIBUTE;

    }

    /**
     * <p>Determines if given republican year is a leap year or not. </p>
     *
     * @param   fyear   the year of French Republic in the French revolutionary calendar
     * @return  {@code true} for leap years else {@code false}
     */
    /*[deutsch]
     * <p>Bestimmt, ob das angegebene republikanische Jahr ein Schaltjahr ist. </p>
     *
     * @param   fyear   the year of French Republic in the French revolutionary calendar
     * @return  {@code true} for leap years else {@code false}
     */
    public boolean isLeapYear(int fyear) {
        throw new AbstractMethodError();
    }

    abstract FrenchRepublicanCalendar transform(long utcDays);

    abstract long transform(FrenchRepublicanCalendar date);

    private static void check(long utcDays) {

        if ((utcDays < -65478L) || (utcDays > 373542L)) {
            throw new IllegalArgumentException("Out of range: " + utcDays);
        }

    }

}
