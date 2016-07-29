package net.time4j.range;

import net.time4j.Moment;
import net.time4j.PlainTimestamp;

import java.text.ParseException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class MomentIntervalFormatTest {

    @Test
    public void parseAbbreviatedWithoutDate() throws ParseException {
        Moment start = PlainTimestamp.of(2012, 4, 30, 14, 15).atUTC();
        Moment end = PlainTimestamp.of(2012, 4, 30, 16, 0).atUTC();
        MomentInterval expected = MomentInterval.between(start, end);

        assertThat(
            MomentInterval.parseISO("2012-04-30T14:15Z/T16:00:01"),
            is(expected.withEnd(end.plus(1, TimeUnit.SECONDS))));
        assertThat(
            MomentInterval.parseISO("2012-04-30T14:15Z/T16:00"),
            is(expected));
        assertThat(
            MomentInterval.parseISO("2012-04-30T14:15Z/T16"),
            is(expected));
        assertThat(
            MomentInterval.parseISO("2012-04-30T14:15Z/T16Z"),
            is(expected));
        assertThat(
            MomentInterval.parseISO("2012-04-30T14:15Z/T16+00:00"),
            is(expected));
        assertThat(
            MomentInterval.parseISO("2012-04-30T14:15Z/T16:00:00+00"),
            is(expected));
    }

    @Test
    public void parseAbbreviatedWithoutDateOrT() throws ParseException {
        Moment start = PlainTimestamp.of(2012, 4, 30, 14, 15).atUTC();
        Moment end = PlainTimestamp.of(2012, 4, 30, 16, 0).atUTC();
        MomentInterval expected = MomentInterval.between(start, end);

        assertThat(
            MomentInterval.parseISO("2012-04-30T14:15Z/16:00:01"),
            is(expected.withEnd(end.plus(1, TimeUnit.SECONDS))));
        assertThat(
            MomentInterval.parseISO("2012-04-30T14:15Z/16:00"),
            is(expected));
        assertThat(
            MomentInterval.parseISO("2012-04-30T14:15Z/16"),
            is(expected));
        assertThat(
            MomentInterval.parseISO("2012-04-30T14:15Z/16Z"),
            is(expected));
        assertThat(
            MomentInterval.parseISO("2012-04-30T14:15Z/16+00:00"),
            is(expected));
        assertThat(
            MomentInterval.parseISO("2012-04-30T14:15Z/16:00:00+00"),
            is(expected));
    }

    @Test
    public void parseAbbreviatedExtendedOrdinalDate() throws ParseException {
        Moment start = PlainTimestamp.of(2012, 4, 1, 14, 15).atUTC();
        Moment end = PlainTimestamp.of(2012, 4, 5, 16, 0).atUTC();
        MomentInterval expected = MomentInterval.between(start, end);

        assertThat(
            MomentInterval.parseISO("2012-092T14:15Z/096T16:00"),
            is(expected));
    }

    @Test
    public void parseAbbreviatedExtendedWeekDate() throws ParseException {
        Moment start = PlainTimestamp.of(2012, 4, 1, 14, 15, 0).atUTC();
        Moment end = PlainTimestamp.of(2012, 4, 5, 16, 0, 1).atUTC();
        MomentInterval expected = MomentInterval.between(start, end);

        assertThat(
            MomentInterval.parseISO("2012-W13-7T14:15:00Z/W14-4T16:00:01"),
            is(expected));
        assertThat(
            MomentInterval.parseISO("2012-W13-7T14:15Z/W14-4T16:00:01"),
            is(expected));
    }

    @Test
    public void parseAbbreviatedBasicOrdinalDate() throws ParseException {
        Moment start = PlainTimestamp.of(2012, 4, 1, 14, 15).atUTC();
        Moment end = PlainTimestamp.of(2012, 4, 5, 16, 0).atUTC();
        MomentInterval expected = MomentInterval.between(start, end);

        assertThat(
            MomentInterval.parseISO("2012092T1415Z/096T1600"),
            is(expected));
    }

    @Test
    public void parseAbbreviatedBasicWeekDate() throws ParseException {
        Moment start = PlainTimestamp.of(2012, 4, 1, 14, 15).atUTC();
        Moment end = PlainTimestamp.of(2012, 4, 5, 16, 0).atUTC();
        MomentInterval expected = MomentInterval.between(start, end);

        assertThat(
            MomentInterval.parseISO("2012W137T1415Z/W144T1600"),
            is(expected));
    }

    @Test
    public void parseExtendedOrdinalDate() throws ParseException {
        Moment start = PlainTimestamp.of(2012, 4, 1, 14, 15).atUTC();
        Moment end = PlainTimestamp.of(2012, 4, 5, 16, 0).atUTC();
        MomentInterval expected = MomentInterval.between(start, end);

        assertThat(
            MomentInterval.parseISO("2012-092T14:15Z/2012-096T16:00"),
            is(expected));
    }

    @Test
    public void parseExtendedWeekDate() throws ParseException {
        Moment start = PlainTimestamp.of(2012, 4, 1, 14, 15).atUTC();
        Moment end = PlainTimestamp.of(2012, 4, 5, 16, 0).atUTC();
        MomentInterval expected = MomentInterval.between(start, end);

        assertThat(
            MomentInterval.parseISO("2012-W13-7T14:15Z/2012-W14-4T16:00"),
            is(expected));
    }

    @Test
    public void parseBasicOrdinalDate() throws ParseException {
        Moment start = PlainTimestamp.of(2012, 4, 1, 14, 15).atUTC();
        Moment end = PlainTimestamp.of(2012, 4, 5, 16, 0).atUTC();
        MomentInterval expected = MomentInterval.between(start, end);

        assertThat(
            MomentInterval.parseISO("2012092T1415Z/2012096T1600"),
            is(expected));
    }

    @Test
    public void parseBasicWeekDate() throws ParseException {
        Moment start = PlainTimestamp.of(2012, 4, 1, 14, 15).atUTC();
        Moment end = PlainTimestamp.of(2012, 4, 5, 16, 0).atUTC();
        MomentInterval expected = MomentInterval.between(start, end);

        assertThat(
            MomentInterval.parseISO("2012W137T1415Z/2012W144T1600"),
            is(expected));
    }

    @Test
    public void parseExtendedCalendarDateAndPeriod() throws ParseException {
        Moment start = PlainTimestamp.of(2012, 4, 1, 14, 15).atUTC();
        Moment end = PlainTimestamp.of(2012, 4, 5, 16, 0).atUTC();
        MomentInterval expected = MomentInterval.between(start, end);

        assertThat(
            MomentInterval.parseISO("2012-04-01T14:15Z/P4DT1H45M"),
            is(expected));
    }

    @Test
    public void parseExtendedOrdinalDateAndPeriod() throws ParseException {
        Moment start = PlainTimestamp.of(2012, 4, 1, 14, 15).atUTC();
        Moment end = PlainTimestamp.of(2012, 4, 5, 16, 0).atUTC();
        MomentInterval expected = MomentInterval.between(start, end);

        assertThat(
            MomentInterval.parseISO("2012-092T14:15Z/P4DT1H45M"),
            is(expected));
    }

    @Test
    public void parseExtendedWeekDateAndPeriod() throws ParseException {
        Moment start = PlainTimestamp.of(2012, 4, 1, 14, 15).atUTC();
        Moment end = PlainTimestamp.of(2012, 4, 5, 16, 0).atUTC();
        MomentInterval expected = MomentInterval.between(start, end);

        assertThat(
            MomentInterval.parseISO("2012-W13-7T14:15Z/P0000-00-04T01:45"),
            is(expected));
    }

    @Test
    public void parsePeriodAndExtendedCalendarDate() throws ParseException {
        Moment start = PlainTimestamp.of(2012, 4, 1, 14, 15).atUTC();
        Moment end = PlainTimestamp.of(2012, 4, 5, 16, 0).atUTC();
        MomentInterval expected = MomentInterval.between(start, end);

        assertThat(
            MomentInterval.parseISO("P4DT1H45M/2012-04-05T16:00Z"),
            is(expected));
    }

    @Test
    public void parsePeriodAndExtendedOrdinalDate() throws ParseException {
        Moment start = PlainTimestamp.of(2012, 4, 1, 14, 15).atUTC();
        Moment end = PlainTimestamp.of(2012, 4, 5, 16, 0).atUTC();
        MomentInterval expected = MomentInterval.between(start, end);

        assertThat(
            MomentInterval.parseISO("P4DT1H45M/2012-096T16:00Z"),
            is(expected));
    }

    @Test
    public void parsePeriodAndExtendedWeekDate() throws ParseException {
        Moment start = PlainTimestamp.of(2012, 4, 1, 14, 15).atUTC();
        Moment end = PlainTimestamp.of(2012, 4, 5, 16, 0).atUTC();
        MomentInterval expected = MomentInterval.between(start, end);

        assertThat(
            MomentInterval.parseISO("P0000-00-04T01:45/2012-W14-4T16:00Z"),
            is(expected));
    }

    @Test
    public void parseAbbreviatedWithoutDateUTCplus2() throws ParseException {
        Moment start = PlainTimestamp.of(2012, 4, 30, 14, 15).atUTC();
        Moment end = PlainTimestamp.of(2012, 4, 30, 16, 0).atUTC();
        MomentInterval expected = MomentInterval.between(start, end);

        assertThat(
            MomentInterval.parseISO("2012-04-30T14:15Z/T18:00+02"),
            is(expected));
    }

    @Test
    public void parseAbbreviatedWithoutDateOrTUTCplus2() throws ParseException {
        Moment start = PlainTimestamp.of(2012, 4, 30, 14, 15).atUTC();
        Moment end = PlainTimestamp.of(2012, 4, 30, 16, 0).atUTC();
        MomentInterval expected = MomentInterval.between(start, end);

        assertThat(
            MomentInterval.parseISO("2012-04-30T14:15Z/T18:00+02"),
            is(expected));
    }

    @Test
    public void parseAbbreviatedExtendedOrdinalDateUTCplus1()
        throws ParseException {
        Moment start = PlainTimestamp.of(2012, 4, 1, 14, 15).atUTC();
        Moment end = PlainTimestamp.of(2012, 4, 5, 16, 0).atUTC();
        MomentInterval expected = MomentInterval.between(start, end);

        assertThat(
            MomentInterval.parseISO("2012-092T14:15Z/096T17:00+01"),
            is(expected));
    }

    @Test
    public void parseAbbreviatedExtendedWeekDateIndiaStandardTime()
        throws ParseException {
        Moment start = PlainTimestamp.of(2012, 4, 1, 14, 15).atUTC();
        Moment end = PlainTimestamp.of(2012, 4, 5, 16, 0).atUTC();
        MomentInterval expected = MomentInterval.between(start, end);

        assertThat(
            MomentInterval.parseISO("2012-W13-7T14:15Z/W14-4T21:30+05:30"),
            is(expected));
    }

    @Test
    public void parseAbbreviatedBasicOrdinalDateUTC()
        throws ParseException {
        Moment start = PlainTimestamp.of(2012, 4, 1, 14, 15).atUTC();
        Moment end = PlainTimestamp.of(2012, 4, 5, 16, 0).atUTC();
        MomentInterval expected = MomentInterval.between(start, end);

        assertThat(
            MomentInterval.parseISO("2012092T1415Z/096T1600Z"),
            is(expected));
    }

    @Test
    public void parseAbbreviatedBasicWeekDateUTCplus2() throws ParseException {
        Moment start = PlainTimestamp.of(2012, 4, 1, 14, 15).atUTC();
        Moment end = PlainTimestamp.of(2012, 4, 5, 16, 0).atUTC();
        MomentInterval expected = MomentInterval.between(start, end);

        assertThat(
            MomentInterval.parseISO("2012W137T1415Z/W144T1800+02"),
            is(expected));
    }

    @Test
    public void parseExtendedOrdinalDateUTCminus9()
        throws ParseException {
        Moment start = PlainTimestamp.of(2012, 4, 1, 14, 15).atUTC();
        Moment end = PlainTimestamp.of(2012, 4, 5, 16, 0).atUTC();
        MomentInterval expected = MomentInterval.between(start, end);

        assertThat(
            MomentInterval.parseISO("2012-092T14:15Z/2012-096T07:00-09:00"),
            is(expected));
    }

    @Test
    public void parseExtendedWeekDateUTCplus2() throws ParseException {
        Moment start = PlainTimestamp.of(2012, 4, 1, 14, 15).atUTC();
        Moment end = PlainTimestamp.of(2012, 4, 5, 16, 0).atUTC();
        MomentInterval expected = MomentInterval.between(start, end);

        assertThat(
            MomentInterval.parseISO("2012-W13-7T14:15Z/2012-W14-4T18:00+02:00"),
            is(expected));
    }

    @Test
    public void parseBasicOrdinalDateUTC() throws ParseException {
        Moment start = PlainTimestamp.of(2012, 4, 1, 14, 15).atUTC();
        Moment end = PlainTimestamp.of(2012, 4, 5, 16, 0).atUTC();
        MomentInterval expected = MomentInterval.between(start, end);

        assertThat(
            MomentInterval.parseISO("2012092T1415Z/2012096T1600Z"),
            is(expected));
    }

    @Test
    public void parseBasicWeekDateUTCplus2() throws ParseException {
        Moment start = PlainTimestamp.of(2012, 4, 1, 14, 15).atUTC();
        Moment end = PlainTimestamp.of(2012, 4, 5, 16, 0).atUTC();
        MomentInterval expected = MomentInterval.between(start, end);

        assertThat(
            MomentInterval.parseISO("2012W137T1415Z/2012W144T1800+0200"),
            is(expected));
    }

}