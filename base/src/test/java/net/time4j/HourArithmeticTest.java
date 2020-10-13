package net.time4j;

import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class HourArithmeticTest {

 	@Parameters(name= "{index}: [T{0}]+1hour=[T{1}]")
 	public static Iterable<Object[]> data() {
 		return Arrays.asList(
            new Object[][] {
                {0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}, {5, 6}, {6, 7}, {7, 8},
                {8, 9}, {9, 10}, {10, 11}, {11, 12}, {12, 13}, {13, 14},
                {14, 15}, {15, 16}, {16, 17}, {17, 18}, {18, 19}, {19, 20},
                {20, 21}, {21, 22}, {22, 23}, {23, 24}
            }
        );
    }

    private int hour;
    private int expected;

    public HourArithmeticTest(
        int hour,
        int expected
    ) {
        super();

        this.hour = hour;
        this.expected = expected;
    }

    @Test
    public void plusOneHour() {
        assertThat(
            PlainTime.of(this.hour).plus(1, ClockUnit.HOURS),
            is(PlainTime.of(this.expected)));
    }

    @Test
    public void minusOneHour() {
        assertThat(
            PlainTime.of(this.expected).minus(1, ClockUnit.HOURS),
            is(PlainTime.of(this.hour)));
    }

    @Test
    public void plus25Hours() {
        assertThat(
            PlainTime.of(this.hour).plus(25, ClockUnit.HOURS),
            is(PlainTime.of(this.expected)));
    }

    @Test
    public void minus25Hours() {
        assertThat(
            PlainTime.of(this.expected).minus(25, ClockUnit.HOURS),
            is(PlainTime.of(this.hour)));
    }

    @Test
    public void hoursBetween() {
        assertThat(
            ClockUnit.HOURS.between(
                PlainTime.of(this.hour),
                PlainTime.of(this.expected)
            ),
            is(1L));
        assertThat(
            ClockUnit.HOURS.between(
                PlainTime.of(this.expected),
                PlainTime.of(this.hour)
            ),
            is(-1L));
    }

}
