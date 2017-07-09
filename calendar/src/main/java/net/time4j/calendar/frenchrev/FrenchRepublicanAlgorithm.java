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
import net.time4j.calendar.astro.AstronomicalSeason;
import net.time4j.calendar.astro.SolarTime;
import net.time4j.engine.EpochDays;
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
     * The standard legal algorithm of the French revolutionary calendar.
     */
    /*[deutsch]
     * Das gesetzliche Standardberechnungsverfahren f&uuml;r den franz&ouml;sischen Revolutionskalender.
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
            if ((utcDays < -492997L) || (utcDays > 375548L)) {
                throw new IllegalArgumentException("Out of range: " + utcDays);
            }
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
    };

    private static final ZonalOffset PARIS_OBSERVATORY =
        ZonalOffset.atLongitude(OffsetSign.AHEAD_OF_UTC, 2, 20, 14.025); // Paris meridian (Wikipedia)

    //~ Methoden --------------------------------------------------------------

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

}
