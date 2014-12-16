package net.time4j.format;

import net.time4j.Iso8601Format;
import net.time4j.PatternType;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.Weekday;
import net.time4j.Weekmodel;
import net.time4j.engine.ChronoEntity;

import java.io.IOException;
import java.text.ParseException;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class MiscellaneousTest {

    @Test
    public void printLocalDayOfWeekAsText() throws IOException {
        TextElement<?> te =
            TextElement.class.cast(Weekmodel.of(Locale.US).localDayOfWeek());
        Attributes attributes =
            new Attributes.Builder()
            .setLocale(Locale.GERMANY)
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
            .setLocale(Locale.GERMANY)
            .set(Attributes.PARSE_CASE_INSENSITIVE, true)
            .build();
        ParseLog status = new ParseLog();
        Object parseResult = te.parse("FreitaG", status, attributes);
        assertThat(parseResult.equals(Weekday.FRIDAY), is(true));
        assertThat(status.getPosition(), is(7));
        assertThat(status.getErrorIndex(), is(-1));
    }

    @Test(expected=ParseException.class)
    public void parseTime24Smart() throws ParseException {
        PlainTime.formatter("HH:mm", PatternType.CLDR, Locale.ENGLISH)
            .with(Attributes.LENIENCY, Leniency.SMART)
            .parse("24:00");
    }

    @Test
    public void parseTime24Lax() throws ParseException {
        assertThat(
            PlainTime.formatter("HH:mm", PatternType.CLDR, Locale.ENGLISH)
            .with(Attributes.LENIENCY, Leniency.LAX)
            .parse("24:00"),
            is(PlainTime.midnightAtStartOfDay()));
    }

    @Test(expected=ParseException.class)
    public void parseTimestampT27Smart() throws ParseException {
        PlainTimestamp.formatter(
                "uuuu-MM-dd HH:mm", PatternType.CLDR, Locale.ENGLISH)
            .with(Attributes.LENIENCY, Leniency.SMART)
            .parse("2014-12-31 27:00");
    }

    @Test
    public void parseTimestampT27Lax() throws ParseException {
        assertThat(
            PlainTimestamp.formatter(
                "uuuu-MM-dd HH:mm", PatternType.CLDR, Locale.ENGLISH)
            .with(Attributes.LENIENCY, Leniency.LAX)
            .parse("2014-12-31 27:00"),
            is(PlainTimestamp.of(2015, 1, 1, 3, 0)));
    }

    @Test(expected=ParseException.class)
    public void parseTimestampMonth13Smart() throws ParseException {
        PlainTimestamp.formatter(
                "uuuu-MM-dd HH:mm", PatternType.CLDR, Locale.ENGLISH)
            .with(Attributes.LENIENCY, Leniency.SMART)
            .parse("2014-13-31 27:00");
    }

    @Test
    public void parseTimestampMonth13Lax() throws ParseException {
        assertThat(
            PlainTimestamp.formatter(
                "uuuu-MM-dd HH:mm", PatternType.CLDR, Locale.ENGLISH)
            .with(Attributes.LENIENCY, Leniency.LAX)
            .parse("2014-13-31 27:00"),
            is(PlainTimestamp.of(2015, 2, 1, 3, 0)));
    }

    @Test(expected=ParseException.class)
    public void parseNoDigitsFound1() throws ParseException {
        PlainDate.localFormatter("yyyy", PatternType.CLDR)
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

}
