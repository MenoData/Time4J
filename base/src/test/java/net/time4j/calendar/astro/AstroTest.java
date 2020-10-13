package net.time4j.calendar.astro;

import net.time4j.CalendarUnit;
import net.time4j.ClockUnit;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.engine.CalendarDays;
import net.time4j.engine.ChronoException;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static net.time4j.calendar.astro.AstronomicalSeason.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;


@RunWith(JUnit4.class)
public class AstroTest {

    private static final double TOLERANCE = 0.001;

    @Test
    public void nasaSeasonsInMinutePrecision() { // see: http://aa.usno.navy.mil/data/docs/EarthSeasons.php
        timeOfSeason(2000, VERNAL_EQUINOX, PlainTimestamp.of(2000, 3, 20, 7, 35));
        timeOfSeason(2000, SUMMER_SOLSTICE, PlainTimestamp.of(2000, 6, 21, 1, 48));
        timeOfSeason(2000, AUTUMNAL_EQUINOX, PlainTimestamp.of(2000, 9, 22, 17, 28));
        timeOfSeason(2000, WINTER_SOLSTICE, PlainTimestamp.of(2000, 12, 21, 13, 37));

        timeOfSeason(2001, VERNAL_EQUINOX, PlainTimestamp.of(2001, 3, 20, 13, 31));
        timeOfSeason(2001, SUMMER_SOLSTICE, PlainTimestamp.of(2001, 6, 21, 7, 38));
        timeOfSeason(2001, AUTUMNAL_EQUINOX, PlainTimestamp.of(2001, 9, 22, 23, 4));
        timeOfSeason(2001, WINTER_SOLSTICE, PlainTimestamp.of(2001, 12, 21, 19, 21));

        timeOfSeason(2002, VERNAL_EQUINOX, PlainTimestamp.of(2002, 3, 20, 19, 16));
        timeOfSeason(2002, SUMMER_SOLSTICE, PlainTimestamp.of(2002, 6, 21, 13, 24));
        timeOfSeason(2002, AUTUMNAL_EQUINOX, PlainTimestamp.of(2002, 9, 23, 4, 55));
        timeOfSeason(2002, WINTER_SOLSTICE, PlainTimestamp.of(2002, 12, 22, 1, 14));

        timeOfSeason(2003, VERNAL_EQUINOX, PlainTimestamp.of(2003, 3, 21, 1, 0));
        timeOfSeason(2003, SUMMER_SOLSTICE, PlainTimestamp.of(2003, 6, 21, 19, 10));
        timeOfSeason(2003, AUTUMNAL_EQUINOX, PlainTimestamp.of(2003, 9, 23, 10, 47));
        timeOfSeason(2003, WINTER_SOLSTICE, PlainTimestamp.of(2003, 12, 22, 7, 4));

        timeOfSeason(2004, VERNAL_EQUINOX, PlainTimestamp.of(2004, 3, 20, 6, 49));
        timeOfSeason(2004, SUMMER_SOLSTICE, PlainTimestamp.of(2004, 6, 21, 0, 57));
        timeOfSeason(2004, AUTUMNAL_EQUINOX, PlainTimestamp.of(2004, 9, 22, 16, 30));
        timeOfSeason(2004, WINTER_SOLSTICE, PlainTimestamp.of(2004, 12, 21, 12, 42));

        timeOfSeason(2005, VERNAL_EQUINOX, PlainTimestamp.of(2005, 3, 20, 12, 33));
        timeOfSeason(2005, SUMMER_SOLSTICE, PlainTimestamp.of(2005, 6, 21, 6, 46));
        timeOfSeason(2005, AUTUMNAL_EQUINOX, PlainTimestamp.of(2005, 9, 22, 22, 23));
        timeOfSeason(2005, WINTER_SOLSTICE, PlainTimestamp.of(2005, 12, 21, 18, 35));

        timeOfSeason(2006, VERNAL_EQUINOX, PlainTimestamp.of(2006, 3, 20, 18, 26));
        timeOfSeason(2006, SUMMER_SOLSTICE, PlainTimestamp.of(2006, 6, 21, 12, 26));
        timeOfSeason(2006, AUTUMNAL_EQUINOX, PlainTimestamp.of(2006, 9, 23, 4, 3));
        timeOfSeason(2006, WINTER_SOLSTICE, PlainTimestamp.of(2006, 12, 22, 0, 22));

        timeOfSeason(2007, VERNAL_EQUINOX, PlainTimestamp.of(2007, 3, 21, 0, 7));
        timeOfSeason(2007, SUMMER_SOLSTICE, PlainTimestamp.of(2007, 6, 21, 18, 6));
        timeOfSeason(2007, AUTUMNAL_EQUINOX, PlainTimestamp.of(2007, 9, 23, 9, 51));
        timeOfSeason(2007, WINTER_SOLSTICE, PlainTimestamp.of(2007, 12, 22, 6, 8));

        timeOfSeason(2008, VERNAL_EQUINOX, PlainTimestamp.of(2008, 3, 20, 5, 48));
        timeOfSeason(2008, SUMMER_SOLSTICE, PlainTimestamp.of(2008, 6, 20, 23, 59));
        timeOfSeason(2008, AUTUMNAL_EQUINOX, PlainTimestamp.of(2008, 9, 22, 15, 44));
        timeOfSeason(2008, WINTER_SOLSTICE, PlainTimestamp.of(2008, 12, 21, 12, 4));

        timeOfSeason(2009, VERNAL_EQUINOX, PlainTimestamp.of(2009, 3, 20, 11, 44));
        timeOfSeason(2009, SUMMER_SOLSTICE, PlainTimestamp.of(2009, 6, 21, 5, 46));
        timeOfSeason(2009, AUTUMNAL_EQUINOX, PlainTimestamp.of(2009, 9, 22, 21, 19));
        timeOfSeason(2009, WINTER_SOLSTICE, PlainTimestamp.of(2009, 12, 21, 17, 47));

        timeOfSeason(2010, VERNAL_EQUINOX, PlainTimestamp.of(2010, 3, 20, 17, 32));
        timeOfSeason(2010, SUMMER_SOLSTICE, PlainTimestamp.of(2010, 6, 21, 11, 28));
        timeOfSeason(2010, AUTUMNAL_EQUINOX, PlainTimestamp.of(2010, 9, 23, 3, 9));
        timeOfSeason(2010, WINTER_SOLSTICE, PlainTimestamp.of(2010, 12, 21, 23, 38));

        timeOfSeason(2011, VERNAL_EQUINOX, PlainTimestamp.of(2011, 3, 20, 23, 21));
        timeOfSeason(2011, SUMMER_SOLSTICE, PlainTimestamp.of(2011, 6, 21, 17, 16));
        timeOfSeason(2011, AUTUMNAL_EQUINOX, PlainTimestamp.of(2011, 9, 23, 9, 5));
        timeOfSeason(2011, WINTER_SOLSTICE, PlainTimestamp.of(2011, 12, 22, 5, 30));

        timeOfSeason(2012, VERNAL_EQUINOX, PlainTimestamp.of(2012, 3, 20, 5, 14));
        timeOfSeason(2012, SUMMER_SOLSTICE, PlainTimestamp.of(2012, 6, 20, 23, 9));
        timeOfSeason(2012, AUTUMNAL_EQUINOX, PlainTimestamp.of(2012, 9, 22, 14, 49));
        timeOfSeason(2012, WINTER_SOLSTICE, PlainTimestamp.of(2012, 12, 21, 11, 12));

        timeOfSeason(2013, VERNAL_EQUINOX, PlainTimestamp.of(2013, 3, 20, 11, 2));
        timeOfSeason(2013, SUMMER_SOLSTICE, PlainTimestamp.of(2013, 6, 21, 5, 4));
        timeOfSeason(2013, AUTUMNAL_EQUINOX, PlainTimestamp.of(2013, 9, 22, 20, 44));
        timeOfSeason(2013, WINTER_SOLSTICE, PlainTimestamp.of(2013, 12, 21, 17, 11));

        timeOfSeason(2014, VERNAL_EQUINOX, PlainTimestamp.of(2014, 3, 20, 16, 57));
        timeOfSeason(2014, SUMMER_SOLSTICE, PlainTimestamp.of(2014, 6, 21, 10, 51));
        timeOfSeason(2014, AUTUMNAL_EQUINOX, PlainTimestamp.of(2014, 9, 23, 2, 29));
        timeOfSeason(2014, WINTER_SOLSTICE, PlainTimestamp.of(2014, 12, 21, 23, 3));

        timeOfSeason(2015, VERNAL_EQUINOX, PlainTimestamp.of(2015, 3, 20, 22, 45));
        timeOfSeason(2015, SUMMER_SOLSTICE, PlainTimestamp.of(2015, 6, 21, 16, 38));
        timeOfSeason(2015, AUTUMNAL_EQUINOX, PlainTimestamp.of(2015, 9, 23, 8, 21));
        timeOfSeason(2015, WINTER_SOLSTICE, PlainTimestamp.of(2015, 12, 22, 4, 48));

        timeOfSeason(2016, VERNAL_EQUINOX, PlainTimestamp.of(2016, 3, 20, 4, 30));
        timeOfSeason(2016, SUMMER_SOLSTICE, PlainTimestamp.of(2016, 6, 20, 22, 34));
        timeOfSeason(2016, AUTUMNAL_EQUINOX, PlainTimestamp.of(2016, 9, 22, 14, 21));
        timeOfSeason(2016, WINTER_SOLSTICE, PlainTimestamp.of(2016, 12, 21, 10, 44));

        timeOfSeason(2017, VERNAL_EQUINOX, PlainTimestamp.of(2017, 3, 20, 10, 29));
        timeOfSeason(2017, SUMMER_SOLSTICE, PlainTimestamp.of(2017, 6, 21, 4, 24));
        timeOfSeason(2017, AUTUMNAL_EQUINOX, PlainTimestamp.of(2017, 9, 22, 20, 2));
        timeOfSeason(2017, WINTER_SOLSTICE, PlainTimestamp.of(2017, 12, 21, 16, 28));

        timeOfSeason(2018, VERNAL_EQUINOX, PlainTimestamp.of(2018, 3, 20, 16, 15));
        timeOfSeason(2018, SUMMER_SOLSTICE, PlainTimestamp.of(2018, 6, 21, 10, 7));
        timeOfSeason(2018, AUTUMNAL_EQUINOX, PlainTimestamp.of(2018, 9, 23, 1, 54));
        timeOfSeason(2018, WINTER_SOLSTICE, PlainTimestamp.of(2018, 12, 21, 22, 23));

        timeOfSeason(2019, VERNAL_EQUINOX, PlainTimestamp.of(2019, 3, 20, 21, 58));
        timeOfSeason(2019, SUMMER_SOLSTICE, PlainTimestamp.of(2019, 6, 21, 15, 54));
        timeOfSeason(2019, AUTUMNAL_EQUINOX, PlainTimestamp.of(2019, 9, 23, 7, 50));
        timeOfSeason(2019, WINTER_SOLSTICE, PlainTimestamp.of(2019, 12, 22, 4, 19));

        timeOfSeason(2020, VERNAL_EQUINOX, PlainTimestamp.of(2020, 3, 20, 3, 50));
        timeOfSeason(2020, SUMMER_SOLSTICE, PlainTimestamp.of(2020, 6, 20, 21, 44));
        timeOfSeason(2020, AUTUMNAL_EQUINOX, PlainTimestamp.of(2020, 9, 22, 13, 31));
        timeOfSeason(2020, WINTER_SOLSTICE, PlainTimestamp.of(2020, 12, 21, 10, 2));

        timeOfSeason(2021, VERNAL_EQUINOX, PlainTimestamp.of(2021, 3, 20, 9, 37));
        timeOfSeason(2021, SUMMER_SOLSTICE, PlainTimestamp.of(2021, 6, 21, 3, 32));
        timeOfSeason(2021, AUTUMNAL_EQUINOX, PlainTimestamp.of(2021, 9, 22, 19, 21));
        timeOfSeason(2021, WINTER_SOLSTICE, PlainTimestamp.of(2021, 12, 21, 15, 59));

        timeOfSeason(2022, VERNAL_EQUINOX, PlainTimestamp.of(2022, 3, 20, 15, 33));
        timeOfSeason(2022, SUMMER_SOLSTICE, PlainTimestamp.of(2022, 6, 21, 9, 14));
        timeOfSeason(2022, AUTUMNAL_EQUINOX, PlainTimestamp.of(2022, 9, 23, 1, 4));
        timeOfSeason(2022, WINTER_SOLSTICE, PlainTimestamp.of(2022, 12, 21, 21, 48));

        timeOfSeason(2023, VERNAL_EQUINOX, PlainTimestamp.of(2023, 3, 20, 21, 24));
        timeOfSeason(2023, SUMMER_SOLSTICE, PlainTimestamp.of(2023, 6, 21, 14, 58));
        timeOfSeason(2023, AUTUMNAL_EQUINOX, PlainTimestamp.of(2023, 9, 23, 6, 50));
        timeOfSeason(2023, WINTER_SOLSTICE, PlainTimestamp.of(2023, 12, 22, 3, 27));

        timeOfSeason(2024, VERNAL_EQUINOX, PlainTimestamp.of(2024, 3, 20, 3, 6));
        timeOfSeason(2024, SUMMER_SOLSTICE, PlainTimestamp.of(2024, 6, 20, 20, 51));
        timeOfSeason(2024, AUTUMNAL_EQUINOX, PlainTimestamp.of(2024, 9, 22, 12, 44));
        timeOfSeason(2024, WINTER_SOLSTICE, PlainTimestamp.of(2024, 12, 21, 9, 21));

        timeOfSeason(2025, VERNAL_EQUINOX, PlainTimestamp.of(2025, 3, 20, 9, 1));
        timeOfSeason(2025, SUMMER_SOLSTICE, PlainTimestamp.of(2025, 6, 21, 2, 42));
        timeOfSeason(2025, AUTUMNAL_EQUINOX, PlainTimestamp.of(2025, 9, 22, 18, 19));
        timeOfSeason(2025, WINTER_SOLSTICE, PlainTimestamp.of(2025, 12, 21, 15, 3));
    }

