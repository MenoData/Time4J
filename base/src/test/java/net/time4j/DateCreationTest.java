package net.time4j;

import net.time4j.engine.Chronology;
import net.time4j.engine.EpochDays;
import net.time4j.format.Attributes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class DateCreationTest {

    @Test
    public void ofCalendarDate1() {
        PlainDate date = PlainDate.of(2014, 5, 31);
        assertThat(date.getYear(), is(2014));
        assertThat(date.getMonth(), is(5));
        assertThat(date.getDayOfMonth(), is(31));
    }

    @Test
    public void ofCalendarDate2() {
        PlainDate date = PlainDate.of(2012, 2, 29);
        assertThat(date.getYear(), is(2012));
        assertThat(date.getMonth(), is(2));
        assertThat(date.getDayOfMonth(), is(29));
    }

    @Test
    public void ofCalendarDate3() {
        PlainDate date = PlainDate.of(2013, Month.JUNE, 15);
        assertThat(date.getYear(), is(2013));
        assertThat(date.getMonth(), is(6));
        assertThat(date.getDayOfMonth(), is(15));
    }

    @Test
    public void ofCalendarDate4() {
        PlainDate date = PlainDate.of(999999999, 12, 31);
        assertThat(date.getYear(), is(999999999));
        assertThat(date.getMonth(), is(12));
        assertThat(date.getDayOfMonth(), is(31));
    }

    @Test
    public void ofCalendarDate5() {
        PlainDate date = PlainDate.of(-999999999, 1, 1);
        assertThat(date.getYear(), is(-999999999));
        assertThat(date.getMonth(), is(1));
        assertThat(date.getDayOfMonth(), is(1));
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofCalendarDateWithOverflowYear1() {
        PlainDate.of(1000000000, 1, 1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofCalendarDateWithOverflowYear2() {
        PlainDate.of(-1000000000, 1, 1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofCalendarDateInvalid1() {
        PlainDate.of(2014, 2, 29);
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofCalendarDateInvalid2() {
        PlainDate.of(2014, 4, 31);
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofCalendarDateInvalid3() {
        PlainDate.of(2014, 2, 0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofCalendarDateWithMonthZero() {
        PlainDate.of(2014, 0, 1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofCalendarDateWithMonth13() {
        PlainDate.of(2014, 13, 1);
    }

    @Test
    public void ofOrdinalDate1() {
        assertThat(
            PlainDate.of(2012, 1),
            is(PlainDate.of(2012, 1, 1)));
    }

    @Test
    public void ofOrdinalDate2() {
        assertThat(
            PlainDate.of(2012, 300),
            is(PlainDate.of(2012, 10, 26)));
    }

    @Test
    public void ofOrdinalDate3() {
        assertThat(
            PlainDate.of(2012, 366),
            is(PlainDate.of(2012, 12, 31)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofOrdinalDate4() {
        PlainDate.of(2012, 367);
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofOrdinalDate5() {
        PlainDate.of(2012, 0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofOrdinalDate6() {
        PlainDate.of(2013, 366);
    }

    @Test
    public void ofWeekdate1() {
        assertThat(
            PlainDate.of(2012, 1, Weekday.MONDAY),
            is(PlainDate.of(2012, 1, 2)));
    }

    @Test
    public void ofWeekdate2() {
        assertThat(
            PlainDate.of(2013, 1, Weekday.MONDAY),
            is(PlainDate.of(2012, 12, 31)));
    }

    @Test
    public void ofWeekdate3() {
        assertThat(
            PlainDate.of(2012, 52, Weekday.SUNDAY),
            is(PlainDate.of(2012, 12, 30)));
    }

    @Test
    public void ofWeekdate4() {
        assertThat(
            PlainDate.of(2013, 1, Weekday.TUESDAY),
            is(PlainDate.of(2013, 1, 1)));
    }

    @Test
    public void ofWeekdate5() {
        assertThat(
            PlainDate.of(2011, 52, Weekday.SATURDAY),
            is(PlainDate.of(2011, 12, 31)));
    }

    @Test
    public void ofWeekdate6() {
        assertThat(
            PlainDate.of(2011, 52, Weekday.SUNDAY),
            is(PlainDate.of(2012, 1, 1)));
    }

    @Test
    public void ofWeekdate7() {
        assertThat(
            PlainDate.of(2032, 53, Weekday.MONDAY),
            is(PlainDate.of(2032, 12, 27)));
    }

    @Test
    public void ofWeekdate8() {
        assertThat(
            PlainDate.of(2032, 53, Weekday.TUESDAY),
            is(PlainDate.of(2032, 12, 28)));
    }

    @Test
    public void ofWeekdate9() {
        assertThat(
            PlainDate.of(2032, 53, Weekday.WEDNESDAY),
            is(PlainDate.of(2032, 12, 29)));
    }

    @Test
    public void ofWeekdate10() {
        assertThat(
            PlainDate.of(2032, 53, Weekday.THURSDAY),
            is(PlainDate.of(2032, 12, 30)));
    }

    @Test
    public void ofWeekdate11() {
        assertThat(
            PlainDate.of(2032, 53, Weekday.FRIDAY),
            is(PlainDate.of(2032, 12, 31)));
    }

    @Test
    public void ofWeekdate12() {
        assertThat(
            PlainDate.of(2032, 53, Weekday.SATURDAY),
            is(PlainDate.of(2033, 1, 1)));
    }

    @Test
    public void ofWeekdate13() {
        assertThat(
            PlainDate.of(2032, 53, Weekday.SUNDAY),
            is(PlainDate.of(2033, 1, 2)));
    }

    @Test
    public void ofWeekdate14() {
        assertThat(
            PlainDate.of(2033, 1, Weekday.MONDAY),
            is(PlainDate.of(2033, 1, 3)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofWeekdate15() {
        PlainDate.of(2032, 54, Weekday.MONDAY);
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofWeekdate16() {
        PlainDate.of(2032, 0, Weekday.MONDAY);
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofWeekdate17() {
        PlainDate.of(2012, 53, Weekday.MONDAY);
    }

    @Test
    public void hasCalendarSystem() {
        assertThat(PlainDate.axis().hasCalendarSystem(), is(true));
    }

    @Test
    public void ofEpochDays1() {
        assertThat(
            PlainDate.of(0, EpochDays.UTC),
            is(PlainDate.of(1972, 1, 1)));
    }

    @Test
    public void ofEpochDays2() {
        long max =
            Chronology.lookup(PlainDate.class)
                .getCalendarSystem().getMaximumSinceUTC();
        assertThat(
            PlainDate.of(max, EpochDays.UTC),
            is(PlainDate.of(999999999, 12, 31)));
        assertThat(EpochDays.UTC.getDefaultMaximum(), is(max));
    }

    @Test
    public void ofEpochDays3() {
        long min =
            Chronology.lookup(PlainDate.class)
                .getCalendarSystem().getMinimumSinceUTC();
        assertThat(
            PlainDate.of(min, EpochDays.UTC),
            is(PlainDate.of(-999999999, 1, 1)));
        assertThat(EpochDays.UTC.getDefaultMinimum(), is(min));
    }

    @Test(expected=IllegalArgumentException.class)
    public void merge() {
        PlainDate.axis().createFrom(
            Moment.UNIX_EPOCH, Attributes.empty(), false, false);
    }

    @Test
    public void mergeLax() {
        assertThat(
            PlainDate.axis().createFrom(
                Moment.UNIX_EPOCH,
                Attributes.empty(),
                true,
                false),
            is(PlainDate.of(1970, 1, 1)));
    }

    @Test
    public void fromLocalDate() {
        LocalDate input = LocalDate.of(2015, 5, 9);
        assertThat(
            PlainDate.from(input),
            is(PlainDate.of(2015, 5, 9))
        );
    }

}