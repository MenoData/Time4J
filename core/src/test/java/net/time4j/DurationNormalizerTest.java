package net.time4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

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
    public void withMinutesOnlyIfEmpty() {
        Duration<ClockUnit> timePeriod = Duration.ofZero();
        assertThat(
            timePeriod.with(ClockUnit.MINUTES.only()).isEmpty(),
            is(true));
    }

    @Test
    public void withApproximatePeriod24Hours() {
		Duration<IsoUnit> dur =
			Duration.ofPositive().years(2).months(13).days(35).minutes(132).build();
        assertThat(
            dur.with(Duration.approximateHours(24)),
            is(Duration.ofPositive().years(3).months(2).days(4).build()));
    }

    @Test
    public void withApproximatePeriod10Seconds() {
		Duration<IsoUnit> dur =
			Duration.ofPositive().years(2).months(13).days(35).minutes(132).build();
        assertThat(
            dur.with(Duration.approximateSeconds(10)),
            is(
            	Duration.ofPositive().years(3).months(2).days(4)
            	.hours(15).minutes(42).seconds(50).build()));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withApproximatePeriod0Minutes() {
		Duration<IsoUnit> dur =
			Duration.ofPositive().years(2).months(13).days(35).minutes(132).build();
        dur.with(Duration.approximateMinutes(0));
    }

    @Test
    public void withTimestampNormalizer() {
		Duration<IsoUnit> dur =
			Duration.ofPositive().years(2).months(13).days(35).minutes(132).build();
        assertThat(
            dur.with(PlainTimestamp.of(2012, 2, 29, 14, 25)),
            is(
            	Duration.ofPositive().years(3).months(2).days(4)
            	.hours(2).minutes(12).build()));
    }

}
