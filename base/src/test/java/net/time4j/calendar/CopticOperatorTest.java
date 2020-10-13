package net.time4j.calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class CopticOperatorTest {

    @Test
    public void plusYears() {
        assertThat(
            CopticCalendar.of(1735, CopticMonth.NASIE, 6).plus(3, CopticCalendar.Unit.YEARS),
            is(CopticCalendar.of(1738, CopticMonth.NASIE, 5)));
        assertThat(
            CopticCalendar.of(1738, 1, 1).isLeapYear(),
            is(false));
    }

    @Test
    public void plusMonths() {
        assertThat(
            CopticCalendar.of(1732, CopticMonth.BASHANS, 30).plus(17, CopticCalendar.Unit.MONTHS),
            is(CopticCalendar.of(1733, CopticMonth.NASIE, 5)));
    }

    @Test
    public void plusWeeks() {
        assertThat(
            CopticCalendar.of(1736, CopticMonth.MESRA, 30).plus(2, CopticCalendar.Unit.WEEKS),
            is(CopticCalendar.of(1737, CopticMonth.TOUT, 9)));
    }

    @Test
    public void plusDays() {
        assertThat(
            CopticCalendar.of(1736, CopticMonth.MESRA, 30).plus(30, CopticCalendar.Unit.DAYS),
            is(CopticCalendar.of(1737, CopticMonth.TOUT, 25)));
    }

    @Test
    public void nextMonth() {
        assertThat(
            CopticCalendar.of(1736, 13, 5).with(CopticCalendar.MONTH_OF_YEAR.incremented()),
            is(CopticCalendar.of(1737, 1, 5)));
    }

    @Test
    public void previousMonth() {
        assertThat(
            CopticCalendar.of(1737, 1, 5).with(CopticCalendar.MONTH_OF_YEAR.decremented()),
            is(CopticCalendar.of(1736, 13, 5)));
    }

    @Test
    public void nextYear() {
        assertThat(
            CopticCalendar.of(1735, 13, 6).with(CopticCalendar.YEAR_OF_ERA.incremented()),
            is(CopticCalendar.of(1736, 13, 5)));
    }

    @Test
    public void previousYear() {
        assertThat(
            CopticCalendar.of(1736, 3, 30).with(CopticCalendar.YEAR_OF_ERA.decremented()),
            is(CopticCalendar.of(1735, 3, 30)));
    }

    @Test
    public void nextDay() {
        assertThat(
            CopticCalendar.of(1735, CopticMonth.NASIE, 6).with(CopticCalendar.DAY_OF_YEAR.incremented()),
            is(CopticCalendar.of(1736, CopticMonth.TOUT, 1)));
        assertThat(
            CopticCalendar.of(1735, CopticMonth.NASIE, 6).with(CopticCalendar.DAY_OF_MONTH.incremented()),
            is(CopticCalendar.of(1736, CopticMonth.TOUT, 1)));
        assertThat(
            CopticCalendar.of(1735, CopticMonth.NASIE, 6).with(CopticCalendar.DAY_OF_WEEK.incremented()),
            is(CopticCalendar.of(1736, CopticMonth.TOUT, 1)));
    }

    @Test
    public void previousDay() {
        assertThat(
            CopticCalendar.of(1736, CopticMonth.TOUT, 1).with(CopticCalendar.DAY_OF_YEAR.decremented()),
            is(CopticCalendar.of(1735, CopticMonth.NASIE, 6)));
        assertThat(
            CopticCalendar.of(1736, CopticMonth.TOUT, 1).with(CopticCalendar.DAY_OF_MONTH.decremented()),
            is(CopticCalendar.of(1735, CopticMonth.NASIE, 6)));
        assertThat(
            CopticCalendar.of(1736, CopticMonth.TOUT, 1).with(CopticCalendar.DAY_OF_WEEK.decremented()),
            is(CopticCalendar.of(1735, CopticMonth.NASIE, 6)));
    }

    @Test
    public void maxDay() {
        assertThat(
            CopticCalendar.of(1736, CopticMonth.BARAMHAT, 21).with(CopticCalendar.DAY_OF_MONTH.maximized()),
            is(CopticCalendar.of(1736, CopticMonth.BARAMHAT, 30)));
        assertThat(
            CopticCalendar.of(1735, CopticMonth.NASIE, 2).with(CopticCalendar.DAY_OF_MONTH.maximized()),
            is(CopticCalendar.of(1735, CopticMonth.NASIE, 6)));
        assertThat(
            CopticCalendar.of(1736, CopticMonth.NASIE, 2).with(CopticCalendar.DAY_OF_MONTH.maximized()),
            is(CopticCalendar.of(1736, CopticMonth.NASIE, 5)));
    }

    @Test
    public void minDay() {
        assertThat(
            CopticCalendar.of(1736, CopticMonth.BARAMHAT, 21).with(CopticCalendar.DAY_OF_MONTH.minimized()),
            is(CopticCalendar.of(1736, CopticMonth.BARAMHAT, 1)));
    }

    @Test
    public void yearAtFloor() {
        assertThat(
            CopticCalendar.of(1736, CopticMonth.BARAMOUDA, 2).with(CopticCalendar.YEAR_OF_ERA.atFloor()),
            is(CopticCalendar.of(1736, CopticMonth.TOUT, 1)));
    }

    @Test
    public void yearAtCeiling() {
        assertThat(
            CopticCalendar.of(1735, CopticMonth.BARAMOUDA, 2).with(CopticCalendar.YEAR_OF_ERA.atCeiling()),
            is(CopticCalendar.of(1735, CopticMonth.NASIE, 6)));
        assertThat(
            CopticCalendar.of(1736, CopticMonth.BARAMOUDA, 2).with(CopticCalendar.YEAR_OF_ERA.atCeiling()),
            is(CopticCalendar.of(1736, CopticMonth.NASIE, 5)));
    }

    @Test
    public void monthAtFloor() {
        assertThat(
            CopticCalendar.of(1736, CopticMonth.BARAMOUDA, 2).with(CopticCalendar.MONTH_OF_YEAR.atFloor()),
            is(CopticCalendar.of(1736, CopticMonth.BARAMOUDA, 1)));
    }

    @Test
    public void monthAtCeiling() {
        assertThat(
            CopticCalendar.of(1736, CopticMonth.BARAMOUDA, 2).with(CopticCalendar.MONTH_OF_YEAR.atCeiling()),
            is(CopticCalendar.of(1736, CopticMonth.BARAMOUDA, 30)));
        assertThat(
            CopticCalendar.of(1735, CopticMonth.NASIE, 2).with(CopticCalendar.YEAR_OF_ERA.atCeiling()),
            is(CopticCalendar.of(1735, CopticMonth.NASIE, 6)));
        assertThat(
            CopticCalendar.of(1736, CopticMonth.NASIE, 2).with(CopticCalendar.YEAR_OF_ERA.atCeiling()),
            is(CopticCalendar.of(1736, CopticMonth.NASIE, 5)));
    }

}