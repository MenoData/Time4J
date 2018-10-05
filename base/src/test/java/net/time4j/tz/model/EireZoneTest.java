package net.time4j.tz.model;

import net.time4j.Moment;
import net.time4j.Month;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.Weekday;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalTransition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class EireZoneTest {

    @Test
    public void handlingNegativeDST() {

        CompositeTransitionModel model = createCompositeModel();
        String tzid = "eire-model";
        Timezone tz = Timezone.of(tzid, model);

        assertThat(
            tz.getID().canonical(), is(tzid));
        assertThat(
            tz.getStandardOffset(PlainTimestamp.of(1968, 1, 1, 0, 0).atUTC()).getIntegralAmount(), is(0));
        assertThat(
            tz.getDaylightSavingOffset(PlainTimestamp.of(1968, 1, 1, 0, 0).atUTC()).getIntegralAmount(), is(0));
        assertThat(
            tz.isDaylightSaving(PlainTimestamp.of(1968, 1, 1, 0, 0).atUTC()), is(false));
        assertThat(
            tz.getStandardOffset(PlainTimestamp.of(1968, 4, 1, 0, 0).atUTC()).getIntegralAmount(), is(0));
        assertThat(
            tz.getDaylightSavingOffset(PlainTimestamp.of(1968, 4, 1, 0, 0).atUTC()).getIntegralAmount(), is(3600));
        assertThat(
            tz.isDaylightSaving(PlainTimestamp.of(1968, 4, 1, 0, 0).atUTC()), is(true));
        assertThat(
            tz.getStandardOffset(Moment.UNIX_EPOCH).getIntegralAmount(), is(3600));
        assertThat(
            tz.getDaylightSavingOffset(Moment.UNIX_EPOCH).getIntegralAmount(), is(0));
        assertThat(
            tz.isDaylightSaving(Moment.UNIX_EPOCH), is(true));
        assertThat(
            tz.getStandardOffset(PlainTimestamp.of(1972, 4, 1, 0, 0).atUTC()).getIntegralAmount(), is(3600));
        assertThat(
            tz.getDaylightSavingOffset(PlainTimestamp.of(1972, 4, 1, 0, 0).atUTC()).getIntegralAmount(), is(0));
        assertThat(
            tz.isDaylightSaving(PlainTimestamp.of(1972, 4, 1, 0, 0).atUTC()), is(true));
        assertThat(
            tz.getStandardOffset(PlainTimestamp.of(1972, 11, 1, 0, 0).atUTC()).getIntegralAmount(), is(3600));
        assertThat(
            tz.getDaylightSavingOffset(PlainTimestamp.of(1972, 11, 1, 0, 0).atUTC()).getIntegralAmount(), is(-3600));
        assertThat(
            tz.isDaylightSaving(PlainTimestamp.of(1972, 11, 1, 0, 0).atUTC()), is(false));
        assertThat(
            tz.getStandardOffset(PlainTimestamp.of(1973, 4, 1, 0, 0).atUTC()).getIntegralAmount(), is(3600));
        assertThat(
            tz.getDaylightSavingOffset(PlainTimestamp.of(1973, 4, 1, 0, 0).atUTC()).getIntegralAmount(), is(0));
        assertThat(
            tz.isDaylightSaving(PlainTimestamp.of(1973, 4, 1, 0, 0).atUTC()), is(true));
    }

    // auch für SerializationTest
    static CompositeTransitionModel createCompositeModel() {
        DaylightSavingRule spring =
            GregorianTimezoneRule.ofWeekdayAfterDate(
                Month.MARCH,
                16,
                Weekday.SUNDAY,
                PlainTime.of(2),
                OffsetIndicator.UTC_TIME,
                Integer.MAX_VALUE);
        DaylightSavingRule autumn =
            GregorianTimezoneRule.ofFixedDay(
                Month.OCTOBER,
                31,
                PlainTime.of(2),
                OffsetIndicator.UTC_TIME,
                -3600);
        List<DaylightSavingRule> rules = new ArrayList<>();
        rules.add(autumn);
        rules.add(spring);

        ZonalTransition first =
            new ZonalTransition(PlainTimestamp.of(1968, 2, 18, 2, 0).atUTC().getPosixTime(), 0, 3600, 3600);
        ZonalTransition second =
            new ZonalTransition(PlainTimestamp.of(1968, 10, 27, 0, 0).atUTC().getPosixTime(), 3600, 3600, 0);
        ZonalTransition third =
            new ZonalTransition(PlainTimestamp.of(1971, 10, 31, 2, 0).atUTC().getPosixTime(), 3600, 0, -3600);
        return new CompositeTransitionModel(
            3,
            Arrays.asList(first, second, third),
            rules,
            true,
            true);
    }

}