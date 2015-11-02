package net.time4j.calendar;

import net.time4j.format.TextWidth;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class EraNameTest {

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

}