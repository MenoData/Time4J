package net.time4j.format.expert;

import net.time4j.PlainDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class OrFormatTest {

    @Test
    public void format1() throws ParseException {
        ChronoFormatter<PlainDate> f =
            ChronoFormatter.ofDatePattern(
                "E, dd.MM.uuuu|E, MM/dd/uuuu|EEEE, d. MMMM uuuu", PatternType.CLDR, Locale.ENGLISH);
        PlainDate date = PlainDate.of(2015, 12, 31);
        assertThat(f.format(date), is("Thu, 31.12.2015"));
    }

    @Test
    public void format2() throws ParseException {
        ChronoFormatter<PlainDate> f =
            ChronoFormatter.ofDatePattern(
                "E, MM/dd/uuuu|E, dd.MM.uuuu|EEEE, d. MMMM uuuu", PatternType.CLDR, Locale.ENGLISH);
        PlainDate date = PlainDate.of(2015, 12, 31);
        assertThat(f.format(date), is("Thu, 12/31/2015"));
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

}
