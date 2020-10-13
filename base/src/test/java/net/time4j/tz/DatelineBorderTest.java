package net.time4j.tz;

import net.time4j.CalendarUnit;
import net.time4j.Moment;
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;

import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.tz.OffsetSign.AHEAD_OF_UTC;
import static net.time4j.tz.OffsetSign.BEHIND_UTC;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class DatelineBorderTest {

    @Test
    public void plusCalendarDaysSamoa() {
        Timezone tz = Timezone.of("Pacific/Apia");
        Moment end =
            PlainDate.of(2011, Month.DECEMBER, 29)
                .atStartOfDay().plus(2, CalendarUnit.DAYS).in(tz);
        assertThat(
            end,
            is(
                PlainDate.of(2011, Month.DECEMBER, 31)
                    .at(PlainTime.midnightAtStartOfDay())
                    .inTimezone(ZonalOffset.ofHours(AHEAD_OF_UTC, 14))));
    }

    @Test
    public void plusPosixDaysSamoa() {
        Timezone tz = Timezone.of("Pacific/Apia");
        Moment end =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().in(tz)
                .plus(1, TimeUnit.DAYS);
        assertThat(
            end,
            is(
                PlainDate.of(2011, Month.DECEMBER, 31)
                    .at(PlainTime.midnightAtStartOfDay())
                    .inTimezone(ZonalOffset.ofHours(AHEAD_OF_UTC, 14))));
    }

    @Test
    public void plusPosixHoursSamoa() {
        Timezone tz = Timezone.of("Pacific/Apia");
        Moment end =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().in(tz)
                .plus(24, TimeUnit.HOURS);
        assertThat(
            end,
            is(
                PlainDate.of(2011, Month.DECEMBER, 31)
                    .at(PlainTime.midnightAtStartOfDay())
                    .inTimezone(ZonalOffset.ofHours(AHEAD_OF_UTC, 14))));
    }

    @Test
    public void standardOffsetChangeSamoa() {
        Timezone tz = Timezone.of("Pacific/Apia");
        Moment start =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().in(tz);
        Moment end =
            PlainDate.of(2011, Month.DECEMBER, 29)
                .atStartOfDay().plus(2, CalendarUnit.DAYS).in(tz);
        assertThat(
            tz.getOffset(start),
            is(ZonalOffset.ofHours(BEHIND_UTC, 10)));
        assertThat(
            tz.getOffset(end),
            is(ZonalOffset.ofHours(AHEAD_OF_UTC, 14)));
    }

    @Test
    public void daysShiftSamoa() {
        Timezone tz = Timezone.of("Pacific/Apia");
        Moment start =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().in(tz);
        Moment end =
            PlainDate.of(2011, Month.DECEMBER, 29)
                .atStartOfDay().plus(2, CalendarUnit.DAYS).in(tz);
        // crossing international dateline border
        assertThat(start.until(end, TimeUnit.DAYS), is(1L));
    }

    @Test
    public void hoursShiftSamoa() {
        Timezone tz = Timezone.of("Pacific/Apia");
        Moment start =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().in(tz);
        Moment end =
            PlainDate.of(2011, Month.DECEMBER, 29)
                .atStartOfDay().plus(2, CalendarUnit.DAYS).in(tz);
        // crossing international dateline border
        assertThat(start.until(end, TimeUnit.HOURS), is(24L));
    }

    @Test
    public void minutesShiftSamoa() {
        Timezone tz = Timezone.of("Pacific/Apia");
        Moment start =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().in(tz);
        Moment end =
            PlainDate.of(2011, Month.DECEMBER, 29)
                .atStartOfDay().plus(2, CalendarUnit.DAYS).in(tz);
        // crossing international dateline border
        assertThat(start.until(end, TimeUnit.MINUTES), is(1440L));
    }

    @Test
    public void secondsShiftSamoa() {
        Timezone tz = Timezone.of("Pacific/Apia");
        Moment start =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().in(tz);
        Moment end =
            PlainDate.of(2011, Month.DECEMBER, 29)
                .atStartOfDay().plus(2, CalendarUnit.DAYS).in(tz);
        // crossing international dateline border
        assertThat(start.until(end, TimeUnit.SECONDS), is(86400L));
    }

    @Test
    public void invalidDaySamoa1() {
        TZID timezone = Timezone.of("Pacific/Apia").getID();
        assertThat(
            PlainDate.of(2011, Month.DECEMBER, 30).atTime(12, 0)
                .isValid(timezone),
            is(false));
    }

    @Test(expected=IllegalArgumentException.class)
    public void invalidDaySamoa2() {
        PlainDate
            .of(2011, Month.DECEMBER, 30) // date was completely skipped
            .atTime(12, 0)
            .in(Timezone.of("Pacific/Apia").with(GapResolver.ABORT.and(OverlapResolver.EARLIER_OFFSET)));
    }

    @Test
    public void dayBeforeShiftSamoa() {
        Timezone tz = Timezone.of("Pacific/Apia");
        Moment start =
            PlainDate.of(2011, Month.DECEMBER, 29).atStartOfDay().in(tz);
        assertThat(
            start.toZonalTimestamp(tz.getID()),
            is(PlainTimestamp.of(2011, 12, 29, 0, 0)));
    }

    @Test
    public void dayAfterShiftSamoa() {
        Timezone tz = Timezone.of("Pacific/Apia");
        Moment end =
            PlainDate.of(2011, Month.DECEMBER, 29)
                .atStartOfDay().plus(2, CalendarUnit.DAYS).in(tz);
        assertThat(
            end.toZonalTimestamp(tz.getID()),
            is(PlainTimestamp.of(2011, 12, 31, 0, 0)));
    }

}