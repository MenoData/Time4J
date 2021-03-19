package net.time4j.calendar;

import net.time4j.CalendarUnit;
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.Weekday;
import net.time4j.engine.CalendarDate;
import net.time4j.engine.CalendarSystem;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.format.FormatStyle;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class MinguoMiscellaneousTest {

    @Test
    public void plusYears() {
        assertThat(
            MinguoCalendar.of(MinguoEra.ROC, 89, 2, 29).plus(2, CalendarUnit.YEARS),
            is(MinguoCalendar.of(MinguoEra.ROC, 91, 2, 28)));
        assertThat(
            MinguoCalendar.of(MinguoEra.ROC, 91, 1, 1).isLeapYear(),
            is(false));
    }

    @Test
    public void plusMonths() {
        assertThat(
            MinguoCalendar.of(MinguoEra.ROC, 89, 1, 31).plus(5, CalendarUnit.MONTHS),
            is(MinguoCalendar.of(MinguoEra.ROC, 89, 6, 30)));
    }

    @Test
    public void plusWeeks() {
        assertThat(
            MinguoCalendar.of(MinguoEra.ROC, 89, 1, 30).plus(5, CalendarUnit.WEEKS),
            is(MinguoCalendar.of(MinguoEra.ROC, 89, 3, 5)));
    }

    @Test
    public void plusDays() {
        assertThat(
            MinguoCalendar.of(MinguoEra.ROC, 89, 2, 28).plus(4, CalendarUnit.DAYS),
            is(MinguoCalendar.of(MinguoEra.ROC, 89, 3, 3)));
    }

    @Test
    public void nextMonth() {
        assertThat(
            MinguoCalendar.of(MinguoEra.ROC, 89, 1, 30).with(MinguoCalendar.MONTH_OF_YEAR.incremented()),
            is(MinguoCalendar.of(MinguoEra.ROC, 89, 2, 29)));
    }

    @Test
    public void previousMonth() {
        assertThat(
            MinguoCalendar.of(MinguoEra.ROC, 89, 3, 30).with(MinguoCalendar.MONTH_OF_YEAR.decremented()),
            is(MinguoCalendar.of(MinguoEra.ROC, 89, 2, 29)));
    }

    @Test
    public void nextYear() {
        assertThat(
            MinguoCalendar.of(MinguoEra.ROC, 89, 2, 29).with(MinguoCalendar.YEAR_OF_ERA.incremented()),
            is(MinguoCalendar.of(MinguoEra.ROC, 90, 2, 28)));
    }

    @Test
    public void previousYear() {
        assertThat(
            MinguoCalendar.of(MinguoEra.ROC, 89, 2, 29).with(MinguoCalendar.YEAR_OF_ERA.decremented()),
            is(MinguoCalendar.of(MinguoEra.ROC, 88, 2, 28)));
    }

    @Test
    public void nextDay() {
        assertThat(
            MinguoCalendar.of(MinguoEra.ROC, 89, 2, 28).with(MinguoCalendar.DAY_OF_YEAR.incremented()),
            is(MinguoCalendar.of(MinguoEra.ROC, 89, 2, 29)));
        assertThat(
            MinguoCalendar.of(MinguoEra.ROC, 89, 2, 29).with(MinguoCalendar.DAY_OF_MONTH.incremented()),
            is(MinguoCalendar.of(MinguoEra.ROC, 89, 3, 1)));
        assertThat(
            MinguoCalendar.of(MinguoEra.ROC, 89, 3, 1).with(MinguoCalendar.DAY_OF_WEEK.incremented()),
            is(MinguoCalendar.of(MinguoEra.ROC, 89, 3, 2)));
    }

    @Test
    public void previousDay() {
        assertThat(
            MinguoCalendar.of(MinguoEra.ROC, 89, 3, 2).with(MinguoCalendar.DAY_OF_YEAR.decremented()),
            is(MinguoCalendar.of(MinguoEra.ROC, 89, 3, 1)));
        assertThat(
            MinguoCalendar.of(MinguoEra.ROC, 89, 3, 1).with(MinguoCalendar.DAY_OF_MONTH.decremented()),
            is(MinguoCalendar.of(MinguoEra.ROC, 89, 2, 29)));
        assertThat(
            MinguoCalendar.of(MinguoEra.ROC, 89, 2, 29).with(MinguoCalendar.DAY_OF_WEEK.decremented()),
            is(MinguoCalendar.of(MinguoEra.ROC, 89, 2, 28)));
    }

    @Test
    public void maxDay() {
        assertThat(
            MinguoCalendar.of(MinguoEra.ROC, 89, 2, 21).with(MinguoCalendar.DAY_OF_MONTH.maximized()),
            is(MinguoCalendar.of(MinguoEra.ROC, 89, 2, 29)));
        assertThat(
            MinguoCalendar.of(MinguoEra.ROC, 90, 2, 21).with(MinguoCalendar.DAY_OF_MONTH.maximized()),
            is(MinguoCalendar.of(MinguoEra.ROC, 90, 2, 28)));
    }

    @Test
    public void minDay() {
        assertThat(
            MinguoCalendar.of(MinguoEra.ROC, 89, 2, 21).with(MinguoCalendar.DAY_OF_MONTH.minimized()),
            is(MinguoCalendar.of(MinguoEra.ROC, 89, 2, 1)));
    }

    @Test
    public void yearAtFloor() {
        assertThat(
            MinguoCalendar.of(MinguoEra.ROC, 89, 5, 2).with(MinguoCalendar.YEAR_OF_ERA.atFloor()),
            is(MinguoCalendar.of(MinguoEra.ROC, 89, 1, 1)));
    }

    @Test
    public void yearAtCeiling() {
        assertThat(
            MinguoCalendar.of(MinguoEra.ROC, 89, 5, 2).with(MinguoCalendar.YEAR_OF_ERA.atCeiling()),
            is(MinguoCalendar.of(MinguoEra.ROC, 89, 12, 31)));
    }

    @Test
    public void monthAtFloor() {
        assertThat(
            MinguoCalendar.of(MinguoEra.ROC, 89, 5, 2).with(MinguoCalendar.MONTH_OF_YEAR.atFloor()),
            is(MinguoCalendar.of(MinguoEra.ROC, 89, 5, 1)));
    }

    @Test
    public void monthAtCeiling() {
        assertThat(
            MinguoCalendar.of(MinguoEra.ROC, 89, 5, 2).with(MinguoCalendar.MONTH_OF_YEAR.atCeiling()),
            is(MinguoCalendar.of(MinguoEra.ROC, 89, 5, 31)));
    }

    @Test
    public void minguoCalendarProperties() {
        MinguoCalendar date = MinguoCalendar.of(MinguoEra.ROC, 89, Month.FEBRUARY, 14);
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
        assertThat(MinguoCalendar.DAY_OF_WEEK.getDefaultMinimum(), is(Weekday.SUNDAY));
    }

    @Test
    public void caSupport() {
        Locale locale = Locale.forLanguageTag("en-u-ca-roc");
        ChronoFormatter<CalendarDate> f = ChronoFormatter.ofGenericCalendarStyle(FormatStyle.FULL, locale);
        assertThat(
            f.format(PlainDate.of(2017, 10, 1)),
            is("Sunday, October 1, 106 Minguo"));
    }

    @Test
    public void printMinimum() {
        CalendarSystem<MinguoCalendar> calsys = MinguoCalendar.axis().getCalendarSystem();
        MinguoCalendar minDate = calsys.transform(calsys.getMinimumSinceUTC());
        assertThat(minDate, is(MinguoCalendar.axis().getMinimum()));
        ChronoFormatter<MinguoCalendar> f =
            ChronoFormatter.ofPattern("yyyy", PatternType.CLDR, Locale.ENGLISH, MinguoCalendar.axis());
        assertThat(f.format(minDate), is("1000001911"));
    }

}