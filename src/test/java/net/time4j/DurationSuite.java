package net.time4j;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        DurationBasicsTest.class,
        DurationNormalizerTest.class
    }
)
public class DurationSuite {

}