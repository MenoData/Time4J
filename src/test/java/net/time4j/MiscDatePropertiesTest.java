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
    public void getMinimumCalendarDate() {
        assertThat(
            PlainDate.of(2012, 2, 28).getMinimum(CALENDAR_DATE),
            is(PlainDate.MIN));
    }

    @Test
    public void getMaximumCalendarDate() {
        assertThat(
            PlainDate.of(2012, 2, 28).getMaximum(CALENDAR_DATE),
            is(PlainDate.MAX));
    }

    @Test
    public void isValidCalendarDate() {
        PlainDate any = PlainDate.of(2000, 1);
        assertThat(
            any.isValid(CALENDAR_DATE, PlainDate.MAX),
            is(true));
        assertThat(
            any.isValid(CALENDAR_DATE, PlainDate.MIN),
            is(true));
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
    public void getMinimumYear() {
        assertThat(
            PlainDate.of(2012, 2, 28).getMinimum(YEAR),
            is(-999999999));
    }

    @Test
    public void getMaximumYear() {
        assertThat(
            PlainDate.of(2012, 2, 28).getMaximum(YEAR),
            is(999999999));
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

    @Test
    public void withYear2() {
        PlainDate date = PlainDate.of(2000, 2, 29);
        assertThat(
            date.with(YEAR, 999999999),
            is(PlainDate.of(999999999, 2, 28)));
    }

    @Test
    public void withYear3() {
        PlainDate date = PlainDate.of(2000, 2, 29);
        assertThat(
            date.with(YEAR, -999999999),
            is(PlainDate.of(-999999999, 2, 28)));
    }

    @Test(expected=NullPointerException.class)
    public void withYear4() {
        PlainDate.of(2000, 1).with(YEAR, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withYear5() {
        PlainDate.of(2000, 1).with(YEAR, -1000000000);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withYear6() {
        PlainDate.of(2000, 1).with(YEAR, 1000000000);
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
    public void getYearOfWeekdate3() {
        assertThat(
            PlainDate.of(1996, 12, 31).get(YEAR_OF_WEEKDATE),
            is(1997));
    }

    @Test
    public void getMinimumYearOfWeekdate() {
        assertThat(
            PlainDate.of(2012, 2, 28).getMinimum(YEAR_OF_WEEKDATE),
            is(-999999999));
    }

    @Test
    public void getMaximumYearOfWeekdate() {
        assertThat(
            PlainDate.of(2012, 2, 28).getMaximum(YEAR_OF_WEEKDATE),
            is(999999999));
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

    @Test
    public void withYearOfWeekdate2() {
        PlainDate date = PlainDate.of(1996, 12, 31); // 1997-W01-2
        assertThat(
            date.with(YEAR_OF_WEEKDATE, 1996),
            is(PlainDate.of(1996, 1, 2))); // 1996-W01-2
    }

    @Test(expected=NullPointerException.class)
    public void withYearOfWeekdate3() {
        PlainDate.of(2000, 1).with(YEAR_OF_WEEKDATE, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withYearOfWeekdate4() {
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
    public void getMinimumQuarterOfYear() {
        assertThat(
            PlainDate.of(2012, 2, 28).getMinimum(QUARTER_OF_YEAR),
            is(Quarter.Q1));
    }

    @Test
    public void getMaximumQuarterOfYear() {
        assertThat(
            PlainDate.of(2012, 2, 28).getMaximum(QUARTER_OF_YEAR),
            is(Quarter.Q4));
    }

    @Test
    public void isValidQuarterOfYear() {
        assertThat(
            PlainDate.of(2014, 3, 31).isValid(QUARTER_OF_YEAR, Quarter.Q1),
            is(true));
        assertThat(
            PlainDate.of(2014, 3, 31).isValid(QUARTER_OF_YEAR, null),
            is(false));
    }

    @Test
    public void withQuarterOfYear1() {
        assertThat(
            PlainDate.of(2014, 3, 31).with(QUARTER_OF_YEAR, Quarter.Q4),
            is(PlainDate.of(2014, 12, 31)));
    }

    @Test(expected=NullPointerException.class)
    public void withQuarterOfYear2() {
        PlainDate.of(2000, 1).with(QUARTER_OF_YEAR, null);
    }

    @Test
    public void getMonthOfYear() {
        assertThat(
            PlainDate.of(2014, 111).get(MONTH_OF_YEAR),
            is(Month.APRIL));
    }

    @Test
    public void getMinimumMonthOfYear() {
        assertThat(
            PlainDate.of(2012, 2, 28).getMinimum(MONTH_OF_YEAR),
            is(Month.JANUARY));
    }

    @Test
    public void getMaximumMonthOfYear() {
        assertThat(
            PlainDate.of(2012, 2, 28).getMaximum(MONTH_OF_YEAR),
            is(Month.DECEMBER));
    }

    @Test
    public void isValidMonthOfYear() {
        assertThat(
            PlainDate.of(2014, 3, 31).isValid(MONTH_OF_YEAR, Month.APRIL),
            is(true));
        assertThat(
            PlainDate.of(2014, 3, 31).isValid(MONTH_OF_YEAR, null),
            is(false));
    }

    @Test
    public void withMonthOfYear1() {
        assertThat(
            PlainDate.of(2014, 3, 31).with(MONTH_OF_YEAR, Month.APRIL),
            is(PlainDate.of(2014, 4, 30)));
        assertThat(
            PlainDate.of(2012, 3, 31).with(MONTH_OF_YEAR, Month.FEBRUARY),
            is(PlainDate.of(2012, 2, 29)));
    }

    @Test(expected=NullPointerException.class)
    public void withMonthOfYear2() {
        PlainDate.of(2000, 1).with(MONTH_OF_YEAR, null);
    }

    @Test
    public void getMonthAsNumber() {
        assertThat(
            PlainDate.of(2014, 111).get(MONTH_AS_NUMBER),
            is(4));
    }

    @Test
    public void getMinimumMonthAsNumber() {
        assertThat(
            PlainDate.of(2012, 2, 28).getMinimum(MONTH_AS_NUMBER),
            is(1));
    }

    @Test
    public void getMaximumMonthAsNumber() {
        assertThat(
            PlainDate.of(2012, 2, 28).getMaximum(MONTH_AS_NUMBER),
            is(12));
    }

    @Test
    public void isValidMonthAsNumber() {
        assertThat(
            PlainDate.of(2014, 3, 31).isValid(MONTH_AS_NUMBER, 1),
            is(true));
        assertThat(
            PlainDate.of(2014, 3, 31).isValid(MONTH_AS_NUMBER, 12),
            is(true));
        assertThat(
            PlainDate.of(2014, 3, 31).isValid(MONTH_AS_NUMBER, null),
            is(false));
        assertThat(
            PlainDate.of(2014, 3, 31).isValid(MONTH_AS_NUMBER, 0),
            is(false));
        assertThat(
            PlainDate.of(2014, 3, 31).isValid(MONTH_AS_NUMBER, 13),
            is(false));
    }

    @Test
    public void withMonthAsNumber1() {
        assertThat(
            PlainDate.of(2014, 3, 31).with(MONTH_AS_NUMBER, 11),
            is(PlainDate.of(2014, 11, 30)));
    }

    @Test(expected=NullPointerException.class)
    public void withMonthAsNumber2() {
        PlainDate.of(2000, 1).with(MONTH_AS_NUMBER, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMonthAsNumber3() {
        PlainDate.of(2000, 1).with(MONTH_AS_NUMBER, 0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMonthAsNumber4() {
        PlainDate.of(2000, 1).with(MONTH_AS_NUMBER, 13);
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
    public void getMinimumWeekdayInMonth() {
        assertThat(
            PlainDate.of(2012, 2, 28).getMinimum(WEEKDAY_IN_MONTH),
            is(1));
    }

    @Test
    public void getMaximumWeekdayInMonth() {
        assertThat(
            PlainDate.of(2012, 2, 28).getMaximum(WEEKDAY_IN_MONTH),
            is(4));
        assertThat(
            PlainDate.of(2012, 2, 29).getMaximum(WEEKDAY_IN_MONTH),
            is(5));
    }

    @Test
    public void isValidWeekdayInMonth() {
        assertThat(
            PlainDate.of(2012, 2, 28).isValid(WEEKDAY_IN_MONTH, 4),
            is(true));
        assertThat(
            PlainDate.of(2012, 2, 28).isValid(WEEKDAY_IN_MONTH, 5),
            is(false));
        assertThat(
            PlainDate.of(2012, 2, 28).isValid(WEEKDAY_IN_MONTH, null),
            is(false));
    }

    @Test
    public void withWeekdayInMonth1() {
        assertThat(
            PlainDate.of(2012, 2, 28).with(WEEKDAY_IN_MONTH, 1),
            is(PlainDate.of(2012, 2, 7)));
    }

    @Test(expected=NullPointerException.class)
    public void withWeekdayInMonth2() {
        PlainDate.of(2012, 2, 28).with(WEEKDAY_IN_MONTH, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withWeekdayInMonth3() {
        PlainDate.of(2012, 2, 28).with(WEEKDAY_IN_MONTH, 5);
    }

    @Test
    public void getDayOfMonth() {
        assertThat(
            PlainDate.of(2014, 111).get(DAY_OF_MONTH),
            is(21));
    }

    @Test
    public void getMinimumDayOfMonth() {
        assertThat(
            PlainDate.of(2014, 12, 31).getMinimum(DAY_OF_MONTH),
            is(1));
    }

    @Test
    public void getMaximumDayOfMonth() {
        assertThat(
            PlainDate.of(2013, 1, 7).getMaximum(DAY_OF_MONTH),
            is(31));
        assertThat(
            PlainDate.of(2012, 2, 7).getMaximum(DAY_OF_MONTH),
            is(29));
        assertThat(
            PlainDate.of(2013, 2, 7).getMaximum(DAY_OF_MONTH),
            is(28));
        assertThat(
            PlainDate.of(2013, 3, 7).getMaximum(DAY_OF_MONTH),
            is(31));
        assertThat(
            PlainDate.of(2012, 4, 7).getMaximum(DAY_OF_MONTH),
            is(30));
        assertThat(
            PlainDate.of(2013, 5, 7).getMaximum(DAY_OF_MONTH),
            is(31));
        assertThat(
            PlainDate.of(2012, 6, 7).getMaximum(DAY_OF_MONTH),
            is(30));
        assertThat(
            PlainDate.of(2013, 7, 7).getMaximum(DAY_OF_MONTH),
            is(31));
        assertThat(
            PlainDate.of(2013, 8, 7).getMaximum(DAY_OF_MONTH),
            is(31));
        assertThat(
            PlainDate.of(2012, 9, 7).getMaximum(DAY_OF_MONTH),
            is(30));
        assertThat(
            PlainDate.of(2013, 10, 7).getMaximum(DAY_OF_MONTH),
            is(31));
        assertThat(
            PlainDate.of(2012, 11, 7).getMaximum(DAY_OF_MONTH),
            is(30));
        assertThat(
            PlainDate.of(2013, 12, 7).getMaximum(DAY_OF_MONTH),
            is(31));
    }

    @Test
    public void isValidDayOfMonth() {
        assertThat(
            PlainDate.of(2012, 2, 28).isValid(DAY_OF_MONTH, 29),
            is(true));
        assertThat(
            PlainDate.of(2012, 2, 28).isValid(DAY_OF_MONTH, 30),
            is(false));
        assertThat(
            PlainDate.of(2012, 2, 28).isValid(DAY_OF_MONTH, null),
            is(false));
    }

    @Test
    public void withDayOfMonth1() {
        assertThat(
            PlainDate.of(2012, 2, 28).with(DAY_OF_MONTH, 29),
            is(PlainDate.of(2012, 2, 29)));
        assertThat(
            PlainDate.of(2012, 2, 28).with(DAY_OF_MONTH, 1),
            is(PlainDate.of(2012, 2, 1)));
    }

    @Test(expected=NullPointerException.class)
    public void withDayOfMonth2() {
        PlainDate.of(2012, 2, 28).with(DAY_OF_MONTH, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDayOfMonth3() {
        PlainDate.of(2012, 2, 28).with(DAY_OF_MONTH, 0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDayOfMonth4() {
        PlainDate.of(2012, 2, 28).with(DAY_OF_MONTH, 30);
    }

    @Test
    public void getDayOfYear() {
        assertThat(
            PlainDate.of(2014, 111).get(DAY_OF_YEAR),
            is(111));
    }

    @Test
    public void getMinimumDayOfYear() {
        assertThat(
            PlainDate.of(2014, 12, 31).getMinimum(DAY_OF_YEAR),
            is(1));
    }

    @Test
    public void getMaximumDayOfYear() {
        assertThat(
            PlainDate.of(2012, 4, 7).getMaximum(DAY_OF_YEAR),
            is(366));
        assertThat(
            PlainDate.of(2013, 4, 7).getMaximum(DAY_OF_YEAR),
            is(365));
    }

    @Test
    public void isValidDayOfYear() {
        assertThat(
            PlainDate.of(2012, 2, 28).isValid(DAY_OF_YEAR, 366),
            is(true));
        assertThat(
            PlainDate.of(2014, 1, 1).isValid(DAY_OF_YEAR, 366),
            is(false));
        assertThat(
            PlainDate.of(2012, 2, 28).isValid(DAY_OF_YEAR, 367),
            is(false));
        assertThat(
            PlainDate.of(2012, 2, 28).isValid(DAY_OF_YEAR, null),
            is(false));
    }

    @Test
    public void withDayOfYear1() {
        assertThat(
            PlainDate.of(2012, 2, 28).with(DAY_OF_YEAR, 366),
            is(PlainDate.of(2012, 12, 31)));
        assertThat(
            PlainDate.of(2012, 2, 28).with(DAY_OF_YEAR, 1),
            is(PlainDate.of(2012, 1, 1)));
    }

    @Test(expected=NullPointerException.class)
    public void withDayOfYear2() {
        PlainDate.of(2012, 2, 28).with(DAY_OF_YEAR, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDayOfYear3() {
        PlainDate.of(2012, 2, 28).with(DAY_OF_YEAR, 0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDayOfYear4() {
        PlainDate.of(2012, 2, 28).with(DAY_OF_YEAR, 367);
    }

    @Test
    public void getDayOfWeek() {
        assertThat(
            PlainDate.of(2014, 4, 21).get(DAY_OF_WEEK),
            is(Weekday.MONDAY));
    }

    @Test
    public void getMinimumDayOfWeek() {
        assertThat(
            PlainDate.of(2014, 12, 31).getMinimum(DAY_OF_WEEK),
            is(Weekday.MONDAY));
    }

    @Test
    public void getMaximumDayOfWeek() {
        assertThat(
            PlainDate.of(2012, 12, 31).getMaximum(DAY_OF_WEEK),
            is(Weekday.SUNDAY));
    }

    @Test
    public void isValidDayOfWeek() {
        assertThat(
            PlainDate.of(2014, 3, 31).isValid(DAY_OF_WEEK, Weekday.FRIDAY),
            is(true));
        assertThat(
            PlainDate.of(2014, 3, 31).isValid(DAY_OF_WEEK, null),
            is(false));
    }

    @Test
    public void withDayOfWeek1() {
        assertThat(
            PlainDate.of(2012, 2, 29).with(DAY_OF_WEEK, Weekday.MONDAY),
            is(PlainDate.of(2012, 2, 27)));
        assertThat(
            PlainDate.of(2012, 2, 29).with(DAY_OF_WEEK, Weekday.TUESDAY),
            is(PlainDate.of(2012, 2, 28)));
        assertThat(
            PlainDate.of(2012, 2, 29).with(DAY_OF_WEEK, Weekday.WEDNESDAY),
            is(PlainDate.of(2012, 2, 29)));
        assertThat(
            PlainDate.of(2012, 2, 29).with(DAY_OF_WEEK, Weekday.THURSDAY),
            is(PlainDate.of(2012, 3, 1)));
        assertThat(
            PlainDate.of(2012, 2, 29).with(DAY_OF_WEEK, Weekday.FRIDAY),
            is(PlainDate.of(2012, 3, 2)));
        assertThat(
            PlainDate.of(2012, 2, 29).with(DAY_OF_WEEK, Weekday.SATURDAY),
            is(PlainDate.of(2012, 3, 3)));
        assertThat(
            PlainDate.of(2012, 2, 29).with(DAY_OF_WEEK, Weekday.SUNDAY),
            is(PlainDate.of(2012, 3, 4)));
    }

    @Test
    public void withDayOfWeek2() {
        assertThat(
            PlainDate.of(2014, 1, 1).with(DAY_OF_WEEK, Weekday.MONDAY),
            is(PlainDate.of(2013, 12, 30)));
        assertThat(
            PlainDate.of(2012, 12, 31).with(DAY_OF_WEEK, Weekday.SUNDAY),
            is(PlainDate.of(2013, 1, 6)));
    }

    @Test(expected=NullPointerException.class)
    public void withDayOfWeek3() {
        PlainDate.of(2000, 1).with(DAY_OF_WEEK, null);
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

    @Test
    public void getMinimumDayOfQuarter() {
        assertThat(
            PlainDate.of(2014, 12, 31).getMinimum(DAY_OF_QUARTER),
            is(1));
    }

    @Test
    public void getMaximumDayOfQuarter() {
        assertThat(
            PlainDate.of(2012, 1, 1).getMaximum(DAY_OF_QUARTER),
            is(91));
        assertThat(
            PlainDate.of(2014, 1, 1).getMaximum(DAY_OF_QUARTER),
            is(90));
        assertThat(
            PlainDate.of(2014, 6, 30).getMaximum(DAY_OF_QUARTER),
            is(91));
        assertThat(
            PlainDate.of(2014, 9, 30).getMaximum(DAY_OF_QUARTER),
            is(92));
        assertThat(
            PlainDate.of(2014, 12, 31).getMaximum(DAY_OF_QUARTER),
            is(92));
    }

    @Test
    public void isValidDayOfQuarter() {
        assertThat(
            PlainDate.of(2012, 2, 28).isValid(DAY_OF_QUARTER, 91),
            is(true));
        assertThat(
            PlainDate.of(2011, 2, 28).isValid(DAY_OF_QUARTER, 91),
            is(false));
        assertThat(
            PlainDate.of(2012, 7, 1).isValid(DAY_OF_QUARTER, 92),
            is(true));
        assertThat(
            PlainDate.of(2012, 2, 28).isValid(DAY_OF_QUARTER, null),
            is(false));
    }

    @Test
    public void withDayOfQuarter1() {
        assertThat(
            PlainDate.of(2012, 2, 28).with(DAY_OF_QUARTER, 91),
            is(PlainDate.of(2012, 3, 31)));
        assertThat(
            PlainDate.of(2012, 2, 28).with(DAY_OF_QUARTER, 1),
            is(PlainDate.of(2012, 1, 1)));
        assertThat(
            PlainDate.of(2012, 7, 1).with(DAY_OF_QUARTER, 92),
            is(PlainDate.of(2012, 9, 30)));
    }

    @Test(expected=NullPointerException.class)
    public void withDayOfQuarter2() {
        PlainDate.of(2012, 2, 28).with(DAY_OF_QUARTER, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDayOfQuarter3() {
        PlainDate.of(2012, 2, 28).with(DAY_OF_QUARTER, 92);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDayOfQuarter4() {
        PlainDate.of(2012, 2, 28).with(DAY_OF_QUARTER, 0);
    }

}