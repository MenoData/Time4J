package net.time4j.scale;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        LeapSecondTest.class,
        TimeScaleTest.class
    }
)
public class ScaleSuite {

}