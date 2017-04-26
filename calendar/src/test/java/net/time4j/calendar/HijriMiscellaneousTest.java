package net.time4j.calendar;

import net.time4j.PlainDate;
import net.time4j.Weekday;
import net.time4j.calendar.service.GenericDatePatterns;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.ChronoException;
import net.time4j.engine.VariantSource;
import net.time4j.format.Attributes;
import net.time4j.format.DisplayMode;
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
public class HijriMiscellaneousTest {

    @Test
    public void genericIslamicPattern() {
        String pattern = GenericDatePatterns.get("islamic", DisplayMode.FULL, new Locale("ar"));
        assertThat(pattern, is("EEEE، d MMMM، y G"));
        pattern = GenericDatePatterns.get("islamic", DisplayMode.FULL, Locale.GERMANY);
        assertThat(pattern, is("EEEE, d. MMMM y G"));
    }

    @Test
    public void executeCodeDemo() throws ParseException {
        ChronoFormatter<HijriCalendar> formatter =
            ChronoFormatter.ofPattern(
                "EEE, d. MMMM yy", PatternType.NON_ISO_DATE, Locale.ENGLISH, HijriCalendar.family())
            .withCalendarVariant(HijriCalendar.VARIANT_UMALQURA)
            .with(Attributes.PIVOT_YEAR, 1500); // mapped to range 1400-1499
        HijriCalendar hijri = formatter.parse("Thu, 29. Ramadan 36");
        PlainDate date = hijri.transform(PlainDate.class);
        assertThat(date, is(PlainDate.of(2015, 7, 16)));
    }

    @Test
    public void executeICU() throws ParseException {
        ChronoFormatter<HijriCalendar> formatter =
            ChronoFormatter.ofPattern(
                "y-MM-dd", PatternType.NON_ISO_DATE, Locale.ENGLISH, HijriCalendar.family())
            .withCalendarVariant(HijriCalendar.VARIANT_ICU4J);
        HijriCalendar hijri = formatter.parse("1-01-01");
        PlainDate date = hijri.transform(PlainDate.class);
        assertThat(date, is(PlainDate.of(622, 7, 18)));
    }

    @Test
    public void defaultFirstDayOfWeek() {
        assertThat(HijriCalendar.DAY_OF_WEEK.getDefaultMinimum(), is(Weekday.SUNDAY));
    }

    @Test(expected=ChronoException.class)
    public void withVariantInvalid() {
        HijriCalendar hijri = HijriCalendar.of(HijriCalendar.VARIANT_UMALQURA, 1395, HijriMonth.RAMADAN, 5);
        hijri.withVariant("invalid");
    }

    @Test
    public void dayAdjustmentUmalqura() {
        HijriCalendar hijri = HijriCalendar.of(HijriCalendar.VARIANT_DIYANET, 1395, HijriMonth.RAMADAN, 5);
        VariantSource v = HijriAdjustment.ofUmalqura(-3);
        assertThat(
            hijri.withVariant(v),
            is(HijriCalendar.of(v, 1395, 9, 2)));
        v = HijriAdjustment.ofUmalqura(-2);
        assertThat(
            hijri.withVariant(v),
            is(HijriCalendar.of(v, 1395, 9, 3)));
        v = HijriAdjustment.ofUmalqura(-1);
        assertThat(
            hijri.withVariant(v),
            is(HijriCalendar.of(v, 1395, 9, 4)));
        v = HijriAdjustment.ofUmalqura(0);
        assertThat(
            hijri.withVariant(v),
            is(HijriCalendar.of(v, 1395, 9, 5)));
        v = HijriAdjustment.ofUmalqura(1);
        assertThat(
            hijri.withVariant(v),
            is(HijriCalendar.of(v, 1395, 9, 6)));
        v = HijriAdjustment.ofUmalqura(2);
        assertThat(
            hijri.withVariant(v),
            is(HijriCalendar.of(v, 1395, 9, 7)));
        v = HijriAdjustment.ofUmalqura(3);
        assertThat(
            hijri.withVariant(v),
            is(HijriCalendar.of(v, 1395, 9, 8)));
    }

    @Test
    public void dayAdjustmentUmalquraMinMax() {
        CalendarSystem<HijriCalendar> calsys =
            HijriCalendar.family().getCalendarSystem(HijriAdjustment.ofUmalqura(3).getVariant());
        long min = calsys.getMinimumSinceUTC();
        assertThat(min, is(-32559L));
        long max = calsys.getMaximumSinceUTC();
        assertThat(max, is(38668L));
        HijriCalendar minHijri = calsys.transform(min);
        HijriCalendar maxHijri = calsys.transform(max);
        assertThat(minHijri.toString(), is("AH-1300-01-01[islamic-umalqura:+3]"));
        assertThat(maxHijri.toString(), is("AH-1500-12-30[islamic-umalqura:+3]"));
    }

    @Test
    public void dayAdjustmentWestIslamicCivil() {
        HijriCalendar hijri = HijriCalendar.of(HijriCalendar.VARIANT_DIYANET, 1395, HijriMonth.RAMADAN, 5);
        VariantSource v = HijriAdjustment.of(HijriAlgorithm.WEST_ISLAMIC_CIVIL, -3);
        assertThat(
            hijri.withVariant(v),
            is(HijriCalendar.of(v, 1395, 9, 2)));
        v = HijriAdjustment.of(HijriAlgorithm.WEST_ISLAMIC_CIVIL, -2);
        assertThat(
            hijri.withVariant(v),
            is(HijriCalendar.of(v, 1395, 9, 3)));
        v = HijriAdjustment.of(HijriAlgorithm.WEST_ISLAMIC_CIVIL, -1);
        assertThat(
            hijri.withVariant(v),
            is(HijriCalendar.of(v, 1395, 9, 4)));
        v = HijriAdjustment.of(HijriAlgorithm.WEST_ISLAMIC_CIVIL, 0);
        assertThat(
            hijri.withVariant(v),
            is(HijriCalendar.of(v, 1395, 9, 5)));
        v = HijriAdjustment.of(HijriAlgorithm.WEST_ISLAMIC_CIVIL, 1);
        assertThat(
            hijri.withVariant(v),
            is(HijriCalendar.of(v, 1395, 9, 6)));
        v = HijriAdjustment.of(HijriAlgorithm.WEST_ISLAMIC_CIVIL, 2);
        assertThat(
            hijri.withVariant(v),
            is(HijriCalendar.of(v, 1395, 9, 7)));
        v = HijriAdjustment.of(HijriAlgorithm.WEST_ISLAMIC_CIVIL, 3);
        assertThat(
            hijri.withVariant(v),
            is(HijriCalendar.of(v, 1395, 9, 8)));
    }

    @Test
    public void dayAdjustmentWestIslamicCivilMinMax() {
        CalendarSystem<HijriCalendar> calsys =
            HijriCalendar.family().getCalendarSystem(
                HijriAdjustment.of(HijriAlgorithm.WEST_ISLAMIC_CIVIL, 3).getVariant());
        long min = calsys.getMinimumSinceUTC();
        assertThat(min, is(-492881L));
        long max = calsys.getMaximumSinceUTC();
        assertThat(max, is(74104L));
        HijriCalendar minHijri = calsys.transform(min);
        HijriCalendar maxHijri = calsys.transform(max);
        assertThat(minHijri.toString(), is("AH-0001-01-01[islamic-civil:+3]"));
        assertThat(maxHijri.toString(), is("AH-1600-12-29[islamic-civil:+3]"));
    }

}