package net.time4j.calendar.astro;

import net.time4j.ClockUnit;
import net.time4j.Moment;
import net.time4j.PlainTimestamp;
import net.time4j.engine.CalendarDays;
import net.time4j.scale.TimeScale;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;

import static net.time4j.calendar.astro.AstronomicalSeason.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


@RunWith(JUnit4.class)
public class AstroTest {

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
        Moment utc =
            season.inYear(year);
        Moment ut =
            Moment.of(
                utc.getElapsedTime(TimeScale.UT) + 2 * 365 * 86400,
                utc.getNanosecond(TimeScale.UT),
                TimeScale.POSIX);
        PlainTimestamp tsp = ut.toZonalTimestamp(ZonalOffset.UTC);

        if (Math.abs(ClockUnit.MINUTES.between(tsp, expected)) > 0L) {
            fail("Expected: " + expected + ", but was: " + tsp);
        }
    }

    @Test
    public void countOfSeasons() {
        assertThat(AstronomicalSeason.values().length, is(4));
    }

    @Test
    public void solarTimeTeheran() {
        ZonalOffset teheran = ZonalOffset.atLongitude(OffsetSign.AHEAD_OF_UTC, 52, 30, 0.0); // +03:30
        Moment spring2024 = AstronomicalSeason.VERNAL_EQUINOX.inYear(2024);
        Moment spring2025 = AstronomicalSeason.VERNAL_EQUINOX.inYear(2025);
        PlainTimestamp tsp2024 = spring2024.get(SolarTime.at(teheran));
        PlainTimestamp tsp2025 = spring2025.get(SolarTime.at(teheran));
        System.out.println("apparent solar time of Teheran: " + spring2024.get(SolarTime.at(teheran)));
        System.out.println("apparent solar time of Teheran: " + spring2025.get(SolarTime.at(teheran)));
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
            is(1295));
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

}