package net.time4j.scale;

import net.time4j.ClockUnit;
import net.time4j.Duration;
import net.time4j.Moment;
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.SI;
import net.time4j.tz.TZID;
import net.time4j.tz.ZonalOffset;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.tz.ZonalOffset.Sign.AHEAD_OF_UTC;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class MomentArithmeticTest {

    @Test
    public void plusClockHoursBerlin() {
        TZID timezone = TZID.EUROPE.BERLIN;
        Moment utc =
            PlainDate.of(2014, Month.MARCH, 30)
                .atStartOfDay(timezone)
                .with(Duration.of(5, ClockUnit.HOURS).later(timezone));
        assertThat(
            utc,
            is(
                PlainDate.of(2014, Month.MARCH, 30)
                .atTime(5, 0).atOffset(ZonalOffset.of(AHEAD_OF_UTC, 2))));
        long t1 =
            PlainDate.of(2014, Month.MARCH, 30).atStartOfDay(timezone)
                .getPosixTime();
        long t2 = utc.getPosixTime();
        assertThat(t2 - t1, is(4 * 3600L)); // 4 SI-hours later
    }

    @Test
    public void plusClockSeconds() {
        assertThat(
            Moment.of(1278028823, TimeScale.UTC)
                .with(Duration.of(3, ClockUnit.SECONDS).laterAtUTC()),
            is(Moment.of(1278028827, TimeScale.UTC)));
    }

    @Test
    public void plusSISeconds() {
        assertThat(
            Moment.of(1278028823, TimeScale.UTC).plus(3, SI.SECONDS),
            is(Moment.of(1278028826, TimeScale.UTC)));
    }

    @Test
    public void minusClockSeconds() {
        assertThat(
            Moment.of(1278028826, TimeScale.UTC)
                .with(Duration.of(3, ClockUnit.SECONDS).earlierAtUTC()),
            is(Moment.of(1278028822, TimeScale.UTC)));
    }

    @Test
    public void minusSISeconds() {
        assertThat(
            Moment.of(1278028826, TimeScale.UTC).minus(3, SI.SECONDS),
            is(Moment.of(1278028823, TimeScale.UTC)));
    }

    @Test
    public void plusSINanos() {
        Moment expected = Moment.of(1278028824, 2, TimeScale.UTC);
        assertThat(
            Moment.of(1278028823, 999999999, TimeScale.UTC)
                .plus(3, SI.NANOSECONDS),
            is(expected));
        assertThat(expected.isLeapSecond(), is(true));
    }

    @Test
    public void minusSINanos() {
        assertThat(
            Moment.of(1278028824, 2, TimeScale.UTC).minus(3, SI.NANOSECONDS),
            is(Moment.of(1278028823, 999999999, TimeScale.UTC)));
    }

}