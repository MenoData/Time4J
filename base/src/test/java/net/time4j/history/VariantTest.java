package net.time4j.history;

import net.time4j.PlainDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class VariantTest {

    @Test
    public void variantEnglandSimple() {
        ChronoHistory history =
            ChronoHistory.ofGregorianReform(PlainDate.of(1752, 9, 14)).with(NewYearRule.MARIA_ANUNCIATA.until(1752));
        assertThat(
            ChronoHistory.from(history.getVariant()),
            is(history));
        System.out.println(history.getVariant());
    }

    @Test
    public void variantEnglandComplex() {
        ChronoHistory history =
            ChronoHistory.of(Locale.UK);
        assertThat(
            ChronoHistory.from(history.getVariant()),
            is(history));
        System.out.println(history.getVariant());
    }

    @Test
    public void variantRussia() {
        ChronoHistory history =
            ChronoHistory.of(new Locale("ru", "RU"));
        assertThat(
            ChronoHistory.from(history.getVariant()),
            is(history));
        System.out.println(history.getVariant());
    }

    @Test
    public void variantScaliger() {
        ChronoHistory history =
            ChronoHistory.ofFirstGregorianReform().with(AncientJulianLeapYears.SCALIGER);
        assertThat(
            ChronoHistory.from(history.getVariant()),
            is(history));
        System.out.println(history.getVariant());
    }

    @Test
    public void variantProlepticGregorian() {
        ChronoHistory history =
            ChronoHistory.PROLEPTIC_GREGORIAN;
        assertThat(
            ChronoHistory.from(history.getVariant()),
            is(history));
        System.out.println(history.getVariant());
    }

    @Test
    public void variantProlepticJulian() {
        ChronoHistory history =
            ChronoHistory.PROLEPTIC_JULIAN;
        assertThat(
            ChronoHistory.from(history.getVariant()),
            is(history));
        System.out.println(history.getVariant());
    }

    @Test
    public void variantProlepticByzantine() {
        ChronoHistory history =
            ChronoHistory.PROLEPTIC_BYZANTINE;
        assertThat(
            ChronoHistory.from(history.getVariant()),
            is(history));
        System.out.println(history.getVariant());
    }

    @Test
    public void variantSweden() {
        ChronoHistory history =
            ChronoHistory.ofSweden().with(NewYearRule.MARIA_ANUNCIATA.until(1500));
        assertThat(
            ChronoHistory.from(history.getVariant()),
            is(history));
        System.out.println(history.getVariant());
    }

    @Test
    public void variantHispanic() {
        ChronoHistory history =
            ChronoHistory.of(new Locale("es", "ES"));
        assertThat(
            ChronoHistory.from(history.getVariant()),
            is(history));
        System.out.println(history.getVariant());
    }

}