package net.time4j.calendar.hindu;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        AstroHinduLunarCalendarTest.class,
        AstroHinduSolarCalendarTest.class,
        HinduElementTest.class,
        HinduEraTest.class,
        HinduFormatTest.class,
        HinduMiscellaneousTest.class,
        HinduVariantTest.class,
        ModernHinduLunarCalendarTest.class,
        ModernHinduSolarCalendarTest.class,
        OldHinduLunarCalendarTest.class,
        OldHinduSolarCalendarTest.class,
        SerializationTest.class
    }
)
public class HinduSuite {

}
