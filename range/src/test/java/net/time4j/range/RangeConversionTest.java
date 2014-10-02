package net.time4j.range;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;
import net.time4j.tz.ZonalOffset;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class RangeConversionTest {

    @Test
    public void dateIntervalToFullDays() {
        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.of(2014, 5, 14);
        PlainTimestamp t1 = PlainTimestamp.of(2014, 2, 27, 0, 0);
        PlainTimestamp t2 = PlainTimestamp.of(2014, 5, 15, 0, 0);
        assertThat(
            DateInterval.between(start, end).toFullDays(),
            is(TimestampInterval.between(t1, t2)));
    }

    @Test
    public void timestampIntervalToMomentInterval() {
        PlainTimestamp t1 = PlainTimestamp.of(2014, 2, 27, 0, 0);
        PlainTimestamp t2 = PlainTimestamp.of(2014, 5, 14, 23, 59, 59);
        Moment m1 = t1.atUTC();
        Moment m2 = t2.atUTC();
        assertThat(
            TimestampInterval.between(t1, t2).atUTC(),
            is(MomentInterval.between(m1, m2)));
    }

    @Test
    public void momentIntervalToTimestampInterval() {
        PlainTimestamp t1 = PlainTimestamp.of(2014, 2, 27, 0, 0);
        PlainTimestamp t2 = PlainTimestamp.of(2014, 5, 14, 23, 59, 59);
        Moment m1 = t1.atUTC();
        Moment m2 = t2.atUTC();
        assertThat(
            MomentInterval.between(m1, m2).toZonalInterval(ZonalOffset.UTC),
            is(TimestampInterval.between(t1, t2)));
    }

}