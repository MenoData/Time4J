package net.time4j.calendar.bahai;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        BadiCalendarTest.class,
        BadiDivisionTest.class,
        BadiElementTest.class,
        BadiEraTest.class,
        BadiMiscTest.class,
        BadiMonthTest.class,
        BadiUnitTest.class,
        FormatTest.class,
        SerializationTest.class
    }
)
public class BadiSuite {

}
