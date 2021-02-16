package net.time4j.range;

import net.time4j.CalendarUnit;
import net.time4j.ClockUnit;
import net.time4j.Duration;
import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;


@RunWith(JUnit4.class)
public class IsoRecurrenceTest {

    @Test
    public void dateIntervalsWithStartDuration() {
        IsoRecurrence<DateInterval> recurrence =
            IsoRecurrence.of(4, PlainDate.of(2016, 7, 1), Duration.of(1, CalendarUnit.MONTHS));
        assertThat(recurrence.isBackwards(), is(false));
        assertThat(recurrence.isEmpty(), is(false));
        assertThat(recurrence.isInfinite(), is(false));
        assertThat(recurrence.getCount(), is(4));
        assertThat(recurrence.toString(), is("R4/2016-07-01/P1M"));

        List<DateInterval> intervals = new ArrayList<>();
        for (DateInterval interval : recurrence) {
            intervals.add(interval);
        }
        assertThat(intervals.size(), is(4));
        assertThat(intervals.get(0), is(DateInterval.between(PlainDate.of(2016, 7, 1), PlainDate.of(2016, 7, 31))));
        assertThat(intervals.get(1), is(DateInterval.between(PlainDate.of(2016, 8, 1), PlainDate.of(2016, 8, 31))));
        assertThat(intervals.get(2), is(DateInterval.between(PlainDate.of(2016, 9, 1), PlainDate.of(2016, 9, 30))));
        assertThat(intervals.get(3), is(DateInterval.between(PlainDate.of(2016, 10, 1), PlainDate.of(2016, 10, 31))));
    }

    @Test(expected=IllegalArgumentException.class)
    public void dateIntervalsWithNegativeDuration() {
        IsoRecurrence.of(4, PlainDate.of(2016, 7, 1), Duration.of(1, CalendarUnit.MONTHS).inverse());
    }

    @Test(expected=IllegalArgumentException.class)
    public void dateIntervalsWithEmptyDuration() {
        IsoRecurrence.of(4, PlainDate.of(2016, 7, 1), Duration.of(0, CalendarUnit.MONTHS));
    }

    @Test
    public void dateIntervalsWithDurationEnd() {
        IsoRecurrence<DateInterval> recurrence =
            IsoRecurrence.of(
                4,
                Duration.of(1, CalendarUnit.MONTHS.keepingEndOfMonth()),
                PlainDate.of(2016, 4, 1).with(PlainDate.DAY_OF_MONTH.maximized()));
        assertThat(recurrence.isBackwards(), is(true));
        assertThat(recurrence.isEmpty(), is(false));
        assertThat(recurrence.isInfinite(), is(false));
        assertThat(recurrence.getCount(), is(4));
        assertThat(recurrence.toString(), is("R4/P1{M-KEEPING_LAST_DATE}/2016-04-30"));

        List<DateInterval> intervals = new ArrayList<>();
        for (DateInterval interval : recurrence) {
            intervals.add(interval);
        }
        assertThat(intervals.size(), is(4));
        assertThat(intervals.get(0), is(DateInterval.between(PlainDate.of(2016, 4, 1), PlainDate.of(2016, 4, 30))));
        assertThat(intervals.get(1), is(DateInterval.between(PlainDate.of(2016, 3, 1), PlainDate.of(2016, 3, 31))));
        assertThat(intervals.get(2), is(DateInterval.between(PlainDate.of(2016, 2, 1), PlainDate.of(2016, 2, 29))));
        assertThat(intervals.get(3), is(DateInterval.between(PlainDate.of(2016, 1, 1), PlainDate.of(2016, 1, 31))));
    }

    @Test
    public void dateIntervalsWithStartEnd() {
        IsoRecurrence<DateInterval> recurrence =
            IsoRecurrence.of(4, PlainDate.of(2016, 12, 1), PlainDate.of(2016, 12, 31));
        assertThat(recurrence.isBackwards(), is(false));
        assertThat(recurrence.isEmpty(), is(false));
        assertThat(recurrence.isInfinite(), is(false));
        assertThat(recurrence.getCount(), is(4));
        assertThat(recurrence.toString(), is("R4/2016-12-01/2016-12-31"));

        List<DateInterval> intervals = new ArrayList<>();
        for (DateInterval interval : recurrence) {
            intervals.add(interval);
        }
        assertThat(intervals.size(), is(4));
        assertThat(intervals.get(0), is(DateInterval.between(PlainDate.of(2016, 12, 1), PlainDate.of(2016, 12, 31))));
        assertThat(intervals.get(1), is(DateInterval.between(PlainDate.of(2017, 1, 1), PlainDate.of(2017, 1, 31))));
        assertThat(intervals.get(2), is(DateInterval.between(PlainDate.of(2017, 2, 1), PlainDate.of(2017, 2, 28))));
        assertThat(intervals.get(3), is(DateInterval.between(PlainDate.of(2017, 3, 1), PlainDate.of(2017, 3, 31))));
    }

