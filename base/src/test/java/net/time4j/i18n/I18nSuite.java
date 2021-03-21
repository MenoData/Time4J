package net.time4j.i18n;

import net.time4j.format.expert.FormatSuite;
import net.time4j.history.HistorySuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        CalendricalNamesTest.class,
        CLDR24Test.class,
        DatePatternTest.class,
        DateTimePatternTest.class,
        FormatPatternTest.class,
        FormatSuite.class,
        HourCycleTest.class,
        HistorySuite.class,
        IsoSanityTest.class,
        NameDisplayTest.class,
        NumberSymbolTest.class,
        PluralRulesTest.class,
        PrettyTimeTest.class,
        RootLocaleTest.class,
        WeekdataTest.class
    }
)
public class I18nSuite {

}