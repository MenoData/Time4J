package net.time4j.calendar;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        ClockTest.class,
        CopticCalendarTest.class,
        CopticOperatorTest.class,
        DiyanetDataTest.class,
        DiyanetRangeTest.class,
        EraNameTest.class,
        EthiopianCalendarTest.class,
        EthiopianOperatorTest.class,
        HijriAlgoTest.class,
        HijriOperatorTest.class,
        HijriPatternTest.class,
        MiscellaneousTest.class,
        MonthNameTest.class,
        PersianCalendarTest.class,
        PersianOperatorTest.class,
        StartOfDayTest.class,
        UmalquraDataTest.class
    }
)
public class CalendarSuite {

}
