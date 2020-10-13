package net.time4j.tz.model;

import net.time4j.Moment;
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.Weekday;
import net.time4j.base.UnixTime;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.TransitionHistory;
import net.time4j.tz.ZonalOffset;
import net.time4j.tz.ZonalTransition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class CompositeTransitionModelTest {

    private static final ZonalTransition FIRST =
        new ZonalTransition(0L, 1800, 7200, 3600);
    private static final ZonalTransition SECOND =
        new ZonalTransition(365 * 86400L, 7200, 3600, 0);
    private static final ZonalTransition THIRD =
        new ZonalTransition(830 * 86400L, 3600, 7200, 3600);

    private static final TransitionHistory MODEL = createModel(false);
    private static final TransitionHistory MODEL_EXT = createModel(true);
    private static final TransitionHistory MODEL_SINGLE =
        createSingleRuleModel();

    private static final ZonalTransition FOURTH =
        new ZonalTransition(
            PlainDate.of(1972, 10, 1)
                .with(PlainDate.WEEKDAY_IN_MONTH.setToLast(Weekday.SUNDAY))
                .atTime(1, 0)
                .atUTC()
                .getPosixTime(),
            7200,
            3600,
            0
        );

    private static final ZonalTransition FIFTH =
        new ZonalTransition(
            PlainDate.of(1973, 3, 1)
                .with(PlainDate.WEEKDAY_IN_MONTH.setToLast(Weekday.SUNDAY))
                .atTime(1, 0)
                .atUTC()
                .getPosixTime(),
            3600,
            7200,
            3600
        );

    @Test
    public void getInitialOffset() {
        ZonalOffset expected =
            ZonalOffset.ofHoursMinutes(OffsetSign.AHEAD_OF_UTC, 0, 30);
        assertThat(MODEL.getInitialOffset(), is(expected));
        assertThat(MODEL_EXT.getInitialOffset(), is(expected));
        assertThat(MODEL_SINGLE.getInitialOffset(), is(expected));
    }

    @Test
    public void getStartTransition1() {
        assertThat(MODEL.getStartTransition(new UT(-1)), nullValue());
        assertThat(MODEL_EXT.getStartTransition(new UT(-1)), nullValue());
        assertThat(MODEL_SINGLE.getStartTransition(new UT(-1)), nullValue());
    }

    @Test
    public void getStartTransition2() {
        assertThat(MODEL.getStartTransition(new UT(0)), is(FIRST));
        assertThat(MODEL_EXT.getStartTransition(new UT(0)), is(FIRST));
        assertThat(MODEL_SINGLE.getStartTransition(new UT(0)), is(FIRST));
    }

    @Test
    public void getStartTransition3() {
        assertThat(
            MODEL.getStartTransition(new UT(365 * 86400L - 1)),
            is(FIRST));
        assertThat(
            MODEL_EXT.getStartTransition(new UT(365 * 86400L - 1)),
            is(FIRST));
        assertThat(
            MODEL_SINGLE.getStartTransition(new UT(365 * 86400L - 1)),
            is(FIRST));
    }

    @Test
    public void getStartTransition4() {
        assertThat(
            MODEL.getStartTransition(new UT(365 * 86400L)),
            is(SECOND));
        assertThat(
            MODEL_EXT.getStartTransition(new UT(365 * 86400L)),
            is(SECOND));
        assertThat(
            MODEL_SINGLE.getStartTransition(new UT(365 * 86400L)),
            is(SECOND));
    }

    @Test
    public void getStartTransition5() {
        ZonalTransition expected = THIRD;
        ZonalTransition zt =
            MODEL.getStartTransition(new UT(FOURTH.getPosixTime() - 1));
        ZonalTransition ztExt =
            MODEL_EXT.getStartTransition(new UT(FOURTH.getPosixTime() - 1));
        ZonalTransition ztSingle =
            MODEL_SINGLE.getStartTransition(new UT(FOURTH.getPosixTime() - 1));
        assertThat(zt, is(expected));
        assertThat(ztExt, is(expected));
        assertThat(ztSingle, is(expected));
    }

    @Test
    public void getStartTransition6() {
        ZonalTransition expected = FOURTH;
        ZonalTransition zt =
            MODEL.getStartTransition(new UT(FOURTH.getPosixTime()));
        ZonalTransition ztExt =
            MODEL_EXT.getStartTransition(new UT(FOURTH.getPosixTime()));
        assertThat(zt, is(expected));
        assertThat(ztExt, is(expected));
        assertThat(zt == expected, is(false));
        assertThat(ztExt == expected, is(false));
        assertThat(zt == ztExt, is(false));
    }

    @Test
    public void findPreviousTransition1() {
        ZonalTransition expected = THIRD; // from array
        ZonalTransition zt =
            MODEL.findPreviousTransition(new UT(FOURTH.getPosixTime())).get();
        assertThat(zt, is(expected));
    }

    @Test
    public void findPreviousTransition2() {
        ZonalTransition expected = FOURTH; // rule-based
        ZonalTransition zt =
            MODEL.findPreviousTransition(new UT(FOURTH.getPosixTime() + 1)).get();
        assertThat(zt, is(expected));
    }

    @Test
    public void findNextTransition1() {
        assertThat(MODEL.findNextTransition(new UT(-1)).get(), is(FIRST));
        assertThat(MODEL_EXT.findNextTransition(new UT(-1)).get(), is(FIRST));
        assertThat(MODEL_SINGLE.findNextTransition(new UT(-1)).get(), is(FIRST));
    }

    @Test
    public void findNextTransition2() {
        assertThat(MODEL.findNextTransition(new UT(0)).get(), is(SECOND));
        assertThat(MODEL_EXT.findNextTransition(new UT(0)).get(), is(SECOND));
        assertThat(MODEL_SINGLE.findNextTransition(new UT(0)).get(), is(SECOND));
    }

    @Test
    public void findNextTransition3() {
        assertThat(
            MODEL.findNextTransition(new UT(THIRD.getPosixTime() - 1)).get(),
            is(THIRD));
        assertThat(
            MODEL_EXT.findNextTransition(new UT(THIRD.getPosixTime() - 1)).get(),
            is(THIRD));
        assertThat(
            MODEL_SINGLE.findNextTransition(new UT(THIRD.getPosixTime() - 1)).get(),
            is(THIRD));
    }

    @Test
    public void findNextTransition4() {
        assertThat(
            MODEL.findNextTransition(new UT(THIRD.getPosixTime())).get(),
            is(FOURTH));
        assertThat(
            MODEL_EXT.findNextTransition(new UT(THIRD.getPosixTime())).get(),
            is(FOURTH));
        Moment m =
            PlainTimestamp.of(1973, 3, 1, 1, 0)
                .with(PlainDate.WEEKDAY_IN_MONTH.setToLast(Weekday.SUNDAY))
                .atUTC();
        assertThat(
            MODEL_SINGLE.findNextTransition(new UT(THIRD.getPosixTime())).get(),
            is(new ZonalTransition(m.getPosixTime(), 7200, 7200, 3600)));
    }

    @Test
    public void findNextTransition5() {
        assertThat(
            MODEL.findNextTransition(new UT(FOURTH.getPosixTime() - 1)).get(),
            is(FOURTH));
        assertThat(
            MODEL_EXT.findNextTransition(new UT(FOURTH.getPosixTime() - 1)).get(),
            is(FOURTH));
    }

    @Test
    public void findNextTransition6() {
        assertThat(
            MODEL.findNextTransition(new UT(FOURTH.getPosixTime())).get(),
            is(FIFTH));
        assertThat(
            MODEL_EXT.findNextTransition(new UT(FOURTH.getPosixTime())).get(),
            is(FIFTH));
    }

    @Test
    public void getGapTransition1() {
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(1970, 1, 1),
                PlainTime.of(0, 29, 59)),
            nullValue());
        assertThat(
            MODEL_EXT.getConflictTransition(
                PlainDate.of(1970, 1, 1),
                PlainTime.of(0, 29, 59)),
            nullValue());
    }

    @Test
    public void getGapTransition2() {
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(1970, 1, 1),
                PlainTime.of(0, 30)),
            is(FIRST));
        assertThat(
            MODEL_EXT.getConflictTransition(
                PlainDate.of(1970, 1, 1),
                PlainTime.of(0, 30)),
            is(FIRST));
    }

    @Test
    public void getGapTransition3() {
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(1970, 1, 1),
                PlainTime.of(1, 59, 59)),
            is(FIRST));
        assertThat(
            MODEL_EXT.getConflictTransition(
                PlainDate.of(1970, 1, 1),
                PlainTime.of(1, 59, 59)),
            is(FIRST));
    }

    @Test
    public void getGapTransition4() {
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(1970, 1, 1),
                PlainTime.of(2, 0)),
            nullValue());
        assertThat(
            MODEL_EXT.getConflictTransition(
                PlainDate.of(1970, 1, 1),
                PlainTime.of(2, 0)),
            nullValue());
    }

    @Test
    public void getGapTransition5() {
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(1973, 3, 1)
                    .with(PlainDate.WEEKDAY_IN_MONTH.setToLast(Weekday.SUNDAY)),
                PlainTime.of(2, 0)),
            is(FIFTH));
        assertThat(
            MODEL_EXT.getConflictTransition(
                PlainDate.of(1973, 3, 1)
                    .with(PlainDate.WEEKDAY_IN_MONTH.setToLast(Weekday.SUNDAY)),
                PlainTime.of(2, 0)),
            is(FIFTH));
    }

    @Test
    public void getOverlapTransition1() {
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(1971, 1, 1),
                PlainTime.of(0, 59, 59)),
            nullValue());
        assertThat(
            MODEL_EXT.getConflictTransition(
                PlainDate.of(1971, 1, 1),
                PlainTime.of(0, 59, 59)),
            nullValue());
    }

    @Test
    public void getOverlapTransition2() {
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(1971, 1, 1),
                PlainTime.of(1, 0)),
            is(SECOND));
        assertThat(
            MODEL_EXT.getConflictTransition(
                PlainDate.of(1971, 1, 1),
                PlainTime.of(1, 0)),
            is(SECOND));
    }

    @Test
    public void getOverlapTransition3() {
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(1971, 1, 1),
                PlainTime.of(1, 59, 59)),
            is(SECOND));
        assertThat(
            MODEL_EXT.getConflictTransition(
                PlainDate.of(1971, 1, 1),
                PlainTime.of(1, 59, 59)),
            is(SECOND));
    }

    @Test
    public void getOverlapTransition4() {
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(1971, 1, 1),
                PlainTime.of(2, 0)),
            nullValue());
        assertThat(
            MODEL_EXT.getConflictTransition(
                PlainDate.of(1971, 1, 1),
                PlainTime.of(2, 0)),
            nullValue());
    }

    @Test
    public void getOverlapTransition5() {
        assertThat(
            MODEL.getConflictTransition(
                PlainDate.of(1972, 10, 1)
                    .with(PlainDate.WEEKDAY_IN_MONTH.setToLast(Weekday.SUNDAY)),
                PlainTime.of(2, 0)),
            is(FOURTH));
        assertThat(
            MODEL_EXT.getConflictTransition(
                PlainDate.of(1972, 10, 1)
                    .with(PlainDate.WEEKDAY_IN_MONTH.setToLast(Weekday.SUNDAY)),
                PlainTime.of(2, 0)),
            is(FOURTH));
    }

    @Test
    public void getValidOffsetsOfGapTransition1() {
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(1970, 1, 1),
                PlainTime.of(0, 29, 59)),
            is(TransitionModel.toList(1800)));
        assertThat(
            MODEL_EXT.getValidOffsets(
                PlainDate.of(1970, 1, 1),
                PlainTime.of(0, 29, 59)),
            is(TransitionModel.toList(1800)));
    }

    @Test
    public void getValidOffsetsOfGapTransition2() {
        List<ZonalOffset> offsets = Collections.emptyList();
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(1970, 1, 1),
                PlainTime.of(0, 30)),
            is(offsets));
        assertThat(
            MODEL_EXT.getValidOffsets(
                PlainDate.of(1970, 1, 1),
                PlainTime.of(0, 30)),
            is(offsets));
    }

    @Test
    public void getValidOffsetsOfGapTransition3() {
        List<ZonalOffset> offsets = Collections.emptyList();
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(1970, 1, 1),
                PlainTime.of(1, 59, 59)),
            is(offsets));
        assertThat(
            MODEL_EXT.getValidOffsets(
                PlainDate.of(1970, 1, 1),
                PlainTime.of(1, 59, 59)),
            is(offsets));
    }

    @Test
    public void getValidOffsetsOfGapTransition4() {
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(1970, 1, 1),
                PlainTime.of(2, 0)),
            is(TransitionModel.toList(7200)));
        assertThat(
            MODEL_EXT.getValidOffsets(
                PlainDate.of(1970, 1, 1),
                PlainTime.of(2, 0)),
            is(TransitionModel.toList(7200)));
    }

    @Test
    public void getValidOffsetsOfOverlapTransition1() {
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(1971, 1, 1),
                PlainTime.of(0, 59, 59)),
            is(TransitionModel.toList(7200)));
        assertThat(
            MODEL_EXT.getValidOffsets(
                PlainDate.of(1971, 1, 1),
                PlainTime.of(0, 59, 59)),
            is(TransitionModel.toList(7200)));
    }

    @Test
    public void getValidOffsetsOfOverlapTransition2() {
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(1971, 1, 1),
                PlainTime.of(1, 0)),
            is(TransitionModel.toList(3600, 7200)));
        assertThat(
            MODEL_EXT.getValidOffsets(
                PlainDate.of(1971, 1, 1),
                PlainTime.of(1, 0)),
            is(TransitionModel.toList(3600, 7200)));
    }

    @Test
    public void getValidOffsetsOfOverlapTransition3() {
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(1971, 1, 1),
                PlainTime.of(1, 59, 59)),
            is(TransitionModel.toList(3600, 7200)));
        assertThat(
            MODEL_EXT.getValidOffsets(
                PlainDate.of(1971, 1, 1),
                PlainTime.of(1, 59, 59)),
            is(TransitionModel.toList(3600, 7200)));
    }

    @Test
    public void getValidOffsetsOfOverlapTransition4() {
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(1971, 1, 1),
                PlainTime.of(2, 0)),
            is(TransitionModel.toList(3600)));
        assertThat(
            MODEL_EXT.getValidOffsets(
                PlainDate.of(1971, 1, 1),
                PlainTime.of(2, 0)),
            is(TransitionModel.toList(3600)));
    }

    @Test
    public void getValidOffsetsBeforeStart() {
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(1900, 1, 1),
                PlainTime.midnightAtStartOfDay()),
            is(TransitionModel.toList(1800)));
        assertThat(
            MODEL_EXT.getValidOffsets(
                PlainDate.of(1900, 1, 1),
                PlainTime.midnightAtStartOfDay()),
            is(TransitionModel.toList(1800)));
    }

    @Test
    public void getValidOffsetsAtStartOfYear2100() {
        assertThat(
            MODEL.getValidOffsets(
                PlainDate.of(2100, 1, 1),
                PlainTime.midnightAtStartOfDay()),
            is(TransitionModel.toList(3600)));
        assertThat(
            MODEL_EXT.getValidOffsets(
                PlainDate.of(2100, 1, 1),
                PlainTime.midnightAtStartOfDay()),
            is(TransitionModel.toList(3600)));
    }

    @Test
    public void getStdTransitions() throws IOException {
        List<ZonalTransition> expected =
            Arrays.asList(FIRST, SECOND, THIRD);
        List<ZonalTransition> expectedExt =
            Arrays.asList(FIRST, SECOND, THIRD, FOURTH, FIFTH);
        long t1 = THIRD.getPosixTime() + 1;
        long t2 = TransitionModel.getFutureMoment(1);
        long factor = (long) (365.2425 * 86400L);
        long minExpected = (t2 - t1) * 2 / factor + 2; // estimated

        assertThat(
            MODEL.getStdTransitions(),
            is(expected));
        assertThat(
            MODEL_EXT.getStdTransitions().containsAll(expectedExt),
            is(true));
        assertThat(
            (MODEL_EXT.getStdTransitions().size() >= minExpected),
            is(true));
    }

    @Test
    public void getTransitions1() {
        assertThat(
            MODEL.getTransitions(new UT(0), new UT(FOURTH.getPosixTime())),
            is(Arrays.asList(FIRST, SECOND, THIRD)));
        assertThat(
            MODEL_EXT.getTransitions(new UT(0), new UT(FOURTH.getPosixTime())),
            is(Arrays.asList(FIRST, SECOND, THIRD)));
    }

    @Test
    public void getTransitions2() {
        assertThat(
            MODEL.getTransitions(new UT(0), new UT(FOURTH.getPosixTime() + 1)),
            is(Arrays.asList(FIRST, SECOND, THIRD, FOURTH)));
        assertThat(
            MODEL_EXT.getTransitions(
                new UT(0),
                new UT(FOURTH.getPosixTime() + 1)),
            is(Arrays.asList(FIRST, SECOND, THIRD, FOURTH)));
    }

    @Test
    public void isEmpty() {
        assertThat(MODEL.isEmpty(), is(false));
        assertThat(MODEL_EXT.isEmpty(), is(false));
        assertThat(MODEL_SINGLE.isEmpty(), is(false));
    }

    @Test
    public void equals() {
        assertThat(MODEL.equals(MODEL_EXT), is(true));
    }

    @Test
    public void identity() {
        assertThat(MODEL == MODEL_EXT, is(false));
    }

    private static TransitionHistory createModel(boolean enlarged) {
        List<ZonalTransition> transitions = Arrays.asList(FIRST, THIRD, SECOND);
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

        if (enlarged) {
            return TransitionModel.of(
                ZonalOffset.ofTotalSeconds(FIRST.getPreviousOffset()),
                transitions,
                rules,
                true,
                true);
        } else {
            return new CompositeTransitionModel(
                3,
                transitions,
                rules,
                true,
                true);
        }
    }

    private static TransitionHistory createSingleRuleModel() {
        List<ZonalTransition> transitions = Arrays.asList(FIRST, THIRD, SECOND);
        DaylightSavingRule rule =
            GregorianTimezoneRule.ofLastWeekday(
                Month.MARCH,
                Weekday.SUNDAY,
                PlainTime.of(1),
                OffsetIndicator.UTC_TIME,
                3600);
        List<DaylightSavingRule> rules = new ArrayList<>();
        rules.add(rule);

        return new CompositeTransitionModel(
            1,
            transitions,
            rules,
            true,
            true);
    }

    // Hilfsklasse
    private static class UT implements UnixTime {

        private final long ut;

        UT(long ut) {
            super();
            this.ut = ut;
        }

        @Override
        public long getPosixTime() {
            return this.ut;
        }

        @Override
        public int getNanosecond() {
            return 0;
        }

    }

}