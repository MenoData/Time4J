package net.time4j;

import net.time4j.base.TimeSource;
import net.time4j.scale.LeapSeconds;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class SystemClockTest {

    @Test
    public void currentTimeInMillis() {
        SystemClock clock = SystemClock.INSTANCE;
        assertThat(
            clock.currentTimeInMillis(),
            is(System.currentTimeMillis()));
    }

    @Test
    public void currentTimeInMicros() {
        SystemClock clock = SystemClock.INSTANCE;
        assertThat(
            clock.currentTimeInMicros(),
            is(System.currentTimeMillis() * 1000));
        assertThat(
            SystemClock.INSTANCE.currentTimeInMicros() % 1000,
            is(0L));
    }

    @Test
    public void currentTime() {
        SystemClock clock = SystemClock.MONOTONIC;
        assertThat(
            Math.abs(clock.currentTime().until(SystemClock.INSTANCE.currentTime(), TimeUnit.SECONDS)) < 1,
            is(true));
    }

    @Test
    public void realTimeInMicros() {
        SystemClock clock = SystemClock.MONOTONIC;
        assertThat(
            Math.abs(clock.realTimeInMicros() - SystemClock.INSTANCE.realTimeInMicros()) < 1000,
            is(true));
        long utc = clock.realTimeInMicros() / 1000000;
        long unix = clock.currentTimeInMicros() / 1000000;
        assertThat(
            LeapSeconds.getInstance().strip(utc),
            is(unix));
    }

    @Test
    public void recalibrate() {
        assertThat(SystemClock.INSTANCE.recalibrated() != SystemClock.INSTANCE, is(true));
        long r1 = SystemClock.MONOTONIC.realTimeInMicros();
        long r2 = SystemClock.MONOTONIC.recalibrated().realTimeInMicros();
        assertThat(
            r1 != r2,
            is(true));
    }

    @Test
    public void synchronizedWith() {
        Moment start = Moment.UNIX_EPOCH.plus(2 * 365, TimeUnit.DAYS);
        TimeSource<?> clock = () -> start;
        Moment end = SystemClock.MONOTONIC.synchronizedWith(clock).currentTime();
        assertThat(
            start.until(end, TimeUnit.SECONDS) < 1L,
            is(true)
        );
    }

}
