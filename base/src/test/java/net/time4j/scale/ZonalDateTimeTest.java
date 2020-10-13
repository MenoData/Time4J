package net.time4j.scale;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.SI;
import net.time4j.ZonalDateTime;
import net.time4j.format.TemporalFormatter;
import net.time4j.format.platform.SimpleFormatter;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


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

    @Test
    public void printZDT() {
        Moment moment = Moment.of(1278028825, TimeScale.UTC);
        Timezone tz = Timezone.of("Asia/Tokyo");
        TemporalFormatter<Moment> formatter =
            SimpleFormatter.ofMomentPattern("yyyy-MM-dd'T'HH:mmZ", Locale.ROOT, tz.getID());
        System.out.println("ZonalDateTime-Formatter-INFO: " + formatter.getClass().getName());
        assertThat(
            moment.inZonalView(tz.getID()).print(formatter),
            is("2012-07-01T09:00+0900"));
    }

    @Test
    public void parseZDT() {
        Moment moment = Moment.of(1278028825, TimeScale.UTC);
        Timezone tz = Timezone.of("Asia/Tokyo");
        TemporalFormatter<Moment> formatter =
            SimpleFormatter.ofMomentPattern("yyyy-MM-dd'T'HH:mmZ", Locale.ROOT, tz.getID());
        assertThat(
            ZonalDateTime.parse("2012-07-01T09:00+0900", formatter),
            is(moment.inZonalView(tz.getID())));
    }

    @Test
    public void toStringOutput() {
        Moment moment = Moment.of(1278028825, TimeScale.UTC);
        Timezone tz = Timezone.of("Asia/Tokyo");
        ZonalDateTime zdt1 = moment.inZonalView(tz.getID());
        assertThat(
            zdt1.toString(),
            is("2012-07-01T09:00:00+09:00[Asia/Tokyo]"));
        ZonalDateTime zdt2 = moment.minus(1, SI.SECONDS).inZonalView(tz.getID());
        assertThat(
            zdt2.toString(),
            is("2012-07-01T08:59:60+09:00[Asia/Tokyo]"));
    }

}