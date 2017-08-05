package net.time4j.calendar.frenchrev;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        DayOfDecadeTest.class,
        FormatTest.class,
        FrenchRepublicanCalendarTest.class,
        FrenchRepublicanElementTest.class,
        FrenchRepublicanEraTest.class,
        FrenchRepublicanMonthTest.class,
        FrenchRepublicanUnitTest.class,
        RommeTest.class,
        SansculottidesTest.class,
        SerializationTest.class
    }
)
public class FrenchRepublicanSuite {

}
