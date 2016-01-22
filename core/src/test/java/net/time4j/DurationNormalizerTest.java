package net.time4j;

import net.time4j.engine.Normalizer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.ClockUnit.HOURS;
import static net.time4j.ClockUnit.NANOS;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class DurationNormalizerTest {

    @Test
    public void withNormalizer2010_1_1() {
        Duration<CalendarUnit> datePeriod =
            Duration.ofCalendarUnits(2, 14, 33);
        assertThat(
            datePeriod.with(PlainDate.of(2010, 1, 1)),
            is(Duration.ofCalendarUnits(3, 3, 2)));
    }

    @Test
    public void withNormalizer2010_2_1() {
        Duration<CalendarUnit> datePeriod =
            Duration.ofCalendarUnits(2, 14, 33);
        assertThat(
            datePeriod.with(PlainDate.of(2010, 2, 1)),
            is(Duration.ofCalendarUnits(3, 3, 3)));
    }

    @Test
    public void withNormalizer2003_12_27() {
        Duration<CalendarUnit> datePeriod =
            Duration.ofCalendarUnits(2, 14, 33);
        assertThat(
            datePeriod.with(PlainDate.of(2003, 12, 27)),
            is(Duration.ofCalendarUnits(3, 3, 5)));
    }

    @Test
    public void withSTD_PERIOD() {
        Duration<IsoUnit> test =
        	Duration.ofZero()
        		.plus(Duration.ofCalendarUnits(2, 14, 30))
        		.plus(Duration.ofClockUnits(47, 59, 60))
        		.plus(1075800000, NANOS)
                .inverse();
        assertThat(
            test.with(Duration.STD_PERIOD),
            is(
                Duration.ofNegative().years(3).months(2).days(32)
                    .seconds(1).millis(75).micros(800).build()));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withSTD_PERIOD_unitsOfSameLength() {
		Duration.ofZero()
			.plus(1, CalendarUnit.weekBasedYears())
			.plus(5, CalendarUnit.CENTURIES)
			.with(Duration.STD_PERIOD);
    }

    @Test
    public void withSTD_CALENDAR_PERIOD1() {
        Duration<CalendarUnit> datePeriod =
            Duration.ofCalendarUnits(2, 15, 3);
        assertThat(
            datePeriod.with(Duration.STD_CALENDAR_PERIOD),
            is(Duration.ofCalendarUnits(3, 3, 3)));
    }

    @Test
    public void withSTD_CALENDAR_PERIOD2() {
        Duration<CalendarUnit> datePeriod =
            Duration.ofCalendarUnits(2, 15, 3).plus(3, CalendarUnit.WEEKS);
        assertThat(
            datePeriod.with(Duration.STD_CALENDAR_PERIOD),
            is(Duration.ofCalendarUnits(3, 3, 24)));
    }

    @Test
    public void withSTD_CLOCK_PERIOD() {
        Duration<ClockUnit> timePeriod =
            Duration.ofClockUnits(2, 61, 120);
        assertThat(
            timePeriod.with(Duration.STD_CLOCK_PERIOD),
            is(Duration.ofClockUnits(3, 3, 0)));
    }

    @Test
    public void withMinutesOnly() {
        Duration<ClockUnit> timePeriod =
            Duration.ofClockUnits(2, 61, 122);
        assertThat(
            timePeriod.with(ClockUnit.MINUTES.only()),
            is(Duration.of(183, ClockUnit.MINUTES)));
    }

    @Test
    public void withNegativeMinutesOnly() {
        Duration<ClockUnit> timePeriod =
            Duration.ofClockUnits(4, 55, 0).inverse();
        assertThat(
            timePeriod.with(ClockUnit.MINUTES.only()),
            is(Duration.of(-295, ClockUnit.MINUTES)));
    }

    @Test
    public void withMinutesOnlyIfEmpty() {
        Duration<ClockUnit> timePeriod = Duration.ofZero();
        assertThat(
            timePeriod.with(ClockUnit.MINUTES.only()).isEmpty(),
            is(true));
    }

    @Test
    public void withApproximatePeriodInRoundedStepsOf24Hours() {
		Duration<IsoUnit> dur =
			Duration.ofPositive().years(2).months(13).days(35).minutes(132).build();
        assertThat(
            dur.with(Duration.approximateHours(24)),
            is(Duration.ofPositive().years(3).months(2).days(4).build()));
    }

    @Test
    public void withApproximatePeriodAppliedOnGeneralDuration() {
        Duration<IsoUnit> dur =
            Duration.ofPositive().years(2).months(13).days(35).minutes(132).build();
        assertThat(
            dur.with(Duration.approximateHours(3)),
            is(Duration.ofPositive().years(3).months(2).days(4).hours(15).build()));
        assertThat(
            dur.with(Duration.approximateSeconds(10)),
            is(Duration.ofPositive().years(3).months(2).days(4).hours(15).minutes(42).seconds(50).build()));
    }

    @Test
    public void withApproximatePeriodInRoundedStepsOf3Hours() {
        assertThat(
            Duration.ofPositive().days(3).minutes(270).build().with(Duration.approximateHours(3)),
            is(Duration.ofPositive().days(3).hours(6).build()));
        assertThat(
            Duration.ofPositive().days(3).minutes(269).build().with(Duration.approximateHours(3)),
            is(Duration.ofPositive().days(3).hours(3).build()));
        assertThat(
            Duration.ofPositive().days(3).minutes(90).build().with(Duration.approximateHours(3)),
            is(Duration.ofPositive().days(3).hours(3).build()));
        assertThat(
            Duration.ofPositive().days(3).minutes(89).build().with(Duration.approximateHours(3)),
            is(Duration.ofPositive().days(3).hours(0).build()));
        assertThat(
            Duration.ofPositive().days(3).minutes(0).build().with(Duration.approximateHours(3)),
            is(Duration.ofPositive().days(3).hours(0).build()));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withApproximatePeriodInNonPositiveRoundedSteps() {
		Duration<IsoUnit> dur =
			Duration.ofPositive().years(2).months(13).days(35).minutes(132).build();
        dur.with(Duration.approximateMinutes(0));
    }

    @Test
    public void withApproximateHours() {
        assertThat(
            Duration.<IsoUnit>of(7, HOURS).with(Duration.approximateHours(3)),
            is(Duration.<IsoUnit>of(6, HOURS)));
    }

    @Test
    public void withApproximateMaxUnit() {
        assertThat(
            Duration.ofPositive().days(7).hours(4).minutes(1).build().with(Duration.approximateMaxUnit(true)),
            is(Duration.<IsoUnit>of(1, CalendarUnit.WEEKS)));

        Normalizer<IsoUnit> n = Duration.approximateMaxUnit(false);
        assertThat(
            Duration.ofPositive().years(2).months(13).days(35).minutes(132).build().with(n),
            is(Duration.ofPositive().years(3).build()));
        assertThat(
            Duration.ofPositive().years(2).months(16).days(35).minutes(132).build().with(n),
            is(Duration.ofPositive().years(3).build()));
        assertThat(
            Duration.ofPositive().years(2).months(17).days(35).minutes(132).build().with(n),
            is(Duration.ofPositive().years(4).build()));
        assertThat(
            Duration.ofPositive().months(13).days(35).minutes(132).build().with(n),
            is(Duration.ofPositive().years(1).build()));
        assertThat(
            Duration.ofPositive().days(35).minutes(132).build().with(n),
            is(Duration.ofPositive().months(1).build()));
        assertThat(
            Duration.ofPositive().days(6).hours(4).minutes(1).build().with(n),
            is(Duration.ofPositive().days(6).build()));
    }

    @Test
    public void withTimestampNormalizer1() {
		Duration<IsoUnit> dur =
			Duration.ofPositive().years(2).months(13).days(35).minutes(132).build();
        assertThat(
            dur.with(PlainTimestamp.of(2012, 2, 29, 14, 25)),
            is(
                Duration.ofPositive().years(3).months(2).days(4)
                    .hours(2).minutes(12).build()));
    }

    @Test
    public void withTimestampNormalizer2() {
        Duration<CalendarUnit> dur = Duration.of(30, CalendarUnit.DAYS);
        assertThat(
            PlainTimestamp.of(2012, 2, 28, 0, 0).normalize(dur),
            is(Duration.ofPositive().months(1).days(1).build()));
    }

    @Test
    public void withMinutesTruncated() {
        Duration<ClockUnit> timePeriod =
            Duration.ofClockUnits(4, 55, 700);
        assertThat(
            timePeriod.with(ClockUnit.MINUTES.truncated()),
            is(Duration.ofClockUnits(4, 55, 0)));
    }

    @Test
    public void withNegativeMinutesTruncated() {
        Duration<ClockUnit> timePeriod =
            Duration.ofClockUnits(4, 55, 700).inverse();
        assertThat(
            timePeriod.with(ClockUnit.MINUTES.truncated()),
            is(Duration.ofClockUnits(4, 55, 0).inverse()));
    }

    @Test
    public void withMinutesTruncatedIfEmpty() {
        Duration<ClockUnit> timePeriod = Duration.ofZero();
        assertThat(
            timePeriod.with(ClockUnit.MINUTES.truncated()).isEmpty(),
            is(true));
    }

    @Test
    public void withMinutesRounded1() {
        Duration<ClockUnit> timePeriod =
            Duration.ofClockUnits(4, 55, 89);
        assertThat(
            timePeriod.with(ClockUnit.MINUTES.rounded()),
            is(Duration.ofClockUnits(4, 56, 0)));
    }

    @Test
    public void withMinutesRounded2() {
        Duration<ClockUnit> timePeriod =
            Duration.ofClockUnits(4, 55, 90);
        assertThat(
            timePeriod.with(ClockUnit.MINUTES.rounded()),
            is(Duration.ofClockUnits(4, 57, 0)));
    }

    @Test
    public void withNegativeMinutesRounded() {
        Duration<ClockUnit> timePeriod =
            Duration.ofClockUnits(4, 55, 90).inverse();
        assertThat(
            timePeriod.with(ClockUnit.MINUTES.rounded()),
            is(Duration.ofClockUnits(4, 57, 0).inverse()));
    }

    @Test
    public void withMinutesRoundedIfEmpty() {
        Duration<ClockUnit> timePeriod = Duration.ofZero();
        assertThat(
            timePeriod.with(ClockUnit.MINUTES.rounded()).isEmpty(),
            is(true));
    }

    @Test
    public void withNanosRounded() {
        Duration<ClockUnit> timePeriod =
            Duration.ofClockUnits(4, 65, 1).plus(500000000, ClockUnit.NANOS);
        assertThat(
            timePeriod.with(ClockUnit.NANOS.rounded()),
            is(Duration.ofClockUnits(5, 5, 1).plus(500000000, ClockUnit.NANOS)));
    }

}