    @Test
    public void empty() {
        IsoRecurrence<DateInterval> recurrence =
            IsoRecurrence.of(0, PlainDate.of(2016, 12, 1), PlainDate.of(2016, 12, 31));
        assertThat(recurrence.isEmpty(), is(true));
        assertThat(recurrence.getCount(), is(0));
        for (DateInterval interval : recurrence) {
            fail("Empty recurrence with interval: " + interval);
        }
    }

    @Test
    public void testEquality() {
        IsoRecurrence<DateInterval> recurrence1 =
            IsoRecurrence.of(4, PlainDate.of(2016, 12, 1), PlainDate.of(2016, 12, 31));
        IsoRecurrence<DateInterval> recurrence2 =
            IsoRecurrence.of(4, PlainDate.of(2016, 12, 1), Duration.of(1, CalendarUnit.MONTHS));
        IsoRecurrence<DateInterval> recurrence3 =
            IsoRecurrence.of(4, PlainDate.of(2016, 7, 1), Duration.of(1, CalendarUnit.MONTHS));
        IsoRecurrence<DateInterval> recurrence4 =
            IsoRecurrence.of(4, PlainDate.of(2016, 7, 1), Duration.of(1, CalendarUnit.MONTHS));
        assertThat(recurrence1.equals(recurrence1), is(true));
        assertThat(recurrence1.equals(null), is(false));
        assertThat(recurrence1.equals(recurrence2), is(false));
        assertThat(recurrence2.equals(recurrence3), is(false));
        assertThat(recurrence4.equals(recurrence3), is(true));
    }

