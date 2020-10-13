package net.time4j.range;

import net.time4j.ClockUnit;
import net.time4j.Duration;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.Weekday;
import net.time4j.tz.olson.EUROPE;
import net.time4j.tz.olson.PACIFIC;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.time4j.Weekday.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JUnit4.class)
public class DayPartitionTest {

    @Test
    public void simpleCase1() {
        DayPartitionRule rule =
            new DayPartitionBuilder((date) -> !date.equals(PlainDate.of(2016, 9, 2)))
                .addExclusion(Collections.singleton(PlainDate.of(2016, 8, 27)))
                .addWeekdayRule(MONDAY, ClockInterval.between(PlainTime.of(9, 0), PlainTime.of(12, 30)))
                .addWeekdayRule(MONDAY, ClockInterval.between(PlainTime.of(14, 0), PlainTime.of(16, 0)))
                .addWeekdayRule(TUESDAY, ClockInterval.between(PlainTime.of(9, 0), PlainTime.of(12, 30)))
                .addWeekdayRule(TUESDAY, ClockInterval.between(PlainTime.of(14, 0), PlainTime.of(19, 0)))
                .addWeekdayRule(WEDNESDAY, ClockInterval.between(PlainTime.of(9, 0), PlainTime.of(12, 30)))
                .addWeekdayRule(THURSDAY, ClockInterval.between(PlainTime.of(9, 0), PlainTime.of(12, 30)))
                .addWeekdayRule(THURSDAY, ClockInterval.between(PlainTime.of(14, 0), PlainTime.of(19, 0)))
                .addWeekdayRule(FRIDAY, ClockInterval.between(PlainTime.of(9, 0), PlainTime.of(12, 30)))
                .addWeekdayRule(SATURDAY, ClockInterval.between(PlainTime.of(10, 0), PlainTime.of(12, 0)))
                .addSpecialRule(
                    PlainDate.of(2016, 9, 6),
                    ClockInterval.between(PlainTime.of(9, 15), PlainTime.of(12, 45)))
                .build();

        List<TimestampInterval> intervals =
            DateInterval.between(PlainDate.of(2016, 8, 25), PlainDate.of(2016, 9, 7))
                .streamPartitioned(rule)
                .parallel()
                .collect(Collectors.toList());

        List<ChronoInterval<PlainTimestamp>> expected = new ArrayList<>();
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 8, 25, 9, 0), PlainTimestamp.of(2016, 8, 25, 12, 30)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 8, 25, 14, 0), PlainTimestamp.of(2016, 8, 25, 19, 0)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 8, 26, 9, 0), PlainTimestamp.of(2016, 8, 26, 12, 30)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 8, 29, 9, 0), PlainTimestamp.of(2016, 8, 29, 12, 30)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 8, 29, 14, 0), PlainTimestamp.of(2016, 8, 29, 16, 0)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 8, 30, 9, 0), PlainTimestamp.of(2016, 8, 30, 12, 30)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 8, 30, 14, 0), PlainTimestamp.of(2016, 8, 30, 19, 0)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 8, 31, 9, 0), PlainTimestamp.of(2016, 8, 31, 12, 30)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 9, 1, 9, 0), PlainTimestamp.of(2016, 9, 1, 12, 30)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 9, 1, 14, 0), PlainTimestamp.of(2016, 9, 1, 19, 0)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 9, 3, 10, 0), PlainTimestamp.of(2016, 9, 3, 12, 0)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 9, 5, 9, 0), PlainTimestamp.of(2016, 9, 5, 12, 30)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 9, 5, 14, 0), PlainTimestamp.of(2016, 9, 5, 16, 0)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 9, 6, 9, 15), PlainTimestamp.of(2016, 9, 6, 12, 45)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 9, 7, 9, 0), PlainTimestamp.of(2016, 9, 7, 12, 30)));

        assertThat(intervals, is(expected));
        assertThat(rule.matches(PlainTimestamp.of(2016, 9, 7, 12, 15)), is(true));
        assertThat(rule.matches(PlainTimestamp.of(2016, 9, 7, 12, 30)), is(false));
    }

    @Test
    public void simpleCase2() {
        DayPartitionRule rule =
            new DayPartitionBuilder((date) -> !date.equals(PlainDate.of(2016, 9, 2)))
                .addExclusion(Collections.singleton(PlainDate.of(2016, 8, 27)))
                .addWeekdayRule(
                    SpanOfWeekdays.betweenMondayAndFriday(),
                    ClockInterval.between(PlainTime.of(9, 0), PlainTime.of(12, 30)))
                .addWeekdayRule(MONDAY, ClockInterval.between(PlainTime.of(14, 0), PlainTime.of(16, 0)))
                .addWeekdayRule(TUESDAY, ClockInterval.between(PlainTime.of(14, 0), PlainTime.of(19, 0)))
                .addWeekdayRule(THURSDAY, ClockInterval.between(PlainTime.of(14, 0), PlainTime.of(19, 0)))
                .addWeekdayRule(SATURDAY, ClockInterval.between(PlainTime.of(10, 0), PlainTime.of(12, 0)))
                .addSpecialRule(
                    PlainDate.of(2016, 9, 6),
                    ClockInterval.between(PlainTime.of(9, 15), PlainTime.of(12, 45)))
                .build();

        List<TimestampInterval> intervals =
            DateInterval.between(PlainDate.of(2016, 8, 25), PlainDate.of(2016, 9, 7))
                .streamPartitioned(rule)
                .parallel()
                .collect(Collectors.toList());

        List<ChronoInterval<PlainTimestamp>> expected = new ArrayList<>();
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 8, 25, 9, 0), PlainTimestamp.of(2016, 8, 25, 12, 30)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 8, 25, 14, 0), PlainTimestamp.of(2016, 8, 25, 19, 0)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 8, 26, 9, 0), PlainTimestamp.of(2016, 8, 26, 12, 30)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 8, 29, 9, 0), PlainTimestamp.of(2016, 8, 29, 12, 30)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 8, 29, 14, 0), PlainTimestamp.of(2016, 8, 29, 16, 0)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 8, 30, 9, 0), PlainTimestamp.of(2016, 8, 30, 12, 30)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 8, 30, 14, 0), PlainTimestamp.of(2016, 8, 30, 19, 0)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 8, 31, 9, 0), PlainTimestamp.of(2016, 8, 31, 12, 30)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 9, 1, 9, 0), PlainTimestamp.of(2016, 9, 1, 12, 30)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 9, 1, 14, 0), PlainTimestamp.of(2016, 9, 1, 19, 0)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 9, 3, 10, 0), PlainTimestamp.of(2016, 9, 3, 12, 0)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 9, 5, 9, 0), PlainTimestamp.of(2016, 9, 5, 12, 30)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 9, 5, 14, 0), PlainTimestamp.of(2016, 9, 5, 16, 0)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 9, 6, 9, 15), PlainTimestamp.of(2016, 9, 6, 12, 45)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 9, 7, 9, 0), PlainTimestamp.of(2016, 9, 7, 12, 30)));

        assertThat(intervals, is(expected));
        assertThat(rule.matches(PlainTimestamp.of(2016, 9, 7, 12, 15)), is(true));
        assertThat(rule.matches(PlainTimestamp.of(2016, 9, 7, 12, 30)), is(false));
    }

    @Test
    public void dailyRule() {
        DayPartitionRule rule =
            new DayPartitionBuilder()
                .addDailyRule(ClockInterval.between(PlainTime.of(9, 0), PlainTime.of(12, 30)))
                .build();

        List<TimestampInterval> intervals =
            DateInterval.between(PlainDate.of(2016, 8, 24), PlainDate.of(2016, 8, 31))
                .streamPartitioned(rule)
                .parallel()
                .collect(Collectors.toList());

        List<ChronoInterval<PlainTimestamp>> expected = new ArrayList<>();
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 8, 24, 9, 0), PlainTimestamp.of(2016, 8, 24, 12, 30)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 8, 25, 9, 0), PlainTimestamp.of(2016, 8, 25, 12, 30)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 8, 26, 9, 0), PlainTimestamp.of(2016, 8, 26, 12, 30)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 8, 27, 9, 0), PlainTimestamp.of(2016, 8, 27, 12, 30)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 8, 28, 9, 0), PlainTimestamp.of(2016, 8, 28, 12, 30)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 8, 29, 9, 0), PlainTimestamp.of(2016, 8, 29, 12, 30)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 8, 30, 9, 0), PlainTimestamp.of(2016, 8, 30, 12, 30)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2016, 8, 31, 9, 0), PlainTimestamp.of(2016, 8, 31, 12, 30)));

        assertThat(intervals, is(expected));
        assertThat(rule.matches(PlainTimestamp.of(2016, 8, 31, 12, 15)), is(true));
        assertThat(rule.matches(PlainTimestamp.of(2016, 8, 31, 12, 30)), is(false));
    }

    @Test
    public void euCaseWithFullIntervalInGap() {
        DayPartitionRule rule =
            new DayPartitionBuilder()
                .addWeekdayRule(MONDAY, ClockInterval.between(PlainTime.of(9, 0), PlainTime.of(12, 30)))
                .addWeekdayRule(SUNDAY, ClockInterval.between(PlainTime.of(2, 10), PlainTime.of(2, 20)))
                .addWeekdayRule(SUNDAY, ClockInterval.between(PlainTime.of(2, 30), PlainTime.of(3, 0)))
                .addWeekdayRule(SATURDAY, ClockInterval.between(PlainTime.of(10, 0), PlainTime.of(12, 0)))
                .build();

        List<MomentInterval> intervals =
            DateInterval.between(PlainDate.of(2016, 3, 26), PlainDate.of(2016, 3, 28))
                .streamPartitioned(rule, EUROPE.BERLIN)
                .collect(Collectors.toList());

        List<MomentInterval> expected = new ArrayList<>();
        expected.add(
            MomentInterval.between(
                PlainTimestamp.of(2016, 3, 26, 10, 0).inTimezone(EUROPE.BERLIN),
                PlainTimestamp.of(2016, 3, 26, 12, 0).inTimezone(EUROPE.BERLIN)));
        expected.add(
            MomentInterval.between(
                PlainTimestamp.of(2016, 3, 28, 9, 0).inTimezone(EUROPE.BERLIN),
                PlainTimestamp.of(2016, 3, 28, 12, 30).inTimezone(EUROPE.BERLIN)));

        assertThat(intervals, is(expected));
    }

    @Test
    public void euCaseWithPartialIntervalInGap() {
        DayPartitionRule rule =
            new DayPartitionBuilder()
                .addWeekdayRule(MONDAY, ClockInterval.between(PlainTime.of(9, 0), PlainTime.of(12, 30)))
                .addWeekdayRule(SUNDAY, ClockInterval.between(PlainTime.of(1, 10), PlainTime.of(2, 20)))
                .addWeekdayRule(SUNDAY, ClockInterval.between(PlainTime.of(2, 30), PlainTime.of(3, 15)))
                .addWeekdayRule(SATURDAY, ClockInterval.between(PlainTime.of(10, 0), PlainTime.of(12, 0)))
                .build();

        List<MomentInterval> intervals =
            DateInterval.between(PlainDate.of(2016, 3, 26), PlainDate.of(2016, 3, 28))
                .streamPartitioned(rule, EUROPE.BERLIN)
                .collect(Collectors.toList());

        List<MomentInterval> expected = new ArrayList<>();
        expected.add(
            MomentInterval.between(
                PlainTimestamp.of(2016, 3, 26, 10, 0).inTimezone(EUROPE.BERLIN),
                PlainTimestamp.of(2016, 3, 26, 12, 0).inTimezone(EUROPE.BERLIN)));
        expected.add(
            MomentInterval.between(
                PlainTimestamp.of(2016, 3, 27, 1, 10).inTimezone(EUROPE.BERLIN),
                PlainTimestamp.of(2016, 3, 27, 3, 0).inTimezone(EUROPE.BERLIN)));
        expected.add(
            MomentInterval.between(
                PlainTimestamp.of(2016, 3, 27, 3, 0).inTimezone(EUROPE.BERLIN),
                PlainTimestamp.of(2016, 3, 27, 3, 15).inTimezone(EUROPE.BERLIN)));
        expected.add(
            MomentInterval.between(
                PlainTimestamp.of(2016, 3, 28, 9, 0).inTimezone(EUROPE.BERLIN),
                PlainTimestamp.of(2016, 3, 28, 12, 30).inTimezone(EUROPE.BERLIN)));

        assertThat(intervals, is(expected));
    }

    @Test
    public void samoaCase() {
        DayPartitionRule rule =
            new DayPartitionBuilder()
                .addWeekdayRule(THURSDAY, ClockInterval.between(PlainTime.of(9, 0), PlainTime.of(12, 30)))
                .addWeekdayRule(THURSDAY, ClockInterval.between(PlainTime.of(14, 0), PlainTime.of(19, 0)))
                .addWeekdayRule(FRIDAY, ClockInterval.between(PlainTime.of(9, 0), PlainTime.of(12, 30)))
                .addWeekdayRule(SATURDAY, ClockInterval.between(PlainTime.of(10, 0), PlainTime.of(12, 0)))
                .build();

        List<MomentInterval> intervals =
            DateInterval.between(PlainDate.of(2011, 12, 29), PlainDate.of(2011, 12, 31))
                .streamPartitioned(rule, PACIFIC.APIA)
                .collect(Collectors.toList());

        List<MomentInterval> expected = new ArrayList<>();
        expected.add(
            MomentInterval.between(
                PlainTimestamp.of(2011, 12, 29, 9, 0).inTimezone(PACIFIC.APIA),
                PlainTimestamp.of(2011, 12, 29, 12, 30).inTimezone(PACIFIC.APIA)));
        expected.add(
            MomentInterval.between(
                PlainTimestamp.of(2011, 12, 29, 14, 0).inTimezone(PACIFIC.APIA),
                PlainTimestamp.of(2011, 12, 29, 19, 0).inTimezone(PACIFIC.APIA)));
        expected.add(
            MomentInterval.between(
                PlainTimestamp.of(2011, 12, 31, 10, 0).inTimezone(PACIFIC.APIA),
                PlainTimestamp.of(2011, 12, 31, 12, 0).inTimezone(PACIFIC.APIA)));

        assertThat(intervals, is(expected));
    }

    @Test
    public void timestampPartitionsOnSingleDay() {
        DayPartitionRule rule =
            new DayPartitionBuilder()
                .addWeekdayRule(THURSDAY, ClockInterval.between(PlainTime.of(9, 0), PlainTime.of(12, 30)))
                .addWeekdayRule(THURSDAY, ClockInterval.between(PlainTime.of(14, 0), PlainTime.of(19, 0)))
                .addWeekdayRule(FRIDAY, ClockInterval.between(PlainTime.of(9, 0), PlainTime.of(12, 30)))
                .addWeekdayRule(SATURDAY, ClockInterval.between(PlainTime.of(10, 0), PlainTime.of(12, 0)))
                .build();

        List<TimestampInterval> intervals =
            TimestampInterval.between(PlainTimestamp.of(2018, 10, 25, 9, 15), PlainTimestamp.of(2018, 10, 25, 15, 30))
                .streamPartitioned(rule)
                .collect(Collectors.toList());

        List<ChronoInterval<PlainTimestamp>> expected = new ArrayList<>();
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2018, 10, 25, 9, 15), PlainTimestamp.of(2018, 10, 25, 12, 30)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2018, 10, 25, 14, 0), PlainTimestamp.of(2018, 10, 25, 15, 30)));

        assertThat(intervals, is(expected));
    }

    @Test
    public void timestampPartitionsOnTwoDays() {
        DayPartitionRule rule =
            new DayPartitionBuilder()
                .addWeekdayRule(THURSDAY, ClockInterval.between(PlainTime.of(9, 0), PlainTime.of(12, 30)))
                .addWeekdayRule(THURSDAY, ClockInterval.between(PlainTime.of(14, 0), PlainTime.of(19, 0)))
                .addWeekdayRule(FRIDAY, ClockInterval.between(PlainTime.of(9, 0), PlainTime.of(12, 30)))
                .addWeekdayRule(SATURDAY, ClockInterval.between(PlainTime.of(10, 0), PlainTime.of(12, 0)))
                .build();

        List<TimestampInterval> intervals =
            TimestampInterval.between(PlainTimestamp.of(2018, 10, 25, 9, 15), PlainTimestamp.of(2018, 10, 26, 15, 30))
                .streamPartitioned(rule)
                .collect(Collectors.toList());

        List<ChronoInterval<PlainTimestamp>> expected = new ArrayList<>();
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2018, 10, 25, 9, 15), PlainTimestamp.of(2018, 10, 25, 12, 30)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2018, 10, 25, 14, 0), PlainTimestamp.of(2018, 10, 25, 19, 0)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2018, 10, 26, 9, 0), PlainTimestamp.of(2018, 10, 26, 12, 30)));

        assertThat(intervals, is(expected));
    }

    @Test
    public void timestampPartitionsOnThreeDays() {
        DayPartitionRule rule =
            new DayPartitionBuilder()
                .addWeekdayRule(THURSDAY, ClockInterval.between(PlainTime.of(9, 0), PlainTime.of(12, 30)))
                .addWeekdayRule(THURSDAY, ClockInterval.between(PlainTime.of(14, 0), PlainTime.of(19, 0)))
                .addWeekdayRule(FRIDAY, ClockInterval.between(PlainTime.of(9, 0), PlainTime.of(12, 30)))
                .addWeekdayRule(SATURDAY, ClockInterval.between(PlainTime.of(10, 0), PlainTime.of(12, 0)))
                .build();

        List<TimestampInterval> intervals =
            TimestampInterval.between(PlainTimestamp.of(2018, 10, 25, 9, 15), PlainTimestamp.of(2018, 10, 27, 11, 30))
                .streamPartitioned(rule)
                .collect(Collectors.toList());

        List<ChronoInterval<PlainTimestamp>> expected = new ArrayList<>();
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2018, 10, 25, 9, 15), PlainTimestamp.of(2018, 10, 25, 12, 30)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2018, 10, 25, 14, 0), PlainTimestamp.of(2018, 10, 25, 19, 0)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2018, 10, 26, 9, 0), PlainTimestamp.of(2018, 10, 26, 12, 30)));
        expected.add(
            TimestampInterval.between(PlainTimestamp.of(2018, 10, 27, 10, 0), PlainTimestamp.of(2018, 10, 27, 11, 30)));

        assertThat(intervals, is(expected));
    }

    @Test
    public void overlapOfPartitions() {
        DayPartitionRule rule =
            new DayPartitionBuilder()
                .addWeekdayRule(
                    Weekday.MONDAY,
                    ClockInterval.between(PlainTime.of(8, 0), PlainTime.of(10, 0)))
                .addWeekdayRule(
                    Weekday.TUESDAY,
                    ClockInterval.between(PlainTime.of(10, 0), PlainTime.of(15, 0)))
                .build();
        Map<Integer, TimestampInterval> events = new HashMap<>();
        events.put(
            1,
            TimestampInterval.between(
                PlainTimestamp.of(2018, 1, 1, 8, 0),
                PlainTimestamp.of(2018, 1, 1, 9, 0)));
        events.put(
            2,
            TimestampInterval.between(
                PlainTimestamp.of(2018, 1, 1, 10, 0),
                PlainTimestamp.of(2018, 1, 1, 12, 0)));
        events.put(
            3,
            TimestampInterval.between(
                PlainTimestamp.of(2018, 1, 2, 10, 0),
                PlainTimestamp.of(2018, 1, 2, 12, 0)));
        events.forEach(
            (id, interval) -> System.out.println(
                "Event: " + id + " => "
                    + Duration.formatter(ClockUnit.class, "#h:mm").format(
                        interval
                            .streamPartitioned(rule)
                            .map(i -> i.getDuration(ClockUnit.HOURS, ClockUnit.MINUTES))
                            .collect(Duration.summingUp())
                            .with(Duration.STD_CLOCK_PERIOD)
                    )
                )
        );
        // output:
        //        Event: 1 => 1:00
        //        Event: 2 => 0:00
        //        Event: 3 => 2:00
    }

}
