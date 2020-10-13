package net.time4j.range;

import net.time4j.PlainDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class ValueIntervalTest {

    @Test
    public void getValue() {
        DateInterval interval = DateInterval.between(PlainDate.of(2017, 1, 5), PlainDate.of(2017, 3, 26));
        ValueInterval<PlainDate, DateInterval, String> vi = interval.withValue("xyz");
        assertThat(vi.getValue(), is("xyz"));
    }

    @Test
    public void withValue() {
        DateInterval interval = DateInterval.between(PlainDate.of(2017, 1, 5), PlainDate.of(2017, 3, 26));
        ValueInterval<PlainDate, DateInterval, String> vi = interval.withValue("xyz");
        assertThat(vi.withValue("abc").getBoundaries(), is(interval));
        assertThat(vi.withValue("abc").getValue(), is("abc"));
    }

    @Test
    public void getBoundaries() {
        DateInterval interval = DateInterval.between(PlainDate.of(2017, 1, 5), PlainDate.of(2017, 3, 26));
        ValueInterval<PlainDate, DateInterval, String> vi = interval.withValue("xyz");
        assertThat(vi.getBoundaries(), is(interval));
    }

    @Test(expected=NullPointerException.class)
    public void nullValue() {
        DateInterval interval = DateInterval.between(PlainDate.of(2017, 1, 5), PlainDate.of(2017, 3, 26));
        interval.withValue(null);
    }

    @Test
    public void delegateMethods() {
        DateInterval interval = DateInterval.between(PlainDate.of(2017, 1, 5), PlainDate.of(2017, 3, 26));
        ValueInterval<PlainDate, DateInterval, String> vi = interval.withValue("xyz");
        assertThat(vi.getStart(), is(interval.getStart()));
        assertThat(vi.getEnd(), is(interval.getEnd()));
        assertThat(vi.isEmpty(), is(interval.isEmpty()));
        assertThat(vi.contains(interval), is(true));
        assertThat(vi.contains(interval.getStartAsCalendarDate()), is(true));
        assertThat(vi.isAfter(interval.getStartAsCalendarDate()), is(false));
        assertThat(vi.isBefore(interval.getStartAsCalendarDate()), is(false));
        assertThat(vi.abuts(DateInterval.since(PlainDate.of(2017, 3, 27))), is(true));
    }

    @Test
    public void monthWithValue() {
        assertThat(CalendarMonth.of(2017, 3).withValue(123).toString(), is("2017-03=>123")); // uses autoboxing
    }

}