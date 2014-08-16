package net.time4j.format;

import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class PluralRulesTest {

    @Test
    public void english() {
        PluralRules rules = PluralRules.of(Locale.ENGLISH);
        assertThat(rules.getCategory(0), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(1), is(PluralCategory.ONE));
        assertThat(rules.getCategory(2), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(11), is(PluralCategory.OTHER));
    }

    @Test
    public void german() {
        PluralRules rules = PluralRules.of(Locale.GERMAN);
        assertThat(rules.getCategory(0), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(1), is(PluralCategory.ONE));
        assertThat(rules.getCategory(2), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(11), is(PluralCategory.OTHER));
    }

    @Test
    public void french() {
        PluralRules rules = PluralRules.of(Locale.FRENCH);
        assertThat(rules.getCategory(0), is(PluralCategory.ONE));
        assertThat(rules.getCategory(1), is(PluralCategory.ONE));
        assertThat(rules.getCategory(2), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(11), is(PluralCategory.OTHER));
    }

    @Test
    public void arabic() {
        PluralRules rules = PluralRules.of(new Locale("ar"));
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
    public void russian() {
        PluralRules rules = PluralRules.of(new Locale("ru"));
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
    public void chinese() {
        PluralRules rules = PluralRules.of(new Locale("zh"));
        assertThat(rules.getCategory(0), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(1), is(PluralCategory.OTHER));
        assertThat(rules.getCategory(77), is(PluralCategory.OTHER));
    }

}