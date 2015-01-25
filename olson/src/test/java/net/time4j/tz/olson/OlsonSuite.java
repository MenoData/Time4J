package net.time4j.tz.olson;

import net.time4j.tz.model.ArrayTransitionModelTest;
import net.time4j.tz.model.CompositeTransitionModelTest;
import net.time4j.tz.model.DaylightSavingRuleTest;
import net.time4j.tz.model.RulesLikeBerlin1947Test;
import net.time4j.tz.model.RulesLikeDhaka2009Test;
import net.time4j.tz.model.RulesOfEuropeanUnionTest;
import net.time4j.tz.model.SerializationTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        ArrayTransitionModelTest.class,
        CompositeTransitionModelTest.class,
        DaylightSavingRuleTest.class,
        PredefinedIDTest.class,
        RulesLikeBerlin1947Test.class,
        RulesLikeDhaka2009Test.class,
        RulesOfEuropeanUnionTest.class,
        SerializationTest.class,
        ZoneNameParsingTest.class
    }
)
public class OlsonSuite {

}