package net.time4j.history;

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
    public void getDisplayNameWide() {
        assertThat(
            HistoricEra.BC.getDisplayName(Locale.ENGLISH, TextWidth.WIDE),
            is("Before Christ"));
        assertThat(
            HistoricEra.AD.getDisplayName(Locale.ENGLISH, TextWidth.WIDE),
            is("Anno Domini"));
    }

    @Test
    public void getDisplayNameShort() {
        assertThat(
            HistoricEra.BC.getDisplayName(Locale.ENGLISH, TextWidth.SHORT),
            is("BC"));
        assertThat(
            HistoricEra.AD.getDisplayName(Locale.ENGLISH, TextWidth.SHORT),
            is("AD"));
    }

    @Test
    public void getAlternativeNameWide() {
        assertThat(
            HistoricEra.BC.getAlternativeName(Locale.ENGLISH, TextWidth.WIDE),
            is("Before Common Era"));
        assertThat(
            HistoricEra.AD.getAlternativeName(Locale.ENGLISH, TextWidth.WIDE),
            is("Common Era"));
    }

    @Test
    public void getAlternativeNameShort() {
        assertThat(
            HistoricEra.BC.getAlternativeName(Locale.ENGLISH, TextWidth.SHORT),
            is("BCE"));
        assertThat(
            HistoricEra.AD.getAlternativeName(Locale.ENGLISH, TextWidth.SHORT),
            is("CE"));
    }

}