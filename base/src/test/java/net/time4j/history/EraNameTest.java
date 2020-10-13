package net.time4j.history;

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
    public void getDisplayNameWide() {
        assertThat(
            HistoricEra.BC.getDisplayName(Locale.ENGLISH, TextWidth.WIDE),
            is("Before Christ"));
        assertThat(
            HistoricEra.AD.getDisplayName(Locale.ENGLISH, TextWidth.WIDE),
            is("Anno Domini"));
        assertThat(
            HistoricEra.HISPANIC.getDisplayName(Locale.ENGLISH, TextWidth.WIDE),
            is("Era of Caesar"));
    }

    @Test
    public void getDisplayNameShort() {
        assertThat(
            HistoricEra.BC.getDisplayName(Locale.ENGLISH, TextWidth.SHORT),
            is("BC"));
        assertThat(
            HistoricEra.AD.getDisplayName(Locale.ENGLISH, TextWidth.SHORT),
            is("AD"));
        assertThat(
            HistoricEra.HISPANIC.getDisplayName(Locale.ENGLISH, TextWidth.SHORT),
            is("Era of Caesar"));
    }

    @Test
    public void getDisplayNameNarrow() {
        assertThat(
            HistoricEra.BC.getDisplayName(Locale.ENGLISH, TextWidth.NARROW),
            is("B"));
        assertThat(
            HistoricEra.AD.getDisplayName(Locale.ENGLISH, TextWidth.NARROW),
            is("A"));
        assertThat(
            HistoricEra.HISPANIC.getDisplayName(Locale.ENGLISH, TextWidth.NARROW),
            is("Era"));
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