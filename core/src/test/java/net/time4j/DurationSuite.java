package net.time4j;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        ClockDurationTest.class,
        DurationArithmeticTest.class,
        DurationBasicsTest.class,
        DurationFormatterTest.class,
        DurationNormalizerTest.class,
        DurationUntilTest.class,
        MachineTimeTest.class
    }
)
public class DurationSuite {

}