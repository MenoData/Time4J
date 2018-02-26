package net.time4j.calendar;

import net.time4j.ClockUnit;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;
import net.time4j.Weekday;
import net.time4j.calendar.astro.MoonPhase;
import net.time4j.engine.CalendarDate;
import net.time4j.engine.EpochDays;
import net.time4j.format.DisplayMode;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
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
    public void qingDynastyShunzhi() {
        ChineseCalendar min = ChineseCalendar.axis().getMinimum();
        assertThat(min.get(ChineseCalendar.ERA), is(ChineseEra.QING_SHUNZHI_1644_1662));
        assertThat(min.get(ChineseCalendar.YEAR_OF_ERA), is(2));
    }

    @Test
    public void qingDynastiesStart() {
        int[] years = {1662, 1723, 1736, 1796, 1821, 1851, 1862, 1875, 1909};
        PlainDate[] dates = {
            PlainDate.of(1662, 2, 18),
            PlainDate.of(1723, 2, 5),
            PlainDate.of(1736, 2, 12),
            PlainDate.of(1796, 2, 9),
            PlainDate.of(1821, 2, 3),
            PlainDate.of(1851, 2, 1),
            PlainDate.of(1862, 1, 30),
            PlainDate.of(1875, 2, 6),
            PlainDate.of(1909, 1, 22)
        };
        for (int i = 0; i < years.length; i++) {
            ChineseCalendar cc = ChineseCalendar.ofNewYear(years[i]);
            assertThat(
                cc.transform(PlainDate.axis()),
                is(dates[i]));
            assertThat(
                cc.get(ChineseCalendar.YEAR_OF_ERA),
                is(1));
            assertThat(
                cc.get(ChineseCalendar.ERA),
                is(ChineseEra.values()[i + 1]));
        }
    }

    @Test
    public void qingDynastyLast() {
        ChineseCalendar last = PlainDate.of(1912, 2, 11).transform(ChineseCalendar.axis());
        ChineseCalendar next = last.plus(1, ChineseCalendar.Unit.DAYS); // day of abdication
        assertThat(last.get(ChineseCalendar.ERA), is(ChineseEra.QING_XUANTONG_1909_1912));
        assertThat(last.get(ChineseCalendar.YEAR_OF_ERA), is(3));
        assertThat(next.get(ChineseCalendar.ERA), is(ChineseEra.YELLOW_EMPEROR));
        assertThat(next.get(ChineseCalendar.YEAR_OF_ERA), is(1911 + 2698));
    }

    @Test
    public void parseEraAndYearOfEra() throws ParseException {
        ChronoFormatter<ChineseCalendar> f =
            ChronoFormatter.ofPattern("G yyyy, MMM/d", PatternType.CLDR, Locale.ENGLISH, ChineseCalendar.axis());
        ChineseCalendar cc = f.parse("Huángdì 4696, M1/1");
        assertThat(cc.get(ChineseCalendar.ERA), is(ChineseEra.YELLOW_EMPEROR));
        assertThat(cc.get(ChineseCalendar.YEAR_OF_ERA), is(4696)); // year-counting of Sun-yat-sen
        assertThat(f.parse("Huangdi 4696, M1/1"), is(cc)); // root locale as fallback
        PlainDate d = cc.transform(PlainDate.axis());
        assertThat(d, is(PlainDate.of(1998, 1, 28)));
        assertThat(f.format(cc), is("Huángdì 4696, M1/1"));
    }

    @Test
    public void caSupport() {
        Locale locale = Locale.forLanguageTag("en-u-ca-chinese");
        ChronoFormatter<CalendarDate> f = ChronoFormatter.ofGenericCalendarStyle(DisplayMode.FULL, locale);
        assertThat(
            f.format(PlainDate.of(2020, 5, 24)),
            is("Sunday, (leap) M04 2, 2020(gēng-zǐ)"));
    }

    @Test
    public void newYear() {
        ChineseCalendar c = ChineseCalendar.ofNewYear(1968);
        assertThat(
            c.transform(PlainDate.axis()),
            is(PlainDate.of(1968, 1, 30)));
        // Tet-festival in South Vietnam, see also: http://www.math.nus.edu.sg/aslaksen/calendar/cal.pdf (page 29)
    }

    @Test
    public void hongkongObservatory2057() {
        ZonalOffset offset = ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 8);
        Moment newMoon = MoonPhase.NEW_MOON.atOrAfter(PlainTimestamp.of(2057, 9, 27, 0, 0).at(offset));
        assertThat(
            ClockUnit.SECONDS.between(
                PlainDate.of(2057, 9, 29).atStartOfDay(),
                newMoon.toZonalTimestamp(offset) // 2057-09-29T00:00:37
            ) <= 120L,
        is(true));
    }

    @Test
    public void hongkongObservatory2097() {
        ZonalOffset offset = ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 8);
        Moment newMoon = MoonPhase.NEW_MOON.atOrAfter(PlainTimestamp.of(2097, 8, 6, 0, 0).at(offset));
        assertThat(
            ClockUnit.SECONDS.between(
                PlainDate.of(2097, 8, 8).atStartOfDay(),
                newMoon.toZonalTimestamp(offset) // 2097-08-08T00:01:47
            ) <= 120L,
            is(true));
    }

}