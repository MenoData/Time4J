package net.time4j;

import java.text.ParseException;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static net.time4j.CalendarUnit.DAYS;
import static net.time4j.CalendarUnit.MONTHS;
import static net.time4j.CalendarUnit.YEARS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class DurationArithmeticTest {

    @Parameters(name= "{index}: [start={0}-{1}-{2}/end={3}-{4}-{5}] => {6}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
            new Object[][] {
                {2012, 2, 29, 2016, 2, 29, "P4Y"},
                {2012, 2, 29, 2016, 2, 29, "P3Y12M"},
                {2012, 2, 29, 2015, 2, 28, "P2Y11M30D"},
                {2015, 2, 28, 2016, 2, 28, "P12M"},
                {2012, 6, 30, 2014, 7, 31, "P2Y1M1D"},
                {2012, 5, 31, 2012, 7, 1, "P1M1D"},
                {2014, 7, 31, 2012, 6, 30, "-P2Y1M1D"},
                {2012, 6, 28, 2014, 7, 31, "P2Y1M3D"},
                {2012, 6, 28, 2014, 7, 30, "P2Y1M2D"},
                {2012, 2, 29, 2015, 2, 28, "P2Y11M30D"},
                {2011, 2, 27, 2016, 3, 1, "P5Y3D"},
                {2011, 1, 1, 2011, 12, 31, "P11M30D"},
                {2012, 1, 1, 2012, 12, 31, "P11M30D"},
                {2011, 1, 1, 2011, 1, 1, "P0D"},
                {2012, 6, 4, 2012, 5, 31, "-P4D"},
                {2010, 4, 29, 2011, 5, 31, "P1Y1M2D"},
                {2004, 2, 29, 2008, 2, 28, "P3Y11M30D"},
                {2003, 2, 27, 2008, 2, 28, "P5Y1D"},
                {2003, 2, 27, 2008, 2, 27, "P5Y"},
                {2003, 2, 27, 2008, 2, 26, "P4Y11M30D"},
                {-999999999, 1, 1, 999999999, 12, 31, "P1999999998Y11M30D"},
                {999999999, 12, 31, -999999999, 1, 1, "-P1999999998Y11M30D"}
            }
        );
    }

    private PlainDate start;
    private PlainDate end;
    private Duration<CalendarUnit> duration;

    public DurationArithmeticTest(
        int startYear,
        int startMonth,
        int startDay,
        int endYear,
        int endMonth,
        int endDay,
        String period
    ) throws ParseException {
        super();

        this.start = PlainDate.of(startYear, startMonth, startDay);
        this.end = PlainDate.of(endYear, endMonth, endDay);
        this.duration = Duration.parseCalendarPeriod(period);
    }

    @Test
    public void plusDuration() {
        assertThat(
            this.start.plus(this.duration),
            is(this.end));
    }

    @Test
    public void minusDuration() {
        assertThat(
            this.start.minus(this.duration.inverse()),
            is(this.end));
    }

    @Test
    public void between() {
        assertThat(
            this.start.until(this.end, Duration.inYearsMonthsDays()),
            is(this.duration.with(Duration.STD_CALENDAR_PERIOD)));
    }

    @Test
    public void assertMonthsDaysInvariant_T1_Plus_T1UntilT2_Eq_T2() {
        Duration<CalendarUnit> period =
            this.start.until(this.end, Duration.in(MONTHS, DAYS));
        assertThat(this.start.plus(period), is(this.end));
    }

    @Test
    public void assertMonthsDaysInvariant_T1UntilT2_Eq_T2UntilT1Inv() {
        Duration<CalendarUnit> p1 =
            this.start.until(this.end, Duration.in(MONTHS, DAYS));
        Duration<CalendarUnit> p2 =
            this.end.until(this.start, Duration.in(MONTHS, DAYS));
        assertThat(p1, is(p2.inverse()));
    }

    @Test
    public void assertMonthsDaysWeakInvariant_T2_Minus_T1UntilT2_Eq_T1() {
        if (this.start.getDayOfMonth() <= 28) {
            Duration<CalendarUnit> period =
                this.start.until(this.end, Duration.in(MONTHS, DAYS));
            assertThat(this.end.minus(period), is(this.start));
        }
    }

    @Test
    public void assertYearsMonthsDaysInvariant_T1_Plus_T1UntilT2_Eq_T2() {
        Duration<CalendarUnit> period =
            this.start.until(this.end, Duration.in(YEARS, MONTHS, DAYS));
        assertThat(this.start.plus(period), is(this.end));
    }

    @Test
    public void assertYearsMonthsDaysInvariant_T1UntilT2_Eq_T2UntilT1Inv() {
        Duration<CalendarUnit> p1 =
            this.start.until(this.end, Duration.in(YEARS, MONTHS, DAYS));
        Duration<CalendarUnit> p2 =
            this.end.until(this.start, Duration.in(YEARS, MONTHS, DAYS));
        assertThat(p1, is(p2.inverse()));
    }

    @Test
    public void assertYearsMonthsDaysWeakInvariant_T2_Minus_T1UntilT2_Eq_T1() {
        if (this.start.getDayOfMonth() <= 28) {
            Duration<CalendarUnit> period =
                this.start.until(this.end, Duration.in(YEARS, MONTHS, DAYS));
            assertThat(this.end.minus(period), is(this.start));
        }
    }

}
