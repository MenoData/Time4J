package net.time4j;

import net.time4j.format.FormatSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        DateSuite.class,
        DurationSuite.class,
        OperatorSuite.class,
        TimeSuite.class,
        FormatSuite.class
    }
)
public class AllSuite {

}