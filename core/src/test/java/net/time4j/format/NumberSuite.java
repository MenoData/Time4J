package net.time4j.format;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        DecimalNumberSystemTest.class,
        EthiopicNumberTest.class,
        RomanNumberTestForModernUsage.class,
        RomanNumberTestForOtherFormats.class
    }
)
public class NumberSuite {

}