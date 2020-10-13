package net.time4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.PlainDate.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class DateElementTest {

    @Test
    public void dateName() {
        assertThat(CALENDAR_DATE.name(), is("CALENDAR_DATE"));
    }

    @Test
    public void yearName() {
        assertThat(YEAR.name(), is("YEAR"));
    }

    @Test
    public void yearOfWeekdateName() {
        assertThat(YEAR_OF_WEEKDATE.name(), is("YEAR_OF_WEEKDATE"));
    }

    @Test
    public void monthOfYearName() {
        assertThat(MONTH_OF_YEAR.name(), is("MONTH_OF_YEAR"));
    }

    @Test
    public void monthAsNumberName() {
        assertThat(MONTH_AS_NUMBER.name(), is("MONTH_AS_NUMBER"));
    }

    @Test
    public void quarterOfYearName() {
        assertThat(QUARTER_OF_YEAR.name(), is("QUARTER_OF_YEAR"));
    }

    @Test
    public void dayOfYearName() {
        assertThat(DAY_OF_YEAR.name(), is("DAY_OF_YEAR"));
    }

    @Test
    public void dayOfQuarterName() {
        assertThat(DAY_OF_QUARTER.name(), is("DAY_OF_QUARTER"));
    }

    @Test
    public void dayOfMonthName() {
        assertThat(DAY_OF_MONTH.name(), is("DAY_OF_MONTH"));
    }

    @Test
    public void dayOfWeekName() {
        assertThat(DAY_OF_WEEK.name(), is("DAY_OF_WEEK"));
    }

    @Test
    public void dateIsLenient() {
        assertThat(CALENDAR_DATE.isLenient(), is(false));
    }

    @Test
    public void yearIsLenient() {
        assertThat(YEAR.isLenient(), is(false));
    }

    @Test
    public void yearOfWeekdateIsLenient() {
        assertThat(YEAR_OF_WEEKDATE.isLenient(), is(false));
    }

    @Test
    public void monthOfYearIsLenient() {
        assertThat(MONTH_OF_YEAR.isLenient(), is(false));
    }

    @Test
    public void monthAsNumberIsLenient() {
        assertThat(MONTH_AS_NUMBER.isLenient(), is(false));
    }

    @Test
    public void quarterOfYearIsLenient() {
        assertThat(QUARTER_OF_YEAR.isLenient(), is(false));
    }

    @Test
    public void dayOfYearIsLenient() {
        assertThat(DAY_OF_YEAR.isLenient(), is(false));
    }

    @Test
    public void dayOfQuarterIsLenient() {
        assertThat(DAY_OF_QUARTER.isLenient(), is(false));
    }

    @Test
    public void dayOfMonthIsLenient() {
        assertThat(DAY_OF_MONTH.isLenient(), is(false));
    }

    @Test
    public void dayOfWeekIsLenient() {
        assertThat(DAY_OF_WEEK.isLenient(), is(false));
    }

    @Test
    public void dateGetDefaultMinimum() {
        assertThat(CALENDAR_DATE.getDefaultMinimum(), is(PlainDate.MIN));
    }

    @Test
    public void yearGetDefaultMinimum() {
        assertThat(YEAR.getDefaultMinimum(), is(-999999999));
    }

    @Test
    public void yearOfWeekdateGetDefaultMinimum() {
        assertThat(YEAR_OF_WEEKDATE.getDefaultMinimum(), is(-999999999));
    }

    @Test
    public void monthOfYearGetDefaultMinimum() {
        assertThat(MONTH_OF_YEAR.getDefaultMinimum(), is(Month.JANUARY));
    }

    @Test
    public void monthAsNumberGetDefaultMinimum() {
        assertThat(MONTH_AS_NUMBER.getDefaultMinimum(), is(1));
    }

    @Test
    public void quarterOfYearGetDefaultMinimum() {
        assertThat(QUARTER_OF_YEAR.getDefaultMinimum(), is(Quarter.Q1));
    }

    @Test
    public void dayOfYearGetDefaultMinimum() {
        assertThat(DAY_OF_YEAR.getDefaultMinimum(), is(1));
    }

    @Test
    public void dayOfQuarterGetDefaultMinimum() {
        assertThat(DAY_OF_QUARTER.getDefaultMinimum(), is(1));
    }

    @Test
    public void dayOfMonthGetDefaultMinimum() {
        assertThat(DAY_OF_MONTH.getDefaultMinimum(), is(1));
    }

    @Test
    public void dayOfWeekGetDefaultMinimum() {
        assertThat(DAY_OF_WEEK.getDefaultMinimum(), is(Weekday.MONDAY));
    }

    @Test
    public void dateGetDefaultMaximum() {
        assertThat(CALENDAR_DATE.getDefaultMaximum(), is(PlainDate.MAX));
    }

    @Test
    public void yearGetDefaultMaximum() {
        assertThat(YEAR.getDefaultMaximum(), is(999999999));
    }

    @Test
    public void yearOfWeekdateGetDefaultMaximum() {
        assertThat(YEAR_OF_WEEKDATE.getDefaultMaximum(), is(999999999));
    }

    @Test
    public void monthOfYearGetDefaultMaximum() {
        assertThat(MONTH_OF_YEAR.getDefaultMaximum(), is(Month.DECEMBER));
    }

    @Test
    public void monthAsNumberGetDefaultMaximum() {
        assertThat(MONTH_AS_NUMBER.getDefaultMaximum(), is(12));
    }

    @Test
    public void quarterOfYearGetDefaultMaximum() {
        assertThat(QUARTER_OF_YEAR.getDefaultMaximum(), is(Quarter.Q4));
    }

    @Test
    public void dayOfYearGetDefaultMaximum() {
        assertThat(DAY_OF_YEAR.getDefaultMaximum(), is(365));
    }

    @Test
    public void dayOfQuarterGetDefaultMaximum() {
        assertThat(DAY_OF_QUARTER.getDefaultMaximum(), is(92));
    }

    @Test
    public void dayOfMonthGetDefaultMaximum() {
        assertThat(DAY_OF_MONTH.getDefaultMaximum(), is(31));
    }

    @Test
    public void dayOfWeekGetDefaultMaximum() {
        assertThat(DAY_OF_WEEK.getDefaultMaximum(), is(Weekday.SUNDAY));
    }

    @Test
    public void dateIsDateElement() {
        assertThat(CALENDAR_DATE.isDateElement(), is(true));
    }

    @Test
    public void yearIsDateElement() {
        assertThat(YEAR.isDateElement(), is(true));
    }

    @Test
    public void yearOfWeekdateIsDateElement() {
        assertThat(YEAR_OF_WEEKDATE.isDateElement(), is(true));
    }

    @Test
    public void monthOfYearIsDateElement() {
        assertThat(MONTH_OF_YEAR.isDateElement(), is(true));
    }

    @Test
    public void monthAsNumberIsDateElement() {
        assertThat(MONTH_AS_NUMBER.isDateElement(), is(true));
    }

    @Test
    public void quarterOfYearIsDateElement() {
        assertThat(QUARTER_OF_YEAR.isDateElement(), is(true));
    }

    @Test
    public void dayOfYearIsDateElement() {
        assertThat(DAY_OF_YEAR.isDateElement(), is(true));
    }

    @Test
    public void dayOfQuarterIsDateElement() {
        assertThat(DAY_OF_QUARTER.isDateElement(), is(true));
    }

    @Test
    public void dayOfMonthIsDateElement() {
        assertThat(DAY_OF_MONTH.isDateElement(), is(true));
    }

    @Test
    public void dayOfWeekIsDateElement() {
        assertThat(DAY_OF_WEEK.isDateElement(), is(true));
    }

    @Test
    public void dateIsTimeElement() {
        assertThat(CALENDAR_DATE.isTimeElement(), is(false));
    }

    @Test
    public void yearIsTimeElement() {
        assertThat(YEAR.isTimeElement(), is(false));
    }

    @Test
    public void yearOfWeekdateIsTimeElement() {
        assertThat(YEAR_OF_WEEKDATE.isTimeElement(), is(false));
    }

    @Test
    public void monthOfYearIsTimeElement() {
        assertThat(MONTH_OF_YEAR.isTimeElement(), is(false));
    }

    @Test
    public void monthAsNumberIsTimeElement() {
        assertThat(MONTH_AS_NUMBER.isTimeElement(), is(false));
    }

    @Test
    public void quarterOfYearIsTimeElement() {
        assertThat(QUARTER_OF_YEAR.isTimeElement(), is(false));
    }

    @Test
    public void dayOfYearIsTimeElement() {
        assertThat(DAY_OF_YEAR.isTimeElement(), is(false));
    }

    @Test
    public void dayOfQuarterIsTimeElement() {
        assertThat(DAY_OF_QUARTER.isTimeElement(), is(false));
    }

    @Test
    public void dayOfMonthIsTimeElement() {
        assertThat(DAY_OF_MONTH.isTimeElement(), is(false));
    }

    @Test
    public void dayOfWeekIsTimeElement() {
        assertThat(DAY_OF_WEEK.isTimeElement(), is(false));
    }

    @Test
    public void dateGetSymbol() {
        assertThat(CALENDAR_DATE.getSymbol(), is('\u0000'));
    }

    @Test
    public void yearGetSymbol() {
        assertThat(YEAR.getSymbol(), is('u'));
    }

    @Test
    public void yearOfWeekdateGetSymbol() {
        assertThat(YEAR_OF_WEEKDATE.getSymbol(), is('Y'));
    }

    @Test
    public void monthOfYearGetSymbol() {
        assertThat(MONTH_OF_YEAR.getSymbol(), is('M'));
    }

    @Test
    public void monthAsNumberGetSymbol() {
        assertThat(MONTH_AS_NUMBER.getSymbol(), is('M'));
    }

    @Test
    public void quarterOfYearGetSymbol() {
        assertThat(QUARTER_OF_YEAR.getSymbol(), is('Q'));
    }

    @Test
    public void dayOfYearGetSymbol() {
        assertThat(DAY_OF_YEAR.getSymbol(), is('D'));
    }

    @Test
    public void dayOfQuarterGetSymbol() {
        assertThat(DAY_OF_QUARTER.getSymbol(), is('\u0000'));
    }

    @Test
    public void dayOfMonthGetSymbol() {
        assertThat(DAY_OF_MONTH.getSymbol(), is('d'));
    }

    @Test
    public void dayOfWeekGetSymbol() {
        assertThat(DAY_OF_WEEK.getSymbol(), is('E'));
    }

    @Test
    public void nameOfAxisElement() {
        assertThat(
            PlainDate.axis().element().name(),
            is("net.time4j.PlainDate-AXIS"));
    }

    @Test
    public void equalsOfAxisElement() {
        assertThat(
            PlainDate.axis().element().equals(PlainTime.axis().element()),
            is(false));
    }

}