package net.time4j.history;

import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;
import net.time4j.format.TextWidth;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class EraFormatTest {

    @Test
    public void print() {
        ChronoFormatter<PlainTimestamp> formatter =
            ChronoFormatter.setUp(PlainTimestamp.class, Locale.GERMANY)
                .addPattern("d. MMMM yyyy GGGG HH:mm", PatternType.CLDR).build();
        assertThat(
            formatter.format(PlainTimestamp.of(1582, 10, 14, 0, 0)),
            is("4. Oktober 1582 nach Christi Geburt 00:00"));
    }

    @Test
    public void parse() throws ParseException {
        ChronoFormatter<PlainTimestamp> formatter =
            ChronoFormatter.setUp(PlainTimestamp.class, Locale.GERMANY)
                .addPattern("d. MMMM yyyy GGGG HH:mm", PatternType.CLDR).build();
        assertThat(
            formatter.parse("4. Oktober 1582 nach Christi Geburt 00:00"),
            is(PlainTimestamp.of(1582, 10, 14, 0, 0)));
    }

}