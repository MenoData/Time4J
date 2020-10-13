package net.time4j;

import java.text.ParseException;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class ClockDurationTest {

    @Test
    public void plusClockUnits() {
        Duration<ClockUnit> duration = Duration.ofClockUnits(2, 0, 3);
        assertThat(
            PlainTime.of(4, 26, 59, 987654321).plus(duration),
            is(PlainTime.of(6, 27, 2, 987654321)));
    }

    @Test
    public void untilInClockUnits() {
        PlainTime t1 = PlainTime.of(4, 26, 59, 987654321);
        PlainTime t2 = PlainTime.of(5, 26, 57, 987654320);
        assertThat(
            t2.until(t1, Duration.inClockUnits()),
            is(
                Duration.ofClockUnits(0, 59, 57)
                .plus(999999999, ClockUnit.NANOS)
                .inverse()));
    }

    @Test
    public void untilInSecondsNanos() {
        PlainTime t1 = PlainTime.of(4, 26, 59, 987654321);
        PlainTime t2 = PlainTime.of(5, 26, 57, 987654320);
        assertThat(
            t1.until(t2, Duration.in(ClockUnit.SECONDS, ClockUnit.NANOS)),
            is(
                Duration.of(3597, ClockUnit.SECONDS)
                .plus(999999999, ClockUnit.NANOS)));
    }

    @Test
    public void untilInHoursMinutes() {
        PlainTime t1 = PlainTime.of(4, 26, 59, 987654321);
        PlainTime t2 = PlainTime.of(5, 26, 57, 987654320);
        assertThat(
            t2.until(t1, Duration.in(ClockUnit.HOURS, ClockUnit.MINUTES)),
            is(Duration.of(-59, ClockUnit.MINUTES)));
    }

    @Test
    public void untilInHoursSeconds() {
        PlainTime t1 = PlainTime.of(4, 26, 59, 987654321);
        PlainTime t2 = PlainTime.of(5, 26, 57, 987654320);
        assertThat(
            t1.until(t2, Duration.in(ClockUnit.HOURS, ClockUnit.SECONDS)),
            is(Duration.of(3597, ClockUnit.SECONDS)));
    }

    @Test
    public void untilInMinutesSeconds() {
        PlainTime t1 = PlainTime.of(4, 26, 59, 987654321);
        PlainTime t2 = PlainTime.of(5, 26, 57, 987654320);
        assertThat(
            t1.until(t2, Duration.in(ClockUnit.MINUTES, ClockUnit.SECONDS)),
            is(Duration.ofClockUnits(0, 59, 57)));
    }

    @Test
    public void untilInSeconds() {
        PlainTime t1 = PlainTime.of(4, 26, 59, 987654321);
        PlainTime t2 = PlainTime.of(5, 26, 57, 987654320);
        assertThat(t2.until(t1, ClockUnit.SECONDS), is(-3597L));
        assertThat(t1.until(t2, ClockUnit.SECONDS), is(3597L));
    }

    @Test
    public void normalize() {
        PlainTime t1 = PlainTime.of(4, 26, 59, 988654321);
        PlainTime t2 = PlainTime.of(5, 26, 57, 987654320);
        Duration<ClockUnit> timeSpan =
            Duration.ofClockUnits(0, 59, 57).plus(998999999, ClockUnit.NANOS);
        assertThat(
            t1.until(t1.plus(timeSpan), Duration.inClockUnits()),
            is(timeSpan));
        assertThat(
            t1.plus(timeSpan),
            is(t2));
    }

    @Test
    public void convertToHours() throws ParseException {
        Duration<ClockUnit> duration = Duration.parseClockPeriod("PT121M3612,123456789S");
        assertThat(ClockUnit.HOURS.convert(duration), is(3L));
    }

    @Test
    public void convertToMinutes() throws ParseException {
        Duration<ClockUnit> duration = Duration.parseClockPeriod("PT21M12.667S");
        assertThat(ClockUnit.MINUTES.convert(duration), is(21L));
    }

    @Test
    public void convertToSeconds() throws ParseException {
        Duration<ClockUnit> duration = Duration.parseClockPeriod("PT21M12.667S");
        assertThat(ClockUnit.SECONDS.convert(duration), is(1272L));
    }

    @Test
    public void convertToMillis() throws ParseException {
        Duration<ClockUnit> duration = Duration.parseClockPeriod("PT21M12.667S");
        assertThat(ClockUnit.MILLIS.convert(duration), is(1272667L));
    }

    @Test
    public void comparatorBase() throws ParseException {
        Duration<ClockUnit> d1 = Duration.parseClockPeriod("PT21M12.667S");
        Duration<ClockUnit> d2 = Duration.parseClockPeriod("PT21M12.100S");
        assertThat(Duration.comparator(PlainTime.MIN).compare(d1, d2) > 0, is(true));
        assertThat(Duration.comparator(PlainTime.MIN).compare(d2, d1) < 0, is(true));
    }

    @Test
    public void comparatorOnClock() throws ParseException {
        Duration<ClockUnit> d1 = Duration.parseClockPeriod("PT21M12.667S");
        Duration<ClockUnit> d2 = Duration.parseClockPeriod("PT21M12.100S");
        assertThat(Duration.comparatorOnClock().compare(d1, d2) > 0, is(true));

        d1 = Duration.parseClockPeriod("PT21M61.667S");
        d2 = Duration.parseClockPeriod("PT22M1.666S");
        assertThat(Duration.comparatorOnClock().compare(d1, d2) > 0, is(true));

        d1 = Duration.parseClockPeriod("PT21M61.667S");
        d2 = Duration.parseClockPeriod("PT22M2.100S");
        assertThat(Duration.comparatorOnClock().compare(d1, d2) < 0, is(true));

        d1 = Duration.parseClockPeriod("-PT21M61.667S");
        d2 = Duration.parseClockPeriod("PT22M0.100S");
        assertThat(Duration.comparatorOnClock().compare(d1, d2) < 0, is(true));

        d1 = Duration.parseClockPeriod("PT21M61.667S");
        d2 = Duration.parseClockPeriod("-PT22M2.100S");
        assertThat(Duration.comparatorOnClock().compare(d1, d2) > 0, is(true));

        d1 = Duration.parseClockPeriod("-PT23H");
        d2 = Duration.parseClockPeriod("-PT22H");
        assertThat(Duration.comparatorOnClock().compare(d1, d2) < 0, is(true));

        d1 = Duration.parseClockPeriod("-PT23H");
        d2 = Duration.parseClockPeriod("-PT22H60M");
        assertThat(Duration.comparatorOnClock().compare(d1, d2) == 0, is(true));

        d1 = Duration.parseClockPeriod("PT0.667S");
        d2 = Duration.parseClockPeriod("PT0.100S");
        assertThat(Duration.comparatorOnClock().compare(d1, d2) > 0, is(true));

        d1 = Duration.parseClockPeriod("-PT0.667S");
        d2 = Duration.parseClockPeriod("PT0.100S");
        assertThat(Duration.comparatorOnClock().compare(d1, d2) < 0, is(true));

        d1 = Duration.parseClockPeriod("PT0.667S");
        d2 = Duration.parseClockPeriod("-PT0.700S");
        assertThat(Duration.comparatorOnClock().compare(d1, d2) > 0, is(true));

        d1 = Duration.parseClockPeriod("-PT0.667S");
        d2 = Duration.parseClockPeriod("-PT0.100S");
        assertThat(Duration.comparatorOnClock().compare(d1, d2) < 0, is(true));
    }

    @Test
    public void streamMax() throws ParseException {
        Duration<ClockUnit> d1 = Duration.parseClockPeriod("PT22M2.666S");
        Duration<ClockUnit> d2 = Duration.parseClockPeriod("-PT1000M");
        Duration<ClockUnit> d3 = Duration.parseClockPeriod("PT21M62.667S");
        Duration<ClockUnit> d4 = Duration.parseClockPeriod("PT22M2.667S");
        Stream<Duration<ClockUnit>> s = Stream.of(d1, d2, d3, d4);
        assertThat(s.max(Duration.comparatorOnClock()).get(), is(d3));

        Stream<Duration<ClockUnit>> e = Stream.empty();
        assertThat(e.max(Duration.comparatorOnClock()).isPresent(), is(false));
    }

}
