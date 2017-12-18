package net.time4j.format;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class DecimalNumberSystemTest {

    @Test
    public void arabicIndic() {
        assertThat(
            NumberSystem.ARABIC_INDIC.toNumeral(1234567890),
            is("١٢٣٤٥٦٧٨٩٠"));
        assertThat(
            NumberSystem.ARABIC_INDIC.toInteger("١٢٣٤٥٦٧٨٩٠"),
            is(1234567890));
        assertThat(
            NumberSystem.ARABIC_INDIC.isDecimal(),
            is(true));
        assertThat(
            NumberSystem.ARABIC_INDIC.getDigits().charAt(0),
            is('\u0660'));
    }

    @Test
    public void arabicIndicExt() {
        assertThat(
            NumberSystem.ARABIC_INDIC_EXT.toNumeral(1234567890),
            is("۱۲۳۴۵۶۷۸۹۰"));
        assertThat(
            NumberSystem.ARABIC_INDIC_EXT.toInteger("۱۲۳۴۵۶۷۸۹۰"),
            is(1234567890));
        assertThat(
            NumberSystem.ARABIC_INDIC_EXT.isDecimal(),
            is(true));
        assertThat(
            NumberSystem.ARABIC_INDIC_EXT.getDigits().charAt(0),
            is('\u06F0'));
    }

    @Test
    public void khmer() {
        assertThat(
            NumberSystem.KHMER.toNumeral(1234567890),
            is("១២៣៤៥៦៧៨៩០"));
        assertThat(
            NumberSystem.KHMER.toInteger("១២៣៤៥៦៧៨៩០"),
            is(1234567890));
        assertThat(
            NumberSystem.KHMER.isDecimal(),
            is(true));
        assertThat(
            NumberSystem.KHMER.getDigits().charAt(0),
            is('\u17E0'));
    }

    @Test
    public void orya() {
        assertThat(
            NumberSystem.ORYA.getDigits(),
            is("୦୧୨୩୪୫୬୭୮୯"));
        assertThat(
            NumberSystem.ORYA.isDecimal(),
            is(true));
        assertThat(
            NumberSystem.ORYA.getCode(),
            is("orya"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void negativeNumberToNumeral() {
        NumberSystem.ARABIC_INDIC.toNumeral(-1234567890);
    }

    @Test(expected=IllegalArgumentException.class)
    public void negativeNumberToInteger() {
        NumberSystem.ARABIC_INDIC.toInteger("-١٢٣٤٥٦٧٨٩٠");
    }

}