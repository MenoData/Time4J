package net.time4j.range;

import net.time4j.PlainTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class BasicTimeRangeTest {

    @Test
    public void containsTemporalInside() {
        PlainTime start = PlainTime.of(14, 45);
        PlainTime end = PlainTime.of(21, 30);
        assertThat(
            TimeInterval
                .between(start, end)
                .contains(PlainTime.of(18, 45)),
            is(true));
    }

    @Test
    public void containsTemporalLeftEdgeClosed() {
        PlainTime start = PlainTime.of(14, 45);
        PlainTime end = PlainTime.of(21, 30);
        assertThat(
            TimeInterval
                .between(start, end)
                .contains(start),
            is(true));
    }

    @Test
    public void containsTemporalLeftEdgeOpen() {
        PlainTime start = PlainTime.of(14, 45);
        PlainTime end = PlainTime.of(21, 30);
        assertThat(
            TimeInterval
                .between(start, end)
                .withStart(Boundary.of(IntervalEdge.OPEN, start))
                .contains(start),
            is(false));
    }

    @Test
    public void containsTemporalRightEdgeClosed() {
        PlainTime start = PlainTime.of(14, 45);
        PlainTime end = PlainTime.of(21, 30);
        assertThat(
            TimeInterval
                .between(start, end)
                .withEnd(Boundary.of(IntervalEdge.CLOSED, end))
                .contains(end),
            is(true));
    }

    @Test
    public void containsTemporalRightEdgeOpen() {
        PlainTime start = PlainTime.of(14, 45);
        PlainTime end = PlainTime.of(21, 30);
        assertThat(
            TimeInterval
                .between(start, end)
                .contains(end),
            is(false));
    }

    @Test
    public void containsTemporalOutside() {
        PlainTime start = PlainTime.of(14, 45);
        PlainTime end = PlainTime.of(21, 30);
        assertThat(
            TimeInterval
                .between(start, end)
                .contains(PlainTime.of(22, 1)),
            is(false));
    }

    @Test
    public void containsTemporalInfinitePastOutside() {
        assertThat(
            TimeInterval
                .until(PlainTime.of(14, 45))
                .contains(PlainTime.of(22, 0)),
            is(false));
    }

    @Test
    public void containsTemporalInfinitePastInside() {
        assertThat(
            TimeInterval
                .until(PlainTime.of(14, 45))
                .contains(PlainTime.axis().getMinimum()),
            is(true));
    }

    @Test
    public void containsTemporalInfiniteFutureOutside() {
        assertThat(
            TimeInterval
                .since(PlainTime.of(14, 45))
                .contains(PlainTime.of(12)),
            is(false));
    }

    @Test
    public void containsTemporalInfiniteFutureInside() {
        assertThat(
            TimeInterval
                .since(PlainTime.of(14, 45))
                .contains(PlainTime.of(20, 30)),
            is(true));
    }

    @Test
    public void containsTemporalInfiniteFutureT24() {
        assertThat(
            TimeInterval
                .since(PlainTime.of(14, 45))
                .contains(PlainTime.axis().getMaximum()),
            is(false));
    }

    @Test
    public void isEmptyInfinitePast() {
        assertThat(
            TimeInterval
                .until(PlainTime.of(14, 45))
                .isEmpty(),
            is(false));
    }

    @Test
    public void isEmptyInfiniteFuture() {
        assertThat(
            TimeInterval
                .since(PlainTime.of(14, 45))
                .isEmpty(),
            is(false));
    }

    @Test
    public void isEmptyAtomicClosed() {
        PlainTime tsp = PlainTime.of(14, 45);
        assertThat(
            TimeInterval
                .between(tsp, tsp)
                .withEnd(Boundary.of(IntervalEdge.CLOSED, tsp))
                .isEmpty(),
            is(false));
    }

    @Test
    public void isEmptyAtomicOpen() {
        PlainTime tsp = PlainTime.of(14, 45);
        assertThat(
            TimeInterval.between(tsp, tsp).isEmpty(),
            is(true));
    }

    @Test
    public void isFiniteInfinitePast() {
        assertThat(
            TimeInterval
                .until(PlainTime.of(14, 45))
                .isFinite(),
            is(true));
    }

    @Test
    public void isFiniteInfiniteFuture() {
        assertThat(
            TimeInterval
                .since(PlainTime.of(14, 45))
                .isFinite(),
            is(true));
    }

    @Test
    public void isFiniteEmpty() {
        PlainTime tsp = PlainTime.of(14, 45);
        assertThat(
            TimeInterval.between(tsp, tsp).isFinite(), // empty
            is(true));
    }

    @Test
    public void isFinite() {
        PlainTime start = PlainTime.of(14, 45);
        PlainTime end = PlainTime.of(21, 30);
        assertThat(
            TimeInterval.between(start, end).isFinite(),
            is(true));
    }

    @Test
    public void getStart() {
        PlainTime start = PlainTime.of(14, 45);
        PlainTime end = PlainTime.of(21, 30);
        assertThat(
            TimeInterval.between(start, end).getStart(),
            is(Boundary.of(IntervalEdge.CLOSED, start)));
    }

    @Test
    public void getEnd() {
        PlainTime start = PlainTime.of(14, 45);
        PlainTime end = PlainTime.of(21, 30);
        assertThat(
            TimeInterval.between(start, end).getEnd(),
            is(Boundary.of(IntervalEdge.OPEN, end)));
    }

    @Test
    public void testEquals() {
        PlainTime start1 = PlainTime.of(14, 45);
        PlainTime end1 = PlainTime.of(21, 30);
        PlainTime start2 = start1;
        PlainTime end2 = PlainTime.axis().stepBackwards(end1);
        assertThat(
            TimeInterval.between(start1, end1)
                .equals(
                    TimeInterval.between(start2, end2)),
            is(false));
        assertThat(
            TimeInterval.between(start1, end1)
                .equals(
                    TimeInterval.between(start2, end2)
                    .withEnd(Boundary.of(IntervalEdge.CLOSED, end2))),
            is(false));
        assertThat(
            TimeInterval.between(start1, end1)
                .equals(TimeInterval.between(start1, end1)),
            is(true));
    }

    @Test
    public void testHashCode() {
        PlainTime start1 = PlainTime.of(14, 45);
        PlainTime end1 = PlainTime.of(21, 30);
        PlainTime start2 = start1;
        PlainTime end2 = PlainTime.axis().stepBackwards(end1);
        assertThat(
            TimeInterval.between(start1, end1).hashCode(),
            not(TimeInterval.between(start2, end2).hashCode()));
        assertThat(
            TimeInterval.between(start1, end1).hashCode(),
            not(
                TimeInterval.between(start2, end2)
                .withEnd(Boundary.of(IntervalEdge.CLOSED, end2))
                .hashCode()));
        assertThat(
            TimeInterval.between(start1, end1).hashCode(),
            is(TimeInterval.between(start1, end1).hashCode()));
    }

    @Test
    public void testToStringInfinitePast() {
        assertThat(
            TimeInterval
                .until(PlainTime.of(20, 45))
                .toString(),
            is("[T00/T20:45)"));
    }

    @Test
    public void testToStringInfiniteFuture() {
        assertThat(
            TimeInterval
                .since(PlainTime.of(20, 45))
                .toString(),
            is("[T20:45/T24)"));
    }

    @Test
    public void testToStringFiniteClosed() {
        PlainTime start = PlainTime.of(19, 45);
        PlainTime end = PlainTime.of(20, 30);
        assertThat(
            TimeInterval
                .between(start, end)
                .withEnd(Boundary.of(IntervalEdge.CLOSED, end))
                .toString(),
            is("[T19:45/T20:30]"));
    }

    @Test
    public void testToStringFiniteHalfOpen() {
        PlainTime start = PlainTime.of(19, 45);
        PlainTime end = PlainTime.of(20, 30);
        assertThat(
            TimeInterval
                .between(start, end)
                .toString(),
            is("[T19:45/T20:30)"));
    }

}