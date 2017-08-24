package net.time4j.calendar.astro;

import net.time4j.calendar.frenchrev.DayOfDecadeTest;
import net.time4j.calendar.frenchrev.FormatTest;
import net.time4j.calendar.frenchrev.FrenchRepublicanCalendarTest;
import net.time4j.calendar.frenchrev.FrenchRepublicanElementTest;
import net.time4j.calendar.frenchrev.FrenchRepublicanEraTest;
import net.time4j.calendar.frenchrev.FrenchRepublicanMonthTest;
import net.time4j.calendar.frenchrev.FrenchRepublicanUnitTest;
import net.time4j.calendar.frenchrev.RommeTest;
import net.time4j.calendar.frenchrev.SansculottidesTest;
import net.time4j.calendar.frenchrev.SerializationTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        AstroTest.class,
        JulianDayTest.class,
        SunSydneyTest.class
    }
)
public class AstroSuite {

}
