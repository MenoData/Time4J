package net.time4j.range;

import net.time4j.CalendarUnit;
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.SystemClock;
import net.time4j.ZonalClock;
import net.time4j.base.GregorianDate;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.time.YearMonth;
import java.time.format.FormatStyle;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class CalendarMonthTest {

    @Test
    public void fromGregorianDate() {
        assertThat(
            CalendarMonth.from(PlainDate.of(2016, 2, 29)),
            is(CalendarMonth.of(2016, 2)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void fromGregorianDateInvalid() {
        CalendarMonth.from(
            new GregorianDate() {
                @Override
                public int getYear() {
                    return 2015;
                }
                @Override
                public int getMonth() {
                    return 2;
                }
                @Override
                public int getDayOfMonth() {
                    return 29;
                }
            }
        );
    }

    @Test
    public void getYear() {
        assertThat(CalendarMonth.of(2011, Month.FEBRUARY).getYear(), is(2011));
    }

    @Test
    public void getMonth() {
        assertThat(CalendarMonth.of(2011, Month.FEBRUARY).getMonth(), is(Month.FEBRUARY));
    }

    @Test
    public void atEndOfMonth() {
        assertThat(CalendarMonth.of(2012, 2).atEndOfMonth(), is(PlainDate.of(2012, 2, 29)));
    }

    @Test
    public void getStart() {
        assertThat(CalendarMonth.of(2011, Month.FEBRUARY).getStart(), is(Boundary.ofClosed(PlainDate.of(2011, 2, 1))));
    }

    @Test
    public void getEnd() {
        assertThat(CalendarMonth.of(2011, Month.FEBRUARY).getEnd(), is(Boundary.ofClosed(PlainDate.of(2011, 2, 28))));
    }

    @Test
    public void compareTo() {
        assertThat(
            CalendarMonth.of(2012, Month.FEBRUARY).compareTo(CalendarMonth.of(2012, Month.MARCH)) < 0, is(true));
        assertThat(
            CalendarMonth.of(2012, Month.FEBRUARY).compareTo(CalendarMonth.of(2012, Month.JANUARY)) > 0, is(true));
        assertThat(
            CalendarMonth.of(2012, Month.FEBRUARY).compareTo(CalendarMonth.of(2012, Month.FEBRUARY)) == 0, is(true));
    }

    @Test
    public void atDay() {
        CalendarMonth cm = CalendarMonth.of(2012, Month.FEBRUARY);
        assertThat(
            cm.atDayOfMonth(1) == cm.getStart().getTemporal(),
            is(true));
        assertThat(
            cm.atDayOfMonth(29),
            is(PlainDate.of(2012, 2, 29)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void atDayOutOfRange() {
        CalendarMonth.of(2013, Month.FEBRUARY).atDayOfMonth(29);
    }

    @Test
    public void currentValue() {
        ZonalClock clock = SystemClock.inLocalView();
        CalendarMonth cm = clock.now(CalendarMonth.chronology());
        assertThat(cm.getYear(), is(clock.today().getYear()));
        assertThat(cm.getMonth(), is(clock.today().get(PlainDate.MONTH_OF_YEAR)));
    }

    @Test
    public void iterator() {
        int count = 1;
        for (PlainDate date : CalendarMonth.of(2016, Month.FEBRUARY)) {
            System.out.println(count + " => " + date);
            count++;
        }
        assertThat(count - 1, is(29));
    }

    @Test
    public void length() {
        assertThat(CalendarMonth.of(2015, Month.JANUARY).length(), is(31));
        assertThat(CalendarMonth.of(2016, Month.FEBRUARY).length(), is(29));
        assertThat(CalendarMonth.of(2017, Month.FEBRUARY).length(), is(28));
        assertThat(CalendarMonth.of(2015, Month.MARCH).length(), is(31));
        assertThat(CalendarMonth.of(2015, Month.APRIL).length(), is(30));
        assertThat(CalendarMonth.of(2015, Month.MAY).length(), is(31));
        assertThat(CalendarMonth.of(2015, Month.JUNE).length(), is(30));
        assertThat(CalendarMonth.of(2015, Month.JULY).length(), is(31));
        assertThat(CalendarMonth.of(2015, Month.AUGUST).length(), is(31));
        assertThat(CalendarMonth.of(2015, Month.SEPTEMBER).length(), is(30));
        assertThat(CalendarMonth.of(2015, Month.OCTOBER).length(), is(31));
        assertThat(CalendarMonth.of(2015, Month.NOVEMBER).length(), is(30));
        assertThat(CalendarMonth.of(2015, Month.DECEMBER).length(), is(31));
    }

    @Test
    public void contains() {
        assertThat(CalendarMonth.of(2016, Month.FEBRUARY).contains(CalendarMonth.YEAR), is(true));
        assertThat(CalendarMonth.of(2016, Month.FEBRUARY).contains(CalendarMonth.MONTH_OF_YEAR), is(true));

        assertThat(CalendarMonth.of(2016, Month.FEBRUARY).contains(PlainDate.of(2015, 1, 31)), is(false));
        assertThat(CalendarMonth.of(2016, Month.FEBRUARY).contains(PlainDate.of(2016, 2, 1)), is(true));
        assertThat(CalendarMonth.of(2016, Month.FEBRUARY).contains(PlainDate.of(2016, 2, 29)), is(true));
        assertThat(CalendarMonth.of(2016, Month.FEBRUARY).contains(PlainDate.of(2016, 3, 1)), is(false));
    }

    @Test
    public void isAfter() {
        assertThat(CalendarMonth.of(2016, Month.FEBRUARY).isAfter(PlainDate.of(2016, 2, 1)), is(false));
        assertThat(CalendarMonth.of(2016, Month.FEBRUARY).isAfter(PlainDate.of(2016, 1, 31)), is(true));

        assertThat(CalendarMonth.of(2016, Month.FEBRUARY).isAfter(CalendarMonth.of(2016, Month.JANUARY)), is(true));
        assertThat(CalendarMonth.of(2016, Month.FEBRUARY).isAfter(CalendarMonth.of(2016, Month.FEBRUARY)), is(false));
    }

    @Test
    public void isBefore() {
        assertThat(CalendarMonth.of(2016, Month.FEBRUARY).isBefore(PlainDate.of(2016, 2, 29)), is(false));
        assertThat(CalendarMonth.of(2016, Month.FEBRUARY).isBefore(PlainDate.of(2016, 3, 1)), is(true));

        assertThat(CalendarMonth.of(2016, Month.FEBRUARY).isBefore(CalendarMonth.of(2016, Month.MARCH)), is(true));
        assertThat(CalendarMonth.of(2016, Month.FEBRUARY).isBefore(CalendarMonth.of(2016, Month.FEBRUARY)), is(false));
    }

    @Test
    public void isSimultaneous() {
        assertThat(
            CalendarMonth.of(2016, Month.FEBRUARY).isSimultaneous(CalendarMonth.of(2016, Month.MARCH)), is(false));
        assertThat(
            CalendarMonth.of(2016, Month.FEBRUARY).isSimultaneous(CalendarMonth.of(2016, Month.FEBRUARY)), is(true));
    }

    @Test
    public void plusYears() {
        assertThat(
            CalendarMonth.of(2012, Month.FEBRUARY).plus(Years.ofGregorian(4)),
            is(CalendarMonth.of(2016, Month.FEBRUARY)));
    }

    @Test
    public void minusYears() {
        assertThat(
            CalendarMonth.of(2012, Month.FEBRUARY).minus(Years.ofGregorian(4)),
            is(CalendarMonth.of(2008, Month.FEBRUARY)));
    }

    @Test
    public void plusMonths() {
        assertThat(
            CalendarMonth.of(2012, Month.FEBRUARY).plus(Months.of(7)),
            is(CalendarMonth.of(2012, Month.SEPTEMBER)));
    }

    @Test
    public void minusMonths() {
        assertThat(
            CalendarMonth.of(2012, Month.FEBRUARY).minus(Months.of(7)),
            is(CalendarMonth.of(2011, Month.JULY)));
    }

    @Test
    public void format() {
        CalendarMonth cm = CalendarMonth.of(2012, Month.FEBRUARY);
        assertThat(
            ChronoFormatter.ofPattern(
                "yyyyMM",
                PatternType.CLDR,
                Locale.ROOT,
                CalendarMonth.chronology()
            ).format(cm),
            is("201202"));
    }

    @Test
    public void parse1() throws ParseException {
        CalendarMonth expected = CalendarMonth.of(2012, Month.FEBRUARY);
        assertThat(
            ChronoFormatter.ofPattern(
                "yyyyMM",
                PatternType.CLDR,
                Locale.ROOT,
                CalendarMonth.chronology()
            ).parse("201202"),
            is(expected));
    }

    @Test
    public void parse2() throws ParseException {
        assertThat(
            ChronoFormatter.ofPattern(
                "yyyyMM'00'",
                PatternType.CLDR,
                Locale.ROOT,
                CalendarMonth.chronology()
            ).parse("20150100"),
            is(CalendarMonth.of(2015, Month.JANUARY)));
    }

    @Test
    public void parse3() throws ParseException {
        assertThat(
            ChronoFormatter.ofPattern(
                "yyyyMM'00'",
                PatternType.CLDR,
                Locale.ROOT,
                CalendarMonth.threeten()
            ).parse("20150100"),
            is(YearMonth.of(2015, 1)));
    }

    @Test
    public void pattern() {
        assertThat(
            CalendarMonth.chronology().getFormatPattern(FormatStyle.LONG, Locale.JAPANESE),
            is("y年M月"));
        assertThat(
            CalendarMonth.chronology().getFormatPattern(FormatStyle.FULL, Locale.ROOT),
            is("uuuu-MM"));
    }

    @Test
    public void threetenAdapter() {
        assertThat(CalendarMonth.from(YearMonth.of(2016, 2)), is(CalendarMonth.of(2016, 2)));
        assertThat(CalendarMonth.of(2016, 2).toTemporalAccessor(), is(YearMonth.of(2016, 2)));
    }

    @Test
    public void nowInSystemTime() {
        assertThat(
            CalendarMonth.nowInSystemTime(),
            is(SystemClock.inLocalView().now(CalendarMonth.chronology())));
    }

    @Test
    public void abuts() {
        assertThat(
            CalendarMonth.of(2016, 11).abuts(CalendarMonth.of(2016, 9)),
            is(false));
        assertThat(
            CalendarMonth.of(2016, 11).abuts(CalendarMonth.of(2016, 10)),
            is(true));
        assertThat(
            CalendarMonth.of(2016, 11).abuts(CalendarMonth.of(2016, 11)),
            is(false));
        assertThat(
            CalendarMonth.of(2016, 11).abuts(CalendarMonth.of(2016, 12)),
            is(true));
        assertThat(
            CalendarMonth.of(2016, 11).abuts(CalendarMonth.of(2017, 1)),
            is(false));

        assertThat(
            CalendarMonth.of(2016, 11).abuts(DateInterval.since(PlainDate.of(2016, 11, 30))),
            is(false));
        assertThat(
            CalendarMonth.of(2016, 11).abuts(DateInterval.since(PlainDate.of(2016, 12, 1))),
            is(true));
        assertThat(
            CalendarMonth.of(2016, 11).abuts(DateInterval.since(PlainDate.of(2016, 12, 2))),
            is(false));

        assertThat(
            CalendarMonth.of(2016, 11).abuts(DateInterval.since(PlainDate.of(2016, 12, 1)).collapse()),
            is(false));
    }

    @Test
    public void prolepticNumber() {
        PlainDate start = PlainDate.of(2017, 12, 1);
        for (int i = 0; i < 14; i++) {
            PlainDate d = start.plus(i, CalendarUnit.MONTHS);
            CalendarMonth cm = CalendarMonth.from(d);
            assertThat(CalendarMonth.from(cm.toProlepticNumber()), is(cm));
        }
    }

}