package net.time4j.calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class PersianOperatorTest {

    @Test
    public void plusYears() {
        assertThat(
            PersianCalendar.of(1436, PersianMonth.ESFAND, 30).plus(2, PersianCalendar.Unit.YEARS),
            is(PersianCalendar.of(1438, PersianMonth.ESFAND, 29)));
        assertThat(
            PersianCalendar.of(1438, 1, 1).isLeapYear(),
            is(false));
    }

    @Test
    public void plusMonths() {
        assertThat(
            PersianCalendar.of(1436, PersianMonth.KHORDAD, 31).plus(4, PersianCalendar.Unit.MONTHS),
            is(PersianCalendar.of(1436, PersianMonth.MEHR, 30)));
    }

    @Test
    public void plusWeeks() {
        assertThat(
            PersianCalendar.of(1436, PersianMonth.KHORDAD, 30).plus(2, PersianCalendar.Unit.WEEKS),
            is(PersianCalendar.of(1436, PersianMonth.TIR, 13)));
    }

    @Test
    public void plusDays() {
        assertThat(
            PersianCalendar.of(1436, PersianMonth.KHORDAD, 30).plus(4, PersianCalendar.Unit.DAYS),
            is(PersianCalendar.of(1436, PersianMonth.TIR, 3)));
    }

    @Test
    public void nextMonth() {
        assertThat(
            PersianCalendar.of(1436, 3, 29).with(PersianCalendar.MONTH_OF_YEAR.incremented()),
            is(PersianCalendar.of(1436, 4, 29)));
    }

    @Test
    public void previousMonth() {
        assertThat(
            PersianCalendar.of(1436, 3, 29).with(PersianCalendar.MONTH_OF_YEAR.decremented()),
            is(PersianCalendar.of(1436, 2, 29)));
    }

    @Test
    public void nextYear() {
        assertThat(
            PersianCalendar.of(1436, 3, 29).with(PersianCalendar.YEAR_OF_ERA.incremented()),
            is(PersianCalendar.of(1437, 3, 29)));
    }

    @Test
    public void previousYear() {
        assertThat(
            PersianCalendar.of(1436, 3, 29).with(PersianCalendar.YEAR_OF_ERA.decremented()),
            is(PersianCalendar.of(1435, 3, 29)));
    }

    @Test
    public void nextDay() {
        assertThat(
            PersianCalendar.of(1436, PersianMonth.KHORDAD, 31).with(PersianCalendar.DAY_OF_YEAR.incremented()),
            is(PersianCalendar.of(1436, PersianMonth.TIR, 1)));
        assertThat(
            PersianCalendar.of(1436, PersianMonth.KHORDAD, 31).with(PersianCalendar.DAY_OF_MONTH.incremented()),
            is(PersianCalendar.of(1436, PersianMonth.TIR, 1)));
        assertThat(
            PersianCalendar.of(1436, PersianMonth.KHORDAD, 31).with(PersianCalendar.DAY_OF_WEEK.incremented()),
            is(PersianCalendar.of(1436, PersianMonth.TIR, 1)));
    }

    @Test
    public void previousDay() {
        assertThat(
            PersianCalendar.of(1436, PersianMonth.KHORDAD, 31).with(PersianCalendar.DAY_OF_YEAR.decremented()),
            is(PersianCalendar.of(1436, PersianMonth.KHORDAD, 30)));
        assertThat(
            PersianCalendar.of(1436, PersianMonth.KHORDAD, 31).with(PersianCalendar.DAY_OF_MONTH.decremented()),
            is(PersianCalendar.of(1436, PersianMonth.KHORDAD, 30)));
        assertThat(
            PersianCalendar.of(1436, PersianMonth.KHORDAD, 31).with(PersianCalendar.DAY_OF_WEEK.decremented()),
            is(PersianCalendar.of(1436, PersianMonth.KHORDAD, 30)));
    }

    @Test
    public void maxDay() {
        assertThat(
            PersianCalendar.of(1436, PersianMonth.KHORDAD, 21).with(PersianCalendar.DAY_OF_MONTH.maximized()),
            is(PersianCalendar.of(1436, PersianMonth.KHORDAD, 31)));
        assertThat(
            PersianCalendar.of(1436, PersianMonth.ESFAND, 21).with(PersianCalendar.DAY_OF_MONTH.maximized()),
            is(PersianCalendar.of(1436, PersianMonth.ESFAND, 30)));
    }

    @Test
    public void minDay() {
        assertThat(
            PersianCalendar.of(1436, PersianMonth.ESFAND, 21).with(PersianCalendar.DAY_OF_MONTH.minimized()),
            is(PersianCalendar.of(1436, PersianMonth.ESFAND, 1)));
    }

    @Test
    public void yearAtFloor() {
        assertThat(
            PersianCalendar.of(1436, PersianMonth.SHAHRIVAR, 2).with(PersianCalendar.YEAR_OF_ERA.atFloor()),
            is(PersianCalendar.of(1436, PersianMonth.FARVARDIN, 1)));
    }

    @Test
    public void yearAtCeiling() {
        assertThat(
            PersianCalendar.of(1436, PersianMonth.SHAHRIVAR, 2).with(PersianCalendar.YEAR_OF_ERA.atCeiling()),
            is(PersianCalendar.of(1436, PersianMonth.ESFAND, 30)));
    }

    @Test
    public void monthAtFloor() {
        assertThat(
            PersianCalendar.of(1436, PersianMonth.SHAHRIVAR, 2).with(PersianCalendar.MONTH_OF_YEAR.atFloor()),
            is(PersianCalendar.of(1436, PersianMonth.SHAHRIVAR, 1)));
    }

    @Test
    public void monthAtCeiling() {
        assertThat(
            PersianCalendar.of(1436, PersianMonth.SHAHRIVAR, 2).with(PersianCalendar.MONTH_OF_YEAR.atCeiling()),
            is(PersianCalendar.of(1436, PersianMonth.SHAHRIVAR, 31)));
    }

}