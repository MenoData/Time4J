package net.time4j.calendar;

import net.time4j.PlainDate;
import net.time4j.Weekday;
import net.time4j.engine.CalendarDays;
import net.time4j.history.ChronoHistory;
import net.time4j.history.HistoricDate;
import net.time4j.history.HistoricEra;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


@RunWith(JUnit4.class)
public class HebrewMiscellaneousTest {

    @Test
    public void hebrewEpoch() {
        PlainDate gregorian = PlainDate.of(-3760, 9, 7);
        PlainDate julian = ChronoHistory.PROLEPTIC_JULIAN.convert(HistoricDate.of(HistoricEra.BC, 3761, 10, 7));
        assertThat(gregorian, is(julian));
    }

    @Test
    public void isSabbaticalYear() {
        HebrewCalendar date = HebrewCalendar.of(5778, HebrewMonth.TISHRI, 1);
        for (int i = 0; i < 7; i++) {
            assertThat(date.isSabbaticalYear(), is(date.getYear() == 5782));
            date = date.plus(1, HebrewCalendar.Unit.YEARS);
        }
    }

    @Test
    public void hebrewCalendarProperties() {
        HebrewCalendar date = HebrewCalendar.of(5778, HebrewMonth.TISHRI, 11);
        assertThat(
            date.getDayOfMonth(),
            is(11));
        assertThat(
            date.getMonth(),
            is(HebrewMonth.TISHRI));
        assertThat(
            date.lengthOfMonth(),
            is(30));
        assertThat(
            date.atTime(12, 0).toDate(),
            is(date));
        assertThat(
            date.lengthOfYear(),
            is(354)
        );
    }

    @Test
    public void hebrewUnits() {
        HebrewCalendar start = HebrewCalendar.of(5778, HebrewMonth.HESHVAN, 6);
        HebrewCalendar end = HebrewCalendar.of(5778, HebrewMonth.ELUL, 6);
        assertThat(HebrewCalendar.Unit.MONTHS.between(start, end), is(10));
        start = start.plus(CalendarDays.ONE);
        assertThat(HebrewCalendar.Unit.MONTHS.between(start, end), is(9));
        start = start.minus(3, HebrewCalendar.Unit.YEARS);
        assertThat(HebrewCalendar.Unit.YEARS.between(start, end), is(3));
        start = start.plus(6, HebrewCalendar.Unit.YEARS).minus(CalendarDays.of(2)); // AM-5781-HESHVAN-5
        assertThat(HebrewCalendar.Unit.YEARS.between(start, end), is(-2));
        start = start.with(HebrewCalendar.MONTH_OF_YEAR, HebrewMonth.ELUL); // AM-5781-ELUL-5
        assertThat(HebrewCalendar.Unit.MONTHS.between(start, end), is(-36));
        start = start.plus(CalendarDays.ONE);
        assertThat(HebrewCalendar.Unit.MONTHS.between(start, end), is(-37));
        start = start.minus(37, HebrewCalendar.Unit.MONTHS);
        assertThat(start, is(end));
    }

    @Test
    public void defaultFirstDayOfWeek() {
        assertThat(HebrewCalendar.DAY_OF_WEEK.getDefaultMinimum(), is(Weekday.SUNDAY));
    }

    @Test
    public void longYear() {
        for (int y = 1; y <= 9999; y++) {
            HebrewCalendar h1 = HebrewCalendar.of(y, HebrewMonth.HESHVAN, 1);
            HebrewCalendar h2 = HebrewCalendar.of(y, HebrewMonth.KISLEV, 1);
            if (h1.lengthOfMonth() == 30 && h2.lengthOfMonth() == 29) {
                fail("Long Heshvan cannot be combined with short Kislev in same year: " + y);
                break;
            }
        }
    }

}