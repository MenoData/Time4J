package net.time4j.range;

import net.time4j.ClockUnit;
import net.time4j.Moment;
import net.time4j.PlainTimestamp;

import java.text.ParseException;
import java.util.concurrent.TimeUnit;

import net.time4j.SI;
import net.time4j.format.expert.Iso8601Format;
import net.time4j.format.expert.IsoDateStyle;
import net.time4j.format.expert.IsoDecimalStyle;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


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

    @Test
    public void formatISO() {
        Moment start = PlainTimestamp.of(2016, 2, 22, 10, 45, 53).plus(120, ClockUnit.MILLIS).atUTC();
        Moment end = PlainTimestamp.of(2016, 2, 22, 16, 30, 27).plus(43, ClockUnit.MILLIS).atUTC();
        MomentInterval interval = MomentInterval.between(start, end);
        assertThat(
            interval.formatISO(
                IsoDateStyle.BASIC_CALENDAR_DATE,
                IsoDecimalStyle.DOT,
                ClockUnit.MILLIS,
                ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 2),
                InfinityStyle.SYMBOL),
            is("20160222T124553.120+0200/20160222T183027.043+0200"));
        assertThat(
            interval.formatISO(
                IsoDateStyle.EXTENDED_CALENDAR_DATE,
                IsoDecimalStyle.DOT,
                ClockUnit.MILLIS,
                ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 2),
                InfinityStyle.SYMBOL),
            is("2016-02-22T12:45:53.120+02:00/2016-02-22T18:30:27.043+02:00"));
    }

    @Test
    public void formatISOInfinity() {
        MomentInterval since = MomentInterval.since(PlainTimestamp.of(2016, 2, 28, 13, 20).atUTC());
        MomentInterval until = MomentInterval.until(PlainTimestamp.of(2016, 2, 28, 13, 20).atUTC());
        assertThat(
            since.formatISO(
                IsoDateStyle.EXTENDED_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MINUTES, ZonalOffset.UTC,
                InfinityStyle.SYMBOL),
            is("2016-02-28T13:20Z/+∞"));
        assertThat(
            since.formatISO(
                IsoDateStyle.EXTENDED_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MINUTES, ZonalOffset.UTC,
                InfinityStyle.HYPHEN),
            is("2016-02-28T13:20Z/-"));
        assertThat(
            since.formatISO(
                IsoDateStyle.EXTENDED_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MINUTES, ZonalOffset.UTC,
                InfinityStyle.MIN_MAX),
            is("2016-02-28T13:20Z/+999999999-12-31T23:59Z"));
        assertThat(
            until.formatISO(
                IsoDateStyle.EXTENDED_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MINUTES, ZonalOffset.UTC,
                InfinityStyle.SYMBOL),
            is("-∞/2016-02-28T13:20Z"));
        assertThat(
            until.formatISO(
                IsoDateStyle.EXTENDED_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MINUTES, ZonalOffset.UTC,
                InfinityStyle.HYPHEN),
            is("-/2016-02-28T13:20Z"));
        assertThat(
            until.formatISO(
                IsoDateStyle.EXTENDED_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MINUTES, ZonalOffset.UTC,
                InfinityStyle.MIN_MAX),
            is("-999999999-01-01T00:00Z/2016-02-28T13:20Z"));
    }

    @Test
    public void formatReducedSameYear() {
        Moment start = PlainTimestamp.of(2012, 6, 30, 23, 59, 59).plus(1, ClockUnit.MILLIS).atUTC().plus(1, SI.SECONDS);
        Moment end = PlainTimestamp.of(2012, 8, 2, 16, 30, 27).plus(120, ClockUnit.MILLIS).atUTC();
        MomentInterval interval = MomentInterval.between(start, end);
        assertThat(
            interval.formatReduced(
                IsoDateStyle.EXTENDED_CALENDAR_DATE,
                IsoDecimalStyle.DOT,
                ClockUnit.MILLIS,
                ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 2),
                InfinityStyle.SYMBOL),
            is("2012-07-01T01:59:60.001+02:00/08-02T18:30:27.120"));
    }

    @Test
    public void formatReducedSameMonth() {
        Moment start = PlainTimestamp.of(2012, 6, 2, 16, 30, 27).plus(120, ClockUnit.MILLIS).atUTC();
        Moment end = PlainTimestamp.of(2012, 6, 30, 23, 59, 59).plus(1, ClockUnit.MILLIS).atUTC().plus(1, SI.SECONDS);
        MomentInterval interval = MomentInterval.between(start, end);
        assertThat(
            interval.formatReduced(
                IsoDateStyle.EXTENDED_CALENDAR_DATE,
                IsoDecimalStyle.DOT,
                ClockUnit.MILLIS,
                ZonalOffset.ofHours(OffsetSign.BEHIND_UTC, 4),
                InfinityStyle.SYMBOL),
            is("2012-06-02T12:30:27.120-04:00/30T19:59:60.001"));
    }

    @Test
    public void formatReducedSameDate() {
        Moment start = PlainTimestamp.of(2012, 6, 30, 16, 30, 27).plus(120, ClockUnit.MILLIS).atUTC();
        Moment end = PlainTimestamp.of(2012, 6, 30, 23, 59, 59).plus(1, ClockUnit.MILLIS).atUTC().plus(1, SI.SECONDS);
        MomentInterval interval = MomentInterval.between(start, end);
        assertThat(
            interval.formatReduced(
                IsoDateStyle.BASIC_WEEK_DATE,
                IsoDecimalStyle.COMMA,
                ClockUnit.MILLIS,
                ZonalOffset.ofHours(OffsetSign.BEHIND_UTC, 4),
                InfinityStyle.SYMBOL),
            is("2012W266T123027,120-0400/T195960,001"));
        assertThat(
            interval.formatReduced(
                IsoDateStyle.EXTENDED_WEEK_DATE,
                IsoDecimalStyle.COMMA,
                ClockUnit.MILLIS,
                ZonalOffset.ofHours(OffsetSign.BEHIND_UTC, 4),
                InfinityStyle.SYMBOL),
            is("2012-W26-6T12:30:27,120-04:00/T19:59:60,001"));
    }

    @Test
    public void formatReducedInfinity() {
        MomentInterval since = MomentInterval.since(PlainTimestamp.of(2016, 2, 28, 13, 20).atUTC());
        MomentInterval until = MomentInterval.until(PlainTimestamp.of(2016, 2, 28, 13, 20).atUTC());
        assertThat(
            since.formatReduced(
                IsoDateStyle.EXTENDED_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MINUTES, ZonalOffset.UTC,
                InfinityStyle.SYMBOL),
            is("2016-02-28T13:20Z/+∞"));
        assertThat(
            since.formatReduced(
                IsoDateStyle.EXTENDED_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MINUTES, ZonalOffset.UTC,
                InfinityStyle.HYPHEN),
            is("2016-02-28T13:20Z/-"));
        assertThat(
            since.formatReduced(
                IsoDateStyle.EXTENDED_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MINUTES, ZonalOffset.UTC,
                InfinityStyle.MIN_MAX),
            is("2016-02-28T13:20Z/+999999999-12-31T23:59Z"));
        assertThat(
            until.formatReduced(
                IsoDateStyle.EXTENDED_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MINUTES, ZonalOffset.UTC,
                InfinityStyle.SYMBOL),
            is("-∞/2016-02-28T13:20Z"));
        assertThat(
            until.formatReduced(
                IsoDateStyle.EXTENDED_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MINUTES, ZonalOffset.UTC,
                InfinityStyle.HYPHEN),
            is("-/2016-02-28T13:20Z"));
        assertThat(
            until.formatReduced(
                IsoDateStyle.EXTENDED_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MINUTES, ZonalOffset.UTC,
                InfinityStyle.MIN_MAX),
            is("-999999999-01-01T00:00Z/2016-02-28T13:20Z"));
    }

    @Test
    public void parseInfinity() throws ParseException {
        Moment utc = PlainTimestamp.of(2015, 1, 1, 8, 45).atUTC();
        assertThat(
            MomentInterval.parseISO("2015-01-01T08:45Z/+∞"),
            is(MomentInterval.since(utc))
        );
        assertThat(
            MomentInterval.parse(
                "[2015-01-01T08:45Z/+∞)", Iso8601Format.EXTENDED_DATE_TIME_OFFSET, BracketPolicy.SHOW_ALWAYS),
            is(MomentInterval.since(utc))
        );
        assertThat(
            MomentInterval.parseISO("-∞/2015-01-01T08:45Z"),
            is(MomentInterval.until(utc))
        );
        assertThat(
            MomentInterval.parse(
                "(-∞/2015-01-01T08:45Z]", Iso8601Format.EXTENDED_DATE_TIME_OFFSET, BracketPolicy.SHOW_ALWAYS),
            is(MomentInterval.until(utc).withClosedEnd())
        );

        assertThat(
            MomentInterval.parseISO("2015-01-01T08:45Z/-"),
            is(MomentInterval.since(utc))
        );
        assertThat(
            MomentInterval.parse(
                "[2015-01-01T08:45Z/-)", Iso8601Format.EXTENDED_DATE_TIME_OFFSET, BracketPolicy.SHOW_ALWAYS),
            is(MomentInterval.since(utc))
        );
        assertThat(
            MomentInterval.parseISO("-/2015-01-01T08:45Z"),
            is(MomentInterval.until(utc))
        );
        assertThat(
            MomentInterval.parse(
                "(-/2015-01-01T08:45Z]", Iso8601Format.EXTENDED_DATE_TIME_OFFSET, BracketPolicy.SHOW_ALWAYS),
            is(MomentInterval.until(utc).withClosedEnd())
        );

        assertThat(
            MomentInterval.parseISO("2015-01-01T08:45Z/+999999999-12-31T23:59:59,999999999Z"),
            is(MomentInterval.since(utc))
        );
        assertThat(
            MomentInterval.parse(
                "[2015-01-01T08:45Z/+999999999-12-31T23:59:59,999999999Z)",
                Iso8601Format.EXTENDED_DATE_TIME_OFFSET,
                BracketPolicy.SHOW_ALWAYS),
            is(MomentInterval.since(utc))
        );
        assertThat(
            MomentInterval.parseISO("-999999999-01-01T00:00Z/2015-01-01T08:45Z"),
            is(MomentInterval.until(utc))
        );
        assertThat(
            MomentInterval.parse(
                "(-999999999-01-01T00:00Z/2015-01-01T08:45Z]",
                Iso8601Format.EXTENDED_DATE_TIME_OFFSET,
                BracketPolicy.SHOW_ALWAYS),
            is(MomentInterval.until(utc).withClosedEnd())
        );
    }

    @Test
    public void parseAlways() throws ParseException {
        MomentInterval always =
            MomentIntervalFactory.INSTANCE.between(Boundary.infinitePast(), Boundary.infiniteFuture());
        assertThat(
            MomentInterval.parseISO("-/-"),
            is(always));
        assertThat(
            MomentInterval.parse(
                "(-/-)",
                Iso8601Format.EXTENDED_DATE_TIME_OFFSET,
                BracketPolicy.SHOW_WHEN_NON_STANDARD),
            is(always));
        assertThat(
            MomentInterval.parseISO("-∞/+∞"),
            is(always));
        assertThat(
            MomentInterval.parse(
                "(-∞/+∞)",
                Iso8601Format.EXTENDED_DATE_TIME_OFFSET,
                BracketPolicy.SHOW_WHEN_NON_STANDARD),
            is(always));
        assertThat(
            MomentInterval.parseISO("-999999999-01-01T00:00Z/+999999999-12-31T23:59:59,999999999Z"),
            is(always));
        assertThat(
            MomentInterval.parse(
                "(-999999999-01-01T00:00Z/+999999999-12-31T23:59:59,999999999Z)",
                Iso8601Format.EXTENDED_DATE_TIME_OFFSET,
                BracketPolicy.SHOW_WHEN_NON_STANDARD),
            is(always));
    }
    
    @Test
    public void parseReducedISO1() throws ParseException {
        MomentInterval range = 
            MomentInterval.parseISO("2023-03-28T00:00:00+01:00/06:00");
        Moment start = PlainTimestamp.of(2023, 3, 27, 23, 0).atUTC();
        Moment end = PlainTimestamp.of(2023, 3, 28, 5, 0).atUTC();
        assertThat(
            range,
            is(MomentInterval.between(start, end)));
    }

    @Test
    public void parseReducedISO2() throws ParseException {
        MomentInterval range = 
            MomentInterval.parseISO("2023-03-28T00:00:00+01:00/2023-03-29T06:00");
        Moment start = PlainTimestamp.of(2023, 3, 27, 23, 0).atUTC();
        Moment end = PlainTimestamp.of(2023, 3, 29, 5, 0).atUTC();
        assertThat(
            range,
            is(MomentInterval.between(start, end)));
    }

    @Test(expected=ParseException.class)
    public void parseReducedISO3() throws ParseException {
        MomentInterval.parseISO("2023-03-28T00:00:00/06:00"); // missing time zone
    }

}