package net.time4j.calendar.frenchrev;

import net.time4j.PlainDate;
import net.time4j.engine.CalendarDays;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class FormatTest {

    @Test
    public void printWithStdArabicYear() {
        ChronoFormatter<FrenchRepublicanCalendar> f =
            ChronoFormatter.setUp(FrenchRepublicanCalendar.axis(), Locale.FRENCH)
                .addPattern("D. MMMM', an '", PatternType.DYNAMIC)
                .addFixedInteger(FrenchRepublicanCalendar.YEAR_OF_ERA, 5)
                .or()
                .addPattern("SSSS', an '", PatternType.DYNAMIC)
                .addFixedInteger(FrenchRepublicanCalendar.YEAR_OF_ERA, 5)
                .build();
        FrenchRepublicanCalendar cal = PlainDate.of(2018, 9, 23).transform(FrenchRepublicanCalendar.axis());
        assertThat(
            f.format(cal),
            is("1. vendémiaire, an 00227"));
    }

    @Test
    public void parseWithStdArabicYear() throws ParseException {
        ChronoFormatter<FrenchRepublicanCalendar> f =
            ChronoFormatter.setUp(FrenchRepublicanCalendar.axis(), Locale.FRENCH)
                .addPattern("D. MMMM', an '", PatternType.DYNAMIC)
                .addFixedInteger(FrenchRepublicanCalendar.YEAR_OF_ERA, 5)
                .or()
                .addPattern("SSSS', an '", PatternType.DYNAMIC)
                .addFixedInteger(FrenchRepublicanCalendar.YEAR_OF_ERA, 5)
                .build();
        FrenchRepublicanCalendar cal = PlainDate.of(2018, 9, 23).transform(FrenchRepublicanCalendar.axis());
        assertThat(
            f.parse("1. vendémiaire, an 00227"),
            is(cal));
    }

    @Test
    public void printMonthOrSansculottidesOnGroundLevel() {
        ChronoFormatter<FrenchRepublicanCalendar> f = create("D MMMM' an 'Y|SSSS', an 'Y");
        FrenchRepublicanCalendar cal = PlainDate.of(2018, 9, 23).transform(FrenchRepublicanCalendar.axis());
        assertThat(
            f.format(cal),
            is("1 vendémiaire an CCXXVII"));
        assertThat(
            f.format(cal.minus(CalendarDays.ONE)),
            is("jour de la révolution, an CCXXVI"));
    }

    @Test
    public void printMonthOrSansculottidesInsideOptionalSection() {
        ChronoFormatter<FrenchRepublicanCalendar> f = create("[D MMMM|SSSS]', an 'Y");
        FrenchRepublicanCalendar cal = PlainDate.of(2018, 9, 23).transform(FrenchRepublicanCalendar.axis());
        assertThat(
            f.format(cal),
            is("1 vendémiaire, an CCXXVII"));
        assertThat(
            f.format(cal.minus(CalendarDays.ONE)),
            is("jour de la révolution, an CCXXVI"));
    }

    @Test
    public void parseMonthOrSansculottides() throws ParseException {
        ChronoFormatter<FrenchRepublicanCalendar> f = create("D MMMM' an 'Y|SSSS', an 'Y");
        FrenchRepublicanCalendar cal = PlainDate.of(2018, 9, 23).transform(FrenchRepublicanCalendar.axis());
        assertThat(
            f.parse("1 vendémiaire an CCXXVII"),
            is(cal));
        assertThat(
            f.parse("jour de la révolution, an CCXXVI"),
            is(cal.minus(CalendarDays.ONE)));
    }

    @Test
    public void printWithSpecificAlgorithm() {
        ChronoFormatter<FrenchRepublicanCalendar> f = create("YYYY-MM-DD");
        FrenchRepublicanCalendar cal = PlainDate.of(2018, 9, 23).transform(FrenchRepublicanCalendar.axis());
        assertThat(
            f.format(cal),
            is("CCXXVII-01-01"));
        assertThat(
            f.with(FrenchRepublicanAlgorithm.attribute(), FrenchRepublicanAlgorithm.EQUINOX).format(cal),
            is("CCXXVII-01-01"));
        assertThat(
            f.with(FrenchRepublicanAlgorithm.attribute(), FrenchRepublicanAlgorithm.ROMME).format(cal),
            is("CCXXVII-01-02"));
    }

    @Test
    public void parseWithSpecificAlgorithm() throws ParseException {
        ChronoFormatter<FrenchRepublicanCalendar> f = create("YYYY-MM-DD");
        FrenchRepublicanCalendar cal = PlainDate.of(2018, 9, 23).transform(FrenchRepublicanCalendar.axis());
        assertThat(
            f.parse("CCXXVII-01-01"),
            is(cal));
        assertThat(
            f.with(FrenchRepublicanAlgorithm.attribute(), FrenchRepublicanAlgorithm.EQUINOX).parse("CCXXVII-01-01"),
            is(cal));
        assertThat(
            f.with(FrenchRepublicanAlgorithm.attribute(), FrenchRepublicanAlgorithm.ROMME).parse("CCXXVII-01-02"),
            is(cal));
    }

    @Test
    public void printNumbers() {
        ChronoFormatter<FrenchRepublicanCalendar> f = create("yyyy-mm-dd");
        FrenchRepublicanCalendar cal = PlainDate.of(2018, 9, 23).transform(FrenchRepublicanCalendar.axis());
        assertThat(
            f.format(cal),
            is("0227-01-01"));
    }

    @Test
    public void parseNumbers() throws ParseException {
        ChronoFormatter<FrenchRepublicanCalendar> f = create("yyyy-mm-dd");
        FrenchRepublicanCalendar cal = PlainDate.of(2018, 9, 23).transform(FrenchRepublicanCalendar.axis());
        assertThat(
            f.parse("0227-01-01"),
            is(cal));
    }

    private static ChronoFormatter<FrenchRepublicanCalendar> create(String pattern) {
        return ChronoFormatter.ofPattern(pattern, PatternType.DYNAMIC, Locale.FRENCH, FrenchRepublicanCalendar.axis());
    }

}
