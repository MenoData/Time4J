package net.time4j.range;

import net.time4j.CalendarUnit;
import net.time4j.PlainDate;
import net.time4j.SystemClock;
import net.time4j.Weekday;
import net.time4j.ZonalClock;
import net.time4j.base.GregorianDate;
import net.time4j.base.GregorianMath;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class CalendarWeekTest {

    @Test
    public void min() {
        CalendarWeek cw = CalendarWeek.of(GregorianMath.MIN_YEAR, 1);
        assertThat(cw.getYear(), is(GregorianMath.MIN_YEAR));
        assertThat(cw.getWeek(), is(1));
        assertThat(cw.at(Weekday.MONDAY), is(PlainDate.axis().getMinimum()));
    }

    @Test
    public void max1() {
        CalendarWeek cw = CalendarWeek.of(GregorianMath.MAX_YEAR, 52);
        assertThat(cw.getYear(), is(GregorianMath.MAX_YEAR));
        assertThat(cw.getWeek(), is(52));
        assertThat(cw.getEnd().getTemporal(), is(PlainDate.axis().getMaximum()));
        assertThat(cw.at(Weekday.FRIDAY), is(PlainDate.axis().getMaximum()));
        assertThat(cw.length(), is(5));

        int count = 0;
        for (PlainDate d : cw) {
            count++;
            assertThat(d.getDayOfWeek(), is(Weekday.valueOf(count)));
        }
        assertThat(count, is(5));

    }

    @Test(expected=IllegalArgumentException.class)
    public void max2() {
        CalendarWeek.of(GregorianMath.MAX_YEAR, 52).at(Weekday.SATURDAY);
    }

    @Test(expected=IllegalArgumentException.class)
    public void invalidWeek() {
        CalendarWeek.of(2014, 53);
    }

    @Test
    public void fromGregorianDate() {
        assertThat(
            CalendarWeek.from(PlainDate.of(2016, 2, 29)),
            is(CalendarWeek.of(2016, 9)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void fromGregorianDateInvalid() {
        CalendarWeek.from(
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
        assertThat(CalendarWeek.of(2011, 4).getYear(), is(2011));
    }

    @Test
    public void getWeek() {
        assertThat(CalendarWeek.of(2011, 4).getWeek(), is(4));
    }

    @Test
    public void getStart() {
        assertThat(CalendarWeek.of(2011, 4).getStart(), is(Boundary.ofClosed(PlainDate.of(2011, 1, 24))));
    }

    @Test
    public void getEnd() {
        assertThat(CalendarWeek.of(2011, 4).getEnd(), is(Boundary.ofClosed(PlainDate.of(2011, 1, 30))));
    }

    @Test
    public void compareTo() {
        assertThat(CalendarWeek.of(2012, 3).compareTo(CalendarWeek.of(2012, 4)) < 0, is(true));
        assertThat(CalendarWeek.of(2012, 3).compareTo(CalendarWeek.of(2012, 2)) > 0, is(true));
        assertThat(CalendarWeek.of(2012, 3).compareTo(CalendarWeek.of(2012, 3)) == 0, is(true));
    }

    @Test
    public void atDayOfWeek() {
        CalendarWeek cw = CalendarWeek.of(2012, 3);
        assertThat(
            cw.at(Weekday.MONDAY) == cw.getStart().getTemporal(),
            is(true));
        assertThat(
            cw.at(Weekday.FRIDAY),
            is(PlainDate.of(2012, 1, 20)));
    }

    @Test
    public void currentValue() {
        ZonalClock clock = SystemClock.inLocalView();
        CalendarWeek cw = clock.now(CalendarWeek.chronology());
        assertThat(cw.getYear(), is(clock.today().get(PlainDate.YEAR_OF_WEEKDATE)));
        assertThat(cw.getWeek(), is(clock.today().get(CalendarWeek.WEEK_OF_YEAR)));
    }

    @Test
    public void iterator() {
        int count = 1;
        for (PlainDate date : CalendarWeek.of(2016, 4)) {
            System.out.println(count + " => " + date);
            count++;
        }
        assertThat(count - 1, is(7));
    }

    @Test
    public void length() {
        assertThat(CalendarWeek.of(2016, 3).length(), is(7));
    }

    @Test
    public void contains() {
        assertThat(CalendarWeek.of(2016, 1).contains(CalendarWeek.YEAR_OF_WEEKDATE), is(true));
        assertThat(CalendarWeek.of(2016, 1).contains(CalendarWeek.WEEK_OF_YEAR), is(true));

        assertThat(CalendarWeek.of(2016, 24).contains(PlainDate.of(2016, 6, 12)), is(false));
        assertThat(CalendarWeek.of(2016, 24).contains(PlainDate.of(2016, 6, 13)), is(true));
        assertThat(CalendarWeek.of(2016, 24).contains(PlainDate.of(2016, 6, 14)), is(true));
        assertThat(CalendarWeek.of(2016, 24).contains(PlainDate.of(2016, 6, 19)), is(true));
        assertThat(CalendarWeek.of(2016, 24).contains(PlainDate.of(2016, 6, 20)), is(false));
    }

    @Test
    public void isAfter() {
        assertThat(CalendarWeek.of(2016, 24).isAfter(PlainDate.of(2016, 6, 13)), is(false));
        assertThat(CalendarWeek.of(2016, 24).isAfter(PlainDate.of(2016, 6, 12)), is(true));

        assertThat(CalendarWeek.of(2016, 24).isAfter(CalendarWeek.of(2016, 23)), is(true));
        assertThat(CalendarWeek.of(2016, 24).isAfter(CalendarWeek.of(2016, 24)), is(false));
    }

    @Test
    public void isBefore() {
        assertThat(CalendarWeek.of(2016, 24).isBefore(PlainDate.of(2016, 6, 20)), is(true));
        assertThat(CalendarWeek.of(2016, 24).isBefore(PlainDate.of(2016, 6, 19)), is(false));

        assertThat(CalendarWeek.of(2016, 24).isBefore(CalendarWeek.of(2016, 25)), is(true));
        assertThat(CalendarWeek.of(2016, 24).isBefore(CalendarWeek.of(2016, 24)), is(false));
    }

    @Test
    public void isSimultaneous() {
        assertThat(CalendarWeek.of(2016, 24).isSimultaneous(CalendarWeek.of(2016, 25)), is(false));
        assertThat(CalendarWeek.of(2016, 24).isSimultaneous(CalendarWeek.of(2016, 24)), is(true));
    }

    @Test
    public void plusYears() {
        assertThat(
            CalendarWeek.of(2012, 24).plus(Years.ofWeekBased(4)),
            is(CalendarWeek.of(2016, 24)));
        assertThat(
            CalendarWeek.of(2015, 53).plus(Years.ofWeekBased(1)),
            is(CalendarWeek.of(2016, 52)));
    }

    @Test
    public void minusYears() {
        assertThat(
            CalendarWeek.of(2012, 24).minus(Years.ofWeekBased(4)),
            is(CalendarWeek.of(2008, 24)));
        assertThat(
            CalendarWeek.of(2015, 53).minus(Years.ofWeekBased(1)),
            is(CalendarWeek.of(2014, 52)));
    }

    @Test
    public void plusWeeks() {
        assertThat(
            CalendarWeek.of(2012, 24).plus(Weeks.of(7)),
            is(CalendarWeek.of(2012, 31)));
    }

    @Test
    public void minusWeeks() {
        assertThat(
            CalendarWeek.of(2016, 1).minus(Weeks.of(7)),
            is(CalendarWeek.of(2015, 47)));
    }

    @Test
    public void formatISO() {
        CalendarWeek cw = CalendarWeek.of(2012, 3);
        assertThat(
            cw.toString(),
            is("2012-W03"));
    }

    @Test
    public void parseISO() throws ParseException {
        CalendarWeek expected = CalendarWeek.of(2012, 3);
        assertThat(
            CalendarWeek.parseISO("2012-W03"),
            is(expected));
        assertThat(
            CalendarWeek.parseISO("2012W03"),
            is(expected));
    }

    @Test
    public void formatKW() {
        ChronoFormatter<CalendarWeek> f =
            ChronoFormatter.setUp(CalendarWeek.chronology(), Locale.GERMAN)
                .addPattern("w. 'KW'", PatternType.CLDR).build();
        assertThat(
            f.format(CalendarWeek.of(2016, 4)),
            is("4. KW"));
    }

    @Test
    public void styledFormatIsoShort() throws ParseException {
        ChronoFormatter<CalendarWeek> f =
            ChronoFormatter.ofStyle(FormatStyle.SHORT, Locale.ROOT, CalendarWeek.chronology());
        assertThat(
            f.format(CalendarWeek.of(2016, 4)),
            is("2016W04"));
        assertThat(
            f.parse("2016W04"),
            is(CalendarWeek.of(2016, 4)));
    }

    @Test
    public void styledFormatIsoMedium() throws ParseException {
        ChronoFormatter<CalendarWeek> f =
            ChronoFormatter.ofStyle(FormatStyle.MEDIUM, Locale.ROOT, CalendarWeek.chronology());
        assertThat(
            f.format(CalendarWeek.of(2016, 4)),
            is("2016-W04"));
        assertThat(
            f.parse("2016-W04"),
            is(CalendarWeek.of(2016, 4)));
    }

    @Test
    public void styledFormatWithEnglish() throws ParseException {
        ChronoFormatter<CalendarWeek> f =
            ChronoFormatter.ofStyle(FormatStyle.MEDIUM, Locale.ENGLISH, CalendarWeek.chronology());
        assertThat(
            f.format(CalendarWeek.of(2016, 4)),
            is("week 4 of 2016"));
        assertThat(
            f.parse("week 4 of 2016"),
            is(CalendarWeek.of(2016, 4)));
    }

    @Test
    public void styledFormatWithGerman() throws ParseException {
        ChronoFormatter<CalendarWeek> f =
            ChronoFormatter.ofStyle(FormatStyle.MEDIUM, Locale.GERMAN, CalendarWeek.chronology());
        assertThat(
            f.format(CalendarWeek.of(2016, 4)),
            is("Woche 4 des Jahres 2016"));
        assertThat(
            f.parse("Woche 4 des Jahres 2016"),
            is(CalendarWeek.of(2016, 4)));
    }

    @Test
    public void styledFormatWithTagalog() throws ParseException {
        ChronoFormatter<CalendarWeek> f =
            ChronoFormatter.ofStyle(FormatStyle.MEDIUM, Locale.forLanguageTag("fil"), CalendarWeek.chronology());
        assertThat(
            f.format(CalendarWeek.of(2016, 1)),
            is("ika-1 linggo ng 2016"));
        assertThat(
            f.format(CalendarWeek.of(2016, 4)),
            is("linggo 4 ng 2016"));
        assertThat(
            f.parse("ika-1 linggo ng 2016"),
            is(CalendarWeek.of(2016, 1)));
        assertThat(
            f.parse("linggo 4 ng 2016"),
            is(CalendarWeek.of(2016, 4)));
    }

    @Test
    public void withLastWeekOfYear() {
        assertThat(
            CalendarWeek.of(2015, 1).withLastWeekOfYear(),
            is(CalendarWeek.of(2015, 53)));
    }

    @Test
    public void isValid() {
        assertThat(
            CalendarWeek.isValid(2015, 1),
            is(true));
        assertThat(
            CalendarWeek.isValid(2015, 53),
            is(true));
        assertThat(
            CalendarWeek.isValid(2015, 0),
            is(false));
        assertThat(
            CalendarWeek.isValid(2015, 54),
            is(false));
        assertThat(
            CalendarWeek.isValid(2016, 52),
            is(true));
        assertThat(
            CalendarWeek.isValid(2016, 53),
            is(false));
    }

    @Test
    public void nowInSystemTime() {
        assertThat(
            CalendarWeek.nowInSystemTime(),
            is(SystemClock.inLocalView().now(CalendarWeek.chronology())));
    }

    @Test
    public void streamDaily() {
        List<PlainDate> expected = new ArrayList<>();
        expected.add(PlainDate.of(2016, 1, 25));
        expected.add(PlainDate.of(2016, 1, 26));
        expected.add(PlainDate.of(2016, 1, 27));
        expected.add(PlainDate.of(2016, 1, 28));
        expected.add(PlainDate.of(2016, 1, 29));
        expected.add(PlainDate.of(2016, 1, 30));
        expected.add(PlainDate.of(2016, 1, 31));
        assertThat(CalendarWeek.of(2016, 4).streamDaily().collect(Collectors.toList()), is(expected));
    }

    @Test
    public void prolepticNumber() {
        PlainDate start = PlainDate.of(2018, 10, 7);
        for (int i = 0; i < 9; i++) {
            PlainDate d = start.plus(i, CalendarUnit.DAYS);
            CalendarWeek cw = CalendarWeek.from(d);
            assertThat(CalendarWeek.from(cw.toProlepticNumber()), is(cw));
        }
    }

    @Test
    public void stream() {
        CalendarWeek start = CalendarWeek.of(2017, 52);
        CalendarWeek end = CalendarWeek.of(2018, 3);
        List<CalendarWeek> expected =
            Arrays.asList(
                start,
                CalendarWeek.of(2018, 1),
                CalendarWeek.of(2018, 2),
                end);
        assertThat(
            CalendarPeriod.between(start, end).stream().collect(Collectors.toList()),
            is(expected));
        assertThat(
            CalendarPeriod.between(start, start).stream().collect(Collectors.toList()),
            is(Collections.singletonList(start)));
    }

    @Test
    public void delta() {
        CalendarWeek start = CalendarWeek.of(2017, 52);
        CalendarWeek end = CalendarWeek.of(2018, 3);
        assertThat(
            CalendarPeriod.between(start, end).delta(),
            is(3L));
        assertThat(
            CalendarPeriod.between(start, start).delta(),
            is(0L));
    }

    @Test
    public void abuts() {
        CalendarWeek cw1 = CalendarWeek.of(2017, 52);
        CalendarWeek cw2 = CalendarWeek.of(2018, 3);
        CalendarWeek cw3 = CalendarWeek.of(2018, 4);
        CalendarWeek cw4 = CalendarWeek.of(2018, 45);
        assertThat(
            CalendarPeriod.between(cw1, cw2).abuts(CalendarPeriod.between(cw3, cw4)),
            is(true));
        cw3 = cw3.plus(Weeks.ONE);
        assertThat(
            CalendarPeriod.between(cw1, cw2).abuts(CalendarPeriod.between(cw3, cw4)),
            is(false));
    }

}