    private static void timeOfSeason(
        int year,
        AstronomicalSeason season,
        PlainTimestamp expected
    ) {
        PlainTimestamp ut = season.inYear(year).get(SolarTime.onAverage(ZonalOffset.UTC));

        if (Math.abs(ClockUnit.MINUTES.between(ut, expected)) > 0L) {
            fail("Expected: " + expected + ", but was: " + ut);
        }
    }

    @Test
    public void countOfSeasons() {
        assertThat(AstronomicalSeason.values().length, is(4));
    }

    @Test
    public void onNorthernHemisphere() {
        for (AstronomicalSeason season : AstronomicalSeason.values()) {
            assertThat(season.onNorthernHemisphere(), is(season));
        }
    }

    @Test
    public void onSouthernHemisphere() {
        assertThat(VERNAL_EQUINOX.onSouthernHemisphere(), is(AUTUMNAL_EQUINOX));
        assertThat(SUMMER_SOLSTICE.onSouthernHemisphere(), is(WINTER_SOLSTICE));
        assertThat(AUTUMNAL_EQUINOX.onSouthernHemisphere(), is(VERNAL_EQUINOX));
        assertThat(WINTER_SOLSTICE.onSouthernHemisphere(), is(SUMMER_SOLSTICE));
    }

    @Test
    public void solarTimeBuilder() {
        assertThat( // Hamburg
            SolarTime.ofLocation().northernLatitude(53, 0, 0).easternLongitude(10, 0, 0).build(),
            is(SolarTime.ofLocation(53, 10)));
        assertThat( // Kilimanjaro
            SolarTime.ofLocation().southernLatitude(3, 4, 0).easternLongitude(37, 21, 33).atAltitude(5895).build(),
            is(SolarTime.ofLocation(-3 - 4 / 60.0, 37 + 21 / 60.0 + 33 / 3600.0, 5895, StdSolarCalculator.NOAA)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void solarTimeBuilderEx1() {
        SolarTime.ofLocation().northernLatitude(90, 0, 0.001).easternLongitude(10, 0, 0).build();
    }

    @Test(expected=IllegalArgumentException.class)
    public void solarTimeBuilderEx2() {
        SolarTime.ofLocation().northernLatitude(40, 0, 0).westernLongitude(180, 0, 0.001).build();
    }

    @Test
    public void solarTimeTeheran() {
        ZonalOffset teheran = ZonalOffset.atLongitude(OffsetSign.AHEAD_OF_UTC, 52, 30, 0.0); // +03:30
        Moment spring2024 = AstronomicalSeason.VERNAL_EQUINOX.inYear(2024);
        Moment spring2025 = AstronomicalSeason.VERNAL_EQUINOX.inYear(2025);
        PlainTimestamp tsp2024 = spring2024.get(SolarTime.apparentAt(teheran));
        PlainTimestamp tsp2025 = spring2025.get(SolarTime.apparentAt(teheran));
        PlainTimestamp tsp2024NOAA = spring2024.get(SolarTime.apparentAt(teheran, "NOAA"));
        PlainTimestamp tsp2025NOAA = spring2025.get(SolarTime.apparentAt(teheran, "NOAA"));
        assertThat(tsp2024, is(tsp2024NOAA));
        assertThat(tsp2025, is(tsp2025NOAA));
        System.out.println("apparent solar time of Teheran: " + spring2024.get(SolarTime.apparentAt(teheran)));
        System.out.println("apparent solar time of Teheran: " + spring2025.get(SolarTime.apparentAt(teheran)));
        assertThat(tsp2024.getHour() < 12, is(true));
        assertThat(tsp2025.getHour() >= 12, is(true));
        assertThat(
            CalendarDays.between(tsp2024.toDate(), tsp2025.toDate().plus(CalendarDays.ONE)).getAmount(),
            is(366L)); // includes noon correction
    }

    @Test
    public void equationOfTime() {
        // maximum deviation: about 1/100 min
        // see also => https://www.esrl.noaa.gov/gmd/grad/solcalc/
        ZonalOffset offset = ZonalOffset.atLongitude(new BigDecimal(-98.583)); // middle of US
        assertThat(
            equationOfTimeRounded(2017, 1, 25, 6, 26, 14, offset),
            is(-1239));
        assertThat(
            equationOfTimeRounded(2017, 2, 25, 6, 26, 14, offset),
            is(-1301));
        assertThat(
            equationOfTimeRounded(2017, 3, 25, 6, 26, 14, offset),
            is(-587));
        assertThat(
            equationOfTimeRounded(2017, 4, 25, 6, 26, 14, offset),
            is(208));
        assertThat(
            equationOfTimeRounded(2017, 5, 25, 6, 26, 14, offset),
            is(303));
        assertThat(
            equationOfTimeRounded(2017, 6, 25, 6, 26, 14, offset),
            is(-271));
        assertThat(
            equationOfTimeRounded(2017, 7, 25, 6, 26, 14, offset),
            is(-654));
        assertThat(
            equationOfTimeRounded(2017, 8, 25, 6, 26, 14, offset),
            is(-203));
        assertThat(
            equationOfTimeRounded(2017, 9, 25, 6, 26, 14, offset),
            is(839));
        assertThat(
            equationOfTimeRounded(2017, 10, 25, 6, 26, 14, offset),
            is(1599));
        assertThat(
            equationOfTimeRounded(2017, 11, 25, 6, 26, 14, offset),
            is(1294)); // 1/100-minute deviation from NOAA
        assertThat(
            equationOfTimeRounded(2017, 12, 25, 6, 26, 14, offset),
            is(-19));
    }

    private int equationOfTimeRounded( // multiplied by 100 and then rounded to minutes
        int year,
        int month,
        int dom,
        int hour,
        int minute,
        int second,
        ZonalOffset offset
    ) {
        PlainTimestamp tsp = PlainTimestamp.of(year, month, dom, hour, minute, second);
        Moment moment = tsp.at(offset);
        double eot = SolarTime.equationOfTime(moment) / 60;
        return (int) Math.round(eot * 100);
    }

    @Test
    public void stdCalculators() {
        String[] names = {"NOAA", "SIMPLE", "CC", "TIME4J"};
        for (String name : names) {
            assertThat(name, is(SolarTime.ofLocation(0, 0, 0, name).getCalculator().name()));
        }
    }

    @Test
    public void ofLocation() {
        SolarTime ny1 = SolarTime.ofLocation(40.9, -74.3);
        SolarTime ny2 = SolarTime.ofLocation(40.9, -74.3, 0, "NOAA");
        assertThat(ny1, is(ny2)); // we assume no service loader in the background here
    }

    @Test
    public void williamsNewYork() { // see: http://www.edwilliams.org/sunrise_sunset_example.htm
        PlainDate date = PlainDate.of(1990, 6, 25);
        SolarTime ny = SolarTime.ofLocation(40.9, -74.3, 0, "SIMPLE");
        TZID tzid = () -> "America/New_York";
        assertThat(
            date.get(ny.sunrise()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(1990, 6, 25, 5, 26)));
        assertThat(
            date.get(ny.sunset()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(1990, 6, 25, 20, 33)));
        assertThat(
            ny.getCalculator().name(),
            is("SIMPLE"));
        assertThat(
            ny.getLatitude(),
            is(40.9));
        assertThat(
            ny.getLongitude(),
            is(-74.3));
        assertThat(
            ny.getAltitude(),
            is(0));
        assertThat(
            date.get(ny.sunrise()).get().toZonalTimestamp(tzid).toTime(),
            is(PlainTime.of(5, 26)));
        assertThat(
            date.get(ny.sunset()).get().toZonalTimestamp(tzid).toTime(),
            is(PlainTime.of(20, 33)));
    }

    @Test
    public void nooaNewYork() {
        PlainDate date = PlainDate.of(1990, 6, 25);
        SolarTime ny = SolarTime.ofLocation(40.9, -74.3, 0, "NOAA");
        TZID tzid = () -> "America/New_York";
        assertThat(
            date.get(ny.sunrise()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(1990, 6, 25, 5, 26, 32)));
        assertThat(
            date.get(ny.transitAtNoon()).toZonalTimestamp(tzid),
            is(PlainTimestamp.of(1990, 6, 25, 12, 59, 47)));
        assertThat(
            date.get(ny.sunset()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(1990, 6, 25, 20, 32, 56)));
        assertThat(
            ny.getCalculator().name(),
            is("NOAA"));
    }

    @Test
    public void williamsReykjavik() {
        PlainDate date = PlainDate.of(2014, 6, 21);
        SolarTime reykjavik = SolarTime.ofLocation(64.15, -21.93, 0, "SIMPLE");
        TZID tzid = () -> "Atlantic/Reykjavik";
        assertThat(
            date.get(reykjavik.sunrise()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2014, 6, 21, 2, 55)));
        assertThat(
            date.get(reykjavik.sunset()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2014, 6, 22, 0, 4))); // next day is correct!!!
    }

    @Test
    public void noaaReykjavik() {
        PlainDate date = PlainDate.of(2014, 6, 21);
        SolarTime reykjavik = SolarTime.ofLocation(64.15, -21.93, 0, "NOAA");
        TZID tzid = () -> "Atlantic/Reykjavik";
        assertThat(
            date.get(reykjavik.sunrise()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2014, 6, 21, 2, 55, 8)));
        assertThat(
            date.get(reykjavik.sunset()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2014, 6, 22, 0, 3, 52))); // next day is correct!!!
    }

    @Test
    public void williamsGermany() {
        PlainDate date = PlainDate.of(2014, 6, 21);
        SolarTime germany = SolarTime.ofLocation(50.93311, 11.58336, 0, "SIMPLE");
        TZID tzid = () -> "Europe/Berlin";
        assertThat(
            date.get(germany.sunrise()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2014, 6, 21, 4, 59)));
        assertThat(
            date.get(germany.sunset()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2014, 6, 21, 21, 31)));
    }

    @Test
    public void noaaGermany() {
        PlainDate date = PlainDate.of(2014, 6, 21);
        SolarTime germany = SolarTime.ofLocation(50.93311, 11.58336, 0, "NOAA");
        TZID tzid = () -> "Europe/Berlin";
        assertThat(
            date.get(germany.transitAtMidnight()).toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2014, 6, 21, 1, 15, 19)));
        assertThat(
            date.get(germany.sunrise()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2014, 6, 21, 4, 59, 27)));
        assertThat(
            date.get(germany.transitAtNoon()).toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2014, 6, 21, 13, 15, 26)));
        assertThat(
            date.get(germany.sunset()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2014, 6, 21, 21, 31, 25)));
    }

    @Test
    public void williamsWales() {
        PlainDate date = PlainDate.of(2015, 8, 12);
        SolarTime wales = SolarTime.ofLocation(53.284355, -3.581405, 0, "SIMPLE");
        TZID tzid = () -> "Europe/London";
        assertThat(
            date.get(wales.sunrise()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2015, 8, 12, 5, 48)));
        assertThat(
            date.get(wales.sunset()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2015, 8, 12, 20, 48)));
    }

    @Test
    public void noaaWales() {
        PlainDate date = PlainDate.of(2015, 8, 12);
        SolarTime wales = SolarTime.ofLocation(53.284355, -3.581405, 0, "NOAA");
        TZID tzid = () -> "Europe/London";
        assertThat(
            date.get(wales.sunrise()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2015, 8, 12, 5, 48, 33)));
        assertThat(
            date.get(wales.sunset()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2015, 8, 12, 20, 49, 4)));
    }

    @Test
    public void williamsAtlanta() {
        PlainDate date = PlainDate.of(2009, 9, 6);
        SolarTime atlanta = SolarTime.ofLocation(33.766667, -84.416667, 0, "SIMPLE");
        TZID tzid = () -> "America/New_York";
        assertThat(
            date.get(atlanta.sunrise()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2009, 9, 6, 7, 14)));
        assertThat(
            date.get(atlanta.sunset()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2009, 9, 6, 19, 56)));
    }

    @Test
    public void noaaAtlanta() {
        PlainDate date = PlainDate.of(2009, 9, 6);
        SolarTime atlanta = SolarTime.ofLocation(33.766667, -84.416667, 0, "NOAA");
        TZID tzid = () -> "America/New_York";
        assertThat(
            date.get(atlanta.sunrise()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2009, 9, 6, 7, 14, 56)));
        assertThat(
            date.get(atlanta.sunset()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2009, 9, 6, 19, 56, 19)));
    }

    @Test
    public void williamsLapland() {
        PlainDate date = PlainDate.of(2014, 1, 15);
        SolarTime suomi = SolarTime.ofLocation(69.8888, 27, 0, "SIMPLE");
        TZID tzid = () -> "Europe/Helsinki";
        assertThat(
            date.get(suomi.sunrise()).isPresent(),
            is(false)); // polar night
        assertThat(
            date.get(suomi.sunset()).isPresent(),
            is(false)); // polar night

        // sunset without sunrise, expected bug in algorithm (limited precision as deficiency)
        date = date.plus(1, CalendarUnit.DAYS);
        assertThat(
            date.get(suomi.sunrise()).isPresent(),
            is(false)); // should be true
        assertThat(
            date.get(suomi.sunset()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2014, 1, 16, 12, 36)));

        date = date.plus(1, CalendarUnit.DAYS);
        assertThat(
            date.get(suomi.sunrise()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2014, 1, 17, 11, 54))); // NOAA=T11:46
        assertThat(
            date.get(suomi.sunset()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2014, 1, 17, 12, 59)));
    }

    @Test
    public void noaaLapland1() {
        PlainDate date = PlainDate.of(2014, 1, 15);
        SolarTime suomi = SolarTime.ofLocation(69.8888, 27, 0, "NOAA");
        TZID tzid = () -> "Europe/Helsinki";
        assertThat(
            date.get(suomi.sunrise()).isPresent(),
            is(false)); // polar night
        assertThat(
            date.get(suomi.sunset()).isPresent(),
            is(false)); // polar night

        // Original NOAA says: still polar night (probably rounding error due to fractional longitude/zenith)
        date = date.plus(1, CalendarUnit.DAYS);
        assertThat(
            date.get(suomi.sunrise()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2014, 1, 16, 12, 7, 9)));
        assertThat(
            date.get(suomi.sunset()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2014, 1, 16, 12, 37, 3)));

        // good agreement with original NOAA
        date = date.plus(1, CalendarUnit.DAYS);
        assertThat(
            date.get(suomi.sunrise()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2014, 1, 17, 11, 45, 53)));
        assertThat(
            date.get(suomi.sunset()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2014, 1, 17, 12, 59, 0)));
    }

    @Test
    public void noaaLapland2() {
        PlainDate date = PlainDate.of(2014, 1, 16);
        SolarTime suomi = SolarTime.ofLocation(70, 28, 0, "NOAA");
        TZID eest = ZonalOffset.ofHoursMinutes(OffsetSign.AHEAD_OF_UTC, 2, 0);
        assertThat(
            date.get(suomi.sunrise()).isPresent(),
            is(false)); // polar night
        assertThat(
            date.get(suomi.sunset()).isPresent(),
            is(false)); // polar night
        assertThat(
            date.matches(suomi.polarNight()),
            is(true));
        assertThat(
            date.matches(suomi.midnightSun()),
            is(false));

        // good agreement with NOAA
        date = PlainDate.of(2014, 1, 17);
        assertThat(
            date.get(suomi.sunrise()).get().toZonalTimestamp(eest),
            is(PlainTimestamp.of(2014, 1, 17, 11, 51, 58)));
        assertThat(
            date.get(suomi.sunset()).get().toZonalTimestamp(eest),
            is(PlainTimestamp.of(2014, 1, 17, 12, 44, 56)));

        // good agreement with NOAA
        date = PlainDate.of(2014, 1, 18);
        assertThat(
            date.get(suomi.sunrise()).get().toZonalTimestamp(eest),
            is(PlainTimestamp.of(2014, 1, 18, 11, 35, 41)));
        assertThat(
            date.get(suomi.sunset()).get().toZonalTimestamp(eest),
            is(PlainTimestamp.of(2014, 1, 18, 13, 1, 53)));

        // good agreement with NOAA
        ZonalOffset eedt = ZonalOffset.ofHoursMinutes(OffsetSign.AHEAD_OF_UTC, 3, 0);
        date = PlainDate.of(2014, 5, 15);
        assertThat(
            date.get(suomi.sunrise()).get().toZonalTimestamp(eedt),
            is(PlainTimestamp.of(2014, 5, 15, 1, 51, 3)));
        assertThat(
            date.get(suomi.sunset()).get().toZonalTimestamp(eedt),
            is(PlainTimestamp.of(2014, 5, 16, 0, 34, 4)));
        SolarTime.Sunshine sunshine = date.get(suomi.sunshine(eedt));
        assertThat(
            sunshine.startUTC(),
            is(date.get(suomi.sunrise()).get()));
        assertThat(
            sunshine.endUTC(),
            is(date.get(suomi.sunset()).get()));
        assertThat(
            sunshine.startLocal(),
            is(date.get(suomi.sunrise()).get().toZonalTimestamp(eedt)));
        assertThat(
            sunshine.endLocal(),
            is(date.get(suomi.sunset()).get().toZonalTimestamp(eedt)));
        assertThat(
            sunshine.length(),
            is(81781));
        assertThat(
            sunshine.isAbsent(),
            is(false));
        assertThat(
            sunshine.isPresent(date.get(suomi.sunrise()).get()),
            is(true));
        assertThat(
            sunshine.isPresent(date.get(suomi.sunset()).get().toZonalTimestamp(eedt)),
            is(false));

        // good agreement with NOAA
        date = PlainDate.of(2014, 5, 16);
        assertThat(
            date.get(suomi.sunrise()).get().toZonalTimestamp(eedt),
            is(PlainTimestamp.of(2014, 5, 16, 1, 33, 48)));
        assertThat(
            date.get(suomi.sunset()).isPresent(),
            is(false));

        date = PlainDate.of(2014, 5, 17);
        assertThat(
            date.get(suomi.sunrise()).isPresent(),
            is(false)); // midnight sun
        assertThat(
            date.get(suomi.sunset()).isPresent(),
            is(false)); // midnight sun
        assertThat(
            date.matches(suomi.polarNight()),
            is(false));
        assertThat(
            date.matches(suomi.midnightSun()),
            is(true));
    }

    @Test
    public void williamsResolute() {
        PlainDate date = PlainDate.of(2016, 11, 1);
        SolarTime resolute = SolarTime.ofLocation(74.6973, -94.8297, 0, "SIMPLE");
        TZID tzid = () -> "America/Resolute";
        assertThat(
            date.get(resolute.sunrise()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2016, 11, 1, 11, 22)));
        assertThat(
            date.get(resolute.sunset()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2016, 11, 1, 14, 38))); // higher delta of 5 minutes compared with NOAA
    }

    @Test
    public void noaaResolute() {
        PlainDate date = PlainDate.of(2016, 11, 1);
        SolarTime resolute = SolarTime.ofLocation(74.6973, -94.8297, 0, "NOAA");
        TZID tzid = () -> "America/Resolute";
        assertThat(
            date.get(resolute.sunrise()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2016, 11, 1, 11, 20, 49)));
        assertThat(
            date.get(resolute.transitAtNoon()).toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2016, 11, 1, 13, 2, 51)));
        assertThat(
            date.get(resolute.sunset()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2016, 11, 1, 14, 43, 15)));
    }

    @Test
    public void williamsSanFrancisco() {
        PlainDate date = PlainDate.of(2016, 5, 3);
        SolarTime sanFrancisco = SolarTime.ofLocation(37.739558, -122.479749, 0, "SIMPLE");
        TZID tzid = () -> "America/Los_Angeles";
        assertThat(
            date.get(sanFrancisco.sunrise()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2016, 5, 3, 6, 11)));
        assertThat(
            date.get(sanFrancisco.sunset()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2016, 5, 3, 20, 3)));
    }

    @Test
    public void noaaSanFrancisco() {
        PlainDate date = PlainDate.of(2016, 5, 3);
        SolarTime sanFrancisco = SolarTime.ofLocation(37.739558, -122.479749, 0, "NOAA");
        TZID tzid = () -> "America/Los_Angeles";
        assertThat(
            date.get(sanFrancisco.sunrise()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2016, 5, 3, 6, 11, 17)));
        assertThat(
            date.get(sanFrancisco.sunset()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2016, 5, 3, 20, 2, 44)));
    }

    @Test
    public void maxElevation() {
        SolarTime st = SolarTime.ofLocation(40, -105, 0, "NOAA");
        assertThat(
            Math.floor(st.getHighestElevationOfSun(PlainDate.of(2010, 6, 21)) * 100) / 100,
            is(73.43)); // from NOAA-spreadsheet
    }

    @Test
    public void sydneyWilliams() {
        SolarTime sydney = SolarTime.ofLocation(-33.85, 151.2, 0, StdSolarCalculator.SIMPLE);
        PlainDate date = PlainDate.of(2017, 1, 1);
        Moment sunrise = date.get(sydney.sunrise()).get(); // 2016-12-31T18:48:00Z
        Moment expected = date.at(PlainTime.of(5, 48)).at(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 11));
        assertThat(
            sunrise.toString(),
            Math.abs(expected.until(sunrise, TimeUnit.MINUTES)) == 0, // exact agreement in minute precision
            is(true));
    }

    @Test
    public void sydneyNOAA() {
        SolarTime sydney = SolarTime.ofLocation(-33.85, 151.2, 0, StdSolarCalculator.NOAA);
        PlainDate date = PlainDate.of(2017, 1, 1);
        Moment sunrise = date.get(sydney.sunrise()).get(); // 2016-12-31T18:47:53Z
        Moment expected = date.at(PlainTime.of(5, 48)).at(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 11));
        assertThat(
            sunrise.toString(),
            Math.abs(expected.until(sunrise, TimeUnit.MINUTES)) == 0, // exact agreement in minute precision
            is(true));
    }

    @Test
    public void sydneyCC() {
        SolarTime sydney = SolarTime.ofLocation(-33.85, 151.2, 0, StdSolarCalculator.CC);
        PlainDate date = PlainDate.of(2017, 1, 1);
        Moment sunrise = date.get(sydney.sunrise()).get(); // 2016-12-31T18:47:45Z
        Moment expected = date.at(PlainTime.of(5, 48)).at(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 11));
        assertThat(
            sunrise.toString(),
            Math.abs(expected.until(sunrise, TimeUnit.MINUTES)) == 0, // exact agreement in minute precision
            is(true));
    }

    @Test
    public void sydneyTime4J() {
        SolarTime sydney = SolarTime.ofLocation(-33.85, 151.2, 0, StdSolarCalculator.TIME4J);
        PlainDate date = PlainDate.of(2017, 1, 1);
        Moment sunrise = date.get(sydney.sunrise()).get(); // 2016-12-31T18:47:53Z
        Moment expected = date.at(PlainTime.of(5, 48)).at(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 11));
        assertThat(
            sunrise.toString(),
            Math.abs(expected.until(sunrise, TimeUnit.MINUTES)) == 0, // exact agreement in minute precision
            is(true));
    }

    @Test
    public void kilimanjaro() {
        PlainDate date = PlainDate.of(2017, 12, 22);
        TZID tzid = () -> "Africa/Dar_es_Salaam"; // Tanzania: UTC+03:00
        // high altitude => earlier sunrise and later sunset
        SolarTime kibo5895 =
            SolarTime.ofLocation()
                .southernLatitude(3, 4, 0)
                .easternLongitude(37, 21, 33)
                .atAltitude(5895)
                .usingCalculator(StdSolarCalculator.TIME4J)
                .build();
        assertThat(
            date.get(kibo5895.sunrise()).get().toZonalTimestamp(tzid).toTime(),
            is(PlainTime.of(6, 10, 35))); // 6:09:28 with same atmospheric refraction as on sea level
        assertThat(
            date.get(kibo5895.sunset()).get().toZonalTimestamp(tzid).toTime(),
            is(PlainTime.of(18, 47, 47))); // 18:48:55 with same atmospheric refraction as on sea level
        assertThat(
            kibo5895.getAltitude(),
            is(5895));
        assertThat(
            date.get(kibo5895.sunshine(tzid)).length(),
            is(12 * 3600 + 37 * 60 + 12));
        // good agreement with NOAA
        SolarTime kiboSeaLevel =
            SolarTime.ofLocation()
                .southernLatitude(3, 4, 0)
                .easternLongitude(37, 21, 33)
                .atAltitude(0)
                .usingCalculator("NOAA")
                .build();
        assertThat(
            date.get(kiboSeaLevel.sunrise()).get().toZonalTimestamp(tzid).toTime(),
            is(PlainTime.of(6, 20, 13)));
        assertThat(
            date.get(kiboSeaLevel.sunset()).get().toZonalTimestamp(tzid).toTime(),
            is(PlainTime.of(18, 38, 9)));
        assertThat(
            kiboSeaLevel.getAltitude(),
            is(0));
        assertThat(
            date.get(kiboSeaLevel.sunshine(tzid)).length(),
            is(12 * 3600 + 17 * 60 + 56));
    }

    @Test(expected=IllegalStateException.class)
    public void latitudeOrLongitudeTwice() {
        SolarTime.ofLocation()
            .northernLatitude(53, 0, 0)
            .southernLatitude(15, 0, 0)
            .westernLongitude(10, 0, 0)
            .easternLongitude(72, 0, 0)
            .build();
    }

    @Test
    public void positionOfSun() {
        JulianDay jd =
            JulianDay.ofEphemerisTime(
                PlainDate.of(1992, 10, 13),
                PlainTime.midnightAtStartOfDay(),
                ZonalOffset.UTC
            );
        assertThat(
            Math.abs(StdSolarCalculator.SIMPLE.rightAscension(jd.getValue()) - 198.76419030829365) < TOLERANCE,
            is(true));
        assertThat(
            Math.abs(StdSolarCalculator.SIMPLE.declination(jd.getValue()) + 7.939068113994067) < TOLERANCE,
            is(true));
        assertThat(
            Math.abs(StdSolarCalculator.NOAA.rightAscension(jd.getValue()) - 198.38082521237328) < TOLERANCE,
            is(true));
        assertThat(
            Math.abs(StdSolarCalculator.NOAA.declination(jd.getValue()) + 7.785069796023854) < TOLERANCE,
            is(true));
        assertThat(
            Math.abs(StdSolarCalculator.CC.rightAscension(jd.getValue()) - 198.37832979724269) < TOLERANCE,
            is(true));
        assertThat(
            Math.abs(StdSolarCalculator.CC.declination(jd.getValue()) + 7.7841496152567915) < TOLERANCE,
            is(true));
        assertThat(
            Math.abs(StdSolarCalculator.TIME4J.rightAscension(jd.getValue()) - 198.37826281251168) < TOLERANCE,
            is(true));
        assertThat(
            Math.abs(StdSolarCalculator.TIME4J.declination(jd.getValue()) + 7.784091134226315) < TOLERANCE,
            is(true));

        // comparison with VSOP87-theory
        System.out.println("ra-VSOP87: " + String.valueOf(15 * (30.749 / 3600 + 13 / 60 + 13))); // 195.12812083333333
        System.out.println("decl-VSOP87: " + String.valueOf(-1.74 / 3600 - 47 / 60 - 7)); // -7.000483333333333
    }

    @Test
    public void lmtSamoa() {
        TZID tzid = Timezone.of("Pacific/Apia").getID();
        SolarTime apia =
            SolarTime.ofLocation().southernLatitude(13, 50, 0).westernLongitude(171, 45, 0).build();
        assertThat(
            PlainDate.of(2011, 12, 28).get(apia.sunrise()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2011, 12, 28, 7, 0, 33))); // before zone change
        assertThat(
            PlainDate.of(2011, 12, 29).get(apia.sunrise()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2011, 12, 29, 7, 1, 6))); // before zone change
        assertThat(
            PlainDate.of(2011, 12, 30).get(apia.sunrise()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2011, 12, 31, 7, 1, 39))); // invalid date
        assertThat(
            PlainDate.of(2011, 12, 31).get(apia.sunrise()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2012, 1, 1, 7, 2, 13))); // civil date is one day later than LMT-date
        assertThat(
            PlainDate.of(2012, 1, 1).get(apia.sunrise()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2012, 1, 2, 7, 2, 47))); // civil date is one day later than LMT-date
        assertThat(
            apia.getObserverZoneID().isPresent(),
            is(false));
    }

    @Test
    public void zonedSamoa() {
        TZID tzid = Timezone.of("Pacific/Apia").getID();
        SolarTime apia =
            SolarTime.ofLocation()
                .southernLatitude(13, 50, 0)
                .westernLongitude(171, 45, 0)
                .inTimezone(tzid)
                .build();
        assertThat(
            PlainDate.of(2011, 12, 29).get(apia.sunrise()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2011, 12, 29, 7, 1, 6))); // before zone change
        try {
            PlainDate.of(2011, 12, 30).get(apia.sunrise()).get().toZonalTimestamp(tzid);
            fail("Invalid date should throw an exception, but passed.");
        } catch (ChronoException ex) {
            // ok, invalid date is expected
        }
        assertThat(
            PlainDate.of(2011, 12, 31).get(apia.sunrise()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2011, 12, 31, 7, 1, 39))); // civil date is same as input date
        assertThat(
            PlainDate.of(2012, 1, 1).get(apia.sunrise()).get().toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2012, 1, 1, 7, 2, 13))); // civil date is same as input date
        assertThat(
            apia.getObserverZoneID().get().canonical(),
            is("Pacific/Apia"));
    }

    @Test
    public void geodeticAngleCC() {
        double dip = StdSolarCalculator.CC.getGeodeticAngle(-22.36, 46);
        assertThat(Math.abs(dip - 0.2535051183347955) < TOLERANCE, is(true));
    }

    @Test
    public void sunPositionHamburg() {
        Timezone tz = Timezone.of("Europe/Berlin");
        SolarTime hh =
            SolarTime.ofLocation()
                .northernLatitude(53, 33, 0.0)
                .easternLongitude(10, 0, 0.0)
                .build();
        Moment moment = PlainTimestamp.of(2017, 6, 15, 7, 30).in(tz);
        SunPosition position = SunPosition.at(moment, hh);

        assertThat(
            Math.abs(position.getAzimuth() - 77.44408289997781) < TOLERANCE,
            is(true)); // usno => 77.4
        assertThat(
            Math.abs(position.getElevation() - 19.986857686242704) < TOLERANCE,
            is(true)); // usno => 20.0
    }

    @Test(expected=UnsupportedOperationException.class)
    public void invalidShadowInPolarRegion() {
        SolarTime svalbard = svalbard(null);
        svalbard.timeOfShadowBeforeNoon(2.0, 100);
    }

    @Test(expected=IllegalArgumentException.class)
    public void invalidObjectHeightIsInfinite() {
        SolarTime shanghai =
            SolarTime.ofLocation()
                .northernLatitude(31, 14, 0.0)
                .easternLongitude(121, 28, 0.0)
                .build();
        shanghai.timeOfShadowBeforeNoon(Double.POSITIVE_INFINITY, 10);
    }

    @Test(expected=IllegalArgumentException.class)
    public void invalidObjectHeightIsZero() {
        SolarTime shanghai =
            SolarTime.ofLocation()
                .northernLatitude(31, 14, 0.0)
                .easternLongitude(121, 28, 0.0)
                .build();
        shanghai.timeOfShadowBeforeNoon(0.0, 10);
    }

    @Test(expected=IllegalArgumentException.class)
    public void invalidShadowIsNegative() {
        SolarTime shanghai =
            SolarTime.ofLocation()
                .northernLatitude(31, 14, 0.0)
                .easternLongitude(121, 28, 0.0)
                .build();
        shanghai.timeOfShadowBeforeNoon(1.8, -10);
    }

    @Test
    public void invalidShadowTooShort() {
        SolarTime shanghai =
            SolarTime.ofLocation()
                .northernLatitude(31, 14, 0.0)
                .easternLongitude(121, 28, 0.0)
                .build();
        Optional<Moment> timeOfShadow =
            shanghai.timeOfShadowBeforeNoon(1.8, 0.5).apply(PlainDate.of(2017, 12, 13));
        assertThat(
            timeOfShadow.isPresent(),
            is(false));
    }

    @Test
    public void sunPositionShanghai() {
        Timezone tz = Timezone.of("Asia/Shanghai");
        SolarTime shanghai =
            SolarTime.ofLocation()
                .northernLatitude(31, 14, 0.0)
                .easternLongitude(121, 28, 0.0)
                .build();
        Moment moment = PlainTimestamp.of(2017, 12, 13, 8, 10).in(tz);
        SunPosition position = SunPosition.at(moment, shanghai);

        assertThat(
            Math.abs(position.getRightAscension() - 260.38484838292726) < TOLERANCE,
            is(true));
        assertThat(
            position.getRightAscensionHMS(),
            is("17:21:32"));
        assertThat(
            Math.abs(position.getDeclination() + 23.140561983747027) < TOLERANCE,
            is(true));
        assertThat(
            Math.abs(position.getAzimuth() - 129.28036788666304) < TOLERANCE,
            is(true)); // usno => 129.3
        assertThat(
            Math.abs(position.getElevation() - 14.679074225415283) < TOLERANCE,
            is(true)); // usno => 14.7
        assertThat(
            Math.round(position.getShadowLength(1.8) * 100) / 100.0,
            is(6.87));

        Optional<Moment> timeOfShadow =
            shanghai.timeOfShadowBeforeNoon(1.8, 6.87).apply(PlainDate.of(2017, 12, 13));
        assertThat(
            timeOfShadow.isPresent() && Math.abs(timeOfShadow.get().getPosixTime() - moment.getPosixTime()) < 30,
            is(true));
    }

    @Test
    public void sunAtZenith() {
        SolarTime lakeNasser =
            SolarTime.ofLocation()
                .northernLatitude(23, 26, 7.2)
                .easternLongitude(25, 0, 0.0)
                .build();
        Moment moment = lakeNasser.transitAtNoon().apply(PlainDate.of(2018, 6, 21)); // summer solstice
        SunPosition position = SunPosition.at(moment, lakeNasser);

        assertThat(
            Math.abs(position.getRightAscension() - 90.01069057923154) < TOLERANCE,
            is(true));
        assertThat(
            Math.abs(position.getDeclination() - 23.435194643263145) < TOLERANCE,
            is(true));
        assertThat(
            Math.abs(position.getAzimuth() - 93.61015814829585) < TOLERANCE,
            is(true));
        assertThat(
            Math.abs(position.getElevation() - 89.99779837537335) < TOLERANCE,
            is(true)); // ~ zenith = 90°
        assertThat(
            position.getShadowLength(1.8) < TOLERANCE,
            is(true));

        Optional<Moment> timeOfShadow1 =
            lakeNasser.timeOfShadowBeforeNoon(1.8, 0.0).apply(PlainDate.of(2018, 6, 21));
        assertThat(
            timeOfShadow1.isPresent() && Math.abs(timeOfShadow1.get().getPosixTime() - moment.getPosixTime()) < 30,
            is(true));
        Optional<Moment> timeOfShadow2 =
            lakeNasser.timeOfShadowAfterNoon(1.8, 0.0).apply(PlainDate.of(2018, 6, 21));
        assertThat(
            timeOfShadow2.isPresent() && Math.abs(timeOfShadow2.get().getPosixTime() - moment.getPosixTime()) < 30,
            is(true));
    }

    @Test
    public void seasonFactory() {
        Moment m1 = PlainDate.of(2018, 1, 1).atTime(0, 0).at(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 18));
        assertThat(
            m1.toZonalTimestamp(ZonalOffset.UTC).getYear(),
            is(2017));
        assertThat(
            AstronomicalSeason.of(m1),
            is(AstronomicalSeason.WINTER_SOLSTICE)); // tests that the change of year does not affect the season
        assertThat(
            AstronomicalSeason.of(PlainDate.of(2018, 1, 1).atFirstMoment(ZonalOffset.UTC)),
            is(AstronomicalSeason.WINTER_SOLSTICE));
        assertThat(
            AstronomicalSeason.of(PlainDate.of(2018, 4, 1).atFirstMoment(ZonalOffset.UTC)),
            is(AstronomicalSeason.VERNAL_EQUINOX));
        assertThat(
            AstronomicalSeason.of(PlainDate.of(2018, 8, 1).atFirstMoment(ZonalOffset.UTC)),
            is(AstronomicalSeason.SUMMER_SOLSTICE));
        assertThat(
            AstronomicalSeason.of(PlainDate.of(2018, 11, 1).atFirstMoment(ZonalOffset.UTC)),
            is(AstronomicalSeason.AUTUMNAL_EQUINOX));
        assertThat(
            AstronomicalSeason.of(PlainDate.of(2018, 12, 31).atFirstMoment(ZonalOffset.UTC)),
            is(AstronomicalSeason.WINTER_SOLSTICE));
        Moment m2 = PlainDate.of(2018, 12, 31).atTime(23, 0).at(ZonalOffset.ofHours(OffsetSign.BEHIND_UTC, 18));
        assertThat(
            m2.toZonalTimestamp(ZonalOffset.UTC).getYear(),
            is(2019));
        assertThat(
            AstronomicalSeason.of(m2),
            is(AstronomicalSeason.WINTER_SOLSTICE)); // tests that the change of year does not affect the season
    }

    @Test(expected=IllegalArgumentException.class)
    public void seasonBeyondMinYear() {
        AstronomicalSeason.of(Moment.axis().getMinimum());
    }

    @Test(expected=IllegalArgumentException.class)
    public void seasonBeyondMaxYear() {
        AstronomicalSeason.of(Moment.axis().getMaximum());
    }

    @Test
    public void fairbanks() {
        TZID tzid = () -> "America/Anchorage";
        SolarTime fairbanks =
            SolarTime.ofLocation()
                .northernLatitude(64, 50, 0.0)
                .westernLongitude(147, 43, 0.0)
                .build();
        System.out.println(fairbanks.getLongitude());
        System.out.println(fairbanks.getLatitude());
        PlainDate date = PlainDate.of(2020, 6, 18);
        assertThat(
            date.minus(1, CalendarUnit.DAYS)
                .get(fairbanks.sunset()).get().with(Moment.PRECISION, TimeUnit.MINUTES).toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2020, 6, 18, 0, 46)));
        assertThat(
            date.get(fairbanks.sunrise()).get().with(Moment.PRECISION, TimeUnit.MINUTES).toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2020, 6, 18, 2, 58)));
        assertThat(
            date.get(fairbanks.sunset()).get().with(Moment.PRECISION, TimeUnit.MINUTES).toZonalTimestamp(tzid),
            is(PlainTimestamp.of(2020, 6, 19, 0, 46)));
    }

    @Test
    public void longyearbyen() {
        TZID tzid = () -> "Arctic/Longyearbyen";
        List<SolarTime> stList = new ArrayList<>();
        stList.add(svalbard(null));
        stList.add(svalbard(tzid));

        for (SolarTime svalbard : stList) {
            PlainDate date = PlainDate.of(2020, 8, 25); // civil date has TWO sunsets
            assertThat(
                date.minus(1, CalendarUnit.DAYS)
                    .get(svalbard.sunset()).get().with(Moment.PRECISION, TimeUnit.MINUTES).toZonalTimestamp(tzid),
                is(PlainTimestamp.of(2020, 8, 25, 0, 10)));
            assertThat(
                date.get(svalbard.sunrise()).get().with(Moment.PRECISION, TimeUnit.MINUTES).toZonalTimestamp(tzid),
                is(PlainTimestamp.of(2020, 8, 25, 1, 50)));
            assertThat(
                date.get(svalbard.sunset()).get().with(Moment.PRECISION, TimeUnit.MINUTES).toZonalTimestamp(tzid),
                is(PlainTimestamp.of(2020, 8, 25, 23, 45)));
        }
    }

    private static SolarTime svalbard(TZID tzid) {
        SolarTime.Builder builder =
            SolarTime.ofLocation()
            .easternLongitude(15, 38, 0.0)
            .northernLatitude(78, 13, 0.0);

        if (tzid != null) {
            builder = builder.inTimezone(tzid); // test shows no effect far away from date border line
        }

        return builder.build();
    }

}