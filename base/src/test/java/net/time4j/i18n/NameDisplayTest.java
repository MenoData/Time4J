package net.time4j.i18n;

import net.time4j.Meridiem;
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.Quarter;
import net.time4j.Weekday;
import net.time4j.engine.ChronoElement;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;
import net.time4j.history.ChronoHistory;
import net.time4j.history.YearDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class NameDisplayTest {

    @Test
    public void getMonthDisplayName_1args() {
        assertThat(Month.FEBRUARY.getDisplayName(Locale.GERMAN), is("Februar"));
    }

    @Test
    public void getMonthDisplayName_3args() {
        assertThat(
            Month.FEBRUARY.getDisplayName(
                Locale.GERMAN, TextWidth.WIDE, OutputContext.FORMAT),
            is("Februar"));
        assertThat(
            Month.FEBRUARY.getDisplayName(
                Locale.GERMAN, TextWidth.ABBREVIATED, OutputContext.FORMAT),
            is("Feb."));
    }

    @Test
    public void getQuarterDisplayName_1args() {
        assertThat(
            Quarter.Q1.getDisplayName(Locale.GERMAN),
            is("1. Quartal"));
    }

    @Test
    public void getQuarterDisplayName_3args() {
        assertThat(
            Quarter.Q1.getDisplayName(
                Locale.GERMAN, TextWidth.WIDE, OutputContext.FORMAT),
            is("1. Quartal"));
        assertThat(
            Quarter.Q1.getDisplayName(
                Locale.GERMAN, TextWidth.ABBREVIATED, OutputContext.FORMAT),
            is("Q1"));
    }

    @Test
    public void getWeekdayDisplayName_1args() {
        assertThat(Weekday.TUESDAY.getDisplayName(Locale.US), is("Tuesday"));
    }

    @Test
    public void getWeekdayDisplayName_3args() {
        assertThat(
            Weekday.TUESDAY.getDisplayName(
                Locale.GERMAN, TextWidth.ABBREVIATED, OutputContext.FORMAT),
            is("Di."));
        assertThat(
            Weekday.TUESDAY.getDisplayName(
                Locale.GERMAN, TextWidth.WIDE, OutputContext.FORMAT),
            is("Dienstag"));
    }

    @Test
    public void getMeridiemDisplayName() {
        assertThat(Meridiem.AM.getDisplayName(Locale.US), is("am"));
        assertThat(Meridiem.PM.getDisplayName(Locale.US), is("pm"));
    }

    @Test
    public void getDisplayNameOfElement_ERA() {
        ChronoElement<?> element = ChronoHistory.ofFirstGregorianReform().era();
        assertThat(element.getDisplayName(Locale.ENGLISH), is("era"));
    }

    @Test
    public void getDisplayNameOfElement_YEAR() {
        assertThat(PlainDate.YEAR.getDisplayName(Locale.GERMAN), is("Jahr"));
    }

    @Test
    public void getDisplayNameOfElement_YEAR_OF_WEEKDATE() {
        assertThat(PlainDate.YEAR_OF_WEEKDATE.getDisplayName(Locale.GERMAN), is("Jahr"));
    }

    @Test
    public void getDisplayNameOfElement_YEAR_OF_ERA() {
        ChronoElement<?> element = ChronoHistory.ofFirstGregorianReform().yearOfEra(YearDefinition.DUAL_DATING);
        assertThat(element.getDisplayName(Locale.ENGLISH), is("year"));
    }

    @Test
    public void getDisplayNameOfElement_QUARTER_OF_YEAR() {
        assertThat(PlainDate.QUARTER_OF_YEAR.getDisplayName(Locale.GERMAN), is("Quartal"));
    }

    @Test
    public void getDisplayNameOfElement_MONTH() {
        assertThat(PlainDate.MONTH_OF_YEAR.getDisplayName(Locale.GERMAN), is("Monat"));
        assertThat(PlainDate.MONTH_AS_NUMBER.getDisplayName(Locale.GERMAN), is("Monat"));
    }

    @Test
    public void getDisplayNameOfElement_DAY_OF_MONTH() {
        assertThat(PlainDate.DAY_OF_MONTH.getDisplayName(Locale.GERMAN), is("Tag"));
    }

    @Test
    public void getDisplayNameOfElement_DAY_OF_WEEK() {
        assertThat(PlainDate.DAY_OF_WEEK.getDisplayName(Locale.GERMAN), is("Wochentag"));
    }

    @Test
    public void getDisplayNameOfElement_AM_PM_OF_DAY() {
        assertThat(PlainTime.AM_PM_OF_DAY.getDisplayName(Locale.ENGLISH), is("am/pm"));
    }

    @Test
    public void getDisplayNameOfElement_HOUR() {
        assertThat(PlainTime.CLOCK_HOUR_OF_DAY.getDisplayName(Locale.GERMAN), is("Stunde"));
        assertThat(PlainTime.DIGITAL_HOUR_OF_DAY.getDisplayName(Locale.GERMAN), is("Stunde"));
        assertThat(PlainTime.CLOCK_HOUR_OF_AMPM.getDisplayName(Locale.GERMAN), is("Stunde"));
        assertThat(PlainTime.DIGITAL_HOUR_OF_AMPM.getDisplayName(Locale.GERMAN), is("Stunde"));
        assertThat(PlainTime.HOUR_FROM_0_TO_24.getDisplayName(Locale.GERMAN), is("Stunde"));
    }

    @Test
    public void getDisplayNameOfElement_MINUTE() {
        assertThat(PlainTime.MINUTE_OF_HOUR.getDisplayName(Locale.ENGLISH), is("minute"));
    }

    @Test
    public void getDisplayNameOfElement_SECOND() {
        assertThat(PlainTime.SECOND_OF_MINUTE.getDisplayName(Locale.ENGLISH), is("second"));
    }

    @Test
    public void parseQuarterName() throws ParseException {
        assertThat(
            Quarter.parse(
                "1. Quartal", Locale.GERMAN, TextWidth.WIDE, OutputContext.FORMAT),
            is(Quarter.Q1));
        assertThat(
            Quarter.parse(
                "Q1", Locale.GERMAN, TextWidth.ABBREVIATED, OutputContext.FORMAT),
            is(Quarter.Q1));
    }

    @Test
    public void parseWeekdayName() throws ParseException {
        assertThat(
            Weekday.parse(
                "Montag", Locale.GERMAN, TextWidth.WIDE, OutputContext.FORMAT),
            is(Weekday.MONDAY));
        assertThat(
            Weekday.parse(
                "Mo.", Locale.GERMAN, TextWidth.ABBREVIATED, OutputContext.FORMAT),
            is(Weekday.MONDAY));
    }

    @Test
    public void parseMonthName() throws ParseException {
        assertThat(
            Month.parse(
                "Januar", Locale.GERMAN, TextWidth.WIDE, OutputContext.FORMAT),
            is(Month.JANUARY));
        assertThat(
            Month.parse(
                "Jan", Locale.GERMAN, TextWidth.ABBREVIATED, OutputContext.STANDALONE),
            is(Month.JANUARY));
    }

    @Test
    public void parseMeridiemName() throws ParseException {
        assertThat(
            Meridiem.parse(
                "a. m.", new Locale("es"), TextWidth.WIDE, OutputContext.FORMAT),
            is(Meridiem.AM));
        assertThat(
            Meridiem.parse(
                "p. m.", new Locale("es"), TextWidth.WIDE, OutputContext.FORMAT),
            is(Meridiem.PM));
        assertThat(
            Meridiem.parse(
                "am", new Locale("es"), TextWidth.WIDE, OutputContext.FORMAT),
            is(Meridiem.AM));
        assertThat(
            Meridiem.parse(
                "AM", new Locale("es"), TextWidth.WIDE, OutputContext.FORMAT),
            is(Meridiem.AM));
        assertThat(
            Meridiem.parse(
                "pm", new Locale("es"), TextWidth.WIDE, OutputContext.FORMAT),
            is(Meridiem.PM));
        assertThat(
            Meridiem.parse(
                "PM", new Locale("es"), TextWidth.WIDE, OutputContext.FORMAT),
            is(Meridiem.PM));
    }

}