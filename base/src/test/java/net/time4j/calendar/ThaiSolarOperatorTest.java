package net.time4j.calendar;

import net.time4j.CalendarUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class ThaiSolarOperatorTest {

    @Test
    public void plusYears() {
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2478, 2, 29).plus(4, CalendarUnit.YEARS),
            is(ThaiSolarCalendar.ofBuddhist(2482, 2, 29)));
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2482, 1, 1).isLeapYear(),
            is(true));
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2482, 2, 29).plus(2, CalendarUnit.YEARS),
            is(ThaiSolarCalendar.ofBuddhist(2485, 2, 28)));
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2485, 1, 1).isLeapYear(),
            is(false));
    }

    @Test
    public void plusMonths() {
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2482, 2, 29).plus(15, CalendarUnit.MONTHS),
            is(ThaiSolarCalendar.ofBuddhist(2484, 5, 29)));
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2547, 2, 29).plus(15, CalendarUnit.MONTHS),
            is(ThaiSolarCalendar.ofBuddhist(2548, 5, 29)));
    }

    @Test
    public void plusWeeks() {
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2482, 3, 31).plus(2, CalendarUnit.WEEKS),
            is(ThaiSolarCalendar.ofBuddhist(2483, 4, 14)));
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2547, 3, 31).plus(2, CalendarUnit.WEEKS),
            is(ThaiSolarCalendar.ofBuddhist(2547, 4, 14)));
    }

    @Test
    public void plusDays() {
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2482, 3, 30).plus(3, CalendarUnit.DAYS),
            is(ThaiSolarCalendar.ofBuddhist(2483, 4, 2)));
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2547, 3, 30).plus(3, CalendarUnit.DAYS),
            is(ThaiSolarCalendar.ofBuddhist(2547, 4, 2)));
    }

    @Test
    public void nextMonth() {
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2482, 3, 30).with(ThaiSolarCalendar.MONTH_OF_YEAR.incremented()),
            is(ThaiSolarCalendar.ofBuddhist(2483, 4, 30)));
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2547, 3, 30).with(ThaiSolarCalendar.MONTH_OF_YEAR.incremented()),
            is(ThaiSolarCalendar.ofBuddhist(2547, 4, 30)));
    }

    @Test
    public void previousMonth() {
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2483, 4, 30).with(ThaiSolarCalendar.MONTH_OF_YEAR.decremented()),
            is(ThaiSolarCalendar.ofBuddhist(2482, 3, 30)));
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2547, 4, 30).with(ThaiSolarCalendar.MONTH_OF_YEAR.decremented()),
            is(ThaiSolarCalendar.ofBuddhist(2547, 3, 30)));
    }

    @Test
    public void nextYear() {
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2482, 3, 30).with(ThaiSolarCalendar.YEAR_OF_ERA.incremented()),
            is(ThaiSolarCalendar.ofBuddhist(2484, 3, 30)));
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2547, 3, 30).with(ThaiSolarCalendar.YEAR_OF_ERA.incremented()),
            is(ThaiSolarCalendar.ofBuddhist(2548, 3, 30)));
    }

    @Test
    public void previousYear() {
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2484, 3, 30).with(ThaiSolarCalendar.YEAR_OF_ERA.decremented()),
            is(ThaiSolarCalendar.ofBuddhist(2482, 3, 30)));
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2548, 3, 30).with(ThaiSolarCalendar.YEAR_OF_ERA.decremented()),
            is(ThaiSolarCalendar.ofBuddhist(2547, 3, 30)));
    }

    @Test
    public void nextDay() {
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2482, 3, 31).with(ThaiSolarCalendar.DAY_OF_YEAR.incremented()),
            is(ThaiSolarCalendar.ofBuddhist(2483, 4, 1)));
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2547, 3, 31).with(ThaiSolarCalendar.DAY_OF_YEAR.incremented()),
            is(ThaiSolarCalendar.ofBuddhist(2547, 4, 1)));
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2482, 3, 31).with(ThaiSolarCalendar.DAY_OF_MONTH.incremented()),
            is(ThaiSolarCalendar.ofBuddhist(2483, 4, 1)));
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2547, 3, 31).with(ThaiSolarCalendar.DAY_OF_MONTH.incremented()),
            is(ThaiSolarCalendar.ofBuddhist(2547, 4, 1)));
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2482, 3, 31).with(ThaiSolarCalendar.DAY_OF_WEEK.incremented()),
            is(ThaiSolarCalendar.ofBuddhist(2483, 4, 1)));
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2547, 3, 31).with(ThaiSolarCalendar.DAY_OF_WEEK.incremented()),
            is(ThaiSolarCalendar.ofBuddhist(2547, 4, 1)));
    }

    @Test
    public void previousDay() {
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2483, 4, 1).with(ThaiSolarCalendar.DAY_OF_YEAR.decremented()),
            is(ThaiSolarCalendar.ofBuddhist(2482, 3, 31)));
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2547, 4, 1).with(ThaiSolarCalendar.DAY_OF_YEAR.decremented()),
            is(ThaiSolarCalendar.ofBuddhist(2547, 3, 31)));
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2483, 4, 1).with(ThaiSolarCalendar.DAY_OF_MONTH.decremented()),
            is(ThaiSolarCalendar.ofBuddhist(2482, 3, 31)));
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2547, 4, 1).with(ThaiSolarCalendar.DAY_OF_MONTH.decremented()),
            is(ThaiSolarCalendar.ofBuddhist(2547, 3, 31)));
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2483, 4, 1).with(ThaiSolarCalendar.DAY_OF_WEEK.decremented()),
            is(ThaiSolarCalendar.ofBuddhist(2482, 3, 31)));
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2547, 4, 1).with(ThaiSolarCalendar.DAY_OF_WEEK.decremented()),
            is(ThaiSolarCalendar.ofBuddhist(2547, 3, 31)));
    }

    @Test
    public void maxDay() {
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2482, 2, 1).with(ThaiSolarCalendar.DAY_OF_MONTH.maximized()),
            is(ThaiSolarCalendar.ofBuddhist(2482, 2, 29)));
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2484, 2, 1).with(ThaiSolarCalendar.DAY_OF_MONTH.maximized()),
            is(ThaiSolarCalendar.ofBuddhist(2484, 2, 28)));
    }

    @Test
    public void minDay() {
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2482, 2, 29).with(ThaiSolarCalendar.DAY_OF_MONTH.minimized()),
            is(ThaiSolarCalendar.ofBuddhist(2482, 2, 1)));
    }

    @Test
    public void yearAtFloor() {
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2482, 2, 11).with(ThaiSolarCalendar.YEAR_OF_ERA.atFloor()),
            is(ThaiSolarCalendar.ofBuddhist(2482, 4, 1)));
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2547, 2, 11).with(ThaiSolarCalendar.YEAR_OF_ERA.atFloor()),
            is(ThaiSolarCalendar.ofBuddhist(2547, 1, 1)));
    }

    @Test
    public void yearAtCeiling() {
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2482, 2, 1).with(ThaiSolarCalendar.YEAR_OF_ERA.atCeiling()),
            is(ThaiSolarCalendar.ofBuddhist(2482, 3, 31)));
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2547, 2, 1).with(ThaiSolarCalendar.YEAR_OF_ERA.atCeiling()),
            is(ThaiSolarCalendar.ofBuddhist(2547, 12, 31)));
    }

    @Test
    public void monthAtFloor() {
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2482, 2, 29).with(ThaiSolarCalendar.MONTH_OF_YEAR.atFloor()),
            is(ThaiSolarCalendar.ofBuddhist(2482, 2, 1)));
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2547, 2, 29).with(ThaiSolarCalendar.MONTH_OF_YEAR.atFloor()),
            is(ThaiSolarCalendar.ofBuddhist(2547, 2, 1)));
    }

    @Test
    public void monthAtCeiling() {
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2482, 2, 1).with(ThaiSolarCalendar.MONTH_OF_YEAR.atCeiling()),
            is(ThaiSolarCalendar.ofBuddhist(2482, 2, 29)));
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2547, 2, 1).with(ThaiSolarCalendar.MONTH_OF_YEAR.atCeiling()),
            is(ThaiSolarCalendar.ofBuddhist(2547, 2, 29)));
    }

}