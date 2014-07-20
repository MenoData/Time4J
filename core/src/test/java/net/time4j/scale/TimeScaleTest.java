package net.time4j.scale;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.SI;
import net.time4j.tz.ZonalOffset;

import java.math.BigDecimal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class TimeScaleTest {

    @Test
    public void transformUTC() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1980, 1, 6),
                PlainTime.of(0, 0, 0, 123456789)
            ).atTimezone(ZonalOffset.UTC);
        assertThat(
            utc.transform(TimeScale.UTC),
            is(new BigDecimal("252892809.123456789")));
        assertThat(
            Moment.of(252892809, 123456789, TimeScale.UTC),
            is(utc));
    }

    @Test
    public void transformGPS() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1980, 1, 6),
                PlainTime.of(0, 0, 1, 123456789)
            ).atTimezone(ZonalOffset.UTC);
        assertThat(
            utc.transform(TimeScale.GPS),
            is(new BigDecimal("1.123456789")));
        assertThat(
            Moment.of(1, 123456789, TimeScale.GPS),
            is(utc));
    }

    @Test
    public void transformTAI() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1980, 1, 5),
                PlainTime.of(23, 59, 50, 123456789)
            ).atTimezone(ZonalOffset.UTC); // 10 secs before GPS epoch
        assertThat(
            utc.transform(TimeScale.TAI),
            is(new BigDecimal("252892809.123456789")));
        assertThat(
            Moment.of(252892809, 123456789, TimeScale.TAI),
            is(utc));
    }

    @Test
    public void transformPOSIX() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1980, 1, 6),
                PlainTime.of(0, 0, 0, 123456789)
            ).atTimezone(ZonalOffset.UTC);
        assertThat(
            utc.transform(TimeScale.POSIX),
            is(new BigDecimal("315964800.123456789")));
        assertThat(
            Moment.of(315964800L, 123456789, TimeScale.POSIX),
            is(utc));
    }

    @Test
    public void getElapsedTimeUTC() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1980, 1, 6),
                PlainTime.of(0, 0, 0, 123456789)
            ).atTimezone(ZonalOffset.UTC);
        assertThat(
            utc.getElapsedTime(TimeScale.UTC),
            is(315964800 + 9 - 2 * 365 * 86400L));
    }

    @Test
    public void getElapsedTimeTAI() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1980, 1, 5),
                PlainTime.of(23, 59, 50, 123456789)
            ).atTimezone(ZonalOffset.UTC); // 10 secs before GPS epoch
        assertThat(
            utc.getElapsedTime(TimeScale.TAI),
            is(315964800 + 9 - 2 * 365 * 86400L));
    }

    @Test
    public void getElapsedTimeGPS() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1980, 1, 6),
                PlainTime.of(0, 0, 1, 123456789)
            ).atTimezone(ZonalOffset.UTC);
        assertThat(
            utc.getElapsedTime(TimeScale.GPS),
            is(1L));
    }

    @Test
    public void getElapsedTimePOSIX() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1980, 1, 6),
                PlainTime.of(0, 0, 0, 123456789)
            ).atTimezone(ZonalOffset.UTC);
        assertThat(
            utc.getElapsedTime(TimeScale.POSIX),
            is(315964800L));
    }

    @Test
    public void getNanosecondUTC() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1980, 1, 6),
                PlainTime.of(0, 0, 0, 123456789)
            ).atTimezone(ZonalOffset.UTC);
        assertThat(
            utc.getNanosecond(TimeScale.UTC),
            is(123456789));
    }

    @Test
    public void getNanosecondGPS() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1980, 1, 6),
                PlainTime.of(0, 0, 0, 123456789)
            ).atTimezone(ZonalOffset.UTC);
        assertThat(
            utc.getNanosecond(TimeScale.GPS),
            is(123456789));
    }

    @Test
    public void getNanosecondTAI() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1980, 1, 6),
                PlainTime.of(0, 0, 0, 123456789)
            ).atTimezone(ZonalOffset.UTC);
        assertThat(
            utc.getNanosecond(TimeScale.TAI),
            is(123456789));
    }

    @Test
    public void getNanosecondPOSIX() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1980, 1, 6),
                PlainTime.of(0, 0, 0, 123456789)
            ).atTimezone(ZonalOffset.UTC);
        assertThat(
            utc.getNanosecond(TimeScale.POSIX),
            is(123456789));
    }

    @Test
    public void toStringUTC() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(2012, 6, 30),
                PlainTime.of(23, 59, 59, 123456789)
            ).atTimezone(ZonalOffset.UTC).plus(1, SI.SECONDS);
        assertThat(
            utc.toString(TimeScale.UTC),
            is("UTC-2012-06-30T23:59:60,123456789Z"));
    }

    @Test
    public void toStringGPS() {
        Moment utc1 =
            PlainTimestamp.of(
                PlainDate.of(2012, 6, 30),
                PlainTime.of(23, 59, 59, 123456789)
            ).atTimezone(ZonalOffset.UTC).plus(1, SI.SECONDS);
        assertThat(
            utc1.toString(TimeScale.GPS),
            is("GPS-2012-07-01T00:00:15,123456789Z"));
        Moment utc2 =
            PlainTimestamp.of(
                PlainDate.of(2012, 7, 1),
                PlainTime.of(0, 0, 0, 123456789)
            ).atTimezone(ZonalOffset.UTC);
        assertThat(
            utc2.toString(TimeScale.GPS),
            is("GPS-2012-07-01T00:00:16,123456789Z"));
    }

    @Test
    public void toStringTAI() {
        Moment utc1 =
            PlainTimestamp.of(
                PlainDate.of(2012, 6, 30),
                PlainTime.of(23, 59, 59, 123456789)
            ).atTimezone(ZonalOffset.UTC).plus(1, SI.SECONDS);
        assertThat(
            utc1.toString(TimeScale.TAI),
            is("TAI-2012-07-01T00:00:34,123456789Z"));
        Moment utc2 =
            PlainTimestamp.of(
                PlainDate.of(2012, 7, 1),
                PlainTime.of(0, 0, 0, 123456789)
            ).atTimezone(ZonalOffset.UTC);
        assertThat(
            utc2.toString(TimeScale.TAI),
            is("TAI-2012-07-01T00:00:35,123456789Z"));
    }

    @Test
    public void toStringPOSIX() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(2012, 6, 30),
                PlainTime.of(23, 59, 59, 123456789)
            ).atTimezone(ZonalOffset.UTC).plus(1, SI.SECONDS);
        assertThat(
            utc.toString(TimeScale.POSIX),
            is("POSIX-2012-06-30T23:59:59,123456789Z"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void getElapsedTimeGPSBefore1980() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1980, 1, 5),
                PlainTime.of(23, 59, 59, 123456789)
            ).atTimezone(ZonalOffset.UTC);
        utc.getElapsedTime(TimeScale.GPS);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getNanosecondGPSBefore1980() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1980, 1, 5),
                PlainTime.of(23, 59, 59, 123456789)
            ).atTimezone(ZonalOffset.UTC);
        utc.getNanosecond(TimeScale.GPS);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getElapsedTimeTAIBefore1972() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1971, 12, 31),
                PlainTime.of(23, 59, 59, 123456789)
            ).atTimezone(ZonalOffset.UTC);
        utc.getElapsedTime(TimeScale.TAI);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getNanosecondTAIBefore1972() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1971, 12, 31),
                PlainTime.of(23, 59, 59, 123456789)
            ).atTimezone(ZonalOffset.UTC);
        utc.getNanosecond(TimeScale.TAI);
    }

}