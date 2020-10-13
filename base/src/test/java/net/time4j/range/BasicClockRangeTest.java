package net.time4j.range;

import net.time4j.ClockUnit;
import net.time4j.Duration;
import net.time4j.PlainTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class BasicClockRangeTest {

    @Test
    public void containsTemporalInside() {
        PlainTime start = PlainTime.of(14, 45);
        PlainTime end = PlainTime.of(21, 30);
        assertThat(
            ClockInterval
                .between(start, end)
                .contains(PlainTime.of(18, 45)),
            is(true));
    }

    @Test
    public void containsTemporalLeftEdgeClosed() {
        PlainTime start = PlainTime.of(14, 45);
        PlainTime end = PlainTime.of(21, 30);
        assertThat(
            ClockInterval
                .between(start, end)
                .contains(start),
            is(true));
    }

    @Test
    public void containsTemporalRightEdgeClosed() {
        PlainTime start = PlainTime.of(14, 45);
        PlainTime end = PlainTime.of(21, 30);
        assertThat(
            ClockInterval.between(start, end).withClosedEnd().contains(end),
            is(true));
    }

    @Test
    public void containsTemporalRightEdgeOpen() {
        PlainTime start = PlainTime.of(14, 45);
        PlainTime end = PlainTime.of(21, 30);
        assertThat(
            ClockInterval.between(start, end).contains(end),
            is(false));
    }

    @Test
    public void containsTemporalOutside() {
        PlainTime start = PlainTime.of(14, 45);
        PlainTime end = PlainTime.of(21, 30);
        assertThat(
            ClockInterval
                .between(start, end)
                .contains(PlainTime.of(22, 1)),
            is(false));
    }

    @Test
    public void containsTemporalInfinitePastOutside() {
        assertThat(
            ClockInterval
                .until(PlainTime.of(14, 45))
                .contains(PlainTime.of(22, 0)),
            is(false));
    }

    @Test
    public void containsTemporalInfinitePastInside() {
        assertThat(
            ClockInterval
                .until(PlainTime.of(14, 45))
                .contains(PlainTime.axis().getMinimum()),
            is(true));
    }

    @Test
    public void containsTemporalInfiniteFutureOutside() {
        assertThat(
            ClockInterval
                .since(PlainTime.of(14, 45))
                .contains(PlainTime.of(12)),
            is(false));
    }

    @Test
    public void containsTemporalInfiniteFutureInside() {
        assertThat(
            ClockInterval
                .since(PlainTime.of(14, 45))
                .contains(PlainTime.of(20, 30)),
            is(true));
    }

    @Test
    public void containsTemporalInfiniteFutureT24() {
        assertThat(
            ClockInterval
                .since(PlainTime.of(14, 45))
                .contains(PlainTime.axis().getMaximum()),
            is(false));
    }

    @Test
    public void isEmptyInfinitePast() {
        assertThat(
            ClockInterval
                .until(PlainTime.of(14, 45))
                .isEmpty(),
            is(false));
    }

    @Test
    public void isEmptyInfiniteFuture() {
        assertThat(
            ClockInterval
                .since(PlainTime.of(14, 45))
                .isEmpty(),
            is(false));
    }

    @Test
    public void isEmptyAtomicClosed() {
        PlainTime tsp = PlainTime.of(14, 45);
        assertThat(
            ClockInterval.between(tsp, tsp).withClosedEnd().isEmpty(),
            is(false));
    }

    @Test
    public void isEmptyAtomicOpen() {
        PlainTime tsp = PlainTime.of(14, 45);
        assertThat(
            ClockInterval.between(tsp, tsp).isEmpty(),
            is(true));
    }

    @Test
    public void isFiniteInfinitePast() {
        assertThat(
            ClockInterval
                .until(PlainTime.of(14, 45))
                .isFinite(),
            is(true));
    }

    @Test
    public void isFiniteInfiniteFuture() {
        assertThat(
            ClockInterval
                .since(PlainTime.of(14, 45))
                .isFinite(),
            is(true));
    }

    @Test
    public void isFiniteEmpty() {
        PlainTime tsp = PlainTime.of(14, 45);
        assertThat(
            ClockInterval.between(tsp, tsp).isFinite(), // empty
            is(true));
    }

    @Test
    public void isFinite() {
        PlainTime start = PlainTime.of(14, 45);
        PlainTime end = PlainTime.of(21, 30);
        assertThat(
            ClockInterval.between(start, end).isFinite(),
            is(true));
    }

    @Test
    public void getStart() {
        PlainTime start = PlainTime.of(14, 45);
        PlainTime end = PlainTime.of(21, 30);
        assertThat(
            ClockInterval.between(start, end).getStart(),
            is(Boundary.of(IntervalEdge.CLOSED, start)));
    }

    @Test
    public void getEnd() {
        PlainTime start = PlainTime.of(14, 45);
        PlainTime end = PlainTime.of(21, 30);
        assertThat(
            ClockInterval.between(start, end).getEnd(),
            is(Boundary.of(IntervalEdge.OPEN, end)));
    }

    @Test
    public void getStartAsLocalTime() {
        LocalTime start = LocalTime.of(14, 45);
        LocalTime end = LocalTime.of(21, 30);
        assertThat(
            ClockInterval.between(start, end).getStartAsLocalTime(),
            is(start));
        assertThat(
            ClockInterval.between(start, end).getEndAsLocalTime(),
            is(end));
    }

    @Test
    public void getEndAsLocalTime() {
        LocalTime end = LocalTime.of(14, 45);
        assertThat(
            ClockInterval.until(end).getEndAsLocalTime(),
            is(end));
        assertThat(
            ClockInterval.since(end).getEndAsLocalTime(),
            is(LocalTime.of(0, 0)));
    }

    @Test
    public void testEquals() {
        PlainTime start1 = PlainTime.of(14, 45);
        PlainTime end1 = PlainTime.of(21, 30);
        PlainTime start2 = start1;
        PlainTime end2 = PlainTime.axis().stepBackwards(end1);
        assertThat(
            ClockInterval.between(start1, end1)
                .equals(
                    ClockInterval.between(start2, end2)),
            is(false));
        assertThat(
            ClockInterval.between(start1, end1)
                .equals(ClockInterval.between(start2, end2).withClosedEnd()),
            is(false));
        assertThat(
            ClockInterval.between(start1, end1)
                .equals(ClockInterval.between(start1, end1)),
            is(true));
    }

    @Test
    public void testHashCode() {
        PlainTime start1 = PlainTime.of(14, 45);
        PlainTime end1 = PlainTime.of(21, 30);
        PlainTime start2 = start1;
        PlainTime end2 = PlainTime.axis().stepBackwards(end1);
        assertThat(
            ClockInterval.between(start1, end1).hashCode(),
            not(ClockInterval.between(start2, end2).hashCode()));
        assertThat(
            ClockInterval.between(start1, end1).hashCode(),
            not(
                ClockInterval.between(start2, end2)
                    .withClosedEnd().hashCode()));
        assertThat(
            ClockInterval.between(start1, end1).hashCode(),
            is(ClockInterval.between(start1, end1).hashCode()));
    }

    @Test
    public void testToStringInfinitePast() {
        assertThat(
            ClockInterval
                .until(PlainTime.of(20, 45))
                .toString(),
            is("[T00/T20:45)"));
    }

    @Test
    public void testToStringInfiniteFuture() {
        assertThat(
            ClockInterval
                .since(PlainTime.of(20, 45))
                .toString(),
            is("[T20:45/T24)"));
    }

    @Test
    public void testToStringFiniteClosed() {
        PlainTime start = PlainTime.of(19, 45);
        PlainTime end = PlainTime.of(20, 30);
        assertThat(
            ClockInterval.between(start, end).withClosedEnd().toString(),
            is("[T19:45/T20:30]"));
    }

    @Test
    public void testToStringFiniteHalfOpen() {
        PlainTime start = PlainTime.of(19, 45);
        PlainTime end = PlainTime.of(20, 30);
        assertThat(
            ClockInterval
                .between(start, end)
                .toString(),
            is("[T19:45/T20:30)"));
    }

    @Test
    public void move() {
        PlainTime start1 = PlainTime.of(3, 20, 27);
        PlainTime end1 = PlainTime.of(7, 50, 14);
        ClockInterval interval = ClockInterval.between(start1, end1);

        PlainTime start2 = PlainTime.of(7, 20, 27);
        PlainTime end2 = PlainTime.of(11, 50, 14);
        ClockInterval expected = ClockInterval.between(start2, end2);

        assertThat(
            interval.move(4, ClockUnit.HOURS),
            is(expected));
    }

    @Test
    public void canonicalForm() {
        assertThat(
            ClockInterval.between(PlainTime.midnightAtStartOfDay(), PlainTime.of(23, 59, 59, 123456789))
                .withClosedEnd().toCanonical(),
            is(ClockInterval.between(PlainTime.midnightAtStartOfDay(), PlainTime.of(23, 59, 59, 123456790))));
    }

    @Test(expected=IllegalStateException.class)
    public void canonicalError() {
        ClockInterval.since(PlainTime.midnightAtStartOfDay()).withClosedEnd().toCanonical();
    }

    @Test
    public void stream1() {
        PlainTime start = PlainTime.of(15, 0);
        PlainTime end = PlainTime.of(20, 0);
        ClockInterval interval = ClockInterval.between(start, end);
        Duration<ClockUnit> duration = Duration.of(1, ClockUnit.HOURS);

        List<PlainTime> expected = new ArrayList<>();
        expected.add(start);
        expected.add(PlainTime.of(16, 0));
        expected.add(PlainTime.of(17, 0));
        expected.add(PlainTime.of(18, 0));
        expected.add(PlainTime.of(19, 0));

        List<PlainTime> result = interval.stream(duration).parallel().collect(Collectors.toList());
        assertThat(result, is(expected));
    }

    @Test
    public void stream2() {
        PlainTime start = PlainTime.of(15, 0);
        PlainTime end = PlainTime.of(20, 0).plus(1, ClockUnit.NANOS);
        ClockInterval interval = ClockInterval.between(start, end);
        Duration<ClockUnit> duration = Duration.of(1, ClockUnit.HOURS);

        List<PlainTime> expected = new ArrayList<>();
        expected.add(start);
        expected.add(PlainTime.of(16, 0));
        expected.add(PlainTime.of(17, 0));
        expected.add(PlainTime.of(18, 0));
        expected.add(PlainTime.of(19, 0));
        expected.add(PlainTime.of(20, 0));

        List<PlainTime> result = interval.stream(duration).parallel().collect(Collectors.toList());
        assertThat(result, is(expected));
    }

    @Test
    public void stream3() {
        PlainTime start = PlainTime.of(15, 0);
        PlainTime end = PlainTime.of(20, 0);
        ClockInterval interval = ClockInterval.between(start, end);
        Duration<ClockUnit> duration = Duration.of(5, ClockUnit.HOURS);

        List<PlainTime> result = interval.stream(duration).parallel().collect(Collectors.toList());
        assertThat(result, is(Collections.singletonList(start)));
    }

    @Test
    public void stream4() {
        PlainTime start = PlainTime.of(15, 0);
        PlainTime end = PlainTime.of(15, 0).plus(2, ClockUnit.NANOS);
        ClockInterval interval = ClockInterval.between(start, end);
        Duration<ClockUnit> duration = Duration.of(1, ClockUnit.NANOS);

        List<PlainTime> expected = new ArrayList<>();
        expected.add(start);
        expected.add(start.plus(1, ClockUnit.NANOS));

        List<PlainTime> result = interval.stream(duration).parallel().collect(Collectors.toList());
        assertThat(result, is(expected));
    }

    @Test
    public void stream5() {
        PlainTime start = PlainTime.of(15, 0);
        PlainTime end = PlainTime.of(15, 0).plus(1, ClockUnit.NANOS);
        ClockInterval interval = ClockInterval.between(start, end);
        Duration<ClockUnit> duration = Duration.of(1, ClockUnit.NANOS);

        List<PlainTime> result = interval.stream(duration).parallel().collect(Collectors.toList());
        assertThat(result, is(Collections.singletonList(start)));
    }

    @Test
    public void stream6() {
        PlainTime start = PlainTime.of(15, 0);
        PlainTime end = PlainTime.of(15, 0);
        ClockInterval interval = ClockInterval.between(start, end);
        Duration<ClockUnit> duration = Duration.of(1, ClockUnit.NANOS);

        List<PlainTime> result = interval.stream(duration).parallel().collect(Collectors.toList());
        assertThat(result, is(Collections.emptyList()));
    }

    @Test(expected=IllegalArgumentException.class)
    public void streamWithEmptyDuration() {
        PlainTime start = PlainTime.of(15, 0);
        PlainTime end = PlainTime.of(16, 0);
        ClockInterval interval = ClockInterval.between(start, end);
        Duration<ClockUnit> duration = Duration.ofZero();
        interval.stream(duration);
    }

    @Test(expected=IllegalArgumentException.class)
    public void streamWithNegativeDuration() {
        PlainTime start = PlainTime.of(15, 0);
        PlainTime end = PlainTime.of(16, 0);
        ClockInterval interval = ClockInterval.between(start, end);
        Duration<ClockUnit> duration = Duration.of(1, ClockUnit.HOURS).inverse();
        interval.stream(duration);
    }

    @Test(expected=IllegalArgumentException.class)
    public void streamWithStartAfterEnd() {
        PlainTime start = PlainTime.of(23, 0);
        PlainTime end = PlainTime.of(16, 0);
        ClockInterval.stream(Duration.of(1, ClockUnit.HOURS), start, end);
    }

    @Test
    public void streamSizeLimit() {
        PlainTime start = PlainTime.of(23, 0, 0);
        PlainTime end = start.plus(Integer.MAX_VALUE - 1, ClockUnit.NANOS);
        ClockInterval interval = ClockInterval.between(start, end);
        assertThat(
            (int) interval.stream(Duration.of(1, ClockUnit.NANOS)).spliterator().getExactSizeIfKnown(),
            is(Integer.MAX_VALUE - 1));
    }

    @Test(expected=ArithmeticException.class)
    public void streamOverflow() {
        PlainTime start = PlainTime.of(23, 0, 0);
        PlainTime end = start.plus(Integer.MAX_VALUE, ClockUnit.NANOS);
        ClockInterval interval = ClockInterval.between(start, end);
        interval.stream(Duration.of(1, ClockUnit.NANOS));
    }

    @Test
    public void random() {
        ClockInterval interval =
            ClockInterval.between(
                PlainTime.of(23, 59, 59),
                PlainTime.midnightAtEndOfDay());
        for (int i = 0; i < 100; i++) {
            PlainTime random = interval.random();
            assertThat(interval.contains(random), is(true));
        }
    }

}