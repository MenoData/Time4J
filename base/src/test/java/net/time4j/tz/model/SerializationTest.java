package net.time4j.tz.model;

import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.Weekday;
import net.time4j.tz.Timezone;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class SerializationTest {

    @Test
    public void roundTripOfRuleBasedTransitionModel()
        throws IOException, ClassNotFoundException {

        RuleBasedTransitionModel model =
            CustomZoneTest.createModelOfEuropeanUnion();
        assertThat(model, is(roundtrip(model)));
        Timezone.of("rule-model", model).dump(System.out);
    }

    @Test
    public void roundTripOfArrayTransitionModel()
        throws IOException, ClassNotFoundException {

        ArrayTransitionModel model = CustomZoneTest.createArrayModel();
        assertThat(model, is(roundtrip(model)));
        Timezone.of("array-model", model).dump(System.out);
    }

    @Test
    public void roundTripOfCompositeTransitionModel()
        throws IOException, ClassNotFoundException {

        CompositeTransitionModel model = CustomZoneTest.createCompositeModel();
        assertThat(model, is(roundtrip(model)));
        Timezone.of("composite-model", model).dump(System.out);
    }

    @Test
    public void roundTripOfEireModel()
        throws IOException, ClassNotFoundException {

        CompositeTransitionModel model = EireZoneTest.createCompositeModel();
        assertThat(model, is(roundtrip(model)));
        Timezone.of("eire-model", model).dump(System.out);
    }

    @Test
    public void roundTripOfFixedDayPatternT24w0()
        throws IOException, ClassNotFoundException {

        DaylightSavingRule rule =
            GregorianTimezoneRule.ofFixedDay(
                Month.DECEMBER,
                31,
                PlainTime.of(24),
                OffsetIndicator.WALL_TIME,
                0);
        DaylightSavingRule copy = (DaylightSavingRule) roundtrip(rule);
        assertThat(rule, is(copy));
        assertThat(copy.getSavings(), is(0));
        assertThat(copy.getTimeOfDay(), is(PlainTime.of(24)));
        assertThat(copy.getIndicator(), is(OffsetIndicator.WALL_TIME));
        assertThat(copy.getDate(2012), is(PlainDate.of(2012, 12, 31)));

    }

    @Test
    public void roundTripOfFixedDayPatternT2330u1800()
        throws IOException, ClassNotFoundException {

        DaylightSavingRule rule =
            GregorianTimezoneRule.ofFixedDay(
                Month.JANUARY,
                1,
                PlainTime.of(23, 30),
                OffsetIndicator.UTC_TIME,
                1800);
        DaylightSavingRule copy = (DaylightSavingRule) roundtrip(rule);
        assertThat(rule, is(copy));
        assertThat(copy.getSavings(), is(1800));
        assertThat(copy.getTimeOfDay(), is(PlainTime.of(23, 30)));
        assertThat(copy.getIndicator(), is(OffsetIndicator.UTC_TIME));
        assertThat(copy.getDate(2012), is(PlainDate.of(2012, 1, 1)));
    }

    @Test
    public void roundTripOfFixedDayPatternT0s900()
        throws IOException, ClassNotFoundException {

        DaylightSavingRule rule =
            GregorianTimezoneRule.ofFixedDay(
                Month.FEBRUARY,
                28,
                PlainTime.of(0),
                OffsetIndicator.STANDARD_TIME,
                900);
        DaylightSavingRule copy = (DaylightSavingRule) roundtrip(rule);
        assertThat(rule, is(copy));
        assertThat(copy.getSavings(), is(900));
        assertThat(copy.getTimeOfDay(), is(PlainTime.of(0)));
        assertThat(copy.getIndicator(), is(OffsetIndicator.STANDARD_TIME));
        assertThat(copy.getDate(2012), is(PlainDate.of(2012, 2, 28)));
    }

    @Test
    public void roundTripOfLastDayOfWeekPatternT0230s3600()
        throws IOException, ClassNotFoundException {

        DaylightSavingRule rule =
            GregorianTimezoneRule.ofLastWeekday(
                Month.MARCH,
                Weekday.SUNDAY,
                PlainTime.of(2, 30),
                OffsetIndicator.STANDARD_TIME,
                3600);
        DaylightSavingRule copy = (DaylightSavingRule) roundtrip(rule);
        assertThat(rule, is(copy));
        assertThat(copy.getSavings(), is(3600));
        assertThat(copy.getTimeOfDay(), is(PlainTime.of(2, 30)));
        assertThat(copy.getIndicator(), is(OffsetIndicator.STANDARD_TIME));
        assertThat(copy.getDate(2012), is(PlainDate.of(2012, 3, 25)));
    }

    @Test
    public void roundTripOfLastDayOfWeekPatternT24s7200()
        throws IOException, ClassNotFoundException {

        DaylightSavingRule rule =
            GregorianTimezoneRule.ofLastWeekday(
                Month.JUNE,
                Weekday.SUNDAY,
                PlainTime.of(24),
                OffsetIndicator.WALL_TIME,
                7200);
        DaylightSavingRule copy = (DaylightSavingRule) roundtrip(rule);
        assertThat(rule, is(copy));
        assertThat(copy.getSavings(), is(7200));
        assertThat(copy.getTimeOfDay(), is(PlainTime.of(24)));
        assertThat(copy.getIndicator(), is(OffsetIndicator.WALL_TIME));
        assertThat(copy.getDate(2015), is(PlainDate.of(2015, 6, 28)));
    }

    @Test
    public void roundTripOfLastDayOfWeekPatternT09u30()
        throws IOException, ClassNotFoundException {

        DaylightSavingRule rule =
            GregorianTimezoneRule.ofLastWeekday(
                Month.APRIL,
                Weekday.FRIDAY,
                PlainTime.of(9),
                OffsetIndicator.UTC_TIME,
                30);
        DaylightSavingRule copy = (DaylightSavingRule) roundtrip(rule);
        assertThat(rule, is(copy));
        assertThat(copy.getSavings(), is(30));
        assertThat(copy.getTimeOfDay(), is(PlainTime.of(9)));
        assertThat(copy.getIndicator(), is(OffsetIndicator.UTC_TIME));
        assertThat(copy.getDate(2012), is(PlainDate.of(2012, 4, 27)));
    }

    @Test
    public void roundTripOfDayOfWeekInMonthT0230w1800AfterPattern()
        throws IOException, ClassNotFoundException {

        DaylightSavingRule rule =
            GregorianTimezoneRule.ofWeekdayAfterDate(
                Month.MARCH,
                1,
                Weekday.SUNDAY,
                PlainTime.of(2, 30),
                OffsetIndicator.WALL_TIME,
                1800);
        DaylightSavingRule copy = (DaylightSavingRule) roundtrip(rule);
        assertThat(rule, is(copy));
        assertThat(copy.getSavings(), is(1800));
        assertThat(copy.getTimeOfDay(), is(PlainTime.of(2, 30)));
        assertThat(copy.getIndicator(), is(OffsetIndicator.WALL_TIME));
        assertThat(copy.getDate(2012), is(PlainDate.of(2012, 3, 4)));
    }

    @Test
    public void roundTripOfDayOfWeekInMonthT24u30AfterPattern()
        throws IOException, ClassNotFoundException {

        DaylightSavingRule rule =
            GregorianTimezoneRule.ofWeekdayAfterDate(
                Month.MARCH,
                11,
                Weekday.SUNDAY,
                PlainTime.of(24),
                OffsetIndicator.UTC_TIME,
                30);
        DaylightSavingRule copy = (DaylightSavingRule) roundtrip(rule);
        assertThat(rule, is(copy));
        assertThat(copy.getSavings(), is(30));
        assertThat(copy.getTimeOfDay(), is(PlainTime.of(24)));
        assertThat(copy.getIndicator(), is(OffsetIndicator.UTC_TIME));
        assertThat(copy.getDate(2012), is(PlainDate.of(2012, 3, 11)));
    }

    @Test
    public void roundTripOfDayOfWeekInMonthT25u0AfterPattern()
        throws IOException, ClassNotFoundException {

        DaylightSavingRule rule =
            GregorianTimezoneRule.ofWeekdayAfterDate(
                Month.SEPTEMBER,
                8,
                Weekday.SATURDAY,
                25 * 3600,
                OffsetIndicator.UTC_TIME,
                0);
        DaylightSavingRule copy = (DaylightSavingRule) roundtrip(rule);
        assertThat(rule, is(copy));
        assertThat(copy.getSavings(), is(0));
        assertThat(copy.getDayOverflow(), is(1L));
        assertThat(copy.getTimeOfDay(), is(PlainTime.of(1)));
        assertThat(copy.getIndicator(), is(OffsetIndicator.UTC_TIME));
        assertThat(copy.getDate(1950), is(PlainDate.of(1950, 9, 10)));
    }

    @Test
    public void roundTripOfFixedDayTMinus6Pattern()
        throws IOException, ClassNotFoundException {

        DaylightSavingRule rule =
            GregorianTimezoneRule.ofFixedDay(
                Month.OCTOBER,
                15,
                -6 * 3600,
                OffsetIndicator.UTC_TIME,
                0);
        DaylightSavingRule copy = (DaylightSavingRule) roundtrip(rule);
        assertThat(rule, is(copy));
        assertThat(copy.getSavings(), is(0));
        assertThat(copy.getDayOverflow(), is(-1L));
        assertThat(copy.getTimeOfDay(), is(PlainTime.of(18)));
        assertThat(copy.getIndicator(), is(OffsetIndicator.UTC_TIME));
        assertThat(copy.getDate(1980), is(PlainDate.of(1980, 10, 14)));
    }

    @Test
    public void roundTripOfDayOfWeekInMonthT0230s3600BeforePattern()
        throws IOException, ClassNotFoundException {

        DaylightSavingRule rule =
            GregorianTimezoneRule.ofWeekdayBeforeDate(
                Month.MARCH,
                1,
                Weekday.SUNDAY,
                PlainTime.of(2, 30),
                OffsetIndicator.STANDARD_TIME,
                3600);
        DaylightSavingRule copy = (DaylightSavingRule) roundtrip(rule);
        assertThat(rule, is(copy));
        assertThat(copy.getSavings(), is(3600));
        assertThat(copy.getTimeOfDay(), is(PlainTime.of(2, 30)));
        assertThat(copy.getIndicator(), is(OffsetIndicator.STANDARD_TIME));
        assertThat(copy.getDate(2012), is(PlainDate.of(2012, 2, 26)));
    }

    @Test
    public void roundTripOfDayOfWeekInMonthT0s7200BeforePattern()
        throws IOException, ClassNotFoundException {

        DaylightSavingRule rule =
            GregorianTimezoneRule.ofWeekdayBeforeDate(
                Month.MARCH,
                1,
                Weekday.MONDAY,
                PlainTime.of(0),
                OffsetIndicator.STANDARD_TIME,
                7200);
        DaylightSavingRule copy = (DaylightSavingRule) roundtrip(rule);
        assertThat(rule, is(copy));
        assertThat(copy.getSavings(), is(7200));
        assertThat(copy.getTimeOfDay(), is(PlainTime.of(0)));
        assertThat(copy.getIndicator(), is(OffsetIndicator.STANDARD_TIME));
        assertThat(copy.getDate(2012), is(PlainDate.of(2012, 2, 27)));
    }

    @Test
    public void roundTripOfDayOfWeekInMonthT0215s3600AfterPattern()
        throws IOException, ClassNotFoundException {

        DaylightSavingRule rule =
            GregorianTimezoneRule.ofWeekdayAfterDate(
                Month.MARCH,
                31,
                Weekday.SUNDAY,
                PlainTime.of(2, 15),
                OffsetIndicator.STANDARD_TIME,
                3600);
        DaylightSavingRule copy = (DaylightSavingRule) roundtrip(rule);
        assertThat(rule, is(copy));
        assertThat(copy.getSavings(), is(3600));
        assertThat(copy.getTimeOfDay(), is(PlainTime.of(2, 15)));
        assertThat(copy.getIndicator(), is(OffsetIndicator.STANDARD_TIME));
        assertThat(copy.getDate(2012), is(PlainDate.of(2012, 4, 1)));
    }

    @Test
    public void serializeHistorizedTimezone()
        throws IOException, ClassNotFoundException {
        Object tz =
            Timezone.of(
                "RulesOfEU",
                CustomZoneTest.createModelOfEuropeanUnion());
        assertThat(roundtrip(tz), is(tz));
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

}