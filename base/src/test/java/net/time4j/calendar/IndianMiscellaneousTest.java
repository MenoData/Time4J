package net.time4j.calendar;

import net.time4j.PlainDate;
import net.time4j.Weekday;
import net.time4j.engine.CalendarDate;
import net.time4j.engine.CalendarDays;
import net.time4j.format.expert.ChronoFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.time.format.FormatStyle;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class IndianMiscellaneousTest {

    @Test
    public void indianCalendarProperties() {
        IndianCalendar date = IndianCalendar.of(1995, IndianMonth.CHAITRA, 9);
        assertThat(
            date.getEra(),
            is(IndianEra.SAKA));
        assertThat(
            date.getYear(),
            is(1995));
        assertThat(
            date.getDayOfMonth(),
            is(9));
        assertThat(
            date.getMonth(),
            is(IndianMonth.CHAITRA));
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
    public void indianCalendarBetween() {
        IndianCalendar start = IndianCalendar.of(1923, IndianMonth.VAISHAKHA, 6);
        IndianCalendar end = IndianCalendar.of(1923, IndianMonth.PAUSHA, 6);
        assertThat(IndianCalendar.Unit.MONTHS.between(start, end), is(8L));
        start = start.plus(CalendarDays.ONE);
        assertThat(IndianCalendar.Unit.MONTHS.between(start, end), is(7L));
        start = start.minus(3, IndianCalendar.Unit.YEARS);
        assertThat(IndianCalendar.Unit.YEARS.between(start, end), is(3L));
        start = start.plus(6, IndianCalendar.Unit.YEARS).minus(CalendarDays.of(2)); // Saka-1926-02-05
        assertThat(IndianCalendar.Unit.YEARS.between(start, end), is(-2L));
        start = start.with(IndianCalendar.MONTH_OF_YEAR, IndianMonth.PAUSHA);
        assertThat(IndianCalendar.Unit.YEARS.between(start, end), is(-2L));
        start = start.plus(1, IndianCalendar.Unit.YEARS);
        assertThat(IndianCalendar.Unit.YEARS.between(start, end), is(-3L));
        start = start.plus(CalendarDays.ONE);
        assertThat(IndianCalendar.Unit.YEARS.between(start, end), is(-4L));
    }

    @Test
    public void formatGenericCalendarByPattern() {
        Locale loc = Locale.forLanguageTag("de-u-ca-indian");
        ChronoFormatter<CalendarDate> formatter = ChronoFormatter.ofGenericCalendarPattern("G y MMMM d, EEEE", loc);
        IndianCalendar indian = IndianCalendar.of(1918, 8, 1);
        PlainDate gregorian = indian.transform(PlainDate.class);
        assertThat(formatter.format(indian), is("Saka 1918 Kartika 1, Mittwoch"));
        assertThat(formatter.format(gregorian), is("Saka 1918 Kartika 1, Mittwoch"));
        assertThat(formatter.with(Locale.US).format(indian), is("AD 1996 October 23, Wednesday"));
    }

    @Test
    public void parseGenericCalendarByPattern() throws ParseException {
        Locale loc = Locale.forLanguageTag("de-u-ca-indian");
        ChronoFormatter<CalendarDate> formatter = ChronoFormatter.ofGenericCalendarPattern("G y MMMM d, EEEE", loc);
        IndianCalendar indian = IndianCalendar.of(1918, 8, 1);
        assertThat(formatter.parse("Saka 1918 Kartika 1, Mittwoch"), is(indian));
    }

    @Test
    public void defaultFirstDayOfWeek() {
        assertThat(IndianCalendar.DAY_OF_WEEK.getDefaultMinimum(), is(Weekday.SUNDAY));
    }

    @Test
    public void caSupport() {
        Locale locale = Locale.forLanguageTag("en-u-ca-indian");
        ChronoFormatter<CalendarDate> f = ChronoFormatter.ofGenericCalendarStyle(FormatStyle.FULL, locale);
        assertThat(
            f.format(PlainDate.of(2017, 10, 1)),
            is("Sunday, Asvina 9, 1939 Saka"));
    }

}