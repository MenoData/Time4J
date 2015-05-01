package net.time4j.history;

import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class EraFormatTest {

    @Test
    public void print() {
        ChronoFormatter<PlainTimestamp> formatter =
            ChronoFormatter.setUp(PlainTimestamp.class, Locale.GERMANY)
                .addPattern("d. MMMM yyyy GGGG HH:mm", PatternType.CLDR).build();
        assertThat(
            formatter.format(PlainTimestamp.of(1582, 10, 14, 0, 0)),
            is("4. Oktober 1582 nach Christi Geburt 00:00"));
    }

    @Test
    public void parse() throws ParseException {
        ChronoFormatter<PlainTimestamp> formatter =
            ChronoFormatter.setUp(PlainTimestamp.class, Locale.GERMANY)
                .addPattern("d. MMMM yyyy GGGG HH:mm", PatternType.CLDR).build();
        assertThat(
            formatter.parse("4. Oktober 1582 nach Christi Geburt 00:00"),
            is(PlainTimestamp.of(1582, 10, 14, 0, 0)));
    }

    @Test
    public void printAlternative() {
        ChronoFormatter<PlainTimestamp> formatter =
            ChronoFormatter.setUp(PlainTimestamp.class, Locale.GERMANY)
                .addPattern("d. MMMM yyyy G HH:mm", PatternType.CLDR)
                .build()
                .withAlternativeEraNames();
        assertThat(
            formatter.format(PlainTimestamp.of(1582, 10, 14, 0, 0)),
            is("4. Oktober 1582 u. Z. 00:00"));
    }

    @Test
    public void parseAlternative() throws ParseException {
        ChronoFormatter<PlainTimestamp> formatter =
            ChronoFormatter.setUp(PlainTimestamp.class, Locale.GERMANY)
                .addPattern("d. MMMM yyyy G HH:mm", PatternType.CLDR)
                .build()
                .withAlternativeEraNames();
        assertThat(
            formatter.parse("4. Oktober 1582 u. Z. 00:00"),
            is(PlainTimestamp.of(1582, 10, 14, 0, 0)));
    }

    @Test
    public void printEngland1() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.GERMANY)
                .addPattern("d. MMMM yyyy G", PatternType.CLDR)
                .build()
                .withGregorianCutOver(PlainDate.of(1752, 9, 14));
        assertThat(
            formatter.format(PlainDate.of(1752, 9, 13)),
            is("2. September 1752 n. Chr."));
    }

    @Test
    public void parseEngland1() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.GERMANY)
                .addPattern("d. MMMM yyyy G", PatternType.CLDR)
                .build()
                .withGregorianCutOver(PlainDate.of(1752, 9, 14));
        assertThat(
            formatter.parse("2. September 1752 n. Chr."),
            is(PlainDate.of(1752, 9, 13)));
    }

    @Test
    public void printEngland2() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.UK)
                .addPattern("d. MMMM G yyyy", PatternType.CLDR)
                .build();
        assertThat(
            formatter.format(PlainDate.of(1752, 9, 13)),
            is("2. September AD 1752"));
    }

    @Test
    public void parseEngland2() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.UK)
                .addPattern("d. MMMM G yyyy", PatternType.CLDR)
                .build();
        assertThat(
            formatter.parse("2. September AD 1752"),
            is(PlainDate.of(1752, 9, 13)));
    }

    @Test
    public void printSweden() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, new Locale("sv", "SE"))
                .addPattern("d. MMMM yyyy GGGG", PatternType.CLDR)
                .build();
        assertThat(
            formatter.format(PlainDate.of(1712, 3, 11)),
            is("30. februari 1712 efter Kristus"));
    }

    @Test
    public void parseSweden() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, new Locale("sv", "SE"))
                .addPattern("d. MMMM yyyy GGGG", PatternType.CLDR)
                .build();
        assertThat(
            formatter.parse("30. februari 1712 efter Kristus"),
            is(PlainDate.of(1712, 3, 11)));
    }

    @Test
    public void printRedOctober() {
        Locale russia = new Locale("en", "RU");
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, russia)
                .addPattern("d. MMMM yyyy", PatternType.CLDR)
                .build()
                .withGregorianCutOver(ChronoHistory.of(russia).getGregorianCutOverDate());
        assertThat(
            formatter.format(PlainDate.of(1917, 11, 7)),
            is("25. October 1917"));
    }

    @Test
    public void parseRedOctober1() throws ParseException {
        Locale russia = new Locale("en", "RU");
        ChronoHistory history = ChronoHistory.of(russia);
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, russia)
                .addPattern("d. MMMM yyyy", PatternType.CLDR)
                .build()
                .withGregorianCutOver(history.getGregorianCutOverDate())
                .withDefault(history.era(), HistoricEra.AD);
        assertThat(
            formatter.parse("25. October 1917"),
            is(PlainDate.of(1917, 11, 7)));
    }

    @Test
    public void parseRedOctober2() throws ParseException {
        Locale russia = new Locale("en", "RU");
        ChronoHistory history = ChronoHistory.of(russia);
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, russia)
                .addInteger(history.dayOfMonth(), 1, 2)
                .addLiteral(". ")
                .addText(history.month())
                .addLiteral(' ')
                .addFixedInteger(history.yearOfEra(), 4)
                .build()
                .withDefault(history.era(), HistoricEra.AD);
        assertThat(
            formatter.parse("25. October 1917"),
            is(PlainDate.of(1917, 11, 7)));
    }

}