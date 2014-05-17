package net.time4j.scale;

import net.time4j.ClockUnit;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.SI;
import net.time4j.tz.ZonalOffset;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class MomentCreationTest {

    @Test
    public void leapsecond_2012_06_30() {
        assertThat(
            Moment.of(1278028824, TimeScale.UTC),
            is(
                PlainTimestamp.of(
                    PlainDate.of(2012, 6, 30),
                    PlainTime.of(23, 59, 59)
                ).inTimezone(ZonalOffset.UTC).plus(1, SI.SECONDS)));
    }

    @Test
    public void leapsecond_2012_06_30_fraction() {
        assertThat(
            Moment.of(1278028824, 123456789, TimeScale.UTC),
            is(
                PlainTimestamp.of(
                    PlainDate.of(2012, 6, 30),
                    PlainTime.of(23, 59, 59, 123456789)
                ).inTimezone(ZonalOffset.UTC).plus(1, SI.SECONDS)));
    }

    @Test
    public void midnightUTC() {
        assertThat(
            Moment.of(1277942424, TimeScale.UTC),
            is(PlainDate.of(2012, 6, 30).atStartOfDay(ZonalOffset.UTC)));
    }

    @Test
    public void midnightUTC_fraction_1() {
        assertThat(
            Moment.of(1277942424, 123456789, TimeScale.UTC),
            is(
                PlainDate.of(2012, 6, 30).atStartOfDay()
                .plus(123456789, ClockUnit.NANOS).atOffset(ZonalOffset.UTC)));
    }

    @Test
    public void midnightUTC_fraction_2() {
        assertThat(
            Moment.of(1277942424, 999999999, TimeScale.UTC),
            is(
                PlainDate.of(2012, 6, 30).atStartOfDay()
                .plus(999999999, ClockUnit.NANOS).atUTC()));
    }

    @Test
    public void midnightPOSIX() {
        assertThat(
            Moment.of(1277942400 + 2 * 365 * 86400, TimeScale.POSIX),
            is(PlainDate.of(2012, 6, 30).atStartOfDay().atUTC()));
    }

}