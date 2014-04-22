package net.time4j;

import net.time4j.engine.Chronology;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.PlainTime.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class TimePropertiesTest {

    @Test
    public void midnightAtStartOfDay() {
        PlainTime start = PlainTime.midnightAtStartOfDay();
        assertThat(start.getHour(), is(0));
        assertThat(start.getMinute(), is(0));
        assertThat(start.getSecond(), is(0));
        assertThat(start.getNanosecond(), is(0));
    }

    @Test
    public void midnightAtEndOfDay() {
        PlainTime end = PlainTime.midnightAtEndOfDay();
        assertThat(end.getHour(), is(24));
        assertThat(end.getMinute(), is(0));
        assertThat(end.getSecond(), is(0));
        assertThat(end.getNanosecond(), is(0));
    }

    @Test
    public void toStringISO() {
        assertThat(
            PlainTime.of(12, 45, 7, 123456789).toString(),
            is("T12:45:07,123456789"));
        assertThat(
            PlainTime.of(12, 45, 7, 123456000).toString(),
            is("T12:45:07,123456"));
        assertThat(
            PlainTime.of(12, 45, 7, 123000000).toString(),
            is("T12:45:07,123"));
        assertThat(
            PlainTime.of(12, 45, 7).toString(),
            is("T12:45:07"));
        assertThat(
            PlainTime.of(12, 45).toString(),
            is("T12:45"));
        assertThat(
            PlainTime.of(12).toString(),
            is("T12"));
        assertThat(
            PlainTime.of(24).toString(),
            is("T24"));
    }

    @Test
    public void getHourDirect() {
        assertThat(PlainTime.of(3, 45).getHour(), is(3));
        assertThat(PlainTime.of(24).getHour(), is(24));
    }

    @Test
    public void getMinuteDirect() {
        assertThat(PlainTime.of(17, 45, 30, 123456789).getMinute(), is(45));
    }

    @Test
    public void getSecondDirect() {
        assertThat(PlainTime.of(17, 45, 30, 123456789).getSecond(), is(30));
    }

    @Test
    public void getChronology() {
        PlainTime any = PlainTime.of(10, 1);
        assertThat(
            (any.getChronology() == Chronology.lookup(PlainTime.class)),
            is(true));
    }

    @Test
    public void containsWallTime() {
        PlainTime any = PlainTime.of(9, 15);
        assertThat(any.contains(WALL_TIME), is(true));
    }

    @Test
    public void getWallTime() {
        PlainTime any = PlainTime.of(10, 1);
        assertThat(
            (any.get(WALL_TIME) == any),
            is(true));
    }

    @Test
    public void getBaseUnitWallTime() {
        assertThat(
            PlainTime.MAX.getChronology().getBaseUnit(WALL_TIME),
            is(nullValue()));
    }

    @Test
    public void getMinimumWallTime() {
        assertThat(
            PlainTime.of(19, 45).getMinimum(WALL_TIME),
            is(PlainTime.MIN));
    }

    @Test
    public void getMaximumWallTime() {
        assertThat(
            PlainTime.of(19, 45).getMaximum(WALL_TIME),
            is(PlainTime.MAX));
    }

    @Test
    public void isValidWallTime() {
        PlainTime any = PlainTime.of(18, 44);
        assertThat(
            any.isValid(WALL_TIME, PlainTime.MAX),
            is(true));
        assertThat(
            any.isValid(WALL_TIME, PlainTime.MIN),
            is(true));
        assertThat(
            any.isValid(WALL_TIME, PlainTime.of(19, 45, 59, 123456789)),
            is(true));
        assertThat(
            any.isValid(WALL_TIME, null),
            is(false));
    }

    @Test
    public void withWallTime() {
        PlainTime any = PlainTime.of(18, 44);
        PlainTime value = PlainTime.of(19, 45, 59, 123456789);
        assertThat(
            any.with(WALL_TIME, value),
            is(value));
    }

    @Test(expected=NullPointerException.class)
    public void withWallTimeNull() {
        PlainTime.of(18, 44).with(WALL_TIME, null);
    }

    @Test
    public void containsPrecision() {
        PlainTime any = PlainTime.of(9, 15);
        assertThat(any.contains(PRECISION), is(true));
    }

    @Test
    public void getPrecision() {
        assertThat(
            PlainTime.of(18, 44, 30, 123456789).get(PRECISION),
            is(ClockUnit.NANOS));
        assertThat(
            PlainTime.of(18, 44, 30, 123456000).get(PRECISION),
            is(ClockUnit.MICROS));
        assertThat(
            PlainTime.of(18, 44, 30, 123000000).get(PRECISION),
            is(ClockUnit.MILLIS));
        assertThat(
            PlainTime.of(18, 44, 30).get(PRECISION),
            is(ClockUnit.SECONDS));
        assertThat(
            PlainTime.of(18, 44).get(PRECISION),
            is(ClockUnit.MINUTES));
        assertThat(
            PlainTime.of(18).get(PRECISION),
            is(ClockUnit.HOURS));
    }

    @Test
    public void getBaseUnitPrecision() {
        assertThat(
            PlainTime.MAX.getChronology().getBaseUnit(PRECISION),
            is(nullValue()));
    }

    @Test
    public void getMinimumPrecision() {
        assertThat(
            PlainTime.of(18, 44).getMinimum(PRECISION),
            is(ClockUnit.HOURS));
    }

    @Test
    public void getMaximumPrecision() {
        assertThat(
            PlainTime.of(18, 44).getMaximum(PRECISION),
            is(ClockUnit.NANOS));
    }

    @Test
    public void isValidPrecision() {
        PlainTime any = PlainTime.of(18, 44);
        assertThat(
            any.isValid(PRECISION, ClockUnit.SECONDS),
            is(true));
        assertThat(
            any.isValid(PRECISION, null),
            is(false));
    }

    @Test
    public void withPrecisionHours() {
        assertThat(
            PlainTime.of(18, 44, 30, 123456789)
                .with(PRECISION, ClockUnit.HOURS),
            is(PlainTime.of(18)));
    }

    @Test
    public void withPrecisionMinutes() {
        assertThat(
            PlainTime.of(18, 44, 30, 123456789)
                .with(PRECISION, ClockUnit.MINUTES),
            is(PlainTime.of(18, 44)));
    }

    @Test
    public void withPrecisionSeconds() {
        assertThat(
            PlainTime.of(18, 44, 30, 123456789)
                .with(PRECISION, ClockUnit.SECONDS),
            is(PlainTime.of(18, 44, 30)));
    }

    @Test
    public void withPrecisionMillis() {
        assertThat(
            PlainTime.of(18, 44, 30, 123456789)
                .with(PRECISION, ClockUnit.MILLIS),
            is(PlainTime.of(18, 44, 30, 123000000)));
    }

    @Test
    public void withPrecisionMicros() {
        assertThat(
            PlainTime.of(18, 44, 30, 123456789)
                .with(PRECISION, ClockUnit.MICROS),
            is(PlainTime.of(18, 44, 30, 123456000)));
    }

    @Test
    public void withPrecisionNanos() {
        assertThat(
            PlainTime.of(18, 44, 30, 123456789)
                .with(PRECISION, ClockUnit.NANOS),
            is(PlainTime.of(18, 44, 30, 123456789)));
    }

    @Test(expected=NullPointerException.class)
    public void withPrecisionNull() {
        PlainTime.of(18, 44, 30, 123456789).with(PRECISION, null);
    }

    @Test
    public void containsAmPm() {
        PlainTime any = PlainTime.of(9, 15);
        assertThat(any.contains(AM_PM_OF_DAY), is(true));
    }

    @Test
    public void getAmPm() {
        assertThat(
            PlainTime.of(10, 1).get(AM_PM_OF_DAY),
            is(Meridiem.AM));
        assertThat(
            PlainTime.of(0).get(AM_PM_OF_DAY),
            is(Meridiem.AM));
        assertThat(
            PlainTime.of(12).get(AM_PM_OF_DAY),
            is(Meridiem.PM));
        assertThat(
            PlainTime.of(24).get(AM_PM_OF_DAY),
            is(Meridiem.AM));
    }

    @Test
    public void getBaseUnitAmPm() {
        assertThat(
            PlainTime.MAX.getChronology().getBaseUnit(AM_PM_OF_DAY),
            is(nullValue()));
    }

    @Test
    public void getMinimumAmPm() {
        assertThat(
            PlainTime.of(19, 45).getMinimum(AM_PM_OF_DAY),
            is(Meridiem.AM));
    }

    @Test
    public void getMaximumAmPm() {
        assertThat(
            PlainTime.of(19, 45).getMaximum(AM_PM_OF_DAY),
            is(Meridiem.PM));
    }

    @Test
    public void isValidAmPm() {
        PlainTime any = PlainTime.of(18, 44);
        assertThat(
            any.isValid(AM_PM_OF_DAY, Meridiem.AM),
            is(true));
        assertThat(
            any.isValid(AM_PM_OF_DAY, Meridiem.PM),
            is(true));
        assertThat(
            any.isValid(AM_PM_OF_DAY, null),
            is(false));
    }

    @Test
    public void withAmPm() {
        assertThat(
            PlainTime.of(18, 44).with(AM_PM_OF_DAY, Meridiem.AM),
            is(PlainTime.of(6, 44)));
        assertThat(
            PlainTime.of(6, 44).with(AM_PM_OF_DAY, Meridiem.AM),
            is(PlainTime.of(6, 44)));
        assertThat(
            PlainTime.of(18, 44).with(AM_PM_OF_DAY, Meridiem.PM),
            is(PlainTime.of(18, 44)));
        assertThat(
            PlainTime.of(6, 44).with(AM_PM_OF_DAY, Meridiem.PM),
            is(PlainTime.of(18, 44)));
    }

    @Test(expected=NullPointerException.class)
    public void withAmPmNull() {
        PlainTime.of(18, 44).with(AM_PM_OF_DAY, null);
    }

    @Test
    public void containsDigitalHourOfDay() {
        PlainTime any = PlainTime.of(9, 15);
        assertThat(any.contains(DIGITAL_HOUR_OF_DAY), is(true));
    }

    @Test
    public void getDigitalHourOfDay() {
        assertThat(
            PlainTime.of(10, 1).get(DIGITAL_HOUR_OF_DAY),
            is(10));
        assertThat(
            PlainTime.of(0).get(DIGITAL_HOUR_OF_DAY),
            is(0));
        assertThat(
            PlainTime.of(12).get(DIGITAL_HOUR_OF_DAY),
            is(12));
        assertThat(
            PlainTime.of(24).get(DIGITAL_HOUR_OF_DAY),
            is(0));
    }

    @Test
    public void getBaseUnitDigitalHourOfDay() {
        IsoTimeUnit unit = ClockUnit.HOURS;
        assertThat(
            PlainTime.MAX.getChronology().getBaseUnit(DIGITAL_HOUR_OF_DAY),
            is(unit));
    }

    @Test
    public void getMinimumDigitalHourOfDay() {
        assertThat(
            PlainTime.of(19, 45).getMinimum(DIGITAL_HOUR_OF_DAY),
            is(0));
    }

    @Test
    public void getMaximumDigitalHourOfDay() {
        assertThat(
            PlainTime.of(19, 45).getMaximum(DIGITAL_HOUR_OF_DAY),
            is(23));
        assertThat(
            PlainTime.of(24).getMaximum(DIGITAL_HOUR_OF_DAY),
            is(23));
    }

    @Test
    public void isValidDigitalHourOfDay() {
        PlainTime any = PlainTime.of(18, 44);
        assertThat(
            any.isValid(DIGITAL_HOUR_OF_DAY, 0),
            is(true));
        assertThat(
            any.isValid(DIGITAL_HOUR_OF_DAY, 23),
            is(true));
        assertThat(
            any.isValid(DIGITAL_HOUR_OF_DAY, 24),
            is(false));
        assertThat(
            any.isValid(DIGITAL_HOUR_OF_DAY, -1),
            is(false));
        assertThat(
            any.isValid(DIGITAL_HOUR_OF_DAY, null),
            is(false));
    }

    @Test
    public void withDigitalHourOfDay() {
        assertThat(
            PlainTime.of(18, 44).with(DIGITAL_HOUR_OF_DAY, 23),
            is(PlainTime.of(23, 44)));
        assertThat(
            PlainTime.of(6, 44).with(DIGITAL_HOUR_OF_DAY, 0),
            is(PlainTime.of(0, 44)));
        assertThat(
            PlainTime.of(18, 44).with(DIGITAL_HOUR_OF_DAY, 12),
            is(PlainTime.of(12, 44)));
        assertThat(
            PlainTime.of(6, 44).with(DIGITAL_HOUR_OF_DAY, 6),
            is(PlainTime.of(6, 44)));
    }

    @Test(expected=NullPointerException.class)
    public void withDigitalHourOfDayNull() {
        PlainTime.of(18, 44).with(DIGITAL_HOUR_OF_DAY, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDigitalHourOfDay24() {
        PlainTime.of(18).with(DIGITAL_HOUR_OF_DAY, 24);
    }

    @Test
    public void containsDigitalHourOfAmPm() {
        PlainTime any = PlainTime.of(9, 15);
        assertThat(any.contains(DIGITAL_HOUR_OF_AMPM), is(true));
    }

    @Test
    public void getDigitalHourOfAmPm() {
        assertThat(
            PlainTime.of(10, 1).get(DIGITAL_HOUR_OF_AMPM),
            is(10));
        assertThat(
            PlainTime.of(0).get(DIGITAL_HOUR_OF_AMPM),
            is(0));
        assertThat(
            PlainTime.of(12).get(DIGITAL_HOUR_OF_AMPM),
            is(0));
        assertThat(
            PlainTime.of(18).get(DIGITAL_HOUR_OF_AMPM),
            is(6));
        assertThat(
            PlainTime.of(24).get(DIGITAL_HOUR_OF_AMPM),
            is(0));
    }

    @Test
    public void getBaseUnitDigitalHourOfAmPm() {
        IsoTimeUnit unit = ClockUnit.HOURS;
        assertThat(
            PlainTime.MAX.getChronology().getBaseUnit(DIGITAL_HOUR_OF_AMPM),
            is(unit));
    }

    @Test
    public void getMinimumDigitalHourOfAmPm() {
        assertThat(
            PlainTime.of(19, 45).getMinimum(DIGITAL_HOUR_OF_AMPM),
            is(0));
    }

    @Test
    public void getMaximumDigitalHourOfAmPm() {
        assertThat(
            PlainTime.of(19, 45).getMaximum(DIGITAL_HOUR_OF_AMPM),
            is(11));
        assertThat(
            PlainTime.of(24).getMaximum(DIGITAL_HOUR_OF_AMPM),
            is(11));
    }

    @Test
    public void isValidDigitalHourOfAmPm() {
        PlainTime any = PlainTime.of(18, 44);
        assertThat(
            any.isValid(DIGITAL_HOUR_OF_AMPM, 0),
            is(true));
        assertThat(
            any.isValid(DIGITAL_HOUR_OF_AMPM, 11),
            is(true));
        assertThat(
            any.isValid(DIGITAL_HOUR_OF_AMPM, 12),
            is(false));
        assertThat(
            any.isValid(DIGITAL_HOUR_OF_AMPM, -1),
            is(false));
        assertThat(
            any.isValid(DIGITAL_HOUR_OF_AMPM, null),
            is(false));
    }

    @Test
    public void withDigitalHourOfAmPm() {
        assertThat(
            PlainTime.of(18, 44).with(DIGITAL_HOUR_OF_AMPM, 11),
            is(PlainTime.of(23, 44)));
        assertThat(
            PlainTime.of(6, 44).with(DIGITAL_HOUR_OF_AMPM, 0),
            is(PlainTime.of(0, 44)));
        assertThat(
            PlainTime.of(18, 44).with(DIGITAL_HOUR_OF_AMPM, 0),
            is(PlainTime.of(12, 44)));
        assertThat(
            PlainTime.of(6, 44).with(DIGITAL_HOUR_OF_AMPM, 6),
            is(PlainTime.of(6, 44)));
        assertThat(
            PlainTime.of(24).with(DIGITAL_HOUR_OF_AMPM, 6),
            is(PlainTime.of(6)));
    }

    @Test(expected=NullPointerException.class)
    public void withDigitalHourOfAmPmNull() {
        PlainTime.of(18, 44).with(DIGITAL_HOUR_OF_AMPM, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDigitalHourOfAmPm12() {
        PlainTime.of(18).with(DIGITAL_HOUR_OF_AMPM, 12);
    }

    @Test
    public void containsClockHourOfDay() {
        PlainTime any = PlainTime.of(9, 15);
        assertThat(any.contains(CLOCK_HOUR_OF_DAY), is(true));
    }

    @Test
    public void getClockHourOfDay() {
        assertThat(
            PlainTime.of(10, 1).get(CLOCK_HOUR_OF_DAY),
            is(10));
        assertThat(
            PlainTime.of(17, 1).get(CLOCK_HOUR_OF_DAY),
            is(17));
        assertThat(
            PlainTime.of(0).get(CLOCK_HOUR_OF_DAY),
            is(24));
        assertThat(
            PlainTime.of(12).get(CLOCK_HOUR_OF_DAY),
            is(12));
        assertThat(
            PlainTime.of(24).get(CLOCK_HOUR_OF_DAY),
            is(24));
    }

    @Test
    public void getBaseUnitClockHourOfDay() {
        IsoTimeUnit unit = ClockUnit.HOURS;
        assertThat(
            PlainTime.MAX.getChronology().getBaseUnit(CLOCK_HOUR_OF_DAY),
            is(unit));
    }

    @Test
    public void getMinimumClockHourOfDay() {
        assertThat(
            PlainTime.of(19, 45).getMinimum(CLOCK_HOUR_OF_DAY),
            is(1));
    }

    @Test
    public void getMaximumClockHourOfDay() {
        assertThat(
            PlainTime.of(19, 45).getMaximum(CLOCK_HOUR_OF_DAY),
            is(24));
        assertThat(
            PlainTime.of(24).getMaximum(CLOCK_HOUR_OF_DAY),
            is(24));
    }

    @Test
    public void isValidClockHourOfDay() {
        PlainTime any = PlainTime.of(18, 44);
        assertThat(
            any.isValid(CLOCK_HOUR_OF_DAY, 0),
            is(false));
        assertThat(
            any.isValid(CLOCK_HOUR_OF_DAY, 1),
            is(true));
        assertThat(
            any.isValid(CLOCK_HOUR_OF_DAY, 24),
            is(true));
        assertThat(
            any.isValid(CLOCK_HOUR_OF_DAY, 25),
            is(false));
        assertThat(
            any.isValid(CLOCK_HOUR_OF_DAY, null),
            is(false));
    }

    @Test
    public void withClockHourOfDay() {
        assertThat(
            PlainTime.of(18, 44).with(CLOCK_HOUR_OF_DAY, 23),
            is(PlainTime.of(23, 44)));
        assertThat(
            PlainTime.of(6, 44).with(CLOCK_HOUR_OF_DAY, 24),
            is(PlainTime.of(0, 44)));
        assertThat(
            PlainTime.of(11, 44).with(CLOCK_HOUR_OF_DAY, 12),
            is(PlainTime.of(12, 44)));
        assertThat(
            PlainTime.of(13, 44).with(CLOCK_HOUR_OF_DAY, 1),
            is(PlainTime.of(1, 44)));
    }

    @Test(expected=NullPointerException.class)
    public void withClockHourOfDayNull() {
        PlainTime.of(18, 44).with(CLOCK_HOUR_OF_DAY, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withClockHourOfDay0() {
        PlainTime.of(18).with(CLOCK_HOUR_OF_DAY, 0);
    }

    @Test
    public void containsClockHourOfAmPm() {
        PlainTime any = PlainTime.of(9, 15);
        assertThat(any.contains(CLOCK_HOUR_OF_AMPM), is(true));
    }

    @Test
    public void getClockHourOfAmPm() {
        assertThat(
            PlainTime.of(10, 1).get(CLOCK_HOUR_OF_AMPM),
            is(10));
        assertThat(
            PlainTime.of(0).get(CLOCK_HOUR_OF_AMPM),
            is(12));
        assertThat(
            PlainTime.of(12).get(CLOCK_HOUR_OF_AMPM),
            is(12));
        assertThat(
            PlainTime.of(18).get(CLOCK_HOUR_OF_AMPM),
            is(6));
        assertThat(
            PlainTime.of(24).get(CLOCK_HOUR_OF_AMPM),
            is(12));
    }

    @Test
    public void getBaseUnitClockHourOfAmPm() {
        IsoTimeUnit unit = ClockUnit.HOURS;
        assertThat(
            PlainTime.MAX.getChronology().getBaseUnit(CLOCK_HOUR_OF_AMPM),
            is(unit));
    }

    @Test
    public void getMinimumClockHourOfAmPm() {
        assertThat(
            PlainTime.of(19, 45).getMinimum(CLOCK_HOUR_OF_AMPM),
            is(1));
    }

    @Test
    public void getMaximumClockHourOfAmPm() {
        assertThat(
            PlainTime.of(19, 45).getMaximum(CLOCK_HOUR_OF_AMPM),
            is(12));
        assertThat(
            PlainTime.of(24).getMaximum(CLOCK_HOUR_OF_AMPM),
            is(12));
    }

    @Test
    public void isValidClockHourOfAmPm() {
        PlainTime any = PlainTime.of(18, 44);
        assertThat(
            any.isValid(CLOCK_HOUR_OF_AMPM, 0),
            is(false));
        assertThat(
            any.isValid(CLOCK_HOUR_OF_AMPM, 1),
            is(true));
        assertThat(
            any.isValid(CLOCK_HOUR_OF_AMPM, 12),
            is(true));
        assertThat(
            any.isValid(CLOCK_HOUR_OF_AMPM, 13),
            is(false));
        assertThat(
            any.isValid(CLOCK_HOUR_OF_AMPM, null),
            is(false));
    }

    @Test
    public void withClockHourOfAmPm() {
        assertThat(
            PlainTime.of(18, 44).with(CLOCK_HOUR_OF_AMPM, 11),
            is(PlainTime.of(23, 44)));
        assertThat(
            PlainTime.of(6, 44).with(CLOCK_HOUR_OF_AMPM, 4),
            is(PlainTime.of(4, 44)));
        assertThat(
            PlainTime.of(11, 44).with(CLOCK_HOUR_OF_AMPM, 12),
            is(PlainTime.of(0, 44)));
        assertThat(
            PlainTime.of(18, 44).with(CLOCK_HOUR_OF_AMPM, 12),
            is(PlainTime.of(12, 44)));
        assertThat(
            PlainTime.of(6, 44).with(CLOCK_HOUR_OF_AMPM, 11),
            is(PlainTime.of(11, 44)));
        assertThat(
            PlainTime.of(24).with(CLOCK_HOUR_OF_AMPM, 12),
            is(PlainTime.of(0)));
    }

    @Test(expected=NullPointerException.class)
    public void withClockHourOfAmPmNull() {
        PlainTime.of(18, 44).with(CLOCK_HOUR_OF_AMPM, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withClockHourOfAmPm0() {
        PlainTime.of(18).with(CLOCK_HOUR_OF_AMPM, 0);
    }

    @Test
    public void containsIsoHour() {
        PlainTime any = PlainTime.of(9, 15);
        assertThat(any.contains(ISO_HOUR), is(true));
    }

    @Test
    public void getIsoHour() {
        assertThat(
            PlainTime.of(10, 1).get(ISO_HOUR),
            is(10));
        assertThat(
            PlainTime.of(0).get(ISO_HOUR),
            is(0));
        assertThat(
            PlainTime.of(12).get(ISO_HOUR),
            is(12));
        assertThat(
            PlainTime.of(18).get(ISO_HOUR),
            is(18));
        assertThat(
            PlainTime.of(24).get(ISO_HOUR),
            is(24));
    }

    @Test
    public void getBaseUnitIsoHour() {
        IsoTimeUnit unit = ClockUnit.HOURS;
        assertThat(
            PlainTime.MAX.getChronology().getBaseUnit(ISO_HOUR),
            is(unit));
    }

    @Test
    public void getMinimumIsoHour() {
        assertThat(
            PlainTime.of(19, 45).getMinimum(ISO_HOUR),
            is(0));
    }

    @Test
    public void getMaximumIsoHour() {
        assertThat(
            PlainTime.of(19, 45).getMaximum(ISO_HOUR),
            is(23));
        assertThat(
            PlainTime.of(11).getMaximum(ISO_HOUR),
            is(24));
    }

    @Test
    public void isValidIsoHour() {
        PlainTime any = PlainTime.of(18, 44);
        assertThat(
            any.isValid(ISO_HOUR, -1),
            is(false));
        assertThat(
            any.isValid(ISO_HOUR, 0),
            is(true));
        assertThat(
            any.isValid(ISO_HOUR, 1),
            is(true));
        assertThat(
            any.isValid(ISO_HOUR, 12),
            is(true));
        assertThat(
            any.isValid(ISO_HOUR, 23),
            is(true));
        assertThat(
            any.isValid(ISO_HOUR, 24),
            is(false));
        assertThat(
            PlainTime.of(11).isValid(ISO_HOUR, 24),
            is(true));
        assertThat(
            any.isValid(ISO_HOUR, 25),
            is(false));
        assertThat(
            any.isValid(ISO_HOUR, null),
            is(false));
    }

    @Test
    public void withIsoHour() {
        assertThat(
            PlainTime.of(18, 44).with(ISO_HOUR, 11),
            is(PlainTime.of(11, 44)));
        assertThat(
            PlainTime.of(6, 44).with(ISO_HOUR, 4),
            is(PlainTime.of(4, 44)));
        assertThat(
            PlainTime.of(11, 44).with(ISO_HOUR, 12),
            is(PlainTime.of(12, 44)));
        assertThat(
            PlainTime.of(18, 44).with(ISO_HOUR, 12),
            is(PlainTime.of(12, 44)));
        assertThat(
            PlainTime.of(6, 44).with(ISO_HOUR, 11),
            is(PlainTime.of(11, 44)));
        assertThat(
            PlainTime.of(13).with(ISO_HOUR, 24),
            is(PlainTime.of(24)));
    }

    @Test(expected=NullPointerException.class)
    public void withIsoHourNull() {
        PlainTime.of(18, 44).with(ISO_HOUR, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withIsoHour24NoFullHour() {
        PlainTime.of(18, 13).with(ISO_HOUR, 24);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withIsoHour25() {
        PlainTime.of(18).with(ISO_HOUR, 25);
    }

    @Test
    public void containsMinuteOfHour() {
        PlainTime any = PlainTime.of(9, 15);
        assertThat(any.contains(MINUTE_OF_HOUR), is(true));
    }

    @Test
    public void getMinuteOfHour() {
        assertThat(
            PlainTime.of(10, 1).get(MINUTE_OF_HOUR),
            is(1));
        assertThat(
            PlainTime.of(0).get(MINUTE_OF_HOUR),
            is(0));
        assertThat(
            PlainTime.of(12, 59).get(MINUTE_OF_HOUR),
            is(59));
    }

    @Test
    public void getBaseUnitMinuteOfHour() {
        IsoTimeUnit unit = ClockUnit.MINUTES;
        assertThat(
            PlainTime.MAX.getChronology().getBaseUnit(MINUTE_OF_HOUR),
            is(unit));
    }

    @Test
    public void getMinimumMinuteOfHour() {
        assertThat(
            PlainTime.of(19, 45).getMinimum(MINUTE_OF_HOUR),
            is(0));
    }

    @Test
    public void getMaximumMinuteOfHour() {
        assertThat(
            PlainTime.of(19, 45).getMaximum(MINUTE_OF_HOUR),
            is(59));
        assertThat(
            PlainTime.of(24).getMaximum(MINUTE_OF_HOUR),
            is(0));
    }

    @Test
    public void isValidMinuteOfHour() {
        PlainTime any = PlainTime.of(18, 44);
        assertThat(
            any.isValid(MINUTE_OF_HOUR, 0),
            is(true));
        assertThat(
            any.isValid(MINUTE_OF_HOUR, 59),
            is(true));
        assertThat(
            any.isValid(MINUTE_OF_HOUR, 60),
            is(false));
        assertThat(
            any.isValid(MINUTE_OF_HOUR, -1),
            is(false));
        assertThat(
            any.isValid(MINUTE_OF_HOUR, null),
            is(false));
        assertThat(
            PlainTime.of(24).isValid(MINUTE_OF_HOUR, 1),
            is(false));
    }

    @Test
    public void withMinuteOfHour() {
        assertThat(
            PlainTime.of(18, 44).with(MINUTE_OF_HOUR, 23),
            is(PlainTime.of(18, 23)));
        assertThat(
            PlainTime.of(6, 44).with(MINUTE_OF_HOUR, 0),
            is(PlainTime.of(6)));
        assertThat(
            PlainTime.of(24).with(MINUTE_OF_HOUR, 0),
            is(PlainTime.of(24)));
    }

    @Test(expected=NullPointerException.class)
    public void withMinuteOfHourNull() {
        PlainTime.of(18, 44).with(MINUTE_OF_HOUR, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMinuteOfHour60() {
        PlainTime.of(3, 45, 30).with(MINUTE_OF_HOUR, 60);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMinuteOfHourIfHour24() {
        PlainTime.of(24).with(MINUTE_OF_HOUR, 1);
    }

    @Test
    public void containsSecondOfMinute() {
        PlainTime any = PlainTime.of(9, 15);
        assertThat(any.contains(SECOND_OF_MINUTE), is(true));
    }

    @Test
    public void getSecondOfMinute() {
        assertThat(
            PlainTime.of(10, 12, 30).get(SECOND_OF_MINUTE),
            is(30));
        assertThat(
            PlainTime.of(0).get(SECOND_OF_MINUTE),
            is(0));
        assertThat(
            PlainTime.of(12, 59, 59).get(SECOND_OF_MINUTE),
            is(59));
    }

    @Test
    public void getBaseUnitSecondOfMinute() {
        IsoTimeUnit unit = ClockUnit.SECONDS;
        assertThat(
            PlainTime.MAX.getChronology().getBaseUnit(SECOND_OF_MINUTE),
            is(unit));
    }

    @Test
    public void getMinimumSecondOfMinute() {
        assertThat(
            PlainTime.of(19, 45, 30).getMinimum(SECOND_OF_MINUTE),
            is(0));
    }

    @Test
    public void getMaximumSecondOfMinute() {
        assertThat(
            PlainTime.of(19, 45, 30).getMaximum(SECOND_OF_MINUTE),
            is(59));
        assertThat(
            PlainTime.of(24).getMaximum(SECOND_OF_MINUTE),
            is(0));
    }

    @Test
    public void isValidSecondOfMinute() {
        PlainTime any = PlainTime.of(18, 44, 30);
        assertThat(
            any.isValid(SECOND_OF_MINUTE, 0),
            is(true));
        assertThat(
            any.isValid(SECOND_OF_MINUTE, 59),
            is(true));
        assertThat(
            any.isValid(SECOND_OF_MINUTE, 60),
            is(false));
        assertThat(
            any.isValid(SECOND_OF_MINUTE, -1),
            is(false));
        assertThat(
            any.isValid(SECOND_OF_MINUTE, null),
            is(false));
        assertThat(
            PlainTime.of(24).isValid(SECOND_OF_MINUTE, 1),
            is(false));
    }

    @Test
    public void withSecondOfMinute() {
        assertThat(
            PlainTime.of(18, 44, 30).with(SECOND_OF_MINUTE, 23),
            is(PlainTime.of(18, 44, 23)));
        assertThat(
            PlainTime.of(6, 44, 59).with(SECOND_OF_MINUTE, 0),
            is(PlainTime.of(6, 44)));
        assertThat(
            PlainTime.of(24).with(SECOND_OF_MINUTE, 0),
            is(PlainTime.of(24)));
    }

    @Test(expected=NullPointerException.class)
    public void withSecondOfMinuteNull() {
        PlainTime.of(18, 44).with(SECOND_OF_MINUTE, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withSecondOfHour60() {
        PlainTime.of(3, 45, 30).with(SECOND_OF_MINUTE, 60);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withSecondOfHourIfHour24() {
        PlainTime.of(24).with(SECOND_OF_MINUTE, 1);
    }

    @Test
    public void containsMinuteOfDay() {
        PlainTime any = PlainTime.of(9, 15);
        assertThat(any.contains(MINUTE_OF_DAY), is(true));
    }

    @Test
    public void getMinuteOfDay() {
        assertThat(
            PlainTime.of(10, 1).get(MINUTE_OF_DAY),
            is(10 * 60 + 1));
        assertThat(
            PlainTime.of(0).get(MINUTE_OF_DAY),
            is(0));
        assertThat(
            PlainTime.of(12, 59).get(MINUTE_OF_DAY),
            is(12 * 60 + 59));
        assertThat(
            PlainTime.of(23, 59, 59).get(MINUTE_OF_DAY),
            is(1439));
        assertThat(
            PlainTime.of(24).get(MINUTE_OF_DAY),
            is(1440));
    }

    @Test
    public void getBaseUnitMinuteOfDay() {
        IsoTimeUnit unit = ClockUnit.MINUTES;
        assertThat(
            PlainTime.MAX.getChronology().getBaseUnit(MINUTE_OF_DAY),
            is(unit));
    }

    @Test
    public void getMinimumMinuteOfDay() {
        assertThat(
            PlainTime.of(19, 45).getMinimum(MINUTE_OF_DAY),
            is(0));
    }

    @Test
    public void getMaximumMinuteOfDay() {
        assertThat(
            PlainTime.of(19, 45, 30).getMaximum(MINUTE_OF_DAY),
            is(1439));
        assertThat(
            PlainTime.of(19, 45).getMaximum(MINUTE_OF_DAY),
            is(1440));
    }

    @Test
    public void isValidMinuteOfDay() {
        PlainTime any = PlainTime.of(18, 44, 30);
        assertThat(
            any.isValid(MINUTE_OF_DAY, -1),
            is(false));
        assertThat(
            any.isValid(MINUTE_OF_DAY, 0),
            is(true));
        assertThat(
            any.isValid(MINUTE_OF_DAY, 59),
            is(true));
        assertThat(
            any.isValid(MINUTE_OF_DAY, 1439),
            is(true));
        assertThat(
            any.isValid(MINUTE_OF_DAY, 1440),
            is(false));
        assertThat(
            any.isValid(MINUTE_OF_DAY, null),
            is(false));
        assertThat(
            PlainTime.of(18, 44).isValid(MINUTE_OF_DAY, 1440),
            is(true));
    }

    @Test
    public void withMinuteOfDay() {
        assertThat(
            PlainTime.of(18, 44).with(MINUTE_OF_DAY, 18 * 60 + 23),
            is(PlainTime.of(18, 23)));
        assertThat(
            PlainTime.of(6, 44).with(MINUTE_OF_DAY, 0),
            is(PlainTime.of(0)));
        assertThat(
            PlainTime.of(18, 44, 30, 123456789).with(MINUTE_OF_DAY, 1439),
            is(PlainTime.of(23, 59, 30, 123456789)));
        assertThat(
            PlainTime.of(6).with(MINUTE_OF_DAY, 1440),
            is(PlainTime.of(24)));
        assertThat(
            PlainTime.of(24).with(MINUTE_OF_DAY, 1440),
            is(PlainTime.of(24)));
    }

    @Test(expected=NullPointerException.class)
    public void withMinuteOfDayNull() {
        PlainTime.of(18, 44).with(MINUTE_OF_DAY, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMinuteOfDay1440IfNoFullMinute() {
        PlainTime.of(18, 44, 1).with(MINUTE_OF_DAY, 1440);
    }

    @Test
    public void containsSecondOfDay() {
        PlainTime any = PlainTime.of(9, 15);
        assertThat(any.contains(SECOND_OF_DAY), is(true));
    }

    @Test
    public void getSecondOfDay() {
        assertThat(
            PlainTime.of(10, 12, 30).get(SECOND_OF_DAY),
            is(10 * 3600 + 12 * 60 + 30));
        assertThat(
            PlainTime.of(0).get(SECOND_OF_DAY),
            is(0));
        assertThat(
            PlainTime.of(23, 59, 59, 123456789).get(SECOND_OF_DAY),
            is(86399));
        assertThat(
            PlainTime.of(24).get(SECOND_OF_DAY),
            is(86400));
    }

    @Test
    public void getBaseUnitSecondOfDay() {
        IsoTimeUnit unit = ClockUnit.SECONDS;
        assertThat(
            PlainTime.MAX.getChronology().getBaseUnit(SECOND_OF_DAY),
            is(unit));
    }

    @Test
    public void getMinimumSecondOfDay() {
        assertThat(
            PlainTime.of(19, 45, 30).getMinimum(SECOND_OF_DAY),
            is(0));
    }

    @Test
    public void getMaximumSecondOfDay() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789).getMaximum(SECOND_OF_DAY),
            is(86399));
        assertThat(
            PlainTime.of(19, 45, 30).getMaximum(SECOND_OF_DAY),
            is(86400));
    }

    @Test
    public void isValidSecondOfDay() {
        PlainTime any = PlainTime.of(18, 44, 30, 123456789);
        assertThat(
            any.isValid(SECOND_OF_DAY, 0),
            is(true));
        assertThat(
            any.isValid(SECOND_OF_DAY, 86399),
            is(true));
        assertThat(
            any.isValid(SECOND_OF_DAY, 86400),
            is(false));
        assertThat(
            any.isValid(SECOND_OF_DAY, -1),
            is(false));
        assertThat(
            any.isValid(SECOND_OF_DAY, null),
            is(false));
        assertThat(
            PlainTime.of(18, 44, 30).isValid(SECOND_OF_DAY, 86400),
            is(true));
    }

    @Test
    public void withSecondOfDay() {
        assertThat(
            PlainTime.of(18, 44, 30, 123456789).with(SECOND_OF_DAY, 23),
            is(PlainTime.of(0, 0, 23, 123456789)));
        assertThat(
            PlainTime.of(6, 44, 59).with(SECOND_OF_DAY, 0),
            is(PlainTime.of(0)));
        assertThat(
            PlainTime.of(23).with(SECOND_OF_DAY, 86400),
            is(PlainTime.of(24)));
        assertThat(
            PlainTime.of(24).with(SECOND_OF_DAY, 86400),
            is(PlainTime.of(24)));
    }

    @Test(expected=NullPointerException.class)
    public void withSecondOfDayNull() {
        PlainTime.of(18, 44).with(SECOND_OF_DAY, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withSecondOfDay86400IfNoFullSecond() {
        PlainTime.of(23, 45, 0, 1).with(SECOND_OF_DAY, 86400);
    }

    @Test
    public void containsMilliOfSecond() {
        PlainTime any = PlainTime.of(9, 15);
        assertThat(any.contains(MILLI_OF_SECOND), is(true));
    }

    @Test
    public void getMilliOfSecond() {
        assertThat(
            PlainTime.of(10, 12, 30, 123456789).get(MILLI_OF_SECOND),
            is(123));
        assertThat(
            PlainTime.of(0).get(MILLI_OF_SECOND),
            is(0));
        assertThat(
            PlainTime.of(24).get(MILLI_OF_SECOND),
            is(0));
    }

    @Test
    public void getBaseUnitMilliOfSecond() {
        IsoTimeUnit unit = ClockUnit.MILLIS;
        assertThat(
            PlainTime.MAX.getChronology().getBaseUnit(MILLI_OF_SECOND),
            is(unit));
    }

    @Test
    public void getMinimumMilliOfSecond() {
        assertThat(
            PlainTime.of(19, 45, 30).getMinimum(MILLI_OF_SECOND),
            is(0));
    }

    @Test
    public void getMaximumMilliOfSecond() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789).getMaximum(MILLI_OF_SECOND),
            is(999));
        assertThat(
            PlainTime.of(24).getMaximum(MILLI_OF_SECOND),
            is(0));
    }

    @Test
    public void isValidMilliOfSecond() {
        PlainTime any = PlainTime.of(18, 44, 30, 123456789);
        assertThat(
            any.isValid(MILLI_OF_SECOND, 0),
            is(true));
        assertThat(
            any.isValid(MILLI_OF_SECOND, 999),
            is(true));
        assertThat(
            any.isValid(MILLI_OF_SECOND, 1000),
            is(false));
        assertThat(
            any.isValid(MILLI_OF_SECOND, -1),
            is(false));
        assertThat(
            any.isValid(MILLI_OF_SECOND, null),
            is(false));
        assertThat(
            PlainTime.of(24).isValid(MILLI_OF_SECOND, 1),
            is(false));
    }

    @Test
    public void withMilliOfSecond() {
        assertThat(
            PlainTime.of(18, 44, 30, 456789).with(MILLI_OF_SECOND, 123),
            is(PlainTime.of(18, 44, 30, 123456789)));
        assertThat(
            PlainTime.of(6, 44, 59, 123456789).with(MILLI_OF_SECOND, 0),
            is(PlainTime.of(6, 44, 59, 456789)));
        assertThat(
            PlainTime.of(23, 44, 30).with(MILLI_OF_SECOND, 999),
            is(PlainTime.of(23, 44, 30, 999000000)));
        assertThat(
            PlainTime.of(24).with(MILLI_OF_SECOND, 0),
            is(PlainTime.of(24)));
    }

    @Test(expected=NullPointerException.class)
    public void withMilliOfSecondNull() {
        PlainTime.of(18, 44).with(MILLI_OF_SECOND, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMilliOfSecond1000() {
        PlainTime.of(23, 45, 30, 123456789).with(MILLI_OF_SECOND, 1000);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMilliOfSecondIfHour24() {
        PlainTime.of(24).with(MILLI_OF_SECOND, 1);
    }

    @Test
    public void containsMilliOfDay() {
        PlainTime any = PlainTime.of(9, 15);
        assertThat(any.contains(MILLI_OF_DAY), is(true));
    }

    @Test
    public void getMilliOfDay() {
        assertThat(
            PlainTime.of(10, 12, 30, 123456789).get(MILLI_OF_DAY),
            is(10 * 3600000 + 12 * 60000 + 30000 + 123));
        assertThat(
            PlainTime.of(0).get(MILLI_OF_DAY),
            is(0));
        assertThat(
            PlainTime.of(23, 59, 59, 123456789).get(MILLI_OF_DAY),
            is(86399 * 1000 + 123));
        assertThat(
            PlainTime.of(24).get(MILLI_OF_DAY),
            is(86400 * 1000));
    }

    @Test
    public void getBaseUnitMilliOfDay() {
        IsoTimeUnit unit = ClockUnit.MILLIS;
        assertThat(
            PlainTime.MAX.getChronology().getBaseUnit(MILLI_OF_DAY),
            is(unit));
    }

    @Test
    public void getMinimumMilliOfDay() {
        assertThat(
            PlainTime.of(19, 45, 30).getMinimum(MILLI_OF_DAY),
            is(0));
    }

    @Test
    public void getMaximumMilliOfDay() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789).getMaximum(MILLI_OF_DAY),
            is(86400 * 1000 - 1));
        assertThat(
            PlainTime.of(19, 45, 30, 123000000).getMaximum(MILLI_OF_DAY),
            is(86400 * 1000));
    }

    @Test
    public void isValidMilliOfDay() {
        PlainTime any = PlainTime.of(18, 44, 30, 123456789);
        assertThat(
            any.isValid(MILLI_OF_DAY, 0),
            is(true));
        assertThat(
            any.isValid(MILLI_OF_DAY, 86400 * 1000 - 1),
            is(true));
        assertThat(
            any.isValid(MILLI_OF_DAY, 86400 * 1000),
            is(false));
        assertThat(
            any.isValid(MILLI_OF_DAY, -1),
            is(false));
        assertThat(
            any.isValid(MILLI_OF_DAY, null),
            is(false));
        assertThat(
            PlainTime.of(18, 44, 30, 123000000).isValid(
                MILLI_OF_DAY,
                86400 * 1000),
            is(true));
    }

    @Test
    public void withMilliOfDay() {
        assertThat(
            PlainTime.of(18, 44, 30, 456789).with(MILLI_OF_DAY, 123),
            is(PlainTime.of(0, 0, 0, 123456789)));
        assertThat(
            PlainTime.of(6, 44, 59).with(MILLI_OF_DAY, 0),
            is(PlainTime.of(0)));
        assertThat(
            PlainTime.of(23, 44, 30, 123000000).with(
                MILLI_OF_DAY,
                86400 * 1000),
            is(PlainTime.of(24)));
        assertThat(
            PlainTime.of(24).with(MILLI_OF_DAY, 86400 * 1000),
            is(PlainTime.of(24)));
    }

    @Test(expected=NullPointerException.class)
    public void withMilliOfDayNull() {
        PlainTime.of(18, 44).with(MILLI_OF_DAY, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMilliOfDayMaxIfNoFullMilli() {
        PlainTime.of(23, 45, 0, 123000001).with(MILLI_OF_DAY, 86400 * 1000);
    }

    @Test
    public void containsMicroOfSecond() {
        PlainTime any = PlainTime.of(9, 15);
        assertThat(any.contains(MICRO_OF_SECOND), is(true));
    }

    @Test
    public void getMicroOfSecond() {
        assertThat(
            PlainTime.of(10, 12, 30, 123456789).get(MICRO_OF_SECOND),
            is(123456));
        assertThat(
            PlainTime.of(0).get(MICRO_OF_SECOND),
            is(0));
        assertThat(
            PlainTime.of(24).get(MICRO_OF_SECOND),
            is(0));
    }

    @Test
    public void getBaseUnitMicroOfSecond() {
        IsoTimeUnit unit = ClockUnit.MICROS;
        assertThat(
            PlainTime.MAX.getChronology().getBaseUnit(MICRO_OF_SECOND),
            is(unit));
    }

    @Test
    public void getMinimumMicroOfSecond() {
        assertThat(
            PlainTime.of(19, 45, 30).getMinimum(MICRO_OF_SECOND),
            is(0));
    }

    @Test
    public void getMaximumMicroOfSecond() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789).getMaximum(MICRO_OF_SECOND),
            is(999999));
        assertThat(
            PlainTime.of(24).getMaximum(MICRO_OF_SECOND),
            is(0));
    }

    @Test
    public void isValidMicroOfSecond() {
        PlainTime any = PlainTime.of(18, 44, 30, 123456789);
        assertThat(
            any.isValid(MICRO_OF_SECOND, 0),
            is(true));
        assertThat(
            any.isValid(MICRO_OF_SECOND, 999999),
            is(true));
        assertThat(
            any.isValid(MICRO_OF_SECOND, 1000000),
            is(false));
        assertThat(
            any.isValid(MICRO_OF_SECOND, -1),
            is(false));
        assertThat(
            any.isValid(MICRO_OF_SECOND, null),
            is(false));
        assertThat(
            PlainTime.of(24).isValid(MICRO_OF_SECOND, 1),
            is(false));
    }

    @Test
    public void withMicroOfSecond() {
        assertThat(
            PlainTime.of(18, 44, 30, 123000789).with(MICRO_OF_SECOND, 456),
            is(PlainTime.of(18, 44, 30, 456789)));
        assertThat(
            PlainTime.of(6, 44, 59, 123456789).with(MICRO_OF_SECOND, 0),
            is(PlainTime.of(6, 44, 59, 789)));
        assertThat(
            PlainTime.of(23, 44, 30).with(MICRO_OF_SECOND, 999999),
            is(PlainTime.of(23, 44, 30, 999999000)));
        assertThat(
            PlainTime.of(24).with(MICRO_OF_SECOND, 0),
            is(PlainTime.of(24)));
    }

    @Test(expected=NullPointerException.class)
    public void withMicroOfSecondNull() {
        PlainTime.of(18, 44).with(MICRO_OF_SECOND, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMicroOfSecondMillion() {
        PlainTime.of(23, 45, 30, 123456789).with(MICRO_OF_SECOND, 1000000);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMicroOfSecondIfHour24() {
        PlainTime.of(24).with(MICRO_OF_SECOND, 1);
    }

    @Test
    public void containsMicroOfDay() {
        PlainTime any = PlainTime.of(9, 15);
        assertThat(any.contains(MICRO_OF_DAY), is(true));
    }

    @Test
    public void getMicroOfDay() {
        assertThat(
            PlainTime.of(10, 12, 30, 123456789).get(MICRO_OF_DAY),
            is((10 * 3600 + 12 * 60 + 30) * 1000000L + 123456));
        assertThat(
            PlainTime.of(0).get(MICRO_OF_DAY),
            is(0L));
        assertThat(
            PlainTime.of(23, 59, 59, 123456789).get(MICRO_OF_DAY),
            is(86399 * 1000000L + 123456));
        assertThat(
            PlainTime.of(24).get(MICRO_OF_DAY),
            is(86400 * 1000000L));
    }

    @Test
    public void getBaseUnitMicroOfDay() {
        IsoTimeUnit unit = ClockUnit.MICROS;
        assertThat(
            PlainTime.MAX.getChronology().getBaseUnit(MICRO_OF_DAY),
            is(unit));
    }

    @Test
    public void getMinimumMicroOfDay() {
        assertThat(
            PlainTime.of(19, 45, 30).getMinimum(MICRO_OF_DAY),
            is(0L));
    }

    @Test
    public void getMaximumMicroOfDay() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789).getMaximum(MICRO_OF_DAY),
            is(86400 * 1000000L - 1));
        assertThat(
            PlainTime.of(19, 45, 30, 123456000).getMaximum(MICRO_OF_DAY),
            is(86400 * 1000000L));
    }

    @Test
    public void isValidMicroOfDay() {
        PlainTime any = PlainTime.of(18, 44, 30, 123456789);
        assertThat(
            any.isValid(MICRO_OF_DAY, 0),
            is(true));
        assertThat(
            any.isValid(MICRO_OF_DAY, 86400 * 1000000L - 1),
            is(true));
        assertThat(
            any.isValid(MICRO_OF_DAY, 86400 * 1000000L),
            is(false));
        assertThat(
            any.isValid(MICRO_OF_DAY, -1),
            is(false));
        assertThat(
            any.isValid(MICRO_OF_DAY, null),
            is(false));
        assertThat(
            PlainTime.of(18, 44, 30, 123456000).isValid(
                MICRO_OF_DAY,
                86400 * 1000000L),
            is(true));
    }

    @Test
    public void withMicroOfDay() {
        assertThat(
            PlainTime.of(18, 44, 30, 789).with(MICRO_OF_DAY, 123456),
            is(PlainTime.of(0, 0, 0, 123456789)));
        assertThat(
            PlainTime.of(6, 44, 59, 123456789).with(MICRO_OF_DAY, 0),
            is(PlainTime.of(0, 0, 0, 789)));
        assertThat(
            PlainTime.of(23, 44, 30, 123456000).with(
                MICRO_OF_DAY,
                86400 * 1000000L),
            is(PlainTime.of(24)));
        assertThat(
            PlainTime.of(24).with(MICRO_OF_DAY, 86400 * 1000000L),
            is(PlainTime.of(24)));
    }

    @Test(expected=NullPointerException.class)
    public void withMicroOfDayNull() {
        PlainTime.of(18, 44).with(MICRO_OF_DAY, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMicroOfDayMaxIfNoFullMicro() {
        PlainTime.of(23, 45, 30, 123456001).with(
            MICRO_OF_DAY,
            86400 * 1000000L);
    }

    @Test
    public void containsNanoOfSecond() {
        PlainTime any = PlainTime.of(9, 15);
        assertThat(any.contains(NANO_OF_SECOND), is(true));
    }

    @Test
    public void getNanoOfSecond() {
        assertThat(
            PlainTime.of(10, 12, 30, 123456789).get(NANO_OF_SECOND),
            is(123456789));
        assertThat(
            PlainTime.of(0).get(NANO_OF_SECOND),
            is(0));
        assertThat(
            PlainTime.of(24).get(NANO_OF_SECOND),
            is(0));
    }

    @Test
    public void getBaseUnitNanoOfSecond() {
        IsoTimeUnit unit = ClockUnit.NANOS;
        assertThat(
            PlainTime.MAX.getChronology().getBaseUnit(NANO_OF_SECOND),
            is(unit));
    }

    @Test
    public void getMinimumNanoOfSecond() {
        assertThat(
            PlainTime.of(19, 45, 30).getMinimum(NANO_OF_SECOND),
            is(0));
    }

    @Test
    public void getMaximumNanoOfSecond() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789).getMaximum(NANO_OF_SECOND),
            is(999999999));
        assertThat(
            PlainTime.of(24).getMaximum(NANO_OF_SECOND),
            is(0));
    }

    @Test
    public void isValidNanoOfSecond() {
        PlainTime any = PlainTime.of(18, 44, 30, 123456789);
        assertThat(
            any.isValid(NANO_OF_SECOND, 0),
            is(true));
        assertThat(
            any.isValid(NANO_OF_SECOND, 999999999),
            is(true));
        assertThat(
            any.isValid(NANO_OF_SECOND, 1000000000),
            is(false));
        assertThat(
            any.isValid(NANO_OF_SECOND, -1),
            is(false));
        assertThat(
            any.isValid(NANO_OF_SECOND, null),
            is(false));
        assertThat(
            PlainTime.of(24).isValid(NANO_OF_SECOND, 1),
            is(false));
    }

    @Test
    public void withNanoOfSecond() {
        assertThat(
            PlainTime.of(18, 44, 30, 123000789).with(NANO_OF_SECOND, 456000),
            is(PlainTime.of(18, 44, 30, 456000)));
        assertThat(
            PlainTime.of(6, 44, 59, 123456789).with(NANO_OF_SECOND, 0),
            is(PlainTime.of(6, 44, 59)));
        assertThat(
            PlainTime.of(23, 44, 30).with(NANO_OF_SECOND, 999999999),
            is(PlainTime.of(23, 44, 30, 999999999)));
        assertThat(
            PlainTime.of(24).with(NANO_OF_SECOND, 0),
            is(PlainTime.of(24)));
    }

    @Test(expected=NullPointerException.class)
    public void withNanoOfSecondNull() {
        PlainTime.of(18, 44).with(NANO_OF_SECOND, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withNanoOfSecondBillion() {
        PlainTime.of(23, 45, 30, 123456789).with(NANO_OF_SECOND, 1000000000);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withNanoOfSecondIfHour24() {
        PlainTime.of(24).with(NANO_OF_SECOND, 1);
    }

    @Test
    public void containsNanoOfDay() {
        PlainTime any = PlainTime.of(9, 15);
        assertThat(any.contains(NANO_OF_DAY), is(true));
    }

    @Test
    public void getNanoOfDay() {
        assertThat(
            PlainTime.of(10, 12, 30, 123456789).get(NANO_OF_DAY),
            is((10 * 3600 + 12 * 60 + 30) * 1000000000L + 123456789));
        assertThat(
            PlainTime.of(0).get(NANO_OF_DAY),
            is(0L));
        assertThat(
            PlainTime.of(23, 59, 59, 123456789).get(NANO_OF_DAY),
            is(86399 * 1000000000L + 123456789));
        assertThat(
            PlainTime.of(24).get(NANO_OF_DAY),
            is(86400 * 1000000000L));
    }

    @Test
    public void getBaseUnitNanoOfDay() {
        IsoTimeUnit unit = ClockUnit.NANOS;
        assertThat(
            PlainTime.MAX.getChronology().getBaseUnit(NANO_OF_DAY),
            is(unit));
    }

    @Test
    public void getMinimumNanoOfDay() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789).getMinimum(NANO_OF_DAY),
            is(0L));
    }

    @Test
    public void getMaximumNanoOfDay() {
        assertThat(
            PlainTime.of(19, 45, 30, 123456789).getMaximum(NANO_OF_DAY),
            is(86400 * 1000000000L));
    }

    @Test
    public void isValidNanoOfDay() {
        PlainTime any = PlainTime.of(18, 44, 30, 123456789);
        assertThat(
            any.isValid(NANO_OF_DAY, 0),
            is(true));
        assertThat(
            any.isValid(NANO_OF_DAY, 86400 * 1000000000L),
            is(true));
        assertThat(
            any.isValid(NANO_OF_DAY, 86400 * 1000000000L + 1),
            is(false));
        assertThat(
            any.isValid(NANO_OF_DAY, -1),
            is(false));
        assertThat(
            any.isValid(NANO_OF_DAY, null),
            is(false));
    }

    @Test
    public void withNanoOfDay() {
        assertThat(
            PlainTime.of(18, 44, 30, 0).with(NANO_OF_DAY, 123456789),
            is(PlainTime.of(0, 0, 0, 123456789)));
        assertThat(
            PlainTime.of(6, 44, 59, 123456789).with(NANO_OF_DAY, 0),
            is(PlainTime.of(0)));
        assertThat(
            PlainTime.of(23, 44, 30, 123456789).with(
                NANO_OF_DAY,
                86400 * 1000000000L),
            is(PlainTime.of(24)));
        assertThat(
            PlainTime.of(24).with(NANO_OF_DAY, 86400 * 1000000000L),
            is(PlainTime.of(24)));
    }

    @Test(expected=NullPointerException.class)
    public void withNanoOfDayNull() {
        PlainTime.of(18, 44).with(NANO_OF_DAY, null);
    }

}