package net.time4j;

import net.time4j.engine.CalendarDays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class CalendarDaysTest {

    @Test
    public void testZERO() {
        assertThat(
            CalendarDays.ZERO.getAmount(),
            is(0L));
        assertThat(
            CalendarDays.ZERO == CalendarDays.of(0L),
            is(true));
    }

    @Test
    public void testONE() {
        assertThat(
            CalendarDays.ONE.getAmount(),
            is(1L));
        assertThat(
            CalendarDays.ONE == CalendarDays.of(1L),
            is(true));
    }

    @Test
    public void getAmount() {
        assertThat(
            CalendarDays.of(Long.MIN_VALUE).getAmount(),
            is(Long.MIN_VALUE));
        assertThat(
            CalendarDays.of(Long.MAX_VALUE).getAmount(),
            is(Long.MAX_VALUE));
    }

    @Test
    public void isZero() {
        assertThat(
            CalendarDays.ZERO.isZero(),
            is(true));
        assertThat(
            CalendarDays.ONE.isZero(),
            is(false));
    }

    @Test
    public void isNegative() {
        assertThat(
            CalendarDays.of(-4).isNegative(),
            is(true));
        assertThat(
            CalendarDays.ZERO.isNegative(),
            is(false));
    }

    @Test
    public void testEquals() {
        CalendarDays c1 = CalendarDays.of(5);
        CalendarDays c2 = CalendarDays.of(5);
        assertThat(
            (c1 == c2),
            is(false));
        assertThat(
            (c1.equals(c2)),
            is(true));
    }

    @Test
    public void abs() {
        assertThat(
            CalendarDays.of(4).abs(),
            is(CalendarDays.of(4)));
        assertThat(
            CalendarDays.of(-4).abs(),
            is(CalendarDays.of(4)));
    }

    @Test
    public void testPlus() {
        CalendarDays c1 = CalendarDays.of(5);
        CalendarDays c2 = CalendarDays.of(3);
        assertThat(
            c1.plus(c2),
            is(CalendarDays.of(8)));
    }

    @Test
    public void testMinus() {
        CalendarDays c1 = CalendarDays.of(5);
        CalendarDays c2 = CalendarDays.of(3);
        assertThat(
            c1.minus(c2),
            is(CalendarDays.of(2)));
    }

    @Test
    public void betweenDates() {
        PlainDate d1 = PlainDate.of(2011, 12, 29);
        PlainDate d2 = PlainDate.of(2012, 3, 1);
        assertThat(
            CalendarDays.between(d1, d2).getAmount(),
            is(63L));
    }

    @Test
    public void addition() {
        PlainDate d1 = PlainDate.of(2011, 12, 29);
        PlainDate d2 = PlainDate.of(2012, 3, 1);
        assertThat(
            d1.plus(CalendarDays.of(63)),
            is(d2));
    }

    @Test
    public void subtraction() {
        PlainDate d1 = PlainDate.of(2011, 12, 29);
        PlainDate d2 = PlainDate.of(2012, 3, 1);
        assertThat(
            d2.minus(CalendarDays.of(63)),
            is(d1));
    }

}