package net.time4j.calendar.astro;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;


@SuppressWarnings("ConstantConditions")
@RunWith(JUnit4.class)
public class MoonTest {

    private static final double TOLERANCE = 0.001;

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
            MoonPhase.getIllumination(m, 0),
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
    public void illuminationOfMoonWithMoreDigits() {
        Moment m =
            JulianDay.ofEphemerisTime(
                PlainDate.of(1992, 4, 12),
                PlainTime.midnightAtStartOfDay(),
                ZonalOffset.UTC
            ).toMoment();
        assertThat(
            MoonPhase.getIllumination(m, 3),
            is(0.68013));
    }

    @Test(expected=ArrayIndexOutOfBoundsException.class)
    public void illuminationOfMoonWithInvalidPrecision() {
        MoonPhase.getIllumination(Moment.UNIX_EPOCH, 4);
    }

    @Test
    public void moonPositionMeeus47a() {
        JulianDay jd =
            JulianDay.ofEphemerisTime(
                PlainDate.of(1992, 4, 12),
                PlainTime.midnightAtStartOfDay(),
                ZonalOffset.UTC
            );

        // Meeus - example 47.a
        double[] data = MoonPosition.calculateMeeus47(jd.getCenturyJ2000());
        assertThat(
            Math.abs(data[0] - 0.004609595895691879) < TOLERANCE,
            is(true)); // nutation-in-longitude
        assertThat(
            Math.abs(data[1] - 23.440635013964783) < TOLERANCE,
            is(true)); // true obliquity in degrees
        assertThat(
            Math.abs(data[2] - 134.6884685693878) < TOLERANCE,
            is(true)); // right ascension in degrees
        assertThat(
            Math.abs(data[3] - 13.768366716980795) < TOLERANCE,
            is(true)); // declination in degrees
        assertThat(
            Math.abs(data[4] - 368409.6848161264) < TOLERANCE,
            is(true)); // distance in km
    }

    @Test
    public void moonPositionHamburg() {
        Timezone tz = Timezone.of("Europe/Berlin");
        LunarTime hh =
            LunarTime.ofLocation(tz.getID())
                .northernLatitude(53, 33, 0.0)
                .easternLongitude(10, 0, 0.0)
                .build();
        Moment moment = PlainTimestamp.of(2017, 6, 15, 7, 30).in(tz);
        MoonPosition position = MoonPosition.at(moment, hh);

        assertThat(
            Math.abs(position.getAzimuth() - 207.62203379012075) < TOLERANCE,
            is(true)); // usno => 207.6, mooncalc => 207.62
        assertThat(
            Math.abs(position.getElevation() - 19.34313658741647) < TOLERANCE,
            is(true)); // usno => 19.4, mooncalc => 19.4
    }

    @Test
    public void moonPositionNY() {
        Timezone tz = Timezone.of("America/New_York");
        LunarTime ny =
            LunarTime.ofLocation(tz.getID())
                .northernLatitude(40, 43, 0.0)
                .westernLongitude(74, 0, 0.0)
                .build();

        Moment m = PlainTimestamp.of(2018, 1, 1, 0, 30).in(tz);
        MoonPosition position = MoonPosition.at(m, ny);
        assertThat(
            Math.abs(position.getAzimuth() - 226.80838342908987) < TOLERANCE,
            is(true)); // usno => 226.8
        assertThat(
            Math.abs(position.getElevation() - 61.23413663043105) < TOLERANCE,
            is(true)); // usno => 61.8

        m = PlainTimestamp.of(2018, 1, 1, 5, 20).in(tz);
        position = MoonPosition.at(m, ny);
        assertThat(
            Math.abs(position.getAzimuth() - 285.6150230123651) < TOLERANCE,
            is(true)); // usno => 285.6
        assertThat(
            Math.abs(position.getElevation() - 11.41776706237054) < TOLERANCE,
            is(true)); // usno => 11.4

        m = PlainTimestamp.of(2018, 1, 1, 16, 50).in(tz);
        position = MoonPosition.at(m, ny);
        assertThat(
            Math.abs(position.getAzimuth() - 65.7056160201322) < TOLERANCE,
            is(true)); // usno => 65.7
        assertThat(
            Math.abs(position.getElevation() - 1.9708028936650985) < TOLERANCE,
            is(true)); // usno => 2.0

        m = PlainTimestamp.of(2018, 1, 1, 21, 0).in(tz);
        position = MoonPosition.at(m, ny);
        assertThat(
            Math.abs(position.getAzimuth() - 105.11861494256615) < TOLERANCE,
            is(true)); // usno => 105.1
        assertThat(
            Math.abs(position.getElevation() - 46.04943472446251) < TOLERANCE,
            is(true)); // usno => 46.4
    }

