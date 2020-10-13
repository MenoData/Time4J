package net.time4j;

import net.time4j.format.Attributes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class TimestampCreationTest {

    @Test
    public void ofDateTime() {
        PlainDate date = PlainDate.of(2014, Month.APRIL, 21);
        PlainTime time = PlainTime.of(19, 45, 30, 123456789);
        PlainTimestamp ts = PlainTimestamp.of(date, time);
        assertThat(ts.getCalendarDate(), is(date));
        assertThat(ts.getWallTime(), is(time));
    }

    @Test
    public void ofDateTime24() {
        PlainDate date = PlainDate.of(2014, Month.APRIL, 21);
        PlainTime time = PlainTime.midnightAtEndOfDay();
        PlainTimestamp ts = PlainTimestamp.of(date, time);
        assertThat(ts.getCalendarDate(), is(PlainDate.of(2014, 4, 22)));
        assertThat(ts.getWallTime(), is(PlainTime.midnightAtStartOfDay()));
    }

    @Test(expected=ArithmeticException.class)
    public void ofDateTimeRangeOverflow() {
        PlainDate date = PlainDate.of(999999999, 12, 31);
        PlainTime time = PlainTime.midnightAtEndOfDay();
        PlainTimestamp.of(date, time);
    }

    @Test
    public void ofYearMonthDayHourMinute() {
        PlainDate date = PlainDate.of(2014, Month.APRIL, 21);
        PlainTime time = PlainTime.of(19, 45);
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 19, 45),
            is(PlainTimestamp.of(date, time)));
    }

    @Test
    public void ofYearMonthDayHourMinuteSecond() {
        PlainDate date = PlainDate.of(2014, Month.APRIL, 21);
        PlainTime time = PlainTime.of(19, 45, 30);
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 19, 45, 30),
            is(PlainTimestamp.of(date, time)));
    }

    @Test
    public void ofYearMonthDayHour24() {
        PlainDate date = PlainDate.of(2014, Month.APRIL, 22);
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 24, 0),
            is(PlainTimestamp.of(date, PlainTime.midnightAtStartOfDay())));
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofYearMonthDayHourMinuteSecondOverflow() {
        PlainTimestamp.of(2014, 4, 21, 19, 45, 60);
    }

    @Test(expected=IllegalArgumentException.class)
    public void merge() {
        PlainTimestamp.axis().createFrom(
            Moment.UNIX_EPOCH, Attributes.empty(), false, false);
    }

    @Test
    public void mergeLax() {
        assertThat(
            PlainTimestamp.axis().createFrom(
                Moment.UNIX_EPOCH,
                Attributes.empty(),
                true,
                false),
        is(PlainTimestamp.of(1970, 1, 1, 0, 0)));
    }

}