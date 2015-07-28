package net.time4j.calendar;

import net.time4j.PlainTimestamp;
import net.time4j.ZonalClock;
import net.time4j.base.TimeSource;
import net.time4j.base.UnixTime;
import net.time4j.engine.StartOfDay;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class ClockTest {

    private static final ZonalClock CLOCK;

    static {
        TimeSource<?> clock = new TimeSource<UnixTime>() {
            @Override
            public UnixTime currentTime() {
                return PlainTimestamp.of(2015, 7, 17, 18, 1).atUTC();
            }
        };
        CLOCK = new ZonalClock(clock, ZonalOffset.UTC);
    }

    @Test
    public void startAtMidnight() {
        assertThat(
            CLOCK.now(HijriCalendar.family(), HijriCalendar.VARIANT_UMALQURA, StartOfDay.MIDNIGHT).toString(),
            is("AH-1436-10-01[islamic-umalqura]"));
    }

    @Test
    public void startOnEvening() {
        assertThat(
            CLOCK.now(
                HijriCalendar.family(),
                HijriCalendar.VARIANT_UMALQURA,
                StartOfDay.ofFixedDeviation(-21600 + 60)
            ).toString(),
            is("AH-1436-10-02[islamic-umalqura]"));
    }

}