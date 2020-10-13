package net.time4j.format.expert;

import net.time4j.PlainDate;

import java.text.ParseException;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class WhitespaceTest {

    @Test
    public void print() {
        ChronoFormatter<PlainDate> fmt =
            ChronoFormatter.setUp(PlainDate.class, Locale.US)
                .addFixedInteger(PlainDate.DAY_OF_MONTH, 2)
                .addFixedInteger(PlainDate.MONTH_AS_NUMBER, 2)
                .addFixedInteger(PlainDate.YEAR, 4)
                .addIgnorableWhitespace()
                .addLiteral('(')
                .addText(PlainDate.DAY_OF_WEEK)
                .addLiteral(')')
                .build();
        assertThat(
            fmt.format(PlainDate.of(2014, 10, 4)),
            is("04102014 (Saturday)"));
    }

    @Test
    public void parse() throws ParseException {
        ChronoFormatter<PlainDate> fmt =
            ChronoFormatter.setUp(PlainDate.class, Locale.US)
                .addFixedInteger(PlainDate.DAY_OF_MONTH, 2)
                .addFixedInteger(PlainDate.MONTH_AS_NUMBER, 2)
                .addFixedInteger(PlainDate.YEAR, 4)
                .addIgnorableWhitespace()
                .addLiteral('(')
                .addText(PlainDate.DAY_OF_WEEK)
                .addLiteral(')')
                .build();
        assertThat(
            fmt.parse("04102014      \t\n(Saturday)"),
            is(PlainDate.of(2014, 10, 4)));
    }

}