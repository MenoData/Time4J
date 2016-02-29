package net.time4j;

import net.time4j.tz.OffsetSign;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class CompareZonalDateTimeTest {

    @Test
    public void comparison1() {
        ZonalOffset offsetBerlin = ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 1);
        ZonalOffset offsetNewYork = ZonalOffset.ofHours(OffsetSign.BEHIND_UTC, 5);
        ZonalDateTime zdt1 =
            PlainTimestamp.of(2016, 2, 29, 17, 45).inZonalView(Timezone.of(offsetBerlin));
        ZonalDateTime zdt2 =
            PlainTimestamp.of(2016, 2, 29, 20, 0).inZonalView(Timezone.of(offsetNewYork));
        assertThat(
            zdt1.compareByMoment(zdt2) < 0,
            is(true));
        assertThat(
            zdt1.compareByLocalTimestamp(zdt2) < 0,
            is(true));
    }

    @Test
    public void comparison2() {
        ZonalOffset offsetBerlin = ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 1);
        ZonalOffset offsetNewYork = ZonalOffset.ofHours(OffsetSign.BEHIND_UTC, 5);
        ZonalDateTime zdt1 =
            PlainTimestamp.of(2016, 2, 29, 17, 45).inZonalView(Timezone.of(offsetBerlin));
        ZonalDateTime zdt2 =
            PlainTimestamp.of(2016, 2, 29, 16, 0).inZonalView(Timezone.of(offsetNewYork));
        assertThat(
            zdt1.compareByMoment(zdt2) < 0,
            is(true));
        assertThat(
            zdt1.compareByLocalTimestamp(zdt2) > 0,
            is(true));
    }

    @Test
    public void comparison3() {
        ZonalOffset offsetBerlin = ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 1);
        ZonalOffset offsetNewYork = ZonalOffset.ofHours(OffsetSign.BEHIND_UTC, 5);
        ZonalDateTime zdt1 =
            PlainTimestamp.of(2016, 2, 29, 17, 45).inZonalView(Timezone.of(offsetBerlin));
        ZonalDateTime zdt2 =
            PlainTimestamp.of(2016, 2, 29, 9, 0).inZonalView(Timezone.of(offsetNewYork));
        assertThat(
            zdt1.compareByMoment(zdt2) > 0,
            is(true));
        assertThat(
            zdt1.compareByLocalTimestamp(zdt2) > 0,
            is(true));
    }

}