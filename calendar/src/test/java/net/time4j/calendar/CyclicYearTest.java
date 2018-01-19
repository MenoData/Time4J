package net.time4j.calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class CyclicYearTest {

    @Test
    public void equals() {
        assertThat(
            CyclicYear.of(29),
            is(CyclicYear.of(CyclicYear.Stem.REN_9_WATER_YANG, CyclicYear.Branch.CHEN_5_DRAGON)));
    }

    @Test
    public void getNumber() {
        assertThat(
            CyclicYear.of(29).getNumber(),
            is(29));
    }

    @Test
    public void getStem() {
        assertThat(
            CyclicYear.of(29).getStem(),
            is(CyclicYear.Stem.REN_9_WATER_YANG));
    }

    @Test
    public void getBranch() {
        assertThat(
            CyclicYear.of(29).getBranch(),
            is(CyclicYear.Branch.CHEN_5_DRAGON));
    }

    @Test
    public void getZodiac() {
        assertThat(
            CyclicYear.of(29).getZodiac(Locale.GERMAN),
            is("Drache"));
    }

    @Test
    public void getDisplayName() {
        assertThat(
            CyclicYear.of(1).getDisplayName(Locale.ROOT),
            is("jiǎ-zǐ"));
        assertThat(
            CyclicYear.of(2).getDisplayName(Locale.ROOT),
            is("yǐ-chǒu"));
        assertThat(
            CyclicYear.of(3).getDisplayName(Locale.ROOT),
            is("bǐng-yín"));
        assertThat(
            CyclicYear.of(4).getDisplayName(Locale.ROOT),
            is("dīng-mǎo"));
        assertThat(
            CyclicYear.of(5).getDisplayName(Locale.ROOT),
            is("wù-chén"));
        assertThat(
            CyclicYear.of(6).getDisplayName(Locale.ROOT),
            is("jǐ-sì"));
        assertThat(
            CyclicYear.of(7).getDisplayName(Locale.ROOT),
            is("gēng-wǔ"));
        assertThat(
            CyclicYear.of(8).getDisplayName(Locale.ROOT),
            is("xīn-wèi"));
        assertThat(
            CyclicYear.of(9).getDisplayName(Locale.ROOT),
            is("rén-shēn"));
        assertThat(
            CyclicYear.of(10).getDisplayName(Locale.ROOT),
            is("guǐ-yǒu"));
        assertThat(
            CyclicYear.of(11).getDisplayName(Locale.ROOT),
            is("jiǎ-xū"));
        assertThat(
            CyclicYear.of(12).getDisplayName(Locale.ROOT),
            is("yǐ-hài"));
        assertThat(
            CyclicYear.of(13).getDisplayName(Locale.ROOT),
            is("bǐng-zǐ"));
        assertThat(
            CyclicYear.of(59).getDisplayName(Locale.ROOT),
            is("rén-xū"));
        assertThat(
            CyclicYear.of(60).getDisplayName(Locale.ROOT),
            is("guǐ-hài"));
    }

}