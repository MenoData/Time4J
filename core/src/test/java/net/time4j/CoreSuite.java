package net.time4j;

import net.time4j.scale.ScaleSuite;
import net.time4j.tz.ZoneSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        TestInitialization.class,
        ComponentElementTest.class,
        DateSuite.class,
        DurationSuite.class,
        OperatorSuite.class,
        MinMaxTest.class,
        ScaleSuite.class,
        SerializationTest.class,
        SystemClockTest.class,
        TemporalTypeTest.class,
        TimeLineTest.class,
        TimeSuite.class,
        ZoneSuite.class
    }
)
public class CoreSuite {

}
