package net.time4j.range;

import net.time4j.ClockUnit;
import net.time4j.PlainTimestamp;
import net.time4j.format.expert.Iso8601Format;
import net.time4j.format.expert.IsoDateStyle;
import net.time4j.format.expert.IsoDecimalStyle;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


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

    @Test
    public void formatISO() {
        PlainTimestamp start = PlainTimestamp.of(2016, 2, 22, 10, 45, 53).plus(120, ClockUnit.MILLIS);
        PlainTimestamp end = PlainTimestamp.of(2016, 2, 22, 16, 30);
        TimestampInterval interval = TimestampInterval.between(start, end);
        assertThat(
            interval.formatISO(
                IsoDateStyle.BASIC_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MILLIS, InfinityStyle.SYMBOL),
            is("20160222T104553.120/20160222T163000.000"));
        assertThat(
            interval.formatISO(
                IsoDateStyle.EXTENDED_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MILLIS, InfinityStyle.SYMBOL),
            is("2016-02-22T10:45:53.120/2016-02-22T16:30:00.000"));
    }

    @Test
    public void formatISOInfinity() {
        TimestampInterval since = TimestampInterval.since(PlainTimestamp.of(2016, 2, 28, 13, 20));
        TimestampInterval until = TimestampInterval.until(PlainTimestamp.of(2016, 2, 28, 13, 20));
        assertThat(
            since.formatISO(
                IsoDateStyle.EXTENDED_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MINUTES, InfinityStyle.SYMBOL),
            is("2016-02-28T13:20/+∞"));
        assertThat(
            since.formatISO(
                IsoDateStyle.EXTENDED_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MINUTES, InfinityStyle.HYPHEN),
            is("2016-02-28T13:20/-"));
        assertThat(
            since.formatISO(
                IsoDateStyle.EXTENDED_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MINUTES, InfinityStyle.MIN_MAX),
            is("2016-02-28T13:20/+999999999-12-31T23:59"));
        assertThat(
            until.formatISO(
                IsoDateStyle.EXTENDED_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MINUTES, InfinityStyle.SYMBOL),
            is("-∞/2016-02-28T13:20"));
        assertThat(
            until.formatISO(
                IsoDateStyle.EXTENDED_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MINUTES, InfinityStyle.HYPHEN),
            is("-/2016-02-28T13:20"));
        assertThat(
            until.formatISO(
                IsoDateStyle.EXTENDED_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MINUTES, InfinityStyle.MIN_MAX),
            is("-999999999-01-01T00:00/2016-02-28T13:20"));
    }

    @Test
    public void formatReducedSameMonth() {
        PlainTimestamp start = PlainTimestamp.of(2016, 2, 22, 10, 45);
        PlainTimestamp end = PlainTimestamp.of(2016, 2, 29, 16, 30);
        TimestampInterval interval = TimestampInterval.between(start, end);
        assertThat(
            interval.formatReduced(
                IsoDateStyle.BASIC_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MINUTES, InfinityStyle.SYMBOL),
            is("20160222T1045/29T1630"));
        assertThat(
            interval.formatReduced(
                IsoDateStyle.EXTENDED_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MINUTES, InfinityStyle.SYMBOL),
            is("2016-02-22T10:45/29T16:30"));
    }

    @Test
    public void formatReducedSameDate() {
        PlainTimestamp start = PlainTimestamp.of(2016, 2, 22, 10, 45, 53);
        PlainTimestamp end = PlainTimestamp.of(2016, 2, 22, 16, 30);
        TimestampInterval interval = TimestampInterval.between(start, end);
        assertThat(
            interval.formatReduced(
                IsoDateStyle.BASIC_CALENDAR_DATE,
                IsoDecimalStyle.DOT,
                ClockUnit.MINUTES,
                InfinityStyle.SYMBOL),
            is("20160222T1045/T1630"));
        assertThat(
            interval.formatReduced(
                IsoDateStyle.EXTENDED_CALENDAR_DATE,
                IsoDecimalStyle.DOT,
                ClockUnit.MINUTES,
                InfinityStyle.SYMBOL),
            is("2016-02-22T10:45/T16:30"));
    }

    @Test
    public void formatReducedInfinity() {
        TimestampInterval since = TimestampInterval.since(PlainTimestamp.of(2016, 2, 28, 13, 20));
        TimestampInterval until = TimestampInterval.until(PlainTimestamp.of(2016, 2, 28, 13, 20));
        assertThat(
            since.formatReduced(
                IsoDateStyle.EXTENDED_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MINUTES, InfinityStyle.SYMBOL),
            is("2016-02-28T13:20/+∞"));
        assertThat(
            since.formatReduced(
                IsoDateStyle.EXTENDED_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MINUTES, InfinityStyle.HYPHEN),
            is("2016-02-28T13:20/-"));
        assertThat(
            since.formatReduced(
                IsoDateStyle.EXTENDED_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MINUTES, InfinityStyle.MIN_MAX),
            is("2016-02-28T13:20/+999999999-12-31T23:59"));
        assertThat(
            until.formatReduced(
                IsoDateStyle.EXTENDED_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MINUTES, InfinityStyle.SYMBOL),
            is("-∞/2016-02-28T13:20"));
        assertThat(
            until.formatReduced(
                IsoDateStyle.EXTENDED_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MINUTES, InfinityStyle.HYPHEN),
            is("-/2016-02-28T13:20"));
        assertThat(
            until.formatReduced(
                IsoDateStyle.EXTENDED_CALENDAR_DATE, IsoDecimalStyle.DOT, ClockUnit.MINUTES, InfinityStyle.MIN_MAX),
            is("-999999999-01-01T00:00/2016-02-28T13:20"));
    }

    @Test
    public void parseInfinity() throws ParseException {
        PlainTimestamp tsp = PlainTimestamp.of(2015, 1, 1, 8, 45);
        assertThat(
            TimestampInterval.parseISO("2015-01-01T08:45/+∞"),
            is(TimestampInterval.since(tsp))
        );
        assertThat(
            TimestampInterval.parse(
                "[2015-01-01T08:45/+∞)", Iso8601Format.EXTENDED_DATE_TIME, BracketPolicy.SHOW_ALWAYS),
            is(TimestampInterval.since(tsp))
        );
        assertThat(
            TimestampInterval.parseISO("-∞/2015-01-01T08:45"),
            is(TimestampInterval.until(tsp))
        );
        assertThat(
            TimestampInterval.parse(
                "(-∞/2015-01-01T08:45]", Iso8601Format.EXTENDED_DATE_TIME, BracketPolicy.SHOW_ALWAYS),
            is(TimestampInterval.until(tsp).withClosedEnd())
        );

        assertThat(
            TimestampInterval.parseISO("2015-01-01T08:45/-"),
            is(TimestampInterval.since(tsp))
        );
        assertThat(
            TimestampInterval.parse(
                "[2015-01-01T08:45/-)", Iso8601Format.EXTENDED_DATE_TIME, BracketPolicy.SHOW_ALWAYS),
            is(TimestampInterval.since(tsp))
        );
        assertThat(
            TimestampInterval.parseISO("-/2015-01-01T08:45"),
            is(TimestampInterval.until(tsp))
        );
        assertThat(
            TimestampInterval.parse(
                "(-/2015-01-01T08:45]", Iso8601Format.EXTENDED_DATE_TIME, BracketPolicy.SHOW_ALWAYS),
            is(TimestampInterval.until(tsp).withClosedEnd())
        );

        assertThat(
            TimestampInterval.parseISO("2015-01-01T08:45/+999999999-12-31T23:59:59,999999999"),
            is(TimestampInterval.since(tsp))
        );
        assertThat(
            TimestampInterval.parse(
                "[2015-01-01T08:45/+999999999-12-31T23:59:59,999999999)",
                Iso8601Format.EXTENDED_DATE_TIME,
                BracketPolicy.SHOW_ALWAYS),
            is(TimestampInterval.since(tsp))
        );
        assertThat(
            TimestampInterval.parseISO("-999999999-01-01T00:00/2015-01-01T08:45"),
            is(TimestampInterval.until(tsp))
        );
        assertThat(
            TimestampInterval.parse(
                "(-999999999-01-01T00:00/2015-01-01T08:45]",
                Iso8601Format.EXTENDED_DATE_TIME,
                BracketPolicy.SHOW_ALWAYS),
            is(TimestampInterval.until(tsp).withClosedEnd())
        );
    }

    @Test
    public void parseAlways() throws ParseException {
        TimestampInterval always =
            TimestampIntervalFactory.INSTANCE.between(Boundary.infinitePast(), Boundary.infiniteFuture());
        assertThat(
            TimestampInterval.parseISO("-/-"),
            is(always));
        assertThat(
            TimestampInterval.parse(
                "(-/-)",
                Iso8601Format.EXTENDED_DATE_TIME,
                BracketPolicy.SHOW_WHEN_NON_STANDARD),
            is(always));
        assertThat(
            TimestampInterval.parseISO("-∞/+∞"),
            is(always));
        assertThat(
            TimestampInterval.parse(
                "(-∞/+∞)",
                Iso8601Format.EXTENDED_DATE_TIME,
                BracketPolicy.SHOW_WHEN_NON_STANDARD),
            is(always));
        assertThat(
            TimestampInterval.parseISO("-999999999-01-01T00:00/+999999999-12-31T23:59:59,999999999"),
            is(always));
        assertThat(
            TimestampInterval.parse(
                "(-999999999-01-01T00:00/+999999999-12-31T23:59:59,999999999)",
                Iso8601Format.EXTENDED_DATE_TIME,
                BracketPolicy.SHOW_WHEN_NON_STANDARD),
            is(always));
    }

}