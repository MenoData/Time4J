package net.time4j.calendar;

import net.time4j.engine.CalendarDays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class HijriUnitTest {

    @Test
    public void plusMonths() {
        HijriCalendar hijri = HijriCalendar.of(HijriAlgorithm.EAST_ISLAMIC_ASTRO, 1437, 12, 1);

        for (int m = 1; m <= 13; m++) {
            hijri = hijri.with(HijriCalendar.DAY_OF_MONTH.maximized()); // last day of month
            HijriCalendar test = hijri.plus(1, HijriCalendar.Unit.MONTHS);
            assertThat(
                hijri.with(HijriCalendar.MONTH_OF_YEAR.incremented()),
                is(test));
            hijri = test;
        }
    }

    @Test
    public void minusMonths() {
        HijriCalendar hijri = HijriCalendar.of(HijriAlgorithm.EAST_ISLAMIC_ASTRO, 1437, 12, 1);

        for (int m = 1; m <= 13; m++) {
            hijri = hijri.with(HijriCalendar.DAY_OF_MONTH.maximized()); // last day of month
            HijriCalendar test = hijri.minus(1, HijriCalendar.Unit.MONTHS);
            assertThat(
                hijri.with(HijriCalendar.MONTH_OF_YEAR.decremented()),
                is(test));
            hijri = test;
        }
    }

    @Test
    public void monthsBetween() {
        HijriCalendar start = HijriCalendar.of(HijriAlgorithm.EAST_ISLAMIC_ASTRO, 1436, 12, 30);
        HijriCalendar end = HijriCalendar.of(HijriAlgorithm.EAST_ISLAMIC_CIVIL, 1439, 3, 29);
        // start.withVariant(HijriCalendar.VARIANT_UMALQURA); // AH-1436-12-30[islamic-umalqura]
        // end.withVariant(HijriCalendar.VARIANT_UMALQURA); // AH-1439-03-30[islamic-umalqura]
        assertThat(
            HijriCalendar.Unit.MONTHS.between(start, end, HijriCalendar.VARIANT_UMALQURA),
            is(27));
        assertThat(
            start.plus(27, HijriCalendar.Unit.MONTHS),
            is(end.withVariant(HijriAlgorithm.EAST_ISLAMIC_ASTRO)));
        assertThat(
            HijriCalendar.Unit.MONTHS.between(
                start,
                end.minus(1, HijriCalendar.Unit.DAYS),
                HijriCalendar.VARIANT_UMALQURA),
            is(26));
    }

    @Test
    public void yearsBetween() {
        HijriCalendar start = HijriCalendar.of(HijriAlgorithm.EAST_ISLAMIC_ASTRO, 1436, 12, 30);
        HijriCalendar end = HijriCalendar.of(HijriAlgorithm.EAST_ISLAMIC_CIVIL, 1438, 12, 29);
        assertThat(
            HijriCalendar.Unit.YEARS.between(start, end, HijriCalendar.VARIANT_UMALQURA),
            is(2));
        assertThat(
            start.plus(2, HijriCalendar.Unit.YEARS),
            is(HijriCalendar.of(HijriAlgorithm.EAST_ISLAMIC_ASTRO, 1438, 12, 29)));
        assertThat(
            HijriCalendar.Unit.YEARS.between(
                start,
                end.minus(1, HijriCalendar.Unit.DAYS),
                HijriCalendar.VARIANT_UMALQURA),
            is(1));
    }

    @Test
    public void daysBetween() {
        HijriCalendar start = HijriCalendar.of(HijriAlgorithm.EAST_ISLAMIC_ASTRO, 1436, 12, 30);
        HijriCalendar end = HijriCalendar.of(HijriAlgorithm.EAST_ISLAMIC_CIVIL, 1438, 12, 29);
        int delta = (int) CalendarDays.between(start, end).getAmount(); // 709
        assertThat(
            HijriCalendar.Unit.DAYS.between(start, end, HijriCalendar.VARIANT_UMALQURA),
            is(delta));
        assertThat(
            start.plus(delta, HijriCalendar.Unit.DAYS),
            is(end.withVariant(HijriAlgorithm.EAST_ISLAMIC_ASTRO)));
    }

    @Test
    public void weeksBetween() {
        HijriCalendar start = HijriCalendar.of(HijriAlgorithm.EAST_ISLAMIC_ASTRO, 1436, 12, 30);
        HijriCalendar end = HijriCalendar.of(HijriAlgorithm.EAST_ISLAMIC_CIVIL, 1438, 12, 29);
        assertThat(
            HijriCalendar.Unit.WEEKS.between(start, end, HijriCalendar.VARIANT_UMALQURA),
            is(101));
        assertThat(
            start.plus(101, HijriCalendar.Unit.WEEKS),
            is(HijriCalendar.of(HijriAlgorithm.EAST_ISLAMIC_ASTRO, 1438, 12, 28)));
    }

}