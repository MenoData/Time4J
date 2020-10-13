package net.time4j;

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
public class TimePropertiesTest {

    @Test
    public void registrationOfWallTime() {
        assertThat(
            Moment.axis().isRegistered(WALL_TIME),
            is(false));
        assertThat(
            PlainDate.axis().isRegistered(WALL_TIME),
            is(false));
        assertThat(
            PlainTime.axis().isRegistered(WALL_TIME),
            is(true));
        assertThat(
            PlainTimestamp.axis().isRegistered(WALL_TIME),
            is(true));
    }

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
        assertThat(
            PlainTime.of(12, 45, 7, 12000000).toString(),
            is("T12:45:07,012"));
        assertThat(
            PlainTime.of(12, 45, 7, 12300000).toString(),
            is("T12:45:07,012300"));
        assertThat(
            PlainTime.of(12, 45, 7, 1234000).toString(),
            is("T12:45:07,001234"));
        assertThat(
            PlainTime.of(12, 45, 7, 12340000).toString(),
            is("T12:45:07,012340"));
        assertThat(
            PlainTime.of(12, 45, 7, 12345000).toString(),
            is("T12:45:07,012345"));
        assertThat(
            PlainTime.of(12, 45, 7, 12345600).toString(),
            is("T12:45:07,012345600"));
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
    public void axis() {
        assertThat(
            (PlainTime.axis() == Chronology.lookup(PlainTime.class)),
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

    @Test(expected=ChronoException.class)
    public void getBaseUnitWallTime() {
        PlainTime.axis().getBaseUnit(WALL_TIME);
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

    @Test(expected=IllegalArgumentException.class)
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

    @Test(expected=ChronoException.class)
    public void getBaseUnitPrecision() {
        PlainTime.axis().getBaseUnit(PRECISION);
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

    @Test(expected=IllegalArgumentException.class)
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

    @Test(expected=ChronoException.class)
    public void getBaseUnitAmPm() {
        PlainTime.axis().getBaseUnit(AM_PM_OF_DAY);
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

    @Test(expected=IllegalArgumentException.class)
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
            PlainTime.axis().getBaseUnit(DIGITAL_HOUR_OF_DAY),
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

    @Test(expected=IllegalArgumentException.class)
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
            PlainTime.axis().getBaseUnit(DIGITAL_HOUR_OF_AMPM),
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

    @Test(expected=IllegalArgumentException.class)
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
            PlainTime.axis().getBaseUnit(CLOCK_HOUR_OF_DAY),
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

    @Test(expected=IllegalArgumentException.class)
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
            PlainTime.axis().getBaseUnit(CLOCK_HOUR_OF_AMPM),
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

    @Test(expected=IllegalArgumentException.class)
    public void withClockHourOfAmPmNull() {
        PlainTime.of(18, 44).with(CLOCK_HOUR_OF_AMPM, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withClockHourOfAmPm0() {
        PlainTime.of(18).with(CLOCK_HOUR_OF_AMPM, 0);
    }

    @Test
    public void containsHour0To24() {
        PlainTime any = PlainTime.of(9, 15);
        assertThat(any.contains(HOUR_FROM_0_TO_24), is(true));
    }

    @Test
    public void getHour0To24() {
        assertThat(
            PlainTime.of(10, 1).get(HOUR_FROM_0_TO_24),
            is(10));
        assertThat(
            PlainTime.of(0).get(HOUR_FROM_0_TO_24),
            is(0));
        assertThat(
            PlainTime.of(12).get(HOUR_FROM_0_TO_24),
            is(12));
        assertThat(
            PlainTime.of(18).get(HOUR_FROM_0_TO_24),
            is(18));
        assertThat(
            PlainTime.of(24).get(HOUR_FROM_0_TO_24),
            is(24));
    }

    @Test
    public void getBaseUnitHour0To24() {
        IsoTimeUnit unit = ClockUnit.HOURS;
        assertThat(
            PlainTime.axis().getBaseUnit(HOUR_FROM_0_TO_24),
            is(unit));
    }

    @Test
    public void getMinimumHour0To24() {
        assertThat(
            PlainTime.of(19, 45).getMinimum(HOUR_FROM_0_TO_24),
            is(0));
    }

    @Test
    public void getMaximumHour0To24() {
        assertThat(
            PlainTime.of(19, 45).getMaximum(HOUR_FROM_0_TO_24),
            is(23));
        assertThat(
            PlainTime.of(11).getMaximum(HOUR_FROM_0_TO_24),
            is(24));
    }

    @Test
    public void isValidHour0To24() {
        PlainTime any = PlainTime.of(18, 44);
        assertThat(
            any.isValid(HOUR_FROM_0_TO_24, -1),
            is(false));
        assertThat(
            any.isValid(HOUR_FROM_0_TO_24, 0),
            is(true));
        assertThat(
            any.isValid(HOUR_FROM_0_TO_24, 1),
            is(true));
        assertThat(
            any.isValid(HOUR_FROM_0_TO_24, 12),
            is(true));
        assertThat(
            any.isValid(HOUR_FROM_0_TO_24, 23),
            is(true));
        assertThat(
            any.isValid(HOUR_FROM_0_TO_24, 24),
            is(false));
        assertThat(
            PlainTime.of(11).isValid(HOUR_FROM_0_TO_24, 24),
            is(true));
        assertThat(
            any.isValid(HOUR_FROM_0_TO_24, 25),
            is(false));
        assertThat(
            any.isValid(HOUR_FROM_0_TO_24, null),
            is(false));
    }

    @Test
    public void withHour0To24() {
        assertThat(
            PlainTime.of(18, 44).with(HOUR_FROM_0_TO_24, 11),
            is(PlainTime.of(11, 44)));
        assertThat(
            PlainTime.of(6, 44).with(HOUR_FROM_0_TO_24, 4),
            is(PlainTime.of(4, 44)));
        assertThat(
            PlainTime.of(11, 44).with(HOUR_FROM_0_TO_24, 12),
            is(PlainTime.of(12, 44)));
        assertThat(
            PlainTime.of(18, 44).with(HOUR_FROM_0_TO_24, 12),
            is(PlainTime.of(12, 44)));
        assertThat(
            PlainTime.of(6, 44).with(HOUR_FROM_0_TO_24, 11),
            is(PlainTime.of(11, 44)));
        assertThat(
            PlainTime.of(13).with(HOUR_FROM_0_TO_24, 24),
            is(PlainTime.of(24)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withHour0To24Null() {
        PlainTime.of(18, 44).with(HOUR_FROM_0_TO_24, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withHour0To2424NoFullHour() {
        PlainTime.of(18, 13).with(HOUR_FROM_0_TO_24, 24);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withHour0To2425() {
        PlainTime.of(18).with(HOUR_FROM_0_TO_24, 25);
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
            PlainTime.axis().getBaseUnit(MINUTE_OF_HOUR),
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

    @Test(expected=IllegalArgumentException.class)
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
            PlainTime.axis().getBaseUnit(SECOND_OF_MINUTE),
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

    @Test(expected=IllegalArgumentException.class)
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
            PlainTime.axis().getBaseUnit(MINUTE_OF_DAY),
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

    @Test(expected=IllegalArgumentException.class)
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
            PlainTime.axis().getBaseUnit(SECOND_OF_DAY),
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

    @Test(expected=IllegalArgumentException.class)
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
            PlainTime.axis().getBaseUnit(MILLI_OF_SECOND),
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

    @Test(expected=IllegalArgumentException.class)
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
            PlainTime.axis().getBaseUnit(MILLI_OF_DAY),
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

    @Test(expected=IllegalArgumentException.class)
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
            PlainTime.axis().getBaseUnit(MICRO_OF_SECOND),
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

    @Test(expected=IllegalArgumentException.class)
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
            PlainTime.axis().getBaseUnit(MICRO_OF_DAY),
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

    @Test(expected=IllegalArgumentException.class)
    public void withMicroOfDayNull() {
        PlainTime.of(18, 44).with(MICRO_OF_DAY, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMicroOfDayLongMax() {
        PlainTime.of(18, 44).with(MICRO_OF_DAY, Long.MAX_VALUE);
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
            PlainTime.axis().getBaseUnit(NANO_OF_SECOND),
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

    @Test(expected=IllegalArgumentException.class)
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
            PlainTime.axis().getBaseUnit(NANO_OF_DAY),
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

    @Test(expected=IllegalArgumentException.class)
    public void withNanoOfDayNull() {
        PlainTime.of(18, 44).with(NANO_OF_DAY, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withNanoOfDayLongMax() {
        PlainTime.of(18, 44).with(NANO_OF_DAY, Long.MAX_VALUE);
    }

    @Test
    public void containsDayOfMonth() {
        assertThat(
            PlainTime.of(21, 10, 45).contains(DAY_OF_MONTH),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getDayOfMonth() {
        PlainTime.of(21, 10, 45).get(DAY_OF_MONTH);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumDayOfMonth() {
        PlainTime.of(21, 10, 45).getMinimum(DAY_OF_MONTH);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumDayOfMonth() {
        PlainTime.of(21, 10, 45).getMaximum(DAY_OF_MONTH);
    }

    @Test
    public void isValidDayOfMonth() {
        assertThat(
            PlainTime.of(21, 10, 45).isValid(DAY_OF_MONTH, 1),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withDayOfMonth() {
        PlainTime.of(21, 10, 45).with(DAY_OF_MONTH, 1);
    }

    @Test
    public void containsDayOfWeek() {
        assertThat(
            PlainTime.of(21, 10, 45).contains(DAY_OF_WEEK),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getDayOfWeek() {
        PlainTime.of(21, 10, 45).get(DAY_OF_WEEK);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumDayOfWeek() {
        PlainTime.of(21, 10, 45).getMinimum(DAY_OF_WEEK);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumDayOfWeek() {
        PlainTime.of(21, 10, 45).getMaximum(DAY_OF_WEEK);
    }

    @Test
    public void isValidDayOfWeek() {
        assertThat(
            PlainTime.of(21, 10, 45).isValid(DAY_OF_WEEK, Weekday.MONDAY),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withDayOfWeek() {
        PlainTime.of(21, 10, 45).with(DAY_OF_WEEK, Weekday.MONDAY);
    }

    @Test
    public void containsDayOfYear() {
        assertThat(
            PlainTime.of(21, 10, 45).contains(DAY_OF_YEAR),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getDayOfYear() {
        PlainTime.of(21, 10, 45).get(DAY_OF_YEAR);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumDayOfYear() {
        PlainTime.of(21, 10, 45).getMinimum(DAY_OF_YEAR);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumDayOfYear() {
        PlainTime.of(21, 10, 45).getMaximum(DAY_OF_YEAR);
    }

    @Test
    public void isValidDayOfYear() {
        assertThat(
            PlainTime.of(21, 10, 45).isValid(DAY_OF_YEAR, 1),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withDayOfYear() {
        PlainTime.of(21, 10, 45).with(DAY_OF_YEAR, 1);
    }

    @Test
    public void containsDayOfQuarter() {
        assertThat(
            PlainTime.of(21, 10, 45).contains(DAY_OF_QUARTER),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getDayOfQuarter() {
        PlainTime.of(21, 10, 45).get(DAY_OF_QUARTER);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumDayOfQuarter() {
        PlainTime.of(21, 10, 45).getMinimum(DAY_OF_QUARTER);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumDayOfQuarter() {
        PlainTime.of(21, 10, 45).getMaximum(DAY_OF_QUARTER);
    }

    @Test
    public void isValidDayOfQuarter() {
        assertThat(
            PlainTime.of(21, 10, 45).isValid(DAY_OF_QUARTER, 1),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withDayOfQuarter() {
        PlainTime.of(21, 10, 45).with(DAY_OF_QUARTER, 1);
    }

    @Test
    public void containsQuarterOfYear() {
        assertThat(
            PlainTime.of(21, 10, 45).contains(QUARTER_OF_YEAR),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getQuarterOfYear() {
        PlainTime.of(21, 10, 45).get(QUARTER_OF_YEAR);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumQuarterOfYear() {
        PlainTime.of(21, 10, 45).getMinimum(QUARTER_OF_YEAR);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumQuarterOfYear() {
        PlainTime.of(21, 10, 45).getMaximum(QUARTER_OF_YEAR);
    }

    @Test
    public void isValidQuarterOfYear() {
        assertThat(
            PlainTime.of(21, 10, 45).isValid(QUARTER_OF_YEAR, Quarter.Q1),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withQuarterOfYear() {
        PlainTime.of(21, 10, 45).with(QUARTER_OF_YEAR, Quarter.Q1);
    }

    @Test
    public void containsMonthAsNumber() {
        assertThat(
            PlainTime.of(21, 10, 45).contains(MONTH_AS_NUMBER),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getMonthAsNumber() {
        PlainTime.of(21, 10, 45).get(MONTH_AS_NUMBER);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumMonthAsNumber() {
        PlainTime.of(21, 10, 45).getMinimum(MONTH_AS_NUMBER);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumMonthAsNumber() {
        PlainTime.of(21, 10, 45).getMaximum(MONTH_AS_NUMBER);
    }

    @Test
    public void isValidMonthAsNumber() {
        assertThat(
            PlainTime.of(21, 10, 45).isValid(MONTH_AS_NUMBER, 8),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withMonthAsNumber() {
        PlainTime.of(21, 10, 45).with(MONTH_AS_NUMBER, 8);
    }

    @Test
    public void containsMonthOfYear() {
        assertThat(
            PlainTime.of(21, 10, 45).contains(MONTH_OF_YEAR),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getMonthOfYear() {
        PlainTime.of(21, 10, 45).get(MONTH_OF_YEAR);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumMonthOfYear() {
        PlainTime.of(21, 10, 45).getMinimum(MONTH_OF_YEAR);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumMonthOfYear() {
        PlainTime.of(21, 10, 45).getMaximum(MONTH_OF_YEAR);
    }

    @Test
    public void isValidMonthOfYear() {
        assertThat(
            PlainTime.of(21, 10, 45).isValid(MONTH_OF_YEAR, Month.AUGUST),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withMonthOfYear() {
        PlainTime.of(21, 10, 45).with(MONTH_OF_YEAR, Month.AUGUST);
    }

    @Test
    public void containsCalendarDate() {
        assertThat(
            PlainTime.of(21, 10, 45).contains(CALENDAR_DATE),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getCalendarDate() {
        PlainTime.of(21, 10, 45).get(CALENDAR_DATE);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumCalendarDate() {
        PlainTime.of(21, 10, 45).getMinimum(CALENDAR_DATE);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumCalendarDate() {
        PlainTime.of(21, 10, 45).getMaximum(CALENDAR_DATE);
    }

    @Test
    public void isValidCalendarDate() {
        assertThat(
            PlainTime.of(21, 10, 45)
                .isValid(CALENDAR_DATE, PlainDate.of(2014, 1)),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withCalendarDate() {
        PlainTime.of(21, 10, 45).with(CALENDAR_DATE, PlainDate.of(2014, 1));
    }

    @Test
    public void containsWeekdayInMonth() {
        assertThat(
            PlainTime.of(21, 10, 45).contains(WEEKDAY_IN_MONTH),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getWeekdayInMonth() {
        PlainTime.of(21, 10, 45).get(WEEKDAY_IN_MONTH);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumWeekdayInMonth() {
        PlainTime.of(21, 10, 45).getMinimum(WEEKDAY_IN_MONTH);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumWeekdayInMonth() {
        PlainTime.of(21, 10, 45).getMaximum(WEEKDAY_IN_MONTH);
    }

    @Test
    public void isValidWeekdayInMonth() {
        assertThat(
            PlainTime.of(21, 10, 45).isValid(WEEKDAY_IN_MONTH, 1),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withWeekdayInMonth() {
        PlainTime.of(21, 10, 45).with(WEEKDAY_IN_MONTH, 1);
    }

    @Test
    public void containsYear() {
        assertThat(
            PlainTime.of(21, 10, 45).contains(YEAR),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getYear() {
        PlainTime.of(21, 10, 45).get(YEAR);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumYear() {
        PlainTime.of(21, 10, 45).getMinimum(YEAR);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumYear() {
        PlainTime.of(21, 10, 45).getMaximum(YEAR);
    }

    @Test
    public void isValidYear() {
        assertThat(
            PlainTime.of(21, 10, 45).isValid(YEAR, 2012),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withYear() {
        PlainTime.of(21, 10, 45).with(YEAR, 2012);
    }

    @Test
    public void containsYearOfWeekdate() {
        assertThat(
            PlainTime.of(21, 10, 45).contains(YEAR_OF_WEEKDATE),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getYearOfWeekdate() {
        PlainTime.of(21, 10, 45).get(YEAR_OF_WEEKDATE);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumYearOfWeekdate() {
        PlainTime.of(21, 10, 45).getMinimum(YEAR_OF_WEEKDATE);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumYearOfWeekdate() {
        PlainTime.of(21, 10, 45).getMaximum(YEAR_OF_WEEKDATE);
    }

    @Test
    public void isValidYearOfWeekdate() {
        assertThat(
            PlainTime.of(21, 10, 45).isValid(YEAR_OF_WEEKDATE, 2012),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withYearOfWeekdate() {
        PlainTime.of(21, 10, 45).with(YEAR_OF_WEEKDATE, 2012);
    }

    @Test
    public void containsDecimalHour() {
        PlainTime any = PlainTime.of(12, 26, 52, 987654321);
        assertThat(any.contains(DECIMAL_HOUR), is(true));
    }

    @Test
    public void getDecimalHour() {
        assertThat(
            PlainTime.of(12, 26, 52, 987654321).get(DECIMAL_HOUR),
            is(new BigDecimal("12.448052126200277")));
        assertThat(
            PlainTime.of(12, 30).get(DECIMAL_HOUR),
            is(new BigDecimal("12.5")));
    }

    @Test
    public void getDecimalHourT00() {
        assertThat(
            PlainTime.midnightAtStartOfDay().get(DECIMAL_HOUR),
            is(BigDecimal.ZERO));
    }

    @Test
    public void getDecimalHourT24() {
        assertThat(
            PlainTime.midnightAtEndOfDay().get(DECIMAL_HOUR),
            is(new BigDecimal("24")));
    }

    @Test
    public void getMinimumDecimalHour() {
        assertThat(
            PlainTime.of(21, 45).getMinimum(DECIMAL_HOUR),
            is(BigDecimal.ZERO));
    }

    @Test
    public void getMaximumDecimalHour() {
        assertThat(
            PlainTime.of(21, 45).getMaximum(DECIMAL_HOUR),
            is(new BigDecimal("24")));
    }

    @Test
    public void isValidDecimalHour() {
        assertThat(
            PlainTime.midnightAtStartOfDay()
                .isValid(DECIMAL_HOUR, new BigDecimal("12.448052126200277")),
            is(true));
        assertThat(
            PlainTime.midnightAtStartOfDay()
                .isValid(DECIMAL_HOUR, BigDecimal.valueOf(12.5)),
            is(true));
        assertThat(
            PlainTime.midnightAtStartOfDay()
                .isValid(DECIMAL_HOUR, new BigDecimal("24.1")),
            is(false));
    }

    @Test
    public void isValidDecimalHourT24() {
        assertThat(
            PlainTime.midnightAtStartOfDay()
                .isValid(DECIMAL_HOUR, new BigDecimal(24.0)),
            is(true));
        assertThat(
            PlainTime.midnightAtStartOfDay()
                .isValid(DECIMAL_HOUR, new BigDecimal("24")),
            is(true));
        assertThat(
            PlainTime.midnightAtStartOfDay()
                .isValid(DECIMAL_HOUR, new BigDecimal("24.000")),
            is(true));
    }

    @Test
    public void withDecimalHour() {
        assertThat(
            PlainTime.midnightAtStartOfDay()
                .with(DECIMAL_HOUR, new BigDecimal("12.448052126200277")),
            is(PlainTime.of(12, 26, 52, 987654321)));
        assertThat(
            PlainTime.midnightAtStartOfDay()
                .with(DECIMAL_HOUR, BigDecimal.valueOf(12.5)),
            is(PlainTime.of(12, 30)));
    }

    @Test
    public void withDecimalHourT24() {
        assertThat(
            PlainTime.midnightAtStartOfDay()
                .with(DECIMAL_HOUR, new BigDecimal(24.0)),
            is(PlainTime.midnightAtEndOfDay()));
        assertThat(
            PlainTime.midnightAtStartOfDay()
                .with(DECIMAL_HOUR, new BigDecimal("24")),
            is(PlainTime.midnightAtEndOfDay()));
        assertThat(
            PlainTime.midnightAtStartOfDay()
                .with(DECIMAL_HOUR, new BigDecimal("24.000")),
            is(PlainTime.midnightAtEndOfDay()));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDecimalHour25() {
        PlainTime.of(4).with(DECIMAL_HOUR, new BigDecimal("25"));
    }

    @Test
    public void containsDecimalMinute() {
        PlainTime any = PlainTime.of(12, 26, 52, 987654321);
        assertThat(any.contains(DECIMAL_MINUTE), is(true));
    }

    @Test
    public void getDecimalMinute() {
        assertThat(
            PlainTime.of(12, 26, 52, 987654321).get(DECIMAL_MINUTE),
            is(new BigDecimal("26.883127572016666")));
    }

    @Test
    public void getMinimumDecimalMinute() {
        assertThat(
            PlainTime.of(21, 45).getMinimum(DECIMAL_MINUTE),
            is(BigDecimal.ZERO));
    }

    @Test
    public void getMaximumDecimalMinute() {
        assertThat(
            PlainTime.of(21, 45).getMaximum(DECIMAL_MINUTE),
            is(new BigDecimal("59.999999999999999")));
    }

    @Test
    public void getMaximumDecimalMinuteT24() {
        assertThat(
            PlainTime.midnightAtEndOfDay().getMaximum(DECIMAL_MINUTE),
            is(BigDecimal.ZERO));
    }

    @Test
    public void isValidDecimalMinute() {
        assertThat(
            PlainTime.midnightAtStartOfDay()
                .isValid(DECIMAL_MINUTE, new BigDecimal("26.883127572016666")),
            is(true));
        assertThat(
            PlainTime.midnightAtEndOfDay()
                .isValid(DECIMAL_MINUTE, BigDecimal.ZERO),
            is(true));
        assertThat(
            PlainTime.midnightAtEndOfDay()
                .isValid(DECIMAL_MINUTE, BigDecimal.ONE),
            is(false));
        assertThat(
            PlainTime.midnightAtStartOfDay()
                .isValid(DECIMAL_MINUTE, new BigDecimal("60")),
            is(false));
    }

    @Test
    public void withDecimalMinute() {
        assertThat(
            PlainTime.of(12)
                .with(DECIMAL_MINUTE, new BigDecimal("26.883127572016666")),
            is(PlainTime.of(12, 26, 52, 987654321)));
        assertThat(
            PlainTime.midnightAtEndOfDay()
                .with(DECIMAL_MINUTE, BigDecimal.ZERO),
            is(PlainTime.of(24)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDecimalMinuteIfT24() {
        PlainTime.midnightAtEndOfDay().with(DECIMAL_MINUTE, BigDecimal.ONE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDecimalMinute60() {
        PlainTime.of(4, 15).with(DECIMAL_MINUTE, new BigDecimal("60"));
    }

    @Test
    public void containsDecimalSecond() {
        PlainTime any = PlainTime.of(12, 26, 52, 987654321);
        assertThat(any.contains(DECIMAL_SECOND), is(true));
    }

    @Test
    public void getDecimalSecond() {
        assertThat(
            PlainTime.of(12, 26, 52, 987654321).get(DECIMAL_SECOND),
            is(new BigDecimal("52.987654321")));
        assertThat(
            PlainTime.MAX.minus(1, ClockUnit.NANOS).get(DECIMAL_SECOND),
            is(new BigDecimal("59.999999999")));
        assertThat(
            PlainTime.midnightAtEndOfDay().get(DECIMAL_SECOND),
            is(BigDecimal.ZERO));
    }

    @Test
    public void getMinimumDecimalSecond() {
        assertThat(
            PlainTime.of(21, 45).getMinimum(DECIMAL_SECOND),
            is(BigDecimal.ZERO));
    }

    @Test
    public void getMaximumDecimalSecond() {
        assertThat(
            PlainTime.of(21, 45).getMaximum(DECIMAL_SECOND),
            is(new BigDecimal("59.999999999999999")));
    }

    @Test
    public void getMaximumDecimalSecondT24() {
        assertThat(
            PlainTime.midnightAtEndOfDay().getMaximum(DECIMAL_SECOND),
            is(BigDecimal.ZERO));
    }

    @Test
    public void isValidDecimalSecond() {
        assertThat(
            PlainTime.of(12, 26, 52, 987654321)
                .isValid(DECIMAL_SECOND, BigDecimal.ZERO),
            is(true));
        assertThat(
            PlainTime.midnightAtEndOfDay()
                .isValid(DECIMAL_SECOND, BigDecimal.ZERO),
            is(true));
        assertThat(
            PlainTime.midnightAtEndOfDay()
                .isValid(DECIMAL_SECOND, BigDecimal.ONE),
            is(false));
        assertThat(
            PlainTime.midnightAtStartOfDay()
                .isValid(DECIMAL_SECOND, new BigDecimal("60")),
            is(false));
    }

    @Test
    public void withDecimalSecond() {
        assertThat(
            PlainTime.of(12, 26)
                .with(DECIMAL_SECOND, new BigDecimal("52.987654321000000")),
            is(PlainTime.of(12, 26, 52, 987654321)));
        assertThat(
            PlainTime.of(12, 26)
                .with(DECIMAL_SECOND, new BigDecimal("59.999999999")),
            is(PlainTime.of(12, 26, 59, 999999999)));
        assertThat(
            PlainTime.midnightAtEndOfDay()
                .with(DECIMAL_SECOND, BigDecimal.ZERO),
            is(PlainTime.of(24)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDecimalSecondIfT24() {
        PlainTime.midnightAtEndOfDay().with(DECIMAL_SECOND, BigDecimal.ONE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDecimalSecond60() {
        PlainTime.of(4).with(DECIMAL_SECOND, new BigDecimal("60"));
    }

}