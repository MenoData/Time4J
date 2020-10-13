package net.time4j.calendar.hindu;

import net.time4j.GeneralTimestamp;
import net.time4j.PlainTime;
import net.time4j.Weekday;
import net.time4j.calendar.CommonElements;
import net.time4j.calendar.IndianMonth;
import net.time4j.engine.CalendarDays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;


@RunWith(JUnit4.class)
public class HinduElementTest {

    @Test
    public void eraElement() {
        HinduCalendar cal = HinduCalendar.ofOldSolar(3101, HinduMonth.of(IndianMonth.MAGHA).getRasi(), 19);
        assertThat(
            cal.getEra(),
            is(HinduEra.KALI_YUGA));
        assertThat(
            cal.get(HinduCalendar.ERA),
            is(HinduEra.KALI_YUGA));
        assertThat(
            cal.getMinimum(HinduCalendar.ERA),
            is(HinduEra.KALI_YUGA));
        assertThat(
            cal.getMaximum(HinduCalendar.ERA),
            is(HinduEra.KALI_YUGA));
        assertThat(
            cal.isValid(HinduCalendar.ERA, HinduEra.KALI_YUGA),
            is(true));
        assertThat(
            cal.with(HinduCalendar.ERA, HinduEra.KALI_YUGA),
            is(cal));
        assertThat(
            cal.isValid(HinduCalendar.ERA, null),
            is(false));
        assertThat(
            cal.isValid(HinduCalendar.ERA, HinduEra.SAKA),
            is(false));
        try {
            cal.with(HinduCalendar.ERA, HinduEra.SAKA);
            fail("Saka not allowed for old Hindu calendar.");
        } catch (IllegalArgumentException iae) {
            // ok
        }
        assertThat(HinduCalendar.ERA.name(), is("ERA"));
        assertThat(HinduCalendar.ERA.getSymbol(), is('G'));
        assertThat(HinduCalendar.ERA.isDateElement(), is(true));
        assertThat(HinduCalendar.ERA.isTimeElement(), is(false));
        assertThat(HinduCalendar.ERA.isLenient(), is(false));
        assertThat(HinduCalendar.ERA.getDefaultMinimum(), is(HinduEra.KALI_YUGA));
        assertThat(HinduCalendar.ERA.getDefaultMaximum(), is(HinduEra.values()[HinduEra.values().length - 1]));
    }

    @Test
    public void yearOfEraElement() {
        HinduCalendar cal = HinduCalendar.ofOldSolar(3101, HinduMonth.of(IndianMonth.MAGHA).getRasi(), 19);
        assertThat(
            cal.getYear(),
            is(3101));
        assertThat(
            cal.get(HinduCalendar.YEAR_OF_ERA),
            is(3101));
        assertThat(
            cal.getMinimum(HinduCalendar.YEAR_OF_ERA),
            is(0));
        assertThat(
            cal.getMaximum(HinduCalendar.YEAR_OF_ERA),
            is(5999));
        assertThat(
            cal.isValid(HinduCalendar.YEAR_OF_ERA, 3000),
            is(true));
        assertThat(
            cal.with(HinduCalendar.YEAR_OF_ERA, 3000),
            is(HinduCalendar.ofOldSolar(3000, HinduMonth.of(IndianMonth.MAGHA).getRasi(), 19)));
        assertThat(
            cal.isValid(HinduCalendar.YEAR_OF_ERA, null),
            is(false));
        assertThat(
            cal.isValid(HinduCalendar.YEAR_OF_ERA, -1),
            is(false));
        try {
            cal.with(HinduCalendar.YEAR_OF_ERA, -1);
            fail("Negative year not allowed.");
        } catch (IllegalArgumentException iae) {
            // ok
        }
        assertThat(HinduCalendar.YEAR_OF_ERA.name(), is("YEAR_OF_ERA"));
        assertThat(HinduCalendar.YEAR_OF_ERA.getSymbol(), is('y'));
        assertThat(HinduCalendar.YEAR_OF_ERA.isDateElement(), is(true));
        assertThat(HinduCalendar.YEAR_OF_ERA.isTimeElement(), is(false));
        assertThat(HinduCalendar.YEAR_OF_ERA.isLenient(), is(false));
        assertThat(HinduCalendar.YEAR_OF_ERA.getDefaultMinimum(), is(0));
        assertThat(HinduCalendar.YEAR_OF_ERA.getDefaultMaximum(), is(6000));
    }

