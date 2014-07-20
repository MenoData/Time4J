package net.time4j.format;

import net.time4j.PatternType;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.Weekday;
import net.time4j.Weekmodel;

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

}