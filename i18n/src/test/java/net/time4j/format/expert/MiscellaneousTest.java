package net.time4j.format.expert;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.Weekday;
import net.time4j.Weekmodel;
import net.time4j.engine.ChronoEntity;
import net.time4j.format.Attributes;
import net.time4j.format.DisplayMode;
import net.time4j.format.Leniency;
import net.time4j.format.SignPolicy;
import net.time4j.format.TextElement;
import net.time4j.format.TextWidth;
import net.time4j.tz.ZonalOffset;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


@RunWith(JUnit4.class)
public class MiscellaneousTest {

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
        e1.with(PlainTimestamp.axis().element(), null);
        ChronoEntity<?> e2 = cf.parseRaw(text);
        assertThat(e1.equals(e2), is(true));
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
    public void roundTripOfParsedValues()
        throws IOException, ClassNotFoundException {

        ParseLog plog = new ParseLog();
        Iso8601Format.EXTENDED_CALENDAR_DATE.parse("2012-02-29", plog);
        Object obj = plog.getRawValues();
        roundtrip(obj);
    }

    private static int roundtrip(Object... obj)
        throws IOException, ClassNotFoundException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        for (int i = 0; i < obj.length; i++) {
            oos.writeObject(obj[i]);
        }
        byte[] data = baos.toByteArray();
        oos.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        for (int i = 0; i < obj.length; i++) {
            assertThat(ois.readObject(), is(obj[i]));
        }
        ois.close();
        return data.length;
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
