package net.time4j.format.expert;

import net.time4j.PlainDate;

import java.text.ParseException;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class DuplicateElementTest {

    @Test
    public void checkDuplicateElementSameContent() throws ParseException {
        ChronoFormatter<PlainDate> fmt =
            ChronoFormatter.setUp(PlainDate.class, Locale.US)
                .addFixedInteger(PlainDate.DAY_OF_MONTH, 2)
                .addFixedInteger(PlainDate.MONTH_AS_NUMBER, 2)
                .addFixedInteger(PlainDate.YEAR, 4)
                .addLiteral(" (")
                .addFixedInteger(PlainDate.DAY_OF_MONTH, 2)
                .addLiteral(')')
                .build();
        assertThat(fmt.parse("04102014 (04)"), is(PlainDate.of(2014, 10, 4)));
    }

    @Test(expected=ParseException.class)
    public void checkDuplicateElementAmbivalentContent() throws ParseException {
        ChronoFormatter<PlainDate> fmt =
            ChronoFormatter.setUp(PlainDate.class, Locale.US)
                .addFixedInteger(PlainDate.DAY_OF_MONTH, 2)
                .addFixedInteger(PlainDate.MONTH_AS_NUMBER, 2)
                .addFixedInteger(PlainDate.YEAR, 4)
                .addLiteral(" (")
                .addFixedInteger(PlainDate.DAY_OF_MONTH, 2)
                .addLiteral(')')
                .build();
        try {
            fmt.parse("04102014 (05)");
        } catch (ParseException pe) {
            assertThat(pe.getErrorOffset(), is(10));
            throw pe;
        }
    }

}