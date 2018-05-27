package net.time4j;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        DateComparisonTest.class,
        DateCreationTest.class,
        DateElementTest.class,
        DatePropertiesTest.class,
        DayArithmeticTest.class,
        EpochDaysTest.class,
        GregorianTransformationTest.class,
        LeapYearOrdinalDateTest.class,
        LeapYearRangeArithmeticTest.class,
        LeapYearTest.class,
        LengthOfMonthTest.class,
        MonthRangeArithmeticTest.class,
        MonthValueTest.class,
        PlatformFormatTest.class,
        QuarterValueTest.class,
        SpecialUnitTest.class,
        StdYearOrdinalDateTest.class,
        StdYearRangeArithmeticTest.class,
        WeekdayValueTest.class,
        WeekmodelTest.class,
        YearMonthArithmeticTest.class
    }
)
public class DateSuite {

}