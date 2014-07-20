package net.time4j.tz;

import net.time4j.CalendarUnit;
import net.time4j.Duration;
import net.time4j.Moment;
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.tz.olson.PACIFIC;

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
        Timezone tz = Timezone.of(PACIFIC.APIA);
        Moment end =
            PlainDate.of(2011, Month.DECEMBER, 29)
                .atStartOfDay().at(tz)
                .with(Duration.of(2, CalendarUnit.DAYS).later(tz));
        assertThat(
            end,
            is(
                PlainDate.of(2011, Month.DECEMBER, 31)
                    .at(PlainTime.midnightAtStartOfDay())
                    .atTimezone(ZonalOffset.ofHours(AHEAD_OF_UTC, 14))));
    }

    @Test
    public void plusPosixDaysSamoa() {
        Timezone tz = Timezone.of(PACIFIC.APIA);
        Moment end =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(tz)
                .plus(1, TimeUnit.DAYS);
        assertThat(
            end,
            is(
                PlainDate.of(2011, Month.DECEMBER, 31)
                    .at(PlainTime.midnightAtStartOfDay())
                    .atTimezone(ZonalOffset.ofHours(AHEAD_OF_UTC, 14))));
    }

    @Test
    public void plusPosixHoursSamoa() {
        Timezone tz = Timezone.of(PACIFIC.APIA);
        Moment end =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(tz)
                .plus(24, TimeUnit.HOURS);
        assertThat(
            end,
            is(
                PlainDate.of(2011, Month.DECEMBER, 31)
                    .at(PlainTime.midnightAtStartOfDay())
                    .atTimezone(ZonalOffset.ofHours(AHEAD_OF_UTC, 14))));
    }

    @Test
    public void standardOffsetChangeSamoa() {
        Timezone tz = Timezone.of(PACIFIC.APIA);
        Moment start =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(tz);
        Moment end =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(tz)
                .with(Duration.of(2, CalendarUnit.DAYS).later(tz));
        assertThat(
            tz.getOffset(start),
            is(ZonalOffset.ofHours(BEHIND_UTC, 10)));
        assertThat(
            tz.getOffset(end),
            is(ZonalOffset.ofHours(AHEAD_OF_UTC, 14)));
    }

    @Test
    public void daysShiftSamoa() {
        Timezone tz = Timezone.of(PACIFIC.APIA);
        Moment start =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(tz);
        Moment end =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(tz)
                .with(Duration.of(2, CalendarUnit.DAYS).later(tz));
        // crossing international dateline border
        assertThat(start.until(end, TimeUnit.DAYS), is(1L));
    }

    @Test
    public void hoursShiftSamoa() {
        Timezone tz = Timezone.of(PACIFIC.APIA);
        Moment start =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(tz);
        Moment end =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(tz)
                .with(Duration.of(2, CalendarUnit.DAYS).later(tz));
        // crossing international dateline border
        assertThat(start.until(end, TimeUnit.HOURS), is(24L));
    }

    @Test
    public void minutesShiftSamoa() {
        Timezone tz = Timezone.of(PACIFIC.APIA);
        Moment start =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(tz);
        Moment end =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(tz)
                .with(Duration.of(2, CalendarUnit.DAYS).later(tz));
        // crossing international dateline border
        assertThat(start.until(end, TimeUnit.MINUTES), is(1440L));
    }

    @Test
    public void secondsShiftSamoa() {
        Timezone tz = Timezone.of(PACIFIC.APIA);
        Moment start =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(tz);
        Moment end =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(tz)
                .with(Duration.of(2, CalendarUnit.DAYS).later(tz));
        // crossing international dateline border
        assertThat(start.until(end, TimeUnit.SECONDS), is(86400L));
    }

    @Test
    public void invalidDaySamoa() {
        TZID timezone = PACIFIC.APIA;
        assertThat(
            PlainDate.of(2011, Month.DECEMBER, 30).atTime(12, 0)
                .isValid(timezone),
            is(false));
    }

    @Test
    public void dayBeforeShiftSamoa() {
        Timezone tz = Timezone.of(PACIFIC.APIA);
        Moment start =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(tz);
        assertThat(
            start.inZonalView(tz.getID()),
            is(PlainTimestamp.of(2011, 12, 29, 0, 0)));
    }

    @Test
    public void dayAfterShiftSamoa() {
        Timezone tz = Timezone.of(PACIFIC.APIA);
        Moment end =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().at(tz)
                .with(Duration.of(2, CalendarUnit.DAYS).later(tz));
        assertThat(
            end.inZonalView(tz.getID()),
            is(PlainTimestamp.of(2011, 12, 31, 0, 0)));
    }

}