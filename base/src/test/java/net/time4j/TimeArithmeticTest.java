package net.time4j;

import net.time4j.base.MathUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.ClockUnit.HOURS;
import static net.time4j.ClockUnit.MICROS;
import static net.time4j.ClockUnit.MILLIS;
import static net.time4j.ClockUnit.MINUTES;
import static net.time4j.ClockUnit.NANOS;
import static net.time4j.ClockUnit.SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class TimeArithmeticTest {

    private static final PlainTime ANY_TIME =
        PlainTime.of(17, 45, 10, 123456789);

    @Test
    public void rollAnyTimeBy5Hours() {
        DayCycles cycles = ANY_TIME.roll(5, HOURS);
        assertThat(cycles.getDayOverflow(), is(0L));
        assertThat(
            cycles.getWallTime(),
            is(PlainTime.of(22, 45, 10, 123456789)));
    }

    @Test
    public void rollAnyTimeBy7Hours() {
        DayCycles cycles = ANY_TIME.roll(7, HOURS);
        assertThat(cycles.getDayOverflow(), is(1L));
        assertThat(
            cycles.getWallTime(),
            is(PlainTime.of(0, 45, 10, 123456789)));
    }

    @Test
    public void rollAnyTimeBy24Hours() {
        DayCycles cycles = ANY_TIME.roll(24, HOURS);
        assertThat(cycles.getDayOverflow(), is(1L));
        assertThat(
            cycles.getWallTime(),
            is(PlainTime.of(17, 45, 10, 123456789)));
    }

    @Test
    public void rollStartingMidnightBy48HoursForward() {
        DayCycles cycles = PlainTime.midnightAtStartOfDay().roll(48, HOURS);
        assertThat(
            cycles.getDayOverflow(),
            is(2L));
        assertThat(
            cycles.getWallTime(),
            is(PlainTime.midnightAtStartOfDay()));
    }

    @Test
    public void rollEndingMidnightBy48HoursForward() {
        DayCycles cycles = PlainTime.midnightAtEndOfDay().roll(48, HOURS);
        assertThat(
            cycles.getDayOverflow(),
            is(3L));
        assertThat(
            cycles.getWallTime(),
            is(PlainTime.midnightAtStartOfDay()));
    }

    @Test
    public void rollEndingMidnightBy48HoursBack() {
        DayCycles cycles = PlainTime.midnightAtEndOfDay().roll(-48, HOURS);
        assertThat(
            cycles.getDayOverflow(),
            is(-1L));
        assertThat(
            cycles.getWallTime(),
            is(PlainTime.midnightAtStartOfDay()));
    }

    @Test
    public void plus24Hours() {
        assertThat(
            PlainTime.of(0).plus(24, HOURS),
            is(PlainTime.of(24)));
        assertThat(
            PlainTime.of(0).plus(48, HOURS),
            is(PlainTime.of(24)));
        assertThat(
            PlainTime.of(24).plus(24, HOURS),
            is(PlainTime.of(24)));
        assertThat(
            ANY_TIME.plus(24, HOURS),
            is(ANY_TIME));
    }

    @Test
    public void minus24Hours() {
        assertThat(
            PlainTime.of(0).minus(24, HOURS),
            is(PlainTime.of(0)));
        assertThat(
            PlainTime.of(0).minus(48, HOURS),
            is(PlainTime.of(0)));
        assertThat(
            PlainTime.of(24).minus(24, HOURS),
            is(PlainTime.of(0)));
        assertThat(
            ANY_TIME.minus(24, HOURS),
            is(ANY_TIME));
    }

    @Test
    public void plusSomeMinutes() {
        assertThat(
            PlainTime.of(23, 59).plus(1, MINUTES),
            is(PlainTime.midnightAtEndOfDay()));
        assertThat(
            PlainTime.of(0).plus(24 * 60 + 1, MINUTES),
            is(PlainTime.of(0, 1)));
        assertThat(
            ANY_TIME.plus(15, MINUTES),
            is(PlainTime.of(18, 0, 10, 123456789)));
        assertThat(
            ANY_TIME.plus(61, MINUTES),
            is(PlainTime.of(18, 46, 10, 123456789)));
    }

    @Test
    public void minusSomeMinutes() {
        assertThat(
            PlainTime.of(0).minus(24 * 60 + 1, MINUTES),
            is(PlainTime.of(23, 59)));
        assertThat(
            ANY_TIME.minus(45, MINUTES),
            is(PlainTime.of(17, 0, 10, 123456789)));
        assertThat(
            ANY_TIME.minus(61, MINUTES),
            is(PlainTime.of(16, 44, 10, 123456789)));
    }

    @Test
    public void plusSomeSeconds() {
        assertThat(
            PlainTime.of(0).plus(86401, SECONDS),
            is(PlainTime.of(0, 0, 1)));
        assertThat(
            ANY_TIME.plus(15, SECONDS),
            is(PlainTime.of(17, 45, 25, 123456789)));
        assertThat(
            ANY_TIME.plus(4000, SECONDS),
            is(PlainTime.of(18, 51, 50, 123456789)));
    }

    @Test
    public void minusSomeSeconds() {
        assertThat(
            PlainTime.of(0).minus(86401, SECONDS),
            is(PlainTime.of(23, 59, 59)));
        assertThat(
            ANY_TIME.minus(45, SECONDS),
            is(PlainTime.of(17, 44, 25, 123456789)));
        assertThat(
            ANY_TIME.minus(3661, SECONDS),
            is(PlainTime.of(16, 44, 9, 123456789)));
    }

    @Test
    public void plusSomeMillis() {
        assertThat(
            PlainTime.of(0).plus(1, MILLIS),
            is(PlainTime.of(0, 0, 0, 1000000)));
        assertThat(
            ANY_TIME.plus(11, MILLIS),
            is(PlainTime.of(17, 45, 10, 134456789)));
    }

    @Test
    public void minusSomeMillis() {
        assertThat(
            PlainTime.of(0).minus(1, MILLIS),
            is(PlainTime.of(23, 59, 59, 999000000)));
        assertThat(
            ANY_TIME.minus(1001, MILLIS),
            is(PlainTime.of(17, 45, 9, 122456789)));
    }

    @Test
    public void plusSomeMicros() {
        assertThat(
            PlainTime.of(0).plus(1, MICROS),
            is(PlainTime.of(0, 0, 0, 1000)));
        assertThat(
            ANY_TIME.plus(11, MICROS),
            is(PlainTime.of(17, 45, 10, 123467789)));
    }

    @Test
    public void minusSomeMicros() {
        assertThat(
            PlainTime.of(0).minus(1, MICROS),
            is(PlainTime.of(23, 59, 59, 999999000)));
        assertThat(
            ANY_TIME.minus(123456, MICROS),
            is(PlainTime.of(17, 45, 10, 789)));
    }

    @Test
    public void plusSomeNanos() {
        assertThat(
            PlainTime.of(0).plus(1, NANOS),
            is(PlainTime.of(0, 0, 0, 1)));
        assertThat(
            ANY_TIME.plus(11, NANOS),
            is(PlainTime.of(17, 45, 10, 123456800)));
    }

    @Test
    public void minusSomeNanos() {
        assertThat(
            PlainTime.of(0).minus(1, NANOS),
            is(PlainTime.of(23, 59, 59, 999999999)));
        assertThat(
            ANY_TIME.minus(123456790, NANOS),
            is(PlainTime.of(17, 45, 9, 999999999)));
    }

    @Test
    public void plusZero() {
        assertThat(ANY_TIME.plus(0, HOURS) == ANY_TIME, is(true));
    }

    @Test
    public void minusZero() {
        assertThat(ANY_TIME.minus(0, HOURS) == ANY_TIME, is(true));
    }

    @Test
    public void hoursBetween() {
        assertThat(
            HOURS.between(
                PlainTime.of(0),
                PlainTime.of(24)
            ),
            is(24L));
        assertThat(
            HOURS.between(
                PlainTime.of(24),
                PlainTime.of(0)
            ),
            is(-24L));
        assertThat(
            HOURS.between(
                PlainTime.of(0),
                ANY_TIME
            ),
            is(17L));
        assertThat(
            HOURS.between(
                ANY_TIME,
                PlainTime.of(0)
            ),
            is(-17L));
    }

    @Test
    public void minutesBetween() {
        assertThat(
            MINUTES.between(
                PlainTime.of(0),
                PlainTime.of(24)
            ),
            is(1440L));
        assertThat(
            MINUTES.between(
                PlainTime.of(24),
                PlainTime.of(0)
            ),
            is(-1440L));
        assertThat(
            MINUTES.between(
                PlainTime.of(0),
                ANY_TIME
            ),
            is(17L * 60 + 45));
        assertThat(
            MINUTES.between(
                ANY_TIME,
                PlainTime.of(0)
            ),
            is(-17L * 60 - 45));
    }

    @Test
    public void secondsBetween() {
        assertThat(
            SECONDS.between(
                PlainTime.of(0),
                PlainTime.of(24)
            ),
            is(86400L));
        assertThat(
            SECONDS.between(
                PlainTime.of(24),
                PlainTime.of(0)
            ),
            is(-86400L));
        assertThat(
            SECONDS.between(
                PlainTime.of(0),
                ANY_TIME
            ),
            is(17L * 3600 + 45 * 60 + 10));
        assertThat(
            SECONDS.between(
                ANY_TIME,
                PlainTime.of(0)
            ),
            is(-17L * 3600 - 45 * 60 - 10));
        assertThat(
            SECONDS.between(
                PlainTime.of(2, 30, 5, 123),
                PlainTime.of(2, 30, 8, 122)
            ),
            is(2L));
        assertThat(
            SECONDS.between(
                PlainTime.of(2, 30, 5, 123),
                PlainTime.of(2, 30, 8, 123)
            ),
            is(3L));
        assertThat(
            SECONDS.between(
                PlainTime.of(2, 30, 5, 123),
                PlainTime.of(2, 30, 8, 124)
            ),
            is(3L));
    }

    @Test
    public void millisBetween() {
        long expected = (17L * 3600 + 45 * 60 + 10) * 1000 + 123;
        assertThat(
            MILLIS.between(PlainTime.of(0), ANY_TIME),
            is(expected));
        assertThat(
            MILLIS.between(ANY_TIME, PlainTime.of(0)),
            is(-expected));
    }

    @Test
    public void microsBetween() {
        long expected = (17L * 3600 + 45 * 60 + 10) * 1000000 + 123456;
        assertThat(
            MICROS.between(PlainTime.of(0), ANY_TIME),
            is(expected));
        assertThat(
            MICROS.between(ANY_TIME, PlainTime.of(0)),
            is(-expected));
    }

    @Test
    public void nanosBetween() {
        long expected = (17L * 3600 + 45 * 60 + 10) * 1000000000 + 123456789;
        assertThat(
            NANOS.between(PlainTime.of(0), ANY_TIME),
            is(expected));
        assertThat(
            NANOS.between(ANY_TIME, PlainTime.of(0)),
            is(-expected));
    }

    @Test(expected=ArithmeticException.class)
    public void overflow() {
        PlainTime.of(1).plus(Long.MAX_VALUE, HOURS);
    }

    @Test
    public void plusLongMaxHours() {
        assertThat(
            PlainTime.of(0).plus(Long.MAX_VALUE, HOURS),
            is(PlainTime.of(MathUtils.floorModulo(Long.MAX_VALUE, 24))));
    }

    @Test
    public void plusLongMinHours() {
        assertThat(
            PlainTime.of(0).plus(Long.MIN_VALUE, HOURS),
            is(PlainTime.of(MathUtils.floorModulo(Long.MIN_VALUE, 24))));
    }

}
