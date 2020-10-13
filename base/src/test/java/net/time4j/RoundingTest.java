package net.time4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.PlainTime.DIGITAL_HOUR_OF_DAY;
import static net.time4j.PlainTime.MINUTE_OF_HOUR;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class RoundingTest {

    @Test
    public void roundedUp() {
        assertThat(
            PlainTime.of(18, 38).with(MINUTE_OF_HOUR.roundedUp(15)),
            is(PlainTime.of(18, 45)));
        assertThat(
            PlainTime.of(18, 46).with(MINUTE_OF_HOUR.roundedUp(15)),
            is(PlainTime.of(19)));
        assertThat(
            PlainTime.of(22, 30).with(DIGITAL_HOUR_OF_DAY.roundedUp(3)),
            is(PlainTime.of(0, 30)));
        assertThat(
            PlainTime.of(18, 30).with(DIGITAL_HOUR_OF_DAY.roundedUp(3)),
            is(PlainTime.of(18, 30)));
        assertThat(
            PlainTime.of(19, 30).with(DIGITAL_HOUR_OF_DAY.roundedUp(3)),
            is(PlainTime.of(21, 30)));
    }

    @Test
    public void roundedHalf() {
        assertThat(
            PlainTime.of(18, 38).with(MINUTE_OF_HOUR.roundedHalf(15)),
            is(PlainTime.of(18, 45)));
        assertThat(
            PlainTime.of(18, 37).with(MINUTE_OF_HOUR.roundedHalf(15)),
            is(PlainTime.of(18, 30)));

        PlainDate d1 = PlainDate.of(2014, 3, 21);
        assertThat(
            d1.with(PlainDate.DAY_OF_MONTH.roundedHalf(10)),
            is(PlainDate.of(2014, 3, 20)));
        PlainDate d2 = PlainDate.of(2014, 3, 31);
        assertThat(
            d2.with(PlainDate.DAY_OF_MONTH.roundedHalf(4)),
            is(PlainDate.of(2014, 4, 1)));
        PlainDate d3 = PlainDate.of(2014, 3, 29);
        assertThat(
            d3.with(PlainDate.DAY_OF_MONTH.roundedHalf(4)),
            is(PlainDate.of(2014, 3, 28)));
    }

    @Test
    public void roundedDown() {
        assertThat(
            PlainTime.of(18, 38).with(MINUTE_OF_HOUR.roundedDown(15)),
            is(PlainTime.of(18, 30)));
        assertThat(
            PlainTime.of(18, 8).with(MINUTE_OF_HOUR.roundedDown(15)),
            is(PlainTime.of(18)));
        assertThat(
            PlainTime.of(2, 30).with(DIGITAL_HOUR_OF_DAY.roundedDown(3)),
            is(PlainTime.of(0, 30)));
        assertThat(
            PlainTime.of(18, 30).with(DIGITAL_HOUR_OF_DAY.roundedDown(3)),
            is(PlainTime.of(18, 30)));
        assertThat(
            PlainTime.of(17, 30).with(DIGITAL_HOUR_OF_DAY.roundedDown(3)),
            is(PlainTime.of(15, 30)));
    }
}