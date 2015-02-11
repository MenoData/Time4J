package net.time4j.tz.model;

import net.time4j.Month;
import net.time4j.PlainTime;
import net.time4j.SystemClock;
import net.time4j.Weekday;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;
import net.time4j.tz.ZonalTransition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class CustomZoneTest {

    @Test
    public void customizedRuleBasedTransitionModel()
        throws IOException, ClassNotFoundException {

        RuleBasedTransitionModel model = createModelOfEuropeanUnion();
        String tzid = "custom~rule-model";
        Timezone tz = Timezone.of(tzid, model);
        assertThat(tz.getID().canonical(), is(tzid));
        tz.dump(System.out);
    }

    @Test
    public void customizedArrayTransitionModel()
        throws IOException, ClassNotFoundException {

        ArrayTransitionModel model = createArrayModel();
        String tzid = "custom~array-model";
        Timezone tz = Timezone.of(tzid, model);
        assertThat(tz.getID().canonical(), is(tzid));
        tz.dump(System.out);
    }

    @Test
    public void customizedCompositeTransitionModel()
        throws IOException, ClassNotFoundException {

        CompositeTransitionModel model = createCompositeModel();
        String tzid = "custom~composite-model";
        Timezone tz = Timezone.of(tzid, model);
        assertThat(tz.getID().canonical(), is(tzid));
        tz.dump(System.out);
    }

    @Test(expected=IllegalArgumentException.class)
    public void customizedOffsetModel_GMT()
        throws IOException, ClassNotFoundException {

        // combination of fixed offset and variable zone
        RuleBasedTransitionModel model = createModelOfEuropeanUnion();
        String tzid = "GMT";
        Timezone.of(tzid, model);
    }

    @Test(expected=IllegalArgumentException.class)
    public void customizedOffsetModel_GMT_02()
        throws IOException, ClassNotFoundException {

        // combination of fixed offset and variable zone
        RuleBasedTransitionModel model = createModelOfEuropeanUnion();
        String tzid = "GMT+02:00";
        Timezone.of(tzid, model);
    }

    @Test(expected=IllegalArgumentException.class)
    public void customizedOffsetModel_UTC_05_30()
        throws IOException, ClassNotFoundException {

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

        List<DaylightSavingRule> rules = new ArrayList<DaylightSavingRule>();
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
        List<DaylightSavingRule> rules = new ArrayList<DaylightSavingRule>();
        rules.add(autumn);
        rules.add(spring);

        ZonalTransition first =
            new ZonalTransition(0L, 3600, 7200, 3600);
        ZonalTransition second =
            new ZonalTransition(365 * 86400L, 7200, 3600, 0);
        ZonalTransition third =
            new ZonalTransition(63072000L, 3600, 7200, 3600);
        ZonalTransition fourth =
            new ZonalTransition(
                SystemClock.INSTANCE.currentTime().getPosixTime() + 63072000L,
                7200,
                3600,
                0);
        return new CompositeTransitionModel(
            4,
            Arrays.asList(fourth, first, third, second),
            rules,
            SystemClock.INSTANCE,
            true,
            true);
    }

}