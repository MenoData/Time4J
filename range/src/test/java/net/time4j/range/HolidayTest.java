package net.time4j.range;

import net.time4j.PlainDate;
import net.time4j.engine.ChronoException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;


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

}