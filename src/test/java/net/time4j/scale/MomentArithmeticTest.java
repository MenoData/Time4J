package net.time4j.scale;

import net.time4j.CalendarUnit;
import net.time4j.ClockUnit;
import net.time4j.Duration;
import net.time4j.Moment;
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.SI;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.tz.ZonalOffset.Sign.AHEAD_OF_UTC;
import static net.time4j.tz.ZonalOffset.Sign.BEHIND_UTC;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class MomentArithmeticTest {

    @Test
    public void plusCalendarDaysSamoa() {
        TZID timezone = TZID.PACIFIC.APIA;
        Moment start =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay(timezone);
        Moment end =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay(timezone)
                .with(Duration.of(2, CalendarUnit.DAYS).later(timezone));
        assertThat(
            Timezone.of(timezone).getOffset(start),
            is(ZonalOffset.ofHours(BEHIND_UTC, 10)));
        assertThat(
            Timezone.of(timezone).getOffset(end),
            is(ZonalOffset.ofHours(AHEAD_OF_UTC, 14)));
        assertThat(
            end,
            is(
                PlainDate.of(2011, Month.DECEMBER, 31)
                    .at(PlainTime.midnightAtStartOfDay())
                    .atOffset(ZonalOffset.ofHours(AHEAD_OF_UTC, 14))));
        // crossing international dateline border
        assertThat(start.until(end, TimeUnit.DAYS), is(1L));
        assertThat(start.until(end, TimeUnit.HOURS), is(24L));
        assertThat(start.until(end, TimeUnit.MINUTES), is(1440L));
        assertThat(start.until(end, TimeUnit.SECONDS), is(86400L));
    }

    @Test
    public void plusClockHoursBerlin() {
        TZID timezone = TZID.EUROPE.BERLIN;
        Moment start =
            PlainDate.of(2014, Month.MARCH, 30).atStartOfDay(timezone);
        Moment end =
            PlainDate.of(2014, Month.MARCH, 30)
                .atStartOfDay(timezone)
                .with(Duration.of(5, ClockUnit.HOURS).later(timezone));
        assertThat(
            end,
            is(
                PlainDate.of(2014, Month.MARCH, 30)
                .atTime(5, 0).atOffset(ZonalOffset.ofHours(AHEAD_OF_UTC, 2))));
        assertThat(start.until(end, TimeUnit.HOURS), is(4L)); // DST-jump
    }

    @Test
    public void minusClockHoursBerlin() {
        TZID timezone = TZID.EUROPE.BERLIN;
        Moment start =
            PlainDate.of(2014, Month.MARCH, 30)
                .atStartOfDay(timezone)
                .with(Duration.of(5, ClockUnit.HOURS).later(timezone));
        Moment end =
            start.with(Duration.of(5, ClockUnit.HOURS).earlier(timezone));
        assertThat(
            end,
            is(PlainDate.of(2014, Month.MARCH, 30).atStartOfDay(timezone)));
        assertThat(start.until(end, TimeUnit.HOURS), is(-4L)); // DST-jump
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
    public void plusNanos() {
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

}