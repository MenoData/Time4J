package net.time4j.tz.olson;

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
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class ZoneNameParsingTest {

 	@Parameterized.Parameters
        (name= "{index}: "
            + "(pattern={0},locale={1},timezone={2},value={3},text={4})")
 	public static Iterable<Object[]> data() {
 		return Arrays.asList(
            new Object[][] {
                {"d. MMMM uuuu HH:mm z",
                    "de",
                    "Europe/Berlin",
                    "2017-10-29T00:30Z",
                    "29. Oktober 2017 02:30 MESZ"},
                {"d. MMMM uuuu HH:mm z",
                    "de",
                    "Europe/Berlin",
                    "2017-10-29T01:30Z",
                    "29. Oktober 2017 02:30 MEZ"},
                {"MMMM/dd/uuuu HH:mm:ss z",
                        "us",
                        "America/Los_Angeles",
                        "2012-06-30T23:59:60Z",
                        "June/30/2012 16:59:60 PDT"},
                {"MMMM/dd/uuuu HH:mm:ss zzzz",
                        "us",
                        "America/Los_Angeles",
                        "2012-06-30T23:59:60Z",
                        "June/30/2012 16:59:60 Pacific Daylight Time"},
                {"MMMM/dd/uuuu HH:mm:ss v",
                        "us",
                        "America/Los_Angeles",
                        "2012-06-30T23:59:60Z",
                        "June/30/2012 16:59:60 PT"},
                {"MMMM/dd/uuuu HH:mm:ss vvvv",
                        "us",
                        "America/Los_Angeles",
                        "2012-06-30T23:59:60Z",
                        "June/30/2012 16:59:60 Pacific Time"},
                {"d. MMM uuuu HH:mm z",
                        "de",
                        "Europe/Berlin",
                        "2012-03-31T23:59Z",
                        "1. Apr. 2012 01:59 MESZ"},
                {"d. MMM uuuu HH:mm zzzz",
                        "de",
                        "Europe/Berlin",
                        "2012-03-31T23:59Z",
                        "1. Apr. 2012 01:59 Mitteleuropäische Sommerzeit"},
                {"d. MMMM uuuu HH:mm z",
                        "de",
                        "Europe/Berlin",
                        "2012-03-01T23:59Z",
                        "2. März 2012 00:59 MEZ"},
                {"d. MMMM uuuu HH:mm zzzz",
                        "de",
                        "Europe/Berlin",
                        "2012-03-01T23:59Z",
                        "2. März 2012 00:59 Mitteleuropäische Normalzeit"},
                {"d. MMM uuuu HH:mm z",
                        "fr",
                        "Europe/Paris",
                        "2012-03-31T23:59Z",
                        "1. avr. 2012 01:59 CEST"},
                {"d. MMM uuuu HH:mm zzzz",
                        "fr",
                        "Europe/Paris",
                        "2012-03-31T23:59Z",
                        "1. avr. 2012 01:59 heure d’été d’Europe centrale"},
                {"d. MMMM uuuu HH:mm z",
                        "fr",
                        "Europe/Paris",
                        "2012-03-01T23:59Z",
                        "2. mars 2012 00:59 CET"},
                {"d. MMMM uuuu HH:mm zzzz",
                        "fr",
                        "Europe/Paris",
                        "2012-03-01T23:59Z",
                        "2. mars 2012 00:59 heure normale d’Europe centrale"},
                {"MMMM/dd/uuuu hh:mm a z",
                        "us",
                        "America/Los_Angeles",
                        "2012-02-21T14:30Z",
                        "February/21/2012 06:30 am PST"},
                {"MMMM/dd/uuuu hh:mm a zzzz",
                        "us",
                        "America/Los_Angeles",
                        "2012-02-21T14:30Z",
                        "February/21/2012 06:30 am Pacific Standard Time"},
                {"d. MMMM uuuu HH:mm z",
                        "en",
                        "Europe/London",
                        "2012-03-01T23:59Z",
                        "1. March 2012 23:59 GMT"},
                {"d. MMMM uuuu HH:mm z",
                        "en",
                        "Europe/London",
                        "2012-03-31T23:59Z",
                        "1. April 2012 00:59 BST"},
                {"d. MMMM uuuu HH:mm zzzz",
                        "en",
                        "Europe/London",
                        "2012-03-01T23:59Z",
                        "1. March 2012 23:59 Greenwich Mean Time"},
                {"d. MMMM uuuu HH:mm zzzz",
                        "en",
                        "Europe/London",
                        "2012-03-31T23:59Z",
                        "1. April 2012 00:59 British Summer Time"},
                {"uuuu-MM-dd'T'HH:mm:ss.SSS z",
                        "in",
                        "Asia/Kolkata",
                        "2012-06-30T23:59:60,123000000Z",
                        "2012-07-01T05:29:60.123 IST"},
                {"uuuu-MM-dd'T'HH:mm:ss.SSS zzzz",
                        "in",
                        "Asia/Kolkata",
                        "2012-06-30T23:59:60,123000000Z",
                        "2012-07-01T05:29:60.123 India Standard Time"}
            }
        );
    }

    private ChronoFormatter<Moment> formatter;
    private Moment value;
    private String text;

    public ZoneNameParsingTest(
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
        if (locale.isEmpty()) {
            return Locale.ROOT;
        } else if (locale.equals("en")) {
            return Locale.UK;
        } else if (locale.equals("us")) {
            return Locale.US;
        } else if (locale.equals("in")) {
            return new Locale("en", "IN");
        }
        return new Locale(locale, locale.toUpperCase());
    }

}