package net.time4j.range;

import net.time4j.MachineTime;
import net.time4j.Moment;
import net.time4j.PlainTimestamp;
import net.time4j.SI;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


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
    public void getStartAsInstant() {
        Instant start = Instant.parse("2014-02-27T10:03:15Z");
        assertThat(
            MomentInterval.since(start).getStartAsInstant(),
            is(start));
        assertThat(
            MomentInterval.since(start).getEndAsInstant(),
            nullValue());
    }

    @Test
    public void getEndAsInstant() {
        Instant end = Instant.parse("2014-05-14T17:45:30Z");
        assertThat(
            MomentInterval.until(end).getEndAsInstant(),
            is(end));
        assertThat(
            MomentInterval.until(end).getStartAsInstant(),
            nullValue());
    }

    @Test
    public void testEquals() {
        Moment start1 = PlainTimestamp.of(2014, 2, 27, 0, 0).atUTC();
        Moment end1 = PlainTimestamp.of(2014, 5, 14, 0, 0).atUTC();
        Moment start2 = start1;
        Moment end2 = Moment.axis().stepBackwards(end1);
        assertThat(
            MomentInterval.between(start1, end1)
                .equals(MomentInterval.between(start2, end2)),
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
            not(MomentInterval.between(start2, end2).withClosedEnd().hashCode()));
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

    @Test
    public void move() {
        Moment start1 = PlainTimestamp.of(2014, 5, 1, 23, 0).atUTC();
        Moment end1 = PlainTimestamp.of(2014, 5, 2, 16, 0).atUTC();
        MomentInterval interval = MomentInterval.between(start1, end1);

        Moment start2 = PlainTimestamp.of(2014, 5, 2, 11, 0).atUTC();
        Moment end2 = PlainTimestamp.of(2014, 5, 3, 4, 0).atUTC();
        MomentInterval expected = MomentInterval.between(start2, end2);

        assertThat(
            interval.move(12, TimeUnit.HOURS),
            is(expected));
        assertThat(
            interval.move(12 * 3600, SI.SECONDS),
            is(expected));
    }

    @Test
    public void surroundingIntervalSI() {
        Moment moment = PlainTimestamp.of(2017, 9, 9, 10, 0).atUTC();
        MachineTime<SI> duration = MachineTime.ofSIUnits(5400, 0);

        MomentInterval centered = MomentInterval.surrounding(moment, duration, MomentInterval.CENTERED);
        Moment startCentered = PlainTimestamp.of(2017, 9, 9, 9, 15).atUTC();
        Moment endCentered = PlainTimestamp.of(2017, 9, 9, 10, 45).atUTC();
        assertThat(centered, is(MomentInterval.between(startCentered, endCentered)));

        MomentInterval left = MomentInterval.surrounding(moment, duration, MomentInterval.LEFT_ALIGNED);
        Moment startLeft = PlainTimestamp.of(2017, 9, 9, 8, 30).atUTC();
        Moment endLeft = PlainTimestamp.of(2017, 9, 9, 10, 0).atUTC();
        assertThat(left, is(MomentInterval.between(startLeft, endLeft)));

        MomentInterval right = MomentInterval.surrounding(moment, duration, MomentInterval.RIGHT_ALIGNED);
        Moment startRight = PlainTimestamp.of(2017, 9, 9, 10, 0).atUTC();
        Moment endRight = PlainTimestamp.of(2017, 9, 9, 11, 30).atUTC();
        assertThat(right, is(MomentInterval.between(startRight, endRight)));
    }

    @Test
    public void surroundingIntervalPOSIX() {
        Instant instant = PlainTimestamp.of(2017, 9, 9, 10, 0).atUTC().toTemporalAccessor();
        java.time.Duration duration = java.time.Duration.ofSeconds(3600);

        MomentInterval centered = MomentInterval.surrounding(instant, duration, MomentInterval.CENTERED);
        Moment startCentered = PlainTimestamp.of(2017, 9, 9, 9, 30).atUTC();
        Moment endCentered = PlainTimestamp.of(2017, 9, 9, 10, 30).atUTC();
        assertThat(centered, is(MomentInterval.between(startCentered, endCentered)));

        MomentInterval left = MomentInterval.surrounding(instant, duration, MomentInterval.LEFT_ALIGNED);
        Moment startLeft = PlainTimestamp.of(2017, 9, 9, 9, 0).atUTC();
        Moment endLeft = PlainTimestamp.of(2017, 9, 9, 10, 0).atUTC();
        assertThat(left, is(MomentInterval.between(startLeft, endLeft)));

        MomentInterval right = MomentInterval.surrounding(instant, duration, MomentInterval.RIGHT_ALIGNED);
        Moment startRight = PlainTimestamp.of(2017, 9, 9, 10, 0).atUTC();
        Moment endRight = PlainTimestamp.of(2017, 9, 9, 11, 0).atUTC();
        assertThat(right, is(MomentInterval.between(startRight, endRight)));
    }

    @Test
    public void always() {
        assertThat(MomentInterval.ALWAYS.getStart().isInfinite(), is(true));
        assertThat(MomentInterval.ALWAYS.getEnd().isInfinite(), is(true));
    }

    @Test
    public void stream() {
        Moment start = PlainTimestamp.of(2014, 5, 1, 23, 0).atUTC();
        Moment end = PlainTimestamp.of(2014, 5, 2, 1, 30).atUTC();
        MomentInterval interval = MomentInterval.between(start, end);
        MachineTime<?> duration = MachineTime.ofPosixUnits(3601, 0);

        List<Moment> expected = new ArrayList<>();
        expected.add(start);
        expected.add(PlainTimestamp.of(2014, 5, 2, 0, 0, 1).atUTC());
        expected.add(PlainTimestamp.of(2014, 5, 2, 1, 0, 2).atUTC());

        List<Moment> result = interval.stream(duration).collect(Collectors.toList());
        assertThat(result, is(expected));
    }


}