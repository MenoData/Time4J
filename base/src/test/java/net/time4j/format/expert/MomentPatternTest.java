package net.time4j.format.expert;

import net.time4j.Moment;
import net.time4j.tz.Timezone;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class MomentPatternTest {

 	@Parameterized.Parameters
        (name= "{index}: "
            + "(pattern={0},locale={1},timezone={2},value={3},text={4})")
 	public static Iterable<Object[]> data() {
 		return Arrays.asList(
            new Object[][] {
                {"uuuu-MM-dd'T'HH:mm:ss.SSSSSSSSSX",
                        "",
                        "Asia/Tokyo",
                        "2012-06-30T23:59:60,123456789Z",
                        "2012-07-01T08:59:60.123456789+09"},
                {"uuuu-MM-dd'T'HH:mm:ss.SSSSSSSSSXXX",
                        "",
                        "Asia/Tokyo",
                        "2012-06-30T23:59:60,123456789Z",
                        "2012-07-01T08:59:60.123456789+09:00"},
                {"uuuu-MM-dd'T'HH:mm:ss.SSSSSSSSSXXX",
                        "",
                        "Europe/Berlin",
                        "2012-06-30T23:59:60,123456789Z",
                        "2012-07-01T01:59:60.123456789+02:00"},
                {"uuuu-MM-dd'T'HH:mm:ss.SSSSSSXXX",
                        "",
                        "Europe/Berlin",
                        "2012-06-30T23:59:60,123456000Z",
                        "2012-07-01T01:59:60.123456+02:00"},
                {"uuuu-MM-dd'T'HH:mm:ss.SSSXXX",
                        "",
                        "Europe/Berlin",
                        "2012-06-30T23:59:60,123000000Z",
                        "2012-07-01T01:59:60.123+02:00"},
                {"uuuu-MM-dd'T'HH:mm:ss.SSSX",
                        "",
                        "Europe/Berlin",
                        "2012-06-30T23:59:60,123000000Z",
                        "2012-07-01T01:59:60.123+02"},
                {"uuuu-MM-dd'T'HH:mm:ss.SSSX",
                        "",
                        "Asia/Kolkata",
                        "2012-06-30T23:59:60,123000000Z",
                        "2012-07-01T05:29:60.123+0530"},
                {"d. MMMM uuuu HH:mm:ss.SSSSSSSSSXXX",
                        "de",
                        "Europe/Berlin",
                        "2012-06-30T23:59:60,123456789Z",
                        "1. Juli 2012 01:59:60.123456789+02:00"},
                {"d. MMMM uuuu HH:mm[:ss]XXX",
                        "de",
                        "Europe/Berlin",
                        "2012-06-30T23:59:60Z",
                        "1. Juli 2012 01:59:60+02:00"},
                {"d. MMMM uuuu HH:mm[:ss.SSSSSSSSS]XXX",
                        "de",
                        "Europe/Berlin",
                        "2012-06-30T23:59:60,123456789Z",
                        "1. Juli 2012 01:59:60.123456789+02:00"},
                {"uuuu-MM-dd'T'HH:mm:ss.SSSXXXXX",
                        "",
                        "Asia/Kolkata",
                        "2012-06-30T23:59:60,123000000Z",
                        "2012-07-01T05:29:60.123+05:30"},
                {"uuuu-MM-dd'T'HH:mm:ss.SSSXXXXX",
                        "",
                        "Europe/Berlin",
                        "2012-06-30T23:59:60,123000000Z",
                        "2012-07-01T01:59:60.123+02:00"},
                {"d. MMMM uuuu HH:mm:ss'['VV']'",
                        "de",
                        "Europe/Berlin",
                        "2012-06-30T23:59:60Z",
                        "1. Juli 2012 01:59:60[Europe/Berlin]"},
                {"d. MMMM uuuu HH:mm'['VV']'",
                        "de",
                        "Europe/Berlin",
                        "2012-05-10T23:59Z",
                        "11. Mai 2012 01:59[Europe/Berlin]"},
                {"d. MMM uuuu HH:mm'['VV']'",
                        "de",
                        "Europe/Berlin",
                        "2012-05-10T23:59Z",
                        "11. Mai 2012 01:59[Europe/Berlin]"},
                {"dd.MM.uuuu HH:mmVV",
                        "de",
                        "Europe/Berlin",
                        "2012-03-10T23:59Z",
                        "11.03.2012 00:59Europe/Berlin"},
                {"MMMM/dd/uuuu HH:mm:ssXXX",
                        "us",
                        "America/Los_Angeles",
                        "2012-06-30T23:59:60Z",
                        "June/30/2012 16:59:60-07:00"},
                {"MMMM/dd/uuuu HH:mm:ss '['VV']'",
                        "us",
                        "America/Los_Angeles",
                        "2012-06-30T23:59:60Z",
                        "June/30/2012 16:59:60 [America/Los_Angeles]"},
                {"MMMM/dd/uuuu HH:mmXXX",
                        "us",
                        "America/Los_Angeles",
                        "2012-02-21T14:30Z",
                        "February/21/2012 06:30-08:00"},
                 {"uuuu-MM-dd'T'HH:mm:ss.SSSZ",
                        "",
                        "Asia/Kolkata",
                        "2012-06-30T23:59:60,123000000Z",
                        "2012-07-01T05:29:60.123+0530"},
                 {"uuuu-MM-dd'T'HH:mm:ss.SSSZ",
                        "",
                        "Europe/Berlin",
                        "2012-06-30T23:59:60,123000000Z",
                        "2012-07-01T01:59:60.123+0200"},
                 {"uuuu-MM-dd'T'HH:mm:ss.SSSZZZZ",
                        "",
                        "Asia/Kolkata",
                        "2012-06-30T23:59:60,123000000Z",
                        "2012-07-01T05:29:60.123GMT+05:30"},
                 {"uuuu-MM-dd'T'HH:mm:ss.SSSZZZZ",
                        "",
                        "Europe/Berlin",
                        "2012-06-30T23:59:60,123000000Z",
                        "2012-07-01T01:59:60.123GMT+02:00"},
                 {"uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ",
                        "",
                        "Asia/Kolkata",
                        "2012-06-30T23:59:60,123000000Z",
                        "2012-07-01T05:29:60.123+05:30"},
                 {"uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ",
                        "",
                        "Europe/Berlin",
                        "2012-06-30T23:59:60,123000000Z",
                        "2012-07-01T01:59:60.123+02:00"},
                 {"uuuu-MM-dd'T'HH:mm:ss.SSSOOOO",
                        "",
                        "Asia/Kolkata",
                        "2012-06-30T23:59:60,123000000Z",
                        "2012-07-01T05:29:60.123GMT+05:30"},
                 {"uuuu-MM-dd'T'HH:mm:ss.SSSOOOO",
                        "",
                        "Europe/Berlin",
                        "2012-06-30T23:59:60,123000000Z",
                        "2012-07-01T01:59:60.123GMT+02:00"},
                 {"uuuu-MM-dd'T'HH:mm:ss.SSSO",
                        "",
                        "Asia/Kolkata",
                        "2012-06-30T23:59:60,123000000Z",
                        "2012-07-01T05:29:60.123GMT+5:30"},
                {"uuuu-MM-dd'T'HH:mm:ss.SSS'['VV']'",
                        "",
                        "Asia/Calcutta",
                        "2012-06-30T23:59:60,123000000Z",
                        "2012-07-01T05:29:60.123[Asia/Calcutta]"}, // alias-Test
                {"uuuu-MM-dd'T'HH:mm:ss.SSSO",
                        "",
                        "Europe/Berlin",
                        "2012-06-30T23:59:60,123000000Z",
                        "2012-07-01T01:59:60.123GMT+2"},
                {"uuuu-MM-dd'T'HH:mmXXX'['VV']'",
                        "de",
                        "Europe/Berlin",
                        "2015-10-25T01:00Z",
                        "2015-10-25T02:00+01:00[Europe/Berlin]"}
           }
        );
    }

    private ChronoFormatter<Moment> formatter;
    private Moment value;
    private String text;
    private String pattern;

    public MomentPatternTest(
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
                .withTimezone(Timezone.of(tzid).getID());
        this.value = Iso8601Format.EXTENDED_DATE_TIME_OFFSET.parse(value);
        this.text = text;
        this.pattern = pattern;
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

    @Test
    public void pattern() {
        assertThat(this.formatter.getPattern(), is(this.pattern));
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