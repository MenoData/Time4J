package net.time4j.range;

import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.ClockUnit.NANOS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class IntervalCollectionTest {

    @Test
    public void getIntervals() {
        DateInterval i1 =
            DateInterval.between(
                PlainDate.of(2014, 2, 28),
                PlainDate.of(2014, 5, 31));
        DateInterval i2 =
            DateInterval.between(
                PlainDate.of(2014, 2, 27),
                PlainDate.of(2014, 6, 1));
        List<ChronoInterval<PlainDate>> intervals =
            new ArrayList<ChronoInterval<PlainDate>>();
        intervals.add(i1);
        intervals.add(i2);
        intervals = // sorted!
            IntervalCollection.onDateAxis().plus(intervals).getIntervals();
        assertThat(
            intervals.get(0).equals(i2),
            is(true));
        assertThat(
            intervals.get(1).equals(i1),
            is(true));
        assertThat(
            intervals.size(),
            is(2));
    }

    @Test(expected=NoSuchElementException.class)
    public void getMinimumEmpty() {
        IntervalCollection.onDateAxis().getMinimum();
    }

    @Test(expected=NoSuchElementException.class)
    public void getMaximumEmpty() {
        IntervalCollection.onDateAxis().getMaximum();
    }

    @Test
    public void getMinimum() {
        DateInterval i1 =
            DateInterval.between(
                PlainDate.of(2014, 2, 28),
                PlainDate.of(2014, 5, 31));
        DateInterval i2 =
            DateInterval.between(
                PlainDate.of(2014, 2, 27),
                PlainDate.of(2014, 6, 1));
        List<ChronoInterval<PlainDate>> intervals =
            new ArrayList<ChronoInterval<PlainDate>>();
        intervals.add(i1);
        intervals.add(i2);
        assertThat(
            IntervalCollection.onDateAxis().plus(intervals).getMinimum(),
            is(PlainDate.of(2014, 2, 27)));
    }

    @Test
    public void getMinimumPast() {
        DateInterval i1 =
            DateInterval.until(PlainDate.of(2014, 2, 28));
        DateInterval i2 =
            DateInterval.between(
                PlainDate.of(2014, 2, 27),
                PlainDate.of(2014, 6, 1));
        List<ChronoInterval<PlainDate>> intervals =
            new ArrayList<ChronoInterval<PlainDate>>();
        intervals.add(i1);
        intervals.add(i2);
        assertThat(
            IntervalCollection.onDateAxis().plus(intervals).getMinimum(),
            nullValue());
    }

    @Test
    public void getMaximum1() {
        DateInterval i1 =
            DateInterval.between(
                PlainDate.of(2014, 2, 28),
                PlainDate.of(2014, 5, 31));
        DateInterval i2 =
            DateInterval.between(
                PlainDate.of(2014, 2, 27),
                PlainDate.of(2014, 6, 1));
        List<ChronoInterval<PlainDate>> intervals =
            new ArrayList<ChronoInterval<PlainDate>>();
        intervals.add(i1);
        intervals.add(i2);
        assertThat(
            IntervalCollection.onDateAxis().plus(intervals).getMaximum(),
            is(PlainDate.of(2014, 6, 1)));
    }

    @Test
    public void getMaximum2() {
        DateInterval i1 =
            DateInterval.between(
                PlainDate.of(2014, 2, 28),
                PlainDate.of(2014, 5, 31));
        DateInterval i2 =
            DateInterval.between(
                PlainDate.of(2014, 2, 27),
                PlainDate.of(2014, 6, 4)).withOpenEnd();
        DateInterval i3 =
            DateInterval.between(
                PlainDate.of(2014, 2, 27),
                PlainDate.of(2014, 6, 1));
        List<ChronoInterval<PlainDate>> intervals =
            new ArrayList<ChronoInterval<PlainDate>>();
        intervals.add(i1);
        intervals.add(i2);
        intervals.add(i3);
        assertThat(
            IntervalCollection.onDateAxis().plus(intervals).getMaximum(),
            is(PlainDate.of(2014, 6, 3)));
    }

    @Test
    public void getMaximumTSP1() {
        TimestampInterval i1 =
            TimestampInterval.between(
                PlainDate.of(2014, 2, 28).atStartOfDay(),
                PlainDate.of(2014, 5, 31).atStartOfDay());
        TimestampInterval i2 =
            TimestampInterval.between(
                PlainDate.of(2014, 2, 27).atStartOfDay(),
                PlainDate.of(2014, 6, 4).atStartOfDay());
        TimestampInterval i3 =
            TimestampInterval.between(
                PlainDate.of(2014, 2, 27).atStartOfDay(),
                PlainDate.of(2014, 6, 1).atStartOfDay());
        List<ChronoInterval<PlainTimestamp>> intervals =
            new ArrayList<ChronoInterval<PlainTimestamp>>();
        intervals.add(i1);
        intervals.add(i2);
        intervals.add(i3);
        assertThat(
            IntervalCollection.onTimestampAxis().plus(intervals).getMaximum(),
            is(PlainDate.of(2014, 6, 4).atStartOfDay().minus(1, NANOS)));
    }

    @Test
    public void getMaximumTSP2() {
        TimestampInterval i1 =
            TimestampInterval.between(
                PlainDate.of(2014, 2, 28).atStartOfDay(),
                PlainDate.of(2014, 5, 31).atStartOfDay());
        TimestampInterval i2 =
            TimestampInterval.between(
                PlainDate.of(2014, 2, 27).atStartOfDay(),
                PlainDate.of(2014, 6, 4).atStartOfDay()).withClosedEnd();
        TimestampInterval i3 =
            TimestampInterval.between(
                PlainDate.of(2014, 2, 27).atStartOfDay(),
                PlainDate.of(2014, 6, 1).atStartOfDay());
        List<ChronoInterval<PlainTimestamp>> intervals =
            new ArrayList<ChronoInterval<PlainTimestamp>>();
        intervals.add(i1);
        intervals.add(i2);
        intervals.add(i3);
        assertThat(
            IntervalCollection.onTimestampAxis().plus(intervals).getMaximum(),
            is(PlainDate.of(2014, 6, 4).atStartOfDay()));
    }

    @Test
    public void getMaximumFuture() {
        DateInterval i1 =
            DateInterval.since(PlainDate.of(2014, 2, 28));
        DateInterval i2 =
            DateInterval.between(
                PlainDate.of(2014, 2, 27),
                PlainDate.of(2014, 6, 1));
        List<ChronoInterval<PlainDate>> intervals =
            new ArrayList<ChronoInterval<PlainDate>>();
        intervals.add(i1);
        intervals.add(i2);
        assertThat(
            IntervalCollection.onDateAxis().plus(intervals).getMaximum(),
            nullValue());
    }

    @Test
    public void gapsWithFuture() {
        DateInterval i1 =
            DateInterval.between(
                PlainDate.of(2014, 2, 28),
                PlainDate.of(2014, 3, 31));
        DateInterval i2 =
            DateInterval.since(PlainDate.of(2014, 4, 10));
        DateInterval i3 =
            DateInterval.between(
                PlainDate.of(2014, 4, 11),
                PlainDate.of(2014, 4, 15));
        DateInterval i4 =
            DateInterval.between(
                PlainDate.of(2014, 6, 15),
                PlainDate.of(2014, 6, 30));
        IntervalCollection<PlainDate> windows = IntervalCollection.onDateAxis();
        windows = windows.plus(i1).plus(i2).plus(i3).plus(i4);
        List<ChronoInterval<PlainDate>> gaps = windows.withGaps().getIntervals();
        ChronoInterval<PlainDate> expected =
            DateInterval.between(
                PlainDate.of(2014, 4, 1),
                PlainDate.of(2014, 4, 9));

        assertThat(gaps.size(), is(1));
        assertThat(gaps.get(0), is(expected));
    }

    @Test
    public void gapsNoOverlaps() {
        DateInterval i1 =
            DateInterval.between(
                PlainDate.of(2014, 4, 1),
                PlainDate.of(2014, 4, 5));
        DateInterval i2 =
            DateInterval.between(
                PlainDate.of(2014, 4, 10),
                PlainDate.of(2014, 6, 1));
        DateInterval i3 =
            DateInterval.between(
                PlainDate.of(2014, 6, 15),
                PlainDate.of(2014, 6, 30));
        IntervalCollection<PlainDate> windows = IntervalCollection.onDateAxis();
        windows = windows.plus(i1).plus(i2).plus(i3);
        List<ChronoInterval<PlainDate>> gaps = windows.withGaps().getIntervals();
        ChronoInterval<PlainDate> expected1 =
            DateInterval.between(
                PlainDate.of(2014, 4, 6),
                PlainDate.of(2014, 4, 9));
        ChronoInterval<PlainDate> expected2 =
            DateInterval.between(
                PlainDate.of(2014, 6, 2),
                PlainDate.of(2014, 6, 14));

        assertThat(gaps.size(), is(2));
        assertThat(gaps.get(0), is(expected1));
        assertThat(gaps.get(1), is(expected2));
    }

    @Test
    public void gapsWithOverlaps() {
        DateInterval i1 =
            DateInterval.between(
                PlainDate.of(2014, 2, 28),
                PlainDate.of(2014, 5, 31));
        DateInterval i2 =
            DateInterval.between(
                PlainDate.of(2014, 4, 1),
                PlainDate.of(2014, 4, 5));
        DateInterval i3 =
            DateInterval.between(
                PlainDate.of(2014, 4, 10),
                PlainDate.of(2014, 6, 1));
        DateInterval i4 =
            DateInterval.between(
                PlainDate.of(2014, 6, 15),
                PlainDate.of(2014, 6, 30));
        IntervalCollection<PlainDate> windows = IntervalCollection.onDateAxis();
        windows = windows.plus(i1).plus(i2).plus(i3).plus(i4);
        List<ChronoInterval<PlainDate>> gaps = windows.withGaps().getIntervals();
        ChronoInterval<PlainDate> expected =
            DateInterval.between(
                PlainDate.of(2014, 6, 2),
                PlainDate.of(2014, 6, 14));

        assertThat(gaps.size(), is(1));
        assertThat(gaps.get(0), is(expected));
    }

    @Test
    public void blocksWithOneGap() {
        DateInterval i1 =
            DateInterval.between(
                PlainDate.of(2014, 2, 28),
                PlainDate.of(2014, 5, 31));
        DateInterval i2 =
            DateInterval.between(
                PlainDate.of(2014, 4, 1),
                PlainDate.of(2014, 4, 5));
        DateInterval i3 =
            DateInterval.between(
                PlainDate.of(2014, 4, 10),
                PlainDate.of(2014, 6, 1));
        DateInterval i4 =
            DateInterval.between(
                PlainDate.of(2014, 6, 15),
                PlainDate.of(2014, 6, 30));
        IntervalCollection<PlainDate> windows = IntervalCollection.onDateAxis();
        windows = windows.plus(i1).plus(i2).plus(i3).plus(i4);
        List<ChronoInterval<PlainDate>> blocks =
            windows.withBlocks().getIntervals();
        ChronoInterval<PlainDate> first =
            DateInterval.between(
                PlainDate.of(2014, 2, 28),
                PlainDate.of(2014, 6, 1));
        ChronoInterval<PlainDate> second =
            DateInterval.between(
                PlainDate.of(2014, 6, 15),
                PlainDate.of(2014, 6, 30));

        assertThat(blocks.size(), is(2));
        assertThat(blocks.get(0), is(first));
        assertThat(blocks.get(1), is(second));
    }

    @Test
    public void blocksFuture() {
        DateInterval i1 =
            DateInterval.since(PlainDate.of(2014, 2, 28));
        DateInterval i2 =
            DateInterval.since(PlainDate.of(2014, 4, 1));
        DateInterval i3 =
            DateInterval.since(PlainDate.of(2014, 3, 10));
        IntervalCollection<PlainDate> windows = IntervalCollection.onDateAxis();
        windows = windows.plus(i1).plus(i2).plus(i3);

        List<ChronoInterval<PlainDate>> blocks =
            windows.withBlocks().getIntervals();
        ChronoInterval<PlainDate> expected = i1;

        assertThat(blocks.size(), is(1));
        assertThat(blocks.get(0), is(expected));
    }

    @Test
    public void blocksIfSingleInterval() {
        DateInterval interval =
            DateInterval.since(PlainDate.of(2014, 2, 28));
        IntervalCollection<PlainDate> windows = IntervalCollection.onDateAxis();
        windows = windows.plus(interval);

        List<ChronoInterval<PlainDate>> blocks =
            windows.withBlocks().getIntervals();
        ChronoInterval<PlainDate> expected = interval;

        assertThat(blocks.size(), is(1));
        assertThat(blocks.get(0), is(expected));
    }

    @Test
    public void blocksIfNoInterval() {
        IntervalCollection<PlainDate> windows = IntervalCollection.onDateAxis();
        assertThat(windows.withBlocks().isEmpty(), is(true));
    }

    @Test
    public void noIntersectionIfNoIntervals() {
        IntervalCollection<PlainDate> windows = IntervalCollection.onDateAxis();
        assertThat(windows.withIntersection().isEmpty(), is(true));
    }

    @Test
    public void noIntersectionIfGapExists() {
        DateInterval i1 =
            DateInterval.between(
                PlainDate.of(2014, 2, 28),
                PlainDate.of(2014, 5, 31));
        DateInterval i2 =
            DateInterval.between(
                PlainDate.of(2014, 4, 1),
                PlainDate.of(2014, 4, 5));
        DateInterval i3 =
            DateInterval.between(
                PlainDate.of(2014, 4, 10),
                PlainDate.of(2014, 6, 1));
        DateInterval i4 =
            DateInterval.between(
                PlainDate.of(2014, 6, 15),
                PlainDate.of(2014, 6, 30));
        IntervalCollection<PlainDate> windows = IntervalCollection.onDateAxis();
        windows = windows.plus(i1).plus(i2).plus(i3).plus(i4);

        assertThat(windows.withIntersection().isEmpty(), is(true));
    }

    @Test
    public void intersectionNormal() {
        DateInterval i1 =
            DateInterval.between(
                PlainDate.of(2014, 2, 28),
                PlainDate.of(2014, 5, 31));
        DateInterval i2 =
            DateInterval.between(
                PlainDate.of(2014, 4, 1),
                PlainDate.of(2014, 4, 15));
        DateInterval i3 =
            DateInterval.between(
                PlainDate.of(2014, 4, 10),
                PlainDate.of(2014, 6, 1));
        IntervalCollection<PlainDate> windows = IntervalCollection.onDateAxis();
        windows = windows.plus(i1).plus(i2).plus(i3);

        List<ChronoInterval<PlainDate>> overlap =
            windows.withIntersection().getIntervals();
        ChronoInterval<PlainDate> expected =
            DateInterval.between(
                PlainDate.of(2014, 4, 10),
                PlainDate.of(2014, 4, 15));

        assertThat(overlap.size(), is(1));
        assertThat(overlap.get(0), is(expected));
    }

    @Test
    public void intersectionFuture() {
        DateInterval i1 =
            DateInterval.since(PlainDate.of(2014, 2, 28));
        DateInterval i2 =
            DateInterval.since(PlainDate.of(2014, 4, 1));
        DateInterval i3 =
            DateInterval.since(PlainDate.of(2014, 3, 10));
        IntervalCollection<PlainDate> windows = IntervalCollection.onDateAxis();
        windows = windows.plus(i1).plus(i2).plus(i3);

        List<ChronoInterval<PlainDate>> overlap =
            windows.withIntersection().getIntervals();
        ChronoInterval<PlainDate> expected = i2;

        assertThat(overlap.size(), is(1));
        assertThat(overlap.get(0), is(expected));
    }

    @Test
    public void noGapsIfSingleInterval() {
        DateInterval interval =
            DateInterval.between(
                PlainDate.of(2014, 4, 10),
                PlainDate.of(2014, 6, 1));
        IntervalCollection<PlainDate> windows = IntervalCollection.onDateAxis();
        windows = windows.plus(interval);

        assertThat(windows.withGaps().isEmpty(), is(true));
    }

    @Test
    public void noGapsIfNoIntervals() {
        IntervalCollection<PlainDate> windows = IntervalCollection.onDateAxis();
        assertThat(windows.withGaps().isEmpty(), is(true));
    }

    @Test
    public void timeWindowIfNoInterval() throws ParseException {
        IntervalCollection<PlainDate> coll = IntervalCollection.onDateAxis();
        ChronoInterval<PlainDate> window =
            DateInterval.parseISO("2012-06-30/2014-12-31");
        assertThat(coll.withTimeWindow(window), is(coll));
    }

    @Test
    public void timeWindowIfEnclosing() throws ParseException {
        IntervalCollection<PlainDate> coll =
            IntervalCollection.onDateAxis()
            .plus(DateInterval.parseISO("2013-06-30/2013-12-31"))
            .plus(DateInterval.parseISO("2014-06-30/2014-07-31"))
            .plus(DateInterval.parseISO("2012-09-01/2014-09-30"));
        ChronoInterval<PlainDate> window =
            DateInterval.parseISO("2012-06-30/2014-12-31");
        assertThat(coll.withTimeWindow(window), is(coll));
    }

    @Test
    public void timeWindowIfOverlap1() throws ParseException {
        IntervalCollection<PlainDate> coll =
            IntervalCollection.onDateAxis()
            .plus(DateInterval.parseISO("2011-01-01/2013-12-31"))
            .plus(DateInterval.parseISO("2014-06-30/2014-07-31"))
            .plus(DateInterval.parseISO("2012-09-01/2015-09-30"));
        ChronoInterval<PlainDate> window =
            DateInterval.parseISO("2012-06-30/2014-12-31");
        IntervalCollection<PlainDate> expected =
            IntervalCollection.onDateAxis()
            .plus(DateInterval.parseISO("2012-06-30/2013-12-31"))
            .plus(DateInterval.parseISO("2014-06-30/2014-07-31"))
            .plus(DateInterval.parseISO("2012-09-01/2014-12-31"));
        assertThat(coll.withTimeWindow(window), is(expected));
    }

    @Test
    public void timeWindowIfOverlap2() throws ParseException {
        IntervalCollection<PlainDate> coll =
            IntervalCollection.onDateAxis()
            .plus(DateInterval.parseISO("2011-01-01/2013-12-31"))
            .plus(DateInterval.parseISO("2014-06-30/2015-01-01"))
            .plus(DateInterval.parseISO("2012-09-01/2015-09-30"));
        ChronoInterval<PlainDate> window =
            DateInterval.parseISO("2012-06-30/2014-12-31");
        IntervalCollection<PlainDate> expected =
            IntervalCollection.onDateAxis()
            .plus(DateInterval.parseISO("2012-06-30/2013-12-31"))
            .plus(DateInterval.parseISO("2014-06-30/2014-12-31"))
            .plus(DateInterval.parseISO("2012-09-01/2014-12-31"));
        assertThat(coll.withTimeWindow(window), is(expected));
    }

    @Test
    public void complementIfOverlap1() throws ParseException {
        IntervalCollection<PlainDate> coll =
            IntervalCollection.onDateAxis()
            .plus(DateInterval.parseISO("2011-01-01/2013-12-31"))
            .plus(DateInterval.parseISO("2014-06-30/2015-01-01"))
            .plus(DateInterval.parseISO("2014-09-01/2015-09-30"));
        ChronoInterval<PlainDate> window =
            DateInterval.parseISO("2012-06-30/2014-12-31");
        IntervalCollection<PlainDate> expected =
            IntervalCollection.onDateAxis()
            .plus(DateInterval.parseISO("2014-01-01/2014-06-29"));
        assertThat(coll.withComplement(window), is(expected));
    }

    @Test
    public void complementIfOverlap2() throws ParseException {
        IntervalCollection<PlainDate> coll =
            IntervalCollection.onDateAxis()
            .plus(DateInterval.parseISO("2011-01-01/2013-12-31"))
            .plus(DateInterval.parseISO("2014-06-30/2014-08-01"))
            .plus(DateInterval.parseISO("2014-09-01/2015-09-30"));
        ChronoInterval<PlainDate> window =
            DateInterval.parseISO("2012-06-30/2014-12-31");
        IntervalCollection<PlainDate> expected =
            IntervalCollection.onDateAxis()
            .plus(DateInterval.parseISO("2014-01-01/2014-06-29"))
            .plus(DateInterval.parseISO("2014-08-02/2014-08-31"));
        assertThat(coll.withComplement(window), is(expected));
    }

    @Test
    public void complementIfOverlap3() throws ParseException {
        IntervalCollection<PlainDate> coll =
            IntervalCollection.onDateAxis()
            .plus(DateInterval.parseISO("2011-01-01/2013-12-31"))
            .plus(DateInterval.parseISO("2014-06-30/2014-08-01"))
            .plus(DateInterval.parseISO("2012-09-01/2015-09-30"));
        ChronoInterval<PlainDate> window =
            DateInterval.parseISO("2012-06-30/2014-12-31");
        IntervalCollection<PlainDate> expected =
            IntervalCollection.onDateAxis();
        assertThat(coll.withComplement(window), is(expected));
    }

    @Test
    public void complementIfBigWindow() throws ParseException {
        IntervalCollection<PlainDate> coll =
            IntervalCollection.onDateAxis()
            .plus(DateInterval.parseISO("2011-01-01/2013-12-31"))
            .plus(DateInterval.parseISO("2014-06-30/2014-08-01"))
            .plus(DateInterval.parseISO("2014-09-01/2015-09-30"));
        ChronoInterval<PlainDate> window =
            DateInterval.parseISO("2010-06-30/2015-12-31");
        IntervalCollection<PlainDate> expected =
            IntervalCollection.onDateAxis()
            .plus(DateInterval.parseISO("2010-06-30/2010-12-31"))
            .plus(DateInterval.parseISO("2014-01-01/2014-06-29"))
            .plus(DateInterval.parseISO("2014-08-02/2014-08-31"))
            .plus(DateInterval.parseISO("2015-10-01/2015-12-31"));
        assertThat(coll.withComplement(window), is(expected));
    }

    @Test
    public void complementIfPastWindow() throws ParseException {
        IntervalCollection<PlainDate> coll =
            IntervalCollection.onDateAxis()
            .plus(DateInterval.parseISO("2011-01-01/2013-12-31"))
            .plus(DateInterval.parseISO("2014-06-30/2014-08-01"))
            .plus(DateInterval.parseISO("2014-09-01/2015-09-30"));
        ChronoInterval<PlainDate> window =
            DateInterval.until(PlainDate.of(2015, 12, 31));
        IntervalCollection<PlainDate> expected =
            IntervalCollection.onDateAxis()
            .plus(DateInterval.until(PlainDate.of(2010, 12, 31)))
            .plus(DateInterval.parseISO("2014-01-01/2014-06-29"))
            .plus(DateInterval.parseISO("2014-08-02/2014-08-31"))
            .plus(DateInterval.parseISO("2015-10-01/2015-12-31"));
        assertThat(coll.withComplement(window), is(expected));
    }

    @Test
    public void complementIfFutureWindow() throws ParseException {
        IntervalCollection<PlainDate> coll =
            IntervalCollection.onDateAxis()
            .plus(DateInterval.parseISO("2011-01-01/2013-12-31"))
            .plus(DateInterval.parseISO("2014-06-30/2014-08-01"))
            .plus(DateInterval.parseISO("2014-09-01/2015-09-30"));
        ChronoInterval<PlainDate> window =
            DateInterval.since(PlainDate.of(2010, 6, 30));
        IntervalCollection<PlainDate> expected =
            IntervalCollection.onDateAxis()
            .plus(DateInterval.parseISO("2010-06-30/2010-12-31"))
            .plus(DateInterval.parseISO("2014-01-01/2014-06-29"))
            .plus(DateInterval.parseISO("2014-08-02/2014-08-31"))
            .plus(DateInterval.since(PlainDate.of(2015, 10, 1)));
        assertThat(coll.withComplement(window), is(expected));
    }

    @Test
    public void complementIfEmpty() throws ParseException {
        IntervalCollection<PlainDate> empty =
            IntervalCollection.onDateAxis();
        ChronoInterval<PlainDate> window =
            DateInterval.parseISO("2012-06-30/2014-12-31");
        IntervalCollection<PlainDate> expected =
            IntervalCollection.onDateAxis().plus(window);
        assertThat(empty.withComplement(window), is(expected));
    }

    @Test
    public void minusIntermediateInterval() {
        DateInterval i1 =
            DateInterval.between(
                PlainDate.of(2014, 2, 28),
                PlainDate.of(2014, 5, 31));
        DateInterval i2 =
            DateInterval.between(
                PlainDate.of(2014, 4, 10),
                PlainDate.of(2014, 6, 1));
        DateInterval i3 =
            DateInterval.between(
                PlainDate.of(2014, 6, 15),
                PlainDate.of(2014, 6, 30));
        IntervalCollection<PlainDate> windows = IntervalCollection.onDateAxis();
        windows = windows.plus(i1).plus(i2).plus(i3);

        DateInterval i4 =
            DateInterval.between(
                PlainDate.of(2014, 4, 9),
                PlainDate.of(2014, 6, 2));
        DateInterval i5 =
            DateInterval.between(
                PlainDate.of(2014, 4, 21),
                PlainDate.of(2014, 5, 30));


        assertThat(
            windows.minus(i4),
            is(
                IntervalCollection.onDateAxis()
                .plus(
                    DateInterval.between(
                        PlainDate.of(2014, 2, 28),
                        PlainDate.of(2014, 4, 8)))
                .plus(i3)));
        assertThat(
            windows.minus(i5),
            is(
                IntervalCollection.onDateAxis()
                .plus(
                    DateInterval.between(
                        PlainDate.of(2014, 2, 28),
                        PlainDate.of(2014, 4, 20)))
                .plus(
                    DateInterval.between(
                        PlainDate.of(2014, 4, 10),
                        PlainDate.of(2014, 4, 20)))
                .plus(DateInterval.atomic(PlainDate.of(2014, 5, 31)))
                .plus(
                    DateInterval.between(
                        PlainDate.of(2014, 5, 31),
                        PlainDate.of(2014, 6, 1)))
                .plus(i3)));
    }

    @Test
    public void minusEarlierInterval() {
        DateInterval i1 =
            DateInterval.between(
                PlainDate.of(2014, 2, 28),
                PlainDate.of(2014, 5, 31));
        DateInterval i2 =
            DateInterval.between(
                PlainDate.of(2014, 4, 10),
                PlainDate.of(2014, 6, 1));
        DateInterval i3 =
            DateInterval.between(
                PlainDate.of(2014, 6, 15),
                PlainDate.of(2014, 6, 30));
        IntervalCollection<PlainDate> windows = IntervalCollection.onDateAxis();
        windows = windows.plus(i1).plus(i2).plus(i3);

        DateInterval earlier =
            DateInterval.between(
                PlainDate.of(2012, 4, 9),
                PlainDate.of(2013, 6, 2));

        assertThat(
            windows.minus(earlier),
            is(windows));
    }

    @Test
    public void minusLaterInterval() {
        DateInterval i1 =
            DateInterval.between(
                PlainDate.of(2014, 2, 28),
                PlainDate.of(2014, 5, 31));
        DateInterval i2 =
            DateInterval.between(
                PlainDate.of(2014, 4, 10),
                PlainDate.of(2014, 6, 1));
        DateInterval i3 =
            DateInterval.between(
                PlainDate.of(2014, 6, 15),
                PlainDate.of(2014, 6, 30));
        IntervalCollection<PlainDate> windows = IntervalCollection.onDateAxis();
        windows = windows.plus(i1).plus(i2).plus(i3);

        DateInterval later =
            DateInterval.between(
                PlainDate.of(2017, 4, 9),
                PlainDate.of(2018, 6, 2));

        assertThat(
            windows.minus(later),
            is(windows));
    }

    @Test
    public void minusBigInterval() {
        DateInterval i1 =
            DateInterval.between(
                PlainDate.of(2014, 2, 28),
                PlainDate.of(2014, 5, 31));
        DateInterval i2 =
            DateInterval.between(
                PlainDate.of(2014, 4, 10),
                PlainDate.of(2014, 6, 1));
        DateInterval i3 =
            DateInterval.between(
                PlainDate.of(2014, 6, 15),
                PlainDate.of(2014, 6, 30));
        IntervalCollection<PlainDate> windows = IntervalCollection.onDateAxis();
        windows = windows.plus(i1).plus(i2).plus(i3);

        DateInterval big =
            DateInterval.between(
                PlainDate.of(2012, 4, 9),
                PlainDate.of(2018, 6, 2));

        assertThat(
            windows.minus(big),
            is(IntervalCollection.onDateAxis()));
    }

    @Test
    public void minusEmptyInterval() {
        DateInterval i1 =
            DateInterval.between(
                PlainDate.of(2014, 2, 28),
                PlainDate.of(2014, 5, 31));
        DateInterval i2 =
            DateInterval.between(
                PlainDate.of(2014, 4, 10),
                PlainDate.of(2014, 6, 1));
        DateInterval i3 =
            DateInterval.between(
                PlainDate.of(2014, 6, 15),
                PlainDate.of(2014, 6, 30));
        IntervalCollection<PlainDate> windows = IntervalCollection.onDateAxis();
        windows = windows.plus(i1).plus(i2).plus(i3);

        DateInterval empty =
            DateInterval.atomic(PlainDate.of(2012, 4, 9)).withOpenEnd();

        assertThat(
            windows.minus(empty),
            is(windows));
    }

    @Test
    public void minusListOfIntervals() {
        DateInterval i1 =
            DateInterval.between(
                PlainDate.of(2014, 2, 28),
                PlainDate.of(2014, 5, 31));
        DateInterval i2 =
            DateInterval.between(
                PlainDate.of(2014, 4, 10),
                PlainDate.of(2014, 6, 1));
        DateInterval i3 =
            DateInterval.between(
                PlainDate.of(2014, 6, 15),
                PlainDate.of(2014, 6, 30));
        IntervalCollection<PlainDate> windows = IntervalCollection.onDateAxis();
        windows = windows.plus(i1).plus(i2).plus(i3);

        DateInterval big =
            DateInterval.between(
                PlainDate.of(2012, 4, 9),
                PlainDate.of(2018, 6, 2));
        IntervalCollection<PlainDate> minuend =
            IntervalCollection.onDateAxis().plus(big);

        DateInterval d1 =
            DateInterval.between(
                PlainDate.of(2012, 4, 9),
                PlainDate.of(2014, 2, 27));
        DateInterval d2 =
            DateInterval.between(
                PlainDate.of(2014, 6, 2),
                PlainDate.of(2014, 6, 14));
        DateInterval d3 =
            DateInterval.between(
                PlainDate.of(2014, 7, 1),
                PlainDate.of(2018, 6, 2));
        IntervalCollection<PlainDate> delta = IntervalCollection.onDateAxis();
        delta = delta.plus(d1).plus(d2).plus(d3);

        assertThat(
            minuend.minus(windows.getIntervals()),
            is(delta));
    }

    @Test
    public void plus() {
        DateInterval i1 =
            DateInterval.between(
                PlainDate.of(2014, 2, 28),
                PlainDate.of(2014, 5, 31));
        DateInterval i2 =
            DateInterval.between(
                PlainDate.of(2014, 4, 30),
                PlainDate.of(2014, 6, 1));
        IntervalCollection<PlainDate> a = IntervalCollection.onDateAxis().plus(i1);
        IntervalCollection<PlainDate> b = IntervalCollection.onDateAxis().plus(i2);
        IntervalCollection<PlainDate> c = IntervalCollection.onDateAxis().plus(Arrays.asList(i1, i2));

        assertThat(a.plus(b).getIntervals(), is(c.getIntervals())); // unmerged
    }

    @Test
    public void minus() {
        DateInterval i1 =
            DateInterval.between(
                PlainDate.of(2014, 2, 28),
                PlainDate.of(2014, 5, 31));
        DateInterval i2 =
            DateInterval.between(
                PlainDate.of(2014, 4, 30),
                PlainDate.of(2014, 6, 1));
        DateInterval j =
            DateInterval.between(
                PlainDate.of(2014, 2, 28),
                PlainDate.of(2014, 4, 29));
        IntervalCollection<PlainDate> a = IntervalCollection.onDateAxis().plus(i1);
        IntervalCollection<PlainDate> b = IntervalCollection.onDateAxis().plus(i2);
        IntervalCollection<PlainDate> c = IntervalCollection.onDateAxis().plus(j);

        assertThat(a.minus(b).getIntervals(), is(c.getIntervals()));
    }

    @Test
    public void union() {
        DateInterval i1 =
            DateInterval.between(
                PlainDate.of(2014, 2, 28),
                PlainDate.of(2014, 5, 31));
        DateInterval i2 =
            DateInterval.between(
                PlainDate.of(2014, 4, 30),
                PlainDate.of(2014, 6, 1));
        DateInterval merged =
            DateInterval.between(
                PlainDate.of(2014, 2, 28),
                PlainDate.of(2014, 6, 1));
        IntervalCollection<PlainDate> a = IntervalCollection.onDateAxis().plus(i1);
        IntervalCollection<PlainDate> b = IntervalCollection.onDateAxis().plus(i2);
        IntervalCollection<PlainDate> c = IntervalCollection.onDateAxis().plus(merged);

        assertThat(a.union(b).getIntervals(), is(c.getIntervals()));
    }

}
