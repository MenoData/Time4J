package net.time4j.tz;

import net.time4j.CalendarUnit;
import net.time4j.Duration;
import net.time4j.Moment;
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;

import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.tz.ZonalOffset.Sign.AHEAD_OF_UTC;
import static net.time4j.tz.ZonalOffset.Sign.BEHIND_UTC;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class DatelineBorderTest {

    @Test
    public void plusCalendarDaysSamoa() {
        TZID timezone = TZID.PACIFIC.APIA;
        Moment end =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(timezone)
                .with(Duration.of(2, CalendarUnit.DAYS).later(timezone));
        assertThat(
            end,
            is(
                PlainDate.of(2011, Month.DECEMBER, 31)
                    .at(PlainTime.midnightAtStartOfDay())
                    .at(ZonalOffset.ofHours(AHEAD_OF_UTC, 14))));
    }

    @Test
    public void plusPosixDaysSamoa() {
        TZID timezone = TZID.PACIFIC.APIA;
        Moment end =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(timezone)
                .plus(1, TimeUnit.DAYS);
        assertThat(
            end,
            is(
                PlainDate.of(2011, Month.DECEMBER, 31)
                    .at(PlainTime.midnightAtStartOfDay())
                    .at(ZonalOffset.ofHours(AHEAD_OF_UTC, 14))));
    }

    @Test
    public void plusPosixHoursSamoa() {
        TZID timezone = TZID.PACIFIC.APIA;
        Moment end =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(timezone)
                .plus(24, TimeUnit.HOURS);
        assertThat(
            end,
            is(
                PlainDate.of(2011, Month.DECEMBER, 31)
                    .at(PlainTime.midnightAtStartOfDay())
                    .at(ZonalOffset.ofHours(AHEAD_OF_UTC, 14))));
    }

    @Test
    public void standardOffsetChangeSamoa() {
        TZID timezone = TZID.PACIFIC.APIA;
        Moment start =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(timezone);
        Moment end =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(timezone)
                .with(Duration.of(2, CalendarUnit.DAYS).later(timezone));
        assertThat(
            Timezone.of(timezone).getOffset(start),
            is(ZonalOffset.ofHours(BEHIND_UTC, 10)));
        assertThat(
            Timezone.of(timezone).getOffset(end),
            is(ZonalOffset.ofHours(AHEAD_OF_UTC, 14)));
    }

    @Test
    public void daysShiftSamoa() {
        TZID timezone = TZID.PACIFIC.APIA;
        Moment start =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(timezone);
        Moment end =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(timezone)
                .with(Duration.of(2, CalendarUnit.DAYS).later(timezone));
        // crossing international dateline border
        assertThat(start.until(end, TimeUnit.DAYS), is(1L));
    }

    @Test
    public void hoursShiftSamoa() {
        TZID timezone = TZID.PACIFIC.APIA;
        Moment start =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(timezone);
        Moment end =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(timezone)
                .with(Duration.of(2, CalendarUnit.DAYS).later(timezone));
        // crossing international dateline border
        assertThat(start.until(end, TimeUnit.HOURS), is(24L));
    }

    @Test
    public void minutesShiftSamoa() {
        TZID timezone = TZID.PACIFIC.APIA;
        Moment start =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(timezone);
        Moment end =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(timezone)
                .with(Duration.of(2, CalendarUnit.DAYS).later(timezone));
        // crossing international dateline border
        assertThat(start.until(end, TimeUnit.MINUTES), is(1440L));
    }

    @Test
    public void secondsShiftSamoa() {
        TZID timezone = TZID.PACIFIC.APIA;
        Moment start =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(timezone);
        Moment end =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(timezone)
                .with(Duration.of(2, CalendarUnit.DAYS).later(timezone));
        // crossing international dateline border
        assertThat(start.until(end, TimeUnit.SECONDS), is(86400L));
    }

    @Test
    public void invalidDaySamoa() {
        TZID timezone = TZID.PACIFIC.APIA;
        assertThat(
            PlainDate.of(2011, Month.DECEMBER, 30).atTime(12, 0)
                .isValid(timezone),
            is(false));
    }

    @Test
    public void dayBeforeShiftSamoa() {
        TZID timezone = TZID.PACIFIC.APIA;
        Moment start =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(timezone);
        assertThat(
            start.inTimezone(timezone),
            is(PlainTimestamp.of(2011, 12, 29, 0, 0)));
    }

    @Test
    public void dayAfterShiftSamoa() {
        TZID timezone = TZID.PACIFIC.APIA;
        Moment end =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(timezone)
                .with(Duration.of(2, CalendarUnit.DAYS).later(timezone));
        assertThat(
            end.inTimezone(timezone),
            is(PlainTimestamp.of(2011, 12, 31, 0, 0)));
    }

}