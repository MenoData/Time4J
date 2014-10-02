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
        RangeConversionTest.class
    }
)
public class RangeSuite {

}