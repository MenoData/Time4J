package net.time4j.calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class IndianOperatorTest {

    @Test
    public void plusYears() {
        assertThat(
            IndianCalendar.of(1918, IndianMonth.CHAITRA, 31).plus(3, IndianCalendar.Unit.YEARS),
            is(IndianCalendar.of(1921, IndianMonth.CHAITRA, 30)));
        assertThat(
            IndianCalendar.of(1921, 1, 1).isLeapYear(),
            is(false));
    }

    @Test
    public void plusMonths() {
        assertThat(
            IndianCalendar.of(1932, IndianMonth.VAISHAKHA, 31).plus(17, IndianCalendar.Unit.MONTHS),
            is(IndianCalendar.of(1933, IndianMonth.ASHWIN, 30)));
    }

    @Test
    public void plusWeeks() {
        assertThat(
            IndianCalendar.of(1936, IndianMonth.PHALGUNA, 29).plus(2, IndianCalendar.Unit.WEEKS),
            is(IndianCalendar.of(1937, IndianMonth.CHAITRA, 13)));
    }

    @Test
    public void plusDays() {
        assertThat(
            IndianCalendar.of(1936, IndianMonth.PHALGUNA, 29).plus(11, IndianCalendar.Unit.DAYS),
            is(IndianCalendar.of(1937, IndianMonth.CHAITRA, 10)));
    }

    @Test
    public void nextMonth() {
        assertThat(
            IndianCalendar.of(1936, 6, 31).with(IndianCalendar.MONTH_OF_YEAR.incremented()),
            is(IndianCalendar.of(1936, 7, 30)));
    }

    @Test
    public void previousMonth() {
        assertThat(
            IndianCalendar.of(1938, 1, 31).with(IndianCalendar.MONTH_OF_YEAR.decremented()),
            is(IndianCalendar.of(1937, 12, 30)));
    }

    @Test
    public void nextYear() {
        assertThat(
            IndianCalendar.of(1938, 1, 31).with(IndianCalendar.YEAR_OF_ERA.incremented()),
            is(IndianCalendar.of(1939, 1, 30)));
    }

    @Test
    public void previousYear() {
        assertThat(
            IndianCalendar.of(1938, 7, 14).with(IndianCalendar.YEAR_OF_ERA.decremented()),
            is(IndianCalendar.of(1937, 7, 14)));
    }

    @Test
    public void nextDay() {
        assertThat(
            IndianCalendar.of(1935, IndianMonth.VAISHAKHA, 31).with(IndianCalendar.DAY_OF_YEAR.incremented()),
            is(IndianCalendar.of(1935, IndianMonth.JYESHTHA, 1)));
        assertThat(
            IndianCalendar.of(1935, IndianMonth.VAISHAKHA, 31).with(IndianCalendar.DAY_OF_MONTH.incremented()),
            is(IndianCalendar.of(1935, IndianMonth.JYESHTHA, 1)));
        assertThat(
            IndianCalendar.of(1935, IndianMonth.VAISHAKHA, 31).with(IndianCalendar.DAY_OF_WEEK.incremented()),
            is(IndianCalendar.of(1935, IndianMonth.JYESHTHA, 1)));
    }

    @Test
    public void previousDay() {
        assertThat(
            IndianCalendar.of(1935, IndianMonth.VAISHAKHA, 1).with(IndianCalendar.DAY_OF_YEAR.decremented()),
            is(IndianCalendar.of(1935, IndianMonth.CHAITRA, 30)));
        assertThat(
            IndianCalendar.of(1935, IndianMonth.VAISHAKHA, 1).with(IndianCalendar.DAY_OF_MONTH.decremented()),
            is(IndianCalendar.of(1935, IndianMonth.CHAITRA, 30)));
        assertThat(
            IndianCalendar.of(1935, IndianMonth.VAISHAKHA, 1).with(IndianCalendar.DAY_OF_WEEK.decremented()),
            is(IndianCalendar.of(1935, IndianMonth.CHAITRA, 30)));
    }

    @Test
    public void maxDay() {
        assertThat(
            IndianCalendar.of(1934, IndianMonth.CHAITRA, 21).with(IndianCalendar.DAY_OF_MONTH.maximized()),
            is(IndianCalendar.of(1934, IndianMonth.CHAITRA, 31)));
        assertThat(
            IndianCalendar.of(1935, IndianMonth.CHAITRA, 21).with(IndianCalendar.DAY_OF_MONTH.maximized()),
            is(IndianCalendar.of(1935, IndianMonth.CHAITRA, 30)));
    }

    @Test
    public void minDay() {
        assertThat(
            IndianCalendar.of(1936, IndianMonth.AGRAHAYANA, 21).with(IndianCalendar.DAY_OF_MONTH.minimized()),
            is(IndianCalendar.of(1936, IndianMonth.AGRAHAYANA, 1)));
    }

    @Test
    public void yearAtFloor() {
        assertThat(
            IndianCalendar.of(1936, IndianMonth.KARTIKA, 2).with(IndianCalendar.YEAR_OF_ERA.atFloor()),
            is(IndianCalendar.of(1936, IndianMonth.CHAITRA, 1)));
    }

    @Test
    public void yearAtCeiling() {
        assertThat(
            IndianCalendar.of(1934, IndianMonth.KARTIKA, 2).with(IndianCalendar.YEAR_OF_ERA.atCeiling()),
            is(IndianCalendar.of(1934, IndianMonth.PHALGUNA, 30)));
    }

    @Test
    public void monthAtFloor() {
        assertThat(
            IndianCalendar.of(1934, IndianMonth.CHAITRA, 2).with(IndianCalendar.MONTH_OF_YEAR.atFloor()),
            is(IndianCalendar.of(1934, IndianMonth.CHAITRA, 1)));
    }

    @Test
    public void monthAtCeiling() {
        assertThat(
            IndianCalendar.of(1934, IndianMonth.CHAITRA, 2).with(IndianCalendar.MONTH_OF_YEAR.atCeiling()),
            is(IndianCalendar.of(1934, IndianMonth.CHAITRA, 31)));
        assertThat(
            IndianCalendar.of(1935, IndianMonth.CHAITRA, 2).with(IndianCalendar.MONTH_OF_YEAR.atCeiling()),
            is(IndianCalendar.of(1935, IndianMonth.CHAITRA, 30)));
    }

}