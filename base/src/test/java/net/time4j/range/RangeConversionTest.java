package net.time4j.range;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.tz.GapResolver;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.OverlapResolver;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import net.time4j.tz.olson.AMERICA;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


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
    public void dateIntervalInTimezone() {
        PlainDate date = PlainDate.of(2016, 10, 16);
        Moment t1 = date.atFirstMoment(AMERICA.SAO_PAULO);
        Moment t2 = date.at(PlainTime.midnightAtEndOfDay()).inTimezone(AMERICA.SAO_PAULO);
        assertThat(
            DateInterval.atomic(date).inTimezone(AMERICA.SAO_PAULO),
            is(MomentInterval.between(t1, t2)));
        assertThat(MomentInterval.between(t1, t2).getSimpleDuration().getSeconds(), is(23 * 3600L));
    }

    @Test
    public void timestampIntervalToMomentIntervalUTC() {
        PlainTimestamp t1 = PlainTimestamp.of(2014, 2, 27, 0, 0);
        PlainTimestamp t2 = PlainTimestamp.of(2014, 5, 14, 23, 59, 59);
        Moment m1 = t1.atUTC();
        Moment m2 = t2.atUTC();
        assertThat(
            TimestampInterval.between(t1, t2).atUTC(),
            is(MomentInterval.between(m1, m2)));
    }

    @Test
    public void timestampIntervalToMomentIntervalSystem() {
        PlainTimestamp t1 = PlainTimestamp.of(2016, 3, 27, 2, 30, 0);
        PlainTimestamp t2 = PlainTimestamp.of(2016, 3, 29, 23, 59, 59);
        Timezone systz = Timezone.ofSystem().with(GapResolver.NEXT_VALID_TIME.and(OverlapResolver.EARLIER_OFFSET));
        Moment m1 = t1.in(systz);
        Moment m2 = t2.in(systz);
        assertThat(
            TimestampInterval.between(t1, t2).inStdTimezone(),
            is(MomentInterval.between(m1, m2)));
    }

    @Test
    public void timestampIntervalToMomentIntervalBrazil() {
        PlainDate date = PlainDate.of(2016, 10, 16);
        PlainTimestamp t1 = date.atTime(0, 30); // falls mid in gap
        PlainTimestamp t2 = date.atTime(2, 0);
        Moment m1 = date.atTime(1, 0).at(ZonalOffset.ofHours(OffsetSign.BEHIND_UTC, 2));
        Moment m2 = date.atTime(2, 0).at(ZonalOffset.ofHours(OffsetSign.BEHIND_UTC, 2));
        assertThat(
            TimestampInterval.between(t1, t2).inTimezone(AMERICA.SAO_PAULO),
            is(MomentInterval.between(m1, m2)));
        assertThat(MomentInterval.between(m1, m2).getSimpleDuration().getSeconds(), is(3600L));
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