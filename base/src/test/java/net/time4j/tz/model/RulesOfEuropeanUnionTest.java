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
public class RulesOfEuropeanUnionTest {

    private static final RuleBasedTransitionModel MODEL = createModel();

    private static final ZonalTransition AUTUMN_1995 =
        new ZonalTransition(
            PlainTimestamp.of(1995, 10, 1, 1, 0)
                .with(PlainDate.WEEKDAY_IN_MONTH.setToLast(Weekday.SUNDAY))
                .atUTC()
                .getPosixTime(),
            7200,
            3600,
            0);
    private static final ZonalTransition SPRING_1996 =
        new ZonalTransition(
            PlainTimestamp.of(1996, 3, 1, 1, 0)
                .with(PlainDate.WEEKDAY_IN_MONTH.setToLast(Weekday.SUNDAY))
                .atUTC()
                .getPosixTime(),
            3600,
            7200,
            3600);
    private static final ZonalTransition AUTUMN_1996 =
        new ZonalTransition(
            PlainTimestamp.of(1996, 10, 1, 1, 0)
                .with(PlainDate.WEEKDAY_IN_MONTH.setToLast(Weekday.SUNDAY))
                .atUTC()
                .getPosixTime(),
            7200,
            3600,
            0);
    private static final ZonalTransition SPRING_1997 =
        new ZonalTransition(
            PlainTimestamp.of(1997, 3, 1, 1, 0)
                .with(PlainDate.WEEKDAY_IN_MONTH.setToLast(Weekday.SUNDAY))
                .atUTC()
                .getPosixTime(),
            3600,
            7200,
            3600);

