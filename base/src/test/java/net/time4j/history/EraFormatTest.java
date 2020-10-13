package net.time4j.history;

import net.time4j.CalendarUnit;
import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;
import net.time4j.format.Attributes;
import net.time4j.format.Leniency;
import net.time4j.format.NumberSystem;
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
public class EraFormatTest {

    @Test
    public void printStdEraName() {
        ChronoFormatter<PlainTimestamp> formatter =
            ChronoFormatter.setUp(PlainTimestamp.class, Locale.GERMANY)
                .addPattern("d. MMMM yyyy GGGG HH:mm", PatternType.CLDR).build();
        assertThat(
            formatter.format(PlainTimestamp.of(1582, 10, 14, 0, 0)),
            is("4. Oktober 1582 n. Chr. 00:00"));
    }

    @Test
    public void parseStdEraName() throws ParseException {
        ChronoFormatter<PlainTimestamp> formatter =
            ChronoFormatter.setUp(PlainTimestamp.class, Locale.GERMANY)
                .addPattern("d. MMMM yyyy GGGG HH:mm", PatternType.CLDR).build();
        assertThat(
            formatter.parse("4. Oktober 1582 n. Chr. 00:00"),
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
    public void printGermanWithLatinEra() {
        ChronoFormatter<PlainTimestamp> formatter =
            ChronoFormatter.setUp(PlainTimestamp.class, Locale.GERMANY)
                .addPattern("d. MMMM yyyy GGGG HH:mm", PatternType.CLDR)
                .build()
                .withLatinEraNames();
        assertThat(
            formatter.format(PlainTimestamp.of(1582, 10, 14, 0, 0)),
            is("4. Oktober 1582 Anno Domini 00:00"));
    }

    @Test
    public void parseGermanWithLatinEra() throws ParseException {
        ChronoFormatter<PlainTimestamp> formatter =
            ChronoFormatter.setUp(PlainTimestamp.class, Locale.GERMANY)
                .addPattern("d. MMMM yyyy GGGG HH:mm", PatternType.CLDR)
                .build()
                .withLatinEraNames();
        assertThat(
            formatter.parse("4. Oktober 1582 Anno Domini 00:00"),
            is(PlainTimestamp.of(1582, 10, 14, 0, 0)));
    }

    @Test
    public void printJulian() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.GERMAN)
                .addPattern("d. MMMM yyyy G", PatternType.CLDR)
                .build()
                .with(ChronoHistory.PROLEPTIC_JULIAN);
        assertThat(
            formatter.format(PlainDate.of(1752, 9, 13)),
            is("2. September 1752 n. Chr."));
    }

