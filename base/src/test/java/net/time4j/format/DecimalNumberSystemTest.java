package net.time4j.format;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class DecimalNumberSystemTest {
    
    @Test
    public void consistency() {
        for (NumberSystem ns : NumberSystem.values()) {
            if (ns.isDecimal()) {
                assertThat(ns.getDigits().length(), is(10));
            }
        }
    }

    @Test
    public void arabic() {
        assertThat(
            NumberSystem.ARABIC.toNumeral(1234567890),
            is("1234567890"));
        assertThat(
            NumberSystem.ARABIC.toInteger("1234567890"),
            is(1234567890));
        assertThat(
            NumberSystem.ARABIC.isDecimal(),
            is(true));
        assertThat(
            NumberSystem.ARABIC.getDigits().charAt(0),
            is('0'));
        assertThat(
            NumberSystem.ARABIC.getCode(),
            is("latn"));
    }

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
        assertThat(
            NumberSystem.ARABIC_INDIC.getCode(),
            is("arab"));
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
        assertThat(
            NumberSystem.ARABIC_INDIC_EXT.getCode(),
            is("arabext"));
    }

    @Test
    public void chineseDecimal() {
        assertThat(
            NumberSystem.CHINESE_DECIMAL.toNumeral(1234567890),
            is("一二三四五六七八九零"));
        assertThat(
            NumberSystem.CHINESE_DECIMAL.toInteger("一二三四五六七八九零"),
            is(1234567890));
        assertThat(
            NumberSystem.CHINESE_DECIMAL.toNumeral(2009),
            is("二零零九"));
        assertThat(
            NumberSystem.CHINESE_DECIMAL.toInteger("二零零九"),
            is(2009));
        assertThat(
            NumberSystem.CHINESE_DECIMAL.toInteger("二〇〇九"),
            is(2009));
        assertThat(
            NumberSystem.CHINESE_DECIMAL.isDecimal(),
            is(true));
        assertThat(
            NumberSystem.CHINESE_DECIMAL.getDigits().charAt(0),
            is('零'));
        assertThat(
            NumberSystem.CHINESE_DECIMAL.getCode(),
            is("hanyear"));
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
        assertThat(
            NumberSystem.KHMER.getCode(),
            is("khmr"));
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