    @Test
    public void monthOfYearElement() {
        HinduCalendar cal = HinduCalendar.ofOldSolar(3101, HinduMonth.of(IndianMonth.MAGHA).getRasi(), 19);
        assertThat(
            cal.getMonth(),
            is(HinduMonth.of(IndianMonth.MAGHA)));
        assertThat(
            cal.get(HinduCalendar.MONTH_OF_YEAR),
            is(HinduMonth.of(IndianMonth.MAGHA)));
        assertThat(
            cal.getMinimum(HinduCalendar.MONTH_OF_YEAR),
            is(HinduMonth.of(IndianMonth.VAISHAKHA)));
        assertThat(
            cal.getMaximum(HinduCalendar.MONTH_OF_YEAR),
            is(HinduMonth.of(IndianMonth.CHAITRA)));
        assertThat(
            cal.isValid(HinduCalendar.MONTH_OF_YEAR, HinduMonth.ofSolar(3)),
            is(true));
        assertThat(
            cal.with(HinduCalendar.MONTH_OF_YEAR, HinduMonth.ofSolar(3)),
            is(HinduCalendar.ofOldSolar(3101, HinduMonth.of(IndianMonth.ASHADHA).getRasi(), 19)));
        assertThat(
            cal.isValid(HinduCalendar.MONTH_OF_YEAR, null),
            is(false));
        try {
            cal.with(HinduCalendar.MONTH_OF_YEAR, null);
            fail("Negative year not allowed.");
        } catch (IllegalArgumentException iae) {
            // ok
        }
        assertThat(HinduCalendar.MONTH_OF_YEAR.name(), is("MONTH_OF_YEAR"));
        assertThat(HinduCalendar.MONTH_OF_YEAR.getSymbol(), is('M'));
        assertThat(HinduCalendar.MONTH_OF_YEAR.isDateElement(), is(true));
        assertThat(HinduCalendar.MONTH_OF_YEAR.isTimeElement(), is(false));
        assertThat(HinduCalendar.MONTH_OF_YEAR.isLenient(), is(false));
        assertThat(HinduCalendar.MONTH_OF_YEAR.getDefaultMinimum(), is(HinduMonth.of(IndianMonth.CHAITRA)));
        assertThat(HinduCalendar.MONTH_OF_YEAR.getDefaultMaximum(), is(HinduMonth.of(IndianMonth.PHALGUNA)));
    }

    @Test
    public void dayOfYearElement() {
        HinduCalendar cal = HinduCalendar.ofOldSolar(3101, HinduMonth.of(IndianMonth.MAGHA).getRasi(), 19);
        assertThat(
            cal.getDayOfYear(),
            is(293));
        assertThat(
            cal.get(HinduCalendar.DAY_OF_YEAR),
            is(cal.getDayOfYear()));
        assertThat(
            cal.getMinimum(HinduCalendar.DAY_OF_YEAR),
            is(1));
        assertThat(
            cal.getMaximum(HinduCalendar.DAY_OF_YEAR),
            is(366));
        assertThat(
            cal.isValid(HinduCalendar.DAY_OF_YEAR, 366),
            is(true));
        assertThat(
            cal.with(HinduCalendar.DAY_OF_YEAR, 366),
            is(HinduCalendar.ofOldSolar(3101, HinduMonth.of(IndianMonth.CHAITRA).getRasi(), 31)));
        assertThat(
            cal.isValid(HinduCalendar.DAY_OF_YEAR, 367),
            is(false));
        try {
            cal.with(HinduCalendar.DAY_OF_YEAR, 367);
            fail("Day-of-year out of range should not be accepted.");
        } catch (IllegalArgumentException iae) {
            // ok
        }
        assertThat(HinduCalendar.DAY_OF_YEAR.name(), is("DAY_OF_YEAR"));
        assertThat(HinduCalendar.DAY_OF_YEAR.getSymbol(), is('D'));
        assertThat(HinduCalendar.DAY_OF_YEAR.isDateElement(), is(true));
        assertThat(HinduCalendar.DAY_OF_YEAR.isTimeElement(), is(false));
        assertThat(HinduCalendar.DAY_OF_YEAR.isLenient(), is(false));
        assertThat(HinduCalendar.DAY_OF_YEAR.getDefaultMinimum(), is(1));
        assertThat(HinduCalendar.DAY_OF_YEAR.getDefaultMaximum(), is(365));
    }

