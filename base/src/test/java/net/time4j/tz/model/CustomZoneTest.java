package net.time4j.tz.model;

import net.time4j.CalendarUnit;
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.SystemClock;
import net.time4j.Weekday;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;
import net.time4j.tz.ZonalTransition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class CustomZoneTest {

    @Test
    public void customizedRuleBasedTransitionModel()
        throws IOException {

        RuleBasedTransitionModel model = createModelOfEuropeanUnion();
        String tzid = "custom~rule-model";
        Timezone tz = Timezone.of(tzid, model);
        assertThat(tz.getID().canonical(), is(tzid));
        tz.dump(System.out);
    }

    @Test
    public void customizedArrayTransitionModel()
        throws IOException {

        ArrayTransitionModel model = createArrayModel();
        String tzid = "custom~array-model";
        Timezone tz = Timezone.of(tzid, model);
        assertThat(tz.getID().canonical(), is(tzid));
        tz.dump(System.out);
    }

    @Test
    public void customizedCompositeTransitionModel()
        throws IOException {

        CompositeTransitionModel model = createCompositeModel();
        String tzid = "custom~composite-model";
        Timezone tz = Timezone.of(tzid, model);
        assertThat(tz.getID().canonical(), is(tzid));
        assertThat(model.hasNegativeDST(), is(false));
        assertThat(tz.isDaylightSaving(PlainTimestamp.of(1971, 1, 1, 0, 0).atUTC()), is(false));
        tz.dump(System.out);
    }

    @Test(expected=IllegalArgumentException.class)
    public void customizedOffsetModel_GMT() {

        // combination of fixed offset and variable zone
        RuleBasedTransitionModel model = createModelOfEuropeanUnion();
        String tzid = "GMT";
        Timezone.of(tzid, model);
    }

    @Test(expected=IllegalArgumentException.class)
    public void customizedOffsetModel_GMT_02() {

        // combination of fixed offset and variable zone
        RuleBasedTransitionModel model = createModelOfEuropeanUnion();
        String tzid = "GMT+02:00";
        Timezone.of(tzid, model);
    }

    @Test(expected=IllegalArgumentException.class)
    public void customizedOffsetModel_UTC_05_30() {

        // combination of fixed offset and variable zone
        RuleBasedTransitionModel model = createModelOfEuropeanUnion();
        String tzid = "UTC+05:30";
        Timezone.of(tzid, model);
    }

    // auch für SerializationTest
    static RuleBasedTransitionModel createModelOfEuropeanUnion() {
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

    // auch für SerializationTest
    static ArrayTransitionModel createArrayModel() {
        ZonalTransition first =
            new ZonalTransition(7L, 1800, 7200, 3600);
        ZonalTransition second =
            new ZonalTransition(365 * 86400L, 7200, 3600, 3600);
        ZonalTransition third =
            new ZonalTransition(2 * 365 * 86400L, 3600, -14 * 3600, 0);
        ZonalTransition fourth =
            new ZonalTransition(
                SystemClock.INSTANCE.currentTime().getPosixTime()
                    + 2 *365 * 86400L,
                -14 * 3600,
                -14 * 3600 + 1800,
                3600);
        return new ArrayTransitionModel(
            Arrays.asList(fourth, first, third, second));
    }

    // auch für SerializationTest
    static CompositeTransitionModel createCompositeModel() {
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

        ZonalTransition first =
            new ZonalTransition(0L, 3600, 7200, 3600); // 1970-01-01
        ZonalTransition second =
            new ZonalTransition(365 * 86400L, 7200, 3600, 0); // 1971-01-01
        ZonalTransition third =
            new ZonalTransition(63072000L, 3600, 7200, 3600); // 1972-01-01
        ZonalTransition fourth =
            new ZonalTransition(
                SystemClock.currentMoment().toZonalTimestamp(ZonalOffset.UTC)
                        .with(PlainDate.DAY_OF_YEAR, 1).plus(2, CalendarUnit.YEARS).atUTC().getPosixTime(),
                7200,
                3600,
                0);
        return new CompositeTransitionModel(
            4,
            Arrays.asList(fourth, first, third, second),
            rules,
            true,
            true);
    }

}