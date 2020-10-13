package net.time4j.range;

import net.time4j.CalendarUnit;
import net.time4j.ClockUnit;
import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;
import net.time4j.engine.ChronoException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class HolidayTest {

    @Test
    public void ofWeekend() {
        HolidayModel hm = HolidayModel.ofWeekend(Locale.ROOT);
        assertThat(
            PlainDate.of(2017, 4, 28).with(hm.nextBusinessDay()),
            is(PlainDate.of(2017, 5, 1)));
        assertThat(
            PlainDate.of(2017, 4, 29).with(hm.nextBusinessDay()),
            is(PlainDate.of(2017, 5, 1)));
        assertThat(
            PlainDate.of(2017, 4, 30).with(hm.nextBusinessDay()),
            is(PlainDate.of(2017, 5, 1)));
        assertThat(
            PlainDate.of(2017, 5, 1).with(hm.nextBusinessDay()),
            is(PlainDate.of(2017, 5, 2)));
    }

    @Test
    public void nextBusinessDay() {
        HolidayModel hm = HolidayModel.ofSaturdayOrSunday();
        assertThat(
            PlainDate.of(2017, 4, 28).with(hm.nextBusinessDay()),
            is(PlainDate.of(2017, 5, 1)));
        assertThat(
            PlainDate.of(2017, 4, 29).with(hm.nextBusinessDay()),
            is(PlainDate.of(2017, 5, 1)));
        assertThat(
            PlainDate.of(2017, 4, 30).with(hm.nextBusinessDay()),
            is(PlainDate.of(2017, 5, 1)));
        assertThat(
            PlainDate.of(2017, 5, 1).with(hm.nextBusinessDay()),
            is(PlainDate.of(2017, 5, 2)));
    }

    @Test
    public void nextOrSameBusinessDay() {
        HolidayModel hm = HolidayModel.ofSaturdayOrSunday();
        assertThat(
            PlainDate.of(2017, 4, 28).with(hm.nextOrSameBusinessDay()),
            is(PlainDate.of(2017, 4, 28)));
        assertThat(
            PlainDate.of(2017, 4, 29).with(hm.nextOrSameBusinessDay()),
            is(PlainDate.of(2017, 5, 1)));
        assertThat(
            PlainDate.of(2017, 4, 30).with(hm.nextOrSameBusinessDay()),
            is(PlainDate.of(2017, 5, 1)));
        assertThat(
            PlainDate.of(2017, 5, 1).with(hm.nextOrSameBusinessDay()),
            is(PlainDate.of(2017, 5, 1)));
    }

    @Test
    public void previousBusinessDay() {
        HolidayModel hm = HolidayModel.ofSaturdayOrSunday();
        assertThat(
            PlainDate.of(2017, 4, 28).with(hm.previousBusinessDay()),
            is(PlainDate.of(2017, 4, 27)));
        assertThat(
            PlainDate.of(2017, 4, 29).with(hm.previousBusinessDay()),
            is(PlainDate.of(2017, 4, 28)));
        assertThat(
            PlainDate.of(2017, 4, 30).with(hm.previousBusinessDay()),
            is(PlainDate.of(2017, 4, 28)));
        assertThat(
            PlainDate.of(2017, 5, 1).with(hm.previousBusinessDay()),
            is(PlainDate.of(2017, 4, 28)));
    }

    @Test
    public void previousOrSameBusinessDay() {
        HolidayModel hm = HolidayModel.ofSaturdayOrSunday();
        assertThat(
            PlainDate.of(2017, 4, 28).with(hm.previousOrSameBusinessDay()),
            is(PlainDate.of(2017, 4, 28)));
        assertThat(
            PlainDate.of(2017, 4, 29).with(hm.previousOrSameBusinessDay()),
            is(PlainDate.of(2017, 4, 28)));
        assertThat(
            PlainDate.of(2017, 4, 30).with(hm.previousOrSameBusinessDay()),
            is(PlainDate.of(2017, 4, 28)));
        assertThat(
            PlainDate.of(2017, 5, 1).with(hm.previousOrSameBusinessDay()),
            is(PlainDate.of(2017, 5, 1)));
    }

    @Test
    public void firstBusinessDay() {
        PlainDate date = CalendarMonth.of(2017, 4).get(HolidayModel.ofSaturdayOrSunday().firstBusinessDay());
        assertThat(date, is(PlainDate.of(2017, 4, 3)));
        date = CalendarMonth.of(2017, 2).get(HolidayModel.ofSaturdayOrSunday().firstBusinessDay());
        assertThat(date, is(PlainDate.of(2017, 2, 1)));
        date = CalendarMonth.of(2017, 10).get(HolidayModel.ofSaturdayOrSunday().firstBusinessDay());
        assertThat(date, is(PlainDate.of(2017, 10, 2)));
    }

    @Test
    public void lastBusinessDay() {
        PlainDate date = CalendarMonth.of(2017, 4).get(HolidayModel.ofSaturdayOrSunday().lastBusinessDay());
        assertThat(date, is(PlainDate.of(2017, 4, 28)));
        date = CalendarMonth.of(2017, 2).get(HolidayModel.ofSaturdayOrSunday().lastBusinessDay());
        assertThat(date, is(PlainDate.of(2017, 2, 28)));
        date = CalendarMonth.of(2017, 9).get(HolidayModel.ofSaturdayOrSunday().lastBusinessDay());
        assertThat(date, is(PlainDate.of(2017, 9, 29)));
    }

    @Test
    public void lastBusinessDayNull() {
        PlainDate date =
            DateInterval
                .between(PlainDate.of(2017, 2, 4), PlainDate.of(2017, 2, 5)) // only weekend
                .get(HolidayModel.ofSaturdayOrSunday().lastBusinessDay());
        assertThat(date, nullValue());
    }

    @Test(expected= ChronoException.class)
    public void lastBusinessDayEx() { // infinite interval
        DateInterval.until(PlainDate.of(2017, 2, 28)).get(HolidayModel.ofSaturdayOrSunday().lastBusinessDay());
    }

    @Test
    public void countOfBusinessDays(){
        assertThat(CalendarMonth.of(2017, 1).get(HolidayModel.ofSaturdayOrSunday().countOfBusinessDays()), is(22));
        assertThat(CalendarMonth.of(2017, 4).get(HolidayModel.ofSaturdayOrSunday().countOfBusinessDays()), is(20));
        assertThat(CalendarWeek.of(2017, 52).get(HolidayModel.ofSaturdayOrSunday().countOfBusinessDays()), is(5));

        assertThat(
            DateInterval.atomic(PlainDate.of(2017, 2, 3)).get(HolidayModel.ofSaturdayOrSunday().countOfBusinessDays()),
            is(1));
        assertThat(
            DateInterval.atomic(PlainDate.of(2017, 2, 4)).get(HolidayModel.ofSaturdayOrSunday().countOfBusinessDays()),
            is(0));
        assertThat(
            DateInterval.atomic(PlainDate.of(2017, 2, 3)).collapse().get(
                HolidayModel.ofSaturdayOrSunday().countOfBusinessDays()),
            is(0));
    }

    @Test(expected=ChronoException.class)
    public void countOfBusinessDaysInfinite(){
        DateInterval.since(PlainDate.of(2017, 2, 4)).get(HolidayModel.ofSaturdayOrSunday().countOfBusinessDays());
    }

    @Test
    public void countOfHolidays(){
        assertThat(CalendarMonth.of(2017, 1).get(HolidayModel.ofSaturdayOrSunday().countOfHolidays()), is(9));
        assertThat(CalendarMonth.of(2017, 4).get(HolidayModel.ofSaturdayOrSunday().countOfHolidays()), is(10));
        assertThat(CalendarWeek.of(2017, 52).get(HolidayModel.ofSaturdayOrSunday().countOfHolidays()), is(2));

        assertThat(
            DateInterval.atomic(PlainDate.of(2017, 2, 3)).get(HolidayModel.ofSaturdayOrSunday().countOfHolidays()),
            is(0));
        assertThat(
            DateInterval.atomic(PlainDate.of(2017, 2, 4)).get(HolidayModel.ofSaturdayOrSunday().countOfHolidays()),
            is(1));
        assertThat(
            DateInterval.atomic(PlainDate.of(2017, 2, 3)).collapse().get(
                HolidayModel.ofSaturdayOrSunday().countOfHolidays()),
            is(0));
    }

    @Test(expected=ChronoException.class)
    public void countOfHolidaysInfinite(){
        DateInterval.since(PlainDate.of(2017, 2, 4)).get(HolidayModel.ofSaturdayOrSunday().countOfHolidays());
    }

    @Test
    public void firstBusinessDayInMonth() {
        assertThat(
            PlainDate.of(2017, 4, 2).matches(HolidayModel.ofSaturdayOrSunday().firstBusinessDayInMonth()),
            is(false));
        assertThat(
            PlainDate.of(2017, 4, 3).matches(HolidayModel.ofSaturdayOrSunday().firstBusinessDayInMonth()),
            is(true));
        assertThat(
            PlainDate.of(2017, 4, 4).matches(HolidayModel.ofSaturdayOrSunday().firstBusinessDayInMonth()),
            is(false));
    }

    @Test
    public void lastBusinessDayInMonth() {
        assertThat(
            PlainDate.of(2017, 4, 27).matches(HolidayModel.ofSaturdayOrSunday().lastBusinessDayInMonth()),
            is(false));
        assertThat(
            PlainDate.of(2017, 4, 28).matches(HolidayModel.ofSaturdayOrSunday().lastBusinessDayInMonth()),
            is(true));
        assertThat(
            PlainDate.of(2017, 4, 29).matches(HolidayModel.ofSaturdayOrSunday().lastBusinessDayInMonth()),
            is(false));
    }

    @Test
    public void businessDays1() {
        HolidayModel hm = HolidayModel.ofSaturdayOrSunday();
        PlainDate start = PlainDate.of(2017, 2, 3);
        PlainDate end = PlainDate.of(2017, 2, 7);
        assertThat(start.plus(2, hm.businessDays()), is(end));
        assertThat(start.until(end, hm.businessDays()), is(2L));
    }

    @Test
    public void businessDays2() {
        HolidayModel hm = HolidayModel.ofSaturdayOrSunday();
        PlainDate start = PlainDate.of(2017, 2, 2);
        PlainDate end = PlainDate.of(2017, 2, 7);
        assertThat(start.plus(3, hm.businessDays()), is(end));
        assertThat(start.until(end, hm.businessDays()), is(3L));
    }

    @Test
    public void businessDays3() {
        HolidayModel hm = HolidayModel.ofSaturdayOrSunday();
        PlainDate start = PlainDate.of(2017, 2, 1);
        PlainDate end = PlainDate.of(2017, 2, 6);
        assertThat(start.plus(3, hm.businessDays()), is(end));
        assertThat(start.until(end, hm.businessDays()), is(3L));
    }

    @Test
    public void businessDays4() {
        HolidayModel hm = HolidayModel.ofSaturdayOrSunday();
        PlainDate start = PlainDate.of(2017, 2, 1);
        PlainDate end = PlainDate.of(2017, 2, 3);
        assertThat(start.plus(2, hm.businessDays()), is(end));
        assertThat(start.until(end, hm.businessDays()), is(2L));
        assertThat(start.until(end.plus(1, CalendarUnit.DAYS), hm.businessDays()), is(2L));
        assertThat(start.until(end.plus(2, CalendarUnit.DAYS), hm.businessDays()), is(2L));

        assertThat(end.until(start, hm.businessDays()), is(-2L));
        assertThat(end.plus(1, CalendarUnit.DAYS).until(start, hm.businessDays()), is(-2L));
        assertThat(end.plus(2, CalendarUnit.DAYS).until(start, hm.businessDays()), is(-2L));
    }

    @Test
    public void businessDays5() {
        HolidayModel hm = HolidayModel.ofSaturdayOrSunday();
        PlainTimestamp start = PlainTimestamp.of(2017, 2, 1, 17, 0);
        PlainTimestamp end = PlainTimestamp.of(2017, 2, 6, 9, 0);
        assertThat(start.plus(3, hm.businessDays()), is(end.plus(8, ClockUnit.HOURS)));
        assertThat(start.until(end, hm.businessDays()), is(2L));
        assertThat(start.until(end.plus(8, ClockUnit.HOURS), hm.businessDays()), is(3L));
    }

    @Test
    public void isBusinessDay() {
        HolidayModel hm = HolidayModel.ofSaturdayOrSunday();
        assertThat(hm.isBusinessDay(PlainDate.of(2017, 2, 1)), is(true));
        assertThat(hm.isBusinessDay(PlainDate.of(2017, 2, 4)), is(false));
    }

}