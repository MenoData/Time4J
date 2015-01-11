package net.time4j.tz.model;

import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.Weekday;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class DaylightSavingRuleTest {

    @Test
    public void getFixedDate() {
        assertThat(
            DaylightSavingRule.ofFixedDay(
                Month.MARCH,
                17,
                PlainTime.of(24),
                OffsetIndicator.UTC_TIME,
                3600
            ).getDate(2014),
            is(PlainDate.of(2014, 3, 17))); // T24:00 wird hier ignoriert!
    }

    @Test
    public void getLastWeekdayDate() {
        assertThat(
            DaylightSavingRule.ofLastWeekday(
                Month.MARCH,
                Weekday.SUNDAY,
                PlainTime.of(2, 0),
                OffsetIndicator.UTC_TIME,
                3600
            ).getDate(2014),
            is(PlainDate.of(2014, 3, 30)));
    }

    @Test
    public void getWeekdayAfterDate() {
        assertThat(
            DaylightSavingRule.ofWeekdayAfterDate(
                Month.MARCH,
                8,
                Weekday.SUNDAY,
                PlainTime.of(2, 0),
                OffsetIndicator.UTC_TIME,
                3600
            ).getDate(2014),
            is(PlainDate.of(2014, 3, 9)));
    }

    @Test
    public void getWeekdayBeforeDate() {
        assertThat(
            DaylightSavingRule.ofWeekdayBeforeDate(
                Month.MARCH,
                8,
                Weekday.SUNDAY,
                PlainTime.of(2, 0),
                OffsetIndicator.UTC_TIME,
                3600
            ).getDate(2014),
            is(PlainDate.of(2014, 3, 2)));
    }

     @Test
    public void getTimeOfDay() {
        assertThat(
            DaylightSavingRule.ofFixedDay(
                Month.MARCH,
                17,
                PlainTime.of(24),
                OffsetIndicator.UTC_TIME,
                3600
            ).getTimeOfDay(),
            is(PlainTime.midnightAtEndOfDay()));
    }

   @Test
    public void getIndicator() {
        assertThat(
            DaylightSavingRule.ofWeekdayAfterDate(
                Month.MARCH,
                8,
                Weekday.SUNDAY,
                PlainTime.of(2, 0),
                OffsetIndicator.STANDARD_TIME,
                3600
            ).getIndicator(),
            is(OffsetIndicator.STANDARD_TIME));
    }

    @Test
    public void getSavings() {
        assertThat(
            DaylightSavingRule.ofWeekdayAfterDate(
                Month.MARCH,
                8,
                Weekday.SUNDAY,
                PlainTime.of(2, 0),
                OffsetIndicator.STANDARD_TIME,
                3600
            ).getSavings(),
            is(3600));
    }

}