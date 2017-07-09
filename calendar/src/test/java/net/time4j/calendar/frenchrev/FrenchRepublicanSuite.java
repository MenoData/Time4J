package net.time4j.calendar.frenchrev;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        DayOfDecadeTest.class,
        FrenchRepublicanCalendarTest.class,
        FrenchRepublicanElementTest.class,
        FrenchRepublicanEraTest.class,
        FrenchRepublicanMonthTest.class,
        FrenchRepublicanUnitTest.class,
        SansculottidesTest.class,
    }
)
public class FrenchRepublicanSuite {

}
