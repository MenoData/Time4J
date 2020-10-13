package net.time4j.format.expert;

import net.time4j.PlainDate;
import net.time4j.format.Attributes;
import net.time4j.format.NumberSystem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class LiteralWithBidisTest {

    @Test(expected=ParseException.class)
    public void testMultiLiteralWithBidisISO() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern(
                "uuuu" + "\u200E\u200F" + "-MM-dd" + "\u061C",
                PatternType.CLDR,
                Locale.ROOT);
        formatter.parse("2015\u200E\u200F-12-20\u061C");
    }

    @Test(expected=ParseException.class)
    public void testMultiLiteralWithBidisEnglish() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern(
                "MM 'on' dd, uuuu",
                PatternType.CLDR,
                Locale.ENGLISH);
        formatter.parse("12 o\u200E\u200Fn 20, 2015");
    }

    @Test
    public void testMultiLiteralWithBidisArabic() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern(
                "uuuu" + "\u200E\u200F" + "-MM-dd" + "\u061C",
                PatternType.CLDR,
                new Locale("ar"))
                .with(Attributes.NUMBER_SYSTEM, NumberSystem.ARABIC);
        PlainDate expected = PlainDate.of(2015, 12, 20);
        assertThat(
            formatter.parse("2015\u200E\u200F-12-20\u061C"), // like pattern
            is(expected));
        assertThat(
            formatter.parse("2015-12-20"), // stripped version without bidis
            is(expected));
        assertThat(
            formatter.parse("2015\u200E\u200F-\u061C12-20"), // extra bidi in the midth
            is(expected));
        assertThat(
            formatter.parse("2015-12-20\u061C\u061C"), // extra bidi at the end
            is(expected));
    }

    @Test(expected=ParseException.class)
    public void testInterpunctuationLiteralWithBidisISO() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern(
                "uuuu-MM-dd",
                PatternType.CLDR,
                Locale.ROOT);
        formatter.parse("2015\u200E\u200F-\u061C12-20"); // extra bidis
    }

    @Test(expected=ParseException.class)
    public void testInterpunctuationLiteralWithBidisEnglish() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern(
                "uuuu-MM-dd",
                PatternType.CLDR,
                Locale.ENGLISH);
        formatter.parse("2015\u200E\u200F-\u061C12-20"); // extra bidis
    }

    @Test
    public void testInterpunctuationLiteralWithBidisArabic() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern(
                "uuuu-MM-dd",
                PatternType.CLDR,
                new Locale("ar"))
                .with(Attributes.NUMBER_SYSTEM, NumberSystem.ARABIC);
        PlainDate expected = PlainDate.of(2015, 12, 20);
        assertThat(
            formatter.parse("2015-12-20"), // like pattern (without bidis)
            is(expected));
        assertThat(
            formatter.parse("2015\u200E\u200F-\u061C12-20"), // extra bidis
            is(expected));
    }


}