package net.time4j.format.expert;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        AdjacentDigitParsingTest.class,
        DayPeriodTest.class,
        DefaultValueTest.class,
        DuplicateElementTest.class,
        Iso8601FormatTest.class,
        MiscellaneousTest.class,
        MomentPatternTest.class,
        OffsetPatternTest.class,
        OrdinalTest.class,
        ParsingTextOverflowTest.class,
        WhitespaceTest.class
    }
)
public class FormatSuite {

}