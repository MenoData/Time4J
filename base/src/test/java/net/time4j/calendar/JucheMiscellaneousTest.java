package net.time4j.calendar;

import net.time4j.CalendarUnit;
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.Weekday;
import net.time4j.engine.CalendarDate;
import net.time4j.engine.CalendarDays;
import net.time4j.format.expert.ChronoFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.format.FormatStyle;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class JucheMiscellaneousTest {

    @Test
    public void plusYears() {
        assertThat(
            JucheCalendar.of(89, 2, 29).plus(2, CalendarUnit.YEARS),
            is(JucheCalendar.of(91, 2, 28)));
        assertThat(
            JucheCalendar.of(91, 1, 1).isLeapYear(),
            is(false));
    }

    @Test
    public void plusMonths() {
        assertThat(
            JucheCalendar.of(89, 1, 31).plus(5, CalendarUnit.MONTHS),
            is(JucheCalendar.of(89, 6, 30)));
    }

    @Test
    public void plusWeeks() {
        assertThat(
            JucheCalendar.of(89, 1, 30).plus(5, CalendarUnit.WEEKS),
            is(JucheCalendar.of(89, 3, 5)));
    }

    @Test
    public void plusDays() {
        assertThat(
            JucheCalendar.of(89, 2, 28).plus(4, CalendarUnit.DAYS),
            is(JucheCalendar.of(89, 3, 3)));
    }

    @Test
    public void nextMonth() {
        assertThat(
            JucheCalendar.of(89, 1, 30).with(JucheCalendar.MONTH_OF_YEAR.incremented()),
            is(JucheCalendar.of(89, 2, 29)));
    }

    @Test
    public void previousMonth() {
        assertThat(
            JucheCalendar.of(89, 3, 30).with(JucheCalendar.MONTH_OF_YEAR.decremented()),
            is(JucheCalendar.of(89, 2, 29)));
    }

    @Test
    public void nextYear() {
        assertThat(
            JucheCalendar.of(89, 2, 29).with(JucheCalendar.YEAR_OF_ERA.incremented()),
            is(JucheCalendar.of(90, 2, 28)));
    }

    @Test
    public void previousYear() {
        assertThat(
            JucheCalendar.of(89, 2, 29).with(JucheCalendar.YEAR_OF_ERA.decremented()),
            is(JucheCalendar.of(88, 2, 28)));
    }

    @Test
    public void nextDay() {
        assertThat(
            JucheCalendar.of(89, 2, 28).with(JucheCalendar.DAY_OF_YEAR.incremented()),
            is(JucheCalendar.of(89, 2, 29)));
        assertThat(
            JucheCalendar.of(89, 2, 29).with(JucheCalendar.DAY_OF_MONTH.incremented()),
            is(JucheCalendar.of(89, 3, 1)));
        assertThat(
            JucheCalendar.of(89, 3, 1).with(JucheCalendar.DAY_OF_WEEK.incremented()),
            is(JucheCalendar.of(89, 3, 2)));
    }

    @Test
    public void previousDay() {
        assertThat(
            JucheCalendar.of(89, 3, 2).with(JucheCalendar.DAY_OF_YEAR.decremented()),
            is(JucheCalendar.of(89, 3, 1)));
        assertThat(
            JucheCalendar.of(89, 3, 1).with(JucheCalendar.DAY_OF_MONTH.decremented()),
            is(JucheCalendar.of(89, 2, 29)));
        assertThat(
            JucheCalendar.of(89, 2, 29).with(JucheCalendar.DAY_OF_WEEK.decremented()),
            is(JucheCalendar.of(89, 2, 28)));
    }

    @Test
    public void maxDay() {
        assertThat(
            JucheCalendar.of(89, 2, 21).with(JucheCalendar.DAY_OF_MONTH.maximized()),
            is(JucheCalendar.of(89, 2, 29)));
        assertThat(
            JucheCalendar.of(90, 2, 21).with(JucheCalendar.DAY_OF_MONTH.maximized()),
            is(JucheCalendar.of(90, 2, 28)));
    }

    @Test
    public void minDay() {
        assertThat(
            JucheCalendar.of(89, 2, 21).with(JucheCalendar.DAY_OF_MONTH.minimized()),
            is(JucheCalendar.of(89, 2, 1)));
    }

    @Test
    public void yearAtFloor() {
        assertThat(
            JucheCalendar.of(89, 5, 2).with(JucheCalendar.YEAR_OF_ERA.atFloor()),
            is(JucheCalendar.of(89, 1, 1)));
    }

    @Test
    public void yearAtCeiling() {
        assertThat(
            JucheCalendar.of(89, 5, 2).with(JucheCalendar.YEAR_OF_ERA.atCeiling()),
            is(JucheCalendar.of(89, 12, 31)));
    }

    @Test
    public void monthAtFloor() {
        assertThat(
            JucheCalendar.of(89, 5, 2).with(JucheCalendar.MONTH_OF_YEAR.atFloor()),
            is(JucheCalendar.of(89, 5, 1)));
    }

    @Test
    public void monthAtCeiling() {
        assertThat(
            JucheCalendar.of(89, 5, 2).with(JucheCalendar.MONTH_OF_YEAR.atCeiling()),
            is(JucheCalendar.of(89, 5, 31)));
    }

    @Test
    public void jucheCalendarProperties() {
        JucheCalendar date = JucheCalendar.of(89, Month.FEBRUARY, 14);
        assertThat(
            date.getDayOfMonth(),
            is(14));
        assertThat(
            date.getMonth(),
            is(Month.FEBRUARY));
        assertThat(
            date.lengthOfMonth(),
            is(29));
        assertThat(
            date.lengthOfYear(),
            is(366));
        assertThat(
            date.atTime(12, 0).toDate(),
            is(date));
    }

    @Test
    public void defaultFirstDayOfWeek() {
        assertThat(JucheCalendar.DAY_OF_WEEK.getDefaultMinimum(), is(Weekday.MONDAY));
    }

    @Test
    public void caSupport() {
        Locale locale = Locale.forLanguageTag("en-u-ca-juche");
        ChronoFormatter<CalendarDate> f = ChronoFormatter.ofGenericCalendarStyle(FormatStyle.FULL, locale);
        assertThat(
            f.format(PlainDate.of(2017, 10, 1)),
            is("Sunday, October 1, 106 Juche"));
    }

    @Test
    public void minimum1912() {
        long expected = PlainDate.of(1912, 1, 1).getDaysSinceEpochUTC();
        assertThat(
            JucheCalendar.axis().getMinimum().getDaysSinceEpochUTC(),
            is(expected));
        assertThat(
            JucheCalendar.axis().getCalendarSystem().getMinimumSinceUTC(),
            is(expected));
        assertThat(
            JucheCalendar.of(1, 1, 1),
            is(JucheCalendar.axis().getMinimum()));
    }

    @Test(expected=ArithmeticException.class)
    public void invalidBefore1912a() {
        PlainDate.of(1911, 12, 31).transform(JucheCalendar.axis());
    }

    @Test(expected=ArithmeticException.class)
    public void invalidBefore1912b() {
        JucheCalendar.axis().getMinimum().minus(CalendarDays.ONE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void invalidBefore1912c() {
        JucheCalendar.of(0, 12, 31);
    }

    @Test
    public void forJuche() {
        EastAsianYear eay = EastAsianYear.forJuche(2);
        assertThat(eay.getCycle(), is(76));
        assertThat(eay.getYearOfCycle().getNumber(), is(50));
        assertThat(eay.getElapsedCyclicYears(), is(4634 - 85));
        CyclicYear cy = eay.getYearOfCycle();
        assertThat(cy.inCycle(eay.getCycle()).getElapsedCyclicYears(), is(eay.getElapsedCyclicYears()));
    }

}