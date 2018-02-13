package net.time4j.calendar;

import net.time4j.PlainDate;
import net.time4j.Weekday;
import net.time4j.engine.CalendarDate;
import net.time4j.engine.EpochDays;
import net.time4j.format.DisplayMode;
import net.time4j.format.expert.ChronoFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class VietnameseMiscellaneousTest {

    @Test
    public void ruleConsistency() {
        int winterSolstice = SolarTerm.MAJOR_11_DONGZHI_270.getIndex();
        EastAsianCS<VietnameseCalendar> calsys = VietnameseCalendar.nowInSystemTime().getCalendarSystem();
        VietnameseCalendar cal = calsys.transform(calsys.getMinimumSinceUTC());
        int prev = 0;
        int year = cal.getYear().getNumber();
        int mCount = 12;
        boolean leapYear = false;
        boolean hasNoMajorSolarTerm = false;

        while (true) {
            assertThat(cal.getDayOfMonth(), is(1));
            long utcDays = cal.getDaysSinceEpochUTC();
            if (year != prev) {
                assertThat(cal.getDayOfYear(), is(1));
                long ny = calsys.newYear(cal.getCycle(), cal.getYear().getNumber());
                assertThat(utcDays, is(ny));
                assertThat(calsys.transform(ny), is(cal));
                assertThat(mCount, is(leapYear ? 13 : 12)); // length of leap year
                prev = year;
                mCount = 0;
                leapYear = false;
                hasNoMajorSolarTerm = false;
            }
            if (cal.getMonth().isLeap()) {
                leapYear = true;
                assertThat(cal.getMonth().getNumber(), is(cal.getLeapMonth()));
                assertThat(hasNoMajorSolarTerm, is(false));
                assertThat(calsys.hasNoMajorSolarTerm(utcDays), is(true)); // first time of no major solar term
                hasNoMajorSolarTerm = true; // max one leap month per leap year
            }
            try {
                if (utcDays >= 375791) { // 3000-11-18
                    break;
                }
                VietnameseCalendar next = calsys.transform(calsys.newMoonOnOrAfter(utcDays + 1));
                if ((cal.getMonth().getNumber() == 11) && !cal.getMonth().isLeap()) { // winter condition
                    assertThat(calsys.getMajorSolarTerm(utcDays) <= winterSolstice, is(true));
                    assertThat(calsys.getMajorSolarTerm(next.getDaysSinceEpochUTC()) >= winterSolstice, is(true));
                }
                year = next.getYear().getNumber();
                mCount++;
                if ((year != prev) && !leapYear) {
                    assertThat(cal.getLeapMonth(), is(0));
                }
                cal = next;
            } catch (RuntimeException re) {
                System.err.println(utcDays + "/" + PlainDate.of(utcDays, EpochDays.UTC));
                re.printStackTrace();
                break;
            }
        }
    }

//    @Test
//    public void conversionRoundtrip() { // long runner hence inactive test (but was once successfully tested)
//        CalendarSystem<VietnameseCalendar> calsys = VietnameseCalendar.axis().getCalendarSystem();
//
//        for (long utcDays = calsys.getMinimumSinceUTC(); utcDays <= calsys.getMaximumSinceUTC(); utcDays++) {
//            VietnameseCalendar cal = calsys.transform(utcDays);
//            assertThat(calsys.transform(cal), is(utcDays));
//        }
//    }

    @Test
    public void defaultFirstDayOfWeek() {
        assertThat(VietnameseCalendar.DAY_OF_WEEK.getDefaultMinimum(), is(Weekday.MONDAY));
    }

    @Test
    public void elementsOfMinimum() {
        VietnameseCalendar min = VietnameseCalendar.axis().getMinimum();
        System.out.println(min);
        assertThat(min.transform(PlainDate.axis()), is(PlainDate.of(1813, 2, 1)));
        assertThat(min.get(CommonElements.RELATED_GREGORIAN_YEAR), is(1813));
        assertThat(min.get(VietnameseCalendar.DAY_OF_MONTH), is(1));
        assertThat(min.get(VietnameseCalendar.DAY_OF_YEAR), is(1));
        assertThat(min.get(VietnameseCalendar.MONTH_OF_YEAR), is(EastAsianMonth.valueOf(1)));
        assertThat(min.get(VietnameseCalendar.CYCLE), is(75));
    }

    @Test
    public void elementsOfMaximum() {
        VietnameseCalendar max = VietnameseCalendar.axis().getMaximum();
        System.out.println(max);
        assertThat(max.transform(PlainDate.axis()), is(PlainDate.of(3000, 1, 27)));
        assertThat(max.get(CommonElements.RELATED_GREGORIAN_YEAR), is(2999));
        assertThat(max.get(VietnameseCalendar.DAY_OF_MONTH), is(30));
        assertThat(max.get(VietnameseCalendar.DAY_OF_YEAR), is(355));
        assertThat(max.get(VietnameseCalendar.MONTH_OF_YEAR), is(EastAsianMonth.valueOf(12)));
        assertThat(max.get(VietnameseCalendar.CYCLE), is(94));
    }

    @Test
    public void caSupport() {
        Locale locale = Locale.forLanguageTag("en-u-ca-vietnam");
        ChronoFormatter<CalendarDate> f = ChronoFormatter.ofGenericCalendarStyle(DisplayMode.FULL, locale);
        assertThat(
            f.format(PlainDate.of(2017, 10, 1)),
            is("Sunday, M08 12, 2017(dīng-yǒu)"));
    }

}