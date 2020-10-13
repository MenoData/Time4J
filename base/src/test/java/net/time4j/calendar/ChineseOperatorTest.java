package net.time4j.calendar;

import net.time4j.PlainDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class ChineseOperatorTest {

    @Test
    public void plusYears() {
        ChineseCalendar stdCC =
            ChineseCalendar.of(EastAsianYear.forGregorian(2016), EastAsianMonth.valueOf(6), 30);
        assertThat(
            stdCC.plus(4, ChineseCalendar.Unit.YEARS),
            is(ChineseCalendar.of(EastAsianYear.forGregorian(2020), EastAsianMonth.valueOf(6), 29)));
        assertThat(
            stdCC.plus(6, ChineseCalendar.Unit.YEARS),
            is(ChineseCalendar.of(EastAsianYear.forGregorian(2022), EastAsianMonth.valueOf(6), 30)));
        ChineseCalendar leapCC =
            ChineseCalendar.of(EastAsianYear.forGregorian(2017), EastAsianMonth.valueOf(6).withLeap(), 30);
        assertThat(
            leapCC.plus(19, ChineseCalendar.Unit.YEARS),
            is(ChineseCalendar.of(EastAsianYear.forGregorian(2036), EastAsianMonth.valueOf(6).withLeap(), 30)));
        assertThat(
            leapCC.plus(8, ChineseCalendar.Unit.YEARS),
            is(ChineseCalendar.of(EastAsianYear.forGregorian(2025), EastAsianMonth.valueOf(6).withLeap(), 29)));
        assertThat(
            leapCC.plus(5, ChineseCalendar.Unit.YEARS),
            is(ChineseCalendar.of(EastAsianYear.forGregorian(2022), EastAsianMonth.valueOf(6), 30)));
        assertThat(
            leapCC.plus(3, ChineseCalendar.Unit.YEARS),
            is(ChineseCalendar.of(EastAsianYear.forGregorian(2020), EastAsianMonth.valueOf(6), 29)));
    }

    @Test
    public void plusMonths() {
        ChineseCalendar stdCC =
            ChineseCalendar.of(EastAsianYear.forGregorian(2016), EastAsianMonth.valueOf(6), 30);
        assertThat(
            stdCC.plus(50, ChineseCalendar.Unit.MONTHS),
            is(ChineseCalendar.of(EastAsianYear.forGregorian(2020), EastAsianMonth.valueOf(6), 29)));
        ChineseCalendar leapCC =
            ChineseCalendar.of(EastAsianYear.forGregorian(2017), EastAsianMonth.valueOf(6).withLeap(), 30);
        assertThat(
            leapCC.plus(37, ChineseCalendar.Unit.MONTHS),
            is(ChineseCalendar.of(EastAsianYear.forGregorian(2020), EastAsianMonth.valueOf(6), 29)));
    }

    @Test
    public void plusWeeks() {
        ChineseCalendar cc =
            ChineseCalendar.of(EastAsianYear.forGregorian(2017), EastAsianMonth.valueOf(6).withLeap(), 28);
        assertThat(
            cc.plus(2, ChineseCalendar.Unit.WEEKS),
            is(ChineseCalendar.of(EastAsianYear.forGregorian(2017), EastAsianMonth.valueOf(7), 12)));
    }

    @Test
    public void plusDays() {
        ChineseCalendar cc =
            ChineseCalendar.of(EastAsianYear.forGregorian(2017), EastAsianMonth.valueOf(6), 28);
        assertThat(
            cc.plus(38, ChineseCalendar.Unit.DAYS),
            is(ChineseCalendar.of(EastAsianYear.forGregorian(2017), EastAsianMonth.valueOf(7), 7)));
    }

    @Test
    public void nextMonth() {
        ChineseCalendar cc =
            ChineseCalendar.of(EastAsianYear.forGregorian(2017), EastAsianMonth.valueOf(6), 29);
        assertThat(
            cc.with(ChineseCalendar.MONTH_AS_ORDINAL.incremented()),
            is(ChineseCalendar.of(EastAsianYear.forGregorian(2017), EastAsianMonth.valueOf(6).withLeap(), 29)));
    }

    @Test
    public void previousMonth() {
        ChineseCalendar cc =
            ChineseCalendar.of(EastAsianYear.forGregorian(2017), EastAsianMonth.valueOf(6).withLeap(), 30);
        assertThat(
            cc.with(ChineseCalendar.MONTH_AS_ORDINAL.decremented()),
            is(ChineseCalendar.of(EastAsianYear.forGregorian(2017), EastAsianMonth.valueOf(6), 29)));
    }

    @Test
    public void nextDay() {
        ChineseCalendar cc =
            ChineseCalendar.of(EastAsianYear.forGregorian(2017), EastAsianMonth.valueOf(6).withLeap(), 30);
        ChineseCalendar expected =
            ChineseCalendar.of(EastAsianYear.forGregorian(2017), EastAsianMonth.valueOf(7), 1);
        assertThat(
            cc.with(ChineseCalendar.DAY_OF_MONTH.incremented()),
            is(expected));
        assertThat(
            cc.with(ChineseCalendar.DAY_OF_YEAR.incremented()),
            is(expected));
        assertThat(
            cc.with(ChineseCalendar.DAY_OF_WEEK.incremented()),
            is(expected));
    }

    @Test
    public void previousDay() {
        ChineseCalendar cc =
            ChineseCalendar.of(EastAsianYear.forGregorian(2017), EastAsianMonth.valueOf(7), 1);
        ChineseCalendar expected =
            ChineseCalendar.of(EastAsianYear.forGregorian(2017), EastAsianMonth.valueOf(6).withLeap(), 30);
        assertThat(
            cc.with(ChineseCalendar.DAY_OF_MONTH.decremented()),
            is(expected));
        assertThat(
            cc.with(ChineseCalendar.DAY_OF_YEAR.decremented()),
            is(expected));
        assertThat(
            cc.with(ChineseCalendar.DAY_OF_WEEK.decremented()),
            is(expected));
    }

    @Test
    public void maxDay() {
        ChineseCalendar cc =
            ChineseCalendar.of(EastAsianYear.forGregorian(2017), EastAsianMonth.valueOf(6).withLeap(), 20);
        ChineseCalendar expected =
            ChineseCalendar.of(EastAsianYear.forGregorian(2017), EastAsianMonth.valueOf(6).withLeap(), 30);
        assertThat(
            cc.with(ChineseCalendar.DAY_OF_MONTH.maximized()),
            is(expected));
        cc =
            ChineseCalendar.of(EastAsianYear.forGregorian(2017), EastAsianMonth.valueOf(6), 20);
        expected =
            ChineseCalendar.of(EastAsianYear.forGregorian(2017), EastAsianMonth.valueOf(6), 29);
        assertThat(
            cc.with(ChineseCalendar.DAY_OF_MONTH.maximized()),
            is(expected));
    }

    @Test
    public void minDay() {
        ChineseCalendar cc =
            ChineseCalendar.of(EastAsianYear.forGregorian(2017), EastAsianMonth.valueOf(6).withLeap(), 20);
        ChineseCalendar expected =
            ChineseCalendar.of(EastAsianYear.forGregorian(2017), EastAsianMonth.valueOf(6).withLeap(), 1);
        assertThat(
            cc.with(ChineseCalendar.DAY_OF_MONTH.minimized()),
            is(expected));
    }

    @Test
    public void yearAtFloor() {
        ChineseCalendar cc =
            ChineseCalendar.of(EastAsianYear.forGregorian(2017), EastAsianMonth.valueOf(6).withLeap(), 15);
        assertThat(
            cc.with(ChineseCalendar.YEAR_OF_ERA.atFloor()),
            is(ChineseCalendar.of(EastAsianYear.forGregorian(2017), EastAsianMonth.valueOf(1), 1)));
        assertThat(
            cc.with(ChineseCalendar.YEAR_OF_ERA.atFloor()).transform(PlainDate.axis()),
            is(PlainDate.of(2017, 1, 28)));
    }

    @Test
    public void yearAtCeiling() {
        ChineseCalendar cc =
            ChineseCalendar.of(EastAsianYear.forGregorian(2017), EastAsianMonth.valueOf(6).withLeap(), 15);
        assertThat(
            cc.with(ChineseCalendar.YEAR_OF_ERA.atCeiling()),
            is(ChineseCalendar.of(EastAsianYear.forGregorian(2017), EastAsianMonth.valueOf(12), 30)));
        assertThat(
            cc.with(ChineseCalendar.YEAR_OF_ERA.atCeiling()).transform(PlainDate.axis()),
            is(PlainDate.of(2018, 2, 15)));
    }

    @Test
    public void monthAtFloor() {
        ChineseCalendar cc =
            ChineseCalendar.of(EastAsianYear.forGregorian(2017), EastAsianMonth.valueOf(6).withLeap(), 15);
        assertThat(
            cc.with(ChineseCalendar.MONTH_AS_ORDINAL.atFloor()),
            is(ChineseCalendar.of(EastAsianYear.forGregorian(2017), EastAsianMonth.valueOf(6).withLeap(), 1)));
    }

    @Test
    public void monthAtCeiling() {
        ChineseCalendar cc =
            ChineseCalendar.of(EastAsianYear.forGregorian(2017), EastAsianMonth.valueOf(6).withLeap(), 15);
        assertThat(
            cc.with(ChineseCalendar.MONTH_AS_ORDINAL.atCeiling()),
            is(ChineseCalendar.of(EastAsianYear.forGregorian(2017), EastAsianMonth.valueOf(6).withLeap(), 30)));
    }

}