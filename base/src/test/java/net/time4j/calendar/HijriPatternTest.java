package net.time4j.calendar;

import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class HijriPatternTest {

    @Parameterized.Parameters(name= "{index}: pattern={3}, umalqura({0}-{1}-{2})={4}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
            new Object[][]{
                {1300, 1, 1, "yyyy-MM-dd", "1300-01-01"},
                {1355, 12, 29, "MM/dd/yyyy", "12/29/1355"},
                {1436, 9, 29, "d. MMMM yyyy", "29. Ramadan 1436"},
                {1436, 5, 29, "d. LLLL yyyy", "29. Dsemádi I 1436"},
                {1436, 10, 1, "EEE, d. MMM yyyy", "Fri, 1. Shaw. 1436"},
                {1436, 10, 2, "G yyyy, MM/dd", "AH 1436, 10/02"},
                {1437, 1, 1, "yyyy (D)", "1437 (1)"},
                {1401, 1, 1, "yy (D)", "01 (1)"},
                {1499, 1, 1, "yy (D)", "99 (1)"},
            }
        );
    }

    private HijriCalendar umalqura;
    private ChronoFormatter<HijriCalendar> formatter;
    private ChronoFormatter<HijriCalendar> cldrFormatter;
    private String text;

    public HijriPatternTest(
        int year,
        int month,
        int dom,
        String pattern,
        String text
    ) throws ParseException {
        super();

        if (year == 1401 || year == 1499) {
            int py = HijriCalendar.family().getDefaultPivotYear();
            int yearOfCentury = year % 100;
            int century;
            if (yearOfCentury >= (py % 100)) {
                century = (((py / 100) - 1) * 100);
            } else {
                century = ((py / 100) * 100);
            }
            year = (century + yearOfCentury);
        }

        this.umalqura = HijriCalendar.ofUmalqura(year, month, dom);
        Locale loc = Locale.ENGLISH;
        if (pattern.contains("LLLL")) {
            loc = new Locale("hu");
        }
        this.formatter =
            ChronoFormatter.setUp(HijriCalendar.class, loc)
                .addPattern(pattern, PatternType.CLDR_DATE).build()
                .withCalendarVariant(HijriCalendar.VARIANT_UMALQURA);
        this.cldrFormatter =
            ChronoFormatter.setUp(HijriCalendar.class, loc)
                .addPattern(pattern, PatternType.CLDR).build()
                .withCalendarVariant(HijriCalendar.VARIANT_UMALQURA);
        this.text = text;
    }

    @Test
    public void print() {
        assertThat(
            this.formatter.format(this.umalqura),
            is(this.text));
        assertThat(
            this.cldrFormatter.format(this.umalqura),
            is(this.text));
    }

    @Test
    public void parse() throws ParseException {
        assertThat(
            this.formatter.parse(this.text),
            is(this.umalqura));
        assertThat(
            this.cldrFormatter.parse(this.text),
            is(this.umalqura));
    }

}
