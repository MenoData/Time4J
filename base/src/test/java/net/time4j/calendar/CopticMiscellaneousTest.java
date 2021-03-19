package net.time4j.calendar;

import net.time4j.PlainDate;
import net.time4j.Weekday;
import net.time4j.engine.CalendarDate;
import net.time4j.engine.CalendarDays;
import net.time4j.format.expert.ChronoFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.format.FormatStyle;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class CopticMiscellaneousTest {

    @Test
    public void copticCalendarProperties() {
        CopticCalendar date = CopticCalendar.of(1720, CopticMonth.AMSHIR, 9);
        assertThat(
            date.getDayOfMonth(),
            is(9));
        assertThat(
            date.getMonth(),
            is(CopticMonth.AMSHIR));
        assertThat(
            date.lengthOfMonth(),
            is(30));
        assertThat(
            date.atTime(12, 0).toDate(),
            is(date));
        assertThat(
            date.lengthOfYear(),
            is(365)
        );
    }

    @Test
    public void copticCalendarBetween() {
        CopticCalendar start = CopticCalendar.of(1723, CopticMonth.AMSHIR, 6);
        CopticCalendar end = CopticCalendar.of(1723, CopticMonth.NASIE, 6);
        assertThat(CopticCalendar.Unit.MONTHS.between(start, end), is(7));
        start = start.plus(CalendarDays.ONE);
        assertThat(CopticCalendar.Unit.MONTHS.between(start, end), is(6));
        start = start.minus(3, CopticCalendar.Unit.YEARS);
        assertThat(CopticCalendar.Unit.YEARS.between(start, end), is(3));
        start = start.plus(6, CopticCalendar.Unit.YEARS).minus(CalendarDays.of(2)); // A.M.-1726-06-05
        assertThat(CopticCalendar.Unit.YEARS.between(start, end), is(-2));
        start = start.with(CopticCalendar.MONTH_OF_YEAR, CopticMonth.NASIE); // A.M.-1726-13-05
        assertThat(CopticCalendar.Unit.YEARS.between(start, end), is(-2));
        start = start.plus(1, CopticCalendar.Unit.YEARS); // A.M.-1727-06-05
        assertThat(CopticCalendar.Unit.YEARS.between(start, end), is(-3));
        start = start.plus(CalendarDays.ONE); // A.M.-1727-06-06
        assertThat(CopticCalendar.Unit.YEARS.between(start, end), is(-4));
    }

    @Test
    public void defaultFirstDayOfWeek() {
        assertThat(CopticCalendar.DAY_OF_WEEK.getDefaultMinimum(), is(Weekday.SATURDAY));
    }

    @Test
    public void caSupport() {
        Locale locale = Locale.forLanguageTag("en-u-ca-coptic");
        ChronoFormatter<CalendarDate> f = ChronoFormatter.ofGenericCalendarStyle(FormatStyle.FULL, locale);
        assertThat(
            f.format(PlainDate.of(2017, 10, 1)),
            is("Sunday, Tout 21, 1734 A.M."));
    }

}