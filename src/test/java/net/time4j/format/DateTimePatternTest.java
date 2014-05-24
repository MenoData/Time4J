package net.time4j.format;

import net.time4j.Iso8601Format;
import net.time4j.PatternType;
import net.time4j.PlainTimestamp;

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