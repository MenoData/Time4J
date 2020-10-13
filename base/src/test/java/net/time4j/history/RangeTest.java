package net.time4j.history;

import net.time4j.PlainDate;
import net.time4j.engine.CalendarDays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class RangeTest {

    @Test(expected=IllegalArgumentException.class)
    public void byzantineBeforeCreationOfWorld() {
        HistoricDate.of(HistoricEra.BYZANTINE, 0, 8, 31);
    }

    @Test
    public void byzantineAtOrAfterCreationOfWorld() {
        NewYearStrategy byzantine = ChronoHistory.PROLEPTIC_BYZANTINE.getNewYearStrategy();
        assertThat(
            HistoricDate.of(HistoricEra.BYZANTINE, 1, 8, 31).getYearOfEra(),
            is(1));
        assertThat(
            HistoricDate.of(HistoricEra.BYZANTINE, 1, 8, 31).getYearOfEra(byzantine),
            is(1));
        assertThat(
            HistoricDate.of(HistoricEra.BYZANTINE, 0, 9, 1).getYearOfEra(),
            is(0));
        assertThat(
            HistoricDate.of(HistoricEra.BYZANTINE, 0, 9, 1).getYearOfEra(byzantine),
            is(1));
        assertThat(
            HistoricDate.of(HistoricEra.BYZANTINE, 1, 9, 1).getYearOfEra(),
            is(1));
        assertThat(
            HistoricDate.of(HistoricEra.BYZANTINE, 1, 9, 1).getYearOfEra(byzantine),
            is(2));
    }

    @Test(expected=IllegalArgumentException.class)
    public void aucBeforeFoundationOfRome() {
        HistoricDate.of(HistoricEra.AB_URBE_CONDITA, 0, 12, 31);
    }

    @Test
    public void aucAtFoundationOfRome() {
        assertThat(
            HistoricDate.of(HistoricEra.AB_URBE_CONDITA, 1, 1, 1).getYearOfEra(NewYearStrategy.DEFAULT),
            is(1));
    }

    @Test(expected=IllegalArgumentException.class)
    public void hispanicBeforeEpoch() {
        HistoricDate.of(HistoricEra.HISPANIC, 0, 12, 31);
    }

    @Test
    public void hispanicAtOrAfterEpoch() {
        int ad = 900 - 38;
        assertThat(
            HistoricDate.of(HistoricEra.HISPANIC, 1, 1, 1).getYearOfEra(NewYearStrategy.DEFAULT),
            is(1));
        assertThat(
            HistoricDate.of(HistoricEra.HISPANIC, 1, 1, 1).getYearOfEra(NewYearRule.CHRISTMAS_STYLE.until(ad)),
            is(1)); // effective rule: BEGIN_OF_JANUARY
        assertThat(
            HistoricDate.of(HistoricEra.HISPANIC, 1, 1, 1).getYearOfEra(NewYearRule.CALCULUS_PISANUS.until(ad)),
            is(1)); // effective rule: BEGIN_OF_JANUARY
        assertThat(
            HistoricDate.of(HistoricEra.HISPANIC, 898, 12, 31).getYearOfEra(NewYearRule.CHRISTMAS_STYLE.until(ad)),
            is(899)); // effective rule: CHRISTMAS_STYLE
        assertThat(
            HistoricDate.of(HistoricEra.HISPANIC, 566 + 38, 1, 1).getYearOfEra(NewYearRule.CALCULUS_PISANUS.until(ad)),
            is(604)); // effective rule: BEGIN_OF_JANUARY
        assertThat(
            HistoricDate.of(HistoricEra.HISPANIC, 567 + 38, 1, 1).getYearOfEra(NewYearRule.CALCULUS_PISANUS.until(ad)),
            is(603)); // effective rule: CALCULUS_PISANUS
        assertThat(
            HistoricDate.of(HistoricEra.HISPANIC, 898, 1, 1).getYearOfEra(NewYearRule.CALCULUS_PISANUS.until(ad)),
            is(896)); // effective rule: CALCULUS_PISANUS
        assertThat(
            HistoricDate.of(HistoricEra.HISPANIC, 899, 12, 31).getYearOfEra(NewYearRule.CHRISTMAS_STYLE.until(ad)),
            is(899)); // effective rule: BEGIN_OF_JANUARY
    }

    @Test
    public void startOfSpanishEra() {
        ChronoHistory h = ChronoHistory.of(new Locale("", "ES"));
        HistoricDate bc38 = HistoricDate.of(HistoricEra.HISPANIC, 1, 1, 1);
        PlainDate d = h.convert(bc38);
        assertThat(d, is(PlainDate.of(-38, 12, 30))); // Julian calendar => 2 days delta
        assertThat(h.convert(d), is(bc38));
        assertThat(
            h.convert(d.minus(CalendarDays.ONE)),
            is(HistoricDate.of(HistoricEra.BC, 39, 12, 31))); // fallback to era BC
    }

}