package net.time4j.i18n;

import net.time4j.Meridiem;
import net.time4j.Month;
import net.time4j.Quarter;
import net.time4j.Weekday;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;

import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class EnumDisplayTest {

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
        assertThat(Meridiem.AM.getDisplayName(Locale.US), is("AM"));
        assertThat(Meridiem.PM.getDisplayName(Locale.US), is("PM"));
    }

}