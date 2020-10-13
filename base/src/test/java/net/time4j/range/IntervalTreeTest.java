package net.time4j.range;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class IntervalTreeTest {

    @Test
    public void size() {
        MomentInterval i1 =
            TimestampInterval.between(
                PlainTimestamp.of(2014, 2, 28, 0, 0),
                PlainTimestamp.of(2014, 5, 31, 23, 59)
            ).atUTC();
        MomentInterval i2 =
            TimestampInterval.between(
                PlainTimestamp.of(2014, 5, 31, 0, 0),
                PlainTimestamp.of(2014, 6, 1, 0, 0)
            ).atUTC();
        MomentInterval i3 =
            TimestampInterval.between(
                PlainTimestamp.of(2014, 6, 15, 0, 0),
                PlainTimestamp.of(2014, 6, 30, 0, 0)
            ).atUTC();
        IntervalTree<Moment, MomentInterval> tree = IntervalTree.onMomentAxis(Arrays.asList(i3, i1, i2, i3));
        assertThat(
            tree.size(),
            is(4)); // containing duplicate interval i3
        tree = IntervalTree.onMomentAxis(Arrays.asList(i3, i1, i2, i3.collapse()));
        assertThat(
            tree.size(),
            is(3)); // exclusion of empty interval
        assertThat(
            tree.isEmpty(),
            is(false));
    }

    @Test
    public void visitor() {
        TimestampInterval i1 =
            TimestampInterval.between(
                PlainTimestamp.of(2014, 2, 28, 0, 0),
                PlainTimestamp.of(2014, 5, 31, 23, 59));
        TimestampInterval i2 =
            TimestampInterval.between(
                PlainTimestamp.of(2014, 5, 31, 0, 0),
                PlainTimestamp.of(2014, 6, 1, 0, 0));
        TimestampInterval i3 =
            TimestampInterval.between(
                PlainTimestamp.of(2014, 6, 15, 0, 0),
                PlainTimestamp.of(2014, 6, 30, 0, 0));
        IntervalTree<PlainTimestamp, TimestampInterval> tree = IntervalTree.onTimestampAxis(Arrays.asList(i3, i1, i2));
        List<TimestampInterval> result = new ArrayList<>();
        tree.accept(
            (interval) -> {
                result.add(interval);
                return false;
            }
        );
        assertThat(
            result,
            is(Arrays.asList(i1, i2, i3))
        );
        assertThat(result.size(), is(3));
        assertThat(result.get(0), is(i1));
        assertThat(result.get(1), is(i2));
        assertThat(result.get(2), is(i3));
    }

    @Test
    public void findIntersectionsWithInterval() {
        DateInterval i1 =
            DateInterval.between(
                PlainDate.of(2014, 2, 28),
                PlainDate.of(2014, 5, 31));
        DateInterval i2 =
            DateInterval.between(
                PlainDate.of(2014, 5, 31),
                PlainDate.of(2014, 6, 1));
        DateInterval i3 =
            DateInterval.between(
                PlainDate.of(2014, 6, 15),
                PlainDate.of(2014, 6, 30));
        IntervalTree<PlainDate, DateInterval> tree = IntervalTree.onDateAxis(Arrays.asList(i3, i1, i2));

        assertThat(
            tree.findIntersections(
                DateInterval.between(
                    PlainDate.of(2014, 6, 2),
                    PlainDate.of(2014, 6, 14)
                )
            ).isEmpty(),
            is(true));
        assertThat(
            tree.findIntersections(
                DateInterval.between(
                    PlainDate.of(2014, 6, 1),
                    PlainDate.of(2014, 6, 14)
                )
            ),
            is(Collections.singletonList(i2)));
        assertThat(
            tree.findIntersections(
                DateInterval.between(
                    PlainDate.of(2014, 6, 1),
                    PlainDate.of(2014, 6, 15)
                )
            ),
            is(Arrays.asList(i2, i3)));
        assertThat(
            tree.findIntersections(
                DateInterval.between(
                    PlainDate.of(2014, 5, 31),
                    PlainDate.of(2014, 6, 15)
                )
            ),
            is(Arrays.asList(i1, i2, i3)));
    }

    @Test
    public void findIntersectionsWithTimePoint() {
        ClockInterval i1 =
            ClockInterval.between(
                PlainTime.of(20, 15, 50),
                PlainTime.of(20, 38, 40));
        ClockInterval i2 =
            ClockInterval.between(
                PlainTime.of(20, 38, 39),
                PlainTime.of(20, 40, 0));
        ClockInterval i3 =
            ClockInterval.between(
                PlainTime.of(20, 40, 0),
                PlainTime.of(20, 45, 30));
        IntervalTree<PlainTime, ClockInterval> tree = IntervalTree.onClockAxis(Arrays.asList(i3, i1, i2));

        assertThat(
            tree.findIntersections(PlainTime.of(21, 0)).isEmpty(),
            is(true));
        assertThat(
            tree.findIntersections(PlainTime.of(20, 38, 39)),
            is(Arrays.asList(i1, i2)));
        assertThat(
            tree.findIntersections(PlainTime.of(20, 40, 0)),
            is(Collections.singletonList(i3)));
    }

    @Test
    public void findIntersectionsUsingInfiniteBoundaries() {
        DateInterval i1 = DateInterval.until(PlainDate.of(2017, 2, 11));
        DateInterval i2 = DateInterval.since(PlainDate.of(2017, 2, 12));
        IntervalTree<PlainDate, DateInterval> tree = IntervalTree.onDateAxis(Arrays.asList(i2, i1));

        assertThat(
            tree.findIntersections(PlainDate.of(2017, 1, 31)),
            is(Arrays.asList(i1)));
        assertThat(
            tree.findIntersections(PlainDate.of(2017, 3, 31)),
            is(Collections.singletonList(i2)));
    }

    @Test
    public void onTraditionalTimeLine() {
        SimpleInterval<Date> i1 =
            SimpleInterval.between(
                new Date(2L),
                new Date(27L)
            );
        SimpleInterval<Date> i2 =
            SimpleInterval.between(
                new Date(27L),
                new Date(30L)
            );
        SimpleInterval<Date> i3 =
            SimpleInterval.between(
                new Date(2L),
                new Date(2L)
            );
        IntervalTree<Date, SimpleInterval<Date>> tree =
            IntervalTree.onTraditionalTimeLine(Arrays.asList(i3, i1, i2, i2));
        assertThat(
            tree.size(),
            is(3)); // containing duplicate interval i2
        assertThat(
            tree.findIntersections(i3),
            is(Collections.emptyList()));
        assertThat(
            tree.findIntersections(new Date(26L)),
            is(Collections.singletonList(i1)));
        assertThat(
            tree.findIntersections(new Date(27L)),
            is(Arrays.asList(i2, i2)));
    }

    @Test
    public void onInstantTimeLine() {
        SimpleInterval<Instant> i = SimpleInterval.between(Instant.EPOCH, Instant.now());
        IntervalTree<Instant, SimpleInterval<Instant>> tree =
            IntervalTree.onInstantTimeLine(Collections.singletonList(i));
        tree.accept(
            (interval) -> {
                System.out.println(interval);
                return false;
            }
        );
        assertThat(tree.contains(i), is(true));
    }

    @Test
    public void contains() {
        DateInterval i1 =
            DateInterval.between(
                PlainDate.of(2014, 2, 28),
                PlainDate.of(2014, 5, 31));
        DateInterval i2 =
            DateInterval.between(
                PlainDate.of(2014, 5, 31),
                PlainDate.of(2014, 6, 1));
        DateInterval i3 =
            DateInterval.between(
                PlainDate.of(2014, 6, 15),
                PlainDate.of(2014, 6, 30));
        IntervalTree<PlainDate, DateInterval> tree = IntervalTree.onDateAxis(Arrays.asList(i3, i1, i2));

        assertThat(
            tree.contains(i1),
            is(true));
        assertThat(
            tree.contains(i2),
            is(true));
        assertThat(
            tree.contains(i3),
            is(true));

        assertThat(
            tree.contains(i1.withOpenStart()),
            is(false));
        assertThat(
            tree.contains(i1.withOpenEnd()),
            is(false));
        assertThat(
            tree.contains(i1.withOpenEnd().withEnd(PlainDate.of(2014, 6, 1))),
            is(false));
    }

    @Test
    public void forEach() {
        DateInterval i1 =
            DateInterval.between(
                PlainDate.of(2014, 2, 28),
                PlainDate.of(2014, 5, 31));
        DateInterval i2 =
            DateInterval.between(
                PlainDate.of(2014, 5, 31),
                PlainDate.of(2014, 6, 1));
        DateInterval i3 =
            DateInterval.between(
                PlainDate.of(2014, 6, 15),
                PlainDate.of(2014, 6, 30));

        IntervalTree<PlainDate, DateInterval> tree = IntervalTree.onDateAxis(Arrays.asList(i3, i1, i2));

        List<DateInterval> coll1 = new ArrayList<>();
        List<DateInterval> coll2 = new ArrayList<>();

        for (DateInterval i : tree) {
            coll1.add(i);
        }
        for (DateInterval i : tree) {
            coll2.add(i);
        }

        assertThat(coll1.equals(coll2), is(true));
        assertThat(coll1.equals(Arrays.asList(i1, i2, i3)), is(true));
    }

    @Test
    public void isEmpty() {
        IntervalTree<PlainDate, DateInterval> tree = IntervalTree.onDateAxis(Arrays.asList());
        assertThat(tree.isEmpty(), is(true));
    }

}
