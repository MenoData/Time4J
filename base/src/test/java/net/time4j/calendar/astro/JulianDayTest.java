package net.time4j.calendar.astro;

import net.time4j.ClockUnit;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.engine.EpochDays;
import net.time4j.scale.TimeScale;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class JulianDayTest {

    @Test
    public void ephemerisTime1() {
        Moment j2000 = // TT = UTC + 42.184
            PlainTimestamp.of(2000, 1, 1, 11, 58, 56).minus(184, ClockUnit.MILLIS).atUTC();
        assertThat(j2000.toString(TimeScale.TT), is("TT-2000-01-01T12Z"));
        JulianDay expected = JulianDay.ofEphemerisTime(2451545.0);
        assertThat(JulianDay.ofEphemerisTime(j2000), is(expected));
        assertThat(expected.getMJD(), is(51544.5));
        assertThat(expected.getCenturyJ2000(), is(0.0));
        assertThat(expected.getScale(), is(TimeScale.TT));
        assertThat(expected.toMoment(), is(j2000));
    }

    @Test
    public void ephemerisTime2() {
        PlainDate date = PlainDate.of(1992, 4, 12);
        PlainTime time = PlainTime.of(2);
        ZonalOffset utc = ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 2);
        JulianDay jdEphemeris = JulianDay.ofEphemerisTime(date, time, utc);
        assertThat(
            jdEphemeris,
            is(JulianDay.ofEphemerisTime(date.get(EpochDays.JULIAN_DAY_NUMBER) - 0.5)));
    }

    @Test
    public void meanSolarTime() {
        Moment j2000 = PlainTimestamp.of(2000, 1, 1, 12, 0).atUTC();
        Moment j1970 = PlainTimestamp.of(1970, 1, 1, 12, 0).atUTC();
        long delta = j1970.until(j2000, TimeUnit.DAYS);
        assertThat(j1970.toString(TimeScale.UT), is("UT-1970-01-01T12Z"));
        JulianDay expected = JulianDay.ofMeanSolarTime(2451545.0 - delta);
        assertThat(JulianDay.ofMeanSolarTime(j1970), is(expected));
        assertThat(expected.getMJD(), is(40587.5));
        assertThat(expected.getScale(), is(TimeScale.UT));
        assertThat(expected.toMoment(), is(j1970));
    }

    @Test
    public void simplifiedTime() {
        Moment j2000 = PlainTimestamp.of(2000, 1, 1, 12, 0).atUTC();
        Moment j1969 = PlainTimestamp.of(1969, 1, 1, 12, 0).atUTC();
        long delta = j1969.until(j2000, TimeUnit.DAYS);
        assertThat(j1969.toString(TimeScale.POSIX), is("POSIX-1969-01-01T12Z"));
        JulianDay expected = JulianDay.ofSimplifiedTime(2451545.0 - delta);
        assertThat(JulianDay.ofSimplifiedTime(j1969), is(expected));
        assertThat(expected.getMJD(), is(40587.5 - 365));
        assertThat(expected.getScale(), is(TimeScale.POSIX));
        assertThat(expected.toMoment(), is(j1969));
        assertThat(expected.plusDays(delta).toMoment(), is(j2000));
    }

    @Test(expected=IllegalArgumentException.class)
    public void ephemerisTimeBelowMin() {
        Moment min = Moment.axis().getMinimum();
        JulianDay.ofEphemerisTime(min);
    }

    @Test(expected=IllegalArgumentException.class)
    public void meanSolarTimeBeyondMax() {
        Moment max = Moment.axis().getMaximum();
        JulianDay.ofMeanSolarTime(max);
    }

    @Test
    public void ephemerisTimeMin() {
        assertThat(
            JulianDay.ofEphemerisTime(JulianDay.MIN).toMoment().toString(TimeScale.TT),
            is("TT--2000-01-01T12Z"));
    }

    @Test
    public void ephemerisTimeMax() {
        assertThat(
            JulianDay.ofEphemerisTime(JulianDay.MAX).toMoment().toString(TimeScale.TT),
            is("TT-3000-12-31T12Z"));
    }

    @Test
    public void meanSolarTimeMin() {
        assertThat(
            JulianDay.ofMeanSolarTime(JulianDay.MIN).toMoment().toString(TimeScale.UT),
            is("UT--2000-01-01T12Z"));
    }

    @Test
    public void meanSolarTimeMax() {
        assertThat(
            JulianDay.ofMeanSolarTime(JulianDay.MAX).toMoment().toString(TimeScale.UT),
            is("UT-3000-12-31T12Z"));
    }

    @Test
    public void simplifiedTimeMin() {
        assertThat(
            JulianDay.ofSimplifiedTime(JulianDay.MIN).toMoment().toString(TimeScale.POSIX),
            is("POSIX--2000-01-01T12Z"));
    }

    @Test
    public void simplifiedTimeMax() {
        assertThat(
            JulianDay.ofSimplifiedTime(JulianDay.MAX).toMoment().toString(TimeScale.POSIX),
            is("POSIX-3000-12-31T12Z"));
    }

    @Test
    public void plusDays() {
        assertThat(
            JulianDay.ofEphemerisTime(JulianDay.MIN).plusDays(4.0),
            is(JulianDay.ofEphemerisTime(JulianDay.MIN + 4.0)));
    }

    @Test
    public void minusDays() {
        assertThat(
            JulianDay.ofEphemerisTime(JulianDay.MAX).minusDays(4.0),
            is(JulianDay.ofEphemerisTime(JulianDay.MAX - 4.0)));
    }

    @Test
    public void plusSeconds() {
        assertThat(
            JulianDay.ofEphemerisTime(JulianDay.MIN).plusSeconds(43200),
            is(JulianDay.ofEphemerisTime(JulianDay.MIN + 0.5)));
    }

    @Test
    public void minusSeconds() {
        assertThat(
            JulianDay.ofEphemerisTime(JulianDay.MAX).minusSeconds(43200),
            is(JulianDay.ofEphemerisTime(JulianDay.MAX - 0.5)));
    }

}