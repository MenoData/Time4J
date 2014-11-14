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
        ComparatorTest.class,
        DateIntervalFormatTest.class,
        MomentIntervalFormatTest.class,
        RangeConversionTest.class,
        RangeDurationTest.class,
        RelationTest.class,
        TimestampIntervalFormatTest.class,
        WindowTest.class
    }
)
public class RangeSuite {

}