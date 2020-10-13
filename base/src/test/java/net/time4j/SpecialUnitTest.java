package net.time4j;

import net.time4j.engine.ChronoException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.CalendarUnit.DAYS;
import static net.time4j.CalendarUnit.WEEKS;
import static net.time4j.CalendarUnit.MONTHS;
import static net.time4j.CalendarUnit.YEARS;
import static net.time4j.PlainDate.YEAR_OF_WEEKDATE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class SpecialUnitTest {

    @Test(expected=UnsupportedOperationException.class)
    public void atEndOfMonthDayBased() {
        DAYS.atEndOfMonth();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void atEndOfMonthWeekBased() {
        WEEKS.atEndOfMonth();
    }

    @Test
    public void atEndOfMonth() {
        assertThat(
            PlainDate.of(2011, 2, 28).plus(1, YEARS.atEndOfMonth()),
            is(PlainDate.of(2012, 2, 29)));
        assertThat(
            PlainDate.of(2011, 7, 1).plus(7, MONTHS.atEndOfMonth()),
            is(PlainDate.of(2012, 2, 29)));
        assertThat(
            PlainDate.of(2011, 7, 1).minus(1, MONTHS.atEndOfMonth()),
            is(PlainDate.of(2011, 6, 30)));
        assertThat(
            PlainDate.of(2011, 7, 1).until(PlainDate.of(2011, 6, 30), MONTHS.atEndOfMonth()),
            is(-1L));
        assertThat(
            PlainDate.of(2011, 6, 30).until(PlainDate.of(2011, 7, 30), MONTHS.atEndOfMonth()),
            is(0L));
        assertThat(
            PlainDate.of(2011, 6, 30).until(PlainDate.of(2011, 7, 31), MONTHS.atEndOfMonth()),
            is(1L));
    }

    @Test
    public void atEndOfMonthAsDuration() {
        assertThat(
            PlainDate.of(2011, 2, 28).plus(Duration.of(1, YEARS.atEndOfMonth())),
            is(PlainDate.of(2012, 2, 29)));
        assertThat(
            PlainDate.of(2011, 7, 1).plus(Duration.of(7, MONTHS.atEndOfMonth())),
            is(PlainDate.of(2012, 2, 29)));
        assertThat(
            PlainDate.of(2015, 2, 27).minus(Duration.of(1, MONTHS.atEndOfMonth())),
            is(PlainDate.of(2015, 1, 31)));
    }

    @Test(expected=UnsupportedOperationException.class)
    public void keepingEndOfMonthDayBased() {
        DAYS.keepingEndOfMonth();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void keepingEndOfMonthWeekBased() {
        WEEKS.keepingEndOfMonth();
    }

    @Test
    public void keepingEndOfMonth() {
        assertThat(
            PlainDate.of(2015, 2, 27).plus(2, MONTHS.keepingEndOfMonth()),
            is(PlainDate.of(2015, 4, 27)));
        assertThat(
            PlainDate.of(2015, 2, 28).plus(2, MONTHS.keepingEndOfMonth()),
            is(PlainDate.of(2015, 4, 30)));
        assertThat(
            PlainDate.of(2015, 1, 31).plus(1, MONTHS.keepingEndOfMonth()),
            is(PlainDate.of(2015, 2, 28)));
        assertThat(
            PlainDate.of(2015, 1, 30).plus(1, MONTHS.keepingEndOfMonth()),
            is(PlainDate.of(2015, 2, 28)));
        assertThat(
            PlainDate.of(2015, 2, 27).minus(1, MONTHS.keepingEndOfMonth()),
            is(PlainDate.of(2015, 1, 27)));
        assertThat(
            PlainDate.of(2015, 2, 28).minus(1, MONTHS.keepingEndOfMonth()),
            is(PlainDate.of(2015, 1, 31)));
        assertThat(
            PlainDate.of(2015, 3, 31).minus(1, MONTHS.keepingEndOfMonth()),
            is(PlainDate.of(2015, 2, 28)));
        assertThat(
            PlainDate.of(2015, 3, 30).minus(1, MONTHS.keepingEndOfMonth()),
            is(PlainDate.of(2015, 2, 28)));
        assertThat(
            PlainDate.of(2015, 3, 10).until(PlainDate.of(2015, 2, 9), MONTHS.keepingEndOfMonth()),
            is(-1L));
        assertThat(
            PlainDate.of(2015, 2, 9).until(PlainDate.of(2015, 3, 10), MONTHS.keepingEndOfMonth()),
            is(1L));
        assertThat(
            PlainDate.of(2015, 3, 10).until(PlainDate.of(2015, 2, 10), MONTHS.keepingEndOfMonth()),
            is(-1L));
        assertThat(
            PlainDate.of(2015, 2, 10).until(PlainDate.of(2015, 3, 10), MONTHS.keepingEndOfMonth()),
            is(1L));
        assertThat(
            PlainDate.of(2015, 3, 10).until(PlainDate.of(2015, 2, 11), MONTHS.keepingEndOfMonth()),
            is(0L));
        assertThat(
            PlainDate.of(2015, 2, 11).until(PlainDate.of(2015, 3, 10), MONTHS.keepingEndOfMonth()),
            is(0L));
        assertThat(
            PlainDate.of(2015, 3, 30).until(PlainDate.of(2015, 2, 28), MONTHS.keepingEndOfMonth()),
            is(-1L));
        assertThat(
            PlainDate.of(2015, 2, 28).until(PlainDate.of(2015, 3, 30), MONTHS.keepingEndOfMonth()),
            is(0L));
        assertThat(
            PlainDate.of(2015, 3, 31).until(PlainDate.of(2015, 2, 28), MONTHS.keepingEndOfMonth()),
            is(-1L));
        assertThat(
            PlainDate.of(2015, 2, 28).until(PlainDate.of(2015, 3, 31), MONTHS.keepingEndOfMonth()),
            is(1L));
    }

    @Test(expected=ChronoException.class)
    public void unlessInvalidAbort() {
        PlainDate.of(2014, 1, 29).plus(1, MONTHS.unlessInvalid());
    }

    @Test
    public void unlessInvalidOK() {
        assertThat(
            PlainDate.of(2012, 1, 29).plus(1, MONTHS.unlessInvalid()),
            is(PlainDate.of(2012, 2, 29)));
    }

    @Test
    public void nextValidDate() {
        assertThat(
            PlainDate.of(2012, 1, 29).plus(1, MONTHS.nextValidDate()),
            is(PlainDate.of(2012, 2, 29)));
        assertThat(
            PlainDate.of(2012, 1, 30).plus(1, MONTHS.nextValidDate()),
            is(PlainDate.of(2012, 3, 1)));
        assertThat(
            PlainDate.of(2012, 1, 31).plus(1, MONTHS.nextValidDate()),
            is(PlainDate.of(2012, 3, 1)));
    }

    @Test
    public void withCarryOver() {
        assertThat(
            PlainDate.of(2012, 1, 31).plus(1, MONTHS.withCarryOver()),
            is(PlainDate.of(2012, 3, 2)));
        assertThat(
            PlainDate.of(2013, 1, 28).plus(1, MONTHS.withCarryOver()),
            is(PlainDate.of(2013, 2, 28)));
        assertThat(
            PlainDate.of(2013, 1, 29).plus(1, MONTHS.withCarryOver()),
            is(PlainDate.of(2013, 3, 1)));
        assertThat(
            PlainDate.of(2013, 1, 30).plus(1, MONTHS.withCarryOver()),
            is(PlainDate.of(2013, 3, 2)));
        assertThat(
            PlainDate.of(2013, 1, 31).plus(1, MONTHS.withCarryOver()),
            is(PlainDate.of(2013, 3, 3)));
        assertThat(
            PlainDate.of(2012, 3, 31).plus(1, MONTHS.withCarryOver()),
            is(PlainDate.of(2012, 5, 1)));
    }

    @Test
    public void weekBasedYears() {
        assertThat(
            PlainDate.of(2000, 2, 29).plus(14, CalendarUnit.weekBasedYears()),
            is(PlainDate.of(2014, 2, 25)));
        IsoDateUnit unit =
            PlainDate.of(1, 1).getChronology().getBaseUnit(YEAR_OF_WEEKDATE);
        assertThat(
            (unit == CalendarUnit.weekBasedYears()),
            is(true));
    }

    @Test
    public void weekBasedYearsAsDuration() {
        Duration<IsoDateUnit> duration1 =
            Duration.of(14, CalendarUnit.weekBasedYears());
        assertThat(
            PlainDate.of(2000, 2, 29).plus(duration1),
            is(PlainDate.of(2014, 2, 25)));
        Duration<IsoDateUnit> duration2 =
            duration1.plus(4, DAYS);
        System.out.println(duration2);
        assertThat(
            PlainDate.of(2000, 2, 29).plus(duration2),
            is(PlainDate.of(2014, 3, 1)));
    }

    @Test
    public void jodaMetric() {
        PlainDate birthDate = PlainDate.of(1996, 2, 29);
        PlainDate currentDate = PlainDate.of(2014, 2, 28);
        IsoDateUnit jodaUnit = YEARS.withJodaMetric();
        Duration<IsoDateUnit> d = Duration.in(jodaUnit).between(birthDate, currentDate);
        System.out.println(d); // Output: P18{Y-JODA_METRIC}
        assertThat(d.getPartialAmount(jodaUnit), is(18L));
        assertThat(birthDate.plus(18, jodaUnit), is(currentDate));
        assertThat(birthDate.until(currentDate, jodaUnit), is(18L));
        assertThat(birthDate.until(currentDate, CalendarUnit.YEARS), is(17L));
        assertThat(CalendarUnit.YEARS.between(birthDate, currentDate), is(17L));
    }

}