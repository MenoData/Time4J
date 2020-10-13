package net.time4j.calendar;

import net.time4j.engine.CalendarDays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class JapaneseUnitTest {

    @Test
    public void plusEras() {
        Nengo ansei = Nengo.ofRelatedGregorianYear(1857);
        JapaneseCalendar jcal1 =
            JapaneseCalendar.of(ansei, 4, EastAsianMonth.valueOf(5), 1);
        assertThat(
            jcal1.plus(5, JapaneseCalendar.Unit.ERAS),
            is(JapaneseCalendar.of(Nengo.MEIJI, 4, EastAsianMonth.valueOf(5), 1)));
        JapaneseCalendar jcal2 =
            JapaneseCalendar.of(ansei, 6, EastAsianMonth.valueOf(5), 1);
        assertThat(
            jcal2.plus(5, JapaneseCalendar.Unit.ERAS),
            is(JapaneseCalendar.of(Nengo.MEIJI, 6, EastAsianMonth.valueOf(5), 1)));
    }

    @Test
    public void plusYears() {
        // change of calendar system
        JapaneseCalendar leap =
            JapaneseCalendar.of(Nengo.MEIJI, 3, EastAsianMonth.valueOf(10).withLeap(), 1);
        assertThat(
            leap.plus(3, JapaneseCalendar.Unit.YEARS),
            is(JapaneseCalendar.ofGregorian(Nengo.MEIJI, 6, 10, 1)));

        // jump between leap months
        Nengo keio = Nengo.ofRelatedGregorianYear(1865);
        Nengo ansei = Nengo.ofRelatedGregorianYear(1857);
        JapaneseCalendar jcal1865 =
            JapaneseCalendar.of(keio, 1, EastAsianMonth.valueOf(5).withLeap(), 30);
        assertThat(
            jcal1865.plus(5, JapaneseCalendar.Unit.YEARS),
            is(JapaneseCalendar.of(Nengo.MEIJI, 3, EastAsianMonth.valueOf(5), 30)));
        assertThat(
            jcal1865.minus(8, JapaneseCalendar.Unit.YEARS),
            is(JapaneseCalendar.of(ansei, 4, EastAsianMonth.valueOf(5).withLeap(), 29)));

        // test for northern court nengos
        Nengo koan = Nengo.ofRelatedGregorianYear(1361, Nengo.Selector.NORTHERN_COURT);
        Nengo koryaku = Nengo.ofRelatedGregorianYear(1380, Nengo.Selector.NORTHERN_COURT);
        JapaneseCalendar jcalNorthern =
            JapaneseCalendar.of(koan, 1, EastAsianMonth.valueOf(1), 30);
        assertThat(
            jcalNorthern.plus(19, JapaneseCalendar.Unit.YEARS),
            is(JapaneseCalendar.of(koryaku, 2, EastAsianMonth.valueOf(1), 30)));
    }

    @Test
    public void plusMonths() {
        // end-of-month-check for gregorian years
        JapaneseCalendar leap =
            JapaneseCalendar.of(Nengo.MEIJI, 3, EastAsianMonth.valueOf(10).withLeap(), 29);
        assertThat(
            leap.plus(27, JapaneseCalendar.Unit.MONTHS),
            is(JapaneseCalendar.ofGregorian(Nengo.MEIJI, 6, 1, 29)));
        assertThat(
            leap.plus(28, JapaneseCalendar.Unit.MONTHS),
            is(JapaneseCalendar.ofGregorian(Nengo.MEIJI, 6, 2, 28)));

        // jump over a lunisolar leap month
        assertThat(
            leap.minus(1, JapaneseCalendar.Unit.MONTHS),
            is(JapaneseCalendar.of(Nengo.MEIJI, 3, EastAsianMonth.valueOf(10), 29)));
        assertThat(
            leap.minus(1, JapaneseCalendar.Unit.MONTHS).plus(2, JapaneseCalendar.Unit.MONTHS),
            is(JapaneseCalendar.of(Nengo.MEIJI, 3, EastAsianMonth.valueOf(11), 29)));

        // end-of-month-check for lunisolar years
        Nengo keio = Nengo.ofRelatedGregorianYear(1865);
        JapaneseCalendar jcal1865 =
            JapaneseCalendar.of(keio, 1, EastAsianMonth.valueOf(1), 30);
        assertThat(
            jcal1865.plus(5, JapaneseCalendar.Unit.MONTHS),
            is(JapaneseCalendar.of(keio, 1, EastAsianMonth.valueOf(5).withLeap(), 30)));
        assertThat(
            jcal1865.plus(6, JapaneseCalendar.Unit.MONTHS),
            is(JapaneseCalendar.of(keio, 1, EastAsianMonth.valueOf(6), 29)));
    }

    @Test
    public void plusWeeks() {
        JapaneseCalendar meiji5 = JapaneseCalendar.of(Nengo.MEIJI, 5, EastAsianMonth.valueOf(12), 1);
        assertThat(
            meiji5.plus(2, JapaneseCalendar.Unit.WEEKS),
            is(JapaneseCalendar.ofGregorian(Nengo.MEIJI, 6, 1, 13)));
        assertThat(
            JapaneseCalendar.ofGregorian(Nengo.MEIJI, 6, 1, 13).minus(2, JapaneseCalendar.Unit.WEEKS),
            is(meiji5));
    }

    @Test
    public void plusDays() {
        JapaneseCalendar meiji5 = JapaneseCalendar.of(Nengo.MEIJI, 5, EastAsianMonth.valueOf(12), 1);
        assertThat(
            meiji5.plus(4, JapaneseCalendar.Unit.DAYS),
            is(JapaneseCalendar.ofGregorian(Nengo.MEIJI, 6, 1, 3)));
        assertThat(
            JapaneseCalendar.ofGregorian(Nengo.MEIJI, 6, 1, 3).minus(4, JapaneseCalendar.Unit.DAYS),
            is(meiji5));
    }

    @Test
    public void erasBetween() {
        Nengo ansei = Nengo.ofRelatedGregorianYear(1857);
        JapaneseCalendar jcal1 =
            JapaneseCalendar.of(ansei, 4, EastAsianMonth.valueOf(5), 1);
        JapaneseCalendar jcal2 =
            JapaneseCalendar.of(ansei, 4, EastAsianMonth.valueOf(5).withLeap(), 1);
        JapaneseCalendar jcal3 =
            JapaneseCalendar.of(ansei, 4, EastAsianMonth.valueOf(5), 2);
        JapaneseCalendar jcal4 =
            JapaneseCalendar.of(ansei, 5, EastAsianMonth.valueOf(5), 1);
        JapaneseCalendar end =
            JapaneseCalendar.of(Nengo.MEIJI, 4, EastAsianMonth.valueOf(5), 1);
        assertThat(JapaneseCalendar.Unit.ERAS.between(jcal1, end), is(5L));
        assertThat(JapaneseCalendar.Unit.ERAS.between(jcal2, end), is(4L));
        assertThat(JapaneseCalendar.Unit.ERAS.between(jcal3, end), is(4L));
        assertThat(JapaneseCalendar.Unit.ERAS.between(jcal4, end), is(4L));
    }

    @Test
    public void yearsBetween() {
        Nengo keio = Nengo.ofRelatedGregorianYear(1865);
        Nengo ansei = Nengo.ofRelatedGregorianYear(1857);
        JapaneseCalendar jcal1865 =
            JapaneseCalendar.of(keio, 1, EastAsianMonth.valueOf(5).withLeap(), 29);
        JapaneseCalendar jcal1857 =
            JapaneseCalendar.of(ansei, 4, EastAsianMonth.valueOf(5).withLeap(), 29);
        assertThat(JapaneseCalendar.Unit.YEARS.between(jcal1857, jcal1865), is(8L));
        assertThat(JapaneseCalendar.Unit.YEARS.between(jcal1865, jcal1857), is(-8L));
        assertThat(JapaneseCalendar.Unit.YEARS.between(jcal1857, jcal1865.minus(CalendarDays.ONE)), is(7L));
    }

    @Test
    public void monthsBetween() {
        JapaneseCalendar leap = JapaneseCalendar.of(Nengo.MEIJI, 3, EastAsianMonth.valueOf(10).withLeap(), 1);
        JapaneseCalendar meiji6 = JapaneseCalendar.ofGregorian(Nengo.MEIJI, 6, 1, 1);
        assertThat(JapaneseCalendar.Unit.MONTHS.between(leap, meiji6), is(27L));
        assertThat(JapaneseCalendar.Unit.MONTHS.between(meiji6, leap), is(-27L));
        assertThat(JapaneseCalendar.Unit.MONTHS.between(leap.plus(CalendarDays.ONE), meiji6), is(26L));
        assertThat(JapaneseCalendar.Unit.MONTHS.between(meiji6, leap.plus(CalendarDays.ONE)), is(-26L));
        JapaneseCalendar ancient = leap.minus(1, JapaneseCalendar.Unit.MONTHS);
        assertThat(JapaneseCalendar.Unit.MONTHS.between(ancient, meiji6), is(28L));
    }

    @Test(expected=ArithmeticException.class)
    public void monthsOverflow() {
        JapaneseCalendar jcal = JapaneseCalendar.ofGregorian(Nengo.HEISEI, 1, 1, 8);
        System.out.println(jcal.plus(24999, JapaneseCalendar.Unit.MONTHS)); // Heisei-2084(4072)-04-08
        jcal.plus(25000, JapaneseCalendar.Unit.MONTHS);
    }

    @Test
    public void daysBetween() {
        JapaneseCalendar meiji5 = JapaneseCalendar.of(Nengo.MEIJI, 5, EastAsianMonth.valueOf(12), 1);
        JapaneseCalendar jcal = JapaneseCalendar.ofGregorian(Nengo.MEIJI, 6, 1, 3);
        assertThat(JapaneseCalendar.Unit.DAYS.between(meiji5, jcal), is(4L));
        assertThat(JapaneseCalendar.Unit.DAYS.between(jcal, meiji5), is(-4L));
    }

}