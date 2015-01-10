package net.time4j;

import net.time4j.clock.MiscSuite;
import net.time4j.i18n.I18nSuite;
import net.time4j.range.RangeSuite;
import net.time4j.tz.olson.OlsonSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        CoreSuite.class, // must be on first line (contains test initialization)
        I18nSuite.class,
        MiscSuite.class,
        OlsonSuite.class,
        RangeSuite.class
    }
)
public class TotalSuite {

}