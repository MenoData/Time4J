package net.time4j;

import net.time4j.base.GregorianMath;
import net.time4j.engine.ChronoException;
import net.time4j.engine.Chronology;

import java.math.BigDecimal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.PlainDate.*;
import static net.time4j.PlainTime.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class TimestampPropertiesTest {

    private static final long MIO = 1000000L;
    private static final long MRD = 1000000000L;

    @Test
    public void axis() {
        assertThat(
            (PlainTimestamp.axis() == Chronology.lookup(PlainTimestamp.class)),
            is(true));
    }

    @Test
    public void toStringISO() {
        PlainDate date = PlainDate.of(2014, 4, 21);
        PlainTime time = PlainTime.of(19, 45, 30, 123456789);
        assertThat(
            PlainTimestamp.of(date, time).toString(),
            is("2014-04-21T19:45:30,123456789"));
        assertThat(
            PlainTimestamp.of(-2014, 4, 21, 19, 45, 30).toString(),
            is("-2014-04-21T19:45:30"));
        assertThat(
            PlainTimestamp.of(10000, 4, 21, 19, 45, 30).toString(),
            is("+10000-04-21T19:45:30"));
    }

    @Test
    public void getCalendarDateDirect() {
        PlainDate date = PlainDate.of(2014, 4, 21);
        PlainTime time = PlainTime.of(19, 45, 30, 123456789);
        assertThat(
            PlainTimestamp.of(date, time).getCalendarDate(),
            is(date));
    }

    @Test
    public void getWallTimeDirect() {
        PlainDate date = PlainDate.of(2014, 4, 21);
        PlainTime time = PlainTime.of(19, 45, 30, 123456789);
        assertThat(
            PlainTimestamp.of(date, time).getWallTime(),
            is(time));
    }

    @Test
    public void getCalendarDateDirect2() {
        PlainDate date = PlainDate.of(2014, 4, 21);
        PlainTime time = PlainTime.of(19, 45, 30, 123456789);
        assertThat(
            PlainTimestamp.of(date, time).toDate(),
            is(date));
    }

    @Test
    public void getWallTimeDirect2() {
        PlainDate date = PlainDate.of(2014, 4, 21);
        PlainTime time = PlainTime.of(19, 45, 30, 123456789);
        assertThat(
            PlainTimestamp.of(date, time).toTime(),
            is(time));
    }

    @Test
    public void getYearDirect() {
        PlainDate date = PlainDate.of(2014, 4, 21);
        PlainTime time = PlainTime.of(19, 45, 30, 123456789);
        assertThat(
            PlainTimestamp.of(date, time).getYear(),
            is(2014));
    }

    @Test
    public void getMonthDirect() {
        PlainDate date = PlainDate.of(2014, 4, 21);
        PlainTime time = PlainTime.of(19, 45, 30, 123456789);
        assertThat(
            PlainTimestamp.of(date, time).getMonth(),
            is(4));
    }

    @Test
    public void getDayOfMonthDirect() {
        PlainDate date = PlainDate.of(2014, 4, 21);
        PlainTime time = PlainTime.of(19, 45, 30, 123456789);
        assertThat(
            PlainTimestamp.of(date, time).getDayOfMonth(),
            is(21));
    }

    @Test
    public void getHourDirect() {
        PlainDate date = PlainDate.of(2014, 4, 21);
        PlainTime time = PlainTime.of(19, 45, 30, 123456789);
        assertThat(
            PlainTimestamp.of(date, time).getHour(),
            is(19));
    }

    @Test
    public void getMinuteDirect() {
        PlainDate date = PlainDate.of(2014, 4, 21);
        PlainTime time = PlainTime.of(19, 45, 30, 123456789);
        assertThat(
            PlainTimestamp.of(date, time).getMinute(),
            is(45));
    }

    @Test
    public void getSecondDirect() {
        PlainDate date = PlainDate.of(2014, 4, 21);
        PlainTime time = PlainTime.of(19, 45, 30, 123456789);
        assertThat(
            PlainTimestamp.of(date, time).getSecond(),
            is(30));
    }

    @Test
    public void containsWallTime() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 0, 0);
        assertThat(anyTS.contains(WALL_TIME), is(true));
    }

    @Test
    public void getWallTime() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 11, 45).get(WALL_TIME),
            is(PlainTime.of(11, 45)));
    }

    @Test(expected=ChronoException.class)
    public void getBaseUnitWallTime() {
        PlainTimestamp.axis().getBaseUnit(WALL_TIME);
    }

    @Test
    public void getMinimumWallTime() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 15);
        assertThat(
            anyTS.getMinimum(WALL_TIME),
            is(PlainTime.midnightAtStartOfDay()));
    }

    @Test
    public void getMaximumWallTime() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(
            anyTS.getMaximum(WALL_TIME),
            is(PlainTime.of(23, 59, 59, 999999999)));
    }

    @Test
    public void isValidWallTime() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(WALL_TIME, PlainTime.of(9, 15)), is(true));
    }

    @Test
    public void isValidWallTimeNull() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(WALL_TIME, null), is(false));
    }

    @Test
    public void isValidWallTime24() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(WALL_TIME, PlainTime.of(24)), is(false));
    }

    @Test
    public void withWallTime() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 9, 15)
                .with(WALL_TIME, PlainTime.of(17, 45, 30)),
            is(PlainTimestamp.of(2014, 4, 21, 17, 45, 30)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withWallTimeNull() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(WALL_TIME, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withWallTime24() {
        PlainTimestamp.of(2014, 4, 21, 0, 0)
            .with(WALL_TIME, PlainTime.midnightAtEndOfDay());
    }

    @Test
    public void containsAmPm() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.contains(AM_PM_OF_DAY), is(true));
    }

    @Test
    public void getAmPm() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 11, 45).get(AM_PM_OF_DAY),
            is(Meridiem.AM));
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 12, 0).get(AM_PM_OF_DAY),
            is(Meridiem.PM));
    }

    @Test(expected=ChronoException.class)
    public void getBaseUnitAmPm() {
        PlainTimestamp.axis().getBaseUnit(AM_PM_OF_DAY);
    }

    @Test
    public void getMinimumAmPm() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 15);
        assertThat(anyTS.getMinimum(AM_PM_OF_DAY), is(Meridiem.AM));
    }

    @Test
    public void getMaximumAmPm() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.getMaximum(AM_PM_OF_DAY), is(Meridiem.PM));
    }

    @Test
    public void isValidAmPm() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(AM_PM_OF_DAY, Meridiem.AM), is(true));
    }

    @Test
    public void isValidAmPmNull() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(AM_PM_OF_DAY, null), is(false));
    }

    @Test
    public void withAmPm() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 9, 15)
                .with(AM_PM_OF_DAY, Meridiem.PM),
            is(PlainTimestamp.of(2014, 4, 21, 21, 15)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withAmPmNull() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(AM_PM_OF_DAY, null);
    }

    @Test
    public void containsMinuteOfDay() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 0, 0);
        assertThat(anyTS.contains(MINUTE_OF_DAY), is(true));
    }

    @Test
    public void getMinuteOfDay() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 11, 45, 30).get(MINUTE_OF_DAY),
            is(11 * 60 + 45));
    }

    @Test
    public void getBaseUnitMinuteOfDay() {
        IsoUnit unit = ClockUnit.MINUTES;
        assertThat(
            PlainTimestamp.axis().getBaseUnit(MINUTE_OF_DAY),
            is(unit));
    }

    @Test
    public void getMinimumMinuteOfDay() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 15);
        assertThat(anyTS.getMinimum(MINUTE_OF_DAY), is(0));
    }

    @Test
    public void getMaximumMinuteOfDay() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.getMaximum(MINUTE_OF_DAY), is(1439));
    }

    @Test
    public void isValidMinuteOfDay() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(MINUTE_OF_DAY, 1439), is(true));
    }

    @Test
    public void isValidMinuteOfDayNull() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(MINUTE_OF_DAY, null), is(false));
    }

    @Test
    public void isValidMinuteOfDay1440() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(MINUTE_OF_DAY, 1440), is(false));
    }

    @Test
    public void withMinuteOfDay() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 9, 15, 30)
                .with(MINUTE_OF_DAY, 1439),
            is(PlainTimestamp.of(2014, 4, 21, 23, 59, 30)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMinuteOfDayNull() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(MINUTE_OF_DAY, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMinuteOfDay1440() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(MINUTE_OF_DAY, 1440);
    }

    @Test
    public void containsSecondOfDay() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 0, 0);
        assertThat(anyTS.contains(SECOND_OF_DAY), is(true));
    }

    @Test
    public void getSecondOfDay() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 11, 45, 30).get(SECOND_OF_DAY),
            is(11 * 3600 + 45 * 60 + 30));
    }

    @Test
    public void getBaseUnitSecondOfDay() {
        IsoUnit unit = ClockUnit.SECONDS;
        assertThat(
            PlainTimestamp.axis().getBaseUnit(SECOND_OF_DAY),
            is(unit));
    }

    @Test
    public void getMinimumSecondOfDay() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 15);
        assertThat(anyTS.getMinimum(SECOND_OF_DAY), is(0));
    }

    @Test
    public void getMaximumSecondOfDay() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.getMaximum(SECOND_OF_DAY), is(86399));
    }

    @Test
    public void isValidSecondOfDay() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(SECOND_OF_DAY, 86399), is(true));
    }

    @Test
    public void isValidSecondOfDayNull() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(SECOND_OF_DAY, null), is(false));
    }

    @Test
    public void isValidSecondOfDay86400() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(SECOND_OF_DAY, 86400), is(false));
    }

    @Test
    public void withSecondOfDay() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 9, 15)
                .with(SECOND_OF_DAY, 86399),
            is(PlainTimestamp.of(2014, 4, 21, 23, 59, 59)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withSecondOfDayNull() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(SECOND_OF_DAY, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withSecondOfDay86400() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(SECOND_OF_DAY, 86400);
    }

    @Test
    public void containsMilliOfDay() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 0, 0);
        assertThat(anyTS.contains(MILLI_OF_DAY), is(true));
    }

    @Test
    public void getMilliOfDay() {
        PlainDate date = PlainDate.of(2014, 4, 21);
        PlainTime time = PlainTime.of(11, 45, 30, 123456789);
        assertThat(
            PlainTimestamp.of(date, time).get(MILLI_OF_DAY),
            is((11 * 3600 + 45 * 60 + 30) * 1000 + 123));
    }

    @Test
    public void getBaseUnitMilliOfDay() {
        IsoUnit unit = ClockUnit.MILLIS;
        assertThat(
            PlainTimestamp.axis().getBaseUnit(MILLI_OF_DAY),
            is(unit));
    }

    @Test
    public void getMinimumMilliOfDay() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 15);
        assertThat(anyTS.getMinimum(MILLI_OF_DAY), is(0));
    }

    @Test
    public void getMaximumMilliOfDay() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.getMaximum(MILLI_OF_DAY), is(86400 * 1000 - 1));
    }

    @Test
    public void isValidMilliOfDay() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(MILLI_OF_DAY, 86399999), is(true));
    }

    @Test
    public void isValidMilliOfDayNull() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(MILLI_OF_DAY, null), is(false));
    }

    @Test
    public void isValidMilliOfDayT24() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(MILLI_OF_DAY, 86400000), is(false));
    }

    @Test
    public void withMilliOfDay() {
        PlainDate date = PlainDate.of(2014, 4, 21);
        PlainTime time = PlainTime.of(23, 59, 59, 999000000);
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 9, 15)
                .with(MILLI_OF_DAY, 86399999),
            is(PlainTimestamp.of(date, time)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMilliOfDayNull() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(MILLI_OF_DAY, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMilliOfDayT24() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(MILLI_OF_DAY, 86400000);
    }

    @Test
    public void containsMicroOfDay() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 0, 0);
        assertThat(anyTS.contains(MICRO_OF_DAY), is(true));
    }

    @Test
    public void getMicroOfDay() {
        PlainDate date = PlainDate.of(2014, 4, 21);
        PlainTime time = PlainTime.of(11, 45, 30, 123456789);
        assertThat(
            PlainTimestamp.of(date, time).get(MICRO_OF_DAY),
            is((11 * 3600 + 45 * 60 + 30) * MIO + 123456));
    }

    @Test
    public void getBaseUnitMicroOfDay() {
        IsoUnit unit = ClockUnit.MICROS;
        assertThat(
            PlainTimestamp.axis().getBaseUnit(MICRO_OF_DAY),
            is(unit));
    }

    @Test
    public void getMinimumMicroOfDay() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 15);
        assertThat(anyTS.getMinimum(MICRO_OF_DAY), is(0L));
    }

    @Test
    public void getMaximumMicroOfDay() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.getMaximum(MICRO_OF_DAY), is(86400 * MIO - 1));
    }

    @Test
    public void isValidMicroOfDay() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(MICRO_OF_DAY, 86399999999L), is(true));
    }

    @Test
    public void isValidMicroOfDayNull() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(MICRO_OF_DAY, null), is(false));
    }

    @Test
    public void isValidMicroOfDayT24() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(MICRO_OF_DAY, 86400000000L), is(false));
    }

    @Test
    public void withMicroOfDay() {
        PlainDate date = PlainDate.of(2014, 4, 21);
        PlainTime time = PlainTime.of(23, 59, 59, 999999000);
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 9, 15)
                .with(MICRO_OF_DAY, 86399999999L),
            is(PlainTimestamp.of(date, time)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMicroOfDayNull() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(MICRO_OF_DAY, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMicroOfDayT24() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(MICRO_OF_DAY, 86400 * MIO);
    }

    @Test
    public void containsNanoOfDay() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 0, 0);
        assertThat(anyTS.contains(NANO_OF_DAY), is(true));
    }

    @Test
    public void getNanoOfDay() {
        PlainDate date = PlainDate.of(2014, 4, 21);
        PlainTime time = PlainTime.of(11, 45, 30, 123456789);
        assertThat(
            PlainTimestamp.of(date, time).get(NANO_OF_DAY),
            is((11 * 3600 + 45 * 60 + 30) * MRD + 123456789));
    }

    @Test
    public void getBaseUnitNanoOfDay() {
        IsoUnit unit = ClockUnit.NANOS;
        assertThat(
            PlainTimestamp.axis().getBaseUnit(NANO_OF_DAY),
            is(unit));
    }

    @Test
    public void getMinimumNanoOfDay() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 15);
        assertThat(anyTS.getMinimum(NANO_OF_DAY), is(0L));
    }

    @Test
    public void getMaximumNanoOfDay() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.getMaximum(NANO_OF_DAY), is(86400 * MRD - 1));
    }

    @Test
    public void isValidNanoOfDay() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(NANO_OF_DAY, 86399 * MRD), is(true));
    }

    @Test
    public void isValidNanoOfDayNull() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(NANO_OF_DAY, null), is(false));
    }

    @Test
    public void isValidNanoOfDayT24() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(NANO_OF_DAY, 86400 * MRD), is(false));
    }

    @Test
    public void withNanoOfDay() {
        PlainDate date = PlainDate.of(2014, 4, 21);
        PlainTime time = PlainTime.of(23, 59, 59, 999999999);
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 9, 15)
                .with(NANO_OF_DAY, 86399999999999L),
            is(PlainTimestamp.of(date, time)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withNanoOfDayNull() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(NANO_OF_DAY, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withNanoOfDayT24() {
        PlainTimestamp.of(2014, 4, 21, 9, 15)
            .with(NANO_OF_DAY, 86400 * MRD);
    }

    @Test
    public void containsHour0To24() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.contains(HOUR_FROM_0_TO_24), is(true));
    }

    @Test
    public void getHour0To24() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 13, 45).get(HOUR_FROM_0_TO_24),
            is(13));
    }

    @Test
    public void getBaseUnitHour0To24() {
        IsoUnit unit = ClockUnit.HOURS;
        assertThat(
            PlainTimestamp.axis().getBaseUnit(HOUR_FROM_0_TO_24),
            is(unit));
    }

    @Test
    public void getMinimumHour0To24() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 15);
        assertThat(anyTS.getMinimum(HOUR_FROM_0_TO_24), is(0));
    }

    @Test
    public void getMaximumHour0To24() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 0, 0);
        assertThat(anyTS.getMaximum(HOUR_FROM_0_TO_24), is(23));
    }

    @Test
    public void isValidHour0To24() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 0, 0);
        assertThat(anyTS.isValid(HOUR_FROM_0_TO_24, 23), is(true));
    }

    @Test
    public void isValidHour0To24Null() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(HOUR_FROM_0_TO_24, null), is(false));
    }

    @Test
    public void isValidHour0To2424() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 0, 0);
        assertThat(anyTS.isValid(HOUR_FROM_0_TO_24, 24), is(false));
    }

    @Test
    public void withHour0To24() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 9, 15)
                .with(HOUR_FROM_0_TO_24, 23),
            is(PlainTimestamp.of(2014, 4, 21, 23, 15)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withHour0To24Null() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(HOUR_FROM_0_TO_24, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withHour0To2424() {
        PlainTimestamp.of(2014, 4, 21, 0, 0).with(HOUR_FROM_0_TO_24, 24);
    }

    @Test
    public void containsMinuteOfHour() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 0, 0);
        assertThat(anyTS.contains(MINUTE_OF_HOUR), is(true));
    }

    @Test
    public void getMinuteOfHour() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 11, 45, 30).get(MINUTE_OF_HOUR),
            is(45));
    }

    @Test
    public void getBaseUnitMinuteOfHour() {
        IsoUnit unit = ClockUnit.MINUTES;
        assertThat(
            PlainTimestamp.axis().getBaseUnit(MINUTE_OF_HOUR),
            is(unit));
    }

    @Test
    public void getMinimumMinuteOfHour() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 15);
        assertThat(anyTS.getMinimum(MINUTE_OF_HOUR), is(0));
    }

    @Test
    public void getMaximumMinuteOfHour() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.getMaximum(MINUTE_OF_HOUR), is(59));
    }

    @Test
    public void isValidMinuteOfHour() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(MINUTE_OF_HOUR, 59), is(true));
    }

    @Test
    public void isValidMinuteOfHourNull() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(MINUTE_OF_HOUR, null), is(false));
    }

    @Test
    public void isValidMinuteOfHour60() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(MINUTE_OF_HOUR, 60), is(false));
    }

    @Test
    public void withMinuteOfHour() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 9, 15, 30)
                .with(MINUTE_OF_HOUR, 59),
            is(PlainTimestamp.of(2014, 4, 21, 9, 59, 30)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMinuteOfHourNull() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(MINUTE_OF_HOUR, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMinuteOfHour60() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(MINUTE_OF_HOUR, 60);
    }

    @Test
    public void containsSecondOfMinute() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 0, 0);
        assertThat(anyTS.contains(SECOND_OF_MINUTE), is(true));
    }

    @Test
    public void getSecondOfMinute() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 11, 45, 30).get(SECOND_OF_MINUTE),
            is(30));
    }

    @Test
    public void getBaseUnitSecondOfMinute() {
        IsoUnit unit = ClockUnit.SECONDS;
        assertThat(
            PlainTimestamp.axis().getBaseUnit(SECOND_OF_MINUTE),
            is(unit));
    }

    @Test
    public void getMinimumSecondOfMinute() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 15);
        assertThat(anyTS.getMinimum(SECOND_OF_MINUTE), is(0));
    }

    @Test
    public void getMaximumSecondOfMinute() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.getMaximum(SECOND_OF_MINUTE), is(59));
    }

    @Test
    public void isValidSecondOfMinute() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(SECOND_OF_MINUTE, 59), is(true));
    }

    @Test
    public void isValidSecondOfMinuteNull() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(SECOND_OF_MINUTE, null), is(false));
    }

    @Test
    public void isValidSecondOfMinute60() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(SECOND_OF_MINUTE, 60), is(false));
    }

    @Test
    public void withSecondOfMinute() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 9, 15)
                .with(SECOND_OF_MINUTE, 59),
            is(PlainTimestamp.of(2014, 4, 21, 9, 15, 59)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withSecondOfMinuteNull() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(SECOND_OF_MINUTE, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withSecondOfMinute60() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(SECOND_OF_MINUTE, 60);
    }

    @Test
    public void containsMilliOfSecond() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 0, 0);
        assertThat(anyTS.contains(MILLI_OF_SECOND), is(true));
    }

    @Test
    public void getMilliOfSecond() {
        PlainDate date = PlainDate.of(2014, 4, 21);
        PlainTime time = PlainTime.of(11, 45, 30, 123456789);
        assertThat(
            PlainTimestamp.of(date, time).get(MILLI_OF_SECOND),
            is(123));
    }

    @Test
    public void getBaseUnitMilliOfSecond() {
        IsoUnit unit = ClockUnit.MILLIS;
        assertThat(
            PlainTimestamp.axis().getBaseUnit(MILLI_OF_SECOND),
            is(unit));
    }

    @Test
    public void getMinimumMilliOfSecond() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 15);
        assertThat(anyTS.getMinimum(MILLI_OF_SECOND), is(0));
    }

    @Test
    public void getMaximumMilliOfSecond() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.getMaximum(MILLI_OF_SECOND), is(999));
    }

    @Test
    public void isValidMilliOfSecond() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(MILLI_OF_SECOND, 999), is(true));
    }

    @Test
    public void isValidMilliOfSecondNull() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(MILLI_OF_SECOND, null), is(false));
    }

    @Test
    public void isValidMilliOfSecond1000() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(MILLI_OF_SECOND, 1000), is(false));
    }

    @Test
    public void withMilliOfSecond() {
        PlainDate date = PlainDate.of(2014, 4, 21);
        PlainTime time = PlainTime.of(9, 15, 0, 999000000);
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 9, 15)
                .with(MILLI_OF_SECOND, 999),
            is(PlainTimestamp.of(date, time)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMilliOfSecondNull() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(MILLI_OF_SECOND, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMilliOfSecond1000() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(MILLI_OF_SECOND, 1000);
    }

    @Test
    public void containsMicroOfSecond() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 0, 0);
        assertThat(anyTS.contains(MICRO_OF_SECOND), is(true));
    }

    @Test
    public void getMicroOfSecond() {
        PlainDate date = PlainDate.of(2014, 4, 21);
        PlainTime time = PlainTime.of(11, 45, 30, 123456789);
        assertThat(
            PlainTimestamp.of(date, time).get(MICRO_OF_SECOND),
            is(123456));
    }

    @Test
    public void getBaseUnitMicroOfSecond() {
        IsoUnit unit = ClockUnit.MICROS;
        assertThat(
            PlainTimestamp.axis().getBaseUnit(MICRO_OF_SECOND),
            is(unit));
    }

    @Test
    public void getMinimumMicroOfSecond() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 15);
        assertThat(anyTS.getMinimum(MICRO_OF_SECOND), is(0));
    }

    @Test
    public void getMaximumMicroOfSecond() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.getMaximum(MICRO_OF_SECOND), is(999999));
    }

    @Test
    public void isValidMicroOfSecond() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(MICRO_OF_SECOND, 999999), is(true));
    }

    @Test
    public void isValidMicroOfSecondNull() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(MICRO_OF_SECOND, null), is(false));
    }

    @Test
    public void isValidMicroOfSecondMIO() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(MICRO_OF_SECOND, 1000000), is(false));
    }

    @Test
    public void withMicroOfSecond() {
        PlainDate date = PlainDate.of(2014, 4, 21);
        PlainTime time = PlainTime.of(9, 15, 0, 999999000);
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 9, 15)
                .with(MICRO_OF_SECOND, 999999),
            is(PlainTimestamp.of(date, time)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMicroOfSecondNull() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(MICRO_OF_SECOND, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMicroOfSecondMIO() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(MICRO_OF_SECOND, 1000000);
    }

    @Test
    public void containsNanoOfSecond() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 0, 0);
        assertThat(anyTS.contains(NANO_OF_SECOND), is(true));
    }

    @Test
    public void getNanoOfSecond() {
        PlainDate date = PlainDate.of(2014, 4, 21);
        PlainTime time = PlainTime.of(11, 45, 30, 123456789);
        assertThat(
            PlainTimestamp.of(date, time).get(NANO_OF_SECOND),
            is(123456789));
    }

    @Test
    public void getBaseUnitNanoOfSecond() {
        IsoUnit unit = ClockUnit.NANOS;
        assertThat(
            PlainTimestamp.axis().getBaseUnit(NANO_OF_SECOND),
            is(unit));
    }

    @Test
    public void getMinimumNanoOfSecond() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 15);
        assertThat(anyTS.getMinimum(NANO_OF_SECOND), is(0));
    }

    @Test
    public void getMaximumNanoOfSecond() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.getMaximum(NANO_OF_SECOND), is(999999999));
    }

    @Test
    public void isValidNanoOfSecond() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(NANO_OF_SECOND, 999999999), is(true));
    }

    @Test
    public void isValidNanoOfSecondNull() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(NANO_OF_SECOND, null), is(false));
    }

    @Test
    public void isValidNanoOfSecondMRD() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(NANO_OF_SECOND, 1000000000), is(false));
    }

    @Test
    public void withNanoOfSecond() {
        PlainDate date = PlainDate.of(2014, 4, 21);
        PlainTime time = PlainTime.of(9, 15, 0, 123456789);
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 9, 15)
                .with(NANO_OF_SECOND, 123456789),
            is(PlainTimestamp.of(date, time)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withNanoOfSecondNull() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(NANO_OF_SECOND, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withNanoOfSecondMRD() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(NANO_OF_SECOND, 1000000000);
    }

    @Test
    public void containsClockHourOfAmPm() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.contains(CLOCK_HOUR_OF_AMPM), is(true));
    }

    @Test
    public void getClockHourOfAmPm() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 1, 45).get(CLOCK_HOUR_OF_AMPM),
            is(1));
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 13, 45).get(CLOCK_HOUR_OF_AMPM),
            is(1));
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 0, 45).get(CLOCK_HOUR_OF_AMPM),
            is(12));
    }

    @Test
    public void getBaseUnitClockHourOfAmPm() {
        IsoUnit unit = ClockUnit.HOURS;
        assertThat(
            PlainTimestamp.axis().getBaseUnit(CLOCK_HOUR_OF_AMPM),
            is(unit));
    }

    @Test
    public void getMinimumClockHourOfAmPm() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 15);
        assertThat(anyTS.getMinimum(CLOCK_HOUR_OF_AMPM), is(1));
    }

    @Test
    public void getMaximumClockHourOfAmPm() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 0, 0);
        assertThat(anyTS.getMaximum(CLOCK_HOUR_OF_AMPM), is(12));
    }

    @Test
    public void isValidClockHourOfAmPm() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 0, 0);
        assertThat(anyTS.isValid(CLOCK_HOUR_OF_AMPM, 12), is(true));
    }

    @Test
    public void isValidClockHourOfAmPmNull() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(CLOCK_HOUR_OF_AMPM, null), is(false));
    }

    @Test
    public void isValidClockHourOfAmPm0() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 0);
        assertThat(anyTS.isValid(CLOCK_HOUR_OF_AMPM, 0), is(false));
    }

    @Test
    public void withClockHourOfAmPm() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 9, 15)
                .with(CLOCK_HOUR_OF_AMPM, 12),
            is(PlainTimestamp.of(2014, 4, 21, 0, 15)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withClockHourOfAmPmNull() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(CLOCK_HOUR_OF_AMPM, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withClockHourOfAmPm0() {
        PlainTimestamp.of(2014, 4, 21, 0, 0).with(CLOCK_HOUR_OF_AMPM, 0);
    }

    @Test
    public void containsClockHourOfDay() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.contains(CLOCK_HOUR_OF_DAY), is(true));
    }

    @Test
    public void getClockHourOfDay() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 1, 45).get(CLOCK_HOUR_OF_DAY),
            is(1));
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 13, 45).get(CLOCK_HOUR_OF_DAY),
            is(13));
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 0, 45).get(CLOCK_HOUR_OF_DAY),
            is(24));
    }

    @Test
    public void getBaseUnitClockHourOfDay() {
        IsoUnit unit = ClockUnit.HOURS;
        assertThat(
            PlainTimestamp.axis().getBaseUnit(CLOCK_HOUR_OF_DAY),
            is(unit));
    }

    @Test
    public void getMinimumClockHourOfDay() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 15);
        assertThat(anyTS.getMinimum(CLOCK_HOUR_OF_DAY), is(1));
    }

    @Test
    public void getMaximumClockHourOfDay() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 0, 0);
        assertThat(anyTS.getMaximum(CLOCK_HOUR_OF_DAY), is(24));
    }

    @Test
    public void isValidClockHourOfDay() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 0, 0);
        assertThat(anyTS.isValid(CLOCK_HOUR_OF_DAY, 24), is(true));
    }

    @Test
    public void isValidClockHourOfDayNull() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(CLOCK_HOUR_OF_DAY, null), is(false));
    }

    @Test
    public void isValidClockHourOfDay0() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 0);
        assertThat(anyTS.isValid(CLOCK_HOUR_OF_DAY, 0), is(false));
    }

    @Test
    public void withClockHourOfDay() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 9, 15)
                .with(CLOCK_HOUR_OF_DAY, 24),
            is(PlainTimestamp.of(2014, 4, 21, 0, 15)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withClockHourOfDayNull() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(CLOCK_HOUR_OF_DAY, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withClockHourOfDay0() {
        PlainTimestamp.of(2014, 4, 21, 0, 0).with(CLOCK_HOUR_OF_DAY, 0);
    }

    @Test
    public void containsDigitalHourOfAmPm() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.contains(DIGITAL_HOUR_OF_AMPM), is(true));
    }

    @Test
    public void getDigitalHourOfAmPm() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 1, 45).get(DIGITAL_HOUR_OF_AMPM),
            is(1));
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 13, 45).get(DIGITAL_HOUR_OF_AMPM),
            is(1));
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 12, 45).get(DIGITAL_HOUR_OF_AMPM),
            is(0));
    }

    @Test
    public void getBaseUnitDigitalHourOfAmPm() {
        IsoUnit unit = ClockUnit.HOURS;
        assertThat(
            PlainTimestamp.axis().getBaseUnit(DIGITAL_HOUR_OF_AMPM),
            is(unit));
    }

    @Test
    public void getMinimumDigitalHourOfAmPm() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 15);
        assertThat(anyTS.getMinimum(DIGITAL_HOUR_OF_AMPM), is(0));
    }

    @Test
    public void getMaximumDigitalHourOfAmPm() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 0, 0);
        assertThat(anyTS.getMaximum(DIGITAL_HOUR_OF_AMPM), is(11));
    }

    @Test
    public void isValidDigitalHourOfAmPm() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 0, 0);
        assertThat(anyTS.isValid(DIGITAL_HOUR_OF_AMPM, 0), is(true));
        assertThat(anyTS.isValid(DIGITAL_HOUR_OF_AMPM, 11), is(true));
    }

    @Test
    public void isValidDigitalHourOfAmPmNull() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(DIGITAL_HOUR_OF_AMPM, null), is(false));
    }

    @Test
    public void isValidDigitalHourOfAmPm12() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 0);
        assertThat(anyTS.isValid(DIGITAL_HOUR_OF_AMPM, 12), is(false));
    }

    @Test
    public void withDigitalHourOfAmPm() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 9, 15)
                .with(DIGITAL_HOUR_OF_AMPM, 11),
            is(PlainTimestamp.of(2014, 4, 21, 11, 15)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDigitalHourOfAmPmNull() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(DIGITAL_HOUR_OF_AMPM, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDigitalHourOfAmPm12() {
        PlainTimestamp.of(2014, 4, 21, 0, 0).with(DIGITAL_HOUR_OF_AMPM, 12);
    }

    @Test
    public void containsDigitalHourOfDay() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.contains(DIGITAL_HOUR_OF_DAY), is(true));
    }

    @Test
    public void getDigitalHourOfDay() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 1, 45).get(DIGITAL_HOUR_OF_DAY),
            is(1));
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 13, 45).get(DIGITAL_HOUR_OF_DAY),
            is(13));
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 0, 45).get(DIGITAL_HOUR_OF_DAY),
            is(0));
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 23, 45).get(DIGITAL_HOUR_OF_DAY),
            is(23));
    }

    @Test
    public void getBaseUnitDigitalHourOfDay() {
        IsoUnit unit = ClockUnit.HOURS;
        assertThat(
            PlainTimestamp.axis().getBaseUnit(DIGITAL_HOUR_OF_DAY),
            is(unit));
    }

    @Test
    public void getMinimumDigitalHourOfDay() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 15);
        assertThat(anyTS.getMinimum(DIGITAL_HOUR_OF_DAY), is(0));
    }

    @Test
    public void getMaximumDigitalHourOfDay() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 0, 0);
        assertThat(anyTS.getMaximum(DIGITAL_HOUR_OF_DAY), is(23));
    }

    @Test
    public void isValidDigitalHourOfDay() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 0, 0);
        assertThat(anyTS.isValid(DIGITAL_HOUR_OF_DAY, 0), is(true));
        assertThat(anyTS.isValid(DIGITAL_HOUR_OF_DAY, 23), is(true));
    }

    @Test
    public void isValidDigitalHourOfDayNull() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(DIGITAL_HOUR_OF_DAY, null), is(false));
    }

    @Test
    public void isValidDigitalHourOfDay24() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 0);
        assertThat(anyTS.isValid(DIGITAL_HOUR_OF_DAY, 24), is(false));
    }

    @Test
    public void withDigitalHourOfDay() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 9, 15)
                .with(DIGITAL_HOUR_OF_DAY, 23),
            is(PlainTimestamp.of(2014, 4, 21, 23, 15)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDigitalHourOfDayNull() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(DIGITAL_HOUR_OF_DAY, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDigitalHourOfDay24() {
        PlainTimestamp.of(2014, 4, 21, 0, 0).with(DIGITAL_HOUR_OF_DAY, 24);
    }

    @Test
    public void containsPrecision() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.contains(PRECISION), is(true));
    }

    @Test
    public void getPrecision() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 1, 45).get(PRECISION),
            is(ClockUnit.MINUTES));
    }

    @Test
    public void getMinimumPrecision() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 1, 45).getMinimum(PRECISION),
            is(ClockUnit.HOURS));
    }

    @Test
    public void getMaximumPrecision() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 1, 45).getMaximum(PRECISION),
            is(ClockUnit.NANOS));
    }

    @Test
    public void isValidPrecision() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 1, 45)
                .isValid(PRECISION, ClockUnit.HOURS),
            is(true));
    }

    @Test
    public void isValidPrecisionNull() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 1, 45)
                .isValid(PRECISION, null),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getBaseUnitPrecision() {
        PlainTimestamp.axis().getBaseUnit(PRECISION);
    }

    @Test
    public void withPrecision() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 9, 15, 43)
                .with(PRECISION, ClockUnit.MINUTES),
            is(PlainTimestamp.of(2014, 4, 21, 9, 15, 0)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withPrecisionNull() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(PRECISION, null);
    }

    @Test
    public void containsDecimalHour() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.contains(DECIMAL_HOUR), is(true));
    }

    @Test
    public void getDecimalHour() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 1, 45).get(DECIMAL_HOUR),
            is(PlainTime.of(1, 45).get(DECIMAL_HOUR)));
    }

    @Test
    public void getMinimumDecimalHour() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 1, 45).getMinimum(DECIMAL_HOUR),
            is(BigDecimal.ZERO));
    }

    @Test
    public void getMaximumDecimalHour() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 1, 45).getMaximum(DECIMAL_HOUR),
            is(DECIMAL_HOUR.getDefaultMaximum()));
    }

    @Test
    public void isValidDecimalHour() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 1, 45)
                .isValid(DECIMAL_HOUR, BigDecimal.ZERO),
            is(true));
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 1, 45)
                .isValid(DECIMAL_HOUR, null),
            is(false));
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 1, 45)
                .isValid(DECIMAL_HOUR, new BigDecimal(24)),
            is(false));
    }

    @Test
    public void withDecimalHour() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 1, 45)
            .with(DECIMAL_HOUR, BigDecimal.ZERO),
            is(PlainTimestamp.of(2014, 4, 21, 0, 0)));
    }

    @Test
    public void containsDecimalMinute() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.contains(DECIMAL_MINUTE), is(true));
    }

    @Test
    public void getDecimalMinute() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 1, 45).get(DECIMAL_MINUTE),
            is(PlainTime.of(1, 45).get(DECIMAL_MINUTE)));
    }

    @Test
    public void getMinimumDecimalMinute() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 1, 45).getMinimum(DECIMAL_MINUTE),
            is(BigDecimal.ZERO));
    }

    @Test
    public void getMaximumDecimalMinute() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 1, 45).getMaximum(DECIMAL_MINUTE),
            is(DECIMAL_MINUTE.getDefaultMaximum()));
    }

    @Test
    public void isValidDecimalMinute() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 1, 45)
                .isValid(DECIMAL_MINUTE, BigDecimal.ZERO),
            is(true));
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 1, 45)
                .isValid(DECIMAL_MINUTE, null),
            is(false));
    }

    @Test
    public void withDecimalMinute() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 1, 45, 30)
            .with(DECIMAL_MINUTE, BigDecimal.ZERO),
            is(PlainTimestamp.of(2014, 4, 21, 1, 0, 0)));
    }

    @Test
    public void containsDecimalSecond() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.contains(DECIMAL_SECOND), is(true));
    }

    @Test
    public void getDecimalSecond() {
        PlainTimestamp tsp =
            PlainTimestamp.of(2014, 4, 21, 1, 45, 28).plus(1, ClockUnit.MILLIS);
        assertThat(
            tsp.get(DECIMAL_SECOND),
            is(tsp.getWallTime().get(DECIMAL_SECOND)));
    }

    @Test
    public void getMinimumDecimalSecond() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 1, 45).getMinimum(DECIMAL_SECOND),
            is(BigDecimal.ZERO));
    }

    @Test
    public void getMaximumDecimalSecond() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 1, 45).getMaximum(DECIMAL_SECOND),
            is(DECIMAL_SECOND.getDefaultMaximum()));
    }

    @Test
    public void isValidDecimalSecond() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 1, 45)
                .isValid(DECIMAL_SECOND, BigDecimal.ZERO),
            is(true));
    }

    @Test
    public void withDecimalSecond() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 1, 45, 30)
            .plus(123, ClockUnit.MILLIS)
            .with(DECIMAL_SECOND, BigDecimal.ZERO),
            is(PlainTimestamp.of(2014, 4, 21, 1, 45, 0)));
    }

    @Test
    public void containsDayOfMonth() {
        assertThat(
            PlainTimestamp.of(2012, 2, 24, 8, 15).contains(DAY_OF_MONTH),
            is(true));
    }

    @Test
    public void getDayOfMonth() {
        assertThat(
            PlainTimestamp.of(2012, 2, 24, 8, 15).get(DAY_OF_MONTH),
            is(24));
    }

    @Test
    public void getMinimumDayOfMonth() {
        assertThat(
            PlainTimestamp.of(2012, 2, 24, 8, 15).getMinimum(DAY_OF_MONTH),
            is(1));
    }

    @Test
    public void getMaximumDayOfMonth() {
        assertThat(
            PlainTimestamp.of(2012, 2, 24, 8, 15).getMaximum(DAY_OF_MONTH),
            is(29));
    }

    @Test
    public void isValidDayOfMonth() {
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 8, 15).isValid(DAY_OF_MONTH, 11),
            is(true));
    }

    @Test
    public void withDayOfMonth() {
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 8, 15).with(DAY_OF_MONTH, 11),
            is(PlainTimestamp.of(2012, 2, 11, 8, 15)));
    }

    @Test
    public void containsDayOfWeek() {
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 8, 15).contains(DAY_OF_WEEK),
            is(true));
    }

    @Test
    public void getDayOfWeek() {
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 8, 15).get(DAY_OF_WEEK),
            is(Weekday.WEDNESDAY));
    }

    @Test
    public void getMinimumDayOfWeek() {
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 8, 15).getMinimum(DAY_OF_WEEK),
            is(Weekday.MONDAY));
    }

    @Test
    public void getMaximumDayOfWeek() {
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 8, 15).getMaximum(DAY_OF_WEEK),
            is(Weekday.SUNDAY));
    }

    @Test
    public void isValidDayOfWeek() {
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 8, 15)
                .isValid(DAY_OF_WEEK, Weekday.MONDAY),
            is(true));
    }

    @Test
    public void withDayOfWeek() {
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 8, 15)
                .with(DAY_OF_WEEK, Weekday.MONDAY),
            is(PlainTimestamp.of(2012, 2, 27, 8, 15)));
    }

    @Test
    public void containsDayOfYear() {
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 8, 15).contains(DAY_OF_YEAR),
            is(true));
    }

    @Test
    public void getDayOfYear() {
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 8, 15).get(DAY_OF_YEAR),
            is(60));
    }

    @Test
    public void getMinimumDayOfYear() {
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 8, 15).getMinimum(DAY_OF_YEAR),
            is(1));
    }

    @Test
    public void getMaximumDayOfYear() {
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 8, 15).getMaximum(DAY_OF_YEAR),
            is(366));
    }

    @Test
    public void isValidDayOfYear() {
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 8, 15).isValid(DAY_OF_YEAR, 1),
            is(true));
    }

    @Test
    public void withDayOfYear() {
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 8, 15).with(DAY_OF_YEAR, 1),
            is(PlainTimestamp.of(2012, 1, 1, 8, 15)));
    }

    @Test
    public void containsDayOfQuarter() {
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 8, 15).contains(DAY_OF_QUARTER),
            is(true));
    }

    @Test
    public void getDayOfQuarter() {
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 8, 15).get(DAY_OF_QUARTER),
            is(60));
    }

    @Test
    public void getMinimumDayOfQuarter() {
        assertThat(
            PlainTimestamp.of(2012, 3, 31, 8, 15).getMinimum(DAY_OF_QUARTER),
            is(1));
    }

    @Test
    public void getMaximumDayOfQuarter() {
        assertThat(
            PlainTimestamp.of(2012, 3, 31, 8, 15).getMaximum(DAY_OF_QUARTER),
            is(91));
    }

    @Test
    public void isValidDayOfQuarter() {
        assertThat(
            PlainTimestamp.of(2012, 3, 31, 8, 15).isValid(DAY_OF_QUARTER, 32),
            is(true));
    }

    @Test
    public void withDayOfQuarter() {
        assertThat(
            PlainTimestamp.of(2012, 3, 31, 8, 15).with(DAY_OF_QUARTER, 32),
            is(PlainTimestamp.of(2012, 2, 1, 8, 15)));
    }

    @Test
    public void containsQuarterOfYear() {
        assertThat(
            PlainTimestamp.of(2012, 3, 31, 8, 15).contains(QUARTER_OF_YEAR),
            is(true));
    }

    @Test
    public void getQuarterOfYear() {
        assertThat(
            PlainTimestamp.of(2012, 3, 31, 8, 15).get(QUARTER_OF_YEAR),
            is(Quarter.Q1));
    }

    @Test
    public void getMinimumQuarterOfYear() {
        assertThat(
            PlainTimestamp.of(2012, 3, 31, 8, 15).getMinimum(QUARTER_OF_YEAR),
            is(Quarter.Q1));
    }

    @Test
    public void getMaximumQuarterOfYear() {
        assertThat(
            PlainTimestamp.of(2012, 3, 31, 8, 15).getMaximum(QUARTER_OF_YEAR),
            is(Quarter.Q4));
    }

    @Test
    public void isValidQuarterOfYear() {
        assertThat(
            PlainTimestamp.of(2012, 3, 31, 8, 15)
                .isValid(QUARTER_OF_YEAR, Quarter.Q2),
            is(true));
    }

    @Test
    public void withQuarterOfYear() {
        assertThat(
            PlainTimestamp.of(2012, 3, 31, 8, 15)
                .with(QUARTER_OF_YEAR, Quarter.Q2),
            is(PlainTimestamp.of(2012, 6, 30, 8, 15)));
    }

    @Test
    public void containsMonthAsNumber() {
        assertThat(
            PlainTimestamp.of(2012, 3, 31, 8, 15).contains(MONTH_AS_NUMBER),
            is(true));
    }

    @Test
    public void getMonthAsNumber() {
        assertThat(
            PlainTimestamp.of(2012, 3, 31, 8, 15).get(MONTH_AS_NUMBER),
            is(3));
    }

    @Test
    public void getMinimumMonthAsNumber() {
        assertThat(
            PlainTimestamp.of(2012, 3, 31, 8, 15).getMinimum(MONTH_AS_NUMBER),
            is(1));
    }

    @Test
    public void getMaximumMonthAsNumber() {
        assertThat(
            PlainTimestamp.of(2012, 3, 31, 8, 15).getMaximum(MONTH_AS_NUMBER),
            is(12));
    }

    @Test
    public void isValidMonthAsNumber() {
        assertThat(
            PlainTimestamp.of(2012, 3, 31, 8, 15).isValid(MONTH_AS_NUMBER, 6),
            is(true));
    }

    @Test
    public void withMonthAsNumber() {
        assertThat(
            PlainTimestamp.of(2012, 3, 31, 8, 15).with(MONTH_AS_NUMBER, 6),
            is(PlainTimestamp.of(2012, 6, 30, 8, 15)));
    }

    @Test
    public void containsMonthOfYear() {
        assertThat(
            PlainTimestamp.of(2012, 3, 31, 8, 15).contains(MONTH_OF_YEAR),
            is(true));
    }

    @Test
    public void getMonthOfYear() {
        assertThat(
            PlainTimestamp.of(2012, 3, 31, 8, 15).get(MONTH_OF_YEAR),
            is(Month.MARCH));
    }

    @Test
    public void getMinimumMonthOfYear() {
        assertThat(
            PlainTimestamp.of(2012, 3, 31, 8, 15).getMinimum(MONTH_OF_YEAR),
            is(Month.JANUARY));
    }

    @Test
    public void getMaximumMonthOfYear() {
        assertThat(
            PlainTimestamp.of(2012, 3, 31, 8, 15).getMaximum(MONTH_OF_YEAR),
            is(Month.DECEMBER));
    }

    @Test
    public void isValidMonthOfYear() {
        assertThat(
            PlainTimestamp.of(2012, 3, 31, 8, 15)
                .isValid(MONTH_OF_YEAR, Month.JUNE),
            is(true));
    }

    @Test
    public void withMonthOfYear() {
        assertThat(
            PlainTimestamp.of(2012, 3, 31, 8, 15)
                .with(MONTH_OF_YEAR, Month.JUNE),
            is(PlainTimestamp.of(2012, 6, 30, 8, 15)));
    }

    @Test
    public void containsCalendarDate() {
        assertThat(
            PlainTimestamp.of(2012, 2, 22, 8, 15).contains(CALENDAR_DATE),
            is(true));
    }

    @Test
    public void getCalendarDate() {
        assertThat(
            PlainTimestamp.of(2012, 2, 22, 8, 15).get(CALENDAR_DATE),
            is(PlainDate.of(2012, 2, 22)));
    }

    @Test
    public void getMinimumCalendarDate() {
        assertThat(
            PlainTimestamp.of(2012, 2, 22, 8, 15).getMinimum(CALENDAR_DATE),
            is(PlainDate.MIN));
    }

    @Test
    public void getMaximumCalendarDate() {
        assertThat(
            PlainTimestamp.of(2012, 2, 22, 8, 15).getMaximum(CALENDAR_DATE),
            is(PlainDate.MAX));
    }

    @Test
    public void isValidCalendarDate() {
        assertThat(
            PlainTimestamp.of(2012, 2, 22, 8, 15)
                .isValid(CALENDAR_DATE, PlainDate.of(2014, 1)),
            is(true));
    }

    @Test
    public void withCalendarDate() {
        assertThat(
            PlainTimestamp.of(2012, 2, 22, 8, 15)
                .with(CALENDAR_DATE, PlainDate.of(2014, 1)),
            is(PlainTimestamp.of(2014, 1, 1, 8, 15)));
    }

    @Test
    public void containsWeekdayInMonth() {
        assertThat(
            PlainTimestamp.of(2012, 2, 22, 8, 15).contains(WEEKDAY_IN_MONTH),
            is(true));
    }

    @Test
    public void getWeekdayInMonth() {
        assertThat(
            PlainTimestamp.of(2012, 2, 22, 8, 15).get(WEEKDAY_IN_MONTH),
            is(4));
    }

    @Test
    public void getMinimumWeekdayInMonth() {
        assertThat(
            PlainTimestamp.of(2012, 2, 22, 8, 15).getMinimum(WEEKDAY_IN_MONTH),
            is(1));
    }

    @Test
    public void getMaximumWeekdayInMonth() {
        assertThat(
            PlainTimestamp.of(2012, 2, 22, 8, 15).getMaximum(WEEKDAY_IN_MONTH),
            is(5));
    }

    @Test
    public void isValidWeekdayInMonth() {
        assertThat(
            PlainTimestamp.of(2012, 2, 22, 8, 15).isValid(WEEKDAY_IN_MONTH, 5),
            is(true));
        assertThat(
            PlainTimestamp.of(2012, 2, 23, 8, 15).isValid(WEEKDAY_IN_MONTH, 5),
            is(false));
    }

    @Test
    public void withWeekdayInMonth() {
        assertThat(
            PlainTimestamp.of(2012, 2, 22, 8, 15).with(WEEKDAY_IN_MONTH, 5),
            is(PlainTimestamp.of(2012, 2, 29, 8, 15)));
    }

    @Test
    public void containsYear() {
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 8, 15).contains(YEAR),
            is(true));
    }

    @Test
    public void getYear() {
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 8, 15).get(YEAR),
            is(2012));
    }

    @Test
    public void getMinimumYear() {
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 8, 15).getMinimum(YEAR),
            is(GregorianMath.MIN_YEAR));
    }

    @Test
    public void getMaximumYear() {
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 8, 15).getMaximum(YEAR),
            is(GregorianMath.MAX_YEAR));
    }

    @Test
    public void isValidYear() {
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 8, 15).isValid(YEAR, 2013),
            is(true));
    }

    @Test
    public void withYear() {
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 8, 15).with(YEAR, 2013),
            is(PlainTimestamp.of(2013, 2, 28, 8, 15)));
    }

    @Test
    public void containsYearOfWeekdate() {
        assertThat(
            PlainTimestamp.of(2014, 1, 1, 21, 10).contains(YEAR_OF_WEEKDATE),
            is(true));
    }

    @Test
    public void getYearOfWeekdate() {
        assertThat(
            PlainTimestamp.of(2013, 12, 30, 8, 15).get(YEAR_OF_WEEKDATE),
            is(2014));
    }

    @Test
    public void getMinimumYearOfWeekdate() {
        assertThat(
            PlainTimestamp.of(2014, 1, 1, 8, 15).getMinimum(YEAR_OF_WEEKDATE),
            is(GregorianMath.MIN_YEAR));
    }

    @Test
    public void getMaximumYearOfWeekdate() {
        assertThat(
            PlainTimestamp.of(2014, 1, 1, 8, 15).getMaximum(YEAR_OF_WEEKDATE),
            is(GregorianMath.MAX_YEAR));
    }

    @Test
    public void isValidYearOfWeekdate() {
        assertThat(
            PlainTimestamp.of(2014, 1, 1, 8, 15)
                .isValid(YEAR_OF_WEEKDATE, 2013),
            is(true));
    }

    @Test
    public void withYearOfWeekdate() {
        assertThat(
            PlainTimestamp.of(2014, 1, 1, 8, 15).with(YEAR_OF_WEEKDATE, 2013),
            is(PlainTimestamp.of(2013, 1, 2, 8, 15)));
    }

}