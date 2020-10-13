package net.time4j.history;

import net.time4j.PlainDate;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class YearDefinitionTest {

    @Test
    public void deathOfQueenElizabethI() {
        NewYearStrategy nys = NewYearRule.MARIA_ANUNCIATA.until(1752);
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1602, 3, 24, YearDefinition.AFTER_NEW_YEAR, nys),
            is(HistoricDate.of(HistoricEra.AD, 1603, 3, 24)));
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1603, 3, 25, YearDefinition.AFTER_NEW_YEAR, nys),
            is(HistoricDate.of(HistoricEra.AD, 1603, 3, 25)));
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1602, 3, 24, YearDefinition.BEFORE_NEW_YEAR, nys),
            is(HistoricDate.of(HistoricEra.AD, 1603, 3, 24)));
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1603, 3, 25, YearDefinition.BEFORE_NEW_YEAR, nys),
            is(HistoricDate.of(HistoricEra.AD, 1603, 3, 25)));
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1602, 3, 24, YearDefinition.DUAL_DATING, nys),
            is(HistoricDate.of(HistoricEra.AD, 1602, 3, 24)));
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1603, 3, 25, YearDefinition.DUAL_DATING, nys),
            is(HistoricDate.of(HistoricEra.AD, 1603, 3, 25)));
    }

    @Test
    public void beginOfJan2MariaAnunciata() {
        NewYearStrategy nys = NewYearRule.BEGIN_OF_JANUARY.until(1114).and(NewYearRule.MARIA_ANUNCIATA.until(1752));
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1113, 3, 24, YearDefinition.AFTER_NEW_YEAR, nys),
            is(HistoricDate.of(HistoricEra.AD, 1113, 3, 24))); // ambivalent case: choice of earlier year
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1113, 3, 24, YearDefinition.BEFORE_NEW_YEAR, nys),
            is(HistoricDate.of(HistoricEra.AD, 1114, 3, 24))); // ambivalent case: choice of later year
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1114, 3, 24, YearDefinition.AFTER_NEW_YEAR, nys),
            is(HistoricDate.of(HistoricEra.AD, 1115, 3, 24)));
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1113, 3, 25, YearDefinition.AFTER_NEW_YEAR, nys),
            is(HistoricDate.of(HistoricEra.AD, 1113, 3, 25)));
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1113, 3, 25, YearDefinition.BEFORE_NEW_YEAR, nys),
            is(HistoricDate.of(HistoricEra.AD, 1113, 3, 25)));
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1114, 3, 25, YearDefinition.AFTER_NEW_YEAR, nys),
            is(HistoricDate.of(HistoricEra.AD, 1114, 3, 25)));
    }

    @Test
    public void beginOfSep2Christmas() {
        NewYearStrategy nys = NewYearRule.BEGIN_OF_SEPTEMBER.until(1488).and(NewYearRule.CHRISTMAS_STYLE.until(1612));
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1487, 2, 24, YearDefinition.AFTER_NEW_YEAR, nys),
            is(HistoricDate.of(HistoricEra.AD, 1487, 2, 24)));
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1487, 2, 24, YearDefinition.BEFORE_NEW_YEAR, nys),
            is(HistoricDate.of(HistoricEra.AD, 1487, 2, 24)));
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1487, 10, 1, YearDefinition.AFTER_NEW_YEAR, nys),
            is(HistoricDate.of(HistoricEra.AD, 1486, 10, 1))); // ambivalent case: choice of earlier year
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1487, 10, 1, YearDefinition.BEFORE_NEW_YEAR, nys),
            is(HistoricDate.of(HistoricEra.AD, 1487, 10, 1))); // ambivalent case: choice of later year
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1487, 12, 28, YearDefinition.AFTER_NEW_YEAR, nys),
            is(HistoricDate.of(HistoricEra.AD, 1486, 12, 28)));
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1487, 12, 28, YearDefinition.BEFORE_NEW_YEAR, nys),
            is(HistoricDate.of(HistoricEra.AD, 1486, 12, 28)));
    }

    @Test
    public void christmas2beginOfSep() {
        NewYearStrategy nys = NewYearRule.CHRISTMAS_STYLE.until(1488).and(NewYearRule.BEGIN_OF_SEPTEMBER.until(1612));
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1487, 2, 24, YearDefinition.AFTER_NEW_YEAR, nys),
            is(HistoricDate.of(HistoricEra.AD, 1487, 2, 24)));
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1487, 2, 24, YearDefinition.BEFORE_NEW_YEAR, nys),
            is(HistoricDate.of(HistoricEra.AD, 1487, 2, 24)));
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1487, 12, 28, YearDefinition.AFTER_NEW_YEAR, nys),
            is(HistoricDate.of(HistoricEra.AD, 1486, 12, 28)));
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1487, 12, 28, YearDefinition.BEFORE_NEW_YEAR, nys),
            is(HistoricDate.of(HistoricEra.AD, 1486, 12, 28)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void christmas2beginOfSepInvalid1() {
        NewYearStrategy nys = NewYearRule.CHRISTMAS_STYLE.until(1488).and(NewYearRule.BEGIN_OF_SEPTEMBER.until(1612));
        HistoricDate.of(HistoricEra.AD, 1487, 10, 1, YearDefinition.AFTER_NEW_YEAR, nys);
    }

    @Test(expected=IllegalArgumentException.class)
    public void christmas2beginOfSepInvalid2() {
        NewYearStrategy nys = NewYearRule.CHRISTMAS_STYLE.until(1488).and(NewYearRule.BEGIN_OF_SEPTEMBER.until(1612));
        HistoricDate.of(HistoricEra.AD, 1487, 10, 1, YearDefinition.BEFORE_NEW_YEAR, nys);
    }

    @Test(expected=IllegalArgumentException.class)
    public void shortYearInEngland1751() {
        NewYearStrategy nys = NewYearRule.MARIA_ANUNCIATA.until(1752);
        HistoricDate.of(HistoricEra.AD, 1751, 3, 24, YearDefinition.AFTER_NEW_YEAR, nys);
        // 1751 starts on March 25th and ends on December 31st
    }

    @Test
    public void conversionWithFrenchEaster() {
        System.out.println(ChronoHistory.PROLEPTIC_JULIAN.convert(Computus.EASTERN.easterSunday(1502)));
        // AD-1502-03-27
        System.out.println(ChronoHistory.PROLEPTIC_JULIAN.convert(Computus.EASTERN.easterSunday(1503)));
        // AD-1503-04-16
        NewYearStrategy nys = NewYearRule.EASTER_STYLE.until(1567);
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1502, 4, 10, YearDefinition.AFTER_NEW_YEAR, nys),
            is(HistoricDate.of(HistoricEra.AD, 1502, 4, 10))); // after Easter
        assertThat(
            HistoricDate.of(HistoricEra.AD, 1502, 4, 10, YearDefinition.BEFORE_NEW_YEAR, nys),
            is(HistoricDate.of(HistoricEra.AD, 1503, 4, 10))); // before Easter
    }

    @Test
    public void printFrenchEaster() {
        ChronoFormatter<PlainDate> f =
            ChronoFormatter.ofDatePattern("d. MMMM G yyyy", PatternType.CLDR, Locale.FRANCE);
        assertThat(
            f.format(PlainDate.of(1503, 4, 20)),
            is("10. avril ap. J.-C. 1502/03"));
        assertThat(
            f.with(ChronoHistory.YEAR_DEFINITION, YearDefinition.AFTER_NEW_YEAR).format(PlainDate.of(1503, 4, 20)),
            is("10. avril ap. J.-C. 1502"));
        assertThat(
            f.with(ChronoHistory.YEAR_DEFINITION, YearDefinition.BEFORE_NEW_YEAR).format(PlainDate.of(1503, 4, 20)),
            is("10. avril ap. J.-C. 1502"));
    }

    @Test
    public void parseFrenchEaster() throws ParseException {
        ChronoFormatter<PlainDate> f =
            ChronoFormatter.ofDatePattern("d. MMMM G yyyy", PatternType.CLDR, Locale.FRANCE);
        assertThat(
            f.parse("10. avril ap. J.-C. 1502/03"),
            is(PlainDate.of(1503, 4, 20)));
        assertThat(
            f.with(ChronoHistory.YEAR_DEFINITION, YearDefinition.AFTER_NEW_YEAR).parse("10. avril ap. J.-C. 1502"),
            is(PlainDate.of(1502, 4, 20))); // after Easter
        assertThat(
            f.with(ChronoHistory.YEAR_DEFINITION, YearDefinition.BEFORE_NEW_YEAR).parse("10. avril ap. J.-C. 1502"),
            is(PlainDate.of(1503, 4, 20))); // before Easter (of next year)
    }

}