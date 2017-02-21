package net.time4j.format.expert;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        AdjacentDigitParsingTest.class,
        CLDRSanityTest.class,
        ChronoHierarchyTest.class,
        DayPeriodTest.class,
        DecimalFormatTest.class,
        DefaultValueTest.class,
        DozenalNumberTest.class,
        DuplicateElementTest.class,
        FractionTest.class,
        Iso8601FormatTest.class,
        LiteralWithBidisTest.class,
        LiteralWithDigitsTest.class,
        MiscellaneousTest.class,
        MomentPatternTest.class,
        MomentScaleTest.class,
        MultiFormatTest.class,
        OffsetPatternTest.class,
        OrFormatTest.class,
        OrdinalTest.class,
        ParsingTextOverflowTest.class,
        SkipUnknownTest.class,
        StyleProcessorTest.class,
        ThreetenFormatTest.class,
        WhitespaceTest.class
    }
)
public class FormatSuite {

}