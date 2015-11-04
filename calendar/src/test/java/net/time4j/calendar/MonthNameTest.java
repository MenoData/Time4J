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
    public void getDisplayNameWideHijri() {
        assertThat(
            HijriMonth.DHU_AL_HIJJAH.getDisplayName(Locale.ROOT, TextWidth.WIDE, OutputContext.FORMAT),
            is("Dhuʻl-Hijjah"));
    }

    @Test
    public void getDisplayNameWidePersian() {
        assertThat(
            PersianMonth.ORDIBEHESHT.getDisplayName(Locale.ROOT, TextWidth.WIDE, OutputContext.FORMAT),
            is("Ordibehesht"));
    }

    @Test
    public void getDisplayNameWideCoptic() {
        assertThat(
            CopticMonth.HATOR.getDisplayName(Locale.ROOT, TextWidth.WIDE, OutputContext.FORMAT),
            is("Hator"));
    }

    @Test
    public void getDisplayNameWideEthiopian() {
        assertThat(
            EthiopianMonth.MESKEREM.getDisplayName(Locale.ROOT, TextWidth.WIDE, OutputContext.FORMAT),
            is("Meskerem"));
    }

    @Test
    public void getDisplayNameShortHijri() {
        assertThat(
            HijriMonth.DHU_AL_HIJJAH.getDisplayName(Locale.ROOT, TextWidth.SHORT, OutputContext.FORMAT),
            is("Dhuʻl-H."));
    }

}