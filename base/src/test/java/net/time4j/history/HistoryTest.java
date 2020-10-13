package net.time4j.history;

import net.time4j.PlainDate;
import net.time4j.base.GregorianMath;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class HistoryTest {

    @Test
    public void isValidFirstGregorianReform() {
        ChronoHistory history = ChronoHistory.ofFirstGregorianReform();
        assertThat(
            history.isValid(new HistoricDate(HistoricEra.BC, 46, 12, 31)),
            is(false));
        assertThat(
            history.isValid(new HistoricDate(HistoricEra.BC, 45, 1, 1)),
            is(true));
        assertThat(
            history.isValid(new HistoricDate(HistoricEra.AD, 1582, 10, 4)),
            is(true));
        assertThat(
            history.isValid(new HistoricDate(HistoricEra.AD, 1582, 10, 5)),
            is(false));
        assertThat(
            history.isValid(new HistoricDate(HistoricEra.AD, 1582, 10, 14)),
            is(false));
        assertThat(
            history.isValid(new HistoricDate(HistoricEra.AD, 1582, 10, 15)),
            is(true));
        assertThat(
            history.isValid(new HistoricDate(HistoricEra.AD, 9999, 12, 31)),
            is(true));
        assertThat(
            history.isValid(new HistoricDate(HistoricEra.AD, 10_000, 1, 1)),
            is(false));
    }

    @Test
    public void convertToISOAtFirstGregorianReform() {
        ChronoHistory history = ChronoHistory.ofFirstGregorianReform();
        assertThat(
            history.convert(new HistoricDate(HistoricEra.BC, 45, 1, 1)),
            is(PlainDate.of(-45, 12, 30)));
        assertThat(
            history.convert(new HistoricDate(HistoricEra.AD, 9999, 12, 31)),
            is(PlainDate.of(9999, 12, 31)));
        assertThat(
            history.convert(new HistoricDate(HistoricEra.AD, 1582, 10, 4)),
            is(PlainDate.of(1582, 10, 14)));
        assertThat(
            history.convert(new HistoricDate(HistoricEra.AD, 1582, 10, 15)),
            is(PlainDate.of(1582, 10, 15)));
    }

    @Test
    public void convertToHistoricAtFirstGregorianReform() {
        ChronoHistory history = ChronoHistory.ofFirstGregorianReform();
        assertThat(
            history.convert(PlainDate.of(-45, 12, 30)),
            is(new HistoricDate(HistoricEra.BC, 45, 1, 1)));
        assertThat(
            history.convert(PlainDate.of(9999, 12, 31)),
            is(new HistoricDate(HistoricEra.AD, 9999, 12, 31)));
        assertThat(
            history.convert(PlainDate.of(1582, 10, 14)),
            is(new HistoricDate(HistoricEra.AD, 1582, 10, 4)));
        assertThat(
            history.convert(PlainDate.of(1582, 10, 15)),
            is(new HistoricDate(HistoricEra.AD, 1582, 10, 15)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void convertFirstGregorianReformFailure1() {
        ChronoHistory history = ChronoHistory.ofFirstGregorianReform();
        history.convert(new HistoricDate(HistoricEra.AD, 1582, 10, 5));
    }

    @Test(expected=IllegalArgumentException.class)
    public void convertFirstGregorianReformFailure2() {
        ChronoHistory history = ChronoHistory.ofFirstGregorianReform();
        history.convert(new HistoricDate(HistoricEra.AD, 1582, 10, 14));
    }

    @Test
    public void getGregorianCutOverDateFirstGregorianReform() {
        ChronoHistory history = ChronoHistory.ofFirstGregorianReform();
        assertThat(history.getGregorianCutOverDate(), is(PlainDate.of(1582, 10, 15)));
    }

    @Test
    public void getGregorianCutOverDateAtReform1800_01_01() {
        PlainDate date = PlainDate.of(1800, 1, 1);
        ChronoHistory history = ChronoHistory.ofGregorianReform(date);
        assertThat(history.getGregorianCutOverDate(), is(date));
    }

    @Test
    public void convertToISOAtReform1800_01_01() {
        PlainDate date = PlainDate.of(1800, 1, 1);
        ChronoHistory history = ChronoHistory.ofGregorianReform(date);
        assertThat(
            history.convert(HistoricDate.of(HistoricEra.AD, 1800, 1, 1)),
            is(date));
        assertThat(
            history.convert(HistoricDate.of(HistoricEra.AD, 1799, 12, 20)),
            is(PlainDate.of(1799, 12, 31)));
        assertThat(
            history.convert(HistoricDate.of(HistoricEra.AD, 1700, 2, 29)),
            is(PlainDate.of(1700, 3, 11)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void convertToISOAtReform1800_01_01Invalid() {
        PlainDate date = PlainDate.of(1800, 1, 1);
        ChronoHistory history = ChronoHistory.ofGregorianReform(date);
        history.convert(HistoricDate.of(HistoricEra.AD, 1800, 2, 29));
    }

    @Test
    public void isValidAtReform1800_01_01ForFebruary29() {
        PlainDate date = PlainDate.of(1800, 1, 1);
        ChronoHistory history = ChronoHistory.ofGregorianReform(date);
        assertThat(
            history.isValid(HistoricDate.of(HistoricEra.AD, 1700, 2, 29)),
            is(true));
        assertThat(
            history.isValid(HistoricDate.of(HistoricEra.AD, 1800, 2, 29)),
            is(false));
    }

    @Test
    public void isValidInSweden() {
        ChronoHistory history = ChronoHistory.ofSweden();
        assertThat(
            history.isValid(HistoricDate.of(HistoricEra.AD, 1700, 2, 29)),
            is(false));
        assertThat(
            history.isValid(HistoricDate.of(HistoricEra.AD, 1704, 2, 29)),
            is(true));
        assertThat(
            history.isValid(HistoricDate.of(HistoricEra.AD, 1708, 2, 29)),
            is(true));
        assertThat(
            history.isValid(HistoricDate.of(HistoricEra.AD, 1712, 2, 29)),
            is(true));
        assertThat(
            history.isValid(HistoricDate.of(HistoricEra.AD, 1712, 2, 30)),
            is(true));
        assertThat(
            history.isValid(HistoricDate.of(HistoricEra.AD, 1753, 2, 17)),
            is(true));
        assertThat(
            history.isValid(HistoricDate.of(HistoricEra.AD, 1753, 2, 18)),
            is(false));
        assertThat(
            history.isValid(HistoricDate.of(HistoricEra.AD, 1753, 2, 29)),
            is(false));
        assertThat(
            history.isValid(HistoricDate.of(HistoricEra.AD, 1753, 3, 1)),
            is(true));
    }

    @Test
    public void convertToISOInSweden() {
        ChronoHistory sweden = ChronoHistory.ofSweden();
        ChronoHistory julian = ChronoHistory.PROLEPTIC_JULIAN;
        assertThat(
            sweden.convert(HistoricDate.of(HistoricEra.AD, 1700, 3, 1)),
            is(julian.convert(HistoricDate.of(HistoricEra.AD, 1700, 2, 29))));
        assertThat(
            sweden.convert(HistoricDate.of(HistoricEra.AD, 1704, 2, 29)),
            is(julian.convert(HistoricDate.of(HistoricEra.AD, 1704, 2, 28))));
        assertThat(
            sweden.convert(HistoricDate.of(HistoricEra.AD, 1708, 2, 29)),
            is(julian.convert(HistoricDate.of(HistoricEra.AD, 1708, 2, 28))));
        assertThat(
            sweden.convert(HistoricDate.of(HistoricEra.AD, 1712, 2, 29)),
            is(julian.convert(HistoricDate.of(HistoricEra.AD, 1712, 2, 28))));
        assertThat(
            sweden.convert(HistoricDate.of(HistoricEra.AD, 1712, 2, 30)),
            is(julian.convert(HistoricDate.of(HistoricEra.AD, 1712, 2, 29))));
        assertThat(
            sweden.convert(HistoricDate.of(HistoricEra.AD, 1712, 3, 1)),
            is(julian.convert(HistoricDate.of(HistoricEra.AD, 1712, 3, 1))));
        assertThat(
            sweden.convert(HistoricDate.of(HistoricEra.AD, 1753, 2, 17)),
            is(PlainDate.of(1753, 2, 28)));
        assertThat(
            sweden.convert(HistoricDate.of(HistoricEra.AD, 1753, 3, 1)),
            is(PlainDate.of(1753, 3, 1)));
    }

    @Test
    public void convertToHistoricInSweden() {
        ChronoHistory sweden = ChronoHistory.ofSweden();
        ChronoHistory julian = ChronoHistory.PROLEPTIC_JULIAN;
        assertThat(
            sweden.convert(julian.convert(HistoricDate.of(HistoricEra.AD, 1700, 2, 29))),
            is(HistoricDate.of(HistoricEra.AD, 1700, 3, 1)));
        assertThat(
            sweden.convert(julian.convert(HistoricDate.of(HistoricEra.AD, 1704, 2, 28))),
            is(HistoricDate.of(HistoricEra.AD, 1704, 2, 29)));
        assertThat(
            sweden.convert(julian.convert(HistoricDate.of(HistoricEra.AD, 1708, 2, 28))),
            is(HistoricDate.of(HistoricEra.AD, 1708, 2, 29)));
        assertThat(
            sweden.convert(julian.convert(HistoricDate.of(HistoricEra.AD, 1712, 2, 28))),
            is(HistoricDate.of(HistoricEra.AD, 1712, 2, 29)));
        assertThat(
            sweden.convert(julian.convert(HistoricDate.of(HistoricEra.AD, 1712, 2, 29))),
            is(HistoricDate.of(HistoricEra.AD, 1712, 2, 30)));
        assertThat(
            sweden.convert(julian.convert(HistoricDate.of(HistoricEra.AD, 1712, 3, 1))),
            is(HistoricDate.of(HistoricEra.AD, 1712, 3, 1)));
        assertThat(
            sweden.convert(PlainDate.of(1753, 2, 28)),
            is(HistoricDate.of(HistoricEra.AD, 1753, 2, 17)));
        assertThat(
            sweden.convert(PlainDate.of(1753, 3, 1)),
            is(HistoricDate.of(HistoricEra.AD, 1753, 3, 1)));
    }

    @Test
    public void prolepticGregorianRanges() {
        ChronoHistory gregorian = ChronoHistory.PROLEPTIC_GREGORIAN;
        PlainDate date = PlainDate.of(2000, 2, 29);
        assertThat(gregorian.convert(date.getMinimum(gregorian.date())), is(PlainDate.axis().getMinimum()));
        assertThat(gregorian.convert(date.getMaximum(gregorian.date())), is(PlainDate.axis().getMaximum()));
    }

    @Test
    public void handleProlepticGregorianPresence() {
        ChronoHistory gregorian = ChronoHistory.PROLEPTIC_GREGORIAN;
        PlainDate date = PlainDate.of(2000, 2, 29);
        assertThat(
            gregorian.convert(date),
            is(HistoricDate.of(HistoricEra.AD, 2000, 2, 29)));
        assertThat(
            gregorian.convert(HistoricDate.of(HistoricEra.AD, 2000, 2, 29)),
            is(date));
    }

    @Test
    public void handleProlepticGregorianInFarPast() {
        ChronoHistory gregorian = ChronoHistory.PROLEPTIC_GREGORIAN;
        PlainDate date = PlainDate.axis().getMinimum();
        assertThat(
            gregorian.convert(date),
            is(HistoricDate.of(HistoricEra.BC, GregorianMath.MAX_YEAR + 1, 1, 1)));
        assertThat(
            gregorian.convert(HistoricDate.of(HistoricEra.BC, GregorianMath.MAX_YEAR + 1, 1, 1)),
            is(date));
    }

    @Test
    public void lengthOfYearInItaly() {
        assertThat(ChronoHistory.ofFirstGregorianReform().getLengthOfYear(HistoricEra.AD, 1500), is(366));
        assertThat(ChronoHistory.ofFirstGregorianReform().getLengthOfYear(HistoricEra.AD, 1582), is(355));
        assertThat(ChronoHistory.ofFirstGregorianReform().getLengthOfYear(HistoricEra.AD, 1600), is(366));
        assertThat(ChronoHistory.ofFirstGregorianReform().getLengthOfYear(HistoricEra.AD, 1700), is(365));
    }

    @Test
    public void lengthOfYearInSweden() {
        ChronoHistory sweden = ChronoHistory.ofSweden();
        assertThat(sweden.getLengthOfYear(HistoricEra.AD, 1500), is(366));
        assertThat(sweden.getLengthOfYear(HistoricEra.AD, 1600), is(366));
        assertThat(sweden.getLengthOfYear(HistoricEra.AD, 1700), is(365));
        assertThat(sweden.getLengthOfYear(HistoricEra.AD, 1704), is(366));
        assertThat(sweden.getLengthOfYear(HistoricEra.AD, 1711), is(365));
        assertThat(sweden.getLengthOfYear(HistoricEra.AD, 1712), is(367));
        assertThat(sweden.getLengthOfYear(HistoricEra.AD, 1800), is(365));
        assertThat(sweden.getLengthOfYear(HistoricEra.AD, 2000), is(366));
    }

    @Test
    public void testProlepticGregorianCutOver() {
        assertThat(
            ChronoHistory.ofGregorianReform(PlainDate.axis().getMinimum()) == ChronoHistory.PROLEPTIC_GREGORIAN,
            is(true)
        );
        assertThat(
            ChronoHistory.PROLEPTIC_GREGORIAN.hasGregorianCutOverDate(),
            is(false)
        );
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testProlepticGregorianCutOverWithoutReform() {
        ChronoHistory.ofGregorianReform(PlainDate.axis().getMinimum()).getGregorianCutOverDate();
    }

    @Test
    public void testProlepticJulianCutOver() {
        assertThat(
            ChronoHistory.ofGregorianReform(PlainDate.axis().getMaximum()) == ChronoHistory.PROLEPTIC_JULIAN,
            is(true)
        );
        assertThat(
            ChronoHistory.PROLEPTIC_JULIAN.hasGregorianCutOverDate(),
            is(false)
        );
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testProlepticJulianCutOverWithoutReform() {
        ChronoHistory.ofGregorianReform(PlainDate.axis().getMaximum()).getGregorianCutOverDate();
    }

    @Test
    public void eraNotChanged() {
        PlainDate date = PlainDate.of(2016, 1, 12);
        assertThat(
            date.with(ChronoHistory.PROLEPTIC_JULIAN.era(), HistoricEra.AD),
            is(date));
    }

    @Test(expected=IllegalArgumentException.class)
    public void eraChanged() {
        PlainDate date = PlainDate.of(2016, 1, 12);
        date.with(ChronoHistory.PROLEPTIC_JULIAN.era(), HistoricEra.BC);
    }

    @Test
    public void dateAccess() {
        assertThat(
            PlainDate.of(2016, 1, 12).get(ChronoHistory.ofFirstGregorianReform().date()),
            is(HistoricDate.of(HistoricEra.AD, 2016, 1, 12)));
        assertThat(
            PlainDate.of(1582, 10, 14).get(ChronoHistory.ofFirstGregorianReform().date()),
            is(HistoricDate.of(HistoricEra.AD, 1582, 10, 4)));
        assertThat(
            PlainDate.of(2016, 1, 12).with(
                ChronoHistory.ofFirstGregorianReform().date(),
                HistoricDate.of(HistoricEra.AD, 1582, 10, 4)),
            is(PlainDate.of(1582, 10, 14)));
        assertThat(
            PlainDate.of(2016, 1, 12).isValid(
                ChronoHistory.ofFirstGregorianReform().date(),
                HistoricDate.of(HistoricEra.AD, 1582, 10, 4)),
            is(true));
        for (int i = 5; i < 15; i++) {
            assertThat(
                PlainDate.of(2016, 1, 12).isValid(
                    ChronoHistory.ofFirstGregorianReform().date(),
                    HistoricDate.of(HistoricEra.AD, 1582, 10, i)),
                is(false));
        }
        assertThat(
            PlainDate.of(2016, 1, 12).isValid(
                ChronoHistory.ofFirstGregorianReform().date(),
                HistoricDate.of(HistoricEra.AD, 1582, 10, 15)),
            is(true));
    }

    @Test
    public void withStdYearIfAmbivalent() {
        ChronoHistory history = ChronoHistory.of(Locale.FRANCE);
        PlainDate date = history.convert(HistoricDate.of(HistoricEra.AD, 1563, 4, 10));
        assertThat(
            date.with(history.yearOfEra(), 1564),
            is(history.convert(HistoricDate.of(HistoricEra.AD, 1564, 4, 10))));
        assertThat(
            date.with(history.yearOfEra(YearDefinition.DUAL_DATING), 1564),
            is(history.convert(HistoricDate.of(HistoricEra.AD, 1564, 4, 10))));
    }

    @Test
    public void withEarlierYearIfAmbivalent() {
        ChronoHistory history = ChronoHistory.of(Locale.FRANCE);
        PlainDate date = history.convert(HistoricDate.of(HistoricEra.AD, 1563, 4, 10));
        assertThat(
            date.with(history.yearOfEra(YearDefinition.AFTER_NEW_YEAR), 1564),
            is(history.convert(HistoricDate.of(HistoricEra.AD, 1564, 4, 10))));
    }

    @Test
    public void withLaterYearIfAmbivalent() {
        ChronoHistory history = ChronoHistory.of(Locale.FRANCE);
        PlainDate date = history.convert(HistoricDate.of(HistoricEra.AD, 1563, 4, 10));
        assertThat(
            date.with(history.yearOfEra(YearDefinition.BEFORE_NEW_YEAR), 1564),
            is(history.convert(HistoricDate.of(HistoricEra.AD, 1565, 4, 10))));
    }

    @Test
    public void centuryOfEra() {
        assertThat(
            PlainDate.of(2000, 12, 31).get(ChronoHistory.PROLEPTIC_GREGORIAN.centuryOfEra()).intValue(),
            is(20));
        assertThat(
            PlainDate.of(2000, 12, 31).with(ChronoHistory.PROLEPTIC_GREGORIAN.centuryOfEra(), 19),
            is(PlainDate.of(1900, 12, 31)));
        assertThat(
            PlainDate.of(2001, 1, 1).get(ChronoHistory.PROLEPTIC_GREGORIAN.centuryOfEra()).intValue(),
            is(21));
        assertThat(
            PlainDate.of(2001, 1, 1).with(ChronoHistory.PROLEPTIC_GREGORIAN.centuryOfEra(), 19),
            is(PlainDate.of(1801, 1, 1)));
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.axis(), Locale.ENGLISH)
                .addEnglishOrdinal(ChronoHistory.PROLEPTIC_GREGORIAN.centuryOfEra())
                .addLiteral(" century")
                .build();
        assertThat(formatter.format(PlainDate.of(2000, 12, 31)), is("20th century"));
        assertThat(formatter.format(PlainDate.of(2001, 1, 1)), is("21st century"));
    }

    @Test
    public void printGermanWithEmbeddedMonth() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern("MMM", PatternType.CLDR, Locale.GERMANY)
                .with(ChronoHistory.ofFirstGregorianReform());
        assertThat(
            formatter.format(PlainDate.of(1582, 10, 1)),
            is("Sept."));
    }

    @Test
    public void printGermanWithStandaloneMonth() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern("LLL", PatternType.CLDR, Locale.GERMANY)
                .with(ChronoHistory.ofFirstGregorianReform());
        assertThat(
            formatter.format(PlainDate.of(1582, 10, 1)),
            is("Sep"));
    }

}