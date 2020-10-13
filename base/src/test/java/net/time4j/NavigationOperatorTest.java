package net.time4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.PlainDate.DAY_OF_WEEK;
import static net.time4j.PlainDate.MONTH_OF_YEAR;
import static net.time4j.PlainDate.QUARTER_OF_YEAR;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class NavigationOperatorTest {

    @Test
    public void nextDayOfWeek() {
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(DAY_OF_WEEK.setToNext(Weekday.FRIDAY)),
            is(PlainDate.of(2014, 4, 25)));
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(DAY_OF_WEEK.setToNext(Weekday.MONDAY)),
            is(PlainDate.of(2014, 4, 28)));
    }

    @Test
    public void previousDayOfWeek() {
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(DAY_OF_WEEK.setToPrevious(Weekday.FRIDAY)),
            is(PlainDate.of(2014, 4, 18)));
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(DAY_OF_WEEK.setToPrevious(Weekday.MONDAY)),
            is(PlainDate.of(2014, 4, 14)));
    }

    @Test
    public void nextOrSameDayOfWeek() {
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(DAY_OF_WEEK.setToNextOrSame(Weekday.FRIDAY)),
            is(PlainDate.of(2014, 4, 25)));
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(DAY_OF_WEEK.setToNextOrSame(Weekday.MONDAY)),
            is(PlainDate.of(2014, 4, 21)));
    }

    @Test
    public void previousOrSameDayOfWeek() {
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(DAY_OF_WEEK.setToPreviousOrSame(Weekday.FRIDAY)),
            is(PlainDate.of(2014, 4, 18)));
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(DAY_OF_WEEK.setToPreviousOrSame(Weekday.MONDAY)),
            is(PlainDate.of(2014, 4, 21)));
    }

    @Test
    public void nextMonth() {
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(MONTH_OF_YEAR.setToNext(Month.MARCH)),
            is(PlainDate.of(2015, 3, 21)));
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(MONTH_OF_YEAR.setToNext(Month.APRIL)),
            is(PlainDate.of(2015, 4, 21)));
    }

    @Test
    public void previousMonth() {
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(MONTH_OF_YEAR.setToPrevious(Month.MARCH)),
            is(PlainDate.of(2014, 3, 21)));
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(MONTH_OF_YEAR.setToPrevious(Month.APRIL)),
            is(PlainDate.of(2013, 4, 21)));
    }

    @Test
    public void nextOrSameMonth() {
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(MONTH_OF_YEAR.setToNextOrSame(Month.MARCH)),
            is(PlainDate.of(2015, 3, 21)));
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(MONTH_OF_YEAR.setToNextOrSame(Month.APRIL)),
            is(PlainDate.of(2014, 4, 21)));
    }

    @Test
    public void previousOrSameMonth() {
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(MONTH_OF_YEAR.setToPreviousOrSame(Month.MARCH)),
            is(PlainDate.of(2014, 3, 21)));
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(MONTH_OF_YEAR.setToPreviousOrSame(Month.APRIL)),
            is(PlainDate.of(2014, 4, 21)));
    }

    @Test
    public void nextQuarterOfYear() {
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(QUARTER_OF_YEAR.setToNext(Quarter.Q3)),
            is(PlainDate.of(2014, 7, 21)));
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(QUARTER_OF_YEAR.setToNext(Quarter.Q2)),
            is(PlainDate.of(2015, 4, 21)));
    }

    @Test
    public void previousQuarterOfYear() {
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(QUARTER_OF_YEAR.setToPrevious(Quarter.Q3)),
            is(PlainDate.of(2013, 7, 21)));
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(QUARTER_OF_YEAR.setToPrevious(Quarter.Q2)),
            is(PlainDate.of(2013, 4, 21)));
    }

    @Test
    public void nextOrSameQuarterOfYear() {
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(QUARTER_OF_YEAR.setToNextOrSame(Quarter.Q3)),
            is(PlainDate.of(2014, 7, 21)));
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(QUARTER_OF_YEAR.setToNextOrSame(Quarter.Q2)),
            is(PlainDate.of(2014, 4, 21)));
    }

    @Test
    public void previousOrSameQuarterOfYear() {
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(QUARTER_OF_YEAR.setToPreviousOrSame(Quarter.Q3)),
            is(PlainDate.of(2013, 7, 21)));
        assertThat(
            PlainDate.of(2014, 4, 21)
                .with(QUARTER_OF_YEAR.setToPreviousOrSame(Quarter.Q2)),
            is(PlainDate.of(2014, 4, 21)));
    }

    @Test
    public void nextDayOfWeekOnTimestamp() {
        assertThat(
            PlainDate.of(2014, 4, 21).atStartOfDay().with(
                DAY_OF_WEEK.setToNext(Weekday.FRIDAY)),
            is(PlainDate.of(2014, 4, 25).atStartOfDay()));
        assertThat(
            PlainDate.of(2014, 4, 21).atStartOfDay().with(
                DAY_OF_WEEK.setToNext(Weekday.MONDAY)),
            is(PlainDate.of(2014, 4, 28).atStartOfDay()));
    }

    @Test
    public void previousDayOfWeekOnTimestamp() {
        assertThat(
            PlainDate.of(2014, 4, 21).atStartOfDay().with(
                DAY_OF_WEEK.setToPrevious(Weekday.FRIDAY)),
            is(PlainDate.of(2014, 4, 18).atStartOfDay()));
        assertThat(
            PlainDate.of(2014, 4, 21).atStartOfDay().with(
                DAY_OF_WEEK.setToPrevious(Weekday.MONDAY)),
            is(PlainDate.of(2014, 4, 14).atStartOfDay()));
    }

    @Test
    public void nextOrSameDayOfWeekOnTimestamp() {
        assertThat(
            PlainDate.of(2014, 4, 21).atStartOfDay().with(
                DAY_OF_WEEK.setToNextOrSame(Weekday.FRIDAY)),
            is(PlainDate.of(2014, 4, 25).atStartOfDay()));
        assertThat(
            PlainDate.of(2014, 4, 21).atStartOfDay().with(
                DAY_OF_WEEK.setToNextOrSame(Weekday.MONDAY)),
            is(PlainDate.of(2014, 4, 21).atStartOfDay()));
    }

    @Test
    public void previousOrSameDayOfWeekOnTimestamp() {
        assertThat(
            PlainDate.of(2014, 4, 21).atStartOfDay().with(
                DAY_OF_WEEK.setToPreviousOrSame(Weekday.FRIDAY)),
            is(PlainDate.of(2014, 4, 18).atStartOfDay()));
        assertThat(
            PlainDate.of(2014, 4, 21).atStartOfDay().with(
                DAY_OF_WEEK.setToPreviousOrSame(Weekday.MONDAY)),
            is(PlainDate.of(2014, 4, 21).atStartOfDay()));
    }

    @Test
    public void nextMonthOnTimestamp() {
        assertThat(
            PlainDate.of(2014, 4, 21).atStartOfDay().with(
                MONTH_OF_YEAR.setToNext(Month.MARCH)),
            is(PlainDate.of(2015, 3, 21).atStartOfDay()));
        assertThat(
            PlainDate.of(2014, 4, 21).atStartOfDay().with(
                MONTH_OF_YEAR.setToNext(Month.APRIL)),
            is(PlainDate.of(2015, 4, 21).atStartOfDay()));
    }

    @Test
    public void previousMonthOnTimestamp() {
        assertThat(
            PlainDate.of(2014, 4, 21).atStartOfDay().with(
                MONTH_OF_YEAR.setToPrevious(Month.MARCH)),
            is(PlainDate.of(2014, 3, 21).atStartOfDay()));
        assertThat(
            PlainDate.of(2014, 4, 21).atStartOfDay().with(
                MONTH_OF_YEAR.setToPrevious(Month.APRIL)),
            is(PlainDate.of(2013, 4, 21).atStartOfDay()));
    }

    @Test
    public void nextOrSameMonthOnTimestamp() {
        assertThat(
            PlainDate.of(2014, 4, 21).atStartOfDay().with(
                MONTH_OF_YEAR.setToNextOrSame(Month.MARCH)),
            is(PlainDate.of(2015, 3, 21).atStartOfDay()));
        assertThat(
            PlainDate.of(2014, 4, 21).atStartOfDay().with(
                MONTH_OF_YEAR.setToNextOrSame(Month.APRIL)),
            is(PlainDate.of(2014, 4, 21).atStartOfDay()));
    }

    @Test
    public void previousOrSameMonthOnTimestamp() {
        assertThat(
            PlainDate.of(2014, 4, 21).atStartOfDay().with(
                MONTH_OF_YEAR.setToPreviousOrSame(Month.MARCH)),
            is(PlainDate.of(2014, 3, 21).atStartOfDay()));
        assertThat(
            PlainDate.of(2014, 4, 21).atStartOfDay().with(
                MONTH_OF_YEAR.setToPreviousOrSame(Month.APRIL)),
            is(PlainDate.of(2014, 4, 21).atStartOfDay()));
    }

    @Test
    public void nextQuarterOfYearOnTimestamp() {
        assertThat(
            PlainDate.of(2014, 4, 21).atStartOfDay().with(
                QUARTER_OF_YEAR.setToNext(Quarter.Q3)),
            is(PlainDate.of(2014, 7, 21).atStartOfDay()));
        assertThat(
            PlainDate.of(2014, 4, 21).atStartOfDay().with(
                QUARTER_OF_YEAR.setToNext(Quarter.Q2)),
            is(PlainDate.of(2015, 4, 21).atStartOfDay()));
    }

    @Test
    public void previousQuarterOfYearOnTimestamp() {
        assertThat(
            PlainDate.of(2014, 4, 21).atStartOfDay().with(
                QUARTER_OF_YEAR.setToPrevious(Quarter.Q3)),
            is(PlainDate.of(2013, 7, 21).atStartOfDay()));
        assertThat(
            PlainDate.of(2014, 4, 21).atStartOfDay().with(
                QUARTER_OF_YEAR.setToPrevious(Quarter.Q2)),
            is(PlainDate.of(2013, 4, 21).atStartOfDay()));
    }

    @Test
    public void nextOrSameQuarterOfYearOnTimestamp() {
        assertThat(
            PlainDate.of(2014, 4, 21).atStartOfDay().with(
                QUARTER_OF_YEAR.setToNextOrSame(Quarter.Q3)),
            is(PlainDate.of(2014, 7, 21).atStartOfDay()));
        assertThat(
            PlainDate.of(2014, 4, 21).atStartOfDay().with(
                QUARTER_OF_YEAR.setToNextOrSame(Quarter.Q2)),
            is(PlainDate.of(2014, 4, 21).atStartOfDay()));
    }

    @Test
    public void previousOrSameQuarterOfYearOnTimestamp() {
        assertThat(
            PlainDate.of(2014, 4, 21).atStartOfDay().with(
                QUARTER_OF_YEAR.setToPreviousOrSame(Quarter.Q3)),
            is(PlainDate.of(2013, 7, 21).atStartOfDay()));
        assertThat(
            PlainDate.of(2014, 4, 21).atStartOfDay().with(
                QUARTER_OF_YEAR.setToPreviousOrSame(Quarter.Q2)),
            is(PlainDate.of(2014, 4, 21).atStartOfDay()));
    }

}