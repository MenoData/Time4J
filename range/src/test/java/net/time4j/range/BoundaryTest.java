package net.time4j.range;

import net.time4j.CalendarUnit;
import net.time4j.PlainDate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;


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

        assertThat(b1.isAfter(b2), is(false));
        assertThat(b2.isAfter(b1), is(true));
        assertThat(b3.isAfter(b1), is(false));
        assertThat(b4.isAfter(b1), is(true));
        assertThat(b1.isAfter(b3), is(true));
        assertThat(b1.isAfter(b4), is(false));

        assertThat(b1.isAfter(b5), is(true));
        assertThat(b1.isAfter(b6), is(false));

        assertThat(b5.isAfter(b1), is(false));
        assertThat(b6.isAfter(b1), is(true));

        assertThat(b5.isAfter(b6), is(false));
        assertThat(b6.isAfter(b5), is(true));
        assertThat(b1.isAfter(b1), is(false));
    }

    @Test
    public void isBefore() {
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

        assertThat(b1.isBefore(b2), is(true));
        assertThat(b2.isBefore(b1), is(false));
        assertThat(b3.isBefore(b1), is(true));
        assertThat(b4.isBefore(b1), is(false));
        assertThat(b1.isBefore(b3), is(false));
        assertThat(b1.isBefore(b4), is(true));

        assertThat(b1.isBefore(b5), is(false));
        assertThat(b1.isBefore(b6), is(true));

        assertThat(b5.isBefore(b1), is(true));
        assertThat(b6.isBefore(b1), is(false));

        assertThat(b5.isBefore(b6), is(true));
        assertThat(b6.isBefore(b5), is(false));
        assertThat(b1.isBefore(b1), is(false));
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

        assertThat(b1.isSimultaneous(b2), is(false));
        assertThat(b2.isSimultaneous(b1), is(false));
        assertThat(b3.isSimultaneous(b1), is(false));
        assertThat(b4.isSimultaneous(b1), is(false));
        assertThat(b1.isSimultaneous(b3), is(false));
        assertThat(b1.isSimultaneous(b4), is(false));
        assertThat(b2.isSimultaneous(b4), is(true));

        assertThat(b1.isSimultaneous(b5), is(false));
        assertThat(b1.isSimultaneous(b6), is(false));

        assertThat(b5.isSimultaneous(b1), is(false));
        assertThat(b6.isSimultaneous(b1), is(false));

        assertThat(b5.isSimultaneous(b6), is(false));
        assertThat(b6.isSimultaneous(b5), is(false));

        assertThat(b1.isSimultaneous(b1), is(true));
        assertThat(
            b1.isSimultaneous(Boundary.of(IntervalEdge.OPEN, d1)),
            is(true));

        assertThat(b5.isSimultaneous(b5), is(true));
        assertThat(b6.isSimultaneous(b6), is(true));
    }

}