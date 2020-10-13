package net.time4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.PlainDate.DAY_OF_MONTH;
import static net.time4j.PlainDate.DAY_OF_QUARTER;
import static net.time4j.PlainDate.DAY_OF_WEEK;
import static net.time4j.PlainDate.DAY_OF_YEAR;
import static net.time4j.PlainDate.MONTH_AS_NUMBER;
import static net.time4j.PlainDate.MONTH_OF_YEAR;
import static net.time4j.PlainDate.QUARTER_OF_YEAR;
import static net.time4j.PlainDate.YEAR;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class DateOperatorTest {

    @Test
    public void yearMinimized() {
        assertThat(
            PlainDate.of(2012, 2, 29).with(YEAR.minimized()),
            is(PlainDate.of(-999999999, 2, 28)));
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 0, 0).with(
                YEAR.minimized()),
            is(PlainTimestamp.of(-999999999, 2, 28, 0, 0)));
    }

    @Test
    public void yearMaximized() {
        assertThat(
            PlainDate.of(2012, 2, 29).with(YEAR.maximized()),
            is(PlainDate.of(999999999, 2, 28)));
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 10, 45).with(
                YEAR.maximized()),
            is(PlainTimestamp.of(999999999, 2, 28, 10, 45)));
    }

    @Test
    public void yearIncremented() {
        assertThat(
            PlainDate.of(2012, 2, 1).with(YEAR.incremented()),
            is(PlainDate.of(2013, 2, 1)));
        assertThat(
            PlainDate.of(2012, 2, 29).with(YEAR.incremented()),
            is(PlainDate.of(2013, 2, 28)));
        assertThat(
            PlainTimestamp.of(2012, 2, 1, 12, 45).with(
                YEAR.incremented()),
            is(PlainTimestamp.of(2013, 2, 1, 12, 45)));
    }

    @Test
    public void yearDecremented() {
        assertThat(
            PlainDate.of(2012, 3, 1).with(YEAR.decremented()),
            is(PlainDate.of(2011, 3, 1)));
        assertThat(
            PlainDate.of(2012, 2, 29).with(YEAR.decremented()),
            is(PlainDate.of(2011, 2, 28)));
        assertThat(
            PlainTimestamp.of(2012, 3, 1, 12, 45).with(
                YEAR.decremented()),
            is(PlainTimestamp.of(2011, 3, 1, 12, 45)));
    }

    @Test
    public void yearAtFloor() {
        assertThat(
            PlainDate.of(2012, 3, 4).with(YEAR.atFloor()),
            is(PlainDate.of(2012, 1, 1)));
        assertThat(
            PlainTimestamp.of(2012, 3, 4, 12, 45)
                .with(YEAR.atFloor()),
            is(PlainTimestamp.of(2012, 1, 1, 0, 0)));
    }

    @Test
    public void yearAtCeiling() {
        assertThat(
            PlainDate.of(2012, 3, 4).with(YEAR.atCeiling()),
            is(PlainDate.of(2012, 12, 31)));
        assertThat(
            PlainTimestamp.of(2012, 3, 4, 12, 45)
                .with(YEAR.atCeiling()),
            is(
                PlainTimestamp.of(2012, 12, 31, 23, 59, 59)
                .with(PlainTime.NANO_OF_SECOND, 999999999)));
    }

    @Test
    public void monthOfYearMinimized() {
        assertThat(
            PlainDate.of(2012, 2, 29).with(MONTH_OF_YEAR.minimized()),
            is(PlainDate.of(2012, 1, 29)));
    }

    @Test
    public void monthOfYearMaximized() {
        assertThat(
            PlainDate.of(2012, 2, 29).with(MONTH_OF_YEAR.maximized()),
            is(PlainDate.of(2012, 12, 29)));
    }

    @Test
    public void monthOfYearIncremented() {
        assertThat(
            PlainDate.of(2012, 2, 1).with(MONTH_OF_YEAR.incremented()),
            is(PlainDate.of(2012, 3, 1)));
        assertThat(
            PlainDate.of(2013, 2, 1).with(MONTH_OF_YEAR.incremented()),
            is(PlainDate.of(2013, 3, 1)));
        assertThat(
            PlainDate.of(2013, 3, 31).with(MONTH_OF_YEAR.incremented()),
            is(PlainDate.of(2013, 4, 30)));
    }

    @Test
    public void monthOfYearDecremented() {
        assertThat(
            PlainDate.of(2012, 3, 1).with(MONTH_OF_YEAR.decremented()),
            is(PlainDate.of(2012, 2, 1)));
        assertThat(
            PlainDate.of(2013, 3, 1).with(MONTH_OF_YEAR.decremented()),
            is(PlainDate.of(2013, 2, 1)));
        assertThat(
            PlainDate.of(2013, 3, 31).with(MONTH_OF_YEAR.decremented()),
            is(PlainDate.of(2013, 2, 28)));
    }

    @Test
    public void monthOfYearAtFloor() {
        assertThat(
            PlainDate.of(2012, 3, 4).with(MONTH_OF_YEAR.atFloor()),
            is(PlainDate.of(2012, 3, 1)));
        assertThat(
            PlainTimestamp.of(2012, 3, 4, 12, 45)
                .with(MONTH_OF_YEAR.atFloor()),
            is(PlainTimestamp.of(2012, 3, 1, 0, 0)));
    }

    @Test
    public void monthOfYearAtCeiling() {
        assertThat(
            PlainDate.of(2012, 3, 4).with(MONTH_OF_YEAR.atCeiling()),
            is(PlainDate.of(2012, 3, 31)));
        assertThat(
            PlainTimestamp.of(2012, 3, 4, 12, 45)
                .with(MONTH_OF_YEAR.atCeiling()),
            is(
                PlainTimestamp.of(2012, 3, 31, 23, 59, 59)
                .with(PlainTime.NANO_OF_SECOND, 999999999)));
    }

    @Test
    public void monthAsNumberMinimized() {
        assertThat(
            PlainDate.of(2012, 2, 29).with(MONTH_AS_NUMBER.minimized()),
            is(PlainDate.of(2012, 1, 29)));
    }

    @Test
    public void monthAsNumberMaximized() {
        assertThat(
            PlainDate.of(2012, 2, 29).with(MONTH_AS_NUMBER.maximized()),
            is(PlainDate.of(2012, 12, 29)));
    }

    @Test
    public void monthAsNumberIncremented() {
        assertThat(
            PlainDate.of(2012, 2, 1).with(MONTH_AS_NUMBER.incremented()),
            is(PlainDate.of(2012, 3, 1)));
        assertThat(
            PlainDate.of(2013, 2, 1).with(MONTH_AS_NUMBER.incremented()),
            is(PlainDate.of(2013, 3, 1)));
        assertThat(
            PlainDate.of(2013, 3, 31).with(MONTH_AS_NUMBER.incremented()),
            is(PlainDate.of(2013, 4, 30)));
    }

    @Test
    public void monthAsNumberDecremented() {
        assertThat(
            PlainDate.of(2012, 3, 1).with(MONTH_AS_NUMBER.decremented()),
            is(PlainDate.of(2012, 2, 1)));
        assertThat(
            PlainDate.of(2013, 3, 1).with(MONTH_AS_NUMBER.decremented()),
            is(PlainDate.of(2013, 2, 1)));
        assertThat(
            PlainDate.of(2013, 3, 31).with(MONTH_AS_NUMBER.decremented()),
            is(PlainDate.of(2013, 2, 28)));
    }

    @Test
    public void monthAsNumberAtFloor() {
        assertThat(
            PlainDate.of(2012, 3, 4).with(MONTH_AS_NUMBER.atFloor()),
            is(PlainDate.of(2012, 3, 1)));
        assertThat(
            PlainTimestamp.of(2012, 3, 4, 12, 45)
                .with(MONTH_AS_NUMBER.atFloor()),
            is(PlainTimestamp.of(2012, 3, 1, 0, 0)));
    }

    @Test
    public void monthAsNumberAtCeiling() {
        assertThat(
            PlainDate.of(2012, 3, 4).with(MONTH_AS_NUMBER.atCeiling()),
            is(PlainDate.of(2012, 3, 31)));
        assertThat(
            PlainTimestamp.of(2012, 3, 4, 12, 45)
                .with(MONTH_AS_NUMBER.atCeiling()),
            is(
                PlainTimestamp.of(2012, 3, 31, 23, 59, 59)
                .with(PlainTime.NANO_OF_SECOND, 999999999)));
    }

    @Test
    public void quarterOfYearMinimized() {
        assertThat(
            PlainDate.of(2012, 5, 31).with(QUARTER_OF_YEAR.minimized()),
            is(PlainDate.of(2012, 2, 29)));
    }

    @Test
    public void quarterOfYearMaximized() {
        assertThat(
            PlainDate.of(2012, 5, 31).with(QUARTER_OF_YEAR.maximized()),
            is(PlainDate.of(2012, 11, 30)));
    }

    @Test
    public void quarterOfYearIncremented() {
        assertThat(
            PlainDate.of(2012, 2, 1).with(QUARTER_OF_YEAR.incremented()),
            is(PlainDate.of(2012, 5, 1)));
        assertThat(
            PlainDate.of(2013, 8, 31).with(QUARTER_OF_YEAR.incremented()),
            is(PlainDate.of(2013, 11, 30)));
    }

    @Test
    public void quarterOfYearDecremented() {
        assertThat(
            PlainDate.of(2012, 3, 1).with(QUARTER_OF_YEAR.decremented()),
            is(PlainDate.of(2011, 12, 1)));
        assertThat(
            PlainDate.of(2013, 12, 31).with(QUARTER_OF_YEAR.decremented()),
            is(PlainDate.of(2013, 9, 30)));
    }

    @Test
    public void quarterOfYearAtFloor() {
        assertThat(
            PlainDate.of(2012, 3, 4).with(QUARTER_OF_YEAR.atFloor()),
            is(PlainDate.of(2012, 1, 1)));
        assertThat(
            PlainTimestamp.of(2012, 3, 4, 12, 45)
                .with(QUARTER_OF_YEAR.atFloor()),
            is(PlainTimestamp.of(2012, 1, 1, 0, 0)));
    }

    @Test
    public void quarterOfYearAtCeiling() {
        assertThat(
            PlainDate.of(2012, 2, 4).with(QUARTER_OF_YEAR.atCeiling()),
            is(PlainDate.of(2012, 3, 31)));
        assertThat(
            PlainTimestamp.of(2012, 2, 4, 12, 45)
                .with(QUARTER_OF_YEAR.atCeiling()),
            is(
                PlainTimestamp.of(2012, 3, 31, 23, 59, 59)
                .with(PlainTime.NANO_OF_SECOND, 999999999)));
    }

    @Test
    public void dayOfWeekMinimized() {
        assertThat(
            PlainDate.of(2014, 4, 30).with(DAY_OF_WEEK.minimized()),
            is(PlainDate.of(2014, 4, 28)));
    }

    @Test
    public void dayOfWeekMaximized() {
        assertThat(
            PlainDate.of(2014, 4, 30).with(DAY_OF_WEEK.maximized()),
            is(PlainDate.of(2014, 5, 4)));
    }

    @Test
    public void dayOfWeekIncremented() {
        assertThat(
            PlainDate.of(2012, 2, 1).with(DAY_OF_WEEK.incremented()),
            is(PlainDate.of(2012, 2, 2)));
        assertThat(
            PlainDate.of(2014, 4, 20).with(DAY_OF_WEEK.incremented()),
            is(PlainDate.of(2014, 4, 21)));
    }

    @Test
    public void dayOfWeekDecremented() {
        assertThat(
            PlainDate.of(2012, 3, 1).with(DAY_OF_WEEK.decremented()),
            is(PlainDate.of(2012, 2, 29)));
        assertThat(
            PlainDate.of(2014, 4, 21).with(DAY_OF_WEEK.decremented()),
            is(PlainDate.of(2014, 4, 20)));
    }

    @Test
    public void dayOfWeekAtFloor() {
        assertThat(
            PlainDate.of(2012, 4, 4).with(DAY_OF_WEEK.atFloor()),
            is(PlainDate.of(2012, 4, 4)));
        assertThat(
            PlainTimestamp.of(2012, 4, 4, 12, 45)
                .with(DAY_OF_WEEK.atFloor()),
            is(PlainTimestamp.of(2012, 4, 4, 0, 0)));
    }

    @Test
    public void dayOfWeekAtCeiling() {
        assertThat(
            PlainDate.of(2012, 2, 4).with(DAY_OF_WEEK.atCeiling()),
            is(PlainDate.of(2012, 2, 4)));
        assertThat(
            PlainTimestamp.of(2012, 2, 4, 12, 45)
                .with(DAY_OF_WEEK.atCeiling()),
            is(
                PlainTimestamp.of(2012, 2, 4, 23, 59, 59)
                .with(PlainTime.NANO_OF_SECOND, 999999999)));
    }

    @Test
    public void dayOfQuarterMinimized() {
        assertThat(
            PlainDate.of(2014, 5, 30).with(DAY_OF_QUARTER.minimized()),
            is(PlainDate.of(2014, 4, 1)));
    }

    @Test
    public void dayOfQuarterMaximized() {
        assertThat(
            PlainDate.of(2014, 4, 2).with(DAY_OF_QUARTER.maximized()),
            is(PlainDate.of(2014, 6, 30)));
    }

    @Test
    public void dayOfQuarterIncremented() {
        assertThat(
            PlainDate.of(2012, 2, 1).with(DAY_OF_QUARTER.incremented()),
            is(PlainDate.of(2012, 2, 2)));
        assertThat(
            PlainDate.of(2014, 6, 30).with(DAY_OF_QUARTER.incremented()),
            is(PlainDate.of(2014, 7, 1)));
    }

    @Test
    public void dayOfQuarterDecremented() {
        assertThat(
            PlainDate.of(2012, 3, 1).with(DAY_OF_QUARTER.decremented()),
            is(PlainDate.of(2012, 2, 29)));
        assertThat(
            PlainDate.of(2014, 7, 1).with(DAY_OF_QUARTER.decremented()),
            is(PlainDate.of(2014, 6, 30)));
    }

    @Test
    public void dayOfQuarterAtFloor() {
        assertThat(
            PlainDate.of(2012, 4, 4).with(DAY_OF_QUARTER.atFloor()),
            is(PlainDate.of(2012, 4, 4)));
        assertThat(
            PlainTimestamp.of(2012, 4, 4, 12, 45)
                .with(DAY_OF_QUARTER.atFloor()),
            is(PlainTimestamp.of(2012, 4, 4, 0, 0)));
    }

    @Test
    public void dayOfQuarterAtCeiling() {
        assertThat(
            PlainDate.of(2012, 2, 4).with(DAY_OF_QUARTER.atCeiling()),
            is(PlainDate.of(2012, 2, 4)));
        assertThat(
            PlainTimestamp.of(2012, 2, 4, 12, 45)
                .with(DAY_OF_QUARTER.atCeiling()),
            is(
                PlainTimestamp.of(2012, 2, 4, 23, 59, 59)
                .with(PlainTime.NANO_OF_SECOND, 999999999)));
    }

    @Test
    public void dayOfYearMinimized() {
        assertThat(
            PlainDate.of(2014, 5, 30).with(DAY_OF_YEAR.minimized()),
            is(PlainDate.of(2014, 1, 1)));
    }

    @Test
    public void dayOfYearMaximized() {
        assertThat(
            PlainDate.of(2014, 4, 2).with(DAY_OF_YEAR.maximized()),
            is(PlainDate.of(2014, 12, 31)));
    }

    @Test
    public void dayOfYearIncremented() {
        assertThat(
            PlainDate.of(2012, 2, 1).with(DAY_OF_YEAR.incremented()),
            is(PlainDate.of(2012, 2, 2)));
        assertThat(
            PlainDate.of(2014, 12, 31).with(DAY_OF_YEAR.incremented()),
            is(PlainDate.of(2015, 1, 1)));
    }

    @Test
    public void dayOfYearDecremented() {
        assertThat(
            PlainDate.of(2012, 3, 1).with(DAY_OF_YEAR.decremented()),
            is(PlainDate.of(2012, 2, 29)));
        assertThat(
            PlainDate.of(2015, 1, 1).with(DAY_OF_YEAR.decremented()),
            is(PlainDate.of(2014, 12, 31)));
    }

    @Test
    public void dayOfYearAtFloor() {
        assertThat(
            PlainDate.of(2012, 4, 4).with(DAY_OF_YEAR.atFloor()),
            is(PlainDate.of(2012, 4, 4)));
        assertThat(
            PlainTimestamp.of(2012, 4, 4, 12, 45)
                .with(DAY_OF_YEAR.atFloor()),
            is(PlainTimestamp.of(2012, 4, 4, 0, 0)));
    }

    @Test
    public void dayOfYearAtCeiling() {
        assertThat(
            PlainDate.of(2012, 2, 4).with(DAY_OF_YEAR.atCeiling()),
            is(PlainDate.of(2012, 2, 4)));
        assertThat(
            PlainTimestamp.of(2012, 2, 4, 12, 45)
                .with(DAY_OF_YEAR.atCeiling()),
            is(
                PlainTimestamp.of(2012, 2, 4, 23, 59, 59)
                .with(PlainTime.NANO_OF_SECOND, 999999999)));
    }

    @Test
    public void dayOfMonthMinimized() {
        assertThat(
            PlainDate.of(2014, 5, 30).with(DAY_OF_MONTH.minimized()),
            is(PlainDate.of(2014, 5, 1)));
    }

    @Test
    public void dayOfMonthMaximized() {
        assertThat(
            PlainDate.of(2012, 2, 2).with(DAY_OF_MONTH.maximized()),
            is(PlainDate.of(2012, 2, 29)));
        assertThat(
            PlainDate.of(2014, 2, 2).with(DAY_OF_MONTH.maximized()),
            is(PlainDate.of(2014, 2, 28)));
    }

    @Test
    public void dayOfMonthIncremented() {
        assertThat(
            PlainDate.of(2012, 2, 1).with(DAY_OF_MONTH.incremented()),
            is(PlainDate.of(2012, 2, 2)));
        assertThat(
            PlainDate.of(2012, 2, 29).with(DAY_OF_MONTH.incremented()),
            is(PlainDate.of(2012, 3, 1)));
        assertThat(
            PlainDate.of(2014, 12, 31).with(DAY_OF_MONTH.incremented()),
            is(PlainDate.of(2015, 1, 1)));
    }

    @Test
    public void dayOfMonthDecremented() {
        assertThat(
            PlainDate.of(2012, 3, 1).with(DAY_OF_MONTH.decremented()),
            is(PlainDate.of(2012, 2, 29)));
        assertThat(
            PlainDate.of(2013, 3, 1).with(DAY_OF_MONTH.decremented()),
            is(PlainDate.of(2013, 2, 28)));
        assertThat(
            PlainDate.of(2015, 1, 1).with(DAY_OF_MONTH.decremented()),
            is(PlainDate.of(2014, 12, 31)));
    }

    @Test
    public void dayOfMonthAtFloor() {
        assertThat(
            PlainDate.of(2012, 4, 4).with(DAY_OF_MONTH.atFloor()),
            is(PlainDate.of(2012, 4, 4)));
        assertThat(
            PlainTimestamp.of(2012, 4, 4, 12, 45)
                .with(DAY_OF_MONTH.atFloor()),
            is(PlainTimestamp.of(2012, 4, 4, 0, 0)));
    }

    @Test
    public void dayOfMonthAtCeiling() {
        assertThat(
            PlainDate.of(2012, 2, 4).with(DAY_OF_MONTH.atCeiling()),
            is(PlainDate.of(2012, 2, 4)));
        assertThat(
            PlainTimestamp.of(2012, 2, 4, 12, 45)
                .with(DAY_OF_MONTH.atCeiling()),
            is(
                PlainTimestamp.of(2012, 2, 4, 23, 59, 59)
                .with(PlainTime.NANO_OF_SECOND, 999999999)));
    }

    @Test
    public void genericOrdinalWeekdayInMonth() {
        assertThat(
            PlainDate.of(2015, 6, 11).with(PlainDate.WEEKDAY_IN_MONTH.setTo(0, Weekday.MONDAY)),
            is(PlainDate.of(2015, 5, 25)));
        assertThat(
            PlainDate.of(2015, 6, 11).with(PlainDate.WEEKDAY_IN_MONTH.setTo(Integer.MAX_VALUE, Weekday.MONDAY)),
            is(PlainDate.of(2015, 6, 29)));
    }

    @Test
    public void setToApril() {
        assertThat(
            PlainDate.of(2018, 10, 31).with(Month.APRIL),
            is(PlainDate.of(2018, 4, 30)));
    }

    @Test
    public void setToTuesday() {
        assertThat(
            PlainDate.of(2018, 10, 7).with(Weekday.TUESDAY),
            is(PlainDate.of(2018, 10, 2)));
    }

    @Test
    public void setToSecondQuarter() {
        assertThat(
            PlainDate.of(2018, 10, 31).with(Quarter.Q2),
            is(PlainDate.of(2018, 4, 30)));
    }

}