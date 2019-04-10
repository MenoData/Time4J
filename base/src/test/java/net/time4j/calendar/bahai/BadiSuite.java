package net.time4j.calendar.bahai;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        BadiDivisionTest.class,
        BadiElementTest.class,
        BadiEraTest.class,
        BadiMonthTest.class,
        FormatTest.class,
        SerializationTest.class
    }
)
public class BadiSuite {

}
