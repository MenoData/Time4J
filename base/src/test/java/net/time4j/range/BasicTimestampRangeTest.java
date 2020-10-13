package net.time4j.range;

import net.time4j.CalendarUnit;
import net.time4j.ClockUnit;
import net.time4j.Duration;
import net.time4j.IsoUnit;
import net.time4j.PlainTimestamp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class BasicTimestampRangeTest {

    @Test
    public void containsTemporalInside() {
        PlainTimestamp start = PlainTimestamp.of(2014, 2, 27, 0, 0);
        PlainTimestamp end = PlainTimestamp.of(2014, 5, 14, 0, 0);
        assertThat(
            TimestampInterval
                .between(start, end)
                .contains(PlainTimestamp.of(2014, 3, 1, 14, 45)),
            is(true));
    }

    @Test
    public void containsTemporalLeftEdgeClosed() {
        PlainTimestamp start = PlainTimestamp.of(2014, 2, 27, 0, 0);
        PlainTimestamp end = PlainTimestamp.of(2014, 5, 14, 0, 0);
        assertThat(
            TimestampInterval
                .between(start, end)
                .contains(start),
            is(true));
    }

    @Test
    public void containsTemporalRightEdgeClosed() {
        PlainTimestamp start = PlainTimestamp.of(2014, 2, 27, 0, 0);
        PlainTimestamp end = PlainTimestamp.of(2014, 5, 14, 0, 0);
        assertThat(
            TimestampInterval.between(start, end).withClosedEnd().contains(end),
            is(true));
    }

    @Test
    public void containsTemporalRightEdgeOpen() {
        PlainTimestamp start = PlainTimestamp.of(2014, 2, 27, 0, 0);
        PlainTimestamp end = PlainTimestamp.of(2014, 5, 14, 0, 0);
        assertThat(
            TimestampInterval.between(start, end).contains(end),
            is(false));
    }

    @Test
    public void containsTemporalOutside() {
        PlainTimestamp start = PlainTimestamp.of(2014, 2, 27, 0, 0);
        PlainTimestamp end = PlainTimestamp.of(2014, 5, 14, 0, 0);
        assertThat(
            TimestampInterval
                .between(start, end)
                .contains(PlainTimestamp.of(2014, 5, 14, 0, 0, 1)),
            is(false));
    }

    @Test
    public void containsTemporalInfinitePastOutside() {
        assertThat(
            TimestampInterval
                .until(PlainTimestamp.of(2014, 5, 14, 0, 0))
                .contains(PlainTimestamp.of(2015, 3, 1, 0, 0)),
            is(false));
    }

    @Test
    public void containsTemporalInfinitePastInside() {
        assertThat(
            TimestampInterval
                .until(PlainTimestamp.of(2014, 5, 14, 0, 0))
                .contains(PlainTimestamp.axis().getMinimum()),
            is(true));
    }

    @Test
    public void containsTemporalInfiniteFutureOutside() {
        assertThat(
            TimestampInterval
                .since(PlainTimestamp.of(2014, 5, 14, 0, 0))
                .contains(PlainTimestamp.of(2012, 3, 1, 0, 0)),
            is(false));
    }

    @Test
    public void containsTemporalInfiniteFutureInside() {
        assertThat(
            TimestampInterval
                .since(PlainTimestamp.of(2014, 5, 14, 0, 0))
                .contains(PlainTimestamp.axis().getMaximum()),
            is(true));
    }

    @Test
    public void isEmptyInfinitePast() {
        assertThat(
            TimestampInterval
                .until(PlainTimestamp.of(2014, 5, 14, 0, 0))
                .isEmpty(),
            is(false));
    }

    @Test
    public void isEmptyInfiniteFuture() {
        assertThat(
            TimestampInterval
                .since(PlainTimestamp.of(2014, 5, 14, 0, 0))
                .isEmpty(),
            is(false));
    }

    @Test
    public void isEmptyAtomicClosed() {
        PlainTimestamp tsp = PlainTimestamp.of(2014, 5, 14, 0, 0);
        assertThat(
            TimestampInterval.between(tsp, tsp).withClosedEnd().isEmpty(),
            is(false));
    }

    @Test
    public void isEmptyAtomicOpen() {
        PlainTimestamp tsp = PlainTimestamp.of(2014, 5, 14, 0, 0);
        assertThat(
            TimestampInterval.between(tsp, tsp).isEmpty(),
            is(true));
    }

    @Test
    public void isFiniteInfinitePast() {
        assertThat(
            TimestampInterval
                .until(PlainTimestamp.of(2014, 5, 14, 0, 0))
                .isFinite(),
            is(false));
    }

    @Test
    public void isFiniteInfiniteFuture() {
        assertThat(
            TimestampInterval
                .since(PlainTimestamp.of(2014, 5, 14, 0, 0))
                .isFinite(),
            is(false));
    }

    @Test
    public void isFiniteEmpty() {
        PlainTimestamp tsp = PlainTimestamp.of(2014, 5, 14, 0, 0);
        assertThat(
            TimestampInterval.between(tsp, tsp).isFinite(), // empty
            is(true));
    }

    @Test
    public void isFinite() {
        PlainTimestamp start = PlainTimestamp.of(2014, 2, 27, 0, 0);
        PlainTimestamp end = PlainTimestamp.of(2014, 5, 14, 0, 0);
        assertThat(
            TimestampInterval.between(start, end).isFinite(),
            is(true));
    }

    @Test
    public void getStart() {
        PlainTimestamp start = PlainTimestamp.of(2014, 2, 27, 0, 0);
        PlainTimestamp end = PlainTimestamp.of(2014, 5, 14, 0, 0);
        assertThat(
            TimestampInterval.between(start, end).getStart(),
            is(Boundary.of(IntervalEdge.CLOSED, start)));
    }

    @Test
    public void getEnd() {
        PlainTimestamp start = PlainTimestamp.of(2014, 2, 27, 0, 0);
        PlainTimestamp end = PlainTimestamp.of(2014, 5, 14, 0, 0);
        assertThat(
            TimestampInterval.between(start, end).getEnd(),
            is(Boundary.of(IntervalEdge.OPEN, end)));
    }

    @Test
    public void getStartAsLocalDateTime() {
        LocalDateTime start = LocalDateTime.parse("2014-02-27T10:03:15");
        assertThat(
            TimestampInterval.since(start).getStartAsLocalDateTime(),
            is(start));
        assertThat(
            TimestampInterval.since(start).getEndAsLocalDateTime(),
            nullValue());
    }

    @Test
    public void getEndAsLocalDateTime() {
        LocalDateTime end = LocalDateTime.parse("2014-05-14T17:45:30");
        assertThat(
            TimestampInterval.until(end).getEndAsLocalDateTime(),
            is(end));
        assertThat(
            TimestampInterval.until(end).getStartAsLocalDateTime(),
            nullValue());
    }

    @Test
    public void testEquals() {
        PlainTimestamp start1 = PlainTimestamp.of(2014, 2, 27, 0, 0);
        PlainTimestamp end1 = PlainTimestamp.of(2014, 5, 14, 0, 0);
        PlainTimestamp start2 = start1;
        PlainTimestamp end2 = PlainTimestamp.axis().stepBackwards(end1);
        assertThat(
            TimestampInterval.between(start1, end1)
                .equals(
                    TimestampInterval.between(start2, end2)),
            is(false));
        assertThat(
            TimestampInterval.between(start1, end1)
                .equals(
                    TimestampInterval.between(start2, end2).withClosedEnd()),
            is(false));
        assertThat(
            TimestampInterval.between(start1, end1)
                .equals(TimestampInterval.between(start1, end1)),
            is(true));
    }

    @Test
    public void testHashCode() {
        PlainTimestamp start1 = PlainTimestamp.of(2014, 2, 27, 0, 0);
        PlainTimestamp end1 = PlainTimestamp.of(2014, 5, 14, 0, 0);
        PlainTimestamp start2 = start1;
        PlainTimestamp end2 = PlainTimestamp.axis().stepBackwards(end1);
        assertThat(
            TimestampInterval.between(start1, end1).hashCode(),
            not(TimestampInterval.between(start2, end2).hashCode()));
        assertThat(
            TimestampInterval.between(start1, end1).hashCode(),
            not(
                TimestampInterval.between(start2, end2)
                    .withClosedEnd().hashCode()));
        assertThat(
            TimestampInterval.between(start1, end1).hashCode(),
            is(TimestampInterval.between(start1, end1).hashCode()));
    }

    @Test
    public void testToStringInfinitePast() {
        assertThat(
            TimestampInterval
                .until(PlainTimestamp.of(2014, 5, 14, 14, 45))
                .toString(),
            is("(-∞/2014-05-14T14:45)"));
    }

    @Test
    public void testToStringInfiniteFuture() {
        assertThat(
            TimestampInterval
                .since(PlainTimestamp.of(2014, 5, 14, 14, 45))
                .toString(),
            is("[2014-05-14T14:45/+∞)"));
    }

    @Test
    public void testToStringFiniteClosed() {
        PlainTimestamp start = PlainTimestamp.of(2014, 2, 27, 14, 45);
        PlainTimestamp end = PlainTimestamp.of(2014, 5, 14, 9, 30);
        assertThat(
            TimestampInterval.between(start, end).withClosedEnd().toString(),
            is("[2014-02-27T14:45/2014-05-14T09:30]"));
    }

    @Test
    public void testToStringFiniteHalfOpen() {
        PlainTimestamp start = PlainTimestamp.of(2014, 2, 27, 14, 45);
        PlainTimestamp end = PlainTimestamp.of(2014, 5, 14, 9, 30);
        assertThat(
            TimestampInterval
                .between(start, end)
                .toString(),
            is("[2014-02-27T14:45/2014-05-14T09:30)"));
    }

    @Test
    public void withClosedEndNormal() {
        PlainTimestamp start = PlainTimestamp.of(2014, 2, 27, 14, 45);
        PlainTimestamp end = PlainTimestamp.of(2014, 5, 14, 9, 30);

        TimestampInterval interval = TimestampInterval.between(start, end);
        assertThat(
            interval.withClosedEnd().getEnd().isClosed(),
            is(true));
    }

    @Test(expected=IllegalStateException.class)
    public void withClosedEndInfinite() {
        PlainTimestamp start = PlainTimestamp.of(2014, 2, 27, 14, 45);
        TimestampInterval.since(start).withClosedEnd();
    }

    @Test
    public void move() {
        PlainTimestamp start1 = PlainTimestamp.of(2014, 5, 1, 23, 0);
        PlainTimestamp end1 = PlainTimestamp.of(2014, 5, 2, 16, 0);
        TimestampInterval interval = TimestampInterval.between(start1, end1);

        PlainTimestamp start2 = PlainTimestamp.of(2014, 5, 2, 11, 0);
        PlainTimestamp end2 = PlainTimestamp.of(2014, 5, 3, 4, 0);
        TimestampInterval expected = TimestampInterval.between(start2, end2);

        assertThat(
            interval.move(12, ClockUnit.HOURS),
            is(expected));
    }

    @Test
    public void stream1() {
        PlainTimestamp start = PlainTimestamp.of(2014, 5, 1, 23, 0);
        PlainTimestamp end = PlainTimestamp.of(2014, 6, 2, 16, 0);
        TimestampInterval interval = TimestampInterval.between(start, end);
        Duration<?> duration = Duration.of(1, (IsoUnit) CalendarUnit.MONTHS).plus(4, ClockUnit.HOURS);

        List<PlainTimestamp> expected = new ArrayList<>();
        expected.add(start);
        expected.add(PlainTimestamp.of(2014, 6, 2, 3, 0));

        List<PlainTimestamp> result = interval.stream(duration).collect(Collectors.toList());
        assertThat(result, is(expected));
    }

    @Test
    public void stream2() {
        PlainTimestamp start = PlainTimestamp.of(2014, 5, 1, 23, 0);
        PlainTimestamp end = PlainTimestamp.of(2014, 6, 2, 16, 0).plus(1, ClockUnit.NANOS);
        TimestampInterval interval = TimestampInterval.between(start, end);
        Duration<?> duration = Duration.of(1, (IsoUnit) CalendarUnit.MONTHS).plus(17, ClockUnit.HOURS);

        List<PlainTimestamp> expected = new ArrayList<>();
        expected.add(start);
        expected.add(PlainTimestamp.of(2014, 6, 2, 16, 0));

        List<PlainTimestamp> result = interval.stream(duration).parallel().collect(Collectors.toList());
        assertThat(result, is(expected));
    }

    @Test
    public void stream3() {
        PlainTimestamp start = PlainTimestamp.of(2014, 5, 1, 23, 0);
        PlainTimestamp end = PlainTimestamp.of(2014, 6, 2, 16, 0);
        TimestampInterval interval = TimestampInterval.between(start, end);
        Duration<?> duration = Duration.of(1, (IsoUnit) CalendarUnit.MONTHS).plus(17, ClockUnit.HOURS);

        List<PlainTimestamp> result = interval.stream(duration).parallel().collect(Collectors.toList());
        assertThat(result, is(Collections.singletonList(start)));
    }

    @Test
    public void stream4() {
        PlainTimestamp start = PlainTimestamp.of(2014, 5, 1, 23, 0);
        PlainTimestamp end = start.plus(2, ClockUnit.NANOS);
        TimestampInterval interval = TimestampInterval.between(start, end);
        Duration<?> duration = Duration.of(1, ClockUnit.NANOS);

        List<PlainTimestamp> expected = new ArrayList<>();
        expected.add(start);
        expected.add(start.plus(1, ClockUnit.NANOS));

        List<PlainTimestamp> result = interval.stream(duration).parallel().collect(Collectors.toList());
        assertThat(result, is(expected));
    }

    @Test
    public void stream5() {
        PlainTimestamp start = PlainTimestamp.of(2014, 5, 1, 23, 0);
        PlainTimestamp end = start.plus(1, ClockUnit.NANOS);
        TimestampInterval interval = TimestampInterval.between(start, end);
        Duration<?> duration = Duration.of(1, ClockUnit.NANOS);

        List<PlainTimestamp> result = interval.stream(duration).parallel().collect(Collectors.toList());
        assertThat(result, is(Collections.singletonList(start)));
    }

    @Test
    public void stream6() {
        PlainTimestamp start = PlainTimestamp.of(2014, 5, 1, 23, 0);
        TimestampInterval interval = TimestampInterval.between(start, start);
        Duration<?> duration = Duration.of(1, ClockUnit.NANOS);

        List<PlainTimestamp> result = interval.stream(duration).parallel().collect(Collectors.toList());
        assertThat(result, is(Collections.emptyList()));
    }

    @Test(expected=IllegalArgumentException.class)
    public void streamWithEmptyDuration() {
        PlainTimestamp start = PlainTimestamp.of(2014, 5, 1, 23, 0);
        PlainTimestamp end = PlainTimestamp.of(2014, 6, 2, 16, 0);
        TimestampInterval interval = TimestampInterval.between(start, end);
        Duration<?> duration = Duration.ofZero();
        interval.stream(duration);
    }

    @Test(expected=IllegalArgumentException.class)
    public void streamWithNegativeDuration() {
        PlainTimestamp start = PlainTimestamp.of(2014, 5, 1, 23, 0);
        PlainTimestamp end = PlainTimestamp.of(2014, 6, 2, 16, 0);
        TimestampInterval interval = TimestampInterval.between(start, end);
        Duration<?> duration = Duration.of(1, CalendarUnit.MONTHS).inverse();
        interval.stream(duration);
    }

    @Test(expected=IllegalArgumentException.class)
    public void streamWithStartAfterEnd() {
        PlainTimestamp start = PlainTimestamp.of(2014, 5, 1, 23, 0);
        PlainTimestamp end = PlainTimestamp.of(2014, 5, 1, 16, 0);
        TimestampInterval.stream(Duration.of(1, CalendarUnit.DAYS), start, end);
    }

    @Test(expected=IllegalStateException.class)
    public void streamInfinite() {
        PlainTimestamp tsp = PlainTimestamp.of(2016, 1, 1, 0, 0);
        TimestampInterval.since(tsp).stream(Duration.of(1, CalendarUnit.DAYS));
    }

    @Test
    public void streamSizeLimit() {
        PlainTimestamp start = PlainTimestamp.of(2014, 5, 1, 23, 0, 0);
        PlainTimestamp end = start.plus(Integer.MAX_VALUE - 1, ClockUnit.NANOS);
        TimestampInterval interval = TimestampInterval.between(start, end);
        assertThat(
            (int) interval.stream(Duration.of(1, ClockUnit.NANOS)).spliterator().getExactSizeIfKnown(),
            is(Integer.MAX_VALUE - 1));
    }

    @Test(expected=ArithmeticException.class)
    public void streamOverflow() {
        PlainTimestamp start = PlainTimestamp.of(2014, 5, 1, 23, 0, 0);
        PlainTimestamp end = PlainTimestamp.of(2014, 5, 1, 23, 0, 3);
        TimestampInterval interval = TimestampInterval.between(start, end);
        interval.stream(Duration.of(1, ClockUnit.NANOS));
    }

    @Test
    public void always() {
        assertThat(TimestampInterval.ALWAYS.getStart().isInfinite(), is(true));
        assertThat(TimestampInterval.ALWAYS.getEnd().isInfinite(), is(true));
    }

    @Test
    public void random() {
        PlainTimestamp start = PlainTimestamp.of(2014, 5, 1, 23, 59, 59);
        PlainTimestamp end = PlainTimestamp.of(2014, 5, 2, 0, 0, 0);
        TimestampInterval interval = TimestampInterval.between(start, end);
        for (int i = 0; i < 10; i++) {
            PlainTimestamp random = interval.random();
            assertThat(interval.contains(random), is(true));
        }
        interval = interval.withEnd(PlainTimestamp.of(2014, 5, 2, 1, 0, 0));
        for (int i = 0; i < 10; i++) {
            PlainTimestamp random = interval.random();
            assertThat(interval.contains(random), is(true));
        }
        interval = interval.withEnd(PlainTimestamp.of(2014, 5, 3, 1, 0, 0));
        for (int i = 0; i < 10; i++) {
            PlainTimestamp random = interval.random();
            assertThat(interval.contains(random), is(true));
        }
        interval = interval.withStart(PlainTimestamp.axis().getMinimum());
        interval = interval.withEnd(PlainTimestamp.axis().getMaximum());
        for (int i = 0; i < 10; i++) {
            PlainTimestamp random = interval.random();
            assertThat(interval.contains(random), is(true));
        }
    }

}