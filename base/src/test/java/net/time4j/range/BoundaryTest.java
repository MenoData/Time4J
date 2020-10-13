package net.time4j.range;

import net.time4j.CalendarUnit;
import net.time4j.PlainDate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class BoundaryTest {

    @Test
    public void getTemporalInfinitePast() {
        assertThat(
            Boundary.infinitePast().getTemporal(),
            nullValue());
    }

    @Test
    public void getTemporalInfiniteFuture() {
        assertThat(
            Boundary.infiniteFuture().getTemporal(),
            nullValue());
    }

    @Test
    public void getTemporalOpen() {
        PlainDate date = PlainDate.of(2014, 5, 21);
        assertThat(
            Boundary.of(IntervalEdge.OPEN, date).getTemporal(),
            is(date));
    }

    @Test
    public void getTemporalClosed() {
        PlainDate date = PlainDate.of(2014, 5, 21);
        assertThat(
            Boundary.of(IntervalEdge.CLOSED, date).getTemporal(),
            is(date));
    }

    @Test
    public void IsOpen() {
        PlainDate date = PlainDate.of(2014, 5, 21);
        assertThat(
            Boundary.of(IntervalEdge.CLOSED, date).isOpen(),
            is(false));
        assertThat(
            Boundary.of(IntervalEdge.OPEN, date).isOpen(),
            is(true));
        assertThat(
            Boundary.infinitePast().isOpen(),
            is(true));
        assertThat(
            Boundary.infiniteFuture().isOpen(),
            is(true));
    }

    @Test
    public void IsClosed() {
        PlainDate date = PlainDate.of(2014, 5, 21);
        assertThat(
            Boundary.of(IntervalEdge.CLOSED, date).isClosed(),
            is(true));
        assertThat(
            Boundary.of(IntervalEdge.OPEN, date).isClosed(),
            is(false));
        assertThat(
            Boundary.infinitePast().isClosed(),
            is(false));
        assertThat(
            Boundary.infiniteFuture().isClosed(),
            is(false));
    }

    @Test
    public void IsInfinite() {
        PlainDate date = PlainDate.of(2014, 5, 21);
        assertThat(
            Boundary.of(IntervalEdge.CLOSED, date).isInfinite(),
            is(false));
        assertThat(
            Boundary.of(IntervalEdge.OPEN, date).isInfinite(),
            is(false));
        assertThat(
            Boundary.infinitePast().isInfinite(),
            is(true));
        assertThat(
            Boundary.infiniteFuture().isInfinite(),
            is(true));
    }

    @Test
    public void testEqualsInfinite() {
        PlainDate date = PlainDate.of(2014, 5, 21);
        Boundary<PlainDate> b1 = Boundary.infinitePast();
        Boundary<PlainDate> b2 = Boundary.of(IntervalEdge.CLOSED, date);
        Boundary<PlainDate> b3 = Boundary.infiniteFuture();

        assertThat(b1.equals(b2), is(false));
        assertThat(b1.equals(b3), is(false));
        assertThat(b2.equals(b3), is(false));

        assertThat(b1.equals(b1), is(true));
        assertThat(b3.equals(b3), is(true));
    }

    @Test
    public void testEqualsNormal() {
        PlainDate date = PlainDate.of(2014, 5, 21);
        Boundary<PlainDate> b1 = Boundary.of(IntervalEdge.OPEN, date);
        Boundary<PlainDate> b2 = Boundary.of(IntervalEdge.CLOSED, date);
        Boundary<PlainDate> b3 = Boundary.infinitePast();
        Boundary<PlainDate> b4 = Boundary.infiniteFuture();
        Boundary<PlainDate> b5 =
            Boundary.of(IntervalEdge.CLOSED, date.minus(1, CalendarUnit.DAYS));
        Boundary<PlainDate> b6 = Boundary.of(IntervalEdge.OPEN, date);

        assertThat(b1.equals(b2), is(false));
        assertThat(b1.equals(b3), is(false));
        assertThat(b1.equals(b4), is(false));
        assertThat(b2.equals(b3), is(false));
        assertThat(b2.equals(b4), is(false));
        assertThat(b3.equals(b4), is(false));

        // same quasi-temporal state, but different edge
        assertThat(b1.equals(b5), is(false));

        assertThat(b1.equals(b1), is(true)); // identity
        assertThat(b1.equals(b6), is(true)); // state comparison
    }

    @Test
    public void testHashCode() {
        PlainDate date = PlainDate.of(2014, 5, 21);
        Boundary<PlainDate> b1 = Boundary.of(IntervalEdge.OPEN, date);
        Boundary<PlainDate> b2 = Boundary.of(IntervalEdge.OPEN, date);
        assertThat(b1.hashCode(), is(b2.hashCode()));
    }

    @Test
    public void testToStringInfinitePast() {
        assertThat(Boundary.infinitePast().toString(), is("(-∞)"));
    }

    @Test
    public void testToStringInfiniteFuture() {
        assertThat(Boundary.infiniteFuture().toString(), is("(+∞)"));
    }

    @Test
    public void testToStringOpen() {
        PlainDate date = PlainDate.of(2014, 5, 21);
        Boundary<PlainDate> b1 = Boundary.of(IntervalEdge.OPEN, date);
        assertThat(b1.toString(), is("(2014-05-21)"));
    }

    @Test
    public void testToStringClosed() {
        PlainDate date = PlainDate.of(2014, 5, 21);
        Boundary<PlainDate> b1 = Boundary.of(IntervalEdge.CLOSED, date);
        assertThat(b1.toString(), is("[2014-05-21]"));
    }

    @Test
    public void isAfter() {
        PlainDate d1 = PlainDate.of(2014, 5, 21);
        Boundary<PlainDate> b1 = Boundary.of(IntervalEdge.OPEN, d1);

        PlainDate d2 = PlainDate.of(2014, 5, 22);
        Boundary<PlainDate> b2 = Boundary.of(IntervalEdge.OPEN, d2);

        PlainDate d3 = PlainDate.of(2014, 5, 20);
        Boundary<PlainDate> b3 = Boundary.of(IntervalEdge.CLOSED, d3);

        PlainDate d4 = PlainDate.of(2014, 5, 22);
        Boundary<PlainDate> b4 = Boundary.of(IntervalEdge.CLOSED, d4);

        Boundary<PlainDate> b5 = Boundary.infinitePast();
        Boundary<PlainDate> b6 = Boundary.infiniteFuture();

        assertThat(Boundary.isAfter(b1, b2), is(false));
        assertThat(Boundary.isAfter(b2, b1), is(true));
        assertThat(Boundary.isAfter(b3, b1), is(false));
        assertThat(Boundary.isAfter(b4, b1), is(true));
        assertThat(Boundary.isAfter(b1, b3), is(true));
        assertThat(Boundary.isAfter(b1, b4), is(false));

        assertThat(Boundary.isAfter(b1, b5), is(true));
        assertThat(Boundary.isAfter(b1, b6), is(false));

        assertThat(Boundary.isAfter(b5, b1), is(false));
        assertThat(Boundary.isAfter(b6, b1), is(true));

        assertThat(Boundary.isAfter(b5, b6), is(false));
        assertThat(Boundary.isAfter(b6, b5), is(true));
        assertThat(Boundary.isAfter(b1, b1), is(false));
    }

    @Test
    public void isSimultaneous() {
        PlainDate d1 = PlainDate.of(2014, 5, 21);
        Boundary<PlainDate> b1 = Boundary.of(IntervalEdge.OPEN, d1);

        PlainDate d2 = PlainDate.of(2014, 5, 22);
        Boundary<PlainDate> b2 = Boundary.of(IntervalEdge.OPEN, d2);

        PlainDate d3 = PlainDate.of(2014, 5, 20);
        Boundary<PlainDate> b3 = Boundary.of(IntervalEdge.CLOSED, d3);

        PlainDate d4 = PlainDate.of(2014, 5, 22);
        Boundary<PlainDate> b4 = Boundary.of(IntervalEdge.CLOSED, d4);

        Boundary<PlainDate> b5 = Boundary.infinitePast();
        Boundary<PlainDate> b6 = Boundary.infiniteFuture();

        assertThat(Boundary.isSimultaneous(b1, b2), is(false));
        assertThat(Boundary.isSimultaneous(b2, b1), is(false));
        assertThat(Boundary.isSimultaneous(b3, b1), is(false));
        assertThat(Boundary.isSimultaneous(b4, b1), is(false));
        assertThat(Boundary.isSimultaneous(b1, b3), is(false));
        assertThat(Boundary.isSimultaneous(b1, b4), is(false));
        assertThat(Boundary.isSimultaneous(b2, b4), is(true));

        assertThat(Boundary.isSimultaneous(b1, b5), is(false));
        assertThat(Boundary.isSimultaneous(b1, b6), is(false));

        assertThat(Boundary.isSimultaneous(b5, b1), is(false));
        assertThat(Boundary.isSimultaneous(b6, b1), is(false));

        assertThat(Boundary.isSimultaneous(b5, b6), is(false));
        assertThat(Boundary.isSimultaneous(b6, b5), is(false));

        assertThat(Boundary.isSimultaneous(b1, b1), is(true));
        assertThat(
            Boundary.isSimultaneous(b1, Boundary.of(IntervalEdge.OPEN, d1)),
            is(true));

        assertThat(Boundary.isSimultaneous(b5, b5), is(true));
        assertThat(Boundary.isSimultaneous(b6, b6), is(true));
    }

}