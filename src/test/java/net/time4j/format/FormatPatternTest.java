package net.time4j.format;

import net.time4j.Iso8601Format;
import net.time4j.Moment;
import net.time4j.PatternType;
import net.time4j.tz.TZID;
import net.time4j.tz.ZonalOffset;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(Parameterized.class)
public class FormatPatternTest {

 	@Parameterized.Parameters
        (name= "{index}: "
            + "(pattern={0},locale={1},timezone={2},value={3},text={4})")
 	public static Iterable<Object[]> data() {
 		return Arrays.asList(
            new Object[][] {
                {"uuuuMMdd'T'HHmmssSSSSSSSSSXX",
                        "",
                        ZonalOffset.UTC,
                        "2012-06-30T23:59:60,123456789Z",
                        "20120630T235960123456789Z"},
                {"uuuu-MM-dd'T'HH:mm:ss.SSSSSSSSSXXX",
                        "",
                        ZonalOffset.UTC,
                        "2012-06-30T23:59:60,123456789Z",
                        "2012-06-30T23:59:60.123456789Z"},
                {"uuuu-MM-dd'T'HH:mm:ss.SSSSSSSSSXXX",
                        "",
                        TZID.ASIA.TOKYO,
                        "2012-06-30T23:59:60,123456789Z",
                        "2012-07-01T08:59:60.123456789+09:00"},
                {"uuuu-MM-dd'T'HH:mm:ss.SSSSSSSSSXXX",
                        "",
                        TZID.EUROPE.BERLIN,
                        "2012-06-30T23:59:60,123456789Z",
                        "2012-07-01T01:59:60.123456789+02:00"},
                {"uuuu-MM-dd'T'HH:mm:ss.SSSSSSXXX",
                        "",
                        TZID.EUROPE.BERLIN,
                        "2012-06-30T23:59:60,123456000Z",
                        "2012-07-01T01:59:60.123456+02:00"},
                {"uuuu-MM-dd'T'HH:mm:ss.SSSXXX",
                        "",
                        TZID.EUROPE.BERLIN,
                        "2012-06-30T23:59:60,123000000Z",
                        "2012-07-01T01:59:60.123+02:00"},
                {"uuuu-MM-dd'T'HH:mm:ss.SSSX",
                        "",
                        TZID.EUROPE.BERLIN,
                        "2012-06-30T23:59:60,123000000Z",
                        "2012-07-01T01:59:60.123+02"},
                {"d. MMMM uuuu HH:mm:ss.SSSSSSSSSXXX",
                        "de",
                        TZID.EUROPE.BERLIN,
                        "2012-06-30T23:59:60,123456789Z",
                        "1. Juli 2012 01:59:60.123456789+02:00"},
                {"d. MMMM uuuu HH:mm[:ss]XXX",
                        "de",
                        TZID.EUROPE.BERLIN,
                        "2012-06-30T23:59:60Z",
                        "1. Juli 2012 01:59:60+02:00"},
                {"d. MMMM uuuu HH:mm[:ss.SSSSSSSSS]XXX",
                        "de",
                        TZID.EUROPE.BERLIN,
                        "2012-06-30T23:59:60,123456789Z",
                        "1. Juli 2012 01:59:60.123456789+02:00"},
                {"d. MMMM uuuu HH:mm:ss'['VV']'",
                        "de",
                        TZID.EUROPE.BERLIN,
                        "2012-06-30T23:59:60Z",
                        "1. Juli 2012 01:59:60[Europe/Berlin]"},
                {"d. MMMM uuuu HH:mm'['VV']'",
                        "de",
                        TZID.EUROPE.BERLIN,
                        "2012-03-10T23:59Z",
                        "11. März 2012 00:59[Europe/Berlin]"},
                {"d. MMM uuuu HH:mm'['VV']'",
                        "de",
                        TZID.EUROPE.BERLIN,
                        "2012-03-10T23:59Z",
                        "11. Mrz 2012 00:59[Europe/Berlin]"},
                {"d. MMM uuuu HH:mm z",
                        "de",
                        TZID.EUROPE.BERLIN,
                        "2012-03-31T23:59Z",
                        "1. Apr 2012 01:59 MESZ"},
                {"d. MMM uuuu HH:mm zzzz",
                        "de",
                        TZID.EUROPE.BERLIN,
                        "2012-03-31T23:59Z",
                        "1. Apr 2012 01:59 Mitteleuropäische Sommerzeit"},
            }
        );
    }

    private ChronoFormatter<Moment> formatter;
    private Moment value;
    private String text;

    public FormatPatternTest(
        String pattern,
        String locale,
        TZID tzid,
        String value,
        String text
    ) throws ParseException {
        super();

        this.formatter =
            Moment.formatter(
                pattern,
                PatternType.CLDR,
                new Locale(locale, locale.toUpperCase()))
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

}