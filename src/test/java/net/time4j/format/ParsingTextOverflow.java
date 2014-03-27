package net.time4j.format;

import net.time4j.PatternType;
import net.time4j.PlainDate;

import java.text.ParseException;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class ParsingTextOverflow {

    private ChronoFormatter<PlainDate> formatter;
    private ChronoFormatter<PlainDate> formatterAny;
    private String text;

    @Before
    public void initialize() {
        this.formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.ROOT)
                .addPattern("MM/dd/yyyy", PatternType.CLDR).build();
        this.formatterAny =
            ChronoFormatter.setUp(PlainDate.class, Locale.ROOT)
                .addPattern("MM/dd/y", PatternType.CLDR)
                .addAny()
                .build();
        this.text = "09/01/3~34";
    }

    @Test
    public void lax() {
        ChronoFormatter<PlainDate> fmt =
            this.formatter.with(Attributes.LENIENCY, Leniency.LAX);

        ParseLog plog = new ParseLog();
        PlainDate date = fmt.parse(this.text, plog);
        PlainDate expected = PlainDate.of(3, 9, 1);
        assertThat(date, is(expected));
        assertThat(plog.isError(), is(false));
    }

    @Test
    public void laxWithAnyEndOfText() {
        ChronoFormatter<PlainDate> fmt =
            this.formatterAny.with(Attributes.LENIENCY, Leniency.LAX);

        ParseLog plog = new ParseLog();
        PlainDate date = fmt.parse(this.text, plog);
        PlainDate expected = PlainDate.of(3, 9, 1);
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
        assertThat(plog.getErrorIndex(), is(7));
        assertThat(plog.getErrorMessage(), is("Unparsed: ~34"));
    }

    @Test
    public void smartWithAnyEndOfText() {
        ChronoFormatter<PlainDate> fmt =
            this.formatterAny.with(Attributes.LENIENCY, Leniency.SMART);

        ParseLog plog = new ParseLog();
        PlainDate date = fmt.parse(this.text, plog);
        PlainDate expected = PlainDate.of(3, 9, 1);
        assertThat(date, is(expected));
        assertThat(plog.isError(), is(false));
    }

    @Test
    public void strict() {
        ChronoFormatter<PlainDate> fmt =
            this.formatter.with(Attributes.LENIENCY, Leniency.STRICT);

        ParseLog plog = new ParseLog();
        PlainDate date = fmt.parse(this.text, plog);
        PlainDate expected = null;
        assertThat(date, is(expected));
        assertThat(plog.getErrorIndex(), is(6));
        assertThat(plog.getErrorMessage(), is("Not enough digits found."));
    }

    @Test(expected=ParseException.class)
    public void strictWithAnyEndOfText() throws ParseException {
        ChronoFormatter<PlainDate> fmt =
            this.formatterAny.with(Attributes.LENIENCY, Leniency.STRICT);

        fmt.parse(this.text);
    }

}