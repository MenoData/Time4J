package net.time4j.calendar;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;
import net.time4j.Weekday;
import net.time4j.calendar.astro.SolarTime;
import net.time4j.engine.CalendarDate;
import net.time4j.engine.CalendarDays;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import net.time4j.history.ChronoHistory;
import net.time4j.history.HistoricDate;
import net.time4j.history.HistoricEra;
import net.time4j.tz.Timezone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.time.format.FormatStyle;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;


@RunWith(JUnit4.class)
public class HebrewMiscellaneousTest {

    @Test
    public void hebrewEpoch() {
        PlainDate gregorian = PlainDate.of(-3760, 9, 7);
        PlainDate julian = ChronoHistory.PROLEPTIC_JULIAN.convert(HistoricDate.of(HistoricEra.BC, 3761, 10, 7));
        assertThat(gregorian, is(julian));
    }

    @Test
    public void isSabbaticalYear() {
        HebrewCalendar date = HebrewCalendar.of(5778, HebrewMonth.TISHRI, 1);
        for (int i = 0; i < 7; i++) {
            assertThat(date.isSabbaticalYear(), is(date.getYear() == 5782));
            date = date.plus(1, HebrewCalendar.Unit.YEARS);
        }
    }

    @Test
    public void isRoshChodesh() {
        HebrewCalendar date = HebrewCalendar.of(5778, HebrewMonth.TISHRI, 1);
        assertThat(date.isRoshChodesh(), is(false));
        assertThat(date.with(HebrewCalendar.DAY_OF_MONTH, 30).isRoshChodesh(), is(true));
        assertThat(date.with(HebrewCalendar.MONTH_OF_YEAR, HebrewMonth.HESHVAN).isRoshChodesh(), is(true));
    }

    @Test
    public void hebrewCalendarProperties() {
        HebrewCalendar date = HebrewCalendar.of(5778, HebrewMonth.TISHRI, 11);
        assertThat(
            date.getDayOfMonth(),
            is(11));
        assertThat(
            date.getMonth(),
            is(HebrewMonth.TISHRI));
        assertThat(
            date.lengthOfMonth(),
            is(30));
        assertThat(
            date.atTime(12, 0).toDate(),
            is(date));
        assertThat(
            date.lengthOfYear(),
            is(354)
        );
    }

    @Test
    public void hebrewTimeProperties() {
        HebrewTime time = HebrewTime.ofDay(5, 123);
        assertThat(
            time.get(HebrewTime.CLOCK_CYCLE),
            is(HebrewTime.ClockCycle.DAY));
        assertThat(
            time.getInt(HebrewTime.CLOCK_HOUR),
            is(5));
        assertThat(
            time.getInt(HebrewTime.DIGITAL_HOUR),
            is(17));
        assertThat(
            time.getInt(HebrewTime.PART_OF_HOUR),
            is(123));
    }

    @Test
    public void onDateInJerusalem() {
        HebrewTime time = HebrewTime.ofDay(12, 540);
        assertThat(
            time.on(HebrewCalendar.of(5778, HebrewMonth.NISAN, 1), SolarTime.ofJerusalem()).get(),
            is(PlainTimestamp.of(2018, 3, 17, 4, 13, 26).atUTC())); // sunrise at 2018-03-17T03:43:02Z
        time = HebrewTime.ofNight(12, 540);
        assertThat(
            time.on(HebrewCalendar.of(5778, HebrewMonth.NISAN, 1), SolarTime.ofJerusalem()).get(),
            is(PlainTimestamp.of(2018, 3, 16, 16, 21, 11).atUTC())); // short after sunset
    }

    @Test
    public void onDateInTimezone() {
        HebrewTime time = HebrewTime.ofDay(12, 540);
        Timezone tz = Timezone.of("Asia/Jerusalem");
        assertThat(
            time.on(HebrewCalendar.of(5778, HebrewMonth.NISAN, 1), tz),
            is(PlainTimestamp.of(2018, 3, 17, 6, 30).in(tz)));
    }

    @Test
    public void atJerusalem() {
        assertThat(
            PlainTimestamp.of(2018, 3, 17, 4, 13, 26).atUTC().get(HebrewTime.at(SolarTime.ofJerusalem())).get(),
            is(HebrewTime.ofDay(12, 539))); // small rounding error (normally 540P)
        assertThat(
            PlainTimestamp.of(2018, 3, 16, 16, 21, 11).atUTC().get(HebrewTime.at(SolarTime.ofJerusalem())).get(),
            is(HebrewTime.ofNight(12, 539))); // small rounding error (normally 540P)
    }

    @Test
    public void atTimezone() {
        Timezone tz = Timezone.of("Asia/Jerusalem");
        Moment moment = PlainTimestamp.of(2018, 3, 17, 6, 30).in(tz);
        assertThat(
            moment.get(HebrewTime.at(tz.getID())),
            is(HebrewTime.ofDay(12, 540)));
    }

