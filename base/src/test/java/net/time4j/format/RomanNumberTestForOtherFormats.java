package net.time4j.format;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class RomanNumberTestForOtherFormats {

    @Test
    public void fullRange() {
        for (int i = 1; i <= 3999; i++) {
            String numeral = NumberSystem.ROMAN.toNumeral(i);
            assertThat(
                NumberSystem.ROMAN.toInteger(numeral),
                is(i));
        }
    }

    @Test
    public void caseInsensitive() {
        for (int i = 1; i <= 3999; i++) {
            String numeral = NumberSystem.ROMAN.toNumeral(i);
            assertThat(
                NumberSystem.ROMAN.toInteger(numeral.toLowerCase(Locale.US), Leniency.STRICT),
                is(i));
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void zeroOutOfRange() {
        NumberSystem.ROMAN.toNumeral(0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void fourThousandOutOfRange() {
        NumberSystem.ROMAN.toNumeral(4000);
    }

    @Test(expected=NumberFormatException.class)
    public void parseStrictIIII() {
        NumberSystem.ROMAN.toInteger("IIII", Leniency.STRICT);
    }

    @Test
    public void parseIIII() {
        assertThat(
            NumberSystem.ROMAN.toInteger("IIII"),
            is(4));
        assertThat(
            NumberSystem.ROMAN.toInteger("IIII", Leniency.SMART),
            is(4));
        assertThat(
            NumberSystem.ROMAN.toInteger("IIII", Leniency.LAX),
            is(4));
    }

    @Test(expected=NumberFormatException.class)
    public void parseStrictMDCCCCX() {
        NumberSystem.ROMAN.toInteger("MDCCCCX", Leniency.STRICT);
    }

    @Test
    public void parseMDCCCCX() {
        assertThat(
            NumberSystem.ROMAN.toInteger("MDCCCCX"),
            is(1910));
    }

    @Test(expected=NumberFormatException.class)
    public void parseStrictMDCDIII() {
        NumberSystem.ROMAN.toInteger("MDCDIII", Leniency.STRICT);
    }

    @Test
    public void parseMDCDIII() {
        assertThat(
            NumberSystem.ROMAN.toInteger("MDCDIII"),
            is(1903));
    }

    @Test(expected=NumberFormatException.class)
    public void parseStrictIIXX() {
        NumberSystem.ROMAN.toInteger("IIXX", Leniency.STRICT);
    }

    @Test
    public void parseIIXX() {
        assertThat(
            NumberSystem.ROMAN.toInteger("IIXX"),
            is(18));
    }

    @Test(expected=NumberFormatException.class)
    public void parseStrictXIIX() {
        NumberSystem.ROMAN.toInteger("XIIX", Leniency.STRICT);
    }

    @Test
    public void parseXIIX() {
        assertThat(
            NumberSystem.ROMAN.toInteger("XIIX"),
            is(18));
    }

    @Test(expected=NumberFormatException.class)
    public void parseStrictMCMLXLI() {
        NumberSystem.ROMAN.toInteger("MCMLXLI", Leniency.STRICT);
    }

    @Test
    public void parseMCMLXLI() {
        assertThat(
            NumberSystem.ROMAN.toInteger("MCMLXLI"),
            is(1991));
    }

    @Test(expected=NumberFormatException.class)
    public void parseStrictMDCCCVIV() {
        NumberSystem.ROMAN.toInteger("MDCCCVIV", Leniency.STRICT);
    }

    @Test
    public void parseMDCCCVIV() {
        assertThat(
            NumberSystem.ROMAN.toInteger("MDCCCVIV"),
            is(1809));
    }

}