    @Test
    public void dayOfMonthElement() {
        HinduCalendar cal = HinduCalendar.ofOldSolar(3101, HinduMonth.of(IndianMonth.MAGHA).getRasi(), 19);
        assertThat(
            cal.getDayOfMonth(),
            is(HinduDay.valueOf(19)));
        assertThat(
            cal.get(HinduCalendar.DAY_OF_MONTH),
            is(cal.getDayOfMonth()));
        assertThat(
            cal.getMinimum(HinduCalendar.DAY_OF_MONTH),
            is(HinduDay.valueOf(1)));
        assertThat(
            cal.getMaximum(HinduCalendar.DAY_OF_MONTH),
            is(HinduDay.valueOf(31)));
        assertThat(
            cal.isValid(HinduCalendar.DAY_OF_MONTH, HinduDay.valueOf(31)),
            is(true));
        assertThat(
            cal.with(HinduCalendar.DAY_OF_MONTH, HinduDay.valueOf(31)),
            is(HinduCalendar.ofOldSolar(3101, HinduMonth.of(IndianMonth.MAGHA).getRasi(), 31)));
        assertThat(
            cal.isValid(HinduCalendar.DAY_OF_MONTH, HinduDay.valueOf(19).withLeap()),
            is(false));
        try {
            cal.with(HinduCalendar.DAY_OF_MONTH, HinduDay.valueOf(19).withLeap());
            fail("Invalid day-of-month should not be accepted.");
        } catch (IllegalArgumentException iae) {
            // ok
        }
        assertThat(HinduCalendar.DAY_OF_MONTH.name(), is("DAY_OF_MONTH"));
        assertThat(HinduCalendar.DAY_OF_MONTH.getSymbol(), is('d'));
        assertThat(HinduCalendar.DAY_OF_MONTH.isDateElement(), is(true));
        assertThat(HinduCalendar.DAY_OF_MONTH.isTimeElement(), is(false));
        assertThat(HinduCalendar.DAY_OF_MONTH.isLenient(), is(false));
        assertThat(HinduCalendar.DAY_OF_MONTH.getDefaultMinimum(), is(HinduDay.valueOf(1)));
        assertThat(HinduCalendar.DAY_OF_MONTH.getDefaultMaximum(), is(HinduDay.valueOf(32)));
    }

    @Test
    public void dayOfWeekElement() {
        HinduCalendar cal = HinduCalendar.ofOldSolar(3101, HinduMonth.of(IndianMonth.MAGHA).getRasi(), 20);
        assertThat(
            cal.getDayOfWeek(),
            is(Weekday.MONDAY));
        assertThat(
            cal.get(HinduCalendar.DAY_OF_WEEK),
            is(cal.getDayOfWeek()));
        assertThat(
            cal.getMinimum(HinduCalendar.DAY_OF_WEEK),
            is(Weekday.SUNDAY));
        assertThat(
            cal.getMaximum(HinduCalendar.DAY_OF_WEEK),
            is(Weekday.SATURDAY));
        assertThat(
            cal.isValid(HinduCalendar.DAY_OF_WEEK, Weekday.FRIDAY),
            is(true));
        assertThat(
            cal.with(HinduCalendar.DAY_OF_WEEK, Weekday.FRIDAY),
            is(cal.plus(CalendarDays.of(4))));
        assertThat(
            cal.isValid(HinduCalendar.DAY_OF_WEEK, null),
            is(false));
        try {
            cal.with(HinduCalendar.DAY_OF_WEEK, null);
            fail("Missing day-of-week should not be accepted.");
        } catch (IllegalArgumentException iae) {
            // ok
        }
        assertThat(HinduCalendar.DAY_OF_WEEK.name(), is("DAY_OF_WEEK"));
        assertThat(HinduCalendar.DAY_OF_WEEK.getSymbol(), is('E'));
        assertThat(HinduCalendar.DAY_OF_WEEK.isDateElement(), is(true));
        assertThat(HinduCalendar.DAY_OF_WEEK.isTimeElement(), is(false));
        assertThat(HinduCalendar.DAY_OF_WEEK.isLenient(), is(false));
        assertThat(HinduCalendar.DAY_OF_WEEK.getDefaultMinimum(), is(Weekday.SUNDAY));
        assertThat(HinduCalendar.DAY_OF_WEEK.getDefaultMaximum(), is(Weekday.SATURDAY));
    }

