package net.time4j.calendar;

import net.time4j.engine.CalendarSystem;
import net.time4j.history.ChronoHistory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class OutOfRangeTest {

    @Test(expected=IllegalArgumentException.class)
    public void chineseMin() {
        CalendarSystem<ChineseCalendar> calsys = ChineseCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MIN_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void chineseMax() {
        CalendarSystem<ChineseCalendar> calsys = ChineseCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MAX_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void copticMin() {
        CalendarSystem<CopticCalendar> calsys = CopticCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MIN_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void copticMax() {
        CalendarSystem<CopticCalendar> calsys = CopticCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MAX_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void ethiopianMin() {
        CalendarSystem<EthiopianCalendar> calsys = EthiopianCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MIN_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void ethiopianMax() {
        CalendarSystem<EthiopianCalendar> calsys = EthiopianCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MAX_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void persianMin() {
        CalendarSystem<PersianCalendar> calsys = PersianCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MIN_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void persianMax() {
        CalendarSystem<PersianCalendar> calsys = PersianCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MAX_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void thaiSolarMin() {
        CalendarSystem<ThaiSolarCalendar> calsys = ThaiSolarCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MIN_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void thaiSolarMax() {
        CalendarSystem<ThaiSolarCalendar> calsys = ThaiSolarCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MAX_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void hijriMin() {
        CalendarSystem<HijriCalendar> calsys =
            HijriCalendar.family().getCalendarSystem(HijriAlgorithm.WEST_ISLAMIC_CIVIL);
        calsys.transform(Long.MIN_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void hijriMax() {
        CalendarSystem<HijriCalendar> calsys =
            HijriCalendar.family().getCalendarSystem(HijriAlgorithm.WEST_ISLAMIC_CIVIL);
        calsys.transform(Long.MAX_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void umalquraMin() {
        CalendarSystem<HijriCalendar> calsys = HijriCalendar.family().getCalendarSystem(HijriCalendar.VARIANT_UMALQURA);
        calsys.transform(Long.MIN_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void umalquraMax() {
        CalendarSystem<HijriCalendar> calsys = HijriCalendar.family().getCalendarSystem(HijriCalendar.VARIANT_UMALQURA);
        calsys.transform(Long.MAX_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void koreanMin() {
        CalendarSystem<KoreanCalendar> calsys = KoreanCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MIN_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void koreanMax() {
        CalendarSystem<KoreanCalendar> calsys = KoreanCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MAX_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void vietnameseMin() {
        CalendarSystem<VietnameseCalendar> calsys = VietnameseCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MIN_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void vietnameseMax() {
        CalendarSystem<VietnameseCalendar> calsys = VietnameseCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MAX_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void minguoMin() {
        CalendarSystem<MinguoCalendar> calsys = MinguoCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MIN_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void minguoMax() {
        CalendarSystem<MinguoCalendar> calsys = MinguoCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MAX_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void japaneseMin() {
        CalendarSystem<JapaneseCalendar> calsys = JapaneseCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MIN_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void japaneseMax() {
        CalendarSystem<JapaneseCalendar> calsys = JapaneseCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MAX_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void hebrewMin() {
        CalendarSystem<HebrewCalendar> calsys = HebrewCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MIN_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void hebrewMax() {
        CalendarSystem<HebrewCalendar> calsys = HebrewCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MAX_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void indianMin() {
        CalendarSystem<IndianCalendar> calsys = IndianCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MIN_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void indianMax() {
        CalendarSystem<IndianCalendar> calsys = IndianCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MAX_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void julianMin() {
        CalendarSystem<JulianCalendar> calsys = JulianCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MIN_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void julianMax() {
        CalendarSystem<JulianCalendar> calsys = JulianCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MAX_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void jucheMin() {
        CalendarSystem<JucheCalendar> calsys = JucheCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MIN_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void jucheMax() {
        CalendarSystem<JucheCalendar> calsys = JucheCalendar.axis().getCalendarSystem();
        calsys.transform(Long.MAX_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void swedenMin() {
        CalendarSystem<HistoricCalendar> calsys =
            HistoricCalendar.family().getCalendarSystem(ChronoHistory.ofSweden());
        calsys.transform(Long.MIN_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void swedenMax() {
        CalendarSystem<HistoricCalendar> calsys =
            HistoricCalendar.family().getCalendarSystem(ChronoHistory.ofSweden());
        calsys.transform(Long.MAX_VALUE);
    }

}