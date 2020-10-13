package net.time4j.tz.model;

import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.Weekday;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class DaylightSavingRuleTest { // more tests see SerializationTest

    @Test
    public void getFixedDate() {
        assertThat(
            GregorianTimezoneRule.ofFixedDay(
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
            GregorianTimezoneRule.ofLastWeekday(
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
            GregorianTimezoneRule.ofWeekdayAfterDate(
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
            GregorianTimezoneRule.ofWeekdayBeforeDate(
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
            GregorianTimezoneRule.ofFixedDay(
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
            GregorianTimezoneRule.ofWeekdayAfterDate(
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
            GregorianTimezoneRule.ofWeekdayAfterDate(
                Month.MARCH,
                8,
                Weekday.SUNDAY,
                PlainTime.of(2, 0),
                OffsetIndicator.STANDARD_TIME,
                3600
            ).getSavings(),
            is(3600));
    }

    @Test // see Asia/Tokyo in TZDB-version 2018f
    public void switchAtTimeT25() {
        DaylightSavingRule rule =
            GregorianTimezoneRule.ofWeekdayAfterDate(
                Month.SEPTEMBER,
                8,
                Weekday.SATURDAY,
                25 * 3600,
                OffsetIndicator.UTC_TIME,
                0);
        assertThat(rule.getDayOverflow(), is(1L));
        assertThat(rule.getTimeOfDay(), is(PlainTime.of(1)));
        assertThat(rule.getDate(1950), is(PlainDate.of(1950, 9, 10)));
        assertThat(rule.getIndicator(), is(OffsetIndicator.UTC_TIME));
        assertThat(rule.getSavings(), is(0));
    }

}