    @Test
    public void lengthOfMonth() {
        HinduCalendar cal =
            HinduCalendar.ofOldLunar(0, HinduMonth.of(IndianMonth.CHAITRA).withLeap(), 1);
        assertThat(
            cal.lengthOfMonth(),
            is(29));
        assertThat(
            cal.getMaximum(HinduCalendar.DAY_OF_MONTH).getValue(),
            is(30));
        assertThat(
            cal.getMaximum(HinduCalendar.DAY_OF_MONTH).isLeap(),
            is(false));
    }

    @Test
    public void lengthOfYear() {
        HinduCalendar cal1 = HinduCalendar.ofOldSolar(3101, HinduMonth.of(IndianMonth.MAGHA).getRasi(), 19);
        assertThat(
            cal1.lengthOfYear(),
            is(366));
        HinduCalendar cal2 = HinduCalendar.ofOldSolar(3102, HinduMonth.of(IndianMonth.MAGHA).getRasi(), 19);
        assertThat(
            cal2.lengthOfYear(),
            is(365));
    }

    @Test
    public void isValid() {
        assertThat(
            HinduCalendar.isValid(
                AryaSiddhanta.SOLAR.variant(),
                HinduEra.KALI_YUGA,
                3101,
                HinduMonth.of(IndianMonth.MAGHA),
                HinduDay.valueOf(20)
            ),
            is(true));
        assertThat(
            HinduCalendar.isValid(
                AryaSiddhanta.LUNAR.variant(),
                HinduEra.KALI_YUGA,
                0,
                HinduMonth.of(IndianMonth.CHAITRA).withLeap(),
                HinduDay.valueOf(14)
            ),
            is(true));
        assertThat(
            HinduCalendar.isValid(
                AryaSiddhanta.LUNAR.variant(),
                HinduEra.KALI_YUGA,
                0,
                HinduMonth.of(IndianMonth.CHAITRA).withLeap(),
                HinduDay.valueOf(15) // expunged day
            ),
            is(false));
        assertThat(
            HinduCalendar.isValid(
                AryaSiddhanta.LUNAR.variant(),
                HinduEra.KALI_YUGA,
                0,
                HinduMonth.of(IndianMonth.CHAITRA).withLeap(),
                HinduDay.valueOf(16)
            ),
            is(true));
    }

    @Test
    public void atTime() {
        HinduCalendar cal = HinduCalendar.ofOldSolar(3101, HinduMonth.of(IndianMonth.MAGHA).getRasi(), 19);
        GeneralTimestamp<HinduCalendar> tsp = cal.atTime(10, 45);
        assertThat(tsp.toDate(), is(cal));
        assertThat(tsp.toTime(), is(PlainTime.of(10, 45)));
    }

    @Test
    public void now() {
        HinduCalendar today1 = HinduCalendar.nowInSystemTime(AryaSiddhanta.SOLAR.variant());
        HinduCalendar today2 = HinduCalendar.nowInSystemTime(AryaSiddhanta.LUNAR.variant());
        System.out.println(today1);
        System.out.println(today2);
        assertThat(today1.getDaysSinceEpochUTC(), is(today2.getDaysSinceEpochUTC()));
    }

    @Test
    public void withFirstDayOfMonth() {
        HinduCalendar cal = HinduCalendar.ofOldLunar(0, HinduMonth.of(IndianMonth.CHAITRA).withLeap(), 1);
        assertThat(cal.with(HinduCalendar.DAY_OF_MONTH.minimized()), is(cal));
        HinduCalendar cal2 = HinduCalendar.ofOldLunar(0, HinduMonth.of(IndianMonth.CHAITRA).withLeap(), 30);
        assertThat(cal2.with(HinduCalendar.DAY_OF_MONTH.minimized()), is(cal));
    }

    @Test
    public void withLastDayOfMonth() {
        HinduCalendar cal = HinduCalendar.ofOldLunar(0, HinduMonth.of(IndianMonth.CHAITRA).withLeap(), 1);
        HinduCalendar expected = HinduCalendar.ofOldLunar(0, HinduMonth.of(IndianMonth.CHAITRA).withLeap(), 30);
        assertThat(cal.with(HinduCalendar.DAY_OF_MONTH.maximized()), is(expected));
    }