    @Test
    public void withCount() {
        IsoRecurrence<DateInterval> recurrence =
            IsoRecurrence.of(4, PlainDate.of(2016, 12, 1), PlainDate.of(2016, 12, 31));
        IsoRecurrence<DateInterval> expected =
            IsoRecurrence.of(12, PlainDate.of(2016, 12, 1), PlainDate.of(2016, 12, 31));
        assertThat(recurrence.withCount(4) == recurrence, is(true));
        assertThat(recurrence.withCount(12), is(expected));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withInvalidCount() {
        IsoRecurrence<DateInterval> recurrence =
            IsoRecurrence.of(4, PlainDate.of(2016, 12, 1), PlainDate.of(2016, 12, 31));
        recurrence.withCount(-1);
    }

    @Test
    public void withInfiniteCount() {
        IsoRecurrence<DateInterval> recurrence =
            IsoRecurrence.of(4, PlainDate.of(2016, 12, 1), PlainDate.of(2016, 12, 31)).withInfiniteCount();
        assertThat(recurrence.isInfinite(), is(true));
        assertThat(recurrence.getCount(), is(-1));
        int index = 0;
        for (DateInterval ignored : recurrence) {
            index++;
            if (index > 5) { // arbitrary choice
                break; // prevents infinite loop
            }
        }
        assertThat(index, is(6));
    }

    @Test(expected=ParseException.class)
    public void parseInvalidSyntax1() throws ParseException {
        IsoRecurrence.parseDateIntervals("2016-12-01/2016-12-31");
    }

    @Test(expected=ParseException.class)
    public void parseInvalidSyntax2() throws ParseException {
        IsoRecurrence.parseDateIntervals("/2016-12-01/2016-12-31");
    }

    @Test(expected=ParseException.class)
    public void parseInvalidSyntax3() throws ParseException {
        IsoRecurrence.parseDateIntervals("S/2016-12-01/2016-12-31");
    }

    @Test(expected=ParseException.class)
    public void parseInvalidSyntax4() throws ParseException {
        IsoRecurrence.parseDateIntervals("RR/2016-12-01/2016-12-31");
    }

    @Test
    public void parseInfiniteDateIntervals() throws ParseException {
        IsoRecurrence<DateInterval> expected =
            IsoRecurrence.of(4, PlainDate.of(2016, 12, 1), PlainDate.of(2016, 12, 31)).withInfiniteCount();
        assertThat(IsoRecurrence.parseDateIntervals("R/2016-12-01/2016-12-31"), is(expected));
    }

    @Test
    public void parseDateIntervalsStartDuration() throws ParseException {
        IsoRecurrence<DateInterval> expected =
            IsoRecurrence.of(4, PlainDate.of(2016, 12, 1), Duration.of(1, CalendarUnit.MONTHS));
        assertThat(IsoRecurrence.parseDateIntervals("R4/20161201/P1M"), is(expected));
        assertThat(IsoRecurrence.parseDateIntervals("R4/2016-12-01/P1M"), is(expected));
    }

    @Test
    public void parseDateIntervalsDurationEnd() throws ParseException {
        IsoRecurrence<DateInterval> expected =
            IsoRecurrence.of(0, Duration.of(2, CalendarUnit.WEEKS), PlainDate.of(2016, 1, 31));
        assertThat(IsoRecurrence.parseDateIntervals("R0/P2W/2016W047"), is(expected));
        assertThat(IsoRecurrence.parseDateIntervals("R0/P2W/2016-W04-7"), is(expected));
    }

    @Test
    public void parseDateIntervalsStartEnd() throws ParseException {
        IsoRecurrence<DateInterval> expected =
            IsoRecurrence.of(87, PlainDate.of(2016, 12, 1), PlainDate.of(2016, 12, 31));
        assertThat(IsoRecurrence.parseDateIntervals("R87/2016336/2016366"), is(expected));
        assertThat(IsoRecurrence.parseDateIntervals("R87/2016336/366"), is(expected));

        assertThat(IsoRecurrence.parseDateIntervals("R87/2016-336/2016-366"), is(expected));
        assertThat(IsoRecurrence.parseDateIntervals("R87/2016-336/366"), is(expected));

        assertThat(IsoRecurrence.parseDateIntervals("R87/20161201/20161231"), is(expected));
        assertThat(IsoRecurrence.parseDateIntervals("R87/20161201/1231"), is(expected));

        assertThat(IsoRecurrence.parseDateIntervals("R87/2016-12-01/2016-12-31"), is(expected));
        assertThat(IsoRecurrence.parseDateIntervals("R87/2016-12-01/12-31"), is(expected));
        assertThat(IsoRecurrence.parseDateIntervals("R87/2016-12-01/31"), is(expected));

        assertThat(IsoRecurrence.parseDateIntervals("R87/2016W484/2016W526"), is(expected));
        assertThat(IsoRecurrence.parseDateIntervals("R87/2016W484/W526"), is(expected));

        assertThat(IsoRecurrence.parseDateIntervals("R87/2016-W48-4/2016-W52-6"), is(expected));
        assertThat(IsoRecurrence.parseDateIntervals("R87/2016-W48-4/W52-6"), is(expected));
    }

    @Test(expected=ParseException.class)
    public void parseDateIntervalsWithDifferentPatterns() throws ParseException {
        IsoRecurrence.parseDateIntervals("R87/20161201/2016-12-31");
    }

    @Test
    public void timestampIntervalsWithStartDuration() {
        IsoRecurrence<TimestampInterval> recurrence =
            IsoRecurrence.of(
                4,
                PlainTimestamp.of(2016, 7, 1, 10, 0),
                Duration.ofPositive().days(1).hours(6).build());
        assertThat(recurrence.isBackwards(), is(false));
        assertThat(recurrence.isEmpty(), is(false));
        assertThat(recurrence.isInfinite(), is(false));
        assertThat(recurrence.getCount(), is(4));
        assertThat(recurrence.toString(), is("R4/2016-07-01T10/P1DT6H"));

        List<TimestampInterval> intervals = new ArrayList<>();
        for (TimestampInterval interval : recurrence) {
            intervals.add(interval);
        }
        assertThat(intervals.size(), is(4));
        assertThat(
            intervals.get(0),
            is(TimestampInterval.between(PlainTimestamp.of(2016, 7, 1, 10, 0), PlainTimestamp.of(2016, 7, 2, 16, 0))));
        assertThat(
            intervals.get(1),
            is(TimestampInterval.between(PlainTimestamp.of(2016, 7, 2, 16, 0), PlainTimestamp.of(2016, 7, 3, 22, 0))));
        assertThat(
            intervals.get(2),
            is(TimestampInterval.between(PlainTimestamp.of(2016, 7, 3, 22, 0), PlainTimestamp.of(2016, 7, 5, 4, 0))));
        assertThat(
            intervals.get(3),
            is(TimestampInterval.between(PlainTimestamp.of(2016, 7, 5, 4, 0), PlainTimestamp.of(2016, 7, 6, 10, 0))));
    }

    @Test
    public void timestampIntervalsWithDurationEnd() {
        IsoRecurrence<TimestampInterval> recurrence =
            IsoRecurrence.of(
                4,
                Duration.ofPositive().days(1).hours(6).build(),
                PlainTimestamp.of(2016, 7, 6, 10, 0));
        assertThat(recurrence.isBackwards(), is(true));
        assertThat(recurrence.isEmpty(), is(false));
        assertThat(recurrence.isInfinite(), is(false));
        assertThat(recurrence.getCount(), is(4));
        assertThat(recurrence.toString(), is("R4/P1DT6H/2016-07-06T10"));

        List<TimestampInterval> intervals = new ArrayList<>();
        for (TimestampInterval interval : recurrence) {
            intervals.add(interval);
        }
        assertThat(intervals.size(), is(4));
        assertThat(
            intervals.get(3),
            is(TimestampInterval.between(PlainTimestamp.of(2016, 7, 1, 10, 0), PlainTimestamp.of(2016, 7, 2, 16, 0))));
        assertThat(
            intervals.get(2),
            is(TimestampInterval.between(PlainTimestamp.of(2016, 7, 2, 16, 0), PlainTimestamp.of(2016, 7, 3, 22, 0))));
        assertThat(
            intervals.get(1),
            is(TimestampInterval.between(PlainTimestamp.of(2016, 7, 3, 22, 0), PlainTimestamp.of(2016, 7, 5, 4, 0))));
        assertThat(
            intervals.get(0),
            is(TimestampInterval.between(PlainTimestamp.of(2016, 7, 5, 4, 0), PlainTimestamp.of(2016, 7, 6, 10, 0))));
    }

    @Test
    public void timestampIntervalsWithStartEnd() {
        IsoRecurrence<TimestampInterval> recurrence =
            IsoRecurrence.of(
                4,
                PlainTimestamp.of(2016, 7, 1, 10, 0),
                PlainTimestamp.of(2016, 7, 2, 16, 0));
        assertThat(recurrence.isBackwards(), is(false));
        assertThat(recurrence.isEmpty(), is(false));
        assertThat(recurrence.isInfinite(), is(false));
        assertThat(recurrence.getCount(), is(4));
        assertThat(recurrence.toString(), is("R4/2016-07-01T10/2016-07-02T16"));

        List<TimestampInterval> intervals = new ArrayList<>();
        for (TimestampInterval interval : recurrence) {
            intervals.add(interval);
        }
        assertThat(intervals.size(), is(4));
        assertThat(
            intervals.get(0),
            is(TimestampInterval.between(PlainTimestamp.of(2016, 7, 1, 10, 0), PlainTimestamp.of(2016, 7, 2, 16, 0))));
        assertThat(
            intervals.get(1),
            is(TimestampInterval.between(PlainTimestamp.of(2016, 7, 2, 16, 0), PlainTimestamp.of(2016, 7, 3, 22, 0))));
        assertThat(
            intervals.get(2),
            is(TimestampInterval.between(PlainTimestamp.of(2016, 7, 3, 22, 0), PlainTimestamp.of(2016, 7, 5, 4, 0))));
        assertThat(
            intervals.get(3),
            is(TimestampInterval.between(PlainTimestamp.of(2016, 7, 5, 4, 0), PlainTimestamp.of(2016, 7, 6, 10, 0))));
    }

    @Test
    public void parseTimestampIntervals() throws ParseException {
        IsoRecurrence<TimestampInterval> expected =
            IsoRecurrence.of(
                87,
                PlainTimestamp.of(2016, 7, 1, 10, 15, 59),
                PlainTimestamp.of(2016, 7, 2, 16, 45, 0).plus(123, ClockUnit.MILLIS));
        assertThat(IsoRecurrence.parseTimestampIntervals("R87/20160701T101559/20160702T164500.123"), is(expected));
        assertThat(IsoRecurrence.parseTimestampIntervals("R87/2016-07-01T10:15:59/2016-07-02T16:45:00.123"), is(expected));
        assertThat(IsoRecurrence.parseTimestampIntervals("R87/2016-07-01T10:15:59/02T16:45:00.123"), is(expected));
        expected =
            IsoRecurrence.of(
                9,
                PlainTimestamp.of(2016, 7, 1, 10, 15, 59),
                PlainTimestamp.of(2016, 7, 1, 16, 45, 0).plus(123, ClockUnit.MILLIS));
        assertThat(IsoRecurrence.parseTimestampIntervals("R9/2016-07-01T10:15:59/T16:45:00.123"), is(expected));
        assertThat(IsoRecurrence.parseTimestampIntervals("R9/2016-07-01T10:15:59/16:45:00.123"), is(expected));
    }

    @Test
    public void parseMomentIntervals() throws ParseException {
        IsoRecurrence<MomentInterval> expected =
            IsoRecurrence.of(
                87,
                PlainTimestamp.of(2016, 7, 1, 10, 15, 59).atUTC(),
                PlainTimestamp.of(2016, 7, 2, 16, 45, 0).plus(123, ClockUnit.MILLIS).atUTC(),
                ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 2));
        assertThat(
            IsoRecurrence.parseMomentIntervals("R87/20160701T121559+0200/20160702T184500.123"), is(expected));
        assertThat(
            IsoRecurrence.parseMomentIntervals("R87/2016-07-01T12:15:59+02:00/2016-07-02T16:45:00.123Z"), is(expected));
        expected =
            IsoRecurrence.of(
                87,
                PlainTimestamp.of(2016, 7, 1, 10, 15, 59).atUTC(),
                Duration.ofPositive().days(1).hours(6).minutes(29).seconds(59).millis(123).build(),
                ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 2));
        assertThat(
            IsoRecurrence.parseMomentIntervals("R87/2016-07-01T12:15:59+02:00/P1DT6H29M59,123000000S"), is(expected));
        expected =
            IsoRecurrence.of(
                2,
                PlainTimestamp.of(2016, 7, 1, 10, 15, 59).atUTC(),
                PlainTimestamp.of(2016, 7, 1, 16, 45, 0).plus(123, ClockUnit.MILLIS).atUTC(),
                ZonalOffset.UTC);
        assertThat(IsoRecurrence.parseMomentIntervals("R2/2016-07-01T10:15:59Z/T16:45:00.123"), is(expected));
        assertThat(IsoRecurrence.parseMomentIntervals("R2/2016-07-01T10:15:59Z/16:45:00.123"), is(expected));
    }

    @Test
    public void testToString() {
        IsoRecurrence<DateInterval> dateIntervalIsoRecurrence =
            IsoRecurrence.of(0, Duration.of(2, CalendarUnit.WEEKS), PlainDate.of(2016, 1, 31)).withInfiniteCount();
        assertThat(
            dateIntervalIsoRecurrence.toString(),
            is("R/P2W/2016-01-31"));
        IsoRecurrence<TimestampInterval> timestampIntervalIsoRecurrence =
            IsoRecurrence.of(
                6,
                PlainTimestamp.of(2016, 7, 1, 10, 15, 59),
                Duration.ofPositive().months(2).days(4).hours(12).build());
        assertThat(
            timestampIntervalIsoRecurrence.toString(),
            is("R6/2016-07-01T10:15:59/P2M4DT12H"));
        IsoRecurrence<MomentInterval> momentIntervalIsoRecurrence =
            IsoRecurrence.of(
                87,
                PlainTimestamp.of(2016, 7, 1, 10, 15, 59).atUTC(),
                PlainTimestamp.of(2016, 7, 2, 16, 45, 0).plus(123, ClockUnit.MILLIS).atUTC(),
                ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 2));
        assertThat(
            momentIntervalIsoRecurrence.toString(),
            is("R87/2016-07-01T12:15:59+02:00/2016-07-02T18:45:00,123+02:00"));
    }

    @Test
    public void intervalStream() {
        IsoRecurrence<DateInterval> recurrence =
            IsoRecurrence.of(4, PlainDate.of(2016, 7, 1), Duration.of(1, CalendarUnit.MONTHS));
        List<DateInterval> expected = new ArrayList<>();
        for (DateInterval interval : recurrence) {
            expected.add(interval);
        }
        assertThat(recurrence.intervalStream().collect(Collectors.toList()), is(expected));
        assertThat(recurrence.intervalStream().parallel().collect(Collectors.toList()), is(expected));
    }

}
