package net.time4j.range;

import net.time4j.Month;
import net.time4j.Quarter;
import net.time4j.calendar.PersianCalendar;
import net.time4j.calendar.PersianMonth;
import net.time4j.engine.CalendarDays;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class CalendarPeriodTest {

    @Test
    public void findIntersection() {
        PersianCalendar start = PersianCalendar.of(1392, PersianMonth.ESFAND, 27);
        PersianCalendar end = PersianCalendar.of(1393, PersianMonth.FARVARDIN, 6);

        CalendarPeriod<PersianCalendar> interval =
            CalendarPeriod.on(PersianCalendar.axis()).between(start, end);
        CalendarPeriod<PersianCalendar> adjusted =
            CalendarPeriod.on(PersianCalendar.axis()).between(
                end.minus(CalendarDays.ONE),
                end.plus(CalendarDays.ONE));
        CalendarPeriod<PersianCalendar> expected =
            CalendarPeriod.on(PersianCalendar.axis()).between(end.minus(CalendarDays.ONE), end);

        assertThat(interval.findIntersection(adjusted).isPresent(), is(true));
        assertThat(interval.findIntersection(adjusted).get(), is(expected));
    }

    @Test
    public void delta() {
        CalendarPeriod<CalendarMonth> interval =
            CalendarPeriod.between(
                CalendarMonth.of(2017, Month.DECEMBER),
                CalendarMonth.of(2019, Month.FEBRUARY));
        assertThat(interval.delta(), is(14L));
    }

    @Test
    public void random() {
        CalendarPeriod<CalendarQuarter> interval =
            CalendarPeriod.between(
                CalendarQuarter.of(2017, Quarter.Q4),
                CalendarQuarter.of(2019, Quarter.Q2));
        for (int i = 0; i < 10; i++) {
            assertThat(interval.contains(interval.random()), is(true));
        }
    }

    @Test
    public void stream() {
        CalendarPeriod<CalendarWeek> interval =
            CalendarPeriod.between(
                CalendarWeek.of(2017, 52),
                CalendarWeek.of(2018, 3));
        List<CalendarWeek> expected =
            Arrays.asList(
                CalendarWeek.of(2017, 52),
                CalendarWeek.of(2018, 1),
                CalendarWeek.of(2018, 2),
                CalendarWeek.of(2018, 3)
            );
        assertThat(interval.stream().collect(Collectors.toList()), is(expected));
    }

    @Test
    public void print() {
        CalendarPeriod<CalendarYear> interval =
            CalendarPeriod.between(
                CalendarYear.of(2017),
                CalendarYear.of(2020));
        ChronoFormatter<CalendarYear> f =
            ChronoFormatter.ofPattern("uuuu", PatternType.CLDR, Locale.ENGLISH, CalendarYear.chronology());
        assertThat(interval.print(f), is("2017 – 2020"));
    }

    @Test
    public void onYears() throws ParseException {
        CalendarPeriod<CalendarYear> expected =
            CalendarPeriod.between(
                CalendarYear.of(2017),
                CalendarYear.of(2020));
        ChronoFormatter<CalendarYear> f =
            ChronoFormatter.ofPattern("uuuu", PatternType.CLDR, Locale.ENGLISH, CalendarYear.chronology());
        assertThat(CalendarPeriod.onYears().parse("2017 – 2020", f), is(expected));
    }

    @Test
    public void onQuarters() throws ParseException {
        CalendarPeriod<CalendarQuarter> expected =
            CalendarPeriod.between(
                CalendarQuarter.of(2017, Quarter.Q3),
                CalendarQuarter.of(2020, Quarter.Q2));
        ChronoFormatter<CalendarQuarter> f =
            ChronoFormatter.ofPattern("QQQ/uuuu", PatternType.CLDR, Locale.ENGLISH, CalendarQuarter.chronology());
        assertThat(CalendarPeriod.onQuarters().parse("Q3/2017 – Q2/2020", f), is(expected));
    }

    @Test
    public void onMonths() throws ParseException {
        CalendarPeriod<CalendarMonth> expected =
            CalendarPeriod.between(
                CalendarMonth.of(2017, 11),
                CalendarMonth.of(2020, 2));
        ChronoFormatter<CalendarMonth> f =
            ChronoFormatter.ofPattern("MM/uuuu", PatternType.CLDR, Locale.ENGLISH, CalendarMonth.chronology());
        assertThat(CalendarPeriod.onMonths().parse("11/2017 – 02/2020", f), is(expected));
    }

    @Test
    public void onWeeks() throws ParseException {
        CalendarPeriod<CalendarWeek> expected =
            CalendarPeriod.between(
                CalendarWeek.of(2017, 52),
                CalendarWeek.of(2020, 4));
        ChronoFormatter<CalendarWeek> f =
            ChronoFormatter.ofPattern( // use root locale for getting the week of year as ISO-week
                "w. 'week of' YYYY", PatternType.CLDR, Locale.ROOT, CalendarWeek.chronology());
        assertThat(CalendarPeriod.onWeeks().parse("52. week of 2017 – 4. week of 2020", f), is(expected));
    }

}
