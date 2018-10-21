package net.time4j.range;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        AlgebraTest.class,
        BasicDateRangeTest.class,
        BasicMomentRangeTest.class,
        BasicClockRangeTest.class,
        BasicTimestampRangeTest.class,
        BoundaryTest.class,
        CalendarMonthTest.class,
        CalendarQuarterTest.class,
        CalendarWeekTest.class,
        CalendarYearTest.class,
        ClockIntervalFormatTest.class,
        ComparatorTest.class,
        DateIntervalFormatTest.class,
        IntervalCollectionTest.class,
        IntervalTreeTest.class,
        IsoRecurrenceTest.class,
        MomentIntervalFormatTest.class,
        RangeConversionTest.class,
        RangeDurationTest.class,
        RelationTest.class,
        SerializationTest.class,
        SimpleIntervalTest.class,
        SingleUnitTest.class,
        TimestampIntervalFormatTest.class,
        ValueIntervalTest.class,
        YearsTest.class
    }
)
public class RangeSuite {

}