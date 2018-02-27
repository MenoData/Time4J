/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (EastAsianCS.java) is part of project Time4J.
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

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.base.MathUtils;
import net.time4j.calendar.astro.AstronomicalSeason;
import net.time4j.calendar.astro.JulianDay;
import net.time4j.calendar.astro.MoonPhase;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.EpochDays;
import net.time4j.tz.ZonalOffset;


/**
 * The heart of Chinese calendar.
 *
 * @author  Meno Hochschild
 * @since   3.40/4.35
 */
abstract class EastAsianCS<D extends EastAsianCalendar<?, D>>
    implements CalendarSystem<D> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long CALENDAR_REFORM_1645 = PlainDate.of(1645, 1, 28).getDaysSinceEpochUTC();
    private static final long MAX_LIMIT = PlainDate.of(3000, 1, 27).getDaysSinceEpochUTC();

    private static final long EPOCH_CHINESE = PlainDate.of(-2636, 2, 15).getDaysSinceEpochUTC();

    static final double MEAN_SYNODIC_MONTH = 29.530588861;
    static final double MEAN_TROPICAL_YEAR = 365.242189;

    //~ Methoden ----------------------------------------------------------

    @Override
    public final D transform(long utcDays) {

        long s1 = this.winterOnOrBefore(utcDays);
        long s2 = this.winterOnOrBefore(s1 + 370);
        long m12 = this.newMoonOnOrAfter(s1 + 1);
        long nextM11 = this.newMoonBefore(s2 + 1);
        long m = this.newMoonBefore(utcDays + 1);
        boolean leapYearInSui = (lunations(m12, nextM11) == 12);

        long me = lunations(m12, m); // might be negative

        if (leapYearInSui && this.hasLeapMonth(m12, m)) {
            me--;
        }

        int month = MathUtils.floorModulo(me, 12);

        if (month == 0) {
            month = 12;
        }

        long elapsedYears =
            (long) Math.floor(1.5 - (month / 12.0) + ((utcDays - EPOCH_CHINESE) / MEAN_TROPICAL_YEAR));

        int cycle = (int) MathUtils.floorDivide(elapsedYears - 1, 60) + 1;
        int yearOfCycle = MathUtils.floorModulo(elapsedYears, 60);

        if (yearOfCycle == 0) {
            yearOfCycle = 60;
        }

        int dayOfMonth = (int) (utcDays - m + 1);
        EastAsianMonth eam = EastAsianMonth.valueOf(month);

        if (leapYearInSui && this.hasNoMajorSolarTerm(m) && !this.hasLeapMonth(m12, this.newMoonBefore(m))) {
            eam = eam.withLeap();
        }

        return this.create(cycle, yearOfCycle, eam, dayOfMonth, utcDays);

    }

    @Override
    public final long transform(D date) {

        return this.transform(date.getCycle(), date.getYear().getNumber(), date.getMonth(), date.getDayOfMonth());

    }

    @Override
    public long getMinimumSinceUTC() {

        return CALENDAR_REFORM_1645;

    }

    @Override
    public final long getMaximumSinceUTC() {

        return MAX_LIMIT;

    }

    abstract D create(
        int cycle,
        int yearOfCycle,
        EastAsianMonth eam,
        int dayOfMonth,
        long utcDays
    );

    abstract ZonalOffset getOffset(long utcDays);

    abstract int[] getLeapMonths();

    // number of leap month or zero if no leap year
    final int getLeapMonth(
        int cycle,
        int yearOfCycle
    ) {
        int[] leapMonths = this.getLeapMonths();
        int elapsedYears = (cycle - 1) * 60 + yearOfCycle - 1;
        int index = 2 * ((elapsedYears - leapMonths[0]) / 3); // first lower bound estimation
        int lm = 0;

        while ((index < leapMonths.length)) {
            int test = leapMonths[index];

            if (test < elapsedYears) {
                index += Math.max(2 * ((elapsedYears - test) / 3), 2);
            } else if (test > elapsedYears) {
                break;
            } else { // test == elapsedYears
                lm = leapMonths[index + 1];
                break;
            }
        }

        return lm;
    }

    // result in utc-days
    final long transform(
        int cycle,
        int yearOfCycle,
        EastAsianMonth month,
        int dayOfMonth
    ) {
        if (!this.isValid(cycle, yearOfCycle, month, dayOfMonth)) {
            throw new IllegalArgumentException("Invalid date.");
        }

        return this.firstDayOfMonth(cycle, yearOfCycle, month) + dayOfMonth - 1;
    }

    // true if valid else false
    boolean isValid(
        int cycle,
        int yearOfCycle,
        EastAsianMonth month,
        int dayOfMonth
    ) {
        if (
            (cycle < 72) || (cycle > 94)
            || (yearOfCycle < 1) || (yearOfCycle > 60)
            || ((cycle == 72) && (yearOfCycle < 22))
            || ((cycle == 94) && (yearOfCycle > 56))
            || (dayOfMonth < 1) || (dayOfMonth > 30)
            || (month == null)
            || (month.isLeap() && (month.getNumber() != this.getLeapMonth(cycle, yearOfCycle)))
        ) {
            return false;
        } else if (dayOfMonth == 30) { // the only case when astronomical validation is required
            long monthStart = this.firstDayOfMonth(cycle, yearOfCycle, month);
            long nextNewMoon = this.newMoonOnOrAfter(monthStart + 1);
            return (nextNewMoon - monthStart == 30);
        }

        return true;
    }

    // result in utc-days
    final long newYear(
        int cycle,
        int yearOfCycle
    ) {
        long midYear =
            (long) Math.floor(EPOCH_CHINESE + ((cycle - 1) * 60 + yearOfCycle - 0.5) * MEAN_TROPICAL_YEAR);
        return this.newYearOnOrBefore(midYear); // starts with new moon on or after winter solstice
    }

    // index of major solar term (used in test cases only)
    final int getMajorSolarTerm(long utcDays) {
        double jd = JulianDay.ofEphemerisTime(this.midnight(utcDays)).getValue();
        int index = (2 + (int) Math.floor(SolarTerm.solarLongitude(jd) / 30)) % 12;
        return ((index == 0) ? 12 : index);
    }

    // leap months have no major solar terms
    final boolean hasNoMajorSolarTerm(long utcDays) {
        double jd0 = JulianDay.ofEphemerisTime(this.midnight(utcDays)).getValue();
        int index0 = (2 + (int) Math.floor(SolarTerm.solarLongitude(jd0) / 30)) % 12;
        double jd1 = JulianDay.ofEphemerisTime(this.midnight(this.newMoonOnOrAfter(utcDays + 1))).getValue();
        int index1 = (2 + (int) Math.floor(SolarTerm.solarLongitude(jd1) / 30)) % 12;
        return (index0 == index1);
    }

    // result in utc-days
    final long newMoonOnOrAfter(long utcDays) {
        Moment m = MoonPhase.NEW_MOON.atOrAfter(this.midnight(utcDays));
        return m.toZonalTimestamp(this.getOffset(utcDays)).toDate().getDaysSinceEpochUTC();
    }

    // local midnight
    Moment midnight(long utcDays) {
        return PlainDate.of(utcDays, EpochDays.UTC).atStartOfDay().at(this.getOffset(utcDays));
    }

    // result in utc-days
    private long newMoonBefore(long utcDays) {
        Moment m = MoonPhase.NEW_MOON.before(this.midnight(utcDays));
        return m.toZonalTimestamp(this.getOffset(utcDays)).toDate().getDaysSinceEpochUTC();
    }

    // count of lunations between m1 and m2
    private static long lunations(long m1, long m2) {
        return Math.round((m2 - m1) / MEAN_SYNODIC_MONTH);
    }

    // a sui is the period from one winter to next winter ensuring that winter solstice is always in 11th month
    private long newYearInSui(long utcDays) {
        long s1 = this.winterOnOrBefore(utcDays);
        long s2 = this.winterOnOrBefore(s1 + 370);
        long m12 = this.newMoonOnOrAfter(s1 + 1);
        long m13 = this.newMoonOnOrAfter(m12 + 1);
        long nextM11 = this.newMoonBefore(s2 + 1);

        if ((lunations(m12, nextM11) == 12) && (this.hasNoMajorSolarTerm(m12) || this.hasNoMajorSolarTerm(m13))) {
            return this.newMoonOnOrAfter(m13 + 1);
        } else {
            return m13;
        }
    }

    // start of lunisolar year
    private long newYearOnOrBefore(long utcDays) {
        long ny = this.newYearInSui(utcDays);
        if (utcDays >= ny) {
            return ny;
        } else {
            return this.newYearInSui(utcDays - 180); // previous sui
        }
    }

    // is there any leap month between m0 and m?
    private boolean hasLeapMonth(long m0, long m) {
        return (
            (m >= m0)
            && (this.hasNoMajorSolarTerm(m) || this.hasLeapMonth(m0, this.newMoonBefore(m)))
        );
    }

    // result in utc-days
    private long firstDayOfMonth(
        int cycle,
        int yearOfCycle,
        EastAsianMonth month
    ) {
        long newYear = this.newYear(cycle, yearOfCycle);
        long approxStartOfMonth = this.newMoonOnOrAfter(newYear + (month.getNumber() - 1) * 29);

        if (month.equals(this.transform(approxStartOfMonth).getMonth())) {
            return approxStartOfMonth;
        } else {
            return this.newMoonOnOrAfter(approxStartOfMonth + 1);
        }
    }

    // search for winter solstice
    private long winterOnOrBefore(long utcDays) {
        ZonalOffset offset = this.getOffset(utcDays);
        PlainDate date = PlainDate.of(utcDays, EpochDays.UTC);
        int year = (((date.getMonth() <= 11) || (date.getDayOfMonth() <= 15)) ? date.getYear() - 1 : date.getYear());
        Moment winter = AstronomicalSeason.WINTER_SOLSTICE.inYear(year);
        PlainDate d = winter.toZonalTimestamp(offset).getCalendarDate();
        if (d.isAfter(date)) {
            winter = AstronomicalSeason.WINTER_SOLSTICE.inYear(year - 1);
            d = winter.toZonalTimestamp(offset).getCalendarDate();
        }
        return d.getDaysSinceEpochUTC();
    }

}
