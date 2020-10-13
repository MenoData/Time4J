package net.time4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.PlainTime.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class SetLenientTest {

    @Test
    public void nanoOfDayLenient() {
        assertThat(
           PlainTime.midnightAtStartOfDay().with(NANO_OF_DAY.setLenient(-1L)),
           is(PlainTime.of(23, 59, 59, 999999999)));
        assertThat(
           PlainTime.midnightAtStartOfDay()
                .with(NANO_OF_DAY.setLenient(86400 * 1000000000L + 1)),
           is(PlainTime.of(0, 0, 0, 1)));
    }

    @Test
    public void microOfDayLenient() {
        assertThat(
           PlainTime.of(6, 45, 20, 1).with(MICRO_OF_DAY.setLenient(-1L)),
           is(PlainTime.of(23, 59, 59, 999999001)));
        assertThat(
           PlainTime.midnightAtStartOfDay().with(MICRO_OF_DAY.setLenient(-1L)),
           is(PlainTime.of(23, 59, 59, 999999000)));
        assertThat(
           PlainTime.midnightAtStartOfDay()
                .with(MICRO_OF_DAY.setLenient(86400 * 1000000L + 1)),
           is(PlainTime.of(0, 0, 0, 1000)));
    }

    @Test
    public void milliOfDayLenient() {
        assertThat(
           PlainTime.of(6, 45, 20, 1).with(MILLI_OF_DAY.setLenient(-1)),
           is(PlainTime.of(23, 59, 59, 999000001)));
        assertThat(
           PlainTime.midnightAtStartOfDay().with(MILLI_OF_DAY.setLenient(-1)),
           is(PlainTime.of(23, 59, 59, 999000000)));
        assertThat(
           PlainTime.midnightAtStartOfDay()
                .with(MILLI_OF_DAY.setLenient(86400 * 1000 + 1)),
           is(PlainTime.of(0, 0, 0, 1000000)));
    }

    @Test
    public void nanoOfSecondLenient() {
        assertThat(
           PlainTime.of(6, 0, 0, 123456789)
                .with(NANO_OF_SECOND.setLenient(-1)),
           is(PlainTime.of(5, 59, 59, 999999999)));
    }

    @Test
    public void microOfSecondLenient() {
        assertThat(
           PlainTime.of(6, 0, 0, 123456789)
                .with(MICRO_OF_SECOND.setLenient(-1)),
           is(PlainTime.of(5, 59, 59, 999999789)));
    }

    @Test
    public void milliOfSecondLenient() {
        assertThat(
           PlainTime.of(6, 0, 0, 123456789)
                .with(MILLI_OF_SECOND.setLenient(-1)),
           is(PlainTime.of(5, 59, 59, 999456789)));
    }

    @Test
    public void secondOfDayLenient() {
        assertThat(
           PlainTime.of(6, 45, 20, 1)
                .with(SECOND_OF_DAY.setLenient(-1)),
           is(PlainTime.of(23, 59, 59, 1)));
    }

    @Test
    public void minuteOfDayLenient() {
        assertThat(
           PlainTime.of(6, 45, 20, 1)
                .with(MINUTE_OF_DAY.setLenient(-1)),
           is(PlainTime.of(23, 59, 20, 1)));
    }

    @Test
    public void secondOfMinuteLenient() {
        assertThat(
           PlainTime.of(6, 0, 30, 123456789)
                .with(SECOND_OF_MINUTE.setLenient(61)),
           is(PlainTime.of(6, 1, 1, 123456789)));
    }

    @Test
    public void minuteOfHourLenient() {
        assertThat(
           PlainTime.of(6, 0, 30, 123456789)
                .with(MINUTE_OF_HOUR.setLenient(61)),
           is(PlainTime.of(7, 1, 30, 123456789)));
    }

    @Test
    public void digitalHourOfDayLenient() {
        assertThat(
           PlainTime.of(6, 12, 30).with(DIGITAL_HOUR_OF_DAY.setLenient(25)),
           is(PlainTime.of(1, 12, 30)));
    }

    @Test
    public void digitalHourOfAmPmLenient() {
        assertThat(
           PlainTime.of(6, 12, 30).with(DIGITAL_HOUR_OF_AMPM.setLenient(14)),
           is(PlainTime.of(14, 12, 30)));
    }

    @Test
    public void hour0To24Lenient() {
        assertThat(
           PlainTime.of(6, 12, 30).with(HOUR_FROM_0_TO_24.setLenient(25)),
           is(PlainTime.of(1, 12, 30)));
    }

    @Test
    public void monthAsNumberLenient() {
        assertThat(
           PlainDate.of(2014, 4, 20)
                .with(PlainDate.MONTH_AS_NUMBER.setLenient(13)),
           is(PlainDate.of(2015, 1, 20)));
    }

    @Test
    public void dayOfYearLenient() {
        assertThat(
           PlainDate.of(2014, 4, 20)
                .with(PlainDate.DAY_OF_YEAR.setLenient(367)),
           is(PlainDate.of(2015, 1, 2)));
    }

    @Test
    public void dayOfQuarterLenient() {
        assertThat(
           PlainDate.of(2014, 4, 20)
                .with(PlainDate.DAY_OF_QUARTER.setLenient(93)),
           is(PlainDate.of(2014, 7, 2)));
    }

    @Test
    public void dayOfMonthLenient() {
        assertThat(
           PlainDate.of(2014, 4, 20)
                .with(PlainDate.DAY_OF_MONTH.setLenient(32)),
           is(PlainDate.of(2014, 5, 2)));
    }

    @Test
    public void digitalHourOfDayLenientOnTimestamp() {
        assertThat(
           PlainTimestamp.of(2014, 5, 9, 1, 12, 30)
                .with(DIGITAL_HOUR_OF_DAY.setLenient(48)),
           is(PlainTimestamp.of(2014, 5, 11, 0, 12, 30)));
    }

    @Test
    public void digitalHourOfAmPmLenientOnTimestamp() {
        assertThat(
           PlainTimestamp.of(2014, 5, 9, 1, 12, 30)
                .with(DIGITAL_HOUR_OF_AMPM.setLenient(48)),
           is(PlainTimestamp.of(2014, 5, 11, 0, 12, 30)));
    }

    @Test
    public void hour0To24LenientOnTimestamp() {
        assertThat(
           PlainTimestamp.of(2014, 5, 9, 1, 12, 30)
                .with(HOUR_FROM_0_TO_24.setLenient(48)),
           is(PlainTimestamp.of(2014, 5, 11, 0, 12, 30)));
    }

    @Test
    public void minuteOfHourLenientOnTimestamp() {
        assertThat(
           PlainTimestamp.of(2014, 5, 9, 23, 59, 30)
                .with(MINUTE_OF_HOUR.setLenient(60)),
           is(PlainTimestamp.of(2014, 5, 10, 0, 0, 30)));
    }

    @Test
    public void dayOfMonthLenientOnTimestamp() {
        assertThat(
           PlainTimestamp.of(2014, 5, 9, 23, 59, 30)
                .with(PlainDate.DAY_OF_MONTH.setLenient(33)),
           is(PlainTimestamp.of(2014, 6, 2, 23, 59, 30)));
        assertThat(
           PlainTimestamp.of(2012, 2, 9, 23, 59, 30)
                .with(PlainDate.DAY_OF_MONTH.setLenient(33)),
           is(PlainTimestamp.of(2012, 3, 4, 23, 59, 30)));
    }

}