package net.time4j.format;

import net.time4j.PatternType;
import net.time4j.PlainDate;

import java.text.ParseException;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class ParsingTextOverflowTest {

    private ChronoFormatter<PlainDate> formatter;
    private ChronoFormatter<PlainDate> formatterAny;
    private String text;

    @Before
    public void initialize() {
        this.formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.ROOT)
                .addPattern("MM/dd/y", PatternType.CLDR)
                .build();
        this.formatterAny =
            ChronoFormatter.setUp(PlainDate.class, Locale.ROOT)
                .addPattern("MM/dd/y", PatternType.CLDR)
                .build()
                .with(Attributes.TRAILING_CHARACTERS, true);
        this.text = "09/01/2013~34";
    }

    @Test
    public void noTrailingCharacters() {
        ChronoFormatter<PlainDate> fmt = this.formatter;

        ParseLog plog = new ParseLog();
        PlainDate date = fmt.parse(this.text, plog);
        PlainDate expected = null;
        assertThat(date, is(expected));
        assertThat(plog.getErrorIndex(), is(10));
        assertThat(
            plog.getErrorMessage(),
            is("Unparsed trailing characters: ~34"));
    }

    @Test
    public void withTrailingCharacters() {
        ChronoFormatter<PlainDate> fmt = this.formatterAny;

        ParseLog plog = new ParseLog();
        PlainDate date = fmt.parse(this.text, plog);
        PlainDate expected = PlainDate.of(2013, 9, 1);
        assertThat(date, is(expected));
        assertThat(plog.isError(), is(false));
    }

    @Test
    public void lax() {
        ChronoFormatter<PlainDate> fmt =
            this.formatter.with(Attributes.LENIENCY, Leniency.LAX);

        ParseLog plog = new ParseLog();
        PlainDate date = fmt.parse(this.text, plog);
        PlainDate expected = PlainDate.of(2013, 9, 1);
        assertThat(date, is(expected));
        assertThat(plog.isError(), is(false));
    }

    @Test
    public void laxWithAnyEndOfText() {
        ChronoFormatter<PlainDate> fmt =
            this.formatterAny.with(Attributes.LENIENCY, Leniency.LAX);

        ParseLog plog = new ParseLog();
        PlainDate date = fmt.parse(this.text, plog);
        PlainDate expected = PlainDate.of(2013, 9, 1);
        assertThat(date, is(expected));
        assertThat(plog.isError(), is(false));
    }

    @Test
    public void smart() {
        ChronoFormatter<PlainDate> fmt =
            this.formatter.with(Attributes.LENIENCY, Leniency.SMART);

        ParseLog plog = new ParseLog();
        PlainDate date = fmt.parse(this.text, plog);
        PlainDate expected = null;
        assertThat(date, is(expected));
        assertThat(plog.getErrorIndex(), is(10));
        assertThat(
            plog.getErrorMessage(),
            is("Unparsed trailing characters: ~34"));
    }

    @Test(expected=ParseException.class)
    public void smartWithAnyEndOfText() throws ParseException {
        ChronoFormatter<PlainDate> fmt =
            this.formatterAny.with(Attributes.LENIENCY, Leniency.SMART);
        fmt.parse(this.text);
    }

    @Test
    public void strict() {
        ChronoFormatter<PlainDate> fmt =
            this.formatter.with(Attributes.LENIENCY, Leniency.STRICT);

        ParseLog plog = new ParseLog();
        PlainDate date = fmt.parse(this.text, plog);
        PlainDate expected = null;
        assertThat(date, is(expected));
        assertThat(plog.getErrorIndex(), is(10));
        assertThat(
            plog.getErrorMessage(),
            is("Unparsed trailing characters: ~34"));
    }

    @Test(expected=ParseException.class)
    public void strictWithAnyEndOfText() throws ParseException {
        ChronoFormatter<PlainDate> fmt =
            this.formatterAny.with(Attributes.LENIENCY, Leniency.STRICT);
        fmt.parse(this.text);
    }

}