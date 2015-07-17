package net.time4j.calendar;

import net.time4j.PlainDate;
import net.time4j.format.Attributes;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;
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
public class MonthNameTest {

    @Test
    public void getDisplayNameWide() {
        assertThat(
            HijriMonth.DHU_AL_HIJJAH.getDisplayName(Locale.ROOT, TextWidth.WIDE, OutputContext.FORMAT),
            is("Dhuʻl-Hijjah"));
    }

    @Test
    public void getDisplayNameShort() {
        assertThat(
            HijriMonth.DHU_AL_HIJJAH.getDisplayName(Locale.ROOT, TextWidth.SHORT, OutputContext.FORMAT),
            is("Dhuʻl-H."));
    }

    @Test
    public void executeCodeDemo() throws ParseException {
        ChronoFormatter<HijriCalendar> formatter =
            ChronoFormatter.setUp(HijriCalendar.class, Locale.ENGLISH)
                .addPattern("EEE, d. MMMM yy", PatternType.GENERIC).build()
                .withCalendarVariant(HijriCalendar.VARIANT_UMALQURA)
                .with(Attributes.PIVOT_YEAR, 1500); // mapped to range 1400-1499
        HijriCalendar hijri = formatter.parse("Thu, 29. Ramadan 36");
        PlainDate date = hijri.transform(PlainDate.class);
        System.out.println(date); // 2015-07-16
    }

}