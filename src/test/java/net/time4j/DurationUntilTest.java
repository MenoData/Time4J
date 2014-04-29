package net.time4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.CalendarUnit.DAYS;
import static net.time4j.CalendarUnit.MONTHS;
import static net.time4j.CalendarUnit.WEEKS;
import static net.time4j.CalendarUnit.YEARS;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class DurationUntilTest {

    @Test
    public void untilInYearsMonthsDays() {
        PlainDate start = PlainDate.of(2003, 2, 27);
        PlainDate end = PlainDate.of(2005, 2, 26);
        assertThat(
            start.until(end, Duration.in(YEARS, MONTHS, DAYS)),
            is(Duration.ofCalendarUnits(1, 11, 30)));
    }

    @Test
    public void untilInYearsMonths() {
        PlainDate start = PlainDate.of(2003, 2, 27);
        PlainDate end = PlainDate.of(2008, 2, 26);
        assertThat(
            start.until(end, Duration.in(YEARS, MONTHS)),
            is(Duration.ofCalendarUnits(4, 11, 0)));
    }

    @Test
    public void untilInMonthsDays() {
        PlainDate start = PlainDate.of(2003, 2, 27);
        PlainDate end = PlainDate.of(2005, 2, 26);
        assertThat(
            start.until(end, Duration.in(MONTHS, DAYS)),
            is(Duration.ofCalendarUnits(0, 23, 30)));

        start = PlainDate.of(2013, 1, 31);
        end = PlainDate.of(2013, 2, 28);
        assertThat(
            start.until(end, Duration.in(MONTHS, DAYS)),
            is(Duration.ofCalendarUnits(0, 0, 28)));

        start = PlainDate.of(2013, 1, 31);
        end = PlainDate.of(2013, 3, 30);
        assertThat(
            start.until(end, Duration.in(MONTHS, DAYS)),
            is(Duration.ofCalendarUnits(0, 1, 30)));

        start = PlainDate.of(2013, 5, 29);
        end = PlainDate.of(2014, 2, 28);
        assertThat(
            start.until(end, Duration.in(MONTHS, DAYS)),
            is(Duration.ofCalendarUnits(0, 8, 30)));

        start = PlainDate.of(2003, 2, 27);
        end = PlainDate.of(2008, 2, 26);
        assertThat(
            start.until(end, Duration.in(MONTHS, DAYS)),
            is(Duration.ofCalendarUnits(0, 59, 30)));
    }

    @Test
    public void untilInMonthsWeeksDays() {
        PlainDate start = PlainDate.of(2003, 2, 27);
        PlainDate end = PlainDate.of(2008, 2, 26);
        assertThat(
            start.until(end, Duration.in(MONTHS, WEEKS, DAYS)),
            is(Duration.ofCalendarUnits(0, 59, 2).plus(4, WEEKS)));
    }

    @Test
    public void untilInWeeksDays() {
        PlainDate start = PlainDate.of(2003, 2, 27);
        PlainDate end = PlainDate.of(2008, 2, 26);
        long days = end.getDaysSinceUTC() - start.getDaysSinceUTC();
        long weeks = days / 7;
        days = days % 7;
        assertThat(
            start.until(end, Duration.in(WEEKS, DAYS)),
            is(Duration.of(days, DAYS).plus(weeks, WEEKS)));
    }

    @Test
    public void untilInWeeks() {
        PlainDate start = PlainDate.of(2003, 2, 27);
        PlainDate end = PlainDate.of(2008, 2, 26);
        long days = end.getDaysSinceUTC() - start.getDaysSinceUTC();
        long weeks = days / 7;
        assertThat(
            start.until(end, Duration.in(WEEKS)),
            is(Duration.of(weeks, WEEKS)));
    }

    @Test
    public void untilInMonths() {
        PlainDate start = PlainDate.of(2003, 2, 27);
        PlainDate end = PlainDate.of(2008, 2, 26);
        assertThat(
            start.until(end, Duration.in(MONTHS)),
            is(Duration.of(59, MONTHS)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void untilInDoubleUnits() {
        PlainDate start = PlainDate.of(2003, 2, 27);
        PlainDate end = PlainDate.of(2008, 2, 26);
        start.until(end, Duration.in(MONTHS, MONTHS));
    }

}