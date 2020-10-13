package net.time4j.calendar.astro;

import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.tz.TZID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class TwilightTest {

    @Test
    public void blueHour() {
        assertThat(
            Twilight.BLUE_HOUR.getAngle(),
            is(4.0));
    }

    @Test
    public void civil() {
        assertThat(
            Twilight.CIVIL.getAngle(),
            is(6.0));
    }

    @Test
    public void nautical() {
        assertThat(
            Twilight.NAUTICAL.getAngle(),
            is(12.0));
    }

    @Test
    public void astronomical() {
        assertThat(
            Twilight.ASTRONOMICAL.getAngle(),
            is(18.0));
    }

    @Test
    public void hamburgWilliams() {
        PlainDate date = PlainDate.of(2017, 9, 6);
        SolarTime hamburg = SolarTime.ofLocation(53 + 33.0 / 60.0, 10.0, 0, StdSolarCalculator.SIMPLE);
        TZID tzid = () -> "Europe/Berlin";
        assertThat(
            date.get(hamburg.sunrise(Twilight.ASTRONOMICAL)).get().toZonalTimestamp(tzid).toTime(),
            is(PlainTime.of(4, 26)));
        assertThat(
            date.get(hamburg.sunrise(Twilight.NAUTICAL)).get().toZonalTimestamp(tzid).toTime(),
            is(PlainTime.of(5, 15)));
        assertThat(
            date.get(hamburg.sunrise(Twilight.CIVIL)).get().toZonalTimestamp(tzid).toTime(),
            is(PlainTime.of(6, 0)));
        assertThat(
            date.get(hamburg.sunset(Twilight.ASTRONOMICAL)).get().toZonalTimestamp(tzid).toTime(),
            is(PlainTime.of(22, 7)));
        assertThat(
            date.get(hamburg.sunset(Twilight.NAUTICAL)).get().toZonalTimestamp(tzid).toTime(),
            is(PlainTime.of(21, 18)));
        assertThat(
            date.get(hamburg.sunset(Twilight.CIVIL)).get().toZonalTimestamp(tzid).toTime(),
            is(PlainTime.of(20, 34)));
    }

    @Test
    public void hamburgNOAA() {
        PlainDate date = PlainDate.of(2017, 9, 6);
        SolarTime hamburg = SolarTime.ofLocation(53 + 33.0 / 60.0, 10.0, 0, StdSolarCalculator.NOAA);
        TZID tzid = () -> "Europe/Berlin";
        assertThat(
            date.get(hamburg.sunrise(Twilight.ASTRONOMICAL)).get().toZonalTimestamp(tzid).toTime(),
            is(PlainTime.of(4, 28, 21)));
        assertThat(
            date.get(hamburg.sunrise(Twilight.NAUTICAL)).get().toZonalTimestamp(tzid).toTime(),
            is(PlainTime.of(5, 17, 16)));
        assertThat(
            date.get(hamburg.sunrise(Twilight.CIVIL)).get().toZonalTimestamp(tzid).toTime(),
            is(PlainTime.of(6, 1, 35)));
        assertThat(
            date.get(hamburg.sunset(Twilight.ASTRONOMICAL)).get().toZonalTimestamp(tzid).toTime(),
            is(PlainTime.of(22, 6, 8)));
        assertThat(
            date.get(hamburg.sunset(Twilight.NAUTICAL)).get().toZonalTimestamp(tzid).toTime(),
            is(PlainTime.of(21, 17, 43)));
        assertThat(
            date.get(hamburg.sunset(Twilight.CIVIL)).get().toZonalTimestamp(tzid).toTime(),
            is(PlainTime.of(20, 33, 41)));
    }

}