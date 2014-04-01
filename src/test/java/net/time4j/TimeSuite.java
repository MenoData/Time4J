package net.time4j;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        HourArithmeticTest.class,
        TimeArithmeticTest.class
    }
)
public class TimeSuite {

}