package net.time4j.tz.olson;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        DaylightSavingRuleTest.class,
        PredefinedIDTest.class,
        ZoneNameParsingTest.class
    }
)
public class OlsonSuite {

}