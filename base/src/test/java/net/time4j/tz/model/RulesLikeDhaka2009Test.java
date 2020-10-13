package net.time4j.tz.model;

import net.time4j.Moment;
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.SystemClock;
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
public class RulesLikeDhaka2009Test {

    private static final ZonalOffset BDT =
        ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 6);
    private static final ZonalOffset BDST =
        ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 7);
    private static final RuleBasedTransitionModel MODEL = createModel();

    private static final ZonalTransition WINTER_2008 =
        new ZonalTransition(
            PlainTimestamp.of(2008, 12, 31, 24, 0)
                .at(BDST)
                .getPosixTime(),
            7 * 3600,
            6 * 3600,
            0);
    private static final ZonalTransition SUMMER_2009 =
        new ZonalTransition(
            PlainTimestamp.of(2009, 6, 19, 23, 0)
                .at(BDT)
                .getPosixTime(),
            6 * 3600,
            7 * 3600,
            3600);
    private static final ZonalTransition WINTER_2009 =
        new ZonalTransition(
            PlainTimestamp.of(2009, 12, 31, 24, 0)
                .at(BDST)
                .getPosixTime(),
            7 * 3600,
            6 * 3600,
            0);
    private static final ZonalTransition SUMMER_2010 =
        new ZonalTransition(
            PlainTimestamp.of(2010, 6, 19, 23, 0)
                .at(BDT)
                .getPosixTime(),
            6 * 3600,
            7 * 3600,
            3600);

    @Test
    public void getInitialOffset() {
        assertThat(
            MODEL.getInitialOffset(),
            is(BDT));
    }

    @Test
    public void getStdTransitions() {
        ZonalTransition summer1970 = // here proleptic (historically 2009 only)
            new ZonalTransition(
                PlainTimestamp.of(1970, 6, 19, 23, 0)
                    .at(BDT)
                    .getPosixTime(),
                6 * 3600,
                7 * 3600,
                3600);
        int year = SystemClock.inZonalView(BDT).today().getYear();
        ZonalTransition winterCurrentYear =
            new ZonalTransition(
                PlainTimestamp.of(year, 12, 31, 24, 0)
                    .at(BDST)
                    .getPosixTime(),
                7 * 3600,
                6 * 3600,
                0);
        List<ZonalTransition> stdTransitions = MODEL.getStdTransitions();
        assertThat(
            stdTransitions.contains(summer1970),
            is(true));
        assertThat(
            stdTransitions.contains(winterCurrentYear),
            is(true));
    }

    @Test
    public void getTransitionsAround2009() {
        Moment start =
            PlainTimestamp.of(2008, 12, 31, 24, 0, 0).at(BDST);
        Moment end =
            PlainTimestamp.of(2010, 6, 19, 23, 0, 1).at(BDT);
        List<ZonalTransition> transitions = MODEL.getTransitions(start, end);
        List<ZonalTransition> expected =
            Arrays.asList(WINTER_2008, SUMMER_2009, WINTER_2009, SUMMER_2010);
        assertThat(transitions, is(expected));
    }

    @Test
    public void getTransitionsOf2009Only() {
        Moment start =
            PlainTimestamp.of(2008, 12, 31, 24, 0, 0)
                .at(BDST)
                .plus(1, TimeUnit.SECONDS);
        Moment end =
            PlainTimestamp.of(2010, 6, 19, 23, 0, 0).at(BDT);
        List<ZonalTransition> transitions = MODEL.getTransitions(start, end);
        List<ZonalTransition> expected =
            Arrays.asList(SUMMER_2009, WINTER_2009);
        assertThat(transitions, is(expected));
    }

    @Test
    public void isEmpty() {
        assertThat(MODEL.isEmpty(), is(false));
    }

    @Test
    public void getValidOffsetsBeforeSummerTransition() {
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(2009, 6, 19),
                PlainTime.of(22, 59)
            ),
            is(Collections.singletonList(BDT)));
    }

    @Test
    public void getValidOffsetsAtSummerTransition() {
        List<ZonalOffset> expected = Collections.emptyList();
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(2009, 6, 19),
                PlainTime.of(23, 0)
            ),
            is(expected));
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(2009, 6, 19),
                PlainTime.of(23, 59)
            ),
            is(expected));
    }

    @Test
    public void getValidOffsetsAfterSummerTransition() {
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(2009, 6, 19),
                PlainTime.of(24, 0)
            ),
            is(Collections.singletonList(BDST)));
    }

    @Test
    public void getValidOffsetsBeforeWinterTransition() {
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(2009, 12, 31),
                PlainTime.of(22, 59)
            ),
            is(Collections.singletonList(BDST)));
    }

    @Test
    public void getValidOffsetsAtWinterTransition() {
        List<ZonalOffset> expected = Arrays.asList(BDT, BDST);
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(2009, 12, 31),
                PlainTime.of(23, 0)
            ),
            is(expected));
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(2009, 12, 31),
                PlainTime.of(23, 59)
            ),
            is(expected));
    }

    @Test
    public void getValidOffsetsAfterWinterTransition() {
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(2009, 12, 31),
                PlainTime.of(24, 0)
            ),
            is(Collections.singletonList(BDT)));
    }

    @Test
    public void getConflictBeforeSummerTransition() {
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(2009, 6, 19),
                PlainTime.of(22, 59)
            ),
            nullValue());
    }

    @Test
    public void getConflictAtSummerTransition() {
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(2009, 6, 19),
                PlainTime.of(23, 0)
            ),
            is(SUMMER_2009));
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(2009, 6, 19),
                PlainTime.of(23, 59)
            ),
            is(SUMMER_2009));
    }

    @Test
    public void getConflictAfterSummerTransition() {
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(2009, 6, 19),
                PlainTime.of(24, 0)
            ),
            nullValue());
    }

    @Test
    public void getConflictBeforeWinterTransition() {
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(2009, 12, 31),
                PlainTime.of(22, 59)
            ),
            nullValue());
    }

    @Test
    public void getConflictAtWinterTransition() {
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(2009, 12, 31),
                PlainTime.of(23, 0)
            ),
            is(WINTER_2009));
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(2009, 12, 31),
                PlainTime.of(23, 59)
            ),
            is(WINTER_2009));
    }

    @Test
    public void getConflictAfterWinterTransition() {
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(2009, 12, 31),
                PlainTime.of(24, 0)
            ),
            nullValue());
    }

    @Test
    public void getStartTransition1() {
        Moment utc =
            PlainDate.of(2009, 6, 19)
                .atTime(23, 0)
                .at(BDT)
                .minus(1, TimeUnit.SECONDS);
        assertThat(
            MODEL.getStartTransition(utc),
            is(WINTER_2008));
    }

    @Test
    public void getStartTransition2() {
        Moment utc = PlainDate.of(2009, 6, 19).atTime(23, 0).at(BDT);
        assertThat(
            MODEL.getStartTransition(utc),
            is(SUMMER_2009));
    }

    @Test
    public void getStartTransition3() {
        Moment utc =
            PlainDate.of(2009, 12, 31)
                .atTime(24, 0)
                .at(BDST)
                .minus(1, TimeUnit.SECONDS);
        assertThat(
            MODEL.getStartTransition(utc),
            is(SUMMER_2009));
    }

    @Test
    public void getStartTransition4() {
        Moment utc = PlainDate.of(2009, 12, 31).atTime(24, 0).at(BDST);
        assertThat(
            MODEL.getStartTransition(utc),
            is(WINTER_2009));
    }

    @Test
    public void findNextTransition1() {
        Moment utc =
            PlainDate.of(2009, 6, 19)
                .atTime(23, 0)
                .at(BDT)
                .minus(1, TimeUnit.SECONDS);
        assertThat(
            MODEL.findNextTransition(utc).get(),
            is(SUMMER_2009));
    }

    @Test
    public void findNextTransition2() {
        Moment utc = PlainDate.of(2009, 6, 19).atTime(23, 0).at(BDT);
        assertThat(
            MODEL.findNextTransition(utc).get(),
            is(WINTER_2009));
    }

    @Test
    public void findNextTransition3() {
        Moment utc =
            PlainDate.of(2009, 12, 31)
                .atTime(24, 0)
                .at(BDST)
                .minus(1, TimeUnit.SECONDS);
        assertThat(
            MODEL.findNextTransition(utc).get(),
            is(WINTER_2009));
    }

    @Test
    public void findNextTransition4() {
        Moment utc = PlainDate.of(2009, 12, 31).atTime(24, 0).at(BDST);
        assertThat(
            MODEL.findNextTransition(utc).get(),
            is(SUMMER_2010));
    }

    private static RuleBasedTransitionModel createModel() {

        DaylightSavingRule start =
            GregorianTimezoneRule.ofFixedDay(
                Month.JUNE,
                19,
                PlainTime.of(23),
                OffsetIndicator.WALL_TIME,
                3600);
        DaylightSavingRule end =
            GregorianTimezoneRule.ofFixedDay(
                Month.DECEMBER,
                31,
                PlainTime.midnightAtEndOfDay(),
                OffsetIndicator.WALL_TIME,
                0);

        List<DaylightSavingRule> rules = new ArrayList<>();
        rules.add(start);
        rules.add(end);
        return new RuleBasedTransitionModel(BDT, rules);

    }

}