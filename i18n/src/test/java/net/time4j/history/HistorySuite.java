package net.time4j.history;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        DayOfYearTest.class,
        EraFormatTest.class,
        EraNameTest.class,
        JulianTransformationTest.class,
        HistoryTest.class,
        NewYearTest.class,
        ScaligerTest.class,
        SerializationTest.class
    }
)
public class HistorySuite {

}