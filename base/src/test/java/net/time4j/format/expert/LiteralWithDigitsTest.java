package net.time4j.format.expert;

import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class LiteralWithDigitsTest {

    @Test
    public void testAlternativeLiteral() throws ParseException {
        ChronoFormatter<PlainTimestamp> formatter =
            ChronoFormatter.setUp(PlainTimestamp.class, Locale.ROOT)
                .addCustomized(PlainDate.COMPONENT, Iso8601Format.EXTENDED_CALENDAR_DATE)
                .addLiteral('T', ' ')
                .addCustomized(PlainTime.COMPONENT, Iso8601Format.EXTENDED_WALL_TIME)
                .build();
        assertThat(formatter.parse("2015-05-13T17:45"), is(PlainTimestamp.of(2015, 5, 13, 17, 45)));
        assertThat(formatter.parse("2015-05-13 17:45"), is(PlainTimestamp.of(2015, 5, 13, 17, 45)));
    }

    @Test
    public void printDateYYYYMM00() {
        ChronoFormatter<PlainDate> fmt =
            ChronoFormatter.ofDatePattern("uMM00", PatternType.CLDR, Locale.ROOT);
        assertThat(
            fmt.format(PlainDate.of(2014, 10, 4)),
            is("20141000"));
    }

    @Test
    public void parseDateYYYYMM00WithDefault() throws ParseException {
        ChronoFormatter<PlainDate> fmt = // year has variable width!
            ChronoFormatter.ofDatePattern("uMM00", PatternType.CLDR, Locale.ROOT)
                .withDefault(PlainDate.DAY_OF_MONTH, 4);
        assertThat(
            fmt.parse("20141000"),
            is(PlainDate.of(2014, 10, 4)));
    }

    @Test(expected=ParseException.class)
    public void parseDateYYYYMM00NoDefault() throws ParseException {
        ChronoFormatter<PlainDate> fmt = // year has variable width!
            ChronoFormatter.ofDatePattern("uMM00", PatternType.CLDR, Locale.ROOT);
        fmt.parse("20141000");
    }

    @Test
    public void printDateYYYYMM00_2() {
        ChronoFormatter<PlainDate> fmt =
            ChronoFormatter.ofDatePattern("uMM'00'", PatternType.CLDR, Locale.ROOT);
        assertThat(
            fmt.format(PlainDate.of(2014, 10, 4)),
            is("20141000"));
    }

    @Test
    public void parseDateYYYYMM00WithDefault_2() throws ParseException {
        ChronoFormatter<PlainDate> fmt = // year has variable width!
            ChronoFormatter.ofDatePattern("uMM'00'", PatternType.CLDR, Locale.ROOT)
                .withDefault(PlainDate.DAY_OF_MONTH, 4);
        assertThat(
            fmt.parse("20141000"),
            is(PlainDate.of(2014, 10, 4)));
    }

    @Test
    public void parseDateYYYYMM0XWithDefault_3() throws ParseException {
        ChronoFormatter<PlainDate> fmt = // year has variable width!
            ChronoFormatter.ofDatePattern("uMM'0x'", PatternType.CLDR, Locale.ROOT)
                .withDefault(PlainDate.DAY_OF_MONTH, 4);
        assertThat(
            fmt.parse("2014100x"),
            is(PlainDate.of(2014, 10, 4)));
    }

    @Test(expected=ParseException.class)
    public void parseDateYYYYMM00NoDefault_2() throws ParseException {
        ChronoFormatter<PlainDate> fmt = // year has variable width!
            ChronoFormatter.ofDatePattern("uMM'00'", PatternType.CLDR, Locale.ROOT);
        fmt.parse("20141000");
    }

    @Test
    public void printTimestamp() {
        ChronoFormatter<PlainTimestamp> fmt =
            ChronoFormatter.ofTimestampPattern("uMM00HHmm", PatternType.CLDR, Locale.ROOT);
        assertThat(
            fmt.format(PlainTimestamp.of(2014, 10, 4, 17, 45)),
            is("201410001745"));
    }

    @Test
    public void parseTimestampWithDefault() throws ParseException {
        ChronoFormatter<PlainTimestamp> fmt = // year has variable width!
            ChronoFormatter.ofTimestampPattern("uMM00HHmm", PatternType.CLDR, Locale.ROOT)
                .withDefault(PlainDate.DAY_OF_MONTH, 4);
        assertThat(
            fmt.parse("201410001745"),
            is(PlainTimestamp.of(2014, 10, 4, 17, 45)));
    }

    @Test(expected=ParseException.class)
    public void parseTimestampNoDefault() throws ParseException {
        ChronoFormatter<PlainTimestamp> fmt = // year has variable width!
            ChronoFormatter.ofTimestampPattern("uMM00HHmm", PatternType.CLDR, Locale.ROOT);
        fmt.parse("201410001745");
    }

}