    @Test
    public void moonPositionShanghai() {
        Timezone tz = Timezone.of("Asia/Shanghai");
        LunarTime shanghai =
            LunarTime.ofLocation(tz.getID())
                .northernLatitude(31, 14, 0.0)
                .easternLongitude(121, 28, 0.0)
                .build();
        Moment moment = PlainTimestamp.of(2017, 12, 13, 8, 10).in(tz);
        MoonPosition position = MoonPosition.at(moment, shanghai);

        assertThat(
            Math.abs(position.getRightAscension() - 202.87178103802793) < TOLERANCE,
            is(true));
        assertThat(
            Math.abs(position.getDeclination() + 4.551668018005712) < TOLERANCE,
            is(true));
        assertThat(
            Math.abs(position.getAzimuth() - 185.055316964463) < TOLERANCE,
            is(true)); // usno => 185.1, mooncalc => 186.28
        assertThat(
            Math.abs(position.getElevation() - 53.18921727502122) < TOLERANCE,
            is(true)); // usno => 53.6, mooncalc => 53.5
        assertThat(
            Math.abs(position.getDistance() - 394687.4916136658) < TOLERANCE,
            is(true));
    }

    @Test
    public void moonlightYannarie() {
        Timezone tz = Timezone.of("Australia/Perth");
        LunarTime lunarTime =
            LunarTime.ofLocation(tz.getID())
                .southernLatitude(22, 35, 37.31)
                .easternLongitude(114, 57, 39.24)
                .atAltitude(46)
                .build();
        assertThat(lunarTime.getObserverZoneID().canonical(), is("Australia/Perth"));
        LunarTime.Moonlight moonlight = lunarTime.on(PlainDate.of(2016, 7, 4));
        assertThat(moonlight.moonrise().get(),
            is(PlainTimestamp.of(2016, 7, 4, 6, 25, 10).in(tz)));
            // sea-level: 06:26:12, mooncalc: 06:26:09, usno: 06:26
        assertThat(moonlight.moonset().get(),
            is(PlainTimestamp.of(2016, 7, 4, 17, 48, 40).in(tz)));
            // sea-level: 17:47:38, mooncalc: 17:47:45, usno: 17:47
        assertThat(moonlight.moonriseLocal().get(),
            is(PlainTimestamp.of(2016, 7, 4, 6, 25, 10)));
        assertThat(moonlight.moonsetLocal().get(),
            is(PlainTimestamp.of(2016, 7, 4, 17, 48, 40)));
        assertThat(moonlight.moonrise(ZonalOffset.UTC).get(),
            is(PlainTimestamp.of(2016, 7, 3, 22, 25, 10)));
        assertThat(moonlight.moonset(ZonalOffset.UTC).get(),
            is(PlainTimestamp.of(2016, 7, 4, 9, 48, 40)));
        assertThat(moonlight.length(), is(41010));
        assertThat(moonlight.isAbsent(), is(false));
        assertThat(moonlight.isPresentAllDay(), is(false));
        assertThat(moonlight.isPresent(PlainTimestamp.of(2016, 7, 4, 6, 25, 9).in(tz)), is(false));
        assertThat(moonlight.isPresent(PlainTimestamp.of(2016, 7, 4, 6, 25, 10).in(tz)), is(true));
    }

    @Test
    public void moonlightLondon() {
        Timezone tz = Timezone.of("Europe/London");
        LunarTime lunarTime =
            LunarTime.ofLocation(tz.getID())
                .northernLatitude(51, 30, 33.8)
                .westernLongitude(0, 7, 5.95)
                .build();
        LunarTime.Moonlight moonlight = lunarTime.on(PlainDate.of(2016, 8, 19));
        assertThat(moonlight.moonrise().get(),
            is(PlainTimestamp.of(2016, 8, 19, 20, 45, 13).in(tz)));
        assertThat(moonlight.moonset().get(),
            is(PlainTimestamp.of(2016, 8, 19, 7, 3, 14).in(tz)));
        assertThat(moonlight.length(), is(37081));
        assertThat(moonlight.isAbsent(), is(false));
        assertThat(moonlight.isPresentAllDay(), is(false));
    }