    @Test
    public void withNewYear() {
        HinduCalendar cal = HinduCalendar.ofOldLunar(0, HinduMonth.of(IndianMonth.CHAITRA), 2);
        HinduCalendar expected = HinduCalendar.ofOldLunar(0, HinduMonth.of(IndianMonth.CHAITRA).withLeap(), 1);
        assertThat(cal.with(HinduCalendar.DAY_OF_YEAR.minimized()), is(expected));
        assertThat(cal.with(HinduCalendar.DAY_OF_YEAR, 1), is(expected));
    }

    @Test
    public void previousYear() {
        HinduCalendar cal = HinduCalendar.ofOldLunar(1, HinduMonth.of(IndianMonth.CHAITRA), 15);
        HinduCalendar expected = HinduCalendar.ofOldLunar(0, HinduMonth.of(IndianMonth.CHAITRA), 15);
        assertThat(cal.previousYear(), is(expected));
    }

    @Test(expected=IllegalArgumentException.class)
    public void previousYearOutOfRange() {
        HinduCalendar.ofOldLunar(0, HinduMonth.of(IndianMonth.CHAITRA), 15).previousYear();
    }

    @Test
    public void nextYear() {
        HinduCalendar cal = HinduCalendar.ofOldLunar(0, HinduMonth.of(IndianMonth.CHAITRA).withLeap(), 14);
        HinduCalendar expected = HinduCalendar.ofOldLunar(1, HinduMonth.of(IndianMonth.CHAITRA), 14);
        assertThat(cal.nextYear(), is(expected));
    }

    @Test(expected=IllegalArgumentException.class)
    public void nextYearOutOfRange() {
        HinduCalendar.ofOldLunar(5999, HinduMonth.of(IndianMonth.CHAITRA), 15).nextYear();
    }

    @Test
    public void previousMonth() {
        HinduCalendar cal = HinduCalendar.ofOldLunar(0, HinduMonth.of(IndianMonth.CHAITRA), 15);
        HinduCalendar expected = HinduCalendar.ofOldLunar(0, HinduMonth.of(IndianMonth.CHAITRA).withLeap(), 14);
        assertThat(cal.previousMonth(), is(expected));
    }

    @Test(expected=IllegalArgumentException.class)
    public void previousMonthOutOfRange() {
        HinduCalendar.ofOldLunar(0, HinduMonth.of(IndianMonth.CHAITRA).withLeap(), 15).previousMonth();
    }

    @Test
    public void nextMonth() {
        HinduCalendar cal = HinduCalendar.ofOldLunar(0, HinduMonth.of(IndianMonth.CHAITRA).withLeap(), 14);
        HinduCalendar expected = HinduCalendar.ofOldLunar(0, HinduMonth.of(IndianMonth.CHAITRA), 14);
        assertThat(cal.nextMonth(), is(expected));
    }

    @Test(expected=IllegalArgumentException.class)
    public void nextMonthOutOfRange() {
        HinduCalendar.ofOldLunar(5999, HinduMonth.of(IndianMonth.PHALGUNA), 15).nextMonth();
    }

    @Test
    public void previousDay() {
        HinduCalendar cal = HinduCalendar.ofOldLunar(0, HinduMonth.of(IndianMonth.CHAITRA).withLeap(), 16);
        HinduCalendar expected = HinduCalendar.ofOldLunar(0, HinduMonth.of(IndianMonth.CHAITRA).withLeap(), 14);
        assertThat(cal.previousDay(), is(expected));
    }

    @Test
    public void nextDay() {
        HinduCalendar cal = HinduCalendar.ofOldLunar(0, HinduMonth.of(IndianMonth.CHAITRA).withLeap(), 14);
        HinduCalendar expected = HinduCalendar.ofOldLunar(0, HinduMonth.of(IndianMonth.CHAITRA).withLeap(), 16);
        assertThat(cal.nextDay(), is(expected));
    }

