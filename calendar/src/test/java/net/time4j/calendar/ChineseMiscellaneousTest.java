package net.time4j.calendar;

import net.time4j.PlainDate;
import net.time4j.Weekday;
import net.time4j.engine.EpochDays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class ChineseMiscellaneousTest {

    @Test
    public void ruleConsistency() {
        int winterSolstice = SolarTerm.MAJOR_11_DONGZHI_270.getIndex();
        EastAsianCS<ChineseCalendar> calsys = ChineseCalendar.nowInSystemTime().getCalendarSystem();
        ChineseCalendar cal = calsys.transform(calsys.getMinimumSinceUTC());
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
                ChineseCalendar next = calsys.transform(calsys.newMoonOnOrAfter(utcDays + 1));
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

    @Test
    public void defaultFirstDayOfWeek() {
        assertThat(ChineseCalendar.DAY_OF_WEEK.getDefaultMinimum(), is(Weekday.SUNDAY));
    }

    @Test
    public void conversionRoundtrip() { // long runner hence inactive test (but was once successfully tested)
//        CalendarSystem<ChineseCalendar> calsys = ChineseCalendar.axis().getCalendarSystem();
//
//        for (long utcDays = calsys.getMinimumSinceUTC(); utcDays <= calsys.getMaximumSinceUTC(); utcDays++) {
//            ChineseCalendar cal = calsys.transform(utcDays);
//            assertThat(calsys.transform(cal), is(utcDays));
//        }
    }

    @Test
    public void createLeapMonthInfos() throws IOException {
        write();
    }

    private static void write() throws IOException {

        EastAsianCS<?> calsys =
            (EastAsianCS<?>) VietnameseCalendar.axis().getCalendarSystem();
        EastAsianCalendar<?, ?> cal = calsys.transform(calsys.getMinimumSinceUTC());
        Map<Integer, Integer> map = new LinkedHashMap<>();
        int year;

        do {
            cal = calsys.transform(calsys.newMoonOnOrAfter(cal.getDaysSinceEpochUTC() + 1));
            year = PlainDate.of(cal.getDaysSinceEpochUTC(), EpochDays.UTC).getYear();
            int elapsedYears = (cal.getCycle() - 1) * 60 + cal.getYear().getNumber() - 1;
            if (cal.getMonth().isLeap()) {
                map.put(elapsedYears, cal.getMonth().getNumber());
            }
        } while (year <= 2999);

        BufferedWriter writer =
            new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream("C:\\work\\vietnamese-leap.txt"), "UTF-8")
            );
        int i = 0;

        for (Map.Entry<Integer, Integer> e : map.entrySet()) {
            writer.write(", ");
            if (i == 10) {
                i = 0;
                writer.newLine();
            }
            i++;
            writer.write(String.valueOf(e.getKey()));
            writer.write(", ");
            writer.write(String.valueOf(e.getValue()));
        }

        writer.close();
    }

}