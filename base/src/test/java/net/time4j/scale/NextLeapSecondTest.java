package net.time4j.scale;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;
import net.time4j.SI;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class NextLeapSecondTest {

    @Test
    public void nextLS2012() {
        Moment ref = PlainDate.of(2012, 1, 1).atTime(0, 0).atUTC();
        if (LeapSeconds.getInstance().isEnabled()) {
            Moment expected =
                PlainDate.of(2012, 6, 30).atTime(23, 59, 59).atUTC()
                    .plus(1, SI.SECONDS);
            assertThat(ref.with(Moment.nextLeapSecond()), is(expected));
        } else {
            assertThat(ref.with(Moment.nextLeapSecond()), is(nullValue()));
        }
    }

    @Test
    public void nextLSIfKnown() {
        Moment ref = PlainDate.of(2200, 1, 1).atTime(0, 0).atUTC();

        if (LeapSeconds.getInstance().isEnabled()) {
            LeapSeconds.getInstance().registerPositiveLS(2215, 12, 31);
            assertThat(
                ref.with(Moment.nextLeapSecond()),
                is(
                    PlainTimestamp.of(2215, 12, 31, 23, 59, 59).atUTC()
                    .plus(1, SI.SECONDS)));
         } else {
            assertThat(ref.with(Moment.nextLeapSecond()), is(nullValue()));
       }
    }

    @Test
    public void nextLSIfUnknown() {
        Moment ref = PlainDate.of(2300, 1, 1).atTime(0, 0).atUTC();
        assertThat(
            ref.with(Moment.nextLeapSecond()),
            is(nullValue()));
    }

}
