package net.time4j.calendar.bahai;

import net.time4j.engine.CalendarSystem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class BadiUnitTest {

    @Test(expected=IllegalArgumentException.class)
    public void badiMin() {
        CalendarSystem<BadiCalendar> calsys = BadiCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MIN_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void badiMax() {
        CalendarSystem<BadiCalendar> calsys = BadiCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MAX_VALUE);
    }

    @Test
    public void plusVahidCycles() {
        BadiCalendar cal = BadiCalendar.of(5, 8, BadiMonth.AZAMAT, 18);
        assertThat(
            cal.plus(5, BadiCalendar.Unit.VAHID_CYCLES),
            is(BadiCalendar.of(10, 8, BadiMonth.AZAMAT, 18)));
    }

    @Test
    public void minusVahidCycles() {
        BadiCalendar cal = BadiCalendar.of(5, 13, BadiMonth.AZAMAT, 18);
        assertThat(
            cal.minus(2, BadiCalendar.Unit.VAHID_CYCLES),
            is(BadiCalendar.of(3, 13, BadiMonth.AZAMAT, 18)));
    }

    @Test
    public void vahidCyclesBetween() {
        BadiCalendar cal1 = BadiCalendar.of(6, 13, BadiMonth.AZAMAT, 18);
        BadiCalendar cal2 = BadiCalendar.of(7, 13, BadiMonth.NUR, 17);
        assertThat(
            BadiCalendar.Unit.VAHID_CYCLES.between(cal1, cal2),
            is(1L));
    }

    @Test
    public void plusYears() {
        BadiCalendar cal = BadiCalendar.of(5, 8, BadiMonth.AZAMAT, 18);
        assertThat(
            cal.plus(5, BadiCalendar.Unit.YEARS),
            is(BadiCalendar.of(5, 13, BadiMonth.AZAMAT, 18)));
        cal = BadiCalendar.ofIntercalary(5, 8, 5);
        assertThat(
            cal.plus(1, BadiCalendar.Unit.YEARS),
            is(BadiCalendar.ofIntercalary(5, 9, 4)));
        assertThat(
            cal.plus(4, BadiCalendar.Unit.YEARS),
            is(BadiCalendar.ofIntercalary(5, 12, 5)));
    }

    @Test
    public void minusYears() {
        BadiCalendar cal = BadiCalendar.of(5, 13, BadiMonth.AZAMAT, 18);
        assertThat(
            cal.minus(5, BadiCalendar.Unit.YEARS),
            is(BadiCalendar.of(5, 8, BadiMonth.AZAMAT, 18)));
        cal = BadiCalendar.ofIntercalary(5, 8, 5);
        assertThat(
            cal.minus(1, BadiCalendar.Unit.YEARS),
            is(BadiCalendar.ofIntercalary(5, 7, 4)));
        assertThat(
            cal.minus(4, BadiCalendar.Unit.YEARS),
            is(BadiCalendar.ofIntercalary(5, 4, 5)));
    }

    @Test
    public void yearsBetween() {
        BadiCalendar cal1 = BadiCalendar.of(5, 13, BadiMonth.AZAMAT, 18);
        BadiCalendar cal2 = BadiCalendar.of(5, 19, BadiMonth.NUR, 17);
        assertThat(
            BadiCalendar.Unit.YEARS.between(cal1, cal2),
            is(6L));
        cal2 = BadiCalendar.of(5, 19, BadiMonth.AZAMAT, 17);
        assertThat(
            BadiCalendar.Unit.YEARS.between(cal1, cal2),
            is(5L));
        cal2 = BadiCalendar.ofIntercalary(5, 8, 5);
        assertThat(
            BadiCalendar.Unit.YEARS.between(cal1, cal2),
            is(-4L));
    }

    @Test
    public void plusDays() {
        BadiCalendar cal = BadiCalendar.of(5, 13, BadiMonth.AZAMAT, 18);
        assertThat(
            cal.plus(21, BadiCalendar.Unit.DAYS),
            is(BadiCalendar.of(5, 13, BadiMonth.RAHMAT, 1)));
        cal = BadiCalendar.of(5, 8, BadiMonth.MULK, 18);
        assertThat(
            cal.plus(21, BadiCalendar.Unit.DAYS),
            is(BadiCalendar.of(5, 8, BadiMonth.ALA, 15)));
        cal = BadiCalendar.of(5, 9, BadiMonth.MULK, 18);
        assertThat(
            cal.plus(41, BadiCalendar.Unit.DAYS),
            is(BadiCalendar.of(5, 10, BadiMonth.BAHA, 17)));
    }

    @Test
    public void minusDays() {
        BadiCalendar cal = BadiCalendar.of(5, 10, BadiMonth.BAHA, 17);
        assertThat(
            cal.minus(41, BadiCalendar.Unit.DAYS),
            is(BadiCalendar.of(5, 9, BadiMonth.MULK, 18)));
    }

    @Test
    public void daysBetween() {
        BadiCalendar b1 = BadiCalendar.of(5, 9, BadiMonth.MULK, 18);
        BadiCalendar b2 = BadiCalendar.of(5, 10, BadiMonth.BAHA, 17);
        assertThat(
            BadiCalendar.Unit.DAYS.between(b1, b2),
            is(41L));
    }

    @Test
    public void plusWeeks() {
        BadiCalendar cal = BadiCalendar.of(5, 8, BadiMonth.MULK, 17);
        assertThat(
            cal.plus(1, BadiCalendar.Unit.WEEKS),
            is(BadiCalendar.ofIntercalary(5, 8, 5)));
    }

    @Test
    public void minusWeeks() {
        BadiCalendar cal = BadiCalendar.ofIntercalary(5, 8, 5);
        assertThat(
            cal.minus(1, BadiCalendar.Unit.WEEKS),
            is(BadiCalendar.of(5, 8, BadiMonth.MULK, 17)));
    }

    @Test
    public void weeksBetween() {
        BadiCalendar b1 = BadiCalendar.of(5, 8, BadiMonth.MULK, 10);
        BadiCalendar b2 = BadiCalendar.ofIntercalary(5, 8, 5);
        assertThat(
            BadiCalendar.Unit.WEEKS.between(b1, b2),
            is(2L));
    }

    @Test
    public void plusMonths() {
        BadiCalendar cal = BadiCalendar.of(5, 8, BadiMonth.MULK, 10);
        assertThat(
            cal.plus(2, BadiCalendar.Unit.MONTHS),
            is(BadiCalendar.of(5, 9, BadiMonth.BAHA, 10)));
        cal = BadiCalendar.ofIntercalary(5, 8, 3);
        assertThat(
            cal.plus(3, BadiCalendar.Unit.MONTHS),
            is(BadiCalendar.of(5, 9, BadiMonth.JALAL, 19)));
    }

    @Test
    public void minusMonths() {
        BadiCalendar cal = BadiCalendar.of(5, 8, BadiMonth.MULK, 10);
        assertThat(
            cal.minus(2, BadiCalendar.Unit.MONTHS),
            is(BadiCalendar.of(5, 8, BadiMonth.SHARAF, 10)));
        cal = BadiCalendar.ofIntercalary(5, 8, 3);
        assertThat(
            cal.minus(3, BadiCalendar.Unit.MONTHS),
            is(BadiCalendar.of(5, 8, BadiMonth.MASAIL, 19)));
    }

    @Test
    public void monthsBetween() {
        BadiCalendar cal1 = BadiCalendar.of(5, 8, BadiMonth.MULK, 10);
        BadiCalendar cal2 = BadiCalendar.of(5, 9, BadiMonth.SHARAF, 10);
        assertThat(
            BadiCalendar.Unit.MONTHS.between(cal1, cal2),
            is(17L));
        assertThat(
            BadiCalendar.Unit.MONTHS.between(cal2, cal1),
            is(-17L));
        cal2 = BadiCalendar.of(5, 9, BadiMonth.BAHA, 10);
        assertThat(
            BadiCalendar.Unit.MONTHS.between(cal1, cal2),
            is(2L));
        cal2 = BadiCalendar.of(5, 9, BadiMonth.BAHA, 9);
        assertThat(
            BadiCalendar.Unit.MONTHS.between(cal1, cal2),
            is(1L));
        cal2 = BadiCalendar.ofIntercalary(5, 8, 3);
        assertThat(
            BadiCalendar.Unit.MONTHS.between(cal1, cal2),
            is(0L));
        cal1 = BadiCalendar.of(5, 8, BadiMonth.MULK, 1);
        assertThat(
            BadiCalendar.Unit.MONTHS.between(cal1, cal2),
            is(0L));
        cal1 = BadiCalendar.of(5, 9, BadiMonth.SHARAF, 10);
        assertThat(
            BadiCalendar.Unit.MONTHS.between(cal1, cal2),
            is(-16L));
    }

    @Test
    public void now() {
        System.out.println(BadiCalendar.nowInSystemTime());
    }

}
