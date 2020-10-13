package net.time4j.format.expert;

import net.time4j.PlainDate;

import java.text.ParseException;
import java.util.Locale;

import net.time4j.engine.AttributeQuery;
import net.time4j.format.Attributes;
import net.time4j.format.Leniency;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;


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

        try {
            fmt.parse(this.text);
            fail("Trailing characters have been accepted.");
        } catch (ParseException pe) {
            assertThat(pe.getErrorOffset(), is(10));
            assertThat(
                pe.getMessage(),
                is("Unparsed trailing characters: ~34"));
        }
    }

    @Test
    public void withTrailingCharacters() throws ParseException {
        // parsing with intolerant parser but also with log => no exception
        ParseLog plog = new ParseLog();
        PlainDate date = this.formatter.parse(this.text, plog);
        PlainDate expected = PlainDate.of(2013, 9, 1);
        assertThat(date, is(expected));
        assertThat(plog.isError(), is(false));

        // parsing with tolerant parser without log
        date = this.formatterAny.parse(this.text);
        assertThat(date, is(expected));
    }

    @Test
    public void lax() throws ParseException {
        ChronoFormatter<PlainDate> fmt =
            this.formatter.with(Attributes.LENIENCY, Leniency.LAX);

        PlainDate date = fmt.parse(this.text);
        PlainDate expected = PlainDate.of(2013, 9, 1);
        assertThat(date, is(expected));
    }

    @Test
    public void laxWithAnyEndOfText() throws ParseException {
        ChronoFormatter<PlainDate> fmt =
            this.formatterAny.with(Attributes.LENIENCY, Leniency.LAX);

        PlainDate date = fmt.parse(this.text);
        PlainDate expected = PlainDate.of(2013, 9, 1);
        assertThat(date, is(expected));
    }

    @Test
    public void smart() {
        ChronoFormatter<PlainDate> fmt =
            this.formatter.with(Attributes.LENIENCY, Leniency.SMART);

        try {
            fmt.parse(this.text);
            fail("Trailing characters have been accepted.");
        } catch (ParseException pe) {
            assertThat(pe.getErrorOffset(), is(10));
            assertThat(
                pe.getMessage(),
                is("Unparsed trailing characters: ~34"));
        }
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

        try {
            fmt.parse(this.text);
            fail("Trailing characters have been accepted.");
        } catch (ParseException pe) {
            assertThat(pe.getErrorOffset(), is(10));
            assertThat(
                pe.getMessage(),
                is("Unparsed trailing characters: ~34"));
        }
    }

    @Test(expected=ParseException.class)
    public void strictWithAnyEndOfText() throws ParseException {
        ChronoFormatter<PlainDate> fmt =
            this.formatterAny.with(Attributes.LENIENCY, Leniency.STRICT);
        fmt.parse(this.text);
    }

    @Test
    public void withIgnorableCustomAttributes() {
        ChronoFormatter<PlainDate> fmt = this.formatter;

        AttributeQuery attrs =
            new Attributes.Builder()
            .set(Attributes.TRAILING_CHARACTERS, false) // does not matter because a parse log is specified
            .build();
        ParseLog plog = new ParseLog();
        PlainDate date = fmt.parse(this.text, plog, attrs);
        PlainDate expected = PlainDate.of(2013, 9, 1);
        assertThat(date, is(expected));
        assertThat(plog.isError(), is(false));
    }

}