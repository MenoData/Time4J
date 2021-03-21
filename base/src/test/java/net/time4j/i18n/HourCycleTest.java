package net.time4j.i18n;

import net.time4j.format.internal.HourCycle;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class HourCycleTest {

    @Test
    public void standardCanada() { // english Canada
        HourCycle hc = HourCycle.of(new Locale("", "CA"));
        assertThat(hc, is(HourCycle.H12));
        assertThat(hc.isHalfdayCycle(), is(true));
        assertThat(hc.isZeroBased(), is(false));
        assertThat(hc.isUsingFlexibleDayperiods(), is(false));
    }

    @Test
    public void englishCanada() { // english Canada
        HourCycle hc = HourCycle.of(Locale.forLanguageTag("en-CA"));
        assertThat(hc, is(HourCycle.H12));
        assertThat(hc.isHalfdayCycle(), is(true));
        assertThat(hc.isZeroBased(), is(false));
        assertThat(hc.isUsingFlexibleDayperiods(), is(false));
    }

    @Test
    public void frenchCanada() {
        HourCycle hc = HourCycle.of(Locale.forLanguageTag("fr-CA"));
        assertThat(hc, is(HourCycle.H23));
        assertThat(hc.isHalfdayCycle(), is(false));
        assertThat(hc.isZeroBased(), is(true));
        assertThat(hc.isUsingFlexibleDayperiods(), is(false));
    }

    @Test
    public void frenchCanadaH11() {
        HourCycle hc = HourCycle.of(Locale.forLanguageTag("fr-CA-u-hc-h11"));
        assertThat(hc, is(HourCycle.H11));
        assertThat(hc.isHalfdayCycle(), is(true));
        assertThat(hc.isZeroBased(), is(true));
        assertThat(hc.isUsingFlexibleDayperiods(), is(false));
        assertThat(HourCycle.of(Locale.forLanguageTag("fr-u-hc-h11-rg-CAZZZZ")), is(hc));
    }

    @Test
    public void germanyH23() {
        HourCycle hc = HourCycle.of(Locale.forLanguageTag("de-DE"));
        assertThat(hc, is(HourCycle.H23));
        assertThat(hc.isHalfdayCycle(), is(false));
        assertThat(hc.isZeroBased(), is(true));
        assertThat(hc.isUsingFlexibleDayperiods(), is(false));
    }

    @Test
    public void germanyH12B() {
        HourCycle hc = HourCycle.of(Locale.forLanguageTag("de-DE-u-hc-h12"));
        assertThat(hc, is(HourCycle.H12_B));
        assertThat(hc.isHalfdayCycle(), is(true));
        assertThat(hc.isZeroBased(), is(false));
        assertThat(hc.isUsingFlexibleDayperiods(), is(true));
    }

    @Test
    public void england() {
        HourCycle hc = HourCycle.of(Locale.forLanguageTag("en-GB"));
        assertThat(hc, is(HourCycle.H23));
        assertThat(hc.isHalfdayCycle(), is(false));
        assertThat(hc.isZeroBased(), is(true));
        assertThat(hc.isUsingFlexibleDayperiods(), is(false));
    }

    @Test
    public void englandH12() {
        HourCycle hc = HourCycle.of(Locale.forLanguageTag("en-GB-u-hc-h12"));
        assertThat(hc, is(HourCycle.H12));
        assertThat(hc.isHalfdayCycle(), is(true));
        assertThat(hc.isZeroBased(), is(false));
        assertThat(hc.isUsingFlexibleDayperiods(), is(false));
    }

    @Test
    public void usa() {
        HourCycle hc = HourCycle.of(Locale.forLanguageTag("en-US"));
        assertThat(hc, is(HourCycle.H12));
        assertThat(hc.isHalfdayCycle(), is(true));
        assertThat(hc.isZeroBased(), is(false));
        assertThat(hc.isUsingFlexibleDayperiods(), is(false));
    }

    @Test
    public void usaH24() {
        HourCycle hc = HourCycle.of(Locale.forLanguageTag("en-US-u-hc-h24"));
        assertThat(hc, is(HourCycle.H24));
        assertThat(hc.isHalfdayCycle(), is(false));
        assertThat(hc.isZeroBased(), is(false));
        assertThat(hc.isUsingFlexibleDayperiods(), is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidHC() {
        HourCycle.of(Locale.forLanguageTag("en-US-u-hc-h25"));
    }

}