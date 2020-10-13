package net.time4j.tz.model;

import net.time4j.Moment;
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.SystemClock;
import net.time4j.Weekday;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.ZonalOffset;
import net.time4j.tz.ZonalTransition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class RulesLikeBerlin1947Test {

    private static final ZonalOffset CET =
        ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 1);
    private static final ZonalOffset CEST =
        ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 2);
    private static final ZonalOffset CEMT =
        ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 3);
    private static final RuleBasedTransitionModel MODEL = createModel();

    private static final ZonalTransition SPRING_1947 =
        new ZonalTransition(
            PlainTimestamp.of(1947, 4, 6, 3, 0)
                .at(CET)
                .getPosixTime(),
            3600,
            2 * 3600,
            3600);
    private static final ZonalTransition SUMMER_START_1947 =
        new ZonalTransition(
            PlainTimestamp.of(1947, 5, 11, 2, 0)
                .at(CET)
                .getPosixTime(),
            2 * 3600,
            3 * 3600,
            7200);
    private static final ZonalTransition SUMMER_END_1947 =
        new ZonalTransition(
            PlainTimestamp.of(1947, 6, 29, 3, 0)
                .at(CEMT)
                .getPosixTime(),
            3 * 3600,
            2 * 3600,
            3600);
    private static final ZonalTransition AUTUMN_1947 =
        new ZonalTransition(
            PlainDate.of(1947, Month.OCTOBER, 1)
                .with(PlainDate.WEEKDAY_IN_MONTH.setToFirst(Weekday.SUNDAY))
                .atTime(2, 0)
                .at(CET)
                .getPosixTime(),
            2 * 3600,
            3600,
            0);

    @Test
    public void getInitialOffset() {
        assertThat(
            MODEL.getInitialOffset(),
            is(CET));
    }

    @Test
    public void getStdTransitions() {
        ZonalTransition spring1970 =
            new ZonalTransition(
                PlainTimestamp.of(1970, 4, 6, 3, 0)
                    .at(CET)
                    .getPosixTime(),
                3600,
                2 * 3600,
                3600);
        int year = SystemClock.inZonalView(CET).today().getYear();
        ZonalTransition autumnCurrentYear =
        new ZonalTransition(
            PlainDate.of(year, Month.OCTOBER, 1)
                .with(PlainDate.WEEKDAY_IN_MONTH.setToFirst(Weekday.SUNDAY))
                .atTime(2, 0)
                .at(CET)
                .getPosixTime(),
            2 * 3600,
            3600,
            0);
        List<ZonalTransition> stdTransitions = MODEL.getStdTransitions();
        assertThat(
            stdTransitions.contains(spring1970),
            is(true));
        assertThat(
            stdTransitions.contains(autumnCurrentYear),
            is(true));
    }

    @Test
    public void getTransitions() {
        Moment start =
            PlainTimestamp.of(1947, 1, 1, 0, 0, 0).at(CET);
        Moment end =
            PlainTimestamp.of(1947, 12, 31, 24, 0, 0).at(CET);
        List<ZonalTransition> transitions = MODEL.getTransitions(start, end);
        List<ZonalTransition> expected =
            Arrays.asList(
                SPRING_1947, SUMMER_START_1947, SUMMER_END_1947, AUTUMN_1947);
        assertThat(transitions, is(expected));
    }

    @Test
    public void isEmpty() {
        assertThat(MODEL.isEmpty(), is(false));
    }

    @Test
    public void getValidOffsetsBeforeSummerStartTransition() {
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(1947, 5, 11),
                PlainTime.of(2, 59)
            ),
            is(Collections.singletonList(CEST)));
    }

    @Test
    public void getValidOffsetsAtSummerStartTransition() {
        List<ZonalOffset> expected = Collections.emptyList();
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(1947, 5, 11),
                PlainTime.of(3, 0)
            ),
            is(expected));
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(1947, 5, 11),
                PlainTime.of(3, 59)
            ),
            is(expected));
    }

    @Test
    public void getValidOffsetsAfterSummerStartTransition() {
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(1947, 5, 11),
                PlainTime.of(4, 0)
            ),
            is(Collections.singletonList(CEMT)));
    }

    @Test
    public void getValidOffsetsBeforeSummerEndTransition() {
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(1947, 6, 29),
                PlainTime.of(1, 59)
            ),
            is(Collections.singletonList(CEMT)));
    }

    @Test
    public void getValidOffsetsAtSummerEndTransition() {
        List<ZonalOffset> expected = Arrays.asList(CEST, CEMT);
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(1947, 6, 29),
                PlainTime.of(2, 0)
            ),
            is(expected));
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(1947, 6, 29),
                PlainTime.of(2, 59)
            ),
            is(expected));
    }

    @Test
    public void getValidOffsetsAfterSummerEndTransition() {
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(1947, 6, 29),
                PlainTime.of(3, 0)
            ),
            is(Collections.singletonList(CEST)));
    }

    @Test
    public void getConflictBeforeSummerStartTransition() {
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(1947, 5, 11),
                PlainTime.of(2, 59)
            ),
            nullValue());
    }

    @Test
    public void getConflictAtSummerStartTransition() {
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(1947, 5, 11),
                PlainTime.of(3, 0)
            ),
            is(SUMMER_START_1947));
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(1947, 5, 11),
                PlainTime.of(3, 59)
            ),
            is(SUMMER_START_1947));
    }

    @Test
    public void getConflictAfterSummerStartTransition() {
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(1947, 5, 11),
                PlainTime.of(4, 0)
            ),
            nullValue());
    }

    @Test
    public void getConflictBeforeSummerEndTransition() {
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(1947, 6, 29),
                PlainTime.of(1, 59)
            ),
            nullValue());
    }

    @Test
    public void getConflictAtSummerEndTransition() {
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(1947, 6, 29),
                PlainTime.of(2, 0)
            ),
            is(SUMMER_END_1947));
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(1947, 6, 29),
                PlainTime.of(2, 59)
            ),
            is(SUMMER_END_1947));
    }

    @Test
    public void getConflictAfterSummerEndTransition() {
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(1947, 6, 29),
                PlainTime.of(3, 0)
            ),
            nullValue());
    }

    @Test
    public void getStartTransition1() {
        Moment utc =
            PlainDate.of(1947, 5, 11)
                .atTime(2, 0)
                .at(CET)
                .minus(1, TimeUnit.SECONDS);
        assertThat(
            MODEL.getStartTransition(utc),
            is(SPRING_1947));
    }

    @Test
    public void getStartTransition2() {
        Moment utc = PlainDate.of(1947, 5, 11).atTime(2, 0).at(CET);
        assertThat(
            MODEL.getStartTransition(utc),
            is(SUMMER_START_1947));
    }

    @Test
    public void getStartTransition3() {
        Moment utc =
            PlainDate.of(1947, 6, 29)
                .atTime(3, 0)
                .at(CEMT)
                .minus(1, TimeUnit.SECONDS);
        assertThat(
            MODEL.getStartTransition(utc),
            is(SUMMER_START_1947));
    }

    @Test
    public void getStartTransition4() {
        Moment utc = PlainDate.of(1947, 6, 29).atTime(3, 0).at(CEMT);
        assertThat(
            MODEL.getStartTransition(utc),
            is(SUMMER_END_1947));
    }

    @Test
    public void findNextTransition1() {
        Moment utc =
            PlainDate.of(1947, 5, 11)
                .atTime(2, 0)
                .at(CET)
                .minus(1, TimeUnit.SECONDS);
        assertThat(
            MODEL.findNextTransition(utc).get(),
            is(SUMMER_START_1947));
    }

    @Test
    public void findNextTransition2() {
        Moment utc = PlainDate.of(1947, 5, 11).atTime(2, 0).at(CET);
        assertThat(
            MODEL.findNextTransition(utc).get(),
            is(SUMMER_END_1947));
    }

    @Test
    public void findNextTransition3() {
        Moment utc =
            PlainDate.of(1947, 6, 29)
                .atTime(3, 0)
                .at(CEMT)
                .minus(1, TimeUnit.SECONDS);
        assertThat(
            MODEL.findNextTransition(utc).get(),
            is(SUMMER_END_1947));
    }

    @Test
    public void findNextTransition4() {
        Moment utc = PlainDate.of(1947, 6, 29).atTime(3, 0).at(CEMT);
        assertThat(
            MODEL.findNextTransition(utc).get(),
            is(AUTUMN_1947));
    }

    private static RuleBasedTransitionModel createModel() {

        DaylightSavingRule spring =
            GregorianTimezoneRule.ofFixedDay(
                Month.APRIL,
                6,
                PlainTime.of(3),
                OffsetIndicator.STANDARD_TIME,
                3600);
        DaylightSavingRule startHighSummer =
            GregorianTimezoneRule.ofFixedDay(
                Month.MAY,
                11,
                PlainTime.of(2),
                OffsetIndicator.STANDARD_TIME,
                7200);
        DaylightSavingRule endHighSummer =
            GregorianTimezoneRule.ofFixedDay(
                Month.JUNE,
                29,
                PlainTime.of(3),
                OffsetIndicator.WALL_TIME,
                3600);
        DaylightSavingRule autumn =
            GregorianTimezoneRule.ofWeekdayAfterDate(
                Month.OCTOBER,
                1,
                Weekday.SUNDAY,
                PlainTime.of(2),
                OffsetIndicator.STANDARD_TIME,
                0);

        List<DaylightSavingRule> rules = new ArrayList<>();
        rules.add(spring);
        rules.add(startHighSummer);
        rules.add(endHighSummer);
        rules.add(autumn);
        return new RuleBasedTransitionModel(CET, rules);

    }

}