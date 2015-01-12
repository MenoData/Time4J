package net.time4j.tz.olson;

import net.time4j.tz.model.DaylightSavingRuleTest;
import net.time4j.tz.model.RulesOfEuropeanUnionTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        DaylightSavingRuleTest.class,
        PredefinedIDTest.class,
        RulesOfEuropeanUnionTest.class,
        ZoneNameParsingTest.class
    }
)
public class OlsonSuite {

}