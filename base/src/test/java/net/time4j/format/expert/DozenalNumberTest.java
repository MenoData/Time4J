package net.time4j.format.expert;

import net.time4j.PlainDate;
import net.time4j.TemporalType;
import net.time4j.format.Attributes;
import net.time4j.format.NumberSystem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class DozenalNumberTest {

    @Test
    public void toNumeral() {
        assertThat(
            NumberSystem.DOZENAL.toNumeral(22),
            is("1\u218A"));
        assertThat(
            NumberSystem.DOZENAL.toNumeral(35),
            is("2\u218B"));
    }

    @Test
    public void toInteger() {
        assertThat(
            NumberSystem.DOZENAL.toInteger("1\u218A"),
            is(22));
        assertThat(
            NumberSystem.DOZENAL.toInteger("2\u218B"),
            is(35));
    }

    @Test
    public void printDate1() {
        ChronoFormatter<PlainDate> f =
            ChronoFormatter.setUp(PlainDate.axis(), Locale.ROOT)
                .addFixedInteger(PlainDate.YEAR, 4)
                .addLiteral('-')
                .padNext(2)
                .addInteger(PlainDate.MONTH_AS_NUMBER, 1, 2)
                .addLiteral('-')
                .padNext(2)
                .addInteger(PlainDate.DAY_OF_MONTH, 1, 2)
                .build()
                .with(Attributes.NUMBER_SYSTEM, NumberSystem.DOZENAL)
                .with(Attributes.PAD_CHAR, '0');
        assertThat(
            f.format(PlainDate.of(2017, 10, 11)),
            is("1201-0\u218A-0\u218B"));
    }

    @Test
    public void printDate2() {
        ChronoFormatter<LocalDate> f =
            ChronoFormatter.ofPattern(
                "yyyy-ppM-ppd",
                PatternType.THREETEN,
                Locale.forLanguageTag("fr-u-nu-dozenal"), // set the number system to DOZENAL
                PlainDate.threeten()
            ).with(Attributes.PAD_CHAR, '0');
        assertThat(
            f.format(LocalDate.of(2017, 10, 11)),
            is("1201-0\u218A-0\u218B"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void printDateWithWrongUnicodeExtension() {
        ChronoFormatter.ofPattern(
            "yyyy-ppM-ppd",
            PatternType.THREETEN,
            Locale.forLanguageTag("fr-u-nu-dozl"), // wrong extension code
            PlainDate.threeten()
        );
    }

    @Test
    public void toNumeralWithBuffer() throws IOException {
        for (int num = 0; num < 20736; num++) {
            StringBuilder sb = new StringBuilder();
            String expected = NumberSystem.DOZENAL.toNumeral(num);
            int count = NumberSystem.DOZENAL.toNumeral(num, sb);
            assertThat(count, is(expected.length()));
            assertThat(sb.toString(), is(expected));
        }
    }

}