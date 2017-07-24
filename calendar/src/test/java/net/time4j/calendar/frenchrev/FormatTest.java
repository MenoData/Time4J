package net.time4j.calendar.frenchrev;

import net.time4j.PlainDate;
import net.time4j.engine.CalendarDays;
import net.time4j.format.Attributes;
import net.time4j.format.NumberSystem;
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
public class FormatTest {

    @Test
    public void printMonthOrSansculottidesOnGroundLevel() {
        ChronoFormatter<FrenchRepublicanCalendar> f = create("D MMMM' an 'Y|SSSS', an 'Y");
        FrenchRepublicanCalendar cal = PlainDate.of(2018, 9, 23).transform(FrenchRepublicanCalendar.axis());
        assertThat(
            f.format(cal),
            is("1 vendémiaire an 227"));
        assertThat(
            f.format(cal.minus(CalendarDays.ONE)),
            is("jour de la révolution, an 226"));
    }

    @Test
    public void printMonthOrSansculottidesInsideOptionalSection() {
        ChronoFormatter<FrenchRepublicanCalendar> f = create("[D MMMM|SSSS]', an 'Y");
        FrenchRepublicanCalendar cal = PlainDate.of(2018, 9, 23).transform(FrenchRepublicanCalendar.axis());
        assertThat(
            f.format(cal),
            is("1 vendémiaire, an 227"));
        assertThat(
            f.format(cal.minus(CalendarDays.ONE)),
            is("jour de la révolution, an 226"));
    }

    @Test
    public void parseMonthOrSansculottides() throws ParseException {
        ChronoFormatter<FrenchRepublicanCalendar> f = create("D MMMM' an 'Y|SSSS', an 'Y");
        FrenchRepublicanCalendar cal = PlainDate.of(2018, 9, 23).transform(FrenchRepublicanCalendar.axis());
        assertThat(
            f.parse("1 vendémiaire an 227"),
            is(cal));
        assertThat(
            f.parse("jour de la révolution, an 226"),
            is(cal.minus(CalendarDays.ONE)));
    }

    @Test
    public void printWithSpecificAlgorithm() {
        ChronoFormatter<FrenchRepublicanCalendar> f = create("YYYY-MM-DD");
        FrenchRepublicanCalendar cal = PlainDate.of(2018, 9, 23).transform(FrenchRepublicanCalendar.axis());
        assertThat(
            f.format(cal),
            is("0227-01-01"));
        assertThat(
            f.with(FrenchRepublicanAlgorithm.attribute(), FrenchRepublicanAlgorithm.EQUINOX).format(cal),
            is("0227-01-01"));
        assertThat(
            f.with(FrenchRepublicanAlgorithm.attribute(), FrenchRepublicanAlgorithm.ROMME).format(cal),
            is("0227-01-02"));
    }

    @Test
    public void parseWithSpecificAlgorithm() throws ParseException {
        ChronoFormatter<FrenchRepublicanCalendar> f = create("YYYY-MM-DD");
        FrenchRepublicanCalendar cal = PlainDate.of(2018, 9, 23).transform(FrenchRepublicanCalendar.axis());
        assertThat(
            f.parse("0227-01-01"),
            is(cal));
        assertThat(
            f.with(FrenchRepublicanAlgorithm.attribute(), FrenchRepublicanAlgorithm.EQUINOX).parse("0227-01-01"),
            is(cal));
        assertThat(
            f.with(FrenchRepublicanAlgorithm.attribute(), FrenchRepublicanAlgorithm.ROMME).parse("0227-01-02"),
            is(cal));
    }

    private static ChronoFormatter<FrenchRepublicanCalendar> create(String pattern) {
        return ChronoFormatter
                .setUp(FrenchRepublicanCalendar.axis(), Locale.FRENCH)
                .startSection(Attributes.NUMBER_SYSTEM, NumberSystem.ARABIC)
                .addPattern(pattern, PatternType.DYNAMIC)
                .endSection()
                .build();
    }

}
