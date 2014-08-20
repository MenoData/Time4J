package net.time4j;

import net.time4j.engine.RuleNotFoundException;

import java.text.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class ComponentElementTest {

    @Test
    public void dateComponent() {
        PlainTimestamp tsp = PlainTimestamp.of(2014, 8, 21, 14, 30);
        tsp = tsp.with(PlainDate.COMPONENT, PlainDate.of(2015, 1, 1));
        assertThat(tsp, is(PlainTimestamp.of(2015, 1, 1, 14, 30)));
    }

    @Test
    public void timeComponent() throws ParseException {
        PlainTimestamp tsp =
          PlainTimestamp.localFormatter("uuuu-MM-dd", PatternType.CLDR)
            .withDefault(PlainTime.COMPONENT, PlainTime.midnightAtEndOfDay())
            .parse("2014-08-20");
        assertThat(tsp, is(PlainTimestamp.of(2014, 8, 21, 0, 0)));
    }

    @Test(expected=RuleNotFoundException.class)
    public void dateFromMoment() {
        Moment moment = Moment.UNIX_EPOCH;
        moment.get(PlainDate.COMPONENT);
    }

    @Test(expected=RuleNotFoundException.class)
    public void timeFromMoment() {
        Moment moment = Moment.UNIX_EPOCH;
        moment.get(PlainTime.COMPONENT);
    }

}