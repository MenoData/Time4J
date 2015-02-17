package net.time4j.i18n;

import net.time4j.Iso8601Format;
import net.time4j.PatternType;
import net.time4j.PlainTimestamp;
import net.time4j.format.ChronoFormatter;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(Parameterized.class)
public class DateTimePatternTest {

 	@Parameterized.Parameters
        (name= "{index}: "
            + "(pattern={0},locale={1},value={2},text={3})")
 	public static Iterable<Object[]> data() {
 		return Arrays.asList(
            new Object[][] {
                {"uuuuMMdd'T'HHmmssSSSSSSSSS",
                        "",
                        "2012-06-30T23:59:59,123456789",
                        "20120630T235959123456789"},
                {"MM/dd/yyyy hh:mm a",
                        "",
                        "2012-06-30T23:45",
                        "06/30/2012 11:45 PM"},
                {"MM/dd/yyyy hh:mm a",
                        "",
                        "2012-06-30T12:45",
                        "06/30/2012 12:45 PM"},
                {"MM/dd/yyyy KK:mm a",
                        "",
                        "2012-06-30T23:45",
                        "06/30/2012 11:45 PM"},
                {"MM/dd/yyyy KK:mm a",
                        "",
                        "2012-06-30T12:45",
                        "06/30/2012 00:45 PM"},
                {"MM/dd/yyyy kk:mm a",
                        "",
                        "2012-06-30T23:45",
                        "06/30/2012 23:45 PM"},
                {"MM/dd/yyyy kk:mm a",
                        "",
                        "2012-06-30T00:45",
                        "06/30/2012 24:45 AM"},
                {"uuuu-MM-dd '('A 'milliseconds of day)'",
                        "",
                        "2012-06-30T23:59:59,123",
                        "2012-06-30 (86399123 milliseconds of day)"},
                {"MM/dd/yyyy hh:mm a (e)",
                        "de",
                        "2014-05-30T23:45",
                        "05/30/2014 11:45 PM (5)"},
                {"MM/dd/yyyy hh:mm a (ee)",
                        "us",
                        "2014-05-30T23:45",
                        "05/30/2014 11:45 PM (06)"},
                {"E, dd.MM.uuuu HH:mm",
                        "de",
                        "2014-05-30T23:45",
                        "Fr., 30.05.2014 23:45"},
                {"EE, dd.MM.uuuu HH:mm",
                        "de",
                        "2014-05-30T23:45",
                        "Fr., 30.05.2014 23:45"},
                {"EEE, dd.MM.uuuu HH:mm",
                        "de",
                        "2014-05-30T23:45",
                        "Fr., 30.05.2014 23:45"},
                {"EEEE, dd.MM.uuuu HH:mm",
                        "de",
                        "2014-05-30T23:45",
                        "Freitag, 30.05.2014 23:45"},
                {"EEEEE, dd.MM.uuuu HH:mm",
                        "de",
                        "2014-05-30T23:45",
                        "F, 30.05.2014 23:45"},
                {"EEEEEE, dd.MM.uuuu HH:mm",
                        "de",
                        "2014-05-30T23:45",
                        "Fr., 30.05.2014 23:45"},
                {"EEE, MM/dd/uuuu hh:mm a",
                        "us",
                        "2014-05-30T23:45",
                        "Fri, 05/30/2014 11:45 PM"},
                {"EEEE, MM/dd/uuuu hh:mm a",
                        "us",
                        "2014-05-30T23:45",
                        "Friday, 05/30/2014 11:45 PM"},
                {"EEEEE, MM/dd/uuuu hh:mm a",
                        "us",
                        "2014-05-30T23:45",
                        "F, 05/30/2014 11:45 PM"},
                {"EEEEEE, MM/dd/uuuu hh:mm a",
                        "us",
                        "2014-05-30T23:45",
                        "Fr, 05/30/2014 11:45 PM"}
           }
        );
    }

    private ChronoFormatter<PlainTimestamp> formatter;
    private PlainTimestamp value;
    private String text;

    public DateTimePatternTest(
        String pattern,
        String locale,
        String value,
        String text
    ) throws ParseException {
        super();

        this.formatter =
            PlainTimestamp.formatter(
                pattern,
                PatternType.CLDR,
                toLocale(locale));
        this.value = Iso8601Format.EXTENDED_DATE_TIME.parse(value);
        this.text = text;
    }

    @Test
    public void print() {
        assertThat(
            this.formatter.format(this.value),
            is(this.text));
    }

    @Test
    public void parse() throws ParseException {
        assertThat(
            this.formatter.parse(this.text),
            is(this.value));
    }

    private static Locale toLocale(String locale) {
        if (locale.equals("en")) {
            return Locale.UK;
        } else if (locale.equals("us")) {
            return Locale.US;
        } else if (locale.equals("in")) {
            return new Locale("en", "IN");
        }
        return new Locale(locale, locale.toUpperCase());
    }

}