    @Test
    public void hebrewUnits() {
        HebrewCalendar start = HebrewCalendar.of(5778, HebrewMonth.HESHVAN, 6);
        HebrewCalendar end = HebrewCalendar.of(5778, HebrewMonth.ELUL, 6);
        assertThat(HebrewCalendar.Unit.MONTHS.between(start, end), is(10));
        start = start.plus(CalendarDays.ONE);
        assertThat(HebrewCalendar.Unit.MONTHS.between(start, end), is(9));
        start = start.minus(3, HebrewCalendar.Unit.YEARS);
        assertThat(HebrewCalendar.Unit.YEARS.between(start, end), is(3));
        start = start.plus(6, HebrewCalendar.Unit.YEARS).minus(CalendarDays.of(2)); // AM-5781-HESHVAN-5
        assertThat(HebrewCalendar.Unit.YEARS.between(start, end), is(-2));
        start = start.with(HebrewCalendar.MONTH_OF_YEAR, HebrewMonth.ELUL); // AM-5781-ELUL-5
        assertThat(HebrewCalendar.Unit.MONTHS.between(start, end), is(-36));
        start = start.plus(CalendarDays.ONE);
        assertThat(HebrewCalendar.Unit.MONTHS.between(start, end), is(-37));
        start = start.minus(37, HebrewCalendar.Unit.MONTHS);
        assertThat(start, is(end));
    }

    @Test
    public void defaultFirstDayOfWeek() {
        assertThat(HebrewCalendar.DAY_OF_WEEK.getDefaultMinimum(), is(Weekday.SUNDAY));
    }

    @Test
    public void longHeshvanShortKislev() {
        for (int y = 1; y <= 9999; y++) {
            HebrewCalendar h1 = HebrewCalendar.of(y, HebrewMonth.HESHVAN, 1);
            HebrewCalendar h2 = HebrewCalendar.of(y, HebrewMonth.KISLEV, 1);
            if (h1.lengthOfMonth() == 30 && h2.lengthOfMonth() == 29) {
                fail("Long Heshvan cannot be combined with short Kislev in same year: " + y);
                break;
            }
        }
    }

    @Test
    public void defaultOrderFormatter() throws ParseException {
        ChronoFormatter<HebrewCalendar> f =
            ChronoFormatter.ofPattern("yyyy-MM-dd", PatternType.CLDR_DATE, Locale.UK, HebrewCalendar.axis());
        HebrewCalendar date = HebrewCalendar.ofCivil(5778, 12, 11);
        assertThat(date.getMonth(), is(HebrewMonth.ELUL));
        assertThat(f.format(date), is("5778-12-11"));
        assertThat(f.parse("5778-12-11"), is(date));
    }

    @Test
    public void enumOrderFormatter() throws ParseException {
        ChronoFormatter<HebrewCalendar> f =
            ChronoFormatter
                .ofPattern("yyyy-MM-dd", PatternType.CLDR_DATE, Locale.UK, HebrewCalendar.axis())
                .with(HebrewMonth.order(), HebrewMonth.Order.ENUM);
        HebrewCalendar date = HebrewCalendar.of(5778, HebrewMonth.ELUL, 11);
        assertThat(date.getMonth(), is(HebrewMonth.ELUL));
        assertThat(f.format(date), is("5778-13-11"));
        assertThat(f.parse("5778-13-11"), is(date));
    }

    @Test
    public void civilOrderFormatter() throws ParseException {
        ChronoFormatter<HebrewCalendar> f =
            ChronoFormatter
                .ofPattern("yyyy-MM-dd", PatternType.CLDR_DATE, Locale.UK, HebrewCalendar.axis())
                .with(HebrewMonth.order(), HebrewMonth.Order.CIVIL);
        HebrewCalendar date = HebrewCalendar.ofCivil(5778, 12, 11);
        assertThat(date.getMonth(), is(HebrewMonth.ELUL));
        assertThat(f.format(date), is("5778-12-11"));
        assertThat(f.parse("5778-12-11"), is(date));
    }

    @Test
    public void biblicalOrderFormatter() throws ParseException {
        ChronoFormatter<HebrewCalendar> f =
            ChronoFormatter
                .ofPattern("yyyy-MM-dd", PatternType.CLDR_DATE, Locale.UK, HebrewCalendar.axis())
                .with(HebrewMonth.order(), HebrewMonth.Order.BIBLICAL);
        HebrewCalendar date = HebrewCalendar.ofBiblical(5778, 6, 11);
        assertThat(date.getMonth(), is(HebrewMonth.ELUL));
        assertThat(f.format(date), is("5778-06-11"));
        assertThat(f.parse("5778-06-11"), is(date));
    }

    @Test
    public void caSupport() {
        Locale locale = Locale.forLanguageTag("en-u-ca-hebrew");
        ChronoFormatter<CalendarDate> f = ChronoFormatter.ofGenericCalendarStyle(FormatStyle.FULL, locale);
        assertThat(
            f.format(PlainDate.of(2017, 10, 1)),
            is("Sunday, 11 Tishri 5778"));
    }

    @Test
    public void dateFormat() {
        HebrewCalendar date = HebrewCalendar.of(5778, HebrewMonth.ADAR_II, 29);
        ChronoFormatter<HebrewCalendar> f =
            ChronoFormatter.ofPattern("MMMM, dd (yyyy)", PatternType.CLDR_DATE, Locale.US, HebrewCalendar.axis());
        assertThat(f.format(date), is("Adar, 29 (5778)"));
        assertThat(f.format(date.plus(1, HebrewCalendar.Unit.YEARS)), is("Adar II, 29 (5779)"));
    }

    @Test
    public void timeFormat() {
        HebrewTime htime = HebrewTime.ofNight(12, 540);
        ChronoFormatter<HebrewTime> f =
            ChronoFormatter.ofPattern("H'H' P'P'", PatternType.DYNAMIC, Locale.ROOT, HebrewTime.axis());
        assertThat(f.format(htime), is("0H 540P"));
    }

}