package net.time4j.calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class CyclicYearTest {

    @Test
    public void equals() {
        assertThat(
            CyclicYear.of(29),
            is(CyclicYear.of(CyclicYear.Stem.REN_9_WATER_YANG, CyclicYear.Branch.CHEN_5_DRAGON)));
    }

    @Test
    public void nonEquals() {
        assertThat(
            CyclicYear.of(29).equals(SexagesimalName.of(29)),
            is(false));
    }

    @Test
    public void ofStemAndBranch() {
        for (int i = 1; i <= 60; i++) {
            CyclicYear cy = CyclicYear.of(i);
            assertThat(
                CyclicYear.of(cy.getStem(), cy.getBranch()),
                is(cy));
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofStemAndBranchInvalid() {
        CyclicYear.of(SexagesimalName.Stem.GENG_7_METAL_YANG, SexagesimalName.Branch.CHOU_2_OX);
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
            CyclicYear.of(1).getDisplayName(Locale.ENGLISH),
            is("jiǎ-zǐ"));
        assertThat(
            CyclicYear.of(2).getDisplayName(Locale.ENGLISH),
            is("yǐ-chǒu"));
        assertThat(
            CyclicYear.of(3).getDisplayName(Locale.ENGLISH),
            is("bǐng-yín"));
        assertThat(
            CyclicYear.of(4).getDisplayName(Locale.ENGLISH),
            is("dīng-mǎo"));
        assertThat(
            CyclicYear.of(5).getDisplayName(Locale.ENGLISH),
            is("wù-chén"));
        assertThat(
            CyclicYear.of(6).getDisplayName(Locale.ENGLISH),
            is("jǐ-sì"));
        assertThat(
            CyclicYear.of(7).getDisplayName(Locale.ENGLISH),
            is("gēng-wǔ"));
        assertThat(
            CyclicYear.of(8).getDisplayName(Locale.ENGLISH),
            is("xīn-wèi"));
        assertThat(
            CyclicYear.of(9).getDisplayName(Locale.ENGLISH),
            is("rén-shēn"));
        assertThat(
            CyclicYear.of(10).getDisplayName(Locale.ENGLISH),
            is("guǐ-yǒu"));
        assertThat(
            CyclicYear.of(11).getDisplayName(Locale.ENGLISH),
            is("jiǎ-xū"));
        assertThat(
            CyclicYear.of(12).getDisplayName(Locale.ENGLISH),
            is("yǐ-hài"));
        assertThat(
            CyclicYear.of(13).getDisplayName(Locale.ENGLISH),
            is("bǐng-zǐ"));
        assertThat(
            CyclicYear.of(59).getDisplayName(Locale.ENGLISH),
            is("rén-xū"));
        assertThat(
            CyclicYear.of(60).getDisplayName(Locale.ENGLISH),
            is("guǐ-hài"));
    }

    @Test
    public void roll() {
        assertThat(
            CyclicYear.of(CyclicYear.Stem.YI_2_WOOD_YIN, CyclicYear.Branch.HAI_12_PIG).roll(5),
            is(CyclicYear.of(17)));
        assertThat(
            CyclicYear.of(59).roll(3),
            is(CyclicYear.of(2)));
        assertThat(
            CyclicYear.of(2).roll(-3),
            is(CyclicYear.of(59)));
    }

    @Test
    public void parse() throws ParseException {
        CyclicYear expected = CyclicYear.of(CyclicYear.Stem.YI_2_WOOD_YIN, CyclicYear.Branch.HAI_12_PIG);
        assertThat(
            CyclicYear.parse("yi-hai", Locale.ROOT),
            is(expected));
        assertThat(
            CyclicYear.parse("yǐ-hài", Locale.ROOT),
            is(expected));
        assertThat(
            CyclicYear.parse("yi-hai", Locale.GERMAN),
            is(expected));
        assertThat(
            CyclicYear.parse("yǐ-hài", Locale.GERMAN),
            is(expected));
        assertThat(
            CyclicYear.parse("乙亥", Locale.CHINESE),
            is(expected));
        assertThat(
            CyclicYear.parse("을해", Locale.KOREAN),
            is(expected));
        assertThat(
            CyclicYear.parse("И-Хай", new Locale("ru")),
            is(expected));
    }

}