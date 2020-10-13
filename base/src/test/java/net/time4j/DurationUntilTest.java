package net.time4j;

import net.time4j.engine.TimeMetric;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collection;
import java.util.HashSet;

import static net.time4j.CalendarUnit.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;


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
        assertThat(
            start.until(end, Duration.in(MONTHS, DAYS).reversible()),
            is(Duration.ofCalendarUnits(0, 0, 58)));

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
    public void untilInDuplicateUnits() {
        PlainDate start = PlainDate.of(2003, 2, 27);
        PlainDate end = PlainDate.of(2008, 2, 26);
        start.until(end, Duration.in(MONTHS, MONTHS));
    }

    @Test
    public void betweenDates() {
        PlainDate d1 = PlainDate.of(2014, 1, 31);
        PlainDate d2 = PlainDate.of(2014, 3, 4);
        Collection<CalendarUnit> units = new HashSet<>();
        units.add(CalendarUnit.DAYS);
        units.add(CalendarUnit.MONTHS);
        Duration<?> p = Duration.ofPositive().months(1).days(4).build();
        assertThat(
            Duration.in(units).between(d1, d2),
            is(p));
    }

    @Test
    public void betweenTimestamps1() {
        PlainTimestamp t1 = PlainTimestamp.of(2014, 1, 31, 21, 45);
        PlainTimestamp t2 = PlainTimestamp.of(2014, 3, 4, 7, 0);
        IsoUnit[] units = {
            CalendarUnit.MONTHS, CalendarUnit.DAYS,
            ClockUnit.HOURS, ClockUnit.MINUTES};
        Duration<?> p = Duration.ofPositive().months(1).days(3).hours(9).minutes(15).build();
        assertThat(
            Duration.in(units).between(t1, t2),
            is(p));
        assertThat(t1.plus(p), is(t2));
        assertThat(t2.minus(p), not(t1));
    }

    @Test
    public void betweenTimestamps2() {
        PlainTimestamp t1 = PlainTimestamp.of(2014, 1, 1, 21, 45);
        PlainTimestamp t2 = PlainTimestamp.of(2014, 3, 31, 7, 0);
        IsoUnit[] units = {
            CalendarUnit.MONTHS, CalendarUnit.DAYS,
            ClockUnit.HOURS, ClockUnit.MINUTES};
        assertThat(
            Duration.in(units).between(t1, t2),
            is(
                Duration.ofPositive().months(2).days(29)
                .hours(9).minutes(15).build()));
    }

    @Test
    public void betweenTimestamps3() {
        PlainTimestamp t1 = PlainTimestamp.of(2014, 1, 31, 21, 45);
        PlainTimestamp t2 = PlainTimestamp.of(2014, 3, 1, 7, 0);
        IsoUnit[] units = {
            CalendarUnit.MONTHS, CalendarUnit.DAYS,
            ClockUnit.HOURS, ClockUnit.MINUTES};
        Duration<?> p = Duration.ofPositive().months(1).hours(9).minutes(15).build();
        assertThat(
            Duration.in(units).between(t1, t2),
            is(p));
        assertThat(t1.plus(p), is(t2));
        assertThat(t2.minus(p), not(t1));
    }

    @Test
    public void betweenTimestamps4() {
        PlainTimestamp t1 = PlainTimestamp.of(2014, 1, 31, 21, 45);
        PlainTimestamp t2 = PlainTimestamp.of(2014, 2, 28, 7, 0);
        IsoUnit[] units = {
            CalendarUnit.MONTHS, CalendarUnit.DAYS,
            ClockUnit.HOURS, ClockUnit.MINUTES};
        assertThat(
            Duration.in(units).between(t1, t2),
            is(
                Duration.ofPositive().months(0).days(27)
                .hours(9).minutes(15).build()));
    }

    @Test
    public void betweenTimestamps5() {
		PlainTimestamp t1 = PlainTimestamp.of(2014, 1, 31, 21, 45);
		PlainTimestamp t2 = PlainTimestamp.of(2014, 3, 1, 7, 0);
		IsoUnit[] units = {
		    CalendarUnit.MONTHS, ClockUnit.HOURS, ClockUnit.MINUTES};
        Duration<?> p = Duration.ofPositive().months(1).hours(9).minutes(15).build();
        assertThat(
            Duration.in(units).between(t1, t2),
            is(p));
        assertThat(t1.plus(p), is(t2));
        assertThat(t2.minus(p), not(t1));
    }

    @Test
    public void betweenTimestamps6() {
		PlainTimestamp t1 = PlainTimestamp.of(2014, 2, 1, 21, 45);
		PlainTimestamp t2 = PlainTimestamp.of(2014, 3, 2, 7, 0);
		IsoUnit[] units = {
		    CalendarUnit.MONTHS, ClockUnit.HOURS, ClockUnit.MINUTES};
        assertThat(
            Duration.in(units).between(t1, t2),
            is(Duration.ofPositive().months(1).hours(9).minutes(15).build()));
    }

    @Test
    public void betweenTimestamps7() {
		PlainTimestamp t1 = PlainTimestamp.of(2014, 2, 1, 21, 45);
		PlainTimestamp t2 = PlainTimestamp.of(2014, 3, 1, 7, 0);
		IsoUnit[] units = {
		    CalendarUnit.MONTHS, ClockUnit.HOURS, ClockUnit.MINUTES};
        assertThat(
            Duration.in(units).between(t1, t2),
            is(Duration.ofPositive().hours(27 * 24 + 9).minutes(15).build()));
    }

    @Test
    public void betweenTimestamps8() {
        PlainTimestamp t1 = PlainTimestamp.of(2014, 1, 31, 21, 45);
        PlainTimestamp t2 = PlainTimestamp.of(2014, 3, 1, 7, 0);
        IsoUnit[] units = {
            CalendarUnit.MONTHS, ClockUnit.HOURS, ClockUnit.MINUTES};
        Duration<?> p = Duration.ofPositive().hours(681).minutes(15).build();
        assertThat(
            Duration.in(units).reversible().between(t1, t2),
            is(p));
        assertThat(t1.plus(p), is(t2));
        assertThat(t2.minus(p), is(t1));
    }

    @Test
    public void betweenWeekBased() {
        PlainDate d1 = PlainDate.of(2012, 1, 1); // Sunday
        PlainDate d2 = PlainDate.of(2016, 3, 1); // Tuesday
        Duration<IsoDateUnit> expected = Duration.of(4, CalendarUnit.weekBasedYears()).plus(9, WEEKS).plus(2, DAYS);
        assertThat(
            Duration.inWeekBasedUnits().between(d1, d2),
            is(expected));
        assertThat(
            d1.plus(expected),
            is(d2));
    }

    @Test
    public void reversible() {
        PlainDate d1 = PlainDate.of(2011, 3, 31);
        PlainDate d2 = PlainDate.of(2011, 7, 1);

        TimeMetric<CalendarUnit, Duration<CalendarUnit>> metric =
            Duration.inYearsMonthsDays().reversible();
        Duration<CalendarUnit> duration =
            metric.between(d1, d2); // P2M31D
        Duration<CalendarUnit> invDur =
            metric.between(d2, d1); // -P2M31D

        assertThat(d1.plus(duration), is(d2)); // first invariance
        assertThat(invDur, is(duration.inverse())); // second invariance
        assertThat(d2.minus(duration), is(d1)); // third invariance
    }

}
