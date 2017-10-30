package net.time4j.calendar;

import net.time4j.calendar.astro.AstroSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        AstroSuite.class,
        CalendarOverrideTest.class,
        ClockTest.class,
        CopticCalendarTest.class,
        CopticMiscellaneousTest.class,
        CopticOperatorTest.class,
        DiyanetDataTest.class,
        DiyanetRangeTest.class,
        EraNameTest.class,
        EthiopianCalendarTest.class,
        EthiopianMiscellaneousTest.class,
        EthiopianOperatorTest.class,
        EthiopianTimeTest.class,
        EvangelistTest.class,
        HebrewCalendarTest.class,
        HebrewMiscellaneousTest.class,
        HebrewMonthTest.class,
        HebrewOperatorTest.class,
        HijriAlgoTest.class,
        HijriMiscellaneousTest.class,
        HijriOperatorTest.class,
        HijriPatternTest.class,
        HijriUnitTest.class,
        HijriYearTest.class,
        HistoricCalendarTest.class,
        IndianCalendarTest.class,
        IndianMiscellaneousTest.class,
        IndianOperatorTest.class,
        JapaneseCalendarTest.class,
        JapaneseElementTest.class,
        JapaneseTransitionTest.class,
        JapaneseUnitTest.class,
        JulianCalendarTest.class,
        JulianMiscellaneousTest.class,
        MinguoCalendarTest.class,
        MinguoMiscellaneousTest.class,
        MonthNameTest.class,
        NengoTest.class,
        PersianCalendarTest.class,
        PersianMiscellaneousTest.class,
        PersianOperatorTest.class,
        RelatedGregorianYearTest.class,
        SerializationTest.class,
        StartOfDayTest.class,
        TabotTest.class,
        ThaiSolarCalendarTest.class,
        ThaiSolarMiscellaneousTest.class,
        ThaiSolarOperatorTest.class,
        UmalquraDataTest.class,
        WeekCalculationTest.class
    }
)
public class CalendarSuite {

}
