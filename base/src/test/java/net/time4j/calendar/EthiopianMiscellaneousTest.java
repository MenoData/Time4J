package net.time4j.calendar;

import net.time4j.Weekday;
import net.time4j.engine.CalendarDays;
import net.time4j.format.Attributes;
import net.time4j.format.Leniency;
import net.time4j.format.NumberSystem;
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
public class EthiopianMiscellaneousTest {

    @Test
    public void ethiopicNumeralFormat() throws ParseException {
        Locale amharic = new Locale("am");
        ChronoFormatter<EthiopianCalendar> formatter =
            ChronoFormatter.setUp(EthiopianCalendar.class, amharic)
                .addPattern("MMMM d ", PatternType.CLDR_DATE)
                .startSection(Attributes.NUMBER_SYSTEM, NumberSystem.ETHIOPIC)
                .addInteger(EthiopianCalendar.YEAR_OF_ERA, 1, 9)
                .endSection()
                .addLiteral(" (")
                .addText(EthiopianCalendar.EVANGELIST)
                .addPattern(") G", PatternType.CLDR_DATE)
                .build()
                .with(Leniency.STRICT);
        String input = "ጥቅምት 11 ፲፱፻፺፯ (ማቴዎስ) ዓ/ም";
        EthiopianCalendar ethio = formatter.parse(input);
        assertThat(
            ethio,
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1997, 2, 11)));

        // roundtrip test
        String output = formatter.format(ethio);
        assertThat(output, is(input));

        // test of default number system for years
        ChronoFormatter<EthiopianCalendar> f2 =
            ChronoFormatter.setUp(EthiopianCalendar.class, amharic)
                .addPattern("MMMM d yyyy G", PatternType.CLDR_DATE).build();
        assertThat(f2.parse("ጥቅምት 11 ፲፱፻፺፯ ዓ/ም"), is(ethio));
    }

    @Test
    public void ethiopianCalendarProperties() {
        EthiopianCalendar date = EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 2008, EthiopianMonth.YEKATIT, 9);
        assertThat(
            date.getEra(),
            is(EthiopianEra.AMETE_MIHRET));
        assertThat(
            date.getDayOfMonth(),
            is(9));
        assertThat(
            date.getMonth(),
            is(EthiopianMonth.YEKATIT));
        assertThat(
            date.lengthOfMonth(),
            is(30));
        assertThat(
            date.lengthOfYear(),
            is(365)
        );
    }

    @Test
    public void ethiopianCalendarBetween() {
        EthiopianCalendar start = EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 2007, EthiopianMonth.YEKATIT, 6);
        EthiopianCalendar end = EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 2007, EthiopianMonth.PAGUMEN, 6);
        assertThat(EthiopianCalendar.Unit.MONTHS.between(start, end), is(7));
        start = start.plus(CalendarDays.ONE);
        assertThat(EthiopianCalendar.Unit.MONTHS.between(start, end), is(6));
        start = start.minus(3, EthiopianCalendar.Unit.YEARS);
        assertThat(EthiopianCalendar.Unit.YEARS.between(start, end), is(3));
        end = end.with(EthiopianCalendar.MONTH_OF_YEAR, EthiopianMonth.YEKATIT);
        assertThat(EthiopianCalendar.Unit.YEARS.between(start, end), is(2));
    }

    @Test
    public void defaultFirstDayOfWeek() {
        assertThat(EthiopianCalendar.DAY_OF_WEEK.getDefaultMinimum(), is(Weekday.SUNDAY));
    }

    @Test
    public void weekdayInMonth() {
        EthiopianCalendar cal = EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 2007, EthiopianMonth.PAGUMEN, 6);
        System.out.println(cal.getDayOfWeek());
        assertThat(cal.getInt(EthiopianCalendar.WEEKDAY_IN_MONTH), is(1));
        assertThat(cal.getMaximum(EthiopianCalendar.WEEKDAY_IN_MONTH), is(1));
        assertThat(
            cal.with(EthiopianCalendar.WEEKDAY_IN_MONTH.setTo(3, Weekday.MONDAY)),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 2008, 1, 10)));
    }

}