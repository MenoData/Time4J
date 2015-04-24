package net.time4j.history;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        JulianTransformationTest.class,
        HistoryTest.class
    }
)
public class HistorySuite {

}