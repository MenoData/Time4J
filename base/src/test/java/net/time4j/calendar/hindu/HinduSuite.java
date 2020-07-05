package net.time4j.calendar.hindu;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        AstroHinduSolarCalendarTest.class,
        HinduElementTest.class,
        HinduEraTest.class,
        HinduFormatTest.class,
        HinduMiscellaneousTest.class,
        HinduVariantTest.class,
        ModernHinduSolarCalendarTest.class,
        ModernHinduLunarCalendarTest.class,
        OldHinduSolarCalendarTest.class,
        OldHinduLunarCalendarTest.class,
        SerializationTest.class
    }
)
public class HinduSuite {

}
