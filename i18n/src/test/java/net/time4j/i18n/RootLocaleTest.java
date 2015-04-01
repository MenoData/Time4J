package net.time4j.i18n;

import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;

import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class RootLocaleTest {

    @Test
    public void monthsWide() {
        IsoTextProviderSPI spi = new IsoTextProviderSPI();
        assertThat(
            spi.months(
                "", Locale.ROOT, TextWidth.WIDE, OutputContext.FORMAT, false
            )[0],
            is("01"));
    }

    @Test
    public void monthsAbbreviated() {
        IsoTextProviderSPI spi = new IsoTextProviderSPI();
        assertThat(
            spi.months(
                "",
                Locale.ROOT,
                TextWidth.ABBREVIATED,
                OutputContext.FORMAT,
                false
            )[0],
            is("1"));
    }

    @Test
    public void quartersWide() {
        IsoTextProviderSPI spi = new IsoTextProviderSPI();
        assertThat(
            spi.quarters(
                "", Locale.ROOT, TextWidth.WIDE, OutputContext.FORMAT
            )[0],
            is("Q1"));
    }

    @Test
    public void quartersAbbreviated() {
        IsoTextProviderSPI spi = new IsoTextProviderSPI();
        assertThat(
            spi.quarters(
                "", Locale.ROOT, TextWidth.ABBREVIATED, OutputContext.FORMAT
            )[0],
            is("Q1"));
    }

    @Test
    public void quartersNarrow() {
        IsoTextProviderSPI spi = new IsoTextProviderSPI();
        assertThat(
            spi.quarters(
                "", Locale.ROOT, TextWidth.NARROW, OutputContext.FORMAT
            )[0],
            is("1"));
    }

    @Test
    public void weekdaysWide() {
        IsoTextProviderSPI spi = new IsoTextProviderSPI();
        assertThat(
            spi.weekdays(
                "", Locale.ROOT, TextWidth.WIDE, OutputContext.FORMAT
            )[0],
            is("1"));
    }

    @Test
    public void erasWide() {
        IsoTextProviderSPI spi = new IsoTextProviderSPI();
        assertThat(
            spi.eras("", Locale.ROOT, TextWidth.WIDE)[0],
            is("BC"));
    }

    @Test
    public void meridiemsWide() {
        IsoTextProviderSPI spi = new IsoTextProviderSPI();
        assertThat(
            spi.meridiems("", Locale.ROOT, TextWidth.WIDE)[0],
            is("AM"));
    }

}