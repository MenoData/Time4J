package net.time4j;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        EpochDaysTest.class,
        GregorianTransformationTest.class,
        LeapYearTest.class,
        RoundingTest.class
    }
)
public class DateSuite {

}