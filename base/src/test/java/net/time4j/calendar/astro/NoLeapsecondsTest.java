package net.time4j.calendar.astro;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;
import net.time4j.scale.LeapSeconds;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.ZonalOffset;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class NoLeapsecondsTest {

    @BeforeClass
    public static void disableLeapSeconds() {
        // System.setProperty("net.time4j.scale.leapseconds.suppressed", "true");
    }

    @AfterClass
    public static void enableLeapSeconds() {
        System.setProperty("net.time4j.scale.leapseconds.suppressed", "false");
    }

    @Test
    public void calculateAstro() {
        SolarTime hh =
            SolarTime.ofLocation()
                .northernLatitude(53, 30, 0.0)
                .easternLongitude(10, 0, 0.0)
                .usingCalculator(StdSolarCalculator.TIME4J)
                .build();

        Moment sunriseHH = PlainDate.of(2018, 3, 21).get(hh.sunrise()).get();

        if (LeapSeconds.getInstance().isEnabled()) {
            assertThat( // without truncating: 2018-03-20T16:15:07,454302310Z
                AstronomicalSeason.VERNAL_EQUINOX.inYear(2018).with(Moment.PRECISION, TimeUnit.SECONDS),
                is(PlainTimestamp.of(2018, 3, 20, 16, 15, 7).atUTC()));
            assertThat(
                sunriseHH, // without truncating: 2018-03-21T05:20:23,757003970Z
                is(PlainTimestamp.of(2018, 3, 21, 6, 20, 23).at(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 1))));
            System.out.println("Leap seconds active.");
        } else {
            assertThat( // without truncating: 2018-03-20T16:15:07,487834453Z
                AstronomicalSeason.VERNAL_EQUINOX.inYear(2018).with(Moment.PRECISION, TimeUnit.SECONDS),
                is(PlainTimestamp.of(2018, 3, 20, 16, 15, 7).atUTC()));
            assertThat(
                sunriseHH, // without truncating: 2018-03-21T05:20:23,832059653Z
                is(PlainTimestamp.of(2018, 3, 21, 6, 20, 23).at(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 1))));
            System.out.println("Leap seconds disabled.");
        }
    }

}