    @Test
    public void moonlightHamburg() {
        Timezone tz = Timezone.of("Europe/Berlin");
        LunarTime lunarTime =
            LunarTime.ofLocation(tz.getID())
                .northernLatitude(53, 33, 2.0)
                .easternLongitude(9, 59, 36.0)
                .build();

        LunarTime.Moonlight moonlight1 = lunarTime.on(PlainDate.of(1982, 9, 26)); // end of summer time
        assertThat(moonlight1.moonrise().get(),
            is(PlainTimestamp.of(1982, 9, 26, 15, 41, 59).in(tz)));
        assertThat(moonlight1.moonset().get(),
            is(PlainTimestamp.of(1982, 9, 26, 23, 17, 43).in(tz)));
        assertThat(moonlight1.length(), is(27344));

        LunarTime.Moonlight moonlight2 = lunarTime.on(PlainDate.of(1982, 9, 27)); // standard time
        assertThat(moonlight2.moonrise().get(),
            is(PlainTimestamp.of(1982, 9, 27, 16, 21, 37).in(tz)));
        assertThat(moonlight2.moonset().isPresent(), is(false));
        assertThat(moonlight2.length(), is(27503));

        LunarTime.Moonlight moonlight3 = lunarTime.on(PlainDate.of(1982, 9, 28));
        assertThat(moonlight3.moonset().get(),
            is(PlainTimestamp.of(1982, 9, 28, 0, 18, 37).in(tz)));
        assertThat(moonlight3.moonrise().get(),
            is(PlainTimestamp.of(1982, 9, 28, 16, 53, 38).in(tz)));
        assertThat(moonlight3.length(), is(26699));
    }

    @Test
    public void moonlightMunich() {
        Timezone tz = Timezone.of("Europe/Berlin");
        LunarTime lunarTime = LunarTime.ofLocation(tz.getID(), 48.1, 11.6);
        LunarTime.Moonlight moonlight = lunarTime.on(PlainDate.of(2000, 3, 25));
        assertThat(moonlight.moonrise().isPresent(), is(false));
        assertThat(moonlight.moonset().get(),
            is(PlainTimestamp.of(2000, 3, 25, 8, 58, 33).in(tz)));
        assertThat(moonlight.length(), is(32313));
        assertThat(moonlight.isAbsent(), is(false));
        assertThat(moonlight.isPresentAllDay(), is(false));
    }

    @Test
    public void moonlightShanghai() {
        Timezone tz = Timezone.of("Asia/Shanghai");
        LunarTime lunarTime =
            LunarTime.ofLocation(tz.getID())
                .northernLatitude(31, 14, 0.0)
                .easternLongitude(121, 28, 0.0)
                .build();
        LunarTime.Moonlight moonlight = lunarTime.on(PlainDate.of(2017, 12, 13));
        assertThat(moonlight.moonrise().get(),
            is(PlainTimestamp.of(2017, 12, 13, 1, 55, 53).in(tz)));
        assertThat(moonlight.moonset().get(),
            is(PlainTimestamp.of(2017, 12, 13, 13, 54, 32).in(tz)));
        assertThat(moonlight.length(), is(43119));
        assertThat(moonlight.isAbsent(), is(false));
        assertThat(moonlight.isPresentAllDay(), is(false));
    }

    @Test
    public void moonlightPolarCircle() { // see also: https://www.mooncalc.org
        ZonalOffset offset = ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 2);
        LunarTime lunarTime = LunarTime.ofLocation(offset, 65, 10);

        LunarTime.Moonlight moonlight = lunarTime.on(PlainDate.of(2007, 6, 14));
        assertThat(moonlight.moonrise().isPresent(), is(false));
        assertThat(moonlight.moonset().isPresent(), is(false));
        assertThat(moonlight.length(), is(86400));
        assertThat(moonlight.isAbsent(), is(false));
        assertThat(moonlight.isPresentAllDay(), is(true));

