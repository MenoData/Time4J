package net.time4j.tz;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        DatelineBorderTest.class,
        OffsetTest.class,
        PlatformTimezoneTest.class,
        ProviderRegistrationTest.class,
        TZIDTest.class
    }
)
public class ZoneSuite {

}