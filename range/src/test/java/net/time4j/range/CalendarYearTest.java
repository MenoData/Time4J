package net.time4j.range;

import net.time4j.PlainDate;
import net.time4j.SystemClock;
import net.time4j.ZonalClock;
import net.time4j.format.DisplayMode;
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
public class CalendarYearTest {

    @Test
    public void getValue() {
        assertThat(CalendarYear.of(2011).getValue(), is(2011));
    }

    @Test
    public void getStart() {
        assertThat(CalendarYear.of(2011).getStart(), is(Boundary.ofClosed(PlainDate.of(2011, 1, 1))));
    }

    @Test
    public void getEnd() {
        assertThat(CalendarYear.of(2011).getEnd(), is(Boundary.ofClosed(PlainDate.of(2011, 12, 31))));
    }

    @Test
    public void compareTo() {
        assertThat(CalendarYear.of(2012).compareTo(CalendarYear.of(2014)) < 0, is(true));
        assertThat(CalendarYear.of(2012).compareTo(CalendarYear.of(2011)) > 0, is(true));
        assertThat(CalendarYear.of(2012).compareTo(CalendarYear.of(2012)) == 0, is(true));
    }

    @Test
    public void atDay() {
        CalendarYear cy = CalendarYear.of(2016);
        assertThat(
            cy.atDayOfYear(1) == cy.getStart().getTemporal(),
            is(true));
        assertThat(
            cy.atDayOfYear(366),
            is(PlainDate.of(2016, 12, 31)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void atDayOutOfRange() {
        CalendarYear cy = CalendarYear.of(2015);
        cy.atDayOfYear(366);
    }

    @Test
    public void currentYear() {
        ZonalClock clock = SystemClock.inLocalView();
        CalendarYear cy = clock.now(CalendarYear.chronology());
        assertThat(cy.getValue(), is(clock.today().getYear()));
    }

    @Test
    public void iterator() {
        int count = 1;
        for (PlainDate date : CalendarYear.of(2016)) {
            System.out.println(count + " => " + date);
            count++;
        }
        assertThat(count - 1, is(366));
    }

    @Test
    public void length() {
        assertThat(CalendarYear.of(2017).length(), is(365));
        assertThat(CalendarYear.of(2016).length(), is(366));
    }

    @Test
    public void isLeap() {
        assertThat(CalendarYear.of(2017).isLeap(), is(false));
        assertThat(CalendarYear.of(2016).isLeap(), is(true));
    }

    @Test
    public void isFinite() {
        assertThat(CalendarYear.of(2016).isFinite(), is(true));
    }

    @Test
    public void isEmpty() {
        assertThat(CalendarYear.of(2016).isEmpty(), is(false));
    }

    @Test
    public void contains() {
        assertThat(CalendarYear.of(2016).contains(CalendarYear.YEAR), is(true));

        assertThat(CalendarYear.of(2016).contains(PlainDate.of(2015, 12, 31)), is(false));
        assertThat(CalendarYear.of(2016).contains(PlainDate.of(2016, 1, 1)), is(true));
        assertThat(CalendarYear.of(2016).contains(PlainDate.of(2016, 5, 31)), is(true));
        assertThat(CalendarYear.of(2016).contains(PlainDate.of(2016, 12, 31)), is(true));
        assertThat(CalendarYear.of(2016).contains(PlainDate.of(2017, 1, 1)), is(false));
    }

    @Test
    public void isAfter() {
        assertThat(CalendarYear.of(2016).isAfter(PlainDate.of(2016, 1, 1)), is(false));
        assertThat(CalendarYear.of(2016).isAfter(PlainDate.of(2015, 12, 31)), is(true));

        assertThat(CalendarYear.of(2016).isAfter(CalendarYear.of(2015)), is(true));
        assertThat(CalendarYear.of(2016).isAfter(CalendarYear.of(2016)), is(false));
    }

    @Test
    public void isBefore() {
        assertThat(CalendarYear.of(2016).isBefore(PlainDate.of(2017, 1, 1)), is(true));
        assertThat(CalendarYear.of(2016).isBefore(PlainDate.of(2016, 12, 31)), is(false));

        assertThat(CalendarYear.of(2016).isBefore(CalendarYear.of(2017)), is(true));
        assertThat(CalendarYear.of(2016).isBefore(CalendarYear.of(2016)), is(false));
    }

    @Test
    public void isSimultaneous() {
        assertThat(CalendarYear.of(2016).isSimultaneous(CalendarYear.of(2017)), is(false));
        assertThat(CalendarYear.of(2016).isSimultaneous(CalendarYear.of(2016)), is(true));
    }

    @Test
    public void plus() {
        assertThat(CalendarYear.of(2012).plus(Years.ofGregorian(4)), is(CalendarYear.of(2016)));
    }

    @Test
    public void minus() {
        assertThat(CalendarYear.of(2012).minus(Years.ofGregorian(4)), is(CalendarYear.of(2008)));
    }

    @Test
    public void format() {
        CalendarYear cy = CalendarYear.of(2016);
        assertThat(
            ChronoFormatter.ofPattern("yyyy", PatternType.CLDR, Locale.ROOT, CalendarYear.chronology()).format(cy),
            is("2016"));
    }

    @Test
    public void parse() throws ParseException {
        CalendarYear expected = CalendarYear.of(2016);
        assertThat(
            ChronoFormatter.ofPattern("yyyy", PatternType.CLDR, Locale.ROOT, CalendarYear.chronology()).parse("2016"),
            is(expected));
    }

    @Test
    public void pattern() {
        assertThat(
            CalendarYear.chronology().getFormatPattern(DisplayMode.FULL, Locale.ROOT),
            is("uuuu"));
    }

}