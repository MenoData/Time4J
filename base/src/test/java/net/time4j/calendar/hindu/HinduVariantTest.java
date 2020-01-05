package net.time4j.calendar.hindu;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class HinduVariantTest {

    @Test
    public void values() {
        assertThat(
            HinduEra.values().length,
            is(6));
    }

    @Test
    public void isSolar() {
        for (HinduRule rule : HinduRule.values()) {
            assertThat(
                HinduVariant.ofSolar(rule).isSolar(),
                is(true));
        }
        assertThat(
            HinduVariant.ofAmanta(HinduRule.ORISSA).isSolar(),
            is(false));
        assertThat(
            HinduVariant.ofGujaratStartingYearOnAshadha().isSolar(),
            is(false));
        assertThat(
            HinduVariant.ofGujaratStartingYearOnKartika().isSolar(),
            is(false));
        assertThat(
            HinduVariant.ofPurnimanta().isSolar(),
            is(false));
    }

    @Test
    public void isLunisolar() {
        assertThat(
            HinduVariant.ofSolar(HinduRule.ORISSA).isLunisolar(),
            is(false));
        assertThat(
            HinduVariant.ofAmanta(HinduRule.ORISSA).isLunisolar(),
            is(true));
        assertThat(
            HinduVariant.ofGujaratStartingYearOnAshadha().isLunisolar(),
            is(true));
        assertThat(
            HinduVariant.ofGujaratStartingYearOnKartika().isLunisolar(),
            is(true));
        assertThat(
            HinduVariant.ofPurnimanta().isLunisolar(),
            is(true));
    }

    @Test
    public void isPurnimanta() {
        assertThat(
            HinduVariant.ofSolar(HinduRule.ORISSA).isPurnimanta(),
            is(false));
        assertThat(
            HinduVariant.ofAmanta(HinduRule.ORISSA).isPurnimanta(),
            is(false));
        assertThat(
            HinduVariant.ofGujaratStartingYearOnAshadha().isPurnimanta(),
            is(false));
        assertThat(
            HinduVariant.ofGujaratStartingYearOnKartika().isPurnimanta(),
            is(false));
        assertThat(
            HinduVariant.ofPurnimanta().isPurnimanta(),
            is(true));
    }

    @Test
    public void isUsingElapsedYears() {
        assertThat(
            HinduVariant.ofSolar(HinduRule.ORISSA, HinduEra.SAKA).isUsingElapsedYears(),
            is(true));
        assertThat(
            HinduVariant.ofSolar(HinduRule.ORISSA, HinduEra.SAKA).withElapsedYears().isUsingElapsedYears(),
            is(true));
        assertThat(
            HinduVariant.ofSolar(HinduRule.ORISSA, HinduEra.SAKA).withCurrentYears().isUsingElapsedYears(),
            is(false));
        assertThat(
            HinduVariant.ofSolar(HinduRule.TAMIL, HinduEra.SAKA).isUsingElapsedYears(),
            is(false));
        assertThat(
            HinduVariant.ofSolar(HinduRule.TAMIL, HinduEra.SAKA).withElapsedYears().isUsingElapsedYears(),
            is(true));
        assertThat(
            HinduVariant.ofSolar(HinduRule.TAMIL, HinduEra.SAKA).withCurrentYears().isUsingElapsedYears(),
            is(false));
        assertThat(
            HinduVariant.ofSolar(HinduRule.MALAYALI, HinduEra.KOLLAM).isUsingElapsedYears(),
            is(false));
        assertThat(
            HinduVariant.ofSolar(HinduRule.ARYA_SIDDHANTA, HinduEra.KALI_YUGA).isUsingElapsedYears(),
            is(true));
    }

    @Test
    public void getDefaultEra() {
        HinduVariant v1 = HinduVariant.ofSolar(HinduRule.ORISSA, HinduEra.SAKA);
        HinduVariant v2 = v1.with(HinduEra.NEPALESE);
        assertThat(
            v1.getDefaultEra(),
            is(HinduEra.SAKA));
        assertThat(
            v2.getDefaultEra(),
            is(HinduEra.NEPALESE));
    }

    @Test
    public void ofAmanta() {
        assertThat(
            HinduVariant.ofAmanta(HinduRule.ORISSA),
            is(HinduVariant.ofAmanta(HinduRule.ORISSA, HinduEra.VIKRAMA)));
    }

}
