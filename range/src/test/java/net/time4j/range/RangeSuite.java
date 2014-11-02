package net.time4j.range;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        BasicDateRangeTest.class,
        BasicMomentRangeTest.class,
        BasicTimeRangeTest.class,
        BasicTimestampRangeTest.class,
        BoundaryTest.class,
        DateIntervalFormatTest.class,
        MomentIntervalFormatTest.class,
        RangeConversionTest.class,
        RangeDurationTest.class,
        TimestampIntervalFormatTest.class
    }
)
public class RangeSuite {

}