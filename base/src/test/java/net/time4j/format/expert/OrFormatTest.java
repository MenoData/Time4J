package net.time4j.format.expert;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class OrFormatTest {

    @Test
    public void format1() {
        ChronoFormatter<PlainDate> f =
            ChronoFormatter.ofDatePattern(
                "E, dd.MM.uuuu|E, MM/dd/uuuu|EEEE, d. MMMM uuuu", PatternType.CLDR, Locale.ENGLISH);
        PlainDate date = PlainDate.of(2015, 12, 31);
        assertThat(f.format(date), is("Thu, 31.12.2015"));
    }

    @Test
    public void format2() {
        ChronoFormatter<PlainDate> f =
            ChronoFormatter.ofDatePattern(
                "E, MM/dd/uuuu|E, dd.MM.uuuu|EEEE, d. MMMM uuuu", PatternType.CLDR, Locale.ENGLISH);
        PlainDate date = PlainDate.of(2015, 12, 31);
        assertThat(f.format(date), is("Thu, 12/31/2015"));
    }

    @Test
    public void format3() {
        ChronoFormatter<PlainDate> f =
            ChronoFormatter.setUp(PlainDate.axis(), Locale.ROOT)
                .addFixedInteger(PlainDate.DAY_OF_YEAR, 2)
                .or()
                .addFixedInteger(PlainDate.DAY_OF_YEAR, 3)
                .build();
        PlainDate date1 = PlainDate.of(2015, 1, 1);
        assertThat(f.format(date1), is("01"));
        PlainDate date2 = PlainDate.of(2015, 12, 31);
        assertThat(f.format(date2), is("365"));
    }

    @Test
    public void parse() throws ParseException {
        ChronoFormatter<PlainDate> f =
            ChronoFormatter.ofDatePattern(
                "E, dd.MM.uuuu|E, MM/dd/uuuu|EEEE, d. MMMM uuuu", PatternType.CLDR, Locale.ENGLISH);
        PlainDate expected = PlainDate.of(2015, 12, 31);
        assertThat(f.parse("Thu, 31.12.2015"), is(expected));
        assertThat(f.parse("Thu, 12/31/2015"), is(expected));
        assertThat(f.parse("Thursday, 31. December 2015"), is(expected));
    }

    @Test
    public void parseWithOrInsideOptional() throws ParseException {
        ChronoFormatter<PlainDate> f =
            ChronoFormatter.ofDatePattern(
                "E, [dd.MM.|MM/dd/]uuuu", PatternType.CLDR, Locale.ENGLISH);
        PlainDate expected = PlainDate.of(2015, 12, 31);
        assertThat(f.parse("Thu, 31.12.2015"), is(expected));
        assertThat(f.parse("Thu, 12/31/2015"), is(expected));
    }

    @Test(expected=ParseException.class)
    public void parseUnexpectedTrailingChars() throws ParseException {
        ChronoFormatter<PlainDate> f =
            ChronoFormatter.ofDatePattern("dd.MM.uuuu|MM/dd/uuuu", PatternType.CLDR, Locale.ROOT);
        f.parse("31.12.2015|12/31/2015");
    }

    @Test(expected=ParseException.class)
    public void parseUnexpectedLanguage() throws ParseException {
        ChronoFormatter<PlainDate> f =
            ChronoFormatter.ofDatePattern("dd.MM.uuuu|MM/dd/uuuu|d. MMMM uuuu", PatternType.CLDR, Locale.ENGLISH);
        f.parse("31. diciembre 2015"); // spanish was not set up
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void parseEmptyString() throws ParseException {
        ChronoFormatter<PlainDate> f =
            ChronoFormatter.ofDatePattern("dd.MM.uuuu|MM/dd/uuuu|d. MMMM uuuu", PatternType.CLDR, Locale.ENGLISH);
        f.parse("");
    }

    @Test(expected=NullPointerException.class)
    public void parseNull() throws ParseException {
        ChronoFormatter<PlainDate> f =
            ChronoFormatter.ofDatePattern("dd.MM.uuuu|MM/dd/uuuu|d. MMMM uuuu", PatternType.CLDR, Locale.ENGLISH);
        f.parse(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void parseDoubleOr() throws ParseException {
        ChronoFormatter.ofDatePattern("dd.MM.uuuu||MM/dd/uuuu", PatternType.CLDR, Locale.ENGLISH);
    }

    @Test(expected=IllegalArgumentException.class)
    public void parseTrailingOr1() throws ParseException {
        ChronoFormatter.ofDatePattern("dd.MM.uuuu|MM/dd/uuuu|", PatternType.CLDR, Locale.ENGLISH);
    }

    @Test(expected=IllegalArgumentException.class)
    public void parseTrailingOr2() throws ParseException {
        ChronoFormatter.ofDatePattern("E, [dd.MM.|MM/dd/|]uuuu", PatternType.CLDR, Locale.ENGLISH);
    }

    @Test(expected=IllegalArgumentException.class)
    public void parseLeadingOr1() throws ParseException {
        ChronoFormatter.ofDatePattern("|dd.MM.uuuu|MM/dd/uuuu", PatternType.CLDR, Locale.ENGLISH);
    }

    @Test(expected=IllegalArgumentException.class)
    public void parseLeadingOr2() throws ParseException {
        ChronoFormatter.ofDatePattern("E, [|dd.MM.|MM/dd/]uuuu", PatternType.CLDR, Locale.ENGLISH);
    }

    @Test
    public void parseWildcards1() throws ParseException {
        String input = "****-04-01T00:00:00Z/****-04-06T11:55:00Z";
        int slash = input.indexOf('/');
        PlainDate d = PlainDate.of(2016, 3, 31);
        ChronoFormatter<Moment> f =
            ChronoFormatter.ofMomentPattern(
                "[uuuu|****]-[MM|**]-[dd|**]'T'HH:mm:ssX", PatternType.CLDR, Locale.ROOT, ZonalOffset.UTC)
            .withDefault(PlainDate.YEAR, d.getYear())
            .withDefault(PlainDate.MONTH_AS_NUMBER, d.getMonth())
            .withDefault(PlainDate.DAY_OF_MONTH, d.getDayOfMonth());
        Moment start = f.parse(input.substring(0, slash));
        Moment end = f.parse(input.substring(slash + 1));
        assertThat(
            start + "/" + end,
            is("2016-04-01T00:00:00Z/2016-04-06T11:55:00Z"));
    }

    @Test
    public void parseWildcards2() throws ParseException {
        String input = "****-04-01T00:00:00Z/****-04-06T11:55:00Z";
        int slash = input.indexOf('/');
        PlainDate d = PlainDate.of(2016, 3, 31);
        ChronoFormatter<Moment> f =
            ChronoFormatter.setUp(Moment.axis(), Locale.ROOT)
                .addPattern("[uuuu|", PatternType.CLDR)
                .skipUnknown((c) -> c == '*', 4)
                .addPattern("]-[MM|", PatternType.CLDR)
                .skipUnknown((c) -> c == '*', 2)
                .addPattern("]-[dd|", PatternType.CLDR)
                .skipUnknown((c) -> c == '*', 2)
                .addPattern("]'T'HH:mm:ssX", PatternType.CLDR)
                .build()
                .withDefault(PlainDate.YEAR, d.getYear())
                .withDefault(PlainDate.MONTH_AS_NUMBER, d.getMonth())
                .withDefault(PlainDate.DAY_OF_MONTH, d.getDayOfMonth());
        Moment start = f.parse(input.substring(0, slash));
        Moment end = f.parse(input.substring(slash + 1));
        assertThat(
            start + "/" + end,
            is("2016-04-01T00:00:00Z/2016-04-06T11:55:00Z"));
    }

}
