package net.time4j;

import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class MonthRangeArithmeticTest {

 	@Parameters(name= "{index}: [2011-01-31].plus-{0}-days=2011-{1}-{2}")
 	public static Iterable<Object[]> data() {
 		return Arrays.asList(
            new Object[][] {
                {1, 2, 1}, {2, 2, 2}, {3, 2, 3}, {4, 2, 4}, {5, 2, 5},
                {6, 2, 6}, {7, 2, 7}, {8, 2, 8}, {9, 2, 9}, {10, 2, 10},
                {11, 2, 11}, {12, 2, 12}, {13, 2, 13}, {14, 2, 14}, {15, 2, 15},
                {16, 2, 16}, {17, 2, 17}, {18, 2, 18}, {19, 2, 19}, {20, 2, 20},
                {21, 2, 21}, {22, 2, 22}, {23, 2, 23}, {24, 2, 24}, {25, 2, 25},
                {26, 2, 26}, {27, 2, 27}, {28, 2, 28}, {29, 3, 1}, {30, 3, 2}
            }
        );
    }

    private int days;
    private int month;
    private int dom;

    public MonthRangeArithmeticTest(
        int days,
        int month,
        int dom
    ) {
        super();

        this.days = days;
        this.month = month;
        this.dom = dom;
    }

    @Test
    public void plusDayOfMonth() {
        assertThat(
            PlainDate.of(2011, 1, 31).plus(this.days, CalendarUnit.DAYS),
            is(PlainDate.of(2011, this.month, this.dom)));
        assertThat(
            PlainDate.of(2011, 1, 31)
                .plus(1, CalendarUnit.DAYS)
                .plus(this.days - 1, CalendarUnit.DAYS),
            is(PlainDate.of(2011, this.month, this.dom)));
    }

}