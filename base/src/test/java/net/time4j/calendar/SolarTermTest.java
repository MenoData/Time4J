package net.time4j.calendar;

import net.time4j.PlainDate;
import net.time4j.calendar.astro.AstronomicalSeason;
import net.time4j.engine.CalendarDays;
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
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class SolarTermTest {

    @Test
    public void getDisplayNameChinese() {
        assertThat(
            SolarTerm.MAJOR_11_DONGZHI_270.getDisplayName(Locale.CHINA),
            is("冬至"));
    }

    @Test
    public void parseChinese() throws ParseException {
        assertThat(
            SolarTerm.parse("冬至", Locale.CHINA),
            is(SolarTerm.MAJOR_11_DONGZHI_270));
    }

    @Test
    public void parseEnglish() throws ParseException {
        assertThat(
            SolarTerm.parse("dōngzhì", Locale.ENGLISH),
            is(SolarTerm.MAJOR_11_DONGZHI_270));
        assertThat(
            SolarTerm.parse("Dongzhi", Locale.ENGLISH), // case-insensitive search
            is(SolarTerm.MAJOR_11_DONGZHI_270));
    }

    @Test
    public void roll() {
        assertThat(
            SolarTerm.MAJOR_11_DONGZHI_270.roll(7),
            is(SolarTerm.MINOR_03_QINGMING_015));
    }

    @Test
    public void ofMajor() {
        assertThat(SolarTerm.ofMajor(1), is(SolarTerm.MAJOR_01_YUSHUI_330));
        assertThat(SolarTerm.ofMajor(2), is(SolarTerm.MAJOR_02_CHUNFEN_000));
        assertThat(SolarTerm.ofMajor(3), is(SolarTerm.MAJOR_03_GUYU_030));
        assertThat(SolarTerm.ofMajor(4), is(SolarTerm.MAJOR_04_XIAOMAN_060));
        assertThat(SolarTerm.ofMajor(5), is(SolarTerm.MAJOR_05_XIAZHI_090));
        assertThat(SolarTerm.ofMajor(6), is(SolarTerm.MAJOR_06_DASHU_120));
        assertThat(SolarTerm.ofMajor(7), is(SolarTerm.MAJOR_07_CHUSHU_150));
        assertThat(SolarTerm.ofMajor(8), is(SolarTerm.MAJOR_08_QIUFEN_180));
        assertThat(SolarTerm.ofMajor(9), is(SolarTerm.MAJOR_09_SHUANGJIANG_210));
        assertThat(SolarTerm.ofMajor(10), is(SolarTerm.MAJOR_10_XIAOXUE_240));
        assertThat(SolarTerm.ofMajor(11), is(SolarTerm.MAJOR_11_DONGZHI_270));
        assertThat(SolarTerm.ofMajor(12), is(SolarTerm.MAJOR_12_DAHAN_300));
    }

    @Test
    public void isMajor() {
        assertThat(SolarTerm.MINOR_01_LICHUN_315.isMajor(), is(false));
        assertThat(SolarTerm.MINOR_02_JINGZHE_345.isMajor(), is(false));
        assertThat(SolarTerm.MINOR_03_QINGMING_015.isMajor(), is(false));
        assertThat(SolarTerm.MINOR_04_LIXIA_045.isMajor(), is(false));
        assertThat(SolarTerm.MINOR_05_MANGZHONG_075.isMajor(), is(false));
        assertThat(SolarTerm.MINOR_06_XIAOSHU_105.isMajor(), is(false));
        assertThat(SolarTerm.MINOR_07_LIQIU_135.isMajor(), is(false));
        assertThat(SolarTerm.MINOR_08_BAILU_165.isMajor(), is(false));
        assertThat(SolarTerm.MINOR_09_HANLU_195.isMajor(), is(false));
        assertThat(SolarTerm.MINOR_10_LIDONG_225.isMajor(), is(false));
        assertThat(SolarTerm.MINOR_11_DAXUE_255.isMajor(), is(false));
        assertThat(SolarTerm.MINOR_12_XIAOHAN_285.isMajor(), is(false));

        assertThat(SolarTerm.MAJOR_01_YUSHUI_330.isMajor(), is(true));
        assertThat(SolarTerm.MAJOR_02_CHUNFEN_000.isMajor(), is(true));
        assertThat(SolarTerm.MAJOR_03_GUYU_030.isMajor(), is(true));
        assertThat(SolarTerm.MAJOR_04_XIAOMAN_060.isMajor(), is(true));
        assertThat(SolarTerm.MAJOR_05_XIAZHI_090.isMajor(), is(true));
        assertThat(SolarTerm.MAJOR_06_DASHU_120.isMajor(), is(true));
        assertThat(SolarTerm.MAJOR_07_CHUSHU_150.isMajor(), is(true));
        assertThat(SolarTerm.MAJOR_08_QIUFEN_180.isMajor(), is(true));
        assertThat(SolarTerm.MAJOR_09_SHUANGJIANG_210.isMajor(), is(true));
        assertThat(SolarTerm.MAJOR_10_XIAOXUE_240.isMajor(), is(true));
        assertThat(SolarTerm.MAJOR_11_DONGZHI_270.isMajor(), is(true));
        assertThat(SolarTerm.MAJOR_12_DAHAN_300.isMajor(), is(true));
    }

    @Test
    public void ofMinor() {
        assertThat(SolarTerm.ofMinor(1), is(SolarTerm.MINOR_01_LICHUN_315));
        assertThat(SolarTerm.ofMinor(2), is(SolarTerm.MINOR_02_JINGZHE_345));
        assertThat(SolarTerm.ofMinor(3), is(SolarTerm.MINOR_03_QINGMING_015));
        assertThat(SolarTerm.ofMinor(4), is(SolarTerm.MINOR_04_LIXIA_045));
        assertThat(SolarTerm.ofMinor(5), is(SolarTerm.MINOR_05_MANGZHONG_075));
        assertThat(SolarTerm.ofMinor(6), is(SolarTerm.MINOR_06_XIAOSHU_105));
        assertThat(SolarTerm.ofMinor(7), is(SolarTerm.MINOR_07_LIQIU_135));
        assertThat(SolarTerm.ofMinor(8), is(SolarTerm.MINOR_08_BAILU_165));
        assertThat(SolarTerm.ofMinor(9), is(SolarTerm.MINOR_09_HANLU_195));
        assertThat(SolarTerm.ofMinor(10), is(SolarTerm.MINOR_10_LIDONG_225));
        assertThat(SolarTerm.ofMinor(11), is(SolarTerm.MINOR_11_DAXUE_255));
        assertThat(SolarTerm.ofMinor(12), is(SolarTerm.MINOR_12_XIAOHAN_285));
    }

    @Test
    public void isMinor() {
        assertThat(SolarTerm.MINOR_01_LICHUN_315.isMinor(), is(true));
        assertThat(SolarTerm.MINOR_02_JINGZHE_345.isMinor(), is(true));
        assertThat(SolarTerm.MINOR_03_QINGMING_015.isMinor(), is(true));
        assertThat(SolarTerm.MINOR_04_LIXIA_045.isMinor(), is(true));
        assertThat(SolarTerm.MINOR_05_MANGZHONG_075.isMinor(), is(true));
        assertThat(SolarTerm.MINOR_06_XIAOSHU_105.isMinor(), is(true));
        assertThat(SolarTerm.MINOR_07_LIQIU_135.isMinor(), is(true));
        assertThat(SolarTerm.MINOR_08_BAILU_165.isMinor(), is(true));
        assertThat(SolarTerm.MINOR_09_HANLU_195.isMinor(), is(true));
        assertThat(SolarTerm.MINOR_10_LIDONG_225.isMinor(), is(true));
        assertThat(SolarTerm.MINOR_11_DAXUE_255.isMinor(), is(true));
        assertThat(SolarTerm.MINOR_12_XIAOHAN_285.isMinor(), is(true));

        assertThat(SolarTerm.MAJOR_01_YUSHUI_330.isMinor(), is(false));
        assertThat(SolarTerm.MAJOR_02_CHUNFEN_000.isMinor(), is(false));
        assertThat(SolarTerm.MAJOR_03_GUYU_030.isMinor(), is(false));
        assertThat(SolarTerm.MAJOR_04_XIAOMAN_060.isMinor(), is(false));
        assertThat(SolarTerm.MAJOR_05_XIAZHI_090.isMinor(), is(false));
        assertThat(SolarTerm.MAJOR_06_DASHU_120.isMinor(), is(false));
        assertThat(SolarTerm.MAJOR_07_CHUSHU_150.isMinor(), is(false));
        assertThat(SolarTerm.MAJOR_08_QIUFEN_180.isMinor(), is(false));
        assertThat(SolarTerm.MAJOR_09_SHUANGJIANG_210.isMinor(), is(false));
        assertThat(SolarTerm.MAJOR_10_XIAOXUE_240.isMinor(), is(false));
        assertThat(SolarTerm.MAJOR_11_DONGZHI_270.isMinor(), is(false));
        assertThat(SolarTerm.MAJOR_12_DAHAN_300.isMinor(), is(false));
    }

    @Test
    public void getIndex() {
        assertThat(SolarTerm.MINOR_01_LICHUN_315.getIndex(), is(1));
        assertThat(SolarTerm.MINOR_02_JINGZHE_345.getIndex(), is(2));
        assertThat(SolarTerm.MINOR_03_QINGMING_015.getIndex(), is(3));
        assertThat(SolarTerm.MINOR_04_LIXIA_045.getIndex(), is(4));
        assertThat(SolarTerm.MINOR_05_MANGZHONG_075.getIndex(), is(5));
        assertThat(SolarTerm.MINOR_06_XIAOSHU_105.getIndex(), is(6));
        assertThat(SolarTerm.MINOR_07_LIQIU_135.getIndex(), is(7));
        assertThat(SolarTerm.MINOR_08_BAILU_165.getIndex(), is(8));
        assertThat(SolarTerm.MINOR_09_HANLU_195.getIndex(), is(9));
        assertThat(SolarTerm.MINOR_10_LIDONG_225.getIndex(), is(10));
        assertThat(SolarTerm.MINOR_11_DAXUE_255.getIndex(), is(11));
        assertThat(SolarTerm.MINOR_12_XIAOHAN_285.getIndex(), is(12));

        assertThat(SolarTerm.MAJOR_01_YUSHUI_330.getIndex(), is(1));
        assertThat(SolarTerm.MAJOR_02_CHUNFEN_000.getIndex(), is(2));
        assertThat(SolarTerm.MAJOR_03_GUYU_030.getIndex(), is(3));
        assertThat(SolarTerm.MAJOR_04_XIAOMAN_060.getIndex(), is(4));
        assertThat(SolarTerm.MAJOR_05_XIAZHI_090.getIndex(), is(5));
        assertThat(SolarTerm.MAJOR_06_DASHU_120.getIndex(), is(6));
        assertThat(SolarTerm.MAJOR_07_CHUSHU_150.getIndex(), is(7));
        assertThat(SolarTerm.MAJOR_08_QIUFEN_180.getIndex(), is(8));
        assertThat(SolarTerm.MAJOR_09_SHUANGJIANG_210.getIndex(), is(9));
        assertThat(SolarTerm.MAJOR_10_XIAOXUE_240.getIndex(), is(10));
        assertThat(SolarTerm.MAJOR_11_DONGZHI_270.getIndex(), is(11));
        assertThat(SolarTerm.MAJOR_12_DAHAN_300.getIndex(), is(12));
    }

    @Test
    public void getSolarLongitude() {
        assertThat(SolarTerm.MINOR_01_LICHUN_315.getSolarLongitude(), is(315));
        assertThat(SolarTerm.MINOR_02_JINGZHE_345.getSolarLongitude(), is(345));
        assertThat(SolarTerm.MINOR_03_QINGMING_015.getSolarLongitude(), is(15));
        assertThat(SolarTerm.MINOR_04_LIXIA_045.getSolarLongitude(), is(45));
        assertThat(SolarTerm.MINOR_05_MANGZHONG_075.getSolarLongitude(), is(75));
        assertThat(SolarTerm.MINOR_06_XIAOSHU_105.getSolarLongitude(), is(105));
        assertThat(SolarTerm.MINOR_07_LIQIU_135.getSolarLongitude(), is(135));
        assertThat(SolarTerm.MINOR_08_BAILU_165.getSolarLongitude(), is(165));
        assertThat(SolarTerm.MINOR_09_HANLU_195.getSolarLongitude(), is(195));
        assertThat(SolarTerm.MINOR_10_LIDONG_225.getSolarLongitude(), is(225));
        assertThat(SolarTerm.MINOR_11_DAXUE_255.getSolarLongitude(), is(255));
        assertThat(SolarTerm.MINOR_12_XIAOHAN_285.getSolarLongitude(), is(285));

        assertThat(SolarTerm.MAJOR_01_YUSHUI_330.getSolarLongitude(), is(330));
        assertThat(SolarTerm.MAJOR_02_CHUNFEN_000.getSolarLongitude(), is(0));
        assertThat(SolarTerm.MAJOR_03_GUYU_030.getSolarLongitude(), is(30));
        assertThat(SolarTerm.MAJOR_04_XIAOMAN_060.getSolarLongitude(), is(60));
        assertThat(SolarTerm.MAJOR_05_XIAZHI_090.getSolarLongitude(), is(90));
        assertThat(SolarTerm.MAJOR_06_DASHU_120.getSolarLongitude(), is(120));
        assertThat(SolarTerm.MAJOR_07_CHUSHU_150.getSolarLongitude(), is(150));
        assertThat(SolarTerm.MAJOR_08_QIUFEN_180.getSolarLongitude(), is(180));
        assertThat(SolarTerm.MAJOR_09_SHUANGJIANG_210.getSolarLongitude(), is(210));
        assertThat(SolarTerm.MAJOR_10_XIAOXUE_240.getSolarLongitude(), is(240));
        assertThat(SolarTerm.MAJOR_11_DONGZHI_270.getSolarLongitude(), is(270));
        assertThat(SolarTerm.MAJOR_12_DAHAN_300.getSolarLongitude(), is(300));
    }

    @Test
    public void getSolarTerm() {
        assertThat(
            PlainDate.of(1989, 12, 21).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_11_DAXUE_255));
        assertThat(
            PlainDate.of(1989, 12, 22).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_11_DONGZHI_270));
        assertThat(
            AstronomicalSeason.WINTER_SOLSTICE
                .inYear(1989)
                .toZonalTimestamp(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 8))
                .getCalendarDate(),
            is(PlainDate.of(1989, 12, 22)));

        assertThat(
            PlainDate.of(2018, 1, 4).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_11_DONGZHI_270));
        assertThat(
            PlainDate.of(2018, 1, 5).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_12_XIAOHAN_285));
        assertThat(
            PlainDate.of(2018, 1, 19).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_12_XIAOHAN_285));
        assertThat(
            PlainDate.of(2018, 1, 20).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_12_DAHAN_300));

        assertThat(
            PlainDate.of(2018, 2, 3).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_12_DAHAN_300));
        assertThat(
            PlainDate.of(2018, 2, 4).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_01_LICHUN_315));
        assertThat(
            PlainDate.of(2018, 2, 18).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_01_LICHUN_315));
        assertThat(
            PlainDate.of(2018, 2, 19).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_01_YUSHUI_330));

        assertThat(
            PlainDate.of(2018, 3, 4).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_01_YUSHUI_330));
        assertThat(
            PlainDate.of(2018, 3, 5).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_02_JINGZHE_345));
        assertThat(
            PlainDate.of(2018, 3, 20).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_02_JINGZHE_345));
        assertThat(
            PlainDate.of(2018, 3, 21).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_02_CHUNFEN_000));
        assertThat(
            AstronomicalSeason.VERNAL_EQUINOX
                .inYear(2018)
                .toZonalTimestamp(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 8))
                .getCalendarDate(),
            is(PlainDate.of(2018, 3, 21)));

        assertThat(
            PlainDate.of(2018, 4, 4).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_02_CHUNFEN_000));
        assertThat(
            PlainDate.of(2018, 4, 5).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_03_QINGMING_015));
        assertThat(
            PlainDate.of(2018, 4, 19).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_03_QINGMING_015));
        assertThat(
            PlainDate.of(2018, 4, 20).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_03_GUYU_030));

        assertThat(
            PlainDate.of(2018, 5, 4).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_03_GUYU_030));
        assertThat(
            PlainDate.of(2018, 5, 5).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_04_LIXIA_045));
        assertThat(
            PlainDate.of(2018, 5, 20).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_04_LIXIA_045));
        assertThat(
            PlainDate.of(2018, 5, 21).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_04_XIAOMAN_060));

        assertThat(
            PlainDate.of(2018, 6, 5).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_04_XIAOMAN_060));
        assertThat(
            PlainDate.of(2018, 6, 6).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_05_MANGZHONG_075));
        assertThat(
            PlainDate.of(2018, 6, 20).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_05_MANGZHONG_075));
        assertThat(
            PlainDate.of(2018, 6, 21).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_05_XIAZHI_090));
        assertThat(
            AstronomicalSeason.SUMMER_SOLSTICE
                .inYear(2018)
                .toZonalTimestamp(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 8))
                .getCalendarDate(),
            is(PlainDate.of(2018, 6, 21)));

        assertThat(
            PlainDate.of(2018, 7, 6).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_05_XIAZHI_090));
        assertThat(
            PlainDate.of(2018, 7, 7).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_06_XIAOSHU_105));
        assertThat(
            PlainDate.of(2018, 7, 22).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_06_XIAOSHU_105));
        assertThat(
            PlainDate.of(2018, 7, 23).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_06_DASHU_120));

        assertThat(
            PlainDate.of(2018, 8, 6).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_06_DASHU_120));
        assertThat(
            PlainDate.of(2018, 8, 7).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_07_LIQIU_135));
        assertThat(
            PlainDate.of(2018, 8, 22).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_07_LIQIU_135));
        assertThat(
            PlainDate.of(2018, 8, 23).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_07_CHUSHU_150));

        assertThat(
            PlainDate.of(2018, 9, 7).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_07_CHUSHU_150));
        assertThat(
            PlainDate.of(2018, 9, 8).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_08_BAILU_165));
        assertThat(
            PlainDate.of(2018, 9, 22).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_08_BAILU_165));
        assertThat(
            PlainDate.of(2018, 9, 23).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_08_QIUFEN_180));
        assertThat(
            AstronomicalSeason.AUTUMNAL_EQUINOX
                .inYear(2018)
                .toZonalTimestamp(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 8))
                .getCalendarDate(),
            is(PlainDate.of(2018, 9, 23)));

        assertThat(
            PlainDate.of(2018, 10, 7).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_08_QIUFEN_180));
        assertThat(
            PlainDate.of(2018, 10, 8).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_09_HANLU_195));
        assertThat(
            PlainDate.of(2018, 10, 22).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_09_HANLU_195));
        assertThat(
            PlainDate.of(2018, 10, 23).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_09_SHUANGJIANG_210));

        assertThat(
            PlainDate.of(2018, 11, 6).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_09_SHUANGJIANG_210));
        assertThat(
            PlainDate.of(2018, 11, 7).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_10_LIDONG_225));
        assertThat(
            PlainDate.of(2018, 11, 21).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_10_LIDONG_225));
        assertThat(
            PlainDate.of(2018, 11, 22).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_10_XIAOXUE_240));

        assertThat(
            PlainDate.of(2018, 12, 6).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_10_XIAOXUE_240));
        assertThat(
            PlainDate.of(2018, 12, 7).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_11_DAXUE_255));
        assertThat(
            PlainDate.of(2018, 12, 21).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_11_DAXUE_255));
        assertThat(
            PlainDate.of(2018, 12, 22).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_11_DONGZHI_270));
        assertThat(
            AstronomicalSeason.WINTER_SOLSTICE
                .inYear(2018)
                .toZonalTimestamp(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 8))
                .getCalendarDate(),
            is(PlainDate.of(2018, 12, 22)));
    }

    @Test
    public void format() throws ParseException {
        ChronoFormatter<ChineseCalendar> formatter =
            ChronoFormatter.setUp(ChineseCalendar.axis(), Locale.ENGLISH)
                .addPattern("EEE, d. MMMM r(U) ", PatternType.CLDR_DATE)
                .addText(ChineseCalendar.SOLAR_TERM)
                .build();
        PlainDate winter =
            AstronomicalSeason.WINTER_SOLSTICE
                .inYear(2018)
                .toZonalTimestamp(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 8))
                .getCalendarDate();
        ChineseCalendar chineseDate = winter.transform(ChineseCalendar.class);
        assertThat(
            formatter.with(Locale.CHINESE).parse("周六, 16. 十一月 2018(戊戌) 冬至"),
            is(chineseDate));
        assertThat(
            formatter.with(Locale.CHINESE).format(chineseDate),
            is("周六, 16. 十一月 2018(戊戌) 冬至"));
        assertThat(
            formatter.format(chineseDate),
            is("Sat, 16. Eleventh Month 2018(wù-xū) dōngzhì"));
    }

    @Test
    public void onOrAfter() {
        ChineseCalendar date = PlainDate.of(2017, 12, 22).transform(ChineseCalendar.axis());
        assertThat(
            SolarTerm.MAJOR_11_DONGZHI_270.onOrAfter(date),
            is(date));
        assertThat(
            SolarTerm.MAJOR_11_DONGZHI_270.onOrAfter(date.plus(CalendarDays.ONE)),
            is(PlainDate.of(2018, 12, 22).transform(ChineseCalendar.axis())));
    }

    @Test
    public void isValidNull() {
        ChineseCalendar date = PlainDate.of(2017, 12, 22).transform(ChineseCalendar.axis());
        assertThat(date.isValid(ChineseCalendar.SOLAR_TERM, null), is(false));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withNull() {
        ChineseCalendar date = PlainDate.of(2017, 12, 22).transform(ChineseCalendar.axis());
        date.with(ChineseCalendar.SOLAR_TERM, null);
    }

    @Test
    public void withSolarTerm() {
        ChineseCalendar date = PlainDate.of(2017, 12, 22).transform(ChineseCalendar.axis());
        assertThat(
            date.with(ChineseCalendar.SOLAR_TERM, SolarTerm.MAJOR_11_DONGZHI_270),
            is(date));
        assertThat(
            date.minus(CalendarDays.of(100)).with(ChineseCalendar.SOLAR_TERM, SolarTerm.MAJOR_11_DONGZHI_270),
            is(date));
        assertThat( // new year
            date.with(ChineseCalendar.DAY_OF_YEAR, 1).with(ChineseCalendar.SOLAR_TERM, SolarTerm.MAJOR_11_DONGZHI_270),
            is(date));
        assertThat(
            date.with(ChineseCalendar.SOLAR_TERM, SolarTerm.MINOR_03_QINGMING_015),
            is(date.with(ChineseCalendar.MONTH_AS_ORDINAL, 3).with(ChineseCalendar.DAY_OF_MONTH, 8)));
    }

}