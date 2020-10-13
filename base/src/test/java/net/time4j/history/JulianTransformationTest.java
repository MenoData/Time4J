package net.time4j.history;

import net.time4j.PlainDate;
import net.time4j.engine.EpochDays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class JulianTransformationTest {

    @Parameterized.Parameters(name= "{index}: mjd-days({0}-{1}-{2})={3}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
            new Object[][]{
                {-999979466, 11, 21, PlainDate.axis().getMinimum().get(EpochDays.MODIFIED_JULIAN_DATE)},
                {-4712, 1, 1, PlainDate.of(-4713, 11, 24).get(EpochDays.MODIFIED_JULIAN_DATE)},
                {0, 1, 1, PlainDate.of(-1, 12, 30).get(EpochDays.MODIFIED_JULIAN_DATE)},
                {1582, 10, 5, PlainDate.of(1582, 10, 15).get(EpochDays.MODIFIED_JULIAN_DATE)},
                {1582, 10, 4, PlainDate.of(1582, 10, 14).get(EpochDays.MODIFIED_JULIAN_DATE)},
                {1752, 9, 3, PlainDate.of(1752, 9, 14).get(EpochDays.MODIFIED_JULIAN_DATE)},
                {2015, 3, 23, PlainDate.of(2015, 4, 5).get(EpochDays.MODIFIED_JULIAN_DATE)},
                {2100, 2, 15, PlainDate.of(2100, 2, 28).get(EpochDays.MODIFIED_JULIAN_DATE)},
                {2100, 2, 16, PlainDate.of(2100, 3, 1).get(EpochDays.MODIFIED_JULIAN_DATE)},
                {2100, 2, 28, PlainDate.of(2100, 3, 13).get(EpochDays.MODIFIED_JULIAN_DATE)},
                {2100, 2, 29, PlainDate.of(2100, 3, 14).get(EpochDays.MODIFIED_JULIAN_DATE)},
                {2100, 3, 1, PlainDate.of(2100, 3, 15).get(EpochDays.MODIFIED_JULIAN_DATE)},
            }
        );
    }

    private int year;
    private int month;
    private int dom;
    private long mjd;

    public JulianTransformationTest(
        int year,
        int month,
        int dom,
        long mjd
    ) {
        super();

        this.year = year;
        this.month = month;
        this.dom = dom;
        this.mjd = mjd;
    }

    @Test
    public void fromMJD() {
        long packed = JulianMath.toPackedDate(this.mjd);
        assertThat(JulianMath.readYear(packed), is(this.year));
        assertThat(JulianMath.readMonth(packed), is(this.month));
        assertThat(JulianMath.readDayOfMonth(packed), is(this.dom));
    }

    @Test
    public void toMJD() {
        assertThat(
            JulianMath.toMJD(this.year, this.month, this.dom),
            is(this.mjd));
    }

}
