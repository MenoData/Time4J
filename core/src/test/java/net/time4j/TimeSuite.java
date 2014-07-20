package net.time4j;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        DecimalElementTest.class,
        HourArithmeticTest.class,
        MeridiemValueTest.class,
        TimeArithmeticTest.class,
        TimeComparisonTest.class,
        TimeCreationTest.class,
        TimeElementTest.class,
        TimePropertiesTest.class,
        TimestampArithmeticTest.class,
        TimestampComparisonTest.class,
        TimestampCreationTest.class,
        TimestampPropertiesTest.class
    }
)
public class TimeSuite {

}