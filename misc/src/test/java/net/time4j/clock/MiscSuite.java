package net.time4j.clock;

import net.time4j.tz.other.WinZoneNameTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        ClockTest.class,
        WinZoneNameTest.class
    }
)
public class MiscSuite {

}