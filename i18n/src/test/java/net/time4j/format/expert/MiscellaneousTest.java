package net.time4j.format.expert;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.SI;
import net.time4j.Weekday;
import net.time4j.Weekmodel;
import net.time4j.ZonalDateTime;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoFunction;
import net.time4j.format.Attributes;
import net.time4j.format.DisplayMode;
import net.time4j.format.Leniency;
import net.time4j.format.NumberSystem;
import net.time4j.format.PluralCategory;
import net.time4j.format.TemporalFormatter;
import net.time4j.format.TextElement;
import net.time4j.format.TextWidth;
import net.time4j.history.ChronoHistory;
import net.time4j.history.HistoricEra;
import net.time4j.scale.TimeScale;
import net.time4j.tz.NameStyle;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import static net.time4j.tz.OffsetSign.AHEAD_OF_UTC;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


@RunWith(JUnit4.class)
public class MiscellaneousTest {

    @Test
    public void printArabicIndicMicroOfDay() {
        ChronoFormatter<PlainTime> f =
            ChronoFormatter.setUp(PlainTime.axis(), new Locale("en"))
                .addLongNumber(PlainTime.MICRO_OF_DAY, 11, 11, SignPolicy.SHOW_NEVER)
                .build()
                .with(Attributes.NUMBER_SYSTEM, NumberSystem.ARABIC_INDIC);
        char zeroChar = NumberSystem.ARABIC_INDIC.getDigits().charAt(0);
        StringBuilder zeroes = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            zeroes.append(zeroChar);
        }
        assertThat(
            f.format(PlainTime.midnightAtEndOfDay()),
            is(NumberSystem.ARABIC_INDIC.toNumeral(86400) + zeroes.toString()));
        assertThat(
            f.with(new Locale("ar")).format(PlainTime.midnightAtEndOfDay()),
            is(NumberSystem.ARABIC_INDIC.toNumeral(86400) + zeroes.toString()));
    }

    @Test
    public void localizedStdNumberSystem() {
        ChronoFormatter<PlainTime> f =
            ChronoFormatter.ofTimePattern("HH:mm", PatternType.CLDR, Locale.ENGLISH);
        assertThat(
            f.getAttributes().get(Attributes.NUMBER_SYSTEM),
            is(NumberSystem.ARABIC));
        assertThat(
            f.with(new Locale("ar")).getAttributes().get(Attributes.NUMBER_SYSTEM),
            is(NumberSystem.ARABIC_INDIC));
        assertThat(
            f.with(new Locale("ar", "DZ")).getAttributes().get(Attributes.NUMBER_SYSTEM),
            is(NumberSystem.ARABIC));
        assertThat(
            f.with(new Locale("fa")).getAttributes().get(Attributes.NUMBER_SYSTEM),
            is(NumberSystem.ARABIC_INDIC_EXT));
        assertThat(
            f.with(new Locale("my")).getAttributes().get(Attributes.NUMBER_SYSTEM),
            is(NumberSystem.MYANMAR));
        assertThat(
            f.with(new Locale("ne")).getAttributes().get(Attributes.NUMBER_SYSTEM),
            is(NumberSystem.DEVANAGARI));
    }

    @Test
    public void testTagalog() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern("MMMM", PatternType.CLDR, new Locale("tl"));
        assertThat(
            formatter.format(PlainDate.of(2015, 11, 19)),
            is("Nobyembre"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void printWithGenericPattern() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern("EEEE, yyyy-MM-dd", PatternType.NON_ISO_DATE, Locale.GERMAN);
        formatter.format(PlainDate.of(2015, 7, 17));
    }

    @Test
    public void printLocalDayOfWeekAsText() throws IOException {
        TextElement<?> te =
            TextElement.class.cast(Weekmodel.of(Locale.US).localDayOfWeek());
        Attributes attributes =
            new Attributes.Builder()
            .setLanguage(Locale.GERMANY)
            .build();
        Appendable buffer = new StringBuilder();
        te.print(PlainDate.of(2013, 3, 8), buffer, attributes);
        assertThat(buffer.toString(), is("Freitag"));
    }

    @Test
    public void parseLocalDayOfWeekAsText() throws IOException {
        TextElement<?> te =
            TextElement.class.cast(Weekmodel.of(Locale.US).localDayOfWeek());
        Attributes attributes =
            new Attributes.Builder()
            .setLanguage(Locale.GERMANY)
            .set(Attributes.PARSE_CASE_INSENSITIVE, true)
            .build();
        ParseLog status = new ParseLog();
        Object parseResult = te.parse("FreitaG", status.getPP(), attributes);
        assertThat(parseResult.equals(Weekday.FRIDAY), is(true));
        assertThat(status.getPosition(), is(7));
        assertThat(status.getErrorIndex(), is(-1));
    }

    @Test
    public void changeOfWeekmodelExtensionElement() throws IOException {
        PlainDate wednesday = PlainDate.of(2015, 4, 8);
        ChronoFormatter<PlainDate> f =
            ChronoFormatter.setUp(PlainDate.class, Locale.GERMANY)
                .addPattern("e", PatternType.CLDR).build();
        String de = f.format(wednesday);
        assertThat(de, is("3"));
        f = f.with(Locale.US);
        String us = f.format(wednesday);
        assertThat(us, is("4"));
    }

    @Test
    public void testGenericFullTimeFormats() {
        PlainTime time = PlainTime.of(22, 40);
        for (Locale loc : DateFormatSymbols.getAvailableLocales()) {
            try {
                PlainTime.formatter(DisplayMode.FULL, loc).format(time);
            } catch (RuntimeException re){
                DateFormat df = DateFormat.getTimeInstance(DateFormat.FULL, loc);
                String pattern = SimpleDateFormat.class.cast(df).toPattern();
                fail("locale=" + loc + ", pattern=[" + pattern + "] => " + re.getMessage() + ")");
            }
        }
    }

    @Test
    public void testGenericLongTimeFormats() {
        PlainTime time = PlainTime.of(22, 40);
        for (Locale loc : DateFormatSymbols.getAvailableLocales()) {
            try {
                PlainTime.formatter(DisplayMode.LONG, loc).format(time);
            } catch (RuntimeException re){
                DateFormat df = DateFormat.getTimeInstance(DateFormat.LONG, loc);
                String pattern = SimpleDateFormat.class.cast(df).toPattern();
                fail("locale=" + loc + ", pattern=[" + pattern + "] => " + re.getMessage() + ")");
            }
        }
    }

    @Test
    public void parseTime24Smart() throws ParseException {
        ParseLog plog = new ParseLog();
        assertThat(
            ChronoFormatter.setUp(PlainTime.class, Locale.ENGLISH)
                .addPattern("HH:mm", PatternType.CLDR).build()
                .with(Attributes.LENIENCY, Leniency.SMART)
                .parse("24:00", plog),
            nullValue());
        assertThat(plog.getErrorIndex(), is(5));
        assertThat(
            plog.getErrorMessage().startsWith(
                "Validation failed => Time 24:00 not allowed"),
            is(true));
    }

    @Test
    public void parseTime24Lax() throws ParseException {
        assertThat(
            ChronoFormatter.setUp(PlainTime.class, Locale.ENGLISH)
                .addPattern("HH:mm", PatternType.CLDR).build()
                .with(Attributes.LENIENCY, Leniency.LAX)
                .parse("24:00"),
            is(PlainTime.midnightAtEndOfDay()));
    }

    @Test
    public void parseTime48Lax() throws ParseException {
        assertThat(
            ChronoFormatter.setUp(PlainTime.class, Locale.ENGLISH)
                .addPattern("HH:mm", PatternType.CLDR).build()
                .with(Attributes.LENIENCY, Leniency.LAX)
                .parse("48:00"),
            is(PlainTime.midnightAtEndOfDay()));
    }

    @Test(expected=ParseException.class)
    public void parseTimestampT27Smart() throws ParseException {
        ChronoFormatter.setUp(PlainTimestamp.class, Locale.ENGLISH)
            .addPattern("uuuu-MM-dd HH:mm", PatternType.CLDR).build()
            .with(Attributes.LENIENCY, Leniency.SMART)
            .parse("2014-12-31 27:00");
    }

    @Test
    public void parseTimestampT27Lax() throws ParseException {
        assertThat(
            ChronoFormatter.setUp(PlainTimestamp.class, Locale.ENGLISH)
                .addPattern("uuuu-MM-dd HH:mm", PatternType.CLDR).build()
                .with(Attributes.LENIENCY, Leniency.LAX)
                .parse("2014-12-31 27:00"),
            is(PlainTimestamp.of(2015, 1, 1, 3, 0)));
    }

    @Test(expected=ParseException.class)
    public void parseTimestampMonth13Smart() throws ParseException {
        ChronoFormatter.setUp(PlainTimestamp.class, Locale.ENGLISH)
            .addPattern("uuuu-MM-dd HH:mm", PatternType.CLDR).build()
            .with(Attributes.LENIENCY, Leniency.SMART)
            .parse("2014-13-31 27:00");
    }

    @Test
    public void parseTimestampMonth13Lax() throws ParseException {
        assertThat(
            ChronoFormatter.setUp(PlainTimestamp.class, Locale.ENGLISH)
                .addPattern("uuuu-MM-dd HH:mm", PatternType.CLDR).build()
                .with(Attributes.LENIENCY, Leniency.LAX)
                .parse("2014-13-31 27:00"),
            is(PlainTimestamp.of(2015, 2, 1, 3, 0)));
    }

    @Test
    public void parseTimestampDefaultingEndOfDay() throws ParseException {
        PlainTimestamp tsp =
            ChronoFormatter.setUp(PlainTimestamp.class, Locale.ENGLISH)
                .addPattern("uuuu-MM-dd", PatternType.CLDR)
                .build()
                .withDefault(PlainTime.COMPONENT, PlainTime.midnightAtEndOfDay())
                .parse("2014-08-20");
        assertThat(tsp, is(PlainTimestamp.of(2014, 8, 21, 0, 0)));
    }

    @Test(expected=ParseException.class)
    public void parseNoDigitsFound1() throws ParseException {
        ChronoFormatter.setUp(PlainDate.class, Locale.ROOT)
            .addPattern("yyyy", PatternType.CLDR).build()
            .with(Attributes.LENIENCY, Leniency.LAX)
            .parse("abcd");
    }

    @Test(expected=ParseException.class)
    public void parseNoDigitsFound2() throws ParseException {
        Iso8601Format.BASIC_CALENDAR_DATE.parse("0625");
    }

    @Test
    public void parseRawData() {
        ChronoFormatter<?> cf = Iso8601Format.EXTENDED_DATE_TIME_OFFSET;
        ParseLog plog = new ParseLog();
        String text = "2014-10-21T15:10:00+02:00";
        cf.parse(text, plog);
        ChronoEntity<?> e1 = plog.getRawValues();
        ChronoEntity<?> e2 = cf.parseRaw(text);
        assertThat(e1.equals(e2.with(Moment.axis().element(), null)), is(true));
    }

    @Test
    public void parseRawDataHistoric() {
        ChronoFormatter<?> cf = ChronoFormatter.ofDatePattern("G yyyy, MMM/d", PatternType.CLDR, Locale.ENGLISH);
        ChronoHistory history = ChronoHistory.ofFirstGregorianReform();
        ChronoEntity<?> e = cf.parseRaw("AD 1492, Jan/1");
        assertThat(e.get(history.era()), is(HistoricEra.AD));
        assertThat(e.get(history.yearOfEra()), is(1492));
        assertThat(e.get(history.month()), is(1));
        assertThat(e.get(history.dayOfMonth()), is(1));
        assertThat(e.contains(PlainDate.DAY_OF_MONTH), is(false));
    }

    @Test
    public void printVariableHourDecimalMinute()
        throws ParseException {

        ChronoFormatter<PlainTime> formatter =
            ChronoFormatter
                .setUp(PlainTime.class, Locale.ROOT)
                .addInteger(PlainTime.ISO_HOUR, 1, 2)
                .addFixedDecimal(PlainTime.DECIMAL_MINUTE, 3, 1)
                .build();
        assertThat(
            formatter.format(PlainTime.of(17, 8, 30)),
            is("1708,5"));
        assertThat(
            formatter.format(PlainTime.of(7, 8, 30)),
            is("708,5"));
    }

    @Test(expected=NullPointerException.class)
    public void momentFormatterWithoutTimezone1() {
        Moment.formatter(DisplayMode.FULL, Locale.FRENCH, null);
    }

    @Test(expected=NullPointerException.class)
    public void momentFormatterWithoutTimezone2() {
        Moment.formatter("HH:mm:ss", PatternType.CLDR, Locale.US, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void momentFormatterWithoutTimezoneWhenPrinting() {
        ChronoFormatter<Moment> formatter =
            ChronoFormatter.setUp(Moment.class, Locale.FRENCH)
                .addPattern("HH:mm:ss", PatternType.CLDR)
                .build();
        formatter.format(Moment.UNIX_EPOCH);
    }

    @Test
    public void momentFormatterRFC1123() {
        assertThat(
            Moment.formatterRFC1123().format(Moment.UNIX_EPOCH),
            is("Thu, 1 Jan 1970 00:00:00 GMT"));
    }

    @Test
    public void momentFormatterPrintingDecimalMinute() {
        ChronoFormatter<Moment> formatter =
            ChronoFormatter.setUp(Moment.class, Locale.US)
                .addPattern("uuuu-MM-dd HH:", PatternType.CLDR)
                .addFixedDecimal(PlainTime.DECIMAL_MINUTE, 3, 1)
                .build()
                .withTimezone(ZonalOffset.UTC);
        assertThat(
            formatter.format(Moment.UNIX_EPOCH),
            is("1970-01-01 00:00.0"));
    }

    @Test
    public void momentFormatterParsingDecimalMinute() throws ParseException {
        ChronoFormatter<Moment> formatter =
            ChronoFormatter.setUp(Moment.class, Locale.US)
                .addPattern("uuuu-MM-dd HH:", PatternType.CLDR)
                .addFixedDecimal(PlainTime.DECIMAL_MINUTE, 3, 1)
                .build()
                .withTimezone(ZonalOffset.UTC);
        assertThat(
            formatter.parse("1970-01-01 00:00.0"),
            is(Moment.UNIX_EPOCH));
    }

    @Test
    public void parseWallTimeWithInvalidHour() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("24:00:00"),
            is(PlainTime.midnightAtEndOfDay()));
        ParseLog plog = new ParseLog();
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("24:15:34", plog),
            nullValue());
        assertThat(plog.getErrorIndex(), is(8));
        assertThat(
            plog.getErrorMessage().startsWith(
                "Validation failed => Time component out of range."),
            is(true));
    }

    @Test
    public void parseWallTimeWithInvalidSecond() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("23:59:59"),
            is(PlainTime.of(23, 59, 59)));
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME
                .with(Attributes.LENIENCY, Leniency.LAX).parse("23:59:60"),
            is(PlainTime.of(24)));
        ParseLog plog = new ParseLog();
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("23:59:60", plog),
            nullValue());
        assertThat(plog.getErrorIndex(), is(8));
        assertThat(
            plog.getErrorMessage().startsWith(
                "Validation failed => Time component out of range."),
            is(true));
    }

    @Test
    public void parseIsoCalendarDateInvalidMonth() throws ParseException {
        ParseLog plog = new ParseLog();
        assertThat(
            Iso8601Format.EXTENDED_CALENDAR_DATE.parse("2015-13-10", plog),
            nullValue());
        assertThat(plog.getErrorIndex(), is(10));
        assertThat(
            plog.getErrorMessage().startsWith(
                "Validation failed => MONTH_OF_YEAR out of range: 13"),
            is(true));
    }

    @Test
    public void parseIsoCalendarDateInvalidDayOfMonth() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_CALENDAR_DATE.parse("2012-02-29"),
            is(PlainDate.of(2012, 2, 29)));
        ParseLog plog = new ParseLog();
        assertThat(
            Iso8601Format.EXTENDED_CALENDAR_DATE.parse("2015-02-29", plog),
            nullValue());
        assertThat(plog.getErrorIndex(), is(10));
        assertThat(
            plog.getErrorMessage().startsWith(
                "Validation failed => DAY_OF_MONTH out of range: 29"),
            is(true));
    }

    @Test
    public void parseIsoOrdinalDateInvalidDayOfYear() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_ORDINAL_DATE.parse("2012-366"),
            is(PlainDate.of(2012, 12, 31)));
        ParseLog plog = new ParseLog();
        assertThat(
            Iso8601Format.EXTENDED_ORDINAL_DATE.parse("2015-366", plog),
            nullValue());
        assertThat(plog.getErrorIndex(), is(8));
        assertThat(
            plog.getErrorMessage().startsWith(
                "Validation failed => DAY_OF_YEAR out of range: 366"),
            is(true));
    }

    @Test
    public void parseIsoWeekdateInvalidWeekOfYear() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_WEEK_DATE.parse("2015-W53-1"),
            is(PlainDate.of(2015, 12, 28)));
        ParseLog plog = new ParseLog();
        assertThat(
            Iso8601Format.EXTENDED_WEEK_DATE.parse("2016-W53-1", plog),
            nullValue());
        assertThat(plog.getErrorIndex(), is(10));
        assertThat(
            plog.getErrorMessage().startsWith(
                "Validation failed => WEEK_OF_YEAR (ISO) out of range: 53"),
                is(true));
    }

    @Test
    public void parseQuarterDateQ1Valid() throws ParseException {
        assertThat(
            getQuarterDateFormatter().parse("2015-Q1-90"),
            is(PlainDate.of(2015, 3, 31)));
    }

    @Test(expected=ParseException.class)
    public void parseQuarterDateQ1Invalid() throws ParseException {
        getQuarterDateFormatter().parse("2015-Q1-91");
    }

    @Test
    public void parseQuarterDateQ1LeapValid() throws ParseException {
        assertThat(
            getQuarterDateFormatter().parse("2012-Q1-91"),
            is(PlainDate.of(2012, 3, 31)));
    }

    @Test(expected=ParseException.class)
    public void parseQuarterDateQ1LeapInvalid() throws ParseException {
        getQuarterDateFormatter().parse("2012-Q1-92");
    }

    @Test
    public void parseQuarterDateQ2Valid() throws ParseException {
        assertThat(
            getQuarterDateFormatter().parse("2015-Q2-91"),
            is(PlainDate.of(2015, 6, 30)));
    }

    @Test(expected=ParseException.class)
    public void parseQuarterDateQ2Invalid() throws ParseException {
        getQuarterDateFormatter().parse("2015-Q2-92");
    }

    @Test
    public void parseQuarterDateQ3Valid() throws ParseException {
        assertThat(
            getQuarterDateFormatter().parse("2015-Q3-92"),
            is(PlainDate.of(2015, 9, 30)));
    }

    @Test(expected=ParseException.class)
    public void parseQuarterDateQ3Invalid() throws ParseException {
        getQuarterDateFormatter().parse("2015-Q3-93");
    }

    @Test
    public void parseQuarterDateQ4Valid() throws ParseException {
        assertThat(
            getQuarterDateFormatter().parse("2015-Q4-92"),
            is(PlainDate.of(2015, 12, 31)));
    }

    @Test
    public void parseQuarterDateQ4Invalid() throws ParseException {
        ParseLog plog = new ParseLog();
        assertThat(
            getQuarterDateFormatter().parse("2015-Q4-93", plog),
            nullValue());
        assertThat(plog.getErrorIndex(), is(10));
        assertThat(
            plog.getErrorMessage().startsWith(
                "Validation failed => DAY_OF_QUARTER out of range: 93"),
            is(true));
    }

    @Test
    public void parseQuarterDateBefore1() throws ParseException {
        ParseLog plog = new ParseLog();
        assertThat(
            getQuarterDateFormatter().parse("2015-Q1-00", plog),
            nullValue());
        assertThat(plog.getErrorIndex(), is(10));
        assertThat(
            plog.getErrorMessage().startsWith(
                "Validation failed => DAY_OF_QUARTER out of range: 0"),
            is(true));
    }

    @Test
    public void parseTimeWithSecondOfDay() throws ParseException {
        ChronoFormatter<PlainTime> formatter =
            ChronoFormatter.setUp(PlainTime.class, Locale.US)
                .addFixedInteger(PlainTime.SECOND_OF_DAY, 5)
                .addLiteral('-')
                .addFixedInteger(PlainTime.MILLI_OF_SECOND, 3)
                .build();
        assertThat(
            formatter.parse("86399-123"),
            is(PlainTime.of(23, 59, 59, 123000000)));
        assertThat(
            formatter.parse("86400-000"),
            is(PlainTime.of(24)));
    }

    @Test
    public void parseTimeWithNanoOfDay() throws ParseException {
        ChronoFormatter<PlainTime> formatter =
            ChronoFormatter.setUp(PlainTime.class, Locale.US)
                .addLongNumber(
                    PlainTime.NANO_OF_DAY, 14, 14, SignPolicy.SHOW_NEVER)
                .build();
        long nanoOfDay = 86400 * 1000000000L;
        ParseLog plog = new ParseLog();
        assertThat(
            formatter.parse(String.valueOf(nanoOfDay - 1)),
            is(PlainTime.of(23, 59, 59, 999999999)));
        assertThat(
            formatter.parse(String.valueOf(nanoOfDay)),
            is(PlainTime.of(24)));
        assertThat(
            formatter.parse(String.valueOf(nanoOfDay + 1), plog),
            nullValue());
        assertThat(plog.getErrorIndex(), is(14));
        assertThat(
            plog.getErrorMessage().startsWith(
                "Validation failed => NANO_OF_DAY out of range:"),
            is(true));
    }

    @Test
    public void printIndividualFormat1() throws ParseException {
        ChronoFormatter<Moment> formatter =
            ChronoFormatter.setUp(Moment.class, Locale.US)
                .startSection(Attributes.PAD_CHAR, '#')
                .padNext(2)
                .addPattern("M/dd/yyyy hh:mm a ", PatternType.CLDR)
                .endSection()
                .addLongTimezoneName(Timezone.getPreferredIDs(Locale.US, false, "DEFAULT"))
                .addLiteral('/')
                .addShortTimezoneName(Timezone.getPreferredIDs(Locale.US, false, "DEFAULT"))
                .addLiteral('/')
                .addTimezoneOffset()
                .build()
                .withStdTimezone();
        PlainTimestamp tsp = PlainDate.of(2015, 2).atStartOfDay();
        Moment moment = tsp.in(Timezone.of("America/New_York"));
        ZonalOffset offset = Timezone.ofSystem().getOffset(moment);
        String displayedOffset = (offset.equals(ZonalOffset.UTC) ? "Z" : offset.toString());
        tsp = moment.toZonalTimestamp(offset);
        String s = PlainTimestamp.formatter("MM/dd/yyyy hh:mm a ", PatternType.CLDR, Locale.US).format(tsp);
        assertThat(
            formatter.format(moment),
            is("#" + s.substring(1)
                    + Timezone.ofSystem().getDisplayName(NameStyle.LONG_STANDARD_TIME, Locale.US)
                    + "/" + Timezone.ofSystem().getDisplayName(NameStyle.SHORT_STANDARD_TIME, Locale.US)
                    + "/" + displayedOffset
            )
        );
    }

    @Test
    public void printIndividualFormat2() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.US)
                .startSection(Attributes.PAD_CHAR, '#')
                .addNumerical(PlainDate.MONTH_OF_YEAR, 1, 2)
                .padPrevious(2)
                .endSection()
                .addLiteral(' ')
                .addText(PlainDate.DAY_OF_WEEK, Collections.singletonMap(Weekday.THURSDAY, "Donnerstag"))
                .addLiteral(" (attribute-value=")
                .addLiteral(Attributes.ZERO_DIGIT)
                .addLiteral(") ")
                .addOrdinal(PlainDate.DAY_OF_MONTH, Collections.singletonMap(PluralCategory.OTHER, "st"))
                .build()
                .with(Attributes.ZERO_DIGIT, '8'); // just done here for better test coverage
        assertThat(
            formatter.format(PlainDate.of(2015, 1)),
            is("9# Donnerstag (attribute-value=8) 9st") // "9" because of value of Attributes.ZERO_DIGIT
        );
    }

    @Test
    public void toJavaTextFormat() throws ParseException {
        ChronoFormatter<Moment> formatter =
            ChronoFormatter.setUp(Moment.class, Locale.US)
                .addPattern("M/dd/yyyy hh:mm a ", PatternType.CLDR)
                .addTimezoneOffset()
                .build()
                .withTimezone("America/New_York");
        PlainTimestamp tsp = PlainDate.of(2015, 1, 2).atStartOfDay();
        Moment moment = tsp.in(Timezone.of("America/New_York"));
        FieldPosition fpos = new FieldPosition(DateFormat.Field.TIME_ZONE); //.TIMEZONE_FIELD);
        assertThat(
            formatter.toFormat().format(moment, new StringBuffer(), fpos).toString(),
            is("1/02/2015 12:00 am -05:00")
        );
        assertThat(fpos.getBeginIndex(), is(19));
        assertThat(fpos.getEndIndex(), is(25));
    }

    @Test
    public void printZDT() {
        Moment moment = Moment.of(1278028824, TimeScale.UTC);
        Timezone tz = Timezone.of("Asia/Tokyo");
        ChronoFormatter<Moment> formatter =
            Iso8601Format.EXTENDED_DATE_TIME_OFFSET;
        assertThat(
            moment.inZonalView(tz.getID()).print(formatter),
            is("2012-07-01T08:59:60+09:00"));
    }

    @Test
    public void parseZDT() throws ParseException {
        Moment moment = Moment.of(1278028824, TimeScale.UTC);
        ChronoFormatter<Moment> formatter =
            Iso8601Format.EXTENDED_DATE_TIME_OFFSET;
        assertThat(
            ZonalDateTime.parse("2012-07-01T08:59:60+09:00", formatter),
            is(moment.inZonalView(ZonalOffset.ofHours(AHEAD_OF_UTC, 9))));
    }

    @Test(expected = ParseException.class)
    public void parseAmbivalentOffset() throws ParseException {
        ChronoFormatter<Moment> formatter =
            ChronoFormatter.ofMomentPattern(
                "uuuu-MM-dd'T'HH:mm:ssXXX'['VV']'",
                PatternType.CLDR,
                Locale.ROOT,
                ZonalOffset.UTC
            ).with(Leniency.STRICT);
        formatter.parse("2012-07-01T08:59:60+01:00[Asia/Tokyo]");
    }

    @Test(expected = ParseException.class)
    public void parseOffsetWithoutSignInSmartMode() throws ParseException {
        ChronoFormatter<Moment> formatter =
            ChronoFormatter.ofMomentPattern(
                "uuuu-MM-dd'T'HH:mm:ss XXX",
                PatternType.CLDR,
                Locale.ROOT,
                ZonalOffset.UTC
            ).with(Leniency.SMART);
        formatter.parse("2012-07-01T08:59:60 9:00");
    }

    @Test
    public void parseOffsetWithoutSignInLaxMode() throws ParseException {
        ChronoFormatter<Moment> formatter =
            ChronoFormatter.ofMomentPattern(
                "uuuu-MM-dd'T'HH:mm:ss XXX",
                PatternType.CLDR,
                Locale.ROOT,
                ZonalOffset.UTC
            ).with(Leniency.LAX);
        assertThat(
            formatter.parse("2012-07-01T08:59:60 9:00"),
            is(PlainTimestamp.of(2012, 7, 1, 0, 0).atUTC().minus(1, SI.SECONDS)));
    }

    @Test
    public void formatPlainTimestampWithOffset() {
        ChronoFormatter<PlainTimestamp> formatter =
            ChronoFormatter.setUp(PlainTimestamp.class, Locale.ROOT)
                .addPattern("uuuu-MM-dd'T'HH:mmXXX", PatternType.CLDR).build();
        assertThat(
            formatter.withTimezone("UTC+2").format(PlainTimestamp.of(2015, 3, 29, 2, 30)),
            is("2015-03-29T02:30+02:00")
        );
    }

    @Test(expected=IllegalArgumentException.class)
    public void formatPlainTimestampWithEuropeBerlin1() {
        ChronoFormatter<PlainTimestamp> formatter =
            ChronoFormatter.setUp(PlainTimestamp.class, Locale.ROOT)
                .addPattern("uuuu-MM-dd'T'HH:mm[VV]", PatternType.CLDR).build();
        formatter.withTimezone("Europe/Berlin").format(PlainTimestamp.of(2015, 3, 29, 2, 30));
    }

    @Test(expected=IllegalArgumentException.class)
    public void formatPlainTimestampWithEuropeBerlin2() {
        ChronoFormatter<PlainTimestamp> formatter =
            ChronoFormatter.setUp(PlainTimestamp.class, Locale.ROOT)
                .addPattern("uuuu-MM-dd'T'HH:mmXXX", PatternType.CLDR).build();
        formatter.withTimezone("Europe/Berlin").format(PlainTimestamp.of(2015, 3, 29, 2, 30));
    }

    @Test(expected=IllegalArgumentException.class)
    public void formatPlainTimestampWithEuropeBerlin3() {
        ChronoFormatter<PlainTimestamp> formatter =
            ChronoFormatter.setUp(PlainTimestamp.class, Locale.ROOT)
                .addPattern("uuuu-MM-dd'T'HH:mm[z]", PatternType.CLDR).build();
        formatter.withTimezone("Europe/Berlin").format(PlainTimestamp.of(2015, 3, 29, 2, 30));
    }

    @Test
    public void iteratorOverParsedValues()
        throws IOException, ClassNotFoundException {

        ParseLog plog = new ParseLog();
        ChronoFormatter<PlainTime> formatter = ChronoFormatter.ofTimePattern("h:mm B", PatternType.CLDR, Locale.US);
        PlainTime time = formatter.parse("9:45 in the evening", plog);
        assertThat(time, is(PlainTime.of(21, 45)));
        Set<ChronoElement<?>> elements = plog.getRawValues().getRegisteredElements();
        assertThat(elements.size(), is(4));
        for (ChronoElement<?> element : elements) {
            String n = element.name();
            assertThat(
                n.equals("CLOCK_HOUR_OF_AMPM")
                    || n.equals("MINUTE_OF_HOUR")
                    || n.equals("APPROXIMATE_DAY_PERIOD")
                    || n.equals("AM_PM_OF_DAY"),
                is(true));
        }
    }

    @Test
    public void parseZDTWithException() throws ParseException {
        Timezone tz = Timezone.of("Asia/Tokyo");
        TemporalFormatter<Moment> formatter =
            Moment.formatter("yyyy-MM-dd HH:mmZ", PatternType.CLDR, Locale.ROOT, tz.getID());
        try {
            ZonalDateTime zdt = ZonalDateTime.parse("2012-07-01T09:00+0900", formatter);
            fail("Parsed successfully to: " + zdt + ", but expected exception.");
        } catch (ParseException pe) {
            assertThat(pe.getMessage(), is("Cannot parse: \"2012-07-01T09:00+0900\" (expected: [ ], found: [T])"));
        }
    }

    @Test
    public void embeddedFormatWithHistory() throws ParseException {
        ChronoFormatter<PlainDate> embedded =
            ChronoFormatter.ofDatePattern("d. MMMM yyyy", PatternType.CLDR, Locale.GERMANY);
        ChronoFormatter<PlainDate> f =
            ChronoFormatter.setUp(PlainDate.axis(), Locale.ROOT)
                .addCustomized(PlainDate.COMPONENT, embedded)
                .build()
                .with(ChronoHistory.ofFirstGregorianReform())
                .with(Locale.ENGLISH)
                .withDefault(ChronoHistory.ofFirstGregorianReform().era(), HistoricEra.AD);
        assertThat(f.format(PlainDate.of(1582, 10, 14)), is("4. October 1582"));
        assertThat(f.parse("4. October 1582"), is(PlainDate.of(1582, 10, 14)));
    }

    @Test
    public void embeddedWithDefaults() throws ParseException {
        ChronoFormatter<PlainDate> embedded =
            ChronoFormatter.ofDatePattern("[uuuu-]MM-dd", PatternType.CLDR, Locale.ROOT);
        ChronoFormatter<PlainTimestamp> f =
            ChronoFormatter.setUp(PlainTimestamp.axis(), Locale.ROOT)
                .addCustomized(
                    PlainDate.COMPONENT,
                    new ChronoPrinter<PlainDate>() {
                        @Override
                        public <R> R print(
                            PlainDate formattable,
                            Appendable buffer,
                            AttributeQuery attributes,
                            ChronoFunction<ChronoDisplay, R> query
                        ) throws IOException {
                            return null;
                        }
                    },
                    embedded)
                .build()
                .withDefault(PlainDate.YEAR, 2016)
                .withDefault(PlainTime.ISO_HOUR, 0);
        PlainTimestamp tsp = f.parse("02-29");
        assertThat(tsp, is(PlainTimestamp.of(2016, 2, 29, 0, 0)));
    }

    private static ChronoFormatter<PlainDate> getQuarterDateFormatter() {
        return ChronoFormatter.setUp(PlainDate.class, Locale.US)
            .addFixedInteger(PlainDate.YEAR, 4)
            .addLiteral('-')
            .addText(PlainDate.QUARTER_OF_YEAR)
            .addLiteral('-')
            .addFixedInteger(PlainDate.DAY_OF_QUARTER, 2)
            .build()
            .with(Attributes.TEXT_WIDTH, TextWidth.ABBREVIATED);
    }

}
