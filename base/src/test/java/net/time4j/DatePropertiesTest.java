package net.time4j;

import net.time4j.engine.ChronoException;
import net.time4j.engine.Chronology;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;

import static net.time4j.PlainDate.*;
import static net.time4j.PlainTime.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class DatePropertiesTest {

    @Test
    public void registrationOfCalendarDate() {
        assertThat(
            Moment.axis().isRegistered(CALENDAR_DATE),
            is(false));
        assertThat(
            PlainTime.axis().isRegistered(CALENDAR_DATE),
            is(false));
        assertThat(
            PlainDate.axis().isRegistered(CALENDAR_DATE),
            is(true));
        assertThat(
            PlainTimestamp.axis().isRegistered(CALENDAR_DATE),
            is(true));
    }

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
    public void axis() {
        assertThat(
            (PlainDate.axis() == Chronology.lookup(PlainDate.class)),
            is(true));
    }

    @Test
    public void containsCalendarDate() {
        PlainDate any = PlainDate.of(2000, 1);
        assertThat(any.contains(CALENDAR_DATE), is(true));
    }

    @Test
    public void getCalendarDate() {
        PlainDate any = PlainDate.of(2000, 1);
        assertThat(
            (any.get(CALENDAR_DATE) == any),
            is(true));
    }

    @Test
    public void getBaseUnitCalendarDate() {
        IsoUnit unit = CalendarUnit.DAYS;
        assertThat(
            PlainDate.axis().getBaseUnit(CALENDAR_DATE),
            is(unit));
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
    public void withCalendarDate() {
        PlainDate any = PlainDate.of(2000, 1);
        PlainDate value = PlainDate.of(2014, 4, 5);
        assertThat(
            any.with(CALENDAR_DATE, value),
            is(value));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withCalendarDateNull() {
        PlainDate.of(2000, 1).with(CALENDAR_DATE, null);
    }

    @Test
    public void containsYear() {
        PlainDate any = PlainDate.of(2000, 1);
        assertThat(any.contains(YEAR), is(true));
    }

    @Test
    public void getYear() {
        assertThat(
            PlainDate.of(2000, 1).get(YEAR),
            is(2000));
    }

    @Test
    public void getBaseUnitYear() {
        IsoUnit unit = CalendarUnit.YEARS;
        assertThat(
            PlainDate.axis().getBaseUnit(YEAR),
            is(unit));
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
    public void withYear() {
        PlainDate date = PlainDate.of(2000, 2, 29);
        assertThat(
            date.with(YEAR, 2014),
            is(PlainDate.of(2014, 2, 28)));
    }

    @Test
    public void withYearMax() {
        PlainDate date = PlainDate.of(2000, 2, 29);
        assertThat(
            date.with(YEAR, 999999999),
            is(PlainDate.of(999999999, 2, 28)));
    }

    @Test
    public void withYearMin() {
        PlainDate date = PlainDate.of(2000, 2, 29);
        assertThat(
            date.with(YEAR, -999999999),
            is(PlainDate.of(-999999999, 2, 28)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withYearNull() {
        PlainDate.of(2000, 1).with(YEAR, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withYear1000000000() {
        PlainDate.of(2000, 1).with(YEAR, -1000000000);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withYear_1000000000() {
        PlainDate.of(2000, 1).with(YEAR, 1000000000);
    }

    @Test
    public void containsYearOfWeekdate() {
        PlainDate any = PlainDate.of(2000, 1);
        assertThat(any.contains(YEAR_OF_WEEKDATE), is(true));
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
    public void getBaseUnitYearOfWeekdate() {
        assertThat(
            PlainDate.axis().getBaseUnit(YEAR_OF_WEEKDATE),
            is(CalendarUnit.weekBasedYears()));
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

    @Test(expected=IllegalArgumentException.class)
    public void withYearOfWeekdateNull() {
        PlainDate.of(2000, 1).with(YEAR_OF_WEEKDATE, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withYearOfWeekdate1234567890() {
        PlainDate.of(2000, 1).with(YEAR_OF_WEEKDATE, 1234567890);
    }

    @Test
    public void containsQuarterOfYear() {
        PlainDate any = PlainDate.of(2000, 1);
        assertThat(any.contains(QUARTER_OF_YEAR), is(true));
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
    public void getBaseUnitQuarterOfYear() {
        IsoUnit unit = CalendarUnit.QUARTERS;
        assertThat(
            PlainDate.axis().getBaseUnit(QUARTER_OF_YEAR),
            is(unit));
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
    public void withQuarterOfYear() {
        assertThat(
            PlainDate.of(2014, 3, 31).with(QUARTER_OF_YEAR, Quarter.Q4),
            is(PlainDate.of(2014, 12, 31)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withQuarterOfYearNull() {
        PlainDate.of(2000, 1).with(QUARTER_OF_YEAR, null);
    }

    @Test
    public void containsMonthOfYear() {
        PlainDate any = PlainDate.of(2000, 1);
        assertThat(any.contains(MONTH_OF_YEAR), is(true));
    }

    @Test
    public void getMonthOfYear() {
        assertThat(
            PlainDate.of(2014, 111).get(MONTH_OF_YEAR),
            is(Month.APRIL));
    }

    @Test
    public void getBaseUnitMonthOfYear() {
        IsoUnit unit = CalendarUnit.MONTHS;
        assertThat(
            PlainDate.axis().getBaseUnit(MONTH_OF_YEAR),
            is(unit));
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
    public void withMonthOfYear() {
        assertThat(
            PlainDate.of(2014, 3, 31).with(MONTH_OF_YEAR, Month.APRIL),
            is(PlainDate.of(2014, 4, 30)));
        assertThat(
            PlainDate.of(2012, 3, 31).with(MONTH_OF_YEAR, Month.FEBRUARY),
            is(PlainDate.of(2012, 2, 29)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMonthOfYearNull() {
        PlainDate.of(2000, 1).with(MONTH_OF_YEAR, null);
    }

    @Test
    public void containsMonthAsNumber() {
        PlainDate any = PlainDate.of(2000, 1);
        assertThat(any.contains(MONTH_AS_NUMBER), is(true));
    }

    @Test
    public void getMonthAsNumber() {
        assertThat(
            PlainDate.of(2014, 111).get(MONTH_AS_NUMBER),
            is(4));
    }

    @Test
    public void getBaseUnitMonthAsNumber() {
        IsoUnit unit = CalendarUnit.MONTHS;
        assertThat(
            PlainDate.axis().getBaseUnit(MONTH_AS_NUMBER),
            is(unit));
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
    public void withMonthAsNumber() {
        assertThat(
            PlainDate.of(2014, 3, 31).with(MONTH_AS_NUMBER, 11),
            is(PlainDate.of(2014, 11, 30)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMonthAsNumberNull() {
        PlainDate.of(2000, 1).with(MONTH_AS_NUMBER, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMonthAsNumber0() {
        PlainDate.of(2000, 1).with(MONTH_AS_NUMBER, 0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withMonthAsNumber13() {
        PlainDate.of(2000, 1).with(MONTH_AS_NUMBER, 13);
    }

    @Test
    public void containsWeekdayInMonth() {
        PlainDate any = PlainDate.of(2000, 1);
        assertThat(any.contains(WEEKDAY_IN_MONTH), is(true));
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
    public void getBaseUnitWeekdayInMonth() {
        IsoUnit unit = CalendarUnit.WEEKS;
        assertThat(
            PlainDate.axis().getBaseUnit(WEEKDAY_IN_MONTH),
            is(unit));
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
    public void withWeekdayInMonth() {
        assertThat(
            PlainDate.of(2012, 2, 28).with(WEEKDAY_IN_MONTH, 1),
            is(PlainDate.of(2012, 2, 7)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withWeekdayInMonthNull() {
        PlainDate.of(2012, 2, 28).with(WEEKDAY_IN_MONTH, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withWeekdayInMonth5Feb() {
        PlainDate.of(2012, 2, 28).with(WEEKDAY_IN_MONTH, 5);
    }

    @Test
    public void containsDayOfMonth() {
        PlainDate any = PlainDate.of(2000, 1);
        assertThat(any.contains(DAY_OF_MONTH), is(true));
    }

    @Test
    public void getDayOfMonth() {
        assertThat(
            PlainDate.of(2014, 111).get(DAY_OF_MONTH),
            is(21));
    }

    @Test
    public void getBaseUnitDayOfMonth() {
        IsoUnit unit = CalendarUnit.DAYS;
        assertThat(
            PlainDate.axis().getBaseUnit(DAY_OF_MONTH),
            is(unit));
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
    public void withDayOfMonth() {
        assertThat(
            PlainDate.of(2012, 2, 28).with(DAY_OF_MONTH, 29),
            is(PlainDate.of(2012, 2, 29)));
        assertThat(
            PlainDate.of(2012, 2, 28).with(DAY_OF_MONTH, 1),
            is(PlainDate.of(2012, 2, 1)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDayOfMonthNull() {
        PlainDate.of(2012, 2, 28).with(DAY_OF_MONTH, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDayOfMonth0() {
        PlainDate.of(2012, 2, 28).with(DAY_OF_MONTH, 0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDayOfMonth30Feb() {
        PlainDate.of(2012, 2, 28).with(DAY_OF_MONTH, 30);
    }

    @Test
    public void containsDayOfYear() {
        PlainDate any = PlainDate.of(2000, 1);
        assertThat(any.contains(DAY_OF_YEAR), is(true));
    }

    @Test
    public void getDayOfYear() {
        assertThat(
            PlainDate.of(2014, 111).get(DAY_OF_YEAR),
            is(111));
    }

    @Test
    public void getBaseUnitDayOfYear() {
        IsoUnit unit = CalendarUnit.DAYS;
        assertThat(
            PlainDate.axis().getBaseUnit(DAY_OF_YEAR),
            is(unit));
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
    public void withDayOfYear() {
        assertThat(
            PlainDate.of(2012, 2, 28).with(DAY_OF_YEAR, 366),
            is(PlainDate.of(2012, 12, 31)));
        assertThat(
            PlainDate.of(2012, 2, 28).with(DAY_OF_YEAR, 1),
            is(PlainDate.of(2012, 1, 1)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDayOfYearNull() {
        PlainDate.of(2012, 2, 28).with(DAY_OF_YEAR, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDayOfYear0() {
        PlainDate.of(2012, 2, 28).with(DAY_OF_YEAR, 0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDayOfYear367() {
        PlainDate.of(2012, 2, 28).with(DAY_OF_YEAR, 367);
    }

    @Test
    public void containsDayOfWeek() {
        PlainDate any = PlainDate.of(2000, 1);
        assertThat(any.contains(DAY_OF_WEEK), is(true));
    }

    @Test
    public void getDayOfWeek() {
        assertThat(
            PlainDate.of(2014, 4, 21).get(DAY_OF_WEEK),
            is(Weekday.MONDAY));
    }

    @Test
    public void getBaseUnitDayOfWeek() {
        IsoUnit unit = CalendarUnit.DAYS;
        assertThat(
            PlainDate.axis().getBaseUnit(DAY_OF_WEEK),
            is(unit));
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
    public void withDayOfWeek() {
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
        assertThat(
            PlainDate.of(2012, 2, 29).with(DAY_OF_WEEK, Weekday.SUNDAY).with(DAY_OF_WEEK, Weekday.SUNDAY),
            is(PlainDate.of(2012, 3, 4)));
        assertThat(
            PlainDate.of(2012, 2, 29).with(DAY_OF_WEEK, Weekday.SUNDAY).with(DAY_OF_WEEK, Weekday.MONDAY),
            is(PlainDate.of(2012, 2, 27)));
    }

    @Test
    public void withDayOfWeekAtYearBorder() {
        assertThat(
            PlainDate.of(2014, 1, 1).with(DAY_OF_WEEK, Weekday.MONDAY),
            is(PlainDate.of(2013, 12, 30)));
        assertThat(
            PlainDate.of(2012, 12, 31).with(DAY_OF_WEEK, Weekday.SUNDAY),
            is(PlainDate.of(2013, 1, 6)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDayOfWeekNull() {
        PlainDate.of(2000, 1).with(DAY_OF_WEEK, null);
    }

    @Test
    public void containsDayOfQuarter() {
        PlainDate any = PlainDate.of(2000, 1);
        assertThat(any.contains(DAY_OF_QUARTER), is(true));
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
    public void getBaseUnitDayOfQuarter() {
        IsoUnit unit = CalendarUnit.DAYS;
        assertThat(
            PlainDate.axis().getBaseUnit(DAY_OF_QUARTER),
            is(unit));
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
    public void withDayOfQuarter() {
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

    @Test(expected=IllegalArgumentException.class)
    public void withDayOfQuarterNull() {
        PlainDate.of(2012, 2, 28).with(DAY_OF_QUARTER, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDayOfQuarter92Q1() {
        PlainDate.of(2012, 2, 28).with(DAY_OF_QUARTER, 92);
    }

    @Test(expected=IllegalArgumentException.class)
    public void withDayOfQuarter0() {
        PlainDate.of(2012, 2, 28).with(DAY_OF_QUARTER, 0);
    }

    @Test
    public void containsPrecision() {
        assertThat(
            PlainDate.of(2014, 4, 21).contains(PRECISION),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getPrecision() {
        PlainDate.of(2014, 4, 21).get(PRECISION);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumPrecision() {
        PlainDate.of(2014, 4, 21).getMinimum(PRECISION);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumPrecision() {
        PlainDate.of(2014, 4, 21).getMaximum(PRECISION);
    }

    @Test
    public void isValidPrecision() {
        assertThat(
            PlainDate.of(2014, 4, 21).isValid(PRECISION, ClockUnit.HOURS),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withPrecision() {
        PlainDate.of(2014, 4, 21).with(PRECISION, ClockUnit.HOURS);
    }

    @Test
    public void containsDecimalHour() {
        assertThat(
            PlainDate.of(2014, 4, 21).contains(DECIMAL_HOUR),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getDecimalHour() {
        PlainDate.of(2014, 4, 21).get(DECIMAL_HOUR);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumDecimalHour() {
        PlainDate.of(2014, 4, 21).getMinimum(DECIMAL_HOUR);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumDecimalHour() {
        PlainDate.of(2014, 4, 21).getMaximum(DECIMAL_HOUR);
    }

    @Test
    public void isValidDecimalHour() {
        assertThat(
            PlainDate.of(2014, 4, 21).isValid(DECIMAL_HOUR, BigDecimal.ZERO),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withDecimalHour() {
        PlainDate.of(2014, 4, 21).with(DECIMAL_HOUR, BigDecimal.ZERO);
    }

    @Test
    public void containsDecimalMinute() {
        assertThat(
            PlainDate.of(2014, 4, 21).contains(DECIMAL_MINUTE),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getDecimalMinute() {
        PlainDate.of(2014, 4, 21).get(DECIMAL_MINUTE);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumDecimalMinute() {
        PlainDate.of(2014, 4, 21).getMinimum(DECIMAL_MINUTE);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumDecimalMinute() {
        PlainDate.of(2014, 4, 21).getMaximum(DECIMAL_MINUTE);
    }

    @Test
    public void isValidDecimalMinute() {
        assertThat(
            PlainDate.of(2014, 4, 21).isValid(DECIMAL_MINUTE, BigDecimal.ZERO),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withDecimalMinute() {
        PlainDate.of(2014, 4, 21).with(DECIMAL_MINUTE, BigDecimal.ZERO);
    }

    @Test
    public void containsDecimalSecond() {
        assertThat(
            PlainDate.of(2014, 4, 21).contains(DECIMAL_SECOND),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getDecimalSecond() {
        PlainDate.of(2014, 4, 21).get(DECIMAL_SECOND);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumDecimalSecond() {
        PlainDate.of(2014, 4, 21).getMinimum(DECIMAL_SECOND);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumDecimalSecond() {
        PlainDate.of(2014, 4, 21).getMaximum(DECIMAL_SECOND);
    }

    @Test
    public void isValidDecimalSecond() {
        assertThat(
            PlainDate.of(2014, 4, 21).isValid(DECIMAL_SECOND, BigDecimal.ZERO),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withDecimalSecond() {
        PlainDate.of(2014, 4, 21).with(DECIMAL_SECOND, BigDecimal.ZERO);
    }

    @Test
    public void containsWallTime() {
        assertThat(
            PlainDate.of(2014, 4, 21).contains(WALL_TIME),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getWallTime() {
        PlainDate.of(2014, 4, 21).get(WALL_TIME);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumWallTime() {
        PlainDate.of(2014, 4, 21).getMinimum(WALL_TIME);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumWallTime() {
        PlainDate.of(2014, 4, 21).getMaximum(WALL_TIME);
    }

    @Test
    public void isValidWallTime() {
        assertThat(
            PlainDate.of(2014, 4, 21).isValid(WALL_TIME, PlainTime.of(0)),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withWallTime() {
        PlainDate.of(2014, 4, 21).with(WALL_TIME, PlainTime.of(0));
    }

    @Test
    public void containsAmPm() {
        assertThat(
            PlainDate.of(2014, 4, 21).contains(AM_PM_OF_DAY),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getAmPm() {
        PlainDate.of(2014, 4, 21).get(AM_PM_OF_DAY);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumAmPm() {
        PlainDate.of(2014, 4, 21).getMinimum(AM_PM_OF_DAY);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumAmPm() {
        PlainDate.of(2014, 4, 21).getMaximum(AM_PM_OF_DAY);
    }

    @Test
    public void isValidAmPm() {
        assertThat(
            PlainDate.of(2014, 4, 21).isValid(AM_PM_OF_DAY, Meridiem.AM),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withAmPm() {
        PlainDate.of(2014, 4, 21).with(AM_PM_OF_DAY, Meridiem.AM);
    }

    @Test
    public void containsClockHourOfAmPm() {
        assertThat(
            PlainDate.of(2014, 4, 21).contains(CLOCK_HOUR_OF_AMPM),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getClockHourOfAmPm() {
        PlainDate.of(2014, 4, 21).get(CLOCK_HOUR_OF_AMPM);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumClockHourOfAmPm() {
        PlainDate.of(2014, 4, 21).getMinimum(CLOCK_HOUR_OF_AMPM);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumClockHourOfAmPm() {
        PlainDate.of(2014, 4, 21).getMaximum(CLOCK_HOUR_OF_AMPM);
    }

    @Test
    public void isValidClockHourOfAmPm() {
        assertThat(
            PlainDate.of(2014, 4, 21).isValid(CLOCK_HOUR_OF_AMPM, 1),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withClockHourAmPm() {
        PlainDate.of(2014, 4, 21).with(CLOCK_HOUR_OF_AMPM, 1);
    }

    @Test
    public void containsDigitalHourOfAmPm() {
        assertThat(
            PlainDate.of(2014, 4, 21).contains(DIGITAL_HOUR_OF_AMPM),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getDigitalHourOfAmPm() {
        PlainDate.of(2014, 4, 21).get(DIGITAL_HOUR_OF_AMPM);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumDigitalHourOfAmPm() {
        PlainDate.of(2014, 4, 21).getMinimum(DIGITAL_HOUR_OF_AMPM);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumDigitalHourOfAmPm() {
        PlainDate.of(2014, 4, 21).getMaximum(DIGITAL_HOUR_OF_AMPM);
    }

    @Test
    public void isValidDigitalHourOfAmPm() {
        assertThat(
            PlainDate.of(2014, 4, 21).isValid(DIGITAL_HOUR_OF_AMPM, 1),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withDigitalHourOfAmPm() {
        PlainDate.of(2014, 4, 21).with(DIGITAL_HOUR_OF_AMPM, 1);
    }

    @Test
    public void containsClockHourOfDay() {
        assertThat(
            PlainDate.of(2014, 4, 21).contains(CLOCK_HOUR_OF_DAY),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getClockHourOfDay() {
        PlainDate.of(2014, 4, 21).get(CLOCK_HOUR_OF_DAY);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumClockHourOfDay() {
        PlainDate.of(2014, 4, 21).getMinimum(CLOCK_HOUR_OF_DAY);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumClockHourOfDay() {
        PlainDate.of(2014, 4, 21).getMaximum(CLOCK_HOUR_OF_DAY);
    }

    @Test
    public void isValidClockHourOfDay() {
        assertThat(
            PlainDate.of(2014, 4, 21).isValid(CLOCK_HOUR_OF_DAY, 1),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withClockHourDay() {
        PlainDate.of(2014, 4, 21).with(CLOCK_HOUR_OF_DAY, 1);
    }

    @Test
    public void containsDigitalHourOfDay() {
        assertThat(
            PlainDate.of(2014, 4, 21).contains(DIGITAL_HOUR_OF_DAY),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getDigitalHourOfDay() {
        PlainDate.of(2014, 4, 21).get(DIGITAL_HOUR_OF_DAY);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumDigitalHourOfDay() {
        PlainDate.of(2014, 4, 21).getMinimum(DIGITAL_HOUR_OF_DAY);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumDigitalHourOfDay() {
        PlainDate.of(2014, 4, 21).getMaximum(DIGITAL_HOUR_OF_DAY);
    }

    @Test
    public void isValidDigitalHourOfDay() {
        assertThat(
            PlainDate.of(2014, 4, 21).isValid(DIGITAL_HOUR_OF_DAY, 1),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withDigitalHourOfDay() {
        PlainDate.of(2014, 4, 21).with(DIGITAL_HOUR_OF_DAY, 1);
    }

    @Test
    public void containshour0To24() {
        assertThat(
            PlainDate.of(2014, 4, 21).contains(HOUR_FROM_0_TO_24),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void gethour0To24() {
        PlainDate.of(2014, 4, 21).get(HOUR_FROM_0_TO_24);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumhour0To24() {
        PlainDate.of(2014, 4, 21).getMinimum(HOUR_FROM_0_TO_24);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumhour0To24() {
        PlainDate.of(2014, 4, 21).getMaximum(HOUR_FROM_0_TO_24);
    }

    @Test
    public void isValidhour0To24() {
        assertThat(
            PlainDate.of(2014, 4, 21).isValid(HOUR_FROM_0_TO_24, 1),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withhour0To24() {
        PlainDate.of(2014, 4, 21).with(HOUR_FROM_0_TO_24, 1);
    }

    @Test
    public void containsMinuteOfHour() {
        assertThat(
            PlainDate.of(2014, 4, 21).contains(MINUTE_OF_HOUR),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getMinuteOfHour() {
        PlainDate.of(2014, 4, 21).get(MINUTE_OF_HOUR);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumMinuteOfHour() {
        PlainDate.of(2014, 4, 21).getMinimum(MINUTE_OF_HOUR);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumMinuteOfHour() {
        PlainDate.of(2014, 4, 21).getMaximum(MINUTE_OF_HOUR);
    }

    @Test
    public void isValidMinuteOfHour() {
        assertThat(
            PlainDate.of(2014, 4, 21).isValid(MINUTE_OF_HOUR, 1),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withMinuteOfHour() {
        PlainDate.of(2014, 4, 21).with(MINUTE_OF_HOUR, 1);
    }

    @Test
    public void containsSecondOfMinute() {
        assertThat(
            PlainDate.of(2014, 4, 21).contains(SECOND_OF_MINUTE),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getSecondOfMinute() {
        PlainDate.of(2014, 4, 21).get(SECOND_OF_MINUTE);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumSecondOfMinute() {
        PlainDate.of(2014, 4, 21).getMinimum(SECOND_OF_MINUTE);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumSecondOfMinute() {
        PlainDate.of(2014, 4, 21).getMaximum(SECOND_OF_MINUTE);
    }

    @Test
    public void isValidSecondOfMinute() {
        assertThat(
            PlainDate.of(2014, 4, 21).isValid(SECOND_OF_MINUTE, 1),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withSecondOfMinute() {
        PlainDate.of(2014, 4, 21).with(SECOND_OF_MINUTE, 1);
    }

    @Test
    public void containsMinuteOfDay() {
        assertThat(
            PlainDate.of(2014, 4, 21).contains(MINUTE_OF_DAY),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getMinuteOfDay() {
        PlainDate.of(2014, 4, 21).get(MINUTE_OF_DAY);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumMinuteOfDay() {
        PlainDate.of(2014, 4, 21).getMinimum(MINUTE_OF_DAY);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumMinuteOfDay() {
        PlainDate.of(2014, 4, 21).getMaximum(MINUTE_OF_DAY);
    }

    @Test
    public void isValidMinuteOfDay() {
        assertThat(
            PlainDate.of(2014, 4, 21).isValid(MINUTE_OF_DAY, 1),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withMinuteOfDay() {
        PlainDate.of(2014, 4, 21).with(MINUTE_OF_DAY, 1);
    }

    @Test
    public void containsSecondOfDay() {
        assertThat(
            PlainDate.of(2014, 4, 21).contains(SECOND_OF_DAY),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getSecondOfDay() {
        PlainDate.of(2014, 4, 21).get(SECOND_OF_DAY);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumSecondOfDay() {
        PlainDate.of(2014, 4, 21).getMinimum(SECOND_OF_DAY);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumSecondOfDay() {
        PlainDate.of(2014, 4, 21).getMaximum(SECOND_OF_DAY);
    }

    @Test
    public void isValidSecondOfDay() {
        assertThat(
            PlainDate.of(2014, 4, 21).isValid(SECOND_OF_DAY, 1),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withSecondOfDay() {
        PlainDate.of(2014, 4, 21).with(SECOND_OF_DAY, 1);
    }

    @Test
    public void containsMilliOfDay() {
        assertThat(
            PlainDate.of(2014, 4, 21).contains(MILLI_OF_DAY),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getMilliOfDay() {
        PlainDate.of(2014, 4, 21).get(MILLI_OF_DAY);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumMilliOfDay() {
        PlainDate.of(2014, 4, 21).getMinimum(MILLI_OF_DAY);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumMilliOfDay() {
        PlainDate.of(2014, 4, 21).getMaximum(MILLI_OF_DAY);
    }

    @Test
    public void isValidMilliOfDay() {
        assertThat(
            PlainDate.of(2014, 4, 21).isValid(MILLI_OF_DAY, 1),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withMilliOfDay() {
        PlainDate.of(2014, 4, 21).with(MILLI_OF_DAY, 1);
    }

    @Test
    public void containsMicroOfDay() {
        assertThat(
            PlainDate.of(2014, 4, 21).contains(MICRO_OF_DAY),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getMicroOfDay() {
        PlainDate.of(2014, 4, 21).get(MICRO_OF_DAY);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumMicroOfDay() {
        PlainDate.of(2014, 4, 21).getMinimum(MICRO_OF_DAY);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumMicroOfDay() {
        PlainDate.of(2014, 4, 21).getMaximum(MICRO_OF_DAY);
    }

    @Test
    public void isValidMicroOfDay() {
        assertThat(
            PlainDate.of(2014, 4, 21).isValid(MICRO_OF_DAY, 1),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withMicroOfDay() {
        PlainDate.of(2014, 4, 21).with(MICRO_OF_DAY, 1);
    }

    @Test
    public void containsNanoOfDay() {
        assertThat(
            PlainDate.of(2014, 4, 21).contains(NANO_OF_DAY),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getNanoOfDay() {
        PlainDate.of(2014, 4, 21).get(NANO_OF_DAY);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumNanoOfDay() {
        PlainDate.of(2014, 4, 21).getMinimum(NANO_OF_DAY);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumNanoOfDay() {
        PlainDate.of(2014, 4, 21).getMaximum(NANO_OF_DAY);
    }

    @Test
    public void isValidNanoOfDay() {
        assertThat(
            PlainDate.of(2014, 4, 21).isValid(NANO_OF_DAY, 1),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withNanoOfDay() {
        PlainDate.of(2014, 4, 21).with(NANO_OF_DAY, 1);
    }

    @Test
    public void containsMilliOfSecond() {
        assertThat(
            PlainDate.of(2014, 4, 21).contains(MILLI_OF_SECOND),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getMilliOfSecond() {
        PlainDate.of(2014, 4, 21).get(MILLI_OF_SECOND);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumMilliOfSecond() {
        PlainDate.of(2014, 4, 21).getMinimum(MILLI_OF_SECOND);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumMilliOfSecond() {
        PlainDate.of(2014, 4, 21).getMaximum(MILLI_OF_SECOND);
    }

    @Test
    public void isValidMilliOfSecond() {
        assertThat(
            PlainDate.of(2014, 4, 21).isValid(MILLI_OF_SECOND, 1),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withMilliOfSecond() {
        PlainDate.of(2014, 4, 21).with(MILLI_OF_SECOND, 1);
    }

    @Test
    public void containsMicroOfSecond() {
        assertThat(
            PlainDate.of(2014, 4, 21).contains(MICRO_OF_SECOND),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getMicroOfSecond() {
        PlainDate.of(2014, 4, 21).get(MICRO_OF_SECOND);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumMicroOfSecond() {
        PlainDate.of(2014, 4, 21).getMinimum(MICRO_OF_SECOND);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumMicroOfSecond() {
        PlainDate.of(2014, 4, 21).getMaximum(MICRO_OF_SECOND);
    }

    @Test
    public void isValidMicroOfSecond() {
        assertThat(
            PlainDate.of(2014, 4, 21).isValid(MICRO_OF_SECOND, 1),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withMicroOfSecond() {
        PlainDate.of(2014, 4, 21).with(MICRO_OF_SECOND, 1);
    }

    @Test
    public void containsNanoOfSecond() {
        assertThat(
            PlainDate.of(2014, 4, 21).contains(NANO_OF_SECOND),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void getNanoOfSecond() {
        PlainDate.of(2014, 4, 21).get(NANO_OF_SECOND);
    }

    @Test(expected=ChronoException.class)
    public void getMinimumNanoOfSecond() {
        PlainDate.of(2014, 4, 21).getMinimum(NANO_OF_SECOND);
    }

    @Test(expected=ChronoException.class)
    public void getMaximumNanoOfSecond() {
        PlainDate.of(2014, 4, 21).getMaximum(NANO_OF_SECOND);
    }

    @Test
    public void isValidNanoOfSecond() {
        assertThat(
            PlainDate.of(2014, 4, 21).isValid(NANO_OF_SECOND, 1),
            is(false));
    }

    @Test(expected=ChronoException.class)
    public void withNanoOfSecond() {
        PlainDate.of(2014, 4, 21).with(NANO_OF_SECOND, 1);
    }

}