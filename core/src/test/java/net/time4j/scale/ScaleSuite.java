package net.time4j.scale;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        LeapSecondTest.class,
        MomentArithmeticTest.class,
        MomentCreationTest.class,
        MomentPropertiesTest.class,
        NextLeapSecondTest.class,
        TimeScaleTest.class,
        ZonalMomentTest.class
    }
)
public class ScaleSuite {

}