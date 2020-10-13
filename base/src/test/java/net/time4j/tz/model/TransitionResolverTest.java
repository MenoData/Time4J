package net.time4j.tz.model;

import net.time4j.Moment;
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.Weekday;
import net.time4j.tz.GapResolver;
import net.time4j.tz.OverlapResolver;
import net.time4j.tz.Timezone;
import net.time4j.tz.TransitionStrategy;
import net.time4j.tz.ZonalOffset;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class TransitionResolverTest {

    @Test
    public void gap_PUSH_FORWARD() {
        TransitionStrategy strategy =
            GapResolver.PUSH_FORWARD.and(OverlapResolver.LATER_OFFSET);
        Timezone tz =
            Timezone
                .of("RulesOfEU", createModelOfEuropeanUnion())
                .with(strategy);
        PlainDate date = PlainDate.of(2015, 3, 29);
        PlainTime time = PlainTime.of(2, 30);
        PlainTimestamp tsp = PlainTimestamp.of(date, time);
        ZonalOffset offset = ZonalOffset.parse("UTC+02:00");
        Moment expected =
            PlainTimestamp.of(date, PlainTime.of(3, 30)).at(offset);
        assertThat(tsp.in(tz), is(expected));
        assertThat(strategy.getOffset(date, time, tz), is(offset));
    }

    @Test
    public void gap_NEXT_VALID_TIME() {
        TransitionStrategy strategy =
            GapResolver.NEXT_VALID_TIME.and(OverlapResolver.LATER_OFFSET);
        Timezone tz =
            Timezone
                .of("RulesOfEU", createModelOfEuropeanUnion())
                .with(strategy);
        PlainDate date = PlainDate.of(2015, 3, 29);
        PlainTime time = PlainTime.of(2, 30);
        PlainTimestamp tsp = PlainTimestamp.of(date, time);
        ZonalOffset offset = ZonalOffset.parse("UTC+02:00");
        Moment expected =
            PlainTimestamp.of(date, PlainTime.of(3, 0)).at(offset);
        assertThat(tsp.in(tz), is(expected));
        assertThat(strategy.getOffset(date, time, tz), is(offset));
    }

    @Test(expected=IllegalArgumentException.class)
    public void gap_resolve_ABORT() {
        TransitionStrategy strategy =
            GapResolver.ABORT.and(OverlapResolver.LATER_OFFSET);
        Timezone tz =
            Timezone
                .of("RulesOfEU", createModelOfEuropeanUnion())
                .with(strategy);
        PlainDate date = PlainDate.of(2015, 3, 29);
        PlainTime time = PlainTime.of(2, 30);
        PlainTimestamp tsp = PlainTimestamp.of(date, time);
        tsp.in(tz);
    }

    @Test(expected=IllegalArgumentException.class)
    public void gap_getOffset_ABORT() {
        TransitionStrategy strategy =
            GapResolver.ABORT.and(OverlapResolver.LATER_OFFSET);
        Timezone tz =
            Timezone
                .of("RulesOfEU", createModelOfEuropeanUnion())
                .with(strategy);
        PlainDate date = PlainDate.of(2015, 3, 29);
        PlainTime time = PlainTime.of(2, 30);
        strategy.getOffset(date, time, tz);
    }

    @Test
    public void overlap_LATER_OFFSET() {
        TransitionStrategy strategy =
            GapResolver.PUSH_FORWARD.and(OverlapResolver.LATER_OFFSET);
        Timezone tz =
            Timezone
                .of("RulesOfEU", createModelOfEuropeanUnion())
                .with(strategy);
        PlainDate date = PlainDate.of(2015, 10, 25);
        PlainTime time = PlainTime.of(2, 30);
        PlainTimestamp tsp = PlainTimestamp.of(date, time);
        ZonalOffset offset = ZonalOffset.parse("UTC+01:00");
        Moment expected =
            PlainTimestamp.of(date, PlainTime.of(2, 30)).at(offset);
        assertThat(tsp.in(tz), is(expected));
        assertThat(strategy.getOffset(date, time, tz), is(offset));
    }

    @Test
    public void overlap_EARLIER_OFFSET() {
        TransitionStrategy strategy =
            GapResolver.PUSH_FORWARD.and(OverlapResolver.EARLIER_OFFSET);
        Timezone tz =
            Timezone
                .of("RulesOfEU", createModelOfEuropeanUnion())
                .with(strategy);
        PlainDate date = PlainDate.of(2015, 10, 25);
        PlainTime time = PlainTime.of(2, 30);
        PlainTimestamp tsp = PlainTimestamp.of(date, time);
        ZonalOffset offset = ZonalOffset.parse("UTC+02:00");
        Moment expected =
            PlainTimestamp.of(date, PlainTime.of(2, 30)).at(offset);
        assertThat(tsp.in(tz), is(expected));
        assertThat(strategy.getOffset(date, time, tz), is(offset));
    }

    @Test
    public void serialize_PUSH_FORWARD_and_LATER_OFFSET()
        throws IOException, ClassNotFoundException {
        Object strategy =
            GapResolver.PUSH_FORWARD.and(OverlapResolver.LATER_OFFSET);
        assertThat(roundtrip(strategy), is(strategy));
    }

    @Test
    public void serialize_PUSH_FORWARD_and_EARLIER_OFFSET()
        throws IOException, ClassNotFoundException {
        Object strategy =
            GapResolver.PUSH_FORWARD.and(OverlapResolver.EARLIER_OFFSET);
        assertThat(roundtrip(strategy), is(strategy));
    }

    @Test
    public void serialize_NEXT_VALID_TIME_and_LATER_OFFSET()
        throws IOException, ClassNotFoundException {
        Object strategy =
            GapResolver.NEXT_VALID_TIME.and(OverlapResolver.LATER_OFFSET);
        assertThat(roundtrip(strategy), is(strategy));
    }

    @Test
    public void serialize_NEXT_VALID_TIME_and_EARLIER_OFFSET()
        throws IOException, ClassNotFoundException {
        Object strategy =
            GapResolver.NEXT_VALID_TIME.and(OverlapResolver.EARLIER_OFFSET);
        assertThat(roundtrip(strategy), is(strategy));
    }

    @Test
    public void serialize_ABORT_and_LATER_OFFSET()
        throws IOException, ClassNotFoundException {
        Object strategy =
            GapResolver.ABORT.and(OverlapResolver.LATER_OFFSET);
        assertThat(roundtrip(strategy), is(strategy));
    }

    @Test
    public void serialize_ABORT_and_EARLIER_OFFSET()
        throws IOException, ClassNotFoundException {
        Object strategy =
            GapResolver.ABORT.and(OverlapResolver.EARLIER_OFFSET);
        assertThat(roundtrip(strategy), is(strategy));
    }

    private static Object roundtrip(Object obj)
        throws IOException, ClassNotFoundException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        byte[] data = baos.toByteArray();
        oos.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object ser = ois.readObject();
        ois.close();
        return ser;
    }

    private static RuleBasedTransitionModel createModelOfEuropeanUnion() {
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

}