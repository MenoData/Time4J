package net.time4j.range;

import net.time4j.CalendarUnit;
import net.time4j.PlainDate;
import net.time4j.Quarter;
import net.time4j.SystemClock;
import net.time4j.ZonalClock;
import net.time4j.base.GregorianDate;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.time.format.FormatStyle;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class CalendarQuarterTest {

    @Test
    public void fromGregorianDate() {
        assertThat(
            CalendarQuarter.from(PlainDate.of(2016, 2, 29)),
            is(CalendarQuarter.of(2016, Quarter.Q1)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void fromGregorianDateInvalid() {
        CalendarQuarter.from(
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
        assertThat(CalendarQuarter.of(2011, Quarter.Q3).getYear(), is(2011));
    }

    @Test
    public void getQuarter() {
        assertThat(CalendarQuarter.of(2011, Quarter.Q3).getQuarter(), is(Quarter.Q3));
    }

    @Test
    public void atEndOfQuarter() {
        assertThat(CalendarQuarter.of(2012, Quarter.Q1).atEndOfQuarter(), is(PlainDate.of(2012, 3, 31)));
    }

    @Test
    public void getStart() {
        assertThat(CalendarQuarter.of(2011, Quarter.Q3).getStart(), is(Boundary.ofClosed(PlainDate.of(2011, 7, 1))));
    }

    @Test
    public void getEnd() {
        assertThat(CalendarQuarter.of(2011, Quarter.Q3).getEnd(), is(Boundary.ofClosed(PlainDate.of(2011, 9, 30))));
    }

    @Test
    public void compareTo() {
        assertThat(CalendarQuarter.of(2012, Quarter.Q3).compareTo(CalendarQuarter.of(2012, Quarter.Q4)) < 0, is(true));
        assertThat(CalendarQuarter.of(2012, Quarter.Q3).compareTo(CalendarQuarter.of(2012, Quarter.Q2)) > 0, is(true));
        assertThat(CalendarQuarter.of(2012, Quarter.Q3).compareTo(CalendarQuarter.of(2012, Quarter.Q3)) == 0, is(true));
    }

    @Test
    public void atDay() {
        CalendarQuarter cq = CalendarQuarter.of(2012, Quarter.Q3);
        assertThat(
            cq.atDayOfQuarter(1) == cq.getStart().getTemporal(),
            is(true));
        assertThat(
            cq.atDayOfQuarter(92),
            is(PlainDate.of(2012, 9, 30)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void atDayOutOfRange() {
        CalendarQuarter.of(2012, Quarter.Q3).atDayOfQuarter(93);
    }

    @Test
    public void currentValue() {
        ZonalClock clock = SystemClock.inLocalView();
        CalendarQuarter cq = clock.now(CalendarQuarter.chronology());
        assertThat(cq.getYear(), is(clock.today().getYear()));
        assertThat(cq.getQuarter(), is(clock.today().get(PlainDate.QUARTER_OF_YEAR)));
    }

    @Test
    public void iterator() {
        int count = 1;
        for (PlainDate date : CalendarQuarter.of(2016, Quarter.Q3)) {
            // System.out.println(count + " => " + date);
            count++;
        }
        assertThat(count - 1, is(92));
    }

    @Test
    public void length() {
        assertThat(CalendarQuarter.of(2016, Quarter.Q3).length(), is(92));
        assertThat(CalendarQuarter.of(2016, Quarter.Q1).length(), is(91));
        assertThat(CalendarQuarter.of(2015, Quarter.Q1).length(), is(90));
    }

    @Test
    public void contains() {
        assertThat(CalendarQuarter.of(2016, Quarter.Q1).contains(CalendarQuarter.YEAR), is(true));
        assertThat(CalendarQuarter.of(2016, Quarter.Q1).contains(CalendarQuarter.QUARTER_OF_YEAR), is(true));

        assertThat(CalendarQuarter.of(2016, Quarter.Q1).contains(PlainDate.of(2015, 12, 31)), is(false));
        assertThat(CalendarQuarter.of(2016, Quarter.Q1).contains(PlainDate.of(2016, 1, 1)), is(true));
        assertThat(CalendarQuarter.of(2016, Quarter.Q1).contains(PlainDate.of(2016, 2, 29)), is(true));
        assertThat(CalendarQuarter.of(2016, Quarter.Q1).contains(PlainDate.of(2016, 3, 31)), is(true));
        assertThat(CalendarQuarter.of(2016, Quarter.Q1).contains(PlainDate.of(2016, 4, 1)), is(false));
    }

    @Test
    public void isAfter() {
        assertThat(CalendarQuarter.of(2016, Quarter.Q3).isAfter(PlainDate.of(2016, 7, 1)), is(false));
        assertThat(CalendarQuarter.of(2016, Quarter.Q3).isAfter(PlainDate.of(2016, 6, 30)), is(true));

        assertThat(CalendarQuarter.of(2016, Quarter.Q3).isAfter(CalendarQuarter.of(2016, Quarter.Q2)), is(true));
        assertThat(CalendarQuarter.of(2016, Quarter.Q3).isAfter(CalendarQuarter.of(2016, Quarter.Q3)), is(false));
    }

    @Test
    public void isBefore() {
        assertThat(CalendarQuarter.of(2016, Quarter.Q3).isBefore(PlainDate.of(2016, 10, 1)), is(true));
        assertThat(CalendarQuarter.of(2016, Quarter.Q3).isBefore(PlainDate.of(2016, 9, 30)), is(false));

        assertThat(CalendarQuarter.of(2016, Quarter.Q3).isBefore(CalendarQuarter.of(2016, Quarter.Q4)), is(true));
        assertThat(CalendarQuarter.of(2016, Quarter.Q3).isBefore(CalendarQuarter.of(2016, Quarter.Q3)), is(false));
    }

    @Test
    public void isSimultaneous() {
        assertThat(CalendarQuarter.of(2016, Quarter.Q3).isSimultaneous(CalendarQuarter.of(2016, Quarter.Q4)), is(false));
        assertThat(CalendarQuarter.of(2016, Quarter.Q3).isSimultaneous(CalendarQuarter.of(2016, Quarter.Q3)), is(true));
    }

    @Test
    public void plusYears() {
        assertThat(
            CalendarQuarter.of(2012, Quarter.Q3).plus(Years.ofGregorian(4)),
            is(CalendarQuarter.of(2016, Quarter.Q3)));
    }

    @Test
    public void minusYears() {
        assertThat(
            CalendarQuarter.of(2012, Quarter.Q3).minus(Years.ofGregorian(4)),
            is(CalendarQuarter.of(2008, Quarter.Q3)));
    }

    @Test
    public void plusQuarters() {
        assertThat(
            CalendarQuarter.of(2012, Quarter.Q3).plus(Quarters.of(7)),
            is(CalendarQuarter.of(2014, Quarter.Q2)));
    }

    @Test
    public void minusQuarters() {
        assertThat(
            CalendarQuarter.of(2012, Quarter.Q3).minus(Quarters.of(7)),
            is(CalendarQuarter.of(2010, Quarter.Q4)));
    }

    @Test
    public void format() {
        CalendarQuarter cq = CalendarQuarter.of(2012, Quarter.Q3);
        assertThat(
            ChronoFormatter.ofPattern(
                "yyyy-'Q'Q",
                PatternType.CLDR,
                Locale.ROOT,
                CalendarQuarter.chronology()
            ).format(cq),
            is("2012-Q3"));
    }

    @Test
    public void parse() throws ParseException {
        CalendarQuarter expected = CalendarQuarter.of(2012, Quarter.Q3);
        assertThat(
            ChronoFormatter.ofPattern(
                "yyyy-'Q'Q",
                PatternType.CLDR,
                Locale.ROOT,
                CalendarQuarter.chronology()
            ).parse("2012-Q3"),
            is(expected));
    }

    @Test
    public void pattern() {
        assertThat(
            CalendarQuarter.chronology().getFormatPattern(FormatStyle.LONG, Locale.JAPANESE),
            is("y/QQQ"));
        assertThat(
            CalendarQuarter.chronology().getFormatPattern(FormatStyle.FULL, Locale.ROOT),
            is("uuuu-'Q'Q"));
    }

    @Test
    public void nowInSystemTime() {
        assertThat(
            CalendarQuarter.nowInSystemTime(),
            is(SystemClock.inLocalView().now(CalendarQuarter.chronology())));
    }

    @Test
    public void prolepticNumber() {
        PlainDate start = PlainDate.of(2017, 10, 1);
        for (int i = 0; i < 6; i++) {
            PlainDate d = start.plus(i, CalendarUnit.QUARTERS);
            CalendarQuarter cq = CalendarQuarter.from(d);
            assertThat(CalendarQuarter.from(cq.toProlepticNumber()), is(cq));
        }
    }

}