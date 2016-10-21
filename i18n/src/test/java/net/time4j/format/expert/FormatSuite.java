package net.time4j.format.expert;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        AdjacentDigitParsingTest.class,
        CLDRSanityTest.class,
        DayPeriodTest.class,
        DefaultValueTest.class,
        DuplicateElementTest.class,
        FractionTest.class,
        Iso8601FormatTest.class,
        LiteralWithDigitsTest.class,
        MiscellaneousTest.class,
        MomentPatternTest.class,
        MultiFormatTest.class,
        OffsetPatternTest.class,
        OrFormatTest.class,
        OrdinalTest.class,
        ParsingTextOverflowTest.class,
        SkipUnknownTest.class,
        WhitespaceTest.class
    }
)
public class FormatSuite {

}