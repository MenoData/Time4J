package net.time4j.history;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        EraFormatTest.class,
        EraNameTest.class,
        JulianTransformationTest.class,
        HistoryTest.class,
        SerializationTest.class
    }
)
public class HistorySuite {

}