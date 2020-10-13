package net.time4j.clock;

import net.time4j.CalendarUnit;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.SystemClock;
import net.time4j.base.TimeSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class ClockTest {

    @Test
    public void fixed() throws InterruptedException {
        Moment m = SystemClock.INSTANCE.currentTime();
        TimeSource<Moment> clock = FixedClock.of(m);
        assertThat(clock.currentTime(), is(m));
        assertThat(clock.currentInstant(), is(m.toTemporalAccessor()));

        Thread.sleep(1000);
        assertThat(clock.currentTime(), is(m));
    }

    @Test
    public void offsetOneMinute() {
        Moment m = SystemClock.INSTANCE.currentTime();
        AdjustableClock clock =
            AdjustableClock.of(FixedClock.of(m)).withOffset(1, SECONDS);
        assertThat(clock.currentTime(), is(m.plus(1, SECONDS)));
    }

    @Test
    public void offsetOneDay() {
        AdjustableClock clock =
            AdjustableClock.of(SystemClock.INSTANCE).withOffset(1, DAYS);
        PlainDate tomorrow =
            SystemClock.inLocalView().today().plus(1, CalendarUnit.DAYS);
        assertThat(clock.inLocalView().today(), is(tomorrow));
    }

    @Test
    public void pulsedMicros() {
        AdjustableClock clock =
            AdjustableClock.of(SystemClock.INSTANCE).withPulse(MICROSECONDS);
        assertThat(clock.currentTime().getNanosecond() % 1000, is(0));
    }

    @Test
    public void pulsedMillis() {
        AdjustableClock clock =
            AdjustableClock.of(SystemClock.INSTANCE).withPulse(MILLISECONDS);
        assertThat(clock.currentTime().getNanosecond() % 1000000, is(0));
    }

    @Test
    public void pulsedSeconds() {
        AdjustableClock clock =
            AdjustableClock.of(SystemClock.INSTANCE).withPulse(SECONDS);
        assertThat(clock.currentTime().getNanosecond(), is(0));
    }

    @Test
    public void pulsedMinutes() {
        AdjustableClock clock =
            AdjustableClock.of(SystemClock.INSTANCE).withPulse(MINUTES);
        assertThat(clock.currentTime().getPosixTime() % 60, is(0L));
    }

    @Test
    public void pulsedHours() {
        AdjustableClock clock =
            AdjustableClock.of(SystemClock.INSTANCE).withPulse(HOURS);
        assertThat(clock.currentTime().getPosixTime() % 3600, is(0L));
    }

    @Test
    public void pulsedDays() {
        AdjustableClock clock =
            AdjustableClock.of(SystemClock.INSTANCE).withPulse(DAYS);
        assertThat(clock.currentTime().getPosixTime() % 86400, is(0L));
    }

}