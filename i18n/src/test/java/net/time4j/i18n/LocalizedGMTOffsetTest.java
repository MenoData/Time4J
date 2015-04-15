package net.time4j.i18n;

import net.time4j.Moment;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.Iso8601Format;
import net.time4j.format.expert.PatternType;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(Parameterized.class)
public class LocalizedGMTOffsetTest {

 	@Parameterized.Parameters
        (name= "{index}: "
            + "(pattern={0},locale={1},timezone={2},value={3},text={4})")
 	public static Iterable<Object[]> data() {
 		return Arrays.asList(
            new Object[][] {
                 {"uuuu-MM-dd'T'HH:mm:ss.SSSOOOO",
                        "ar-DZ",
                        "Asia/Kolkata",
                        "2012-06-30T23:59:60,123000000Z",
                        "2012-07-01T05:29:60.123جرينتش\u200E+05:30"}
           }
        );
    }

    private ChronoFormatter<Moment> formatter;
    private Moment value;
    private String text;

    public LocalizedGMTOffsetTest(
        String pattern,
        String locale,
        String tzid,
        String value,
        String text
    ) throws ParseException {
        super();

        this.formatter =
            ChronoFormatter.setUp(Moment.class, toLocale(locale))
                .addPattern(pattern, PatternType.CLDR).build()
                .withTimezone(tzid);
        this.value = Iso8601Format.EXTENDED_DATE_TIME_OFFSET.parse(value);
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
        } else if (locale.equals("ar-DZ")) {
            return new Locale("ar", "DZ");
        }
        return new Locale(locale, locale.toUpperCase());
    }

}