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

/* Generating method for leap month infos

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
                new OutputStreamWriter(new FileOutputStream("C:\\work\\chinese-leap.txt"), "UTF-8")
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
*/

    @Test
    public void minmax(){
        ChineseCalendar min = ChineseCalendar.axis().getMinimum();
        assertThat(min.getCycle(), is(72));
        assertThat(min.getYear().getNumber(), is(22));
        assertThat(min.get(ChineseCalendar.CYCLE), is(72));
        assertThat(min.getMinimum(ChineseCalendar.CYCLE), is(72));
        assertThat(min.getMinimum(ChineseCalendar.YEAR_OF_CYCLE), is(CyclicYear.of(22)));
        assertThat(min.getMaximum(ChineseCalendar.YEAR_OF_CYCLE), is(CyclicYear.of(60)));
        System.out.println(min);

        ChineseCalendar max = ChineseCalendar.axis().getMaximum();
        assertThat(max.getCycle(), is(94));
        assertThat(max.getYear().getNumber(), is(56));
        assertThat(max.get(ChineseCalendar.CYCLE), is(94));
        assertThat(max.getMaximum(ChineseCalendar.CYCLE), is(94));
        assertThat(max.getMinimum(ChineseCalendar.YEAR_OF_CYCLE), is(CyclicYear.of(1)));
        assertThat(max.getMaximum(ChineseCalendar.YEAR_OF_CYCLE), is(CyclicYear.of(56)));
        System.out.println(max);
    }

    @Test
    public void yongzheng() {
        PlainDate d = PlainDate.of(1723, 7, 1);
        ChineseCalendar cc = d.transform(ChineseCalendar.axis()).with(ChineseCalendar.DAY_OF_YEAR, 1);
        System.out.println(cc); // chinese[40(1723)-1-01]
        System.out.println(cc.transform(PlainDate.axis())); // 1723-02-05
        System.out.println(EastAsianYear.forGregorian(2018).getElapsedCyclicYears() + 1); // 4655 = 4716 - 60 - 1
        System.out.println(EastAsianYear.forGregorian(1998).getElapsedCyclicYears() + 1); // 4635
        System.out.println(EastAsianYear.forGregorian(-2636).getElapsedCyclicYears()); // 0

        int[] years = {1662, 1723, 1736, 1796, 1821, 1851, 1862, 1875, 1909, 1912};
        for (int i = 0; i < years.length; i++) {
            System.out.println(
                ChineseCalendar.of(EastAsianYear.forGregorian(years[i]), EastAsianMonth.valueOf(1), 1)
                    .transform(PlainDate.axis())
            );
        }
        System.out.println();
        System.out.println(PlainDate.of(1912, 2, 12).getDaysSinceEpochUTC());

/*
        1662-02-18
        1723-02-05
        1736-02-12
        1796-02-09
        1821-02-03
        1851-02-01
        1862-01-30
        1875-02-06
        1909-01-22
        1912-02-18
*/
    }

    @Test
    public void caSupport() {
        Locale locale = Locale.forLanguageTag("en-u-ca-chinese");
        ChronoFormatter<CalendarDate> f = ChronoFormatter.ofGenericCalendarStyle(DisplayMode.FULL, locale);
        assertThat(
            f.format(PlainDate.of(2017, 10, 1)),
            is("Sunday, M08 12, 2017(dīng-yǒu)"));
    }

}