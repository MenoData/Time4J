package net.time4j.format;

import net.time4j.PatternType;
import net.time4j.PlainDate;

import java.text.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class DefaultValueTest {

    @Test
    public void missingYear() throws ParseException {
        ChronoFormatter<PlainDate> fmt =
            PlainDate.localFormatter("MM-dd", PatternType.CLDR)
                     .withDefault(PlainDate.YEAR, 2012);
        PlainDate date = fmt.parse("05-21");
        assertThat(date, is(PlainDate.of(2012, 5, 21)));
        System.out.println(date); // 2012-05-21
    }

    @Test
    public void missingMonth() throws ParseException {
        ChronoFormatter<PlainDate> fmt =
            PlainDate.localFormatter("yyyy-dd", PatternType.CLDR)
                     .withDefault(PlainDate.MONTH_AS_NUMBER, 11);
        PlainDate date = fmt.parse("2012-21");
        assertThat(date, is(PlainDate.of(2012, 11, 21)));
        System.out.println(date); // 2012-11-21
    }

}