package net.time4j.tz.threeten;

import net.time4j.CalendarUnit;
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.SystemClock;
import net.time4j.Weekday;
import net.time4j.tz.TransitionHistory;
import net.time4j.tz.ZonalOffset;
import net.time4j.tz.ZonalTransition;
import net.time4j.tz.model.DaylightSavingRule;
import net.time4j.tz.model.GregorianTimezoneRule;
import net.time4j.tz.model.OffsetIndicator;
import net.time4j.tz.model.TransitionModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class NegativeDayOfMonthPatternTest {

    @Test
    public void roundTripOfNegativeDayOfMonthPattern1()
        throws IOException, ClassNotFoundException {

        DaylightSavingRule rule =
            new NegativeDayOfMonthPattern(
                Month.DECEMBER,
                -5,
                Weekday.SUNDAY,
                PlainTime.of(24),
                OffsetIndicator.WALL_TIME,
                0);
        DaylightSavingRule copy = (DaylightSavingRule) roundtrip(rule);
        assertThat(rule, is(copy));
        assertThat(copy.getSavings(), is(0));
        assertThat(copy.getTimeOfDay(), is(PlainTime.of(24)));
        assertThat(copy.getIndicator(), is(OffsetIndicator.WALL_TIME));
        assertThat(copy.getDate(2015), is(PlainDate.of(2015, 12, 27)));
    }

    @Test
    public void roundTripOfNegativeDayOfMonthPattern2()
        throws IOException, ClassNotFoundException {

        DaylightSavingRule rule =
            new NegativeDayOfMonthPattern(
                Month.DECEMBER,
                -6,
                Weekday.SUNDAY,
                PlainTime.of(1, 30),
                OffsetIndicator.STANDARD_TIME,
                3600);
        DaylightSavingRule copy = (DaylightSavingRule) roundtrip(rule);
        assertThat(rule, is(copy));
        assertThat(copy.getSavings(), is(3600));
        assertThat(copy.getTimeOfDay(), is(PlainTime.of(1, 30)));
        assertThat(copy.getIndicator(), is(OffsetIndicator.STANDARD_TIME));
        assertThat(copy.getDate(2015), is(PlainDate.of(2015, 12, 20)));
    }

    @Test
    public void roundTripOfCustomModelWithNegativeDomPattern()
        throws IOException, ClassNotFoundException {

        Object model = createModel();
        Object copy = roundtrip(model);
        assertThat(model, is(copy));
    }

    private static TransitionHistory createModel() {
        DaylightSavingRule spring =
            GregorianTimezoneRule.ofLastWeekday(
                Month.MARCH,
                Weekday.SUNDAY,
                PlainTime.of(1),
                OffsetIndicator.UTC_TIME,
                3600);
        DaylightSavingRule autumn =
            new NegativeDayOfMonthPattern(
                Month.OCTOBER,
                -2,
                Weekday.SUNDAY,
                PlainTime.of(1, 30),
                OffsetIndicator.STANDARD_TIME,
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
        return TransitionModel.of(
            ZonalOffset.ofTotalSeconds(3600),
            Arrays.asList(fourth, first, third, second),
            rules);
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