    @Test
    public void getInitialOffset() {
        assertThat(
            MODEL.getInitialOffset(),
            is(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 1)));
    }

    @Test
    public void hasNegativeDST() {
        assertThat(MODEL.hasNegativeDST(), is(false));
    }

    @Test
    public void getStdTransitions() {
        ZonalTransition spring1970 = // here proleptic (historically after 1981)
            new ZonalTransition(
                PlainTimestamp.of(1970, 3, 1, 1, 0)
                    .with(PlainDate.WEEKDAY_IN_MONTH.setToLast(Weekday.SUNDAY))
                    .atUTC()
                    .getPosixTime(),
                3600,
                7200,
                3600);
        int year =
            SystemClock.inZonalView(
                ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 1)
            ).today().getYear();
        ZonalTransition autumnCurrentYear =
            new ZonalTransition(
                PlainTimestamp.of(year, 10, 1, 1, 0)
                    .with(PlainDate.WEEKDAY_IN_MONTH.setToLast(Weekday.SUNDAY))
                    .atUTC()
                    .getPosixTime(),
                7200,
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
    public void getTransitionsAround1996() {
        Moment start =
            PlainTimestamp.of(1995, 10, 29, 1, 0, 0).atUTC();
        Moment end =
            PlainTimestamp.of(1997, 3, 30, 1, 0, 1).atUTC();
        List<ZonalTransition> transitions = MODEL.getTransitions(start, end);
        List<ZonalTransition> expected =
            Arrays.asList(AUTUMN_1995, SPRING_1996, AUTUMN_1996, SPRING_1997);
        assertThat(transitions, is(expected));
    }

    @Test
    public void getTransitionsOf1996Only() {
        Moment start =
            PlainTimestamp.of(1995, 10, 29, 1, 0, 1).atUTC();
        Moment end =
            PlainTimestamp.of(1997, 3, 30, 1, 0, 0).atUTC();
        List<ZonalTransition> transitions = MODEL.getTransitions(start, end);
        List<ZonalTransition> expected =
            Arrays.asList(SPRING_1996, AUTUMN_1996);
        assertThat(transitions, is(expected));
    }

    @Test
    public void isEmpty() {
        assertThat(MODEL.isEmpty(), is(false));
    }

    @Test
    public void getValidOffsetsBeforeSpringTransition() {
        List<ZonalOffset> expected =
            Collections.singletonList(
                ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 1));
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(1996, 3, 31),
                PlainTime.of(1, 59)
            ),
            is(expected));
    }

    @Test
    public void getValidOffsetsAtSpringTransition() {
        List<ZonalOffset> expected = Collections.emptyList();
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(1996, 3, 31),
                PlainTime.of(2, 0)
            ),
            is(expected));
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(1996, 3, 31),
                PlainTime.of(2, 59)
            ),
            is(expected));
    }

    @Test
    public void getValidOffsetsAfterSpringTransition() {
        List<ZonalOffset> expected =
            Collections.singletonList(
                ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 2));
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(1996, 3, 31),
                PlainTime.of(3, 0)
            ),
            is(expected));
    }

    @Test
    public void getValidOffsetsBeforeAutumnTransition() {
        List<ZonalOffset> expected =
            Collections.singletonList(
                ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 2));
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(1996, 10, 27),
                PlainTime.of(1, 59)
            ),
            is(expected));
    }

    @Test
    public void getValidOffsetsAtAutumnTransition() {
        List<ZonalOffset> expected =
            Arrays.asList(
                ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 1),
                ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 2));
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(1996, 10, 27),
                PlainTime.of(2, 0)
            ),
            is(expected));
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(1996, 10, 27),
                PlainTime.of(2, 59)
            ),
            is(expected));
    }

    @Test
    public void getValidOffsetsAfterAutumnTransition() {
        List<ZonalOffset> expected =
            Collections.singletonList(
                ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 1));
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(1996, 10, 27),
                PlainTime.of(3, 0)
            ),
            is(expected));
    }

    @Test
    public void getConflictBeforeSpringTransition() {
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(1996, 3, 31),
                PlainTime.of(1, 59)
            ),
            nullValue());
    }

    @Test
    public void getConflictAtSpringTransition() {
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(1996, 3, 31),
                PlainTime.of(2, 0)
            ),
            is(SPRING_1996));
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(1996, 3, 31),
                PlainTime.of(2, 59)
            ),
            is(SPRING_1996));
    }

    @Test
    public void getConflictAfterSpringTransition() {
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(1996, 3, 31),
                PlainTime.of(3, 0)
            ),
            nullValue());
    }

    @Test
    public void getConflictBeforeAutumnTransition() {
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(1996, 10, 27),
                PlainTime.of(1, 59)
            ),
            nullValue());
    }

    @Test
    public void getConflictAtAutumnTransition() {
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(1996, 10, 27),
                PlainTime.of(2, 0)
            ),
            is(AUTUMN_1996));
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(1996, 10, 27),
                PlainTime.of(2, 59)
            ),
            is(AUTUMN_1996));
    }

    @Test
    public void getConflictAfterAutumnTransition() {
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(1996, 10, 27),
                PlainTime.of(3, 0)
            ),
            nullValue());
    }

    @Test
    public void getStartTransition1() {
        Moment utc =
            PlainDate.of(1996, 3, 31)
                .atTime(1, 0)
                .atUTC()
                .minus(1, TimeUnit.SECONDS);
        assertThat(
            MODEL.getStartTransition(utc),
            is(AUTUMN_1995));
    }

    @Test
    public void getStartTransition2() {
        Moment utc = PlainDate.of(1996, 3, 31).atTime(1, 0).atUTC();
        assertThat(
            MODEL.getStartTransition(utc),
            is(SPRING_1996));
    }

    @Test
    public void getStartTransition3() {
        Moment utc =
            PlainDate.of(1996, 10, 27)
                .atTime(1, 0)
                .atUTC()
                .minus(1, TimeUnit.SECONDS);
        assertThat(
            MODEL.getStartTransition(utc),
            is(SPRING_1996));
    }

    @Test
    public void getStartTransition4() {
        Moment utc = PlainDate.of(1996, 10, 27).atTime(1, 0).atUTC();
        assertThat(
            MODEL.getStartTransition(utc),
            is(AUTUMN_1996));
    }

    @Test
    public void findNextTransition1() {
        Moment utc =
            PlainDate.of(1996, 3, 31)
                .atTime(1, 0)
                .atUTC()
                .minus(1, TimeUnit.SECONDS);
        assertThat(
            MODEL.findNextTransition(utc).get(),
            is(SPRING_1996));
    }

    @Test
    public void findNextTransition2() {
        Moment utc = PlainDate.of(1996, 3, 31).atTime(1, 0).atUTC();
        assertThat(
            MODEL.findNextTransition(utc).get(),
            is(AUTUMN_1996));
    }

    @Test
    public void findNextTransition3() {
        Moment utc =
            PlainDate.of(1996, 10, 27)
                .atTime(1, 0)
                .atUTC()
                .minus(1, TimeUnit.SECONDS);
        assertThat(
            MODEL.findNextTransition(utc).get(),
            is(AUTUMN_1996));
    }

    @Test
    public void findNextTransition4() {
        Moment utc = PlainDate.of(1996, 10, 27).atTime(1, 0).atUTC();
        assertThat(
            MODEL.findNextTransition(utc).get(),
            is(SPRING_1997));
    }

    private static RuleBasedTransitionModel createModel() {

        DaylightSavingRule spring =
            GregorianTimezoneRule.ofLastWeekday(
                Month.MARCH,
                Weekday.SUNDAY,
                PlainTime.of(1),
                OffsetIndicator.UTC_TIME,
                3600);
        DaylightSavingRule autumn =
            GregorianTimezoneRule.ofLastWeekday(
                Month.OCTOBER,
                Weekday.SUNDAY,
                PlainTime.of(1),
                OffsetIndicator.UTC_TIME,
                0);

        List<DaylightSavingRule> rules = new ArrayList<>();
        rules.add(autumn);
        rules.add(spring);

        return new RuleBasedTransitionModel(
            ZonalOffset.ofTotalSeconds(3600),
            rules);
    }

}