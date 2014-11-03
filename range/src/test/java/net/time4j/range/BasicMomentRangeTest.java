package net.time4j.range;

import net.time4j.Moment;
import net.time4j.PlainTimestamp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class BasicMomentRangeTest {

    @Test
    public void containsTemporalInside() {
        Moment start = PlainTimestamp.of(2014, 2, 27, 0, 0).atUTC();
        Moment end = PlainTimestamp.of(2014, 5, 14, 0, 0).atUTC();
        assertThat(
            MomentInterval
                .between(start, end)
                .contains(PlainTimestamp.of(2014, 3, 1, 14, 45).atUTC()),
            is(true));
    }

    @Test
    public void containsTemporalLeftEdgeClosed() {
        Moment start = PlainTimestamp.of(2014, 2, 27, 0, 0).atUTC();
        Moment end = PlainTimestamp.of(2014, 5, 14, 0, 0).atUTC();
        assertThat(
            MomentInterval
                .between(start, end)
                .contains(start),
            is(true));
    }

    @Test
    public void containsTemporalRightEdgeClosed() {
        Moment start = PlainTimestamp.of(2014, 2, 27, 0, 0).atUTC();
        Moment end = PlainTimestamp.of(2014, 5, 14, 0, 0).atUTC();
        assertThat(
            MomentInterval.between(start, end).withClosedEnd().contains(end),
            is(true));
    }

    @Test
    public void containsTemporalRightEdgeOpen() {
        Moment start = PlainTimestamp.of(2014, 2, 27, 0, 0).atUTC();
        Moment end = PlainTimestamp.of(2014, 5, 14, 0, 0).atUTC();
        assertThat(
            MomentInterval
                .between(start, end)
                .contains(end),
            is(false));
    }

    @Test
    public void containsTemporalOutside() {
        Moment start = PlainTimestamp.of(2014, 2, 27, 0, 0).atUTC();
        Moment end = PlainTimestamp.of(2014, 5, 14, 0, 0).atUTC();
        assertThat(
            MomentInterval
                .between(start, end)
                .contains(PlainTimestamp.of(2014, 5, 14, 0, 0, 1).atUTC()),
            is(false));
    }

    @Test
    public void containsTemporalInfinitePastOutside() {
        assertThat(
            MomentInterval
                .until(PlainTimestamp.of(2014, 5, 14, 0, 0).atUTC())
                .contains(PlainTimestamp.of(2015, 3, 1, 0, 0).atUTC()),
            is(false));
    }

    @Test
    public void containsTemporalInfinitePastInside() {
        assertThat(
            MomentInterval
                .until(PlainTimestamp.of(2014, 5, 14, 0, 0).atUTC())
                .contains(Moment.axis().getMinimum()),
            is(true));
    }

    @Test
    public void containsTemporalInfiniteFutureOutside() {
        assertThat(
            MomentInterval
                .since(PlainTimestamp.of(2014, 5, 14, 0, 0).atUTC())
                .contains(PlainTimestamp.of(2012, 3, 1, 0, 0).atUTC()),
            is(false));
    }

    @Test
    public void containsTemporalInfiniteFutureInside() {
        assertThat(
            MomentInterval
                .since(PlainTimestamp.of(2014, 5, 14, 0, 0).atUTC())
                .contains(Moment.axis().getMaximum()),
            is(true));
    }

    @Test
    public void isEmptyInfinitePast() {
        assertThat(
            MomentInterval
                .until(PlainTimestamp.of(2014, 5, 14, 0, 0).atUTC())
                .isEmpty(),
            is(false));
    }

    @Test
    public void isEmptyInfiniteFuture() {
        assertThat(
            MomentInterval
                .since(PlainTimestamp.of(2014, 5, 14, 0, 0).atUTC())
                .isEmpty(),
            is(false));
    }

    @Test
    public void isEmptyAtomicClosed() {
        Moment tsp = PlainTimestamp.of(2014, 5, 14, 0, 0).atUTC();
        assertThat(
            MomentInterval.between(tsp, tsp).withClosedEnd().isEmpty(),
            is(false));
    }

    @Test
    public void isEmptyAtomicOpen() {
        Moment tsp = PlainTimestamp.of(2014, 5, 14, 0, 0).atUTC();
        assertThat(
            MomentInterval.between(tsp, tsp).isEmpty(),
            is(true));
    }

    @Test
    public void isFiniteInfinitePast() {
        assertThat(
            MomentInterval
                .until(PlainTimestamp.of(2014, 5, 14, 0, 0).atUTC())
                .isFinite(),
            is(false));
    }

    @Test
    public void isFiniteInfiniteFuture() {
        assertThat(
            MomentInterval
                .since(PlainTimestamp.of(2014, 5, 14, 0, 0).atUTC())
                .isFinite(),
            is(false));
    }

    @Test
    public void isFiniteEmpty() {
        Moment tsp = PlainTimestamp.of(2014, 5, 14, 0, 0).atUTC();
        assertThat(
            MomentInterval.between(tsp, tsp).isFinite(), // empty
            is(true));
    }

    @Test
    public void isFinite() {
        Moment start = PlainTimestamp.of(2014, 2, 27, 0, 0).atUTC();
        Moment end = PlainTimestamp.of(2014, 5, 14, 0, 0).atUTC();
        assertThat(
            MomentInterval.between(start, end).isFinite(),
            is(true));
    }

    @Test
    public void getStart() {
        Moment start = PlainTimestamp.of(2014, 2, 27, 0, 0).atUTC();
        Moment end = PlainTimestamp.of(2014, 5, 14, 0, 0).atUTC();
        assertThat(
            MomentInterval.between(start, end).getStart(),
            is(Boundary.of(IntervalEdge.CLOSED, start)));
    }

    @Test
    public void getEnd() {
        Moment start = PlainTimestamp.of(2014, 2, 27, 0, 0).atUTC();
        Moment end = PlainTimestamp.of(2014, 5, 14, 0, 0).atUTC();
        assertThat(
            MomentInterval.between(start, end).getEnd(),
            is(Boundary.of(IntervalEdge.OPEN, end)));
    }

    @Test
    public void testEquals() {
        Moment start1 = PlainTimestamp.of(2014, 2, 27, 0, 0).atUTC();
        Moment end1 = PlainTimestamp.of(2014, 5, 14, 0, 0).atUTC();
        Moment start2 = start1;
        Moment end2 = Moment.axis().stepBackwards(end1);
        assertThat(
            MomentInterval.between(start1, end1)
                .equals(
                    MomentInterval.between(start2, end2)),
            is(false));
        assertThat(
            MomentInterval.between(start1, end1)
                .equals(MomentInterval.between(start2, end2).withClosedEnd()),
            is(false));
        assertThat(
            MomentInterval.between(start1, end1)
                .equals(MomentInterval.between(start1, end1)),
            is(true));
    }

    @Test
    public void testHashCode() {
        Moment start1 = PlainTimestamp.of(2014, 2, 27, 0, 0).atUTC();
        Moment end1 = PlainTimestamp.of(2014, 5, 14, 0, 0).atUTC();
        Moment start2 = start1;
        Moment end2 = Moment.axis().stepBackwards(end1);
        assertThat(
            MomentInterval.between(start1, end1).hashCode(),
            not(MomentInterval.between(start2, end2).hashCode()));
        assertThat(
            MomentInterval.between(start1, end1).hashCode(),
            not(
                MomentInterval.between(start2, end2)
                .withClosedEnd().hashCode()));
        assertThat(
            MomentInterval.between(start1, end1).hashCode(),
            is(MomentInterval.between(start1, end1).hashCode()));
    }

    @Test
    public void testToStringInfinitePast() {
        assertThat(
            MomentInterval
                .until(PlainTimestamp.of(2014, 5, 14, 14, 45).atUTC())
                .toString(),
            is("(-∞/2014-05-14T14:45:00Z)"));
    }

    @Test
    public void testToStringInfiniteFuture() {
        assertThat(
            MomentInterval
                .since(PlainTimestamp.of(2014, 5, 14, 14, 45).atUTC())
                .toString(),
            is("[2014-05-14T14:45:00Z/+∞)"));
    }

    @Test
    public void testToStringFiniteClosed() {
        Moment start = PlainTimestamp.of(2014, 2, 27, 14, 45).atUTC();
        Moment end = PlainTimestamp.of(2014, 5, 14, 9, 30).atUTC();
        assertThat(
            MomentInterval.between(start, end).withClosedEnd().toString(),
            is("[2014-02-27T14:45:00Z/2014-05-14T09:30:00Z]"));
    }

    @Test
    public void testToStringFiniteHalfOpen() {
        Moment start = PlainTimestamp.of(2014, 2, 27, 14, 45).atUTC();
        Moment end = PlainTimestamp.of(2014, 5, 14, 9, 30).atUTC();
        assertThat(
            MomentInterval
                .between(start, end)
                .toString(),
            is("[2014-02-27T14:45:00Z/2014-05-14T09:30:00Z)"));
    }

}