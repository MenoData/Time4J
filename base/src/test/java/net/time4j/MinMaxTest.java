package net.time4j;

import net.time4j.base.GregorianMath;
import net.time4j.tz.ZonalOffset;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class MinMaxTest {

    @Test
    public void minimumDate() {
        assertThat(
            PlainDate.axis().getMinimum(),
            is(PlainDate.of(GregorianMath.MIN_YEAR, 1)));
    }

    @Test
    public void minimumTime() {
        assertThat(
            PlainTime.axis().getMinimum(),
            is(PlainTime.midnightAtStartOfDay()));
    }

    @Test
    public void minimumTimestamp() {
        assertThat(
            PlainTimestamp.axis().getMinimum(),
            is(PlainTimestamp.of(GregorianMath.MIN_YEAR, 1, 1, 0, 0)));
    }

    @Test
    public void minimumMoment() {
        assertThat(
            Moment.axis().getMinimum(),
            is(PlainTimestamp.axis().getMinimum().atUTC()));
    }

    @Test
    public void maximumDate() {
        assertThat(
            PlainDate.axis().getMaximum(),
            is(PlainDate.of(GregorianMath.MAX_YEAR, 12, 31)));
    }

    @Test
    public void maximumTime() {
        assertThat(
            PlainTime.axis().getMaximum(),
            is(PlainTime.midnightAtEndOfDay()));
    }

    @Test
    public void maximumTimestamp() {
        assertThat(
            PlainTimestamp.axis().getMaximum(),
            is(
                PlainTimestamp.of(GregorianMath.MAX_YEAR, 12, 31, 23, 59, 59)
                    .with(PlainTime.NANO_OF_SECOND.maximized())));
    }

    @Test
    public void maximumMoment() {
        assertThat(
            Moment.axis().getMaximum(),
            is(PlainTimestamp.axis().getMaximum().atUTC()));
    }

    @Test
    public void minimumMomentToTsp() {
        ZonalOffset utc = ZonalOffset.UTC;
        assertThat(
            Moment.axis().getMinimum().toZonalTimestamp(utc),
            is(PlainTimestamp.axis().getMinimum()));
    }

    @Test
    public void maximumMomentToTsp() {
        ZonalOffset utc = ZonalOffset.UTC;
        assertThat(
            Moment.axis().getMaximum().toZonalTimestamp(utc),
            is(PlainTimestamp.axis().getMaximum()));
    }

}