    @Test
    public void parseJulian() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.GERMAN)
                .addPattern("d. MMMM yyyy G", PatternType.CLDR)
                .build()
                .with(ChronoHistory.PROLEPTIC_JULIAN);
        assertThat(
            formatter.parse("2. September 1752 n. Chr."),
            is(PlainDate.of(1752, 9, 13)));
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
    public void printEngland3() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern("d. MMMM GGGG yyyy", PatternType.CLDR, Locale.UK).with(Leniency.STRICT);
        assertThat(
            formatter.format(PlainDate.of(1603, 4, 3)),
            is("24. March Anno Domini 1602/03")); // death of Queen Elizabeth I.
    }

    @Test
    public void printEngland3Roman() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.ENGLISH)
                .addPattern("d. MMMM GGGG ", PatternType.CLDR)
                .startSection(Attributes.NUMBER_SYSTEM, NumberSystem.ROMAN)
                .addPattern("yyyy", PatternType.CLDR)
                .endSection()
                .build()
                .with(ChronoHistory.of(Locale.UK))
                .with(Leniency.STRICT);
        assertThat(
            formatter.format(PlainDate.of(1603, 4, 3)),
            is("24. March Anno Domini MDCII/MDCIII")); // death of Queen Elizabeth I.
    }

    @Test
    public void parseEngland3() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern("d. MMMM GGGG yyyy", PatternType.CLDR, Locale.UK).with(Leniency.STRICT);
        assertThat(
            formatter.parse("24. March Anno Domini 1602/03"), // death of Queen Elizabeth I. (officially 1602)
            is(PlainDate.of(1603, 4, 3)));
        assertThat(
            formatter.parse("24. March Anno Domini 1602/3"), // test for year-of-era-part < 10
            is(PlainDate.of(1603, 4, 3)));
        assertThat(
            formatter.parse("24. March Anno Domini 1751/2"), // test for year-of-era-part < 10
            is(PlainDate.of(1752, 4, 4)));
    }

    @Test
    public void parseEngland3Roman() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.ENGLISH)
                .addPattern("d. MMMM GGGG ", PatternType.CLDR)
                .startSection(Attributes.NUMBER_SYSTEM, NumberSystem.ROMAN)
                .addPattern("yyyy", PatternType.CLDR)
                .endSection()
                .build()
                .with(ChronoHistory.of(Locale.UK))
                .with(Leniency.STRICT);
        assertThat(
            formatter.parse("24. March Anno Domini MDCII/MDCIII"), // death of Queen Elizabeth I. (officially 1602)
            is(PlainDate.of(1603, 4, 3)));
    }

    @Test
    public void printEngland4() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.UK)
                .addPattern("d. MMMM GGGG yyyy", PatternType.CLDR)
                .build()
                .with(Leniency.STRICT);
        assertThat(
            formatter.format(PlainDate.of(1603, 4, 4)),
            is("25. March Anno Domini 1603")); // new year
    }

    @Test
    public void parseEngland4() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.UK)
                .addPattern("d. MMMM GGGG yyyy", PatternType.CLDR)
                .build()
                .with(Leniency.STRICT);
        assertThat(
            formatter.parse("25. March Anno Domini 1603"), // new year
            is(PlainDate.of(1603, 4, 4)));
        assertThat(
            formatter.parse("24. March Anno Domini 1603"), // death of Queen Elizabeth I. using year-of-era only
            is(PlainDate.of(1603, 4, 3)));
    }

    @Test(expected=ParseException.class) // due to trailing chars "/4"
    public void parseEngland5() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.UK)
                .addPattern("d. MMMM GGGG yyyy", PatternType.CLDR)
                .build()
                .with(Leniency.STRICT);
        formatter.parse("24. March Anno Domini 1602/4"); // inplausible difference in year part
    }

    @Test
    public void printSweden1() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, new Locale("sv", "SE"))
                .addPattern("d. MMMM yyyy GGGG", PatternType.CLDR)
                .build();
        ChronoFormatter<PlainDate> formatterAlt =
            ChronoFormatter.setUp(PlainDate.class, new Locale("sv", "SE"))
                .addPattern("d. MMMM yyyy GGGG", PatternType.CLDR_DATE)
                .build();
        assertThat(
            formatter.format(PlainDate.of(1712, 3, 11)),
            is("30. februari 1712 efter Kristus"));
        assertThat(
            formatterAlt.format(PlainDate.of(1712, 3, 11)),
            is("30. februari 1712 efter Kristus"));
    }

    @Test
    public void parseSweden1() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, new Locale("sv", "SE"))
                .addPattern("d. MMMM yyyy GGGG", PatternType.CLDR)
                .build();
        assertThat(
            formatter.parse("30. februari 1712 efter Kristus"),
            is(PlainDate.of(1712, 3, 11)));
    }

    @Test
    public void printSweden2() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, new Locale("sv"))
                .addPattern("d. MMMM yyyy", PatternType.CLDR)
                .build()
                .with(ChronoHistory.ofSweden());
        ChronoFormatter<PlainDate> formatterAlt =
            ChronoFormatter.setUp(PlainDate.class, new Locale("sv"))
                .addPattern("d. MMMM yyyy", PatternType.CLDR_DATE)
                .build()
                .with(ChronoHistory.ofSweden());
        assertThat(
            formatter.format(PlainDate.of(1712, 3, 11)),
            is("30. februari 1712"));
        assertThat(
            formatterAlt.format(PlainDate.of(1712, 3, 11)),
            is("30. februari 1712"));
    }

    @Test
    public void parseSweden2() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, new Locale("sv"))
                .addPattern("d. MMMM yyyy", PatternType.CLDR)
                .build()
                .with(ChronoHistory.ofSweden())
                .withDefault(ChronoHistory.ofSweden().era(), HistoricEra.AD);
        assertThat(
            formatter.parse("30. februari 1712"),
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
                .with(history)
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

    @Test
    public void printRussiaByzantine() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, new Locale("en", "RU"))
                .addPattern("d. MMMM GGGG yyyy", PatternType.CLDR)
                .build()
                .with(Leniency.STRICT);
        PlainDate ad1523 = ChronoHistory.PROLEPTIC_JULIAN.convert(HistoricDate.of(HistoricEra.AD, 1523, 8, 31));
        assertThat(
            formatter.format(ad1523),
            is("31. August Anno Mundi 7031"));
        assertThat(
            formatter.format(ad1523.plus(1, CalendarUnit.DAYS)),
            is("1. September Anno Mundi 7032/31"));
        PlainDate ad1699 = ChronoHistory.PROLEPTIC_JULIAN.convert(HistoricDate.of(HistoricEra.AD, 1699, 12, 20));
        assertThat(
            formatter.format(ad1699),
            is("20. December Anno Mundi 7208/07")); // decree of Peter I.
    }

    @Test
    public void parseRussiaByzantine() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, new Locale("en", "RU"))
                .addPattern("d. MMMM GGGG yyyy", PatternType.CLDR)
                .build()
                .with(Leniency.STRICT);
        PlainDate ad1523 = ChronoHistory.PROLEPTIC_JULIAN.convert(HistoricDate.of(HistoricEra.AD, 1523, 8, 31));
        assertThat(
            formatter.parse("31. August Anno Mundi 7031"),
            is(ad1523));
        assertThat(
            formatter.parse("1. September Anno Mundi 7031"),
            is(ad1523.plus(1, CalendarUnit.DAYS)));
        assertThat(
            formatter.parse("1. September Anno Mundi 7032/31"),
            is(ad1523.plus(1, CalendarUnit.DAYS)));
        PlainDate ad1699 = ChronoHistory.PROLEPTIC_JULIAN.convert(HistoricDate.of(HistoricEra.AD, 1699, 12, 20));
        assertThat(
            formatter.parse("20. December Anno Mundi 7208/07"),
            is(ad1699));
        assertThat(
            formatter.parse("20. December Anno Mundi 7207"),
            is(ad1699));
    }

    @Test
    public void printByzantine() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.ENGLISH)
                .addPattern("d. MMMM GGGG yyyy", PatternType.CLDR)
                .build()
                .with(Leniency.STRICT)
                .with(ChronoHistory.PROLEPTIC_BYZANTINE);
        PlainDate ad2016 = ChronoHistory.PROLEPTIC_JULIAN.convert(HistoricDate.of(HistoricEra.AD, 2016, 8, 31));
        assertThat(
            formatter.format(ad2016),
            is("31. August Anno Mundi 7524"));
        assertThat(
            formatter.format(ad2016.plus(1, CalendarUnit.DAYS)),
            is("1. September Anno Mundi 7525/24"));
    }

    @Test
    public void parseByzantine() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.ENGLISH)
                .addPattern("d. MMMM GGGG yyyy", PatternType.CLDR)
                .build()
                .with(Leniency.STRICT)
                .with(ChronoHistory.PROLEPTIC_BYZANTINE);
        PlainDate ad2016 = ChronoHistory.PROLEPTIC_JULIAN.convert(HistoricDate.of(HistoricEra.AD, 2016, 8, 31));
        assertThat(
            formatter.parse("31. August Anno Mundi 7524"),
            is(ad2016));
        assertThat(
            formatter.parse("1. September Anno Mundi 7524"),
            is(ad2016.plus(1, CalendarUnit.DAYS)));
        assertThat(
            formatter.parse("1. September Anno Mundi 7525/24"),
            is(ad2016.plus(1, CalendarUnit.DAYS)));
    }

    @Test
    public void printAUC() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.US)
                .addPattern("MM/dd/y G", PatternType.CLDR)
                .build()
                .with(Leniency.STRICT)
                .with(ChronoHistory.ofFirstGregorianReform().with(EraPreference.abUrbeCondita()));
        PlainDate ad1 = ChronoHistory.PROLEPTIC_JULIAN.convert(HistoricDate.of(HistoricEra.AD, 1, 1, 1));
        assertThat(
            formatter.format(ad1),
            is("01/01/754 a.u.c."));
    }

    @Test
    public void parseAUC() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.US)
                .addPattern("MM/dd/", PatternType.CLDR)
                .addInteger(PlainDate.YEAR, 2, 4) // test for min-width of 2 digits
                .addPattern(" G", PatternType.CLDR)
                .build()
                .with(Leniency.STRICT)
                .with(ChronoHistory.ofFirstGregorianReform().with(EraPreference.abUrbeCondita()));
        PlainDate ad1 = ChronoHistory.PROLEPTIC_JULIAN.convert(HistoricDate.of(HistoricEra.BC, 45, 1, 1));
        assertThat(
            formatter.format(ad1),
            is("01/01/709 a.u.c."));
    }

    @Test
    public void printFrance() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern("d. MMMM GGGG yyyy", PatternType.CLDR, Locale.FRANCE).with(Leniency.STRICT);
        assertThat(
            formatter.format(PlainDate.of(1565, 4, 3)),
            is("24. mars après Jésus-Christ 1564/65")); // before Easter
    }

    @Test
    public void parseFrance() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern("d. MMMM GGGG yyyy", PatternType.CLDR, Locale.FRANCE).with(Leniency.STRICT);
        assertThat(
            formatter.parse("24. mars après Jésus-Christ 1564/65"),
            is(PlainDate.of(1565, 4, 3)));
        assertThat(
            formatter.parse("24. mars après Jésus-Christ 1564/5"), // test for year-of-era-part < 10
            is(PlainDate.of(1565, 4, 3)));
    }

    @Test
    public void printPisa1700() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern("G-yyyy-MM-dd", PatternType.CLDR, new Locale("en", "IT", "PISA")).with(Leniency.STRICT);
        assertThat(
            formatter.format(PlainDate.of(1700, 1, 30)),
            is("AD-1698/1700-01-30"));
    }

    @Test
    public void parsePisa1700() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern("G-yyyy-MM-dd", PatternType.CLDR, new Locale("en", "IT", "PISA")).with(Leniency.STRICT);
        assertThat(
            formatter.parse("AD-1698/1700-01-30"),
            is(PlainDate.of(1700, 1, 30)));
        assertThat(
            formatter.parse("AD-1700-01-30"),
            is(PlainDate.of(1700, 1, 30)));
    }

    @Test
    public void printPisa1713() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern("G-yyyy-MM-dd", PatternType.CLDR, new Locale("en", "IT", "PISA")).with(Leniency.STRICT);
        assertThat(
            formatter.format(PlainDate.of(1713, 1, 30)),
            is("AD-1711/13-01-30"));
    }

    @Test
    public void parsePisa1713() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern("G-yyyy-MM-dd", PatternType.CLDR, new Locale("en", "IT", "PISA")).with(Leniency.STRICT);
        assertThat(
            formatter.parse("AD-1711/13-01-30"),
            is(PlainDate.of(1713, 1, 30)));
        assertThat(
            formatter.parse("AD-1713-01-30"),
            is(PlainDate.of(1713, 1, 30)));
    }

}