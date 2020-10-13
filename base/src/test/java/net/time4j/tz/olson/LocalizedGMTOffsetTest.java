package net.time4j.tz.olson;

import net.time4j.Moment;
import net.time4j.format.Attributes;
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
public class LocalizedGMTOffsetTest {

    @Parameterized.Parameters
        (name= "{index}: "
            + "(pattern={0},locale={1},timezone={2},value={3},text={4},noGMTPrefix={5})")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
            new Object[][] {
                {"uuuu-MM-dd'T'HH:mm:ss.SSS OOOO",
                    "sq",
                    "UTC",
                    "2012-06-30T23:59:60,123000000Z",
                    "2012-06-30T23:59:60.123 GMT",
                    false},
                {"uuuu-MM-dd'T'HH:mm:ss.SSS OOOO",
                    "sq",
                    "Europe/Berlin",
                    "2012-06-30T23:59:60,123000000Z",
                    "2012-07-01T01:59:60.123 GMT+02:00",
                    false},
                {"uuuu-MM-dd HH:mm:ss OOOO",
                    "fa",
                    "Asia/Tehran", // +04:30
                    "2012-06-30T23:59:60Z",
                    "۲۰۱۲-۰۷-۰۱ ۰۴:۲۹:۶۰ \u200E+۰۴:۳۰ گرینویچ",
                    false},
                {"uuuu-MM-dd'T'HH:mm:ss.SSS OOOO",
                    "no",
                    "Europe/Oslo",
                    "2012-06-30T23:59:60,123000000Z",
                    "2012-07-01T01:59:60.123 GMT+02:00",
                    false},
                {"uuuu-MM-dd'T'HH:mm:ss.SSS OOOO",
                    "fr",
                    "Europe/Paris",
                    "2012-06-30T23:59:60,123000000Z",
                    "2012-07-01T01:59:60.123 UTC+02:00",
                    false},
                {"uuuu-MM-dd'T'HH:mm:ss.SSSOOOO",
                    "in",
                    "Asia/Kolkata",
                    "2012-06-30T23:59:60,123000000Z",
                    "2012-07-01T05:29:60.123GMT+05:30",
                    false},
                {"uuuu-MM-dd'T'HH:mm:ss.SSSOOOO",
                    "ar",
                    "Asia/Kolkata",
                    "2012-06-30T23:59:60,123000000Z",
                    "٢٠١٢-٠٧-٠١T٠٥:٢٩:٦٠.١٢٣غرينتش\u061C+٠٥:٣٠", // with ALM-marker
                    false},
                {"uuuu-MM-dd'T'HH:mm:ss.SSSOOOO",
                    "ar-DZ",
                    "Asia/Kolkata",
                    "2012-06-30T23:59:60,123000000Z",
                    "2012-07-01T05:29:60.123غرينتش\u061C+05:30", // with ALM-marker
                    false},
                {"uuuu-MM-dd'T'HH:mm:ss.SSSOOOO",
                    "ar-DZ",
                    "Asia/Kolkata",
                    "2012-06-30T23:59:60,123000000Z",
                    "2012-07-01T05:29:60.123\u061C+05:30", // with ALM-marker
                    true},
                {"uuuu-MM-dd'T'HH:mm:ssOOOO",
                    "en-ARABEXT", // language en makes sure not to use bidi chars for sign representation
                    "UTC",
                    "2015-11-02T18:44:34Z",
                    "۲۰۱۵-۱۱-۰۲T۱۸:۴۴:۳۴+۰۰:۰۰",
                    true},
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
        String text,
        boolean noGMTPrefix
    ) throws ParseException {
        super();

        this.formatter =
            ChronoFormatter.setUp(Moment.class, toLocale(locale))
                .addPattern(pattern, PatternType.CLDR).build()
                .withTimezone(tzid)
                .with(Attributes.NO_GMT_PREFIX, noGMTPrefix);
        if (locale.endsWith("-ARABEXT")) {
            this.formatter = this.formatter.with(Attributes.ZERO_DIGIT, '۰');
        }
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
        if (locale.startsWith("en")) {
            return Locale.UK;
        } else if (locale.equals("in")) {
            return new Locale("en", "IN");
        } else if (locale.equals("ar")) {
            return new Locale("ar");
        } else if (locale.equals("ar-DZ")) {
            return new Locale("ar", "DZ");
        }
        return new Locale(locale, locale.toUpperCase());
    }

}