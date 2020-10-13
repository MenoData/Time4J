package net.time4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class StdYearOrdinalDateTest {

 	@Parameters(name= "{index}: PlainDate.of(2011,{0})=2011-{1}-{2}")
 	public static Iterable<Object[]> data() {
        return OrdinalDateData.dataStdYear();
    }

    private int doy;
    private int month;
    private int dom;

    public StdYearOrdinalDateTest(
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
            PlainDate.of(2011, this.doy),
            is(PlainDate.of(2011, this.month, this.dom)));
    }

    @Test
    public void withDayOfYear() {
        assertThat(
            PlainDate.of(2011, 1).with(PlainDate.DAY_OF_YEAR, this.doy),
            is(PlainDate.of(2011, this.month, this.dom)));
    }

}
