package net.time4j.format;

import net.time4j.PlainDate;
import net.time4j.Weekday;
import net.time4j.Weekmodel;

import java.io.IOException;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class MiscellaneousTest {

    @Test
    public void formatLocalDayOfWeek() throws IOException {
        TextElement<?> te =
            TextElement.class.cast(Weekmodel.of(Locale.US).localDayOfWeek());
        Attributes attributes =
            new Attributes.Builder()
            .setLocale(Locale.GERMANY)
            .set(Attributes.PARSE_CASE_INSENSITIVE, true)
            .build();

        Appendable buffer = new StringBuilder();
        ParseLog status = new ParseLog();
        te.print(PlainDate.of(2013, 3, 8), buffer, attributes);
        assertThat(
            buffer.toString(),
            is("Freitag"));

        Object parseResult = te.parse("FreitaG", status, attributes);
        assertThat(parseResult.equals(Weekday.FRIDAY), is(true));
        assertThat(status.getPosition(), is(7));
        assertThat(status.getErrorIndex(), is(-1));
    }

}