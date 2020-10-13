package net.time4j;

import java.math.BigDecimal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.PlainTime.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class RatioTest {

    @Test
    public void nanoOfDayRatio() {
        assertThat(
           PlainTime.midnightAtStartOfDay().get(NANO_OF_DAY.ratio()),
           is(BigDecimal.ZERO));
        assertThat(
           PlainTime.of(6).get(NANO_OF_DAY.ratio()),
           is(new BigDecimal("0.25")));
        assertThat(
           PlainTime.midnightAtEndOfDay().get(NANO_OF_DAY.ratio()),
           is(BigDecimal.ONE));
        assertThat(
           PlainTime.of(23, 59, 59, 999999999).get(NANO_OF_DAY.ratio()),
           is(new BigDecimal("0.999999999999988")));
    }

    @Test
    public void microOfDayRatio() {
        BigDecimal expected = new BigDecimal("0.250000000011574");
        assertThat(
           PlainTime.of(6, 0, 0, 1000).get(MICRO_OF_DAY.ratio()),
           is(expected));
        assertThat(
           PlainTime.of(6, 0, 0, 1999).get(MICRO_OF_DAY.ratio()),
           is(expected));
        assertThat(
           PlainTime.of(6, 15, 30, 123456000).get(MICRO_OF_DAY.ratio()),
           is(new BigDecimal("0.260765317777778")));
    }

    @Test
    public void milliOfDayRatio() {
        BigDecimal expected = new BigDecimal("0.2500115625");
        assertThat(
           PlainTime.of(6, 0, 0, 999000000).get(MILLI_OF_DAY.ratio()),
           is(expected));
        assertThat(
           PlainTime.of(6, 0, 0, 999999999).get(MILLI_OF_DAY.ratio()),
           is(expected));
        assertThat(
           PlainTime.of(6, 15, 30, 123000000).get(MILLI_OF_DAY.ratio()),
           is(new BigDecimal("0.2607653125")));
    }

    @Test
    public void nanoOfSecondRatio() {
        assertThat(
           PlainTime.of(6, 0, 0, 123456789).get(NANO_OF_SECOND.ratio()),
           is(new BigDecimal("0.123456789")));
    }

    @Test
    public void microOfSecondRatio() {
        assertThat(
           PlainTime.of(6, 0, 0, 123456789).get(MICRO_OF_SECOND.ratio()),
           is(new BigDecimal("0.123456")));
    }

    @Test
    public void milliOfSecondRatio() {
        assertThat(
           PlainTime.of(6, 0, 0, 123456789).get(MILLI_OF_SECOND.ratio()),
           is(new BigDecimal("0.123")));
    }

    @Test
    public void secondOfDayRatio() {
        BigDecimal expected = new BigDecimal("0.281481481481481");
        assertThat(
           PlainTime.of(6, 45, 20).get(SECOND_OF_DAY.ratio()),
           is(expected));
        assertThat(
           PlainTime.of(6, 45, 20, 1).get(SECOND_OF_DAY.ratio()),
           is(expected));
    }

    @Test
    public void minuteOfDayRatio() {
        BigDecimal expected = new BigDecimal("0.258333333333333");
        assertThat(
           PlainTime.of(6, 12).get(MINUTE_OF_DAY.ratio()),
           is(expected));
        assertThat(
           PlainTime.of(6, 12, 1).get(MINUTE_OF_DAY.ratio()),
           is(expected));
    }

    @Test
    public void secondOfMinuteRatio() {
        assertThat(
           PlainTime.of(6, 0, 30, 123456789).get(SECOND_OF_MINUTE.ratio()),
           is(new BigDecimal("0.5")));
    }

    @Test
    public void minuteOfHourRatio() {
        assertThat(
           PlainTime.of(6, 12, 30).get(MINUTE_OF_HOUR.ratio()),
           is(new BigDecimal("0.2")));
    }

    @Test
    public void digitalHourOfDayRatio() {
        assertThat(
           PlainTime.of(6, 12, 30).get(DIGITAL_HOUR_OF_DAY.ratio()),
           is(new BigDecimal("0.25")));
    }

    @Test
    public void digitalHourOfAmPmRatio() {
        assertThat(
           PlainTime.of(6, 12, 30).get(DIGITAL_HOUR_OF_AMPM.ratio()),
           is(new BigDecimal("0.5")));
    }

    @Test
    public void hour0To24Ratio() {
        assertThat(
           PlainTime.midnightAtStartOfDay().get(HOUR_FROM_0_TO_24.ratio()),
           is(BigDecimal.ZERO));
        assertThat(
           PlainTime.of(6).get(HOUR_FROM_0_TO_24.ratio()),
           is(new BigDecimal("0.25")));
        assertThat(
           PlainTime.of(6, 12, 30).get(HOUR_FROM_0_TO_24.ratio()),
           is(new BigDecimal("0.25")));
        assertThat(
           PlainTime.midnightAtEndOfDay().get(HOUR_FROM_0_TO_24.ratio()),
           is(BigDecimal.ONE));
    }

    @Test
    public void monthAsNumberRatio() {
        assertThat(
           PlainDate.of(2014, 4, 20).get(PlainDate.MONTH_AS_NUMBER.ratio()),
           is(new BigDecimal("0.25")));
    }

    @Test
    public void dayOfYearRatio() {
        assertThat(
           PlainDate.of(2014, 4, 20).get(PlainDate.DAY_OF_YEAR.ratio()),
           is(new BigDecimal("0.298630136986301")));
        assertThat(
           PlainDate.of(2012, 4, 20).get(PlainDate.DAY_OF_YEAR.ratio()),
           is(new BigDecimal("0.300546448087432")));
    }

    @Test
    public void dayOfQuarterRatio() {
        assertThat(
           PlainDate.of(2014, 4, 20).get(PlainDate.DAY_OF_QUARTER.ratio()),
           is(new BigDecimal("0.208791208791209")));
    }

    @Test
    public void dayOfMonthRatio() {
        assertThat(
           PlainDate.of(2014, 4, 21).get(PlainDate.DAY_OF_MONTH.ratio()),
           is(new BigDecimal("0.666666666666667")));
        assertThat(
           PlainDate.of(2012, 2, 21).get(PlainDate.DAY_OF_MONTH.ratio()),
           is(new BigDecimal("0.689655172413793")));
    }

}