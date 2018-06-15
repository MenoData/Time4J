package net.time4j;

import net.time4j.format.NumberSuite;
import net.time4j.scale.ScaleSuite;
import net.time4j.tz.ZoneSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        TestInitialization.class,
        AnnualDateTest.class,
        AxisElementTest.class,
        CompareZonalDateTimeTest.class,
        ComponentElementTest.class,
        DateSuite.class,
        DurationSuite.class,
        MachineTimeTest.class,
        MinMaxTest.class,
        NumberSuite.class,
        OperatorSuite.class,
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
