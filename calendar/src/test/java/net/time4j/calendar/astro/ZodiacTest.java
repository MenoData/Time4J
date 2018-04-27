package net.time4j.calendar.astro;

import net.time4j.PlainTimestamp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class ZodiacTest {

    @Test
    public void precessionalShiftOfAries() {
        assertThat(
            SunPosition.atEntry(Zodiac.ARIES).inYear(0),
            is(PlainTimestamp.of(0, 3, 20, 12, 14, 59).atUTC())); // ~ vernal equinox in year 0
        assertThat(
            SunPosition.atEntry(Zodiac.ARIES).inYear(2000),
            is(PlainTimestamp.of(2000, 3, 4, 16, 54, 47).atUTC())); // precessional shift of vernal equinox
    }

    @Test
    public void precessionalShiftOfPisces() {
        assertThat(
            SunPosition.atEntry(Zodiac.PISCES).inYear(0),
            is(PlainTimestamp.of(0, 4, 1, 9, 56, 4).atUTC()));
        assertThat(
            SunPosition.atEntry(Zodiac.PISCES).inYear(2000),
            is(PlainTimestamp.of(2000, 3, 24, 15, 11, 9).atUTC())); // near vernal equinox in year 2000
    }

}