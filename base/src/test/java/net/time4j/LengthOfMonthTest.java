package net.time4j;

import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class LengthOfMonthTest {

 	@Parameterized.Parameters(name= "{index}: length-of-month({0})={1}")
 	public static Iterable<Object[]> data() {
 		return Arrays.asList(
            new Object[][] {
                {1, 31}, {2, 28}, {3, 31},
                {4, 30}, {5, 31}, {6, 30},
                {7, 31}, {8, 31}, {9, 30},
                {10, 31}, {11, 30}, {12, 31}
            }
        );
    }

    private int month;
    private int len;

    public LengthOfMonthTest(
        int month,
        int len
    ) {
        super();

        this.month = month;
        this.len = len;
    }

    @Test
    public void lengthOfMonth() {
        assertThat(
            PlainDate.of(2014, this.month, 1).lengthOfMonth(),
            is(this.len));
        if (this.month == 2) {
            assertThat(
                PlainDate.of(2012, this.month, 1).lengthOfMonth(),
                is(29));
        }
    }

}