package net.time4j.range;

import net.time4j.PlainDate;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class WindowTest {

    @Test
    public void toIntervals() {
        DateInterval i1 =
            DateInterval.between(
                PlainDate.of(2014, 2, 28),
                PlainDate.of(2014, 5, 31));
        DateInterval i2 =
            DateInterval.between(
                PlainDate.of(2014, 2, 27),
                PlainDate.of(2014, 6, 1));
        List<DateInterval> intervals = new ArrayList<DateInterval>();
        intervals.add(i1);
        intervals.add(i2);
        intervals = TimeWindows.onDateAxis().append(intervals).toIntervals();
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
        TimeWindows<DateInterval> windows = TimeWindows.onDateAxis();
        windows = windows.append(i1).append(i2).append(i3).append(i4);
        List<DateInterval> gaps = windows.gaps().toIntervals();
        DateInterval expected =
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
        TimeWindows<DateInterval> windows = TimeWindows.onDateAxis();
        windows = windows.append(i1).append(i2).append(i3);
        List<DateInterval> gaps = windows.gaps().toIntervals();
        DateInterval expected1 =
            DateInterval.between(
                PlainDate.of(2014, 4, 6),
                PlainDate.of(2014, 4, 9));
        DateInterval expected2 =
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
        TimeWindows<DateInterval> windows = TimeWindows.onDateAxis();
        windows = windows.append(i1).append(i2).append(i3).append(i4);
        List<DateInterval> gaps = windows.gaps().toIntervals();
        DateInterval expected =
            DateInterval.between(
                PlainDate.of(2014, 6, 2),
                PlainDate.of(2014, 6, 14));

        assertThat(gaps.size(), is(1));
        assertThat(gaps.get(0), is(expected));
    }

}