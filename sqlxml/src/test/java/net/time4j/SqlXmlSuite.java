package net.time4j;

import net.time4j.sql.JDBCAdapterTest;
import net.time4j.xml.AnnualDateTest;
import net.time4j.xml.XMLAdapterTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
    {
        AnnualDateTest.class,
        JDBCAdapterTest.class,
        XMLAdapterTest.class
    }
)
public class SqlXmlSuite {

}