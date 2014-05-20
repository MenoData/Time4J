package net.time4j.format;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        AdjacentDigitParsingTest.class,
        CalendricalNamesTest.class,
        DefaultValueTest.class,
        DuplicateElementTest.class,
        FormatPatternTest.class,
        Iso8601FormatTest.class,
        MiscellaneousTest.class,
        ParsingTextOverflowTest.class,
        WhitespaceTest.class
    }
)
public class FormatSuite {

}