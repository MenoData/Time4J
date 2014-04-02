package net.time4j;

import net.time4j.engine.ChronoException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.CalendarUnit.DAYS;
import static net.time4j.CalendarUnit.MONTHS;
import static net.time4j.CalendarUnit.YEARS;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class SpecialUnitTest {

    @Test
    public void atEndOfMonth() {
        assertThat(
            PlainDate.of(2011, 2, 28).plus(1, YEARS.atEndOfMonth()),
            is(PlainDate.of(2012, 2, 29)));
        assertThat(
            PlainDate.of(2011, 7, 1).plus(7, MONTHS.atEndOfMonth()),
            is(PlainDate.of(2012, 2, 29)));
        assertThat(
            PlainDate.of(2011, 6, 30).plus(7, DAYS.atEndOfMonth()),
            is(PlainDate.of(2011, 7, 31)));
    }

    @Test(expected=ChronoException.class)
    public void unlessInvalidAbort() {
        PlainDate.of(2014, 1, 29).plus(1, MONTHS.unlessInvalid());
    }

    @Test
    public void unlessInvalidOK() {
        assertThat(
            PlainDate.of(2012, 1, 29).plus(1, MONTHS.unlessInvalid()),
            is(PlainDate.of(2012, 2, 29)));
    }

    @Test
    public void nextValidDate() {
        assertThat(
            PlainDate.of(2012, 1, 29).plus(1, MONTHS.nextValidDate()),
            is(PlainDate.of(2012, 2, 29)));
        assertThat(
            PlainDate.of(2012, 1, 30).plus(1, MONTHS.nextValidDate()),
            is(PlainDate.of(2012, 3, 1)));
        assertThat(
            PlainDate.of(2012, 1, 31).plus(1, MONTHS.nextValidDate()),
            is(PlainDate.of(2012, 3, 1)));
    }

    @Test
    public void withCarryOver() {
        assertThat(
            PlainDate.of(2012, 1, 31).plus(1, MONTHS.withCarryOver()),
            is(PlainDate.of(2012, 3, 2)));
        assertThat(
            PlainDate.of(2013, 1, 28).plus(1, MONTHS.withCarryOver()),
            is(PlainDate.of(2013, 2, 28)));
        assertThat(
            PlainDate.of(2013, 1, 29).plus(1, MONTHS.withCarryOver()),
            is(PlainDate.of(2013, 3, 1)));
        assertThat(
            PlainDate.of(2013, 1, 30).plus(1, MONTHS.withCarryOver()),
            is(PlainDate.of(2013, 3, 2)));
        assertThat(
            PlainDate.of(2013, 1, 31).plus(1, MONTHS.withCarryOver()),
            is(PlainDate.of(2013, 3, 3)));
        assertThat(
            PlainDate.of(2012, 3, 31).plus(1, MONTHS.withCarryOver()),
            is(PlainDate.of(2012, 5, 1)));
    }

}