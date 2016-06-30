package net.time4j;

import net.time4j.clock.ClockTest;
import net.time4j.tz.other.MilitaryZoneTest;
import net.time4j.tz.other.WindowsZoneTest;
import net.time4j.xml.AnnualDateTest;
import net.time4j.xml.XMLAdapterTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        AnnualDateTest.class,
        ClockTest.class,
        MilitaryZoneTest.class,
        WindowsZoneTest.class,
        XMLAdapterTest.class
    }
)
public class MiscSuite {

}