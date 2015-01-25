package net.time4j.tz.model;

import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.SystemClock;
import net.time4j.Weekday;
import net.time4j.base.TimeSource;
import net.time4j.base.UnixTime;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.TransitionHistory;
import net.time4j.tz.ZonalOffset;
import net.time4j.tz.ZonalTransition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;


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
    }

    @Test
    public void getStartTransition1() {
        assertThat(MODEL.getStartTransition(new UT(-1)), nullValue());
        assertThat(MODEL_EXT.getStartTransition(new UT(-1)), nullValue());
    }

    @Test
    public void getStartTransition2() {
        assertThat(MODEL.getStartTransition(new UT(0)), is(FIRST));
        assertThat(MODEL_EXT.getStartTransition(new UT(0)), is(FIRST));
    }

    @Test
    public void getStartTransition3() {
        assertThat(
            MODEL.getStartTransition(new UT(365 * 86400L - 1)),
            is(FIRST));
        assertThat(
            MODEL_EXT.getStartTransition(new UT(365 * 86400L - 1)),
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
    }

    @Test
    public void getStartTransition5() {
        ZonalTransition expected = THIRD;
        ZonalTransition zt =
            MODEL.getStartTransition(new UT(FOURTH.getPosixTime() - 1));
        ZonalTransition ztExt =
            MODEL_EXT.getStartTransition(new UT(FOURTH.getPosixTime() - 1));
        assertThat(zt, is(expected));
        assertThat(ztExt, is(expected));
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
    public void getNextTransition1() {
        assertThat(MODEL.getNextTransition(new UT(-1)), is(FIRST));
        assertThat(MODEL_EXT.getNextTransition(new UT(-1)), is(FIRST));
    }

    @Test
    public void getNextTransition2() {
        assertThat(MODEL.getNextTransition(new UT(0)), is(SECOND));
        assertThat(MODEL_EXT.getNextTransition(new UT(0)), is(SECOND));
    }

    @Test
    public void getNextTransition3() {
        assertThat(
            MODEL.getNextTransition(new UT(THIRD.getPosixTime() - 1)),
            is(THIRD));
        assertThat(
            MODEL_EXT.getNextTransition(new UT(THIRD.getPosixTime() - 1)),
            is(THIRD));
    }

    @Test
    public void getNextTransition4() {
        assertThat(
            MODEL.getNextTransition(new UT(THIRD.getPosixTime())),
            is(FOURTH));
        assertThat(
            MODEL_EXT.getNextTransition(new UT(THIRD.getPosixTime())),
            is(FOURTH));
    }

    @Test
    public void getNextTransition5() {
        assertThat(
            MODEL.getNextTransition(new UT(FOURTH.getPosixTime() - 1)),
            is(FOURTH));
        assertThat(
            MODEL_EXT.getNextTransition(new UT(FOURTH.getPosixTime() - 1)),
            is(FOURTH));
    }

    @Test
    public void getNextTransition6() {
        assertThat(
            MODEL.getNextTransition(new UT(FOURTH.getPosixTime())),
            is(FIFTH));
        assertThat(
            MODEL_EXT.getNextTransition(new UT(FOURTH.getPosixTime())),
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
    public void getStdTransitions() {
        List<ZonalTransition> expected =
            Arrays.asList(FIRST, SECOND, THIRD);
        List<ZonalTransition> expectedExt =
            Arrays.asList(FIRST, SECOND, THIRD, FOURTH, FIFTH);
        assertThat(
            MODEL.getStdTransitions(),
            is(expected));
        assertThat(
            MODEL_EXT.getStdTransitions(),
            is(expectedExt));
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
            DaylightSavingRule.ofLastWeekday(
                Month.MARCH,
                Weekday.SUNDAY,
                PlainTime.of(1),
                OffsetIndicator.UTC_TIME,
                3600);
        DaylightSavingRule autumn =
            DaylightSavingRule.ofLastWeekday(
                Month.OCTOBER,
                Weekday.SUNDAY,
                PlainTime.of(1),
                OffsetIndicator.UTC_TIME,
                0);
        List<DaylightSavingRule> rules = new ArrayList<DaylightSavingRule>();
        rules.add(autumn);
        rules.add(spring);

        if (enlarged) {
            return TransitionModel.of(
                ZonalOffset.ofTotalSeconds(FIRST.getPreviousOffset()),
                transitions,
                rules,
                new TimeSource<UnixTime>() {
                    @Override
                    public UnixTime currentTime() {
                        return PlainDate.of(1972, 6, 30).atStartOfDay().atUTC();
                    }
                },
                true,
                true);
        } else {
            return new CompositeTransitionModel(
                3,
                transitions,
                rules,
                SystemClock.INSTANCE,
                true,
                true);
        }
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