package net.time4j.calendar.astro;

import net.time4j.Moment;
import net.time4j.PlainTimestamp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class ZodiacTest {

    @Test
    public void next() {
        assertThat(
            Zodiac.ARIES.next(),
            is(Zodiac.TAURUS));
        assertThat(
            Zodiac.PISCES.next(),
            is(Zodiac.ARIES));
        assertThat(
            Zodiac.OPHIUCHUS.next(),
            is(Zodiac.SAGITTARIUS));
        assertThat(
            Zodiac.SCORPIUS.next(),
            is(Zodiac.OPHIUCHUS));
    }

    @Test
    public void precessionalShiftOfAries() {
        assertThat(
            SunPosition.atEntry(Zodiac.ARIES).inYear(0),
            is(PlainTimestamp.of(0, 3, 21, 16, 10).atUTC())); // ~ vernal equinox in year 0
        assertThat(
            SunPosition.atEntry(Zodiac.ARIES).inYear(2000),
            is(PlainTimestamp.of(2000, 4, 18, 13, 9).atUTC())); // precessional shift of vernal equinox
    }

    @Test
    public void precessionalShiftOfPisces() {
        assertThat(
            SunPosition.atEntry(Zodiac.PISCES).inYear(0),
            is(PlainTimestamp.of(0, 2, 12, 20, 48).atUTC()));
        assertThat(
            SunPosition.atEntry(Zodiac.PISCES).inYear(2000),
            is(PlainTimestamp.of(2000, 3, 11, 21, 4).atUTC())); // near vernal equinox in year 2000
    }

    @Test
    public void sunPositionTestZodiac() {
        assertThat(
            SunPosition.at(PlainTimestamp.of(0, 3, 21, 16, 10).atUTC()).test(Zodiac.PISCES),
            is(true));
        assertThat(
            SunPosition.at(PlainTimestamp.of(0, 3, 21, 16, 15).atUTC()).test(Zodiac.ARIES),
            is(true));
        assertThat(
            SunPosition.at(PlainTimestamp.of(2000, 3, 11, 21, 9).atUTC()).test(Zodiac.PISCES),
            is(true));
        assertThat(
            SunPosition.at(PlainTimestamp.of(2000, 4, 18, 13, 16).atUTC()).test(Zodiac.ARIES),
            is(true));
    }

}