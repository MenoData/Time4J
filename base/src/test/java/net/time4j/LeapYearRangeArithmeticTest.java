package net.time4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class LeapYearRangeArithmeticTest {

 	@Parameters(name= "{index}: [2011-12-31]+{0}days=[2012-{1}-{2}]")
 	public static Iterable<Object[]> data() {
        return OrdinalDateData.dataLeapYear();
    }

    private int doy;
    private int month;
    private int dom;

    public LeapYearRangeArithmeticTest(
        int doy,
        int month,
        int dom
    ) {
        super();

        this.doy = doy;
        this.month = month;
        this.dom = dom;
    }

    @Test
    public void plusDayOfYear() {
        assertThat(
            PlainDate.of(2011, 12, 31).plus(this.doy, CalendarUnit.DAYS),
            is(PlainDate.of(2012, this.month, this.dom)));
        assertThat(
            PlainDate.of(2011, 12, 31)
                .plus(1, CalendarUnit.DAYS)
                .plus(this.doy - 1, CalendarUnit.DAYS),
            is(PlainDate.of(2012, this.month, this.dom)));
    }

    @Test
    public void daysBetween() {
        assertThat(
            CalendarUnit.DAYS.between(
                PlainDate.of(2011, 12, 31),
                PlainDate.of(2012, this.month, this.dom)),
            is(Long.valueOf(this.doy)));
        assertThat(
            CalendarUnit.DAYS.between(
                PlainDate.of(2012, 1, 1),
                PlainDate.of(2012, this.month, this.dom)),
            is(Long.valueOf(this.doy - 1)));
    }

}
