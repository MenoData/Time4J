package net.time4j.range;

import net.time4j.CalendarUnit;
import net.time4j.PlainDate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class BasicDateRangeTest {

    @Test(expected=NullPointerException.class)
    public void containsNull() {
        assertThat(
            DateInterval
                .between(PlainDate.of(2014, 2, 27), PlainDate.of(2014, 5, 14))
                .contains((DateInterval) null),
            is(false));
    }

    @Test
    public void containsTemporalInside() {
        assertThat(
            DateInterval
                .between(PlainDate.of(2014, 2, 27), PlainDate.of(2014, 5, 14))
                .contains(PlainDate.of(2014, 3, 1)),
            is(true));
    }

    @Test
    public void containsTemporalLeftEdgeClosed() {
        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.of(2014, 5, 14);
        assertThat(
            DateInterval
                .between(start, end)
                .contains(start),
            is(true));
    }

    @Test
    public void containsTemporalRightEdgeClosed() {
        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.of(2014, 5, 14);
        assertThat(
            DateInterval
                .between(start, end)
                .contains(end),
            is(true));
    }

    @Test
    public void containsTemporalRightEdgeOpen() {
        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.of(2014, 5, 14);
        assertThat(
            DateInterval.between(start, end).withOpenEnd().contains(end),
            is(false));
    }

    @Test
    public void containsTemporalOutside() {
        assertThat(
            DateInterval
                .between(PlainDate.of(2014, 2, 27), PlainDate.of(2014, 5, 14))
                .contains(PlainDate.of(2012, 3, 1)),
            is(false));
    }

    @Test
    public void containsTemporalInfinitePastOutside() {
        assertThat(
            DateInterval
                .until(PlainDate.of(2014, 5, 14))
                .contains(PlainDate.of(2015, 3, 1)),
            is(false));
    }

    @Test
    public void containsTemporalInfinitePastInside() {
        assertThat(
            DateInterval
                .until(PlainDate.of(2014, 5, 14))
                .contains(PlainDate.axis().getMinimum()),
            is(true));
    }

    @Test
    public void containsTemporalInfiniteFutureOutside() {
        assertThat(
            DateInterval
                .since(PlainDate.of(2014, 5, 14))
                .contains(PlainDate.of(2012, 3, 1)),
            is(false));
    }

    @Test
    public void containsTemporalInfiniteFutureInside() {
        assertThat(
            DateInterval
                .since(PlainDate.of(2014, 5, 14))
                .contains(PlainDate.axis().getMaximum()),
            is(true));
    }

    @Test
    public void isEmptyInfinitePast() {
        assertThat(
            DateInterval
                .until(PlainDate.of(2014, 5, 14))
                .isEmpty(),
            is(false));
    }

    @Test
    public void isEmptyInfiniteFuture() {
        assertThat(
            DateInterval
                .since(PlainDate.of(2014, 5, 14))
                .isEmpty(),
            is(false));
    }

    @Test
    public void isEmptyAtomicClosed() {
        assertThat(
            DateInterval
                .atomic(PlainDate.of(2014, 5, 14))
                .isEmpty(),
            is(false));
    }

    @Test
    public void isEmptyAtomicOpen() {
        PlainDate date = PlainDate.of(2014, 5, 14);
        assertThat(
            DateInterval.atomic(date).withOpenEnd().isEmpty(),
            is(true));
    }

    @Test
    public void isFiniteInfinitePast() {
        assertThat(
            DateInterval
                .until(PlainDate.of(2014, 5, 14))
                .isFinite(),
            is(false));
    }

    @Test
    public void isFiniteInfiniteFuture() {
        assertThat(
            DateInterval
                .since(PlainDate.of(2014, 5, 14))
                .isFinite(),
            is(false));
    }

    @Test
    public void isFiniteEmpty() {
        PlainDate date = PlainDate.of(2014, 5, 14);
        assertThat(
            DateInterval.atomic(date).withOpenEnd().isFinite(), // empty
            is(true));
    }

    @Test
    public void isFinite() {
        assertThat(
            DateInterval.atomic(PlainDate.of(2014, 5, 14)).isFinite(),
            is(true));
    }

    @Test
    public void getStart() {
        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.of(2014, 5, 14);
        assertThat(
            DateInterval.between(start, end).getStart(),
            is(Boundary.of(IntervalEdge.CLOSED, start)));
    }

    @Test
    public void getEnd() {
        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.of(2014, 5, 14);
        assertThat(
            DateInterval.between(start, end).getEnd(),
            is(Boundary.of(IntervalEdge.CLOSED, end)));
    }

    @Test
    public void testEquals() {
        PlainDate start1 = PlainDate.of(2014, 2, 27);
        PlainDate end1 = PlainDate.of(2014, 5, 14);
        PlainDate start2 = start1;
        PlainDate end2 = PlainDate.of(2014, 5, 15);
        assertThat(
            DateInterval.between(start1, end1)
                .equals(DateInterval.between(start2, end2)),
            is(false));
        assertThat(
            DateInterval.between(start1, end1)
                .equals(DateInterval.between(start2, end2).withOpenEnd()),
            is(false));
        assertThat(
            DateInterval.between(start1, end1)
                .equals(DateInterval.between(start1, end1)),
            is(true));
    }

    @Test
    public void testHashCode() {
        PlainDate start1 = PlainDate.of(2014, 2, 27);
        PlainDate end1 = PlainDate.of(2014, 5, 14);
        PlainDate start2 = PlainDate.of(2014, 2, 26);
        PlainDate end2 = end1;
        assertThat(
            DateInterval.between(start1, end1).hashCode(),
            not(DateInterval.between(start2, end2).hashCode()));
        assertThat(
            DateInterval.between(start1, end1).hashCode(),
            is(DateInterval.between(start1, end1).hashCode()));
    }

    @Test
    public void testToStringInfinitePast() {
        assertThat(
            DateInterval
                .until(PlainDate.of(2014, 5, 14))
                .toString(),
            is("(-∞/2014-05-14]"));
    }

    @Test
    public void testToStringInfiniteFuture() {
        assertThat(
            DateInterval
                .since(PlainDate.of(2014, 5, 14))
                .toString(),
            is("[2014-05-14/+∞)"));
    }

    @Test
    public void testToStringFiniteClosed() {
        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.of(2014, 5, 14);
        assertThat(
            DateInterval
                .between(start, end)
                .toString(),
            is("[2014-02-27/2014-05-14]"));
    }

    @Test
    public void testToStringFiniteHalfOpen() {
        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.of(2014, 5, 14);
        assertThat(
            DateInterval.between(start, end).withOpenEnd().toString(),
            is("[2014-02-27/2014-05-14)"));
    }

    @Test
    public void withOpenEnd() {
        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.of(2014, 5, 14);

        DateInterval interval = DateInterval.between(start, end);
        assertThat(
            interval.withOpenEnd().getEnd().isOpen(),
            is(true));

        DateInterval infinite = DateInterval.since(start);
        assertThat(
            infinite.withOpenEnd(),
            is(infinite));
    }

    @Test
    public void withClosedEnd() {
        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.of(2014, 5, 14);

        DateInterval interval = DateInterval.between(start, end);
        assertThat(
            interval.withClosedEnd(),
            is(interval));
    }

    @Test
    public void withStart() {
        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate mid = PlainDate.of(2014, 4, 20);
        PlainDate end = PlainDate.of(2014, 5, 14);

        DateInterval interval = DateInterval.between(mid, end);
        assertThat(
            interval.withStart(start),
            is(DateInterval.between(start, end)));
    }

    @Test
    public void withEnd() {
        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate mid = PlainDate.of(2014, 4, 20);
        PlainDate end = PlainDate.of(2014, 5, 14);

        DateInterval interval = DateInterval.between(start, mid);
        assertThat(
            interval.withEnd(end),
            is(DateInterval.between(start, end)));
    }

    @Test
    public void collapseNormal() {
        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.of(2014, 5, 14);

        DateInterval interval = DateInterval.between(start, end);
        assertThat(
            interval.collapse().isEmpty(),
            is(true));
        assertThat(
            interval.collapse().getEnd().getTemporal().equals(start),
            is(true));
        assertThat(
            interval.collapse().getEnd().isOpen(),
            is(true));
    }

    @Test(expected=IllegalStateException.class)
    public void collapsePast() {
        PlainDate end = PlainDate.of(2014, 2, 27);
        DateInterval.until(end).collapse();
    }

    @Test
    public void move() {
        PlainDate start1 = PlainDate.of(2014, 2, 27);
        PlainDate end1 = PlainDate.of(2014, 5, 14);
        DateInterval interval = DateInterval.between(start1, end1);

        PlainDate start2 = PlainDate.of(2014, 10, 27);
        PlainDate end2 = PlainDate.of(2015, 1, 14);
        DateInterval expected = DateInterval.between(start2, end2);

        assertThat(
            interval.move(8, CalendarUnit.MONTHS),
            is(expected));
    }

}