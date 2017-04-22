package net.time4j.calendar;

import net.time4j.PlainDate;
import net.time4j.Weekday;
import net.time4j.calendar.service.GenericDatePatterns;
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

}