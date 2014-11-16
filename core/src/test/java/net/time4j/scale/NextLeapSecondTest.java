package net.time4j.scale;

import net.time4j.Moment;
import net.time4j.PlainTimestamp;
import net.time4j.SI;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

// TODO: needs adjustment if any new leap second is announced
@RunWith(JUnit4.class)
public class NextLeapSecondTest {

    @Test
    public void nextLS() {
        if (LeapSeconds.getInstance().getCount() <= 25) {
            assertThat(
                Moment.nextLeapSecond(),
                nullValue());
        } else {
            System.out.println("Warning: NextLeapSecondTest not up-to-date.");
        }
    }

    @Test
    public void nextLSIfKnown() {
        if (LeapSeconds.getInstance().getCount() <= 25) {
            LeapSeconds.getInstance().registerPositiveLS(2115, 12, 31);
            assertThat(
                Moment.nextLeapSecond(),
                is(
                    PlainTimestamp.of(2115, 12, 31, 23, 59, 59).atUTC()
                    .plus(1, SI.SECONDS)));
        } else {
            System.out.println("Warning: NextLeapSecondTest not up-to-date.");
        }
    }

}