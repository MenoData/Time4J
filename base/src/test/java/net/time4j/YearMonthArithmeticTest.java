package net.time4j;

import net.time4j.base.GregorianMath;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.CalendarUnit.CENTURIES;
import static net.time4j.CalendarUnit.DECADES;
import static net.time4j.CalendarUnit.MILLENNIA;
import static net.time4j.CalendarUnit.MONTHS;
import static net.time4j.CalendarUnit.QUARTERS;
import static net.time4j.CalendarUnit.YEARS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class YearMonthArithmeticTest {

    @Test
    public void plusOneYear() {
        assertThat(
            PlainDate.of(2012, 2, 1).plus(1, YEARS),
            is(PlainDate.of(2013, 2, 1)));
        assertThat(
            PlainDate.of(2012, 2, 28).plus(1, YEARS),
            is(PlainDate.of(2013, 2, 28)));
        assertThat(
            PlainDate.of(2012, 2, 29).plus(1, YEARS),
            is(PlainDate.of(2013, 2, 28)));
    }

    @Test
    public void plusFourYears() {
        assertThat(
            PlainDate.of(2012, 2, 1).plus(4, YEARS),
            is(PlainDate.of(2016, 2, 1)));
        assertThat(
            PlainDate.of(2012, 2, 28).plus(4, YEARS),
            is(PlainDate.of(2016, 2, 28)));
        assertThat(
            PlainDate.of(2012, 2, 29).plus(4, YEARS),
            is(PlainDate.of(2016, 2, 29)));
    }

    @Test(expected=ArithmeticException.class)
    public void maxPlusYear() {
        PlainDate.of(GregorianMath.MAX_YEAR, 1, 1).plus(1, YEARS);
    }

    @Test
    public void minusOneYear() {
        assertThat(
            PlainDate.of(2012, 2, 1).minus(1, YEARS),
            is(PlainDate.of(2011, 2, 1)));
        assertThat(
            PlainDate.of(2012, 2, 28).minus(1, YEARS),
            is(PlainDate.of(2011, 2, 28)));
        assertThat(
            PlainDate.of(2012, 2, 29).minus(1, YEARS),
            is(PlainDate.of(2011, 2, 28)));
    }

    @Test
    public void minusFourYears() {
        assertThat(
            PlainDate.of(2012, 2, 1).minus(4, YEARS),
            is(PlainDate.of(2008, 2, 1)));
        assertThat(
            PlainDate.of(2012, 2, 28).minus(4, YEARS),
            is(PlainDate.of(2008, 2, 28)));
        assertThat(
            PlainDate.of(2012, 2, 29).minus(4, YEARS),
            is(PlainDate.of(2008, 2, 29)));
    }

    @Test(expected=ArithmeticException.class)
    public void minMinusYear() {
        PlainDate.of(GregorianMath.MIN_YEAR, 12, 31).minus(1, YEARS);
    }

    @Test
    public void plusOneMonth() {
        assertThat(
            PlainDate.of(2012, 12, 14).plus(1, MONTHS),
            is(PlainDate.of(2013, 1, 14)));
        assertThat(
            PlainDate.of(2012, 1, 1).plus(1, MONTHS),
            is(PlainDate.of(2012, 2, 1)));
        assertThat(
            PlainDate.of(2012, 1, 28).plus(1, MONTHS),
            is(PlainDate.of(2012, 2, 28)));
        assertThat(
            PlainDate.of(2014, 1, 29).plus(1, MONTHS),
            is(PlainDate.of(2014, 2, 28)));
        assertThat(
            PlainDate.of(2012, 1, 29).plus(1, MONTHS),
            is(PlainDate.of(2012, 2, 29)));
        assertThat(
            PlainDate.of(2012, 1, 30).plus(1, MONTHS),
            is(PlainDate.of(2012, 2, 29)));
        assertThat(
            PlainDate.of(2012, 3, 31).plus(1, MONTHS),
            is(PlainDate.of(2012, 4, 30)));
        assertThat(
            PlainDate.of(2012, 2, 1).plus(1, MONTHS),
            is(PlainDate.of(2012, 3, 1)));
        assertThat(
            PlainDate.of(2012, 7, 31).plus(1, MONTHS),
            is(PlainDate.of(2012, 8, 31)));
    }

    @Test
    public void plusThirteenMonths() {
        assertThat(
            PlainDate.of(2012, 2, 19).plus(13, MONTHS),
            is(PlainDate.of(2013, 3, 19)));
        assertThat(
            PlainDate.of(2011, 1, 30).plus(13, MONTHS),
            is(PlainDate.of(2012, 2, 29)));
        assertThat(
            PlainDate.of(2010, 1, 29).plus(13, MONTHS),
            is(PlainDate.of(2011, 2, 28)));
    }

    @Test(expected=ArithmeticException.class)
    public void maxPlusMonth() {
        PlainDate.of(GregorianMath.MAX_YEAR, 12, 1).plus(1, MONTHS);
    }

    @Test
    public void minusOneMonth() {
        assertThat(
            PlainDate.of(2012, 1, 14).minus(1, MONTHS),
            is(PlainDate.of(2011, 12, 14)));
        assertThat(
            PlainDate.of(2012, 3, 1).minus(1, MONTHS),
            is(PlainDate.of(2012, 2, 1)));
        assertThat(
            PlainDate.of(2012, 3, 28).minus(1, MONTHS),
            is(PlainDate.of(2012, 2, 28)));
        assertThat(
            PlainDate.of(2012, 3, 29).minus(1, MONTHS),
            is(PlainDate.of(2012, 2, 29)));
        assertThat(
            PlainDate.of(2012, 3, 30).minus(1, MONTHS),
            is(PlainDate.of(2012, 2, 29)));
        assertThat(
            PlainDate.of(2012, 3, 31).minus(1, MONTHS),
            is(PlainDate.of(2012, 2, 29)));
        assertThat(
            PlainDate.of(2012, 4, 1).minus(1, MONTHS),
            is(PlainDate.of(2012, 3, 1)));
        assertThat(
            PlainDate.of(2012, 5, 31).minus(1, MONTHS),
            is(PlainDate.of(2012, 4, 30)));
    }

    @Test
    public void minusThirteenMonths() {
        assertThat(
            PlainDate.of(2013, 3, 19).minus(13, MONTHS),
            is(PlainDate.of(2012, 2, 19)));
        assertThat(
            PlainDate.of(2013, 3, 31).minus(13, MONTHS),
            is(PlainDate.of(2012, 2, 29)));
        assertThat(
            PlainDate.of(2014, 3, 31).minus(13, MONTHS),
            is(PlainDate.of(2013, 2, 28)));
    }

    @Test(expected=ArithmeticException.class)
    public void minMinusMonth() {
        PlainDate.of(GregorianMath.MIN_YEAR, 1, 31).minus(1, MONTHS);
    }

    @Test(expected=ArithmeticException.class)
    public void plusLongMaxYears() {
        PlainDate.of(1, 1).plus(Long.MAX_VALUE, YEARS);
    }

    @Test(expected=ArithmeticException.class)
    public void minusLongMaxYears() {
        PlainDate.of(1, 1).minus(Long.MAX_VALUE, YEARS);
    }

    @Test(expected=ArithmeticException.class)
    public void plusLongMaxMonths() {
        PlainDate.of(1, 1).plus(Long.MAX_VALUE, MONTHS);
    }

    @Test(expected=ArithmeticException.class)
    public void minusLongMaxMonths() {
        PlainDate.of(1, 1).minus(Long.MAX_VALUE, MONTHS);
    }

    @Test(expected=ArithmeticException.class)
    public void plusLongMinYears() {
        PlainDate.of(1, 1).plus(Long.MIN_VALUE, YEARS);
    }

    @Test(expected=ArithmeticException.class)
    public void minusLongMinYears() {
        PlainDate.of(1, 1).minus(Long.MIN_VALUE, YEARS);
    }

    @Test(expected=ArithmeticException.class)
    public void plusLongMinMonths() {
        PlainDate.of(1, 1).plus(Long.MIN_VALUE, MONTHS);
    }

    @Test(expected=ArithmeticException.class)
    public void minusLongMinMonths() {
        PlainDate.of(1, 1).minus(Long.MIN_VALUE, MONTHS);
    }

    @Test(expected=NullPointerException.class)
    public void plusNull() {
        PlainDate.of(1, 1).plus(Long.MAX_VALUE, null);
    }

    @Test(expected=NullPointerException.class)
    public void minusNull() {
        PlainDate.of(1, 1).minus(Long.MAX_VALUE, null);
    }

    @Test
    public void plusMillennia() {
        assertThat(
            PlainDate.of(2014, 9, 20).plus(2, MILLENNIA),
            is(PlainDate.of(4014, 9, 20)));
    }

    @Test
    public void plusCenturies() {
        assertThat(
            PlainDate.of(2014, 9, 20).plus(2, CENTURIES),
            is(PlainDate.of(2214, 9, 20)));
        assertThat(
            PlainDate.of(2000, 2, 29).plus(3, CENTURIES),
            is(PlainDate.of(2300, 2, 28)));
        assertThat(
            PlainDate.of(2000, 2, 29).plus(4, CENTURIES),
            is(PlainDate.of(2400, 2, 29)));
    }

    @Test
    public void plusDecades() {
        assertThat(
            PlainDate.of(2014, 9, 20).plus(2, DECADES),
            is(PlainDate.of(2034, 9, 20)));
    }

    @Test
    public void plusQuarters() {
        assertThat(
            PlainDate.of(2014, 9, 20).plus(2, QUARTERS),
            is(PlainDate.of(2015, 3, 20)));
        assertThat(
            PlainDate.of(2014, 11, 30).plus(1, QUARTERS),
            is(PlainDate.of(2015, 2, 28)));
    }

    @Test
    public void minusQuarters() {
        assertThat(
            PlainDate.of(2014, 9, 20).minus(2, QUARTERS),
            is(PlainDate.of(2014, 3, 20)));
        assertThat(
            PlainDate.of(2014, 12, 31).minus(1, QUARTERS),
            is(PlainDate.of(2014, 9, 30)));
    }

    @Test
    public void quartersBetween() {
        assertThat(
            QUARTERS.between(
                PlainDate.of(2014, 9, 20),
                PlainDate.of(2015, 3, 20)),
            is(2L));
        assertThat(
            QUARTERS.between(
                PlainDate.of(2014, 12, 31),
                PlainDate.of(2014, 9, 30)),
            is(-1L));
        assertThat(
            QUARTERS.between(
                PlainDate.of(2014, 9, 20),
                PlainDate.of(2015, 3, 19)),
            is(1L));
        assertThat(
            QUARTERS.between(
                PlainDate.of(2014, 12, 29),
                PlainDate.of(2014, 9, 30)),
            is(0L));
    }

    @Test
    public void monthsBetween() {
        assertThat(
            MONTHS.between(
                PlainDate.of(2014, 9, 1),
                PlainDate.of(2014, 9, 30)),
            is(0L));
        assertThat(
            MONTHS.between(
                PlainDate.of(2014, 9, 30),
                PlainDate.of(2014, 9, 1)),
            is(0L));
        assertThat(
            MONTHS.between(
                PlainDate.of(2014, 9, 20),
                PlainDate.of(2015, 3, 20)),
            is(6L));
        assertThat(
            MONTHS.between(
                PlainDate.of(2014, 12, 31),
                PlainDate.of(2014, 9, 30)),
            is(-3L));
        assertThat(
            MONTHS.between(
                PlainDate.of(2014, 9, 20),
                PlainDate.of(2015, 3, 19)),
            is(5L));
        assertThat(
            MONTHS.between(
                PlainDate.of(2014, 12, 29),
                PlainDate.of(2014, 9, 30)),
            is(-2L));
    }

    @Test
    public void yearsBetween() {
        assertThat(
            YEARS.between(
                PlainDate.of(2014, 9, 20),
                PlainDate.of(2015, 9, 20)),
            is(1L));
        assertThat(
            YEARS.between(
                PlainDate.of(2014, 9, 20),
                PlainDate.of(2015, 9, 19)),
            is(0L));
        assertThat(
            YEARS.between(
                PlainDate.of(2014, 12, 31),
                PlainDate.of(2014, 9, 30)),
            is(0L));
        assertThat(
            YEARS.between(
                PlainDate.of(2014, 12, 29),
                PlainDate.of(2011, 9, 30)),
            is(-3L));
        assertThat(
            YEARS.between(
                PlainDate.of(2014, 12, 29),
                PlainDate.of(2011, 12, 30)),
            is(-2L));
    }

    @Test
    public void decadesBetween() {
        assertThat(
            DECADES.between(
                PlainDate.of(1914, 9, 20),
                PlainDate.of(2004, 9, 20)),
            is(9L));
        assertThat(
            DECADES.between(
                PlainDate.of(1914, 9, 20),
                PlainDate.of(2004, 9, 19)),
            is(8L));
    }

    @Test
    public void centuriesBetween() {
        assertThat(
            CENTURIES.between(
                PlainDate.of(1914, 9, 20),
                PlainDate.of(2014, 9, 20)),
            is(1L));
        assertThat(
            CENTURIES.between(
                PlainDate.of(1914, 9, 20),
                PlainDate.of(2014, 9, 19)),
            is(0L));
    }

    @Test
    public void millenniaBetween() {
        assertThat(
            MILLENNIA.between(
                PlainDate.of(1914, 9, 20),
                PlainDate.of(3914, 9, 20)),
            is(2L));
        assertThat(
            MILLENNIA.between(
                PlainDate.of(1914, 9, 20),
                PlainDate.of(3914, 9, 19)),
            is(1L));
        assertThat(
            MILLENNIA.between(
                PlainDate.of(1914, 9, 20),
                PlainDate.of(3014, 9, 19)),
            is(1L));
    }

}