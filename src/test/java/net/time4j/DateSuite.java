package net.time4j;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        EpochDaysTest.class,
        GregorianTransformationTest.class,
        LeapYearOrdinalDateTest.class,
        LeapYearRangeArithmeticTest.class,
        LeapYearTest.class,
        MonthRangeArithmeticTest.class,
        RoundingTest.class,
        StdDayArithmeticTest.class,
        StdYearOrdinalDateTest.class,
        StdYearRangeArithmeticTest.class,
        YearMonthArithmeticTest.class
    }
)
public class DateSuite {

}