package net.time4j.calendar;

import net.time4j.format.TextWidth;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class EraNameTest {

    @Test
    public void getDisplayNameWideHebrew() {
        assertThat(
            HebrewEra.ANNO_MUNDI.getDisplayName(Locale.ENGLISH, TextWidth.WIDE),
            is("AM"));
    }

    @Test
    public void getDisplayShortWideHebrew() {
        assertThat(
            HebrewEra.ANNO_MUNDI.getDisplayName(Locale.ENGLISH, TextWidth.SHORT),
            is("AM"));
    }

    @Test
    public void getDisplayNameWideIslamic() {
        assertThat(
            HijriEra.ANNO_HEGIRAE.getDisplayName(Locale.ENGLISH, TextWidth.WIDE),
            is("AH"));
    }

    @Test
    public void getDisplayNameShortIslamic() {
        assertThat(
            HijriEra.ANNO_HEGIRAE.getDisplayName(Locale.ENGLISH, TextWidth.SHORT),
            is("AH"));
    }

    @Test
    public void getDisplayNameWidePersian() {
        assertThat(
            PersianEra.ANNO_PERSICO.getDisplayName(Locale.ENGLISH, TextWidth.WIDE),
            is("AP"));
    }

    @Test
    public void getDisplayNameShortPersian() {
        assertThat(
            PersianEra.ANNO_PERSICO.getDisplayName(Locale.GERMAN, TextWidth.SHORT),
            is("AP"));
    }

    @Test
    public void getDisplayNameWideCoptic() {
        assertThat(
            CopticEra.ANNO_MARTYRUM.getDisplayName(Locale.ENGLISH, TextWidth.WIDE),
            is("Year of the Martyrs"));
    }

    @Test
    public void getDisplayNameShortCoptic() {
        assertThat(
            CopticEra.ANNO_MARTYRUM.getDisplayName(Locale.GERMAN, TextWidth.SHORT),
            is("A.M."));
    }

    @Test
    public void getDisplayNameWideEthiopic() {
        assertThat(
            EthiopianEra.AMETE_ALEM.getDisplayName(Locale.ENGLISH, TextWidth.WIDE),
            is("Year of the World"));
        assertThat(
            EthiopianEra.AMETE_MIHRET.getDisplayName(Locale.ENGLISH, TextWidth.WIDE),
            is("Year of Grace"));
        assertThat(
            EthiopianEra.AMETE_ALEM.getDisplayName(Locale.ROOT, TextWidth.WIDE),
            is("Amete Alem"));
        assertThat(
            EthiopianEra.AMETE_MIHRET.getDisplayName(Locale.ROOT, TextWidth.WIDE),
            is("Amete Mihret"));
    }

    @Test
    public void getDisplayNameShortEthiopic() {
        assertThat(
            EthiopianEra.AMETE_ALEM.getDisplayName(Locale.ENGLISH, TextWidth.SHORT),
            is("Amete Alem"));
        assertThat(
            EthiopianEra.AMETE_MIHRET.getDisplayName(Locale.ENGLISH, TextWidth.SHORT),
            is("Amete Mihret"));
        assertThat(
            EthiopianEra.AMETE_ALEM.getDisplayName(Locale.ROOT, TextWidth.SHORT),
            is("Amete Alem"));
        assertThat(
            EthiopianEra.AMETE_MIHRET.getDisplayName(Locale.ROOT, TextWidth.SHORT),
            is("Amete Mihret"));
    }

    @Test
    public void getDisplayNameWideMinguo() {
        assertThat(
            MinguoEra.ROC.getDisplayName(Locale.GERMAN, TextWidth.WIDE),
            is("Minguo"));
    }

    @Test
    public void getDisplayNameShortMinguo() {
        assertThat(
            MinguoEra.ROC.getDisplayName(Locale.GERMAN, TextWidth.SHORT),
            is("Minguo"));
    }

    @Test
    public void getDisplayNameWideThaiSolar() {
        assertThat(
            ThaiSolarEra.RATTANAKOSIN.getDisplayName(Locale.ENGLISH, TextWidth.WIDE),
            is("Rattanakosin Sok"));
        assertThat(
            ThaiSolarEra.BUDDHIST.getDisplayName(Locale.ENGLISH, TextWidth.WIDE),
            is("Buddhist Era"));
        assertThat(
            ThaiSolarEra.RATTANAKOSIN.getDisplayName(Locale.FRANCE, TextWidth.WIDE),
            is("Rattanakosin Sok"));
        assertThat(
            ThaiSolarEra.BUDDHIST.getDisplayName(Locale.FRANCE, TextWidth.WIDE),
            is("ère bouddhique"));
    }

    @Test
    public void getDisplayNameShortThaiSolar() {
        assertThat(
            ThaiSolarEra.RATTANAKOSIN.getDisplayName(Locale.ENGLISH, TextWidth.SHORT),
            is("R.S."));
        assertThat(
            ThaiSolarEra.BUDDHIST.getDisplayName(Locale.ENGLISH, TextWidth.SHORT),
            is("BE"));
    }

    @Test
    public void getDisplayNameIndian() {
        assertThat(
            IndianEra.SAKA.getDisplayName(Locale.ENGLISH),
            is("Saka"));
        assertThat(
            IndianEra.SAKA.getDisplayName(new Locale("hi", "IN")),
            is("शक"));
    }

    @Test
    public void getDisplayNameChinese() {
        assertThat(
            ChineseEra.QING_SHUNZHI_1644_1662.getDisplayName(Locale.ROOT),
            is("Shunzhi"));
        assertThat(
            ChineseEra.QING_SHUNZHI_1644_1662.getDisplayName(Locale.ENGLISH),
            is("Shùnzhì"));
        assertThat(
            ChineseEra.QING_SHUNZHI_1644_1662.getDisplayName(Locale.CHINA),
            is("順治"));
    }

}