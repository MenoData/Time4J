package net.time4j;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        DateOperatorTest.class,
        NavigationOperatorTest.class,
        OrdinalWeekdayOperatorTest.class,
        RatioTest.class,
        RoundingTest.class,
        SetLenientTest.class,
        TimeOperatorTest.class,
        YOWOperatorTest.class
    }
)
public class OperatorSuite {

}