package net.time4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.PlainTime.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class TimeOperatorTest {

    @Test
    public void clockHourOfAmPmMinimized() {
        assertThat(
            PlainTime.of(19, 45, 30).with(CLOCK_HOUR_OF_AMPM.minimized()),
            is(PlainTime.of(13, 45, 30)));
    }

    @Test
    public void clockHourOfAmPmMinimizedIfHour24() {
        assertThat(
            PlainTime.of(24).with(CLOCK_HOUR_OF_AMPM.minimized()),
            is(PlainTime.of(1)));
    }

    @Test
    public void clockHourOfAmPmMaximized() {
        assertThat(
            PlainTime.of(19, 45, 30).with(CLOCK_HOUR_OF_AMPM.maximized()),
            is(PlainTime.of(12, 45, 30)));
    }

    @Test
    public void clockHourOfAmPmMaximizedIfHour24() {
        assertThat(
            PlainTime.of(24).with(CLOCK_HOUR_OF_AMPM.maximized()),
            is(PlainTime.of(0)));
    }

    @Test
    public void clockHourOfAmPmDecremented() {
        assertThat(
            PlainTime.of(19, 45, 30).with(CLOCK_HOUR_OF_AMPM.decremented()),
            is(PlainTime.of(18, 45, 30)));
    }

    @Test
    public void clockHourOfAmPmDecrementedIfHour24() {
        assertThat(
            PlainTime.of(24).with(CLOCK_HOUR_OF_AMPM.decremented()),
            is(PlainTime.of(23)));
    }

    @Test
    public void clockHourOfAmPmIncremented() {
        assertThat(
            PlainTime.of(19, 45, 30).with(CLOCK_HOUR_OF_AMPM.incremented()),
            is(PlainTime.of(20, 45, 30)));
    }

    @Test
    public void clockHourOfAmPmIncrementedIfHour24() {
        assertThat(
            PlainTime.of(24).with(CLOCK_HOUR_OF_AMPM.incremented()),
            is(PlainTime.of(1)));
    }

    @Test
    public void clockHourOfAmPmAtFloor() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(CLOCK_HOUR_OF_AMPM.atFloor()),
            is(PlainTime.of(19)));
    }

    @Test
    public void clockHourOfAmPmAtFloorOnTimestamp() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 19, 45, 30)
                .with(CLOCK_HOUR_OF_AMPM.atFloor()),
            is(PlainTimestamp.of(2014, 4, 21, 19, 0)));
    }

    @Test
    public void clockHourOfAmPmAtFloorIfHour24() {
        assertThat(
            PlainTime.of(24).with(CLOCK_HOUR_OF_AMPM.atFloor()),
            is(PlainTime.of(24)));
    }

    @Test
    public void clockHourOfAmPmAtCeiling() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(CLOCK_HOUR_OF_AMPM.atCeiling()),
            is(PlainTime.of(19, 59, 59, 999999999)));
    }

    @Test
    public void clockHourOfAmPmAtCeilingOnTimestamp() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 19, 45, 30)
                .with(CLOCK_HOUR_OF_AMPM.atCeiling()),
            is(
                PlainTimestamp.of(2014, 4, 21, 19, 59, 59)
                .with(NANO_OF_SECOND, 999999999)));
    }

    @Test
    public void clockHourOfAmPmAtCeilingIfHour24() {
        assertThat(
            PlainTime.of(24).with(CLOCK_HOUR_OF_AMPM.atCeiling()),
            is(PlainTime.of(24)));
    }

    @Test
    public void clockHourOfDayMinimized() {
        assertThat(
            PlainTime.of(19, 45, 30).with(CLOCK_HOUR_OF_DAY.minimized()),
            is(PlainTime.of(1, 45, 30)));
    }

    @Test
    public void clockHourOfDayMinimizedIfHour24() {
        assertThat(
            PlainTime.of(24).with(CLOCK_HOUR_OF_DAY.minimized()),
            is(PlainTime.of(1)));
    }

    @Test
    public void clockHourOfDayMaximized() {
        assertThat(
            PlainTime.of(19, 45, 30).with(CLOCK_HOUR_OF_DAY.maximized()),
            is(PlainTime.of(0, 45, 30)));
    }

    @Test
    public void clockHourOfDayMaximizedIfHour24() {
        assertThat(
            PlainTime.of(24).with(CLOCK_HOUR_OF_DAY.maximized()),
            is(PlainTime.of(0)));
    }

    @Test
    public void clockHourOfDayDecremented() {
        assertThat(
            PlainTime.of(19, 45, 30).with(CLOCK_HOUR_OF_DAY.decremented()),
            is(PlainTime.of(18, 45, 30)));
    }

    @Test
    public void clockHourOfDayDecrementedIfHour24() {
        assertThat(
            PlainTime.of(24).with(CLOCK_HOUR_OF_DAY.decremented()),
            is(PlainTime.of(23)));
    }

    @Test
    public void clockHourOfDayIncremented() {
        assertThat(
            PlainTime.of(19, 45, 30).with(CLOCK_HOUR_OF_DAY.incremented()),
            is(PlainTime.of(20, 45, 30)));
    }

    @Test
    public void clockHourOfDayIncrementedIfHour24() {
        assertThat(
            PlainTime.of(24).with(CLOCK_HOUR_OF_DAY.incremented()),
            is(PlainTime.of(1)));
    }

    @Test
    public void clockHourOfDayAtFloor() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(CLOCK_HOUR_OF_DAY.atFloor()),
            is(PlainTime.of(19)));
    }

    @Test
    public void clockHourOfDayAtFloorIfHour24() {
        assertThat(
            PlainTime.of(24).with(CLOCK_HOUR_OF_DAY.atFloor()),
            is(PlainTime.of(24)));
    }

    @Test
    public void clockHourOfDayAtCeiling() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(CLOCK_HOUR_OF_DAY.atCeiling()),
            is(PlainTime.of(19, 59, 59, 999999999)));
    }

    @Test
    public void clockHourOfDayAtCeilingIfHour24() {
        assertThat(
            PlainTime.of(24).with(CLOCK_HOUR_OF_DAY.atCeiling()),
            is(PlainTime.of(24)));
    }

    @Test
    public void digitalHourOfAmPmMinimized() {
        assertThat(
            PlainTime.of(19, 45, 30).with(DIGITAL_HOUR_OF_AMPM.minimized()),
            is(PlainTime.of(12, 45, 30)));
    }

    @Test
    public void digitalHourOfAmPmMinimizedIfHour24() {
        assertThat(
            PlainTime.of(24).with(DIGITAL_HOUR_OF_AMPM.minimized()),
            is(PlainTime.of(0)));
    }

    @Test
    public void digitalHourOfAmPmMaximized() {
        assertThat(
            PlainTime.of(19, 45, 30).with(DIGITAL_HOUR_OF_AMPM.maximized()),
            is(PlainTime.of(23, 45, 30)));
    }

    @Test
    public void digitalHourOfAmPmMaximizedIfHour24() {
        assertThat(
            PlainTime.of(24).with(DIGITAL_HOUR_OF_AMPM.maximized()),
            is(PlainTime.of(11)));
    }

    @Test
    public void digitalHourOfAmPmDecremented() {
        assertThat(
            PlainTime.of(19, 45, 30).with(DIGITAL_HOUR_OF_AMPM.decremented()),
            is(PlainTime.of(18, 45, 30)));
    }

    @Test
    public void digitalHourOfAmPmDecrementedIfHour24() {
        assertThat(
            PlainTime.of(24).with(DIGITAL_HOUR_OF_AMPM.decremented()),
            is(PlainTime.of(23)));
    }

    @Test
    public void digitalHourOfAmPmIncremented() {
        assertThat(
            PlainTime.of(19, 45, 30).with(DIGITAL_HOUR_OF_AMPM.incremented()),
            is(PlainTime.of(20, 45, 30)));
    }

    @Test
    public void digitalHourOfAmPmIncrementedIfHour24() {
        assertThat(
            PlainTime.of(24).with(DIGITAL_HOUR_OF_AMPM.incremented()),
            is(PlainTime.of(1)));
    }

    @Test
    public void digitalHourOfAmPmAtFloor() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(DIGITAL_HOUR_OF_AMPM.atFloor()),
            is(PlainTime.of(19)));
    }

    @Test
    public void digitalHourOfAmPmAtFloorIfHour24() {
        assertThat(
            PlainTime.of(24).with(DIGITAL_HOUR_OF_AMPM.atFloor()),
            is(PlainTime.of(24)));
    }

    @Test
    public void digitalHourOfAmPmAtCeiling() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(DIGITAL_HOUR_OF_AMPM.atCeiling()),
            is(PlainTime.of(19, 59, 59, 999999999)));
    }

    @Test
    public void digitalHourOfAmPmAtCeilingIfHour24() {
        assertThat(
            PlainTime.of(24).with(DIGITAL_HOUR_OF_AMPM.atCeiling()),
            is(PlainTime.of(24)));
    }

    @Test
    public void digitalHourOfDayMinimized() {
        assertThat(
            PlainTime.of(19, 45, 30).with(DIGITAL_HOUR_OF_DAY.minimized()),
            is(PlainTime.of(0, 45, 30)));
    }

    @Test
    public void digitalHourOfDayMinimizedOnTimestamp() {
        assertThat(
            PlainTimestamp.of(2014, 5, 7, 19, 45, 30)
                .with(DIGITAL_HOUR_OF_DAY.minimized()),
            is(PlainTimestamp.of(2014, 5, 7, 0, 45, 30)));
    }

    @Test
    public void digitalHourOfDayMinimizedIfHour24() {
        assertThat(
            PlainTime.of(24).with(DIGITAL_HOUR_OF_DAY.minimized()),
            is(PlainTime.of(0)));
    }

    @Test
    public void digitalHourOfDayMaximized() {
        assertThat(
            PlainTime.of(19, 45, 30).with(DIGITAL_HOUR_OF_DAY.maximized()),
            is(PlainTime.of(23, 45, 30)));
    }

    @Test
    public void digitalHourOfDayMaximizedOnTimestamp() {
        assertThat(
            PlainTimestamp.of(2014, 5, 7, 19, 45, 30)
                .with(DIGITAL_HOUR_OF_DAY.maximized()),
            is(PlainTimestamp.of(2014, 5, 7, 23, 45, 30)));
    }

    @Test
    public void digitalHourOfDayMaximizedIfHour24() {
        assertThat(
            PlainTime.of(24).with(DIGITAL_HOUR_OF_DAY.maximized()),
            is(PlainTime.of(23)));
    }

    @Test
    public void digitalHourOfDayDecremented() {
        assertThat(
            PlainTime.of(19, 45, 30).with(DIGITAL_HOUR_OF_DAY.decremented()),
            is(PlainTime.of(18, 45, 30)));
    }

    @Test
    public void digitalHourOfDayDecrementedOnTimestamp() {
        assertThat(
            PlainTimestamp.of(2014, 5, 7, 0, 45)
                .with(DIGITAL_HOUR_OF_DAY.decremented()),
            is(PlainTimestamp.of(2014, 5, 6, 23, 45)));
    }

    @Test
    public void digitalHourOfDayDecrementedIfHour24() {
        assertThat(
            PlainTime.of(24).with(DIGITAL_HOUR_OF_DAY.decremented()),
            is(PlainTime.of(23)));
    }

    @Test
    public void digitalHourOfDayIncremented() {
        assertThat(
            PlainTime.of(19, 45, 30).with(DIGITAL_HOUR_OF_DAY.incremented()),
            is(PlainTime.of(20, 45, 30)));
    }

    @Test
    public void digitalHourOfDayIncrementedOnTimestamp() {
        assertThat(
            PlainTimestamp.of(2014, 5, 7, 23, 45)
                .with(DIGITAL_HOUR_OF_DAY.incremented()),
            is(PlainTimestamp.of(2014, 5, 8, 0, 45)));
    }

    @Test
    public void digitalHourOfDayIncrementedIfHour24() {
        assertThat(
            PlainTime.of(24).with(DIGITAL_HOUR_OF_DAY.incremented()),
            is(PlainTime.of(1)));
    }

    @Test
    public void digitalHourOfDayAtFloor() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(DIGITAL_HOUR_OF_DAY.atFloor()),
            is(PlainTime.of(19)));
    }

    @Test
    public void digitalHourOfDayAtFloorIfHour24() {
        assertThat(
            PlainTime.of(24).with(DIGITAL_HOUR_OF_DAY.atFloor()),
            is(PlainTime.of(24)));
    }

    @Test
    public void digitalHourOfDayAtCeiling() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(DIGITAL_HOUR_OF_DAY.atCeiling()),
            is(PlainTime.of(19, 59, 59, 999999999)));
    }

    @Test
    public void digitalHourOfDayAtCeilingIfHour24() {
        assertThat(
            PlainTime.of(24).with(DIGITAL_HOUR_OF_DAY.atCeiling()),
            is(PlainTime.of(24)));
    }

    @Test
    public void hour0To24Minimized() {
        assertThat(
            PlainTime.of(19, 45, 30).with(HOUR_FROM_0_TO_24.minimized()),
            is(PlainTime.of(0, 45, 30)));
    }

    @Test
    public void hour0To24MinimizedOnTimestamp() {
        assertThat(
            PlainTimestamp.of(2014, 5, 7, 19, 45, 30)
                .with(HOUR_FROM_0_TO_24.minimized()),
            is(PlainTimestamp.of(2014, 5, 7, 0, 45, 30)));
    }

    @Test
    public void hour0To24MinimizedIfFullHour() {
        assertThat(
            PlainTime.of(13).with(HOUR_FROM_0_TO_24.minimized()),
            is(PlainTime.of(0)));
        assertThat(
            PlainTime.of(24).with(HOUR_FROM_0_TO_24.minimized()),
            is(PlainTime.of(0)));
    }

    @Test
    public void hour0To24Maximized() {
        assertThat(
            PlainTime.of(19, 45, 30).with(HOUR_FROM_0_TO_24.maximized()),
            is(PlainTime.of(23, 45, 30)));
    }

    @Test
    public void hour0To24MaximizedOnTimestamp() {
        assertThat(
            PlainTimestamp.of(2014, 5, 7, 19, 0)
                .with(HOUR_FROM_0_TO_24.maximized()),
            is(PlainTimestamp.of(2014, 5, 7, 23, 0)));
    }

    @Test
    public void hour0To24MaximizedIfFullHour() {
        assertThat(
            PlainTime.of(0).with(HOUR_FROM_0_TO_24.maximized()),
            is(PlainTime.of(24)));
        assertThat(
            PlainTime.of(10).with(HOUR_FROM_0_TO_24.maximized()),
            is(PlainTime.of(24)));
        assertThat(
            PlainTime.of(24).with(HOUR_FROM_0_TO_24.maximized()),
            is(PlainTime.of(24)));
    }

    @Test
    public void hour0To24Decremented() {
        assertThat(
            PlainTime.of(19, 45, 30).with(HOUR_FROM_0_TO_24.decremented()),
            is(PlainTime.of(18, 45, 30)));
    }

    @Test
    public void hour0To24DecrementedOnTimestamp() {
        assertThat(
            PlainTimestamp.of(2014, 5, 7, 0, 45, 30)
                .with(HOUR_FROM_0_TO_24.decremented()),
            is(PlainTimestamp.of(2014, 5, 6, 23, 45, 30)));
    }

    @Test
    public void hour0To24DecrementedIfFullHour() {
        assertThat(
            PlainTime.of(0).with(HOUR_FROM_0_TO_24.decremented()),
            is(PlainTime.of(23)));
        assertThat(
            PlainTime.of(1).with(HOUR_FROM_0_TO_24.decremented()),
            is(PlainTime.of(0)));
        assertThat(
            PlainTime.of(24).with(HOUR_FROM_0_TO_24.decremented()),
            is(PlainTime.of(23)));
    }

    @Test
    public void hour0To24Incremented() {
        assertThat(
            PlainTime.of(19, 45, 30).with(HOUR_FROM_0_TO_24.incremented()),
            is(PlainTime.of(20, 45, 30)));
    }

    @Test
    public void hour0To24IncrementedOnTimestamp() {
        assertThat(
            PlainTimestamp.of(2014, 5, 7, 23, 45, 30)
                .with(HOUR_FROM_0_TO_24.incremented()),
            is(PlainTimestamp.of(2014, 5, 8, 0, 45, 30)));
    }

    @Test
    public void hour0To24IncrementedIfFullHour() {
        assertThat(
            PlainTime.of(23).with(HOUR_FROM_0_TO_24.incremented()),
            is(PlainTime.of(24)));
        assertThat(
            PlainTime.of(24).with(HOUR_FROM_0_TO_24.incremented()),
            is(PlainTime.of(1)));
        assertThat(
            PlainTime.of(0).with(HOUR_FROM_0_TO_24.incremented()),
            is(PlainTime.of(1)));
    }

    @Test
    public void hour0To24AtFloor() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(HOUR_FROM_0_TO_24.atFloor()),
            is(PlainTime.of(19)));
    }

    @Test
    public void hour0To24AtFloorIfFullHour() {
        assertThat(
            PlainTime.of(19).with(HOUR_FROM_0_TO_24.atFloor()),
            is(PlainTime.of(19)));
        assertThat(
            PlainTime.of(24).with(HOUR_FROM_0_TO_24.atFloor()),
            is(PlainTime.of(24)));
    }

    @Test
    public void hour0To24AtCeiling() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(HOUR_FROM_0_TO_24.atCeiling()),
            is(PlainTime.of(19, 59, 59, 999999999)));
    }

    @Test
    public void hour0To24AtCeilingIfFullHour() {
        assertThat(
            PlainTime.of(19).with(HOUR_FROM_0_TO_24.atCeiling()),
            is(PlainTime.of(19, 59, 59, 999999999)));
        assertThat(
            PlainTime.of(24).with(HOUR_FROM_0_TO_24.atCeiling()),
            is(PlainTime.of(24)));
    }

    @Test
    public void minuteOfHourMinimized() {
        assertThat(
            PlainTime.of(19, 45, 30).with(MINUTE_OF_HOUR.minimized()),
            is(PlainTime.of(19, 0, 30)));
    }

    @Test
    public void minuteOfHourMinimizedIfFullMinute() {
        assertThat(
            PlainTime.of(24).with(MINUTE_OF_HOUR.minimized()),
            is(PlainTime.of(24)));
    }

    @Test
    public void minuteOfHourMaximized() {
        assertThat(
            PlainTime.of(19, 45, 30).with(MINUTE_OF_HOUR.maximized()),
            is(PlainTime.of(19, 59, 30)));
    }

    @Test
    public void minuteOfHourMaximizedIfFullMinute() {
        assertThat(
            PlainTime.of(19, 45).with(MINUTE_OF_HOUR.maximized()),
            is(PlainTime.of(19, 59)));
        assertThat(
            PlainTime.of(24).with(MINUTE_OF_HOUR.maximized()),
            is(PlainTime.of(24)));
    }

    @Test
    public void minuteOfHourDecremented() {
        assertThat(
            PlainTime.of(19, 45, 30).with(MINUTE_OF_HOUR.decremented()),
            is(PlainTime.of(19, 44, 30)));
        assertThat(
            PlainTime.of(0, 0, 30).with(MINUTE_OF_HOUR.decremented()),
            is(PlainTime.of(23, 59, 30)));
    }

    @Test
    public void minuteOfHourDecrementedIfFullMinute() {
        assertThat(
            PlainTime.of(0).with(MINUTE_OF_HOUR.decremented()),
            is(PlainTime.of(23, 59)));
        assertThat(
            PlainTime.of(0, 1).with(MINUTE_OF_HOUR.decremented()),
            is(PlainTime.of(0)));
        assertThat(
            PlainTime.of(24).with(MINUTE_OF_HOUR.decremented()),
            is(PlainTime.of(23, 59)));
    }

    @Test
    public void minuteOfHourIncremented() {
        assertThat(
            PlainTime.of(19, 45, 30).with(MINUTE_OF_HOUR.incremented()),
            is(PlainTime.of(19, 46, 30)));
        assertThat(
            PlainTime.of(23, 59, 30).with(MINUTE_OF_HOUR.incremented()),
            is(PlainTime.of(0, 0, 30)));
    }

    @Test
    public void minuteOfHourIncrementedIfFullMinute() {
        assertThat(
            PlainTime.of(23, 59).with(MINUTE_OF_HOUR.incremented()),
            is(PlainTime.of(24)));
        assertThat(
            PlainTime.of(24).with(MINUTE_OF_HOUR.incremented()),
            is(PlainTime.of(0, 1)));
        assertThat(
            PlainTime.of(0).with(MINUTE_OF_HOUR.incremented()),
            is(PlainTime.of(0, 1)));
    }

    @Test
    public void minuteOfHourAtFloor() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(MINUTE_OF_HOUR.atFloor()),
            is(PlainTime.of(19, 45)));
    }

    @Test
    public void minuteOfHourAtFloorIfFullMinute() {
        assertThat(
            PlainTime.of(19, 45).with(MINUTE_OF_HOUR.atFloor()),
            is(PlainTime.of(19, 45)));
        assertThat(
            PlainTime.of(24).with(MINUTE_OF_HOUR.atFloor()),
            is(PlainTime.of(24)));
    }

    @Test
    public void minuteOfHourAtCeiling() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(MINUTE_OF_HOUR.atCeiling()),
            is(PlainTime.of(19, 45, 59, 999999999)));
    }

    @Test
    public void minuteOfHourAtCeilingIfFullMinute() {
        assertThat(
            PlainTime.of(19, 45).with(MINUTE_OF_HOUR.atCeiling()),
            is(PlainTime.of(19, 45, 59, 999999999)));
        assertThat(
            PlainTime.of(24).with(MINUTE_OF_HOUR.atCeiling()),
            is(PlainTime.of(24)));
    }

    @Test
    public void minuteOfDayMinimized() {
        assertThat(
            PlainTime.of(19, 45, 30).with(MINUTE_OF_DAY.minimized()),
            is(PlainTime.of(0, 0, 30)));
    }

    @Test
    public void minuteOfDayMinimizedIfFullMinute() {
        assertThat(
            PlainTime.of(24).with(MINUTE_OF_DAY.minimized()),
            is(PlainTime.of(0)));
    }

    @Test
    public void minuteOfDayMaximized() {
        assertThat(
            PlainTime.of(19, 45, 30).with(MINUTE_OF_DAY.maximized()),
            is(PlainTime.of(23, 59, 30)));
    }

    @Test
    public void minuteOfDayMaximizedIfFullMinute() {
        assertThat(
            PlainTime.of(10, 15).with(MINUTE_OF_DAY.maximized()),
            is(PlainTime.of(24)));
        assertThat(
            PlainTime.of(24).with(MINUTE_OF_DAY.maximized()),
            is(PlainTime.of(24)));
    }

    @Test
    public void minuteOfDayDecremented() {
        assertThat(
            PlainTime.of(19, 45, 30).with(MINUTE_OF_DAY.decremented()),
            is(PlainTime.of(19, 44, 30)));
        assertThat(
            PlainTime.of(0, 0, 30).with(MINUTE_OF_DAY.decremented()),
            is(PlainTime.of(23, 59, 30)));
    }

    @Test
    public void minuteOfDayDecrementedIfFullMinute() {
        assertThat(
            PlainTime.of(0).with(MINUTE_OF_DAY.decremented()),
            is(PlainTime.of(23, 59)));
        assertThat(
            PlainTime.of(0, 1).with(MINUTE_OF_DAY.decremented()),
            is(PlainTime.of(0)));
        assertThat(
            PlainTime.of(24).with(MINUTE_OF_DAY.decremented()),
            is(PlainTime.of(23, 59)));
    }

    @Test
    public void minuteOfDayIncremented() {
        assertThat(
            PlainTime.of(19, 45, 30).with(MINUTE_OF_DAY.incremented()),
            is(PlainTime.of(19, 46, 30)));
        assertThat(
            PlainTime.of(23, 59, 30).with(MINUTE_OF_DAY.incremented()),
            is(PlainTime.of(0, 0, 30)));
    }

    @Test
    public void minuteOfDayIncrementedIfFullMinute() {
        assertThat(
            PlainTime.of(23, 59).with(MINUTE_OF_DAY.incremented()),
            is(PlainTime.of(24)));
        assertThat(
            PlainTime.of(24).with(MINUTE_OF_DAY.incremented()),
            is(PlainTime.of(0, 1)));
        assertThat(
            PlainTime.of(0).with(MINUTE_OF_DAY.incremented()),
            is(PlainTime.of(0, 1)));
    }

    @Test
    public void minuteOfDayAtFloor() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(MINUTE_OF_DAY.atFloor()),
            is(PlainTime.of(19, 45)));
    }

    @Test
    public void minuteOfDayAtFloorIfFullMinute() {
        assertThat(
            PlainTime.of(19, 45).with(MINUTE_OF_DAY.atFloor()),
            is(PlainTime.of(19, 45)));
        assertThat(
            PlainTime.of(24).with(MINUTE_OF_DAY.atFloor()),
            is(PlainTime.of(24)));
    }

    @Test
    public void minuteOfDayAtCeiling() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(MINUTE_OF_DAY.atCeiling()),
            is(PlainTime.of(19, 45, 59, 999999999)));
    }

    @Test
    public void minuteOfDayAtCeilingIfFullMinute() {
        assertThat(
            PlainTime.of(19, 45).with(MINUTE_OF_DAY.atCeiling()),
            is(PlainTime.of(19, 45, 59, 999999999)));
        assertThat(
            PlainTime.of(24).with(MINUTE_OF_DAY.atCeiling()),
            is(PlainTime.of(24)));
    }

    @Test
    public void secondOfMinuteMinimized() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(SECOND_OF_MINUTE.minimized()),
            is(PlainTime.of(19, 45, 0, 123456789)));
    }

    @Test
    public void secondOfMinuteMinimizedIfFullSecond() {
        assertThat(
            PlainTime.of(24).with(SECOND_OF_MINUTE.minimized()),
            is(PlainTime.of(24)));
    }

    @Test
    public void secondOfMinuteMaximized() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(SECOND_OF_MINUTE.maximized()),
            is(PlainTime.of(19, 45, 59, 123456789)));
    }

    @Test
    public void secondOfMinuteMaximizedIfFullSecond() {
        assertThat(
            PlainTime.of(19, 45, 30).with(SECOND_OF_MINUTE.maximized()),
            is(PlainTime.of(19, 45, 59)));
        assertThat(
            PlainTime.of(24).with(SECOND_OF_MINUTE.maximized()),
            is(PlainTime.of(24)));
    }

    @Test
    public void secondOfMinuteDecremented() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(SECOND_OF_MINUTE.decremented()),
            is(PlainTime.of(19, 45, 29, 123456789)));
        assertThat(
            PlainTime.of(0, 0, 0, 123456789)
                .with(SECOND_OF_MINUTE.decremented()),
            is(PlainTime.of(23, 59, 59, 123456789)));
    }

    @Test
    public void secondOfMinuteDecrementedIfFullSecond() {
        assertThat(
            PlainTime.of(0).with(SECOND_OF_MINUTE.decremented()),
            is(PlainTime.of(23, 59, 59)));
        assertThat(
            PlainTime.of(0, 0, 1).with(SECOND_OF_MINUTE.decremented()),
            is(PlainTime.of(0)));
        assertThat(
            PlainTime.of(24).with(SECOND_OF_MINUTE.decremented()),
            is(PlainTime.of(23, 59, 59)));
    }

    @Test
    public void secondOfMinuteIncremented() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(SECOND_OF_MINUTE.incremented()),
            is(PlainTime.of(19, 45, 31, 123456789)));
        assertThat(
            PlainTime.of(23, 59, 59, 123456789)
                .with(SECOND_OF_MINUTE.incremented()),
            is(PlainTime.of(0, 0, 0, 123456789)));
    }

    @Test
    public void secondOfMinuteIncrementedIfFullSecond() {
        assertThat(
            PlainTime.of(23, 59, 59).with(SECOND_OF_MINUTE.incremented()),
            is(PlainTime.of(24)));
        assertThat(
            PlainTime.of(24).with(SECOND_OF_MINUTE.incremented()),
            is(PlainTime.of(0, 0, 1)));
        assertThat(
            PlainTime.of(0).with(SECOND_OF_MINUTE.incremented()),
            is(PlainTime.of(0, 0, 1)));
    }

    @Test
    public void secondOfMinuteAtFloor() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(SECOND_OF_MINUTE.atFloor()),
            is(PlainTime.of(19, 45, 30)));
    }

    @Test
    public void secondOfMinuteAtFloorIfFullSecond() {
        assertThat(
            PlainTime.of(19, 45, 30).with(SECOND_OF_MINUTE.atFloor()),
            is(PlainTime.of(19, 45, 30)));
        assertThat(
            PlainTime.of(24).with(SECOND_OF_MINUTE.atFloor()),
            is(PlainTime.of(24)));
    }

    @Test
    public void secondOfMinuteAtCeiling() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(SECOND_OF_MINUTE.atCeiling()),
            is(PlainTime.of(19, 45, 30, 999999999)));
    }

    @Test
    public void secondOfMinuteAtCeilingIfFullSecond() {
        assertThat(
            PlainTime.of(19, 45, 30).with(SECOND_OF_MINUTE.atCeiling()),
            is(PlainTime.of(19, 45, 30, 999999999)));
        assertThat(
            PlainTime.of(24).with(SECOND_OF_MINUTE.atCeiling()),
            is(PlainTime.of(24)));
    }

    @Test
    public void secondOfDayMinimized() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789).with(SECOND_OF_DAY.minimized()),
            is(PlainTime.of(0, 0, 0, 123456789)));
    }

    @Test
    public void secondOfDayMinimizedIfFullSecond() {
        assertThat(
            PlainTime.of(19, 45, 30).with(SECOND_OF_DAY.minimized()),
            is(PlainTime.of(0)));
        assertThat(
            PlainTime.of(24).with(SECOND_OF_DAY.minimized()),
            is(PlainTime.of(0)));
    }

    @Test
    public void secondOfDayMaximized() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789).with(SECOND_OF_DAY.maximized()),
            is(PlainTime.of(23, 59, 59, 123456789)));
    }

    @Test
    public void secondOfDayMaximizedIfFullSecond() {
        assertThat(
            PlainTime.of(19, 45, 30).with(SECOND_OF_DAY.maximized()),
            is(PlainTime.of(24)));
        assertThat(
            PlainTime.of(24).with(SECOND_OF_DAY.maximized()),
            is(PlainTime.of(24)));
    }

    @Test
    public void secondOfDayDecremented() {
        assertThat(
            PlainTime.of(19, 45, 30).with(SECOND_OF_DAY.decremented()),
            is(PlainTime.of(19, 45, 29)));
        assertThat(
            PlainTime.of(0, 0, 0, 123456789).with(SECOND_OF_DAY.decremented()),
            is(PlainTime.of(23, 59, 59, 123456789)));
    }

    @Test
    public void secondOfDayDecrementedIfFullSecond() {
        assertThat(
            PlainTime.of(0).with(SECOND_OF_DAY.decremented()),
            is(PlainTime.of(23, 59, 59)));
        assertThat(
            PlainTime.of(0, 0, 1).with(SECOND_OF_DAY.decremented()),
            is(PlainTime.of(0)));
        assertThat(
            PlainTime.of(24).with(SECOND_OF_DAY.decremented()),
            is(PlainTime.of(23, 59, 59)));
    }

    @Test
    public void secondOfDayIncremented() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(SECOND_OF_DAY.incremented()),
            is(PlainTime.of(19, 45, 31, 123456789)));
        assertThat(
            PlainTime.of(23, 59, 59, 123456789)
                .with(SECOND_OF_DAY.incremented()),
            is(PlainTime.of(0, 0, 0, 123456789)));
    }

    @Test
    public void secondOfDayIncrementedIfFullSecond() {
        assertThat(
            PlainTime.of(23, 59, 59).with(SECOND_OF_DAY.incremented()),
            is(PlainTime.of(24)));
        assertThat(
            PlainTime.of(24).with(SECOND_OF_DAY.incremented()),
            is(PlainTime.of(0, 0, 1)));
        assertThat(
            PlainTime.of(0).with(SECOND_OF_DAY.incremented()),
            is(PlainTime.of(0, 0, 1)));
    }

    @Test
    public void secondOfDayAtFloor() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(SECOND_OF_DAY.atFloor()),
            is(PlainTime.of(19, 45, 30)));
    }

    @Test
    public void secondOfDayAtFloorIfFullSecond() {
        assertThat(
            PlainTime.of(19, 45, 30).with(SECOND_OF_DAY.atFloor()),
            is(PlainTime.of(19, 45, 30)));
        assertThat(
            PlainTime.of(24).with(SECOND_OF_DAY.atFloor()),
            is(PlainTime.of(24)));
    }

    @Test
    public void secondOfDayAtCeiling() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(SECOND_OF_DAY.atCeiling()),
            is(PlainTime.of(19, 45, 30, 999999999)));
    }

    @Test
    public void secondOfDayAtCeilingIfFullSecond() {
        assertThat(
            PlainTime.of(19, 45, 30).with(SECOND_OF_DAY.atCeiling()),
            is(PlainTime.of(19, 45, 30, 999999999)));
        assertThat(
            PlainTime.of(24).with(SECOND_OF_DAY.atCeiling()),
            is(PlainTime.of(24)));
    }

    @Test
    public void milliOfSecondMinimized() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(MILLI_OF_SECOND.minimized()),
            is(PlainTime.of(19, 45, 30, 456789)));
    }

    @Test
    public void milliOfSecondMinimizedIfFullFraction() {
        assertThat(
            PlainTime.of(24).with(MILLI_OF_SECOND.minimized()),
            is(PlainTime.of(24)));
    }

    @Test
    public void milliOfSecondMaximized() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(MILLI_OF_SECOND.maximized()),
            is(PlainTime.of(19, 45, 30, 999456789)));
    }

    @Test
    public void milliOfSecondMaximizedIfFullFraction() {
        assertThat(
            PlainTime.of(19, 45, 30, 123000000)
                .with(MILLI_OF_SECOND.maximized()),
            is(PlainTime.of(19, 45, 30, 999000000)));
        assertThat(
            PlainTime.of(24).with(MILLI_OF_SECOND.maximized()),
            is(PlainTime.of(24)));
    }

    @Test
    public void milliOfSecondDecremented() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(MILLI_OF_SECOND.decremented()),
            is(PlainTime.of(19, 45, 30, 122456789)));
        assertThat(
            PlainTime.of(0, 0, 0, 456789)
                .with(MILLI_OF_SECOND.decremented()),
            is(PlainTime.of(23, 59, 59, 999456789)));
    }

    @Test
    public void milliOfSecondDecrementedIfFullFraction() {
        assertThat(
            PlainTime.of(0).with(MILLI_OF_SECOND.decremented()),
            is(PlainTime.of(23, 59, 59, 999000000)));
        assertThat(
            PlainTime.of(0, 0, 0, 1000000).with(MILLI_OF_SECOND.decremented()),
            is(PlainTime.of(0)));
        assertThat(
            PlainTime.of(24).with(MILLI_OF_SECOND.decremented()),
            is(PlainTime.of(23, 59, 59, 999000000)));
    }

    @Test
    public void milliOfSecondIncremented() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(MILLI_OF_SECOND.incremented()),
            is(PlainTime.of(19, 45, 30, 124456789)));
        assertThat(
            PlainTime.of(23, 59, 59, 999456789)
                .with(MILLI_OF_SECOND.incremented()),
            is(PlainTime.of(0, 0, 0, 456789)));
    }

    @Test
    public void milliOfSecondIncrementedIfFullFraction() {
        assertThat(
            PlainTime.of(23, 59, 59, 999000000)
                .with(MILLI_OF_SECOND.incremented()),
            is(PlainTime.of(24)));
        assertThat(
            PlainTime.of(24).with(MILLI_OF_SECOND.incremented()),
            is(PlainTime.of(0, 0, 0, 1000000)));
        assertThat(
            PlainTime.of(0).with(MILLI_OF_SECOND.incremented()),
            is(PlainTime.of(0, 0, 0, 1000000)));
    }

    @Test
    public void milliOfSecondAtFloor() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(MILLI_OF_SECOND.atFloor()),
            is(PlainTime.of(19, 45, 30, 123000000)));
    }

    @Test
    public void milliOfSecondAtFloorIfFullFraction() {
        assertThat(
            PlainTime.of(19, 45, 30, 123000000)
                .with(MILLI_OF_SECOND.atFloor()),
            is(PlainTime.of(19, 45, 30, 123000000)));
        assertThat(
            PlainTime.of(24).with(MILLI_OF_SECOND.atFloor()),
            is(PlainTime.of(24)));
    }

    @Test
    public void milliOfSecondAtCeiling() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(MILLI_OF_SECOND.atCeiling()),
            is(PlainTime.of(19, 45, 30, 123999999)));
    }

    @Test
    public void milliOfSecondAtCeilingIfFullFraction() {
        assertThat(
            PlainTime.of(19, 45, 30, 123000000)
                .with(MILLI_OF_SECOND.atCeiling()),
            is(PlainTime.of(19, 45, 30, 123999999)));
        assertThat(
            PlainTime.of(24).with(MILLI_OF_SECOND.atCeiling()),
            is(PlainTime.of(24)));
    }

    @Test
    public void milliOfDayMinimized() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789).with(MILLI_OF_DAY.minimized()),
            is(PlainTime.of(0, 0, 0, 456789)));
    }

    @Test
    public void milliOfDayMinimizedIfFullFraction() {
        assertThat(
            PlainTime.of(19, 45, 30, 123000000).with(MILLI_OF_DAY.minimized()),
            is(PlainTime.of(0)));
        assertThat(
            PlainTime.of(24).with(MILLI_OF_DAY.minimized()),
            is(PlainTime.of(0)));
    }

    @Test
    public void milliOfDayMaximized() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789).with(MILLI_OF_DAY.maximized()),
            is(PlainTime.of(23, 59, 59, 999456789)));
    }

    @Test
    public void milliOfDayMaximizedIfFullFraction() {
        assertThat(
            PlainTime.of(19, 45, 30, 123000000).with(MILLI_OF_DAY.maximized()),
            is(PlainTime.of(24)));
        assertThat(
            PlainTime.of(24).with(MILLI_OF_DAY.maximized()),
            is(PlainTime.of(24)));
    }

    @Test
    public void milliOfDayDecremented() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(MILLI_OF_DAY.decremented()),
            is(PlainTime.of(19, 45, 30, 122456789)));
        assertThat(
            PlainTime.of(0, 0, 0, 456789).with(MILLI_OF_DAY.decremented()),
            is(PlainTime.of(23, 59, 59, 999456789)));
    }

    @Test
    public void milliOfDayDecrementedIfFullFraction() {
        assertThat(
            PlainTime.of(0).with(MILLI_OF_DAY.decremented()),
            is(PlainTime.of(23, 59, 59, 999000000)));
        assertThat(
            PlainTime.of(0, 0, 0, 1000000).with(MILLI_OF_DAY.decremented()),
            is(PlainTime.of(0)));
        assertThat(
            PlainTime.of(24).with(MILLI_OF_DAY.decremented()),
            is(PlainTime.of(23, 59, 59, 999000000)));
    }

    @Test
    public void milliOfDayIncremented() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(MILLI_OF_DAY.incremented()),
            is(PlainTime.of(19, 45, 30, 124456789)));
        assertThat(
            PlainTime.of(23, 59, 59, 999456789)
                .with(MILLI_OF_DAY.incremented()),
            is(PlainTime.of(0, 0, 0, 456789)));
    }

    @Test
    public void milliOfDayIncrementedIfFullFraction() {
        assertThat(
            PlainTime.of(23, 59, 59, 999000000)
                .with(MILLI_OF_DAY.incremented()),
            is(PlainTime.of(24)));
        assertThat(
            PlainTime.of(24).with(MILLI_OF_DAY.incremented()),
            is(PlainTime.of(0, 0, 0, 1000000)));
        assertThat(
            PlainTime.of(0).with(MILLI_OF_DAY.incremented()),
            is(PlainTime.of(0, 0, 0, 1000000)));
    }

    @Test
    public void milliOfDayAtFloor() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(MILLI_OF_DAY.atFloor()),
            is(PlainTime.of(19, 45, 30, 123000000)));
    }

    @Test
    public void milliOfDayAtFloorIfFullFraction() {
        assertThat(
            PlainTime.of(19, 45, 30, 123000000).with(MILLI_OF_DAY.atFloor()),
            is(PlainTime.of(19, 45, 30, 123000000)));
        assertThat(
            PlainTime.of(24).with(MILLI_OF_DAY.atFloor()),
            is(PlainTime.of(24)));
    }

    @Test
    public void milliOfDayAtCeiling() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(MILLI_OF_DAY.atCeiling()),
            is(PlainTime.of(19, 45, 30, 123999999)));
    }

    @Test
    public void milliOfDayAtCeilingIfFullFraction() {
        assertThat(
            PlainTime.of(19, 45, 30, 123000000).with(MILLI_OF_DAY.atCeiling()),
            is(PlainTime.of(19, 45, 30, 123999999)));
        assertThat(
            PlainTime.of(24).with(MILLI_OF_DAY.atCeiling()),
            is(PlainTime.of(24)));
    }

    @Test
    public void microOfSecondMinimized() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(MICRO_OF_SECOND.minimized()),
            is(PlainTime.of(19, 45, 30, 789)));
    }

    @Test
    public void microOfSecondMinimizedIfFullFraction() {
        assertThat(
            PlainTime.of(24).with(MICRO_OF_SECOND.minimized()),
            is(PlainTime.of(24)));
    }

    @Test
    public void microOfSecondMaximized() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(MICRO_OF_SECOND.maximized()),
            is(PlainTime.of(19, 45, 30, 999999789)));
    }

    @Test
    public void microOfSecondMaximizedIfFullFraction() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456000)
                .with(MICRO_OF_SECOND.maximized()),
            is(PlainTime.of(19, 45, 30, 999999000)));
        assertThat(
            PlainTime.of(24).with(MICRO_OF_SECOND.maximized()),
            is(PlainTime.of(24)));
    }

    @Test
    public void microOfSecondDecremented() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(MICRO_OF_SECOND.decremented()),
            is(PlainTime.of(19, 45, 30, 123455789)));
        assertThat(
            PlainTime.of(0, 0, 0, 789)
                .with(MICRO_OF_SECOND.decremented()),
            is(PlainTime.of(23, 59, 59, 999999789)));
    }

    @Test
    public void microOfSecondDecrementedIfFullFraction() {
        assertThat(
            PlainTime.of(0).with(MICRO_OF_SECOND.decremented()),
            is(PlainTime.of(23, 59, 59, 999999000)));
        assertThat(
            PlainTime.of(0, 0, 0, 1000).with(MICRO_OF_SECOND.decremented()),
            is(PlainTime.of(0)));
        assertThat(
            PlainTime.of(24).with(MICRO_OF_SECOND.decremented()),
            is(PlainTime.of(23, 59, 59, 999999000)));
    }

    @Test
    public void microOfSecondIncremented() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(MICRO_OF_SECOND.incremented()),
            is(PlainTime.of(19, 45, 30, 123457789)));
        assertThat(
            PlainTime.of(23, 59, 59, 999999789)
                .with(MICRO_OF_SECOND.incremented()),
            is(PlainTime.of(0, 0, 0, 789)));
    }

    @Test
    public void microOfSecondIncrementedIfFullFraction() {
        assertThat(
            PlainTime.of(23, 59, 59, 999999000)
                .with(MICRO_OF_SECOND.incremented()),
            is(PlainTime.of(24)));
        assertThat(
            PlainTime.of(24).with(MICRO_OF_SECOND.incremented()),
            is(PlainTime.of(0, 0, 0, 1000)));
        assertThat(
            PlainTime.of(0).with(MICRO_OF_SECOND.incremented()),
            is(PlainTime.of(0, 0, 0, 1000)));
    }

    @Test
    public void microOfSecondAtFloor() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(MICRO_OF_SECOND.atFloor()),
            is(PlainTime.of(19, 45, 30, 123456000)));
    }

    @Test
    public void microOfSecondAtFloorIfFullFraction() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456000)
                .with(MICRO_OF_SECOND.atFloor()),
            is(PlainTime.of(19, 45, 30, 123456000)));
        assertThat(
            PlainTime.of(24).with(MICRO_OF_SECOND.atFloor()),
            is(PlainTime.of(24)));
    }

    @Test
    public void microOfSecondAtCeiling() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(MICRO_OF_SECOND.atCeiling()),
            is(PlainTime.of(19, 45, 30, 123456999)));
    }

    @Test
    public void microOfSecondAtCeilingIfFullFraction() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456000)
                .with(MICRO_OF_SECOND.atCeiling()),
            is(PlainTime.of(19, 45, 30, 123456999)));
        assertThat(
            PlainTime.of(24).with(MICRO_OF_SECOND.atCeiling()),
            is(PlainTime.of(24)));
    }

    @Test
    public void microOfDayMinimized() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789).with(MICRO_OF_DAY.minimized()),
            is(PlainTime.of(0, 0, 0, 789)));
    }

    @Test
    public void microOfDayMinimizedIfFullFraction() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456000).with(MICRO_OF_DAY.minimized()),
            is(PlainTime.of(0)));
        assertThat(
            PlainTime.of(24).with(MICRO_OF_DAY.minimized()),
            is(PlainTime.of(0)));
    }

    @Test
    public void microOfDayMaximized() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789).with(MICRO_OF_DAY.maximized()),
            is(PlainTime.of(23, 59, 59, 999999789)));
    }

    @Test
    public void microOfDayMaximizedIfFullFraction() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456000).with(MICRO_OF_DAY.maximized()),
            is(PlainTime.of(24)));
        assertThat(
            PlainTime.of(24).with(MICRO_OF_DAY.maximized()),
            is(PlainTime.of(24)));
    }

    @Test
    public void microOfDayDecremented() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(MICRO_OF_DAY.decremented()),
            is(PlainTime.of(19, 45, 30, 123455789)));
        assertThat(
            PlainTime.of(0, 0, 0, 789).with(MICRO_OF_DAY.decremented()),
            is(PlainTime.of(23, 59, 59, 999999789)));
    }

    @Test
    public void microOfDayDecrementedIfFullFraction() {
        assertThat(
            PlainTime.of(0).with(MICRO_OF_DAY.decremented()),
            is(PlainTime.of(23, 59, 59, 999999000)));
        assertThat(
            PlainTime.of(0, 0, 0, 1000).with(MICRO_OF_DAY.decremented()),
            is(PlainTime.of(0)));
        assertThat(
            PlainTime.of(24).with(MICRO_OF_DAY.decremented()),
            is(PlainTime.of(23, 59, 59, 999999000)));
    }

    @Test
    public void microOfDayIncremented() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(MICRO_OF_DAY.incremented()),
            is(PlainTime.of(19, 45, 30, 123457789)));
        assertThat(
            PlainTime.of(23, 59, 59, 999999789)
                .with(MICRO_OF_DAY.incremented()),
            is(PlainTime.of(0, 0, 0, 789)));
    }

    @Test
    public void microOfDayIncrementedIfFullFraction() {
        assertThat(
            PlainTime.of(23, 59, 59, 999999000)
                .with(MICRO_OF_DAY.incremented()),
            is(PlainTime.of(24)));
        assertThat(
            PlainTime.of(24).with(MICRO_OF_DAY.incremented()),
            is(PlainTime.of(0, 0, 0, 1000)));
        assertThat(
            PlainTime.of(0).with(MICRO_OF_DAY.incremented()),
            is(PlainTime.of(0, 0, 0, 1000)));
    }

    @Test
    public void microOfDayAtFloor() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(MICRO_OF_DAY.atFloor()),
            is(PlainTime.of(19, 45, 30, 123456000)));
    }

    @Test
    public void microOfDayAtFloorIfFullFraction() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456000).with(MICRO_OF_DAY.atFloor()),
            is(PlainTime.of(19, 45, 30, 123456000)));
        assertThat(
            PlainTime.of(24).with(MICRO_OF_DAY.atFloor()),
            is(PlainTime.of(24)));
    }

    @Test
    public void microOfDayAtCeiling() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(MICRO_OF_DAY.atCeiling()),
            is(PlainTime.of(19, 45, 30, 123456999)));
    }

    @Test
    public void microOfDayAtCeilingIfFullFraction() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456000).with(MICRO_OF_DAY.atCeiling()),
            is(PlainTime.of(19, 45, 30, 123456999)));
        assertThat(
            PlainTime.of(24).with(MICRO_OF_DAY.atCeiling()),
            is(PlainTime.of(24)));
    }

    @Test
    public void nanoOfSecondMinimized() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(NANO_OF_SECOND.minimized()),
            is(PlainTime.of(19, 45, 30)));
    }

    @Test
    public void nanoOfSecondMinimizedIfHour24() {
        assertThat(
            PlainTime.of(24).with(NANO_OF_SECOND.minimized()),
            is(PlainTime.of(24)));
    }

    @Test
    public void nanoOfSecondMaximized() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(NANO_OF_SECOND.maximized()),
            is(PlainTime.of(19, 45, 30, 999999999)));
    }

    @Test
    public void nanoOfSecondMaximizedIfHour24() {
        assertThat(
            PlainTime.of(24).with(NANO_OF_SECOND.maximized()),
            is(PlainTime.of(24)));
    }

    @Test
    public void nanoOfSecondDecremented() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(NANO_OF_SECOND.decremented()),
            is(PlainTime.of(19, 45, 30, 123456788)));
        assertThat(
            PlainTime.of(0)
                .with(NANO_OF_SECOND.decremented()),
            is(PlainTime.of(23, 59, 59, 999999999)));
    }

    @Test
    public void nanoOfSecondDecrementedIfHour24() {
        assertThat(
            PlainTime.of(24).with(NANO_OF_SECOND.decremented()),
            is(PlainTime.of(23, 59, 59, 999999999)));
    }

    @Test
    public void nanoOfSecondIncremented() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(NANO_OF_SECOND.incremented()),
            is(PlainTime.of(19, 45, 30, 123456790)));
        assertThat(
            PlainTime.of(23, 59, 59, 999999999)
                .with(NANO_OF_SECOND.incremented()),
            is(PlainTime.of(24)));
    }

    @Test
    public void nanoOfSecondIncrementedIfHour24() {
        assertThat(
            PlainTime.of(24).with(NANO_OF_SECOND.incremented()),
            is(PlainTime.of(0, 0, 0, 1)));
    }

    @Test
    public void nanoOfSecondAtFloor() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(NANO_OF_SECOND.atFloor()),
            is(PlainTime.of(19, 45, 30, 123456789)));
    }

    @Test
    public void nanoOfSecondAtFloorIfHour24() {
        assertThat(
            PlainTime.of(24).with(NANO_OF_SECOND.atFloor()),
            is(PlainTime.of(24)));
    }

    @Test
    public void nanoOfSecondAtCeiling() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(NANO_OF_SECOND.atCeiling()),
            is(PlainTime.of(19, 45, 30, 123456789)));
    }

    @Test
    public void nanoOfSecondAtCeilingIfHour24() {
        assertThat(
            PlainTime.of(24).with(NANO_OF_SECOND.atCeiling()),
            is(PlainTime.of(24)));
    }

    @Test
    public void nanoOfDayMinimized() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789).with(NANO_OF_DAY.minimized()),
            is(PlainTime.of(0)));
    }

    @Test
    public void nanoOfDayMinimizedIfHour24() {
        assertThat(
            PlainTime.of(24).with(NANO_OF_DAY.minimized()),
            is(PlainTime.of(0)));
    }

    @Test
    public void nanoOfDayMaximized() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789).with(NANO_OF_DAY.maximized()),
            is(PlainTime.of(24)));
    }

    @Test
    public void nanoOfDayMaximizedIfHour24() {
        assertThat(
            PlainTime.of(24).with(NANO_OF_DAY.maximized()),
            is(PlainTime.of(24)));
    }

    @Test
    public void nanoOfDayDecremented() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(NANO_OF_DAY.decremented()),
            is(PlainTime.of(19, 45, 30, 123456788)));
        assertThat(
            PlainTime.of(0).with(NANO_OF_DAY.decremented()),
            is(PlainTime.of(23, 59, 59, 999999999)));
    }

    @Test
    public void nanoOfDayDecrementedIfHour24() {
        assertThat(
            PlainTime.of(24).with(NANO_OF_DAY.decremented()),
            is(PlainTime.of(23, 59, 59, 999999999)));
    }

    @Test
    public void nanoOfDayIncremented() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(NANO_OF_DAY.incremented()),
            is(PlainTime.of(19, 45, 30, 123456790)));
        assertThat(
            PlainTime.of(23, 59, 59, 999999999)
                .with(NANO_OF_DAY.incremented()),
            is(PlainTime.of(24)));
    }

    @Test
    public void nanoOfDayIncrementedIfHour24() {
        assertThat(
            PlainTime.of(24).with(NANO_OF_DAY.incremented()),
            is(PlainTime.of(0, 0, 0, 1)));
    }

    @Test
    public void nanoOfDayAtFloor() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(NANO_OF_DAY.atFloor()),
            is(PlainTime.of(19, 45, 30, 123456789)));
    }

    @Test
    public void nanoOfDayAtFloorIfHour24() {
        assertThat(
            PlainTime.of(24).with(NANO_OF_DAY.atFloor()),
            is(PlainTime.of(24)));
    }

    @Test
    public void nanoOfDayAtCeiling() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789)
                .with(NANO_OF_DAY.atCeiling()),
            is(PlainTime.of(19, 45, 30, 123456789)));
    }

    @Test
    public void nanoOfDayAtCeilingIfHour24() {
        assertThat(
            PlainTime.of(24).with(NANO_OF_DAY.atCeiling()),
            is(PlainTime.of(24)));
    }

    @Test
    public void combinedOperators() {
        PlainTime time = PlainTime.nowInSystemTime();
        assertThat(
            time.with(PlainTime.DIGITAL_HOUR_OF_DAY.atFloor(7)),
            is(PlainTime.of(7, 0)));
        assertThat(
            time.with(PlainTime.DIGITAL_HOUR_OF_DAY.atCeiling(7)),
            is(PlainTime.of(7, 59, 59, 999_999_999)));
    }

}