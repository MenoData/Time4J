package net.time4j.scale;

import net.time4j.ClockUnit;
import net.time4j.Meridiem;
import net.time4j.Moment;
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.Quarter;
import net.time4j.SI;
import net.time4j.Weekday;
import net.time4j.base.GregorianMath;
import net.time4j.engine.ChronoException;
import net.time4j.engine.Chronology;
import net.time4j.tz.ZonalOffset;

import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.PlainDate.*;
import static net.time4j.PlainTime.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class MomentPropertiesTest {

    private static final long MIO = 1000000L;
    private static final long MRD = 1000000000L;

    @Test
    public void axis() {
        assertThat(
            (Moment.axis() == Chronology.lookup(Moment.class)),
            is(true));
    }

    private Moment utc;

    @Before
    public void setUp() {
        this.utc =
            PlainTimestamp.of(
                PlainDate.of(2012, 6, 30),
                PlainTime.of(23, 59, 59, 123456789)
            ).inTimezone(ZonalOffset.UTC).plus(1, SI.SECONDS);

    }

    @Test
    public void testToString() {
        assertThat(
            this.utc.toString(),
            is("2012-06-30T23:59:60,123456789Z"));
    }

    @Test
    public void testToStringEpoch() {
        assertThat(
            Moment.UNIX_EPOCH.toString(),
            is("1970-01-01T00:00:00Z"));
    }

    @Test
    public void containsAmPm() {
        assertThat(this.utc.contains(AM_PM_OF_DAY), is(true));
    }

    @Test
    public void getAmPm() {
        assertThat(this.utc.get(AM_PM_OF_DAY), is(Meridiem.PM));
    }

    @Test(expected=ChronoException.class)
    public void getBaseUnitAmPm() {
        Moment.axis().getBaseUnit(AM_PM_OF_DAY);
    }

    @Test
    public void getMinimumAmPm() {
        assertThat(this.utc.getMinimum(AM_PM_OF_DAY), is(Meridiem.AM));
    }

    @Test
    public void getMaximumAmPm() {
        assertThat(this.utc.getMaximum(AM_PM_OF_DAY), is(Meridiem.PM));
    }

    @Test
    public void isValidAmPm() {
        assertThat(this.utc.isValid(AM_PM_OF_DAY, Meridiem.AM), is(true));
    }

    @Test
    public void isValidAmPmNull() {
        assertThat(this.utc.isValid(AM_PM_OF_DAY, null), is(false));
    }

    @Test
    public void withAmPm() {
        assertThat(
            this.utc.with(AM_PM_OF_DAY, Meridiem.AM),
            is(
                PlainTimestamp.of(2012, 6, 30, 11, 59, 59)
                .plus(123456789, ClockUnit.NANOS)
                .inTimezone(ZonalOffset.UTC)));
    }

    @Test(expected=NullPointerException.class)
    public void withAmPmNull() {
        this.utc.with(AM_PM_OF_DAY, null);
    }

    @Test
    public void containsMinuteOfDay() {
        assertThat(this.utc.contains(MINUTE_OF_DAY), is(true));
    }

    @Test
    public void getMinuteOfDay() {
        assertThat(
            this.utc.get(MINUTE_OF_DAY),
            is(1439));
    }

    @Test(expected=ChronoException.class)
    public void getBaseUnitMinuteOfDay() {
        Moment.axis().getBaseUnit(MINUTE_OF_DAY);
    }

    @Test
    public void getMinimumMinuteOfDay() {
        assertThat(this.utc.getMinimum(MINUTE_OF_DAY), is(0));
    }

    @Test
    public void getMaximumMinuteOfDay() {
        assertThat(this.utc.getMaximum(MINUTE_OF_DAY), is(1439));
    }

    @Test
    public void isValidMinuteOfDay() {
        assertThat(this.utc.isValid(MINUTE_OF_DAY, 1439), is(true));
    }

    @Test
    public void isValidMinuteOfDayNull() {
        assertThat(this.utc.isValid(MINUTE_OF_DAY, null), is(false));
    }

    @Test
    public void isValidMinuteOfDay1440() {
        assertThat(this.utc.isValid(MINUTE_OF_DAY, 1440), is(false));
    }

    @Test
    public void withMinuteOfDay() {
        assertThat(
            this.utc.with(MINUTE_OF_DAY, 1439),
            is(this.utc));
    }

    @Test(expected=NullPointerException.class)
    public void withMinuteOfDayNull() {
        this.utc.with(MINUTE_OF_DAY, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMinuteOfDay1440() {
        this.utc.with(MINUTE_OF_DAY, 1440);
    }

    @Test
    public void containsSecondOfDay() {
        assertThat(this.utc.contains(SECOND_OF_DAY), is(true));
    }

    @Test
    public void getSecondOfDay() {
        assertThat(
            this.utc.get(SECOND_OF_DAY),
            is(86399));
    }

    @Test(expected=ChronoException.class)
    public void getBaseUnitSecondOfDay() {
        Moment.axis().getBaseUnit(SECOND_OF_DAY);
    }

    @Test
    public void getMinimumSecondOfDay() {
        assertThat(this.utc.getMinimum(SECOND_OF_DAY), is(0));
    }

    @Test
    public void getMaximumSecondOfDay() {
        assertThat(this.utc.getMaximum(SECOND_OF_DAY), is(86399));
    }

    @Test
    public void isValidSecondOfDay() {
        assertThat(this.utc.isValid(SECOND_OF_DAY, 86399), is(true));
    }

    @Test
    public void isValidSecondOfDayNull() {
        assertThat(this.utc.isValid(SECOND_OF_DAY, null), is(false));
    }

    @Test
    public void isValidSecondOfDay86400() {
        assertThat(this.utc.isValid(SECOND_OF_DAY, 86400), is(false));
    }

    @Test
    public void withSecondOfDay() {
        assertThat(
            this.utc.with(SECOND_OF_DAY, 86399),
            is(this.utc));
    }

    @Test(expected=NullPointerException.class)
    public void withSecondOfDayNull() {
        this.utc.with(SECOND_OF_DAY, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withSecondOfDay86400() {
        this.utc.with(SECOND_OF_DAY, 86400);
    }

    @Test
    public void containsMilliOfDay() {
        assertThat(this.utc.contains(MILLI_OF_DAY), is(true));
    }

    @Test
    public void getMilliOfDay() {
        assertThat(
            this.utc.get(MILLI_OF_DAY),
            is(86399 * 1000 + 123));
    }

    @Test(expected=ChronoException.class)
    public void getBaseUnitMilliOfDay() {
        Moment.axis().getBaseUnit(MILLI_OF_DAY);
    }

    @Test
    public void getMinimumMilliOfDay() {
        assertThat(this.utc.getMinimum(MILLI_OF_DAY), is(0));
    }

    @Test
    public void getMaximumMilliOfDay() {
        assertThat(this.utc.getMaximum(MILLI_OF_DAY), is(86400 * 1000 - 1));
    }

    @Test
    public void isValidMilliOfDay() {
        assertThat(this.utc.isValid(MILLI_OF_DAY, 86399999), is(true));
    }

    @Test
    public void isValidMilliOfDayNull() {
        assertThat(this.utc.isValid(MILLI_OF_DAY, null), is(false));
    }

    @Test
    public void isValidMilliOfDayT24() {
        assertThat(this.utc.isValid(MILLI_OF_DAY, 86400000), is(false));
    }

    @Test
    public void withMilliOfDay() {
        assertThat(
            this.utc.with(MILLI_OF_DAY, 86399999),
            is(this.utc.plus((999 - 123) * MIO, SI.NANOSECONDS)));
    }

    @Test(expected=NullPointerException.class)
    public void withMilliOfDayNull() {
        this.utc.with(MILLI_OF_DAY, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMilliOfDayT24() {
        this.utc.with(MILLI_OF_DAY, 86400000);
    }

    @Test
    public void containsMicroOfDay() {
        assertThat(this.utc.contains(MICRO_OF_DAY), is(true));
    }

    @Test
    public void getMicroOfDay() {
        assertThat(
            this.utc.get(MICRO_OF_DAY),
            is(86399 * MIO + 123456));
    }

    @Test(expected=ChronoException.class)
    public void getBaseUnitMicroOfDay() {
        Moment.axis().getBaseUnit(MICRO_OF_DAY);
    }

    @Test
    public void getMinimumMicroOfDay() {
        assertThat(this.utc.getMinimum(MICRO_OF_DAY), is(0L));
    }

    @Test
    public void getMaximumMicroOfDay() {
        assertThat(this.utc.getMaximum(MICRO_OF_DAY), is(86400 * MIO - 1));
    }

    @Test
    public void isValidMicroOfDay() {
        assertThat(this.utc.isValid(MICRO_OF_DAY, 86399999999L), is(true));
    }

    @Test
    public void isValidMicroOfDayNull() {
        assertThat(this.utc.isValid(MICRO_OF_DAY, null), is(false));
    }

    @Test
    public void isValidMicroOfDayT24() {
        assertThat(this.utc.isValid(MICRO_OF_DAY, 86400000000L), is(false));
    }

    @Test
    public void withMicroOfDay() {
        assertThat(
            this.utc.with(MICRO_OF_DAY, 86399999999L),
            is(this.utc.plus((999999 - 123456) * 1000L, SI.NANOSECONDS)));
    }

    @Test(expected=NullPointerException.class)
    public void withMicroOfDayNull() {
        this.utc.with(MICRO_OF_DAY, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMicroOfDayT24() {
        this.utc.with(MICRO_OF_DAY, 86400 * MIO);
    }

    @Test
    public void containsNanoOfDay() {
        assertThat(this.utc.contains(NANO_OF_DAY), is(true));
    }

    @Test
    public void getNanoOfDay() {
        assertThat(
            this.utc.get(NANO_OF_DAY),
            is(86399 * MRD + 123456789));
    }

    @Test(expected=ChronoException.class)
    public void getBaseUnitNanoOfDay() {
        Moment.axis().getBaseUnit(NANO_OF_DAY);
    }

    @Test
    public void getMinimumNanoOfDay() {
        assertThat(this.utc.getMinimum(NANO_OF_DAY), is(0L));
    }

    @Test
    public void getMaximumNanoOfDay() {
        assertThat(this.utc.getMaximum(NANO_OF_DAY), is(86400 * MRD - 1));
    }

    @Test
    public void isValidNanoOfDay() {
        assertThat(this.utc.isValid(NANO_OF_DAY, 86399 * MRD), is(true));
    }

    @Test
    public void isValidNanoOfDayNull() {
        assertThat(this.utc.isValid(NANO_OF_DAY, null), is(false));
    }

    @Test
    public void isValidNanoOfDayT24() {
        assertThat(this.utc.isValid(NANO_OF_DAY, 86400 * MRD), is(false));
    }

    @Test
    public void withNanoOfDay() {
        assertThat(
            this.utc.with(NANO_OF_DAY, 86399999999999L),
            is(this.utc.plus((999999999 - 123456789), SI.NANOSECONDS)));
    }

    @Test(expected=NullPointerException.class)
    public void withNanoOfDayNull() {
        this.utc.with(NANO_OF_DAY, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withNanoOfDayT24() {
        this.utc.with(NANO_OF_DAY, 86400 * MRD);
    }

    @Test
    public void containsIsoHour() {
        assertThat(this.utc.contains(ISO_HOUR), is(true));
    }

    @Test
    public void getIsoHour() {
        assertThat(this.utc.get(ISO_HOUR), is(23));
    }

    @Test(expected=ChronoException.class)
    public void getBaseUnitIsoHour() {
        Moment.axis().getBaseUnit(ISO_HOUR);
    }

    @Test
    public void getMinimumIsoHour() {
        assertThat(this.utc.getMinimum(ISO_HOUR), is(0));
    }

    @Test
    public void getMaximumIsoHour() {
        assertThat(this.utc.getMaximum(ISO_HOUR), is(23));
    }

    @Test
    public void isValidIsoHour() {
        assertThat(this.utc.isValid(ISO_HOUR, 23), is(true));
    }

    @Test
    public void isValidIsoHourNull() {
        assertThat(this.utc.isValid(ISO_HOUR, null), is(false));
    }

    @Test
    public void isValidIsoHour24() {
        assertThat(this.utc.isValid(ISO_HOUR, 24), is(false));
    }

    @Test
    public void withIsoHour() {
        assertThat(
            this.utc.with(ISO_HOUR, 23),
            is(this.utc));
    }

    @Test(expected=NullPointerException.class)
    public void withIsoHourNull() {
        this.utc.with(ISO_HOUR, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withIsoHour24() {
        this.utc.with(ISO_HOUR, 24);
    }

    @Test
    public void containsMinuteOfHour() {
        assertThat(this.utc.contains(MINUTE_OF_HOUR), is(true));
    }

    @Test
    public void getMinuteOfHour() {
        assertThat(
            this.utc.get(MINUTE_OF_HOUR),
            is(59));
    }

    @Test(expected=ChronoException.class)
    public void getBaseUnitMinuteOfHour() {
        Moment.axis().getBaseUnit(MINUTE_OF_HOUR);
    }

    @Test
    public void getMinimumMinuteOfHour() {
        assertThat(this.utc.getMinimum(MINUTE_OF_HOUR), is(0));
    }

    @Test
    public void getMaximumMinuteOfHour() {
        assertThat(this.utc.getMaximum(MINUTE_OF_HOUR), is(59));
    }

    @Test
    public void isValidMinuteOfHour() {
        assertThat(this.utc.isValid(MINUTE_OF_HOUR, 59), is(true));
    }

    @Test
    public void isValidMinuteOfHourNull() {
        assertThat(this.utc.isValid(MINUTE_OF_HOUR, null), is(false));
    }

    @Test
    public void isValidMinuteOfHour60() {
        assertThat(this.utc.isValid(MINUTE_OF_HOUR, 60), is(false));
    }

    @Test
    public void withMinuteOfHour() {
        assertThat(
            this.utc.with(MINUTE_OF_HOUR, 59),
            is(this.utc));
    }

    @Test(expected=NullPointerException.class)
    public void withMinuteOfHourNull() {
        this.utc.with(MINUTE_OF_HOUR, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMinuteOfHour60() {
        this.utc.with(MINUTE_OF_HOUR, 60);
    }

    @Test
    public void containsSecondOfMinute() {
        assertThat(this.utc.contains(SECOND_OF_MINUTE), is(true));
    }

    @Test
    public void getSecondOfMinute() {
        assertThat(
            this.utc.get(SECOND_OF_MINUTE),
            is(60));
    }

    @Test(expected=ChronoException.class)
    public void getBaseUnitSecondOfMinute() {
        Moment.axis().getBaseUnit(SECOND_OF_MINUTE);
    }

    @Test
    public void getMinimumSecondOfMinute() {
        assertThat(this.utc.getMinimum(SECOND_OF_MINUTE), is(0));
    }

    @Test
    public void getMaximumSecondOfMinute() {
        assertThat(this.utc.getMaximum(SECOND_OF_MINUTE), is(60));
    }

    @Test
    public void isValidSecondOfMinute() {
        assertThat(this.utc.isValid(SECOND_OF_MINUTE, 59), is(true));
    }

    @Test
    public void isValidSecondOfMinuteNull() {
        assertThat(this.utc.isValid(SECOND_OF_MINUTE, null), is(false));
    }

    @Test
    public void isValidSecondOfMinute60() {
        assertThat(this.utc.isValid(SECOND_OF_MINUTE, 60), is(true));
    }

    @Test
    public void withSecondOfMinute() {
        assertThat(
            this.utc.with(SECOND_OF_MINUTE, 60),
            is(this.utc));
    }

    @Test(expected=NullPointerException.class)
    public void withSecondOfMinuteNull() {
        this.utc.with(SECOND_OF_MINUTE, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withSecondOfMinute60() {
        PlainTimestamp.of(2010, 4, 21, 9, 15)
            .inTimezone(ZonalOffset.UTC)
            .with(SECOND_OF_MINUTE, 60);
    }

    @Test
    public void containsMilliOfSecond() {
        assertThat(this.utc.contains(MILLI_OF_SECOND), is(true));
    }

    @Test
    public void getMilliOfSecond() {
        assertThat(
            this.utc.get(MILLI_OF_SECOND),
            is(123));
    }

    @Test(expected=ChronoException.class)
    public void getBaseUnitMilliOfSecond() {
        Moment.axis().getBaseUnit(MILLI_OF_SECOND);
    }

    @Test
    public void getMinimumMilliOfSecond() {
        assertThat(this.utc.getMinimum(MILLI_OF_SECOND), is(0));
    }

    @Test
    public void getMaximumMilliOfSecond() {
        assertThat(this.utc.getMaximum(MILLI_OF_SECOND), is(999));
    }

    @Test
    public void isValidMilliOfSecond() {
        assertThat(this.utc.isValid(MILLI_OF_SECOND, 999), is(true));
    }

    @Test
    public void isValidMilliOfSecondNull() {
        assertThat(this.utc.isValid(MILLI_OF_SECOND, null), is(false));
    }

    @Test
    public void isValidMilliOfSecond1000() {
        assertThat(this.utc.isValid(MILLI_OF_SECOND, 1000), is(false));
    }

    @Test
    public void withMilliOfSecond() {
        assertThat(
            this.utc.with(MILLI_OF_SECOND, 999),
            is(this.utc.plus((999 - 123) * MIO, SI.NANOSECONDS)));
    }

    @Test(expected=NullPointerException.class)
    public void withMilliOfSecondNull() {
        this.utc.with(MILLI_OF_SECOND, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMilliOfSecond1000() {
        this.utc.with(MILLI_OF_SECOND, 1000);
    }

    @Test
    public void containsMicroOfSecond() {
        assertThat(this.utc.contains(MICRO_OF_SECOND), is(true));
    }

    @Test
    public void getMicroOfSecond() {
        assertThat(
            this.utc.get(MICRO_OF_SECOND),
            is(123456));
    }

    @Test(expected=ChronoException.class)
    public void getBaseUnitMicroOfSecond() {
        Moment.axis().getBaseUnit(MICRO_OF_SECOND);
    }

    @Test
    public void getMinimumMicroOfSecond() {
        assertThat(this.utc.getMinimum(MICRO_OF_SECOND), is(0));
    }

    @Test
    public void getMaximumMicroOfSecond() {
        assertThat(this.utc.getMaximum(MICRO_OF_SECOND), is(999999));
    }

    @Test
    public void isValidMicroOfSecond() {
        assertThat(this.utc.isValid(MICRO_OF_SECOND, 999999), is(true));
    }

    @Test
    public void isValidMicroOfSecondNull() {
        assertThat(this.utc.isValid(MICRO_OF_SECOND, null), is(false));
    }

    @Test
    public void isValidMicroOfSecondMIO() {
        assertThat(this.utc.isValid(MICRO_OF_SECOND, 1000000), is(false));
    }

    @Test
    public void withMicroOfSecond() {
        assertThat(
            this.utc.with(MICRO_OF_SECOND, 999999),
            is(this.utc.plus((999999 - 123456) * 1000, SI.NANOSECONDS)));
    }

    @Test(expected=NullPointerException.class)
    public void withMicroOfSecondNull() {
        this.utc.with(MICRO_OF_SECOND, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMicroOfSecondMIO() {
        this.utc.with(MICRO_OF_SECOND, 1000000);
    }

    @Test
    public void containsNanoOfSecond() {
        assertThat(this.utc.contains(NANO_OF_SECOND), is(true));
    }

    @Test
    public void getNanoOfSecond() {
        assertThat(
            this.utc.get(NANO_OF_SECOND),
            is(123456789));
    }

    @Test(expected=ChronoException.class)
    public void getBaseUnitNanoOfSecond() {
        Moment.axis().getBaseUnit(NANO_OF_SECOND);
    }

    @Test
    public void getMinimumNanoOfSecond() {
        assertThat(this.utc.getMinimum(NANO_OF_SECOND), is(0));
    }

    @Test
    public void getMaximumNanoOfSecond() {
        assertThat(this.utc.getMaximum(NANO_OF_SECOND), is(999999999));
    }

    @Test
    public void isValidNanoOfSecond() {
        assertThat(this.utc.isValid(NANO_OF_SECOND, 999999999), is(true));
    }

    @Test
    public void isValidNanoOfSecondNull() {
        assertThat(this.utc.isValid(NANO_OF_SECOND, null), is(false));
    }

    @Test
    public void isValidNanoOfSecondMRD() {
        assertThat(this.utc.isValid(NANO_OF_SECOND, 1000000000), is(false));
    }

    @Test
    public void withNanoOfSecond() {
        assertThat(
            this.utc.with(NANO_OF_SECOND, 123456789),
            is(this.utc));
    }

    @Test(expected=NullPointerException.class)
    public void withNanoOfSecondNull() {
        this.utc.with(NANO_OF_SECOND, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withNanoOfSecondMRD() {
        this.utc.with(NANO_OF_SECOND, 1000000000);
    }

    @Test
    public void containsClockHourOfAmPm() {
        assertThat(this.utc.contains(CLOCK_HOUR_OF_AMPM), is(true));
    }

    @Test
    public void getClockHourOfAmPm() {
        assertThat(
            this.utc.get(CLOCK_HOUR_OF_AMPM),
            is(11));
    }

    @Test(expected=ChronoException.class)
    public void getBaseUnitClockHourOfAmPm() {
        Moment.axis().getBaseUnit(CLOCK_HOUR_OF_AMPM);
    }

    @Test
    public void getMinimumClockHourOfAmPm() {
        assertThat(this.utc.getMinimum(CLOCK_HOUR_OF_AMPM), is(1));
    }

    @Test
    public void getMaximumClockHourOfAmPm() {
        assertThat(this.utc.getMaximum(CLOCK_HOUR_OF_AMPM), is(12));
    }

    @Test
    public void isValidClockHourOfAmPm() {
        assertThat(this.utc.isValid(CLOCK_HOUR_OF_AMPM, 12), is(true));
    }

    @Test
    public void isValidClockHourOfAmPmNull() {
        assertThat(this.utc.isValid(CLOCK_HOUR_OF_AMPM, null), is(false));
    }

    @Test
    public void isValidClockHourOfAmPm0() {
        assertThat(this.utc.isValid(CLOCK_HOUR_OF_AMPM, 0), is(false));
    }

    @Test
    public void withClockHourOfAmPm() {
        assertThat(
            this.utc.with(CLOCK_HOUR_OF_AMPM, 12),
            is(
                PlainDate.of(2012, 6, 30)
                .at(PlainTime.of(12, 59, 59, 123456789))
                .inTimezone(ZonalOffset.UTC)));
    }

    @Test(expected=NullPointerException.class)
    public void withClockHourOfAmPmNull() {
        this.utc.with(CLOCK_HOUR_OF_AMPM, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withClockHourOfAmPm0() {
        this.utc.with(CLOCK_HOUR_OF_AMPM, 0);
    }

    @Test
    public void containsClockHourOfDay() {
        assertThat(this.utc.contains(CLOCK_HOUR_OF_DAY), is(true));
    }

    @Test
    public void getClockHourOfDay() {
        assertThat(
            this.utc.get(CLOCK_HOUR_OF_DAY),
            is(23));
    }

    @Test(expected=ChronoException.class)
    public void getBaseUnitClockHourOfDay() {
        Moment.axis().getBaseUnit(CLOCK_HOUR_OF_DAY);
    }

    @Test
    public void getMinimumClockHourOfDay() {
        assertThat(this.utc.getMinimum(CLOCK_HOUR_OF_DAY), is(1));
    }

    @Test
    public void getMaximumClockHourOfDay() {
        assertThat(this.utc.getMaximum(CLOCK_HOUR_OF_DAY), is(24));
    }

    @Test
    public void isValidClockHourOfDay() {
        assertThat(this.utc.isValid(CLOCK_HOUR_OF_DAY, 24), is(true));
    }

    @Test
    public void isValidClockHourOfDayNull() {
        assertThat(this.utc.isValid(CLOCK_HOUR_OF_DAY, null), is(false));
    }

    @Test
    public void isValidClockHourOfDay0() {
        assertThat(this.utc.isValid(CLOCK_HOUR_OF_DAY, 0), is(false));
    }

    @Test
    public void withClockHourOfDay() {
        assertThat(
            this.utc.with(CLOCK_HOUR_OF_DAY, 23),
            is(this.utc));
    }

    @Test(expected=NullPointerException.class)
    public void withClockHourOfDayNull() {
        this.utc.with(CLOCK_HOUR_OF_DAY, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withClockHourOfDay0() {
        this.utc.with(CLOCK_HOUR_OF_DAY, 0);
    }

    @Test
    public void containsDigitalHourOfAmPm() {
        assertThat(this.utc.contains(DIGITAL_HOUR_OF_AMPM), is(true));
    }

    @Test
    public void getDigitalHourOfAmPm() {
        assertThat(
            this.utc.get(DIGITAL_HOUR_OF_AMPM),
            is(11));
    }

    @Test(expected=ChronoException.class)
    public void getBaseUnitDigitalHourOfAmPm() {
        Moment.axis().getBaseUnit(DIGITAL_HOUR_OF_AMPM);
    }

    @Test
    public void getMinimumDigitalHourOfAmPm() {
        assertThat(this.utc.getMinimum(DIGITAL_HOUR_OF_AMPM), is(0));
    }

    @Test
    public void getMaximumDigitalHourOfAmPm() {
        assertThat(this.utc.getMaximum(DIGITAL_HOUR_OF_AMPM), is(11));
    }

    @Test
    public void isValidDigitalHourOfAmPm() {
        assertThat(this.utc.isValid(DIGITAL_HOUR_OF_AMPM, 0), is(true));
        assertThat(this.utc.isValid(DIGITAL_HOUR_OF_AMPM, 11), is(true));
    }

    @Test
    public void isValidDigitalHourOfAmPmNull() {
        assertThat(this.utc.isValid(DIGITAL_HOUR_OF_AMPM, null), is(false));
    }

    @Test
    public void isValidDigitalHourOfAmPm12() {
        assertThat(this.utc.isValid(DIGITAL_HOUR_OF_AMPM, 12), is(false));
    }

    @Test
    public void withDigitalHourOfAmPm() {
        assertThat(
            this.utc.with(DIGITAL_HOUR_OF_AMPM, 11),
            is(this.utc));
    }

    @Test(expected=NullPointerException.class)
    public void withDigitalHourOfAmPmNull() {
        this.utc.with(DIGITAL_HOUR_OF_AMPM, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDigitalHourOfAmPm12() {
        this.utc.with(DIGITAL_HOUR_OF_AMPM, 12);
    }

    @Test
    public void containsDigitalHourOfDay() {
        assertThat(this.utc.contains(DIGITAL_HOUR_OF_DAY), is(true));
    }

    @Test
    public void getDigitalHourOfDay() {
        assertThat(
            this.utc.get(DIGITAL_HOUR_OF_DAY),
            is(23));
    }

    @Test(expected=ChronoException.class)
    public void getBaseUnitDigitalHourOfDay() {
        Moment.axis().getBaseUnit(DIGITAL_HOUR_OF_DAY);
    }

    @Test
    public void getMinimumDigitalHourOfDay() {
        assertThat(this.utc.getMinimum(DIGITAL_HOUR_OF_DAY), is(0));
    }

    @Test
    public void getMaximumDigitalHourOfDay() {
        assertThat(this.utc.getMaximum(DIGITAL_HOUR_OF_DAY), is(23));
    }

    @Test
    public void isValidDigitalHourOfDay() {
        assertThat(this.utc.isValid(DIGITAL_HOUR_OF_DAY, 0), is(true));
        assertThat(this.utc.isValid(DIGITAL_HOUR_OF_DAY, 23), is(true));
    }

    @Test
    public void isValidDigitalHourOfDayNull() {
        assertThat(this.utc.isValid(DIGITAL_HOUR_OF_DAY, null), is(false));
    }

    @Test
    public void isValidDigitalHourOfDay24() {
        assertThat(this.utc.isValid(DIGITAL_HOUR_OF_DAY, 24), is(false));
    }

    @Test
    public void withDigitalHourOfDay() {
        assertThat(
            this.utc.with(DIGITAL_HOUR_OF_DAY, 23),
            is(this.utc));
    }

    @Test(expected=NullPointerException.class)
    public void withDigitalHourOfDayNull() {
        this.utc.with(DIGITAL_HOUR_OF_DAY, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDigitalHourOfDay24() {
        this.utc.with(DIGITAL_HOUR_OF_DAY, 24);
    }

    @Test
    public void containsPrecision() {
        assertThat(this.utc.contains(PRECISION), is(false));
    }

    @Test(expected=ChronoException.class)
    public void getPrecision() {
        this.utc.get(PRECISION);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumPrecision() {
        this.utc.getMinimum(PRECISION);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumPrecision() {
        this.utc.getMaximum(PRECISION);
    }

    @Test
    public void isValidPrecision() {
        assertThat(
            this.utc.isValid(PRECISION, ClockUnit.HOURS),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withPrecision() {
        this.utc.with(PRECISION, ClockUnit.HOURS);
    }

    @Test
    public void containsDecimalHour() {
        assertThat(this.utc.contains(DECIMAL_HOUR), is(false));
    }

    @Test(expected=ChronoException.class)
    public void getDecimalHour() {
        this.utc.get(DECIMAL_HOUR);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumDecimalHour() {
        this.utc.getMinimum(DECIMAL_HOUR);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumDecimalHour() {
        this.utc.getMaximum(DECIMAL_HOUR);
    }

    @Test
    public void isValidDecimalHour() {
        assertThat(
            this.utc.isValid(DECIMAL_HOUR, BigDecimal.ZERO),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withDecimalHour() {
        this.utc.with(DECIMAL_HOUR, BigDecimal.ZERO);
    }

    @Test
    public void containsDecimalMinute() {
        assertThat(this.utc.contains(DECIMAL_MINUTE), is(false));
    }

    @Test(expected=ChronoException.class)
    public void getDecimalMinute() {
        this.utc.get(DECIMAL_MINUTE);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumDecimalMinute() {
        this.utc.getMinimum(DECIMAL_MINUTE);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumDecimalMinute() {
        this.utc.getMaximum(DECIMAL_MINUTE);
    }

    @Test
    public void isValidDecimalMinute() {
        assertThat(
            this.utc.isValid(DECIMAL_MINUTE, BigDecimal.ZERO),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withDecimalMinute() {
        this.utc.with(DECIMAL_MINUTE, BigDecimal.ZERO);
    }

    @Test
    public void containsDecimalSecond() {
        assertThat(this.utc.contains(DECIMAL_SECOND), is(false));
    }

    @Test(expected=ChronoException.class)
    public void getDecimalSecond() {
        this.utc.get(DECIMAL_SECOND);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumDecimalSecond() {
        this.utc.getMinimum(DECIMAL_SECOND);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumDecimalSecond() {
        this.utc.getMaximum(DECIMAL_SECOND);
    }

    @Test
    public void isValidDecimalSecond() {
        assertThat(
            this.utc.isValid(DECIMAL_SECOND, BigDecimal.ZERO),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withDecimalSecond() {
        this.utc.with(DECIMAL_SECOND, BigDecimal.ZERO);
    }

    @Test
    public void containsDayOfMonth() {
        assertThat(
            this.utc.contains(DAY_OF_MONTH),
            is(true));
    }

    @Test
    public void getDayOfMonth() {
        assertThat(
            this.utc.get(DAY_OF_MONTH),
            is(30));
    }

    @Test
    public void getMinimumDayOfMonth() {
        assertThat(
            this.utc.getMinimum(DAY_OF_MONTH),
            is(1));
    }

    @Test
    public void getMaximumDayOfMonth() {
        assertThat(
            this.utc.getMaximum(DAY_OF_MONTH),
            is(30));
    }

    @Test
    public void isValidDayOfMonth() {
        assertThat(
            this.utc.isValid(DAY_OF_MONTH, 11),
            is(true));
    }

    @Test
    public void withDayOfMonth() {
        assertThat(
            this.utc.with(DAY_OF_MONTH, 30),
            is(this.utc));
    }

    @Test
    public void containsDayOfWeek() {
        assertThat(
            this.utc.contains(DAY_OF_WEEK),
            is(true));
    }

    @Test
    public void getDayOfWeek() {
        assertThat(
            this.utc.get(DAY_OF_WEEK),
            is(Weekday.SATURDAY));
    }

    @Test
    public void getMinimumDayOfWeek() {
        assertThat(
            this.utc.getMinimum(DAY_OF_WEEK),
            is(Weekday.MONDAY));
    }

    @Test
    public void getMaximumDayOfWeek() {
        assertThat(
            this.utc.getMaximum(DAY_OF_WEEK),
            is(Weekday.SUNDAY));
    }

    @Test
    public void isValidDayOfWeek() {
        assertThat(
            this.utc.isValid(DAY_OF_WEEK, Weekday.MONDAY),
            is(true));
    }

    @Test
    public void withDayOfWeek() {
        assertThat(
            this.utc.with(DAY_OF_WEEK, Weekday.SATURDAY),
            is(this.utc));
    }

    @Test
    public void containsDayOfYear() {
        assertThat(
            this.utc.contains(DAY_OF_YEAR),
            is(true));
    }

    @Test
    public void getDayOfYear() {
        assertThat(
            this.utc.get(DAY_OF_YEAR),
            is(31 + 29 + 31 + 30 + 31 + 30));
    }

    @Test
    public void getMinimumDayOfYear() {
        assertThat(
            this.utc.getMinimum(DAY_OF_YEAR),
            is(1));
    }

    @Test
    public void getMaximumDayOfYear() {
        assertThat(
            this.utc.getMaximum(DAY_OF_YEAR),
            is(366));
    }

    @Test
    public void isValidDayOfYear() {
        assertThat(
            this.utc.isValid(DAY_OF_YEAR, 1),
            is(true));
    }

    @Test
    public void withDayOfYear() {
        assertThat(
            this.utc.with(DAY_OF_YEAR, 1),
            is(
                PlainTimestamp.of(2012, 1, 1, 23, 59, 59)
                .plus(123456789, ClockUnit.NANOS)
                .atUTC()));
    }

    @Test
    public void containsDayOfQuarter() {
        assertThat(
            this.utc.contains(DAY_OF_QUARTER),
            is(true));
    }

    @Test
    public void getDayOfQuarter() {
        assertThat(
            this.utc.get(DAY_OF_QUARTER),
            is(91));
    }

    @Test
    public void getMinimumDayOfQuarter() {
        assertThat(
            this.utc.getMinimum(DAY_OF_QUARTER),
            is(1));
    }

    @Test
    public void getMaximumDayOfQuarter() {
        assertThat(
            this.utc.getMaximum(DAY_OF_QUARTER),
            is(91));
    }

    @Test
    public void isValidDayOfQuarter() {
        assertThat(
            this.utc.isValid(DAY_OF_QUARTER, 32),
            is(true));
    }

    @Test
    public void withDayOfQuarter() {
        assertThat(
            this.utc.with(DAY_OF_QUARTER, 91),
            is(this.utc));
    }

    @Test
    public void containsQuarterOfYear() {
        assertThat(
            this.utc.contains(QUARTER_OF_YEAR),
            is(true));
    }

    @Test
    public void getQuarterOfYear() {
        assertThat(
            this.utc.get(QUARTER_OF_YEAR),
            is(Quarter.Q2));
    }

    @Test
    public void getMinimumQuarterOfYear() {
        assertThat(
            this.utc.getMinimum(QUARTER_OF_YEAR),
            is(Quarter.Q1));
    }

    @Test
    public void getMaximumQuarterOfYear() {
        assertThat(
            this.utc.getMaximum(QUARTER_OF_YEAR),
            is(Quarter.Q4));
    }

    @Test
    public void isValidQuarterOfYear() {
        assertThat(
            this.utc.isValid(QUARTER_OF_YEAR, Quarter.Q3),
            is(true));
    }

    @Test
    public void withQuarterOfYear() {
        assertThat(
            this.utc.with(QUARTER_OF_YEAR, Quarter.Q2),
            is(this.utc));
    }

    @Test
    public void containsMonthAsNumber() {
        assertThat(
            this.utc.contains(MONTH_AS_NUMBER),
            is(true));
    }

    @Test
    public void getMonthAsNumber() {
        assertThat(
            this.utc.get(MONTH_AS_NUMBER),
            is(6));
    }

    @Test
    public void getMinimumMonthAsNumber() {
        assertThat(
            this.utc.getMinimum(MONTH_AS_NUMBER),
            is(1));
    }

    @Test
    public void getMaximumMonthAsNumber() {
        assertThat(
            this.utc.getMaximum(MONTH_AS_NUMBER),
            is(12));
    }

    @Test
    public void isValidMonthAsNumber() {
        assertThat(
            this.utc.isValid(MONTH_AS_NUMBER, 6),
            is(true));
    }

    @Test
    public void withMonthAsNumber() {
        assertThat(
            this.utc.with(MONTH_AS_NUMBER, 6),
            is(this.utc));
    }

    @Test
    public void containsMonthOfYear() {
        assertThat(
            this.utc.contains(MONTH_OF_YEAR),
            is(true));
    }

    @Test
    public void getMonthOfYear() {
        assertThat(
            this.utc.get(MONTH_OF_YEAR),
            is(Month.JUNE));
    }

    @Test
    public void getMinimumMonthOfYear() {
        assertThat(
            this.utc.getMinimum(MONTH_OF_YEAR),
            is(Month.JANUARY));
    }

    @Test
    public void getMaximumMonthOfYear() {
        assertThat(
            this.utc.getMaximum(MONTH_OF_YEAR),
            is(Month.DECEMBER));
    }

    @Test
    public void isValidMonthOfYear() {
        assertThat(
            this.utc.isValid(MONTH_OF_YEAR, Month.JUNE),
            is(true));
    }

    @Test
    public void withMonthOfYear() {
        assertThat(
            this.utc.with(MONTH_OF_YEAR, Month.JUNE),
            is(this.utc));
    }

    @Test
    public void containsWeekdayInMonth() {
        assertThat(
            this.utc.contains(WEEKDAY_IN_MONTH),
            is(true));
    }

    @Test
    public void getWeekdayInMonth() {
        assertThat(
            this.utc.get(WEEKDAY_IN_MONTH),
            is(5));
    }

    @Test
    public void getMinimumWeekdayInMonth() {
        assertThat(
            this.utc.getMinimum(WEEKDAY_IN_MONTH),
            is(1));
    }

    @Test
    public void getMaximumWeekdayInMonth() {
        assertThat(
            this.utc.getMaximum(WEEKDAY_IN_MONTH),
            is(5));
    }

    @Test
    public void isValidWeekdayInMonth() {
        assertThat(
            this.utc.isValid(WEEKDAY_IN_MONTH, 5),
            is(true));
    }

    @Test
    public void withWeekdayInMonth() {
        assertThat(
            this.utc.with(WEEKDAY_IN_MONTH, 5),
            is(this.utc));
    }

    @Test
    public void containsYear() {
        assertThat(
            this.utc.contains(YEAR),
            is(true));
    }

    @Test
    public void getYear() {
        assertThat(
            this.utc.get(YEAR),
            is(2012));
    }

    @Test
    public void getMinimumYear() {
        assertThat(
            this.utc.getMinimum(YEAR),
            is(GregorianMath.MIN_YEAR));
    }

    @Test
    public void getMaximumYear() {
        assertThat(
            this.utc.getMaximum(YEAR),
            is(GregorianMath.MAX_YEAR));
    }

    @Test
    public void isValidYear() {
        assertThat(
            this.utc.isValid(YEAR, 2013),
            is(true));
    }

    @Test
    public void withYear() {
        assertThat(
            this.utc.with(YEAR, 2013),
            is(
                PlainDate.of(2013, 6, 30)
                .at(PlainTime.of(23, 59, 59, 123456789))
                .inTimezone(ZonalOffset.UTC)));
    }

    @Test
    public void containsYearOfWeekdate() {
        assertThat(
            this.utc.contains(YEAR_OF_WEEKDATE),
            is(true));
    }

    @Test
    public void getYearOfWeekdate() {
        assertThat(
            this.utc.get(YEAR_OF_WEEKDATE),
            is(2012));
    }

    @Test
    public void getMinimumYearOfWeekdate() {
        assertThat(
            this.utc.getMinimum(YEAR_OF_WEEKDATE),
            is(GregorianMath.MIN_YEAR));
    }

    @Test
    public void getMaximumYearOfWeekdate() {
        assertThat(
            this.utc.getMaximum(YEAR_OF_WEEKDATE),
            is(GregorianMath.MAX_YEAR));
    }

    @Test
    public void isValidYearOfWeekdate() {
        assertThat(
            this.utc.isValid(YEAR_OF_WEEKDATE, 2013),
            is(true));
    }

    @Test
    public void withYearOfWeekdate() {
        assertThat(
            this.utc.with(YEAR_OF_WEEKDATE, 2013),
            is(
                PlainDate.of(2013, 6, 29) // gleiche KW + gleicher Wochentag
                .at(PlainTime.of(23, 59, 59, 123456789))
                .inTimezone(ZonalOffset.UTC)));
    }

}