        LunarTime.Moonlight moonlight2 = lunarTime.on(PlainDate.of(2007, 6, 30));
        assertThat(moonlight2.moonrise().isPresent(), is(false));
        assertThat(moonlight2.moonset().isPresent(), is(false));
        assertThat(moonlight2.length(), is(0));
        assertThat(moonlight2.isAbsent(), is(true));
        assertThat(moonlight2.isPresentAllDay(), is(false));
    }

    @Test
    public void moonlightNorthPole() {
        // see also:
        // http://aa.usno.navy.mil/cgi-bin/aa_rstablew.pl?ID=AA&year=2017&task=1&place=north+pole&lon_sign=1&lon_deg=0&lon_min=0&lat_sign=1&lat_deg=90&lat_min=0&tz=2&tz_sign=1
        Timezone tz = Timezone.of("Arctic/Longyearbyen"); // = Europe/Oslo
        LunarTime lunarTime =
            LunarTime.ofLocation(tz.getID())
                .northernLatitude(90, 0, 0.0)
                .easternLongitude(0, 0, 0.0)
                .build();

        LunarTime.Moonlight moonlight1 = lunarTime.on(PlainDate.of(2017, 5, 22));
        assertThat(moonlight1.isAbsent(), is(false));
        assertThat(moonlight1.isPresentAllDay(), is(false));
        assertThat(moonlight1.moonrise().get(), is(PlainTimestamp.of(2017, 5, 22, 1, 29, 45).in(tz)));
        assertThat(moonlight1.moonset().isPresent(), is(false));

        LunarTime.Moonlight moonlight2 = lunarTime.on(PlainDate.of(2017, 6, 1));
        assertThat(moonlight2.isAbsent(), is(false));
        assertThat(moonlight2.isPresentAllDay(), is(true));

        LunarTime.Moonlight moonlight3 = lunarTime.on(PlainDate.of(2017, 8, 15));
        assertThat(moonlight3.isAbsent(), is(false));
        assertThat(moonlight3.isPresentAllDay(), is(true));

        LunarTime.Moonlight moonlight4 = lunarTime.on(PlainDate.of(2017, 8, 24));
        assertThat(moonlight4.isAbsent(), is(false));
        assertThat(moonlight4.isPresentAllDay(), is(false));
        assertThat(moonlight4.moonset().get(), is(PlainTimestamp.of(2017, 8, 24, 17, 59, 57).in(tz)));
        assertThat(moonlight4.moonrise().isPresent(), is(false));

        LunarTime.Moonlight moonlight5 = lunarTime.on(PlainDate.of(2017, 8, 31));
        assertThat(moonlight5.isAbsent(), is(true));
        assertThat(moonlight5.isPresentAllDay(), is(false));

        LunarTime.Moonlight moonlight6 = lunarTime.on(PlainDate.of(2017, 9, 21));
        assertThat(moonlight6.isAbsent(), is(false));
        assertThat(moonlight6.isPresentAllDay(), is(false));
        assertThat(moonlight6.moonset().get(), is(PlainTimestamp.of(2017, 9, 21, 2, 48, 18).in(tz)));
        assertThat(moonlight6.moonrise().isPresent(), is(false));

        LunarTime.Moonlight moonlight7 = lunarTime.on(PlainDate.of(2017, 12, 13));
        assertThat(moonlight7.isAbsent(), is(true));
        assertThat(moonlight7.isPresentAllDay(), is(false));
    }

    @Test
    public void minLunation() {
        int min = MoonPhase.minLunation();
        try {
            MoonPhase.NEW_MOON.atLunation(min);
        } catch (IllegalArgumentException iae) {
            fail("min-lunation failed: " + min);
        }

        min--;
        try {
            MoonPhase.NEW_MOON.atLunation(min);
            fail("min-lunation should have failed: " + min);
        } catch (IllegalArgumentException iae) {
            // expected
        }
    }

    @Test
    public void maxLunation() {
        int max = MoonPhase.maxLunation();
        try {
            MoonPhase.LAST_QUARTER.atLunation(max);
        } catch (IllegalArgumentException iae) {
            fail("max-lunation failed: " + max);
        }

        max++;
        try {
            MoonPhase.LAST_QUARTER.atLunation(max);
            fail("max-lunation should have failed: " + max);
        } catch (IllegalArgumentException iae) {
            // expected
        }
    }

    @Test
    public void apogee() { // Meeus - example 50.a

        Moment m = PlainDate.of(1988, 10, 1).at(PlainTime.midnightAtStartOfDay()).atUTC();
        Moment result = PlainDate.of(1988, 10, 7).at(PlainTime.of(20, 29)).atUTC();
        assertThat(MoonPosition.inNextApogeeAfter(m), is(result));

        System.out.println(MoonPosition.at(result, GeoLocation.of(0.0, 0.0)).getDistance()); // 405978 km
    }

    @Test
    public void perigee() { // vgl. TimeAndDate (two minutes difference)

        Moment m = PlainDate.of(2019, 1, 1).at(PlainTime.midnightAtStartOfDay()).atUTC();
        Moment result = PlainDate.of(2019, 1, 21).at(PlainTime.of(19, 57)).atUTC();
        assertThat(MoonPosition.inNextPerigeeAfter(m), is(result));

        System.out.println(MoonPosition.at(result, GeoLocation.of(0.0, 0.0)).getDistance()); // 357344 km

    }

    @Test
    public void stableLoopingOfAnomalisticMonths() {

        Moment m = PlainDate.of(1988, 7, 1).at(PlainTime.midnightAtStartOfDay()).atUTC();
        int lunation = 0; // start

        while (lunation < 4) {
            lunation++;
            m = MoonPosition.inNextApogeeAfter(m);
        }

        Moment result = PlainDate.of(1988, 10, 7).at(PlainTime.of(20, 29)).atUTC();
        assertThat(m, is(result));
        assertThat(lunation, is(4));

    }

}