package net.time4j;


import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class MonthValueTest {

    @Test
    public void valueOf_int() {
        for (int i = 0; i < 12; i++) {
            assertThat(Month.valueOf(i + 1), is(Month.values()[i]));
        }
    }

    @Test
    public void getValue() {
        for (int i = 0; i < 12; i++) {
            assertThat(Month.values()[i].getValue(), is(i + 1));
        }
    }

    @Test
    public void getQuarterOfYear() {
        assertThat(Month.JANUARY.getQuarterOfYear(), is(Quarter.Q1));
        assertThat(Month.FEBRUARY.getQuarterOfYear(), is(Quarter.Q1));
        assertThat(Month.MARCH.getQuarterOfYear(), is(Quarter.Q1));
        assertThat(Month.APRIL.getQuarterOfYear(), is(Quarter.Q2));
        assertThat(Month.MAY.getQuarterOfYear(), is(Quarter.Q2));
        assertThat(Month.JUNE.getQuarterOfYear(), is(Quarter.Q2));
        assertThat(Month.JULY.getQuarterOfYear(), is(Quarter.Q3));
        assertThat(Month.AUGUST.getQuarterOfYear(), is(Quarter.Q3));
        assertThat(Month.SEPTEMBER.getQuarterOfYear(), is(Quarter.Q3));
        assertThat(Month.OCTOBER.getQuarterOfYear(), is(Quarter.Q4));
        assertThat(Month.NOVEMBER.getQuarterOfYear(), is(Quarter.Q4));
        assertThat(Month.DECEMBER.getQuarterOfYear(), is(Quarter.Q4));
    }

    @Test
    public void getLength() {
        assertThat(Month.JANUARY.getLength(2012), is(31));
        assertThat(Month.FEBRUARY.getLength(2012), is(29));
        assertThat(Month.FEBRUARY.getLength(1900), is(28));
        assertThat(Month.MARCH.getLength(2012), is(31));
        assertThat(Month.APRIL.getLength(2012), is(30));
        assertThat(Month.MAY.getLength(2012), is(31));
        assertThat(Month.JUNE.getLength(2012), is(30));
        assertThat(Month.JULY.getLength(2012), is(31));
        assertThat(Month.AUGUST.getLength(2012), is(31));
        assertThat(Month.SEPTEMBER.getLength(2012), is(30));
        assertThat(Month.OCTOBER.getLength(2012), is(31));
        assertThat(Month.NOVEMBER.getLength(2012), is(30));
        assertThat(Month.DECEMBER.getLength(2012), is(31));
    }

    @Test
    public void next() {
        assertThat(Month.FEBRUARY.next(), is(Month.MARCH));
        assertThat(Month.DECEMBER.next(), is(Month.JANUARY));
    }

    @Test
    public void previous() {
        assertThat(Month.FEBRUARY.previous(), is(Month.JANUARY));
        assertThat(Month.JANUARY.previous(), is(Month.DECEMBER));
    }

    @Test
    public void roll() {
        assertThat(Month.FEBRUARY.roll(-2), is(Month.DECEMBER));
    }

    @Test
    public void test() {
        assertThat(Month.FEBRUARY.test(PlainDate.of(2012, 2, 17)), is(true));
    }

    @Test
    public void atStartOfQuarter() {
        assertThat(Month.atStartOfQuarter(Quarter.Q1), is(Month.JANUARY));
        assertThat(Month.atStartOfQuarter(Quarter.Q2), is(Month.APRIL));
        assertThat(Month.atStartOfQuarter(Quarter.Q3), is(Month.JULY));
        assertThat(Month.atStartOfQuarter(Quarter.Q4), is(Month.OCTOBER));
    }

    @Test
    public void atEndOfQuarter() {
        assertThat(Month.atEndOfQuarter(Quarter.Q1), is(Month.MARCH));
        assertThat(Month.atEndOfQuarter(Quarter.Q2), is(Month.JUNE));
        assertThat(Month.atEndOfQuarter(Quarter.Q3), is(Month.SEPTEMBER));
        assertThat(Month.atEndOfQuarter(Quarter.Q4), is(Month.DECEMBER));
    }

    @Test
    public void getDisplayName_1args() {
        assertThat(Month.FEBRUARY.getDisplayName(Locale.GERMAN), is("Februar"));
    }

    @Test
    public void getDisplayName_2args() {
        assertThat(
            Month.FEBRUARY.getDisplayName(Locale.GERMAN, true),
            is("Februar"));
        assertThat(
            Month.FEBRUARY.getDisplayName(Locale.GERMAN, false),
            is("Feb"));
    }

}