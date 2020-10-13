package net.time4j.format.expert;

import net.time4j.PlainDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class SkipUnknownTest {

    @Test
    public void print() {
        ChronoFormatter<PlainDate> fmt =
            ChronoFormatter.setUp(PlainDate.class, Locale.US)
                .addText(PlainDate.DAY_OF_WEEK)
                .skipUnknown(10)
                .addPattern("[, ]dd.MM.uuuu", PatternType.CLDR)
                .build();
        assertThat(
            fmt.format(PlainDate.of(2014, 10, 4)),
            is("Saturday, 04.10.2014"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void invalidKeepRemainingChars() throws ParseException {
        ChronoFormatter.setUp(PlainDate.class, Locale.US)
            .addText(PlainDate.DAY_OF_WEEK)
            .skipUnknown(-1)
            .addPattern("[, ]dd.MM.uuuu", PatternType.CLDR)
            .build();
    }

    @Test(expected=IllegalArgumentException.class)
    public void invalidMaxIterations() throws ParseException {
        ChronoFormatter.setUp(PlainDate.class, Locale.US)
            .addText(PlainDate.DAY_OF_WEEK)
            .skipUnknown(c -> true, 0)
            .addPattern("[, ]dd.MM.uuuu", PatternType.CLDR)
            .build();
    }

    @Test(expected=NullPointerException.class)
    public void missingCondition() throws ParseException {
        ChronoFormatter.setUp(PlainDate.class, Locale.US)
            .addText(PlainDate.DAY_OF_WEEK)
            .skipUnknown(null, 1)
            .addPattern("[, ]dd.MM.uuuu", PatternType.CLDR)
            .build();
    }

    @Test
    public void parseKeepRemainingChars() throws ParseException {
        ChronoFormatter<PlainDate> fmt =
            ChronoFormatter.setUp(PlainDate.class, Locale.US)
                .addText(PlainDate.DAY_OF_WEEK)
                .skipUnknown(10)
                .addPattern("[, ]dd.MM.uuuu", PatternType.CLDR)
                .build();
        assertThat(
            fmt.parse("Saturday,\txyz\n04.10.2014"),
            is(PlainDate.of(2014, 10, 4)));
    }

    @Test(expected=ParseException.class)
    public void parseWithTrailingCharsKept() throws ParseException {
        ChronoFormatter<PlainDate> fmt =
            ChronoFormatter.setUp(PlainDate.class, Locale.US)
                .addText(PlainDate.DAY_OF_WEEK)
                .addPattern("[, ]dd.MM.uuuu", PatternType.CLDR)
                .skipUnknown(3)
                .build();
        try {
            fmt.parse("Saturday, 04.10.2014\txyz\n");
        } catch (ParseException pe) {
            assertThat(pe.getErrorOffset(), is(22));
            throw pe;
        }
    }

    @Test
    public void parseWithTrailingCharsRemoved() throws ParseException {
        ChronoFormatter<PlainDate> fmt =
            ChronoFormatter.setUp(PlainDate.class, Locale.US)
                .addText(PlainDate.DAY_OF_WEEK)
                .addPattern("[, ]dd.MM.uuuu", PatternType.CLDR)
                .skipUnknown(0) // same effect as with format attribute TRAILING_CHARACTERS
                .build();
        assertThat(
            fmt.parse("Saturday, 04.10.2014\txyz\n"),
            is(PlainDate.of(2014, 10, 4)));
    }

    @Test
    public void parseWithNonDigitCondition() throws ParseException {
        ChronoFormatter<PlainDate> fmt =
            ChronoFormatter.setUp(PlainDate.class, Locale.US)
                .addText(PlainDate.DAY_OF_WEEK)
                .skipUnknown(
                    c -> ((c < '0') || (c > '9')),
                    Integer.MAX_VALUE
                )
                .addFixedInteger(PlainDate.DAY_OF_MONTH, 2)
                .addLiteral('.')
                .addFixedInteger(PlainDate.MONTH_AS_NUMBER, 2)
                .addLiteral('.')
                .addFixedInteger(PlainDate.YEAR, 4)
                .build();
        assertThat(
            fmt.parse("Saturday,\txyz\n04.10.2014"),
            is(PlainDate.of(2014, 10, 4)));
    }

}