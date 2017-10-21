package net.time4j.history;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        ComputusTest.class,
        DayOfYearTest.class,
        EraFormatTest.class,
        EraNameTest.class,
        JulianTransformationTest.class,
        HistoryTest.class,
        NewYearTest.class,
        RangeTest.class,
        ScaligerTest.class,
        SerializationTest.class,
        VariantTest.class,
        YearDefinitionTest.class
    }
)
public class HistorySuite {

}