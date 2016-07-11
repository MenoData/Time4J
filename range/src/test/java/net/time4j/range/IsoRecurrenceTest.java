package net.time4j.range;

import net.time4j.CalendarUnit;
import net.time4j.Duration;
import net.time4j.PlainDate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
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

        List<DateInterval> intervals = new ArrayList<DateInterval>();
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

        List<DateInterval> intervals = new ArrayList<DateInterval>();
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

        List<DateInterval> intervals = new ArrayList<DateInterval>();
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
        for (DateInterval interval : recurrence) {
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
        assertThat(IsoRecurrence.parseDateIntervals("R87/2016-336/2016-366"), is(expected));
        assertThat(IsoRecurrence.parseDateIntervals("R87/20161201/20161231"), is(expected));
        assertThat(IsoRecurrence.parseDateIntervals("R87/2016-12-01/2016-12-31"), is(expected));
        assertThat(IsoRecurrence.parseDateIntervals("R87/2016W484/2016W526"), is(expected));
        assertThat(IsoRecurrence.parseDateIntervals("R87/2016-W48-4/2016-W52-6"), is(expected));
    }

    @Test(expected=ParseException.class)
    public void parseDateIntervalsWithDifferentPatterns() throws ParseException {
        IsoRecurrence.parseDateIntervals("R87/20161201/2016-12-31");
    }

}
