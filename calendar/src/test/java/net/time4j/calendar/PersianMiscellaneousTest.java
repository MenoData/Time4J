package net.time4j.calendar;

import net.time4j.PlainDate;
import net.time4j.Weekday;
import net.time4j.engine.CalendarDate;
import net.time4j.engine.CalendarDays;
import net.time4j.format.DisplayMode;
import net.time4j.format.expert.ChronoFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class PersianMiscellaneousTest {

    @Test
    public void persianCalendarProperties() {
        PersianCalendar date = PersianCalendar.of(1394, PersianMonth.ABAN, 14);
        assertThat(
            date.getDayOfMonth(),
            is(14));
        assertThat(
            date.getMonth(),
            is(PersianMonth.ABAN));
        assertThat(
            date.lengthOfMonth(),
            is(30));
        assertThat(
            date.atTime(12, 0).toDate(),
            is(date));
    }

    @Test
    public void persianCalendarBetween() {
        PersianCalendar start = PersianCalendar.of(1394, PersianMonth.ABAN, 14);
        PersianCalendar end = PersianCalendar.of(1394, PersianMonth.ESFAND, 13);
        assertThat(PersianCalendar.Unit.MONTHS.between(start, end), is(3));
        end = end.plus(CalendarDays.ONE);
        assertThat(PersianCalendar.Unit.MONTHS.between(start, end), is(4));

        start = PersianCalendar.of(1360, 2, 20);
        end = PersianCalendar.of(1394, 11, 25);

        for (int i = 0; i < 15; i++) {
            start = start.plus(1, PersianCalendar.Unit.DAYS);
            assertThat(
                PersianCalendar.Unit.YEARS.between(start, end),
                is(34));
        }

        start = PersianCalendar.of(1360, 2, 20);
        end = PersianCalendar.of(1394, 2, 20);

        assertThat(
            PersianCalendar.Unit.YEARS.between(start, end),
            is(34));
        start = PersianCalendar.of(1360, 2, 21);
        assertThat(
            PersianCalendar.Unit.YEARS.between(start, end),
            is(33));
    }

    @Test
    public void khayam() {
        for (int pyear = 1178; pyear <= 1633; pyear++) {
            int m = pyear % 33;
            boolean leapKhayam = (m == 1 || m == 5 || m == 9 || m == 13 || m == 17 || m == 22 || m == 26 || m == 30);
            assertThat(
                PersianCalendar.of(pyear, 1, 1).isLeapYear(),
                is(leapKhayam));
        }
    }

    @Test
    public void formatPersianCalendar() throws ParseException {
        ChronoFormatter<PersianCalendar> formatter = // pattern => G y MMMM d, EEEE
            ChronoFormatter.ofStyle(DisplayMode.FULL, new Locale("fa"), PersianCalendar.axis());
        PersianCalendar jalali = PersianCalendar.of(1393, 2, 10);

        String expected = "ه\u200D.ش."; // era
        expected += " ";
        expected += "۱۳۹۳"; // year
        expected += " ";
        expected += "اردیبهشت"; // month
        expected += " ";
        expected += "۱۰"; // day-of-month
        expected += ", ";
        expected += "چهارشنبه"; // day-of-week
        PersianCalendar parsed = formatter.parse(expected);
        assertThat(parsed, is(jalali));

        String formatted = formatter.format(jalali); //         ه‍.ش. ۱۳۹۳ اردیبهشت۱۰,چهارشنبه
        assertThat(formatted, is(expected));

        assertThat(jalali.transform(PlainDate.class), is(PlainDate.of(2014, 4, 30)));
    }

    @Test
    public void formatGenericCalendarByPattern() {
        Locale loc = Locale.forLanguageTag("de-IR-u-ca-persian");
        System.out.println(loc); // de_IR_#u-ca-persian
        ChronoFormatter<CalendarDate> formatter = ChronoFormatter.ofGenericCalendarPattern("G y MMMM d, EEEE", loc);
        PersianCalendar jalali = PersianCalendar.of(1393, 1, 10);
        PlainDate gregorian = jalali.transform(PlainDate.class);
        assertThat(formatter.format(jalali), is("AP 1393 Farwardin 10, Sonntag"));
        assertThat(formatter.format(gregorian), is("AP 1393 Farwardin 10, Sonntag"));
        assertThat(formatter.with(Locale.US).format(jalali), is("AD 2014 March 30, Sunday")); // converted to gregorian
    }

    @Test
    public void formatGenericCalendarByStyle() {
        Locale loc = Locale.forLanguageTag("de-IR-u-ca-persian");
        ChronoFormatter<CalendarDate> formatter = ChronoFormatter.ofGenericCalendarStyle(DisplayMode.MEDIUM, loc);
        PersianCalendar jalali = PersianCalendar.of(1393, 1, 10);
        PlainDate gregorian = jalali.transform(PlainDate.class);
        assertThat(formatter.format(jalali), is("10.01.1393 AP"));
        assertThat(formatter.format(gregorian), is("10.01.1393 AP"));
        assertThat(formatter.with(Locale.US).format(jalali), is("Mar 30, 2014")); // converted to gregorian
    }

    @Test
    public void defaultFirstDayOfWeek() {
        assertThat(PersianCalendar.DAY_OF_WEEK.getDefaultMinimum(), is(Weekday.SATURDAY));
    }

}