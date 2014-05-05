package net.time4j.tz;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        OffsetTest.class,
        PlatformTimezoneTest.class
    }
)
public class ZoneSuite {

}