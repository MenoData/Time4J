package net.time4j;

import net.time4j.engine.Chronology;

import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.PlainDate.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class MiscDatePropertiesTest {

    @Test
    public void lengthOfYear() {
        assertThat(PlainDate.of(2000, 1).lengthOfYear(), is(366));
        assertThat(PlainDate.of(2001, 1).lengthOfYear(), is(365));
    }

    @Test
    public void toStringISO() {
        assertThat(PlainDate.of(2000, 4, 7).toString(), is("2000-04-07"));
        assertThat(PlainDate.of(-2000, 10, 17).toString(), is("-2000-10-17"));
        assertThat(PlainDate.of(10000, 4, 7).toString(), is("+10000-04-07"));
    }

    @Test
    public void isWeekendISO() {
        assertThat(
            PlainDate.of(2014, 4, 4).isWeekend(Locale.ROOT),
            is(false));
        assertThat(
            PlainDate.of(2014, 4, 5).isWeekend(Locale.ROOT),
            is(true));
        assertThat(
            PlainDate.of(2014, 4, 6).isWeekend(Locale.ROOT),
            is(true));
        assertThat(
            PlainDate.of(2014, 4, 7).isWeekend(Locale.ROOT),
            is(false));
    }

    @Test
    public void isWeekendUS() {
        assertThat(
            PlainDate.of(2014, 4, 4).isWeekend(Locale.US),
            is(false));
        assertThat(
            PlainDate.of(2014, 4, 5).isWeekend(Locale.US),
            is(true));
        assertThat(
            PlainDate.of(2014, 4, 6).isWeekend(Locale.US),
            is(true));
        assertThat(
            PlainDate.of(2014, 4, 7).isWeekend(Locale.US),
            is(false));
    }

    @Test
    public void isWeekendYemen() {
        Locale yemen = new Locale("ar", "Ye"); // Thursday + Friday
        assertThat(
            PlainDate.of(2014, 4, 2).isWeekend(yemen),
            is(false));
        assertThat(
            PlainDate.of(2014, 4, 3).isWeekend(yemen),
            is(true));
        assertThat(
            PlainDate.of(2014, 4, 4).isWeekend(yemen),
            is(true));
        assertThat(
            PlainDate.of(2014, 4, 5).isWeekend(yemen),
            is(false));
    }

    @Test
    public void getChronology() {
        PlainDate any = PlainDate.of(2000, 1);
        assertThat(
            (any.getChronology() == Chronology.lookup(PlainDate.class)),
            is(true));
    }

    @Test
    public void getCalendarDate() {
        PlainDate any = PlainDate.of(2000, 1);
        assertThat(
            (any.get(CALENDAR_DATE) == any),
            is(true));
    }

    @Test
    public void isValidCalendarDate() {
        PlainDate any = PlainDate.of(2000, 1);
        assertThat(
            any.isValid(CALENDAR_DATE, PlainDate.of(2014, 4, 5)),
            is(true));
        assertThat(
            any.isValid(CALENDAR_DATE, null),
            is(false));
    }

    @Test
    public void withCalendarDate1() {
        PlainDate any = PlainDate.of(2000, 1);
        PlainDate value = PlainDate.of(2014, 4, 5);
        assertThat(
            any.with(CALENDAR_DATE, value),
            is(value));
    }

    @Test(expected=NullPointerException.class)
    public void withCalendarDate2() {
        PlainDate.of(2000, 1).with(CALENDAR_DATE, null);
    }

    @Test
    public void getYear() {
        assertThat(
            PlainDate.of(2000, 1).get(YEAR),
            is(2000));
    }

    @Test
    public void isValidYear() {
        PlainDate any = PlainDate.of(2000, 1);
        assertThat(
            any.isValid(YEAR, 2014),
            is(true));
        assertThat(
            any.isValid(YEAR, 1000000000),
            is(false));
        assertThat(
            any.isValid(YEAR, 999999999),
            is(true));
        assertThat(
            any.isValid(YEAR, null),
            is(false));
    }

    @Test
    public void withYear1() {
        PlainDate date = PlainDate.of(2000, 2, 29);
        assertThat(
            date.with(YEAR, 2014),
            is(PlainDate.of(2014, 2, 28)));
    }

    @Test(expected=NullPointerException.class)
    public void withYear2() {
        PlainDate.of(2000, 1).with(YEAR, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withYear3() {
        PlainDate.of(2000, 1).with(YEAR, 1234567890);
    }

    @Test
    public void getYearOfWeekdate1() {
        assertThat(
            PlainDate.of(2012, 1).get(YEAR_OF_WEEKDATE),
            is(2011));
    }

    @Test
    public void getYearOfWeekdate2() {
        assertThat(
            PlainDate.of(2014, 1).get(YEAR_OF_WEEKDATE),
            is(2014));
    }

    @Test
    public void isValidYearOfWeekdate() {
        PlainDate any = PlainDate.of(2000, 1);
        assertThat(
            any.isValid(YEAR_OF_WEEKDATE, 2014),
            is(true));
        assertThat(
            any.isValid(YEAR_OF_WEEKDATE, 1000000000),
            is(false));
        assertThat(
            any.isValid(YEAR_OF_WEEKDATE, 999999999),
            is(true));
        assertThat(
            any.isValid(YEAR_OF_WEEKDATE, null),
            is(false));
    }

    @Test
    public void withYearOfWeekdate1() {
        PlainDate date = PlainDate.of(2000, 2, 29); // 2000-W09-2
        assertThat(
            date.with(YEAR_OF_WEEKDATE, 2014),
            is(PlainDate.of(2014, 2, 25))); // 2014-W09-2
    }

    @Test(expected=NullPointerException.class)
    public void withYearOfWeekdate2() {
        PlainDate.of(2000, 1).with(YEAR_OF_WEEKDATE, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withYearOfWeekdate3() {
        PlainDate.of(2000, 1).with(YEAR_OF_WEEKDATE, 1234567890);
    }

    @Test
    public void getQuarterOfYear() {
        assertThat(
            PlainDate.of(2014, 3, 31).get(QUARTER_OF_YEAR),
            is(Quarter.Q1));
        assertThat(
            PlainDate.of(2014, 111).get(QUARTER_OF_YEAR),
            is(Quarter.Q2));
        assertThat(
            PlainDate.of(2014, 9, 20).get(QUARTER_OF_YEAR),
            is(Quarter.Q3));
        assertThat(
            PlainDate.of(2014, 10, 1).get(QUARTER_OF_YEAR),
            is(Quarter.Q4));
    }

    @Test
    public void getMonthOfYear() {
        assertThat(
            PlainDate.of(2014, 111).get(MONTH_OF_YEAR),
            is(Month.APRIL));
    }

    @Test
    public void getMonthAsNumber() {
        assertThat(
            PlainDate.of(2014, 111).get(MONTH_AS_NUMBER),
            is(4));
    }

    @Test
    public void getWeekdayInMonth() {
        assertThat(
            PlainDate.of(2014, 4, 21).get(WEEKDAY_IN_MONTH),
            is(3));
        assertThat(
            PlainDate.of(2014, 4, 28).get(WEEKDAY_IN_MONTH),
            is(4));
        assertThat(
            PlainDate.of(2014, 4, 30).get(WEEKDAY_IN_MONTH),
            is(5));
    }

    @Test
    public void getDayOfMonth() {
        assertThat(
            PlainDate.of(2014, 111).get(DAY_OF_MONTH),
            is(21));
    }

    @Test
    public void getDayOfYear() {
        assertThat(
            PlainDate.of(2014, 111).get(DAY_OF_YEAR),
            is(111));
    }

    @Test
    public void getDayOfWeek() {
        assertThat(
            PlainDate.of(2014, 111).get(DAY_OF_WEEK),
            is(Weekday.MONDAY));
    }

    @Test
    public void getDayOfQuarter() {
        assertThat(
            PlainDate.of(2014, 3, 31).get(DAY_OF_QUARTER),
            is(90));
        assertThat(
            PlainDate.of(2014, 111).get(DAY_OF_QUARTER),
            is(21));
        assertThat(
            PlainDate.of(2014, 5, 4).get(DAY_OF_QUARTER),
            is(34));
        assertThat(
            PlainDate.of(2014, 6, 30).get(DAY_OF_QUARTER),
            is(91));
        assertThat(
            PlainDate.of(2014, 9, 30).get(DAY_OF_QUARTER),
            is(92));
        assertThat(
            PlainDate.of(2014, 12, 31).get(DAY_OF_QUARTER),
            is(92));
    }

}