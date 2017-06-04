package net.time4j.calendar;

import net.time4j.CalendarUnit;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;
import net.time4j.Weekday;
import net.time4j.calendar.astro.AstronomicalSeason;
import net.time4j.calendar.astro.JulianDay;
import net.time4j.engine.CalendarDate;
import net.time4j.engine.CalendarDays;
import net.time4j.format.DisplayMode;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.scale.TimeScale;
import net.time4j.tz.ZonalOffset;
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
    public void khayamIsEqualToBorkowskiInRange1178To1633() {
        for (int pyear = 1178; pyear <= 1633; pyear++) {
            assertThat(
                PersianCalendar.of(pyear, 1, 1).isLeapYear(),
                is(PersianAlgorithm.KHAYYAM.isLeapYear(pyear)));
        }
        PersianCalendar pcal = PersianCalendar.of(1178, 1, 1);
        long utcDays = pcal.getDaysSinceEpochUTC();
        long max = PersianCalendar.of(1634, 12, 29).getDaysSinceEpochUTC();
        while (utcDays <= max) {
            PersianCalendar khayyam = PersianAlgorithm.KHAYYAM.transform(utcDays);
            PersianCalendar expected = PersianCalendar.axis().getCalendarSystem().transform(utcDays);
            assertThat(khayyam, is(expected));
            utcDays++;
        }
    }

    @Test
    public void khayamOverFullRange() {
        long min = PersianCalendar.axis().getCalendarSystem().getMinimumSinceUTC();
        long max = PersianCalendar.axis().getCalendarSystem().getMaximumSinceUTC();
        for (long utcDays = min; utcDays <= max; utcDays++) {
            PersianCalendar khayyam = PersianAlgorithm.KHAYYAM.transform(utcDays);
            assertThat(PersianAlgorithm.KHAYYAM.transform(khayyam), is(utcDays));
        }
    }

    @Test
    public void birashkIsEqualToBorkowskiInRange1244To1402() {
        for (int pyear = 1244; pyear <= 1402; pyear++) {
            assertThat(
                PersianCalendar.of(pyear, 1, 1).isLeapYear(),
                is(PersianAlgorithm.BIRASHK.isLeapYear(pyear)));
        }
        long utcDays = PersianCalendar.of(1244, 1, 1).getDaysSinceEpochUTC();
        long max = PersianCalendar.of(1403, 12, 29).getDaysSinceEpochUTC();
        System.out.println(PersianCalendar.of(1403, 12, 29).transform(PlainDate.class)); // 2025-03-19
        while (utcDays <= max) {
            PersianCalendar birashk = PersianAlgorithm.BIRASHK.transform(utcDays);
            PersianCalendar expected = PersianCalendar.axis().getCalendarSystem().transform(utcDays);
            assertThat(birashk, is(expected));
            utcDays++;
        }
    }

    @Test
    public void birashkError2025() {
        long vernalEquinox = PlainDate.of(2025, 3, 21).getDaysSinceEpochUTC();
        assertThat(
            PersianAlgorithm.BIRASHK.transform(vernalEquinox),
            is(new PersianCalendar(1404, 1, 2)));
        assertThat(
            PersianAlgorithm.BIRASHK.isLeapYear(1403),
            is(false));
        assertThat(
            PersianCalendar.of(1403, 12, 30).isLeapYear(),
            is(true));
    }

    @Test
    public void birashkOverFullRange() {
        long min = PersianCalendar.axis().getCalendarSystem().getMinimumSinceUTC();
        long max = PersianCalendar.axis().getCalendarSystem().getMaximumSinceUTC();
        for (long utcDays = min; utcDays <= max; utcDays++) {
            PersianCalendar birashk = PersianAlgorithm.BIRASHK.transform(utcDays);
            assertThat(PersianAlgorithm.BIRASHK.transform(birashk), is(utcDays));
        }
    }

    @Test
    public void borkowskiAstronomical() {
        int year = PersianCalendar.of(1, 1, 1).getInt(CommonElements.RELATED_GREGORIAN_YEAR);
        ZonalOffset offset = ZonalOffset.ofTotalSeconds((int) (3.425 * 3600)); // +03:25:30 (used by Borkowski)
        while (year <= 3000) {
            JulianDay jd = AstronomicalSeason.VERNAL_EQUINOX.julianDay(year);
            double tt = (jd.getValue() - 2441317.5) * 86400.0;
            double deltaT; // formula of Borkowski
            int t = (year - 1800) / 100;
            if (year < 948) {
                deltaT = (44.3 * t + 320) * t + 1360;
            } else if (year < 1637) {
                deltaT = 25.5 * t * t;
            } else if (year > 2005) {
                deltaT = 25.5 * t * t - 36;
            } else {
                deltaT = TimeScale.deltaT(year, 3);
            }
            double ut = tt - deltaT;
            long seconds = (long) Math.floor(ut);
            int nanos = (int) ((ut - seconds) * 1_000_000_000);

            PlainTimestamp tsp =
                // Borkowski uses mean solar time
                Moment.of(
                    seconds + 2 * 365 * 86400,
                    nanos,
                    TimeScale.POSIX
                ).toZonalTimestamp(offset);
            if (tsp.getHour() >= 12) {
                tsp = tsp.plus(1, CalendarUnit.DAYS); // determine equinox day nearest to midnight
            }
            PlainDate cal = PersianCalendar.of(year - 621, 1, 1).transform(PlainDate.axis()); // algorithmic date
            try {
                assertThat(cal, is(tsp.getCalendarDate()));
            } catch (Throwable th) {
                switch (year) {
                    case 659:
                    case 1113:
                    case 1307:
                    case 2487:
                    case 2681:
                    case 2846:
                        System.out.println(tsp); // tolerated deviation, probably caused by slightly different astronomy
                        break;
                    default:
                        throw th;
                }
            }
            year++;
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