package net.time4j;

import net.time4j.format.Attributes;

import java.math.BigDecimal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class TimeCreationTest {

    @Test
    public void ofHourMinuteSecondNano() {
        PlainTime time = PlainTime.of(13, 24, 59, 123456789);
        assertThat(time.getHour(), is(13));
        assertThat(time.getMinute(), is(24));
        assertThat(time.getSecond(), is(59));
        assertThat(time.getNanosecond(), is(123456789));
    }

    @Test
    public void ofHourMinuteSecond() {
        PlainTime time = PlainTime.of(13, 24, 59);
        assertThat(time.getHour(), is(13));
        assertThat(time.getMinute(), is(24));
        assertThat(time.getSecond(), is(59));
        assertThat(time.getNanosecond(), is(0));
    }

    @Test
    public void ofHourMinute() {
        PlainTime time = PlainTime.of(13, 24);
        assertThat(time.getHour(), is(13));
        assertThat(time.getMinute(), is(24));
        assertThat(time.getSecond(), is(0));
        assertThat(time.getNanosecond(), is(0));
    }

    @Test
    public void ofHour() {
        PlainTime time = PlainTime.of(13);
        assertThat(time.getHour(), is(13));
        assertThat(time.getMinute(), is(0));
        assertThat(time.getSecond(), is(0));
        assertThat(time.getNanosecond(), is(0));
    }

    @Test
    public void ofBigDecimalZero() {
        PlainTime time = PlainTime.of(BigDecimal.ZERO);
        assertThat(time, is(PlainTime.of(0)));
    }

    @Test
    public void ofBigDecimalOne() {
        PlainTime time = PlainTime.of(new BigDecimal("24"));
        assertThat(time, is(PlainTime.of(24)));
    }

    @Test
    public void ofBigDecimalHalf() {
        PlainTime time = PlainTime.of(new BigDecimal("12.5"));
        assertThat(time, is(PlainTime.of(12, 30)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofHourMinuteEx() {
        PlainTime.of(24, 1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void merge() {
        PlainTime.axis().createFrom(
            Moment.UNIX_EPOCH, Attributes.empty(), false, false);
    }

    @Test
    public void mergeLax() {
        assertThat(
            PlainTime.axis().createFrom(
                Moment.UNIX_EPOCH,
                Attributes.empty(),
                true,
                false),
        is(PlainTime.midnightAtStartOfDay()));
    }

}