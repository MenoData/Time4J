package net.time4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.PlainTime.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class TimeElementTest {

    @Test
    public void wallTimeName() {
        assertThat(WALL_TIME.name(), is("WALL_TIME"));
    }

    @Test
    public void amPmOfDayName() {
        assertThat(AM_PM_OF_DAY.name(), is("AM_PM_OF_DAY"));
    }

    @Test
    public void precisionName() {
        assertThat(PRECISION.name(), is("PRECISION"));
    }

    @Test
    public void clockHourOfAmPmName() {
        assertThat(CLOCK_HOUR_OF_AMPM.name(), is("CLOCK_HOUR_OF_AMPM"));
    }

    @Test
    public void digitalHourOfAmPmName() {
        assertThat(DIGITAL_HOUR_OF_AMPM.name(), is("DIGITAL_HOUR_OF_AMPM"));
    }

    @Test
    public void clockHourOfDayName() {
        assertThat(CLOCK_HOUR_OF_DAY.name(), is("CLOCK_HOUR_OF_DAY"));
    }

    @Test
    public void digitalHourOfDayName() {
        assertThat(DIGITAL_HOUR_OF_DAY.name(), is("DIGITAL_HOUR_OF_DAY"));
    }

    @Test
    public void hour0To24Name() {
        assertThat(HOUR_FROM_0_TO_24.name(), is("HOUR_FROM_0_TO_24"));
    }

    @Test
    public void minuteOfHourName() {
        assertThat(MINUTE_OF_HOUR.name(), is("MINUTE_OF_HOUR"));
    }

    @Test
    public void minuteOfDayName() {
        assertThat(MINUTE_OF_DAY.name(), is("MINUTE_OF_DAY"));
    }

    @Test
    public void secondOfMinuteName() {
        assertThat(SECOND_OF_MINUTE.name(), is("SECOND_OF_MINUTE"));
    }

    @Test
    public void secondOfDayName() {
        assertThat(SECOND_OF_DAY.name(), is("SECOND_OF_DAY"));
    }

    @Test
    public void milliOfSecond() {
        assertThat(MILLI_OF_SECOND.name(), is("MILLI_OF_SECOND"));
    }

    @Test
    public void milliOfDayName() {
        assertThat(MILLI_OF_DAY.name(), is("MILLI_OF_DAY"));
    }

    @Test
    public void microOfSecondName() {
        assertThat(MICRO_OF_SECOND.name(), is("MICRO_OF_SECOND"));
    }

    @Test
    public void microOfDayName() {
        assertThat(MICRO_OF_DAY.name(), is("MICRO_OF_DAY"));
    }

    @Test
    public void nanoOfSecondName() {
        assertThat(NANO_OF_SECOND.name(), is("NANO_OF_SECOND"));
    }

    @Test
    public void nanoOfDayName() {
        assertThat(NANO_OF_DAY.name(), is("NANO_OF_DAY"));
    }

    @Test
    public void wallTimeIsLenient() {
        assertThat(WALL_TIME.isLenient(), is(false));
    }

    @Test
    public void amPmOfDayIsLenient() {
        assertThat(AM_PM_OF_DAY.isLenient(), is(false));
    }

    @Test
    public void precisionIsLenient() {
        assertThat(PRECISION.isLenient(), is(false));
    }

    @Test
    public void clockHourOfAmPmIsLenient() {
        assertThat(CLOCK_HOUR_OF_AMPM.isLenient(), is(false));
    }

    @Test
    public void digitalHourOfAmPmIsLenient() {
        assertThat(DIGITAL_HOUR_OF_AMPM.isLenient(), is(false));
    }

    @Test
    public void clockHourOfDayIsLenient() {
        assertThat(CLOCK_HOUR_OF_DAY.isLenient(), is(false));
    }

    @Test
    public void digitalHourOfDayIsLenient() {
        assertThat(DIGITAL_HOUR_OF_DAY.isLenient(), is(false));
    }

    @Test
    public void hour0To24IsLenient() {
        assertThat(HOUR_FROM_0_TO_24.isLenient(), is(false));
    }

    @Test
    public void minuteOfHourIsLenient() {
        assertThat(MINUTE_OF_HOUR.isLenient(), is(false));
    }

    @Test
    public void minuteOfDayIsLenient() {
        assertThat(MINUTE_OF_DAY.isLenient(), is(false));
    }

    @Test
    public void secondOfMinuteIsLenient() {
        assertThat(SECOND_OF_MINUTE.isLenient(), is(false));
    }

    @Test
    public void secondOfDayIsLenient() {
        assertThat(SECOND_OF_DAY.isLenient(), is(false));
    }

    @Test
    public void milliOfSecondIsLenient() {
        assertThat(MILLI_OF_SECOND.isLenient(), is(false));
    }

    @Test
    public void milliOfDayIsLenient() {
        assertThat(MILLI_OF_DAY.isLenient(), is(false));
    }

    @Test
    public void microOfSecondIsLenient() {
        assertThat(MICRO_OF_SECOND.isLenient(), is(false));
    }

    @Test
    public void microOfDayIsLenient() {
        assertThat(MICRO_OF_DAY.isLenient(), is(false));
    }

    @Test
    public void nanoOfSecondIsLenient() {
        assertThat(NANO_OF_SECOND.isLenient(), is(false));
    }

    @Test
    public void nanoOfDayIsLenient() {
        assertThat(NANO_OF_DAY.isLenient(), is(false));
    }

    @Test
    public void wallTimeIsDateElement() {
        assertThat(WALL_TIME.isDateElement(), is(false));
    }

    @Test
    public void amPmOfDayIsDateElement() {
        assertThat(AM_PM_OF_DAY.isDateElement(), is(false));
    }

    @Test
    public void precisionIsDateElement() {
        assertThat(PRECISION.isDateElement(), is(false));
    }

    @Test
    public void clockHourOfAmPmIsDateElement() {
        assertThat(CLOCK_HOUR_OF_AMPM.isDateElement(), is(false));
    }

    @Test
    public void digitalHourOfAmPmIsDateElement() {
        assertThat(DIGITAL_HOUR_OF_AMPM.isDateElement(), is(false));
    }

    @Test
    public void clockHourOfDayIsDateElement() {
        assertThat(CLOCK_HOUR_OF_DAY.isDateElement(), is(false));
    }

    @Test
    public void digitalHourOfDayIsDateElement() {
        assertThat(DIGITAL_HOUR_OF_DAY.isDateElement(), is(false));
    }

    @Test
    public void hour0To24IsDateElement() {
        assertThat(HOUR_FROM_0_TO_24.isDateElement(), is(false));
    }

    @Test
    public void minuteOfHourIsDateElement() {
        assertThat(MINUTE_OF_HOUR.isDateElement(), is(false));
    }

    @Test
    public void minuteOfDayIsDateElement() {
        assertThat(MINUTE_OF_DAY.isDateElement(), is(false));
    }

    @Test
    public void secondOfMinuteIsDateElement() {
        assertThat(SECOND_OF_MINUTE.isDateElement(), is(false));
    }

    @Test
    public void secondOfDayIsDateElement() {
        assertThat(SECOND_OF_DAY.isDateElement(), is(false));
    }

    @Test
    public void milliOfSecondIsDateElement() {
        assertThat(MILLI_OF_SECOND.isDateElement(), is(false));
    }

    @Test
    public void milliOfDayIsDateElement() {
        assertThat(MILLI_OF_DAY.isDateElement(), is(false));
    }

    @Test
    public void microOfSecondIsDateElement() {
        assertThat(MICRO_OF_SECOND.isDateElement(), is(false));
    }

    @Test
    public void microOfDayIsDateElement() {
        assertThat(MICRO_OF_DAY.isDateElement(), is(false));
    }

    @Test
    public void nanoOfSecondIsDateElement() {
        assertThat(NANO_OF_SECOND.isDateElement(), is(false));
    }

    @Test
    public void nanoOfDayIsDateElement() {
        assertThat(NANO_OF_DAY.isDateElement(), is(false));
    }

    @Test
    public void wallTimeIsTimeElement() {
        assertThat(WALL_TIME.isTimeElement(), is(true));
    }

    @Test
    public void amPmOfDayIsTimeElement() {
        assertThat(AM_PM_OF_DAY.isTimeElement(), is(true));
    }

    @Test
    public void precisionIsTimeElement() {
        assertThat(PRECISION.isTimeElement(), is(true));
    }

    @Test
    public void clockHourOfAmPmIsTimeElement() {
        assertThat(CLOCK_HOUR_OF_AMPM.isTimeElement(), is(true));
    }

    @Test
    public void digitalHourOfAmPmIsTimeElement() {
        assertThat(DIGITAL_HOUR_OF_AMPM.isTimeElement(), is(true));
    }

    @Test
    public void clockHourOfDayIsTimeElement() {
        assertThat(CLOCK_HOUR_OF_DAY.isTimeElement(), is(true));
    }

    @Test
    public void digitalHourOfDayIsTimeElement() {
        assertThat(DIGITAL_HOUR_OF_DAY.isTimeElement(), is(true));
    }

    @Test
    public void hour0To24IsTimeElement() {
        assertThat(HOUR_FROM_0_TO_24.isTimeElement(), is(true));
    }

    @Test
    public void minuteOfHourIsTimeElement() {
        assertThat(MINUTE_OF_HOUR.isTimeElement(), is(true));
    }

    @Test
    public void minuteOfDayIsTimeElement() {
        assertThat(MINUTE_OF_DAY.isTimeElement(), is(true));
    }

    @Test
    public void secondOfMinuteIsTimeElement() {
        assertThat(SECOND_OF_MINUTE.isTimeElement(), is(true));
    }

    @Test
    public void secondOfDayIsTimeElement() {
        assertThat(SECOND_OF_DAY.isTimeElement(), is(true));
    }

    @Test
    public void milliOfSecondIsTimeElement() {
        assertThat(MILLI_OF_SECOND.isTimeElement(), is(true));
    }

    @Test
    public void milliOfDayIsTimeElement() {
        assertThat(MILLI_OF_DAY.isTimeElement(), is(true));
    }

    @Test
    public void microOfSecondIsTimeElement() {
        assertThat(MICRO_OF_SECOND.isTimeElement(), is(true));
    }

    @Test
    public void microOfDayIsTimeElement() {
        assertThat(MICRO_OF_DAY.isTimeElement(), is(true));
    }

    @Test
    public void nanoOfSecondIsTimeElement() {
        assertThat(NANO_OF_SECOND.isTimeElement(), is(true));
    }

    @Test
    public void nanoOfDayIsTimeElement() {
        assertThat(NANO_OF_DAY.isTimeElement(), is(true));
    }

    @Test
    public void wallTimeGetSymbol() {
        assertThat(
            WALL_TIME.getSymbol(),
            is('\u0000'));
    }

    @Test
    public void amPmOfDayGetSymbol() {
        assertThat(AM_PM_OF_DAY.getSymbol(), is('a'));
    }

    @Test
    public void precisionGetSymbol() {
        assertThat(PRECISION.getSymbol(), is('\u0000'));
    }

    @Test
    public void clockHourOfAmPmGetSymbol() {
        assertThat(CLOCK_HOUR_OF_AMPM.getSymbol(), is('h'));
    }

    @Test
    public void digitalHourOfAmPmGetSymbol() {
        assertThat(DIGITAL_HOUR_OF_AMPM.getSymbol(), is('K'));
    }

    @Test
    public void clockHourOfDayGetSymbol() {
        assertThat(CLOCK_HOUR_OF_DAY.getSymbol(), is('k'));
    }

    @Test
    public void digitalHourOfDayGetSymbol() {
        assertThat(DIGITAL_HOUR_OF_DAY.getSymbol(), is('H'));
    }

    @Test
    public void hour0To24GetSymbol() {
        assertThat(HOUR_FROM_0_TO_24.getSymbol(), is('H'));
    }

    @Test
    public void minuteOfHourGetSymbol() {
        assertThat(MINUTE_OF_HOUR.getSymbol(), is('m'));
    }

    @Test
    public void minuteOfDayGetSymbol() {
        assertThat(MINUTE_OF_DAY.getSymbol(), is('\u0000'));
    }

    @Test
    public void secondOfMinuteGetSymbol() {
        assertThat(SECOND_OF_MINUTE.getSymbol(), is('s'));
    }

    @Test
    public void secondOfDayGetSymbol() {
        assertThat(SECOND_OF_DAY.getSymbol(), is('\u0000'));
    }

    @Test
    public void milliOfSecondGetSymbol() {
        assertThat(MILLI_OF_SECOND.getSymbol(), is('\u0000'));
    }

    @Test
    public void milliOfDayGetSymbol() {
        assertThat(MILLI_OF_DAY.getSymbol(), is('A'));
    }

    @Test
    public void microOfSecondGetSymbol() {
        assertThat(MICRO_OF_SECOND.getSymbol(), is('\u0000'));
    }

    @Test
    public void microOfDayGetSymbol() {
        assertThat(MICRO_OF_DAY.getSymbol(), is('\u0000'));
    }

    @Test
    public void nanoOfSecondGetSymbol() {
        assertThat(NANO_OF_SECOND.getSymbol(), is('S'));
    }

    @Test
    public void nanoOfDayGetSymbol() {
        assertThat(
            NANO_OF_DAY.getSymbol(),
            is('\u0000'));
    }

    @Test
    public void wallTimeGetDefaultMinimum() {
        assertThat(WALL_TIME.getDefaultMinimum(), is(PlainTime.of(0)));
    }

    @Test
    public void amPmOfDayGetDefaultMinimum() {
        assertThat(AM_PM_OF_DAY.getDefaultMinimum(), is(Meridiem.AM));
    }

    @Test
    public void precisionGetDefaultMinimum() {
        assertThat(PRECISION.getDefaultMinimum(), is(ClockUnit.HOURS));
    }

    @Test
    public void clockHourOfAmPmGetDefaultMinimum() {
        assertThat(CLOCK_HOUR_OF_AMPM.getDefaultMinimum(), is(1));
    }

    @Test
    public void digitalHourOfAmPmGetDefaultMinimum() {
        assertThat(DIGITAL_HOUR_OF_AMPM.getDefaultMinimum(), is(0));
    }

    @Test
    public void clockHourOfDayGetDefaultMinimum() {
        assertThat(CLOCK_HOUR_OF_DAY.getDefaultMinimum(), is(1));
    }

    @Test
    public void digitalHourOfDayGetDefaultMinimum() {
        assertThat(DIGITAL_HOUR_OF_DAY.getDefaultMinimum(), is(0));
    }

    @Test
    public void hour0To24GetDefaultMinimum() {
        assertThat(HOUR_FROM_0_TO_24.getDefaultMinimum(), is(0));
    }

    @Test
    public void minuteOfHourGetDefaultMinimum() {
        assertThat(MINUTE_OF_HOUR.getDefaultMinimum(), is(0));
    }

    @Test
    public void minuteOfDayGetDefaultMinimum() {
        assertThat(MINUTE_OF_DAY.getDefaultMinimum(), is(0));
    }

    @Test
    public void secondOfMinuteGetDefaultMinimum() {
        assertThat(SECOND_OF_MINUTE.getDefaultMinimum(), is(0));
    }

    @Test
    public void secondOfDayGetDefaultMinimum() {
        assertThat(SECOND_OF_DAY.getDefaultMinimum(), is(0));
    }

    @Test
    public void milliOfSecondGetDefaultMinimum() {
        assertThat(MILLI_OF_SECOND.getDefaultMinimum(), is(0));
    }

    @Test
    public void milliOfDayGetDefaultMinimum() {
        assertThat(MILLI_OF_DAY.getDefaultMinimum(), is(0));
    }

    @Test
    public void microOfSecondGetDefaultMinimum() {
        assertThat(MICRO_OF_SECOND.getDefaultMinimum(), is(0));
    }

    @Test
    public void microOfDayGetDefaultMinimum() {
        assertThat(MICRO_OF_DAY.getDefaultMinimum(), is(0L));
    }

    @Test
    public void nanoOfSecondGetDefaultMinimum() {
        assertThat(NANO_OF_SECOND.getDefaultMinimum(), is(0));
    }

    @Test
    public void nanoOfDayGetDefaultMinimum() {
        assertThat(NANO_OF_DAY.getDefaultMinimum(), is(0L));
    }

    @Test
    public void wallTimeGetDefaultMaximum() {
        assertThat(
            WALL_TIME.getDefaultMaximum(),
            is(PlainTime.of(23, 59, 59, 999999999)));
    }

    @Test
    public void amPmOfDayGetDefaultMaximum() {
        assertThat(AM_PM_OF_DAY.getDefaultMaximum(), is(Meridiem.PM));
    }

    @Test
    public void precisionGetDefaultMaximum() {
        assertThat(PRECISION.getDefaultMaximum(), is(ClockUnit.NANOS));
    }

    @Test
    public void clockHourOfAmPmGetDefaultMaximum() {
        assertThat(CLOCK_HOUR_OF_AMPM.getDefaultMaximum(), is(12));
    }

    @Test
    public void digitalHourOfAmPmGetDefaultMaximum() {
        assertThat(DIGITAL_HOUR_OF_AMPM.getDefaultMaximum(), is(11));
    }

    @Test
    public void clockHourOfDayGetDefaultMaximum() {
        assertThat(CLOCK_HOUR_OF_DAY.getDefaultMaximum(), is(24));
    }

    @Test
    public void digitalHourOfDayGetDefaultMaximum() {
        assertThat(DIGITAL_HOUR_OF_DAY.getDefaultMaximum(), is(23));
    }

    @Test
    public void hour0To24GetDefaultMaximum() {
        assertThat(HOUR_FROM_0_TO_24.getDefaultMaximum(), is(23));
    }

    @Test
    public void minuteOfHourGetDefaultMaximum() {
        assertThat(MINUTE_OF_HOUR.getDefaultMaximum(), is(59));
    }

    @Test
    public void minuteOfDayGetDefaultMaximum() {
        assertThat(MINUTE_OF_DAY.getDefaultMaximum(), is(1439));
    }

    @Test
    public void secondOfMinuteGetDefaultMaximum() {
        assertThat(SECOND_OF_MINUTE.getDefaultMaximum(), is(59));
    }

    @Test
    public void secondOfDayGetDefaultMaximum() {
        assertThat(SECOND_OF_DAY.getDefaultMaximum(), is(86399));
    }

    @Test
    public void milliOfSecondGetDefaultMaximum() {
        assertThat(MILLI_OF_SECOND.getDefaultMaximum(), is(999));
    }

    @Test
    public void milliOfDayGetDefaultMaximum() {
        assertThat(MILLI_OF_DAY.getDefaultMaximum(), is(86400 * 1000 - 1));
    }

    @Test
    public void microOfSecondGetDefaultMaximum() {
        assertThat(MICRO_OF_SECOND.getDefaultMaximum(), is(999999));
    }

    @Test
    public void microOfDayGetDefaultMaximum() {
        assertThat(MICRO_OF_DAY.getDefaultMaximum(), is(86400 * 1000000L - 1));
    }

    @Test
    public void nanoOfSecondGetDefaultMaximum() {
        assertThat(NANO_OF_SECOND.getDefaultMaximum(), is(999999999));
    }

    @Test
    public void nanoOfDayGetDefaultMaximum() {
        assertThat(
            NANO_OF_DAY.getDefaultMaximum(),
            is(86400 * 1000000000L - 1));
    }

}