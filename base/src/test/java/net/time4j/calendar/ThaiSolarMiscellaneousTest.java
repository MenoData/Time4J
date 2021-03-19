package net.time4j.calendar;

import net.time4j.CalendarUnit;
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.Weekday;
import net.time4j.engine.CalendarDate;
import net.time4j.format.expert.ChronoFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.format.FormatStyle;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class ThaiSolarMiscellaneousTest {

    @Test
    public void thaiSolarCalendarProperties() {
        ThaiSolarCalendar date = ThaiSolarCalendar.of(ThaiSolarEra.RATTANAKOSIN, 106, 2, 10);
        assertThat(
            date,
            is(ThaiSolarCalendar.of(ThaiSolarEra.BUDDHIST, 1888 + 542, 2, 10)));
        assertThat(
            date.getEra(),
            is(ThaiSolarEra.BUDDHIST));
        assertThat(
            date.getYear(),
            is(1888 + 542));
        assertThat(
            date.getMonth(),
            is(Month.FEBRUARY));
        assertThat(
            date.getDayOfYear(),
            is(316));
        assertThat(
            date.getDayOfMonth(),
            is(10));
        assertThat(
            date.getDayOfWeek(),
            is(Weekday.FRIDAY));
        assertThat(
            date.lengthOfMonth(),
            is(29));
        assertThat(
            date.lengthOfYear(),
            is(366));
        assertThat(
            date.isLeapYear(),
            is(true));
        assertThat(
            date.atTime(12, 0).toDate(),
            is(date));
    }

    @Test
    public void thaiSolarCalendarBetween() {
        ThaiSolarCalendar start = ThaiSolarCalendar.ofBuddhist(2482, 2, 10);
        ThaiSolarCalendar end = ThaiSolarCalendar.ofBuddhist(2485, 2, 10);
        assertThat(CalendarUnit.YEARS.between(start, end), is(2L));
        assertThat(CalendarUnit.MONTHS.between(start, end), is(24L));
        assertThat(CalendarUnit.DAYS.between(start, end), is(731L));
    }

    @Test
    public void formatThaiSolarCalendar() {
        ChronoFormatter<ThaiSolarCalendar> f =
            ChronoFormatter.ofStyle(FormatStyle.FULL, Locale.GERMAN, ThaiSolarCalendar.axis());
        assertThat(
            f.format(ThaiSolarCalendar.ofBuddhist(2482, 2, 10)),
            is("Samstag, 10. Februar 2482 BE")
        );
    }

    @Test
    public void defaultFirstDayOfWeek() {
        assertThat(ThaiSolarCalendar.DAY_OF_WEEK.getDefaultMinimum(), is(Weekday.SUNDAY));
    }

    @Test
    public void caSupport() {
        Locale locale = Locale.forLanguageTag("en-u-ca-buddhist");
        ChronoFormatter<CalendarDate> f = ChronoFormatter.ofGenericCalendarStyle(FormatStyle.FULL, locale);
        assertThat(
            f.format(PlainDate.of(2017, 10, 1)),
            is("Sunday, October 1, 2560 BE"));
    }

    @Test
    public void buddhistPreference() {
        Locale locale = Locale.forLanguageTag("en-TH");
        ChronoFormatter<CalendarDate> f = ChronoFormatter.ofGenericCalendarStyle(FormatStyle.FULL, locale);
        assertThat(
            f.format(PlainDate.of(2017, 10, 1)),
            is("Sunday, October 1, 2560 BE"));
    }

}