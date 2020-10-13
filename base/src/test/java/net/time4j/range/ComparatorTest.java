package net.time4j.range;

import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.CalendarUnit.DAYS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class ComparatorTest {

    @Test
    public void compareTimestampIntervals() {
        PlainTimestamp start = PlainTimestamp.of(2014, 2, 27, 0, 0);
        PlainTimestamp end = PlainTimestamp.of(2014, 5, 14, 0, 0);
        TimestampInterval a = TimestampInterval.between(start, end);
        TimestampInterval b =
            TimestampInterval.between(start, end.plus(1, DAYS));
        List<TimestampInterval> intervals = new ArrayList<TimestampInterval>();
        intervals.add(b);
        intervals.add(a);

        assertThat(
            intervals.get(0).equals(b),
            is(true));
        assertThat(
            intervals.get(1).equals(a),
            is(true));

        Collections.sort(intervals, TimestampInterval.comparator());

        assertThat(
            intervals.get(0).equals(a),
            is(true));
        assertThat(
            intervals.get(1).equals(b),
            is(true));
    }

    @Test
    public void compareDateIntervalsWithEarlierStart() {
        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.of(2014, 5, 14);
        DateInterval a = DateInterval.between(start.minus(1, DAYS), end);
        DateInterval b = DateInterval.between(start, end.minus(1, DAYS));
        List<DateInterval> intervals = new ArrayList<DateInterval>();
        intervals.add(b);
        intervals.add(a);

        assertThat(
            intervals.get(0).equals(b),
            is(true));
        assertThat(
            intervals.get(1).equals(a),
            is(true));

        Collections.sort(intervals, DateInterval.comparator());

        assertThat(
            intervals.get(0).equals(a),
            is(true));
        assertThat(
            intervals.get(1).equals(b),
            is(true));
    }

    @Test
    public void compareDateIntervalsWithLaterStart() {
        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.of(2014, 5, 14);
        DateInterval a = DateInterval.between(start.plus(1, DAYS), end);
        DateInterval b = DateInterval.between(start, end.plus(1, DAYS));
        List<DateInterval> intervals = new ArrayList<DateInterval>();
        intervals.add(a);
        intervals.add(b);

        assertThat(
            intervals.get(0).equals(a),
            is(true));
        assertThat(
            intervals.get(1).equals(b),
            is(true));

        Collections.sort(intervals, DateInterval.comparator());

        assertThat(
            intervals.get(0).equals(b),
            is(true));
        assertThat(
            intervals.get(1).equals(a),
            is(true));
    }

    @Test
    public void compareDateIntervalsWithSameStartLaterEnd() {
        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.of(2014, 5, 14);
        DateInterval a = DateInterval.between(start, end);
        DateInterval b = DateInterval.between(start, end.plus(1, DAYS));
        List<DateInterval> intervals = new ArrayList<DateInterval>();
        intervals.add(b);
        intervals.add(a);

        assertThat(
            intervals.get(0).equals(b),
            is(true));
        assertThat(
            intervals.get(1).equals(a),
            is(true));

        Collections.sort(intervals, DateInterval.comparator());

        assertThat(
            intervals.get(0).equals(a),
            is(true));
        assertThat(
            intervals.get(1).equals(b),
            is(true));
    }

    @Test
    public void compareDateIntervalsWithSameStartEarlierEnd() {
        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.of(2014, 5, 14);
        DateInterval a = DateInterval.between(start, end);
        DateInterval b = DateInterval.between(start, end.minus(1, DAYS));
        List<DateInterval> intervals = new ArrayList<DateInterval>();
        intervals.add(a);
        intervals.add(b);

        assertThat(
            intervals.get(0).equals(a),
            is(true));
        assertThat(
            intervals.get(1).equals(b),
            is(true));

        Collections.sort(intervals, DateInterval.comparator());

        assertThat(
            intervals.get(0).equals(b),
            is(true));
        assertThat(
            intervals.get(1).equals(a),
            is(true));
    }

    @Test
    public void compareDateIntervalsWithMaxEnd() {
        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.axis().getMaximum();
        DateInterval a = DateInterval.between(start, end);
        DateInterval b = DateInterval.between(start, end).withOpenEnd();
        List<DateInterval> intervals = new ArrayList<DateInterval>();
        intervals.add(a);
        intervals.add(b);

        assertThat(
            intervals.get(0).equals(a),
            is(true));
        assertThat(
            intervals.get(1).equals(b),
            is(true));

        Collections.sort(intervals, DateInterval.comparator());

        assertThat(
            intervals.get(0).equals(b),
            is(true));
        assertThat(
            intervals.get(1).equals(a),
            is(true));
    }

    @Test
    public void compareTimestampIntervalsWithMaxEnd() {
        PlainTimestamp start = PlainTimestamp.of(2014, 2, 27, 0, 0);
        PlainTimestamp end = PlainTimestamp.axis().getMaximum();
        TimestampInterval a =
            TimestampInterval.between(start, end);
        TimestampInterval b =
            TimestampInterval.between(start, end).withClosedEnd();
        List<TimestampInterval> intervals = new ArrayList<TimestampInterval>();
        intervals.add(b);
        intervals.add(a);

        assertThat(
            intervals.get(0).equals(b),
            is(true));
        assertThat(
            intervals.get(1).equals(a),
            is(true));

        Collections.sort(intervals, TimestampInterval.comparator());

        assertThat(
            intervals.get(0).equals(a),
            is(true));
        assertThat(
            intervals.get(1).equals(b),
            is(true));
    }

}