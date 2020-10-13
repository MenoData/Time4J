package net.time4j.i18n;

import java.util.Locale;

import net.time4j.format.NumberType;
import net.time4j.format.PluralCategory;
import net.time4j.format.PluralRules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class PluralRulesTest {

    @Test
    public void englishCardinals() {
        PluralRules rules =
            PluralRules.of(Locale.ENGLISH, NumberType.CARDINALS);
        assertThat(rules.getCategory(0), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(1), is(PluralCategory.ONE));
        assertThat(rules.getCategory(2), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(11), is(PluralCategory.OTHER));
    }

    @Test
    public void englishOrdinals() {
        PluralRules rules =
            PluralRules.of(Locale.ENGLISH, NumberType.ORDINALS);
        assertThat(rules.getCategory(0), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(1), is(PluralCategory.ONE));
        assertThat(rules.getCategory(21), is(PluralCategory.ONE));
        assertThat(rules.getCategory(31), is(PluralCategory.ONE));
        assertThat(rules.getCategory(51), is(PluralCategory.ONE));
        assertThat(rules.getCategory(101), is(PluralCategory.ONE));
        assertThat(rules.getCategory(2), is(PluralCategory.TWO));
        assertThat(rules.getCategory(22), is(PluralCategory.TWO));
        assertThat(rules.getCategory(92), is(PluralCategory.TWO));
        assertThat(rules.getCategory(3), is(PluralCategory.FEW));
        assertThat(rules.getCategory(23), is(PluralCategory.FEW));
        assertThat(rules.getCategory(24), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(10), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(11), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(12), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(13), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(14), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(20), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(30), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(111), is(PluralCategory.OTHER));
    }

    @Test
    public void germanCardinals() {
        PluralRules rules =
            PluralRules.of(Locale.GERMAN, NumberType.CARDINALS);
        assertThat(rules.getCategory(0), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(1), is(PluralCategory.ONE));
        assertThat(rules.getCategory(2), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(11), is(PluralCategory.OTHER));
    }

    @Test
    public void germanOrdinals() {
        PluralRules rules =
            PluralRules.of(Locale.GERMAN, NumberType.ORDINALS);
        assertThat(rules.getCategory(0), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(1), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(2), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(11), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(56), is(PluralCategory.OTHER));
    }

    @Test
    public void frenchCardinals() {
        PluralRules rules =
            PluralRules.of(Locale.FRENCH, NumberType.CARDINALS);
        assertThat(rules.getCategory(0), is(PluralCategory.ONE));
        assertThat(rules.getCategory(1), is(PluralCategory.ONE));
        assertThat(rules.getCategory(2), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(11), is(PluralCategory.OTHER));
    }

    @Test
    public void frenchOrdinals() {
        PluralRules rules =
            PluralRules.of(Locale.FRENCH, NumberType.ORDINALS);
        assertThat(rules.getCategory(0), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(1), is(PluralCategory.ONE));
        assertThat(rules.getCategory(2), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(11), is(PluralCategory.OTHER));
    }

    @Test
    public void arabicCardinals() {
        PluralRules rules =
            PluralRules.of(new Locale("ar"), NumberType.CARDINALS);
        assertThat(rules.getCategory(0), is(PluralCategory.ZERO));
        assertThat(rules.getCategory(1), is(PluralCategory.ONE));
        assertThat(rules.getCategory(2), is(PluralCategory.TWO));
        assertThat(rules.getCategory(3), is(PluralCategory.FEW));
        assertThat(rules.getCategory(10), is(PluralCategory.FEW));
        assertThat(rules.getCategory(107), is(PluralCategory.FEW));
        assertThat(rules.getCategory(11), is(PluralCategory.MANY));
        assertThat(rules.getCategory(138), is(PluralCategory.MANY));
        assertThat(rules.getCategory(100), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(101), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(102), is(PluralCategory.OTHER));
    }

    @Test
    public void russianCardinals() {
        PluralRules rules =
            PluralRules.of(new Locale("ru"), NumberType.CARDINALS);
        assertThat(rules.getCategory(1), is(PluralCategory.ONE));
        assertThat(rules.getCategory(21), is(PluralCategory.ONE));
        assertThat(rules.getCategory(2), is(PluralCategory.FEW));
        assertThat(rules.getCategory(34), is(PluralCategory.FEW));
        assertThat(rules.getCategory(0), is(PluralCategory.MANY));
        assertThat(rules.getCategory(5), is(PluralCategory.MANY));
        assertThat(rules.getCategory(19), is(PluralCategory.MANY));
        assertThat(rules.getCategory(100), is(PluralCategory.MANY));
    }

    @Test
    public void chineseCardinals() {
        PluralRules rules =
            PluralRules.of(new Locale("zh"), NumberType.CARDINALS);
        assertThat(rules.getCategory(0), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(1), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(77), is(PluralCategory.OTHER));
    }

}