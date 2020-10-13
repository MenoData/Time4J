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
import net.time4j.Weekmodel;
import net.time4j.engine.Chronology;
import net.time4j.engine.RuleNotFoundException;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.PlainDate.*;
import static net.time4j.PlainTime.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


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
    public void testToStringLS() {
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
    public void containsWeekOfYear() {
        assertThat(this.utc.contains(Weekmodel.ISO.weekOfYear()), is(false));
    }

    @Test
    public void getWeekOfYear() {
        assertThat(
            this.utc.get(Weekmodel.ISO.weekOfYear().atUTC()),
            is(26));
    }

    @Test
    public void withWeekOfYear() {
        assertThat(
            this.utc.with(
                Weekmodel.ISO.weekOfYear().newValue(1).atUTC()),
            is(
                PlainTimestamp.of(
                    PlainDate.of(2012, 1, 7),
                    PlainTime.of(23, 59, 59, 123456789)
                ).atUTC()));
    }

    @Test
    public void containsAmPm() {
        assertThat(this.utc.contains(AM_PM_OF_DAY), is(false));
    }

    @Test
    public void getAmPm() {
        assertThat(this.utc.get(AM_PM_OF_DAY.atUTC()), is(Meridiem.PM));
    }

    @Test(expected=RuleNotFoundException.class)
    public void withAmPm() {
        this.utc.with(AM_PM_OF_DAY, Meridiem.AM);
    }

    @Test
    public void containsMinuteOfDay() {
        assertThat(this.utc.contains(MINUTE_OF_DAY), is(false));
    }

    @Test
    public void getMinuteOfDay() {
        assertThat(
            this.utc.get(MINUTE_OF_DAY.atUTC()),
            is(1439));
    }

    @Test
    public void withMinuteOfDay() {
        assertThat(
            this.utc.with(MINUTE_OF_DAY.newValue(1439).atUTC()),
            is(this.utc));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMinuteOfDayNull() {
        this.utc.with(MINUTE_OF_DAY.newValue(null).atUTC());
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMinuteOfDay1440() {
        this.utc.with(MINUTE_OF_DAY.newValue(1440).atUTC());
    }

    @Test
    public void containsSecondOfDay() {
        assertThat(this.utc.contains(SECOND_OF_DAY), is(false));
    }

    @Test
    public void getSecondOfDay() {
        assertThat(
            this.utc.get(SECOND_OF_DAY.atUTC()),
            is(86399));
    }

    @Test
    public void withSecondOfDay() {
        assertThat(
            this.utc.with(SECOND_OF_DAY.newValue(86399).atUTC()),
            is(this.utc.minus(1, SI.SECONDS)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withSecondOfDayNull() {
        this.utc.with(SECOND_OF_DAY.newValue(null).atUTC());
    }

    @Test(expected=IllegalArgumentException.class)
    public void withSecondOfDay86400() {
        this.utc.with(SECOND_OF_DAY.newValue(86400).atUTC());
    }

    @Test
    public void containsMilliOfDay() {
        assertThat(this.utc.contains(MILLI_OF_DAY), is(false));
    }

    @Test
    public void getMilliOfDay() {
        assertThat(
            this.utc.get(MILLI_OF_DAY.atUTC()),
            is(86399 * 1000 + 123));
    }

    @Test
    public void withMilliOfDay() {
        assertThat(
            this.utc.with(MILLI_OF_DAY.newValue(86399999).atUTC()),
            is(
                this.utc
                    .plus((999 - 123) * MIO, SI.NANOSECONDS)
                    .minus(1, SI.SECONDS)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMilliOfDayNull() {
        this.utc.with(MILLI_OF_DAY.newValue(null).atUTC());
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMilliOfDayT24() {
        this.utc.with(MILLI_OF_DAY.newValue(86400000).atUTC());
    }

    @Test
    public void containsMicroOfDay() {
        assertThat(this.utc.contains(MICRO_OF_DAY), is(false));
    }

    @Test
    public void getMicroOfDay() {
        assertThat(
            this.utc.get(MICRO_OF_DAY.atUTC()),
            is(86399 * MIO + 123456));
    }

    @Test
    public void withMicroOfDay() {
        assertThat(
            this.utc.with(MICRO_OF_DAY.newValue(86399999999L).atUTC()),
            is(
                this.utc
                    .plus((999999 - 123456) * 1000L, SI.NANOSECONDS)
                    .minus(1, SI.SECONDS)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMicroOfDayNull() {
        this.utc.with(MICRO_OF_DAY.newValue(null).atUTC());
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMicroOfDayT24() {
        this.utc.with(MICRO_OF_DAY.newValue(86400 * MIO).atUTC());
    }

    @Test
    public void containsNanoOfDay() {
        assertThat(this.utc.contains(NANO_OF_DAY), is(false));
    }

    @Test
    public void getNanoOfDay() {
        assertThat(
            this.utc.get(NANO_OF_DAY.atUTC()),
            is(86399 * MRD + 123456789));
    }

    @Test
    public void withNanoOfDay() {
        assertThat(
            this.utc.with(NANO_OF_DAY.newValue(86399999999999L).atUTC()),
            is(
                this.utc
                    .plus((999999999 - 123456789), SI.NANOSECONDS)
                    .minus(1, SI.SECONDS)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withNanoOfDayNull() {
        this.utc.with(NANO_OF_DAY.newValue(null).atUTC());
    }

    @Test(expected=IllegalArgumentException.class)
    public void withNanoOfDayT24() {
        this.utc.with(NANO_OF_DAY.newValue(86400 * MRD).atUTC());
    }

    @Test
    public void containsHour0To24() {
        assertThat(this.utc.contains(HOUR_FROM_0_TO_24), is(false));
    }

    @Test
    public void getHour0To24() {
        assertThat(this.utc.get(HOUR_FROM_0_TO_24.atUTC()), is(23));
    }

    @Test
    public void withHour0To24() {
        assertThat(
            this.utc.with(HOUR_FROM_0_TO_24.newValue(23).atUTC()),
            is(this.utc));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withHour0To24Null() {
        this.utc.with(HOUR_FROM_0_TO_24.newValue(null).atUTC());
    }

    @Test(expected=IllegalArgumentException.class)
    public void withHour0To2424() {
        this.utc.with(HOUR_FROM_0_TO_24.newValue(24).atUTC());
    }

    @Test
    public void containsMinuteOfHour() {
        assertThat(this.utc.contains(MINUTE_OF_HOUR), is(false));
    }

    @Test
    public void getMinuteOfHour() {
        assertThat(
            this.utc.get(MINUTE_OF_HOUR.atUTC()),
            is(59));
    }

    @Test
    public void withMinuteOfHour() {
        assertThat(
            this.utc.with(MINUTE_OF_HOUR.newValue(59).atUTC()),
            is(this.utc));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMinuteOfHourNull() {
        this.utc.with(MINUTE_OF_HOUR.newValue(null).atUTC());
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMinuteOfHour60() {
        this.utc.with(MINUTE_OF_HOUR.newValue(60).atUTC());
    }

    @Test
    public void containsSecondOfMinute() {
        assertThat(this.utc.contains(SECOND_OF_MINUTE), is(false));
    }

    @Test
    public void getSecondOfMinute() {
        assertThat(
            this.utc.get(SECOND_OF_MINUTE.in(Timezone.of("Asia/Tokyo"))),
            is(60));
    }

    @Test
    public void withSecondOfMinute() {
        assertThat(
            this.utc.with(SECOND_OF_MINUTE.newValue(60).atUTC()),
            is(this.utc));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withSecondOfMinuteNull() {
        this.utc.with(SECOND_OF_MINUTE.newValue(null).atUTC());
    }

    @Test(expected=IllegalArgumentException.class)
    public void withSecondOfMinute60() {
        PlainTimestamp.of(2010, 4, 21, 9, 15).atUTC()
            .with(SECOND_OF_MINUTE.newValue(60).atUTC());
    }

    @Test
    public void containsMilliOfSecond() {
        assertThat(this.utc.contains(MILLI_OF_SECOND), is(false));
    }

    @Test
    public void getMilliOfSecond() {
        assertThat(
            this.utc.get(MILLI_OF_SECOND.atUTC()),
            is(123));
    }

    @Test
    public void withMilliOfSecond() {
        assertThat(
            this.utc.with(MILLI_OF_SECOND.newValue(999).atUTC()),
            is(this.utc.plus((999 - 123) * MIO, SI.NANOSECONDS)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMilliOfSecondNull() {
        this.utc.with(MILLI_OF_SECOND.newValue(null).atUTC());
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMilliOfSecond1000() {
        this.utc.with(MILLI_OF_SECOND.newValue(1000).atUTC());
    }

    @Test
    public void containsMicroOfSecond() {
        assertThat(this.utc.contains(MICRO_OF_SECOND), is(false));
    }

    @Test
    public void getMicroOfSecond() {
        assertThat(
            this.utc.get(MICRO_OF_SECOND.atUTC()),
            is(123456));
    }

    @Test
    public void withMicroOfSecond() {
        assertThat(
            this.utc.with(MICRO_OF_SECOND.newValue(999999).atUTC()),
            is(this.utc.plus((999999 - 123456) * 1000, SI.NANOSECONDS)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMicroOfSecondNull() {
        this.utc.with(MICRO_OF_SECOND.newValue(null).atUTC());
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMicroOfSecondMIO() {
        this.utc.with(MICRO_OF_SECOND.newValue(1000000).atUTC());
    }

    @Test
    public void containsNanoOfSecond() {
        assertThat(this.utc.contains(NANO_OF_SECOND), is(false));
    }

    @Test
    public void getNanoOfSecond() {
        assertThat(
            this.utc.get(NANO_OF_SECOND.in(Timezone.of("Asia/Baghdad"))),
            is(123456789));
    }

    @Test
    public void withNanoOfSecond() {
        assertThat(
            this.utc.with(NANO_OF_SECOND.newValue(123456789).atUTC()),
            is(this.utc));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withNanoOfSecondNull() {
        this.utc.with(NANO_OF_SECOND.newValue(null).atUTC());
    }

    @Test(expected=IllegalArgumentException.class)
    public void withNanoOfSecondMRD() {
        this.utc.with(NANO_OF_SECOND.newValue(1000000000).atUTC());
    }

    @Test
    public void containsClockHourOfAmPm() {
        assertThat(this.utc.contains(CLOCK_HOUR_OF_AMPM), is(false));
    }

    @Test
    public void getClockHourOfAmPm() {
        assertThat(
            this.utc.get(CLOCK_HOUR_OF_AMPM.atUTC()),
            is(11));
    }

    @Test
    public void withClockHourOfAmPm() {
        assertThat(
            this.utc.with(CLOCK_HOUR_OF_AMPM.newValue(12).atUTC()),
            is(
                PlainDate.of(2012, 6, 30)
                    .at(PlainTime.of(12, 59, 59, 123456789))
                    .inTimezone(ZonalOffset.UTC)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withClockHourOfAmPmNull() {
        this.utc.with(CLOCK_HOUR_OF_AMPM.newValue(null).atUTC());
    }

    @Test(expected=IllegalArgumentException.class)
    public void withClockHourOfAmPm0() {
        this.utc.with(CLOCK_HOUR_OF_AMPM.newValue(0).atUTC());
    }

    @Test
    public void containsClockHourOfDay() {
        assertThat(this.utc.contains(CLOCK_HOUR_OF_DAY), is(false));
    }

    @Test
    public void getClockHourOfDay() {
        assertThat(
            this.utc.get(CLOCK_HOUR_OF_DAY.atUTC()),
            is(23));
    }

    @Test
    public void withClockHourOfDay() {
        assertThat(
            this.utc.with(CLOCK_HOUR_OF_DAY.newValue(23).atUTC()),
            is(this.utc));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withClockHourOfDayNull() {
        this.utc.with(CLOCK_HOUR_OF_DAY.newValue(null).atUTC());
    }

    @Test(expected=IllegalArgumentException.class)
    public void withClockHourOfDay0() {
        this.utc.with(CLOCK_HOUR_OF_DAY.newValue(0).atUTC());
    }

    @Test
    public void containsDigitalHourOfAmPm() {
        assertThat(this.utc.contains(DIGITAL_HOUR_OF_AMPM), is(false));
    }

    @Test
    public void getDigitalHourOfAmPm() {
        assertThat(
            this.utc.get(DIGITAL_HOUR_OF_AMPM.atUTC()),
            is(11));
    }

    @Test
    public void withDigitalHourOfAmPm() {
        assertThat(
            this.utc.with(DIGITAL_HOUR_OF_AMPM.newValue(11).atUTC()),
            is(this.utc));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDigitalHourOfAmPmNull() {
        this.utc.with(DIGITAL_HOUR_OF_AMPM.newValue(null).atUTC());
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDigitalHourOfAmPm12() {
        this.utc.with(DIGITAL_HOUR_OF_AMPM.newValue(12).atUTC());
    }

    @Test
    public void containsDigitalHourOfDay() {
        assertThat(this.utc.contains(DIGITAL_HOUR_OF_DAY), is(false));
    }

    @Test
    public void getDigitalHourOfDay() {
        assertThat(
            this.utc.get(DIGITAL_HOUR_OF_DAY.atUTC()),
            is(23));
    }

    @Test
    public void withDigitalHourOfDay() {
        assertThat(
            this.utc.with(DIGITAL_HOUR_OF_DAY.newValue(23).atUTC()),
            is(this.utc));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDigitalHourOfDayNull() {
        this.utc.with(DIGITAL_HOUR_OF_DAY.newValue(null).atUTC());
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDigitalHourOfDay24() {
        this.utc.with(DIGITAL_HOUR_OF_DAY.newValue(24).atUTC());
    }

    @Test
    public void containsPrecision() {
        assertThat(this.utc.contains(PRECISION), is(false));
    }

    @Test(expected=RuleNotFoundException.class)
    public void getPrecision() {
        this.utc.get(PRECISION);
    }

    @Test
    public void isValidPrecision() {
        assertThat(
            this.utc.isValid(PRECISION, ClockUnit.HOURS),
            is(false));
    }

    @Test(expected=RuleNotFoundException.class)
    public void withPrecision() {
        this.utc.with(PRECISION, ClockUnit.HOURS);
    }

    @Test
    public void containsDecimalHour() {
        assertThat(this.utc.contains(DECIMAL_HOUR), is(false));
    }

    @Test(expected=RuleNotFoundException.class)
    public void getDecimalHour1() {
        this.utc.get(DECIMAL_HOUR);
    }

    @Test
    public void getDecimalHour2() {
        assertThat(
            this.utc.get(DECIMAL_HOUR.atUTC()),
            is(new BigDecimal("23.999756515774722")));
    }

    @Test
    public void isValidDecimalHour() {
        assertThat(
            this.utc.isValid(DECIMAL_HOUR, BigDecimal.ZERO),
            is(false));
    }

    @Test(expected=RuleNotFoundException.class)
    public void withDecimalHour() {
        this.utc.with(DECIMAL_HOUR, BigDecimal.ZERO);
    }

    @Test
    public void containsDecimalMinute() {
        assertThat(this.utc.contains(DECIMAL_MINUTE), is(false));
    }

    @Test(expected=RuleNotFoundException.class)
    public void getDecimalMinute1() {
        this.utc.get(DECIMAL_MINUTE);
    }

    @Test
    public void getDecimalMinute2() {
        assertThat(
            this.utc.get(DECIMAL_MINUTE.atUTC()),
            is(new BigDecimal("59.985390946483333")));
    }

    @Test
    public void isValidDecimalMinute() {
        assertThat(
            this.utc.isValid(DECIMAL_MINUTE, BigDecimal.ZERO),
            is(false));
    }

    @Test(expected=RuleNotFoundException.class)
    public void withDecimalMinute() {
        this.utc.with(DECIMAL_MINUTE, BigDecimal.ZERO);
    }

    @Test
    public void containsDecimalSecond() {
        assertThat(this.utc.contains(DECIMAL_SECOND), is(false));
    }

    @Test(expected=RuleNotFoundException.class)
    public void getDecimalSecond1() {
        this.utc.get(DECIMAL_SECOND);
    }

    @Test
    public void getDecimalSecond2() {
        assertThat(
            this.utc.get(DECIMAL_SECOND.atUTC()),
            is(new BigDecimal("59.123456789")));
    }

    @Test
    public void isValidDecimalSecond() {
        assertThat(
            this.utc.isValid(DECIMAL_SECOND, BigDecimal.ZERO),
            is(false));
    }

    @Test(expected=RuleNotFoundException.class)
    public void withDecimalSecond() {
        this.utc.with(DECIMAL_SECOND, BigDecimal.ZERO);
    }

    @Test
    public void containsDayOfMonth() {
        assertThat(
            this.utc.contains(DAY_OF_MONTH),
            is(false));
    }

    @Test
    public void getDayOfMonth() {
        assertThat(
            this.utc.get(DAY_OF_MONTH.in(Timezone.of("Asia/Tokyo"))),
            is(1));
    }

    @Test
    public void withDayOfMonth() {
        assertThat(
            this.utc.with(DAY_OF_MONTH.newValue(30).atUTC()),
            is(this.utc));
    }

    @Test
    public void containsDayOfWeek() {
        assertThat(
            this.utc.contains(DAY_OF_WEEK),
            is(false));
    }

    @Test
    public void getDayOfWeek() {
        assertThat(
            this.utc.get(DAY_OF_WEEK.atUTC()),
            is(Weekday.SATURDAY));
    }

    @Test
    public void withDayOfWeek() {
        assertThat(
            this.utc.with(DAY_OF_WEEK.newValue(Weekday.SATURDAY).atUTC()),
            is(this.utc));
    }

    @Test
    public void containsDayOfYear() {
        assertThat(
            this.utc.contains(DAY_OF_YEAR),
            is(false));
    }

    @Test
    public void getDayOfYear() {
        assertThat(
            this.utc.get(DAY_OF_YEAR.atUTC()),
            is(31 + 29 + 31 + 30 + 31 + 30));
    }

    @Test
    public void withDayOfYear() {
        assertThat(
            this.utc.with(DAY_OF_YEAR.newValue(1).atUTC()),
            is(
                PlainTimestamp.of(2012, 1, 1, 23, 59, 59)
                .plus(123456789, ClockUnit.NANOS)
                .atUTC()));
    }

    @Test
    public void containsDayOfQuarter() {
        assertThat(
            this.utc.contains(DAY_OF_QUARTER),
            is(false));
    }

    @Test
    public void getDayOfQuarter() {
        assertThat(
            this.utc.get(DAY_OF_QUARTER.atUTC()),
            is(91));
    }

    @Test
    public void withDayOfQuarter() {
        assertThat(
            this.utc.with(DAY_OF_QUARTER.newValue(91).atUTC()),
            is(this.utc));
    }

    @Test
    public void containsQuarterOfYear() {
        assertThat(
            this.utc.contains(QUARTER_OF_YEAR),
            is(false));
    }

    @Test
    public void getQuarterOfYear() {
        assertThat(
            this.utc.get(QUARTER_OF_YEAR.atUTC()),
            is(Quarter.Q2));
    }

    @Test
    public void withQuarterOfYear() {
        assertThat(
            this.utc.with(QUARTER_OF_YEAR.newValue(Quarter.Q2).atUTC()),
            is(this.utc));
    }

    @Test
    public void containsMonthAsNumber() {
        assertThat(
            this.utc.contains(MONTH_AS_NUMBER),
            is(false));
    }

    @Test
    public void getMonthAsNumber() {
        assertThat(
            this.utc.get(MONTH_AS_NUMBER.atUTC()),
            is(6));
    }

    @Test
    public void withMonthAsNumber() {
        assertThat(
            this.utc.with(MONTH_AS_NUMBER.newValue(6).atUTC()),
            is(this.utc));
    }

    @Test
    public void containsMonthOfYear() {
        assertThat(
            this.utc.contains(MONTH_OF_YEAR),
            is(false));
    }

    @Test
    public void getMonthOfYear() {
        assertThat(
            this.utc.get(MONTH_OF_YEAR.atUTC()),
            is(Month.JUNE));
    }

    @Test
    public void withMonthOfYear() {
        assertThat(
            this.utc.with(MONTH_OF_YEAR.newValue(Month.JUNE).atUTC()),
            is(this.utc));
    }

    @Test
    public void containsWeekdayInMonth() {
        assertThat(
            this.utc.contains(WEEKDAY_IN_MONTH),
            is(false));
    }

    @Test
    public void getWeekdayInMonth() {
        assertThat(
            this.utc.get(WEEKDAY_IN_MONTH.atUTC()),
            is(5));
    }

    @Test
    public void withWeekdayInMonth() {
        assertThat(
            this.utc.with(WEEKDAY_IN_MONTH.newValue(5).atUTC()),
            is(this.utc));
    }

    @Test
    public void containsYear() {
        assertThat(
            this.utc.contains(YEAR),
            is(false));
    }

    @Test
    public void getYear() {
        assertThat(
            this.utc.get(YEAR.atUTC()),
            is(2012));
    }

    @Test
    public void withYear() {
        assertThat(
            this.utc.with(YEAR.newValue(2013).atUTC()),
            is(
                PlainDate.of(2013, 6, 30)
                .at(PlainTime.of(23, 59, 59, 123456789))
                .inTimezone(ZonalOffset.UTC)));
    }

    @Test
    public void containsYearOfWeekdate() {
        assertThat(
            this.utc.contains(YEAR_OF_WEEKDATE),
            is(false));
    }

    @Test
    public void getYearOfWeekdate() {
        assertThat(
            this.utc.get(YEAR_OF_WEEKDATE.atUTC()),
            is(2012));
    }

    @Test
    public void withYearOfWeekdate() {
        assertThat(
            this.utc.with(YEAR_OF_WEEKDATE.newValue(2013).atUTC()),
            is(
                PlainDate.of(2013, 6, 29) // gleiche KW + gleicher Wochentag
                .at(PlainTime.of(23, 59, 59, 123456789))
                .inTimezone(ZonalOffset.UTC)));
    }

    @Test
    public void containsPosixTime() {
        assertThat(
            this.utc.contains(Moment.POSIX_TIME),
            is(true));
    }

    @Test
    public void getPosixTime() {
        assertThat(
            this.utc.get(Moment.POSIX_TIME),
            is(this.utc.getPosixTime()));
    }

    @Test
    public void withPosixTime() {
        assertThat(
            this.utc.with(Moment.POSIX_TIME, 0),
            is(
                PlainDate.of(1970, 1, 1)
                .at(PlainTime.of(0, 0, 0, 123456789))
                .atUTC()));
    }

    @Test
    public void containsFraction() {
        assertThat(
            this.utc.contains(Moment.FRACTION),
            is(true));
    }

    @Test
    public void getFraction() {
        assertThat(
            this.utc.get(Moment.FRACTION),
            is(this.utc.getNanosecond()));
    }

    @Test
    public void withFraction() {
        assertThat(
            this.utc.with(Moment.FRACTION, 0),
            is(this.utc.minus(123456789, SI.NANOSECONDS)));
    }

    @Test
    public void containsPrecisionInTimeUnits() {
        assertThat(
            this.utc.contains(Moment.PRECISION),
            is(true));
    }

    @Test
    public void getPrecisionInTimeUnits() {
        assertThat(
            this.utc.get(Moment.PRECISION),
            is(TimeUnit.NANOSECONDS));
    }

    @Test
    public void withPrecisionInTimeUnits() {
        assertThat(
            this.utc.with(Moment.PRECISION, TimeUnit.DAYS),
            is(PlainTimestamp.of(2012, 6, 30, 0, 0).atUTC()));
        assertThat(
            this.utc.with(Moment.PRECISION, TimeUnit.HOURS),
            is(PlainTimestamp.of(2012, 6, 30, 23, 0).atUTC()));
        assertThat(
            this.utc.with(Moment.PRECISION, TimeUnit.MINUTES),
            is(PlainTimestamp.of(2012, 6, 30, 23, 59).atUTC()));
        assertThat(
            this.utc.with(Moment.PRECISION, TimeUnit.SECONDS),
            is(this.utc.minus(123456789, SI.NANOSECONDS)));
        assertThat(
            this.utc.with(Moment.PRECISION, TimeUnit.MILLISECONDS),
            is(this.utc.minus(456789, SI.NANOSECONDS)));
        assertThat(
            this.utc.with(Moment.PRECISION, TimeUnit.MICROSECONDS),
            is(this.utc.minus(789, SI.NANOSECONDS)));
        assertThat(
            this.utc.with(Moment.PRECISION, TimeUnit.NANOSECONDS),
            is(this.utc));
    }

}