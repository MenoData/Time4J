package net.time4j.calendar;

import net.time4j.PlainDate;
import net.time4j.engine.EpochDays;
import net.time4j.format.Attributes;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


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
            HijriCalendar.Unit.MONTHS.between(
                start,
                end.minus(1, HijriCalendar.Unit.DAYS),
                HijriCalendar.VARIANT_UMALQURA),
            is(26));
    }

}