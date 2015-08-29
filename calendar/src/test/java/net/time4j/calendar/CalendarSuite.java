package net.time4j.calendar;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        ClockTest.class,
        EraNameTest.class,
        HijriAlgoTest.class,
        HijriOperatorTest.class,
        HijriPatternTest.class,
        MonthNameTest.class,
        UmalquraDataTest.class
    }
)
public class CalendarSuite {

}
