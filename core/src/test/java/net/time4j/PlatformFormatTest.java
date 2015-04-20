package net.time4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class PlatformFormatTest {

    @Test
    public void printStdDate() {
        PlainDate date = PlainDate.of(2014, 5, 31);
        assertThat(
            PlainDate.formatter("MM/dd/yyyy", Platform.PATTERN, Locale.US).format(date),
            is("05/31/2014"));
    }

    @Test
    public void parseStdDate() throws ParseException {
        PlainDate date = PlainDate.of(2014, 5, 31);
        assertThat(
            PlainDate.formatter("MM/dd/yyyy", Platform.PATTERN, Locale.US).parse("05/31/2014"),
            is(date));
    }

    @Test
    public void printOldDate() {
        PlainDate date = PlainDate.of(1425, 5, 31);
        assertThat(
            PlainDate.formatter("MM/dd/yyyy", Platform.PATTERN, Locale.US).format(date),
            is("05/31/1425"));
    }

    @Test
    public void parseOldDate() throws ParseException {
        PlainDate date = PlainDate.of(1425, 5, 31);
        assertThat(
            PlainDate.formatter("MM/dd/yyyy", Platform.PATTERN, Locale.US).parse("05/31/1425"),
            is(date));
    }

}