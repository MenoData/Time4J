package net.time4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class DateComparisonTest {

    @Test
    public void isBefore() {
        assertThat(
            PlainDate.of(2012, 2, 29).isBefore(PlainDate.of(2012, 3, 1)),
            is(true));
        assertThat(
            PlainDate.of(2012, 2, 29).isBefore(PlainDate.of(2012, 2, 29)),
            is(false));
        assertThat(
            PlainDate.of(2012, 2, 29).isBefore(PlainDate.of(2012, 2, 28)),
            is(false));
        assertThat(
            PlainDate.of(2012, 2, 29).isBefore(PlainDate.of(2011, 5, 31)),
            is(false));
        assertThat(
            PlainDate.of(2012, 2, 29).isBefore(PlainDate.of(2013, 1, 1)),
            is(true));
    }

    @Test
    public void isSimultaneous() {
        assertThat(
            PlainDate.of(2012, 2, 29).isSimultaneous(PlainDate.of(2012, 3, 1)),
            is(false));
        assertThat(
            PlainDate.of(2012, 2, 29).isSimultaneous(PlainDate.of(2012, 2, 29)),
            is(true));
    }

    @Test
    public void isAfter() {
        assertThat(
            PlainDate.of(2012, 2, 29).isAfter(PlainDate.of(2012, 3, 1)),
            is(false));
        assertThat(
            PlainDate.of(2012, 2, 29).isAfter(PlainDate.of(2012, 2, 29)),
            is(false));
        assertThat(
            PlainDate.of(2012, 2, 29).isAfter(PlainDate.of(2012, 2, 28)),
            is(true));
        assertThat(
            PlainDate.of(2012, 2, 29).isAfter(PlainDate.of(2011, 5, 31)),
            is(true));
        assertThat(
            PlainDate.of(2012, 2, 29).isAfter(PlainDate.of(2013, 1, 1)),
            is(false));
    }

    @Test
    public void compareTo() {
        assertThat(
            PlainDate.of(2012, 2, 29).compareTo(PlainDate.of(2012, 3, 1)) > 0,
            is(false));
        assertThat(
            PlainDate.of(2012, 2, 29).compareTo(PlainDate.of(2012, 2, 29)) > 0,
            is(false));
        assertThat(
            PlainDate.of(2012, 2, 29).compareTo(PlainDate.of(2012, 2, 28)) > 0,
            is(true));
        assertThat(
            PlainDate.of(2012, 2, 29).compareTo(PlainDate.of(2011, 5, 31)) > 0,
            is(true));
        assertThat(
            PlainDate.of(2012, 2, 29).compareTo(PlainDate.of(2013, 1, 1)) > 0,
            is(false));
    }

}