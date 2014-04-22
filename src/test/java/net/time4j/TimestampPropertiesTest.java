package net.time4j;

import net.time4j.engine.Chronology;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.PlainTime.AM_PM_OF_DAY;
import static net.time4j.PlainTime.ISO_HOUR;
import static net.time4j.PlainTime.MICRO_OF_DAY;
import static net.time4j.PlainTime.MILLI_OF_DAY;
import static net.time4j.PlainTime.MINUTE_OF_DAY;
import static net.time4j.PlainTime.NANO_OF_DAY;
import static net.time4j.PlainTime.SECOND_OF_DAY;
import static net.time4j.PlainTime.WALL_TIME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class TimestampPropertiesTest {

    private static final long MIO = 1000000L;
    private static final long MRD = 1000000000L;

    @Test
    public void getChronology() {
        PlainTimestamp any = PlainTimestamp.of(2014, 4, 21, 0, 0);
        assertThat(
            (any.getChronology() == Chronology.lookup(PlainTimestamp.class)),
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

    @Test
    public void getBaseUnitWallTime() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 15);
        assertThat(
            anyTS.getChronology().getBaseUnit(WALL_TIME),
            is(nullValue()));
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
        assertThat(anyTS.isValid(WALL_TIME, PlainTime.of(24)), is(true));
    }

    @Test
    public void isValidWallTimeNull() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(WALL_TIME, null), is(false));
    }

    @Test
    public void withWallTime() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 9, 15)
                .with(WALL_TIME, PlainTime.of(17, 45, 30)),
            is(PlainTimestamp.of(2014, 4, 21, 17, 45, 30)));
    }

    @Test(expected=NullPointerException.class)
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

    @Test
    public void getBaseUnitAmPm() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 15);
        assertThat(
            anyTS.getChronology().getBaseUnit(AM_PM_OF_DAY),
            is(nullValue()));
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

    @Test(expected=NullPointerException.class)
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
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 15);
        assertThat(
            anyTS.getChronology().getBaseUnit(MINUTE_OF_DAY),
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
        assertThat(anyTS.isValid(MINUTE_OF_DAY, 1440), is(false));
    }

    @Test
    public void isValidMinuteOfDayNull() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(MINUTE_OF_DAY, null), is(false));
    }

    @Test
    public void withMinuteOfDay() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 9, 15, 30)
                .with(MINUTE_OF_DAY, 1439),
            is(PlainTimestamp.of(2014, 4, 21, 23, 59, 30)));
    }

    @Test(expected=NullPointerException.class)
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
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 15);
        assertThat(
            anyTS.getChronology().getBaseUnit(SECOND_OF_DAY),
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
        assertThat(anyTS.isValid(SECOND_OF_DAY, 86400), is(false));
    }

    @Test
    public void isValidSecondOfDayNull() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(SECOND_OF_DAY, null), is(false));
    }

    @Test
    public void withSecondOfDay() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 9, 15)
                .with(SECOND_OF_DAY, 86399),
            is(PlainTimestamp.of(2014, 4, 21, 23, 59, 59)));
    }

    @Test(expected=NullPointerException.class)
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
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 15);
        assertThat(
            anyTS.getChronology().getBaseUnit(MILLI_OF_DAY),
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
        assertThat(anyTS.isValid(MILLI_OF_DAY, 86400000), is(false));
    }

    @Test
    public void isValidMilliOfDayNull() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(MILLI_OF_DAY, null), is(false));
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

    @Test(expected=NullPointerException.class)
    public void withMilliOfDayNull() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(MILLI_OF_DAY, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMilliOfDay86400000() {
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
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 15);
        assertThat(
            anyTS.getChronology().getBaseUnit(MICRO_OF_DAY),
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
        assertThat(anyTS.isValid(MICRO_OF_DAY, 86400000000L), is(false));
    }

    @Test
    public void isValidMicroOfDayNull() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(MICRO_OF_DAY, null), is(false));
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

    @Test(expected=NullPointerException.class)
    public void withMicroOfDayNull() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(MICRO_OF_DAY, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMicroOfDay86400000() {
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
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 15);
        assertThat(
            anyTS.getChronology().getBaseUnit(NANO_OF_DAY),
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
        assertThat(anyTS.isValid(NANO_OF_DAY, 86400 * MRD), is(false));
    }

    @Test
    public void isValidNanoOfDayNull() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(NANO_OF_DAY, null), is(false));
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

    @Test(expected=NullPointerException.class)
    public void withNanoOfDayNull() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(NANO_OF_DAY, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withNanoOfDay86400MRD() {
        PlainTimestamp.of(2014, 4, 21, 9, 15)
            .with(NANO_OF_DAY, 86400 * MRD);
    }

    @Test
    public void containsIsoHour() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.contains(ISO_HOUR), is(true));
    }

    @Test
    public void getIsoHour() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 13, 45).get(ISO_HOUR),
            is(13));
    }

    @Test
    public void getBaseUnitIsoHour() {
        IsoUnit unit = ClockUnit.HOURS;
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 15);
        assertThat(
            anyTS.getChronology().getBaseUnit(ISO_HOUR),
            is(unit));
    }

    @Test
    public void getMinimumIsoHour() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 12, 15);
        assertThat(anyTS.getMinimum(ISO_HOUR), is(0));
    }

    @Test
    public void getMaximumIsoHour() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 0, 0);
        assertThat(anyTS.getMaximum(ISO_HOUR), is(23));
    }

    @Test
    public void isValidIsoHour() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 0, 0);
        assertThat(anyTS.isValid(ISO_HOUR, 23), is(true));
        assertThat(anyTS.isValid(ISO_HOUR, 24), is(false));
    }

    @Test
    public void isValidIsoHourNull() {
        PlainTimestamp anyTS = PlainTimestamp.of(2014, 4, 21, 9, 15);
        assertThat(anyTS.isValid(ISO_HOUR, null), is(false));
    }

    @Test
    public void withIsoHour() {
        assertThat(
            PlainTimestamp.of(2014, 4, 21, 9, 15)
                .with(ISO_HOUR, 23),
            is(PlainTimestamp.of(2014, 4, 21, 23, 15)));
    }

    @Test(expected=NullPointerException.class)
    public void withIsoHourNull() {
        PlainTimestamp.of(2014, 4, 21, 9, 15).with(ISO_HOUR, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withIsoHour24() {
        PlainTimestamp.of(2014, 4, 21, 0, 0).with(ISO_HOUR, 24);
    }

}