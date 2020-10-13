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
public class MultiFormatTest {

    @Test
    public void parse() throws ParseException {
        MultiFormatParser<PlainDate> mfp = createMultipleFormat();
        PlainDate expected = PlainDate.of(2015, 12, 31);
        assertThat(mfp.parse("31.12.2015"), is(expected));
        assertThat(mfp.parse("12/31/2015"), is(expected));
        assertThat(mfp.parse("31. Dezember 2015"), is(expected));
        assertThat(mfp.parse("31. décembre 2015"), is(expected));
        assertThat(mfp.parse("31st of December 2015"), is(expected));
    }

    @Test(expected=ParseException.class)
    public void parseTrailingChars() throws ParseException {
        MultiFormatParser<PlainDate> mfp = createMultipleFormat();
        mfp.parse("31.12.2015xyz");
    }

    @Test(expected=ParseException.class)
    public void parseUnexpectedLiterals() throws ParseException {
        MultiFormatParser<PlainDate> mfp = createMultipleFormat();
        mfp.parse("31-12-2015");
    }

    @Test(expected=ParseException.class)
    public void parseUnexpectedLanguage() throws ParseException {
        MultiFormatParser<PlainDate> mfp = createMultipleFormat();
        mfp.parse("31. diciembre 2015"); // spanish was not set up
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void parseEmptyString() throws ParseException {
        MultiFormatParser<PlainDate> mfp = createMultipleFormat();
        mfp.parse("");
    }

    @Test(expected=NullPointerException.class)
    public void parseNull() throws ParseException {
        MultiFormatParser<PlainDate> mfp = createMultipleFormat();
        mfp.parse(null);
    }

    private static MultiFormatParser<PlainDate> createMultipleFormat() {
        ChronoFormatter<PlainDate> germanStyle =
            ChronoFormatter.ofDatePattern("dd.MM.uuuu", PatternType.CLDR, Locale.GERMAN);
        ChronoFormatter<PlainDate> germanStyle2 =
            ChronoFormatter.ofDatePattern("d. MMMM uuuu", PatternType.CLDR, Locale.GERMAN);
        ChronoFormatter<PlainDate> frenchStyle =
            ChronoFormatter.ofDatePattern("d. MMMM uuuu", PatternType.CLDR, Locale.FRENCH);
        ChronoFormatter<PlainDate> usStyle =
            ChronoFormatter.ofDatePattern("MM/dd/uuuu", PatternType.CLDR, Locale.US);
        ChronoFormatter<PlainDate> usStyle2 =
            ChronoFormatter.setUp(PlainDate.axis(), Locale.US)
                .addEnglishOrdinal(PlainDate.DAY_OF_MONTH)
                .addPattern(" 'of' MMMM uuuu", PatternType.CLDR)
                .build();
        return MultiFormatParser.of(germanStyle, germanStyle2, frenchStyle, usStyle, usStyle2);
    }

}
