package net.time4j;


import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


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
        assertThat(Month.FEBRUARY.roll(22), is(Month.DECEMBER));
        assertThat(Month.FEBRUARY.roll(-38), is(Month.DECEMBER));
    }

    @Test
    public void test() {
        assertThat(Month.FEBRUARY.test(PlainDate.of(2012, 2, 17)), is(true));
    }

    @Test
    public void atStartOfQuarterYear() {
        assertThat(Month.atStartOfQuarterYear(Quarter.Q1), is(Month.JANUARY));
        assertThat(Month.atStartOfQuarterYear(Quarter.Q2), is(Month.APRIL));
        assertThat(Month.atStartOfQuarterYear(Quarter.Q3), is(Month.JULY));
        assertThat(Month.atStartOfQuarterYear(Quarter.Q4), is(Month.OCTOBER));
    }

    @Test
    public void atEndOfQuarterYear() {
        assertThat(Month.atEndOfQuarterYear(Quarter.Q1), is(Month.MARCH));
        assertThat(Month.atEndOfQuarterYear(Quarter.Q2), is(Month.JUNE));
        assertThat(Month.atEndOfQuarterYear(Quarter.Q3), is(Month.SEPTEMBER));
        assertThat(Month.atEndOfQuarterYear(Quarter.Q4), is(Month.DECEMBER));
    }

    @Test
    public void czechMonthInFormatContext() {
        assertThat(
            Month.JANUARY.getDisplayName(new Locale("cs"), TextWidth.WIDE, OutputContext.FORMAT),
            is("ledna")
        );
    }

    @Test
    public void czechMonthInStandaloneContext() {
        assertThat(
            Month.JANUARY.getDisplayName(new Locale("cs"), TextWidth.WIDE, OutputContext.STANDALONE),
            is("leden")
        );
    }

    @Test
    public void threetenConversion() {
        assertThat(Month.JANUARY.toTemporalAccessor(), is(java.time.Month.JANUARY));
        assertThat(Month.DECEMBER.toTemporalAccessor(), is(java.time.Month.DECEMBER));

        assertThat(Month.from(java.time.Month.JANUARY), is(Month.JANUARY));
        assertThat(Month.from(java.time.Month.DECEMBER), is(Month.DECEMBER));
    }

}