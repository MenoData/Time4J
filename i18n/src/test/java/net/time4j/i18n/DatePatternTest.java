package net.time4j.i18n;

import net.time4j.Iso8601Format;
import net.time4j.PatternType;
import net.time4j.PlainDate;
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
public class DatePatternTest {

 	@Parameterized.Parameters
        (name= "{index}: "
            + "(pattern={0},locale={1},value={2},text={3})")
 	public static Iterable<Object[]> data() {
 		return Arrays.asList(
            new Object[][] {
                {"dd.MM.uuuu ('mjd'=g)",
                        "de",
                        "2014-05-30",
                        "30.05.2014 (mjd=56807)"},
                {"dd.MM.uuuu ('mjd'=g)",
                        "de",
                        "1858-11-17",
                        "17.11.1858 (mjd=0)"},
                {"F. EEEE 'im Monat,' dd.MM.uuuu",
                        "de",
                        "2014-05-30",
                        "5. Freitag im Monat, 30.05.2014"},
                {"F. EEEE 'in month,' MM/dd/uuuu",
                        "us",
                        "2014-05-19",
                        "3. Monday in month, 05/19/2014"},
                {"uuuu-DDD",
                        "",
                        "2014-12-31",
                        "2014-365"},
                {"uuuu-MM-dd (w)",
                        "",
                        "2009-12-28",
                        "2009-12-28 (53)"},
                {"uuuu-MM-dd (W)",
                        "de",
                        "2009-12-28",
                        "2009-12-28 (5)"},
                {"YYYY-MM-dd ('W'ww-e)",
                        "de",
                        "2010-01-03",
                        "2009-01-03 (W53-7)"},
                {"uuuu-MM-dd (QQ)",
                        "de",
                        "2009-12-28",
                        "2009-12-28 (04)"},
                {"uuuu-MM-dd (QQQ)",
                        "de",
                        "2009-12-28",
                        "2009-12-28 (Q4)"},
                {"uuuu-MM-dd (QQQQ)",
                        "de",
                        "2009-12-28",
                        "2009-12-28 (viertes Quartal)"},
                {"dd.MM.yyyy G",
                        "de",
                        "2009-12-28",
                        "28.12.2009 n. Chr."},
           }
        );
    }

    private ChronoFormatter<PlainDate> formatter;
    private PlainDate value;
    private String text;

    public DatePatternTest(
        String pattern,
        String locale,
        String value,
        String text
    ) throws ParseException {
        super();

        this.formatter =
            PlainDate.formatter(
                pattern,
                PatternType.CLDR,
                toLocale(locale));
        this.value = Iso8601Format.EXTENDED_CALENDAR_DATE.parse(value);
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