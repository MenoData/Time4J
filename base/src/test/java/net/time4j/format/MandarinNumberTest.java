package net.time4j.format;

import java.text.ParseException;
import java.util.Locale;
import net.time4j.PlainDate;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class MandarinNumberTest {

    @Test
    public void mandarin() {
        assertThat(
            NumberSystem.CHINESE_MANDARIN.toNumeral(1234),
            is("一千兩百三十四"));
        assertThat(
            NumberSystem.CHINESE_MANDARIN.toInteger("一千兩百三十四"),
            is(1234));
        assertThat(
            NumberSystem.CHINESE_MANDARIN.isDecimal(),
            is(false));
        assertThat(
            NumberSystem.CHINESE_MANDARIN.getDigits(),
            is("零〇一二兩三四五六七八九十百千"));
        assertThat(
            NumberSystem.CHINESE_MANDARIN.getCode(),
            is("mandarin"));
    }

    @Test
    public void simplified() {
        assertThat(
            NumberSystem.CHINESE_SIMPLIFIED.toInteger("二千〇九"),
            is(2009));
        assertThat(
            NumberSystem.CHINESE_SIMPLIFIED.toNumeral(2009),
            is("二千〇九"));
        assertThat(
            NumberSystem.CHINESE_SIMPLIFIED.toNumeral(1230),
            is("一千二百三十"));
        assertThat(
            NumberSystem.CHINESE_SIMPLIFIED.toInteger("一千兩百三十"),
            is(1230));
        assertThat(
            NumberSystem.CHINESE_SIMPLIFIED.toInteger("一千二百三十"),
            is(1230));
        assertThat(
            NumberSystem.CHINESE_SIMPLIFIED.isDecimal(),
            is(false));
        assertThat(
            NumberSystem.CHINESE_SIMPLIFIED.getDigits(),
            is("零〇一二兩三四五六七八九十百千"));
        assertThat(
            NumberSystem.CHINESE_SIMPLIFIED.getCode(),
            is("hans"));
    }

    @Test
    public void mandarin_2362() {
        assertThat(
            NumberSystem.CHINESE_MANDARIN.toNumeral(2362),
            is("兩千三百六十二"));
        assertThat(
            NumberSystem.CHINESE_MANDARIN.toInteger("兩千三百六十二"),
            is(2362));
    }

    @Test
    public void mandarin_1600() {
        assertThat(
            NumberSystem.CHINESE_MANDARIN.toNumeral(1600),
            is("一千六百"));
        assertThat(
            NumberSystem.CHINESE_MANDARIN.toInteger("一千六百"),
            is(1600));
    }

    @Test
    public void mandarin_1230() {
        assertThat(
            NumberSystem.CHINESE_MANDARIN.toNumeral(1230),
            is("一千兩百三十"));
        assertThat(
            NumberSystem.CHINESE_MANDARIN.toInteger("一千兩百三十"),
            is(1230));
        assertThat(
            NumberSystem.CHINESE_MANDARIN.toInteger("一千二百三十"),
            is(1230));
    }

    @Test
    public void mandarin_1035() {
        assertThat(
            NumberSystem.CHINESE_MANDARIN.toNumeral(1035),
            is("一千零三十五"));
        assertThat(
            NumberSystem.CHINESE_MANDARIN.toInteger("一千零三十五"),
            is(1035));
        assertThat(
            NumberSystem.CHINESE_MANDARIN.toInteger("一千〇三十五"),
            is(1035));
    }

    @Test
    public void mandarin_1006() {
        assertThat(
            NumberSystem.CHINESE_MANDARIN.toNumeral(1006),
            is("一千零六"));
        assertThat(
            NumberSystem.CHINESE_MANDARIN.toInteger("一千零六"),
            is(1006));
        assertThat(
            NumberSystem.CHINESE_MANDARIN.toInteger("一千〇六"),
            is(1006));
    }

    @Test
    public void mandarin_350() {
        assertThat(
            NumberSystem.CHINESE_MANDARIN.toNumeral(350),
            is("三百五十"));
        assertThat(
            NumberSystem.CHINESE_MANDARIN.toInteger("三百五十"),
            is(350));
    }

    @Test
    public void mandarin_302() {
        assertThat(
            NumberSystem.CHINESE_MANDARIN.toNumeral(302),
            is("三百零二"));
        assertThat(
            NumberSystem.CHINESE_MANDARIN.toInteger("三百零二"),
            is(302));
        assertThat(
            NumberSystem.CHINESE_MANDARIN.toInteger("三百〇二"),
            is(302));
    }

    @Test
    public void mandarin_20() {
        assertThat(
            NumberSystem.CHINESE_MANDARIN.toNumeral(20),
            is("二十"));
        assertThat(
            NumberSystem.CHINESE_MANDARIN.toInteger("二十"),
            is(20));
    }

    @Test
    public void mandarin_19() {
        assertThat(
            NumberSystem.CHINESE_MANDARIN.toNumeral(19),
            is("十九"));
        assertThat(
            NumberSystem.CHINESE_MANDARIN.toInteger("一十九"),
            is(19));
        assertThat(
            NumberSystem.CHINESE_MANDARIN.toInteger("十九"),
            is(19));
    }

    @Test
    public void mandarin_1_9() {
        for (int i = 1; i <= 9; i++) {
            String numeral =
                "" + NumberSystem.CHINESE_DECIMAL.getDigits().charAt(i + 1);
            assertThat(
                NumberSystem.CHINESE_MANDARIN.toNumeral(i),
                is(numeral));
            assertThat(
                NumberSystem.CHINESE_MANDARIN.toInteger(numeral),
                is(i));
        }
    }

    @Test
    public void mandarin_zero() {
        assertThat(
            NumberSystem.CHINESE_MANDARIN.toNumeral(0),
            is("零"));
        assertThat(
            NumberSystem.CHINESE_MANDARIN.toInteger("零"),
            is(0));
        assertThat(
            NumberSystem.CHINESE_MANDARIN.toInteger("〇"),
            is(0));
    }

    @Test(expected=IllegalArgumentException.class)
    public void negativeIntegerToNumber() {
        NumberSystem.CHINESE_MANDARIN.toNumeral(-123);
    }

    @Test
    public void chineseNumerals() throws ParseException {
        ChronoFormatter<PlainDate> f = 
            ChronoFormatter.setUp(PlainDate.axis(), Locale.CHINA)
                .startSection(Attributes.NUMBER_SYSTEM, NumberSystem.CHINESE_DECIMAL)
                .addPattern("yyyy年MM月", PatternType.CLDR) // two MM for padding
                .endSection()
                .startSection(Attributes.NUMBER_SYSTEM, NumberSystem.CHINESE_MANDARIN)
                .addPattern("d日", PatternType.CLDR)
                .endSection()
                .build();
        assertThat(
            f.parse("二零零九年零一月十三日"), 
            is(PlainDate.of(2009, 1, 13)));
        assertThat(
            f.print(PlainDate.of(2009, 1, 13)), 
            is("二零零九年零一月十三日"));
    }

}