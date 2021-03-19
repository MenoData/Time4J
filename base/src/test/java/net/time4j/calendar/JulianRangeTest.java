package net.time4j.calendar;

import net.time4j.history.HistoricEra;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class JulianRangeTest {

    @Test(expected=ArithmeticException.class)
    public void daysBetweenCast() {
        JulianCalendar jc1 = JulianCalendar.of(HistoricEra.AD, 1, 1, 1);
        JulianCalendar jc2 = JulianCalendar.of(HistoricEra.AD, 10_000_000, 1, 1);
        JulianCalendar.Unit.DAYS.between(jc1, jc2); // result out of int-range
    }

    @Test
    public void weeksBetweenCast() {
        JulianCalendar jc1 = JulianCalendar.of(HistoricEra.AD, 1, 1, 1);
        JulianCalendar jc2 = JulianCalendar.of(HistoricEra.AD, 10_000_000, 1, 1);
        assertThat(
            JulianCalendar.Unit.WEEKS.between(jc1, jc2), // fits into an int
            is(521785662));
    }

    @Test
    public void weeksBetweenSafe() {
        JulianCalendar jc1 = JulianCalendar.of(HistoricEra.AD, 1, 1, 1);
        JulianCalendar jc2 = JulianCalendar.of(HistoricEra.AD, 10_000_000, 1, 1);
        assertThat(
            jc1.until(jc2, JulianCalendar.Unit.WEEKS), // always safe
            is(521785662L));
    }

}