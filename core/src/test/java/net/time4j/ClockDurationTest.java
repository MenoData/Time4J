package net.time4j;

import java.text.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


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

}
