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
        Duration<CalendarUnit> datePeriod =
            Duration.ofCalendarUnits(2, 14, 30);
        Duration<ClockUnit> timePeriod =
            Duration.ofClockUnits(47, 59, 60).plus(1075800000, NANOS);
        Duration<IsoUnit> test =
            datePeriod.union(timePeriod).negate();
        assertThat(
            test.with(Duration.STD_PERIOD),
            is(
                Duration.ofNegative().years(3).months(2).days(32)
                .seconds(1).millis(75).micros(800).build()));
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

}