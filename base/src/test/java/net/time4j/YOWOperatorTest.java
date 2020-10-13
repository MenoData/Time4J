package net.time4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.PlainDate.YEAR_OF_WEEKDATE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class YOWOperatorTest {

    @Test
    public void minimized() {
         // [1997-W01-2])
        assertThat(
            PlainDate.of(1996, 12, 31).with(YEAR_OF_WEEKDATE.minimized()),
            is(PlainDate.of(-999999999, 1, 2)));
    }

    @Test
    public void maximized() {
         // [1997-W01-2])
        assertThat(
            PlainDate.of(1996, 12, 31).with(YEAR_OF_WEEKDATE.maximized()),
            is(PlainDate.of(999999999, 1, 5)));
    }

    @Test
    public void decremented() {
         // [1997-W01-2])
        assertThat(
            PlainDate.of(1996, 12, 31).with(YEAR_OF_WEEKDATE.decremented()),
            is(PlainDate.of(1996, 1, 2))); // 1996-W01-2
    }

    @Test
    public void incrementedNormal() {
         // [1997-W01-2])
        assertThat(
            PlainDate.of(1996, 12, 31).with(YEAR_OF_WEEKDATE.incremented()),
            is(PlainDate.of(1997, 12, 30))); // 1998-W01-2
    }

    @Test
    public void incrementedKW53() {
        assertThat(
            PlainDate.of(2004, 53, Weekday.MONDAY)
                .with(YEAR_OF_WEEKDATE.incremented()),
            is(PlainDate.of(2005, 12, 26))); // 2005-W52-1
    }

    @Test
    public void atFloor() {
        assertThat(
            PlainDate.of(2004, 5, 1).with(YEAR_OF_WEEKDATE.atFloor()),
            is(PlainDate.of(2004, 1, Weekday.MONDAY)));
    }

    @Test
    public void atCeiling() {
        assertThat(
            PlainDate.of(2004, 5, 1).with(YEAR_OF_WEEKDATE.atCeiling()),
            is(PlainDate.of(2004, 53, Weekday.SUNDAY)));
    }

    @Test
    public void minimizedOnTimestamp() {
         // [1997-W01-2])
        PlainTimestamp ts =
            PlainDate.of(1996, 12, 31).atStartOfDay();
        assertThat(
            ts.with(YEAR_OF_WEEKDATE.minimized()),
            is(PlainDate.of(-999999999, 1, 2).atStartOfDay()));
    }

    @Test
    public void maximizedOnTimestamp() {
         // [1997-W01-2])
        PlainTimestamp ts =
            PlainDate.of(1996, 12, 31).atStartOfDay();
        assertThat(
            ts.with(YEAR_OF_WEEKDATE.maximized()),
            is(PlainDate.of(999999999, 1, 5).atStartOfDay()));
    }

    @Test
    public void decrementedOnTimestamp() {
         // [1997-W01-2])
        PlainTimestamp ts =
            PlainDate.of(1996, 12, 31).atStartOfDay();
        assertThat(
            ts.with(YEAR_OF_WEEKDATE.decremented()),
            is(PlainDate.of(1996, 1, 2).atStartOfDay())); // 1996-W01-2
    }

    @Test
    public void incrementedNormalOnTimestamp() {
         // [1997-W01-2])
        PlainTimestamp ts =
            PlainDate.of(1996, 12, 31).atStartOfDay();
        assertThat(
            ts.with(YEAR_OF_WEEKDATE.incremented()),
            is(PlainDate.of(1997, 12, 30).atStartOfDay())); // 1998-W01-2
    }

    @Test
    public void incrementedKW53OnTimestamp() {
        PlainTimestamp ts =
            PlainDate.of(2004, 53, Weekday.MONDAY).atStartOfDay();
        assertThat(
            ts.with(YEAR_OF_WEEKDATE.incremented()),
            is(PlainDate.of(2005, 12, 26).atStartOfDay())); // 2005-W52-1
    }

    @Test
    public void atFloorOnTimestamp() {
        PlainTimestamp ts =
            PlainTimestamp.of(2004, 5, 1, 12, 45);
        assertThat(
            ts.with(YEAR_OF_WEEKDATE.atFloor()),
            is(PlainDate.of(2004, 1, Weekday.MONDAY).atStartOfDay()));
    }

    @Test
    public void atCeilingOnTimestamp() {
        PlainTimestamp ts =
            PlainTimestamp.of(2004, 5, 1, 12, 45);
        PlainTimestamp expected =
            PlainDate
                .of(2004, 53, Weekday.SUNDAY)
                .at(PlainTime.of(23, 59, 59, 999999999));
        assertThat(
            ts.with(YEAR_OF_WEEKDATE.atCeiling()),
            is(expected));
    }

}