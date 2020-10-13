package net.time4j.calendar;

import net.time4j.format.NumberSystem;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


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
    public void getDisplayNameWideIndian() {
        assertThat(
            IndianMonth.CHAITRA.getDisplayName(Locale.ROOT, TextWidth.WIDE, OutputContext.FORMAT),
            is("Chaitra"));
    }

    @Test
    public void getDisplayNameShortHijri() {
        assertThat(
            HijriMonth.DHU_AL_HIJJAH.getDisplayName(Locale.ROOT, TextWidth.SHORT, OutputContext.FORMAT),
            is("Dhuʻl-H."));
    }

    @Test
    public void getDisplayNameOldJapanese() {
        assertThat(
            EastAsianMonth.valueOf(1).getOldJapaneseName(Locale.ENGLISH),
            is("Mutsuki"));
        assertThat(
            EastAsianMonth.valueOf(1).withLeap().getOldJapaneseName(Locale.ENGLISH),
            is("Mutsuki"));
        assertThat(
            EastAsianMonth.valueOf(7).getOldJapaneseName(Locale.JAPANESE),
            is("文月"));
    }

    @Test
    public void getDisplayNameEastAsian() {
        assertThat(
            EastAsianMonth.valueOf(1).getDisplayName(Locale.ENGLISH, NumberSystem.ARABIC),
            is("1"));
        assertThat(
            EastAsianMonth.valueOf(1).withLeap().getDisplayName(Locale.ENGLISH, NumberSystem.ARABIC),
            is("i1"));
        assertThat(
            EastAsianMonth.valueOf(7).getDisplayName(Locale.JAPANESE, NumberSystem.ARABIC),
            is("7月"));
        assertThat(
            EastAsianMonth.valueOf(7).withLeap().getDisplayName(Locale.JAPANESE, NumberSystem.ARABIC),
            is("閏7月"));
        assertThat(
            EastAsianMonth.valueOf(7).getDisplayName(Locale.JAPANESE, NumberSystem.JAPANESE),
            is("七月"));
        assertThat(
            EastAsianMonth.valueOf(7).withLeap().getDisplayName(Locale.JAPANESE, NumberSystem.JAPANESE),
            is("閏七月"));
    }

}