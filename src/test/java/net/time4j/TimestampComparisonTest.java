package net.time4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class TimestampComparisonTest {

    @Test
    public void isBefore() {
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 23, 59, 59)
                .isBefore(PlainTimestamp.of(2012, 3, 1, 0, 0)),
            is(true));
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 23, 59, 58)
                .isBefore(PlainTimestamp.of(2012, 2, 29, 23, 59, 59)),
            is(true));
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 23, 59, 59)
                .isBefore(PlainTimestamp.of(2012, 2, 29, 23, 59, 59)),
            is(false));
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 0, 0)
                .isBefore(PlainTimestamp.of(2012, 2, 28, 23, 59, 59)),
            is(false));
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 23, 59, 59)
                .isBefore(PlainTimestamp.of(2011, 5, 31, 23, 59, 59)),
            is(false));
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 23, 59, 59)
                .isBefore(PlainTimestamp.of(2013, 1, 1, 0, 0)),
            is(true));
    }

    @Test
    public void isSimultaneous() {
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 23, 59, 59)
                .isSimultaneous(PlainTimestamp.of(2012, 3, 1, 0, 0, 0)),
            is(false));
        PlainDate date = PlainDate.of(2012, 2, 29);
        PlainTime time = PlainTime.of(23, 59, 59);
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 23, 59, 59)
                .isSimultaneous(PlainTimestamp.of(date, time)),
            is(true));
        assertThat(
            PlainTimestamp.of(2012, 3, 1, 0, 0)
                .isSimultaneous(
                    PlainTimestamp.of(date, PlainTime.midnightAtEndOfDay())),
            is(true));
    }

    @Test
    public void isAfter() {
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 23, 59, 59)
                .isAfter(PlainTimestamp.of(2012, 3, 1, 0, 0)),
            is(false));
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 23, 59, 59)
                .isAfter(PlainTimestamp.of(2012, 2, 29, 23, 59, 59)),
            is(false));
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 23, 59, 59)
                .isAfter(PlainTimestamp.of(2012, 2, 29, 23, 59, 58)),
            is(true));
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 23, 59, 59)
                .isAfter(PlainTimestamp.of(2012, 2, 29, 23, 58, 59)),
            is(true));
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 23, 59, 59)
                .isAfter(PlainTimestamp.of(2012, 2, 29, 22, 59, 59)),
            is(true));
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 0, 0)
                .isAfter(PlainTimestamp.of(2012, 2, 28, 23, 59, 59)),
            is(true));
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 0, 0)
                .isAfter(PlainTimestamp.of(2011, 5, 31, 23, 59, 59)),
            is(true));
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 23, 59, 59)
                .isAfter(PlainTimestamp.of(2013, 1, 1, 0, 0)),
            is(false));
    }

    @Test
    public void compareTo() {
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 23, 59)
                .compareTo(PlainTimestamp.of(2012, 3, 1, 0, 0)) > 0,
            is(false));
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 0, 0)
                .compareTo(PlainTimestamp.of(2012, 2, 29, 0, 0)) == 0,
            is(true));
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 0, 0)
                .compareTo(PlainTimestamp.of(2012, 2, 28, 23, 59, 59)) > 0,
            is(true));
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 0, 0)
                .compareTo(PlainTimestamp.of(2011, 5, 31, 23, 59)) > 0,
            is(true));
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 23, 59)
                .compareTo(PlainTimestamp.of(2013, 1, 1, 0, 0)) > 0,
            is(false));
    }

    @Test
    public void compareToInNanos() {
        PlainDate date = PlainDate.of(2014, 4, 21);
        PlainTime t1 = PlainTime.of(19, 45, 30, 123456788);
        PlainTime t2 = PlainTime.of(19, 45, 30, 123456789);
        PlainTime t3 = PlainTime.of(19, 45, 30, 123456790);
        assertThat(
            PlainTimestamp.of(date, t1).isBefore(PlainTimestamp.of(date, t2)),
            is(true));
        assertThat(
            PlainTimestamp.of(date, t1).isBefore(PlainTimestamp.of(date, t3)),
            is(true));
        assertThat(
            PlainTimestamp.of(date, t2).isBefore(PlainTimestamp.of(date, t3)),
            is(true));
        assertThat(
            PlainTimestamp.of(date, t2)
                .isSimultaneous(PlainTimestamp.of(date, t2)),
            is(true));
    }

}