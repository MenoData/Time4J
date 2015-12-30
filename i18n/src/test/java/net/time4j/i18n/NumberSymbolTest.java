package net.time4j.i18n;

import net.time4j.format.NumberSymbolProvider;

import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class NumberSymbolTest {

    @Test
    public void getZeroDigitFarsi() {
        NumberSymbolProvider nsp = SymbolProviderSPI.INSTANCE;
        assertThat(
            nsp.getZeroDigit(new Locale("fa")),
            is('\u06F0'));
    }

    @Test
    public void getMinusSignFarsi() {
        NumberSymbolProvider nsp = SymbolProviderSPI.INSTANCE;
        assertThat(
            nsp.getMinusSign(new Locale("fa")),
            is("\u200E\u2212"));
    }

    @Test
    public void getDecimalSeparatorGerman() {
        NumberSymbolProvider nsp = SymbolProviderSPI.INSTANCE;
        assertThat(
            nsp.getDecimalSeparator(new Locale("de")),
            is(','));
    }

}