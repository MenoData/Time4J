package net.time4j.format.expert;

import net.time4j.PlainDate;
import net.time4j.format.Attributes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.format.FormatStyle;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class StyleProcessorTest {

    @Test
    public void flexibleStylePattern() {
        PlainDate date = PlainDate.of(2016, 12, 18);
        ChronoFormatter<PlainDate> chinese =
            ChronoFormatter.ofDateStyle(FormatStyle.LONG, Locale.CHINESE);
        ChronoFormatter<PlainDate> english =
            ChronoFormatter.ofDateStyle(FormatStyle.LONG, Locale.ENGLISH);
        assertThat(
            chinese.with(Locale.ENGLISH).format(date),
            is(english.format(date))
        );
    }

    @Test
    public void fourDigitYear() {
        ChronoFormatter<PlainDate> f =
            ChronoFormatter.ofDateStyle(FormatStyle.SHORT, Locale.ENGLISH);
        assertThat(
            f.format(PlainDate.of(2016, 12, 23)),
            is("12/23/16")
        );
        assertThat(
            f.with(Attributes.FOUR_DIGIT_YEAR, true).format(PlainDate.of(2016, 12, 23)),
            is("12/23/2016")
        );
    }

}
