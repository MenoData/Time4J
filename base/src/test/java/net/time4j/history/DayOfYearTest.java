package net.time4j.history;

import net.time4j.PlainDate;
import net.time4j.format.Leniency;
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
public class DayOfYearTest {

    @Test
    public void printJulian() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.GERMAN)
                .addPattern("(D) yyyy G", PatternType.CLDR)
                .build()
                .with(ChronoHistory.PROLEPTIC_JULIAN);
        assertThat(
            formatter.format(PlainDate.of(1752, 9, 13)),
            is("(246) 1752 n. Chr."));
    }

    @Test
    public void parseJulian() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.GERMAN)
                .addPattern("(D) yyyy G", PatternType.CLDR)
                .build()
                .with(ChronoHistory.PROLEPTIC_JULIAN);
        assertThat(
            formatter.parse("(246) 1752 n. Chr."),
            is(PlainDate.of(1752, 9, 13)));
    }

    @Test
    public void printEngland1() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.UK)
                .addPattern("(D) G yyyy", PatternType.CLDR)
                .build();
        assertThat(
            formatter.format(PlainDate.of(1751, 5, 20)),
            is("(46) AD 1751"));
        assertThat(
            PlainDate.of(1751, 5, 20).getMinimum(ChronoHistory.of(Locale.UK).dayOfYear()),
            is(1));
        assertThat(
            PlainDate.of(1751, 5, 20).getMaximum(ChronoHistory.of(Locale.UK).dayOfYear()),
            is(365 - 31 - 28 - 24));
    }

    @Test
    public void parseEngland1() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.UK)
                .addPattern("(D) G yyyy", PatternType.CLDR)
                .build();
        assertThat(
            formatter.parse("(46) AD 1751"),
            is(PlainDate.of(1751, 5, 20)));
    }

    @Test
    public void printEngland2() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.GERMANY)
                .addPattern("(D) yyyy G", PatternType.CLDR)
                .build()
                .withGregorianCutOver(PlainDate.of(1752, 9, 14));
        assertThat(
            formatter.format(PlainDate.of(1752, 9, 14)),
            is("(247) 1752 n. Chr."));
        assertThat(
            PlainDate.of(1752, 9, 14).getMinimum(ChronoHistory.of(Locale.UK).dayOfYear()),
            is(1));
        assertThat(
            PlainDate.of(1752, 9, 14).getMaximum(ChronoHistory.of(Locale.UK).dayOfYear()),
            is(366 - 11));
    }

    @Test
    public void parseEngland2() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.GERMANY)
                .addPattern("(D) yyyy G", PatternType.CLDR)
                .build()
                .withGregorianCutOver(PlainDate.of(1752, 9, 14));
        assertThat(
            formatter.parse("(247) 1752 n. Chr."),
            is(PlainDate.of(1752, 9, 14)));
    }

    @Test
    public void printEngland3() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern("(D) GGGG yyyy", PatternType.CLDR, Locale.UK).with(Leniency.STRICT);
        assertThat(
            formatter.format(PlainDate.of(1603, 4, 3)),
            is("(365) Anno Domini 1602/03")); // death of Queen Elizabeth I.
        assertThat(
            PlainDate.of(1603, 4, 3).getMinimum(ChronoHistory.of(Locale.UK).dayOfYear()),
            is(1));
        assertThat(
            PlainDate.of(1603, 4, 3).getMaximum(ChronoHistory.of(Locale.UK).dayOfYear()),
            is(365));
    }

    @Test
    public void parseEngland3() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern("(D) GGGG yyyy", PatternType.CLDR, Locale.UK).with(Leniency.STRICT);
        assertThat(
            formatter.parse("(365) Anno Domini 1602/03"), // death of Queen Elizabeth I. (officially 1602)
            is(PlainDate.of(1603, 4, 3)));
    }

    @Test
    public void printEngland4() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.UK)
                .addPattern("(D) GGGG yyyy", PatternType.CLDR)
                .build()
                .with(Leniency.STRICT);
        assertThat(
            formatter.format(PlainDate.of(1603, 4, 4)),
            is("(1) Anno Domini 1603")); // new year
        assertThat(
            PlainDate.of(1603, 4, 4).getMinimum(ChronoHistory.of(Locale.UK).dayOfYear()),
            is(1));
        assertThat(
            PlainDate.of(1603, 4, 4).getMaximum(ChronoHistory.of(Locale.UK).dayOfYear()),
            is(366));
    }

    @Test
    public void parseEngland4() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.UK)
                .addPattern("(D) GGGG yyyy", PatternType.CLDR)
                .build()
                .with(Leniency.STRICT);
        assertThat(
            formatter.parse("(1) Anno Domini 1603"), // new year
            is(PlainDate.of(1603, 4, 4)));
    }

    @Test
    public void printSweden() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, new Locale("sv"))
                .addPattern("(D) yyyy", PatternType.CLDR)
                .build()
                .with(ChronoHistory.ofSweden());
        assertThat(
            formatter.format(PlainDate.of(1712, 3, 11)),
            is("(61) 1712"));
        assertThat(
            PlainDate.of(1712, 3, 11).getMinimum(ChronoHistory.ofSweden().dayOfYear()),
            is(1));
        assertThat(
            PlainDate.of(1712, 3, 11).getMaximum(ChronoHistory.ofSweden().dayOfYear()),
            is(367));
    }

    @Test
    public void parseSweden() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, new Locale("sv"))
                .addPattern("(D) yyyy", PatternType.CLDR)
                .build()
                .with(ChronoHistory.ofSweden())
                .withDefault(ChronoHistory.ofSweden().era(), HistoricEra.AD);
        assertThat(
            formatter.parse("(61) 1712"),
            is(PlainDate.of(1712, 3, 11)));
    }

    @Test
    public void printPisa() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern("G yyyy (D)", PatternType.CLDR, new Locale("en", "IT", "PISA"))
                .with(Leniency.STRICT);
        assertThat(
            formatter.format(PlainDate.of(1700, 1, 30)),
            is("AD 1698/1700 (312)"));
    }

    @Test
    public void parsePisa() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern("G yyyy (D)", PatternType.CLDR, new Locale("en", "IT", "PISA"))
                .with(Leniency.STRICT);
        assertThat(
            formatter.parse("AD 1698/1700 (312)"),
            is(PlainDate.of(1700, 1, 30)));
    }

    @Test(expected=ParseException.class)
    public void parseEnglandNonDualDate() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern("(D) GGGG yyyy", PatternType.CLDR, Locale.UK).with(Leniency.STRICT);
        formatter.parse("(365) Anno Domini 1602"); // conflict: text-year=1602, parsed-year=1603
    }

    @Test(expected=ParseException.class)
    public void parsePisaNonDualDate() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern("G yyyy (D)", PatternType.CLDR, new Locale("en", "IT", "PISA"))
                .with(Leniency.STRICT);
        formatter.parse("AD 1698 (312)"); // conflict: text-year=1698, parsed-year=1700
    }

}