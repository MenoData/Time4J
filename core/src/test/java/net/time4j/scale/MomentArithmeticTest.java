package net.time4j.scale;

import net.time4j.ClockUnit;
import net.time4j.Duration;
import net.time4j.Moment;
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.SI;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.util.concurrent.TimeUnit;
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
        Timezone tz = Timezone.of("Europe/Berlin");
        Moment end =
            PlainDate.of(2014, Month.MARCH, 30)
                .atStartOfDay().at(tz)
                .with(Duration.of(5, ClockUnit.HOURS).later(tz));
        assertThat(
            end,
            is(
                PlainDate.of(2014, Month.MARCH, 30)
                .atTime(5, 0)
                .atTimezone(ZonalOffset.ofHours(AHEAD_OF_UTC, 2))));
    }

    @Test
    public void minusClockHoursBerlin() {
        Timezone tz = Timezone.of("Europe/Berlin");
        Moment start =
            PlainDate.of(2014, Month.MARCH, 30)
                .atStartOfDay().at(tz)
                .with(Duration.of(5, ClockUnit.HOURS).later(tz));
        Moment end =
            start.with(Duration.of(5, ClockUnit.HOURS).earlier(tz));
        assertThat(
            end,
            is(
                PlainDate.of(2014, Month.MARCH, 30)
                .atStartOfDay()
                .at(tz)));
    }

    @Test
    public void plusPosixHoursBerlin() {
        Moment end =
            PlainDate.of(2014, Month.MARCH, 30)
                .atStartOfDay()
                .at(Timezone.of("Europe/Berlin"))
                .plus(4, TimeUnit.HOURS);
        assertThat(
            end,
            is(
                PlainDate.of(2014, Month.MARCH, 30)
                .atTime(5, 0)
                .atTimezone(ZonalOffset.ofHours(AHEAD_OF_UTC, 2))));
    }

    @Test
    public void minusPosixHoursBerlin() {
        Moment start =
            PlainDate.of(2014, Month.MARCH, 30)
            .atTime(5, 0)
            .at(Timezone.of("Europe/Berlin"));
        Moment end = start.minus(4, TimeUnit.HOURS);
        assertThat(
            end,
            is(
                PlainDate.of(2014, Month.MARCH, 30)
                .atStartOfDay()
                .atTimezone(ZonalOffset.ofHours(AHEAD_OF_UTC, 1))));
    }

    @Test
    public void realHourShiftBerlin() {
        Timezone berlin = Timezone.of("Europe/Berlin");
        Moment start =
            PlainDate.of(2014, Month.MARCH, 30).atStartOfDay().at(berlin);
        Moment end =
            PlainDate.of(2014, Month.MARCH, 30)
                .atStartOfDay()
                .at(berlin)
                .with(Duration.of(5, ClockUnit.HOURS).later(berlin));
        assertThat(start.until(end, TimeUnit.HOURS), is(4L)); // DST-jump
    }

    @Test
    public void localHourShiftBerlin() {
        Timezone berlin = Timezone.of("Europe/Berlin");
        Moment start =
            PlainDate.of(2014, Month.MARCH, 30).atStartOfDay().at(berlin);
        Moment end =
            PlainDate.of(2014, Month.MARCH, 30).atTime(5, 0).at(berlin);
        assertThat(
            Duration.in(berlin, ClockUnit.HOURS).between(start, end),
            is(Duration.of(5, ClockUnit.HOURS))
        );
    }

    @Test
    public void plusSISeconds() {
        assertThat(
            Moment.of(1278028823, TimeScale.UTC).plus(3, SI.SECONDS),
            is(Moment.of(1278028826, TimeScale.UTC)));
    }

    @Test
    public void minusSISeconds() {
        assertThat(
            Moment.of(1278028826, TimeScale.UTC).minus(3, SI.SECONDS),
            is(Moment.of(1278028823, TimeScale.UTC)));
    }

    @Test
    public void betweenSISeconds() {
        assertThat(
            SI.SECONDS.between(
                Moment.of(1278028823, TimeScale.UTC),
                Moment.of(1278028826, TimeScale.UTC)
            ),
            is(3L));
    }

    @Test
    public void betweenTimeUnitSeconds() {
        assertThat(
            Moment.of(1278028823, TimeScale.UTC)
                .until(Moment.of(1278028826, TimeScale.UTC), TimeUnit.SECONDS),
            is(2L));
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

    @Test
    public void plusPosixNanos() {
        Moment expected = Moment.of(1278028825, 2, TimeScale.UTC);
        assertThat(
            Moment.of(1278028823, 999999999, TimeScale.UTC)
                .plus(3, TimeUnit.NANOSECONDS),
            is(expected));
        assertThat(expected.isLeapSecond(), is(false));
        assertThat(
            expected,
            is(Moment.of(1278028800 + 2 * 365 * 86400, 2, TimeScale.POSIX)));
    }

    @Test
    public void minusPosixNanos() {
        Moment expected = Moment.of(1278028823, 999999999, TimeScale.UTC);
        assertThat(
            Moment.of(1278028825, 2, TimeScale.UTC)
                .minus(3, TimeUnit.NANOSECONDS),
            is(expected));
        assertThat(expected.isLeapSecond(), is(false));
        assertThat(
            expected,
            is(
                Moment.of(1278028799 + 2 * 365 * 86400,
                999999999,
                TimeScale.POSIX)));
    }

}