    @Test
    public void weekdayMin() {
        HinduCalendar cal1 = HinduCalendar.ofOldLunar(0, HinduMonth.of(IndianMonth.CHAITRA).withLeap(), 14);
        HinduCalendar cal2 = cal1.minus(CalendarDays.of(12));
        assertThat(cal1.with(HinduCalendar.DAY_OF_WEEK.minimized()), is(cal1.minus(CalendarDays.of(3))));
        assertThat(cal2.with(HinduCalendar.DAY_OF_WEEK.minimized()), is(cal2.minus(CalendarDays.ONE)));
        assertThat(cal1.getMinimum(HinduCalendar.DAY_OF_WEEK), is(Weekday.SUNDAY));
        assertThat(cal2.getMinimum(HinduCalendar.DAY_OF_WEEK), is(Weekday.THURSDAY));
        assertThat(cal1.getMaximum(HinduCalendar.DAY_OF_WEEK), is(Weekday.SATURDAY));
        assertThat(cal2.getMaximum(HinduCalendar.DAY_OF_WEEK), is(Weekday.SATURDAY));
        assertThat(
            cal2.minus(CalendarDays.ONE).getDaysSinceEpochUTC(),
            is(HinduCalendar.family().getCalendarSystem(cal1.getVariant()).getMinimumSinceUTC()));
    }

    @Test
    public void weekdayMax() {
        HinduCalendar cal1 = HinduCalendar.ofOldLunar(5999, HinduMonth.of(IndianMonth.PHALGUNA), 17);
        HinduCalendar cal2 = cal1.plus(CalendarDays.of(12));
        assertThat(cal1.with(HinduCalendar.DAY_OF_WEEK.maximized()), is(cal1.plus(CalendarDays.of(2))));
        assertThat(cal2.with(HinduCalendar.DAY_OF_WEEK.maximized()), is(cal2.plus(CalendarDays.ONE)));
        assertThat(cal1.getMinimum(HinduCalendar.DAY_OF_WEEK), is(Weekday.SUNDAY));
        assertThat(cal2.getMinimum(HinduCalendar.DAY_OF_WEEK), is(Weekday.SUNDAY));
        assertThat(cal1.getMaximum(HinduCalendar.DAY_OF_WEEK), is(Weekday.SATURDAY));
        assertThat(cal2.getMaximum(HinduCalendar.DAY_OF_WEEK), is(Weekday.WEDNESDAY));
        assertThat(
            cal2.plus(CalendarDays.ONE).getDaysSinceEpochUTC(),
            is(HinduCalendar.family().getCalendarSystem(cal1.getVariant()).getMaximumSinceUTC()));
    }

    @Test
    public void sanityCheckForMonth() {
        HinduCalendar min = HinduCalendar.ofOldLunar(1, HinduMonth.of(IndianMonth.CHAITRA), 15);
        HinduCalendar max = HinduCalendar.ofOldLunar(5998, HinduMonth.of(IndianMonth.PHALGUNA), 16);
        HinduCalendar cal = min;

        while (cal.isBefore(max)) {
            int kyYear = cal.getExpiredYearOfKaliYuga();
            HinduCalendar hmin = cal.with(HinduCalendar.MONTH_OF_YEAR.minimized());
            assertThat(hmin.getExpiredYearOfKaliYuga(), is(kyYear));
            hmin = hmin.previousMonth();
            assertThat(hmin.getExpiredYearOfKaliYuga(), not(kyYear));

            cal = cal.nextMonth();
        }
    }

    @Test
    public void sanityCheckForDayOfMonth() {
        HinduCalendar min = HinduCalendar.ofOldLunar(0, HinduMonth.of(IndianMonth.CHAITRA), 1);
        HinduCalendar max = HinduCalendar.ofOldLunar(5999, HinduMonth.of(IndianMonth.MAGHA), 29);
        HinduCalendar cal = min;

        while (cal.isBefore(max)) {
            int kyYear = cal.getExpiredYearOfKaliYuga();
            HinduMonth month = cal.getMonth();

            HinduCalendar hmin = cal.with(HinduCalendar.DAY_OF_MONTH.minimized());
            assertThat(hmin.getExpiredYearOfKaliYuga(), is(kyYear));
            assertThat(hmin.getMonth(), is(month));
            hmin = hmin.previousDay();
            assertThat(hmin.getMonth(), not(month));

            HinduCalendar hmax = cal.with(HinduCalendar.DAY_OF_MONTH.maximized());
            assertThat(hmax.getExpiredYearOfKaliYuga(), is(kyYear));
            assertThat(hmax.getMonth(), is(month));
            hmax = hmax.nextDay();
            assertThat(hmax.getMonth(), not(month));

            cal = cal.nextDay();
        }
    }

    @Test
    public void relatedGregorianYear() {
        HinduCalendar cal = HinduCalendar.ofOldSolar(5121, 2, 7);
        assertThat(
            cal.get(CommonElements.RELATED_GREGORIAN_YEAR),
            is(2020));
    }

}
