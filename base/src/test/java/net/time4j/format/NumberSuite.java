package net.time4j.format;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        DecimalNumberTest.class,
        EthiopicNumberTest.class,
        JapaneseNumberTest.class,
        KoreanNumberTest1.class,
        KoreanNumberTest2.class,
        MandarinNumberTest.class,
        RomanNumberTestForModernUsage.class,
        RomanNumberTestForOtherFormats.class
    }
)
public class NumberSuite {

}