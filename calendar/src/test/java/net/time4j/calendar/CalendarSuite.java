package net.time4j.calendar;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        ClockTest.class,
        DiyanetDataTest.class,
        DiyanetRangeTest.class,
        EraNameTest.class,
        HijriAlgoTest.class,
        HijriOperatorTest.class,
        HijriPatternTest.class,
        MiscellaneousTest.class,
        MonthNameTest.class,
        PersianCalendarTest.class,
        StartOfDayTest.class,
        UmalquraDataTest.class
    }
)
public class CalendarSuite {

}
