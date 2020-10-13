package net.time4j;

import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class LeapYearTest {

 	@Parameters(name= "{index}: leap-year({0})={1}")
 	public static Iterable<Object[]> data() {
 		return Arrays.asList(
            new Object[][] {
                {2000, true}, {1900, false}, {2001, false},
                {2002, false}, {2003, false}, {2004, true},
                {0, true}, {-1, false}, {-2, false},
                {-3, false}, {-4, true}, {2100, false},
                {2012, true}, {2016, true}, {2017, false},
                {2013, false}, {2014, false}, {2015, false},
                {1600, true}, {1700, false}, {1800, false}
            }
        );
    }

    private int year;
    private boolean leap;

    public LeapYearTest(
        int year,
        boolean leap
    ) {
        super();

        this.year = year;
        this.leap = leap;
    }

    @Test
    public void isLeapYear() {
        assertThat(PlainDate.of(this.year, 1, 1).isLeapYear(), is(this.leap));
    }

}
