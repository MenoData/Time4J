package net.time4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class TimeComparisonTest {

    @Test
    public void isBefore() {
        assertThat(
            PlainTime.of(19, 45, 30, 1).isBefore(PlainTime.of(19, 45, 30, 2)),
            is(true));
        assertThat(
            PlainTime.of(19, 45, 30).isBefore(PlainTime.of(20)),
            is(true));
        assertThat(
            PlainTime.of(19, 45, 30).isBefore(PlainTime.of(19, 45, 30)),
            is(false));
        assertThat(
            PlainTime.of(20).isBefore(PlainTime.of(19, 45, 30)),
            is(false));
        assertThat(
            PlainTime.of(23, 59, 59, 999999999).isBefore(PlainTime.of(24)),
            is(true));
    }

    @Test
    public void isSimultaneous() {
        assertThat(
            PlainTime.of(19, 45, 30).isSimultaneous(PlainTime.of(19, 45, 30)),
            is(true));
        assertThat(
            PlainTime.of(0).isSimultaneous(PlainTime.of(24)),
            is(false));
    }

    @Test
    public void isAfter() {
        assertThat(
            PlainTime.of(19, 45, 30, 1).isAfter(PlainTime.of(19, 45, 30, 2)),
            is(false));
        assertThat(
            PlainTime.of(19, 45, 30).isAfter(PlainTime.of(20)),
            is(false));
        assertThat(
            PlainTime.of(19, 45, 30).isAfter(PlainTime.of(19, 45, 30)),
            is(false));
        assertThat(
            PlainTime.of(20).isAfter(PlainTime.of(19, 45, 30)),
            is(true));
        assertThat(
            PlainTime.of(23, 59, 59, 999999999).isAfter(PlainTime.of(24)),
            is(false));
    }

    @Test
    public void compareTo() {
        assertThat(
            PlainTime.of(23, 59, 59, 999999999)
                .compareTo(PlainTime.of(23, 59, 59, 999999998)) > 0,
            is(true));
        assertThat(
            PlainTime.of(23, 59, 59, 999999998)
                .compareTo(PlainTime.of(23, 59, 59, 999999999)) > 0,
            is(false));
    }

}