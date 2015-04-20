package net.time4j.scale;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class ZonalDateTimeTest {

    @Test
    public void toMoment() {
        Moment moment = Moment.of(1278028824, TimeScale.UTC);
        Timezone tz = Timezone.of("Asia/Tokyo");
        assertThat(
            moment.inZonalView(tz.getID()).toMoment(),
            is(moment));
    }

    @Test
    public void toTimestamp() {
        Moment moment = Moment.of(1278028824, TimeScale.UTC);
        Timezone tz = Timezone.of("Asia/Tokyo");
        assertThat(
            moment.inZonalView(tz.getID()).toTimestamp(),
            is(PlainTimestamp.of(2012, 7, 1, 8, 59, 59)));
    }

    @Test
    public void hasTimezone() {
        Moment moment = Moment.of(1278028824, TimeScale.UTC);
        Timezone tz = Timezone.of("Asia/Tokyo");
        assertThat(
            moment.inZonalView(tz.getID()).hasTimezone(),
            is(true));
    }

    @Test
    public void getTimezone() {
        Moment moment = Moment.of(1278028824, TimeScale.UTC);
        Timezone tz = Timezone.of("Asia/Tokyo");
        assertThat(
            moment.inZonalView(tz.getID()).getTimezone(),
            is(tz.getID()));
    }

    @Test
    public void isLeapsecond() {
        Moment moment = Moment.of(1278028824, TimeScale.UTC);
        Timezone tz = Timezone.of("Asia/Tokyo");
        assertThat(
            moment.inZonalView(tz.getID()).isLeapSecond(),
            is(true));
    }

    @Test
    public void getSecondOfMinute() {
        Moment moment = Moment.of(1278028824, TimeScale.UTC);
        Timezone tz = Timezone.of("Asia/Tokyo");
        assertThat(
            moment.inZonalView(tz.getID()).get(PlainTime.SECOND_OF_MINUTE),
            is(60));
    }

    @Test
    public void getDayOfMonth() {
        Moment moment = Moment.of(1278028824, TimeScale.UTC);
        Timezone tz = Timezone.of("Asia/Tokyo");
        assertThat(
            moment.inZonalView(tz.getID()).get(PlainDate.DAY_OF_MONTH),
            is(1));
    }

    @Test
    public void getPosixTime() {
        long utc = 1278028824L;
        Moment moment = Moment.of(utc, TimeScale.UTC);
        Timezone tz = Timezone.of("Asia/Tokyo");
        assertThat(
            moment.inZonalView(tz.getID()).getPosixTime(),
            is(utc + 2 * 365 * 86400 - 25));
    }

    @Test
    public void getMaxSecondOfMinute() {
        Moment moment =
            PlainTimestamp.of(2012, 6, 30, 23, 59, 0).atUTC();
        assertThat(
            moment.inZonalView(ZonalOffset.UTC)
                .getMaximum(PlainTime.SECOND_OF_MINUTE),
            is(60));
    }

}