package net.time4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class OrdinalWeekdayOperatorTest {

 	@Parameters(name= "{index}: PlainDate.of(2013, {1}, {2}) => {0}")
 	public static Iterable<Object[]> data() {
 		return Arrays.asList(
            new Object[][] {
                {1, 4, 1, 4, 1}, {1, 1, 1, 1, 7}, {1, 5, 1, 5, 6},
                {1, 8, 1, 8, 5}, {1, 3, 1, 3, 4}, {1, 6, 1, 6, 3},
                {1, 9, 1, 9, 2}, {2, 4, 1, 4, 2}, {2, 1, 1, 1, 1},
                {2, 5, 1, 5, 7}, {2, 8, 1, 8, 6}, {2, 3, 1, 3, 5},
                {2, 6, 1, 6, 4}, {2, 9, 1, 9, 3}, {3, 4, 1, 4, 3},
                {3, 1, 1, 1, 2}, {3, 5, 1, 5, 1}, {3, 8, 1, 8, 7},
                {3, 3, 1, 3, 6}, {3, 6, 1, 6, 5}, {3, 9, 1, 9, 4},
                {4, 4, 1, 4, 4}, {4, 1, 1, 1, 3}, {4, 5, 1, 5, 2},
                {4, 8, 1, 8, 1}, {4, 3, 1, 3, 7}, {4, 6, 1, 6, 6},
                {4, 9, 1, 9, 5}, {5, 4, 1, 4, 5}, {5, 1, 1, 1, 4},
                {5, 5, 1, 5, 3}, {5, 8, 1, 8, 2}, {5, 3, 1, 3, 1},
                {5, 6, 1, 6, 7}, {5, 9, 1, 9, 6}, {6, 4, 1, 4, 6},
                {6, 1, 1, 1, 5}, {6, 5, 1, 5, 4}, {6, 8, 1, 8, 3},
                {6, 3, 1, 3, 2}, {6, 6, 1, 6, 1}, {6, 9, 1, 9, 7},
                {7, 4, 1, 4, 7}, {7, 1, 1, 1, 6}, {7, 5, 1, 5, 5},
                {7, 8, 1, 8, 4}, {7, 3, 1, 3, 3}, {7, 6, 1, 6, 2},
                {7, 9, 1, 9, 1}
            }
        );
    }

    private Weekday dow;
    private int month;
    private int dom;
    private int expectedMonth;
    private int expectedDom;

    public OrdinalWeekdayOperatorTest(
        int wd,
        int month,
        int dom,
        int expectedMonth,
        int expectedDom
    ) {
        super();

        this.dow = Weekday.valueOf(wd);
        this.month = month;
        this.dom = dom;
        this.expectedMonth = expectedMonth;
        this.expectedDom = expectedDom;

    }

    @Test
    public void setToFirst() {
        PlainDate date = PlainDate.of(2013, this.month, this.dom);
        assertThat(
            date.with(PlainDate.WEEKDAY_IN_MONTH.setToFirst(this.dow)),
            is(PlainDate.of(2013, this.expectedMonth, this.expectedDom)));
    }

    @Test
    public void setToSecond() {
        PlainDate date = PlainDate.of(2013, this.month, this.dom);
        assertThat(
            date.with(PlainDate.WEEKDAY_IN_MONTH.setToSecond(this.dow)),
            is(PlainDate
                .of(2013, this.expectedMonth, this.expectedDom)
                .plus(1, CalendarUnit.WEEKS)));
    }

    @Test
    public void setToThird() {
        PlainDate date = PlainDate.of(2013, this.month, this.dom);
        assertThat(
            date.with(PlainDate.WEEKDAY_IN_MONTH.setToThird(this.dow)),
            is(PlainDate
                .of(2013, this.expectedMonth, this.expectedDom)
                .plus(2, CalendarUnit.WEEKS)));
    }

    @Test
    public void setToFourth() {
        PlainDate date = PlainDate.of(2013, this.month, this.dom);
        assertThat(
            date.with(PlainDate.WEEKDAY_IN_MONTH.setToFourth(this.dow)),
            is(PlainDate
                .of(2013, this.expectedMonth, this.expectedDom)
                .plus(3, CalendarUnit.WEEKS)));
    }

    @Test
    public void setToLast() {
        PlainDate date = PlainDate.of(2013, this.month, this.dom);
        PlainDate expected =
            PlainDate
                .of(2013, this.expectedMonth, this.expectedDom)
                .plus(4, CalendarUnit.WEEKS);
        if (expected.getMonth() != this.expectedMonth) {
            expected = expected.minus(1, CalendarUnit.WEEKS);
        }
        assertThat(
            date.with(PlainDate.WEEKDAY_IN_MONTH.setToLast(this.dow)),
            is(expected));
    }

    @Test
    public void setToFirstOnTimestamp() {
        PlainTimestamp ts =
            PlainDate.of(2013, this.month, this.dom).atStartOfDay();
        assertThat(
            ts.with(PlainDate.WEEKDAY_IN_MONTH.setToFirst(this.dow)),
            is(
                PlainDate.of(2013, this.expectedMonth, this.expectedDom)
                .atStartOfDay()));
    }

    @Test
    public void setToSecondOnTimestamp() {
        PlainTimestamp ts =
            PlainDate.of(2013, this.month, this.dom).atStartOfDay();
        assertThat(
            ts.with(PlainDate.WEEKDAY_IN_MONTH.setToSecond(this.dow)),
            is(
                PlainDate.of(2013, this.expectedMonth, this.expectedDom)
                .plus(1, CalendarUnit.WEEKS)
                .atStartOfDay()));
    }

    @Test
    public void setToThirdOnTimestamp() {
        PlainTimestamp ts =
            PlainDate.of(2013, this.month, this.dom).atStartOfDay();
        assertThat(
            ts.with(PlainDate.WEEKDAY_IN_MONTH.setToThird(this.dow)),
            is(
                PlainDate.of(2013, this.expectedMonth, this.expectedDom)
                .plus(2, CalendarUnit.WEEKS)
                .atStartOfDay()));
    }

    @Test
    public void setToFourthOnTimestamp() {
        PlainTimestamp ts =
            PlainDate.of(2013, this.month, this.dom).atStartOfDay();
        assertThat(
            ts.with(PlainDate.WEEKDAY_IN_MONTH.setToFourth(this.dow)),
            is(
                PlainDate.of(2013, this.expectedMonth, this.expectedDom)
                .plus(3, CalendarUnit.WEEKS)
                .atStartOfDay()));
    }

    @Test
    public void setToLastOnTimestamp() {
        PlainTimestamp ts =
            PlainDate.of(2013, this.month, this.dom).atStartOfDay();
        PlainTimestamp expected =
            PlainDate.of(2013, this.expectedMonth, this.expectedDom)
                .plus(4, CalendarUnit.WEEKS)
                .atStartOfDay();
        if (expected.getMonth() != this.expectedMonth) {
            expected = expected.minus(1, CalendarUnit.WEEKS);
        }
        assertThat(
            ts.with(PlainDate.WEEKDAY_IN_MONTH.setToLast(this.dow)),
            is(expected));
    }

}