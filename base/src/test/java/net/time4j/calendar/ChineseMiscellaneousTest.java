package net.time4j.calendar;

import net.time4j.ClockUnit;
import net.time4j.GeneralTimestamp;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.Weekday;
import net.time4j.calendar.astro.MoonPhase;
import net.time4j.engine.CalendarDate;
import net.time4j.engine.CalendarDays;
import net.time4j.engine.EpochDays;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import net.time4j.engine.AttributeQuery;
import net.time4j.format.Attributes;
import net.time4j.format.NumberSystem;
import net.time4j.format.expert.ChronoParser;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


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
                assertThat(cal.isLeapYear(), is(true));
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
        ChineseCalendar cc = f.parse("Huángdì 4696, Mo1/1");
        assertThat(cc.get(ChineseCalendar.ERA), is(ChineseEra.YELLOW_EMPEROR));
        assertThat(cc.get(ChineseCalendar.YEAR_OF_ERA), is(4696)); // year-counting of Sun-yat-sen
        assertThat(f.parse("Huangdi 4696, Mo1/1"), is(cc)); // root locale as fallback
        PlainDate d = cc.transform(PlainDate.axis());
        assertThat(d, is(PlainDate.of(1998, 1, 28)));
        assertThat(f.format(cc), is("Huángdì 4696, Mo1/1"));
    }

    @Test
    public void findLeapMonth() {
        for (int y = 2015; y < 2022; y++) {
            Optional<EastAsianMonth> m = ChineseCalendar.ofNewYear(y).findLeapMonth();
            if ((y == 2017) || (y == 2020)) {
                assertThat(m.isPresent() && m.get().isLeap(), is(true));
                int num = (y == 2017) ? 6 : 4;
                assertThat(m.get().getNumber(), is(num));
            } else {
                assertThat(m.isPresent(), is(false));
            }
        }
    }

    @Test
    public void withBeginOfNextLeapMonth() {
        ChineseCalendar cc1 = ChineseCalendar.ofNewYear(2017);
        cc1 = cc1.withBeginOfNextLeapMonth();
        assertThat(cc1.get(CommonElements.RELATED_GREGORIAN_YEAR), is(2017));
        assertThat(cc1.getMonth(), is(EastAsianMonth.valueOf(6).withLeap()));
        assertThat(cc1.getDayOfMonth(), is(1));

        ChineseCalendar cc2 = ChineseCalendar.ofNewYear(2018);
        cc2 = cc2.withBeginOfNextLeapMonth();
        assertThat(cc2.get(CommonElements.RELATED_GREGORIAN_YEAR), is(2020));
        assertThat(cc2.getMonth(), is(EastAsianMonth.valueOf(4).withLeap()));
        assertThat(cc2.getDayOfMonth(), is(1));
    }

    @Test
    public void customizedLeapMonthFormat() {
        ChronoFormatter<ChineseCalendar> f =
            ChronoFormatter.ofPattern("M/d, U(r)", PatternType.CLDR, Locale.ENGLISH, ChineseCalendar.axis())
                .with(EastAsianMonth.LEAP_MONTH_IS_TRAILING, true)
                .with(EastAsianMonth.LEAP_MONTH_INDICATOR, 'b');
        ChineseCalendar cc =
            ChineseCalendar.of(EastAsianYear.forGregorian(2020), EastAsianMonth.valueOf(4).withLeap(), 5);
        assertThat(f.format(cc), is("4b/5, " + cc.getYear().getDisplayName(Locale.ENGLISH) + "(2020)"));
    }

    @Test
    public void caSupport1() {
        Locale locale = Locale.forLanguageTag("en-u-ca-chinese");
        ChronoFormatter<CalendarDate> f = ChronoFormatter.ofGenericCalendarStyle(FormatStyle.FULL, locale);
        assertThat(
            f.format(PlainDate.of(2020, 5, 24)),
            is("Sunday, (leap) Fourth Month 2, 2020(gēng-zǐ)"));
    }

    @Test
    public void caSupport2() {
        Locale locale = Locale.forLanguageTag("ast-u-ca-chinese"); // r(U) MMMM d, EEEE
        ChronoFormatter<CalendarDate> f = ChronoFormatter.ofGenericCalendarStyle(FormatStyle.FULL, locale);
        assertThat(
            f.format(PlainDate.of(2020, 5, 24)),
            is("2020(gēng-zǐ) *mes 4 2, domingu"));
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

    @Test
    public void minusClockUnitsOnTimestamp() {
        ChineseCalendar cc =
            ChineseCalendar.of(EastAsianYear.forGregorian(2020), EastAsianMonth.valueOf(4).withLeap(), 1);
        ChineseCalendar expectedDate =
            ChineseCalendar.of(EastAsianYear.forGregorian(2020), EastAsianMonth.valueOf(4), 30);
        assertThat(
            cc.atTime(17, 45).minus(18, ClockUnit.HOURS),
            is(GeneralTimestamp.of(expectedDate, PlainTime.of(23, 45))));
    }

    @Test
    public void minusDaysOnTimestamp() {
        ChineseCalendar cc =
            ChineseCalendar.of(EastAsianYear.forGregorian(2020), EastAsianMonth.valueOf(4).withLeap(), 5);
        assertThat(
            cc.atTime(17, 45).minus(CalendarDays.ONE),
            is(GeneralTimestamp.of(cc.minus(CalendarDays.ONE), PlainTime.of(17, 45))));
    }

    @Test
    public void qingmingFestival() {
        ChineseCalendar qingming = ChineseCalendar.ofQingMing(2019);
        assertThat(qingming.transform(PlainDate.class), is(PlainDate.of(2019, 4, 5)));
    }

    @Test
    public void dragonBoatFestival() {
        ChineseCalendar dragonBoatFestival =
            ChineseCalendar.of(
                EastAsianYear.forGregorian(2009),
                EastAsianMonth.valueOf(5), // not a leap month
                5);
        assertThat(dragonBoatFestival.transform(PlainDate.axis()), is(PlainDate.of(2009, 5, 28)));
        assertThat(dragonBoatFestival.plus(1, ChineseCalendar.Unit.MONTHS).getMonth().isLeap(), is(true));
    }

    @Test
    public void solarTermOnOrAfter_MINOR_01_LICHUN_315() {
        ChineseCalendar gregorianNewYear = PlainDate.of(2021, 1, 1).transform(ChineseCalendar.class);
        assertThat(
            SolarTerm.MINOR_01_LICHUN_315.onOrAfter(gregorianNewYear).transform(PlainDate.class),
            is(PlainDate.of(2021, 2, 3)));
    }

    @Test
    public void solarTermList() {
        List<PlainDate> list =
            SolarTerm.list(2021, ChineseCalendar.axis())
                .stream()
                .map((d) -> d.transform(PlainDate.axis()))
                .collect(Collectors.toList());
        List<PlainDate> expected =
            Arrays.asList(
                PlainDate.of(2021, 2, 3),
                PlainDate.of(2021, 2, 18),
                PlainDate.of(2021, 3, 5),
                PlainDate.of(2021, 3, 20),
                PlainDate.of(2021, 4, 4),
                PlainDate.of(2021, 4, 20),
                PlainDate.of(2021, 5, 5),
                PlainDate.of(2021, 5, 21),
                PlainDate.of(2021, 6, 5),
                PlainDate.of(2021, 6, 21),
                PlainDate.of(2021, 7, 7),
                PlainDate.of(2021, 7, 22),
                PlainDate.of(2021, 8, 7),
                PlainDate.of(2021, 8, 23),
                PlainDate.of(2021, 9, 7),
                PlainDate.of(2021, 9, 23),
                PlainDate.of(2021, 10, 8),
                PlainDate.of(2021, 10, 23),
                PlainDate.of(2021, 11, 7),
                PlainDate.of(2021, 11, 22),
                PlainDate.of(2021, 12, 7),
                PlainDate.of(2021, 12, 21),
                PlainDate.of(2022, 1, 5),
                PlainDate.of(2022, 1, 20));
        assertThat(list, is(expected));
    }

    @Test
    public void solarTermSinceLichun() {
        ChineseCalendar gregorianNewYear = PlainDate.of(2021, 1, 1).transform(ChineseCalendar.axis());
        ChineseCalendar chineseNewYear = ChineseCalendar.ofNewYear(2021); // 2021-02-12
        ChineseCalendar beforeLichun21 = PlainDate.of(2021, 2, 2).transform(ChineseCalendar.axis());
        ChineseCalendar onLichun21 = PlainDate.of(2021, 2, 3).transform(ChineseCalendar.axis());
        assertThat(
            chineseNewYear.transform(PlainDate.axis()),
            is(PlainDate.of(2021, 2, 12)));
        assertThat(
            gregorianNewYear.with(SolarTerm.MINOR_01_LICHUN_315.sinceLichun()).transform(PlainDate.class),
            is(PlainDate.of(2021, 2, 3)));
        assertThat(
            chineseNewYear.with(SolarTerm.MINOR_01_LICHUN_315.sinceLichun()).transform(PlainDate.class),
            is(PlainDate.of(2021, 2, 3)));
        assertThat(
            beforeLichun21.with(SolarTerm.MINOR_01_LICHUN_315.sinceLichun()).transform(PlainDate.class),
            is(PlainDate.of(2021, 2, 3)));
        assertThat(
            onLichun21.with(SolarTerm.MINOR_01_LICHUN_315.sinceLichun()).transform(PlainDate.class),
            is(PlainDate.of(2021, 2, 3)));
        assertThat(
            onLichun21.with(SolarTerm.MINOR_01_LICHUN_315.sinceLichun()).transform(PlainDate.class),
            is(PlainDate.of(2021, 2, 3)));
    }

    @Test
    public void solarTermSinceNewYear() {
        ChineseCalendar chineseNewYear = ChineseCalendar.ofNewYear(2021); // 2021-02-12
        assertThat(
            chineseNewYear.with(SolarTerm.MINOR_01_LICHUN_315.sinceNewYear()).transform(PlainDate.class),
            is(PlainDate.of(2022, 2, 4)));    }

    @Test
    public void sexagesimalMonth() {
        PlainDate gregorian = PlainDate.of(2020, 7, 14);
        ChineseCalendar calendar = gregorian.transform(ChineseCalendar.class);
        SexagesimalName sn = calendar.getSexagesimalMonth();
        assertThat(sn.getStem(), is(SexagesimalName.Stem.GUI_10_WATER_YIN));
        assertThat(sn.getBranch(), is(SexagesimalName.Branch.WEI_8_SHEEP));
        assertThat(sn.getDisplayName(Locale.CHINESE), is("癸未"));

        sn = PlainDate.of(2020, 8, 6).transform(ChineseCalendar.axis()).getSexagesimalMonth();
        assertThat(sn.getStem(), is(SexagesimalName.Stem.GUI_10_WATER_YIN));
        assertThat(sn.getBranch(), is(SexagesimalName.Branch.WEI_8_SHEEP));

        sn = PlainDate.of(2020, 8, 7).transform(ChineseCalendar.axis()).getSexagesimalMonth();
        assertThat(sn.getStem(), is(SexagesimalName.Stem.JIA_1_WOOD_YANG));
        assertThat(sn.getBranch(), is(SexagesimalName.Branch.SHEN_9_MONKEY));
        assertThat(sn.getDisplayName(Locale.CHINESE), is("甲申"));

        sn = PlainDate.of(2020, 9, 6).transform(ChineseCalendar.axis()).getSexagesimalMonth();
        assertThat(sn.getStem(), is(SexagesimalName.Stem.JIA_1_WOOD_YANG));
        assertThat(sn.getBranch(), is(SexagesimalName.Branch.SHEN_9_MONKEY));

        sn = PlainDate.of(2020, 9, 7).transform(ChineseCalendar.axis()).getSexagesimalMonth();
        assertThat(sn.getStem(), is(SexagesimalName.Stem.YI_2_WOOD_YIN));
        assertThat(sn.getBranch(), is(SexagesimalName.Branch.YOU_10_FOWL));
        assertThat(sn.getDisplayName(Locale.CHINESE), is("乙酉"));

        sn = PlainDate.of(2020, 2, 4).transform(ChineseCalendar.axis()).getSexagesimalMonth();
        assertThat(sn.getStem(), is(SexagesimalName.Stem.WU_5_EARTH_YANG));
        assertThat(sn.getBranch(), is(SexagesimalName.Branch.YIN_3_TIGER));
        assertThat(sn.getDisplayName(Locale.CHINESE), is("戊寅"));

        sn = PlainDate.of(2020, 2, 3).transform(ChineseCalendar.axis()).getSexagesimalMonth();
        assertThat(sn.getStem(), is(SexagesimalName.Stem.DING_4_FIRE_YIN));
        assertThat(sn.getBranch(), is(SexagesimalName.Branch.CHOU_2_OX));
        assertThat(sn.getDisplayName(Locale.CHINESE), is("丁丑"));

        ChineseCalendar newYear = ChineseCalendar.ofNewYear(2020); // 2020-01-25
        sn = newYear.getSexagesimalMonth();
        assertThat(sn.getStem(), is(SexagesimalName.Stem.DING_4_FIRE_YIN));
        assertThat(sn.getBranch(), is(SexagesimalName.Branch.CHOU_2_OX));
        assertThat(sn.getDisplayName(Locale.CHINESE), is("丁丑"));

        sn = PlainDate.of(2020, 1, 6).transform(ChineseCalendar.axis()).getSexagesimalMonth();
        assertThat(sn.getStem(), is(SexagesimalName.Stem.DING_4_FIRE_YIN));
        assertThat(sn.getBranch(), is(SexagesimalName.Branch.CHOU_2_OX));
        assertThat(sn.getDisplayName(Locale.CHINESE), is("丁丑"));

        sn = PlainDate.of(2020, 1, 5).transform(ChineseCalendar.axis()).getSexagesimalMonth();
        assertThat(sn.getStem(), is(SexagesimalName.Stem.BING_3_FIRE_YANG));
        assertThat(sn.getBranch(), is(SexagesimalName.Branch.ZI_1_RAT));
        assertThat(sn.getDisplayName(Locale.CHINESE), is("丙子"));

        sn = PlainDate.of(2019, 12, 7).transform(ChineseCalendar.axis()).getSexagesimalMonth();
        assertThat(sn.getStem(), is(SexagesimalName.Stem.BING_3_FIRE_YANG));
        assertThat(sn.getBranch(), is(SexagesimalName.Branch.ZI_1_RAT));
        assertThat(sn.getDisplayName(Locale.CHINESE), is("丙子"));

        sn = PlainDate.of(2019, 12, 6).transform(ChineseCalendar.axis()).getSexagesimalMonth();
        assertThat(sn.getStem(), is(SexagesimalName.Stem.YI_2_WOOD_YIN));
        assertThat(sn.getBranch(), is(SexagesimalName.Branch.HAI_12_PIG));
        assertThat(sn.getDisplayName(Locale.CHINESE), is("乙亥"));

        ChineseCalendar newYear2 = ChineseCalendar.ofNewYear(2021); // 2021-02-12
        System.out.println(newYear2.transform(PlainDate.axis()));
        sn = newYear2.getSexagesimalMonth();
        assertThat(sn.getStem(), is(SexagesimalName.Stem.GENG_7_METAL_YANG));
        assertThat(sn.getBranch(), is(SexagesimalName.Branch.YIN_3_TIGER));
        assertThat(sn.getDisplayName(Locale.CHINESE), is("庚寅"));

        sn = PlainDate.of(2021, 3, 4).transform(ChineseCalendar.axis()).getSexagesimalMonth();
        assertThat(sn.getStem(), is(SexagesimalName.Stem.GENG_7_METAL_YANG));
        assertThat(sn.getBranch(), is(SexagesimalName.Branch.YIN_3_TIGER));
        assertThat(sn.getDisplayName(Locale.CHINESE), is("庚寅"));

        sn = PlainDate.of(2021, 3, 5).transform(ChineseCalendar.axis()).getSexagesimalMonth();
        assertThat(sn.getStem(), is(SexagesimalName.Stem.XIN_8_METAL_YIN));
        assertThat(sn.getBranch(), is(SexagesimalName.Branch.MAO_4_HARE));
        assertThat(sn.getDisplayName(Locale.CHINESE), is("辛卯"));

        sn = PlainDate.of(2021, 2, 11).transform(ChineseCalendar.axis()).getSexagesimalMonth();
        assertThat(sn.getStem(), is(SexagesimalName.Stem.GENG_7_METAL_YANG));
        assertThat(sn.getBranch(), is(SexagesimalName.Branch.YIN_3_TIGER));
        assertThat(sn.getDisplayName(Locale.CHINESE), is("庚寅"));

        sn = PlainDate.of(2021, 2, 3).transform(ChineseCalendar.axis()).getSexagesimalMonth();
        assertThat(sn.getStem(), is(SexagesimalName.Stem.GENG_7_METAL_YANG));
        assertThat(sn.getBranch(), is(SexagesimalName.Branch.YIN_3_TIGER));
        assertThat(sn.getDisplayName(Locale.CHINESE), is("庚寅"));

        sn = PlainDate.of(2021, 2, 2).transform(ChineseCalendar.axis()).getSexagesimalMonth();
        assertThat(sn.getStem(), is(SexagesimalName.Stem.JI_6_EARTH_YIN));
        assertThat(sn.getBranch(), is(SexagesimalName.Branch.CHOU_2_OX));
        assertThat(sn.getDisplayName(Locale.CHINESE), is("己丑"));

        sn = PlainDate.of(2020, 12, 7).transform(ChineseCalendar.axis()).getSexagesimalMonth();
        assertThat(sn.getStem(), is(SexagesimalName.Stem.WU_5_EARTH_YANG));
        assertThat(sn.getBranch(), is(SexagesimalName.Branch.ZI_1_RAT));
        assertThat(sn.getDisplayName(Locale.CHINESE), is("戊子"));

        // in contradiction to https://www.yourchineseastrology.com
        // but in agreement with data from Hongkong observatory,
        sn = PlainDate.of(2020, 12, 6).transform(ChineseCalendar.axis()).getSexagesimalMonth();
        assertThat(sn.getStem(), is(SexagesimalName.Stem.DING_4_FIRE_YIN));
        assertThat(sn.getBranch(), is(SexagesimalName.Branch.HAI_12_PIG));
        assertThat(sn.getDisplayName(Locale.CHINESE), is("丁亥"));
    }

    @Test
    public void sexagesimalMonthSanity() {
        ChineseCalendar min = ChineseCalendar.axis().getMinimum();
        ChineseCalendar max = ChineseCalendar.axis().getMaximum();
        ChineseCalendar cal = min;

        while (cal.getDaysSinceEpochUTC() <= max.getDaysSinceEpochUTC() - 30) {
            int num = cal.getSexagesimalMonth().getNumber();
            cal = cal.plus(1, ChineseCalendar.Unit.MONTHS);
            assertThat(num >= 1 && num <= 60, is(true));
        }
    }

    @Test
    public void sexagesimalDay() {
        PlainDate gregorian = PlainDate.of(1900, 2, 20);
        ChineseCalendar calendar = gregorian.transform(ChineseCalendar.class);
        SexagesimalName sn = calendar.getSexagesimalDay();
        assertThat(sn.getStem(), is(SexagesimalName.Stem.JIA_1_WOOD_YANG));
        assertThat(sn.getBranch(), is(SexagesimalName.Branch.ZI_1_RAT));
        assertThat(sn.getDisplayName(Locale.CHINESE), is("甲子"));

        ChineseCalendar next = calendar.plus(1, ChineseCalendar.Unit.DAYS);
        assertThat(next.getSexagesimalDay(), is(sn.roll(1)));

        ChineseCalendar later = calendar.plus(60, ChineseCalendar.Unit.DAYS);
        assertThat(later.transform(PlainDate.axis()), is(PlainDate.of(1900, 4, 21)));
        assertThat(later.getSexagesimalDay().getDisplayName(Locale.CHINESE), is("甲子"));

        // https://www.yourchineseastrology.com/calendar/2020/8.htm
        ChineseCalendar latest = PlainDate.of(2020, 8, 6).transform(ChineseCalendar.axis());
        assertThat(latest.getSexagesimalDay().getDisplayName(Locale.CHINESE), is("辛巳"));
    }

    @Test
    public void chineseLunarDays() {
        ChronoFormatter<ChineseCalendar> formatter =
            ChronoFormatter.setUp(ChineseCalendar.axis(), Locale.CHINA)
                .addPattern("r(U)MMMM", PatternType.CLDR_DATE)
                .startSection(Attributes.NUMBER_SYSTEM, NumberSystem.CHINESE_LUNAR_DAYS)
                .addPattern("d日(", PatternType.CLDR)
                .endSection()
                .addCustomized( // zodiac printer
                    ChineseCalendar.YEAR_OF_CYCLE, 
                    (CyclicYear year, StringBuilder buffer, AttributeQuery attrs) -> {
                        buffer.append(year.getZodiac(Locale.TRADITIONAL_CHINESE));
                        return Collections.emptySet();
                    },
                    ChronoParser.unsupported())
                .addLiteral(')')
                .build();
        ChineseCalendar chineseDate = ChineseCalendar.ofNewYear(2024);
        assertThat(
            formatter.format(chineseDate),
            is("2024(甲辰)正月初一日(龍)"));
        assertThat(
            formatter.format(chineseDate.plus(20, ChineseCalendar.Unit.DAYS)),
            is("2024(甲辰)正月廿一日(龍)"));
    }

}