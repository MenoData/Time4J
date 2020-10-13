package net.time4j.calendar.frenchrev;

import net.time4j.engine.CalendarSystem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class FrenchRepublicanUnitTest {

    @Test(expected=IllegalArgumentException.class)
    public void frenchrevMin() {
        CalendarSystem<FrenchRepublicanCalendar> calsys = FrenchRepublicanCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MIN_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void frenchrevMax() {
        CalendarSystem<FrenchRepublicanCalendar> calsys = FrenchRepublicanCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MAX_VALUE);
    }

    @Test
    public void plusYears() {
        FrenchRepublicanCalendar fcal = FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 18);
        assertThat(
            fcal.plus(2, FrenchRepublicanCalendar.Unit.YEARS),
            is(FrenchRepublicanCalendar.of(10, FrenchRepublicanMonth.BRUMAIRE, 18)));
        FrenchRepublicanCalendar fcal2 = FrenchRepublicanCalendar.of(11, Sansculottides.LEAP_DAY);
        assertThat(
            fcal2.plus(2, FrenchRepublicanCalendar.Unit.YEARS),
            is(FrenchRepublicanCalendar.of(13, Sansculottides.COMPLEMENTARY_DAY_5)));
        FrenchRepublicanCalendar fcal3 = FrenchRepublicanCalendar.of(11, Sansculottides.LEAP_DAY);
        assertThat(
            fcal3.plus(4, FrenchRepublicanCalendar.Unit.YEARS),
            is(FrenchRepublicanCalendar.of(15, Sansculottides.LEAP_DAY)));
    }

    @Test
    public void minusYears() {
        FrenchRepublicanCalendar fcal = FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 18);
        assertThat(
            fcal.minus(2, FrenchRepublicanCalendar.Unit.YEARS),
            is(FrenchRepublicanCalendar.of(6, FrenchRepublicanMonth.BRUMAIRE, 18)));
        FrenchRepublicanCalendar fcal2 = FrenchRepublicanCalendar.of(11, Sansculottides.LEAP_DAY);
        assertThat(
            fcal2.minus(2, FrenchRepublicanCalendar.Unit.YEARS),
            is(FrenchRepublicanCalendar.of(9, Sansculottides.COMPLEMENTARY_DAY_5)));
        FrenchRepublicanCalendar fcal3 = FrenchRepublicanCalendar.of(11, Sansculottides.LEAP_DAY);
        assertThat(
            fcal3.minus(4, FrenchRepublicanCalendar.Unit.YEARS),
            is(FrenchRepublicanCalendar.of(7, Sansculottides.LEAP_DAY)));
    }

    @Test
    public void yearsBetween() {
        FrenchRepublicanCalendar f1 = FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 18);
        FrenchRepublicanCalendar f2 = FrenchRepublicanCalendar.of(11, Sansculottides.LEAP_DAY);
        assertThat(
            FrenchRepublicanCalendar.Unit.YEARS.between(f1, f2),
            is(3L));
        f2 = FrenchRepublicanCalendar.of(11, FrenchRepublicanMonth.BRUMAIRE, 18);
        assertThat(
            FrenchRepublicanCalendar.Unit.YEARS.between(f1, f2),
            is(3L));
        f2 = FrenchRepublicanCalendar.of(11, FrenchRepublicanMonth.BRUMAIRE, 17);
        assertThat(
            FrenchRepublicanCalendar.Unit.YEARS.between(f1, f2),
            is(2L));
    }

    @Test
    public void plusDays() {
        FrenchRepublicanCalendar fcal = FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 18);
        assertThat(
            fcal.plus(21, FrenchRepublicanCalendar.Unit.DAYS),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.FRIMAIRE, 9)));
    }

    @Test
    public void minusDays() {
        FrenchRepublicanCalendar fcal = FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 18);
        assertThat(
            fcal.minus(50, FrenchRepublicanCalendar.Unit.DAYS),
            is(FrenchRepublicanCalendar.of(7, Sansculottides.COMPLEMENTARY_DAY_4)));
    }

    @Test
    public void daysBetween() {
        FrenchRepublicanCalendar f1 = FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 18);
        FrenchRepublicanCalendar f2 = FrenchRepublicanCalendar.of(11, Sansculottides.LEAP_DAY);
        assertThat(
            FrenchRepublicanCalendar.Unit.DAYS.between(f1, f2),
            is(365L * 3 + 300 + 6 + 12));
    }

    @Test
    public void plusWeeks() {
        FrenchRepublicanCalendar fcal = FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 18);
        assertThat(
            fcal.plus(3, FrenchRepublicanCalendar.Unit.WEEKS),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.FRIMAIRE, 9)));
    }

    @Test
    public void minusWeeks() {
        FrenchRepublicanCalendar fcal = FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 18);
        assertThat(
            fcal.minus(7, FrenchRepublicanCalendar.Unit.WEEKS),
            is(FrenchRepublicanCalendar.of(7, Sansculottides.COMPLEMENTARY_DAY_5)));
    }

    @Test
    public void weeksBetween() {
        FrenchRepublicanCalendar f1 = FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 18);
        FrenchRepublicanCalendar f2 = FrenchRepublicanCalendar.of(11, Sansculottides.LEAP_DAY);
        assertThat(
            FrenchRepublicanCalendar.Unit.WEEKS.between(f1, f2),
            is((365 * 3 + 300 + 6 + 12) / 7L));
    }

    @Test
    public void plusDecades() {
        FrenchRepublicanCalendar fcal = FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 18);
        assertThat(
            fcal.plus(2, FrenchRepublicanCalendar.Unit.DECADES),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.FRIMAIRE, 8)));
        fcal = FrenchRepublicanCalendar.of(7, Sansculottides.LEAP_DAY);
        assertThat(
            fcal.plus(2, FrenchRepublicanCalendar.Unit.DECADES),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.VENDEMIAIRE, 20)));
    }

    @Test
    public void minusDecades() {
        FrenchRepublicanCalendar fcal = FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 18);
        assertThat(
            fcal.minus(5, FrenchRepublicanCalendar.Unit.DECADES),
            is(FrenchRepublicanCalendar.of(7, FrenchRepublicanMonth.FRUCTIDOR, 28)));
    }

    @Test
    public void decadesBetween() {
        FrenchRepublicanCalendar f1 = FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 18);
        FrenchRepublicanCalendar f2 = FrenchRepublicanCalendar.of(7, FrenchRepublicanMonth.FRUCTIDOR, 28);
        assertThat(
            FrenchRepublicanCalendar.Unit.DECADES.between(f1, f2),
            is(-5L));
        f2 = FrenchRepublicanCalendar.of(9, FrenchRepublicanMonth.BRUMAIRE, 17);
        assertThat(
            FrenchRepublicanCalendar.Unit.DECADES.between(f1, f2),
            is(35L));
        f1 = FrenchRepublicanCalendar.of(7, Sansculottides.LEAP_DAY);
        assertThat(
            FrenchRepublicanCalendar.Unit.DECADES.between(f1, f2),
            is(40L));
    }

    @Test
    public void plusMonths() {
        FrenchRepublicanCalendar fcal = FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 18);
        assertThat(
            fcal.plus(2, FrenchRepublicanCalendar.Unit.MONTHS),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.NIVOSE, 18)));
        fcal = FrenchRepublicanCalendar.of(7, Sansculottides.LEAP_DAY);
        assertThat(
            fcal.plus(2, FrenchRepublicanCalendar.Unit.MONTHS),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 30)));
    }

    @Test
    public void minusMonths() {
        FrenchRepublicanCalendar fcal = FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 18);
        assertThat(
            fcal.minus(2, FrenchRepublicanCalendar.Unit.MONTHS),
            is(FrenchRepublicanCalendar.of(7, FrenchRepublicanMonth.FRUCTIDOR, 18)));
    }

    @Test
    public void monthsBetween() {
        FrenchRepublicanCalendar f1 = FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 18);
        FrenchRepublicanCalendar f2 = FrenchRepublicanCalendar.of(7, FrenchRepublicanMonth.FRUCTIDOR, 18);
        assertThat(
            FrenchRepublicanCalendar.Unit.MONTHS.between(f1, f2),
            is(-2L));
        f2 = FrenchRepublicanCalendar.of(9, FrenchRepublicanMonth.BRUMAIRE, 17);
        assertThat(
            FrenchRepublicanCalendar.Unit.MONTHS.between(f1, f2),
            is(11L));
        f1 = FrenchRepublicanCalendar.of(7, Sansculottides.LEAP_DAY);
        assertThat(
            FrenchRepublicanCalendar.Unit.MONTHS.between(f1, f2),
            is(13L));
    }

    @Test
    public void now() {
        System.out.println(FrenchRepublicanCalendar.nowInSystemTime());
    }

}
