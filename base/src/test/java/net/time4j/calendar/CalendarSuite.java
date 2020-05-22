package net.time4j.calendar;

import net.time4j.calendar.astro.AstroSuite;
import net.time4j.calendar.frenchrev.FrenchRepublicanSuite;
import net.time4j.calendar.hindu.HinduSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        AstroSuite.class,
        CalendarOverrideTest.class,
        ChineseMiscellaneousTest.class,
        ChineseOperatorTest.class,
        ChineseYearTest.class,
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
        FrenchRepublicanSuite.class,
        HebrewAnniversaryTest.class,
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
        HinduSuite.class,
        HistoricCalendarTest.class,
        HongkongObservatory1Test.class,
        HongkongObservatory2Test.class,
        IndianCalendarTest.class,
        IndianMiscellaneousTest.class,
        IndianOperatorTest.class,
        IntRangeFormattabilityTest.class,
        JapaneseCalendarTest.class,
        JapaneseElementTest.class,
        JapaneseTransitionTest.class,
        JapaneseUnitTest.class,
        JucheMiscellaneousTest.class,
        JulianCalendarTest.class,
        JulianRangeTest.class,
        JulianMiscellaneousTest.class,
        KoreanMiscellaneousTest.class,
        MinguoCalendarTest.class,
        MinguoMiscellaneousTest.class,
        MonthNameTest.class,
        NengoTest.class,
        OutOfRangeTest.class,
        PersianCalendarTest.class,
        PersianMiscellaneousTest.class,
        PersianOperatorTest.class,
        RelatedGregorianYearTest.class,
        SerializationTest.class,
        SolarTermTest.class,
        StartOfDayTest.class,
        TabotTest.class,
        ThaiSolarCalendarTest.class,
        ThaiSolarMiscellaneousTest.class,
        ThaiSolarOperatorTest.class,
        UmalquraDataTest.class,
        VietnameseMiscellaneousTest.class,
        WeekCalculationTest.class
    }
)
public class CalendarSuite {

}
