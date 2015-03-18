package net.time4j.tz.olson;

import net.time4j.tz.javazi.JavaziTest;
import net.time4j.tz.model.ArrayTransitionModelTest;
import net.time4j.tz.model.CompositeTransitionModelTest;
import net.time4j.tz.model.CustomZoneTest;
import net.time4j.tz.model.DaylightSavingRuleTest;
import net.time4j.tz.model.RulesLikeBerlin1947Test;
import net.time4j.tz.model.RulesLikeDhaka2009Test;
import net.time4j.tz.model.RulesOfEuropeanUnionTest;
import net.time4j.tz.model.SerializationTest;
import net.time4j.tz.model.StartOfDayTest;
import net.time4j.tz.model.TransitionResolverTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        ArrayTransitionModelTest.class,
        CompositeTransitionModelTest.class,
        CustomZoneTest.class,
        DaylightSavingRuleTest.class,
        JavaziTest.class,
        PredefinedIDTest.class,
        RulesLikeBerlin1947Test.class,
        RulesLikeDhaka2009Test.class,
        RulesOfEuropeanUnionTest.class,
        SerializationTest.class,
        StartOfDayTest.class,
        TransitionResolverTest.class,
        ZoneNameParsingTest.class
    }
)
public class OlsonSuite {

}