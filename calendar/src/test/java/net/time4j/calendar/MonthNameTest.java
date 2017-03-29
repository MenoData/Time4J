package net.time4j.calendar;

import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

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

    @Test
    public void getDisplayNameJapaneseGregorian() {
        assertThat(
            JapaneseMonth.ofGregorian(1).getDisplayName(Locale.ENGLISH, TextWidth.WIDE, OutputContext.FORMAT),
            is("January"));
        assertThat(
            JapaneseMonth.ofGregorian(1).getDisplayName(Locale.ENGLISH, TextWidth.ABBREVIATED, OutputContext.FORMAT),
            is("Jan"));
        assertThat(
            JapaneseMonth.ofGregorian(1).getDisplayName(Locale.ENGLISH, TextWidth.NARROW, OutputContext.FORMAT),
            is("J"));
    }

    @Test
    public void getDisplayNameJapaneseLunisolar() {
        assertThat(
            JapaneseMonth.ofLunisolarStdType(1).getDisplayName(Locale.ENGLISH, TextWidth.WIDE, OutputContext.FORMAT),
            is("1"));
        assertThat(
            JapaneseMonth.ofLunisolarStdType(1).getDisplayName(Locale.ENGLISH, TextWidth.WIDE, OutputContext.STANDALONE),
            is("Mutsuki"));
        assertThat(
            JapaneseMonth.ofLunisolarLeapType(1).getDisplayName(Locale.ENGLISH, TextWidth.WIDE, OutputContext.FORMAT),
            is("*1"));
        assertThat(
            JapaneseMonth.ofLunisolarLeapType(1).getDisplayName(Locale.ENGLISH, TextWidth.WIDE, OutputContext.STANDALONE),
            is("*Mutsuki"));
        assertThat(
            JapaneseMonth.ofLunisolarStdType(7).getDisplayName(Locale.JAPANESE, TextWidth.WIDE, OutputContext.FORMAT),
            is("七月"));
        assertThat(
            JapaneseMonth.ofLunisolarStdType(7).getDisplayName(Locale.JAPANESE, TextWidth.WIDE, OutputContext.STANDALONE),
            is("文月"));
        assertThat(
            JapaneseMonth.ofLunisolarLeapType(7).getDisplayName(Locale.JAPANESE, TextWidth.WIDE, OutputContext.FORMAT),
            is("閏七月"));
        assertThat(
            JapaneseMonth.ofLunisolarLeapType(7).getDisplayName(Locale.JAPANESE, TextWidth.WIDE, OutputContext.STANDALONE),
            is("閏文月"));
    }

}