package net.time4j.i18n;

import net.time4j.Meridiem;
import net.time4j.Month;
import net.time4j.Quarter;
import net.time4j.Weekday;
import net.time4j.format.CalendarText;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;

import java.util.Locale;

import net.time4j.history.HistoricEra;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class RootLocaleTest {

    @Test
    public void monthsWide() {
        CalendarText ct = CalendarText.getInstance(CalendarText.ISO_CALENDAR_TYPE, Locale.ROOT);
        assertThat(
            ct.getStdMonths(TextWidth.WIDE, OutputContext.FORMAT).print(Month.JANUARY),
            is("01"));
    }

    @Test
    public void monthsAbbreviated() {
        CalendarText ct = CalendarText.getInstance(CalendarText.ISO_CALENDAR_TYPE, Locale.ROOT);
        assertThat(
            ct.getStdMonths(TextWidth.ABBREVIATED, OutputContext.FORMAT).print(Month.JANUARY),
            is("1"));
    }

    @Test
    public void quartersWide() {
        CalendarText ct = CalendarText.getInstance(CalendarText.ISO_CALENDAR_TYPE, Locale.ROOT);
        assertThat(
            ct.getQuarters(TextWidth.WIDE, OutputContext.FORMAT).print(Quarter.Q1),
            is("Q1"));
    }

    @Test
    public void quartersAbbreviated() {
        CalendarText ct = CalendarText.getInstance(CalendarText.ISO_CALENDAR_TYPE, Locale.ROOT);
        assertThat(
            ct.getQuarters(TextWidth.ABBREVIATED, OutputContext.FORMAT).print(Quarter.Q1),
            is("Q1"));
    }

    @Test
    public void quartersNarrow() {
        CalendarText ct = CalendarText.getInstance(CalendarText.ISO_CALENDAR_TYPE, Locale.ROOT);
        assertThat(
            ct.getQuarters(TextWidth.NARROW, OutputContext.FORMAT).print(Quarter.Q1),
            is("1"));
    }

    @Test
    public void weekdaysWide() {
        CalendarText ct = CalendarText.getInstance(CalendarText.ISO_CALENDAR_TYPE, Locale.ROOT);
        assertThat(
            ct.getWeekdays(TextWidth.WIDE, OutputContext.FORMAT).print(Weekday.MONDAY),
            is("1"));
    }

    @Test
    public void erasWide() {
        CalendarText ct = CalendarText.getInstance(CalendarText.ISO_CALENDAR_TYPE, Locale.ROOT);
        assertThat(
            ct.getEras(TextWidth.WIDE).print(HistoricEra.BC),
            is("BC"));
    }

    @Test
    public void meridiemsWide() {
        CalendarText ct = CalendarText.getInstance(CalendarText.ISO_CALENDAR_TYPE, Locale.ROOT);
        assertThat(
            ct.getMeridiems(TextWidth.WIDE, OutputContext.FORMAT).print(Meridiem.AM),
            is("AM"));
    }

}