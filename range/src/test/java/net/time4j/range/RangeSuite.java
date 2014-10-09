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
        IntervalFormatTest.class,
        RangeConversionTest.class,
        RangeDurationTest.class
    }
)
public class RangeSuite {

}