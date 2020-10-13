package net.time4j;

import net.time4j.base.GregorianDate;
import net.time4j.engine.TimePoint;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class DateComparisonTest {

    @Test
    public void equalsMethod() {
        PlainDate d1 = PlainDate.of(1, 1);
        Object d2 = PlainDate.of(1, 1, 1);
        Object d3 = null;
        Object d4 = PlainDate.of(2014, 1, 1);
        Object d5 =
            new GregorianDate() {
                @Override
                public int getYear() {
                    return 1;
                }
                @Override
                public int getMonth() {
                    return 1;
                }
                @Override
                public int getDayOfMonth() {
                    return 1;
                }
            };
        assertThat(d1.equals(d2), is(true));
        assertThat(d1.equals(d3), is(false));
        assertThat(d1.equals(d4), is(false));
        assertThat(d1.equals(d5), is(false));
    }

    @Test
    public void hashCodeMethod() {
        PlainDate d1 = PlainDate.of(1, 1);
        Object d2 = PlainDate.of(1, 1, 1);
        Object d3 = PlainDate.of(2014, 1, 1);
        Object d4 =
            new GregorianDate() {
                @Override
                public int getYear() {
                    return 1;
                }
                @Override
                public int getMonth() {
                    return 1;
                }
                @Override
                public int getDayOfMonth() {
                    return 1;
                }
            };
        assertThat(d1.hashCode() == d2.hashCode(), is(true));
        assertThat(d1.hashCode() == d3.hashCode(), is(false));
        assertThat(d1.hashCode() == d4.hashCode(), is(false));
    }

    @Test
    public void isBefore() {
        assertThat(
            PlainDate.of(2012, 2, 29).isBefore(PlainDate.of(2012, 3, 1)),
            is(true));
        assertThat(
            PlainDate.of(2012, 2, 29).isBefore(PlainDate.of(2012, 2, 29)),
            is(false));
        assertThat(
            PlainDate.of(2012, 2, 29).isBefore(PlainDate.of(2012, 2, 28)),
            is(false));
        assertThat(
            PlainDate.of(2012, 2, 29).isBefore(PlainDate.of(2011, 5, 31)),
            is(false));
        assertThat(
            PlainDate.of(2012, 2, 29).isBefore(PlainDate.of(2013, 1, 1)),
            is(true));
    }

    @Test
    public void isBeforeOrEqual() {
        assertThat(
            PlainDate.of(2012, 2, 29).isBeforeOrEqual(PlainDate.of(2012, 3, 1)),
            is(true));
        assertThat(
            PlainDate.of(2012, 2, 29).isBeforeOrEqual(PlainDate.of(2012, 2, 29)),
            is(true));
        assertThat(
            PlainDate.of(2012, 2, 29).isBeforeOrEqual(PlainDate.of(2012, 2, 28)),
            is(false));
    }

    @Test
    public void isSimultaneous() {
        assertThat(
            PlainDate.of(2012, 2, 29).isSimultaneous(PlainDate.of(2012, 3, 1)),
            is(false));
        assertThat(
            PlainDate.of(2012, 2, 29).isSimultaneous(PlainDate.of(2012, 2, 29)),
            is(true));
    }

    @Test
    public void isAfter() {
        assertThat(
            PlainDate.of(2012, 2, 29).isAfter(PlainDate.of(2012, 3, 1)),
            is(false));
        assertThat(
            PlainDate.of(2012, 2, 29).isAfter(PlainDate.of(2012, 2, 29)),
            is(false));
        assertThat(
            PlainDate.of(2012, 2, 29).isAfter(PlainDate.of(2012, 2, 28)),
            is(true));
        assertThat(
            PlainDate.of(2012, 2, 29).isAfter(PlainDate.of(2011, 5, 31)),
            is(true));
        assertThat(
            PlainDate.of(2012, 2, 29).isAfter(PlainDate.of(2013, 1, 1)),
            is(false));
    }

    @Test
    public void isAfterOrEqual() {
        assertThat(
            PlainDate.of(2012, 2, 29).isAfterOrEqual(PlainDate.of(2012, 3, 1)),
            is(false));
        assertThat(
            PlainDate.of(2012, 2, 29).isAfterOrEqual(PlainDate.of(2012, 2, 29)),
            is(true));
        assertThat(
            PlainDate.of(2012, 2, 29).isAfterOrEqual(PlainDate.of(2012, 2, 28)),
            is(true));
    }

    @Test
    public void compareTo() {
        assertThat(
            PlainDate.of(2012, 2, 29).compareTo(PlainDate.of(2012, 3, 1)) > 0,
            is(false));
        assertThat(
            PlainDate.of(2012, 2, 29).compareTo(PlainDate.of(2012, 2, 29)) > 0,
            is(false));
        assertThat(
            PlainDate.of(2012, 2, 29).compareTo(PlainDate.of(2012, 2, 28)) > 0,
            is(true));
        assertThat(
            PlainDate.of(2012, 2, 29).compareTo(PlainDate.of(2011, 5, 31)) > 0,
            is(true));
        assertThat(
            PlainDate.of(2012, 2, 29).compareTo(PlainDate.of(2013, 1, 1)) > 0,
            is(false));
    }

    @Test
    public void min() {
        PlainDate d1 = PlainDate.of(2014, 4, 21);
        PlainDate d2 = PlainDate.of(2014, 5, 20);
        assertThat(
            TimePoint.min(d1, d2),
            is(d1));
    }

    @Test
    public void max() {
        PlainDate d1 = PlainDate.of(2014, 4, 21);
        PlainDate d2 = PlainDate.of(2014, 5, 20);
        assertThat(
            TimePoint.max(d1, d2),
            is(d2));
    }

    @Test
    public void isAfterAll() {
        PlainDate d1 = PlainDate.of(2014, 4, 21);
        PlainDate d2 = PlainDate.of(2014, 5, 20);
        PlainDate d3 = PlainDate.of(2014, 3, 15);
        assertThat(
            PlainDate.of(2014, 5, 21).isAfterAll(d1, d2, d3),
            is(true));
        assertThat(
            PlainDate.of(2014, 5, 21).isAfterAll(d1, d2.plus(1, CalendarUnit.DAYS), d3),
            is(false));
    }

    @Test
    public void isBeforeAll() {
        PlainDate d1 = PlainDate.of(2014, 4, 21);
        PlainDate d2 = PlainDate.of(2014, 5, 20);
        PlainDate d3 = PlainDate.of(2014, 3, 15);
        assertThat(
            PlainDate.of(2014, 3, 14).isBeforeAll(d1, d2, d3),
            is(true));
        assertThat(
            PlainDate.of(2014, 3, 14).isBeforeAll(d1, d2, d3.minus(1, CalendarUnit.DAYS)),
            is(false));
    }

//    @Test
//    public void minNonCompilable() {
//        PlainDate d1 = PlainDate.of(2014, 4, 21);
//        PlainTime d2 = PlainTime.of(2014, 5, 20);
//        assertThat(
//            TimePoint.min(d1, d2),
//            is(d1));
//    }

}