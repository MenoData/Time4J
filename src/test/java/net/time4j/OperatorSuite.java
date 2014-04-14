package net.time4j;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        OrdinalWeekdayTest.class,
        RoundingTest.class,
        YOWOperatorTest.class
    }
)
public class OperatorSuite {

}