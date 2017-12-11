package net.time4j.calendar.astro;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class MoonTest {

    @Test
    public void newMoon() {
        assertThat(
            MoonPhase.NEW_MOON.atLunation(-283),
            is(PlainTimestamp.of(1977, 2, 18, 3, 36, 53).atUTC())); // Meeus (example 49.a)
    }

    @Test
    public void moonPhaseOfLastQuarter() {
        assertThat(
            MoonPhase.LAST_QUARTER.atLunation(544),
            is(PlainTimestamp.of(2044, 1, 21, 23, 47, 7).atUTC())); // Meeus (example 49.b)
    }

    // for following tests compare with
    // http://aa.usno.navy.mil/cgi-bin/aa_phases.pl?year=2017&month=10&day=7&nump=50&format=p

    @Test
    public void newMoonBefore() {
        assertThat(
            MoonPhase.NEW_MOON.before(PlainTimestamp.of(2017, 11, 18, 11, 42).atUTC())
                .plus(30, TimeUnit.SECONDS)
                .with(Moment.PRECISION, TimeUnit.MINUTES),
            is(PlainTimestamp.of(2017, 10, 19, 19, 12).atUTC()));
        assertThat(
            MoonPhase.NEW_MOON.before(PlainTimestamp.of(2017, 11, 18, 11, 43).atUTC())
                .plus(30, TimeUnit.SECONDS)
                .with(Moment.PRECISION, TimeUnit.MINUTES),
            is(PlainTimestamp.of(2017, 11, 18, 11, 42).atUTC()));
    }

    @Test
    public void newMoonAfter() {
        assertThat(
            MoonPhase.NEW_MOON.after(PlainTimestamp.of(2017, 10, 19, 19, 12).atUTC()) // 19:12:06
                .plus(30, TimeUnit.SECONDS)
                .with(Moment.PRECISION, TimeUnit.MINUTES),
            is(PlainTimestamp.of(2017, 10, 19, 19, 12).atUTC()));
        assertThat(
            MoonPhase.NEW_MOON.after(PlainTimestamp.of(2017, 10, 19, 19, 13).atUTC())
                .plus(30, TimeUnit.SECONDS)
                .with(Moment.PRECISION, TimeUnit.MINUTES),
            is(PlainTimestamp.of(2017, 11, 18, 11, 42).atUTC()));
    }

    @Test
    public void moonPhaseOfFirstQuarterBefore() {
        assertThat(
            MoonPhase.FIRST_QUARTER.before(PlainTimestamp.of(2017, 11, 26, 17, 2).atUTC())
                .plus(30, TimeUnit.SECONDS)
                .with(Moment.PRECISION, TimeUnit.MINUTES),
            is(PlainTimestamp.of(2017, 10, 27, 22, 22).atUTC()));
        assertThat(
            MoonPhase.FIRST_QUARTER.before(PlainTimestamp.of(2017, 11, 26, 17, 3).atUTC()) // 17:02:56
                .plus(30, TimeUnit.SECONDS)
                .with(Moment.PRECISION, TimeUnit.MINUTES),
            is(PlainTimestamp.of(2017, 11, 26, 17, 3).atUTC()));
    }

    @Test
    public void fullMoonBefore() {
        assertThat(
            MoonPhase.FULL_MOON.before(PlainTimestamp.of(2017, 12, 3, 15, 46).atUTC())
                .plus(30, TimeUnit.SECONDS)
                .with(Moment.PRECISION, TimeUnit.MINUTES),
            is(PlainTimestamp.of(2017, 11, 4, 5, 23).atUTC()));
        assertThat(
            MoonPhase.FULL_MOON.before(PlainTimestamp.of(2017, 12, 3, 15, 47).atUTC()) // 15:46:56
                .plus(30, TimeUnit.SECONDS)
                .with(Moment.PRECISION, TimeUnit.MINUTES),
            is(PlainTimestamp.of(2017, 12, 3, 15, 47).atUTC()));
    }

    @Test
    public void moonPhaseOfLastQuarterBefore() {
        assertThat(
            MoonPhase.LAST_QUARTER.before(PlainTimestamp.of(2017, 11, 10, 20, 36).atUTC())
                .plus(30, TimeUnit.SECONDS)
                .with(Moment.PRECISION, TimeUnit.MINUTES),
            is(PlainTimestamp.of(2017, 10, 12, 12, 25).atUTC()));
        assertThat(
            MoonPhase.LAST_QUARTER.before(PlainTimestamp.of(2017, 11, 10, 20, 37).atUTC())
                .plus(30, TimeUnit.SECONDS)
                .with(Moment.PRECISION, TimeUnit.MINUTES),
            is(PlainTimestamp.of(2017, 11, 10, 20, 37).atUTC()));
    }

    @Test
    public void illuminationOfMoon() {
        Moment m =
            JulianDay.ofEphemerisTime(
                PlainDate.of(1992, 4, 12),
                PlainTime.midnightAtStartOfDay(),
                ZonalOffset.UTC
            ).toMoment();
        assertThat(
            MoonPhase.getIllumination(m),
            is(0.68)); // Meeus (example 48.a)
        assertThat(
            MoonPhase.getIllumination(MoonPhase.NEW_MOON.after(m)),
            is(0.0));
        assertThat(
            MoonPhase.getIllumination(MoonPhase.FIRST_QUARTER.after(m)),
            is(0.5));
        assertThat(
            MoonPhase.getIllumination(MoonPhase.FULL_MOON.after(m)),
            is(1.0));
        assertThat(
            MoonPhase.getIllumination(MoonPhase.LAST_QUARTER.after(m)),
            is(0.5));
    }

    @Test
    public void moonPosition() {
        JulianDay jd =
            JulianDay.ofEphemerisTime(
                PlainDate.of(1992, 4, 12),
                PlainTime.midnightAtStartOfDay(),
                ZonalOffset.UTC
            );

        // Meeus - example 47.a
        double[] data = MoonPosition.calculateMeeus(jd.getCenturyJ2000());

        assertThat(
            data[0],
            is(0.004609595895691879)); // nutation-in-longitude
        assertThat(
            data[1],
            is(23.440635013964783)); // true obliquity in degrees
        assertThat(
            data[2],
            is(134.68846856938873)); // right ascension in degrees
        assertThat(
            data[3],
            is(13.768366716980461)); // declination in degrees
        assertThat(
            data[4],
            is(368409.6848161269)); // distance in km
    }

}