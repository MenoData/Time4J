package net.time4j;

import net.time4j.base.TimeSource;
import net.time4j.scale.LeapSeconds;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


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
        SystemClock clock1 = SystemClock.MONOTONIC;
        SystemClock clock2 = SystemClock.INSTANCE;
        long delta = Math.abs(clock1.realTimeInMicros() - clock2.realTimeInMicros());
        System.out.println("=>" + delta);
        assertThat(
            "Observed delta: " + delta,
            delta < 20000,
            is(true));
        long utc = clock1.realTimeInMicros() / 1000000;
        long unix = clock1.currentTimeInMicros() / 1000000;
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

    @Test
    public void platformView() {
        System.out.println(SystemClock.inPlatformView().now());
    }

    @Test
    public void localTime() {
        System.out.println(SystemClock.inLocalView().now(PlainTime.axis()));
    }

    @Test
    public void today() {
        assertThat(SystemClock.inLocalView().today(), is(PlainDate.nowInSystemTime()));
    }

    @Test
    public void nowTimestamp() {
        PlainTimestamp now = SystemClock.inLocalView().now();
        PlainTimestamp expected = PlainTimestamp.nowInSystemTime();
        assertThat(
            now.minus(1, ClockUnit.NANOS).isBefore(expected) && now.plus(2, ClockUnit.MILLIS).isAfter(expected),
            is(true));
    }

    @Test
    public void nowTime() {
        PlainTime now = SystemClock.inLocalView().now().toTime();
        PlainTime expected = PlainTime.nowInSystemTime();
        assertThat(
            now.minus(1, ClockUnit.NANOS).isBefore(expected) && now.plus(2, ClockUnit.MILLIS).isAfter(expected),
            is(true));
    }

    @Test
    public void nowMoment() {
        Moment now = SystemClock.inLocalView().now().inStdTimezone();
        Moment expected = Moment.nowInSystemTime();
        assertThat(
            now.minus(1, TimeUnit.SECONDS).isBefore(expected)
                && now.plus(2, TimeUnit.SECONDS).isAfter(expected),
            is(true));
    }

}
