package net.time4j.calendar.astro;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        AstroTest.class,
        JulianDayTest.class,
        MoonTest.class,
        NoLeapsecondsTest.class,
        SerializationTest.class,
        SunSydneyTest.class,
        TwilightTest.class,
        ZodiacTest.class
    }
)
public class AstroSuite {

}
