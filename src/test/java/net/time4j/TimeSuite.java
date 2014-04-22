package net.time4j;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        HourArithmeticTest.class,
        MeridiemValueTest.class,
        TimePropertiesTest.class,
        TimeArithmeticTest.class,
        TimeComparisonTest.class,
        TimeCreationTest.class,
        TimeElementTest.class,
        TimestampArithmeticTest.class,
        TimestampComparisonTest.class,
        TimestampCreationTest.class,
        TimestampPropertiesTest.class
    }
)
public class TimeSuite {

}