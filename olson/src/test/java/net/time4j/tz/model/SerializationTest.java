package net.time4j.tz.model;

import net.time4j.Month;
import net.time4j.PlainTime;
import net.time4j.SystemClock;
import net.time4j.Weekday;
import net.time4j.tz.ZonalOffset;
import net.time4j.tz.ZonalTransition;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class SerializationTest {

    @Test
    public void roundTripOfRuleBasedTransitionModel()
        throws IOException, ClassNotFoundException {

        RuleBasedTransitionModel model = createModelOfEuropeanUnion();
        assertThat(model, is(roundtrip(model)));
    }

    @Test
    public void roundTripOfArrayTransitionModel()
        throws IOException, ClassNotFoundException {

        ArrayTransitionModel model = createArrayModel();
        assertThat(model, is(roundtrip(model)));
    }

    @Test
    public void roundTripOfFixedDayPattern()
        throws IOException, ClassNotFoundException {

        DaylightSavingRule rule =
            DaylightSavingRule.ofFixedDay(
                Month.DECEMBER,
                31,
                PlainTime.of(24),
                OffsetIndicator.WALL_TIME,
                0);
        assertThat(rule, is(roundtrip(rule)));
    }

    @Test
    public void roundTripOfLastDayOfWeekPattern()
        throws IOException, ClassNotFoundException {

        DaylightSavingRule rule =
            DaylightSavingRule.ofLastWeekday(
                Month.MARCH,
                Weekday.SUNDAY,
                PlainTime.of(2, 30),
                OffsetIndicator.STANDARD_TIME,
                3600);
        assertThat(rule, is(roundtrip(rule)));
    }

    @Test
    public void roundTripOfDayOfWeekInMonthAfterPattern()
        throws IOException, ClassNotFoundException {

        DaylightSavingRule rule =
            DaylightSavingRule.ofWeekdayAfterDate(
                Month.MARCH,
                1,
                Weekday.SUNDAY,
                PlainTime.of(2, 30),
                OffsetIndicator.WALL_TIME,
                3600);
        assertThat(rule, is(roundtrip(rule)));
    }

    @Test
    public void roundTripOfDayOfWeekInMonthBeforePattern()
        throws IOException, ClassNotFoundException {

        DaylightSavingRule rule =
            DaylightSavingRule.ofWeekdayBeforeDate(
                Month.MARCH,
                1,
                Weekday.SUNDAY,
                PlainTime.of(2, 30),
                OffsetIndicator.WALL_TIME,
                3600);
        assertThat(rule, is(roundtrip(rule)));
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

        return new RuleBasedTransitionModel(
            ZonalOffset.ofTotalSeconds(3600),
            rules);
    }

    private static ArrayTransitionModel createArrayModel() {
        ZonalTransition first =
            new ZonalTransition(0L, 1800, 7200, 3600);
        ZonalTransition second =
            new ZonalTransition(365 * 86400L, 7200, 3600, 3600);
        ZonalTransition third =
            new ZonalTransition(2 * 365 * 86400L, 3600, -14 * 3600, 0);
        ZonalTransition fourth =
            new ZonalTransition(
                SystemClock.INSTANCE.currentTime().getPosixTime()
                    + 2 *365 * 86400L,
                -14 * 3600,
                -14 * 3600 + 3600,
                3600);
        return new ArrayTransitionModel(
            Arrays.asList(fourth, first, third, second));
    }

}