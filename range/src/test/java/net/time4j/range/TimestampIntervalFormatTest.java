package net.time4j.range;

import net.time4j.PlainTimestamp;

import java.text.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class TimestampIntervalFormatTest {

    @Test
    public void parseAbbreviatedWithoutDate() throws ParseException {
        PlainTimestamp start = PlainTimestamp.of(2012, 4, 30, 14, 15);
        PlainTimestamp end = PlainTimestamp.of(2012, 4, 30, 16, 0);
        TimestampInterval expected = TimestampInterval.between(start, end);

        assertThat(
            TimestampInterval.parseISO("2012-04-30T14:15/T16:00"),
            is(expected));
    }

    @Test
    public void parseAbbreviatedWithoutDateAnd24Hour() throws ParseException {
        PlainTimestamp start = PlainTimestamp.of(2012, 4, 30, 14, 15);
        PlainTimestamp end = PlainTimestamp.of(2012, 5, 1, 0, 0);
        TimestampInterval expected = TimestampInterval.between(start, end);

        assertThat(
            TimestampInterval.parseISO("2012-04-30T14:15/T24:00"),
            is(expected));
    }

    @Test
    public void parseAbbreviatedWithoutDateOrT() throws ParseException {
        PlainTimestamp start = PlainTimestamp.of(2012, 4, 30, 14, 15);
        PlainTimestamp end = PlainTimestamp.of(2012, 4, 30, 16, 0);
        TimestampInterval expected = TimestampInterval.between(start, end);

        assertThat(
            TimestampInterval.parseISO("2012-04-30T14:15/16:00"),
            is(expected));
    }

    @Test
    public void parseAbbreviatedExtendedOrdinalDate() throws ParseException {
        PlainTimestamp start = PlainTimestamp.of(2012, 4, 1, 14, 15);
        PlainTimestamp end = PlainTimestamp.of(2012, 4, 5, 16, 0);
        TimestampInterval expected = TimestampInterval.between(start, end);

        assertThat(
            TimestampInterval.parseISO("2012-092T14:15/096T16:00"),
            is(expected));
    }

    @Test
    public void parseAbbreviatedExtendedWeekDate() throws ParseException {
        PlainTimestamp start = PlainTimestamp.of(2012, 4, 1, 14, 15);
        PlainTimestamp end = PlainTimestamp.of(2012, 4, 5, 16, 0);
        TimestampInterval expected = TimestampInterval.between(start, end);

        assertThat(
            TimestampInterval.parseISO("2012-W13-7T14:15/W14-4T16:00"),
            is(expected));
    }

    @Test
    public void parseAbbreviatedBasicOrdinalDate() throws ParseException {
        PlainTimestamp start = PlainTimestamp.of(2012, 4, 1, 14, 15);
        PlainTimestamp end = PlainTimestamp.of(2012, 4, 5, 16, 0);
        TimestampInterval expected = TimestampInterval.between(start, end);

        assertThat(
            TimestampInterval.parseISO("2012092T1415/096T1600"),
            is(expected));
    }

    @Test
    public void parseAbbreviatedBasicWeekDate() throws ParseException {
        PlainTimestamp start = PlainTimestamp.of(2012, 4, 1, 14, 15);
        PlainTimestamp end = PlainTimestamp.of(2012, 4, 5, 16, 0);
        TimestampInterval expected = TimestampInterval.between(start, end);

        assertThat(
            TimestampInterval.parseISO("2012W137T1415/W144T1600"),
            is(expected));
    }

    @Test
    public void parseExtendedOrdinalDate() throws ParseException {
        PlainTimestamp start = PlainTimestamp.of(2012, 4, 1, 14, 15);
        PlainTimestamp end = PlainTimestamp.of(2012, 4, 5, 16, 0);
        TimestampInterval expected = TimestampInterval.between(start, end);

        assertThat(
            TimestampInterval.parseISO("2012-092T14:15/2012-096T16:00"),
            is(expected));
    }

    @Test
    public void parseExtendedWeekDate() throws ParseException {
        PlainTimestamp start = PlainTimestamp.of(2012, 4, 1, 14, 15);
        PlainTimestamp end = PlainTimestamp.of(2012, 4, 5, 16, 0);
        TimestampInterval expected = TimestampInterval.between(start, end);

        assertThat(
            TimestampInterval.parseISO("2012-W13-7T14:15/2012-W14-4T16:00"),
            is(expected));
    }

    @Test
    public void parseBasicOrdinalDate() throws ParseException {
        PlainTimestamp start = PlainTimestamp.of(2012, 4, 1, 14, 15);
        PlainTimestamp end = PlainTimestamp.of(2012, 4, 5, 16, 0);
        TimestampInterval expected = TimestampInterval.between(start, end);

        assertThat(
            TimestampInterval.parseISO("2012092T1415/2012096T1600"),
            is(expected));
    }

    @Test
    public void parseBasicWeekDate() throws ParseException {
        PlainTimestamp start = PlainTimestamp.of(2012, 4, 1, 14, 15);
        PlainTimestamp end = PlainTimestamp.of(2012, 4, 5, 16, 0);
        TimestampInterval expected = TimestampInterval.between(start, end);

        assertThat(
            TimestampInterval.parseISO("2012W137T1415/2012W144T1600"),
            is(expected));
    }

    @Test
    public void parseExtendedCalendarDateAndPeriod() throws ParseException {
        PlainTimestamp start = PlainTimestamp.of(2012, 4, 1, 14, 15);
        PlainTimestamp end = PlainTimestamp.of(2012, 4, 5, 16, 0);
        TimestampInterval expected = TimestampInterval.between(start, end);

        assertThat(
            TimestampInterval.parseISO("2012-04-01T14:15/P4DT1H45M"),
            is(expected));
    }

    @Test
    public void parseExtendedOrdinalDateAndPeriod() throws ParseException {
        PlainTimestamp start = PlainTimestamp.of(2012, 4, 1, 14, 15);
        PlainTimestamp end = PlainTimestamp.of(2012, 4, 5, 16, 0);
        TimestampInterval expected = TimestampInterval.between(start, end);

        assertThat(
            TimestampInterval.parseISO("2012-092T14:15/P4DT1H45M"),
            is(expected));
    }

    @Test
    public void parseExtendedWeekDateAndPeriod() throws ParseException {
        PlainTimestamp start = PlainTimestamp.of(2012, 4, 1, 14, 15);
        PlainTimestamp end = PlainTimestamp.of(2012, 4, 5, 16, 0);
        TimestampInterval expected = TimestampInterval.between(start, end);

        assertThat(
            TimestampInterval.parseISO("2012-W13-7T14:15/P0000-00-04T01:45"),
            is(expected));
    }

    @Test
    public void parsePeriodAndExtendedCalendarDate() throws ParseException {
        PlainTimestamp start = PlainTimestamp.of(2012, 4, 1, 14, 15);
        PlainTimestamp end = PlainTimestamp.of(2012, 4, 5, 16, 0);
        TimestampInterval expected = TimestampInterval.between(start, end);

        assertThat(
            TimestampInterval.parseISO("P4DT1H45M/2012-04-05T16:00"),
            is(expected));
    }

    @Test
    public void parsePeriodAndExtendedOrdinalDate() throws ParseException {
        PlainTimestamp start = PlainTimestamp.of(2012, 4, 1, 14, 15);
        PlainTimestamp end = PlainTimestamp.of(2012, 4, 5, 16, 0);
        TimestampInterval expected = TimestampInterval.between(start, end);

        assertThat(
            TimestampInterval.parseISO("P4DT1H45M/2012-096T16:00"),
            is(expected));
    }

    @Test
    public void parsePeriodAndExtendedWeekDate() throws ParseException {
        PlainTimestamp start = PlainTimestamp.of(2012, 4, 1, 14, 15);
        PlainTimestamp end = PlainTimestamp.of(2012, 4, 5, 16, 0);
        TimestampInterval expected = TimestampInterval.between(start, end);

        assertThat(
            TimestampInterval.parseISO("P0000-00-04T01:45/2012-W14-4T16:00"),
            is(expected));
    }

}