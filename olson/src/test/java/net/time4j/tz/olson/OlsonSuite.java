package net.time4j.tz.olson;

import net.time4j.tz.threeten.JdkZoneProviderTest;
import net.time4j.tz.model.ArrayTransitionModelTest;
import net.time4j.tz.model.CompositeTransitionModelTest;
import net.time4j.tz.model.CustomZoneTest;
import net.time4j.tz.model.DaylightSavingRuleTest;
import net.time4j.tz.model.EireZoneTest;
import net.time4j.tz.model.RulesLikeBerlin1947Test;
import net.time4j.tz.model.RulesLikeDhaka2009Test;
import net.time4j.tz.model.RulesOfEuropeanUnionTest;
import net.time4j.tz.model.SerializationTest;
import net.time4j.tz.model.StartOfDayTest;
import net.time4j.tz.model.TransitionResolverTest;
import net.time4j.tz.spi.CountryToZonesTest;

import net.time4j.tz.threeten.NegativeDayOfMonthPatternTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        ArrayTransitionModelTest.class,
        CompositeTransitionModelTest.class,
        CountryToZonesTest.class,
        CustomZoneTest.class,
        DaylightSavingRuleTest.class,
        EireZoneTest.class,
        JdkZoneProviderTest.class,
        LocalizedGMTOffsetTest.class,
        NegativeDayOfMonthPatternTest.class,
        PredefinedIDTest.class,
        RulesLikeBerlin1947Test.class,
        RulesLikeDhaka2009Test.class,
        RulesOfEuropeanUnionTest.class,
        SamoaTest.class,
        SerializationTest.class,
        StartOfDayTest.class,
        TransitionResolverTest.class,
        ZoneConversionTest.class,
        ZoneNameParsingTest.class,
        ZoneNameResourceTest.class
    }
)
public class OlsonSuite {

}