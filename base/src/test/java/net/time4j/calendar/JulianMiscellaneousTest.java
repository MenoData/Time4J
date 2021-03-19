package net.time4j.calendar;

import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.Weekday;
import net.time4j.Weekmodel;
import net.time4j.engine.CalendarDate;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import net.time4j.history.HistoricDate;
import net.time4j.history.HistoricEra;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.format.FormatStyle;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class JulianMiscellaneousTest {

    @Test
    public void julianCalendarProperties() {
        JulianCalendar date = JulianCalendar.of(HistoricEra.AD, 1752, Month.FEBRUARY, 29);
        assertThat(
            date.getDayOfMonth(),
            is(29));
        assertThat(
            date.getMonth(),
            is(Month.FEBRUARY));
        assertThat(
            date.lengthOfMonth(),
            is(29));
        assertThat(
            date.atTime(12, 0).toDate(),
            is(date));
        assertThat(
            date.lengthOfYear(),
            is(366)
        );
        assertThat(
            date.get(JulianCalendar.DATE),
            is(HistoricDate.of(HistoricEra.AD, 1752, 2, 29))
        );
    }

    @Test
    public void julianCalendarBetween() {
        JulianCalendar start = JulianCalendar.of(HistoricEra.AD, 1752, Month.FEBRUARY, 28);
        JulianCalendar end = JulianCalendar.of(HistoricEra.AD, 1752, Month.APRIL, 27);
        assertThat(JulianCalendar.Unit.MONTHS.between(start, end), is(1));
        assertThat(JulianCalendar.Unit.DAYS.between(start, end), is(59));
        end = end.plus(1, JulianCalendar.Unit.YEARS);
        assertThat(JulianCalendar.Unit.YEARS.between(start, end), is(1));
    }

    @Test
    public void formatJulianCalendar() {
        ChronoFormatter<JulianCalendar> f =
            ChronoFormatter.ofStyle(FormatStyle.FULL, Locale.GERMAN, JulianCalendar.axis());
        assertThat(
            f.format(JulianCalendar.of(HistoricEra.AD, 1752, 9, 14)),
            is("Montag, 14. September 1752 n. Chr.")
        );
    }

    @Test
    public void defaultFirstDayOfWeek() {
        assertThat(JulianCalendar.DAY_OF_WEEK.getDefaultMinimum(), is(Weekday.SUNDAY));
    }

    @Test
    public void caSupport() {
        Locale locale = Locale.forLanguageTag("en-u-ca-julian");
        ChronoFormatter<CalendarDate> f = ChronoFormatter.ofGenericCalendarStyle(FormatStyle.FULL, locale);
        assertThat(
            f.format(PlainDate.of(2017, 10, 1)),
            is("Sunday, September 18, 2017 AD"));
    }

    @Test
    public void isValidIfWeekdayOutOfRange() {
        JulianCalendar min = JulianCalendar.axis().getMinimum(); // wednesday
        JulianCalendar max = JulianCalendar.axis().getMaximum(); // sunday

        assertThat(min.isValid(JulianCalendar.DAY_OF_WEEK, Weekday.TUESDAY), is(false));
        assertThat(min.isValid(JulianCalendar.DAY_OF_WEEK, Weekday.WEDNESDAY), is(true));
        assertThat(max.isValid(JulianCalendar.DAY_OF_WEEK, Weekday.MONDAY), is(false));
        assertThat(max.isValid(JulianCalendar.DAY_OF_WEEK, Weekday.SUNDAY), is(true));

        assertThat(min.getMinimum(JulianCalendar.DAY_OF_WEEK), is(Weekday.WEDNESDAY));
        assertThat(min.getMaximum(JulianCalendar.DAY_OF_WEEK), is(Weekday.SATURDAY));
        assertThat(max.getMinimum(JulianCalendar.DAY_OF_WEEK), is(Weekday.SUNDAY));
        assertThat(max.getMaximum(JulianCalendar.DAY_OF_WEEK), is(Weekday.SUNDAY));

        StdCalendarElement<Weekday, JulianCalendar> elementUS =
            CommonElements.localDayOfWeek(JulianCalendar.axis(), Weekmodel.of(Locale.US));
        assertThat(min.getMinimum(elementUS), is(Weekday.WEDNESDAY));
        assertThat(min.getMaximum(elementUS), is(Weekday.SATURDAY));
        assertThat(max.getMinimum(elementUS), is(Weekday.SUNDAY));
        assertThat(max.getMaximum(elementUS), is(Weekday.SUNDAY));

        StdCalendarElement<Weekday, JulianCalendar> elementISO =
            CommonElements.localDayOfWeek(JulianCalendar.axis(), Weekmodel.ISO);
        assertThat(min.getMinimum(elementISO), is(Weekday.WEDNESDAY));
        assertThat(min.getMaximum(elementISO), is(Weekday.SUNDAY));
        assertThat(max.getMinimum(elementISO), is(Weekday.MONDAY));
        assertThat(max.getMaximum(elementISO), is(Weekday.SUNDAY));
    }

    @Test
    public void monthFormat() {
        JulianCalendar date = JulianCalendar.of(HistoricEra.AD, 2018, 4, 29);
        ChronoFormatter<JulianCalendar> f1 =
            ChronoFormatter.ofPattern("M", PatternType.CLDR, Locale.ENGLISH, JulianCalendar.axis());
        assertThat(f1.format(date), is("4"));
        assertThat(f1.parseRaw("4").getInt(JulianCalendar.MONTH_OF_YEAR), is(4));
        ChronoFormatter<JulianCalendar> f2 =
            ChronoFormatter.ofPattern("MM", PatternType.CLDR, Locale.ENGLISH, JulianCalendar.axis());
        assertThat(f2.format(date), is("04"));
        assertThat(f2.parseRaw("04").getInt(JulianCalendar.MONTH_OF_YEAR), is(4));
        ChronoFormatter<JulianCalendar> f3 =
            ChronoFormatter.ofPattern("MMM", PatternType.CLDR, Locale.ENGLISH, JulianCalendar.axis());
        assertThat(f3.format(date), is("Apr"));
        assertThat(f3.parseRaw("Apr").getInt(JulianCalendar.MONTH_OF_YEAR), is(4));
        ChronoFormatter<JulianCalendar> f4 =
            ChronoFormatter.ofPattern("MMMM", PatternType.CLDR, Locale.ENGLISH, JulianCalendar.axis());
        assertThat(f4.format(date), is("April"));
        assertThat(f4.parseRaw("April").getInt(JulianCalendar.MONTH_OF_YEAR), is(4));
        ChronoFormatter<JulianCalendar> f5 =
            ChronoFormatter.ofPattern("MMMMM", PatternType.CLDR, Locale.ENGLISH, JulianCalendar.axis());
        assertThat(f5.format(date), is("A")); // unambivalent parsing impossible
    }

    @Test
    public void dayFormat() {
        JulianCalendar date = JulianCalendar.of(HistoricEra.AD, 2018, 4, 7);
        ChronoFormatter<JulianCalendar> f1 =
            ChronoFormatter.ofPattern("d", PatternType.CLDR, Locale.ENGLISH, JulianCalendar.axis());
        assertThat(f1.format(date), is("7"));
        assertThat(f1.parseRaw("7").getInt(JulianCalendar.DAY_OF_MONTH), is(7));
        ChronoFormatter<JulianCalendar> f2 =
            ChronoFormatter.ofPattern("dd", PatternType.CLDR, Locale.ENGLISH, JulianCalendar.axis());
        assertThat(f2.format(date), is("07"));
        assertThat(f2.parseRaw("07").getInt(JulianCalendar.DAY_OF_MONTH), is(7));
    }

}