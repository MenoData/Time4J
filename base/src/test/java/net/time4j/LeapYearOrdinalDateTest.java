package net.time4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class LeapYearOrdinalDateTest {

 	@Parameters(name= "{index}: PlainDate.of(2012,{0})=2012-{1}-{2}")
 	public static Iterable<Object[]> data() {
        return OrdinalDateData.dataLeapYear();
    }

    private int doy;
    private int month;
    private int dom;

    public LeapYearOrdinalDateTest(
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
    public void createOrdinalDate() {
        assertThat(
            PlainDate.of(2012, this.doy),
            is(PlainDate.of(2012, this.month, this.dom)));
    }

    @Test
    public void withDayOfYear() {
        assertThat(
            PlainDate.of(2012, 1).with(PlainDate.DAY_OF_YEAR, this.doy),
            is(PlainDate.of(2012, this.month, this.dom)));
    }

}
