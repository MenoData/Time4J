package net.time4j;

import net.time4j.base.WallTime;
import net.time4j.engine.TimePoint;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class TimeComparisonTest {

    @Test
    public void equalsMethod() {
        PlainTime d1 = PlainTime.of(19, 45, 30);
        Object d2 = PlainTime.of(19, 45, 30, 0);
        Object d3 = PlainTime.of(19, 45, 30, 1);
        Object d4 = null;
        Object d5 =
            new WallTime() {
                @Override
                public int getHour() {
                    return 19;
                }
                @Override
                public int getMinute() {
                    return 45;
                }
                @Override
                public int getSecond() {
                    return 30;
                }
                @Override
                public int getNanosecond() {
                    return 0;
                }
            };
        assertThat(d1.equals(d2), is(true));
        assertThat(d1.equals(d3), is(false));
        assertThat(d1.equals(d4), is(false));
        assertThat(d1.equals(d5), is(false));
    }

    @Test
    public void hashCodeMethod() {
        PlainTime d1 = PlainTime.of(19, 45, 30);
        Object d2 = PlainTime.of(19, 45, 30, 0);
        Object d3 = PlainTime.of(19, 45, 30, 1);
        Object d4 =
            new WallTime() {
                @Override
                public int getHour() {
                    return 19;
                }
                @Override
                public int getMinute() {
                    return 45;
                }
                @Override
                public int getSecond() {
                    return 30;
                }
                @Override
                public int getNanosecond() {
                    return 0;
                }
            };
        assertThat(d1.hashCode() == d2.hashCode(), is(true));
        assertThat(d1.hashCode() == d3.hashCode(), is(false));
        assertThat(d1.hashCode() == d4.hashCode(), is(false));
    }

    @Test
    public void isBefore() {
        assertThat(
            PlainTime.of(19, 45, 30, 1).isBefore(PlainTime.of(19, 45, 30, 2)),
            is(true));
        assertThat(
            PlainTime.of(19, 45, 30).isBefore(PlainTime.of(20)),
            is(true));
        assertThat(
            PlainTime.of(19, 45, 30).isBefore(PlainTime.of(19, 45, 30)),
            is(false));
        assertThat(
            PlainTime.of(20).isBefore(PlainTime.of(19, 45, 30)),
            is(false));
        assertThat(
            PlainTime.of(23, 59, 59, 999999999).isBefore(PlainTime.of(24)),
            is(true));
    }

    @Test
    public void isSimultaneous() {
        assertThat(
            PlainTime.of(19, 45, 30).isSimultaneous(PlainTime.of(19, 45, 30)),
            is(true));
        assertThat(
            PlainTime.of(0).isSimultaneous(PlainTime.of(24)),
            is(false));
    }

    @Test
    public void isAfter() {
        assertThat(
            PlainTime.of(19, 45, 30, 1).isAfter(PlainTime.of(19, 45, 30, 2)),
            is(false));
        assertThat(
            PlainTime.of(19, 45, 30).isAfter(PlainTime.of(20)),
            is(false));
        assertThat(
            PlainTime.of(19, 45, 30).isAfter(PlainTime.of(19, 45, 30)),
            is(false));
        assertThat(
            PlainTime.of(20).isAfter(PlainTime.of(19, 45, 30)),
            is(true));
        assertThat(
            PlainTime.of(23, 59, 59, 999999999).isAfter(PlainTime.of(24)),
            is(false));
    }

    @Test
    public void compareTo() {
        assertThat(
            PlainTime.of(23, 59, 59, 999999999)
                .compareTo(PlainTime.of(23, 59, 59, 999999998)) > 0,
            is(true));
        assertThat(
            PlainTime.of(23, 59, 59, 999999998)
                .compareTo(PlainTime.of(23, 59, 59, 999999999)) > 0,
            is(false));
    }

    @Test
    public void min() {
        PlainTime t1 = PlainTime.of(22, 4, 21);
        PlainTime t2 = PlainTime.of(22, 5, 20);
        assertThat(
            TimePoint.min(t1, t2),
            is(t1));
    }

    @Test
    public void max() {
        PlainTime t1 = PlainTime.of(22, 4, 21);
        PlainTime t2 = PlainTime.of(23, 5, 20);
        assertThat(
            TimePoint.max(t1, t2),
            is(t2));
    }

}